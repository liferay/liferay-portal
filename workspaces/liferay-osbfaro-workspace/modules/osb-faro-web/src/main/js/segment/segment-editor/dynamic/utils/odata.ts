import {
	Conjunctions,
	CUSTOM_FUNCTION_OPERATOR_KEY_MAP,
	CustomFunctionOperators,
	FunctionalOperators,
	GROUP,
	NotOperators,
	PropertyTypes,
	RelationalOperators,
	SUPPORTED_PROPERTY_TYPES_MAP,
} from './constants';
import {
	Context,
	Criteria,
	Criterion,
	CriterionGroup,
	ODataASTNode,
} from './types';
import {CustomValue} from 'shared/util/records';
import {fromJS, Map} from 'immutable';
import {generateGroupId, generateRowId, isCriterionGroup} from './utils';
import {get, invert, isFinite, isNull, isString, isUndefined} from 'lodash';
import {getPropertyValue, setPropertyValue} from './custom-inputs';
import {
	getRemoteCriterionTypeByOperator,
	REMOTE_CRITERION_TYPES,
} from '../criterion-types/registry';
import {getSafeDecodedURIComponent} from 'shared/util/util';
import {filter as oDataFilterFn} from 'odata-v4-parser';
import {RemoteCriterionType} from '../criterion-types/RemoteCriterionType';

const OPERATORS = {
	...CustomFunctionOperators,
	...FunctionalOperators,
	...RelationalOperators,
};

const EXPRESSION_TYPES = {
	AND: 'AndExpression',
	BOOL_PAREN: 'BoolParenExpression',
	COMMON: 'CommonExpression',
	EQUALS: 'EqualsExpression',
	FIRST_MEMBER: 'FirstMemberExpression',
	FUNCTION: 'FunctionExpression',
	GREATER_OR_EQUALS: 'GreaterOrEqualsExpression',
	GREATER_THAN: 'GreaterThanExpression',
	LESSER_OR_EQUALS: 'LesserOrEqualsExpression',
	LESSER_THAN: 'LesserThanExpression',
	MEMBER: 'MemberExpression',
	METHOD_CALL: 'MethodCallExpression',
	NOT: 'NotExpression',
	NOT_EQUALS: 'NotEqualsExpression',
	OR: 'OrExpression',
	PAREN: 'ParenExpression',
	PROPERTY_PATH: 'PropertyPathExpression',
};

const EDM_NUMBERS = [
	'Edm.Decimal',
	'Edm.SByte',
	'Edm.Int16',
	'Edm.Int32',
	'Edm.Int64',
];

const EDM_NULL = 'null';

const EDM_STRING = 'Edm.String';

/**
 * Maps Odata-v4-parser generated AST expression names to internally used
 * constants.
 */
const oDataV4ParserNameMap = {
	[EXPRESSION_TYPES.AND]: Conjunctions.And,
	between: OPERATORS.Between,
	[EXPRESSION_TYPES.BOOL_PAREN]: GROUP,
	contains: OPERATORS.Contains,
	[EXPRESSION_TYPES.EQUALS]: OPERATORS.EQ,
	[EXPRESSION_TYPES.GREATER_OR_EQUALS]: OPERATORS.GE,
	[EXPRESSION_TYPES.GREATER_THAN]: OPERATORS.GT,
	[EXPRESSION_TYPES.LESSER_OR_EQUALS]: OPERATORS.LE,
	[EXPRESSION_TYPES.LESSER_THAN]: OPERATORS.LT,
	[EXPRESSION_TYPES.NOT_EQUALS]: OPERATORS.NE,
	[EXPRESSION_TYPES.OR]: Conjunctions.Or,
};

/**
 * Constants for characters we will encode ourselves.
 */
const FARO_SPECIAL_CHARS = {
	ampersand: {
		encoded: '_FARO_AMPERSAND_',
		raw: '&',
	},
	at: {
		encoded: '_FARO_AT_',
		raw: '@',
	},
	backtick: {
		encoded: '_FARO_BACKTICK_',
		raw: '`',
	},
	braceLeft: {
		encoded: '_FARO_LEFT_BRACE_',
		raw: '{',
	},
	braceRight: {
		encoded: '_FARO_RIGHT_BRACE_',
		raw: '}',
	},
	bracketLeft: {
		encoded: '_FARO_LEFT_BRACKET_',
		raw: '[',
	},
	bracketRight: {
		encoded: '_FARO_RIGHT_BRACKET_',
		raw: ']',
	},
	caret: {
		encoded: '_FARO_CARET_',
		raw: '^',
	},
	dash: {
		encoded: '_FARO_DASH_',
		raw: '-',
	},
	dollar: {
		encoded: '_FARO_DOLLAR_',
		raw: '$',
	},
	greaterThan: {
		encoded: '_FARO_GREATER_THAN_',
		raw: '>',
	},
	hash: {
		encoded: '_FARO_HASH_',
		raw: '#',
	},
	lessThan: {
		encoded: '_FARO_LESS_THAN_',
		raw: '<',
	},
	percent: {
		encoded: '_FARO_PERCENT_',
		raw: '%',
	},
	pipe: {
		encoded: '_FARO_PIPE_',
		raw: '|',
	},
	plus: {
		encoded: '_FARO_PLUS_',
		raw: '+',
	},
	question: {
		encoded: '_FARO_QUESTION_',
		raw: '?',
	},
	slash: {
		encoded: '_FARO_SLASH_',
		raw: '/',
	},
	underscore: {
		encoded: '_FARO_UNDERSCORE_',
		raw: '_',
	},
};

/**
 * Wraps a node in a grouping node.
 * @returns Object representing the grouping
 */
const addNewGroup = ({oDataASTNode, prevConjunction}: Context): Context => ({
	lastNodeWasGroup: false,
	oDataASTNode: {
		type: EXPRESSION_TYPES.BOOL_PAREN,
		value: oDataASTNode,
	},
	prevConjunction,
});

