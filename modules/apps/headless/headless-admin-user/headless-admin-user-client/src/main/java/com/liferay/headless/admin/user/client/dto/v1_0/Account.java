/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.dto.v1_0;

import com.liferay.headless.admin.user.client.function.UnsafeSupplier;
import com.liferay.headless.admin.user.client.serdes.v1_0.AccountSerDes;

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
public class Account implements Cloneable, Serializable {

	public static Account toDTO(String json) {
		return AccountSerDes.toDTO(json);
	}

	public AccountContactInformation getAccountContactInformation() {
		return accountContactInformation;
	}

	public void setAccountContactInformation(
		AccountContactInformation accountContactInformation) {

		this.accountContactInformation = accountContactInformation;
	}

	public void setAccountContactInformation(
		UnsafeSupplier<AccountContactInformation, Exception>
			accountContactInformationUnsafeSupplier) {

		try {
			accountContactInformation =
				accountContactInformationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AccountContactInformation accountContactInformation;

	public AccountGroupBrief[] getAccountGroupBriefs() {
		return accountGroupBriefs;
	}

	public void setAccountGroupBriefs(AccountGroupBrief[] accountGroupBriefs) {
		this.accountGroupBriefs = accountGroupBriefs;
	}

	public void setAccountGroupBriefs(
		UnsafeSupplier<AccountGroupBrief[], Exception>
			accountGroupBriefsUnsafeSupplier) {

		try {
			accountGroupBriefs = accountGroupBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AccountGroupBrief[] accountGroupBriefs;

	public AccountRole[] getAccountRoles() {
		return accountRoles;
	}

	public void setAccountRoles(AccountRole[] accountRoles) {
		this.accountRoles = accountRoles;
	}

	public void setAccountRoles(
		UnsafeSupplier<AccountRole[], Exception> accountRolesUnsafeSupplier) {

		try {
			accountRoles = accountRolesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AccountRole[] accountRoles;

	public UserAccount[] getAccountUserAccounts() {
		return accountUserAccounts;
	}

	public void setAccountUserAccounts(UserAccount[] accountUserAccounts) {
		this.accountUserAccounts = accountUserAccounts;
	}

	public void setAccountUserAccounts(
		UnsafeSupplier<UserAccount[], Exception>
			accountUserAccountsUnsafeSupplier) {

		try {
			accountUserAccounts = accountUserAccountsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserAccount[] accountUserAccounts;

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

	public String getDefaultBillingAddressExternalReferenceCode() {
		return defaultBillingAddressExternalReferenceCode;
	}

	public void setDefaultBillingAddressExternalReferenceCode(
		String defaultBillingAddressExternalReferenceCode) {

		this.defaultBillingAddressExternalReferenceCode =
			defaultBillingAddressExternalReferenceCode;
	}

	public void setDefaultBillingAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			defaultBillingAddressExternalReferenceCodeUnsafeSupplier) {

		try {
			defaultBillingAddressExternalReferenceCode =
				defaultBillingAddressExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String defaultBillingAddressExternalReferenceCode;

	public Long getDefaultBillingAddressId() {
		return defaultBillingAddressId;
	}

	public void setDefaultBillingAddressId(Long defaultBillingAddressId) {
		this.defaultBillingAddressId = defaultBillingAddressId;
	}

	public void setDefaultBillingAddressId(
		UnsafeSupplier<Long, Exception> defaultBillingAddressIdUnsafeSupplier) {

		try {
			defaultBillingAddressId =
				defaultBillingAddressIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long defaultBillingAddressId;

	public String getDefaultShippingAddressExternalReferenceCode() {
		return defaultShippingAddressExternalReferenceCode;
	}

	public void setDefaultShippingAddressExternalReferenceCode(
		String defaultShippingAddressExternalReferenceCode) {

		this.defaultShippingAddressExternalReferenceCode =
			defaultShippingAddressExternalReferenceCode;
	}

	public void setDefaultShippingAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			defaultShippingAddressExternalReferenceCodeUnsafeSupplier) {

		try {
			defaultShippingAddressExternalReferenceCode =
				defaultShippingAddressExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String defaultShippingAddressExternalReferenceCode;

	public Long getDefaultShippingAddressId() {
		return defaultShippingAddressId;
	}

	public void setDefaultShippingAddressId(Long defaultShippingAddressId) {
		this.defaultShippingAddressId = defaultShippingAddressId;
	}

	public void setDefaultShippingAddressId(
		UnsafeSupplier<Long, Exception>
			defaultShippingAddressIdUnsafeSupplier) {

		try {
			defaultShippingAddressId =
				defaultShippingAddressIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long defaultShippingAddressId;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public String[] getDomains() {
		return domains;
	}

	public void setDomains(String[] domains) {
		this.domains = domains;
	}

	public void setDomains(
		UnsafeSupplier<String[], Exception> domainsUnsafeSupplier) {

		try {
			domains = domainsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] domains;

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

	public String getLogoExternalReferenceCode() {
		return logoExternalReferenceCode;
	}

	public void setLogoExternalReferenceCode(String logoExternalReferenceCode) {
		this.logoExternalReferenceCode = logoExternalReferenceCode;
	}

	public void setLogoExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			logoExternalReferenceCodeUnsafeSupplier) {

		try {
			logoExternalReferenceCode =
				logoExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String logoExternalReferenceCode;

	public Long getLogoId() {
		return logoId;
	}

	public void setLogoId(Long logoId) {
		this.logoId = logoId;
	}

	public void setLogoId(
		UnsafeSupplier<Long, Exception> logoIdUnsafeSupplier) {

		try {
			logoId = logoIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long logoId;

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public void setLogoURL(
		UnsafeSupplier<String, Exception> logoURLUnsafeSupplier) {

		try {
			logoURL = logoURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String logoURL;

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

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public void setNumberOfUsers(
		UnsafeSupplier<Integer, Exception> numberOfUsersUnsafeSupplier) {

		try {
			numberOfUsers = numberOfUsersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfUsers;

	public String[] getOrganizationExternalReferenceCodes() {
		return organizationExternalReferenceCodes;
	}

	public void setOrganizationExternalReferenceCodes(
		String[] organizationExternalReferenceCodes) {

		this.organizationExternalReferenceCodes =
			organizationExternalReferenceCodes;
	}

	public void setOrganizationExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			organizationExternalReferenceCodesUnsafeSupplier) {

		try {
			organizationExternalReferenceCodes =
				organizationExternalReferenceCodesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] organizationExternalReferenceCodes;

	public Long[] getOrganizationIds() {
		return organizationIds;
	}

	public void setOrganizationIds(Long[] organizationIds) {
		this.organizationIds = organizationIds;
	}

	public void setOrganizationIds(
		UnsafeSupplier<Long[], Exception> organizationIdsUnsafeSupplier) {

		try {
			organizationIds = organizationIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] organizationIds;

	public String getParentAccountExternalReferenceCode() {
		return parentAccountExternalReferenceCode;
	}

	public void setParentAccountExternalReferenceCode(
		String parentAccountExternalReferenceCode) {

		this.parentAccountExternalReferenceCode =
			parentAccountExternalReferenceCode;
	}

	public void setParentAccountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentAccountExternalReferenceCodeUnsafeSupplier) {

		try {
			parentAccountExternalReferenceCode =
				parentAccountExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentAccountExternalReferenceCode;

	public Long getParentAccountId() {
		return parentAccountId;
	}

	public void setParentAccountId(Long parentAccountId) {
		this.parentAccountId = parentAccountId;
	}

	public void setParentAccountId(
		UnsafeSupplier<Long, Exception> parentAccountIdUnsafeSupplier) {

		try {
			parentAccountId = parentAccountIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long parentAccountId;

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

	public PostalAddress[] getPostalAddresses() {
		return postalAddresses;
	}

	public void setPostalAddresses(PostalAddress[] postalAddresses) {
		this.postalAddresses = postalAddresses;
	}

	public void setPostalAddresses(
		UnsafeSupplier<PostalAddress[], Exception>
			postalAddressesUnsafeSupplier) {

		try {
			postalAddresses = postalAddressesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PostalAddress[] postalAddresses;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer status;

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public void setTaxId(
		UnsafeSupplier<String, Exception> taxIdUnsafeSupplier) {

		try {
			taxId = taxIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String taxId;

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

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Type type;

	@Override
	public Account clone() throws CloneNotSupportedException {
		return (Account)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Account)) {
			return false;
		}

		Account account = (Account)object;

		return Objects.equals(toString(), account.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AccountSerDes.toJSON(this);
	}

	public static enum Type {

		BUSINESS("business"), GUEST("guest"), PERSON("person"),
		SUPPLIER("supplier");

		public static Type create(String value) {
			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value) ||
					Objects.equals(type.name(), value)) {

					return type;
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

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

}