/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class ExportImportReportEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@Test
	@TestInfo("LPD-77587")
	public void testAddEmptyExportImportReportEntry() throws Exception {
		int count =
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount();

		long groupId = RandomTestUtil.randomLong();
		long companyId = TestPropsValues.getCompanyId();
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long exportImportConfigurationId = RandomTestUtil.randomLong();
		String modelNameLanguageKey = RandomTestUtil.randomString();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.addExportImportReportEntry(
				groupId, companyId, classExternalReferenceCode, classNameId, 0,
				exportImportConfigurationId,
				ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
				modelNameLanguageKey);

		Assert.assertEquals(groupId, exportImportReportEntry.getGroupId());
		Assert.assertEquals(companyId, exportImportReportEntry.getCompanyId());
		Assert.assertEquals(
			classExternalReferenceCode,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			classNameId, exportImportReportEntry.getClassNameId());
		Assert.assertEquals(
			exportImportConfigurationId,
			exportImportReportEntry.getExportImportConfigurationId());
		Assert.assertEquals(
			modelNameLanguageKey,
			exportImportReportEntry.getModelNameLanguageKey());
		Assert.assertEquals(
			_getEmptyReportEntryErrorMessage(
				classExternalReferenceCode, modelNameLanguageKey),
			exportImportReportEntry.getErrorMessage());
		Assert.assertNull(exportImportReportEntry.getErrorStacktrace());
		Assert.assertEquals(
			ExportImportReportEntryConstants.ORIGIN_STAGING,
			exportImportReportEntry.getOrigin());
		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED,
			exportImportReportEntry.getStatus());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_EMPTY,
			exportImportReportEntry.getType());

		Assert.assertEquals(
			count + 1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount());
	}

	@Test
	public void testAddErrorExportImportReportEntry() throws Exception {
		int count =
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount();

		long groupId = RandomTestUtil.randomLong();
		long companyId = TestPropsValues.getCompanyId();
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long classPK = RandomTestUtil.randomLong();
		long exportImportConfigurationId = RandomTestUtil.randomLong();
		String errorMessage = RandomTestUtil.randomString();
		String errorStacktrace = RandomTestUtil.randomString();
		String modelNameLanguageKey = RandomTestUtil.randomString();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.addExportImportReportEntry(
				groupId, companyId, classExternalReferenceCode, classNameId,
				classPK, exportImportConfigurationId,
				ExportImportReportEntryConstants.TYPE_ERROR, errorMessage,
				errorStacktrace, modelNameLanguageKey);

		Assert.assertEquals(groupId, exportImportReportEntry.getGroupId());
		Assert.assertEquals(companyId, exportImportReportEntry.getCompanyId());
		Assert.assertEquals(
			classExternalReferenceCode,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			classNameId, exportImportReportEntry.getClassNameId());
		Assert.assertEquals(classPK, exportImportReportEntry.getClassPK());
		Assert.assertEquals(
			exportImportConfigurationId,
			exportImportReportEntry.getExportImportConfigurationId());
		Assert.assertEquals(
			errorMessage, exportImportReportEntry.getErrorMessage());
		Assert.assertEquals(
			errorStacktrace, exportImportReportEntry.getErrorStacktrace());
		Assert.assertEquals(
			modelNameLanguageKey,
			exportImportReportEntry.getModelNameLanguageKey());
		Assert.assertEquals(
			ExportImportReportEntryConstants.ORIGIN_STAGING,
			exportImportReportEntry.getOrigin());
		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED,
			exportImportReportEntry.getStatus());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_ERROR,
			exportImportReportEntry.getType());

		Assert.assertEquals(
			count + 1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount());
	}

	@Test
	@TestInfo("LPD-65084")
	public void testAddMissingReferenceExportImportReportEntry()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long exportImportConfigurationId = RandomTestUtil.randomLong();
		String modelNameLanguageKey = RandomTestUtil.randomString();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.addExportImportReportEntry(
				group.getGroupId(), TestPropsValues.getCompanyId(),
				classExternalReferenceCode, classNameId, 0,
				exportImportConfigurationId,
				ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE, null,
				null, modelNameLanguageKey);

		Assert.assertEquals(
			group.getGroupId(), exportImportReportEntry.getGroupId());
		Assert.assertEquals(
			StringBundler.concat(
				"Missing Reference: Entity ", modelNameLanguageKey,
				" with external reference code ", classExternalReferenceCode,
				" in scope ", group.getGroupId(),
				" was not found. Please ensure the referenced entity is ",
				"imported."),
			exportImportReportEntry.getErrorMessage());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE,
			exportImportReportEntry.getType());
	}

	@Test
	public void testGetExportImportReportEntries() throws Exception {
		long exportImportConfigurationId = RandomTestUtil.randomLong();

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(), exportImportConfigurationId);

		Assert.assertTrue(exportImportReportEntries.isEmpty());

		_exportImportReportEntryLocalService.addExportImportReportEntry(
			RandomTestUtil.randomLong(), TestPropsValues.getCompanyId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(), 0,
			exportImportConfigurationId,
			ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
			RandomTestUtil.randomString());
		_exportImportReportEntryLocalService.addExportImportReportEntry(
			RandomTestUtil.randomLong(), TestPropsValues.getCompanyId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(), 0,
			RandomTestUtil.randomLong(),
			ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
			RandomTestUtil.randomString());

		_exportImportReportEntryLocalService.addExportImportReportEntry(
			RandomTestUtil.randomLong(), _company.getCompanyId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(), 0,
			exportImportConfigurationId,
			ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
			RandomTestUtil.randomString());
		_exportImportReportEntryLocalService.addExportImportReportEntry(
			RandomTestUtil.randomLong(), _company.getCompanyId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(), 0,
			RandomTestUtil.randomLong(),
			ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
			RandomTestUtil.randomString());

		exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(), exportImportConfigurationId);

		Assert.assertEquals(
			exportImportReportEntries.toString(), 1,
			exportImportReportEntries.size());
	}

	@Test
	public void testGetExportImportReportEntriesCount() throws Exception {
		long exportImportConfigurationId = RandomTestUtil.randomLong();

		Assert.assertEquals(
			0,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount(
					TestPropsValues.getCompanyId(),
					exportImportConfigurationId));

		_exportImportReportEntryLocalService.addExportImportReportEntry(
			RandomTestUtil.randomLong(), TestPropsValues.getCompanyId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(), 0,
			exportImportConfigurationId,
			ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
			RandomTestUtil.randomString());

		_exportImportReportEntryLocalService.addExportImportReportEntry(
			RandomTestUtil.randomLong(), TestPropsValues.getCompanyId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(), 0,
			RandomTestUtil.randomLong(),
			ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
			RandomTestUtil.randomString());

		_exportImportReportEntryLocalService.addExportImportReportEntry(
			RandomTestUtil.randomLong(), _company.getCompanyId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(), 0,
			exportImportConfigurationId,
			ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
			RandomTestUtil.randomString());

		_exportImportReportEntryLocalService.addExportImportReportEntry(
			RandomTestUtil.randomLong(), _company.getCompanyId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(), 0,
			RandomTestUtil.randomLong(),
			ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
			RandomTestUtil.randomString());

		Assert.assertEquals(
			1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount(
					TestPropsValues.getCompanyId(),
					exportImportConfigurationId));
	}

	@Test
	public void testGetOrAddEmptyExportImportReportEntry() throws Exception {
		int count =
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount();

		long groupId = RandomTestUtil.randomLong();
		long companyId = TestPropsValues.getCompanyId();
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long exportImportConfigurationId = RandomTestUtil.randomLong();
		String modelNameLanguageKey = RandomTestUtil.randomString();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId,
					ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
					modelNameLanguageKey);

		Assert.assertEquals(groupId, exportImportReportEntry.getGroupId());
		Assert.assertEquals(companyId, exportImportReportEntry.getCompanyId());
		Assert.assertEquals(
			classExternalReferenceCode,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			classNameId, exportImportReportEntry.getClassNameId());
		Assert.assertEquals(
			exportImportConfigurationId,
			exportImportReportEntry.getExportImportConfigurationId());
		Assert.assertEquals(
			_getEmptyReportEntryErrorMessage(
				classExternalReferenceCode, modelNameLanguageKey),
			exportImportReportEntry.getErrorMessage());
		Assert.assertNull(exportImportReportEntry.getErrorStacktrace());
		Assert.assertEquals(
			modelNameLanguageKey,
			exportImportReportEntry.getModelNameLanguageKey());
		Assert.assertEquals(
			ExportImportReportEntryConstants.ORIGIN_STAGING,
			exportImportReportEntry.getOrigin());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_EMPTY,
			exportImportReportEntry.getType());
		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED,
			exportImportReportEntry.getStatus());

		Assert.assertEquals(
			count + 1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount());

		ExportImportReportEntry getExportImportReportEntry =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId,
					ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
					modelNameLanguageKey);

		Assert.assertEquals(
			exportImportReportEntry.getExportImportReportEntryId(),
			getExportImportReportEntry.getExportImportReportEntryId());

		Assert.assertEquals(
			count + 1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount());
	}

	@Test
	public void testGetOrAddErrorExportImportReportEntry() throws Exception {
		int count =
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount();

		long groupId = RandomTestUtil.randomLong();
		long companyId = TestPropsValues.getCompanyId();
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long classPK = RandomTestUtil.randomLong();
		long exportImportConfigurationId = RandomTestUtil.randomLong();
		String errorMessage = RandomTestUtil.randomString();
		String errorStackTrace = RandomTestUtil.randomString();
		String modelNameLanguageKey = RandomTestUtil.randomString();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					classPK, exportImportConfigurationId,
					ExportImportReportEntryConstants.TYPE_ERROR, errorMessage,
					errorStackTrace, modelNameLanguageKey);

		Assert.assertEquals(groupId, exportImportReportEntry.getGroupId());
		Assert.assertEquals(companyId, exportImportReportEntry.getCompanyId());
		Assert.assertEquals(
			classExternalReferenceCode,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			classNameId, exportImportReportEntry.getClassNameId());
		Assert.assertEquals(classPK, exportImportReportEntry.getClassPK());
		Assert.assertEquals(
			exportImportConfigurationId,
			exportImportReportEntry.getExportImportConfigurationId());
		Assert.assertEquals(
			errorMessage, exportImportReportEntry.getErrorMessage());
		Assert.assertEquals(
			errorStackTrace, exportImportReportEntry.getErrorStacktrace());
		Assert.assertEquals(
			modelNameLanguageKey,
			exportImportReportEntry.getModelNameLanguageKey());
		Assert.assertEquals(
			ExportImportReportEntryConstants.ORIGIN_STAGING,
			exportImportReportEntry.getOrigin());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_ERROR,
			exportImportReportEntry.getType());
		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED,
			exportImportReportEntry.getStatus());

		Assert.assertEquals(
			count + 1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount());

		ExportImportReportEntry getExportImportReportEntry =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					classPK, exportImportConfigurationId,
					ExportImportReportEntryConstants.TYPE_ERROR, errorMessage,
					errorStackTrace, modelNameLanguageKey);

		Assert.assertEquals(
			exportImportReportEntry.getExportImportReportEntryId(),
			getExportImportReportEntry.getExportImportReportEntryId());

		Assert.assertEquals(
			count + 1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount());
	}

	@Test
	@TestInfo("LPD-65084")
	public void testGetOrAddMissingReferenceExportImportReportEntry()
		throws Exception {

		int count =
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount();

		long groupId = RandomTestUtil.randomLong();
		long companyId = TestPropsValues.getCompanyId();
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long exportImportConfigurationId = RandomTestUtil.randomLong();
		String modelNameLanguageKey = RandomTestUtil.randomString();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId,
					ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE,
					null, null, modelNameLanguageKey);

		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE,
			exportImportReportEntry.getType());

		Assert.assertEquals(
			count + 1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount());

		ExportImportReportEntry duplicateExportImportReportEntry =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId,
					ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE,
					null, null, modelNameLanguageKey);

		Assert.assertEquals(
			exportImportReportEntry.getExportImportReportEntryId(),
			duplicateExportImportReportEntry.getExportImportReportEntryId());

		Assert.assertEquals(
			count + 1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount());
	}

	@Test
	public void testResolveEmptyExportImportReportEntries() throws Exception {
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long companyId = TestPropsValues.getCompanyId();
		long exportImportConfigurationId1 = RandomTestUtil.randomLong();
		long exportImportConfigurationId2 = RandomTestUtil.randomLong();
		long exportImportConfigurationId3 = RandomTestUtil.randomLong();
		long groupId = TestPropsValues.getGroupId();

		ExportImportReportEntry exportImportReportEntry1 =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId1,
					ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
					RandomTestUtil.randomString());
		ExportImportReportEntry exportImportReportEntry2 =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId2,
					ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
					RandomTestUtil.randomString());
		ExportImportReportEntry exportImportReportEntry3 =
			_updateStatusResolved(
				_exportImportReportEntryLocalService.
					getOrAddExportImportReportEntry(
						groupId, companyId, classExternalReferenceCode,
						classNameId, 0, exportImportConfigurationId3,
						ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
						RandomTestUtil.randomString()));

		ExportImportReportEntry randomExportImportReportEntry1 =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, RandomTestUtil.randomString(),
					classNameId, 0, RandomTestUtil.randomLong(),
					ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
					RandomTestUtil.randomString());
		ExportImportReportEntry randomExportImportReportEntry2 =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode,
					RandomTestUtil.randomLong(), 0, RandomTestUtil.randomLong(),
					ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
					RandomTestUtil.randomString());
		ExportImportReportEntry randomExportImportReportEntry3 =
			_updateStatusResolved(
				_exportImportReportEntryLocalService.
					getOrAddExportImportReportEntry(
						groupId, companyId, RandomTestUtil.randomString(),
						classNameId, 0, RandomTestUtil.randomLong(),
						ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
						RandomTestUtil.randomString()));
		ExportImportReportEntry randomExportImportReportEntry4 =
			_updateStatusResolved(
				_exportImportReportEntryLocalService.
					getOrAddExportImportReportEntry(
						groupId, companyId, classExternalReferenceCode,
						RandomTestUtil.randomLong(), 0,
						RandomTestUtil.randomLong(),
						ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
						RandomTestUtil.randomString()));

		Long originalExportImportConfigurationId =
			ExportImportThreadLocal.getExportImportConfigurationId();

		ExportImportThreadLocal.setExportImportConfigurationId(
			exportImportConfigurationId1);

		_exportImportReportEntryLocalService.
			resolveEmptyExportImportReportEntries(
				groupId, companyId, classExternalReferenceCode, classNameId);

		Assert.assertNull(
			_exportImportReportEntryLocalService.fetchExportImportReportEntry(
				exportImportReportEntry1.getExportImportReportEntryId()));

		_assertStatus(
			exportImportReportEntry2,
			ExportImportReportEntryConstants.STATUS_RESOLVED, true);
		_assertStatus(
			exportImportReportEntry3,
			ExportImportReportEntryConstants.STATUS_RESOLVED, false);
		_assertStatus(
			randomExportImportReportEntry1,
			ExportImportReportEntryConstants.STATUS_UNRESOLVED, false);
		_assertStatus(
			randomExportImportReportEntry2,
			ExportImportReportEntryConstants.STATUS_UNRESOLVED, false);
		_assertStatus(
			randomExportImportReportEntry3,
			ExportImportReportEntryConstants.STATUS_RESOLVED, false);
		_assertStatus(
			randomExportImportReportEntry4,
			ExportImportReportEntryConstants.STATUS_RESOLVED, false);

		ExportImportThreadLocal.setExportImportConfigurationId(
			originalExportImportConfigurationId);
	}

	@Test
	public void testResolveMissingReferenceExportImportReportEntries()
		throws Exception {

		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long companyId = TestPropsValues.getCompanyId();
		long exportImportConfigurationId1 = RandomTestUtil.randomLong();
		long groupId = TestPropsValues.getGroupId();

		ExportImportReportEntry exportImportReportEntry1 =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId1,
					ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE,
					null, null, RandomTestUtil.randomString());

		long exportImportConfigurationId2 = RandomTestUtil.randomLong();

		ExportImportReportEntry exportImportReportEntry2 =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId2,
					ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE,
					null, null, RandomTestUtil.randomString());

		ExportImportReportEntry randomExportImportReportEntry1 =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId1,
					ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
					RandomTestUtil.randomString());
		ExportImportReportEntry randomExportImportReportEntry2 =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, exportImportConfigurationId1,
					ExportImportReportEntryConstants.TYPE_WARNING,
					RandomTestUtil.randomString(), null,
					RandomTestUtil.randomString());
		ExportImportReportEntry randomExportImportReportEntry3 =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, RandomTestUtil.randomString(),
					classNameId, 0, exportImportConfigurationId1,
					ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE,
					null, null, RandomTestUtil.randomString());

		Long originalExportImportConfigurationId =
			ExportImportThreadLocal.getExportImportConfigurationId();

		ExportImportThreadLocal.setExportImportConfigurationId(
			exportImportConfigurationId1);

		_exportImportReportEntryLocalService.
			resolveMissingReferenceExportImportReportEntries(
				groupId, companyId, classExternalReferenceCode, classNameId);

		Assert.assertNull(
			_exportImportReportEntryLocalService.fetchExportImportReportEntry(
				exportImportReportEntry1.getExportImportReportEntryId()));

		_assertStatus(
			exportImportReportEntry2,
			ExportImportReportEntryConstants.STATUS_RESOLVED, true);
		_assertStatus(
			randomExportImportReportEntry1,
			ExportImportReportEntryConstants.STATUS_UNRESOLVED, false);
		_assertStatus(
			randomExportImportReportEntry2,
			ExportImportReportEntryConstants.STATUS_UNRESOLVED, false);
		_assertStatus(
			randomExportImportReportEntry3,
			ExportImportReportEntryConstants.STATUS_UNRESOLVED, false);

		ExportImportThreadLocal.setExportImportConfigurationId(
			originalExportImportConfigurationId);
	}

	private void _assertStatus(
			ExportImportReportEntry exportImportReportEntry, int status,
			boolean updated)
		throws Exception {

		ExportImportReportEntry actualExportImportReportEntry =
			_exportImportReportEntryLocalService.getExportImportReportEntry(
				exportImportReportEntry.getExportImportReportEntryId());

		long mvccVersion = exportImportReportEntry.getMvccVersion();

		Assert.assertEquals(
			updated ? mvccVersion + 1 : mvccVersion,
			actualExportImportReportEntry.getMvccVersion());

		Assert.assertEquals(status, actualExportImportReportEntry.getStatus());
	}

	private String _getEmptyReportEntryErrorMessage(
		String classExternalReferenceCode, String modelNameLanguageKey) {

		return StringBundler.concat(
			"The ", modelNameLanguageKey, " with external reference code ",
			classExternalReferenceCode,
			" was not found. An empty shell was created.");
	}

	private ExportImportReportEntry _updateStatusResolved(
		ExportImportReportEntry exportImportReportEntry) {

		exportImportReportEntry.setStatus(
			ExportImportReportEntryConstants.STATUS_RESOLVED);

		return _exportImportReportEntryLocalService.
			updateExportImportReportEntry(exportImportReportEntry);
	}

	private static Company _company;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

}