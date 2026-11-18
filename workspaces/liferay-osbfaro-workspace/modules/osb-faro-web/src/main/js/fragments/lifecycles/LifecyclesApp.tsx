import BundleRouter from 'route-middleware/BundleRouter';
import LDPLayout from 'shared/components/LDPLayout';
import LifecycleDashboard from 'lifecycle/pages/BaseLifecycle';
import React from 'react';
import SectionApp from '../SectionApp';

interface ILifecyclesAppProps {
	channelId: string;
	groupId: string;
}

const LifecyclesApp: React.FC<ILifecyclesAppProps> = (props) => (
	<SectionApp
		{...props}
		path="lifecycle"
		routes={[
			{
				children: [
					{
						element: (
							<BundleRouter
								data={LifecycleDashboard}
								destructured={false}
							/>
						),
						path: 'lifecycle/:lifecycleId?',
					},
				],
				element: <LDPLayout />,
			},
		]}
	/>
);

export {LifecyclesApp as Lifecycles};
