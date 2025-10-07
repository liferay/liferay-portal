/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

/**
 * @author Igor Beslic
 */
public abstract class IndexedRecordHandler {

	public static boolean isIndexedRecord(Object object) {
		if (object instanceof IndexedRecord) {
			return true;
		}

		return false;
	}

	protected void updateField(
		Schema.Field schemaField, Object value, IndexedRecord indexedRecord) {

		if (schemaField == null) {
			return;
		}

		indexedRecord.put(schemaField.pos(), value);
	}

}