const PARAM_REGEX = /\s+((?:criterionGroup|operator|value)=)/g;

/**
 * Trim the spaces before params.
 * This function is necessary because the odata-v4-parser library can not
 * handle spaces between parameters inside of custom functions.
 */
export const trimSpacesBeforeParams = (queryString: string): string =>
	queryString.replace(PARAM_REGEX, '$1');

const buildRemoteFilterString = (
	criterionGroup: any,
	criterionType: RemoteCriterionType
): string => {
	const items: any[] = criterionGroup?.items ?? [];

	const find = (name: string) =>
		items.find((item: any) => item.propertyName === name);

	const idItem = find(criterionType.idProperty);
	const nameItem = find(criterionType.nameProperty);
	const activityKeyItem = find('activityKey');
	const appIdItem = find('applicationId');
	const eventIdItem = find('eventId');
	const dayItem = find('day');
	const categoriesItem = criterionType.supportsCategories
		? find('categories')
		: undefined;

	const parts: string[] = [];

	if (idItem && nameItem) {
		parts.push(`${criterionType.idProperty} eq '${idItem.value}'`);
		parts.push(`${criterionType.nameProperty} eq '${nameItem.value}'`);
	}

	if (appIdItem && eventIdItem) {
		const appIds = (appIdItem.value as string[])
			.map((id: string) => `'${id}'`)
			.join(',');
		const eventIds = (eventIdItem.value as string[])
			.map((id: string) => `'${id}'`)
			.join(',');
		parts.push(
			`(applicationId in (${appIds}) and eventId in (${eventIds}))`
		);
	}
	else if (activityKeyItem) {
		parts.push(`activityKey eq '${activityKeyItem.value}'`);
	}

	if (categoriesItem?.value?.length > 0) {
		const catParts = (
			categoriesItem.value as Array<{id: string; name: string}>
		).map(
			(cat) =>
				`(categories/id eq '${cat.id}' and categories/name eq '${cat.name}')`
		);
		parts.push(`(${catParts.join(' or ')})`);
	}

	if (dayItem) {
		parts.push(
			`${dayItem.propertyName} ${dayItem.operatorName} '${dayItem.value}'`
		);
	}

	const result = parts.join(' and ');

	return result ? `(${result})` : result;
};

/**
 * Recursively traverses the criteria object to build an oData filter query
 * string. Properties is required to parse the correctly with or without quotes
 * and formatting the query differently for certain types like collection.
 * @returns An OData query string built from the criteria object.
 */
const buildQueryString = (
	criteria: Criteria[],
	queryConjunction?: string
): string =>
	criteria
		.filter(Boolean)
		.reduce((queryString: string, criterion: Criteria, index: number) => {
			if (index > 0) {
				queryString = queryString.concat(` ${queryConjunction} `);
			}

			if (isCriterionGroup(criterion)) {
				const {conjunctionName, items} = criterion as CriterionGroup;

				const val = buildQueryString(items, conjunctionName);

				if (val) {
					queryString = queryString.concat(`(${val})`);
				}
			}
			else {
				const {operatorName, propertyName, type, value} =
					criterion as Criterion;

				const parsedValue = isString(value)
					? `'${decodeQuotesToOdataQuotes(encodeQuotes(value))}'`
					: value;

				if (isValueType(RelationalOperators, operatorName)) {
					if (operatorName === RelationalOperators.In) {
						const ids = (value as string[])
							.map((id: string) => `'${id}'`)
							.join(',');
						queryString = queryString.concat(
							`${propertyName} in (${ids})`
						);
					}
					else {
						queryString = queryString.concat(
							`${propertyName} ${operatorName} ${parsedValue}`
						);
					}
				}
				else if (isValueType(CustomFunctionOperators, operatorName)) {
					const remoteCriterionType =
						getRemoteCriterionTypeByOperator(operatorName);

					if (remoteCriterionType) {
						const criterionGroup = value
							.get('criterionGroup')
							?.toJS();
						const filterString = buildRemoteFilterString(
							criterionGroup,
							remoteCriterionType
						);
						const occurrenceOperator = value.get('operator');
						const occurrenceCount = value.get('value');

						const params: string[] = [
							`filter='${encodeQuotes(filterString)}'`,
						];

						if (!isNull(occurrenceOperator)) {
							params.push(`operator='${occurrenceOperator}'`);
						}

						if (!isNull(occurrenceCount)) {
							params.push(`value=${occurrenceCount}`);
						}

						queryString = queryString.concat(
							`activities.filterByCount(${decodeQuotesToOdataQuotes(
								params.join(',')
							)})`
						);
					}
					else {
						const fnName = getFunctionNameFromOperatorName(
							operatorName ?? ''
						);

						const paramKeys = value.keySeq().toJS();

						const paramsString = paramKeys
							.map((key: string) => {
								if (
									(key === 'value' || key === 'operator') &&
									isNull(value.get(key))
								) {
									return;
								}
								else if (key === 'criterionGroup') {
									return `filter='${encodeQuotes(
										buildQueryString([
											value.get(key).toJS(),
										])
									)}'`;
								}
								else if (
									key === 'value' &&
									!isString(value.get(key))
								) {
									return `${key}=${value.get(key)}`;
								}

								return `${key}='${value.get(key)}'`;
							})
							.filter(
								(val: string | undefined) => !isUndefined(val)
							)
							.join();

						queryString = queryString.concat(
							`${fnName}(${decodeQuotesToOdataQuotes(
								paramsString
							)})`
						);
					}
				}
				else if (isValueType(FunctionalOperators, operatorName)) {
					if (operatorName === FunctionalOperators.Between) {
						const {end, start} = parsedValue;

						queryString = queryString.concat(
							`between(${propertyName},'${start}','${end}')`
						);
					}
					else {
						queryString = queryString.concat(
							`${operatorName}(${propertyName}, ${parsedValue})`
						);
					}
				}
				else if (isValueType(NotOperators, operatorName)) {
					const baseOperator = (
						(operatorName ?? '') as string
					).replace(/not-/g, '') as Conjunctions &
						CustomFunctionOperators &
						FunctionalOperators &
						RelationalOperators &
						'GROUP';

					const baseExpression: Criterion[] = [
						{
							operatorName: baseOperator,
							propertyName,
							type,
							value,
						},
					];

					queryString = queryString.concat(
						`(not ${buildQueryString(baseExpression)})`
					);
				}
			}

			return queryString;
		}, '');

