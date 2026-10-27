/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplatePortletDataHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_importedGroup = GroupTestUtil.addGroup();
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-106595")
	public void testExportImportDesignLibraryLayoutPageTemplates()
		throws Exception {

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		Group designLibraryGroup = _addDesignLibraryGroup();

		LayoutPageTemplateCollection basicLayoutPageTemplateCollection =
			_addLayoutPageTemplateCollection(
				designLibraryGroup.getGroupId(),
				LayoutPageTemplateCollectionTypeConstants.BASIC);

		LayoutPageTemplateEntry basicLayoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				0, designLibraryGroup.getGroupId(),
				basicLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				LayoutPageTemplateEntryTypeConstants.BASIC);

		long journalArticleClassNameId = _portal.getClassNameId(
			JournalArticle.class.getName());

		LayoutPageTemplateEntry displayPageLayoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				journalArticleClassNameId, designLibraryGroup.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

		Group importedDesignLibraryGroup = _addDesignLibraryGroup();

		long importedDesignLibraryGroupId =
			importedDesignLibraryGroup.getGroupId();

		_importLayouts(
			false, importedDesignLibraryGroupId,
			_exportLayouts(false, designLibraryGroup.getGroupId()));

		_assertExists(
			importedDesignLibraryGroupId, basicLayoutPageTemplateCollection);
		_assertExists(
			importedDesignLibraryGroupId, basicLayoutPageTemplateEntry);
		_assertExists(
			importedDesignLibraryGroupId, displayPageLayoutPageTemplateEntry);
	}

	@Test
	public void testExportImportLayoutPageTemplateCollectionsDeletions()
		throws Exception {

		LayoutPageTemplateCollection basicLayoutPageTemplateCollection1 =
			_addLayoutPageTemplateCollection(
				_group.getGroupId(),
				LayoutPageTemplateCollectionTypeConstants.BASIC);
		LayoutPageTemplateCollection basicLayoutPageTemplateCollection2 =
			_addLayoutPageTemplateCollection(
				_group.getGroupId(),
				LayoutPageTemplateCollectionTypeConstants.BASIC);
		LayoutPageTemplateCollection displayPageLayoutPageTemplateCollection1 =
			_addLayoutPageTemplateCollection(
				_group.getGroupId(),
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE);
		LayoutPageTemplateCollection displayPageLayoutPageTemplateCollection2 =
			_addLayoutPageTemplateCollection(
				_group.getGroupId(),
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE);

		_importLayouts(
			false, _importedGroup.getGroupId(),
			_exportLayouts(false, _group.getGroupId()));

		_assertExists(
			_importedGroup.getGroupId(), basicLayoutPageTemplateCollection1);
		_assertExists(
			_importedGroup.getGroupId(), basicLayoutPageTemplateCollection2);
		_assertExists(
			_importedGroup.getGroupId(),
			displayPageLayoutPageTemplateCollection1);
		_assertExists(
			_importedGroup.getGroupId(),
			displayPageLayoutPageTemplateCollection2);

		_layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(
				basicLayoutPageTemplateCollection2);
		_layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(
				displayPageLayoutPageTemplateCollection2);

		_importLayouts(
			true, _importedGroup.getGroupId(),
			_exportLayouts(true, _group.getGroupId()));

		_assertExists(
			_importedGroup.getGroupId(), basicLayoutPageTemplateCollection1);
		_assertDoesNotExist(
			_importedGroup.getGroupId(), basicLayoutPageTemplateCollection2);
		_assertExists(
			_importedGroup.getGroupId(),
			displayPageLayoutPageTemplateCollection1);
		_assertDoesNotExist(
			_importedGroup.getGroupId(),
			displayPageLayoutPageTemplateCollection2);
	}

	@Test
	public void testExportImportLayoutPageTemplateEntriesDeletions()
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_addLayoutPageTemplateCollection(
				_group.getGroupId(),
				LayoutPageTemplateCollectionTypeConstants.BASIC);

		LayoutPageTemplateEntry basicLayoutPageTemplateEntry1 =
			_addLayoutPageTemplateEntry(
				0, _group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				LayoutPageTemplateEntryTypeConstants.BASIC);
		LayoutPageTemplateEntry basicLayoutPageTemplateEntry2 =
			_addLayoutPageTemplateEntry(
				0, _group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				LayoutPageTemplateEntryTypeConstants.BASIC);

		long journalArticleClassNameId = _portal.getClassNameId(
			JournalArticle.class.getName());

		LayoutPageTemplateEntry displayPageLayoutPageTemplateEntry1 =
			_addLayoutPageTemplateEntry(
				journalArticleClassNameId, _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);
		LayoutPageTemplateEntry displayPageLayoutPageTemplateEntry2 =
			_addLayoutPageTemplateEntry(
				journalArticleClassNameId, _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry1 =
			_addLayoutPageTemplateEntry(
				0, _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry2 =
			_addLayoutPageTemplateEntry(
				0, _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);
		LayoutPageTemplateEntry widgetLayoutPageTemplateEntry1 =
			_addLayoutPageTemplateEntry(
				0, _group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE);
		LayoutPageTemplateEntry widgetLayoutPageTemplateEntry2 =
			_addLayoutPageTemplateEntry(
				0, _group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE);

		_importLayouts(
			false, _importedGroup.getGroupId(),
			_exportLayouts(false, _group.getGroupId()));

		_assertExists(
			_importedGroup.getGroupId(), basicLayoutPageTemplateEntry1);
		_assertExists(
			_importedGroup.getGroupId(), basicLayoutPageTemplateEntry2);
		_assertExists(
			_importedGroup.getGroupId(), displayPageLayoutPageTemplateEntry1);
		_assertExists(
			_importedGroup.getGroupId(), displayPageLayoutPageTemplateEntry2);
		_assertExists(
			_importedGroup.getGroupId(), masterLayoutPageTemplateEntry1);
		_assertExists(
			_importedGroup.getGroupId(), masterLayoutPageTemplateEntry2);
		_assertExists(
			_importedGroup.getGroupId(), widgetLayoutPageTemplateEntry1);
		_assertExists(
			_importedGroup.getGroupId(), widgetLayoutPageTemplateEntry2);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			basicLayoutPageTemplateEntry2);
		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			displayPageLayoutPageTemplateEntry2);
		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			masterLayoutPageTemplateEntry2);
		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			widgetLayoutPageTemplateEntry2);

		_assertJSONTypeInfoPropertyValue(
			"ContentPageTemplate", basicLayoutPageTemplateEntry2);
		_assertJSONTypeInfoPropertyValue(
			"WidgetPageTemplate", widgetLayoutPageTemplateEntry2);

		_importLayouts(
			true, _importedGroup.getGroupId(),
			_exportLayouts(true, _group.getGroupId()));

		_assertExists(
			_importedGroup.getGroupId(), basicLayoutPageTemplateEntry1);
		_assertDoesNotExist(
			_importedGroup.getGroupId(), basicLayoutPageTemplateEntry2);
		_assertExists(
			_importedGroup.getGroupId(), displayPageLayoutPageTemplateEntry1);
		_assertDoesNotExist(
			_importedGroup.getGroupId(), displayPageLayoutPageTemplateEntry2);
		_assertExists(
			_importedGroup.getGroupId(), masterLayoutPageTemplateEntry1);
		_assertDoesNotExist(
			_importedGroup.getGroupId(), masterLayoutPageTemplateEntry2);
		_assertExists(
			_importedGroup.getGroupId(), widgetLayoutPageTemplateEntry1);
		_assertDoesNotExist(
			_importedGroup.getGroupId(), widgetLayoutPageTemplateEntry2);
	}

	private Group _addDesignLibraryGroup() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry.getGroup();
	}

	private LayoutPageTemplateCollection _addLayoutPageTemplateCollection(
			long groupId, int type)
		throws Exception {

		return _layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(
				null, TestPropsValues.getUserId(), groupId,
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), type,
				ServiceContextTestUtil.getServiceContext(
					groupId, TestPropsValues.getUserId()));
	}

	private LayoutPageTemplateEntry _addLayoutPageTemplateEntry(
			long classNameId, long groupId, long layoutPageTemplateCollectionId,
			int type)
		throws Exception {

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			layoutPageTemplateCollectionId, null, classNameId, null,
			RandomTestUtil.randomString(), type, 0, false, 0, 0, 0,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	private void _assertDoesNotExist(
		long groupId,
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					layoutPageTemplateCollection.getExternalReferenceCode(),
					groupId));
	}

	private void _assertDoesNotExist(
		long groupId, LayoutPageTemplateEntry layoutPageTemplateEntry) {

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					layoutPageTemplateEntry.getExternalReferenceCode(),
					groupId));
	}

	private void _assertExists(
		long groupId,
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		Assert.assertNotNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					layoutPageTemplateCollection.getExternalReferenceCode(),
					groupId));
	}

	private void _assertExists(
		long groupId, LayoutPageTemplateEntry layoutPageTemplateEntry) {

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					layoutPageTemplateEntry.getExternalReferenceCode(),
					groupId));
	}

	private void _assertJSONTypeInfoPropertyValue(
			String jsonTypeInfoPropertyValue,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		SystemEvent systemEvent = _systemEventLocalService.fetchSystemEvent(
			layoutPageTemplateEntry.getGroupId(),
			_portal.getClassNameId(LayoutPageTemplateEntry.class),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			SystemEventConstants.TYPE_DELETE);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			systemEvent.getExtraData());

		Assert.assertEquals(
			jsonTypeInfoPropertyValue,
			jsonObject.getString("jsonTypeInfoPropertyValue"));
	}

	private File _exportLayouts(boolean deletions, long groupId)
		throws Exception {

		return _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, false,
							new long[0], _getParameterMap(deletions))));
	}

	private Map<String, String[]> _getParameterMap(boolean deletions) {
		return HashMapBuilder.put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.toString(deletions)}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA + "_" +
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	private void _importLayouts(boolean deletions, long groupId, File larFile)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, false,
							new long[0], _getParameterMap(deletions)));

		if (deletions) {
			_exportImportLocalService.importLayoutsDataDeletions(
				exportImportConfiguration, larFile);
		}

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Group _importedGroup;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

}