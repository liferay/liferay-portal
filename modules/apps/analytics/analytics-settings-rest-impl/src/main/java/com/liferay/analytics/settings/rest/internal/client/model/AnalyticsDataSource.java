/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author Riccardo Ferrari
 */
public class AnalyticsDataSource {

	public Date getCreateDate() {
		return _createDate;
	}

	public Long getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	@JsonProperty("groupIds")
	public Long[] getSiteIds() {
		return _siteIds;
	}

	public String getState() {
		return _state;
	}

	public String getStatus() {
		return _status;
	}

	public String getURL() {
		return _url;
	}

	public String getWorkspaceURL() {
		return _workspaceURL;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public void setId(Long id) {
		_id = id;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setSiteIds(Long[] siteIds) {
		_siteIds = siteIds;
	}

	public void setState(String state) {
		_state = state;
	}

	public void setStatus(String status) {
		_status = status;
	}

	public void setURL(String url) {
		_url = url;
	}

	public void setWorkspaceURL(String workspaceURL) {
		_workspaceURL = workspaceURL;
	}

	private Date _createDate;
	private Long _id;
	private String _name;
	private Long[] _siteIds;
	private String _state;
	private String _status;
	private String _url;
	private String _workspaceURL;

}