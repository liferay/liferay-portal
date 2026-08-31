import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import moment from 'moment';
import React, {FC, useState} from 'react';
import TextTruncate from './TextTruncate';
import {Colors} from 'shared/util/colors-size';
import {formatDateToTimeZone} from 'shared/util/date';
import {
	isWebhookUserAgent,
	SessionEvent,
	TimelineCampaign,
	VerticalTimelineHeader,
	VerticalTimelineIndividual,
	VerticalTimelineItem,
	VerticalTimelinePageGroup,
	VerticalTimelineSession,
} from 'shared/util/activities';
import {LIFERAY_DXP_APPLICATION_IDS} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';

// 'LT' is moment's locale-aware time token: 12-hour with AM/PM for
// en-US, 24-hour for pt-BR/es-ES/ja-JP.

const TIME_FORMAT = 'LT';

const DEVICE_ICONS_MAP = {
	any: {
		color: Colors.MainLighten65,
		id: 'anyIcon',
		symbol: 'devices',
		title: Liferay.Language.get('unknown-device'),
	},
	desktop: {symbol: 'display', title: Liferay.Language.get('desktop')},
	mobile: {symbol: 'mobile-portrait', title: Liferay.Language.get('mobile')},
	smartphone: {
		symbol: 'mobile-portrait',
		title: Liferay.Language.get('mobile'),
	},
	tablet: {
		symbol: 'tablet-landscape',
		title: Liferay.Language.get('tablet'),
	},
};

const normalizeApplicationId = (applicationId: string): string =>
	LIFERAY_DXP_APPLICATION_IDS.has(applicationId) ? 'DXP' : applicationId;

type ITEM_SHAPE = VerticalTimelineItem;

type IRowProps<Item> = {
	initialExpanded?: boolean;
	item: Item;
	LDPEnabled?: boolean;
	timeZoneId: string;
};

/**
 * The clickable part of a row: everything but the content it reveals. The caret
 * lives here so every expandable row carries it in the same place, on the right.
 */
const RowMain: FC<{
	children: React.ReactNode;
	expanded: boolean;
	onToggle: () => void;
}> = ({children, expanded, onToggle}) => (
	<div
		className="row-main d-flex align-items-start"
		onClick={onToggle}
		onKeyPress={onToggle}
		role="button"
		tabIndex={0}
	>
		{children}

		<ClayIcon
			className="angle-icon icon-root ml-3 flex-shrink-0 text-secondary"
			symbol={expanded ? 'angle-up' : 'angle-down'}
		/>
	</div>
);

const EventCountPill: FC<{totalEvents?: number}> = ({totalEvents}) =>
	totalEvents === undefined ? null : (
		<span className="event-count-pill align-items-center d-inline-flex flex-shrink-0 font-weight-semi-bold text-secondary">
			<ClayIcon className="icon-root" symbol="click" />

			<span className="event-count ml-1">{totalEvents}</span>
		</span>
	);

/**
 * Marks a row the visitor reached through a campaign, shown beside the row's
 * event count. Every touch reads the same on the row itself; which campaign it
 * was rides in the tooltip, so a long Salesforce campaign name never stretches
 * the row. A touch whose identity matched no stored campaign names its raw id
 * there instead, so the two states stay tellable apart without the row
 * carrying a machine-readable string.
 */
const CampaignLabel: FC<{campaign: TimelineCampaign}> = ({
	campaign: {campaignId, campaignName},
}) => (
	<span
		className="campaign-label-root align-items-center d-inline-flex flex-shrink-0"
		data-tooltip
		data-tooltip-align="top"
		title={
			campaignName ??
			(sub(Liferay.Language.get('unresolved-campaign-x'), [
				campaignId,
			]) as string)
		}
	>
		<ClayLabel
			className="campaign-label flex-shrink-0 font-weight-semi-bold m-0"
			displayType="warning"
			withClose={false}
		>
			<ClayLabel.ItemBefore>
				<ClayIcon symbol="megaphone" />
			</ClayLabel.ItemBefore>

			<ClayLabel.ItemExpand>
				{Liferay.Language.get('campaign-touch')}
			</ClayLabel.ItemExpand>
		</ClayLabel>
	</span>
);

