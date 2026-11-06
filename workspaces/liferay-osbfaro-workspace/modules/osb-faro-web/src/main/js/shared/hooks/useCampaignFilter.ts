import {setUriQueryValues} from 'shared/util/router';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useQueryParams} from 'shared/hooks/useQueryParams';

type Campaign = {
	id: string;
	name: string;
};

interface ICampaignFilter {
	campaignId?: string;
	campaignName?: string;
	setCampaign: (campaign: Campaign | null) => void;
}

/**
 * Reads the campaign filter from the URL query and writes it back there. The
 * URL is the single source of truth, so the pages rendering the filter do not
 * mirror the selection in component state.
 */

export const useCampaignFilter = (): ICampaignFilter => {
	const {campaignId, campaignName} = useQueryParams();
	const history = useHistoryAdapter();

	return {
		campaignId,
		campaignName,
		setCampaign: (campaign) =>
			history.push(
				setUriQueryValues({
					campaignId: campaign?.id ?? null,
					campaignName: campaign?.name ?? null,
				})
			),
	};
};
