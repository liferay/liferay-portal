/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.model;

import com.liferay.osb.faro.provisioning.client.constants.KoroneikiConstants;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.Account;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ExternalLink;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ProductPurchase;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Matthew Kong
 */
public class OSBAccountEntry {

	public OSBAccountEntry() {
	}

	public OSBAccountEntry(Account account) {
		_corpEntryName = account.getParentAccountKey();
		_corpProjectUuid = account.getKey();

		ExternalLink[] externalLinks = account.getExternalLinks();

		if (ArrayUtil.isNotEmpty(externalLinks)) {
			for (ExternalLink externalLink : externalLinks) {
				if (!StringUtil.equals(
						externalLink.getDomain(),
						KoroneikiConstants.DOMAIN_DOSSIERA) ||
					!StringUtil.equals(
						externalLink.getEntityId(),
						KoroneikiConstants.ENTITY_NAME_ACCOUNT)) {

					continue;
				}

				_dossieraAccountKey = externalLink.getEntityId();
			}
		}

		_name = account.getName();

		for (ProductPurchase productPurchase : account.getProductPurchases()) {
			_offeringEntries.add(new OSBOfferingEntry(productPurchase));
		}
	}

	public long getAccountEntryId() {
		return _accountEntryId;
	}

	public String getCode() {
		return _code;
	}

	public String getCorpEntryName() {
		return _corpEntryName;
	}

	public long getCorpProjectId() {
		return _corpProjectId;
	}

	public String getCorpProjectUuid() {
		return _corpProjectUuid;
	}

	public long getCountryId() {
		return _countryId;
	}

	public Date getCreateDate() {
		if (_createDate == null) {
			return null;
		}

		return new Date(_createDate.getTime());
	}

	public String getDossieraAccountKey() {
		return _dossieraAccountKey;
	}

	public long getHighestSupportResponseId() {
		return _highestSupportResponseId;
	}

	public long getIndustry() {
		return _industry;
	}

	public String getInstructions() {
		return _instructions;
	}

	public Date getLastAuditDate() {
		if (_lastAuditDate == null) {
			return null;
		}

		return new Date(_lastAuditDate.getTime());
	}

	public long getMaxCustomers() {
		return _maxCustomers;
	}

	public Date getModifiedDate() {
		if (_modifiedDate == null) {
			return null;
		}

		return new Date(_modifiedDate.getTime());
	}

	public long getModifiedUserId() {
		return _modifiedUserId;
	}

	public String getModifiedUserName() {
		return _modifiedUserName;
	}

	public String getName() {
		return _name;
	}

	public String getNotes() {
		return _notes;
	}

	public List<OSBOfferingEntry> getOfferingEntries() {
		return _offeringEntries;
	}

	public long getPartnerEntryId() {
		return _partnerEntryId;
	}

	public long getRedirectAccountEntryId() {
		return _redirectAccountEntryId;
	}

	public int getStatus() {
		return _status;
	}

	public long getStatusByUserId() {
		return _statusByUserId;
	}

	public String getStatusByUserName() {
		return _statusByUserName;
	}

	public Date getStatusDate() {
		if (_statusDate == null) {
			return null;
		}

		return new Date(_statusDate.getTime());
	}

	public String getStatusMessage() {
		return _statusMessage;
	}

	public int getTier() {
		return _tier;
	}

	public int getType() {
		return _type;
	}

	public long getUserId() {
		return _userId;
	}

	public String getUserName() {
		return _userName;
	}

	public boolean isPartnerManagedSupport() {
		return _partnerManagedSupport;
	}

	public void setAccountEntryId(long accountEntryId) {
		_accountEntryId = accountEntryId;
	}

	public void setCode(String code) {
		_code = code;
	}

	public void setCorpEntryName(String corpEntryName) {
		_corpEntryName = corpEntryName;
	}

	public void setCorpProjectId(long corpProjectId) {
		_corpProjectId = corpProjectId;
	}

	public void setCorpProjectUuid(String corpProjectUuid) {
		_corpProjectUuid = corpProjectUuid;
	}

	public void setCountryId(long countryId) {
		_countryId = countryId;
	}

	public void setCreateDate(Date createDate) {
		if (createDate != null) {
			_createDate = new Date(createDate.getTime());
		}
	}

	public void setDossieraAccountKey(String dossieraAccountKey) {
		_dossieraAccountKey = dossieraAccountKey;
	}

	public void setHighestSupportResponseId(long highestSupportResponseId) {
		_highestSupportResponseId = highestSupportResponseId;
	}

	public void setIndustry(long industry) {
		_industry = industry;
	}

	public void setInstructions(String instructions) {
		_instructions = instructions;
	}

	public void setLastAuditDate(Date lastAuditDate) {
		if (lastAuditDate != null) {
			_lastAuditDate = new Date(lastAuditDate.getTime());
		}
	}

	public void setMaxCustomers(long maxCustomers) {
		_maxCustomers = maxCustomers;
	}

	public void setModifiedDate(Date modifiedDate) {
		if (modifiedDate != null) {
			_modifiedDate = new Date(modifiedDate.getTime());
		}
	}

	public void setModifiedUserId(long modifiedUserId) {
		_modifiedUserId = modifiedUserId;
	}

	public void setModifiedUserName(String modifiedUserName) {
		_modifiedUserName = modifiedUserName;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setNotes(String notes) {
		_notes = notes;
	}

	public void setOfferingEntries(List<OSBOfferingEntry> offeringEntries) {
		_offeringEntries = offeringEntries;
	}

	public void setPartnerEntryId(long partnerEntryId) {
		_partnerEntryId = partnerEntryId;
	}

	public void setPartnerManagedSupport(boolean partnerManagedSupport) {
		_partnerManagedSupport = partnerManagedSupport;
	}

	public void setRedirectAccountEntryId(long redirectAccountEntryId) {
		_redirectAccountEntryId = redirectAccountEntryId;
	}

	public void setStatus(int status) {
		_status = status;
	}

	public void setStatusByUserId(long statusByUserId) {
		_statusByUserId = statusByUserId;
	}

	public void setStatusByUserName(String statusByUserName) {
		_statusByUserName = statusByUserName;
	}

	public void setStatusDate(Date statusDate) {
		if (statusDate != null) {
			_statusDate = new Date(statusDate.getTime());
		}
	}

	public void setStatusMessage(String statusMessage) {
		_statusMessage = statusMessage;
	}

	public void setTier(int tier) {
		_tier = tier;
	}

	public void setType(int type) {
		_type = type;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	private long _accountEntryId;
	private String _code;
	private String _corpEntryName;
	private long _corpProjectId;
	private String _corpProjectUuid;
	private long _countryId;
	private Date _createDate;
	private String _dossieraAccountKey;
	private long _highestSupportResponseId;
	private long _industry;
	private String _instructions;
	private Date _lastAuditDate;
	private long _maxCustomers;
	private Date _modifiedDate;
	private long _modifiedUserId;
	private String _modifiedUserName;
	private String _name;
	private String _notes;
	private List<OSBOfferingEntry> _offeringEntries = new ArrayList<>();
	private long _partnerEntryId;
	private boolean _partnerManagedSupport;
	private long _redirectAccountEntryId;
	private int _status;
	private long _statusByUserId;
	private String _statusByUserName;
	private Date _statusDate;
	private String _statusMessage;
	private int _tier;
	private int _type;
	private long _userId;
	private String _userName;

}