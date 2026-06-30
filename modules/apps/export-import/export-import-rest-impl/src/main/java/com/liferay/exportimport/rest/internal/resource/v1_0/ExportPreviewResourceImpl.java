/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.lar.DeletionSystemEventExporter;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.rest.dto.v1_0.ExportPreview;
import com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandler;
import com.liferay.exportimport.rest.internal.util.DateRangeUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PreviewPortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.ExportPreviewResource;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.staging.StagingGroupHelper;

import jakarta.ws.rs.NotFoundException;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Daniel Raposo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/export-preview.properties",
	scope = ServiceScope.PROTOTYPE, service = ExportPreviewResource.class
)
public class ExportPreviewResourceImpl extends BaseExportPreviewResourceImpl {

	@Override
	public ExportPreview getAssetLibraryExportPreview(
			String assetLibraryExternalReferenceCode, Date endDate,
			Date startDate)
		throws Exception {

		Group group = _getAssetLibraryGroup(assetLibraryExternalReferenceCode);

		return _getExportPreview(endDate, group, 0, null, startDate);
	}

	@Override
	public ExportPreview getAssetLibraryPortletExportPreview(
			String assetLibraryExternalReferenceCode, String portletId,
			Date endDate, Long plid, Date startDate)
		throws Exception {

		Group group = _getAssetLibraryGroup(assetLibraryExternalReferenceCode);

		return _getExportPreview(
			endDate, group, GetterUtil.getLong(plid), portletId, startDate);
	}

	@Override
	public ExportPreview getExportPreview(Date endDate, Date startDate)
		throws Exception {

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			contextCompany.getCompanyId());

		if (group == null) {
			throw new NotFoundException();
		}

		return _getExportPreview(endDate, group, 0, null, startDate);
	}

	@Override
	public ExportPreview getSiteExportPreview(
			String siteExternalReferenceCode, Date endDate, Date startDate)
		throws Exception {

		Group group = _getSiteGroup(siteExternalReferenceCode);

		return _getExportPreview(endDate, group, 0, null, startDate);
	}

	@Override
	public ExportPreview getSitePortletExportPreview(
			String siteExternalReferenceCode, String portletId, Date endDate,
			Long plid, Date startDate)
		throws Exception {

		Group group = _getSiteGroup(siteExternalReferenceCode);

		return _getExportPreview(
			endDate, group, GetterUtil.getLong(plid), portletId, startDate);
	}

	private Group _getAssetLibraryGroup(String externalReferenceCode) {
		Group group = groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if ((group == null) || !group.isDepot()) {
			throw new NotFoundException();
		}

		return group;
	}

	private ExportPreview _getExportPreview(
			Date endDate, Group group, long plid, String portletId,
			Date startDate)
		throws Exception {

		long groupId = group.getGroupId();

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), groupId);

		Map<String, List<PreviewPortletDataHandler>>
			previewPortletDataHandlersMap = new LinkedHashMap<>();

		boolean portletScoped = !Validator.isBlank(portletId);

		List<Portlet> portlets = null;

		if (portletScoped) {
			portlets = ListUtil.fromArray(
				_portletLocalService.getPortletById(
					contextCompany.getCompanyId(), portletId));
		}
		else {
			portlets = _exportImportHelper.getExportablePortlets(
				contextCompany.getCompanyId(), false, groupId);
		}

		String range = null;

		DateRange dateRange = DateRangeUtil.toDateRange(startDate, endDate);

		if (dateRange != null) {
			endDate = dateRange.getEndDate();
			range = ExportImportDateUtil.RANGE_DATE_RANGE;
			startDate = dateRange.getStartDate();
		}

		Locale locale = contextAcceptLanguage.getPreferredLocale();

		for (Portlet portlet : portlets) {
			PortletDataHandler portletDataHandler =
				_portletDataHandlerProvider.provide(portlet);

			if (portletDataHandler == null) {
				continue;
			}

			PortletDataContext portletDataContext =
				_portletDataContextFactory.createPreparePortletDataContext(
					contextCompany.getCompanyId(), groupId, range, startDate,
					endDate);

			portletDataHandler.prepareManifestSummary(portletDataContext);

			if (LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS.equals(
					portlet.getPortletId())) {

				portletDataContext.addDeletionSystemEventStagedModelTypes(
					portletDataHandler.
						getDeletionSystemEventStagedModelTypes());

				long publicDeletionSystemEventsCount =
					_deletionSystemEventExporter.getDeletionSystemEventsCount(
						portletDataContext);

				ManifestSummary privateManifestSummary = null;
				long privateDeletionSystemEventsCount = 0;

				if (!portletScoped && group.isPrivateLayoutsEnabled()) {
					PortletDataContext privatePortletDataContext =
						_portletDataContextFactory.
							createPreparePortletDataContext(
								contextCompany.getCompanyId(), groupId, range,
								startDate, endDate);

					privatePortletDataContext.setPrivateLayout(true);

					portletDataHandler.prepareManifestSummary(
						privatePortletDataContext);

					privatePortletDataContext.
						addDeletionSystemEventStagedModelTypes(
							portletDataHandler.
								getDeletionSystemEventStagedModelTypes());

					privateManifestSummary =
						privatePortletDataContext.getManifestSummary();
					privateDeletionSystemEventsCount =
						_deletionSystemEventExporter.
							getDeletionSystemEventsCount(
								privatePortletDataContext);
				}

				PreviewPortletDataHandlerUtil.
					addLayoutSetPreviewPortletDataHandler(
						contextCompany.getCompanyId(), locale, portlet,
						portletDataHandler, previewPortletDataHandlersMap,
						privateDeletionSystemEventsCount,
						privateManifestSummary, publicDeletionSystemEventsCount,
						portletDataContext.getManifestSummary());
			}
			else {
				PreviewPortletDataHandlerUtil.addPreviewPortletDataHandler(
					contextCompany.getCompanyId(), locale,
					portletDataContext.getManifestSummary(), portlet,
					portletDataHandler,
					PortletDataHandler::getExportPortletDataHandlerControls,
					portletScoped, previewPortletDataHandlersMap);

				if (portletScoped) {
					PreviewPortletDataHandlerUtil.
						addConfigurationPreviewPortletDataHandler(
							locale, portlet,
							portletDataHandler.
								getExportConfigurationPortletDataHandlerControls(
									contextCompany.getCompanyId(), groupId,
									portlet, plid, false),
							previewPortletDataHandlersMap);
				}
			}
		}

		return new ExportPreview() {
			{
				setAdditionCount(
					() -> PreviewPortletDataHandlerUtil.getAdditionCount(
						previewPortletDataHandlersMap));
				setDeletionCount(
					() -> PreviewPortletDataHandlerUtil.getDeletionCount(
						previewPortletDataHandlersMap));
				setPreviewPortletDataHandlerSections(
					() ->
						PreviewPortletDataHandlerUtil.
							toPreviewPortletDataHandlerSections(
								locale, previewPortletDataHandlersMap));
			}
		};
	}

	private Group _getSiteGroup(String externalReferenceCode) {
		Group group = groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if ((group == null) || (!group.isCMS() && !group.isSite())) {
			throw new NotFoundException();
		}

		return group;
	}

	@Reference
	private DeletionSystemEventExporter _deletionSystemEventExporter;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private PortletDataContextFactory _portletDataContextFactory;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}