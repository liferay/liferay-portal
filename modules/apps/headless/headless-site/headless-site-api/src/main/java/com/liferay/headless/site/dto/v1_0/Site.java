/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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
import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(description = "Represents the site being created.", value = "Site")
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents the site being created.",
	requiredProperties = {"name"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Site")
public class Site implements Serializable {

	public static Site toDTO(String json) {
		return ObjectMapperUtil.readValue(Site.class, json);
	}

	public static Site unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Site.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The site's external reference code."
	)
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

	@GraphQLField(description = "The site's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFriendlyUrlPath() {
		if (_friendlyUrlPathSupplier != null) {
			friendlyUrlPath = _friendlyUrlPathSupplier.get();

			_friendlyUrlPathSupplier = null;
		}

		return friendlyUrlPath;
	}

	public void setFriendlyUrlPath(String friendlyUrlPath) {
		this.friendlyUrlPath = friendlyUrlPath;

		_friendlyUrlPathSupplier = null;
	}

	@JsonIgnore
	public void setFriendlyUrlPath(
		UnsafeSupplier<String, Exception> friendlyUrlPathUnsafeSupplier) {

		_friendlyUrlPathSupplier = () -> {
			try {
				return friendlyUrlPathUnsafeSupplier.get();
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
	protected String friendlyUrlPath;

	@JsonIgnore
	private Supplier<String> _friendlyUrlPathSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The default value is open."
	)
	@JsonGetter("membershipType")
	@Valid
	public MembershipType getMembershipType() {
		if (_membershipTypeSupplier != null) {
			membershipType = _membershipTypeSupplier.get();

			_membershipTypeSupplier = null;
		}

		return membershipType;
	}

	@JsonIgnore
	public String getMembershipTypeAsString() {
		MembershipType membershipType = getMembershipType();

		if (membershipType == null) {
			return null;
		}

		return membershipType.toString();
	}

	public void setMembershipType(MembershipType membershipType) {
		this.membershipType = membershipType;

		_membershipTypeSupplier = null;
	}

	@JsonIgnore
	public void setMembershipType(
		UnsafeSupplier<MembershipType, Exception>
			membershipTypeUnsafeSupplier) {

		_membershipTypeSupplier = () -> {
			try {
				return membershipTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The default value is open.")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected MembershipType membershipType;

	@JsonIgnore
	private Supplier<MembershipType> _membershipTypeSupplier;

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
	@NotEmpty
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getParentSiteKey() {
		if (_parentSiteKeySupplier != null) {
			parentSiteKey = _parentSiteKeySupplier.get();

			_parentSiteKeySupplier = null;
		}

		return parentSiteKey;
	}

	public void setParentSiteKey(String parentSiteKey) {
		this.parentSiteKey = parentSiteKey;

		_parentSiteKeySupplier = null;
	}

	@JsonIgnore
	public void setParentSiteKey(
		UnsafeSupplier<String, Exception> parentSiteKeyUnsafeSupplier) {

		_parentSiteKeySupplier = () -> {
			try {
				return parentSiteKeyUnsafeSupplier.get();
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
	protected String parentSiteKey;

	@JsonIgnore
	private Supplier<String> _parentSiteKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTemplateKey() {
		if (_templateKeySupplier != null) {
			templateKey = _templateKeySupplier.get();

			_templateKeySupplier = null;
		}

		return templateKey;
	}

	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;

		_templateKeySupplier = null;
	}

	@JsonIgnore
	public void setTemplateKey(
		UnsafeSupplier<String, Exception> templateKeyUnsafeSupplier) {

		_templateKeySupplier = () -> {
			try {
				return templateKeyUnsafeSupplier.get();
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
	protected String templateKey;

	@JsonIgnore
	private Supplier<String> _templateKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("templateType")
	@Valid
	public TemplateType getTemplateType() {
		if (_templateTypeSupplier != null) {
			templateType = _templateTypeSupplier.get();

			_templateTypeSupplier = null;
		}

		return templateType;
	}

	@JsonIgnore
	public String getTemplateTypeAsString() {
		TemplateType templateType = getTemplateType();

		if (templateType == null) {
			return null;
		}

		return templateType.toString();
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;

		_templateTypeSupplier = null;
	}

	@JsonIgnore
	public void setTemplateType(
		UnsafeSupplier<TemplateType, Exception> templateTypeUnsafeSupplier) {

		_templateTypeSupplier = () -> {
			try {
				return templateTypeUnsafeSupplier.get();
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
	protected TemplateType templateType;

	@JsonIgnore
	private Supplier<TemplateType> _templateTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Site)) {
			return false;
		}

		Site site = (Site)object;

		return Objects.equals(toString(), site.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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

		String friendlyUrlPath = getFriendlyUrlPath();

		if (friendlyUrlPath != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(friendlyUrlPath));

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

		MembershipType membershipType = getMembershipType();

		if (membershipType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"membershipType\": ");

			sb.append("\"");

			sb.append(membershipType);

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

		String parentSiteKey = getParentSiteKey();

		if (parentSiteKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSiteKey\": ");

			sb.append("\"");

			sb.append(_escape(parentSiteKey));

			sb.append("\"");
		}

		String templateKey = getTemplateKey();

		if (templateKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateKey\": ");

			sb.append("\"");

			sb.append(_escape(templateKey));

			sb.append("\"");
		}

		TemplateType templateType = getTemplateType();

		if (templateType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateType\": ");

			sb.append("\"");

			sb.append(templateType);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.site.dto.v1_0.Site",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("MembershipType")
	public static enum MembershipType {

		OPEN("open"), PRIVATE("private"), RESTRICTED("restricted");

		@JsonCreator
		public static MembershipType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (MembershipType membershipType : values()) {
				if (Objects.equals(membershipType.getValue(), value)) {
					return membershipType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private MembershipType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("TemplateType")
	public static enum TemplateType {

		SITE_INITIALIZER("site-initializer"), SITE_TEMPLATE("site-template");

		@JsonCreator
		public static TemplateType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (TemplateType templateType : values()) {
				if (Objects.equals(templateType.getValue(), value)) {
					return templateType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private TemplateType(String value) {
			_value = value;
		}

		private final String _value;

	}

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