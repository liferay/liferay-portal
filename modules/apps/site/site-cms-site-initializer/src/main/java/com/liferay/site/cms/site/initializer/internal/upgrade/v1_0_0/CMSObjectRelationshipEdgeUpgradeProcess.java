/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v1_0_0;

import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.tree.constants.TreeConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Víctor Galán
 */
public class CMSObjectRelationshipEdgeUpgradeProcess extends UpgradeProcess {

	public CMSObjectRelationshipEdgeUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFolderLocalService objectFolderLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFolderLocalService = objectFolderLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private void _checkMaxDepth(List<ObjectDefinition> objectDefinitionPath)
		throws UpgradeException {

		if ((objectDefinitionPath.size() - 1) > TreeConstants.MAX_HEIGHT) {
			throw new UpgradeException(
				_getExceptionMessage(objectDefinitionPath));
		}

		ObjectDefinition currentObjectDefinition = objectDefinitionPath.get(
			objectDefinitionPath.size() - 1);

		for (ObjectRelationship objectRelationship :
				_objectRelationshipLocalService.getObjectRelationships(
					currentObjectDefinition.getObjectDefinitionId(),
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false)) {

			if (objectRelationship.isSelf() ||
				!objectRelationship.compareType(
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				continue;
			}

			ObjectDefinition childObjectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectRelationship.getObjectDefinitionId2());

			if ((childObjectDefinition == null) ||
				objectDefinitionPath.contains(childObjectDefinition)) {

				continue;
			}

			objectDefinitionPath.add(childObjectDefinition);

			_checkMaxDepth(objectDefinitionPath);

			objectDefinitionPath.remove(objectDefinitionPath.size() - 1);
		}
	}

	private List<ObjectRelationship> _getCMSObjectRelationships(long companyId)
		throws UpgradeException {

		List<ObjectRelationship> objectRelationships = new ArrayList<>();

		Set<Long> objectDefinitionIds = new HashSet<>();

		for (String externalReferenceCode :
				_CMS_ROOT_FOLDER_EXTERNAL_REFERENCE_CODES) {

			ObjectFolder objectFolder =
				_objectFolderLocalService.
					fetchObjectFolderByExternalReferenceCode(
						externalReferenceCode, companyId);

			if (objectFolder == null) {
				continue;
			}

			for (ObjectDefinition objectDefinition :
					_objectDefinitionLocalService.
						getObjectFolderObjectDefinitions(
							objectFolder.getObjectFolderId())) {

				_getObjectRelationships(
					objectDefinition.getObjectDefinitionId(),
					objectDefinitionIds, objectRelationships);
			}
		}

		return _sortObjectRelationships(objectRelationships);
	}

	private String _getExceptionMessage(
		List<ObjectDefinition> objectDefinitionPath) {

		return StringBundler.concat(
			"This CMS chain exceeds the maximum nesting depth (",
			TreeConstants.MAX_HEIGHT, "). ",
			StringUtil.merge(
				TransformUtil.transform(
					objectDefinitionPath, ObjectDefinition::getShortName),
				" -> "));
	}

	private void _getObjectRelationships(
		long objectDefinitionId, Set<Long> objectDefinitionIds,
		List<ObjectRelationship> objectRelationships) {

		if (!objectDefinitionIds.add(objectDefinitionId)) {
			return;
		}

		for (ObjectRelationship objectRelationship :
				_objectRelationshipLocalService.getObjectRelationships(
					objectDefinitionId,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false)) {

			if (objectRelationship.isSelf() ||
				!objectRelationship.compareType(
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				continue;
			}

			objectRelationships.add(objectRelationship);

			_getObjectRelationships(
				objectRelationship.getObjectDefinitionId2(),
				objectDefinitionIds, objectRelationships);
		}
	}

	private List<ObjectRelationship> _sortObjectRelationships(
			List<ObjectRelationship> objectRelationships)
		throws UpgradeException {

		List<ObjectRelationship> sortedObjectRelationships = new ArrayList<>(
			objectRelationships.size());

		while (!objectRelationships.isEmpty()) {
			List<ObjectRelationship> rootObjectRelationships =
				new ArrayList<>();

			for (ObjectRelationship objectRelationship : objectRelationships) {
				long objectDefinitionId1 =
					objectRelationship.getObjectDefinitionId1();

				if (!ListUtil.exists(
						objectRelationships,
						parentObjectRelationship ->
							parentObjectRelationship.getObjectDefinitionId2() ==
								objectDefinitionId1)) {

					rootObjectRelationships.add(objectRelationship);
				}
			}

			if (rootObjectRelationships.isEmpty()) {
				String objectRelationshipNames = StringUtil.merge(
					TransformUtil.transform(
						objectRelationships, ObjectRelationship::getName),
					StringPool.COMMA_AND_SPACE);

				throw new UpgradeException(
					"These CMS object relationships form a cycle: " +
						objectRelationshipNames);
			}

			objectRelationships.removeAll(rootObjectRelationships);
			sortedObjectRelationships.addAll(rootObjectRelationships);
		}

		return sortedObjectRelationships;
	}

	private void _upgradeCompany(long companyId) throws PortalException {
		for (String externalReferenceCode :
				_CMS_ROOT_FOLDER_EXTERNAL_REFERENCE_CODES) {

			ObjectFolder objectFolder =
				_objectFolderLocalService.
					fetchObjectFolderByExternalReferenceCode(
						externalReferenceCode, companyId);

			if (objectFolder == null) {
				continue;
			}

			for (ObjectDefinition objectDefinition :
					_objectDefinitionLocalService.
						getObjectFolderObjectDefinitions(
							objectFolder.getObjectFolderId())) {

				_checkMaxDepth(ListUtil.fromArray(objectDefinition));
			}
		}

		List<ObjectRelationship> objectRelationships =
			_getCMSObjectRelationships(companyId);

		if (objectRelationships.isEmpty()) {
			return;
		}

		for (ObjectRelationship objectRelationship : objectRelationships) {
			ObjectRelationship currentObjectRelationship =
				_objectRelationshipLocalService.fetchObjectRelationship(
					objectRelationship.getObjectRelationshipId());

			if ((currentObjectRelationship == null) ||
				currentObjectRelationship.isEdge()) {

				continue;
			}

			_objectRelationshipLocalService.updateObjectRelationship(
				currentObjectRelationship.getExternalReferenceCode(),
				currentObjectRelationship.getObjectRelationshipId(),
				currentObjectRelationship.getParameterObjectFieldId(),
				currentObjectRelationship.getDeletionType(), true,
				currentObjectRelationship.getLabelMap(), null);
		}
	}

	private static final String[] _CMS_ROOT_FOLDER_EXTERNAL_REFERENCE_CODES = {
		ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
		ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
	};

	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}