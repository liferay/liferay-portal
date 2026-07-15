/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.scope;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.group.capability.GroupCapabilityUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Daniel Raposo
 */
@GraphQLName(
	description = "Represents the scope for a given entity.", value = "Scope"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Scope")
public class Scope implements Serializable {

	public static Scope of(Group group, Locale locale) {
		if ((group == null) || !GroupCapabilityUtil.isSupportsScope(group)) {
			return null;
		}

		return new Scope() {
			{
				setExternalReferenceCode(group::getExternalReferenceCode);
				setKey(
					() -> NestedFieldsSupplier.supply(
						"scope.key", nestedField -> group.getGroupKey()));
				setLabel(
					() -> NestedFieldsSupplier.supply(
						"scope.label",
						nestedField -> _getGroupName(group, locale)));
				setType(() -> _getGroupType(group));
			}
		};
	}

	public static Scope of(long groupId, Locale locale) throws PortalException {
		if (groupId == 0) {
			return null;
		}

		return of(GroupLocalServiceUtil.getGroup(groupId), locale);
	}

	public static Scope ofReference(
		String scopeExternalReferenceCode, Type scopeType) {

		return new Scope() {
			{
				setExternalReferenceCode(() -> scopeExternalReferenceCode);
				setType(() -> scopeType);
			}
		};
	}

	public static Scope toDTO(String json) {
		return ObjectMapperUtil.readValue(Scope.class, json);
	}

	public static Scope unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Scope.class, json);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Scope scope)) {
			return false;
		}

		if (Objects.equals(
				getExternalReferenceCode(), scope.getExternalReferenceCode()) &&
			Objects.equals(getKey(), scope.getKey()) &&
			Objects.equals(getLabel(), scope.getLabel()) &&
			Objects.equals(getType(), scope.getType())) {

			return true;
		}

		return false;
	}

	@Schema(description = "The scope's external reference code.")
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	@Schema(description = "The scope's identifier.")
	public String getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	@Schema(description = "The scope's name.")
	public String getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	@Schema(description = "The scope's type.")
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getExternalReferenceCode(), getType());
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

	public void setLabel(String label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		_labelSupplier = () -> {
			try {
				return labelUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
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

	public String toString() {
		StringBundler sb = new StringBundler(22);

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

		String label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(label));

			sb.append("\"");
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(type);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@Schema(
		accessMode = Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.vulcan.scope.Scope",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		ASSET_LIBRARY("AssetLibrary"), SITE("Site"), SPACE("Space");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
				}
			}

			throw new IllegalArgumentException("Invalid value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLField(description = "The scope's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@GraphQLField(description = "The scope's identifier.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String key;

	@GraphQLField(description = "The scope's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String label;

	@GraphQLField(description = "The scope's type.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	private static String _getGroupName(Group group, Locale locale) {
		try {
			String descriptiveName = group.getDescriptiveName(locale);

			if (Validator.isNotNull(descriptiveName)) {
				return descriptiveName;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return group.getName(locale);
	}

	private static Scope.Type _getGroupType(Group group) {
		if (group.isDepot()) {
			return Scope.Type.ASSET_LIBRARY;
		}
		else if (group.isSite()) {
			return Scope.Type.SITE;
		}
		else if (group.isCMS()) {
			return Scope.Type.SPACE;
		}

		return null;
	}

	private Scope() {
	}

	private String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private static final Log _log = LogFactoryUtil.getLog(Scope.class);

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@JsonIgnore
	private Supplier<String> _labelSupplier;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

}