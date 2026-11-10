/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.exportimport.data.handler;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Víctor Galán
 */
@Component(service = StagedModelDataHandler.class)
public class AssetListEntryAudienceEntryRelStagedModelDataHandler
	extends BaseStagedModelDataHandler<AssetListEntryAudienceEntryRel> {

	public static final String[] CLASS_NAMES = {
		AssetListEntryAudienceEntryRel.class.getName()
	};

	@Override
	public void deleteStagedModel(
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(
			assetListEntryAudienceEntryRel);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(
			uuid, groupId, className, extraData);
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel)
		throws Exception {

		String typeSettings =
			_assetListEntryExportImportContentProcessor.
				replaceExportContentReferences(
					portletDataContext, assetListEntryAudienceEntryRel,
					assetListEntryAudienceEntryRel.getTypeSettings(), false,
					false);

		assetListEntryAudienceEntryRel.setTypeSettings(typeSettings);

		Element entryElement = portletDataContext.getExportDataElement(
			assetListEntryAudienceEntryRel);

		portletDataContext.addClassedModel(
			entryElement,
			ExportImportPathUtil.getModelPath(assetListEntryAudienceEntryRel),
			assetListEntryAudienceEntryRel);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long assetListEntryAudienceEntryRelId)
		throws Exception {

		AssetListEntryAudienceEntryRel existingAssetListEntryAudienceEntryRel =
			fetchMissingReference(uuid, groupId);

		if (existingAssetListEntryAudienceEntryRel == null) {
			return;
		}

		Map<Long, Long> assetListEntryAudienceEntryRelIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetListEntryAudienceEntryRel.class);

		assetListEntryAudienceEntryRelIds.put(
			assetListEntryAudienceEntryRelId,
			existingAssetListEntryAudienceEntryRel.
				getAssetListEntryAudienceEntryRelId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel)
		throws Exception {

		Map<Long, Long> assetListEntryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetListEntry.class);

		long assetListEntryId = MapUtil.getLong(
			assetListEntryIds,
			assetListEntryAudienceEntryRel.getAssetListEntryId(),
			assetListEntryAudienceEntryRel.getAssetListEntryId());

		AssetListEntryAudienceEntryRel importedAssetListEntryAudienceEntryRel =
			(AssetListEntryAudienceEntryRel)
				assetListEntryAudienceEntryRel.clone();

		importedAssetListEntryAudienceEntryRel.setGroupId(
			portletDataContext.getScopeGroupId());
		importedAssetListEntryAudienceEntryRel.setAssetListEntryId(
			assetListEntryId);

		String typeSettings =
			_assetListEntryExportImportContentProcessor.
				replaceImportContentReferences(
					portletDataContext, importedAssetListEntryAudienceEntryRel,
					importedAssetListEntryAudienceEntryRel.getTypeSettings());

		importedAssetListEntryAudienceEntryRel.setTypeSettings(typeSettings);

		AssetListEntryAudienceEntryRel existingAssetListEntryAudienceEntryRel =
			_stagedModelRepository.fetchStagedModelByUuidAndGroupId(
				assetListEntryAudienceEntryRel.getUuid(),
				portletDataContext.getScopeGroupId());

		if ((existingAssetListEntryAudienceEntryRel == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedAssetListEntryAudienceEntryRel =
				_stagedModelRepository.addStagedModel(
					portletDataContext, importedAssetListEntryAudienceEntryRel);
		}
		else {
			importedAssetListEntryAudienceEntryRel.setAssetListEntryId(
				existingAssetListEntryAudienceEntryRel.getAssetListEntryId());

			importedAssetListEntryAudienceEntryRel =
				_stagedModelRepository.updateStagedModel(
					portletDataContext, importedAssetListEntryAudienceEntryRel);
		}

		portletDataContext.importClassedModel(
			assetListEntryAudienceEntryRel,
			importedAssetListEntryAudienceEntryRel);
	}

	@Override
	protected StagedModelRepository<AssetListEntryAudienceEntryRel>
		getStagedModelRepository() {

		return _stagedModelRepository;
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.asset.list.model.AssetListEntry)"
	)
	private volatile ExportImportContentProcessor<String>
		_assetListEntryExportImportContentProcessor;

	@Reference(
		target = "(model.class.name=com.liferay.asset.list.model.AssetListEntryAudienceEntryRel)",
		unbind = "-"
	)
	private StagedModelRepository<AssetListEntryAudienceEntryRel>
		_stagedModelRepository;

}