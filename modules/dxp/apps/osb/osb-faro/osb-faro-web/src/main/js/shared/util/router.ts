import Constants, {DataSourceTypes, EntityTypes} from '../util/constants';
import pathToRegexp from 'path-to-regexp';
import {invert, isEmpty, isString, memoize} from 'lodash';
import {matchPath} from 'react-router-dom';

function createURL(href) {
	try {
		return new URL(href);
	} catch {
		return new URL(href, document.baseURI);
	}
}

function isDef(param) {
	return param !== null && param !== undefined;
}

function addParam(url, key, value) {
	url.searchParams.delete(key);

	if (isDef(key) && isDef(value)) {
		url.searchParams.append(key, value);
	}
}

const {cur: defaultCur, orderDefault} = Constants.pagination;

/* Resource Types */

export const ACCOUNTS = 'accounts';
export const ACTIVITIES = 'activities';
export const ANALYTICS = 'analytics';
export const ANY = 'Any';
export const ASSETS = 'assets';
export const COMMERCE = 'commerce';
export const CONTACTS = 'contacts';
export const CSV = 'csv';
export const GROWTH = 'growth';
export const INDIVIDUALS = 'individuals';
export const LIFERAY = 'liferay';
export const PAGES = 'pages';
export const SALESFORCE = 'salesforce';
export const SEGMENTS = 'segments';
export const SETTINGS = 'settings';
export const TOUCHPOINTS = 'pages';
export const UI_KIT = 'ui-kit';
export const USERS = 'users';

/* Filter Key Constants */

