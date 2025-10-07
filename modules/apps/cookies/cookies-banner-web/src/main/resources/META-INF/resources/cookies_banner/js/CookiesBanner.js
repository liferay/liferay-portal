/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {checkConsent, getOpener} from 'frontend-js-web';

import {
	acceptAllCookies,
	declineAllCookies,
	getCookie,
	setCookie,
	setUserConfigCookie,
	userConfigCookieName,
} from '../../js/CookiesUtil';

let openCookieConsentModal = () => {
	console.warn(
		'OpenCookieConsentModal was called, but cookie feature is not enabled'
	);
};

export default function ({
	configurationNamespace,
	configurationURL,
	includeDeclineAllButton,
	namespace,
	optionalConsentCookieTypeNames,
	requiredConsentCookieTypeNames,
	title,
}) {
	const acceptAllButton = document.getElementById(
		`${namespace}acceptAllButton`
	);
	const configurationButton = document.getElementById(
		`${namespace}configurationButton`
	);
	const declineAllButton = document.getElementById(
		`${namespace}declineAllButton`
	);
	const cookieBanner = document.querySelector('.cookies-banner');
	const editMode = document.body.classList.contains('has-edit-mode-menu');

	if (!editMode) {
		const cookieManager = document.getElementById(
			'_com_liferay_my_account_web_portlet_MyAccountPortlet_cookiesBannerConfigurationForm'
		);
		const productAnalyticsBanner = document.querySelector(
			'.product-analytics-banner'
		);

		if (
			cookieManager ||
			(productAnalyticsBanner &&
				productAnalyticsBanner.style.display === 'block')
		) {
			cookieBanner.style.display = 'none';
		}
		else {
			setBannerVisibility(cookieBanner);
		}

		const cookiePreferences = {};

		optionalConsentCookieTypeNames.forEach(
			(optionalConsentCookieTypeName) => {
				cookiePreferences[optionalConsentCookieTypeName] =
					getCookie(optionalConsentCookieTypeName) || 'false';
			}
		);

		Liferay.on('cookiePreferenceUpdate', (event) => {
			cookiePreferences[event.key] = event.value;
		});

		acceptAllButton.addEventListener('click', () => {
			cookieBanner.style.display = 'none';

			acceptAllCookies(
				optionalConsentCookieTypeNames,
				requiredConsentCookieTypeNames
			);

			setUserConfigCookie();
		});

		openCookieConsentModal = ({
			alertDisplayType,
			alertMessage,
			customTitle,
			onCloseFunction,
		}) => {
			let url = configurationURL;

			if (alertDisplayType) {
				url = `${url}&_${configurationNamespace}_alertDisplayType=${alertDisplayType}`;
			}

			if (alertMessage) {
				url = `${url}&_${configurationNamespace}_alertMessage=${alertMessage}`;
			}

			openModal({
				buttons: [
					{
						className: includeDeclineAllButton ? '' : 'd-none',
						displayType: 'secondary',
						label: Liferay.Language.get(
							'use-necessary-cookies-only'
						),
						onClick() {
							declineAllCookies(
								optionalConsentCookieTypeNames,
								requiredConsentCookieTypeNames
							);

							setUserConfigCookie();

							setBannerVisibility(cookieBanner);

							getOpener().Liferay.fire('closeModal');
						},
					},
					{
						displayType: 'secondary',
						label: Liferay.Language.get('accept-selected'),
						onClick() {
							Object.entries(cookiePreferences).forEach(
								([key, value]) => {
									setCookie(key, value);
								}
							);

							requiredConsentCookieTypeNames.forEach(
								(requiredConsentCookieTypeName) => {
									setCookie(
										requiredConsentCookieTypeName,
										'true'
									);
								}
							);

							setUserConfigCookie();

							setBannerVisibility(cookieBanner);

							getOpener().Liferay.fire('closeModal');
						},
					},
					{
						displayType: 'secondary',
						label: Liferay.Language.get('accept-all'),
						onClick() {
							acceptAllCookies(
								optionalConsentCookieTypeNames,
								requiredConsentCookieTypeNames
							);

							setUserConfigCookie();

							setBannerVisibility(cookieBanner);

							getOpener().Liferay.fire('closeModal');
						},
					},
				],
				displayType: 'primary',
				height: '70vh',
				id: 'cookiesBannerConfiguration',
				iframeBodyCssClass: '',
				onClose: onCloseFunction || undefined,
				size: 'lg',
				title: customTitle || title,
				url,
			});
		};

		configurationButton.addEventListener('click', () => {
			openCookieConsentModal({});
		});

		if (declineAllButton !== null) {
			declineAllButton.addEventListener('click', () => {
				cookieBanner.style.display = 'none';

				declineAllCookies(
					optionalConsentCookieTypeNames,
					requiredConsentCookieTypeNames
				);

				setUserConfigCookie();
			});
		}
	}
}

function checkCookieConsentForTypes(cookieTypes, modalOptions) {
	return new Promise((resolve, reject) => {
		if (isCookieTypesAccepted(cookieTypes)) {
			resolve();
		}
		else {
			openCookieConsentModal({
				alertDisplayType: modalOptions?.alertDisplayType || 'info',
				alertMessage: modalOptions?.alertMessage || null,
				customTitle: modalOptions?.customTitle || null,
				onCloseFunction: () =>
					isCookieTypesAccepted(cookieTypes) ? resolve() : reject(),
			});
		}
	});
}

function isCookieTypesAccepted(cookieTypes) {
	if (!Array.isArray(cookieTypes)) {
		cookieTypes = [cookieTypes];
	}

	return cookieTypes.every((cookieType) => checkConsent(cookieType));
}

function setBannerVisibility(cookieBanner) {
	if (getCookie(userConfigCookieName)) {
		cookieBanner.style.display = 'none';
	}
	else {
		cookieBanner.style.display = 'block';
	}
}

export {checkCookieConsentForTypes, openCookieConsentModal};
