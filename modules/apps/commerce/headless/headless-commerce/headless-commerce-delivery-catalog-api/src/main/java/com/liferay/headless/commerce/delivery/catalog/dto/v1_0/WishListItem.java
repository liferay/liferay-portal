/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.dto.v1_0;

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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Single entry in a wish list referencing a product or SKU together with snapshot display fields the storefront uses to render the saved item.",
	value = "WishListItem"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Single entry in a wish list referencing a product or SKU together with snapshot display fields the storefront uses to render the saved item."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WishListItem")
public class WishListItem implements Serializable {

	public static WishListItem toDTO(String json) {
		return ObjectMapperUtil.readValue(WishListItem.class, json);
	}

	public static WishListItem unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(WishListItem.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Resolved final price of the referenced SKU at the time the response is built, formatted in the channel currency.",
		example = "$8.99"
	)
	public String getFinalPrice() {
		if (_finalPriceSupplier != null) {
			finalPrice = _finalPriceSupplier.get();

			_finalPriceSupplier = null;
		}

		return finalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;

		_finalPriceSupplier = null;
	}

	@JsonIgnore
	public void setFinalPrice(
		UnsafeSupplier<String, Exception> finalPriceUnsafeSupplier) {

		_finalPriceSupplier = () -> {
			try {
				return finalPriceUnsafeSupplier.get();
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
		description = "Resolved final price of the referenced SKU at the time the response is built, formatted in the channel currency."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String finalPrice;

	@JsonIgnore
	private Supplier<String> _finalPriceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Resolved friendly URL of the referenced product in the active channel.",
		example = "/hand-saw"
	)
	public String getFriendlyURL() {
		if (_friendlyURLSupplier != null) {
			friendlyURL = _friendlyURLSupplier.get();

			_friendlyURLSupplier = null;
		}

		return friendlyURL;
	}

	public void setFriendlyURL(String friendlyURL) {
		this.friendlyURL = friendlyURL;

		_friendlyURLSupplier = null;
	}

	@JsonIgnore
	public void setFriendlyURL(
		UnsafeSupplier<String, Exception> friendlyURLUnsafeSupplier) {

		_friendlyURLSupplier = () -> {
			try {
				return friendlyURLUnsafeSupplier.get();
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
		description = "Resolved friendly URL of the referenced product in the active channel."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String friendlyURL;

	@JsonIgnore
	private Supplier<String> _friendlyURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Resolved URL of the referenced product's thumbnail image.",
		example = "https://example.com/thumbnail.png"
	)
	public String getIcon() {
		if (_iconSupplier != null) {
			icon = _iconSupplier.get();

			_iconSupplier = null;
		}

		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;

		_iconSupplier = null;
	}

	@JsonIgnore
	public void setIcon(UnsafeSupplier<String, Exception> iconUnsafeSupplier) {
		_iconSupplier = () -> {
			try {
				return iconUnsafeSupplier.get();
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
		description = "Resolved URL of the referenced product's thumbnail image."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String icon;

	@JsonIgnore
	private Supplier<String> _iconSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the wish-list item (FK identifier). Read-only.",
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
		description = "Reference to the wish-list item (FK identifier). Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the referenced product head (FK identifier).",
		example = "30129"
	)
	public Long getProductId() {
		if (_productIdSupplier != null) {
			productId = _productIdSupplier.get();

			_productIdSupplier = null;
		}

		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;

		_productIdSupplier = null;
	}

	@JsonIgnore
	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		_productIdSupplier = () -> {
			try {
				return productIdUnsafeSupplier.get();
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
		description = "Reference to the referenced product head (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized display name of the referenced product, resolved for the request locale.",
		example = "Hand Saw"
	)
	public String getProductName() {
		if (_productNameSupplier != null) {
			productName = _productNameSupplier.get();

			_productNameSupplier = null;
		}

		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;

		_productNameSupplier = null;
	}

	@JsonIgnore
	public void setProductName(
		UnsafeSupplier<String, Exception> productNameUnsafeSupplier) {

		_productNameSupplier = () -> {
			try {
				return productNameUnsafeSupplier.get();
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
		description = "Localized display name of the referenced product, resolved for the request locale."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String productName;

	@JsonIgnore
	private Supplier<String> _productNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the referenced SKU (FK identifier).",
		example = "30130"
	)
	public Long getSkuId() {
		if (_skuIdSupplier != null) {
			skuId = _skuIdSupplier.get();

			_skuIdSupplier = null;
		}

		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;

		_skuIdSupplier = null;
	}

	@JsonIgnore
	public void setSkuId(UnsafeSupplier<Long, Exception> skuIdUnsafeSupplier) {
		_skuIdSupplier = () -> {
			try {
				return skuIdUnsafeSupplier.get();
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
		description = "Reference to the referenced SKU (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long skuId;

	@JsonIgnore
	private Supplier<Long> _skuIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WishListItem)) {
			return false;
		}

		WishListItem wishListItem = (WishListItem)object;

		return Objects.equals(toString(), wishListItem.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String finalPrice = getFinalPrice();

		if (finalPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"finalPrice\": ");

			sb.append("\"");

			sb.append(_escape(finalPrice));

			sb.append("\"");
		}

		String friendlyURL = getFriendlyURL();

		if (friendlyURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyURL\": ");

			sb.append("\"");

			sb.append(_escape(friendlyURL));

			sb.append("\"");
		}

		String icon = getIcon();

		if (icon != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"icon\": ");

			sb.append("\"");

			sb.append(_escape(icon));

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

		Long productId = getProductId();

		if (productId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productId);
		}

		String productName = getProductName();

		if (productName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productName\": ");

			sb.append("\"");

			sb.append(_escape(productName));

			sb.append("\"");
		}

		Long skuId = getSkuId();

		if (skuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(skuId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishListItem",
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
// LIFERAY-REST-BUILDER-HASH:58952376