/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.internal.vulcan.batch.engine.util;

import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateRegistry;
import com.liferay.portal.vulcan.resource.OpenAPIResource;
import com.liferay.portal.vulcan.util.OpenAPIUtil;
import com.liferay.portal.vulcan.yaml.openapi.OpenAPIYAML;

import java.util.Collections;
import java.util.List;

/**
 * @author Alberto Javier Moreno Lage
 */
public class EntityScopesUtil {

	public static List<String> getEntityScopes(
			long companyId, boolean export, String internalClassNameKey,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			OpenAPIResource openAPIResource,
			VulcanBatchEngineTaskItemDelegateRegistry
				vulcanBatchEngineTaskItemDelegateRegistry)
		throws Exception {

		if (internalClassNameKey.contains(StringPool.POUND)) {
			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.getObjectDefinition(
					companyId,
					TaskItemUtil.getTaskItemDelegateName(internalClassNameKey));

			return Collections.singletonList(objectDefinition.getScope());
		}

		OpenAPIYAML openAPIYAML = OpenAPIYAMLUtil.getOpenAPIYAML(
			companyId, internalClassNameKey, openAPIResource,
			vulcanBatchEngineTaskItemDelegateRegistry);

		if (export) {
			return OpenAPIUtil.getReadEntityScopes(
				TaskItemUtil.getSimpleClassName(internalClassNameKey),
				openAPIYAML);
		}

		return OpenAPIUtil.getCreateEntityScopes(
			TaskItemUtil.getSimpleClassName(internalClassNameKey), openAPIYAML);
	}

}