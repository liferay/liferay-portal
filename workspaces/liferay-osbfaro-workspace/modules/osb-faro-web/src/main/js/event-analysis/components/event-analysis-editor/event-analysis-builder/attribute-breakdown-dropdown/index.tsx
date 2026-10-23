import BaseDropdown from '../base-dropdown';
import BreakdownOptions from './options';
import EventAttributeDefinitionQuery, {
	UPDATE_EVENT_ATTRIBUTE_DEFINITION,
} from 'event-analysis/queries/EventAttributeDefinitionQuery';
import EventAttributeDefinitionsQuery, {
	EventAttributeDefinitionsData,
	EventAttributeDefinitionsVariables,
} from 'event-analysis/queries/EventAttributeDefinitionsQuery';
import EditAttributeEventModal from 'shared/components/modals/EditAttributeEventModal';
import React, {useState} from 'react';
import {AddBreakdown} from '../../context/attributes';
import {Align} from '@clayui/drop-down';
import {DataTypes} from 'shared/types/DataTypes';
import {
	Attribute,
	AttributeOwnerTypes,
	AttributeTypes,
} from 'event-analysis/utils/types';
import {
	BREAKDOWN_FNS_MAP,
	getModifiedEventAttributeDefinitions,
	getTabs,
} from 'event-analysis/utils/utils';
import {CSSTransition, TransitionGroup} from 'react-transition-group';
import {DISPLAY_NAME} from 'shared/util/pagination';
import {OrderByDirections} from 'shared/util/constants';
import {SafeResults} from 'shared/hoc/util';
import {useModal} from '@clayui/modal';
import {useQuery} from '@apollo/client';

interface IAttributeBreakdownDropdownProps {
	alignmentPosition?: (typeof Align)[keyof typeof Align];
	disabledIds: string[];
	eventId: string;
	onAttributeSelect: AddBreakdown;
	trigger: React.ReactElement;
	uneditableIds: string[];
}

const AttributeBreakdownDropdown: React.FC<
	IAttributeBreakdownDropdownProps
> = ({
	alignmentPosition = Align.RightTop,
	disabledIds,
	eventId,
	onAttributeSelect,
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
	>(EventAttributeDefinitionsQuery, {
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

	const hasOptions = (attribute: Attribute) =>
		[DataTypes.Date, DataTypes.Duration, DataTypes.Number].includes(
			attribute.dataType
		);

	const showOptions = selectedAttribute && hasOptions(selectedAttribute);

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
					<TransitionGroup className="transition-carousel-group">
						{(!selectedAttribute || !showOptions) && (
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
														const attribute =
															item as Attribute;
														const {
															dataType,
															description,
															displayName,
															id: newAttributeId,
														} = attribute;

														const breakdownFn =
															BREAKDOWN_FNS_MAP[
																dataType
															];

														onAttributeSelect({
															attribute,
															breakdown:
																breakdownFn({
																	attributeId:
																		newAttributeId,
																	attributeType:
																		attributeOwnerType,
																	description,
																	displayName:
																		displayName ??
																		'',
																}),
														});

														setActive(false);
													}}
													onItemOptionsClick={
														setSelectedAttribute
													}
													onQueryChange={setQuery}
													query={query}
													showInfoCard={
														attributeOwnerType ===
														AttributeOwnerTypes.Event
													}
													showOptionsCondition={
														hasOptions
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

						{showOptions && (
							<CSSTransition
								classNames="transition-attribute-carousel-left"
								timeout={250}
							>
								<div className="w-100">
									<BreakdownOptions
										attribute={selectedAttribute!}
										attributeOwnerType={attributeOwnerType}
										onActiveChange={setActive}
										onAttributeChange={(attribute) => {
											setSelectedAttribute(
												attribute ?? null
											);
										}}
										onEditClick={
											uneditableIds &&
											selectedAttribute &&
											uneditableIds.some(
												(uneditableAttributeId) =>
													uneditableAttributeId ===
													selectedAttribute.id
											)
												? undefined
												: () => {
														setEditingId(
															selectedAttribute?.id
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
					query={EventAttributeDefinitionQuery}
					showTypecast
				/>
			)}
		</>
	);
};

export default AttributeBreakdownDropdown;
