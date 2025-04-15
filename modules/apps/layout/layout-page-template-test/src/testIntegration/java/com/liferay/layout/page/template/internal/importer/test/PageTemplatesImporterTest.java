/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.exporter.LayoutsExporter;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateExportImportConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletPreferences;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class PageTemplatesImporterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_bundle = FrameworkUtil.getBundle(getClass());

		_bundleContext = _bundle.getBundleContext();

		_group = GroupTestUtil.addGroup();
		_testPortletName = "TEST_PORTLET_" + RandomTestUtil.randomString();
		_user = TestPropsValues.getUser();
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testDoubleImportLayoutPageTemplate() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			String name = RandomTestUtil.randomString();

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					addLayoutPageTemplateCollection(
						null, TestPropsValues.getUserId(), _group.getGroupId(),
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						null, name, RandomTestUtil.randomString(),
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						serviceContext);

			String layoutPageTemplateEntryName = RandomTestUtil.randomString();

			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, layoutPageTemplateEntryName,
				LayoutPageTemplateEntryTypeConstants.BASIC, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

			File file = _layoutsExporter.exportLayoutPageTemplateEntries(
				_group.getGroupId());

			for (LayoutPageTemplateEntry layoutPageTemplateEntry :
					_layoutPageTemplateEntryLocalService.
						getLayoutPageTemplateEntries(_group.getGroupId())) {

				_layoutPageTemplateEntryLocalService.
					deleteLayoutPageTemplateEntry(
						layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
			}

			List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
				_layoutsImporter.importFile(
					TestPropsValues.getUserId(), _group.getGroupId(), 0, file,
					LayoutsImportStrategy.DO_NOT_OVERWRITE, true);

			Assert.assertEquals(
				layoutsImporterResultEntries.toString(), 1,
				layoutsImporterResultEntries.size());

			LayoutsImporterResultEntry layoutsImporterResultEntry =
				layoutsImporterResultEntries.get(0);

			Assert.assertEquals(
				layoutPageTemplateEntryName,
				layoutsImporterResultEntry.getName());
			Assert.assertEquals(
				LayoutsImporterResultEntry.Status.IMPORTED,
				layoutsImporterResultEntry.getStatus());

			for (LayoutPageTemplateEntry layoutPageTemplateEntry :
					_layoutPageTemplateEntryLocalService.
						getLayoutPageTemplateEntries(_group.getGroupId())) {

				_layoutPageTemplateEntryLocalService.
					deleteLayoutPageTemplateEntry(
						layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
			}

			layoutsImporterResultEntries = _layoutsImporter.importFile(
				TestPropsValues.getUserId(), _group.getGroupId(), 0, file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);

			Assert.assertEquals(
				layoutsImporterResultEntries.toString(), 1,
				layoutsImporterResultEntries.size());

			layoutsImporterResultEntry = layoutsImporterResultEntries.get(0);

			Assert.assertEquals(
				layoutPageTemplateEntryName,
				layoutsImporterResultEntry.getName());
			Assert.assertEquals(
				LayoutsImporterResultEntry.Status.IMPORTED,
				layoutsImporterResultEntry.getStatus());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	@TestInfo("LPS-106212")
	public void testExportImportLayoutPageTemplateWithMasterLayout()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
					null, RandomTestUtil.randomString(),
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
					WorkflowConstants.STATUS_DRAFT,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					addLayoutPageTemplateCollection(
						null, TestPropsValues.getUserId(), _group.getGroupId(),
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						null, RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						serviceContext);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, RandomTestUtil.randomString(),
					LayoutPageTemplateEntryTypeConstants.BASIC,
					masterLayoutPageTemplateEntry.getPlid(),
					WorkflowConstants.STATUS_APPROVED, serviceContext);

			File file = _layoutsExporter.exportLayoutPageTemplateEntries(
				new long[] {
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				},
				LayoutPageTemplateEntryTypeConstants.BASIC);

			_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

			_layoutsImporter.importFile(
				TestPropsValues.getUserId(), _group.getGroupId(), file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						_group.getGroupId(),
						layoutPageTemplateEntry.
							getLayoutPageTemplateEntryKey());

			Layout layout = _layoutLocalService.fetchLayout(
				layoutPageTemplateEntry.getPlid());

			Assert.assertEquals(
				masterLayoutPageTemplateEntry.getPlid(),
				layout.getMasterLayoutPlid());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testImportEmptyLayoutPageTemplateEntryCollection()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry("collection", new HashMap<>());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem layoutStructureItem =
			_getMainChildLayoutStructureItem(layoutStructure);

		Assert.assertTrue(
			layoutStructureItem instanceof CollectionStyledLayoutStructureItem);

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				(CollectionStyledLayoutStructureItem)layoutStructureItem;

		Assert.assertNotNull(collectionStyledLayoutStructureItem);

		Assert.assertEquals(
			2, collectionStyledLayoutStructureItem.getNumberOfColumns());
		Assert.assertEquals(
			4, collectionStyledLayoutStructureItem.getNumberOfItems());
		Assert.assertEquals(
			1, collectionStyledLayoutStructureItem.getNumberOfItemsPerPage());
		Assert.assertEquals(
			"simple", collectionStyledLayoutStructureItem.getPaginationType());
	}

	@Test
	public void testImportEmptyLayoutPageTemplateEntryRow() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry("row", new HashMap<>());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem layoutStructureItem =
			_getMainChildLayoutStructureItem(layoutStructure);

		Assert.assertTrue(
			layoutStructureItem instanceof RowStyledLayoutStructureItem);

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)layoutStructureItem;

		Assert.assertNotNull(rowStyledLayoutStructureItem);

		Assert.assertEquals(
			6, rowStyledLayoutStructureItem.getNumberOfColumns());
		Assert.assertFalse(rowStyledLayoutStructureItem.isGutters());

		List<String> rowChildrenItemIds =
			rowStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			rowChildrenItemIds.toString(), 6, rowChildrenItemIds.size());

		for (String rowChildItemId : rowChildrenItemIds) {
			LayoutStructureItem childLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(rowChildItemId);

			Assert.assertEquals(
				LayoutDataItemTypeConstants.TYPE_COLUMN,
				childLayoutStructureItem.getItemType());

			ColumnLayoutStructureItem columnLayoutStructureItem =
				(ColumnLayoutStructureItem)childLayoutStructureItem;

			Assert.assertEquals(2, columnLayoutStructureItem.getSize());
		}
	}

	@Test
	public void testImportEmptyLayoutPageTemplateEntrySection()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry("section", new HashMap<>());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem layoutStructureItem =
			_getMainChildLayoutStructureItem(layoutStructure);

		Assert.assertTrue(
			layoutStructureItem instanceof ContainerStyledLayoutStructureItem);

		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem =
			(ContainerStyledLayoutStructureItem)layoutStructureItem;

		Assert.assertNotNull(layoutStructure);

		Assert.assertEquals(
			"fluid", containerStyledLayoutStructureItem.getWidthType());

		JSONObject itemConfigJSONObject =
			containerStyledLayoutStructureItem.getItemConfigJSONObject();

		JSONObject stylesJSONObject = itemConfigJSONObject.getJSONObject(
			"styles");

		Assert.assertEquals(
			StringPool.BLANK, stylesJSONObject.getString("marginBottom"));
		Assert.assertEquals("5", stylesJSONObject.getString("paddingBottom"));
		Assert.assertEquals("5", stylesJSONObject.getString("paddingLeft"));
		Assert.assertEquals("5", stylesJSONObject.getString("paddingTop"));

		JSONObject jsonObject =
			containerStyledLayoutStructureItem.getBackgroundImageJSONObject();

		Assert.assertEquals("test.jpg", jsonObject.get("title"));
		Assert.assertEquals("test-image.jpg", jsonObject.get("url"));
	}

	@Test
	public void testImportEmptyLayoutPageTemplateEntryWidget()
		throws Exception {

		_registerTestPortlet(_testPortletName);

		String configProperty1 = RandomTestUtil.randomString();
		String configProperty2 = RandomTestUtil.randomString();

		Role role = _roleLocalService.getDefaultGroupRole(_group.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry(
				"widget",
				HashMapBuilder.put(
					"CONFIG_PROPERTY_1", configProperty1
				).put(
					"CONFIG_PROPERTY_2", configProperty2
				).put(
					"ROLE_KEY", role.getName()
				).put(
					"WIDGET_NAME", _testPortletName
				).build());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem layoutStructureItem =
			_getMainChildLayoutStructureItem(layoutStructure);

		Assert.assertTrue(
			layoutStructureItem instanceof FragmentStyledLayoutStructureItem);

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)layoutStructureItem;

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertNotNull(fragmentEntryLink);

		JSONObject editableValuesJSONObject = JSONFactoryUtil.createJSONObject(
			fragmentEntryLink.getEditableValues());

		String portletId = editableValuesJSONObject.getString("portletId");

		Assert.assertEquals(_testPortletName, portletId);

		String instanceId = editableValuesJSONObject.getString("instanceId");

		Assert.assertNotNull(instanceId);

		PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				_portletPreferencesLocalService.fetchPortletPreferences(
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
					layoutPageTemplateEntry.getPlid(),
					PortletIdCodec.encode(portletId, instanceId)));

		Assert.assertEquals(
			configProperty1,
			jxPortletPreferences.getValue("config-property-1", null));

		Assert.assertEquals(
			configProperty2,
			jxPortletPreferences.getValue("config-property-2", null));

		String resourcePrimKey = PortletPermissionUtil.getPrimaryKey(
			layoutPageTemplateEntry.getPlid(),
			PortletIdCodec.encode(portletId, instanceId));

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				layoutPageTemplateEntry.getCompanyId(), _testPortletName,
				ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey);

		Assert.assertEquals(
			resourcePermissions.toString(), 1, resourcePermissions.size());

		ResourcePermission resourcePermission = resourcePermissions.get(0);

		Assert.assertEquals(role.getRoleId(), resourcePermission.getRoleId());

		ResourceAction resourceAction = null;

		for (ResourceAction curResourceAction :
				_resourceActionLocalService.getResourceActions(
					_testPortletName)) {

			if (Objects.equals(curResourceAction.getActionId(), "VIEW")) {
				resourceAction = curResourceAction;

				break;
			}
		}

		Assert.assertNotNull(resourceAction);

		long bitwiseValue = resourceAction.getBitwiseValue();

		Assert.assertTrue(
			(resourcePermission.getActionIds() & bitwiseValue) == bitwiseValue);
	}

	@Test
	public void testImportEmptyLayoutPageTemplateEntryWithInvalidWidget()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry(
				"widget",
				HashMapBuilder.put(
					"WIDGET_NAME", RandomTestUtil.randomString()
				).build());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 0, childrenItemIds.size());
	}

	@Test
	public void testImportLayoutPageTemplateEntryDropZoneFragment()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					addLayoutPageTemplateCollection(
						null, TestPropsValues.getUserId(), _group.getGroupId(),
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						null, RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						serviceContext);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, RandomTestUtil.randomString(),
					LayoutPageTemplateEntryTypeConstants.BASIC, 0,
					WorkflowConstants.STATUS_APPROVED, serviceContext);

			FragmentCollection fragmentCollection =
				_fragmentCollectionLocalService.addFragmentCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), serviceContext);

			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.addFragmentEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					fragmentCollection.getFragmentCollectionId(),
					StringUtil.randomString(), StringUtil.randomString(),
					RandomTestUtil.randomString(),
					StringBundler.concat(
						"<div class=\"fragment_1\"><h1> Drop Zone 1 </h1>",
						"<lfr-drop-zone></lfr-drop-zone><h1> Drop Zone 2 </h1>",
						"<lfr-drop-zone></lfr-drop-zone></div>"),
					RandomTestUtil.randomString(), false, "{fieldSets: []}",
					null, 0, false, false, FragmentConstants.TYPE_COMPONENT,
					null, WorkflowConstants.STATUS_APPROVED,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId()));

			_addFragmentEntryToLayoutPageTemplateEntry(
				fragmentEntry, layoutPageTemplateEntry);

			_assertLayoutPageTemplateEntryFragmentDropZoneLayoutStructureItems(
				new String[] {StringPool.BLANK, StringPool.BLANK},
				fragmentEntry,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());

			File file = _layoutsExporter.exportLayoutPageTemplateEntries(
				new long[] {
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				},
				LayoutPageTemplateEntryTypeConstants.BASIC);

			_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

			_layoutsImporter.importFile(
				TestPropsValues.getUserId(), _group.getGroupId(), file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);

			_assertLayoutPageTemplateEntryFragmentDropZoneLayoutStructureItems(
				new String[] {StringPool.BLANK, StringPool.BLANK},
				fragmentEntry,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testImportLayoutPageTemplateEntryDropZoneFragmentWithIds()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					addLayoutPageTemplateCollection(
						null, TestPropsValues.getUserId(), _group.getGroupId(),
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						null, RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						serviceContext);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, RandomTestUtil.randomString(),
					LayoutPageTemplateEntryTypeConstants.BASIC, 0,
					WorkflowConstants.STATUS_APPROVED, serviceContext);

			FragmentCollection fragmentCollection =
				_fragmentCollectionLocalService.addFragmentCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), serviceContext);

			String dropZoneId1 = RandomTestUtil.randomString();
			String dropZoneId2 = RandomTestUtil.randomString();

			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.addFragmentEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					fragmentCollection.getFragmentCollectionId(),
					StringUtil.randomString(), StringUtil.randomString(),
					RandomTestUtil.randomString(),
					StringBundler.concat(
						"<div class=\"fragment_1\"><h1> Drop Zone 1 </h1>",
						"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId1,
						"\"></lfr-drop-zone><h1> Drop Zone 2 </h1>",
						"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId2,
						"\"></lfr-drop-zone></div>"),
					RandomTestUtil.randomString(), false, "{fieldSets: []}",
					null, 0, false, false, FragmentConstants.TYPE_COMPONENT,
					null, WorkflowConstants.STATUS_APPROVED,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId()));

			_addFragmentEntryToLayoutPageTemplateEntry(
				fragmentEntry, layoutPageTemplateEntry);

			_assertLayoutPageTemplateEntryFragmentDropZoneLayoutStructureItems(
				new String[] {dropZoneId1, dropZoneId2}, fragmentEntry,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());

			File file = _layoutsExporter.exportLayoutPageTemplateEntries(
				new long[] {
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				},
				LayoutPageTemplateEntryTypeConstants.BASIC);

			_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

			_layoutsImporter.importFile(
				TestPropsValues.getUserId(), _group.getGroupId(), file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);

			_assertLayoutPageTemplateEntryFragmentDropZoneLayoutStructureItems(
				new String[] {dropZoneId1, dropZoneId2}, fragmentEntry,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testImportLayoutPageTemplateEntryDropZoneFragmentWithIdsAndPropagation()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					addLayoutPageTemplateCollection(
						null, TestPropsValues.getUserId(), _group.getGroupId(),
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						null, RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						serviceContext);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, RandomTestUtil.randomString(),
					LayoutPageTemplateEntryTypeConstants.BASIC, 0,
					WorkflowConstants.STATUS_APPROVED, serviceContext);

			FragmentCollection fragmentCollection =
				_fragmentCollectionLocalService.addFragmentCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), serviceContext);

			String dropZoneId1 = RandomTestUtil.randomString();
			String dropZoneId2 = RandomTestUtil.randomString();

			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.addFragmentEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					fragmentCollection.getFragmentCollectionId(),
					StringUtil.randomString(), StringUtil.randomString(),
					RandomTestUtil.randomString(),
					StringBundler.concat(
						"<div class=\"fragment_1\"><h1> Drop Zone 1 </h1>",
						"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId1,
						"\"></lfr-drop-zone><h1> Drop Zone 2 </h1>",
						"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId2,
						"\"></lfr-drop-zone></div>"),
					RandomTestUtil.randomString(), false, "{fieldSets: []}",
					null, 0, false, false, FragmentConstants.TYPE_COMPONENT,
					null, WorkflowConstants.STATUS_APPROVED,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId()));

			_addFragmentEntryToLayoutPageTemplateEntry(
				fragmentEntry, layoutPageTemplateEntry);

			_assertLayoutPageTemplateEntryFragmentDropZoneLayoutStructureItems(
				new String[] {dropZoneId1, dropZoneId2}, fragmentEntry,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());

			File file = _layoutsExporter.exportLayoutPageTemplateEntries(
				new long[] {
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				},
				LayoutPageTemplateEntryTypeConstants.BASIC);

			_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

			String dropZoneId3 = RandomTestUtil.randomString();

			fragmentEntry.setHtml(
				StringBundler.concat(
					"<div class=\"fragment_1\"><h1> Drop Zone 1 </h1>",
					"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId1,
					"\"></lfr-drop-zone><h1> Drop Zone 3 </h1>",
					"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId3,
					"\"></lfr-drop-zone><h1> Drop Zone 2 </h1>",
					"<lfr-drop-zone data-lfr-drop-zone-id=\"", dropZoneId2,
					"\"></lfr-drop-zone></div>"));

			fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
				fragmentEntry);

			_layoutsImporter.importFile(
				TestPropsValues.getUserId(), _group.getGroupId(), file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);

			_assertLayoutPageTemplateEntryFragmentDropZoneLayoutStructureItems(
				new String[] {dropZoneId1, dropZoneId3, dropZoneId2},
				fragmentEntry,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testImportLayoutPageTemplateEntryDropZoneFragmentWithPropagation()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					addLayoutPageTemplateCollection(
						null, TestPropsValues.getUserId(), _group.getGroupId(),
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						null, RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						serviceContext);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, RandomTestUtil.randomString(),
					LayoutPageTemplateEntryTypeConstants.BASIC, 0,
					WorkflowConstants.STATUS_APPROVED, serviceContext);

			FragmentCollection fragmentCollection =
				_fragmentCollectionLocalService.addFragmentCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), serviceContext);

			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.addFragmentEntry(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					fragmentCollection.getFragmentCollectionId(),
					StringUtil.randomString(), StringUtil.randomString(),
					RandomTestUtil.randomString(),
					StringBundler.concat(
						"<div class=\"fragment_1\"><h1> Drop Zone 1 </h1>",
						"<lfr-drop-zone></lfr-drop-zone><h1> Drop Zone 2 </h1>",
						"<lfr-drop-zone></lfr-drop-zone></div>"),
					RandomTestUtil.randomString(), false, "{fieldSets: []}",
					null, 0, false, false, FragmentConstants.TYPE_COMPONENT,
					null, WorkflowConstants.STATUS_APPROVED,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId()));

			_addFragmentEntryToLayoutPageTemplateEntry(
				fragmentEntry, layoutPageTemplateEntry);

			_assertLayoutPageTemplateEntryFragmentDropZoneLayoutStructureItems(
				new String[] {StringPool.BLANK, StringPool.BLANK},
				fragmentEntry,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());

			File file = _layoutsExporter.exportLayoutPageTemplateEntries(
				new long[] {
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				},
				LayoutPageTemplateEntryTypeConstants.BASIC);

			_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

			fragmentEntry.setHtml(
				StringBundler.concat(
					"<div class=\"fragment_1\"><h1> Drop Zone 1 </h1>",
					"<lfr-drop-zone></lfr-drop-zone><h1> Drop Zone 2 </h1>",
					"<lfr-drop-zone></lfr-drop-zone><h1> Drop Zone 3 </h1>",
					"<lfr-drop-zone></lfr-drop-zone></div>"));

			fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
				fragmentEntry);

			_layoutsImporter.importFile(
				TestPropsValues.getUserId(), _group.getGroupId(), file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);

			_assertLayoutPageTemplateEntryFragmentDropZoneLayoutStructureItems(
				new String[] {
					StringPool.BLANK, StringPool.BLANK, StringPool.BLANK
				},
				fragmentEntry,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testImportLayoutPageTemplateEntryHTMLFragment()
		throws Exception {

		String html =
			"<lfr-editable id=\"element-html\" type=\"html\">\n\t\t<h1>" +
				"\n\t\t\tEdited HTML\n\t\t</h1>\n\n\t\t<p>\n\t\t\tEdited " +
					"<strong>paragraph</strong>.\n\t\t</p>\n\t</lfr-editable>";

		_createFragmentEntry("test-html-fragment", "Test HTML Fragment", html);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry("html-fragment", new HashMap<>());

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			layoutPageTemplateEntry);

		_validateHTMLFragmentEntryLinkEditableValues(
			fragmentEntryLink.getEditableValues());
	}

	@Test
	public void testImportLayoutPageTemplateEntryImageFragment()
		throws Exception {

		String html =
			"<lfr-editable id=\"element-image\" type=\"image\"><img src=\"#\"" +
				"</lfr-editable>";

		_createFragmentEntry(
			"test-image-fragment", "Test Image Fragment", html);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry(
				"image-fragment", new HashMap<>());

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			layoutPageTemplateEntry);

		_validateImageFragmentEntryLinkEditableValues(
			fragmentEntryLink.getEditableValues());
	}

	@Test
	public void testImportLayoutPageTemplateEntryLinkFragment()
		throws Exception {

		String html =
			"<lfr-editable id=\"element-link\" type=\"link\"><a href=\"\">" +
				"Go Somewhere</a></lfr-editable>";

		_createFragmentEntry("test-link-fragment", "Test Link Fragment", html);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry("link-fragment", new HashMap<>());

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			layoutPageTemplateEntry);

		_validateLinkFragmentEntryLinkEditableValues(
			fragmentEntryLink.getEditableValues());
	}

	@Test
	public void testImportLayoutPageTemplateEntryTextFragment()
		throws Exception {

		String html =
			"<lfr-editable id=\"element-text\" type=\"text\">Test Text " +
				"Fragment</lfr-editable>";

		_createFragmentEntry("test-text-fragment", "Test Text Fragment", html);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getImportLayoutPageTemplateEntry("text-fragment", new HashMap<>());

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			layoutPageTemplateEntry);

		_validateTextFragmentEntryLinkEditableValues(
			fragmentEntryLink.getEditableValues());
	}

	@Test
	public void testImportLayoutPageTemplates() throws Exception {
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			_getLayoutsImporterResultEntries(
				"layout-page-template-multiple", new HashMap<>());

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 2,
			layoutsImporterResultEntries.size());

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_getLayoutPageTemplateEntry(layoutsImporterResultEntries, 0);
		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_getLayoutPageTemplateEntry(layoutsImporterResultEntries, 1);

		List<String> actualLayoutPageTemplateEntryNames = ListUtil.sort(
			new ArrayList() {
				{
					add(layoutPageTemplateEntry1.getName());
					add(layoutPageTemplateEntry2.getName());
				}
			});

		Assert.assertArrayEquals(
			new String[] {
				"Layout Page Template One", "Layout Page Template Two"
			},
			actualLayoutPageTemplateEntryNames.toArray(new String[0]));
	}

	@Test
	public void testImportLayoutPageTemplateWithCustomLookAndFeel()
		throws Exception {

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			_getLayoutsImporterResultEntries(
				"layout-page-template-custom-look-and-feel", new HashMap<>());

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 1,
			layoutsImporterResultEntries.size());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry(layoutsImporterResultEntries, 0);

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layout);

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		Assert.assertEquals(
			"false",
			typeSettingsUnicodeProperties.getProperty(
				"lfr-theme:regular:show-footer"));
		Assert.assertEquals(
			"false",
			typeSettingsUnicodeProperties.getProperty(
				"lfr-theme:regular:show-header"));
		Assert.assertEquals(
			"false",
			typeSettingsUnicodeProperties.getProperty(
				"lfr-theme:regular:show-header-search"));
		Assert.assertEquals(
			"true",
			typeSettingsUnicodeProperties.getProperty(
				"lfr-theme:regular:show-maximize-minimize-application-links"));
		Assert.assertEquals(
			"false",
			typeSettingsUnicodeProperties.getProperty(
				"lfr-theme:regular:wrap-widget-page-content"));
	}

	@Test
	public void testImportLayoutPageTemplateWithMasterPage() throws Exception {
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				"Test Master Page",
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_DRAFT,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			_getLayoutsImporterResultEntries(
				"layout-page-template-master-page", new HashMap<>());

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 1,
			layoutsImporterResultEntries.size());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry(layoutsImporterResultEntries, 0);

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			layout.getMasterLayoutPlid());
	}

	@Test
	public void testImportLayoutPageTemplateWithThumbnail() throws Exception {
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			_getLayoutsImporterResultEntries(
				"layout-page-template-thumbnail", new HashMap<>());

		Assert.assertEquals(
			layoutsImporterResultEntries.toString(), 1,
			layoutsImporterResultEntries.size());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry(layoutsImporterResultEntries, 0);

		Assert.assertNotNull(
			PortletFileRepositoryUtil.getPortletFileEntry(
				layoutPageTemplateEntry.getPreviewFileEntryId()));
	}

	private void _addFragmentEntryToLayoutPageTemplateEntry(
			FragmentEntry fragmentEntry,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				"{}", fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), draftLayout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				null, 0, defaultSegmentsExperienceId);

		for (FragmentEntryLinkListener fragmentEntryLinkListener :
				_fragmentEntryLinkListenerRegistry.
					getFragmentEntryLinkListeners()) {

			fragmentEntryLinkListener.onAddFragmentEntryLink(fragmentEntryLink);
		}

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);
	}

	private void _addZipWriterEntry(
			ZipWriter zipWriter, URL url, Map<String, String> valuesMap)
		throws IOException {

		String entryPath = url.getPath();

		String zipPath = StringUtil.removeSubstring(
			entryPath, _LAYOUT_PATE_TEMPLATES_PATH);

		zipWriter.addEntry(
			zipPath,
			StringUtil.replace(URLUtil.toString(url), "${", "}", valuesMap));
	}

	private void
			_assertLayoutPageTemplateEntryFragmentDropZoneLayoutStructureItems(
				String[] dropZoneIds, FragmentEntry fragmentEntry,
				String layoutPageTemplateEntryKey)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(), layoutPageTemplateEntryKey);

		Assert.assertNotNull(layoutPageTemplateEntry);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem mainChildLayoutStructureItem =
			_getMainChildLayoutStructureItem(layoutStructure);

		Assert.assertTrue(
			mainChildLayoutStructureItem instanceof
				FragmentStyledLayoutStructureItem);

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)mainChildLayoutStructureItem;

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertEquals(
			fragmentEntry.getFragmentEntryId(),
			fragmentEntryLink.getFragmentEntryId());

		List<String> childrenItemIds =
			fragmentStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), dropZoneIds.length,
			childrenItemIds.size());

		for (int i = 0; i < childrenItemIds.size(); i++) {
			String itemId = childrenItemIds.get(i);

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(itemId);

			Assert.assertTrue(
				layoutStructureItem instanceof
					FragmentDropZoneLayoutStructureItem);

			String dropZoneId = dropZoneIds[i];

			if (!Validator.isBlank(dropZoneId)) {
				FragmentDropZoneLayoutStructureItem
					fragmentDropZoneLayoutStructureItem =
						(FragmentDropZoneLayoutStructureItem)
							layoutStructureItem;

				Assert.assertEquals(
					dropZoneId,
					fragmentDropZoneLayoutStructureItem.
						getFragmentDropZoneId());
			}
		}

		for (String itemId : childrenItemIds) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(itemId);

			Assert.assertTrue(
				layoutStructureItem instanceof
					FragmentDropZoneLayoutStructureItem);
		}
	}

	private void _createFragmentEntry(String key, String name, String html)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				"Test Collection", StringPool.BLANK, serviceContext);

		_fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), key, name,
			StringPool.BLANK, html, StringPool.BLANK, false, StringPool.BLANK,
			null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private File _generateZipFile(String type, Map<String, String> valuesMap)
		throws Exception {

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		Enumeration<URL> enumeration = _bundle.findEntries(
			StringBundler.concat(
				_LAYOUT_PATE_TEMPLATES_PATH + type,
				StringPool.FORWARD_SLASH + _ROOT_FOLDER,
				StringPool.FORWARD_SLASH),
			LayoutPageTemplateExportImportConstants.
				FILE_NAME_PAGE_TEMPLATE_COLLECTION,
			true);

		try {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				_populateZipWriter(zipWriter, url, valuesMap);
			}

			return zipWriter.getFile();
		}
		catch (Exception exception) {
			throw new Exception(exception);
		}
	}

	private FragmentEntryLink _getFragmentEntryLink(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem layoutStructureItem =
			_getMainChildLayoutStructureItem(layoutStructure);

		Assert.assertTrue(
			layoutStructureItem instanceof FragmentStyledLayoutStructureItem);

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)layoutStructureItem;

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertNotNull(fragmentEntryLink);

		return fragmentEntryLink;
	}

	private LayoutPageTemplateEntry _getImportLayoutPageTemplateEntry(
			String type, Map<String, String> valuesMap)
		throws Exception {

		File file = _generateZipFile(type, valuesMap);

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries = null;

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			layoutsImporterResultEntries = _layoutsImporter.importFile(
				_user.getUserId(), _group.getGroupId(), 0, file,
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

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(), layoutPageTemplateEntryKey);

		Assert.assertNotNull(layoutPageTemplateEntry);

		return layoutPageTemplateEntry;
	}

	private LayoutPageTemplateEntry _getLayoutPageTemplateEntry(
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
		int index) {

		LayoutsImporterResultEntry layoutsImporterResultEntry =
			layoutsImporterResultEntries.get(index);

		Assert.assertEquals(
			LayoutsImporterResultEntry.Status.IMPORTED,
			layoutsImporterResultEntry.getStatus());

		String layoutPageTemplateEntryKey = StringUtil.toLowerCase(
			layoutsImporterResultEntry.getName());

		layoutPageTemplateEntryKey = StringUtil.replace(
			layoutPageTemplateEntryKey, CharPool.SPACE, CharPool.DASH);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(), layoutPageTemplateEntryKey);

		Assert.assertNotNull(layoutPageTemplateEntry);

		return layoutPageTemplateEntry;
	}

	private List<LayoutsImporterResultEntry> _getLayoutsImporterResultEntries(
			String testCaseName, Map<String, String> valuesMap)
		throws Exception {

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries = null;

		File file = _generateZipFile(testCaseName, valuesMap);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			layoutsImporterResultEntries = _layoutsImporter.importFile(
				_user.getUserId(), _group.getGroupId(), 0, file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertNotNull(layoutsImporterResultEntries);

		return layoutsImporterResultEntries;
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

	private void _populateZipWriter(
			ZipWriter zipWriter, URL url, Map<String, String> valuesMap)
		throws IOException {

		String zipPath = StringUtil.removeSubstring(
			url.getFile(), _LAYOUT_PATE_TEMPLATES_PATH);

		try (InputStream inputStream = url.openStream()) {
			zipWriter.addEntry(zipPath, inputStream);
		}

		String path = FileUtil.getPath(url.getPath());

		Enumeration<URL> enumeration = _bundle.findEntries(
			path,
			LayoutPageTemplateExportImportConstants.FILE_NAME_PAGE_TEMPLATE,
			true);

		while (enumeration.hasMoreElements()) {
			URL elementURL = enumeration.nextElement();

			_addZipWriterEntry(zipWriter, elementURL, valuesMap);
		}

		enumeration = _bundle.findEntries(
			path,
			LayoutPageTemplateExportImportConstants.FILE_NAME_PAGE_DEFINITION,
			true);

		while (enumeration.hasMoreElements()) {
			URL elementURL = enumeration.nextElement();

			_addZipWriterEntry(zipWriter, elementURL, valuesMap);
		}

		enumeration = _bundle.findEntries(path, "thumbnail.png", true);

		if (enumeration == null) {
			return;
		}

		while (enumeration.hasMoreElements()) {
			URL elementURL = enumeration.nextElement();

			_addZipWriterEntry(zipWriter, elementURL, valuesMap);
		}
	}

	private void _registerTestPortlet(String portletId) throws Exception {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				Portlet.class, new PageTemplatesImporterTest.TestPortlet(),
				HashMapDictionaryBuilder.put(
					"com.liferay.portlet.instanceable", "true"
				).put(
					"jakarta.portlet.name", portletId
				).build()));
	}

	private void _validateHTMLFragmentEntryLinkEditableValues(
			String editableValues)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			editableValues);

		JSONObject editableFragmentEntryProcessorJSONObject =
			jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertNotNull(editableFragmentEntryProcessorJSONObject);

		JSONObject elementJSONObject =
			editableFragmentEntryProcessorJSONObject.getJSONObject(
				"element-html");

		Assert.assertNotNull(elementJSONObject);

		Assert.assertEquals(
			"\n\t\t<h1>\n\t\t\tEdited HTML\n\t\t</h1>\n\n\t\t<p>\n\t\t\t" +
				"Edited <strong>paragraph</strong>.\n\t\t</p>\n\t",
			elementJSONObject.getString("en_US"));
	}

	private void _validateImageFragmentEntryLinkEditableValues(
			String editableValues)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			editableValues);

		JSONObject editableFragmentEntryProcessorJSONObject =
			jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertNotNull(editableFragmentEntryProcessorJSONObject);

		JSONObject elementJSONObject =
			editableFragmentEntryProcessorJSONObject.getJSONObject(
				"element-image");

		Assert.assertNotNull(elementJSONObject);

		JSONObject configJSONObject = elementJSONObject.getJSONObject("config");

		Assert.assertNotNull(configJSONObject);

		Assert.assertEquals(
			"Test image description", configJSONObject.getString("alt"));
		Assert.assertEquals("www.test.com", configJSONObject.getString("href"));
		Assert.assertEquals("_blank", configJSONObject.getString("target"));

		JSONObject freeMarkerFragmentEntryProcessorJSONObject =
			jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertNotNull(freeMarkerFragmentEntryProcessorJSONObject);

		Assert.assertEquals(
			"4",
			freeMarkerFragmentEntryProcessorJSONObject.getString(
				"bottomSpacing"));
		Assert.assertEquals(
			"center",
			freeMarkerFragmentEntryProcessorJSONObject.getString("imageAlign"));
		Assert.assertEquals(
			"w-0",
			freeMarkerFragmentEntryProcessorJSONObject.getString("imageSize"));
	}

	private void _validateLinkFragmentEntryLinkEditableValues(
			String editableValues)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			editableValues);

		JSONObject editableFragmentEntryProcessorJSONObject =
			jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertNotNull(editableFragmentEntryProcessorJSONObject);

		JSONObject elementJSONObject =
			editableFragmentEntryProcessorJSONObject.getJSONObject(
				"element-link");

		Assert.assertNotNull(elementJSONObject);

		JSONObject configJSONObject = elementJSONObject.getJSONObject("config");

		Assert.assertNotNull(configJSONObject);

		Assert.assertEquals(
			"http://www.test.com", configJSONObject.getString("href"));

		Assert.assertEquals(
			"Edited Link", elementJSONObject.getString("en_US"));
	}

	private void _validateTextFragmentEntryLinkEditableValues(
			String editableValues)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			editableValues);

		JSONObject editableFragmentEntryProcessorJSONObject =
			jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertNotNull(editableFragmentEntryProcessorJSONObject);

		JSONObject elementJSONObject =
			editableFragmentEntryProcessorJSONObject.getJSONObject(
				"element-text");

		Assert.assertNotNull(elementJSONObject);

		JSONObject configJSONObject = elementJSONObject.getJSONObject("config");

		Assert.assertNotNull(configJSONObject);

		Assert.assertEquals("www.test.com", configJSONObject.getString("href"));

		Assert.assertEquals("_blank", configJSONObject.getString("target"));

		Assert.assertEquals(
			"Edited Text", elementJSONObject.getString("en_US"));

		JSONObject freeMarkerFragmentEntryProcessorJSONObject =
			jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertNotNull(freeMarkerFragmentEntryProcessorJSONObject);

		Assert.assertEquals(
			"2",
			freeMarkerFragmentEntryProcessorJSONObject.getString(
				"bottomSpacing"));
		Assert.assertEquals(
			"h2",
			freeMarkerFragmentEntryProcessorJSONObject.getString(
				"headingLevel"));
		Assert.assertEquals(
			"center",
			freeMarkerFragmentEntryProcessorJSONObject.getString("textAlign"));
		Assert.assertEquals(
			"danger",
			freeMarkerFragmentEntryProcessorJSONObject.getString("textColor"));
	}

	private static final String _LAYOUT_PATE_TEMPLATES_PATH =
		"com/liferay/layout/page/template/internal/importer/test/dependencies/";

	private static final String _ROOT_FOLDER = "page-templates";

	private Bundle _bundle;
	private BundleContext _bundleContext;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutsExporter _layoutsExporter;

	@Inject
	private LayoutsImporter _layoutsImporter;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new CopyOnWriteArrayList<>();
	private String _testPortletName;
	private User _user;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

	private class TestPortlet extends GenericPortlet {
	}

}