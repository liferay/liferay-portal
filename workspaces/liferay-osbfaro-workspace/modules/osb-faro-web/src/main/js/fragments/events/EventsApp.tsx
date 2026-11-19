import BundleRouter from 'route-middleware/BundleRouter';
import EditLayout from 'shared/components/EditLayout';
import EventAnalysisCreate from 'event-analysis/pages/Create';
import EventAnalysisEdit from 'event-analysis/pages/Edit';
import EventAnalysisList from 'event-analysis/pages/List';
import React from 'react';
import SectionApp from '../SectionApp';

interface IEventsAppProps {
	channelId: string;
	groupId: string;
}

interface IEditEventAnalysisAppProps extends IEventsAppProps {
	params: {id?: string};
}

const EventsApp: React.FC<IEventsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="event-analysis"
		routes={[
			{
				element: (
					<BundleRouter
						data={EventAnalysisList}
						destructured={false}
					/>
				),
				path: 'event-analysis',
			},
		]}
	/>
);

const EditEventAnalysisApp: React.FC<IEditEventAnalysisAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={
			params.id ? `event-analysis/${params.id}` : 'event-analysis/create'
		}
		routes={[
			{
				children: [
					{
						element: (
							<BundleRouter
								data={EventAnalysisCreate}
								destructured={false}
							/>
						),
						path: 'event-analysis/create',
					},
					{
						element: (
							<BundleRouter
								data={EventAnalysisEdit}
								destructured={false}
							/>
						),
						path: 'event-analysis/:id',
					},
				],
				element: <EditLayout />,
			},
		]}
	/>
);

export {EditEventAnalysisApp as EditEventAnalysis, EventsApp as Events};
