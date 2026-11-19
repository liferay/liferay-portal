import BundleRouter from 'route-middleware/BundleRouter';
import ExperimentOverview from 'experiments/pages/ExperimentOverviewPage';
import ExperimentsList from 'experiments/pages/ExperimentsListPage';
import React from 'react';
import SectionApp from '../SectionApp';

interface ITestsAppProps {
	channelId: string;
	groupId: string;
}

interface ITestAppProps extends ITestsAppProps {
	params: {id: string};
}

const TestsApp: React.FC<ITestsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="tests"
		routes={[
			{
				element: (
					<BundleRouter data={ExperimentsList} destructured={false} />
				),
				path: 'tests',
			},
		]}
	/>
);

const TestApp: React.FC<ITestAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={`tests/overview/${params.id}`}
		routes={[
			{
				element: (
					<BundleRouter
						data={ExperimentOverview}
						destructured={false}
					/>
				),
				path: 'tests/overview/:id',
			},
		]}
	/>
);

export {TestApp as Test, TestsApp as Tests};
