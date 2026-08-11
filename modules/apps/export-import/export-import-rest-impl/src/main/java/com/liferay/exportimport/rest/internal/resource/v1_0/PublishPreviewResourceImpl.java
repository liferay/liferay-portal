/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.lar.DeletionSystemEventExporter;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.PublishPreview;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.ParameterMapUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PreviewPortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.PublishPreviewResource;
import com.liferay.portal.kernel.model.Group;

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
	properties = "OSGI-INF/liferay/rest/v1_0/publish-preview.properties",
	scope = ServiceScope.PROTOTYPE, service = PublishPreviewResource.class
)
public class PublishPreviewResourceImpl extends BasePublishPreviewResourceImpl {

	@Override
	public PublishPreview getSitePublishPreview(
			String siteExternalReferenceCode, String dateRangeType,
			Date endDate, Date startDate)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode));

		PermissionUtil.checkPublishPermission(stagingGroup.getGroupId());

		Locale locale = contextAcceptLanguage.getPreferredLocale();

		Map<String, String[]> parameterMap =
			ParameterMapUtil.putDateRangeParameters(
				dateRangeType, startDate, endDate, new LinkedHashMap<>(),
				contextUser);

		Map<String, List<PreviewPortletDataHandler>>
			previewPortletDataHandlersMap =
				PreviewPortletDataHandlerUtil.getPreviewPortletDataHandlersMap(
					_deletionSystemEventExporter, stagingGroup, locale,
					parameterMap, 0, _portletDataContextFactory,
					_portletDataHandlerProvider,
					_exportImportHelper.getExportablePortlets(
						stagingGroup.getCompanyId(), false,
						stagingGroup.getGroupId()),
					false, contextUser.getTimeZone());

		return new PublishPreview() {
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

	@Reference
	private DeletionSystemEventExporter _deletionSystemEventExporter;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private PortletDataContextFactory _portletDataContextFactory;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

}