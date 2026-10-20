/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.PublishProcessRequestSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class PublishProcessRequest implements Cloneable, Serializable {

	public static PublishProcessRequest toDTO(String json) {
		return PublishProcessRequestSerDes.toDTO(json);
	}

	public Boolean getComments() {
		return comments;
	}

	public void setComments(Boolean comments) {
		this.comments = comments;
	}

	public void setComments(
		UnsafeSupplier<Boolean, Exception> commentsUnsafeSupplier) {

		try {
			comments = commentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean comments;

	public DateRangeType getDateRangeType() {
		return dateRangeType;
	}

	public String getDateRangeTypeAsString() {
		if (dateRangeType == null) {
			return null;
		}

		return dateRangeType.toString();
	}

	public void setDateRangeType(DateRangeType dateRangeType) {
		this.dateRangeType = dateRangeType;
	}

	public void setDateRangeType(
		UnsafeSupplier<DateRangeType, Exception> dateRangeTypeUnsafeSupplier) {

		try {
			dateRangeType = dateRangeTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DateRangeType dateRangeType;

	public Boolean getDeletions() {
		return deletions;
	}

	public void setDeletions(Boolean deletions) {
		this.deletions = deletions;
	}

	public void setDeletions(
		UnsafeSupplier<Boolean, Exception> deletionsUnsafeSupplier) {

		try {
			deletions = deletionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean deletions;

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

	public Boolean getPermissions() {
		return permissions;
	}

	public void setPermissions(Boolean permissions) {
		this.permissions = permissions;
	}

	public void setPermissions(
		UnsafeSupplier<Boolean, Exception> permissionsUnsafeSupplier) {

		try {
			permissions = permissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean permissions;

	public PortletDataHandler[] getPortletDataHandlers() {
		return portletDataHandlers;
	}

	public void setPortletDataHandlers(
		PortletDataHandler[] portletDataHandlers) {

		this.portletDataHandlers = portletDataHandlers;
	}

	public void setPortletDataHandlers(
		UnsafeSupplier<PortletDataHandler[], Exception>
			portletDataHandlersUnsafeSupplier) {

		try {
			portletDataHandlers = portletDataHandlersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PortletDataHandler[] portletDataHandlers;

	public Boolean getRatings() {
		return ratings;
	}

	public void setRatings(Boolean ratings) {
		this.ratings = ratings;
	}

	public void setRatings(
		UnsafeSupplier<Boolean, Exception> ratingsUnsafeSupplier) {

		try {
			ratings = ratingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean ratings;

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

	@Override
	public PublishProcessRequest clone() throws CloneNotSupportedException {
		return (PublishProcessRequest)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PublishProcessRequest)) {
			return false;
		}

		PublishProcessRequest publishProcessRequest =
			(PublishProcessRequest)object;

		return Objects.equals(toString(), publishProcessRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PublishProcessRequestSerDes.toJSON(this);
	}

	public static enum DateRangeType {

		ALL("ALL"), DATE_RANGE("DATE_RANGE"),
		FROM_LAST_PUBLISH_DATE("FROM_LAST_PUBLISH_DATE"), LAST("LAST");

		public static DateRangeType create(String value) {
			for (DateRangeType dateRangeType : values()) {
				if (Objects.equals(dateRangeType.getValue(), value) ||
					Objects.equals(dateRangeType.name(), value)) {

					return dateRangeType;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private DateRangeType(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:-1977898638