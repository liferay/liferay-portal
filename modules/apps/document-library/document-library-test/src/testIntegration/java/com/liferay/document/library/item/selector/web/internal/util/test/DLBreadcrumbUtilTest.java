/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.folder.criterion.FolderItemSelectorCriterion;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class DLBreadcrumbUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-81022")
	@Test
	@TestInfo("LPD-109452")
	public void testAddPortletBreadcrumbEntries() throws Exception {
		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		List<String> titles = TransformUtil.transform(
			_getBreadcrumbEntries(group.getGroupId(), depotEntry.getGroupId()),
			BreadcrumbEntry::getTitle);

		Assert.assertEquals(
			Arrays.asList(
				"Sites and Libraries", "Sites",
				group.getDescriptiveName(LocaleUtil.US)),
			titles);
	}

	private List<BreadcrumbEntry> _getBreadcrumbEntries(
			long repositoryId, long scopeGroupId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			new MockLiferayResourceRequest());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setScopeGroupId(scopeGroupId);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Bundle bundle = FrameworkUtil.getBundle(_itemSelectorView.getClass());

		Class<?> clazz = bundle.loadClass(
			"com.liferay.document.library.item.selector.web.internal.util." +
				"DLBreadcrumbUtil");

		ReflectionTestUtil.invoke(
			clazz, "addPortletBreadcrumbEntries",
			new Class<?>[] {
				String.class, Folder.class, HttpServletRequest.class,
				LiferayPortletResponse.class, PortletURL.class, long.class,
				boolean.class
			},
			RandomTestUtil.randomString(), null, mockHttpServletRequest,
			new MockLiferayPortletRenderResponse(), new MockLiferayPortletURL(),
			repositoryId, true);

		return (List<BreadcrumbEntry>)mockHttpServletRequest.getAttribute(
			WebKeys.PORTLET_BREADCRUMBS);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.item.selector.web.internal.folder.DLFolderItemSelectorView",
		type = ItemSelectorView.class
	)
	private ItemSelectorView<FolderItemSelectorCriterion> _itemSelectorView;

}