/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.schema.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.avro.Schema;

import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class RejectSchemaConstants {

	public static final String FIELD_ERROR_CODE = "errorCode";

	public static final String FIELD_ERROR_MESSAGE = "errorMessage";

	public static final List<Schema.Field> rejectFields =
		Collections.unmodifiableList(
			new ArrayList<Schema.Field>() {
				{
					Schema.Field field = new Schema.Field(
						FIELD_ERROR_MESSAGE,
						AvroUtils.wrapAsNullable(AvroUtils._string()), null,
						(Object)null);

					field.addProp(
						SchemaConstants.TALEND_COLUMN_DB_LENGTH, "255");
					field.addProp(
						SchemaConstants.TALEND_FIELD_GENERATED, "true");
					field.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");

					add(field);

					field = new Schema.Field(
						FIELD_ERROR_CODE,
						AvroUtils.wrapAsNullable(AvroUtils._int()), null,
						(Object)null);

					field.addProp(
						SchemaConstants.TALEND_FIELD_GENERATED, "true");
					field.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");

					add(field);
				}
			});

}