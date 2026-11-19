import {
	buildRoutes,
	getDataSourceType,
	getMatchedRoute,
	getRouteName,
	getType,
	LIFERAY,
	Routes,
	toRoute,
} from '../router';
import {
	ACCOUNTS,
	INDIVIDUALS,
	SEGMENTS,
	DataSourceTypes,
	EntityTypes,
} from '../constants';

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
