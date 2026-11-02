/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.expando.kernel.exception.ValueDataException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.expando.model.impl.ExpandoValueImpl;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcellus Tavares
 */
@RunWith(Arquillian.class)
public class ExpandoValueLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_classNameId = PortalUtil.getClassNameId(DLFileEntry.class);

		_enLocale = LocaleUtil.fromLanguageId("en_US");
		_frLocale = LocaleUtil.fromLanguageId("fr_FR");
		_ptLocale = LocaleUtil.fromLanguageId("pt_BR");

		_expandoTable = ExpandoTestUtil.addTable(
			_classNameId, "testExpandoTable");
	}

	@Test
	public void testAddEscapedGeolocationValue() throws Exception {
		JSONObject geolocationJSONObject = JSONUtil.put(
			"latitude", RandomTestUtil.randomDouble()
		).put(
			"longitude", RandomTestUtil.randomDouble()
		);

		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.GEOLOCATION);

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column,
			HtmlUtil.escape(geolocationJSONObject.toString()));

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		Assert.assertEquals(
			String.valueOf(geolocationJSONObject),
			String.valueOf(value.getGeolocationJSONObject()));

		Serializable serializable = value.getSerializable();

		Assert.assertTrue(serializable instanceof JSONObject);
		Assert.assertEquals(
			String.valueOf(geolocationJSONObject),
			String.valueOf(serializable));
	}

	@Test
	public void testAddLocalizedStringArrayValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING_ARRAY_LOCALIZED);

		String[] enValues = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString()
		};
		String[] ptValues = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString()
		};

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column,
			HashMapBuilder.put(
				_enLocale, enValues
			).put(
				_ptLocale, ptValues
			).build());

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		Map<Locale, String[]> stringArrayMap = value.getStringArrayMap();

		Assert.assertArrayEquals(enValues, stringArrayMap.get(_enLocale));
		Assert.assertArrayEquals(ptValues, stringArrayMap.get(_ptLocale));
	}

	@Test
	public void testAddLocalizedStringValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING_LOCALIZED);

		String enValue = RandomTestUtil.randomString();
		String ptValue = RandomTestUtil.randomString();

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column,
			HashMapBuilder.put(
				_enLocale, enValue
			).put(
				_ptLocale, ptValue
			).build());

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		Map<Locale, String> stringMap = value.getStringMap();

		Assert.assertEquals(enValue, stringMap.get(_enLocale));
		Assert.assertEquals(ptValue, stringMap.get(_ptLocale));
	}

	@Test
	public void testAddLocalizedStringValues() throws Exception {
		ExpandoColumn column1 = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING_LOCALIZED);
		ExpandoColumn column2 = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING_LOCALIZED);
		ExpandoColumn column3 = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING_LOCALIZED);

		Serializable data1 = HashMapBuilder.put(
			_enLocale, RandomTestUtil.randomString()
		).put(
			_ptLocale, RandomTestUtil.randomString()
		).build();
		Serializable data2 = HashMapBuilder.put(
			_enLocale, RandomTestUtil.randomString()
		).build();
		Serializable data3 = new HashMap<>();

		Map<String, Serializable> attributes =
			new HashMapBuilder<>().<String, Serializable>put(
				column1.getName(), data1
			).put(
				column2.getName(), data2
			).put(
				column3.getName(), data3
			).build();

		long classPK = _counterLocalService.increment();

		ExpandoTestUtil.addValues(_expandoTable, classPK, attributes);

		Assert.assertEquals(
			attributes,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), attributes.keySet(), classPK));
		Assert.assertEquals(
			attributes,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), classPK));
		Assert.assertEquals(
			data1,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), column1.getName(), classPK));
		Assert.assertEquals(
			data2,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), column2.getName(), classPK));
		Assert.assertEquals(
			data3,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), column3.getName(), classPK));
	}

	@Test
	public void testAddNondefaultLocalizedStringValue() throws Exception {
		try {
			ExpandoTestUtil.addColumn(
				_expandoTable, RandomTestUtil.randomString(),
				ExpandoColumnConstants.STRING_LOCALIZED,
				HashMapBuilder.put(
					_ptLocale, RandomTestUtil.randomString()
				).build());

			Assert.fail();
		}
		catch (ValueDataException.MustInformDefaultLocale valueDataException) {
			Assert.assertNotNull(valueDataException);
		}
	}

	@Test
	public void testAddSiteDefaultLocalizedStringValue() throws Exception {
		Locale originalLocale = LocaleUtil.getSiteDefault();

		try {
			LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.FRANCE);

			ExpandoColumn column = ExpandoTestUtil.addColumn(
				_expandoTable, RandomTestUtil.randomString(),
				ExpandoColumnConstants.STRING_LOCALIZED);

			ExpandoValue value = ExpandoTestUtil.addValue(
				_expandoTable, column,
				HashMapBuilder.put(
					_enLocale, RandomTestUtil.randomString()
				).put(
					_frLocale, RandomTestUtil.randomString()
				).build());

			value = _expandoValueLocalService.getExpandoValue(
				value.getValueId());

			Assert.assertEquals(_frLocale, value.getDefaultLocale());
		}
		finally {
			LocaleThreadLocal.setSiteDefaultLocale(originalLocale);
		}
	}

	@Test
	public void testAddStringArrayValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING_ARRAY);

		String[] data = {
			RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + ", " + RandomTestUtil.randomString()
		};

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column, data);

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		Assert.assertArrayEquals(data, value.getStringArray());
	}

	@Test
	public void testAddValueWithFlushInBetween() throws Throwable {
		ExpandoColumn column1 = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);
		ExpandoColumn column2 = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		long classPK = _counterLocalService.increment();

		ExpandoTestUtil.addValues(
			_expandoTable, classPK,
			new HashMapBuilder<>().<String, Serializable>put(
				column1.getName(), RandomTestUtil.randomString()
			).<String, Serializable>put(
				column2.getName(), RandomTestUtil.randomString()
			).build());

		_entityCache.clearCache(ExpandoValueImpl.class);
		_finderCache.clearCache(ExpandoValueImpl.class);

		String data1 = RandomTestUtil.randomString();
		String data2 = RandomTestUtil.randomString();

		TransactionInvokerUtil.invoke(
			TransactionConfig.Factory.create(
				Propagation.REQUIRED, new Class<?>[] {Exception.class}),
			() -> {
				ExpandoTestUtil.addValue(
					_expandoTable, column1, classPK, data1);

				ExpandoTestUtil.addValue(
					_expandoTable, column2, classPK, data2);

				return null;
			});

		Assert.assertEquals(
			data1,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), column1.getName(), classPK));
		Assert.assertEquals(
			data2,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), column2.getName(), classPK));
	}

	@Test
	public void testAddValuesWithFlushInBetween() throws Throwable {
		ExpandoColumn column1 = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);
		ExpandoColumn column2 = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		long classPK = _counterLocalService.increment();

		ExpandoTestUtil.addValues(
			_expandoTable, classPK,
			new HashMapBuilder<>().<String, Serializable>put(
				column1.getName(), RandomTestUtil.randomString()
			).<String, Serializable>put(
				column2.getName(), RandomTestUtil.randomString()
			).build());

		_entityCache.clearCache(ExpandoValueImpl.class);
		_finderCache.clearCache(ExpandoValueImpl.class);

		String data1 = RandomTestUtil.randomString();
		String data2 = RandomTestUtil.randomString();

		TransactionInvokerUtil.invoke(
			TransactionConfig.Factory.create(
				Propagation.REQUIRED, new Class<?>[] {Exception.class}),
			() -> {
				ExpandoTestUtil.addValues(
					_expandoTable, classPK,
					new HashMapBuilder<>().<String, Serializable>put(
						column1.getName(), data1
					).<String, Serializable>put(
						column2.getName(), data2
					).build());

				return null;
			});

		Assert.assertEquals(
			data1,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), column1.getName(), classPK));
		Assert.assertEquals(
			data2,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), column2.getName(), classPK));
	}

	@Test
	public void testAddWrongValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		Map<Locale, String> dataMap = HashMapBuilder.put(
			_enLocale, RandomTestUtil.randomString()
		).put(
			_ptLocale, RandomTestUtil.randomString()
		).build();

		try {
			ExpandoTestUtil.addValue(
				_expandoTable, column, dataMap, LocaleUtil.getDefault());

			Assert.fail();
		}
		catch (ValueDataException valueDataException) {
			if (_log.isDebugEnabled()) {
				_log.debug(valueDataException);
			}
		}
	}

	@Test
	public void testDeleteAllColumns() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		long classPK = _counterLocalService.increment();

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column, classPK, RandomTestUtil.randomString());

		_expandoValueLocalService.deleteColumnValues(column.getColumnId());

		Assert.assertNull(
			_expandoRowLocalService.fetchExpandoRow(value.getRowId()));
	}

	@Test
	public void testDeleteOneColumn() throws Exception {
		ExpandoColumn column1 = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);
		ExpandoColumn column2 = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		long classPK = _counterLocalService.increment();

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column1, classPK, RandomTestUtil.randomString());

		ExpandoTestUtil.addValue(
			_expandoTable, column2, classPK, RandomTestUtil.randomString());

		_expandoValueLocalService.deleteColumnValues(column1.getColumnId());

		Assert.assertNotNull(
			_expandoRowLocalService.getExpandoRow(value.getRowId()));
	}

	@Test
	public void testGetDefaultColumnValue() throws Exception {
		String defaultValue = RandomTestUtil.randomString();

		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING_LOCALIZED,
			HashMapBuilder.put(
				_enLocale, defaultValue
			).build());

		column = _expandoColumnLocalService.getColumn(column.getColumnId());

		Map<Locale, String> data =
			(Map<Locale, String>)column.getDefaultValue();

		Assert.assertEquals(defaultValue, data.get(_enLocale));
	}

	@Test
	public void testGetNonexistingLocaleValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING_LOCALIZED);

		String enValue = RandomTestUtil.randomString();
		String ptValue = RandomTestUtil.randomString();

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column,
			HashMapBuilder.put(
				_enLocale, enValue
			).put(
				_ptLocale, ptValue
			).build(),
			_ptLocale);

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		Assert.assertEquals(_ptLocale, value.getDefaultLocale());

		List<Locale> availableLocales = value.getAvailableLocales();

		Assert.assertEquals(_ptLocale, availableLocales.get(0));
		Assert.assertEquals(_enLocale, availableLocales.get(1));

		Assert.assertEquals(enValue, value.getString(_enLocale));
		Assert.assertEquals(ptValue, value.getString(_frLocale));
		Assert.assertEquals(ptValue, value.getString(_ptLocale));
	}

	@Test
	public void testGetSerializableData() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING_ARRAY_LOCALIZED);

		String[] enValues = {
			RandomTestUtil.randomString() + ", " +
				RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + ", " + RandomTestUtil.randomString()
		};
		String[] ptValues = {
			RandomTestUtil.randomString() + ", " +
				RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + ", " + RandomTestUtil.randomString()
		};

		Map<Locale, String[]> dataMap = HashMapBuilder.put(
			_enLocale, enValues
		).put(
			_ptLocale, ptValues
		).build();

		long classPK = _counterLocalService.increment();

		ExpandoTestUtil.addValue(_expandoTable, column, classPK, dataMap);

		Serializable serializable = _expandoValueLocalService.getData(
			TestPropsValues.getCompanyId(),
			PortalUtil.getClassName(_classNameId), _expandoTable.getName(),
			column.getName(), classPK);

		Assert.assertTrue(serializable instanceof Map);

		dataMap = (Map<Locale, String[]>)serializable;

		Assert.assertArrayEquals(enValues, dataMap.get(_enLocale));
		Assert.assertArrayEquals(ptValues, dataMap.get(_ptLocale));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoValueLocalServiceTest.class);

	private long _classNameId;

	@Inject
	private CounterLocalService _counterLocalService;

	private Locale _enLocale;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoRowLocalService _expandoRowLocalService;

	@DeleteAfterTestRun
	private ExpandoTable _expandoTable;

	@Inject
	private ExpandoValueLocalService _expandoValueLocalService;

	@Inject
	private FinderCache _finderCache;

	private Locale _frLocale;
	private Locale _ptLocale;

}