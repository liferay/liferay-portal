/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.ImportProcessRequestSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class ImportProcessRequest implements Cloneable, Serializable {

	public static ImportProcessRequest toDTO(String json) {
		return ImportProcessRequestSerDes.toDTO(json);
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

	public DataStrategy getDataStrategy() {
		return dataStrategy;
	}

	public String getDataStrategyAsString() {
		if (dataStrategy == null) {
			return null;
		}

		return dataStrategy.toString();
	}

	public void setDataStrategy(DataStrategy dataStrategy) {
		this.dataStrategy = dataStrategy;
	}

	public void setDataStrategy(
		UnsafeSupplier<DataStrategy, Exception> dataStrategyUnsafeSupplier) {

		try {
			dataStrategy = dataStrategyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DataStrategy dataStrategy;

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

	public String[] getSiteExternalReferenceCodes() {
		return siteExternalReferenceCodes;
	}

	public void setSiteExternalReferenceCodes(
		String[] siteExternalReferenceCodes) {

		this.siteExternalReferenceCodes = siteExternalReferenceCodes;
	}

	public void setSiteExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			siteExternalReferenceCodesUnsafeSupplier) {

		try {
			siteExternalReferenceCodes =
				siteExternalReferenceCodesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] siteExternalReferenceCodes;

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

	public UserIdStrategy getUserIdStrategy() {
		return userIdStrategy;
	}

	public String getUserIdStrategyAsString() {
		if (userIdStrategy == null) {
			return null;
		}

		return userIdStrategy.toString();
	}

	public void setUserIdStrategy(UserIdStrategy userIdStrategy) {
		this.userIdStrategy = userIdStrategy;
	}

	public void setUserIdStrategy(
		UnsafeSupplier<UserIdStrategy, Exception>
			userIdStrategyUnsafeSupplier) {

		try {
			userIdStrategy = userIdStrategyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserIdStrategy userIdStrategy;

	@Override
	public ImportProcessRequest clone() throws CloneNotSupportedException {
		return (ImportProcessRequest)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ImportProcessRequest)) {
			return false;
		}

		ImportProcessRequest importProcessRequest =
			(ImportProcessRequest)object;

		return Objects.equals(toString(), importProcessRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ImportProcessRequestSerDes.toJSON(this);
	}

	public static enum DataStrategy {

		MIRROR("MIRROR"), MIRROR_OVERWRITE("MIRROR_OVERWRITE"),
		COPY_AS_NEW("COPY_AS_NEW");

		public static DataStrategy create(String value) {
			for (DataStrategy dataStrategy : values()) {
				if (Objects.equals(dataStrategy.getValue(), value) ||
					Objects.equals(dataStrategy.name(), value)) {

					return dataStrategy;
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

		private DataStrategy(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum UserIdStrategy {

		CURRENT_USER_ID("CURRENT_USER_ID"),
		ALWAYS_CURRENT_USER_ID("ALWAYS_CURRENT_USER_ID");

		public static UserIdStrategy create(String value) {
			for (UserIdStrategy userIdStrategy : values()) {
				if (Objects.equals(userIdStrategy.getValue(), value) ||
					Objects.equals(userIdStrategy.name(), value)) {

					return userIdStrategy;
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

		private UserIdStrategy(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:504387022