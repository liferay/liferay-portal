/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.type.facet.portlet;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.asset.SearchableAssetClassNamesProvider;
import com.liferay.portal.search.web.internal.portlet.preferences.BasePortletPreferences;

import jakarta.portlet.PortletPreferences;

import java.util.List;
import java.util.Locale;

/**
 * @author Lino Alves
 */
public class TypeFacetPortletPreferencesImpl
	extends BasePortletPreferences implements TypeFacetPortletPreferences {

	public TypeFacetPortletPreferencesImpl(
		ObjectDefinitionLocalService objectDefinitionLocalService,
		PortletPreferences portletPreferences,
		SearchableAssetClassNamesProvider searchableAssetClassNamesProvider) {

		super(portletPreferences);

		_objectDefinitionLocalService = objectDefinitionLocalService;
		_searchableAssetClassNamesProvider = searchableAssetClassNamesProvider;
	}

	@Override
	public String getAssetTypes() {
		return getString(
			TypeFacetPortletPreferences.PREFERENCE_KEY_ASSET_TYPES,
			StringPool.BLANK);
	}

	@Override
	public List<KeyValuePair> getAvailableAssetTypes(
		long companyId, Locale locale) {

		String[] assetTypes = getCurrentAssetTypesArray(companyId);

		return TransformUtil.transformToList(
			getAllAssetTypes(companyId),
			assetType -> {
				if (ArrayUtil.contains(assetTypes, assetType)) {
					return null;
				}

				return _getKeyValuePair(assetType, companyId, locale);
			});
	}

	@Override
	public List<KeyValuePair> getCurrentAssetTypes(
		long companyId, Locale locale) {

		return TransformUtil.transformToList(
			getCurrentAssetTypesArray(companyId),
			assetType -> _getKeyValuePair(assetType, companyId, locale));
	}

	@Override
	public String[] getCurrentAssetTypesArray(long companyId) {
		String assetTypes = getString(
			TypeFacetPortletPreferences.PREFERENCE_KEY_ASSET_TYPES, null);

		if (assetTypes != null) {
			return StringUtil.split(assetTypes);
		}

		return getAllAssetTypes(companyId);
	}

	@Override
	public int getFrequencyThreshold() {
		return getInteger(
			TypeFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD, 1);
	}

	@Override
	public String getOrder() {
		return getString(
			TypeFacetPortletPreferences.PREFERENCE_KEY_ORDER, "count:desc");
	}

	@Override
	public String getParameterName() {
		return getString(
			TypeFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME, "type");
	}

	@Override
	public boolean isFrequenciesVisible() {
		return getBoolean(
			TypeFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE,
			true);
	}

	protected String[] getAllAssetTypes(long companyId) {
		return _searchableAssetClassNamesProvider.getClassNames(companyId);
	}

	private KeyValuePair _getKeyValuePair(
		String className, long companyId, Locale locale) {

		String modelResource = ResourceActionsUtil.getModelResource(
			locale, className);

		if (className.startsWith(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION)) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					companyId, className);

			if (objectDefinition != null) {
				modelResource = objectDefinition.getLabel(locale);
			}
		}

		return new KeyValuePair(className, modelResource);
	}

	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final SearchableAssetClassNamesProvider
		_searchableAssetClassNamesProvider;

}