import formatAccountSessions from '../formatAccountSessions';
import {AccountUserSession} from 'shared/queries/AccountUserSessionQuery';

const ANONYMOUS = Liferay.Language.get('anonymous');

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
				properties: [],
			},
		],
		individualId: null,
		languageId: 'en-US',
		screenHeight: 1080,
		screenWidth: 1920,
		timezoneOffset: '-03:00',
		userAgent: 'Mozilla/5.0',
		userName: null,
		...overrides,
	}) as unknown as AccountUserSession;

describe('formatAccountSessions', () => {
	it('returns an empty array when there are no sessions', () => {
		expect(formatAccountSessions([])).toEqual([]);
	});

	it('groups sessions by day and then by individual', () => {
		const items = formatAccountSessions([
			buildSession({
				createDate: '2026-07-16T10:00:00.000Z',
				userName: 'Ada Lovelace',
			}),
			buildSession({
				createDate: '2026-07-16T09:00:00.000Z',
				userName: 'Alan Turing',
			}),
			buildSession({
				createDate: '2026-07-15T10:00:00.000Z',
				userName: null,
			}),
		]);

		const dayHeaders = items.filter((item) => item.header);
		const userHeaders = items.filter((item) => item.userHeader);

		expect(dayHeaders).toHaveLength(2);
		expect(userHeaders).toHaveLength(3);
		expect(userHeaders.map((header) => header.title)).toEqual([
			'Ada Lovelace',
			'Alan Turing',
			ANONYMOUS,
		]);
	});

	it('marks a session without an individualId as anonymous', () => {
		const [, userHeader] = formatAccountSessions([
			buildSession({individualId: null, userName: null}),
		]);

		expect(userHeader.userHeader).toBe(true);
		expect(userHeader.isAnonymous).toBe(true);
		expect(userHeader.title).toBe(ANONYMOUS);
	});

	it('marks a session with an individualId as a known individual', () => {
		const [, userHeader] = formatAccountSessions([
			buildSession({individualId: 'ind-1', userName: 'Grace Hopper'}),
		]);

		expect(userHeader.isAnonymous).toBe(false);
		expect(userHeader.title).toBe('Grace Hopper');
	});

	it('links a known individual to their page by individualId', () => {
		const [, userHeader] = formatAccountSessions(
			[
				buildSession({
					individualId: 'ind-1',
					userId: 'abc123',
					userName: 'Grace Hopper',
				}),
			],
			{channelId: '420253908131944590', groupId: 'liferay.com'}
		);

		expect(userHeader.title).toBe('Grace Hopper');
		expect(userHeader.userHeaderUrl).toBe(
			'/workspace/liferay.com/420253908131944590/contacts/individuals/known-individuals/ind-1'
		);
	});

	it('links an anonymous session by its userId when there is no individualId', () => {
		const [, userHeader] = formatAccountSessions(
			[buildSession({userId: 'abc123', userName: null})],
			{channelId: '420253908131944590', groupId: 'liferay.com'}
		);

		expect(userHeader.isAnonymous).toBe(true);
		expect(userHeader.title).toBe('abc123');
		expect(userHeader.userHeaderUrl).toBe(
			'/workspace/liferay.com/420253908131944590/contacts/individuals/known-individuals/abc123'
		);
	});

	it('does not link a session without an individualId or userId', () => {
		const [, userHeader] = formatAccountSessions(
			[
				buildSession({
					individualId: null,
					userId: null,
					userName: 'Grace Hopper',
				}),
			],
			{channelId: '420253908131944590', groupId: 'liferay.com'}
		);

		expect(userHeader.isAnonymous).toBe(true);
		expect(userHeader.title).toBe('Grace Hopper');
		expect(userHeader.userHeaderUrl).toBeUndefined();
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

		const dayHeaders = items.filter((item) => item.header);

		expect(dayHeaders[0].totalEvents).toBe(1);
		expect(dayHeaders[1].totalEvents).toBe(2);
	});

	it('marks the first and last session of each user group as group boundaries', () => {
		const items = formatAccountSessions([
			buildSession({
				createDate: '2026-07-16T10:00:00.000Z',
				userName: 'Ada Lovelace',
			}),
			buildSession({
				createDate: '2026-07-16T08:00:00.000Z',
				userName: 'Ada Lovelace',
			}),
		]);

		const sessions = items.filter(
			(item) => !item.header && !item.userHeader
		);

		expect(sessions).toHaveLength(2);
		expect(sessions[0].groupStart).toBe(true);
		expect(sessions[0].groupEnd).toBe(false);
		expect(sessions[1].groupStart).toBe(false);
		expect(sessions[1].groupEnd).toBe(true);
	});

	it('includes accountId and accountName in a pageViewed event dashboard link', () => {
		const [, , session] = formatAccountSessions([buildSession()], {
			accountId: 'acc-1',
			accountName: 'Acme Corporation',
			channelId: '420253908131944590',
			groupId: 'liferay.com',
		});

		const [event] = session.nestedItems as {descriptionUrl?: string}[];

		expect(event.descriptionUrl).toContain('accountId=acc-1');
		expect(event.descriptionUrl).toContain('accountName=Acme');
	});

	it('maps session attributes with the correct field names', () => {
		const [, , session] = formatAccountSessions([
			buildSession({userName: 'Ada Lovelace'}),
		]);

		expect(session.attributes).toMatchObject({
			contentLanguageId: 'en-US',
			devicePixelRatio: 1,
			languageId: 'en-US',
			screenHeight: 1080,
			screenWidth: 1920,
			timezoneOffset: '-03:00',
		});
	});
});
