/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import java.util.Date;

/**
 * @author Riccardo Ferrari
 */
public class Campaign {

	public Long getAccountsTouched() {
		return _accountsTouched;
	}

	public String getCampaignName() {
		return _campaignName;
	}

	public String getCampaignType() {
		return _campaignType;
	}

	public Date getEndDate() {
		if (_endDate == null) {
			return null;
		}

		return new Date(_endDate.getTime());
	}

	public String getId() {
		return _id;
	}

	public Long getIndividualsTouched() {
		return _individualsTouched;
	}

	public String getOrigin() {
		return _origin;
	}

	public Date getStartDate() {
		if (_startDate == null) {
			return null;
		}

		return new Date(_startDate.getTime());
	}

	public String getStatus() {
		return _status;
	}

	public void setAccountsTouched(Long accountsTouched) {
		_accountsTouched = accountsTouched;
	}

	public void setCampaignName(String campaignName) {
		_campaignName = campaignName;
	}

	public void setCampaignType(String campaignType) {
		_campaignType = campaignType;
	}

	public void setEndDate(Date endDate) {
		if (endDate != null) {
			_endDate = new Date(endDate.getTime());
		}
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIndividualsTouched(Long individualsTouched) {
		_individualsTouched = individualsTouched;
	}

	public void setOrigin(String origin) {
		_origin = origin;
	}

	public void setStartDate(Date startDate) {
		if (startDate != null) {
			_startDate = new Date(startDate.getTime());
		}
	}

	public void setStatus(String status) {
		_status = status;
	}

	private Long _accountsTouched;
	private String _campaignName;
	private String _campaignType;
	private Date _endDate;
	private String _id;
	private Long _individualsTouched;
	private String _origin;
	private Date _startDate;
	private String _status;

}