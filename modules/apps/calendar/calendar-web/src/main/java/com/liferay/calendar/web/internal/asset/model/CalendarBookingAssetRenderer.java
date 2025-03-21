/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.calendar.constants.CalendarActionKeys;
import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.web.internal.constants.CalendarWebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Fabio Pezzutto
 * @author Eduardo Lundgren
 * @author Pier Paolo Ramon
 */
public class CalendarBookingAssetRenderer
	extends BaseJSPAssetRenderer<CalendarBooking> implements TrashRenderer {

	public CalendarBookingAssetRenderer(
		CalendarBooking calendarBooking,
		ModelResourcePermission<Calendar> calendarModelResourcePermission) {

		_calendarBooking = calendarBooking;
		_calendarModelResourcePermission = calendarModelResourcePermission;
	}

	@Override
	public CalendarBooking getAssetObject() {
		return _calendarBooking;
	}

	@Override
	public String getClassName() {
		return CalendarBooking.class.getName();
	}

	@Override
	public long getClassPK() {
		return _calendarBooking.getCalendarBookingId();
	}

	@Override
	public long getGroupId() {
		return _calendarBooking.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<CalendarBooking> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public int getStatus() {
		return _calendarBooking.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String summary = _calendarBooking.getDescription(
			getLocale(portletRequest));

		return StringUtil.shorten(HtmlUtil.stripHtml(summary), 200);
	}

	@Override
	public String getTitle(Locale locale) {
		return _calendarBooking.getTitle(locale);
	}

	@Override
	public String getType() {
		return CalendarBookingAssetRendererFactory.TYPE;
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(
			_calendarBooking.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group, CalendarPortletKeys.CALENDAR, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_calendar_booking.jsp"
		).setParameter(
			"calendarBookingId", _calendarBooking.getCalendarBookingId()
		).buildPortletURL();
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		try {
			return PortletURLBuilder.createRenderURL(
				liferayPortletResponse, CalendarPortletKeys.CALENDAR
			).setMVCPath(
				"/view_calendar_booking.jsp"
			).setParameter(
				"calendarBookingId", _calendarBooking.getCalendarBookingId()
			).setParameter(
				"returnToFullPageURL",
				PortalUtil.getCurrentURL(liferayPortletRequest)
			).setWindowState(
				WindowState.MAXIMIZED
			).buildString();
		}
		catch (Exception exception) {
			_log.error("Unable to get view in context URL", exception);
		}

		return null;
	}

	@Override
	public long getUserId() {
		return _calendarBooking.getUserId();
	}

	@Override
	public String getUserName() {
		return _calendarBooking.getUserName();
	}

	@Override
	public String getUuid() {
		return _calendarBooking.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _calendarModelResourcePermission.contains(
			permissionChecker, _calendarBooking.getCalendar(),
			CalendarActionKeys.MANAGE_BOOKINGS);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _calendarModelResourcePermission.contains(
			permissionChecker, _calendarBooking.getCalendar(), ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			CalendarWebKeys.CALENDAR_BOOKING, _calendarBooking);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isCommentable() {
		try {
			Calendar calendar = _calendarBooking.getCalendar();

			return calendar.isEnableComments();
		}
		catch (Exception exception) {
			_log.error("Unable to check commentable", exception);
		}

		return false;
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	@Override
	public boolean isRatable() {
		try {
			Calendar calendar = _calendarBooking.getCalendar();

			return calendar.isEnableRatings();
		}
		catch (Exception exception) {
			_log.error("Unable to check ratable", exception);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarBookingAssetRenderer.class);

	private final CalendarBooking _calendarBooking;
	private final ModelResourcePermission<Calendar>
		_calendarModelResourcePermission;

}