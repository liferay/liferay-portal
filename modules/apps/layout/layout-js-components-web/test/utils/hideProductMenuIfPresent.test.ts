/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import hideProductMenuIfPresent from '../../src/main/resources/META-INF/resources/js/utils/hideProductMenuIfPresent';

function getHandler(eventName: string) {
	const [, handler] = (Liferay.on as jest.Mock).mock.calls.find(
		([name]) => name === eventName
	);

	return handler;
}

describe('hideProductMenuIfPresent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	afterEach(() => {
		document.body.className = '';
	});

	it('hides when there is no product menu', () => {
		const onHide = jest.fn();

		hideProductMenuIfPresent({onHide});

		expect(onHide).toHaveBeenCalledTimes(1);
		expect(Liferay.fire).not.toHaveBeenCalled();
	});

	it('hides right away when the sidebar is closed', () => {
		document.body.classList.add('product-menu-unified-sidebar');

		const onHide = jest.fn();

		hideProductMenuIfPresent({onHide});

		expect(onHide).toHaveBeenCalledTimes(1);
		expect(Liferay.fire).not.toHaveBeenCalled();
	});

	it('closes the open sidebar before hiding', () => {
		document.body.classList.add(
			'product-menu-open',
			'product-menu-unified-sidebar'
		);

		const onHide = jest.fn();

		hideProductMenuIfPresent({onHide});

		expect(Liferay.fire).toHaveBeenCalledWith(
			'productMenuSidebarStateRequested',
			{visible: false}
		);
		expect(onHide).not.toHaveBeenCalled();

		const handleStateChanged = getHandler('productMenuSidebarStateChanged');

		handleStateChanged({visible: true});

		expect(Liferay.fire).toHaveBeenCalledTimes(1);
		expect(onHide).not.toHaveBeenCalled();

		handleStateChanged({visible: false});

		expect(onHide).toHaveBeenCalledTimes(1);
		expect(Liferay.detach).toHaveBeenCalledWith(
			'productMenuSidebarStateChanged',
			handleStateChanged
		);
		expect(Liferay.detach).toHaveBeenCalledWith(
			'productMenuSidebarReady',
			getHandler('productMenuSidebarReady')
		);
	});

	it('asks again when the sidebar becomes ready after the request', () => {
		document.body.classList.add(
			'product-menu-open',
			'product-menu-unified-sidebar'
		);

		hideProductMenuIfPresent({onHide: jest.fn()});

		getHandler('productMenuSidebarReady')({visible: true});

		expect(Liferay.fire).toHaveBeenCalledTimes(2);
		expect(Liferay.fire).toHaveBeenLastCalledWith(
			'productMenuSidebarStateRequested',
			{visible: false}
		);
	});
});
