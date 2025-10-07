/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class ExportImportMasterLayoutsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();

		_serviceContext1 = ServiceContextTestUtil.getServiceContext(
			_group1, TestPropsValues.getUserId());
		_serviceContext2 = ServiceContextTestUtil.getServiceContext(
			_group2, TestPropsValues.getUserId());
	}

	@Test
	public void testExportImportMasterLayoutDropZoneAllowNewFragmentEntries()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, _serviceContext1.getUserId(),
				_serviceContext1.getScopeGroupId(), 0, null,
				StringUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED, _serviceContext1);

		Layout layout1 = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry1.getPlid());

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group1.getGroupId(),
				layoutPageTemplateEntry1.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						layoutPageTemplateEntry1.getPlid()),
				_read("export_import_master_layout_layout_data.json"));

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			_group1.getGroupId(), RandomTestUtil.randomString(),
			_serviceContext1);

		Class<?> clazz = getClass();

		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			null, _group1.getGroupId(), TestPropsValues.getUserId(),
			LayoutPageTemplateEntry.class.getName(),
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
			RandomTestUtil.randomString(), repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/thumbnail.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);

		_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
			fileEntry.getFileEntryId());

		File file = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "getFile", new Class<?>[] {long[].class},
			new long[] {
				layoutPageTemplateEntry1.getLayoutPageTemplateEntryId()
			});

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries = null;

		ServiceContextThreadLocal.pushServiceContext(_serviceContext2);

		try {
			layoutsImporterResultEntries = _layoutsImporter.importFile(
				TestPropsValues.getUserId(), _group2.getGroupId(), 0, file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertNotNull(layoutsImporterResultEntries);

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 1,
			layoutsImporterResultEntries.size());

		LayoutsImporterResultEntry layoutPageTemplateImportEntry =
			layoutsImporterResultEntries.get(0);

		Assert.assertEquals(
			LayoutsImporterResultEntry.Status.IMPORTED,
			layoutPageTemplateImportEntry.getStatus());

		String layoutPageTemplateEntryKey = StringUtil.toLowerCase(
			layoutPageTemplateImportEntry.getName());

		layoutPageTemplateEntryKey = StringUtil.replace(
			layoutPageTemplateEntryKey, CharPool.SPACE, CharPool.DASH);

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group2.getGroupId(), layoutPageTemplateEntryKey);

		Assert.assertNotNull(layoutPageTemplateEntry2);

		Layout layout2 = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry2.getPlid());

		Assert.assertNotNull(layout2);

		Assert.assertEquals(
			layout1.getMasterLayoutPlid(), layout2.getMasterLayoutPlid());

		Assert.assertEquals(
			layoutPageTemplateEntry1.getName(),
			layoutPageTemplateEntry2.getName());
		Assert.assertEquals(
			layoutPageTemplateEntry1.getType(),
			layoutPageTemplateEntry2.getType());

		LayoutPageTemplateStructure layoutPageTemplateStructure1 =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layoutPageTemplateEntry1.getGroupId(),
					layoutPageTemplateEntry1.getPlid());
		LayoutPageTemplateStructure layoutPageTemplateStructure2 =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layoutPageTemplateEntry2.getGroupId(),
					layoutPageTemplateEntry2.getPlid());

		LayoutStructure layoutStructure1 = LayoutStructure.of(
			layoutPageTemplateStructure1.getDefaultSegmentsExperienceData());
		LayoutStructure layoutStructure2 = LayoutStructure.of(
			layoutPageTemplateStructure2.getDefaultSegmentsExperienceData());

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem1 =
			_getDropZoneLayoutStructureItem(layoutStructure1);
		DropZoneLayoutStructureItem dropZoneLayoutStructureItem2 =
			_getDropZoneLayoutStructureItem(layoutStructure2);

		_validateDropZoneLayoutStructureItem(
			dropZoneLayoutStructureItem1, dropZoneLayoutStructureItem2);
	}

	private DropZoneLayoutStructureItem _getDropZoneLayoutStructureItem(
		LayoutStructure layoutStructure) {

		LayoutStructureItem layoutStructureItem =
			_getMainChildLayoutStructureItem(layoutStructure);

		Assert.assertTrue(
			layoutStructureItem instanceof DropZoneLayoutStructureItem);

		return (DropZoneLayoutStructureItem)layoutStructureItem;
	}

	private LayoutStructureItem _getMainChildLayoutStructureItem(
		LayoutStructure layoutStructure) {

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		String childItemId = childrenItemIds.get(0);

		return layoutStructure.getLayoutStructureItem(childItemId);
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private List<String> _removeNonfragmentEntryKeys(
		List<String> importedFragmentEntryKeys) {

		List<String> filteredFragmentEntryKeys = new ArrayList<>();

		ListUtil.filter(
			importedFragmentEntryKeys, filteredFragmentEntryKeys,
			fragmentEntryKey -> !fragmentEntryKey.equals(
				"lfr-all-fragments-id"));

		return Collections.unmodifiableList(filteredFragmentEntryKeys);
	}

	private void _validateDropZoneLayoutStructureItem(
		DropZoneLayoutStructureItem expectedDropZoneLayoutStructureItem,
		DropZoneLayoutStructureItem actualDropZoneLayoutStructureItem) {

		Assert.assertEquals(
			expectedDropZoneLayoutStructureItem.isAllowNewFragmentEntries(),
			actualDropZoneLayoutStructureItem.isAllowNewFragmentEntries());

		Assert.assertEquals(
			ListUtil.sort(
				_removeNonfragmentEntryKeys(
					expectedDropZoneLayoutStructureItem.
						getFragmentEntryKeys())),
			ListUtil.sort(
				_removeNonfragmentEntryKeys(
					actualDropZoneLayoutStructureItem.getFragmentEntryKeys())));
	}

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutsImporter _layoutsImporter;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/export_master_layouts"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext1;
	private ServiceContext _serviceContext2;

}