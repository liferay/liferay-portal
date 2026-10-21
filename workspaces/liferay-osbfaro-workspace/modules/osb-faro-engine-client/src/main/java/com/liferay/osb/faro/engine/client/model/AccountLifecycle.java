/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * @author Riccardo Ferrari
 */
public class AccountLifecycle {

	public AccountLifecycleStage getAccountLifecycleStage(String name) {
		if ((name == null) || (_accountLifecycleStages == null)) {
			return null;
		}

		for (AccountLifecycleStage accountLifecycleStage :
				_accountLifecycleStages) {

			if (StringUtil.equalsIgnoreCase(
					_normalize(accountLifecycleStage.getName()),
					_normalize(name))) {

				return accountLifecycleStage;
			}
		}

		return null;
	}

	@JsonProperty("stages")
	public List<AccountLifecycleStage> getAccountLifecycleStages() {
		return _accountLifecycleStages;
	}

	public String getAuthorId() {
		return _authorId;
	}

	public String getAuthorName() {
		return _authorName;
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

	public Date getModifiedDate() {
		if (_modifiedDate == null) {
			return null;
		}

		return new Date(_modifiedDate.getTime());
	}

	public String getName() {
		return _name;
	}

	public Date getProcessedDate() {
		if (_processedDate == null) {
			return null;
		}

		return new Date(_processedDate.getTime());
	}

	public String getSegmentId() {
		return _segmentId;
	}

	@JsonProperty("defaultAccountLifecycle")
	public Boolean isDefaultAccountLifecycle() {
		return _defaultAccountLifecycle;
	}

	public void setAccountLifecycleStages(
		List<AccountLifecycleStage> accountLifecycleStages) {

		_accountLifecycleStages = accountLifecycleStages;
	}

	public void setAuthorId(String authorId) {
		_authorId = authorId;
	}

	public void setAuthorName(String authorName) {
		_authorName = authorName;
	}

	public void setDefaultAccountLifecycle(Boolean defaultAccountLifecycle) {
		_defaultAccountLifecycle = defaultAccountLifecycle;
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

	public void setModifiedDate(Date modifiedDate) {
		if (modifiedDate != null) {
			_modifiedDate = new Date(modifiedDate.getTime());
		}
	}

	public void setName(String name) {
		_name = name;
	}

	public void setProcessedDate(Date processedDate) {
		if (processedDate != null) {
			_processedDate = new Date(processedDate.getTime());
		}
	}

	public void setSegmentId(String segmentId) {
		_segmentId = segmentId;
	}

	private String _normalize(String name) {
		return StringUtil.trim(
			StringUtil.replace(
				name, new char[] {'-', '_'}, new char[] {' ', ' '}));
	}

	private List<AccountLifecycleStage> _accountLifecycleStages;
	private String _authorId;
	private String _authorName;
	private Boolean _defaultAccountLifecycle;
	private String _description;
	private String _externalReferenceCode;
	private String _id;
	private Date _modifiedDate;
	private String _name;
	private Date _processedDate;
	private String _segmentId;

}