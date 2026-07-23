/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class ObjectEntryFolderModelListener
	extends BaseModelListener<ObjectEntryFolder> {

	@Override
	public void onAfterCreate(ObjectEntryFolder objectEntryFolder)
		throws ModelListenerException {

		try {
			_onAfterCreate(objectEntryFolder);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(ObjectEntryFolder objectEntryFolder)
		throws ModelListenerException {

		try {
			_onAfterRemove(objectEntryFolder);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(
			ObjectEntryFolder originalObjectEntryFolder,
			ObjectEntryFolder objectEntryFolder)
		throws ModelListenerException {

		try {
			if (!Objects.equals(
					originalObjectEntryFolder.getExternalReferenceCode(),
					objectEntryFolder.getExternalReferenceCode())) {

				_updateCMSDefaultPermissionObjectEntry(
					objectEntryFolder.getExternalReferenceCode(),
					objectEntryFolder,
					originalObjectEntryFolder.getExternalReferenceCode());
			}

			if (originalObjectEntryFolder.getParentObjectEntryFolderId() !=
					objectEntryFolder.getParentObjectEntryFolderId()) {

				_updateCMSDefaultPermissions(objectEntryFolder);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _onAfterCreate(ObjectEntryFolder objectEntryFolder)
		throws Exception {

		CMSDefaultPermissionUtil.addCMSDefaultPermissions(
			objectEntryFolder, _filterFactory);
	}

	private void _onAfterRemove(ObjectEntryFolder objectEntryFolder)
		throws PortalException {

		_sharingEntryLocalService.deleteSharingEntries(
			_portal.getClassNameId(ObjectEntryFolder.class.getName()),
			objectEntryFolder.getObjectEntryFolderId());

		ObjectDefinition cmsDefaultPermissionObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION",
					objectEntryFolder.getCompanyId());

		if (cmsDefaultPermissionObjectDefinition == null) {
			return;
		}

		ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
			objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
			objectEntryFolder.getExternalReferenceCode(),
			objectEntryFolder.getModelClassName(), _filterFactory);

		if (objectEntry == null) {
			return;
		}

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	private void _updateCMSDefaultPermissionObjectEntry(
			String externalReferenceCode, ObjectEntryFolder objectEntryFolder,
			String originalExternalReferenceCode)
		throws Exception {

		ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
			objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
			originalExternalReferenceCode,
			objectEntryFolder.getModelClassName(), _filterFactory);

		if (objectEntry == null) {
			return;
		}

		CMSDefaultPermissionUtil.updateClassExternalReferenceCode(
			objectEntry, externalReferenceCode, objectEntryFolder.getUserId());
	}

	private void _updateCMSDefaultPermissions(
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		JSONObject defaultPermissionsJSONObject =
			CMSDefaultPermissionUtil.getCMSDefaultPermissionJSONObject(
				objectEntryFolder, _filterFactory);

		if ((defaultPermissionsJSONObject == null) ||
			JSONUtil.isEmpty(defaultPermissionsJSONObject)) {

			return;
		}

		ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
			objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
			objectEntryFolder.getExternalReferenceCode(),
			objectEntryFolder.getModelClassName(), _filterFactory);

		if (objectEntry != null) {
			_objectEntryLocalService.deleteObjectEntry(
				objectEntry.getObjectEntryId());
		}

		CMSDefaultPermissionUtil.addCMSDefaultPermissions(
			objectEntryFolder, _filterFactory);
	}

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

}