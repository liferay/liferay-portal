/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.portlet;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.calendar.constants.CalendarBookingConstants;
import com.liferay.calendar.constants.CalendarNotificationTemplateConstants;
import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.exception.CalendarBookingDurationException;
import com.liferay.calendar.exception.CalendarBookingRecurrenceException;
import com.liferay.calendar.exception.CalendarNameException;
import com.liferay.calendar.exception.CalendarResourceCodeException;
import com.liferay.calendar.exception.CalendarResourceNameException;
import com.liferay.calendar.exception.DuplicateCalendarResourceException;
import com.liferay.calendar.exception.NoSuchResourceException;
import com.liferay.calendar.exporter.CalendarDataFormat;
import com.liferay.calendar.exporter.CalendarDataHandler;
import com.liferay.calendar.exporter.CalendarDataHandlerFactory;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarNotificationTemplate;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.notification.NotificationTemplateType;
import com.liferay.calendar.notification.NotificationType;
import com.liferay.calendar.recurrence.Frequency;
import com.liferay.calendar.recurrence.PositionalWeekday;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.RecurrenceSerializer;
import com.liferay.calendar.recurrence.Weekday;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.service.CalendarBookingService;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.CalendarNotificationTemplateService;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.calendar.service.CalendarResourceService;
import com.liferay.calendar.service.CalendarService;
import com.liferay.calendar.util.JCalendarUtil;
import com.liferay.calendar.util.RecurrenceUtil;
import com.liferay.calendar.util.comparator.CalendarBookingStartTimeComparator;
import com.liferay.calendar.web.internal.constants.CalendarWebKeys;
import com.liferay.calendar.web.internal.display.context.CalendarDisplayContext;
import com.liferay.calendar.web.internal.util.CalendarResourceUtil;
import com.liferay.calendar.web.internal.util.CalendarUtil;
import com.liferay.calendar.workflow.constants.CalendarBookingWorkflowConstants;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.rss.util.RSSUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 * @author Fabio Pezzutto
 * @author Andrea Di Giorgi
 * @author Marcellus Tavares
 * @author Bruno Basto
 * @author Pier Paolo Ramon
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=calendar-portlet",
		"com.liferay.portlet.display-category=category.collaboration",
		"com.liferay.portlet.friendly-url-mapping=calendar",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/calendar.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"jakarta.portlet.display-name=Calendar",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + CalendarPortletKeys.CALENDAR,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CalendarPortlet extends MVCPortlet {

	public void deleteCalendar(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long calendarId = ParamUtil.getLong(actionRequest, "calendarId");

		_calendarService.deleteCalendar(calendarId);
	}

	public void deleteCalendarResource(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long calendarResourceId = ParamUtil.getLong(
			actionRequest, "calendarResourceId");

		_calendarResourceService.deleteCalendarResource(calendarResourceId);
	}

	public void importCalendar(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String data = _file.read(uploadPortletRequest.getFile("file"));

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		if (Validator.isNotNull(data)) {
			long calendarId = ParamUtil.getLong(
				uploadPortletRequest, "calendarId");

			try {
				CalendarDataHandler calendarDataHandler =
					CalendarDataHandlerFactory.getCalendarDataHandler(
						CalendarDataFormat.ICAL);

				calendarDataHandler.importCalendar(calendarId, data);

				jsonObject.put("success", true);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				String message = themeDisplay.translate(
					"an-unexpected-error-occurred-while-importing-your-file");

				jsonObject.put(
					"error", message
				).put(
					"success", false
				);
			}
		}
		else {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", themeDisplay.getLocale(), getClass());

			String message = ResourceBundleUtil.getString(
				resourceBundle, "failed-to-import-empty-file");

			jsonObject.put(
				"error", message
			).put(
				"success", false
			);
		}

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	public void invokeTransition(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long calendarBookingId = ParamUtil.getLong(
			actionRequest, "calendarBookingId");

		long startTime = ParamUtil.getLong(actionRequest, "startTime");
		int status = ParamUtil.getInteger(actionRequest, "status");
		boolean updateInstance = ParamUtil.getBoolean(
			actionRequest, "updateInstance");
		boolean allFollowing = ParamUtil.getBoolean(
			actionRequest, "allFollowing");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CalendarBooking.class.getName(), actionRequest);

		CalendarBooking calendarBooking =
			_calendarBookingService.invokeTransition(
				calendarBookingId, startTime, status, updateInstance,
				allFollowing, serviceContext);

		String redirect = getRedirect(actionRequest, actionResponse);

		if (calendarBooking.getCalendarBookingId() != calendarBookingId) {
			redirect = _getViewCalendarBookingURL(
				actionRequest, calendarBooking);
		}

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	public void moveCalendarBookingToTrash(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long calendarBookingId = ParamUtil.getLong(
			actionRequest, "calendarBookingId");

		_calendarBookingService.moveCalendarBookingToTrash(calendarBookingId);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			_getCalendar(renderRequest);
			_getCalendarBooking(renderRequest);
			_getCalendarResource(renderRequest);
			_setRenderRequestAttributes(renderRequest, renderResponse);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchResourceException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());
			}
			else {
				throw new PortletException(exception);
			}
		}

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			String resourceID = resourceRequest.getResourceID();

			if (resourceID.equals("calendar")) {
				_serveCalendar(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("calendarBookingInvitees")) {
				_serveCalendarBookingInvitees(
					resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("calendarBookings")) {
				_serveCalendarBookings(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("calendarBookingsRSS")) {
				_serveCalendarBookingsRSS(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("calendarRenderingRules")) {
				_serveCalendarRenderingRules(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("calendarResources")) {
				_serveCalendarResources(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("currentTime")) {
				_serveCurrentTime(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("exportCalendar")) {
				_serveExportCalendar(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("hasExclusiveCalendarBooking")) {
				_serveHasExclusiveCalendarBooking(
					resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("resourceCalendars")) {
				_serveResourceCalendars(resourceRequest, resourceResponse);
			}
			else {
				_serveUnknownResource(resourceRequest, resourceResponse);
			}
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	public void updateCalendar(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long calendarId = ParamUtil.getLong(actionRequest, "calendarId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		String timeZoneId = ParamUtil.getString(actionRequest, "timeZoneId");
		int color = ParamUtil.getInteger(actionRequest, "color");
		boolean defaultCalendar = ParamUtil.getBoolean(
			actionRequest, "defaultCalendar");
		boolean enableComments = ParamUtil.getBoolean(
			actionRequest, "enableComments");
		boolean enableRatings = ParamUtil.getBoolean(
			actionRequest, "enableRatings");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Calendar.class.getName(), actionRequest);

		Calendar calendar = null;

		if (calendarId <= 0) {
			long calendarResourceId = ParamUtil.getLong(
				actionRequest, "calendarResourceId");

			CalendarResource calendarResource =
				_calendarResourceService.getCalendarResource(
					calendarResourceId);

			calendar = _calendarService.addCalendar(
				calendarResource.getGroupId(), calendarResourceId, nameMap,
				descriptionMap, timeZoneId, color, defaultCalendar,
				enableComments, enableRatings, serviceContext);
		}
		else {
			calendar = _calendarService.updateCalendar(
				calendarId, nameMap, descriptionMap, timeZoneId, color,
				defaultCalendar, enableComments, enableRatings, serviceContext);
		}

		String redirect = _getEditCalendarURL(
			actionRequest, actionResponse, calendar);

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	public void updateCalendarNotificationTemplate(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long calendarNotificationTemplateId = ParamUtil.getLong(
			actionRequest, "calendarNotificationTemplateId");

		NotificationType notificationType = NotificationType.parse(
			ParamUtil.getString(actionRequest, "notificationType"));
		String subject = ParamUtil.getString(actionRequest, "subject");
		String body = ParamUtil.getString(actionRequest, "body");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CalendarNotificationTemplate.class.getName(), actionRequest);

		if (calendarNotificationTemplateId <= 0) {
			long calendarId = ParamUtil.getLong(actionRequest, "calendarId");
			NotificationTemplateType notificationTemplateType =
				NotificationTemplateType.parse(
					ParamUtil.getString(
						actionRequest, "notificationTemplateType"));

			_calendarNotificationTemplateService.
				addCalendarNotificationTemplate(
					calendarId, notificationType,
					_getNotificationTypeSettings(
						actionRequest, notificationType),
					notificationTemplateType, subject, body, serviceContext);
		}
		else {
			_calendarNotificationTemplateService.
				updateCalendarNotificationTemplate(
					calendarNotificationTemplateId,
					_getNotificationTypeSettings(
						actionRequest, notificationType),
					subject, body, serviceContext);
		}
	}

	public void updateCalendarResource(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long calendarResourceId = ParamUtil.getLong(
			actionRequest, "calendarResourceId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CalendarResource.class.getName(), actionRequest);

		if (calendarResourceId <= 0) {
			String code = ParamUtil.getString(actionRequest, "code");

			_calendarResourceService.addCalendarResource(
				serviceContext.getScopeGroupId(),
				_portal.getClassNameId(CalendarResource.class), 0,
				PortalUUIDUtil.generate(), code, nameMap, descriptionMap,
				active, serviceContext);
		}
		else {
			_calendarResourceService.updateCalendarResource(
				calendarResourceId, nameMap, descriptionMap, active,
				serviceContext);

			long defaultCalendarId = ParamUtil.getLong(
				actionRequest, "defaultCalendarId");

			if (defaultCalendarId > 0) {
				_calendarLocalService.updateCalendar(defaultCalendarId, true);
			}
		}
	}

	public void updateFormCalendarBooking(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long calendarBookingId = ParamUtil.getLong(
			actionRequest, "calendarBookingId");

		long calendarId = ParamUtil.getLong(actionRequest, "calendarId");

		Calendar calendar = _calendarService.getCalendar(calendarId);

		long[] childCalendarIds = ParamUtil.getLongValues(
			actionRequest, "childCalendarIds");
		long[] reinvitableCalendarIds = ParamUtil.getLongValues(
			actionRequest, "reinvitableCalendarIds");
		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		String location = ParamUtil.getString(actionRequest, "location");
		java.util.Calendar startTimeJCalendar = _getJCalendar(
			actionRequest, "startTime");
		java.util.Calendar endTimeJCalendar = _getJCalendar(
			actionRequest, "endTime");
		boolean allDay = ParamUtil.getBoolean(actionRequest, "allDay");
		Recurrence recurrence = _getRecurrence(actionRequest);
		long[] reminders = _getReminders(actionRequest);
		String[] remindersType = _getRemindersType(actionRequest);
		int instanceIndex = ParamUtil.getInteger(
			actionRequest, "instanceIndex");
		boolean updateCalendarBookingInstance = ParamUtil.getBoolean(
			actionRequest, "updateCalendarBookingInstance");
		boolean allFollowing = ParamUtil.getBoolean(
			actionRequest, "allFollowing");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CalendarBooking.class.getName(), actionRequest);

		CalendarBooking calendarBooking = _updateCalendarBooking(
			calendarBookingId, calendar, childCalendarIds,
			reinvitableCalendarIds, titleMap, descriptionMap, location,
			startTimeJCalendar.getTimeInMillis(),
			endTimeJCalendar.getTimeInMillis(), allDay, recurrence, reminders,
			remindersType, instanceIndex, updateCalendarBookingInstance,
			allFollowing, serviceContext);

		String redirect = getRedirect(actionRequest, actionResponse);

		int workflowAction = ParamUtil.getInteger(
			actionRequest, "workflowAction",
			WorkflowConstants.ACTION_SAVE_DRAFT);

		if ((calendarBooking != null) &&
			(workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT)) {

			redirect = _getSaveAndContinueRedirect(
				actionRequest, calendarBooking, redirect);
		}
		else {
			redirect = HttpComponentsUtil.setParameter(
				redirect, actionResponse.getNamespace() + "calendarBookingId",
				calendarBooking.getCalendarBookingId());
		}

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	public void updateSchedulerCalendarBooking(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long calendarBookingId = ParamUtil.getLong(
			actionRequest, "calendarBookingId");

		CalendarBooking calendarBooking =
			_calendarBookingService.fetchCalendarBooking(calendarBookingId);

		long calendarId = ParamUtil.getLong(actionRequest, "calendarId");

		Calendar calendar = _calendarService.getCalendar(calendarId);

		long[] childCalendarIds = {};
		Map<Locale, String> titleMap = new HashMap<>();
		Map<Locale, String> descriptionMap = new HashMap<>();
		String location = null;
		java.util.Calendar startTimeJCalendar = _getJCalendar(
			actionRequest, "startTime");
		java.util.Calendar endTimeJCalendar = _getJCalendar(
			actionRequest, "endTime");
		boolean allDay = ParamUtil.getBoolean(actionRequest, "allDay");

		TimeZone timeZone = _getTimeZone(actionRequest);

		Recurrence recurrence = RecurrenceSerializer.deserialize(
			ParamUtil.getString(actionRequest, "recurrence"), timeZone);

		long[] reminders = {0, 0};
		String[] remindersType = {"email", "email"};
		int instanceIndex = ParamUtil.getInteger(
			actionRequest, "instanceIndex");
		boolean updateInstance = ParamUtil.getBoolean(
			actionRequest, "updateInstance");
		boolean allFollowing = ParamUtil.getBoolean(
			actionRequest, "allFollowing");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CalendarBooking.class.getName(), actionRequest);

		if (calendarBooking != null) {
			childCalendarIds = _calendarBookingLocalService.getChildCalendarIds(
				calendarBookingId, calendarId);
			titleMap = calendarBooking.getTitleMap();
			descriptionMap = calendarBooking.getDescriptionMap();
			location = calendarBooking.getLocation();
			reminders = new long[] {
				calendarBooking.getFirstReminder(),
				calendarBooking.getSecondReminder()
			};
			remindersType = new String[] {
				calendarBooking.getFirstReminderType(),
				calendarBooking.getSecondReminderType()
			};

			_addAssetEntry(calendarBooking, serviceContext);
		}

		String title = ParamUtil.getString(actionRequest, "title");

		titleMap.put(LocaleUtil.getSiteDefault(), title);

		JSONObject jsonObject = null;

		try {
			calendarBooking = _updateCalendarBooking(
				calendarBookingId, calendar, childCalendarIds, new long[0],
				titleMap, descriptionMap, location,
				startTimeJCalendar.getTimeInMillis(),
				endTimeJCalendar.getTimeInMillis(), allDay, recurrence,
				reminders, remindersType, instanceIndex, updateInstance,
				allFollowing, serviceContext);

			jsonObject = CalendarUtil.toCalendarBookingJSONObject(
				themeDisplay, calendarBooking, timeZone);
		}
		catch (PortalException portalException) {
			String errorMessage = "";

			if (portalException instanceof AssetCategoryException) {
				AssetCategoryException assetCategoryException =
					(AssetCategoryException)portalException;

				errorMessage = _getErrorMessageForException(
					assetCategoryException, themeDisplay);
			}

			jsonObject = JSONUtil.put("exception", errorMessage);
		}

		hideDefaultSuccessMessage(actionRequest);

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (SessionErrors.contains(
				renderRequest, NoSuchResourceException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof AssetCategoryException ||
			throwable instanceof AssetTagException ||
			throwable instanceof CalendarBookingDurationException ||
			throwable instanceof CalendarBookingRecurrenceException ||
			throwable instanceof CalendarNameException ||
			throwable instanceof CalendarResourceCodeException ||
			throwable instanceof CalendarResourceNameException ||
			throwable instanceof DuplicateCalendarResourceException ||
			throwable instanceof PrincipalException) {

			return true;
		}

		return false;
	}

	private void _addAssetEntry(
		CalendarBooking calendarBooking, ServiceContext serviceContext) {

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			CalendarBooking.class.getName(),
			calendarBooking.getCalendarBookingId());

		if (assetEntry != null) {
			serviceContext.setAssetCategoryIds(assetEntry.getCategoryIds());
			serviceContext.setAssetLinkEntryIds(
				ListUtil.toLongArray(
					_assetLinkLocalService.getDirectLinks(
						assetEntry.getEntryId()),
					AssetLink.ENTRY_ID2_ACCESSOR));
			serviceContext.setAssetPriority(assetEntry.getPriority());
			serviceContext.setAssetTagNames(assetEntry.getTagNames());
		}

		ExpandoBridge expandoBridge = calendarBooking.getExpandoBridge();

		if (expandoBridge != null) {
			serviceContext.setExpandoBridgeAttributes(
				expandoBridge.getAttributes());
		}
	}

	private void _addCalendar(
			PortletRequest portletRequest, Set<Calendar> calendarsSet,
			long classNameId, long classPK)
		throws Exception {

		CalendarResource calendarResource =
			CalendarResourceUtil.getCalendarResource(
				portletRequest, classNameId, classPK);

		if (calendarResource == null) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		List<Calendar> calendars =
			_calendarLocalService.getCalendarResourceCalendars(
				calendarResource.getGroupId(),
				calendarResource.getCalendarResourceId());

		for (Calendar calendar : calendars) {
			if (!_calendarModelResourcePermission.contains(
					permissionChecker, calendar, ActionKeys.VIEW)) {

				continue;
			}

			calendarsSet.add(calendar);
		}
	}

	private Set<Long> _filterReinvitedCalendarIds(
		long[] childCalendarIds, long[] reinvitableCalendarIds) {

		Set<Long> reinvitedCalendarIds = new HashSet<>();

		for (long childCalendarId : childCalendarIds) {
			if (ArrayUtil.contains(reinvitableCalendarIds, childCalendarId)) {
				reinvitedCalendarIds.add(childCalendarId);
			}
		}

		return reinvitedCalendarIds;
	}

	private void _getCalendar(PortletRequest portletRequest) throws Exception {
		long calendarId = ParamUtil.getLong(portletRequest, "calendarId");

		if (calendarId <= 0) {
			return;
		}

		portletRequest.setAttribute(
			CalendarWebKeys.CALENDAR, _calendarService.getCalendar(calendarId));
	}

	private void _getCalendarBooking(PortletRequest portletRequest)
		throws Exception {

		if (portletRequest.getAttribute(CalendarWebKeys.CALENDAR_BOOKING) !=
				null) {

			return;
		}

		long calendarBookingId = ParamUtil.getLong(
			portletRequest, "calendarBookingId");

		if (calendarBookingId <= 0) {
			return;
		}

		portletRequest.setAttribute(
			CalendarWebKeys.CALENDAR_BOOKING,
			_calendarBookingService.getCalendarBooking(calendarBookingId));
	}

	private void _getCalendarResource(PortletRequest portletRequest)
		throws Exception {

		long calendarResourceId = ParamUtil.getLong(
			portletRequest, "calendarResourceId");

		long classNameId = ParamUtil.getLong(portletRequest, "classNameId");
		long classPK = ParamUtil.getLong(portletRequest, "classPK");

		CalendarResource calendarResource = null;

		if (calendarResourceId > 0) {
			calendarResource = _calendarResourceService.getCalendarResource(
				calendarResourceId);
		}
		else if ((classNameId > 0) && (classPK > 0)) {
			calendarResource = CalendarResourceUtil.getCalendarResource(
				portletRequest, classNameId, classPK);
		}

		portletRequest.setAttribute(
			CalendarWebKeys.CALENDAR_RESOURCE, calendarResource);
	}

	private List<Integer> _getDaysOfWeek(Recurrence recurrenceObj) {
		List<Integer> daysOfWeek = new ArrayList<>();

		List<PositionalWeekday> positionalWeekdays =
			recurrenceObj.getPositionalWeekdays();

		if (positionalWeekdays != null) {
			for (PositionalWeekday positionalWeekday : positionalWeekdays) {
				Weekday weekday = positionalWeekday.getWeekday();

				daysOfWeek.add(weekday.getCalendarWeekday());
			}
		}

		return daysOfWeek;
	}

	private String _getEditCalendarURL(
			ActionRequest actionRequest, ActionResponse actionResponse,
			Calendar calendar)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String editCalendarURL = getRedirect(actionRequest, actionResponse);

		if (Validator.isNull(editCalendarURL)) {
			editCalendarURL = _portal.getLayoutFullURL(themeDisplay);
		}

		String namespace = actionResponse.getNamespace();

		editCalendarURL = HttpComponentsUtil.setParameter(
			editCalendarURL, "p_p_id", themeDisplay.getPpid());
		editCalendarURL = HttpComponentsUtil.setParameter(
			editCalendarURL, namespace + "mvcPath",
			templatePath + "edit_calendar.jsp");
		editCalendarURL = HttpComponentsUtil.setParameter(
			editCalendarURL, namespace + "redirect",
			getRedirect(actionRequest, actionResponse));
		editCalendarURL = HttpComponentsUtil.setParameter(
			editCalendarURL, namespace + "backURL",
			ParamUtil.getString(actionRequest, "backURL"));
		editCalendarURL = HttpComponentsUtil.setParameter(
			editCalendarURL, namespace + "calendarId",
			calendar.getCalendarId());

		return editCalendarURL;
	}

	private String _getErrorMessageForException(
		AssetCategoryException assetCategoryException,
		ThemeDisplay themeDisplay) {

		String errorMessage = "";

		AssetVocabulary assetVocabulary =
			assetCategoryException.getVocabulary();

		String vocabularyTitle = StringPool.BLANK;

		if (assetVocabulary != null) {
			vocabularyTitle = assetVocabulary.getTitle(
				themeDisplay.getLocale());
		}

		if (assetCategoryException.getType() ==
				AssetCategoryException.AT_LEAST_ONE_CATEGORY) {

			errorMessage = themeDisplay.translate(
				"please-select-at-least-one-category-for-x", vocabularyTitle);
		}
		else if (assetCategoryException.getType() ==
					AssetCategoryException.TOO_MANY_CATEGORIES) {

			errorMessage = themeDisplay.translate(
				"you-cannot-select-more-than-one-category-for-x",
				vocabularyTitle);
		}

		return errorMessage;
	}

	private CalendarBooking _getFirstCalendarBookingInstance(
		CalendarBooking calendarBooking, Recurrence recurrenceObj,
		TimeZone timeZone) {

		if (recurrenceObj == null) {
			return calendarBooking;
		}

		List<Integer> daysOfWeek = _getDaysOfWeek(recurrenceObj);

		java.util.Calendar startTimeJCalendar = CalendarFactoryUtil.getCalendar(
			calendarBooking.getStartTime(), timeZone);

		int startTimeDayOfWeek = startTimeJCalendar.get(
			java.util.Calendar.DAY_OF_WEEK);

		if ((recurrenceObj.getFrequency() == Frequency.WEEKLY) &&
			!daysOfWeek.contains(startTimeDayOfWeek)) {

			java.util.Calendar firstDayJCalendar = JCalendarUtil.getJCalendar(
				calendarBooking.getStartTime(), timeZone);

			long startTime = firstDayJCalendar.getTimeInMillis();

			long endTime = startTime + calendarBooking.getDuration();

			calendarBooking.setStartTime(startTime);

			calendarBooking.setEndTime(endTime);
			calendarBooking.setRecurrence(
				RecurrenceSerializer.serialize(recurrenceObj));

			calendarBooking = RecurrenceUtil.getCalendarBookingInstance(
				calendarBooking, 1);
		}

		return calendarBooking;
	}

	private java.util.Calendar _getJCalendar(
		PortletRequest portletRequest, String name) {

		int hour = ParamUtil.getInteger(portletRequest, name + "Hour");

		if (ParamUtil.getInteger(portletRequest, name + "AmPm") ==
				java.util.Calendar.PM) {

			hour += 12;
		}

		TimeZone timeZone = ParamUtil.getBoolean(portletRequest, "allDay") ?
			TimeZoneUtil.getTimeZone(StringPool.UTC) :
				_getTimeZone(portletRequest);

		return JCalendarUtil.getJCalendar(
			ParamUtil.getInteger(portletRequest, name + "Year"),
			ParamUtil.getInteger(portletRequest, name + "Month"),
			ParamUtil.getInteger(portletRequest, name + "Day"), hour,
			ParamUtil.getInteger(portletRequest, name + "Minute"), 0, 0,
			timeZone);
	}

	private String _getNotificationTypeSettings(
		ActionRequest actionRequest, NotificationType notificationType) {

		UnicodeProperties notificationTypeSettingsUnicodeProperties =
			new UnicodeProperties(true);

		if (notificationType == NotificationType.EMAIL) {
			String fromAddress = ParamUtil.getString(
				actionRequest, "fromAddress");
			String fromName = ParamUtil.getString(actionRequest, "fromName");

			notificationTypeSettingsUnicodeProperties.put(
				CalendarNotificationTemplateConstants.PROPERTY_FROM_ADDRESS,
				fromAddress);
			notificationTypeSettingsUnicodeProperties.put(
				CalendarNotificationTemplateConstants.PROPERTY_FROM_NAME,
				fromName);
		}

		return notificationTypeSettingsUnicodeProperties.toString();
	}

	private long _getOffset(
			CalendarBooking editedCalendarBookingInstance, long newStartTime,
			Recurrence recurrence)
		throws PortalException {

		Frequency frequency = null;

		if (recurrence != null) {
			frequency = recurrence.getFrequency();
		}

		long oldStartTime = editedCalendarBookingInstance.getStartTime();

		if (frequency == Frequency.WEEKLY) {
			CalendarBooking firstInstance =
				_calendarBookingService.getCalendarBookingInstance(
					editedCalendarBookingInstance.getCalendarBookingId(), 0);

			TimeZone timeZone = editedCalendarBookingInstance.getTimeZone();

			java.util.Calendar oldStartTimeJCalendar =
				CalendarFactoryUtil.getCalendar(oldStartTime, timeZone);

			java.util.Calendar firstInstanceJCalendar =
				CalendarFactoryUtil.getCalendar(
					firstInstance.getStartTime(), timeZone);

			if (!JCalendarUtil.isSameDayOfWeek(
					oldStartTimeJCalendar, firstInstanceJCalendar)) {

				java.util.Calendar newStartTimeJCalendar =
					CalendarFactoryUtil.getCalendar(newStartTime, timeZone);

				newStartTimeJCalendar = JCalendarUtil.mergeJCalendar(
					oldStartTimeJCalendar, newStartTimeJCalendar, timeZone);

				newStartTime = newStartTimeJCalendar.getTimeInMillis();
			}
		}

		return newStartTime - oldStartTime;
	}

	private Recurrence _getRecurrence(ActionRequest actionRequest) {
		boolean repeat = ParamUtil.getBoolean(actionRequest, "repeat");

		if (!repeat) {
			return null;
		}

		Recurrence recurrence = new Recurrence();

		int count = 0;

		String ends = ParamUtil.getString(actionRequest, "ends");

		if (ends.equals("after")) {
			count = ParamUtil.getInteger(actionRequest, "count");
		}

		recurrence.setCount(count);

		Frequency frequency = Frequency.parse(
			ParamUtil.getString(actionRequest, "frequency"));

		recurrence.setFrequency(frequency);

		recurrence.setInterval(ParamUtil.getInteger(actionRequest, "interval"));

		TimeZone timeZone = _getTimeZone(actionRequest);

		recurrence.setTimeZone(timeZone);

		if (ends.equals("on")) {
			java.util.Calendar untilJCalendar = _getJCalendar(
				actionRequest, "untilDate");

			java.util.Calendar startTimeJCalendar = _getJCalendar(
				actionRequest, "startTime");

			recurrence.setUntilJCalendar(
				JCalendarUtil.mergeJCalendar(
					untilJCalendar, startTimeJCalendar, timeZone));
		}

		List<PositionalWeekday> positionalWeekdays = new ArrayList<>();

		if (frequency == Frequency.WEEKLY) {
			String[] weekdayValues = ParamUtil.getParameterValues(
				actionRequest, "weekdays");

			for (String weekdayValue : weekdayValues) {
				Weekday weekday = Weekday.parse(weekdayValue);

				java.util.Calendar startTimeJCalendar = _getJCalendar(
					actionRequest, "startTime");

				java.util.Calendar weekdayJCalendar =
					JCalendarUtil.getJCalendar(
						startTimeJCalendar.getTimeInMillis(), timeZone);

				weekdayJCalendar.set(
					java.util.Calendar.DAY_OF_WEEK,
					weekday.getCalendarWeekday());

				weekday = Weekday.getWeekday(weekdayJCalendar);

				positionalWeekdays.add(new PositionalWeekday(weekday, 0));
			}
		}
		else if ((frequency == Frequency.MONTHLY) ||
				 (frequency == Frequency.YEARLY)) {

			boolean repeatOnWeekday = ParamUtil.getBoolean(
				actionRequest, "repeatOnWeekday");

			if (repeatOnWeekday) {
				int position = ParamUtil.getInteger(actionRequest, "position");

				Weekday weekday = Weekday.parse(
					ParamUtil.getString(actionRequest, "weekday"));

				positionalWeekdays.add(
					new PositionalWeekday(weekday, position));

				if (frequency == Frequency.YEARLY) {
					recurrence.setMonths(
						Arrays.asList(
							ParamUtil.getInteger(
								actionRequest, "startTimeMonth")));
				}
			}
		}

		recurrence.setPositionalWeekdays(positionalWeekdays);

		String[] exceptionDates = StringUtil.split(
			ParamUtil.getString(actionRequest, "exceptionDates"));

		for (String exceptionDate : exceptionDates) {
			recurrence.addExceptionJCalendar(
				JCalendarUtil.getJCalendar(Long.valueOf(exceptionDate)));
		}

		return recurrence;
	}

	private long[] _getReminders(PortletRequest portletRequest) {
		long firstReminder = ParamUtil.getInteger(
			portletRequest, "reminderValue0");
		long firstReminderDuration = ParamUtil.getInteger(
			portletRequest, "reminderDuration0");
		long secondReminder = ParamUtil.getInteger(
			portletRequest, "reminderValue1");
		long secondReminderDuration = ParamUtil.getInteger(
			portletRequest, "reminderDuration1");

		return new long[] {
			firstReminder * firstReminderDuration * Time.SECOND,
			secondReminder * secondReminderDuration * Time.SECOND
		};
	}

	private String[] _getRemindersType(PortletRequest portletRequest) {
		String firstReminderType = ParamUtil.getString(
			portletRequest, "reminderType0");
		String secondReminderType = ParamUtil.getString(
			portletRequest, "reminderType1");

		return new String[] {firstReminderType, secondReminderType};
	}

	private String _getSaveAndContinueRedirect(
			ActionRequest actionRequest, CalendarBooking calendarBooking,
			String redirect)
		throws Exception {

		PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG);

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, portletConfig.getPortletName(),
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter("mvcPath", "/edit_calendar_booking.jsp");
		portletURL.setParameter("redirect", redirect, false);
		portletURL.setParameter(
			"groupId", String.valueOf(calendarBooking.getGroupId()), false);
		portletURL.setParameter(
			"calendarBookingId",
			String.valueOf(calendarBooking.getCalendarBookingId()), false);
		portletURL.setWindowState(actionRequest.getWindowState());

		return portletURL.toString();
	}

	private TimeZone _getTimeZone(PortletRequest portletRequest) {
		PortletPreferences portletPreferences = portletRequest.getPreferences();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		String timeZoneId = portletPreferences.getValue(
			"timeZoneId", user.getTimeZoneId());

		if (Validator.isNull(timeZoneId)) {
			timeZoneId = user.getTimeZoneId();
		}

		return TimeZone.getTimeZone(timeZoneId);
	}

	private String _getViewCalendarBookingURL(
			ActionRequest actionRequest, CalendarBooking calendarBooking)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				actionRequest, themeDisplay.getPpid(), themeDisplay.getPlid(),
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/view_calendar_booking.jsp"
		).setParameter(
			"calendarBookingId", calendarBooking.getCalendarBookingId()
		).setParameter(
			"instanceIndex", "0"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private Hits _search(ThemeDisplay themeDisplay, String keywords)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		keywords = StringUtil.toLowerCase(keywords);

		searchContext.setAttribute(
			_localization.getLocalizedName(
				Field.NAME, searchContext.getLanguageId()),
			keywords);
		searchContext.setAttribute(
			_localization.getLocalizedName(
				"resourceName", searchContext.getLanguageId()),
			keywords);

		searchContext.setCompanyId(themeDisplay.getCompanyId());
		searchContext.setEnd(SearchContainer.DEFAULT_DELTA);
		searchContext.setGroupIds(new long[0]);

		Group group = themeDisplay.getScopeGroup();

		searchContext.setIncludeStagingGroups(group.isStagingGroup());

		searchContext.setStart(0);
		searchContext.setUserId(themeDisplay.getUserId());

		return _baseSearcher.search(searchContext);
	}

	private void _serveCalendar(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long calendarId = ParamUtil.getLong(resourceRequest, "calendarId");

		JSONObject jsonObject = CalendarUtil.toCalendarJSONObject(
			themeDisplay, _calendarService.getCalendar(calendarId));

		writeJSON(resourceRequest, resourceResponse, jsonObject);
	}

	private void _serveCalendarBookingInvitees(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long parentCalendarBookingId = ParamUtil.getLong(
			resourceRequest, "parentCalendarBookingId");

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		Group group = themeDisplay.getScopeGroup();

		Collection<CalendarResource> calendarResources =
			CalendarUtil.getCalendarResources(
				_calendarBookingService.getChildCalendarBookings(
					parentCalendarBookingId, group.isStagingGroup()));

		for (CalendarResource calendarResource : calendarResources) {
			JSONObject jsonObject = CalendarUtil.toCalendarResourceJSONObject(
				themeDisplay, calendarResource);

			jsonArray.put(jsonObject);
		}

		writeJSON(resourceRequest, resourceResponse, jsonArray);
	}

	private void _serveCalendarBookings(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<CalendarBooking> calendarBookings =
			Collections.<CalendarBooking>emptyList();

		long[] calendarIds = ParamUtil.getLongValues(
			resourceRequest, "calendarIds");
		TimeZone timeZone = _getTimeZone(resourceRequest);

		if (ArrayUtil.isNotEmpty(calendarIds)) {
			java.util.Calendar endTimeJCalendar = _getJCalendar(
				resourceRequest, "endTime");
			java.util.Calendar startTimeJCalendar = _getJCalendar(
				resourceRequest, "startTime");
			int[] statuses = ParamUtil.getIntegerValues(
				resourceRequest, "statuses");

			calendarBookings = _calendarBookingService.search(
				themeDisplay.getCompanyId(), new long[0], calendarIds,
				new long[0], -1, null, startTimeJCalendar.getTimeInMillis(),
				endTimeJCalendar.getTimeInMillis(), timeZone, true, statuses,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				CalendarBookingStartTimeComparator.getInstance(true));

			int eventsPerPage = ParamUtil.getInteger(
				resourceRequest, "eventsPerPage");

			if ((eventsPerPage > 0) &&
				(eventsPerPage < calendarBookings.size())) {

				calendarBookings = calendarBookings.subList(0, eventsPerPage);
			}
		}

		JSONArray jsonArray = CalendarUtil.toCalendarBookingsJSONArray(
			themeDisplay, calendarBookings, timeZone);

		writeJSON(resourceRequest, resourceResponse, jsonArray);
	}

	private void _serveCalendarBookingsRSS(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		PortletPreferences portletPreferences =
			resourceRequest.getPreferences();

		boolean enableRss = GetterUtil.getBoolean(
			portletPreferences.getValue("enableRss", null), true);

		if (!_portal.isRSSFeedsEnabled() || !enableRss) {
			_portal.sendRSSFeedsDisabledError(
				resourceRequest, resourceResponse);

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long calendarId = ParamUtil.getLong(resourceRequest, "calendarId");

		long timeInterval = GetterUtil.getLong(
			portletPreferences.getValue("rssTimeInterval", StringPool.BLANK),
			Time.WEEK);

		long startTime = System.currentTimeMillis();

		long endTime = startTime + timeInterval;

		int max = GetterUtil.getInteger(
			portletPreferences.getValue("rssDelta", StringPool.BLANK),
			SearchContainer.DEFAULT_DELTA);

		String rssFeedType = portletPreferences.getValue(
			"rssFeedType", RSSUtil.FORMAT_DEFAULT);

		String type = RSSUtil.getFormatType(rssFeedType);
		double version = RSSUtil.getFeedTypeVersion(rssFeedType);

		String displayStyle = portletPreferences.getValue(
			"rssDisplayStyle", RSSUtil.DISPLAY_STYLE_DEFAULT);

		String rss = _calendarBookingService.getCalendarBookingsRSS(
			calendarId, startTime, endTime, max, type, version, displayStyle,
			themeDisplay);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, null, rss.getBytes(),
			ContentTypes.TEXT_XML_UTF8);
	}

	private void _serveCalendarRenderingRules(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long[] calendarIds = ParamUtil.getLongValues(
			resourceRequest, "calendarIds");

		if (ArrayUtil.isEmpty(calendarIds)) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long startTime = ParamUtil.getLong(resourceRequest, "startTime");
		long endTime = ParamUtil.getLong(resourceRequest, "endTime");
		String ruleName = ParamUtil.getString(resourceRequest, "ruleName");

		JSONObject jsonObject =
			CalendarUtil.getCalendarRenderingRulesJSONObject(
				themeDisplay, calendarIds,
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					CalendarBookingWorkflowConstants.STATUS_MAYBE,
					WorkflowConstants.STATUS_PENDING
				},
				startTime, endTime, ruleName, _getTimeZone(resourceRequest));

		writeJSON(resourceRequest, resourceResponse, jsonObject);
	}

	private void _serveCalendarResources(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String keywords = ParamUtil.getString(resourceRequest, "keywords");

		PortletPreferences portletPreferences =
			resourceRequest.getPreferences();

		boolean showUserEvents = GetterUtil.getBoolean(
			portletPreferences.getValue("showUserEvents", null), true);

		Set<Calendar> calendarsSet = new LinkedHashSet<>();

		Hits hits = _search(themeDisplay, keywords);

		for (Document document : hits.getDocs()) {
			long calendarId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			Calendar calendar = _calendarService.fetchCalendar(calendarId);

			if (calendar == null) {
				continue;
			}

			CalendarResource calendarResource = calendar.getCalendarResource();

			if (!calendarResource.isActive() ||
				(calendarResource.isUser() && !showUserEvents)) {

				continue;
			}

			Group group = _groupLocalService.getGroup(calendar.getGroupId());

			long layoutSetPrototypeClassNameId = _portal.getClassNameId(
				LayoutSetPrototype.class);

			if (group.getClassNameId() == layoutSetPrototypeClassNameId) {
				continue;
			}

			if (group.hasStagingGroup()) {
				Group stagingGroup = group.getStagingGroup();

				long stagingGroupId = stagingGroup.getGroupId();

				if (stagingGroupId == themeDisplay.getScopeGroupId()) {
					calendar =
						_calendarLocalService.fetchCalendarByUuidAndGroupId(
							calendar.getUuid(), stagingGroupId);
				}
			}

			calendarsSet.add(calendar);
		}

		String name = StringUtil.merge(
			_customSQL.keywords(keywords), StringPool.BLANK);

		List<Group> groups = _groupLocalService.search(
			themeDisplay.getCompanyId(), name, null,
			LinkedHashMapBuilder.<String, Object>put(
				"usersGroups", themeDisplay.getUserId()
			).build(),
			true, 0, SearchContainer.DEFAULT_DELTA);

		for (Group group : groups) {
			long groupClassNameId = _portal.getClassNameId(Group.class);

			_addCalendar(
				resourceRequest, calendarsSet, groupClassNameId,
				group.getGroupId());
		}

		if (showUserEvents) {
			long userClassNameId = _portal.getClassNameId(User.class);

			List<User> users = _userLocalService.search(
				themeDisplay.getCompanyId(), keywords, 0, null, 0,
				SearchContainer.DEFAULT_DELTA,
				UserFirstNameComparator.getInstance(false));

			for (User user : users) {
				_addCalendar(
					resourceRequest, calendarsSet, userClassNameId,
					user.getUserId());
			}
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (Calendar calendar : calendarsSet) {
			JSONObject jsonObject = CalendarUtil.toCalendarJSONObject(
				themeDisplay, calendar);

			jsonArray.put(jsonObject);
		}

		writeJSON(resourceRequest, resourceResponse, jsonArray);
	}

	private void _serveCurrentTime(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletPreferences portletPreferences =
			resourceRequest.getPreferences();

		User user = themeDisplay.getUser();

		String timeZoneId = portletPreferences.getValue(
			"timeZoneId", user.getTimeZoneId());

		boolean usePortalTimeZone = GetterUtil.getBoolean(
			portletPreferences.getValue(
				"usePortalTimeZone", Boolean.TRUE.toString()));

		if (usePortalTimeZone) {
			timeZoneId = user.getTimeZoneId();
		}

		java.util.Calendar nowCalendar = CalendarFactoryUtil.getCalendar(
			TimeZone.getTimeZone(timeZoneId));

		writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"day", nowCalendar.get(java.util.Calendar.DAY_OF_MONTH)
			).put(
				"hour", nowCalendar.get(java.util.Calendar.HOUR_OF_DAY)
			).put(
				"minute", nowCalendar.get(java.util.Calendar.MINUTE)
			).put(
				"month", nowCalendar.get(java.util.Calendar.MONTH)
			).put(
				"year", nowCalendar.get(java.util.Calendar.YEAR)
			));
	}

	private void _serveExportCalendar(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long calendarId = ParamUtil.getLong(resourceRequest, "calendarId");

		Calendar calendar = _calendarService.getCalendar(calendarId);

		String fileName =
			calendar.getName(themeDisplay.getLocale()) + CharPool.PERIOD +
				String.valueOf(CalendarDataFormat.ICAL);

		CalendarDataHandler calendarDataHandler =
			CalendarDataHandlerFactory.getCalendarDataHandler(
				CalendarDataFormat.ICAL);

		String data = calendarDataHandler.exportCalendar(calendarId);

		String contentType = MimeTypesUtil.getContentType(fileName);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, fileName, data.getBytes(),
			contentType);
	}

	private void _serveHasExclusiveCalendarBooking(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"hasExclusiveCalendarBooking",
				() -> {
					long calendarId = ParamUtil.getLong(
						resourceRequest, "calendarId");

					Calendar calendar = _calendarService.getCalendar(
						calendarId);

					java.util.Calendar endTimeJCalendar = _getJCalendar(
						resourceRequest, "endTime");

					java.util.Calendar startTimeJCalendar = _getJCalendar(
						resourceRequest, "startTime");

					return _calendarBookingLocalService.
						hasExclusiveCalendarBooking(
							calendar, startTimeJCalendar.getTimeInMillis(),
							endTimeJCalendar.getTimeInMillis());
				}));
	}

	private void _serveResourceCalendars(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long calendarResourceId = ParamUtil.getLong(
			resourceRequest, "calendarResourceId");

		List<Calendar> calendars = _calendarService.search(
			themeDisplay.getCompanyId(), null, new long[] {calendarResourceId},
			null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		JSONArray jsonArray = CalendarUtil.toCalendarsJSONArray(
			themeDisplay, calendars);

		writeJSON(resourceRequest, resourceResponse, jsonArray);
	}

	private void _serveUnknownResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String message = themeDisplay.translate(
			"calendar-does-not-serve-unknown-resource-x",
			resourceRequest.getResourceID());

		if (_log.isWarnEnabled()) {
			_log.warn(message);
		}

		writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"error", message
			).put(
				"success", false
			));
	}

	private void _setRenderRequestAttributes(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		CalendarDisplayContext calendarDisplayContext =
			new CalendarDisplayContext(
				renderRequest, renderResponse, _groupLocalService,
				_calendarBookingLocalService, _calendarBookingService,
				_calendarLocalService, _calendarResourceLocalService,
				_calendarService);

		renderRequest.setAttribute(
			CalendarWebKeys.CALENDAR_DISPLAY_CONTEXT, calendarDisplayContext);
	}

	private CalendarBooking _updateCalendarBooking(
			long calendarBookingId, Calendar calendar, long[] childCalendarIds,
			long[] reinvitableCalendarIds, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String location, long startTime,
			long endTime, boolean allDay, Recurrence recurrence,
			long[] reminders, String[] remindersType, int instanceIndex,
			boolean updateInstance, boolean allFollowing,
			ServiceContext serviceContext)
		throws PortalException {

		CalendarBooking calendarBooking =
			_calendarBookingService.fetchCalendarBooking(calendarBookingId);

		TimeZone timeZone = TimeZoneUtil.getTimeZone(StringPool.UTC);

		if (!allDay) {
			timeZone = calendar.getTimeZone();
		}

		if (recurrence != null) {
			java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
				startTime, timeZone);

			recurrence = RecurrenceUtil.inTimeZone(
				recurrence, startTimeJCalendar, timeZone);
		}

		if (calendarBookingId <= 0) {
			calendarBooking = _calendarBookingService.addCalendarBooking(
				calendar.getCalendarId(), childCalendarIds,
				CalendarBookingConstants.RECURRING_CALENDAR_BOOKING_ID_DEFAULT,
				CalendarBookingConstants.PARENT_CALENDAR_BOOKING_ID_DEFAULT,
				titleMap, descriptionMap, location, startTime, endTime, allDay,
				RecurrenceSerializer.serialize(recurrence), reminders[0],
				remindersType[0], reminders[1], remindersType[1],
				serviceContext);
		}
		else {
			if (updateInstance) {
				calendarBooking =
					_calendarBookingService.updateCalendarBookingInstance(
						calendarBookingId, instanceIndex,
						calendar.getCalendarId(), childCalendarIds, titleMap,
						descriptionMap, location, startTime, endTime, allDay,
						RecurrenceSerializer.serialize(recurrence),
						allFollowing, reminders[0], remindersType[0],
						reminders[1], remindersType[1], serviceContext);
			}
			else {
				calendarBooking =
					_calendarBookingService.getCalendarBookingInstance(
						calendarBookingId, instanceIndex);

				long duration = endTime - startTime;
				long offset = _getOffset(
					calendarBooking, startTime, recurrence);

				calendarBooking =
					_calendarBookingService.
						getNewStartTimeAndDurationCalendarBooking(
							calendarBookingId, offset, duration);

				calendarBooking = _getFirstCalendarBookingInstance(
					calendarBooking, recurrence, timeZone);

				calendarBooking = _calendarBookingService.updateCalendarBooking(
					calendarBookingId, calendar.getCalendarId(),
					childCalendarIds, titleMap, descriptionMap, location,
					calendarBooking.getStartTime(),
					calendarBooking.getEndTime(), allDay,
					RecurrenceSerializer.serialize(recurrence), reminders[0],
					remindersType[0], reminders[1], remindersType[1],
					serviceContext);
			}

			Set<Long> reinvitedCalendarIds = _filterReinvitedCalendarIds(
				childCalendarIds, reinvitableCalendarIds);

			List<CalendarBooking> childCalendarBookings =
				calendarBooking.getChildCalendarBookings();

			for (CalendarBooking childCalendarBooking : childCalendarBookings) {
				long childCalendarId = childCalendarBooking.getCalendarId();

				if (!reinvitedCalendarIds.contains(childCalendarId)) {
					continue;
				}

				Calendar childCalendar = childCalendarBooking.getCalendar();

				if (_calendarBookingLocalService.hasExclusiveCalendarBooking(
						childCalendar, startTime, endTime)) {

					continue;
				}

				_calendarBookingLocalService.updateStatus(
					childCalendarBooking.getUserId(), childCalendarBooking,
					WorkflowConstants.STATUS_PENDING, serviceContext);
			}
		}

		return calendarBooking;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarPortlet.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.calendar.model.Calendar)"
	)
	private BaseSearcher _baseSearcher;

	@Reference
	private CalendarBookingLocalService _calendarBookingLocalService;

	@Reference
	private CalendarBookingService _calendarBookingService;

	@Reference
	private CalendarLocalService _calendarLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.calendar.model.Calendar)"
	)
	private ModelResourcePermission<Calendar> _calendarModelResourcePermission;

	@Reference
	private CalendarNotificationTemplateService
		_calendarNotificationTemplateService;

	@Reference
	private CalendarResourceLocalService _calendarResourceLocalService;

	@Reference
	private CalendarResourceService _calendarResourceService;

	@Reference
	private CalendarService _calendarService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private File _file;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.calendar.web)(&(release.schema.version>=1.1.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private UserLocalService _userLocalService;

}