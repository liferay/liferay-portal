/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName("ServiceProviderConfig")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ServiceProviderConfig")
public class ServiceProviderConfig implements Serializable {

	public static ServiceProviderConfig toDTO(String json) {
		return ObjectMapperUtil.readValue(ServiceProviderConfig.class, json);
	}

	public static ServiceProviderConfig unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ServiceProviderConfig.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public AuthenticationScheme[] getAuthenticationSchemes() {
		if (_authenticationSchemesSupplier != null) {
			authenticationSchemes = _authenticationSchemesSupplier.get();

			_authenticationSchemesSupplier = null;
		}

		return authenticationSchemes;
	}

	public void setAuthenticationSchemes(
		AuthenticationScheme[] authenticationSchemes) {

		this.authenticationSchemes = authenticationSchemes;

		_authenticationSchemesSupplier = null;
	}

	@JsonIgnore
	public void setAuthenticationSchemes(
		UnsafeSupplier<AuthenticationScheme[], Exception>
			authenticationSchemesUnsafeSupplier) {

		_authenticationSchemesSupplier = () -> {
			try {
				return authenticationSchemesUnsafeSupplier.get();
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
	protected AuthenticationScheme[] authenticationSchemes;

	@JsonIgnore
	private Supplier<AuthenticationScheme[]> _authenticationSchemesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Bulk getBulk() {
		if (_bulkSupplier != null) {
			bulk = _bulkSupplier.get();

			_bulkSupplier = null;
		}

		return bulk;
	}

	public void setBulk(Bulk bulk) {
		this.bulk = bulk;

		_bulkSupplier = null;
	}

	@JsonIgnore
	public void setBulk(UnsafeSupplier<Bulk, Exception> bulkUnsafeSupplier) {
		_bulkSupplier = () -> {
			try {
				return bulkUnsafeSupplier.get();
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
	protected Bulk bulk;

	@JsonIgnore
	private Supplier<Bulk> _bulkSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ChangePassword getChangePassword() {
		if (_changePasswordSupplier != null) {
			changePassword = _changePasswordSupplier.get();

			_changePasswordSupplier = null;
		}

		return changePassword;
	}

	public void setChangePassword(ChangePassword changePassword) {
		this.changePassword = changePassword;

		_changePasswordSupplier = null;
	}

	@JsonIgnore
	public void setChangePassword(
		UnsafeSupplier<ChangePassword, Exception>
			changePasswordUnsafeSupplier) {

		_changePasswordSupplier = () -> {
			try {
				return changePasswordUnsafeSupplier.get();
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
	protected ChangePassword changePassword;

	@JsonIgnore
	private Supplier<ChangePassword> _changePasswordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDocumentationUri() {
		if (_documentationUriSupplier != null) {
			documentationUri = _documentationUriSupplier.get();

			_documentationUriSupplier = null;
		}

		return documentationUri;
	}

	public void setDocumentationUri(String documentationUri) {
		this.documentationUri = documentationUri;

		_documentationUriSupplier = null;
	}

	@JsonIgnore
	public void setDocumentationUri(
		UnsafeSupplier<String, Exception> documentationUriUnsafeSupplier) {

		_documentationUriSupplier = () -> {
			try {
				return documentationUriUnsafeSupplier.get();
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
	protected String documentationUri;

	@JsonIgnore
	private Supplier<String> _documentationUriSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Etag getEtag() {
		if (_etagSupplier != null) {
			etag = _etagSupplier.get();

			_etagSupplier = null;
		}

		return etag;
	}

	public void setEtag(Etag etag) {
		this.etag = etag;

		_etagSupplier = null;
	}

	@JsonIgnore
	public void setEtag(UnsafeSupplier<Etag, Exception> etagUnsafeSupplier) {
		_etagSupplier = () -> {
			try {
				return etagUnsafeSupplier.get();
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
	protected Etag etag;

	@JsonIgnore
	private Supplier<Etag> _etagSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Filter getFilter() {
		if (_filterSupplier != null) {
			filter = _filterSupplier.get();

			_filterSupplier = null;
		}

		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;

		_filterSupplier = null;
	}

	@JsonIgnore
	public void setFilter(
		UnsafeSupplier<Filter, Exception> filterUnsafeSupplier) {

		_filterSupplier = () -> {
			try {
				return filterUnsafeSupplier.get();
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
	protected Filter filter;

	@JsonIgnore
	private Supplier<Filter> _filterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Meta getMeta() {
		if (_metaSupplier != null) {
			meta = _metaSupplier.get();

			_metaSupplier = null;
		}

		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;

		_metaSupplier = null;
	}

	@JsonIgnore
	public void setMeta(UnsafeSupplier<Meta, Exception> metaUnsafeSupplier) {
		_metaSupplier = () -> {
			try {
				return metaUnsafeSupplier.get();
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
	protected Meta meta;

	@JsonIgnore
	private Supplier<Meta> _metaSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Patch getPatch() {
		if (_patchSupplier != null) {
			patch = _patchSupplier.get();

			_patchSupplier = null;
		}

		return patch;
	}

	public void setPatch(Patch patch) {
		this.patch = patch;

		_patchSupplier = null;
	}

	@JsonIgnore
	public void setPatch(UnsafeSupplier<Patch, Exception> patchUnsafeSupplier) {
		_patchSupplier = () -> {
			try {
				return patchUnsafeSupplier.get();
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
	protected Patch patch;

	@JsonIgnore
	private Supplier<Patch> _patchSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getSchemas() {
		if (_schemasSupplier != null) {
			schemas = _schemasSupplier.get();

			_schemasSupplier = null;
		}

		return schemas;
	}

	public void setSchemas(String[] schemas) {
		this.schemas = schemas;

		_schemasSupplier = null;
	}

	@JsonIgnore
	public void setSchemas(
		UnsafeSupplier<String[], Exception> schemasUnsafeSupplier) {

		_schemasSupplier = () -> {
			try {
				return schemasUnsafeSupplier.get();
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
	protected String[] schemas;

	@JsonIgnore
	private Supplier<String[]> _schemasSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Sort getSort() {
		if (_sortSupplier != null) {
			sort = _sortSupplier.get();

			_sortSupplier = null;
		}

		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;

		_sortSupplier = null;
	}

	@JsonIgnore
	public void setSort(UnsafeSupplier<Sort, Exception> sortUnsafeSupplier) {
		_sortSupplier = () -> {
			try {
				return sortUnsafeSupplier.get();
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
	protected Sort sort;

	@JsonIgnore
	private Supplier<Sort> _sortSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ServiceProviderConfig)) {
			return false;
		}

		ServiceProviderConfig serviceProviderConfig =
			(ServiceProviderConfig)object;

		return Objects.equals(toString(), serviceProviderConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		AuthenticationScheme[] authenticationSchemes =
			getAuthenticationSchemes();

		if (authenticationSchemes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authenticationSchemes\": ");

			sb.append("[");

			for (int i = 0; i < authenticationSchemes.length; i++) {
				sb.append(String.valueOf(authenticationSchemes[i]));

				if ((i + 1) < authenticationSchemes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Bulk bulk = getBulk();

		if (bulk != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulk\": ");

			sb.append(String.valueOf(bulk));
		}

		ChangePassword changePassword = getChangePassword();

		if (changePassword != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"changePassword\": ");

			sb.append(String.valueOf(changePassword));
		}

		String documentationUri = getDocumentationUri();

		if (documentationUri != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentationUri\": ");

			sb.append("\"");

			sb.append(_escape(documentationUri));

			sb.append("\"");
		}

		Etag etag = getEtag();

		if (etag != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"etag\": ");

			sb.append(String.valueOf(etag));
		}

		Filter filter = getFilter();

		if (filter != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"filter\": ");

			sb.append(String.valueOf(filter));
		}

		Meta meta = getMeta();

		if (meta != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(meta));
		}

		Patch patch = getPatch();

		if (patch != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"patch\": ");

			sb.append(String.valueOf(patch));
		}

		String[] schemas = getSchemas();

		if (schemas != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < schemas.length; i++) {
				sb.append("\"");

				sb.append(_escape(schemas[i]));

				sb.append("\"");

				if ((i + 1) < schemas.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Sort sort = getSort();

		if (sort != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sort\": ");

			sb.append(String.valueOf(sort));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.scim.rest.dto.v1_0.ServiceProviderConfig",
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