const DeviceIcon: FC<{browserName?: string; device?: string}> = ({
	browserName,
	device = '',
}) => {
	const {title, ...otherIconAttributes} =
		(DEVICE_ICONS_MAP as any)[device.toLowerCase()] || DEVICE_ICONS_MAP.any;

	return (
		<span
			className="device-icon align-items-center d-inline-flex flex-shrink-0"
			data-tooltip
			data-tooltip-align="bottom"
			title={[title, browserName].filter(Boolean).join('\n')}
		>
			<ClayIcon
				className="icon-root text-secondary"
				{...otherIconAttributes}
			/>
		</span>
	);
};

const RowTime: FC<{time?: moment.Moment; timeZoneId: string}> = ({
	time,
	timeZoneId,
}) => (
	<span className="row-time text-secondary flex-shrink-0 font-weight-semi-bold text-right">
		{time && formatDateToTimeZone(time, TIME_FORMAT, timeZoneId)}
	</span>
);

/**
 * Names the data source the session came from: `DXP` for anything Liferay
 * produced, the application id itself for an external source reaching Analytics
 * Cloud through a webhook.
 */
const DataSourceLabel: FC<{applicationId?: string; isWebhook: boolean}> = ({
	applicationId,
	isWebhook,
}) =>
	applicationId ? (
		<ClayLabel
			className={getCN(
				'data-source-label',
				'flex-shrink-0',
				'font-weight-semi-bold',
				'm-0'
			)}
			displayType={isWebhook ? 'success' : 'info'}
		>
			<strong>
				{normalizeApplicationId(applicationId).toUpperCase()}
			</strong>
		</ClayLabel>
	) : null;

const BecameKnownLabel: FC = () => (
	<ClayLabel
		className="became-known-label flex-shrink-0 font-weight-semi-bold m-0"
		displayType="success"
		withClose={false}
	>
		<ClayLabel.ItemBefore>
			<ClayIcon symbol="user" />
		</ClayLabel.ItemBefore>

		<ClayLabel.ItemExpand>
			{Liferay.Language.get('became-known')}
		</ClayLabel.ItemExpand>
	</ClayLabel>
);

const ExternalLink: FC<{url: string}> = ({url}) => (
	<ClayLink
		className="subtitle align-items-center align-self-start d-inline-flex font-weight-normal mw-100 text-secondary"
		href={url}
		rel="noopener noreferrer"
		target="_blank"
	>
		<TextTruncate title={url} />

		<ClayIcon className="ml-2" fontSize={12} symbol="shortcut" />
	</ClayLink>
);

const RowAttributes: FC<{payload: Record<string, unknown>}> = ({payload}) => (
	<code className="attributes-payload text-secondary d-block w-100">
		{JSON.stringify(payload, null, 2)}
	</code>
);

const DayRow: FC<{item: VerticalTimelineHeader}> = ({
	item: {title, totalEvents},
}) => (
	<li className="timeline-row day-row p-3 bg-white w-100 d-flex align-items-center">
		<ClayIcon
			className="day-icon icon-root text-secondary mr-2"
			symbol="calendar"
		/>

		<span className="title text-dark">{title}</span>

		<EventCountPill totalEvents={totalEvents} />
	</li>
);

/**
 * The individual a group of sessions belongs to: a plain, unexpandable row —
 * no caret, no click handler — ahead of that individual's sessions for the
 * day.
 */
