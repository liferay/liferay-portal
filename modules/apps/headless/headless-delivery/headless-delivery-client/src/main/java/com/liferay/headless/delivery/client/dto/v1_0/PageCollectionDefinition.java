/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.PageCollectionDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageCollectionDefinition implements Cloneable, Serializable {

	public static PageCollectionDefinition toDTO(String json) {
		return PageCollectionDefinitionSerDes.toDTO(json);
	}

	public CollectionConfig getCollectionConfig() {
		return collectionConfig;
	}

	public void setCollectionConfig(CollectionConfig collectionConfig) {
		this.collectionConfig = collectionConfig;
	}

	public void setCollectionConfig(
		UnsafeSupplier<CollectionConfig, Exception>
			collectionConfigUnsafeSupplier) {

		try {
			collectionConfig = collectionConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CollectionConfig collectionConfig;

	public CollectionViewport[] getCollectionViewports() {
		return collectionViewports;
	}

	public void setCollectionViewports(
		CollectionViewport[] collectionViewports) {

		this.collectionViewports = collectionViewports;
	}

	public void setCollectionViewports(
		UnsafeSupplier<CollectionViewport[], Exception>
			collectionViewportsUnsafeSupplier) {

		try {
			collectionViewports = collectionViewportsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CollectionViewport[] collectionViewports;

	public Boolean getDisplayAllItems() {
		return displayAllItems;
	}

	public void setDisplayAllItems(Boolean displayAllItems) {
		this.displayAllItems = displayAllItems;
	}

	public void setDisplayAllItems(
		UnsafeSupplier<Boolean, Exception> displayAllItemsUnsafeSupplier) {

		try {
			displayAllItems = displayAllItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean displayAllItems;

	public Boolean getDisplayAllPages() {
		return displayAllPages;
	}

	public void setDisplayAllPages(Boolean displayAllPages) {
		this.displayAllPages = displayAllPages;
	}

	public void setDisplayAllPages(
		UnsafeSupplier<Boolean, Exception> displayAllPagesUnsafeSupplier) {

		try {
			displayAllPages = displayAllPagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean displayAllPages;

	public EmptyCollectionConfig getEmptyCollectionConfig() {
		return emptyCollectionConfig;
	}

	public void setEmptyCollectionConfig(
		EmptyCollectionConfig emptyCollectionConfig) {

		this.emptyCollectionConfig = emptyCollectionConfig;
	}

	public void setEmptyCollectionConfig(
		UnsafeSupplier<EmptyCollectionConfig, Exception>
			emptyCollectionConfigUnsafeSupplier) {

		try {
			emptyCollectionConfig = emptyCollectionConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected EmptyCollectionConfig emptyCollectionConfig;

	public FragmentStyle getFragmentStyle() {
		return fragmentStyle;
	}

	public void setFragmentStyle(FragmentStyle fragmentStyle) {
		this.fragmentStyle = fragmentStyle;
	}

	public void setFragmentStyle(
		UnsafeSupplier<FragmentStyle, Exception> fragmentStyleUnsafeSupplier) {

		try {
			fragmentStyle = fragmentStyleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentStyle fragmentStyle;

	public FragmentViewport[] getFragmentViewports() {
		return fragmentViewports;
	}

	public void setFragmentViewports(FragmentViewport[] fragmentViewports) {
		this.fragmentViewports = fragmentViewports;
	}

	public void setFragmentViewports(
		UnsafeSupplier<FragmentViewport[], Exception>
			fragmentViewportsUnsafeSupplier) {

		try {
			fragmentViewports = fragmentViewportsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentViewport[] fragmentViewports;

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public void setLayout(
		UnsafeSupplier<Layout, Exception> layoutUnsafeSupplier) {

		try {
			layout = layoutUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Layout layout;

	public String getListItemStyle() {
		return listItemStyle;
	}

	public void setListItemStyle(String listItemStyle) {
		this.listItemStyle = listItemStyle;
	}

	public void setListItemStyle(
		UnsafeSupplier<String, Exception> listItemStyleUnsafeSupplier) {

		try {
			listItemStyle = listItemStyleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String listItemStyle;

	public String getListStyle() {
		return listStyle;
	}

	public void setListStyle(String listStyle) {
		this.listStyle = listStyle;
	}

	public void setListStyle(
		UnsafeSupplier<String, Exception> listStyleUnsafeSupplier) {

		try {
			listStyle = listStyleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String listStyle;

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

	public Integer getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public void setNumberOfColumns(
		UnsafeSupplier<Integer, Exception> numberOfColumnsUnsafeSupplier) {

		try {
			numberOfColumns = numberOfColumnsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfColumns;

	public Integer getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public void setNumberOfItems(
		UnsafeSupplier<Integer, Exception> numberOfItemsUnsafeSupplier) {

		try {
			numberOfItems = numberOfItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfItems;

	public Integer getNumberOfItemsPerPage() {
		return numberOfItemsPerPage;
	}

	public void setNumberOfItemsPerPage(Integer numberOfItemsPerPage) {
		this.numberOfItemsPerPage = numberOfItemsPerPage;
	}

	public void setNumberOfItemsPerPage(
		UnsafeSupplier<Integer, Exception> numberOfItemsPerPageUnsafeSupplier) {

		try {
			numberOfItemsPerPage = numberOfItemsPerPageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfItemsPerPage;

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public void setNumberOfPages(
		UnsafeSupplier<Integer, Exception> numberOfPagesUnsafeSupplier) {

		try {
			numberOfPages = numberOfPagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfPages;

	public PaginationType getPaginationType() {
		return paginationType;
	}

	public String getPaginationTypeAsString() {
		if (paginationType == null) {
			return null;
		}

		return paginationType.toString();
	}

	public void setPaginationType(PaginationType paginationType) {
		this.paginationType = paginationType;
	}

	public void setPaginationType(
		UnsafeSupplier<PaginationType, Exception>
			paginationTypeUnsafeSupplier) {

		try {
			paginationType = paginationTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PaginationType paginationType;

	public Boolean getShowAllItems() {
		return showAllItems;
	}

	public void setShowAllItems(Boolean showAllItems) {
		this.showAllItems = showAllItems;
	}

	public void setShowAllItems(
		UnsafeSupplier<Boolean, Exception> showAllItemsUnsafeSupplier) {

		try {
			showAllItems = showAllItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean showAllItems;

	public String getTemplateKey() {
		return templateKey;
	}

	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;
	}

	public void setTemplateKey(
		UnsafeSupplier<String, Exception> templateKeyUnsafeSupplier) {

		try {
			templateKey = templateKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String templateKey;

	@Override
	public PageCollectionDefinition clone() throws CloneNotSupportedException {
		return (PageCollectionDefinition)super.clone();
	}

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
		return PageCollectionDefinitionSerDes.toJSON(this);
	}

	public static enum PaginationType {

		NONE("None"), NUMERIC("Numeric"), REGULAR("Regular"), SIMPLE("Simple");

		public static PaginationType create(String value) {
			for (PaginationType paginationType : values()) {
				if (Objects.equals(paginationType.getValue(), value) ||
					Objects.equals(paginationType.name(), value)) {

					return paginationType;
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

		private PaginationType(String value) {
			_value = value;
		}

		private final String _value;

	}

}