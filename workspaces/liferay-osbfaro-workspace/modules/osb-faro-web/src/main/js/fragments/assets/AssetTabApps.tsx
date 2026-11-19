import AssetDashboard, {
	ACCOUNTS_TAB_ID,
	KNOWN_INDIVIDUALS_TAB_ID,
	OVERVIEW_TAB_ID,
} from 'assets/pages/Dashboard';
import BundleRouter from 'route-middleware/BundleRouter';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';

interface IAssetParams {
	[name: string]: string | undefined;
	accountId?: string;
	accountName?: string;
	assetId: string;
	assetType: string;
	downloadReport?: string;
	rangeEnd?: string;
	rangeKey?: string;
	rangeStart?: string;
	segmentId?: string;
	segmentName?: string;
	title?: string;
	touchpoint?: string;
	type?: string;
}

interface IAssetListParams extends IAssetParams {
	delta?: string;
	field?: string;
	page?: string;
	query?: string;
	sortOrder?: string;
	useDB?: string;
}

function createAssetTabApp<Params extends {[name: string]: string | undefined}>(
	tabId: string
): React.FC<IPageAppProps<Params>> {
	return (props) => (
		<PageApp {...props}>
			<BundleRouter
				componentProps={{tabId}}
				data={AssetDashboard}
				destructured={false}
			/>
		</PageApp>
	);
}

export const AssetAccountsApp =
	createAssetTabApp<IAssetListParams>(ACCOUNTS_TAB_ID);

export const AssetApp = createAssetTabApp<IAssetParams>(OVERVIEW_TAB_ID);

export const AssetKnownIndividualsApp = createAssetTabApp<IAssetListParams>(
	KNOWN_INDIVIDUALS_TAB_ID
);
