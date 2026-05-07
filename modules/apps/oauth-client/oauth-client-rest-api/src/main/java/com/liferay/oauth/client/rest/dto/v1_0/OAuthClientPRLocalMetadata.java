/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.headless.delivery.dto.v1_0.Creator;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Manuele Castro
 * @generated
 */
@Generated("")
@GraphQLName("OAuthClientPRLocalMetadata")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "OAuthClientPRLocalMetadata")
public class OAuthClientPRLocalMetadata implements Serializable {

	public static OAuthClientPRLocalMetadata toDTO(String json) {
		return ObjectMapperUtil.readValue(
			OAuthClientPRLocalMetadata.class, json);
	}

	public static OAuthClientPRLocalMetadata unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			OAuthClientPRLocalMetadata.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Creator getCreator() {
		if (_creatorSupplier != null) {
			creator = _creatorSupplier.get();

			_creatorSupplier = null;
		}

		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;

		_creatorSupplier = null;
	}

	@JsonIgnore
	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		_creatorSupplier = () -> {
			try {
				return creatorUnsafeSupplier.get();
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
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
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
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
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
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getLocalWellKnownEnabled() {
		if (_localWellKnownEnabledSupplier != null) {
			localWellKnownEnabled = _localWellKnownEnabledSupplier.get();

			_localWellKnownEnabledSupplier = null;
		}

		return localWellKnownEnabled;
	}

	public void setLocalWellKnownEnabled(Boolean localWellKnownEnabled) {
		this.localWellKnownEnabled = localWellKnownEnabled;

		_localWellKnownEnabledSupplier = null;
	}

	@JsonIgnore
	public void setLocalWellKnownEnabled(
		UnsafeSupplier<Boolean, Exception>
			localWellKnownEnabledUnsafeSupplier) {

		_localWellKnownEnabledSupplier = () -> {
			try {
				return localWellKnownEnabledUnsafeSupplier.get();
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
	protected Boolean localWellKnownEnabled;

	@JsonIgnore
	private Supplier<Boolean> _localWellKnownEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLocalWellKnownURI() {
		if (_localWellKnownURISupplier != null) {
			localWellKnownURI = _localWellKnownURISupplier.get();

			_localWellKnownURISupplier = null;
		}

		return localWellKnownURI;
	}

	public void setLocalWellKnownURI(String localWellKnownURI) {
		this.localWellKnownURI = localWellKnownURI;

		_localWellKnownURISupplier = null;
	}

	@JsonIgnore
	public void setLocalWellKnownURI(
		UnsafeSupplier<String, Exception> localWellKnownURIUnsafeSupplier) {

		_localWellKnownURISupplier = () -> {
			try {
				return localWellKnownURIUnsafeSupplier.get();
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
	protected String localWellKnownURI;

	@JsonIgnore
	private Supplier<String> _localWellKnownURISupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getMetadataJSON() {
		if (_metadataJSONSupplier != null) {
			metadataJSON = _metadataJSONSupplier.get();

			_metadataJSONSupplier = null;
		}

		return metadataJSON;
	}

	public void setMetadataJSON(String metadataJSON) {
		this.metadataJSON = metadataJSON;

		_metadataJSONSupplier = null;
	}

	@JsonIgnore
	public void setMetadataJSON(
		UnsafeSupplier<String, Exception> metadataJSONUnsafeSupplier) {

		_metadataJSONSupplier = () -> {
			try {
				return metadataJSONUnsafeSupplier.get();
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
	protected String metadataJSON;

	@JsonIgnore
	private Supplier<String> _metadataJSONSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getResource() {
		if (_resourceSupplier != null) {
			resource = _resourceSupplier.get();

			_resourceSupplier = null;
		}

		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;

		_resourceSupplier = null;
	}

	@JsonIgnore
	public void setResource(
		UnsafeSupplier<String, Exception> resourceUnsafeSupplier) {

		_resourceSupplier = () -> {
			try {
				return resourceUnsafeSupplier.get();
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
	protected String resource;

	@JsonIgnore
	private Supplier<String> _resourceSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OAuthClientPRLocalMetadata)) {
			return false;
		}

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			(OAuthClientPRLocalMetadata)object;

		return Objects.equals(
			toString(), oAuthClientPRLocalMetadata.toString());
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

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(creator);
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

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

		Boolean localWellKnownEnabled = getLocalWellKnownEnabled();

		if (localWellKnownEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localWellKnownEnabled\": ");

			sb.append(localWellKnownEnabled);
		}

		String localWellKnownURI = getLocalWellKnownURI();

		if (localWellKnownURI != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localWellKnownURI\": ");

			sb.append("\"");

			sb.append(_escape(localWellKnownURI));

			sb.append("\"");
		}

		String metadataJSON = getMetadataJSON();

		if (metadataJSON != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataJSON\": ");

			sb.append("\"");

			sb.append(_escape(metadataJSON));

			sb.append("\"");
		}

		String resource = getResource();

		if (resource != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resource\": ");

			sb.append("\"");

			sb.append(_escape(resource));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.oauth.client.rest.dto.v1_0.OAuthClientPRLocalMetadata",
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
// LIFERAY-REST-BUILDER-HASH:1581154868