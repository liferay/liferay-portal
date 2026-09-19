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

import java.util.Arrays;
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
			"latitude", 33.997696
		).put(
			"longitude", -117.844664
		);

		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column", ExpandoColumnConstants.GEOLOCATION);

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column,
			HtmlUtil.escape(geolocationJSONObject.toString()));

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		Assert.assertEquals(
			String.valueOf(geolocationJSONObject),
			String.valueOf(value.getGeolocationJSONObject()));
	}

	@Test
	public void testAddLocalizedStringArrayValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column",
			ExpandoColumnConstants.STRING_ARRAY_LOCALIZED);

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column,
			HashMapBuilder.put(
				_enLocale, new String[] {"one", "two", "three"}
			).put(
				_ptLocale, new String[] {"um", "dois", "tres"}
			).build());

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		Map<Locale, String[]> stringArrayMap = value.getStringArrayMap();

		String[] enValues = stringArrayMap.get(_enLocale);

		Assert.assertEquals(Arrays.toString(enValues), 3, enValues.length);
		Assert.assertEquals("two", enValues[1]);

		String[] ptValues = stringArrayMap.get(_ptLocale);

		Assert.assertEquals(Arrays.toString(ptValues), 3, ptValues.length);
		Assert.assertEquals("tres", ptValues[2]);
	}

	@Test
	public void testAddLocalizedStringValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column",
			ExpandoColumnConstants.STRING_LOCALIZED);

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column,
			HashMapBuilder.put(
				_enLocale, "Test"
			).put(
				_ptLocale, "Teste"
			).build());

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		Map<Locale, String> stringMap = value.getStringMap();

		Assert.assertEquals("Test", stringMap.get(_enLocale));
		Assert.assertEquals("Teste", stringMap.get(_ptLocale));
	}

	@Test
	public void testAddLocalizedStringValues() throws Exception {
		ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column One",
			ExpandoColumnConstants.STRING_LOCALIZED);
		ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column Two",
			ExpandoColumnConstants.STRING_LOCALIZED);
		ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column Three",
			ExpandoColumnConstants.STRING_LOCALIZED);

		Serializable data = HashMapBuilder.put(
			_enLocale, "one"
		).put(
			_ptLocale, "um"
		).build();

		long classPK = _counterLocalService.increment();

		ExpandoTestUtil.addValues(
			_expandoTable, classPK,
			new HashMapBuilder<>().<String, Serializable>put(
				"Test Column One", data
			).put(
				"Test Column Two",
				HashMapBuilder.put(
					_enLocale, "two"
				).build()
			).put(
				"Test Column Three", new HashMap<Locale, String>()
			).build());

		Assert.assertEquals(
			data,
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), "Test Column One", classPK));
		Assert.assertEquals(
			HashMapBuilder.put(
				_enLocale, "two"
			).build(),
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), "Test Column Two", classPK));
		Assert.assertEquals(
			new HashMap<Locale, String>(),
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), "Test Column Three", classPK));
	}

	@Test
	public void testAddNondefaultLocalizedStringValue() throws Exception {
		try {
			ExpandoTestUtil.addColumn(
				_expandoTable, "Test Column",
				ExpandoColumnConstants.STRING_LOCALIZED,
				HashMapBuilder.put(
					_ptLocale, "Teste"
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
				_expandoTable, "Test Column",
				ExpandoColumnConstants.STRING_LOCALIZED);

			ExpandoValue value = ExpandoTestUtil.addValue(
				_expandoTable, column,
				HashMapBuilder.put(
					_enLocale, "one"
				).put(
					_frLocale, "un"
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
			_expandoTable, "Test Column", ExpandoColumnConstants.STRING_ARRAY);

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column, new String[] {"one", "two, three"});

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		String[] data = value.getStringArray();

		Assert.assertEquals(Arrays.toString(data), 2, data.length);
		Assert.assertEquals("one", data[0]);
		Assert.assertEquals("two, three", data[1]);
	}

	@Test
	public void testAddValueWithFlushInBetween() throws Throwable {
		ExpandoColumn column1 = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column 1", ExpandoColumnConstants.STRING);

		ExpandoColumn column2 = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column 2", ExpandoColumnConstants.STRING);

		long classPK = _counterLocalService.increment();

		ExpandoTestUtil.addValues(
			_expandoTable, classPK,
			new HashMapBuilder<>().<String, Serializable>put(
				"Test Column 1", "column1-one"
			).<String, Serializable>put(
				"Test Column 2", "column2-one"
			).build());

		_entityCache.clearCache(ExpandoValueImpl.class);
		_finderCache.clearCache(ExpandoValueImpl.class);

		TransactionInvokerUtil.invoke(
			TransactionConfig.Factory.create(
				Propagation.REQUIRED, new Class<?>[] {Exception.class}),
			() -> {
				ExpandoTestUtil.addValue(
					_expandoTable, column1, classPK, "column1-two");

				ExpandoTestUtil.addValue(
					_expandoTable, column2, classPK, "column2-two");

				return null;
			});

		Assert.assertEquals(
			"column1-two",
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), "Test Column 1", classPK));
		Assert.assertEquals(
			"column2-two",
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), "Test Column 2", classPK));
	}

	@Test
	public void testAddValuesWithFlushInBetween() throws Throwable {
		ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column 1", ExpandoColumnConstants.STRING);
		ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column 2", ExpandoColumnConstants.STRING);

		long classPK = _counterLocalService.increment();

		ExpandoTestUtil.addValues(
			_expandoTable, classPK,
			new HashMapBuilder<>().<String, Serializable>put(
				"Test Column 1", "column1-one"
			).<String, Serializable>put(
				"Test Column 2", "column2-one"
			).build());

		_entityCache.clearCache(ExpandoValueImpl.class);
		_finderCache.clearCache(ExpandoValueImpl.class);

		TransactionInvokerUtil.invoke(
			TransactionConfig.Factory.create(
				Propagation.REQUIRED, new Class<?>[] {Exception.class}),
			() -> {
				ExpandoTestUtil.addValues(
					_expandoTable, classPK,
					new HashMapBuilder<>().<String, Serializable>put(
						"Test Column 1", "column1-two"
					).<String, Serializable>put(
						"Test Column 2", "column2-two"
					).build());

				return null;
			});

		Assert.assertEquals(
			"column1-two",
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), "Test Column 1", classPK));
		Assert.assertEquals(
			"column2-two",
			_expandoValueLocalService.getData(
				_expandoTable.getCompanyId(), _expandoTable.getClassName(),
				_expandoTable.getName(), "Test Column 2", classPK));
	}

	@Test
	public void testAddWrongValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column", ExpandoColumnConstants.STRING);

		Map<Locale, String> dataMap = HashMapBuilder.put(
			_enLocale, "one"
		).put(
			_ptLocale, "um"
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
			_expandoTable, "Test Column", ExpandoColumnConstants.STRING);

		long classPK = _counterLocalService.increment();

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column, classPK, "value");

		_expandoValueLocalService.deleteColumnValues(column.getColumnId());

		Assert.assertNull(
			_expandoRowLocalService.fetchExpandoRow(value.getRowId()));
	}

	@Test
	public void testDeleteOneColumn() throws Exception {
		ExpandoColumn column1 = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column 1", ExpandoColumnConstants.STRING);

		ExpandoColumn column2 = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column 2", ExpandoColumnConstants.STRING);

		long classPK = _counterLocalService.increment();

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column1, classPK, "value");

		ExpandoTestUtil.addValue(_expandoTable, column2, classPK, "value");

		_expandoValueLocalService.deleteColumnValues(column1.getColumnId());

		Assert.assertNotNull(_expandoRowLocalService.getRow(value.getRowId()));
	}

	@Test
	public void testGetDefaultColumnValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column",
			ExpandoColumnConstants.STRING_LOCALIZED,
			HashMapBuilder.put(
				_enLocale, "Test"
			).build());

		column = _expandoColumnLocalService.getColumn(column.getColumnId());

		Map<Locale, String> data =
			(Map<Locale, String>)column.getDefaultValue();

		Assert.assertEquals("Test", data.get(_enLocale));
	}

	@Test
	public void testGetNonexistingLocaleValue() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column",
			ExpandoColumnConstants.STRING_LOCALIZED);

		ExpandoValue value = ExpandoTestUtil.addValue(
			_expandoTable, column,
			HashMapBuilder.put(
				_enLocale, "one"
			).put(
				_ptLocale, "um"
			).build(),
			_ptLocale);

		value = _expandoValueLocalService.getExpandoValue(value.getValueId());

		Assert.assertEquals(_ptLocale, value.getDefaultLocale());

		List<Locale> availableLocales = value.getAvailableLocales();

		Assert.assertEquals(_ptLocale, availableLocales.get(0));
		Assert.assertEquals(_enLocale, availableLocales.get(1));

		Assert.assertEquals("um", value.getString(_ptLocale));
		Assert.assertEquals("one", value.getString(_enLocale));
		Assert.assertEquals("um", value.getString(_frLocale));
	}

	@Test
	public void testGetSerializableData() throws Exception {
		ExpandoColumn column = ExpandoTestUtil.addColumn(
			_expandoTable, "Test Column",
			ExpandoColumnConstants.STRING_ARRAY_LOCALIZED);

		Map<Locale, String[]> dataMap = HashMapBuilder.put(
			_enLocale, new String[] {"Hello, Joe", "Hi, Joe"}
		).put(
			_ptLocale, new String[] {"Ola, João", "Oi, João"}
		).build();

		long classPK = _counterLocalService.increment();

		ExpandoTestUtil.addValue(_expandoTable, column, classPK, dataMap);

		Serializable serializable = _expandoValueLocalService.getData(
			TestPropsValues.getCompanyId(),
			PortalUtil.getClassName(_classNameId), _expandoTable.getName(),
			column.getName(), classPK);

		Assert.assertTrue(serializable instanceof Map);

		dataMap = (Map<Locale, String[]>)serializable;

		String[] enValues = dataMap.get(_enLocale);

		Assert.assertEquals(Arrays.toString(enValues), 2, enValues.length);
		Assert.assertEquals("Hi, Joe", enValues[1]);
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