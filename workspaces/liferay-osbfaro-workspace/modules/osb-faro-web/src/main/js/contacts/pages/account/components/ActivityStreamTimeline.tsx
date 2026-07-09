import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import Loading from 'shared/components/Loading';
import moment from 'moment';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import PaginationBar from 'shared/components/PaginationBar';
import React, {useMemo, useState} from 'react';
import {
	AccountUserSession,
	AccountUserSessionEvent,
} from 'shared/queries/AccountUserSessionQuery';
import {Routes, toRoute} from 'shared/util/router';
import {applyTimeZone, formatDateToTimeZone} from 'shared/util/date';
import {Sizes} from 'shared/util/constants';
import {Text} from '@clayui/core';

interface IActivityStreamTimelineProps {
	channelId: string;
	delta: number;
	groupId: string;
	hasQuery: boolean;
	loading: boolean;
	onClearQuery: () => void;
	onDeltaChange: (delta: number) => void;
	onPageChange: (page: number) => void;
	page: number;
	sessions: AccountUserSession[];
	timeZoneId?: string;
	totalItems: number;
}

interface UserGroup {
	isAnonymous: boolean;
	sessions: AccountUserSession[];
	userId?: string;
	userName: string;
}

interface DateGroup {
	dateKey: string;
	totalEvents: number;
	userGroups: UserGroup[];
}

const DEVICE_ICONS: Record<string, string> = {
	desktop: 'ac_display',
	mobile: 'mobile-portrait',
	smartphone: 'mobile-portrait',
	tablet: 'ac_tablet_landscape',
};

const SESSION_ATTRIBUTE_KEYS: (keyof AccountUserSession)[] = [
	'contentLanguageId',
	'devicePixelRatio',
	'languageId',
	'screenHeight',
	'screenWidth',
	'timezoneOffset',
	'userAgent',
];

const EVENT_ATTRIBUTE_KEYS: (keyof AccountUserSessionEvent)[] = [
	'applicationId',
	'assetTitle',
	'canonicalUrl',
	'pageDescription',
	'pageKeywords',
	'referrer',
	'url',
];

const ANONYMOUS_FALLBACK = (): string => Liferay.Language.get('anonymous');

const formatDay = (dateKey: string, timeZoneId?: string): string =>
	formatDateToTimeZone(dateKey, 'MMM D, YYYY', timeZoneId);

const formatTime = (date: string, timeZoneId?: string): string =>
	formatDateToTimeZone(date, 'h:mma', timeZoneId);

export const formatSessionTimeRange = (
	{completeDate, createDate}: AccountUserSession,
	timeZoneId?: string
): string => {
	const start = formatTime(createDate, timeZoneId);

	if (completeDate) {
		const end = formatTime(completeDate, timeZoneId);

		return start === end ? start : `${start} - ${end}`;
	}

	return `${start} - ${Liferay.Language.get('in-progress').toLowerCase()}`;
};

export const isEmptyValue = (value: unknown): boolean =>
	value === null || value === undefined || value === '';

export const formatAttributeValue = (value: unknown): string => {
	if (typeof value === 'string') {
		return /^[\w-]+$/.test(value) ? value : `"${value}"`;
	}

	return String(value);
};

export const groupByDate = (
	sessions: AccountUserSession[],
	timeZoneId?: string
): DateGroup[] => {
	const byDate = new Map<string, AccountUserSession[]>();

	sessions.forEach((session) => {
		const dateKey = applyTimeZone(session.createDate, timeZoneId)
			.startOf('day')
			.format('YYYY-MM-DD');

		const bucket = byDate.get(dateKey) ?? [];

		bucket.push(session);

		byDate.set(dateKey, bucket);
	});

	return Array.from(byDate.entries())
		.sort(([a], [b]) => b.localeCompare(a))
		.map(([dateKey, daySessions]) => {
			const byUser = new Map<string, AccountUserSession[]>();

			daySessions.forEach((session) => {
				const key =
					session.userId ??
					session.userName ??
					ANONYMOUS_FALLBACK();

				const bucket = byUser.get(key) ?? [];

				bucket.push(session);

				byUser.set(key, bucket);
			});

			const userGroups: UserGroup[] = Array.from(byUser.values()).map(
				(userSessions) => ({
					isAnonymous: userSessions[0].userName === null,
					sessions: userSessions.sort(
						(a, b) =>
							moment(b.createDate).valueOf() -
							moment(a.createDate).valueOf()
					),
					userId: userSessions[0].userId,
					userName: userSessions[0].userName ?? ANONYMOUS_FALLBACK(),
				})
			);

			const totalEvents = daySessions.reduce(
				(acc, {events}) => acc + (events?.length ?? 0),
				0
			);

			return {
				dateKey,
				totalEvents,
				userGroups,
			};
		});
};

