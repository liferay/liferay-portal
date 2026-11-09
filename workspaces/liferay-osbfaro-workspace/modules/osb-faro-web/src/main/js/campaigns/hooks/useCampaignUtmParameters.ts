import * as API from 'shared/api';
import {omit, pickBy} from 'lodash';
import {ICustomUtmParameter, UtmParameters} from 'campaigns/utils/types';
import {groupUtmParameters} from 'campaigns/utils/utmParameters';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {useRequest} from 'shared/hooks/useRequest';

export interface IUseCampaignUtmParameters {
	customParameters: ICustomUtmParameter[];
	error: boolean;
	loading: boolean;
	refetch: () => void;
	utmParameters: UtmParameters;
}

export const useCampaignUtmParameters = ({
	campaignId,
	channelId,
	groupId,
}: {
	campaignId: string;
	channelId: string;
	groupId: string;
}): IUseCampaignUtmParameters => {
	const rangeQueryValues = pickBy(useQueryRangeSelectors());

	// `CUSTOM` is a client side sentinel the API answers with a 500, so a
	// custom range travels as its bounds alone.

	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: API.campaigns.fetchCampaignAcquisitionParameters,
		variables: {
			channelId,
			groupId,
			id: campaignId,
			rangeQueryValues:
				rangeQueryValues.rangeKey === RangeKeyTimeRanges.CustomRange
					? omit(rangeQueryValues, 'rangeKey')
					: rangeQueryValues,
		},
	});

	return {
		...groupUtmParameters(data ?? []),
		error,
		loading,
		refetch,
	};
};
