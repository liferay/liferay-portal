/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.scope;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class Scope implements Cloneable, Serializable {

	public static Scope toDTO(String json) {
		ScopeJSONParser scopeJSONParser = new ScopeJSONParser();

		return scopeJSONParser.parseToDTO(json);
	}

	@Override
	public Scope clone() throws CloneNotSupportedException {
		return (Scope)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Scope)) {
			return false;
		}

		Scope scope = (Scope)object;

		return Objects.equals(toString(), scope.toString());
	}

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public String toString() {
		return ScopeJSONParser.toJSON(this);
	}

	public static enum Type {

		ASSET_LIBRARY("AssetLibrary"), SITE("Site"), SPACE("Space");

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

	protected String externalReferenceCode;
	protected String key;
	protected String label;
	protected Type type;

	private static class ScopeJSONParser extends BaseJSONParser<Scope> {

		public static String toJSON(Scope scope) {
			if (scope == null) {
				return "null";
			}

			StringBuilder sb = new StringBuilder();

			sb.append("{");

			if (scope.getExternalReferenceCode() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"externalReferenceCode\": \"");
				sb.append(_escape(scope.getExternalReferenceCode()));
				sb.append("\"");
			}

			if (scope.getKey() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"key\": \"");
				sb.append(_escape(scope.getKey()));
				sb.append("\"");
			}

			if (scope.getLabel() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"label\": \"");
				sb.append(_escape(scope.getLabel()));
				sb.append("\"");
			}

			if (scope.getType() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"type\": \"");
				sb.append(scope.getType());
				sb.append("\"");
			}

			sb.append("}");

			return sb.toString();
		}

		@Override
		protected Scope createDTO() {
			return new Scope();
		}

		@Override
		protected Scope[] createDTOArray(int size) {
			return new Scope[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Scope scope, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					scope.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					scope.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					scope.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					scope.setType(
						Scope.Type.create((String)jsonParserFieldValue));
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

		private static String _escape(Object object) {
			String string = String.valueOf(object);

			for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
				string = string.replace(strings[0], strings[1]);
			}

			return string;
		}

	}

}
// LIFERAY-REST-BUILDER-HASH:98987299