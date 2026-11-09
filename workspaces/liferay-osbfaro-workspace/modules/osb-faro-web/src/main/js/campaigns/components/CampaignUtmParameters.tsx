import ErrorDisplay from 'shared/components/ErrorDisplay';
import Loading from 'shared/components/Loading';
import React from 'react';
import UtmParametersCard from './UtmParametersCard';
import {useCampaignUtmParameters} from 'campaigns/hooks/useCampaignUtmParameters';

interface ICampaignUtmParametersProps {
	campaignId: string;
	channelId: string;
	groupId: string;
}

/**
 * Fetches on its own so the UTM parameters never hold up the rest of the
 * campaign detail page.
 */
const CampaignUtmParameters: React.FC<ICampaignUtmParametersProps> = ({
	campaignId,
	channelId,
	groupId,
}) => {
	const {customParameters, error, loading, refetch, utmParameters} =
		useCampaignUtmParameters({campaignId, channelId, groupId});

	if (error) {
		return <ErrorDisplay onReload={refetch} spacer />;
	}

	if (loading) {
		return <Loading />;
	}

	return (
		<UtmParametersCard
			customParameters={customParameters}
			utmParameters={utmParameters}
		/>
	);
};

export default CampaignUtmParameters;
