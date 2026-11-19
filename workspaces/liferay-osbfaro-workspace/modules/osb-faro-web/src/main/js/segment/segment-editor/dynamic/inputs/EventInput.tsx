import AttributeFilterSection from './components/AttributeFilterSection';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import DateFilterConjunctionInput from './components/DateFilterConjunctionInput';
import Form from 'shared/components/form';
import OccurenceConjunctionInput from './components/OccurenceConjunctionInput';
import React, {useCallback, useMemo, useState} from 'react';
import {
	AttributeConjunctionChangeParams,
	Criterion,
	ISegmentEditorCustomInputBase,
} from '../utils/types';
import {CustomValue} from 'shared/util/records';
import {fromJS, Map} from 'immutable';
import {
	ATTRIBUTE_PROPERTY_PREFIX,
	FunctionalOperators,
	RelationalOperators,
} from '../utils/constants';
import {
	getFilterCriterionIMapByPropertyNamePrefix,
	getIndexFromPropertyNamePrefix,
	hasAttributeFilterCriterion,
	removeItemsByIndex,
} from '../utils/custom-inputs';
import {isBoolean, isNil} from 'lodash';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {getSegmentCategoryLabel} from 'segment/utils/labels';

type Touched = {
	attribute: boolean;
	attributeValue: boolean;
	dateFilter: boolean;
	occurenceCount: boolean;
};

type Valid = {
	attribute: boolean;
	attributeValue: boolean;
	dateFilter: boolean;
	occurenceCount: boolean;
};

interface IEventInputProps extends ISegmentEditorCustomInputBase {
	touched: Touched;
	valid: Valid;
	segmentCategory: SegmentCategories;
	segmentType: SegmentTypes;
}

