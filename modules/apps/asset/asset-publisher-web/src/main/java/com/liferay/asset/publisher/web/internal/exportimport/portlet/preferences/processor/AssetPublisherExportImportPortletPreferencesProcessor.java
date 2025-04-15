/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfigurationUtil;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.asset.publisher.web.internal.display.context.AssetPublisherDisplayContext;
import com.liferay.asset.publisher.web.internal.helper.AssetPublisherWebHelper;
import com.liferay.asset.publisher.web.internal.util.AssetPublisherUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.base.BaseExportImportPortletPreferencesProcessor;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.model.adapter.StagedGroup;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletPreferences;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the implementation of
 * <code>ExportImportPortletPreferencesProcessor</code> (in the
 * <code>com.liferay.exportimport.api</code> module) for the Asset Publisher
 * portlet. This implementation provides specific export and import capabilities
 * and routines for processing portlet preferences while exporting or importing
 * Asset Publisher instances.
 *
 * @author Máté Thurzó
 */
@Component(
	configurationPid = "com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration",
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
	service = ExportImportPortletPreferencesProcessor.class
)
public class AssetPublisherExportImportPortletPreferencesProcessor
	extends BaseExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(exportCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(importCapability);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			if (MergeLayoutPrototypesThreadLocal.isInProgress()) {
				if (MapUtil.getBoolean(
						portletDataContext.getParameterMap(),
						PortletDataHandlerKeys.PORTLET_DATA)) {

					_exportAssetObjects(portletDataContext, portletPreferences);
				}
			}
			else {
				_exportAssetObjects(portletDataContext, portletPreferences);
			}

			return _updateExportPortletPreferences(
				portletDataContext, portletDataContext.getPortletId(),
				portletPreferences);
		}
		catch (Exception exception) {
			PortletDataException portletDataException =
				new PortletDataException(
					"Unable to update portlet preferences during export",
					exception);

			portletDataException.setPortletId(
				AssetPublisherPortletKeys.ASSET_PUBLISHER);
			portletDataException.setType(
				PortletDataException.EXPORT_PORTLET_DATA);

			throw portletDataException;
		}
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			_importLayoutReferences(portletDataContext);

			capability.process(portletDataContext, portletPreferences);

			return _updateImportPortletPreferences(
				portletDataContext, portletPreferences);
		}
		catch (Exception exception) {
			PortletDataException portletDataException =
				new PortletDataException(
					"Unable to update portlet preferences during import",
					exception);

			portletDataException.setPortletId(
				AssetPublisherPortletKeys.ASSET_PUBLISHER);
			portletDataException.setType(
				PortletDataException.IMPORT_PORTLET_DATA);

			throw portletDataException;
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_assetPublisherWebConfiguration = ConfigurableUtil.createConfigurable(
			AssetPublisherWebConfiguration.class, properties);
	}

	@Override
	protected String getExportPortletPreferencesValue(
			PortletDataContext portletDataContext, Portlet portlet,
			String className, long primaryKeyLong)
		throws Exception {

		String uuid = null;
		long groupId = 0L;

		Element rootElement = portletDataContext.getExportDataRootElement();

		if (className.equals(AssetCategory.class.getName())) {
			AssetCategory assetCategory =
				assetCategoryLocalService.fetchCategory(primaryKeyLong);

			if (assetCategory != null) {
				uuid = assetCategory.getUuid();
				groupId = assetCategory.getGroupId();

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portlet.getPortletId(), assetCategory);
			}
		}
		else if (className.equals(AssetVocabulary.class.getName())) {
			AssetVocabulary assetVocabulary =
				assetVocabularyLocalService.fetchAssetVocabulary(
					primaryKeyLong);

			if (assetVocabulary != null) {
				uuid = assetVocabulary.getUuid();
				groupId = assetVocabulary.getGroupId();

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portlet.getPortletId(),
					assetVocabulary);
			}
		}
		else if (className.equals(DDMStructure.class.getName())) {
			DDMStructure ddmStructure = ddmStructureLocalService.fetchStructure(
				primaryKeyLong);

			if ((ddmStructure != null) &&
				(!ExportImportThreadLocal.isStagingInProcess() ||
				 stagingGroupHelper.isStagedPortletData(
					 portletDataContext.getGroupId(),
					 ddmStructure.getClassName()))) {

				uuid = ddmStructure.getUuid();
				groupId = ddmStructure.getGroupId();

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portlet.getPortletId(), ddmStructure);
			}
		}
		else if (className.equals(DLFileEntryType.class.getName())) {
			DLFileEntryType dlFileEntryType =
				dlFileEntryTypeLocalService.fetchFileEntryType(primaryKeyLong);

			if ((dlFileEntryType != null) &&
				(!ExportImportThreadLocal.isStagingInProcess() ||
				 stagingGroupHelper.isStagedPortletData(
					 portletDataContext.getGroupId(),
					 DLFileEntry.class.getName()))) {

				uuid = dlFileEntryType.getUuid();
				groupId = dlFileEntryType.getGroupId();

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portlet.getPortletId(),
					dlFileEntryType);
			}
		}
		else if (className.equals(Organization.class.getName())) {
			Organization organization =
				organizationLocalService.fetchOrganization(primaryKeyLong);

			if (organization != null) {
				uuid = organization.getUuid();

				portletDataContext.addReferenceElement(
					portlet, rootElement, organization,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
			}
		}

		if (Validator.isNull(uuid)) {
			return null;
		}

		return StringUtil.merge(new Object[] {uuid, groupId}, StringPool.POUND);
	}

	@Override
	protected String getImportPortletPreferencesNewExternalReferenceCode(
		PortletDataContext portletDataContext, Class<?> clazz,
		long companyGroupId, Map<String, String[]> primaryKeys,
		String externalReferenceCode) {

		String className = clazz.getName();

		if (!className.equals(Group.class.getName())) {
			return null;
		}

		Group group = groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, portletDataContext.getCompanyId());

		if (group == null) {
			return null;
		}

		return externalReferenceCode;
	}

	@Override
	protected Long getImportPortletPreferencesNewValue(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<Long, Long> primaryKeys,
			String portletPreferencesOldValue)
		throws Exception {

		if (Validator.isNumber(portletPreferencesOldValue)) {
			long oldPrimaryKey = GetterUtil.getLong(portletPreferencesOldValue);

			return MapUtil.getLong(primaryKeys, oldPrimaryKey, oldPrimaryKey);
		}

		String className = clazz.getName();

		String[] oldValues = StringUtil.split(
			portletPreferencesOldValue, StringPool.POUND);

		String uuid = oldValues[0];

		long groupId = portletDataContext.getScopeGroupId();

		if (oldValues.length > 1) {
			Map<Long, Long> groupIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Group.class);

			groupId = MapUtil.getLong(
				groupIds, GetterUtil.getLong(oldValues[1]), groupId);
		}

		if (className.equals(AssetCategory.class.getName())) {
			AssetCategory assetCategory =
				assetCategoryLocalService.fetchAssetCategoryByUuidAndGroupId(
					uuid, groupId);

			if (assetCategory != null) {
				return assetCategory.getCategoryId();
			}
		}
		else if (className.equals(AssetVocabulary.class.getName())) {
			AssetVocabulary assetVocabulary =
				assetVocabularyLocalService.
					fetchAssetVocabularyByUuidAndGroupId(uuid, groupId);

			if (assetVocabulary != null) {
				return assetVocabulary.getVocabularyId();
			}
		}
		else if (className.equals(DDMStructure.class.getName())) {
			DDMStructure ddmStructure =
				ddmStructureLocalService.fetchStructureByUuidAndGroupId(
					uuid, groupId, true);

			if (ddmStructure == null) {
				Map<String, String> structureUuids =
					(Map<String, String>)
						portletDataContext.getNewPrimaryKeysMap(
							DDMStructure.class + ".ddmStructureUuid");

				String defaultStructureUuid = MapUtil.getString(
					structureUuids, uuid, uuid);

				ddmStructure =
					ddmStructureLocalService.fetchDDMStructureByUuidAndGroupId(
						defaultStructureUuid, groupId);
			}

			if (ddmStructure != null) {
				return ddmStructure.getStructureId();
			}
		}
		else if (className.equals(DLFileEntryType.class.getName())) {
			DLFileEntryType dlFileEntryType =
				dlFileEntryTypeLocalService.
					fetchDLFileEntryTypeByUuidAndGroupId(uuid, groupId);

			if (dlFileEntryType == null) {
				Element rootElement =
					portletDataContext.getImportDataRootElement();

				Element element = portletDataContext.getReferenceElement(
					rootElement, clazz, companyGroupId, uuid,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY);

				if (element != null) {
					String fileEntryTypeKey = element.attributeValue(
						"file-entry-type-key");

					boolean preloaded = GetterUtil.getBoolean(
						element.attributeValue("preloaded"));

					if (preloaded) {
						dlFileEntryType =
							dlFileEntryTypeLocalService.fetchFileEntryType(
								companyGroupId, fileEntryTypeKey);
					}
				}
			}

			if (dlFileEntryType != null) {
				return dlFileEntryType.getFileEntryTypeId();
			}
		}

		return null;
	}

	@Reference
	protected AssetCategoryLocalService assetCategoryLocalService;

	@Reference
	protected AssetPublisherHelper assetPublisherHelper;

	@Reference
	protected AssetPublisherWebHelper assetPublisherWebHelper;

	@Reference
	protected AssetVocabularyLocalService assetVocabularyLocalService;

	@Reference(target = "(name=ReferencedStagedModelImporter)")
	protected Capability capability;

	@Reference
	protected CompanyLocalService companyLocalService;

	@Reference
	protected DDMStructureLocalService ddmStructureLocalService;

	@Reference
	protected DLFileEntryTypeLocalService dlFileEntryTypeLocalService;

	@Reference(target = "(name=CommonPortletDisplayTemplateExportCapability)")
	protected Capability exportCapability;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference(target = "(name=CommonPortletDisplayTemplateImportCapability)")
	protected Capability importCapability;

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected OrganizationLocalService organizationLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletLocalService portletLocalService;

	@Reference
	protected StagingGroupHelper stagingGroupHelper;

	private void _exportAssetObjects(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		List<AssetEntry> assetEntries = null;

		Layout layout = layoutLocalService.getLayout(
			portletDataContext.getPlid());

		String selectionStyle = portletPreferences.getValue(
			"selectionStyle",
			AssetPublisherSelectionStyleConfigurationUtil.
				defaultSelectionStyle());

		if (selectionStyle.equals(
				AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC)) {

			if (!_assetPublisherWebConfiguration.dynamicExportEnabled() ||
				layout.isTypeAssetDisplay()) {

				return;
			}

			AssetEntryQuery assetEntryQuery = _getAssetEntryQuery(
				layout, portletDataContext.getCompanyGroupId(),
				portletDataContext.getScopeGroupId(), portletPreferences);

			long assetVocabularyId = GetterUtil.getLong(
				portletPreferences.getValue("assetVocabularyId", null));

			if (assetVocabularyId > 0) {
				_mergeAnyCategoryIds(assetEntryQuery, assetVocabularyId);

				if (ArrayUtil.isEmpty(assetEntryQuery.getAnyCategoryIds())) {
					return;
				}
			}

			BaseModelSearchResult<AssetEntry> baseModelSearchResult =
				assetPublisherHelper.getAssetEntries(
					assetEntryQuery, layout, portletPreferences,
					AssetPublisherPortletKeys.ASSET_PUBLISHER,
					LocaleUtil.getDefault(), TimeZoneUtil.getDefault(),
					portletDataContext.getCompanyId(),
					portletDataContext.getScopeGroupId(),
					UserConstants.USER_ID_DEFAULT,
					new HashMap<String, Serializable>(),
					assetEntryQuery.getStart(), assetEntryQuery.getEnd());

			assetEntries = baseModelSearchResult.getBaseModels();
		}
		else {
			if (!_assetPublisherWebConfiguration.manualExportEnabled()) {
				return;
			}

			long[] groupIds = assetPublisherHelper.getGroupIds(
				portletPreferences, portletDataContext.getScopeGroupId(),
				layout);

			assetEntries = assetPublisherHelper.getAssetEntries(
				null, portletPreferences,
				PermissionThreadLocal.getPermissionChecker(), groupIds, false,
				false);
		}

		for (AssetEntry assetEntry : assetEntries) {
			AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

			if ((assetRenderer == null) ||
				!(assetRenderer.getAssetObject() instanceof StagedModel)) {

				continue;
			}

			AssetRendererFactory<?> assetRendererFactory =
				assetRenderer.getAssetRendererFactory();

			if ((assetRendererFactory != null) &&
				ExportImportThreadLocal.isStagingInProcess() &&
				!stagingGroupHelper.isStagedPortlet(
					assetEntry.getGroupId(),
					assetRendererFactory.getPortletId())) {

				continue;
			}

			if (!portletDataContext.addPrimaryKey(
					AssetEntry.class, assetRenderer.getUuid())) {

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portletDataContext.getPortletId(),
					(StagedModel)assetRenderer.getAssetObject());
			}
		}
	}

	private AssetEntryQuery _getAssetEntryQuery(
			Layout layout, long companyId, long groupId,
			PortletPreferences portletPreferences)
		throws Exception {

		AssetEntryQuery assetEntryQuery =
			assetPublisherHelper.getAssetEntryQuery(
				portletPreferences, groupId, layout, null, null);

		assetEntryQuery.setClassNameIds(
			assetPublisherHelper.getClassNameIds(
				portletPreferences,
				AssetRendererFactoryRegistryUtil.getClassNameIds(
					companyId, true)));
		assetEntryQuery.setEnablePermissions(false);

		int end = _assetPublisherWebConfiguration.dynamicExportLimit();

		if (_isPaginationTypeNone(portletPreferences)) {
			int delta = GetterUtil.getInteger(
				portletPreferences.getValue("delta", null),
				SearchContainer.DEFAULT_DELTA);

			if ((delta < end) || (end == 0)) {
				end = delta;
			}
		}

		if (end == 0) {
			end = QueryUtil.ALL_POS;
		}

		assetEntryQuery.setEnd(end);
		assetEntryQuery.setExcludeZeroViewCount(false);

		int start = 0;

		if (end == 0) {
			start = QueryUtil.ALL_POS;
		}

		assetEntryQuery.setStart(start);

		return assetEntryQuery;
	}

	private String _getExportScopeId(
			PortletDataContext portletDataContext,
			Element groupIdMappingsElement, Layout layout, String value)
		throws Exception {

		if (value.startsWith(AssetPublisherHelper.SCOPE_ID_LAYOUT_PREFIX)) {

			// Legacy preferences

			String scopeIdSuffix = value.substring(
				AssetPublisherHelper.SCOPE_ID_LAYOUT_PREFIX.length());

			long scopeIdLayoutId = GetterUtil.getLong(scopeIdSuffix);

			Layout scopeIdLayout = layoutLocalService.fetchLayout(
				layout.getGroupId(), layout.isPrivateLayout(), scopeIdLayoutId);

			if (scopeIdLayout == null) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Ignoring scope ", value,
							" because the referenced layout ", scopeIdLayoutId,
							" was not found"));
				}

				return value;
			}

			if (layout.getPlid() != scopeIdLayout.getPlid()) {
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portletDataContext.getPortletId(),
					scopeIdLayout);
			}

			return AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX +
				scopeIdLayout.getUuid();
		}

		if (value.startsWith(
				AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX)) {

			String scopeLayoutUuid = value.substring(
				AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX.length());

			Layout scopeUuidLayout =
				layoutLocalService.fetchLayoutByUuidAndGroupId(
					scopeLayoutUuid, portletDataContext.getGroupId(),
					portletDataContext.isPrivateLayout());

			if (scopeUuidLayout == null) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Ignoring scope ", value,
							" because the referenced layout ", scopeLayoutUuid,
							" was not found"));
				}

				return value;
			}

			if (layout.getPlid() != scopeUuidLayout.getPlid()) {
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portletDataContext.getPortletId(),
					scopeUuidLayout);
			}

			return value;
		}

		long groupId = GroupConstants.DEFAULT_LIVE_GROUP_ID;

		try {
			groupId = assetPublisherHelper.getGroupIdFromScopeId(
				value, portletDataContext.getGroupId(),
				portletDataContext.isPrivateLayout());
		}
		catch (NoSuchGroupException noSuchGroupException) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Ignoring scope ", value, " because the referenced ",
						"group was not found"),
					noSuchGroupException);
			}

			return value;
		}
		catch (PrincipalException principalException) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Ignoring scope ", value, " because the referenced ",
						"parent group no longer allows sharing content with ",
						"child sites"),
					principalException);
			}

			return value;
		}

		Group group = groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return value;
		}

		long liveGroupId = group.getLiveGroupId();

		if (group.isStagedRemotely()) {
			liveGroupId = group.getRemoteLiveGroupId();
		}

		if (liveGroupId == GroupConstants.DEFAULT_LIVE_GROUP_ID) {
			liveGroupId = group.getGroupId();
		}

		Element groupIdMappingElement = groupIdMappingsElement.addElement(
			"group-id-mapping");

		groupIdMappingElement.addAttribute("group-id", String.valueOf(groupId));
		groupIdMappingElement.addAttribute(
			"live-group-id", String.valueOf(liveGroupId));
		groupIdMappingElement.addAttribute("group-key", group.getGroupKey());

		return String.valueOf(groupId);
	}

	private void _importLayoutReferences(PortletDataContext portletDataContext)
		throws PortletDataException {

		Element importDataRootElement =
			portletDataContext.getImportDataRootElement();

		Element referencesElement = importDataRootElement.element("references");

		if (referencesElement == null) {
			return;
		}

		List<Element> referenceElements = referencesElement.elements();

		for (Element referenceElement : referenceElements) {
			String className = referenceElement.attributeValue("class-name");

			if (!className.equals(Layout.class.getName())) {
				continue;
			}

			long classPK = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, className, Long.valueOf(classPK));
		}
	}

	private boolean _isPaginationTypeNone(
		PortletPreferences portletPreferences) {

		String paginationType = GetterUtil.getString(
			portletPreferences.getValue("paginationType", null));

		if (!ArrayUtil.contains(
				AssetPublisherDisplayContext.PAGINATION_TYPES,
				paginationType) ||
			Objects.equals(
				paginationType,
				AssetPublisherDisplayContext.PAGINATION_TYPE_NONE)) {

			return true;
		}

		return false;
	}

	private void _mergeAnyCategoryIds(
		AssetEntryQuery assetEntryQuery, long assetVocabularyId) {

		List<AssetCategory> assetCategories =
			assetCategoryLocalService.getVocabularyRootCategories(
				assetVocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		long[] vocabularyCategoryIds = new long[0];

		for (AssetCategory assetCategory : assetCategories) {
			vocabularyCategoryIds = ArrayUtil.append(
				vocabularyCategoryIds, assetCategory.getCategoryId());
		}

		long[] originalAnyCategoryIds = assetEntryQuery.getAnyCategoryIds();

		if (ArrayUtil.isEmpty(originalAnyCategoryIds)) {
			assetEntryQuery.setAnyCategoryIds(vocabularyCategoryIds);
		}
		else {
			long[] newAnyCategoryIds = new long[0];

			for (long originalAnyCategoryId : originalAnyCategoryIds) {
				if (ArrayUtil.contains(
						vocabularyCategoryIds, originalAnyCategoryId)) {

					newAnyCategoryIds = ArrayUtil.append(
						newAnyCategoryIds, originalAnyCategoryId);
				}
			}

			assetEntryQuery.setAnyCategoryIds(newAnyCategoryIds);
		}
	}

	private void _restorePortletPreference(
			PortletDataContext portletDataContext, String name,
			PortletPreferences portletPreferences)
		throws Exception {

		PortletPreferences originalPortletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				layoutLocalService.getLayout(portletDataContext.getPlid()),
				portletDataContext.getPortletId());

		String[] values = originalPortletPreferences.getValues(
			name, new String[] {StringPool.BLANK});

		portletPreferences.setValues(name, values);
	}

	private void _updateExportClassNameIds(
			PortletPreferences portletPreferences, String key)
		throws Exception {

		String[] oldValues = portletPreferences.getValues(key, null);

		if (oldValues == null) {
			return;
		}

		String[] newValues = new String[oldValues.length];

		int i = 0;

		for (String oldValue : oldValues) {
			if (key.equals("anyAssetType") &&
				(oldValue.equals("false") || oldValue.equals("true"))) {

				newValues[i++] = oldValue;

				continue;
			}

			try {
				long classNameId = GetterUtil.getLong(oldValue);

				String className = portal.getClassName(classNameId);

				newValues[i++] = className;
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to get class name ID for class name " +
							oldValue,
						exception);
				}
			}
		}

		portletPreferences.setValues(key, newValues);
	}

	private void _updateExportOrderByColumnClassPKs(
			PortletDataContext portletDataContext, Portlet portlet,
			PortletPreferences portletPreferences, String key)
		throws Exception {

		String oldValue = portletPreferences.getValue(key, null);

		String[] ddmStructureFieldNameParts = StringUtil.split(
			oldValue, DDMIndexer.DDM_FIELD_SEPARATOR);

		String primaryKey = ddmStructureFieldNameParts[2];

		if (!Validator.isNumber(primaryKey)) {
			return;
		}

		long primaryKeyLong = GetterUtil.getLong(primaryKey);

		String newPreferencesValue = getExportPortletPreferencesValue(
			portletDataContext, portlet, DDMStructure.class.getName(),
			primaryKeyLong);

		if (Validator.isNull(newPreferencesValue)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to export portlet preferences value for class ",
						DDMStructure.class.getName(), " with primary key ",
						primaryKeyLong));
			}

			return;
		}

		String newValue = StringUtil.replace(
			oldValue, primaryKey, newPreferencesValue);

		portletPreferences.setValue(key, newValue);
	}

	private PortletPreferences _updateExportPortletPreferences(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		String anyAssetTypeString = portletPreferences.getValue(
			"anyAssetType", null);

		String selectionStyle = portletPreferences.getValue(
			"selectionStyle", null);

		if (Validator.isNotNull(selectionStyle) &&
			selectionStyle.equals(
				AssetPublisherSelectionStyleConstants.TYPE_MANUAL)) {

			portletPreferences.reset("anyAssetType");

			anyAssetTypeString = portletPreferences.getValue(
				"anyAssetType", null);
		}
		else if (Validator.isNotNull(anyAssetTypeString) &&
				 anyAssetTypeString.equals("false")) {

			String[] classNameIds = portletPreferences.getValues(
				"classNameIds", StringPool.EMPTY_ARRAY);

			if (classNameIds.length == 1) {
				portletPreferences.setValue("anyAssetType", classNameIds[0]);

				anyAssetTypeString = portletPreferences.getValue(
					"anyAssetType", null);

				portletPreferences.reset("classNameIds");
			}
		}

		String anyAssetTypeClassName = StringPool.BLANK;

		long anyAssetType = GetterUtil.getLong(anyAssetTypeString);

		if (anyAssetType > 0) {
			anyAssetTypeClassName = portal.getClassName(anyAssetType);
		}

		Portlet portlet = portletLocalService.getPortletById(
			portletDataContext.getCompanyId(), portletId);

		Enumeration<String> enumeration = portletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			String value = GetterUtil.getString(
				portletPreferences.getValue(name, null));

			if (name.equals("anyAssetType") || name.equals("classNameIds")) {
				if (name.equals("classNameIds") &&
					Validator.isNotNull(anyAssetTypeString) &&
					!anyAssetTypeString.equals("false")) {

					portletPreferences.reset(name);
				}
				else {
					_updateExportClassNameIds(portletPreferences, name);
				}
			}
			else if (name.equals(
						"anyClassTypeDLFileEntryAssetRendererFactory") ||
					 (name.equals("classTypeIds") &&
					  anyAssetTypeClassName.equals(
						  DLFileEntry.class.getName())) ||
					 name.equals(
						 "classTypeIdsDLFileEntryAssetRendererFactory")) {

				String anyClassTypeDLFileEntryAssetRendererFactory =
					portletPreferences.getValue(
						"anyClassTypeDLFileEntryAssetRendererFactory", null);

				String[] classTypeIdsDLFileEntryAssetRendererFactory =
					portletPreferences.getValues(
						"classTypeIdsDLFileEntryAssetRendererFactory",
						StringPool.EMPTY_ARRAY);

				if (Validator.isNotNull(
						anyClassTypeDLFileEntryAssetRendererFactory) &&
					anyClassTypeDLFileEntryAssetRendererFactory.equals(
						"false") &&
					(classTypeIdsDLFileEntryAssetRendererFactory.length == 1) &&
					!classTypeIdsDLFileEntryAssetRendererFactory[0].contains(
						StringPool.COMMA)) {

					portletPreferences.setValue(
						"anyClassTypeDLFileEntryAssetRendererFactory",
						classTypeIdsDLFileEntryAssetRendererFactory[0]);

					portletPreferences.reset(
						"classTypeIdsDLFileEntryAssetRendererFactory");

					anyClassTypeDLFileEntryAssetRendererFactory =
						portletPreferences.getValue(
							"anyClassTypeDLFileEntryAssetRendererFactory",
							null);
				}

				if (!anyAssetTypeClassName.equals(
						DLFileEntry.class.getName()) ||
					(name.equals(
						"classTypeIdsDLFileEntryAssetRendererFactory") &&
					 Validator.isNotNull(
						 anyClassTypeDLFileEntryAssetRendererFactory) &&
					 !anyClassTypeDLFileEntryAssetRendererFactory.equals(
						 "false"))) {

					portletPreferences.reset(name);
				}
				else {
					updateExportPortletPreferencesClassPKs(
						portletDataContext, portlet, portletPreferences, name,
						DLFileEntryType.class.getName());
				}
			}
			else if (name.equals(
						"anyClassTypeJournalArticleAssetRendererFactory") ||
					 (name.equals("classTypeIds") &&
					  anyAssetTypeClassName.equals(
						  JournalArticle.class.getName())) ||
					 name.equals(
						 "classTypeIdsJournalArticleAssetRendererFactory")) {

				String anyClassTypeJournalArticleAssetRendererFactory =
					portletPreferences.getValue(
						"anyClassTypeJournalArticleAssetRendererFactory", null);

				String[] classTypeIdsJournalArticleAssetRendererFactory =
					portletPreferences.getValues(
						"classTypeIdsJournalArticleAssetRendererFactory",
						StringPool.EMPTY_ARRAY);

				if (Validator.isNotNull(
						anyClassTypeJournalArticleAssetRendererFactory) &&
					anyClassTypeJournalArticleAssetRendererFactory.equals(
						"false") &&
					(classTypeIdsJournalArticleAssetRendererFactory.length ==
						1) &&
					!classTypeIdsJournalArticleAssetRendererFactory[0].contains(
						StringPool.COMMA)) {

					portletPreferences.setValue(
						"anyClassTypeJournalArticleAssetRendererFactory",
						classTypeIdsJournalArticleAssetRendererFactory[0]);

					portletPreferences.reset(
						"classTypeIdsJournalArticleAssetRendererFactory");

					anyClassTypeJournalArticleAssetRendererFactory =
						portletPreferences.getValue(
							"anyClassTypeJournalArticleAssetRendererFactory",
							null);
				}

				if (!anyAssetTypeClassName.equals(
						JournalArticle.class.getName()) ||
					(name.equals(
						"classTypeIdsJournalArticleAssetRendererFactory") &&
					 Validator.isNotNull(
						 anyClassTypeJournalArticleAssetRendererFactory) &&
					 !anyClassTypeJournalArticleAssetRendererFactory.equals(
						 "false"))) {

					portletPreferences.reset(name);
				}
				else {
					updateExportPortletPreferencesClassPKs(
						portletDataContext, portlet, portletPreferences, name,
						DDMStructure.class.getName());
				}
			}
			else if (name.equals("assetListEntryExternalReferenceCode")) {
				AssetListEntry assetListEntry =
					AssetPublisherUtil.getAssetListEntry(
						false, portletDataContext.getCompanyId(),
						portletDataContext.getScopeGroupId(),
						portletPreferences);

				if (assetListEntry != null) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, portletId, assetListEntry);
				}

				portletPreferences.reset("assetListEntryId");
			}
			else if (name.equals("assetListEntryGroupExternalReferenceCode")) {
				updateExportPortletPreferencesExternalReferenceCodes(
					portletDataContext, portlet, portletPreferences, name,
					Group.class.getName());
			}
			else if (name.equals("assetVocabularyId")) {
				long assetVocabularyId = GetterUtil.getLong(value);

				AssetVocabulary assetVocabulary =
					assetVocabularyLocalService.fetchAssetVocabulary(
						assetVocabularyId);

				if (assetVocabulary != null) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, portletId, assetVocabulary);
				}

				updateExportPortletPreferencesClassPKs(
					portletDataContext, portlet, portletPreferences, name,
					AssetVocabulary.class.getName());
			}
			else if (name.startsWith("orderByColumn") &&
					 StringUtil.startsWith(
						 value, DDMIndexer.DDM_FIELD_PREFIX)) {

				_updateExportOrderByColumnClassPKs(
					portletDataContext, portlet, portletPreferences, name);
			}
			else if (name.startsWith("queryName") &&
					 StringUtil.equalsIgnoreCase(value, "assetCategories")) {

				String index = name.substring(9);

				long assetCategoryId = GetterUtil.getLong(
					portletPreferences.getValue("queryValues" + index, null));

				AssetCategory assetCategory =
					assetCategoryLocalService.fetchAssetCategory(
						assetCategoryId);

				if (assetCategory != null) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, portletId, assetCategory);
				}

				updateExportPortletPreferencesClassPKs(
					portletDataContext, portlet, portletPreferences,
					"queryValues" + index, AssetCategory.class.getName());
			}
			else if (name.equals("scopeIds")) {
				_updateExportScopeIds(
					portletDataContext, portletPreferences, name,
					portletDataContext.getPlid());
			}
		}

		return portletPreferences;
	}

	private void _updateExportScopeIds(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, String key, long plid)
		throws Exception {

		String[] oldValues = portletPreferences.getValues(key, null);

		if (oldValues == null) {
			return;
		}

		Layout layout = layoutLocalService.getLayout(plid);

		String[] newValues = new String[oldValues.length];

		Element rootElement = portletDataContext.getExportDataRootElement();

		Element groupIdMappingsElement = rootElement.addElement(
			"group-id-mappings");

		for (int i = 0; i < oldValues.length; i++) {
			newValues[i] = _getExportScopeId(
				portletDataContext, groupIdMappingsElement, layout,
				oldValues[i]);
		}

		portletPreferences.setValues(key, newValues);
	}

	private void _updateImportClassNameIds(
			PortletPreferences portletPreferences, String key)
		throws Exception {

		String[] oldValues = portletPreferences.getValues(key, null);

		if (oldValues == null) {
			return;
		}

		String[] newValues = new String[oldValues.length];

		int i = 0;

		for (String oldValue : oldValues) {
			if (key.equals("anyAssetType") &&
				(oldValue.equals("false") || oldValue.equals("true"))) {

				newValues[i++] = oldValue;

				continue;
			}

			try {
				newValues[i++] = String.valueOf(
					portal.getClassNameId(oldValue));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to find class name ID for class name " +
							oldValue,
						exception);
				}
			}
		}

		portletPreferences.setValues(key, newValues);
	}

	private void _updateImportOrderByColumnClassPKs(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, String key,
			long companyGroupId)
		throws Exception {

		String oldValue = portletPreferences.getValue(key, null);

		Map<Long, Long> primaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDMStructure.class);

		String[] ddmStructureFieldNameParts = StringUtil.split(
			oldValue, DDMIndexer.DDM_FIELD_SEPARATOR);

		String portletPreferencesOldValue = ddmStructureFieldNameParts[2];

		Long newPrimaryKey = getImportPortletPreferencesNewValue(
			portletDataContext, DDMStructure.class, companyGroupId, primaryKeys,
			portletPreferencesOldValue);

		if (Validator.isNull(newPrimaryKey)) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Unable to import portlet preferences value " +
						portletPreferencesOldValue);
			}

			return;
		}

		String newValue = StringUtil.replace(
			oldValue, portletPreferencesOldValue, newPrimaryKey.toString());

		portletPreferences.setValue(key, newValue);
	}

	private PortletPreferences _updateImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		Company company = companyLocalService.getCompanyById(
			portletDataContext.getCompanyId());

		Group companyGroup = company.getGroup();

		String anyAssetTypeClassName = portletPreferences.getValue(
			"anyAssetType", StringPool.BLANK);

		Enumeration<String> enumeration = portletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			String value = GetterUtil.getString(
				portletPreferences.getValue(name, null));

			if (name.equals("anyAssetType") || name.equals("classNameIds")) {
				_updateImportClassNameIds(portletPreferences, name);
			}
			else if (name.equals(
						"anyClassTypeDLFileEntryAssetRendererFactory") ||
					 (name.equals("classTypeIds") &&
					  anyAssetTypeClassName.equals(
						  DLFileEntry.class.getName())) ||
					 name.equals(
						 "classTypeIdsDLFileEntryAssetRendererFactory")) {

				updateImportPortletPreferencesClassPKs(
					portletDataContext, portletPreferences, name,
					DLFileEntryType.class, companyGroup.getGroupId());
			}
			else if (name.equals(
						"anyClassTypeJournalArticleAssetRendererFactory") ||
					 (name.equals("classTypeIds") &&
					  anyAssetTypeClassName.equals(
						  JournalArticle.class.getName())) ||
					 name.equals(
						 "classTypeIdsJournalArticleAssetRendererFactory")) {

				updateImportPortletPreferencesClassPKs(
					portletDataContext, portletPreferences, name,
					DDMStructure.class, companyGroup.getGroupId());
			}
			else if (name.equals("assetListEntryGroupExternalReferenceCode")) {
				updateImportPortletPreferencesExternalReferenceCodes(
					portletDataContext, portletPreferences, name, Group.class,
					companyGroup.getGroupId());
			}
			else if (name.equals("assetVocabularyId")) {
				updateImportPortletPreferencesClassPKs(
					portletDataContext, portletPreferences, name,
					AssetVocabulary.class, companyGroup.getGroupId());
			}
			else if (name.startsWith("orderByColumn") &&
					 StringUtil.startsWith(
						 value, DDMIndexer.DDM_FIELD_PREFIX)) {

				_updateImportOrderByColumnClassPKs(
					portletDataContext, portletPreferences, name,
					companyGroup.getGroupId());
			}
			else if (name.startsWith("queryName") &&
					 StringUtil.equalsIgnoreCase(value, "assetCategories")) {

				String index = name.substring(9);

				updateImportPortletPreferencesClassPKs(
					portletDataContext, portletPreferences,
					"queryValues" + index, AssetCategory.class,
					companyGroup.getGroupId());
			}
			else if (name.equals("scopeIds")) {
				_updateImportScopeIds(
					portletDataContext, portletPreferences, name,
					companyGroup.getGroupId(), portletDataContext.getPlid());
			}
		}

		_restorePortletPreference(
			portletDataContext, "notifiedAssetEntryIds", portletPreferences);

		return portletPreferences;
	}

	private void _updateImportScopeIds(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, String key,
			long companyGroupId, long plid)
		throws Exception {

		String[] oldValues = portletPreferences.getValues(key, null);

		if (oldValues == null) {
			return;
		}

		StagedModelDataHandler<StagedGroup> stagedModelDataHandler =
			(StagedModelDataHandler<StagedGroup>)
				StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
					StagedGroup.class.getName());

		Element rootElement = portletDataContext.getImportDataRootElement();

		Element groupIdMappingsElement = rootElement.element(
			"group-id-mappings");

		for (Element groupIdMappingElement :
				groupIdMappingsElement.elements("group-id-mapping")) {

			stagedModelDataHandler.importMissingReference(
				portletDataContext, groupIdMappingElement);
		}

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		Layout layout = layoutLocalService.getLayout(plid);

		List<String> newValues = new ArrayList<>(oldValues.length);

		for (String oldValue : oldValues) {
			String newValue = oldValue;

			if (Objects.equals(oldValue, "[$COMPANY_GROUP_SCOPE_ID$]")) {
				oldValue = String.valueOf(companyGroupId);
			}

			if (Validator.isNumber(oldValue)) {
				long groupId = Long.valueOf(oldValue);

				if (groupIds.containsKey(groupId)) {
					groupId = groupIds.get(groupId);
				}

				Group group = groupLocalService.fetchGroup(groupId);

				if (group == null) {
					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Ignoring group ", newValue, " because it ",
								"cannot be converted to scope"));
					}

					continue;
				}

				newValue = assetPublisherHelper.getScopeId(
					group, portletDataContext.getScopeGroupId());
			}

			try {
				if (!assetPublisherWebHelper.isScopeIdSelectable(
						PermissionThreadLocal.getPermissionChecker(), newValue,
						companyGroupId, layout, false)) {

					continue;
				}

				newValues.add(newValue);
			}
			catch (NoSuchGroupException noSuchGroupException) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Ignoring scope ", newValue, " because the ",
							"referenced group was not found"),
						noSuchGroupException);
				}
			}
			catch (NoSuchLayoutException noSuchLayoutException) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Ignoring scope ", newValue, " because the ",
							"referenced layout was not found"),
						noSuchLayoutException);
				}
			}
			catch (PrincipalException principalException) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Ignoring scope ", newValue, " because the ",
							"referenced parent group no longer allows sharing ",
							"content with child sites"),
						principalException);
				}
			}
		}

		portletPreferences.setValues(key, newValues.toArray(new String[0]));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetPublisherExportImportPortletPreferencesProcessor.class);

	private volatile AssetPublisherWebConfiguration
		_assetPublisherWebConfiguration;

}