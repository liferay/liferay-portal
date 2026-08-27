import moment from 'moment';
import {formatDateToTimeZone, getCustomDateTimeFormat} from 'shared/util/date';
import {formatTime} from 'shared/util/time';
import {
	GEOLOCATION_OPTIONS,
	getCustomInputOperators,
	INPUT_DATE_FORMAT,
	isKnown,
	isUnknown,
	PropertyTypes,
	RelationalOperators,
	SUPPORTED_OPERATORS_MAP,
} from 'segment/segment-editor/dynamic/utils/constants';

export function getOperatorLabel(
	operatorKey: string,
	type: PropertyTypes
): string {
	type OperatorsMap = typeof SUPPORTED_OPERATORS_MAP;
	let supportedOperators:
		| OperatorsMap[keyof OperatorsMap]
		| typeof GEOLOCATION_OPTIONS;

	switch (type) {
		case PropertyTypes.AccountDate:
		case PropertyTypes.AccountNumber:
		case PropertyTypes.AccountText:
			supportedOperators =
				SUPPORTED_OPERATORS_MAP[
					type.replace('account-', '') as keyof OperatorsMap
				];
			break;
		case PropertyTypes.OrganizationBoolean:
		case PropertyTypes.OrganizationDate:
		case PropertyTypes.OrganizationDateTime:
		case PropertyTypes.OrganizationNumber:
		case PropertyTypes.OrganizationText:
			supportedOperators =
				SUPPORTED_OPERATORS_MAP[
					type.replace('organization-', '') as keyof OperatorsMap
				];
			break;
		case PropertyTypes.SessionDateTime:
		case PropertyTypes.SessionNumber:
		case PropertyTypes.SessionText:
			supportedOperators =
				SUPPORTED_OPERATORS_MAP[
					type.replace('session-', '') as keyof OperatorsMap
				];
			break;
		case PropertyTypes.SessionGeolocation:
			supportedOperators = GEOLOCATION_OPTIONS;
			break;

		// Channel and UTM Parameter compare their value with the operators
		// their input offers rather than with the ones their own type maps
		// to, which describe the enclosing "sessions.filter" call instead.

		case PropertyTypes.SessionChannel:
		case PropertyTypes.SessionUtmParameter:
			supportedOperators = getCustomInputOperators(type);
			break;
		case PropertyTypes.AccountSelectText:
		case PropertyTypes.Behavior:
		case PropertyTypes.Boolean:
		case PropertyTypes.Date:
		case PropertyTypes.DateTime:
		case PropertyTypes.Duration:
		case PropertyTypes.Number:
		case PropertyTypes.OrganizationSelectText:
		case PropertyTypes.Text:
		default:
			supportedOperators =
				SUPPORTED_OPERATORS_MAP[type as keyof OperatorsMap];
	}

	const operator = (
		supportedOperators as Array<{
			key?: string;
			label: string;
			value?: string;
		}>
	).find(({key, value}) => (key || value) === operatorKey);

	return operator ? operator.label : '';
}

export function maybeFormatToKnownType(
	operatorName: string,
	value: any
): string {
	const valueNull = value === null;

	if (operatorName === RelationalOperators.EQ && valueNull) {
		return isUnknown;
	}
	else if (operatorName === RelationalOperators.NE && valueNull) {
		return isKnown;
	}

	return operatorName;
}

export function maybeFormatValue(
	value: any,
	type: string,
	timeZoneId?: string
): string | number {
	switch (type) {
		case PropertyTypes.AccountText:
		case PropertyTypes.Behavior:
		case PropertyTypes.Interest:
		case PropertyTypes.SessionChannel:
		case PropertyTypes.SessionGeolocation:
		case PropertyTypes.SessionText:
		case PropertyTypes.SessionUtmParameter:
		case PropertyTypes.Text:
			return `"${value}"`;
		case PropertyTypes.Boolean:
			return value.toUpperCase();
		case PropertyTypes.Date:
			return moment(value).format(INPUT_DATE_FORMAT);
		case PropertyTypes.DateTime:
		case PropertyTypes.SessionDateTime:
			return formatDateToTimeZone(
				value,
				getCustomDateTimeFormat(),
				timeZoneId
			);
		case PropertyTypes.Duration:
			return formatTime(value);
		case PropertyTypes.AccountDate:
		case PropertyTypes.AccountNumber:
		case PropertyTypes.Number:
		case PropertyTypes.SessionNumber:
		default:
			return value;
	}
}
