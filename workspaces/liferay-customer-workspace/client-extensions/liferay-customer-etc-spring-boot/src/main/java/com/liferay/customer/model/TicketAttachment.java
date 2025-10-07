/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.model;

import com.liferay.petra.string.StringBundler;

import org.json.JSONObject;

/**
 * @author Amos Fong
 */
public class TicketAttachment {

	public static final int STATUS_APPROVED = 0;

	public static final int STATUS_DRAFT = 2;

	public static final String STORAGE_PROVIDER_GCS = "gcs";

	public TicketAttachment(JSONObject jsonObject) {
		_accountKey = jsonObject.getString("accountKey");
		_draftCommentBody = jsonObject.optString("draftCommentBody");
		_fileName = jsonObject.getString("fileName");
		_fileSize = jsonObject.getString("fileSize");
		_gcsBucketName = jsonObject.getString("gcsBucketName");
		_jiraIssueKey = jsonObject.optString("jiraIssueKey");
		_md5Checksum = jsonObject.optString("md5Checksum");

		JSONObject statusJSONObject = jsonObject.getJSONObject("status");

		_status = statusJSONObject.getInt("code");

		_storageProvider = jsonObject.getString("storageProvider");
		_ticketAttachmentId = jsonObject.getLong("id");
		_type = jsonObject.optString("type");

		JSONObject creatorJSONObject = jsonObject.getJSONObject("creator");

		_userId = creatorJSONObject.getLong("id");
	}

	public String getAccountKey() {
		return _accountKey;
	}

	public String getDraftCommentBody() {
		return _draftCommentBody;
	}

	public String getFileName() {
		return _fileName;
	}

	public String getFileSize() {
		return _fileSize;
	}

	public String getGCSBucketName() {
		return _gcsBucketName;
	}

	public String getGCSObjectName() {
		StringBundler sb = new StringBundler(6);

		sb.append("tickets/");
		sb.append(_jiraIssueKey);
		sb.append("/");
		sb.append(_ticketAttachmentId);
		sb.append("/");
		sb.append(_fileName);

		return sb.toString();
	}

	public String getJiraIssueKey() {
		return _jiraIssueKey;
	}

	public String getMD5Checksum() {
		return _md5Checksum;
	}

	public int getStatus() {
		return _status;
	}

	public String getStorageProvider() {
		return _storageProvider;
	}

	public long getTicketAttachmentId() {
		return _ticketAttachmentId;
	}

	public String getType() {
		return _type;
	}

	public long getUserId() {
		return _userId;
	}

	public boolean isApproved() {
		if (_status == STATUS_APPROVED) {
			return true;
		}

		return false;
	}

	private final String _accountKey;
	private final String _draftCommentBody;
	private final String _fileName;
	private final String _fileSize;
	private final String _gcsBucketName;
	private final String _jiraIssueKey;
	private final String _md5Checksum;
	private final int _status;
	private final String _storageProvider;
	private final long _ticketAttachmentId;
	private final String _type;
	private final long _userId;

}