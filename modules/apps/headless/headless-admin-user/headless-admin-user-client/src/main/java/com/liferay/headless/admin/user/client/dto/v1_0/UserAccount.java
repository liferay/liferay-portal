/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.dto.v1_0;

import com.liferay.headless.admin.user.client.function.UnsafeSupplier;
import com.liferay.headless.admin.user.client.serdes.v1_0.UserAccountSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class UserAccount implements Cloneable, Serializable {

	public static UserAccount toDTO(String json) {
		return UserAccountSerDes.toDTO(json);
	}

	public AccountBrief[] getAccountBriefs() {
		return accountBriefs;
	}

	public void setAccountBriefs(AccountBrief[] accountBriefs) {
		this.accountBriefs = accountBriefs;
	}

	public void setAccountBriefs(
		UnsafeSupplier<AccountBrief[], Exception> accountBriefsUnsafeSupplier) {

		try {
			accountBriefs = accountBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AccountBrief[] accountBriefs;

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public String getAdditionalName() {
		return additionalName;
	}

	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
	}

	public void setAdditionalName(
		UnsafeSupplier<String, Exception> additionalNameUnsafeSupplier) {

		try {
			additionalName = additionalNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String additionalName;

	public String getAlternateName() {
		return alternateName;
	}

	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;
	}

	public void setAlternateName(
		UnsafeSupplier<String, Exception> alternateNameUnsafeSupplier) {

		try {
			alternateName = alternateNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String alternateName;

	public AssetLibraryBrief[] getAssetLibraryBriefs() {
		return assetLibraryBriefs;
	}

	public void setAssetLibraryBriefs(AssetLibraryBrief[] assetLibraryBriefs) {
		this.assetLibraryBriefs = assetLibraryBriefs;
	}

	public void setAssetLibraryBriefs(
		UnsafeSupplier<AssetLibraryBrief[], Exception>
			assetLibraryBriefsUnsafeSupplier) {

		try {
			assetLibraryBriefs = assetLibraryBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AssetLibraryBrief[] assetLibraryBriefs;

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setBirthDate(
		UnsafeSupplier<Date, Exception> birthDateUnsafeSupplier) {

		try {
			birthDate = birthDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date birthDate;

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public void setCurrentPassword(
		UnsafeSupplier<String, Exception> currentPasswordUnsafeSupplier) {

		try {
			currentPassword = currentPasswordUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String currentPassword;

	public com.liferay.headless.admin.user.client.custom.field.CustomField[]
		getCustomFields() {

		return customFields;
	}

	public void setCustomFields(
		com.liferay.headless.admin.user.client.custom.field.CustomField[]
			customFields) {

		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.headless.admin.user.client.custom.field.CustomField[],
			 Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.admin.user.client.custom.field.CustomField[]
		customFields;

	public String getDashboardURL() {
		return dashboardURL;
	}

	public void setDashboardURL(String dashboardURL) {
		this.dashboardURL = dashboardURL;
	}

	public void setDashboardURL(
		UnsafeSupplier<String, Exception> dashboardURLUnsafeSupplier) {

		try {
			dashboardURL = dashboardURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String dashboardURL;

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

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public void setFamilyName(
		UnsafeSupplier<String, Exception> familyNameUnsafeSupplier) {

		try {
			familyName = familyNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String familyName;

	public Gender getGender() {
		return gender;
	}

	public String getGenderAsString() {
		if (gender == null) {
			return null;
		}

		return gender.toString();
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setGender(
		UnsafeSupplier<Gender, Exception> genderUnsafeSupplier) {

		try {
			gender = genderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Gender gender;

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public void setGivenName(
		UnsafeSupplier<String, Exception> givenNameUnsafeSupplier) {

		try {
			givenName = givenNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String givenName;

	public Boolean getHasLoginDate() {
		return hasLoginDate;
	}

	public void setHasLoginDate(Boolean hasLoginDate) {
		this.hasLoginDate = hasLoginDate;
	}

	public void setHasLoginDate(
		UnsafeSupplier<Boolean, Exception> hasLoginDateUnsafeSupplier) {

		try {
			hasLoginDate = hasLoginDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean hasLoginDate;

	public String getHonorificPrefix() {
		return honorificPrefix;
	}

	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;
	}

	public void setHonorificPrefix(
		UnsafeSupplier<String, Exception> honorificPrefixUnsafeSupplier) {

		try {
			honorificPrefix = honorificPrefixUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String honorificPrefix;

	public String getHonorificSuffix() {
		return honorificSuffix;
	}

	public void setHonorificSuffix(String honorificSuffix) {
		this.honorificSuffix = honorificSuffix;
	}

	public void setHonorificSuffix(
		UnsafeSupplier<String, Exception> honorificSuffixUnsafeSupplier) {

		try {
			honorificSuffix = honorificSuffixUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String honorificSuffix;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setImage(
		UnsafeSupplier<String, Exception> imageUnsafeSupplier) {

		try {
			image = imageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String image;

	public String getImageExternalReferenceCode() {
		return imageExternalReferenceCode;
	}

	public void setImageExternalReferenceCode(
		String imageExternalReferenceCode) {

		this.imageExternalReferenceCode = imageExternalReferenceCode;
	}

	public void setImageExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			imageExternalReferenceCodeUnsafeSupplier) {

		try {
			imageExternalReferenceCode =
				imageExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String imageExternalReferenceCode;

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	public void setImageId(
		UnsafeSupplier<Long, Exception> imageIdUnsafeSupplier) {

		try {
			imageId = imageIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long imageId;

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public void setJobTitle(
		UnsafeSupplier<String, Exception> jobTitleUnsafeSupplier) {

		try {
			jobTitle = jobTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String jobTitle;

	public String[] getKeywords() {
		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	public void setKeywords(
		UnsafeSupplier<String[], Exception> keywordsUnsafeSupplier) {

		try {
			keywords = keywordsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] keywords;

	public String getLanguageDisplayName() {
		return languageDisplayName;
	}

	public void setLanguageDisplayName(String languageDisplayName) {
		this.languageDisplayName = languageDisplayName;
	}

	public void setLanguageDisplayName(
		UnsafeSupplier<String, Exception> languageDisplayNameUnsafeSupplier) {

		try {
			languageDisplayName = languageDisplayNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String languageDisplayName;

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	public void setLanguageId(
		UnsafeSupplier<String, Exception> languageIdUnsafeSupplier) {

		try {
			languageId = languageIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String languageId;

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public void setLastLoginDate(
		UnsafeSupplier<Date, Exception> lastLoginDateUnsafeSupplier) {

		try {
			lastLoginDate = lastLoginDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date lastLoginDate;

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

	public OrganizationBrief[] getOrganizationBriefs() {
		return organizationBriefs;
	}

	public void setOrganizationBriefs(OrganizationBrief[] organizationBriefs) {
		this.organizationBriefs = organizationBriefs;
	}

	public void setOrganizationBriefs(
		UnsafeSupplier<OrganizationBrief[], Exception>
			organizationBriefsUnsafeSupplier) {

		try {
			organizationBriefs = organizationBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected OrganizationBrief[] organizationBriefs;

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

	public com.liferay.headless.admin.user.client.permission.Permission[]
		getPermissions() {

		return permissions;
	}

	public void setPermissions(
		com.liferay.headless.admin.user.client.permission.Permission[]
			permissions) {

		this.permissions = permissions;
	}

	public void setPermissions(
		UnsafeSupplier
			<com.liferay.headless.admin.user.client.permission.Permission[],
			 Exception> permissionsUnsafeSupplier) {

		try {
			permissions = permissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.admin.user.client.permission.Permission[]
		permissions;

	public String getProfileURL() {
		return profileURL;
	}

	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;
	}

	public void setProfileURL(
		UnsafeSupplier<String, Exception> profileURLUnsafeSupplier) {

		try {
			profileURL = profileURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String profileURL;

	public RoleBrief[] getRoleBriefs() {
		return roleBriefs;
	}

	public void setRoleBriefs(RoleBrief[] roleBriefs) {
		this.roleBriefs = roleBriefs;
	}

	public void setRoleBriefs(
		UnsafeSupplier<RoleBrief[], Exception> roleBriefsUnsafeSupplier) {

		try {
			roleBriefs = roleBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RoleBrief[] roleBriefs;

	public SiteBrief[] getSiteBriefs() {
		return siteBriefs;
	}

	public void setSiteBriefs(SiteBrief[] siteBriefs) {
		this.siteBriefs = siteBriefs;
	}

	public void setSiteBriefs(
		UnsafeSupplier<SiteBrief[], Exception> siteBriefsUnsafeSupplier) {

		try {
			siteBriefs = siteBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SiteBrief[] siteBriefs;

	public Status getStatus() {
		return status;
	}

	public String getStatusAsString() {
		if (status == null) {
			return null;
		}

		return status.toString();
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status status;

	public TaxonomyCategoryBrief[] getTaxonomyCategoryBriefs() {
		return taxonomyCategoryBriefs;
	}

	public void setTaxonomyCategoryBriefs(
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs) {

		this.taxonomyCategoryBriefs = taxonomyCategoryBriefs;
	}

	public void setTaxonomyCategoryBriefs(
		UnsafeSupplier<TaxonomyCategoryBrief[], Exception>
			taxonomyCategoryBriefsUnsafeSupplier) {

		try {
			taxonomyCategoryBriefs = taxonomyCategoryBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TaxonomyCategoryBrief[] taxonomyCategoryBriefs;

	public UserAccountContactInformation getUserAccountContactInformation() {
		return userAccountContactInformation;
	}

	public void setUserAccountContactInformation(
		UserAccountContactInformation userAccountContactInformation) {

		this.userAccountContactInformation = userAccountContactInformation;
	}

	public void setUserAccountContactInformation(
		UnsafeSupplier<UserAccountContactInformation, Exception>
			userAccountContactInformationUnsafeSupplier) {

		try {
			userAccountContactInformation =
				userAccountContactInformationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserAccountContactInformation userAccountContactInformation;

	public UserGroupBrief[] getUserGroupBriefs() {
		return userGroupBriefs;
	}

	public void setUserGroupBriefs(UserGroupBrief[] userGroupBriefs) {
		this.userGroupBriefs = userGroupBriefs;
	}

	public void setUserGroupBriefs(
		UnsafeSupplier<UserGroupBrief[], Exception>
			userGroupBriefsUnsafeSupplier) {

		try {
			userGroupBriefs = userGroupBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserGroupBrief[] userGroupBriefs;

	@Override
	public UserAccount clone() throws CloneNotSupportedException {
		return (UserAccount)super.clone();
	}

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
		return UserAccountSerDes.toJSON(this);
	}

	public static enum Gender {

		MALE("Male"), FEMALE("Female");

		public static Gender create(String value) {
			for (Gender gender : values()) {
				if (Objects.equals(gender.getValue(), value) ||
					Objects.equals(gender.name(), value)) {

					return gender;
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

		private Gender(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum Status {

		ACTIVE("Active"), INACTIVE("Inactive");

		public static Status create(String value) {
			for (Status status : values()) {
				if (Objects.equals(status.getValue(), value) ||
					Objects.equals(status.name(), value)) {

					return status;
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

		private Status(String value) {
			_value = value;
		}

		private final String _value;

	}

}