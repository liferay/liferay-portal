import * as data from 'test/data';
import {
	buildLegendItems,
	buildCampaignUrls,
	buildTouchIndividualUrls,
	formatEvents,
	formatGroupingTime,
	formatSessions,
	getActivityLabel,
	getEventCampaign,
	getSafeRangeKey,
	groupEventsByPage,
	groupSessionsByDay,
	isWebhookUserAgent,
	mapEventMetricToActivityHistory,
	mergeCampaignDays,
	toDayKey
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
					campaignId: '7013a000002QwErtAAG',
					campaignName: 'Spring Compactor Promo 2026'
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

		it('includes the raw experience id and name in attributes when the event carries one', () => {
			const result = formatEvents([
				{
					applicationId: 'Page',
					createDate: '2026-07-16T10:00:00.000Z',
					experienceId: '39201',
					experienceName: 'Q3 Promo Experience',
					name: 'pageViewed'
				}
			]);

			expect(result[0].attributes.experienceId).toBe('39201');
			expect(result[0].attributes.experienceName).toBe(
				'Q3 Promo Experience'
			);
		});

		it('omits the experience attributes when the event carries none', () => {
			const result = formatEvents([
				{
					applicationId: 'Page',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'pageViewed'
				}
			]);

			expect(result[0].attributes).not.toHaveProperty('experienceId');
			expect(result[0].attributes).not.toHaveProperty('experienceName');
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
					campaignId: '7013a000002QwErtAAG',
					campaignName: 'Spring Compactor Promo 2026'
				})
			).toEqual({
				campaignId: '7013a000002QwErtAAG',
				campaignName: 'Spring Compactor Promo 2026'
			});
		});

		it('keeps the raw id of a touch that resolved to no campaign', () => {
			expect(
				getEventCampaign({
					campaignId: '7013a000002XyZbAAK',
					campaignName: null
				})
			).toEqual({
				campaignId: '7013a000002XyZbAAK',
				campaignName: null
			});
		});

		it('reads an event that carried no campaign identity as no campaign', () => {
			expect(
				getEventCampaign({
					campaignId: null,
					campaignName: null
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
					campaignId: '7013a000002QwErtAAG',
					campaignName: 'Spring Compactor Promo 2026'
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
					campaignId: '7013a000002XyZbAAK',
					campaignName: null
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
					campaignId: '7013a000002QwErtAAG',
					campaignName: 'Spring Compactor Promo 2026'
				}
			]);

			expect(result[0].nestedItems[0].campaign).toBeUndefined();
		});

		it('adds the experience a page view was served by onto the group', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					experienceId: '39201',
					experienceName: 'Q3 Promo Experience',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				}
			]);

			expect(result[0].experienceNames).toEqual(['Q3 Promo Experience']);
		});

		it('falls back to the raw id when the group\'s experience has no name', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					experienceId: '39201',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				}
			]);

			expect(result[0].experienceNames).toEqual(['39201']);
		});

		it('excludes the default experience from the group', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					experienceId: 'DEFAULT',
					experienceName: 'Default',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				}
			]);

			expect(result[0].experienceNames).toBeUndefined();
		});

		it('leaves a page no view specified an experience for without one', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				}
			]);

			expect(result[0].experienceNames).toBeUndefined();
		});

		it('collapses the same experience seen on more than one view of the group into a single name', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					experienceId: '39201',
					experienceName: 'Q3 Promo Experience',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				},
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:05:00.000Z',
					experienceId: '39201',
					experienceName: 'Q3 Promo Experience',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				}
			]);

			expect(result[0].experienceNames).toEqual(['Q3 Promo Experience']);
		});

		it('keeps every distinct experience a group\'s views were served by, in the order they were seen', () => {
			const result = groupEventsByPage([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:00:00.000Z',
					experienceId: '39201',
					experienceName: 'Q3 Promo Experience',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				},
				{
					applicationId: 'Page',
					canonicalUrl: 'https://liferay.com/home',
					createDate: '2026-07-16T10:05:00.000Z',
					experienceId: '39202',
					experienceName: 'Winter Sale Experience',
					name: 'pageViewed',
					pageGroupId: 'https://liferay.com/home'
				}
			]);

			expect(result[0].experienceNames).toEqual([
				'Q3 Promo Experience',
				'Winter Sale Experience'
			]);
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

			const [{header, items}] = result;
			expect(header.header).toBe(true);
			expect(typeof header.title).toBe('string');
			expect(typeof header.totalEvents).toBe('number');

			const [session] = items;
			expect(session.session).toBe(true);
			expect(session).toHaveProperty('attributes');
			expect(session).toHaveProperty('device');
			expect(session).toHaveProperty('nestedItems');
			expect(Array.isArray(session.nestedItems)).toBe(true);
		});

		it('does not carry a duration, since it is not developed yet', () => {
			const [{items: [session]}] = formatSessions([data.mockSession(0)]);

			expect(session.duration).toBeUndefined();
		});

		it('marks the session the individual became known in', () => {
			const [{items: [session]}] = formatSessions([
				data.mockSession(0, {becameKnown: true})
			]);

			expect(session.becameKnown).toBe(true);
		});

		it('leaves the sessions unmarked when the individual is still anonymous', () => {
			const [{items: [session]}] = formatSessions([
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

	describe('mapEventMetricToActivityHistory', () => {
		const buildEventMetric = (extra = {}) => ({
			totalEventsMetric: {
				histogram: {
					metrics: [
						{key: '2026-09-07T00:00:00Z', value: 7},
						{key: '2026-09-08T00:00:00Z', value: 4}
					]
				}
			},
			totalSessionsMetric: {
				histogram: {metrics: [{value: 3}, {value: 2}]}
			},
			...extra
		});

		it('carries the campaign activities of each interval', () => {
			const points = mapEventMetricToActivityHistory(
				buildEventMetric({
					totalCampaignActivitiesMetric: {
						histogram: {metrics: [{value: 5}, {value: 1}]}
					}
				})
			);

			expect(points.map(({totalCampaignResponses}) => totalCampaignResponses)).toEqual([5, 1]);
		});

		it('leaves the campaign activities undefined while the metric is absent', () => {
			const points = mapEventMetricToActivityHistory(buildEventMetric());

			expect(points[0].totalCampaignResponses).toBeUndefined();
			expect(points[0].totalEvents).toBe(7);
			expect(points[0].totalSessions).toBe(3);
		});
	});

	describe('mergeCampaignDays', () => {
		const buildDay = (date) => ({
			date,
			header: {header: true, title: date, totalEvents: 1},
			items: [{individual: true, individualName: 'Ada Lovelace'}]
		});

		const campaignDay = {campaigns: [{campaignId: 'c1'}]};

		it('adds a day that only campaigns reached, newest first', () => {
			const days = mergeCampaignDays(
				[buildDay('2026-07-15T00:00:00Z')],
				{'2026-07-16': campaignDay}
			);

			expect(days.map(({date}) => toDayKey(date))).toEqual([
				'2026-07-16',
				'2026-07-15'
			]);
			expect(days[0].items).toEqual([]);
			expect(days[0].header.totalEvents).toBeUndefined();
		});

		it('keys an added day by its calendar date, so its header cannot drift', () => {
			const [day] = mergeCampaignDays([], {
				'2026-07-16': campaignDay
			});

			expect(day.date).toBe('2026-07-16');
			expect(day.header.title).toBe(formatGroupingTime(day.date));
		});

		it('titles an added day in the project time zone it was given', () => {
			const [day] = mergeCampaignDays(
				[],
				{'2026-07-16': campaignDay},
				{timeZoneId: 'Asia/Tokyo'}
			);

			expect(day.header.title).toBe(
				formatGroupingTime('2026-07-16', 'Asia/Tokyo')
			);
		});

		it('does not repeat a day the sessions already cover', () => {
			const days = mergeCampaignDays(
				[buildDay('2026-07-16T10:00:00Z')],
				{'2026-07-16': campaignDay}
			);

			expect(days).toHaveLength(1);
			expect(days[0].items).toHaveLength(1);
		});

		it('ignores a day whose campaigns came back empty', () => {
			const days = mergeCampaignDays([buildDay('2026-07-16T10:00:00Z')], {
				'2026-07-14': {campaigns: []}
			});

			expect(days).toHaveLength(1);
		});

		it('leaves a campaign day to the page whose own days cover it', () => {
			const middlePage = [
				buildDay('2026-07-15T10:00:00Z'),
				buildDay('2026-07-12T10:00:00Z')
			];

			const campaigns = {
				'2026-07-13': campaignDay,
				'2026-07-20': campaignDay,
				'2026-07-01': campaignDay
			};

			const days = mergeCampaignDays(middlePage, campaigns, {
				isFirstPage: false,
				isLastPage: false
			});

			expect(days.map(({date}) => toDayKey(date))).toEqual([
				'2026-07-15',
				'2026-07-13',
				'2026-07-12'
			]);
		});

		it('gives the first page every day above it, so today is never dropped', () => {
			const days = mergeCampaignDays(
				[buildDay('2026-07-15T10:00:00Z')],
				{'2026-07-20': campaignDay},
				{isFirstPage: true, isLastPage: false}
			);

			expect(days.map(({date}) => toDayKey(date))).toEqual([
				'2026-07-20',
				'2026-07-15'
			]);
		});

		it('gives the last page every day below it', () => {
			const days = mergeCampaignDays(
				[buildDay('2026-07-15T10:00:00Z')],
				{'2026-07-01': campaignDay},
				{isFirstPage: false, isLastPage: true}
			);

			expect(days.map(({date}) => toDayKey(date))).toEqual([
				'2026-07-15',
				'2026-07-01'
			]);
		});

		it('returns the days untouched when nothing was fetched', () => {
			const sessionDays = [buildDay('2026-07-16T10:00:00Z')];

			expect(mergeCampaignDays(sessionDays)).toEqual(sessionDays);
		});
	});

	describe('buildCampaignUrls', () => {
		const campaignDays = {
			'2026-07-16': {campaigns: [{campaignId: 'c1'}, {campaignId: 'c2'}]},
			'2026-07-15': {campaigns: [{campaignId: 'c1'}]}
		};

		it('links every campaign to its page, once per campaign', () => {
			expect(
				buildCampaignUrls(campaignDays, {channelId: '456', groupId: '23'})
			).toEqual({
				c1: '/workspace/23/456/campaigns/c1',
				c2: '/workspace/23/456/campaigns/c2'
			});
		});

		it('links nothing without a channel and a group', () => {
			expect(buildCampaignUrls(campaignDays, {})).toEqual({});
			expect(buildCampaignUrls(campaignDays, {groupId: '23'})).toEqual({});
		});

		it('links nothing when there are no days', () => {
			expect(
				buildCampaignUrls(undefined, {channelId: '456', groupId: '23'})
			).toEqual({});
		});
	});

	describe('buildTouchIndividualUrls', () => {
		const campaignDays = {
			'2026-07-16': {
				campaigns: [
					{
						touches: [
							{individualId: 'ind-1'},
							{individualId: null}
						]
					},
					{touches: [{individualId: 'ind-2'}]}
				]
			}
		};

		it('routes every touch that matched an individual', () => {
			const urls = buildTouchIndividualUrls(campaignDays, {
				channelId: '456',
				groupId: '23'
			});

			expect(Object.keys(urls)).toEqual(['ind-1', 'ind-2']);
			expect(urls['ind-1']).toContain('ind-1');
		});

		it('routes nothing without a channel and a group to route within', () => {
			expect(buildTouchIndividualUrls(campaignDays, {})).toEqual({});
			expect(
				buildTouchIndividualUrls(campaignDays, {channelId: '456'})
			).toEqual({});
		});

		it('routes nothing when no day was fetched', () => {
			expect(
				buildTouchIndividualUrls(undefined, {
					channelId: '456',
					groupId: '23'
				})
			).toEqual({});
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
