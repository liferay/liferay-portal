import AttributeFilterSection from './components/AttributeFilterSection';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import DateFilterConjunctionInput from './components/DateFilterConjunctionInput';
import Form from 'shared/components/form';
import OccurenceConjunctionInput from './components/OccurenceConjunctionInput';
import React from 'react';
import SelectPageAssetInput, {
	BehaviorSelection,
	PageAssetItem,
} from './components/SelectPageAssetInput';
import {
	ACTIVITY_KEY,
	ATTRIBUTE_PROPERTY_PREFIX,
	Conjunctions,
	FunctionalOperators,
	RelationalOperators,
} from '../utils/constants';
import {SegmentTypes} from 'shared/util/constants';
import {
	AttributeConjunctionChangeParams,
	Criterion,
	ISegmentEditorCustomInputBase,
} from '../utils/types';
import {CustomValue} from 'shared/util/records';
import {
	EntityType,
	ReferencedObjectsContext,
} from '../context/referencedObjects';
import {fromJS, List, Map} from 'immutable';
import {
	getActivityKeysFromValue,
	getFilterCriterionIMapByPropertyName,
	getFilterCriterionIMapByPropertyNamePrefix,
	getFilterValueByPropertyName,
	getIndexFromPropertyName,
	getIndexFromPropertyNamePrefix,
	hasAttributeFilterCriterion,
	removeItemsByIndex,
} from '../utils/custom-inputs';
import {isBoolean, isNil, isNull} from 'lodash';
import {Modal} from 'shared/types/Modal';
import {parseActivityKey, parseReferencedEntityId} from '../utils/utils';

type Touched = {
	asset: boolean;
	attribute: boolean;
	attributeValue: boolean;
	dateFilter: boolean;
	occurenceCount: boolean;
};

type Valid = {
	asset: boolean;
	attribute: boolean;
	attributeValue: boolean;
	dateFilter: boolean;
	occurenceCount: boolean;
};

interface IBehaviorInputProps extends ISegmentEditorCustomInputBase {
	channelId: string;
	close: Modal.close;
	open: Modal.open;
	segmentType: SegmentTypes;
	touched: Touched;
	valid: Valid;
}

interface IBehaviorInputState {
	showAttributeFilter: boolean;
}

export class BehaviorInput extends React.Component<
	IBehaviorInputProps,
	IBehaviorInputState
