import dateFns from 'date-fns';
import {
	ACCOUNT_PROPERTIES,
	INDIVIDUAL_PROPERTIES,
	ORGANIZATION_PROPERTIES,
	SESSION_PROPERTIES,
	WEB_BEHAVIORS,
} from '../utils/properties';
import {
	ACQUISITION_PARAMETER_PROPERTY_PREFIX,
	Conjunctions,
	CustomFunctionOperators,
	isKnown,
	isUnknown,
	MAX_NESTED_OR_CRITERIA,
	MAX_SEQUENTIAL_CRITERIA,
	NestedOrLimitState,
	NotOperators,
	PropertyTypes,
	SequentialLimitState,
	SUPPORTED_OPERATORS_MAP,
} from './constants';
import {Criteria, Criterion, CriterionGroup, Operator} from './types';
import {DEFAULT_UTM_PARAMETER_OPTIONS} from './properties/session-properties';
import {EntityType, ReferencedEntities} from '../context/referencedObjects';
import {getActionFromEventId} from './activity-keys';
import {Event} from 'event-analysis/utils/types';
import {every, isBoolean, isString, isUndefined} from 'lodash';
import {FieldContexts, FieldOwnerTypes} from 'shared/util/constants';
import {fromJS, Map} from 'immutable';
import {Property} from 'shared/util/records';
import {v4 as uuidv4} from 'uuid';

const GROUP_ID_NAMESPACE = 'group_';
const ROW_ID_NAMESPACE = 'row_';

export const createInterestProperty = (name: string): Property =>
	new Property({
		entityName: Liferay.Language.get('individual'),
		label: name,
		name,
		propertyKey: 'interest',
		type: PropertyTypes.Interest,
	});

export const createVocabularyProperty = ({
	id,
	name,
}: {
	id: string;
	name: string;
}): Property =>
	new Property({
		entityName: Liferay.Language.get('vocabularies-and-categories'),
		label: name,
		name: id,
		propertyKey: 'vocabulary',
		type: PropertyTypes.Vocabulary,
	});

export function createTagProperty({
	id,
	name,
}: {
	id: string;
	name: string;
}): Property {
	return new Property({
		entityName: Liferay.Language.get('tags'),
		label: name,
		name: id,
		propertyKey: 'tag',
		type: PropertyTypes.Tag,
	});
}

/**
 * Creates a new group object with items.
 */
export const createNewGroup = (
	items: Criteria[],
	conjunctionName: Conjunctions = Conjunctions.And
): CriterionGroup => ({
	conjunctionName,
	criteriaGroupId: generateGroupId(),
	items,
});

/**
 * Generates a unique group id.
 */
export const generateGroupId = (): string => `${GROUP_ID_NAMESPACE}${uuidv4()}`;

/**
 * Generates a unique row id.
 */
export const generateRowId = (): string => `${ROW_ID_NAMESPACE}${uuidv4()}`;

/**
 * Gets a list of group ids from a criteria object.
 * Used for disallowing groups to be moved into its own deeper nested groups.
 * Example of returned value: ['group_02', 'group_03']
 */
export const getChildGroupIds = (criteria: Criteria): string[] => {
	let childGroupIds: string[] = [];

	if (isCriterionGroup(criteria) && criteria.items.length) {
		childGroupIds = criteria.items.reduce(
			(groupIdList: string[], item) =>
				isCriterionGroup(item)
					? [
							...groupIdList,
							item.criteriaGroupId,
							...getChildGroupIds(item),
						]
					: groupIdList,
			[] as string[]
		);
	}

	return childGroupIds;
};

/**
 * Gets the property name from the propertyLabel string .
 */
export const getPropertyNameFromRaw = (propertyLabel: string = ''): string => {
	const properties = propertyLabel.split('/');

	return properties.length > 1 ? properties[1] : properties[0];
};

export const getPropertyContextFromRaw = (
	propertyLabel: string = ''
): string | null => {
	const properties = propertyLabel.split('/');

	return properties.length > 1 ? properties[0] : null;
};

const _getLimitState = (
	length: number,
	max: number
): 'exceedsLimit' | 'reachedLimit' | null => {
	if (length > max) {
		return 'exceedsLimit';
	}

	if (length === max) {
		return 'reachedLimit';
	}

	return null;
};

/**
 * Returns the current state of the nested OR limit for the given group, or
 * null when the limit does not apply. Callers must skip the root group; this
 * helper assumes the input is nested.
 */
