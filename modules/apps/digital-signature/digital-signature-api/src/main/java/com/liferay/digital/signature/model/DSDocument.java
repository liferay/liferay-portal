/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.model;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class DSDocument {

	public String getAssignTabsToDSRecipientId() {
		return assignTabsToDSRecipientId;
	}

	public String getDSDocumentId() {
		return dsDocumentId;
	}

	public String getData() {
		return data;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getName() {
		return name;
	}

	public String getURI() {
		return uri;
	}

	public boolean isTransformPDFFields() {
		return transformPDFFields;
	}

	public void setAssignTabsToDSRecipientId(String assignTabsToDSRecipientId) {
		this.assignTabsToDSRecipientId = assignTabsToDSRecipientId;
	}

	public void setDSDocumentId(String dsDocumentId) {
		this.dsDocumentId = dsDocumentId;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTransformPDFFields(boolean transformPDFFields) {
		this.transformPDFFields = transformPDFFields;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	public JSONObject toJSONObject() {
		return JSONUtil.put(
			"assignTabsToRecipientId", getAssignTabsToDSRecipientId()
		).put(
			"documentBase64", getData()
		).put(
			"documentId", getDSDocumentId()
		).put(
			"fileExtension", getFileExtension()
		).put(
			"name", getName()
		).put(
			"transformPdfFields", isTransformPDFFields()
		);
	}

	protected String assignTabsToDSRecipientId;
	protected String data;
	protected String dsDocumentId;
	protected String fileExtension;
	protected String name;
	protected boolean transformPDFFields;
	protected String uri;

}