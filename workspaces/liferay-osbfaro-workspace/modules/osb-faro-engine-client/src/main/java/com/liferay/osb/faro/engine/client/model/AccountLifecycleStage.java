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

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
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

	public String getSegmentId() {
		return _segmentId;
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

	public void setExternalReferenceCode(String externalReferenceCode) {
		_externalReferenceCode = externalReferenceCode;
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

	public void setSegmentId(String segmentId) {
		_segmentId = segmentId;
	}

	private String _accountLifecycleId;
	private AccountLifecycleStageRule _accountLifecycleStageRule;
	private String _description;
	private String _externalReferenceCode;
	private String _id;
	private Integer _maxDuration;
	private String _name;
	private Integer _position;
	private String _segmentId;

}