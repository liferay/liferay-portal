import Constants, {DataSourceTypes, EntityTypes} from '../util/constants';
import {compile} from 'shared/util/path-to-regexp';
import {invert, isEmpty, isString, memoize} from 'lodash';
import {matchPath} from 'react-router-dom';

function createURL(href: string): URL {
	try {
		return new URL(href);
	}
	catch {
		return new URL(href, document.baseURI);
	}
}

function isDef(param: unknown): boolean {
	return param !== null && param !== undefined;
}

function addParam(url: URL, key: string, value: unknown): void {
	url.searchParams.delete(key);

	if (isDef(key) && isDef(value)) {
		url.searchParams.append(key, String(value));
	}
}

const {cur: defaultCur, orderDefault} = Constants.pagination;

/* Resource Types */

export const ACCOUNTS = 'accounts';
export const ACTIVITIES = 'activities';
export const ANALYTICS = 'analytics';
export const ANY = 'Any';
export const ASSETS = 'assets';
export const CONTACTS = 'contacts';
export const CSV = 'csv';
export const GROWTH = 'growth';
export const INDIVIDUALS = 'individuals';
export const LIFECYCLE = 'lifecycle';
export const LIFERAY = 'liferay';
export const PAGES = 'pages';
export const SEGMENTS = 'segments';
export const SETTINGS = 'settings';
export const TOUCHPOINTS = 'pages';
export const UI_KIT = 'ui-kit';
export const USERS = 'users';

/* Filter Key Constants */

export const PERIOD = 'rangeKey';
export const SEGMENT_CATEGORY = 'segmentCategory';
export const SEGMENT_STATE = 'state';
export const SEGMENT_TYPE = 'segmentType';
export const INDIVIDUAL_COUNT = 'individualCount';
export const DATE_MODIFIED = 'dateModified';
export const LAST_MEMBERSHIP_UPDATE_DATE = 'lastMembershipUpdateDate';
export const USER_NAME = 'userName';
export const STATUSES = 'statuses';
export const TYPES = 'types';

/* Routes */

