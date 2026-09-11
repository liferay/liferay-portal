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
@GraphQLName("OAuthClientEntry")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "OAuthClientEntry")
public class OAuthClientEntry implements Serializable {

	public static OAuthClientEntry toDTO(String json) {
		return ObjectMapperUtil.readValue(OAuthClientEntry.class, json);
	}

	public static OAuthClientEntry unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(OAuthClientEntry.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAuthRequestParametersJSON() {
		if (_authRequestParametersJSONSupplier != null) {
			authRequestParametersJSON =
				_authRequestParametersJSONSupplier.get();

			_authRequestParametersJSONSupplier = null;
		}

		return authRequestParametersJSON;
	}

	public void setAuthRequestParametersJSON(String authRequestParametersJSON) {
		this.authRequestParametersJSON = authRequestParametersJSON;

		_authRequestParametersJSONSupplier = null;
	}

	@JsonIgnore
	public void setAuthRequestParametersJSON(
		UnsafeSupplier<String, Exception>
			authRequestParametersJSONUnsafeSupplier) {

		_authRequestParametersJSONSupplier = () -> {
			try {
				return authRequestParametersJSONUnsafeSupplier.get();
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
	protected String authRequestParametersJSON;

	@JsonIgnore
	private Supplier<String> _authRequestParametersJSONSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAuthServerWellKnownURI() {
		if (_authServerWellKnownURISupplier != null) {
			authServerWellKnownURI = _authServerWellKnownURISupplier.get();

			_authServerWellKnownURISupplier = null;
		}

		return authServerWellKnownURI;
	}

	public void setAuthServerWellKnownURI(String authServerWellKnownURI) {
		this.authServerWellKnownURI = authServerWellKnownURI;

		_authServerWellKnownURISupplier = null;
	}

	@JsonIgnore
	public void setAuthServerWellKnownURI(
		UnsafeSupplier<String, Exception>
			authServerWellKnownURIUnsafeSupplier) {

		_authServerWellKnownURISupplier = () -> {
			try {
				return authServerWellKnownURIUnsafeSupplier.get();
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
	protected String authServerWellKnownURI;

	@JsonIgnore
	private Supplier<String> _authServerWellKnownURISupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getClientId() {
		if (_clientIdSupplier != null) {
			clientId = _clientIdSupplier.get();

			_clientIdSupplier = null;
		}

		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;

		_clientIdSupplier = null;
	}

	@JsonIgnore
	public void setClientId(
		UnsafeSupplier<String, Exception> clientIdUnsafeSupplier) {

		_clientIdSupplier = () -> {
			try {
				return clientIdUnsafeSupplier.get();
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
	protected String clientId;

	@JsonIgnore
	private Supplier<String> _clientIdSupplier;

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
	public String getCustomClaims() {
		if (_customClaimsSupplier != null) {
			customClaims = _customClaimsSupplier.get();

			_customClaimsSupplier = null;
		}

		return customClaims;
	}

	public void setCustomClaims(String customClaims) {
		this.customClaims = customClaims;

		_customClaimsSupplier = null;
	}

	@JsonIgnore
	public void setCustomClaims(
		UnsafeSupplier<String, Exception> customClaimsUnsafeSupplier) {

		_customClaimsSupplier = () -> {
			try {
				return customClaimsUnsafeSupplier.get();
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
	protected String customClaims;

	@JsonIgnore
	private Supplier<String> _customClaimsSupplier;

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
	public String getInfoJSON() {
		if (_infoJSONSupplier != null) {
			infoJSON = _infoJSONSupplier.get();

			_infoJSONSupplier = null;
		}

		return infoJSON;
	}

	public void setInfoJSON(String infoJSON) {
		this.infoJSON = infoJSON;

		_infoJSONSupplier = null;
	}

	@JsonIgnore
	public void setInfoJSON(
		UnsafeSupplier<String, Exception> infoJSONUnsafeSupplier) {

		_infoJSONSupplier = () -> {
			try {
				return infoJSONUnsafeSupplier.get();
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
	protected String infoJSON;

	@JsonIgnore
	private Supplier<String> _infoJSONSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getMatcherField() {
		if (_matcherFieldSupplier != null) {
			matcherField = _matcherFieldSupplier.get();

			_matcherFieldSupplier = null;
		}

		return matcherField;
	}

	public void setMatcherField(String matcherField) {
		this.matcherField = matcherField;

		_matcherFieldSupplier = null;
	}

	@JsonIgnore
	public void setMatcherField(
		UnsafeSupplier<String, Exception> matcherFieldUnsafeSupplier) {

		_matcherFieldSupplier = () -> {
			try {
				return matcherFieldUnsafeSupplier.get();
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
	protected String matcherField;

	@JsonIgnore
	private Supplier<String> _matcherFieldSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getMetadataCacheTime() {
		if (_metadataCacheTimeSupplier != null) {
			metadataCacheTime = _metadataCacheTimeSupplier.get();

			_metadataCacheTimeSupplier = null;
		}

		return metadataCacheTime;
	}

	public void setMetadataCacheTime(Long metadataCacheTime) {
		this.metadataCacheTime = metadataCacheTime;

		_metadataCacheTimeSupplier = null;
	}

	@JsonIgnore
	public void setMetadataCacheTime(
		UnsafeSupplier<Long, Exception> metadataCacheTimeUnsafeSupplier) {

		_metadataCacheTimeSupplier = () -> {
			try {
				return metadataCacheTimeUnsafeSupplier.get();
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
	protected Long metadataCacheTime;

	@JsonIgnore
	private Supplier<Long> _metadataCacheTimeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public OAuthClientASLocalMetadata getOAuthClientASLocalMetadata() {
		if (_oAuthClientASLocalMetadataSupplier != null) {
			oAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataSupplier.get();

			_oAuthClientASLocalMetadataSupplier = null;
		}

		return oAuthClientASLocalMetadata;
	}

	public void setOAuthClientASLocalMetadata(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		this.oAuthClientASLocalMetadata = oAuthClientASLocalMetadata;

		_oAuthClientASLocalMetadataSupplier = null;
	}

	@JsonIgnore
	public void setOAuthClientASLocalMetadata(
		UnsafeSupplier<OAuthClientASLocalMetadata, Exception>
			oAuthClientASLocalMetadataUnsafeSupplier) {

		_oAuthClientASLocalMetadataSupplier = () -> {
			try {
				return oAuthClientASLocalMetadataUnsafeSupplier.get();
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
	protected OAuthClientASLocalMetadata oAuthClientASLocalMetadata;

	@JsonIgnore
	private Supplier<OAuthClientASLocalMetadata>
		_oAuthClientASLocalMetadataSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getOidcUserInfoMapperJSON() {
		if (_oidcUserInfoMapperJSONSupplier != null) {
			oidcUserInfoMapperJSON = _oidcUserInfoMapperJSONSupplier.get();

			_oidcUserInfoMapperJSONSupplier = null;
		}

		return oidcUserInfoMapperJSON;
	}

	public void setOidcUserInfoMapperJSON(String oidcUserInfoMapperJSON) {
		this.oidcUserInfoMapperJSON = oidcUserInfoMapperJSON;

		_oidcUserInfoMapperJSONSupplier = null;
	}

	@JsonIgnore
	public void setOidcUserInfoMapperJSON(
		UnsafeSupplier<String, Exception>
			oidcUserInfoMapperJSONUnsafeSupplier) {

		_oidcUserInfoMapperJSONSupplier = () -> {
			try {
				return oidcUserInfoMapperJSONUnsafeSupplier.get();
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
	protected String oidcUserInfoMapperJSON;

	@JsonIgnore
	private Supplier<String> _oidcUserInfoMapperJSONSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getTokenConnectionTimeout() {
		if (_tokenConnectionTimeoutSupplier != null) {
			tokenConnectionTimeout = _tokenConnectionTimeoutSupplier.get();

			_tokenConnectionTimeoutSupplier = null;
		}

		return tokenConnectionTimeout;
	}

	public void setTokenConnectionTimeout(Integer tokenConnectionTimeout) {
		this.tokenConnectionTimeout = tokenConnectionTimeout;

		_tokenConnectionTimeoutSupplier = null;
	}

	@JsonIgnore
	public void setTokenConnectionTimeout(
		UnsafeSupplier<Integer, Exception>
			tokenConnectionTimeoutUnsafeSupplier) {

		_tokenConnectionTimeoutSupplier = () -> {
			try {
				return tokenConnectionTimeoutUnsafeSupplier.get();
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
	protected Integer tokenConnectionTimeout;

	@JsonIgnore
	private Supplier<Integer> _tokenConnectionTimeoutSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTokenRequestParametersJSON() {
		if (_tokenRequestParametersJSONSupplier != null) {
			tokenRequestParametersJSON =
				_tokenRequestParametersJSONSupplier.get();

			_tokenRequestParametersJSONSupplier = null;
		}

		return tokenRequestParametersJSON;
	}

	public void setTokenRequestParametersJSON(
		String tokenRequestParametersJSON) {

		this.tokenRequestParametersJSON = tokenRequestParametersJSON;

		_tokenRequestParametersJSONSupplier = null;
	}

	@JsonIgnore
	public void setTokenRequestParametersJSON(
		UnsafeSupplier<String, Exception>
			tokenRequestParametersJSONUnsafeSupplier) {

		_tokenRequestParametersJSONSupplier = () -> {
			try {
				return tokenRequestParametersJSONUnsafeSupplier.get();
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
	protected String tokenRequestParametersJSON;

	@JsonIgnore
	private Supplier<String> _tokenRequestParametersJSONSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OAuthClientEntry)) {
			return false;
		}

		OAuthClientEntry oAuthClientEntry = (OAuthClientEntry)object;

		return Objects.equals(toString(), oAuthClientEntry.toString());
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

		String authRequestParametersJSON = getAuthRequestParametersJSON();

		if (authRequestParametersJSON != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authRequestParametersJSON\": ");

			sb.append("\"");

			sb.append(_escape(authRequestParametersJSON));

			sb.append("\"");
		}

		String authServerWellKnownURI = getAuthServerWellKnownURI();

		if (authServerWellKnownURI != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authServerWellKnownURI\": ");

			sb.append("\"");

			sb.append(_escape(authServerWellKnownURI));

			sb.append("\"");
		}

		String clientId = getClientId();

		if (clientId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clientId\": ");

			sb.append("\"");

			sb.append(_escape(clientId));

			sb.append("\"");
		}

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(creator);
		}

		String customClaims = getCustomClaims();

		if (customClaims != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customClaims\": ");

			sb.append("\"");

			sb.append(_escape(customClaims));

			sb.append("\"");
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

		String infoJSON = getInfoJSON();

		if (infoJSON != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"infoJSON\": ");

			sb.append("\"");

			sb.append(_escape(infoJSON));

			sb.append("\"");
		}

		String matcherField = getMatcherField();

		if (matcherField != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"matcherField\": ");

			sb.append("\"");

			sb.append(_escape(matcherField));

			sb.append("\"");
		}

		Long metadataCacheTime = getMetadataCacheTime();

		if (metadataCacheTime != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataCacheTime\": ");

			sb.append(metadataCacheTime);
		}

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			getOAuthClientASLocalMetadata();

		if (oAuthClientASLocalMetadata != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oAuthClientASLocalMetadata\": ");

			sb.append(String.valueOf(oAuthClientASLocalMetadata));
		}

		String oidcUserInfoMapperJSON = getOidcUserInfoMapperJSON();

		if (oidcUserInfoMapperJSON != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oidcUserInfoMapperJSON\": ");

			sb.append("\"");

			sb.append(_escape(oidcUserInfoMapperJSON));

			sb.append("\"");
		}

		Integer tokenConnectionTimeout = getTokenConnectionTimeout();

		if (tokenConnectionTimeout != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tokenConnectionTimeout\": ");

			sb.append(tokenConnectionTimeout);
		}

		String tokenRequestParametersJSON = getTokenRequestParametersJSON();

		if (tokenRequestParametersJSON != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tokenRequestParametersJSON\": ");

			sb.append("\"");

			sb.append(_escape(tokenRequestParametersJSON));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.oauth.client.rest.dto.v1_0.OAuthClientEntry",
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
// LIFERAY-REST-BUILDER-HASH:1994775296