/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.text.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class BatchEnginePortletDataHandlerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		_fastDateFormatFactoryUtilMockedStatic = Mockito.mockStatic(
			FastDateFormatFactoryUtil.class);

		Mockito.when(
			FastDateFormatFactoryUtil.getSimpleDateFormat(Mockito.anyString())
		).thenReturn(
			_dateFormat
		);
	}

	@After
	public void tearDown() {
		_fastDateFormatFactoryUtilMockedStatic.close();
	}

	@Test
	public void testBuildExportParametersWithEndDate() {
		Date endDate = _getDate(0);

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_mockExportImportDescriptor(),
				_mockPortletDataContext(endDate, null, null), null);

		Assert.assertEquals(
			"dateModified le " + _dateFormat.format(endDate),
			parameters.get("filter"));
	}

	@Test
	public void testBuildExportParametersWithEndDateAndStartDate() {
		Date endDate = _getDate(0);
		Date startDate = _getDate(-1);

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_mockExportImportDescriptor(),
				_mockPortletDataContext(endDate, null, startDate), null);

		Assert.assertEquals(
			StringBundler.concat(
				"dateModified le ", _dateFormat.format(endDate),
				" and dateModified ge ", _dateFormat.format(startDate)),
			parameters.get("filter"));
	}

	@Test
	public void testBuildExportParametersWithItemClassName() {
		String itemClassName = RandomTestUtil.randomString();

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_mockExportImportDescriptor(itemClassName, null, null, null),
				_mockPortletDataContext(), null);

		Assert.assertEquals(itemClassName, parameters.get("itemClassName"));
	}

	@Test
	public void testBuildExportParametersWithItemModelName() {
		String itemModelName = RandomTestUtil.randomString();

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_mockExportImportDescriptor(null, itemModelName, null, null),
				_mockPortletDataContext(), null);

		Assert.assertEquals(itemModelName, parameters.get("itemModelName"));
	}

	@Test
	public void testBuildExportParametersWithNestedFields() {
		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_mockExportImportDescriptor(
					null, null, List.of("nestedField1", "nestedField2"), null),
				_mockPortletDataContext(), null);

		Assert.assertEquals(
			"customFields.attributeType,nestedField1,nestedField2",
			parameters.get("batchNestedFields"));

		parameters = BatchEnginePortletDataHandlerUtil.buildExportParameters(
			_mockExportImportDescriptor(
				null, null, List.of("nestedField1", "nestedField2"), null),
			_mockPortletDataContext(
				null,
				HashMapBuilder.put(
					PortletDataHandlerKeys.PERMISSIONS, new String[] {"true"}
				).build(),
				null),
			null);

		Assert.assertEquals(
			"customFields.attributeType,permissions,nestedField1,nestedField2",
			parameters.get("batchNestedFields"));
	}

	@Test
	public void testBuildExportParametersWithNoDates() {
		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_mockExportImportDescriptor(), _mockPortletDataContext(), null);

		Assert.assertNull(parameters.get("filter"));
	}

	@Test
	public void testBuildExportParametersWithParameters() {
		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_mockExportImportDescriptor(
					null, null, null,
					HashMapBuilder.<String, Serializable>put(
						"param1", "value1"
					).put(
						"param2", "value2"
					).build()),
				_mockPortletDataContext(), null);

		Assert.assertEquals("value1", parameters.get("param1"));
		Assert.assertEquals("value2", parameters.get("param2"));
	}

	@Test
	public void testBuildExportParametersWithSiteExternalReferenceCode() {
		String siteExternalReferenceCode = RandomTestUtil.randomString();

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_mockExportImportDescriptor(null, null, null, null),
				_mockPortletDataContext(), siteExternalReferenceCode);

		Assert.assertEquals(
			siteExternalReferenceCode,
			parameters.get("siteExternalReferenceCode"));
	}

	@Test
	public void testBuildExportParametersWithStartDate() {
		Date startDate = _getDate(-1);

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_mockExportImportDescriptor(),
				_mockPortletDataContext(null, null, startDate), null);

		Assert.assertEquals(
			"dateModified ge " + _dateFormat.format(startDate),
			parameters.get("filter"));
	}

	@Test
	public void testBuildImportParametersWithItemClassName() {
		String itemClassName = RandomTestUtil.randomString();

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildImportParameters(
				_mockExportImportDescriptor(itemClassName, null, null, null),
				_mockPortletDataContext(), null);

		Assert.assertEquals(itemClassName, parameters.get("itemClassName"));
	}

	@Test
	public void testBuildImportParametersWithItemModelName() {
		String itemModelName = RandomTestUtil.randomString();

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildImportParameters(
				_mockExportImportDescriptor(null, itemModelName, null, null),
				_mockPortletDataContext(), null);

		Assert.assertEquals(itemModelName, parameters.get("itemModelName"));
	}

	private Date _getDate(int days) {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.DATE, days);

		return calendar.getTime();
	}

	private ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
		_mockExportImportDescriptor() {

		return Mockito.mock(
			ExportImportVulcanBatchEngineTaskItemDelegate.
				ExportImportDescriptor.class);
	}

	private ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
		_mockExportImportDescriptor(
			String itemClassName, String itemModelName,
			List<String> nestedFields, Map<String, Serializable> parameters) {

		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor = _mockExportImportDescriptor();

		Mockito.when(
			exportImportDescriptor.getItemClassName()
		).thenReturn(
			itemClassName
		);

		Mockito.when(
			exportImportDescriptor.getItemModelName()
		).thenReturn(
			itemModelName
		);

		Mockito.when(
			exportImportDescriptor.getNestedFields()
		).thenReturn(
			nestedFields
		);

		Mockito.when(
			exportImportDescriptor.getParameters(Mockito.any())
		).thenReturn(
			parameters
		);

		return exportImportDescriptor;
	}

	private PortletDataContext _mockPortletDataContext() {
		return Mockito.mock(PortletDataContext.class);
	}

	private PortletDataContext _mockPortletDataContext(
		Date endDate, Map<String, String[]> parameterMap, Date startDate) {

		PortletDataContext portletDataContext = _mockPortletDataContext();

		Mockito.when(
			portletDataContext.getEndDate()
		).thenReturn(
			endDate
		);

		Mockito.when(
			portletDataContext.getParameterMap()
		).thenReturn(
			(parameterMap == null) ? new HashMap<>() : parameterMap
		);

		Mockito.when(
			portletDataContext.getStartDate()
		).thenReturn(
			startDate
		);

		return portletDataContext;
	}

	private DateFormat _dateFormat;
	private MockedStatic<FastDateFormatFactoryUtil>
		_fastDateFormatFactoryUtilMockedStatic;

}