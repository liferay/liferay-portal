/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.dto.v1_0;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
@GraphQLName("PortalInstance")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PortalInstance")
public class PortalInstance implements Serializable {

	public static PortalInstance toDTO(String json) {
		return ObjectMapperUtil.readValue(PortalInstance.class, json);
	}

	public static PortalInstance unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PortalInstance.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The portal instance's administrator. This field is optional and is only used in the portal instance creation."
	)
	@Valid
	public Admin getAdmin() {
		if (_adminSupplier != null) {
			admin = _adminSupplier.get();

			_adminSupplier = null;
		}

		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;

		_adminSupplier = null;
	}

	@JsonIgnore
	public void setAdmin(UnsafeSupplier<Admin, Exception> adminUnsafeSupplier) {
		_adminSupplier = () -> {
			try {
				return adminUnsafeSupplier.get();
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
		description = "The portal instance's administrator. This field is optional and is only used in the portal instance creation."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Admin admin;

	@JsonIgnore
	private Supplier<Admin> _adminSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "internal unique key."
	)
	public Long getCompanyId() {
		if (_companyIdSupplier != null) {
			companyId = _companyIdSupplier.get();

			_companyIdSupplier = null;
		}

		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;

		_companyIdSupplier = null;
	}

	@JsonIgnore
	public void setCompanyId(
		UnsafeSupplier<Long, Exception> companyIdUnsafeSupplier) {

		_companyIdSupplier = () -> {
			try {
				return companyIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "internal unique key.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long companyId;

	@JsonIgnore
	private Supplier<Long> _companyIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "domain used for email authentication."
	)
	public String getDomain() {
		if (_domainSupplier != null) {
			domain = _domainSupplier.get();

			_domainSupplier = null;
		}

		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;

		_domainSupplier = null;
	}

	@JsonIgnore
	public void setDomain(
		UnsafeSupplier<String, Exception> domainUnsafeSupplier) {

		_domainSupplier = () -> {
			try {
				return domainUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "domain used for email authentication.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String domain;

	@JsonIgnore
	private Supplier<String> _domainSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "public unique key (corresponds to company's webId field)"
	)
	public String getPortalInstanceId() {
		if (_portalInstanceIdSupplier != null) {
			portalInstanceId = _portalInstanceIdSupplier.get();

			_portalInstanceIdSupplier = null;
		}

		return portalInstanceId;
	}

	public void setPortalInstanceId(String portalInstanceId) {
		this.portalInstanceId = portalInstanceId;

		_portalInstanceIdSupplier = null;
	}

	@JsonIgnore
	public void setPortalInstanceId(
		UnsafeSupplier<String, Exception> portalInstanceIdUnsafeSupplier) {

		_portalInstanceIdSupplier = () -> {
			try {
				return portalInstanceIdUnsafeSupplier.get();
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
		description = "public unique key (corresponds to company's webId field)"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String portalInstanceId;

	@JsonIgnore
	private Supplier<String> _portalInstanceIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSiteInitializerKey() {
		if (_siteInitializerKeySupplier != null) {
			siteInitializerKey = _siteInitializerKeySupplier.get();

			_siteInitializerKeySupplier = null;
		}

		return siteInitializerKey;
	}

	public void setSiteInitializerKey(String siteInitializerKey) {
		this.siteInitializerKey = siteInitializerKey;

		_siteInitializerKeySupplier = null;
	}

	@JsonIgnore
	public void setSiteInitializerKey(
		UnsafeSupplier<String, Exception> siteInitializerKeyUnsafeSupplier) {

		_siteInitializerKeySupplier = () -> {
			try {
				return siteInitializerKeyUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String siteInitializerKey;

	@JsonIgnore
	private Supplier<String> _siteInitializerKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getVirtualHost() {
		if (_virtualHostSupplier != null) {
			virtualHost = _virtualHostSupplier.get();

			_virtualHostSupplier = null;
		}

		return virtualHost;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;

		_virtualHostSupplier = null;
	}

	@JsonIgnore
	public void setVirtualHost(
		UnsafeSupplier<String, Exception> virtualHostUnsafeSupplier) {

		_virtualHostSupplier = () -> {
			try {
				return virtualHostUnsafeSupplier.get();
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
	protected String virtualHost;

	@JsonIgnore
	private Supplier<String> _virtualHostSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortalInstance)) {
			return false;
		}

		PortalInstance portalInstance = (PortalInstance)object;

		return Objects.equals(toString(), portalInstance.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		Admin admin = getAdmin();

		if (admin != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"admin\": ");

			sb.append(String.valueOf(admin));
		}

		Long companyId = getCompanyId();

		if (companyId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"companyId\": ");

			sb.append(companyId);
		}

		String domain = getDomain();

		if (domain != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"domain\": ");

			sb.append("\"");

			sb.append(_escape(domain));

			sb.append("\"");
		}

		String portalInstanceId = getPortalInstanceId();

		if (portalInstanceId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portalInstanceId\": ");

			sb.append("\"");

			sb.append(_escape(portalInstanceId));

			sb.append("\"");
		}

		String siteInitializerKey = getSiteInitializerKey();

		if (siteInitializerKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteInitializerKey\": ");

			sb.append("\"");

			sb.append(_escape(siteInitializerKey));

			sb.append("\"");
		}

		String virtualHost = getVirtualHost();

		if (virtualHost != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"virtualHost\": ");

			sb.append("\"");

			sb.append(_escape(virtualHost));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.portal.instances.dto.v1_0.PortalInstance",
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