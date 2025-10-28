/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = StagedModelDataHandler.class)
public class SiteNavigationMenuItemStagedModelDataHandler
	extends BaseStagedModelDataHandler<SiteNavigationMenuItem> {

	public static final String[] CLASS_NAMES = {
		SiteNavigationMenuItem.class.getName()
	};

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return siteNavigationMenuItem.getName();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

		Element siteNavigationMenuItemElement =
			portletDataContext.getExportDataElement(siteNavigationMenuItem);

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());

		if (!siteNavigationMenuItemType.exportData(
				portletDataContext, siteNavigationMenuItemElement,
				siteNavigationMenuItem)) {

			Element parentElement = siteNavigationMenuItemElement.getParent();

			parentElement.remove(siteNavigationMenuItemElement);

			return;
		}

		if (siteNavigationMenuItem.getSiteNavigationMenuId() > 0) {
			SiteNavigationMenu siteNavigationMenu =
				_siteNavigationMenuLocalService.getSiteNavigationMenu(
					siteNavigationMenuItem.getSiteNavigationMenuId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, siteNavigationMenuItem, siteNavigationMenu,
				PortletDataContext.REFERENCE_TYPE_PARENT);
		}

		if (siteNavigationMenuItem.getParentSiteNavigationMenuItemId() > 0) {
			SiteNavigationMenuItem parentSiteNavigationMenuItem =
				_siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
					siteNavigationMenuItem.getParentSiteNavigationMenuItemId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, siteNavigationMenuItem,
				parentSiteNavigationMenuItem,
				PortletDataContext.REFERENCE_TYPE_PARENT);
		}

		portletDataContext.addClassedModel(
			siteNavigationMenuItemElement,
			ExportImportPathUtil.getModelPath(siteNavigationMenuItem),
			siteNavigationMenuItem);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long siteNavigationMenuItemId)
		throws Exception {

		SiteNavigationMenuItem existingSiteNavigationMenuItem =
			fetchMissingReference(uuid, groupId);

		if (existingSiteNavigationMenuItem == null) {
			return;
		}

		Map<Long, Long> siteNavigationMenuItemIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				SiteNavigationMenuItem.class);

		siteNavigationMenuItemIds.put(
			siteNavigationMenuItemId,
			existingSiteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

		Map<Long, Long> siteNavigationMenuIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				SiteNavigationMenu.class);

		long siteNavigationMenuId = MapUtil.getLong(
			siteNavigationMenuIds,
			siteNavigationMenuItem.getSiteNavigationMenuId(),
			siteNavigationMenuItem.getSiteNavigationMenuId());

		Map<Long, Long> siteNavigationMenuItemIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				SiteNavigationMenuItem.class);

		long parentSiteNavigationMenuItemId = MapUtil.getLong(
			siteNavigationMenuItemIds,
			siteNavigationMenuItem.getParentSiteNavigationMenuItemId(),
			siteNavigationMenuItem.getParentSiteNavigationMenuItemId());

		SiteNavigationMenuItem importedSiteNavigationMenuItem =
			(SiteNavigationMenuItem)siteNavigationMenuItem.clone();

		importedSiteNavigationMenuItem.setGroupId(
			portletDataContext.getScopeGroupId());
		importedSiteNavigationMenuItem.setSiteNavigationMenuId(
			siteNavigationMenuId);
		importedSiteNavigationMenuItem.setParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId);

		SiteNavigationMenuItem existingSiteNavigationMenuItem =
			_stagedModelRepository.fetchStagedModelByUuidAndGroupId(
				siteNavigationMenuItem.getUuid(),
				portletDataContext.getScopeGroupId());

		if ((existingSiteNavigationMenuItem == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedSiteNavigationMenuItem =
				_stagedModelRepository.addStagedModel(
					portletDataContext, importedSiteNavigationMenuItem);
		}
		else {
			importedSiteNavigationMenuItem.setMvccVersion(
				existingSiteNavigationMenuItem.getMvccVersion());
			importedSiteNavigationMenuItem.setSiteNavigationMenuItemId(
				existingSiteNavigationMenuItem.getSiteNavigationMenuItemId());

			importedSiteNavigationMenuItem =
				_stagedModelRepository.updateStagedModel(
					portletDataContext, importedSiteNavigationMenuItem);
		}

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());

		if (!siteNavigationMenuItemType.importData(
				portletDataContext, siteNavigationMenuItem,
				importedSiteNavigationMenuItem)) {

			_stagedModelRepository.deleteStagedModel(
				importedSiteNavigationMenuItem);

			return;
		}

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			siteNavigationMenuItem);

		importedSiteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
				importedSiteNavigationMenuItem.getUserId(),
				importedSiteNavigationMenuItem.getSiteNavigationMenuItemId(),
				importedSiteNavigationMenuItem.getTypeSettings(),
				serviceContext);

		portletDataContext.importClassedModel(
			siteNavigationMenuItem, importedSiteNavigationMenuItem);
	}

	@Override
	protected StagedModelRepository<SiteNavigationMenuItem>
		getStagedModelRepository() {

		return _stagedModelRepository;
	}

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.site.navigation.model.SiteNavigationMenuItem)",
		unbind = "-"
	)
	private StagedModelRepository<SiteNavigationMenuItem>
		_stagedModelRepository;

}