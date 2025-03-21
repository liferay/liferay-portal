/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.UserSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class User implements Cloneable, Serializable {

	public static User toDTO(String json) {
		return UserSerDes.toDTO(json);
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public Object[] getAddresses() {
		return addresses;
	}

	public void setAddresses(Object[] addresses) {
		this.addresses = addresses;
	}

	public void setAddresses(
		UnsafeSupplier<Object[], Exception> addressesUnsafeSupplier) {

		try {
			addresses = addressesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object[] addresses;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDisplayName(
		UnsafeSupplier<String, Exception> displayNameUnsafeSupplier) {

		try {
			displayName = displayNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String displayName;

	public MultiValuedAttribute[] getEmails() {
		return emails;
	}

	public void setEmails(MultiValuedAttribute[] emails) {
		this.emails = emails;
	}

	public void setEmails(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			emailsUnsafeSupplier) {

		try {
			emails = emailsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MultiValuedAttribute[] emails;

	public MultiValuedAttribute[] getEntitlements() {
		return entitlements;
	}

	public void setEntitlements(MultiValuedAttribute[] entitlements) {
		this.entitlements = entitlements;
	}

	public void setEntitlements(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			entitlementsUnsafeSupplier) {

		try {
			entitlements = entitlementsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MultiValuedAttribute[] entitlements;

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setExternalId(
		UnsafeSupplier<String, Exception> externalIdUnsafeSupplier) {

		try {
			externalId = externalIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalId;

	public MultiValuedAttribute[] getGroups() {
		return groups;
	}

	public void setGroups(MultiValuedAttribute[] groups) {
		this.groups = groups;
	}

	public void setGroups(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			groupsUnsafeSupplier) {

		try {
			groups = groupsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MultiValuedAttribute[] groups;

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

	public MultiValuedAttribute[] getIms() {
		return ims;
	}

	public void setIms(MultiValuedAttribute[] ims) {
		this.ims = ims;
	}

	public void setIms(
		UnsafeSupplier<MultiValuedAttribute[], Exception> imsUnsafeSupplier) {

		try {
			ims = imsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MultiValuedAttribute[] ims;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setLocale(
		UnsafeSupplier<String, Exception> localeUnsafeSupplier) {

		try {
			locale = localeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String locale;

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public void setMeta(UnsafeSupplier<Meta, Exception> metaUnsafeSupplier) {
		try {
			meta = metaUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Meta meta;

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<Name, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Name name;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setNickName(
		UnsafeSupplier<String, Exception> nickNameUnsafeSupplier) {

		try {
			nickName = nickNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String nickName;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPassword(
		UnsafeSupplier<String, Exception> passwordUnsafeSupplier) {

		try {
			password = passwordUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String password;

	public MultiValuedAttribute[] getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(MultiValuedAttribute[] phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public void setPhoneNumbers(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			phoneNumbersUnsafeSupplier) {

		try {
			phoneNumbers = phoneNumbersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MultiValuedAttribute[] phoneNumbers;

	public MultiValuedAttribute[] getPhotos() {
		return photos;
	}

	public void setPhotos(MultiValuedAttribute[] photos) {
		this.photos = photos;
	}

	public void setPhotos(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			photosUnsafeSupplier) {

		try {
			photos = photosUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MultiValuedAttribute[] photos;

	public String getPreferredLanguage() {
		return preferredLanguage;
	}

	public void setPreferredLanguage(String preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	public void setPreferredLanguage(
		UnsafeSupplier<String, Exception> preferredLanguageUnsafeSupplier) {

		try {
			preferredLanguage = preferredLanguageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String preferredLanguage;

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public void setProfileUrl(
		UnsafeSupplier<String, Exception> profileUrlUnsafeSupplier) {

		try {
			profileUrl = profileUrlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String profileUrl;

	public MultiValuedAttribute[] getRoles() {
		return roles;
	}

	public void setRoles(MultiValuedAttribute[] roles) {
		this.roles = roles;
	}

	public void setRoles(
		UnsafeSupplier<MultiValuedAttribute[], Exception> rolesUnsafeSupplier) {

		try {
			roles = rolesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MultiValuedAttribute[] roles;

	public String[] getSchemas() {
		return schemas;
	}

	public void setSchemas(String[] schemas) {
		this.schemas = schemas;
	}

	public void setSchemas(
		UnsafeSupplier<String[], Exception> schemasUnsafeSupplier) {

		try {
			schemas = schemasUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] schemas;

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public void setTimezone(
		UnsafeSupplier<String, Exception> timezoneUnsafeSupplier) {

		try {
			timezone = timezoneUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String timezone;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	public UserSchemaExtension
		getUrn_ietf_params_scim_schemas_extension_liferay_2_0_User() {

		return urn_ietf_params_scim_schemas_extension_liferay_2_0_User;
	}

	public void setUrn_ietf_params_scim_schemas_extension_liferay_2_0_User(
		UserSchemaExtension
			urn_ietf_params_scim_schemas_extension_liferay_2_0_User) {

		this.urn_ietf_params_scim_schemas_extension_liferay_2_0_User =
			urn_ietf_params_scim_schemas_extension_liferay_2_0_User;
	}

	public void setUrn_ietf_params_scim_schemas_extension_liferay_2_0_User(
		UnsafeSupplier<UserSchemaExtension, Exception>
			urn_ietf_params_scim_schemas_extension_liferay_2_0_UserUnsafeSupplier) {

		try {
			urn_ietf_params_scim_schemas_extension_liferay_2_0_User =
				urn_ietf_params_scim_schemas_extension_liferay_2_0_UserUnsafeSupplier.
					get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserSchemaExtension
		urn_ietf_params_scim_schemas_extension_liferay_2_0_User;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserName(
		UnsafeSupplier<String, Exception> userNameUnsafeSupplier) {

		try {
			userName = userNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String userName;

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public void setUserType(
		UnsafeSupplier<String, Exception> userTypeUnsafeSupplier) {

		try {
			userType = userTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String userType;

	public MultiValuedAttribute[] getX509Certificates() {
		return x509Certificates;
	}

	public void setX509Certificates(MultiValuedAttribute[] x509Certificates) {
		this.x509Certificates = x509Certificates;
	}

	public void setX509Certificates(
		UnsafeSupplier<MultiValuedAttribute[], Exception>
			x509CertificatesUnsafeSupplier) {

		try {
			x509Certificates = x509CertificatesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MultiValuedAttribute[] x509Certificates;

	@Override
	public User clone() throws CloneNotSupportedException {
		return (User)super.clone();
	}

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
		return UserSerDes.toJSON(this);
	}

}