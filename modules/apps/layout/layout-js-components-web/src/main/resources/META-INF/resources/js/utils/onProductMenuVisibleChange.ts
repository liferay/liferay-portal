/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function onProductMenuVisibleChange(
	callback: (visible: boolean) => void
) {
	if (document.body.classList.contains('product-menu-unified-sidebar')) {

		// The sidebar announces its state on every request, even when it does
		// not change, so only report real changes, like the legacy menu does.

		let currentVisible =
			document.body.classList.contains('product-menu-open');

		const handleStateChanged = ({visible}: {visible: boolean}) => {
			if (visible === currentVisible) {
				return;
			}

			currentVisible = visible;

			callback(visible);
		};

		Liferay.on('productMenuSidebarStateChanged', handleStateChanged);

		return {
			removeListener: () =>
				Liferay.detach(
					'productMenuSidebarStateChanged',
					handleStateChanged
				),
		};
	}

	// @ts-ignore

	const sideNavigation = Liferay.SideNavigation?.instance(
		document.querySelector('.product-menu-toggle')
	);

	if (!sideNavigation) {
		return {removeListener: () => {}};
	}

	const closedListener = sideNavigation.on('closed.lexicon.sidenav', () =>
		callback(false)
	);
	const openStartListener = sideNavigation.on(
		'openStart.lexicon.sidenav',
		() => callback(true)
	);

	return {
		removeListener: () => {
			closedListener.removeListener();
			openStartListener.removeListener();
		},
	};
}
