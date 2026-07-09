import Alert from '@clayui/alert';
import AttributeFilterSection from './components/AttributeFilterSection';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import DateFilterConjunctionInput from './components/DateFilterConjunctionInput';
import Form from 'shared/components/form';
import OccurenceConjunctionInput from './components/OccurenceConjunctionInput';
import React, {useCallback, useEffect, useMemo, useState} from 'react';
import RealTimePeriodInput, {
	DEFAULT_OPTIONS,
} from './components/RealTimePeriodInput';
import {Attribute, DataTypes} from 'event-analysis/utils/types';
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
	getFilterCriterionIMap,
	getFilterCriterionIMapByPropertyNamePrefix,
	getIndexFromPropertyName,
	getIndexFromPropertyNamePrefix,
	hasAttributeFilterCriterion,
	removeItemsByIndex,
} from '../utils/custom-inputs';
import {isBoolean, isNil} from 'lodash';
import {SegmentTypes} from 'shared/util/constants';

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
	segmentType: SegmentTypes;
}

const EventInput: React.FC<IEventInputProps> = ({
	displayValue,
	onChange,
	operatorRenderer: OperatorDropdown,
	property,
	segmentType,
	touched,
	valid,
	value: valueIMap,
}) => {
	const [selectedCustomAttribute, setSelectedCustomAttribute] =
		useState<Attribute | null>(null);

	const [showAttributeFilter, setShowAttributeFilter] = useState(() =>
		hasAttributeFilterCriterion(valueIMap, ATTRIBUTE_PROPERTY_PREFIX)
	);
	const {id: eventId, options} = property;

	const getRealTimePeriodFromCriterion = useCallback((): {
		interval: number;
		timeWindow: string;
	} | null => {
		const dayCriterion = valueIMap.getIn(['criterionGroup', 'items', 2]);

		if (!dayCriterion) {
			return null;
		}

		const dayValue: string = dayCriterion.get('value');

		if (!dayValue || typeof dayValue !== 'string') {
			return null;
		}

		const parts = dayValue.split('_');

		if (parts.length !== 2) {
			return null;
		}

		const [intervalStr, timeWindow] = parts;
		const interval = parseInt(intervalStr, 10);

		if (isNaN(interval)) {
			return null;
		}

		return {interval, timeWindow};
	}, [valueIMap]);

	const handleRealTimePeriodChange = useCallback(
		(interval: number, timeWindow: string) => {
			const newDayValue = `${interval}_${timeWindow}`;

			const conjunctionDateFilterIndex = getIndexFromPropertyName(
				valueIMap,
				'day'
			);

			let dayCriterion;
			if (conjunctionDateFilterIndex >= 0) {
				const existingDayIMap = getFilterCriterionIMap(
					valueIMap,
					conjunctionDateFilterIndex
				);

				dayCriterion = existingDayIMap.merge({
					operatorName: RelationalOperators.GE,
					touched: true,
					valid: true,
					value: newDayValue,
				});
			}
			else {
				dayCriterion = fromJS({
					operatorName: RelationalOperators.GE,
					propertyName: 'day',
					touched: true,
					valid: true,
					value: newDayValue,
				});
			}

			const updatedValue = valueIMap.mergeIn(
				['criterionGroup', 'items', 2],
				dayCriterion
			);

			onChange({
				touched: {...touched, dateFilter: true},
				valid: {...valid, dateFilter: true},
				value: updatedValue,
			});
		},
		[onChange, valueIMap, touched, valid]
	);

	useEffect(() => {
		if (segmentType === SegmentTypes.RealTime) {
			const currentPeriod = getRealTimePeriodFromCriterion();

			if (!currentPeriod) {
				handleRealTimePeriodChange(
					DEFAULT_OPTIONS.interval,
					DEFAULT_OPTIONS.timeWindow
				);
			}
		}
	}, [
		segmentType,
		getRealTimePeriodFromCriterion,
		handleRealTimePeriodChange,
	]);

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
			attribute,
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

			if (attribute) {
				setSelectedCustomAttribute(attribute);
			}
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

	const isRealTime = segmentType === SegmentTypes.RealTime;
	const isSelectedAttributeDateType =
		selectedCustomAttribute?.dataType === DataTypes.Date;

	const initialPeriod = getRealTimePeriodFromCriterion();

	return (
		<div className="criteria-statement">
			<Form.Group autoFit>
				<Form.GroupItem
					className="font-weight-semibold text-secondary"
					label
					shrink
				>
					{Liferay.Language.get('individual')}
				</Form.GroupItem>

				<OperatorDropdown />

				<Form.GroupItem className="entity-name" label shrink>
					{Liferay.Language.get('triggered').toLowerCase()}
				</Form.GroupItem>

				<Form.GroupItem className="display-value" label shrink>
					<b>{displayValue}</b>
				</Form.GroupItem>

				<OccurenceConjunctionInput
					onChange={
						handleOccurenceConjunctionChange as (params: {
							criterion?: Criterion;
							touched?: boolean;
							valid?: boolean;
						}) => void
					}
					operatorName={
						valueIMap.get('operator') as FunctionalOperators &
							RelationalOperators
					}
					touched={touched.occurenceCount}
					valid={valid.occurenceCount}
					value={valueIMap.get('value')}
				/>

				{isRealTime ? (
					<RealTimePeriodInput
						initialInterval={initialPeriod?.interval}
						initialTimeWindow={initialPeriod?.timeWindow}
						onChange={handleRealTimePeriodChange}
					/>
				) : (
					<DateFilterConjunctionInput
						conjunctionCriterion={dateFilterConjunctionCriterion}
						onChange={handleDateFilterConjunctionChange}
					/>
				)}
			</Form.Group>

			{showAttributeFilter ? (
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
			)}

			{isRealTime && isSelectedAttributeDateType && (
				<Alert className="mt-2" displayType="info" variant="feedback">
					{Liferay.Language.get(
						'event-date-attributes-may-create-time-conflicts-and-reduce-matching-users.-review-your-criteria-to-ensure-the-segment-behaves-as-expected'
					)}
				</Alert>
			)}
		</div>
	);
};

export default EventInput;
