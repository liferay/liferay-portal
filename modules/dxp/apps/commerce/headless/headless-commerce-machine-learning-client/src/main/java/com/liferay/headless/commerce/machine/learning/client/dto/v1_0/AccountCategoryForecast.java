/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.dto.v1_0;

import com.liferay.headless.commerce.machine.learning.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.machine.learning.client.serdes.v1_0.AccountCategoryForecastSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class AccountCategoryForecast implements Cloneable, Serializable {

	public static AccountCategoryForecast toDTO(String json) {
		return AccountCategoryForecastSerDes.toDTO(json);
	}

	public Long getAccount() {
		return account;
	}

	public void setAccount(Long account) {
		this.account = account;
	}

	public void setAccount(
		UnsafeSupplier<Long, Exception> accountUnsafeSupplier) {

		try {
			account = accountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long account;

	public Float getActual() {
		return actual;
	}

	public void setActual(Float actual) {
		this.actual = actual;
	}

	public void setActual(
		UnsafeSupplier<Float, Exception> actualUnsafeSupplier) {

		try {
			actual = actualUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Float actual;

	public Long getCategory() {
		return category;
	}

	public void setCategory(Long category) {
		this.category = category;
	}

	public void setCategory(
		UnsafeSupplier<Long, Exception> categoryUnsafeSupplier) {

		try {
			category = categoryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long category;

	public String getCategoryTitle() {
		return categoryTitle;
	}

	public void setCategoryTitle(String categoryTitle) {
		this.categoryTitle = categoryTitle;
	}

	public void setCategoryTitle(
		UnsafeSupplier<String, Exception> categoryTitleUnsafeSupplier) {

		try {
			categoryTitle = categoryTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String categoryTitle;

	public Float getForecast() {
		return forecast;
	}

	public void setForecast(Float forecast) {
		this.forecast = forecast;
	}

	public void setForecast(
		UnsafeSupplier<Float, Exception> forecastUnsafeSupplier) {

		try {
			forecast = forecastUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Float forecast;

	public Float getForecastLowerBound() {
		return forecastLowerBound;
	}

	public void setForecastLowerBound(Float forecastLowerBound) {
		this.forecastLowerBound = forecastLowerBound;
	}

	public void setForecastLowerBound(
		UnsafeSupplier<Float, Exception> forecastLowerBoundUnsafeSupplier) {

		try {
			forecastLowerBound = forecastLowerBoundUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Float forecastLowerBound;

	public Float getForecastUpperBound() {
		return forecastUpperBound;
	}

	public void setForecastUpperBound(Float forecastUpperBound) {
		this.forecastUpperBound = forecastUpperBound;
	}

	public void setForecastUpperBound(
		UnsafeSupplier<Float, Exception> forecastUpperBoundUnsafeSupplier) {

		try {
			forecastUpperBound = forecastUpperBoundUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Float forecastUpperBound;

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setTimestamp(
		UnsafeSupplier<Date, Exception> timestampUnsafeSupplier) {

		try {
			timestamp = timestampUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date timestamp;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setUnit(UnsafeSupplier<String, Exception> unitUnsafeSupplier) {
		try {
			unit = unitUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String unit;

	@Override
	public AccountCategoryForecast clone() throws CloneNotSupportedException {
		return (AccountCategoryForecast)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountCategoryForecast)) {
			return false;
		}

		AccountCategoryForecast accountCategoryForecast =
			(AccountCategoryForecast)object;

		return Objects.equals(toString(), accountCategoryForecast.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AccountCategoryForecastSerDes.toJSON(this);
	}

}