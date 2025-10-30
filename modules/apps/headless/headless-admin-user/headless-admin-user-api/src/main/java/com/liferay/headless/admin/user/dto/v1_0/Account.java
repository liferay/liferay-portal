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

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "An account represents an external account, for example a customer business.",
	value = "Account"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Account")
public class Account implements Serializable {

	public static Account toDTO(String json) {
		return ObjectMapperUtil.readValue(Account.class, json);
	}

	public static Account unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Account.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The account's contact information."
	)
	@Valid
	public AccountContactInformation getAccountContactInformation() {
		if (_accountContactInformationSupplier != null) {
			accountContactInformation =
				_accountContactInformationSupplier.get();

			_accountContactInformationSupplier = null;
		}

		return accountContactInformation;
	}

	public void setAccountContactInformation(
		AccountContactInformation accountContactInformation) {

		this.accountContactInformation = accountContactInformation;

		_accountContactInformationSupplier = null;
	}

	@JsonIgnore
	public void setAccountContactInformation(
		UnsafeSupplier<AccountContactInformation, Exception>
			accountContactInformationUnsafeSupplier) {

		_accountContactInformationSupplier = () -> {
			try {
				return accountContactInformationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The account's contact information.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected AccountContactInformation accountContactInformation;

	@JsonIgnore
	private Supplier<AccountContactInformation>
		_accountContactInformationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the account's account groups."
	)
	@Valid
	public AccountGroupBrief[] getAccountGroupBriefs() {
		if (_accountGroupBriefsSupplier != null) {
			accountGroupBriefs = _accountGroupBriefsSupplier.get();

			_accountGroupBriefsSupplier = null;
		}

		return accountGroupBriefs;
	}

	public void setAccountGroupBriefs(AccountGroupBrief[] accountGroupBriefs) {
		this.accountGroupBriefs = accountGroupBriefs;

		_accountGroupBriefsSupplier = null;
	}

	@JsonIgnore
	public void setAccountGroupBriefs(
		UnsafeSupplier<AccountGroupBrief[], Exception>
			accountGroupBriefsUnsafeSupplier) {

		_accountGroupBriefsSupplier = () -> {
			try {
				return accountGroupBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of the account's account groups.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AccountGroupBrief[] accountGroupBriefs;

	@JsonIgnore
	private Supplier<AccountGroupBrief[]> _accountGroupBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the account's roles."
	)
	@Valid
	public AccountRole[] getAccountRoles() {
		if (_accountRolesSupplier != null) {
			accountRoles = _accountRolesSupplier.get();

			_accountRolesSupplier = null;
		}

		return accountRoles;
	}

	public void setAccountRoles(AccountRole[] accountRoles) {
		this.accountRoles = accountRoles;

		_accountRolesSupplier = null;
	}

	@JsonIgnore
	public void setAccountRoles(
		UnsafeSupplier<AccountRole[], Exception> accountRolesUnsafeSupplier) {

		_accountRolesSupplier = () -> {
			try {
				return accountRolesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of the account's roles.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AccountRole[] accountRoles;

	@JsonIgnore
	private Supplier<AccountRole[]> _accountRolesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The users linked to the account"
	)
	@Valid
	public UserAccount[] getAccountUserAccounts() {
		if (_accountUserAccountsSupplier != null) {
			accountUserAccounts = _accountUserAccountsSupplier.get();

			_accountUserAccountsSupplier = null;
		}

		return accountUserAccounts;
	}

	public void setAccountUserAccounts(UserAccount[] accountUserAccounts) {
		this.accountUserAccounts = accountUserAccounts;

		_accountUserAccountsSupplier = null;
	}

	@JsonIgnore
	public void setAccountUserAccounts(
		UnsafeSupplier<UserAccount[], Exception>
			accountUserAccountsUnsafeSupplier) {

		_accountUserAccountsSupplier = () -> {
			try {
				return accountUserAccountsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The users linked to the account")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected UserAccount[] accountUserAccounts;

	@JsonIgnore
	private Supplier<UserAccount[]> _accountUserAccountsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Block of actions allowed by the user making the request."
	)
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

	@GraphQLField(
		description = "Block of actions allowed by the user making the request."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user who created the account."
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

	@GraphQLField(description = "The user who created the account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

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
		description = "The account's creation date."
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

	@GraphQLField(description = "The account's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The account's most recent modification date."
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

	@GraphQLField(description = "The account's most recent modification date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getDefaultBillingAddressExternalReferenceCode() {
		if (_defaultBillingAddressExternalReferenceCodeSupplier != null) {
			defaultBillingAddressExternalReferenceCode =
				_defaultBillingAddressExternalReferenceCodeSupplier.get();

			_defaultBillingAddressExternalReferenceCodeSupplier = null;
		}

		return defaultBillingAddressExternalReferenceCode;
	}

	public void setDefaultBillingAddressExternalReferenceCode(
		String defaultBillingAddressExternalReferenceCode) {

		this.defaultBillingAddressExternalReferenceCode =
			defaultBillingAddressExternalReferenceCode;

		_defaultBillingAddressExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setDefaultBillingAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			defaultBillingAddressExternalReferenceCodeUnsafeSupplier) {

		_defaultBillingAddressExternalReferenceCodeSupplier = () -> {
			try {
				return defaultBillingAddressExternalReferenceCodeUnsafeSupplier.
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String defaultBillingAddressExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_defaultBillingAddressExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getDefaultBillingAddressId() {
		if (_defaultBillingAddressIdSupplier != null) {
			defaultBillingAddressId = _defaultBillingAddressIdSupplier.get();

			_defaultBillingAddressIdSupplier = null;
		}

		return defaultBillingAddressId;
	}

	public void setDefaultBillingAddressId(Long defaultBillingAddressId) {
		this.defaultBillingAddressId = defaultBillingAddressId;

		_defaultBillingAddressIdSupplier = null;
	}

	@JsonIgnore
	public void setDefaultBillingAddressId(
		UnsafeSupplier<Long, Exception> defaultBillingAddressIdUnsafeSupplier) {

		_defaultBillingAddressIdSupplier = () -> {
			try {
				return defaultBillingAddressIdUnsafeSupplier.get();
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
	protected Long defaultBillingAddressId;

	@JsonIgnore
	private Supplier<Long> _defaultBillingAddressIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getDefaultShippingAddressExternalReferenceCode() {
		if (_defaultShippingAddressExternalReferenceCodeSupplier != null) {
			defaultShippingAddressExternalReferenceCode =
				_defaultShippingAddressExternalReferenceCodeSupplier.get();

			_defaultShippingAddressExternalReferenceCodeSupplier = null;
		}

		return defaultShippingAddressExternalReferenceCode;
	}

	public void setDefaultShippingAddressExternalReferenceCode(
		String defaultShippingAddressExternalReferenceCode) {

		this.defaultShippingAddressExternalReferenceCode =
			defaultShippingAddressExternalReferenceCode;

		_defaultShippingAddressExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setDefaultShippingAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			defaultShippingAddressExternalReferenceCodeUnsafeSupplier) {

		_defaultShippingAddressExternalReferenceCodeSupplier = () -> {
			try {
				return defaultShippingAddressExternalReferenceCodeUnsafeSupplier.
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String defaultShippingAddressExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_defaultShippingAddressExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getDefaultShippingAddressId() {
		if (_defaultShippingAddressIdSupplier != null) {
			defaultShippingAddressId = _defaultShippingAddressIdSupplier.get();

			_defaultShippingAddressIdSupplier = null;
		}

		return defaultShippingAddressId;
	}

	public void setDefaultShippingAddressId(Long defaultShippingAddressId) {
		this.defaultShippingAddressId = defaultShippingAddressId;

		_defaultShippingAddressIdSupplier = null;
	}

	@JsonIgnore
	public void setDefaultShippingAddressId(
		UnsafeSupplier<Long, Exception>
			defaultShippingAddressIdUnsafeSupplier) {

		_defaultShippingAddressIdSupplier = () -> {
			try {
				return defaultShippingAddressIdUnsafeSupplier.get();
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
	protected Long defaultShippingAddressId;

	@JsonIgnore
	private Supplier<Long> _defaultShippingAddressIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
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
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The account's email domains. Users assigned to this account generally will have email addresses under one of these domains."
	)
	public String[] getDomains() {
		if (_domainsSupplier != null) {
			domains = _domainsSupplier.get();

			_domainsSupplier = null;
		}

		return domains;
	}

	public void setDomains(String[] domains) {
		this.domains = domains;

		_domainsSupplier = null;
	}

	@JsonIgnore
	public void setDomains(
		UnsafeSupplier<String[], Exception> domainsUnsafeSupplier) {

		_domainsSupplier = () -> {
			try {
				return domainsUnsafeSupplier.get();
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
		description = "The account's email domains. Users assigned to this account generally will have email addresses under one of these domains."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] domains;

	@JsonIgnore
	private Supplier<String[]> _domainsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The optional external key of this account."
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

	@GraphQLField(description = "The optional external key of this account.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of keywords describing the account."
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

	@GraphQLField(description = "A list of keywords describing the account.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] keywords;

	@JsonIgnore
	private Supplier<String[]> _keywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLogoBase64() {
		if (_logoBase64Supplier != null) {
			logoBase64 = _logoBase64Supplier.get();

			_logoBase64Supplier = null;
		}

		return logoBase64;
	}

	public void setLogoBase64(String logoBase64) {
		this.logoBase64 = logoBase64;

		_logoBase64Supplier = null;
	}

	@JsonIgnore
	public void setLogoBase64(
		UnsafeSupplier<String, Exception> logoBase64UnsafeSupplier) {

		_logoBase64Supplier = () -> {
			try {
				return logoBase64UnsafeSupplier.get();
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
	protected String logoBase64;

	@JsonIgnore
	private Supplier<String> _logoBase64Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getLogoExternalReferenceCode() {
		if (_logoExternalReferenceCodeSupplier != null) {
			logoExternalReferenceCode =
				_logoExternalReferenceCodeSupplier.get();

			_logoExternalReferenceCodeSupplier = null;
		}

		return logoExternalReferenceCode;
	}

	public void setLogoExternalReferenceCode(String logoExternalReferenceCode) {
		this.logoExternalReferenceCode = logoExternalReferenceCode;

		_logoExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setLogoExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			logoExternalReferenceCodeUnsafeSupplier) {

		_logoExternalReferenceCodeSupplier = () -> {
			try {
				return logoExternalReferenceCodeUnsafeSupplier.get();
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
	protected String logoExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _logoExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getLogoId() {
		if (_logoIdSupplier != null) {
			logoId = _logoIdSupplier.get();

			_logoIdSupplier = null;
		}

		return logoId;
	}

	public void setLogoId(Long logoId) {
		this.logoId = logoId;

		_logoIdSupplier = null;
	}

	@JsonIgnore
	public void setLogoId(
		UnsafeSupplier<Long, Exception> logoIdUnsafeSupplier) {

		_logoIdSupplier = () -> {
			try {
				return logoIdUnsafeSupplier.get();
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
	protected Long logoId;

	@JsonIgnore
	private Supplier<Long> _logoIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLogoURL() {
		if (_logoURLSupplier != null) {
			logoURL = _logoURLSupplier.get();

			_logoURLSupplier = null;
		}

		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;

		_logoURLSupplier = null;
	}

	@JsonIgnore
	public void setLogoURL(
		UnsafeSupplier<String, Exception> logoURLUnsafeSupplier) {

		_logoURLSupplier = () -> {
			try {
				return logoURLUnsafeSupplier.get();
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
	protected String logoURL;

	@JsonIgnore
	private Supplier<String> _logoURLSupplier;

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
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of this account's associated users."
	)
	public Integer getNumberOfUsers() {
		if (_numberOfUsersSupplier != null) {
			numberOfUsers = _numberOfUsersSupplier.get();

			_numberOfUsersSupplier = null;
		}

		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;

		_numberOfUsersSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfUsers(
		UnsafeSupplier<Integer, Exception> numberOfUsersUnsafeSupplier) {

		_numberOfUsersSupplier = () -> {
			try {
				return numberOfUsersUnsafeSupplier.get();
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
		description = "The number of this account's associated users."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfUsers;

	@JsonIgnore
	private Supplier<Integer> _numberOfUsersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getOrganizationExternalReferenceCodes() {
		if (_organizationExternalReferenceCodesSupplier != null) {
			organizationExternalReferenceCodes =
				_organizationExternalReferenceCodesSupplier.get();

			_organizationExternalReferenceCodesSupplier = null;
		}

		return organizationExternalReferenceCodes;
	}

	public void setOrganizationExternalReferenceCodes(
		String[] organizationExternalReferenceCodes) {

		this.organizationExternalReferenceCodes =
			organizationExternalReferenceCodes;

		_organizationExternalReferenceCodesSupplier = null;
	}

	@JsonIgnore
	public void setOrganizationExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			organizationExternalReferenceCodesUnsafeSupplier) {

		_organizationExternalReferenceCodesSupplier = () -> {
			try {
				return organizationExternalReferenceCodesUnsafeSupplier.get();
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
	protected String[] organizationExternalReferenceCodes;

	@JsonIgnore
	private Supplier<String[]> _organizationExternalReferenceCodesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getOrganizationIds() {
		if (_organizationIdsSupplier != null) {
			organizationIds = _organizationIdsSupplier.get();

			_organizationIdsSupplier = null;
		}

		return organizationIds;
	}

	public void setOrganizationIds(Long[] organizationIds) {
		this.organizationIds = organizationIds;

		_organizationIdsSupplier = null;
	}

	@JsonIgnore
	public void setOrganizationIds(
		UnsafeSupplier<Long[], Exception> organizationIdsUnsafeSupplier) {

		_organizationIdsSupplier = () -> {
			try {
				return organizationIdsUnsafeSupplier.get();
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
	protected Long[] organizationIds;

	@JsonIgnore
	private Supplier<Long[]> _organizationIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getParentAccountExternalReferenceCode() {
		if (_parentAccountExternalReferenceCodeSupplier != null) {
			parentAccountExternalReferenceCode =
				_parentAccountExternalReferenceCodeSupplier.get();

			_parentAccountExternalReferenceCodeSupplier = null;
		}

		return parentAccountExternalReferenceCode;
	}

	public void setParentAccountExternalReferenceCode(
		String parentAccountExternalReferenceCode) {

		this.parentAccountExternalReferenceCode =
			parentAccountExternalReferenceCode;

		_parentAccountExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setParentAccountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentAccountExternalReferenceCodeUnsafeSupplier) {

		_parentAccountExternalReferenceCodeSupplier = () -> {
			try {
				return parentAccountExternalReferenceCodeUnsafeSupplier.get();
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
	protected String parentAccountExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _parentAccountExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getParentAccountId() {
		if (_parentAccountIdSupplier != null) {
			parentAccountId = _parentAccountIdSupplier.get();

			_parentAccountIdSupplier = null;
		}

		return parentAccountId;
	}

	public void setParentAccountId(Long parentAccountId) {
		this.parentAccountId = parentAccountId;

		_parentAccountIdSupplier = null;
	}

	@JsonIgnore
	public void setParentAccountId(
		UnsafeSupplier<Long, Exception> parentAccountIdUnsafeSupplier) {

		_parentAccountIdSupplier = () -> {
			try {
				return parentAccountIdUnsafeSupplier.get();
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
	protected Long parentAccountId;

	@JsonIgnore
	private Supplier<Long> _parentAccountIdSupplier;

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
		description = "The addresses linked to the account"
	)
	@Valid
	public PostalAddress[] getPostalAddresses() {
		if (_postalAddressesSupplier != null) {
			postalAddresses = _postalAddressesSupplier.get();

			_postalAddressesSupplier = null;
		}

		return postalAddresses;
	}

	public void setPostalAddresses(PostalAddress[] postalAddresses) {
		this.postalAddresses = postalAddresses;

		_postalAddressesSupplier = null;
	}

	@JsonIgnore
	public void setPostalAddresses(
		UnsafeSupplier<PostalAddress[], Exception>
			postalAddressesUnsafeSupplier) {

		_postalAddressesSupplier = () -> {
			try {
				return postalAddressesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The addresses linked to the account")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PostalAddress[] postalAddresses;

	@JsonIgnore
	private Supplier<PostalAddress[]> _postalAddressesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer status;

	@JsonIgnore
	private Supplier<Integer> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Abcd1234")
	public String getTaxId() {
		if (_taxIdSupplier != null) {
			taxId = _taxIdSupplier.get();

			_taxIdSupplier = null;
		}

		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;

		_taxIdSupplier = null;
	}

	@JsonIgnore
	public void setTaxId(
		UnsafeSupplier<String, Exception> taxIdUnsafeSupplier) {

		_taxIdSupplier = () -> {
			try {
				return taxIdUnsafeSupplier.get();
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
	protected String taxId;

	@JsonIgnore
	private Supplier<String> _taxIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The categories associated with this account."
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

	@GraphQLField(description = "The categories associated with this account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected TaxonomyCategoryBrief[] taxonomyCategoryBriefs;

	@JsonIgnore
	private Supplier<TaxonomyCategoryBrief[]> _taxonomyCategoryBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
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
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		AccountContactInformation accountContactInformation =
			getAccountContactInformation();

		if (accountContactInformation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountContactInformation\": ");

			sb.append(String.valueOf(accountContactInformation));
		}

		AccountGroupBrief[] accountGroupBriefs = getAccountGroupBriefs();

		if (accountGroupBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupBriefs\": ");

			sb.append("[");

			for (int i = 0; i < accountGroupBriefs.length; i++) {
				sb.append(String.valueOf(accountGroupBriefs[i]));

				if ((i + 1) < accountGroupBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		AccountRole[] accountRoles = getAccountRoles();

		if (accountRoles != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountRoles\": ");

			sb.append("[");

			for (int i = 0; i < accountRoles.length; i++) {
				sb.append(String.valueOf(accountRoles[i]));

				if ((i + 1) < accountRoles.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		UserAccount[] accountUserAccounts = getAccountUserAccounts();

		if (accountUserAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountUserAccounts\": ");

			sb.append("[");

			for (int i = 0; i < accountUserAccounts.length; i++) {
				sb.append(String.valueOf(accountUserAccounts[i]));

				if ((i + 1) < accountUserAccounts.length) {
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

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(creator));
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

		String defaultBillingAddressExternalReferenceCode =
			getDefaultBillingAddressExternalReferenceCode();

		if (defaultBillingAddressExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultBillingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(defaultBillingAddressExternalReferenceCode));

			sb.append("\"");
		}

		Long defaultBillingAddressId = getDefaultBillingAddressId();

		if (defaultBillingAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultBillingAddressId\": ");

			sb.append(defaultBillingAddressId);
		}

		String defaultShippingAddressExternalReferenceCode =
			getDefaultShippingAddressExternalReferenceCode();

		if (defaultShippingAddressExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultShippingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(defaultShippingAddressExternalReferenceCode));

			sb.append("\"");
		}

		Long defaultShippingAddressId = getDefaultShippingAddressId();

		if (defaultShippingAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultShippingAddressId\": ");

			sb.append(defaultShippingAddressId);
		}

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		String[] domains = getDomains();

		if (domains != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"domains\": ");

			sb.append("[");

			for (int i = 0; i < domains.length; i++) {
				sb.append("\"");

				sb.append(_escape(domains[i]));

				sb.append("\"");

				if ((i + 1) < domains.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
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

		String logoBase64 = getLogoBase64();

		if (logoBase64 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoBase64\": ");

			sb.append("\"");

			sb.append(_escape(logoBase64));

			sb.append("\"");
		}

		String logoExternalReferenceCode = getLogoExternalReferenceCode();

		if (logoExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(logoExternalReferenceCode));

			sb.append("\"");
		}

		Long logoId = getLogoId();

		if (logoId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoId\": ");

			sb.append(logoId);
		}

		String logoURL = getLogoURL();

		if (logoURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoURL\": ");

			sb.append("\"");

			sb.append(_escape(logoURL));

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

		Integer numberOfUsers = getNumberOfUsers();

		if (numberOfUsers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUsers\": ");

			sb.append(numberOfUsers);
		}

		String[] organizationExternalReferenceCodes =
			getOrganizationExternalReferenceCodes();

		if (organizationExternalReferenceCodes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0; i < organizationExternalReferenceCodes.length;
				 i++) {

				sb.append("\"");

				sb.append(_escape(organizationExternalReferenceCodes[i]));

				sb.append("\"");

				if ((i + 1) < organizationExternalReferenceCodes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long[] organizationIds = getOrganizationIds();

		if (organizationIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationIds\": ");

			sb.append("[");

			for (int i = 0; i < organizationIds.length; i++) {
				sb.append(organizationIds[i]);

				if ((i + 1) < organizationIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String parentAccountExternalReferenceCode =
			getParentAccountExternalReferenceCode();

		if (parentAccountExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentAccountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(parentAccountExternalReferenceCode));

			sb.append("\"");
		}

		Long parentAccountId = getParentAccountId();

		if (parentAccountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentAccountId\": ");

			sb.append(parentAccountId);
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

		PostalAddress[] postalAddresses = getPostalAddresses();

		if (postalAddresses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postalAddresses\": ");

			sb.append("[");

			for (int i = 0; i < postalAddresses.length; i++) {
				sb.append(String.valueOf(postalAddresses[i]));

				if ((i + 1) < postalAddresses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Integer status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(status);
		}

		String taxId = getTaxId();

		if (taxId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxId\": ");

			sb.append("\"");

			sb.append(_escape(taxId));

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

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(type);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.Account",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		BUSINESS("business"), GUEST("guest"), PERSON("person"),
		SUPPLIER("supplier");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
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

		private Type(String value) {
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