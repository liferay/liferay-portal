import * as API from 'shared/api';
import {useRequest} from 'shared/hooks/useRequest';

interface ICampaignNamesParams {
	channelId: string;
	groupId: string;
	ids: string[];
}

/**
 * A campaign that no longer exists in the property has a `null` name.
 */
const fetchCampaignNames = ({channelId, groupId, ids}: ICampaignNamesParams) =>
	Promise.all(
		ids.map((id) =>
			API.campaigns.fetchCampaign({channelId, groupId, id}).then(
				({campaignName}) => [id, campaignName],
				() => [id, null]
			)
		)
	).then(Object.fromEntries);

export const useCampaignNames = (params: ICampaignNamesParams) => {
	const {data, loading} = useRequest<
		ICampaignNamesParams,
		Record<string, string | null>
	>({
		dataSourceFn: fetchCampaignNames,
		skipRequest: !params.ids.length,
		variables: params,
	});

	return {
		loading: params.ids.length > 0 && loading,
		names: data ?? {},
	};
};
