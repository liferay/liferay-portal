/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.AttributeSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class Attribute implements Cloneable, Serializable {

	public static Attribute toDTO(String json) {
		return AttributeSerDes.toDTO(json);
	}

	public String[] getCanonicalValues() {
		return canonicalValues;
	}

	public void setCanonicalValues(String[] canonicalValues) {
		this.canonicalValues = canonicalValues;
	}

	public void setCanonicalValues(
		UnsafeSupplier<String[], Exception> canonicalValuesUnsafeSupplier) {

		try {
			canonicalValues = canonicalValuesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] canonicalValues;

	public Boolean getCaseExact() {
		return caseExact;
	}

	public void setCaseExact(Boolean caseExact) {
		this.caseExact = caseExact;
	}

	public void setCaseExact(
		UnsafeSupplier<Boolean, Exception> caseExactUnsafeSupplier) {

		try {
			caseExact = caseExactUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean caseExact;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public Boolean getMultiValued() {
		return multiValued;
	}

	public void setMultiValued(Boolean multiValued) {
		this.multiValued = multiValued;
	}

	public void setMultiValued(
		UnsafeSupplier<Boolean, Exception> multiValuedUnsafeSupplier) {

		try {
			multiValued = multiValuedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean multiValued;

	public Mutability getMutability() {
		return mutability;
	}

	public String getMutabilityAsString() {
		if (mutability == null) {
			return null;
		}

		return mutability.toString();
	}

	public void setMutability(Mutability mutability) {
		this.mutability = mutability;
	}

	public void setMutability(
		UnsafeSupplier<Mutability, Exception> mutabilityUnsafeSupplier) {

		try {
			mutability = mutabilityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Mutability mutability;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public String[] getReferenceTypes() {
		return referenceTypes;
	}

	public void setReferenceTypes(String[] referenceTypes) {
		this.referenceTypes = referenceTypes;
	}

	public void setReferenceTypes(
		UnsafeSupplier<String[], Exception> referenceTypesUnsafeSupplier) {

		try {
			referenceTypes = referenceTypesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] referenceTypes;

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		try {
			required = requiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean required;

	public Returned getReturned() {
		return returned;
	}

	public String getReturnedAsString() {
		if (returned == null) {
			return null;
		}

		return returned.toString();
	}

	public void setReturned(Returned returned) {
		this.returned = returned;
	}

	public void setReturned(
		UnsafeSupplier<Returned, Exception> returnedUnsafeSupplier) {

		try {
			returned = returnedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Returned returned;

	public Attribute[] getSubAttributes() {
		return subAttributes;
	}

	public void setSubAttributes(Attribute[] subAttributes) {
		this.subAttributes = subAttributes;
	}

	public void setSubAttributes(
		UnsafeSupplier<Attribute[], Exception> subAttributesUnsafeSupplier) {

		try {
			subAttributes = subAttributesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Attribute[] subAttributes;

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Type type;

	public Uniqueness getUniqueness() {
		return uniqueness;
	}

	public String getUniquenessAsString() {
		if (uniqueness == null) {
			return null;
		}

		return uniqueness.toString();
	}

	public void setUniqueness(Uniqueness uniqueness) {
		this.uniqueness = uniqueness;
	}

	public void setUniqueness(
		UnsafeSupplier<Uniqueness, Exception> uniquenessUnsafeSupplier) {

		try {
			uniqueness = uniquenessUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Uniqueness uniqueness;

	@Override
	public Attribute clone() throws CloneNotSupportedException {
		return (Attribute)super.clone();
	}

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
		return AttributeSerDes.toJSON(this);
	}

	public static enum Mutability {

		READ_ONLY("readOnly"), READ_WRITE("readWrite"), IMMUTABLE("immutable"),
		WRITE_ONLY("writeOnly");

		public static Mutability create(String value) {
			for (Mutability mutability : values()) {
				if (Objects.equals(mutability.getValue(), value) ||
					Objects.equals(mutability.name(), value)) {

					return mutability;
				}
			}

			return null;
		}

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

	public static enum Returned {

		ALWAYS("always"), NEVER("never"), DEFAULT("default"),
		REQUEST("request");

		public static Returned create(String value) {
			for (Returned returned : values()) {
				if (Objects.equals(returned.getValue(), value) ||
					Objects.equals(returned.name(), value)) {

					return returned;
				}
			}

			return null;
		}

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

	public static enum Type {

		STRING("string"), BOOLEAN("boolean"), DECIMAL("decimal"),
		INTEGER("integer"), DATE_TIME("dateTime"), REFERENCE("reference"),
		COMPLEX("complex");

		public static Type create(String value) {
			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value) ||
					Objects.equals(type.name(), value)) {

					return type;
				}
			}

			return null;
		}

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

	public static enum Uniqueness {

		NONE("none"), SERVER("server"), GLOBAL("global");

		public static Uniqueness create(String value) {
			for (Uniqueness uniqueness : values()) {
				if (Objects.equals(uniqueness.getValue(), value) ||
					Objects.equals(uniqueness.name(), value)) {

					return uniqueness;
				}
			}

			return null;
		}

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

}