/**
 * Converts custom encodings back to original characters.
 */
const decodeSpecialCharacters = (queryString: string): string => {
	const specialCharactersArr = Object.values(FARO_SPECIAL_CHARS);

	const specialCharsEncoded = specialCharactersArr
		.map(({encoded}) => encoded)
		.join('|');

	const pattern = new RegExp(specialCharsEncoded, 'g');

	return queryString.replace(pattern, (match) => {
		const specialCharacter = specialCharactersArr.find(
			({encoded}) => encoded === match
		);

		if (specialCharacter) {
			return specialCharacter.raw;
		}

		return match;
	});
};

const encodeQuotes = (text: string): string => text.replace(/'/g, '%27');

/**
 * Encode certain special characters with our own encoding.
 */
const encodeSpecialCharacters = (queryString: string): string => {
	const charsNeedEscaped = ['+', '?', '$', '[', ']', '^', '{', '}', '|'];
	const specialCharactersArr = Object.values(FARO_SPECIAL_CHARS);

	const specialCharsPattern = specialCharactersArr
		.map(({raw}) => (charsNeedEscaped.includes(raw) ? `\\${raw}` : raw))
		.join('|');

	const pattern = new RegExp(specialCharsPattern, 'g');

	return queryString.replace(pattern, (match) => {
		const specialCharacter = specialCharactersArr.find(
			({raw}) => raw === match
		);

		if (specialCharacter) {
			return specialCharacter.encoded;
		}

		return match;
	});
};

/**
 * Escape single quotes in a string for general purposes.
 */
export const escapeSingleQuotes = (text: string) => text.replace(/'/g, "''");

/**
 * Escape all %27 encoded quotes.
 */
const decodeQuotesToOdataQuotes = (encodedText: string): string =>
	encodedText.replace(/%27/g, "''");

/**
 * Encode all %22 decoded quotes.
 */
const encodeDoubleQuotesToOdataQuotes = (decodedText: string): string =>
	decodedText.replaceAll('"', '%22');

/**
 * Gets the internal name of a child expression from the oDataV4Parser name
 */
const getChildExpressionName = (oDataASTNode: ODataASTNode): string =>
	getExpressionName(oDataASTNode.value);

/**
 * Gets the conjunction of the group or returns AND as a default.
 * @returns The conjunction name for a group or, if not available, AND.
 */
const getConjunctionForGroup = (oDataASTNode: ODataASTNode): string => {
	const childExpressionName = getChildExpressionName(oDataASTNode);

	return isValueType(Conjunctions, childExpressionName)
		? childExpressionName
		: Conjunctions.And;
};

/**
 * Gets the operatorName from the function name & namespace.
 */
const getOperatorNameFromFunctionName = (
	name: string,
	namespace: string
): CustomFunctionOperators =>
	CUSTOM_FUNCTION_OPERATOR_KEY_MAP[
		`${namespace}.${name}` as keyof typeof CUSTOM_FUNCTION_OPERATOR_KEY_MAP
	];

/**
 * Gets the function name & namespace from the operatorName.
 */
const getFunctionNameFromOperatorName = (operatorName: string): string =>
	invert(CUSTOM_FUNCTION_OPERATOR_KEY_MAP)[operatorName];

/**
 * Gets the internal name of an expression from the oDataV4Parser name.
 */
const getExpressionName = (oDataASTNode: ODataASTNode): string => {
	const {type} = oDataASTNode;

	let returnValue = oDataV4ParserNameMap[type];

	if (type == EXPRESSION_TYPES.METHOD_CALL) {
		returnValue = oDataASTNode.value.method;
	}
	else if (type === EXPRESSION_TYPES.FUNCTION) {
		const {name, namespace} = oDataASTNode.value.fn.value;

		returnValue = getOperatorNameFromFunctionName(name, namespace);
	}

	return returnValue;
};

const getFunctionName = (oDataASTNode: ODataASTNode): string =>
	oDataV4ParserNameMap[oDataASTNode.value.method];

/**
 * Returns the next expression in the syntax tree that is not a grouping.
 */
const getNextNonGroupExpression = (
	oDataASTNode: ODataASTNode
): ODataASTNode => {
	let returnValue;

	if (oDataASTNode.value.type === EXPRESSION_TYPES.BOOL_PAREN) {
		returnValue = getNextNonGroupExpression(oDataASTNode.value);
	}
	else {
		returnValue = oDataASTNode.value.left
			? oDataASTNode.value.left
			: oDataASTNode.value;
	}

	return returnValue;
};

/**
 * Returns the next expression in the syntax tree that is not a grouping.
 * Also ignoring Common, Paren, Member, and FirstMember expressions for property
 * path expression types like `cookies/any(c:c eq 'key=value')` since the
 * expressions' value are the same for a collection query.
 */
const getNextOperatorExpression = (
	oDataASTNode: ODataASTNode
): ODataASTNode => {
	let returnValue;

	const nextNode = oDataASTNode.value.left
		? oDataASTNode.value.left
		: oDataASTNode.value;

	const type = nextNode.type;

	if (
		type === EXPRESSION_TYPES.BOOL_PAREN ||
		type === EXPRESSION_TYPES.AND ||
		type === EXPRESSION_TYPES.OR ||
		type === EXPRESSION_TYPES.COMMON ||
		type === EXPRESSION_TYPES.FIRST_MEMBER ||
		type === EXPRESSION_TYPES.MEMBER ||
		type === EXPRESSION_TYPES.PAREN
	) {
		returnValue = getNextOperatorExpression(nextNode);
	}
	else {
		returnValue = nextNode;
	}

	return returnValue;
};

/**
 * Checks if a grouping has different conjunctions (e.g. (x AND y OR z)).
 */
const hasDifferentConjunctions = ({
	lastNodeWasGroup,
	oDataASTNode,
	prevConjunction,
}: Context): boolean =>
	prevConjunction !== oDataASTNode.type && !lastNodeWasGroup;

/**
/**
 * Checks if the value is a certain type.
 * @param {object} types - A map of supported types.
 * @param {*} value - The value to validate.
 */
const isValueType = (types: object, value: string | undefined): boolean =>
	value !== undefined && Object.values(types).includes(value);

/**
 * Checks if the group is needed; It is unnecessary when there are multiple
 * groupings in a row, when the conjunction directly outside the group is the
 * same as the one inside or there is no conjunction within a grouping.
 */
const isRedundantGroup = ({
	lastNodeWasGroup,
	oDataASTNode,
	prevConjunction,
}: Context): boolean => {
	const nextNodeExpressionName = getExpressionName(
		getNextNonGroupExpression(oDataASTNode)
	);

	return (
		lastNodeWasGroup ||
		oDataV4ParserNameMap[prevConjunction ?? ''] ===
			nextNodeExpressionName ||
		!isValueType(Conjunctions, nextNodeExpressionName)
	);
};

/**
 * Removes all extra quotes and leaves escaped quotes
 */
const parseNestedOdataString = (text: string): string => {
	const escapedText = text.replace(/''/g, '%27');

	return removeQuotes(escapedText).replace(/%27/g, "'");
};

/**
 * Removes surrounding quotes from a string.
 */
const removeSurroundingQuotes = (text: string) =>
	text.replace(/^['"](.*)['"]$/g, '$1');

/**
 * Removes both single `'` and double `"` quotes from a string.
 */
const removeQuotes = (text: string): string => text.replace(/['"]+/g, '');

/**
 * Removes a grouping node and returns the child node.
 */
const skipGroup = ({oDataASTNode, prevConjunction}: Context): Context => ({
	lastNodeWasGroup: true,
	oDataASTNode: oDataASTNode.value,
	prevConjunction,
});

/**
 * Replaces the "between" method with "substring" because the
 * oDataV4Parser can't handle between.
 */
export const convertBetweenToSubstring = (queryString: string): string =>
	queryString.replace(
		/between(?=\([\w-:]+,('[\w-:]+',?){2}\))/g,
		'substring'
	);

export const decodeValueFromCriteria = (criteria: Criteria) => {
	const decodeValue = (value: string) => {
		let decodedValue = value;

		try {
			decodedValue = getSafeDecodedURIComponent(value);
		}
		catch (e) {}

		return decodedValue;
	};

	const formatCriteria = (criteria: any) => {
		const newCriteria = {...criteria};

		if (newCriteria.value) {
			if (typeof newCriteria.value === 'string') {
				newCriteria.value = decodeValue(newCriteria.value);
			}
			else if (newCriteria.value?._map) {
				const firstItemValue = getPropertyValue(
					newCriteria.value,
					'value',
					0
				);

				// Only the first item's value is a decodable entity id (an
				// activityKey, vocabulary, or tag id). A vocabulary/tag
				// criterion leads with an applicationId array, whose value must
				// not be run through URI decoding.

				if (typeof firstItemValue === 'string') {
					newCriteria.value = setPropertyValue(
						newCriteria.value,
						'value',
						0,
						decodeValue(firstItemValue)
					);
				}
			}
		}

		if (newCriteria.items) {
			newCriteria.items = newCriteria.items.map(formatCriteria);
		}

		if (newCriteria.propertyName) {
			newCriteria.propertyName = decodeValue(newCriteria.propertyName);
		}

		return newCriteria;
	};

	return formatCriteria(criteria);
};

const escapeRegExp = (value: string): string =>
	value.replace(/[.*+?^${}()|[\]\\/]/g, '\\$&');

/**
 * Parses an already-extracted inner filter string (the content of the
 * `filter='...'` parameter, with OData `''` escaping already resolved) into a
 * flat list of criterion items plus the matched RemoteCriterionType and entity
 * id.  Returns null when the filter does not match any registered remote
 * criterion type (e.g. vocabulary, tag).
 */
const buildInnerFilterItems = (
	innerFilter: string
): {
	entityId: string;
	items: Criterion[];
	matchedType: RemoteCriterionType | undefined;
} | null => {
	let matchedType: RemoteCriterionType | undefined;
	let entityId = '';
	let entityName = '';

	for (const criterionType of REMOTE_CRITERION_TYPES) {
		const idMatch = innerFilter.match(
			new RegExp(`${escapeRegExp(criterionType.idProperty)} eq '([^']+)'`)
		);

		if (!idMatch) {
			continue;
		}

		matchedType = criterionType;
		entityId = idMatch[1];

		const nameMatch = innerFilter.match(
			new RegExp(
				`${escapeRegExp(criterionType.nameProperty)} eq '([^']+)'`
			)
		);
		entityName = nameMatch?.[1] ?? entityId;

		break;
	}

	const appIdInMatch = innerFilter.match(/applicationId in \(([^)]+)\)/);
	const eventIdInMatch = innerFilter.match(/eventId in \(([^)]+)\)/);
	const appIdEqMatch = innerFilter.match(/applicationId eq '([^']+)'/);
	const eventIdEqMatch = innerFilter.match(/eventId eq '([^']+)'/);

	// Bail only when the filter matches neither a registered remote criterion
	// type (vocabulary/tag, which list applicationId/eventId with `in`) nor a
	// single-type behavior (applicationId/eventId with `eq`) nor a specific
	// asset (activityKey). A behavior carries no entity id but must still
	// round-trip.

	if (
		!matchedType &&
		!(appIdInMatch && eventIdInMatch) &&
		!(appIdEqMatch && eventIdEqMatch)
	) {
		return null;
	}

	const items: Criterion[] = [];

	if (matchedType) {
		items.push(
			{
				operatorName: RelationalOperators.EQ,
				propertyName: matchedType.idProperty,
				rowId: generateRowId(),
				touched: false,
				valid: true,
				value: entityId,
			} as unknown as Criterion,
			{
				operatorName: RelationalOperators.EQ,
				propertyName: matchedType.nameProperty,
				rowId: generateRowId(),
				touched: false,
				valid: true,
				value: entityName,
			} as unknown as Criterion
		);
	}

	if (appIdInMatch && eventIdInMatch) {

		// A vocabulary/tag criterion lists one or more applicationIds/eventIds.

		const parseIds = (s: string) =>
			s.split(',').map((id) => id.trim().replace(/^'|'$/g, ''));

		items.push({
			operatorName: RelationalOperators.In,
			propertyName: 'applicationId',
			rowId: generateRowId(),
			touched: false,
			valid: true,
			value: parseIds(appIdInMatch[1]),
		} as unknown as Criterion);

		items.push({
			operatorName: RelationalOperators.In,
			propertyName: 'eventId',
			rowId: generateRowId(),
			touched: false,
			valid: true,
			value: parseIds(eventIdInMatch[1]),
		} as unknown as Criterion);
	}
	else if (appIdEqMatch && eventIdEqMatch) {

		// A single-type behavior targets one applicationId/eventId.

		items.push({
			operatorName: RelationalOperators.EQ,
			propertyName: 'applicationId',
			rowId: generateRowId(),
			touched: false,
			valid: true,
			value: appIdEqMatch[1],
		} as unknown as Criterion);

		items.push({
			operatorName: RelationalOperators.EQ,
			propertyName: 'eventId',
			rowId: generateRowId(),
			touched: false,
			valid: true,
			value: eventIdEqMatch[1],
		} as unknown as Criterion);
	}
	else {
		const activityKeyMatch = innerFilter.match(/activityKey eq '([^']+)'/);

		if (activityKeyMatch) {
			items.push({
				operatorName: RelationalOperators.EQ,
				propertyName: 'activityKey',
				rowId: generateRowId(),
				touched: false,
				valid: true,
				value: activityKeyMatch[1],
			} as unknown as Criterion);
		}
	}

	const objectDefinitionNameMatch = innerFilter.match(
		/objectDefinitionName eq '([^']+)'/
	);

	if (objectDefinitionNameMatch) {
		items.push({
			operatorName: RelationalOperators.EQ,
			propertyName: 'objectDefinitionName',
			rowId: generateRowId(),
			touched: false,
			valid: true,
			value: objectDefinitionNameMatch[1],
		} as unknown as Criterion);
	}

	if (matchedType?.supportsCategories) {
		const catRegex =
			/\(categories\/id eq '([^']+)' and categories\/name eq '([^']+)'\)/g;
		const categoryItems: Array<{id: string; name: string}> = [];
		let catMatch: RegExpExecArray | null;

		while ((catMatch = catRegex.exec(innerFilter)) !== null) {
			categoryItems.push({id: catMatch[1], name: catMatch[2]});
		}

		if (categoryItems.length > 0) {
			items.push({
				operatorName: RelationalOperators.In,
				propertyName: 'categories',
				rowId: generateRowId(),
				touched: false,
				valid: true,
				value: categoryItems,
			} as unknown as Criterion);
		}
	}

	const dayMatch = innerFilter.match(/day (gt|ge|lt|le|eq|ne) '([^']+)'/);

	if (dayMatch) {
		items.push({
			operatorName: dayMatch[1],
			propertyName: 'day',
			rowId: generateRowId(),
			touched: false,
			valid: true,
			value: dayMatch[2],
		} as unknown as Criterion);
	}

	return {entityId, items, matchedType};
};

