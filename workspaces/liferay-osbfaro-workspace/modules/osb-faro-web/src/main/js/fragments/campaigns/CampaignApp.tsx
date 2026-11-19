import BundleRouter from 'route-middleware/BundleRouter';
import CampaignDetail from 'campaigns/pages/CampaignDetail';
import ErrorPage from 'shared/pages/ErrorPage';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';
import {LDPElement} from 'shared/components/LDPElement';

interface ICampaignParams {
	[name: string]: string | undefined;
	accountId?: string;
	accountName?: string;
	id: string;
	rangeEnd?: string;
	rangeKey?: string;
	rangeStart?: string;
}

const CampaignApp: React.FC<IPageAppProps<ICampaignParams>> = (props) => (
	<PageApp {...props}>
		<LDPElement fallback={<ErrorPage />}>
			<BundleRouter data={CampaignDetail} destructured={false} />
		</LDPElement>
	</PageApp>
);

export default CampaignApp;