> {
	static contextType = ReferencedObjectsContext;

	constructor(props: IBehaviorInputProps) {
		super(props);
		this.handlePageAssetSelect = this.handlePageAssetSelect.bind(this);
		this.handleAttributeConjunctionChange =
			this.handleAttributeConjunctionChange.bind(this);
		this.handleClearAttributeFilter =
			this.handleClearAttributeFilter.bind(this);
		this.handleDateFilterConjunctionChange =
			this.handleDateFilterConjunctionChange.bind(this);
		this.handleOccurenceConjunctionChange =
			this.handleOccurenceConjunctionChange.bind(this);
		this.handleShowAttributeFilterClick =
			this.handleShowAttributeFilterClick.bind(this);

		this.state = {
			showAttributeFilter: hasAttributeFilterCriterion(
				props.value,
				ATTRIBUTE_PROPERTY_PREFIX
			),
		};
	}

	declare context: React.ContextType<typeof ReferencedObjectsContext>;

	getAttributeCriterionIMap(value: CustomValue) {
		return getFilterCriterionIMapByPropertyNamePrefix(
			value,
			ATTRIBUTE_PROPERTY_PREFIX
		);
	}

	getAttributeIndex(value: CustomValue) {
		return getIndexFromPropertyNamePrefix(value, ATTRIBUTE_PROPERTY_PREFIX);
	}

	getConjunctionDateFilterIMap(value: CustomValue) {
		return getFilterCriterionIMapByPropertyName(value, 'day');
	}

	// The applicationId hint the selector uses on reload to preselect the type:
	// a specific-asset criterion yields the activityKey's applicationId,
	// otherwise the stored applicationId.

	getApplicationId(): string | undefined {
		const {value} = this.props;

		const [activityKey] = getActivityKeysFromValue(value);

		if (activityKey) {
			return parseActivityKey(activityKey).objectType;
		}

		return getFilterValueByPropertyName(value, 'applicationId');
	}

	getObjectDefinitionName(): string | undefined {
		return getFilterValueByPropertyName(
			this.props.value,
			'objectDefinitionName'
		);
	}

	getEventId(): string {
		const {value} = this.props;

		const [activityKey] = getActivityKeysFromValue(value);

		if (activityKey) {
			return parseActivityKey(activityKey).eventId;
		}

		return getFilterValueByPropertyName(value, 'eventId') ?? '';
	}

	// Resolves each selected activityKey back to a {id, name} chip, looking up
	// the name in the referenced entities (falling back to the id).

	getSelectedItems(): Array<PageAssetItem & {activityKey: string}> {
		const {value} = this.props;
		const {referencedEntities} = this.context;

		return getActivityKeysFromValue(value).map((activityKey) => {
			const {id} = parseActivityKey(activityKey);

			const entity = referencedEntities.getIn([
				EntityType.Assets,
				parseReferencedEntityId(
					id,
					referencedEntities,
					EntityType.Assets
				),
			]);

			return {activityKey, id, name: entity ? entity.get('name') : id};
		});
	}

	handlePageAssetSelect({
		applicationId,
		eventId,
		objectDefinitionName,
		selections,
	}: BehaviorSelection) {
		const {
			context: {addEntities},
			props: {onChange, touched, valid, value},
		} = this;

		const previousEventId = this.getEventId();

		const activityKeys = selections.map(({activityKey}) => activityKey);

		if (selections.length) {
			addEntities?.({
				entityType: EntityType.Assets,
				payload: selections.map(({id, name}) => Map({id, name})),
			});
		}

		// Specific assets -> match them by activityKey (a flat item, or an "or"
		// group for N). No specific asset -> match every asset of the selected
		// type via applicationId + eventId ("triggered Download on Documents").

		const assetItems = activityKeys.length
			? [
					activityKeys.length > 1
						? {
								conjunctionName: Conjunctions.Or,
								items: activityKeys.map((activityKey) => ({
									operatorName: RelationalOperators.EQ,
									propertyName: ACTIVITY_KEY,
									value: activityKey,
								})),
							}
						: {
								operatorName: RelationalOperators.EQ,
								propertyName: ACTIVITY_KEY,
								value: activityKeys[0],
							},
				]
			: [
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'applicationId',
						value: applicationId,
					},
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'eventId',
						value: eventId,
					},
				];

		const items = value.getIn(['criterionGroup', 'items']) as List<any>;

		const dayItem = items.find(
			(item: any) => item.get?.('propertyName') === 'day'
		);

		const attributeIndex = this.getAttributeIndex(value);
		const attributeItem =
			attributeIndex >= 0 ? items.get(attributeIndex) : undefined;
		const keepAttribute = attributeItem && previousEventId === eventId;

		onChange({
			touched: {...touched, asset: true},
			valid: {...valid, asset: true},
			value: value.setIn(
				['criterionGroup', 'items'],
				List([
					...assetItems.map((item) => fromJS(item)),
					...(objectDefinitionName
						? [
								fromJS({
									operatorName: RelationalOperators.EQ,
									propertyName: 'objectDefinitionName',
									value: objectDefinitionName,
								}),
							]
						: []),
					...(keepAttribute ? [attributeItem] : []),
					...(dayItem ? [dayItem] : []),
				])
			) as CustomValue,
		});
	}

	handleAttributeConjunctionChange({
		criterion,
		touched: conjunctionTouched,
		valid: conjunctionValid,
	}: AttributeConjunctionChangeParams) {
		const {onChange, touched, valid, value} = this.props;

		const attributeIndex = this.getAttributeIndex(value);

		const nextValue =
			attributeIndex >= 0
				? (value.mergeIn(
						['criterionGroup', 'items', attributeIndex],
						fromJS(criterion)
					) as CustomValue)
				: (value.updateIn(
						['criterionGroup', 'items'],
						(items: List<any>) => items.push(fromJS(criterion))
					) as CustomValue);

		onChange({
			touched: {...touched, ...conjunctionTouched},
			valid: {...valid, ...conjunctionValid},
			value: nextValue,
		});
	}

	handleClearAttributeFilter() {
		const {onChange, touched, valid, value} = this.props;

		const attributeIndex = this.getAttributeIndex(value);

		const nextValue =
			attributeIndex >= 0
				? removeItemsByIndex(value, [attributeIndex])
				: value;

		this.setState({showAttributeFilter: false});

		onChange({
			touched: {...touched, attribute: false, attributeValue: false},
			valid: {...valid, attribute: true, attributeValue: true},
			value: nextValue,
		});
	}

	handleShowAttributeFilterClick() {
		this.setState({showAttributeFilter: true});
	}

	handleDateFilterConjunctionChange(criterion: Criterion | null) {
		const {onChange, touched, valid, value} = this.props;

		// The day item's position varies (it follows the activityKey item(s), or
		// the applicationId + eventId pair of a single-type criterion), so
		// locate it by property name rather than a fixed index.

		const dayIndex = getIndexFromPropertyName(value, 'day');

		let nextValue = value;

		if (isNull(criterion)) {
			if (dayIndex >= 0) {
				nextValue = value.deleteIn([
					'criterionGroup',
					'items',
					dayIndex,
				]) as CustomValue;
			}
		}
		else if (dayIndex >= 0) {
			nextValue = value.mergeIn(
				['criterionGroup', 'items', dayIndex],
				fromJS(criterion)
			) as CustomValue;
		}
		else {
			nextValue = value.updateIn(
				['criterionGroup', 'items'],
				(items: any) => (items as List<any>).push(fromJS(criterion))
			) as CustomValue;
		}

		onChange({
			touched: {...touched, dateFilter: criterion && criterion.touched},
			valid: {...valid, dateFilter: isNull(criterion) || criterion.valid},
			value: nextValue,
		});
	}

	handleOccurenceConjunctionChange({
		criterion,
		touched: occurenceCountTouched,
		valid: occurenceCountValid,
	}: {
		criterion?: Criterion;
		touched?: boolean;
		valid?: boolean;
	}) {
		const {onChange, touched, valid, value: valueIMap} = this.props;

		let params: {touched?: Touched; valid?: Valid; value?: CustomValue} = {
			touched,
			valid,
		};

		if (criterion?.operatorName) {
			params = {
				...params,
				value: valueIMap.mergeIn(
					['operator'],
					criterion.operatorName
				) as CustomValue,
			};
		}
		else if (!isNil(criterion?.value)) {
			params = {
				...params,
				value: valueIMap.mergeIn(
					['value'],
					criterion.value
				) as CustomValue,
			};
		}

		if (isBoolean(occurenceCountTouched)) {
			params = {
				...params,
				touched: {...touched, occurenceCount: occurenceCountTouched},
			};
		}

		if (isBoolean(occurenceCountValid)) {
			params = {
				...params,
				valid: {...valid, occurenceCount: occurenceCountValid},
			};
		}

		onChange(params);
	}

	render() {
		const {
			channelId,
			displayValue,
			groupId = '',
			operatorRenderer: OperatorDropdown,
			property,
			segmentType,
			touched,
			valid,
			value,
		} = this.props;

		const conjunctionCriterion = (
			this.getConjunctionDateFilterIMap(value) ||
			Map({propertyName: 'day'})
		).toJS();

		const attributeConjunctionCriterion = (
			this.getAttributeCriterionIMap(value) ||
			Map({propertyName: ATTRIBUTE_PROPERTY_PREFIX})
		).toJS();

		return (
			<div className="criteria-statement">
				<Form.Group autoFit className="page-asset-criteria">
					<Form.GroupItem className="entity-name" label shrink>
						{property.entityName}
					</Form.GroupItem>

					<OperatorDropdown />

					<Form.GroupItem className="entity-name" label shrink>
						{Liferay.Language.get('triggered').toLowerCase()}
					</Form.GroupItem>

					<Form.GroupItem className="display-value" label shrink>
						<b>{displayValue}</b>
					</Form.GroupItem>

					<SelectPageAssetInput
						action={property.name}
						actionLabel={displayValue}
						applicationId={this.getApplicationId()}
						channelId={channelId}
						groupId={groupId}
						objectDefinitionName={this.getObjectDefinitionName()}
						onSelectionsChange={this.handlePageAssetSelect}
						selectedItems={this.getSelectedItems()}
					/>
				</Form.Group>

				{segmentType === SegmentTypes.Batch && (
					<Form.Group autoFit>
						<OccurenceConjunctionInput
							onChange={this.handleOccurenceConjunctionChange}
							operatorName={
								value.get('operator') as FunctionalOperators &
									RelationalOperators
							}
							touched={touched.occurenceCount}
							valid={valid.occurenceCount}
							value={value.get('value')}
						/>

						<DateFilterConjunctionInput
							conjunctionCriterion={conjunctionCriterion}
							onChange={this.handleDateFilterConjunctionChange}
						/>
					</Form.Group>
				)}

				{this.state.showAttributeFilter ? (
					<AttributeFilterSection
						conjunctionCriterion={attributeConjunctionCriterion}
						eventId={this.getEventId()}
						onChange={this.handleAttributeConjunctionChange}
						onClear={this.handleClearAttributeFilter}
						touched={touched}
						valid={valid}
					/>
				) : (
					<Form.Group autoFit>
						<ClayButton
							className="button-root"
							displayType="secondary"
							onClick={this.handleShowAttributeFilterClick}
						>
							<ClayIcon symbol="plus" />

							<span className="ml-2">
								{Liferay.Language.get('add-event-attribute')}
							</span>
						</ClayButton>
					</Form.Group>
				)}
			</div>
		);
	}
}

export default BehaviorInput;
