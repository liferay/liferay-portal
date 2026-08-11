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

	public Boolean getLogo() {
		return logo;
	}

	public void setLogo(Boolean logo) {
		this.logo = logo;
	}

	public void setLogo(UnsafeSupplier<Boolean, Exception> logoUnsafeSupplier) {
		try {
			logo = logoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean logo;

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

	public RequestPortletDataHandler[] getRequestPortletDataHandlers() {
		return requestPortletDataHandlers;
	}

	public void setRequestPortletDataHandlers(
		RequestPortletDataHandler[] requestPortletDataHandlers) {

		this.requestPortletDataHandlers = requestPortletDataHandlers;
	}

	public void setRequestPortletDataHandlers(
		UnsafeSupplier<RequestPortletDataHandler[], Exception>
			requestPortletDataHandlersUnsafeSupplier) {

		try {
			requestPortletDataHandlers =
				requestPortletDataHandlersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RequestPortletDataHandler[] requestPortletDataHandlers;

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

	public Boolean getSitePagesSettings() {
		return sitePagesSettings;
	}

	public void setSitePagesSettings(Boolean sitePagesSettings) {
		this.sitePagesSettings = sitePagesSettings;
	}

	public void setSitePagesSettings(
		UnsafeSupplier<Boolean, Exception> sitePagesSettingsUnsafeSupplier) {

		try {
			sitePagesSettings = sitePagesSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean sitePagesSettings;

	public Boolean getSiteTemplateSettings() {
		return siteTemplateSettings;
	}

	public void setSiteTemplateSettings(Boolean siteTemplateSettings) {
		this.siteTemplateSettings = siteTemplateSettings;
	}

	public void setSiteTemplateSettings(
		UnsafeSupplier<Boolean, Exception> siteTemplateSettingsUnsafeSupplier) {

		try {
			siteTemplateSettings = siteTemplateSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean siteTemplateSettings;

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

	public Boolean getThemeSettings() {
		return themeSettings;
	}

	public void setThemeSettings(Boolean themeSettings) {
		this.themeSettings = themeSettings;
	}

	public void setThemeSettings(
		UnsafeSupplier<Boolean, Exception> themeSettingsUnsafeSupplier) {

		try {
			themeSettings = themeSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean themeSettings;

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
// LIFERAY-REST-BUILDER-HASH:814828940