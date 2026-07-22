import {
	ACCOUNTS,
	buildRoutes,
	getDataSourceType,
	getMatchedRoute,
	getRouteName,
	getType,
	INDIVIDUALS,
	LIFERAY,
	removePageParam,
	removeUriQueryParam,
	resetPaginationParams,
	Routes,
	SEGMENTS,
	setUriFilterValues,
	setUriQueryValue,
	setUriQueryValues,
	toRoute,
} from '../router';
import {DataSourceTypes, EntityTypes} from '../constants';
import {Map, Set} from 'immutable';

describe('setUriFilterValues', () => {
	it('should add filter queries to url and return as a string', () => {
		const mockFilterBy = new Map({
			biz: new Set(['buz']),
			foo: new Set(['bar', 'baz']),
		});

		const url = 'http://www.liferay.com';

		expect(setUriFilterValues(mockFilterBy, url)).toBe(
			'/?biz=buz&foo=bar%2Cbaz'
		);
	});
});

describe('setUriQueryValue', () => {
	it('should add query to url and return as a string', () => {
		const url = 'http://www.liferay.com';

		expect(setUriQueryValue(url, 'foo', 'bar')).toBe('/?foo=bar');

		expect(setUriQueryValue(url, 'foo')).toBe('/');
	});
});

describe('setUriQueryValues', () => {
	it('should add multiple queries to url and return as a string', () => {
		const url = 'http://www.liferay.com';

		expect(setUriQueryValues({baz: 'qux', foo: 'bar'}, url)).toBe(
			'/?baz=qux&foo=bar'
		);
	});
});

describe('getType', () => {
	it('should return type for a given route name', () => {
		expect(getType(ACCOUNTS)).toBe(EntityTypes.Account);
	});
});

describe('getDataSourceType', () => {
	it('should return a data-source type for a given route name', () => {
		expect(getDataSourceType(LIFERAY)).toBe(DataSourceTypes.Liferay);
	});
});

describe('getMatchedRoute', () => {
	it('should return the matched route', () => {
		expect(
			getMatchedRoute(
				[{route: '/foo/:id'}, {route: '/bar/:id'}],
				'/foo/123'
			)
		).toBe('/foo/:id');
	});
});

describe('getRouteName', () => {
	it('should return route name for a given type', () => {
		expect(getRouteName(EntityTypes.Account)).toBe(ACCOUNTS);
	});

	it('should return route name for the segment types', () => {
		expect(getRouteName(EntityTypes.IndividualsSegment)).toBe(SEGMENTS);
	});
});

describe('removePageParam', () => {
	it('should remove page query string', () => {
		const url = 'http://www.liferay.com/';

		expect(removePageParam(null, `${url}?page=3`)).toBe('/');
	});

	it('should remove page query string and set new path', () => {
		const url = 'http://www.liferay.com/';

		expect(removePageParam('/bar', `${url}foo?page=3`)).toBe('/bar');
	});
});

describe('removeUriQueryParam', () => {
	it('should remove uri query param', () => {
		const href =
			'http://localhost:3000/project/33551/touchpoints/?sortField=views';
		const name = 'sortField';
		const removeURI = removeUriQueryParam(href, name);

		expect(removeURI).toEqual('/project/33551/touchpoints/');
	});
});

describe('resetPaginationParams', () => {
	it('should reset the pagination parameters to the default value', () => {
		const url = 'http://www.liferay.com/';

		expect(
			resetPaginationParams(null, `${url}?page=3&orderBy=desc&query=test`)
		).toBe('/?page=1&orderBy=asc&query=');
	});
});

describe('setUriFilterValues', () => {
	it('should set the uri filter params from the filterBy Map', () => {
		const url = 'http://www.liferay.com';

		const mockFilterBy = new Map({
			devices: new Set(['desktop', 'mobile']),
			foo: new Set(['bar']),
		});

		expect(setUriFilterValues(mockFilterBy, url)).toBe(
			'/?devices=desktop%2Cmobile&foo=bar'
		);
	});
});

describe('Routes', () => {
	it('should match Routes snapshot', () => {
		expect(Routes).toBeDefined();
		expect(typeof Routes).toBe('object');
		expect(Routes.BASE).toBe('/');
		expect(Routes.WORKSPACE_WITH_ID).toBe('/workspace/:groupId');
		expect(Routes.WORKSPACE_ADD).toBe('/workspace/add');
		expect(Routes.SETTINGS_DATA_SOURCE).toBe(
			'/workspace/:groupId/settings/data-source/:id'
		);
		expect(Routes.CONTACTS_ENTITY).toBe(
			'/workspace/:groupId/:channelId?/contacts/:type/:id'
		);
		expect(Object.keys(Routes).length).toBeGreaterThan(10);
	});
});

describe('toRoute', () => {
	it('should create a url for specific options', () => {
		const id = 123;

		const groupId = 456;

		expect(
			toRoute(Routes.CONTACTS_ENTITY, {groupId, id, type: INDIVIDUALS})
		).toBe(`/workspace/${groupId}/contacts/${INDIVIDUALS}/${id}`);

		expect(
			toRoute(Routes.CONTACTS_ENTITY, {groupId, id, type: SEGMENTS})
		).toBe(`/workspace/${groupId}/contacts/${SEGMENTS}/${id}`);
	});
});

describe('buildRoutes', () => {
	it('should return an object with keys that map to route strings', () => {
		const routes = buildRoutes({
			BAR: '/bar',
			FOO: '/foo',
		});

		expect(routes).toMatchObject({
			BAR: '/bar',
			FOO: '/foo',
		});
	});

	it('should allow for nesting of routes using route objects', () => {
		const routes = buildRoutes({
			BAR: {
				path: '/bar',
				routes: {
					BAZ: '/baz',
					BIZ: '/biz',
					FIZZ: {
						path: '/fizz',
						routes: {
							BUZZ: '/buzz',
						},
					},
				},
			},
			FOO: '/foo',
		});

		expect(routes).toMatchObject({
			BAR: '/bar',
			BAZ: '/bar/baz',
			BIZ: '/bar/biz',
			BUZZ: '/bar/fizz/buzz',
			FIZZ: '/bar/fizz',
			FOO: '/foo',
		});
	});
});
