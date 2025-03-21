/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.model.KBArticle;
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
 * @author Sam Ziemer
 */
@RunWith(Arquillian.class)
public class KBArticlePortletLayoutFinderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_portletIds = new String[] {
			PortletProviderUtil.getPortletId(
				KBArticle.class.getName(), PortletProvider.Action.MANAGE),
			PortletProviderUtil.getPortletId(
				KBArticle.class.getName(), PortletProvider.Action.VIEW)
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

	@Test(expected = NoSuchLayoutException.class)
	public void testGetPlidAndPortletIdWhenPortletDoesNotExist()
		throws Exception {

		_addLayouts(false, false);

		_portletLayoutFinder.find(_getThemeDisplay(), _kbArticleGroupId);
	}

	@Test
	public void testSetTargetGroupWithDifferentGroup() throws Exception {
		_addLayouts(true, true);

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		ReflectionTestUtil.invoke(
			FindStrutsAction.class, "_setTargetLayout",
			new Class<?>[] {HttpServletRequest.class, long.class, long.class},
			httpServletRequest, _kbArticleGroupId, _kbLayout.getPlid());

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		Assert.assertTrue(layout instanceof VirtualLayout);
		Assert.assertNotEquals(_group.getGroupId(), layout.getGroupId());
	}

	@Test
	public void testSetTargetGroupWithSameGroup() throws Exception {
		_addLayouts(true, false);

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		ReflectionTestUtil.invoke(
			FindStrutsAction.class, "_setTargetLayout",
			new Class<?>[] {HttpServletRequest.class, long.class, long.class},
			httpServletRequest, _kbArticleGroupId, _kbLayout.getPlid());

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		Assert.assertNull(layout);
	}

	private void _addLayouts(
			boolean portletExists, boolean blogEntryWithDifferentGroup)
		throws Exception {

		_group = GroupTestUtil.addGroup();

		_kbLayout = LayoutTestUtil.addTypePortletLayout(_group);

		if (portletExists) {
			LayoutTestUtil.addPortletToLayout(
				_kbLayout,
				PortletProviderUtil.getPortletId(
					KBArticle.class.getName(), PortletProvider.Action.VIEW));
		}

		Group group = _group;

		if (blogEntryWithDifferentGroup) {
			group = GroupTestUtil.addGroup();
		}

		_kbArticleGroupId = group.getGroupId();
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return httpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setPlid(_kbLayout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private static String[] _portletIds;

	@DeleteAfterTestRun
	private Group _group;

	private long _kbArticleGroupId;
	private Layout _kbLayout;
	private PermissionChecker _originalPermissionChecker;
	private PortletLayoutFinder _portletLayoutFinder;

}