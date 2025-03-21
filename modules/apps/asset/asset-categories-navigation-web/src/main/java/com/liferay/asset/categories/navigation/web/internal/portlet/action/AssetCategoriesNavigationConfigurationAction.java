/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.navigation.web.internal.portlet.action;

import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ReadOnlyException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "jakarta.portlet.name=" + AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION,
	service = ConfigurationAction.class
)
public class AssetCategoriesNavigationConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	protected void postProcess(
			long companyId, PortletRequest portletRequest,
			PortletPreferences portletPreferences)
		throws PortalException {

		super.postProcess(companyId, portletRequest, portletPreferences);

		boolean allAssetVocabularies = GetterUtil.getBoolean(
			portletPreferences.getValue("allAssetVocabularies", null));

		if (allAssetVocabularies) {
			return;
		}

		String assetVocabularyIdsString = portletPreferences.getValue(
			"assetVocabularyIds", null);

		if (Validator.isNull(assetVocabularyIdsString)) {
			return;
		}

		long[] assetVocabularyIds = GetterUtil.getLongValues(
			StringUtil.split(assetVocabularyIdsString, ','));

		Map<Long, List<String>> groupAssetVocabularyIdsMap =
			_getGroupAssetVocabularyExternalReferenceCodesMap(
				assetVocabularyIds);

		try {
			_resetPortletPreferences(portletPreferences);

			_setPortletPreferences(
				portletPreferences, portletRequest, groupAssetVocabularyIdsMap);

			portletPreferences.reset("assetVocabularyIds");
			portletPreferences.reset("displayStyleGroupId");
		}
		catch (ReadOnlyException readOnlyException) {
			throw new SystemException(readOnlyException);
		}
	}

	private Map<Long, List<String>>
			_getGroupAssetVocabularyExternalReferenceCodesMap(
				long[] assetVocabularyIds)
		throws PortalException {

		Map<Long, List<String>> groupAssetVocabularyExternalReferenceCodesMap =
			new HashMap<>();

		for (long assetVocabularyId : assetVocabularyIds) {
			AssetVocabulary assetVocabulary =
				_assetVocabularyService.fetchVocabulary(assetVocabularyId);

			if (assetVocabulary == null) {
				continue;
			}

			List<String> groupAssetVocabularyExternalReferenceCodes =
				groupAssetVocabularyExternalReferenceCodesMap.computeIfAbsent(
					assetVocabulary.getGroupId(), key -> new ArrayList<>());

			groupAssetVocabularyExternalReferenceCodes.add(
				assetVocabulary.getExternalReferenceCode());
		}

		return groupAssetVocabularyExternalReferenceCodesMap;
	}

	private void _resetPortletPreferences(PortletPreferences portletPreferences)
		throws ReadOnlyException {

		Map<String, String[]> portletPreferencesMap =
			portletPreferences.getMap();

		for (Map.Entry<String, String[]> entry :
				portletPreferencesMap.entrySet()) {

			String key = entry.getKey();

			if (key.startsWith("assetVocabularyExternalReferenceCodes_")) {
				portletPreferences.reset(key);
			}
		}
	}

	private void _setPortletPreferences(
			PortletPreferences portletPreferences,
			PortletRequest portletRequest,
			Map<Long, List<String>> groupAssetVocabularyIdsMap)
		throws PortalException, ReadOnlyException {

		List<String> groupExternalReferenceCodes = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (Map.Entry<Long, List<String>> entries :
				groupAssetVocabularyIdsMap.entrySet()) {

			Group group = _groupLocalService.getGroup(entries.getKey());

			if (group.getGroupId() == themeDisplay.getScopeGroupId()) {
				portletPreferences.setValues(
					"assetVocabularyExternalReferenceCodes",
					ArrayUtil.toStringArray(entries.getValue()));
			}
			else {
				groupExternalReferenceCodes.add(
					group.getExternalReferenceCode());

				portletPreferences.setValues(
					"assetVocabularyExternalReferenceCodes_" +
						group.getExternalReferenceCode(),
					ArrayUtil.toStringArray(entries.getValue()));
			}
		}

		portletPreferences.setValues(
			"assetVocabularyGroupExternalReferenceCodes",
			ArrayUtil.toStringArray(groupExternalReferenceCodes));
	}

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private GroupLocalService _groupLocalService;

}