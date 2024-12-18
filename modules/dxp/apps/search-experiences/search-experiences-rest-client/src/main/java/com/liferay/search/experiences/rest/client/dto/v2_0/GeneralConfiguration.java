/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v2_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v2_0.GeneralConfigurationSerDes;

import java.io.Serializable;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class GeneralConfiguration implements Cloneable, Serializable {

	public static GeneralConfiguration toDTO(String json) {
		return GeneralConfigurationSerDes.toDTO(json);
	}

	public Boolean getApplyIndexerClauses() {
		return applyIndexerClauses;
	}

	public void setApplyIndexerClauses(Boolean applyIndexerClauses) {
		this.applyIndexerClauses = applyIndexerClauses;
	}

	public void setApplyIndexerClauses(
		UnsafeSupplier<Boolean, Exception> applyIndexerClausesUnsafeSupplier) {

		try {
			applyIndexerClauses = applyIndexerClausesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean applyIndexerClauses;

	public String[] getClauseContributorsExcludes() {
		return clauseContributorsExcludes;
	}

	public void setClauseContributorsExcludes(
		String[] clauseContributorsExcludes) {

		this.clauseContributorsExcludes = clauseContributorsExcludes;
	}

	public void setClauseContributorsExcludes(
		UnsafeSupplier<String[], Exception>
			clauseContributorsExcludesUnsafeSupplier) {

		try {
			clauseContributorsExcludes =
				clauseContributorsExcludesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] clauseContributorsExcludes;

	public String[] getClauseContributorsIncludes() {
		return clauseContributorsIncludes;
	}

	public void setClauseContributorsIncludes(
		String[] clauseContributorsIncludes) {

		this.clauseContributorsIncludes = clauseContributorsIncludes;
	}

	public void setClauseContributorsIncludes(
		UnsafeSupplier<String[], Exception>
			clauseContributorsIncludesUnsafeSupplier) {

		try {
			clauseContributorsIncludes =
				clauseContributorsIncludesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] clauseContributorsIncludes;

	public Boolean getCollectionProvider() {
		return collectionProvider;
	}

	public void setCollectionProvider(Boolean collectionProvider) {
		this.collectionProvider = collectionProvider;
	}

	public void setCollectionProvider(
		UnsafeSupplier<Boolean, Exception> collectionProviderUnsafeSupplier) {

		try {
			collectionProvider = collectionProviderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean collectionProvider;

	public Boolean getEmptySearchEnabled() {
		return emptySearchEnabled;
	}

	public void setEmptySearchEnabled(Boolean emptySearchEnabled) {
		this.emptySearchEnabled = emptySearchEnabled;
	}

	public void setEmptySearchEnabled(
		UnsafeSupplier<Boolean, Exception> emptySearchEnabledUnsafeSupplier) {

		try {
			emptySearchEnabled = emptySearchEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean emptySearchEnabled;

	public Boolean getExplain() {
		return explain;
	}

	public void setExplain(Boolean explain) {
		this.explain = explain;
	}

	public void setExplain(
		UnsafeSupplier<Boolean, Exception> explainUnsafeSupplier) {

		try {
			explain = explainUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean explain;

	public Boolean getIncludeResponseString() {
		return includeResponseString;
	}

	public void setIncludeResponseString(Boolean includeResponseString) {
		this.includeResponseString = includeResponseString;
	}

	public void setIncludeResponseString(
		UnsafeSupplier<Boolean, Exception>
			includeResponseStringUnsafeSupplier) {

		try {
			includeResponseString = includeResponseStringUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean includeResponseString;

	public String[] getSearchableAssetTypes() {
		return searchableAssetTypes;
	}

	public void setSearchableAssetTypes(String[] searchableAssetTypes) {
		this.searchableAssetTypes = searchableAssetTypes;
	}

	public void setSearchableAssetTypes(
		UnsafeSupplier<String[], Exception>
			searchableAssetTypesUnsafeSupplier) {

		try {
			searchableAssetTypes = searchableAssetTypesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] searchableAssetTypes;

	@Override
	public GeneralConfiguration clone() throws CloneNotSupportedException {
		return (GeneralConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GeneralConfiguration)) {
			return false;
		}

		GeneralConfiguration generalConfiguration =
			(GeneralConfiguration)object;

		return Objects.equals(toString(), generalConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return GeneralConfigurationSerDes.toJSON(this);
	}

}