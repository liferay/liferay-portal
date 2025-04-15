/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(description = "https://www.schema.org/Creator", value = "Creator")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Creator")
public class Creator implements Serializable {

	public static Creator toDTO(String json) {
		return ObjectMapperUtil.readValue(Creator.class, json);
	}

	public static Creator unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Creator.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAdditionalName() {
		if (_additionalNameSupplier != null) {
			additionalName = _additionalNameSupplier.get();

			_additionalNameSupplier = null;
		}

		return additionalName;
	}

	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;

		_additionalNameSupplier = null;
	}

	@JsonIgnore
	public void setAdditionalName(
		UnsafeSupplier<String, Exception> additionalNameUnsafeSupplier) {

		_additionalNameSupplier = () -> {
			try {
				return additionalNameUnsafeSupplier.get();
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
	protected String additionalName;

	@JsonIgnore
	private Supplier<String> _additionalNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of the content."
	)
	public String getContentType() {
		if (_contentTypeSupplier != null) {
			contentType = _contentTypeSupplier.get();

			_contentTypeSupplier = null;
		}

		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;

		_contentTypeSupplier = null;
	}

	@JsonIgnore
	public void setContentType(
		UnsafeSupplier<String, Exception> contentTypeUnsafeSupplier) {

		_contentTypeSupplier = () -> {
			try {
				return contentTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of the content.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String contentType;

	@JsonIgnore
	private Supplier<String> _contentTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFamilyName() {
		if (_familyNameSupplier != null) {
			familyName = _familyNameSupplier.get();

			_familyNameSupplier = null;
		}

		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;

		_familyNameSupplier = null;
	}

	@JsonIgnore
	public void setFamilyName(
		UnsafeSupplier<String, Exception> familyNameUnsafeSupplier) {

		_familyNameSupplier = () -> {
			try {
				return familyNameUnsafeSupplier.get();
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
	protected String familyName;

	@JsonIgnore
	private Supplier<String> _familyNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getGivenName() {
		if (_givenNameSupplier != null) {
			givenName = _givenNameSupplier.get();

			_givenNameSupplier = null;
		}

		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;

		_givenNameSupplier = null;
	}

	@JsonIgnore
	public void setGivenName(
		UnsafeSupplier<String, Exception> givenNameUnsafeSupplier) {

		_givenNameSupplier = () -> {
			try {
				return givenNameUnsafeSupplier.get();
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
	protected String givenName;

	@JsonIgnore
	private Supplier<String> _givenNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getImage() {
		if (_imageSupplier != null) {
			image = _imageSupplier.get();

			_imageSupplier = null;
		}

		return image;
	}

	public void setImage(String image) {
		this.image = image;

		_imageSupplier = null;
	}

	@JsonIgnore
	public void setImage(
		UnsafeSupplier<String, Exception> imageUnsafeSupplier) {

		_imageSupplier = () -> {
			try {
				return imageUnsafeSupplier.get();
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
	protected String image;

	@JsonIgnore
	private Supplier<String> _imageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getProfileURL() {
		if (_profileURLSupplier != null) {
			profileURL = _profileURLSupplier.get();

			_profileURLSupplier = null;
		}

		return profileURL;
	}

	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;

		_profileURLSupplier = null;
	}

	@JsonIgnore
	public void setProfileURL(
		UnsafeSupplier<String, Exception> profileURLUnsafeSupplier) {

		_profileURLSupplier = () -> {
			try {
				return profileURLUnsafeSupplier.get();
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
	protected String profileURL;

	@JsonIgnore
	private Supplier<String> _profileURLSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Creator)) {
			return false;
		}

		Creator creator = (Creator)object;

		return Objects.equals(toString(), creator.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String additionalName = getAdditionalName();

		if (additionalName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionalName\": ");

			sb.append("\"");

			sb.append(_escape(additionalName));

			sb.append("\"");
		}

		String contentType = getContentType();

		if (contentType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(contentType));

			sb.append("\"");
		}

		String familyName = getFamilyName();

		if (familyName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"familyName\": ");

			sb.append("\"");

			sb.append(_escape(familyName));

			sb.append("\"");
		}

		String givenName = getGivenName();

		if (givenName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"givenName\": ");

			sb.append("\"");

			sb.append(_escape(givenName));

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

		String image = getImage();

		if (image != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"image\": ");

			sb.append("\"");

			sb.append(_escape(image));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		String profileURL = getProfileURL();

		if (profileURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"profileURL\": ");

			sb.append("\"");

			sb.append(_escape(profileURL));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.form.dto.v1_0.Creator",
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