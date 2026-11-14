import BundleRouter from 'route-middleware/BundleRouter';
import SitesDashboard from 'sites/pages';
import React from 'react';
import SectionApp from '../SectionApp';

interface ISitesAppProps {
	channelId: string;
	groupId: string;
}

const SitesApp: React.FC<ISitesAppProps> = (props) => (
	<SectionApp
		{...props}
		path="sites"
		routes={[
			{
				element: (
					<BundleRouter data={SitesDashboard} destructured={false} />
				),
				path: 'sites/*',
			},
		]}
	/>
);

export default SitesApp;