const parseRemoteFilterByCount = (
	queryString: string
): CriterionGroup | null => {
	const match = queryString.match(
		/activities\.filterByCount\(filter='((?:[^']|'')*)'\s*(?:,operator='([^']*)')?(?:,value=(\d+))?\)/
	);

	if (!match) {
		return null;
	}

	const filterContent = match[1].replace(/''/g, "'");
	const occurrenceOperator = match[2] ?? null;
	const occurrenceValue = match[3] !== undefined ? parseInt(match[3]) : null;

	const innerFilter =
		filterContent.startsWith('(') && filterContent.endsWith(')')
			? filterContent.slice(1, -1)
			: filterContent;

	const result = buildInnerFilterItems(innerFilter);

	if (!result) {
		return null;
	}

	const {entityId, items, matchedType} = result;

	const criterionGroup: CriterionGroup = {
		conjunctionName: Conjunctions.And,
		criteriaGroupId: generateGroupId(),
		items,
	};

	const customValue = new CustomValue(
		Map({
			criterionGroup: fromJS(criterionGroup),
			operator: occurrenceOperator,
			value: occurrenceValue,
		})
	);

	// A single-type behavior has no matched remote criterion type: it filters by
	// activity, so use the activities operator and let the property resolve from
	// its eventId (the entity id is empty).

	return wrapInCriteriaGroup([
		{
			operatorName:
				matchedType?.positiveOperator ??
				CustomFunctionOperators.ActivitiesFilterByCount,
			propertyName: matchedType ? entityId : items[0]?.propertyName ?? '',
			rowId: generateRowId(),
			touched: false,
			valid: true,
			value: customValue,
		} as unknown as Criteria,
	]);
};

