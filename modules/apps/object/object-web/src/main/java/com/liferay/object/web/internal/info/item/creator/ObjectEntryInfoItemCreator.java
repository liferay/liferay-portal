/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.creator;

import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.creator.InfoItemCreator;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.web.internal.info.item.handler.ObjectEntryInfoItemExceptionRequestHandler;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.text.DateFormat;

import java.util.Map;

/**
 * @author Rubén Pulido
 */
public class ObjectEntryInfoItemCreator
	implements InfoItemCreator<ObjectEntry> {

	public ObjectEntryInfoItemCreator(
		InfoItemFormProvider<ObjectEntry> infoItemFormProvider,
		ObjectDefinition objectDefinition,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectScopeProviderRegistry objectScopeProviderRegistry) {

		_infoItemFormProvider = infoItemFormProvider;
		_objectDefinition = objectDefinition;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
	}

	@Override
	public ObjectEntry createFromInfoItemFieldValues(
			long groupId, InfoItemFieldValues infoItemFieldValues,
			int statusInt)
		throws InfoFormException {

		try {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"yyyy-MM-dd HH:mm");

			ObjectEntryManager objectEntryManager =
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType());

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			Map<String, Object> curProperties = _getProperties(
				infoItemFieldValues, themeDisplay);

			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
				objectEntryManager.addObjectEntry(
					new DefaultDTOConverterContext(
						false, null, null, null, null, themeDisplay.getLocale(),
						null, themeDisplay.getUser()),
					_objectDefinition,
					new com.liferay.object.rest.dto.v1_0.ObjectEntry() {
						{
							setDisplayDate(
								() -> GetterUtil.getDate(
									curProperties.get("displayDate"),
									dateFormat, null));
							setExpirationDate(
								() -> GetterUtil.getDate(
									curProperties.get("expirationDate"),
									dateFormat, null));
							setFriendlyUrlPath(
								() -> GetterUtil.getString(
									curProperties.get("objectEntryFriendlyURL"),
									null));
							setFriendlyUrlPath_i18n(
								() -> (Map<String, String>)curProperties.get(
									"objectEntryFriendlyURL_i18n"));
							setKeywords(serviceContext::getAssetTagNames);
							setProperties(() -> curProperties);
							setReviewDate(
								() -> GetterUtil.getDate(
									curProperties.get("reviewDate"), dateFormat,
									null));
							setStatus(
								() -> new Status() {
									{
										setCode(() -> statusInt);
									}
								});
							setTaxonomyCategoryIds(
								() -> ArrayUtil.toLongArray(
									serviceContext.getAssetCategoryIds()));
						}
					},
					ObjectEntryInfoItemUtil.getScopeKey(
						groupId, _objectDefinition,
						_objectScopeProviderRegistry));

			ObjectEntry serviceBuilderObjectEntry =
				_objectEntryLocalService.createObjectEntry(
					GetterUtil.getLong(objectEntry.getId()));

			serviceBuilderObjectEntry.setExternalReferenceCode(
				objectEntry.getExternalReferenceCode());
			serviceBuilderObjectEntry.setObjectDefinitionId(
				_objectDefinition.getObjectDefinitionId());

			return serviceBuilderObjectEntry;
		}
		catch (Exception exception) {
			ObjectEntryInfoItemExceptionRequestHandler.handleInfoFormException(
				exception, groupId, _infoItemFormProvider, _objectDefinition);
		}

		return null;
	}

	private Map<String, Object> _getProperties(
		InfoItemFieldValues infoItemFieldValues, ThemeDisplay themeDisplay) {

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getScopeGroupId(), "LPD-50377")) {

			return ObjectEntryUtil.toProperties(
				infoItemFieldValues, _objectDefinition, null);
		}

		return ObjectEntryUtil.toProperties(
			themeDisplay.getCompanyId(), infoItemFieldValues, null);
	}

	private final InfoItemFormProvider<ObjectEntry> _infoItemFormProvider;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;

}