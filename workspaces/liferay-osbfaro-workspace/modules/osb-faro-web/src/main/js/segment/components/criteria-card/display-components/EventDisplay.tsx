import AttributeConjunctionDisplay from './AttributeConjunctionDisplay';
import DateFilterConjunctionDisplay from './DateFilterConjunctionDisplay';
import OccurenceConjunctionDisplay from './OccurenceConjunctionDisplay';
import React from 'react';
import {CustomValue} from 'shared/util/records';
import {
	getFilterCriterionIMap,
	getFilterCriterionIMapByPropertyNamePrefix,
	hasAttributeFilterCriterion,
} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {getOperatorLabel, maybeFormatToKnownType} from '../utils';
import {IDisplayComponentProps} from '../types';
import {Map} from 'immutable';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {getSegmentCategoryLabel} from 'segment/utils/labels';

const EventDisplay: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
	segmentCategory,
	segmentType,
}) => {
	const isAccountSegment = segmentCategory === SegmentCategories.Account;

	const {operatorName, value} = criterion;

	const valueIMap = value as CustomValue;

	const {label, options, type} = property;

	const operatorKey = maybeFormatToKnownType(operatorName ?? '', name);

	const operatorLabel = getOperatorLabel(operatorKey, type);

	const eventOperator = valueIMap.get('operator');

	const occurenceCount = valueIMap.get('value');

	const conjunctionCriterion = (
		getFilterCriterionIMap(valueIMap, 2) ||
		Map({propertyName: 'completeDate'})
	).toJS();

	const attributeCriterion = getFilterCriterionIMapByPropertyNamePrefix(
		valueIMap,
		'attribute/'
	);

	const hasAttributeFilter = hasAttributeFilterCriterion(
		valueIMap,
		'attribute/'
	);

	if (
		options?.length &&
		options.some((option) => option.label === 'hidden' && option.value)
	) {
		return (
			<b className="undefined-property">
				{Liferay.Language.get('custom-event-no-longer-exists')}
			</b>
		);
	}

	return (
		<>
			<span className="sentence-start">
				{getSegmentCategoryLabel(segmentCategory)}
			</span>

			<span>{operatorLabel}</span>

			<span>
				{isAccountSegment
					? Liferay.Language.get(
							'at-least-one-individual-who-triggered'
						)
					: Liferay.Language.get('performed').toLowerCase()}
			</span>

			<b>{label}</b>

			{segmentType === SegmentTypes.Batch && (
				<>
					<OccurenceConjunctionDisplay
						operatorName={eventOperator}
						value={occurenceCount}
					/>

					<DateFilterConjunctionDisplay
						conjunctionCriterion={conjunctionCriterion}
					/>
				</>
			)}

			{hasAttributeFilter && (
				<AttributeConjunctionDisplay
					conjunctionCriterion={attributeCriterion.toJS()}
				/>
			)}
		</>
	);
};

export default EventDisplay;
