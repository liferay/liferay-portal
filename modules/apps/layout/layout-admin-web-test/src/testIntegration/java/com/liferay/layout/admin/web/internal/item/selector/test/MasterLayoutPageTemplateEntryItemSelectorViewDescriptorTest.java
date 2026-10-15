/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.item.selector.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class MasterLayoutPageTemplateEntryItemSelectorViewDescriptorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			_group.getCompanyId(), true, "LPD-57283");
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-57283"), @FeatureFlag("LPD-81022")}
	)
	@Test
	@TestInfo("LPD-81022")
	public void testGetSearchContainer() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Group designLibraryGroup = _addDesignLibraryGroup(true);

		LayoutPageTemplateEntry designLibraryLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				designLibraryGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Group unconnectedDesignLibraryGroup = _addDesignLibraryGroup(false);

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			unconnectedDesignLibraryGroup.getGroupId(),
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			Arrays.asList("Blank", layoutPageTemplateEntry.getName()),
			_getNames(_group));
		Assert.assertEquals(
			Arrays.asList(designLibraryLayoutPageTemplateEntry.getName()),
			_getNames(designLibraryGroup));
		Assert.assertEquals(
			Collections.emptyList(), _getNames(unconnectedDesignLibraryGroup));
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag("LPD-57283"),
			@FeatureFlag(enable = false, value = "LPD-81022")
		}
	)
	@Test
	@TestInfo("LPD-81022")
	public void testGetSearchContainerWhenFeatureFlagIsDisabled()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Group designLibraryGroup = _addDesignLibraryGroup(true);

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			designLibraryGroup.getGroupId(),
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			Arrays.asList("Blank", layoutPageTemplateEntry.getName()),
			_getNames(_group));
		Assert.assertEquals(
			Collections.emptyList(), _getNames(designLibraryGroup));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				_getMasterLayoutPageTemplateEntryItemSelectorViewDescriptor(
					_group),
				"isShowBreadcrumb", new Class<?>[0]));
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-57283"), @FeatureFlag("LPD-81022")}
	)
	@Test
	@TestInfo("LPD-81022")
	public void testIsShowBreadcrumb() throws Exception {
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				_getMasterLayoutPageTemplateEntryItemSelectorViewDescriptor(
					_group),
				"isShowBreadcrumb", new Class<?>[0]));

		_addDesignLibraryGroup(true);

		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				_getMasterLayoutPageTemplateEntryItemSelectorViewDescriptor(
					_group),
				"isShowBreadcrumb", new Class<?>[0]));
	}

	private Group _addDesignLibraryGroup(boolean connected) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		if (connected) {
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), _group.getGroupId());
		}

		return depotEntry.getGroup();
	}

	private Object _getMasterLayoutPageTemplateEntryItemSelectorViewDescriptor(
			Group scopeGroup)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setRefererGroupId(_group.getGroupId());
		themeDisplay.setScopeGroupId(scopeGroup.getGroupId());

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			new MockLiferayResourceRequest());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Bundle bundle = BundleUtil.getBundle(
			SystemBundleUtil.getBundleContext(),
			"com.liferay.layout.admin.web");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.layout.admin.web.internal.item.selector." +
				"MasterLayoutPageTemplateEntryItemSelectorViewDescriptor");

		Constructor<?> constructor = clazz.getConstructor(
			HttpServletRequest.class, PortletURL.class);

		return constructor.newInstance(
			mockHttpServletRequest, new MockLiferayPortletURL());
	}

	private List<String> _getNames(Group scopeGroup) throws Exception {
		SearchContainer<LayoutPageTemplateEntry> searchContainer =
			ReflectionTestUtil.invoke(
				_getMasterLayoutPageTemplateEntryItemSelectorViewDescriptor(
					scopeGroup),
				"getSearchContainer", new Class<?>[0]);

		return TransformUtil.transform(
			searchContainer.getResults(), LayoutPageTemplateEntry::getName);
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}