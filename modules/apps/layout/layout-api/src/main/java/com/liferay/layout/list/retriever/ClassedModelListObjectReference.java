/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.list.retriever;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

/**
 * @author Eudaldo Alonso
 */
public class ClassedModelListObjectReference implements ListObjectReference {

	public ClassedModelListObjectReference(JSONObject jsonObject) {
		_classPK = jsonObject.getLong("classPK");
		_className = jsonObject.getString("className");
		_externalReferenceCode = jsonObject.getString("externalReferenceCode");
		_itemType = jsonObject.getString("itemType");
		_scopeExternalReferenceCode = jsonObject.getString(
			"scopeExternalReferenceCode");
		_title = jsonObject.getString("title");
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	@Override
	public String getItemType() {
		return _itemType;
	}

	public String getScopeExternalReferenceCode() {
		return _scopeExternalReferenceCode;
	}

	public String getTitle() {
		return _title;
	}

	@Override
	public String toJSON() {
		return JSONUtil.put(
			"className", _className
		).put(
			"classPK", _classPK
		).put(
			"externalReferenceCode", _externalReferenceCode
		).put(
			"itemType", _itemType
		).put(
			"scopeExternalReferenceCode", _scopeExternalReferenceCode
		).put(
			"title", _title
		).toString();
	}

	private final String _className;
	private final long _classPK;
	private final String _externalReferenceCode;
	private final String _itemType;
	private final String _scopeExternalReferenceCode;
	private final String _title;

}