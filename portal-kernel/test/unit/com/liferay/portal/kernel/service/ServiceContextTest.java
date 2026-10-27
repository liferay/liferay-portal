/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupWrapper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Shuyang Zhou
 */
public class ServiceContextTest {

	@Before
	public void setUp() {
		GroupLocalServiceUtil.setService(
			new GroupLocalServiceWrapper() {

				@Override
				public Group getGroup(long groupId) {
					_groupIds.add(groupId);

					return new GroupWrapper(null) {

						@Override
						public long getGroupId() {
							return groupId;
						}

					};
				}

			});
	}

	@After
	public void tearDown() {
		GroupLocalServiceUtil.setService(null);
	}

	@Test
	public void testGetScopeGroup() throws Exception {
		_testGetScopeGroupWithThemeDisplay();
		_testGetScopeGroupWithThemeDisplayOfAnotherGroup();
		_testGetScopeGroupWithoutThemeDisplay();
	}

	private ServiceContext _getServiceContext(
		long scopeGroupId, ThemeDisplay themeDisplay) {

		ServiceContext serviceContext = new ServiceContext();

		if (themeDisplay != null) {
			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			serviceContext.setRequest(mockHttpServletRequest);
		}

		serviceContext.setScopeGroupId(scopeGroupId);

		return serviceContext;
	}

	private ThemeDisplay _getThemeDisplay(long scopeGroupId) {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(scopeGroupId);

		return themeDisplay;
	}

	private void _testGetScopeGroupWithThemeDisplay() throws Exception {
		long scopeGroupId = RandomTestUtil.randomLong();

		ThemeDisplay themeDisplay = _getThemeDisplay(scopeGroupId);

		ServiceContext serviceContext = _getServiceContext(
			scopeGroupId, themeDisplay);

		_groupIds.clear();

		Assert.assertSame(
			themeDisplay.getScopeGroup(), serviceContext.getScopeGroup());
		Assert.assertTrue(_groupIds.toString(), _groupIds.isEmpty());
	}

	private void _testGetScopeGroupWithThemeDisplayOfAnotherGroup()
		throws Exception {

		long scopeGroupId = RandomTestUtil.randomLong();

		ServiceContext serviceContext = _getServiceContext(
			scopeGroupId, _getThemeDisplay(RandomTestUtil.randomLong()));

		_groupIds.clear();

		Group scopeGroup = serviceContext.getScopeGroup();

		Assert.assertEquals(scopeGroupId, scopeGroup.getGroupId());

		Assert.assertEquals(Collections.singletonList(scopeGroupId), _groupIds);
	}

	private void _testGetScopeGroupWithoutThemeDisplay() throws Exception {
		long scopeGroupId = RandomTestUtil.randomLong();

		ServiceContext serviceContext = _getServiceContext(scopeGroupId, null);

		_groupIds.clear();

		Group scopeGroup = serviceContext.getScopeGroup();

		Assert.assertEquals(scopeGroupId, scopeGroup.getGroupId());

		Assert.assertEquals(Collections.singletonList(scopeGroupId), _groupIds);
	}

	private final List<Long> _groupIds = new ArrayList<>();

}