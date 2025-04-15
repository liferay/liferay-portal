/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageContainerDefinitionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class PageContainerDefinition implements Cloneable, Serializable {

	public static PageContainerDefinition toDTO(String json) {
		return PageContainerDefinitionSerDes.toDTO(json);
	}

	public FragmentImage getBackgroundFragmentImage() {
		return backgroundFragmentImage;
	}

	public void setBackgroundFragmentImage(
		FragmentImage backgroundFragmentImage) {

		this.backgroundFragmentImage = backgroundFragmentImage;
	}

	public void setBackgroundFragmentImage(
		UnsafeSupplier<FragmentImage, Exception>
			backgroundFragmentImageUnsafeSupplier) {

		try {
			backgroundFragmentImage =
				backgroundFragmentImageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentImage backgroundFragmentImage;

	public String getContentVisibility() {
		return contentVisibility;
	}

	public void setContentVisibility(String contentVisibility) {
		this.contentVisibility = contentVisibility;
	}

	public void setContentVisibility(
		UnsafeSupplier<String, Exception> contentVisibilityUnsafeSupplier) {

		try {
			contentVisibility = contentVisibilityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String contentVisibility;

	public String[] getCssClasses() {
		return cssClasses;
	}

	public void setCssClasses(String[] cssClasses) {
		this.cssClasses = cssClasses;
	}

	public void setCssClasses(
		UnsafeSupplier<String[], Exception> cssClassesUnsafeSupplier) {

		try {
			cssClasses = cssClassesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] cssClasses;

	public String getCustomCSS() {
		return customCSS;
	}

	public void setCustomCSS(String customCSS) {
		this.customCSS = customCSS;
	}

	public void setCustomCSS(
		UnsafeSupplier<String, Exception> customCSSUnsafeSupplier) {

		try {
			customCSS = customCSSUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String customCSS;

	public CustomCSSViewport[] getCustomCSSViewports() {
		return customCSSViewports;
	}

	public void setCustomCSSViewports(CustomCSSViewport[] customCSSViewports) {
		this.customCSSViewports = customCSSViewports;
	}

	public void setCustomCSSViewports(
		UnsafeSupplier<CustomCSSViewport[], Exception>
			customCSSViewportsUnsafeSupplier) {

		try {
			customCSSViewports = customCSSViewportsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CustomCSSViewport[] customCSSViewports;

	public FragmentLink getFragmentLink() {
		return fragmentLink;
	}

	public void setFragmentLink(FragmentLink fragmentLink) {
		this.fragmentLink = fragmentLink;
	}

	public void setFragmentLink(
		UnsafeSupplier<FragmentLink, Exception> fragmentLinkUnsafeSupplier) {

		try {
			fragmentLink = fragmentLinkUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentLink fragmentLink;

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

	public HtmlProperties getHtmlProperties() {
		return htmlProperties;
	}

	public void setHtmlProperties(HtmlProperties htmlProperties) {
		this.htmlProperties = htmlProperties;
	}

	public void setHtmlProperties(
		UnsafeSupplier<HtmlProperties, Exception>
			htmlPropertiesUnsafeSupplier) {

		try {
			htmlProperties = htmlPropertiesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected HtmlProperties htmlProperties;

	public Boolean getIndexed() {
		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}

	public void setIndexed(
		UnsafeSupplier<Boolean, Exception> indexedUnsafeSupplier) {

		try {
			indexed = indexedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean indexed;

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

	@Override
	public PageContainerDefinition clone() throws CloneNotSupportedException {
		return (PageContainerDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageContainerDefinition)) {
			return false;
		}

		PageContainerDefinition pageContainerDefinition =
			(PageContainerDefinition)object;

		return Objects.equals(toString(), pageContainerDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageContainerDefinitionSerDes.toJSON(this);
	}

}