import {formatPayloadTables} from '../payloadTables';
import {
	ACTIVITY_PAYLOAD_LAYOUT,
	buildCampaignUrls,
	buildTouchIndividualUrls,
	formatEvents,
	formatGroupingTime,
	formatSessions,
	getActivityLabel,
	getDisplayableFields,
	getSafeRangeKey,
	groupEventsByPage,
	groupSessionsByDay,
	mapEventMetricToActivityHistory,
	mergeCampaignDays,
	SESSION_PAYLOAD_LAYOUT,
	toDayKey
} from '../activities';

const CONTEXT = {channelId: '456', groupId: '23'};

const CAMPAIGN_ENTITY_ID = '6beeaea355642c82e8bb7f9c049f58e8';

const CAMPAIGN_URL = `/workspace/23/456/campaigns/${CAMPAIGN_ENTITY_ID}`;

const CAMPAIGN = {
	campaignEntityId: CAMPAIGN_ENTITY_ID,
	campaignId: '7013a000002QwErtAAG',
	campaignName: 'Spring Compactor Promo 2026'
};

const UNRESOLVED_CAMPAIGN = {
	campaignEntityId: null,
	campaignId: '7013a000002XyZbAAK',
	campaignName: null
};

const HOME_URL = 'https://liferay.com/home';

const buildEvent = (overrides = {}) => ({
	applicationId: 'CustomEvent',
	createDate: '2026-07-16T10:00:00.000Z',
	eventId: 'customEvent',
	name: 'customEvent',
	...overrides
});

const buildPageView = (overrides = {}) =>
	buildEvent({
		applicationId: 'Page',
		canonicalUrl: HOME_URL,
		eventId: 'pageViewed',
		name: 'pageViewed',
		pageGroupId: HOME_URL,
		pageTitle: 'Home',
		...overrides
	});

const buildSession = (overrides = {}) => ({
	becameKnown: false,
	browserName: 'Chrome',
	completeDate: '2026-07-16T11:00:00.000Z',
	createDate: '2026-07-16T10:00:00.000Z',
	deviceType: 'Desktop',
	events: [
		buildPageView(),
		buildPageView({createDate: '2026-07-16T10:05:00.000Z'})
	],
	languageId: 'pt-BR',
	userAgent: 'Mozilla/5.0',
	...overrides
});