export const getNestedOrLimitState = (
	criteria: CriterionGroup | null | undefined
): NestedOrLimitState | null => {
	if (!criteria || criteria.conjunctionName !== Conjunctions.Or) {
		return null;
	}

	return _getLimitState(criteria.items?.length ?? 0, MAX_NESTED_OR_CRITERIA);
};

/**
 * Returns the current state of the sequential criteria limit for the root
 * AND group, or null when the limit does not apply. Callers must check that
 * sequential mode is enabled and that the group is the root.
 */
export const getSequentialLimitState = (
	criteria: CriterionGroup | null | undefined
): SequentialLimitState | null => {
	if (!criteria || criteria.conjunctionName !== Conjunctions.And) {
		return null;
	}

	return _getLimitState(criteria.items?.length ?? 0, MAX_SEQUENTIAL_CRITERIA);
};

export const hasRootAndExceeded = (
	criteria: CriterionGroup | null | undefined
): boolean => getSequentialLimitState(criteria) === 'exceedsLimit';

export const hasNestedOrExceeded = (
	criteria: CriterionGroup | Criterion | null | undefined
): boolean =>
	!!criteria &&
	isCriterionGroup(criteria) &&
	criteria.items.some(
		(item) =>
			isCriterionGroup(item) &&
			(getNestedOrLimitState(item) === 'exceedsLimit' ||
				hasNestedOrExceeded(item))
	);

/**
 * Gets the list of operators for a supported type.
 * Used for displaying the operators available for each criteria row.
 */
export const getSupportedOperatorsFromType = (type: string = ''): Operator[] =>
	(SUPPORTED_OPERATORS_MAP as Record<string, Operator[]>)[
		type.toLowerCase()
	] || [];

/**
 * Checks if value is a CriterionGroup.
 */
export const isCriterionGroup = (
	value: CriterionGroup | Criterion
): value is CriterionGroup =>
	!!value && (value as CriterionGroup).items !== undefined;

/**
 * Checks if value is an ImmutableMap
 */
export const isMap = (
	value: Map<string, any> | object
): value is Map<string, any> => Map.isMap(value as Map<string, any>);

/**
 * Checks if value is either isKnown or isUnknown.
 */
export const isOfKnownType = (key: string): boolean =>
	[isKnown, isUnknown].includes(key);

/**
 * Converts an object of key value pairs to a form data object for passing
 * into a fetch body.
 */
export const objectToFormData = (
	dataObject: Record<string, string | Blob>
): FormData => {
	const formData = new FormData();

	Object.keys(dataObject).forEach((key) => {
		formData.set(key, dataObject[key]);
	});

	return formData;
};

/**
 * Parse an activityKey string into an object.
 */
export const parseActivityKey = (
	activityKey: string = ''
): {eventId: string; id: string; objectType: string} => {
	const [objectType, eventId, id] = activityKey.split('#');

	return {eventId, id, objectType};
};

/**
 * Returns a YYYY-MM-DD date
 * based on a JS Date object
 *
 * @export
 */
export const jsDatetoYYYYMMDD = (dateJsObject: Date): string => {
	const DATE_FORMAT = 'YYYY-MM-DD';
	return dateFns.format(dateJsObject, DATE_FORMAT);
};

/**
 * Finds the matching property based on its Criterion.
 */
