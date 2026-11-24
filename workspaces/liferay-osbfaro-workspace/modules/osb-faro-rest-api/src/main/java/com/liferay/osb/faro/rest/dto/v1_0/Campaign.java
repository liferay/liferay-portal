/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A marketing campaign: a CRM campaign synced from Salesforce, or a campaign that LDP tracks from the UTM campaign tag of site visits.",
	value = "Campaign"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A marketing campaign: a CRM campaign synced from Salesforce, or a campaign that LDP tracks from the UTM campaign tag of site visits."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Campaign")
public class Campaign implements Serializable {

	public static Campaign toDTO(String json) {
		return ObjectMapperUtil.readValue(Campaign.class, json);
	}

	public static Campaign unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Campaign.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of distinct accounts the campaign touched. In `getWorkspaceGroupChannelCampaignsPage` it counts the touches in the requested date range; in `getWorkspaceGroupChannelCampaign` it counts every touch, with no date range."
	)
	public Long getAccountsTouched() {
		if (_accountsTouchedSupplier != null) {
			accountsTouched = _accountsTouchedSupplier.get();

			_accountsTouchedSupplier = null;
		}

		return accountsTouched;
	}

	public void setAccountsTouched(Long accountsTouched) {
		this.accountsTouched = accountsTouched;

		_accountsTouchedSupplier = null;
	}

	@JsonIgnore
	public void setAccountsTouched(
		UnsafeSupplier<Long, Exception> accountsTouchedUnsafeSupplier) {

		_accountsTouchedSupplier = () -> {
			try {
				return accountsTouchedUnsafeSupplier.get();
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
		description = "Number of distinct accounts the campaign touched. In `getWorkspaceGroupChannelCampaignsPage` it counts the touches in the requested date range; in `getWorkspaceGroupChannelCampaign` it counts every touch, with no date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long accountsTouched;

	@JsonIgnore
	private Supplier<Long> _accountsTouchedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Campaign type as set in the CRM, such as Email or Webinar. Null for campaigns tracked from site visits."
	)
	public String getCampaignType() {
		if (_campaignTypeSupplier != null) {
			campaignType = _campaignTypeSupplier.get();

			_campaignTypeSupplier = null;
		}

		return campaignType;
	}

	public void setCampaignType(String campaignType) {
		this.campaignType = campaignType;

		_campaignTypeSupplier = null;
	}

	@JsonIgnore
	public void setCampaignType(
		UnsafeSupplier<String, Exception> campaignTypeUnsafeSupplier) {

		_campaignTypeSupplier = () -> {
			try {
				return campaignTypeUnsafeSupplier.get();
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
		description = "Campaign type as set in the CRM, such as Email or Webinar. Null for campaigns tracked from site visits."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String campaignType;

	@JsonIgnore
	private Supplier<String> _campaignTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Planned end date of the campaign as set in the CRM. Null when none is set."
	)
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

	@GraphQLField(
		description = "Planned end date of the campaign as set in the CRM. Null when none is set."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date endDate;

	@JsonIgnore
	private Supplier<Date> _endDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Campaign ID. Use it as `campaignId` in the other campaign tools."
	)
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
		description = "Campaign ID. Use it as `campaignId` in the other campaign tools."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of distinct individuals the campaign touched, known or anonymous. Counted over the same window as `accountsTouched`."
	)
	public Long getIndividualsTouched() {
		if (_individualsTouchedSupplier != null) {
			individualsTouched = _individualsTouchedSupplier.get();

			_individualsTouchedSupplier = null;
		}

		return individualsTouched;
	}

	public void setIndividualsTouched(Long individualsTouched) {
		this.individualsTouched = individualsTouched;

		_individualsTouchedSupplier = null;
	}

	@JsonIgnore
	public void setIndividualsTouched(
		UnsafeSupplier<Long, Exception> individualsTouchedUnsafeSupplier) {

		_individualsTouchedSupplier = () -> {
			try {
				return individualsTouchedUnsafeSupplier.get();
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
		description = "Number of distinct individuals the campaign touched, known or anonymous. Counted over the same window as `accountsTouched`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long individualsTouched;

	@JsonIgnore
	private Supplier<Long> _individualsTouchedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the campaign last touched an account in the requested date range. Only `getWorkspaceGroupChannelCampaignsPage` returns it; it is null in `getWorkspaceGroupChannelCampaign`."
	)
	public Date getLastTouchDate() {
		if (_lastTouchDateSupplier != null) {
			lastTouchDate = _lastTouchDateSupplier.get();

			_lastTouchDateSupplier = null;
		}

		return lastTouchDate;
	}

	public void setLastTouchDate(Date lastTouchDate) {
		this.lastTouchDate = lastTouchDate;

		_lastTouchDateSupplier = null;
	}

	@JsonIgnore
	public void setLastTouchDate(
		UnsafeSupplier<Date, Exception> lastTouchDateUnsafeSupplier) {

		_lastTouchDateSupplier = () -> {
			try {
				return lastTouchDateUnsafeSupplier.get();
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
		description = "When the campaign last touched an account in the requested date range. Only `getWorkspaceGroupChannelCampaignsPage` returns it; it is null in `getWorkspaceGroupChannelCampaign`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date lastTouchDate;

	@JsonIgnore
	private Supplier<Date> _lastTouchDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the campaign."
	)
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

	@GraphQLField(description = "Name of the campaign.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Where the campaign comes from: SALESFORCE for a CRM campaign, or ANALYTICS for a campaign tracked from the UTM campaign tag of site visits."
	)
	public String getOrigin() {
		if (_originSupplier != null) {
			origin = _originSupplier.get();

			_originSupplier = null;
		}

		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;

		_originSupplier = null;
	}

	@JsonIgnore
	public void setOrigin(
		UnsafeSupplier<String, Exception> originUnsafeSupplier) {

		_originSupplier = () -> {
			try {
				return originUnsafeSupplier.get();
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
		description = "Where the campaign comes from: SALESFORCE for a CRM campaign, or ANALYTICS for a campaign tracked from the UTM campaign tag of site visits."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String origin;

	@JsonIgnore
	private Supplier<String> _originSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Planned start date of the campaign as set in the CRM. Null when none is set."
	)
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

	@GraphQLField(
		description = "Planned start date of the campaign as set in the CRM. Null when none is set."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date startDate;

	@JsonIgnore
	private Supplier<Date> _startDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Campaign status as set in the CRM, such as Planned, In Progress or Completed. Null for campaigns tracked from site visits."
	)
	public String getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(String status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
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
		description = "Campaign status as set in the CRM, such as Planned, In Progress or Completed. Null for campaigns tracked from site visits."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String status;

	@JsonIgnore
	private Supplier<String> _statusSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Campaign)) {
			return false;
		}

		Campaign campaign = (Campaign)object;

		return Objects.equals(toString(), campaign.toString());
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

		Long accountsTouched = getAccountsTouched();

		if (accountsTouched != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountsTouched\": ");

			sb.append(accountsTouched);
		}

		String campaignType = getCampaignType();

		if (campaignType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"campaignType\": ");

			sb.append("\"");

			sb.append(_escape(campaignType));

			sb.append("\"");
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

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
		}

		Long individualsTouched = getIndividualsTouched();

		if (individualsTouched != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualsTouched\": ");

			sb.append(individualsTouched);
		}

		Date lastTouchDate = getLastTouchDate();

		if (lastTouchDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastTouchDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(lastTouchDate));

			sb.append("\"");
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

		String origin = getOrigin();

		if (origin != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"origin\": ");

			sb.append("\"");

			sb.append(_escape(origin));

			sb.append("\"");
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

		String status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(status));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.Campaign",
		name = "x-class-name"
	)
	public String xClassName;

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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:277645736