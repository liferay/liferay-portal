import moment from 'moment';
import React from 'react';
import {DEFAULT_ACTIVITY_MAX} from 'shared/api/activities';
import getEventDashboardUrl, {
	EventDashboardContext,
} from './getEventDashboardUrl';
import {
	applyTimeZone,
	DEFAULT_DATE_FORMAT,
	formatUTCDate,
	getCustomDateFormat,
} from 'shared/util/date';
import {getSafeDecodedURIComponent} from './util';
import {AssetTypes, TimeIntervals} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {Routes, toRoute} from 'shared/util/router';
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

/**
 * The Salesforce Campaign a touch carried. `campaignId` is the raw value the
 * tenant's configured campaign-identity query param held, and is always
 * present. `campaignName` is the campaign that id resolved to, and is null when
 * it matched none — an unresolved touch the timeline still has to show, rather
 * than one that carried no campaign at all (which produces no campaign here).
 */
export type TimelineCampaign = {
	campaignId: string;
	campaignName: string | null;
};

export type SessionEvent = {
	attributes: Record<string, unknown>;
	campaign?: TimelineCampaign;
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
	totalEvents?: number;
};

export type VerticalTimelinePageGroup = {
	campaign?: TimelineCampaign;
	descriptionUrl?: string;
	experienceNames?: string[];
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
	becameKnown?: boolean;
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
	jobTitle?: string;
};

/**
 * Every row shape the shared VerticalTimeline component can render. A
 * discriminated union — each variant carries its own literal-`true` tag
 * (`individual`, `session`, `pageGroup`), except `SessionEvent`, the fallback
 * case once the other three are ruled out.
 */
export type VerticalTimelineItem =
	| VerticalTimelineIndividual
	| VerticalTimelineSession
	| VerticalTimelinePageGroup
	| SessionEvent;

export type CampaignTouchMember = {
	individualId: string | null;
	individualName: string;
	jobTitle: string | null;
	status: string | null;
};

export type CampaignTouch = {
	campaignId: string;
	campaignName: string;
	origin: string;
	touches: CampaignTouchMember[];
	touchesCount: number;
};

export type TimelineDay = {
	date: string;
	header: VerticalTimelineHeader;
	items: VerticalTimelineItem[];
};

export interface ActivityHistoryPoint {
	intervalInitDate: number;
	totalCampaignResponses?: number;
	totalEvents: number;
	totalSessions?: number;
}

