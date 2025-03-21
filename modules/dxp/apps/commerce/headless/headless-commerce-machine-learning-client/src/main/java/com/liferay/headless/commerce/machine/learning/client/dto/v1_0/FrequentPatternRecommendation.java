/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.dto.v1_0;

import com.liferay.headless.commerce.machine.learning.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.machine.learning.client.serdes.v1_0.FrequentPatternRecommendationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class FrequentPatternRecommendation implements Cloneable, Serializable {

	public static FrequentPatternRecommendation toDTO(String json) {
		return FrequentPatternRecommendationSerDes.toDTO(json);
	}

	public Long[] getAntecedentIds() {
		return antecedentIds;
	}

	public void setAntecedentIds(Long[] antecedentIds) {
		this.antecedentIds = antecedentIds;
	}

	public void setAntecedentIds(
		UnsafeSupplier<Long[], Exception> antecedentIdsUnsafeSupplier) {

		try {
			antecedentIds = antecedentIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] antecedentIds;

	public Long getAntecedentIdsLength() {
		return antecedentIdsLength;
	}

	public void setAntecedentIdsLength(Long antecedentIdsLength) {
		this.antecedentIdsLength = antecedentIdsLength;
	}

	public void setAntecedentIdsLength(
		UnsafeSupplier<Long, Exception> antecedentIdsLengthUnsafeSupplier) {

		try {
			antecedentIdsLength = antecedentIdsLengthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long antecedentIdsLength;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		try {
			createDate = createDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date createDate;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public void setJobId(
		UnsafeSupplier<String, Exception> jobIdUnsafeSupplier) {

		try {
			jobId = jobIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String jobId;

	public Long getRecommendedProductId() {
		return recommendedProductId;
	}

	public void setRecommendedProductId(Long recommendedProductId) {
		this.recommendedProductId = recommendedProductId;
	}

	public void setRecommendedProductId(
		UnsafeSupplier<Long, Exception> recommendedProductIdUnsafeSupplier) {

		try {
			recommendedProductId = recommendedProductIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long recommendedProductId;

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public void setScore(UnsafeSupplier<Float, Exception> scoreUnsafeSupplier) {
		try {
			score = scoreUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Float score;

	@Override
	public FrequentPatternRecommendation clone()
		throws CloneNotSupportedException {

		return (FrequentPatternRecommendation)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FrequentPatternRecommendation)) {
			return false;
		}

		FrequentPatternRecommendation frequentPatternRecommendation =
			(FrequentPatternRecommendation)object;

		return Objects.equals(
			toString(), frequentPatternRecommendation.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FrequentPatternRecommendationSerDes.toJSON(this);
	}

}