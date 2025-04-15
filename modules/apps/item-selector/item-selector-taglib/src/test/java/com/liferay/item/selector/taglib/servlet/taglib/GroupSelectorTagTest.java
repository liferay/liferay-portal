/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.servlet.taglib;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
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
	}

	@After
	public void tearDown() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testSetAttributes() {
		_testSetAttributes(Arrays.asList(_companyGroup), 1, _companyGroup);

		Group group = _getGroup();

		_testSetAttributes(Arrays.asList(_companyGroup, group), 2, group);
	}

	private Group _getGroup() {
		Group group = Mockito.mock(Group.class);

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
		List<Group> expectedGroups, long expectedGroupsCount, Group group) {

		GroupSelectorTag groupSelectorTag = new GroupSelectorTag();

		HttpServletRequest httpServletRequest =
			new TestMockHttpServletRequest();

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

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private Group _companyGroup;
	private final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	private static class TestMockHttpServletRequest
		extends MockHttpServletRequest {

		@Override
		public String getParameter(String name) {
			if (Objects.equals(name, "groupType")) {
				return "site";
			}

			if (Objects.equals(name, "scopeGroupType")) {
				return "true";
			}

			return super.getParameter(name);
		}

	}

}