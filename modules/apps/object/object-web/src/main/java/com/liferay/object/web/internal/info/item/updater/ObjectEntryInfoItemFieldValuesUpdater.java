/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.updater;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.object.rest.dto.v1_0.util.ScopeUtil;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.web.internal.info.item.handler.ObjectEntryInfoItemExceptionRequestHandler;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.text.DateFormat;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class ObjectEntryInfoItemFieldValuesUpdater
	implements InfoItemFieldValuesUpdater<ObjectEntry> {

	public ObjectEntryInfoItemFieldValuesUpdater(
		InfoItemFormProvider<ObjectEntry> infoItemFormProvider,
		ObjectDefinition objectDefinition,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectScopeProviderRegistry objectScopeProviderRegistry) {

		_infoItemFormProvider = infoItemFormProvider;
		_objectDefinition = objectDefinition;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
	}

	@Override
	public ObjectEntry updateFromInfoItemFieldValues(
			ObjectEntry objectEntry, InfoItemFieldValues infoItemFieldValues)
		throws Exception {

		return updateFromInfoItemFieldValues(
			objectEntry, infoItemFieldValues,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public ObjectEntry updateFromInfoItemFieldValues(
			ObjectEntry objectEntry, InfoItemFieldValues infoItemFieldValues,
			int statusInt)
		throws InfoFormException {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		Map<String, Object> curProperties = _getProperties(
			objectEntry, infoItemFieldValues, themeDisplay);

		try {
			String scopeKey = ObjectEntryInfoItemUtil.getScopeKey(
				objectEntry.getGroupId(), _objectDefinition,
				_objectScopeProviderRegistry);

			_deleteRelatedObjectEntries(
				themeDisplay.getCompanyId(),
				new DefaultDTOConverterContext(
					false, null, null, null, null, themeDisplay.getLocale(),
					null, themeDisplay.getUser()),
				infoItemFieldValues, scopeKey);

			com.liferay.object.rest.dto.v1_0.ObjectEntry dtoObjectEntry =
				objectEntryManager.partialUpdateObjectEntry(
					objectEntry.getCompanyId(),
					new DefaultDTOConverterContext(
						false, null, null, null, null, themeDisplay.getLocale(),
						null, themeDisplay.getUser()),
					objectEntry.getExternalReferenceCode(), _objectDefinition,
					new com.liferay.object.rest.dto.v1_0.ObjectEntry() {
						{
							setFriendlyUrlPath(
								() -> GetterUtil.getString(
									curProperties.get("objectEntryFriendlyURL"),
									null));
							setFriendlyUrlPath_i18n(
								() -> (Map<String, String>)curProperties.get(
									"objectEntryFriendlyURL_i18n"));
							setKeywords(serviceContext::getAssetTagNames);
							setProperties(() -> curProperties);
							setStatus(
								() -> new Status() {
									{
										setCode(() -> statusInt);
									}
								});
							setTaxonomyCategoryBriefs(
								() -> _toTaxonomyCategoryBriefs(
									serviceContext.getAssetCategoryIds()));
						}
					},
					scopeKey);

			if (curProperties.containsKey("displayDate") ||
				curProperties.containsKey("expirationDate") ||
				curProperties.containsKey("reviewDate")) {

				dtoObjectEntry.setDisplayDate(
					() -> {
						if (curProperties.containsKey("displayDate")) {
							return GetterUtil.getDate(
								curProperties.get("displayDate"),
								_dateTimeFormatter, null);
						}

						return objectEntry.getDisplayDate();
					});
				dtoObjectEntry.setExpirationDate(
					() -> {
						if (curProperties.containsKey("expirationDate")) {
							return GetterUtil.getDate(
								curProperties.get("expirationDate"),
								_dateTimeFormatter, null);
						}

						return objectEntry.getExpirationDate();
					});
				dtoObjectEntry.setReviewDate(
					() -> {
						if (curProperties.containsKey("reviewDate")) {
							return GetterUtil.getDate(
								curProperties.get("reviewDate"),
								_dateTimeFormatter, null);
						}

						return objectEntry.getReviewDate();
					});

				dtoObjectEntry = objectEntryManager.updateObjectEntry(
					objectEntry.getCompanyId(),
					new DefaultDTOConverterContext(
						false, null, null, null, null, themeDisplay.getLocale(),
						null, themeDisplay.getUser()),
					dtoObjectEntry.getExternalReferenceCode(),
					_objectDefinition, dtoObjectEntry, scopeKey);
			}

			return ObjectEntryUtil.toObjectEntry(
				objectEntry.getObjectDefinitionId(), dtoObjectEntry);
		}
		catch (Exception exception) {
			ObjectEntryInfoItemExceptionRequestHandler.handleInfoFormException(
				exception, objectEntry.getGroupId(), _infoItemFormProvider,
				_objectDefinition);
		}

		return null;
	}

	private void _deleteRelatedObjectEntries(
			long companyId, DTOConverterContext dtoConverterContext,
			InfoItemFieldValues infoItemFieldValues, String scopeKey)
		throws Exception {

		InfoFieldValue<Object> deletedItemIdentifiersInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("deletedItemIdentifiers");

		String[] deletedItemIdentifiers = GetterUtil.getStringValues(
			deletedItemIdentifiersInfoFieldValue.getValue());

		for (String deletedItemIdentifier : deletedItemIdentifiers) {
			String[] split = StringUtil.split(
				deletedItemIdentifier, StringPool.UNDERLINE);

			if (split.length != 2) {
				continue;
			}

			String className = split[0];

			ObjectDefinition objectDefinition =
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByClassName(companyId, className);

			if (objectDefinition == null) {
				continue;
			}

			ObjectEntryManager objectEntryManager =
				_objectEntryManagerRegistry.getObjectEntryManager(
					objectDefinition.getStorageType());

			String externalReferenceCode = split[1];

			objectEntryManager.deleteObjectEntry(
				companyId, dtoConverterContext, externalReferenceCode,
				objectDefinition, scopeKey);
		}
	}

	private Map<String, Object> _getProperties(
		ObjectEntry objectEntry, InfoItemFieldValues infoItemFieldValues,
		ThemeDisplay themeDisplay) {

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getScopeGroupId(), "LPD-50377")) {

			return ObjectEntryUtil.toProperties(
				infoItemFieldValues, _objectDefinition,
				objectEntry.getValues());
		}

		return ObjectEntryUtil.toProperties(
			themeDisplay.getCompanyId(), infoItemFieldValues,
			objectEntry.getValues());
	}

	private TaxonomyCategoryBrief[] _toTaxonomyCategoryBriefs(
		long[] assetCategoryIds) {

		return TransformUtil.transformToArray(
			ListUtil.fromArray(assetCategoryIds),
			assetCategoryId -> {
				AssetCategory assetCategory =
					AssetCategoryServiceUtil.getCategory(assetCategoryId);

				return new TaxonomyCategoryBrief() {
					{
						setScope(
							() -> ScopeUtil.toScope(
								assetCategory.getGroupId()));
						setTaxonomyCategoryId(() -> assetCategoryId);
					}
				};
			},
			TaxonomyCategoryBrief.class);
	}

	private static final DateFormat _dateTimeFormatter =
		DateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm");

	private final InfoItemFormProvider<ObjectEntry> _infoItemFormProvider;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;

}