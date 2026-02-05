/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateEntryStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testChangeDefaultLayoutUtilityPageEntry() throws Exception {
		initExport();

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				stagingGroup.getGroupId(),
				_portal.getClassNameId(FileEntry.class.getName()), 0, null,
				false, WorkflowConstants.STATUS_APPROVED);

		layoutPageTemplateEntry1 =
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(), true);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, layoutPageTemplateEntry1);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			LayoutPageTemplateEntry exportedLayoutPageTemplateEntry1 =
				(LayoutPageTemplateEntry)readExportedStagedModel(
					layoutPageTemplateEntry1);

			LayoutPageTemplateEntry importedLayoutPageTemplateEntry1 =
				_getImportedLayoutPageTemplateEntry(
					exportedLayoutPageTemplateEntry1, liveGroup,
					layoutPageTemplateEntry1);

			Assert.assertTrue(
				importedLayoutPageTemplateEntry1.isDefaultTemplate());
		}

		initExport();

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				stagingGroup.getGroupId(),
				_portal.getClassNameId(FileEntry.class.getName()), 0, null,
				false, WorkflowConstants.STATUS_APPROVED);

		layoutPageTemplateEntry2 =
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry2.getLayoutPageTemplateEntryId(), true);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, layoutPageTemplateEntry2);

		// We changed the default layout page template entry, so we have to
		// export the model again

		layoutPageTemplateEntry1 =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntry1.getLayoutPageTemplateEntryId());

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, layoutPageTemplateEntry1);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			LayoutPageTemplateEntry exportedLayoutPageTemplateEntry1 =
				(LayoutPageTemplateEntry)readExportedStagedModel(
					layoutPageTemplateEntry1);

			LayoutPageTemplateEntry importedLayoutPageTemplateEntry1 =
				_getImportedLayoutPageTemplateEntry(
					exportedLayoutPageTemplateEntry1, liveGroup,
					layoutPageTemplateEntry1);

			Assert.assertFalse(
				importedLayoutPageTemplateEntry1.isDefaultTemplate());

			LayoutPageTemplateEntry exportedLayoutPageTemplateEntry2 =
				(LayoutPageTemplateEntry)readExportedStagedModel(
					layoutPageTemplateEntry2);

			LayoutPageTemplateEntry importedLayoutUtilityPageEntry2 =
				_getImportedLayoutPageTemplateEntry(
					exportedLayoutPageTemplateEntry2, liveGroup,
					layoutPageTemplateEntry2);

			Assert.assertTrue(
				importedLayoutUtilityPageEntry2.isDefaultTemplate());
		}
	}

	@Test
	public void testImportLayoutPageTemplateEntry() throws Exception {
		initExport();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				stagingGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.BASIC,
				WorkflowConstants.STATUS_APPROVED);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, layoutPageTemplateEntry);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			LayoutPageTemplateEntry exportedLayoutPageTemplateEntry =
				(LayoutPageTemplateEntry)readExportedStagedModel(
					layoutPageTemplateEntry);

			LayoutPageTemplateEntry importedLayoutPageTemplateEntry =
				_getImportedLayoutPageTemplateEntry(
					exportedLayoutPageTemplateEntry, liveGroup,
					layoutPageTemplateEntry);

			Layout importedLayout = _layoutLocalService.fetchLayout(
				importedLayoutPageTemplateEntry.getPlid());

			Assert.assertTrue(importedLayout.isPrivateLayout());

			Layout draftImportedLayout = importedLayout.fetchDraftLayout();

			Assert.assertTrue(draftImportedLayout.isPrivateLayout());
		}
	}

	@Test
	public void testImportLayoutPageTemplateEntryByGuestUser()
		throws Exception {

		Company company = CompanyLocalServiceUtil.getCompany(
			stagingGroup.getCompanyId());

		Group companyGroup = company.getGroup();

		User guestUser = company.getGuestUser();

		_layoutPrototype = _addLayoutPrototype(
			company.getCompanyId(), companyGroup.getGroupId(),
			"Test Layout Prototype", guestUser.getUserId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchFirstLayoutPageTemplateEntry(
					_layoutPrototype.getLayoutPrototypeId());

		_targetCompany = CompanyTestUtil.addCompany();

		User targetGuestUser = _targetCompany.getGuestUser();

		_addLayoutPrototype(
			_targetCompany.getCompanyId(), _targetCompany.getGroupId(),
			"Test Layout Prototype", targetGuestUser.getUserId());

		initExport(companyGroup);

		try {
			ExportImportThreadLocal.setPortletExportInProcess(true);

			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, layoutPageTemplateEntry);
		}
		finally {
			ExportImportThreadLocal.setPortletExportInProcess(false);
		}

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable(
				companyGroup, _targetCompany.getGroup())) {

			portletDataContext.setUserIdStrategy(
				new TestUserIdStrategy(targetGuestUser));

			StagedModel exportedStagedModel = readExportedStagedModel(
				layoutPageTemplateEntry);

			Assert.assertNotNull(exportedStagedModel);

			try {
				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}
		}
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		long userId = TestPropsValues.getUserId();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, userId, group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Test Collection", StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			null, userId, group.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			null, "Test Entry", LayoutPageTemplateEntryTypeConstants.BASIC, 0,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group) {
		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntryByUuidAndGroupId(
				uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return LayoutPageTemplateEntry.class;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		DateTestUtil.assertEquals(
			stagedModel.getCreateDate(), importedStagedModel.getCreateDate());

		Assert.assertEquals(
			stagedModel.getUuid(), importedStagedModel.getUuid());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			(LayoutPageTemplateEntry)stagedModel;
		LayoutPageTemplateEntry importLayoutPageTemplateEntry =
			(LayoutPageTemplateEntry)importedStagedModel;

		Assert.assertEquals(
			layoutPageTemplateEntry.getName(),
			importLayoutPageTemplateEntry.getName());
		Assert.assertEquals(
			layoutPageTemplateEntry.getType(),
			importLayoutPageTemplateEntry.getType());
	}

	private LayoutPrototype _addLayoutPrototype(
			long companyId, long groupId, String name, long userId)
		throws Exception {

		return LayoutPrototypeLocalServiceUtil.addLayoutPrototype(
			userId, companyId,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			(Map<Locale, String>)null, true,
			ServiceContextTestUtil.getServiceContext(
				companyId, groupId, userId));
	}

	private LayoutPageTemplateEntry _getImportedLayoutPageTemplateEntry(
			LayoutPageTemplateEntry exportedLayoutPageTemplateEntry,
			Group group, LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedLayoutPageTemplateEntry);

		return (LayoutPageTemplateEntry)getStagedModel(
			layoutPageTemplateEntry.getUuid(), group);
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@DeleteAfterTestRun
	private LayoutPrototype _layoutPrototype;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private Company _targetCompany;

}