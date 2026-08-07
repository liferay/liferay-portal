/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

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
import jakarta.validation.constraints.DecimalMin;

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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Container that groups product configuration entries under a single inventory, shipping, and tax template; each catalog has one master configuration list used as the catalog-level default, and additional sub-templates can inherit from a parent and be qualified by account, account group, channel, or order type.",
	value = "ProductConfigurationList"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Container that groups product configuration entries under a single inventory, shipping, and tax template; each catalog has one master configuration list used as the catalog-level default, and additional sub-templates can inherit from a parent and be qualified by account, account group, channel, or order type."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductConfigurationList")
public class ProductConfigurationList implements Serializable {

	public static ProductConfigurationList toDTO(String json) {
		return ObjectMapperUtil.readValue(ProductConfigurationList.class, json);
	}

	public static ProductConfigurationList unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductConfigurationList.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of available operations for the current user keyed by action name; each entry carries the URL template and HTTP method; read-only."
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
		description = "Map of available operations for the current user keyed by action name; each entry carries the URL template and HTTP method; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the catalog whose site owns this configuration list; on create, it is resolved first and falls back to `catalogId`; write-only.",
		example = "AB-34098-789-N"
	)
	public String getCatalogExternalReferenceCode() {
		if (_catalogExternalReferenceCodeSupplier != null) {
			catalogExternalReferenceCode =
				_catalogExternalReferenceCodeSupplier.get();

			_catalogExternalReferenceCodeSupplier = null;
		}

		return catalogExternalReferenceCode;
	}

	public void setCatalogExternalReferenceCode(
		String catalogExternalReferenceCode) {

		this.catalogExternalReferenceCode = catalogExternalReferenceCode;

		_catalogExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setCatalogExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			catalogExternalReferenceCodeUnsafeSupplier) {

		_catalogExternalReferenceCodeSupplier = () -> {
			try {
				return catalogExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the catalog whose site owns this configuration list; on create, it is resolved first and falls back to `catalogId`; write-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String catalogExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _catalogExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the catalog whose site owns this configuration list; used on create to derive the owning site when `catalogExternalReferenceCode` does not resolve; write-only.",
		example = "30130"
	)
	public Long getCatalogId() {
		if (_catalogIdSupplier != null) {
			catalogId = _catalogIdSupplier.get();

			_catalogIdSupplier = null;
		}

		return catalogId;
	}

	public void setCatalogId(Long catalogId) {
		this.catalogId = catalogId;

		_catalogIdSupplier = null;
	}

	@JsonIgnore
	public void setCatalogId(
		UnsafeSupplier<Long, Exception> catalogIdUnsafeSupplier) {

		_catalogIdSupplier = () -> {
			try {
				return catalogIdUnsafeSupplier.get();
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
		description = "Identifier of the catalog whose site owns this configuration list; used on create to derive the owning site when `catalogExternalReferenceCode` does not resolve; write-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long catalogId;

	@JsonIgnore
	private Supplier<Long> _catalogIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Timestamp when the configuration list was created; set by the service on persist; read-only.",
		example = "2017-07-21"
	)
	public Date getCreateDate() {
		if (_createDateSupplier != null) {
			createDate = _createDateSupplier.get();

			_createDateSupplier = null;
		}

		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;

		_createDateSupplier = null;
	}

	@JsonIgnore
	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		_createDateSupplier = () -> {
			try {
				return createDateUnsafeSupplier.get();
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
		description = "Timestamp when the configuration list was created; set by the service on persist; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Custom field values defined for the configuration list; serialized for the request locale."
	)
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

	@GraphQLField(
		description = "Custom field values defined for the configuration list; serialized for the request locale."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date and time at which the configuration list becomes effective; interpreted in the requesting user's time zone; rejected when invalid.",
		example = "2017-07-21"
	)
	public Date getDisplayDate() {
		if (_displayDateSupplier != null) {
			displayDate = _displayDateSupplier.get();

			_displayDateSupplier = null;
		}

		return displayDate;
	}

	public void setDisplayDate(Date displayDate) {
		this.displayDate = displayDate;

		_displayDateSupplier = null;
	}

	@JsonIgnore
	public void setDisplayDate(
		UnsafeSupplier<Date, Exception> displayDateUnsafeSupplier) {

		_displayDateSupplier = () -> {
			try {
				return displayDateUnsafeSupplier.get();
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
		description = "Date and time at which the configuration list becomes effective; interpreted in the requesting user's time zone; rejected when invalid."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date displayDate;

	@JsonIgnore
	private Supplier<Date> _displayDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date and time after which the configuration list is no longer effective; ignored when `neverExpire` is true; rejected when invalid.",
		example = "2017-08-21"
	)
	public Date getExpirationDate() {
		if (_expirationDateSupplier != null) {
			expirationDate = _expirationDateSupplier.get();

			_expirationDateSupplier = null;
		}

		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;

		_expirationDateSupplier = null;
	}

	@JsonIgnore
	public void setExpirationDate(
		UnsafeSupplier<Date, Exception> expirationDateUnsafeSupplier) {

		_expirationDateSupplier = () -> {
			try {
				return expirationDateUnsafeSupplier.get();
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
		description = "Date and time after which the configuration list is no longer effective; ignored when `neverExpire` is true; rejected when invalid."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date expirationDate;

	@JsonIgnore
	private Supplier<Date> _expirationDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per product configuration list within the company.",
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
		description = "Idempotency key for create and update; must be unique per product configuration list within the company."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the product configuration list; read-only.",
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
		description = "Identifier of the product configuration list; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether this is the catalog-level default configuration list; a catalog has exactly one master list (a duplicate is rejected), the master cannot be deleted, and it seeds the default product configuration entry on creation.",
		example = "true"
	)
	public Boolean getMaster() {
		if (_masterSupplier != null) {
			master = _masterSupplier.get();

			_masterSupplier = null;
		}

		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;

		_masterSupplier = null;
	}

	@JsonIgnore
	public void setMaster(
		UnsafeSupplier<Boolean, Exception> masterUnsafeSupplier) {

		_masterSupplier = () -> {
			try {
				return masterUnsafeSupplier.get();
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
		description = "Whether this is the catalog-level default configuration list; a catalog has exactly one master list (a duplicate is rejected), the master cannot be deleted, and it seeds the default product configuration entry on creation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean master;

	@JsonIgnore
	private Supplier<Boolean> _masterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Administrative display name shown in the Commerce control panel; sortable.",
		example = "Hand Saw"
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
		description = "Administrative display name shown in the Commerce control panel; sortable."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, the expiration date is ignored and the configuration list never expires; defaults to true on create or update when the field is omitted.",
		example = "true"
	)
	public Boolean getNeverExpire() {
		if (_neverExpireSupplier != null) {
			neverExpire = _neverExpireSupplier.get();

			_neverExpireSupplier = null;
		}

		return neverExpire;
	}

	public void setNeverExpire(Boolean neverExpire) {
		this.neverExpire = neverExpire;

		_neverExpireSupplier = null;
	}

	@JsonIgnore
	public void setNeverExpire(
		UnsafeSupplier<Boolean, Exception> neverExpireUnsafeSupplier) {

		_neverExpireSupplier = () -> {
			try {
				return neverExpireUnsafeSupplier.get();
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
		description = "When true, the expiration date is ignored and the configuration list never expires; defaults to true on create or update when the field is omitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean neverExpire;

	@JsonIgnore
	private Supplier<Boolean> _neverExpireSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the parent configuration list; it takes precedence over `parentProductConfigurationListId`. An unresolved code fails the request, except during an import, where it creates an empty configuration list to be completed later. The parent must belong to the same site as the child.",
		example = "AB-34098-789-N"
	)
	public String getParentProductConfigurationListExternalReferenceCode() {
		if (_parentProductConfigurationListExternalReferenceCodeSupplier !=
				null) {

			parentProductConfigurationListExternalReferenceCode =
				_parentProductConfigurationListExternalReferenceCodeSupplier.
					get();

			_parentProductConfigurationListExternalReferenceCodeSupplier = null;
		}

		return parentProductConfigurationListExternalReferenceCode;
	}

	public void setParentProductConfigurationListExternalReferenceCode(
		String parentProductConfigurationListExternalReferenceCode) {

		this.parentProductConfigurationListExternalReferenceCode =
			parentProductConfigurationListExternalReferenceCode;

		_parentProductConfigurationListExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setParentProductConfigurationListExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentProductConfigurationListExternalReferenceCodeUnsafeSupplier) {

		_parentProductConfigurationListExternalReferenceCodeSupplier = () -> {
			try {
				return parentProductConfigurationListExternalReferenceCodeUnsafeSupplier.
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
		description = "External reference code of the parent configuration list; it takes precedence over `parentProductConfigurationListId`. An unresolved code fails the request, except during an import, where it creates an empty configuration list to be completed later. The parent must belong to the same site as the child."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String parentProductConfigurationListExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_parentProductConfigurationListExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the parent configuration list; enables inheritance, with child lists copying parent entry snapshots on creation; the parent must belong to the same site as the child.",
		example = "30130"
	)
	public Long getParentProductConfigurationListId() {
		if (_parentProductConfigurationListIdSupplier != null) {
			parentProductConfigurationListId =
				_parentProductConfigurationListIdSupplier.get();

			_parentProductConfigurationListIdSupplier = null;
		}

		return parentProductConfigurationListId;
	}

	public void setParentProductConfigurationListId(
		Long parentProductConfigurationListId) {

		this.parentProductConfigurationListId =
			parentProductConfigurationListId;

		_parentProductConfigurationListIdSupplier = null;
	}

	@JsonIgnore
	public void setParentProductConfigurationListId(
		UnsafeSupplier<Long, Exception>
			parentProductConfigurationListIdUnsafeSupplier) {

		_parentProductConfigurationListIdSupplier = () -> {
			try {
				return parentProductConfigurationListIdUnsafeSupplier.get();
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
		description = "Identifier of the parent configuration list; enables inheritance, with child lists copying parent entry snapshots on creation; the parent must belong to the same site as the child."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long parentProductConfigurationListId;

	@JsonIgnore
	private Supplier<Long> _parentProductConfigurationListIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display weight used to order qualified lists ascending before applying account, account group, channel, and order-type tie-breakers.",
		example = "1.2"
	)
	public Double getPriority() {
		if (_prioritySupplier != null) {
			priority = _prioritySupplier.get();

			_prioritySupplier = null;
		}

		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;

		_prioritySupplier = null;
	}

	@JsonIgnore
	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		_prioritySupplier = () -> {
			try {
				return priorityUnsafeSupplier.get();
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
		description = "Display weight used to order qualified lists ascending before applying account, account group, channel, and order-type tie-breakers."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Nested product configuration entries associated with this list; supplying entries on create or update triggers nested calls to create or update each entry."
	)
	@Valid
	public ProductConfiguration[] getProductConfigurations() {
		if (_productConfigurationsSupplier != null) {
			productConfigurations = _productConfigurationsSupplier.get();

			_productConfigurationsSupplier = null;
		}

		return productConfigurations;
	}

	public void setProductConfigurations(
		ProductConfiguration[] productConfigurations) {

		this.productConfigurations = productConfigurations;

		_productConfigurationsSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurations(
		UnsafeSupplier<ProductConfiguration[], Exception>
			productConfigurationsUnsafeSupplier) {

		_productConfigurationsSupplier = () -> {
			try {
				return productConfigurationsUnsafeSupplier.get();
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
		description = "Nested product configuration entries associated with this list; supplying entries on create or update triggers nested calls to create or update each entry."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ProductConfiguration[] productConfigurations;

	@JsonIgnore
	private Supplier<ProductConfiguration[]> _productConfigurationsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductConfigurationList)) {
			return false;
		}

		ProductConfigurationList productConfigurationList =
			(ProductConfigurationList)object;

		return Objects.equals(toString(), productConfigurationList.toString());
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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		String catalogExternalReferenceCode = getCatalogExternalReferenceCode();

		if (catalogExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(catalogExternalReferenceCode));

			sb.append("\"");
		}

		Long catalogId = getCatalogId();

		if (catalogId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogId\": ");

			sb.append(catalogId);
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(createDate));

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

		Date displayDate = getDisplayDate();

		if (displayDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(displayDate));

			sb.append("\"");
		}

		Date expirationDate = getExpirationDate();

		if (expirationDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(expirationDate));

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

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Boolean master = getMaster();

		if (master != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"master\": ");

			sb.append(master);
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

		Boolean neverExpire = getNeverExpire();

		if (neverExpire != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(neverExpire);
		}

		String parentProductConfigurationListExternalReferenceCode =
			getParentProductConfigurationListExternalReferenceCode();

		if (parentProductConfigurationListExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"parentProductConfigurationListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(parentProductConfigurationListExternalReferenceCode));

			sb.append("\"");
		}

		Long parentProductConfigurationListId =
			getParentProductConfigurationListId();

		if (parentProductConfigurationListId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentProductConfigurationListId\": ");

			sb.append(parentProductConfigurationListId);
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		ProductConfiguration[] productConfigurations =
			getProductConfigurations();

		if (productConfigurations != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurations\": ");

			sb.append("[");

			for (int i = 0; i < productConfigurations.length; i++) {
				sb.append(String.valueOf(productConfigurations[i]));

				if ((i + 1) < productConfigurations.length) {
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
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationList",
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
// LIFERAY-REST-BUILDER-HASH:725379502