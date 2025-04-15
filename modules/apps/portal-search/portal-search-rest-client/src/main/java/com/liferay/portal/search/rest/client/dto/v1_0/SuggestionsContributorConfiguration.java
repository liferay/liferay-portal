/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.dto.v1_0;

import com.liferay.portal.search.rest.client.function.UnsafeSupplier;
import com.liferay.portal.search.rest.client.serdes.v1_0.SuggestionsContributorConfigurationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class SuggestionsContributorConfiguration
	implements Cloneable, Serializable {

	public static SuggestionsContributorConfiguration toDTO(String json) {
		return SuggestionsContributorConfigurationSerDes.toDTO(json);
	}

	public Object getAttributes() {
		return attributes;
	}

	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}

	public void setAttributes(
		UnsafeSupplier<Object, Exception> attributesUnsafeSupplier) {

		try {
			attributes = attributesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object attributes;

	public String getContributorName() {
		return contributorName;
	}

	public void setContributorName(String contributorName) {
		this.contributorName = contributorName;
	}

	public void setContributorName(
		UnsafeSupplier<String, Exception> contributorNameUnsafeSupplier) {

		try {
			contributorName = contributorNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String contributorName;

	public String getDisplayGroupName() {
		return displayGroupName;
	}

	public void setDisplayGroupName(String displayGroupName) {
		this.displayGroupName = displayGroupName;
	}

	public void setDisplayGroupName(
		UnsafeSupplier<String, Exception> displayGroupNameUnsafeSupplier) {

		try {
			displayGroupName = displayGroupNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String displayGroupName;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setSize(UnsafeSupplier<Integer, Exception> sizeUnsafeSupplier) {
		try {
			size = sizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer size;

	@Override
	public SuggestionsContributorConfiguration clone()
		throws CloneNotSupportedException {

		return (SuggestionsContributorConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SuggestionsContributorConfiguration)) {
			return false;
		}

		SuggestionsContributorConfiguration
			suggestionsContributorConfiguration =
				(SuggestionsContributorConfiguration)object;

		return Objects.equals(
			toString(), suggestionsContributorConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SuggestionsContributorConfigurationSerDes.toJSON(this);
	}

}