import ClayIcon from '@clayui/icon';
import EventCountPill from 'shared/components/EventCountPill';
import React, {FC} from 'react';

type IDateHeaderProps = {
	title: string;
	totalEvents?: number;
	totalTouches?: number;
};

const DateHeader: FC<IDateHeaderProps> = ({
	title,
	totalEvents,
	totalTouches,
}) => (
	<div className="date-header bg-white w-100 d-flex align-items-center">
		<ClayIcon className="day-icon icon-root mr-2" symbol="calendar" />

		<span className="title">{title}</span>

		<div className="date-header-counts d-flex">
			<EventCountPill totalEvents={totalEvents} />

			<EventCountPill symbol="comments" totalEvents={totalTouches} />
		</div>
	</div>
);

export default DateHeader;
