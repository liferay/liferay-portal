/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.util;

import com.liferay.calendar.constants.CalendarBookingConstants;
import com.liferay.calendar.exporter.CalendarDataFormat;
import com.liferay.calendar.exporter.CalendarDataHandler;
import com.liferay.calendar.exporter.CalendarDataHandlerFactory;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.notification.NotificationType;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.service.CalendarBookingLocalServiceUtil;
import com.liferay.calendar.service.CalendarBookingServiceUtil;
import com.liferay.calendar.service.CalendarLocalServiceUtil;
import com.liferay.calendar.util.CalendarResourceUtil;
import com.liferay.calendar.util.JCalendarUtil;
import com.liferay.calendar.workflow.constants.CalendarBookingWorkflowConstants;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.net.URI;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Marcellus Tavares
 */
@org.osgi.service.component.annotations.Component(service = {})
public class CalendarICalDataHandler implements CalendarDataHandler {

	@Override
	public String exportCalendar(long calendarId) throws Exception {
		net.fortuna.ical4j.model.Calendar iCalCalendar = _toICalCalendar(
			CalendarBookingServiceUtil.getCalendarBookings(
				calendarId,
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					CalendarBookingWorkflowConstants.STATUS_MAYBE,
					WorkflowConstants.STATUS_PENDING
				}));

