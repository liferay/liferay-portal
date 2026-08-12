import sendRequest from 'shared/util/request';

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
	rangeEnd?: string | null;
	rangeKey?: number | null;
	rangeStart?: string | null;
	selectedMetric: TopAssetMetric;
}

export async function fetchAccountTopAssets({
	accountId,
	channelId,
	groupId,
	objectType,
	rangeEnd,
	rangeKey,
	rangeStart,
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
			...(rangeKey ? {rangeKey} : {}),
			...(rangeEnd && rangeStart ? {rangeEnd, rangeStart} : {}),
		},
		method: 'GET',
		path: `contacts/${groupId}/asset-summary`,
	});
}

interface IFetchIndividualTopAssets {
	channelId: string;
	groupId: string;
	individualId: string;
	objectType?: TopAssetObjectType;
	rangeEnd?: string | null;
	rangeKey?: number | null;
	rangeStart?: string | null;
	selectedMetric: TopAssetMetric;
}

/**
 * The individual scope travels in the same `filter` string
 * `fetchAccountTopAssets` uses for `accountIds`, because the engine resolves
 * `individualIds` there rather than through a query parameter of its own.
 */

export async function fetchIndividualTopAssets({
	channelId,
	groupId,
	individualId,
	objectType,
	rangeEnd,
	rangeKey,
	rangeStart,
	selectedMetric,
}: IFetchIndividualTopAssets): Promise<{items: ITopAsset[]}> {
	return sendRequest({
		data: {
			channelId,
			filter: `individualIds in ('${individualId}')`,
			pageSize: 5,
			selectedMetric,
			sort: `${selectedMetric},desc`,
			...(objectType && {objectType}),
			...(rangeKey ? {rangeKey} : {}),
			...(rangeEnd && rangeStart ? {rangeEnd, rangeStart} : {}),
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
	rangeKey?: number | null;
}

export async function searchTypes({
	channelId,
	groupId,
	page = 1,
	pageSize = 10,
	rangeKey,
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
