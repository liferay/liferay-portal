import formatAccountSessions from '../formatAccountSessions';
import {AccountUserSession} from 'shared/queries/AccountUserSessionQuery';
import {
	VerticalTimelineHeader,
	VerticalTimelineIndividual,
	VerticalTimelineSession,
} from 'shared/util/activities';

const ANONYMOUS_USER = Liferay.Language.get('anonymous-user');

type Item =
	| VerticalTimelineHeader
	| VerticalTimelineIndividual
	| VerticalTimelineSession;

const isHeader = (item: Item): item is VerticalTimelineHeader =>
	'header' in item;

const isIndividual = (item: Item): item is VerticalTimelineIndividual =>
	'individual' in item;

const isSession = (item: Item): item is VerticalTimelineSession =>
	'session' in item;

const buildSession = (
	overrides: Record<string, unknown> = {}
): AccountUserSession =>
	({
		browserName: 'Chrome',
		completeDate: null,
		contentLanguageId: 'en-US',
		createDate: '2026-07-16T10:00:00.000Z',
		devicePixelRatio: 1,
		deviceType: 'Desktop',
		events: [
			{
				applicationId: 'Page',
				assetTitle: 'Home',
				canonicalUrl: 'https://liferay.com/home',
				createDate: '2026-07-16T10:00:00.000Z',
				name: 'pageViewed',
				pageGroupId: 'https://liferay.com/home',
				properties: [],
			},
		],
		individualId: null,
		languageId: 'en-US',
		screenHeight: 1080,
		screenWidth: 1920,
		timezoneOffset: '-03:00',
		userAgent: 'Mozilla/5.0',
		userId: null,
		userName: null,
		...overrides,
	}) as unknown as AccountUserSession;

