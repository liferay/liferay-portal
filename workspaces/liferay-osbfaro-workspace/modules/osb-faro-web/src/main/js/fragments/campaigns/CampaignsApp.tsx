import BundleRouter from 'route-middleware/BundleRouter';
import CampaignsDashboard from 'campaigns/pages';
import ErrorPage from 'shared/pages/ErrorPage';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';
import {LDPElement} from 'shared/components/LDPElement';

interface ICampaignsParams {
	[name: string]: string | undefined;
	accountId?: string;
	accountName?: string;
	rangeEnd?: string;
	rangeKey?: string;
	rangeStart?: string;
}

const CampaignsApp: React.FC<IPageAppProps<ICampaignsParams>> = (props) => (
	<PageApp {...props}>
		<LDPElement fallback={<ErrorPage />}>
			<BundleRouter data={CampaignsDashboard} destructured={false} />
		</LDPElement>
	</PageApp>
);

export {CampaignsApp as Campaigns};
