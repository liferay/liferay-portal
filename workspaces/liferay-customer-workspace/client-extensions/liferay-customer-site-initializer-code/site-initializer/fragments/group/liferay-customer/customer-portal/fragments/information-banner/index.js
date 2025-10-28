/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

const bannerElement = document.querySelectorAll('.cp-information-banner');
const bannerFragment = fragmentElement.querySelector('.cp-information-banner');
const closeButton = fragmentElement.querySelector('.close');

const currentDateTrimmed = new Date(new Date().setHours(0, 0, 0, 0));

const isAfterStart = () => {
	if (!configuration.displayStartDate) {
		return true;
	}

	const [month, day, year] = configuration.displayStartDate.split('/');
	const startDate = new Date(year, month - 1, day);

	return currentDateTrimmed >= startDate;
};

const isBeforeExpiration = () => {
	if (!configuration.displayEndDate) {
		return true;
	}

	const [month, day, year] = configuration.displayEndDate.split('/');
	const expirationDate = new Date(year, month - 1, day);

	return currentDateTrimmed < expirationDate;
};

const bannerState = !sessionStorage.getItem('@liferayCP:showBanner')
	? true
	: false;

const sessionStorageLogState = Liferay.ThemeDisplay.isSignedIn();

if (
	bannerState &&
	isAfterStart() &&
	isBeforeExpiration() &&
	sessionStorageLogState
) {
	bannerElement?.forEach((item) => {
		item.style.display = 'flex';
	});
}

if (closeButton) {
	closeButton.addEventListener('click', () => {
		sessionStorage.setItem('@liferayCP:showBanner', false);
		bannerFragment.style.display = 'none';
	});
}
