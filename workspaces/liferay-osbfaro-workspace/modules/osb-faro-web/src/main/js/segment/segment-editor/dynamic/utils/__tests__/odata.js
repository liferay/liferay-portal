import * as data from 'test/data';
import * as ODataUtil from '../odata';
import * as Utils from '../utils';
import {
	ALL_APPLICATION_IDS,
	ALL_EVENT_IDS,
	ATTRIBUTE_PROPERTY_PREFIX,
	CustomFunctionOperators,
	FunctionalOperators,
	RelationalOperators,
} from '../constants';
import {createCustomValueMap} from '../custom-inputs';
import {CustomValue} from 'shared/util/records';
import {List, Map} from 'immutable';

function testConversionToAndFrom(testQuery, queryConjunction) {
	const translatedMap = ODataUtil.translateQueryToCriteria(testQuery);

	const translatedString = ODataUtil.buildQueryString(
		[translatedMap],
		queryConjunction
	);

	expect(translatedString).toEqual(testQuery);
}

describe('odata', () => {
	beforeAll(() => {
		Utils.generateGroupId = jest.fn(() => 'group_01');
		Utils.generateRowId = jest.fn(() => 'row_01');
	});

	describe('convertBetweenToSubstring', () => {
		it('should convert between to substring', () => {
			expect(
				ODataUtil.convertBetweenToSubstring(
					"between(date,'2020-12-12','2020-12-16')"
				)
			).toEqual("substring(date,'2020-12-12','2020-12-16')");
		});

		it('should not convert between to substring if missing params', () => {
			expect(
				ODataUtil.convertBetweenToSubstring('between(date)')
			).toEqual('between(date)');
		});
	});

	describe('trimSpacesBeforeParams', () => {
		it('should trims spaces before params', () => {
			expect(
				ODataUtil.trimSpacesBeforeParams(
					"accounts.filterByCount(filter='activityKey eq ''Page#pageViewed#12341234''', operator='lt', value=2)"
				)
			).toEqual(
				"accounts.filterByCount(filter='activityKey eq ''Page#pageViewed#12341234''',operator='lt',value=2)"
			);
		});

		it('should return the oData string as is if no spaces exist before params', () => {
			const queryString =
				"accounts.filterByCount(filter='activityKey eq ''Page#pageViewed#12341234''',operator='lt',value=2)";

			expect(ODataUtil.trimSpacesBeforeParams(queryString)).toEqual(
				queryString
			);
		});
	});

	describe('buildQueryString', () => {
		it('should build a query string from a flat criteria map', () => {
			expect(
				ODataUtil.buildQueryString([data.mockNewCriteria(1)])
			).toEqual("(firstName eq 'test')");
			expect(
				ODataUtil.buildQueryString([data.mockNewCriteria(3)])
			).toEqual(
				"(firstName eq 'test' and firstName eq 'test' and firstName eq 'test')"
			);
		});

		it('should build a query string from a criteria map with nested items', () => {
			expect(
				ODataUtil.buildQueryString([data.mockNewCriteriaNested()])
			).toEqual(
				"((((firstName eq 'test' or firstName eq 'test') and firstName eq 'test') or firstName eq 'test') and firstName eq 'test')"
			);
		});

		it('should build a query string and only wrap strings in single quotes', () => {
			expect(
				ODataUtil.buildQueryString([data.mockNewCriteria(1)])
			).toEqual("(firstName eq 'test')");

			expect(
				ODataUtil.buildQueryString([
					data.mockNewCriteria(1, {value: null}),
				])
			).toEqual('(firstName eq null)');

			expect(
				ODataUtil.buildQueryString([
					data.mockNewCriteria(1, {value: 123}),
				])
			).toEqual('(firstName eq 123)');
		});

		describe('attribute placeholder exclusion', () => {
			const buildBehaviorValue = (attributeItem) =>
				createCustomValueMap([
					{
						key: 'criterionGroup',
						value: [
							{
								operatorName: RelationalOperators.EQ,
								propertyName: 'applicationId',
								value: 'Blog'
							},
							{
								operatorName: RelationalOperators.EQ,
								propertyName: 'eventId',
								value: 'blogViewed'
							},
							attributeItem,
							{
								operatorName: RelationalOperators.GT,
								propertyName: 'day',
								value: 'last24Hours'
							}
						]
					},
					{key: 'operator', value: RelationalOperators.GE},
					{key: 'value', value: 1}
				]);

			it('should not serialize the empty attribute/ placeholder', () => {
				const value = buildBehaviorValue({
					operatorName: FunctionalOperators.Contains,
					propertyName: ATTRIBUTE_PROPERTY_PREFIX,
					value: ''
				});

				expect(
					ODataUtil.buildQueryString([
						{
							operatorName:
								CustomFunctionOperators.ActivitiesFilterByCount,
							propertyName: 'applicationId',
							value
						}
					])
				).toEqual(
					"activities.filterByCount(filter='(applicationId eq ''Blog'' and eventId eq ''blogViewed'' and day gt ''last24Hours'')',operator='ge',value=1)"
				);
			});

			it('should still serialize a real attribute condition', () => {
				const value = buildBehaviorValue({
					operatorName: FunctionalOperators.Contains,
					propertyName: `${ATTRIBUTE_PROPERTY_PREFIX}61727469636c654964`,
					value: 'foo'
				});

				expect(
					ODataUtil.buildQueryString([
						{
							operatorName:
								CustomFunctionOperators.ActivitiesFilterByCount,
							propertyName: 'applicationId',
							value
						}
					])
				).toEqual(
					"activities.filterByCount(filter='(applicationId eq ''Blog'' and eventId eq ''blogViewed'' and contains(attribute/61727469636c654964, ''foo'') and day gt ''last24Hours'')',operator='ge',value=1)"
				);
			});
		});
	});

	describe('escapeSingleQuotes', () => {
		it('should escape all single quotes in a given string', () => {
			expect(ODataUtil.escapeSingleQuotes("o'high o'hara")).toEqual(
				"o''high o''hara"
			);
		});
	});

	describe('toCriteria', () => {
		it('should return a parsed criteria', () => {});
	});

	describe('translateQueryToCriteria', () => {
		it('should translate a query string into a criteria map', () => {
			expect(
				ODataUtil.translateQueryToCriteria("(firstName eq 'test')")
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'eq',
						propertyName: 'firstName',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: 'test',
					},
				],
			});
		});

		it('should handle a query string with a null value', () => {
			expect(
				ODataUtil.translateQueryToCriteria('(firstName eq null)')
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'eq',
						propertyName: 'firstName',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: null,
					},
				],
			});
		});

		it('should handle a query string with a number value', () => {
			expect(
				ODataUtil.translateQueryToCriteria('(firstName eq 123)')
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'eq',
						propertyName: 'firstName',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: 123,
					},
				],
			});
		});

		it('should handle a query string with empty groups', () => {
			expect(
				ODataUtil.translateQueryToCriteria("(((firstName eq 'test')))")
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'eq',
						propertyName: 'firstName',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: 'test',
					},
				],
			});
		});

		it('should handle a query string with "ne" operator', () => {
			expect(
				ODataUtil.translateQueryToCriteria("firstName ne 'test'")
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'ne',
						propertyName: 'firstName',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: 'test',
					},
				],
			});
		});

		it('should handle a query string with "contains" operator', () => {
			expect(
				ODataUtil.translateQueryToCriteria(
					"contains(firstName, 'test')"
				)
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'contains',
						propertyName: 'firstName',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: 'test',
					},
				],
			});
		});

		it('should return null if the query is empty or invalid', () => {
			expect(ODataUtil.translateQueryToCriteria()).toEqual(null);
			expect(ODataUtil.translateQueryToCriteria('()')).toEqual(null);
			expect(
				ODataUtil.translateQueryToCriteria(
					"(firstName eq 'test' eq 'test')"
				)
			).toEqual(null);
			expect(
				ODataUtil.translateQueryToCriteria("(firstName = 'test')")
			).toEqual(null);
		});

		it('should handle a query string with an activities-filter-by-count custom function', () => {
			expect(
				ODataUtil.translateQueryToCriteria(
					"activities.filterByCount(filter='(activityKey eq ''Page#pageViewed#348853654381438580'')',operator='lt',value=2)"
				)
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'activities-filter-by-count',
						propertyName: 'activityKey',
						rowId: 'row_01',
						touched: {
							asset: false,
							attribute: false,
							attributeValue: false,
							occurenceCount: false,
						},
						valid: {
							asset: true,
							attribute: true,
							attributeValue: true,
							occurenceCount: true,
						},
						value: new CustomValue({
							criterionGroup: new Map({
								conjunctionName: 'and',
								criteriaGroupId: 'group_01',
								items: new List([
									new Map({
										operatorName: 'eq',
										propertyName: 'activityKey',
										rowId: 'row_01',
										touched: false,
										valid: true,
										value: 'Page#pageViewed#348853654381438580',
									}),
								]),
							}),
							operator: 'lt',
							value: 2,
						}),
					},
				],
			});
		});

		it('should handle a query string with a not-activities-filter-by-count custom function', () => {
			expect(
				ODataUtil.translateQueryToCriteria(
					"not activities.filterByCount(filter='(activityKey eq ''Page#pageViewed#348853654381438580'')',operator='lt',value=2)"
				)
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'not-activities-filter-by-count',
						propertyName: 'activityKey',
						rowId: 'row_01',
						touched: {
							asset: false,
							attribute: false,
							attributeValue: false,
							occurenceCount: false,
						},
						valid: {
							asset: true,
							attribute: true,
							attributeValue: true,
							occurenceCount: true,
						},
						value: new CustomValue({
							criterionGroup: new Map({
								conjunctionName: 'and',
								criteriaGroupId: 'group_01',
								items: new List([
									new Map({
										operatorName: 'eq',
										propertyName: 'activityKey',
										rowId: 'row_01',
										touched: false,
										valid: true,
										value: 'Page#pageViewed#348853654381438580',
									}),
								]),
							}),
							operator: 'lt',
							value: 2,
						}),
					},
				],
			});
		});

		describe('attribute filter parsing (attribute/<id>)', () => {
			const getAttributeItem = (query) => {
				const result = ODataUtil.translateQueryToCriteria(query);

				return result.items[0].value
					.getIn(['criterionGroup', 'items'])
					.find((item) =>
						item.get('propertyName')?.startsWith('attribute/')
					)
					.toJS();
			};

			it('should parse a "contains" attribute condition', () => {
				expect(
					getAttributeItem(
						"activities.filterByCount(filter='(applicationId eq ''Document'' and eventId eq ''documentDownloaded'' and day gt ''last24Hours'' and contains(attribute/2, ''euroroad.com''))',operator='ge',value=1)"
					)
				).toEqual({
					operatorName: 'contains',
					propertyName: 'attribute/2',
					rowId: 'row_01',
					touched: false,
					valid: true,
					value: 'euroroad.com',
				});
			});

			it('should parse a "not contains" attribute condition', () => {
				expect(
					getAttributeItem(
						"activities.filterByCount(filter='(applicationId eq ''Document'' and eventId eq ''documentDownloaded'' and day gt ''last24Hours'' and (not contains(attribute/2, ''euroroad.com'')))',operator='ge',value=1)"
					)
				).toEqual({
					operatorName: 'not-contains',
					propertyName: 'attribute/2',
					rowId: 'row_01',
					touched: false,
					valid: true,
					value: 'euroroad.com',
				});
			});

			it('should parse a "between" attribute condition', () => {
				expect(
					getAttributeItem(
						"activities.filterByCount(filter='(applicationId eq ''Document'' and eventId eq ''documentDownloaded'' and day gt ''last24Hours'' and between(attribute/4,''2020-2-2'',''2020-2-3''))',operator='ge',value=1)"
					)
				).toEqual({
					operatorName: 'between',
					propertyName: 'attribute/4',
					rowId: 'row_01',
					touched: false,
					valid: true,
					value: {end: '2020-2-3', start: '2020-2-2'},
				});
			});

			it('should parse a relational attribute condition with a string value', () => {
				expect(
					getAttributeItem(
						"activities.filterByCount(filter='(applicationId eq ''Document'' and eventId eq ''documentDownloaded'' and day gt ''last24Hours'' and attribute/2 eq ''foo'')',operator='ge',value=1)"
					)
				).toEqual({
					operatorName: 'eq',
					propertyName: 'attribute/2',
					rowId: 'row_01',
					touched: false,
					valid: true,
					value: 'foo',
				});
			});

			it('should parse a relational attribute condition with a numeric value', () => {
				expect(
					getAttributeItem(
						"activities.filterByCount(filter='(applicationId eq ''Document'' and eventId eq ''documentDownloaded'' and day gt ''last24Hours'' and attribute/3 gt 100)',operator='ge',value=1)"
					)
				).toEqual({
					operatorName: 'gt',
					propertyName: 'attribute/3',
					rowId: 'row_01',
					touched: false,
					valid: true,
					value: 100,
				});
			});
		});

		it('should handle a query string with an accounts-filter-by-count custom function', () => {
			expect(
				ODataUtil.translateQueryToCriteria(
					"accounts.filterByCount(filter='(activityKey eq ''Page#pageViewed#348853654381438580'')',operator='lt',value=2)"
				)
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'accounts-filter-by-count',
						propertyName: 'activityKey',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: new CustomValue({
							criterionGroup: new Map({
								conjunctionName: 'and',
								criteriaGroupId: 'group_01',
								items: new List([
									new Map({
										operatorName: 'eq',
										propertyName: 'activityKey',
										rowId: 'row_01',
										touched: false,
										valid: true,
										value: 'Page#pageViewed#348853654381438580',
									}),
								]),
							}),
							operator: 'lt',
							value: 2,
						}),
					},
				],
			});
		});

		it('should handle a query string with a not-accounts-filter-by-count custom function', () => {
			expect(
				ODataUtil.translateQueryToCriteria(
					"not accounts.filterByCount(filter='(activityKey eq ''Page#pageViewed#348853654381438580'')',operator='lt',value=2)"
				)
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'not-accounts-filter-by-count',
						propertyName: 'activityKey',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: new CustomValue({
							criterionGroup: new Map({
								conjunctionName: 'and',
								criteriaGroupId: 'group_01',
								items: new List([
									new Map({
										operatorName: 'eq',
										propertyName: 'activityKey',
										rowId: 'row_01',
										touched: false,
										valid: true,
										value: 'Page#pageViewed#348853654381438580',
									}),
								]),
							}),
							operator: 'lt',
							value: 2,
						}),
					},
				],
			});
		});

		it('should handle a query string with an accounts-filter custom function', () => {
			expect(
				ODataUtil.translateQueryToCriteria(
					"accounts.filter(filter='(id eq ''48853654381438580'')')"
				)
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'accounts-filter',
						propertyName: 'id',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: new CustomValue({
							criterionGroup: new Map({
								conjunctionName: 'and',
								criteriaGroupId: 'group_01',
								items: new List([
									new Map({
										operatorName: 'eq',
										propertyName: 'id',
										rowId: 'row_01',
										touched: false,
										valid: true,
										value: '48853654381438580',
									}),
								]),
							}),
						}),
					},
				],
			});
		});

		it('should handle a query string with a not-accounts-filter custom function', () => {
			expect(
				ODataUtil.translateQueryToCriteria(
					"not (accounts.filter(filter='(id eq ''48853654381438580'')'))"
				)
			).toEqual({
				conjunctionName: 'and',
				criteriaGroupId: 'group_01',
				items: [
					{
						operatorName: 'not-accounts-filter',
						propertyName: 'id',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: new CustomValue({
							criterionGroup: new Map({
								conjunctionName: 'and',
								criteriaGroupId: 'group_01',
								items: new List([
									new Map({
										operatorName: 'eq',
										propertyName: 'id',
										rowId: 'row_01',
										touched: false,
										valid: true,
										value: '48853654381438580',
									}),
								]),
							}),
						}),
					},
				],
			});
		});
	});

	describe('conversion to and from', () => {
		it('should be able to translate a query string to map and back to string', () => {
			const testQuery = "(firstName eq 'test')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a complex query string to map and back to string', () => {
			const testQuery =
				"((firstName eq 'test' or firstName eq 'test') and firstName eq 'test')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "not" to map and back to string', () => {
			const testQuery = "((not contains(firstName, 'test')))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a complex query string with "not" to map and back to string', () => {
			const testQuery =
				"(firstName eq 'test' and ((not contains(lastName, 'foo')) or (not contains(lastName, 'bar'))))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "contains" to map and back to string', () => {
			const testQuery = "(contains(firstName, 'test'))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "not contains" to map and back to string', () => {
			const testQuery = "((not contains(firstName, 'test')))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "accounts.filterByCount" to map and back to string', () => {
			const testQuery =
				"(accounts.filterByCount(filter='(id eq ''48853654381438580'')',operator='lt',value='2'))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "not accounts.filterByCount" to map and back to string', () => {
			const testQuery =
				"((not accounts.filterByCount(filter='(id eq ''48853654381438580'')',operator='lt',value='2')))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "accounts.filter" to map and back to string', () => {
			const testQuery =
				"(accounts.filter(filter='(id eq ''48853654381438580'')'))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "not accounts.filter" to map and back to string', () => {
			const testQuery =
				"((not accounts.filter(filter='(activityKey eq ''Page#pageViewed#348853654381438580'')')))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "activities.filter" to map and back to string', () => {
			const testQuery =
				"(activities.filter(filter='(activityKey eq ''Page#pageViewed#348853654381438580'')'))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "not activities.filter" to map and back to string', () => {
			const testQuery =
				"((not activities.filter(filter='(activityKey eq ''Page#pageViewed#348853654381438580'')')))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "activities.filterByCount" to map and back to string', () => {
			const testQuery =
				"(activities.filterByCount(filter='(activityKey eq ''Page#pageViewed#348853654381438580'')',operator='lt',value='2'))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "not activities.filterByCount" to map and back to string', () => {
			const testQuery =
				"((not activities.filterByCount(filter='(activityKey eq ''Page#pageViewed#348853654381438580'')',operator='lt',value='2')))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a single-type behavior query (applicationId eq / eventId eq) to map and back to string', () => {
			const testQuery =
				"(activities.filterByCount(filter='(applicationId eq ''Document'' and eventId eq ''documentDownloaded'' and day gt ''last24Hours'')',operator='ge',value=1))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate an ObjectEntry behavior query with objectDefinitionName to map and back to string', () => {
			const testQuery =
				"(activities.filterByCount(filter='(applicationId eq ''ObjectEntry'' and eventId eq ''objectEntryImpressionMade'' and objectDefinitionName eq ''CMSBasicWebContent'' and day gt ''last24Hours'')',operator='ge',value=1))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a behavior query with an attribute "contains" condition to map and back to string', () => {
			const testQuery =
				"(activities.filterByCount(filter='(applicationId eq ''Document'' and eventId eq ''documentDownloaded'' and day gt ''last24Hours'' and contains(attribute/2, ''euroroad.com''))',operator='ge',value=1))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a behavior query with a "not contains" attribute condition to map and back to string', () => {
			const testQuery =
				"(activities.filterByCount(filter='(applicationId eq ''Document'' and eventId eq ''documentDownloaded'' and day gt ''last24Hours'' and (not contains(attribute/2, ''euroroad.com'')))',operator='ge',value=1))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a custom event query with an attribute "contains" condition to map and back to string', () => {
			const testQuery =
				"(events.filterByCount(filter='(eventDefinitionId eq ''1'' and contains(attribute/2, ''euroroad.com'') and day gt ''last24Hours'')',operator='ge',value=1))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with "between" to a map and back to a string', () => {
			const testQuery = "(between(date,'2020-2-2','2020-2-3'))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with a nested "between" to a map and back to a string', () => {
			const testQuery =
				"(accounts.filterByCount(filter='(activityKey eq ''Page#pageViewed#348853654381438580'' and between(date,''2020-1-4'',''2020-1-6''))',operator='lt',value=2))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string that contains a single quote', () => {
			const testQuery = "(firstName eq 'o''hara')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a nested query string that contains a single quote', () => {
			const testQuery =
				"(accounts.filter(filter='(organization/accountName/value ne ''o''''hara'')'))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with special characters', () => {
			const testQuery = "(firstName eq 'test+/?%#&')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a nested query string with special characters', () => {
			const testQuery =
				"(accounts.filter(filter='(organization/description/value ne ''test+/?%#&'')'))";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with brackets characters', () => {
			const testQuery = "(value eq '[A, B]')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with greater than / less than characters', () => {
			const testQuery = "(value eq '<A>, <B>')";

			testConversionToAndFrom(testQuery);
		});

		it.skip('should be able to translate a collection type query string with "contains" to map and back to string', () => {
			const testQuery =
				"(cookies/any(c:contains(c, 'keyTest=valueTest')))";

			testConversionToAndFrom(testQuery);
		});

		it.skip('should be able to translate a collection type query string with "not contains" to map and back to string', () => {
			const testQuery =
				"((not (cookies/any(c:contains(c, 'keyTest=valueTest')))))";

			testConversionToAndFrom(testQuery);
		});

		it.skip('should be able to translate a collection type query string with "eq" to map and back to string', () => {
			const testQuery = "(cookies/any(c:c eq 'keyTest=valueTest'))";

			testConversionToAndFrom(testQuery);
		});

		it.skip('should be able to translate a collection type query string with "not" to map and back to string', () => {
			const testQuery =
				"((not (cookies/any(c:c eq 'keyTest=valueTest'))))";

			testConversionToAndFrom(testQuery);
		});

		it.skip('should be able to translate a nested and complex collection type query string to map and back to string', () => {
			const testQuery =
				"((not (cookies/any(c:c eq 'keyTest1=valueTest1'))) and ((not (cookies/any(c:c eq 'keyTest2=valueTest2'))) or (cookies/any(c:c eq 'keyTest3=valueTest3') and cookies/any(c:c eq 'keyTest4=valueTest4'))) and name eq 'test')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with a backtick in the value to map and back to string', () => {
			const testQuery = "(givenName eq '`test`')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with a caret in the value to map and back to string', () => {
			const testQuery = "(givenName eq 'te^st')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with braces in the value to map and back to string', () => {
			const testQuery = "(givenName eq '{test}')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with a pipe in the value to map and back to string', () => {
			const testQuery = "(givenName eq 'te|st')";

			testConversionToAndFrom(testQuery);
		});

		it('should be able to translate a query string with a single quote in the value to map and back to string', () => {
			const testQuery = "(givenName eq 'O''Brien')";

			testConversionToAndFrom(testQuery);
		});
	});

	describe('vocabularies.filterByCount', () => {
		function encodeFilter(filterStr) {
			return filterStr.replace(/'/g, "''");
		}

		function buildVocabQuery(filterStr, operator = 'ge', count = 1) {
			return `(activities.filterByCount(filter='${encodeFilter(
				`(${filterStr})`
			)}',operator='${operator}',value=${count}))`;
		}

		function makeVocabFilterCriteria(innerItems) {
			return [
				{
					conjunctionName: 'and',
					criteriaGroupId: 'group_01',
					items: [
						{
							operatorName:
								CustomFunctionOperators.VocabulariesFilter,
							propertyName: 'vocab-id',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: new CustomValue({
								criterionGroup: new Map({
									conjunctionName: 'and',
									criteriaGroupId: 'group_01',
									items: new List(
										innerItems.map((item) => new Map(item))
									),
								}),
								operator: 'ge',
								value: 1,
							}),
						},
					],
				},
			];
		}

		const BASE_VOC_ITEMS = [
			{
				operatorName: 'eq',
				propertyName: 'vocabularies/id',
				rowId: 'row_01',
				touched: false,
				valid: true,
				value: 'vocab-id',
			},
			{
				operatorName: 'eq',
				propertyName: 'vocabularies/name',
				rowId: 'row_01',
				touched: false,
				valid: true,
				value: 'My Vocabulary',
			},
		];

		const DAY_ITEM = {
			operatorName: 'gt',
			propertyName: 'day',
			rowId: 'row_01',
			touched: false,
			valid: true,
			value: '2023-01-01',
		};

		it('should round-trip a vocabulary filter with specific asset type and no specific event', () => {
			testConversionToAndFrom(
				buildVocabQuery(
					"vocabularies/id eq 'vocab-id' and vocabularies/name eq 'My Vocabulary' and activityKey eq 'WebContent' and day gt '2023-01-01'"
				)
			);
		});

		it('should round-trip a vocabulary filter with specific asset type and specific event', () => {
			testConversionToAndFrom(
				buildVocabQuery(
					"vocabularies/id eq 'vocab-id' and vocabularies/name eq 'My Vocabulary' and activityKey eq 'WebContent#webContentViewed' and day gt '2023-01-01'"
				)
			);
		});

		it('should build a vocabulary filter query for any asset type using applicationId in and eventId in', () => {
			const appIds = ALL_APPLICATION_IDS.map((id) => `'${id}'`).join(',');
			const eventIds = ALL_EVENT_IDS.map((id) => `'${id}'`).join(',');

			expect(
				ODataUtil.buildQueryString(
					makeVocabFilterCriteria([
						...BASE_VOC_ITEMS,
						{
							operatorName: 'in',
							propertyName: 'applicationId',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: ALL_APPLICATION_IDS,
						},
						{
							operatorName: 'in',
							propertyName: 'eventId',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: ALL_EVENT_IDS,
						},
						DAY_ITEM,
					])
				)
			).toEqual(
				buildVocabQuery(
					`vocabularies/id eq 'vocab-id' and vocabularies/name eq 'My Vocabulary' and (applicationId in (${appIds}) and eventId in (${eventIds})) and day gt '2023-01-01'`
				)
			);
		});

		it('should build a vocabulary filter query with a single category', () => {
			expect(
				ODataUtil.buildQueryString(
					makeVocabFilterCriteria([
						...BASE_VOC_ITEMS,
						{
							operatorName: 'eq',
							propertyName: 'activityKey',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: 'WebContent',
						},
						{
							operatorName: 'in',
							propertyName: 'categories',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: [{id: 'cat-1', name: 'Category One'}],
						},
						DAY_ITEM,
					])
				)
			).toEqual(
				buildVocabQuery(
					"vocabularies/id eq 'vocab-id' and vocabularies/name eq 'My Vocabulary' and activityKey eq 'WebContent' and ((categories/id eq 'cat-1' and categories/name eq 'Category One')) and day gt '2023-01-01'"
				)
			);
		});

		it('should build a vocabulary filter query with multiple categories using OR grouping', () => {
			expect(
				ODataUtil.buildQueryString(
					makeVocabFilterCriteria([
						...BASE_VOC_ITEMS,
						{
							operatorName: 'eq',
							propertyName: 'activityKey',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: 'WebContent',
						},
						{
							operatorName: 'in',
							propertyName: 'categories',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: [
								{id: 'cat-1', name: 'Category One'},
								{id: 'cat-2', name: 'Category Two'},
							],
						},
						DAY_ITEM,
					])
				)
			).toEqual(
				buildVocabQuery(
					"vocabularies/id eq 'vocab-id' and vocabularies/name eq 'My Vocabulary' and activityKey eq 'WebContent' and ((categories/id eq 'cat-1' and categories/name eq 'Category One') or (categories/id eq 'cat-2' and categories/name eq 'Category Two')) and day gt '2023-01-01'"
				)
			);
		});

		it('should build a vocabulary filter query without categories', () => {
			const result = ODataUtil.buildQueryString(
				makeVocabFilterCriteria([
					...BASE_VOC_ITEMS,
					{
						operatorName: 'eq',
						propertyName: 'activityKey',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: 'WebContent',
					},
					DAY_ITEM,
				])
			);

			expect(result).toEqual(
				buildVocabQuery(
					"vocabularies/id eq 'vocab-id' and vocabularies/name eq 'My Vocabulary' and activityKey eq 'WebContent' and day gt '2023-01-01'"
				)
			);
			expect(result).not.toContain('categories');
		});
	});

	describe('tags.filterByCount', () => {
		function encodeFilter(filterStr) {
			return filterStr.replace(/'/g, "''");
		}

		function buildTagQuery(filterStr, operator = 'ge', count = 1) {
			return `(activities.filterByCount(filter='${encodeFilter(
				`(${filterStr})`
			)}',operator='${operator}',value=${count}))`;
		}

		function makeTagFilterCriteria(innerItems) {
			return [
				{
					conjunctionName: 'and',
					criteriaGroupId: 'group_01',
					items: [
						{
							operatorName: CustomFunctionOperators.TagsFilter,
							propertyName: 'tag-id',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: new CustomValue({
								criterionGroup: new Map({
									conjunctionName: 'and',
									criteriaGroupId: 'group_01',
									items: new List(
										innerItems.map((item) => new Map(item))
									),
								}),
								operator: 'ge',
								value: 1,
							}),
						},
					],
				},
			];
		}

		const BASE_TAG_ITEMS = [
			{
				operatorName: 'eq',
				propertyName: 'tags/id',
				rowId: 'row_01',
				touched: false,
				valid: true,
				value: 'tag-id',
			},
			{
				operatorName: 'eq',
				propertyName: 'tags/name',
				rowId: 'row_01',
				touched: false,
				valid: true,
				value: 'My Tag',
			},
		];

		const DAY_ITEM = {
			operatorName: 'gt',
			propertyName: 'day',
			rowId: 'row_01',
			touched: false,
			valid: true,
			value: '2023-01-01',
		};

		it('should round-trip a tag filter with specific asset type and no specific event', () => {
			testConversionToAndFrom(
				buildTagQuery(
					"tags/id eq 'tag-id' and tags/name eq 'My Tag' and activityKey eq 'WebContent' and day gt '2023-01-01'"
				)
			);
		});

		it('should round-trip a tag filter with specific asset type and specific event', () => {
			testConversionToAndFrom(
				buildTagQuery(
					"tags/id eq 'tag-id' and tags/name eq 'My Tag' and activityKey eq 'WebContent#webContentViewed' and day gt '2023-01-01'"
				)
			);
		});

		it('should build a tag filter query for any asset type using applicationId in and eventId in', () => {
			const appIds = ALL_APPLICATION_IDS.map((id) => `'${id}'`).join(',');
			const eventIds = ALL_EVENT_IDS.map((id) => `'${id}'`).join(',');

			expect(
				ODataUtil.buildQueryString(
					makeTagFilterCriteria([
						...BASE_TAG_ITEMS,
						{
							operatorName: 'in',
							propertyName: 'applicationId',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: ALL_APPLICATION_IDS,
						},
						{
							operatorName: 'in',
							propertyName: 'eventId',
							rowId: 'row_01',
							touched: false,
							valid: true,
							value: ALL_EVENT_IDS,
						},
						DAY_ITEM,
					])
				)
			).toEqual(
				buildTagQuery(
					`tags/id eq 'tag-id' and tags/name eq 'My Tag' and (applicationId in (${appIds}) and eventId in (${eventIds})) and day gt '2023-01-01'`
				)
			);
		});

		it('should build a tag filter query without categories even when a categories item is present', () => {
			const result = ODataUtil.buildQueryString(
				makeTagFilterCriteria([
					...BASE_TAG_ITEMS,
					{
						operatorName: 'eq',
						propertyName: 'activityKey',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: 'WebContent',
					},
					{
						operatorName: 'in',
						propertyName: 'categories',
						rowId: 'row_01',
						touched: false,
						valid: true,
						value: [{id: 'cat-1', name: 'Category One'}],
					},
					DAY_ITEM,
				])
			);

			expect(result).toEqual(
				buildTagQuery(
					"tags/id eq 'tag-id' and tags/name eq 'My Tag' and activityKey eq 'WebContent' and day gt '2023-01-01'"
				)
			);
			expect(result).not.toContain('categories');
		});
	});
});
