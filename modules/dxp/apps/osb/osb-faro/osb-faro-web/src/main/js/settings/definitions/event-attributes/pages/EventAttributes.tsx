import BasePage from 'settings/components/BasePage';
import React from 'react';
import TabsCard from '../components/TabsCard';
import {getDefinitions} from 'shared/util/breadcrumbs';

interface IEventAttributesProps {
	groupId: string;
}

const EventAttributes: React.FC<IEventAttributesProps> = ({groupId}) => (
	<BasePage
		breadcrumbItems={[
			getDefinitions({groupId}),
			{active: true, label: Liferay.Language.get('event-attributes')}
		]}
		pageDescription={Liferay.Language.get(
			'attributes-provide-additional-context-for-events.-they-are-usually-event-specific-but-can-be-used-by-more-than-one.-global-attributes-will-be-sent-with-all-events-without-needing-to-be-configured'
		)}
		pageTitle={Liferay.Language.get('event-attributes')}
	>
		<TabsCard groupId={groupId} />
	</BasePage>
);

export default EventAttributes;
