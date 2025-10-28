import BasePage from 'settings/components/BasePage';
import React from 'react';
import TabsCard from '../components/TabsCard';
import {getDefinitions} from 'shared/util/breadcrumbs';

interface IEventsProps {
	groupId: string;
}

const Events: React.FC<IEventsProps> = ({groupId}) => (
	<BasePage
		breadcrumbItems={[
			getDefinitions({groupId}),
			{active: true, label: Liferay.Language.get('events')}
		]}
		pageDescription={Liferay.Language.get(
			'this-is-the-data-model-of-events-sent-to-analytics-cloud'
		)}
		pageTitle={Liferay.Language.get('events')}
	>
		<TabsCard groupId={groupId} />
	</BasePage>
);

export default Events;
