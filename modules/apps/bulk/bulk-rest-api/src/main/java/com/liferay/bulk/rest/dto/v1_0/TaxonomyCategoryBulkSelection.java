/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
@GraphQLName("TaxonomyCategoryBulkSelection")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TaxonomyCategoryBulkSelection")
public class TaxonomyCategoryBulkSelection implements Serializable {

	public static TaxonomyCategoryBulkSelection toDTO(String json) {
		return ObjectMapperUtil.readValue(
			TaxonomyCategoryBulkSelection.class, json);
	}

	public static TaxonomyCategoryBulkSelection unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			TaxonomyCategoryBulkSelection.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public DocumentBulkSelection getDocumentBulkSelection() {
		if (_documentBulkSelectionSupplier != null) {
			documentBulkSelection = _documentBulkSelectionSupplier.get();

			_documentBulkSelectionSupplier = null;
		}

		return documentBulkSelection;
	}

	public void setDocumentBulkSelection(
		DocumentBulkSelection documentBulkSelection) {

		this.documentBulkSelection = documentBulkSelection;

		_documentBulkSelectionSupplier = null;
	}

	@JsonIgnore
	public void setDocumentBulkSelection(
		UnsafeSupplier<DocumentBulkSelection, Exception>
			documentBulkSelectionUnsafeSupplier) {

		_documentBulkSelectionSupplier = () -> {
			try {
				return documentBulkSelectionUnsafeSupplier.get();
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
	protected DocumentBulkSelection documentBulkSelection;

	@JsonIgnore
	private Supplier<DocumentBulkSelection> _documentBulkSelectionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getTaxonomyCategoryIdsToAdd() {
		if (_taxonomyCategoryIdsToAddSupplier != null) {
			taxonomyCategoryIdsToAdd = _taxonomyCategoryIdsToAddSupplier.get();

			_taxonomyCategoryIdsToAddSupplier = null;
		}

		return taxonomyCategoryIdsToAdd;
	}

	public void setTaxonomyCategoryIdsToAdd(Long[] taxonomyCategoryIdsToAdd) {
		this.taxonomyCategoryIdsToAdd = taxonomyCategoryIdsToAdd;

		_taxonomyCategoryIdsToAddSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryIdsToAdd(
		UnsafeSupplier<Long[], Exception>
			taxonomyCategoryIdsToAddUnsafeSupplier) {

		_taxonomyCategoryIdsToAddSupplier = () -> {
			try {
				return taxonomyCategoryIdsToAddUnsafeSupplier.get();
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
	protected Long[] taxonomyCategoryIdsToAdd;

	@JsonIgnore
	private Supplier<Long[]> _taxonomyCategoryIdsToAddSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getTaxonomyCategoryIdsToRemove() {
		if (_taxonomyCategoryIdsToRemoveSupplier != null) {
			taxonomyCategoryIdsToRemove =
				_taxonomyCategoryIdsToRemoveSupplier.get();

			_taxonomyCategoryIdsToRemoveSupplier = null;
		}

		return taxonomyCategoryIdsToRemove;
	}

	public void setTaxonomyCategoryIdsToRemove(
		Long[] taxonomyCategoryIdsToRemove) {

		this.taxonomyCategoryIdsToRemove = taxonomyCategoryIdsToRemove;

		_taxonomyCategoryIdsToRemoveSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryIdsToRemove(
		UnsafeSupplier<Long[], Exception>
			taxonomyCategoryIdsToRemoveUnsafeSupplier) {

		_taxonomyCategoryIdsToRemoveSupplier = () -> {
			try {
				return taxonomyCategoryIdsToRemoveUnsafeSupplier.get();
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
	protected Long[] taxonomyCategoryIdsToRemove;

	@JsonIgnore
	private Supplier<Long[]> _taxonomyCategoryIdsToRemoveSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TaxonomyCategoryBulkSelection)) {
			return false;
		}

		TaxonomyCategoryBulkSelection taxonomyCategoryBulkSelection =
			(TaxonomyCategoryBulkSelection)object;

		return Objects.equals(
			toString(), taxonomyCategoryBulkSelection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DocumentBulkSelection documentBulkSelection =
			getDocumentBulkSelection();

		if (documentBulkSelection != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentBulkSelection\": ");

			sb.append(String.valueOf(documentBulkSelection));
		}

		Long[] taxonomyCategoryIdsToAdd = getTaxonomyCategoryIdsToAdd();

		if (taxonomyCategoryIdsToAdd != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIdsToAdd\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryIdsToAdd.length; i++) {
				sb.append(taxonomyCategoryIdsToAdd[i]);

				if ((i + 1) < taxonomyCategoryIdsToAdd.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long[] taxonomyCategoryIdsToRemove = getTaxonomyCategoryIdsToRemove();

		if (taxonomyCategoryIdsToRemove != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIdsToRemove\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryIdsToRemove.length; i++) {
				sb.append(taxonomyCategoryIdsToRemove[i]);

				if ((i + 1) < taxonomyCategoryIdsToRemove.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.bulk.rest.dto.v1_0.TaxonomyCategoryBulkSelection",
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