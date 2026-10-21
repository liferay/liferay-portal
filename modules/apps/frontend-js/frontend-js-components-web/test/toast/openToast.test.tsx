/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import '@testing-library/jest-dom';

jest.mock('frontend-js-web', () => ({
	uuidv4: (jest.requireActual('frontend-js-web') as any).uuidv4,
}));

interface LiferayMock {
	SPA?: {app: object | null};
	destroyComponent: (id: string) => void;
	once?: (name: string, callback: () => void) => void;
	[key: string]: unknown;
}

const getAlerts = () =>
	Array.from(
		document.querySelectorAll(
			'#ToastAlertContainer .alert-notifications-fixed > .alert'
		)
	);

describe('openToast', () => {
	const baseLiferay = (globalThis as any).Liferay;

	let components: Map<string, {destroy: () => void}>;
	let liferay: LiferayMock;
	let openToast: typeof import('../../src/main/resources/META-INF/resources/toast/openToast').default;

	beforeEach(() => {
		document.body.innerHTML = '';

		components = new Map();

		jest.isolateModules(() => {
			openToast =
				require('../../src/main/resources/META-INF/resources/toast/openToast').default;
		});

		liferay = {
			...baseLiferay,
			Icons: {spritemap: '/spritemap.svg'},
			component: (id: string, component: {destroy: () => void}) => {
				components.set(id, component);
			},
			destroyComponent: (id: string) => {
				components.get(id)?.destroy();
				components.delete(id);
			},
		};

		(globalThis as any).Liferay = liferay;
	});

	it('does not move focus unless asked to', () => {
		openToast({message: 'saved', type: 'success'});

		expect(document.activeElement).not.toBe(
			document.querySelector('#ToastAlertContainer [role="alert"]')
		);
	});

	it('focuses the alert when autoFocus is set', () => {
		openToast({autoFocus: true, message: 'saved', type: 'success'});

		expect(document.activeElement).toBe(
			document.querySelector('#ToastAlertContainer [role="alert"]')
		);
	});

	it('renders the alert before returning', () => {
		openToast({message: 'saved', type: 'success'});

		expect(
			document.querySelector('#ToastAlertContainer .alert-success')
		).not.toBeNull();
	});

	it('shows many toasts in one pile', () => {
		openToast({message: 'first', type: 'success'});
		openToast({message: 'second', type: 'info'});
		openToast({message: 'third', type: 'danger'});

		expect(document.querySelectorAll('#ToastAlertContainer')).toHaveLength(
			1
		);
		expect(
			document.querySelectorAll(
				'#ToastAlertContainer .alert-notifications-fixed'
			)
		).toHaveLength(1);

		const alerts = getAlerts();

		expect(alerts).toHaveLength(3);
		expect(alerts[0]).toHaveTextContent('first');
		expect(alerts[1]).toHaveTextContent('second');
		expect(alerts[2]).toHaveTextContent('third');
	});

	it('shows the toasts opened before the pile mounts in one pile', () => {
		const readyCallbacks: Array<() => void> = [];

		liferay.SPA = {app: null};
		liferay.once = (name: string, callback: () => void) => {
			if (name === 'SPAReady') {
				readyCallbacks.push(callback);
			}
		};

		openToast({message: 'first', type: 'success'});
		openToast({message: 'second', type: 'info'});

		expect(document.querySelector('#ToastAlertContainer')).toBeNull();

		liferay.SPA = {app: {}};

		act(() => {
			readyCallbacks.forEach((callback) => callback());
		});

		expect(document.querySelectorAll('#ToastAlertContainer')).toHaveLength(
			1
		);

		const alerts = getAlerts();

		expect(alerts).toHaveLength(2);
		expect(alerts[0]).toHaveTextContent('first');
		expect(alerts[1]).toHaveTextContent('second');
	});

	it('removes only the toast of a destroyed component', async () => {
		openToast({
			message: 'first',
			renderData: {componentId: 'firstToast', portletId: 'portlet'},
		});
		openToast({
			message: 'second',
			renderData: {componentId: 'secondToast', portletId: 'portlet'},
		});

		act(() => {
			liferay.destroyComponent('firstToast');
		});

		await waitFor(() => expect(getAlerts()).toHaveLength(1));

		expect(getAlerts()[0]).toHaveTextContent('second');
	});

	it('mounts a new pile with the toasts when the body is emptied', () => {
		openToast({message: 'first'});

		const firstRoot = document.querySelector(
			'#ToastAlertContainer'
		)!.parentElement!;

		document.body.innerHTML = '';

		openToast({message: 'second'});

		expect(firstRoot.querySelector('.alert')).toBeNull();
		expect(document.querySelectorAll('#ToastAlertContainer')).toHaveLength(
			1
		);

		const alerts = getAlerts();

		expect(alerts).toHaveLength(2);
		expect(alerts[0]).toHaveTextContent('first');
		expect(alerts[1]).toHaveTextContent('second');
	});

	it('renders into the given container instead of the pile', () => {
		const container = document.createElement('div');

		document.body.appendChild(container);

		openToast({container, message: 'in container'});
		openToast({message: 'in pile'});

		expect(container.closest('#ToastAlertContainer')).toBeNull();
		expect(container.querySelector('.alert')).toHaveClass('mb-3');
		expect(container.querySelector('.alert')).toHaveTextContent(
			'in container'
		);

		const alerts = getAlerts();

		expect(alerts).toHaveLength(1);
		expect(alerts[0]).toHaveTextContent('in pile');
	});

	it('empties the given container when the toast closes', async () => {
		const container = document.createElement('div');

		container.id = 'toastContainer';

		document.body.appendChild(container);

		openToast({
			container,
			containerId: 'toastContainer',
			message: 'in container',
		});

		await userEvent.click(
			within(container).getByRole('button', {name: 'close'})
		);

		await waitFor(() => expect(container).toBeEmptyDOMElement());
	});
});
