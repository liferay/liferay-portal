import BasePage from 'settings/components/base-page/BasePage';
import EVENT_DEFINITION_QUERY, {
	EventDefinitionData,
	EventDefinitionVariables,
	UPDATE_EVENT_DEFINITION,
} from 'event-analysis/queries/EventDefinitionQuery';
import EditAttributeEventModal from 'shared/components/modals/EditAttributeEventModal';
import EventDetailsCard from '../components/EventDetailsCard';
import React from 'react';
import {Event} from 'event-analysis/utils/types';
import {getDefinitions, getEvents} from 'shared/util/breadcrumbs';
import {SafeResults} from 'shared/hoc/util';
import {useModal} from '@clayui/modal';
import {useQuery} from '@apollo/client';

interface IViewProps extends React.HTMLAttributes<HTMLElement> {
	eventId: string;
	groupId: string;
}

const View: React.FC<IViewProps> = ({eventId, groupId}) => {
	const result = useQuery<EventDefinitionData, EventDefinitionVariables>(
		EVENT_DEFINITION_QUERY,
		{
			variables: {id: eventId},
		}
	);

	const editModal = useModal();

	const viewEventPageActions = [
		{
			label: Liferay.Language.get('edit'),
			onClick: () => editModal.onOpenChange(true),
		},
	];

	return (
		<>
			<SafeResults {...result}>
				{({
					eventDefinition: {
						description,
						displayName,
						eventAttributeDefinitions,
						name,
					},
				}: {
					eventDefinition: Event;
				}) => (
					<BasePage
						breadcrumbItems={[
							getDefinitions({groupId}),
							getEvents({groupId}),
							{active: true, label: displayName || name},
						]}
						pageActions={viewEventPageActions}
						pageDescription={
							description ||
							Liferay.Language.get('no-description')
						}
						pageTitle={name}
						subTitle={displayName}
					>
						<EventDetailsCard
							eventAttributes={eventAttributeDefinitions ?? []}
							eventName={name}
							groupId={groupId}
						/>
					</BasePage>
				)}
			</SafeResults>

			{editModal.open && (
				<EditAttributeEventModal
					id={eventId}
					mutation={UPDATE_EVENT_DEFINITION}
					observer={editModal.observer}
					onClose={() => editModal.onOpenChange(false)}
					query={EVENT_DEFINITION_QUERY}
				/>
			)}
		</>
	);
};

export default View;
