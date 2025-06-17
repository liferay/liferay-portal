import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React, {FC, useState} from 'react';
import Sticker from './Sticker';
import TextTruncate from './TextTruncate';
import {Colors} from 'shared/util/colors-size';
import {formatDateToTimeZone} from 'shared/util/date';
import {Link} from 'react-router-dom';
import {UserSessionAttributes} from 'shared/util/activities';

const DEVICE_ICONS_MAP = {
	any: {
		color: Colors.MainLighten65,
		id: 'anyIcon',
		symbol: 'devices',
		title: Liferay.Language.get('unknown-device')
	},
	desktop: {symbol: 'ac_display', title: Liferay.Language.get('desktop')},
	mobile: {symbol: 'mobile-portrait', title: Liferay.Language.get('mobile')},
	smartphone: {
		symbol: 'mobile-portrait',
		title: Liferay.Language.get('mobile')
	},
	tablet: {
		symbol: 'ac_tablet_landscape',
		title: Liferay.Language.get('tablet')
	}
};

const ATTRIBUTE_CLASSES_MAP = {
	title: 'attribute-important'
};

type ITEM_SHAPE = {
	attributes: UserSessionAttributes;
	browserName: string;
	description: string;
	device: string;
	endTime: number;
	header: boolean;
	nestedItems: ITEM_SHAPE[];
	subtitle: string;
	time: string;
	title: string;
	totalEvents: number;
	url: string;
};

type ITimelineItemProps = {
	channelId?: string;
	className?: string;
	groupId?: string;
	initialExpanded?: boolean;
	item: ITEM_SHAPE;
	timeZoneId: string;
};

const TimelineItem: FC<ITimelineItemProps> = ({
	className,
	initialExpanded,
	item: {
		attributes,
		browserName,
		description,
		device,
		endTime,
		header,
		nestedItems,
		subtitle,
		time,
		title,
		totalEvents,
		url
	},
	timeZoneId
}) => {
	const [expanded, setExpanded] = useState(initialExpanded);
	const expandable = !!attributes;

	return (
		<li
			className={getCN('timeline-item', className, {
				expanded,
				header
			})}
		>
			<div className='timeline-panel'>
				<div className='timeline-panel-body'>
					{!header && (
						<TimelineElement
							endTime={endTime}
							nestedItems={nestedItems}
							time={time}
							timeZoneId={timeZoneId}
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
									header: !title
								}
							)}
							description={description}
							header={header}
							subtitle={subtitle}
							title={title}
							totalEvents={totalEvents}
							url={url}
						/>

						{expandable && !!nestedItems && (
							<TimelinePanelBodyContentDetails
								browserName={browserName}
								device={device}
								itemCount={nestedItems.length}
							/>
						)}

						{!header && (
							<ClayIcon
								className='icon-root'
								symbol={expanded ? 'caret-top' : 'caret-bottom'}
							/>
						)}
					</TimelinePanelBody>

					{expanded && (
						<TimelineItemAttributes attributes={attributes} />
					)}
				</div>

				{nestedItems && (
					<VerticalTimeline
						items={nestedItems}
						nested
						timeZoneId={timeZoneId}
					/>
				)}
			</div>
		</li>
	);
};

const TimelinePanelBody: FC<{
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
				tabIndex: 0
		  }
		: {};

	const bodyClasses = getCN('timeline-panel-body-content', {
		selectable: expandable
	});

	return (
		<div className={bodyClasses} {...bodyAttributes}>
			{children}
		</div>
	);
};

const TimelinePanelBodyContentDetails: FC<{
	browserName: string;
	device: string;
	itemCount: number;
}> = ({browserName, device, itemCount}) => {
	const {title: deviceIconTitle, ...otherIconAttributes} =
		DEVICE_ICONS_MAP[device.toLowerCase()] || DEVICE_ICONS_MAP.any;

	return (
		<div className='timeline-panel-body-content-details'>
			<div className='icon-group'>
				<ClayIcon
					className='event-icon icon-root'
					symbol='ac_event_icon'
				/>

				<span className='item-count'>{itemCount}</span>

				<span
					className='device-icon'
					data-tooltip
					data-tooltip-align='bottom'
					title={`${deviceIconTitle}\n${browserName}`}
				>
					<ClayIcon
						className={getCN('icon-root', Colors.MainLighten28)}
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
	header: boolean;
	subtitle: string;
	title: string;
	totalEvents: number;
	url: string;
}> = ({className, description, header, subtitle, title, totalEvents, url}) => {
	const eventTitle =
		title && !header ? <TextTruncate title={`${title}`} /> : title;

	return (
		<div className={className}>
			{url ? (
				<span className='text-truncate'>
					<Link className='title' to={url}>
						{eventTitle}
					</Link>
				</span>
			) : (
				<span className='title'>{eventTitle}</span>
			)}

			{header && (
				<>
					<ClayIcon
						className='event-icon icon-root'
						symbol='ac_event_icon'
					/>

					<span className='item-count'>{totalEvents}</span>
				</>
			)}

			{description && <span className='description'>{description}</span>}

			{subtitle && <TextTruncate className='subtitle' title={subtitle} />}
		</div>
	);
};

const TimelineElement: FC<{
	endTime: number;
	nestedItems: ITEM_SHAPE[];
	time: string;
	timeZoneId: string;
}> = ({endTime, nestedItems, time, timeZoneId}) => {
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
			<div className='timeline-line' />

			<div className='timeline-increment'>
				<Sticker circle display='point' size='lg' />

				{time && (
					<div className='timeline-item-label timeline-time-label label-root'>
						{timeRange}
					</div>
				)}
			</div>
		</>
	);
};

const TimelineItemAttributes: FC<{attributes: UserSessionAttributes}> = ({
	attributes: {header: attributesTitle, ...otherValues} = {}
}) => (
	<div className='timeline-panel-body-content'>
		<div className='timeline-panel-body-content-text'>
			<div className='attributes-title'>
				<span className='label-root'>{attributesTitle}</span>
			</div>

			{Object.entries(otherValues).map(([key, value]) => (
				<div className='attributes-item' key={key}>
					<span className='attribute-key'>{`${key}`}</span>

					<TextTruncate
						className={getCN(
							'attribute-value',
							ATTRIBUTE_CLASSES_MAP[key]
						)}
						title={value || '""'}
					/>
				</div>
			))}
		</div>
	</div>
);

type IVerticalTimelineProps = {
	groupId?: string;
	initialExpanded?: boolean;
	items: ITEM_SHAPE[];
	loading?: boolean;
	nested?: boolean;
	timeZoneId: string;
};

const VerticalTimeline: FC<IVerticalTimelineProps> = ({
	groupId,
	initialExpanded,
	items = [],
	loading = false,
	nested = false,
	timeZoneId
}) =>
	loading ? (
		<Loading />
	) : (
		<div className='vertical-timeline-root'>
			<ul
				className={getCN('timeline', 'timeline-center', {
					'timeline-nested': nested
				})}
			>
				{items.map((item, i) => (
					<TimelineItem
						groupId={groupId}
						initialExpanded={initialExpanded}
						item={item}
						key={i}
						timeZoneId={timeZoneId}
					/>
				))}
			</ul>
		</div>
	);

export default VerticalTimeline;
