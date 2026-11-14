import BundleRouter from 'route-middleware/BundleRouter';
import ExperimentsList from 'experiments/pages/ExperimentsListPage';
import React from 'react';
import SectionApp from '../SectionApp';

interface ITestsAppProps {
	channelId: string;
	groupId: string;
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

export default TestsApp;
