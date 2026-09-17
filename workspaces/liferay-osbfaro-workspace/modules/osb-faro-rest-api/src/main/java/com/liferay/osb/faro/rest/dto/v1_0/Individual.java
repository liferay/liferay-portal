/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

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
	description = "A single individual synced to Analytics Cloud. Individuals are aggregated from data sources configured to sync to Analytics Cloud, and may be either 'known' (identified by email) or 'anonymous' (browser-tracked only).",
	value = "Individual"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A single individual synced to Analytics Cloud. Individuals are aggregated from data sources configured to sync to Analytics Cloud, and may be either 'known' (identified by email) or 'anonymous' (browser-tracked only)."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Individual")
public class Individual implements Serializable {

	public static Individual toDTO(String json) {
		return ObjectMapperUtil.readValue(Individual.class, json);
	}

	public static Individual unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Individual.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of the account this Individual is primarily associated with."
	)
	public String getAccountName() {
		if (_accountNameSupplier != null) {
			accountName = _accountNameSupplier.get();

			_accountNameSupplier = null;
		}

		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;

		_accountNameSupplier = null;
	}

	@JsonIgnore
	public void setAccountName(
		UnsafeSupplier<String, Exception> accountNameUnsafeSupplier) {

		_accountNameSupplier = () -> {
			try {
				return accountNameUnsafeSupplier.get();
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
		description = "Display name of the account this Individual is primarily associated with."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String accountName;

	@JsonIgnore
	private Supplier<String> _accountNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total number of tracked activities attributed to this individual."
	)
	public Long getActivitiesCount() {
		if (_activitiesCountSupplier != null) {
			activitiesCount = _activitiesCountSupplier.get();

			_activitiesCountSupplier = null;
		}

		return activitiesCount;
	}

	public void setActivitiesCount(Long activitiesCount) {
		this.activitiesCount = activitiesCount;

		_activitiesCountSupplier = null;
	}

	@JsonIgnore
	public void setActivitiesCount(
		UnsafeSupplier<Long, Exception> activitiesCountUnsafeSupplier) {

		_activitiesCountSupplier = () -> {
			try {
				return activitiesCountUnsafeSupplier.get();
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
		description = "Total number of tracked activities attributed to this individual."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long activitiesCount;

	@JsonIgnore
	private Supplier<Long> _activitiesCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Engagement classification computed by Analytics Cloud from the individual's recent activity. The set of values is defined by the analytics engine."
	)
	public String getActivityStatus() {
		if (_activityStatusSupplier != null) {
			activityStatus = _activityStatusSupplier.get();

			_activityStatusSupplier = null;
		}

		return activityStatus;
	}

	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;

		_activityStatusSupplier = null;
	}

	@JsonIgnore
	public void setActivityStatus(
		UnsafeSupplier<String, Exception> activityStatusUnsafeSupplier) {

		_activityStatusSupplier = () -> {
			try {
				return activityStatusUnsafeSupplier.get();
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
		description = "Engagement classification computed by Analytics Cloud from the individual's recent activity. The set of values is defined by the analytics engine."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String activityStatus;

	@JsonIgnore
	private Supplier<String> _activityStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Average duration of the individual's sessions, in milliseconds."
	)
	public Long getAverageSessionDuration() {
		if (_averageSessionDurationSupplier != null) {
			averageSessionDuration = _averageSessionDurationSupplier.get();

			_averageSessionDurationSupplier = null;
		}

		return averageSessionDuration;
	}

	public void setAverageSessionDuration(Long averageSessionDuration) {
		this.averageSessionDuration = averageSessionDuration;

		_averageSessionDurationSupplier = null;
	}

	@JsonIgnore
	public void setAverageSessionDuration(
		UnsafeSupplier<Long, Exception> averageSessionDurationUnsafeSupplier) {

		_averageSessionDurationSupplier = () -> {
			try {
				return averageSessionDurationUnsafeSupplier.get();
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
		description = "Average duration of the individual's sessions, in milliseconds."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long averageSessionDuration;

	@JsonIgnore
	private Supplier<Long> _averageSessionDurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "First time this Individual was observed by Analytics Cloud."
	)
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
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
		description = "First time this Individual was observed by Analytics Cloud."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Last time this individual's profile was modified."
	)
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
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
		description = "Last time this individual's profile was modified."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Per-category demographic attributes attached to this Individual. The map key is the demographic category (e.g. 'profile', 'address', 'employment'), and each value is a list of attribute records. Use `IndividualDemographicField.fieldType` to interpret each `value`."
	)
	@Valid
	public Object getDemographics() {
		if (_demographicsSupplier != null) {
			demographics = _demographicsSupplier.get();

			_demographicsSupplier = null;
		}

		return demographics;
	}

	public void setDemographics(Object demographics) {
		this.demographics = demographics;

		_demographicsSupplier = null;
	}

	@JsonIgnore
	public void setDemographics(
		UnsafeSupplier<Object, Exception> demographicsUnsafeSupplier) {

		_demographicsSupplier = () -> {
			try {
				return demographicsUnsafeSupplier.get();
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
		description = "Per-category demographic attributes attached to this Individual. The map key is the demographic category (e.g. 'profile', 'address', 'employment'), and each value is a list of attribute records. Use `IndividualDemographicField.fieldType` to interpret each `value`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Object demographics;

	@JsonIgnore
	private Supplier<Object> _demographicsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Email address of a known individual, taken from the 'email' demographic attribute. Null for anonymous individuals or when no data source supplies one."
	)
	public String getEmailAddress() {
		if (_emailAddressSupplier != null) {
			emailAddress = _emailAddressSupplier.get();

			_emailAddressSupplier = null;
		}

		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;

		_emailAddressSupplier = null;
	}

	@JsonIgnore
	public void setEmailAddress(
		UnsafeSupplier<String, Exception> emailAddressUnsafeSupplier) {

		_emailAddressSupplier = () -> {
			try {
				return emailAddressUnsafeSupplier.get();
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
		description = "Email address of a known individual, taken from the 'email' demographic attribute. Null for anonymous individuals or when no data source supplies one."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String emailAddress;

	@JsonIgnore
	private Supplier<String> _emailAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date of the earliest tracked activity for this individual."
	)
	public Date getFirstActivityDate() {
		if (_firstActivityDateSupplier != null) {
			firstActivityDate = _firstActivityDateSupplier.get();

			_firstActivityDateSupplier = null;
		}

		return firstActivityDate;
	}

	public void setFirstActivityDate(Date firstActivityDate) {
		this.firstActivityDate = firstActivityDate;

		_firstActivityDateSupplier = null;
	}

	@JsonIgnore
	public void setFirstActivityDate(
		UnsafeSupplier<Date, Exception> firstActivityDateUnsafeSupplier) {

		_firstActivityDateSupplier = () -> {
			try {
				return firstActivityDateUnsafeSupplier.get();
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
		description = "Date of the earliest tracked activity for this individual."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date firstActivityDate;

	@JsonIgnore
	private Supplier<Date> _firstActivityDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Individual ID. Use this with `getWorkspaceGroupIndividual` to fetch a single individual."
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
		description = "Individual ID. Use this with `getWorkspaceGroupIndividual` to fetch a single individual."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date the individual stopped being anonymous and became known."
	)
	public Date getKnownSinceDate() {
		if (_knownSinceDateSupplier != null) {
			knownSinceDate = _knownSinceDateSupplier.get();

			_knownSinceDateSupplier = null;
		}

		return knownSinceDate;
	}

	public void setKnownSinceDate(Date knownSinceDate) {
		this.knownSinceDate = knownSinceDate;

		_knownSinceDateSupplier = null;
	}

	@JsonIgnore
	public void setKnownSinceDate(
		UnsafeSupplier<Date, Exception> knownSinceDateUnsafeSupplier) {

		_knownSinceDateSupplier = () -> {
			try {
				return knownSinceDateUnsafeSupplier.get();
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
		description = "Date the individual stopped being anonymous and became known."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date knownSinceDate;

	@JsonIgnore
	private Supplier<Date> _knownSinceDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date of the most recent tracked activity for this individual."
	)
	public Date getLastActivityDate() {
		if (_lastActivityDateSupplier != null) {
			lastActivityDate = _lastActivityDateSupplier.get();

			_lastActivityDateSupplier = null;
		}

		return lastActivityDate;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		this.lastActivityDate = lastActivityDate;

		_lastActivityDateSupplier = null;
	}

	@JsonIgnore
	public void setLastActivityDate(
		UnsafeSupplier<Date, Exception> lastActivityDateUnsafeSupplier) {

		_lastActivityDateSupplier = () -> {
			try {
				return lastActivityDateUnsafeSupplier.get();
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
		description = "Date of the most recent tracked activity for this individual."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date lastActivityDate;

	@JsonIgnore
	private Supplier<Date> _lastActivityDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Country detected for the individual's most recent session."
	)
	public String getLastSessionCountry() {
		if (_lastSessionCountrySupplier != null) {
			lastSessionCountry = _lastSessionCountrySupplier.get();

			_lastSessionCountrySupplier = null;
		}

		return lastSessionCountry;
	}

	public void setLastSessionCountry(String lastSessionCountry) {
		this.lastSessionCountry = lastSessionCountry;

		_lastSessionCountrySupplier = null;
	}

	@JsonIgnore
	public void setLastSessionCountry(
		UnsafeSupplier<String, Exception> lastSessionCountryUnsafeSupplier) {

		_lastSessionCountrySupplier = () -> {
			try {
				return lastSessionCountryUnsafeSupplier.get();
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
		description = "Country detected for the individual's most recent session."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String lastSessionCountry;

	@JsonIgnore
	private Supplier<String> _lastSessionCountrySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of a known individual, built from the 'givenName' and 'familyName' demographic attributes. Null when neither is available."
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

	@GraphQLField(
		description = "Display name of a known individual, built from the 'givenName' and 'familyName' demographic attributes. Null when neither is available."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Profile classification. KNOWN individuals are identified by email/account; ANONYMOUS individuals are browser-tracked only."
	)
	@JsonGetter("profileType")
	@Valid
	public ProfileType getProfileType() {
		if (_profileTypeSupplier != null) {
			profileType = _profileTypeSupplier.get();

			_profileTypeSupplier = null;
		}

		return profileType;
	}

	@JsonIgnore
	public String getProfileTypeAsString() {
		ProfileType profileType = getProfileType();

		if (profileType == null) {
			return null;
		}

		return profileType.toString();
	}

	public void setProfileType(ProfileType profileType) {
		this.profileType = profileType;

		_profileTypeSupplier = null;
	}

	@JsonIgnore
	public void setProfileType(
		UnsafeSupplier<ProfileType, Exception> profileTypeUnsafeSupplier) {

		_profileTypeSupplier = () -> {
			try {
				return profileTypeUnsafeSupplier.get();
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
		description = "Profile classification. KNOWN individuals are identified by email/account; ANONYMOUS individuals are browser-tracked only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected ProfileType profileType;

	@JsonIgnore
	private Supplier<ProfileType> _profileTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total number of sessions attributed to this individual over their whole history. Not a windowed count."
	)
	public Long getSessionsCount() {
		if (_sessionsCountSupplier != null) {
			sessionsCount = _sessionsCountSupplier.get();

			_sessionsCountSupplier = null;
		}

		return sessionsCount;
	}

	public void setSessionsCount(Long sessionsCount) {
		this.sessionsCount = sessionsCount;

		_sessionsCountSupplier = null;
	}

	@JsonIgnore
	public void setSessionsCount(
		UnsafeSupplier<Long, Exception> sessionsCountUnsafeSupplier) {

		_sessionsCountSupplier = () -> {
			try {
				return sessionsCountUnsafeSupplier.get();
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
		description = "Total number of sessions attributed to this individual over their whole history. Not a windowed count."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long sessionsCount;

	@JsonIgnore
	private Supplier<Long> _sessionsCountSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Individual)) {
			return false;
		}

		Individual individual = (Individual)object;

		return Objects.equals(toString(), individual.toString());
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

		String accountName = getAccountName();

		if (accountName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountName\": ");

			sb.append("\"");

			sb.append(_escape(accountName));

			sb.append("\"");
		}

		Long activitiesCount = getActivitiesCount();

		if (activitiesCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activitiesCount\": ");

			sb.append(activitiesCount);
		}

		String activityStatus = getActivityStatus();

		if (activityStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activityStatus\": ");

			sb.append("\"");

			sb.append(_escape(activityStatus));

			sb.append("\"");
		}

		Long averageSessionDuration = getAverageSessionDuration();

		if (averageSessionDuration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"averageSessionDuration\": ");

			sb.append(averageSessionDuration);
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
		}

		Object demographics = getDemographics();

		if (demographics != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"demographics\": ");

			if (demographics instanceof Collection) {
				sb.append(
					JSONFactoryUtil.createJSONArray(
						(Collection<?>)demographics));
			}
			else if (demographics instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)demographics));
			}
			else if (demographics instanceof Object[]) {
				sb.append(
					JSONFactoryUtil.createJSONArray(
						Arrays.asList((Object[])demographics)));
			}
			else if (demographics instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)demographics));
				sb.append("\"");
			}
			else {
				sb.append(demographics);
			}
		}

		String emailAddress = getEmailAddress();

		if (emailAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(emailAddress));

			sb.append("\"");
		}

		Date firstActivityDate = getFirstActivityDate();

		if (firstActivityDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"firstActivityDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(firstActivityDate));

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

		Date knownSinceDate = getKnownSinceDate();

		if (knownSinceDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knownSinceDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(knownSinceDate));

			sb.append("\"");
		}

		Date lastActivityDate = getLastActivityDate();

		if (lastActivityDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastActivityDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(lastActivityDate));

			sb.append("\"");
		}

		String lastSessionCountry = getLastSessionCountry();

		if (lastSessionCountry != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastSessionCountry\": ");

			sb.append("\"");

			sb.append(_escape(lastSessionCountry));

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

		ProfileType profileType = getProfileType();

		if (profileType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"profileType\": ");

			sb.append("\"");
			sb.append(profileType);
			sb.append("\"");
		}

		Long sessionsCount = getSessionsCount();

		if (sessionsCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionsCount\": ");

			sb.append(sessionsCount);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.Individual",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ProfileType")
	public static enum ProfileType {

		ANONYMOUS("ANONYMOUS"), KNOWN("KNOWN");

		@JsonCreator
		public static ProfileType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ProfileType profileType : values()) {
				if (Objects.equals(profileType.getValue(), value)) {
					return profileType;
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

		private ProfileType(String value) {
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
// LIFERAY-REST-BUILDER-HASH:383417997