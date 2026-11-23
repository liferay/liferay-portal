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
	readonly [key: `${string}URL`]: string;
} & {
	readonly channelId: string;
	readonly friendlyURL: string;
	readonly groupId: string;
	readonly navigationItems: INavigationItem[] | null;
	readonly params: Readonly<{[name: string]: string}>;
	readonly sidebarNavigationItems: INavigationItem[] | null;
	readonly signOutURL: string;
};

export function buildConfig({
	channelId = '',
	friendlyURL = '',
	groupId = '',
	navigationItems,
	pageURLs = {},
	params,
	sidebarNavigationItems,
	signOutURL = '',
}: IBackendConfig): Config {
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

	return Object.freeze({
		...urls,
		channelId,
		friendlyURL,
		groupId,
		navigationItems: navigationItems ?? null,
		params: Object.freeze(pageParams),
		sidebarNavigationItems: sidebarNavigationItems ?? null,
		signOutURL,
	}) as Config;
}
