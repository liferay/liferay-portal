/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayToaster, createToastStore} from '..';
import {act, cleanup, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useEffect} from 'react';

import type {ToastStore} from '..';

const getAlerts = (container: HTMLElement) =>
	Array.from(
		container.querySelectorAll('.alert-notifications-fixed > .alert')
	);

describe('createToastStore', () => {
	it('adds toasts with unique keys in order', () => {
		const store = createToastStore();

		const first = store.add({});
		const second = store.add({});

		expect(first).not.toBe(second);
		expect(store.getToasts().map(({key}) => key)).toEqual([first, second]);
	});

	it('changes the snapshot identity on every change', () => {
		const store = createToastStore();

		const empty = store.getToasts();

		const key = store.add({});

		const withToast = store.getToasts();

		store.remove(key);

		expect(store.getToasts()).not.toBe(withToast);
		expect(withToast).not.toBe(empty);
	});

	it('closes a toast calling its onClose with the event', () => {
		const store = createToastStore();
		const onClose = jest.fn();
		const event = {
			defaultPrevented: false,
		} as React.MouseEvent<HTMLButtonElement>;

		const key = store.add({onClose});

		store.close(key, event);

		expect(onClose).toHaveBeenCalledWith(event);
		expect(store.getToasts()).toHaveLength(0);
	});

	it('keeps a toast when the default of the close event is prevented', () => {
		const store = createToastStore();
		const event = {
			defaultPrevented: true,
		} as React.MouseEvent<HTMLButtonElement>;

		const key = store.add({onClose: jest.fn()});

		store.close(key, event);

		expect(store.getToasts()).toHaveLength(1);
	});

	it('removes a toast silently', () => {
		const store = createToastStore();
		const onClose = jest.fn();

		const key = store.add({onClose});

		store.remove(key);

		expect(onClose).not.toHaveBeenCalled();
		expect(store.getToasts()).toHaveLength(0);
	});

	it('removes a toast when its onClose runs without a Toaster', () => {
		const store = createToastStore();
		const onClose = jest.fn();

		store.add({onClose});

		store.getToasts()[0]?.props.onClose?.();

		expect(onClose).toHaveBeenCalledTimes(1);
		expect(store.getToasts()).toHaveLength(0);
	});

	it('does not notify when removing or closing an unknown toast', () => {
		const store = createToastStore();
		const listener = jest.fn();

		store.subscribe(listener);

		store.remove('unknown');
		store.close('unknown');

		expect(listener).not.toHaveBeenCalled();
	});

	it('notifies subscribers until they unsubscribe', () => {
		const store = createToastStore();
		const listener = jest.fn();

		const unsubscribe = store.subscribe(listener);

		const key = store.add({});

		expect(listener).toHaveBeenCalledTimes(1);

		store.remove(key);

		expect(listener).toHaveBeenCalledTimes(2);

		unsubscribe();

		store.add({});

		expect(listener).toHaveBeenCalledTimes(2);
	});
});

describe('BasicRendering', () => {
	afterEach(cleanup);

	it('renders the toasts as direct children of the fixed notifications, newest last', () => {
		const store = createToastStore();

		store.add({title: 'First'});
		store.add({title: 'Second'});
		store.add({title: 'Third'});

		const {container} = render(<ClayToaster store={store} />);

		const alerts = getAlerts(container);

		expect(alerts).toHaveLength(3);
		expect(alerts.map((alert) => alert.textContent)).toEqual([
			expect.stringContaining('First'),
			expect.stringContaining('Second'),
			expect.stringContaining('Third'),
		]);
	});

	it('renders the container props on the outer container', () => {
		const {container} = render(
			<ClayToaster
				className="cadmin"
				id="ToastAlertContainer"
				store={createToastStore()}
			/>
		);

		expect(container.querySelector('#ToastAlertContainer')).toHaveClass(
			'alert-container',
			'cadmin'
		);
	});
});

