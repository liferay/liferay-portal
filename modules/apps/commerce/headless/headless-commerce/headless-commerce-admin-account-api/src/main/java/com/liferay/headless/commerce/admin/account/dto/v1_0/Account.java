/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.dto.v1_0;

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

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;

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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Commerce buyer organization or end customer that places orders. Owns addresses, members (users with account-scoped roles), and links to organizations and account groups. Created on POST, updated on PATCH, and returned by the matching list endpoint.",
	value = "Account"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Commerce buyer organization or end customer that places orders. Owns addresses, members (users with account-scoped roles), and links to organizations and account groups. Created on POST, updated on PATCH, and returned by the matching list endpoint.",
	requiredProperties = {"externalReferenceCode", "name"}
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
		description = "Addresses owned by this account, accepted on POST or PATCH and persisted in the same transaction; existing addresses are matched by `id` and updated when supplied, otherwise added. Not returned on read; fetch them through the addresses sub-resource.",
		example = "[{city=Diamond Bar, countryISOCode=US, defaultBilling=true, defaultShipping=true, description=right stairs, first room on the left, id=31130, latitude=33.9976884, longitude=-117.8144595, name=Alessio Antonio Rendina, phoneNumber=(123) 456 7890, regionISOCode=CA, street1=1400 Montefino Ave, street2=1st floor, street3=suite 200, zip=91765}]"
	)
	@Valid
	public AccountAddress[] getAccountAddresses() {
		if (_accountAddressesSupplier != null) {
			accountAddresses = _accountAddressesSupplier.get();

			_accountAddressesSupplier = null;
		}

		return accountAddresses;
	}

	public void setAccountAddresses(AccountAddress[] accountAddresses) {
		this.accountAddresses = accountAddresses;

		_accountAddressesSupplier = null;
	}

	@JsonIgnore
	public void setAccountAddresses(
		UnsafeSupplier<AccountAddress[], Exception>
			accountAddressesUnsafeSupplier) {

		_accountAddressesSupplier = () -> {
			try {
				return accountAddressesUnsafeSupplier.get();
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
		description = "Addresses owned by this account, accepted on POST or PATCH and persisted in the same transaction; existing addresses are matched by `id` and updated when supplied, otherwise added. Not returned on read; fetch them through the addresses sub-resource."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected AccountAddress[] accountAddresses;

	@JsonIgnore
	private Supplier<AccountAddress[]> _accountAddressesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Members (user-to-account links) on this account, each carrying the user's account-scoped roles, accepted on POST or PATCH and persisted in the same transaction; existing memberships are kept, never removed. Not returned on read; fetch them through the members sub-resource.",
		example = "[{description={en_US=Account Administrator Description US, hr_HR=Account Administrator Description HR, hu_HU=Account Administrator Description HU}}, {id=31256, name=Alessio Antonio Rendina, roles=null}]"
	)
	@Valid
	public AccountMember[] getAccountMembers() {
		if (_accountMembersSupplier != null) {
			accountMembers = _accountMembersSupplier.get();

			_accountMembersSupplier = null;
		}

		return accountMembers;
	}

	public void setAccountMembers(AccountMember[] accountMembers) {
		this.accountMembers = accountMembers;

		_accountMembersSupplier = null;
	}

	@JsonIgnore
	public void setAccountMembers(
		UnsafeSupplier<AccountMember[], Exception>
			accountMembersUnsafeSupplier) {

		_accountMembersSupplier = () -> {
			try {
				return accountMembersUnsafeSupplier.get();
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
		description = "Members (user-to-account links) on this account, each carrying the user's account-scoped roles, accepted on POST or PATCH and persisted in the same transaction; existing memberships are kept, never removed. Not returned on read; fetch them through the members sub-resource."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected AccountMember[] accountMembers;

	@JsonIgnore
	private Supplier<AccountMember[]> _accountMembersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Organizations linked to this account, accepted on POST or PATCH and persisted in the same transaction; existing links are kept, never removed. Not returned on read; fetch them through the organizations sub-resource.",
		example = "[{id=20546, name=Liferay Italy, organizationId=20433, treePath=/Liferay/Liferay Italy}]"
	)
	@Valid
	public AccountOrganization[] getAccountOrganizations() {
		if (_accountOrganizationsSupplier != null) {
			accountOrganizations = _accountOrganizationsSupplier.get();

			_accountOrganizationsSupplier = null;
		}

		return accountOrganizations;
	}

	public void setAccountOrganizations(
		AccountOrganization[] accountOrganizations) {

		this.accountOrganizations = accountOrganizations;

		_accountOrganizationsSupplier = null;
	}

	@JsonIgnore
	public void setAccountOrganizations(
		UnsafeSupplier<AccountOrganization[], Exception>
			accountOrganizationsUnsafeSupplier) {

		_accountOrganizationsSupplier = () -> {
			try {
				return accountOrganizationsUnsafeSupplier.get();
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
		description = "Organizations linked to this account, accepted on POST or PATCH and persisted in the same transaction; existing links are kept, never removed. Not returned on read; fetch them through the organizations sub-resource."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected AccountOrganization[] accountOrganizations;

	@JsonIgnore
	private Supplier<AccountOrganization[]> _accountOrganizationsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the account is active. Inactive accounts cannot place orders but remain visible in the admin UI. Defaults to true on create; mapped to the workflow status of the underlying account record (active when approved, inactive otherwise).",
		example = "true"
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
		description = "Whether the account is active. Inactive accounts cannot place orders but remain visible in the admin UI. Defaults to true on create; mapped to the workflow status of the underlying account record (active when approved, inactive otherwise)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Free-form custom field map populated by the platform expando mechanism. Keys are the registered custom field names; values follow the type declared on the field definition."
	)
	@Valid
	public Map<String, ?> getCustomFields() {
		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(Map<String, ?> customFields) {
		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier<Map<String, ?>, Exception> customFieldsUnsafeSupplier) {

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

	@GraphQLField(
		description = "Free-form custom field map populated by the platform expando mechanism. Keys are the registered custom field names; values follow the type declared on the field definition."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, ?> customFields;

	@JsonIgnore
	private Supplier<Map<String, ?>> _customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Creation timestamp in ISO 8601 (UTC). Read-only; set when the account is first persisted. Filterable and sortable via the OData query parameter.",
		example = "2017-07-21"
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
		description = "Creation timestamp in ISO 8601 (UTC). Read-only; set when the account is first persisted. Filterable and sortable via the OData query parameter."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Last modification timestamp in ISO 8601 (UTC). Read-only; updated on each save. Filterable and sortable via the OData query parameter.",
		example = "2017-07-21"
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
		description = "Last modification timestamp in ISO 8601 (UTC). Read-only; updated on each save. Filterable and sortable via the OData query parameter."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the address (FK identifier) used as the default billing address for the account. Setting this field clears the previous default billing flag on sibling addresses.",
		example = "10130"
	)
	public Long getDefaultBillingAccountAddressId() {
		if (_defaultBillingAccountAddressIdSupplier != null) {
			defaultBillingAccountAddressId =
				_defaultBillingAccountAddressIdSupplier.get();

			_defaultBillingAccountAddressIdSupplier = null;
		}

		return defaultBillingAccountAddressId;
	}

	public void setDefaultBillingAccountAddressId(
		Long defaultBillingAccountAddressId) {

		this.defaultBillingAccountAddressId = defaultBillingAccountAddressId;

		_defaultBillingAccountAddressIdSupplier = null;
	}

	@JsonIgnore
	public void setDefaultBillingAccountAddressId(
		UnsafeSupplier<Long, Exception>
			defaultBillingAccountAddressIdUnsafeSupplier) {

		_defaultBillingAccountAddressIdSupplier = () -> {
			try {
				return defaultBillingAccountAddressIdUnsafeSupplier.get();
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
		description = "Reference to the address (FK identifier) used as the default billing address for the account. Setting this field clears the previous default billing flag on sibling addresses."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long defaultBillingAccountAddressId;

	@JsonIgnore
	private Supplier<Long> _defaultBillingAccountAddressIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the address (FK identifier) used as the default shipping address for the account. Setting this field clears the previous default shipping flag on sibling addresses.",
		example = "10131"
	)
	public Long getDefaultShippingAccountAddressId() {
		if (_defaultShippingAccountAddressIdSupplier != null) {
			defaultShippingAccountAddressId =
				_defaultShippingAccountAddressIdSupplier.get();

			_defaultShippingAccountAddressIdSupplier = null;
		}

		return defaultShippingAccountAddressId;
	}

	public void setDefaultShippingAccountAddressId(
		Long defaultShippingAccountAddressId) {

		this.defaultShippingAccountAddressId = defaultShippingAccountAddressId;

		_defaultShippingAccountAddressIdSupplier = null;
	}

	@JsonIgnore
	public void setDefaultShippingAccountAddressId(
		UnsafeSupplier<Long, Exception>
			defaultShippingAccountAddressIdUnsafeSupplier) {

		_defaultShippingAccountAddressIdSupplier = () -> {
			try {
				return defaultShippingAccountAddressIdUnsafeSupplier.get();
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
		description = "Reference to the address (FK identifier) used as the default shipping address for the account. Setting this field clears the previous default shipping flag on sibling addresses."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long defaultShippingAccountAddressId;

	@JsonIgnore
	private Supplier<Long> _defaultShippingAccountAddressIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Email addresses associated with the account. The first entry is the primary contact email; on create or update only the first entry is persisted as the account email.",
		example = "[joe.1@commerce.com, joe.2@commerce.com, joe.3@commerce.com]"
	)
	public String[] getEmailAddresses() {
		if (_emailAddressesSupplier != null) {
			emailAddresses = _emailAddressesSupplier.get();

			_emailAddressesSupplier = null;
		}

		return emailAddresses;
	}

	public void setEmailAddresses(String[] emailAddresses) {
		this.emailAddresses = emailAddresses;

		_emailAddressesSupplier = null;
	}

	@JsonIgnore
	public void setEmailAddresses(
		UnsafeSupplier<String[], Exception> emailAddressesUnsafeSupplier) {

		_emailAddressesSupplier = () -> {
			try {
				return emailAddressesUnsafeSupplier.get();
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
		description = "Email addresses associated with the account. The first entry is the primary contact email; on create or update only the first entry is persisted as the account email."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] emailAddresses;

	@JsonIgnore
	private Supplier<String[]> _emailAddressesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per account within the company. Used as the addressing key on the by-externalReferenceCode endpoints.",
		example = "AB-34098-789-N"
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
		description = "Idempotency key for create and update; must be unique per account within the company. Used as the addressing key on the by-externalReferenceCode endpoints."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal primary key. Read-only; assigned by the system on create.",
		example = "30130"
	)
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

	@GraphQLField(
		description = "Internal primary key. Read-only; assigned by the system on create."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the document library entry (FK identifier) that stores the account logo image. Read-only on this schema; set through the dedicated logo upload endpoint.",
		example = "20078"
	)
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

	@GraphQLField(
		description = "Reference to the document library entry (FK identifier) that stores the account logo image. Read-only on this schema; set through the dedicated logo upload endpoint."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long logoId;

	@JsonIgnore
	private Supplier<Long> _logoIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Relative URL the back-office uses to fetch the account logo image. Computed from the logo identifier and a cache-busting token; read-only.",
		example = "/image/organization_logo?img_id=20078&t=1500000000000"
	)
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

	@GraphQLField(
		description = "Relative URL the back-office uses to fetch the account logo image. Computed from the logo identifier and a cache-busting token; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String logoURL;

	@JsonIgnore
	private Supplier<String> _logoURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of the account. Required on create. Filterable and sortable via the OData query parameter; matched by the search query parameter.",
		example = "Account Name"
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
		description = "Display name of the account. Required on create. Filterable and sortable via the OData query parameter; matched by the search query parameter."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether this account sits at the root of the account hierarchy. Read-only; true when the account has no parent account.",
		example = "true"
	)
	public Boolean getRoot() {
		if (_rootSupplier != null) {
			root = _rootSupplier.get();

			_rootSupplier = null;
		}

		return root;
	}

	public void setRoot(Boolean root) {
		this.root = root;

		_rootSupplier = null;
	}

	@JsonIgnore
	public void setRoot(UnsafeSupplier<Boolean, Exception> rootUnsafeSupplier) {
		_rootSupplier = () -> {
			try {
				return rootUnsafeSupplier.get();
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
		description = "Whether this account sits at the root of the account hierarchy. Read-only; true when the account has no parent account."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean root;

	@JsonIgnore
	private Supplier<Boolean> _rootSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Tax identifier of the account such as a VAT or EIN number. Persisted as supplied and surfaced on invoices.",
		example = "Abcd1234"
	)
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

	@GraphQLField(
		description = "Tax identifier of the account such as a VAT or EIN number. Persisted as supplied and surfaced on invoices."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String taxId;

	@JsonIgnore
	private Supplier<String> _taxIdSupplier;

	@DecimalMax("2")
	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Integer account type -- 0=Guest, 1=Personal, 2=Business. Determines the available members and address flows. Defaults to Personal on create. Filterable and sortable via the OData query parameter.",
		example = "1"
	)
	public Integer getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(Integer type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Integer, Exception> typeUnsafeSupplier) {
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

	@GraphQLField(
		description = "Integer account type -- 0=Guest, 1=Personal, 2=Business. Determines the available members and address flows. Defaults to Personal on create. Filterable and sortable via the OData query parameter."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer type;

	@JsonIgnore
	private Supplier<Integer> _typeSupplier;

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

		AccountAddress[] accountAddresses = getAccountAddresses();

		if (accountAddresses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountAddresses\": ");

			sb.append("[");

			for (int i = 0; i < accountAddresses.length; i++) {
				sb.append(String.valueOf(accountAddresses[i]));

				if ((i + 1) < accountAddresses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		AccountMember[] accountMembers = getAccountMembers();

		if (accountMembers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountMembers\": ");

			sb.append("[");

			for (int i = 0; i < accountMembers.length; i++) {
				sb.append(String.valueOf(accountMembers[i]));

				if ((i + 1) < accountMembers.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		AccountOrganization[] accountOrganizations = getAccountOrganizations();

		if (accountOrganizations != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountOrganizations\": ");

			sb.append("[");

			for (int i = 0; i < accountOrganizations.length; i++) {
				sb.append(String.valueOf(accountOrganizations[i]));

				if ((i + 1) < accountOrganizations.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		Map<String, ?> customFields = getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(customFields));
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

		Long defaultBillingAccountAddressId =
			getDefaultBillingAccountAddressId();

		if (defaultBillingAccountAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultBillingAccountAddressId\": ");

			sb.append(defaultBillingAccountAddressId);
		}

		Long defaultShippingAccountAddressId =
			getDefaultShippingAccountAddressId();

		if (defaultShippingAccountAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultShippingAccountAddressId\": ");

			sb.append(defaultShippingAccountAddressId);
		}

		String[] emailAddresses = getEmailAddresses();

		if (emailAddresses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddresses\": ");

			sb.append("[");

			for (int i = 0; i < emailAddresses.length; i++) {
				sb.append("\"");

				sb.append(_escape(emailAddresses[i]));

				sb.append("\"");

				if ((i + 1) < emailAddresses.length) {
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

		Boolean root = getRoot();

		if (root != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"root\": ");

			sb.append(root);
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

		Integer type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(type);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.account.dto.v1_0.Account",
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
// LIFERAY-REST-BUILDER-HASH:-731757613