export const findPropertyByCriterion = (
	criterion: Criterion,
	referencedPropertiesIMap: Map<string, Map<string, Property>>
): Property | undefined => {
	const {operatorName, propertyName, type, value} = criterion;

	if (
		[
			CustomFunctionOperators.ActivitiesFilterByCount,
			NotOperators.NotActivitiesFilterByCount,
		].includes(
			operatorName as unknown as CustomFunctionOperators | NotOperators
		)
	) {
		const items = (value as Map<string, any>).getIn([
			'criterionGroup',
			'items',
		]) as any;

		const eventIdItem = items?.find?.(
			(item: any) => item.get?.('propertyName') === 'eventId'
		);

		let eventId;

		if (eventIdItem) {
			const eventIds = eventIdItem.get('value');
			const eventIdArray = eventIds?.toJS?.() ?? eventIds;

			eventId = Array.isArray(eventIdArray)
				? eventIdArray[0]
				: eventIdArray;
		}
		else {
			const activityKeyValue =
				(value as Map<string, any>).getIn(
					['criterionGroup', 'items', 0, 'value'],
					''
				) ||
				(value as Map<string, any>).getIn(
					['criterionGroup', 'items', 0, 'items', 0, 'value'],
					''
				);

			eventId = parseActivityKey(activityKeyValue).eventId;
		}

		const action = getActionFromEventId(eventId || propertyName);

		return WEB_BEHAVIORS.find(
			(property: Property | undefined) => property?.name === action
		);
	}
	else if (
		[
			CustomFunctionOperators.EventsFilterByCount,
			NotOperators.NotEventsFilterByCount,
		].includes(
			operatorName as unknown as CustomFunctionOperators | NotOperators
		)
	) {
		const eventId = value.getIn(
			['criterionGroup', 'items', 0, 'value'],
			''
		);

		return referencedPropertiesIMap.getIn(['event', eventId]);
	}
	else if (
		[
			CustomFunctionOperators.AccountsFilter,
			NotOperators.NotAccountsFilter,
		].includes(
			operatorName as unknown as CustomFunctionOperators | NotOperators
		)
	) {
		if (getPropertyContextFromRaw(propertyName) !== FieldContexts.Custom) {
			return ACCOUNT_PROPERTIES.find(
				(property: Property | undefined) =>
					property?.name === propertyName
			);
		}

		return referencedPropertiesIMap.getIn(
			[
				'account',
				getPropertyContextFromRaw(propertyName) ?? '',
				getPropertyNameFromRaw(propertyName),
			],
			''
		);
	}
	else if (
		[
			NotOperators.NotOrganizationsFilter,
			CustomFunctionOperators.OrganizationsFilter,
		].includes(
			operatorName as unknown as CustomFunctionOperators & NotOperators
		)
	) {
		if (getPropertyContextFromRaw(propertyName) !== FieldContexts.Custom) {
			return ORGANIZATION_PROPERTIES.find(
				(property: Property | undefined) =>
					property?.name === propertyName
			);
		}

		return referencedPropertiesIMap.getIn(
			[
				'organization',
				getPropertyContextFromRaw(propertyName) ?? '',
				getPropertyNameFromRaw(propertyName),
			],
			''
		);
	}
	else if (
		[
			CustomFunctionOperators.SessionsFilter,
			NotOperators.NotSessionsFilter,
		].includes(
			operatorName as unknown as CustomFunctionOperators | NotOperators
		) ||
		type === PropertyTypes.SessionDateTime
	) {

		// A UTM Parameter criterion's inner field name varies with whichever
		// parameter the user picked — one of the backend's mapped standard
		// field names (e.g. "context/acquisitionSource") or, for a custom
		// one, "context/<name>" where name starts with "utm" — so it's
		// matched back to the single "UTM Parameter" sidebar entry instead
		// of by an exact property name. See AcquisitionParameterUtil in
		// osb-asah-common.

		const acquisitionParameterName = propertyName?.startsWith(
			ACQUISITION_PARAMETER_PROPERTY_PREFIX
		)
			? propertyName.slice(ACQUISITION_PARAMETER_PROPERTY_PREFIX.length)
			: '';

		const isUtmParameter =
			DEFAULT_UTM_PARAMETER_OPTIONS.some(
				(option) => option.fieldName === propertyName
			) || acquisitionParameterName.toLowerCase().startsWith('utm');

		if (isUtmParameter) {
			return SESSION_PROPERTIES.find(
				(property: Property | undefined) =>
					property?.type === PropertyTypes.SessionUtmParameter
			);
		}

		return SESSION_PROPERTIES.find(
			(property: Property | undefined) => property?.name === propertyName
		);
	}
	else if (
		[
			CustomFunctionOperators.VocabulariesFilter,
			NotOperators.NotVocabulariesFilter,
		].includes(
			operatorName as unknown as CustomFunctionOperators | NotOperators
		)
	) {
		return (
			(referencedPropertiesIMap.getIn(['vocabulary', propertyName]) as
				| Property
				| undefined) ??
			createVocabularyProperty({
				id: propertyName ?? '',
				name:
					((value as Map<string, any>)
						?.getIn(['criterionGroup', 'items'])
						?.find(
							(item: Map<string, any>) =>
								item?.get('propertyName') ===
								'vocabularies/name'
						)
						?.get('value') as string | undefined) ??
					propertyName ??
					'',
			})
		);
	}
	else if (
		[
			CustomFunctionOperators.TagsFilter,
			NotOperators.NotTagsFilter,
		].includes(
			operatorName as unknown as CustomFunctionOperators | NotOperators
		)
	) {
		return (
			(referencedPropertiesIMap.getIn(['tag', propertyName]) as
				| Property
				| undefined) ??
			createTagProperty({
				id: propertyName ?? '',
				name:
					((value as Map<string, any>)
						?.getIn(['criterionGroup', 'items'])
						?.find(
							(item: Map<string, any>) =>
								item?.get('propertyName') === 'tags/name'
						)
						?.get('value') as string | undefined) ??
					propertyName ??
					'',
			})
		);
	}
	else if (operatorName === CustomFunctionOperators.InterestsFilter) {
		return createInterestProperty(propertyName ?? '');
	}
	else if (INDIVIDUAL_PROPERTIES.find(({name}) => name === propertyName)) {
		return INDIVIDUAL_PROPERTIES.find(({name}) => name === propertyName);
	}
	else {
		return referencedPropertiesIMap.getIn(
			[
				'individual',
				getPropertyContextFromRaw(propertyName) ?? '',
				getPropertyNameFromRaw(propertyName),
			],
			''
		);
	}
};

