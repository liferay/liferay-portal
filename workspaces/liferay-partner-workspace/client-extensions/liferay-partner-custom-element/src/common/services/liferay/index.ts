/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface IThemeDisplay {
	getBCP47LanguageId: () => string;
	getCompanyGroupId: () => number;
	getDefaultLanguageId: () => string;
	getLanguageId: () => string;
	getLayoutRelativeControlPanelURL: () => string;
	getLayoutRelativeURL: () => string;
	getPathThemeImages: () => string;
	getPortalURL: () => string;
	getScopeGroupId: () => number;
	getSiteGroupId: () => number;
	getUserEmailAddress: () => string;
	getUserId: () => string;
	getUserName: () => string;
}
interface IUtil {
	navigate: (path: string) => void;
	openConfirmModal: (options?: any) => void;
	openToast: (options?: any) => void;
}

interface ILiferayLanguage {
	get: (key: string) => string;
}

interface ILiferay {
	FeatureFlags: {[index: string]: boolean};
	Language: ILiferayLanguage;
	ThemeDisplay: IThemeDisplay;
	Util: IUtil;
	authToken: string;
}
declare global {
	interface Window {
		Liferay: ILiferay;
	}
}
export const Liferay = window.Liferay || {
	FeatureFlags: {},
	Language: {
		get: (key: string) => key,
	},
	ThemeDisplay: {
		getBCP47LanguageId: () => 'en-US',
		getCompanyGroupId: () => 0,
		getDefaultLanguageId: () => 'en_US',
		getLanguageId: () => 'en_US',
		getLayoutRelativeControlPanelURL: () => '',
		getLayoutRelativeURL: () => '',
		getPathThemeImages: () => '',
		getPortalURL: () => '',
		getScopeGroupId: () => 0,
		getSiteGroupId: () => 0,
		getUserEmailAddress: () => '',
		getUserId: () => '0',
		getUserName: () => 'Test Test',
	},
	authToken: '',
};
