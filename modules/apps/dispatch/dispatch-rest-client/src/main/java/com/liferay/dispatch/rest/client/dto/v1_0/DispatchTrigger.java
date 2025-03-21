/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.rest.client.dto.v1_0;

import com.liferay.dispatch.rest.client.function.UnsafeSupplier;
import com.liferay.dispatch.rest.client.serdes.v1_0.DispatchTriggerSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Nilton Vieira
 * @generated
 */
@Generated("")
public class DispatchTrigger implements Cloneable, Serializable {

	public static DispatchTrigger toDTO(String json) {
		return DispatchTriggerSerDes.toDTO(json);
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setCompanyId(
		UnsafeSupplier<Long, Exception> companyIdUnsafeSupplier) {

		try {
			companyId = companyIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long companyId;

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

	public Integer getDispatchTaskClusterMode() {
		return dispatchTaskClusterMode;
	}

	public void setDispatchTaskClusterMode(Integer dispatchTaskClusterMode) {
		this.dispatchTaskClusterMode = dispatchTaskClusterMode;
	}

	public void setDispatchTaskClusterMode(
		UnsafeSupplier<Integer, Exception>
			dispatchTaskClusterModeUnsafeSupplier) {

		try {
			dispatchTaskClusterMode =
				dispatchTaskClusterModeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer dispatchTaskClusterMode;

	public String getDispatchTaskExecutorType() {
		return dispatchTaskExecutorType;
	}

	public void setDispatchTaskExecutorType(String dispatchTaskExecutorType) {
		this.dispatchTaskExecutorType = dispatchTaskExecutorType;
	}

	public void setDispatchTaskExecutorType(
		UnsafeSupplier<String, Exception>
			dispatchTaskExecutorTypeUnsafeSupplier) {

		try {
			dispatchTaskExecutorType =
				dispatchTaskExecutorTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String dispatchTaskExecutorType;

	public Map<String, ?> getDispatchTaskSettings() {
		return dispatchTaskSettings;
	}

	public void setDispatchTaskSettings(Map<String, ?> dispatchTaskSettings) {
		this.dispatchTaskSettings = dispatchTaskSettings;
	}

	public void setDispatchTaskSettings(
		UnsafeSupplier<Map<String, ?>, Exception>
			dispatchTaskSettingsUnsafeSupplier) {

		try {
			dispatchTaskSettings = dispatchTaskSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> dispatchTaskSettings;

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEndDate(
		UnsafeSupplier<Date, Exception> endDateUnsafeSupplier) {

		try {
			endDate = endDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date endDate;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

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

	public Boolean getOverlapAllowed() {
		return overlapAllowed;
	}

	public void setOverlapAllowed(Boolean overlapAllowed) {
		this.overlapAllowed = overlapAllowed;
	}

	public void setOverlapAllowed(
		UnsafeSupplier<Boolean, Exception> overlapAllowedUnsafeSupplier) {

		try {
			overlapAllowed = overlapAllowedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean overlapAllowed;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStartDate(
		UnsafeSupplier<Date, Exception> startDateUnsafeSupplier) {

		try {
			startDate = startDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date startDate;

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		try {
			system = systemUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean system;

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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setUserId(
		UnsafeSupplier<Long, Exception> userIdUnsafeSupplier) {

		try {
			userId = userIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long userId;

	@Override
	public DispatchTrigger clone() throws CloneNotSupportedException {
		return (DispatchTrigger)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DispatchTrigger)) {
			return false;
		}

		DispatchTrigger dispatchTrigger = (DispatchTrigger)object;

		return Objects.equals(toString(), dispatchTrigger.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DispatchTriggerSerDes.toJSON(this);
	}

}