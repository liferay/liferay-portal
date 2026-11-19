import {camelCase} from 'lodash';

export interface INavigationItem {
	active?: boolean;
	data?: {[key: string]: boolean | string};
	deprecated?: boolean;
	href?: string;
	icon?: string;
	label: string;
	navigationItems?: INavigationItem[];
}

export interface IBackendConfig {
	channelId?: string;
	friendlyURL?: string;
	groupId?: string;
	navigationItems?: INavigationItem[] | null;
	pageURLs?: {[friendlyURL: string]: string};
	params?: {[name: string]: string | undefined} | null;
	sidebarNavigationItems?: INavigationItem[] | null;
	signOutURL?: string;
}

export type Config = {
	[key: `${string}URL`]: string;
} & {
	channelId: string;
	groupId: string;
	navigationItems: INavigationItem[] | null;
	params: {[name: string]: string};
	sidebarNavigationItems: INavigationItem[] | null;
};

export let config = {
	channelId: '',
	friendlyURL: '',
	groupId: '',
	navigationItems: null,
	params: {},
	sidebarNavigationItems: null,
	signOutURL: '',
} as Config;

export function initializeConfig({
	channelId = '',
	friendlyURL = '',
	groupId = '',
	navigationItems,
	pageURLs = {},
	params,
	sidebarNavigationItems,
	signOutURL = '',
}: IBackendConfig) {
	const pageParams: {[name: string]: string} = {};

	for (const [name, value] of Object.entries(params ?? {})) {
		if (value) {
			pageParams[name] = value;
		}
	}

	const urls: {[key: string]: string} = {};

	for (const [pageFriendlyURL, url] of Object.entries(pageURLs)) {
		urls[`${camelCase(pageFriendlyURL)}URL`] = url;
	}

	config = {
		...urls,
		channelId,
		friendlyURL,
		groupId,
		navigationItems: navigationItems ?? null,
		params: pageParams,
		sidebarNavigationItems: sidebarNavigationItems ?? null,
		signOutURL,
	} as Config;
}
