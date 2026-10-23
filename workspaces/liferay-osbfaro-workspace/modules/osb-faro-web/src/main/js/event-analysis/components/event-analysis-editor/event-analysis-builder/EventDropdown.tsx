import BaseDropdown from './base-dropdown';
import Constants from 'shared/util/constants';
import EVENT_DEFINITION_QUERY, {
	UPDATE_EVENT_DEFINITION,
} from 'event-analysis/queries/EventDefinitionQuery';
import EVENT_DEFINITIONS_QUERY, {
	EventDefinitionsData,
	EventDefinitionsVariables,
} from 'event-analysis/queries/EventDefinitionsQuery';
import EditAttributeEventModal from 'shared/components/modals/EditAttributeEventModal';
import React, {useState} from 'react';
import {Align} from '@clayui/drop-down';
import {Attribute, Event, EventTypes} from 'event-analysis/utils/types';
import {DISPLAY_NAME} from 'shared/util/pagination';
import {SafeResults} from 'shared/hoc/util';
import {useModal} from '@clayui/modal';
import {useQuery} from '@apollo/client';

const {
	pagination: {orderDefault},
} = Constants;

interface IAnalysisDropdownProps {
	alignmentPosition?: (typeof Align)[keyof typeof Align];
	onEventChange: (event: Event) => void;
	trigger: React.ReactElement;
}

const AnalysisDropdown: React.FC<IAnalysisDropdownProps> = ({
	alignmentPosition = Align.RightTop,
	onEventChange,
	trigger,
}) => {
	const editModal = useModal();

	const [editingId, setEditingId] = useState<string>();

	const [query, setQuery] = useState('');
	const [eventType, setEventType] = useState<EventTypes>(EventTypes.All);

	const result = useQuery<EventDefinitionsData | EventDefinitionsVariables>(
		EVENT_DEFINITIONS_QUERY,
		{
			fetchPolicy: 'network-only',
			variables: {
				eventType,
				hidden: false,
				keyword: '',
				page: 0,
				size: 200,
				sort: {
					column: DISPLAY_NAME,
					type: orderDefault.toUpperCase(),
				},
			},
		}
	);

	return (
		<>
			<BaseDropdown
				alignmentPosition={alignmentPosition}
				trigger={trigger}
			>
				{({setActive}) => (
					<>
						<BaseDropdown.Header
							activeTabId={eventType}
							tabs={[
								{
									onClick: () => setEventType(EventTypes.All),
									tabId: EventTypes.All,
									title: Liferay.Language.get('all'),
								},
								{
									onClick: () =>
										setEventType(EventTypes.Default),
									tabId: EventTypes.Default,
									title: Liferay.Language.get('default'),
								},
								{
									onClick: () =>
										setEventType(EventTypes.Custom),
									tabId: EventTypes.Custom,
									title: Liferay.Language.get('custom'),
								},
							]}
							title={Liferay.Language.get('events')}
						/>

						<SafeResults
							{...result}
							page={false}
							pageDisplay={false}
						>
							{({
								eventDefinitions: {eventDefinitions},
							}: {
								eventDefinitions: {eventDefinitions: Event[]};
							}) => (
								<BaseDropdown.SearchableList
									items={eventDefinitions}
									onEditClick={(item?: Attribute | Event) => {
										if (!item) {
											return;
										}

										setEditingId(item.id);

										editModal.onOpenChange(true);

										setActive(false);
									}}
									onItemClick={(item: Attribute | Event) => {
										onEventChange(item as Event);

										setActive(false);
										setEventType(EventTypes.All);
										setQuery('');
									}}
									onQueryChange={setQuery}
									query={query}
								/>
							)}
						</SafeResults>
					</>
				)}
			</BaseDropdown>

			{editModal.open && editingId && (
				<EditAttributeEventModal
					id={editingId}
					mutation={UPDATE_EVENT_DEFINITION}
					observer={editModal.observer}
					onClose={(save: boolean) => {
						if (save) {
							result.refetch();
						}

						editModal.onOpenChange(false);
					}}
					query={EVENT_DEFINITION_QUERY}
				/>
			)}
		</>
	);
};

export default AnalysisDropdown;
