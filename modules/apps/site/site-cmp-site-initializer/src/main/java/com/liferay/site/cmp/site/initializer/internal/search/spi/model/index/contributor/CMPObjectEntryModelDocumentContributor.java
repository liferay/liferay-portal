/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.search.spi.model.index.contributor;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.site.cmp.site.initializer.internal.util.CMPObjectEntryUtil;

/**
 * @author Pedro Leite
 */
public class CMPObjectEntryModelDocumentContributor
	implements ModelDocumentContributor<ObjectEntry> {

	public CMPObjectEntryModelDocumentContributor(
		FilterFactory<Predicate> filterFactory,
		GroupLocalService groupLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ObjectEntryLocalService objectEntryLocalService) {

		_filterFactory = filterFactory;
		_groupLocalService = groupLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	public void contribute(Document document, ObjectEntry objectEntry) {
		try {
			_contribute(document, objectEntry);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _addKeyword(
		Document document, String fieldName, long[] objectEntryIds) {

		if (objectEntryIds.length == 0) {
			return;
		}

		document.addKeyword(fieldName, objectEntryIds);
	}

	private void _contribute(Document document, ObjectEntry objectEntry)
		throws PortalException {

		if (!LicenseManagerUtil.isAppEnabled(App.CMP)) {
			return;
		}

		ObjectEntryFolder rootObjectEntryFolder = _getRootObjectEntryFolder(
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntry.getObjectEntryFolderId()));

		if (rootObjectEntryFolder == null) {
			return;
		}

		String externalReferenceCode =
			rootObjectEntryFolder.getExternalReferenceCode();

		if (!StringUtil.equals(
				externalReferenceCode,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) &&
			!StringUtil.equals(
				externalReferenceCode,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			return;
		}

		long[] cmpTaskObjectEntryIds =
			CMPObjectEntryUtil.getLinkedObjectEntryIds(
				_filterFactory, _groupLocalService, "L_CMP_TASK_LINK",
				_objectDefinitionLocalService, objectEntry,
				_objectEntryLocalService,
				"r_cmpTaskToCMPTaskLinks_c_cmpTaskId");

		_addKeyword(
			document, "cmpProjectObjectEntryIds",
			_getCMPProjectObjectEntryIds(cmpTaskObjectEntryIds, objectEntry));
		_addKeyword(document, "cmpTaskObjectEntryIds", cmpTaskObjectEntryIds);
	}

	private long[] _getCMPProjectObjectEntryIds(
			long[] cmpTaskObjectEntryIds, ObjectEntry objectEntry)
		throws PortalException {

		return ArrayUtil.unique(
			ArrayUtil.append(
				CMPObjectEntryUtil.getLinkedObjectEntryIds(
					_filterFactory, _groupLocalService, "L_CMP_PROJECT_LINK",
					_objectDefinitionLocalService, objectEntry,
					_objectEntryLocalService,
					"r_cmpProjectToCMPProjectLinks_c_cmpProjectId"),
				TransformUtil.transformToLongArray(
					ListUtil.fromArray(cmpTaskObjectEntryIds),
					cmpTaskObjectEntryId -> {
						ObjectEntry cmpTaskObjectEntry =
							_objectEntryLocalService.fetchObjectEntry(
								cmpTaskObjectEntryId);

						if (cmpTaskObjectEntry == null) {
							return null;
						}

						long cmpProjectObjectEntryId = MapUtil.getLong(
							cmpTaskObjectEntry.getValues(),
							"r_cmpProjectToCMPTasks_c_cmpProjectId");

						if (cmpProjectObjectEntryId == 0) {
							return null;
						}

						return cmpProjectObjectEntryId;
					})));
	}

	private ObjectEntryFolder _getRootObjectEntryFolder(
		ObjectEntryFolder objectEntryFolder) {

		if (objectEntryFolder == null) {
			return null;
		}

		String[] parts = StringUtil.split(
			objectEntryFolder.getTreePath(), CharPool.SLASH);

		if (parts.length <= 2) {
			return objectEntryFolder;
		}

		return _objectEntryFolderLocalService.fetchObjectEntryFolder(
			GetterUtil.getLong(parts[1]));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMPObjectEntryModelDocumentContributor.class);

	private final FilterFactory<Predicate> _filterFactory;
	private final GroupLocalService _groupLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;

}