describe('IncrementalInteractions', () => {
	afterEach(() => {
		cleanup();
		jest.useRealTimers();
	});

	it('renders toasts added after the first render', () => {
		const store = createToastStore();

		const {container} = render(<ClayToaster store={store} />);

		expect(getAlerts(container)).toHaveLength(0);

		act(() => {
			store.add({});
		});

		expect(getAlerts(container)).toHaveLength(1);
	});

	it('renders a toast added between the first render and the subscription', () => {
		const store = createToastStore();

		function AddOnMount({store}: {store: ToastStore}) {
			useEffect(() => {
				store.add({title: 'Early'});
			}, [store]);

			return null;
		}

		const {container} = render(
			<>
				<AddOnMount store={store} />

				<ClayToaster store={store} />
			</>
		);

		expect(container).toHaveTextContent('Early');
		expect(getAlerts(container)).toHaveLength(1);
	});

	it('removes a toast with its close button and calls onClose', async () => {
		const store = createToastStore();
		const onClose = jest.fn();

		store.add({
			closeButtonAriaLabel: 'Close',
			onClose,
			title: 'First',
		});
		store.add({title: 'Second'});

		const {container} = render(<ClayToaster store={store} />);

		await userEvent.click(
			screen.getAllByLabelText('Close')[0] as HTMLElement
		);

		expect(container).not.toHaveTextContent('First');
		expect(container).toHaveTextContent('Second');
		expect(getAlerts(container)).toHaveLength(1);
		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('removes a toast when it auto closes', () => {
		jest.useFakeTimers();

		const store = createToastStore();
		const onClose = jest.fn();

		store.add({autoClose: 5000, onClose});
		store.add({title: 'Sticky'});

		const {container} = render(<ClayToaster store={store} />);

		expect(getAlerts(container)).toHaveLength(2);
		expect(store.getToasts()).toHaveLength(2);

		act(() => {
			jest.advanceTimersByTime(5000);
		});

		expect(container).toHaveTextContent('Sticky');
		expect(getAlerts(container)).toHaveLength(1);
		expect(onClose).toHaveBeenCalledTimes(1);
		expect(store.getToasts()).toHaveLength(1);
	});

	it('keeps the toast when onClose prevents the default of the event', async () => {
		const store = createToastStore();
		const onClose = jest.fn((event?: React.MouseEvent<HTMLButtonElement>) =>
			event?.preventDefault()
		);

		store.add({closeButtonAriaLabel: 'Close', onClose});

		const {container} = render(<ClayToaster store={store} />);

		await userEvent.click(screen.getByLabelText('Close'));

		expect(getAlerts(container)).toHaveLength(1);
		expect(onClose).toHaveBeenCalledTimes(1);
		expect(store.getToasts()).toHaveLength(1);
	});

	it('keeps the content and the focus of the shown toasts when adding another', () => {
		const store = createToastStore();

		store.add({children: <button>Undo</button>});

		const {container} = render(<ClayToaster store={store} />);

		const button = screen.getByText('Undo');

		button.focus();

		expect(document.activeElement).toBe(button);

		act(() => {
			store.add({});
		});

		expect(document.activeElement).toBe(button);
		expect(getAlerts(container)).toHaveLength(2);
		expect(screen.getByText('Undo')).toBe(button);
	});

	it('focuses a toast added with autoFocus from an effect, not the older one', () => {
		const store = createToastStore();

		store.add({children: <button>A</button>});

		function AddFocused({store}: {store: ToastStore}) {
			useEffect(() => {
				store.add({autoFocus: true, children: <button>B</button>});
			}, [store]);

			return null;
		}

		const {container} = render(
			<>
				<ClayToaster store={store} />

				<AddFocused store={store} />
			</>
		);

		const [older, newer] = getAlerts(container);

		expect(newer?.contains(document.activeElement)).toBe(true);
		expect(older?.contains(document.activeElement)).toBe(false);
	});
});
