/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalContent;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.LayoutParentLayoutIdException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.ThemeSettingImpl;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.PortletPreferences;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Julio Camarero
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class LayoutSetPrototypePropagationTest
	extends BasePrototypePropagationTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

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
	public void testLayoutDeleteAndReadWithSameFriendlyURL() throws Exception {
		setLinkEnabled(true);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), "test", true);

		String friendlyURL = layout.getFriendlyURL();

		Assert.assertEquals(
			_initialPrototypeLayoutsCount, getGroupLayoutCount());

		propagateChanges(group);

		Assert.assertEquals(
			_initialPrototypeLayoutsCount + 1, getGroupLayoutCount());

		LayoutLocalServiceUtil.deleteLayout(
			layout, ServiceContextTestUtil.getServiceContext());

		Layout newLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), "test", true);

		Assert.assertEquals(friendlyURL, newLayout.getFriendlyURL());

		Assert.assertEquals(
			_initialPrototypeLayoutsCount + 1, getGroupLayoutCount());

		propagateChanges(group);

		Assert.assertEquals(
			_initialPrototypeLayoutsCount + 1, getGroupLayoutCount());

		Layout propagatedLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				newLayout.getUuid(), group.getGroupId(), false);

		Assert.assertNotNull(
			"Deleted and readded layout could not be found on propagated site",
			propagatedLayout);

		Assert.assertEquals(
			"Friendly URLs of the source and target layouts should match",
			friendlyURL, propagatedLayout.getFriendlyURL());
	}

	@Test
	public void testLayoutPermissionPropagationWithLinkEnabled()
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

		propagateChanges(group);

		Assert.assertTrue(
			ResourcePermissionLocalServiceUtil.hasResourcePermission(
				layout.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPrimaryKey()), role.getRoleId(),
				ActionKeys.CUSTOMIZE));
	}

	@Test
	public void testLayoutPropagationWhenLoadingLayoutsTreeWithLinkEnabled()
		throws Exception {

		setLinkEnabled(true);

		LayoutTestUtil.addTypePortletLayout(_layoutSetPrototypeGroup, true);

		Assert.assertEquals(
			_initialPrototypeLayoutsCount, getGroupLayoutCount());

		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

		LayoutServiceUtil.getLayouts(
			group.getGroupId(), false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			false, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 5, TimeUnit.SECONDS,
			() -> Assert.assertEquals(
				_initialPrototypeLayoutsCount + 1, getGroupLayoutCount()));
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
			fragmentEntry.getExternalReferenceCode(),
			fragmentEntry.getScopeERC(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), draftLayout1,
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
	public void testLayoutPropagationWithFriendlyURLConflict()
		throws Exception {

		LayoutSet layoutSet = group.getPublicLayoutSet();

		List<Layout> initialMergeFailFriendlyURLLayouts =
			layoutSet.getMergeFailFriendlyURLLayouts();

		setLinkEnabled(true);

		LayoutTestUtil.addTypePortletLayout(group.getGroupId(), "test", false);
		LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), "test", true);

		propagateChanges(group);

		layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			layoutSet.getLayoutSetId());

		List<Layout> mergeFailFriendlyURLLayouts =
			layoutSet.getMergeFailFriendlyURLLayouts();

		Assert.assertEquals(
			mergeFailFriendlyURLLayouts.toString(),
			initialMergeFailFriendlyURLLayouts.size() + 1,
			mergeFailFriendlyURLLayouts.size());
	}

	@Test
	public void testLayoutPropagationWithFriendlyURLConflictResolvedByDelete()
		throws Exception {

		LayoutSet layoutSet = group.getPublicLayoutSet();

		List<Layout> initialMergeFailFriendlyURLLayouts =
			layoutSet.getMergeFailFriendlyURLLayouts();

		setLinkEnabled(true);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			group.getGroupId(), "test", false);

		LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), "test", true);

		propagateChanges(group);

		LayoutLocalServiceUtil.deleteLayout(layout);

		propagateChanges(group);

		layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			layoutSet.getLayoutSetId());

		List<Layout> mergeFailFriendlyURLLayouts =
			layoutSet.getMergeFailFriendlyURLLayouts();

		Assert.assertEquals(
			mergeFailFriendlyURLLayouts.toString(),
			initialMergeFailFriendlyURLLayouts.size(),
			mergeFailFriendlyURLLayouts.size());
	}

	@Test
	@TestInfo("LPD-31491")
	public void testLayoutPropagationWithFriendlyUrlConflictWithParentLayout()
		throws Exception {

		Layout prototypeLayout1 = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), true);

		Locale locale = _portal.getSiteDefaultLocale(_layoutSetPrototypeGroup);

		String languageId = LocaleUtil.toLanguageId(locale);

		prototypeLayout1 = _layoutLocalService.updateFriendlyURL(
			TestPropsValues.getUserId(), prototypeLayout1.getPlid(), "/page-a",
			languageId);

		_propagateChanges(0, 1);

		Assert.assertNotNull(
			_layoutLocalService.getLayoutByFriendlyURL(
				group.getGroupId(), false, "/page-a"));

		prototypeLayout1 = _layoutLocalService.updateFriendlyURL(
			TestPropsValues.getUserId(), prototypeLayout1.getPlid(), "/page-a0",
			languageId);

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.getFriendlyURLEntryLocalization(
				_layoutSetPrototypeGroup.getGroupId(),
				_portal.getClassNameId(
					ResourceActionsUtil.getCompositeModelName(
						Layout.class.getName(),
						String.valueOf(prototypeLayout1.isPrivateLayout()))),
				"/page-a");

		_friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
			friendlyURLEntryLocalization.getFriendlyURLEntryId(),
			friendlyURLEntryLocalization.getLanguageId());

		Layout prototypeLayout2 = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), true);

		prototypeLayout2 = _layoutLocalService.updateFriendlyURL(
			TestPropsValues.getUserId(), prototypeLayout2.getPlid(), "/page-a",
			languageId);

		prototypeLayout1 = _layoutLocalService.updateParentLayoutId(
			prototypeLayout1.getPlid(), prototypeLayout2.getPlid());

		_propagateChanges(1, 1);

		Layout layout1 = _layoutLocalService.getLayoutByFriendlyURL(
			group.getGroupId(), false, "/page-a0");

		Assert.assertEquals(0, layout1.getParentPlid());

		Assert.assertNull(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/page-a"));

		_layoutLocalService.updateFriendlyURL(
			TestPropsValues.getUserId(), prototypeLayout1.getPlid(),
			prototypeLayout1.getFriendlyURL(locale), languageId);
		_layoutLocalService.updateFriendlyURL(
			TestPropsValues.getUserId(), prototypeLayout2.getPlid(),
			prototypeLayout2.getFriendlyURL(locale), languageId);

		_sites.removeMergeFailFriendlyURLLayouts(group.getPublicLayoutSet());

		_propagateChanges(0, 2);

		Layout layout2 = _layoutLocalService.getLayoutByFriendlyURL(
			group.getGroupId(), false, "/page-a");

		layout1 = _layoutLocalService.getLayoutByFriendlyURL(
			group.getGroupId(), false, "/page-a0");

		Assert.assertEquals(layout2.getPlid(), layout1.getParentPlid());
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
	public void testLayoutPropagationWithLinkEnabled() throws Exception {
		doTestLayoutPropagation(true);
	}

	@Test
	@TestInfo("LPS-161955")
	public void testLayoutPropagationWithMasterLayout() throws Exception {
		Layout siteTemplateMasterLayout = LayoutTestUtil.addTypeContentLayout(
			_layoutSetPrototypeGroup, true, false);

		LayoutTestUtil.addTypeContentLayout(
			_layoutSetPrototypeGroup, true, false,
			siteTemplateMasterLayout.getPlid());

		propagateChanges(group);

		LayoutTestUtil.addTypeContentLayout(
			_layoutSetPrototypeGroup, true, false,
			siteTemplateMasterLayout.getPlid());

		propagateChanges(group);

		Assert.assertEquals(
			0,
			LayoutLocalServiceUtil.getMasterLayoutsCount(
				group.getGroupId(), siteTemplateMasterLayout.getPlid()));

		Layout siteMasterLayout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false,
			siteTemplateMasterLayout.getFriendlyURL());

		Assert.assertEquals(
			4,
			LayoutLocalServiceUtil.getMasterLayoutsCount(
				group.getGroupId(), siteMasterLayout.getPlid()));
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

		_layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.fetchLayoutSetPrototype(
				_layoutSetPrototype.getLayoutSetPrototypeId());

		_layoutSetPrototype.setModifiedDate(new Date());

		_layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.updateLayoutSetPrototype(
				_layoutSetPrototype);

		Layout siteTemplateMasterLayout = LayoutTestUtil.addTypeContentLayout(
			_layoutSetPrototypeGroup, true, false);

		Layout siteTemplateLayoutFromMasterLayout =
			LayoutTestUtil.addTypeContentLayout(
				_layoutSetPrototypeGroup, true, false,
				siteTemplateMasterLayout.getPlid());

		propagateChanges(group);

		Layout siteMasterLayout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false,
			siteTemplateMasterLayout.getFriendlyURL());

		Assert.assertEquals(
			siteMasterLayout.getTheme(
			).getThemeId(),
			siteTemplateMasterLayout.getTheme(
			).getThemeId());
		Assert.assertEquals(
			siteMasterLayout.getTheme(
			).getThemeId(),
			_THEME_ID);

		Layout siteLayoutFromMasterLayout =
			LayoutLocalServiceUtil.getFriendlyURLLayout(
				group.getGroupId(), false,
				siteTemplateLayoutFromMasterLayout.getFriendlyURL());

		Assert.assertEquals(
			siteLayoutFromMasterLayout.getTheme(
			).getThemeId(),
			siteTemplateLayoutFromMasterLayout.getTheme(
			).getThemeId());
		Assert.assertEquals(
			siteLayoutFromMasterLayout.getTheme(
			).getThemeId(),
			_THEME_ID);
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

	@Test
	public void testPortletPreferencesPropagationWithPreferencesUniquePerLayoutEnabled()
		throws Exception {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			TestPropsValues.getCompanyId(),
			JournalContentPortletKeys.JOURNAL_CONTENT);

		boolean preferencesUniquePerLayout =
			portlet.getPreferencesUniquePerLayout();

		try {
			portlet.setPreferencesUniquePerLayout(false);

			_layoutSetPrototypeLayout = LayoutTestUtil.addTypePortletLayout(
				_layoutSetPrototypeGroup, true, layoutPrototype, true);

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
		}
		finally {
			portlet.setPreferencesUniquePerLayout(preferencesUniquePerLayout);
		}
	}

	@Test
	public void testResetLayoutTemplate() throws Exception {
		_layoutSetPrototypeHelper.resetPrototype(layout);
		_layoutSetPrototypeHelper.resetPrototype(_layout);

		propagateChanges(group);

		setLinkEnabled(true);

		layout = LayoutTestUtil.updateLayoutTemplateId(layout, "1_column");

		Assert.assertTrue(_sites.isLayoutModifiedSinceLastMerge(layout));

		Assert.assertFalse(_sites.isLayoutModifiedSinceLastMerge(_layout));

		_layout = LayoutTestUtil.updateLayoutTemplateId(_layout, "1_column");

		layout = LayoutLocalServiceUtil.getLayout(layout.getPlid());

		_layoutSetPrototypeHelper.resetPrototype(layout);

		layout = propagateChanges(layout);

		Assert.assertFalse(_sites.isLayoutModifiedSinceLastMerge(layout));
		Assert.assertEquals(
			initialLayoutTemplateId,
			LayoutTestUtil.getLayoutTemplateId(layout));

		_layout = propagateChanges(_layout);

		Assert.assertTrue(_sites.isLayoutModifiedSinceLastMerge(_layout));
		Assert.assertEquals(
			"1_column", LayoutTestUtil.getLayoutTemplateId(_layout));
	}

	@Test
	public void testResetPortletPreferences() throws Exception {
		LayoutTestUtil.updateLayoutPortletPreference(
			prototypeLayout, portletId, "showAvailableLocales",
			Boolean.FALSE.toString());

		_layoutSetPrototypeHelper.resetPrototype(layout);
		_layoutSetPrototypeHelper.resetPrototype(_layout);

		propagateChanges(group);

		setLinkEnabled(true);

		layout = LayoutTestUtil.updateLayoutPortletPreference(
			layout, portletId, "showAvailableLocales", Boolean.TRUE.toString());

		Assert.assertTrue(_sites.isLayoutModifiedSinceLastMerge(layout));

		Assert.assertFalse(_sites.isLayoutModifiedSinceLastMerge(_layout));

		_layout = LayoutTestUtil.updateLayoutPortletPreference(
			_layout, _portletId, "showAvailableLocales",
			Boolean.TRUE.toString());

		layout = LayoutLocalServiceUtil.getLayout(layout.getPlid());

		_layoutSetPrototypeHelper.resetPrototype(layout);

		layout = propagateChanges(layout);

		Assert.assertFalse(_sites.isLayoutModifiedSinceLastMerge(layout));

		PortletPreferences layoutPortletPreferences =
			LayoutTestUtil.getPortletPreferences(layout, portletId);

		Assert.assertEquals(
			Boolean.FALSE.toString(),
			layoutPortletPreferences.getValue(
				"showAvailableLocales", StringPool.BLANK));

		_layout = propagateChanges(_layout);

		Assert.assertTrue(_sites.isLayoutModifiedSinceLastMerge(_layout));

		layoutPortletPreferences = LayoutTestUtil.getPortletPreferences(
			_layout, _portletId);

		Assert.assertEquals(
			Boolean.TRUE.toString(),
			layoutPortletPreferences.getValue(
				"showAvailableLocales", StringPool.BLANK));
	}

	@Test
	public void testResetPrototypeWithoutPermissions() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user1));

		Group userGroup = GroupLocalServiceUtil.getUserGroup(
			_user2.getCompanyId(), _user2.getUserId());

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			userGroup.getGroupId(), true);

		try {
			_layoutSetPrototypeHelper.resetPrototype(layoutSet);

			Assert.fail(
				"The user should not be able to reset another user's " +
					"dashboard");
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}
	}

	@Test
	public void testResetPrototypeWithPermissions() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleLocalServiceUtil.addUserRole(_user1.getUserId(), role);

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_user1.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_user1.getCompanyId()), role.getRoleId(),
			ActionKeys.UPDATE);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user1));

		Group userGroup = GroupLocalServiceUtil.getUserGroup(
			_user2.getCompanyId(), _user2.getUserId());

		_layoutSetPrototypeHelper.resetPrototype(
			LayoutSetLocalServiceUtil.getLayoutSet(
				userGroup.getGroupId(), true));
	}

	@Test
	public void testResetUserPrototypeWithoutPermissions() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user1));

		Group userGroup = GroupLocalServiceUtil.getUserGroup(
			_user1.getCompanyId(), _user1.getUserId());

		_layoutSetPrototypeHelper.resetPrototype(
			LayoutSetLocalServiceUtil.getLayoutSet(
				userGroup.getGroupId(), true));
	}

	@FeatureFlag(enable = false, value = "LPD-38869")
	@Test
	public void testThemeSettingsAfterLayoutPropagation() throws Exception {
		LayoutSet prototypePrivateLayoutSet =
			_layoutSetPrototypeGroup.getPrivateLayoutSet();

		prototypePrivateLayoutSet.setThemeId(_THEME_ID);

		prototypePrivateLayoutSet = LayoutSetLocalServiceUtil.updateLayoutSet(
			prototypePrivateLayoutSet);

		LayoutSet prototypePublicLayoutSet =
			_layoutSetPrototypeGroup.getPublicLayoutSet();

		prototypePublicLayoutSet.setThemeId(_THEME_ID);

		LayoutSetLocalServiceUtil.updateLayoutSet(prototypePublicLayoutSet);

		_layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.fetchLayoutSetPrototype(
				_layoutSetPrototype.getLayoutSetPrototypeId());

		_layoutSetPrototype.setModifiedDate(new Date());

		_layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.updateLayoutSetPrototype(
				_layoutSetPrototype);

		propagateChanges(group);

		LayoutSet propagatedLayoutSet = group.getPrivateLayoutSet();

		Assert.assertEquals(
			prototypePrivateLayoutSet.getThemeId(),
			propagatedLayoutSet.getThemeId());
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

		Group testGroup = GroupTestUtil.addGroup();

		try {
			prototypePrivateLayoutSet.setThemeId(_THEME_ID);

			prototypePrivateLayoutSet =
				LayoutSetLocalServiceUtil.updateLayoutSet(
					prototypePrivateLayoutSet);

			layoutSetPrototype =
				LayoutSetPrototypeLocalServiceUtil.fetchLayoutSetPrototype(
					layoutSetPrototype.getLayoutSetPrototypeId());

			layoutSetPrototype.setModifiedDate(new Date());

			layoutSetPrototype =
				LayoutSetPrototypeLocalServiceUtil.updateLayoutSetPrototype(
					layoutSetPrototype);

			LayoutSet privateLayoutSet =
				LayoutSetLocalServiceUtil.fetchLayoutSet(
					testGroup.getGroupId(), true);

			privateLayoutSet.setLayoutSetPrototypeLinkEnabled(true);

			LayoutSetLocalServiceUtil.updateLayoutSet(privateLayoutSet);

			setLinkEnabled(
				testGroup, 0, layoutSetPrototype.getLayoutSetPrototypeId(),
				false, true);

			MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

			_sites.mergeLayoutSetPrototypeLayouts(
				testGroup, testGroup.getPrivateLayoutSet());

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
		}
		finally {
			GroupTestUtil.deleteGroup(testGroup);

			GroupTestUtil.deleteGroup(layoutSetPrototypeGroup);
		}
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

		Group testGroup = GroupTestUtil.addGroup();

		try {
			prototypePrivateLayoutSet.setThemeId(_THEME_ID);

			prototypePrivateLayoutSet =
				LayoutSetLocalServiceUtil.updateLayoutSet(
					prototypePrivateLayoutSet);

			layoutSetPrototype =
				LayoutSetPrototypeLocalServiceUtil.fetchLayoutSetPrototype(
					layoutSetPrototype.getLayoutSetPrototypeId());

			layoutSetPrototype.setModifiedDate(new Date());

			layoutSetPrototype =
				LayoutSetPrototypeLocalServiceUtil.updateLayoutSetPrototype(
					layoutSetPrototype);

			LayoutSet publicLayoutSet =
				LayoutSetLocalServiceUtil.fetchLayoutSet(
					testGroup.getGroupId(), false);

			publicLayoutSet.setLayoutSetPrototypeLinkEnabled(true);

			LayoutSetLocalServiceUtil.updateLayoutSet(publicLayoutSet);

			setLinkEnabled(
				testGroup, layoutSetPrototype.getLayoutSetPrototypeId(), 0,
				true, false);

			MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

			_sites.mergeLayoutSetPrototypeLayouts(
				testGroup, testGroup.getPublicLayoutSet());

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
		}
		finally {
			GroupTestUtil.deleteGroup(testGroup);

			GroupTestUtil.deleteGroup(layoutSetPrototypeGroup);
		}
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

		_layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.fetchLayoutSetPrototype(
				_layoutSetPrototype.getLayoutSetPrototypeId());

		_layoutSetPrototype.setModifiedDate(new Date());

		_layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.updateLayoutSetPrototype(
				_layoutSetPrototype);

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

		Assert.assertEquals(
			_initialPrototypeLayoutsCount, getGroupLayoutCount());

		propagateChanges(group);

		if (linkEnabled) {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount + 1, getGroupLayoutCount());
		}
		else {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount, getGroupLayoutCount());
		}

		LayoutLocalServiceUtil.deleteLayout(
			layout, ServiceContextTestUtil.getServiceContext());

		if (linkEnabled) {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount + 1, getGroupLayoutCount());
		}
		else {
			Assert.assertEquals(
				_initialPrototypeLayoutsCount, getGroupLayoutCount());
		}

		propagateChanges(group);

		Assert.assertEquals(
			_initialPrototypeLayoutsCount, getGroupLayoutCount());
	}

	protected void doTestLayoutPropagationWithLayoutPrototype(
			boolean layoutSetLayoutLinkEnabled)
		throws Exception {

		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		_layoutSetPrototypeLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true, layoutPrototype,
			layoutSetLayoutLinkEnabled);

		_layoutSetPrototypeLayout = propagateChanges(_layoutSetPrototypeLayout);

		propagateChanges(group);

		Layout layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			group.getGroupId(), false,
			_layoutSetPrototypeLayout.getFriendlyURL());

		LayoutTestUtil.updateLayoutTemplateId(
			layoutPrototypeLayout, "1_column");

		if (layoutSetLayoutLinkEnabled) {
			Assert.assertEquals(
				initialLayoutTemplateId,
				LayoutTestUtil.getLayoutTemplateId(layout));
		}

		layout = propagateChanges(layout);

		propagateChanges(group);

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

		// Portlet data is no longer propagated once the group has been created

		for (String languageId : journalArticle.getAvailableLanguageIds()) {
			String localization = content.get(languageId);

			String importedLocalization = _journalContent.getContent(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				Constants.VIEW, languageId);

			Assert.assertEquals(localization, importedLocalization);
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

	protected void propagateChanges(Group group) throws Exception {
		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			group.getGroupId(), false);

		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

		_sites.mergeLayoutSetPrototypeLayouts(group, layoutSet);

		Thread.sleep(2000);

		LayoutSetPrototype layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		LayoutSet layoutSetPrototypeLayoutSet =
			layoutSetPrototype.getLayoutSet();

		UnicodeProperties layoutSetPrototypeSettingsUnicodeProperties =
			layoutSetPrototypeLayoutSet.getSettingsProperties();

		int mergeFailCount = GetterUtil.getInteger(
			layoutSetPrototypeSettingsUnicodeProperties.getProperty(
				Sites.MERGE_FAIL_COUNT));

		Assert.assertEquals(0, mergeFailCount);
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

			layout.setLayoutPrototypeLinkEnabled(linkEnabled);

			LayoutLocalServiceUtil.updateLayout(layout);

			_layout = LayoutLocalServiceUtil.getLayout(_layout.getPlid());

			_layout.setLayoutPrototypeLinkEnabled(linkEnabled);

			LayoutLocalServiceUtil.updateLayout(_layout);
		}

		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		_sites.updateLayoutSetPrototypesLinks(
			group, _layoutSetPrototype.getLayoutSetPrototypeId(), 0,
			linkEnabled, linkEnabled);

		Thread.sleep(2000);
	}

	protected void setLinkEnabled(
			Group group, long publicLayoutSetPrototypeId,
			long privateLayoutSetPrototypeId, boolean publicLinkEnabled,
			boolean privateLinkEnabled)
		throws Exception {

		_sites.updateLayoutSetPrototypesLinks(
			group, publicLayoutSetPrototypeId, privateLayoutSetPrototypeId,
			publicLinkEnabled, privateLinkEnabled);

		Thread.sleep(2000);
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

	private void _propagateChanges(int failCount, int layoutCount)
		throws Exception {

		LayoutSet layoutSet = group.getPublicLayoutSet();

		List<Layout> initialMergeFailFriendlyURLLayouts =
			layoutSet.getMergeFailFriendlyURLLayouts();

		propagateChanges(group);

		layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			layoutSet.getLayoutSetId());

		List<Layout> mergeFailFriendlyURLLayouts =
			layoutSet.getMergeFailFriendlyURLLayouts();

		Assert.assertEquals(
			mergeFailFriendlyURLLayouts.toString(),
			initialMergeFailFriendlyURLLayouts.size() + failCount,
			mergeFailFriendlyURLLayouts.size());

		Assert.assertEquals(
			_initialLayoutCount + layoutCount, getGroupLayoutCount());
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

	private static final String _THEME_ID = "minium_WAR_miniumtheme";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPrototypePropagationTest.class);

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

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

	@Inject
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	private JournalArticle _layoutSetPrototypeJournalArticle;

	@DeleteAfterTestRun
	private Layout _layoutSetPrototypeLayout;

	@Inject
	private Portal _portal;

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

}