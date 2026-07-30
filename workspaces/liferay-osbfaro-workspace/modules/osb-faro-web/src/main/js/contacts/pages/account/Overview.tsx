import AccountInfoBar from './components/AccountInfoBar';
import React from 'react';
import {IAccount} from './components/AccountInfo';

interface IOverviewProps {
	account?: IAccount;
}

const Overview: React.FC<IOverviewProps> = ({account}) => (
	<section>
		<AccountInfoBar
			accountName={account?.accountName}
			accountType={account?.accountType}
			annualRevenue={account?.annualRevenue}
			country={account?.country}
			industry={account?.industry}
			lifecycleStage={account?.lifecycleStage}
		/>
	</section>
);

export default Overview;
