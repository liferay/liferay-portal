/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a definition of a Page Collection.",
	value = "PageCollectionDefinition"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageCollectionDefinition")
public class PageCollectionDefinition implements Serializable {

	public static PageCollectionDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(PageCollectionDefinition.class, json);
	}

	public static PageCollectionDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PageCollectionDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public CollectionConfig getCollectionConfig() {
		if (_collectionConfigSupplier != null) {
			collectionConfig = _collectionConfigSupplier.get();

			_collectionConfigSupplier = null;
		}

		return collectionConfig;
	}

	public void setCollectionConfig(CollectionConfig collectionConfig) {
		this.collectionConfig = collectionConfig;

		_collectionConfigSupplier = null;
	}

	@JsonIgnore
	public void setCollectionConfig(
		UnsafeSupplier<CollectionConfig, Exception>
			collectionConfigUnsafeSupplier) {

		_collectionConfigSupplier = () -> {
			try {
				return collectionConfigUnsafeSupplier.get();
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
	protected CollectionConfig collectionConfig;

	@JsonIgnore
	private Supplier<CollectionConfig> _collectionConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of viewports of the page collection."
	)
	@Valid
	public CollectionViewport[] getCollectionViewports() {
		if (_collectionViewportsSupplier != null) {
			collectionViewports = _collectionViewportsSupplier.get();

			_collectionViewportsSupplier = null;
		}

		return collectionViewports;
	}

	public void setCollectionViewports(
		CollectionViewport[] collectionViewports) {

		this.collectionViewports = collectionViewports;

		_collectionViewportsSupplier = null;
	}

	@JsonIgnore
	public void setCollectionViewports(
		UnsafeSupplier<CollectionViewport[], Exception>
			collectionViewportsUnsafeSupplier) {

		_collectionViewportsSupplier = () -> {
			try {
				return collectionViewportsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of viewports of the page collection.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected CollectionViewport[] collectionViewports;

	@JsonIgnore
	private Supplier<CollectionViewport[]> _collectionViewportsSupplier;

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
		description = "The fragment style of the page collection."
	)
	@Valid
	public FragmentStyle getFragmentStyle() {
		if (_fragmentStyleSupplier != null) {
			fragmentStyle = _fragmentStyleSupplier.get();

			_fragmentStyleSupplier = null;
		}

		return fragmentStyle;
	}

	public void setFragmentStyle(FragmentStyle fragmentStyle) {
		this.fragmentStyle = fragmentStyle;

		_fragmentStyleSupplier = null;
	}

	@JsonIgnore
	public void setFragmentStyle(
		UnsafeSupplier<FragmentStyle, Exception> fragmentStyleUnsafeSupplier) {

		_fragmentStyleSupplier = () -> {
			try {
				return fragmentStyleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment style of the page collection.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentStyle fragmentStyle;

	@JsonIgnore
	private Supplier<FragmentStyle> _fragmentStyleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment viewports of the page collection."
	)
	@Valid
	public FragmentViewport[] getFragmentViewports() {
		if (_fragmentViewportsSupplier != null) {
			fragmentViewports = _fragmentViewportsSupplier.get();

			_fragmentViewportsSupplier = null;
		}

		return fragmentViewports;
	}

	public void setFragmentViewports(FragmentViewport[] fragmentViewports) {
		this.fragmentViewports = fragmentViewports;

		_fragmentViewportsSupplier = null;
	}

	@JsonIgnore
	public void setFragmentViewports(
		UnsafeSupplier<FragmentViewport[], Exception>
			fragmentViewportsUnsafeSupplier) {

		_fragmentViewportsSupplier = () -> {
			try {
				return fragmentViewportsUnsafeSupplier.get();
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
		description = "The fragment viewports of the page collection."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentViewport[] fragmentViewports;

	@JsonIgnore
	private Supplier<FragmentViewport[]> _fragmentViewportsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "the page section's layout."
	)
	@Valid
	public Layout getLayout() {
		if (_layoutSupplier != null) {
			layout = _layoutSupplier.get();

			_layoutSupplier = null;
		}

		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;

		_layoutSupplier = null;
	}

	@JsonIgnore
	public void setLayout(
		UnsafeSupplier<Layout, Exception> layoutUnsafeSupplier) {

		_layoutSupplier = () -> {
			try {
				return layoutUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "the page section's layout.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Layout layout;

	@JsonIgnore
	private Supplier<Layout> _layoutSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The style of a list of items in the page collection."
	)
	public String getListItemStyle() {
		if (_listItemStyleSupplier != null) {
			listItemStyle = _listItemStyleSupplier.get();

			_listItemStyleSupplier = null;
		}

		return listItemStyle;
	}

	public void setListItemStyle(String listItemStyle) {
		this.listItemStyle = listItemStyle;

		_listItemStyleSupplier = null;
	}

	@JsonIgnore
	public void setListItemStyle(
		UnsafeSupplier<String, Exception> listItemStyleUnsafeSupplier) {

		_listItemStyleSupplier = () -> {
			try {
				return listItemStyleUnsafeSupplier.get();
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
		description = "The style of a list of items in the page collection."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String listItemStyle;

	@JsonIgnore
	private Supplier<String> _listItemStyleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The style of a list in the page collection."
	)
	public String getListStyle() {
		if (_listStyleSupplier != null) {
			listStyle = _listStyleSupplier.get();

			_listStyleSupplier = null;
		}

		return listStyle;
	}

	public void setListStyle(String listStyle) {
		this.listStyle = listStyle;

		_listStyleSupplier = null;
	}

	@JsonIgnore
	public void setListStyle(
		UnsafeSupplier<String, Exception> listStyleUnsafeSupplier) {

		_listStyleSupplier = () -> {
			try {
				return listStyleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The style of a list in the page collection.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String listStyle;

	@JsonIgnore
	private Supplier<String> _listStyleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The custom name of a Page Collection."
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

	@GraphQLField(description = "The custom name of a Page Collection.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of columns in the page collection."
	)
	public Integer getNumberOfColumns() {
		if (_numberOfColumnsSupplier != null) {
			numberOfColumns = _numberOfColumnsSupplier.get();

			_numberOfColumnsSupplier = null;
		}

		return numberOfColumns;
	}

	public void setNumberOfColumns(Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;

		_numberOfColumnsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfColumns(
		UnsafeSupplier<Integer, Exception> numberOfColumnsUnsafeSupplier) {

		_numberOfColumnsSupplier = () -> {
			try {
				return numberOfColumnsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The number of columns in the page collection.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfColumns;

	@JsonIgnore
	private Supplier<Integer> _numberOfColumnsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The maximum number of items to display in the page collection when pagination is disabled."
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
		description = "The maximum number of items to display in the page collection when pagination is disabled."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfItems;

	@JsonIgnore
	private Supplier<Integer> _numberOfItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of items per page in the page collection."
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
		description = "The number of items per page in the page collection."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfItemsPerPage;

	@JsonIgnore
	private Supplier<Integer> _numberOfItemsPerPageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The maximum number of pages to show when pagination is enabled."
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
		description = "The maximum number of pages to show when pagination is enabled."
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

	@io.swagger.v3.oas.annotations.media.Schema(
		deprecated = true,
		description = "Whether to show all items when pagination is enabled. Deprecated as of Cavanaugh (7.4.x), replaced by displayAllPages"
	)
	public Boolean getShowAllItems() {
		if (_showAllItemsSupplier != null) {
			showAllItems = _showAllItemsSupplier.get();

			_showAllItemsSupplier = null;
		}

		return showAllItems;
	}

	public void setShowAllItems(Boolean showAllItems) {
		this.showAllItems = showAllItems;

		_showAllItemsSupplier = null;
	}

	@JsonIgnore
	public void setShowAllItems(
		UnsafeSupplier<Boolean, Exception> showAllItemsUnsafeSupplier) {

		_showAllItemsSupplier = () -> {
			try {
				return showAllItemsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField(
		description = "Whether to show all items when pagination is enabled. Deprecated as of Cavanaugh (7.4.x), replaced by displayAllPages"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean showAllItems;

	@JsonIgnore
	private Supplier<Boolean> _showAllItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page collection's template key."
	)
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

	@GraphQLField(description = "The page collection's template key.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String templateKey;

	@JsonIgnore
	private Supplier<String> _templateKeySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageCollectionDefinition)) {
			return false;
		}

		PageCollectionDefinition pageCollectionDefinition =
			(PageCollectionDefinition)object;

		return Objects.equals(toString(), pageCollectionDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		CollectionConfig collectionConfig = getCollectionConfig();

		if (collectionConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionConfig\": ");

			sb.append(String.valueOf(collectionConfig));
		}

		CollectionViewport[] collectionViewports = getCollectionViewports();

		if (collectionViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionViewports\": ");

			sb.append("[");

			for (int i = 0; i < collectionViewports.length; i++) {
				sb.append(String.valueOf(collectionViewports[i]));

				if ((i + 1) < collectionViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		FragmentStyle fragmentStyle = getFragmentStyle();

		if (fragmentStyle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(String.valueOf(fragmentStyle));
		}

		FragmentViewport[] fragmentViewports = getFragmentViewports();

		if (fragmentViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0; i < fragmentViewports.length; i++) {
				sb.append(String.valueOf(fragmentViewports[i]));

				if ((i + 1) < fragmentViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Layout layout = getLayout();

		if (layout != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layout\": ");

			sb.append(String.valueOf(layout));
		}

		String listItemStyle = getListItemStyle();

		if (listItemStyle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listItemStyle\": ");

			sb.append("\"");

			sb.append(_escape(listItemStyle));

			sb.append("\"");
		}

		String listStyle = getListStyle();

		if (listStyle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listStyle\": ");

			sb.append("\"");

			sb.append(_escape(listStyle));

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

		Integer numberOfColumns = getNumberOfColumns();

		if (numberOfColumns != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfColumns\": ");

			sb.append(numberOfColumns);
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

		Boolean showAllItems = getShowAllItems();

		if (showAllItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showAllItems\": ");

			sb.append(showAllItems);
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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.PageCollectionDefinition",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("PaginationType")
	public static enum PaginationType {

		NONE("None"), NUMERIC("Numeric"), REGULAR("Regular"), SIMPLE("Simple");

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