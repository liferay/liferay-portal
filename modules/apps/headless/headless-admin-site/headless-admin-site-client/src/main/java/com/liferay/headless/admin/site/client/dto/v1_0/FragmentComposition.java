/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentCompositionSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentComposition implements Cloneable, Serializable {

	public static FragmentComposition toDTO(String json) {
		return FragmentCompositionSerDes.toDTO(json);
	}

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

	public String getCreatorExternalReferenceCode() {
		return creatorExternalReferenceCode;
	}

	public void setCreatorExternalReferenceCode(
		String creatorExternalReferenceCode) {

		this.creatorExternalReferenceCode = creatorExternalReferenceCode;
	}

	public void setCreatorExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			creatorExternalReferenceCodeUnsafeSupplier) {

		try {
			creatorExternalReferenceCode =
				creatorExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String creatorExternalReferenceCode;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	public void setDatePublished(
		UnsafeSupplier<Date, Exception> datePublishedUnsafeSupplier) {

		try {
			datePublished = datePublishedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date datePublished;

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

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public String getFragmentSetExternalReferenceCode() {
		return fragmentSetExternalReferenceCode;
	}

	public void setFragmentSetExternalReferenceCode(
		String fragmentSetExternalReferenceCode) {

		this.fragmentSetExternalReferenceCode =
			fragmentSetExternalReferenceCode;
	}

	public void setFragmentSetExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			fragmentSetExternalReferenceCodeUnsafeSupplier) {

		try {
			fragmentSetExternalReferenceCode =
				fragmentSetExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fragmentSetExternalReferenceCode;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String key;

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

	public PageElement getPageElement() {
		return pageElement;
	}

	public void setPageElement(PageElement pageElement) {
		this.pageElement = pageElement;
	}

	public void setPageElement(
		UnsafeSupplier<PageElement, Exception> pageElementUnsafeSupplier) {

		try {
			pageElement = pageElementUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageElement pageElement;

	public ItemExternalReference getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(ItemExternalReference thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setThumbnail(
		UnsafeSupplier<ItemExternalReference, Exception>
			thumbnailUnsafeSupplier) {

		try {
			thumbnail = thumbnailUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference thumbnail;

	@Override
	public FragmentComposition clone() throws CloneNotSupportedException {
		return (FragmentComposition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentComposition)) {
			return false;
		}

		FragmentComposition fragmentComposition = (FragmentComposition)object;

		return Objects.equals(toString(), fragmentComposition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentCompositionSerDes.toJSON(this);
	}

}