export const PERIOD = 'rangeKey';
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
				path: '/:groupId([\\w._-]+)',
				routes: {
					CHANNEL: {
						path: '/:channelId(\\d+)?',
						routes: {
							ASSETS: {
								path: '/assets',
								routes: {
									ASSETS_BLOGS: {
										path: '/:assetType(blogs)?',
										routes: {
											ASSETS_BLOGS_KNOWN_INDIVIDUALS:
												'/:assetId/known-individuals/:touchpoint/:title?',
											ASSETS_BLOGS_OVERVIEW:
												'/:assetId/page/:touchpoint/:title?',
											ASSETS_BLOGS_ROUTES:
												'/:assetId/:tabId(page|known-individuals)/:touchpoint/:title?'
										}
									},
									ASSETS_CUSTOM: {
										path: '/custom',
										routes: {
											ASSETS_CUSTOM_DASHBOARD:
												'/:id/page/:touchpoint/:title?'
										}
									},
									ASSETS_DOCUMENTS_AND_MEDIA: {
										path: '/documents-and-media',
										routes: {
											ASSETS_DOCUMENTS_AND_MEDIA_KNOWN_INDIVIDUALS:
												'/:assetId/known-individuals/:touchpoint/:title?',
											ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW:
												'/:assetId/page/:touchpoint/:title?',
											ASSETS_DOCUMENTS_AND_MEDIA_ROUTES:
												'/:assetId/:tabId(page|known-individuals)/:touchpoint/:title'
										}
									},
									ASSETS_FORMS: {
										path: '/forms',
										routes: {
											ASSETS_FORMS_KNOWN_INDIVIDUALS:
												'/:assetId/known-individuals/:touchpoint/:title?',
											ASSETS_FORMS_OVERVIEW:
												'/:assetId/page/:touchpoint/:title?',
											ASSETS_FORMS_ROUTES:
												'/:assetId/:tabId(page|known-individuals)/:touchpoint/:title?'
										}
									},
									ASSETS_WEB_CONTENT: {
										path: '/web-content',
										routes: {
											ASSETS_WEB_CONTENT_KNOWN_INDIVIDUALS:
												'/:assetId/known-individuals/:touchpoint/:title?',
											ASSETS_WEB_CONTENT_OVERVIEW:
												'/:assetId/page/:touchpoint/:title?',
											ASSETS_WEB_CONTENT_ROUTES:
												'/:assetId/:tabId(page|known-individuals)/:touchpoint/:title?'
										}
									}
								}
							},
							COMMERCE: `/${COMMERCE}`,
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
											CONTACTS_ACCOUNT_INTEREST_DETAILS: `/interests/:interestId/:tabId(${INDIVIDUALS}|${PAGES})?`,
											CONTACTS_ACCOUNT_INTERESTS:
												'/interests',
											CONTACTS_ACCOUNT_SEGMENTS: `/${SEGMENTS}`
										}
									},
									CONTACTS_ENTITY: `/:type(${ACCOUNTS}|${INDIVIDUALS}|${SEGMENTS})/:id`,
									CONTACTS_INDIVIDUALS: {
										path: `/${INDIVIDUALS}`,
										routes: {
											CONTACTS_INDIVIDUALS_DISTRIBUTION:
												'/distribution',
											CONTACTS_INDIVIDUALS_INTEREST_DETAILS:
												'/interests/:interestId',
											CONTACTS_INDIVIDUALS_INTERESTS:
												'/interests',
											CONTACTS_INDIVIDUALS_KNOWN_INDIVIDUALS: {
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
															CONTACTS_INDIVIDUAL_SEGMENTS: `/${SEGMENTS}`
														}
													}
												}
											}
										}
									},
									// Deprecated - Prefer the more specific routes for the entity type
									CONTACTS_INTEREST_DETAILS: `/:type(${ACCOUNTS}|${INDIVIDUALS}|${SEGMENTS})/:id/interests/:interestId`,
									// Deprecated - Prefer the more specific routes for the entity type
									CONTACTS_INTERESTS: `/:type(${ACCOUNTS}|${INDIVIDUALS}|${SEGMENTS})/:id/interests`,

									/*
									 * CONTACTS_LIST_ACCOUNT, CONTACTS_LIST_INDIVIDUAL and CONTACTS_LIST_SEGMENT are
									 * separate for the sake of keeping two separate Routers.
									 * CONTACTS_LIST_ENTITY should be used as consumable route.
									 */
									CONTACTS_LIST_ACCOUNT: `/:type(${ACCOUNTS})`,
									CONTACTS_LIST_ENTITY: `/:type(${ACCOUNTS}|${INDIVIDUALS}|${SEGMENTS})`,
									CONTACTS_LIST_INDIVIDUAL: `/:type(${INDIVIDUALS})`,
									CONTACTS_LIST_SEGMENT: `/:type(${SEGMENTS})`,
									CONTACTS_SEGMENT: {
										path: `/${SEGMENTS}/:id`,
										routes: {
											CONTACTS_SEGMENT_DISTRIBUTION:
												'/distribution',
											CONTACTS_SEGMENT_EDIT: '/edit',
											CONTACTS_SEGMENT_INTEREST_DETAILS: `/interests/:interestId/:tabId(${INDIVIDUALS}|${PAGES})?`,
											CONTACTS_SEGMENT_INTERESTS:
												'/interests',
											CONTACTS_SEGMENT_MEMBERSHIP:
												'/membership'
										}
									},
									CONTACTS_SEGMENT_CREATE: `/${SEGMENTS}/create`
								}
							},
							EVENT_ANALYSIS: {
								path: '/event-analysis',
								routes: {
									EVENT_ANALYSIS_CREATE: '/create',
									EVENT_ANALYSIS_EDIT: '/:id'
								}
							},
							SITES: {
								path: '/sites',
								routes: {
									SITES_INTERESTS: {
										path: '/interests',
										routes: {
											SITES_INTEREST_DETAILS:
												'/:interestId'
										}
									},
									SITES_SEARCH_TERMS: {
										path: '/search-terms',
										routes: {}
									},
									SITES_TOUCHPOINTS: {
										path: '/pages',
										routes: {
											SITES_TOUCHPOINTS_KNOWN_INDIVIDUALS:
												'/known-individuals/:touchpoint/:title?',
											SITES_TOUCHPOINTS_OVERVIEW:
												'/overview/:touchpoint/:title?',
											SITES_TOUCHPOINTS_PATH:
												'/path/:touchpoint/:title?',
											SITES_TOUCHPOINTS_ROUTES:
												'/:typeId/:touchpoint/:title?'
										}
									}
								}
							},
							TESTS: {
								path: '/tests',
								routes: {
									TESTS_OVERVIEW: '/overview/:id'
								}
							},
							UI_KIT: '/ui-kit/:name?'
						}
					},
					SETTINGS: {
						path: '/settings',
						routes: {
							SETTINGS_ADD_DATA_SOURCE: '/data-source/add',
							SETTINGS_APIS: {
								path: '/apis',
								routes: {
									SETTINGS_APIS_TOKEN_LIST: '/tokens'
								}
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
										'/suppressed-users'
								}
							},
							SETTINGS_DATA_SOURCE: '/data-source/:id',
							SETTINGS_DATA_SOURCE_CLEAR_DATA:
								'/data-source/:id/clear-data',
							SETTINGS_DATA_SOURCE_DELETE:
								'/data-source/:id/delete',
							SETTINGS_DATA_SOURCE_EDIT: '/data-source/:id/edit',
							SETTINGS_DATA_SOURCE_LIST: '/data-source',
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
												'/:attributeId(\\d+)'
										}
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
												'/:eventId(\\d+)'
										}
									},
									SETTINGS_DEFINITIONS_INDIVIDUAL_ATTRIBUTES:
										'/individual-attributes',
									SETTINGS_DEFINITIONS_INTEREST_TOPICS:
										'/interest-topics',
									SETTINGS_DEFINITIONS_SEARCH: '/search'
								}
							},
							SETTINGS_RECOMMENDATIONS: {
								path: '/recommendations',
								routes: {
									SETTINGS_RECOMMENDATION_MODEL_VIEW: {
										path: '/:jobId([\\d]+)',
										routes: {
											SETTINGS_RECOMMENDATION_EDIT:
												'/edit'
										}
									},
									SETTINGS_RECOMMENDATIONS_CREATE_ITEM_SIMILARITY_MODEL:
										'/create-item-similarity-model'
								}
							},
							SETTINGS_SALESFORCE_ADD: `/data-source/${SALESFORCE}`,
							SETTINGS_SALESFORCE_CONFIGURATION_STATUS: `/data-source/:id/${SALESFORCE}/configuration-status`,
							SETTINGS_SALESFORCE_FIELD_MAPPING: {
								path: `/data-source/:id/${SALESFORCE}`,
								routes: {
									SETTINGS_SALESFORCE_FIELD_MAPPING_ACCOUNTS:
										'/field-mapping/accounts',
									SETTINGS_SALESFORCE_FIELD_MAPPING_INDIVIDUALS:
										'/field-mapping/individuals'
								}
							},
							SETTINGS_USAGE: '/usage',
							SETTINGS_USERS: {
								path: '/users',
								routes: {
									SETTINGS_USERS_REQUESTS: '/requests'
								}
							},
							SETTINGS_WORKSPACE: '/workspace'
						}
					}
				}
			}
		}
	},
	WORKSPACES: '/workspaces'
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
		} else {
			routes[key] = prefix + pathOrConfig.path;

			buildRoutes(pathOrConfig.routes, routes, routes[key]);
		}
	}

	return routes;
}