describe('formatAccountSessions', () => {
	it('returns an empty array when there are no sessions', () => {
		expect(formatAccountSessions([])).toEqual([]);
	});

	it('groups sessions by day, emitting one individual row per individual ahead of their sessions', () => {
		const items = formatAccountSessions([
			buildSession({
				createDate: '2026-07-16T10:00:00.000Z',
				individualId: 'ind-1',
				userName: 'Ada Lovelace',
			}),
			buildSession({
				createDate: '2026-07-16T09:00:00.000Z',
				individualId: 'ind-2',
				userName: 'Alan Turing',
			}),
			buildSession({
				createDate: '2026-07-15T10:00:00.000Z',
				individualId: 'ind-3',
				userName: 'Grace Hopper',
			}),
		]);

		const dayHeaders = items.filter(isHeader);
		const individuals = items.filter(isIndividual);
		const sessions = items.filter(isSession);

		expect(dayHeaders).toHaveLength(2);
		expect(sessions).toHaveLength(3);
		expect(
			individuals.map((individual) => individual.individualName)
		).toEqual(['Ada Lovelace', 'Alan Turing', 'Grace Hopper']);
	});

	it('does not repeat the individual row for later sessions of the same day', () => {
		const items = formatAccountSessions([
			buildSession({
				createDate: '2026-07-16T10:00:00.000Z',
				individualId: 'ind-1',
				userName: 'Ada Lovelace',
			}),
			buildSession({
				createDate: '2026-07-16T08:00:00.000Z',
				individualId: 'ind-1',
				userName: 'Ada Lovelace',
			}),
		]);

		expect(items.filter(isIndividual)).toHaveLength(1);
		expect(items.filter(isSession)).toHaveLength(2);
	});

	it('always shows the generic "Anonymous User" label for an individual without an individualId, regardless of the tracked userName', () => {
		const [individual] = formatAccountSessions([
			buildSession({individualId: null, userName: 'tracked-name'}),
		]).filter(isIndividual);

		expect(individual.isAnonymous).toBe(true);
		expect(individual.individualName).toBe(ANONYMOUS_USER);
	});

	it('marks an individual with an individualId as known', () => {
		const [individual] = formatAccountSessions([
			buildSession({individualId: 'ind-1', userName: 'Grace Hopper'}),
		]).filter(isIndividual);

		expect(individual.isAnonymous).toBe(false);
		expect(individual.individualName).toBe('Grace Hopper');
	});

	it('links a known individual to their profile by individualId', () => {
		const [individual] = formatAccountSessions(
			[
				buildSession({
					individualId: 'ind-1',
					userId: 'abc123',
					userName: 'Grace Hopper',
				}),
			],
			{channelId: '420253908131944590', groupId: 'liferay.com'}
		).filter(isIndividual);

		expect(individual.individualUrl).toBe(
			'/workspace/liferay.com/420253908131944590/contacts/individuals/known-individuals/ind-1'
		);
	});

	it('links an anonymous individual by their userId when there is no individualId', () => {
		const [individual] = formatAccountSessions(
			[buildSession({userId: 'abc123', userName: null})],
			{channelId: '420253908131944590', groupId: 'liferay.com'}
		).filter(isIndividual);

		expect(individual.isAnonymous).toBe(true);
		expect(individual.individualId).toBe('abc123');
		expect(individual.individualUrl).toBe(
			'/workspace/liferay.com/420253908131944590/contacts/individuals/known-individuals/abc123'
		);
	});

	it('does not link an individual without an individualId or userId', () => {
		const [individual] = formatAccountSessions(
			[
				buildSession({
					individualId: null,
					userId: null,
					userName: 'Grace Hopper',
				}),
			],
			{channelId: '420253908131944590', groupId: 'liferay.com'}
		).filter(isIndividual);

		expect(individual.isAnonymous).toBe(true);
		expect(individual.individualName).toBe(ANONYMOUS_USER);
		expect(individual.individualUrl).toBeUndefined();
	});

	it('groups every anonymous session with neither an individualId nor a userId under a single individual', () => {
		const items = formatAccountSessions([
			buildSession({
				createDate: '2026-07-16T10:00:00.000Z',
				individualId: null,
				userId: null,
				userName: null,
			}),
			buildSession({
				createDate: '2026-07-16T09:00:00.000Z',
				individualId: null,
				userId: null,
				userName: null,
			}),
		]);

		const individuals = items.filter(isIndividual);

		expect(individuals).toHaveLength(1);
		expect(individuals[0].individualName).toBe(ANONYMOUS_USER);
		expect(items.filter(isSession)).toHaveLength(2);
	});

	it('orders days most-recent first and sums the day event totals', () => {
		const items = formatAccountSessions([
			buildSession({
				createDate: '2026-07-15T10:00:00.000Z',
				events: [{}, {}],
				userName: 'A',
			}),
			buildSession({
				createDate: '2026-07-16T10:00:00.000Z',
				events: [{}],
				userName: 'B',
			}),
		]);

		const dayHeaders = items.filter(isHeader);

		expect(dayHeaders[0].totalEvents).toBe(1);
		expect(dayHeaders[1].totalEvents).toBe(2);
	});

	it("includes accountId and accountName in a page group's dashboard link", () => {
		const [session] = formatAccountSessions([buildSession()], {
			accountId: 'acc-1',
			accountName: 'Acme Corporation',
			channelId: '420253908131944590',
			groupId: 'liferay.com',
		}).filter(isSession);

		const [pageGroup] = session.nestedItems as {descriptionUrl?: string}[];

		expect(pageGroup.descriptionUrl).toContain('accountId=acc-1');
		expect(pageGroup.descriptionUrl).toContain('accountName=Acme');
	});

	it('maps session attributes with the correct field names', () => {
		const [session] = formatAccountSessions([
			buildSession({userName: 'Ada Lovelace'}),
		]).filter(isSession);

		expect(session.attributes).toMatchObject({
			contentLanguageId: 'en-US',
			devicePixelRatio: 1,
			languageId: 'en-US',
			screenHeight: 1080,
			screenWidth: 1920,
			timezoneOffset: '-03:00',
		});
	});

	it('marks the session, sets its total events and passes through the device fields', () => {
		const [session] = formatAccountSessions([
			buildSession({
				browserName: 'Firefox',
				deviceType: 'Mobile',
				events: [{applicationId: 'Page'}, {applicationId: 'Form'}],
			}),
		]).filter(isSession);

		expect(session.session).toBe(true);
		expect(session.applicationId).toBe('Page');
		expect(session.browserName).toBe('Firefox');
		expect(session.device).toBe('Mobile');
		expect(session.totalEvents).toBe(2);
	});

	it('marks a webhook session as having no reliable timestamps', () => {
		const [session] = formatAccountSessions([
			buildSession({userAgent: 'HubSpot Webhook'}),
		]).filter(isSession);

		expect(session.noTimestamps).toBe(true);
	});

	it('groups the session events by the page they happened on', () => {
		const [session] = formatAccountSessions([
			buildSession({
				events: [
					{
						applicationId: 'Page',
						canonicalUrl: 'https://liferay.com/home',
						createDate: '2026-07-16T10:00:00.000Z',
						name: 'pageViewed',
						pageGroupId: 'https://liferay.com/home',
						pageTitle: 'Home',
					},
					{
						applicationId: 'Page',
						canonicalUrl: 'https://liferay.com/home',
						createDate: '2026-07-16T10:01:00.000Z',
						name: 'formSubmitted',
						pageGroupId: 'https://liferay.com/home',
					},
				],
			}),
		]).filter(isSession);

		expect(session.nestedItems).toHaveLength(1);
		expect(session.nestedItems[0]).toMatchObject({
			pageGroup: true,
			title: 'Home',
			totalEvents: 2,
		});
	});
});
