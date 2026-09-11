/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

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
	description = "A shop-by-diagram setting that turns a product into an interactive image with clickable pins. Created and updated by the admin catalog write surface, which first uploads the diagram image as a diagram-type attachment and then persists the diagram setting.",
	value = "Diagram"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A shop-by-diagram setting that turns a product into an interactive image with clickable pins. Created and updated by the admin catalog write surface, which first uploads the diagram image as a diagram-type attachment and then persists the diagram setting."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Diagram")
public class Diagram implements Serializable {

	public static Diagram toDTO(String json) {
		return ObjectMapperUtil.readValue(Diagram.class, json);
	}

	public static Diagram unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Diagram.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public AttachmentBase64 getAttachmentBase64() {
		if (_attachmentBase64Supplier != null) {
			attachmentBase64 = _attachmentBase64Supplier.get();

			_attachmentBase64Supplier = null;
		}

		return attachmentBase64;
	}

	public void setAttachmentBase64(AttachmentBase64 attachmentBase64) {
		this.attachmentBase64 = attachmentBase64;

		_attachmentBase64Supplier = null;
	}

	@JsonIgnore
	public void setAttachmentBase64(
		UnsafeSupplier<AttachmentBase64, Exception>
			attachmentBase64UnsafeSupplier) {

		_attachmentBase64Supplier = () -> {
			try {
				return attachmentBase64UnsafeSupplier.get();
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
	protected AttachmentBase64 attachmentBase64;

	@JsonIgnore
	private Supplier<AttachmentBase64> _attachmentBase64Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Hex or CSS color string used to render the diagram background or overlay. No server-side validation; maximum length is 75 characters.",
		example = "black"
	)
	public String getColor() {
		if (_colorSupplier != null) {
			color = _colorSupplier.get();

			_colorSupplier = null;
		}

		return color;
	}

	public void setColor(String color) {
		this.color = color;

		_colorSupplier = null;
	}

	@JsonIgnore
	public void setColor(
		UnsafeSupplier<String, Exception> colorUnsafeSupplier) {

		_colorSupplier = () -> {
			try {
				return colorUnsafeSupplier.get();
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
		description = "Hex or CSS color string used to render the diagram background or overlay. No server-side validation; maximum length is 75 characters."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String color;

	@JsonIgnore
	private Supplier<String> _colorSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the diagram setting. Assigned by the server.",
		example = "31130"
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
		description = "Identifier of the diagram setting. Assigned by the server."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the attachment that holds the diagram image. Populated from the uploaded `attachmentBase64`; can also be set explicitly to point at an existing diagram-type attachment.",
		example = "33132"
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

	@GraphQLField(
		description = "Identifier of the attachment that holds the diagram image. Populated from the uploaded `attachmentBase64`; can also be set explicitly to point at an existing diagram-type attachment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long imageId;

	@JsonIgnore
	private Supplier<Long> _imageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Public download URL for the diagram image. Read-only; returns null when the underlying attachment has no document-library file.",
		example = "Name 1"
	)
	public String getImageURL() {
		if (_imageURLSupplier != null) {
			imageURL = _imageURLSupplier.get();

			_imageURLSupplier = null;
		}

		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;

		_imageURLSupplier = null;
	}

	@JsonIgnore
	public void setImageURL(
		UnsafeSupplier<String, Exception> imageURLUnsafeSupplier) {

		_imageURLSupplier = () -> {
			try {
				return imageURLUnsafeSupplier.get();
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
		description = "Public download URL for the diagram image. Read-only; returns null when the underlying attachment has no document-library file."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String imageURL;

	@JsonIgnore
	private Supplier<String> _imageURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the parent product the diagram belongs to. Read-only; populated from the linked product.",
		example = "exampleERC"
	)
	public String getProductExternalReferenceCode() {
		if (_productExternalReferenceCodeSupplier != null) {
			productExternalReferenceCode =
				_productExternalReferenceCodeSupplier.get();

			_productExternalReferenceCodeSupplier = null;
		}

		return productExternalReferenceCode;
	}

	public void setProductExternalReferenceCode(
		String productExternalReferenceCode) {

		this.productExternalReferenceCode = productExternalReferenceCode;

		_productExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setProductExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productExternalReferenceCodeUnsafeSupplier) {

		_productExternalReferenceCodeSupplier = () -> {
			try {
				return productExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the parent product the diagram belongs to. Read-only; populated from the linked product."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String productExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _productExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the parent product. Read-only; populated from the linked product.",
		example = "33131"
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
		description = "Identifier of the parent product. Read-only; populated from the linked product."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Pixel radius used by the storefront to compute the click area around each pin.",
		example = "33.54"
	)
	public Double getRadius() {
		if (_radiusSupplier != null) {
			radius = _radiusSupplier.get();

			_radiusSupplier = null;
		}

		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;

		_radiusSupplier = null;
	}

	@JsonIgnore
	public void setRadius(
		UnsafeSupplier<Double, Exception> radiusUnsafeSupplier) {

		_radiusSupplier = () -> {
			try {
				return radiusUnsafeSupplier.get();
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
		description = "Pixel radius used by the storefront to compute the click area around each pin."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double radius;

	@JsonIgnore
	private Supplier<Double> _radiusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Diagram type key resolved against the registered diagram types. Defaults to `diagram.type.default` on add when omitted; patch preserves the previous value when omitted.",
		example = "default"
	)
	public String getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(String type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
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
		description = "Diagram type key resolved against the registered diagram types. Defaults to `diagram.type.default` on add when omitted; patch preserves the previous value when omitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String type;

	@JsonIgnore
	private Supplier<String> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Diagram)) {
			return false;
		}

		Diagram diagram = (Diagram)object;

		return Objects.equals(toString(), diagram.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		AttachmentBase64 attachmentBase64 = getAttachmentBase64();

		if (attachmentBase64 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachmentBase64\": ");

			sb.append(String.valueOf(attachmentBase64));
		}

		String color = getColor();

		if (color != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"color\": ");

			sb.append("\"");

			sb.append(_escape(color));

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

		Long imageId = getImageId();

		if (imageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageId\": ");

			sb.append(imageId);
		}

		String imageURL = getImageURL();

		if (imageURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageURL\": ");

			sb.append("\"");

			sb.append(_escape(imageURL));

			sb.append("\"");
		}

		String productExternalReferenceCode = getProductExternalReferenceCode();

		if (productExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(productExternalReferenceCode));

			sb.append("\"");
		}

		Long productId = getProductId();

		if (productId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productId);
		}

		Double radius = getRadius();

		if (radius != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"radius\": ");

			sb.append(radius);
		}

		String type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(type));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.Diagram",
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
// LIFERAY-REST-BUILDER-HASH:764433814