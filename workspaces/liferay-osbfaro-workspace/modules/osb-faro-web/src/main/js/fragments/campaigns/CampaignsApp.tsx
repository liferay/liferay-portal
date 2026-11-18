import BundleRouter from 'route-middleware/BundleRouter';
import LDPLayout from 'shared/components/LDPLayout';
import CampaignsDashboard from '../../campaigns/pages';
import React from 'react';
import SectionApp from '../SectionApp';

interface ICampaignsAppProps {
	channelId: string;
	groupId: string;
}

const CampaignsApp: React.FC<ICampaignsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="campaigns"
		routes={[
			{
				children: [
					{
						element: (
							<BundleRouter
								data={CampaignsDashboard}
								destructured={false}
							/>
						),
						path: 'campaigns',
					},
				],
				element: <LDPLayout />,
			},
		]}
	/>
);

export {CampaignsApp as Campaigns};
