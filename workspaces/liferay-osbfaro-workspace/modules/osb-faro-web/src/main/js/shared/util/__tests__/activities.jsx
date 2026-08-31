import * as data from 'test/data';
import {
	buildLegendItems,
	formatEvents,
	formatGroupingTime,
	formatSessions,
	getActivityLabel,
	getEventCampaign,
	getSafeRangeKey,
	groupEventsByPage,
	groupSessionsByDay,
	isWebhookUserAgent
} from '../activities';

describe('activities', () => {
	describe('buildLegendItems', () => {
		it('should return an array formatted for use as items in ChangeLegend', () => {
			const mockChangeData = {
				activityChange: 20,
				activityCount: 10
			};

			const result = buildLegendItems(mockChangeData);

			expect(Array.isArray(result)).toBe(true);
			expect(result.length).toBe(1);
			expect(result[0].change).toBe(20);
			expect(result[0].id).toBe('activities');
			expect(result[0].secondaryInfo).toContain('30');
			expect(result[0].title).toContain('10');
		});
	});

	describe('formatGroupingTime', () => {
		it('should format grouping time', () => {
			const result = formatGroupingTime(data.getTimestamp());

			expect(typeof result).toBe('string');
			expect(result.length).toBeGreaterThan(0);
		});
	});

	describe('isWebhookUserAgent', () => {
		it('returns true when the user agent names a webhook', () => {
			expect(isWebhookUserAgent('HubSpot Webhook')).toBe(true);
			expect(isWebhookUserAgent('Marketo Webhook')).toBe(true);
		});

		it('returns false for a regular browser user agent, or when absent', () => {
			expect(isWebhookUserAgent('Mozilla/5.0')).toBe(false);
			expect(isWebhookUserAgent(undefined)).toBe(false);
		});
	});

	describe('formatEvents', () => {
		it('carries a campaign onto an event that is not page bound', () => {
			const [withCampaign, withoutCampaign] = formatEvents([
				{
					applicationId: 'CustomEvent',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'eventName',
					utmCampaignId: '7013a000002QwErtAAG',
					utmCampaignName: 'Spring Compactor Promo 2026'
				},
				{
					applicationId: 'CustomEvent',
					createDate: '2026-07-16T10:01:00.000Z',
					name: 'eventName'
				}
			]);

			expect(withCampaign.campaign).toEqual({
				campaignId: '7013a000002QwErtAAG',
				campaignName: 'Spring Compactor Promo 2026'
			});
			expect(withoutCampaign.campaign).toBeUndefined();
		});

		it('should decode canonicalUrl into subtitle for DXP events', () => {
			const result = formatEvents([
				{
					applicationId: 'Page',
					assetTitle: 'this is a page title',
					canonicalUrl:
						'http://localhost:7400/%e6%96%b0%e3%81%97%e3%81%84%e3%82%b5%e3%82%a4%e3%83%88',
					eventId: 'pageViewed',
					name: 'eventName',
					pageDescription: 'this is a page description',
					pageTitle: 'this is a page title',
					referrer:
						'http://localhost:7400/%e6%96%b0%e3%81%97%e3%81%84%e3%82%b5%e3%82%a4%e3%83%88',
					url: 'http://localhost:7400/%e6%96%b0%e3%81%97%e3%81%84%e3%82%b5%e3%82%a4%e3%83%88'
				}
			]);

			expect(result).toMatchObject([
				{
					attributes: {
						applicationId: 'Page',
						eventId: 'pageViewed'
					},
					description: 'this is a page title',
					subtitle: 'http://localhost:7400/新しいサイト',
					title: 'eventName'
				}
			]);
		});

		it('should not set subtitle for webhook events regardless of provider', () => {
			const hubSpotResult = formatEvents(
				[
					{
						applicationId: 'HubSpot',
						assetTitle: null,
						canonicalUrl: 'https://hubspot.com',
						eventId: 'emailView',
						name: 'emailView'
					}
				],
				'HubSpot Webhook'
			);

			const marketoResult = formatEvents(
				[
					{
						applicationId: 'Marketo',
						assetTitle: null,
						canonicalUrl: 'https://marketo.com',
						eventId: 'emailView',
						name: 'emailView'
					}
				],
				'Marketo Webhook'
			);

			expect(hubSpotResult[0].subtitle).toBeUndefined();
			expect(marketoResult[0].subtitle).toBeUndefined();
		});

		it('should transform properties array into an object in attributes', () => {
			const result = formatEvents([
				{
					applicationId: 'HubSpot',
					eventId: 'formSubmit',
					name: 'formSubmit',
					properties: [
						{name: 'formId', value: 'abc123'},
						{name: 'pageUrl', value: 'https://hubspot.com/landing'}
					]
				}
			]);

			expect(result[0].attributes.properties).toEqual({
				formId: 'abc123',
				pageUrl: 'https://hubspot.com/landing'
			});
		});

		it('should include eventDate in attributes only when present', () => {
			const withDate = formatEvents([
				{
					applicationId: 'Page',
					eventDate: '2026-05-07T19:57:21.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed'
				}
			]);

			const withoutDate = formatEvents([
				{
					applicationId: 'Page',
					eventId: 'pageViewed',
					name: 'pageViewed'
				}
			]);

			expect(withDate[0].attributes.eventDate).toBe(
				'2026-05-07T19:57:21.000Z'
			);
			expect(withoutDate[0].attributes).not.toHaveProperty('eventDate');
		});
	});

	describe('getEventCampaign', () => {
		it('reads a resolved touch as its campaign id and name', () => {
			expect(
				getEventCampaign({
					utmCampaignId: '7013a000002QwErtAAG',
					utmCampaignName: 'Spring Compactor Promo 2026'
				})
			).toEqual({
				campaignId: '7013a000002QwErtAAG',
				campaignName: 'Spring Compactor Promo 2026'
			});
		});

		it('keeps the raw id of a touch that resolved to no campaign', () => {
			expect(
				getEventCampaign({
					utmCampaignId: '7013a000002XyZbAAK',
					utmCampaignName: null
				})
			).toEqual({
				campaignId: '7013a000002XyZbAAK',
				campaignName: null
			});
		});

		it('reads an event that carried no campaign identity as no campaign', () => {
			expect(
				getEventCampaign({
					utmCampaignId: null,
					utmCampaignName: null
				})
			).toBeUndefined();

			expect(getEventCampaign({})).toBeUndefined();
		});
	});

	describe('groupEventsByPage', () => {
		it('groups events that share a page group key into a single page entry', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home',
					pageTitle: 'Home'
				},
				{
					applicationId: 'Form',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:01:00.000Z',
					name: 'formSubmitted',
					pageGroupId: 'https://liferay.com/home'
				}
			]);

			expect(result).toHaveLength(1);
			expect(result[0]).toMatchObject({
				pageGroup: true,
				subtitle: 'https://liferay.com/home',
				title: 'Home',
				totalEvents: 2
			});
			expect(result[0].nestedItems).toHaveLength(2);
		});

		it('carries the campaign of the touch that led to the page onto the group', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home',
					utmCampaignId: '7013a000002QwErtAAG',
					utmCampaignName: 'Spring Compactor Promo 2026'
				}
			]);

			expect(result[0].campaign).toEqual({
				campaignId: '7013a000002QwErtAAG',
				campaignName: 'Spring Compactor Promo 2026'
			});
		});

		it('keeps an unresolved campaign on the group rather than dropping it', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home',
					utmCampaignId: '7013a000002XyZbAAK',
					utmCampaignName: null
				}
			]);

			expect(result[0].campaign).toEqual({
				campaignId: '7013a000002XyZbAAK',
				campaignName: null
			});
		});

		it('leaves a page nobody reached through a campaign without one', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				}
			]);

			expect(result[0].campaign).toBeUndefined();
		});

		it('does not repeat the group\'s campaign on its own nested events', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home',
					utmCampaignId: '7013a000002QwErtAAG',
					utmCampaignName: 'Spring Compactor Promo 2026'
				}
			]);

			expect(result[0].nestedItems[0].campaign).toBeUndefined();
		});

		it('does not repeat the page subtitle on the group\'s own nested events', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				}
			]);

			expect(result[0].nestedItems[0].subtitle).toBeUndefined();
		});

		it('leaves an event the API gave no page group key ungrouped', () => {
			const result = groupEventsByPage(
				[
					{
						applicationId: 'HubSpot',
						canonicalUrl: 'https://hubspot.com',
						createDate: '2026-07-16T10:00:00.000Z',
						name: 'emailViewed'
					}
				],
				'HubSpot Webhook'
			);

			expect(result).toHaveLength(1);
			expect(result[0].pageGroup).toBeUndefined();
			expect(result[0].title).toBe('emailViewed');
		});

		it('leaves a DXP event with no page group key ungrouped', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: null,
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'somethingHappened',
					pageGroupId: null,
					url: null
				}
			]);

			expect(result).toHaveLength(1);
			expect(result[0].pageGroup).toBeUndefined();
		});

		it('orders groups and loose events by their most recent event, newest first', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/older-page',
					createDate: '2026-07-16T09:00:00.000Z',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/older-page',
					pageTitle: 'Older Page'
				},
				{
					applicationId: 'HubSpot',
					canonicalUrl: 'https://hubspot.com',
					createDate: '2026-07-16T11:00:00.000Z',
					name: 'emailViewed'
				},
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/newer-page',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/newer-page',
					pageTitle: 'Newer Page'
				}
			]);

			expect(result.map((item) => item.title)).toEqual([
				'emailViewed',
				'Newer Page',
				'Older Page'
			]);
		});
	});

	describe('groupSessionsByDay', () => {
		it('groups sessions by the day they started and sorts each day newest first', () => {
			const result = groupSessionsByDay([
				{createDate: '2026-07-16T08:00:00.000Z'},
				{createDate: '2026-07-16T10:00:00.000Z'},
				{createDate: '2026-07-15T10:00:00.000Z'}
			]);

			expect(result).toHaveLength(2);
			expect(result[0].daySessions.map((session) => session.createDate)).toEqual([
				'2026-07-16T10:00:00.000Z',
				'2026-07-16T08:00:00.000Z'
			]);
		});

		it('orders days most-recent first and sums each day\'s event totals in the header', () => {
			const result = groupSessionsByDay([
				{createDate: '2026-07-15T10:00:00.000Z', events: [{}, {}]},
				{createDate: '2026-07-16T10:00:00.000Z', events: [{}]}
			]);

			expect(result[0].header.totalEvents).toBe(1);
			expect(result[1].header.totalEvents).toBe(2);
			expect(result[0].header.header).toBe(true);
		});
	});

	describe('formatSessions', () => {
		it('should format sessions', () => {
			const result = formatSessions([
				data.mockSession(2, {}, {assetType: 'foo'})
			]);

			expect(Array.isArray(result)).toBe(true);
			expect(result.length).toBeGreaterThan(0);

			const header = result[0];
			expect(header.header).toBe(true);
			expect(typeof header.title).toBe('string');
			expect(typeof header.totalEvents).toBe('number');

			const session = result[1];
			expect(session.session).toBe(true);
			expect(session).toHaveProperty('attributes');
			expect(session).toHaveProperty('device');
			expect(session).toHaveProperty('nestedItems');
			expect(Array.isArray(session.nestedItems)).toBe(true);
		});

		it('does not carry a duration, since it is not developed yet', () => {
			const [, session] = formatSessions([data.mockSession(0)]);

			expect(session.duration).toBeUndefined();
		});

		it('marks the session the individual became known in', () => {
			const [, session] = formatSessions([
				data.mockSession(0, {becameKnown: true})
			]);

			expect(session.becameKnown).toBe(true);
		});

		it('leaves the sessions unmarked when the individual is still anonymous', () => {
			const [, session] = formatSessions([
				data.mockSession(0, {becameKnown: false})
			]);

			expect(session.becameKnown).toBe(false);
		});
	});

	describe('getActivityLabel', () => {
		it('should get singular label', () => {
			const result = getActivityLabel(1);

			expect(Array.isArray(result)).toBe(true);
			expect(result.length).toBe(2);
			expect(result[0]).toContain('Event');
		});

		it('should plural label', () => {
			const result = getActivityLabel(2);

			expect(Array.isArray(result)).toBe(true);
			expect(result.length).toBe(2);
			expect(result[0]).toContain('Events');
		});
	});

	describe('getSafeRangeKey', () => {
		it('should return the rangeKey when it is different of CUSTOM', () => {
			const rangeKey = getSafeRangeKey('30');

			expect(rangeKey).toBe('30');
		});

		it('should return null when it is CUSTOM', () => {
			const rangeKey = getSafeRangeKey('CUSTOM');

			expect(rangeKey).toBe(null);
		});
	});
});
