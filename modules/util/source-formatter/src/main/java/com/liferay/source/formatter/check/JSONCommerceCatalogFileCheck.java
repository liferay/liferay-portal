/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.source.formatter.check.util.JsonSourceUtil;

import java.util.Comparator;

/**
 * @author Alan Huang
 */
public class JSONCommerceCatalogFileCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws JSONException {

		if (absolutePath.endsWith(".options.json") ||
			absolutePath.endsWith(".products.json")) {

			return JSONUtil.toString(
				JsonSourceUtil.sortJSONArray(
					new JSONArrayImpl(content),
					new ExternalReferenceCodeComparator()));
		}
		else if (absolutePath.endsWith(".products.specifications.json")) {
			return JSONUtil.toString(
				JsonSourceUtil.sortJSONArray(
					new JSONArrayImpl(content),
					new CPDefinitionExternalReferenceCodeComparator()));
		}

		return content;
	}

	private class CPDefinitionExternalReferenceCodeComparator
		implements Comparator<Object> {

		@Override
		public int compare(Object object1, Object object2) {
			JSONObject jsonObject1 = (JSONObject)object1;

			String cpDefinitionExternalReferenceCode1 = jsonObject1.getString(
				"cpDefinitionExternalReferenceCode");

			JSONObject jsonObject2 = (JSONObject)object2;

			String cpDefinitionExternalReferenceCode2 = jsonObject2.getString(
				"cpDefinitionExternalReferenceCode");

			if (!cpDefinitionExternalReferenceCode1.equals(
					cpDefinitionExternalReferenceCode2)) {

				return cpDefinitionExternalReferenceCode1.compareTo(
					cpDefinitionExternalReferenceCode2);
			}

			String key1 = jsonObject1.getString("key");
			String key2 = jsonObject2.getString("key");

			return key1.compareTo(key2);
		}

	}

	private class ExternalReferenceCodeComparator
		implements Comparator<Object> {

		@Override
		public int compare(Object object1, Object object2) {
			JSONObject jsonObject1 = (JSONObject)object1;

			String externalReferenceCode1 = jsonObject1.getString(
				"externalReferenceCode");

			JSONObject jsonObject2 = (JSONObject)object2;

			String externalReferenceCode2 = jsonObject2.getString(
				"externalReferenceCode");

			return externalReferenceCode1.compareTo(externalReferenceCode2);
		}

	}

}