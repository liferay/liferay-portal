/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AccountLifecycleStageMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class AccountLifecycleStageMetric implements Cloneable, Serializable {

	public static AccountLifecycleStageMetric toDTO(String json) {
		return AccountLifecycleStageMetricSerDes.toDTO(json);
	}

	public Long getAccountsCount() {
		return accountsCount;
	}

	public void setAccountsCount(Long accountsCount) {
		this.accountsCount = accountsCount;
	}

	public void setAccountsCount(
		UnsafeSupplier<Long, Exception> accountsCountUnsafeSupplier) {

		try {
			accountsCount = accountsCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long accountsCount;

	public Double getAverageStageDuration() {
		return averageStageDuration;
	}

	public void setAverageStageDuration(Double averageStageDuration) {
		this.averageStageDuration = averageStageDuration;
	}

	public void setAverageStageDuration(
		UnsafeSupplier<Double, Exception> averageStageDurationUnsafeSupplier) {

		try {
			averageStageDuration = averageStageDurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double averageStageDuration;

	public Double getConversionRateToNextStage() {
		return conversionRateToNextStage;
	}

	public void setConversionRateToNextStage(Double conversionRateToNextStage) {
		this.conversionRateToNextStage = conversionRateToNextStage;
	}

	public void setConversionRateToNextStage(
		UnsafeSupplier<Double, Exception>
			conversionRateToNextStageUnsafeSupplier) {

		try {
			conversionRateToNextStage =
				conversionRateToNextStageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double conversionRateToNextStage;

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

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public void setPercentage(
		UnsafeSupplier<Double, Exception> percentageUnsafeSupplier) {

		try {
			percentage = percentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double percentage;

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void setPosition(
		UnsafeSupplier<Integer, Exception> positionUnsafeSupplier) {

		try {
			position = positionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer position;

	@Override
	public AccountLifecycleStageMetric clone()
		throws CloneNotSupportedException {

		return (AccountLifecycleStageMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountLifecycleStageMetric)) {
			return false;
		}

		AccountLifecycleStageMetric accountLifecycleStageMetric =
			(AccountLifecycleStageMetric)object;

		return Objects.equals(
			toString(), accountLifecycleStageMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AccountLifecycleStageMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1440446916