describe('activities', () => {
	it('maps each interval of the event metric histogram into a point', () => {
		expect(
			mapEventMetricToActivityHistory({
				totalCampaignActivitiesMetric: {
					histogram: {metrics: [{value: 5}]}
				},
				totalEventsMetric: {
					histogram: {metrics: [{key: '2026-09-07T00:00:00Z', value: 7}]}
				},
				totalSessionsMetric: {histogram: {metrics: [{value: 3}]}}
			})
		).toEqual([
			{
				intervalInitDate: Date.UTC(2026, 8, 7),
				totalCampaignResponses: 5,
				totalEvents: 7,
				totalSessions: 3
			}
		]);
	});

	it('keeps every displayable field, even unknown or falsy ones, and drops empty values, lists and Apollo\'s typename', () => {
		expect(
			getDisplayableFields({
				__typename: 'UserSession',
				becameKnown: false,
				browserName: 'Chrome',
				contentLanguageId: null,
				devicePixelRatio: '',
				events: [buildPageView()],
				languageId: undefined,
				screenHeight: 0,
				someNewField: 'someValue'
			})
		).toEqual({
			becameKnown: false,
			browserName: 'Chrome',
			screenHeight: 0,
			someNewField: 'someValue'
		});
	});

	describe('formatEvents', () => {
		it('turns an event into a row named, described and timed after it, with its decoded url', () => {
			const [row] = formatEvents([
				buildPageView({
					assetTitle: 'Asset',
					canonicalUrl: 'http://localhost:7400/%e6%96%b0'
				})
			]);

			expect(row).toMatchObject({
				description: 'Asset',
				subtitle: 'http://localhost:7400/新',
				title: 'pageViewed'
			});
			expect(row.time.toISOString()).toBe('2026-07-16T10:00:00.000Z');
		});

		it('gives a webhook event neither a subtitle nor a dashboard link', () => {
			const [row] = formatEvents(
				[buildEvent({canonicalUrl: 'https://hubspot.com'})],
				'HubSpot Webhook',
				CONTEXT
			);

			expect(row.subtitle).toBeUndefined();
			expect(row.descriptionUrl).toBeUndefined();
		});

		it('lays the attributes out as the event\'s displayable fields, with its properties and utm parameters as groups of their own', () => {
			const [{attributes}] = formatEvents([
				buildEvent({
					acquisitionProperties: [{name: 'utm_medium', value: 'ppc'}],
					properties: [{name: 'formId', value: 'abc123'}]
				})
			]);

			expect(attributes).toEqual({
				acquisitionProperties: {utm_medium: 'ppc'},
				applicationId: 'CustomEvent',
				createDate: '2026-07-16T10:00:00.000Z',
				eventId: 'customEvent',
				name: 'customEvent',
				properties: {formId: 'abc123'}
			});
		});

		it('carries the campaign the touch resolved to, linked to its page', () => {
			const [row] = formatEvents([buildEvent(CAMPAIGN)], undefined, CONTEXT);

			expect(row.campaign).toEqual({
				campaignId: CAMPAIGN.campaignId,
				campaignName: CAMPAIGN.campaignName
			});
			expect(row.attributes.campaign).toEqual({
				campaignName: CAMPAIGN.campaignName,
				campaignUrl: CAMPAIGN_URL
			});
		});

		it('keeps an unresolved campaign unlinked rather than linking its utm_cid', () => {
			const [row] = formatEvents(
				[buildEvent(UNRESOLVED_CAMPAIGN)],
				undefined,
				CONTEXT
			);

			expect(row.campaign).toEqual({
				campaignId: UNRESOLVED_CAMPAIGN.campaignId,
				campaignName: null
			});
			expect(row.attributes.campaign).toEqual({campaignName: null});
		});

		it('carries no campaign for an event without one', () => {
			const [row] = formatEvents([buildEvent()], undefined, CONTEXT);

			expect(row).not.toHaveProperty('campaign');
			expect(row.attributes).not.toHaveProperty('campaign');
		});
	});

	describe('payload layouts', () => {
		it('lays a session out as a single table of its attributes', () => {
			const [{attributes}] = formatSessions([buildSession()])[0].items;

			expect(
				formatPayloadTables(attributes, SESSION_PAYLOAD_LAYOUT)
			).toEqual([
				{
					rows: [
						{property: 'becameKnown', value: 'false'},
						{property: 'browserName', value: 'Chrome'},
						{
							property: 'completeDate',
							value: '2026-07-16T11:00:00.000Z'
						},
						{property: 'createDate', value: '2026-07-16T10:00:00.000Z'},
						{property: 'deviceType', value: 'Desktop'},
						{property: 'languageId', value: 'pt-BR'},
						{property: 'userAgent', value: 'Mozilla/5.0'}
					],
					title: 'Session Attributes'
				}
			]);
		});

		it('lays an event out as its attributes, its linked campaign, then its utm parameters', () => {
			const [{attributes}] = formatEvents(
				[
					buildPageView({
						...CAMPAIGN,
						acquisitionProperties: [
							{name: 'utm_medium', value: 'email'}
						],
						properties: [{name: 'utm_source', value: 'newsletter'}]
					})
				],
				undefined,
				CONTEXT
			);

			const [attributesTable, campaignTable, utmTable] =
				formatPayloadTables(attributes, ACTIVITY_PAYLOAD_LAYOUT);

			expect(attributesTable).toEqual({
				rows: [
					{property: 'applicationId', value: 'Page'},
					{property: 'createDate', value: '2026-07-16T10:00:00.000Z'},
					{property: 'eventId', value: 'pageViewed'},
					{property: 'name', value: 'pageViewed'},
					{property: 'canonicalUrl', value: HOME_URL},
					{property: 'pageGroupId', value: HOME_URL},
					{property: 'pageTitle', value: 'Home'},
					{property: 'campaignEntityId', value: CAMPAIGN_ENTITY_ID},
					{property: 'campaignId', value: CAMPAIGN.campaignId},
					{property: 'campaignName', value: CAMPAIGN.campaignName},
					{property: 'utm_source', value: 'newsletter'}
				],
				title: 'Event Attributes'
			});
			expect(campaignTable).toEqual({
				rows: [
					{
						href: CAMPAIGN_URL,
						property: 'campaignName',
						value: CAMPAIGN.campaignName
					}
				],
				title: 'Campaign'
			});
			expect(utmTable).toEqual({
				rows: [{property: 'utm_medium', value: 'email'}],
				title: 'UTM Parameters'
			});
		});
	});

	describe('groupEventsByPage', () => {
		it('collapses the events of a page into one group named, timed and linked after its page view', () => {
			const [group, ...rest] = groupEventsByPage(
				[
					buildEvent({assetTitle: 'Contact Form', pageGroupId: HOME_URL}),
					buildPageView({createDate: '2026-07-16T10:05:00.000Z'})
				],
				undefined,
				CONTEXT
			);

			expect(rest).toEqual([]);
			expect(group).toMatchObject({
				descriptionUrl:
					'/workspace/23/456/sites/pages/overview/https%3A%2F%2Fliferay.com%2Fhome/Home',
				pageGroup: true,
				subtitle: HOME_URL,
				title: 'Home',
				totalEvents: 2
			});
			expect(group.time.toISOString()).toBe('2026-07-16T10:00:00.000Z');
			expect(group.nestedItems.map(({subtitle}) => subtitle)).toEqual([
				undefined,
				undefined
			]);
		});

		it('leaves an event the API gave no page group key ungrouped', () => {
			const [item] = groupEventsByPage([
				buildPageView({pageGroupId: null})
			]);

			expect(item.pageGroup).toBeUndefined();
			expect(item.title).toBe('pageViewed');
		});

		it('orders groups and loose events by their most recent event, newest first', () => {
			const result = groupEventsByPage([
				buildPageView({
					createDate: '2026-07-16T09:00:00.000Z',
					pageGroupId: 'https://liferay.com/older',
					pageTitle: 'Older Page'
				}),
				buildEvent({
					createDate: '2026-07-16T11:00:00.000Z',
					name: 'emailViewed'
				}),
				buildPageView({
					createDate: '2026-07-16T10:00:00.000Z',
					pageGroupId: 'https://liferay.com/newer',
					pageTitle: 'Newer Page'
				}),
				buildPageView({
					createDate: '2026-07-16T12:00:00.000Z',
					pageGroupId: 'https://liferay.com/older',
					pageTitle: 'Older Page'
				})
			]);

			expect(result.map(({title}) => title)).toEqual([
				'Older Page',
				'emailViewed',
				'Newer Page'
			]);
		});

		it.each([
			[
				'the page view\'s campaign over the ones around it',
				[
					buildEvent({...UNRESOLVED_CAMPAIGN, pageGroupId: HOME_URL}),
					buildPageView(CAMPAIGN),
					buildEvent({...UNRESOLVED_CAMPAIGN, pageGroupId: HOME_URL})
				]
			],
			[
				'another event\'s campaign when the page view carried none',
				[
					buildPageView(),
					buildEvent({...CAMPAIGN, pageGroupId: HOME_URL})
				]
			]
		])('carries %s onto the group', (_, events) => {
			const [group] = groupEventsByPage(events);

			expect(group.campaign.campaignId).toBe(CAMPAIGN.campaignId);
		});

		it('keeps the campaign in the details of the nested events, not on their rows', () => {
			const [
				{
					nestedItems: [nestedEvent]
				}
			] = groupEventsByPage([buildPageView(CAMPAIGN)], undefined, CONTEXT);

			expect(nestedEvent.campaign).toBeUndefined();
			expect(nestedEvent.attributes.campaign).toEqual({
				campaignName: CAMPAIGN.campaignName,
				campaignUrl: CAMPAIGN_URL
			});
		});

		it('lists each distinct experience the page views were served by once, by name or raw id, without the default', () => {
			const [group] = groupEventsByPage(
				[
					{experienceId: '39201', experienceName: 'Q3 Promo'},
					{experienceId: '39202'},
					{experienceId: '39201', experienceName: 'Q3 Promo'},
					{experienceId: 'DEFAULT', experienceName: 'Default'}
				].map((experience) => buildPageView(experience))
			);

			expect(group.experienceNames).toEqual(['Q3 Promo', '39202']);
		});
	});

	describe('formatGroupingTime', () => {
		beforeEach(() => {
			jest.useFakeTimers().setSystemTime(new Date('2026-06-20T12:00:00Z'));
		});

		afterEach(() => {
			jest.useRealTimers();
		});

		it('formats a past day in the custom date format, in the given time zone', () => {
			expect(
				formatGroupingTime('2026-06-10T01:30:00Z', 'America/Recife')
			).toBe('Jun 9, 2026');
		});

		it('labels the current day as today', () => {
			expect(formatGroupingTime('2026-06-20T08:00:00Z')).toBe('Today');
		});
	});

	describe('groupSessionsByDay', () => {
		it('groups sessions by the day they started, newest day and newest session first', () => {
			const result = groupSessionsByDay([
				{createDate: '2026-07-15T10:00:00.000Z'},
				{createDate: '2026-07-16T08:00:00.000Z'},
				{createDate: '2026-07-16T10:00:00.000Z'}
			]);

			expect(
				result.map(({date, daySessions}) => [
					date,
					daySessions.map(({createDate}) => createDate)
				])
			).toEqual([
				[
					'2026-07-16',
					['2026-07-16T10:00:00.000Z', '2026-07-16T08:00:00.000Z']
				],
				['2026-07-15', ['2026-07-15T10:00:00.000Z']]
			]);
		});

		it('heads each day with its title and the total events of its sessions', () => {
			const [day] = groupSessionsByDay([
				{createDate: '2026-07-16T08:00:00.000Z', events: [{}, {}]},
				{createDate: '2026-07-16T10:00:00.000Z', events: null}
			]);

			expect(day.header).toEqual({
				header: true,
				title: 'Jul 16, 2026',
				totalEvents: 2
			});
		});
	});

	describe('formatSessions', () => {
		it('turns each session into a timeline row under its day', () => {
			const [day, ...rest] = formatSessions([
				buildSession({becameKnown: true})
			]);

			expect(rest).toEqual([]);
			expect(day.date).toBe('2026-07-16');
			expect(day.items).toEqual([
				{
					applicationId: 'Page',
					attributes: expect.objectContaining({languageId: 'pt-BR'}),
					becameKnown: true,
					browserName: 'Chrome',
					device: 'Desktop',
					endTime: '2026-07-16T11:00:00.000Z',
					nestedItems: [
						expect.objectContaining({pageGroup: true, totalEvents: 2})
					],
					noTimestamps: false,
					session: true,
					time: '2026-07-16T10:00:00.000Z',
					totalEvents: 2,
					userAgent: 'Mozilla/5.0'
				}
			]);
		});

		it('shows no timestamps for a webhook session', () => {
			const [
				{
					items: [row]
				}
			] = formatSessions([
				buildSession({
					events: [buildEvent({applicationId: 'HubSpot'})],
					userAgent: 'HubSpot Webhook'
				})
			]);

			expect(row.noTimestamps).toBe(true);
		});
	});

	describe.each([
		[
			'buildTouchIndividualUrls',
			buildTouchIndividualUrls,
			{
				'2026-07-16': {
					campaigns: [
						{touches: [{individualId: 'ind-1'}, {individualId: null}]}
					]
				}
			},
			{
				'ind-1':
					'/workspace/23/456/contacts/individuals/known-individuals/ind-1'
			}
		],
		[
			'buildCampaignUrls',
			buildCampaignUrls,
			{
				'2026-07-15': {campaigns: [{campaignId: 'c1'}]},
				'2026-07-16': {campaigns: [{campaignId: 'c1'}]}
			},
			{c1: '/workspace/23/456/campaigns/c1'}
		]
	])('%s', (_, buildUrls, campaignDays, expected) => {
		it('routes every id in the campaign days, once each', () => {
			expect(buildUrls(campaignDays, CONTEXT)).toEqual(expected);
		});

		it('routes nothing without a channel and a group', () => {
			expect(buildUrls(campaignDays, {})).toEqual({});
		});
	});

	describe('mergeCampaignDays', () => {
		const buildDay = (date) => ({
			date,
			header: {header: true, title: date, totalEvents: 1},
			items: [{individual: true, individualName: 'Ada Lovelace'}]
		});

		const campaignDay = {campaigns: [{campaignId: 'c1'}]};

		const dayKeysOf = (days) => days.map(({date}) => toDayKey(date));

		it('adds an empty day that only campaigns reached, newest first', () => {
			const days = mergeCampaignDays([buildDay('2026-07-15T00:00:00Z')], {
				'2026-07-16': campaignDay
			});

			expect(dayKeysOf(days)).toEqual(['2026-07-16', '2026-07-15']);
			expect(days[0]).toEqual({
				date: '2026-07-16',
				header: {header: true, title: 'Jul 16, 2026'},
				items: []
			});
		});

		it.each([
			['a day the sessions already cover', {'2026-07-16': campaignDay}],
			['a day whose campaigns came back empty', {'2026-07-14': {campaigns: []}}]
		])('adds no day for %s', (_, campaignDays) => {
			const sessionDays = [buildDay('2026-07-16T10:00:00Z')];

			expect(mergeCampaignDays(sessionDays, campaignDays)).toEqual(
				sessionDays
			);
		});

		describe('across pages', () => {
			const campaignDays = {
				'2026-07-01': campaignDay,
				'2026-07-13': campaignDay,
				'2026-07-20': campaignDay
			};

			const sessionDays = [
				buildDay('2026-07-15T10:00:00Z'),
				buildDay('2026-07-12T10:00:00Z')
			];

			it.each([
				[
					'a middle page only the days between its own',
					sessionDays,
					{isFirstPage: false, isLastPage: false},
					['2026-07-15', '2026-07-13', '2026-07-12']
				],
				[
					'the first page every day above it, so today is never dropped',
					sessionDays,
					{isFirstPage: true, isLastPage: false},
					['2026-07-20', '2026-07-15', '2026-07-13', '2026-07-12']
				],
				[
					'the last page every day below it',
					sessionDays,
					{isFirstPage: false, isLastPage: true},
					['2026-07-15', '2026-07-13', '2026-07-12', '2026-07-01']
				],
				[
					'a page without sessions no day unless it is the only page',
					[],
					{isFirstPage: true, isLastPage: false},
					[]
				]
			])('gives %s', (_, days, pages, expected) => {
				expect(
					dayKeysOf(mergeCampaignDays(days, campaignDays, pages))
				).toEqual(expected);
			});
		});
	});

	it.each([
		[1, 'Event: '],
		[2, 'Events: ']
	])('labels %p events with the right plural', (totalEvents, label) => {
		const [text, count] = getActivityLabel(totalEvents);

		expect(text).toBe(label);
		expect(count.props.children).toBe(totalEvents);
	});

	it.each([
		['30', '30'],
		['CUSTOM', null]
	])('keeps the range key %p as %p, since a custom range has no key', (rangeKey, expected) => {
		expect(getSafeRangeKey(rangeKey)).toBe(expected);
	});
});
