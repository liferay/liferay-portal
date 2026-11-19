import BundleRouter from 'route-middleware/BundleRouter';
import CampaignDetail from '../../campaigns/pages/CampaignDetail';
import CampaignsDashboard from '../../campaigns/pages';
import LDPLayout from 'shared/components/LDPLayout';
import React from 'react';
import SectionApp from '../SectionApp';

interface ICampaignsAppProps {
	channelId: string;
	groupId: string;
}

interface ICampaignAppProps extends ICampaignsAppProps {
	params: {id: string};
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

const CampaignApp: React.FC<ICampaignAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={`campaigns/${params.id}`}
		routes={[
			{
				children: [
					{
						element: (
							<BundleRouter
								data={CampaignDetail}
								destructured={false}
							/>
						),
						path: 'campaigns/:id',
					},
				],
				element: <LDPLayout />,
			},
		]}
	/>
);

export {CampaignApp as Campaign, CampaignsApp as Campaigns};
