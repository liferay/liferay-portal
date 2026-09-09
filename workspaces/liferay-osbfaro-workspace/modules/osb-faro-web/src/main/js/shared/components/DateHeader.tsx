import ClayIcon from '@clayui/icon';
import EventCountPill from 'shared/components/EventCountPill';
import React, {FC} from 'react';

type IDateHeaderProps = {
	title: string;
	totalEvents?: number;
};

const DateHeader: FC<IDateHeaderProps> = ({title, totalEvents}) => (
	<div className="date-header bg-white w-100 d-flex align-items-center">
		<ClayIcon className="day-icon icon-root mr-2" symbol="calendar" />

		<span className="title">{title}</span>

		<EventCountPill totalEvents={totalEvents} />
	</div>
);

export default DateHeader;