/**
 * Splits an OData filter string at top-level conjunction operators (and/or),
 * respecting parentheses nesting and single-quoted strings (including escaped
 * '' sequences). Returns the detected conjunction and the individual parts.
 */
const splitTopLevelConjunction = (
	queryString: string
): {conjunction: string; parts: string[]} => {
	const stripped =
		queryString.startsWith('(') && queryString.endsWith(')')
			? queryString.slice(1, -1).trim()
			: queryString.trim();

	const parts: string[] = [];
	let conjunction = Conjunctions.And;
	let current = '';
	let depth = 0;
	let inString = false;
	let i = 0;

	while (i < stripped.length) {
		const char = stripped[i];

		if (inString) {
			if (char === "'" && stripped[i + 1] === "'") {
				current += "''";
				i += 2;
				continue;
			}
			else if (char === "'") {
				inString = false;
			}

			current += char;
		}
		else if (char === "'") {
			inString = true;
			current += char;
		}
		else if (char === '(') {
			depth++;
			current += char;
		}
		else if (char === ')') {
			depth--;
			current += char;
		}
		else if (depth === 0) {
			const remaining = stripped.slice(i);

			if (/^ and /i.test(remaining)) {
				parts.push(current.trim());
				conjunction = Conjunctions.And;
				current = '';
				i += 5;
				continue;
			}
			else if (/^ or /i.test(remaining)) {
				parts.push(current.trim());
				conjunction = Conjunctions.Or;
				current = '';
				i += 4;
				continue;
			}
			else {
				current += char;
			}
		}
		else {
			current += char;
		}

		i++;
	}

	if (current.trim()) {
		parts.push(current.trim());
	}

	return {conjunction, parts};
};

