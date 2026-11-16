/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alvaro Fernandez Gonzalez
 */
@RunWith(Arquillian.class)
public class ViewGroupFragmentEntryUsagesMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		_fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());
	}

	@Test
	@TestInfo("LPD-114647")
	public void testRender() throws Exception {
		User user = TestPropsValues.getUser();

		UserTestUtil.setUser(user);

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			"/view_group_fragment_entry_usages.jsp",
			_mvcRenderCommand.render(
				_getMockLiferayPortletRenderRequest(
					_fragmentEntry.getFragmentEntryId(), user),
				mockLiferayPortletRenderResponse));

		HttpServletResponse httpServletResponse =
			mockLiferayPortletRenderResponse.getHttpServletResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_OK, httpServletResponse.getStatus());
	}

	@Test
	@TestInfo("LPD-114647")
	public void testRenderWithNonexistentFragmentEntry() throws Exception {
		User user = TestPropsValues.getUser();

		UserTestUtil.setUser(user);

		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				RandomTestUtil.randomLong(), user));
	}

	@Test
	@TestInfo("LPD-114647")
	public void testRenderWithoutManageFragmentEntriesPermission()
		throws Exception {

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.POWER_USER);

		UserTestUtil.setUser(_user);

		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				_fragmentEntry.getFragmentEntryId(), _user));
	}

	private void _assertNotFound(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			"/error.jsp",
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				mockLiferayPortletRenderResponse));

		HttpServletResponse httpServletResponse =
			mockLiferayPortletRenderResponse.getHttpServletResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, httpServletResponse.getStatus());

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletRenderRequest, NoSuchEntryException.class));
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			long fragmentEntryId, User user)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			layout);

		themeDisplay.setUser(user);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter(
			"fragmentEntryId", String.valueOf(fragmentEntryId));

		return mockLiferayPortletRenderRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private FragmentEntry _fragmentEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/fragment/view_group_fragment_entry_usages"
	)
	private MVCRenderCommand _mvcRenderCommand;

	@DeleteAfterTestRun
	private User _user;

}