import moment from 'moment';
import React from 'react';
import {DEFAULT_ACTIVITY_MAX} from 'shared/api/activities';
import getEventDashboardUrl, {
	EventDashboardContext,
} from './getEventDashboardUrl';
import {getCustomDateFormat} from 'shared/util/date';
import {getSafeDecodedURIComponent} from './util';
import {
	AssetTypes,
	LIFERAY_DXP_APPLICATION_IDS,
	TimeIntervals,
} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';
import {UserSession, UserSessionEvent} from 'shared/queries/UserSessionQuery';

export const CHART_ACTIVITY_ID = 'activities';
export const CHART_ID = 'individualActivity';

export const INTERVAL_MAP = {
	D: TimeIntervals.Day,
	M: TimeIntervals.Month,
	W: TimeIntervals.Week,
};

export type SessionEvent = {
	attributes: Record<string, unknown>;
	description: string;
	descriptionUrl?: string;
	subtitle: string | undefined;
	time: moment.Moment;
	title: string;
};

export type UserSessionAttributes = {
	contentLanguageID: string;
	description: string;
	devicePixelRatioz: number;
	header: string;
	keywords: string;
	languageID: string;
	screenHeight: number;
	screenWidth: number;
	timezoneOffset: string;
	userAgent: string;
};

export type VerticalTimelineHeader = {
	header: true;
	title: string;
	totalEvents: number;
};

export type VerticalTimelinePageGroup = {
	descriptionUrl?: string;
	nestedItems: SessionEvent[];
	pageGroup: true;
	subtitle: string;
	time: moment.Moment;
	title: string;
	totalEvents: number;
};

export type VerticalTimelineSession = {
	applicationId: string;
	attributes: Record<string, unknown>;
	browserName?: string;
	device: string;
	endTime?: Date | string | null;
	nestedItems: (SessionEvent | VerticalTimelinePageGroup)[];
	noTimestamps?: boolean;
	session: true;
	time: string;
	totalEvents: number;
	userAgent: string;
};

/**
 * The individual a group of sessions belongs to, shown as its own plain row —
 * not expandable — ahead of that individual's sessions for the day. Only the
 * account activity stream has this level; the individual stream's subject is
 * already the individual, so it never emits one.
 */
export type VerticalTimelineIndividual = {
	individual: true;
	individualId?: string;
	individualName: string;
	individualUrl?: string;
	isAnonymous: boolean;
};

/**
 * Every row shape the shared VerticalTimeline component can render. A
 * discriminated union — each variant carries its own literal-`true` tag
 * (`header`, `individual`, `session`, `pageGroup`), except `SessionEvent`,
 * the fallback case once the other four are ruled out.
 */
export type VerticalTimelineItem =
	| VerticalTimelineHeader
	| VerticalTimelineIndividual
	| VerticalTimelineSession
	| VerticalTimelinePageGroup
	| SessionEvent;

export interface ActivityHistoryPoint {
	intervalInitDate: number;
	totalEvents: number;
	totalSessions?: number;
}

interface EventMetricLike {
	totalEventsMetric: {
		histogram: {metrics?: Array<{key: string; value: number}>};
	};
	totalSessionsMetric?: {
		histogram?: {metrics?: Array<{value: number}>};
	};
}

/**
 * Maps an event-metric histogram into the activity-history points consumed by
 * the activity-stream chart. Shared by the account and individual cards, which
 * read the same eventMetric shape.
 */
export const mapEventMetricToActivityHistory = (
	eventMetric: EventMetricLike
): ActivityHistoryPoint[] =>
	eventMetric.totalEventsMetric.histogram.metrics?.map(
		({key, value}, index) => ({
			intervalInitDate: moment.utc(key).valueOf(),
			totalEvents: value,
			totalSessions:
				eventMetric?.totalSessionsMetric?.histogram?.metrics?.[index]
					?.value,
		})
	) ?? [];

/**
 * Format actvitiy metrics for use in ChangeLegend
 * @param {Object} changeMetrics - History data points.
 * @param {number} changeMetrics.activityChange - The activity count change from
 *                                                previous period.
 * @param {number} changeMetrics.activityCount - The activity count.
 * @return {Array} Activity metrics formatted for use in ChangeLegend.
 */
export const buildLegendItems = ({
	activityChange,
	activityCount,
}: {
	activityChange: number;
	activityCount: number;
}): {change: number; id: string; secondaryInfo: string; title: string}[] => [
	{
		change: activityChange,
		id: CHART_ACTIVITY_ID,
		secondaryInfo: sub(Liferay.Language.get('x-day-change'), [
			DEFAULT_ACTIVITY_MAX,
		]) as string,
		title: sub(Liferay.Language.get('total-activity-count-x'), [
			toLocale(activityCount),
		]) as string,
	},
];

