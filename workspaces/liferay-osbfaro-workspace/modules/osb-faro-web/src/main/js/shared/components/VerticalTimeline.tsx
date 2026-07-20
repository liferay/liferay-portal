import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React, {FC, useState} from 'react';
import Sticker from './Sticker';
import TextTruncate from './TextTruncate';
import {Colors} from 'shared/util/colors-size';
import {formatDateToTimeZone} from 'shared/util/date';
import {Link} from 'react-router-dom';

const DEVICE_ICONS_MAP = {
	any: {
		color: Colors.MainLighten65,
		id: 'anyIcon',
		symbol: 'devices',
		title: Liferay.Language.get('unknown-device'),
	},
	desktop: {symbol: 'desktop', title: Liferay.Language.get('desktop')},
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

const LIFERAY_DXP_APPLICATION_IDS = new Set([
	'Blog',
	'Comment',
	'CustomEvent',
	'Document',
	'Form',
	'Layout',
	'ObjectEntry',
	'Page',
	'Ratings',
	'WebContent',
]);

const normalizeApplicationId = (applicationId: string): string =>
	LIFERAY_DXP_APPLICATION_IDS.has(applicationId) ? 'DXP' : applicationId;

type ITEM_SHAPE = {
	applicationId: string;
	attributes: Record<string, unknown>;
	browserName: string;
	description: string;
	descriptionUrl?: string;
	device: string;
	endTime: number;
	groupEnd?: boolean;
	groupStart?: boolean;
	header: boolean;
	isAnonymous?: boolean;
	nestedItems: ITEM_SHAPE[];
	subtitle: string;
	time: string;
	title: string;
	totalEvents: number;
	url: string;
	userAgent: string;
	userHeader?: boolean;
	userHeaderUrl?: string;
};

type ITimelineItemProps = {
	channelId?: string;
	className?: string;
	groupId?: string;
	initialExpanded?: boolean;
	item: ITEM_SHAPE;
	LDPEnabled?: boolean;
	timeZoneId: string;
};

const TimelineItem: FC<ITimelineItemProps> = ({
	LDPEnabled,
	className,
	initialExpanded = false,
	item: {
		applicationId,
		attributes,
		browserName,
		description,
		descriptionUrl,
		device,
		endTime,
		groupEnd,
		groupStart,
		header,
		isAnonymous,
		nestedItems,
		subtitle,
		time,
		title,
		totalEvents,
		url,
		userAgent,
		userHeader,
		userHeaderUrl,
	},
	timeZoneId,
}) => {
	const [expanded, setExpanded] = useState<boolean>(initialExpanded);
	const expandable = !!attributes;

	if (userHeader) {
		return (
			<li className={getCN('timeline-item', 'user-header', className)}>
				<div className="timeline-panel">
					<div className="timeline-panel-body">
						<div className="timeline-panel-body-content user-header-content">
							<ClaySticker
								className="user-header-sticker mr-2"
								shape="user-icon"
								size="sm"
							>
								<ClayIcon
									color="gray"
									symbol={isAnonymous ? 'anonymize' : 'user'}
								/>
							</ClaySticker>

							{userHeaderUrl ? (
								<ClayLink
									className="user-header-name"
									href={userHeaderUrl}
								>
									{title}
								</ClayLink>
							) : (
								<span className="user-header-name">
									{title}
								</span>
							)}
						</div>
					</div>
				</div>
			</li>
		);
	}

	return (
		<li
			className={getCN('timeline-item', className, {
				expanded,
				'group-end': groupEnd,
				'group-start': groupStart,
				header,
			})}
		>
			<div className="timeline-panel">
				<div className="timeline-panel-body">
					{!header && (
						<TimelineElement
							endTime={endTime}
							nestedItems={nestedItems}
							time={time}
							timeZoneId={timeZoneId}
							userAgent={userAgent}
						/>
					)}

					<TimelinePanelBody
						expandable={expandable}
						expanded={expanded}
						setExpanded={setExpanded}
					>
						<TimelinePanelBodyContentText
							className={getCN(
								'timeline-panel-body-content-text',
								{
									header: !title,
								}
							)}
							description={description}
							descriptionUrl={descriptionUrl}
							header={header}
							subtitle={subtitle}
							title={title}
							totalEvents={totalEvents}
							url={url}
						/>

						{expandable && !!nestedItems && (
							<TimelinePanelBodyContentDetails
								applicationId={applicationId}
								browserName={browserName}
								device={device}
								itemCount={nestedItems.length}
								LDPEnabled={LDPEnabled}
								userAgent={userAgent}
							/>
						)}

						{!header && (
							<ClayIcon
								className="icon-root"
								symbol={expanded ? 'caret-top' : 'caret-bottom'}
							/>
						)}
					</TimelinePanelBody>

					{expanded && !!attributes && (
						<TimelineItemAttributes payload={attributes} />
					)}
				</div>

				{nestedItems && (
					<VerticalTimeline
						items={nestedItems}
						LDPEnabled={LDPEnabled}
						nested
						timeZoneId={timeZoneId}
					/>
				)}
			</div>
		</li>
	);
};

const TimelinePanelBody: FC<{
	children?: React.ReactNode;
	expandable: boolean;
	expanded: boolean;
	setExpanded: (expandable: boolean) => void;
}> = ({children, expandable, expanded, setExpanded}) => {
	const toggleExpand = () => {
		if (expandable) {
			setExpanded(!expanded);
		}
	};

	const bodyAttributes = expandable
		? {
				onClick: toggleExpand,
				onKeyPress: toggleExpand,
				role: 'button',
				tabIndex: 0,
			}
		: {};

	const bodyClasses = getCN('timeline-panel-body-content', {
		selectable: expandable,
	});

	return (
		<div className={bodyClasses} {...bodyAttributes}>
			{children}
		</div>
	);
};

const TimelinePanelBodyContentDetails: FC<{
	applicationId: string;
	browserName: string;
	device: string;
	itemCount: number;
	LDPEnabled?: boolean;
	userAgent: string;
}> = ({
	LDPEnabled,
	applicationId,
	browserName,
	device,
	itemCount,
	userAgent,
}) => {
	const {title: deviceIconTitle, ...otherIconAttributes} =
		(DEVICE_ICONS_MAP as any)[device.toLowerCase()] || DEVICE_ICONS_MAP.any;

	const isWebhook = userAgent?.toLowerCase().includes('webhook');

	return (
		<div className="timeline-panel-body-content-details">
			<div className="align-items-center d-flex icon-group">
				{LDPEnabled && applicationId && (
					<div>
						<ClayLabel
							className={getCN('label-lg mr-5', {
								'label-info': !isWebhook,
								'label-success': isWebhook,
							})}
							displayType={isWebhook ? 'success' : 'info'}
						>
							<strong>
								{normalizeApplicationId(
									applicationId
								).toUpperCase()}
							</strong>
						</ClayLabel>

						<ClayIcon
							className="icon-root text-secondary"
							fontSize={16}
							symbol="click"
						/>
					</div>
				)}

				<span className="font-weight-semibold item-count text-secondary">
					{itemCount}
				</span>

				<span
					className="device-icon mr-6"
					data-tooltip
					data-tooltip-align="bottom"
					title={`${deviceIconTitle}\n${browserName}`}
				>
					<ClayIcon
						className="icon-root text-secondary"
						{...otherIconAttributes}
					/>
				</span>
			</div>
		</div>
	);
};

const TimelinePanelBodyContentText: FC<{
	className: string;
	description: string;
	descriptionUrl?: string;
	header: boolean;
	subtitle: string;
	title: string;
	totalEvents: number;
	url: string;
}> = ({
	className,
	description,
	descriptionUrl,
	header,
	subtitle,
	title,
	totalEvents,
	url,
}) => {
	const eventTitle =
		title && !header ? <TextTruncate title={`${title}`} /> : title;

	return (
		<div className={className}>
			{url ? (
				<span className="text-truncate">
					<Link className="title" to={url}>
						{eventTitle}
					</Link>
				</span>
			) : (
				<span className="title">{eventTitle}</span>
			)}

			{!header && description && (
				<div className="description">
					{descriptionUrl ? (
						<ClayLink className="subtitle" href={descriptionUrl}>
							<TextTruncate title={description} />
						</ClayLink>
					) : (
						<TextTruncate title={description} />
					)}
				</div>
			)}

			{header && (
				<span className="item-count text-secondary">
					<ClayIcon
						className="event-icon icon-root mr-2"
						symbol="click"
					/>

					{totalEvents}
				</span>
			)}

			{subtitle && (
				<ClayLink
					className="subtitle"
					href={subtitle}
					rel="noopener noreferrer"
					target="_blank"
				>
					<TextTruncate title={subtitle} />
				</ClayLink>
			)}
		</div>
	);
};

const TimelineElement: FC<{
	endTime: number;
	nestedItems: ITEM_SHAPE[];
	time: string;
	timeZoneId: string;
	userAgent: string;
}> = ({endTime, nestedItems, time, timeZoneId, userAgent}) => {
	const isSession = !!nestedItems;

	const timeRange = !nestedItems ? (
		formatDateToTimeZone(time, 'h:mma', timeZoneId)
	) : (
		<>
			<span>{formatDateToTimeZone(time, 'h:mma', timeZoneId)}</span>
			{' - '}
			<span>
				{endTime
					? formatDateToTimeZone(endTime, 'h:mma', timeZoneId)
					: Liferay.Language.get('in-progress').toLowerCase()}
			</span>
		</>
	);

	return (
		<>
			<div className="timeline-line" />

			<div
				className={getCN('timeline-increment', {
					'timeline-increment-dxp':
						isSession &&
						!userAgent?.toLowerCase().includes('webhook'),
					'timeline-increment-webhook':
						isSession &&
						userAgent?.toLowerCase().includes('webhook'),
				})}
			>
				<Sticker circle display="point" size="lg" />

				{time && (
					<div className="timeline-item-label timeline-time-label label-root">
						{timeRange}
					</div>
				)}
			</div>
		</>
	);
};

const TimelineItemAttributes: FC<{payload: Record<string, unknown>}> = ({
	payload,
}) => (
	<div className="timeline-panel-body-content">
		<code className="attributes-payload">
			{JSON.stringify(payload, null, 2)}
		</code>
	</div>
);

type IVerticalTimelineProps = {
	groupId?: string;
	initialExpanded?: boolean;
	items: ITEM_SHAPE[];
	LDPEnabled?: boolean;
	loading?: boolean;
	nested?: boolean;
	timeZoneId: string;
};

const VerticalTimeline: FC<IVerticalTimelineProps> = ({
	groupId,
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
			<ul
				className={getCN('timeline', 'timeline-center', {
					'timeline-nested': nested,
				})}
			>
				{items.map((item, i) => (
					<TimelineItem
						groupId={groupId}
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
