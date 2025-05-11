/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Outlet, useOutletContext} from 'react-router-dom';

import {DashboardNavigation} from '../../components/DashboardNavigation/DashboardNavigation';

import './PublisherDashboard.scss';
import {PageRenderer} from '../../components/Page';
import useAccounts, {useAccount} from '../../hooks/data/useAccounts';
import i18n from '../../i18n';
import {getAccountImage} from '../../utils/util';

type PublisherDashboardOutletProps = {
	accountsSearch: ReturnType<typeof useAccounts>;
	catalogId?: number;
};

const PublisherDashboardOutlet: React.FC<PublisherDashboardOutletProps> = ({
	accountsSearch,
	catalogId,
}) => {
	const {data: selectedAccount, error, isLoading} = useAccount();

	return (
		<PageRenderer error={error} isLoading={isLoading}>
			<div className="published-apps-dashboard-page-container">
				<DashboardNavigation
					accountIcon={getAccountImage(selectedAccount?.logoURL)}
					accountsSearch={accountsSearch}
					currentAccount={selectedAccount as unknown as Account}
					dashboardNavigationItems={[
						{
							itemTitle: i18n.translate('apps'),
							path: '/',
							symbol: 'grid',
						},
						{
							itemTitle: i18n.translate('solutions'),
							path: '/solutions',
							symbol: 'polls',
						},
						{
							itemTitle: i18n.translate('account'),
							path: '/accounts',
							symbol: 'briefcase',
						},
					]}
				/>

				<Outlet
					context={{
						catalogId,
						selectedAccount,
					}}
				/>
			</div>
		</PageRenderer>
	);
};

const usePublisherDashboardOutletContext = () => {
	const context = useOutletContext<{
		catalogId: number;
		selectedAccount: UserAccount;
	}>();

	return context;
};

export {usePublisherDashboardOutletContext};

export default PublisherDashboardOutlet;
