/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function hideProductMenuIfPresent({
	onHide,
}: {
	onHide: () => void;
}) {

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