const EventInput: React.FC<IEventInputProps> = ({
	displayValue,
	onChange,
	operatorRenderer: OperatorDropdown,
	property,
	segmentCategory,
	segmentType,
	touched,
	valid,
	value: valueIMap,
}) => {
	const [showAttributeFilter, setShowAttributeFilter] = useState(() =>
		hasAttributeFilterCriterion(valueIMap, ATTRIBUTE_PROPERTY_PREFIX)
	);
	const {id: eventId, options} = property;

	const getConjunctionDateFilterIMap = (value: CustomValue) => {
		const conjunctionCriterion = value.getIn([
			'criterionGroup',
			'items',
			2,
		]);

		if (conjunctionCriterion) {
			return conjunctionCriterion;
		}
	};

	const handleAttributeConjunctionChange = useCallback(
		({
			criterion,
			touched: conjunctionTouched,
			valid: conjunctionValid,
		}: AttributeConjunctionChangeParams) => {
			const attributeIndex = getIndexFromPropertyNamePrefix(
				valueIMap,
				ATTRIBUTE_PROPERTY_PREFIX
			);

			const nextValue =
				attributeIndex >= 0
					? valueIMap.mergeIn(
							['criterionGroup', 'items', attributeIndex],
							fromJS(criterion)
						)
					: valueIMap.updateIn(
							['criterionGroup', 'items'],
							(items: any) => items.push(fromJS(criterion))
						);

			onChange({
				touched: {...touched, ...conjunctionTouched},
				valid: {...valid, ...conjunctionValid},
				value: nextValue,
			});
		},
		[onChange, valueIMap, touched, valid]
	);

	const handleClearAttributeFilter = useCallback(() => {
		const attributeIndex = getIndexFromPropertyNamePrefix(
			valueIMap,
			ATTRIBUTE_PROPERTY_PREFIX
		);

		const nextValue =
			attributeIndex >= 0
				? removeItemsByIndex(valueIMap, [attributeIndex])
				: valueIMap;

		setShowAttributeFilter(false);

		onChange({
			touched: {...touched, attribute: false, attributeValue: false},
			valid: {...valid, attribute: true, attributeValue: true},
			value: nextValue,
		});
	}, [onChange, valueIMap, touched, valid]);

	const handleShowAttributeFilterClick = useCallback(() => {
		setShowAttributeFilter(true);
	}, []);

	const handleDateFilterConjunctionChange = useCallback(
		(criterion: Criterion | null) => {
			let value: Map<string, any>;

			if (isNil(criterion)) {
				value = valueIMap.deleteIn(['criterionGroup', 'items', 2]);
			}
			else {
				value = valueIMap.mergeIn(
					['criterionGroup', 'items', 2],
					fromJS(criterion)
				);
			}

			onChange({
				touched: {
					...touched,
					dateFilter: criterion && criterion.touched,
				},
				valid: {
					...valid,
					dateFilter: isNil(criterion) || criterion.valid,
				},
				value,
			});
		},
		[onChange, valueIMap, touched, valid]
	);

	const handleOccurenceConjunctionChange = useCallback(
		({
			criterion,
			touched: occurenceCountTouched,
			valid: occurenceCountValid,
		}: {
			criterion?: Criterion;
			touched?: boolean;
			valid?: boolean;
		}) => {
			let params: {
				touched?: Touched;
				valid?: Valid;
				value?: CustomValue;
			} = {
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
					touched: {
						...touched,
						occurenceCount: occurenceCountTouched,
					},
				};
			}

			if (isBoolean(occurenceCountValid)) {
				params = {
					...params,
					valid: {...valid, occurenceCount: occurenceCountValid},
				};
			}

			onChange(params);
		},
		[onChange, valueIMap, touched, valid]
	);

	const dateFilterConjunctionCriterion = useMemo(
		() =>
			(
				getConjunctionDateFilterIMap(valueIMap) ||
				Map({propertyName: 'day'})
			).toJS(),
		[valueIMap]
	);

	if (
		options!.length &&
		options!.some((option) => option.label === 'hidden' && option.value)
	) {
		return (
			<div className="criteria-statement">
				<b className="non-existent-property-message">
					{Liferay.Language.get('custom-event-no-longer-exists')}
				</b>
			</div>
		);
	}

	const isAccountSegment = segmentCategory === SegmentCategories.Account;

	return (
		<div className="criteria-statement">
			<Form.Group autoFit>
				<Form.GroupItem
					className="font-weight-semibold text-secondary"
					label
					shrink
				>
					{getSegmentCategoryLabel(segmentCategory)}
				</Form.GroupItem>

				<OperatorDropdown />

				{isAccountSegment ? (
					<Form.GroupItem className="entity-name" label shrink>
						{Liferay.Language.get(
							'at-least-one-individual-who-triggered'
						)}
					</Form.GroupItem>
				) : (
					<Form.GroupItem className="entity-name" label shrink>
						{Liferay.Language.get('triggered').toLowerCase()}
					</Form.GroupItem>
				)}

				<Form.GroupItem className="display-value" label shrink>
					<b>{displayValue}</b>
				</Form.GroupItem>

				{segmentType === SegmentTypes.Batch && (
					<>
						<OccurenceConjunctionInput
							onChange={handleOccurenceConjunctionChange}
							operatorName={
								valueIMap.get(
									'operator'
								) as FunctionalOperators & RelationalOperators
							}
							touched={touched.occurenceCount}
							valid={valid.occurenceCount}
							value={valueIMap.get('value')}
						/>

						<DateFilterConjunctionInput
							conjunctionCriterion={
								dateFilterConjunctionCriterion
							}
							onChange={handleDateFilterConjunctionChange}
						/>
					</>
				)}
			</Form.Group>

			{segmentType === SegmentTypes.Batch &&
				(showAttributeFilter ? (
					<AttributeFilterSection
						conjunctionCriterion={(
							getFilterCriterionIMapByPropertyNamePrefix(
								valueIMap,
								ATTRIBUTE_PROPERTY_PREFIX
							) || Map({propertyName: ATTRIBUTE_PROPERTY_PREFIX})
						).toJS()}
						eventId={eventId}
						onChange={handleAttributeConjunctionChange}
						onClear={handleClearAttributeFilter}
						touched={{
							attribute: touched.attribute,
							attributeValue: touched.attributeValue,
						}}
						valid={{
							attribute: valid.attribute,
							attributeValue: valid.attributeValue,
						}}
					/>
				) : (
					<Form.Group autoFit>
						<ClayButton
							className="button-root"
							displayType="secondary"
							onClick={handleShowAttributeFilterClick}
						>
							<ClayIcon symbol="plus" />

							<span className="ml-2">
								{Liferay.Language.get('add-event-attribute')}
							</span>
						</ClayButton>
					</Form.Group>
				))}
		</div>
	);
};

export default EventInput;
