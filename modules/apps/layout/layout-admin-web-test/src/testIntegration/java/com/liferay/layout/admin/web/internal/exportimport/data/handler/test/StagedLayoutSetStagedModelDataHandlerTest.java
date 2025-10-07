/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManagerUtil;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.set.model.adapter.StagedLayoutSet;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;
import com.liferay.portal.test.rule.Inject;
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
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class StagedLayoutSetStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	@Test
	public void testCleanStagedModelDataHandler() throws Exception {
	}

	@Test
	public void testClientExtensionEntries() throws Exception {
		_testClientExtensionEntries(
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS, "http://css.css");
		_testClientExtensionEntries(
			ClientExtensionEntryConstants.TYPE_THEME_CSS, "http://css.css");
		_testClientExtensionEntries(
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS, "http://js.js");
	}

	@Test
	public void testExportImportFaviconDisabled() throws Exception {
		_testExportImportFavicon(false, false);
	}

	@Test
	public void testExportImportFaviconEnabled() throws Exception {
		_testExportImportFavicon(true, true);
	}

	@Test
	public void testExportImportFaviconUndefined() throws Exception {
		_testExportImportFavicon(null, true);
	}

	@Test
	@TestInfo("LPD-47835")
	public void testExportImportLayoutPriorityWithDuplicateLayoutId()
		throws Exception {

		initExport();

		Layout layout1 = LayoutTestUtil.addTypeContentLayout(stagingGroup);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(stagingGroup);

		Layout layout3 = LayoutTestUtil.addTypeContentLayout(stagingGroup);

		layout3 = _layoutLocalService.updatePriority(layout3.getPlid(), 0);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				stagingGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Layout masterLayout = _updateLayoutId(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			RandomTestUtil.randomLong());

		layout3 = _updateLayoutId(layout3, masterLayout.getLayoutId());

		Assert.assertEquals(masterLayout.getLayoutId(), layout3.getLayoutId());

		Layout layout4 = LayoutTestUtil.addTypeContentLayout(
			stagingGroup, false, false, masterLayout.getPlid());

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, _assertPriority(layout1.getPlid(), 1));
		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, _assertPriority(layout2.getPlid(), 2));
		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, _assertPriority(layout3.getPlid(), 0));
		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, _assertPriority(layout4.getPlid(), 3));

		StagedLayoutSet stagedLayoutSet = ModelAdapterUtil.adapt(
			stagingGroup.getPublicLayoutSet(), LayoutSet.class,
			StagedLayoutSet.class);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedLayoutSet);

		initImport();

		ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
			ExportImportLifecycleConstants.EVENT_LAYOUT_IMPORT_STARTED,
			ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_IMPORT_IN_PROCESS,
			portletDataContext.getExportImportProcessId(),
			PortletDataContextFactoryUtil.clonePortletDataContext(
				portletDataContext));

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, (Layout)readExportedStagedModel(layout1));
		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, (Layout)readExportedStagedModel(layout2));
		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, (Layout)readExportedStagedModel(layout3));
		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, (Layout)readExportedStagedModel(layout4));

		portletDataContext.setPrivateLayout(false);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext,
			(StagedLayoutSet)readExportedStagedModel(stagedLayoutSet));

		ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
			ExportImportLifecycleConstants.EVENT_LAYOUT_IMPORT_SUCCEEDED,
			ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_IMPORT_IN_PROCESS,
			portletDataContext.getExportImportProcessId(),
			PortletDataContextFactoryUtil.clonePortletDataContext(
				portletDataContext));

		_assertPriority(1, layout1.getUuid());
		_assertPriority(2, layout2.getUuid());
		_assertPriority(0, layout3.getUuid());

		LayoutPageTemplateEntry importedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByUuidAndGroupId(
					layoutPageTemplateEntry.getUuid(), liveGroup.getGroupId());

		Layout importedMasterLayout = _layoutLocalService.getLayout(
			importedLayoutPageTemplateEntry.getPlid());

		Layout importedLayout4 = _assertPriority(3, layout4.getUuid());

		Assert.assertEquals(
			importedMasterLayout.getPlid(),
			importedLayout4.getMasterLayoutPlid());
	}

	@Override
	@Test
	public void testStagedModelDataHandler() throws Exception {
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		addDependentStagedModel(
			dependentStagedModelsMap, LayoutSet.class,
			ModelAdapterUtil.adapt(
				group.getPublicLayoutSet(), LayoutSet.class,
				StagedLayoutSet.class));

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return ModelAdapterUtil.adapt(
			group.getPublicLayoutSet(), LayoutSet.class, StagedLayoutSet.class);
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return ModelAdapterUtil.adapt(
			group.getPublicLayoutSet(), LayoutSet.class, StagedLayoutSet.class);
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return StagedLayoutSet.class;
	}

	private Layout _assertPriority(int priority, String uuid) throws Exception {
		Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			uuid, liveGroup.getGroupId(), false);

		Assert.assertEquals(priority, layout.getPriority());

		return layout;
	}

	private Layout _assertPriority(long plid, int priority) throws Exception {
		Layout layout = _layoutLocalService.getLayout(plid);

		Assert.assertEquals(priority, layout.getPriority());

		return layout;
	}

	private void _testClientExtensionEntries(String type, String url)
		throws Exception {

		initExport();

		ClientExtensionEntry clientExtensionEntry =
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				StringPool.BLANK, StringPool.BLANK, type,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"url", url
				).buildString());

		StagedLayoutSet stagedLayoutSet = ModelAdapterUtil.adapt(
			stagingGroup.getPublicLayoutSet(), LayoutSet.class,
			StagedLayoutSet.class);

		LayoutSet stagingLayoutSet = stagedLayoutSet.getLayoutSet();

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			_portal.getClassNameId(LayoutSet.class),
			stagingLayoutSet.getLayoutSetId(),
			clientExtensionEntry.getExternalReferenceCode(), type,
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedLayoutSet);

		initImport();

		ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
			ExportImportLifecycleConstants.EVENT_LAYOUT_IMPORT_STARTED,
			ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_IMPORT_IN_PROCESS,
			portletDataContext.getExportImportProcessId(),
			PortletDataContextFactoryUtil.clonePortletDataContext(
				portletDataContext));

		StagedLayoutSet exportedStagedLayoutSet =
			(StagedLayoutSet)readExportedStagedModel(stagedLayoutSet);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedLayoutSet);

		ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
			ExportImportLifecycleConstants.EVENT_LAYOUT_IMPORT_SUCCEEDED,
			ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_IMPORT_IN_PROCESS,
			portletDataContext.getExportImportProcessId(),
			PortletDataContextFactoryUtil.clonePortletDataContext(
				portletDataContext));

		StagedLayoutSet importedStagedLayoutSet = ModelAdapterUtil.adapt(
			liveGroup.getPublicLayoutSet(), LayoutSet.class,
			StagedLayoutSet.class);

		LayoutSet importedLayoutSet = importedStagedLayoutSet.getLayoutSet();

		Assert.assertEquals(
			1,
			_clientExtensionEntryRelLocalService.
				getClientExtensionEntryRelsCount(
					_portal.getClassNameId(LayoutSet.class),
					importedLayoutSet.getLayoutSetId(), type));

		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			_portal.getClassNameId(LayoutSet.class),
			stagingLayoutSet.getLayoutSetId(), type);

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			_portal.getClassNameId(LayoutSet.class),
			stagingLayoutSet.getLayoutSetId(),
			clientExtensionEntry.getExternalReferenceCode(), type,
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));

		initExport();

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedLayoutSet);

		initImport();

		ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
			ExportImportLifecycleConstants.EVENT_LAYOUT_IMPORT_STARTED,
			ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_IMPORT_IN_PROCESS,
			portletDataContext.getExportImportProcessId(),
			PortletDataContextFactoryUtil.clonePortletDataContext(
				portletDataContext));

		exportedStagedLayoutSet = (StagedLayoutSet)readExportedStagedModel(
			stagedLayoutSet);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedLayoutSet);

		ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
			ExportImportLifecycleConstants.EVENT_LAYOUT_IMPORT_SUCCEEDED,
			ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_IMPORT_IN_PROCESS,
			portletDataContext.getExportImportProcessId(),
			PortletDataContextFactoryUtil.clonePortletDataContext(
				portletDataContext));

		importedStagedLayoutSet = ModelAdapterUtil.adapt(
			liveGroup.getPublicLayoutSet(), LayoutSet.class,
			StagedLayoutSet.class);

		importedLayoutSet = importedStagedLayoutSet.getLayoutSet();

		Assert.assertEquals(
			1,
			_clientExtensionEntryRelLocalService.
				getClientExtensionEntryRelsCount(
					_portal.getClassNameId(LayoutSet.class),
					importedLayoutSet.getLayoutSetId(), type));
	}

	private void _testExportImportFavicon(
			Boolean faviconEnabled, boolean shouldImportFavicon)
		throws Exception {

		initExport();

		FileEntry faviconFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "favicon.ico",
			"image/x-icon", TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));

		_layoutSetLocalService.updateFaviconFileEntryId(
			stagingGroup.getGroupId(), false,
			faviconFileEntry.getFileEntryId());

		LayoutSet stagingLayoutSet = _layoutSetLocalService.getLayoutSet(
			stagingGroup.getGroupId(), false);

		StagedLayoutSet stagedLayoutSet = ModelAdapterUtil.adapt(
			stagingLayoutSet, LayoutSet.class, StagedLayoutSet.class);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, faviconFileEntry);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedLayoutSet);

		initImport();

		if (faviconEnabled != null) {
			portletDataContext.getParameterMap(
			).put(
				PortletDataHandlerKeys.FAVICON,
				new String[] {String.valueOf(faviconEnabled)}
			);
		}

		FileEntry exportedFaviconFileEntry = (FileEntry)readExportedStagedModel(
			faviconFileEntry);

		if (exportedFaviconFileEntry != null) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedFaviconFileEntry);
		}

		StagedLayoutSet exportedStagedLayoutSet =
			(StagedLayoutSet)readExportedStagedModel(stagedLayoutSet);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedLayoutSet);

		LayoutSet importedLayoutSet = _layoutSetLocalService.getLayoutSet(
			liveGroup.getGroupId(), false);

		if (shouldImportFavicon) {
			Assert.assertTrue(importedLayoutSet.getFaviconFileEntryId() > 0);
		}
		else {
			Assert.assertEquals(0, importedLayoutSet.getFaviconFileEntryId());
		}
	}

	private Layout _updateLayoutId(Layout layout, long layoutId)
		throws Exception {

		layout.setLayoutId(layoutId);

		return _layoutLocalService.updateLayout(layout);
	}

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Inject
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

}