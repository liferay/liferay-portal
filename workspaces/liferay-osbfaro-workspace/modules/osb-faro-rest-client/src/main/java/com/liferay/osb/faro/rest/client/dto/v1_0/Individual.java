/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.IndividualSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class Individual implements Cloneable, Serializable {

	public static Individual toDTO(String json) {
		return IndividualSerDes.toDTO(json);
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setAccountName(
		UnsafeSupplier<String, Exception> accountNameUnsafeSupplier) {

		try {
			accountName = accountNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String accountName;

	public Long getActivitiesCount() {
		return activitiesCount;
	}

	public void setActivitiesCount(Long activitiesCount) {
		this.activitiesCount = activitiesCount;
	}

	public void setActivitiesCount(
		UnsafeSupplier<Long, Exception> activitiesCountUnsafeSupplier) {

		try {
			activitiesCount = activitiesCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long activitiesCount;

	public String getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

	public void setActivityStatus(
		UnsafeSupplier<String, Exception> activityStatusUnsafeSupplier) {

		try {
			activityStatus = activityStatusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String activityStatus;

	public Long getAverageSessionDuration() {
		return averageSessionDuration;
	}

	public void setAverageSessionDuration(Long averageSessionDuration) {
		this.averageSessionDuration = averageSessionDuration;
	}

	public void setAverageSessionDuration(
		UnsafeSupplier<Long, Exception> averageSessionDurationUnsafeSupplier) {

		try {
			averageSessionDuration = averageSessionDurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long averageSessionDuration;

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

	public Object getDemographics() {
		return demographics;
	}

	public void setDemographics(Object demographics) {
		this.demographics = demographics;
	}

	public void setDemographics(
		UnsafeSupplier<Object, Exception> demographicsUnsafeSupplier) {

		try {
			demographics = demographicsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object demographics;

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setEmailAddress(
		UnsafeSupplier<String, Exception> emailAddressUnsafeSupplier) {

		try {
			emailAddress = emailAddressUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String emailAddress;

	public Date getFirstActivityDate() {
		return firstActivityDate;
	}

	public void setFirstActivityDate(Date firstActivityDate) {
		this.firstActivityDate = firstActivityDate;
	}

	public void setFirstActivityDate(
		UnsafeSupplier<Date, Exception> firstActivityDateUnsafeSupplier) {

		try {
			firstActivityDate = firstActivityDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date firstActivityDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

	public Date getKnownSinceDate() {
		return knownSinceDate;
	}

	public void setKnownSinceDate(Date knownSinceDate) {
		this.knownSinceDate = knownSinceDate;
	}

	public void setKnownSinceDate(
		UnsafeSupplier<Date, Exception> knownSinceDateUnsafeSupplier) {

		try {
			knownSinceDate = knownSinceDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date knownSinceDate;

	public Date getLastActivityDate() {
		return lastActivityDate;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}

	public void setLastActivityDate(
		UnsafeSupplier<Date, Exception> lastActivityDateUnsafeSupplier) {

		try {
			lastActivityDate = lastActivityDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date lastActivityDate;

	public String getLastSessionCountry() {
		return lastSessionCountry;
	}

	public void setLastSessionCountry(String lastSessionCountry) {
		this.lastSessionCountry = lastSessionCountry;
	}

	public void setLastSessionCountry(
		UnsafeSupplier<String, Exception> lastSessionCountryUnsafeSupplier) {

		try {
			lastSessionCountry = lastSessionCountryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String lastSessionCountry;

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

	public ProfileType getProfileType() {
		return profileType;
	}

	public String getProfileTypeAsString() {
		if (profileType == null) {
			return null;
		}

		return profileType.toString();
	}

	public void setProfileType(ProfileType profileType) {
		this.profileType = profileType;
	}

	public void setProfileType(
		UnsafeSupplier<ProfileType, Exception> profileTypeUnsafeSupplier) {

		try {
			profileType = profileTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProfileType profileType;

	public Long getSessionsCount() {
		return sessionsCount;
	}

	public void setSessionsCount(Long sessionsCount) {
		this.sessionsCount = sessionsCount;
	}

	public void setSessionsCount(
		UnsafeSupplier<Long, Exception> sessionsCountUnsafeSupplier) {

		try {
			sessionsCount = sessionsCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long sessionsCount;

	@Override
	public Individual clone() throws CloneNotSupportedException {
		return (Individual)super.clone();
	}

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
		return IndividualSerDes.toJSON(this);
	}

	public static enum ProfileType {

		ANONYMOUS("ANONYMOUS"), KNOWN("KNOWN");

		public static ProfileType create(String value) {
			for (ProfileType profileType : values()) {
				if (Objects.equals(profileType.getValue(), value) ||
					Objects.equals(profileType.name(), value)) {

					return profileType;
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

		private ProfileType(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:769200648