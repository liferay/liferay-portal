import BaseDropdown from '../base-dropdown';
import EVENT_ATTRIBUTE_DEFINITION_QUERY, {
	UPDATE_EVENT_ATTRIBUTE_DEFINITION,
} from 'event-analysis/queries/EventAttributeDefinitionQuery';
import EVENT_ATTRIBUTE_DEFINITIONS_QUERY, {
	EventAttributeDefinitionsData,
	EventAttributeDefinitionsVariables,
} from 'event-analysis/queries/EventAttributeDefinitionsQuery';
import FilterOptions from './filter';
import getCN from 'classnames';
import EditAttributeEventModal from 'shared/components/modals/EditAttributeEventModal';
import React, {useState} from 'react';
import {Align} from '@clayui/drop-down';
import {
	Attribute,
	AttributeOwnerTypes,
	AttributeTypes,
} from 'event-analysis/utils/types';
import {CSSTransition, TransitionGroup} from 'react-transition-group';
import {DISPLAY_NAME} from 'shared/util/pagination';
import {
	getModifiedEventAttributeDefinitions,
	getTabs,
} from 'event-analysis/utils/utils';
import {OrderByDirections} from 'shared/util/constants';
import {SafeResults} from 'shared/hoc/util';
import {useModal} from '@clayui/modal';
import {useQuery} from '@apollo/client';

interface IAttributeFilterDropdownProps {
	alignmentPosition?: (typeof Align)[keyof typeof Align];
	disabledIds?: string[];
	eventId: string;
	trigger: React.ReactElement;
	uneditableIds: string[];
}

const AttributeFilterDropdown: React.FC<IAttributeFilterDropdownProps> = ({
	alignmentPosition = Align.RightTop,
	disabledIds,
	eventId,
	trigger,
	uneditableIds,
}) => {
	const editModal = useModal();

	const [editingId, setEditingId] = useState<string>();

	const [attributeOwnerType, setAttributeOwnerType] =
		useState<AttributeOwnerTypes>(AttributeOwnerTypes.Event);
	const [query, setQuery] = useState('');
	const [selectedAttribute, setSelectedAttribute] =
		useState<Attribute | null>(null);

	const result = useQuery<
		EventAttributeDefinitionsData,
		EventAttributeDefinitionsVariables
	>(EVENT_ATTRIBUTE_DEFINITIONS_QUERY, {
		fetchPolicy: 'network-only',
		variables: {
			eventDefinitionId: eventId,
			keyword: '',
			page: 0,
			size: 200,
			sort: {
				column: DISPLAY_NAME,
				type: OrderByDirections.Ascending,
			},
			type: AttributeTypes.All,
		},
	});

	const onClose = (save: boolean) => {
		if (save) {
			result.refetch();
		}

		editModal.onOpenChange(false);
	};

	return (
		<>
			<BaseDropdown
				alignmentPosition={alignmentPosition}
				className="event-analysis-editor-attribute-dropdown-root"
				onActiveChange={(active) => {
					if (!active) {
						setAttributeOwnerType(AttributeOwnerTypes.Event);
						setQuery('');
						setSelectedAttribute(null);
					}
				}}
				trigger={trigger}
			>
				{({setActive}) => (
					<TransitionGroup
						className={getCN('transition-carousel-group', {
							'show-overflow': selectedAttribute,
						})}
					>
						{!selectedAttribute && (
							<CSSTransition
								classNames="transition-attribute-carousel-right"
								timeout={250}
							>
								<div className="d-flex flex-column">
									<BaseDropdown.Header
										activeTabId={attributeOwnerType}
										tabs={getTabs(setAttributeOwnerType)}
										title={Liferay.Language.get(
											'attributes'
										)}
									/>

									<SafeResults
										page={false}
										pageDisplay={false}
										{...result}
									>
										{({
											eventAttributeDefinitions: {
												eventAttributeDefinitions,
											},
										}: {
											eventAttributeDefinitions: {
												eventAttributeDefinitions: Attribute[];
											};
										}) => {
											const modifiedEventAttributeDefinitions =
												getModifiedEventAttributeDefinitions(
													{
														attributeOwnerType,
														eventAttributeDefinitions,
													}
												);

											return (
												<BaseDropdown.SearchableList
													disabledIds={disabledIds}
													items={
														modifiedEventAttributeDefinitions
													}
													onEditClick={(item) => {
														if (!item) {
															return;
														}

														setEditingId(item.id);

														editModal.onOpenChange(
															true
														);

														setActive(false);
													}}
													onItemClick={(item) => {
														setSelectedAttribute(
															item as Attribute
														);
													}}
													onQueryChange={setQuery}
													query={query}
													showInfoCard={
														attributeOwnerType ===
														AttributeOwnerTypes.Event
													}
													uneditableIds={
														uneditableIds
													}
												/>
											);
										}}
									</SafeResults>
								</div>
							</CSSTransition>
						)}

						{selectedAttribute && (
							<CSSTransition
								classNames="transition-attribute-carousel-left"
								timeout={250}
							>
								<div className="w-100">
									<FilterOptions
										attribute={selectedAttribute!}
										attributeOwnerType={attributeOwnerType}
										eventId={eventId}
										onActiveChange={setActive}
										onAttributeChange={(attribute) => {
											setSelectedAttribute(
												attribute ?? null
											);
										}}
										onEditClick={
											uneditableIds &&
											uneditableIds.some(
												(uneditableAttributeId) =>
													uneditableAttributeId ===
													selectedAttribute.id
											)
												? undefined
												: () => {
														setEditingId(
															selectedAttribute.id
														);

														editModal.onOpenChange(
															true
														);

														setActive(false);
													}
										}
									/>
								</div>
							</CSSTransition>
						)}
					</TransitionGroup>
				)}
			</BaseDropdown>

			{editModal.open && editingId && (
				<EditAttributeEventModal
					id={editingId}
					mutation={UPDATE_EVENT_ATTRIBUTE_DEFINITION}
					observer={editModal.observer}
					onClose={onClose}
					query={EVENT_ATTRIBUTE_DEFINITION_QUERY}
					showTypecast
				/>
			)}
		</>
	);
};

export default AttributeFilterDropdown;
