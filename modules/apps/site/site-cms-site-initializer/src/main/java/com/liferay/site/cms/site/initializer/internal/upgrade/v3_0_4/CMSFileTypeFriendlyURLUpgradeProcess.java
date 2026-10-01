/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_4;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.cms.site.initializer.internal.util.CMSFileTypeUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class CMSFileTypeFriendlyURLUpgradeProcess extends UpgradeProcess {

	public CMSFileTypeFriendlyURLUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		CompanyLocalService companyLocalService,
		DLFileEntryLocalService dlFileEntryLocalService,
		FriendlyURLEntryLocalService friendlyURLEntryLocalService,
		FriendlyURLNormalizer friendlyURLNormalizer,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFolderLocalService objectFolderLocalService) {

		_classNameLocalService = classNameLocalService;
		_companyLocalService = companyLocalService;
		_dlFileEntryLocalService = dlFileEntryLocalService;
		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
		_friendlyURLNormalizer = friendlyURLNormalizer;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFolderLocalService = objectFolderLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private FriendlyURLEntry _fetchFriendlyURLEntry(
		long groupId, long classNameId, String urlTitle) {

		return _friendlyURLEntryLocalService.fetchFriendlyURLEntry(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			urlTitle);
	}

	private String _getUniqueUrlTitle(
		DLFileEntry dlFileEntry, long fileEntryClassNameId, long groupId,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		long objectEntryClassNameId, String urlTitle) {

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURLEntryLocalization.class.getName(), "urlTitle");

		String curUrlTitle = urlTitle;

		for (int i = 1;
			 !_isUrlTitleAvailable(
				 dlFileEntry, fileEntryClassNameId, groupId, objectDefinition,
				 objectEntry, objectEntryClassNameId, curUrlTitle);
			 i++) {

			String suffix = StringPool.DASH + i;

			String prefix = urlTitle;

			if ((prefix.length() + suffix.length()) > maxLength) {
				prefix = prefix.substring(0, maxLength - suffix.length());
			}

			curUrlTitle = _friendlyURLNormalizer.normalizeWithEncoding(
				prefix + suffix);
		}

		return curUrlTitle;
	}

	private boolean _isUrlTitleAvailable(
		DLFileEntry dlFileEntry, long fileEntryClassNameId, long groupId,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		long objectEntryClassNameId, String urlTitle) {

		FriendlyURLEntry objectEntryFriendlyURLEntry = _fetchFriendlyURLEntry(
			groupId, objectEntryClassNameId, urlTitle);

		if ((objectEntryFriendlyURLEntry != null) &&
			(objectEntryFriendlyURLEntry.getClassPK() !=
				objectEntry.getObjectEntryId())) {

			return false;
		}

		FriendlyURLEntry fileEntryFriendlyURLEntry = _fetchFriendlyURLEntry(
			dlFileEntry.getGroupId(), fileEntryClassNameId, urlTitle);

		if ((fileEntryFriendlyURLEntry == null) ||
			CMSFileTypeUtil.isObjectEntryAttachment(
				_dlFileEntryLocalService.fetchDLFileEntry(
					fileEntryFriendlyURLEntry.getClassPK()),
				objectDefinition, objectEntry.getObjectEntryId())) {

			return true;
		}

		return false;
	}

	private void _upgradeCompany(long companyId) throws PortalException {
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				companyId);

		if (objectFolder == null) {
			return;
		}

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectFolderObjectDefinitions(
					objectFolder.getObjectFolderId())) {

			if (CMSFileTypeUtil.hasFileObjectField(objectDefinition)) {
				_upgradeObjectDefinition(objectDefinition);
			}
		}
	}

	private void _upgradeObjectDefinition(ObjectDefinition objectDefinition)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_objectEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"objectDefinitionId",
					objectDefinition.getObjectDefinitionId())));
		actionableDynamicQuery.setPerformActionMethod(
			(ObjectEntry objectEntry) -> _upgradeObjectEntry(
				objectDefinition, objectEntry));

		actionableDynamicQuery.performActions();
	}

	private void _upgradeObjectEntry(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws PortalException {

		Map<String, Serializable> values = objectEntry.getValues();

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			GetterUtil.getLong(values.get("file")));

		if (!CMSFileTypeUtil.isObjectEntryAttachment(
				dlFileEntry, objectDefinition,
				objectEntry.getObjectEntryId())) {

			return;
		}

		long objectEntryClassNameId = _classNameLocalService.getClassNameId(
			objectDefinition.getClassName());

		FriendlyURLEntry objectEntryFriendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				objectEntryClassNameId, objectEntry.getObjectEntryId());

		if (objectEntryFriendlyURLEntry == null) {
			return;
		}

		String urlTitle = objectEntryFriendlyURLEntry.getUrlTitle(
			objectEntry.getDefaultLanguageId());

		if (Validator.isNull(urlTitle)) {
			return;
		}

		long fileEntryClassNameId = _classNameLocalService.getClassNameId(
			FileEntry.class);

		FriendlyURLEntry fileEntryFriendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				fileEntryClassNameId, dlFileEntry.getFileEntryId());

		if ((fileEntryFriendlyURLEntry != null) &&
			urlTitle.equals(fileEntryFriendlyURLEntry.getUrlTitle())) {

			return;
		}

		long groupId = objectEntry.getNonzeroGroupId();

		String uniqueUrlTitle = _getUniqueUrlTitle(
			dlFileEntry, fileEntryClassNameId, groupId, objectDefinition,
			objectEntry, objectEntryClassNameId, urlTitle);

		if (!uniqueUrlTitle.equals(urlTitle)) {
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				groupId, objectEntryClassNameId, objectEntry.getObjectEntryId(),
				objectEntry.getDefaultLanguageId(),
				HashMapBuilder.putAll(
					objectEntryFriendlyURLEntry.getLanguageIdToUrlTitleMap()
				).put(
					objectEntry.getDefaultLanguageId(), uniqueUrlTitle
				).build(),
				new ServiceContext());

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Changed the friendly URL of object entry ",
						objectEntry.getObjectEntryId(), " from \"", urlTitle,
						"\" to \"", uniqueUrlTitle, "\""));
			}
		}

		FriendlyURLEntry friendlyURLEntry = _fetchFriendlyURLEntry(
			dlFileEntry.getGroupId(), fileEntryClassNameId, uniqueUrlTitle);

		if ((friendlyURLEntry != null) &&
			(friendlyURLEntry.getClassPK() != dlFileEntry.getFileEntryId())) {

			_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
				dlFileEntry.getGroupId(), fileEntryClassNameId,
				friendlyURLEntry.getClassPK());
		}

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			dlFileEntry.getGroupId(), fileEntryClassNameId,
			dlFileEntry.getFileEntryId(), objectEntry.getDefaultLanguageId(),
			HashMapBuilder.put(
				objectEntry.getDefaultLanguageId(), uniqueUrlTitle
			).build(),
			new ServiceContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSFileTypeFriendlyURLUpgradeProcess.class);

	private final ClassNameLocalService _classNameLocalService;
	private final CompanyLocalService _companyLocalService;
	private final DLFileEntryLocalService _dlFileEntryLocalService;
	private final FriendlyURLEntryLocalService _friendlyURLEntryLocalService;
	private final FriendlyURLNormalizer _friendlyURLNormalizer;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;

}