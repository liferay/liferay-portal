/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Outlet, useOutletContext} from 'react-router-dom';
import useSWR, {KeyedMutator} from 'swr';

import {DashboardNavigation} from '../../components/DashboardNavigation/DashboardNavigation';
import {PageRenderer} from '../../components/Page';
import {useMarketplaceContext} from '../../context/MarketplaceContext';
import SearchBuilder from '../../core/SearchBuilder';
import {OrderTypes, OrderWorkflowStatusCode} from '../../enums/Order';
import {usePlacedOrders} from '../../hooks/data/usePlacedOrder';
import HeadlessAdminUser from '../../services/rest/HeadlessAdminUser';
import {useSSATrialsExtend} from './useSSATrialsExtend';

const SSADashboardOutlet = () => {
	const {marketplaceUserAccount, myUserAccount, properties} =
		useMarketplaceContext();

	const {data: ssaAccount, isLoading: isSSALoading} = useSWR(
		'/ssa-account',
		() =>
			HeadlessAdminUser.getAccountByExternalReferenceCode(
				properties.accountExternalReferenceCode
			)
	);

	const isFilterByAuthorIdEnabled =
		properties.featureFlags.includes('LPD-63837');

	const authorFilter = isFilterByAuthorIdEnabled ? 'authorId' : 'author';

	const authorFilterValue = isFilterByAuthorIdEnabled
		? myUserAccount?.id
		: myUserAccount?.name;

	const {data: inProgressTrialResponse = {totalCount: 0}} = usePlacedOrders({
		accountId: ssaAccount?.id as number,
		filter: new SearchBuilder()
			.eq(authorFilter, authorFilterValue, {
				unquote: isFilterByAuthorIdEnabled,
			})
			.and()
			.eq('orderTypeExternalReferenceCode', OrderTypes.SSA_SAAS)
			.and()
			.lambda('orderStatus', OrderWorkflowStatusCode.IN_PROGRESS, {
				unquote: true,
			})
			.build(),
		page: 1,
		pageSize: 1,
		shouldFetch: !!ssaAccount,
	});

	const {
		data: ssaTrialExtend,
		error,
		isLoading,
		mutate: ssaTrialExtendMutate,
	} = useSSATrialsExtend(ssaAccount!);

	const fetching = isSSALoading || isLoading;

	return (
		<PageRenderer error={error} isLoading={fetching}>
			<div className="published-apps-dashboard-page-container">
				<DashboardNavigation
					currentAccount={ssaAccount}
					dashboardNavigationItems={[
						{
							active: true,
							itemTitle: 'My SaaS Demos',
							path: '/',
							symbol: 'nodes',
							visible: true,
						},
						{
							itemTitle: 'Manage SaaS',
							path: '/saas-trials',
							symbol: 'cog',
							visible: marketplaceUserAccount.isSSAAdmin,
						},
					].filter(({visible}) => visible)}
				/>

				<span className="h-vh-100 ml-6 w-100">
					{ssaAccount ? (
						<Outlet
							context={{
								myTrialsInProgress:
									inProgressTrialResponse.totalCount,
								selectedAccountId: ssaAccount?.id,
								ssaAccount,
								ssaTrialExtend,
								ssaTrialExtendMutate,
							}}
						/>
					) : (
						<h1>
							{`Unable to find ${properties.accountExternalReferenceCode}`}
						</h1>
					)}
				</span>
			</div>
		</PageRenderer>
	);
};

const useSSADashboardOutlet = () => {
	return useOutletContext<{
		myTrialsInProgress: number;
		selectedAccountId: number;
		ssaAccount: Account;
		ssaTrialExtend: APIResponse<TrialExtend>;
		ssaTrialExtendMutate: KeyedMutator<APIResponse<TrialExtend>>;
	}>();
};

export {useSSADashboardOutlet};

export default SSADashboardOutlet;
