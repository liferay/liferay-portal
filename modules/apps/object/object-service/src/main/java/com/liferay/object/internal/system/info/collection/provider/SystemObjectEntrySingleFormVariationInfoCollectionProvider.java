/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system.info.collection.provider;

import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.object.info.collection.provider.util.ObjectEntryInfoCollectionProviderUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class SystemObjectEntrySingleFormVariationInfoCollectionProvider
	implements SingleFormVariationInfoCollectionProvider<SystemObjectEntry> {

	public SystemObjectEntrySingleFormVariationInfoCollectionProvider(
		String itemClassName, ObjectDefinition objectDefinition,
		SystemObjectDefinitionManager systemObjectDefinitionManager) {

		_itemClassName = itemClassName;
		_objectDefinition = objectDefinition;
		_systemObjectDefinitionManager = systemObjectDefinitionManager;
	}

	@Override
	public InfoPage<SystemObjectEntry> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		try {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			if (themeDisplay == null) {
				return InfoPage.of(
					Collections.emptyList(), collectionQuery.getPagination(),
					0);
			}

			Page<?> page = _systemObjectDefinitionManager.getPage(
				themeDisplay.getUser(),
				ObjectEntryInfoCollectionProviderUtil.getSearch(
					collectionQuery),
				null,
				ObjectEntryInfoCollectionProviderUtil.getPagination(
					collectionQuery.getPagination()),
				null);

			if (page == null) {
				return InfoPage.of(
					Collections.emptyList(), collectionQuery.getPagination(),
					0);
			}

			return InfoPage.of(
				TransformUtil.transform(
					page.getItems(),
					pageItem -> {
						Map<String, Object> values = ObjectMapperUtil.readValue(
							Map.class, pageItem.toString());

						if (values == null) {
							return null;
						}

						return new SystemObjectEntry(
							GetterUtil.getLong(values.get("id")),
							GetterUtil.getString(
								values.get("externalReferenceCode")),
							values);
					}),
				collectionQuery.getPagination(), (int)page.getTotalCount());
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to get collection info page for object definition " +
					_objectDefinition.getObjectDefinitionId(),
				exception);
		}
	}

	@Override
	public String getCollectionItemClassName() {
		return _itemClassName;
	}

	@Override
	public String getFormVariationKey() {
		return String.valueOf(_objectDefinition.getObjectDefinitionId());
	}

	@Override
	public String getKey() {
		return StringBundler.concat(
			SystemObjectEntrySingleFormVariationInfoCollectionProvider.class.
				getName(),
			StringPool.UNDERLINE, _objectDefinition.getCompanyId(),
			StringPool.UNDERLINE, _objectDefinition.getName());
	}

	@Override
	public String getLabel(Locale locale) {
		return _objectDefinition.getPluralLabel(locale);
	}

	@Override
	public boolean isAvailable() {
		if (_objectDefinition.getCompanyId() !=
				CompanyThreadLocal.getCompanyId()) {

			return false;
		}

		return true;
	}

	private final String _itemClassName;
	private final ObjectDefinition _objectDefinition;
	private final SystemObjectDefinitionManager _systemObjectDefinitionManager;

}