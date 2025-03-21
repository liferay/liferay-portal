/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName("User")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "User")
public class User implements Serializable {

	public static User toDTO(String json) {
		return ObjectMapperUtil.readValue(User.class, json);
	}

	public static User unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(User.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A Boolean value indicating the user's administrative status."
	)
	public Boolean getActive() {
		if (_activeSupplier != null) {
			active = _activeSupplier.get();

			_activeSupplier = null;
		}

		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;

		_activeSupplier = null;
	}

	@JsonIgnore
	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		_activeSupplier = () -> {
			try {
				return activeUnsafeSupplier.get();
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
		description = "A Boolean value indicating the user's administrative status."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A physical mailing address for this user."
	)
	@Valid
	public Object[] getAddresses() {
		if (_addressesSupplier != null) {
			addresses = _addressesSupplier.get();

			_addressesSupplier = null;
		}

		return addresses;
	}

	public void setAddresses(Object[] addresses) {
		this.addresses = addresses;

		_addressesSupplier = null;
	}

	@JsonIgnore
	public void setAddresses(
		UnsafeSupplier<Object[], Exception> addressesUnsafeSupplier) {

		_addressesSupplier = () -> {
			try {
				return addressesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A physical mailing address for this user.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object[] addresses;

	@JsonIgnore
	private Supplier<Object[]> _addressesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the user, suitable for display to end-users."
	)
	public String getDisplayName() {
		if (_displayNameSupplier != null) {
			displayName = _displayNameSupplier.get();

			_displayNameSupplier = null;
		}

		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;

		_displayNameSupplier = null;
	}

	@JsonIgnore
	public void setDisplayName(
		UnsafeSupplier<String, Exception> displayNameUnsafeSupplier) {

		_displayNameSupplier = () -> {
			try {
				return displayNameUnsafeSupplier.get();
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
		description = "The name of the user, suitable for display to end-users."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String displayName;

	@JsonIgnore
	private Supplier<String> _displayNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Email addresses for the User."
	)
	@Valid
	public MultiValuedAttribute[] getEmails() {
		if (_emailsSupplier != null) {
			emails = _emailsSupplier.get();

			_emailsSupplier = null;
		}

		return emails;
	}

	public void setEmails(MultiValuedAttribute[] emails) {
		this.emails = emails;

		_emailsSupplier = null;
	}

	@JsonIgnore
	public void setEmails(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			emailsUnsafeSupplier) {

		_emailsSupplier = () -> {
			try {
				return emailsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Email addresses for the User.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MultiValuedAttribute[] emails;

	@JsonIgnore
	private Supplier<MultiValuedAttribute[]> _emailsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of entitlements for the user that represent a thing the user has."
	)
	@Valid
	public MultiValuedAttribute[] getEntitlements() {
		if (_entitlementsSupplier != null) {
			entitlements = _entitlementsSupplier.get();

			_entitlementsSupplier = null;
		}

		return entitlements;
	}

	public void setEntitlements(MultiValuedAttribute[] entitlements) {
		this.entitlements = entitlements;

		_entitlementsSupplier = null;
	}

	@JsonIgnore
	public void setEntitlements(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			entitlementsUnsafeSupplier) {

		_entitlementsSupplier = () -> {
			try {
				return entitlementsUnsafeSupplier.get();
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
		description = "A list of entitlements for the user that represent a thing the user has."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MultiValuedAttribute[] entitlements;

	@JsonIgnore
	private Supplier<MultiValuedAttribute[]> _entitlementsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A String that is an identifier for the resource as defined by the provisioning client."
	)
	public String getExternalId() {
		if (_externalIdSupplier != null) {
			externalId = _externalIdSupplier.get();

			_externalIdSupplier = null;
		}

		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;

		_externalIdSupplier = null;
	}

	@JsonIgnore
	public void setExternalId(
		UnsafeSupplier<String, Exception> externalIdUnsafeSupplier) {

		_externalIdSupplier = () -> {
			try {
				return externalIdUnsafeSupplier.get();
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
		description = "A String that is an identifier for the resource as defined by the provisioning client."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalId;

	@JsonIgnore
	private Supplier<String> _externalIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of groups to which the user belongs, either through direct membership, through nested groups, or dynamically calculated."
	)
	@Valid
	public MultiValuedAttribute[] getGroups() {
		if (_groupsSupplier != null) {
			groups = _groupsSupplier.get();

			_groupsSupplier = null;
		}

		return groups;
	}

	public void setGroups(MultiValuedAttribute[] groups) {
		this.groups = groups;

		_groupsSupplier = null;
	}

	@JsonIgnore
	public void setGroups(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			groupsUnsafeSupplier) {

		_groupsSupplier = () -> {
			try {
				return groupsUnsafeSupplier.get();
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
		description = "A list of groups to which the user belongs, either through direct membership, through nested groups, or dynamically calculated."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MultiValuedAttribute[] groups;

	@JsonIgnore
	private Supplier<MultiValuedAttribute[]> _groupsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A unique identifier for a SCIM resource as defined by the service provider."
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
		description = "A unique identifier for a SCIM resource as defined by the service provider."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Instant messaging address for the user."
	)
	@Valid
	public MultiValuedAttribute[] getIms() {
		if (_imsSupplier != null) {
			ims = _imsSupplier.get();

			_imsSupplier = null;
		}

		return ims;
	}

	public void setIms(MultiValuedAttribute[] ims) {
		this.ims = ims;

		_imsSupplier = null;
	}

	@JsonIgnore
	public void setIms(
		UnsafeSupplier<MultiValuedAttribute[], Exception> imsUnsafeSupplier) {

		_imsSupplier = () -> {
			try {
				return imsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Instant messaging address for the user.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MultiValuedAttribute[] ims;

	@JsonIgnore
	private Supplier<MultiValuedAttribute[]> _imsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Used to indicate the User's default location for purposes of localizing such items as currency, date time format, or numerical representations."
	)
	public String getLocale() {
		if (_localeSupplier != null) {
			locale = _localeSupplier.get();

			_localeSupplier = null;
		}

		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;

		_localeSupplier = null;
	}

	@JsonIgnore
	public void setLocale(
		UnsafeSupplier<String, Exception> localeUnsafeSupplier) {

		_localeSupplier = () -> {
			try {
				return localeUnsafeSupplier.get();
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
		description = "Used to indicate the User's default location for purposes of localizing such items as currency, date time format, or numerical representations."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String locale;

	@JsonIgnore
	private Supplier<String> _localeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Meta getMeta() {
		if (_metaSupplier != null) {
			meta = _metaSupplier.get();

			_metaSupplier = null;
		}

		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;

		_metaSupplier = null;
	}

	@JsonIgnore
	public void setMeta(UnsafeSupplier<Meta, Exception> metaUnsafeSupplier) {
		_metaSupplier = () -> {
			try {
				return metaUnsafeSupplier.get();
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
	protected Meta meta;

	@JsonIgnore
	private Supplier<Meta> _metaSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The components of the user's name."
	)
	@Valid
	public Name getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(Name name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<Name, Exception> nameUnsafeSupplier) {
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

	@GraphQLField(description = "The components of the user's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Name name;

	@JsonIgnore
	private Supplier<Name> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The casual way to address the user in real life, e.g., \"Bob\" or \"Bobby\" instead of \"Robert\"."
	)
	public String getNickName() {
		if (_nickNameSupplier != null) {
			nickName = _nickNameSupplier.get();

			_nickNameSupplier = null;
		}

		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;

		_nickNameSupplier = null;
	}

	@JsonIgnore
	public void setNickName(
		UnsafeSupplier<String, Exception> nickNameUnsafeSupplier) {

		_nickNameSupplier = () -> {
			try {
				return nickNameUnsafeSupplier.get();
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
		description = "The casual way to address the user in real life, e.g., \"Bob\" or \"Bobby\" instead of \"Robert\"."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String nickName;

	@JsonIgnore
	private Supplier<String> _nickNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "This attribute is intended to be used as a means to set, replace, or compare (i.e., filter for equality) a password."
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

	@GraphQLField(
		description = "This attribute is intended to be used as a means to set, replace, or compare (i.e., filter for equality) a password."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String password;

	@JsonIgnore
	private Supplier<String> _passwordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Phone numbers for the user."
	)
	@Valid
	public MultiValuedAttribute[] getPhoneNumbers() {
		if (_phoneNumbersSupplier != null) {
			phoneNumbers = _phoneNumbersSupplier.get();

			_phoneNumbersSupplier = null;
		}

		return phoneNumbers;
	}

	public void setPhoneNumbers(MultiValuedAttribute[] phoneNumbers) {
		this.phoneNumbers = phoneNumbers;

		_phoneNumbersSupplier = null;
	}

	@JsonIgnore
	public void setPhoneNumbers(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			phoneNumbersUnsafeSupplier) {

		_phoneNumbersSupplier = () -> {
			try {
				return phoneNumbersUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Phone numbers for the user.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MultiValuedAttribute[] phoneNumbers;

	@JsonIgnore
	private Supplier<MultiValuedAttribute[]> _phoneNumbersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A URI that is a uniform resource locator that points to a resource location representing the user's image."
	)
	@Valid
	public MultiValuedAttribute[] getPhotos() {
		if (_photosSupplier != null) {
			photos = _photosSupplier.get();

			_photosSupplier = null;
		}

		return photos;
	}

	public void setPhotos(MultiValuedAttribute[] photos) {
		this.photos = photos;

		_photosSupplier = null;
	}

	@JsonIgnore
	public void setPhotos(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			photosUnsafeSupplier) {

		_photosSupplier = () -> {
			try {
				return photosUnsafeSupplier.get();
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
		description = "A URI that is a uniform resource locator that points to a resource location representing the user's image."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MultiValuedAttribute[] photos;

	@JsonIgnore
	private Supplier<MultiValuedAttribute[]> _photosSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Indicates the user's preferred written or spoken languages and is generally used for selecting a localized user interface."
	)
	public String getPreferredLanguage() {
		if (_preferredLanguageSupplier != null) {
			preferredLanguage = _preferredLanguageSupplier.get();

			_preferredLanguageSupplier = null;
		}

		return preferredLanguage;
	}

	public void setPreferredLanguage(String preferredLanguage) {
		this.preferredLanguage = preferredLanguage;

		_preferredLanguageSupplier = null;
	}

	@JsonIgnore
	public void setPreferredLanguage(
		UnsafeSupplier<String, Exception> preferredLanguageUnsafeSupplier) {

		_preferredLanguageSupplier = () -> {
			try {
				return preferredLanguageUnsafeSupplier.get();
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
		description = "Indicates the user's preferred written or spoken languages and is generally used for selecting a localized user interface."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String preferredLanguage;

	@JsonIgnore
	private Supplier<String> _preferredLanguageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A URI that is a uniform resource locator and that points to a location representing the user's online profile (e.g., a web page)."
	)
	public String getProfileUrl() {
		if (_profileUrlSupplier != null) {
			profileUrl = _profileUrlSupplier.get();

			_profileUrlSupplier = null;
		}

		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;

		_profileUrlSupplier = null;
	}

	@JsonIgnore
	public void setProfileUrl(
		UnsafeSupplier<String, Exception> profileUrlUnsafeSupplier) {

		_profileUrlSupplier = () -> {
			try {
				return profileUrlUnsafeSupplier.get();
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
		description = "A URI that is a uniform resource locator and that points to a location representing the user's online profile (e.g., a web page)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String profileUrl;

	@JsonIgnore
	private Supplier<String> _profileUrlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of roles for the user that collectively represent who the user is, e.g., \"Student\", \"Faculty\"."
	)
	@Valid
	public MultiValuedAttribute[] getRoles() {
		if (_rolesSupplier != null) {
			roles = _rolesSupplier.get();

			_rolesSupplier = null;
		}

		return roles;
	}

	public void setRoles(MultiValuedAttribute[] roles) {
		this.roles = roles;

		_rolesSupplier = null;
	}

	@JsonIgnore
	public void setRoles(
		UnsafeSupplier<MultiValuedAttribute[], Exception> rolesUnsafeSupplier) {

		_rolesSupplier = () -> {
			try {
				return rolesUnsafeSupplier.get();
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
		description = "A list of roles for the user that collectively represent who the user is, e.g., \"Student\", \"Faculty\"."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MultiValuedAttribute[] roles;

	@JsonIgnore
	private Supplier<MultiValuedAttribute[]> _rolesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A multi-valued list of strings indicating the namespaces of the SCIM schemas that define the attributes present in the current JSON structure."
	)
	public String[] getSchemas() {
		if (_schemasSupplier != null) {
			schemas = _schemasSupplier.get();

			_schemasSupplier = null;
		}

		return schemas;
	}

	public void setSchemas(String[] schemas) {
		this.schemas = schemas;

		_schemasSupplier = null;
	}

	@JsonIgnore
	public void setSchemas(
		UnsafeSupplier<String[], Exception> schemasUnsafeSupplier) {

		_schemasSupplier = () -> {
			try {
				return schemasUnsafeSupplier.get();
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
		description = "A multi-valued list of strings indicating the namespaces of the SCIM schemas that define the attributes present in the current JSON structure."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] schemas;

	@JsonIgnore
	private Supplier<String[]> _schemasSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The User's time zone, in IANA Time Zone database format, also known as the \"Olson\" time zone database format (e.g., \"America/Los_Angeles\")."
	)
	public String getTimezone() {
		if (_timezoneSupplier != null) {
			timezone = _timezoneSupplier.get();

			_timezoneSupplier = null;
		}

		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;

		_timezoneSupplier = null;
	}

	@JsonIgnore
	public void setTimezone(
		UnsafeSupplier<String, Exception> timezoneUnsafeSupplier) {

		_timezoneSupplier = () -> {
			try {
				return timezoneUnsafeSupplier.get();
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
		description = "The User's time zone, in IANA Time Zone database format, also known as the \"Olson\" time zone database format (e.g., \"America/Los_Angeles\")."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String timezone;

	@JsonIgnore
	private Supplier<String> _timezoneSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's title, such as \"Vice President\"."
	)
	public String getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		_titleSupplier = () -> {
			try {
				return titleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's title, such as \"Vice President\".")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The components of the Liferay's User Schema Extension."
	)
	@Valid
	public UserSchemaExtension
		getUrn_ietf_params_scim_schemas_extension_liferay_2_0_User() {

		if (_urn_ietf_params_scim_schemas_extension_liferay_2_0_UserSupplier !=
				null) {

			urn_ietf_params_scim_schemas_extension_liferay_2_0_User =
				_urn_ietf_params_scim_schemas_extension_liferay_2_0_UserSupplier.
					get();

			_urn_ietf_params_scim_schemas_extension_liferay_2_0_UserSupplier =
				null;
		}

		return urn_ietf_params_scim_schemas_extension_liferay_2_0_User;
	}

	public void setUrn_ietf_params_scim_schemas_extension_liferay_2_0_User(
		UserSchemaExtension
			urn_ietf_params_scim_schemas_extension_liferay_2_0_User) {

		this.urn_ietf_params_scim_schemas_extension_liferay_2_0_User =
			urn_ietf_params_scim_schemas_extension_liferay_2_0_User;

		_urn_ietf_params_scim_schemas_extension_liferay_2_0_UserSupplier = null;
	}

	@JsonIgnore
	public void setUrn_ietf_params_scim_schemas_extension_liferay_2_0_User(
		UnsafeSupplier<UserSchemaExtension, Exception>
			urn_ietf_params_scim_schemas_extension_liferay_2_0_UserUnsafeSupplier) {

		_urn_ietf_params_scim_schemas_extension_liferay_2_0_UserSupplier =
			() -> {
				try {
					return urn_ietf_params_scim_schemas_extension_liferay_2_0_UserUnsafeSupplier.
						get();
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
		description = "The components of the Liferay's User Schema Extension."
	)
	@JsonProperty(
		access = JsonProperty.Access.READ_WRITE,
		value = "urn:ietf:params:scim:schemas:extension:liferay:2.0:User"
	)
	protected UserSchemaExtension
		urn_ietf_params_scim_schemas_extension_liferay_2_0_User;

	@JsonIgnore
	private Supplier<UserSchemaExtension>
		_urn_ietf_params_scim_schemas_extension_liferay_2_0_UserSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A service provider's unique identifier for the user, typically used by the user to directly authenticate to the service provider."
	)
	public String getUserName() {
		if (_userNameSupplier != null) {
			userName = _userNameSupplier.get();

			_userNameSupplier = null;
		}

		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;

		_userNameSupplier = null;
	}

	@JsonIgnore
	public void setUserName(
		UnsafeSupplier<String, Exception> userNameUnsafeSupplier) {

		_userNameSupplier = () -> {
			try {
				return userNameUnsafeSupplier.get();
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
		description = "A service provider's unique identifier for the user, typically used by the user to directly authenticate to the service provider."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String userName;

	@JsonIgnore
	private Supplier<String> _userNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Used to identify the relationship between the organization and the user."
	)
	public String getUserType() {
		if (_userTypeSupplier != null) {
			userType = _userTypeSupplier.get();

			_userTypeSupplier = null;
		}

		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;

		_userTypeSupplier = null;
	}

	@JsonIgnore
	public void setUserType(
		UnsafeSupplier<String, Exception> userTypeUnsafeSupplier) {

		_userTypeSupplier = () -> {
			try {
				return userTypeUnsafeSupplier.get();
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
		description = "Used to identify the relationship between the organization and the user."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String userType;

	@JsonIgnore
	private Supplier<String> _userTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of certificates associated with the resource (e.g., a User)."
	)
	@Valid
	public MultiValuedAttribute[] getX509Certificates() {
		if (_x509CertificatesSupplier != null) {
			x509Certificates = _x509CertificatesSupplier.get();

			_x509CertificatesSupplier = null;
		}

		return x509Certificates;
	}

	public void setX509Certificates(MultiValuedAttribute[] x509Certificates) {
		this.x509Certificates = x509Certificates;

		_x509CertificatesSupplier = null;
	}

	@JsonIgnore
	public void setX509Certificates(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			x509CertificatesUnsafeSupplier) {

		_x509CertificatesSupplier = () -> {
			try {
				return x509CertificatesUnsafeSupplier.get();
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
		description = "A list of certificates associated with the resource (e.g., a User)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MultiValuedAttribute[] x509Certificates;

	@JsonIgnore
	private Supplier<MultiValuedAttribute[]> _x509CertificatesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof User)) {
			return false;
		}

		User user = (User)object;

		return Objects.equals(toString(), user.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		Object[] addresses = getAddresses();

		if (addresses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addresses\": ");

			sb.append("[");

			for (int i = 0; i < addresses.length; i++) {
				sb.append("\"");

				sb.append(_escape(addresses[i]));

				sb.append("\"");

				if ((i + 1) < addresses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String displayName = getDisplayName();

		if (displayName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayName\": ");

			sb.append("\"");

			sb.append(_escape(displayName));

			sb.append("\"");
		}

		MultiValuedAttribute[] emails = getEmails();

		if (emails != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emails\": ");

			sb.append("[");

			for (int i = 0; i < emails.length; i++) {
				sb.append(String.valueOf(emails[i]));

				if ((i + 1) < emails.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		MultiValuedAttribute[] entitlements = getEntitlements();

		if (entitlements != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entitlements\": ");

			sb.append("[");

			for (int i = 0; i < entitlements.length; i++) {
				sb.append(String.valueOf(entitlements[i]));

				if ((i + 1) < entitlements.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String externalId = getExternalId();

		if (externalId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalId\": ");

			sb.append("\"");

			sb.append(_escape(externalId));

			sb.append("\"");
		}

		MultiValuedAttribute[] groups = getGroups();

		if (groups != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groups\": ");

			sb.append("[");

			for (int i = 0; i < groups.length; i++) {
				sb.append(String.valueOf(groups[i]));

				if ((i + 1) < groups.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		MultiValuedAttribute[] ims = getIms();

		if (ims != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ims\": ");

			sb.append("[");

			for (int i = 0; i < ims.length; i++) {
				sb.append(String.valueOf(ims[i]));

				if ((i + 1) < ims.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String locale = getLocale();

		if (locale != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"locale\": ");

			sb.append("\"");

			sb.append(_escape(locale));

			sb.append("\"");
		}

		Meta meta = getMeta();

		if (meta != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(meta));
		}

		Name name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(String.valueOf(name));
		}

		String nickName = getNickName();

		if (nickName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nickName\": ");

			sb.append("\"");

			sb.append(_escape(nickName));

			sb.append("\"");
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

		MultiValuedAttribute[] phoneNumbers = getPhoneNumbers();

		if (phoneNumbers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneNumbers\": ");

			sb.append("[");

			for (int i = 0; i < phoneNumbers.length; i++) {
				sb.append(String.valueOf(phoneNumbers[i]));

				if ((i + 1) < phoneNumbers.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		MultiValuedAttribute[] photos = getPhotos();

		if (photos != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"photos\": ");

			sb.append("[");

			for (int i = 0; i < photos.length; i++) {
				sb.append(String.valueOf(photos[i]));

				if ((i + 1) < photos.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String preferredLanguage = getPreferredLanguage();

		if (preferredLanguage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"preferredLanguage\": ");

			sb.append("\"");

			sb.append(_escape(preferredLanguage));

			sb.append("\"");
		}

		String profileUrl = getProfileUrl();

		if (profileUrl != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"profileUrl\": ");

			sb.append("\"");

			sb.append(_escape(profileUrl));

			sb.append("\"");
		}

		MultiValuedAttribute[] roles = getRoles();

		if (roles != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roles\": ");

			sb.append("[");

			for (int i = 0; i < roles.length; i++) {
				sb.append(String.valueOf(roles[i]));

				if ((i + 1) < roles.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] schemas = getSchemas();

		if (schemas != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < schemas.length; i++) {
				sb.append("\"");

				sb.append(_escape(schemas[i]));

				sb.append("\"");

				if ((i + 1) < schemas.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String timezone = getTimezone();

		if (timezone != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timezone\": ");

			sb.append("\"");

			sb.append(_escape(timezone));

			sb.append("\"");
		}

		String title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(title));

			sb.append("\"");
		}

		UserSchemaExtension
			urn_ietf_params_scim_schemas_extension_liferay_2_0_User =
				getUrn_ietf_params_scim_schemas_extension_liferay_2_0_User();

		if (urn_ietf_params_scim_schemas_extension_liferay_2_0_User != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"urn:ietf:params:scim:schemas:extension:liferay:2.0:User\": ");

			sb.append(
				String.valueOf(
					urn_ietf_params_scim_schemas_extension_liferay_2_0_User));
		}

		String userName = getUserName();

		if (userName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userName\": ");

			sb.append("\"");

			sb.append(_escape(userName));

			sb.append("\"");
		}

		String userType = getUserType();

		if (userType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userType\": ");

			sb.append("\"");

			sb.append(_escape(userType));

			sb.append("\"");
		}

		MultiValuedAttribute[] x509Certificates = getX509Certificates();

		if (x509Certificates != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"x509Certificates\": ");

			sb.append("[");

			for (int i = 0; i < x509Certificates.length; i++) {
				sb.append(String.valueOf(x509Certificates[i]));

				if ((i + 1) < x509Certificates.length) {
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
		defaultValue = "com.liferay.scim.rest.dto.v1_0.User",
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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}