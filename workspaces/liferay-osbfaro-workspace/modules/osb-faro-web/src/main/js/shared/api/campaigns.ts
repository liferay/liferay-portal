import sendRequest from 'shared/util/request';
import {Metric} from 'contacts/pages/account/utils/types';

/**
 * Mirrors `CampaignDisplay` on the Faro BFF, whose de-underscored field names
 * are the contract. The list is fetched by the data set itself through
 * `apiURL`, so it needs no request function here.
 */
export interface ICampaign {
	accountsTouched: number;
	campaignName: string;
	campaignType: string;
	endDate: string | number;
	id: string;
	individualsTouched: number;
	origin?: string;
	startDate: string | number;
	status: string;
}

export enum CampaignMetricType {
	AccountsTouched = 'accountsTouched',
	CampaignCount = 'campaignCount',
	ClosedWonAmount = 'closedWonAmount',
	OpenPipelineAmount = 'openPipelineAmount',
}

export interface ICampaignMetric extends Metric {
	metricType: CampaignMetricType;
}

interface IFetchCampaignMetrics {
	channelId: string;
	groupId: string;
}

/**
 * A plain list rather than a page: these are cards, not a table. Every value
 * is computed over a fixed 90 day window and compared against the window
 * before it, which is where each card's trend comes from.
 */
export async function fetchCampaignMetrics({
	channelId,
	groupId,
}: IFetchCampaignMetrics): Promise<ICampaignMetric[]> {
	return sendRequest({
		data: {channelId},
		method: 'GET',
		path: `contacts/${groupId}/campaigns/metrics`,
	});
}
