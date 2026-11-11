/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function hideProductMenuSidebar(onHide: () => void) {
	if (!document.body.classList.contains('product-menu-open')) {
		onHide();

		return;
	}

	function requestHide() {
		Liferay.fire('productMenuSidebarStateRequested', {visible: false});
	}

	function handleReady({visible}: {visible: boolean}) {

		// The sidebar may have mounted after the first request, so ask again.

		if (visible) {
			requestHide();
		}
	}

	function handleStateChanged({visible}: {visible: boolean}) {
		if (visible) {
			return;
		}

		Liferay.detach('productMenuSidebarReady', handleReady);
		Liferay.detach('productMenuSidebarStateChanged', handleStateChanged);

		onHide();
	}

	Liferay.on('productMenuSidebarReady', handleReady);
	Liferay.on('productMenuSidebarStateChanged', handleStateChanged);

	requestHide();
}

export default function hideProductMenuIfPresent({
	onHide,
}: {
	onHide: () => void;
}) {
	if (document.body.classList.contains('product-menu-unified-sidebar')) {
		hideProductMenuSidebar(onHide);

		return;
	}

	// @ts-ignore

	const sideNavigation = Liferay.SideNavigation?.instance(
		document.querySelector('.product-menu-toggle')
	);

	if (sideNavigation?.visible()) {
		let removeListener = () => {};

		const listener = sideNavigation.on('closed.lexicon.sidenav', () => {
			onHide();

			removeListener();
		});

		removeListener = () => listener.removeListener();

		sideNavigation.hide();
	}
	else {
		onHide();
	}
}
