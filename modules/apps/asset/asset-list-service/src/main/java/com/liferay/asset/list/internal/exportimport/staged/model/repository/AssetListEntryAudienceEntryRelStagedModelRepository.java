/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.exportimport.staged.model.repository;

import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryHelper;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "model.class.name=com.liferay.asset.list.model.AssetListEntryAudienceEntryRel",
	service = StagedModelRepository.class
)
public class AssetListEntryAudienceEntryRelStagedModelRepository
	implements StagedModelRepository<AssetListEntryAudienceEntryRel> {

	@Override
	public AssetListEntryAudienceEntryRel addStagedModel(
			PortletDataContext portletDataContext,
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel)
		throws PortalException {

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			assetListEntryAudienceEntryRel);

		if (portletDataContext.isDataStrategyMirror()) {
			serviceContext.setUuid(assetListEntryAudienceEntryRel.getUuid());
		}

		return _assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				assetListEntryAudienceEntryRel.getAssetListEntryId(),
				assetListEntryAudienceEntryRel.getAudienceEntryERC(),
				assetListEntryAudienceEntryRel.getPriority(),
				assetListEntryAudienceEntryRel.getTypeSettings(),
				serviceContext);
	}

	@Override
	public void deleteStagedModel(
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel)
		throws PortalException {

		_assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRel(
				assetListEntryAudienceEntryRel);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (assetListEntryAudienceEntryRel != null) {
			deleteStagedModel(assetListEntryAudienceEntryRel);
		}
	}

	@Override
	public void deleteStagedModels(PortletDataContext portletDataContext)
		throws PortalException {
	}

	@Override
	public AssetListEntryAudienceEntryRel fetchMissingReference(
		String uuid, long groupId) {

		return _stagedModelRepositoryHelper.fetchMissingReference(
			uuid, groupId, this);
	}

	@Override
	public AssetListEntryAudienceEntryRel fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _assetListEntryAudienceEntryRelLocalService.
			fetchAssetListEntryAudienceEntryRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<AssetListEntryAudienceEntryRel>
		fetchStagedModelsByUuidAndCompanyId(String uuid, long companyId) {

		return _assetListEntryAudienceEntryRelLocalService.
			getAssetListEntryAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new StagedModelModifiedDateComparator<>());
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		return _assetListEntryAudienceEntryRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public AssetListEntryAudienceEntryRel getStagedModel(long id)
		throws PortalException {

		return _assetListEntryAudienceEntryRelLocalService.
			getAssetListEntryAudienceEntryRel(id);
	}

	@Override
	public AssetListEntryAudienceEntryRel saveStagedModel(
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel)
		throws PortalException {

		return _assetListEntryAudienceEntryRelLocalService.
			updateAssetListEntryAudienceEntryRel(
				assetListEntryAudienceEntryRel);
	}

	@Override
	public AssetListEntryAudienceEntryRel updateStagedModel(
			PortletDataContext portletDataContext,
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel)
		throws PortalException {

		if (assetListEntryAudienceEntryRel.getTypeSettings() == null) {
			return assetListEntryAudienceEntryRel;
		}

		return _assetListEntryAudienceEntryRelLocalService.
			updateAssetListEntryAudienceEntryRelTypeSettings(
				assetListEntryAudienceEntryRel.getAssetListEntryId(),
				assetListEntryAudienceEntryRel.getAudienceEntryERC(),
				assetListEntryAudienceEntryRel.getTypeSettings());
	}

	@Reference
	private AssetListEntryAudienceEntryRelLocalService
		_assetListEntryAudienceEntryRelLocalService;

	@Reference
	private StagedModelRepositoryHelper _stagedModelRepositoryHelper;

}