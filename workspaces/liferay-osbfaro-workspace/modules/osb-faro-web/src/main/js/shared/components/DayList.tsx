import ActivitySection from 'shared/components/ActivitySection';
import DateHeader from 'shared/components/DateHeader';
import React, {FC} from 'react';
import VerticalTimeline from 'shared/components/VerticalTimeline';
import {TimelineDay} from 'shared/util/activities';

type IDayListProps = {
	initialExpanded?: boolean;
	items?: TimelineDay[];
	LDPEnabled?: boolean;
	timeZoneId: string;
};

const DayList: FC<IDayListProps> = ({
	initialExpanded,
	items = [],
	LDPEnabled = true,
	timeZoneId,
}) => (
	<div className="day-list-root">
		{items.map(({header, items: dayItems}) => (
			<div className="day-list-day" key={header.title}>
				<DateHeader
					title={header.title}
					totalEvents={header.totalEvents}
				/>

				<ActivitySection label={Liferay.Language.get('timed-activity')}>
					<VerticalTimeline
						initialExpanded={initialExpanded}
						items={dayItems}
						LDPEnabled={LDPEnabled}
						timeZoneId={timeZoneId}
					/>
				</ActivitySection>
			</div>
		))}
	</div>
);

export default DayList;
