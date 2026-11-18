import BundleRouter from 'route-middleware/BundleRouter';
import LDPLayout from 'shared/components/LDPLayout';
import AccountsList from 'contacts/pages/account/List';
import React from 'react';
import SectionApp from '../SectionApp';

interface IAccountsAppProps {
	channelId: string;
	groupId: string;
}

const AccountsApp: React.FC<IAccountsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="contacts/accounts"
		routes={[
			{
				children: [
					{
						element: <BundleRouter data={AccountsList} />,
						path: 'contacts/accounts',
					},
				],
				element: <LDPLayout />,
			},
		]}
	/>
);

export {AccountsApp as Accounts};
