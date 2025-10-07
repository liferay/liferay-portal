/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {HashRouter, Route, Routes} from 'react-router-dom';

import withProviders from '../../hoc/withProviders';
import FinanceDashboardOutlet from './FinanceDashboardOutlet';
import OrderDetails from './pages/Orders/OrderDetails/OrderDetails';
import Orders from './pages/Orders/Orders';
import PaymentDetails from './pages/Payments/PaymentDetails/PaymentDetails';
import Payments from './pages/Payments/Payments';

import './index.scss';

const SSADashboardRouter = () => (
	<HashRouter>
		<Routes>
			<Route element={<FinanceDashboardOutlet />}>
				<Route element={<Orders />} index />

				<Route element={<OrderDetails />} path="order/:orderId" />

				<Route element={<Payments />} path="payments" />

				<Route element={<PaymentDetails />} path="payments/:entryId" />
			</Route>
		</Routes>
	</HashRouter>
);

export default withProviders(SSADashboardRouter);