/**
 * Parses a query string that may contain multiple top-level criteria joined by
 * and/or, where some parts are filterByCount calls and others are standard
 * OData expressions. Returns a CriterionGroup combining all parsed criteria.
 */
const parseMultipleCriteria = (queryString: string): CriterionGroup | null => {
	const {conjunction, parts} = splitTopLevelConjunction(queryString);

	const criteriaItems: Criteria[] = [];

	for (const part of parts) {
		if (part.startsWith('activities.filterByCount(')) {
			const result = parseRemoteFilterByCount(part);

			if (result?.items?.length) {
				criteriaItems.push(...result.items);
			}
		}
		else {
			try {
				const encodedQuotes = encodeDoubleQuotesToOdataQuotes(part);
				const trimSpaces = trimSpacesBeforeParams(encodedQuotes);
				const encodedSpecialCharacters =
					encodeSpecialCharacters(trimSpaces);
				const substrings = convertBetweenToSubstring(
					encodedSpecialCharacters
				);
				const token = oDataFilterFn(substrings);
				const stringified = JSON.stringify(token);
				const decodedSpecialCharacters =
					decodeSpecialCharacters(stringified);
				const oDataASTNode = JSON.parse(decodedSpecialCharacters);
				const criteriaArray = toCriteria({oDataASTNode});
				const parsed = isCriterionGroup(criteriaArray[0])
					? criteriaArray[0]
					: wrapInCriteriaGroup(criteriaArray);
				const decoded = decodeValueFromCriteria(parsed);

				criteriaItems.push(...decoded.items);
			}
			catch {

				// skip unparseable parts

			}
		}
	}

	if (!criteriaItems.length) {
		return null;
	}

	return {
		conjunctionName: conjunction,
		criteriaGroupId: generateGroupId(),
		items: criteriaItems,
	};
};

/**
 * Converts an OData filter query string to an object that can be used by the
 * criteria builder
 */
const translateQueryToCriteria = (queryString: string): Criteria => {
	let criteria;

	try {
		if (queryString === '()') {
			throw new Error('queryString is ()');
		}

		const encodedQuotes = encodeDoubleQuotesToOdataQuotes(queryString);
		const trimSpaces = trimSpacesBeforeParams(encodedQuotes);
		const encodedSpecialCharacters = encodeSpecialCharacters(trimSpaces);
		const substrings = convertBetweenToSubstring(encodedSpecialCharacters);
		const token = oDataFilterFn(substrings);
		const stringfied = JSON.stringify(token);
		const decodedSpecialCharacters = decodeSpecialCharacters(stringfied);

		const oDataASTNode = JSON.parse(decodedSpecialCharacters);
		const criteriaArray = toCriteria({oDataASTNode});

		criteria = isCriterionGroup(criteriaArray[0])
			? criteriaArray[0]
			: wrapInCriteriaGroup(criteriaArray);

		criteria = decodeValueFromCriteria(criteria);
	}
	catch (e) {
		try {
			criteria = parseMultipleCriteria(queryString);
		}
		catch (innerError) {

			// eslint-disable-next-line no-console
			console.error(
				'Faro: parseMultipleCriteria fallback failed for queryString',
				queryString,
				innerError
			);
			criteria = null;
		}
	}

	return criteria;
};

/**
 * Recursively transforms the AST generated by the odata-v4-parser library into
 * a shape the criteria builder expects. Returns an array so that left and right
 * arguments can be concatenated together.
 */
const toCriteria = (context: Context): Criteria[] => {
	const {oDataASTNode} = context;

	const expressionName = getExpressionName(oDataASTNode);

	let criterion: Criteria[] | undefined;

	if (oDataASTNode.type === EXPRESSION_TYPES.NOT) {
		criterion = transformNotNode(context);
	}
	else if (oDataASTNode.type === EXPRESSION_TYPES.COMMON) {
		criterion = transformCommonNode(context);
	}
	else if (oDataASTNode.type === EXPRESSION_TYPES.METHOD_CALL) {
		criterion = transformFunctionalNode(context);
	}
	else if (oDataASTNode.type === EXPRESSION_TYPES.FUNCTION) {
		criterion = transformCustomFunctionNode(context);
	}
	else if (isValueType(RelationalOperators, expressionName)) {
		criterion = transformOperatorNode(context);
	}
	else if (isValueType(Conjunctions, expressionName)) {
		criterion = transformConjunctionNode(context);
	}
	else if (expressionName === GROUP) {
		criterion = transformGroupNode(context);
	}

	return criterion ?? [];
};

