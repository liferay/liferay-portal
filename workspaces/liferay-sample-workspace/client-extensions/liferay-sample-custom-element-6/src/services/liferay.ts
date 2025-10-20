/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type IOAuth2ClientAgentApplication = {
	authorizeURL: string;
	clientId: string;
	encodedRedirectURL: string;
	fetch: typeof fetch;
	homePageURL: string;
	redirectURIs: string[];
	tokenURL: string;
};

export type IOAuth2Client = {
	FromUserAgentApplication: (
		agentName: string
	) => IOAuth2ClientAgentApplication;
};

type ILiferay = {
	CommerceContext: {
		account?: {
			accountId: number | string | null;
			accountName: string | null;
		};
		commerceChannelId: string;
		currency: {
			currencyCode: string;
			currencyId: string;
		};
	};
	MarketplaceCustomerFlow: {appId: number};
	OAuth2Client: IOAuth2Client;
	Service: Function;
	ThemeDisplay: {
		getBCP47LanguageId: () => string;
		getCanonicalURL: () => string;
		getCompanyGroupId: () => string;
		getCompanyId: () => string;
		getDefaultLanguageId: () => string;
		getLanguageId: () => string;
		getLayoutRelativeURL: () => string;
		getLayoutURL: () => string;
		getPathContext: () => string;
		getPathThemeImages: () => string;
		getPortalURL: () => string;
		getScopeGroupId: () => number;
		getURLHome: () => string;
		getUserEmailAddress: () => string;
		getUserId: () => string;
		getUserName: () => string;
		isSignedIn: () => boolean;
	};
	Util: {
		fetch: typeof fetch;
		navigate: (path: string) => void;
		openModal: (options?: {}) => void;
		openToast: (options?: {
			message: string;
			onClick?: ({event}: {event: any}) => void;
			title?: string;
			type?: 'danger' | 'info' | 'success';
		}) => void;
	};
	authToken: string;
	detach: Function;
	fire: (event: string, data: unknown) => null;
	on: Function;
};

declare global {
	interface Window {
		Liferay: ILiferay;
	}
}

export const Liferay = window.Liferay || {
	CommerceContext: {},
	Service: {},
	ThemeDisplay: {
		getCanonicalURL: () => window.location.href,
		getCompanyGroupId: () => '',
		getCompanyId: () => '',
		getDefaultLanguageId: () => 'en_US',
		getLanguageId: () => '',
		getLayoutRelativeURL: () => '',
		getLayoutURL: () => '',
		getPathContext: () => '',
		getPathThemeImages: () => '',
		getPortalURL: () => '',
		getURLHome: () => '',
		getUserId: () => '',
		isSignedIn: () => {
			return false;
		},
	},
	Util: {
		LocalStorage: localStorage,
		SessionStorage: sessionStorage,
	},
	detach: (
		type: keyof WindowEventMap,
		callback: EventListenerOrEventListenerObject
	) => window.removeEventListener(type, callback),
	fire: () => null,
	on: (
		type: keyof WindowEventMap,
		callback: EventListenerOrEventListenerObject
	) => window.addEventListener(type, callback),
};