const getCompiledRoute = memoize(pathToRegexp.compile);

export function toRoute(route: string, options?: {[key: string]: any}) {
	return getCompiledRoute(route)(options);
}

const ROUTE_TO_TYPE_MAP = {
	[ACCOUNTS]: EntityTypes.Account,
	[ASSETS]: EntityTypes.Asset,
	[INDIVIDUALS]: EntityTypes.Individual,
	[PAGES]: EntityTypes.Page,
	[SEGMENTS]: EntityTypes.IndividualsSegment
};

const PROVIDER_ROUTE_TO_TYPE_MAP = {
	[CSV]: DataSourceTypes.Csv,
	[LIFERAY]: DataSourceTypes.Liferay
};

const TYPE_TO_ROUTE_MAP = {
	...invert(ROUTE_TO_TYPE_MAP)
};

export const assetTypePaths = {
	blog: Routes.ASSETS_BLOGS_OVERVIEW,
	custom: Routes.ASSETS_CUSTOM_DASHBOARD,
	document: Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW,
	form: Routes.ASSETS_FORMS_OVERVIEW,
	journal: Routes.ASSETS_WEB_CONTENT_OVERVIEW
};

export const toAssetOverviewRoute = (assetType, routeParams, query) => {
	let route = '';

	if (assetType === 'blog') {
		route = toRoute(assetTypePaths[assetType], {
			...routeParams,
			assetType: 'blogs'
		});
	} else {
		route = toRoute(assetTypePaths[assetType], routeParams);
	}

	return !isEmpty(query) ? setUriQueryValues(query, route) : route;
};

export function getType(routeName) {
	return ROUTE_TO_TYPE_MAP[routeName];
}

export function getRouteName(type) {
	return TYPE_TO_ROUTE_MAP[type];
}

export function getDataSourceType(routeName) {
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
export function getMatchedRoute(routes, pathname = location.pathname) {
	const matchedRoute = routes.find(({exact = true, route}) =>
		matchPath(pathname, {exact, path: route})
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
export function setUriFilterValues(filterBy, href = window.location.href) {
	const uri = createURL(href);

	filterBy.forEach((valueISet, key) => {
		addParam(uri, key, valueISet.filter(Boolean).toArray());
	});

	return `${uri.pathname}${uri.search}`;
}

export function setUriQueryValue(href, name, value) {
	const uri = createURL(href);

	addParam(uri, name, value);

	return `${uri.pathname}${uri.search}`;
}

export function setUriQueryValues(values, href = window.location.href) {
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
export function removeUriQueryParam(href, ...names) {
	const uri = createURL(href);

	for (const name of names) {
		uri.searchParams.delete(name);
	}

	return `${uri.pathname}${uri.search}`;
}

export function removePageParam(newPath, href = window.location.href) {
	const uri = createURL(href);

	if (newPath) {
		uri.pathname = newPath;
	}

	uri.searchParams.delete('page');

	return `${uri.pathname}${uri.search}`;
}

export function resetPaginationParams(newPath, href = window.location.href) {
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
