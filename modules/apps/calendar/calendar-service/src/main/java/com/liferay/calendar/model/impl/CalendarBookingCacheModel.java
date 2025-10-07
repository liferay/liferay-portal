/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.model.impl;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing CalendarBooking in entity cache.
 *
 * @author Eduardo Lundgren
 * @generated
 */
public class CalendarBookingCacheModel
	implements CacheModel<CalendarBooking>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CalendarBookingCacheModel)) {
			return false;
		}

		CalendarBookingCacheModel calendarBookingCacheModel =
			(CalendarBookingCacheModel)object;

		if ((calendarBookingId ==
				calendarBookingCacheModel.calendarBookingId) &&
			(mvccVersion == calendarBookingCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, calendarBookingId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(65);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", calendarBookingId=");
		sb.append(calendarBookingId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", calendarId=");
		sb.append(calendarId);
		sb.append(", calendarResourceId=");
		sb.append(calendarResourceId);
		sb.append(", parentCalendarBookingId=");
		sb.append(parentCalendarBookingId);
		sb.append(", recurringCalendarBookingId=");
		sb.append(recurringCalendarBookingId);
		sb.append(", vEventUid=");
		sb.append(vEventUid);
		sb.append(", title=");
		sb.append(title);
		sb.append(", description=");
		sb.append(description);
		sb.append(", location=");
		sb.append(location);
		sb.append(", startTime=");
		sb.append(startTime);
		sb.append(", endTime=");
		sb.append(endTime);
		sb.append(", allDay=");
		sb.append(allDay);
		sb.append(", recurrence=");
		sb.append(recurrence);
		sb.append(", firstReminder=");
		sb.append(firstReminder);
		sb.append(", firstReminderType=");
		sb.append(firstReminderType);
		sb.append(", secondReminder=");
		sb.append(secondReminder);
		sb.append(", secondReminderType=");
		sb.append(secondReminderType);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append(", status=");
		sb.append(status);
		sb.append(", statusByUserId=");
		sb.append(statusByUserId);
		sb.append(", statusByUserName=");
		sb.append(statusByUserName);
		sb.append(", statusDate=");
		sb.append(statusDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CalendarBooking toEntityModel() {
		CalendarBookingImpl calendarBookingImpl = new CalendarBookingImpl();

		calendarBookingImpl.setMvccVersion(mvccVersion);
		calendarBookingImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			calendarBookingImpl.setUuid("");
		}
		else {
			calendarBookingImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			calendarBookingImpl.setExternalReferenceCode("");
		}
		else {
			calendarBookingImpl.setExternalReferenceCode(externalReferenceCode);
		}

		calendarBookingImpl.setCalendarBookingId(calendarBookingId);
		calendarBookingImpl.setGroupId(groupId);
		calendarBookingImpl.setCompanyId(companyId);
		calendarBookingImpl.setUserId(userId);

		if (userName == null) {
			calendarBookingImpl.setUserName("");
		}
		else {
			calendarBookingImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			calendarBookingImpl.setCreateDate(null);
		}
		else {
			calendarBookingImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			calendarBookingImpl.setModifiedDate(null);
		}
		else {
			calendarBookingImpl.setModifiedDate(new Date(modifiedDate));
		}

		calendarBookingImpl.setCalendarId(calendarId);
		calendarBookingImpl.setCalendarResourceId(calendarResourceId);
		calendarBookingImpl.setParentCalendarBookingId(parentCalendarBookingId);
		calendarBookingImpl.setRecurringCalendarBookingId(
			recurringCalendarBookingId);

		if (vEventUid == null) {
			calendarBookingImpl.setVEventUid("");
		}
		else {
			calendarBookingImpl.setVEventUid(vEventUid);
		}

		if (title == null) {
			calendarBookingImpl.setTitle("");
		}
		else {
			calendarBookingImpl.setTitle(title);
		}

		if (description == null) {
			calendarBookingImpl.setDescription("");
		}
		else {
			calendarBookingImpl.setDescription(description);
		}

		if (location == null) {
			calendarBookingImpl.setLocation("");
		}
		else {
			calendarBookingImpl.setLocation(location);
		}

		calendarBookingImpl.setStartTime(startTime);
		calendarBookingImpl.setEndTime(endTime);
		calendarBookingImpl.setAllDay(allDay);

		if (recurrence == null) {
			calendarBookingImpl.setRecurrence("");
		}
		else {
			calendarBookingImpl.setRecurrence(recurrence);
		}

		calendarBookingImpl.setFirstReminder(firstReminder);

		if (firstReminderType == null) {
			calendarBookingImpl.setFirstReminderType("");
		}
		else {
			calendarBookingImpl.setFirstReminderType(firstReminderType);
		}

		calendarBookingImpl.setSecondReminder(secondReminder);

		if (secondReminderType == null) {
			calendarBookingImpl.setSecondReminderType("");
		}
		else {
			calendarBookingImpl.setSecondReminderType(secondReminderType);
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			calendarBookingImpl.setLastPublishDate(null);
		}
		else {
			calendarBookingImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		calendarBookingImpl.setStatus(status);
		calendarBookingImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			calendarBookingImpl.setStatusByUserName("");
		}
		else {
			calendarBookingImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			calendarBookingImpl.setStatusDate(null);
		}
		else {
			calendarBookingImpl.setStatusDate(new Date(statusDate));
		}

		calendarBookingImpl.resetOriginalValues();

		return calendarBookingImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		calendarBookingId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		calendarId = objectInput.readLong();

		calendarResourceId = objectInput.readLong();

		parentCalendarBookingId = objectInput.readLong();

		recurringCalendarBookingId = objectInput.readLong();
		vEventUid = objectInput.readUTF();
		title = objectInput.readUTF();
		description = (String)objectInput.readObject();
		location = objectInput.readUTF();

		startTime = objectInput.readLong();

		endTime = objectInput.readLong();

		allDay = objectInput.readBoolean();
		recurrence = objectInput.readUTF();

		firstReminder = objectInput.readLong();
		firstReminderType = objectInput.readUTF();

		secondReminder = objectInput.readLong();
		secondReminderType = objectInput.readUTF();
		lastPublishDate = objectInput.readLong();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		if (externalReferenceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(externalReferenceCode);
		}

		objectOutput.writeLong(calendarBookingId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(calendarId);

		objectOutput.writeLong(calendarResourceId);

		objectOutput.writeLong(parentCalendarBookingId);

		objectOutput.writeLong(recurringCalendarBookingId);

		if (vEventUid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(vEventUid);
		}

		if (title == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(title);
		}

		if (description == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(description);
		}

		if (location == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(location);
		}

		objectOutput.writeLong(startTime);

		objectOutput.writeLong(endTime);

		objectOutput.writeBoolean(allDay);

		if (recurrence == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(recurrence);
		}

		objectOutput.writeLong(firstReminder);

		if (firstReminderType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(firstReminderType);
		}

		objectOutput.writeLong(secondReminder);

		if (secondReminderType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(secondReminderType);
		}

		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeInt(status);

		objectOutput.writeLong(statusByUserId);

		if (statusByUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(statusByUserName);
		}

		objectOutput.writeLong(statusDate);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long calendarBookingId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long calendarId;
	public long calendarResourceId;
	public long parentCalendarBookingId;
	public long recurringCalendarBookingId;
	public String vEventUid;
	public String title;
	public String description;
	public String location;
	public long startTime;
	public long endTime;
	public boolean allDay;
	public String recurrence;
	public long firstReminder;
	public String firstReminderType;
	public long secondReminder;
	public String secondReminderType;
	public long lastPublishDate;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}