const ActivityStreamTimeline: React.FC<IActivityStreamTimelineProps> = ({
	channelId,
	delta,
	groupId,
	hasQuery,
	loading,
	onClearQuery,
	onDeltaChange,
	onPageChange,
	page,
	sessions,
	timeZoneId,
	totalItems,
}) => {
	const dateGroups = useMemo(
		() => groupByDate(sessions, timeZoneId),
		[sessions, timeZoneId]
	);

	const isLastPage = page * delta >= totalItems;

	if (loading) {
		return <Loading />;
	}

	if (!sessions.length) {
		if (hasQuery) {
			return (
				<NoResultsDisplay
					description={Liferay.Language.get(
						'review-your-search-and-try-again'
					)}
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_no_results_found',
					}}
					spacer
					title={Liferay.Language.get('there-are-no-results-found')}
				>
					<button
						className="btn btn-secondary"
						onClick={onClearQuery}
						type="button"
					>
						{Liferay.Language.get('clear-search')}
					</button>
				</NoResultsDisplay>
			);
		}

		return (
			<NoResultsDisplay
				description={Liferay.Language.get(
					'check-back-later-to-see-if-data-has-been-received-from-your-data-sources,-or-try-a-different-date-range'
				)}
				spacer
				title={Liferay.Language.get('no-data-was-found')}
			/>
		);
	}

	return (
		<div className="account-activity-stream-timeline">
			{dateGroups.map(({dateKey, totalEvents: dayEvents, userGroups}) => (
				<section className="mb-4" key={dateKey}>
					<header className="d-flex align-items-center py-2 border-bottom mb-3 mt-2">
						<Text size={4} weight="semi-bold">
							{formatDay(dateKey, timeZoneId)}
						</Text>

						<span className="ml-3 text-secondary d-inline-flex align-items-center">
							<ClayIcon className="mr-1" symbol="click" />

							{dayEvents}
						</span>
					</header>

					{userGroups.map(
						({
							isAnonymous,
							sessions: userSessions,
							userId,
							userName,
						}) => (
							<div
								className="mb-4"
								key={`${dateKey}-${userId ?? userName}`}
							>
								<div className="d-flex align-items-center">
									<ClaySticker
										className="mr-2"
										shape="user-icon"
										size="sm"
									>
										<ClayIcon
											color="gray"
											symbol={
												isAnonymous
													? 'anonymize'
													: 'user'
											}
										/>
									</ClaySticker>

									<Text color="primary" weight="semi-bold">
										{userId ? (
											<ClayLink
												href={toRoute(
													Routes.CONTACTS_INDIVIDUAL,
													{
														channelId,
														groupId,
														id: userId,
													}
												)}
											>
												{userName}
											</ClayLink>
										) : (
											userName
										)}
									</Text>
								</div>

								<div className="timeline-rail-container">
									<div
										className={`timeline-rail ${
											isLastPage ? 'is-capped' : ''
										}`}
									/>

									{userSessions.map((session, idx) => (
										<SessionRow
											key={`${session.createDate}-${idx}`}
											session={session}
											timeZoneId={timeZoneId}
										/>
									))}
								</div>
							</div>
						)
					)}
				</section>
			))}

			<PaginationBar
				className="mt-3"
				onDeltaChange={onDeltaChange}
				onPageChange={onPageChange}
				page={page}
				selectedDelta={delta}
				totalItems={totalItems}
			/>
		</div>
	);
};

interface ISessionRowProps {
	session: AccountUserSession;
	timeZoneId?: string;
}

