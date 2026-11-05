/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.updater;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.RelatedInfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
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
import com.liferay.object.service.ObjectEntryServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectEntryTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.object.web.internal.info.item.handler.ObjectEntryInfoItemExceptionRequestHandler;
import com.liferay.object.web.internal.util.ObjectEntryCMSInfoItemFieldValuesUtil;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.scope.Scope;

import java.text.DateFormat;

import java.util.Locale;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class ObjectEntryInfoItemFieldValuesUpdater
	implements InfoItemFieldValuesUpdater<ObjectEntry> {

	public ObjectEntryInfoItemFieldValuesUpdater(
		InfoItemFormProvider<ObjectEntry> infoItemFormProvider,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectScopeProviderRegistry objectScopeProviderRegistry) {

		_infoItemFormProvider = infoItemFormProvider;
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

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				objectDefinition.getCompanyId(),
				objectDefinition.getStorageType());

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
				infoItemFieldValues, objectDefinition, objectEntry);

			String scopeKey = ObjectEntryInfoItemUtil.getScopeKey(
				objectEntry.getGroupId(), objectDefinition,
				_objectScopeProviderRegistry);

			DTOConverterContext dtoConverterContext =
				new DefaultDTOConverterContext(
					false, null, null, null, null, themeDisplay.getLocale(),
					null, themeDisplay.getUser());

			dtoConverterContext.setAttribute("addActions", Boolean.FALSE);

			_deleteRelatedObjectEntries(
				themeDisplay.getCompanyId(), dtoConverterContext,
				infoItemFieldValues, objectEntry, scopeKey);

			com.liferay.object.rest.dto.v1_0.ObjectEntry dtoObjectEntry = null;

			if (objectEntryManager instanceof
					DefaultObjectEntryManager defaultObjectEntryManager) {

				dtoObjectEntry = defaultObjectEntryManager.getObjectEntry(
					dtoConverterContext, objectDefinition, objectEntry);
			}
			else {
				dtoObjectEntry = objectEntryManager.getObjectEntry(
					objectEntry.getCompanyId(), dtoConverterContext,
					objectEntry.getExternalReferenceCode(), objectDefinition,
					scopeKey);
			}

			dtoObjectEntry = ObjectEntryManagerUtil.partialUpdateObjectEntry(
				dtoObjectEntry, objectDefinition.getObjectDefinitionId(),
				new com.liferay.object.rest.dto.v1_0.ObjectEntry() {
					{
						setFriendlyUrlPath(
							() -> GetterUtil.getString(
								curProperties.get("objectEntryFriendlyURL"),
								null));
						setFriendlyUrlPath_i18n(
							() -> (Map<String, String>)curProperties.get(
								"objectEntryFriendlyURL_i18n"));
						setKeywords(() -> _getKeywords(serviceContext));
						setProperties(() -> curProperties);
						setStatus(
							() -> new Status() {
								{
									setCode(() -> statusInt);
								}
							});
						setTaxonomyCategoryBriefs(
							() -> _getTaxonomyCategoryBriefs(
								themeDisplay.getLocale(), serviceContext));
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

			DTOConverterContext updateDTOConverterContext =
				new DefaultDTOConverterContext(
					false, null, null, null, null, themeDisplay.getLocale(),
					null, themeDisplay.getUser());

			updateDTOConverterContext.setAttribute("addActions", Boolean.FALSE);

			if (objectEntryManager instanceof
					DefaultObjectEntryManager defaultObjectEntryManager) {

				dtoObjectEntry = defaultObjectEntryManager.updateObjectEntry(
					updateDTOConverterContext, objectDefinition, dtoObjectEntry,
					scopeKey, objectEntry);
			}
			else {
				dtoObjectEntry = objectEntryManager.updateObjectEntry(
					objectEntry.getCompanyId(), updateDTOConverterContext,
					dtoObjectEntry.getExternalReferenceCode(), objectDefinition,
					dtoObjectEntry, scopeKey);
			}

			ObjectEntry updatedObjectEntry = ObjectEntryUtil.toObjectEntry(
				objectDefinition, dtoObjectEntry);

			if (objectDefinition.isDefaultStorageType()) {
				ObjectEntryUtil.relateObjectEntries(
					DefaultObjectEntryManagerProvider.provide(
						objectEntryManager),
					infoItemFieldValues, objectDefinition,
					updatedObjectEntry.getPrimaryKey(), serviceContext,
					themeDisplay.getUserId());

				ObjectEntryUtil.relateNestedObjectEntries(
					themeDisplay.getCompanyId(),
					updatedObjectEntry.getGroupId(), infoItemFieldValues,
					_objectEntryManagerRegistry, serviceContext,
					themeDisplay.getUserId());
			}

			return updatedObjectEntry;
		}
		catch (Exception exception) {
			ObjectEntryInfoItemExceptionRequestHandler.handleInfoFormException(
				exception, objectEntry.getGroupId(), _infoItemFormProvider,
				objectDefinition.getObjectDefinitionId());
		}

		return null;
	}

	private void _deleteRelatedObjectEntries(
			long companyId, DTOConverterContext dtoConverterContext,
			InfoItemFieldValues infoItemFieldValues, ObjectEntry objectEntry,
			String scopeKey)
		throws Exception {

		InfoFieldValue<Object> deletedItemIdentifiersInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("deletedItemIdentifiers");

		if (deletedItemIdentifiersInfoFieldValue == null) {
			return;
		}

		String[] deletedItemIdentifiers = GetterUtil.getStringValues(
			deletedItemIdentifiersInfoFieldValue.getValue());
		Tree tree = null;

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
					objectDefinition.getCompanyId(),
					objectDefinition.getStorageType());

			String externalReferenceCode = split[1];

			if (!objectDefinition.isRootDescendantNode()) {
				objectEntryManager.deleteObjectEntry(
					companyId, dtoConverterContext, externalReferenceCode,
					objectDefinition, scopeKey);

				continue;
			}

			ObjectEntry relatedObjectEntry =
				ObjectEntryServiceUtil.fetchObjectEntry(
					externalReferenceCode, objectEntry.getGroupId(),
					objectDefinition.getObjectDefinitionId());

			if ((relatedObjectEntry == null) ||
				!relatedObjectEntry.isRootDescendantNode()) {

				objectEntryManager.deleteObjectEntry(
					companyId, dtoConverterContext, externalReferenceCode,
					objectDefinition, scopeKey);

				continue;
			}

			if (tree == null) {
				ObjectEntryTreeFactory objectEntryTreeFactory =
					new ObjectEntryTreeFactory(
						ObjectEntryLocalServiceUtil.getService(),
						ObjectRelationshipLocalServiceUtil.getService());

				tree = objectEntryTreeFactory.create(
					objectEntry.getObjectEntryId());
			}

			Node node = tree.fetchNode(relatedObjectEntry.getObjectEntryId());

			if (node == null) {
				throw new NoSuchObjectEntryException(
					StringBundler.concat(
						"No ObjectEntry exists with the key ",
						"{externalReferenceCode=", externalReferenceCode,
						", rootObjectEntryId=",
						objectEntry.getRootObjectEntryId(), "}"));
			}

			DefaultObjectEntryManager defaultObjectEntryManager =
				DefaultObjectEntryManagerProvider.provide(objectEntryManager);
			Edge edge = node.getEdge();
			Node parentNode = node.getParentNode();

			defaultObjectEntryManager.deleteRelatedObjectEntry(
				node.getPrimaryKey(),
				ObjectRelationshipLocalServiceUtil.getObjectRelationship(
					edge.getObjectRelationshipId()),
				parentNode.getPrimaryKey());
		}
	}

	private String[] _getKeywords(ServiceContext serviceContext) {
		String[] assetTagNames = serviceContext.getAssetTagNames();

		if (ArrayUtil.isEmpty(assetTagNames)) {
			return null;
		}

		return assetTagNames;
	}

	private Map<String, Object> _getProperties(
		InfoItemFieldValues infoItemFieldValues,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			if (infoFieldValue.getValue() instanceof RelatedInfoFieldValue) {
				return ObjectEntryUtil.toProperties(
					infoItemFieldValues, objectDefinition,
					objectEntry.getValues());
			}
		}

		return ObjectEntryUtil.toProperties(
			objectEntry.getCompanyId(), infoItemFieldValues,
			objectEntry.getValues());
	}

	private TaxonomyCategoryBrief[] _getTaxonomyCategoryBriefs(
		Locale locale, ServiceContext serviceContext) {

		long[] assetCategoryIds = serviceContext.getAssetCategoryIds();

		if (ArrayUtil.isEmpty(assetCategoryIds)) {
			return null;
		}

		return _toTaxonomyCategoryBriefs(assetCategoryIds, locale);
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
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;

}