/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler;

import com.liferay.changeset.service.ChangesetEntryLocalService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;

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
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(), _mockGroupLocalService(null),
				_mockPortletDataContext(endDate, null, null),
				_getStagingGroupHelper(false));

		Assert.assertEquals(
			"dateModified le " + _dateFormat.format(endDate),
			parameters.get("filter"));
	}

	@Test
	public void testBuildExportParametersWithEndDateAndFilterParameter() {
		Date endDate = _getDate(0);

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(
					null, null,
					HashMapBuilder.<String, Serializable>put(
						"filter", "param1 eq value1"
					).put(
						"param2", "value2"
					).build()),
				_mockGroupLocalService(null),
				_mockPortletDataContext(endDate, null, null),
				_getStagingGroupHelper(false));

		Assert.assertEquals(
			StringBundler.concat(
				"dateModified le ", _dateFormat.format(endDate),
				" and (param1 eq value1)"),
			parameters.get("filter"));
		Assert.assertEquals("value2", parameters.get("param2"));
	}

	@Test
	public void testBuildExportParametersWithEndDateAndStartDateAndFilterParameter() {
		Date endDate = _getDate(0);
		Date startDate = _getDate(-1);

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(
					null, null,
					HashMapBuilder.<String, Serializable>put(
						"filter", "param1 eq value1"
					).put(
						"param2", "value2"
					).build()),
				_mockGroupLocalService(null),
				_mockPortletDataContext(endDate, null, startDate),
				_getStagingGroupHelper(false));

		Assert.assertEquals(
			StringBundler.concat(
				"dateModified le ", _dateFormat.format(endDate),
				" and dateModified ge ", _dateFormat.format(startDate),
				" and (param1 eq value1)"),
			parameters.get("filter"));
		Assert.assertEquals("value2", parameters.get("param2"));
	}

	@Test
	public void testBuildExportParametersWithModelClassName() {
		String modelClassName = RandomTestUtil.randomString();

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(modelClassName, null, null),
				_mockGroupLocalService(null), _mockPortletDataContext(),
				_getStagingGroupHelper(false));

		Assert.assertEquals(modelClassName, parameters.get("modelClassName"));
	}

	@Test
	public void testBuildExportParametersWithNestedFields() {
		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(
					null, List.of("nestedField1", "nestedField2"), null),
				_mockGroupLocalService(null), _mockPortletDataContext(),
				_getStagingGroupHelper(false));

		Assert.assertEquals(
			"customFields.attributeType,nestedField1,nestedField2",
			parameters.get("batchNestedFields"));

		parameters = BatchEnginePortletDataHandlerUtil.buildExportParameters(
			_changesetEntryLocalService, _classNameLocalService,
			_mockExportImportDescriptor(
				null, List.of("nestedField1", "nestedField2"), null),
			_mockGroupLocalService(null),
			_mockPortletDataContext(
				null,
				HashMapBuilder.put(
					PortletDataHandlerKeys.COMMENTS, new String[] {"true"}
				).put(
					PortletDataHandlerKeys.PERMISSIONS, new String[] {"true"}
				).build(),
				null),
			_getStagingGroupHelper(false));

		Assert.assertEquals(
			"customFields.attributeType,systemProperties.comments," +
				"permissions,nestedField1,nestedField2",
			parameters.get("batchNestedFields"));
	}

	@Test
	public void testBuildExportParametersWithNoDates() {
		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(), _mockGroupLocalService(null),
				_mockPortletDataContext(), _getStagingGroupHelper(false));

		Assert.assertEquals(StringPool.BLANK, parameters.get("filter"));
	}

	@Test
	public void testBuildExportParametersWithParameters() {
		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(
					null, null,
					HashMapBuilder.<String, Serializable>put(
						"filter", "param1 eq value1"
					).put(
						"param1", "value1"
					).put(
						"param2", "value2"
					).build()),
				_mockGroupLocalService(null), _mockPortletDataContext(),
				_getStagingGroupHelper(false));

		Assert.assertEquals("(param1 eq value1)", parameters.get("filter"));
		Assert.assertEquals("value1", parameters.get("param1"));
		Assert.assertEquals("value2", parameters.get("param2"));
	}

	@Test
	public void testBuildExportParametersWithStartDate() {
		Date startDate = _getDate(-1);

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(), _mockGroupLocalService(null),
				_mockPortletDataContext(null, null, startDate),
				_getStagingGroupHelper(false));

		Assert.assertEquals(
			"dateModified ge " + _dateFormat.format(startDate),
			parameters.get("filter"));
	}

	@Test
	public void testBuildExportParametersWithStartDateAndFilterParameter() {
		Date startDate = _getDate(-1);

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(
					null, null,
					HashMapBuilder.<String, Serializable>put(
						"filter", "param1 eq value1"
					).put(
						"param2", "value2"
					).build()),
				_mockGroupLocalService(null),
				_mockPortletDataContext(null, null, startDate),
				_getStagingGroupHelper(false));

		Assert.assertEquals(
			StringBundler.concat(
				"dateModified ge ", _dateFormat.format(startDate),
				" and (param1 eq value1)"),
			parameters.get("filter"));
		Assert.assertEquals("value2", parameters.get("param2"));
	}

	@Test
	public void testBuildImportParametersWithBatchRestrictFields() {
		Assert.assertNull(
			_getBatchRestrictFields(
				HashMapBuilder.put(
					PortletDataHandlerKeys.COMMENTS, new String[] {"true"}
				).put(
					PortletDataHandlerKeys.PERMISSIONS, new String[] {"true"}
				).build()));
		Assert.assertEquals(
			"systemProperties.comments",
			_getBatchRestrictFields(
				HashMapBuilder.put(
					PortletDataHandlerKeys.COMMENTS, new String[] {"false"}
				).put(
					PortletDataHandlerKeys.PERMISSIONS, new String[] {"true"}
				).build()));
		Assert.assertEquals(
			"systemProperties.comments,permissions",
			_getBatchRestrictFields(
				HashMapBuilder.put(
					PortletDataHandlerKeys.COMMENTS, new String[] {"false"}
				).build()));
		Assert.assertEquals(
			"systemProperties.comments,permissions",
			_getBatchRestrictFields(
				HashMapBuilder.put(
					PortletDataHandlerKeys.COMMENTS, new String[] {"false"}
				).put(
					PortletDataHandlerKeys.PERMISSIONS, new String[] {"false"}
				).build()));
		Assert.assertEquals(
			"systemProperties.comments,permissions",
			_getBatchRestrictFields(
				HashMapBuilder.put(
					PortletDataHandlerKeys.PERMISSIONS, new String[] {"false"}
				).build()));
		Assert.assertEquals(
			"permissions",
			_getBatchRestrictFields(
				HashMapBuilder.put(
					PortletDataHandlerKeys.COMMENTS, new String[] {"true"}
				).put(
					PortletDataHandlerKeys.PERMISSIONS, new String[] {"false"}
				).build()));
	}

	@Test
	public void testBuildImportParametersWithModelClassName() {
		String modelClassName = RandomTestUtil.randomString();

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildImportParameters(
				_mockExportImportDescriptor(modelClassName, null, null),
				_mockGroupLocalService(null), _mockPortletDataContext(),
				_getStagingGroupHelper(false));

		Assert.assertEquals(modelClassName, parameters.get("modelClassName"));
	}

	@Test
	public void testBuildParametersWithGroup() {
		_testBuildParametersWithGroup(false);
		_testBuildParametersWithGroup(true);
	}

	private Serializable _getBatchRestrictFields(
		Map<String, String[]> parameterMap) {

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildImportParameters(
				_mockExportImportDescriptor(), _mockGroupLocalService(null),
				_mockPortletDataContext(null, parameterMap, null),
				_getStagingGroupHelper(false));

		return parameters.get("batchRestrictFields");
	}

	private Date _getDate(int days) {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.DATE, days);

		return calendar.getTime();
	}

	private StagingGroupHelper _getStagingGroupHelper(boolean companyGroup) {
		StagingGroupHelper stagingGroupHelper = Mockito.mock(
			StagingGroupHelper.class);

		Mockito.when(
			stagingGroupHelper.isCompanyGroup(Mockito.any(Group.class))
		).thenReturn(
			companyGroup
		);

		return stagingGroupHelper;
	}

	private ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
		_mockExportImportDescriptor() {

		return Mockito.mock(
			ExportImportVulcanBatchEngineTaskItemDelegate.
				ExportImportDescriptor.class);
	}

	private ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
		_mockExportImportDescriptor(
			String modelClassName, List<String> nestedFields,
			Map<String, Serializable> parameters) {

		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor = _mockExportImportDescriptor();

		Mockito.when(
			exportImportDescriptor.getModelClassName()
		).thenReturn(
			modelClassName
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

	private Group _mockGroup(String externalReferenceCode, long groupId) {
		Group group = Mockito.mock(Group.class);

		Mockito.doReturn(
			externalReferenceCode
		).when(
			group
		).getExternalReferenceCode();

		Mockito.doReturn(
			groupId
		).when(
			group
		).getGroupId();

		return group;
	}

	private GroupLocalService _mockGroupLocalService(Group group) {
		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		Mockito.doReturn(
			group
		).when(
			groupLocalService
		).fetchGroup(
			Mockito.anyLong()
		);

		return groupLocalService;
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

	private void _testBuildParametersWithGroup(boolean companyGroup) {
		String siteExternalReferenceCode = RandomTestUtil.randomString();
		long siteId = RandomTestUtil.randomLong();

		Group group = _mockGroup(siteExternalReferenceCode, siteId);

		Map<String, Serializable> parameters =
			BatchEnginePortletDataHandlerUtil.buildExportParameters(
				_changesetEntryLocalService, _classNameLocalService,
				_mockExportImportDescriptor(), _mockGroupLocalService(group),
				_mockPortletDataContext(null, null, null),
				_getStagingGroupHelper(companyGroup));

		String expectedSiteExternalReferenceCode =
			companyGroup ? null : siteExternalReferenceCode;

		Assert.assertEquals(
			expectedSiteExternalReferenceCode,
			parameters.get("siteExternalReferenceCode"));

		Long expectedSiteId = companyGroup ? null : siteId;

		Assert.assertEquals(expectedSiteId, parameters.get("siteId"));

		parameters = BatchEnginePortletDataHandlerUtil.buildImportParameters(
			_mockExportImportDescriptor(), _mockGroupLocalService(group),
			_mockPortletDataContext(null, null, null),
			_getStagingGroupHelper(companyGroup));

		Assert.assertEquals(
			expectedSiteExternalReferenceCode,
			parameters.get("siteExternalReferenceCode"));
		Assert.assertEquals(expectedSiteId, parameters.get("siteId"));
	}

	private final ChangesetEntryLocalService _changesetEntryLocalService =
		Mockito.mock(ChangesetEntryLocalService.class);
	private final ClassNameLocalService _classNameLocalService = Mockito.mock(
		ClassNameLocalService.class);
	private DateFormat _dateFormat;
	private MockedStatic<FastDateFormatFactoryUtil>
		_fastDateFormatFactoryUtilMockedStatic;

}