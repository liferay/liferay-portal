/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.verify;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.internal.util.DDMFormUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.processor.RawMetadataProcessor;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileVersion;
import com.liferay.portal.repository.liferayrepository.model.LiferayFolder;
import com.liferay.portal.verify.VerifyProcess;

import java.io.InputStream;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Douglas Wong
 * @author Alexander Chow
 */
@Component(service = VerifyProcess.class)
public class DLServiceVerifyProcess extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		_checkDLFileEntryMetadata();
		_checkDDMStructureDefinition();
		_checkMimeTypes();
		_updateClassNameId();
		_updateFileEntryAssets();
		_updateFolderAssets();

		if (FeatureFlagManagerUtil.isEnabled("LPS-157670")) {
			updateStagedPortletNames();
		}
	}

	protected void updateStagedPortletNames() throws PortalException {
		ActionableDynamicQuery groupActionableDynamicQuery =
			_groupLocalService.getActionableDynamicQuery();

		groupActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property siteProperty = PropertyFactoryUtil.forName("site");

				dynamicQuery.add(siteProperty.eq(Boolean.TRUE));
			});
		groupActionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod<Group>)group -> {
				UnicodeProperties typeSettingsUnicodeProperties =
					group.getTypeSettingsProperties();

				if (typeSettingsUnicodeProperties == null) {
					return;
				}

				String propertyKey = _staging.getStagedPortletId(
					DLPortletKeys.DOCUMENT_LIBRARY);

				String propertyValue =
					typeSettingsUnicodeProperties.getProperty(propertyKey);

				if (Validator.isNull(propertyValue)) {
					return;
				}

				typeSettingsUnicodeProperties.remove(propertyKey);

				propertyKey = _staging.getStagedPortletId(
					DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

				typeSettingsUnicodeProperties.put(propertyKey, propertyValue);

				group.setTypeSettingsProperties(typeSettingsUnicodeProperties);

				_groupLocalService.updateGroup(group);
			});

		groupActionableDynamicQuery.performActions();
	}

	private void _checkDDMStructureDefinition() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				Group group = _groupLocalService.getCompanyGroup(companyId);
				String name =
					com.liferay.portal.kernel.metadata.RawMetadataProcessor.
						TIKA_RAW_METADATA;

				DDMStructure ddmStructure =
					_ddmStructureLocalService.fetchStructure(
						group.getGroupId(),
						_portal.getClassNameId(RawMetadataProcessor.class),
						name);

				if (ddmStructure != null) {
					DDMForm ddmForm = DDMFormUtil.buildDDMForm(
						_portal.getSiteDefaultLocale(group.getGroupId()));

					String definition = _serializeJSONDDMForm(ddmForm);

					if (!definition.equals(ddmStructure.getDefinition())) {
						ddmStructure.setDDMForm(ddmForm);

						_ddmStructureLocalService.updateDDMStructure(
							ddmStructure);
					}
				}
			});
	}

	private void _checkDLFileEntryMetadata() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			List<DLFileEntryMetadata> mismatchedCompanyIdDLFileEntryMetadatas =
				_dlFileEntryMetadataLocalService.
					getMismatchedCompanyIdFileEntryMetadatas();

			if (_log.isDebugEnabled()) {
				int size = mismatchedCompanyIdDLFileEntryMetadatas.size();

				_log.debug(
					StringBundler.concat(
						"Deleting ", size,
						" file entry metadatas with mismatched company IDs"));
			}

			for (DLFileEntryMetadata dlFileEntryMetadata :
					mismatchedCompanyIdDLFileEntryMetadatas) {

				_deleteUnusedDLFileEntryMetadata(dlFileEntryMetadata);
			}

			List<DLFileEntryMetadata> noStructuresDLFileEntryMetadatas =
				_dlFileEntryMetadataLocalService.
					getNoStructuresFileEntryMetadatas();

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Deleting " + noStructuresDLFileEntryMetadatas.size() +
						" file entry metadatas with no structures");
			}

			for (DLFileEntryMetadata dlFileEntryMetadata :
					noStructuresDLFileEntryMetadatas) {

				_deleteUnusedDLFileEntryMetadata(dlFileEntryMetadata);
			}
		}
	}

	private void _checkFileVersionMimeTypes(String[] originalMimeTypes)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			_dlFileVersionLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Criterion criterion = RestrictionsFactoryUtil.eq(
					"mimeType", originalMimeTypes[0]);

				for (int i = 1; i < originalMimeTypes.length; i++) {
					criterion = RestrictionsFactoryUtil.or(
						criterion,
						RestrictionsFactoryUtil.eq(
							"mimeType", originalMimeTypes[i]));
				}

				dynamicQuery.add(criterion);
			});
		actionableDynamicQuery.setPerformActionMethod(
			(DLFileVersion dlFileVersion) -> {
				String title = DLUtil.getTitleWithExtension(
					dlFileVersion.getTitle(), dlFileVersion.getExtension());

				try (InputStream inputStream =
						_dlFileEntryLocalService.getFileAsStream(
							dlFileVersion.getFileEntryId(),
							dlFileVersion.getVersion(), false)) {

					String mimeType = MimeTypesUtil.getContentType(
						inputStream, title);

					if (mimeType.equals(dlFileVersion.getMimeType())) {
						return;
					}

					dlFileVersion.setMimeType(mimeType);

					dlFileVersion =
						_dlFileVersionLocalService.updateDLFileVersion(
							dlFileVersion);

					try {
						DLFileEntry dlFileEntry = dlFileVersion.getFileEntry();

						if (Objects.equals(
								dlFileEntry.getVersion(),
								dlFileVersion.getVersion())) {

							dlFileEntry.setMimeType(mimeType);

							_dlFileEntryLocalService.updateDLFileEntry(
								dlFileEntry);
						}
					}
					catch (PortalException portalException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to get file entry " +
									dlFileVersion.getFileEntryId(),
								portalException);
						}
					}
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						DLFileEntry dlFileEntry =
							_dlFileEntryLocalService.fetchDLFileEntry(
								dlFileVersion.getFileEntryId());

						if (dlFileEntry == null) {
							_log.warn(
								"Unable to find file entry associated with " +
									"file version " +
										dlFileVersion.getFileVersionId(),
								exception);
						}
						else {
							_log.warn(
								StringBundler.concat(
									"Unable to find file version ",
									dlFileVersion.getVersion(),
									" for file entry ", dlFileEntry.getName()),
								exception);
						}
					}
				}
			});

		if (_log.isDebugEnabled()) {
			long count = actionableDynamicQuery.performCount();

			_log.debug(
				StringBundler.concat(
					"Processing ", count, " file versions with mime types: ",
					StringUtil.merge(originalMimeTypes, StringPool.COMMA)));
		}

		actionableDynamicQuery.performActions();
	}

	private void _checkMimeTypes() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_checkFileVersionMimeTypes(
				new String[] {
					ContentTypes.APPLICATION_OCTET_STREAM,
					_MS_OFFICE_2010_TEXT_XML_UTF8
				});

			if (_log.isDebugEnabled()) {
				_log.debug("Fixed file entries with invalid mime types");
			}
		}
	}

	private void _deleteUnusedDLFileEntryMetadata(
			DLFileEntryMetadata dlFileEntryMetadata)
		throws Exception {

		_dlFileEntryMetadataLocalService.deleteFileEntryMetadata(
			dlFileEntryMetadata);
	}

	private String _serializeJSONDDMForm(DDMForm ddmForm) {
		DDMFormSerializerSerializeRequest.Builder builder =
			DDMFormSerializerSerializeRequest.Builder.newBuilder(ddmForm);

		DDMFormSerializerSerializeResponse ddmFormSerializerSerializeResponse =
			_jsonDDMFormSerializer.serialize(builder.build());

		return ddmFormSerializerSerializeResponse.getContent();
	}

	private void _updateClassNameId() {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			runSQL(
				"update DLFileEntry set classNameId = 0 where classNameId is " +
					"null");
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to fix file entries where class name ID is null",
					exception);
			}
		}
	}

	private void _updateFileEntryAssets() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			List<DLFileEntry> dlFileEntries =
				_dlFileEntryLocalService.getNoAssetFileEntries();

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Processing " + dlFileEntries.size() +
						" file entries with no asset");
			}

			for (DLFileEntry dlFileEntry : dlFileEntries) {
				FileEntry fileEntry = new LiferayFileEntry(dlFileEntry);
				FileVersion fileVersion = new LiferayFileVersion(
					dlFileEntry.getFileVersion());

				try {
					_dlAppHelperLocalService.updateAsset(
						dlFileEntry.getUserId(), fileEntry, fileVersion,
						new ServiceContext());
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to update asset for file entry ",
								dlFileEntry.getFileEntryId(), ": ",
								exception.getMessage()));
					}
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Assets verified for file entries");
			}
		}
	}

	private void _updateFolderAssets() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			List<DLFolder> dlFolders =
				_dlFolderLocalService.getNoAssetFolders();

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Processing " + dlFolders.size() +
						" folders with no asset");
			}

			for (DLFolder dlFolder : dlFolders) {
				Folder folder = new LiferayFolder(dlFolder);

				try {
					_dlAppHelperLocalService.updateAsset(
						dlFolder.getUserId(), folder, null, null, null);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to update asset for folder ",
								dlFolder.getFolderId(), ": ",
								exception.getMessage()));
					}
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Assets verified for folders");
			}
		}
	}

	private static final String _MS_OFFICE_2010_TEXT_XML_UTF8 =
		"text/xml; charset=\"utf-8\"";

	private static final Log _log = LogFactoryUtil.getLog(
		DLServiceVerifyProcess.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLAppHelperLocalService _dlAppHelperLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(ddm.form.serializer.type=json)")
	private DDMFormSerializer _jsonDDMFormSerializer;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.document.library.service)(&(release.schema.version>=3.0.0)(!(release.schema.version>=4.0.0))))"
	)
	private Release _release;

	@Reference
	private Staging _staging;

}