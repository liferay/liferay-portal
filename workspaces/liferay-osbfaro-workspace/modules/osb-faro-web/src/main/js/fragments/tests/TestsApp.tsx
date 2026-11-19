import BundleRouter from 'route-middleware/BundleRouter';
import ExperimentsList from 'experiments/pages/ExperimentsListPage';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';

interface ITestsParams {
	[name: string]: string | undefined;
	delta?: string;
	field?: string;
	page?: string;
	query?: string;
	sortOrder?: string;
}

const TestsApp: React.FC<IPageAppProps<ITestsParams>> = (props) => (
	<PageApp {...props}>
		<BundleRouter data={ExperimentsList} destructured={false} />
	</PageApp>
);

export {TestsApp as Tests};
