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

interface IFetchCampaign {
	channelId: string;
	groupId: string;
	id: string;
}

export async function fetchCampaign({
	channelId,
	groupId,
	id,
}: IFetchCampaign): Promise<ICampaign> {
	return sendRequest({
		data: {channelId},
		method: 'GET',
		path: `contacts/${groupId}/campaigns/${id}`,
	});
}

interface IFetchCampaignMetrics {
	channelId: string;
	groupId: string;
	rangeQueryValues?: {[key: string]: any};
}

/**
 * A plain list rather than a page: these are cards, not a table. Every value
 * is computed over the selected range and compared against the window of the
 * same length before it, which is where each card's trend comes from.
 */
export async function fetchCampaignMetrics({
	channelId,
	groupId,
	rangeQueryValues,
}: IFetchCampaignMetrics): Promise<ICampaignMetric[]> {
	return sendRequest({
		data: {channelId, ...rangeQueryValues},
		method: 'GET',
		path: `contacts/${groupId}/campaigns/metrics`,
	});
}