const IndividualRow: FC<{item: VerticalTimelineIndividual}> = ({
	item: {individualId, individualName, individualUrl, isAnonymous},
}) => (
	<li className="timeline-row individual-row bg-white w-100">
		<div className="row-content flex-fill d-flex align-items-center">
			<ClaySticker className="individual-sticker" shape="user-icon">
				<ClayIcon
					color="gray"
					symbol={isAnonymous ? 'anonymize' : 'user'}
				/>
			</ClaySticker>

			<div className="individual-info">
				{individualUrl ? (
					<ClayLink className="individual-name" href={individualUrl}>
						<Text color="primary" size={3} weight="semi-bold">
							{individualName}
						</Text>
					</ClayLink>
				) : (
					<span className="individual-name">
						<Text color="primary" size={3} weight="semi-bold">
							{individualName}
						</Text>
					</span>
				)}

				{individualId && (
					<div className="individual-id">
						<Text color="secondary" size={3} weight="normal">
							{individualId}
						</Text>
					</div>
				)}
			</div>
		</div>
	</li>
);

/**
 * A session. Expanding it reveals its raw attributes (browser, device, screen
 * size…) — the pages visited during the session are not gated behind that
 * expand; they always render below, so the stream reads as a list of visited
 * pages without an extra click.
 */
const SessionRow: FC<IRowProps<VerticalTimelineSession>> = ({
	LDPEnabled,
	initialExpanded,
	item: {
		applicationId,
		attributes,
		becameKnown,
		browserName,
		device,
		endTime,
		nestedItems,
		noTimestamps,
		time,
		totalEvents,
		userAgent,
	},
	timeZoneId,
}) => {
	const [expanded, setExpanded] = useState<boolean>(!!initialExpanded);

	const getEndLabel = () => {
		if (noTimestamps) {
			return Liferay.Language.get('no-timestamps').toLowerCase();
		}

		if (endTime) {
			return formatDateToTimeZone(endTime, TIME_FORMAT, timeZoneId);
		}

		return Liferay.Language.get('in-progress').toLowerCase();
	};

	return (
		<li
			className={getCN(
				'timeline-row',
				'session-row',
				'bg-white',
				'w-100',
				{expanded}
			)}
		>
			<RowMain
				expanded={expanded}
				onToggle={() => setExpanded(!expanded)}
			>
				<div className="row-content flex-fill">
					<span className="title text-dark">
						{sub(Liferay.Language.get('session-x-x'), [
							time
								? formatDateToTimeZone(
										time,
										TIME_FORMAT,
										timeZoneId
									)
								: '',
							getEndLabel(),
						])}
					</span>
				</div>

				<div className="row-details ml-auto pl-3 d-flex align-items-center">
					{becameKnown && <BecameKnownLabel />}

					{LDPEnabled && (
						<DataSourceLabel
							applicationId={applicationId}
							isWebhook={isWebhookUserAgent(userAgent)}
						/>
					)}

					<EventCountPill totalEvents={totalEvents} />

					<DeviceIcon browserName={browserName} device={device} />
				</div>
			</RowMain>

			{expanded && <RowAttributes payload={attributes} />}

			{!!nestedItems.length && (
				<VerticalTimeline
					items={nestedItems}
					LDPEnabled={LDPEnabled}
					nested
					timeZoneId={timeZoneId}
				/>
			)}
		</li>
	);
};

/**
 * Every event of one visited page, collapsed into a single row: the page title
 * links to its dashboard, the URL opens the page itself, and the pill counts the
 * events the row stands for. Expanding it reveals those events.
 */
