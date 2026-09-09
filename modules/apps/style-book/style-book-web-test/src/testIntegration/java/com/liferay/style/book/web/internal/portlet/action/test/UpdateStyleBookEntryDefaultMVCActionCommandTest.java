/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

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
public class UpdateStyleBookEntryDefaultMVCActionCommandTest {

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

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_themeDisplay.setLayout(layout);
		_themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
	}

	@Test
	public void testMarkAsDefaultStyleBookEntry() throws Exception {
		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK, themeId,
				_serviceContext);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"defaultStyleBookEntry", Boolean.TRUE.toString());
		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry.getStyleBookEntryId()));
		mockLiferayPortletActionRequest.addParameter("themeId", themeId);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, StyleBookPortletKeys.STYLE_BOOK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_updateStyleBookEntryDefaultMVCActionCommandTest.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Assert.assertEquals(
			styleBookEntry,
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), themeId));
	}

	@Test
	public void testReplaceDefaultStyleBookEntry() throws Exception {
		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK, themeId,
				_serviceContext);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"defaultStyleBookEntry", Boolean.TRUE.toString());
		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry1.getStyleBookEntryId()));
		mockLiferayPortletActionRequest.addParameter("themeId", themeId);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, StyleBookPortletKeys.STYLE_BOOK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_updateStyleBookEntryDefaultMVCActionCommandTest.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Assert.assertEquals(
			styleBookEntry1,
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), themeId));

		StyleBookEntry styleBookEntry2 =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK, themeId,
				_serviceContext);

		mockLiferayPortletActionRequest = new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"defaultStyleBookEntry", Boolean.TRUE.toString());
		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry2.getStyleBookEntryId()));
		mockLiferayPortletActionRequest.addParameter("themeId", themeId);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, StyleBookPortletKeys.STYLE_BOOK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_updateStyleBookEntryDefaultMVCActionCommandTest.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Assert.assertEquals(
			styleBookEntry2,
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), themeId));
		Assert.assertNotEquals(
			styleBookEntry1,
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), themeId));
	}

	@Test
	public void testUnmarkAsDefaultStyleBookEntry() throws Exception {
		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK, themeId,
				_serviceContext);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"defaultStyleBookEntry", Boolean.TRUE.toString());
		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry.getStyleBookEntryId()));
		mockLiferayPortletActionRequest.addParameter("themeId", themeId);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, StyleBookPortletKeys.STYLE_BOOK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_updateStyleBookEntryDefaultMVCActionCommandTest.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Assert.assertEquals(
			styleBookEntry,
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), themeId));

		mockLiferayPortletActionRequest = new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"defaultStyleBookEntry", Boolean.FALSE.toString());
		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry.getStyleBookEntryId()));
		mockLiferayPortletActionRequest.addParameter("themeId", themeId);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, StyleBookPortletKeys.STYLE_BOOK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_updateStyleBookEntryDefaultMVCActionCommandTest.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Assert.assertNull(
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), themeId));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	private ThemeDisplay _themeDisplay;

	@Inject(
		filter = "mvc.command.name=/style_book/update_style_book_entry_default"
	)
	private MVCActionCommand _updateStyleBookEntryDefaultMVCActionCommandTest;

}