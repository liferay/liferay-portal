/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName("PublishProcessRequest")
@io.swagger.v3.oas.annotations.media.Schema(requiredProperties = {"name"})
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PublishProcessRequest")
public class PublishProcessRequest implements Serializable {

	public static PublishProcessRequest toDTO(String json) {
		return ObjectMapperUtil.readValue(PublishProcessRequest.class, json);
	}

	public static PublishProcessRequest unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PublishProcessRequest.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getComments() {
		if (_commentsSupplier != null) {
			comments = _commentsSupplier.get();

			_commentsSupplier = null;
		}

		return comments;
	}

	public void setComments(Boolean comments) {
		this.comments = comments;

		_commentsSupplier = null;
	}

	@JsonIgnore
	public void setComments(
		UnsafeSupplier<Boolean, Exception> commentsUnsafeSupplier) {

		_commentsSupplier = () -> {
			try {
				return commentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean comments;

	@JsonIgnore
	private Supplier<Boolean> _commentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A cron expression. When set, the publish is scheduled to recur instead of running immediately."
	)
	public String getCronExpression() {
		if (_cronExpressionSupplier != null) {
			cronExpression = _cronExpressionSupplier.get();

			_cronExpressionSupplier = null;
		}

		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;

		_cronExpressionSupplier = null;
	}

	@JsonIgnore
	public void setCronExpression(
		UnsafeSupplier<String, Exception> cronExpressionUnsafeSupplier) {

		_cronExpressionSupplier = () -> {
			try {
				return cronExpressionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A cron expression. When set, the publish is scheduled to recur instead of running immediately."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String cronExpression;

	@JsonIgnore
	private Supplier<String> _cronExpressionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The recipe used to resolve the publish window at every run, relaunch, and scheduled occurrence. ALL publishes everything, DATE_RANGE publishes the window between the start and end dates, resolving a missing end date as the run time, LAST publishes a window of whole hours ending at the run time, derived from the start date, and FROM_LAST_PUBLISH_DATE publishes the changes recorded since each entity's own last publish. When absent, the type is inferred from the given dates."
	)
	@JsonGetter("dateRangeType")
	@Valid
	public DateRangeType getDateRangeType() {
		if (_dateRangeTypeSupplier != null) {
			dateRangeType = _dateRangeTypeSupplier.get();

			_dateRangeTypeSupplier = null;
		}

		return dateRangeType;
	}

	@JsonIgnore
	public String getDateRangeTypeAsString() {
		DateRangeType dateRangeType = getDateRangeType();

		if (dateRangeType == null) {
			return null;
		}

		return dateRangeType.toString();
	}

	public void setDateRangeType(DateRangeType dateRangeType) {
		this.dateRangeType = dateRangeType;

		_dateRangeTypeSupplier = null;
	}

	@JsonIgnore
	public void setDateRangeType(
		UnsafeSupplier<DateRangeType, Exception> dateRangeTypeUnsafeSupplier) {

		_dateRangeTypeSupplier = () -> {
			try {
				return dateRangeTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The recipe used to resolve the publish window at every run, relaunch, and scheduled occurrence. ALL publishes everything, DATE_RANGE publishes the window between the start and end dates, resolving a missing end date as the run time, LAST publishes a window of whole hours ending at the run time, derived from the start date, and FROM_LAST_PUBLISH_DATE publishes the changes recorded since each entity's own last publish. When absent, the type is inferred from the given dates."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DateRangeType dateRangeType;

	@JsonIgnore
	private Supplier<DateRangeType> _dateRangeTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getDeletions() {
		if (_deletionsSupplier != null) {
			deletions = _deletionsSupplier.get();

			_deletionsSupplier = null;
		}

		return deletions;
	}

	public void setDeletions(Boolean deletions) {
		this.deletions = deletions;

		_deletionsSupplier = null;
	}

	@JsonIgnore
	public void setDeletions(
		UnsafeSupplier<Boolean, Exception> deletionsUnsafeSupplier) {

		_deletionsSupplier = () -> {
			try {
				return deletionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean deletions;

	@JsonIgnore
	private Supplier<Boolean> _deletionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getEndDate() {
		if (_endDateSupplier != null) {
			endDate = _endDateSupplier.get();

			_endDateSupplier = null;
		}

		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;

		_endDateSupplier = null;
	}

	@JsonIgnore
	public void setEndDate(
		UnsafeSupplier<Date, Exception> endDateUnsafeSupplier) {

		_endDateSupplier = () -> {
			try {
				return endDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date endDate;

	@JsonIgnore
	private Supplier<Date> _endDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getLogo() {
		if (_logoSupplier != null) {
			logo = _logoSupplier.get();

			_logoSupplier = null;
		}

		return logo;
	}

	public void setLogo(Boolean logo) {
		this.logo = logo;

		_logoSupplier = null;
	}

	@JsonIgnore
	public void setLogo(UnsafeSupplier<Boolean, Exception> logoUnsafeSupplier) {
		_logoSupplier = () -> {
			try {
				return logoUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean logo;

	@JsonIgnore
	private Supplier<Boolean> _logoSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getPermissions() {
		if (_permissionsSupplier != null) {
			permissions = _permissionsSupplier.get();

			_permissionsSupplier = null;
		}

		return permissions;
	}

	public void setPermissions(Boolean permissions) {
		this.permissions = permissions;

		_permissionsSupplier = null;
	}

	@JsonIgnore
	public void setPermissions(
		UnsafeSupplier<Boolean, Exception> permissionsUnsafeSupplier) {

		_permissionsSupplier = () -> {
			try {
				return permissionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean permissions;

	@JsonIgnore
	private Supplier<Boolean> _permissionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getRatings() {
		if (_ratingsSupplier != null) {
			ratings = _ratingsSupplier.get();

			_ratingsSupplier = null;
		}

		return ratings;
	}

	public void setRatings(Boolean ratings) {
		this.ratings = ratings;

		_ratingsSupplier = null;
	}

	@JsonIgnore
	public void setRatings(
		UnsafeSupplier<Boolean, Exception> ratingsUnsafeSupplier) {

		_ratingsSupplier = () -> {
			try {
				return ratingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean ratings;

	@JsonIgnore
	private Supplier<Boolean> _ratingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public RequestPortletDataHandler[] getRequestPortletDataHandlers() {
		if (_requestPortletDataHandlersSupplier != null) {
			requestPortletDataHandlers =
				_requestPortletDataHandlersSupplier.get();

			_requestPortletDataHandlersSupplier = null;
		}

		return requestPortletDataHandlers;
	}

	public void setRequestPortletDataHandlers(
		RequestPortletDataHandler[] requestPortletDataHandlers) {

		this.requestPortletDataHandlers = requestPortletDataHandlers;

		_requestPortletDataHandlersSupplier = null;
	}

	@JsonIgnore
	public void setRequestPortletDataHandlers(
		UnsafeSupplier<RequestPortletDataHandler[], Exception>
			requestPortletDataHandlersUnsafeSupplier) {

		_requestPortletDataHandlersSupplier = () -> {
			try {
				return requestPortletDataHandlersUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected RequestPortletDataHandler[] requestPortletDataHandlers;

	@JsonIgnore
	private Supplier<RequestPortletDataHandler[]>
		_requestPortletDataHandlersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the scheduled publish stops recurring. Applies only when a cron expression is given."
	)
	public Date getScheduleEndDate() {
		if (_scheduleEndDateSupplier != null) {
			scheduleEndDate = _scheduleEndDateSupplier.get();

			_scheduleEndDateSupplier = null;
		}

		return scheduleEndDate;
	}

	public void setScheduleEndDate(Date scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;

		_scheduleEndDateSupplier = null;
	}

	@JsonIgnore
	public void setScheduleEndDate(
		UnsafeSupplier<Date, Exception> scheduleEndDateUnsafeSupplier) {

		_scheduleEndDateSupplier = () -> {
			try {
				return scheduleEndDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "When the scheduled publish stops recurring. Applies only when a cron expression is given."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date scheduleEndDate;

	@JsonIgnore
	private Supplier<Date> _scheduleEndDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the scheduled publish starts. Applies only when a cron expression is given. Defaults to now."
	)
	public Date getScheduleStartDate() {
		if (_scheduleStartDateSupplier != null) {
			scheduleStartDate = _scheduleStartDateSupplier.get();

			_scheduleStartDateSupplier = null;
		}

		return scheduleStartDate;
	}

	public void setScheduleStartDate(Date scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;

		_scheduleStartDateSupplier = null;
	}

	@JsonIgnore
	public void setScheduleStartDate(
		UnsafeSupplier<Date, Exception> scheduleStartDateUnsafeSupplier) {

		_scheduleStartDateSupplier = () -> {
			try {
				return scheduleStartDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "When the scheduled publish starts. Applies only when a cron expression is given. Defaults to now."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date scheduleStartDate;

	@JsonIgnore
	private Supplier<Date> _scheduleStartDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSitePagesSettings() {
		if (_sitePagesSettingsSupplier != null) {
			sitePagesSettings = _sitePagesSettingsSupplier.get();

			_sitePagesSettingsSupplier = null;
		}

		return sitePagesSettings;
	}

	public void setSitePagesSettings(Boolean sitePagesSettings) {
		this.sitePagesSettings = sitePagesSettings;

		_sitePagesSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSitePagesSettings(
		UnsafeSupplier<Boolean, Exception> sitePagesSettingsUnsafeSupplier) {

		_sitePagesSettingsSupplier = () -> {
			try {
				return sitePagesSettingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean sitePagesSettings;

	@JsonIgnore
	private Supplier<Boolean> _sitePagesSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSiteTemplateSettings() {
		if (_siteTemplateSettingsSupplier != null) {
			siteTemplateSettings = _siteTemplateSettingsSupplier.get();

			_siteTemplateSettingsSupplier = null;
		}

		return siteTemplateSettings;
	}

	public void setSiteTemplateSettings(Boolean siteTemplateSettings) {
		this.siteTemplateSettings = siteTemplateSettings;

		_siteTemplateSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSiteTemplateSettings(
		UnsafeSupplier<Boolean, Exception> siteTemplateSettingsUnsafeSupplier) {

		_siteTemplateSettingsSupplier = () -> {
			try {
				return siteTemplateSettingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean siteTemplateSettings;

	@JsonIgnore
	private Supplier<Boolean> _siteTemplateSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getStartDate() {
		if (_startDateSupplier != null) {
			startDate = _startDateSupplier.get();

			_startDateSupplier = null;
		}

		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;

		_startDateSupplier = null;
	}

	@JsonIgnore
	public void setStartDate(
		UnsafeSupplier<Date, Exception> startDateUnsafeSupplier) {

		_startDateSupplier = () -> {
			try {
				return startDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date startDate;

	@JsonIgnore
	private Supplier<Date> _startDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getThemeSettings() {
		if (_themeSettingsSupplier != null) {
			themeSettings = _themeSettingsSupplier.get();

			_themeSettingsSupplier = null;
		}

		return themeSettings;
	}

	public void setThemeSettings(Boolean themeSettings) {
		this.themeSettings = themeSettings;

		_themeSettingsSupplier = null;
	}

	@JsonIgnore
	public void setThemeSettings(
		UnsafeSupplier<Boolean, Exception> themeSettingsUnsafeSupplier) {

		_themeSettingsSupplier = () -> {
			try {
				return themeSettingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean themeSettings;

	@JsonIgnore
	private Supplier<Boolean> _themeSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The time zone the cron expression is evaluated in. Defaults to the server time zone."
	)
	public String getTimeZoneId() {
		if (_timeZoneIdSupplier != null) {
			timeZoneId = _timeZoneIdSupplier.get();

			_timeZoneIdSupplier = null;
		}

		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;

		_timeZoneIdSupplier = null;
	}

	@JsonIgnore
	public void setTimeZoneId(
		UnsafeSupplier<String, Exception> timeZoneIdUnsafeSupplier) {

		_timeZoneIdSupplier = () -> {
			try {
				return timeZoneIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The time zone the cron expression is evaluated in. Defaults to the server time zone."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String timeZoneId;

	@JsonIgnore
	private Supplier<String> _timeZoneIdSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Boolean comments = getComments();

		if (comments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(comments);
		}

		String cronExpression = getCronExpression();

		if (cronExpression != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cronExpression\": ");

			sb.append("\"");

			sb.append(_escape(cronExpression));

			sb.append("\"");
		}

		DateRangeType dateRangeType = getDateRangeType();

		if (dateRangeType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateRangeType\": ");

			sb.append("\"");
			sb.append(dateRangeType);
			sb.append("\"");
		}

		Boolean deletions = getDeletions();

		if (deletions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(deletions);
		}

		Date endDate = getEndDate();

		if (endDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(endDate));

			sb.append("\"");
		}

		Boolean logo = getLogo();

		if (logo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logo\": ");

			sb.append(logo);
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Boolean permissions = getPermissions();

		if (permissions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append(permissions);
		}

		Boolean ratings = getRatings();

		if (ratings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(ratings);
		}

		RequestPortletDataHandler[] requestPortletDataHandlers =
			getRequestPortletDataHandlers();

		if (requestPortletDataHandlers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestPortletDataHandlers\": ");

			sb.append("[");

			for (int i = 0; i < requestPortletDataHandlers.length; i++) {
				sb.append(String.valueOf(requestPortletDataHandlers[i]));

				if ((i + 1) < requestPortletDataHandlers.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date scheduleEndDate = getScheduleEndDate();

		if (scheduleEndDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleEndDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(scheduleEndDate));

			sb.append("\"");
		}

		Date scheduleStartDate = getScheduleStartDate();

		if (scheduleStartDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleStartDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(scheduleStartDate));

			sb.append("\"");
		}

		Boolean sitePagesSettings = getSitePagesSettings();

		if (sitePagesSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePagesSettings\": ");

			sb.append(sitePagesSettings);
		}

		Boolean siteTemplateSettings = getSiteTemplateSettings();

		if (siteTemplateSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteTemplateSettings\": ");

			sb.append(siteTemplateSettings);
		}

		Date startDate = getStartDate();

		if (startDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(startDate));

			sb.append("\"");
		}

		Boolean themeSettings = getThemeSettings();

		if (themeSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeSettings\": ");

			sb.append(themeSettings);
		}

		String timeZoneId = getTimeZoneId();

		if (timeZoneId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timeZoneId\": ");

			sb.append("\"");

			sb.append(_escape(timeZoneId));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.PublishProcessRequest",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("DateRangeType")
	public static enum DateRangeType {

		ALL("ALL"), DATE_RANGE("DATE_RANGE"),
		FROM_LAST_PUBLISH_DATE("FROM_LAST_PUBLISH_DATE"), LAST("LAST");

		@JsonCreator
		public static DateRangeType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (DateRangeType dateRangeType : values()) {
				if (Objects.equals(dateRangeType.getValue(), value)) {
					return dateRangeType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
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

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:1438411992