/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.dto.v2_0;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.math.BigDecimal;

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
	description = "Add-on price calculation layered on a price list. Carries a target (categories, products, or product-groups), a modifierAmount or modifierPercentage, a modifierType (absolute or percentage), a limitationType, and a priority. Backed by price modifier.",
	value = "PriceModifier"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Add-on price calculation layered on a price list. Carries a target (categories, products, or product-groups), a modifierAmount or modifierPercentage, a modifierType (absolute or percentage), a limitationType, and a priority. Backed by price modifier.",
	requiredProperties = {"modifierAmount", "modifierType", "target", "title"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PriceModifier")
public class PriceModifier implements Serializable {

	public static PriceModifier toDTO(String json) {
		return ObjectMapperUtil.readValue(PriceModifier.class, json);
	}

	public static PriceModifier unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PriceModifier.class, json);
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
		description = "Whether the pricemodifier is currently enabled. When false the runtime skips this record during evaluation.",
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
		description = "Whether the pricemodifier is currently enabled. When false the runtime skips this record during evaluation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

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
		description = "Internal numeric identifier of the pricemodifier. Server-assigned and stable.",
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
		description = "Internal numeric identifier of the pricemodifier. Server-assigned and stable."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Absolute add-on amount the modifier contributes. Mutually exclusive with modifierPercentage; meaningful when modifierType is absolute.",
		example = "25"
	)
	@Valid
	public BigDecimal getModifierAmount() {
		if (_modifierAmountSupplier != null) {
			modifierAmount = _modifierAmountSupplier.get();

			_modifierAmountSupplier = null;
		}

		return modifierAmount;
	}

	public void setModifierAmount(BigDecimal modifierAmount) {
		this.modifierAmount = modifierAmount;

		_modifierAmountSupplier = null;
	}

	@JsonIgnore
	public void setModifierAmount(
		UnsafeSupplier<BigDecimal, Exception> modifierAmountUnsafeSupplier) {

		_modifierAmountSupplier = () -> {
			try {
				return modifierAmountUnsafeSupplier.get();
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
		description = "Absolute add-on amount the modifier contributes. Mutually exclusive with modifierPercentage; meaningful when modifierType is absolute."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected BigDecimal modifierAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _modifierAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Calculation kind for the modifier. One of `absolute` (modifierAmount used) or `percentage` (modifierPercentage used).",
		example = "percentage"
	)
	public String getModifierType() {
		if (_modifierTypeSupplier != null) {
			modifierType = _modifierTypeSupplier.get();

			_modifierTypeSupplier = null;
		}

		return modifierType;
	}

	public void setModifierType(String modifierType) {
		this.modifierType = modifierType;

		_modifierTypeSupplier = null;
	}

	@JsonIgnore
	public void setModifierType(
		UnsafeSupplier<String, Exception> modifierTypeUnsafeSupplier) {

		_modifierTypeSupplier = () -> {
			try {
				return modifierTypeUnsafeSupplier.get();
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
		description = "Calculation kind for the modifier. One of `absolute` (modifierAmount used) or `percentage` (modifierPercentage used)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String modifierType;

	@JsonIgnore
	private Supplier<String> _modifierTypeSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the bound priceList; alternative to `priceListId` for lookup.",
		example = "PLAB-34098-789-N"
	)
	public String getPriceListExternalReferenceCode() {
		if (_priceListExternalReferenceCodeSupplier != null) {
			priceListExternalReferenceCode =
				_priceListExternalReferenceCodeSupplier.get();

			_priceListExternalReferenceCodeSupplier = null;
		}

		return priceListExternalReferenceCode;
	}

	public void setPriceListExternalReferenceCode(
		String priceListExternalReferenceCode) {

		this.priceListExternalReferenceCode = priceListExternalReferenceCode;

		_priceListExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setPriceListExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			priceListExternalReferenceCodeUnsafeSupplier) {

		_priceListExternalReferenceCodeSupplier = () -> {
			try {
				return priceListExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the bound priceList; alternative to `priceListId` for lookup."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String priceListExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _priceListExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the priceList entity (FK identifier).",
		example = "20078"
	)
	public Long getPriceListId() {
		if (_priceListIdSupplier != null) {
			priceListId = _priceListIdSupplier.get();

			_priceListIdSupplier = null;
		}

		return priceListId;
	}

	public void setPriceListId(Long priceListId) {
		this.priceListId = priceListId;

		_priceListIdSupplier = null;
	}

	@JsonIgnore
	public void setPriceListId(
		UnsafeSupplier<Long, Exception> priceListIdUnsafeSupplier) {

		_priceListIdSupplier = () -> {
			try {
				return priceListIdUnsafeSupplier.get();
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
		description = "Reference to the priceList entity (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long priceListId;

	@JsonIgnore
	private Supplier<Long> _priceListIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceModifierCategories entries cascaded on upsert."
	)
	@Valid
	public PriceModifierCategory[] getPriceModifierCategories() {
		if (_priceModifierCategoriesSupplier != null) {
			priceModifierCategories = _priceModifierCategoriesSupplier.get();

			_priceModifierCategoriesSupplier = null;
		}

		return priceModifierCategories;
	}

	public void setPriceModifierCategories(
		PriceModifierCategory[] priceModifierCategories) {

		this.priceModifierCategories = priceModifierCategories;

		_priceModifierCategoriesSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierCategories(
		UnsafeSupplier<PriceModifierCategory[], Exception>
			priceModifierCategoriesUnsafeSupplier) {

		_priceModifierCategoriesSupplier = () -> {
			try {
				return priceModifierCategoriesUnsafeSupplier.get();
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
		description = "Collection of priceModifierCategories entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceModifierCategory[] priceModifierCategories;

	@JsonIgnore
	private Supplier<PriceModifierCategory[]> _priceModifierCategoriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceModifierProductGroups entries cascaded on upsert."
	)
	@Valid
	public PriceModifierProductGroup[] getPriceModifierProductGroups() {
		if (_priceModifierProductGroupsSupplier != null) {
			priceModifierProductGroups =
				_priceModifierProductGroupsSupplier.get();

			_priceModifierProductGroupsSupplier = null;
		}

		return priceModifierProductGroups;
	}

	public void setPriceModifierProductGroups(
		PriceModifierProductGroup[] priceModifierProductGroups) {

		this.priceModifierProductGroups = priceModifierProductGroups;

		_priceModifierProductGroupsSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierProductGroups(
		UnsafeSupplier<PriceModifierProductGroup[], Exception>
			priceModifierProductGroupsUnsafeSupplier) {

		_priceModifierProductGroupsSupplier = () -> {
			try {
				return priceModifierProductGroupsUnsafeSupplier.get();
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
		description = "Collection of priceModifierProductGroups entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceModifierProductGroup[] priceModifierProductGroups;

	@JsonIgnore
	private Supplier<PriceModifierProductGroup[]>
		_priceModifierProductGroupsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Collection of priceModifierProducts entries cascaded on upsert."
	)
	@Valid
	public PriceModifierProduct[] getPriceModifierProducts() {
		if (_priceModifierProductsSupplier != null) {
			priceModifierProducts = _priceModifierProductsSupplier.get();

			_priceModifierProductsSupplier = null;
		}

		return priceModifierProducts;
	}

	public void setPriceModifierProducts(
		PriceModifierProduct[] priceModifierProducts) {

		this.priceModifierProducts = priceModifierProducts;

		_priceModifierProductsSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierProducts(
		UnsafeSupplier<PriceModifierProduct[], Exception>
			priceModifierProductsUnsafeSupplier) {

		_priceModifierProductsSupplier = () -> {
			try {
				return priceModifierProductsUnsafeSupplier.get();
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
		description = "Collection of priceModifierProducts entries cascaded on upsert."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PriceModifierProduct[] priceModifierProducts;

	@JsonIgnore
	private Supplier<PriceModifierProduct[]> _priceModifierProductsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Priority value that orders this pricemodifier relative to its peers. Lower numbers resolve first.",
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
		description = "Priority value that orders this pricemodifier relative to its peers. Lower numbers resolve first."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Which slice of the parent the record applies to. Refer to the parent schema's documentation for the allowed values.",
		example = "product"
	)
	public String getTarget() {
		if (_targetSupplier != null) {
			target = _targetSupplier.get();

			_targetSupplier = null;
		}

		return target;
	}

	public void setTarget(String target) {
		this.target = target;

		_targetSupplier = null;
	}

	@JsonIgnore
	public void setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		_targetSupplier = () -> {
			try {
				return targetUnsafeSupplier.get();
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
		description = "Which slice of the parent the record applies to. Refer to the parent schema's documentation for the allowed values."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String target;

	@JsonIgnore
	private Supplier<String> _targetSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display title of the pricemodifier.", example = "20% Off"
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

	@GraphQLField(description = "Display title of the pricemodifier.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PriceModifier)) {
			return false;
		}

		PriceModifier priceModifier = (PriceModifier)object;

		return Objects.equals(toString(), priceModifier.toString());
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

		BigDecimal modifierAmount = getModifierAmount();

		if (modifierAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifierAmount\": ");

			sb.append(modifierAmount);
		}

		String modifierType = getModifierType();

		if (modifierType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifierType\": ");

			sb.append("\"");

			sb.append(_escape(modifierType));

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

		String priceListExternalReferenceCode =
			getPriceListExternalReferenceCode();

		if (priceListExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceListExternalReferenceCode));

			sb.append("\"");
		}

		Long priceListId = getPriceListId();

		if (priceListId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListId\": ");

			sb.append(priceListId);
		}

		PriceModifierCategory[] priceModifierCategories =
			getPriceModifierCategories();

		if (priceModifierCategories != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierCategories\": ");

			sb.append("[");

			for (int i = 0; i < priceModifierCategories.length; i++) {
				sb.append(String.valueOf(priceModifierCategories[i]));

				if ((i + 1) < priceModifierCategories.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PriceModifierProductGroup[] priceModifierProductGroups =
			getPriceModifierProductGroups();

		if (priceModifierProductGroups != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierProductGroups\": ");

			sb.append("[");

			for (int i = 0; i < priceModifierProductGroups.length; i++) {
				sb.append(String.valueOf(priceModifierProductGroups[i]));

				if ((i + 1) < priceModifierProductGroups.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PriceModifierProduct[] priceModifierProducts =
			getPriceModifierProducts();

		if (priceModifierProducts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierProducts\": ");

			sb.append("[");

			for (int i = 0; i < priceModifierProducts.length; i++) {
				sb.append(String.valueOf(priceModifierProducts[i]));

				if ((i + 1) < priceModifierProducts.length) {
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

		String target = getTarget();

		if (target != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"target\": ");

			sb.append("\"");

			sb.append(_escape(target));

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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifier",
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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:1539485668