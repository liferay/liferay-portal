import {ACCOUNTS, Routes, SEGMENTS, toRoute} from 'shared/util/router';

type IBasicRouteArgs = {
	groupId: string;
	label?: string | null;
};

type IBasicSidebarRouteArgs = IBasicRouteArgs & {
	channelId: string;
};

export type IBreadcrumbArgs = {
	active?: boolean;
	href?: string | null;
	groupId?: string;
	id?: string;
	label: string;
	truncate?: boolean;
};

export const getEntityName = ({
	active = true,
	label,
	truncate = true,
	...otherData
}: IBreadcrumbArgs) => ({
	active,
	label,
	truncate,
	...otherData,
});

/**
 * Home
 */
export const getHome = ({
	channelId,
	groupId,
	label,
}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.SITES, {channelId, groupId}),
	label: label ? label : Liferay.Language.get('home'),
});

/**
 * Entities
 */
export const getAccounts = ({channelId, groupId}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.CONTACTS_LIST_ENTITY, {
		channelId,
		groupId,
		type: ACCOUNTS,
	}),
	label: Liferay.Language.get('accounts'),
});

export const getCampaigns = ({channelId, groupId}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.CAMPAIGNS, {channelId, groupId}),
	label: Liferay.Language.get('campaigns'),
});

export const getIndividuals = ({
	LDPEnabled,
	channelId,
	groupId,
}: IBasicSidebarRouteArgs & {LDPEnabled: boolean}) => ({
	href: toRoute(
		LDPEnabled
			? Routes.CONTACTS_INDIVIDUALS
			: Routes.CONTACTS_INDIVIDUALS_KNOWN_INDIVIDUALS,
		{
			channelId,
			groupId,
		}
	),
	label: Liferay.Language.get('individuals'),
});

export const getSegments = ({channelId, groupId}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.CONTACTS_LIST_ENTITY, {
		channelId,
		groupId,
		type: SEGMENTS,
	}),
	label: Liferay.Language.get('segments'),
});

export const getSites = ({channelId, groupId}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.SITES, {
		channelId,
		groupId,
	}),
	label: Liferay.Language.get('sites'),
	truncate: true,
});

export const getPages = ({channelId, groupId}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.SITES_TOUCHPOINTS, {
		channelId,
		groupId,
	}),
	label: Liferay.Language.get('pages'),
	truncate: true,
});

export const getTests = ({channelId, groupId}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.TESTS, {channelId, groupId}),
	label: Liferay.Language.get('tests'),
});

/**
 * Assets
 */
export const getAssets = ({channelId, groupId}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.ASSETS, {channelId, groupId}),
	label: Liferay.Language.get('assets'),
});

export const getBlogs = ({channelId, groupId}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.ASSETS_BLOGS, {channelId, groupId}),
	label: Liferay.Language.get('blogs'),
});

export const getCustomContent = ({
	channelId,
	groupId,
}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.ASSETS_CUSTOM, {
		channelId,
		groupId,
	}),
	label: Liferay.Language.get('custom'),
});

export const getDocumentsAndMedia = ({
	channelId,
	groupId,
}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.ASSETS_DOCUMENTS_AND_MEDIA, {
		channelId,
		groupId,
	}),
	label: Liferay.Language.get('documents-and-media'),
});

export const getForms = ({channelId, groupId}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.ASSETS_FORMS, {
		channelId,
		groupId,
	}),
	label: Liferay.Language.get('forms'),
});

export const getWebContent = ({
	channelId,
	groupId,
}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.ASSETS_WEB_CONTENT, {
		channelId,
		groupId,
	}),
	label: Liferay.Language.get('web-content'),
});

/**
 * Settings
 */
export const getChannels = ({groupId}: IBasicRouteArgs) => ({
	href: toRoute(Routes.SETTINGS_CHANNELS, {groupId}),
	label: Liferay.Language.get('properties'),
});

export const getChannelName = ({
	active = false,
	groupId,
	id,
	label,
}: IBreadcrumbArgs) => ({
	active,
	href:
		groupId && id
			? toRoute(Routes.SETTINGS_CHANNELS_VIEW, {
					groupId,
					id,
				})
			: null,
	label,
	truncate: true,
});

export const getDataPrivacy = ({groupId}: IBasicRouteArgs) => ({
	href: toRoute(Routes.SETTINGS_DATA_PRIVACY, {groupId}),
	label: Liferay.Language.get('data-control-&-privacy'),
});

export const getDataSources = ({groupId}: IBasicRouteArgs) => ({
	href: toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {groupId}),
	label: Liferay.Language.get('data-sources'),
});

export const getDataSourceName = ({
	active = false,
	groupId,
	id,
	label,
}: IBreadcrumbArgs) => ({
	active,
	href:
		groupId && id
			? toRoute(Routes.SETTINGS_DATA_SOURCE, {
					groupId,
					id,
				})
			: null,
	label,
	truncate: true,
});

export const getDefinitions = ({groupId}: IBasicRouteArgs) => ({
	href: toRoute(Routes.SETTINGS_DEFINITIONS, {groupId}),
	label: Liferay.Language.get('definitions'),
});

export const getRecommendations = ({groupId}: IBasicRouteArgs) => ({
	href: toRoute(Routes.SETTINGS_RECOMMENDATIONS, {groupId}),
	label: Liferay.Language.get('recommendations'),
});

export const getEvents = ({groupId}: IBasicRouteArgs) => ({
	href: toRoute(Routes.SETTINGS_DEFINITIONS_EVENTS_DEFAULT, {groupId}),
	label: Liferay.Language.get('events'),
});

export const getEventAnalysis = ({
	channelId,
	groupId,
}: IBasicSidebarRouteArgs) => ({
	href: toRoute(Routes.EVENT_ANALYSIS, {
		channelId,
		groupId,
	}),
	label: Liferay.Language.get('event-analysis'),
});

export const getEventAttributes = ({groupId}: IBasicRouteArgs) => ({
	href: toRoute(Routes.SETTINGS_DEFINITIONS_EVENT_ATTRIBUTES_GLOBAL, {
		groupId,
	}),
	label: Liferay.Language.get('event-attributes'),
});
