/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {checkConsent, getOpener} from 'frontend-js-web';

import {
	acceptAllCookies,
	declineAllCookies,
	getCookie,
	productAnalyticsConfiguredCookieName,
	setCookie,
	setProductAnalyticsConfigCookie,
	userConfigCookieName,
} from '../../js/CookiesUtil';

let openProductAnalyticsConsentModal = () => {
	console.warn(
		'OpenProductAnalyticsConsentModal was called, but product analytics feature is not enabled'
	);
};

export default function ({
	configurationNamespace,
	configurationURL,
	namespace,
	optionalConsentCookieTypeNames,
	requiredConsentCookieTypeNames,
	title,
}) {
	const acceptAllButton = document.getElementById(
		`${namespace}acceptAllButton`
	);
	const customizeButton = document.getElementById(
		`${namespace}customizeButton`
	);
	const declineAllButton = document.getElementById(
		`${namespace}declineAllButton`
	);
	const productAnalyticsBanner = document.querySelector(
		'.product-analytics-banner'
	);
	const editMode = document.body.classList.contains('has-edit-mode-menu');

	if (!editMode) {
		setBannerVisibility(productAnalyticsBanner);

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
			productAnalyticsBanner.style.display = 'none';

			acceptAllCookies(
				optionalConsentCookieTypeNames,
				requiredConsentCookieTypeNames
			);

			setProductAnalyticsConfigCookie();
			setBannerVisibility(productAnalyticsBanner);
		});

		openProductAnalyticsConsentModal = ({
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
						displayType: 'secondary',
						label: Liferay.Language.get(
							'use-necessary-cookies-only'
						),
						onClick() {
							declineAllCookies(
								optionalConsentCookieTypeNames,
								requiredConsentCookieTypeNames
							);

							setProductAnalyticsConfigCookie();

							setBannerVisibility(productAnalyticsBanner);

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

							setProductAnalyticsConfigCookie();

							setBannerVisibility(productAnalyticsBanner);

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

							setProductAnalyticsConfigCookie();

							setBannerVisibility(productAnalyticsBanner);

							getOpener().Liferay.fire('closeModal');
						},
					},
				],
				displayType: 'primary',
				height: '70vh',
				id: 'productAnalyticsConsentPanel',
				iframeBodyCssClass: '',
				onClose: onCloseFunction || undefined,
				size: 'lg',
				title: customTitle || title,
				url,
			});
		};

		customizeButton.addEventListener('click', () => {
			openProductAnalyticsConsentModal({});
		});

		if (declineAllButton !== null) {
			declineAllButton.addEventListener('click', () => {
				productAnalyticsBanner.style.display = 'none';

				declineAllCookies(
					optionalConsentCookieTypeNames,
					requiredConsentCookieTypeNames
				);

				setProductAnalyticsConfigCookie();
				setBannerVisibility(productAnalyticsBanner);
			});
		}
	}
}

function checkProductAnalyticsConsentForTypes(cookieTypes, modalOptions) {
	return new Promise((resolve, reject) => {
		if (isCookieTypesAccepted(cookieTypes)) {
			resolve();
		}
		else {
			openProductAnalyticsConsentModal({
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

function setBannerVisibility(productAnalyticsBanner) {
	const cookieBanner = document.querySelector('.cookies-banner');
	const productAnalytics = document.getElementById(
		'_com_liferay_my_account_web_portlet_MyAccountPortlet_productAnalyticsConsentPanelForm'
	);

	if (getCookie(productAnalyticsConfiguredCookieName) || productAnalytics) {
		productAnalyticsBanner.style.display = 'none';

		if (cookieBanner) {
			const cookieManager = document.getElementById(
				'_com_liferay_my_account_web_portlet_MyAccountPortlet_cookiesBannerConfigurationForm'
			);

			if (cookieManager || getCookie(userConfigCookieName)) {
				cookieBanner.style.display = 'none';
			}
			else {
				cookieBanner.style.display = 'block';
			}
		}
	}
	else {
		productAnalyticsBanner.style.display = 'block';

		if (cookieBanner) {
			cookieBanner.style.display = 'none';
		}
	}
}

export {checkProductAnalyticsConsentForTypes, openProductAnalyticsConsentModal};