		return _toString(iCalCalendar);
	}

	@Override
	public String exportCalendarBooking(long calendarBookingId)
		throws Exception {

		net.fortuna.ical4j.model.Calendar iCalCalendar = _toICalCalendar(
			ListUtil.fromArray(
				CalendarBookingLocalServiceUtil.getCalendarBooking(
					calendarBookingId)));

		return _toString(iCalCalendar);
	}

	@Override
	public void importCalendar(long calendarId, String data) throws Exception {
		CalendarBuilder calendarBuilder = new CalendarBuilder();

		UnsyncStringReader unsyncStringReader = new UnsyncStringReader(data);

		net.fortuna.ical4j.model.Calendar iCalCalendar = calendarBuilder.build(
			unsyncStringReader);

		List<VEvent> vEvents = iCalCalendar.getComponents(Component.VEVENT);

		for (VEvent vEvent : vEvents) {
			_importICalEvent(calendarId, vEvent);
		}
	}

	@Activate
	protected void activate() {
		CalendarDataHandlerFactory.registerCalendarDataHandler(
			CalendarDataFormat.ICAL, this);
	}

	@Deactivate
	protected void deactivate() {
		CalendarDataHandlerFactory.unregisterCalendarDataHandler(
			CalendarDataFormat.ICAL);
	}

	private void _addHourMinuteFromTimeInMillis(
		long sourceTimeInMillis, java.util.Calendar targetJCalendar) {

		java.util.Calendar jCalendar = java.util.Calendar.getInstance();

		jCalendar.setTimeInMillis(sourceTimeInMillis);

		targetJCalendar.add(
			java.util.Calendar.HOUR_OF_DAY,
			jCalendar.get(java.util.Calendar.HOUR_OF_DAY));
		targetJCalendar.add(
			java.util.Calendar.MINUTE,
			jCalendar.get(java.util.Calendar.MINUTE));
	}

	private void _addHourMinuteToUntilDate(Recur recur) {
		if (recur.getUntil() == null) {
			return;
		}

		java.util.Calendar jCalendar = java.util.Calendar.getInstance();

		jCalendar.setTime(recur.getUntil());

		jCalendar.add(java.util.Calendar.HOUR_OF_DAY, 23);
		jCalendar.add(java.util.Calendar.MINUTE, 59);

		recur.setUntil(new DateTime(jCalendar.getTimeInMillis()));
	}

	private String _getDescription(VEvent vEvent) {
		Property descriptionProperty = vEvent.getDescription();

		if (descriptionProperty == null) {
			return null;
		}

		return descriptionProperty.getValue();
	}

	private TimeZoneRegistry _getTimeZoneRegistry() {
		if (_timeZoneRegistry == null) {
			TimeZoneRegistryFactory timeZoneRegistryFactory =
				TimeZoneRegistryFactory.getInstance();

			_timeZoneRegistry = timeZoneRegistryFactory.createRegistry();
		}

		return _timeZoneRegistry;
	}

	private String _getXAltDescription(VEvent vEvent) {
		Property xAltDescProperty = vEvent.getProperty("X-ALT-DESC");

		if (xAltDescProperty == null) {
			return _getDescription(vEvent);
		}

		Parameter fmtTypeParameter = xAltDescProperty.getParameter("FMTTYPE");

		if ((fmtTypeParameter == null) ||
			!Objects.equals(fmtTypeParameter.getValue(), "text/html")) {

			return _getDescription(vEvent);
		}

		return xAltDescProperty.getValue();
	}

	private void _importICalEvent(long calendarId, VEvent vEvent)
		throws Exception {

		Calendar calendar = CalendarLocalServiceUtil.getCalendar(calendarId);

		// Title

		User user = UserLocalServiceUtil.getUser(calendar.getUserId());

		Map<Locale, String> titleMap = new HashMap<>();

		Summary summary = vEvent.getSummary();

		if (summary != null) {
			String title = ModelHintsUtil.trimString(
				CalendarBooking.class.getName(), "title", summary.getValue());

			titleMap.put(user.getLocale(), title);
		}

		// Description

		Map<Locale, String> descriptionMap = new HashMap<>();

		String description = _getXAltDescription(vEvent);

		if (description != null) {
			descriptionMap.put(user.getLocale(), description);
		}

		// Location

		String locationString = StringPool.BLANK;

		Location location = vEvent.getLocation();

		if (location != null) {
			locationString = location.getValue();
		}

		// Dates

		DtStart dtStart = vEvent.getStartDate();

		Date startDate = dtStart.getDate();

		DtEnd dtEnd = vEvent.getEndDate();

		Date endDate = dtEnd.getDate();

		// All day

		boolean allDay = false;

		if (_isICalDateOnly(dtStart)) {
			allDay = true;

			long time = endDate.getTime();

			endDate.setTime(time - 1);
		}

		// Recurrence

		RRule rRule = (RRule)vEvent.getProperty(Property.RRULE);

		String recurrence = StringPool.BLANK;

		if (rRule != null) {
			recurrence = StringUtil.trim(rRule.toString());

			PropertyList propertyList = vEvent.getProperties(Property.EXDATE);

			if (!propertyList.isEmpty()) {
				StringBundler sb = new StringBundler();

				sb.append(recurrence);
				sb.append(StringPool.NEW_LINE);
				sb.append(_EXDATE);

				Iterator<ExDate> iterator = propertyList.iterator();

				while (iterator.hasNext()) {
					ExDate exDate = iterator.next();

					DateList dateList = exDate.getDates();

					ListIterator<Date> listIterator = dateList.listIterator();

					while (listIterator.hasNext()) {
						Date date = listIterator.next();

						java.util.Calendar jCalendar =
							JCalendarUtil.getJCalendar(date.getTime());

						int year = jCalendar.get(java.util.Calendar.YEAR);
						int month = jCalendar.get(java.util.Calendar.MONTH) + 1;
						int day = jCalendar.get(java.util.Calendar.DATE);
						int hour = jCalendar.get(
							java.util.Calendar.HOUR_OF_DAY);
						int minute = jCalendar.get(java.util.Calendar.MINUTE);
						int second = jCalendar.get(java.util.Calendar.SECOND);

						sb.append(
							String.format(
								_EXDATE_FORMAT, year, month, day, hour, minute,
								second));

						if (!listIterator.hasNext()) {
							continue;
						}

						sb.append(StringPool.COMMA);
					}

					if (!iterator.hasNext()) {
						continue;
					}

					sb.append(StringPool.COMMA);
				}

				recurrence = sb.toString();
			}
		}

		// Reminders

		ComponentList componentList = vEvent.getAlarms();

		long[] reminders = new long[componentList.size()];
		String[] reminderTypes = new String[componentList.size()];

		int i = 0;

		for (Iterator<VAlarm> iterator = componentList.iterator();
			 iterator.hasNext();) {

			VAlarm vAlarm = iterator.next();

			Action action = vAlarm.getAction();

			String value = StringUtil.lowerCase(action.getValue());

			if (!_isActionSupported(value)) {
				continue;
			}

			reminderTypes[i] = value;

			Trigger trigger = vAlarm.getTrigger();

			DateTime dateTime = trigger.getDateTime();

			TemporalAmount temporalAmount = trigger.getDuration();

			if ((dateTime == null) && (temporalAmount == null)) {
				continue;
			}

			long time = 0;

			if (dateTime != null) {
				time = startDate.getTime() - dateTime.getTime();

				if (time < 0) {
					continue;
				}
			}
			else {
				Duration duration = Duration.from(temporalAmount);

				if (!duration.isNegative()) {
					continue;
				}

				time += duration.toMillis();
			}

			reminders[i] = time;

			i++;
		}

		long firstReminder = 0;
		String firstReminderType = null;
		long secondReminder = 0;
		String secondReminderType = null;

		if (i > 0) {
			firstReminder = reminders[0];
			firstReminderType = reminderTypes[0];
		}

		if (i > 1) {
			secondReminder = reminders[1];
			secondReminderType = reminderTypes[1];
		}

		// Attendees

		PropertyList propertyList = vEvent.getProperties(Property.ATTENDEE);

		List<Long> childCalendarIds = new ArrayList<>();

		for (Iterator<Attendee> iterator = propertyList.iterator();
			 iterator.hasNext();) {

			Attendee attendee = iterator.next();

			URI uri = attendee.getCalAddress();

			if (uri == null) {
				continue;
			}

			User attendeeUser = UserLocalServiceUtil.fetchUserByEmailAddress(
				calendar.getCompanyId(), uri.getSchemeSpecificPart());

			if ((attendeeUser == null) ||
				(calendar.getUserId() == attendeeUser.getUserId())) {

				continue;
			}

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(calendar.getCompanyId());
			serviceContext.setScopeGroupId(calendar.getGroupId());

			CalendarResource calendarResource =
				CalendarResourceUtil.getUserCalendarResource(
					attendeeUser.getUserId(), serviceContext);

			if (calendarResource == null) {
				continue;
			}

			childCalendarIds.add(calendarResource.getDefaultCalendarId());
		}

		long[] childCalendarIdsArray = ArrayUtil.toArray(
			childCalendarIds.toArray(new Long[0]));

		// Merge calendar booking

		CalendarBooking calendarBooking = null;

		String vEventUidValue = null;

		Uid uid = vEvent.getUid();

		if (uid != null) {
			vEventUidValue = uid.getValue();

			calendarBooking =
				CalendarBookingLocalServiceUtil.fetchCalendarBooking(
					calendarId, vEventUidValue);
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAttribute("sendNotification", Boolean.FALSE);
		serviceContext.setAttribute("vEventUid", vEventUidValue);
		serviceContext.setScopeGroupId(calendar.getGroupId());

		if (calendarBooking == null) {
			CalendarBookingServiceUtil.addCalendarBooking(
				calendarId, childCalendarIdsArray,
				CalendarBookingConstants.PARENT_CALENDAR_BOOKING_ID_DEFAULT,
				CalendarBookingConstants.RECURRING_CALENDAR_BOOKING_ID_DEFAULT,
				titleMap, descriptionMap, locationString, startDate.getTime(),
				endDate.getTime(), allDay, recurrence, firstReminder,
				firstReminderType, secondReminder, secondReminderType,
				serviceContext);
		}
		else {
			CalendarBookingServiceUtil.updateCalendarBooking(
				calendarBooking.getCalendarBookingId(), calendarId,
				childCalendarIdsArray, titleMap, descriptionMap, locationString,
				startDate.getTime(), endDate.getTime(), allDay, recurrence,
				firstReminder, firstReminderType, secondReminder,
				secondReminderType, serviceContext);
		}
	}

	private boolean _isActionSupported(String value) {
		try {
			NotificationType.parse(value);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}

			return false;
		}

		return true;
	}

	private boolean _isICalDateOnly(DateProperty dateProperty) {
		Parameter valueParameter = dateProperty.getParameter(Parameter.VALUE);

		if (valueParameter == null) {
			return false;
		}

		return Objects.equals(valueParameter.getValue(), "DATE");
	}

	private VAlarm _toICalAlarm(
		NotificationType notificationType, long reminder, String emailAddress) {

		TemporalAmount temporalAmount = _toTemporalAmount(reminder);

		VAlarm vAlarm = new VAlarm(temporalAmount);

		PropertyList propertyList = vAlarm.getProperties();

		Action action = Action.DISPLAY;

		if (notificationType == NotificationType.EMAIL) {
			URI uri = URI.create("mailto:".concat(emailAddress));

			Attendee attendee = new Attendee(uri);

			action = Action.EMAIL;

			propertyList.add(attendee);
			propertyList.add(new Summary("Alarm Notification"));
		}

		propertyList.add(action);
		propertyList.add(new Description("This is an event reminder."));

		return vAlarm;
	}

	private Attendee _toICalAttendee(
		String fullName, String emailAddress, int status) {

		Attendee attendee = new Attendee();

		URI uri = URI.create("mailto:".concat(emailAddress));

		attendee.setCalAddress(uri);

		Cn cn = new Cn(fullName);

		ParameterList parameters = attendee.getParameters();

		parameters.add(cn);
		parameters.add(CuType.INDIVIDUAL);
		parameters.add(Role.REQ_PARTICIPANT);
		parameters.add(Rsvp.TRUE);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			parameters.add(PartStat.ACCEPTED);
		}
		else {
			parameters.add(PartStat.NEEDS_ACTION);
		}

		return attendee;
	}

	private net.fortuna.ical4j.model.Calendar _toICalCalendar(
			List<CalendarBooking> calendarBookings)
		throws Exception {

		net.fortuna.ical4j.model.Calendar iCalCalendar =
			new net.fortuna.ical4j.model.Calendar();

		PropertyList propertiesList = iCalCalendar.getProperties();

		ProdId prodId = new ProdId(
			"-//Liferay Inc//Liferay Portal " + ReleaseInfo.getVersion() +
				"//EN");

		propertiesList.add(prodId);

		propertiesList.add(Version.VERSION_2_0);
		propertiesList.add(CalScale.GREGORIAN);
		propertiesList.add(Method.PUBLISH);

		List<CalendarComponent> calendarComponents =
			iCalCalendar.getComponents();

		Set<String> timeZoneIds = new HashSet<>();

		for (CalendarBooking calendarBooking : calendarBookings) {
			TimeZone timeZone = calendarBooking.getTimeZone();

			if (timeZoneIds.add(timeZone.getID())) {
				net.fortuna.ical4j.model.TimeZone icalTimeZone =
					_toICalTimeZone(timeZone);

				calendarComponents.add(icalTimeZone.getVTimeZone());
			}

			calendarComponents.add(_toICalEvent(calendarBooking));
		}

		return iCalCalendar;
	}

	private DateTime _toICalDateTime(long time, TimeZone timeZone) {
		DateTime dateTime = new DateTime();

		dateTime.setTime(time);

		if (timeZone == null) {
			dateTime.setUtc(true);
		}
		else {
			dateTime.setTimeZone(_toICalTimeZone(timeZone));
		}

		return dateTime;
	}

	private VEvent _toICalEvent(CalendarBooking calendarBooking)
		throws Exception {

		VEvent vEvent = new VEvent();

		PropertyList propertyList = vEvent.getProperties();

		// UID

		Uid uid = new Uid(calendarBooking.getVEventUid());

		propertyList.add(uid);

		// Dates

		if (calendarBooking.isAllDay()) {
			DtStart dtStart = new DtStart(
				new Date(calendarBooking.getStartTime()));

			propertyList.add(dtStart);

			java.util.Calendar endJCalendar = JCalendarUtil.getJCalendar(
				calendarBooking.getEndTime());

			endJCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);

			DtEnd dtEnd = new DtEnd(new Date(endJCalendar.getTime()));

			propertyList.add(dtEnd);
		}
		else {
			DtStart dtStart = new DtStart(
				_toICalDateTime(
					calendarBooking.getStartTime(),
					calendarBooking.getTimeZone()));

			propertyList.add(dtStart);

			DtEnd dtEnd = new DtEnd(
				_toICalDateTime(
					calendarBooking.getEndTime(),
					calendarBooking.getTimeZone()));

			propertyList.add(dtEnd);
		}

		// Title

		User user = UserLocalServiceUtil.fetchUser(calendarBooking.getUserId());

		Locale locale =
			(user != null) ? user.getLocale() : LocaleUtil.getSiteDefault();

		Summary summary = new Summary(calendarBooking.getTitle(locale));

		propertyList.add(summary);

		// Description

		Company company = CompanyLocalServiceUtil.getCompany(
			calendarBooking.getCompanyId());

		String calendarBookingDescription = StringUtil.replace(
			calendarBooking.getDescription(locale),
			new String[] {"href=\"/", "src=\"/"},
			new String[] {
				"href=\"" + company.getPortalURL(calendarBooking.getGroupId()) +
					"/",
				"src=\"" + company.getPortalURL(calendarBooking.getGroupId()) +
					"/"
			});

		propertyList.add(
			new Description(HtmlUtil.stripHtml(calendarBookingDescription)));

		XProperty xProperty = new XProperty(
			"X-ALT-DESC", calendarBookingDescription);

		ParameterList parameters = xProperty.getParameters();

		parameters.add(new XParameter("FMTTYPE", "text/html"));

		propertyList.add(xProperty);

		// Location

		Location location = new Location(calendarBooking.getLocation());

		propertyList.add(location);

		// Recurrence

		String recurrence = calendarBooking.getRecurrence();

		if (Validator.isNotNull(recurrence)) {
			int index = recurrence.indexOf(CharPool.NEW_LINE);

			if (index > 0) {
				recurrence = recurrence.substring(0, index);
			}

			recurrence = StringUtil.removeSubstring(recurrence, _RRULE);

			RRule rRule = new RRule(recurrence);

			_addHourMinuteToUntilDate(rRule.getRecur());

			propertyList.add(rRule);

			ExDate exDate = _toICalExDate(
				calendarBooking.getRecurrenceObj(),
				calendarBooking.getStartTime(), calendarBooking.getTimeZone());

			if (exDate != null) {
				propertyList.add(exDate);
			}
		}

		// Reminders

		ComponentList componentList = vEvent.getAlarms();

		long firstReminder = calendarBooking.getFirstReminder();

		if ((firstReminder > 0) && (user != null)) {
			VAlarm vAlarm = _toICalAlarm(
				calendarBooking.getFirstReminderNotificationType(),
				firstReminder, user.getEmailAddress());

			componentList.add(vAlarm);
		}

		long secondReminder = calendarBooking.getSecondReminder();

		if ((secondReminder > 0) && (user != null)) {
			VAlarm alarm = _toICalAlarm(
				calendarBooking.getSecondReminderNotificationType(),
				secondReminder, user.getEmailAddress());

			componentList.add(alarm);
		}

		// Attendees

		List<CalendarBooking> childCalendarBookings =
			calendarBooking.getChildCalendarBookings();

		for (CalendarBooking childCalendarBooking : childCalendarBookings) {
			CalendarResource calResource =
				childCalendarBooking.getCalendarResource();

			if (!calResource.isUser() ||
				(calendarBooking.getCalendarBookingId() ==
					childCalendarBooking.getCalendarBookingId())) {

				continue;
			}

			User calResourceUser = UserLocalServiceUtil.getUser(
				calResource.getClassPK());

			Attendee attendee = _toICalAttendee(
				calResourceUser.getFullName(),
				calResourceUser.getEmailAddress(),
				childCalendarBooking.getStatus());

			propertyList.add(attendee);
		}

		return vEvent;
	}

	private ExDate _toICalExDate(
		Recurrence recurrence, long startTime, TimeZone timeZone) {

		List<java.util.Calendar> exceptionJCalendars =
			recurrence.getExceptionJCalendars();

		if (exceptionJCalendars.isEmpty()) {
			return null;
		}

		DateList dateList = new DateList();

		if (timeZone == null) {
			dateList.setUtc(true);
		}
		else {
			dateList.setTimeZone(_toICalTimeZone(timeZone));
		}

		for (java.util.Calendar exceptionJCalendar : exceptionJCalendars) {
			_addHourMinuteFromTimeInMillis(startTime, exceptionJCalendar);

			DateTime dateTime = _toICalDateTime(
				exceptionJCalendar.getTimeInMillis(), timeZone);

			dateList.add(dateTime);
		}

		ExDate exDate = new ExDate(dateList);

		if (timeZone == null) {
			exDate.setUtc(true);
		}
		else {
			exDate.setTimeZone(_toICalTimeZone(timeZone));
		}

		return exDate;
	}

	private net.fortuna.ical4j.model.TimeZone _toICalTimeZone(
		TimeZone timeZone) {

		TimeZoneRegistry timeZoneRegistry = _getTimeZoneRegistry();

		return timeZoneRegistry.getTimeZone(timeZone.getID());
	}

	private String _toString(net.fortuna.ical4j.model.Calendar iCalCalendar)
		throws Exception {

		CalendarOutputter calendarOutputter = new CalendarOutputter();

		ComponentList componentList = iCalCalendar.getComponents();

		if (componentList.isEmpty()) {
			calendarOutputter.setValidating(false);
		}

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		calendarOutputter.output(iCalCalendar, unsyncStringWriter);

		unsyncStringWriter.flush();

		return unsyncStringWriter.toString();
	}

	private TemporalAmount _toTemporalAmount(long reminder) {
		int weeks = (int)(reminder / Time.WEEK);

		if (weeks > 0) {
			return Period.ofWeeks(weeks);
		}

		int days = (int)(reminder / Time.DAY);

		if (days > 0) {
			return Period.ofDays(days);
		}

		int hours = (int)(reminder / Time.HOUR);

		if (hours > 0) {
			return Duration.ofHours(hours);
		}

		int minutes = (int)(reminder / Time.MINUTE);

		if (minutes > 0) {
			return Duration.ofMinutes(minutes);
		}

		int seconds = (int)(reminder / Time.SECOND);

		if (seconds > 0) {
			return Duration.ofSeconds(seconds);
		}

		return null;
	}

	private static final String _EXDATE =
		"EXDATE;TZID=\"UTC\";VALUE=DATE-TIME:";

	private static final String _EXDATE_FORMAT = "%04d%02d%02dT%02d%02d%02dZ";

	private static final String _RRULE = "RRULE:";

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarICalDataHandler.class);

	private static TimeZoneRegistry _timeZoneRegistry;

}