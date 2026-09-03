/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.util.Map;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = StagedModelDataHandler.class)
public class SiteNavigationMenuStagedModelDataHandler
	extends BaseStagedModelDataHandler<SiteNavigationMenu> {

	public static final String[] CLASS_NAMES = {
		SiteNavigationMenu.class.getName()
	};

	@Override
	public SiteNavigationMenu fetchStagedModelByExternalReferenceCodeAndGroupId(
		String externalReferenceCode, long groupId) {

		return _siteNavigationMenuLocalService.
			fetchSiteNavigationMenuByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public SiteNavigationMenu fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _stagedModelRepository.fetchStagedModelByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(SiteNavigationMenu siteNavigationMenu) {
		return siteNavigationMenu.getName();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			SiteNavigationMenu siteNavigationMenu)
		throws Exception {

		Element siteNavigationMenuElement =
			portletDataContext.getExportDataElement(siteNavigationMenu);

		portletDataContext.addClassedModel(
			siteNavigationMenuElement,
			ExportImportPathUtil.getModelPath(siteNavigationMenu),
			siteNavigationMenu);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long siteNavigationMenuId)
		throws Exception {

		SiteNavigationMenu existingSiteNavigationMenu = fetchMissingReference(
			uuid, groupId);

		if (existingSiteNavigationMenu == null) {
			return;
		}

		Map<Long, Long> siteNavigationMenuIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				SiteNavigationMenu.class);

		siteNavigationMenuIds.put(
			siteNavigationMenuId,
			existingSiteNavigationMenu.getSiteNavigationMenuId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			SiteNavigationMenu siteNavigationMenu)
		throws Exception {

		SiteNavigationMenu importedSiteNavigationMenu =
			(SiteNavigationMenu)siteNavigationMenu.clone();

		importedSiteNavigationMenu.setGroupId(
			portletDataContext.getScopeGroupId());

		SiteNavigationMenu existingSiteNavigationMenu =
			fetchExistingStagedModel(
				siteNavigationMenu, portletDataContext.getScopeGroupId());

		if ((existingSiteNavigationMenu == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedSiteNavigationMenu = _stagedModelRepository.addStagedModel(
				portletDataContext, importedSiteNavigationMenu);
		}
		else {
			importedSiteNavigationMenu.setMvccVersion(
				existingSiteNavigationMenu.getMvccVersion());
			importedSiteNavigationMenu.setSiteNavigationMenuId(
				existingSiteNavigationMenu.getSiteNavigationMenuId());

			importedSiteNavigationMenu =
				_stagedModelRepository.updateStagedModel(
					portletDataContext, importedSiteNavigationMenu);

			importedSiteNavigationMenu.setUuid(siteNavigationMenu.getUuid());

			importedSiteNavigationMenu =
				_siteNavigationMenuLocalService.updateSiteNavigationMenu(
					importedSiteNavigationMenu);
		}

		portletDataContext.importClassedModel(
			siteNavigationMenu, importedSiteNavigationMenu);
	}

	@Override
	protected StagedModelRepository<SiteNavigationMenu>
		getStagedModelRepository() {

		return _stagedModelRepository;
	}

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.site.navigation.model.SiteNavigationMenu)"
	)
	private StagedModelRepository<SiteNavigationMenu> _stagedModelRepository;

}