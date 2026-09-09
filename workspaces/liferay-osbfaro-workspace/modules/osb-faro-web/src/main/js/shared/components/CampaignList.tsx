import CampaignRow from 'shared/components/CampaignRow';
import PaginationBar from 'shared/components/PaginationBar';
import React, {FC} from 'react';
import {CampaignTouch} from 'shared/util/activities';

type ICampaignListProps = {
	campaigns: CampaignTouch[];
	onDeltaChange: (delta: number) => void;
	onPageChange: (page: number) => void;
	page: number;
	selectedDelta: number;
	totalItems: number;
};

const CampaignList: FC<ICampaignListProps> = ({
	campaigns,
	onDeltaChange,
	onPageChange,
	page,
	selectedDelta,
	totalItems,
}) => (
	<>
		<div className="vertical-timeline-root">
			<ul className="timeline-rows">
				{campaigns.map((campaign) => (
					<CampaignRow
						campaign={campaign}
						key={campaign.campaignId}
					/>
				))}
			</ul>
		</div>

		{!!totalItems && (
			<PaginationBar
				href={window.location.href}
				onDeltaChange={onDeltaChange}
				onPageChange={onPageChange}
				page={page}
				resultsMessagePlural={Liferay.Language.get(
					'showing-x-to-x-of-x-campaign-entries'
				)}
				resultsMessageSingular={Liferay.Language.get(
					'showing-x-to-x-of-x-campaign-entry'
				)}
				selectedDelta={selectedDelta}
				totalItems={totalItems}
			/>
		)}
	</>
);

export default CampaignList;
