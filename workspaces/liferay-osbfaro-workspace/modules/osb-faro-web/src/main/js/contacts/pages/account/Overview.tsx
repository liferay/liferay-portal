import AccountInfoBar from './components/AccountInfoBar';
import React from 'react';

// LPD-100078 - TODO Feed the bar from the account DTO returned by
// API.accounts.fetch instead of this mock.

const MOCK_ACCOUNT = {
	accountName: 'Hydrofield',
	accountType: 'Prospect',
	annualRevenue: 120000000,
	country: 'Australia',
	industry: 'Health Sector',
	lifecycleStage: 'ENGAGED',
};

const Overview: React.FC = () => (
	<section>
		<AccountInfoBar {...MOCK_ACCOUNT} />
	</section>
);

export default Overview;
