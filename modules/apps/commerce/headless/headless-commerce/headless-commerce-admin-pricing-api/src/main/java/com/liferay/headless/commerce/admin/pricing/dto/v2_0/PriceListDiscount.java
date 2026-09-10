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

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
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
	description = "Discount linkage attached to a price list. Ties one discount to the price list so its rules participate in price resolution against that list. Backed by price list discount link.",
	value = "PriceListDiscount"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Discount linkage attached to a price list. Ties one discount to the price list so its rules participate in price resolution against that list. Backed by price list discount link.",
	requiredProperties = {"discountId", "priceListId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PriceListDiscount")
public class PriceListDiscount implements Serializable {

	public static PriceListDiscount toDTO(String json) {
		return ObjectMapperUtil.readValue(PriceListDiscount.class, json);
	}

	public static PriceListDiscount unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PriceListDiscount.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the bound discount; alternative to `discountId` for lookup.",
		example = "DAB-34098-789-N"
	)
	public String getDiscountExternalReferenceCode() {
		if (_discountExternalReferenceCodeSupplier != null) {
			discountExternalReferenceCode =
				_discountExternalReferenceCodeSupplier.get();

			_discountExternalReferenceCodeSupplier = null;
		}

		return discountExternalReferenceCode;
	}

	public void setDiscountExternalReferenceCode(
		String discountExternalReferenceCode) {

		this.discountExternalReferenceCode = discountExternalReferenceCode;

		_discountExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setDiscountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			discountExternalReferenceCodeUnsafeSupplier) {

		_discountExternalReferenceCodeSupplier = () -> {
			try {
				return discountExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the bound discount; alternative to `discountId` for lookup."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String discountExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _discountExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the discount entity (FK identifier).",
		example = "30324"
	)
	public Long getDiscountId() {
		if (_discountIdSupplier != null) {
			discountId = _discountIdSupplier.get();

			_discountIdSupplier = null;
		}

		return discountId;
	}

	public void setDiscountId(Long discountId) {
		this.discountId = discountId;

		_discountIdSupplier = null;
	}

	@JsonIgnore
	public void setDiscountId(
		UnsafeSupplier<Long, Exception> discountIdUnsafeSupplier) {

		_discountIdSupplier = () -> {
			try {
				return discountIdUnsafeSupplier.get();
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
		description = "Reference to the discount entity (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Long discountId;

	@JsonIgnore
	private Supplier<Long> _discountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of the linked discount, mirrored from discount.title. Read-only.",
		example = "20% Off"
	)
	public String getDiscountName() {
		if (_discountNameSupplier != null) {
			discountName = _discountNameSupplier.get();

			_discountNameSupplier = null;
		}

		return discountName;
	}

	public void setDiscountName(String discountName) {
		this.discountName = discountName;

		_discountNameSupplier = null;
	}

	@JsonIgnore
	public void setDiscountName(
		UnsafeSupplier<String, Exception> discountNameUnsafeSupplier) {

		_discountNameSupplier = () -> {
			try {
				return discountNameUnsafeSupplier.get();
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
		description = "Display name of the linked discount, mirrored from discount.title. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String discountName;

	@JsonIgnore
	private Supplier<String> _discountNameSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Ordering integer that orders this pricelistdiscount relative to its peers. Lower numbers resolve first.",
		example = "1"
	)
	public Integer getOrder() {
		if (_orderSupplier != null) {
			order = _orderSupplier.get();

			_orderSupplier = null;
		}

		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;

		_orderSupplier = null;
	}

	@JsonIgnore
	public void setOrder(
		UnsafeSupplier<Integer, Exception> orderUnsafeSupplier) {

		_orderSupplier = () -> {
			try {
				return orderUnsafeSupplier.get();
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
		description = "Ordering integer that orders this pricelistdiscount relative to its peers. Lower numbers resolve first."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer order;

	@JsonIgnore
	private Supplier<Integer> _orderSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the priceListDiscount entity (FK identifier).",
		example = "30643"
	)
	public Long getPriceListDiscountId() {
		if (_priceListDiscountIdSupplier != null) {
			priceListDiscountId = _priceListDiscountIdSupplier.get();

			_priceListDiscountIdSupplier = null;
		}

		return priceListDiscountId;
	}

	public void setPriceListDiscountId(Long priceListDiscountId) {
		this.priceListDiscountId = priceListDiscountId;

		_priceListDiscountIdSupplier = null;
	}

	@JsonIgnore
	public void setPriceListDiscountId(
		UnsafeSupplier<Long, Exception> priceListDiscountIdUnsafeSupplier) {

		_priceListDiscountIdSupplier = () -> {
			try {
				return priceListDiscountIdUnsafeSupplier.get();
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
		description = "Reference to the priceListDiscount entity (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long priceListDiscountId;

	@JsonIgnore
	private Supplier<Long> _priceListDiscountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the bound priceList; alternative to `priceListId` for lookup.",
		example = "PAB-34098-789-N"
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
		example = "30130"
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
	@NotNull
	protected Long priceListId;

	@JsonIgnore
	private Supplier<Long> _priceListIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PriceListDiscount)) {
			return false;
		}

		PriceListDiscount priceListDiscount = (PriceListDiscount)object;

		return Objects.equals(toString(), priceListDiscount.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String discountExternalReferenceCode =
			getDiscountExternalReferenceCode();

		if (discountExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(discountExternalReferenceCode));

			sb.append("\"");
		}

		Long discountId = getDiscountId();

		if (discountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountId\": ");

			sb.append(discountId);
		}

		String discountName = getDiscountName();

		if (discountName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountName\": ");

			sb.append("\"");

			sb.append(_escape(discountName));

			sb.append("\"");
		}

		Integer order = getOrder();

		if (order != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"order\": ");

			sb.append(order);
		}

		Long priceListDiscountId = getPriceListDiscountId();

		if (priceListDiscountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListDiscountId\": ");

			sb.append(priceListDiscountId);
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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListDiscount",
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
// LIFERAY-REST-BUILDER-HASH:-645637581