interface EventMetricLike {
	totalCampaignActivitiesMetric?: {
		histogram?: {metrics?: Array<{value: number}>};
	};
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
			totalCampaignResponses:
				eventMetric?.totalCampaignActivitiesMetric?.histogram
					?.metrics?.[index]?.value,
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
 * The campaign a single event's touch carried, or undefined when it carried
 * none. A resolved and an unresolved touch are both campaigns — only a touch
 * with no campaign identity at all produces nothing, so the timeline can tell
 * "this campaign did not resolve" apart from "there was no campaign here".
 */
export const getEventCampaign = ({
	campaignId,
	campaignName,
}: UserSessionEvent): TimelineCampaign | undefined =>
	campaignId ? {campaignId, campaignName: campaignName ?? null} : undefined;

/**
 * Turns one of the name/value lists an event carries into the object the
 * timeline expands into a table. Each list keeps its own entry in the payload
 * rather than being merged into one, so the table a parameter lands in is the
 * API's own classification.
 */
const toAttributeMap = (
	attributes: Array<{name: string; value: string}>
): Record<string, string> =>
	Object.fromEntries(attributes.map(({name, value}) => [name, value]));

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
			experienceId,
			experienceName,
			name,
			pageTitle,
			properties,
			utmProperties,
		} = event;

		const campaign = getEventCampaign(event);

		return {
			attributes: {
				applicationId,
				...(eventDate && {eventDate}),
				eventId,
				...(experienceId && {experienceId}),
				...(experienceName && {experienceName}),
				...(properties?.length && {
					properties: toAttributeMap(properties),
				}),
				...(utmProperties?.length && {
					utmProperties: toAttributeMap(utmProperties),
				}),
			},
			...(campaign && {campaign}),
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
 * The key the API grouped the event under, which is also the key it paged the
 * results on. Deriving it here as well would let the rendered groups and the
 * page boundaries drift apart, so the backend owns it: it is the page's
 * canonical URL, and absent for an event that is not page bound.
 */
const getPageGroupKey = ({pageGroupId}: UserSessionEvent): string =>
	pageGroupId || '';

/**
 * Groups a session's events by the page they happened on, so the activity
 * stream shows one entry per visited page instead of a raw list of events.
 *
 * Events are keyed by the page group the API assigned them, so a page visited
 * more than once in the same session collapses into a single entry carrying the
 * time range and event count of every event on that page. Events that are not
 * page bound (an external data source, or a DXP event with no URL) carry no key
 * and stay as direct session items. Groups and those loose events are ordered by
 * their most recent event, newest first, matching how the timeline already
 * orders days and sessions. Within a group the events keep the order they
 * arrive in.
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

		const experienceNames = Array.from(
			new Set(
				pageEvents
					.map(({experienceId, experienceName}) =>
						experienceId && experienceId !== 'DEFAULT'
							? experienceName || experienceId
							: undefined
					)
					.filter((name): name is string => !!name)
			)
		);

		const subtitle = getSafeDecodedURIComponent(pageKey);

		// formatEvents already builds a descriptionUrl for every event,
		// including the representative one above, so the group reuses it
		// instead of calling getEventDashboardUrl a second time.

		const formattedPageEvents = formatEvents(
			pageEvents,
			userAgent,
			context
		);

		// Every event on the page carries the campaign of the touch that
		// brought the visitor to it, so the group takes the first one it
		// finds — preferring the page-view event, the touch itself.

		const groupCampaign = [pageEvent, ...pageEvents].reduce<
			TimelineCampaign | undefined
		>((campaign, event) => campaign ?? getEventCampaign(event), undefined);

		sortableItems.push({
			item: {
				...(groupCampaign && {campaign: groupCampaign}),
				descriptionUrl:
					formattedPageEvents[Math.max(pageEventIndex, 0)]
						.descriptionUrl,
				...(experienceNames.length && {experienceNames}),

				// The page group's own subtitle and campaign label already show
				// the page URL and the touch it came from, so its nested
				// events don't repeat either.

				nestedItems: formattedPageEvents.map((event) => ({
					...event,
					campaign: undefined,
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
	datetime: Date | string | number,
	timeZoneId?: string
): string => {
	const day = toDayKey(datetime, timeZoneId);

	return day === toDayKey(Date.now(), timeZoneId)
		? Liferay.Language.get('today')
		: moment.utc(day).format(getCustomDateFormat());
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

export const toDayKey = (
	datetime: Date | string | number,
	timeZoneId?: string
): string =>
	timeZoneId
		? applyTimeZone(datetime, timeZoneId).format(DEFAULT_DATE_FORMAT)
		: formatUTCDate(datetime, DEFAULT_DATE_FORMAT);

export const buildTouchIndividualUrls = (
	campaignDays: Record<
		string,
		{campaigns: Array<{touches: Array<{individualId: string | null}>}>}
	> = {},
	{channelId, groupId}: EventDashboardContext = {}
): Record<string, string> => {
	if (!channelId || !groupId) {
		return {};
	}

	return Object.values(campaignDays).reduce<Record<string, string>>(
		(urls, {campaigns}) => {
			campaigns.forEach(({touches}) =>
				touches.forEach(({individualId}) => {
					if (individualId) {
						urls[individualId] = toRoute(
							Routes.CONTACTS_INDIVIDUAL,
							{channelId, groupId, id: individualId}
						);
					}
				})
			);

			return urls;
		},
		{}
	);
};

export const mergeCampaignDays = (
	days: TimelineDay[],
	campaignDays: Record<string, {campaigns: unknown[]}> = {},
	{
		isFirstPage = true,
		isLastPage = true,
		timeZoneId,
	}: {isFirstPage?: boolean; isLastPage?: boolean; timeZoneId?: string} = {}
): TimelineDay[] => {
	const dayKeys = days.map(({date}) => toDayKey(date));

	const sessionDayKeys = new Set(dayKeys);

	const newestDayKey = dayKeys[0];

	const oldestDayKey = dayKeys[dayKeys.length - 1];

	const ownsDay = (dayKey: string) => {
		if (!dayKeys.length) {
			return isFirstPage && isLastPage;
		}

		return (
			(isFirstPage || dayKey <= newestDayKey) &&
			(isLastPage || dayKey >= oldestDayKey)
		);
	};

	const campaignOnlyDays = Object.entries(campaignDays)
		.filter(
			([dayKey, {campaigns}]) =>
				campaigns.length &&
				!sessionDayKeys.has(dayKey) &&
				ownsDay(dayKey)
		)
		.map(([dayKey]) => ({
			date: dayKey,
			header: {
				header: true as const,
				title: formatGroupingTime(dayKey, timeZoneId),
			},
			items: [],
		}));

	// Every date here is a calendar date of the same fixed shape, so string
	// order matches chronological order. ownsDay above already relies on that.

	return [...days, ...campaignOnlyDays].sort((a, b) =>
		b.date.localeCompare(a.date)
	);
};

/**
 * Groups sessions by the day they started, newest day first, and emits a day
 * header followed by that day's sessions. Shared by the account and individual
 * activity streams, which then order the sessions inside each day.
 */
export const groupSessionsByDay = <
	T extends {createDate: string; events?: unknown[] | null},
>(
	sessions: T[],
	timeZoneId?: string
): {date: string; daySessions: T[]; header: VerticalTimelineHeader}[] => {
	const sessionsByDay = groupBy(sessions, (session) =>
		toDayKey(session.createDate, timeZoneId)
	);

	return Array.from(sessionsByDay.keys())
		.sort((a, b) => b.localeCompare(a))
		.map((dayKey) => {
			const daySessions = sessionsByDay.get(dayKey) ?? [];

			return {
				date: dayKey,
				daySessions: daySessions.sort(
					(a, b) =>
						moment(b.createDate).valueOf() -
						moment(a.createDate).valueOf()
				),
				header: {
					header: true,
					title: formatGroupingTime(dayKey, timeZoneId),
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
 * subject — so a day holds its sessions directly.
 */
export const formatSessions = (
	sessions: UserSession[] = [],
	context: EventDashboardContext = {}
): TimelineDay[] =>
	groupSessionsByDay(sessions, context.timeZoneId).map(
		({date, daySessions, header}) => {
			const items: VerticalTimelineSession[] = [];

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
					becameKnown: session.becameKnown,
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

			return {date, header, items};
		}
	);

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
