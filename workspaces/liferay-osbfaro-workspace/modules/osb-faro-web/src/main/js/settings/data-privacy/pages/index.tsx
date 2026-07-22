import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense} from 'react';
import {Route, Routes as RouterRoutes} from 'react-router-dom';

const Overview = lazy(
	() => import(/* webpackChunkName: "DataPrivacyOverview" */ './Overview')
);

const RequestLog = lazy(
	() => import(/* webpackChunkName: "RequestLog" */ './RequestLog')
);

const SuppressedUsers = lazy(
	() => import(/* webpackChunkName: "SupressedUsers" */ './SuppressedUsers')
);

interface IDataPrivacyProps extends React.HTMLAttributes<HTMLDivElement> {
	groupId: string;
}

const DataPrivacy: React.FC<IDataPrivacyProps> = ({groupId}) => (
	<Suspense fallback={<Loading />}>
		<RouterRoutes>
			<Route element={<BundleRouter data={Overview} />} index />

			<Route
				element={
					<BundleRouter
						componentProps={{groupId}}
						data={SuppressedUsers}
						destructured={false}
					/>
				}
				path="suppressed-users"
			/>

			<Route
				element={
					<BundleRouter
						componentProps={{groupId}}
						data={RequestLog}
						destructured={false}
					/>
				}
				path="request-log/*"
			/>

			<Route element={<ErrorPage />} path="*" />
		</RouterRoutes>
	</Suspense>
);

export default DataPrivacy;
