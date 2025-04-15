/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.dto.v1_0;

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
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName("Attribute")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Attribute")
public class Attribute implements Serializable {

	public static Attribute toDTO(String json) {
		return ObjectMapperUtil.readValue(Attribute.class, json);
	}

	public static Attribute unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Attribute.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A collection of suggested canonical values that MAY be used (e.g., \"work\" and \"home\")."
	)
	public String[] getCanonicalValues() {
		if (_canonicalValuesSupplier != null) {
			canonicalValues = _canonicalValuesSupplier.get();

			_canonicalValuesSupplier = null;
		}

		return canonicalValues;
	}

	public void setCanonicalValues(String[] canonicalValues) {
		this.canonicalValues = canonicalValues;

		_canonicalValuesSupplier = null;
	}

	@JsonIgnore
	public void setCanonicalValues(
		UnsafeSupplier<String[], Exception> canonicalValuesUnsafeSupplier) {

		_canonicalValuesSupplier = () -> {
			try {
				return canonicalValuesUnsafeSupplier.get();
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
		description = "A collection of suggested canonical values that MAY be used (e.g., \"work\" and \"home\")."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] canonicalValues;

	@JsonIgnore
	private Supplier<String[]> _canonicalValuesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A Boolean value that specifies whether or not a string attribute is case sensitive"
	)
	public Boolean getCaseExact() {
		if (_caseExactSupplier != null) {
			caseExact = _caseExactSupplier.get();

			_caseExactSupplier = null;
		}

		return caseExact;
	}

	public void setCaseExact(Boolean caseExact) {
		this.caseExact = caseExact;

		_caseExactSupplier = null;
	}

	@JsonIgnore
	public void setCaseExact(
		UnsafeSupplier<Boolean, Exception> caseExactUnsafeSupplier) {

		_caseExactSupplier = () -> {
			try {
				return caseExactUnsafeSupplier.get();
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
		description = "A Boolean value that specifies whether or not a string attribute is case sensitive"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean caseExact;

	@JsonIgnore
	private Supplier<Boolean> _caseExactSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The attribute's human-readable description.  When applicable, service providers MUST specify the description"
	)
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
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
		description = "The attribute's human-readable description.  When applicable, service providers MUST specify the description"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A Boolean value indicating the attribute's plurality."
	)
	public Boolean getMultiValued() {
		if (_multiValuedSupplier != null) {
			multiValued = _multiValuedSupplier.get();

			_multiValuedSupplier = null;
		}

		return multiValued;
	}

	public void setMultiValued(Boolean multiValued) {
		this.multiValued = multiValued;

		_multiValuedSupplier = null;
	}

	@JsonIgnore
	public void setMultiValued(
		UnsafeSupplier<Boolean, Exception> multiValuedUnsafeSupplier) {

		_multiValuedSupplier = () -> {
			try {
				return multiValuedUnsafeSupplier.get();
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
		description = "A Boolean value indicating the attribute's plurality."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean multiValued;

	@JsonIgnore
	private Supplier<Boolean> _multiValuedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A single keyword indicating the circumstances under which the value of the attribute can be (re)defined."
	)
	@JsonGetter("mutability")
	@Valid
	public Mutability getMutability() {
		if (_mutabilitySupplier != null) {
			mutability = _mutabilitySupplier.get();

			_mutabilitySupplier = null;
		}

		return mutability;
	}

	@JsonIgnore
	public String getMutabilityAsString() {
		Mutability mutability = getMutability();

		if (mutability == null) {
			return null;
		}

		return mutability.toString();
	}

	public void setMutability(Mutability mutability) {
		this.mutability = mutability;

		_mutabilitySupplier = null;
	}

	@JsonIgnore
	public void setMutability(
		UnsafeSupplier<Mutability, Exception> mutabilityUnsafeSupplier) {

		_mutabilitySupplier = () -> {
			try {
				return mutabilityUnsafeSupplier.get();
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
		description = "A single keyword indicating the circumstances under which the value of the attribute can be (re)defined."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Mutability mutability;

	@JsonIgnore
	private Supplier<Mutability> _mutabilitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The attribute's name."
	)
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

	@GraphQLField(description = "The attribute's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A multi-valued array of JSON strings that indicate the SCIM resource types that may be referenced"
	)
	public String[] getReferenceTypes() {
		if (_referenceTypesSupplier != null) {
			referenceTypes = _referenceTypesSupplier.get();

			_referenceTypesSupplier = null;
		}

		return referenceTypes;
	}

	public void setReferenceTypes(String[] referenceTypes) {
		this.referenceTypes = referenceTypes;

		_referenceTypesSupplier = null;
	}

	@JsonIgnore
	public void setReferenceTypes(
		UnsafeSupplier<String[], Exception> referenceTypesUnsafeSupplier) {

		_referenceTypesSupplier = () -> {
			try {
				return referenceTypesUnsafeSupplier.get();
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
		description = "A multi-valued array of JSON strings that indicate the SCIM resource types that may be referenced"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] referenceTypes;

	@JsonIgnore
	private Supplier<String[]> _referenceTypesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A Boolean value that specifies whether or not the attribute is required."
	)
	public Boolean getRequired() {
		if (_requiredSupplier != null) {
			required = _requiredSupplier.get();

			_requiredSupplier = null;
		}

		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;

		_requiredSupplier = null;
	}

	@JsonIgnore
	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		_requiredSupplier = () -> {
			try {
				return requiredUnsafeSupplier.get();
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
		description = "A Boolean value that specifies whether or not the attribute is required."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean required;

	@JsonIgnore
	private Supplier<Boolean> _requiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A single keyword that indicates when an attribute and associated values are returned in response to a GET request or in response to a PUT, POST, or PATCH request."
	)
	@JsonGetter("returned")
	@Valid
	public Returned getReturned() {
		if (_returnedSupplier != null) {
			returned = _returnedSupplier.get();

			_returnedSupplier = null;
		}

		return returned;
	}

	@JsonIgnore
	public String getReturnedAsString() {
		Returned returned = getReturned();

		if (returned == null) {
			return null;
		}

		return returned.toString();
	}

	public void setReturned(Returned returned) {
		this.returned = returned;

		_returnedSupplier = null;
	}

	@JsonIgnore
	public void setReturned(
		UnsafeSupplier<Returned, Exception> returnedUnsafeSupplier) {

		_returnedSupplier = () -> {
			try {
				return returnedUnsafeSupplier.get();
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
		description = "A single keyword that indicates when an attribute and associated values are returned in response to a GET request or in response to a PUT, POST, or PATCH request."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Returned returned;

	@JsonIgnore
	private Supplier<Returned> _returnedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Attribute[] getSubAttributes() {
		if (_subAttributesSupplier != null) {
			subAttributes = _subAttributesSupplier.get();

			_subAttributesSupplier = null;
		}

		return subAttributes;
	}

	public void setSubAttributes(Attribute[] subAttributes) {
		this.subAttributes = subAttributes;

		_subAttributesSupplier = null;
	}

	@JsonIgnore
	public void setSubAttributes(
		UnsafeSupplier<Attribute[], Exception> subAttributesUnsafeSupplier) {

		_subAttributesSupplier = () -> {
			try {
				return subAttributesUnsafeSupplier.get();
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
	protected Attribute[] subAttributes;

	@JsonIgnore
	private Supplier<Attribute[]> _subAttributesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The attribute's data type.  Valid values are \"string\", \"boolean\", \"decimal\", \"integer\", \"dateTime\", \"reference\", and \"complex\"."
	)
	@JsonGetter("type")
	@Valid
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

	@GraphQLField(
		description = "The attribute's data type.  Valid values are \"string\", \"boolean\", \"decimal\", \"integer\", \"dateTime\", \"reference\", and \"complex\"."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A single keyword value that specifies how the service provider enforces uniqueness of attribute values."
	)
	@JsonGetter("uniqueness")
	@Valid
	public Uniqueness getUniqueness() {
		if (_uniquenessSupplier != null) {
			uniqueness = _uniquenessSupplier.get();

			_uniquenessSupplier = null;
		}

		return uniqueness;
	}

	@JsonIgnore
	public String getUniquenessAsString() {
		Uniqueness uniqueness = getUniqueness();

		if (uniqueness == null) {
			return null;
		}

		return uniqueness.toString();
	}

	public void setUniqueness(Uniqueness uniqueness) {
		this.uniqueness = uniqueness;

		_uniquenessSupplier = null;
	}

	@JsonIgnore
	public void setUniqueness(
		UnsafeSupplier<Uniqueness, Exception> uniquenessUnsafeSupplier) {

		_uniquenessSupplier = () -> {
			try {
				return uniquenessUnsafeSupplier.get();
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
		description = "A single keyword value that specifies how the service provider enforces uniqueness of attribute values."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Uniqueness uniqueness;

	@JsonIgnore
	private Supplier<Uniqueness> _uniquenessSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Attribute)) {
			return false;
		}

		Attribute attribute = (Attribute)object;

		return Objects.equals(toString(), attribute.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String[] canonicalValues = getCanonicalValues();

		if (canonicalValues != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"canonicalValues\": ");

			sb.append("[");

			for (int i = 0; i < canonicalValues.length; i++) {
				sb.append("\"");

				sb.append(_escape(canonicalValues[i]));

				sb.append("\"");

				if ((i + 1) < canonicalValues.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean caseExact = getCaseExact();

		if (caseExact != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"caseExact\": ");

			sb.append(caseExact);
		}

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		Boolean multiValued = getMultiValued();

		if (multiValued != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multiValued\": ");

			sb.append(multiValued);
		}

		Mutability mutability = getMutability();

		if (mutability != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mutability\": ");

			sb.append("\"");

			sb.append(mutability);

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

		String[] referenceTypes = getReferenceTypes();

		if (referenceTypes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"referenceTypes\": ");

			sb.append("[");

			for (int i = 0; i < referenceTypes.length; i++) {
				sb.append("\"");

				sb.append(_escape(referenceTypes[i]));

				sb.append("\"");

				if ((i + 1) < referenceTypes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean required = getRequired();

		if (required != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(required);
		}

		Returned returned = getReturned();

		if (returned != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"returned\": ");

			sb.append("\"");

			sb.append(returned);

			sb.append("\"");
		}

		Attribute[] subAttributes = getSubAttributes();

		if (subAttributes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subAttributes\": ");

			sb.append("[");

			for (int i = 0; i < subAttributes.length; i++) {
				sb.append(String.valueOf(subAttributes[i]));

				if ((i + 1) < subAttributes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Uniqueness uniqueness = getUniqueness();

		if (uniqueness != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uniqueness\": ");

			sb.append("\"");

			sb.append(uniqueness);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.scim.rest.dto.v1_0.Attribute",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Mutability")
	public static enum Mutability {

		READ_ONLY("readOnly"), READ_WRITE("readWrite"), IMMUTABLE("immutable"),
		WRITE_ONLY("writeOnly");

		@JsonCreator
		public static Mutability create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Mutability mutability : values()) {
				if (Objects.equals(mutability.getValue(), value)) {
					return mutability;
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

		private Mutability(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Returned")
	public static enum Returned {

		ALWAYS("always"), NEVER("never"), DEFAULT("default"),
		REQUEST("request");

		@JsonCreator
		public static Returned create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Returned returned : values()) {
				if (Objects.equals(returned.getValue(), value)) {
					return returned;
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

		private Returned(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Type")
	public static enum Type {

		STRING("string"), BOOLEAN("boolean"), DECIMAL("decimal"),
		INTEGER("integer"), DATE_TIME("dateTime"), REFERENCE("reference"),
		COMPLEX("complex");

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

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Uniqueness")
	public static enum Uniqueness {

		NONE("none"), SERVER("server"), GLOBAL("global");

		@JsonCreator
		public static Uniqueness create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Uniqueness uniqueness : values()) {
				if (Objects.equals(uniqueness.getValue(), value)) {
					return uniqueness;
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

		private Uniqueness(String value) {
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