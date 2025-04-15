/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.dto.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.PriceListSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class PriceList implements Cloneable, Serializable {

	public static PriceList toDTO(String json) {
		return PriceListSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setAuthor(
		UnsafeSupplier<String, Exception> authorUnsafeSupplier) {

		try {
			author = authorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String author;

	public Boolean getCatalogBasePriceList() {
		return catalogBasePriceList;
	}

	public void setCatalogBasePriceList(Boolean catalogBasePriceList) {
		this.catalogBasePriceList = catalogBasePriceList;
	}

	public void setCatalogBasePriceList(
		UnsafeSupplier<Boolean, Exception> catalogBasePriceListUnsafeSupplier) {

		try {
			catalogBasePriceList = catalogBasePriceListUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean catalogBasePriceList;

	public Long getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
	}

	public void setCatalogId(
		UnsafeSupplier<Long, Exception> catalogIdUnsafeSupplier) {

		try {
			catalogId = catalogIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long catalogId;

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public void setCatalogName(
		UnsafeSupplier<String, Exception> catalogNameUnsafeSupplier) {

		try {
			catalogName = catalogNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String catalogName;

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

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public void setCurrencyCode(
		UnsafeSupplier<String, Exception> currencyCodeUnsafeSupplier) {

		try {
			currencyCode = currencyCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String currencyCode;

	public String getCurrencyExternalReferenceCode() {
		return currencyExternalReferenceCode;
	}

	public void setCurrencyExternalReferenceCode(
		String currencyExternalReferenceCode) {

		this.currencyExternalReferenceCode = currencyExternalReferenceCode;
	}

	public void setCurrencyExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			currencyExternalReferenceCodeUnsafeSupplier) {

		try {
			currencyExternalReferenceCode =
				currencyExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String currencyExternalReferenceCode;

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public void setCurrencyId(
		UnsafeSupplier<Long, Exception> currencyIdUnsafeSupplier) {

		try {
			currencyId = currencyIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long currencyId;

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

	public Boolean getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(Boolean netPrice) {
		this.netPrice = netPrice;
	}

	public void setNetPrice(
		UnsafeSupplier<Boolean, Exception> netPriceUnsafeSupplier) {

		try {
			netPrice = netPriceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean netPrice;

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

	public Long getParentPriceListId() {
		return parentPriceListId;
	}

	public void setParentPriceListId(Long parentPriceListId) {
		this.parentPriceListId = parentPriceListId;
	}

	public void setParentPriceListId(
		UnsafeSupplier<Long, Exception> parentPriceListIdUnsafeSupplier) {

		try {
			parentPriceListId = parentPriceListIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long parentPriceListId;

	public PriceEntry[] getPriceEntries() {
		return priceEntries;
	}

	public void setPriceEntries(PriceEntry[] priceEntries) {
		this.priceEntries = priceEntries;
	}

	public void setPriceEntries(
		UnsafeSupplier<PriceEntry[], Exception> priceEntriesUnsafeSupplier) {

		try {
			priceEntries = priceEntriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PriceEntry[] priceEntries;

	public PriceListAccountGroup[] getPriceListAccountGroups() {
		return priceListAccountGroups;
	}

	public void setPriceListAccountGroups(
		PriceListAccountGroup[] priceListAccountGroups) {

		this.priceListAccountGroups = priceListAccountGroups;
	}

	public void setPriceListAccountGroups(
		UnsafeSupplier<PriceListAccountGroup[], Exception>
			priceListAccountGroupsUnsafeSupplier) {

		try {
			priceListAccountGroups = priceListAccountGroupsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PriceListAccountGroup[] priceListAccountGroups;

	public PriceListAccount[] getPriceListAccounts() {
		return priceListAccounts;
	}

	public void setPriceListAccounts(PriceListAccount[] priceListAccounts) {
		this.priceListAccounts = priceListAccounts;
	}

	public void setPriceListAccounts(
		UnsafeSupplier<PriceListAccount[], Exception>
			priceListAccountsUnsafeSupplier) {

		try {
			priceListAccounts = priceListAccountsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PriceListAccount[] priceListAccounts;

	public PriceListChannel[] getPriceListChannels() {
		return priceListChannels;
	}

	public void setPriceListChannels(PriceListChannel[] priceListChannels) {
		this.priceListChannels = priceListChannels;
	}

	public void setPriceListChannels(
		UnsafeSupplier<PriceListChannel[], Exception>
			priceListChannelsUnsafeSupplier) {

		try {
			priceListChannels = priceListChannelsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PriceListChannel[] priceListChannels;

	public PriceListDiscount[] getPriceListDiscounts() {
		return priceListDiscounts;
	}

	public void setPriceListDiscounts(PriceListDiscount[] priceListDiscounts) {
		this.priceListDiscounts = priceListDiscounts;
	}

	public void setPriceListDiscounts(
		UnsafeSupplier<PriceListDiscount[], Exception>
			priceListDiscountsUnsafeSupplier) {

		try {
			priceListDiscounts = priceListDiscountsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PriceListDiscount[] priceListDiscounts;

	public PriceListOrderType[] getPriceListOrderTypes() {
		return priceListOrderTypes;
	}

	public void setPriceListOrderTypes(
		PriceListOrderType[] priceListOrderTypes) {

		this.priceListOrderTypes = priceListOrderTypes;
	}

	public void setPriceListOrderTypes(
		UnsafeSupplier<PriceListOrderType[], Exception>
			priceListOrderTypesUnsafeSupplier) {

		try {
			priceListOrderTypes = priceListOrderTypesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PriceListOrderType[] priceListOrderTypes;

	public PriceModifier[] getPriceModifiers() {
		return priceModifiers;
	}

	public void setPriceModifiers(PriceModifier[] priceModifiers) {
		this.priceModifiers = priceModifiers;
	}

	public void setPriceModifiers(
		UnsafeSupplier<PriceModifier[], Exception>
			priceModifiersUnsafeSupplier) {

		try {
			priceModifiers = priceModifiersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PriceModifier[] priceModifiers;

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

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Type type;

	public Status getWorkflowStatusInfo() {
		return workflowStatusInfo;
	}

	public void setWorkflowStatusInfo(Status workflowStatusInfo) {
		this.workflowStatusInfo = workflowStatusInfo;
	}

	public void setWorkflowStatusInfo(
		UnsafeSupplier<Status, Exception> workflowStatusInfoUnsafeSupplier) {

		try {
			workflowStatusInfo = workflowStatusInfoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status workflowStatusInfo;

	@Override
	public PriceList clone() throws CloneNotSupportedException {
		return (PriceList)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PriceList)) {
			return false;
		}

		PriceList priceList = (PriceList)object;

		return Objects.equals(toString(), priceList.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PriceListSerDes.toJSON(this);
	}

	public static enum Type {

		PRICE_LIST("price-list"), PROMOTION("promotion"), CONTRACT("contract");

		public static Type create(String value) {
			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value) ||
					Objects.equals(type.name(), value)) {

					return type;
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

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

}