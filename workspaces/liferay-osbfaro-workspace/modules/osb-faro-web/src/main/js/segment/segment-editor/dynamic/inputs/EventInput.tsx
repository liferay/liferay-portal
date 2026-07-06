import Alert from '@clayui/alert';
import AttributeConjunctionInput from './components/attribute-conjunction-input';
import DateFilterConjunctionInput from './components/DateFilterConjunctionInput';
import EventPropertiesQuery, {
	EventPropertiesData,
	EventPropertiesVariables,
} from '../queries/EventPropertiesQuery';
import Form from 'shared/components/form';
import OccurenceConjunctionInput from './components/OccurenceConjunctionInput';
import React, {useCallback, useEffect, useMemo, useState} from 'react';
import RealTimePeriodInput, {
	DEFAULT_OPTIONS,
} from './components/RealTimePeriodInput';
import {Attribute, DataTypes} from 'event-analysis/utils/types';
import {Criterion, ISegmentEditorCustomInputBase} from '../utils/types';
import {CustomValue} from 'shared/util/records';
import {fromJS, Map} from 'immutable';
import {FunctionalOperators, RelationalOperators} from '../utils/constants';
import {
	getFilterCriterionIMap,
	getIndexFromPropertyName,
} from '../utils/custom-inputs';
import {isBoolean, isNil} from 'lodash';
import {NAME} from 'shared/util/pagination';
import {OrderByDirections, SegmentTypes} from 'shared/util/constants';
import {SafeResults} from 'shared/hoc/util';
import {useQuery} from '@apollo/client';

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
		}: {
			attribute?: Attribute;
			criterion: Criterion;
			touched: {attribute: boolean; attributeValue: boolean};
			valid: {attribute: boolean; attributeValue: boolean};
		}) => {
			onChange({
				touched: {...touched, ...conjunctionTouched},
				valid: {...valid, ...conjunctionValid},
				value: valueIMap.mergeIn(
					['criterionGroup', 'items', 1],
					fromJS(criterion)
				),
			});

			if (attribute) {
				setSelectedCustomAttribute(attribute);
			}
		},
		[onChange, valueIMap, touched, valid]
	);

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

	const result = useQuery<EventPropertiesData, EventPropertiesVariables>(
		EventPropertiesQuery,
		{
			variables: {
				eventId,
				keyword: '',
				page: 0,
				size: 25,
				sort: {
					column: NAME,
					type: OrderByDirections.Ascending,
				},
			},
		}
	);

	const isRealTime = segmentType === SegmentTypes.RealTime;
	const isSelectedAttributeDateType =
		selectedCustomAttribute?.dataType === DataTypes.Date;

	const initialPeriod = getRealTimePeriodFromCriterion();

	return (
		<div className="criteria-statement">
			<SafeResults {...result} page={false} pageDisplay={false}>
				{(data: any) => {
					const rawAttributes =
						data?.eventProperties?.eventProperties || [];
					const attributes = rawAttributes.map((attr: Attribute) => ({
						...attr,
					}));

					return (
						<>
							<Form.Group autoFit>
								<Form.GroupItem
									className="font-weight-semibold text-secondary"
									label
									shrink
								>
									{Liferay.Language.get('individual')}
								</Form.GroupItem>

								<OperatorDropdown />

								<Form.GroupItem
									className="entity-name"
									label
									shrink
								>
									{Liferay.Language.get(
										'triggered'
									).toLowerCase()}
								</Form.GroupItem>

								<Form.GroupItem
									className="display-value"
									label
									shrink
								>
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
										valueIMap.get(
											'operator'
										) as FunctionalOperators &
											RelationalOperators
									}
									touched={touched.occurenceCount}
									valid={valid.occurenceCount}
									value={valueIMap.get('value')}
								/>

								{isRealTime ? (
									<RealTimePeriodInput
										initialInterval={
											initialPeriod?.interval
										}
										initialTimeWindow={
											initialPeriod?.timeWindow
										}
										onChange={handleRealTimePeriodChange}
									/>
								) : (
									<DateFilterConjunctionInput
										conjunctionCriterion={
											dateFilterConjunctionCriterion
										}
										onChange={
											handleDateFilterConjunctionChange
										}
									/>
								)}
							</Form.Group>

							{!!attributes.length && (
								<Form.Group autoFit>
									<Form.GroupItem
										className="conjunction"
										label
										shrink
									>
										{Liferay.Language.get(
											'where-attribute'
										)}
									</Form.GroupItem>

									<AttributeConjunctionInput
										attributes={attributes}
										conjunctionCriterion={getFilterCriterionIMap(
											valueIMap,
											1
										).toJS()}
										onChange={
											handleAttributeConjunctionChange
										}
										touched={{
											attribute: touched.attribute,
											attributeValue:
												touched.attributeValue,
										}}
										valid={{
											attribute: valid.attribute,
											attributeValue:
												valid.attributeValue,
										}}
									/>
								</Form.Group>
							)}

							{isRealTime && isSelectedAttributeDateType && (
								<Alert
									className="mt-2"
									displayType="info"
									variant="feedback"
								>
									{Liferay.Language.get(
										'event-date-attributes-may-create-time-conflicts-and-reduce-matching-users.-review-your-criteria-to-ensure-the-segment-behaves-as-expected'
									)}
								</Alert>
							)}
						</>
					);
				}}
			</SafeResults>
		</div>
	);
};

export default EventInput;
