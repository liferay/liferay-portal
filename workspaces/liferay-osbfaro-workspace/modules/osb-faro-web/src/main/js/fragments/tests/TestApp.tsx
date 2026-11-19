import BundleRouter from 'route-middleware/BundleRouter';
import ExperimentOverview from 'experiments/pages/ExperimentOverviewPage';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';

interface ITestParams {
	[name: string]: string | undefined;
	id: string;
}

const TestApp: React.FC<IPageAppProps<ITestParams>> = (props) => (
	<PageApp {...props}>
		<BundleRouter data={ExperimentOverview} destructured={false} />
	</PageApp>
);

export default TestApp;
