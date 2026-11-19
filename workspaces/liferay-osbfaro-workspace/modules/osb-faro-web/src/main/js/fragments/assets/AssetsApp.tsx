import AssetsList from 'assets/pages/List';
import BundleRouter from 'route-middleware/BundleRouter';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';

interface IAssetsParams {
	[name: string]: string | undefined;
	accountId?: string;
	accountName?: string;
	objectType?: string;
	orderBy?: string;
	page?: string;
	rangeEnd?: string;
	rangeKey?: string;
	rangeStart?: string;
	segmentId?: string;
	segmentName?: string;
}

const AssetsApp: React.FC<IPageAppProps<IAssetsParams>> = (props) => (
	<PageApp {...props}>
		<BundleRouter data={AssetsList} destructured={false} />
	</PageApp>
);

export {AssetsApp as Assets};
