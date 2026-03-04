/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link CommercePriceList}.
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceList
 * @generated
 */
public class CommercePriceListWrapper
	extends BaseModelWrapper<CommercePriceList>
	implements CommercePriceList, ModelWrapper<CommercePriceList> {

	public CommercePriceListWrapper(CommercePriceList commercePriceList) {
		super(commercePriceList);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("commercePriceListId", getCommercePriceListId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("catalogBasePriceList", isCatalogBasePriceList());
		attributes.put("commerceCurrencyCode", getCommerceCurrencyCode());
		attributes.put("displayDate", getDisplayDate());
		attributes.put("expirationDate", getExpirationDate());
		attributes.put("name", getName());
		attributes.put("netPrice", isNetPrice());
		attributes.put(
			"parentCommercePriceListId", getParentCommercePriceListId());
		attributes.put("priority", getPriority());
		attributes.put("type", getType());
		attributes.put("lastPublishDate", getLastPublishDate());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

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

		Long commercePriceListId = (Long)attributes.get("commercePriceListId");

		if (commercePriceListId != null) {
			setCommercePriceListId(commercePriceListId);
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

		Boolean catalogBasePriceList = (Boolean)attributes.get(
			"catalogBasePriceList");

		if (catalogBasePriceList != null) {
			setCatalogBasePriceList(catalogBasePriceList);
		}

		String commerceCurrencyCode = (String)attributes.get(
			"commerceCurrencyCode");

		if (commerceCurrencyCode != null) {
			setCommerceCurrencyCode(commerceCurrencyCode);
		}

		Date displayDate = (Date)attributes.get("displayDate");

		if (displayDate != null) {
			setDisplayDate(displayDate);
		}

		Date expirationDate = (Date)attributes.get("expirationDate");

		if (expirationDate != null) {
			setExpirationDate(expirationDate);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Boolean netPrice = (Boolean)attributes.get("netPrice");

		if (netPrice != null) {
			setNetPrice(netPrice);
		}

		Long parentCommercePriceListId = (Long)attributes.get(
			"parentCommercePriceListId");

		if (parentCommercePriceListId != null) {
			setParentCommercePriceListId(parentCommercePriceListId);
		}

		Double priority = (Double)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		String statusByUserName = (String)attributes.get("statusByUserName");

		if (statusByUserName != null) {
			setStatusByUserName(statusByUserName);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	@Override
	public CommercePriceList cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public CommercePriceList fetchParentCommercePriceList() {
		return model.fetchParentCommercePriceList();
	}

	/**
	 * Returns the catalog base price list of this commerce price list.
	 *
	 * @return the catalog base price list of this commerce price list
	 */
	@Override
	public boolean getCatalogBasePriceList() {
		return model.getCatalogBasePriceList();
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			getCommerceCurrency()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCommerceCurrency();
	}

	/**
	 * Returns the commerce currency code of this commerce price list.
	 *
	 * @return the commerce currency code of this commerce price list
	 */
	@Override
	public String getCommerceCurrencyCode() {
		return model.getCommerceCurrencyCode();
	}

	/**
	 * Returns the commerce price list ID of this commerce price list.
	 *
	 * @return the commerce price list ID of this commerce price list
	 */
	@Override
	public long getCommercePriceListId() {
		return model.getCommercePriceListId();
	}

	/**
	 * Returns the company ID of this commerce price list.
	 *
	 * @return the company ID of this commerce price list
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this commerce price list.
	 *
	 * @return the create date of this commerce price list
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this commerce price list.
	 *
	 * @return the ct collection ID of this commerce price list
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the display date of this commerce price list.
	 *
	 * @return the display date of this commerce price list
	 */
	@Override
	public Date getDisplayDate() {
		return model.getDisplayDate();
	}

	/**
	 * Returns the expiration date of this commerce price list.
	 *
	 * @return the expiration date of this commerce price list
	 */
	@Override
	public Date getExpirationDate() {
		return model.getExpirationDate();
	}

	/**
	 * Returns the external reference code of this commerce price list.
	 *
	 * @return the external reference code of this commerce price list
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this commerce price list.
	 *
	 * @return the group ID of this commerce price list
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the last publish date of this commerce price list.
	 *
	 * @return the last publish date of this commerce price list
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the modified date of this commerce price list.
	 *
	 * @return the modified date of this commerce price list
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this commerce price list.
	 *
	 * @return the mvcc version of this commerce price list
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this commerce price list.
	 *
	 * @return the name of this commerce price list
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the net price of this commerce price list.
	 *
	 * @return the net price of this commerce price list
	 */
	@Override
	public boolean getNetPrice() {
		return model.getNetPrice();
	}

	/**
	 * Returns the parent commerce price list ID of this commerce price list.
	 *
	 * @return the parent commerce price list ID of this commerce price list
	 */
	@Override
	public long getParentCommercePriceListId() {
		return model.getParentCommercePriceListId();
	}

	/**
	 * Returns the primary key of this commerce price list.
	 *
	 * @return the primary key of this commerce price list
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this commerce price list.
	 *
	 * @return the priority of this commerce price list
	 */
	@Override
	public double getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the status of this commerce price list.
	 *
	 * @return the status of this commerce price list
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this commerce price list.
	 *
	 * @return the status by user ID of this commerce price list
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this commerce price list.
	 *
	 * @return the status by user name of this commerce price list
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this commerce price list.
	 *
	 * @return the status by user uuid of this commerce price list
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this commerce price list.
	 *
	 * @return the status date of this commerce price list
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the type of this commerce price list.
	 *
	 * @return the type of this commerce price list
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this commerce price list.
	 *
	 * @return the user ID of this commerce price list
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this commerce price list.
	 *
	 * @return the user name of this commerce price list
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this commerce price list.
	 *
	 * @return the user uuid of this commerce price list
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this commerce price list.
	 *
	 * @return the uuid of this commerce price list
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is approved.
	 *
	 * @return <code>true</code> if this commerce price list is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is catalog base price list.
	 *
	 * @return <code>true</code> if this commerce price list is catalog base price list; <code>false</code> otherwise
	 */
	@Override
	public boolean isCatalogBasePriceList() {
		return model.isCatalogBasePriceList();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is denied.
	 *
	 * @return <code>true</code> if this commerce price list is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is a draft.
	 *
	 * @return <code>true</code> if this commerce price list is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is expired.
	 *
	 * @return <code>true</code> if this commerce price list is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is inactive.
	 *
	 * @return <code>true</code> if this commerce price list is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is incomplete.
	 *
	 * @return <code>true</code> if this commerce price list is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is net price.
	 *
	 * @return <code>true</code> if this commerce price list is net price; <code>false</code> otherwise
	 */
	@Override
	public boolean isNetPrice() {
		return model.isNetPrice();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is pending.
	 *
	 * @return <code>true</code> if this commerce price list is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this commerce price list is scheduled.
	 *
	 * @return <code>true</code> if this commerce price list is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets whether this commerce price list is catalog base price list.
	 *
	 * @param catalogBasePriceList the catalog base price list of this commerce price list
	 */
	@Override
	public void setCatalogBasePriceList(boolean catalogBasePriceList) {
		model.setCatalogBasePriceList(catalogBasePriceList);
	}

	/**
	 * Sets the commerce currency code of this commerce price list.
	 *
	 * @param commerceCurrencyCode the commerce currency code of this commerce price list
	 */
	@Override
	public void setCommerceCurrencyCode(String commerceCurrencyCode) {
		model.setCommerceCurrencyCode(commerceCurrencyCode);
	}

	/**
	 * Sets the commerce price list ID of this commerce price list.
	 *
	 * @param commercePriceListId the commerce price list ID of this commerce price list
	 */
	@Override
	public void setCommercePriceListId(long commercePriceListId) {
		model.setCommercePriceListId(commercePriceListId);
	}

	/**
	 * Sets the company ID of this commerce price list.
	 *
	 * @param companyId the company ID of this commerce price list
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this commerce price list.
	 *
	 * @param createDate the create date of this commerce price list
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this commerce price list.
	 *
	 * @param ctCollectionId the ct collection ID of this commerce price list
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the display date of this commerce price list.
	 *
	 * @param displayDate the display date of this commerce price list
	 */
	@Override
	public void setDisplayDate(Date displayDate) {
		model.setDisplayDate(displayDate);
	}

	/**
	 * Sets the expiration date of this commerce price list.
	 *
	 * @param expirationDate the expiration date of this commerce price list
	 */
	@Override
	public void setExpirationDate(Date expirationDate) {
		model.setExpirationDate(expirationDate);
	}

	/**
	 * Sets the external reference code of this commerce price list.
	 *
	 * @param externalReferenceCode the external reference code of this commerce price list
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this commerce price list.
	 *
	 * @param groupId the group ID of this commerce price list
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the last publish date of this commerce price list.
	 *
	 * @param lastPublishDate the last publish date of this commerce price list
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this commerce price list.
	 *
	 * @param modifiedDate the modified date of this commerce price list
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this commerce price list.
	 *
	 * @param mvccVersion the mvcc version of this commerce price list
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this commerce price list.
	 *
	 * @param name the name of this commerce price list
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets whether this commerce price list is net price.
	 *
	 * @param netPrice the net price of this commerce price list
	 */
	@Override
	public void setNetPrice(boolean netPrice) {
		model.setNetPrice(netPrice);
	}

	/**
	 * Sets the parent commerce price list ID of this commerce price list.
	 *
	 * @param parentCommercePriceListId the parent commerce price list ID of this commerce price list
	 */
	@Override
	public void setParentCommercePriceListId(long parentCommercePriceListId) {
		model.setParentCommercePriceListId(parentCommercePriceListId);
	}

	/**
	 * Sets the primary key of this commerce price list.
	 *
	 * @param primaryKey the primary key of this commerce price list
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this commerce price list.
	 *
	 * @param priority the priority of this commerce price list
	 */
	@Override
	public void setPriority(double priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets the status of this commerce price list.
	 *
	 * @param status the status of this commerce price list
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this commerce price list.
	 *
	 * @param statusByUserId the status by user ID of this commerce price list
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this commerce price list.
	 *
	 * @param statusByUserName the status by user name of this commerce price list
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this commerce price list.
	 *
	 * @param statusByUserUuid the status by user uuid of this commerce price list
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this commerce price list.
	 *
	 * @param statusDate the status date of this commerce price list
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the type of this commerce price list.
	 *
	 * @param type the type of this commerce price list
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this commerce price list.
	 *
	 * @param userId the user ID of this commerce price list
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this commerce price list.
	 *
	 * @param userName the user name of this commerce price list
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this commerce price list.
	 *
	 * @param userUuid the user uuid of this commerce price list
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this commerce price list.
	 *
	 * @param uuid the uuid of this commerce price list
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<CommercePriceList, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CommercePriceList, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CommercePriceListWrapper wrap(
		CommercePriceList commercePriceList) {

		return new CommercePriceListWrapper(commercePriceList);
	}

}