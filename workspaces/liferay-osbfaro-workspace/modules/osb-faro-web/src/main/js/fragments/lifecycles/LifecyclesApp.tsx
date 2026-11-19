import BundleRouter from 'route-middleware/BundleRouter';
import EditLayout from 'shared/components/EditLayout';
import LDPLayout from 'shared/components/LDPLayout';
import LifecycleCreate from 'lifecycle/pages/CreateLifecycle';
import LifecycleDashboard from 'lifecycle/pages/BaseLifecycle';
import LifecycleDuplicate from 'lifecycle/pages/DuplicateLifecycle';
import LifecycleEdit from 'lifecycle/pages/EditLifecycle';
import React from 'react';
import SectionApp from '../SectionApp';
import {RouteObject} from 'react-router-dom';

interface ILifecyclesAppProps {
	channelId: string;
	groupId: string;
}

interface ILifecyclesDashboardAppProps extends ILifecyclesAppProps {
	params: {lifecycleId?: string};
}

interface ILifecycleAppProps extends ILifecyclesAppProps {
	params: {lifecycleId: string};
}

interface IEditLifecycleAppProps extends ILifecyclesAppProps {
	params: {
		backLifecycleId?: string;
		lifecycleId?: string;
		sourceLifecycleId?: string;
	};
}

const LIFECYCLE_ROUTES: RouteObject[] = [
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
];

const LifecyclesApp: React.FC<ILifecyclesDashboardAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={
			params.lifecycleId ? `lifecycle/${params.lifecycleId}` : 'lifecycle'
		}
		routes={LIFECYCLE_ROUTES}
	/>
);

const LifecycleApp: React.FC<ILifecycleAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={`lifecycle/${params.lifecycleId}`}
		routes={LIFECYCLE_ROUTES}
	/>
);

const EditLifecycleApp: React.FC<IEditLifecycleAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={
			params.lifecycleId
				? `lifecycle/${params.lifecycleId}/edit`
				: params.sourceLifecycleId
					? `lifecycle/${params.sourceLifecycleId}/duplicate`
					: 'lifecycle/new'
		}
		routes={[
			{
				children: [
					{
						children: [
							{
								element: (
									<BundleRouter
										data={LifecycleCreate}
										destructured={false}
									/>
								),
								path: 'lifecycle/new',
							},
							{
								element: (
									<BundleRouter
										data={LifecycleDuplicate}
										destructured={false}
									/>
								),
								path: 'lifecycle/:sourceLifecycleId/duplicate',
							},
							{
								element: (
									<BundleRouter
										data={LifecycleEdit}
										destructured={false}
									/>
								),
								path: 'lifecycle/:lifecycleId/edit',
							},
						],
						element: <LDPLayout />,
					},
				],
				element: <EditLayout />,
			},
		]}
		state={{lifecycleId: params.backLifecycleId}}
	/>
);

export {
	EditLifecycleApp as EditLifecycle,
	LifecycleApp as Lifecycle,
	LifecyclesApp as Lifecycles,
};
