/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Mendez Gonzalez
 */
@Component(service = StagedModelDataHandler.class)
public class WebsiteStagedModelDataHandler
	extends BaseStagedModelDataHandler<Website> {

	public static final String[] CLASS_NAMES = {Website.class.getName()};

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		Website website = _websiteLocalService.fetchWebsiteByUuidAndCompanyId(
			uuid, group.getCompanyId());

		if (website != null) {
			deleteStagedModel(website);
		}
	}

	@Override
	public void deleteStagedModel(Website website) {
		_websiteLocalService.deleteWebsite(website);
	}

	@Override
	public List<Website> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_websiteLocalService.fetchWebsiteByUuidAndCompanyId(
				uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, Website website)
		throws Exception {

		Element websiteElement = portletDataContext.getExportDataElement(
			website);

		portletDataContext.addClassedModel(
			websiteElement, ExportImportPathUtil.getModelPath(website),
			website);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Website website)
		throws Exception {

		ScopeUtil.validateInstanceScoped(portletDataContext);

		long userId = portletDataContext.getUserId(website.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			website);

		Website existingWebsite =
			_websiteLocalService.fetchWebsiteByUuidAndCompanyId(
				website.getUuid(), portletDataContext.getCompanyId());

		Website importedWebsite = null;

		if (existingWebsite == null) {
			serviceContext.setUuid(website.getUuid());

			importedWebsite = _websiteLocalService.addWebsite(
				website.getExternalReferenceCode(), userId,
				website.getClassName(), website.getClassPK(), website.getUrl(),
				website.getListTypeId(), website.isPrimary(), serviceContext);
		}
		else {
			importedWebsite = _websiteLocalService.updateWebsite(
				website.getExternalReferenceCode(),
				existingWebsite.getWebsiteId(), website.getUrl(),
				website.getListTypeId(), website.isPrimary());
		}

		portletDataContext.importClassedModel(website, importedWebsite);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private WebsiteLocalService _websiteLocalService;

}