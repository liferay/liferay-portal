/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0;

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

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(description = "Represents a user.", value = "UserAccount")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "UserAccount")
public class UserAccount implements Serializable {

	public static UserAccount toDTO(String json) {
		return ObjectMapperUtil.readValue(UserAccount.class, json);
	}

	public static UserAccount unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(UserAccount.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's account."
	)
	@Valid
	public AccountBrief[] getAccountBriefs() {
		if (_accountBriefsSupplier != null) {
			accountBriefs = _accountBriefsSupplier.get();

			_accountBriefsSupplier = null;
		}

		return accountBriefs;
	}

	public void setAccountBriefs(AccountBrief[] accountBriefs) {
		this.accountBriefs = accountBriefs;

		_accountBriefsSupplier = null;
	}

	@JsonIgnore
	public void setAccountBriefs(
		UnsafeSupplier<AccountBrief[], Exception> accountBriefsUnsafeSupplier) {

		_accountBriefsSupplier = () -> {
			try {
				return accountBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of the user's account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AccountBrief[] accountBriefs;

	@JsonIgnore
	private Supplier<AccountBrief[]> _accountBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's additional name (e.g., middle name)."
	)
	public String getAdditionalName() {
		if (_additionalNameSupplier != null) {
			additionalName = _additionalNameSupplier.get();

			_additionalNameSupplier = null;
		}

		return additionalName;
	}

	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;

		_additionalNameSupplier = null;
	}

	@JsonIgnore
	public void setAdditionalName(
		UnsafeSupplier<String, Exception> additionalNameUnsafeSupplier) {

		_additionalNameSupplier = () -> {
			try {
				return additionalNameUnsafeSupplier.get();
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
		description = "The user's additional name (e.g., middle name)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String additionalName;

	@JsonIgnore
	private Supplier<String> _additionalNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's alias or screen name."
	)
	public String getAlternateName() {
		if (_alternateNameSupplier != null) {
			alternateName = _alternateNameSupplier.get();

			_alternateNameSupplier = null;
		}

		return alternateName;
	}

	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;

		_alternateNameSupplier = null;
	}

	@JsonIgnore
	public void setAlternateName(
		UnsafeSupplier<String, Exception> alternateNameUnsafeSupplier) {

		_alternateNameSupplier = () -> {
			try {
				return alternateNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's alias or screen name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String alternateName;

	@JsonIgnore
	private Supplier<String> _alternateNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's asset libraries."
	)
	@Valid
	public AssetLibraryBrief[] getAssetLibraryBriefs() {
		if (_assetLibraryBriefsSupplier != null) {
			assetLibraryBriefs = _assetLibraryBriefsSupplier.get();

			_assetLibraryBriefsSupplier = null;
		}

		return assetLibraryBriefs;
	}

	public void setAssetLibraryBriefs(AssetLibraryBrief[] assetLibraryBriefs) {
		this.assetLibraryBriefs = assetLibraryBriefs;

		_assetLibraryBriefsSupplier = null;
	}

	@JsonIgnore
	public void setAssetLibraryBriefs(
		UnsafeSupplier<AssetLibraryBrief[], Exception>
			assetLibraryBriefsUnsafeSupplier) {

		_assetLibraryBriefsSupplier = () -> {
			try {
				return assetLibraryBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's asset libraries.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AssetLibraryBrief[] assetLibraryBriefs;

	@JsonIgnore
	private Supplier<AssetLibraryBrief[]> _assetLibraryBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's date of birth."
	)
	public Date getBirthDate() {
		if (_birthDateSupplier != null) {
			birthDate = _birthDateSupplier.get();

			_birthDateSupplier = null;
		}

		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;

		_birthDateSupplier = null;
	}

	@JsonIgnore
	public void setBirthDate(
		UnsafeSupplier<Date, Exception> birthDateUnsafeSupplier) {

		_birthDateSupplier = () -> {
			try {
				return birthDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's date of birth.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date birthDate;

	@JsonIgnore
	private Supplier<Date> _birthDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user who created this user account."
	)
	@Valid
	public Creator getCreator() {
		if (_creatorSupplier != null) {
			creator = _creatorSupplier.get();

			_creatorSupplier = null;
		}

		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;

		_creatorSupplier = null;
	}

	@JsonIgnore
	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		_creatorSupplier = () -> {
			try {
				return creatorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user who created this user account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's current password. Used to authenticate a user when they attempt to update their own password."
	)
	public String getCurrentPassword() {
		if (_currentPasswordSupplier != null) {
			currentPassword = _currentPasswordSupplier.get();

			_currentPasswordSupplier = null;
		}

		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;

		_currentPasswordSupplier = null;
	}

	@JsonIgnore
	public void setCurrentPassword(
		UnsafeSupplier<String, Exception> currentPasswordUnsafeSupplier) {

		_currentPasswordSupplier = () -> {
			try {
				return currentPasswordUnsafeSupplier.get();
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
		description = "The user's current password. Used to authenticate a user when they attempt to update their own password."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String currentPassword;

	@JsonIgnore
	private Supplier<String> _currentPasswordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public com.liferay.portal.vulcan.custom.field.CustomField[]
		getCustomFields() {

		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(
		com.liferay.portal.vulcan.custom.field.CustomField[] customFields) {

		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.portal.vulcan.custom.field.CustomField[], Exception>
				customFieldsUnsafeSupplier) {

		_customFieldsSupplier = () -> {
			try {
				return customFieldsUnsafeSupplier.get();
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
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A relative URL to the user's dashboard."
	)
	public String getDashboardURL() {
		if (_dashboardURLSupplier != null) {
			dashboardURL = _dashboardURLSupplier.get();

			_dashboardURLSupplier = null;
		}

		return dashboardURL;
	}

	public void setDashboardURL(String dashboardURL) {
		this.dashboardURL = dashboardURL;

		_dashboardURLSupplier = null;
	}

	@JsonIgnore
	public void setDashboardURL(
		UnsafeSupplier<String, Exception> dashboardURLUnsafeSupplier) {

		_dashboardURLSupplier = () -> {
			try {
				return dashboardURLUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A relative URL to the user's dashboard.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String dashboardURL;

	@JsonIgnore
	private Supplier<String> _dashboardURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The creation date of the user's account."
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

	@GraphQLField(description = "The creation date of the user's account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time any field of the user's account was changed."
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
		description = "The last time any field of the user's account was changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's main email address."
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

	@GraphQLField(description = "The user's main email address.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String emailAddress;

	@JsonIgnore
	private Supplier<String> _emailAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The optional external key of this user account."
	)
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
		description = "The optional external key of this user account."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's surname (last name)."
	)
	public String getFamilyName() {
		if (_familyNameSupplier != null) {
			familyName = _familyNameSupplier.get();

			_familyNameSupplier = null;
		}

		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;

		_familyNameSupplier = null;
	}

	@JsonIgnore
	public void setFamilyName(
		UnsafeSupplier<String, Exception> familyNameUnsafeSupplier) {

		_familyNameSupplier = () -> {
			try {
				return familyNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's surname (last name).")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String familyName;

	@JsonIgnore
	private Supplier<String> _familyNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's gender."
	)
	@JsonGetter("gender")
	@Valid
	public Gender getGender() {
		if (_genderSupplier != null) {
			gender = _genderSupplier.get();

			_genderSupplier = null;
		}

		return gender;
	}

	@JsonIgnore
	public String getGenderAsString() {
		Gender gender = getGender();

		if (gender == null) {
			return null;
		}

		return gender.toString();
	}

	public void setGender(Gender gender) {
		this.gender = gender;

		_genderSupplier = null;
	}

	@JsonIgnore
	public void setGender(
		UnsafeSupplier<Gender, Exception> genderUnsafeSupplier) {

		_genderSupplier = () -> {
			try {
				return genderUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's gender.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Gender gender;

	@JsonIgnore
	private Supplier<Gender> _genderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's first name."
	)
	public String getGivenName() {
		if (_givenNameSupplier != null) {
			givenName = _givenNameSupplier.get();

			_givenNameSupplier = null;
		}

		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;

		_givenNameSupplier = null;
	}

	@JsonIgnore
	public void setGivenName(
		UnsafeSupplier<String, Exception> givenNameUnsafeSupplier) {

		_givenNameSupplier = () -> {
			try {
				return givenNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's first name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String givenName;

	@JsonIgnore
	private Supplier<String> _givenNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the user has been signed in."
	)
	public Boolean getHasLoginDate() {
		if (_hasLoginDateSupplier != null) {
			hasLoginDate = _hasLoginDateSupplier.get();

			_hasLoginDateSupplier = null;
		}

		return hasLoginDate;
	}

	public void setHasLoginDate(Boolean hasLoginDate) {
		this.hasLoginDate = hasLoginDate;

		_hasLoginDateSupplier = null;
	}

	@JsonIgnore
	public void setHasLoginDate(
		UnsafeSupplier<Boolean, Exception> hasLoginDateUnsafeSupplier) {

		_hasLoginDateSupplier = () -> {
			try {
				return hasLoginDateUnsafeSupplier.get();
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
		description = "A flag that indicates whether the user has been signed in."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean hasLoginDate;

	@JsonIgnore
	private Supplier<Boolean> _hasLoginDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's title (e.g., Dr., Mr., Mrs, Ms., etc.)."
	)
	public String getHonorificPrefix() {
		if (_honorificPrefixSupplier != null) {
			honorificPrefix = _honorificPrefixSupplier.get();

			_honorificPrefixSupplier = null;
		}

		return honorificPrefix;
	}

	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;

		_honorificPrefixSupplier = null;
	}

	@JsonIgnore
	public void setHonorificPrefix(
		UnsafeSupplier<String, Exception> honorificPrefixUnsafeSupplier) {

		_honorificPrefixSupplier = () -> {
			try {
				return honorificPrefixUnsafeSupplier.get();
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
		description = "The user's title (e.g., Dr., Mr., Mrs, Ms., etc.)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String honorificPrefix;

	@JsonIgnore
	private Supplier<String> _honorificPrefixSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's suffix (e.g., II, Jr., PhD, etc.)."
	)
	public String getHonorificSuffix() {
		if (_honorificSuffixSupplier != null) {
			honorificSuffix = _honorificSuffixSupplier.get();

			_honorificSuffixSupplier = null;
		}

		return honorificSuffix;
	}

	public void setHonorificSuffix(String honorificSuffix) {
		this.honorificSuffix = honorificSuffix;

		_honorificSuffixSupplier = null;
	}

	@JsonIgnore
	public void setHonorificSuffix(
		UnsafeSupplier<String, Exception> honorificSuffixUnsafeSupplier) {

		_honorificSuffixSupplier = () -> {
			try {
				return honorificSuffixUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's suffix (e.g., II, Jr., PhD, etc.).")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String honorificSuffix;

	@JsonIgnore
	private Supplier<String> _honorificSuffixSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(description = "The user's ID.")
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
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

	@GraphQLField(description = "The user's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A relative URL to the user's profile image."
	)
	public String getImage() {
		if (_imageSupplier != null) {
			image = _imageSupplier.get();

			_imageSupplier = null;
		}

		return image;
	}

	public void setImage(String image) {
		this.image = image;

		_imageSupplier = null;
	}

	@JsonIgnore
	public void setImage(
		UnsafeSupplier<String, Exception> imageUnsafeSupplier) {

		_imageSupplier = () -> {
			try {
				return imageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A relative URL to the user's profile image.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String image;

	@JsonIgnore
	private Supplier<String> _imageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's profile image external reference code."
	)
	public String getImageExternalReferenceCode() {
		if (_imageExternalReferenceCodeSupplier != null) {
			imageExternalReferenceCode =
				_imageExternalReferenceCodeSupplier.get();

			_imageExternalReferenceCodeSupplier = null;
		}

		return imageExternalReferenceCode;
	}

	public void setImageExternalReferenceCode(
		String imageExternalReferenceCode) {

		this.imageExternalReferenceCode = imageExternalReferenceCode;

		_imageExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setImageExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			imageExternalReferenceCodeUnsafeSupplier) {

		_imageExternalReferenceCodeSupplier = () -> {
			try {
				return imageExternalReferenceCodeUnsafeSupplier.get();
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
		description = "The user's profile image external reference code."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String imageExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _imageExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's profile image id."
	)
	public Long getImageId() {
		if (_imageIdSupplier != null) {
			imageId = _imageIdSupplier.get();

			_imageIdSupplier = null;
		}

		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;

		_imageIdSupplier = null;
	}

	@JsonIgnore
	public void setImageId(
		UnsafeSupplier<Long, Exception> imageIdUnsafeSupplier) {

		_imageIdSupplier = () -> {
			try {
				return imageIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's profile image id.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long imageId;

	@JsonIgnore
	private Supplier<Long> _imageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's job title."
	)
	public String getJobTitle() {
		if (_jobTitleSupplier != null) {
			jobTitle = _jobTitleSupplier.get();

			_jobTitleSupplier = null;
		}

		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;

		_jobTitleSupplier = null;
	}

	@JsonIgnore
	public void setJobTitle(
		UnsafeSupplier<String, Exception> jobTitleUnsafeSupplier) {

		_jobTitleSupplier = () -> {
			try {
				return jobTitleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's job title.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String jobTitle;

	@JsonIgnore
	private Supplier<String> _jobTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of keywords describing the user."
	)
	public String[] getKeywords() {
		if (_keywordsSupplier != null) {
			keywords = _keywordsSupplier.get();

			_keywordsSupplier = null;
		}

		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;

		_keywordsSupplier = null;
	}

	@JsonIgnore
	public void setKeywords(
		UnsafeSupplier<String[], Exception> keywordsUnsafeSupplier) {

		_keywordsSupplier = () -> {
			try {
				return keywordsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of keywords describing the user.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] keywords;

	@JsonIgnore
	private Supplier<String[]> _keywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's preferred language."
	)
	public String getLanguageDisplayName() {
		if (_languageDisplayNameSupplier != null) {
			languageDisplayName = _languageDisplayNameSupplier.get();

			_languageDisplayNameSupplier = null;
		}

		return languageDisplayName;
	}

	public void setLanguageDisplayName(String languageDisplayName) {
		this.languageDisplayName = languageDisplayName;

		_languageDisplayNameSupplier = null;
	}

	@JsonIgnore
	public void setLanguageDisplayName(
		UnsafeSupplier<String, Exception> languageDisplayNameUnsafeSupplier) {

		_languageDisplayNameSupplier = () -> {
			try {
				return languageDisplayNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's preferred language.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String languageDisplayName;

	@JsonIgnore
	private Supplier<String> _languageDisplayNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's preferred language id."
	)
	public String getLanguageId() {
		if (_languageIdSupplier != null) {
			languageId = _languageIdSupplier.get();

			_languageIdSupplier = null;
		}

		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;

		_languageIdSupplier = null;
	}

	@JsonIgnore
	public void setLanguageId(
		UnsafeSupplier<String, Exception> languageIdUnsafeSupplier) {

		_languageIdSupplier = () -> {
			try {
				return languageIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's preferred language id.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String languageId;

	@JsonIgnore
	private Supplier<String> _languageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time the user logged in."
	)
	public Date getLastLoginDate() {
		if (_lastLoginDateSupplier != null) {
			lastLoginDate = _lastLoginDateSupplier.get();

			_lastLoginDateSupplier = null;
		}

		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;

		_lastLoginDateSupplier = null;
	}

	@JsonIgnore
	public void setLastLoginDate(
		UnsafeSupplier<Date, Exception> lastLoginDateUnsafeSupplier) {

		_lastLoginDateSupplier = () -> {
			try {
				return lastLoginDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The last time the user logged in.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date lastLoginDate;

	@JsonIgnore
	private Supplier<Date> _lastLoginDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's full name."
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

	@GraphQLField(description = "The user's full name.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's organizations."
	)
	@Valid
	public OrganizationBrief[] getOrganizationBriefs() {
		if (_organizationBriefsSupplier != null) {
			organizationBriefs = _organizationBriefsSupplier.get();

			_organizationBriefsSupplier = null;
		}

		return organizationBriefs;
	}

	public void setOrganizationBriefs(OrganizationBrief[] organizationBriefs) {
		this.organizationBriefs = organizationBriefs;

		_organizationBriefsSupplier = null;
	}

	@JsonIgnore
	public void setOrganizationBriefs(
		UnsafeSupplier<OrganizationBrief[], Exception>
			organizationBriefsUnsafeSupplier) {

		_organizationBriefsSupplier = () -> {
			try {
				return organizationBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of the user's organizations.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected OrganizationBrief[] organizationBriefs;

	@JsonIgnore
	private Supplier<OrganizationBrief[]> _organizationBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's password."
	)
	public String getPassword() {
		if (_passwordSupplier != null) {
			password = _passwordSupplier.get();

			_passwordSupplier = null;
		}

		return password;
	}

	public void setPassword(String password) {
		this.password = password;

		_passwordSupplier = null;
	}

	@JsonIgnore
	public void setPassword(
		UnsafeSupplier<String, Exception> passwordUnsafeSupplier) {

		_passwordSupplier = () -> {
			try {
				return passwordUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's password.")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String password;

	@JsonIgnore
	private Supplier<String> _passwordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public com.liferay.portal.vulcan.permission.Permission[] getPermissions() {
		if (_permissionsSupplier != null) {
			permissions = _permissionsSupplier.get();

			_permissionsSupplier = null;
		}

		return permissions;
	}

	public void setPermissions(
		com.liferay.portal.vulcan.permission.Permission[] permissions) {

		this.permissions = permissions;

		_permissionsSupplier = null;
	}

	@JsonIgnore
	public void setPermissions(
		UnsafeSupplier
			<com.liferay.portal.vulcan.permission.Permission[], Exception>
				permissionsUnsafeSupplier) {

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
	protected com.liferay.portal.vulcan.permission.Permission[] permissions;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.permission.Permission[]>
		_permissionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A relative URL to the user's profile."
	)
	public String getProfileURL() {
		if (_profileURLSupplier != null) {
			profileURL = _profileURLSupplier.get();

			_profileURLSupplier = null;
		}

		return profileURL;
	}

	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;

		_profileURLSupplier = null;
	}

	@JsonIgnore
	public void setProfileURL(
		UnsafeSupplier<String, Exception> profileURLUnsafeSupplier) {

		_profileURLSupplier = () -> {
			try {
				return profileURLUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A relative URL to the user's profile.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String profileURL;

	@JsonIgnore
	private Supplier<String> _profileURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's roles."
	)
	@Valid
	public RoleBrief[] getRoleBriefs() {
		if (_roleBriefsSupplier != null) {
			roleBriefs = _roleBriefsSupplier.get();

			_roleBriefsSupplier = null;
		}

		return roleBriefs;
	}

	public void setRoleBriefs(RoleBrief[] roleBriefs) {
		this.roleBriefs = roleBriefs;

		_roleBriefsSupplier = null;
	}

	@JsonIgnore
	public void setRoleBriefs(
		UnsafeSupplier<RoleBrief[], Exception> roleBriefsUnsafeSupplier) {

		_roleBriefsSupplier = () -> {
			try {
				return roleBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of the user's roles.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected RoleBrief[] roleBriefs;

	@JsonIgnore
	private Supplier<RoleBrief[]> _roleBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's sites."
	)
	@Valid
	public SiteBrief[] getSiteBriefs() {
		if (_siteBriefsSupplier != null) {
			siteBriefs = _siteBriefsSupplier.get();

			_siteBriefsSupplier = null;
		}

		return siteBriefs;
	}

	public void setSiteBriefs(SiteBrief[] siteBriefs) {
		this.siteBriefs = siteBriefs;

		_siteBriefsSupplier = null;
	}

	@JsonIgnore
	public void setSiteBriefs(
		UnsafeSupplier<SiteBrief[], Exception> siteBriefsUnsafeSupplier) {

		_siteBriefsSupplier = () -> {
			try {
				return siteBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of the user's sites.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected SiteBrief[] siteBriefs;

	@JsonIgnore
	private Supplier<SiteBrief[]> _siteBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's status."
	)
	@JsonGetter("status")
	@Valid
	public Status getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	@JsonIgnore
	public String getStatusAsString() {
		Status status = getStatus();

		if (status == null) {
			return null;
		}

		return status.toString();
	}

	public void setStatus(Status status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

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

	@GraphQLField(description = "The user's status.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The categories associated with this user."
	)
	@Valid
	public TaxonomyCategoryBrief[] getTaxonomyCategoryBriefs() {
		if (_taxonomyCategoryBriefsSupplier != null) {
			taxonomyCategoryBriefs = _taxonomyCategoryBriefsSupplier.get();

			_taxonomyCategoryBriefsSupplier = null;
		}

		return taxonomyCategoryBriefs;
	}

	public void setTaxonomyCategoryBriefs(
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs) {

		this.taxonomyCategoryBriefs = taxonomyCategoryBriefs;

		_taxonomyCategoryBriefsSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryBriefs(
		UnsafeSupplier<TaxonomyCategoryBrief[], Exception>
			taxonomyCategoryBriefsUnsafeSupplier) {

		_taxonomyCategoryBriefsSupplier = () -> {
			try {
				return taxonomyCategoryBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The categories associated with this user.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected TaxonomyCategoryBrief[] taxonomyCategoryBriefs;

	@JsonIgnore
	private Supplier<TaxonomyCategoryBrief[]> _taxonomyCategoryBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's contact information."
	)
	@Valid
	public UserAccountContactInformation getUserAccountContactInformation() {
		if (_userAccountContactInformationSupplier != null) {
			userAccountContactInformation =
				_userAccountContactInformationSupplier.get();

			_userAccountContactInformationSupplier = null;
		}

		return userAccountContactInformation;
	}

	public void setUserAccountContactInformation(
		UserAccountContactInformation userAccountContactInformation) {

		this.userAccountContactInformation = userAccountContactInformation;

		_userAccountContactInformationSupplier = null;
	}

	@JsonIgnore
	public void setUserAccountContactInformation(
		UnsafeSupplier<UserAccountContactInformation, Exception>
			userAccountContactInformationUnsafeSupplier) {

		_userAccountContactInformationSupplier = () -> {
			try {
				return userAccountContactInformationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's contact information.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected UserAccountContactInformation userAccountContactInformation;

	@JsonIgnore
	private Supplier<UserAccountContactInformation>
		_userAccountContactInformationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's userGroups."
	)
	@Valid
	public UserGroupBrief[] getUserGroupBriefs() {
		if (_userGroupBriefsSupplier != null) {
			userGroupBriefs = _userGroupBriefsSupplier.get();

			_userGroupBriefsSupplier = null;
		}

		return userGroupBriefs;
	}

	public void setUserGroupBriefs(UserGroupBrief[] userGroupBriefs) {
		this.userGroupBriefs = userGroupBriefs;

		_userGroupBriefsSupplier = null;
	}

	@JsonIgnore
	public void setUserGroupBriefs(
		UnsafeSupplier<UserGroupBrief[], Exception>
			userGroupBriefsUnsafeSupplier) {

		_userGroupBriefsSupplier = () -> {
			try {
				return userGroupBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of the user's userGroups.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected UserGroupBrief[] userGroupBriefs;

	@JsonIgnore
	private Supplier<UserGroupBrief[]> _userGroupBriefsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserAccount)) {
			return false;
		}

		UserAccount userAccount = (UserAccount)object;

		return Objects.equals(toString(), userAccount.toString());
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

		AccountBrief[] accountBriefs = getAccountBriefs();

		if (accountBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountBriefs\": ");

			sb.append("[");

			for (int i = 0; i < accountBriefs.length; i++) {
				sb.append(String.valueOf(accountBriefs[i]));

				if ((i + 1) < accountBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		String additionalName = getAdditionalName();

		if (additionalName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionalName\": ");

			sb.append("\"");

			sb.append(_escape(additionalName));

			sb.append("\"");
		}

		String alternateName = getAlternateName();

		if (alternateName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"alternateName\": ");

			sb.append("\"");

			sb.append(_escape(alternateName));

			sb.append("\"");
		}

		AssetLibraryBrief[] assetLibraryBriefs = getAssetLibraryBriefs();

		if (assetLibraryBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < assetLibraryBriefs.length; i++) {
				sb.append(String.valueOf(assetLibraryBriefs[i]));

				if ((i + 1) < assetLibraryBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date birthDate = getBirthDate();

		if (birthDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"birthDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(birthDate));

			sb.append("\"");
		}

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(creator));
		}

		String currentPassword = getCurrentPassword();

		if (currentPassword != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currentPassword\": ");

			sb.append("\"");

			sb.append(_escape(currentPassword));

			sb.append("\"");
		}

		com.liferay.portal.vulcan.custom.field.CustomField[] customFields =
			getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(customFields[i]);

				if ((i + 1) < customFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String dashboardURL = getDashboardURL();

		if (dashboardURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dashboardURL\": ");

			sb.append("\"");

			sb.append(_escape(dashboardURL));

			sb.append("\"");
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

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		String familyName = getFamilyName();

		if (familyName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"familyName\": ");

			sb.append("\"");

			sb.append(_escape(familyName));

			sb.append("\"");
		}

		Gender gender = getGender();

		if (gender != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gender\": ");

			sb.append("\"");

			sb.append(gender);

			sb.append("\"");
		}

		String givenName = getGivenName();

		if (givenName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"givenName\": ");

			sb.append("\"");

			sb.append(_escape(givenName));

			sb.append("\"");
		}

		Boolean hasLoginDate = getHasLoginDate();

		if (hasLoginDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hasLoginDate\": ");

			sb.append(hasLoginDate);
		}

		String honorificPrefix = getHonorificPrefix();

		if (honorificPrefix != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"honorificPrefix\": ");

			sb.append("\"");

			sb.append(_escape(honorificPrefix));

			sb.append("\"");
		}

		String honorificSuffix = getHonorificSuffix();

		if (honorificSuffix != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"honorificSuffix\": ");

			sb.append("\"");

			sb.append(_escape(honorificSuffix));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String image = getImage();

		if (image != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"image\": ");

			sb.append("\"");

			sb.append(_escape(image));

			sb.append("\"");
		}

		String imageExternalReferenceCode = getImageExternalReferenceCode();

		if (imageExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(imageExternalReferenceCode));

			sb.append("\"");
		}

		Long imageId = getImageId();

		if (imageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageId\": ");

			sb.append(imageId);
		}

		String jobTitle = getJobTitle();

		if (jobTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jobTitle\": ");

			sb.append("\"");

			sb.append(_escape(jobTitle));

			sb.append("\"");
		}

		String[] keywords = getKeywords();

		if (keywords != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < keywords.length; i++) {
				sb.append("\"");

				sb.append(_escape(keywords[i]));

				sb.append("\"");

				if ((i + 1) < keywords.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String languageDisplayName = getLanguageDisplayName();

		if (languageDisplayName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageDisplayName\": ");

			sb.append("\"");

			sb.append(_escape(languageDisplayName));

			sb.append("\"");
		}

		String languageId = getLanguageId();

		if (languageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageId\": ");

			sb.append("\"");

			sb.append(_escape(languageId));

			sb.append("\"");
		}

		Date lastLoginDate = getLastLoginDate();

		if (lastLoginDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastLoginDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(lastLoginDate));

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

		OrganizationBrief[] organizationBriefs = getOrganizationBriefs();

		if (organizationBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationBriefs\": ");

			sb.append("[");

			for (int i = 0; i < organizationBriefs.length; i++) {
				sb.append(String.valueOf(organizationBriefs[i]));

				if ((i + 1) < organizationBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String password = getPassword();

		if (password != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"password\": ");

			sb.append("\"");

			sb.append(_escape(password));

			sb.append("\"");
		}

		com.liferay.portal.vulcan.permission.Permission[] permissions =
			getPermissions();

		if (permissions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < permissions.length; i++) {
				sb.append(permissions[i]);

				if ((i + 1) < permissions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String profileURL = getProfileURL();

		if (profileURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"profileURL\": ");

			sb.append("\"");

			sb.append(_escape(profileURL));

			sb.append("\"");
		}

		RoleBrief[] roleBriefs = getRoleBriefs();

		if (roleBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleBriefs\": ");

			sb.append("[");

			for (int i = 0; i < roleBriefs.length; i++) {
				sb.append(String.valueOf(roleBriefs[i]));

				if ((i + 1) < roleBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		SiteBrief[] siteBriefs = getSiteBriefs();

		if (siteBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteBriefs\": ");

			sb.append("[");

			for (int i = 0; i < siteBriefs.length; i++) {
				sb.append(String.valueOf(siteBriefs[i]));

				if ((i + 1) < siteBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(status);

			sb.append("\"");
		}

		TaxonomyCategoryBrief[] taxonomyCategoryBriefs =
			getTaxonomyCategoryBriefs();

		if (taxonomyCategoryBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryBriefs.length; i++) {
				sb.append(String.valueOf(taxonomyCategoryBriefs[i]));

				if ((i + 1) < taxonomyCategoryBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		UserAccountContactInformation userAccountContactInformation =
			getUserAccountContactInformation();

		if (userAccountContactInformation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccountContactInformation\": ");

			sb.append(String.valueOf(userAccountContactInformation));
		}

		UserGroupBrief[] userGroupBriefs = getUserGroupBriefs();

		if (userGroupBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userGroupBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userGroupBriefs.length; i++) {
				sb.append(String.valueOf(userGroupBriefs[i]));

				if ((i + 1) < userGroupBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.UserAccount",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Gender")
	public static enum Gender {

		MALE("Male"), FEMALE("Female");

		@JsonCreator
		public static Gender create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Gender gender : values()) {
				if (Objects.equals(gender.getValue(), value)) {
					return gender;
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

		private Gender(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Status")
	public static enum Status {

		ACTIVE("Active"), INACTIVE("Inactive");

		@JsonCreator
		public static Status create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Status status : values()) {
				if (Objects.equals(status.getValue(), value)) {
					return status;
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

		private Status(String value) {
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