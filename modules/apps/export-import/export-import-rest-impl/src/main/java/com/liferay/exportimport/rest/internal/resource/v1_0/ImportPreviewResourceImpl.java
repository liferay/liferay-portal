/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportGroup;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.rest.dto.v1_0.AssetLibrary;
import com.liferay.exportimport.rest.dto.v1_0.ImportPreview;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.Site;
import com.liferay.exportimport.rest.internal.util.AssetLibraryUtil;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.ImportTempFileEntryUtil;
import com.liferay.exportimport.rest.internal.util.LayoutUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.ImportPreviewResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;

import jakarta.ws.rs.BadRequestException;

import java.io.File;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Daniel Raposo
 * @author Jaime León
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

	private AssetLibrary[] _getAssetLibraries(
		List<ExportImportGroup> exportImportGroups) {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-41307")) {

			return new AssetLibrary[0];
		}

		return transformToArray(
			ListUtil.filter(exportImportGroups, ExportImportGroup::isDepot),
			exportImportGroup -> new AssetLibrary() {
				{
					setDescriptiveName(exportImportGroup::getDescriptiveName);
					setExistsInInstance(
						() -> _hasMatchingGroup(exportImportGroup));
					setExternalReferenceCode(
						exportImportGroup::getExternalReferenceCode);
					setType(
						() -> AssetLibraryUtil.toAssetLibraryType(
							exportImportGroup.getDepotType()));
				}
			},
			AssetLibrary.class);
	}

	private List<ExportImportGroup> _getExportImportGroups(
			PortletDataContext portletDataContext)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-41307") &&
			!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-85946")) {

			return Collections.emptyList();
		}

		return _exportImportHelper.getExportImportGroups(portletDataContext);
	}

	private ImportPreview _getImportPreview(
			BinaryFile binaryFile, File file, Layout layout, String portletId,
			Group scopeGroup)
		throws Exception {

		long groupId = scopeGroup.getGroupId();

		ImportTempFileEntryUtil.deleteUnreservedTempFileEntries(
			groupId, contextUser.getUserId());

		_validateImportFile(file, groupId, layout, portletId);

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			PortletDataContext portletDataContext =
				_portletDataContextFactory.createImportPortletDataContext(
					contextCompany.getCompanyId(), groupId, new HashMap<>(),
					_exportImportHelper.getUserIdStrategy(
						contextUser.getUserId(),
						UserIdStrategy.CURRENT_USER_ID),
					zipReader);

			ManifestSummary manifestSummary =
				_exportImportHelper.getManifestSummary(portletDataContext);

			boolean portletScoped = !Validator.isBlank(portletId);

			List<Portlet> portlets = manifestSummary.getDataPortlets();

			if (portletScoped) {
				portlets = ListUtil.fromArray(
					_portletLocalService.getPortletById(
						contextCompany.getCompanyId(), portletId));
			}

			PortletDataHandler[] importPortletDataHandlers =
				PortletDataHandlerUtil.getImportPortletDataHandlers(
					true, scopeGroup,
					contextAcceptLanguage.getPreferredLocale(), manifestSummary,
					portlets, portletScoped);

			FileEntry fileEntry = ImportTempFileEntryUtil.addTempFileEntry(
				file, binaryFile.getFileName(), groupId,
				binaryFile.getContentType());

			List<ExportImportGroup> exportImportGroups = _getExportImportGroups(
				portletDataContext);

			return new ImportPreview() {
				{
					setAdditionCount(
						() -> PortletDataHandlerUtil.getAdditionCount(
							importPortletDataHandlers));
					setAssetLibraries(
						() -> _getAssetLibraries(exportImportGroups));
					setAuthor(fileEntry::getUserName);
					setDeletionCount(
						() -> PortletDataHandlerUtil.getDeletionCount(
							importPortletDataHandlers));
					setExportDate(manifestSummary::getExportDate);
					setFileName(
						() -> ImportTempFileEntryUtil.getFileName(fileEntry));
					setFileSize(fileEntry::getSize);
					setPortletDataHandlers(() -> importPortletDataHandlers);
					setSites(() -> _getSites(exportImportGroups));
				}
			};
		}
	}

	private ImportPreview _getImportPreview(
			Group group, MultipartBody multipartBody, String portletId,
			String sitePageExternalReferenceCode)
		throws Exception {

		Layout layout = LayoutUtil.getPortletLayout(
			group, portletId, sitePageExternalReferenceCode);

		Group scopeGroup = GroupUtil.getScopeGroup(group, layout, portletId);

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), scopeGroup.getGroupId(),
			PermissionUtil.getGroupActionId(portletId));

		UploadServletRequest uploadServletRequest =
			_portal.getUploadServletRequest(contextHttpServletRequest);

		File file = uploadServletRequest.getFile("file", true);

		if (file == null) {
			throw new BadRequestException("The request has no \"file\" part");
		}

		return _getImportPreview(
			multipartBody.getBinaryFile("file"), file, layout, portletId,
			scopeGroup);
	}

	private String _getMessage(JSONObject jsonObject) {
		JSONArray messageListItemsJSONArray = jsonObject.getJSONArray(
			"messageListItems");

		if (JSONUtil.isEmpty(messageListItemsJSONArray)) {
			return jsonObject.getString("message");
		}

		StringBundler sb = new StringBundler(
			(messageListItemsJSONArray.length() * 10) + 1);

		sb.append(jsonObject.getString("message"));

		for (int i = 0; i < messageListItemsJSONArray.length(); i++) {
			JSONObject messageListItemJSONObject =
				messageListItemsJSONArray.getJSONObject(i);

			sb.append(StringPool.NEW_LINE);
			sb.append(messageListItemJSONObject.getString("type"));

			String site = messageListItemJSONObject.getString("site");

			if (Validator.isNotNull(site)) {
				sb.append(StringPool.SPACE);
				sb.append(site);
			}

			sb.append(StringPool.COLON);
			sb.append(StringPool.SPACE);
			sb.append(messageListItemJSONObject.getString("name"));

			String info = messageListItemJSONObject.getString("info");

			if (Validator.isNull(info)) {
				continue;
			}

			sb.append(StringPool.SPACE);
			sb.append(StringPool.OPEN_PARENTHESIS);
			sb.append(info);
			sb.append(StringPool.CLOSE_PARENTHESIS);
		}

		return sb.toString();
	}

	private Site[] _getSites(List<ExportImportGroup> exportImportGroups) {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-85946")) {

			return new Site[0];
		}

		return transformToArray(
			ListUtil.filter(
				exportImportGroups,
				exportImportGroup -> !exportImportGroup.isDepot()),
			exportImportGroup -> new Site() {
				{
					setChildSitesCount(exportImportGroup::getChildGroupsCount);
					setDescriptiveName(exportImportGroup::getDescriptiveName);
					setExistsInInstance(
						() -> _hasMatchingGroup(exportImportGroup));
					setExternalReferenceCode(
						exportImportGroup::getExternalReferenceCode);
					setPath(exportImportGroup::getPath);
				}
			},
			Site.class);
	}

	private boolean _hasMatchingGroup(ExportImportGroup exportImportGroup) {
		Group group = groupLocalService.fetchGroupByExternalReferenceCode(
			exportImportGroup.getExternalReferenceCode(),
			contextCompany.getCompanyId());

		if ((group != null) &&
			_exportImportHelper.isGroupMatching(exportImportGroup, group)) {

			return true;
		}

		return false;
	}

	private void _validateImportFile(
			File file, long groupId, Layout layout, String portletId)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			_addExportImportConfiguration(groupId, layout, portletId);

		try {
			if (Validator.isBlank(portletId)) {
				_exportImportLocalService.validateImportLayoutsFile(
					exportImportConfiguration, file);
			}
			else {
				_exportImportLocalService.validateImportPortletInfo(
					exportImportConfiguration, file);
			}
		}
		catch (PortalException portalException) {
			JSONObject jsonObject = _staging.getExceptionMessagesJSONObject(
				contextAcceptLanguage.getPreferredLocale(), portalException,
				exportImportConfiguration);

			throw new BadRequestException(_getMessage(jsonObject));
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
	private Portal _portal;

	@Reference
	private PortletDataContextFactory _portletDataContextFactory;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private Staging _staging;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

}