/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {HashRouter, Route, Routes} from 'react-router-dom';

import withProviders from '../../hoc/withProviders';
import CustomerDashboardOutlet from './CustomerDashboardOutlet';
import Apps from './pages/Apps';
import App from './pages/Apps/App/App';
import AppOutlet from './pages/Apps/App/AppOutlet';
import Provisioning from './pages/Apps/App/CloudProvisioning';
import CloudProvisioningOutlet from './pages/Apps/App/CloudProvisioning/pages/CloudProvisioningOutlet';
import EnvironmentSelection from './pages/Apps/App/CloudProvisioning/pages/EnvironmentSelection';
import CloudProvisioningInstallation from './pages/Apps/App/CloudProvisioning/pages/Installation';
import ProjectSelection from './pages/Apps/App/CloudProvisioning/pages/ProjectSelection';
import Download from './pages/Apps/App/Download/Download';
import CreateLicense from './pages/Apps/App/Licenses/CreateLicense';
import Licenses from './pages/Apps/App/Licenses/Licenses';
import Connections from './pages/Connections';
import Solutions from './pages/Solutions';
import Solution from './pages/Solutions/Solution';
import SolutionOutlet from './pages/Solutions/SolutionOutlet';

const CustomerDashboardRouter = () => {
	return (
		<HashRouter>
			<Routes>
				<Route element={<CustomerDashboardOutlet />}>
					<Route element={<Apps />} index />

					<Route element={<Connections />} path="connections" />

					<Route element={<AppOutlet />} path="order/:orderId">
						<Route element={<App />} index />

						<Route element={<Download />} path="download" />

						<Route element={<Licenses />} path="licenses" />

						<Route
							element={<Provisioning />}
							path="cloud-provisioning"
						/>
					</Route>
					<Route element={<Solutions />} path="solutions" />

					<Route
						element={<SolutionOutlet />}
						path="solutions/:orderId"
					>
						<Route element={<Solution />} index />
					</Route>
				</Route>

				<Route
					element={<CreateLicense />}
					path="order/:orderId/create-license"
				/>

				<Route
					element={<CloudProvisioningOutlet />}
					path="order/:orderId/cloud-provisioning/install"
				>
					<Route element={<ProjectSelection />} index />

					<Route
						element={<EnvironmentSelection />}
						path="environment"
					/>
					<Route
						element={<CloudProvisioningInstallation />}
						path="installation"
					/>
				</Route>
			</Routes>
		</HashRouter>
	);
};

export default withProviders(CustomerDashboardRouter);
