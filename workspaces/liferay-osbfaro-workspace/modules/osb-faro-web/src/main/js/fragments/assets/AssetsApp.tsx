import BundleRouter from 'route-middleware/BundleRouter';
import AssetsList from 'assets/pages/List';
import React from 'react';
import SectionApp from '../SectionApp';

interface IAssetsAppProps {
	channelId: string;
	groupId: string;
}

const AssetsApp: React.FC<IAssetsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="assets"
		routes={[
			{
				element: (
					<BundleRouter data={AssetsList} destructured={false} />
				),
				path: 'assets/*',
			},
		]}
	/>
);

export {AssetsApp as Assets};
