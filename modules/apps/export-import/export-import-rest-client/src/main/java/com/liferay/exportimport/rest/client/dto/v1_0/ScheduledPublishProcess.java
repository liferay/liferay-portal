/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.ScheduledPublishProcessSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class ScheduledPublishProcess implements Cloneable, Serializable {

	public static ScheduledPublishProcess toDTO(String json) {
		return ScheduledPublishProcessSerDes.toDTO(json);
	}

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

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

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public Date getNextFireDate() {
		return nextFireDate;
	}

	public void setNextFireDate(Date nextFireDate) {
		this.nextFireDate = nextFireDate;
	}

	public void setNextFireDate(
		UnsafeSupplier<Date, Exception> nextFireDateUnsafeSupplier) {

		try {
			nextFireDate = nextFireDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date nextFireDate;

	public Object getPublishParameters() {
		return publishParameters;
	}

	public void setPublishParameters(Object publishParameters) {
		this.publishParameters = publishParameters;
	}

	public void setPublishParameters(
		UnsafeSupplier<Object, Exception> publishParametersUnsafeSupplier) {

		try {
			publishParameters = publishParametersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object publishParameters;

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

	@Override
	public ScheduledPublishProcess clone() throws CloneNotSupportedException {
		return (ScheduledPublishProcess)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ScheduledPublishProcess)) {
			return false;
		}

		ScheduledPublishProcess scheduledPublishProcess =
			(ScheduledPublishProcess)object;

		return Objects.equals(toString(), scheduledPublishProcess.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ScheduledPublishProcessSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-735704498