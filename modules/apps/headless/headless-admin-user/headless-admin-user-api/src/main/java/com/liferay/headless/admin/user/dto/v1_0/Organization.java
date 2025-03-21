/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	description = "Represents an organization. Organizations can contain other organizations (suborganizations). Properties follow the [Organization](https://schema.org/Organization) specification.",
	value = "Organization"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Organization")
public class Organization implements Serializable {

	public static Organization toDTO(String json) {
		return ObjectMapperUtil.readValue(Organization.class, json);
	}

	public static Organization unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Organization.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The list of accounts associated with this organization."
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

	@GraphQLField(
		description = "The list of accounts associated with this organization."
	)
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

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Organization[] getChildOrganizations() {
		if (_childOrganizationsSupplier != null) {
			childOrganizations = _childOrganizationsSupplier.get();

			_childOrganizationsSupplier = null;
		}

		return childOrganizations;
	}

	public void setChildOrganizations(Organization[] childOrganizations) {
		this.childOrganizations = childOrganizations;

		_childOrganizationsSupplier = null;
	}

	@JsonIgnore
	public void setChildOrganizations(
		UnsafeSupplier<Organization[], Exception>
			childOrganizationsUnsafeSupplier) {

		_childOrganizationsSupplier = () -> {
			try {
				return childOrganizationsUnsafeSupplier.get();
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
	protected Organization[] childOrganizations;

	@JsonIgnore
	private Supplier<Organization[]> _childOrganizationsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The text of a comment associated with the organization."
	)
	public String getComment() {
		if (_commentSupplier != null) {
			comment = _commentSupplier.get();

			_commentSupplier = null;
		}

		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;

		_commentSupplier = null;
	}

	@JsonIgnore
	public void setComment(
		UnsafeSupplier<String, Exception> commentUnsafeSupplier) {

		_commentSupplier = () -> {
			try {
				return commentUnsafeSupplier.get();
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
		description = "The text of a comment associated with the organization."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String comment;

	@JsonIgnore
	private Supplier<String> _commentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user who created the organization."
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

	@GraphQLField(description = "The user who created the organization.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public CustomField[] getCustomFields() {
		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(CustomField[] customFields) {
		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier<CustomField[], Exception> customFieldsUnsafeSupplier) {

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
	protected CustomField[] customFields;

	@JsonIgnore
	private Supplier<CustomField[]> _customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's creation date."
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

	@GraphQLField(description = "The organization's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The most recent time any of the organization's fields changed."
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
		description = "The most recent time any of the organization's fields changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The optional external key of this organization."
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
		description = "The optional external key of this organization."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's ID."
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

	@GraphQLField(description = "The organization's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A relative URL to the organization's image."
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

	@GraphQLField(description = "A relative URL to the organization's image.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String image;

	@JsonIgnore
	private Supplier<String> _imageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's image external reference code."
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
		description = "The organization's image external reference code."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String imageExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _imageExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's image id."
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

	@GraphQLField(description = "The organization's image id.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long imageId;

	@JsonIgnore
	private Supplier<Long> _imageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of keywords describing the organization."
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

	@GraphQLField(
		description = "A list of keywords describing the organization."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] keywords;

	@JsonIgnore
	private Supplier<String[]> _keywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's postal information (country and region)."
	)
	@Valid
	public Location getLocation() {
		if (_locationSupplier != null) {
			location = _locationSupplier.get();

			_locationSupplier = null;
		}

		return location;
	}

	public void setLocation(Location location) {
		this.location = location;

		_locationSupplier = null;
	}

	@JsonIgnore
	public void setLocation(
		UnsafeSupplier<Location, Exception> locationUnsafeSupplier) {

		_locationSupplier = () -> {
			try {
				return locationUnsafeSupplier.get();
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
		description = "The organization's postal information (country and region)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Location location;

	@JsonIgnore
	private Supplier<Location> _locationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's name."
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

	@GraphQLField(description = "The organization's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of this organization's associated accounts."
	)
	public Integer getNumberOfAccounts() {
		if (_numberOfAccountsSupplier != null) {
			numberOfAccounts = _numberOfAccountsSupplier.get();

			_numberOfAccountsSupplier = null;
		}

		return numberOfAccounts;
	}

	public void setNumberOfAccounts(Integer numberOfAccounts) {
		this.numberOfAccounts = numberOfAccounts;

		_numberOfAccountsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfAccounts(
		UnsafeSupplier<Integer, Exception> numberOfAccountsUnsafeSupplier) {

		_numberOfAccountsSupplier = () -> {
			try {
				return numberOfAccountsUnsafeSupplier.get();
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
		description = "The number of this organization's associated accounts."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfAccounts;

	@JsonIgnore
	private Supplier<Integer> _numberOfAccountsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of this organization's child organizations."
	)
	public Integer getNumberOfOrganizations() {
		if (_numberOfOrganizationsSupplier != null) {
			numberOfOrganizations = _numberOfOrganizationsSupplier.get();

			_numberOfOrganizationsSupplier = null;
		}

		return numberOfOrganizations;
	}

	public void setNumberOfOrganizations(Integer numberOfOrganizations) {
		this.numberOfOrganizations = numberOfOrganizations;

		_numberOfOrganizationsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfOrganizations(
		UnsafeSupplier<Integer, Exception>
			numberOfOrganizationsUnsafeSupplier) {

		_numberOfOrganizationsSupplier = () -> {
			try {
				return numberOfOrganizationsUnsafeSupplier.get();
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
		description = "The number of this organization's child organizations."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfOrganizations;

	@JsonIgnore
	private Supplier<Integer> _numberOfOrganizationsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of this organization's associated users."
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
		description = "The number of this organization's associated users."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfUsers;

	@JsonIgnore
	private Supplier<Integer> _numberOfUsersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Account[] getOrganizationAccounts() {
		if (_organizationAccountsSupplier != null) {
			organizationAccounts = _organizationAccountsSupplier.get();

			_organizationAccountsSupplier = null;
		}

		return organizationAccounts;
	}

	public void setOrganizationAccounts(Account[] organizationAccounts) {
		this.organizationAccounts = organizationAccounts;

		_organizationAccountsSupplier = null;
	}

	@JsonIgnore
	public void setOrganizationAccounts(
		UnsafeSupplier<Account[], Exception>
			organizationAccountsUnsafeSupplier) {

		_organizationAccountsSupplier = () -> {
			try {
				return organizationAccountsUnsafeSupplier.get();
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
	protected Account[] organizationAccounts;

	@JsonIgnore
	private Supplier<Account[]> _organizationAccountsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's contact information, which includes email addresses, postal addresses, phone numbers, and web URLs. This is modeled internally as a `Contact`."
	)
	@Valid
	public OrganizationContactInformation getOrganizationContactInformation() {
		if (_organizationContactInformationSupplier != null) {
			organizationContactInformation =
				_organizationContactInformationSupplier.get();

			_organizationContactInformationSupplier = null;
		}

		return organizationContactInformation;
	}

	public void setOrganizationContactInformation(
		OrganizationContactInformation organizationContactInformation) {

		this.organizationContactInformation = organizationContactInformation;

		_organizationContactInformationSupplier = null;
	}

	@JsonIgnore
	public void setOrganizationContactInformation(
		UnsafeSupplier<OrganizationContactInformation, Exception>
			organizationContactInformationUnsafeSupplier) {

		_organizationContactInformationSupplier = () -> {
			try {
				return organizationContactInformationUnsafeSupplier.get();
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
		description = "The organization's contact information, which includes email addresses, postal addresses, phone numbers, and web URLs. This is modeled internally as a `Contact`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected OrganizationContactInformation organizationContactInformation;

	@JsonIgnore
	private Supplier<OrganizationContactInformation>
		_organizationContactInformationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's parent organization."
	)
	@Valid
	public Organization getParentOrganization() {
		if (_parentOrganizationSupplier != null) {
			parentOrganization = _parentOrganizationSupplier.get();

			_parentOrganizationSupplier = null;
		}

		return parentOrganization;
	}

	public void setParentOrganization(Organization parentOrganization) {
		this.parentOrganization = parentOrganization;

		_parentOrganizationSupplier = null;
	}

	@JsonIgnore
	public void setParentOrganization(
		UnsafeSupplier<Organization, Exception>
			parentOrganizationUnsafeSupplier) {

		_parentOrganizationSupplier = () -> {
			try {
				return parentOrganizationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The organization's parent organization.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Organization parentOrganization;

	@JsonIgnore
	private Supplier<Organization> _parentOrganizationSupplier;

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
		description = "The list of roles associated with this organization."
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

	@GraphQLField(
		description = "The list of roles associated with this organization."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected RoleBrief[] roleBriefs;

	@JsonIgnore
	private Supplier<RoleBrief[]> _roleBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of services the organization provides. This follows the [`Service`](https://www.schema.org/Service) specification."
	)
	@Valid
	public Service[] getServices() {
		if (_servicesSupplier != null) {
			services = _servicesSupplier.get();

			_servicesSupplier = null;
		}

		return services;
	}

	public void setServices(Service[] services) {
		this.services = services;

		_servicesSupplier = null;
	}

	@JsonIgnore
	public void setServices(
		UnsafeSupplier<Service[], Exception> servicesUnsafeSupplier) {

		_servicesSupplier = () -> {
			try {
				return servicesUnsafeSupplier.get();
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
		description = "A list of services the organization provides. This follows the [`Service`](https://www.schema.org/Service) specification."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Service[] services;

	@JsonIgnore
	private Supplier<Service[]> _servicesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The categories associated with this organization."
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

	@GraphQLField(
		description = "The categories associated with this organization."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected TaxonomyCategoryBrief[] taxonomyCategoryBriefs;

	@JsonIgnore
	private Supplier<TaxonomyCategoryBrief[]> _taxonomyCategoryBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The tree path of the organization."
	)
	public String getTreePath() {
		if (_treePathSupplier != null) {
			treePath = _treePathSupplier.get();

			_treePathSupplier = null;
		}

		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;

		_treePathSupplier = null;
	}

	@JsonIgnore
	public void setTreePath(
		UnsafeSupplier<String, Exception> treePathUnsafeSupplier) {

		_treePathSupplier = () -> {
			try {
				return treePathUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The tree path of the organization.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String treePath;

	@JsonIgnore
	private Supplier<String> _treePathSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The list of users associated with this organization."
	)
	@Valid
	public UserAccountBrief[] getUserAccountBriefs() {
		if (_userAccountBriefsSupplier != null) {
			userAccountBriefs = _userAccountBriefsSupplier.get();

			_userAccountBriefsSupplier = null;
		}

		return userAccountBriefs;
	}

	public void setUserAccountBriefs(UserAccountBrief[] userAccountBriefs) {
		this.userAccountBriefs = userAccountBriefs;

		_userAccountBriefsSupplier = null;
	}

	@JsonIgnore
	public void setUserAccountBriefs(
		UnsafeSupplier<UserAccountBrief[], Exception>
			userAccountBriefsUnsafeSupplier) {

		_userAccountBriefsSupplier = () -> {
			try {
				return userAccountBriefsUnsafeSupplier.get();
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
		description = "The list of users associated with this organization."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected UserAccountBrief[] userAccountBriefs;

	@JsonIgnore
	private Supplier<UserAccountBrief[]> _userAccountBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public UserAccount[] getUserAccounts() {
		if (_userAccountsSupplier != null) {
			userAccounts = _userAccountsSupplier.get();

			_userAccountsSupplier = null;
		}

		return userAccounts;
	}

	public void setUserAccounts(UserAccount[] userAccounts) {
		this.userAccounts = userAccounts;

		_userAccountsSupplier = null;
	}

	@JsonIgnore
	public void setUserAccounts(
		UnsafeSupplier<UserAccount[], Exception> userAccountsUnsafeSupplier) {

		_userAccountsSupplier = () -> {
			try {
				return userAccountsUnsafeSupplier.get();
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
	protected UserAccount[] userAccounts;

	@JsonIgnore
	private Supplier<UserAccount[]> _userAccountsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Organization)) {
			return false;
		}

		Organization organization = (Organization)object;

		return Objects.equals(toString(), organization.toString());
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

		Organization[] childOrganizations = getChildOrganizations();

		if (childOrganizations != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"childOrganizations\": ");

			sb.append("[");

			for (int i = 0; i < childOrganizations.length; i++) {
				sb.append(String.valueOf(childOrganizations[i]));

				if ((i + 1) < childOrganizations.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String comment = getComment();

		if (comment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");

			sb.append(_escape(comment));

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

		CustomField[] customFields = getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(String.valueOf(customFields[i]));

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

		Location location = getLocation();

		if (location != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"location\": ");

			sb.append(String.valueOf(location));
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

		Integer numberOfAccounts = getNumberOfAccounts();

		if (numberOfAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfAccounts\": ");

			sb.append(numberOfAccounts);
		}

		Integer numberOfOrganizations = getNumberOfOrganizations();

		if (numberOfOrganizations != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfOrganizations\": ");

			sb.append(numberOfOrganizations);
		}

		Integer numberOfUsers = getNumberOfUsers();

		if (numberOfUsers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUsers\": ");

			sb.append(numberOfUsers);
		}

		Account[] organizationAccounts = getOrganizationAccounts();

		if (organizationAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationAccounts\": ");

			sb.append("[");

			for (int i = 0; i < organizationAccounts.length; i++) {
				sb.append(String.valueOf(organizationAccounts[i]));

				if ((i + 1) < organizationAccounts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		OrganizationContactInformation organizationContactInformation =
			getOrganizationContactInformation();

		if (organizationContactInformation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationContactInformation\": ");

			sb.append(String.valueOf(organizationContactInformation));
		}

		Organization parentOrganization = getParentOrganization();

		if (parentOrganization != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentOrganization\": ");

			sb.append(String.valueOf(parentOrganization));
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

		Service[] services = getServices();

		if (services != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"services\": ");

			sb.append("[");

			for (int i = 0; i < services.length; i++) {
				sb.append(String.valueOf(services[i]));

				if ((i + 1) < services.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		String treePath = getTreePath();

		if (treePath != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"treePath\": ");

			sb.append("\"");

			sb.append(_escape(treePath));

			sb.append("\"");
		}

		UserAccountBrief[] userAccountBriefs = getUserAccountBriefs();

		if (userAccountBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccountBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userAccountBriefs.length; i++) {
				sb.append(String.valueOf(userAccountBriefs[i]));

				if ((i + 1) < userAccountBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		UserAccount[] userAccounts = getUserAccounts();

		if (userAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccounts\": ");

			sb.append("[");

			for (int i = 0; i < userAccounts.length; i++) {
				sb.append(String.valueOf(userAccounts[i]));

				if ((i + 1) < userAccounts.length) {
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
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.Organization",
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