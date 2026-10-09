/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {
	HashRouter,
	Navigate,
	Route,
	Routes,
	createRoutesFromElements,
} from 'react-router';

import {FilterContextProvider} from '../shared/components/filter/FilterContext.es';
import HeaderController from '../shared/components/header/HeaderController.es';
import {AppContextProvider} from './AppContext.es';
import InstanceListPage from './instance-list-page/InstanceListPage.es';
import PerformanceByAssigneePage from './performance-by-assignee-page/PerformanceByAssigneePage.es';
import PerformanceByStepPage from './performance-by-step-page/PerformanceByStepPage.es';
import ProcessListPage from './process-list-page/ProcessListPage.es';
import ProcessMetricsContainer, {
	DashboardTab,
	PerformanceTab,
} from './process-metrics/ProcessMetricsContainer.es';
import SettingsContainer from './settings/SettingsContainer.es';
import IndexesPage from './settings/indexes-page/IndexesPage.es';
import SLAContainer from './sla/SLAContainer.es';
import SLAFormPage from './sla/form-page/SLAFormPage.es';
import SLAListPage from './sla/list-page/SLAListPage.es';
import WorkloadByAssigneePage from './workload-by-assignee-page/WorkloadByAssigneePage.es';

const appRoutes = (
	<>
		<Route
			element={
				<Navigate
					replace
					to="/processes/20/1/overdueInstanceCount:desc"
				/>
			}
			path="/"
		/>

		<Route
			element={<ProcessListPage />}
			path="/processes/:pageSize/:page/:sort"
		/>

		<Route element={<ProcessMetricsContainer />} path="/metrics/:processId">
			<Route
				element={<DashboardTab />}
				path="dashboard/:pageSize/:page/:sort"
			/>

			<Route element={<PerformanceTab />} path="performance" />
		</Route>

		<Route
			element={<InstanceListPage />}
			path="/instance/:processId/:pageSize/:page/:sort"
		/>

		<Route element={<SLAContainer />} path="/sla/:processId">
			<Route element={<SLAListPage />} path="list/:pageSize/:page" />

			<Route element={<SLAFormPage />} path="new" />

			<Route element={<SLAFormPage />} path="edit/:id" />
		</Route>

		<Route
			element={<PerformanceByStepPage />}
			path="/performance/step/:processId/:pageSize/:page/:sort"
		/>

		<Route
			element={<WorkloadByAssigneePage />}
			path="/workload/assignee/:processId/:pageSize/:page/:sort"
		/>

		<Route
			element={<PerformanceByAssigneePage />}
			path="/performance/assignee/:processId/:pageSize/:page/:sort"
		/>

		<Route element={<SettingsContainer />} path="/settings">
			<Route element={<IndexesPage />} path="indexes" />
		</Route>
	</>
);

export const appDataRoutes = createRoutesFromElements(appRoutes);

const App = (props) => {
	return (
		<AppContextProvider {...props}>
			<FilterContextProvider>
				<HashRouter useTransitions={false}>
					<HeaderController basePath="/processes" />

					<div className="portal-workflow-metrics-app">
						<Routes>{appRoutes}</Routes>
					</div>
				</HashRouter>
			</FilterContextProvider>
		</AppContextProvider>
	);
};

export default App;
