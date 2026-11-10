/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.exportimport.data.handler;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.staging.StagingGroupHelper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = StagedModelDataHandler.class)
public class AssetListEntryStagedModelDataHandler
	extends BaseStagedModelDataHandler<AssetListEntry> {

	public static final String[] CLASS_NAMES = {AssetListEntry.class.getName()};

	@Override
	public void deleteStagedModel(AssetListEntry assetListEntry)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(assetListEntry);
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
	public String getDisplayName(AssetListEntry assetListEntry) {
		return assetListEntry.getTitle();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			AssetListEntry assetListEntry)
		throws Exception {

		Element entryElement = portletDataContext.getExportDataElement(
			assetListEntry);

		portletDataContext.addClassedModel(
			entryElement, ExportImportPathUtil.getModelPath(assetListEntry),
			assetListEntry);

		_exportAssetListEntryAssetEntryRels(portletDataContext, assetListEntry);
		_exportAssetListEntryAudienceEntryRels(
			portletDataContext, assetListEntry);
		_exportAssetListEntrySegmentsEntryRels(
			portletDataContext, assetListEntry);
		_exportAssetObjects(portletDataContext, assetListEntry);
		_exportReferenceStagedModel(assetListEntry, portletDataContext);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long assetListEntryId)
		throws Exception {

		AssetListEntry existingAssetListEntry = fetchMissingReference(
			uuid, groupId);

		if (existingAssetListEntry == null) {
			return;
		}

		Map<Long, Long> assetListEntryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetListEntry.class);

		assetListEntryIds.put(
			assetListEntryId, existingAssetListEntry.getAssetListEntryId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			AssetListEntry assetListEntry)
		throws Exception {

		AssetListEntry importedAssetListEntry =
			(AssetListEntry)assetListEntry.clone();

		importedAssetListEntry.setGroupId(portletDataContext.getScopeGroupId());

		AssetListEntry existingAssetListEntry =
			_stagedModelRepository.fetchStagedModelByUuidAndGroupId(
				assetListEntry.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingAssetListEntry == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedAssetListEntry = _stagedModelRepository.addStagedModel(
				portletDataContext, importedAssetListEntry);
		}
		else {
			importedAssetListEntry.setMvccVersion(
				existingAssetListEntry.getMvccVersion());
			importedAssetListEntry.setAssetListEntryId(
				existingAssetListEntry.getAssetListEntryId());

			importedAssetListEntry = _stagedModelRepository.updateStagedModel(
				portletDataContext, importedAssetListEntry);
		}

		portletDataContext.importClassedModel(
			assetListEntry, importedAssetListEntry);

		if (existingAssetListEntry != null) {
			_assetListEntryAssetEntryRelLocalService.
				deleteAssetListEntryAssetEntryRelByAssetListEntryId(
					existingAssetListEntry.getAssetListEntryId());

			_assetListEntryAudienceEntryRelLocalService.
				deleteAssetListEntryAudienceEntryRelByAssetListEntryId(
					existingAssetListEntry.getAssetListEntryId());

			_assetListEntrySegmentsEntryRelLocalService.
				deleteAssetListEntrySegmentsEntryRelByAssetListEntryId(
					existingAssetListEntry.getAssetListEntryId());
		}

		_importAssetObjects(portletDataContext);

		_importAssetEntryListSegmentsEntryRelElements(
			portletDataContext, assetListEntry);

		_importAssetEntryListAssetEntryRelElements(
			portletDataContext, assetListEntry);

		_importAssetListEntryAudienceEntryRelElements(
			portletDataContext, assetListEntry);

		importedAssetListEntry.setModifiedDate(
			assetListEntry.getModifiedDate());
		importedAssetListEntry.setAssetEntrySubtype(
			_getAssetEntrySubtype(assetListEntry, portletDataContext));
		importedAssetListEntry.setAssetEntryType(
			assetListEntry.getAssetEntryType());

		_assetListEntryLocalService.updateAssetListEntry(
			importedAssetListEntry);
	}

	@Override
	protected StagedModelRepository<AssetListEntry> getStagedModelRepository() {
		return _stagedModelRepository;
	}

	@Override
	protected boolean isSkipImportReferenceStagedModels() {
		return true;
	}

	private void _exportAssetListEntryAssetEntryRels(
			PortletDataContext portletDataContext,
			AssetListEntry assetListEntry)
		throws Exception {

		for (AssetListEntryAssetEntryRel assetListEntryAssetEntryRel :
				_assetListEntryAssetEntryRelLocalService.
					getAssetListEntryAssetEntryRels(
						assetListEntry.getAssetListEntryId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS)) {

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, assetListEntry, assetListEntryAssetEntryRel,
				PortletDataContext.REFERENCE_TYPE_CHILD);
		}
	}

	private void _exportAssetListEntryAudienceEntryRels(
			PortletDataContext portletDataContext,
			AssetListEntry assetListEntry)
		throws Exception {

		List<AssetListEntryAudienceEntryRel> assetListEntryAudienceEntryRels =
			_assetListEntryAudienceEntryRelLocalService.
				getAssetListEntryAudienceEntryRels(
					assetListEntry.getAssetListEntryId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		for (AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel :
				assetListEntryAudienceEntryRels) {

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, assetListEntry,
				assetListEntryAudienceEntryRel,
				PortletDataContext.REFERENCE_TYPE_CHILD);
		}
	}

	private void _exportAssetListEntrySegmentsEntryRels(
			PortletDataContext portletDataContext,
			AssetListEntry assetListEntry)
		throws Exception {

		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRels(
					assetListEntry.getAssetListEntryId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		for (AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel :
				assetListEntrySegmentsEntryRels) {

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, assetListEntry,
				assetListEntrySegmentsEntryRel,
				PortletDataContext.REFERENCE_TYPE_CHILD);
		}
	}

	private void _exportAssetObjects(
			PortletDataContext portletDataContext,
			AssetListEntry assetListEntry)
		throws Exception {

		if (assetListEntry.getType() !=
				AssetListEntryTypeConstants.TYPE_MANUAL) {

			return;
		}

		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					assetListEntry.getAssetListEntryId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		for (AssetEntry assetEntry :
				TransformUtil.transform(
					assetListEntryAssetEntryRels,
					assetListEntryAssetEntryRel ->
						_assetEntryLocalService.fetchEntry(
							assetListEntryAssetEntryRel.getAssetEntryId()))) {

			AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

			if ((assetRenderer == null) ||
				!(assetRenderer.getAssetObject() instanceof StagedModel)) {

				continue;
			}

			AssetRendererFactory<?> assetRendererFactory =
				assetRenderer.getAssetRendererFactory();

			if ((assetRendererFactory != null) &&
				ExportImportThreadLocal.isStagingInProcess() &&
				!_stagingGroupHelper.isStagedPortlet(
					assetEntry.getGroupId(),
					assetRendererFactory.getPortletId())) {

				continue;
			}

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, portletDataContext.getPortletId(),
				(StagedModel)assetRenderer.getAssetObject());
		}
	}

	private void _exportReferenceStagedModel(
			AssetListEntry assetListEntry,
			PortletDataContext portletDataContext)
		throws Exception {

		if (Validator.isNull(assetListEntry.getAssetEntrySubtype())) {
			return;
		}

		long classTypeId = GetterUtil.getLong(
			assetListEntry.getAssetEntrySubtype());

		if (Objects.equals(
				assetListEntry.getAssetEntryType(),
				DLFileEntry.class.getName())) {

			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.fetchFileEntryType(classTypeId);

			if (dlFileEntryType != null) {
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, assetListEntry, dlFileEntryType,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
			}
		}
		else if (Objects.equals(
					assetListEntry.getAssetEntryType(),
					"com.liferay.journal.model.JournalArticle")) {

			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchStructure(classTypeId);

			if (ddmStructure != null) {
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, assetListEntry, ddmStructure,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
			}
		}
	}

	private String _getAssetEntrySubtype(
		AssetListEntry assetListEntry, PortletDataContext portletDataContext) {

		if (Validator.isNull(assetListEntry.getAssetEntrySubtype())) {
			return null;
		}

		if (Objects.equals(
				assetListEntry.getAssetEntryType(),
				DLFileEntry.class.getName())) {

			Map<Long, Long> primaryKeys =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					DLFileEntryType.class);

			long oldPrimaryKey = GetterUtil.getLong(
				assetListEntry.getAssetEntrySubtype());

			return String.valueOf(
				MapUtil.getLong(primaryKeys, oldPrimaryKey, oldPrimaryKey));
		}
		else if (Objects.equals(
					assetListEntry.getAssetEntryType(),
					"com.liferay.journal.model.JournalArticle")) {

			Map<Long, Long> primaryKeys =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					"com.liferay.dynamic.data.mapping.model.DDMStructure");

			long oldPrimaryKey = GetterUtil.getLong(
				assetListEntry.getAssetEntrySubtype());

			return String.valueOf(
				MapUtil.getLong(primaryKeys, oldPrimaryKey, oldPrimaryKey));
		}

		return assetListEntry.getAssetEntrySubtype();
	}

	private void _importAssetEntryListAssetEntryRelElements(
			PortletDataContext portletDataContext,
			AssetListEntry assetListEntry)
		throws Exception {

		List<Element> assetEntryListAssetEntryRelElements =
			portletDataContext.getReferenceDataElements(
				assetListEntry, AssetListEntryAssetEntryRel.class,
				PortletDataContext.REFERENCE_TYPE_CHILD);

		for (Element assetEntryListAssetEntryRelElement :
				assetEntryListAssetEntryRelElements) {

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, assetEntryListAssetEntryRelElement);
		}
	}

	private void _importAssetEntryListSegmentsEntryRelElements(
			PortletDataContext portletDataContext,
			AssetListEntry assetListEntry)
		throws Exception {

		List<Element> assetEntryListSegmentsEntryRelElements =
			portletDataContext.getReferenceDataElements(
				assetListEntry, AssetListEntrySegmentsEntryRel.class,
				PortletDataContext.REFERENCE_TYPE_CHILD);

		for (Element assetEntryListSegmentsEntryRelElement :
				assetEntryListSegmentsEntryRelElements) {

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, assetEntryListSegmentsEntryRelElement);
		}
	}

	private void _importAssetListEntryAudienceEntryRelElements(
			PortletDataContext portletDataContext,
			AssetListEntry assetListEntry)
		throws Exception {

		List<Element> assetListEntryAudienceEntryRelElements =
			portletDataContext.getReferenceDataElements(
				assetListEntry, AssetListEntryAudienceEntryRel.class,
				PortletDataContext.REFERENCE_TYPE_CHILD);

		for (Element assetListEntryAudienceEntryRelElement :
				assetListEntryAudienceEntryRelElements) {

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, assetListEntryAudienceEntryRelElement);
		}
	}

	private void _importAssetObjects(PortletDataContext portletDataContext)
		throws Exception {

		Element importDataRootElement =
			portletDataContext.getImportDataRootElement();

		Element referencesElement = importDataRootElement.element("references");

		if (referencesElement == null) {
			return;
		}

		List<Element> referenceElements = referencesElement.elements();

		for (Element referenceElement : referenceElements) {
			String className = referenceElement.attributeValue("class-name");
			long classPK = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, className, Long.valueOf(classPK));
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	@Reference
	private AssetListEntryAudienceEntryRelLocalService
		_assetListEntryAudienceEntryRelLocalService;

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.asset.list.model.AssetListEntry)"
	)
	private StagedModelRepository<AssetListEntry> _stagedModelRepository;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}