/**
 * Transform an operator expression node into a criterion for the criteria
 * builder.
 */
const transformCommonNode = ({oDataASTNode}: Context): Criteria[] => {
	const nextNodeExpression = getNextOperatorExpression(oDataASTNode);

	let value;

	if (nextNodeExpression.type === EXPRESSION_TYPES.FUNCTION) {
		return transformCustomFunctionNode({oDataASTNode: nextNodeExpression});
	}
	else if (nextNodeExpression.type === EXPRESSION_TYPES.METHOD_CALL) {
		const methodExpressionName = getExpressionName(nextNodeExpression);

		if (methodExpressionName === 'substring') {
			const [{raw: propertyName}, {raw: start}, {raw: end}] =
				nextNodeExpression.value.parameters;

			return [
				{
					operatorName: OPERATORS.Between,
					propertyName,
					rowId: generateRowId(),
					touched: false,
					valid: true,
					value: {
						end: removeQuotes(end),
						start: removeQuotes(start),
					},
				},
			] as unknown as Criterion[];
		}

		return [];
	}
	else {
		const anyExpression = get(nextNodeExpression, [
			'value',
			'next',
			'value',
		]);

		const methodExpression = get(anyExpression, [
			'value',
			'predicate',
			'value',
		]);

		const methodExpressionName = getExpressionName(methodExpression);

		if (methodExpressionName == OPERATORS.Contains) {
			value = removeQuotes(methodExpression.value.parameters[1].raw);
		}
		else if (methodExpressionName == OPERATORS.EQ) {
			value = removeQuotes(methodExpression.value.right.raw);
		}

		return [
			{
				operatorName: methodExpressionName,
				propertyName: nextNodeExpression.value.current.raw,
				rowId: generateRowId(),
				touched: false,
				valid: true,
				value,
			},
		] as unknown as Criteria[];
	}
};

/**
 * Transforms conjunction expression node into a criterion for the criteria
 * builder. If it comes across a grouping sharing an AND and OR conjunction, it
 * will add a new grouping so the criteria builder doesn't require a user to
 * know operator precedence.
 * @returns an array containing the concatenated left and right values of a
 * conjunction expression or a new grouping.
 */
const transformConjunctionNode = (context: Context): Criteria[] => {
	const {oDataASTNode} = context;

	const conjunctionType = oDataASTNode.type;
	const nextNode = oDataASTNode.value;

	return hasDifferentConjunctions(context)
		? toCriteria(addNewGroup(context))
		: [
				...toCriteria({
					oDataASTNode: nextNode.left,
					prevConjunction: conjunctionType,
				}),
				...toCriteria({
					oDataASTNode: nextNode.right,
					prevConjunction: conjunctionType,
				}),
			];
};

/**
 * Transform a custom function expression node into a criterion for the criteria
 * builder.
 * @returns an array containing the object representation of an operator
 * criterion
 */
const transformCustomFunctionNode = ({oDataASTNode}: Context): Criterion[] => {
	const {fn, params} = oDataASTNode.value;

	const {name, namespace} = fn.value;

	let detectedFilterType: 'tag' | 'vocabulary' | null = null;
	let detectedEntityId: string | null = null;
	let detectedEntityName: string | null = null;

	const customValue = new CustomValue(
		params.value.reduce((accIMap: Map<string, any>, cur: any) => {
			const {
				name: {
					value: {name},
				},
				value: {value},
			} = cur.value;

			if (name === 'filter') {
				const rawFilterFull = parseNestedOdataString(value.raw);
				const rawFilter =
					rawFilterFull.startsWith('(') && rawFilterFull.endsWith(')')
						? rawFilterFull.slice(1, -1)
						: rawFilterFull;

				const innerResult = buildInnerFilterItems(rawFilter);

				if (innerResult) {
					return accIMap.set(
						'criterionGroup',
						fromJS({
							conjunctionName: Conjunctions.And,
							criteriaGroupId: generateGroupId(),
							items: innerResult.items,
						})
					);
				}

				const parsed = translateQueryToCriteria(rawFilter);
				const criterionGroupIMap = fromJS(parsed);

				if (!parsed) {
					const vocIdMatch = rawFilter.match(
						/vocabularies\/id eq '([^']+)'/
					);
					const tagIdMatch = rawFilter.match(/tags\/id eq '([^']+)'/);

					if (vocIdMatch) {
						detectedFilterType = 'vocabulary';
						detectedEntityId = vocIdMatch[1];
						detectedEntityName =
							rawFilter.match(
								/vocabularies\/name eq '([^']+)'/
							)?.[1] ?? null;
					}
					else if (tagIdMatch) {
						detectedFilterType = 'tag';
						detectedEntityId = tagIdMatch[1];
						detectedEntityName =
							rawFilter.match(/tags\/name eq '([^']+)'/)?.[1] ??
							null;
					}
				}

				return detectedEntityName
					? accIMap
							.set('criterionGroup', criterionGroupIMap)
							.set('_name', detectedEntityName)
					: accIMap.set('criterionGroup', criterionGroupIMap);
			}
			else if (name === 'value' && isFinite(parseInt(value.raw))) {
				return accIMap.set(name, parseInt(value.raw));
			}
			else {
				return accIMap.set(name, removeQuotes(value.raw));
			}
		}, Map())
	);

	const firstItemPropertyName = customValue.getIn([
		'criterionGroup',
		'items',
		0,
		'propertyName',
	]);
	const isVocabularyFilter =
		firstItemPropertyName === 'vocabularies/id' ||
		detectedFilterType === 'vocabulary';
	const isTagFilter =
		firstItemPropertyName === 'tags/id' || detectedFilterType === 'tag';

	const operatorName = isVocabularyFilter
		? CustomFunctionOperators.VocabulariesFilter
		: isTagFilter
			? CustomFunctionOperators.TagsFilter
			: getOperatorNameFromFunctionName(name, namespace);

	const propertyName =
		isVocabularyFilter || isTagFilter
			? customValue.getIn(['criterionGroup', 'items', 0, 'value']) ??
				detectedEntityId
			: firstItemPropertyName;

	let touched:
		| boolean
		| {asset: boolean; occurenceCount: boolean}
		| {
				attribute: boolean;
				attributeValue: boolean;
				occurenceCount: boolean;
		  } = false;
	let valid:
		| boolean
		| {asset: boolean; occurenceCount: boolean}
		| {
				attribute: boolean;
				attributeValue: boolean;
				occurenceCount: boolean;
		  } = true;

	// TODO: Prob need one here for PropertyTypes.Event

	if (
		SUPPORTED_PROPERTY_TYPES_MAP[PropertyTypes.Behavior].includes(
			operatorName
		)
	) {
		touched = {asset: false, occurenceCount: false};
		valid = {asset: true, occurenceCount: true};
	}
	else if (
		SUPPORTED_PROPERTY_TYPES_MAP[PropertyTypes.Event].includes(operatorName)
	) {
		touched = {
			attribute: false,
			attributeValue: false,
			occurenceCount: false,
		};
		valid = {attribute: true, attributeValue: true, occurenceCount: true};
	}

	return [
		{
			operatorName,
			propertyName,
			rowId: generateRowId(),
			touched,
			valid,
			value: customValue,
		},
	] as unknown as Criterion[];
};

