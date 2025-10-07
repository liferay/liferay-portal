/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link CalendarBooking}.
 * </p>
 *
 * @author Eduardo Lundgren
 * @see CalendarBooking
 * @generated
 */
public class CalendarBookingWrapper
	extends BaseModelWrapper<CalendarBooking>
	implements CalendarBooking, ModelWrapper<CalendarBooking> {

	public CalendarBookingWrapper(CalendarBooking calendarBooking) {
		super(calendarBooking);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("calendarBookingId", getCalendarBookingId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("calendarId", getCalendarId());
		attributes.put("calendarResourceId", getCalendarResourceId());
		attributes.put("parentCalendarBookingId", getParentCalendarBookingId());
		attributes.put(
			"recurringCalendarBookingId", getRecurringCalendarBookingId());
		attributes.put("vEventUid", getVEventUid());
		attributes.put("title", getTitle());
		attributes.put("description", getDescription());
		attributes.put("location", getLocation());
		attributes.put("startTime", getStartTime());
		attributes.put("endTime", getEndTime());
		attributes.put("allDay", isAllDay());
		attributes.put("recurrence", getRecurrence());
		attributes.put("firstReminder", getFirstReminder());
		attributes.put("firstReminderType", getFirstReminderType());
		attributes.put("secondReminder", getSecondReminder());
		attributes.put("secondReminderType", getSecondReminderType());
		attributes.put("lastPublishDate", getLastPublishDate());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long calendarBookingId = (Long)attributes.get("calendarBookingId");

		if (calendarBookingId != null) {
			setCalendarBookingId(calendarBookingId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long calendarId = (Long)attributes.get("calendarId");

		if (calendarId != null) {
			setCalendarId(calendarId);
		}

		Long calendarResourceId = (Long)attributes.get("calendarResourceId");

		if (calendarResourceId != null) {
			setCalendarResourceId(calendarResourceId);
		}

		Long parentCalendarBookingId = (Long)attributes.get(
			"parentCalendarBookingId");

		if (parentCalendarBookingId != null) {
			setParentCalendarBookingId(parentCalendarBookingId);
		}

		Long recurringCalendarBookingId = (Long)attributes.get(
			"recurringCalendarBookingId");

		if (recurringCalendarBookingId != null) {
			setRecurringCalendarBookingId(recurringCalendarBookingId);
		}

		String vEventUid = (String)attributes.get("vEventUid");

		if (vEventUid != null) {
			setVEventUid(vEventUid);
		}

		String title = (String)attributes.get("title");

		if (title != null) {
			setTitle(title);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String location = (String)attributes.get("location");

		if (location != null) {
			setLocation(location);
		}

		Long startTime = (Long)attributes.get("startTime");

		if (startTime != null) {
			setStartTime(startTime);
		}

		Long endTime = (Long)attributes.get("endTime");

		if (endTime != null) {
			setEndTime(endTime);
		}

		Boolean allDay = (Boolean)attributes.get("allDay");

		if (allDay != null) {
			setAllDay(allDay);
		}

		String recurrence = (String)attributes.get("recurrence");

		if (recurrence != null) {
			setRecurrence(recurrence);
		}

		Long firstReminder = (Long)attributes.get("firstReminder");

		if (firstReminder != null) {
			setFirstReminder(firstReminder);
		}

		String firstReminderType = (String)attributes.get("firstReminderType");

		if (firstReminderType != null) {
			setFirstReminderType(firstReminderType);
		}

		Long secondReminder = (Long)attributes.get("secondReminder");

		if (secondReminder != null) {
			setSecondReminder(secondReminder);
		}

		String secondReminderType = (String)attributes.get(
			"secondReminderType");

		if (secondReminderType != null) {
			setSecondReminderType(secondReminderType);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		String statusByUserName = (String)attributes.get("statusByUserName");

		if (statusByUserName != null) {
			setStatusByUserName(statusByUserName);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	@Override
	public CalendarBooking cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the all day of this calendar booking.
	 *
	 * @return the all day of this calendar booking
	 */
	@Override
	public boolean getAllDay() {
		return model.getAllDay();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	@Override
	public Calendar getCalendar()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCalendar();
	}

	/**
	 * Returns the calendar booking ID of this calendar booking.
	 *
	 * @return the calendar booking ID of this calendar booking
	 */
	@Override
	public long getCalendarBookingId() {
		return model.getCalendarBookingId();
	}

	/**
	 * Returns the calendar ID of this calendar booking.
	 *
	 * @return the calendar ID of this calendar booking
	 */
	@Override
	public long getCalendarId() {
		return model.getCalendarId();
	}

	@Override
	public CalendarResource getCalendarResource()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCalendarResource();
	}

	/**
	 * Returns the calendar resource ID of this calendar booking.
	 *
	 * @return the calendar resource ID of this calendar booking
	 */
	@Override
	public long getCalendarResourceId() {
		return model.getCalendarResourceId();
	}

	@Override
	public java.util.List<CalendarBooking> getChildCalendarBookings() {
		return model.getChildCalendarBookings();
	}

	/**
	 * Returns the company ID of this calendar booking.
	 *
	 * @return the company ID of this calendar booking
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this calendar booking.
	 *
	 * @return the create date of this calendar booking
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this calendar booking.
	 *
	 * @return the ct collection ID of this calendar booking
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the description of this calendar booking.
	 *
	 * @return the description of this calendar booking
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this calendar booking in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this calendar booking
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this calendar booking in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this calendar booking. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this calendar booking in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this calendar booking
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this calendar booking in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this calendar booking
	 */
	@Override
	public String getDescription(String languageId, boolean useDefault) {
		return model.getDescription(languageId, useDefault);
	}

	@Override
	public String getDescriptionCurrentLanguageId() {
		return model.getDescriptionCurrentLanguageId();
	}

	@Override
	public String getDescriptionCurrentValue() {
		return model.getDescriptionCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized descriptions of this calendar booking.
	 *
	 * @return the locales and localized descriptions of this calendar booking
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	@Override
	public long getDuration() {
		return model.getDuration();
	}

	/**
	 * Returns the end time of this calendar booking.
	 *
	 * @return the end time of this calendar booking
	 */
	@Override
	public long getEndTime() {
		return model.getEndTime();
	}

	/**
	 * Returns the external reference code of this calendar booking.
	 *
	 * @return the external reference code of this calendar booking
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the first reminder of this calendar booking.
	 *
	 * @return the first reminder of this calendar booking
	 */
	@Override
	public long getFirstReminder() {
		return model.getFirstReminder();
	}

	@Override
	public com.liferay.calendar.notification.NotificationType
		getFirstReminderNotificationType() {

		return model.getFirstReminderNotificationType();
	}

	/**
	 * Returns the first reminder type of this calendar booking.
	 *
	 * @return the first reminder type of this calendar booking
	 */
	@Override
	public String getFirstReminderType() {
		return model.getFirstReminderType();
	}

	/**
	 * Returns the group ID of this calendar booking.
	 *
	 * @return the group ID of this calendar booking
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	@Override
	public int getInstanceIndex() {
		return model.getInstanceIndex();
	}

	/**
	 * Returns the last publish date of this calendar booking.
	 *
	 * @return the last publish date of this calendar booking
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the location of this calendar booking.
	 *
	 * @return the location of this calendar booking
	 */
	@Override
	public String getLocation() {
		return model.getLocation();
	}

	/**
	 * Returns the modified date of this calendar booking.
	 *
	 * @return the modified date of this calendar booking
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this calendar booking.
	 *
	 * @return the mvcc version of this calendar booking
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	@Override
	public CalendarBooking getParentCalendarBooking()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getParentCalendarBooking();
	}

	/**
	 * Returns the parent calendar booking ID of this calendar booking.
	 *
	 * @return the parent calendar booking ID of this calendar booking
	 */
	@Override
	public long getParentCalendarBookingId() {
		return model.getParentCalendarBookingId();
	}

	/**
	 * Returns the primary key of this calendar booking.
	 *
	 * @return the primary key of this calendar booking
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the recurrence of this calendar booking.
	 *
	 * @return the recurrence of this calendar booking
	 */
	@Override
	public String getRecurrence() {
		return model.getRecurrence();
	}

	@Override
	public com.liferay.calendar.recurrence.Recurrence getRecurrenceObj() {
		return model.getRecurrenceObj();
	}

	/**
	 * Returns the recurring calendar booking ID of this calendar booking.
	 *
	 * @return the recurring calendar booking ID of this calendar booking
	 */
	@Override
	public long getRecurringCalendarBookingId() {
		return model.getRecurringCalendarBookingId();
	}

	/**
	 * Returns the second reminder of this calendar booking.
	 *
	 * @return the second reminder of this calendar booking
	 */
	@Override
	public long getSecondReminder() {
		return model.getSecondReminder();
	}

	@Override
	public com.liferay.calendar.notification.NotificationType
		getSecondReminderNotificationType() {

		return model.getSecondReminderNotificationType();
	}

	/**
	 * Returns the second reminder type of this calendar booking.
	 *
	 * @return the second reminder type of this calendar booking
	 */
	@Override
	public String getSecondReminderType() {
		return model.getSecondReminderType();
	}

	/**
	 * Returns the start time of this calendar booking.
	 *
	 * @return the start time of this calendar booking
	 */
	@Override
	public long getStartTime() {
		return model.getStartTime();
	}

	/**
	 * Returns the status of this calendar booking.
	 *
	 * @return the status of this calendar booking
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this calendar booking.
	 *
	 * @return the status by user ID of this calendar booking
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this calendar booking.
	 *
	 * @return the status by user name of this calendar booking
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this calendar booking.
	 *
	 * @return the status by user uuid of this calendar booking
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this calendar booking.
	 *
	 * @return the status date of this calendar booking
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	@Override
	public java.util.TimeZone getTimeZone() {
		return model.getTimeZone();
	}

	/**
	 * Returns the title of this calendar booking.
	 *
	 * @return the title of this calendar booking
	 */
	@Override
	public String getTitle() {
		return model.getTitle();
	}

	/**
	 * Returns the localized title of this calendar booking in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized title of this calendar booking
	 */
	@Override
	public String getTitle(java.util.Locale locale) {
		return model.getTitle(locale);
	}

	/**
	 * Returns the localized title of this calendar booking in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this calendar booking. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getTitle(java.util.Locale locale, boolean useDefault) {
		return model.getTitle(locale, useDefault);
	}

	/**
	 * Returns the localized title of this calendar booking in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized title of this calendar booking
	 */
	@Override
	public String getTitle(String languageId) {
		return model.getTitle(languageId);
	}

	/**
	 * Returns the localized title of this calendar booking in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this calendar booking
	 */
	@Override
	public String getTitle(String languageId, boolean useDefault) {
		return model.getTitle(languageId, useDefault);
	}

	@Override
	public String getTitleCurrentLanguageId() {
		return model.getTitleCurrentLanguageId();
	}

	@Override
	public String getTitleCurrentValue() {
		return model.getTitleCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized titles of this calendar booking.
	 *
	 * @return the locales and localized titles of this calendar booking
	 */
	@Override
	public Map<java.util.Locale, String> getTitleMap() {
		return model.getTitleMap();
	}

	/**
	 * Returns the class primary key of the trash entry for this calendar booking.
	 *
	 * @return the class primary key of the trash entry for this calendar booking
	 */
	@Override
	public long getTrashEntryClassPK() {
		return model.getTrashEntryClassPK();
	}

	/**
	 * Returns the user ID of this calendar booking.
	 *
	 * @return the user ID of this calendar booking
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this calendar booking.
	 *
	 * @return the user name of this calendar booking
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this calendar booking.
	 *
	 * @return the user uuid of this calendar booking
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this calendar booking.
	 *
	 * @return the uuid of this calendar booking
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the v event uid of this calendar booking.
	 *
	 * @return the v event uid of this calendar booking
	 */
	@Override
	public String getVEventUid() {
		return model.getVEventUid();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is all day.
	 *
	 * @return <code>true</code> if this calendar booking is all day; <code>false</code> otherwise
	 */
	@Override
	public boolean isAllDay() {
		return model.isAllDay();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is approved.
	 *
	 * @return <code>true</code> if this calendar booking is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is denied.
	 *
	 * @return <code>true</code> if this calendar booking is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is a draft.
	 *
	 * @return <code>true</code> if this calendar booking is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is expired.
	 *
	 * @return <code>true</code> if this calendar booking is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is inactive.
	 *
	 * @return <code>true</code> if this calendar booking is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is incomplete.
	 *
	 * @return <code>true</code> if this calendar booking is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is in the Recycle Bin.
	 *
	 * @return <code>true</code> if this calendar booking is in the Recycle Bin; <code>false</code> otherwise
	 */
	@Override
	public boolean isInTrash() {
		return model.isInTrash();
	}

	@Override
	public boolean isMasterBooking() {
		return model.isMasterBooking();
	}

	@Override
	public boolean isMasterRecurringBooking() {
		return model.isMasterRecurringBooking();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is pending.
	 *
	 * @return <code>true</code> if this calendar booking is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	@Override
	public boolean isRecurring() {
		return model.isRecurring();
	}

	/**
	 * Returns <code>true</code> if this calendar booking is scheduled.
	 *
	 * @return <code>true</code> if this calendar booking is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
	}

	@Override
	public void persist() {
		model.persist();
	}

	@Override
	public void prepareLocalizedFieldsForImport()
		throws com.liferay.portal.kernel.exception.LocaleException {

		model.prepareLocalizedFieldsForImport();
	}

	@Override
	public void prepareLocalizedFieldsForImport(
			java.util.Locale defaultImportLocale)
		throws com.liferay.portal.kernel.exception.LocaleException {

		model.prepareLocalizedFieldsForImport(defaultImportLocale);
	}

	/**
	 * Sets whether this calendar booking is all day.
	 *
	 * @param allDay the all day of this calendar booking
	 */
	@Override
	public void setAllDay(boolean allDay) {
		model.setAllDay(allDay);
	}

	/**
	 * Sets the calendar booking ID of this calendar booking.
	 *
	 * @param calendarBookingId the calendar booking ID of this calendar booking
	 */
	@Override
	public void setCalendarBookingId(long calendarBookingId) {
		model.setCalendarBookingId(calendarBookingId);
	}

	/**
	 * Sets the calendar ID of this calendar booking.
	 *
	 * @param calendarId the calendar ID of this calendar booking
	 */
	@Override
	public void setCalendarId(long calendarId) {
		model.setCalendarId(calendarId);
	}

	/**
	 * Sets the calendar resource ID of this calendar booking.
	 *
	 * @param calendarResourceId the calendar resource ID of this calendar booking
	 */
	@Override
	public void setCalendarResourceId(long calendarResourceId) {
		model.setCalendarResourceId(calendarResourceId);
	}

	/**
	 * Sets the company ID of this calendar booking.
	 *
	 * @param companyId the company ID of this calendar booking
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this calendar booking.
	 *
	 * @param createDate the create date of this calendar booking
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this calendar booking.
	 *
	 * @param ctCollectionId the ct collection ID of this calendar booking
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the description of this calendar booking.
	 *
	 * @param description the description of this calendar booking
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this calendar booking in the language.
	 *
	 * @param description the localized description of this calendar booking
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this calendar booking in the language, and sets the default locale.
	 *
	 * @param description the localized description of this calendar booking
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescription(
		String description, java.util.Locale locale,
		java.util.Locale defaultLocale) {

		model.setDescription(description, locale, defaultLocale);
	}

	@Override
	public void setDescriptionCurrentLanguageId(String languageId) {
		model.setDescriptionCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized descriptions of this calendar booking from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this calendar booking
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this calendar booking from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this calendar booking
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the end time of this calendar booking.
	 *
	 * @param endTime the end time of this calendar booking
	 */
	@Override
	public void setEndTime(long endTime) {
		model.setEndTime(endTime);
	}

	/**
	 * Sets the external reference code of this calendar booking.
	 *
	 * @param externalReferenceCode the external reference code of this calendar booking
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the first reminder of this calendar booking.
	 *
	 * @param firstReminder the first reminder of this calendar booking
	 */
	@Override
	public void setFirstReminder(long firstReminder) {
		model.setFirstReminder(firstReminder);
	}

	/**
	 * Sets the first reminder type of this calendar booking.
	 *
	 * @param firstReminderType the first reminder type of this calendar booking
	 */
	@Override
	public void setFirstReminderType(String firstReminderType) {
		model.setFirstReminderType(firstReminderType);
	}

	/**
	 * Sets the group ID of this calendar booking.
	 *
	 * @param groupId the group ID of this calendar booking
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	@Override
	public void setInstanceIndex(int instanceIndex) {
		model.setInstanceIndex(instanceIndex);
	}

	/**
	 * Sets the last publish date of this calendar booking.
	 *
	 * @param lastPublishDate the last publish date of this calendar booking
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the location of this calendar booking.
	 *
	 * @param location the location of this calendar booking
	 */
	@Override
	public void setLocation(String location) {
		model.setLocation(location);
	}

	/**
	 * Sets the modified date of this calendar booking.
	 *
	 * @param modifiedDate the modified date of this calendar booking
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this calendar booking.
	 *
	 * @param mvccVersion the mvcc version of this calendar booking
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the parent calendar booking ID of this calendar booking.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID of this calendar booking
	 */
	@Override
	public void setParentCalendarBookingId(long parentCalendarBookingId) {
		model.setParentCalendarBookingId(parentCalendarBookingId);
	}

	/**
	 * Sets the primary key of this calendar booking.
	 *
	 * @param primaryKey the primary key of this calendar booking
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the recurrence of this calendar booking.
	 *
	 * @param recurrence the recurrence of this calendar booking
	 */
	@Override
	public void setRecurrence(String recurrence) {
		model.setRecurrence(recurrence);
	}

	/**
	 * Sets the recurring calendar booking ID of this calendar booking.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID of this calendar booking
	 */
	@Override
	public void setRecurringCalendarBookingId(long recurringCalendarBookingId) {
		model.setRecurringCalendarBookingId(recurringCalendarBookingId);
	}

	/**
	 * Sets the second reminder of this calendar booking.
	 *
	 * @param secondReminder the second reminder of this calendar booking
	 */
	@Override
	public void setSecondReminder(long secondReminder) {
		model.setSecondReminder(secondReminder);
	}

	/**
	 * Sets the second reminder type of this calendar booking.
	 *
	 * @param secondReminderType the second reminder type of this calendar booking
	 */
	@Override
	public void setSecondReminderType(String secondReminderType) {
		model.setSecondReminderType(secondReminderType);
	}

	/**
	 * Sets the start time of this calendar booking.
	 *
	 * @param startTime the start time of this calendar booking
	 */
	@Override
	public void setStartTime(long startTime) {
		model.setStartTime(startTime);
	}

	/**
	 * Sets the status of this calendar booking.
	 *
	 * @param status the status of this calendar booking
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this calendar booking.
	 *
	 * @param statusByUserId the status by user ID of this calendar booking
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this calendar booking.
	 *
	 * @param statusByUserName the status by user name of this calendar booking
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this calendar booking.
	 *
	 * @param statusByUserUuid the status by user uuid of this calendar booking
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this calendar booking.
	 *
	 * @param statusDate the status date of this calendar booking
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the title of this calendar booking.
	 *
	 * @param title the title of this calendar booking
	 */
	@Override
	public void setTitle(String title) {
		model.setTitle(title);
	}

	/**
	 * Sets the localized title of this calendar booking in the language.
	 *
	 * @param title the localized title of this calendar booking
	 * @param locale the locale of the language
	 */
	@Override
	public void setTitle(String title, java.util.Locale locale) {
		model.setTitle(title, locale);
	}

	/**
	 * Sets the localized title of this calendar booking in the language, and sets the default locale.
	 *
	 * @param title the localized title of this calendar booking
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitle(
		String title, java.util.Locale locale, java.util.Locale defaultLocale) {

		model.setTitle(title, locale, defaultLocale);
	}

	@Override
	public void setTitleCurrentLanguageId(String languageId) {
		model.setTitleCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized titles of this calendar booking from the map of locales and localized titles.
	 *
	 * @param titleMap the locales and localized titles of this calendar booking
	 */
	@Override
	public void setTitleMap(Map<java.util.Locale, String> titleMap) {
		model.setTitleMap(titleMap);
	}

	/**
	 * Sets the localized titles of this calendar booking from the map of locales and localized titles, and sets the default locale.
	 *
	 * @param titleMap the locales and localized titles of this calendar booking
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitleMap(
		Map<java.util.Locale, String> titleMap,
		java.util.Locale defaultLocale) {

		model.setTitleMap(titleMap, defaultLocale);
	}

	/**
	 * Sets the user ID of this calendar booking.
	 *
	 * @param userId the user ID of this calendar booking
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this calendar booking.
	 *
	 * @param userName the user name of this calendar booking
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this calendar booking.
	 *
	 * @param userUuid the user uuid of this calendar booking
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this calendar booking.
	 *
	 * @param uuid the uuid of this calendar booking
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the v event uid of this calendar booking.
	 *
	 * @param vEventUid the v event uid of this calendar booking
	 */
	@Override
	public void setVEventUid(String vEventUid) {
		model.setVEventUid(vEventUid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<CalendarBooking, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CalendarBooking, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CalendarBookingWrapper wrap(CalendarBooking calendarBooking) {
		return new CalendarBookingWrapper(calendarBooking);
	}

}