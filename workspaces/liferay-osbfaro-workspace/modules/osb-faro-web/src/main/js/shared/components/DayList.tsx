import ActivitySection from 'shared/components/ActivitySection';
import CampaignList from 'shared/components/CampaignList';
import DateHeader from 'shared/components/DateHeader';
import React, {FC} from 'react';
import VerticalTimeline from 'shared/components/VerticalTimeline';
import {TimelineDay} from 'shared/util/activities';

type IDayListProps = {
	emptyState?: React.ReactNode;
	initialExpanded?: boolean;
	items?: TimelineDay[];
	LDPEnabled?: boolean;
	timeZoneId: string;
};

const DayList: FC<IDayListProps> = ({
	emptyState,
	initialExpanded,
	items = [],
	LDPEnabled = true,
	timeZoneId,
}) => (
	<div className="day-list-root">
		{items.map(({campaigns, header, items: dayItems}) => (
			<div className="day-list-day" key={header.title}>
				<DateHeader
					title={header.title}
					totalEvents={header.totalEvents}
				/>

				<ActivitySection label={Liferay.Language.get('day-level')}>
					{campaigns?.length ? (
						<CampaignList campaigns={campaigns} />
					) : (
						emptyState
					)}
				</ActivitySection>

				<ActivitySection label={Liferay.Language.get('timed-activity')}>
					{dayItems.length ? (
						<VerticalTimeline
							initialExpanded={initialExpanded}
							items={dayItems}
							LDPEnabled={LDPEnabled}
							timeZoneId={timeZoneId}
						/>
					) : (
						emptyState
					)}
				</ActivitySection>
			</div>
		))}
	</div>
);

export default DayList;
