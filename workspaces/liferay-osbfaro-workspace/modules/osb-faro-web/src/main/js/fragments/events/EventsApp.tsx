import BundleRouter from 'route-middleware/BundleRouter';
import EventAnalysisList from 'event-analysis/pages/List';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';

interface IEventsParams {
	[name: string]: string | undefined;
	delta?: string;
	field?: string;
	page?: string;
	query?: string;
	rangeEnd?: string;
	rangeKey?: string;
	rangeStart?: string;
	sortOrder?: string;
}

const EventsApp: React.FC<IPageAppProps<IEventsParams>> = (props) => (
	<PageApp {...props}>
		<BundleRouter data={EventAnalysisList} destructured={false} />
	</PageApp>
);

export {EventsApp as Events};
