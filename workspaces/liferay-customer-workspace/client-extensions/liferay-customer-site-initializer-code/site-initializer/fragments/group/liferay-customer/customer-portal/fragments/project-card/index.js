/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

document.addEventListener('DOMContentLoaded', () => {
	const projectCard = document.getElementById('projectCard');

	if (!projectCard) {
		return;
	}

	projectCard.addEventListener('click', function (event) {
		const isInteractiveButton = event.target.closest('a, button');
		const targetURL = this.dataset.href;

		if (isInteractiveButton || !targetURL) {
			return;
		}

		window.location.href = targetURL;
	});
});