/**
 * Transform a function expression node into a criterion for the criteria
 * builder.
 * @returns an array containing the object representation of an operator
 * criterion
 */
const transformFunctionalNode = ({oDataASTNode}: Context): Criterion[] =>
	[
		{
			operatorName: getFunctionName(oDataASTNode),
			propertyName: oDataASTNode.value.parameters[0].raw,
			rowId: generateRowId(),
			touched: false,
			valid: true,
			value: removeQuotes(oDataASTNode.value.parameters[1].raw),
		},
	] as unknown as Criterion[];

/**
 * Transforms a group expression node into a criterion for the criteria
 * builder. If it comes across a grouping that is redundant (doesn't provide
 * readability improvements, superfluous to order of operations), it will remove
 * it.
 * @returns Criterion representation of an AST expression node in an array.
 */
const transformGroupNode = (context: Context): Criteria[] => {
	const {oDataASTNode, prevConjunction} = context;

	return isRedundantGroup(context)
		? toCriteria(skipGroup(context))
		: [
				{
					conjunctionName: getConjunctionForGroup(oDataASTNode),
					criteriaGroupId: generateGroupId(),
					items: toCriteria({
						lastNodeWasGroup: true,
						oDataASTNode: oDataASTNode.value,
						prevConjunction,
					}),
				},
			];
};

/**
 * Transform an operator expression node into a criterion for the criteria
 * builder.
 * @returns An array containing the object representation of an operator
 * criterion.
 */
const transformNotNode = ({oDataASTNode}: Context): Criteria[] => {
	const nextNodeExpression = getNextOperatorExpression(oDataASTNode);

	const nextNodeExpressionName = getExpressionName(nextNodeExpression);

	let returnValue: Criteria[] = [];

	if (nextNodeExpressionName == OPERATORS.Contains) {
		returnValue = [
			{
				...transformFunctionalNode({
					oDataASTNode: nextNodeExpression,
				})[0],
				operatorName: NotOperators.NotContains,
			},
		] as unknown as Criteria[];
	}
	else if (isValueType(CustomFunctionOperators, nextNodeExpressionName)) {
		const criterion = transformCustomFunctionNode({
			oDataASTNode: nextNodeExpression,
		})[0];

		returnValue = [
			{
				...criterion,
				operatorName: `not-${criterion.operatorName}`,
			},
		] as unknown as Criteria[];
	}
	else if (nextNodeExpression.type == EXPRESSION_TYPES.PROPERTY_PATH) {
		const anyExpression = nextNodeExpression.value.next.value;

		const methodExpression = anyExpression.value.predicate.value;

		const methodExpressionName = getExpressionName(methodExpression);

		if (methodExpressionName == OPERATORS.Contains) {
			returnValue = [
				{
					...transformFunctionalNode({
						oDataASTNode: nextNodeExpression,
					})[0],
					operatorName: NotOperators.NotContains,
				},
			] as unknown as Criteria[];
		}
	}

	return returnValue;
};

/**
 * Transform an operator expression node into a criterion for the criteria
 * builder.
 * @returns An array containing the object representation of an operator
 * criterion.
 */
const transformOperatorNode = ({oDataASTNode}: Context): Criterion[] => {
	const valueType = oDataASTNode.value.right.value;

	let value: string | number | null = removeSurroundingQuotes(
		oDataASTNode.value.right.raw
	);

	if (EDM_NUMBERS.includes(valueType)) {
		value = parseFloat(value);
	}
	else if (valueType === EDM_STRING) {
		value = unescapeSingleQuotes(value as string);
	}
	else if (valueType === EDM_NULL) {
		value = null;
	}

	return [
		{
			operatorName: getExpressionName(oDataASTNode),
			propertyName: oDataASTNode.value.left.raw,
			rowId: generateRowId(),
			touched: false,
			valid: true,
			value,
		},
	] as unknown as Criterion[];
};

/**
 * Unescape single quotes in a string for general purposes.
 */
const unescapeSingleQuotes = (text: string) => text.replace(/''/g, "'");

/**
 * Wraps the criteria items in a criterion group.
 */
export const wrapInCriteriaGroup = (
	criteriaArray: Criteria[]
): CriterionGroup => ({
	conjunctionName: Conjunctions.And,
	criteriaGroupId: generateGroupId(),
	items: criteriaArray,
});

export {buildQueryString, translateQueryToCriteria};
