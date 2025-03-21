/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.BasePortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.FindStrutsAction;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Julio Camarero
 * @author László Csontos
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class PortletLayoutFinderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_portletIds = new String[] {
			PortletProviderUtil.getPortletId(
				BlogsEntry.class.getName(), PortletProvider.Action.MANAGE),
			PortletProviderUtil.getPortletId(
				BlogsEntry.class.getName(), PortletProvider.Action.VIEW)
		};

		_portletLayoutFinder = new BasePortletLayoutFinder() {

			@Override
			protected String[] getPortletIds() {
				return _portletIds;
			}

		};

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testGetPlidAndPortletIdViewInContext() throws Exception {
		addLayouts(true, false);

		PortletLayoutFinder.Result result = _portletLayoutFinder.find(
			getThemeDisplay(), _blogsEntryGroupId);

		Assert.assertEquals(_blogLayout.getPlid(), result.getPlid());

		Assert.assertEquals(
			PortletProviderUtil.getPortletId(
				BlogsEntry.class.getName(), PortletProvider.Action.VIEW),
			result.getPortletId());
	}

	@Test(expected = NoSuchLayoutException.class)
	public void testGetPlidAndPortletIdWhenPortletDoesNotExist()
		throws Exception {

		addLayouts(false, false);

		_portletLayoutFinder.find(getThemeDisplay(), _blogsEntryGroupId);
	}

	@Test
	public void testSetTargetGroupWithDifferentGroup() throws Exception {
		addLayouts(true, true);

		HttpServletRequest httpServletRequest = getHttpServletRequest();

		ReflectionTestUtil.invoke(
			FindStrutsAction.class, "_setTargetLayout",
			new Class<?>[] {HttpServletRequest.class, long.class, long.class},
			httpServletRequest, _blogsEntryGroupId, _blogLayout.getPlid());

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		Assert.assertTrue(layout instanceof VirtualLayout);
		Assert.assertNotEquals(_group.getGroupId(), layout.getGroupId());
	}

	@Test
	public void testSetTargetGroupWithSameGroup() throws Exception {
		addLayouts(true, false);

		HttpServletRequest httpServletRequest = getHttpServletRequest();

		ReflectionTestUtil.invoke(
			FindStrutsAction.class, "_setTargetLayout",
			new Class<?>[] {HttpServletRequest.class, long.class, long.class},
			httpServletRequest, _blogsEntryGroupId, _blogLayout.getPlid());

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		Assert.assertNull(layout);
	}

	protected void addLayouts(
			boolean portletExists, boolean blogEntryWithDifferentGroup)
		throws Exception {

		_group = GroupTestUtil.addGroup();

		_blogLayout = LayoutTestUtil.addTypePortletLayout(_group);
		_assetLayout = LayoutTestUtil.addTypePortletLayout(_group);

		if (portletExists) {
			LayoutTestUtil.addPortletToLayout(
				_blogLayout,
				PortletProviderUtil.getPortletId(
					BlogsEntry.class.getName(), PortletProvider.Action.VIEW));
		}

		Group group = _group;

		if (blogEntryWithDifferentGroup) {
			group = GroupTestUtil.addGroup();
		}

		_blogsEntryGroupId = group.getGroupId();
	}

	protected HttpServletRequest getHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, getThemeDisplay());

		return httpServletRequest;
	}

	protected ThemeDisplay getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setPlid(_assetLayout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private static String[] _portletIds;

	private Layout _assetLayout;
	private Layout _blogLayout;
	private long _blogsEntryGroupId;

	@DeleteAfterTestRun
	private Group _group;

	private PermissionChecker _originalPermissionChecker;
	private PortletLayoutFinder _portletLayoutFinder;

}