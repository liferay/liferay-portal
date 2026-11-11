/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.DesignLibraryTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class AssetDisplayPageUsagesManagementToolbarDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@FeatureFlag("LPD-57283")
	@Test
	public void testGetDefaultLayoutPageTemplateEntry() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		Assert.assertNull(_getDefaultLayoutPageTemplateEntry());

		long classNameId = _portal.getClassNameId(BlogsEntry.class);
		Group designLibraryGroup = DesignLibraryTestUtil.addDesignLibraryGroup(
			_group.getGroupId(), true);

		LayoutPageTemplateEntry designLibraryLayoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				designLibraryGroup.getGroupId(), classNameId, null, true,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			designLibraryLayoutPageTemplateEntry,
			_getDefaultLayoutPageTemplateEntry());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, null, true,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			layoutPageTemplateEntry, _getDefaultLayoutPageTemplateEntry());
	}

	private LayoutPageTemplateEntry _getDefaultLayoutPageTemplateEntry()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(), "-",
				WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter(
			"classNameId",
			String.valueOf(_portal.getClassNameId(BlogsEntry.class)));
		mockLiferayPortletRenderRequest.setParameter("classTypeId", "0");

		Bundle bundle = FrameworkUtil.getBundle(
			AssetDisplayPageUsagesManagementToolbarDisplayContextTest.class);

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(),
			"com.liferay.layout.page.template.admin.web");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.layout.page.template.admin.web.internal.display." +
				"context." +
					"AssetDisplayPageUsagesManagementToolbarDisplayContext");

		Constructor<?> constructor = clazz.getConstructor(
			HttpServletRequest.class, LiferayPortletRequest.class,
			LiferayPortletResponse.class, SearchContainer.class);

		Object assetDisplayPageUsagesManagementToolbarDisplayContext =
			constructor.newInstance(
				mockLiferayPortletRenderRequest.getHttpServletRequest(),
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse(), null);

		return ReflectionTestUtil.invoke(
			assetDisplayPageUsagesManagementToolbarDisplayContext,
			"getDefaultLayoutPageTemplateEntry", new Class<?>[0]);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

}