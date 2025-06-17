/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link CPConfigurationEntry}.
 * </p>
 *
 * @author Marco Leo
 * @see CPConfigurationEntry
 * @generated
 */
public class CPConfigurationEntryWrapper
	extends BaseModelWrapper<CPConfigurationEntry>
	implements CPConfigurationEntry, ModelWrapper<CPConfigurationEntry> {

	public CPConfigurationEntryWrapper(
		CPConfigurationEntry cpConfigurationEntry) {

		super(cpConfigurationEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("CPConfigurationEntryId", getCPConfigurationEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put("CPConfigurationListId", getCPConfigurationListId());
		attributes.put("CPTaxCategoryId", getCPTaxCategoryId());
		attributes.put("allowedOrderQuantities", getAllowedOrderQuantities());
		attributes.put("backOrders", isBackOrders());
		attributes.put(
			"commerceAvailabilityEstimateId",
			getCommerceAvailabilityEstimateId());
		attributes.put(
			"CPDefinitionInventoryEngine", getCPDefinitionInventoryEngine());
		attributes.put("depth", getDepth());
		attributes.put("displayAvailability", isDisplayAvailability());
		attributes.put("displayStockQuantity", isDisplayStockQuantity());
		attributes.put("freeShipping", isFreeShipping());
		attributes.put("height", getHeight());
		attributes.put("lowStockActivity", getLowStockActivity());
		attributes.put("maxOrderQuantity", getMaxOrderQuantity());
		attributes.put("minOrderQuantity", getMinOrderQuantity());
		attributes.put("minStockQuantity", getMinStockQuantity());
		attributes.put("multipleOrderQuantity", getMultipleOrderQuantity());
		attributes.put("purchasable", isPurchasable());
		attributes.put("shippable", isShippable());
		attributes.put("shippingExtraPrice", getShippingExtraPrice());
		attributes.put("shipSeparately", isShipSeparately());
		attributes.put("taxExempt", isTaxExempt());
		attributes.put("visible", isVisible());
		attributes.put("weight", getWeight());
		attributes.put("width", getWidth());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long CPConfigurationEntryId = (Long)attributes.get(
			"CPConfigurationEntryId");

		if (CPConfigurationEntryId != null) {
			setCPConfigurationEntryId(CPConfigurationEntryId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		Long CPConfigurationListId = (Long)attributes.get(
			"CPConfigurationListId");

		if (CPConfigurationListId != null) {
			setCPConfigurationListId(CPConfigurationListId);
		}

		Long CPTaxCategoryId = (Long)attributes.get("CPTaxCategoryId");

		if (CPTaxCategoryId != null) {
			setCPTaxCategoryId(CPTaxCategoryId);
		}

		String allowedOrderQuantities = (String)attributes.get(
			"allowedOrderQuantities");

		if (allowedOrderQuantities != null) {
			setAllowedOrderQuantities(allowedOrderQuantities);
		}

		Boolean backOrders = (Boolean)attributes.get("backOrders");

		if (backOrders != null) {
			setBackOrders(backOrders);
		}

		Long commerceAvailabilityEstimateId = (Long)attributes.get(
			"commerceAvailabilityEstimateId");

		if (commerceAvailabilityEstimateId != null) {
			setCommerceAvailabilityEstimateId(commerceAvailabilityEstimateId);
		}

		String CPDefinitionInventoryEngine = (String)attributes.get(
			"CPDefinitionInventoryEngine");

		if (CPDefinitionInventoryEngine != null) {
			setCPDefinitionInventoryEngine(CPDefinitionInventoryEngine);
		}

		Double depth = (Double)attributes.get("depth");

		if (depth != null) {
			setDepth(depth);
		}

		Boolean displayAvailability = (Boolean)attributes.get(
			"displayAvailability");

		if (displayAvailability != null) {
			setDisplayAvailability(displayAvailability);
		}

		Boolean displayStockQuantity = (Boolean)attributes.get(
			"displayStockQuantity");

		if (displayStockQuantity != null) {
			setDisplayStockQuantity(displayStockQuantity);
		}

		Boolean freeShipping = (Boolean)attributes.get("freeShipping");

		if (freeShipping != null) {
			setFreeShipping(freeShipping);
		}

		Double height = (Double)attributes.get("height");

		if (height != null) {
			setHeight(height);
		}

		String lowStockActivity = (String)attributes.get("lowStockActivity");

		if (lowStockActivity != null) {
			setLowStockActivity(lowStockActivity);
		}

		BigDecimal maxOrderQuantity = (BigDecimal)attributes.get(
			"maxOrderQuantity");

		if (maxOrderQuantity != null) {
			setMaxOrderQuantity(maxOrderQuantity);
		}

		BigDecimal minOrderQuantity = (BigDecimal)attributes.get(
			"minOrderQuantity");

		if (minOrderQuantity != null) {
			setMinOrderQuantity(minOrderQuantity);
		}

		BigDecimal minStockQuantity = (BigDecimal)attributes.get(
			"minStockQuantity");

		if (minStockQuantity != null) {
			setMinStockQuantity(minStockQuantity);
		}

		BigDecimal multipleOrderQuantity = (BigDecimal)attributes.get(
			"multipleOrderQuantity");

		if (multipleOrderQuantity != null) {
			setMultipleOrderQuantity(multipleOrderQuantity);
		}

		Boolean purchasable = (Boolean)attributes.get("purchasable");

		if (purchasable != null) {
			setPurchasable(purchasable);
		}

		Boolean shippable = (Boolean)attributes.get("shippable");

		if (shippable != null) {
			setShippable(shippable);
		}

		Double shippingExtraPrice = (Double)attributes.get(
			"shippingExtraPrice");

		if (shippingExtraPrice != null) {
			setShippingExtraPrice(shippingExtraPrice);
		}

		Boolean shipSeparately = (Boolean)attributes.get("shipSeparately");

		if (shipSeparately != null) {
			setShipSeparately(shipSeparately);
		}

		Boolean taxExempt = (Boolean)attributes.get("taxExempt");

		if (taxExempt != null) {
			setTaxExempt(taxExempt);
		}

		Boolean visible = (Boolean)attributes.get("visible");

		if (visible != null) {
			setVisible(visible);
		}

		Double weight = (Double)attributes.get("weight");

		if (weight != null) {
			setWeight(weight);
		}

		Double width = (Double)attributes.get("width");

		if (width != null) {
			setWidth(width);
		}
	}

	@Override
	public CPConfigurationEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the allowed order quantities of this cp configuration entry.
	 *
	 * @return the allowed order quantities of this cp configuration entry
	 */
	@Override
	public String getAllowedOrderQuantities() {
		return model.getAllowedOrderQuantities();
	}

	@Override
	public BigDecimal[] getAllowedOrderQuantitiesArray() {
		return model.getAllowedOrderQuantitiesArray();
	}

	/**
	 * Returns the back orders of this cp configuration entry.
	 *
	 * @return the back orders of this cp configuration entry
	 */
	@Override
	public boolean getBackOrders() {
		return model.getBackOrders();
	}

	/**
	 * Returns the fully qualified class name of this cp configuration entry.
	 *
	 * @return the fully qualified class name of this cp configuration entry
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this cp configuration entry.
	 *
	 * @return the class name ID of this cp configuration entry
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this cp configuration entry.
	 *
	 * @return the class pk of this cp configuration entry
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the commerce availability estimate ID of this cp configuration entry.
	 *
	 * @return the commerce availability estimate ID of this cp configuration entry
	 */
	@Override
	public long getCommerceAvailabilityEstimateId() {
		return model.getCommerceAvailabilityEstimateId();
	}

	/**
	 * Returns the company ID of this cp configuration entry.
	 *
	 * @return the company ID of this cp configuration entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the cp configuration entry ID of this cp configuration entry.
	 *
	 * @return the cp configuration entry ID of this cp configuration entry
	 */
	@Override
	public long getCPConfigurationEntryId() {
		return model.getCPConfigurationEntryId();
	}

	/**
	 * Returns the cp configuration list ID of this cp configuration entry.
	 *
	 * @return the cp configuration list ID of this cp configuration entry
	 */
	@Override
	public long getCPConfigurationListId() {
		return model.getCPConfigurationListId();
	}

	/**
	 * Returns the cp definition inventory engine of this cp configuration entry.
	 *
	 * @return the cp definition inventory engine of this cp configuration entry
	 */
	@Override
	public String getCPDefinitionInventoryEngine() {
		return model.getCPDefinitionInventoryEngine();
	}

	@Override
	public CPTaxCategory getCPTaxCategory()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCPTaxCategory();
	}

	/**
	 * Returns the cp tax category ID of this cp configuration entry.
	 *
	 * @return the cp tax category ID of this cp configuration entry
	 */
	@Override
	public long getCPTaxCategoryId() {
		return model.getCPTaxCategoryId();
	}

	/**
	 * Returns the create date of this cp configuration entry.
	 *
	 * @return the create date of this cp configuration entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this cp configuration entry.
	 *
	 * @return the ct collection ID of this cp configuration entry
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the depth of this cp configuration entry.
	 *
	 * @return the depth of this cp configuration entry
	 */
	@Override
	public double getDepth() {
		return model.getDepth();
	}

	/**
	 * Returns the display availability of this cp configuration entry.
	 *
	 * @return the display availability of this cp configuration entry
	 */
	@Override
	public boolean getDisplayAvailability() {
		return model.getDisplayAvailability();
	}

	/**
	 * Returns the display stock quantity of this cp configuration entry.
	 *
	 * @return the display stock quantity of this cp configuration entry
	 */
	@Override
	public boolean getDisplayStockQuantity() {
		return model.getDisplayStockQuantity();
	}

	/**
	 * Returns the external reference code of this cp configuration entry.
	 *
	 * @return the external reference code of this cp configuration entry
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the free shipping of this cp configuration entry.
	 *
	 * @return the free shipping of this cp configuration entry
	 */
	@Override
	public boolean getFreeShipping() {
		return model.getFreeShipping();
	}

	/**
	 * Returns the group ID of this cp configuration entry.
	 *
	 * @return the group ID of this cp configuration entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the height of this cp configuration entry.
	 *
	 * @return the height of this cp configuration entry
	 */
	@Override
	public double getHeight() {
		return model.getHeight();
	}

	/**
	 * Returns the low stock activity of this cp configuration entry.
	 *
	 * @return the low stock activity of this cp configuration entry
	 */
	@Override
	public String getLowStockActivity() {
		return model.getLowStockActivity();
	}

	/**
	 * Returns the max order quantity of this cp configuration entry.
	 *
	 * @return the max order quantity of this cp configuration entry
	 */
	@Override
	public BigDecimal getMaxOrderQuantity() {
		return model.getMaxOrderQuantity();
	}

	/**
	 * Returns the min order quantity of this cp configuration entry.
	 *
	 * @return the min order quantity of this cp configuration entry
	 */
	@Override
	public BigDecimal getMinOrderQuantity() {
		return model.getMinOrderQuantity();
	}

	/**
	 * Returns the min stock quantity of this cp configuration entry.
	 *
	 * @return the min stock quantity of this cp configuration entry
	 */
	@Override
	public BigDecimal getMinStockQuantity() {
		return model.getMinStockQuantity();
	}

	/**
	 * Returns the modified date of this cp configuration entry.
	 *
	 * @return the modified date of this cp configuration entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the multiple order quantity of this cp configuration entry.
	 *
	 * @return the multiple order quantity of this cp configuration entry
	 */
	@Override
	public BigDecimal getMultipleOrderQuantity() {
		return model.getMultipleOrderQuantity();
	}

	/**
	 * Returns the mvcc version of this cp configuration entry.
	 *
	 * @return the mvcc version of this cp configuration entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	@Override
	public CPConfigurationList getParentCPConfigurationList()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getParentCPConfigurationList();
	}

	/**
	 * Returns the primary key of this cp configuration entry.
	 *
	 * @return the primary key of this cp configuration entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the purchasable of this cp configuration entry.
	 *
	 * @return the purchasable of this cp configuration entry
	 */
	@Override
	public boolean getPurchasable() {
		return model.getPurchasable();
	}

	/**
	 * Returns the shippable of this cp configuration entry.
	 *
	 * @return the shippable of this cp configuration entry
	 */
	@Override
	public boolean getShippable() {
		return model.getShippable();
	}

	/**
	 * Returns the shipping extra price of this cp configuration entry.
	 *
	 * @return the shipping extra price of this cp configuration entry
	 */
	@Override
	public double getShippingExtraPrice() {
		return model.getShippingExtraPrice();
	}

	/**
	 * Returns the ship separately of this cp configuration entry.
	 *
	 * @return the ship separately of this cp configuration entry
	 */
	@Override
	public boolean getShipSeparately() {
		return model.getShipSeparately();
	}

	/**
	 * Returns the tax exempt of this cp configuration entry.
	 *
	 * @return the tax exempt of this cp configuration entry
	 */
	@Override
	public boolean getTaxExempt() {
		return model.getTaxExempt();
	}

	/**
	 * Returns the user ID of this cp configuration entry.
	 *
	 * @return the user ID of this cp configuration entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this cp configuration entry.
	 *
	 * @return the user name of this cp configuration entry
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this cp configuration entry.
	 *
	 * @return the user uuid of this cp configuration entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this cp configuration entry.
	 *
	 * @return the uuid of this cp configuration entry
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the visible of this cp configuration entry.
	 *
	 * @return the visible of this cp configuration entry
	 */
	@Override
	public boolean getVisible() {
		return model.getVisible();
	}

	/**
	 * Returns the weight of this cp configuration entry.
	 *
	 * @return the weight of this cp configuration entry
	 */
	@Override
	public double getWeight() {
		return model.getWeight();
	}

	/**
	 * Returns the width of this cp configuration entry.
	 *
	 * @return the width of this cp configuration entry
	 */
	@Override
	public double getWidth() {
		return model.getWidth();
	}

	/**
	 * Returns <code>true</code> if this cp configuration entry is back orders.
	 *
	 * @return <code>true</code> if this cp configuration entry is back orders; <code>false</code> otherwise
	 */
	@Override
	public boolean isBackOrders() {
		return model.isBackOrders();
	}

	/**
	 * Returns <code>true</code> if this cp configuration entry is display availability.
	 *
	 * @return <code>true</code> if this cp configuration entry is display availability; <code>false</code> otherwise
	 */
	@Override
	public boolean isDisplayAvailability() {
		return model.isDisplayAvailability();
	}

	/**
	 * Returns <code>true</code> if this cp configuration entry is display stock quantity.
	 *
	 * @return <code>true</code> if this cp configuration entry is display stock quantity; <code>false</code> otherwise
	 */
	@Override
	public boolean isDisplayStockQuantity() {
		return model.isDisplayStockQuantity();
	}

	/**
	 * Returns <code>true</code> if this cp configuration entry is free shipping.
	 *
	 * @return <code>true</code> if this cp configuration entry is free shipping; <code>false</code> otherwise
	 */
	@Override
	public boolean isFreeShipping() {
		return model.isFreeShipping();
	}

	@Override
	public boolean isMaster()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isMaster();
	}

	/**
	 * Returns <code>true</code> if this cp configuration entry is purchasable.
	 *
	 * @return <code>true</code> if this cp configuration entry is purchasable; <code>false</code> otherwise
	 */
	@Override
	public boolean isPurchasable() {
		return model.isPurchasable();
	}

	/**
	 * Returns <code>true</code> if this cp configuration entry is shippable.
	 *
	 * @return <code>true</code> if this cp configuration entry is shippable; <code>false</code> otherwise
	 */
	@Override
	public boolean isShippable() {
		return model.isShippable();
	}

	/**
	 * Returns <code>true</code> if this cp configuration entry is ship separately.
	 *
	 * @return <code>true</code> if this cp configuration entry is ship separately; <code>false</code> otherwise
	 */
	@Override
	public boolean isShipSeparately() {
		return model.isShipSeparately();
	}

	/**
	 * Returns <code>true</code> if this cp configuration entry is tax exempt.
	 *
	 * @return <code>true</code> if this cp configuration entry is tax exempt; <code>false</code> otherwise
	 */
	@Override
	public boolean isTaxExempt() {
		return model.isTaxExempt();
	}

	/**
	 * Returns <code>true</code> if this cp configuration entry is visible.
	 *
	 * @return <code>true</code> if this cp configuration entry is visible; <code>false</code> otherwise
	 */
	@Override
	public boolean isVisible() {
		return model.isVisible();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the allowed order quantities of this cp configuration entry.
	 *
	 * @param allowedOrderQuantities the allowed order quantities of this cp configuration entry
	 */
	@Override
	public void setAllowedOrderQuantities(String allowedOrderQuantities) {
		model.setAllowedOrderQuantities(allowedOrderQuantities);
	}

	/**
	 * Sets whether this cp configuration entry is back orders.
	 *
	 * @param backOrders the back orders of this cp configuration entry
	 */
	@Override
	public void setBackOrders(boolean backOrders) {
		model.setBackOrders(backOrders);
	}

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this cp configuration entry.
	 *
	 * @param classNameId the class name ID of this cp configuration entry
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this cp configuration entry.
	 *
	 * @param classPK the class pk of this cp configuration entry
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the commerce availability estimate ID of this cp configuration entry.
	 *
	 * @param commerceAvailabilityEstimateId the commerce availability estimate ID of this cp configuration entry
	 */
	@Override
	public void setCommerceAvailabilityEstimateId(
		long commerceAvailabilityEstimateId) {

		model.setCommerceAvailabilityEstimateId(commerceAvailabilityEstimateId);
	}

	/**
	 * Sets the company ID of this cp configuration entry.
	 *
	 * @param companyId the company ID of this cp configuration entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cp configuration entry ID of this cp configuration entry.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID of this cp configuration entry
	 */
	@Override
	public void setCPConfigurationEntryId(long CPConfigurationEntryId) {
		model.setCPConfigurationEntryId(CPConfigurationEntryId);
	}

	/**
	 * Sets the cp configuration list ID of this cp configuration entry.
	 *
	 * @param CPConfigurationListId the cp configuration list ID of this cp configuration entry
	 */
	@Override
	public void setCPConfigurationListId(long CPConfigurationListId) {
		model.setCPConfigurationListId(CPConfigurationListId);
	}

	/**
	 * Sets the cp definition inventory engine of this cp configuration entry.
	 *
	 * @param CPDefinitionInventoryEngine the cp definition inventory engine of this cp configuration entry
	 */
	@Override
	public void setCPDefinitionInventoryEngine(
		String CPDefinitionInventoryEngine) {

		model.setCPDefinitionInventoryEngine(CPDefinitionInventoryEngine);
	}

	/**
	 * Sets the cp tax category ID of this cp configuration entry.
	 *
	 * @param CPTaxCategoryId the cp tax category ID of this cp configuration entry
	 */
	@Override
	public void setCPTaxCategoryId(long CPTaxCategoryId) {
		model.setCPTaxCategoryId(CPTaxCategoryId);
	}

	/**
	 * Sets the create date of this cp configuration entry.
	 *
	 * @param createDate the create date of this cp configuration entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this cp configuration entry.
	 *
	 * @param ctCollectionId the ct collection ID of this cp configuration entry
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the depth of this cp configuration entry.
	 *
	 * @param depth the depth of this cp configuration entry
	 */
	@Override
	public void setDepth(double depth) {
		model.setDepth(depth);
	}

	/**
	 * Sets whether this cp configuration entry is display availability.
	 *
	 * @param displayAvailability the display availability of this cp configuration entry
	 */
	@Override
	public void setDisplayAvailability(boolean displayAvailability) {
		model.setDisplayAvailability(displayAvailability);
	}

	/**
	 * Sets whether this cp configuration entry is display stock quantity.
	 *
	 * @param displayStockQuantity the display stock quantity of this cp configuration entry
	 */
	@Override
	public void setDisplayStockQuantity(boolean displayStockQuantity) {
		model.setDisplayStockQuantity(displayStockQuantity);
	}

	/**
	 * Sets the external reference code of this cp configuration entry.
	 *
	 * @param externalReferenceCode the external reference code of this cp configuration entry
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets whether this cp configuration entry is free shipping.
	 *
	 * @param freeShipping the free shipping of this cp configuration entry
	 */
	@Override
	public void setFreeShipping(boolean freeShipping) {
		model.setFreeShipping(freeShipping);
	}

	/**
	 * Sets the group ID of this cp configuration entry.
	 *
	 * @param groupId the group ID of this cp configuration entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the height of this cp configuration entry.
	 *
	 * @param height the height of this cp configuration entry
	 */
	@Override
	public void setHeight(double height) {
		model.setHeight(height);
	}

	/**
	 * Sets the low stock activity of this cp configuration entry.
	 *
	 * @param lowStockActivity the low stock activity of this cp configuration entry
	 */
	@Override
	public void setLowStockActivity(String lowStockActivity) {
		model.setLowStockActivity(lowStockActivity);
	}

	/**
	 * Sets the max order quantity of this cp configuration entry.
	 *
	 * @param maxOrderQuantity the max order quantity of this cp configuration entry
	 */
	@Override
	public void setMaxOrderQuantity(BigDecimal maxOrderQuantity) {
		model.setMaxOrderQuantity(maxOrderQuantity);
	}

	/**
	 * Sets the min order quantity of this cp configuration entry.
	 *
	 * @param minOrderQuantity the min order quantity of this cp configuration entry
	 */
	@Override
	public void setMinOrderQuantity(BigDecimal minOrderQuantity) {
		model.setMinOrderQuantity(minOrderQuantity);
	}

	/**
	 * Sets the min stock quantity of this cp configuration entry.
	 *
	 * @param minStockQuantity the min stock quantity of this cp configuration entry
	 */
	@Override
	public void setMinStockQuantity(BigDecimal minStockQuantity) {
		model.setMinStockQuantity(minStockQuantity);
	}

	/**
	 * Sets the modified date of this cp configuration entry.
	 *
	 * @param modifiedDate the modified date of this cp configuration entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the multiple order quantity of this cp configuration entry.
	 *
	 * @param multipleOrderQuantity the multiple order quantity of this cp configuration entry
	 */
	@Override
	public void setMultipleOrderQuantity(BigDecimal multipleOrderQuantity) {
		model.setMultipleOrderQuantity(multipleOrderQuantity);
	}

	/**
	 * Sets the mvcc version of this cp configuration entry.
	 *
	 * @param mvccVersion the mvcc version of this cp configuration entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this cp configuration entry.
	 *
	 * @param primaryKey the primary key of this cp configuration entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets whether this cp configuration entry is purchasable.
	 *
	 * @param purchasable the purchasable of this cp configuration entry
	 */
	@Override
	public void setPurchasable(boolean purchasable) {
		model.setPurchasable(purchasable);
	}

	/**
	 * Sets whether this cp configuration entry is shippable.
	 *
	 * @param shippable the shippable of this cp configuration entry
	 */
	@Override
	public void setShippable(boolean shippable) {
		model.setShippable(shippable);
	}

	/**
	 * Sets the shipping extra price of this cp configuration entry.
	 *
	 * @param shippingExtraPrice the shipping extra price of this cp configuration entry
	 */
	@Override
	public void setShippingExtraPrice(double shippingExtraPrice) {
		model.setShippingExtraPrice(shippingExtraPrice);
	}

	/**
	 * Sets whether this cp configuration entry is ship separately.
	 *
	 * @param shipSeparately the ship separately of this cp configuration entry
	 */
	@Override
	public void setShipSeparately(boolean shipSeparately) {
		model.setShipSeparately(shipSeparately);
	}

	/**
	 * Sets whether this cp configuration entry is tax exempt.
	 *
	 * @param taxExempt the tax exempt of this cp configuration entry
	 */
	@Override
	public void setTaxExempt(boolean taxExempt) {
		model.setTaxExempt(taxExempt);
	}

	/**
	 * Sets the user ID of this cp configuration entry.
	 *
	 * @param userId the user ID of this cp configuration entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this cp configuration entry.
	 *
	 * @param userName the user name of this cp configuration entry
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this cp configuration entry.
	 *
	 * @param userUuid the user uuid of this cp configuration entry
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this cp configuration entry.
	 *
	 * @param uuid the uuid of this cp configuration entry
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets whether this cp configuration entry is visible.
	 *
	 * @param visible the visible of this cp configuration entry
	 */
	@Override
	public void setVisible(boolean visible) {
		model.setVisible(visible);
	}

	/**
	 * Sets the weight of this cp configuration entry.
	 *
	 * @param weight the weight of this cp configuration entry
	 */
	@Override
	public void setWeight(double weight) {
		model.setWeight(weight);
	}

	/**
	 * Sets the width of this cp configuration entry.
	 *
	 * @param width the width of this cp configuration entry
	 */
	@Override
	public void setWidth(double width) {
		model.setWidth(width);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<CPConfigurationEntry, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CPConfigurationEntry, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CPConfigurationEntryWrapper wrap(
		CPConfigurationEntry cpConfigurationEntry) {

		return new CPConfigurationEntryWrapper(cpConfigurationEntry);
	}

}