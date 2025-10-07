/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model.impl;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.cache.CacheField;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPageTemplateStructureRelImpl
	extends LayoutPageTemplateStructureRelBaseImpl {

	@Override
	public JSONObject getDataJSONObject() {
		if (_dataJSONObject == null) {
			try {
				_dataJSONObject = JSONFactoryUtil.createJSONObject(getData());

				dataJSONObjectUpdateEntityCacheBiConsumer.accept(
					this, _dataJSONObject);
			}
			catch (JSONException jsonException) {
				if (_log.isDebugEnabled()) {
					_log.debug(jsonException);
				}
			}
		}

		return _dataJSONObject;
	}

	@Override
	public void setData(String data) {
		super.setData(data);

		_dataJSONObject = null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateStructureRelImpl.class);

	@CacheField(permanent = true, propagateToInterface = true)
	private transient JSONObject _dataJSONObject;

}