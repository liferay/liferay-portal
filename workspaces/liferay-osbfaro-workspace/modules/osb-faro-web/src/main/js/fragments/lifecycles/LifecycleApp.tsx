import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import LifecycleDashboard from 'lifecycle/pages/BaseLifecycle';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';
import {LDPElement} from 'shared/components/LDPElement';

interface ILifecycleParams {
	[name: string]: string | undefined;
	lifecycleId?: string;
	segmentId?: string;
	segmentName?: string;
}

const LifecycleApp: React.FC<IPageAppProps<ILifecycleParams>> = (props) => (
	<PageApp {...props}>
		<LDPElement fallback={<ErrorPage />}>
			<BundleRouter data={LifecycleDashboard} destructured={false} />
		</LDPElement>
	</PageApp>
);

export default LifecycleApp;
