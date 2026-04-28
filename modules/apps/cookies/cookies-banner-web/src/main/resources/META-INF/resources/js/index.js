/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// Cookies API

export {
	default as CookiesBanner,
	checkCookieConsentForTypes,
	openCookieConsentModal,
} from '../cookies_banner/js/CookiesBanner';

export {default as CookiesBannerConfiguration} from '../cookies_banner_configuration/js/CookiesBannerConfiguration';

export {default as ConfigurationFormEventHandler} from './ConfigurationFormEventHandler';

export {
	initGlobalPrivacyControl,
	isGlobalPrivacyControlSignalActive,
} from './GlobalPrivacyControlUtil';

export {
	default as toggleThirdPartyCookies,
	runThirdPartyCookiesInterval,
	flipThirdPartyCookiesOff,
	suppressThirdPartyCookies,
} from './toggleThirdPartyCookies';
