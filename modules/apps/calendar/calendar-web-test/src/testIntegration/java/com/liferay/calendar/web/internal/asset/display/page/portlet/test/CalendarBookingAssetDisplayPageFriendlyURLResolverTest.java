/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.asset.display.page.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.test.util.CalendarBookingTestUtil;
import com.liferay.calendar.test.util.CalendarTestUtil;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class CalendarBookingAssetDisplayPageFriendlyURLResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-106977")
	public void testGetActualURL() throws Exception {
		_group = GroupTestUtil.addGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate();

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		CalendarBooking calendarBooking = _addCalendarBooking();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(TestPropsValues.getUser());

		Assert.assertEquals(
			_portal.getLayoutActualURL(layout, Portal.PATH_MAIN),
			_getActualURL(calendarBooking, mockHttpServletRequest));

		ListMergeable<String> titleListMergeable =
			(ListMergeable<String>)mockHttpServletRequest.getAttribute(
				WebKeys.PAGE_TITLE);

		Assert.assertEquals(
			calendarBooking.getTitle(LocaleUtil.getDefault()),
			titleListMergeable.mergeToString(StringPool.SPACE));

		_assertDisplayPageAttributes(true, mockHttpServletRequest);

		_user = UserTestUtil.addUser();

		mockHttpServletRequest = _getMockHttpServletRequest(_user);

		Assert.assertEquals(
			_portal.getLayoutActualURL(layout, Portal.PATH_MAIN),
			_getActualURL(calendarBooking, mockHttpServletRequest));

		_assertDisplayPageAttributes(false, mockHttpServletRequest);
	}

	private CalendarBooking _addCalendarBooking() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		return CalendarBookingTestUtil.addRegularCalendarBooking(
			CalendarTestUtil.addCalendar(_group, serviceContext));
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate() throws Exception {
		return DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(), _portal.getClassNameId(CalendarBooking.class),
			null, true, WorkflowConstants.STATUS_APPROVED);
	}

	private void _assertDisplayPageAttributes(
		boolean expected, MockHttpServletRequest mockHttpServletRequest) {

		for (String name : _DISPLAY_PAGE_ATTRIBUTE_NAMES) {
			Assert.assertEquals(
				name, expected,
				mockHttpServletRequest.getAttribute(name) != null);
		}
	}

	private String _getActualURL(
			CalendarBooking calendarBooking,
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		return _friendlyURLResolver.getActualURL(
			TestPropsValues.getCompanyId(), _group.getGroupId(), false,
			Portal.PATH_MAIN, _getFriendlyURL(calendarBooking),
			Collections.emptyMap(),
			HashMapBuilder.<String, Object>put(
				"request", mockHttpServletRequest
			).build());
	}

	private String _getFriendlyURL(CalendarBooking calendarBooking) {
		return _friendlyURLResolver.getURLSeparator() +
			calendarBooking.getCalendarBookingId();
	}

	private MockHttpServletRequest _getMockHttpServletRequest(User user) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);

		return mockHttpServletRequest;
	}

	private static final String[] _DISPLAY_PAGE_ATTRIBUTE_NAMES = {
		LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
		LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER,
		WebKeys.LAYOUT_ASSET_ENTRY, WebKeys.PAGE_DESCRIPTION,
		WebKeys.PAGE_KEYWORDS, WebKeys.PAGE_TITLE
	};

	@Inject(
		filter = "component.name=com.liferay.calendar.web.internal.asset.display.page.portlet.CalendarBookingAssetDisplayPageFriendlyURLResolver"
	)
	private FriendlyURLResolver _friendlyURLResolver;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private User _user;

}