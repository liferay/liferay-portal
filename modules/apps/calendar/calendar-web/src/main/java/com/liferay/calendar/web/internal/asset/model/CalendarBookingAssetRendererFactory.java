/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.calendar.constants.CalendarActionKeys;
import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.web.internal.util.CalendarResourceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fabio Pezzutto
 * @author Eduardo Lundgren
 */
@Component(
	property = "jakarta.portlet.name=" + CalendarPortletKeys.CALENDAR,
	service = AssetRendererFactory.class
)
public class CalendarBookingAssetRendererFactory
	extends BaseAssetRendererFactory<CalendarBooking> {

	public static final String TYPE = "calendar";

	public CalendarBookingAssetRendererFactory() {
		setLinkable(true);
		setClassName(CalendarBooking.class.getName());
		setPortletId(CalendarPortletKeys.CALENDAR);
		setSearchable(true);
	}

	@Override
	public AssetRenderer<CalendarBooking> getAssetRenderer(
			long classPK, int type)
		throws PortalException {

		CalendarBookingAssetRenderer calendarBookingAssetRenderer =
			new CalendarBookingAssetRenderer(
				_calendarBookingLocalService.getCalendarBooking(classPK),
				_calendarModelResourcePermission);

		calendarBookingAssetRenderer.setAssetRendererType(type);
		calendarBookingAssetRenderer.setServletContext(_servletContext);

		return calendarBookingAssetRenderer;
	}

	@Override
	public String getClassName() {
		return CalendarBooking.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "calendar";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLAdd(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classTypeId)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		CalendarResource calendarResource =
			CalendarResourceUtil.getScopeGroupCalendarResource(
				liferayPortletRequest, themeDisplay.getScopeGroupId());

		if (calendarResource == null) {
			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				liferayPortletRequest, getGroup(liferayPortletRequest),
				CalendarPortletKeys.CALENDAR, 0, 0, PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_calendar_booking.jsp"
		).setParameter(
			"calendarId",
			() -> {
				Calendar calendar = calendarResource.getDefaultCalendar();

				return calendar.getCalendarId();
			}
		).buildPortletURL();
	}

	@Override
	public boolean hasAddPermission(
			PermissionChecker permissionChecker, long groupId, long classTypeId)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(permissionChecker.getCompanyId());

		CalendarResource calendarResource =
			CalendarResourceUtil.getScopeGroupCalendarResource(
				groupId, serviceContext);

		if (calendarResource == null) {
			return false;
		}

		Calendar calendar = calendarResource.getDefaultCalendar();

		return _calendarModelResourcePermission.contains(
			permissionChecker, calendar.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		CalendarBooking calendarBooking =
			_calendarBookingLocalService.getCalendarBooking(classPK);

		if (actionId.equals(ActionKeys.DELETE) ||
			actionId.equals(ActionKeys.UPDATE)) {

			actionId = CalendarActionKeys.MANAGE_BOOKINGS;
		}

		return _calendarModelResourcePermission.contains(
			permissionChecker, calendarBooking.getCalendarId(), actionId);
	}

	@Reference
	private CalendarBookingLocalService _calendarBookingLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.calendar.model.Calendar)"
	)
	private ModelResourcePermission<Calendar> _calendarModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.calendar.web)")
	private ServletContext _servletContext;

}