/**
 * An external data source reaches Analytics Cloud through a webhook, which it
 * announces in the session's user agent. Its events are not page bound, so they
 * are neither linked to a dashboard nor grouped by page.
 */
export const isWebhookUserAgent = (userAgent?: string): boolean =>
	!!userAgent?.toLowerCase().includes('webhook');

/**
 * Formats UserSessions events and maps its attributes to the required to be used in VerticalTimeline component.
 * @param {Array} events Array of UserSessions events.
 * @returns {Array.<Object>} Array of objects for a vertical timeline.
 */
export const formatEvents = (
	events: UserSessionEvent[],
	userAgent?: string,
	context: EventDashboardContext = {}
): Array<SessionEvent> => {
	const isWebhook = isWebhookUserAgent(userAgent);

	return events.map((event) => {
		const {
			applicationId,
			assetTitle,
			canonicalUrl,
			createDate,
			eventDate,
			eventId,
			name,
			pageTitle,
			properties,
		} = event;

		return {
			attributes: {
				applicationId,
				...(eventDate && {eventDate}),
				eventId,
				...(properties?.length && {
					properties: Object.fromEntries(
						properties.map(({name: propName, value}) => [
							propName,
							value,
						])
					),
				}),
			},
			description: assetTitle || pageTitle,
			descriptionUrl: getEventDashboardUrl(event, {
				...context,
				isWebhook,
			}),
			subtitle: !isWebhook
				? getSafeDecodedURIComponent(canonicalUrl)
				: undefined,
			time: moment(createDate),
			title: name,
		};
	});
};

/**
 * Only DXP events are page bound — they carry the page they happened on in
 * their canonical URL. Events from an external data source are not, so they get
 * no key and stay out of the grouping.
 */
const getPageGroupKey = ({
	applicationId,
	canonicalUrl,
	url,
}: UserSessionEvent): string =>
	LIFERAY_DXP_APPLICATION_IDS.has(applicationId)
		? canonicalUrl || url || ''
		: '';

/**
 * Groups a session's events by the page they happened on, so the activity
 * stream shows one entry per visited page instead of a raw list of events.
 *
 * Events are keyed by their canonical URL, so a page visited more than once in
 * the same session collapses into a single entry carrying the time range and
 * event count of every event on that page. Events that are not page bound (an
 * external data source, or a DXP event with no URL) stay as direct session
 * items. Groups and those loose events are ordered by their most recent event,
 * newest first, matching how the timeline already orders days and sessions.
 * Within a group the events keep the order they arrive in.
 */
export const groupEventsByPage = (
	events: UserSessionEvent[],
	userAgent?: string,
	context: EventDashboardContext = {}
): (SessionEvent | VerticalTimelinePageGroup)[] => {
	const eventsByPage = new Map<string, UserSessionEvent[]>();
	const pagelessEvents: UserSessionEvent[] = [];

	events.forEach((event) => {
		const pageKey = getPageGroupKey(event);

		if (!pageKey) {
			pagelessEvents.push(event);

			return;
		}

		const pageEvents = eventsByPage.get(pageKey) ?? [];

		pageEvents.push(event);

		eventsByPage.set(pageKey, pageEvents);
	});

	const sortableItems: {
		item: SessionEvent | VerticalTimelinePageGroup;
		latestTime: number;
	}[] = [];

	eventsByPage.forEach((pageEvents, pageKey) => {
		const eventTimes = pageEvents.map(({createDate}) =>
			moment(createDate).valueOf()
		);

		const {earliestTime, latestTime} = eventTimes.reduce(
			(range, eventTime) => ({
				earliestTime: Math.min(range.earliestTime, eventTime),
				latestTime: Math.max(range.latestTime, eventTime),
			}),
			{earliestTime: Infinity, latestTime: -Infinity}
		);

		// The page title lives on the page-view event; other events on the same
		// page (a form submission, a comment) carry their own asset title, so
		// prefer the page-view event when naming and linking the group.

		const pageEventIndex = pageEvents.findIndex(
			({applicationId}) => applicationId === AssetTypes.WebPage
		);

		const pageEvent = pageEvents[pageEventIndex] ?? pageEvents[0];

		const subtitle = getSafeDecodedURIComponent(pageKey);

		// formatEvents already builds a descriptionUrl for every event,
		// including the representative one above, so the group reuses it
		// instead of calling getEventDashboardUrl a second time.

		const formattedPageEvents = formatEvents(
			pageEvents,
			userAgent,
			context
		);

		sortableItems.push({
			item: {
				descriptionUrl:
					formattedPageEvents[Math.max(pageEventIndex, 0)]
						.descriptionUrl,

				// The page group's own subtitle already shows the page URL, so
				// its nested events don't repeat it.

				nestedItems: formattedPageEvents.map((event) => ({
					...event,
					subtitle: undefined,
				})),
				pageGroup: true,
				subtitle,
				time: moment(earliestTime),
				title: pageEvent.pageTitle || pageEvent.assetTitle || subtitle,
				totalEvents: pageEvents.length,
			},
			latestTime,
		});
	});

	formatEvents(pagelessEvents, userAgent, context).forEach((item) =>
		sortableItems.push({
			item,
			latestTime: item.time.valueOf(),
		})
	);

	return sortableItems
		.sort((a, b) => b.latestTime - a.latestTime)
		.map(({item}) => item);
};

