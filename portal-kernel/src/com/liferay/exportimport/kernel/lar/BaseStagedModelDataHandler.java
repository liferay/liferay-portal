/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManagerUtil;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.comment.DiscussionStagingHandler;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LocalizedModel;
import com.liferay.portal.kernel.model.ResourcedModel;
import com.liferay.portal.kernel.model.StagedGroupedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TransientValue;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Máté Thurzó
 * @author Daniel Kocsis
 * @author Zsolt Berentey
 */
public abstract class BaseStagedModelDataHandler<T extends StagedModel>
	implements StagedModelDataHandler<T> {

	@Override
	public abstract void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException;

	@Override
	public abstract void deleteStagedModel(T stagedModel)
		throws PortalException;

	@Override
	public void exportStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		if (!isEnabled(_getCompanyId(stagedModel))) {
			return;
		}

		validateExport(portletDataContext, stagedModel);

		String path = ExportImportPathUtil.getModelPath(stagedModel);

		if (portletDataContext.isPathExportedInScope(path)) {
			return;
		}

		try {
			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.
					EVENT_STAGED_MODEL_EXPORT_STARTED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				PortletDataContextFactoryUtil.clonePortletDataContext(
					portletDataContext),
				new TransientValue<T>(stagedModel));

			ManifestSummary manifestSummary =
				portletDataContext.getManifestSummary();

			PortletDataHandlerStatusMessageSenderUtil.sendStatusMessage(
				"stagedModel", stagedModel, manifestSummary);

			doExportStagedModel(portletDataContext, (T)stagedModel.clone());

			exportAssetCategories(portletDataContext, stagedModel);
			exportAssetTags(portletDataContext, stagedModel);
			exportComments(portletDataContext, stagedModel);
			exportRatings(portletDataContext, stagedModel);

			if (countStagedModel(portletDataContext, stagedModel)) {
				manifestSummary.incrementModelAdditionCount(
					stagedModel.getStagedModelType());
			}

			portletDataContext.cleanUpMissingReferences(stagedModel);

			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.
					EVENT_STAGED_MODEL_EXPORT_SUCCEEDED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				PortletDataContextFactoryUtil.clonePortletDataContext(
					portletDataContext),
				new TransientValue<T>(stagedModel));
		}
		catch (PortletDataException portletDataException) {
			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_STAGED_MODEL_EXPORT_FAILED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				PortletDataContextFactoryUtil.clonePortletDataContext(
					portletDataContext),
				new TransientValue<T>(stagedModel), portletDataException);

			throw portletDataException;
		}
		catch (Throwable throwable) {
			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_STAGED_MODEL_EXPORT_FAILED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				PortletDataContextFactoryUtil.clonePortletDataContext(
					portletDataContext),
				new TransientValue<T>(stagedModel), throwable);

			if ((throwable instanceof SystemException) &&
				(throwable.getCause() instanceof PortletDataException)) {

				throw (PortletDataException)throwable.getCause();
			}

			PortletDataException portletDataException =
				new PortletDataException(throwable.getMessage(), throwable);

			portletDataException.setStagedModelDisplayName(
				getDisplayName(stagedModel));
			portletDataException.setStagedModelClassName(
				stagedModel.getModelClassName());
			portletDataException.setStagedModelClassPK(
				GetterUtil.getString(stagedModel.getPrimaryKeyObj()));

			if (throwable instanceof NoSuchModelException) {
				portletDataException.setType(
					PortletDataException.MISSING_DEPENDENCY);
			}
			else {
				portletDataException.setType(
					PortletDataException.EXPORT_STAGED_MODEL);
			}

			throw portletDataException;
		}
	}

	@Override
	public T fetchMissingReference(String uuid, long groupId) {

		// Try to fetch the existing staged model from the importing group

		T existingStagedModel = fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if ((existingStagedModel != null) &&
			!isStagedModelInTrash(existingStagedModel)) {

			return existingStagedModel;
		}

		try {

			// Try to fetch the existing staged model from the parent sites

			Group originalGroup = GroupLocalServiceUtil.getGroup(groupId);

			Group group = originalGroup.getParentGroup();

			while (group != null) {
				existingStagedModel = fetchStagedModelByUuidAndGroupId(
					uuid, group.getGroupId());

				if ((existingStagedModel != null) &&
					!isStagedModelInTrash(existingStagedModel)) {

					return existingStagedModel;
				}

				group = group.getParentGroup();
			}

			List<T> existingStagedModels = fetchStagedModelsByUuidAndCompanyId(
				uuid, originalGroup.getCompanyId());

			for (T stagedModel : existingStagedModels) {
				try {
					if (stagedModel instanceof StagedGroupedModel) {
						StagedGroupedModel stagedGroupedModel =
							(StagedGroupedModel)stagedModel;

						group = GroupLocalServiceUtil.getGroup(
							stagedGroupedModel.getGroupId());

						if (!group.isStagingGroup() &&
							!isStagedModelInTrash(stagedModel)) {

							return stagedModel;
						}
					}
					else if (!isStagedModelInTrash(stagedModel)) {
						return stagedModel;
					}
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to fetch missing reference staged model from " +
						"group " + groupId);
			}
		}

		return null;
	}

	@Override
	public T fetchStagedModelByUuidAndGroupId(String uuid, long groupId) {
		return null;
	}

	@Override
	public abstract List<T> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId);

	@Override
	public abstract String[] getClassNames();

	@Override
	public String getDisplayName(T stagedModel) {
		return stagedModel.getUuid();
	}

	@Override
	public int[] getExportableStatuses() {
		return new int[] {WorkflowConstants.STATUS_APPROVED};
	}

	@Override
	public Map<String, String> getReferenceAttributes(
		PortletDataContext portletDataContext, T stagedModel) {

		return new HashMap<>();
	}

	@Override
	public void importMissingReference(
			PortletDataContext portletDataContext, Element referenceElement)
		throws PortletDataException {

		if (!isEnabled(_getCompanyId(portletDataContext))) {
			return;
		}

		try {
			doImportMissingReference(portletDataContext, referenceElement);
		}
		catch (Exception exception) {
			if (!StringUtil.equalsIgnoreCase(
					referenceElement.attributeValue("type"),
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY_DISPOSABLE)) {

				throw exception;
			}
		}
	}

	@Override
	public void importMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long classPK)
		throws PortletDataException {

		if (!isEnabled(_getCompanyId(portletDataContext))) {
			return;
		}

		try {
			doImportMissingReference(
				portletDataContext, uuid, groupId, classPK);
		}
		catch (PortletDataException portletDataException) {
			throw portletDataException;
		}
		catch (Exception exception) {
			throw new PortletDataException(exception);
		}
	}

	@Override
	public void importStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		if (!isEnabled(stagedModel.getCompanyId())) {
			return;
		}

		String path = ExportImportPathUtil.getModelPath(stagedModel);

		if (portletDataContext.isPathProcessed(path)) {
			return;
		}

		if (stagedModel instanceof CTModel) {
			CTModel<?> ctModel = (CTModel)stagedModel;

			if ((ctModel.getCtCollectionId() !=
					CTCollectionThreadLocal.CT_COLLECTION_ID_PRODUCTION) &&
				(ctModel.getCtCollectionId() !=
					CTCollectionThreadLocal.getCTCollectionId())) {

				return;
			}
		}

		try {
			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.
					EVENT_STAGED_MODEL_IMPORT_STARTED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				PortletDataContextFactoryUtil.clonePortletDataContext(
					portletDataContext),
				new TransientValue<T>(stagedModel));

			ManifestSummary manifestSummary =
				portletDataContext.getManifestSummary();

			PortletDataHandlerStatusMessageSenderUtil.sendStatusMessage(
				"stagedModel", stagedModel, manifestSummary);

			if (stagedModel instanceof AuditedModel) {
				Element element =
					portletDataContext.getImportDataStagedModelElement(
						stagedModel);

				String userUuid = element.attributeValue("user-uuid");

				if (Validator.isNotNull(userUuid)) {
					AuditedModel auditedModel = (AuditedModel)stagedModel;

					auditedModel.setUserId(
						portletDataContext.getUserId(userUuid));
				}
			}

			if (stagedModel instanceof LocalizedModel) {
				LocalizedModel localizedModel = (LocalizedModel)stagedModel;

				localizedModel.prepareLocalizedFieldsForImport();
			}

			restoreStagedModel(portletDataContext, stagedModel);

			importAssetCategories(portletDataContext, stagedModel);
			importAssetTags(portletDataContext, stagedModel);

			importReferenceStagedModels(portletDataContext, stagedModel);

			doImportStagedModel(portletDataContext, stagedModel);

			importComments(portletDataContext, stagedModel);
			importRatings(portletDataContext, stagedModel);

			manifestSummary.incrementModelAdditionCount(
				stagedModel.getStagedModelType());

			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.
					EVENT_STAGED_MODEL_IMPORT_SUCCEEDED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				PortletDataContextFactoryUtil.clonePortletDataContext(
					portletDataContext),
				new TransientValue<T>(stagedModel));
		}
		catch (PortletDataException portletDataException) {
			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_STAGED_MODEL_IMPORT_FAILED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				PortletDataContextFactoryUtil.clonePortletDataContext(
					portletDataContext),
				new TransientValue<T>(stagedModel), portletDataException);

			throw portletDataException;
		}
		catch (Throwable throwable) {
			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_STAGED_MODEL_IMPORT_FAILED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				PortletDataContextFactoryUtil.clonePortletDataContext(
					portletDataContext),
				new TransientValue<T>(stagedModel), throwable);

			if ((throwable instanceof SystemException) &&
				(throwable.getCause() instanceof PortletDataException)) {

				throw (PortletDataException)throwable.getCause();
			}

			PortletDataException portletDataException =
				new PortletDataException(throwable.getMessage(), throwable);

			portletDataException.setStagedModelDisplayName(
				getDisplayName(stagedModel));
			portletDataException.setStagedModelClassName(
				stagedModel.getModelClassName());
			portletDataException.setStagedModelClassPK(
				GetterUtil.getString(stagedModel.getPrimaryKeyObj()));

			if (throwable instanceof NoSuchModelException) {
				portletDataException.setType(
					PortletDataException.MISSING_DEPENDENCY);
			}
			else {
				portletDataException.setType(
					PortletDataException.IMPORT_STAGED_MODEL);
			}

			throw portletDataException;
		}
	}

	@Override
	public void restoreStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		if (!isEnabled(_getCompanyId(stagedModel))) {
			return;
		}

		try {
			if (stagedModel instanceof TrashedModel) {
				doRestoreStagedModel(portletDataContext, stagedModel);
			}
		}
		catch (PortletDataException portletDataException) {
			throw portletDataException;
		}
		catch (Exception exception) {
			PortletDataException portletDataException =
				new PortletDataException(exception);

			portletDataException.setStagedModelDisplayName(
				getDisplayName(stagedModel));
			portletDataException.setStagedModelClassName(
				stagedModel.getModelClassName());
			portletDataException.setStagedModelClassPK(
				GetterUtil.getString(stagedModel.getPrimaryKeyObj()));

			throw portletDataException;
		}
	}

	@Override
	public boolean validateReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		validateMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		groupId = MapUtil.getLong(groupIds, groupId);

		try {
			return validateMissingReference(uuid, groupId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	protected boolean countStagedModel(
		PortletDataContext portletDataContext, T stagedModel) {

		return !portletDataContext.isStagedModelCounted(stagedModel);
	}

	protected abstract void doExportStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws Exception;

	protected void doImportMissingReference(
			PortletDataContext portletDataContext, Element referenceElement)
		throws PortletDataException {

		importMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		groupId = MapUtil.getLong(groupIds, groupId);

		long classPK = GetterUtil.getLong(
			referenceElement.attributeValue("class-pk"));

		importMissingReference(portletDataContext, uuid, groupId, classPK);
	}

	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long classPK)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	protected abstract void doImportStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws Exception;

	protected void doRestoreStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	protected void exportAssetCategories(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		List<AssetCategory> assetCategories =
			AssetCategoryLocalServiceUtil.getCategories(
				ExportImportClassedModelUtil.getClassNameId(stagedModel),
				ExportImportClassedModelUtil.getClassPK(stagedModel));

		for (AssetCategory assetCategory : assetCategories) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, stagedModel, assetCategory,
				PortletDataContext.REFERENCE_TYPE_WEAK);
		}
	}

	protected void exportAssetTags(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(
			ExportImportClassedModelUtil.getClassNameId(stagedModel),
			ExportImportClassedModelUtil.getClassPK(stagedModel));

		for (AssetTag assetTag : assetTags) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, stagedModel, assetTag,
				PortletDataContext.REFERENCE_TYPE_WEAK);
		}
	}

	protected void exportComments(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA_ALL) &&
			!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.COMMENTS)) {

			return;
		}

		DiscussionStagingHandler discussionStagingHandler =
			CommentManagerUtil.getDiscussionStagingHandler();

		if (discussionStagingHandler != null) {
			discussionStagingHandler.exportReferenceDiscussions(
				portletDataContext, stagedModel);
		}
	}

	protected void exportRatings(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA_ALL) &&
			!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.RATINGS)) {

			return;
		}

		List<RatingsEntry> ratingsEntries =
			RatingsEntryLocalServiceUtil.getEntries(
				ExportImportClassedModelUtil.getClassName(stagedModel),
				ExportImportClassedModelUtil.getClassPK(stagedModel));

		if (ratingsEntries.isEmpty()) {
			return;
		}

		for (RatingsEntry ratingsEntry : ratingsEntries) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, stagedModel, ratingsEntry,
				PortletDataContext.REFERENCE_TYPE_WEAK);
		}
	}

	protected int getProcessFlag() {

		// Ordered by precedence

		if (ExportImportThreadLocal.isLayoutStagingInProcess()) {
			return ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_STAGING_IN_PROCESS;
		}
		else if (ExportImportThreadLocal.isPortletStagingInProcess()) {
			return ExportImportLifecycleConstants.
				PROCESS_FLAG_PORTLET_STAGING_IN_PROCESS;
		}
		else if (ExportImportThreadLocal.isLayoutExportInProcess()) {
			return ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_EXPORT_IN_PROCESS;
		}
		else if (ExportImportThreadLocal.isLayoutImportInProcess()) {
			return ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_IMPORT_IN_PROCESS;
		}
		else if (ExportImportThreadLocal.isPortletExportInProcess()) {
			return ExportImportLifecycleConstants.
				PROCESS_FLAG_PORTLET_EXPORT_IN_PROCESS;
		}
		else if (ExportImportThreadLocal.isPortletImportInProcess()) {
			return ExportImportLifecycleConstants.
				PROCESS_FLAG_PORTLET_IMPORT_IN_PROCESS;
		}

		return 0;
	}

	protected String[] getSkipImportReferenceStagedModelNames() {
		return null;
	}

	protected void importAssetCategories(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		List<Element> referenceElements =
			portletDataContext.getReferenceElements(
				stagedModel, AssetCategory.class);

		if (referenceElements.isEmpty()) {
			return;
		}

		List<Long> assetCategoryIds = new ArrayList<>(referenceElements.size());

		for (Element referenceElement : referenceElements) {
			Long classPK = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, stagedModel, AssetCategory.class, classPK);

			assetCategoryIds.add(classPK);
		}

		Map<Long, Long> assetCategoryIdsMap =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetCategory.class);

		long[] importedAssetCategoryIds = new long[assetCategoryIds.size()];

		for (int i = 0; i < assetCategoryIds.size(); i++) {
			long categoryId = assetCategoryIds.get(i);

			importedAssetCategoryIds[i] = MapUtil.getLong(
				assetCategoryIdsMap, categoryId, categoryId);
		}

		portletDataContext.addAssetCategories(
			ExportImportClassedModelUtil.getClassName(stagedModel),
			ExportImportClassedModelUtil.getClassPK(stagedModel),
			importedAssetCategoryIds);
	}

	protected void importAssetTags(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		List<Element> referenceElements =
			portletDataContext.getReferenceElements(
				stagedModel, AssetTag.class);

		List<Long> assetTagIds = new ArrayList<>(referenceElements.size());

		for (Element referenceElement : referenceElements) {
			Long classPK = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, stagedModel, AssetTag.class, classPK);

			assetTagIds.add(classPK);
		}

		Map<Long, Long> assetTagIdsMap =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetTag.class);

		Set<String> assetTagNames = new HashSet<>();

		for (long assetTagId : assetTagIds) {
			long importedStagedAssetTagId = MapUtil.getLong(
				assetTagIdsMap, assetTagId, assetTagId);

			AssetTag assetTag = AssetTagLocalServiceUtil.fetchAssetTag(
				importedStagedAssetTagId);

			if (assetTag != null) {
				assetTagNames.add(assetTag.getName());
			}
		}

		if (assetTagNames.isEmpty()) {
			return;
		}

		portletDataContext.addAssetTags(
			ExportImportClassedModelUtil.getClassName(stagedModel),
			ExportImportClassedModelUtil.getClassPK(stagedModel),
			assetTagNames.toArray(new String[0]));
	}

	protected void importComments(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortalException {

		if (stagedModel instanceof ResourcedModel) {
			ResourcedModel resourcedModel = (ResourcedModel)stagedModel;

			if (!resourcedModel.isResourceMain()) {
				return;
			}
		}

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA_ALL) &&
			!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.COMMENTS)) {

			return;
		}

		DiscussionStagingHandler discussionStagingDataHandler =
			CommentManagerUtil.getDiscussionStagingHandler();

		if (discussionStagingDataHandler != null) {
			discussionStagingDataHandler.importReferenceDiscussions(
				portletDataContext, stagedModel);
		}
	}

	protected void importMissingGroupReference(
			PortletDataContext portletDataContext, Element referenceElement)
		throws PortletDataException {

		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				"com.liferay.site.model.adapter.StagedGroup");

		stagedModelDataHandler.importMissingReference(
			portletDataContext, referenceElement);
	}

	protected void importRatings(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortalException {

		if (stagedModel instanceof ResourcedModel) {
			ResourcedModel resourcedModel = (ResourcedModel)stagedModel;

			if (!resourcedModel.isResourceMain()) {
				return;
			}
		}

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA_ALL) &&
			!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.RATINGS)) {

			return;
		}

		StagedModelDataHandlerUtil.importReferenceStagedModels(
			portletDataContext, stagedModel, RatingsEntry.class);
	}

	protected void importReferenceStagedModels(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		if (isSkipImportReferenceStagedModels()) {
			return;
		}

		Element stagedModelElement =
			portletDataContext.getImportDataStagedModelElement(stagedModel);

		Element referencesElement = stagedModelElement.element("references");

		if (referencesElement == null) {
			return;
		}

		DiscussionStagingHandler discussionStagingHandler =
			CommentManagerUtil.getDiscussionStagingHandler();

		String stagedModelClassName = null;

		if (discussionStagingHandler != null) {
			stagedModelClassName = discussionStagingHandler.getClassName();
		}

		List<Element> referenceElements = referencesElement.elements();

		for (Element referenceElement : referenceElements) {
			String className = referenceElement.attributeValue("class-name");

			if (className.equals(AssetCategory.class.getName()) ||
				className.equals(RatingsEntry.class.getName()) ||
				className.equals(stagedModelClassName) ||
				ArrayUtil.contains(
					getSkipImportReferenceStagedModelNames(), className,
					false)) {

				continue;
			}

			Long classPK = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, stagedModel, className, classPK);
		}
	}

	protected boolean isSkipImportReferenceStagedModels() {
		return false;
	}

	protected boolean isStagedModelInTrash(T stagedModel) {
		if (!(stagedModel instanceof TrashedModel)) {
			return false;
		}

		TrashedModel trashedModel = (TrashedModel)stagedModel;

		return trashedModel.isInTrash();
	}

	protected void validateExport(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		if (stagedModel instanceof TrashedModel) {
			TrashedModel trashedModel = (TrashedModel)stagedModel;

			if (trashedModel.isInTrash()) {
				PortletDataException portletDataException =
					new PortletDataException(
						PortletDataException.STATUS_IN_TRASH);

				portletDataException.setStagedModelDisplayName(
					getDisplayName(stagedModel));
				portletDataException.setStagedModelClassName(
					stagedModel.getModelClassName());
				portletDataException.setStagedModelClassPK(
					GetterUtil.getString(stagedModel.getPrimaryKeyObj()));

				throw portletDataException;
			}
		}

		if (!portletDataContext.isInitialPublication() &&
			(stagedModel instanceof WorkflowedModel)) {

			WorkflowedModel workflowedModel = (WorkflowedModel)stagedModel;

			if (!ArrayUtil.contains(
					getExportableStatuses(), workflowedModel.getStatus())) {

				PortletDataException portletDataException =
					new PortletDataException(
						PortletDataException.STATUS_UNAVAILABLE);

				portletDataException.setStagedModelDisplayName(
					getDisplayName(stagedModel));
				portletDataException.setStagedModelClassName(
					stagedModel.getModelClassName());
				portletDataException.setStagedModelClassPK(
					GetterUtil.getString(stagedModel.getPrimaryKeyObj()));

				throw portletDataException;
			}
		}
	}

	protected boolean validateMissingGroupReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				"com.liferay.site.model.adapter.StagedGroup");

		return stagedModelDataHandler.validateReference(
			portletDataContext, referenceElement);
	}

	protected boolean validateMissingReference(String uuid, long groupId) {
		T existingStagedModel = fetchMissingReference(uuid, groupId);

		if (existingStagedModel == null) {
			return false;
		}

		return true;
	}

	private long _getCompanyId(PortletDataContext portletDataContext) {
		if (portletDataContext != null) {
			return portletDataContext.getCompanyId();
		}

		return CompanyThreadLocal.getCompanyId();
	}

	private long _getCompanyId(T stagedModel) {
		if (stagedModel != null) {
			return stagedModel.getCompanyId();
		}

		return CompanyThreadLocal.getCompanyId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseStagedModelDataHandler.class);

}