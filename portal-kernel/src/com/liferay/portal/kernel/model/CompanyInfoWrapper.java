/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CompanyInfo}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CompanyInfo
 * @generated
 */
public class CompanyInfoWrapper
	extends BaseModelWrapper<CompanyInfo>
	implements CompanyInfo, ModelWrapper<CompanyInfo> {

	public CompanyInfoWrapper(CompanyInfo companyInfo) {
		super(companyInfo);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("companyInfoId", getCompanyInfoId());
		attributes.put("companyId", getCompanyId());
		attributes.put("homeURL", getHomeURL());
		attributes.put("indexNameCurrent", getIndexNameCurrent());
		attributes.put("indexNameNext", getIndexNameNext());
		attributes.put("industry", getIndustry());
		attributes.put("key", getKey());
		attributes.put("legalId", getLegalId());
		attributes.put("legalName", getLegalName());
		attributes.put("legalType", getLegalType());
		attributes.put("logoId", getLogoId());
		attributes.put("name", getName());
		attributes.put("sicCode", getSicCode());
		attributes.put("size", getSize());
		attributes.put("tickerSymbol", getTickerSymbol());
		attributes.put("type", getType());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long companyInfoId = (Long)attributes.get("companyInfoId");

		if (companyInfoId != null) {
			setCompanyInfoId(companyInfoId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		String homeURL = (String)attributes.get("homeURL");

		if (homeURL != null) {
			setHomeURL(homeURL);
		}

		String indexNameCurrent = (String)attributes.get("indexNameCurrent");

		if (indexNameCurrent != null) {
			setIndexNameCurrent(indexNameCurrent);
		}

		String indexNameNext = (String)attributes.get("indexNameNext");

		if (indexNameNext != null) {
			setIndexNameNext(indexNameNext);
		}

		String industry = (String)attributes.get("industry");

		if (industry != null) {
			setIndustry(industry);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		String legalId = (String)attributes.get("legalId");

		if (legalId != null) {
			setLegalId(legalId);
		}

		String legalName = (String)attributes.get("legalName");

		if (legalName != null) {
			setLegalName(legalName);
		}

		String legalType = (String)attributes.get("legalType");

		if (legalType != null) {
			setLegalType(legalType);
		}

		Long logoId = (Long)attributes.get("logoId");

		if (logoId != null) {
			setLogoId(logoId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String sicCode = (String)attributes.get("sicCode");

		if (sicCode != null) {
			setSicCode(sicCode);
		}

		String size = (String)attributes.get("size");

		if (size != null) {
			setSize(size);
		}

		String tickerSymbol = (String)attributes.get("tickerSymbol");

		if (tickerSymbol != null) {
			setTickerSymbol(tickerSymbol);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}
	}

	@Override
	public CompanyInfo cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this company info.
	 *
	 * @return the company ID of this company info
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the company info ID of this company info.
	 *
	 * @return the company info ID of this company info
	 */
	@Override
	public long getCompanyInfoId() {
		return model.getCompanyInfoId();
	}

	/**
	 * Returns the home url of this company info.
	 *
	 * @return the home url of this company info
	 */
	@Override
	public String getHomeURL() {
		return model.getHomeURL();
	}

	/**
	 * Returns the index name current of this company info.
	 *
	 * @return the index name current of this company info
	 */
	@Override
	public String getIndexNameCurrent() {
		return model.getIndexNameCurrent();
	}

	/**
	 * Returns the index name next of this company info.
	 *
	 * @return the index name next of this company info
	 */
	@Override
	public String getIndexNameNext() {
		return model.getIndexNameNext();
	}

	/**
	 * Returns the industry of this company info.
	 *
	 * @return the industry of this company info
	 */
	@Override
	public String getIndustry() {
		return model.getIndustry();
	}

	/**
	 * Returns the key of this company info.
	 *
	 * @return the key of this company info
	 */
	@Override
	public String getKey() {
		return model.getKey();
	}

	/**
	 * Returns the legal ID of this company info.
	 *
	 * @return the legal ID of this company info
	 */
	@Override
	public String getLegalId() {
		return model.getLegalId();
	}

	/**
	 * Returns the legal name of this company info.
	 *
	 * @return the legal name of this company info
	 */
	@Override
	public String getLegalName() {
		return model.getLegalName();
	}

	/**
	 * Returns the legal type of this company info.
	 *
	 * @return the legal type of this company info
	 */
	@Override
	public String getLegalType() {
		return model.getLegalType();
	}

	/**
	 * Returns the logo ID of this company info.
	 *
	 * @return the logo ID of this company info
	 */
	@Override
	public long getLogoId() {
		return model.getLogoId();
	}

	/**
	 * Returns the mvcc version of this company info.
	 *
	 * @return the mvcc version of this company info
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this company info.
	 *
	 * @return the name of this company info
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this company info.
	 *
	 * @return the primary key of this company info
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the sic code of this company info.
	 *
	 * @return the sic code of this company info
	 */
	@Override
	public String getSicCode() {
		return model.getSicCode();
	}

	/**
	 * Returns the size of this company info.
	 *
	 * @return the size of this company info
	 */
	@Override
	public String getSize() {
		return model.getSize();
	}

	/**
	 * Returns the ticker symbol of this company info.
	 *
	 * @return the ticker symbol of this company info
	 */
	@Override
	public String getTickerSymbol() {
		return model.getTickerSymbol();
	}

	/**
	 * Returns the type of this company info.
	 *
	 * @return the type of this company info
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this company info.
	 *
	 * @param companyId the company ID of this company info
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the company info ID of this company info.
	 *
	 * @param companyInfoId the company info ID of this company info
	 */
	@Override
	public void setCompanyInfoId(long companyInfoId) {
		model.setCompanyInfoId(companyInfoId);
	}

	/**
	 * Sets the home url of this company info.
	 *
	 * @param homeURL the home url of this company info
	 */
	@Override
	public void setHomeURL(String homeURL) {
		model.setHomeURL(homeURL);
	}

	/**
	 * Sets the index name current of this company info.
	 *
	 * @param indexNameCurrent the index name current of this company info
	 */
	@Override
	public void setIndexNameCurrent(String indexNameCurrent) {
		model.setIndexNameCurrent(indexNameCurrent);
	}

	/**
	 * Sets the index name next of this company info.
	 *
	 * @param indexNameNext the index name next of this company info
	 */
	@Override
	public void setIndexNameNext(String indexNameNext) {
		model.setIndexNameNext(indexNameNext);
	}

	/**
	 * Sets the industry of this company info.
	 *
	 * @param industry the industry of this company info
	 */
	@Override
	public void setIndustry(String industry) {
		model.setIndustry(industry);
	}

	/**
	 * Sets the key of this company info.
	 *
	 * @param key the key of this company info
	 */
	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	/**
	 * Sets the legal ID of this company info.
	 *
	 * @param legalId the legal ID of this company info
	 */
	@Override
	public void setLegalId(String legalId) {
		model.setLegalId(legalId);
	}

	/**
	 * Sets the legal name of this company info.
	 *
	 * @param legalName the legal name of this company info
	 */
	@Override
	public void setLegalName(String legalName) {
		model.setLegalName(legalName);
	}

	/**
	 * Sets the legal type of this company info.
	 *
	 * @param legalType the legal type of this company info
	 */
	@Override
	public void setLegalType(String legalType) {
		model.setLegalType(legalType);
	}

	/**
	 * Sets the logo ID of this company info.
	 *
	 * @param logoId the logo ID of this company info
	 */
	@Override
	public void setLogoId(long logoId) {
		model.setLogoId(logoId);
	}

	/**
	 * Sets the mvcc version of this company info.
	 *
	 * @param mvccVersion the mvcc version of this company info
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this company info.
	 *
	 * @param name the name of this company info
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this company info.
	 *
	 * @param primaryKey the primary key of this company info
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the sic code of this company info.
	 *
	 * @param sicCode the sic code of this company info
	 */
	@Override
	public void setSicCode(String sicCode) {
		model.setSicCode(sicCode);
	}

	/**
	 * Sets the size of this company info.
	 *
	 * @param size the size of this company info
	 */
	@Override
	public void setSize(String size) {
		model.setSize(size);
	}

	/**
	 * Sets the ticker symbol of this company info.
	 *
	 * @param tickerSymbol the ticker symbol of this company info
	 */
	@Override
	public void setTickerSymbol(String tickerSymbol) {
		model.setTickerSymbol(tickerSymbol);
	}

	/**
	 * Sets the type of this company info.
	 *
	 * @param type the type of this company info
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected CompanyInfoWrapper wrap(CompanyInfo companyInfo) {
		return new CompanyInfoWrapper(companyInfo);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:748823636