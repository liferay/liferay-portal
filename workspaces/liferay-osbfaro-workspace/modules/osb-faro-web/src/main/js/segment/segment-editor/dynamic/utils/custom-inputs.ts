import {ACTIVITY_KEY, Conjunctions, TIME_PERIOD_OPTIONS} from './constants';
import {createNewGroup} from './utils';
import {CustomValue} from 'shared/util/records';
import {fromJS, List, Map} from 'immutable';
import {isArray} from 'lodash';

/**
 * Create the valueIMap for a custom input.
 * @param {Array} params - Array of root keys for the valueIMap.
 * @returns {CustomValue} The params array converted into a deeply immutable valueIMap.
 */
export const createCustomValueMap = (
	params: {key: string; value: any}[]
): CustomValue => {
	let valueIMap = Map();

	params.forEach(({key, value}) => {
		if (isArray(value)) {
			valueIMap = valueIMap.set(key, fromJS(createNewGroup(value)));
		}
		else {
			valueIMap = valueIMap.set(key, value);
		}
	});

	return new CustomValue(valueIMap);
};

/**
 * Get the filter Criterion Map at index.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @param {number} index - The index of the Criterion Map.
 * @returns {Map} The Criterion Map.
 */
export const getFilterCriterionIMap = (valueIMap: CustomValue, index: number) =>
	valueIMap.getIn(['criterionGroup', 'items', index]);

/**
 * Get the index where the first matching property name is found in the criteria list.
 * This is useful if we don't know where the criterion is at in the list, but we know the property name.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @returns {number} The index of the matching criterion or -1 if not found.
 */
export const getIndexFromPropertyName = (
	valueIMap: CustomValue,
	propertyName: string
): number => {
	const items = valueIMap.getIn(['criterionGroup', 'items']);

	if (!items) return -1;

	return items.findIndex(
		(entry: Map<string, any>) => entry.get('propertyName') === propertyName
	);
};

export const getIndexFromPropertyNamePrefix = (
	valueIMap: CustomValue,
	prefix: string
): number => {
	const items = valueIMap.getIn(['criterionGroup', 'items']);

	if (!items) return -1;

	return items.findIndex((entry: Map<string, any>) =>
		entry.get('propertyName')?.startsWith(prefix)
	);
};

export const getFilterCriterionIMapByPropertyNamePrefix = (
	valueIMap: CustomValue,
	prefix: string
): any => {
	const index = getIndexFromPropertyNamePrefix(valueIMap, prefix);

	return index < 0 ? undefined : getFilterCriterionIMap(valueIMap, index);
};

export const hasAttributeFilterCriterion = (
	valueIMap: CustomValue,
	prefix: string
): boolean => {
	const attributeCriterion = getFilterCriterionIMapByPropertyNamePrefix(
		valueIMap,
		prefix
	);

	return (
		!!attributeCriterion &&
		attributeCriterion.get('propertyName') !== prefix
	);
};

/**
 * Reads every activityKey out of a behavior criterion value. A single selection
 * is stored as a flat activityKey item; N selections are stored as an "or" group
 * of activityKey items. Returns the list of keys for either shape.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @returns {string[]} The activityKey values.
 */
export const getActivityKeysFromValue = (valueIMap: CustomValue): string[] => {
	const items = valueIMap.getIn(['criterionGroup', 'items']) as any;

	if (!items) return [];

	const flatItem = items.find(
		(item: any) => item.get?.('propertyName') === ACTIVITY_KEY
	);

	if (flatItem) {
		const activityKey = flatItem.get('value');

		return activityKey ? [activityKey] : [];
	}

	const orGroup = items.find(
		(item: any) => item.get?.('conjunctionName') === Conjunctions.Or
	);

	if (orGroup) {
		return ((orGroup.get('items') as List<any>) ?? List())
			.filter((item: any) => item.get?.('propertyName') === ACTIVITY_KEY)
			.map((item: any) => item.get('value'))
			.toArray();
	}

	return [];
};

/**
 * The criterion item with the given propertyName (e.g. the applicationId,
 * eventId, or day item). Undefined when the item is absent.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @param {string} propertyName - The item's propertyName to find.
 * @returns {Map|undefined} The criterion Map.
 */