const PageGroupRow: FC<IRowProps<VerticalTimelinePageGroup>> = ({
	LDPEnabled,
	item: {
		campaign,
		descriptionUrl,
		nestedItems,
		subtitle,
		time,
		title,
		totalEvents,
	},
	timeZoneId,
}) => {
	const [expanded, setExpanded] = useState<boolean>(false);

	return (
		<li
			className={getCN('timeline-row', 'page-row', 'bg-white', 'w-100', {
				expanded,
			})}
		>
			<RowMain
				expanded={expanded}
				onToggle={() => setExpanded(!expanded)}
			>
				<div className="row-content flex-fill">
					<div className="page-row-header">
						<RowTime time={time} timeZoneId={timeZoneId} />

						<ClayIcon
							className="row-icon icon-root text-secondary mt-0 flex-shrink-0"
							symbol="page"
						/>

						{descriptionUrl ? (
							<ClayLink
								className="title text-dark"
								href={descriptionUrl}
							>
								<TextTruncate title={title} />
							</ClayLink>
						) : (
							<span className="title text-dark">
								<TextTruncate title={title} />
							</span>
						)}
					</div>

					<div className="page-info">
						{subtitle && <ExternalLink url={subtitle} />}

						<div className="row-metrics d-flex align-items-center">
							<EventCountPill totalEvents={totalEvents} />

							{campaign && <CampaignLabel campaign={campaign} />}
						</div>
					</div>
				</div>
			</RowMain>

			{expanded && !!nestedItems.length && (
				<VerticalTimeline
					items={nestedItems}
					LDPEnabled={LDPEnabled}
					nested
					timeZoneId={timeZoneId}
				/>
			)}
		</li>
	);
};

/**
 * A single event. Expanding it reveals its raw attributes.
 */
const EventRow: FC<IRowProps<SessionEvent>> = ({
	item: {
		attributes,
		campaign,
		description,
		descriptionUrl,
		subtitle,
		time,
		title,
	},
	timeZoneId,
}) => {
	const [expanded, setExpanded] = useState<boolean>(false);

	return (
		<li
			className={getCN('timeline-row', 'event-row', 'bg-white', 'w-100', {
				expanded,
			})}
		>
			<RowMain
				expanded={expanded}
				onToggle={() => setExpanded(!expanded)}
			>
				<div className="row-content flex-fill">
					<div className="event-header">
						<RowTime time={time} timeZoneId={timeZoneId} />

						<span className="title text-dark">
							<TextTruncate title={title} />
						</span>
					</div>

					<div className="event-info">
						{description && (
							<div className="description align-self-start font-weight-normal mw-100 text-secondary">
								{descriptionUrl ? (
									<ClayLink
										className="description-link font-weight-normal text-secondary"
										href={descriptionUrl}
									>
										<TextTruncate title={description} />
									</ClayLink>
								) : (
									<TextTruncate title={description} />
								)}
							</div>
						)}

						{subtitle && <ExternalLink url={subtitle} />}

						{campaign && (
							<div className="row-metrics d-flex align-items-center">
								<CampaignLabel campaign={campaign} />
							</div>
						)}
					</div>
				</div>
			</RowMain>

			{expanded && <RowAttributes payload={attributes} />}
		</li>
	);
};

const TimelineRow: FC<IRowProps<ITEM_SHAPE>> = (props) => {
	const {item} = props;

	if ('header' in item) {
		return <DayRow item={item} />;
	}

	if ('individual' in item) {
		return <IndividualRow item={item} />;
	}

	if ('session' in item) {
		return <SessionRow {...props} item={item} />;
	}

	if ('pageGroup' in item) {
		return <PageGroupRow {...props} item={item} />;
	}

	return <EventRow {...props} item={item} />;
};

type IVerticalTimelineProps = {
	initialExpanded?: boolean;
	items: ITEM_SHAPE[];
	LDPEnabled?: boolean;
	loading?: boolean;
	nested?: boolean;
	timeZoneId: string;
};

/**
 * Renders the activity stream as a list of rows: day, session, visited page and
 * event. Each level reveals the next one when expanded, so the stream reads as a
 * summary until the marketer drills into it.
 */
const VerticalTimeline: FC<IVerticalTimelineProps> = ({
	initialExpanded,
	items = [],
	LDPEnabled = true,
	loading = false,
	nested = false,
	timeZoneId,
}) =>
	loading ? (
		<Loading />
	) : (
		<div className="vertical-timeline-root">
			<ul className={getCN('timeline-rows', {nested})}>
				{items.map((item, i) => (
					<TimelineRow
						initialExpanded={initialExpanded}
						item={item}
						key={i}
						LDPEnabled={LDPEnabled}
						timeZoneId={timeZoneId}
					/>
				))}
			</ul>
		</div>
	);

export default VerticalTimeline;
