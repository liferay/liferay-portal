/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class SystemObject1toMObjectRelatedModelsProviderImpl
	<T extends BaseModel<T>>
		extends BaseSystemObjectRelatedModelsProviderImpl<T> {

	public SystemObject1toMObjectRelatedModelsProviderImpl(
		InlineSQLHelper inlineSQLHelper, ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		SystemObjectDefinitionManager systemObjectDefinitionManager,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		super(
			inlineSQLHelper, objectDefinition, objectDefinitionLocalService,
			objectEntryLocalService, objectFieldLocalService,
			objectRelationshipLocalService, systemObjectDefinitionManager,
			systemObjectDefinitionManagerRegistry);
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_ONE_TO_MANY;
	}

}