export const getFilterCriterionIMapByPropertyName = (
	valueIMap: CustomValue,
	propertyName: string
): any => {
	const index = getIndexFromPropertyName(valueIMap, propertyName);

	return index < 0 ? undefined : getFilterCriterionIMap(valueIMap, index);
};

/**
 * The value of the criterion item with the given propertyName (e.g. the
 * applicationId or eventId of a behavior criterion). Undefined when the item is
 * absent.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @param {string} propertyName - The item's propertyName to read.
 * @returns {*} The item's value.
 */
export const getFilterValueByPropertyName = (
	valueIMap: CustomValue,
	propertyName: string
): any =>
	getFilterCriterionIMapByPropertyName(valueIMap, propertyName)?.get('value');

/**
 * Get the operator name from the criterion at the specified index in the valueIMap.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @returns {string} The operator name of the criterion at the specified index.
 */
export const getOperator = (valueIMap: CustomValue, index: number): string =>
	getPropertyValue(valueIMap, 'operatorName', index);

/**
 * Get the time period value from the valueIMap.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @returns {string} The time period value from the criterion with a 'completeDate' property name.
 */
export const getCompleteDate = (valueIMap: CustomValue): string => {
	const index = getIndexFromPropertyName(valueIMap, 'completeDate');

	return getPropertyValue(valueIMap, 'value', index);
};

/**
 * Get the value of the propertyName at the specified index.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @param {string} propertyName - The propertyName string in the criterion to update.
 * @param {number} index - The index of the criterion in the items list.
 * @returns {*} The value of the criterion propertyName at the specified index in the items list.
 */
export const getPropertyValue = (
	valueIMap: CustomValue,
	propertyName: string,
	index: number
): any => valueIMap.getIn(['criterionGroup', 'items', index, propertyName]);

/**
 * Get the time period label from the time period value.
 * @param {string} timePeriod - The time period value.
 * @returns {string} The time period label that matches the timePeriodValue.
 */
export const getTimePeriodLabel = (value: string): string => {
	const timePeriod = TIME_PERIOD_OPTIONS.find(
		(timePeriod) => timePeriod.value === value
	);

	return timePeriod ? timePeriod.label : '';
};

/**
 * Remove entries in valueIMap by their index in the criteria list.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @param {Array} indexArray - Array of indexes to remove from the criteria list in valueIMap.
 * @returns {CustomValue} The valueIMap with the items at the indexes in indexArray removed.
 */
export const removeItemsByIndex = (
	valueIMap: CustomValue,
	indexArray: number[]
): CustomValue =>
	valueIMap.updateIn(['criterionGroup', 'items'], (iList: any) =>
		iList.filterNot((_: unknown, i: number) => indexArray.includes(i))
	) as CustomValue;

/**
 * Set the operator name from the criterion at the specified index in the valueIMap.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @param {number} index - The index of the criterion in the items list.
 * @param {*} value - The value to update in the valueIMap.
 * @returns {CustomValue} The updated valueIMap.
 */
export const setOperator = (
	valueIMap: CustomValue,
	index: number,
	value: any
): CustomValue => setPropertyValue(valueIMap, 'operatorName', index, value);

/**
 * Set the time period value in the valueIMap.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @param {string} value - The value to update for the time period.
 * @returns {CustomValue} The updated valueIMap.
 */
export const setCompleteDate = (
	valueIMap: CustomValue,
	completeDate: string
): CustomValue => {
	const index = getIndexFromPropertyName(valueIMap, 'completeDate');

	return setPropertyValue(valueIMap, 'value', index, completeDate);
};

/**
 * Set the value of the propertyName at the specified index.
 * @param {CustomValue} valueIMap - The Immutable Map representing the custom input value.
 * @param {string} propertyName - The propertyName string in the criterion to update.
 * @param {number} index - The index of the criterion in the items list.
 * @param {*} value - The value to update in the valueIMap.
 * @returns {CustomValue} The updated valueIMap.
 */
export const setPropertyValue = (
	valueIMap: CustomValue,
	propertyName: string,
	index: number,
	value: any
): CustomValue =>
	valueIMap.setIn(
		['criterionGroup', 'items', index, propertyName],
		value
	) as CustomValue;