export const Routes = buildRoutes({
	BASE: '/',
	LOADING: '/loading',
	LOGOUT: '/c/portal/logout',
	OAUTH_RECEIVE: '/oauth/receive',
	TEST: '/test',
	WORKSPACE: {
		path: '/workspace',
		routes: {
			WORKSPACE_ADD: '/add',
			WORKSPACE_ADD_TRIAL: '/add/trial',
			WORKSPACE_ADD_WITH_CORP_PROJECT_UUID: '/:corpProjectUuid/add',
			WORKSPACE_SELECT_ACCOUNT: '/select-account',
			WORKSPACE_WITH_ID: {
				path: '/:groupId',
				routes: {
					CHANNEL: {
						path: '/:channelId?',
						routes: {
							ASSETS: {
								path: '/assets',
								routes: {
									ASSETS_BLOGS: {
										path: '/blogs',
										routes: {
											ASSETS_BLOGS_ACCOUNTS:
												'/:assetId/accounts/:touchpoint/:title?/:type?',
											ASSETS_BLOGS_KNOWN_INDIVIDUALS:
												'/:assetId/known-individuals/:touchpoint/:title?/:type?',
											ASSETS_BLOGS_OVERVIEW:
												'/:assetId/page/:touchpoint/:title?/:type?',
											ASSETS_BLOGS_ROUTES:
												'/:assetId/:tabId/:touchpoint/:title?/:type?',
										},
									},
									ASSETS_CUSTOM: {
										path: '/custom',
										routes: {
											ASSETS_CUSTOM_DASHBOARD:
												'/:id/page/:touchpoint/:title?/:type?',
										},
									},
									ASSETS_DOCUMENTS_AND_MEDIA: {
										path: '/documents-and-media',
										routes: {
											ASSETS_DOCUMENTS_AND_MEDIA_ACCOUNTS:
												'/:assetId/accounts/:touchpoint/:title?/:type?',
											ASSETS_DOCUMENTS_AND_MEDIA_KNOWN_INDIVIDUALS:
												'/:assetId/known-individuals/:touchpoint/:title?/:type?',
											ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW:
												'/:assetId/page/:touchpoint/:title?/:type?',
											ASSETS_DOCUMENTS_AND_MEDIA_ROUTES:
												'/:assetId/:tabId/:touchpoint/:title?/:type?',
										},
									},
									ASSETS_FORMS: {
										path: '/forms',
										routes: {
											ASSETS_FORMS_ACCOUNTS:
												'/:assetId/accounts/:touchpoint/:title?/:type?',
											ASSETS_FORMS_KNOWN_INDIVIDUALS:
												'/:assetId/known-individuals/:touchpoint/:title?/:type?',
											ASSETS_FORMS_OVERVIEW:
												'/:assetId/page/:touchpoint/:title?/:type?',
											ASSETS_FORMS_ROUTES:
												'/:assetId/:tabId/:touchpoint/:title?/:type?',
										},
									},
									ASSETS_OBJECT_ENTRY: {
										path: '/object-entry',
										routes: {
											ASSETS_OBJECT_ENTRY_ACCOUNTS:
												'/:assetId/accounts/:touchpoint/:title?/:type?',
											ASSETS_OBJECT_ENTRY_KNOWN_INDIVIDUALS:
												'/:assetId/known-individuals/:touchpoint/:title?/:type?',
											ASSETS_OBJECT_ENTRY_OVERVIEW:
												'/:assetId/page/:touchpoint/:title?/:type?',
											ASSETS_OBJECT_ENTRY_ROUTES:
												'/:assetId/:tabId/:touchpoint/:title?/:type?',
										},
									},
									ASSETS_WEB_CONTENT: {
										path: '/web-content',
										routes: {
											ASSETS_WEB_CONTENT_ACCOUNTS:
												'/:assetId/accounts/:touchpoint/:title?/:type?',
											ASSETS_WEB_CONTENT_KNOWN_INDIVIDUALS:
												'/:assetId/known-individuals/:touchpoint/:title?/:type?',
											ASSETS_WEB_CONTENT_OVERVIEW:
												'/:assetId/page/:touchpoint/:title?/:type?',
											ASSETS_WEB_CONTENT_ROUTES:
												'/:assetId/:tabId/:touchpoint/:title?/:type?',
										},
									},
								},
							},
							CAMPAIGNS: {
								path: '/campaigns',
								routes: {},
							},
							CONTACTS: {
								path: `/${CONTACTS}`,
								routes: {
									CONTACTS_ACCOUNT: {
										path: `/${ACCOUNTS}/:id`,
										routes: {
											CONTACTS_ACCOUNT_ACTIVITIES: `/${ACTIVITIES}`,
											CONTACTS_ACCOUNT_DETAILS:
												'/details',
											CONTACTS_ACCOUNT_INDIVIDUALS: `/${INDIVIDUALS}`,
											CONTACTS_ACCOUNT_INTEREST_DETAILS:
												'/interests/:interestId/:tabId?',
											CONTACTS_ACCOUNT_INTERESTS:
												'/interests',
											CONTACTS_ACCOUNT_OVERVIEW:
												'/overview',
											CONTACTS_ACCOUNT_PROFILE:
												'/profile',
											CONTACTS_ACCOUNT_SEGMENTS: `/${SEGMENTS}`,
										},
									},
									CONTACTS_ENTITY: '/:type/:id',
									CONTACTS_INDIVIDUALS: {
										path: `/${INDIVIDUALS}`,
										routes: {
											CONTACTS_INDIVIDUALS_DISTRIBUTION:
												'/distribution',
											CONTACTS_INDIVIDUALS_INTEREST_DETAILS:
												'/interests/:interestId',
											CONTACTS_INDIVIDUALS_INTERESTS:
												'/interests',
											CONTACTS_INDIVIDUALS_KNOWN_INDIVIDUALS:
												{
													path: '/known-individuals',
													routes: {
														CONTACTS_INDIVIDUAL: {
															path: '/:id',
															routes: {
																CONTACTS_INDIVIDUAL_DETAILS:
																	'/details',
																CONTACTS_INDIVIDUAL_INTEREST_DETAILS:
																	'/interests/:interestId',
																CONTACTS_INDIVIDUAL_INTERESTS:
																	'/interests',
																CONTACTS_INDIVIDUAL_SEGMENTS: `/${SEGMENTS}`,
															},
														},
													},
												},
										},
									},

									// Deprecated - Prefer the more specific routes for the entity type

									CONTACTS_INTEREST_DETAILS:
										'/:type/:id/interests/:interestId',

									// Deprecated - Prefer the more specific routes for the entity type

									CONTACTS_INTERESTS: '/:type/:id/interests',

									/*
									 * CONTACTS_LIST_SEGMENT is kept separate to drive
									 * its own Router; CONTACTS_LIST_ENTITY is the
									 * consumable route.
									 */
									CONTACTS_LIST_ENTITY: '/:type',
									CONTACTS_LIST_SEGMENT: '/:type',
									CONTACTS_SEGMENT: {
										path: `/${SEGMENTS}/:id`,
										routes: {
											CONTACTS_SEGMENT_DISTRIBUTION:
												'/distribution',
											CONTACTS_SEGMENT_EDIT: '/edit',
											CONTACTS_SEGMENT_INTEREST_DETAILS:
												'/interests/:interestId/:tabId?',
											CONTACTS_SEGMENT_INTERESTS:
												'/interests',
											CONTACTS_SEGMENT_MEMBERSHIP:
												'/membership',
										},
									},
									CONTACTS_SEGMENT_CREATE: `/${SEGMENTS}/create`,
								},
							},
							EVENT_ANALYSIS: {
								path: '/event-analysis',
								routes: {
									EVENT_ANALYSIS_CREATE: '/create',
									EVENT_ANALYSIS_EDIT: '/:id',
								},
							},
							LIFECYCLE: {
								path: '/lifecycle',
								routes: {
									LIFECYCLE_CREATE: '/new',
									LIFECYCLE_EDIT: '/:lifecycleId/edit',
								},
							},
							SITES: {
								path: '/sites',
								routes: {
									SITES_INTERESTS: {
										path: '/interests',
										routes: {
											SITES_INTEREST_DETAILS:
												'/:interestId',
										},
									},
									SITES_SEARCH_TERMS: {
										path: '/search-terms',
										routes: {},
									},
									SITES_TOUCHPOINTS: {
										path: '/pages',
										routes: {
											SITES_TOUCHPOINTS_ACCOUNTS:
												'/accounts/:touchpoint/:title?',
											SITES_TOUCHPOINTS_KNOWN_INDIVIDUALS:
												'/known-individuals/:touchpoint/:title?',
											SITES_TOUCHPOINTS_OVERVIEW:
												'/overview/:touchpoint/:title?',
											SITES_TOUCHPOINTS_PATH:
												'/path/:touchpoint/:title?',
											SITES_TOUCHPOINTS_ROUTES:
												'/:typeId/:touchpoint/:title?',
										},
									},
								},
							},
							TESTS: {
								path: '/tests',
								routes: {
									TESTS_OVERVIEW: '/overview/:id',
								},
							},
							UI_KIT: '/ui-kit/:name?',
						},
					},
					SETTINGS: {
						path: '/settings',
						routes: {
							SETTINGS_APIS: {
								path: '/apis',
								routes: {
									SETTINGS_APIS_TOKEN_LIST: '/tokens',
								},
							},
							SETTINGS_CHANNELS: '/properties',
							SETTINGS_CHANNELS_VIEW: '/properties/:id',
							SETTINGS_CSV_UPLOAD: `/data-source/${CSV}`,
							SETTINGS_CSV_UPLOAD_CONFIGURE: `/data-source/${CSV}/:fileVersionId`,
							SETTINGS_DATA_PRIVACY: {
								path: '/data-privacy',
								routes: {
									SETTINGS_DATA_PRIVACY_REQUEST_LOG:
										'/request-log',
									SETTINGS_DATA_PRIVACY_SUPPRESSED_USERS:
										'/suppressed-users',
								},
							},
							SETTINGS_DATA_SOURCE: '/data-source/:id',
							SETTINGS_DATA_SOURCE_CLEAR_DATA:
								'/data-source/:id/clear-data',
							SETTINGS_DATA_SOURCE_DELETE:
								'/data-source/:id/delete',
							SETTINGS_DATA_SOURCE_EDIT: '/data-source/:id/edit',
							SETTINGS_DATA_SOURCE_LIST: '/data-source',
							SETTINGS_DATA_SOURCE_ONBOARDING:
								'/data-source/:id/onboarding',
							SETTINGS_DEFINITIONS: {
								path: '/definitions',
								routes: {
									SETTINGS_DEFINITIONS_BEHAVIORS:
										'/behaviors',
									SETTINGS_DEFINITIONS_EVENT_ATTRIBUTES: {
										path: '/event-attributes',
										routes: {
											SETTINGS_DEFINITIONS_EVENT_ATTRIBUTES_GLOBAL:
												'/global',
											SETTINGS_DEFINITIONS_EVENT_ATTRIBUTES_LOCAL:
												'/local',
											SETTINGS_DEFINITIONS_EVENT_ATTRIBUTES_VIEW:
												'/:attributeId',
										},
									},
									SETTINGS_DEFINITIONS_EVENTS: {
										path: '/events',
										routes: {
											SETTINGS_DEFINITIONS_EVENTS_BLOCK_LIST:
												'/block-list',
											SETTINGS_DEFINITIONS_EVENTS_CUSTOM:
												'/custom',
											SETTINGS_DEFINITIONS_EVENTS_DEFAULT:
												'/default',
											SETTINGS_DEFINITIONS_EVENTS_VIEW:
												'/:eventId',
										},
									},
									SETTINGS_DEFINITIONS_INDIVIDUAL_ATTRIBUTES:
										'/individual-attributes',
									SETTINGS_DEFINITIONS_INTEREST_TOPICS:
										'/interest-topics',
									SETTINGS_DEFINITIONS_SEARCH: '/search',
								},
							},
							SETTINGS_FEATURE_FLAGS: '/feature-flags',
							SETTINGS_RECOMMENDATIONS: {
								path: '/recommendations',
								routes: {
									SETTINGS_RECOMMENDATION_MODEL_VIEW: {
										path: '/:jobId',
										routes: {
											SETTINGS_RECOMMENDATION_EDIT:
												'/edit',
										},
									},
									SETTINGS_RECOMMENDATIONS_CREATE_ITEM_SIMILARITY_MODEL:
										'/create-item-similarity-model',
								},
							},
							SETTINGS_USAGE: '/usage',
							SETTINGS_USERS: {
								path: '/users',
								routes: {
									SETTINGS_USERS_REQUESTS: '/requests',
								},
							},
							SETTINGS_WORKSPACE: '/workspace',
						},
					},
				},
			},
		},
	},
	WORKSPACES: '/workspaces',
});

