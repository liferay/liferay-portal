/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.uad.anonymizer;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.internal.uad.constants.ObjectUADConstants;
import com.liferay.object.internal.uad.util.ObjectEntryUADUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.search.StrictObjectReindexThreadLocal;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.user.associated.data.anonymizer.DynamicQueryUADAnonymizer;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryUADAnonymizer
	extends DynamicQueryUADAnonymizer<ObjectEntry> {

	public ObjectEntryUADAnonymizer(
		AssetEntryLocalService assetEntryLocalService,
		ObjectDefinition objectDefinition,
		ObjectEntryLocalService objectEntryLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
		_objectDefinition = objectDefinition;
		_objectEntryLocalService = objectEntryLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	@Override
	public void autoAnonymize(
			ObjectEntry objectEntry, long userId, User anonymousUser)
		throws PortalException {

		if (objectEntry.getUserId() == userId) {
			objectEntry.setUserId(anonymousUser.getUserId());
			objectEntry.setUserName(anonymousUser.getFullName());
		}

		if (objectEntry.getStatusByUserId() == userId) {
			objectEntry.setStatusByUserId(anonymousUser.getUserId());
			objectEntry.setStatusByUserName(anonymousUser.getFullName());
		}

		objectEntry = _objectEntryLocalService.updateObjectEntry(objectEntry);

		if (_objectDefinition.isEnableIndexSearch()) {
			Indexer<ObjectEntry> indexer = IndexerRegistryUtil.getIndexer(
				_objectDefinition.getClassName());

			try (SafeCloseable safeCloseable =
					StrictObjectReindexThreadLocal.
						setStrictObjectReindexWithSafeCloseable(true)) {

				indexer.reindex(objectEntry);
			}
		}

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			_objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		if ((assetEntry != null) && (assetEntry.getUserId() == userId)) {
			assetEntry.setUserId(anonymousUser.getUserId());
			assetEntry.setUserName(anonymousUser.getFullName());

			_assetEntryLocalService.updateAssetEntry(assetEntry);
		}

		for (ResourcePermission resourcePermission :
				_resourcePermissionLocalService.getResourcePermissions(
					objectEntry.getCompanyId(),
					_objectDefinition.getClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(objectEntry.getPrimaryKey()))) {

			if (resourcePermission.getOwnerId() != userId) {
				continue;
			}

			resourcePermission.setOwnerId(anonymousUser.getUserId());

			_resourcePermissionLocalService.updateResourcePermission(
				resourcePermission);
		}
	}

	@Override
	public void delete(ObjectEntry objectEntry) throws PortalException {
		try (SafeCloseable safeCloseable =
				ObjectEntryThreadLocal.
					setDisassociateRelatedModelsWithSafeCloseable(true)) {

			_objectEntryLocalService.deleteObjectEntry(objectEntry);
		}
	}

	@Override
	public Class<ObjectEntry> getTypeClass() {
		return ObjectEntry.class;
	}

	@Override
	public String getTypeKey() {
		return _objectDefinition.getClassName();
	}

	@Override
	protected ActionableDynamicQuery doGetActionableDynamicQuery() {
		return ObjectEntryUADUtil.addActionableDynamicQueryCriteria(
			_objectEntryLocalService.getActionableDynamicQuery(),
			_objectDefinition.getObjectDefinitionId());
	}

	@Override
	protected String[] doGetUserIdFieldNames() {
		return ObjectUADConstants.USER_ID_FIELD_NAMES_OBJECT_ENTRY;
	}

	@Override
	protected ActionableDynamicQuery getActionableDynamicQuery(long userId) {
		return ObjectEntryUADUtil.addActionableDynamicQueryCriteria(
			doGetActionableDynamicQuery(), doGetUserIdFieldNames(), userId);
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}