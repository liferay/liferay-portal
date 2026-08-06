import LifecycleStageDisplay from './LifecycleStageDisplay';
import React from 'react';
import {CustomValue} from 'shared/util/records';
import {
	getOperator,
	getPropertyValue,
} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {
	getOperatorLabel,
	maybeFormatToKnownType,
	maybeFormatValue,
} from '../utils';
import {IDisplayComponentProps} from '../types';
import {isOfKnownType} from 'segment/segment-editor/dynamic/utils/utils';
import {PropertyTypes} from 'segment/segment-editor/dynamic/utils/constants';

const AccountDisplay: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
	timeZoneId,
}) => {
	const valueIMap = criterion.value as CustomValue;

	const {entityName, label, type} = property;

	const selectText = type === PropertyTypes.AccountSelectText;

	const operatorName = selectText
		? criterion.operatorName ?? ''
		: getOperator(valueIMap, 0);
	const value = getPropertyValue(valueIMap, 'value', 0);

	const operatorKey = maybeFormatToKnownType(operatorName, value);

	const operatorLabel = getOperatorLabel(operatorKey, type);

	return (
		<>
			{entityName}

			<b>{label}</b>

			<span>{operatorLabel}</span>

			{!isOfKnownType(operatorKey) &&
				(selectText ? (
					<LifecycleStageDisplay label={label} value={value} />
				) : (
					<b>{maybeFormatValue(value, type, timeZoneId)}</b>
				))}
		</>
	);
};

export default AccountDisplay;
