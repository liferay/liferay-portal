import FaroConstants from 'shared/util/constants';
import sendRequest from 'shared/util/request';
import {Metric} from 'contacts/pages/account/utils/types';

const {
	pagination: {delta: DEFAULT_DELTA},
} = FaroConstants;

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

/**
 * One value of one UTM parameter seen on the campaign's tagged website
 * sessions. The API sends one item per value, ordered by name and then by
 * usage, and `standard` marks the five standard `utm_*` parameters.
 */
export interface ICampaignAcquisitionParameter {
	fieldName: string | null;
	name: string;
	standard: boolean;
	value: string;
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

interface IFetchCampaignAcquisitionParameters {
	channelId: string;
	groupId: string;
	id: string;
	rangeQueryValues?: {[key: string]: any};
}

/**
 * Without range values the API answers for all time, unlike the campaign's
 * other endpoints, so the selected range is always passed through.
 */
export async function fetchCampaignAcquisitionParameters({
	channelId,
	groupId,
	id,
	rangeQueryValues,
}: IFetchCampaignAcquisitionParameters): Promise<
	ICampaignAcquisitionParameter[]
> {
	return sendRequest({
		data: {channelId, ...rangeQueryValues},
		method: 'GET',
		path: `contacts/${groupId}/campaigns/${id}/acquisition-parameters`,
	});
}

interface IFetchCampaignMetrics {
	accountId?: string;
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
	accountId,
	channelId,
	groupId,
	rangeQueryValues,
}: IFetchCampaignMetrics): Promise<ICampaignMetric[]> {
	return sendRequest({
		data: {
			accountIds: accountId,
			channelId,
			...rangeQueryValues,
		},
		method: 'GET',
		path: `contacts/${groupId}/campaigns/metrics`,
	});
}

interface ISearchCampaignNames {
	channelId?: string;
	groupId: string;
	page?: number;
	pageSize?: number;
	query?: string;
}

/**
 * Feeds the campaign picker. Modeled on `account-names`: it pages from zero,
 * filters by `keywords` and reports its total as `totalCount`. The backend
 * decides whether the names come from UTM or Salesforce.
 */
export async function searchCampaignNames({
	channelId = '',
	groupId,
	page = 0,
	pageSize = DEFAULT_DELTA,
	query = '',
}: ISearchCampaignNames): Promise<{
	items: {id: string; name: string}[];
	totalCount: number;
}> {
	return sendRequest({
		data: {channelId, keywords: query, page, pageSize},
		method: 'GET',
		path: `contacts/${groupId}/campaigns/campaign-names`,
	});
}
