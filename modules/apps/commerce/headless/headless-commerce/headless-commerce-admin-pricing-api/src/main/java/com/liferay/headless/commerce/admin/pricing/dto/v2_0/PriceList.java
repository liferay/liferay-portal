/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.dto.v2_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Versioned price catalog that prices SKUs in a specific currency for a catalog. Carries a priority, a display/expiration window, the parent catalog, and the cascading rel collections (account-group, account, channel, discount, order-type, modifier, entry). Workflow-aware -- the runtime only honours price lists whose status is approved.",
	value = "PriceList"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Versioned price catalog that prices SKUs in a specific currency for a catalog. Carries a priority, a display/expiration window, the parent catalog, and the cascading rel collections (account-group, account, channel, discount, order-type, modifier, entry). Workflow-aware -- the runtime only honours price lists whose status is approved.",
	requiredProperties = {"catalogId", "currencyCode", "name", "type"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PriceList")
public class PriceList implements Serializable {

	public static PriceList toDTO(String json) {
		return ObjectMapperUtil.readValue(PriceList.class, json);
	}

	public static PriceList unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PriceList.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
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
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the pricelist is currently enabled. When false the runtime skips this record during evaluation.",
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
		description = "Whether the pricelist is currently enabled. When false the runtime skips this record during evaluation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Login of the user who last persisted the record. Read-only.",
		example = "admin"
	)
	public String getAuthor() {
		if (_authorSupplier != null) {
			author = _authorSupplier.get();

			_authorSupplier = null;
		}

		return author;
	}

	public void setAuthor(String author) {
		this.author = author;

		_authorSupplier = null;
	}

	@JsonIgnore
	public void setAuthor(
		UnsafeSupplier<String, Exception> authorUnsafeSupplier) {

		_authorSupplier = () -> {
			try {
				return authorUnsafeSupplier.get();
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
		description = "Login of the user who last persisted the record. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String author;

	@JsonIgnore
	private Supplier<String> _authorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, the price list is the catalog's base list and serves as the fallback when no other price list resolves.",
		example = "true"
	)
	public Boolean getCatalogBasePriceList() {
		if (_catalogBasePriceListSupplier != null) {
			catalogBasePriceList = _catalogBasePriceListSupplier.get();

			_catalogBasePriceListSupplier = null;
		}

		return catalogBasePriceList;
	}

	public void setCatalogBasePriceList(Boolean catalogBasePriceList) {
		this.catalogBasePriceList = catalogBasePriceList;

		_catalogBasePriceListSupplier = null;
	}

	@JsonIgnore
	public void setCatalogBasePriceList(
		UnsafeSupplier<Boolean, Exception> catalogBasePriceListUnsafeSupplier) {

		_catalogBasePriceListSupplier = () -> {
			try {
				return catalogBasePriceListUnsafeSupplier.get();
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
		description = "When true, the price list is the catalog's base list and serves as the fallback when no other price list resolves."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean catalogBasePriceList;

	@JsonIgnore
	private Supplier<Boolean> _catalogBasePriceListSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the bound catalog; alternative to `catalogId` for lookup.",
		example = "AAB-34098-789-N"
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
		description = "External reference code of the bound catalog; alternative to `catalogId` for lookup."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String catalogExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _catalogExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the catalog entity (FK identifier).",
		example = "23130"
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
		description = "Reference to the catalog entity (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Long catalogId;

	@JsonIgnore
	private Supplier<Long> _catalogIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of the parent catalog, mirrored from catalog.name. Read-only.",
		example = "catalog"
	)
	public String getCatalogName() {
		if (_catalogNameSupplier != null) {
			catalogName = _catalogNameSupplier.get();

			_catalogNameSupplier = null;
		}

		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;

		_catalogNameSupplier = null;
	}

	@JsonIgnore
	public void setCatalogName(
		UnsafeSupplier<String, Exception> catalogNameUnsafeSupplier) {

		_catalogNameSupplier = () -> {
			try {
				return catalogNameUnsafeSupplier.get();
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
		description = "Display name of the parent catalog, mirrored from catalog.name. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String catalogName;

	@JsonIgnore
	private Supplier<String> _catalogNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date value. ISO 8601.", example = "2017-07-21"
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

	@GraphQLField(description = "Date value. ISO 8601.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ISO 4217 currency code in which monetary values on this record are expressed.",
		example = "EUR"
	)
	public String getCurrencyCode() {
		if (_currencyCodeSupplier != null) {
			currencyCode = _currencyCodeSupplier.get();

			_currencyCodeSupplier = null;
		}

		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;

		_currencyCodeSupplier = null;
	}

	@JsonIgnore
	public void setCurrencyCode(
		UnsafeSupplier<String, Exception> currencyCodeUnsafeSupplier) {

		_currencyCodeSupplier = () -> {
			try {
				return currencyCodeUnsafeSupplier.get();
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
		description = "ISO 4217 currency code in which monetary values on this record are expressed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String currencyCode;

	@JsonIgnore
	private Supplier<String> _currencyCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the bound currency; alternative to `currencyId` for lookup.",
		example = "AAB-34098-789-N"
	)
	public String getCurrencyExternalReferenceCode() {
		if (_currencyExternalReferenceCodeSupplier != null) {
			currencyExternalReferenceCode =
				_currencyExternalReferenceCodeSupplier.get();

			_currencyExternalReferenceCodeSupplier = null;
		}

		return currencyExternalReferenceCode;
	}

	public void setCurrencyExternalReferenceCode(
		String currencyExternalReferenceCode) {

		this.currencyExternalReferenceCode = currencyExternalReferenceCode;

		_currencyExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setCurrencyExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			currencyExternalReferenceCodeUnsafeSupplier) {

		_currencyExternalReferenceCodeSupplier = () -> {
			try {
				return currencyExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the bound currency; alternative to `currencyId` for lookup."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String currencyExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _currencyExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the currency that the price list expresses monetary values in.",
		example = "30130"
	)
	public Long getCurrencyId() {
		if (_currencyIdSupplier != null) {
			currencyId = _currencyIdSupplier.get();

			_currencyIdSupplier = null;
		}

		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;

		_currencyIdSupplier = null;
	}

	@JsonIgnore
	public void setCurrencyId(
		UnsafeSupplier<Long, Exception> currencyIdUnsafeSupplier) {

		_currencyIdSupplier = () -> {
			try {
				return currencyIdUnsafeSupplier.get();
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
		description = "Reference to the currency that the price list expresses monetary values in."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long currencyId;

	@JsonIgnore
	private Supplier<Long> _currencyIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Free-form Expando custom fields attached to the underlying entity. Keys are Expando attribute names; values follow each attribute's declared column type.",
		example = "{priority=high, segment=B2B}"
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
		description = "Free-form Expando custom fields attached to the underlying entity. Keys are Expando attribute names; values follow each attribute's declared column type."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, ?> customFields;

	@JsonIgnore
	private Supplier<Map<String, ?>> _customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date and time when this record becomes effective. ISO 8601.",
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
		description = "Date and time when this record becomes effective. ISO 8601."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date displayDate;

	@JsonIgnore
	private Supplier<Date> _displayDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date and time when this record expires. ISO 8601; the runtime stops honoring it past this date.",
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
		description = "Date and time when this record expires. ISO 8601; the runtime stops honoring it past this date."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date expirationDate;

	@JsonIgnore
	private Supplier<Date> _expirationDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique within the company.",
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
		description = "Idempotency key for create and update; must be unique within the company."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal numeric identifier of the pricelist. Server-assigned and stable.",
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
		description = "Internal numeric identifier of the pricelist. Server-assigned and stable."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of the pricelist.",
		example = "Laptops, Beverages"
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

	@GraphQLField(description = "Display name of the pricelist.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, prices in this price list are expressed net of tax; when false, prices are gross (tax-inclusive).",
		example = "true"
	)
	public Boolean getNetPrice() {
		if (_netPriceSupplier != null) {
			netPrice = _netPriceSupplier.get();

			_netPriceSupplier = null;
		}

		return netPrice;
	}

	public void setNetPrice(Boolean netPrice) {
		this.netPrice = netPrice;

		_netPriceSupplier = null;
	}

	@JsonIgnore
	public void setNetPrice(
		UnsafeSupplier<Boolean, Exception> netPriceUnsafeSupplier) {

		_netPriceSupplier = () -> {
			try {
				return netPriceUnsafeSupplier.get();
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
		description = "When true, prices in this price list are expressed net of tax; when false, prices are gross (tax-inclusive)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean netPrice;

	@JsonIgnore
	private Supplier<Boolean> _netPriceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, the record has no expiration date and expirationDate is ignored.",
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
		description = "When true, the record has no expiration date and expirationDate is ignored."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean neverExpire;

	@JsonIgnore
	private Supplier<Boolean> _neverExpireSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the parent price list; non-null when this list is a child of another price list.",
		example = "30130"
	)
	public Long getParentPriceListId() {
		if (_parentPriceListIdSupplier != null) {
			parentPriceListId = _parentPriceListIdSupplier.get();

			_parentPriceListIdSupplier = null;
		}

		return parentPriceListId;
	}

	public void setParentPriceListId(Long parentPriceListId) {
		this.parentPriceListId = parentPriceListId;

		_parentPriceListIdSupplier = null;
	}

	@JsonIgnore
	public void setParentPriceListId(
		UnsafeSupplier<Long, Exception> parentPriceListIdUnsafeSupplier) {

		_parentPriceListIdSupplier = () -> {
			try {
				return parentPriceListIdUnsafeSupplier.get();
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
		description = "Reference to the parent price list; non-null when this list is a child of another price list."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long parentPriceListId;

	@JsonIgnore
	private Supplier<Long> _parentPriceListIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceEntries entries cascaded on upsert."
	)
	@Valid
	public PriceEntry[] getPriceEntries() {
		if (_priceEntriesSupplier != null) {
			priceEntries = _priceEntriesSupplier.get();

			_priceEntriesSupplier = null;
		}

		return priceEntries;
	}

	public void setPriceEntries(PriceEntry[] priceEntries) {
		this.priceEntries = priceEntries;

		_priceEntriesSupplier = null;
	}

	@JsonIgnore
	public void setPriceEntries(
		UnsafeSupplier<PriceEntry[], Exception> priceEntriesUnsafeSupplier) {

		_priceEntriesSupplier = () -> {
			try {
				return priceEntriesUnsafeSupplier.get();
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
		description = "Collection of priceEntries entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceEntry[] priceEntries;

	@JsonIgnore
	private Supplier<PriceEntry[]> _priceEntriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceListAccountGroups entries cascaded on upsert."
	)
	@Valid
	public PriceListAccountGroup[] getPriceListAccountGroups() {
		if (_priceListAccountGroupsSupplier != null) {
			priceListAccountGroups = _priceListAccountGroupsSupplier.get();

			_priceListAccountGroupsSupplier = null;
		}

		return priceListAccountGroups;
	}

	public void setPriceListAccountGroups(
		PriceListAccountGroup[] priceListAccountGroups) {

		this.priceListAccountGroups = priceListAccountGroups;

		_priceListAccountGroupsSupplier = null;
	}

	@JsonIgnore
	public void setPriceListAccountGroups(
		UnsafeSupplier<PriceListAccountGroup[], Exception>
			priceListAccountGroupsUnsafeSupplier) {

		_priceListAccountGroupsSupplier = () -> {
			try {
				return priceListAccountGroupsUnsafeSupplier.get();
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
		description = "Collection of priceListAccountGroups entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceListAccountGroup[] priceListAccountGroups;

	@JsonIgnore
	private Supplier<PriceListAccountGroup[]> _priceListAccountGroupsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceListAccounts entries cascaded on upsert."
	)
	@Valid
	public PriceListAccount[] getPriceListAccounts() {
		if (_priceListAccountsSupplier != null) {
			priceListAccounts = _priceListAccountsSupplier.get();

			_priceListAccountsSupplier = null;
		}

		return priceListAccounts;
	}

	public void setPriceListAccounts(PriceListAccount[] priceListAccounts) {
		this.priceListAccounts = priceListAccounts;

		_priceListAccountsSupplier = null;
	}

	@JsonIgnore
	public void setPriceListAccounts(
		UnsafeSupplier<PriceListAccount[], Exception>
			priceListAccountsUnsafeSupplier) {

		_priceListAccountsSupplier = () -> {
			try {
				return priceListAccountsUnsafeSupplier.get();
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
		description = "Collection of priceListAccounts entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceListAccount[] priceListAccounts;

	@JsonIgnore
	private Supplier<PriceListAccount[]> _priceListAccountsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceListChannels entries cascaded on upsert."
	)
	@Valid
	public PriceListChannel[] getPriceListChannels() {
		if (_priceListChannelsSupplier != null) {
			priceListChannels = _priceListChannelsSupplier.get();

			_priceListChannelsSupplier = null;
		}

		return priceListChannels;
	}

	public void setPriceListChannels(PriceListChannel[] priceListChannels) {
		this.priceListChannels = priceListChannels;

		_priceListChannelsSupplier = null;
	}

	@JsonIgnore
	public void setPriceListChannels(
		UnsafeSupplier<PriceListChannel[], Exception>
			priceListChannelsUnsafeSupplier) {

		_priceListChannelsSupplier = () -> {
			try {
				return priceListChannelsUnsafeSupplier.get();
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
		description = "Collection of priceListChannels entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceListChannel[] priceListChannels;

	@JsonIgnore
	private Supplier<PriceListChannel[]> _priceListChannelsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceListDiscounts entries cascaded on upsert."
	)
	@Valid
	public PriceListDiscount[] getPriceListDiscounts() {
		if (_priceListDiscountsSupplier != null) {
			priceListDiscounts = _priceListDiscountsSupplier.get();

			_priceListDiscountsSupplier = null;
		}

		return priceListDiscounts;
	}

	public void setPriceListDiscounts(PriceListDiscount[] priceListDiscounts) {
		this.priceListDiscounts = priceListDiscounts;

		_priceListDiscountsSupplier = null;
	}

	@JsonIgnore
	public void setPriceListDiscounts(
		UnsafeSupplier<PriceListDiscount[], Exception>
			priceListDiscountsUnsafeSupplier) {

		_priceListDiscountsSupplier = () -> {
			try {
				return priceListDiscountsUnsafeSupplier.get();
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
		description = "Collection of priceListDiscounts entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceListDiscount[] priceListDiscounts;

	@JsonIgnore
	private Supplier<PriceListDiscount[]> _priceListDiscountsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceListOrderTypes entries cascaded on upsert."
	)
	@Valid
	public PriceListOrderType[] getPriceListOrderTypes() {
		if (_priceListOrderTypesSupplier != null) {
			priceListOrderTypes = _priceListOrderTypesSupplier.get();

			_priceListOrderTypesSupplier = null;
		}

		return priceListOrderTypes;
	}

	public void setPriceListOrderTypes(
		PriceListOrderType[] priceListOrderTypes) {

		this.priceListOrderTypes = priceListOrderTypes;

		_priceListOrderTypesSupplier = null;
	}

	@JsonIgnore
	public void setPriceListOrderTypes(
		UnsafeSupplier<PriceListOrderType[], Exception>
			priceListOrderTypesUnsafeSupplier) {

		_priceListOrderTypesSupplier = () -> {
			try {
				return priceListOrderTypesUnsafeSupplier.get();
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
		description = "Collection of priceListOrderTypes entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceListOrderType[] priceListOrderTypes;

	@JsonIgnore
	private Supplier<PriceListOrderType[]> _priceListOrderTypesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceModifiers entries cascaded on upsert."
	)
	@Valid
	public PriceModifier[] getPriceModifiers() {
		if (_priceModifiersSupplier != null) {
			priceModifiers = _priceModifiersSupplier.get();

			_priceModifiersSupplier = null;
		}

		return priceModifiers;
	}

	public void setPriceModifiers(PriceModifier[] priceModifiers) {
		this.priceModifiers = priceModifiers;

		_priceModifiersSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifiers(
		UnsafeSupplier<PriceModifier[], Exception>
			priceModifiersUnsafeSupplier) {

		_priceModifiersSupplier = () -> {
			try {
				return priceModifiersUnsafeSupplier.get();
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
		description = "Collection of priceModifiers entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceModifier[] priceModifiers;

	@JsonIgnore
	private Supplier<PriceModifier[]> _priceModifiersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Priority value that orders this pricelist relative to its peers. Lower numbers resolve first.",
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
		description = "Priority value that orders this pricelist relative to its peers. Lower numbers resolve first."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Type discriminator for the record. Refer to the parent schema's documentation for the allowed values.",
		example = "price-list, promotion, contract"
	)
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

	@GraphQLField(
		description = "Type discriminator for the record. Refer to the parent schema's documentation for the allowed values."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Status getWorkflowStatusInfo() {
		if (_workflowStatusInfoSupplier != null) {
			workflowStatusInfo = _workflowStatusInfoSupplier.get();

			_workflowStatusInfoSupplier = null;
		}

		return workflowStatusInfo;
	}

	public void setWorkflowStatusInfo(Status workflowStatusInfo) {
		this.workflowStatusInfo = workflowStatusInfo;

		_workflowStatusInfoSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowStatusInfo(
		UnsafeSupplier<Status, Exception> workflowStatusInfoUnsafeSupplier) {

		_workflowStatusInfoSupplier = () -> {
			try {
				return workflowStatusInfoUnsafeSupplier.get();
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
	protected Status workflowStatusInfo;

	@JsonIgnore
	private Supplier<Status> _workflowStatusInfoSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PriceList)) {
			return false;
		}

		PriceList priceList = (PriceList)object;

		return Objects.equals(toString(), priceList.toString());
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

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		String author = getAuthor();

		if (author != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(author));

			sb.append("\"");
		}

		Boolean catalogBasePriceList = getCatalogBasePriceList();

		if (catalogBasePriceList != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogBasePriceList\": ");

			sb.append(catalogBasePriceList);
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

		String catalogName = getCatalogName();

		if (catalogName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogName\": ");

			sb.append("\"");

			sb.append(_escape(catalogName));

			sb.append("\"");
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

		String currencyCode = getCurrencyCode();

		if (currencyCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyCode\": ");

			sb.append("\"");

			sb.append(_escape(currencyCode));

			sb.append("\"");
		}

		String currencyExternalReferenceCode =
			getCurrencyExternalReferenceCode();

		if (currencyExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(currencyExternalReferenceCode));

			sb.append("\"");
		}

		Long currencyId = getCurrencyId();

		if (currencyId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyId\": ");

			sb.append(currencyId);
		}

		Map<String, ?> customFields = getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(customFields));
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

		Boolean netPrice = getNetPrice();

		if (netPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"netPrice\": ");

			sb.append(netPrice);
		}

		Boolean neverExpire = getNeverExpire();

		if (neverExpire != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(neverExpire);
		}

		Long parentPriceListId = getParentPriceListId();

		if (parentPriceListId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentPriceListId\": ");

			sb.append(parentPriceListId);
		}

		PriceEntry[] priceEntries = getPriceEntries();

		if (priceEntries != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceEntries\": ");

			sb.append("[");

			for (int i = 0; i < priceEntries.length; i++) {
				sb.append(String.valueOf(priceEntries[i]));

				if ((i + 1) < priceEntries.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PriceListAccountGroup[] priceListAccountGroups =
			getPriceListAccountGroups();

		if (priceListAccountGroups != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListAccountGroups\": ");

			sb.append("[");

			for (int i = 0; i < priceListAccountGroups.length; i++) {
				sb.append(String.valueOf(priceListAccountGroups[i]));

				if ((i + 1) < priceListAccountGroups.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PriceListAccount[] priceListAccounts = getPriceListAccounts();

		if (priceListAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListAccounts\": ");

			sb.append("[");

			for (int i = 0; i < priceListAccounts.length; i++) {
				sb.append(String.valueOf(priceListAccounts[i]));

				if ((i + 1) < priceListAccounts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PriceListChannel[] priceListChannels = getPriceListChannels();

		if (priceListChannels != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListChannels\": ");

			sb.append("[");

			for (int i = 0; i < priceListChannels.length; i++) {
				sb.append(String.valueOf(priceListChannels[i]));

				if ((i + 1) < priceListChannels.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PriceListDiscount[] priceListDiscounts = getPriceListDiscounts();

		if (priceListDiscounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListDiscounts\": ");

			sb.append("[");

			for (int i = 0; i < priceListDiscounts.length; i++) {
				sb.append(String.valueOf(priceListDiscounts[i]));

				if ((i + 1) < priceListDiscounts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PriceListOrderType[] priceListOrderTypes = getPriceListOrderTypes();

		if (priceListOrderTypes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListOrderTypes\": ");

			sb.append("[");

			for (int i = 0; i < priceListOrderTypes.length; i++) {
				sb.append(String.valueOf(priceListOrderTypes[i]));

				if ((i + 1) < priceListOrderTypes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PriceModifier[] priceModifiers = getPriceModifiers();

		if (priceModifiers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifiers\": ");

			sb.append("[");

			for (int i = 0; i < priceModifiers.length; i++) {
				sb.append(String.valueOf(priceModifiers[i]));

				if ((i + 1) < priceModifiers.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
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

		Status workflowStatusInfo = getWorkflowStatusInfo();

		if (workflowStatusInfo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowStatusInfo\": ");

			sb.append(String.valueOf(workflowStatusInfo));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceList",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		PRICE_LIST("price-list"), PROMOTION("promotion"), CONTRACT("contract");

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
// LIFERAY-REST-BUILDER-HASH:-1828408617