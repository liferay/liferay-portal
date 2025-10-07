/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function main() {
	const menu = fragmentElement.querySelector('.dropdown-menu-container');
	const trigger = fragmentElement.querySelector('.dropdown-trigger');

	trigger?.addEventListener('click', () => {
		menu.classList.toggle('active');
	});

	document.addEventListener('click', (event) => {
		if (!fragmentElement.contains(event.target)) {
			menu.classList.remove('active');
		}
	});
}

main();
