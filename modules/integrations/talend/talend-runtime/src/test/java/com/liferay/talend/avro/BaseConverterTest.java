/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.avro;

import com.liferay.talend.BaseTestCase;

import javax.json.JsonObject;

/**
 * @author Igor Beslic
 */
public class BaseConverterTest extends BaseTestCase {

	protected JsonObjectIndexedRecordConverter
		getJsonObjectIndexedRecordConverter(
			String endpoint, String httpOperation, JsonObject oasJsonObject) {

		return new JsonObjectIndexedRecordConverter(
			getSchema(endpoint, httpOperation, oasJsonObject));
	}

}