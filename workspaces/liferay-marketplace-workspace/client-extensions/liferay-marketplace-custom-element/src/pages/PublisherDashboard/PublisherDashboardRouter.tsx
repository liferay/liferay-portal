/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect} from 'react';
import {HashRouter, Outlet, Route, Routes} from 'react-router-dom';

import NewAppContextProvider from '../../context/NewAppContext';
import SolutionContextProvider from '../../context/SolutionContext';
import withProviders from '../../hoc/withProviders';
import {useAccount} from '../../hooks/data/useAccounts';
import {useCatalogs} from '../../hooks/data/useCatalogs';
import {useSupplierAccounts} from '../../hooks/data/useSupplierAccounts';
import {Liferay} from '../../liferay/liferay';
import CommerceSelectAccount from '../../services/rest/CommerceSelectAccount';
import PublishedDashboardOutlet from './PublisherDashboardOutlet';
import Accounts from './pages/Accounts/Accounts';
import Apps from './pages/Apps';
import App from './pages/Apps/App';
import {AppContextProvider} from './pages/Apps/AppCreationFlow/AppContext/AppManageState';
import {AppCreationFlow} from './pages/Apps/AppCreationFlow/AppCreationFlow';
import PublishAppOutlet from './pages/NewAppFlow/PublishAppOutlet';
import {Build, Profile as AppProfile} from './pages/NewAppFlow/pages';
import Licensing from './pages/NewAppFlow/pages/Licensing';
import LicensePrices from './pages/NewAppFlow/pages/Licensing/LicensePrices';
import Pricing from './pages/NewAppFlow/pages/Pricing';
import Storefront from './pages/NewAppFlow/pages/Storefront';
import SubmitApp from './pages/NewAppFlow/pages/Submit';
import Support from './pages/NewAppFlow/pages/Support';
import Version from './pages/NewAppFlow/pages/Version';
import NewAppBuildOutlet from './pages/NewVersionFlow/NewAppBuildOutlet';
import SubmitNewBuild from './pages/NewVersionFlow/pages/SubmitNewBuild';
import Solutions from './pages/Solutions';
import SolutionsDetails from './pages/Solutions/Solution';
import PublishSolutionOutlet from './pages/Solutions/SolutionForm/PublishSolutionOutlet';
import {
	CompanyProfile,
	ContactUs,
	Create,
	Details,
	Header,
	Profile,
	Submit,
} from './pages/Solutions/SolutionForm/pages';

const PublisherDashboardRouter = () => {
	const {accountId} = Liferay.CommerceContext.account || {};
	const {data, isValidating} = useAccount();
	const {data: catalogs = [], isLoading} = useCatalogs();
	const accountsSearch = useSupplierAccounts();

	useEffect(() => {
		const checkAccount = async (accountId: number) => {
			await CommerceSelectAccount.selectAccount(accountId);

			Liferay.CommerceContext.account = {
				accountId,
				accountName: data?.name ?? null,
			};

			window.location.reload();
		};

		const newAccountId = accountsSearch.items.at(0)?.id;

		if (!isValidating && data?.type !== 'supplier' && newAccountId) {
			checkAccount(newAccountId);
		}
	}, [isValidating, data?.type, accountsSearch.items, data?.name]);

	const catalog = catalogs.find((catalog) => catalog.accountId === accountId);

	const catalogId = catalog?.id;

	if (isLoading) {
		return null;
	}

	return (
		<HashRouter>
			<Routes>
				<Route path="newapp">
					<Route
						element={
							<NewAppContextProvider catalog={catalog as Catalog}>
								<Outlet />
							</NewAppContextProvider>
						}
						path=":productId?"
					>
						<Route element={<PublishAppOutlet />} path="publisher">
							<Route element={<AppProfile />} path="profile" />
							<Route element={<Build />} path="build" />
							<Route element={<Create />} index />
							<Route element={<Licensing />} path="licensing" />
							<Route element={<Pricing />} path="pricing" />
							<Route element={<Storefront />} path="storefront" />
							<Route element={<Version />} path="version" />
							<Route element={<SubmitApp />} path="submit" />

							<Route
								element={<LicensePrices />}
								path="licensing-prices"
							/>

							<Route element={<Support />} path="support" />
						</Route>
						<Route element={<NewAppBuildOutlet />} path="newbuild">
							<Route element={<Build />} index />
							<Route element={<SubmitNewBuild />} path="submit" />
						</Route>
					</Route>
				</Route>

				<Route
					element={
						<AppContextProvider>
							<AppCreationFlow catalogId={String(catalogId)} />
						</AppContextProvider>
					}
					path="app/create"
				/>

				<Route
					element={
						<PublishedDashboardOutlet
							accountsSearch={accountsSearch}
							catalogId={catalogId}
						/>
					}
				>
					<Route path="/">
						<Route element={<Apps />} index />

						<Route
							element={
								<NewAppContextProvider
									catalog={catalog as Catalog}
								>
									<App />
								</NewAppContextProvider>
							}
							path="app/:productId"
						/>
					</Route>

					<Route element={<Accounts />} path="accounts" />

					<Route path="solutions">
						<Route element={<Solutions />} index />
					</Route>
				</Route>

				<Route path="solutions">
					<Route
						element={
							<SolutionContextProvider
								catalogId={catalogId as number}
							>
								<Outlet />
							</SolutionContextProvider>
						}
						path=":productId?"
					>
						<Route
							element={
								<PublishedDashboardOutlet
									accountsSearch={accountsSearch}
									catalogId={catalogId}
								/>
							}
						>
							<Route element={<SolutionsDetails />} index />
						</Route>

						<Route
							element={<PublishSolutionOutlet />}
							path="publisher"
						>
							<Route element={<Create />} path="" />
							<Route
								element={<CompanyProfile />}
								path="company"
							/>
							<Route element={<ContactUs />} path="contact" />
							<Route element={<Details />} path="details" />
							<Route element={<Header />} path="header" />
							<Route element={<Profile />} path="profile" />
							<Route element={<Submit />} path="submit" />
						</Route>
					</Route>
				</Route>
			</Routes>
		</HashRouter>
	);
};

export default withProviders(PublisherDashboardRouter);