type Config = {
	[key: string]:
		| string
		| {
				path?: string;
				routes?: Config;
		  };
};

export function buildRoutes(
	config: Config,
	routes: {[key: string]: string} = {},
	prefix: string = ''
): {[key: string]: string} {
	for (const [key, pathOrConfig] of Object.entries(config)) {
		if (isString(pathOrConfig)) {
			routes[key] = prefix + pathOrConfig;
		}
		else {
			routes[key] = prefix + pathOrConfig.path;

			if (pathOrConfig.routes) {
				buildRoutes(pathOrConfig.routes, routes, routes[key]);
			}
		}
	}

	return routes;
}

const getCompiledRoute = memoize(compile);

export function toRoute(route: string, options?: {[key: string]: any}) {
	return getCompiledRoute(route)(options || {});
}

const ROUTE_TO_TYPE_MAP = {
	[ACCOUNTS]: EntityTypes.Account,
	[ASSETS]: EntityTypes.Asset,
	[INDIVIDUALS]: EntityTypes.Individual,
	[PAGES]: EntityTypes.Page,
	[SEGMENTS]: EntityTypes.IndividualsSegment,
};

const PROVIDER_ROUTE_TO_TYPE_MAP = {
	[CSV]: DataSourceTypes.Csv,
	[LIFERAY]: DataSourceTypes.Liferay,
};

