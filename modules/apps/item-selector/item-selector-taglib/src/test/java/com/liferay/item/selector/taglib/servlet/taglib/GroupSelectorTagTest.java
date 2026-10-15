/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.servlet.taglib;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class GroupSelectorTagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws PortalException {
		_companyGroup = Mockito.mock(Group.class);

		Mockito.when(
			_companyGroup.isCompany()
		).thenReturn(
			true
		);

		Mockito.when(
			GroupLocalServiceUtil.getCompanyGroup(_COMPANY_ID)
		).thenReturn(
			_companyGroup
		);

		Mockito.when(
			GroupLocalServiceUtil.getGroup(_COMPANY_GROUP_ID)
		).thenReturn(
			_companyGroup
		);

		long[] groupIds = new long[2];

		groupIds[0] = _GROUP_ID;
		groupIds[1] = _COMPANY_GROUP_ID;

		Mockito.when(
			PortalUtil.getCurrentAndAncestorSiteGroupIds(_GROUP_ID, true)
		).thenReturn(
			groupIds
		);
	}

	@After
	public void tearDown() {
		_groupLocalServiceUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-81022")
	public void testSetAttributes() throws PortalException {
		_testSetAttributes(
			Arrays.asList(_companyGroup), 1, _companyGroup, false);

		Group group = _getGroup();

		Mockito.when(
			GroupLocalServiceUtil.getGroup(_GROUP_ID)
		).thenReturn(
			group
		);

		_testSetAttributes(Arrays.asList(group), 1, group, true);
		_testSetAttributes(
			Arrays.asList(group, _companyGroup), 2, group, false);
	}

	private Group _getGroup() {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			group.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			group.isCompany()
		).thenReturn(
			false
		);

		return group;
	}

	private void _testSetAttributes(
		List<Group> expectedGroups, long expectedGroupsCount, Group group,
		boolean scopeGroupOnly) {

		GroupSelectorTag groupSelectorTag = new GroupSelectorTag();

		HttpServletRequest httpServletRequest = new TestMockHttpServletRequest(
			scopeGroupOnly);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
		);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		groupSelectorTag.setAttributes(httpServletRequest);

		List<Group> actualGroups = (ArrayList)httpServletRequest.getAttribute(
			"liferay-item-selector:group-selector:groups");

		Assert.assertEquals(
			actualGroups.toString(), expectedGroups.size(),
			actualGroups.size());

		for (int i = 0; i < expectedGroups.size(); i++) {
			Assert.assertEquals(expectedGroups.get(i), actualGroups.get(i));
		}

		long actualGroupsCount = GetterUtil.getLong(
			httpServletRequest.getAttribute(
				"liferay-item-selector:group-selector:groupsCount"));

		Assert.assertEquals(expectedGroupsCount, actualGroupsCount);
	}

	private static final long _COMPANY_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private Group _companyGroup;
	private final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

	private static class TestMockHttpServletRequest
		extends MockHttpServletRequest {

		public TestMockHttpServletRequest(boolean scopeGroupOnly) {
			_scopeGroupOnly = scopeGroupOnly;
		}

		@Override
		public String getParameter(String name) {
			if (Objects.equals(name, "groupType")) {
				return "site";
			}

			if (Objects.equals(name, "scopeGroupOnly")) {
				return String.valueOf(_scopeGroupOnly);
			}

			if (Objects.equals(name, "scopeGroupType")) {
				return "true";
			}

			return super.getParameter(name);
		}

		private final boolean _scopeGroupOnly;

	}

}