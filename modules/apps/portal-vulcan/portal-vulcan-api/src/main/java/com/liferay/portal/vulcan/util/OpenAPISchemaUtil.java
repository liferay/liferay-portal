/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.List;

/**
 * @author Nathaly Gomes
 */
public class OpenAPISchemaUtil {

	public static String getReference(Schema schema) {
		if (schema instanceof ArraySchema) {
			ArraySchema arraySchema = (ArraySchema)schema;

			Schema itemsSchema = arraySchema.getItems();

			return itemsSchema.get$ref();
		}

		List<Schema> allOfSchemas = schema.getAllOf();

		if (ListUtil.isNotEmpty(allOfSchemas)) {
			Schema allOfSchema = allOfSchemas.get(0);

			return allOfSchema.get$ref();
		}

		return schema.get$ref();
	}

	public static Schema setDescription(String description, Schema schema) {
		if (Validator.isNull(schema.get$ref()) ||
			Validator.isNull(description)) {

			schema.setDescription(description);

			return schema;
		}

		return new ObjectSchema() {
			{
				addAllOfItem(schema);
				setDescription(description);
				setExtensions(schema.getExtensions());
			}
		};
	}

	public static void setReference(String reference, Schema schema) {
		if (Validator.isNull(schema.getDescription())) {
			schema.set$ref(reference);

			return;
		}

		schema.addAllOfItem(
			new Schema() {
				{
					set$ref(reference);
				}
			});
	}

}