const TYPE_TO_ROUTE_MAP = {
	...invert(ROUTE_TO_TYPE_MAP),
};

export const assetTypePaths = {
	blog: Routes.ASSETS_BLOGS_OVERVIEW,
	custom: Routes.ASSETS_CUSTOM_DASHBOARD,
	document: Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW,
	form: Routes.ASSETS_FORMS_OVERVIEW,
	journal: Routes.ASSETS_WEB_CONTENT_OVERVIEW,
};

export const toAssetOverviewRoute = (
	assetType: keyof typeof assetTypePaths,
	routeParams: {[key: string]: any},
	query: {[key: string]: any}
) => {
	let route = '';

	if (assetType === 'blog') {
		route = toRoute(assetTypePaths[assetType], {
			...routeParams,
			assetType: 'blogs',
		});
	}
	else {
		route = toRoute(assetTypePaths[assetType], routeParams);
	}

	return !isEmpty(query) ? setUriQueryValues(query, route) : route;
};

export function getType(routeName: keyof typeof ROUTE_TO_TYPE_MAP) {
	return ROUTE_TO_TYPE_MAP[routeName];
}

export function getRouteName(type: keyof typeof TYPE_TO_ROUTE_MAP) {
	return TYPE_TO_ROUTE_MAP[type];
}

