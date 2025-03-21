/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.internal.vulcan.batch.engine.util;

import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.batch.planner.rest.internal.vulcan.yaml.openapi.OpenAPIYAMLProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.openapi.v1_0.ObjectEntryOpenAPIResource;
import com.liferay.object.rest.openapi.v1_0.ObjectEntryOpenAPIResourceProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.batch.engine.Field;
import com.liferay.portal.vulcan.util.OpenAPIUtil;
import com.liferay.portal.vulcan.yaml.openapi.OpenAPIYAML;

import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Matija Petanjek
 */
public class FieldProviderUtil {

	public static List<Field> filter(
		List<Field> fields, Field.AccessType ignoredAccessType) {

		return ListUtil.filter(
			fields,
			dtoEntityField -> {
				if (dtoEntityField.getAccessType() == ignoredAccessType) {
					return false;
				}

				String name = dtoEntityField.getName();

				return !name.startsWith("x-");
			});
	}

	public static List<Field> getFields(
			long companyId, String internalClassNameKey,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryOpenAPIResourceProvider
				objectEntryOpenAPIResourceProvider,
			OpenAPIYAMLProvider openAPIYAMLProvider, UriInfo uriInfo)
		throws Exception {

		int index = internalClassNameKey.indexOf(StringPool.POUND);

		if (index < 0) {
			OpenAPIYAML openAPIYAML = openAPIYAMLProvider.getOpenAPIYAML(
				companyId, internalClassNameKey);

			return ListUtil.fromMapValues(
				OpenAPIUtil.getDTOEntityFields(
					TaskItemUtil.getSimpleClassName(internalClassNameKey),
					openAPIYAML));
		}

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				companyId,
				TaskItemUtil.getTaskItemDelegateName(internalClassNameKey));

		ObjectEntryOpenAPIResource objectEntryOpenAPIResource =
			objectEntryOpenAPIResourceProvider.getObjectEntryOpenAPIResource(
				objectDefinition);

		Map<String, Field> fields = objectEntryOpenAPIResource.getFields(
			uriInfo);

		return TransformUtil.transform(
			fields.values(),
			field -> {
				if ((Objects.equals(field.getType(), "array") ||
					 Objects.equals(field.getType(), "object")) &&
					!Validator.isBlank(field.getRef()) &&
					Validator.isNotNull(field.getUnsupportedFormats())) {

					return null;
				}

				return field;
			});
	}

}