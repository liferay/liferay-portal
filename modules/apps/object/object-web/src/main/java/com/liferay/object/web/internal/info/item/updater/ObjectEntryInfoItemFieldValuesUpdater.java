/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.updater;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.RelatedInfoFieldValue;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.rest.manager.v1_0.util.ObjectEntryManagerUtil;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.web.internal.info.item.handler.ObjectEntryInfoItemExceptionRequestHandler;
import com.liferay.object.web.internal.util.ObjectEntryCMSInfoItemFieldValuesUtil;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.scope.Scope;

import java.text.DateFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		try {
			infoItemFieldValues =
				ObjectEntryCMSInfoItemFieldValuesUtil.
					normalizeInfoItemFieldValues(
						infoItemFieldValues, objectEntry,
						_objectEntryManagerRegistry,
						_objectScopeProviderRegistry, serviceContext);

			Map<String, Object> curProperties = _getProperties(
				objectEntry, infoItemFieldValues);

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
				ObjectEntryManagerUtil.partialUpdateObjectEntry(
					objectEntryManager.getObjectEntry(
						objectEntry.getCompanyId(),
						new DefaultDTOConverterContext(
							false, null, null, null, null,
							themeDisplay.getLocale(), null,
							themeDisplay.getUser()),
						objectEntry.getExternalReferenceCode(),
						_objectDefinition, scopeKey),
					_objectDefinition.getObjectDefinitionId(),
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
									serviceContext.getAssetCategoryIds(),
									themeDisplay.getLocale()));
						}
					});

			if (curProperties.containsKey("displayDate") ||
				curProperties.containsKey("expirationDate") ||
				curProperties.containsKey("reviewDate")) {

				dtoObjectEntry.setDisplayDate(
					() -> {
						Map.Entry<String, Object> displayDateEntry =
							MapUtil.getEntry(curProperties, "displayDate");

						if (displayDateEntry != null) {
							return GetterUtil.getDate(
								displayDateEntry.getValue(), _dateTimeFormatter,
								null);
						}

						return objectEntry.getDisplayDate();
					});
				dtoObjectEntry.setExpirationDate(
					() -> {
						Map.Entry<String, Object> expirationDateEntry =
							MapUtil.getEntry(curProperties, "expirationDate");

						if (expirationDateEntry != null) {
							return GetterUtil.getDate(
								expirationDateEntry.getValue(),
								_dateTimeFormatter, null);
						}

						return objectEntry.getExpirationDate();
					});
				dtoObjectEntry.setReviewDate(
					() -> {
						Map.Entry<String, Object> reviewDateEntry =
							MapUtil.getEntry(curProperties, "reviewDate");

						if (reviewDateEntry != null) {
							return GetterUtil.getDate(
								reviewDateEntry.getValue(), _dateTimeFormatter,
								null);
						}

						return objectEntry.getReviewDate();
					});
			}

			ObjectEntry updatedObjectEntry = ObjectEntryUtil.toObjectEntry(
				_objectDefinition,
				objectEntryManager.updateObjectEntry(
					objectEntry.getCompanyId(),
					new DefaultDTOConverterContext(
						false, null, null, null, null, themeDisplay.getLocale(),
						null, themeDisplay.getUser()),
					dtoObjectEntry.getExternalReferenceCode(),
					_objectDefinition, dtoObjectEntry, scopeKey));

			_relateMainObjectEntry(
				infoItemFieldValues, _objectDefinition, updatedObjectEntry,
				serviceContext, themeDisplay.getUserId());

			_relateNestedObjectEntries(
				themeDisplay.getCompanyId(), updatedObjectEntry.getGroupId(),
				infoItemFieldValues, serviceContext, themeDisplay.getUserId());

			return updatedObjectEntry;
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

		if (deletedItemIdentifiersInfoFieldValue == null) {
			return;
		}

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
					_objectDefinition.getCompanyId(),
					objectDefinition.getStorageType());

			String externalReferenceCode = split[1];

			objectEntryManager.deleteObjectEntry(
				companyId, dtoConverterContext, externalReferenceCode,
				objectDefinition, scopeKey);
		}
	}

	private Map<String, Object> _getProperties(
		ObjectEntry objectEntry, InfoItemFieldValues infoItemFieldValues) {

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			if (infoFieldValue.getValue() instanceof RelatedInfoFieldValue) {
				return ObjectEntryUtil.toProperties(
					infoItemFieldValues, _objectDefinition,
					objectEntry.getValues());
			}
		}

		return ObjectEntryUtil.toProperties(
			objectEntry.getCompanyId(), infoItemFieldValues,
			objectEntry.getValues());
	}

	private void _relateMainObjectEntry(
			InfoItemFieldValues infoItemFieldValues,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ServiceContext serviceContext, long userId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					objectDefinition.getCompanyId(),
					objectDefinition.getStorageType()));

		for (ObjectRelationship objectRelationship :
				ObjectRelationshipLocalServiceUtil.getObjectRelationships(
					objectDefinition.getObjectDefinitionId(),
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
					false)) {

			if (!objectRelationship.compareType(
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

				continue;
			}

			ObjectDefinition relatedObjectDefinition =
				ObjectRelationshipUtil.getRelatedObjectDefinition(
					objectDefinition, objectRelationship);

			defaultObjectEntryManager.disassociateRelatedModels(
				new DefaultDTOConverterContext(
					false, null, null, null,
					LocaleUtil.fromLanguageId(
						objectDefinition.getDefaultLanguageId(), true, false),
					null, null),
				objectDefinition, objectRelationship,
				objectEntry.getPrimaryKey(), relatedObjectDefinition, userId);

			InfoFieldValue<Object> infoFieldValue =
				infoItemFieldValues.getInfoFieldValue(
					ObjectRelationshipConstants.
						OBJECT_RELATIONSHIP_FIELD_NAME_PREFIX +
							objectRelationship.getName());

			Object value = infoFieldValue.getValue();

			if (value instanceof List) {
				List<String> relatedObjectEntryIds = (List<String>)value;

				for (String relatedObjectEntryId : relatedObjectEntryIds) {
					ObjectRelationshipLocalServiceUtil.
						addObjectRelationshipMappingTableValues(
							userId,
							objectRelationship.getObjectRelationshipId(),
							objectEntry.getPrimaryKey(),
							GetterUtil.getLong(relatedObjectEntryId),
							serviceContext);
				}
			}
		}
	}

	private void _relateNestedObjectEntries(
			long companyId, long groupId,
			InfoItemFieldValues infoItemFieldValues,
			ServiceContext serviceContext, long userId)
		throws Exception {

		Map<String, ObjectDefinition> objectDefinitionMap = new HashMap<>();

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoField infoField = infoFieldValue.getInfoField();

			if (!StringUtil.startsWith(
					infoField.getUniqueId(),
					ObjectRelationship.class.getSimpleName()) ||
				!(infoField.getInfoFieldType() instanceof
					RelationshipInfoFieldType)) {

				continue;
			}

			InfoField<RelationshipInfoFieldType>
				relationshipInfoFieldTypeInfoField =
					(InfoField<RelationshipInfoFieldType>)infoField;

			if (!relationshipInfoFieldTypeInfoField.getAttribute(
					RelationshipInfoFieldType.MULTIPLE)) {

				continue;
			}

			RelatedInfoFieldValue relatedInfoFieldValue =
				(RelatedInfoFieldValue)infoFieldValue.getValue();

			String[] parts = StringUtil.split(
				infoField.getUniqueId(), StringPool.POUND);

			Map
				<RelatedInfoFieldValue.RelatedInfoFieldValueIdentifier,
				 InfoFieldValue<?>> relatedInfoFieldValues =
					relatedInfoFieldValue.getRelatedInfoFieldValues();

			for (Map.Entry
					<RelatedInfoFieldValue.RelatedInfoFieldValueIdentifier,
					 InfoFieldValue<?>> entry :
						relatedInfoFieldValues.entrySet()) {

				InfoFieldValue<?> entryInfoFieldValue = entry.getValue();

				if (entryInfoFieldValue == null) {
					continue;
				}

				Object value = entryInfoFieldValue.getValue();

				if (!(value instanceof List<?>) ||
					ListUtil.isEmpty((List<?>)value)) {

					continue;
				}

				String objectDefinitionName = parts[1];

				ObjectDefinition objectDefinition = objectDefinitionMap.get(
					objectDefinitionName);

				if (objectDefinition == null) {
					objectDefinition =
						ObjectDefinitionLocalServiceUtil.getObjectDefinition(
							companyId, objectDefinitionName);

					objectDefinitionMap.put(
						objectDefinitionName, objectDefinition);
				}

				DefaultObjectEntryManager defaultObjectEntryManager =
					DefaultObjectEntryManagerProvider.provide(
						_objectEntryManagerRegistry.getObjectEntryManager(
							objectDefinition.getCompanyId(),
							objectDefinition.getStorageType()));

				RelatedInfoFieldValue.RelatedInfoFieldValueIdentifier
					relatedInfoFieldValueIdentifier = entry.getKey();

				ObjectEntry objectEntry =
					ObjectEntryLocalServiceUtil.fetchObjectEntry(
						relatedInfoFieldValueIdentifier.
							getExternalReferenceCode(),
						groupId, objectDefinition.getObjectDefinitionId());

				ObjectRelationship objectRelationship =
					ObjectRelationshipLocalServiceUtil.getObjectRelationship(
						objectDefinition.getObjectDefinitionId(),
						StringUtil.removeSubstring(
							infoField.getName(),
							ObjectRelationshipConstants.
								OBJECT_RELATIONSHIP_FIELD_NAME_PREFIX));

				ObjectDefinition relatedObjectDefinition =
					ObjectRelationshipUtil.getRelatedObjectDefinition(
						objectDefinition, objectRelationship);

				defaultObjectEntryManager.disassociateRelatedModels(
					new DefaultDTOConverterContext(
						false, null, null, null,
						LocaleUtil.fromLanguageId(
							objectDefinition.getDefaultLanguageId(), true,
							false),
						null, null),
					objectDefinition, objectRelationship,
					objectEntry.getPrimaryKey(), relatedObjectDefinition,
					userId);

				List<String> relatedObjectEntryIds = (List<String>)value;

				for (String relatedObjectEntryId : relatedObjectEntryIds) {
					ObjectRelationshipLocalServiceUtil.
						addObjectRelationshipMappingTableValues(
							userId,
							objectRelationship.getObjectRelationshipId(),
							objectEntry.getPrimaryKey(),
							GetterUtil.getLong(relatedObjectEntryId),
							serviceContext);
				}
			}
		}
	}

	private TaxonomyCategoryBrief[] _toTaxonomyCategoryBriefs(
		long[] assetCategoryIds, Locale locale) {

		return TransformUtil.transformToArray(
			ListUtil.fromArray(assetCategoryIds),
			assetCategoryId -> {
				AssetCategory assetCategory =
					AssetCategoryServiceUtil.getCategory(assetCategoryId);

				return new TaxonomyCategoryBrief() {
					{
						setScope(
							() -> Scope.of(assetCategory.getGroupId(), locale));
						setTaxonomyCategoryExternalReferenceCode(
							assetCategory::getExternalReferenceCode);
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