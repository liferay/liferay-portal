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
