/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.custom.field.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.expando.kernel.exception.NoSuchValueException;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.rule.LazyReferencing;
import com.liferay.exportimport.test.rule.LazyReferencingTestRule;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.custom.field.CustomField;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.custom.field.CustomValue;
import com.liferay.portal.vulcan.custom.field.Geo;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class CustomFieldsUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			LazyReferencingTestRule.INSTANCE, new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_initialExpandoColumnsCount =
			_expandoColumnLocalService.getColumnsCount(
				TestPropsValues.getCompanyId(),
				_classNameLocalService.getClassNameId(_clazz), "CUSTOM_FIELDS");

		_expandoTable = ExpandoTestUtil.addTable(
			_classNameLocalService.getClassNameId(_clazz), "CUSTOM_FIELDS");

		_expandoColumn1 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.BOOLEAN);
		_expandoColumn2 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.BOOLEAN_ARRAY);
		_expandoColumn3 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.DATE);
		_expandoColumn4 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.DATE_ARRAY);
		_expandoColumn5 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.DOUBLE);
		_expandoColumn6 = _addExpandoColumn(
			new double[] {
				_DATA_DOUBLE, RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble()
			},
			null, _expandoTable, ExpandoColumnConstants.DOUBLE_ARRAY);
		_expandoColumn7 = _addExpandoColumn(
			new double[] {
				_DATA_DOUBLE, RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble()
			},
			ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO, _expandoTable,
			ExpandoColumnConstants.DOUBLE_ARRAY);
		_expandoColumn8 = _addExpandoColumn(
			new double[] {
				_DATA_DOUBLE, RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble()
			},
			ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST,
			_expandoTable, ExpandoColumnConstants.DOUBLE_ARRAY);
		_expandoColumn9 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.FLOAT);
		_expandoColumn10 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.FLOAT_ARRAY);
		_expandoColumn11 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.GEOLOCATION);
		_expandoColumn12 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.INTEGER);
		_expandoColumn13 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.INTEGER_ARRAY);
		_expandoColumn14 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.LONG);
		_expandoColumn15 = _addExpandoColumn(
			new long[] {
				_DATA_LONG, RandomTestUtil.randomLong(),
				RandomTestUtil.randomLong()
			},
			null, _expandoTable, ExpandoColumnConstants.LONG_ARRAY);
		_expandoColumn16 = _addExpandoColumn(
			new long[] {
				_DATA_LONG, RandomTestUtil.randomLong(),
				RandomTestUtil.randomLong()
			},
			ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO, _expandoTable,
			ExpandoColumnConstants.LONG_ARRAY);
		_expandoColumn17 = _addExpandoColumn(
			new long[] {
				_DATA_LONG, RandomTestUtil.randomLong(),
				RandomTestUtil.randomLong()
			},
			ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST,
			_expandoTable, ExpandoColumnConstants.LONG_ARRAY);
		_expandoColumn18 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.NUMBER);
		_expandoColumn19 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.NUMBER_ARRAY);
		_expandoColumn20 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.SHORT);
		_expandoColumn21 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.SHORT_ARRAY);
		_expandoColumn22 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.STRING);
		_expandoColumn23 = _addExpandoColumn(
			new String[] {
				_DATA_STRING, RandomTestUtil.randomString(),
				RandomTestUtil.randomString()
			},
			null, _expandoTable, ExpandoColumnConstants.STRING_ARRAY);
		_expandoColumn24 = _addExpandoColumn(
			new String[] {
				_DATA_STRING, RandomTestUtil.randomString(),
				RandomTestUtil.randomString()
			},
			ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO, _expandoTable,
			ExpandoColumnConstants.STRING_ARRAY);
		_expandoColumn25 = _addExpandoColumn(
			new String[] {
				_DATA_STRING, RandomTestUtil.randomString(),
				RandomTestUtil.randomString()
			},
			ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST,
			_expandoTable, ExpandoColumnConstants.STRING_ARRAY);
		_expandoColumn26 = _addExpandoColumn(
			null, null, _expandoTable,
			ExpandoColumnConstants.STRING_ARRAY_LOCALIZED);
		_expandoColumn27 = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.STRING_LOCALIZED);

		_user = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, Arrays.asList("customFields.attributeType")));
	}

	@Test
	public void testToCustomFieldsCustomFieldValues() throws Exception {
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn1, _user.getPrimaryKey(), true);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn2, _user.getPrimaryKey(),
			new boolean[] {true});

		Date randomDate = RandomTestUtil.nextDate();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn3, _user.getPrimaryKey(), randomDate);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn4, _user.getPrimaryKey(),
			new Date[] {randomDate});

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn5, _user.getPrimaryKey(),
			_DATA_DOUBLE);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn6, _user.getPrimaryKey(),
			new double[] {_DATA_DOUBLE});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn7, _user.getPrimaryKey(),
			new double[] {_DATA_DOUBLE});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn8, _user.getPrimaryKey(),
			new double[] {_DATA_DOUBLE});

		float randomFloat = RandomTestUtil.randomFloat();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn9, _user.getPrimaryKey(), randomFloat);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn10, _user.getPrimaryKey(),
			new float[] {randomFloat});

		double randomDouble1 = RandomTestUtil.randomDouble();
		double randomDouble2 = RandomTestUtil.randomDouble();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn11, _user.getPrimaryKey(),
			JSONUtil.put(
				"latitude", randomDouble1
			).put(
				"longitude", randomDouble2
			).toString());

		int randomInt = RandomTestUtil.randomInt();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn12, _user.getPrimaryKey(), randomInt);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn13, _user.getPrimaryKey(),
			new int[] {randomInt});

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn14, _user.getPrimaryKey(), _DATA_LONG);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn15, _user.getPrimaryKey(),
			new long[] {_DATA_LONG});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn16, _user.getPrimaryKey(),
			new long[] {_DATA_LONG});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn17, _user.getPrimaryKey(),
			new long[] {_DATA_LONG});

		Number randomNumber = RandomTestUtil.randomInt();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn18, _user.getPrimaryKey(),
			randomNumber);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn19, _user.getPrimaryKey(),
			new Number[] {randomNumber});

		short randomShort = (short)RandomTestUtil.randomInt(
			Short.MIN_VALUE, Short.MAX_VALUE);

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn20, _user.getPrimaryKey(),
			randomShort);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn21, _user.getPrimaryKey(),
			new short[] {randomShort});

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn22, _user.getPrimaryKey(),
			_DATA_STRING);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn23, _user.getPrimaryKey(),
			new String[] {_DATA_STRING});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn24, _user.getPrimaryKey(),
			new String[] {_DATA_STRING});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn25, _user.getPrimaryKey(),
			new String[] {_DATA_STRING});

		String randomString = RandomTestUtil.randomString();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn26, _user.getPrimaryKey(),
			HashMapBuilder.put(
				_enLocale, new String[] {_DATA_STRING}
			).put(
				_frLocale, new String[] {randomString}
			).put(
				_ptLocale, new String[] {randomString}
			).build());
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn27, _user.getPrimaryKey(),
			HashMapBuilder.put(
				_enLocale, _DATA_STRING
			).put(
				_frLocale, randomString
			).put(
				_ptLocale, randomString
			).build());

		CustomField[] customFields = CustomFieldsUtil.toCustomFields(
			true, _clazz.getName(), _user.getPrimaryKey(),
			TestPropsValues.getCompanyId(), LocaleUtil.getDefault());

		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.BOOLEAN;
					customValue = new CustomValue() {
						{
							data = true;
						}
					};
					dataType = "";
					name = _expandoColumn1.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn1.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.BOOLEAN_ARRAY;
					customValue = new CustomValue() {
						{
							data = new boolean[] {true};
						}
					};
					dataType = "";
					name = _expandoColumn2.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn2.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DATE;
					customValue = new CustomValue() {
						{
							data = _dateFormat.format(randomDate);
						}
					};
					dataType = "";
					name = _expandoColumn3.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn3.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DATE_ARRAY;
					customValue = new CustomValue() {
						{
							data = new Date[] {randomDate};
						}
					};
					dataType = "";
					name = _expandoColumn4.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn4.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DOUBLE;
					customValue = new CustomValue() {
						{
							data = _DATA_DOUBLE;
						}
					};
					dataType = "Decimal";
					name = _expandoColumn5.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn5.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DOUBLE_ARRAY;
					customValue = new CustomValue() {
						{
							data = new double[] {_DATA_DOUBLE};
						}
					};
					dataType = "Decimal";
					name = _expandoColumn6.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn6.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DOUBLE_ARRAY;
					customValue = new CustomValue() {
						{
							data = new double[] {_DATA_DOUBLE};
						}
					};
					dataType = "Decimal";
					name = _expandoColumn7.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn7.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DOUBLE_ARRAY;
					customValue = new CustomValue() {
						{
							data = new double[] {_DATA_DOUBLE};
						}
					};
					dataType = "Decimal";
					name = _expandoColumn8.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn8.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.FLOAT;
					customValue = new CustomValue() {
						{
							data = randomFloat;
						}
					};
					dataType = "Decimal";
					name = _expandoColumn9.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn9.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.FLOAT_ARRAY;
					customValue = new CustomValue() {
						{
							data = new float[] {randomFloat};
						}
					};
					dataType = "Decimal";
					name = _expandoColumn10.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn10.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.GEOLOCATION;
					customValue = new CustomValue() {
						{
							geo = new Geo() {
								{
									latitude = randomDouble1;
									longitude = randomDouble2;
								}
							};
						}
					};
					dataType = "Geolocation";
					name = _expandoColumn11.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn11.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.INTEGER;
					customValue = new CustomValue() {
						{
							data = randomInt;
						}
					};
					dataType = "Integer";
					name = _expandoColumn12.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn12.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.INTEGER_ARRAY;
					customValue = new CustomValue() {
						{
							data = new int[] {randomInt};
						}
					};
					dataType = "Integer";
					name = _expandoColumn13.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn13.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.LONG;
					customValue = new CustomValue() {
						{
							data = _DATA_LONG;
						}
					};
					dataType = "Integer";
					name = _expandoColumn14.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn14.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.LONG_ARRAY;
					customValue = new CustomValue() {
						{
							data = new long[] {_DATA_LONG};
						}
					};
					dataType = "Integer";
					name = _expandoColumn15.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn15.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.LONG_ARRAY;
					customValue = new CustomValue() {
						{
							data = new long[] {_DATA_LONG};
						}
					};
					dataType = "Integer";
					name = _expandoColumn16.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn16.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.LONG_ARRAY;
					customValue = new CustomValue() {
						{
							data = new long[] {_DATA_LONG};
						}
					};
					dataType = "Integer";
					name = _expandoColumn17.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn17.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.NUMBER;
					customValue = new CustomValue() {
						{
							data = new BigDecimal(randomNumber.intValue());
						}
					};
					dataType = "";
					name = _expandoColumn18.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn18.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.NUMBER_ARRAY;
					customValue = new CustomValue() {
						{
							data = new Number[] {
								new BigDecimal(randomNumber.intValue())
							};
						}
					};
					dataType = "";
					name = _expandoColumn19.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn19.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.SHORT;
					customValue = new CustomValue() {
						{
							data = randomShort;
						}
					};
					dataType = "Integer";
					name = _expandoColumn20.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn20.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.SHORT_ARRAY;
					customValue = new CustomValue() {
						{
							data = new short[] {randomShort};
						}
					};
					dataType = "Integer";
					name = _expandoColumn21.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn21.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING;
					customValue = new CustomValue() {
						{
							data = _DATA_STRING;
						}
					};
					dataType = "Text";
					name = _expandoColumn22.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn22.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_ARRAY;
					customValue = new CustomValue() {
						{
							data = new String[] {_DATA_STRING};
						}
					};
					dataType = "Text";
					name = _expandoColumn23.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn23.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_ARRAY;
					customValue = new CustomValue() {
						{
							data = new String[] {_DATA_STRING};
						}
					};
					dataType = "Text";
					name = _expandoColumn24.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn24.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_ARRAY;
					customValue = new CustomValue() {
						{
							data = new String[] {_DATA_STRING};
						}
					};
					dataType = "Text";
					name = _expandoColumn25.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn25.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_ARRAY_LOCALIZED;
					customValue = new CustomValue() {
						{
							data = HashMapBuilder.put(
								_enLocale, new String[] {_DATA_STRING}
							).put(
								_frLocale, new String[] {randomString}
							).put(
								_ptLocale, new String[] {randomString}
							).build();
						}
					};
					dataType = "";
					name = _expandoColumn26.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn26.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_LOCALIZED;
					customValue = new CustomValue() {
						{
							data = _DATA_STRING;
							data_i18n = HashMapBuilder.put(
								"en-US", _DATA_STRING
							).put(
								"fr-FR", randomString
							).put(
								"pt-BR", randomString
							).build();
						}
					};
					dataType = "Text";
					name = _expandoColumn27.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn27.getName()));

		// Without nested fields

		NestedFieldsContextThreadLocal.setNestedFieldsContext(null);

		customFields = CustomFieldsUtil.toCustomFields(
			true, _clazz.getName(), _user.getPrimaryKey(),
			TestPropsValues.getCompanyId(), LocaleUtil.getDefault());

		_assertNullAttributeType(customFields, _expandoColumn1.getName());
		_assertNullAttributeType(customFields, _expandoColumn2.getName());
		_assertNullAttributeType(customFields, _expandoColumn3.getName());
		_assertNullAttributeType(customFields, _expandoColumn4.getName());
		_assertNullAttributeType(customFields, _expandoColumn5.getName());
		_assertNullAttributeType(customFields, _expandoColumn6.getName());
		_assertNullAttributeType(customFields, _expandoColumn7.getName());
		_assertNullAttributeType(customFields, _expandoColumn8.getName());
		_assertNullAttributeType(customFields, _expandoColumn9.getName());
		_assertNullAttributeType(customFields, _expandoColumn10.getName());
		_assertNullAttributeType(customFields, _expandoColumn11.getName());
		_assertNullAttributeType(customFields, _expandoColumn12.getName());
		_assertNullAttributeType(customFields, _expandoColumn13.getName());
		_assertNullAttributeType(customFields, _expandoColumn14.getName());
		_assertNullAttributeType(customFields, _expandoColumn15.getName());
		_assertNullAttributeType(customFields, _expandoColumn16.getName());
		_assertNullAttributeType(customFields, _expandoColumn17.getName());
		_assertNullAttributeType(customFields, _expandoColumn18.getName());
		_assertNullAttributeType(customFields, _expandoColumn19.getName());
		_assertNullAttributeType(customFields, _expandoColumn20.getName());
		_assertNullAttributeType(customFields, _expandoColumn21.getName());
		_assertNullAttributeType(customFields, _expandoColumn22.getName());
		_assertNullAttributeType(customFields, _expandoColumn23.getName());
		_assertNullAttributeType(customFields, _expandoColumn24.getName());
		_assertNullAttributeType(customFields, _expandoColumn25.getName());
		_assertNullAttributeType(customFields, _expandoColumn26.getName());
		_assertNullAttributeType(customFields, _expandoColumn27.getName());
	}

	@Test
	public void testToCustomFieldsDefaultCustomFieldValues() throws Exception {
		CustomField[] customFields = CustomFieldsUtil.toCustomFields(
			true, _clazz.getName(), _user.getPrimaryKey(),
			TestPropsValues.getCompanyId(), LocaleUtil.getDefault());

		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.BOOLEAN;
					customValue = new CustomValue() {
						{
							data = false;
						}
					};
					dataType = "";
					name = _expandoColumn1.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn1.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.BOOLEAN_ARRAY;
					customValue = new CustomValue() {
						{
							data = new boolean[0];
						}
					};
					dataType = "";
					name = _expandoColumn2.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn2.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DATE;
					customValue = new CustomValue() {
						{
							data = "1970-01-01T00:00:00Z";
						}
					};
					dataType = "";
					name = _expandoColumn3.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn3.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DATE_ARRAY;
					customValue = new CustomValue() {
						{
							data = new String[0];
						}
					};
					dataType = "";
					name = _expandoColumn4.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn4.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DOUBLE;
					customValue = new CustomValue() {
						{
							data = 0.0;
						}
					};
					dataType = "Decimal";
					name = _expandoColumn5.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn5.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DOUBLE_ARRAY;
					customValue = new CustomValue() {
						{
							data = new double[] {0.0};
						}
					};
					dataType = "Decimal";
					name = _expandoColumn6.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn6.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DOUBLE_ARRAY;
					customValue = new CustomValue() {
						{
							data = new double[] {0.0};
						}
					};
					dataType = "Decimal";
					name = _expandoColumn7.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn7.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.DOUBLE_ARRAY;
					customValue = new CustomValue() {
						{
							data = new double[] {_DATA_DOUBLE};
						}
					};
					dataType = "Decimal";
					name = _expandoColumn8.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn8.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.FLOAT;
					customValue = new CustomValue() {
						{
							data = (float)0.0;
						}
					};
					dataType = "Decimal";
					name = _expandoColumn9.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn9.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.FLOAT_ARRAY;
					customValue = new CustomValue() {
						{
							data = new float[0];
						}
					};
					dataType = "Decimal";
					name = _expandoColumn10.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn10.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.GEOLOCATION;
					customValue = new CustomValue() {
						{
							geo = new Geo() {
								{
									latitude = Double.NaN;
									longitude = Double.NaN;
								}
							};
						}
					};
					dataType = "Geolocation";
					name = _expandoColumn11.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn11.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.INTEGER;
					customValue = new CustomValue() {
						{
							data = 0;
						}
					};
					dataType = "Integer";
					name = _expandoColumn12.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn12.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.INTEGER_ARRAY;
					customValue = new CustomValue() {
						{
							data = new int[0];
						}
					};
					dataType = "Integer";
					name = _expandoColumn13.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn13.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.LONG;
					customValue = new CustomValue() {
						{
							data = 0L;
						}
					};
					dataType = "Integer";
					name = _expandoColumn14.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn14.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.LONG_ARRAY;
					customValue = new CustomValue() {
						{
							data = new long[] {0L};
						}
					};
					dataType = "Integer";
					name = _expandoColumn15.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn15.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.LONG_ARRAY;
					customValue = new CustomValue() {
						{
							data = new long[] {0L};
						}
					};
					dataType = "Integer";
					name = _expandoColumn16.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn16.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.LONG_ARRAY;
					customValue = new CustomValue() {
						{
							data = new long[] {_DATA_LONG};
						}
					};
					dataType = "Integer";
					name = _expandoColumn17.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn17.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.NUMBER;
					customValue = new CustomValue() {
						{
							data = 0;
						}
					};
					dataType = "";
					name = _expandoColumn18.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn18.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.NUMBER_ARRAY;
					customValue = new CustomValue() {
						{
							data = new Number[0];
						}
					};
					dataType = "";
					name = _expandoColumn19.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn19.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.SHORT;
					customValue = new CustomValue() {
						{
							data = (short)0;
						}
					};
					dataType = "Integer";
					name = _expandoColumn20.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn20.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.SHORT_ARRAY;
					customValue = new CustomValue() {
						{
							data = new short[0];
						}
					};
					dataType = "Integer";
					name = _expandoColumn21.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn21.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING;
					customValue = new CustomValue() {
						{
							data = "";
						}
					};
					dataType = "Text";
					name = _expandoColumn22.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn22.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_ARRAY;
					customValue = new CustomValue() {
						{
							data = new String[] {"false"};
						}
					};
					dataType = "Text";
					name = _expandoColumn23.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn23.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_ARRAY;
					customValue = new CustomValue() {
						{
							data = new String[] {"false"};
						}
					};
					dataType = "Text";
					name = _expandoColumn24.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn24.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_ARRAY;
					customValue = new CustomValue() {
						{
							data = new String[] {_DATA_STRING};
						}
					};
					dataType = "Text";
					name = _expandoColumn25.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn25.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_ARRAY_LOCALIZED;
					customValue = new CustomValue() {
						{
							data = new HashMap<>();
						}
					};
					dataType = "";
					name = _expandoColumn26.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn26.getName()));
		_assertEquals(
			new CustomField() {
				{
					attributeType = AttributeType.STRING_LOCALIZED;
					customValue = new CustomValue() {
						{
							data_i18n = new HashMap<>();
						}
					};
					dataType = "Text";
					name = _expandoColumn27.getName();
				}
			},
			_getCustomField(customFields, _expandoColumn27.getName()));
	}

	@Test
	public void testToCustomFieldsMissingCustomFields() throws Exception {

		// Random class name

		Assert.assertEquals(
			new CustomField[0],
			CustomFieldsUtil.toCustomFields(
				true, RandomTestUtil.randomString(),
				RandomTestUtil.randomLong(), TestPropsValues.getCompanyId(),
				LocaleUtil.getDefault()));

		// Random primary key

		Assert.assertEquals(
			new CustomField[0],
			CustomFieldsUtil.toCustomFields(
				true, DLFileEntry.class.getName(), RandomTestUtil.randomLong(),
				TestPropsValues.getCompanyId(), LocaleUtil.getDefault()));
	}

	@Test
	public void testToMapCustomFieldValues() throws Exception {
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn1, _user.getPrimaryKey(), true);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn2, _user.getPrimaryKey(),
			new boolean[] {true});

		Date randomDate = RandomTestUtil.nextDate();

		randomDate = new Date((randomDate.getTime() / 1000) * 1000);

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn3, _user.getPrimaryKey(), randomDate);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn4, _user.getPrimaryKey(),
			new Date[] {randomDate});

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn5, _user.getPrimaryKey(),
			_DATA_DOUBLE);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn6, _user.getPrimaryKey(),
			new double[] {_DATA_DOUBLE});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn7, _user.getPrimaryKey(),
			new double[] {_DATA_DOUBLE});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn8, _user.getPrimaryKey(),
			new double[] {_DATA_DOUBLE});

		float randomFloat = RandomTestUtil.randomFloat();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn9, _user.getPrimaryKey(), randomFloat);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn10, _user.getPrimaryKey(),
			new float[] {randomFloat});

		double randomDouble1 = RandomTestUtil.randomDouble();
		double randomDouble2 = RandomTestUtil.randomDouble();

		JSONObject jsonObject = JSONUtil.put(
			"latitude", randomDouble1
		).put(
			"longitude", randomDouble2
		);

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn11, _user.getPrimaryKey(),
			jsonObject.toString());

		int randomInt = RandomTestUtil.randomInt();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn12, _user.getPrimaryKey(), randomInt);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn13, _user.getPrimaryKey(),
			new int[] {randomInt});

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn14, _user.getPrimaryKey(), _DATA_LONG);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn15, _user.getPrimaryKey(),
			new long[] {_DATA_LONG});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn16, _user.getPrimaryKey(),
			new long[] {_DATA_LONG});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn17, _user.getPrimaryKey(),
			new long[] {_DATA_LONG});

		Number randomNumber = RandomTestUtil.randomInt();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn18, _user.getPrimaryKey(),
			randomNumber);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn19, _user.getPrimaryKey(),
			new Number[] {randomNumber});

		short randomShort = (short)RandomTestUtil.randomInt(
			Short.MIN_VALUE, Short.MAX_VALUE);

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn20, _user.getPrimaryKey(),
			randomShort);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn21, _user.getPrimaryKey(),
			new short[] {randomShort});

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn22, _user.getPrimaryKey(),
			_DATA_STRING);
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn23, _user.getPrimaryKey(),
			new String[] {_DATA_STRING});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn24, _user.getPrimaryKey(),
			new String[] {_DATA_STRING});
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn25, _user.getPrimaryKey(),
			new String[] {_DATA_STRING});

		String randomString = RandomTestUtil.randomString();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn26, _user.getPrimaryKey(),
			HashMapBuilder.put(
				_enLocale, new String[] {_DATA_STRING}
			).put(
				_frLocale, new String[] {randomString}
			).put(
				_ptLocale, new String[] {randomString}
			).build());
		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn27, _user.getPrimaryKey(),
			HashMapBuilder.put(
				_enLocale, _DATA_STRING
			).put(
				_frLocale, randomString
			).put(
				_ptLocale, randomString
			).build());

		Map<String, Serializable> map = CustomFieldsUtil.toMap(
			_clazz.getName(), TestPropsValues.getCompanyId(),
			CustomFieldsUtil.toCustomFields(
				true, _clazz.getName(), _user.getPrimaryKey(),
				TestPropsValues.getCompanyId(), LocaleUtil.getDefault()),
			LocaleUtil.getDefault());

		Assert.assertTrue((boolean)map.get(_expandoColumn1.getName()));
		Assert.assertArrayEquals(
			new boolean[] {true},
			(boolean[])map.get(_expandoColumn2.getName()));
		Assert.assertTrue(
			DateUtil.equals(
				randomDate, (Date)map.get(_expandoColumn3.getName())));
		Assert.assertArrayEquals(
			new Date[] {randomDate},
			(Date[])map.get(_expandoColumn4.getName()));
		Assert.assertEquals(_DATA_DOUBLE, map.get(_expandoColumn5.getName()));
		Assert.assertArrayEquals(
			new double[] {_DATA_DOUBLE},
			(double[])map.get(_expandoColumn6.getName()), 0);
		Assert.assertArrayEquals(
			new double[] {_DATA_DOUBLE},
			(double[])map.get(_expandoColumn7.getName()), 0);
		Assert.assertArrayEquals(
			new double[] {_DATA_DOUBLE},
			(double[])map.get(_expandoColumn8.getName()), 0);
		Assert.assertEquals(randomFloat, map.get(_expandoColumn9.getName()));
		Assert.assertArrayEquals(
			new float[] {randomFloat},
			(float[])map.get(_expandoColumn10.getName()), 0);
		Assert.assertEquals(
			jsonObject.toString(), map.get(_expandoColumn11.getName()));
		Assert.assertEquals(randomInt, map.get(_expandoColumn12.getName()));
		Assert.assertArrayEquals(
			new int[] {randomInt}, (int[])map.get(_expandoColumn13.getName()));
		Assert.assertEquals(_DATA_LONG, map.get(_expandoColumn14.getName()));
		Assert.assertArrayEquals(
			new long[] {_DATA_LONG},
			(long[])map.get(_expandoColumn15.getName()));
		Assert.assertArrayEquals(
			new long[] {_DATA_LONG},
			(long[])map.get(_expandoColumn16.getName()));
		Assert.assertArrayEquals(
			new long[] {_DATA_LONG},
			(long[])map.get(_expandoColumn17.getName()));
		Assert.assertEquals(
			new BigDecimal(randomNumber.intValue()),
			map.get(_expandoColumn18.getName()));
		Assert.assertArrayEquals(
			new Number[] {new BigDecimal(randomNumber.intValue())},
			(Number[])map.get(_expandoColumn19.getName()));
		Assert.assertEquals(randomShort, map.get(_expandoColumn20.getName()));
		Assert.assertArrayEquals(
			new short[] {randomShort},
			(short[])map.get(_expandoColumn21.getName()));
		Assert.assertEquals(_DATA_STRING, map.get(_expandoColumn22.getName()));
		Assert.assertArrayEquals(
			new String[] {_DATA_STRING},
			(String[])map.get(_expandoColumn23.getName()));
		Assert.assertArrayEquals(
			new String[] {_DATA_STRING},
			(String[])map.get(_expandoColumn24.getName()));
		Assert.assertArrayEquals(
			new String[] {_DATA_STRING},
			(String[])map.get(_expandoColumn25.getName()));
		AssertUtils.assertEquals(
			HashMapBuilder.put(
				_enLocale, new String[] {_DATA_STRING}
			).put(
				_frLocale, new String[] {randomString}
			).put(
				_ptLocale, new String[] {randomString}
			).build(),
			(Map)map.get(_expandoColumn26.getName()));
		AssertUtils.assertEquals(
			HashMapBuilder.put(
				_enLocale, _DATA_STRING
			).put(
				_frLocale, randomString
			).put(
				_ptLocale, randomString
			).build(),
			(Map)map.get(_expandoColumn27.getName()));
		Assert.assertEquals(
			map.toString(), _initialExpandoColumnsCount + 27, map.size());
	}

	@Test
	public void testToMapDefaultCustomFieldValues() throws Exception {
		Map<String, Serializable> map = CustomFieldsUtil.toMap(
			_clazz.getName(), TestPropsValues.getCompanyId(),
			CustomFieldsUtil.toCustomFields(
				true, _clazz.getName(), RandomTestUtil.randomLong(),
				TestPropsValues.getCompanyId(), LocaleUtil.getDefault()),
			LocaleUtil.getDefault());

		Assert.assertFalse((boolean)map.get(_expandoColumn1.getName()));
		Assert.assertArrayEquals(
			new boolean[0], (boolean[])map.get(_expandoColumn2.getName()));
		Assert.assertEquals(new Date(0), map.get(_expandoColumn3.getName()));
		Assert.assertArrayEquals(
			new Date[0], (Date[])map.get(_expandoColumn4.getName()));
		Assert.assertEquals(0.0, map.get(_expandoColumn5.getName()));
		Assert.assertArrayEquals(
			new double[] {0.0}, (double[])map.get(_expandoColumn6.getName()),
			0);
		Assert.assertArrayEquals(
			new double[] {0.0}, (double[])map.get(_expandoColumn7.getName()),
			0);
		Assert.assertArrayEquals(
			new double[] {_DATA_DOUBLE},
			(double[])map.get(_expandoColumn8.getName()), 0);
		Assert.assertEquals((float)0.0, map.get(_expandoColumn9.getName()));
		Assert.assertArrayEquals(
			new float[0], (float[])map.get(_expandoColumn10.getName()), 0);
		Assert.assertEquals("{}", map.get(_expandoColumn11.getName()));
		Assert.assertEquals(0, map.get(_expandoColumn12.getName()));
		Assert.assertArrayEquals(
			new int[0], (int[])map.get(_expandoColumn13.getName()));
		Assert.assertEquals(0L, map.get(_expandoColumn14.getName()));
		Assert.assertArrayEquals(
			new long[] {0L}, (long[])map.get(_expandoColumn15.getName()));
		Assert.assertArrayEquals(
			new long[] {0L}, (long[])map.get(_expandoColumn16.getName()));
		Assert.assertArrayEquals(
			new long[] {_DATA_LONG},
			(long[])map.get(_expandoColumn17.getName()));
		Assert.assertEquals(0, map.get(_expandoColumn18.getName()));
		Assert.assertArrayEquals(
			new Number[0], (Number[])map.get(_expandoColumn19.getName()));
		Assert.assertEquals((short)0, map.get(_expandoColumn20.getName()));
		Assert.assertArrayEquals(
			new short[0], (short[])map.get(_expandoColumn21.getName()));
		Assert.assertEquals("", map.get(_expandoColumn22.getName()));
		Assert.assertArrayEquals(
			new String[] {"false"},
			(String[])map.get(_expandoColumn23.getName()));
		Assert.assertArrayEquals(
			new String[] {"false"},
			(String[])map.get(_expandoColumn24.getName()));
		Assert.assertArrayEquals(
			new String[] {_DATA_STRING},
			(String[])map.get(_expandoColumn25.getName()));
		Assert.assertEquals(
			new HashMap<>(), map.get(_expandoColumn26.getName()));
		Assert.assertEquals(
			new HashMap<>(), map.get(_expandoColumn27.getName()));
		Assert.assertEquals(
			map.toString(), _initialExpandoColumnsCount + 27, map.size());
	}

	@Test
	@TestInfo({"LPD-54757", "LPD-61106"})
	public void testToMapExpectedClassAndValue() throws Exception {

		// Boolean

		_testToMapExpectedClassAndValue(
			_createCustomField("true", null, _expandoColumn1, null),
			Boolean.class, true);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn1, null),
			Boolean.class, false);
		_testToMapExpectedClassAndValue(
			_createCustomField(Boolean.TRUE, null, _expandoColumn1, null),
			Boolean.class, true);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_INT, null, _expandoColumn1, null),
			Boolean.class, false);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new boolean[] {false}, null, _expandoColumn1, null),
			Boolean.class, false);
		_testToMapExpectedClassAndValue(
			_createCustomField(true, null, _expandoColumn1, null),
			Boolean.class, true);

		// Boolean array

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList("false", "true"), null, _expandoColumn2, null),
			boolean[].class, new boolean[] {false, true});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(Boolean.FALSE, Boolean.TRUE), null,
				_expandoColumn2, null),
			boolean[].class, new boolean[] {false, true});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn2, null),
			boolean[].class, new boolean[] {false});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(false, true), null, _expandoColumn2, null),
			boolean[].class, new boolean[] {false, true});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Boolean[] {Boolean.FALSE, Boolean.TRUE}, null,
				_expandoColumn2, null),
			boolean[].class, new boolean[] {false, true});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {"false", "true"}, null, _expandoColumn2, null),
			boolean[].class, new boolean[] {false, true});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new boolean[] {false, true}, null, _expandoColumn2, null),
			boolean[].class, new boolean[] {false, true});

		// Date

		Date randomDate1 = RandomTestUtil.nextDate();

		randomDate1 = new Date((randomDate1.getTime() / 1000) * 1000);

		_testToMapExpectedClassAndValue(
			_createCustomField(
				_dateFormat.format(randomDate1), null, _expandoColumn3, null),
			Date.class, randomDate1);
		_testToMapExpectedClassAndValue(
			_createCustomField(randomDate1, null, _expandoColumn3, null),
			Date.class, randomDate1);

		// Date array

		Date randomDate2 = RandomTestUtil.nextDate();

		randomDate2 = new Date((randomDate2.getTime() / 1000) * 1000);

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_dateFormat.format(randomDate2)), null,
				_expandoColumn4, null),
			Date[].class, new Date[] {randomDate2});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(randomDate2), null, _expandoColumn4, null),
			Date[].class, new Date[] {randomDate2});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Date[] {randomDate2}, null, _expandoColumn4, null),
			Date[].class, new Date[] {randomDate2});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {_dateFormat.format(randomDate2)}, null,
				_expandoColumn4, null),
			Date[].class, new Date[] {randomDate2});

		// Double

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Double.valueOf(_DATA_DOUBLE), null, _expandoColumn5, null),
			Double.class, _DATA_DOUBLE);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Float.valueOf(_DATA_FLOAT), null, _expandoColumn5, null),
			Double.class, (double)_DATA_FLOAT);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Integer.valueOf(_DATA_INT), null, _expandoColumn5, null),
			Double.class, (double)_DATA_INT);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				String.valueOf(_DATA_INT), null, _expandoColumn5, null),
			Double.class, (double)_DATA_INT);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_DOUBLE, null, _expandoColumn5, null),
			Double.class, _DATA_DOUBLE);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_FLOAT, null, _expandoColumn5, null),
			Double.class, (double)_DATA_FLOAT);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_INT, null, _expandoColumn5, null),
			Double.class, (double)_DATA_INT);

		// Double array

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_DOUBLE), null, _expandoColumn6, null),
			double[].class, new double[] {_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_FLOAT), null, _expandoColumn6, null),
			double[].class, new double[] {(double)_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn6, null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Double[] {Double.valueOf(_DATA_DOUBLE)}, null,
				_expandoColumn6, null),
			double[].class, new double[] {_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Float[] {Float.valueOf(_DATA_FLOAT)}, null, _expandoColumn6,
				null),
			double[].class, new double[] {(double)_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Integer[] {Integer.valueOf(_DATA_INT)}, null,
				_expandoColumn6, null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_INT)}, null, _expandoColumn6,
				null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new double[] {_DATA_DOUBLE}, null, _expandoColumn6, null),
			double[].class, new double[] {_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new float[] {_DATA_FLOAT}, null, _expandoColumn6, null),
			double[].class, new double[] {(double)_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new int[] {_DATA_INT}, null, _expandoColumn6, null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_DOUBLE), null, _expandoColumn7, null),
			double[].class, new double[] {_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_FLOAT), null, _expandoColumn7, null),
			double[].class, new double[] {(double)_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn7, null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Double[] {Double.valueOf(_DATA_DOUBLE)}, null,
				_expandoColumn7, null),
			double[].class, new double[] {_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Float[] {Float.valueOf(_DATA_FLOAT)}, null, _expandoColumn7,
				null),
			double[].class, new double[] {(double)_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Integer[] {Integer.valueOf(_DATA_INT)}, null,
				_expandoColumn7, null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_INT)}, null, _expandoColumn7,
				null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new double[] {_DATA_DOUBLE}, null, _expandoColumn7, null),
			double[].class, new double[] {_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new float[] {_DATA_FLOAT}, null, _expandoColumn7, null),
			double[].class, new double[] {(double)_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new int[] {_DATA_INT}, null, _expandoColumn7, null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_DOUBLE), null, _expandoColumn8, null),
			double[].class, new double[] {_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_FLOAT), null, _expandoColumn8, null),
			double[].class, new double[] {(double)_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn8, null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Double[] {Double.valueOf(_DATA_DOUBLE)}, null,
				_expandoColumn8, null),
			double[].class, new double[] {_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Float[] {Float.valueOf(_DATA_FLOAT)}, null, _expandoColumn8,
				null),
			double[].class, new double[] {(double)_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Integer[] {Integer.valueOf(_DATA_INT)}, null,
				_expandoColumn8, null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_INT)}, null, _expandoColumn8,
				null),
			double[].class, new double[] {(double)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new double[] {_DATA_DOUBLE}, null, _expandoColumn8, null),
			double[].class, new double[] {_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new float[] {_DATA_FLOAT}, null, _expandoColumn8, null),
			double[].class, new double[] {(double)_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new int[] {_DATA_INT}, null, _expandoColumn8, null),
			double[].class, new double[] {(double)_DATA_INT});

		// Float

		_testToMapExpectedClassAndValue(
			_createCustomField(
				String.valueOf(_DATA_DOUBLE), null, _expandoColumn9, null),
			Float.class, (float)_DATA_DOUBLE);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				String.valueOf(_DATA_FLOAT), null, _expandoColumn9, null),
			Float.class, _DATA_FLOAT);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_DOUBLE, null, _expandoColumn9, null),
			Float.class, (float)_DATA_DOUBLE);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_FLOAT, null, _expandoColumn9, null),
			Float.class, _DATA_FLOAT);

		// Float array

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(String.valueOf(_DATA_DOUBLE)), null,
				_expandoColumn10, null),
			float[].class, new float[] {(float)_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(String.valueOf(_DATA_FLOAT)), null,
				_expandoColumn10, null),
			float[].class, new float[] {_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_DOUBLE), null, _expandoColumn10, null),
			float[].class, new float[] {(float)_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_FLOAT), null, _expandoColumn10, null),
			float[].class, new float[] {_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Double[] {Double.valueOf(_DATA_DOUBLE)}, null,
				_expandoColumn10, null),
			float[].class, new float[] {(float)_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Float[] {Float.valueOf(_DATA_FLOAT)}, null,
				_expandoColumn10, null),
			float[].class, new float[] {_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_FLOAT)}, null,
				_expandoColumn10, null),
			float[].class, new float[] {_DATA_FLOAT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new double[] {_DATA_DOUBLE}, null, _expandoColumn10, null),
			float[].class, new float[] {(float)_DATA_DOUBLE});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new float[] {_DATA_FLOAT}, null, _expandoColumn10, null),
			float[].class, new float[] {_DATA_FLOAT});

		// Geolocation

		double randomDouble1 = RandomTestUtil.randomDouble();
		double randomDouble2 = RandomTestUtil.randomDouble();

		_testToMapExpectedClassAndValue(
			_createCustomField(
				null, null, _expandoColumn11,
				new Geo() {
					{
						latitude = randomDouble1;
						longitude = randomDouble2;
					}
				}),
			String.class,
			JSONUtil.put(
				"latitude", randomDouble1
			).put(
				"longitude", randomDouble2
			).toString());

		// Integer

		_testToMapExpectedClassAndValue(
			_createCustomField(
				String.valueOf(_DATA_INT), null, _expandoColumn12, null),
			Integer.class, _DATA_INT);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_INT, null, _expandoColumn12, null),
			Integer.class, _DATA_INT);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_LONG, null, _expandoColumn12, null),
			Integer.class, (int)_DATA_LONG);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal(_DATA_INT), null, _expandoColumn12, null),
			Integer.class, _DATA_INT);

		// Integer array

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(String.valueOf(_DATA_INT)), null,
				_expandoColumn13, null),
			int[].class, new int[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn13, null),
			int[].class, new int[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_LONG), null, _expandoColumn13, null),
			int[].class, new int[] {(int)_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(new BigDecimal(_DATA_INT)), null,
				_expandoColumn13, null),
			int[].class, new int[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal[] {new BigDecimal(_DATA_INT)}, null,
				_expandoColumn13, null),
			int[].class, new int[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Integer[] {Integer.valueOf(_DATA_INT)}, null,
				_expandoColumn13, null),
			int[].class, new int[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Long[] {Long.valueOf(_DATA_LONG)}, null, _expandoColumn13,
				null),
			int[].class, new int[] {(int)_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_INT)}, null,
				_expandoColumn13, null),
			int[].class, new int[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new int[] {_DATA_INT}, null, _expandoColumn13, null),
			int[].class, new int[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new long[] {_DATA_LONG}, null, _expandoColumn13, null),
			int[].class, new int[] {(int)_DATA_LONG});

		// Long

		_testToMapExpectedClassAndValue(
			_createCustomField(
				String.valueOf(_DATA_INT), null, _expandoColumn14, null),
			Long.class, (long)_DATA_INT);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				String.valueOf(_DATA_INT), null, _expandoColumn14, null),
			Long.class, (long)_DATA_INT);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				String.valueOf(_DATA_LONG), null, _expandoColumn14, null),
			Long.class, (long)_DATA_LONG);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				String.valueOf(_DATA_LONG), null, _expandoColumn14, null),
			Long.class, _DATA_LONG);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_INT, null, _expandoColumn14, null),
			Long.class, (long)_DATA_INT);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_LONG, null, _expandoColumn14, null),
			Long.class, _DATA_LONG);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal(_DATA_LONG), null, _expandoColumn14, null),
			Long.class, _DATA_LONG);

		// Long array

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(String.valueOf(_DATA_INT)), null,
				_expandoColumn15, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(String.valueOf(_DATA_LONG)), null,
				_expandoColumn15, null),
			long[].class, new long[] {(long)_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn15, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_LONG), null, _expandoColumn15, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(new BigDecimal(_DATA_LONG)), null,
				_expandoColumn15, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal[] {new BigDecimal(_DATA_LONG)}, null,
				_expandoColumn15, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Integer[] {Integer.valueOf(_DATA_INT)}, null,
				_expandoColumn15, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Long[] {Long.valueOf(_DATA_LONG)}, null, _expandoColumn15,
				null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_INT)}, null,
				_expandoColumn15, null),
			long[].class, new long[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_LONG)}, null,
				_expandoColumn15, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new int[] {_DATA_INT}, null, _expandoColumn15, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new long[] {_DATA_LONG}, null, _expandoColumn15, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(String.valueOf(_DATA_INT)), null,
				_expandoColumn16, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(String.valueOf(_DATA_LONG)), null,
				_expandoColumn16, null),
			long[].class, new long[] {(long)_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn16, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_LONG), null, _expandoColumn16, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(new BigDecimal(_DATA_LONG)), null,
				_expandoColumn16, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal[] {new BigDecimal(_DATA_LONG)}, null,
				_expandoColumn16, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Integer[] {Integer.valueOf(_DATA_INT)}, null,
				_expandoColumn16, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Long[] {Long.valueOf(_DATA_LONG)}, null, _expandoColumn16,
				null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_INT)}, null,
				_expandoColumn16, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_LONG)}, null,
				_expandoColumn16, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new int[] {_DATA_INT}, null, _expandoColumn16, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new long[] {_DATA_LONG}, null, _expandoColumn16, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn17, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_LONG), null, _expandoColumn17, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(new BigDecimal(_DATA_LONG)), null,
				_expandoColumn17, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal[] {new BigDecimal(_DATA_LONG)}, null,
				_expandoColumn17, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Integer[] {Integer.valueOf(_DATA_INT)}, null,
				_expandoColumn17, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Long[] {Long.valueOf(_DATA_LONG)}, null, _expandoColumn17,
				null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_INT)}, null,
				_expandoColumn17, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {String.valueOf(_DATA_LONG)}, null,
				_expandoColumn17, null),
			long[].class, new long[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new int[] {_DATA_INT}, null, _expandoColumn17, null),
			long[].class, new long[] {(long)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new long[] {_DATA_LONG}, null, _expandoColumn17, null),
			long[].class, new long[] {_DATA_LONG});

		// Number

		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_INT, null, _expandoColumn18, null),
			Number.class, _DATA_INT);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_LONG, null, _expandoColumn18, null),
			Number.class, _DATA_LONG);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal(_DATA_LONG), null, _expandoColumn18, null),
			Number.class, new BigDecimal(_DATA_LONG));

		// Number array

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn19, null),
			Number[].class, new Number[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_LONG), null, _expandoColumn19, null),
			Number[].class, new Number[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(new BigDecimal(_DATA_LONG)), null,
				_expandoColumn19, null),
			Number[].class, new Number[] {new BigDecimal(_DATA_LONG)});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal[] {new BigDecimal(_DATA_LONG)}, null,
				_expandoColumn19, null),
			Number[].class, new Number[] {new BigDecimal(_DATA_LONG)});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Integer[] {Integer.valueOf(_DATA_INT)}, null,
				_expandoColumn19, null),
			Number[].class, new Number[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Long[] {Long.valueOf(_DATA_LONG)}, null, _expandoColumn19,
				null),
			Number[].class, new Number[] {_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new int[] {_DATA_INT}, null, _expandoColumn19, null),
			Number[].class, new Number[] {_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new long[] {_DATA_LONG}, null, _expandoColumn19, null),
			Number[].class, new Number[] {_DATA_LONG});

		// Short

		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_INT, null, _expandoColumn20, null),
			Short.class, (short)_DATA_INT);
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_LONG, null, _expandoColumn20, null),
			Short.class, (short)_DATA_LONG);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal(_DATA_LONG), null, _expandoColumn20, null),
			Short.class, (short)_DATA_LONG);

		// Short array

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn21, null),
			short[].class, new short[] {(short)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_LONG), null, _expandoColumn21, null),
			short[].class, new short[] {(short)_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(new BigDecimal(_DATA_LONG)), null,
				_expandoColumn21, null),
			short[].class, new short[] {(short)_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new BigDecimal[] {new BigDecimal(_DATA_LONG)}, null,
				_expandoColumn21, null),
			short[].class, new short[] {(short)_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Integer[] {Integer.valueOf(_DATA_INT)}, null,
				_expandoColumn21, null),
			short[].class, new short[] {(short)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new Long[] {Long.valueOf(_DATA_LONG)}, null, _expandoColumn21,
				null),
			short[].class, new short[] {(short)_DATA_LONG});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new int[] {_DATA_INT}, null, _expandoColumn21, null),
			short[].class, new short[] {(short)_DATA_INT});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new long[] {_DATA_LONG}, null, _expandoColumn21, null),
			short[].class, new short[] {(short)_DATA_LONG});

		// String

		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_INT, null, _expandoColumn22, null),
			String.class, String.valueOf(_DATA_INT));
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_STRING, null, _expandoColumn22, null),
			String.class, _DATA_STRING);

		// String array

		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn23, null),
			String[].class, new String[] {String.valueOf(_DATA_INT)});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_STRING), null, _expandoColumn23, null),
			String[].class, new String[] {_DATA_STRING});
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_STRING, null, _expandoColumn23, null),
			String.class, _DATA_STRING);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {_DATA_STRING}, null, _expandoColumn23, null),
			String[].class, new String[] {_DATA_STRING});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn24, null),
			String[].class, new String[] {String.valueOf(_DATA_INT)});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_STRING), null, _expandoColumn24, null),
			String[].class, new String[] {_DATA_STRING});
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_STRING, null, _expandoColumn24, null),
			String.class, _DATA_STRING);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {_DATA_STRING}, null, _expandoColumn24, null),
			String[].class, new String[] {_DATA_STRING});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_INT), null, _expandoColumn25, null),
			String[].class, new String[] {String.valueOf(_DATA_INT)});
		_testToMapExpectedClassAndValue(
			_createCustomField(
				Arrays.asList(_DATA_STRING), null, _expandoColumn25, null),
			String[].class, new String[] {_DATA_STRING});
		_testToMapExpectedClassAndValue(
			_createCustomField(_DATA_STRING, null, _expandoColumn25, null),
			String.class, _DATA_STRING);
		_testToMapExpectedClassAndValue(
			_createCustomField(
				new String[] {_DATA_STRING}, null, _expandoColumn25, null),
			String[].class, new String[] {_DATA_STRING});

		// String localized

		String randomString = RandomTestUtil.randomString();

		_testToMapExpectedClassAndValue(
			_createCustomField(
				_DATA_STRING,
				HashMapBuilder.put(
					"en-US", _DATA_STRING
				).put(
					"fr-FR", randomString
				).put(
					"pt-BR", randomString
				).build(),
				_expandoColumn27, null),
			Map.class,
			HashMapBuilder.put(
				_enLocale, _DATA_STRING
			).put(
				_frLocale, randomString
			).put(
				_ptLocale, randomString
			).build());
	}

	@Test
	@TestInfo({"LPD-54757", "LPD-61106"})
	public void testToMapExpectedClassAndValueInvalidValues() {

		// Boolean array

		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"Unable to parse date from " + _DATA_INT + "",
			() -> _testToMapExpectedClassAndValue(
				_createCustomField(_DATA_INT, null, _expandoColumn3, null),
				null, null));

		// Date array

		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"Unable to parse date from " + _DATA_INT + "",
			() -> _testToMapExpectedClassAndValue(
				_createCustomField(
					Arrays.asList(_DATA_INT), null, _expandoColumn4, null),
				null, null));
	}

	@Test
	public void testToMapWithNonexistentCustomFieldAndLazyReferencingDisabled()
		throws Exception {

		long exportImportConfigurationId = RandomTestUtil.nextLong();

		ExportImportThreadLocal.setExportImportConfigurationId(
			exportImportConfigurationId);

		int initialExpandoColumnsCount =
			_expandoColumnLocalService.getColumnsCount(
				TestPropsValues.getCompanyId(),
				_classNameLocalService.getClassNameId(_clazz), "CUSTOM_FIELDS");

		try {
			CustomFieldsUtil.toMap(
				_clazz.getName(), TestPropsValues.getCompanyId(),
				new CustomField[] {
					new CustomField() {
						{
							attributeType = AttributeType.INTEGER;
							customValue = new CustomValue() {
								{
									data = _DATA_INT;
								}
							};
							dataType = "Integer";
							name = RandomTestUtil.randomString();
						}
					}
				},
				LocaleUtil.getDefault());

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {

			// TODO LPD-65443 The exception should not be a NPE

			Assert.assertNotNull(nullPointerException);

			Assert.assertEquals(
				initialExpandoColumnsCount,
				_expandoColumnLocalService.getColumnsCount(
					TestPropsValues.getCompanyId(),
					_classNameLocalService.getClassNameId(_clazz),
					"CUSTOM_FIELDS"));
			Assert.assertTrue(
				ListUtil.isEmpty(
					_exportImportReportEntryLocalService.
						getExportImportReportEntries(
							TestPropsValues.getCompanyId(),
							exportImportConfigurationId)));
		}
	}

	@LazyReferencing
	@Test
	public void testToMapWithNonexistentCustomFieldAndLazyReferencingEnabled()
		throws Exception {

		long exportImportConfigurationId = RandomTestUtil.nextLong();

		ExportImportThreadLocal.setExportImportConfigurationId(
			exportImportConfigurationId);

		int initialExpandoColumnsCount =
			_expandoColumnLocalService.getColumnsCount(
				TestPropsValues.getCompanyId(),
				_classNameLocalService.getClassNameId(_clazz), "CUSTOM_FIELDS");

		Date randomDate = RandomTestUtil.nextDate();
		String randomName1 = RandomTestUtil.randomString();
		String randomName2 = RandomTestUtil.randomString();
		String randomName3 = RandomTestUtil.randomString();
		String randomName4 = RandomTestUtil.randomString();
		String randomName5 = RandomTestUtil.randomString();
		String randomName6 = RandomTestUtil.randomString();
		String randomName7 = RandomTestUtil.randomString();
		String randomName8 = RandomTestUtil.randomString();
		String randomName9 = RandomTestUtil.randomString();
		String randomName10 = RandomTestUtil.randomString();
		String randomName11 = RandomTestUtil.randomString();
		String randomName12 = RandomTestUtil.randomString();
		String randomName13 = RandomTestUtil.randomString();
		String randomName14 = RandomTestUtil.randomString();
		String randomName15 = RandomTestUtil.randomString();
		String randomName16 = RandomTestUtil.randomString();
		String randomName17 = RandomTestUtil.randomString();
		String randomName18 = RandomTestUtil.randomString();
		String randomName19 = RandomTestUtil.randomString();
		String randomName20 = RandomTestUtil.randomString();
		String randomName21 = RandomTestUtil.randomString();
		Number randomNumber = RandomTestUtil.randomInt();
		short randomShort = (short)RandomTestUtil.randomInt(
			Short.MIN_VALUE, Short.MAX_VALUE);
		String randomString = RandomTestUtil.randomString();

		Map<String, Serializable> map = CustomFieldsUtil.toMap(
			_clazz.getName(), TestPropsValues.getCompanyId(),
			new CustomField[] {
				new CustomField() {
					{
						attributeType = AttributeType.BOOLEAN;
						customValue = new CustomValue() {
							{
								data = true;
							}
						};
						name = _expandoColumn1.getName();
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.BOOLEAN;
						customValue = new CustomValue() {
							{
								data = true;
							}
						};
						name = randomName1;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.BOOLEAN_ARRAY;
						customValue = new CustomValue() {
							{
								data = new boolean[] {true};
							}
						};
						name = randomName2;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.DATE;
						customValue = new CustomValue() {
							{
								data = _dateFormat.format(randomDate);
							}
						};
						dataType = "";
						name = randomName3;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.DATE_ARRAY;
						customValue = new CustomValue() {
							{
								data = new Date[] {randomDate};
							}
						};
						name = randomName4;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.DOUBLE;
						customValue = new CustomValue() {
							{
								data = _DATA_DOUBLE;
							}
						};
						name = randomName5;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.DOUBLE_ARRAY;
						customValue = new CustomValue() {
							{
								data = new double[] {_DATA_DOUBLE};
							}
						};
						name = randomName6;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.FLOAT;
						customValue = new CustomValue() {
							{
								data = _DATA_FLOAT;
							}
						};
						name = randomName7;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.FLOAT_ARRAY;
						customValue = new CustomValue() {
							{
								data = new float[] {_DATA_FLOAT};
							}
						};
						name = randomName8;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.INTEGER;
						customValue = new CustomValue() {
							{
								data = _DATA_INT;
							}
						};
						name = randomName9;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.INTEGER_ARRAY;
						customValue = new CustomValue() {
							{
								data = new int[] {_DATA_INT};
							}
						};
						name = randomName10;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.LONG;
						customValue = new CustomValue() {
							{
								data = _DATA_LONG;
							}
						};
						name = randomName11;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.LONG_ARRAY;
						customValue = new CustomValue() {
							{
								data = new long[] {_DATA_LONG};
							}
						};
						name = randomName12;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.SHORT;
						customValue = new CustomValue() {
							{
								data = randomShort;
							}
						};
						name = randomName13;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.SHORT_ARRAY;
						customValue = new CustomValue() {
							{
								data = new short[] {randomShort};
							}
						};
						name = randomName14;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.STRING;
						customValue = new CustomValue() {
							{
								data = _DATA_STRING;
							}
						};
						name = randomName15;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.STRING_ARRAY;
						customValue = new CustomValue() {
							{
								data = new String[] {_DATA_STRING};
							}
						};
						name = randomName16;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.NUMBER;
						customValue = new CustomValue() {
							{
								data = new BigDecimal(randomNumber.intValue());
							}
						};
						name = randomName17;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.NUMBER_ARRAY;
						customValue = new CustomValue() {
							{
								data = new Number[] {
									new BigDecimal(randomNumber.intValue())
								};
							}
						};
						name = randomName18;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.STRING_ARRAY_LOCALIZED;
						customValue = new CustomValue() {
							{
								data = HashMapBuilder.put(
									_enLocale, new String[] {_DATA_STRING}
								).put(
									_frLocale, new String[] {randomString}
								).put(
									_ptLocale, new String[] {randomString}
								).build();
							}
						};
						name = randomName19;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.STRING_LOCALIZED;
						customValue = new CustomValue() {
							{
								data = _DATA_STRING;
								data_i18n = HashMapBuilder.put(
									"en-US", _DATA_STRING
								).put(
									"fr-FR", randomString
								).put(
									"pt-BR", randomString
								).build();
							}
						};
						name = randomName20;
					}
				},
				new CustomField() {
					{
						attributeType = AttributeType.GEOLOCATION;
						customValue = new CustomValue() {
							{
								geo = new Geo() {
									{
										latitude = _DATA_DOUBLE;
										longitude = _DATA_DOUBLE;
									}
								};
							}
						};
						name = randomName21;
					}
				}
			},
			LocaleUtil.getDefault());

		Assert.assertEquals(
			initialExpandoColumnsCount + 21,
			_expandoColumnLocalService.getColumnsCount(
				TestPropsValues.getCompanyId(),
				_classNameLocalService.getClassNameId(_clazz),
				"CUSTOM_FIELDS"));

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			TestPropsValues.getCompanyId(), _clazz.getName());

		Assert.assertEquals(
			1, expandoBridge.getAttributeType(_expandoColumn1.getName()));

		Assert.assertTrue((boolean)map.get(_expandoColumn1.getName()));
		Assert.assertEquals(1, expandoBridge.getAttributeType(randomName1));
		Assert.assertTrue((boolean)map.get(randomName1));
		Assert.assertEquals(2, expandoBridge.getAttributeType(randomName2));
		Assert.assertArrayEquals(
			new boolean[] {true}, (boolean[])map.get(randomName2));
		Assert.assertEquals(3, expandoBridge.getAttributeType(randomName3));
		Assert.assertTrue(
			DateUtil.equals(
				new Date((randomDate.getTime() / 1000) * 1000),
				(Date)map.get(randomName3)));
		Assert.assertEquals(4, expandoBridge.getAttributeType(randomName4));
		Assert.assertArrayEquals(
			new Date[] {randomDate}, (Date[])map.get(randomName4));
		Assert.assertEquals(5, expandoBridge.getAttributeType(randomName5));
		Assert.assertEquals(_DATA_DOUBLE, map.get(randomName5));
		Assert.assertEquals(6, expandoBridge.getAttributeType(randomName6));
		Assert.assertArrayEquals(
			new double[] {_DATA_DOUBLE}, (double[])map.get(randomName6), 0);
		Assert.assertEquals(7, expandoBridge.getAttributeType(randomName7));
		Assert.assertEquals(_DATA_FLOAT, map.get(randomName7));
		Assert.assertEquals(8, expandoBridge.getAttributeType(randomName8));
		Assert.assertArrayEquals(
			new float[] {_DATA_FLOAT}, (float[])map.get(randomName8), 0);
		Assert.assertEquals(9, expandoBridge.getAttributeType(randomName9));
		Assert.assertEquals(_DATA_INT, map.get(randomName9));
		Assert.assertEquals(10, expandoBridge.getAttributeType(randomName10));
		Assert.assertArrayEquals(
			new int[] {_DATA_INT}, (int[])map.get(randomName10));
		Assert.assertEquals(11, expandoBridge.getAttributeType(randomName11));
		Assert.assertEquals(_DATA_LONG, map.get(randomName11));
		Assert.assertEquals(12, expandoBridge.getAttributeType(randomName12));
		Assert.assertArrayEquals(
			new long[] {_DATA_LONG}, (long[])map.get(randomName12));
		Assert.assertEquals(13, expandoBridge.getAttributeType(randomName13));
		Assert.assertEquals(randomShort, map.get(randomName13));
		Assert.assertEquals(14, expandoBridge.getAttributeType(randomName14));
		Assert.assertArrayEquals(
			new short[] {randomShort}, (short[])map.get(randomName14));
		Assert.assertEquals(15, expandoBridge.getAttributeType(randomName15));
		Assert.assertEquals(_DATA_STRING, map.get(randomName15));
		Assert.assertEquals(16, expandoBridge.getAttributeType(randomName16));
		Assert.assertArrayEquals(
			new String[] {_DATA_STRING}, (String[])map.get(randomName16));
		Assert.assertEquals(17, expandoBridge.getAttributeType(randomName17));
		Assert.assertEquals(
			new BigDecimal(randomNumber.intValue()), map.get(randomName17));
		Assert.assertEquals(18, expandoBridge.getAttributeType(randomName18));
		Assert.assertArrayEquals(
			new Number[] {new BigDecimal(randomNumber.intValue())},
			(Number[])map.get(randomName18));
		Assert.assertEquals(19, expandoBridge.getAttributeType(randomName19));
		AssertUtils.assertEquals(
			HashMapBuilder.put(
				_enLocale, new String[] {_DATA_STRING}
			).put(
				_frLocale, new String[] {randomString}
			).put(
				_ptLocale, new String[] {randomString}
			).build(),
			(Map)map.get(randomName19));
		Assert.assertEquals(20, expandoBridge.getAttributeType(randomName20));
		AssertUtils.assertEquals(
			HashMapBuilder.put(
				_enLocale, _DATA_STRING
			).put(
				_frLocale, randomString
			).put(
				_ptLocale, randomString
			).build(),
			(Map)map.get(randomName20));
		Assert.assertEquals(21, expandoBridge.getAttributeType(randomName21));
		Assert.assertEquals(
			JSONUtil.put(
				"latitude", _DATA_DOUBLE
			).put(
				"longitude", _DATA_DOUBLE
			).toString(),
			map.get(randomName21));

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(), exportImportConfigurationId);

		Assert.assertEquals(
			exportImportReportEntries.toString(),
			exportImportReportEntries.size(), 21);

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntries.get(0);

		Assert.assertEquals(0L, exportImportReportEntry.getGroupId());
		Assert.assertEquals(
			TestPropsValues.getCompanyId(),
			exportImportReportEntry.getCompanyId());
		Assert.assertEquals(
			randomName1,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			_classNameLocalService.getClassNameId(ExpandoColumn.class),
			exportImportReportEntry.getClassNameId());
		Assert.assertEquals(
			exportImportConfigurationId,
			exportImportReportEntry.getExportImportConfigurationId());
		Assert.assertNull(exportImportReportEntry.getErrorMessage());
		Assert.assertNull(exportImportReportEntry.getErrorStacktrace());
		Assert.assertEquals(
			ExpandoColumn.class.getName(),
			exportImportReportEntry.getModelName());
		Assert.assertEquals(
			ExportImportReportEntryConstants.ORIGIN_STAGING,
			exportImportReportEntry.getOrigin());
		Assert.assertEquals("company", exportImportReportEntry.getScope());
		Assert.assertEquals("", exportImportReportEntry.getScopeKey());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_EMPTY,
			exportImportReportEntry.getType());
		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED,
			exportImportReportEntry.getStatus());
	}

	private ExpandoColumn _addExpandoColumn(
			Object defaultData, String displayType, ExpandoTable expandoTable,
			int type)
		throws Exception {

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			expandoTable, "A" + RandomTestUtil.randomString(), type,
			defaultData);

		if (displayType != null) {
			UnicodeProperties unicodeProperties =
				expandoColumn.getTypeSettingsProperties();

			unicodeProperties.putAll(
				HashMapBuilder.put(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE, displayType
				).build());

			expandoColumn.setTypeSettingsProperties(unicodeProperties);
		}

		return _expandoColumnLocalService.updateExpandoColumn(expandoColumn);
	}

	private void _assertEquals(
		CustomField customField1, CustomField customField2) {

		Assert.assertEquals(
			customField1.getAttributeType(), customField2.getAttributeType());

		_assertEquals(
			customField1.getCustomValue(), customField2.getCustomValue());

		Assert.assertEquals(
			customField1.getDataType(), customField2.getDataType());
		Assert.assertEquals(customField1.getName(), customField2.getName());
	}

	private void _assertEquals(
		CustomValue customValue1, CustomValue customValue2) {

		if (customValue1 == customValue2) {
			return;
		}

		if (customValue1.getData() instanceof Map) {
			AssertUtils.assertEquals(
				(Map)customValue1.getData(), (Map)customValue2.getData());
		}
		else {
			Assert.assertTrue(
				Objects.deepEquals(
					customValue1.getData(), customValue2.getData()));
		}

		if (customValue1.getData_i18n() != customValue2.getData_i18n()) {
			AssertUtils.assertEquals(
				(Map)customValue1.getData_i18n(),
				(Map)customValue2.getData_i18n());
		}

		_assertEquals(customValue1.getGeo(), customValue2.getGeo());
	}

	private void _assertEquals(Geo geo1, Geo geo2) {
		if (geo1 == geo2) {
			return;
		}

		Assert.assertEquals(geo1.getLatitude(), geo2.getLatitude());
		Assert.assertEquals(geo1.getLongitude(), geo2.getLongitude());
	}

	private void _assertNullAttributeType(
			CustomField[] customFields, String name)
		throws Exception {

		CustomField customField = _getCustomField(customFields, name);

		Assert.assertNull(customField.getAttributeType());
	}

	private CustomField _createCustomField(
		Object data, Map<String, String> data_i18n, ExpandoColumn expandoColumn,
		Geo geo) {

		CustomField customField = new CustomField();

		CustomValue customValue = new CustomValue();

		customValue.setData(data);
		customValue.setData_i18n(data_i18n);
		customValue.setGeo(geo);

		customField.setCustomValue(customValue);

		customField.setName(expandoColumn.getName());

		return customField;
	}

	private CustomField _getCustomField(CustomField[] customFields, String name)
		throws Exception {

		for (CustomField customField : customFields) {
			if (StringUtil.equals(customField.getName(), name)) {
				return customField;
			}
		}

		throw new NoSuchValueException();
	}

	private void _testToMapExpectedClassAndValue(
			CustomField customField, Class<?> expectedClass,
			Object expectedValue)
		throws Exception {

		Map<String, Serializable> map = CustomFieldsUtil.toMap(
			_clazz.getName(), TestPropsValues.getCompanyId(),
			new CustomField[] {customField}, LocaleUtil.getDefault());

		Object actualValue = map.get(customField.getName());

		if (expectedClass == Map.class) {
			AssertUtils.assertEquals(
				(Map<Locale, ?>)expectedValue, (Map<Locale, ?>)actualValue);
		}
		else {
			Assert.assertTrue(Objects.deepEquals(expectedValue, actualValue));
		}

		Assert.assertTrue(expectedClass.isInstance(actualValue));

		_user.setExpandoBridgeAttributes(
			new ServiceContext() {
				{
					setExpandoBridgeAttributes(map);
				}
			});

		Assert.assertNotNull(
			_getCustomField(
				CustomFieldsUtil.toCustomFields(
					true, _clazz.getName(), _user.getPrimaryKey(),
					TestPropsValues.getCompanyId(), LocaleUtil.getDefault()),
				_expandoColumn1.getName()));
	}

	private static final double _DATA_DOUBLE = RandomTestUtil.randomDouble();

	private static final float _DATA_FLOAT = RandomTestUtil.randomFloat();

	private static final int _DATA_INT = RandomTestUtil.randomInt();

	private static final long _DATA_LONG = RandomTestUtil.randomLong();

	private static final String _DATA_STRING = RandomTestUtil.randomString();

	private static final DateFormat _dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss'Z'");

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private final Class<?> _clazz = User.class;

	@Inject
	private CompanyLocalService _companyLocalService;

	private final Locale _enLocale = LocaleUtil.fromLanguageId("en_US");
	private ExpandoColumn _expandoColumn1;
	private ExpandoColumn _expandoColumn2;
	private ExpandoColumn _expandoColumn3;
	private ExpandoColumn _expandoColumn4;
	private ExpandoColumn _expandoColumn5;
	private ExpandoColumn _expandoColumn6;
	private ExpandoColumn _expandoColumn7;
	private ExpandoColumn _expandoColumn8;
	private ExpandoColumn _expandoColumn9;
	private ExpandoColumn _expandoColumn10;
	private ExpandoColumn _expandoColumn11;
	private ExpandoColumn _expandoColumn12;
	private ExpandoColumn _expandoColumn13;
	private ExpandoColumn _expandoColumn14;
	private ExpandoColumn _expandoColumn15;
	private ExpandoColumn _expandoColumn16;
	private ExpandoColumn _expandoColumn17;
	private ExpandoColumn _expandoColumn18;
	private ExpandoColumn _expandoColumn19;
	private ExpandoColumn _expandoColumn20;
	private ExpandoColumn _expandoColumn21;
	private ExpandoColumn _expandoColumn22;
	private ExpandoColumn _expandoColumn23;
	private ExpandoColumn _expandoColumn24;
	private ExpandoColumn _expandoColumn25;
	private ExpandoColumn _expandoColumn26;
	private ExpandoColumn _expandoColumn27;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@DeleteAfterTestRun
	private ExpandoTable _expandoTable;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	private final Locale _frLocale = LocaleUtil.fromLanguageId("fr_FR");
	private int _initialExpandoColumnsCount;
	private final Locale _ptLocale = LocaleUtil.fromLanguageId("pt_BR");

	@DeleteAfterTestRun
	private User _user;

}