/**
 * Formats datetime to today or the current date.
 * @param {Date|string|number} datetime - Any value accepeted by Moment.
 * @returns {Moment} Date label to be displayed.
 */
export const formatGroupingTime = (
	datetime: Date | string | number
): string => {
	const time = moment(datetime);

	return time.isSame(moment(), 'day')
		? Liferay.Language.get('today')
		: time.utc().format(getCustomDateFormat());
};

/**
 * Groups items into a Map keyed by a caller-provided key function, preserving
 * each group's insertion order.
 */
export const groupBy = <T,>(
	items: T[],
	keyFn: (item: T) => string
): Map<string, T[]> => {
	const grouped = new Map<string, T[]>();

	items.forEach((item) => {
		const key = keyFn(item);
		const group = grouped.get(key) ?? [];

		group.push(item);

		grouped.set(key, group);
	});

	return grouped;
};

/**
 * Groups sessions by the day they started, newest day first, and emits a day
 * header followed by that day's sessions. Shared by the account and individual
 * activity streams, which then order the sessions inside each day.
 */
export const groupSessionsByDay = <
	T extends {createDate: string; events?: unknown[] | null},
>(
	sessions: T[]
): {daySessions: T[]; header: VerticalTimelineHeader}[] => {
	const sessionsByDay = groupBy(sessions, (session) =>
		moment.utc(session.createDate).startOf('day').format()
	);

	return Array.from(sessionsByDay.keys())
		.sort((a, b) => moment(b).valueOf() - moment(a).valueOf())
		.map((dayKey) => {
			const daySessions = sessionsByDay.get(dayKey) ?? [];

			return {
				daySessions: daySessions.sort(
					(a, b) =>
						moment(b.createDate).valueOf() -
						moment(a.createDate).valueOf()
				),
				header: {
					header: true,
					title: formatGroupingTime(dayKey),
					totalEvents: daySessions.reduce(
						(total, {events}) => total + (events?.length ?? 0),
						0
					),
				},
			};
		});
};

/**
 * Formats individual user sessions for the shared VerticalTimeline, grouping
 * them by day and grouping each session's events by the page they happened on.
 * The individual stream has no per-user level — the individual is the page's
 * subject — so a day header is followed straight by that day's sessions.
 */
export const formatSessions = (
	sessions: UserSession[] = [],
	context: EventDashboardContext = {}
): (VerticalTimelineHeader | VerticalTimelineSession)[] => {
	const items: (VerticalTimelineHeader | VerticalTimelineSession)[] = [];

	groupSessionsByDay(sessions).forEach(({daySessions, header}) => {
		items.push(header);

		daySessions.forEach((session) => {
			const events = (session.events ??
				[]) as unknown as UserSessionEvent[];

			items.push({
				applicationId: events[0]?.applicationId ?? '',
				attributes: {
					contentLanguageID: session.contentLanguageID,
					devicePixelRatioz: session.devicePixelRatioz,
					header: Liferay.Language.get('session-attributes'),
					languageID: session.languageID,
					screenHeight: session.screenHeight,
					screenWidth: session.screenWidth,
					timezoneOffset: session.timezoneOffset,
					userAgent: session.userAgent,
				},
				browserName: session.browserName,
				device: session.deviceType,
				endTime: session.completeDate,
				nestedItems: groupEventsByPage(
					events,
					session.userAgent,
					context
				),
				noTimestamps: isWebhookUserAgent(session.userAgent),
				session: true,
				time: session.createDate,
				totalEvents: events.length,
				userAgent: session.userAgent,
			});
		});
	});

	return items;
};

/**
 * Helper function get the correct pluralization of count label.
 * @param {Number} totalEvents
 * @returns {Array} Label to be displayed.
 */
export const getActivityLabel = (totalEvents: number): React.ReactNode[] =>
	sub(
		totalEvents === 1
			? Liferay.Language.get('event-x')
			: Liferay.Language.get('events-x'),
		[<b key="ACTIVITIES">{totalEvents}</b>],
		false
	) as React.ReactNode[];

export const getSafeRangeKey = (
	rangeKey: RangeSelectors['rangeKey']
): RangeSelectors['rangeKey'] | null => {
	if (rangeKey === 'CUSTOM') {
		return null;
	}

	return rangeKey;
};
