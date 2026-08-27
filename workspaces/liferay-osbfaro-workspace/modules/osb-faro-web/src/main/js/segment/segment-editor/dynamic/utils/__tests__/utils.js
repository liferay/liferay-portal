jest.unmock('react-dom');

import * as data from 'test/data';
import * as utils from '../utils';
import {
	ACTIVITY_KEY,
	Conjunctions,
	CustomFunctionOperators,
	isKnown,
	isUnknown,
	PropertyTypes,
	RelationalOperators,
	SUPPORTED_OPERATORS_MAP,
	TimeSpans,
} from '../constants';
import {EntityType} from '../../context/referencedObjects';
import {fromJS, Map} from 'immutable';
import {Property} from 'shared/util/records';

const {
	AccountsFilter,
	ActivitiesFilterByCount,
	InterestsFilter,
	SessionsFilter,
} = CustomFunctionOperators;
const {And} = Conjunctions;
const {EQ, GT} = RelationalOperators;

describe('utils', () => {
	describe('createInterestProperty', () => {
		it('should create an Interest Property', () => {
			const name = 'Some Interest Property';
			const property = utils.createInterestProperty(name);

			expect(property).toBeInstanceOf(Property);
			expect(property.name).toBe(name);
			expect(property.label).toBe(name);
			expect(property.propertyKey).toBe('interest');
			expect(property.type).toBe('interest');
		});
	});

	describe('createNewGroup', () => {
		it('should create a new CriterionGroup', () => {
			const items = [];

			const criterionGroup = utils.createNewGroup(items);

			expect(criterionGroup.items).toBe(items);
			expect(criterionGroup.conjunctionName).toBe(And);
			expect(criterionGroup.criteriaGroupId).toBeTruthy();
		});

		it('should create a new CriterionGroup with the supplied conjunction', () => {
			const items = [];

			const criterionGroup = utils.createNewGroup(items, 'or');

			expect(criterionGroup.conjunctionName).toBe('or');
		});
	});

	describe('generateGroupId', () => {
		it('should generate a group id', () => {
			expect(utils.generateGroupId()).toContain('group_');
		});
	});

	describe('generateRowId', () => {
		it('should generate a row id', () => {
			expect(utils.generateRowId()).toContain('row_');
		});
	});

	describe('getChildGroupIds', () => {
		it('should grab child group ids', () => {
			const excluded = 'excluded';
			const criterionGroup = {
				criteriaGroupId: excluded,
				items: [
					{criteriaGroupId: 'test', items: []},
					{criteriaGroupId: 'foo', items: []},
					{
						criteriaGroupId: 'stuff',
						items: [{criteriaGroupId: 'bar', items: []}],
					},
				],
			};

			const childGroupIds = utils.getChildGroupIds(criterionGroup);

			expect(childGroupIds.length).toBe(4);
			expect(childGroupIds.includes(excluded)).toBeFalse();
		});
	});

	describe('getPropertyContextFromRaw', () => {
		it('should get context name from a raw property string', () => {
			const context = 'custom';

			expect(
				utils.getPropertyContextFromRaw(`${context}/city/value`)
			).toBe(context);
		});

		it('should return null if no context exists', () => {
			expect(utils.getPropertyContextFromRaw('Foo')).toBeNull();
		});
	});

	describe('getPropertyNameFromRaw', () => {
		it('should get property name from a raw property string', () => {
			const propertyName = 'city';

			expect(
				utils.getPropertyNameFromRaw(
					`demographics/${propertyName}/value`
				)
			).toBe(propertyName);
		});
	});

	describe('getSupportedOperatorsFromType', () => {
		it('should get the supported operators from type', () => {
			expect(
				utils.getSupportedOperatorsFromType(PropertyTypes.AccountNumber)
			).toBe(SUPPORTED_OPERATORS_MAP[PropertyTypes.AccountNumber]);
		});
	});

	describe('isCriterionGroup', () => {
		it('should return true when value is a CriterionGroup', () => {
			expect(
				utils.isCriterionGroup({
					conjunctionName: And,
					criteriaGroupId: 'foo',
					items: [],
				})
			).toBeTrue();
		});

		it('should return false when value is a Criterion', () => {
			expect(utils.isCriterionGroup(data.generateCriterion())).toBe(
				false
			);
		});

		it('should return false when value null', () => {
			expect(utils.isCriterionGroup(null)).toBeFalse();
		});
	});

	describe('isMap', () => {
		it('should return true when value is an instance of ImmutableMap', () => {
			expect(utils.isMap(new Map())).toBeTrue();
		});

		it('should return false when value is not an instance of ImmutableMap', () => {
			expect(utils.isMap({})).toBeFalse();
		});
	});

	describe('getNestedOrLimitState', () => {
		const orWith = (count) => ({
			conjunctionName: Conjunctions.Or,
			criteriaGroupId: 'group-1',
			items: Array.from({length: count}, (_, i) => ({rowId: `r${i}`})),
		});

		it('should return null for missing criteria', () => {
			expect(utils.getNestedOrLimitState(null)).toBeNull();
			expect(utils.getNestedOrLimitState(undefined)).toBeNull();
		});

		it('should return null for an AND group regardless of items', () => {
			const andGroup = {
				conjunctionName: Conjunctions.And,
				criteriaGroupId: 'group-1',
				items: Array.from({length: 5}, (_, i) => ({rowId: `r${i}`})),
			};

			expect(utils.getNestedOrLimitState(andGroup)).toBeNull();
		});

		it('should return null for an OR group below the limit', () => {
			expect(utils.getNestedOrLimitState(orWith(2))).toBeNull();
		});

		it('should return reachedLimit for an OR group at the limit', () => {
			expect(utils.getNestedOrLimitState(orWith(3))).toBe('reachedLimit');
		});

		it('should return exceedsLimit for an OR group past the limit', () => {
			expect(utils.getNestedOrLimitState(orWith(4))).toBe('exceedsLimit');
		});
	});

	describe('hasNestedOrExceeded', () => {
		const makeRow = (rowId) => ({rowId});

		it('should return false for missing criteria', () => {
			expect(utils.hasNestedOrExceeded(null)).toBeFalse();
			expect(utils.hasNestedOrExceeded(undefined)).toBeFalse();
		});

		it('should return false for a root group with no nested children', () => {
			const root = {
				conjunctionName: Conjunctions.And,
				criteriaGroupId: 'root',
				items: [makeRow('r0'), makeRow('r1')],
			};

			expect(utils.hasNestedOrExceeded(root)).toBeFalse();
		});

		it('should ignore the size of the root OR group itself', () => {
			const rootOr = {
				conjunctionName: Conjunctions.Or,
				criteriaGroupId: 'root',
				items: [
					makeRow('r0'),
					makeRow('r1'),
					makeRow('r2'),
					makeRow('r3'),
				],
			};

			expect(utils.hasNestedOrExceeded(rootOr)).toBeFalse();
		});

		it('should return false when a nested OR group is at the limit', () => {
			const root = {
				conjunctionName: Conjunctions.And,
				criteriaGroupId: 'root',
				items: [
					{
						conjunctionName: Conjunctions.Or,
						criteriaGroupId: 'nested',
						items: [makeRow('r0'), makeRow('r1'), makeRow('r2')],
					},
				],
			};

			expect(utils.hasNestedOrExceeded(root)).toBeFalse();
		});

		it('should return true when a nested OR group has more than 3 items', () => {
			const root = {
				conjunctionName: Conjunctions.And,
				criteriaGroupId: 'root',
				items: [
					{
						conjunctionName: Conjunctions.Or,
						criteriaGroupId: 'nested',
						items: [
							makeRow('r0'),
							makeRow('r1'),
							makeRow('r2'),
							makeRow('r3'),
						],
					},
				],
			};

			expect(utils.hasNestedOrExceeded(root)).toBeTrue();
		});

		it('should return true for an exceeded OR group deeper in the tree', () => {
			const root = {
				conjunctionName: Conjunctions.And,
				criteriaGroupId: 'root',
				items: [
					{
						conjunctionName: Conjunctions.And,
						criteriaGroupId: 'mid',
						items: [
							{
								conjunctionName: Conjunctions.Or,
								criteriaGroupId: 'leaf',
								items: [
									makeRow('r0'),
									makeRow('r1'),
									makeRow('r2'),
									makeRow('r3'),
								],
							},
						],
					},
				],
			};

			expect(utils.hasNestedOrExceeded(root)).toBeTrue();
		});
	});

	describe('isOfKnownType', () => {
		it('should return true when value is isKnown or isUnknown', () => {
			expect(utils.isOfKnownType(isKnown)).toBeTrue();
			expect(utils.isOfKnownType(isUnknown)).toBeTrue();
		});

		it('should return false when value is not isKnown or isUnknown', () => {
			expect(utils.isOfKnownType('foo')).toBeFalse();
		});
	});

	describe('objectToFormData', () => {
		it('should convert input object to FormData', () => {
			expect(utils.objectToFormData({foo: 'bar'})).toBeInstanceOf(
				FormData
			);
		});
	});

	describe('parseActivityKey', () => {
		it('should return an object containing the eventId, id, & objectType from the activityKey', () => {
			expect(utils.parseActivityKey('foo#bar#test')).toMatchObject({
				eventId: 'bar',
				id: 'test',
				objectType: 'foo',
			});
		});
	});

	describe('jsDatetoYYYYMMDD', () => {
		it('should return Date object as a string in YYYY-MM-DD format', () => {
			expect(utils.jsDatetoYYYYMMDD(new Date('12-31-2012'))).toBe(
				'2012-12-31'
			);
		});
	});

	describe('findPropertyByCriterion', () => {
		const makeBehaviorCriterion = (activityKey) =>
			data.generateCriterion({
				operatorName: ActivitiesFilterByCount,
				propertyName: ACTIVITY_KEY,
				value: fromJS({
					criterionGroup: {
						conjunctionName: And,
						criteriaGroupId: 'group_0',
						items: [
							{
								operatorName: EQ,
								propertyName: ACTIVITY_KEY,
								value: activityKey,
							},
							{
								operatorName: GT,
								propertyName: 'day',
								value: TimeSpans.Last24Hours,
							},
						],
					},
					operator: GT,
					value: 12,
				}),
			});

		// Stored segments use application-specific eventIds; each must map back
		// to its generic behavior property so the criterion still resolves.

		it.each([
			['Page#pageViewed#123123', 'view'],
			['WebContent#webContentViewed#123123', 'view'],
			['Blog#blogViewed#123123', 'view'],
			['Document#documentDownloaded#123123', 'download'],
			['Form#formSubmitted#123123', 'submit'],
		])(
			'should map the stored activityKey %s back to the %s behavior property',
			(activityKey, expectedName) => {
				const property = utils.findPropertyByCriterion(
					makeBehaviorCriterion(activityKey)
				);

				expect(property).toBeInstanceOf(Property);
				expect(property.name).toBe(expectedName);
				expect(property.propertyKey).toBe('web');
				expect(property.type).toBe('behavior');
			}
		);

		it('should return the account name Property when provided with an account name Criterion', () => {
			const criterion = data.generateCriterion({
				operatorName: AccountsFilter,
				propertyName: 'accountName',
				value: fromJS({
					criterionGroup: {
						conjunctionName: And,
						criteriaGroupId: 'group_0',
						items: [
							{
								operatorName: EQ,
								propertyName: 'organization/accountName/value',
								value: 'foo',
							},
						],
					},
				}),
			});

			const mockProperty = new Property({
				entityName: 'Account',
				label: 'Account Name',
				name: 'accountName',
				type: 'account-text',
			});

			const property = utils.findPropertyByCriterion(criterion);

			expect(property.get('name')).toEqual(mockProperty.get('name'));
		});

		it('should return the session url Property when provided with a session url Criterion', () => {
			const criterion = data.generateCriterion({
				operatorName: SessionsFilter,
				propertyName: 'context/url',
				value: fromJS({
					criterionGroup: {
						conjunctionName: And,
						criteriaGroupId: 'group_0',
						items: [
							{
								operatorName: EQ,
								propertyName: 'context/url',
								value: 'https://www.liferay.com',
							},
							{
								operatorName: GT,
								propertyName: 'completeDate',
								value: TimeSpans.Last24Hours,
							},
						],
					},
				}),
			});

			const property = utils.findPropertyByCriterion(criterion);

			expect(property).toBeInstanceOf(Property);
			expect(property.name).toBe('context/url');
			expect(property.propertyKey).toBe('session');
			expect(property.type).toBe('session-text');
		});

		it('should return the Channel Property when provided with a Channel Criterion', () => {
			const criterion = data.generateCriterion({
				operatorName: SessionsFilter,
				propertyName: 'context/channel',
				value: fromJS({
					criterionGroup: {
						conjunctionName: And,
						criteriaGroupId: 'group_0',
						items: [
							{
								operatorName: EQ,
								propertyName: 'context/channel',
								value: 'direct',
							},
						],
					},
				}),
			});

			const property = utils.findPropertyByCriterion(criterion);

			expect(property).toBeInstanceOf(Property);
			expect(property.name).toBe('context/channel');
			expect(property.propertyKey).toBe('session');
			expect(property.type).toBe('session-channel');
		});

		it.each([
			['context/acquisitionSource', 'a default UTM parameter'],
			['context/utm_cid', 'a custom UTM parameter'],
		])(
			'should return the UTM Parameter Property when provided with %s (%s) Criterion',
			(propertyName) => {
				const criterion = data.generateCriterion({
					operatorName: SessionsFilter,
					propertyName,
					value: fromJS({
						criterionGroup: {
							conjunctionName: And,
							criteriaGroupId: 'group_0',
							items: [
								{
									operatorName: EQ,
									propertyName,
									value: 'google',
								},
							],
						},
					}),
				});

				const property = utils.findPropertyByCriterion(criterion);

				expect(property).toBeInstanceOf(Property);
				expect(property.name).toBe('attribute/utmParameter');
				expect(property.propertyKey).toBe('session');
				expect(property.type).toBe('session-utm-parameter');
			}
		);

		it('should return the session url Property, not the UTM Parameter Property, for a non-UTM session attribute', () => {
			const criterion = data.generateCriterion({
				operatorName: SessionsFilter,
				propertyName: 'context/referrer',
				value: fromJS({
					criterionGroup: {
						conjunctionName: And,
						criteriaGroupId: 'group_0',
						items: [
							{
								operatorName: EQ,
								propertyName: 'context/referrer',
								value: 'https://www.google.com',
							},
						],
					},
				}),
			});

			const property = utils.findPropertyByCriterion(criterion);

			expect(property.type).toBe('session-text');
			expect(property.type).not.toBe('session-utm-parameter');
		});

		it('should return the interest Property when provided with an interest Criterion', () => {
			const criterion = data.generateCriterion({
				operatorName: InterestsFilter,
				propertyName: 'name',
				value: fromJS({
					criterionGroup: {
						conjunctionName: And,
						criteriaGroupId: 'group_0',
						items: [
							{
								operatorName: EQ,
								propertyName: 'name',
								value: 'foo',
							},
							{
								operatorName: EQ,
								propertyName: 'score',
								value: 'true',
							},
						],
					},
				}),
			});

			const property = utils.findPropertyByCriterion(criterion);

			expect(property).toBeInstanceOf(Property);
			expect(property.name).toBe('name');
			expect(property.propertyKey).toBe('interest');
			expect(property.type).toBe('interest');
		});

		it('should return the given name Property when provided with a given name Criterion', () => {
			const criterion = data.generateCriterion({
				operatorName: EQ,
				propertyName: 'demographics/givenName/value',
				value: 'test',
			});

			const mockProperty = new Property();

			const referencedPropertiesIMap = fromJS({
				individual: {
					demographics: {
						givenName: mockProperty,
					},
				},
			});

			const property = utils.findPropertyByCriterion(
				criterion,
				referencedPropertiesIMap
			);

			expect(property).toBe(mockProperty);
		});

		it('should return a Vocabulary Property whose label uses the name extracted from the criterion group when not in the cache', () => {
			const criterion = data.generateCriterion({
				operatorName: CustomFunctionOperators.VocabulariesFilter,
				propertyName: 'vocab-id',
				value: fromJS({
					criterionGroup: {
						conjunctionName: And,
						criteriaGroupId: 'group_0',
						items: [
							{
								operatorName: EQ,
								propertyName: 'vocabularies/id',
								value: 'vocab-id',
							},
							{
								operatorName: EQ,
								propertyName: 'vocabularies/name',
								value: 'My Vocabulary',
							},
						],
					},
				}),
			});

			const property = utils.findPropertyByCriterion(criterion, Map());

			expect(property).toBeInstanceOf(Property);
			expect(property.name).toBe('vocab-id');
			expect(property.label).toBe('My Vocabulary');
			expect(property.propertyKey).toBe('vocabulary');
		});

		it('should return the cached Vocabulary Property when present in referencedPropertiesIMap', () => {
			const criterion = data.generateCriterion({
				operatorName: CustomFunctionOperators.VocabulariesFilter,
				propertyName: 'vocab-id',
				value: fromJS({criterionGroup: {items: []}}),
			});

			const cachedProperty = new Property({
				label: 'Cached Vocabulary',
				name: 'vocab-id',
				propertyKey: 'vocabulary',
			});

			const referencedPropertiesIMap = fromJS({
				vocabulary: {'vocab-id': cachedProperty},
			});

			const property = utils.findPropertyByCriterion(
				criterion,
				referencedPropertiesIMap
			);

			expect(property).toBe(cachedProperty);
		});

		it('should return a Tag Property whose label uses the name extracted from the criterion group when not in the cache', () => {
			const criterion = data.generateCriterion({
				operatorName: CustomFunctionOperators.TagsFilter,
				propertyName: 'tag-id',
				value: fromJS({
					criterionGroup: {
						conjunctionName: And,
						criteriaGroupId: 'group_0',
						items: [
							{
								operatorName: EQ,
								propertyName: 'tags/id',
								value: 'tag-id',
							},
							{
								operatorName: EQ,
								propertyName: 'tags/name',
								value: 'My Tag',
							},
						],
					},
				}),
			});

			const property = utils.findPropertyByCriterion(criterion, Map());

			expect(property).toBeInstanceOf(Property);
			expect(property.name).toBe('tag-id');
			expect(property.label).toBe('My Tag');
			expect(property.propertyKey).toBe('tag');
		});

		it('should return the cached Tag Property when present in referencedPropertiesIMap', () => {
			const criterion = data.generateCriterion({
				operatorName: CustomFunctionOperators.TagsFilter,
				propertyName: 'tag-id',
				value: fromJS({criterionGroup: {items: []}}),
			});

			const cachedProperty = new Property({
				label: 'Cached Tag',
				name: 'tag-id',
				propertyKey: 'tag',
			});

			const referencedPropertiesIMap = fromJS({
				tag: {'tag-id': cachedProperty},
			});

			const property = utils.findPropertyByCriterion(
				criterion,
				referencedPropertiesIMap
			);

			expect(property).toBe(cachedProperty);
		});
	});

	describe('isValid', () => {
		it.each`
			value        | result
			${'test'}    | ${true}
			${123}       | ${true}
			${null}      | ${true}
			${true}      | ${true}
			${false}     | ${true}
			${''}        | ${false}
			${undefined} | ${false}
		`('should return $result if $value', ({result, value}) => {
			expect(utils.isValid(value)).toBe(result);
		});
	});

	describe('validateSegmentInputs', () => {
		it('should validate as false if a criterion is invalid', () => {
			expect(
				utils.validateSegmentInputs(
					data.mockNewCriteria(10, {valid: false})
				)
			).toBeFalse();
		});

		it('should validate as true if all criterions are valid', () => {
			expect(
				utils.validateSegmentInputs(data.mockNewCriteriaNested())
			).toBeTrue();
		});
	});

	describe('invalidateCriterionWithMissingProperty', () => {
		it('should invalidate criterions with missing property', () => {
			const criteria = data.mockNewCriteria(1, {
				propertyName: 'demographics/firstName/value',
			});

			expect(
				utils.invalidateCriterionWithMissingProperty(
					criteria,
					new Map({
						demographics: new Map(),
					})
				).items[0].valid
			).toBeFalse();
		});

		it('should not invalidate criterions with matched property', () => {
			const criteria = data.mockNewCriteria(1, {
				propertyName: 'demographics/firstName/value',
				valid: true,
			});

			expect(
				utils.invalidateCriterionWithMissingProperty(
					criteria,
					new Map({
						individual: new Map({
							demographics: new Map({firstName: new Property()}),
						}),
					})
				).items[0].valid
			).toBeTrue();
		});
	});

	describe('convertFieldMappingToAccountProperty', () => {
		const accountFieldMapping = {
			context: 'organization',
			id: '345606994945962466',
			name: 'accountName',
			ownerType: 'account',
			rawType: 'Text',
		};

		it('should convert fieldMapping to an Account Property Record', () => {
			const result =
				utils.convertFieldMappingToAccountProperty(accountFieldMapping);

			expect(result).toBeInstanceOf(Property);
			expect(result.id).toBe('345606994945962466');
			expect(result.name).toBe('345606994945962466');
			expect(result.label).toBe('accountName');
			expect(result.propertyKey).toBe('account');
			expect(result.type).toBe('account-text');
		});
	});

	describe('convertFieldMappingToIndividualProperty', () => {
		const individualFieldMapping = {
			context: 'demographics',
			id: '335454102264596251',
			name: 'additionalName',
			ownerType: 'individual',
			rawType: 'Text',
		};

		it('should convert fieldMapping to an Individual Property Record', () => {
			const result = utils.convertFieldMappingToIndividualProperty(
				individualFieldMapping
			);

			expect(result).toBeInstanceOf(Property);
			expect(result.id).toBe('335454102264596251');
			expect(result.name).toBe('demographics/335454102264596251/value');
			expect(result.label).toBe('additionalName');
			expect(result.propertyKey).toBe('individual');
			expect(result.type).toBe('text');
		});
	});

	describe('convertFieldMappingsToProperties', () => {
		it('should convert fieldMappings to Properties', () => {
			const fieldMappingsIMap = fromJS({
				account: {
					organization: {
						accountName: {
							context: 'organization',
							id: '345606994945962466',
							name: 'accountName',
							ownerType: 'account',
							rawType: 'Text',
						},
					},
				},
				individual: {
					custom: {
						additionaName: {
							context: 'custom',
							id: '123123',
							name: 'customIndividualField',
							ownerType: 'individual',
							rawType: 'Text',
						},
					},
					demographics: {
						additionaName: {
							context: 'demographics',
							id: '335454102264596251',
							name: 'additionalName',
							ownerType: 'individual',
							rawType: 'Text',
						},
					},
				},
				organization: {
					custom: {
						additionaName: {
							context: 'custom',
							id: '321321',
							name: 'customOrganizationField',
							ownerType: 'organization',
							rawType: 'Text',
						},
					},
				},
			});

			const result =
				utils.convertFieldMappingsToProperties(fieldMappingsIMap);

			const accountProp = result.getIn([
				'account',
				'organization',
				'accountName',
			]);
			expect(accountProp).toBeInstanceOf(Property);
			expect(accountProp.id).toBe('345606994945962466');
			expect(accountProp.propertyKey).toBe('account');
			expect(accountProp.type).toBe('account-text');

			const individualProp = result.getIn([
				'individual',
				'demographics',
				'additionaName',
			]);
			expect(individualProp).toBeInstanceOf(Property);
			expect(individualProp.id).toBe('335454102264596251');
			expect(individualProp.propertyKey).toBe('individual');
		});
	});

	describe('parseReferencedEntityId', () => {
		it('should parse referenced entity id', () => {
			const referencedEntities = new Map({
				assets: new Map({'123_title': 'test'}),
			});

			expect(
				utils.parseReferencedEntityId(
					'123',
					referencedEntities,
					EntityType.Assets
				)
			).toBe('123_title');
		});
	});
});
