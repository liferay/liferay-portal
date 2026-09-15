/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.ExportImportConfigurationTemporarySwapper;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.exception.ListTypeDefinitionNameException;
import com.liferay.list.type.exception.ListTypeDefinitionSystemException;
import com.liferay.list.type.exception.NoSuchListTypeDefinitionException;
import com.liferay.list.type.exception.RequiredListTypeDefinitionException;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ListTypeDefinitionLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddListTypeDefinition() throws Exception {
		AssertUtils.assertFailure(
			ListTypeDefinitionNameException.class,
			"Name is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(LocaleUtil.US, ""), false,
				Collections.emptyList(), new ServiceContext()));
		AssertUtils.assertFailure(
			ListTypeDefinitionSystemException.class, false,
			"Only allowed bundles can add system list type definitions",
			this::_addSystemListTypeDefinition);

		ListTypeDefinition listTypeDefinition = _addListTypeDefinition();

		Assert.assertNotNull(listTypeDefinition);
		Assert.assertTrue(
			Validator.isNotNull(listTypeDefinition.getExternalReferenceCode()));
		Assert.assertTrue(Validator.isNotNull(listTypeDefinition.getName()));
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, listTypeDefinition.getStatus());
		Assert.assertNotNull(
			_listTypeDefinitionLocalService.fetchListTypeDefinition(
				listTypeDefinition.getListTypeDefinitionId()));
		Assert.assertEquals(
			1,
			_listTypeEntryLocalService.getListTypeEntriesCount(
				listTypeDefinition.getListTypeDefinitionId()));
	}

	@Test
	@TestInfo("LPD-55656")
	public void testAddOrUpdateListTypeEntries() throws Exception {
		String key = RandomTestUtil.randomString();

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false,
				Collections.singletonList(
					ListTypeEntryUtil.createListTypeEntry(key)),
				new ServiceContext());

		Assert.assertEquals(
			1,
			_listTypeEntryLocalService.getListTypeEntriesCount(
				listTypeDefinition.getListTypeDefinitionId()));

		listTypeDefinition =
			_listTypeDefinitionLocalService.updateListTypeDefinition(
				null, listTypeDefinition.getListTypeDefinitionId(),
				TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				Collections.singletonList(
					ListTypeEntryUtil.createListTypeEntry(key)),
				new ServiceContext());

		Assert.assertEquals(
			1,
			_listTypeEntryLocalService.getListTypeEntriesCount(
				listTypeDefinition.getListTypeDefinitionId()));
	}

	@Test
	public void testDeleteListTypeDefinition() throws Exception {
		ListTypeDefinition systemListTypeDefinition =
			_addSystemListTypeDefinition();

		AssertUtils.assertFailure(
			ListTypeDefinitionSystemException.class, false,
			"Only allowed bundles can delete system list type definitions",
			() -> _listTypeDefinitionLocalService.deleteListTypeDefinition(
				systemListTypeDefinition.getListTypeDefinitionId()));

		_testDeleteListTypeDefinition(
			systemListTypeDefinition.getListTypeDefinitionId());

		ListTypeDefinition listTypeDefinition = _addListTypeDefinition();

		ObjectField objectField = ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, StringUtil.randomId());

		objectField.setListTypeDefinitionId(
			listTypeDefinition.getListTypeDefinitionId());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, true, false,
				true, false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Test"), "Test", null, null,
				LocalizedMapUtil.getLocalizedMap("Tests"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.singletonList(objectField),
				Collections.emptyList(), new ServiceContext());

		AssertUtils.assertFailure(
			RequiredListTypeDefinitionException.class, null,
			() -> _listTypeDefinitionLocalService.deleteListTypeDefinition(
				listTypeDefinition.getListTypeDefinitionId()));

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		_testDeleteListTypeDefinition(
			listTypeDefinition.getListTypeDefinitionId());
	}

	@Test
	public void testGetOrAddEmptyListTypeDefinition() throws Throwable {

		// Lazy referencing disabled

		long companyId = TestPropsValues.getCompanyId();
		String externalReferenceCode = RandomTestUtil.randomString();
		long userId = TestPropsValues.getUserId();

		AssertUtils.assertFailure(
			NoSuchListTypeDefinitionException.class,
			String.format(
				"No ListTypeDefinition exists with the key {externalReference" +
					"Code=%s, companyId=%s}",
				externalReferenceCode, companyId),
			() ->
				_listTypeDefinitionLocalService.getOrAddEmptyListTypeDefinition(
					externalReferenceCode, companyId, userId,
					RandomTestUtil.randomBoolean()));

		// Lazy referencing enabled

		long exportImportConfigurationId = RandomTestUtil.randomLong();

		try (AutoCloseable autoCloseable =
				new ExportImportConfigurationTemporarySwapper(
					exportImportConfigurationId)) {

			ListTypeDefinition listTypeDefinition =
				_listTypeDefinitionLocalService.getOrAddEmptyListTypeDefinition(
					externalReferenceCode, companyId, userId,
					RandomTestUtil.randomBoolean());

			EntityCacheUtil.clearCache();

			Assert.assertNotNull(
				_listTypeDefinitionLocalService.fetchListTypeDefinition(
					listTypeDefinition.getListTypeDefinitionId()));

			Assert.assertEquals(
				externalReferenceCode,
				listTypeDefinition.getExternalReferenceCode());
			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, listTypeDefinition.getStatus());

			List<ExportImportReportEntry> exportImportReportEntries =
				_exportImportReportEntryLocalService.
					getExportImportReportEntries(
						companyId, exportImportConfigurationId);

			Assert.assertEquals(
				exportImportReportEntries.toString(), 1,
				exportImportReportEntries.size());
			Assert.assertTrue(
				ListUtil.exists(
					exportImportReportEntries,
					exportImportReportEntry ->
						Objects.equals(
							exportImportReportEntry.
								getClassExternalReferenceCode(),
							externalReferenceCode) &&
						(exportImportReportEntry.getType() ==
							ExportImportReportEntryConstants.TYPE_EMPTY)));

			listTypeDefinition =
				_listTypeDefinitionLocalService.updateListTypeDefinition(
					listTypeDefinition.getExternalReferenceCode(),
					listTypeDefinition.getListTypeDefinitionId(),
					listTypeDefinition.getUserId(),
					listTypeDefinition.getNameMap(), Collections.emptyList(),
					new ServiceContext());

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED,
				listTypeDefinition.getStatus());
		}
	}

	@Test
	public void testUpdateListTypeDefinition() throws Exception {
		ListTypeDefinition listTypeDefinition = _addListTypeDefinition();

		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		listTypeDefinition =
			_listTypeDefinitionLocalService.updateListTypeDefinition(
				externalReferenceCode,
				listTypeDefinition.getListTypeDefinitionId(),
				TestPropsValues.getUserId(),
				Collections.singletonMap(LocaleUtil.getDefault(), name),
				Arrays.asList(
					ListTypeEntryUtil.createListTypeEntry(
						RandomTestUtil.randomString()),
					ListTypeEntryUtil.createListTypeEntry(
						RandomTestUtil.randomString())),
				new ServiceContext());

		Assert.assertEquals(
			externalReferenceCode,
			listTypeDefinition.getExternalReferenceCode());
		Assert.assertEquals(
			name, listTypeDefinition.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, listTypeDefinition.getStatus());
		Assert.assertEquals(
			2,
			_listTypeEntryLocalService.getListTypeEntriesCount(
				listTypeDefinition.getListTypeDefinitionId()));

		listTypeDefinition =
			_listTypeDefinitionLocalService.updateListTypeDefinition(
				StringPool.BLANK, listTypeDefinition.getListTypeDefinitionId(),
				TestPropsValues.getUserId(),
				Collections.singletonMap(LocaleUtil.getDefault(), name),
				Collections.emptyList(), new ServiceContext());

		externalReferenceCode = listTypeDefinition.getExternalReferenceCode();

		Assert.assertFalse(externalReferenceCode.isEmpty());

		Assert.assertEquals(
			0,
			_listTypeEntryLocalService.getListTypeEntriesCount(
				listTypeDefinition.getListTypeDefinitionId()));

		ListTypeDefinition systemListTypeDefinition =
			_addSystemListTypeDefinition();

		externalReferenceCode = RandomTestUtil.randomString();

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.clear("liferay.mode");

		try {
			systemListTypeDefinition =
				_listTypeDefinitionLocalService.updateListTypeDefinition(
					externalReferenceCode,
					systemListTypeDefinition.getListTypeDefinitionId(),
					TestPropsValues.getUserId(),
					Collections.singletonMap(LocaleUtil.getDefault(), name),
					_listTypeEntryLocalService.getListTypeEntries(
						systemListTypeDefinition.getListTypeDefinitionId()),
					new ServiceContext());
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);
		}

		Assert.assertNotEquals(
			externalReferenceCode,
			systemListTypeDefinition.getExternalReferenceCode());

		Assert.assertEquals(
			name, systemListTypeDefinition.getName(LocaleUtil.getDefault()));
	}

	private ListTypeDefinition _addListTypeDefinition() throws Exception {
		return _listTypeDefinitionLocalService.addListTypeDefinition(
			null, TestPropsValues.getUserId(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			false,
			Collections.singletonList(
				ListTypeEntryUtil.createListTypeEntry(
					RandomTestUtil.randomString())),
			new ServiceContext());
	}

	private ListTypeDefinition _addSystemListTypeDefinition() throws Exception {
		return _listTypeDefinitionLocalService.addListTypeDefinition(
			null, TestPropsValues.getUserId(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			true,
			Collections.singletonList(
				ListTypeEntryUtil.createListTypeEntry(
					RandomTestUtil.randomString())),
			new ServiceContext());
	}

	private void _testDeleteListTypeDefinition(long listTypeDefinitionId)
		throws Exception {

		_listTypeDefinitionLocalService.deleteListTypeDefinition(
			listTypeDefinitionId);

		Assert.assertNull(
			_listTypeDefinitionLocalService.fetchListTypeDefinition(
				listTypeDefinitionId));
		Assert.assertEquals(
			0,
			_listTypeEntryLocalService.getListTypeEntriesCount(
				listTypeDefinitionId));
	}

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}