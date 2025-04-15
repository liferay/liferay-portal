/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.dto.v1_0;

import com.liferay.headless.commerce.admin.pricing.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v1_0.DiscountSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class Discount implements Cloneable, Serializable {

	public static Discount toDTO(String json) {
		return DiscountSerDes.toDTO(json);
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

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public void setCouponCode(
		UnsafeSupplier<String, Exception> couponCodeUnsafeSupplier) {

		try {
			couponCode = couponCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String couponCode;

	public Map<String, ?> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, ?> customFields) {
		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier<Map<String, ?>, Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> customFields;

	public DiscountAccountGroup[] getDiscountAccountGroups() {
		return discountAccountGroups;
	}

	public void setDiscountAccountGroups(
		DiscountAccountGroup[] discountAccountGroups) {

		this.discountAccountGroups = discountAccountGroups;
	}

	public void setDiscountAccountGroups(
		UnsafeSupplier<DiscountAccountGroup[], Exception>
			discountAccountGroupsUnsafeSupplier) {

		try {
			discountAccountGroups = discountAccountGroupsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DiscountAccountGroup[] discountAccountGroups;

	public DiscountCategory[] getDiscountCategories() {
		return discountCategories;
	}

	public void setDiscountCategories(DiscountCategory[] discountCategories) {
		this.discountCategories = discountCategories;
	}

	public void setDiscountCategories(
		UnsafeSupplier<DiscountCategory[], Exception>
			discountCategoriesUnsafeSupplier) {

		try {
			discountCategories = discountCategoriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DiscountCategory[] discountCategories;

	public DiscountProduct[] getDiscountProducts() {
		return discountProducts;
	}

	public void setDiscountProducts(DiscountProduct[] discountProducts) {
		this.discountProducts = discountProducts;
	}

	public void setDiscountProducts(
		UnsafeSupplier<DiscountProduct[], Exception>
			discountProductsUnsafeSupplier) {

		try {
			discountProducts = discountProductsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DiscountProduct[] discountProducts;

	public DiscountRule[] getDiscountRules() {
		return discountRules;
	}

	public void setDiscountRules(DiscountRule[] discountRules) {
		this.discountRules = discountRules;
	}

	public void setDiscountRules(
		UnsafeSupplier<DiscountRule[], Exception> discountRulesUnsafeSupplier) {

		try {
			discountRules = discountRulesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DiscountRule[] discountRules;

	public Date getDisplayDate() {
		return displayDate;
	}

	public void setDisplayDate(Date displayDate) {
		this.displayDate = displayDate;
	}

	public void setDisplayDate(
		UnsafeSupplier<Date, Exception> displayDateUnsafeSupplier) {

		try {
			displayDate = displayDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date displayDate;

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public void setExpirationDate(
		UnsafeSupplier<Date, Exception> expirationDateUnsafeSupplier) {

		try {
			expirationDate = expirationDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date expirationDate;

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

	public Integer getLimitationTimes() {
		return limitationTimes;
	}

	public void setLimitationTimes(Integer limitationTimes) {
		this.limitationTimes = limitationTimes;
	}

	public void setLimitationTimes(
		UnsafeSupplier<Integer, Exception> limitationTimesUnsafeSupplier) {

		try {
			limitationTimes = limitationTimesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer limitationTimes;

	public String getLimitationType() {
		return limitationType;
	}

	public void setLimitationType(String limitationType) {
		this.limitationType = limitationType;
	}

	public void setLimitationType(
		UnsafeSupplier<String, Exception> limitationTypeUnsafeSupplier) {

		try {
			limitationType = limitationTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String limitationType;

	public BigDecimal getMaximumDiscountAmount() {
		return maximumDiscountAmount;
	}

	public void setMaximumDiscountAmount(BigDecimal maximumDiscountAmount) {
		this.maximumDiscountAmount = maximumDiscountAmount;
	}

	public void setMaximumDiscountAmount(
		UnsafeSupplier<BigDecimal, Exception>
			maximumDiscountAmountUnsafeSupplier) {

		try {
			maximumDiscountAmount = maximumDiscountAmountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal maximumDiscountAmount;

	public Boolean getNeverExpire() {
		return neverExpire;
	}

	public void setNeverExpire(Boolean neverExpire) {
		this.neverExpire = neverExpire;
	}

	public void setNeverExpire(
		UnsafeSupplier<Boolean, Exception> neverExpireUnsafeSupplier) {

		try {
			neverExpire = neverExpireUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean neverExpire;

	public Integer getNumberOfUse() {
		return numberOfUse;
	}

	public void setNumberOfUse(Integer numberOfUse) {
		this.numberOfUse = numberOfUse;
	}

	public void setNumberOfUse(
		UnsafeSupplier<Integer, Exception> numberOfUseUnsafeSupplier) {

		try {
			numberOfUse = numberOfUseUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfUse;

	public BigDecimal getPercentageLevel1() {
		return percentageLevel1;
	}

	public void setPercentageLevel1(BigDecimal percentageLevel1) {
		this.percentageLevel1 = percentageLevel1;
	}

	public void setPercentageLevel1(
		UnsafeSupplier<BigDecimal, Exception> percentageLevel1UnsafeSupplier) {

		try {
			percentageLevel1 = percentageLevel1UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal percentageLevel1;

	public BigDecimal getPercentageLevel2() {
		return percentageLevel2;
	}

	public void setPercentageLevel2(BigDecimal percentageLevel2) {
		this.percentageLevel2 = percentageLevel2;
	}

	public void setPercentageLevel2(
		UnsafeSupplier<BigDecimal, Exception> percentageLevel2UnsafeSupplier) {

		try {
			percentageLevel2 = percentageLevel2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal percentageLevel2;

	public BigDecimal getPercentageLevel3() {
		return percentageLevel3;
	}

	public void setPercentageLevel3(BigDecimal percentageLevel3) {
		this.percentageLevel3 = percentageLevel3;
	}

	public void setPercentageLevel3(
		UnsafeSupplier<BigDecimal, Exception> percentageLevel3UnsafeSupplier) {

		try {
			percentageLevel3 = percentageLevel3UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal percentageLevel3;

	public BigDecimal getPercentageLevel4() {
		return percentageLevel4;
	}

	public void setPercentageLevel4(BigDecimal percentageLevel4) {
		this.percentageLevel4 = percentageLevel4;
	}

	public void setPercentageLevel4(
		UnsafeSupplier<BigDecimal, Exception> percentageLevel4UnsafeSupplier) {

		try {
			percentageLevel4 = percentageLevel4UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal percentageLevel4;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		try {
			target = targetUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String target;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	public Boolean getUseCouponCode() {
		return useCouponCode;
	}

	public void setUseCouponCode(Boolean useCouponCode) {
		this.useCouponCode = useCouponCode;
	}

	public void setUseCouponCode(
		UnsafeSupplier<Boolean, Exception> useCouponCodeUnsafeSupplier) {

		try {
			useCouponCode = useCouponCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean useCouponCode;

	public Boolean getUsePercentage() {
		return usePercentage;
	}

	public void setUsePercentage(Boolean usePercentage) {
		this.usePercentage = usePercentage;
	}

	public void setUsePercentage(
		UnsafeSupplier<Boolean, Exception> usePercentageUnsafeSupplier) {

		try {
			usePercentage = usePercentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean usePercentage;

	@Override
	public Discount clone() throws CloneNotSupportedException {
		return (Discount)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Discount)) {
			return false;
		}

		Discount discount = (Discount)object;

		return Objects.equals(toString(), discount.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DiscountSerDes.toJSON(this);
	}

}