/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class BaseConfigurationActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-33733")
	public void testUpdateDisplayStyleGroupPreferencesWithDifferentGroup()
		throws Exception {

		Group group = _getGroup(RandomTestUtil.randomLong());

		_setUpGroupLocalServiceUtil(group);

		TestConfigurationAction testConfigurationAction =
			new TestConfigurationAction();

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		testConfigurationAction.postProcess(
			_COMPANY_ID,
			_getMockActionRequest(
				group.getGroupKey(),
				_getThemeDisplay(RandomTestUtil.randomLong())),
			portletPreferences);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroup(
				_COMPANY_ID, group.getGroupKey()));

		Mockito.verify(
			portletPreferences
		).setValue(
			"displayStyleGroupExternalReferenceCode",
			group.getExternalReferenceCode()
		);
	}

	@Test
	public void testUpdateDisplayStyleGroupPreferencesWithNoSelection()
		throws Exception {

		_setUpGroupLocalServiceUtil(_getGroup(RandomTestUtil.randomLong()));

		TestConfigurationAction testConfigurationAction =
			new TestConfigurationAction();

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		testConfigurationAction.postProcess(
			_COMPANY_ID,
			_getMockActionRequest(
				StringPool.BLANK,
				_getThemeDisplay(RandomTestUtil.randomLong())),
			portletPreferences);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroup(
				_COMPANY_ID, StringPool.BLANK));

		Mockito.verify(
			portletPreferences
		).reset(
			"displayStyleGroupExternalReferenceCode"
		);
	}

	@Test
	public void testUpdateDisplayStyleGroupPreferencesWithSameGroup()
		throws Exception {

		Group group = _getGroup(RandomTestUtil.randomLong());

		_setUpGroupLocalServiceUtil(group);

		TestConfigurationAction testConfigurationAction =
			new TestConfigurationAction();

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		testConfigurationAction.postProcess(
			_COMPANY_ID,
			_getMockActionRequest(
				group.getGroupKey(), _getThemeDisplay(group.getGroupId())),
			portletPreferences);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroup(
				_COMPANY_ID, group.getGroupKey()));

		Mockito.verify(
			portletPreferences
		).reset(
			"displayStyleGroupExternalReferenceCode"
		);
	}

	private Group _getGroup(long groupId) {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			group.getGroupKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return group;
	}

	private MockActionRequest _getMockActionRequest(
		String displayStyleGroupKey, ThemeDisplay themeDisplay) {

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
		mockActionRequest.setParameter(
			"preferences--displayStyleGroupKey--", displayStyleGroupKey);

		return mockActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(long groupId) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		themeDisplay.setCompany(company);

		themeDisplay.setScopeGroupId(groupId);

		return themeDisplay;
	}

	private void _setUpGroupLocalServiceUtil(Group group) throws Exception {
		_groupLocalServiceUtilMockedStatic.reset();

		if (group == null) {
			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroup(
					Mockito.anyLong(), Mockito.anyString())
			).thenReturn(
				null
			);

			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.getGroup(Mockito.anyLong())
			).thenReturn(
				null
			);
		}
		else {
			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroup(
					_COMPANY_ID, group.getGroupKey())
			).thenReturn(
				group
			);

			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.getGroup(group.getGroupId())
			).thenReturn(
				group
			);
		}
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	private class TestConfigurationAction extends BaseConfigurationAction {
	}

}