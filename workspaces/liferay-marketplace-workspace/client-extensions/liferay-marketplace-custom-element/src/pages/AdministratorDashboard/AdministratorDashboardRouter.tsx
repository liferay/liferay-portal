/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {HashRouter, Route, Routes} from 'react-router-dom';

import withProviders from '../../hoc/withProviders';
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
							element={<App isAdministratorDashboard />}
							index
						/>
					</Route>
				</Route>

				<Route path="solutions">
					<Route element={<Solutions />} index />

					<Route path=":productId">
						<Route
							element={<App isAdministratorDashboard />}
							index
						/>
					</Route>
				</Route>
			</Route>
		</Routes>
	</HashRouter>
);

export default withProviders(AdministratorDashboardRouter);
