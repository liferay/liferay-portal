/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.rest.dto.v1_0.SyncProcessRequest;
import com.liferay.exportimport.rest.internal.util.LayoutUtil;
import com.liferay.exportimport.rest.internal.util.ParameterMapUtil;
import com.liferay.exportimport.rest.internal.util.PortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.SyncProcessResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.sites.kernel.util.Sites;

import jakarta.ws.rs.BadRequestException;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 * @author Jaime Leon
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/sync-process.properties",
	scope = ServiceScope.PROTOTYPE, service = SyncProcessResource.class
)
@CTAware
public class SyncProcessResourceImpl extends BaseSyncProcessResourceImpl {

	@Override
	public void postSiteTemplateSyncProcess(
			Long siteTemplateId, SyncProcessRequest syncProcessRequest)
		throws Exception {

		LayoutSetPrototypePermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(), siteTemplateId,
			ActionKeys.UPDATE);

		if (!ArrayUtil.exists(
				syncProcessRequest.getPortletDataHandlers(),
				portletDataHandler ->
					(portletDataHandler != null) &&
					GetterUtil.getBoolean(
						portletDataHandler.getSelected(), true))) {

			throw new BadRequestException(
				"At least one portlet data handler must be selected");
		}

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.getLayoutSetPrototype(
				siteTemplateId);

		Group group = layoutSetPrototype.getGroup();

		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			syncProcessRequest,
			PortletDataHandlerUtil.getExportPortletDataHandlers(
				group, contextAcceptLanguage.getPreferredLocale(), 0,
				_portletDataHandlerProvider,
				_exportImportHelper.getExportablePortlets(
					contextCompany.getCompanyId(), false, group.getGroupId()),
				false));

		try {
			_sites.mergeLayoutSetPrototypeLayouts(
				layoutSetPrototype, contextUser.getUserId(),
				LayoutUtil.getLayoutIds(
					_layoutLocalService.getLayouts(group.getGroupId(), true),
					parameterMap),
				parameterMap);
		}
		catch (IllegalStateException illegalStateException) {
			throw new BadRequestException(
				illegalStateException.getMessage(), illegalStateException);
		}
	}

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Reference
	private Sites _sites;

}