export function getDataSourceType(
	routeName: keyof typeof PROVIDER_ROUTE_TO_TYPE_MAP
) {
	return PROVIDER_ROUTE_TO_TYPE_MAP[routeName];
}

/**
 * Return the matched route by comparing pathname
 * to an array of routes until a match is found or
 * the routes array has all been checked.
 * @param {Array.<Object>} routes - Array of routes to check for match.
 * @param {string} pathname - The current pathname.
 * @returns {string} Matched path string or null if no match.
 */
export function getMatchedRoute(
	routes: {exact?: boolean; route: string}[],
	pathname = location.pathname
) {
	const matchedRoute = routes.find(({exact = true, route}) =>
		matchPath({end: exact, path: route}, pathname)
	);

	return (matchedRoute && matchedRoute.route) || null;
}

/**
 * Represents the query filter parameters as an Immutable Map
 * with filter fields as Map keys and the field values as Map values.
 * @example new Map({devices: new Set(['desktop', 'mobile'])})
 * @typedef {Map.<Set>} FilterBy
 */

/**
 * Return the url with filter params added.
 * @param {FilterBy} filterBy - A Map of active filters.
 * @param {string} href - The url with filter params added.
 */
export function setUriFilterValues(
	filterBy: {
		forEach: (
			callback: (
				valueISet: {
					filter: (predicate: (value: unknown) => unknown) => {
						toArray: () => unknown[];
					};
				},
				key: string
			) => void
		) => void;
	},
	href = window.location.href
) {
	const uri = createURL(href);

	filterBy.forEach((valueISet, key) => {
		addParam(uri, key, valueISet.filter(Boolean).toArray());
	});

	return `${uri.pathname}${uri.search}`;
}

export function setUriQueryValue(href: string, name: string, value: unknown) {
	const uri = createURL(href);

	addParam(uri, name, value);

	return `${uri.pathname}${uri.search}`;
}

export function setUriQueryValues(
	values: {[key: string]: any},
	href = window.location.href
) {
	const uri = createURL(href);

	for (const [name, value] of Object.entries(values)) {
		addParam(uri, name, value);
	}

	return `${uri.pathname}${uri.search}`;
}

/**
 * Remove URL Query Param
 * @param {string} href
 * @param {string} names
 */
export function removeUriQueryParam(href: string, ...names: string[]) {
	const uri = createURL(href);

	for (const name of names) {
		uri.searchParams.delete(name);
	}

	return `${uri.pathname}${uri.search}`;
}

export function removePageParam(newPath: string, href = window.location.href) {
	const uri = createURL(href);

	if (newPath) {
		uri.pathname = newPath;
	}

	uri.searchParams.delete('page');

	return `${uri.pathname}${uri.search}`;
}

export function resetPaginationParams(
	newPath: string,
	href = window.location.href
) {
	const uri = createURL(href);

	if (newPath) {
		uri.pathname = newPath;
	}

	addParam(uri, 'page', defaultCur);
	addParam(uri, 'orderBy', orderDefault);
	addParam(uri, 'query', '');

	return `${uri.pathname}${uri.search}`;
}

export function reloadPage() {
	window.location.reload();
}
