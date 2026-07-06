import sendRequest from 'shared/util/request';
import {RangeKeyTimeRanges} from '../util/constants';

export type TopAssetMetric =
	| 'downloadsMetric'
	| 'impressionsMetric'
	| 'viewsMetric';

export type TopAssetObjectType = 'content' | 'file';

export interface ITopAsset {
	assetTitle: string;
	assetType?: string;
	downloadsMetric: {value: number};
	id: string;
	impressionsMetric: {value: number};
	mimeType?: string;
	viewsMetric: {value: number};
}

interface IFetchAccountTopAssets {
	accountId: string;
	channelId: string;
	groupId: string;

	// TODO(LPD-91217): confirm `objectType` query param name once backend lands.

	objectType?: TopAssetObjectType;
	selectedMetric: TopAssetMetric;
}

export async function fetchAccountTopAssets({
	accountId,
	channelId,
	groupId,
	objectType,
	selectedMetric,
}: IFetchAccountTopAssets): Promise<{items: ITopAsset[]}> {
	return sendRequest({
		data: {
			channelId,
			filter: `accountIds in ('${accountId}')`,
			pageSize: 5,
			selectedMetric,
			sort: `${selectedMetric},desc`,
			...(objectType && {objectType}),
		},
		method: 'GET',
		path: `contacts/${groupId}/asset-summary`,
	});
}

interface ISearchAssetTypes {
	channelId: string;
	groupId: string;
	page?: number;
	pageSize?: number;
	rangeKey?: number;
}

export async function searchTypes({
	channelId,
	groupId,
	page = 1,
	pageSize = 10,
	rangeKey = Number(RangeKeyTimeRanges.Last30Days),
}: ISearchAssetTypes): Promise<{
	items: Array<{id: string; name: string}>;
	totalCount: number;
}> {
	return sendRequest({
		data: {channelId, page, pageSize, rangeKey},
		method: 'GET',
		path: `contacts/${groupId}/asset-summary-types`,
	});
}
