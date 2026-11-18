import {Criterion} from './types';
import {isOfKnownType} from './utils';
import {Property} from 'shared/util/records';
import {PropertyTypes} from './constants';
import {
	getOperatorLabel,
	maybeFormatToKnownType,
	maybeFormatValue,
} from 'segment/components/criteria-card/utils';

/**
 * Types whose value is not a plain readable value (an entity id, or a nested
 * group of criteria), so the label stops at the operator.
 */
const TYPES_WITHOUT_VALUE_LABEL: string[] = [
	PropertyTypes.AccountSelectText,
	PropertyTypes.Event,
	PropertyTypes.OrganizationSelectText,
	PropertyTypes.SelectText,
];

const isPrimitive = (value: unknown): value is number | string =>
	typeof value === 'string' || typeof value === 'number';

/**
 * Builds the full condition of a criterion as a single plain sentence, such as
 * `Individual Country is "Brazil"`. This is the one accessible name of a rule,
 * shared by its row, the keyboard pickup, the drag preview and the keyboard
 * movement announcements, so two rows with the same attribute can be told
 * apart by their operator and value.
 */
export function getCriterionLabel(
	criterion: Criterion,
	property?: Property
): string {
	if (!property) {
		return criterion.propertyName ?? '';
	}

	const {operatorName, value} = criterion;
	const {entityName, label, type} = property;

	const operatorKey = maybeFormatToKnownType(operatorName ?? '', value);
	const operatorLabel = getOperatorLabel(operatorKey, type);

	const parts: Array<number | string> = [entityName, label, operatorLabel];

	if (
		!isOfKnownType(operatorKey) &&
		!TYPES_WITHOUT_VALUE_LABEL.includes(type) &&
		isPrimitive(value) &&
		value !== ''
	) {
		parts.push(maybeFormatValue(value, type));
	}

	return parts.filter((part) => part !== undefined && part !== '').join(' ');
}
