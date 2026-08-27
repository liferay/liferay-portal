import DateFilterConjunctionDisplay from './DateFilterConjunctionDisplay';
import React from 'react';
import {CustomValue} from 'shared/util/records';
import {
	DATELESS_SESSION_PROPERTY_TYPES,
	PropertyTypes,
} from 'segment/segment-editor/dynamic/utils/constants';
import {
	getFilterCriterionIMap,
	getIndexFromPropertyName,
	getOperator,
	getPropertyValue,
} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {
	getOperatorLabel,
	maybeFormatToKnownType,
	maybeFormatValue,
} from '../utils';
import {getUtmParameterLabelByFieldName} from 'segment/segment-editor/dynamic/utils/properties/session-properties';
import {IDisplayComponentProps} from '../types';
import {isOfKnownType} from 'segment/segment-editor/dynamic/utils/utils';
import {Map} from 'immutable';

const SessionDisplay: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
	timeZoneId,
}) => {
	const valueIMap = criterion.value as CustomValue;

	const {entityName, label, options = [], type} = property;

	const operatorName = getOperator(valueIMap, 0);

	const value = getPropertyValue(valueIMap, 'value', 0);

	const operatorKey = maybeFormatToKnownType(operatorName, value);

	const operatorLabel = getOperatorLabel(operatorKey, type);

	let values = [0];

	if (type === PropertyTypes.SessionGeolocation) {
		const cityIndex = getIndexFromPropertyName(valueIMap, 'context/city');
		const countryIndex = getIndexFromPropertyName(
			valueIMap,
			'context/country'
		);
		const regionIndex = getIndexFromPropertyName(
			valueIMap,
			'context/region'
		);

		values = [cityIndex, regionIndex, countryIndex].filter(
			(index) => index > -1
		);
	}

	const conjunctionCriterion = (
		getFilterCriterionIMap(valueIMap, 1) || Map({propertyName: 'date'})
	).toJS();

	// Every UTM Parameter criterion comes from the same sidebar entry, so
	// the property's own label names none of them in particular. The
	// parameter the user picked is the field the criterion filters on.

	const displayLabel =
		type === PropertyTypes.SessionUtmParameter
			? getUtmParameterLabelByFieldName(
					getPropertyValue(valueIMap, 'propertyName', 0) ?? ''
				)
			: label;

	return (
		<>
			{entityName}

			<b>{displayLabel}</b>

			<span>{operatorLabel}</span>

			{!isOfKnownType(operatorKey) && (
				<b>
					{values
						.map((index) => {
							const value = getPropertyValue(
								valueIMap,
								'value',
								index
							);

							// A value chosen from a fixed option list is
							// stored as the option's value, which is not
							// what the editor shows for it.

							const option = options.find(
								(option) => option.value === value
							);

							return maybeFormatValue(
								option ? option.label : value,
								type,
								timeZoneId
							);
						})
						.join(', ')}
				</b>
			)}

			{!DATELESS_SESSION_PROPERTY_TYPES.includes(type) && (
				<DateFilterConjunctionDisplay
					conjunctionCriterion={conjunctionCriterion}
				/>
			)}
		</>
	);
};

export default SessionDisplay;
