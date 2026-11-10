/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.lar.DeletionSystemEventExporter;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.SyncPreview;
import com.liferay.exportimport.rest.internal.util.PortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.SyncPreviewResource;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 * @author Daniel Raposo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/sync-preview.properties",
	scope = ServiceScope.PROTOTYPE, service = SyncPreviewResource.class
)
public class SyncPreviewResourceImpl extends BaseSyncPreviewResourceImpl {

	@Override
	public SyncPreview getSiteTemplateSyncPreview(Long siteTemplateId)
		throws Exception {

		LayoutSetPrototypePermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(), siteTemplateId,
			ActionKeys.UPDATE);

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.getLayoutSetPrototype(
				siteTemplateId);

		Group group = layoutSetPrototype.getGroup();

		PortletDataHandler[] exportPortletDataHandlers =
			PortletDataHandlerUtil.getExportPortletDataHandlers(
				_deletionSystemEventExporter, group,
				contextAcceptLanguage.getPreferredLocale(),
				HashMapBuilder.put(
					ExportImportDateUtil.RANGE,
					new String[] {ExportImportDateUtil.RANGE_ALL}
				).build(),
				0, _portletDataContextFactory, _portletDataHandlerProvider,
				_exportImportHelper.getExportablePortlets(
					group.getCompanyId(), false, group.getGroupId()),
				false, contextUser.getTimeZone());

		return new SyncPreview() {
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
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private PortletDataContextFactory _portletDataContextFactory;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

}