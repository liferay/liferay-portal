import * as API from 'shared/api';
import {createCampaignProperty} from '../utils/utils';
import {PaginatedSource} from '../criterion-types/RemoteCriterionType';

/**
 * Lists the property's campaigns in the sidebar's Campaigns section, one item
 * per campaign, with the same search box and pagination as Search Terms.
 */
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
