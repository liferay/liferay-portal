import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import NoResultsDisplay from './NoResultsDisplay';
import React, {FC, useState} from 'react';
import Sticker from './Sticker';
import TextTruncate from './TextTruncate';
import {formatDateToTimeZone} from 'shared/util/date';
import {get} from 'lodash';
import {Link} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';

type TITLE_ELEMENT_ATTRIBUTES = {
	key: string;
	props: {
		children: string;
	};
	type: string;
	ref: string;
	_owner: string;
	_store: {};
};

type ITEM_SHAPE = {
	header: boolean;
	individual: object;
	nestedItems: ITEM_SHAPE[];
	subtitle: string | TITLE_ELEMENT_ATTRIBUTES[];
	symbol: string;
	time: number;
	title: string | TITLE_ELEMENT_ATTRIBUTES[];
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
	channelId,
	className,
	groupId,
	initialExpanded = true,
	item: {header, individual, nestedItems, subtitle, symbol, time, title, url},
	timeZoneId
}) => {
	const [expanded, setExpanded] = useState(initialExpanded);

	const expandable = nestedItems?.length;

	const toggleExpand = () => {
		if (expandable) {
			setExpanded(!expanded);
		}
	};

	const classes = getCN('timeline-item', className, {
		header
	});

	const bodyClasses = getCN('timeline-panel-body-content', {
		selectable: expandable
	});

	const bodyAttributes = expandable
		? {
				onClick: toggleExpand,
				onKeyPress: toggleExpand,
				role: 'button',
				tabIndex: 0
		  }
		: {};

	const individualName = get(individual, 'name');
	const individualId = get(individual, 'id');

	return (
		<li className={classes}>
			<div className='timeline-panel'>
				<div className='timeline-panel-body'>
					{!header && (
						<div className='timeline-increment'>
							<Sticker circle display='point' size='lg' />
						</div>
					)}

					{!!time && (
						<div className='timeline-item-label'>
							{formatDateToTimeZone(time, 'h:mma', timeZoneId)}
						</div>
					)}

					<div className={bodyClasses} {...bodyAttributes}>
						{symbol && (
							<div className='sticker-container'>
								<Sticker display='light' symbol={symbol} />
							</div>
						)}

						<div className='timeline-panel-body-content-text'>
							<div>
								{!!individualName && (
									<Link
										className='entity-link'
										to={toRoute(
											Routes.CONTACTS_INDIVIDUAL,
											{
												channelId,
												groupId,
												id: individualId
											}
										)}
									>
										{individualName}
									</Link>
								)}

								<span className='text-truncate'>
									{url ? (
										<Link className='title' to={url}>
											{title}
										</Link>
									) : (
										<span className='title'>{title}</span>
									)}
								</span>
							</div>

							{subtitle && (
								<TextTruncate
									className='subtitle'
									title={subtitle}
								/>
							)}
						</div>

						{expandable && (
							<div className='timeline-panel-body-content-details'>
								<span className='item-count'>
									{nestedItems.length}
								</span>

								<ClayIcon
									className='icon-root'
									symbol={
										expanded ? 'caret-bottom' : 'caret-top'
									}
								/>
							</div>
						)}
					</div>
				</div>

				{expanded && nestedItems && (
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

type HEADER_LABEL_SHAPE = {
	count: string;
	label: string;
	title: string;
};

type IVerticalTimelineProps = {
	groupId?: string;
	headerLabels?: HEADER_LABEL_SHAPE;
	initialExpanded?: boolean;
	items: ITEM_SHAPE[];
	loading?: boolean;
	nested?: boolean;
	timeZoneId: string;
};

const VerticalTimeline: FC<IVerticalTimelineProps> = ({
	groupId,
	headerLabels,
	initialExpanded = true,
	items = [],
	loading = false,
	nested = false,
	timeZoneId
}) => {
	const {count, label, title} = headerLabels || {};
	const classes = getCN('timeline', 'timeline-center', {
		'timeline-nested': nested
	});

	if (loading) {
		return <Loading />;
	} else if (!items.length && !nested) {
		return (
			<NoResultsDisplay
				description={Liferay.Language.get(
					'please-try-a-different-search-term'
				)}
				icon={{
					border: false,
					size: Sizes.XXXLarge,
					symbol: 'ac_no_results_found'
				}}
				spacer
				title={Liferay.Language.get('there-are-no-results-found')}
			/>
		);
	} else {
		return (
			<div className='vertical-timeline-root-deprecated'>
				{title && (
					<div className='timeline-header'>
						<div className='header-label'>{label}</div>

						<div className='header-title'>{title}</div>

						<div className='header-count'>{count}</div>
					</div>
				)}

				<ul className={classes}>
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
	}
};

export default VerticalTimeline;
