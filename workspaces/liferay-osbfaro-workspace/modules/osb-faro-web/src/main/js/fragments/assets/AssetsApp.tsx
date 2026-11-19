import AssetDashboard from 'assets/pages/Dashboard';
import AssetsList from 'assets/pages/List';
import BundleRouter from 'route-middleware/BundleRouter';
import React from 'react';
import SectionApp from '../SectionApp';
import {RouteObject} from 'react-router-dom';

interface IAssetsAppProps {
	channelId: string;
	groupId: string;
}

interface IAssetParams {
	assetId: string;
	assetType: string;
	title: string;
	touchpoint: string;
	type: string;
}

interface IAssetAppProps extends IAssetsAppProps {
	params: IAssetParams;
}

const ASSETS_ROUTES: RouteObject[] = [
	{
		element: <BundleRouter data={AssetDashboard} destructured={false} />,
		path: 'assets/:assetType/:assetId/:tabId/:touchpoint/:title?/:type?',
	},
	{
		element: <BundleRouter data={AssetsList} destructured={false} />,
		path: 'assets/*',
	},
];

const getAssetPath = (
	tabId: string,
	{assetId, assetType, title, touchpoint, type}: IAssetParams
) => {
	const segments = [
		'assets',
		assetType,
		assetId,
		tabId,
		touchpoint,
		title,
		type,
	].filter(Boolean);

	const encodedSegments = segments.map(encodeURIComponent);

	return encodedSegments.join('/');
};

const AssetApp: React.FC<IAssetAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={getAssetPath('page', params)}
		routes={ASSETS_ROUTES}
	/>
);

const AssetAccountsApp: React.FC<IAssetAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={getAssetPath('accounts', params)}
		routes={ASSETS_ROUTES}
	/>
);

const AssetKnownIndividualsApp: React.FC<IAssetAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={getAssetPath('known-individuals', params)}
		routes={ASSETS_ROUTES}
	/>
);

const AssetsApp: React.FC<IAssetsAppProps> = (props) => (
	<SectionApp {...props} path="assets" routes={ASSETS_ROUTES} />
);

export {
	AssetApp as Asset,
	AssetAccountsApp as AssetAccounts,
	AssetKnownIndividualsApp as AssetKnownIndividuals,
	AssetsApp as Assets,
};
