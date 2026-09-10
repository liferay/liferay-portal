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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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

import java.util.Arrays;
import java.util.Collection;
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
@GraphQLName("OAuthClientASLocalMetadata")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "OAuthClientASLocalMetadata")
public class OAuthClientASLocalMetadata implements Serializable {

	public static OAuthClientASLocalMetadata toDTO(String json) {
		return ObjectMapperUtil.readValue(
			OAuthClientASLocalMetadata.class, json);
	}

	public static OAuthClientASLocalMetadata unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			OAuthClientASLocalMetadata.class, json);
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
	public String getIssuer() {
		if (_issuerSupplier != null) {
			issuer = _issuerSupplier.get();

			_issuerSupplier = null;
		}

		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;

		_issuerSupplier = null;
	}

	@JsonIgnore
	public void setIssuer(
		UnsafeSupplier<String, Exception> issuerUnsafeSupplier) {

		_issuerSupplier = () -> {
			try {
				return issuerUnsafeSupplier.get();
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
	protected String issuer;

	@JsonIgnore
	private Supplier<String> _issuerSupplier;

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
	public String getOAuthASLocalWellKnownURI() {
		if (_oAuthASLocalWellKnownURISupplier != null) {
			oAuthASLocalWellKnownURI = _oAuthASLocalWellKnownURISupplier.get();

			_oAuthASLocalWellKnownURISupplier = null;
		}

		return oAuthASLocalWellKnownURI;
	}

	public void setOAuthASLocalWellKnownURI(String oAuthASLocalWellKnownURI) {
		this.oAuthASLocalWellKnownURI = oAuthASLocalWellKnownURI;

		_oAuthASLocalWellKnownURISupplier = null;
	}

	@JsonIgnore
	public void setOAuthASLocalWellKnownURI(
		UnsafeSupplier<String, Exception>
			oAuthASLocalWellKnownURIUnsafeSupplier) {

		_oAuthASLocalWellKnownURISupplier = () -> {
			try {
				return oAuthASLocalWellKnownURIUnsafeSupplier.get();
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
	protected String oAuthASLocalWellKnownURI;

	@JsonIgnore
	private Supplier<String> _oAuthASLocalWellKnownURISupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getOAuthASMetadataJSON() {
		if (_oAuthASMetadataJSONSupplier != null) {
			oAuthASMetadataJSON = _oAuthASMetadataJSONSupplier.get();

			_oAuthASMetadataJSONSupplier = null;
		}

		return oAuthASMetadataJSON;
	}

	public void setOAuthASMetadataJSON(String oAuthASMetadataJSON) {
		this.oAuthASMetadataJSON = oAuthASMetadataJSON;

		_oAuthASMetadataJSONSupplier = null;
	}

	@JsonIgnore
	public void setOAuthASMetadataJSON(
		UnsafeSupplier<String, Exception> oAuthASMetadataJSONUnsafeSupplier) {

		_oAuthASMetadataJSONSupplier = () -> {
			try {
				return oAuthASMetadataJSONUnsafeSupplier.get();
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
	protected String oAuthASMetadataJSON;

	@JsonIgnore
	private Supplier<String> _oAuthASMetadataJSONSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OAuthClientASLocalMetadata)) {
			return false;
		}

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			(OAuthClientASLocalMetadata)object;

		return Objects.equals(
			toString(), oAuthClientASLocalMetadata.toString());
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

		String issuer = getIssuer();

		if (issuer != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"issuer\": ");

			sb.append("\"");

			sb.append(_escape(issuer));

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

		String oAuthASLocalWellKnownURI = getOAuthASLocalWellKnownURI();

		if (oAuthASLocalWellKnownURI != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oAuthASLocalWellKnownURI\": ");

			sb.append("\"");

			sb.append(_escape(oAuthASLocalWellKnownURI));

			sb.append("\"");
		}

		String oAuthASMetadataJSON = getOAuthASMetadataJSON();

		if (oAuthASMetadataJSON != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oAuthASMetadataJSON\": ");

			sb.append("\"");

			sb.append(_escape(oAuthASMetadataJSON));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.oauth.client.rest.dto.v1_0.OAuthClientASLocalMetadata",
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
// LIFERAY-REST-BUILDER-HASH:-948129685