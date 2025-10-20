/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener;

import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.util.PortalInstances;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ModelListener.class)
public class ObjectFolderModelListener extends BaseModelListener<ObjectFolder> {

	@Override
	public void onAfterCreate(ObjectFolder objectFolder)
		throws ModelListenerException {

		try {
			_onAfterCreate(objectFolder);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(ObjectFolder objectFolder)
		throws ModelListenerException {

		if (PortalInstances.isCurrentCompanyInDeletionProcess()) {
			return;
		}

		try {
			ObjectFolder defaultObjectFolder =
				_objectFolderLocalService.getDefaultObjectFolder(
					objectFolder.getCompanyId());

			for (ObjectDefinition objectDefinition :
					_objectDefinitionLocalService.
						getObjectFolderObjectDefinitions(
							objectFolder.getObjectFolderId())) {

				_objectDefinitionLocalService.updateObjectFolderId(
					objectDefinition.getObjectDefinitionId(),
					defaultObjectFolder.getObjectFolderId());
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(
			ObjectFolder originalObjectFolder, ObjectFolder objectFolder)
		throws ModelListenerException {

		try {
			for (ObjectDefinition objectDefinition :
					_objectDefinitionLocalService.
						getObjectFolderObjectDefinitions(
							objectFolder.getObjectFolderId())) {

				Indexer<ObjectDefinition> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						ObjectDefinition.class);

				indexer.reindex(objectDefinition);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private Role _getOrAddCMSAdministratorRole(long companyId, long userId)
		throws Exception {

		Role role = _roleLocalService.fetchRole(
			companyId, RoleConstants.CMS_ADMINISTRATOR);

		if (role != null) {
			return role;
		}

		return _roleLocalService.addRole(
			null, userId, null, 0, RoleConstants.CMS_ADMINISTRATOR, null, null,
			RoleConstants.TYPE_REGULAR, null, null);
	}

	private void _onAfterCreate(ObjectFolder objectFolder) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				objectFolder.getCompanyId(), "LPD-17564") ||
			(!Objects.equals(
				objectFolder.getExternalReferenceCode(),
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES) &&
			 !Objects.equals(
				 objectFolder.getExternalReferenceCode(),
				 ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES))) {

			return;
		}

		Role cmsAdministratorRole = _getOrAddCMSAdministratorRole(
			objectFolder.getCompanyId(), objectFolder.getUserId());

		_resourcePermissionLocalService.setResourcePermissions(
			objectFolder.getCompanyId(), ObjectFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectFolder.getObjectFolderId()),
			cmsAdministratorRole.getRoleId(),
			TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					ObjectFolder.class.getName()),
				ResourceAction::getActionId, String.class));
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}