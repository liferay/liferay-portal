/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.dto.v1_0;

import com.liferay.analytics.settings.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.settings.rest.client.serdes.v1_0.RecommendationConfigurationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class RecommendationConfiguration implements Cloneable, Serializable {

	public static RecommendationConfiguration toDTO(String json) {
		return RecommendationConfigurationSerDes.toDTO(json);
	}

	public RecommendationItem getContentRecommenderMostPopularItems() {
		return contentRecommenderMostPopularItems;
	}

	public void setContentRecommenderMostPopularItems(
		RecommendationItem contentRecommenderMostPopularItems) {

		this.contentRecommenderMostPopularItems =
			contentRecommenderMostPopularItems;
	}

	public void setContentRecommenderMostPopularItems(
		UnsafeSupplier<RecommendationItem, Exception>
			contentRecommenderMostPopularItemsUnsafeSupplier) {

		try {
			contentRecommenderMostPopularItems =
				contentRecommenderMostPopularItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RecommendationItem contentRecommenderMostPopularItems;

	public RecommendationItem getContentRecommenderUserPersonalization() {
		return contentRecommenderUserPersonalization;
	}

	public void setContentRecommenderUserPersonalization(
		RecommendationItem contentRecommenderUserPersonalization) {

		this.contentRecommenderUserPersonalization =
			contentRecommenderUserPersonalization;
	}

	public void setContentRecommenderUserPersonalization(
		UnsafeSupplier<RecommendationItem, Exception>
			contentRecommenderUserPersonalizationUnsafeSupplier) {

		try {
			contentRecommenderUserPersonalization =
				contentRecommenderUserPersonalizationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RecommendationItem contentRecommenderUserPersonalization;

	@Override
	public RecommendationConfiguration clone()
		throws CloneNotSupportedException {

		return (RecommendationConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RecommendationConfiguration)) {
			return false;
		}

		RecommendationConfiguration recommendationConfiguration =
			(RecommendationConfiguration)object;

		return Objects.equals(
			toString(), recommendationConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return RecommendationConfigurationSerDes.toJSON(this);
	}

}