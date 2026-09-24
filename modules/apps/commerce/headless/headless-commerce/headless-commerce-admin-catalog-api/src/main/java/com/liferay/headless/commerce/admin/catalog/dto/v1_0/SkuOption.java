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
	description = "Selection of a single product option value for a SKU variant; links the SKU to one product option and one of that option's values.",
	value = "SkuOption"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Selection of a single product option value for a SKU variant; links the SKU to one product option and one of that option's values."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SkuOption")
public class SkuOption implements Serializable {

	public static SkuOption toDTO(String json) {
		return ObjectMapperUtil.readValue(SkuOption.class, json);
	}

	public static SkuOption unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SkuOption.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Stable string key of the product option; on read the value is the option's key, while on write the value may also be the option's numeric identifier encoded as a string, which the service resolves back to the key.",
		example = "color"
	)
	public String getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	public void setKey(String key) {
		this.key = key;

		_keySupplier = null;
	}

	@JsonIgnore
	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		_keySupplier = () -> {
			try {
				return keyUnsafeSupplier.get();
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
		description = "Stable string key of the product option; on read the value is the option's key, while on write the value may also be the option's numeric identifier encoded as a string, which the service resolves back to the key."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the product option this selection answers; it takes precedence over `optionId` and over the numeric form of `key`, and it names the product option link rather than the global option template. During an import, when no product option carries the code and `key` names an existing option, an empty product option is created and completed when the product option itself is imported. Outside an import, a code that matches nothing fails the request with a 404 error.",
		example = "AB-34098-789-N"
	)
	public String getOptionExternalReferenceCode() {
		if (_optionExternalReferenceCodeSupplier != null) {
			optionExternalReferenceCode =
				_optionExternalReferenceCodeSupplier.get();

			_optionExternalReferenceCodeSupplier = null;
		}

		return optionExternalReferenceCode;
	}

	public void setOptionExternalReferenceCode(
		String optionExternalReferenceCode) {

		this.optionExternalReferenceCode = optionExternalReferenceCode;

		_optionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOptionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			optionExternalReferenceCodeUnsafeSupplier) {

		_optionExternalReferenceCodeSupplier = () -> {
			try {
				return optionExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the product option this selection answers; it takes precedence over `optionId` and over the numeric form of `key`, and it names the product option link rather than the global option template. During an import, when no product option carries the code and `key` names an existing option, an empty product option is created and completed when the product option itself is imported. Outside an import, a code that matches nothing fails the request with a 404 error."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String optionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _optionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Field type of the product option this selection answers, mirroring `fieldType` on that product option. On write it is consulted only during an import, to give the empty product option a valid field type, and is then required alongside `optionExternalReferenceCode`. It is read from the product option rather than from the option the product option derives from, because the two may differ.",
		example = "select"
	)
	public String getOptionFieldType() {
		if (_optionFieldTypeSupplier != null) {
			optionFieldType = _optionFieldTypeSupplier.get();

			_optionFieldTypeSupplier = null;
		}

		return optionFieldType;
	}

	public void setOptionFieldType(String optionFieldType) {
		this.optionFieldType = optionFieldType;

		_optionFieldTypeSupplier = null;
	}

	@JsonIgnore
	public void setOptionFieldType(
		UnsafeSupplier<String, Exception> optionFieldTypeUnsafeSupplier) {

		_optionFieldTypeSupplier = () -> {
			try {
				return optionFieldTypeUnsafeSupplier.get();
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
		description = "Field type of the product option this selection answers, mirroring `fieldType` on that product option. On write it is consulted only during an import, to give the empty product option a valid field type, and is then required alongside `optionExternalReferenceCode`. It is read from the product option rather than from the option the product option derives from, because the two may differ."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String optionFieldType;

	@JsonIgnore
	private Supplier<String> _optionFieldTypeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal numeric identifier of the product option this selection answers.",
		example = "31130"
	)
	public Long getOptionId() {
		if (_optionIdSupplier != null) {
			optionId = _optionIdSupplier.get();

			_optionIdSupplier = null;
		}

		return optionId;
	}

	public void setOptionId(Long optionId) {
		this.optionId = optionId;

		_optionIdSupplier = null;
	}

	@JsonIgnore
	public void setOptionId(
		UnsafeSupplier<Long, Exception> optionIdUnsafeSupplier) {

		_optionIdSupplier = () -> {
			try {
				return optionIdUnsafeSupplier.get();
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
		description = "Internal numeric identifier of the product option this selection answers."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long optionId;

	@JsonIgnore
	private Supplier<Long> _optionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the product option this selection answers contributes to the SKU, mirroring `skuContributor` on that product option. On write it is consulted only during an import, together with `optionFieldType`, because the two decide which field types the empty product option may carry.",
		example = "true"
	)
	public Boolean getOptionSkuContributor() {
		if (_optionSkuContributorSupplier != null) {
			optionSkuContributor = _optionSkuContributorSupplier.get();

			_optionSkuContributorSupplier = null;
		}

		return optionSkuContributor;
	}

	public void setOptionSkuContributor(Boolean optionSkuContributor) {
		this.optionSkuContributor = optionSkuContributor;

		_optionSkuContributorSupplier = null;
	}

	@JsonIgnore
	public void setOptionSkuContributor(
		UnsafeSupplier<Boolean, Exception> optionSkuContributorUnsafeSupplier) {

		_optionSkuContributorSupplier = () -> {
			try {
				return optionSkuContributorUnsafeSupplier.get();
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
		description = "Whether the product option this selection answers contributes to the SKU, mirroring `skuContributor` on that product option. On write it is consulted only during an import, together with `optionFieldType`, because the two decide which field types the empty product option may carry."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean optionSkuContributor;

	@JsonIgnore
	private Supplier<Boolean> _optionSkuContributorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the chosen product option value; it takes precedence over `optionValueId` and over the numeric form of `value`, and it names the product option value link rather than the global option value. During an import, when no product option value carries the code, an empty product option value is created under the resolved product option and completed when the product option value itself is imported. Outside an import, a code that matches nothing fails the request with a 404 error.",
		example = "AB-34098-789-N"
	)
	public String getOptionValueExternalReferenceCode() {
		if (_optionValueExternalReferenceCodeSupplier != null) {
			optionValueExternalReferenceCode =
				_optionValueExternalReferenceCodeSupplier.get();

			_optionValueExternalReferenceCodeSupplier = null;
		}

		return optionValueExternalReferenceCode;
	}

	public void setOptionValueExternalReferenceCode(
		String optionValueExternalReferenceCode) {

		this.optionValueExternalReferenceCode =
			optionValueExternalReferenceCode;

		_optionValueExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOptionValueExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			optionValueExternalReferenceCodeUnsafeSupplier) {

		_optionValueExternalReferenceCodeSupplier = () -> {
			try {
				return optionValueExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the chosen product option value; it takes precedence over `optionValueId` and over the numeric form of `value`, and it names the product option value link rather than the global option value. During an import, when no product option value carries the code, an empty product option value is created under the resolved product option and completed when the product option value itself is imported. Outside an import, a code that matches nothing fails the request with a 404 error."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String optionValueExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _optionValueExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal numeric identifier of the chosen value within the product option referenced by `optionId`.",
		example = "31130"
	)
	public Long getOptionValueId() {
		if (_optionValueIdSupplier != null) {
			optionValueId = _optionValueIdSupplier.get();

			_optionValueIdSupplier = null;
		}

		return optionValueId;
	}

	public void setOptionValueId(Long optionValueId) {
		this.optionValueId = optionValueId;

		_optionValueIdSupplier = null;
	}

	@JsonIgnore
	public void setOptionValueId(
		UnsafeSupplier<Long, Exception> optionValueIdUnsafeSupplier) {

		_optionValueIdSupplier = () -> {
			try {
				return optionValueIdUnsafeSupplier.get();
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
		description = "Internal numeric identifier of the chosen value within the product option referenced by `optionId`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long optionValueId;

	@JsonIgnore
	private Supplier<Long> _optionValueIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the option this selection derives from, as opposed to `optionExternalReferenceCode`, which names the product option link. On read it mirrors that option's code. On write it is consulted only during an import, and only when neither the product option nor an option named by `key` exists yet. The option is then created as an empty stub so the product option can be created under it, and both are completed when the options themselves are imported.",
		example = "AB-34098-789-N"
	)
	public String getParentOptionExternalReferenceCode() {
		if (_parentOptionExternalReferenceCodeSupplier != null) {
			parentOptionExternalReferenceCode =
				_parentOptionExternalReferenceCodeSupplier.get();

			_parentOptionExternalReferenceCodeSupplier = null;
		}

		return parentOptionExternalReferenceCode;
	}

	public void setParentOptionExternalReferenceCode(
		String parentOptionExternalReferenceCode) {

		this.parentOptionExternalReferenceCode =
			parentOptionExternalReferenceCode;

		_parentOptionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setParentOptionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentOptionExternalReferenceCodeUnsafeSupplier) {

		_parentOptionExternalReferenceCodeSupplier = () -> {
			try {
				return parentOptionExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the option this selection derives from, as opposed to `optionExternalReferenceCode`, which names the product option link. On read it mirrors that option's code. On write it is consulted only during an import, and only when neither the product option nor an option named by `key` exists yet. The option is then created as an empty stub so the product option can be created under it, and both are completed when the options themselves are imported."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String parentOptionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _parentOptionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Field type of the option this selection derives from, mirroring `fieldType` on that option. On write it is consulted only during an import, to give the product option created under an empty option a valid field type, and is then required alongside `parentOptionExternalReferenceCode`.",
		example = "select"
	)
	public String getParentOptionFieldType() {
		if (_parentOptionFieldTypeSupplier != null) {
			parentOptionFieldType = _parentOptionFieldTypeSupplier.get();

			_parentOptionFieldTypeSupplier = null;
		}

		return parentOptionFieldType;
	}

	public void setParentOptionFieldType(String parentOptionFieldType) {
		this.parentOptionFieldType = parentOptionFieldType;

		_parentOptionFieldTypeSupplier = null;
	}

	@JsonIgnore
	public void setParentOptionFieldType(
		UnsafeSupplier<String, Exception> parentOptionFieldTypeUnsafeSupplier) {

		_parentOptionFieldTypeSupplier = () -> {
			try {
				return parentOptionFieldTypeUnsafeSupplier.get();
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
		description = "Field type of the option this selection derives from, mirroring `fieldType` on that option. On write it is consulted only during an import, to give the product option created under an empty option a valid field type, and is then required alongside `parentOptionExternalReferenceCode`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String parentOptionFieldType;

	@JsonIgnore
	private Supplier<String> _parentOptionFieldTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the option this selection derives from contributes to the SKU, mirroring `skuContributor` on that option. On write it is consulted only during an import, together with `parentOptionFieldType`, because the two decide which field types the product option created under an empty option may carry.",
		example = "true"
	)
	public Boolean getParentOptionSkuContributor() {
		if (_parentOptionSkuContributorSupplier != null) {
			parentOptionSkuContributor =
				_parentOptionSkuContributorSupplier.get();

			_parentOptionSkuContributorSupplier = null;
		}

		return parentOptionSkuContributor;
	}

	public void setParentOptionSkuContributor(
		Boolean parentOptionSkuContributor) {

		this.parentOptionSkuContributor = parentOptionSkuContributor;

		_parentOptionSkuContributorSupplier = null;
	}

	@JsonIgnore
	public void setParentOptionSkuContributor(
		UnsafeSupplier<Boolean, Exception>
			parentOptionSkuContributorUnsafeSupplier) {

		_parentOptionSkuContributorSupplier = () -> {
			try {
				return parentOptionSkuContributorUnsafeSupplier.get();
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
		description = "Whether the option this selection derives from contributes to the SKU, mirroring `skuContributor` on that option. On write it is consulted only during an import, together with `parentOptionFieldType`, because the two decide which field types the product option created under an empty option may carry."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean parentOptionSkuContributor;

	@JsonIgnore
	private Supplier<Boolean> _parentOptionSkuContributorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Stable string key of the option value chosen for this option; on read the value is the option value's key. During an import it also gives the empty product option value its key, so that the key is valid for the field type of the product option.",
		example = "red"
	)
	public String getValue() {
		if (_valueSupplier != null) {
			value = _valueSupplier.get();

			_valueSupplier = null;
		}

		return value;
	}

	public void setValue(String value) {
		this.value = value;

		_valueSupplier = null;
	}

	@JsonIgnore
	public void setValue(
		UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		_valueSupplier = () -> {
			try {
				return valueUnsafeSupplier.get();
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
		description = "Stable string key of the option value chosen for this option; on read the value is the option value's key. During an import it also gives the empty product option value its key, so that the key is valid for the field type of the product option."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String value;

	@JsonIgnore
	private Supplier<String> _valueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SkuOption)) {
			return false;
		}

		SkuOption skuOption = (SkuOption)object;

		return Objects.equals(toString(), skuOption.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String key = getKey();

		if (key != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(key));

			sb.append("\"");
		}

		String optionExternalReferenceCode = getOptionExternalReferenceCode();

		if (optionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(optionExternalReferenceCode));

			sb.append("\"");
		}

		String optionFieldType = getOptionFieldType();

		if (optionFieldType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionFieldType\": ");

			sb.append("\"");

			sb.append(_escape(optionFieldType));

			sb.append("\"");
		}

		Long optionId = getOptionId();

		if (optionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionId\": ");

			sb.append(optionId);
		}

		Boolean optionSkuContributor = getOptionSkuContributor();

		if (optionSkuContributor != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionSkuContributor\": ");

			sb.append(optionSkuContributor);
		}

		String optionValueExternalReferenceCode =
			getOptionValueExternalReferenceCode();

		if (optionValueExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionValueExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(optionValueExternalReferenceCode));

			sb.append("\"");
		}

		Long optionValueId = getOptionValueId();

		if (optionValueId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionValueId\": ");

			sb.append(optionValueId);
		}

		String parentOptionExternalReferenceCode =
			getParentOptionExternalReferenceCode();

		if (parentOptionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentOptionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(parentOptionExternalReferenceCode));

			sb.append("\"");
		}

		String parentOptionFieldType = getParentOptionFieldType();

		if (parentOptionFieldType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentOptionFieldType\": ");

			sb.append("\"");

			sb.append(_escape(parentOptionFieldType));

			sb.append("\"");
		}

		Boolean parentOptionSkuContributor = getParentOptionSkuContributor();

		if (parentOptionSkuContributor != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentOptionSkuContributor\": ");

			sb.append(parentOptionSkuContributor);
		}

		String value = getValue();

		if (value != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(value));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuOption",
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
// LIFERAY-REST-BUILDER-HASH:1447903972