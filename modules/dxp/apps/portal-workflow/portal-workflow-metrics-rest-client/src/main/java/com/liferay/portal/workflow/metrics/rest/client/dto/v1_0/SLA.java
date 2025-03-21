/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.SLASerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class SLA implements Cloneable, Serializable {

	public static SLA toDTO(String json) {
		return SLASerDes.toDTO(json);
	}

	public String getCalendarKey() {
		return calendarKey;
	}

	public void setCalendarKey(String calendarKey) {
		this.calendarKey = calendarKey;
	}

	public void setCalendarKey(
		UnsafeSupplier<String, Exception> calendarKeyUnsafeSupplier) {

		try {
			calendarKey = calendarKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String calendarKey;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public void setDuration(
		UnsafeSupplier<Long, Exception> durationUnsafeSupplier) {

		try {
			duration = durationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long duration;

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

	public PauseNodeKeys getPauseNodeKeys() {
		return pauseNodeKeys;
	}

	public void setPauseNodeKeys(PauseNodeKeys pauseNodeKeys) {
		this.pauseNodeKeys = pauseNodeKeys;
	}

	public void setPauseNodeKeys(
		UnsafeSupplier<PauseNodeKeys, Exception> pauseNodeKeysUnsafeSupplier) {

		try {
			pauseNodeKeys = pauseNodeKeysUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PauseNodeKeys pauseNodeKeys;

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public void setProcessId(
		UnsafeSupplier<Long, Exception> processIdUnsafeSupplier) {

		try {
			processId = processIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long processId;

	public StartNodeKeys getStartNodeKeys() {
		return startNodeKeys;
	}

	public void setStartNodeKeys(StartNodeKeys startNodeKeys) {
		this.startNodeKeys = startNodeKeys;
	}

	public void setStartNodeKeys(
		UnsafeSupplier<StartNodeKeys, Exception> startNodeKeysUnsafeSupplier) {

		try {
			startNodeKeys = startNodeKeysUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected StartNodeKeys startNodeKeys;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer status;

	public StopNodeKeys getStopNodeKeys() {
		return stopNodeKeys;
	}

	public void setStopNodeKeys(StopNodeKeys stopNodeKeys) {
		this.stopNodeKeys = stopNodeKeys;
	}

	public void setStopNodeKeys(
		UnsafeSupplier<StopNodeKeys, Exception> stopNodeKeysUnsafeSupplier) {

		try {
			stopNodeKeys = stopNodeKeysUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected StopNodeKeys stopNodeKeys;

	@Override
	public SLA clone() throws CloneNotSupportedException {
		return (SLA)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SLA)) {
			return false;
		}

		SLA sla = (SLA)object;

		return Objects.equals(toString(), sla.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SLASerDes.toJSON(this);
	}

}