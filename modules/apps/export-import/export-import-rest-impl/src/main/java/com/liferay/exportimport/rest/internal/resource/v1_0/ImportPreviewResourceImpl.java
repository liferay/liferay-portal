/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.rest.dto.v1_0.ImportPreview;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.Site;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.LayoutUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.ImportPreviewResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;

import jakarta.ws.rs.BadRequestException;

import java.util.HashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Daniel Raposo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/import-preview.properties",
	scope = ServiceScope.PROTOTYPE, service = ImportPreviewResource.class
)
public class ImportPreviewResourceImpl extends BaseImportPreviewResourceImpl {

	@Override
	public ImportPreview postAssetLibraryImportPreview(
			String assetLibraryExternalReferenceCode, String portletId,
			MultipartBody multipartBody)
		throws Exception {

		return _getImportPreview(
			GroupUtil.getAssetLibraryGroup(
				contextCompany.getCompanyId(),
				assetLibraryExternalReferenceCode),
			multipartBody, portletId, null);
	}

	@Override
	public ImportPreview postImportPreview(
			String portletId, MultipartBody multipartBody)
		throws Exception {

		return _getImportPreview(
			GroupUtil.getCompanyGroup(contextCompany.getCompanyId()),
			multipartBody, portletId, null);
	}

	@Override
	public ImportPreview postSiteImportPreview(
			String siteExternalReferenceCode, String portletId,
			String sitePageExternalReferenceCode, MultipartBody multipartBody)
		throws Exception {

		return _getImportPreview(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode,
				portletId),
			multipartBody, portletId, sitePageExternalReferenceCode);
	}

	private ExportImportConfiguration _addExportImportConfiguration(
			long groupId, Layout layout, String portletId)
		throws Exception {

		if (Validator.isBlank(portletId)) {
			return _exportImportConfigurationLocalService.
				addExportImportConfiguration(
					contextUser.getUserId(), groupId, null, null,
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							contextUser.getUserId(), groupId, false, null,
							new HashMap<>(),
							contextAcceptLanguage.getPreferredLocale(),
							contextUser.getTimeZone()),
					new ServiceContext());
		}

		return _exportImportConfigurationLocalService.
			addExportImportConfiguration(
				contextUser.getUserId(), groupId, null, null,
				ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportPortletSettingsMap(
						contextUser.getUserId(), layout.getPlid(), groupId,
						portletId, new HashMap<>(),
						contextAcceptLanguage.getPreferredLocale(),
						contextUser.getTimeZone()),
				new ServiceContext());
	}

	private FileEntry _addTempFileEntry(
			long groupId, MultipartBody multipartBody)
		throws Exception {

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		String folderName = ImportPreviewResource.class.getName();

		String[] tempFileNames = _layoutService.getTempFileNames(
			groupId, folderName);

		for (String tempFileEntryName : tempFileNames) {
			_layoutService.deleteTempFileEntry(
				groupId, folderName, tempFileEntryName);
		}

		return _layoutService.addTempFileEntry(
			groupId, folderName, binaryFile.getFileName(),
			binaryFile.getInputStream(), binaryFile.getContentType());
	}

	private ImportPreview _getImportPreview(
			Group group, MultipartBody multipartBody, String portletId,
			String sitePageExternalReferenceCode)
		throws Exception {

		Layout layout = LayoutUtil.getPortletLayout(
			group, portletId, sitePageExternalReferenceCode);

		Group scopeGroup = GroupUtil.getScopeGroup(group, layout, portletId);

		long groupId = scopeGroup.getGroupId();

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), groupId,
			PermissionUtil.getGroupActionId(portletId));

		FileEntry fileEntry = _addTempFileEntry(groupId, multipartBody);

		_validateImportFile(fileEntry, groupId, layout, portletId);

		ManifestSummary manifestSummary =
			_exportImportHelper.getManifestSummary(
				contextUser.getUserId(), groupId, new HashMap<>(), fileEntry);

		boolean portletScoped = !Validator.isBlank(portletId);

		List<Portlet> portlets = manifestSummary.getDataPortlets();

		if (portletScoped) {
			portlets = ListUtil.fromArray(
				_portletLocalService.getPortletById(
					contextCompany.getCompanyId(), portletId));
		}

		PortletDataHandler[] importPortletDataHandlers =
			PortletDataHandlerUtil.getImportPortletDataHandlers(
				true, scopeGroup, contextAcceptLanguage.getPreferredLocale(),
				manifestSummary, portlets, portletScoped);

		return new ImportPreview() {
			{
				setAdditionCount(
					() -> PortletDataHandlerUtil.getAdditionCount(
						importPortletDataHandlers));
				setAuthor(fileEntry::getUserName);
				setDeletionCount(
					() -> PortletDataHandlerUtil.getDeletionCount(
						importPortletDataHandlers));
				setExportDate(manifestSummary::getExportDate);
				setFileName(fileEntry::getFileName);
				setFileSize(fileEntry::getSize);
				setPortletDataHandlers(() -> importPortletDataHandlers);
				setSites(() -> _getSites(fileEntry));
			}
		};
	}

	private Site[] _getSites(FileEntry fileEntry) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-85946")) {

			return new Site[0];
		}

		return transformToArray(
			_exportImportHelper.getExportImportGroups(fileEntry),
			exportImportGroup -> new Site() {
				{
					setChildSitesCount(exportImportGroup::getChildGroupsCount);
					setDescriptiveName(exportImportGroup::getDescriptiveName);
					setExistsInInstance(
						() -> {
							Group group =
								groupLocalService.
									fetchGroupByExternalReferenceCode(
										exportImportGroup.
											getExternalReferenceCode(),
										contextCompany.getCompanyId());

							return group != null;
						});
					setExternalReferenceCode(
						exportImportGroup::getExternalReferenceCode);
					setPath(exportImportGroup::getPath);
				}
			},
			Site.class);
	}

	private void _validateImportFile(
			FileEntry fileEntry, long groupId, Layout layout, String portletId)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			_addExportImportConfiguration(groupId, layout, portletId);

		try {
			if (Validator.isBlank(portletId)) {
				_exportImportLocalService.validateImportLayoutsFile(
					exportImportConfiguration, fileEntry.getContentStream());
			}
			else {
				_exportImportLocalService.validateImportPortletInfo(
					exportImportConfiguration, fileEntry.getContentStream());
			}
		}
		catch (PortalException portalException) {
			_layoutService.deleteTempFileEntry(
				groupId, ImportPreviewResource.class.getName(),
				fileEntry.getFileName());

			JSONObject jsonObject = _staging.getExceptionMessagesJSONObject(
				contextAcceptLanguage.getPreferredLocale(), portalException,
				exportImportConfiguration);

			throw new BadRequestException(jsonObject.getString("message"));
		}
		finally {
			_exportImportConfigurationLocalService.
				deleteExportImportConfiguration(exportImportConfiguration);
		}
	}

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private Staging _staging;

}