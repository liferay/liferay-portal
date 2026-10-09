/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.internal.exportimport.data.handler;

import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRel;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.entry.rel.util.comparator.AssetEntryAssetCategoryRelAssetEntryAssetCategoryRelIdComparator;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.xml.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = StagedModelDataHandler.class)
public class FriendlyURLEntryStagedModelDataHandler
	extends BaseStagedModelDataHandler<FriendlyURLEntry> {

	public static final String[] CLASS_NAMES = {
		FriendlyURLEntry.class.getName()
	};

	@Override
	public FriendlyURLEntry fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _stagedModelRepository.fetchStagedModelByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public List<FriendlyURLEntry> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _stagedModelRepository.fetchStagedModelsByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			FriendlyURLEntry friendlyURLEntry)
		throws Exception {

		Element friendlyURLEntryElement =
			portletDataContext.getExportDataElement(friendlyURLEntry);

		_exportAssetCategories(portletDataContext, friendlyURLEntry);

		friendlyURLEntryElement.addAttribute(
			"resource-class-name", friendlyURLEntry.getClassName());

		portletDataContext.addZipEntry(
			ExportImportPathUtil.getModelPath(
				friendlyURLEntry, friendlyURLEntry.getUuid()),
			friendlyURLEntry.getUrlTitleMapAsXML());

		FriendlyURLEntry mainFriendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				friendlyURLEntry.getClassNameId(),
				friendlyURLEntry.getClassPK());

		if (mainFriendlyURLEntry == null) {
			_friendlyURLEntryLocalService.setMainFriendlyURLEntry(
				friendlyURLEntry);
		}

		if (friendlyURLEntry.isMain()) {
			friendlyURLEntryElement.addAttribute(
				"mainEntry", Boolean.TRUE.toString());
		}

		portletDataContext.addClassedModel(
			friendlyURLEntryElement,
			ExportImportPathUtil.getModelPath(friendlyURLEntry),
			friendlyURLEntry);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			FriendlyURLEntry friendlyURLEntry)
		throws Exception {

		Element friendlyURLEntryElement =
			portletDataContext.getImportDataStagedModelElement(
				friendlyURLEntry);

		String className = friendlyURLEntryElement.attributeValue(
			"resource-class-name");

		long classNameId = _classNameLocalService.getClassNameId(className);

		Map<Long, Long> newPrimaryKeysMap =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(className);

		if (!newPrimaryKeysMap.containsKey(friendlyURLEntry.getClassPK())) {
			portletDataContext.removePrimaryKey(
				ExportImportPathUtil.getModelPath(friendlyURLEntry));

			return;
		}

		FriendlyURLEntry existingFriendlyURLEntry =
			fetchStagedModelByUuidAndGroupId(
				friendlyURLEntry.getUuid(),
				portletDataContext.getScopeGroupId());

		FriendlyURLEntry importedFriendlyURLEntry =
			(FriendlyURLEntry)friendlyURLEntry.clone();

		importedFriendlyURLEntry.setGroupId(
			portletDataContext.getScopeGroupId());
		importedFriendlyURLEntry.setCompanyId(
			portletDataContext.getCompanyId());
		importedFriendlyURLEntry.setClassNameId(classNameId);
		importedFriendlyURLEntry.setClassPK(
			MapUtil.getLong(
				newPrimaryKeysMap, friendlyURLEntry.getClassPK(),
				friendlyURLEntry.getClassPK()));

		if ((existingFriendlyURLEntry == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedFriendlyURLEntry = _stagedModelRepository.addStagedModel(
				portletDataContext, importedFriendlyURLEntry);

			boolean mainEntry = GetterUtil.getBoolean(
				friendlyURLEntryElement.attributeValue("mainEntry"));

			if (mainEntry) {
				_friendlyURLEntryLocalService.setMainFriendlyURLEntry(
					importedFriendlyURLEntry);
			}
		}
		else {
			importedFriendlyURLEntry = _stagedModelRepository.updateStagedModel(
				portletDataContext, importedFriendlyURLEntry);

			boolean mainEntry = GetterUtil.getBoolean(
				friendlyURLEntryElement.attributeValue("mainEntry"));

			if (mainEntry) {
				_friendlyURLEntryLocalService.setMainFriendlyURLEntry(
					existingFriendlyURLEntry);
			}
		}

		_importAssetCategories(
			portletDataContext, friendlyURLEntry, importedFriendlyURLEntry);

		portletDataContext.importClassedModel(
			friendlyURLEntry, importedFriendlyURLEntry);
	}

	@Override
	protected StagedModelRepository<FriendlyURLEntry>
		getStagedModelRepository() {

		return _stagedModelRepository;
	}

	private void _exportAssetCategories(
			PortletDataContext portletDataContext,
			FriendlyURLEntry friendlyURLEntry)
		throws Exception {

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			FriendlyURLEntry.class.getName(),
			friendlyURLEntry.getFriendlyURLEntryId());

		if (assetEntry == null) {
			return;
		}

		List<AssetEntryAssetCategoryRel> assetEntryAssetCategoryRels =
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsByAssetEntryId(
					assetEntry.getEntryId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS,
					AssetEntryAssetCategoryRelAssetEntryAssetCategoryRelIdComparator.
						getInstance(true));

		for (AssetEntryAssetCategoryRel assetEntryAssetCategoryRel :
				assetEntryAssetCategoryRels) {

			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchCategory(
					assetEntryAssetCategoryRel.getAssetCategoryId());

			if (assetCategory == null) {
				continue;
			}

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, friendlyURLEntry, assetCategory,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _importAssetCategories(
			PortletDataContext portletDataContext,
			FriendlyURLEntry friendlyURLEntry,
			FriendlyURLEntry importedFriendlyURL)
		throws Exception {

		if (friendlyURLEntry.getClassNameId() == _portal.getClassNameId(
				AssetCategory.class.getName())) {

			return;
		}

		List<Element> assetCategoryElements =
			portletDataContext.getReferenceDataElements(
				friendlyURLEntry, AssetCategory.class);

		if (ListUtil.isEmpty(assetCategoryElements)) {
			return;
		}

		List<Long> assetCategoryIds = new ArrayList<>();

		for (Element assetCategoryElement : assetCategoryElements) {
			String assetCategoryPath = assetCategoryElement.attributeValue(
				"path");

			AssetCategory assetCategory =
				(AssetCategory)portletDataContext.getZipEntryAsObject(
					assetCategoryPath);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, assetCategory);

			Map<Long, Long> assetCategoryNewPrimaryKeys =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					AssetCategory.class);

			assetCategoryIds.add(
				MapUtil.getLong(
					assetCategoryNewPrimaryKeys, assetCategory.getCategoryId(),
					assetCategory.getCategoryId()));
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		_assetEntryLocalService.updateEntry(
			serviceContext.getUserId(), importedFriendlyURL.getGroupId(),
			importedFriendlyURL.getCreateDate(),
			importedFriendlyURL.getModifiedDate(),
			FriendlyURLEntry.class.getName(),
			importedFriendlyURL.getFriendlyURLEntryId(),
			importedFriendlyURL.getUuid(), 0,
			ArrayUtil.toLongArray(assetCategoryIds), new String[0], true, false,
			null, null, null, null, ContentTypes.TEXT_PLAIN, null, null, null,
			null, null, 0, 0, serviceContext.getAssetPriority());
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(model.class.name=com.liferay.friendly.url.model.FriendlyURLEntry)"
	)
	private StagedModelRepository<FriendlyURLEntry> _stagedModelRepository;

}