/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a control that selects items of a tree by external reference code. The most specific rule wins, in the order of an item list, a subtree list, the closest ancestor's subtree list, and the selectAll flag. An included list wins over the excluded one at the same level. The pages control takes the external reference codes of the Headless Admin Site API's site pages.",
	value = "PortletDataHandlerTreeSelection"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents a control that selects items of a tree by external reference code. The most specific rule wins, in the order of an item list, a subtree list, the closest ancestor's subtree list, and the selectAll flag. An included list wins over the excluded one at the same level. The pages control takes the external reference codes of the Headless Admin Site API's site pages."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PortletDataHandlerTreeSelection")
public class PortletDataHandlerTreeSelection
	extends PortletDataHandlerControl implements Serializable {

	public static PortletDataHandlerTreeSelection toDTO(String json) {
		return ObjectMapperUtil.readValue(
			PortletDataHandlerTreeSelection.class, json);
	}

	public static PortletDataHandlerTreeSelection unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PortletDataHandlerTreeSelection.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAdditionCount() {
		if (_additionCountSupplier != null) {
			additionCount = _additionCountSupplier.get();

			_additionCountSupplier = null;
		}

		return additionCount;
	}

	public void setAdditionCount(Long additionCount) {
		this.additionCount = additionCount;

		_additionCountSupplier = null;
	}

	@JsonIgnore
	public void setAdditionCount(
		UnsafeSupplier<Long, Exception> additionCountUnsafeSupplier) {

		_additionCountSupplier = () -> {
			try {
				return additionCountUnsafeSupplier.get();
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
	protected Long additionCount;

	@JsonIgnore
	private Supplier<Long> _additionCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getDeletionCount() {
		if (_deletionCountSupplier != null) {
			deletionCount = _deletionCountSupplier.get();

			_deletionCountSupplier = null;
		}

		return deletionCount;
	}

	public void setDeletionCount(Long deletionCount) {
		this.deletionCount = deletionCount;

		_deletionCountSupplier = null;
	}

	@JsonIgnore
	public void setDeletionCount(
		UnsafeSupplier<Long, Exception> deletionCountUnsafeSupplier) {

		_deletionCountSupplier = () -> {
			try {
				return deletionCountUnsafeSupplier.get();
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
	protected Long deletionCount;

	@JsonIgnore
	private Supplier<Long> _deletionCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference codes of the items to exclude."
	)
	public String[] getExcludedItemExternalReferenceCodes() {
		if (_excludedItemExternalReferenceCodesSupplier != null) {
			excludedItemExternalReferenceCodes =
				_excludedItemExternalReferenceCodesSupplier.get();

			_excludedItemExternalReferenceCodesSupplier = null;
		}

		return excludedItemExternalReferenceCodes;
	}

	public void setExcludedItemExternalReferenceCodes(
		String[] excludedItemExternalReferenceCodes) {

		this.excludedItemExternalReferenceCodes =
			excludedItemExternalReferenceCodes;

		_excludedItemExternalReferenceCodesSupplier = null;
	}

	@JsonIgnore
	public void setExcludedItemExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			excludedItemExternalReferenceCodesUnsafeSupplier) {

		_excludedItemExternalReferenceCodesSupplier = () -> {
			try {
				return excludedItemExternalReferenceCodesUnsafeSupplier.get();
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
		description = "The external reference codes of the items to exclude."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] excludedItemExternalReferenceCodes;

	@JsonIgnore
	private Supplier<String[]> _excludedItemExternalReferenceCodesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference codes of the items to exclude with their descendants."
	)
	public String[] getExcludedSubtreeExternalReferenceCodes() {
		if (_excludedSubtreeExternalReferenceCodesSupplier != null) {
			excludedSubtreeExternalReferenceCodes =
				_excludedSubtreeExternalReferenceCodesSupplier.get();

			_excludedSubtreeExternalReferenceCodesSupplier = null;
		}

		return excludedSubtreeExternalReferenceCodes;
	}

	public void setExcludedSubtreeExternalReferenceCodes(
		String[] excludedSubtreeExternalReferenceCodes) {

		this.excludedSubtreeExternalReferenceCodes =
			excludedSubtreeExternalReferenceCodes;

		_excludedSubtreeExternalReferenceCodesSupplier = null;
	}

	@JsonIgnore
	public void setExcludedSubtreeExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			excludedSubtreeExternalReferenceCodesUnsafeSupplier) {

		_excludedSubtreeExternalReferenceCodesSupplier = () -> {
			try {
				return excludedSubtreeExternalReferenceCodesUnsafeSupplier.
					get();
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
		description = "The external reference codes of the items to exclude with their descendants."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] excludedSubtreeExternalReferenceCodes;

	@JsonIgnore
	private Supplier<String[]> _excludedSubtreeExternalReferenceCodesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference codes of the items to include."
	)
	public String[] getItemExternalReferenceCodes() {
		if (_itemExternalReferenceCodesSupplier != null) {
			itemExternalReferenceCodes =
				_itemExternalReferenceCodesSupplier.get();

			_itemExternalReferenceCodesSupplier = null;
		}

		return itemExternalReferenceCodes;
	}

	public void setItemExternalReferenceCodes(
		String[] itemExternalReferenceCodes) {

		this.itemExternalReferenceCodes = itemExternalReferenceCodes;

		_itemExternalReferenceCodesSupplier = null;
	}

	@JsonIgnore
	public void setItemExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			itemExternalReferenceCodesUnsafeSupplier) {

		_itemExternalReferenceCodesSupplier = () -> {
			try {
				return itemExternalReferenceCodesUnsafeSupplier.get();
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
		description = "The external reference codes of the items to include."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] itemExternalReferenceCodes;

	@JsonIgnore
	private Supplier<String[]> _itemExternalReferenceCodesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether every item is selected before the lists apply."
	)
	public Boolean getSelectAll() {
		if (_selectAllSupplier != null) {
			selectAll = _selectAllSupplier.get();

			_selectAllSupplier = null;
		}

		return selectAll;
	}

	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;

		_selectAllSupplier = null;
	}

	@JsonIgnore
	public void setSelectAll(
		UnsafeSupplier<Boolean, Exception> selectAllUnsafeSupplier) {

		_selectAllSupplier = () -> {
			try {
				return selectAllUnsafeSupplier.get();
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
		description = "A flag that indicates whether every item is selected before the lists apply."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean selectAll;

	@JsonIgnore
	private Supplier<Boolean> _selectAllSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the control's items are processed."
	)
	public Boolean getSelected() {
		if (_selectedSupplier != null) {
			selected = _selectedSupplier.get();

			_selectedSupplier = null;
		}

		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;

		_selectedSupplier = null;
	}

	@JsonIgnore
	public void setSelected(
		UnsafeSupplier<Boolean, Exception> selectedUnsafeSupplier) {

		_selectedSupplier = () -> {
			try {
				return selectedUnsafeSupplier.get();
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
		description = "A flag that indicates whether the control's items are processed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean selected;

	@JsonIgnore
	private Supplier<Boolean> _selectedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference codes of the items to include with their descendants."
	)
	public String[] getSubtreeExternalReferenceCodes() {
		if (_subtreeExternalReferenceCodesSupplier != null) {
			subtreeExternalReferenceCodes =
				_subtreeExternalReferenceCodesSupplier.get();

			_subtreeExternalReferenceCodesSupplier = null;
		}

		return subtreeExternalReferenceCodes;
	}

	public void setSubtreeExternalReferenceCodes(
		String[] subtreeExternalReferenceCodes) {

		this.subtreeExternalReferenceCodes = subtreeExternalReferenceCodes;

		_subtreeExternalReferenceCodesSupplier = null;
	}

	@JsonIgnore
	public void setSubtreeExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			subtreeExternalReferenceCodesUnsafeSupplier) {

		_subtreeExternalReferenceCodesSupplier = () -> {
			try {
				return subtreeExternalReferenceCodesUnsafeSupplier.get();
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
		description = "The external reference codes of the items to include with their descendants."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] subtreeExternalReferenceCodes;

	@JsonIgnore
	private Supplier<String[]> _subtreeExternalReferenceCodesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortletDataHandlerTreeSelection)) {
			return false;
		}

		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
			(PortletDataHandlerTreeSelection)object;

		return Objects.equals(
			toString(), portletDataHandlerTreeSelection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long additionCount = getAdditionCount();

		if (additionCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(additionCount);
		}

		Long deletionCount = getDeletionCount();

		if (deletionCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(deletionCount);
		}

		String[] excludedItemExternalReferenceCodes =
			getExcludedItemExternalReferenceCodes();

		if (excludedItemExternalReferenceCodes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"excludedItemExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0; i < excludedItemExternalReferenceCodes.length;
				 i++) {

				sb.append("\"");

				sb.append(_escape(excludedItemExternalReferenceCodes[i]));

				sb.append("\"");

				if ((i + 1) < excludedItemExternalReferenceCodes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] excludedSubtreeExternalReferenceCodes =
			getExcludedSubtreeExternalReferenceCodes();

		if (excludedSubtreeExternalReferenceCodes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"excludedSubtreeExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0; i < excludedSubtreeExternalReferenceCodes.length;
				 i++) {

				sb.append("\"");

				sb.append(_escape(excludedSubtreeExternalReferenceCodes[i]));

				sb.append("\"");

				if ((i + 1) < excludedSubtreeExternalReferenceCodes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] itemExternalReferenceCodes = getItemExternalReferenceCodes();

		if (itemExternalReferenceCodes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0; i < itemExternalReferenceCodes.length; i++) {
				sb.append("\"");

				sb.append(_escape(itemExternalReferenceCodes[i]));

				sb.append("\"");

				if ((i + 1) < itemExternalReferenceCodes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean selectAll = getSelectAll();

		if (selectAll != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(selectAll);
		}

		Boolean selected = getSelected();

		if (selected != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selected\": ");

			sb.append(selected);
		}

		String[] subtreeExternalReferenceCodes =
			getSubtreeExternalReferenceCodes();

		if (subtreeExternalReferenceCodes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtreeExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0; i < subtreeExternalReferenceCodes.length; i++) {
				sb.append("\"");

				sb.append(_escape(subtreeExternalReferenceCodes[i]));

				sb.append("\"");

				if ((i + 1) < subtreeExternalReferenceCodes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerTreeSelection",
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
// LIFERAY-REST-BUILDER-HASH:1905086326