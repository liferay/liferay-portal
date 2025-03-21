/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Eudaldo Alonso
 */
public class ContentDashboardAdminConfigurationDisplayContext {

	public ContentDashboardAdminConfigurationDisplayContext(
		AssetVocabularyLocalService assetVocabularyLocalService,
		long[] assetVocabularyIds, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_assetVocabularyLocalService = assetVocabularyLocalService;
		_assetVocabularyIds = assetVocabularyIds;
		_groupLocalService = groupLocalService;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public ActionURL getActionURL() {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/content_dashboard/update_content_dashboard_configuration"
		).setRedirect(
			getRedirect()
		).buildActionURL();
	}

	public JSONArray getAvailableVocabularyJSONArray() {
		long[] assetVocabularyIds = ArrayUtil.clone(_assetVocabularyIds);

		Arrays.sort(assetVocabularyIds);

		List<AssetVocabulary> filteredAssetVocabularies = ListUtil.filter(
			_getAvailableAssetVocabularies(),
			assetVocabulary -> {
				int pos = Arrays.binarySearch(
					assetVocabularyIds, assetVocabulary.getVocabularyId());

				return pos < 0;
			});

		filteredAssetVocabularies.sort(
			Comparator.comparing(
				this::_getAssetVocabularyLabel, String.CASE_INSENSITIVE_ORDER));

		return JSONUtil.toJSONArray(
			filteredAssetVocabularies, this::_toJSONObject, _log);
	}

	public JSONArray getCurrentVocabularyJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (long assetVocabularyId : _assetVocabularyIds) {
			try {
				AssetVocabulary assetVocabulary =
					_assetVocabularyLocalService.getAssetVocabulary(
						assetVocabularyId);

				if (assetVocabulary != null) {
					jsonArray.put(_toJSONObject(assetVocabulary));
				}
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		return jsonArray;
	}

	public String getRedirect() {
		return _themeDisplay.getURLCurrent();
	}

	private List<AssetVocabulary> _getAssetVocabularies() {
		if (_assetVocabularies != null) {
			return _assetVocabularies;
		}

		_assetVocabularies = _assetVocabularyLocalService.getGroupVocabularies(
			ArrayUtil.toLongArray(
				_groupLocalService.getGroupIds(
					_themeDisplay.getCompanyId(), true)));

		return _assetVocabularies;
	}

	private String _getAssetVocabularyLabel(AssetVocabulary assetVocabulary) {
		String assetVocabularyTitle = assetVocabulary.getTitle(
			_themeDisplay.getLanguageId());

		Group group = _groupLocalService.fetchGroup(
			assetVocabulary.getGroupId());

		if (group == null) {
			return assetVocabularyTitle;
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			_themeDisplay.getLocale(), getClass());

		try {
			return LanguageUtil.format(
				resourceBundle, "x-group-x",
				new String[] {
					assetVocabularyTitle,
					group.getDescriptiveName(resourceBundle.getLocale())
				});
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return LanguageUtil.format(
				resourceBundle, "x-group-x",
				new String[] {
					assetVocabularyTitle,
					group.getName(resourceBundle.getLocale())
				});
		}
	}

	private List<AssetVocabulary> _getAvailableAssetVocabularies() {
		if (_availableAssetVocabularies == null) {
			_availableAssetVocabularies = _getAssetVocabularies();
		}

		return _availableAssetVocabularies;
	}

	private JSONObject _toJSONObject(AssetVocabulary assetVocabulary) {
		Group group = _groupLocalService.fetchGroup(
			assetVocabulary.getGroupId());

		return JSONUtil.put(
			"global", group.isCompany()
		).put(
			"label", HtmlUtil.escape(_getAssetVocabularyLabel(assetVocabulary))
		).put(
			"site", assetVocabulary.getGroupId()
		).put(
			"value", assetVocabulary.getVocabularyId()
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardAdminConfigurationDisplayContext.class);

	private List<AssetVocabulary> _assetVocabularies;
	private final long[] _assetVocabularyIds;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private List<AssetVocabulary> _availableAssetVocabularies;
	private final GroupLocalService _groupLocalService;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}