import {matchRoutes} from 'react-router-dom';

/**
 * Mirrors the two asset routes declared in `shared/pages/AppSidebarRoutes`.
 * The dashboard path carries a dynamic `:assetType` where five literal
 * branches used to sit, so it now overlaps the list's splat and only React
 * Router's ranking keeps them apart. Keep these in step with that file.
 */
const ASSET_ROUTES = [
	{
		id: 'dashboard',
		path: ':channelId?/assets/:assetType/:assetId/:tabId/:touchpoint/:title?/:type?',
	},
	{id: 'list', path: ':channelId?/assets/*'},
];

const match = (pathname: string) => {
	const matches = matchRoutes(ASSET_ROUTES, pathname);

	return {
		id: matches?.[0]?.route.id,
		params: matches?.[0]?.params ?? {},
	};
};

describe('asset routes', () => {

	// These five URLs were each served by a route of their own. They have to
	// keep resolving to the dashboard, unchanged, or every bookmark breaks.

	it.each([
		['blogs'],
		['documents-and-media'],
		['forms'],
		['object-entry'],
		['web-content'],
	])('routes the %s dashboard URL to the dashboard', (slug) => {
		const {id, params} = match(`/456/assets/${slug}/123/page/Any/My+Title`);

		expect(id).toBe('dashboard');
		expect(params.assetType).toBe(slug);
		expect(params.assetId).toBe('123');
		expect(params.tabId).toBe('page');
	});

	it.each([['accounts'], ['known-individuals'], ['page']])(
		'routes the %s tab to the dashboard',
		(tabId) => {
			const {id, params} = match(
				`/456/assets/blogs/123/${tabId}/Any/My+Title`
			);

			expect(id).toBe('dashboard');
			expect(params.tabId).toBe(tabId);
		}
	);

	it('leaves the asset list on its own route', () => {
		expect(match('/456/assets').id).toBe('list');
		expect(match('/assets').id).toBe('list');
	});

	// A short URL under /assets has nowhere near enough segments for the
	// dashboard, so it belongs to the list.

	it('leaves a short URL under assets on the list route', () => {
		expect(match('/456/assets/blogs').id).toBe('list');
	});

	// The dashboard route matches any slug; turning away the ones that name no
	// asset type is the dashboard component's job, not the router's.

	it('routes an unknown slug to the dashboard rather than the list', () => {
		const {id, params} = match('/456/assets/banana/123/page/Any');

		expect(id).toBe('dashboard');
		expect(params.assetType).toBe('banana');
	});
});
