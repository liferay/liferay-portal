import React from 'react';
import WizardPage, {Step} from 'settings/components/base-page/WizardPage';
import {ConnectMarketoCampaignStep} from 'settings/components/marketo/steps/ConnectMarketoCampaignStep';
import {sub} from 'shared/util/lang';

const steps: Step[] = [
	{
		content: (props: any) => <ConnectMarketoCampaignStep {...props} />,
		description: Liferay.Language.get(
			'to-connect-your-data-source-with-liferay-data-platform-enter-their-url-the-client-id-and-secret'
		),
		title: sub(Liferay.Language.get('connect-x'), [
			Liferay.Language.get('marketo'),
		]) as string,
	},
];

const ConnectMarketoCampaign = () => <WizardPage steps={steps} />;

export default ConnectMarketoCampaign;
