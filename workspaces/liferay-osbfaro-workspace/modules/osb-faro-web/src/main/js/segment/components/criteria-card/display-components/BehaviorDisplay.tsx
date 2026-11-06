import AttributeConjunctionDisplay from './AttributeConjunctionDisplay';
import DateFilterConjunctionDisplay from './DateFilterConjunctionDisplay';
import OccurenceConjunctionDisplay from './OccurenceConjunctionDisplay';
import React from 'react';
import ReferencedEntityDisplay from './ReferencedEntityDisplay';
import {ASSET_TYPE_LANG_MAP} from 'shared/util/lang';
import {CustomValue} from 'shared/util/records';
import {EntityType} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {
	getActivityKeysFromValue,
	getFilterCriterionIMapByPropertyName,
	getFilterCriterionIMapByPropertyNamePrefix,
	getFilterValueByPropertyName,
	hasAttributeFilterCriterion,
} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {getOperatorLabel, maybeFormatToKnownType} from '../utils';
import {IDisplayComponentProps} from '../types';
import {Map} from 'immutable';
import {parseActivityKey} from 'segment/segment-editor/dynamic/utils/utils';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {getSegmentCategoryLabel} from 'segment/utils/labels';

const BehaviorDisplay: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
	segmentCategory,
	segmentType,
}) => {
	const {operatorName, value} = criterion;

	const valueIMap = value as CustomValue;

	const {label, type} = property;

	const isAccountSegment = segmentCategory === SegmentCategories.Account;

	const activityKeys = getActivityKeysFromValue(valueIMap);

	// A behavior with no specific asset stores its applicationId; show it as the
	// type label ("Documents and Media").

	const singleApplicationId = activityKeys.length
		? undefined
		: getFilterValueByPropertyName(valueIMap, 'applicationId');

	const operatorKey = maybeFormatToKnownType(operatorName ?? '', name);

	const operatorLabel = getOperatorLabel(operatorKey, type);

	const eventOperator = valueIMap.get('operator');

	const occurenceCount = valueIMap.get('value');

	const conjunctionCriterion = (
		getFilterCriterionIMapByPropertyName(valueIMap, 'day') ||
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

	return (
		<>
			{getSegmentCategoryLabel(segmentCategory)}

			<span>{operatorLabel}</span>

			{isAccountSegment && (
				<span>
					{Liferay.Language.get(
						'at-least-one-individual-who-triggered'
					)}
				</span>
			)}

			<span>{label}</span>

			{activityKeys.map((activityKey, index) => {
				const {id, objectType} = parseActivityKey(activityKey);

				return (
					<React.Fragment key={activityKey}>
						{index > 0 && <span>{','}</span>}

						<ReferencedEntityDisplay
							id={id}
							label={
								ASSET_TYPE_LANG_MAP[
									objectType as keyof typeof ASSET_TYPE_LANG_MAP
								]
							}
							type={EntityType.Assets}
						/>
					</React.Fragment>
				);
			})}

			{singleApplicationId && (
				<span>
					{ASSET_TYPE_LANG_MAP[
						singleApplicationId as keyof typeof ASSET_TYPE_LANG_MAP
					] ?? singleApplicationId}
				</span>
			)}

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

export default BehaviorDisplay;
