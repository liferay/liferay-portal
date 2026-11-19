import AccountProfileRoutes from 'contacts/pages/account/ProfileRoutes';
import AccountsList from 'contacts/pages/account/List';
import BundleRouter from 'route-middleware/BundleRouter';
import LDPLayout from 'shared/components/LDPLayout';
import React from 'react';
import SectionApp from '../SectionApp';
import {RouteObject} from 'react-router-dom';

interface IAccountsAppProps {
	channelId: string;
	groupId: string;
}

interface IAccountAppProps extends IAccountsAppProps {
	params: {id: string};
}

const ACCOUNT_ROUTES: RouteObject[] = [
	{
		children: [
			{
				element: <BundleRouter data={AccountProfileRoutes} />,
				path: 'contacts/accounts/:id/*',
			},
		],
		element: <LDPLayout />,
	},
];

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

const AccountApp: React.FC<IAccountAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={`contacts/accounts/${params.id}/overview`}
		routes={ACCOUNT_ROUTES}
	/>
);

const AccountActivitiesApp: React.FC<IAccountAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`contacts/accounts/${params.id}/activities`}
		routes={ACCOUNT_ROUTES}
	/>
);

const AccountProfileApp: React.FC<IAccountAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={`contacts/accounts/${params.id}/profile`}
		routes={ACCOUNT_ROUTES}
	/>
);

export {
	AccountActivitiesApp as AccountActivities,
	AccountApp as Account,
	AccountProfileApp as AccountProfile,
	AccountsApp as Accounts,
};
