/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Riccardo Ferrari
 */
public class AccountLifecycleStage {

	public String getAccountLifecycleId() {
		return _accountLifecycleId;
	}

	@JsonProperty("segment")
	public AccountLifecycleStageRule getAccountLifecycleStageRule() {
		return _accountLifecycleStageRule;
	}

	public String getDescription() {
		return _description;
	}

	public Integer getDisplayOrder() {
		return _displayOrder;
	}

	public String getId() {
		return _id;
	}

	public Integer getMaxDuration() {
		return _maxDuration;
	}

	public String getSegmentId() {
		return _segmentId;
	}

	public String getStageType() {
		return _stageType;
	}

	public void setAccountLifecycleId(String accountLifecycleId) {
		_accountLifecycleId = accountLifecycleId;
	}

	public void setAccountLifecycleStageRule(
		AccountLifecycleStageRule accountLifecycleStageRule) {

		_accountLifecycleStageRule = accountLifecycleStageRule;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setDisplayOrder(Integer displayOrder) {
		_displayOrder = displayOrder;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setMaxDuration(Integer maxDuration) {
		_maxDuration = maxDuration;
	}

	public void setSegmentId(String segmentId) {
		_segmentId = segmentId;
	}

	public void setStageType(String stageType) {
		_stageType = stageType;
	}

	private String _accountLifecycleId;
	private AccountLifecycleStageRule _accountLifecycleStageRule;
	private String _description;
	private Integer _displayOrder;
	private String _id;
	private Integer _maxDuration;
	private String _segmentId;
	private String _stageType;

}