/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.model.listener;

import com.liferay.digital.sales.room.constants.DSRFolderConstants;
import com.liferay.digital.sales.room.constants.DSRRoleConstants;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class DLFileEntryModelListener extends BaseModelListener<DLFileEntry> {

	@Override
	public void onBeforeCreate(DLFileEntry dlFileEntry)
		throws ModelListenerException {

		try {
			_onBeforeCreate(dlFileEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _onBeforeCreate(DLFileEntry dlFileEntry) throws Exception {
		if (ExportImportThreadLocal.isImportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress() ||
			(dlFileEntry.getFolderId() ==
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", dlFileEntry.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		Group group = _groupLocalService.getGroup(dlFileEntry.getGroupId());

		if (!Objects.equals(
				group.getClassName(), objectDefinition.getClassName())) {

			return;
		}

		DLFolder dlFolder =
			_dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				dlFileEntry.getGroupId());

		if ((dlFolder == null) ||
			(dlFileEntry.getFolderId() != dlFolder.getFolderId())) {

			return;
		}

		String[] actionIds = TransformUtil.transformToArray(
			_resourceActionLocalService.getResourceActions(
				DLFileEntry.class.getName()),
			ResourceAction::getActionId, String.class);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(
				HashMapBuilder.put(
					DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR,
					new String[] {
						ActionKeys.ADD_DISCUSSION, ActionKeys.DOWNLOAD,
						ActionKeys.SUBSCRIBE, ActionKeys.UPDATE, ActionKeys.VIEW
					}
				).put(
					RoleConstants.OWNER,
					ArrayUtil.filter(
						actionIds,
						actionId -> !Objects.equals(
							actionId, ActionKeys.PERMISSIONS))
				).put(
					RoleConstants.SITE_MEMBER,
					new String[] {
						ActionKeys.ADD_DISCUSSION, ActionKeys.DOWNLOAD,
						ActionKeys.SUBSCRIBE, ActionKeys.VIEW
					}
				).put(
					RoleConstants.SITE_OWNER, actionIds
				).build(),
				DLFileEntry.class.getName()));
	}

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

}