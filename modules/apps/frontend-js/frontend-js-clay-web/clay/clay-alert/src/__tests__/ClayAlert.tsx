/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '..';
import ClayButton from '@clayui/button';
import {Provider} from '@clayui/provider';
import {act, cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

describe('ClayAlert', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(
			<ClayAlert spritemap="/foo/bar" title="Hello!" />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as a different type', () => {
		const {container} = render(
			<ClayAlert
				displayType="danger"
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as `stripe` variant', () => {
		const {container} = render(
			<ClayAlert spritemap="/foo/bar" title="Hello!" variant="stripe" />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as `feedback` variant', () => {
		const {container} = render(
			<ClayAlert spritemap="/foo/bar" title="Hello!" variant="feedback" />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with an icon for closing', () => {
		const {container} = render(
			<ClayAlert onClose={() => {}} spritemap="/foo/bar" title="Hello!" />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with a title and a message with markup', () => {
		const {container} = render(
			<ClayAlert spritemap="/foo/bar" title="Hello!">
				<span>test</span>
			</ClayAlert>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with a footer and button', () => {
		const {container} = render(
			<ClayAlert spritemap="/foo/bar" title="Hello!">
				<span>test</span>

				<ClayAlert.Footer>
					<ClayButton.Group>
						<ClayButton alert>View</ClayButton>
					</ClayButton.Group>
				</ClayAlert.Footer>
			</ClayAlert>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with ToastContainer as a wrapper ', () => {
		const {container} = render(
			<ClayAlert.ToastContainer>
				<ClayAlert spritemap="/foo/bar" title="One!" />

				<ClayAlert spritemap="/foo/bar" title="Two!" />

				<ClayAlert spritemap="/foo/bar" title="Three!" />
			</ClayAlert.ToastContainer>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with an icon for closing with autoClose', () => {
		const {container} = render(
			<ClayAlert
				autoClose
				onClose={() => {}}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with autoClose and without icon', () => {
		const {container} = render(
			<ClayAlert
				autoClose
				hideCloseIcon
				onClose={() => {}}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders alert inline with action', () => {
		const {container} = render(
			<ClayAlert
				actions={<ClayButton small>Baz</ClayButton>}
				spritemap="/foo/bar"
				title="Foo:"
				variant="inline"
			>
				Bar!
			</ClayAlert>
		);

		expect(container).toMatchSnapshot();
	});

	it('render toast alert with stacked action', () => {
		const {container} = render(
			<ClayAlert
				actions={
					<ClayButton alert small>
						Baz
					</ClayButton>
				}
				spritemap="/foo/bar"
				title="Foo:"
			>
				Bar!
			</ClayAlert>
		);

		expect(container).toMatchSnapshot();
	});
});

describe('IncrementalInteractions', () => {
	beforeEach(() => {
		jest.useFakeTimers();
	});

	afterEach(() => {
		cleanup();
		document.body.classList.remove('c-prefers-persistent-alerts');
		jest.useRealTimers();
	});

	it('calls the latest onClose when it changes while the timer runs', () => {
		const firstOnClose = jest.fn();
		const secondOnClose = jest.fn();

		const {rerender} = render(
			<ClayAlert
				autoClose={5000}
				onClose={firstOnClose}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		act(() => {
			jest.advanceTimersByTime(2000);
		});

		rerender(
			<ClayAlert
				autoClose={5000}
				onClose={secondOnClose}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		act(() => {
			jest.advanceTimersByTime(3000);
		});

		expect(secondOnClose).toHaveBeenCalledTimes(1);
		expect(firstOnClose).not.toHaveBeenCalled();
	});

	it('does not call onClose when the Provider disables auto close', () => {
		const onClose = jest.fn();

		render(
			<Provider persistentAlerts spritemap="/foo/bar">
				<ClayAlert autoClose={5000} onClose={onClose} title="Hello!" />
			</Provider>
		);

		act(() => {
			jest.advanceTimersByTime(20000);
		});

		expect(onClose).not.toHaveBeenCalled();
	});

	it('does not call onClose when a nested Provider inherits persistentAlerts', () => {
		const onClose = jest.fn();

		render(
			<Provider persistentAlerts spritemap="/foo/bar">
				<Provider spritemap="/foo/bar">
					<ClayAlert
						autoClose={5000}
						onClose={onClose}
						title="Hello!"
					/>
				</Provider>
			</Provider>
		);

		act(() => {
			jest.advanceTimersByTime(20000);
		});

		expect(onClose).not.toHaveBeenCalled();
	});

	it('calls onClose when a nested Provider overrides persistentAlerts with false', () => {
		const onClose = jest.fn();

		render(
			<Provider persistentAlerts spritemap="/foo/bar">
				<Provider persistentAlerts={false} spritemap="/foo/bar">
					<ClayAlert
						autoClose={5000}
						onClose={onClose}
						title="Hello!"
					/>
				</Provider>
			</Provider>
		);

		act(() => {
			jest.advanceTimersByTime(5000);
		});

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('does not call onClose when the body prefers persistent alerts at mount', () => {
		const onClose = jest.fn();

		document.body.classList.add('c-prefers-persistent-alerts');

		render(
			<ClayAlert
				autoClose={5000}
				onClose={onClose}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		act(() => {
			jest.advanceTimersByTime(20000);
		});

		expect(onClose).not.toHaveBeenCalled();
	});

	it('calls onClose after the remaining time when the body stops preferring persistent alerts', async () => {
		const onClose = jest.fn();

		render(
			<ClayAlert
				autoClose={5000}
				onClose={onClose}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		act(() => {
			jest.advanceTimersByTime(2000);
		});

		await act(async () => {
			document.body.classList.add('c-prefers-persistent-alerts');
		});

		act(() => {
			jest.advanceTimersByTime(20000);
		});

		expect(onClose).not.toHaveBeenCalled();

		await act(async () => {
			document.body.classList.remove('c-prefers-persistent-alerts');
		});

		act(() => {
			jest.advanceTimersByTime(2999);
		});

		expect(onClose).not.toHaveBeenCalled();

		act(() => {
			jest.advanceTimersByTime(1);
		});

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('does not call onClose when the body prefers persistent alerts over a Provider that enables auto close', () => {
		const onClose = jest.fn();

		document.body.classList.add('c-prefers-persistent-alerts');

		render(
			<Provider persistentAlerts={false} spritemap="/foo/bar">
				<ClayAlert autoClose={5000} onClose={onClose} title="Hello!" />
			</Provider>
		);

		act(() => {
			jest.advanceTimersByTime(20000);
		});

		expect(onClose).not.toHaveBeenCalled();
	});

	it('calls onClose after the remaining time when the Provider enables auto close again', () => {
		const onClose = jest.fn();

		const alert = (
			<ClayAlert autoClose={5000} onClose={onClose} title="Hello!" />
		);

		const {rerender} = render(
			<Provider spritemap="/foo/bar">{alert}</Provider>
		);

		act(() => {
			jest.advanceTimersByTime(2000);
		});

		rerender(
			<Provider persistentAlerts spritemap="/foo/bar">
				{alert}
			</Provider>
		);

		act(() => {
			jest.advanceTimersByTime(20000);
		});

		expect(onClose).not.toHaveBeenCalled();

		rerender(<Provider spritemap="/foo/bar">{alert}</Provider>);

		act(() => {
			jest.advanceTimersByTime(2999);
		});

		expect(onClose).not.toHaveBeenCalled();

		act(() => {
			jest.advanceTimersByTime(1);
		});

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('does not call onClose while hovered when the Provider enables auto close again', () => {
		const onClose = jest.fn();

		const alert = (
			<ClayAlert autoClose={5000} onClose={onClose} title="Hello!" />
		);

		const {container, rerender} = render(
			<Provider persistentAlerts spritemap="/foo/bar">
				{alert}
			</Provider>
		);

		const alertElement = container.querySelector('.alert') as HTMLElement;

		userEvent.hover(alertElement);

		rerender(<Provider spritemap="/foo/bar">{alert}</Provider>);

		act(() => {
			jest.advanceTimersByTime(20000);
		});

		expect(onClose).not.toHaveBeenCalled();

		userEvent.unhover(alertElement);

		act(() => {
			jest.advanceTimersByTime(5000);
		});

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('calls onClose after the current autoClose when it was false at mount', () => {
		const onClose = jest.fn();

		const {rerender} = render(
			<Provider spritemap="/foo/bar">
				<ClayAlert autoClose={false} onClose={onClose} title="Hello!" />
			</Provider>
		);

		const alert = (
			<ClayAlert autoClose={5000} onClose={onClose} title="Hello!" />
		);

		rerender(<Provider spritemap="/foo/bar">{alert}</Provider>);

		rerender(
			<Provider persistentAlerts spritemap="/foo/bar">
				{alert}
			</Provider>
		);

		rerender(<Provider spritemap="/foo/bar">{alert}</Provider>);

		act(() => {
			jest.advanceTimersByTime(4999);
		});

		expect(onClose).not.toHaveBeenCalled();

		act(() => {
			jest.advanceTimersByTime(1);
		});

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('does not call onClose when autoClose changes to false', () => {
		const onClose = jest.fn();

		const {rerender} = render(
			<ClayAlert
				autoClose={5000}
				onClose={onClose}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		act(() => {
			jest.advanceTimersByTime(2000);
		});

		rerender(
			<ClayAlert
				autoClose={false}
				onClose={onClose}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		act(() => {
			jest.advanceTimersByTime(10000);
		});

		expect(onClose).not.toHaveBeenCalled();
	});

	it('calls onClose after the remaining time when the mouse leaves after a pause', () => {
		const onClose = jest.fn();

		const {container} = render(
			<ClayAlert
				autoClose={5000}
				onClose={onClose}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		act(() => {
			jest.advanceTimersByTime(2000);
		});

		const alertElement = container.querySelector('.alert') as HTMLElement;

		userEvent.hover(alertElement);

		act(() => {
			jest.advanceTimersByTime(20000);
		});

		expect(onClose).not.toHaveBeenCalled();

		userEvent.unhover(alertElement);

		act(() => {
			jest.advanceTimersByTime(2999);
		});

		expect(onClose).not.toHaveBeenCalled();

		act(() => {
			jest.advanceTimersByTime(1);
		});

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('does not call onClose again when hovered after the timer fires', () => {
		const onClose = jest.fn();

		const {container} = render(
			<ClayAlert
				autoClose={5000}
				onClose={onClose}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		act(() => {
			jest.advanceTimersByTime(5000);
		});

		expect(onClose).toHaveBeenCalledTimes(1);

		const alertElement = container.querySelector('.alert') as HTMLElement;

		userEvent.hover(alertElement);
		userEvent.unhover(alertElement);

		act(() => {
			jest.advanceTimersByTime(5000);
		});

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('calls onClose once when the mouse leaves before the timer fires', () => {
		const onClose = jest.fn();

		const {container} = render(
			<ClayAlert
				autoClose={5000}
				onClose={onClose}
				spritemap="/foo/bar"
				title="Hello!"
			/>
		);

		act(() => {
			jest.advanceTimersByTime(2000);
		});

		userEvent.unhover(container.querySelector('.alert') as HTMLElement);

		act(() => {
			jest.advanceTimersByTime(10000);
		});

		expect(onClose).toHaveBeenCalledTimes(1);
	});
});
