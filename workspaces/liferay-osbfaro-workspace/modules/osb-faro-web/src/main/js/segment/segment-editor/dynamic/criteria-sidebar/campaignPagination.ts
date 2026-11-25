import * as API from 'shared/api';
import {createCampaignProperty} from '../utils/utils';
import {PaginatedSource} from '../criterion-types/RemoteCriterionType';

export const campaignPagination: PaginatedSource = {
	api: ({channelId, groupId, keywords, page, pageSize}) =>
		API.campaigns.searchCampaignNames({
			channelId,
			groupId,
			page,
			pageSize,
			query: keywords,
		}),

	createProperty: createCampaignProperty,
};
