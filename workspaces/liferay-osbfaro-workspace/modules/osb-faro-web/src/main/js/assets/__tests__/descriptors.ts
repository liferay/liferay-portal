import {
	ASSET_DESCRIPTORS,
	getAssetDescriptorByGraphQLType,
	getAssetDescriptorByRESTType,
	getAssetDescriptorBySlug,
	toAssetOverviewRoute,
} from 'assets/descriptors';
import {AssetTypes} from 'shared/util/constants';
import {Name} from 'shared/components/audience-report/types';

describe('ASSET_DESCRIPTORS', () => {
	it('should cover every asset type that has a dashboard', () => {
		expect(ASSET_DESCRIPTORS.map(({slug}) => slug)).toEqual([
			'blogs',
			'documents-and-media',
			'forms',
			'web-content',
			'object-entry',
		]);
	});

	it('should give every descriptor the three dashboard routes', () => {
		ASSET_DESCRIPTORS.forEach(({assetType, graphQLType, routes, slug}) => {
			expect(Object.values(AssetTypes)).toContain(assetType);
			expect(Object.values(Name)).toContain(graphQLType);

			expect(routes.accounts).toContain(`/assets/${slug}/`);
			expect(routes.knownIndividuals).toContain(`/assets/${slug}/`);
			expect(routes.overview).toContain(`/assets/${slug}/`);
		});
	});

	it('should not reuse a graphQL or REST type across descriptors', () => {
		const graphQLTypes = ASSET_DESCRIPTORS.map(
			({graphQLType}) => graphQLType
		);

		const restTypes = ASSET_DESCRIPTORS.map(
			({restType}) => restType
		).filter(Boolean);

		expect(new Set(graphQLTypes).size).toBe(graphQLTypes.length);
		expect(new Set(restTypes).size).toBe(restTypes.length);
	});
});

describe('getAssetDescriptorByGraphQLType', () => {
	it('should resolve every type the assets query returns', () => {
		expect(getAssetDescriptorByGraphQLType('blog').slug).toBe('blogs');
		expect(getAssetDescriptorByGraphQLType('document').slug).toBe(
			'documents-and-media'
		);
		expect(getAssetDescriptorByGraphQLType('form').slug).toBe('forms');
		expect(getAssetDescriptorByGraphQLType('journal').slug).toBe(
			'web-content'
		);
	});

	it('should fall back to object entry for an unknown type', () => {
		expect(getAssetDescriptorByGraphQLType('custom').slug).toBe(
			'object-entry'
		);
		expect(getAssetDescriptorByGraphQLType(undefined).slug).toBe(
			'object-entry'
		);
	});
});

describe('getAssetDescriptorByRESTType', () => {

	// The asset summary endpoint spells web content "webContent" where the
	// GraphQL API spells it "journal", so the two lookups are not
	// interchangeable.

	it('should resolve every type the asset summary returns', () => {
		expect(getAssetDescriptorByRESTType('blog').slug).toBe('blogs');
		expect(getAssetDescriptorByRESTType('document').slug).toBe(
			'documents-and-media'
		);
		expect(getAssetDescriptorByRESTType('form').slug).toBe('forms');
		expect(getAssetDescriptorByRESTType('webContent').slug).toBe(
			'web-content'
		);
	});

	it('should fall back to object entry for an unknown type', () => {
		expect(getAssetDescriptorByRESTType('folder').slug).toBe(
			'object-entry'
		);
		expect(getAssetDescriptorByRESTType('journal').slug).toBe(
			'object-entry'
		);
		expect(getAssetDescriptorByRESTType(undefined).slug).toBe(
			'object-entry'
		);
	});
});

describe('getAssetDescriptorBySlug', () => {
	it('should resolve a slug that names a dashboard', () => {
		expect(getAssetDescriptorBySlug('web-content')?.graphQLType).toBe(
			Name.Journal
		);
	});

	it('should return nothing for a slug that names no dashboard', () => {
		expect(getAssetDescriptorBySlug('custom')).toBeUndefined();
	});
});

describe('toAssetOverviewRoute', () => {
	const routeParams = {
		assetId: 123,
		channelId: 456,
		groupId: 789,
		title: 'Foo',
		touchpoint: 'Any',
	};

	it('should build the overview url for every mapped type', () => {
		expect(toAssetOverviewRoute('blog', routeParams, {})).toBe(
			'/workspace/789/456/assets/blogs/123/page/Any/Foo'
		);
		expect(toAssetOverviewRoute('journal', routeParams, {})).toBe(
			'/workspace/789/456/assets/web-content/123/page/Any/Foo'
		);
	});

	it('should fall back to the object entry overview for an unknown type', () => {
		expect(toAssetOverviewRoute('custom', routeParams, {})).toBe(
			'/workspace/789/456/assets/object-entry/123/page/Any/Foo'
		);
	});

	it('should append the query when there is one', () => {
		expect(
			toAssetOverviewRoute('blog', routeParams, {rangeKey: 7})
		).toContain('rangeKey=7');
	});
});
