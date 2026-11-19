import {config, initializeConfig} from '../config';

describe('initializeConfig', () => {
	it.each([
		['/segments', 'segmentsURL'],
		['/edit-segment', 'editSegmentURL'],
		['/data-source-onboarding', 'dataSourceOnboardingURL'],
	])('exposes the URL of the %s page as config.%s', (friendlyURL, key) => {
		initializeConfig({
			pageURLs: {[friendlyURL]: `/web/my-workspace${friendlyURL}`},
		});

		expect(config[key as `${string}URL`]).toBe(
			`/web/my-workspace${friendlyURL}`
		);
	});

	it('keeps the params that have a value', () => {
		initializeConfig({params: {id: '123', query: '', tabId: undefined}});

		expect(config.params).toEqual({id: '123'});
	});

	it('keeps the values that the renderer passes', () => {
		initializeConfig({
			channelId: '321',
			friendlyURL: '/segment',
			groupId: '23',
			navigationItems: [{label: 'Overview'}],
			signOutURL: '/c/portal/logout',
		});

		expect(config.channelId).toBe('321');
		expect(config.friendlyURL).toBe('/segment');
		expect(config.groupId).toBe('23');
		expect(config.navigationItems).toEqual([{label: 'Overview'}]);
		expect(config.signOutURL).toBe('/c/portal/logout');
	});

	it('defaults the values that the renderer does not pass', () => {
		initializeConfig({});

		expect(config.channelId).toBe('');
		expect(config.groupId).toBe('');
		expect(config.navigationItems).toBeNull();
		expect(config.params).toEqual({});
		expect(config.sidebarNavigationItems).toBeNull();
	});
});
