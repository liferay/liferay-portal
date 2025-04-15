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

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("ProductConfigurationListAccountGroup")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"accountGroupId", "productConfigurationListId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductConfigurationListAccountGroup")
public class ProductConfigurationListAccountGroup implements Serializable {

	public static ProductConfigurationListAccountGroup toDTO(String json) {
		return ObjectMapperUtil.readValue(
			ProductConfigurationListAccountGroup.class, json);
	}

	public static ProductConfigurationListAccountGroup unsafeToDTO(
		String json) {

		return ObjectMapperUtil.unsafeReadValue(
			ProductConfigurationListAccountGroup.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public AccountGroup getAccountGroup() {
		if (_accountGroupSupplier != null) {
			accountGroup = _accountGroupSupplier.get();

			_accountGroupSupplier = null;
		}

		return accountGroup;
	}

	public void setAccountGroup(AccountGroup accountGroup) {
		this.accountGroup = accountGroup;

		_accountGroupSupplier = null;
	}

	@JsonIgnore
	public void setAccountGroup(
		UnsafeSupplier<AccountGroup, Exception> accountGroupUnsafeSupplier) {

		_accountGroupSupplier = () -> {
			try {
				return accountGroupUnsafeSupplier.get();
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
	protected AccountGroup accountGroup;

	@JsonIgnore
	private Supplier<AccountGroup> _accountGroupSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DAB-34098-789-N")
	public String getAccountGroupExternalReferenceCode() {
		if (_accountGroupExternalReferenceCodeSupplier != null) {
			accountGroupExternalReferenceCode =
				_accountGroupExternalReferenceCodeSupplier.get();

			_accountGroupExternalReferenceCodeSupplier = null;
		}

		return accountGroupExternalReferenceCode;
	}

	public void setAccountGroupExternalReferenceCode(
		String accountGroupExternalReferenceCode) {

		this.accountGroupExternalReferenceCode =
			accountGroupExternalReferenceCode;

		_accountGroupExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAccountGroupExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			accountGroupExternalReferenceCodeUnsafeSupplier) {

		_accountGroupExternalReferenceCodeSupplier = () -> {
			try {
				return accountGroupExternalReferenceCodeUnsafeSupplier.get();
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
	protected String accountGroupExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _accountGroupExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30324")
	public Long getAccountGroupId() {
		if (_accountGroupIdSupplier != null) {
			accountGroupId = _accountGroupIdSupplier.get();

			_accountGroupIdSupplier = null;
		}

		return accountGroupId;
	}

	public void setAccountGroupId(Long accountGroupId) {
		this.accountGroupId = accountGroupId;

		_accountGroupIdSupplier = null;
	}

	@JsonIgnore
	public void setAccountGroupId(
		UnsafeSupplier<Long, Exception> accountGroupIdUnsafeSupplier) {

		_accountGroupIdSupplier = () -> {
			try {
				return accountGroupIdUnsafeSupplier.get();
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
	@NotNull
	protected Long accountGroupId;

	@JsonIgnore
	private Supplier<Long> _accountGroupIdSupplier;

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

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30643")
	public Long getProductConfigurationListAccountGroupId() {
		if (_productConfigurationListAccountGroupIdSupplier != null) {
			productConfigurationListAccountGroupId =
				_productConfigurationListAccountGroupIdSupplier.get();

			_productConfigurationListAccountGroupIdSupplier = null;
		}

		return productConfigurationListAccountGroupId;
	}

	public void setProductConfigurationListAccountGroupId(
		Long productConfigurationListAccountGroupId) {

		this.productConfigurationListAccountGroupId =
			productConfigurationListAccountGroupId;

		_productConfigurationListAccountGroupIdSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurationListAccountGroupId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListAccountGroupIdUnsafeSupplier) {

		_productConfigurationListAccountGroupIdSupplier = () -> {
			try {
				return productConfigurationListAccountGroupIdUnsafeSupplier.
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long productConfigurationListAccountGroupId;

	@JsonIgnore
	private Supplier<Long> _productConfigurationListAccountGroupIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "PAB-34098-789-N")
	public String getProductConfigurationListExternalReferenceCode() {
		if (_productConfigurationListExternalReferenceCodeSupplier != null) {
			productConfigurationListExternalReferenceCode =
				_productConfigurationListExternalReferenceCodeSupplier.get();

			_productConfigurationListExternalReferenceCodeSupplier = null;
		}

		return productConfigurationListExternalReferenceCode;
	}

	public void setProductConfigurationListExternalReferenceCode(
		String productConfigurationListExternalReferenceCode) {

		this.productConfigurationListExternalReferenceCode =
			productConfigurationListExternalReferenceCode;

		_productConfigurationListExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurationListExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productConfigurationListExternalReferenceCodeUnsafeSupplier) {

		_productConfigurationListExternalReferenceCodeSupplier = () -> {
			try {
				return productConfigurationListExternalReferenceCodeUnsafeSupplier.
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
	protected String productConfigurationListExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_productConfigurationListExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getProductConfigurationListId() {
		if (_productConfigurationListIdSupplier != null) {
			productConfigurationListId =
				_productConfigurationListIdSupplier.get();

			_productConfigurationListIdSupplier = null;
		}

		return productConfigurationListId;
	}

	public void setProductConfigurationListId(Long productConfigurationListId) {
		this.productConfigurationListId = productConfigurationListId;

		_productConfigurationListIdSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurationListId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListIdUnsafeSupplier) {

		_productConfigurationListIdSupplier = () -> {
			try {
				return productConfigurationListIdUnsafeSupplier.get();
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
	@NotNull
	protected Long productConfigurationListId;

	@JsonIgnore
	private Supplier<Long> _productConfigurationListIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductConfigurationListAccountGroup)) {
			return false;
		}

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup =
				(ProductConfigurationListAccountGroup)object;

		return Objects.equals(
			toString(), productConfigurationListAccountGroup.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		AccountGroup accountGroup = getAccountGroup();

		if (accountGroup != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroup\": ");

			sb.append(String.valueOf(accountGroup));
		}

		String accountGroupExternalReferenceCode =
			getAccountGroupExternalReferenceCode();

		if (accountGroupExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(accountGroupExternalReferenceCode));

			sb.append("\"");
		}

		Long accountGroupId = getAccountGroupId();

		if (accountGroupId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupId\": ");

			sb.append(accountGroupId);
		}

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Long productConfigurationListAccountGroupId =
			getProductConfigurationListAccountGroupId();

		if (productConfigurationListAccountGroupId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListAccountGroupId\": ");

			sb.append(productConfigurationListAccountGroupId);
		}

		String productConfigurationListExternalReferenceCode =
			getProductConfigurationListExternalReferenceCode();

		if (productConfigurationListExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(productConfigurationListExternalReferenceCode));

			sb.append("\"");
		}

		Long productConfigurationListId = getProductConfigurationListId();

		if (productConfigurationListId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListId\": ");

			sb.append(productConfigurationListId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListAccountGroup",
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