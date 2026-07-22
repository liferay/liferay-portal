import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense} from 'react';
import {Route, Routes as RouterRoutes} from 'react-router-dom';

const AccessTokenList = lazy(
	() => import(/* webpackChunkName: "AccessTokenList" */ './AccessTokenList')
);

interface IApisProps extends React.HTMLAttributes<HTMLDivElement> {
	groupId: string;
}

const DataPrivacy: React.FC<IApisProps> = () => (
	<Suspense fallback={<Loading />}>
		<RouterRoutes>
			<Route
				element={<BundleRouter data={AccessTokenList} />}
				path="tokens/*"
			/>

			<Route element={<ErrorPage />} path="*" />
		</RouterRoutes>
	</Suspense>
);

export default DataPrivacy;
