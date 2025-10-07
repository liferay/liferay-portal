/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Align} from '@clayui/drop-down';
import {HashRouter, Route, Routes, useParams} from 'react-router-dom';

import DropDown from '../../components/DropDown';
import NewAppContextProvider from '../../context/NewAppContext';
import withProviders from '../../hoc/withProviders';
import {Liferay} from '../../liferay/liferay';
import koroneikiOAuth2 from '../../services/oauth/Koroneiki';
import App from '../PublisherDashboard/pages/Apps/App';
import AdministratorDashboardOutlet from './AdministratorDashboardOutlet';
import AdministrationSummary from './pages';
import Apps from './pages/Apps';
import Orders from './pages/Orders';
import PublisherRequest from './pages/PublisherRequest';
import {Publishers} from './pages/Publishers';
import Solutions from './pages/Solutions';
import Trial from './pages/Trial';

import './index.scss';

const AppWithActions = () => {
	const {productId} = useParams();

	return (
		<App
			header={
				<DropDown
					actions={[
						{
							name: 'Koroneiki Sync',
							onClick: () =>
								koroneikiOAuth2
									.syncProduct(productId as string)
									.then(() =>
										Liferay.Util.openToast({
											message:
												'Koroneiki Sync Successfully',
											title: 'Success',
										})
									)
									.catch((error) => {
										console.error(error);

										Liferay.Util.openToast({
											message: 'Koroneiki Sync Failed',
											title: 'Error',
											type: 'danger',
										});
									}),
						},
					]}
					item={null}
					position={Align.BottomCenter}
					trigger={
						<ClayButton displayType="secondary" size="sm">
							Administrator Actions
						</ClayButton>
					}
				/>
			}
		/>
	);
};

const AdministratorDashboardRouter = () => (
	<HashRouter>
		<Routes>
			<Route element={<AdministratorDashboardOutlet />}>
				<Route element={<AdministrationSummary />} index />
				<Route element={<Orders />} path="orders" />
				<Route
					element={<PublisherRequest />}
					path="publisher-request"
				/>
				<Route element={<Publishers />} path="publishers" />
				<Route element={<Trial />} path="trial" />

				<Route path="apps">
					<Route element={<Apps />} index />

					<Route path=":productId">
						<Route
							element={
								<NewAppContextProvider>
									<AppWithActions />
								</NewAppContextProvider>
							}
							index
						/>
					</Route>
				</Route>

				<Route path="solutions">
					<Route element={<Solutions />} index />

					<Route path=":productId">
						<Route element={<App />} index />
					</Route>
				</Route>
			</Route>
		</Routes>
	</HashRouter>
);

export default withProviders(AdministratorDashboardRouter, {
	withErrorBoundary: true,
});
