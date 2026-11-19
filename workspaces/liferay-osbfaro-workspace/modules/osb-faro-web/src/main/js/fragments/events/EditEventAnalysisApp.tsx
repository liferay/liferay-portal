import BundleRouter from 'route-middleware/BundleRouter';
import EventAnalysisCreate from 'event-analysis/pages/Create';
import EventAnalysisEdit from 'event-analysis/pages/Edit';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';
import {config} from 'shared/util/config';

const EventAnalysisEditor = () => {
	const {id} = config.params;

	return (
		<BundleRouter
			data={id ? EventAnalysisEdit : EventAnalysisCreate}
			destructured={false}
		/>
	);
};

interface IEditEventAnalysisParams {
	[name: string]: string | undefined;
	id?: string;
}

const EditEventAnalysisApp: React.FC<
	IPageAppProps<IEditEventAnalysisParams>
> = (props) => (
	<PageApp {...props}>
		<EventAnalysisEditor />
	</PageApp>
);

export default EditEventAnalysisApp;
