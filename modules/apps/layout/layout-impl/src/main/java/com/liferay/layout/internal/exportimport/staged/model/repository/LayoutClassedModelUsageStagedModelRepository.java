/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.exportimport.staged.model.repository;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.layout.model.LayoutClassedModelUsage",
	service = StagedModelRepository.class
)
public class LayoutClassedModelUsageStagedModelRepository
	implements StagedModelRepository<LayoutClassedModelUsage> {

	@Override
	public LayoutClassedModelUsage addStagedModel(
			PortletDataContext portletDataContext,
			LayoutClassedModelUsage layoutClassedModelUsage)
		throws PortalException {

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			layoutClassedModelUsage);

		if (portletDataContext.isDataStrategyMirror()) {
			serviceContext.setUuid(layoutClassedModelUsage.getUuid());
		}

		return _layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			layoutClassedModelUsage.getGroupId(),
			layoutClassedModelUsage.getClassExternalReferenceCode(),
			layoutClassedModelUsage.getClassNameId(),
			layoutClassedModelUsage.getClassPK(),
			layoutClassedModelUsage.getContainerKey(),
			layoutClassedModelUsage.getContainerType(),
			layoutClassedModelUsage.getPlid(), serviceContext);
	}

	@Override
	public void deleteStagedModel(
			LayoutClassedModelUsage layoutClassedModelUsage)
		throws PortalException {

		_layoutClassedModelUsageLocalService.deleteLayoutClassedModelUsage(
			layoutClassedModelUsage);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		LayoutClassedModelUsage layoutClassedModelUsage =
			fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (layoutClassedModelUsage != null) {
			deleteStagedModel(layoutClassedModelUsage);
		}
	}

	@Override
	public void deleteStagedModels(PortletDataContext portletDataContext)
		throws PortalException {
	}

	@Override
	public LayoutClassedModelUsage fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _layoutClassedModelUsageLocalService.
			fetchLayoutClassedModelUsageByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<LayoutClassedModelUsage> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _layoutClassedModelUsageLocalService.
			getLayoutClassedModelUsagesByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		return _layoutClassedModelUsageLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public LayoutClassedModelUsage saveStagedModel(
			LayoutClassedModelUsage layoutClassedModelUsage)
		throws PortalException {

		return _layoutClassedModelUsageLocalService.
			updateLayoutClassedModelUsage(layoutClassedModelUsage);
	}

	@Override
	public LayoutClassedModelUsage updateStagedModel(
			PortletDataContext portletDataContext,
			LayoutClassedModelUsage layoutClassedModelUsage)
		throws PortalException {

		return _layoutClassedModelUsageLocalService.
			updateLayoutClassedModelUsage(
				layoutClassedModelUsage.getClassNameId(),
				layoutClassedModelUsage.getClassPK(),
				layoutClassedModelUsage.getContainerKey(),
				layoutClassedModelUsage.getContainerType(),
				layoutClassedModelUsage.getLayoutClassedModelUsageId(),
				layoutClassedModelUsage.getPlid());
	}

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

}