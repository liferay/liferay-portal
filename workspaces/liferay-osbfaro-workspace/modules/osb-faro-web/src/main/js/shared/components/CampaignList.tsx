import CampaignRow from 'shared/components/CampaignRow';
import PaginationBar from 'shared/components/PaginationBar';
import React, {FC} from 'react';
import {CampaignTouch} from 'shared/util/activities';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

const CAMPAIGNS_PER_PAGE = 8;

type ICampaignListProps = {
	campaigns: CampaignTouch[];
};

const CampaignList: FC<ICampaignListProps> = ({campaigns}) => {
	const {delta, onDeltaChange, onPageChange, page} = useStatefulPagination(
		undefined,
		{initialDelta: CAMPAIGNS_PER_PAGE}
	);

	const start = (page - 1) * delta;

	return (
		<>
			<div className="vertical-timeline-root">
				<ul className="timeline-rows">
					{campaigns.slice(start, start + delta).map((campaign) => (
						<CampaignRow
							campaign={campaign}
							key={campaign.campaignId}
						/>
					))}
				</ul>
			</div>

			{!!campaigns.length && (
				<PaginationBar
					href={window.location.href}
					onDeltaChange={onDeltaChange}
					onPageChange={onPageChange}
					page={page}
					selectedDelta={delta}
					totalItems={campaigns.length}
				/>
			)}
		</>
	);
};

export default CampaignList;