export const convertFieldMappingToAccountProperty = (
	fieldMapping:
		| Map<string, any>
		| {
				context: string;
				displayName: string;
				id: string;
				name: string;
				ownerType: string;
				rawType: string;
				type: string;
		  }
): Property => {
	const displayName = isMap(fieldMapping)
		? fieldMapping.get('displayName')
		: fieldMapping.displayName;
	const id = isMap(fieldMapping) ? fieldMapping.get('id') : fieldMapping.id;
	const name = isMap(fieldMapping)
		? fieldMapping.get('name')
		: fieldMapping.name;
	const type = isMap(fieldMapping)
		? fieldMapping.get('rawType')
		: fieldMapping.rawType;

	return new Property({
		entityName: Liferay.Language.get('account'),
		id,
		label: displayName || name,
		name: id,
		propertyKey: FieldOwnerTypes.Account,
		type: `account-${type.toLowerCase()}` as PropertyTypes,
	});
};

export const convertFieldMappingToIndividualProperty = (
	fieldMapping:
		| Map<string, any>
		| {
				context: string;
				displayName: string;
				id: string;
				name: string;
				ownerType: string;
				rawType: string;
				type: string;
		  }
): Property => {
	const context = isMap(fieldMapping)
		? fieldMapping.get('context')
		: fieldMapping.context;
	const displayName = isMap(fieldMapping)
		? fieldMapping.get('displayName')
		: fieldMapping.displayName;
	const id = isMap(fieldMapping) ? fieldMapping.get('id') : fieldMapping.id;
	const name = isMap(fieldMapping)
		? fieldMapping.get('name')
		: fieldMapping.name;
	const type = isMap(fieldMapping)
		? fieldMapping.get('rawType')
		: fieldMapping.rawType;

	return new Property({
		entityName: Liferay.Language.get('individual'),
		id,
		label: displayName || name,
		name: context ? `${context}/${id}/value` : id,
		propertyKey: FieldOwnerTypes.Individual,
		type: type.toLowerCase(),
	});
};

export const convertFieldMappingToOrganizationProperty = (
	fieldMapping:
		| Map<string, any>
		| {
				context: string;
				displayName: string;
				id: string;
				name: string;
				ownerType: string;
				rawType: string;
				type: string;
		  }
): Property => {
	const context = isMap(fieldMapping)
		? fieldMapping.get('context')
		: fieldMapping.context;
	const displayName = isMap(fieldMapping)
		? fieldMapping.get('displayName')
		: fieldMapping.displayName;
	const id = isMap(fieldMapping) ? fieldMapping.get('id') : fieldMapping.id;
	const name = isMap(fieldMapping)
		? fieldMapping.get('name')
		: fieldMapping.name;
	const type = isMap(fieldMapping)
		? fieldMapping.get('rawType')
		: fieldMapping.rawType;

	return new Property({
		entityName: Liferay.Language.get('organization'),
		id,
		label: displayName || name,
		name: context ? `${context}/${id}/value` : id,
		propertyKey: FieldOwnerTypes.Organization,
		type: `organization-${type.toLowerCase()}` as PropertyTypes,
	});
};

