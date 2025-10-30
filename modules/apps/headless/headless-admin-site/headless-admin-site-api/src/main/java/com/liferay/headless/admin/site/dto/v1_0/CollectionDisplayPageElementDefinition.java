/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The page element definition of a Collection.",
	value = "CollectionDisplayPageElementDefinition"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "CollectionDisplayPageElementDefinition")
public class CollectionDisplayPageElementDefinition
	extends PageElementDefinition implements Serializable {

	public static CollectionDisplayPageElementDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(
			CollectionDisplayPageElementDefinition.class, json);
	}

	public static CollectionDisplayPageElementDefinition unsafeToDTO(
		String json) {

		return ObjectMapperUtil.unsafeReadValue(
			CollectionDisplayPageElementDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public CollectionDisplayListStyle getCollectionDisplayListStyle() {
		if (_collectionDisplayListStyleSupplier != null) {
			collectionDisplayListStyle =
				_collectionDisplayListStyleSupplier.get();

			_collectionDisplayListStyleSupplier = null;
		}

		return collectionDisplayListStyle;
	}

	public void setCollectionDisplayListStyle(
		CollectionDisplayListStyle collectionDisplayListStyle) {

		this.collectionDisplayListStyle = collectionDisplayListStyle;

		_collectionDisplayListStyleSupplier = null;
	}

	@JsonIgnore
	public void setCollectionDisplayListStyle(
		UnsafeSupplier<CollectionDisplayListStyle, Exception>
			collectionDisplayListStyleUnsafeSupplier) {

		_collectionDisplayListStyleSupplier = () -> {
			try {
				return collectionDisplayListStyleUnsafeSupplier.get();
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
	protected CollectionDisplayListStyle collectionDisplayListStyle;

	@JsonIgnore
	private Supplier<CollectionDisplayListStyle>
		_collectionDisplayListStyleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of viewports of the collection display page element."
	)
	@Valid
	public CollectionDisplayViewport[] getCollectionDisplayViewports() {
		if (_collectionDisplayViewportsSupplier != null) {
			collectionDisplayViewports =
				_collectionDisplayViewportsSupplier.get();

			_collectionDisplayViewportsSupplier = null;
		}

		return collectionDisplayViewports;
	}

	public void setCollectionDisplayViewports(
		CollectionDisplayViewport[] collectionDisplayViewports) {

		this.collectionDisplayViewports = collectionDisplayViewports;

		_collectionDisplayViewportsSupplier = null;
	}

	@JsonIgnore
	public void setCollectionDisplayViewports(
		UnsafeSupplier<CollectionDisplayViewport[], Exception>
			collectionDisplayViewportsUnsafeSupplier) {

		_collectionDisplayViewportsSupplier = () -> {
			try {
				return collectionDisplayViewportsUnsafeSupplier.get();
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
		description = "A list of viewports of the collection display page element."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected CollectionDisplayViewport[] collectionDisplayViewports;

	@JsonIgnore
	private Supplier<CollectionDisplayViewport[]>
		_collectionDisplayViewportsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public CollectionSettings getCollectionSettings() {
		if (_collectionSettingsSupplier != null) {
			collectionSettings = _collectionSettingsSupplier.get();

			_collectionSettingsSupplier = null;
		}

		return collectionSettings;
	}

	public void setCollectionSettings(CollectionSettings collectionSettings) {
		this.collectionSettings = collectionSettings;

		_collectionSettingsSupplier = null;
	}

	@JsonIgnore
	public void setCollectionSettings(
		UnsafeSupplier<CollectionSettings, Exception>
			collectionSettingsUnsafeSupplier) {

		_collectionSettingsSupplier = () -> {
			try {
				return collectionSettingsUnsafeSupplier.get();
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
	protected CollectionSettings collectionSettings;

	@JsonIgnore
	private Supplier<CollectionSettings> _collectionSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to show all items when pagination is disabled."
	)
	public Boolean getDisplayAllItems() {
		if (_displayAllItemsSupplier != null) {
			displayAllItems = _displayAllItemsSupplier.get();

			_displayAllItemsSupplier = null;
		}

		return displayAllItems;
	}

	public void setDisplayAllItems(Boolean displayAllItems) {
		this.displayAllItems = displayAllItems;

		_displayAllItemsSupplier = null;
	}

	@JsonIgnore
	public void setDisplayAllItems(
		UnsafeSupplier<Boolean, Exception> displayAllItemsUnsafeSupplier) {

		_displayAllItemsSupplier = () -> {
			try {
				return displayAllItemsUnsafeSupplier.get();
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
		description = "Whether to show all items when pagination is disabled."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean displayAllItems;

	@JsonIgnore
	private Supplier<Boolean> _displayAllItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to show all pages when pagination is enabled."
	)
	public Boolean getDisplayAllPages() {
		if (_displayAllPagesSupplier != null) {
			displayAllPages = _displayAllPagesSupplier.get();

			_displayAllPagesSupplier = null;
		}

		return displayAllPages;
	}

	public void setDisplayAllPages(Boolean displayAllPages) {
		this.displayAllPages = displayAllPages;

		_displayAllPagesSupplier = null;
	}

	@JsonIgnore
	public void setDisplayAllPages(
		UnsafeSupplier<Boolean, Exception> displayAllPagesUnsafeSupplier) {

		_displayAllPagesSupplier = () -> {
			try {
				return displayAllPagesUnsafeSupplier.get();
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
		description = "Whether to show all pages when pagination is enabled."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean displayAllPages;

	@JsonIgnore
	private Supplier<Boolean> _displayAllPagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public EmptyCollectionConfig getEmptyCollectionConfig() {
		if (_emptyCollectionConfigSupplier != null) {
			emptyCollectionConfig = _emptyCollectionConfigSupplier.get();

			_emptyCollectionConfigSupplier = null;
		}

		return emptyCollectionConfig;
	}

	public void setEmptyCollectionConfig(
		EmptyCollectionConfig emptyCollectionConfig) {

		this.emptyCollectionConfig = emptyCollectionConfig;

		_emptyCollectionConfigSupplier = null;
	}

	@JsonIgnore
	public void setEmptyCollectionConfig(
		UnsafeSupplier<EmptyCollectionConfig, Exception>
			emptyCollectionConfigUnsafeSupplier) {

		_emptyCollectionConfigSupplier = () -> {
			try {
				return emptyCollectionConfigUnsafeSupplier.get();
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
	protected EmptyCollectionConfig emptyCollectionConfig;

	@JsonIgnore
	private Supplier<EmptyCollectionConfig> _emptyCollectionConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Specifies if the collection display is hidden to the user."
	)
	public Boolean getHidden() {
		if (_hiddenSupplier != null) {
			hidden = _hiddenSupplier.get();

			_hiddenSupplier = null;
		}

		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;

		_hiddenSupplier = null;
	}

	@JsonIgnore
	public void setHidden(
		UnsafeSupplier<Boolean, Exception> hiddenUnsafeSupplier) {

		_hiddenSupplier = () -> {
			try {
				return hiddenUnsafeSupplier.get();
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
		description = "Specifies if the collection display is hidden to the user."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean hidden;

	@JsonIgnore
	private Supplier<Boolean> _hiddenSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The custom name of a collection display page element."
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

	@GraphQLField(
		description = "The custom name of a collection display page element."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The maximum number of items to display in the collection display page element when pagination is disabled."
	)
	public Integer getNumberOfItems() {
		if (_numberOfItemsSupplier != null) {
			numberOfItems = _numberOfItemsSupplier.get();

			_numberOfItemsSupplier = null;
		}

		return numberOfItems;
	}

	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;

		_numberOfItemsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfItems(
		UnsafeSupplier<Integer, Exception> numberOfItemsUnsafeSupplier) {

		_numberOfItemsSupplier = () -> {
			try {
				return numberOfItemsUnsafeSupplier.get();
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
		description = "The maximum number of items to display in the collection display page element when pagination is disabled."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfItems;

	@JsonIgnore
	private Supplier<Integer> _numberOfItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The maximum number of items per page to display in the collection display page element when pagination is enabled."
	)
	public Integer getNumberOfItemsPerPage() {
		if (_numberOfItemsPerPageSupplier != null) {
			numberOfItemsPerPage = _numberOfItemsPerPageSupplier.get();

			_numberOfItemsPerPageSupplier = null;
		}

		return numberOfItemsPerPage;
	}

	public void setNumberOfItemsPerPage(Integer numberOfItemsPerPage) {
		this.numberOfItemsPerPage = numberOfItemsPerPage;

		_numberOfItemsPerPageSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfItemsPerPage(
		UnsafeSupplier<Integer, Exception> numberOfItemsPerPageUnsafeSupplier) {

		_numberOfItemsPerPageSupplier = () -> {
			try {
				return numberOfItemsPerPageUnsafeSupplier.get();
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
		description = "The maximum number of items per page to display in the collection display page element when pagination is enabled."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfItemsPerPage;

	@JsonIgnore
	private Supplier<Integer> _numberOfItemsPerPageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The maximum number of pages to display in the collection display page element when pagination is enabled."
	)
	public Integer getNumberOfPages() {
		if (_numberOfPagesSupplier != null) {
			numberOfPages = _numberOfPagesSupplier.get();

			_numberOfPagesSupplier = null;
		}

		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;

		_numberOfPagesSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfPages(
		UnsafeSupplier<Integer, Exception> numberOfPagesUnsafeSupplier) {

		_numberOfPagesSupplier = () -> {
			try {
				return numberOfPagesUnsafeSupplier.get();
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
		description = "The maximum number of pages to display in the collection display page element when pagination is enabled."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfPages;

	@JsonIgnore
	private Supplier<Integer> _numberOfPagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of pagination."
	)
	@JsonGetter("paginationType")
	@Valid
	public PaginationType getPaginationType() {
		if (_paginationTypeSupplier != null) {
			paginationType = _paginationTypeSupplier.get();

			_paginationTypeSupplier = null;
		}

		return paginationType;
	}

	@JsonIgnore
	public String getPaginationTypeAsString() {
		PaginationType paginationType = getPaginationType();

		if (paginationType == null) {
			return null;
		}

		return paginationType.toString();
	}

	public void setPaginationType(PaginationType paginationType) {
		this.paginationType = paginationType;

		_paginationTypeSupplier = null;
	}

	@JsonIgnore
	public void setPaginationType(
		UnsafeSupplier<PaginationType, Exception>
			paginationTypeUnsafeSupplier) {

		_paginationTypeSupplier = () -> {
			try {
				return paginationTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of pagination.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PaginationType paginationType;

	@JsonIgnore
	private Supplier<PaginationType> _paginationTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollectionDisplayPageElementDefinition)) {
			return false;
		}

		CollectionDisplayPageElementDefinition
			collectionDisplayPageElementDefinition =
				(CollectionDisplayPageElementDefinition)object;

		return Objects.equals(
			toString(), collectionDisplayPageElementDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		CollectionDisplayListStyle collectionDisplayListStyle =
			getCollectionDisplayListStyle();

		if (collectionDisplayListStyle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionDisplayListStyle\": ");

			sb.append(String.valueOf(collectionDisplayListStyle));
		}

		CollectionDisplayViewport[] collectionDisplayViewports =
			getCollectionDisplayViewports();

		if (collectionDisplayViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionDisplayViewports\": ");

			sb.append("[");

			for (int i = 0; i < collectionDisplayViewports.length; i++) {
				sb.append(String.valueOf(collectionDisplayViewports[i]));

				if ((i + 1) < collectionDisplayViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		CollectionSettings collectionSettings = getCollectionSettings();

		if (collectionSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionSettings\": ");

			sb.append(String.valueOf(collectionSettings));
		}

		Boolean displayAllItems = getDisplayAllItems();

		if (displayAllItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayAllItems\": ");

			sb.append(displayAllItems);
		}

		Boolean displayAllPages = getDisplayAllPages();

		if (displayAllPages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayAllPages\": ");

			sb.append(displayAllPages);
		}

		EmptyCollectionConfig emptyCollectionConfig =
			getEmptyCollectionConfig();

		if (emptyCollectionConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emptyCollectionConfig\": ");

			sb.append(String.valueOf(emptyCollectionConfig));
		}

		Boolean hidden = getHidden();

		if (hidden != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hidden\": ");

			sb.append(hidden);
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

		Integer numberOfItems = getNumberOfItems();

		if (numberOfItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItems\": ");

			sb.append(numberOfItems);
		}

		Integer numberOfItemsPerPage = getNumberOfItemsPerPage();

		if (numberOfItemsPerPage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItemsPerPage\": ");

			sb.append(numberOfItemsPerPage);
		}

		Integer numberOfPages = getNumberOfPages();

		if (numberOfPages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfPages\": ");

			sb.append(numberOfPages);
		}

		PaginationType paginationType = getPaginationType();

		if (paginationType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paginationType\": ");

			sb.append("\"");
			sb.append(paginationType);
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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.CollectionDisplayPageElementDefinition",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("PaginationType")
	public static enum PaginationType {

		NONE("None"), NUMERIC("Numeric"), SIMPLE("Simple");

		@JsonCreator
		public static PaginationType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (PaginationType paginationType : values()) {
				if (Objects.equals(paginationType.getValue(), value)) {
					return paginationType;
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

		private PaginationType(String value) {
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