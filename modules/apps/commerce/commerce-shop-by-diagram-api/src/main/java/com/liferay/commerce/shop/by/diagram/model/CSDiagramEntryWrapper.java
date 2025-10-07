/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link CSDiagramEntry}.
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramEntry
 * @generated
 */
public class CSDiagramEntryWrapper
	extends BaseModelWrapper<CSDiagramEntry>
	implements CSDiagramEntry, ModelWrapper<CSDiagramEntry> {

	public CSDiagramEntryWrapper(CSDiagramEntry csDiagramEntry) {
		super(csDiagramEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("CSDiagramEntryId", getCSDiagramEntryId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("CPDefinitionId", getCPDefinitionId());
		attributes.put("CPInstanceId", getCPInstanceId());
		attributes.put("CProductId", getCProductId());
		attributes.put("diagram", isDiagram());
		attributes.put("quantity", getQuantity());
		attributes.put("sequence", getSequence());
		attributes.put("sku", getSku());

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

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long CSDiagramEntryId = (Long)attributes.get("CSDiagramEntryId");

		if (CSDiagramEntryId != null) {
			setCSDiagramEntryId(CSDiagramEntryId);
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

		Long CPDefinitionId = (Long)attributes.get("CPDefinitionId");

		if (CPDefinitionId != null) {
			setCPDefinitionId(CPDefinitionId);
		}

		Long CPInstanceId = (Long)attributes.get("CPInstanceId");

		if (CPInstanceId != null) {
			setCPInstanceId(CPInstanceId);
		}

		Long CProductId = (Long)attributes.get("CProductId");

		if (CProductId != null) {
			setCProductId(CProductId);
		}

		Boolean diagram = (Boolean)attributes.get("diagram");

		if (diagram != null) {
			setDiagram(diagram);
		}

		Integer quantity = (Integer)attributes.get("quantity");

		if (quantity != null) {
			setQuantity(quantity);
		}

		String sequence = (String)attributes.get("sequence");

		if (sequence != null) {
			setSequence(sequence);
		}

		String sku = (String)attributes.get("sku");

		if (sku != null) {
			setSku(sku);
		}
	}

	@Override
	public CSDiagramEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this cs diagram entry.
	 *
	 * @return the company ID of this cs diagram entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	@Override
	public com.liferay.commerce.product.model.CPDefinition getCPDefinition()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCPDefinition();
	}

	/**
	 * Returns the cp definition ID of this cs diagram entry.
	 *
	 * @return the cp definition ID of this cs diagram entry
	 */
	@Override
	public long getCPDefinitionId() {
		return model.getCPDefinitionId();
	}

	/**
	 * Returns the cp instance ID of this cs diagram entry.
	 *
	 * @return the cp instance ID of this cs diagram entry
	 */
	@Override
	public long getCPInstanceId() {
		return model.getCPInstanceId();
	}

	/**
	 * Returns the c product ID of this cs diagram entry.
	 *
	 * @return the c product ID of this cs diagram entry
	 */
	@Override
	public long getCProductId() {
		return model.getCProductId();
	}

	/**
	 * Returns the create date of this cs diagram entry.
	 *
	 * @return the create date of this cs diagram entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the cs diagram entry ID of this cs diagram entry.
	 *
	 * @return the cs diagram entry ID of this cs diagram entry
	 */
	@Override
	public long getCSDiagramEntryId() {
		return model.getCSDiagramEntryId();
	}

	/**
	 * Returns the ct collection ID of this cs diagram entry.
	 *
	 * @return the ct collection ID of this cs diagram entry
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the diagram of this cs diagram entry.
	 *
	 * @return the diagram of this cs diagram entry
	 */
	@Override
	public boolean getDiagram() {
		return model.getDiagram();
	}

	/**
	 * Returns the external reference code of this cs diagram entry.
	 *
	 * @return the external reference code of this cs diagram entry
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the modified date of this cs diagram entry.
	 *
	 * @return the modified date of this cs diagram entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this cs diagram entry.
	 *
	 * @return the mvcc version of this cs diagram entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this cs diagram entry.
	 *
	 * @return the primary key of this cs diagram entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the quantity of this cs diagram entry.
	 *
	 * @return the quantity of this cs diagram entry
	 */
	@Override
	public int getQuantity() {
		return model.getQuantity();
	}

	/**
	 * Returns the sequence of this cs diagram entry.
	 *
	 * @return the sequence of this cs diagram entry
	 */
	@Override
	public String getSequence() {
		return model.getSequence();
	}

	/**
	 * Returns the sku of this cs diagram entry.
	 *
	 * @return the sku of this cs diagram entry
	 */
	@Override
	public String getSku() {
		return model.getSku();
	}

	/**
	 * Returns the user ID of this cs diagram entry.
	 *
	 * @return the user ID of this cs diagram entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this cs diagram entry.
	 *
	 * @return the user name of this cs diagram entry
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this cs diagram entry.
	 *
	 * @return the user uuid of this cs diagram entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this cs diagram entry is diagram.
	 *
	 * @return <code>true</code> if this cs diagram entry is diagram; <code>false</code> otherwise
	 */
	@Override
	public boolean isDiagram() {
		return model.isDiagram();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this cs diagram entry.
	 *
	 * @param companyId the company ID of this cs diagram entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cp definition ID of this cs diagram entry.
	 *
	 * @param CPDefinitionId the cp definition ID of this cs diagram entry
	 */
	@Override
	public void setCPDefinitionId(long CPDefinitionId) {
		model.setCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Sets the cp instance ID of this cs diagram entry.
	 *
	 * @param CPInstanceId the cp instance ID of this cs diagram entry
	 */
	@Override
	public void setCPInstanceId(long CPInstanceId) {
		model.setCPInstanceId(CPInstanceId);
	}

	/**
	 * Sets the c product ID of this cs diagram entry.
	 *
	 * @param CProductId the c product ID of this cs diagram entry
	 */
	@Override
	public void setCProductId(long CProductId) {
		model.setCProductId(CProductId);
	}

	/**
	 * Sets the create date of this cs diagram entry.
	 *
	 * @param createDate the create date of this cs diagram entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the cs diagram entry ID of this cs diagram entry.
	 *
	 * @param CSDiagramEntryId the cs diagram entry ID of this cs diagram entry
	 */
	@Override
	public void setCSDiagramEntryId(long CSDiagramEntryId) {
		model.setCSDiagramEntryId(CSDiagramEntryId);
	}

	/**
	 * Sets the ct collection ID of this cs diagram entry.
	 *
	 * @param ctCollectionId the ct collection ID of this cs diagram entry
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets whether this cs diagram entry is diagram.
	 *
	 * @param diagram the diagram of this cs diagram entry
	 */
	@Override
	public void setDiagram(boolean diagram) {
		model.setDiagram(diagram);
	}

	/**
	 * Sets the external reference code of this cs diagram entry.
	 *
	 * @param externalReferenceCode the external reference code of this cs diagram entry
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the modified date of this cs diagram entry.
	 *
	 * @param modifiedDate the modified date of this cs diagram entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this cs diagram entry.
	 *
	 * @param mvccVersion the mvcc version of this cs diagram entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this cs diagram entry.
	 *
	 * @param primaryKey the primary key of this cs diagram entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the quantity of this cs diagram entry.
	 *
	 * @param quantity the quantity of this cs diagram entry
	 */
	@Override
	public void setQuantity(int quantity) {
		model.setQuantity(quantity);
	}

	/**
	 * Sets the sequence of this cs diagram entry.
	 *
	 * @param sequence the sequence of this cs diagram entry
	 */
	@Override
	public void setSequence(String sequence) {
		model.setSequence(sequence);
	}

	/**
	 * Sets the sku of this cs diagram entry.
	 *
	 * @param sku the sku of this cs diagram entry
	 */
	@Override
	public void setSku(String sku) {
		model.setSku(sku);
	}

	/**
	 * Sets the user ID of this cs diagram entry.
	 *
	 * @param userId the user ID of this cs diagram entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this cs diagram entry.
	 *
	 * @param userName the user name of this cs diagram entry
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this cs diagram entry.
	 *
	 * @param userUuid the user uuid of this cs diagram entry
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<CSDiagramEntry, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CSDiagramEntry, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected CSDiagramEntryWrapper wrap(CSDiagramEntry csDiagramEntry) {
		return new CSDiagramEntryWrapper(csDiagramEntry);
	}

}