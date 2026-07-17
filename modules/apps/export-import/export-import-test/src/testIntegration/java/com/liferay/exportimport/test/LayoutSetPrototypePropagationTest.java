/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.background.task.constants.LayoutSetPrototypeBackgroundTaskConstants;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalContent;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.exception.LayoutParentLayoutIdException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.ThemeSetting;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.ThemeSettingImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.PortletPreferences;

import java.awt.image.BufferedImage;

import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Julio Camarero
 * @author Eduardo García
 */
@FeatureFlag("LPD-82107")
@RunWith(Arquillian.class)
@Sync
public class LayoutSetPrototypePropagationTest
	extends BasePrototypePropagationTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			LayoutSetPrototypePropagationTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testAddChildLayoutWithLinkDisabled() throws Exception {
		testAddChildLayout(false);
	}

	@Test
	public void testAddChildLayoutWithLinkEnabled() throws Exception {
		testAddChildLayout(true);
	}

	@Test
	public void testAddGroup() throws Exception {
		Assert.assertEquals(_initialPrototypeLayoutsCount, _initialLayoutCount);
	}

	@Test
	public void testIsLayoutDeleteable() throws Exception {
		Assert.assertFalse(layout.isLayoutDeleteable());

		setLinkEnabled(false);

		Assert.assertTrue(layout.isLayoutDeleteable());
	}

	@Test
	public void testIsLayoutSortable() throws Exception {
		Assert.assertFalse(layout.isLayoutSortable());

		setLinkEnabled(false);

		Assert.assertTrue(layout.isLayoutSortable());
	}

	@Test
	public void testIsLayoutUpdateable() throws Exception {
		doTestIsLayoutUpdateable();
	}

	@Test
	public void testLayoutPermissionPropagation() throws Exception {
		_testLayoutPermissionPropagation(false);
		_testLayoutPermissionPropagation(true);
	}

	@Test
	@TestInfo("LPD-50062")
	public void testLayoutPropagationWithFragmentEntries() throws Exception {
		setLinkEnabled(true);

		Layout layout1 = _addLayout(_layoutSetPrototypeGroup.getGroupId());

		Layout draftLayout1 = layout1.fetchDraftLayout();

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(),
				_layoutSetPrototypeGroup.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(
					_layoutSetPrototypeGroup.getGroupId()));

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(),
				_layoutSetPrototypeGroup.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringPool.BLANK, "<h1>Heading Example</h1>", StringPool.BLANK,
				false, StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_layoutSetPrototypeGroup.getGroupId()));

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, fragmentEntry.getCss(),
			fragmentEntry.getConfiguration(),
			fragmentEntry.getExternalReferenceCode(), null,
			fragmentEntry.getHtml(), fragmentEntry.getJs(), draftLayout1,
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(), null,
			0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout1.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout1, layout1);

		propagateChanges(group);

		_fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(), "<h1>Updated Heading Example</h1>",
			fragmentEntry.getJs(), fragmentEntry.isCacheable(),
			fragmentEntry.getConfiguration(), fragmentEntry.getIcon(),
			fragmentEntry.getPreviewFileEntryId(), fragmentEntry.isReadOnly(),
			fragmentEntry.getTypeOptions(), fragmentEntry.getStatus());

		propagateChanges(group);
	}

	@Test
	public void testLayoutPropagationWithLayoutPrototypeLinkDisabled()
		throws Exception {

		doTestLayoutPropagationWithLayoutPrototype(false);
	}

	@Test
	public void testLayoutPropagationWithLayoutPrototypeLinkEnabled()
		throws Exception {

		doTestLayoutPropagationWithLayoutPrototype(true);
	}

	@Test
	public void testLayoutPropagationWithLinkDisabled() throws Exception {
		doTestLayoutPropagation(false);
	}

	@Test
	@TestInfo("LPD-80908")
	public void testLayoutPropagationWithLinkEnabled() throws Exception {
		doTestLayoutPropagation(true);
	}

	@Test
	@TestInfo("LPS-161955")
	public void testLayoutPropagationWithMasterLayout() throws Exception {
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutSetPrototypeGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		LayoutTestUtil.addTypeContentLayout(
			_layoutSetPrototypeGroup, true, false,
			masterLayoutPageTemplateEntry.getExternalReferenceCode());

		LayoutTestUtil.addTypeContentLayout(
			_layoutSetPrototypeGroup, true, false,
			masterLayoutPageTemplateEntry.getExternalReferenceCode());

		Assert.assertEquals(
			0,
			LayoutLocalServiceUtil.getMasterLayoutsCount(
				group.getGroupId(),
				masterLayoutPageTemplateEntry.getExternalReferenceCode()));

		Assert.assertNull(
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					masterLayoutPageTemplateEntry.getExternalReferenceCode(),
					group.getGroupId()));

		propagateChanges(group);

		Assert.assertEquals(
			4,
			LayoutLocalServiceUtil.getMasterLayoutsCount(
				group.getGroupId(),
				masterLayoutPageTemplateEntry.getExternalReferenceCode()));

		Assert.assertNotNull(
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					masterLayoutPageTemplateEntry.getExternalReferenceCode(),
					group.getGroupId()));

		Layout masterLayout = LayoutLocalServiceUtil.getLayout(
			masterLayoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(
			LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				masterLayout.getExternalReferenceCode(), group.getGroupId()));
	}

	@Test
	public void testLayoutPropagationWithPortletPreferencesAfterRepublishingLayout()
		throws Exception {

		String portletName = "com_liferay_test_portlet_TestPortlet";

		_registerTestPortlet(portletName);

		Layout layoutSetPrototypePublishedLayout = _addLayout(
			_layoutSetPrototypeGroup.getGroupId());

		Layout layoutSetPrototypeDraftLayout =
			layoutSetPrototypePublishedLayout.fetchDraftLayout();

		String portletId = _addPortletToLayout(
			layoutSetPrototypeDraftLayout, portletName);

		PortletPreferencesIds portletPreferencesIds =
			_portletPreferencesFactory.getPortletPreferencesIds(
				layoutSetPrototypeDraftLayout.getCompanyId(),
				layoutSetPrototypeDraftLayout.getGroupId(), 0,
				layoutSetPrototypeDraftLayout.getPlid(), portletId);

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPreferences(
				portletPreferencesIds);

		String key = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		portletPreferences.setValue(key, value);

		_portletPreferencesLocalService.updatePreferences(
			portletPreferencesIds.getOwnerId(),
			portletPreferencesIds.getOwnerType(),
			portletPreferencesIds.getPlid(),
			portletPreferencesIds.getPortletId(), portletPreferences);

		ContentLayoutTestUtil.publishLayout(
			layoutSetPrototypeDraftLayout, layoutSetPrototypePublishedLayout);

		propagateChanges(group);

		Layout groupPublishedLayout =
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false,
				layoutSetPrototypePublishedLayout.getFriendlyURL());

		_verifyPortletPreferenceValue(
			groupPublishedLayout, portletId, key, value);

		Layout groupDrafLayout = groupPublishedLayout.fetchDraftLayout();

		_verifyPortletPreferenceValue(groupDrafLayout, portletId, key, value);

		ContentLayoutTestUtil.publishLayout(
			layoutSetPrototypeDraftLayout, layoutSetPrototypePublishedLayout);

		propagateChanges(group);

		_verifyPortletPreferenceValue(
			groupPublishedLayout, portletId, key, value);

		_verifyPortletPreferenceValue(groupDrafLayout, portletId, key, value);
	}

	@Test
	@TestInfo("LPD-81019")
	public void testLayoutSetPrototypeLayoutERCPropagation() throws Exception {
		long prototypeGroupId = _layoutSetPrototypeGroup.getGroupId();

		Layout contentLayout = _addLayout(prototypeGroupId);
		Layout embeddedLayout = LayoutTestUtil.addTypeEmbeddedLayout(
			prototypeGroupId, true);
		Layout linkToPageLayout = LayoutTestUtil.addTypeLinkToLayoutLayout(
			prototypeGroupId, true, prototypeLayout.getLayoutId());
		Layout linkToURLLayout = LayoutTestUtil.addTypeLinkToURLLayout(
			prototypeGroupId, true, "http://www.liferay.com");
		Layout nodeLayout = LayoutTestUtil.addTypeNodeLayout(
			prototypeGroupId, true);
		Layout widgetLayout = LayoutTestUtil.addTypePortletLayout(
			prototypeGroupId, true);

		propagateChanges(group);

		_assertLayoutSetPrototypeLayoutERC(contentLayout, group.getGroupId());
		_assertLayoutSetPrototypeLayoutERC(embeddedLayout, group.getGroupId());
		_assertLayoutSetPrototypeLayoutERC(
			linkToPageLayout, group.getGroupId());
		_assertLayoutSetPrototypeLayoutERC(linkToURLLayout, group.getGroupId());
		_assertLayoutSetPrototypeLayoutERC(nodeLayout, group.getGroupId());
		_assertLayoutSetPrototypeLayoutERC(widgetLayout, group.getGroupId());
	}

	@Test
	public void testLayoutSetPrototypePropagation() throws Exception {
		long userId = TestPropsValues.getUserId();

		int initialCount = _layoutLocalService.getLayoutsCount(
			group.getGroupId(), false);

		LayoutTestUtil.addTypePortletLayout(_layoutSetPrototypeGroup, true);

		long timestamp = System.currentTimeMillis();

		propagateChanges(false, group);

		_assertNotification("successful", timestamp, userId);

		Assert.assertEquals(
			initialCount + 1,
			_layoutLocalService.getLayoutsCount(group.getGroupId(), false));
	}

	@Test
	public void testLayoutSetPrototypePropagationCheckNotification()
		throws Exception {

		_testLayoutSetPrototypePropagationCheckNotification(
			"completed-with-errors",
			_getRandomStatus(
				BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
				BackgroundTaskConstants.STATUS_SUCCESSFUL),
			BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS);

		_testLayoutSetPrototypePropagationCheckNotification(
			"failed",
			_getRandomStatus(
				BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
				BackgroundTaskConstants.STATUS_SUCCESSFUL),
			BackgroundTaskConstants.STATUS_FAILED);

		_testLayoutSetPrototypePropagationCheckNotification(
			"successful", BackgroundTaskConstants.STATUS_SUCCESSFUL,
			BackgroundTaskConstants.STATUS_SUCCESSFUL);
	}

	@Test
	public void testLayoutSetPrototypePropagationDeletesCacheFile()
		throws Exception {

		_testLayoutSetPrototypePropagation(true);
	}

	@Test
	public void testLayoutSetPrototypePropagationDoesNotPropagateDeletions()
		throws Exception {

		long userId = TestPropsValues.getUserId();

		int initialCount = _layoutLocalService.getLayoutsCount(
			group.getGroupId(), false);

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		propagateChanges(group);

		Assert.assertEquals(
			initialCount + 2,
			_layoutLocalService.getLayoutsCount(group.getGroupId(), false));

		_layoutLocalService.deleteLayout(layout1);

		long timestamp = System.currentTimeMillis();

		propagateChanges(false, group);

		_assertNotification("successful", timestamp, userId);

		Assert.assertEquals(
			initialCount + 2,
			_layoutLocalService.getLayoutsCount(group.getGroupId(), false));

		layout1 = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false, layout1.getFriendlyURL());

		Assert.assertNull(layout1.getLayoutSetPrototypeLayout());

		layout2 = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false, layout2.getFriendlyURL());

		Assert.assertNotNull(layout2.getLayoutSetPrototypeLayout());
	}

	@Test
	public void testLayoutSetPrototypePropagationKeepsCacheFile()
		throws Exception {

		_testLayoutSetPrototypePropagation(false);
	}

	@Test
	public void testLayoutSetPrototypePropagationOverridesLogo()
		throws Exception {

		LayoutSetLocalServiceUtil.updateLogo(
			group.getGroupId(), false, true,
			ImageToolUtil.getBytes(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "png"));

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			group.getGroupId(), false);

		long logoId = layoutSet.getLogoId();

		LayoutSetLocalServiceUtil.updateLogo(
			_layoutSetPrototypeGroup.getGroupId(), true, true,
			ImageToolUtil.getBytes(
				new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png"));

		long timestamp = System.currentTimeMillis();

		propagateChanges(false, group);

		_assertNotification(
			"successful", timestamp, TestPropsValues.getUserId());

		layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			group.getGroupId(), false);

		Assert.assertNotEquals(logoId, layoutSet.getLogoId());
	}

	@Test
	public void testLayoutSetPrototypePropagationOverridesSiteChanges()
		throws Exception {

		AssetVocabulary layoutSetPrototypeAssetVocabulary =
			AssetTestUtil.addVocabulary(_layoutSetPrototypeGroup.getGroupId());

		AssetCategory layoutSetPrototypeAssetCategory =
			AssetTestUtil.addCategory(
				_layoutSetPrototypeGroup.getGroupId(),
				layoutSetPrototypeAssetVocabulary.getVocabularyId());

		String title = layoutSetPrototypeAssetCategory.getTitle(
			LocaleUtil.getSiteDefault());

		AssetVocabulary siteAssetVocabulary = AssetTestUtil.addVocabulary(
			group.getGroupId());

		AssetCategory siteAssetCategory = AssetTestUtil.addCategory(
			group.getGroupId(), siteAssetVocabulary.getVocabularyId());

		long timestamp = System.currentTimeMillis();

		propagateChanges(false, group);

		_assertNotification(
			"successful", timestamp, TestPropsValues.getUserId());

		AssetCategory assetCategory =
			AssetCategoryLocalServiceUtil.fetchAssetCategoryByUuidAndGroupId(
				layoutSetPrototypeAssetCategory.getUuid(), group.getGroupId());

		assetCategory = AssetCategoryLocalServiceUtil.updateCategory(
			assetCategory.getExternalReferenceCode(),
			TestPropsValues.getUserId(), assetCategory.getCategoryId(),
			assetCategory.getParentCategoryId(),
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), "New Asset Category"),
			assetCategory.getDescriptionMap(), assetCategory.getVocabularyId(),
			null, ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertEquals(
			"New Asset Category",
			assetCategory.getTitle(LocaleUtil.getSiteDefault()));

		timestamp = System.currentTimeMillis();

		propagateChanges(false, group);

		_assertNotification(
			"successful", timestamp, TestPropsValues.getUserId());

		assetCategory =
			AssetCategoryLocalServiceUtil.getAssetCategoryByUuidAndGroupId(
				layoutSetPrototypeAssetCategory.getUuid(), group.getGroupId());

		Assert.assertEquals(
			title, assetCategory.getTitle(LocaleUtil.getSiteDefault()));

		Assert.assertNotNull(
			AssetCategoryLocalServiceUtil.fetchAssetCategory(
				siteAssetCategory.getCategoryId()));
	}

	@Test
	public void testLayoutSetPrototypePropagationToAllLinkedSites()
		throws Exception {

		long userId = TestPropsValues.getUserId();

		Group group2 = GroupTestUtil.addGroup();

		_sites.updateLayoutSetPrototypesLinks(
			group2, _layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			true);

		long timestamp1 = System.currentTimeMillis();

		propagateChanges(false, _layoutSetPrototype);

		_assertNotification("successful", timestamp1, userId);

		int initialCount1 = _layoutLocalService.getLayoutsCount(
			group.getGroupId(), false);
		int initialCount2 = _layoutLocalService.getLayoutsCount(
			group2.getGroupId(), false);

		LayoutTestUtil.addTypePortletLayout(_layoutSetPrototypeGroup, true);

		long timestamp2 = System.currentTimeMillis();

		propagateChanges(false, _layoutSetPrototype);

		_assertNotification("successful", timestamp2, userId);

		Assert.assertEquals(
			initialCount1 + 1,
			_layoutLocalService.getLayoutsCount(group.getGroupId(), false));
		Assert.assertEquals(
			initialCount2 + 1,
			_layoutLocalService.getLayoutsCount(group2.getGroupId(), false));

		GroupLocalServiceUtil.deleteGroup(group2);
	}

	@Test
	public void testLayoutSetPrototypePropagationWithExportImportInProcess()
		throws Exception {

		_testLayoutSetPrototypePropagationWithExportImportInProcess(
			ExportImportThreadLocal::setLayoutExportInProcess);
		_testLayoutSetPrototypePropagationWithExportImportInProcess(
			ExportImportThreadLocal::setLayoutImportInProcess);
		_testLayoutSetPrototypePropagationWithExportImportInProcess(
			ExportImportThreadLocal::setLayoutStagingInProcess);
	}

	@Test
	public void testLayoutSetPrototypePropagationWithLinkDisabled()
		throws Exception {

		long userId = TestPropsValues.getUserId();

		_sites.updateLayoutSetPrototypesLinks(
			group, _layoutSetPrototype.getLayoutSetPrototypeId(), 0, false,
			false);

		int initialCount = _layoutLocalService.getLayoutsCount(
			group.getGroupId(), false);

		LayoutTestUtil.addTypePortletLayout(_layoutSetPrototypeGroup, true);

		long timestamp = System.currentTimeMillis();

		propagateChanges(false, group);

		_assertNotification("successful", timestamp, userId);

		Assert.assertEquals(
			initialCount,
			_layoutLocalService.getLayoutsCount(group.getGroupId(), false));
	}

	@Test
	public void testMasterPageTemplateThemeSettingsAfterLayoutPropagation()
		throws Exception {

		LayoutSet prototypePrivateLayoutSet =
			_layoutSetPrototypeGroup.getPrivateLayoutSet();

		prototypePrivateLayoutSet.setThemeId(_THEME_ID);

		LayoutSetLocalServiceUtil.updateLayoutSet(prototypePrivateLayoutSet);

		LayoutSet prototypePublicLayoutSet =
			_layoutSetPrototypeGroup.getPublicLayoutSet();

		prototypePublicLayoutSet.setThemeId(_THEME_ID);

		LayoutSetLocalServiceUtil.updateLayoutSet(prototypePublicLayoutSet);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutSetPrototypeGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Layout siteTemplateLayoutFromMasterLayout =
			LayoutTestUtil.addTypeContentLayout(
				_layoutSetPrototypeGroup, true, false,
				masterLayoutPageTemplateEntry.getExternalReferenceCode());

		propagateChanges(group);

		Layout masterLayout = LayoutLocalServiceUtil.getLayout(
			masterLayoutPageTemplateEntry.getPlid());

		Theme masterLayoutTheme = masterLayout.getTheme();

		Layout siteMasterLayout =
			LayoutLocalServiceUtil.getLayoutByExternalReferenceCode(
				masterLayout.getExternalReferenceCode(), group.getGroupId());

		Theme siteMasterLayoutTheme = siteMasterLayout.getTheme();

		Assert.assertEquals(
			siteMasterLayoutTheme.getThemeId(), masterLayoutTheme.getThemeId());
		Assert.assertEquals(siteMasterLayoutTheme.getThemeId(), _THEME_ID);

		Layout siteLayoutFromMasterLayout =
			LayoutLocalServiceUtil.getLayoutByExternalReferenceCode(
				siteTemplateLayoutFromMasterLayout.getExternalReferenceCode(),
				group.getGroupId());

		Theme siteLayoutFromMasterLayoutTheme =
			siteLayoutFromMasterLayout.getTheme();

		Theme siteTemplateLayoutFromMasterLayoutTheme =
			siteTemplateLayoutFromMasterLayout.getTheme();

		Assert.assertEquals(
			siteLayoutFromMasterLayoutTheme.getThemeId(),
			siteTemplateLayoutFromMasterLayoutTheme.getThemeId());

		Assert.assertEquals(
			siteLayoutFromMasterLayoutTheme.getThemeId(), _THEME_ID);
	}

	@Test
	public void testPortletDataPropagationWithLinkDisabled() throws Exception {
		doTestPortletDataPropagation(false);
	}

	@Test
	public void testPortletDataPropagationWithLinkEnabled() throws Exception {
		doTestPortletDataPropagation(true);
	}

	@Test
	public void testPortletPreferencesPropagationWithGlobalScopeLinkDisabled()
		throws Exception {

		doTestPortletPreferencesPropagation(false, true);
	}

	@Test
	public void testPortletPreferencesPropagationWithGlobalScopeLinkEnabled()
		throws Exception {

		doTestPortletPreferencesPropagation(true, true);
	}

	@Ignore
	@Test
	public void testPortletPreferencesPropagationWithPreferencesUniquePerLayoutEnabled()
		throws Exception {

		// TODO LPD-92847 Shared portlet preferences are not exported through
		// the new Batch + Export/Import framework path since LPD-57377

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			TestPropsValues.getCompanyId(),
			JournalContentPortletKeys.JOURNAL_CONTENT);

		boolean preferencesUniquePerLayout =
			portlet.getPreferencesUniquePerLayout();

		portlet.setPreferencesUniquePerLayout(false);

		_layoutSetPrototypeLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true, globalGroupId, layoutPrototype,
			true);

		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"bulletStyle", new String[] {"Dots"}
		).build();

		String testPortletId1 = LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layoutSetPrototypeLayout,
			JournalContentPortletKeys.JOURNAL_CONTENT, "column-1",
			preferenceMap);

		preferenceMap.put("bulletStyle", new String[] {"Arrows"});

		String testPortletId2 = LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layoutSetPrototypeLayout,
			JournalContentPortletKeys.JOURNAL_CONTENT, "column-2",
			preferenceMap);

		propagateChanges(group);

		Layout layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false,
			_layoutSetPrototypeLayout.getFriendlyURL());

		PortletPreferences testPortletIdPortletPreferences =
			PortletPreferencesFactoryUtil.getPortletSetup(
				group.getGroupId(), layout,
				JournalContentPortletKeys.JOURNAL_CONTENT, null);

		Assert.assertEquals(
			"Arrows",
			testPortletIdPortletPreferences.getValue(
				"bulletStyle", StringPool.BLANK));

		PortletPreferences testPortletId1PortletPreferences =
			PortletPreferencesFactoryUtil.getPortletSetup(
				layout, testPortletId1, null);

		Assert.assertEquals(
			"Arrows",
			testPortletId1PortletPreferences.getValue(
				"bulletStyle", StringPool.BLANK));

		PortletPreferences testPortletId2PortletPreferences =
			PortletPreferencesFactoryUtil.getPortletSetup(
				layout, testPortletId2, null);

		Assert.assertEquals(
			"Arrows",
			testPortletId2PortletPreferences.getValue(
				"bulletStyle", StringPool.BLANK));

		portlet.setPreferencesUniquePerLayout(preferencesUniquePerLayout);
	}

	@FeatureFlag(enable = false, value = "LPD-38869")
	@Test
	public void testThemeSettingsAfterLayoutPropagation() throws Exception {
		LayoutSet prototypePrivateLayoutSet =
			_layoutSetPrototypeGroup.getPrivateLayoutSet();

		prototypePrivateLayoutSet.setThemeId(_THEME_ID);
		prototypePrivateLayoutSet.setColorSchemeId(_COLOR_SCHEME_ID);

		prototypePrivateLayoutSet = LayoutSetLocalServiceUtil.updateLayoutSet(
			prototypePrivateLayoutSet);

		LayoutSet prototypePublicLayoutSet =
			_layoutSetPrototypeGroup.getPublicLayoutSet();

		prototypePublicLayoutSet.setThemeId(_THEME_ID);

		prototypePrivateLayoutSet.setColorSchemeId(_COLOR_SCHEME_ID);

		LayoutSetLocalServiceUtil.updateLayoutSet(prototypePublicLayoutSet);

		propagateChanges(group);

		LayoutSet propagatedLayoutSet = group.getPrivateLayoutSet();

		Assert.assertEquals(
			prototypePrivateLayoutSet.getThemeId(),
			propagatedLayoutSet.getThemeId());
		Assert.assertEquals(
			prototypePrivateLayoutSet.getColorSchemeId(),
			propagatedLayoutSet.getColorSchemeId());
	}

	@FeatureFlag("LPD-38869")
	@Test
	public void testThemeSettingsAfterLayoutPropagationWithPrivateLinkEnabled()
		throws Exception {

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Group layoutSetPrototypeGroup = layoutSetPrototype.getGroup();

		LayoutSet prototypePrivateLayoutSet =
			layoutSetPrototypeGroup.getPrivateLayoutSet();

		prototypePrivateLayoutSet.setThemeId(_THEME_ID);

		prototypePrivateLayoutSet = LayoutSetLocalServiceUtil.updateLayoutSet(
			prototypePrivateLayoutSet);

		Group testGroup = GroupTestUtil.addGroup();

		LayoutSet privateLayoutSet = LayoutSetLocalServiceUtil.fetchLayoutSet(
			testGroup.getGroupId(), true);

		privateLayoutSet.setLayoutSetPrototypeLinkEnabled(true);

		LayoutSetLocalServiceUtil.updateLayoutSet(privateLayoutSet);

		setLinkEnabled(
			testGroup, 0, layoutSetPrototype.getLayoutSetPrototypeId(), false,
			true);

		_sites.mergeLayoutSetPrototypeLayouts(testGroup.getPrivateLayoutSet());

		LayoutSet publicLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			testGroup.getGroupId(), false);

		Assert.assertNotEquals(
			prototypePrivateLayoutSet.getThemeId(),
			publicLayoutSet.getThemeId());

		privateLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			testGroup.getGroupId(), true);

		Assert.assertEquals(
			prototypePrivateLayoutSet.getThemeId(),
			privateLayoutSet.getThemeId());

		GroupTestUtil.deleteGroup(testGroup);

		GroupTestUtil.deleteGroup(layoutSetPrototypeGroup);
	}

	@FeatureFlag("LPD-38869")
	@Test
	public void testThemeSettingsAfterLayoutPropagationWithPublicLinkEnabled()
		throws Exception {

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Group layoutSetPrototypeGroup = layoutSetPrototype.getGroup();

		LayoutSet prototypePrivateLayoutSet =
			layoutSetPrototypeGroup.getPrivateLayoutSet();

		prototypePrivateLayoutSet.setThemeId(_THEME_ID);

		prototypePrivateLayoutSet = LayoutSetLocalServiceUtil.updateLayoutSet(
			prototypePrivateLayoutSet);

		Group testGroup = GroupTestUtil.addGroup();

		LayoutSet publicLayoutSet = LayoutSetLocalServiceUtil.fetchLayoutSet(
			testGroup.getGroupId(), false);

		publicLayoutSet.setLayoutSetPrototypeLinkEnabled(true);

		LayoutSetLocalServiceUtil.updateLayoutSet(publicLayoutSet);

		setLinkEnabled(
			testGroup, layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			false);

		_sites.mergeLayoutSetPrototypeLayouts(testGroup.getPublicLayoutSet());

		publicLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			testGroup.getGroupId(), false);

		Assert.assertEquals(
			prototypePrivateLayoutSet.getThemeId(),
			publicLayoutSet.getThemeId());

		LayoutSet privateLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			testGroup.getGroupId(), true);

		Assert.assertNotEquals(
			prototypePrivateLayoutSet.getThemeId(),
			privateLayoutSet.getThemeId());

		GroupTestUtil.deleteGroup(testGroup);

		GroupTestUtil.deleteGroup(layoutSetPrototypeGroup);
	}

	@Test
	public void testThemeSettingsWithLinkEnabled() throws Exception {
		LayoutSet prototypeLayoutSet =
			_layoutSetPrototypeGroup.getPrivateLayoutSet();

		Theme prototypeTheme = prototypeLayoutSet.getTheme();

		prototypeTheme.addSetting("test", "true", true, null, null, null);

		Map<String, ThemeSetting> prototypeThemeSettings =
			prototypeTheme.getConfigurableSettings();

		UnicodeProperties settingsUnicodeProperties =
			prototypeLayoutSet.getSettingsProperties();

		String device = "regular";

		for (String propertyKey : prototypeThemeSettings.keySet()) {
			settingsUnicodeProperties.setProperty(
				ThemeSettingImpl.namespaceProperty(device, propertyKey),
				RandomTestUtil.randomString());
		}

		prototypeLayoutSet.setSettingsProperties(settingsUnicodeProperties);

		prototypeLayoutSet = LayoutSetLocalServiceUtil.updateLayoutSet(
			prototypeLayoutSet);

		setLinkEnabled(true);

		propagateChanges(group);

		layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false, prototypeLayout.getFriendlyURL());

		_layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false, _prototypeLayout.getFriendlyURL());

		LayoutSet targetLayoutSet = layout.getLayoutSet();

		for (String propertyKey : prototypeThemeSettings.keySet()) {
			String prototypeValue = prototypeLayoutSet.getThemeSetting(
				propertyKey, device);
			String targetValue = targetLayoutSet.getThemeSetting(
				propertyKey, device);

			Assert.assertEquals(
				propertyKey + "=" + prototypeValue,
				propertyKey + "=" + targetValue);
		}
	}

	@Override
	protected void doSetUp() throws Exception {

		// Layout set prototype

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		_layoutSetPrototypeGroup = _layoutSetPrototype.getGroup();

		prototypeLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		LayoutTestUtil.updateLayoutTemplateId(
			prototypeLayout, initialLayoutTemplateId);

		_layoutSetPrototypeJournalArticle = JournalTestUtil.addArticle(
			_layoutSetPrototypeGroup.getGroupId(), "Test Article",
			"Test Content");

		portletId = addPortletToLayout(
			TestPropsValues.getUserId(), prototypeLayout,
			_layoutSetPrototypeJournalArticle, "column-1");

		_prototypeLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		LayoutTestUtil.updateLayoutTemplateId(
			_prototypeLayout, initialLayoutTemplateId);

		_portletId = addPortletToLayout(
			TestPropsValues.getUserId(), _prototypeLayout,
			_layoutSetPrototypeJournalArticle, "column-1");

		_initialPrototypeLayoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
			_layoutSetPrototypeGroup, true);

		// Group

		setLinkEnabled(true);

		layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false, prototypeLayout.getFriendlyURL());

		_layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false, _prototypeLayout.getFriendlyURL());

		_initialLayoutCount = getGroupLayoutCount();

		journalArticle = JournalArticleLocalServiceUtil.getArticleByUrlTitle(
			group.getGroupId(),
			_layoutSetPrototypeJournalArticle.getUrlTitle());

		// Users

		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();
	}

	protected void doTestIsLayoutUpdateable() throws Exception {
		Assert.assertTrue(layout.isLayoutUpdateable());
		Assert.assertTrue(_layout.isLayoutUpdateable());

		prototypeLayout = LayoutLocalServiceUtil.getLayout(
			prototypeLayout.getPlid());

		setLayoutUpdateable(prototypeLayout, false);

		Assert.assertFalse(layout.isLayoutUpdateable());
		Assert.assertTrue(_layout.isLayoutUpdateable());

		setLayoutsUpdateable(false);

		Assert.assertFalse(layout.isLayoutUpdateable());
		Assert.assertFalse(_layout.isLayoutUpdateable());

		setLinkEnabled(false);

		Assert.assertTrue(layout.isLayoutUpdateable());
		Assert.assertTrue(_layout.isLayoutUpdateable());
	}

	protected void doTestLayoutPropagation(boolean linkEnabled)
		throws Exception {

		setLinkEnabled(linkEnabled);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), true);

		childLayout = _layoutLocalService.updateParentLayoutId(
			childLayout.getPlid(), layout.getPlid());

		Assert.assertEquals(
			_initialPrototypeLayoutsCount, getGroupLayoutCount());

		propagateChanges(group);

		if (linkEnabled) {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount + 2, getGroupLayoutCount());

			Layout propagatedLayout =
				LayoutLocalServiceUtil.getLayoutByExternalReferenceCode(
					layout.getExternalReferenceCode(), group.getGroupId());

			Layout propagatedChildLayout =
				LayoutLocalServiceUtil.getLayoutByExternalReferenceCode(
					childLayout.getExternalReferenceCode(), group.getGroupId());

			Assert.assertEquals(
				propagatedLayout.getLayoutId(),
				propagatedChildLayout.getParentLayoutId());
		}
		else {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount, getGroupLayoutCount());
		}

		LayoutLocalServiceUtil.deleteLayout(
			layout, ServiceContextTestUtil.getServiceContext());

		if (linkEnabled) {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount + 2, getGroupLayoutCount());
		}
		else {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount, getGroupLayoutCount());
		}

		propagateChanges(group);

		if (linkEnabled) {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount + 2, getGroupLayoutCount());
		}
		else {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount, getGroupLayoutCount());
		}
	}

	protected void doTestLayoutPropagationWithLayoutPrototype(
			boolean layoutSetLayoutLinkEnabled)
		throws Exception {

		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		_layoutSetPrototypeLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true, globalGroupId, layoutPrototype,
			layoutSetLayoutLinkEnabled);

		propagateChanges(group);

		Layout layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false,
			_layoutSetPrototypeLayout.getFriendlyURL());

		LayoutTestUtil.updateLayoutTemplateId(
			layoutPrototypeLayout, "1_column");

		layout = LayoutLocalServiceUtil.getLayout(layout.getPlid());

		Assert.assertEquals(
			initialLayoutTemplateId,
			LayoutTestUtil.getLayoutTemplateId(layout));

		// The Page Template does not propagate on Site-Template-linked pages

		_sites.mergeLayoutPrototypeLayout(layout);

		layout = LayoutLocalServiceUtil.getLayout(layout.getPlid());

		Assert.assertEquals(
			initialLayoutTemplateId,
			LayoutTestUtil.getLayoutTemplateId(layout));

		layout = propagateChanges(layout);

		if (layoutSetLayoutLinkEnabled) {
			Assert.assertEquals(
				"1_column", LayoutTestUtil.getLayoutTemplateId(layout));
		}
		else {
			Assert.assertEquals(
				initialLayoutTemplateId,
				LayoutTestUtil.getLayoutTemplateId(layout));
		}
	}

	protected void doTestPortletDataPropagation(boolean linkEnabled)
		throws Exception {

		setLinkEnabled(linkEnabled);

		Map<String, String> content = new HashMap<>();

		for (String languageId : journalArticle.getAvailableLanguageIds()) {
			String localization = _journalContent.getContent(
				_layoutSetPrototypeJournalArticle.getGroupId(),
				_layoutSetPrototypeJournalArticle.getArticleId(),
				Constants.VIEW, languageId);

			String importedLocalization = _journalContent.getContent(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				Constants.VIEW, languageId);

			Assert.assertEquals(localization, importedLocalization);

			content.put(languageId, localization);
		}

		String newContent = DDMStructureTestUtil.getSampleStructuredContent(
			"New Test Content");

		JournalTestUtil.updateArticle(
			_layoutSetPrototypeJournalArticle, "New Test Title", newContent);

		propagateChanges(group);

		for (String languageId : journalArticle.getAvailableLanguageIds()) {
			String localization = content.get(languageId);

			String importedLocalization = _journalContent.getContent(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				Constants.VIEW, languageId);

			if (linkEnabled) {
				Assert.assertEquals("New Test Content", importedLocalization);
			}
			else {
				Assert.assertEquals(localization, importedLocalization);
			}
		}
	}

	@Override
	protected void doTestPortletPreferencesPropagation(boolean linkEnabled)
		throws Exception {

		doTestPortletPreferencesPropagation(linkEnabled, false);
	}

	protected int getGroupLayoutCount() throws Exception {
		return LayoutLocalServiceUtil.getLayoutsCount(group, false);
	}

	protected void propagateChanges(boolean initial, Group group)
		throws Exception {

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			group.getGroupId(), false);

		propagateChanges(
			initial,
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId()));
	}

	protected void propagateChanges(
			boolean initial, LayoutSetPrototype layoutSetPrototype)
		throws Exception {

		if (initial) {
			for (LayoutSet layoutSet :
					LayoutSetLocalServiceUtil.
						getLayoutSetsByLayoutSetPrototypeUuid(
							layoutSetPrototype.getUuid())) {

				_sites.mergeLayoutSetPrototypeLayouts(layoutSet);
			}
		}
		else {
			_sites.mergeLayoutSetPrototypeLayouts(
				layoutSetPrototype, TestPropsValues.getUserId());
		}
	}

	protected void propagateChanges(Group group) throws Exception {
		propagateChanges(RandomTestUtil.randomBoolean(), group);
	}

	@Override
	protected Layout propagateChanges(Layout layout) throws Exception {
		Layout layoutSetPrototypeLayout =
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				layout.getLayoutSetPrototypeLayoutERC(),
				_layoutSetPrototypeGroup.getGroupId());

		if (layoutSetPrototypeLayout != null) {
			super.propagateChanges(layoutSetPrototypeLayout);
		}

		propagateChanges(group);

		return LayoutLocalServiceUtil.getLayout(layout.getPlid());
	}

	@Override
	protected boolean propagatesLocalEntityReferences() {
		return true;
	}

	protected void setLayoutsUpdateable(boolean layoutsUpdateable)
		throws Exception {

		_layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.updateLayoutSetPrototype(
				_layoutSetPrototype.getLayoutSetPrototypeId(),
				_layoutSetPrototype.getNameMap(),
				_layoutSetPrototype.getDescriptionMap(),
				_layoutSetPrototype.isActive(), layoutsUpdateable,
				ServiceContextTestUtil.getServiceContext());
	}

	protected Layout setLayoutUpdateable(
			Layout layout, boolean layoutUpdateable)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			Sites.LAYOUT_UPDATEABLE, String.valueOf(layoutUpdateable));

		layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		return LayoutLocalServiceUtil.updateLayout(layout);
	}

	@Override
	protected void setLinkEnabled(boolean linkEnabled) throws Exception {
		if ((layout != null) && (_layout != null)) {
			layout = LayoutLocalServiceUtil.getLayout(layout.getPlid());

			layout.setPortletLayoutPageTemplateEntryLinkEnabled(linkEnabled);

			LayoutLocalServiceUtil.updateLayout(layout);

			_layout = LayoutLocalServiceUtil.getLayout(_layout.getPlid());

			_layout.setPortletLayoutPageTemplateEntryLinkEnabled(linkEnabled);

			LayoutLocalServiceUtil.updateLayout(_layout);
		}

		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		_sites.updateLayoutSetPrototypesLinks(
			group, _layoutSetPrototype.getLayoutSetPrototypeId(), 0,
			linkEnabled, linkEnabled);
	}

	protected void setLinkEnabled(
			Group group, long publicLayoutSetPrototypeId,
			long privateLayoutSetPrototypeId, boolean publicLinkEnabled,
			boolean privateLinkEnabled)
		throws Exception {

		_sites.updateLayoutSetPrototypesLinks(
			group, publicLayoutSetPrototypeId, privateLayoutSetPrototypeId,
			publicLinkEnabled, privateLinkEnabled);
	}

	protected void testAddChildLayout(boolean layoutSetPrototypeLinkEnabled)
		throws Exception {

		setLinkEnabled(layoutSetPrototypeLinkEnabled);

		try {
			LayoutTestUtil.addTypePortletLayout(group, layout.getPlid());

			Assert.assertFalse(
				"Able to add a child page to a page associated to a site " +
					"template with link enabled",
				layoutSetPrototypeLinkEnabled);
		}
		catch (LayoutParentLayoutIdException layoutParentLayoutIdException) {
			if (_log.isDebugEnabled()) {
				_log.debug(layoutParentLayoutIdException);
			}

			Assert.assertTrue(
				"Unable to add a child page to a page associated to a " +
					"template with link disabled",
				layoutSetPrototypeLinkEnabled);
		}
	}

	@Override
	protected boolean useColumnCustomizable() {
		return false;
	}

	private Layout _addLayout(long groupId) throws Exception {
		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), groupId, true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, null,
			LayoutConstants.TYPE_CONTENT, false, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext());

		Layout draftLayout = layout.fetchDraftLayout();

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			LayoutTypeSettingsConstants.KEY_PUBLISHED, Boolean.TRUE.toString());

		draftLayout.setTypeSettingsProperties(unicodeProperties);

		_layoutLocalService.updateLayout(draftLayout);

		return layout;
	}

	private String _addPortletToLayout(Layout layout, String portletId)
		throws Exception {

		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(layout, portletId);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private void _assertLayoutSetPrototypeLayoutERC(
			Layout prototypeLayout, long groupId)
		throws Exception {

		Layout propagatedLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				prototypeLayout.getUuid(), groupId, false);

		Assert.assertNotNull(propagatedLayout);
		Assert.assertEquals(
			prototypeLayout.getExternalReferenceCode(),
			propagatedLayout.getLayoutSetPrototypeLayoutERC());
	}

	private void _assertNotification(
			String expectedResult, long timestamp, long userId)
		throws Exception {

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 5, TimeUnit.SECONDS,
			() -> {
				String result = _getLatestMergeNotificationResult(
					timestamp, userId);

				Assert.assertEquals(expectedResult, result);
			});
	}

	private String _getLatestMergeNotificationResult(
			long timestamp, long userId)
		throws Exception {

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				userId);

		for (int i = userNotificationEvents.size() - 1; i >= 0; i--) {
			UserNotificationEvent userNotificationEvent =
				userNotificationEvents.get(i);

			if ((userNotificationEvent.getTimestamp() < timestamp) ||
				!Objects.equals(
					userNotificationEvent.getType(),
					LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE)) {

				continue;
			}

			JSONObject payloadJSONObject = JSONFactoryUtil.createJSONObject(
				userNotificationEvent.getPayload());

			return payloadJSONObject.getString("result");
		}

		return null;
	}

	private int _getRandomStatus(int... statuses) {
		return statuses[RandomTestUtil.randomInt(0, statuses.length - 1)];
	}

	private SafeCloseable _registerMessageListenerWithSafeCloseable(
		MessageListener messageListener) {

		return _registerServiceWithSafeCloseable(
			MessageListener.class,
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name", DestinationNames.BACKGROUND_TASK_STATUS
			).put(
				"service.ranking", Integer.MAX_VALUE
			).build(),
			messageListener);
	}

	private <S> SafeCloseable _registerServiceWithSafeCloseable(
		Class<S> clazz, Dictionary<String, ?> properties, S service) {

		Bundle bundle = FrameworkUtil.getBundle(
			LayoutSetPrototypePropagationTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<S> serviceRegistration =
			bundleContext.registerService(clazz, service, properties);

		return serviceRegistration::unregister;
	}

	private void _registerTestPortlet(String portletName) {
		Bundle bundle = FrameworkUtil.getBundle(
			LayoutSetPrototypePropagationTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		bundleContext.registerService(
			jakarta.portlet.Portlet.class, new MVCPortlet(),
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.instanceable", "true"
			).put(
				"com.liferay.portlet.preferences-owned-by-group", "true"
			).put(
				"jakarta.portlet.init-param.view-template", "/view.jsp"
			).put(
				"jakarta.portlet.name", portletName
			).build());
	}

	private void _testLayoutPermissionPropagation(boolean initial)
		throws Exception {

		setLinkEnabled(true);

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.POWER_USER);

		ResourcePermissionServiceUtil.setIndividualResourcePermissions(
			prototypeLayout.getGroupId(), prototypeLayout.getCompanyId(),
			Layout.class.getName(),
			String.valueOf(prototypeLayout.getPrimaryKey()), role.getRoleId(),
			new String[] {ActionKeys.CUSTOMIZE});

		prototypeLayout = updateModifiedDate(
			prototypeLayout,
			new Date(System.currentTimeMillis() + Time.MINUTE));

		propagateChanges(initial, group);

		Assert.assertEquals(
			initial,
			ResourcePermissionLocalServiceUtil.hasResourcePermission(
				layout.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPrimaryKey()), role.getRoleId(),
				ActionKeys.CUSTOMIZE));
	}

	private void _testLayoutSetPrototypePropagation(boolean deleteCacheFile)
		throws Exception {

		TestMessageListener testMessageListener = new TestMessageListener();

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_SET_PROTOTYPE_MERGE_DELETE_CACHE_FILE_ENABLED",
					deleteCacheFile);
			SafeCloseable safeCloseable2 =
				_registerMessageListenerWithSafeCloseable(
					testMessageListener)) {

			LayoutTestUtil.addTypePortletLayout(_layoutSetPrototypeGroup, true);

			long timestamp = System.currentTimeMillis();

			propagateChanges(false, group);

			Assert.assertTrue(testMessageListener._fileExists);

			_assertNotification(
				"successful", timestamp, TestPropsValues.getUserId());

			Assert.assertEquals(
				!deleteCacheFile, FileUtil.exists(testMessageListener._path));
		}
		finally {
			FileUtil.delete(testMessageListener._path);
		}
	}

	private void _testLayoutSetPrototypePropagationCheckNotification(
			String expectedResult, int status1, int status2)
		throws Exception {

		long userId = TestPropsValues.getUserId();

		Group testGroup = GroupTestUtil.addGroup();

		_sites.updateLayoutSetPrototypesLinks(
			testGroup, _layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			false);

		try (SafeCloseable safeCloseable = _registerServiceWithSafeCloseable(
				BackgroundTaskExecutor.class,
				HashMapDictionaryBuilder.<String, Object>put(
					"background.task.executor.class.name",
					BackgroundTaskExecutorNames.
						LAYOUT_SET_PROTOTYPE_MERGE_BACKGROUND_TASK_EXECUTOR
				).put(
					"service.ranking", 1000
				).build(),
				new TestBackgroundTaskExecutor(
					HashMapBuilder.put(
						group.getGroupId(), status1
					).put(
						testGroup.getGroupId(), status2
					).build()))) {

			long timestamp = System.currentTimeMillis();

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.portal.background.task.internal.messaging." +
						"BackgroundTaskMessageListener",
					LoggerTestUtil.OFF)) {

				_sites.mergeLayoutSetPrototypeLayouts(
					_layoutSetPrototype, userId);

				_assertNotification(expectedResult, timestamp, userId);
			}
		}

		GroupLocalServiceUtil.deleteGroup(testGroup);
	}

	private void _testLayoutSetPrototypePropagationWithExportImportInProcess(
			Consumer<Boolean> exportImportThreadLocalConsumer)
		throws Exception {

		try {
			exportImportThreadLocalConsumer.accept(true);

			propagateChanges(group);

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}
		finally {
			exportImportThreadLocalConsumer.accept(false);
		}
	}

	private void _verifyPortletPreferenceValue(
		Layout layout, String portletId, String key, String expectedValue) {

		PortletPreferencesIds portletPreferencesIds =
			_portletPreferencesFactory.getPortletPreferencesIds(
				layout.getCompanyId(), layout.getGroupId(), 0, layout.getPlid(),
				portletId);

		com.liferay.portal.kernel.model.PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferencesIds.getOwnerId(),
				portletPreferencesIds.getOwnerType(), layout.getPlid(),
				portletPreferencesIds.getPortletId());

		PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				portletPreferences);

		Assert.assertEquals(
			expectedValue, jxPortletPreferences.getValue(key, null));
	}

	private static final String _COLOR_SCHEME_ID =
		RandomTestUtil.randomString();

	private static final String _TEMP_DIR =
		SystemProperties.get(SystemProperties.TMP_DIR) +
			"/liferay/layout_set_prototype/";

	private static final String _THEME_ID = "minium_WAR_miniumtheme";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPrototypePropagationTest.class);

	private static BundleContext _bundleContext;

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private int _initialLayoutCount;
	private int _initialPrototypeLayoutsCount;

	@Inject
	private JournalContent _journalContent;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private LayoutSetPrototype _layoutSetPrototype;

	private Group _layoutSetPrototypeGroup;
	private JournalArticle _layoutSetPrototypeJournalArticle;

	@DeleteAfterTestRun
	private Layout _layoutSetPrototypeLayout;

	private String _portletId;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	private Layout _prototypeLayout;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private Sites _sites;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	private static class TestBackgroundTaskExecutor
		extends BaseBackgroundTaskExecutor {

		public TestBackgroundTaskExecutor(Map<Long, Integer> groupIdStatusMap) {
			_groupIdStatusMap = groupIdStatusMap;
		}

		@Override
		public BackgroundTaskExecutor clone() {
			return this;
		}

		@Override
		public BackgroundTaskResult execute(BackgroundTask backgroundTask) {
			Integer status = _groupIdStatusMap.getOrDefault(
				backgroundTask.getGroupId(),
				BackgroundTaskConstants.STATUS_SUCCESSFUL);

			if (status == BackgroundTaskConstants.STATUS_FAILED) {
				throw new RuntimeException();
			}

			return new BackgroundTaskResult(status);
		}

		@Override
		public BackgroundTaskDisplay getBackgroundTaskDisplay(
			BackgroundTask backgroundTask) {

			return null;
		}

		private final Map<Long, Integer> _groupIdStatusMap;

	}

	private class TestMessageListener extends BaseMessageListener {

		@Override
		protected void doReceive(Message message) throws PortalException {
			if (!Objects.equals(
					message.getString("taskExecutorClassName"),
					BackgroundTaskExecutorNames.
						LAYOUT_SET_PROTOTYPE_MERGE_BACKGROUND_TASK_EXECUTOR)) {

				return;
			}

			int backgroundTaskStatus = message.getInteger("status");

			if ((backgroundTaskStatus !=
					BackgroundTaskConstants.STATUS_CANCELLED) &&
				(backgroundTaskStatus !=
					BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS) &&
				(backgroundTaskStatus !=
					BackgroundTaskConstants.STATUS_FAILED) &&
				(backgroundTaskStatus !=
					BackgroundTaskConstants.STATUS_SUCCESSFUL)) {

				return;
			}

			com.liferay.portal.background.task.model.BackgroundTask
				backgroundTask = _backgroundTaskLocalService.getBackgroundTask(
					message.getLong("backgroundTaskId"));

			String sessionId = MapUtil.getString(
				backgroundTask.getTaskContextMap(),
				LayoutSetPrototypeBackgroundTaskConstants.SESSION_ID);

			_path = _TEMP_DIR + sessionId + ".lar";

			_fileExists = FileUtil.exists(_path);
		}

		private boolean _fileExists;
		private String _path;

	}

}