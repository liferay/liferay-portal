import ActivityStreamTimeline, {
	formatAttributeValue,
	formatSessionTimeRange,
	groupByDate,
	isEmptyValue,
} from '../ActivityStreamTimeline';
import React from 'react';
import {AccountUserSession} from 'shared/queries/AccountUserSessionQuery';
import {Routes, toRoute} from 'shared/util/router';
import {fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

const buildSession = (
	overrides: Partial<AccountUserSession> = {}
): AccountUserSession => ({
	browserName: 'Chrome',
	completeDate: '2024-04-03T08:30:00.000Z',
	contentLanguageId: 'en-US',
	createDate: '2024-04-03T08:00:00.000Z',
	devicePixelRatio: 1,
	deviceType: 'Desktop',
	events: [],
	languageId: 'en-US',
	screenHeight: 1080,
	screenWidth: 1920,
	timezoneOffset: '-03:00',
	userAgent: 'Mozilla/5.0',
	userId: 'user-1',
	userName: 'Jane Doe',
	...overrides,
});

describe('ActivityStreamTimeline helpers', () => {
	describe('groupByDate', () => {
		it('collapses sessions on the same UTC day into one bucket', () => {
			const groups = groupByDate([
				buildSession({createDate: '2024-04-03T08:00:00.000Z'}),
				buildSession({createDate: '2024-04-03T20:00:00.000Z'}),
			]);

			expect(groups).toHaveLength(1);
			expect(groups[0].dateKey).toBe('2024-04-03');
			expect(groups[0].userGroups[0].sessions).toHaveLength(2);
		});

		it('separates different days and sorts descending', () => {
			const groups = groupByDate([
				buildSession({createDate: '2024-04-01T10:00:00.000Z'}),
				buildSession({createDate: '2024-04-03T10:00:00.000Z'}),
				buildSession({createDate: '2024-04-02T10:00:00.000Z'}),
			]);

			expect(groups.map((g) => g.dateKey)).toEqual([
				'2024-04-03',
				'2024-04-02',
				'2024-04-01',
			]);
		});

		it('marks userName=null group as anonymous with localized fallback', () => {
			const groups = groupByDate([buildSession({userName: null})]);

			expect(groups[0].userGroups[0].isAnonymous).toBe(true);
			expect(groups[0].userGroups[0].userName).toBe('Anonymous');
		});

		it('keeps anonymous sessions with different userIds in separate groups', () => {
			const groups = groupByDate([
				buildSession({userId: 'anon-1', userName: null}),
				buildSession({userId: 'anon-2', userName: null}),
			]);

			expect(groups[0].userGroups).toHaveLength(2);
			expect(groups[0].userGroups.map((u) => u.userId).sort()).toEqual([
				'anon-1',
				'anon-2',
			]);
		});

		it('groups multiple sessions by same userId into one user group', () => {
			const groups = groupByDate([
				buildSession({
					createDate: '2024-04-03T08:00:00.000Z',
					userId: 'alice',
					userName: 'Alice',
				}),
				buildSession({
					createDate: '2024-04-03T10:00:00.000Z',
					userId: 'alice',
					userName: 'Alice',
				}),
				buildSession({
					createDate: '2024-04-03T09:00:00.000Z',
					userId: 'bob',
					userName: 'Bob',
				}),
			]);

			const userGroups = groups[0].userGroups;
			const alice = userGroups.find((u) => u.userId === 'alice');

			expect(userGroups).toHaveLength(2);
			expect(alice?.sessions).toHaveLength(2);
			expect(alice?.sessions[0].createDate).toBe(
				'2024-04-03T10:00:00.000Z'
			);
			expect(alice?.sessions[1].createDate).toBe(
				'2024-04-03T08:00:00.000Z'
			);
		});

		it('sums totalEvents across all sessions for the day', () => {
			const groups = groupByDate([
				buildSession({
					createDate: '2024-04-03T08:00:00.000Z',
					events: [{} as never, {} as never, {} as never],
				}),
				buildSession({
					createDate: '2024-04-03T20:00:00.000Z',
					events: [{} as never, {} as never],
				}),
			]);

			expect(groups[0].totalEvents).toBe(5);
		});
	});

	describe('formatSessionTimeRange', () => {
		it('renders a range when completeDate is set', () => {
			const result = formatSessionTimeRange(
				buildSession({
					completeDate: '2024-04-03T08:30:00.000Z',
					createDate: '2024-04-03T08:00:00.000Z',
				}),
				'UTC'
			);

			expect(result).toBe('8:00am - 8:30am');
		});

		it('renders an in-progress label when completeDate is null', () => {
			const result = formatSessionTimeRange(
				buildSession({
					completeDate: null,
					createDate: '2024-04-03T08:00:00.000Z',
				}),
				'UTC'
			);

			expect(result).toBe('8:00am - in progress');
		});

		it('renders a single time when start equals end', () => {
			const result = formatSessionTimeRange(
				buildSession({
					completeDate: '2024-04-03T08:00:00.000Z',
					createDate: '2024-04-03T08:00:00.000Z',
				}),
				'UTC'
			);

			expect(result).toBe('8:00am');
		});
	});

	describe('formatAttributeValue', () => {
		it('leaves bare-word strings unquoted', () => {
			expect(formatAttributeValue('WebContent')).toBe('WebContent');
			expect(formatAttributeValue('en-US')).toBe('en-US');
		});

		it('quotes strings with non-word characters', () => {
			expect(formatAttributeValue('Mozilla/5.0')).toBe('"Mozilla/5.0"');
			expect(formatAttributeValue('hello world')).toBe('"hello world"');
		});

		it('coerces numbers and booleans via String()', () => {
			expect(formatAttributeValue(2)).toBe('2');
			expect(formatAttributeValue(true)).toBe('true');
		});

		it('returns "null" for null', () => {
			expect(formatAttributeValue(null)).toBe('null');
		});
	});

	describe('isEmptyValue', () => {
		it('returns true for null, undefined, and empty string', () => {
			expect(isEmptyValue(null)).toBe(true);
			expect(isEmptyValue(undefined)).toBe(true);
			expect(isEmptyValue('')).toBe(true);
		});

		it('returns false for non-empty values', () => {
			expect(isEmptyValue('x')).toBe(false);
			expect(isEmptyValue(0)).toBe(false);
			expect(isEmptyValue(false)).toBe(false);
		});
	});
});

describe('ActivityStreamTimeline rendering', () => {
	const baseProps = {
		channelId: '123',
		delta: 20,
		groupId: 'liferay.com',
		hasQuery: false,
		loading: false,
		onClearQuery: jest.fn(),
		onDeltaChange: jest.fn(),
		onPageChange: jest.fn(),
		page: 1,
		sessions: [] as AccountUserSession[],
		timeZoneId: 'UTC',
		totalItems: 0,
	};

	it('renders the loading state', () => {
		const {container} = render(
			<ActivityStreamTimeline {...baseProps} loading />
		);

		expect(container.querySelector('.loading-root')).toBeInTheDocument();
	});

	it('renders the no-results state with a clear button when hasQuery=true', () => {
		const onClearQuery = jest.fn();
		const {getByText} = render(
			<ActivityStreamTimeline
				{...baseProps}
				hasQuery
				onClearQuery={onClearQuery}
			/>
		);

		expect(getByText('There are no results found.')).toBeInTheDocument();

		fireEvent.click(getByText('Clear Search'));

		expect(onClearQuery).toHaveBeenCalled();
	});

	it('renders the empty data state when hasQuery=false and no sessions', () => {
		const {getByText} = render(<ActivityStreamTimeline {...baseProps} />);

		expect(getByText('No data was found.')).toBeInTheDocument();
	});

	it('renders a known user with the user icon', () => {
		const {container} = render(
			<ActivityStreamTimeline
				{...baseProps}
				sessions={[
					{
						browserName: 'Chrome',
						completeDate: '2024-04-03T08:30:00.000Z',
						contentLanguageId: 'en-US',
						createDate: '2024-04-03T08:00:00.000Z',
						devicePixelRatio: 1,
						deviceType: 'Desktop',
						events: [],
						languageId: 'en-US',
						screenHeight: 1080,
						screenWidth: 1920,
						timezoneOffset: '-03:00',
						userAgent: 'Mozilla/5.0',
						userId: 'user-1',
						userName: 'Jane Doe',
					},
				]}
				totalItems={1}
			/>
		);

		expect(
			container.querySelector('use[href*="user"]')
		).toBeInTheDocument();
	});

	it('links a known individual name to its profile page', () => {
		const {getByRole} = render(
			<ActivityStreamTimeline
				{...baseProps}
				sessions={[buildSession({userId: 'ind-1', userName: 'Jane Doe'})]}
				totalItems={1}
			/>
		);

		expect(getByRole('link', {name: 'Jane Doe'})).toHaveAttribute(
			'href',
			toRoute(Routes.CONTACTS_INDIVIDUAL, {
				channelId: baseProps.channelId,
				groupId: baseProps.groupId,
				id: 'ind-1',
			})
		);
	});

	it('links an anonymous individual to its own profile page', () => {
		const {getByRole} = render(
			<ActivityStreamTimeline
				{...baseProps}
				sessions={[buildSession({userId: 'anon-1', userName: null})]}
				totalItems={1}
			/>
		);

		expect(getByRole('link', {name: 'Anonymous'})).toHaveAttribute(
			'href',
			toRoute(Routes.CONTACTS_INDIVIDUAL, {
				channelId: baseProps.channelId,
				groupId: baseProps.groupId,
				id: 'anon-1',
			})
		);
	});

	it('renders an event canonicalUrl and not its raw url', () => {
		const {getByText, queryByText} = render(
			<ActivityStreamTimeline
				{...baseProps}
				sessions={[
					buildSession({
						events: [
							{
								applicationId: 'app',
								assetTitle: 'Asset',
								canonicalUrl: 'https://example.com/canonical',
								createDate: '2024-04-03T08:05:00.000Z',
								name: 'View Page',
								pageDescription: '',
								pageKeywords: '',
								pageTitle: 'Home',
								referrer: '',
								url: 'https://example.com/raw?utm=1',
							},
						],
					}),
				]}
				totalItems={1}
			/>
		);

		expect(getByText('https://example.com/canonical')).toBeInTheDocument();
		expect(
			queryByText('https://example.com/raw?utm=1')
		).not.toBeInTheDocument();
	});

	it('omits the event url line when canonicalUrl is empty', () => {
		const {queryByText} = render(
			<ActivityStreamTimeline
				{...baseProps}
				sessions={[
					buildSession({
						events: [
							{
								applicationId: 'app',
								assetTitle: 'Asset',
								canonicalUrl: '',
								createDate: '2024-04-03T08:05:00.000Z',
								name: 'View Page',
								pageDescription: '',
								pageKeywords: '',
								pageTitle: 'Home',
								referrer: '',
								url: 'https://example.com/raw?utm=1',
							},
						],
					}),
				]}
				totalItems={1}
			/>
		);

		expect(
			queryByText('https://example.com/raw?utm=1')
		).not.toBeInTheDocument();
	});

	it('renders an anonymous session with the anonymize icon', () => {
		const {container, getByText} = render(
			<ActivityStreamTimeline
				{...baseProps}
				sessions={[
					{
						browserName: 'Chrome',
						completeDate: '2024-04-03T08:30:00.000Z',
						contentLanguageId: 'en-US',
						createDate: '2024-04-03T08:00:00.000Z',
						devicePixelRatio: 1,
						deviceType: 'Desktop',
						events: [],
						languageId: 'en-US',
						screenHeight: 1080,
						screenWidth: 1920,
						timezoneOffset: '-03:00',
						userAgent: 'Mozilla/5.0',
						userId: 'anon-1',
						userName: null,
					},
				]}
				totalItems={1}
			/>
		);

		expect(
			container.querySelector('use[href*="anonymize"]')
		).toBeInTheDocument();
		expect(getByText('Anonymous')).toBeInTheDocument();
	});
});
