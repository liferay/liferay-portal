/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.model;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import org.json.JSONObject;

/**
 * @author Amos Fong
 */
public class BusinessEvent {

	public BusinessEvent(JSONObject jsonObject) {
		JSONObject propertiesJSONObject = jsonObject.getJSONObject(
			"properties");

		_accountExternalReferenceCode = propertiesJSONObject.getString(
			"accountEntryToBusinessEventsERC");
		_accountId = propertiesJSONObject.getLong(
			"r_accountEntryToBusinessEvents_accountEntryId");

		_businessEventId = jsonObject.getLong("id");

		JSONObject creatorJSONObject = jsonObject.getJSONObject("creator");

		_creatorGivenName = creatorJSONObject.getString("givenName");
		_creatorId = creatorJSONObject.getLong("id");

		JSONObject eventStatusJSONObject = propertiesJSONObject.getJSONObject(
			"eventStatus");

		_eventStatusKey = eventStatusJSONObject.getString("key");

		JSONObject eventTypeJSONObject = propertiesJSONObject.getJSONObject(
			"eventType");

		_eventTypeName = eventTypeJSONObject.optString("name");

		_lastComment = propertiesJSONObject.optString("lastComment");
		_name = propertiesJSONObject.getString("name");
		_targetGoLiveDateTime = propertiesJSONObject.getString(
			"targetGoLiveDateTime");
	}

	public String getAccountExternalReferenceCode() {
		return _accountExternalReferenceCode;
	}

	public long getAccountId() {
		return _accountId;
	}

	public String getActivityHistoryURL(String customerPortalURL) {
		return getURL(customerPortalURL) + "/activity-history";
	}

	public long getBusinessEventId() {
		return _businessEventId;
	}

	public String getCreatorGivenName() {
		return _creatorGivenName;
	}

	public long getCreatorId() {
		return _creatorId;
	}

	public String getEditURL(String customerPortalURL) {
		return getURL(customerPortalURL) + "/edit";
	}

	public String getEventStatusKey() {
		return _eventStatusKey;
	}

	public String getEventTypeName() {
		return _eventTypeName;
	}

	public String getLastComment() {
		return _lastComment;
	}

	public String getName() {
		return _name;
	}

	public String getTargetGoLiveDate() {
		return _targetGoLiveDateTime.split("T")[0];
	}

	public String getTargetGoLiveDateTime() {
		return _targetGoLiveDateTime;
	}

	public String getURL(String customerPortalURL) {
		StringBundler sb = new StringBundler(5);

		sb.append(customerPortalURL);
		sb.append("/project/#/");
		sb.append(_accountExternalReferenceCode);
		sb.append("/business-events/");
		sb.append(_businessEventId);

		return sb.toString();
	}

	public boolean isCanceled() {
		return StringUtil.equals(_eventStatusKey, "canceled");
	}

	public boolean isCompleted() {
		return StringUtil.equals(_eventStatusKey, "completed");
	}

	public boolean isOverdue() {
		return StringUtil.equals(_eventStatusKey, "overdue");
	}

	private final String _accountExternalReferenceCode;
	private final long _accountId;
	private final long _businessEventId;
	private final String _creatorGivenName;
	private final long _creatorId;
	private final String _eventStatusKey;
	private final String _eventTypeName;
	private final String _lastComment;
	private final String _name;
	private final String _targetGoLiveDateTime;

}