export const convertEventToProperty = (
	eventDefinition: Map<string, any> | Event = Map()
): Map<string, Map<string, Property>> => {
	const displayName = isMap(eventDefinition)
		? eventDefinition.get('displayName')
		: eventDefinition.displayName;
	const name = isMap(eventDefinition)
		? eventDefinition.get('name')
		: eventDefinition.name;

	const hidden = isMap(eventDefinition)
		? eventDefinition.get('hidden')
		: eventDefinition.hidden;

	return new Property({
		entityName: Liferay.Language.get('event'),
		id: name,
		label: displayName || name,
		name,
		options: [{label: 'hidden', value: hidden}],
		propertyKey: 'event',
		type: PropertyTypes.Event,
	});
};

export const convertFieldMappingsToProperties = (
	fieldMappingsIMap: Map<
		string,
		Map<string, Map<string, Map<string, any>>>
	> = Map()
): Map<string, Map<string, Map<string, Property>>> =>
	fieldMappingsIMap.map((ownerTypeGroup, key) => {
		let conversionFn: ((fieldMappingIMap: any) => Property) | undefined;

		if (key === FieldOwnerTypes.Account) {
			conversionFn = convertFieldMappingToAccountProperty;
		}
		else if (key === FieldOwnerTypes.Individual) {
			conversionFn = convertFieldMappingToIndividualProperty;
		}
		else if (key === FieldOwnerTypes.Organization) {
			conversionFn = convertFieldMappingToOrganizationProperty;
		}

		if (conversionFn) {
			const fn = conversionFn;
			return ownerTypeGroup!.map((contextGroup) =>
				contextGroup!.reduce(
					(
						acc?: Map<string, Property>,
						fieldMappingIMap?: Map<string, any>,
						k?: string
					) => (acc ?? Map()).set(k ?? '', fn(fieldMappingIMap)),
					Map() as Map<string, Property>
				)
			);
		}
	}) as Map<string, Map<string, Map<string, Property>>>;

export const convertReferencedObjectsToProperties = (
	referencedObjectsIMap: Map<
		string,
		Map<string, Map<string, Map<string, any>>>
	> = Map()
): Map<string, Map<string, Map<string, Property> | Property>> => {
	const fieldMappingProperties = convertFieldMappingsToProperties(
		referencedObjectsIMap.get('fieldMappings')
	);

	const eventProperties = referencedObjectsIMap
		.get('event', Map())
		.merge(referencedObjectsIMap.get('custom-events'))
		.map(convertEventToProperty);

	return fieldMappingProperties.merge(fromJS({event: eventProperties}));
};

/**
 * Check to see if the value is a valid input value.
 * The input value cannot be an empty string or undefined.
 * @returns {boolean}
 */
export const isValid = (value: any): boolean =>
	!(isUndefined(value) || (isString(value) && !value.length));

/**
 * Recursively check through all criterions and invalidates those
 * that do not have a matching property
 */
export const invalidateCriterionWithMissingProperty = (
	criteria: Criteria,
	referencedPropertiesIMap: Map<string, Property>
): Criteria => {
	if (isCriterionGroup(criteria)) {
		const {items} = criteria;

		if (items.length) {
			return {
				...criteria,
				items: items.map((criterion) =>
					invalidateCriterionWithMissingProperty(
						criterion,
						referencedPropertiesIMap
					)
				),
			};
		}
	}
	else {
		if (findPropertyByCriterion(criteria, referencedPropertiesIMap)) {
			return criteria;
		}

		return {
			...criteria,
			valid: isBoolean(criteria.valid)
				? false
				: Object.keys(criteria.valid as object).reduce(
						(acc, key) => ({...acc, [key]: false}),
						{}
					),
		};
	}

	return criteria;
};

export const parseReferencedEntityId = (
	id: string,
	referencedEntities: ReferencedEntities,
	type: EntityType
) => {
	let parsedId: string | undefined = id;

	if (
		type === EntityType.Assets &&
		parsedId &&
		parsedId.indexOf('_') === -1
	) {
		const keys = Object.keys(
			referencedEntities.getIn([EntityType.Assets]).toObject()
		);

		parsedId = keys.find((key) => key.includes(id));
	}

	return parsedId;
};

/**
 * Recursively check through all criteria to see if they're valid.
 */
export const validateSegmentInputs = (criteria: Criteria): boolean => {
	if (isCriterionGroup(criteria)) {
		const {items} = criteria;

		if (items.length) {
			return items.map(validateSegmentInputs).every(Boolean);
		}
	}
	else if (criteria) {
		if (isBoolean(criteria.valid)) {
			return criteria.valid;
		}

		return every(criteria.valid, Boolean);
	}

	return false;
};
