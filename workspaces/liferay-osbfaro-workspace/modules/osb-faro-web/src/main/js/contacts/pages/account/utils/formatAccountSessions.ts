import moment from 'moment';
import {AccountUserSession} from 'shared/queries/AccountUserSessionQuery';
import {EventDashboardContext} from 'shared/util/getEventDashboardUrl';
import {
	formatEvents,
	formatGroupingTime,
	markGroupBoundaries,
} from 'shared/util/activities';
import {Routes, toRoute} from 'shared/util/router';
import {UserSessionEvent} from 'shared/queries/UserSessionQuery';

const ANONYMOUS_KEY = '__anonymous__';

type VerticalTimelineItem = Record<string, unknown>;

const toSessionItem = (
	session: AccountUserSession,
	context: EventDashboardContext
): VerticalTimelineItem => ({
	applicationId: session.events?.[0]?.applicationId ?? '',
	attributes: {
		contentLanguageId: session.contentLanguageId,
		devicePixelRatio: session.devicePixelRatio,
		header: Liferay.Language.get('session-attributes'),
		languageId: session.languageId,
		screenHeight: session.screenHeight,
		screenWidth: session.screenWidth,
		timezoneOffset: session.timezoneOffset,
		userAgent: session.userAgent,
	},
	browserName: session.browserName,
	device: session.deviceType,
	endTime: session.completeDate,
	nestedItems: formatEvents(
		(session.events ?? []) as unknown as UserSessionEvent[],
		session.userAgent,
		context
	),
	time: session.createDate,
	userAgent: session.userAgent,
});

/**
 * Formats account user sessions for the shared VerticalTimeline component. The
 * sessions are grouped by day and then by individual (userName), so each day
 * renders a header per individual — known (with the user icon) or anonymous
 * (with the anonymize icon) — followed by that individual's sessions.
 *
 * Unlike the individual timeline's shared `formatSessions`, this reads the
 * correct session field names, so account session attributes populate.
 */
export const formatAccountSessions = (
	sessions: AccountUserSession[] = [],
	context: EventDashboardContext = {}
): VerticalTimelineItem[] => {
	const sessionsByDay = new Map<string, AccountUserSession[]>();

	sessions.forEach((session) => {
		const dayKey = moment.utc(session.createDate).startOf('day').format();

		const daySessions = sessionsByDay.get(dayKey) ?? [];

		daySessions.push(session);

		sessionsByDay.set(dayKey, daySessions);
	});

	const orderedDays = Array.from(sessionsByDay.keys()).sort(
		(a, b) => moment(b).unix() - moment(a).unix()
	);

	const items: VerticalTimelineItem[] = [];

	orderedDays.forEach((dayKey) => {
		const daySessions = sessionsByDay.get(dayKey) ?? [];

		items.push({
			header: true,
			title: formatGroupingTime(dayKey),
			totalEvents: daySessions.reduce(
				(total, {events}) => total + (events?.length ?? 0),
				0
			),
		});

		const sessionsByUser = new Map<string, AccountUserSession[]>();

		daySessions.forEach((session) => {
			const userKey = session.userId ?? session.userName ?? ANONYMOUS_KEY;

			const userSessions = sessionsByUser.get(userKey) ?? [];

			userSessions.push(session);

			sessionsByUser.set(userKey, userSessions);
		});

		sessionsByUser.forEach((userSessions) => {
			const {userId, userName} = userSessions[0];

			items.push({
				isAnonymous: userName == null,
				title: userName || userId || Liferay.Language.get('anonymous'),
				userHeader: true,
				userHeaderUrl:
					userId && context.channelId && context.groupId
						? toRoute(Routes.CONTACTS_INDIVIDUAL, {
								channelId: context.channelId,
								groupId: context.groupId,
								id: userId,
							})
						: undefined,
			});

			userSessions
				.sort(
					(a, b) =>
						moment(b.createDate).valueOf() -
						moment(a.createDate).valueOf()
				)
				.forEach((session) =>
					items.push(toSessionItem(session, context))
				);
		});
	});

	return markGroupBoundaries(items);
};

export default formatAccountSessions;
