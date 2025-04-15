/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.exception.StyleBookEntryNameException;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.ActionRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AddStyleBookEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = new ServiceContext();

		_serviceContext.setScopeGroupId(_group.getGroupId());
		_serviceContext.setUserId(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		_themeDisplay.setLanguageId(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()));
		_themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		_themeDisplay.setRealUser(TestPropsValues.getUser());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddStyleBookEntry() {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		String name = RandomTestUtil.randomString();

		mockLiferayPortletActionRequest.addParameter("name", name);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		ReflectionTestUtil.invoke(
			_addStyleBookEntryMVCActionCommandTest, "_addStyleBookEntry",
			new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);

		Assert.assertEquals(
			1,
			_styleBookEntryLocalService.getStyleBookEntriesCount(
				_group.getGroupId()));
		Assert.assertEquals(
			1,
			_styleBookEntryLocalService.getStyleBookEntriesCount(
				_group.getGroupId(), name));
	}

	@Test(expected = StyleBookEntryNameException.class)
	public void testAddStyleBookEntryWithEmptyName() {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter("name", StringPool.BLANK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		ReflectionTestUtil.invoke(
			_addStyleBookEntryMVCActionCommandTest, "_addStyleBookEntry",
			new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);
	}

	@Test(expected = StyleBookEntryNameException.class)
	public void testAddStyleBookEntryWithInvalidCharPeriod() {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter("name", ".");
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		ReflectionTestUtil.invoke(
			_addStyleBookEntryMVCActionCommandTest, "_addStyleBookEntry",
			new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);
	}

	@Test(expected = StyleBookEntryNameException.class)
	public void testAddStyleBookEntryWithInvalidCharSlash() {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter("name", "/");
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		ReflectionTestUtil.invoke(
			_addStyleBookEntryMVCActionCommandTest, "_addStyleBookEntry",
			new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);
	}

	@Test(expected = StyleBookEntryNameException.class)
	public void testAddStyleBookEntryWithNameMaxLength() {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"name", RandomTestUtil.randomString(256));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		ReflectionTestUtil.invoke(
			_addStyleBookEntryMVCActionCommandTest, "_addStyleBookEntry",
			new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);
	}

	@Inject(filter = "mvc.command.name=/style_book/add_style_book_entry")
	private MVCActionCommand _addStyleBookEntryMVCActionCommandTest;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	private ThemeDisplay _themeDisplay;

}