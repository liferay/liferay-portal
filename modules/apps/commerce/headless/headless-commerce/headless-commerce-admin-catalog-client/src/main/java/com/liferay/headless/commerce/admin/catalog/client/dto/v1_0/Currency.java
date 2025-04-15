/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.CurrencySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Map;
import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class Currency implements Cloneable, Serializable {

	public static Currency toDTO(String json) {
		return CurrencySerDes.toDTO(json);
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCode(UnsafeSupplier<String, Exception> codeUnsafeSupplier) {
		try {
			code = codeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String code;

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

	public Map<String, String> getFormatPattern() {
		return formatPattern;
	}

	public void setFormatPattern(Map<String, String> formatPattern) {
		this.formatPattern = formatPattern;
	}

	public void setFormatPattern(
		UnsafeSupplier<Map<String, String>, Exception>
			formatPatternUnsafeSupplier) {

		try {
			formatPattern = formatPatternUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> formatPattern;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Integer getMaxFractionDigits() {
		return maxFractionDigits;
	}

	public void setMaxFractionDigits(Integer maxFractionDigits) {
		this.maxFractionDigits = maxFractionDigits;
	}

	public void setMaxFractionDigits(
		UnsafeSupplier<Integer, Exception> maxFractionDigitsUnsafeSupplier) {

		try {
			maxFractionDigits = maxFractionDigitsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maxFractionDigits;

	public Integer getMinFractionDigits() {
		return minFractionDigits;
	}

	public void setMinFractionDigits(Integer minFractionDigits) {
		this.minFractionDigits = minFractionDigits;
	}

	public void setMinFractionDigits(
		UnsafeSupplier<Integer, Exception> minFractionDigitsUnsafeSupplier) {

		try {
			minFractionDigits = minFractionDigitsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer minFractionDigits;

	public Map<String, String> getName() {
		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;
	}

	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> name;

	public Boolean getPrimary() {
		return primary;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	public void setPrimary(
		UnsafeSupplier<Boolean, Exception> primaryUnsafeSupplier) {

		try {
			primary = primaryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean primary;

	public Double getPriority() {
		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		try {
			priority = priorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double priority;

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public void setRate(
		UnsafeSupplier<BigDecimal, Exception> rateUnsafeSupplier) {

		try {
			rate = rateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal rate;

	public RoundingMode getRoundingMode() {
		return roundingMode;
	}

	public String getRoundingModeAsString() {
		if (roundingMode == null) {
			return null;
		}

		return roundingMode.toString();
	}

	public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	public void setRoundingMode(
		UnsafeSupplier<RoundingMode, Exception> roundingModeUnsafeSupplier) {

		try {
			roundingMode = roundingModeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RoundingMode roundingMode;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setSymbol(
		UnsafeSupplier<String, Exception> symbolUnsafeSupplier) {

		try {
			symbol = symbolUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String symbol;

	@Override
	public Currency clone() throws CloneNotSupportedException {
		return (Currency)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Currency)) {
			return false;
		}

		Currency currency = (Currency)object;

		return Objects.equals(toString(), currency.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CurrencySerDes.toJSON(this);
	}

	public static enum RoundingMode {

		UP("UP"), DOWN("DOWN"), CEILING("CEILING"), FLOOR("FLOOR"),
		HALF_UP("HALF_UP"), HALF_DOWN("HALF_DOWN"), HALF_EVEN("HALF_EVEN"),
		UNNECESSARY("UNNECESSARY");

		public static RoundingMode create(String value) {
			for (RoundingMode roundingMode : values()) {
				if (Objects.equals(roundingMode.getValue(), value) ||
					Objects.equals(roundingMode.name(), value)) {

					return roundingMode;
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

		private RoundingMode(String value) {
			_value = value;
		}

		private final String _value;

	}

}