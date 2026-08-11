/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.model.listener;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.exception.DuplicatePIMLinkException;
import com.liferay.site.pim.site.initializer.internal.util.PIMLinkUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMObjectEntryFolderUtil;
import com.liferay.site.pim.site.initializer.link.PIMLinkType;
import com.liferay.site.pim.site.initializer.link.PIMLinkTypeRegistry;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onBeforeCreate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private boolean _isPIMLinkObjectEntry(ObjectEntry objectEntry) {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		return Objects.equals(
			objectDefinition.getExternalReferenceCode(),
			PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK);
	}

	private boolean _isPIMProductObjectEntry(ObjectEntry objectEntry) {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				objectEntry.getCompanyId());

		if ((objectFolder != null) &&
			(objectDefinition.getObjectFolderId() ==
				objectFolder.getObjectFolderId())) {

			return true;
		}

		return false;
	}

	private void _onBeforeCreate(ObjectEntry objectEntry)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-96666") ||
			(objectEntry.getObjectDefinition() == null)) {

			return;
		}

		if (_isPIMLinkObjectEntry(objectEntry)) {
			_validate(objectEntry);
		}
		else if (_isPIMProductObjectEntry(objectEntry)) {
			_setObjectEntryFolderId(objectEntry);
		}
	}

	private void _setObjectEntryFolderId(ObjectEntry objectEntry)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntry.getObjectEntryFolderId());

		if ((objectEntryFolder == null) ||
			Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) ||
			Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			objectEntryFolder =
				PIMObjectEntryFolderUtil.getOrAddProductsObjectEntryFolder(
					_groupLocalService.getGroup(objectEntry.getGroupId()),
					_objectEntryFolderLocalService);

			objectEntry.setObjectEntryFolderId(
				objectEntryFolder.getObjectEntryFolderId());
		}
	}

	private void _validate(ObjectEntry objectEntry) throws PortalException {
		Map<String, Serializable> values = objectEntry.getValues();

		if (Validator.isNull(
				MapUtil.getString(
					values, "sourceClassExternalReferenceCode")) ||
			Validator.isNull(MapUtil.getString(values, "sourceClassName"))) {

			throw new UnsupportedOperationException();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinitionByClassName(
				objectEntry.getCompanyId(),
				MapUtil.getString(values, "sourceClassName"));

		objectEntry = _objectEntryLocalService.getObjectEntry(
			MapUtil.getString(values, "sourceClassExternalReferenceCode"),
			objectEntry.getGroupId(), objectDefinition.getObjectDefinitionId());

		PIMLinkUtil.checkPermission(objectEntry, ActionKeys.UPDATE);

		PIMLinkType pimLinkType = _pimLinkTypeRegistry.getPIMLinkType(
			MapUtil.getString(values, "type"));

		if ((pimLinkType == null) || !pimLinkType.isClustered()) {
			throw new UnsupportedOperationException();
		}

		objectEntry = PIMLinkUtil.fetchPIMLinkObjectEntry(
			objectEntry.getCompanyId(), _filterFactory,
			objectEntry.getGroupId(), objectEntry.getExternalReferenceCode(),
			objectEntry.getModelClassName(), MapUtil.getString(values, "type"));

		if (objectEntry != null) {
			throw new DuplicatePIMLinkException();
		}
	}

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private PIMLinkTypeRegistry _pimLinkTypeRegistry;

}