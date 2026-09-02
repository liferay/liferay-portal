/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class DLFileEntryTypeStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testExportDLFileEntryType() throws Exception {
		initExport();

		DLFileEntryType dlFileEntryType = _addSystemFileEntryType(
			stagingGroup, TestPropsValues.getUserId(),
			RandomTestUtil.randomString());

		Assert.assertNotEquals(
			UserLocalServiceUtil.getGuestUserId(dlFileEntryType.getCompanyId()),
			dlFileEntryType.getUserId());

		Map<String, String> referenceAttributes =
			_getStagedModelDataHandler().getReferenceAttributes(
				portletDataContext, dlFileEntryType);

		Assert.assertTrue(
			referenceAttributes.toString(),
			GetterUtil.getBoolean(referenceAttributes.get("preloaded")));
	}

	@Test
	public void testImportDLFileEntryType() throws Exception {
		String name = RandomTestUtil.randomString();

		DLFileEntryType dlFileEntryType = _addSystemFileEntryType(
			stagingGroup, TestPropsValues.getUserId(), name);

		initExport();

		try {
			ExportImportThreadLocal.setPortletExportInProcess(true);

			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, dlFileEntryType);
		}
		finally {
			ExportImportThreadLocal.setPortletExportInProcess(false);
		}

		_targetCompany = CompanyTestUtil.addCompany(true);

		Group targetCompanyGroup = null;
		DLFileEntryType targetDLFileEntryType = null;
		User targetUser = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_targetCompany.getCompanyId())) {

			targetUser = UserTestUtil.getAdminUser(
				_targetCompany.getCompanyId());

			Assert.assertNotNull(targetUser);

			UserTestUtil.setUser(targetUser);

			targetCompanyGroup = _targetCompany.getGroup();

			targetDLFileEntryType = _addSystemFileEntryType(
				targetCompanyGroup, targetUser.getUserId(),
				RandomTestUtil.randomString());
		}

		Assert.assertNotEquals(
			dlFileEntryType.getUuid(), targetDLFileEntryType.getUuid());

		try (SafeCloseable safeCloseable1 = initImportWithSafeCloseable(
				stagingGroup, targetCompanyGroup)) {

			portletDataContext.setUserIdStrategy(
				new TestUserIdStrategy(targetUser));

			StagedModel exportedStagedModel = readExportedStagedModel(
				dlFileEntryType);

			Assert.assertNotNull(exportedStagedModel);

			try (SafeCloseable safeCloseable2 =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						_targetCompany.getCompanyId())) {

				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importReferenceStagedModels(
					portletDataContext, exportedStagedModel,
					DDMStructure.class);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			Map<Long, Long> fileEntryTypeIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					DLFileEntryType.class);

			Assert.assertEquals(
				fileEntryTypeIds.toString(),
				Long.valueOf(targetDLFileEntryType.getFileEntryTypeId()),
				fileEntryTypeIds.get(dlFileEntryType.getFileEntryTypeId()));
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_targetCompany.getCompanyId())) {

			DLFileEntryType importedDLFileEntryType =
				DLFileEntryTypeLocalServiceUtil.fetchFileEntryType(
					targetCompanyGroup.getGroupId(), _FILE_ENTRY_TYPE_KEY);

			Assert.assertEquals(
				targetDLFileEntryType.getFileEntryTypeId(),
				importedDLFileEntryType.getFileEntryTypeId());
			Assert.assertEquals(
				name, importedDLFileEntryType.getName(LocaleUtil.US));
		}
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DLFileEntryMetadata.class.getName());

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, ddmStructure);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			DDMStructure.class.getSimpleName());

		DDMStructure ddmStructure = (DDMStructure)dependentStagedModels.get(0);

		return DLFileEntryTypeLocalServiceUtil.addFileEntryType(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			ddmStructure.getStructureId(), null,
			Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
			Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return DLFileEntryTypeLocalServiceUtil.
			getDLFileEntryTypeByUuidAndGroupId(uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return DLFileEntryType.class;
	}

	@Override
	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			DDMStructure.class.getSimpleName());

		Assert.assertEquals(
			dependentStagedModels.toString(), 1, dependentStagedModels.size());

		DDMStructure ddmStructure = (DDMStructure)dependentStagedModels.get(0);

		DDMStructureLocalServiceUtil.getDDMStructureByUuidAndGroupId(
			ddmStructure.getUuid(), group.getGroupId());
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		DLFileEntryType dlFileEntryType = (DLFileEntryType)stagedModel;
		DLFileEntryType importedDLFileEntryType =
			(DLFileEntryType)importedStagedModel;

		Assert.assertEquals(
			dlFileEntryType.getName(), importedDLFileEntryType.getName());
		Assert.assertEquals(
			dlFileEntryType.getDescription(),
			importedDLFileEntryType.getDescription());
	}

	private DLFileEntryType _addSystemFileEntryType(
			Group group, long userId, String name)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), userId);

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm();

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.addStructure(
			null, userId, group.getGroupId(),
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
			PortalUtil.getClassNameId(DLFileEntryMetadata.class.getName()),
			null, Collections.singletonMap(LocaleUtil.US, name), null, ddmForm,
			DDMUtil.getDefaultDDMFormLayout(ddmForm),
			StorageType.DEFAULT.toString(), DDMStructureConstants.TYPE_DEFAULT,
			serviceContext);

		return DLFileEntryTypeLocalServiceUtil.addFileEntryType(
			null, userId, group.getGroupId(), ddmStructure.getStructureId(),
			_FILE_ENTRY_TYPE_KEY, Collections.singletonMap(LocaleUtil.US, name),
			Collections.singletonMap(LocaleUtil.US, name),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_SYSTEM,
			serviceContext);
	}

	private StagedModelDataHandler<DLFileEntryType>
		_getStagedModelDataHandler() {

		return (StagedModelDataHandler<DLFileEntryType>)
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				DLFileEntryType.class.getName());
	}

	private static final String _FILE_ENTRY_TYPE_KEY =
		RandomTestUtil.randomString();

	@DeleteAfterTestRun
	private Company _targetCompany;

}