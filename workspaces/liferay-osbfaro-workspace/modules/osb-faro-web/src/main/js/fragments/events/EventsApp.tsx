import BundleRouter from 'route-middleware/BundleRouter';
import EventAnalysisList from 'event-analysis/pages/List';
import React from 'react';
import SectionApp from '../SectionApp';

interface IEventsAppProps {
	channelId: string;
	groupId: string;
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

export default EventsApp;
