/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CONSENT_TYPE} from '~/util/enum';

interface IThemeDisplay {
	getBCP47LanguageId(): () => string;
	getCompanyGroupId: () => number;
	getDefaultLanguageId: () => string;
	getLanguageId: () => string;
	getPathThemeImages: () => string;
	getScopeGroupId: () => number;
	getSiteGroupId: () => number;
	getUserId: () => string;
	getUserName: () => string;
}

export type LiferayStorage = Storage & {
	getItem(key: string, consentType: CONSENT_TYPE): string | null;
	setItem(key: string, value: string, consentType: CONSENT_TYPE): void;
};

interface LiferayLanguage {
	get: (key: string) => string;
}

interface LiferaySession {
	reset: () => void;
}

interface LiferayUtil {
	LocalStorage: LiferayStorage;
	SessionStorage: LiferayStorage;
	fetch: typeof fetch;
	openToast: (options?: {
		message: string;
		onClick?: ({event}: {event: any}) => void;
		type?: 'danger' | 'success';
	}) => void;
}

type FetchType = (
	input: RequestInfo | URL,
	init?: RequestInit
) => Promise<Response>;

interface OAuth2Client {
	FromUserAgentApplication: (_userAgent: string) => {
		authorizeURL: string;
		clientId: string;
		encodedRedirectURL: string;
		fetch: FetchType;
		homePageURL: string;
		redirectURIs: string[];
		tokenURL: string;
	};
}

interface ILiferay {
	Language: LiferayLanguage;
	OAuth2Client: OAuth2Client;
	Session: LiferaySession;
	ThemeDisplay: IThemeDisplay;
	Util: LiferayUtil;
	authToken: string;
}

declare global {
	interface Window {
		Liferay: ILiferay;
	}
}

export const Liferay = window.Liferay || {
	Language: {
		get: (key: string) => key,
	},
	OAuth2Client: {
		FromUserAgentApplication: (_userAgent: string) => ({
			authorizeURL: '',
			clientId: '',
			encodedRedirectURL: '',
			homePageURL: '',
			redirectURIs: [''],
			tokenURL: '',
		}),
	},
	Session: {
		reset: () => null,
	},
	ThemeDisplay: {
		getBCP47LanguageId: () => 'en-US',
		getCompanyGroupId: () => 0,
		getDefaultLanguageId: () => 'en_US',
		getLanguageId: () => 'en_US',
		getPathThemeImages: () => '',
		getScopeGroupId: () => 0,
		getSiteGroupId: () => 0,
		getUserId: () => '0',
		getUserName: () => 'Test Test',
	},
	Util: {
		LocalStorage: localStorage,
		SessionStorage: sessionStorage,
		openToast: () => null,
	},
	authToken: '',
};

export const LIFERAY_URLS = {
	manage_roles: `${window.origin}/group/guest/~/control_panel/manage?p_p_id=com_liferay_roles_admin_web_portlet_RolesAdminPortlet&p_p_lifecycle=0&p_p_state=maximized`,
	manage_server: `${window.origin}/group/guest/~/control_panel/manage?p_p_id=com_liferay_server_admin_web_portlet_ServerAdminPortlet&p_p_lifecycle=0&p_p_state=maximized`,
	manage_user_groups: `${window.origin}/group/guest/~/control_panel/manage?p_p_id=com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet&p_p_lifecycle=0&p_p_state=maximized`,
};
