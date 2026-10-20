/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.ScheduledPublishProcessRequestSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class ScheduledPublishProcessRequest implements Cloneable, Serializable {

	public static ScheduledPublishProcessRequest toDTO(String json) {
		return ScheduledPublishProcessRequestSerDes.toDTO(json);
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setCronExpression(
		UnsafeSupplier<String, Exception> cronExpressionUnsafeSupplier) {

		try {
			cronExpression = cronExpressionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String cronExpression;

	public PublishProcessRequest getPublishProcessRequest() {
		return publishProcessRequest;
	}

	public void setPublishProcessRequest(
		PublishProcessRequest publishProcessRequest) {

		this.publishProcessRequest = publishProcessRequest;
	}

	public void setPublishProcessRequest(
		UnsafeSupplier<PublishProcessRequest, Exception>
			publishProcessRequestUnsafeSupplier) {

		try {
			publishProcessRequest = publishProcessRequestUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PublishProcessRequest publishProcessRequest;

	public Date getScheduleEndDate() {
		return scheduleEndDate;
	}

	public void setScheduleEndDate(Date scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;
	}

	public void setScheduleEndDate(
		UnsafeSupplier<Date, Exception> scheduleEndDateUnsafeSupplier) {

		try {
			scheduleEndDate = scheduleEndDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date scheduleEndDate;

	public Date getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(Date scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}

	public void setScheduleStartDate(
		UnsafeSupplier<Date, Exception> scheduleStartDateUnsafeSupplier) {

		try {
			scheduleStartDate = scheduleStartDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date scheduleStartDate;

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public void setTimeZoneId(
		UnsafeSupplier<String, Exception> timeZoneIdUnsafeSupplier) {

		try {
			timeZoneId = timeZoneIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String timeZoneId;

	@Override
	public ScheduledPublishProcessRequest clone()
		throws CloneNotSupportedException {

		return (ScheduledPublishProcessRequest)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ScheduledPublishProcessRequest)) {
			return false;
		}

		ScheduledPublishProcessRequest scheduledPublishProcessRequest =
			(ScheduledPublishProcessRequest)object;

		return Objects.equals(
			toString(), scheduledPublishProcessRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ScheduledPublishProcessRequestSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:914498699