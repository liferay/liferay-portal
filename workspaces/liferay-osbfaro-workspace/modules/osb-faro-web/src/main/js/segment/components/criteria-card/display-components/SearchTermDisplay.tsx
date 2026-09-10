import React from 'react';
import {CustomValue} from 'shared/util/records';
import {getPropertyValue} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {IDisplayComponentProps} from '../types';
import {maybeFormatValue} from '../utils';
import {SEARCH_TERM_BOOLEAN_OPTIONS} from 'segment/segment-editor/dynamic/utils/constants';

const SearchTermDisplay: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
	timeZoneId,
}) => {
	const valueIMap = criterion.value as CustomValue;

	const {entityName, type} = property;

	const searchTermName = getPropertyValue(valueIMap, 'value', 0);

	const operatorLabel = SEARCH_TERM_BOOLEAN_OPTIONS.find(
		({value}) => value === getPropertyValue(valueIMap, 'value', 1)
	)?.label;

	return (
		<>
			{entityName}

			<span>{operatorLabel}</span>

			<span>{Liferay.Language.get('searching').toLowerCase()}</span>

			<b>{maybeFormatValue(searchTermName, type, timeZoneId)}</b>
		</>
	);
};

export default SearchTermDisplay;
