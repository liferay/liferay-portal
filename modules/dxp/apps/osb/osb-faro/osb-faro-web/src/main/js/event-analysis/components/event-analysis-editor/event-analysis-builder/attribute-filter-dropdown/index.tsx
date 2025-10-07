import BaseDropdown from '../base-dropdown';
import EVENT_ATTRIBUTE_DEFINITION_QUERY, {
	UPDATE_EVENT_ATTRIBUTE_DEFINITION
} from 'event-analysis/queries/EventAttributeDefinitionQuery';
import EVENT_ATTRIBUTE_DEFINITIONS_QUERY, {
	EventAttributeDefinitionsData,
	EventAttributeDefinitionsVariables
} from 'event-analysis/queries/EventAttributeDefinitionsQuery';
import FilterOptions from './filter';
import getCN from 'classnames';
import React, {useState} from 'react';
import {Align} from '@clayui/drop-down';
import {
	Attribute,
	AttributeOwnerTypes,
	AttributeTypes,
	Filter
} from 'event-analysis/utils/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect, ConnectedProps} from 'react-redux';
import {CSSTransition, TransitionGroup} from 'react-transition-group';
import {DISPLAY_NAME} from 'shared/util/pagination';
import {
	getModifiedEventAttributeDefinitions,
	getTabs
} from 'event-analysis/utils/utils';
import {OrderByDirections} from 'shared/util/constants';
import {SafeResults} from 'shared/hoc/util';
import {useQuery} from '@apollo/react-hooks';

const connector = connect(null, {close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IAttributeFilterDropdownProps extends PropsFromRedux {
	alignmentPosition?: typeof Align[keyof typeof Align];
	attribute?: Attribute;
	disabledIds?: string[];
	eventId: string;
	filter?: Filter;
	trigger: React.ReactElement;
	uneditableIds: string[];
}

const AttributeFilterDropdown: React.FC<IAttributeFilterDropdownProps> = ({
	alignmentPosition = Align.RightTop,
	attribute,
	close,
	disabledIds,
	eventId,
	filter,
	open,
	trigger,
	uneditableIds
}) => {
	const [
		attributeOwnerType,
		setAttributeOwnerType
	] = useState<AttributeOwnerTypes>(AttributeOwnerTypes.Event);
	const [query, setQuery] = useState('');
	const [selectedAttribute, setSelectedAttribute] = useState<Attribute>(
		filter ? attribute : null
	);

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
				type: OrderByDirections.Ascending
			},
			type: AttributeTypes.All
		}
	});

	const attributeId = attribute ? attribute.id : null;
	const filterId = filter ? filter.id : null;

	const onClose = (save: boolean) => {
		if (save) {
			result.refetch();
		}

		close();
	};

	return (
		<BaseDropdown
			alignmentPosition={alignmentPosition}
			className='event-analysis-editor-attribute-dropdown-root'
			onActiveChange={active => {
				if (!active) {
					setAttributeOwnerType(AttributeOwnerTypes.Event);
					setQuery('');
					setSelectedAttribute(filter ? attribute : null);
				}
			}}
			trigger={trigger}
		>
			{({setActive}) => (
				<TransitionGroup
					className={getCN('transition-carousel-group', {
						'show-overflow': selectedAttribute
					})}
				>
					{!selectedAttribute && (
						<CSSTransition
							classNames='transition-attribute-carousel-right'
							timeout={250}
						>
							<div className='d-flex flex-column'>
								<BaseDropdown.Header
									activeTabId={attributeOwnerType}
									tabs={getTabs(setAttributeOwnerType)}
									title={Liferay.Language.get('attributes')}
								/>

								<SafeResults
									page={false}
									pageDisplay={false}
									{...result}
								>
									{({
										eventAttributeDefinitions: {
											eventAttributeDefinitions
										}
									}: {
										eventAttributeDefinitions: {
											eventAttributeDefinitions: Attribute[];
										};
									}) => {
										const modifiedEventAttributeDefinitions = getModifiedEventAttributeDefinitions(
											{
												attribute,
												attributeOwnerType,
												eventAttributeDefinitions
											}
										);

										return (
											<BaseDropdown.SearchableList
												activeId={attributeId}
												disabledIds={disabledIds}
												items={
													modifiedEventAttributeDefinitions
												}
												onEditClick={(
													attribute: Attribute
												) => {
													open(
														modalTypes.EDIT_ATTRIBUTE_EVENT_MODAL,
														{
															id: attribute.id,
															mutation: UPDATE_EVENT_ATTRIBUTE_DEFINITION,
															onClose,
															query: EVENT_ATTRIBUTE_DEFINITION_QUERY,
															showTypecast: true
														}
													);

													setActive(false);
												}}
												onItemClick={(
													attribute: Attribute
												) => {
													setSelectedAttribute(
														attribute
													);
												}}
												onQueryChange={setQuery}
												query={query}
												showInfoCard={
													attributeOwnerType ===
													AttributeOwnerTypes.Event
												}
												uneditableIds={uneditableIds}
											/>
										);
									}}
								</SafeResults>
							</div>
						</CSSTransition>
					)}

					{selectedAttribute && (
						<CSSTransition
							classNames='transition-attribute-carousel-left'
							timeout={250}
						>
							<div className='w-100'>
								<FilterOptions
									attribute={selectedAttribute}
									attributeOwnerType={attributeOwnerType}
									eventId={eventId}
									filterId={filterId}
									onActiveChange={setActive}
									onAttributeChange={params => {
										setSelectedAttribute(params);
									}}
									onEditClick={
										uneditableIds &&
										uneditableIds.some(
											uneditableAttributeId =>
												uneditableAttributeId ===
												selectedAttribute.id
										)
											? null
											: () => {
													open(
														modalTypes.EDIT_ATTRIBUTE_EVENT_MODAL,
														{
															id:
																selectedAttribute.id,
															mutation: UPDATE_EVENT_ATTRIBUTE_DEFINITION,
															onClose,
															query: EVENT_ATTRIBUTE_DEFINITION_QUERY,
															showTypecast: true
														}
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
	);
};

export default connector(AttributeFilterDropdown);
