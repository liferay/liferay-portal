import AccountsList from 'contacts/pages/account/List';
import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';
import {LDPElement} from 'shared/components/LDPElement';

interface IAccountsParams {
	[name: string]: string | undefined;
	campaignId?: string;
	campaignName?: string;
}

const AccountsApp: React.FC<IPageAppProps<IAccountsParams>> = (props) => (
	<PageApp {...props}>
		<LDPElement fallback={<ErrorPage />}>
			<BundleRouter data={AccountsList} />
		</LDPElement>
	</PageApp>
);

export {AccountsApp as Accounts};
