/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import java.util.Date;

/**
 * @author Riccardo Ferrari
 */
public class AccountLifecycleStageStatus {

	public String getDescription() {
		return _description;
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

	public Integer getMaxDuration() {
		return _maxDuration;
	}

	public String getName() {
		return _name;
	}

	public Integer getPosition() {
		return _position;
	}

	public Date getStartDate() {
		if (_startDate == null) {
			return null;
		}

		return new Date(_startDate.getTime());
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setEndDate(Date endDate) {
		if (endDate != null) {
			_endDate = new Date(endDate.getTime());
		}
	}

	public void setId(String id) {
		_id = id;
	}

	public void setMaxDuration(Integer maxDuration) {
		_maxDuration = maxDuration;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setPosition(Integer position) {
		_position = position;
	}

	public void setStartDate(Date startDate) {
		if (startDate != null) {
			_startDate = new Date(startDate.getTime());
		}
	}

	private String _description;
	private Date _endDate;
	private String _id;
	private Integer _maxDuration;
	private String _name;
	private Integer _position;
	private Date _startDate;

}