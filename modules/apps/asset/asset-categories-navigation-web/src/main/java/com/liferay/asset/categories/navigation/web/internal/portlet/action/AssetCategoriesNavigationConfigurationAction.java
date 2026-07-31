/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.navigation.web.internal.portlet.action;

import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.petra.string.StringPool;
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
import java.util.LinkedHashMap;
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

		if (assetVocabularyIdsString == null) {
			return;
		}

		long[] assetVocabularyIds = GetterUtil.getLongValues(
			StringUtil.split(assetVocabularyIdsString, ','));

		try {
			_resetPortletPreferences(portletPreferences);

			_setPortletPreferences(
				assetVocabularyIds, portletPreferences, portletRequest);

			portletPreferences.reset("assetVocabularyIds");
			portletPreferences.reset("displayStyleGroupId");
		}
		catch (ReadOnlyException readOnlyException) {
			throw new SystemException(readOnlyException);
		}
	}

	private void _resetPortletPreferences(PortletPreferences portletPreferences)
		throws ReadOnlyException {

		Map<String, String[]> portletPreferencesMap =
			portletPreferences.getMap();

		for (Map.Entry<String, String[]> entry :
				portletPreferencesMap.entrySet()) {

			String key = entry.getKey();

			if (key.startsWith("assetVocabularyExternalReferenceCodes")) {
				portletPreferences.reset(key);
			}
		}
	}

	private void _setPortletPreferences(
			long[] assetVocabularyIds, PortletPreferences portletPreferences,
			PortletRequest portletRequest)
		throws PortalException, ReadOnlyException {

		Map<String, List<String>> assetVocabularyExternalReferenceCodesMap =
			new LinkedHashMap<>();
		List<String> groupExternalReferenceCodes = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (long assetVocabularyId : assetVocabularyIds) {
			AssetVocabulary assetVocabulary =
				_assetVocabularyService.fetchVocabulary(assetVocabularyId);

			if (assetVocabulary == null) {
				continue;
			}

			Group group = _groupLocalService.getGroup(
				assetVocabulary.getGroupId());

			String groupExternalReferenceCode = StringPool.BLANK;

			if (group.getGroupId() != themeDisplay.getScopeGroupId()) {
				groupExternalReferenceCode = group.getExternalReferenceCode();
			}

			groupExternalReferenceCodes.add(groupExternalReferenceCode);

			String key = "assetVocabularyExternalReferenceCodes";

			if (Validator.isNotNull(groupExternalReferenceCode)) {
				key += "_" + groupExternalReferenceCode;
			}

			List<String> assetVocabularyExternalReferenceCodes =
				assetVocabularyExternalReferenceCodesMap.computeIfAbsent(
					key, curKey -> new ArrayList<>());

			assetVocabularyExternalReferenceCodes.add(
				assetVocabulary.getExternalReferenceCode());
		}

		for (Map.Entry<String, List<String>> entry :
				assetVocabularyExternalReferenceCodesMap.entrySet()) {

			portletPreferences.setValues(
				entry.getKey(), ArrayUtil.toStringArray(entry.getValue()));
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