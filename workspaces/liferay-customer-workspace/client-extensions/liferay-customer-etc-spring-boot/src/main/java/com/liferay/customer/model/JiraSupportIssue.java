/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.model;

import com.liferay.customer.constants.JiraIssueConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Jenny Chen
 */
public class JiraSupportIssue {

	public JiraSupportIssue(JSONObject jsonObject) {
		_key = jsonObject.getString("key");

		JSONObject fieldsJSONObject = jsonObject.getJSONObject("fields");

		JSONArray jsonArray = fieldsJSONObject.optJSONArray(
			"labels", new JSONArray());

		List<String> labels = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			labels.add(jsonArray.getString(i));
		}

		_labels = labels.toArray(new String[0]);

		_organizationId = StringPool.BLANK;

		JSONObject statusJSONObject = fieldsJSONObject.optJSONObject(
			"status", new JSONObject());

		_status = statusJSONObject.optString("name");

		_summary = fieldsJSONObject.optString("summary");
	}

	public JiraSupportIssue(JSONObject jsonObject, String ticketURL) {
		this(jsonObject);

		_ticketURL = ticketURL;
	}

	public JiraSupportIssue(
		JSONObject jsonObject, String organizationId, String workspaceId) {

		this(jsonObject);

		_organizationId = organizationId;
		_workspaceId = workspaceId;
	}

	public String getKey() {
		return _key;
	}

	public String[] getLabels() {
		return _labels;
	}

	public String getOrganizationId() {
		return _organizationId;
	}

	public String getStatus() {
		return _status;
	}

	public String getSummary() {
		return _summary;
	}

	public String getTicketURL() {
		return _ticketURL;
	}

	public String getWorkspaceId() {
		return _workspaceId;
	}

	public boolean isClosed() {
		return ArrayUtil.contains(JiraIssueConstants.STATUSES_CLOSED, _status);
	}

	private final String _key;
	private final String[] _labels;
	private String _organizationId;
	private final String _status;
	private final String _summary;
	private String _ticketURL = StringPool.BLANK;
	private String _workspaceId = StringPool.BLANK;

}