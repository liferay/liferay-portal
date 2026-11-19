/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.lar.DeletionSystemEventExporter;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.PublishPreview;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.LayoutUtil;
import com.liferay.exportimport.rest.internal.util.ParameterMapUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.PublishPreviewResource;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Daniel Raposo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/publish-preview.properties",
	scope = ServiceScope.PROTOTYPE, service = PublishPreviewResource.class
)
public class PublishPreviewResourceImpl extends BasePublishPreviewResourceImpl {

	@Override
	public PublishPreview getAssetLibraryPublishPreview(
			String assetLibraryExternalReferenceCode, String dateFilterType,
			Date endDate, Integer hours, String portletId, Date startDate)
		throws Exception {

		return _getPublishPreview(
			dateFilterType, endDate,
			GroupUtil.getAssetLibraryGroup(
				contextCompany.getCompanyId(),
				assetLibraryExternalReferenceCode),
			hours, portletId, null, startDate);
	}

	@Override
	public PublishPreview getSitePublishPreview(
			String siteExternalReferenceCode, String dateFilterType,
			Date endDate, Integer hours, String portletId,
			String sitePageExternalReferenceCode, Date startDate)
		throws Exception {

		return _getPublishPreview(
			dateFilterType, endDate,
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			hours, portletId, sitePageExternalReferenceCode, startDate);
	}

	private PublishPreview _getPublishPreview(
			String dateFilterType, Date endDate, Group group, Integer hours,
			String portletId, String sitePageExternalReferenceCode,
			Date startDate)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(group);

		Layout layout = LayoutUtil.getPortletLayout(
			stagingGroup, portletId, sitePageExternalReferenceCode);

		Group scopeGroup = GroupUtil.getScopeGroup(
			stagingGroup, layout, portletId);

		PermissionUtil.checkPublishPermission(
			scopeGroup.getGroupId(),
			PermissionUtil.getPublishGroupActionId(portletId));

		Map<String, String[]> parameterMap =
			ParameterMapUtil.putDateRangeParameters(
				dateFilterType, startDate, endDate, hours,
				new LinkedHashMap<>(), contextUser);

		List<Portlet> portlets = null;

		boolean portletScoped = !Validator.isBlank(portletId);

		if (portletScoped) {
			portlets = ListUtil.fromArray(
				_portletLocalService.getPortletById(
					contextCompany.getCompanyId(), portletId));
		}
		else {
			portlets = _exportImportHelper.getExportablePortlets(
				contextCompany.getCompanyId(), false, scopeGroup.getGroupId());
		}

		PortletDataHandler[] exportPortletDataHandlers =
			PortletDataHandlerUtil.getExportPortletDataHandlers(
				_deletionSystemEventExporter, scopeGroup,
				contextAcceptLanguage.getPreferredLocale(), parameterMap,
				(layout == null) ? 0 : layout.getPlid(),
				_portletDataContextFactory, _portletDataHandlerProvider,
				portlets, portletScoped, contextUser.getTimeZone());

		return new PublishPreview() {
			{
				setAdditionCount(
					() -> PortletDataHandlerUtil.getAdditionCount(
						exportPortletDataHandlers));
				setDeletionCount(
					() -> PortletDataHandlerUtil.getDeletionCount(
						exportPortletDataHandlers));
				setPortletDataHandlers(() -> exportPortletDataHandlers);
			}
		};
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

}