const SessionRow: React.FC<ISessionRowProps> = ({session, timeZoneId}) => {
	const [expanded, setExpanded] = useState(false);
	const [hovered, setHovered] = useState(false);

	const deviceIcon = session.deviceType
		? DEVICE_ICONS[session.deviceType.toLowerCase()] ?? 'devices'
		: null;

	return (
		<div>
			<div className="timeline-row">
				<span className="timeline-session-dot" />

				<div
					className={`timeline-row-wrapper ${
						expanded ? 'is-expanded' : ''
					} ${hovered ? 'is-hovered' : ''}`}
				>
					<button
						aria-expanded={expanded}
						className="timeline-row-button btn btn-unstyled align-items-center text-left"
						onClick={() => setExpanded((prev) => !prev)}
						onMouseEnter={() => setHovered(true)}
						onMouseLeave={() => setHovered(false)}
						type="button"
					>
						<Text weight="semi-bold">
							{formatSessionTimeRange(session, timeZoneId)}
						</Text>

						<span className="ml-auto d-inline-flex align-items-center text-secondary">
							<span
								className="d-inline-flex align-items-center mr-3"
								style={{fontSize: '1rem'}}
							>
								<ClayIcon className="mr-1" symbol="click" />

								{session.events?.length ?? 0}
							</span>

							{deviceIcon && (
								<ClayIcon
									className="mr-3"
									symbol={deviceIcon}
								/>
							)}

							<ClayIcon
								symbol={expanded ? 'caret-top' : 'caret-bottom'}
							/>
						</span>
					</button>

					{expanded && <SessionAttributes session={session} />}
				</div>
			</div>

			<ul className="list-unstyled mb-0 mt-2">
				{session.events?.map((event, idx) => (
					<EventRow
						event={event}
						key={`${event.createDate}-${idx}`}
						timeZoneId={timeZoneId}
					/>
				))}
			</ul>
		</div>
	);
};

interface IEventRowProps {
	event: AccountUserSessionEvent;
	timeZoneId?: string;
}

const EventRow: React.FC<IEventRowProps> = ({event, timeZoneId}) => {
	const [expanded, setExpanded] = useState(false);
	const [hovered, setHovered] = useState(false);

	return (
		<li>
			<div className="timeline-row">
				<span className="timeline-event-dot" />

				<div
					className={`timeline-row-wrapper ${
						expanded ? 'is-expanded' : ''
					} ${hovered ? 'is-hovered' : ''}`}
				>
					<button
						aria-expanded={expanded}
						className="timeline-row-button btn btn-unstyled align-items-start text-left"
						onClick={() => setExpanded((prev) => !prev)}
						onMouseEnter={() => setHovered(true)}
						onMouseLeave={() => setHovered(false)}
						type="button"
					>
						<span
							className="text-secondary"
							style={{minWidth: '5rem'}}
						>
							{formatTime(event.createDate, timeZoneId)}
						</span>

						<div className="flex-grow-1 min-w-0">
							<div className="font-weight-semi-bold text-dark">
								{event.name}
							</div>

							<div className="font-weight-semi-bold text-secondary text-truncate">
								{event.pageTitle || event.assetTitle}
							</div>

							{event.canonicalUrl && (
								<div className="text-secondary text-truncate">
									{event.canonicalUrl}
								</div>
							)}
						</div>

						<ClayIcon
							className="ml-3 text-secondary"
							symbol={expanded ? 'caret-top' : 'caret-bottom'}
						/>
					</button>

					{expanded && <EventAttributes event={event} />}
				</div>
			</div>
		</li>
	);
};

interface ISessionAttributesProps {
	session: AccountUserSession;
}

const SessionAttributes: React.FC<ISessionAttributesProps> = ({session}) => {
	const items = SESSION_ATTRIBUTE_KEYS.map((key) => ({
		key,
		value: session[key],
	})).filter(({value}) => !isEmptyValue(value));

	if (!items.length) {
		return null;
	}

	return (
		<div className="timeline-attributes">
			<div className="font-weight-semi-bold mb-2">
				{Liferay.Language.get('session-attributes')}
			</div>

			<AttributesList items={items} />
		</div>
	);
};

interface IEventAttributesProps {
	event: AccountUserSessionEvent;
}

const EventAttributes: React.FC<IEventAttributesProps> = ({event}) => {
	const items = EVENT_ATTRIBUTE_KEYS.map((key) => ({
		key,
		value: event[key],
	})).filter(({value}) => !isEmptyValue(value));

	if (!items.length) {
		return null;
	}

	return (
		<div className="timeline-event-attributes">
			<div className="font-weight-semi-bold text-secondary mb-2">
				{Liferay.Language.get('event-attributes')}
			</div>

			<AttributesList items={items} />
		</div>
	);
};

interface IAttributesListProps {
	items: {key: string; value: unknown}[];
}

const AttributesList: React.FC<IAttributesListProps> = ({items}) => (
	<dl className="mb-0">
		{items.map(({key, value}) => (
			<div className="d-flex py-1" key={key}>
				<dt
					className="font-weight-normal text-secondary mb-0"
					style={{minWidth: '11rem'}}
				>
					{key}
				</dt>

				<dd className="mb-0 text-break text-secondary">
					{formatAttributeValue(value)}
				</dd>
			</div>
		))}
	</dl>
);

export default ActivityStreamTimeline;
