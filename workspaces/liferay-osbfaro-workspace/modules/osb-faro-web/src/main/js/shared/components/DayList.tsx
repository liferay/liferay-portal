import ActivitySection from 'shared/components/ActivitySection';
import CampaignList from 'shared/components/CampaignList';
import DateHeader from 'shared/components/DateHeader';
import React, {FC} from 'react';
import VerticalTimeline from 'shared/components/VerticalTimeline';
import {CampaignDays} from 'shared/hooks/useCampaignTouchesByDay';
import {ENABLE_DAY_LEVEL_ACTIVITY} from 'shared/util/feature-flags';
import {TimelineDay, toDayKey} from 'shared/util/activities';

type IDayListProps = {
	campaignDays?: CampaignDays;
	individualUrls?: Record<string, string>;
	initialExpanded?: boolean;
	items?: TimelineDay[];
	LDPEnabled?: boolean;
	onCampaignDeltaChange?: (date: string, delta: number) => void;
	onCampaignPageChange?: (date: string, page: number) => void;
	timeZoneId: string;
};

const DayList: FC<IDayListProps> = ({
	campaignDays = {},
	individualUrls,
	initialExpanded,
	items = [],
	LDPEnabled = true,
	onCampaignDeltaChange,
	onCampaignPageChange,
	timeZoneId,
}) => (
	<div className="day-list-root">
		{items.map(({date, header, items: dayItems}) => {
			const campaignDay = campaignDays[toDayKey(date)];

			return (
				<div className="day-list-day" key={date}>
					<DateHeader
						title={header.title}
						totalEvents={header.totalEvents}
						totalTouches={campaignDay?.touchesCount}
					/>

					{ENABLE_DAY_LEVEL_ACTIVITY &&
						!!campaignDay?.campaigns.length && (
							<ActivitySection
								label={Liferay.Language.get('day-level')}
							>
								<CampaignList
									campaigns={campaignDay.campaigns}
									individualUrls={individualUrls}
									onDeltaChange={(delta) =>
										onCampaignDeltaChange?.(date, delta)
									}
									onPageChange={(page) =>
										onCampaignPageChange?.(date, page)
									}
									page={campaignDay.page}
									selectedDelta={campaignDay.delta}
									totalItems={campaignDay.campaignsCount}
								/>
							</ActivitySection>
						)}

					{!!dayItems.length && (
						<ActivitySection
							label={Liferay.Language.get('timed-activity')}
						>
							<VerticalTimeline
								initialExpanded={initialExpanded}
								items={dayItems}
								LDPEnabled={LDPEnabled}
								timeZoneId={timeZoneId}
							/>
						</ActivitySection>
					)}
				</div>
			);
		})}
	</div>
);

export default DayList;
