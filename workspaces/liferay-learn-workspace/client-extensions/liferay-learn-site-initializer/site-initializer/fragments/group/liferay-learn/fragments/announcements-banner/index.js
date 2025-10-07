/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const publicSiteNavigationContainer = document.querySelector(
	'.public-site-navigation-container'
);

const announcementsBanner = document.querySelector('.announcements-banner');
const editMode = document.body.classList.contains('has-edit-mode-menu');

if (editMode) {
	announcementsBanner.style.display = 'flex';
}

document.addEventListener('DOMContentLoaded', () => {
	const announcementsBannerClosed = sessionStorage.getItem(
		'@liferay-learn/announcements-banner-closed'
	);

	if (announcementsBannerClosed === 'true') {
		announcementsBanner.style.display = 'none';
		publicSiteNavigationContainer.classList.remove(
			'navigation-margin-true'
		);

		return;
	}

	if (announcementsBanner) {
		announcementsBanner.style.display = 'flex';

		if (themeDisplay.isSignedIn()) {
			announcementsBanner.style.top = '56px';
		}
		else {
			announcementsBanner.style.top = '0px';
		}

		publicSiteNavigationContainer.classList.add('navigation-margin-true');
	}

	document.querySelector('.icon-x').addEventListener('click', () => {
		document.querySelector('.announcements-banner').style.display = 'none';

		publicSiteNavigationContainer.classList.remove(
			'navigation-margin-true'
		);

		sessionStorage.setItem(
			'@liferay-learn/announcements-banner-closed',
			'true'
		);
	});
});
