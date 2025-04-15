/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.client.dto.v1_0;

import com.liferay.headless.commerce.admin.site.setting.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.site.setting.client.serdes.v1_0.AvailabilityEstimateSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class AvailabilityEstimate implements Cloneable, Serializable {

	public static AvailabilityEstimate toDTO(String json) {
		return AvailabilityEstimateSerDes.toDTO(json);
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void setGroupId(
		UnsafeSupplier<Long, Exception> groupIdUnsafeSupplier) {

		try {
			groupId = groupIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long groupId;

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

	public Double getPriority() {
		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		try {
			priority = priorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double priority;

	public Map<String, String> getTitle() {
		return title;
	}

	public void setTitle(Map<String, String> title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<Map<String, String>, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> title;

	@Override
	public AvailabilityEstimate clone() throws CloneNotSupportedException {
		return (AvailabilityEstimate)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AvailabilityEstimate)) {
			return false;
		}

		AvailabilityEstimate availabilityEstimate =
			(AvailabilityEstimate)object;

		return Objects.equals(toString(), availabilityEstimate.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AvailabilityEstimateSerDes.toJSON(this);
	}

}