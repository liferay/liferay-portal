/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.converter;

import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.headless.admin.user.dto.v1_0.FileEntry;
import com.liferay.headless.admin.user.dto.v1_0.Link;
import com.liferay.headless.admin.user.dto.v1_0.SharedAsset;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.util.LinkUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.sharing.interpreter.SharingEntryInterpreter;
import com.liferay.sharing.interpreter.SharingEntryInterpreterProvider;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "dto.class.name=com.liferay.sharing.model.SharingEntry",
	service = DTOConverter.class
)
public class SharedAssetDTOConverter
	implements DTOConverter<SharingEntry, SharedAsset> {

	public static Link toLink(com.liferay.object.rest.dto.v1_0.Link link) {
		return new Link() {
			{
				setHref(link::getHref);
				setLabel(link::getLabel);
			}
		};
	}

	@Override
	public String getContentType() {
		return SharingEntry.class.getSimpleName();
	}

	@Override
	public SharedAsset toDTO(
			DTOConverterContext dtoConverterContext, SharingEntry sharingEntry)
		throws PortalException {

		Group group = _groupLocalService.getGroup(sharingEntry.getGroupId());
		String mimeType = _getMimeType(sharingEntry);
		SharingEntryInterpreter sharingEntryInterpreter =
			_sharingEntryInterpreterProvider.getSharingEntryInterpreter(
				sharingEntry);

		return new SharedAsset() {
			{
				setActionIds(
					() -> {
						String[] actionIds = TransformUtil.transformToArray(
							SharingEntryAction.getSharingEntryActions(
								sharingEntry.getActionIds()),
							SharingEntryAction::getActionId, String.class);

						if (!ArrayUtil.contains(
								actionIds,
								SharingEntryAction.UPDATE.getActionId()) &&
							_hasUpdatePermission(
								GuestOrUserUtil.getPermissionChecker(),
								sharingEntry)) {

							return ArrayUtil.append(
								actionIds,
								SharingEntryAction.UPDATE.getActionId());
						}

						return actionIds;
					});
				setAssetType(
					() -> {
						if (sharingEntryInterpreter == null) {
							return null;
						}

						return sharingEntryInterpreter.getAssetTypeTitle(
							sharingEntry, dtoConverterContext.getLocale());
					});
				setClassName(sharingEntry::getClassName);
				setClassPK(sharingEntry::getClassPK);
				setCreator(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.getUser(sharingEntry.getUserId())));
				setDateCreated(sharingEntry::getCreateDate);
				setDateModified(sharingEntry::getModifiedDate);
				setExternalReferenceCode(
					sharingEntry::getExternalReferenceCode);
				setFile(
					() -> NestedFieldsSupplier.supply(
						"file",
						fieldName -> _getFileEntry(sharingEntry.getClassPK())));
				setFileTypeIcon(
					() -> {
						if (StringUtil.equals(
								ObjectEntryFolder.class.getName(),
								sharingEntry.getClassName())) {

							return "folder";
						}

						if (Validator.isNull(mimeType)) {
							return null;
						}

						return _dlMimeTypeDisplayContext.getIconFileMimeType(
							mimeType);
					});
				setFileTypeIconColor(
					() -> {
						if (StringUtil.equals(
								ObjectEntryFolder.class.getName(),
								sharingEntry.getClassName())) {

							return "folder";
						}

						if (Validator.isNull(mimeType)) {
							return null;
						}

						return _dlMimeTypeDisplayContext.
							getCssClassFileMimeType(mimeType);
					});
				setId(sharingEntry::getSharingEntryId);
				setShareable(sharingEntry::isShareable);
				setSiteName(
					() -> group.getName(dtoConverterContext.getLocale()));
				setTitle(
					() -> {
						if (sharingEntryInterpreter == null) {
							return null;
						}

						return sharingEntryInterpreter.getTitle(
							sharingEntry, dtoConverterContext.getLocale());
					});
				setVisible(
					() -> {
						if ((sharingEntryInterpreter == null) ||
							!sharingEntryInterpreter.isVisible(sharingEntry)) {

							return false;
						}

						SharingConfiguration groupSharingConfiguration =
							_sharingConfigurationFactory.
								getGroupSharingConfiguration(group);

						return groupSharingConfiguration.isEnabled();
					});
			}
		};
	}

	private FileEntry _getFileEntry(long classPK) {
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			classPK);

		if (objectEntry == null) {
			return null;
		}

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!Objects.equals(
				objectDefinition.getObjectFolderExternalReferenceCode(),
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES)) {

			return null;
		}

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId())) {

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT) &&
				Objects.equals(objectField.getName(), "file")) {

				Map<String, Serializable> values = objectEntry.getValues();

				long fileEntryId = GetterUtil.getLong(values.get("file"));

				if (fileEntryId != 0) {
					return _getFileEntry(
						fileEntryId, objectDefinition, objectEntry,
						objectField);
				}
			}
		}

		return null;
	}

	private FileEntry _getFileEntry(
		long fileEntryId, ObjectDefinition objectDefinition,
		ObjectEntry objectEntry, ObjectField objectField) {

		FileEntry fileEntry = new FileEntry();

		DLFileEntry dlFileEntry = _dLFileEntryLocalService.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry == null) {
			return fileEntry;
		}

		fileEntry.setExternalReferenceCode(
			dlFileEntry::getExternalReferenceCode);
		fileEntry.setId(dlFileEntry::getFileEntryId);
		fileEntry.setLink(
			() -> toLink(
				LinkUtil.toLink(
					_dlAppService, dlFileEntry, _dlURLHelper,
					objectEntry.getGroupId(),
					objectDefinition.getExternalReferenceCode(), objectEntry,
					_objectEntryService, objectField,
					GuestOrUserUtil.getPermissionChecker(), _portal)));
		fileEntry.setName(dlFileEntry::getFileName);
		fileEntry.setPreviewURL(
			() -> {
				LiferayFileEntry liferayFileEntry = new LiferayFileEntry(
					dlFileEntry);

				String previewURL = _dlURLHelper.getPreviewURL(
					liferayFileEntry, liferayFileEntry.getFileVersion(), null,
					StringPool.BLANK);

				if (Validator.isNull(previewURL)) {
					return null;
				}

				return previewURL;
			});
		fileEntry.setThumbnailURL(
			() -> {
				String thumbnailURL = _dlURLHelper.getThumbnailSrc(
					new LiferayFileEntry(dlFileEntry), null);

				if (Validator.isNull(thumbnailURL)) {
					return null;
				}

				return thumbnailURL;
			});

		return fileEntry;
	}

	private String _getMimeType(SharingEntry sharingEntry)
		throws PortalException {

		if (StringUtil.equals(
				ObjectEntryFolder.class.getName(),
				sharingEntry.getClassName())) {

			return null;
		}

		if (StringUtil.equals(
				DLFileEntry.class.getName(), sharingEntry.getClassName())) {

			try {
				com.liferay.portal.kernel.repository.model.FileEntry fileEntry =
					_dlAppLocalService.getFileEntry(sharingEntry.getClassPK());

				return fileEntry.getMimeType();
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}

				return null;
			}
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				sharingEntry.getCompanyId(), sharingEntry.getClassName());

		if (objectDefinition == null) {
			return null;
		}

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "file");

		if (objectField == null) {
			return null;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			sharingEntry.getClassPK());

		if (objectEntry == null) {
			return null;
		}

		Map<String, Serializable> objectEntryValues = objectEntry.getValues();

		long file = (long)objectEntryValues.get("file");

		if (file == 0) {
			return null;
		}

		com.liferay.portal.kernel.repository.model.FileEntry fileEntry =
			_dlAppLocalService.fetchFileEntry(file);

		if (fileEntry == null) {
			return null;
		}

		return fileEntry.getMimeType();
	}

	private boolean _hasUpdatePermission(
			PermissionChecker permissionChecker, SharingEntry sharingEntry)
		throws PortalException {

		if (permissionChecker == null) {
			return false;
		}

		ModelResourcePermission<?> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				sharingEntry.getClassName());

		if (modelResourcePermission != null) {
			return modelResourcePermission.contains(
				permissionChecker, sharingEntry.getClassPK(),
				SharingEntryAction.UPDATE.getActionId());
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SharedAssetDTOConverter.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryLocalService _dLFileEntryLocalService;

	@Reference
	private DLMimeTypeDisplayContext _dlMimeTypeDisplayContext;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SharingConfigurationFactory _sharingConfigurationFactory;

	@Reference
	private SharingEntryInterpreterProvider _sharingEntryInterpreterProvider;

	@Reference
	private UserLocalService _userLocalService;

}