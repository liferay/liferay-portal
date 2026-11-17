/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import onProductMenuVisibleChange from '../../src/main/resources/META-INF/resources/js/utils/onProductMenuVisibleChange';

function getHandler(eventName: string) {
	const [, handler] = (Liferay.on as jest.Mock).mock.calls.find(
		([name]) => name === eventName
	);

	return handler;
}

describe('onProductMenuVisibleChange', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	afterEach(() => {
		document.body.className = '';

		delete (Liferay as any).SideNavigation;
	});

	it('does nothing when there is no product menu', () => {
		const callback = jest.fn();

		onProductMenuVisibleChange(callback).removeListener();

		expect(callback).not.toHaveBeenCalled();
		expect(Liferay.on).not.toHaveBeenCalled();
	});

	it('follows the legacy product menu', () => {
		const listeners: Record<string, () => void> = {};
		const removeListener = jest.fn();

		(Liferay as any).SideNavigation = {
			instance: () => ({
				on: (eventName: string, listener: () => void) => {
					listeners[eventName] = listener;

					return {removeListener};
				},
			}),
		};

		const callback = jest.fn();

		const handle = onProductMenuVisibleChange(callback);

		listeners['openStart.lexicon.sidenav']();

		expect(callback).toHaveBeenLastCalledWith(true);

		listeners['closed.lexicon.sidenav']();

		expect(callback).toHaveBeenLastCalledWith(false);

		handle.removeListener();

		expect(removeListener).toHaveBeenCalledTimes(2);
	});

	it('follows the unified sidebar', () => {
		document.body.classList.add('product-menu-unified-sidebar');

		const callback = jest.fn();

		const handle = onProductMenuVisibleChange(callback);

		const handleStateChanged = getHandler('productMenuSidebarStateChanged');

		handleStateChanged({visible: true});

		expect(callback).toHaveBeenLastCalledWith(true);

		handleStateChanged({visible: false});

		expect(callback).toHaveBeenLastCalledWith(false);

		handle.removeListener();

		expect(Liferay.detach).toHaveBeenCalledWith(
			'productMenuSidebarStateChanged',
			handleStateChanged
		);
	});

	it('ignores the unified sidebar repeating its state', () => {
		document.body.classList.add(
			'product-menu-open',
			'product-menu-unified-sidebar'
		);

		const callback = jest.fn();

		onProductMenuVisibleChange(callback);

		const handleStateChanged = getHandler('productMenuSidebarStateChanged');

		handleStateChanged({visible: true});

		expect(callback).not.toHaveBeenCalled();

		handleStateChanged({visible: false});
		handleStateChanged({visible: false});

		expect(callback).toHaveBeenCalledTimes(1);
	});
});
