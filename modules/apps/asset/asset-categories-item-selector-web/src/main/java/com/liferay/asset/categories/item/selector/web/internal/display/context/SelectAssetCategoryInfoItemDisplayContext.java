/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.item.selector.web.internal.display.context;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;
import com.liferay.portlet.asset.util.comparator.AssetVocabularyGroupLocalizedTitleComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SelectAssetCategoryInfoItemDisplayContext {

	public SelectAssetCategoryInfoItemDisplayContext(
		HttpServletRequest httpServletRequest,
		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
		String itemSelectedEventName, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_infoItemItemSelectorCriterion = infoItemItemSelectorCriterion;
		_itemSelectedEventName = itemSelectedEventName;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"addCategoryURL",
			() -> {
				try {
					return _getAddCategoryURL();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}

				return null;
			}
		).put(
			"itemSelectedEventName", _itemSelectedEventName
		).put(
			"moveCategory",
			ParamUtil.getBoolean(_httpServletRequest, "moveCategory")
		).put(
			"multiSelection", _isMultiSelection()
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"nodes", _getVocabulariesJSONArray()
		).put(
			"selectedCategoryIds",
			() -> ParamUtil.getStringValues(
				_httpServletRequest, "selectedCategoryIds")
		).build();
	}

	public List<Long> getVocabularyIds() {
		if (_vocabularyIds != null) {
			return _vocabularyIds;
		}

		long[] vocabularyIds = ParamUtil.getLongValues(
			_httpServletRequest, "vocabularyIds");

		if (ArrayUtil.isNotEmpty(vocabularyIds)) {
			_vocabularyIds = ListUtil.fromArray(vocabularyIds);

			return _vocabularyIds;
		}

		List<AssetVocabulary> assetVocabularies =
			AssetVocabularyServiceUtil.getGroupVocabularies(
				new long[] {
					_themeDisplay.getCompanyGroupId(),
					_themeDisplay.getScopeGroupId()
				},
				new int[] {AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC});

		if (assetVocabularies.isEmpty()) {
			_vocabularyIds = Collections.emptyList();

			return _vocabularyIds;
		}

		ListUtil.sort(
			assetVocabularies,
			new AssetVocabularyGroupLocalizedTitleComparator(
				_themeDisplay.getScopeGroupId(), _themeDisplay.getLocale(),
				true));

		_vocabularyIds = ListUtil.toList(
			assetVocabularies, AssetVocabulary.VOCABULARY_ID_ACCESSOR);

		return _vocabularyIds;
	}

	public String getVocabularyTitle(long vocabularyId) throws PortalException {
		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.fetchAssetVocabulary(vocabularyId);

		StringBundler sb = new StringBundler(5);

		sb.append(
			HtmlUtil.escape(
				assetVocabulary.getTitle(_themeDisplay.getLocale())));

		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		if (assetVocabulary.getGroupId() == _themeDisplay.getCompanyGroupId()) {
			sb.append(LanguageUtil.get(_httpServletRequest, "global"));
		}
		else {
			Group group = GroupLocalServiceUtil.fetchGroup(
				assetVocabulary.getGroupId());

			sb.append(group.getDescriptiveName(_themeDisplay.getLocale()));
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private String _getAddCategoryURL() throws Exception {
		if (!ParamUtil.getBoolean(
				_httpServletRequest, "showAddCategoryButton")) {

			return null;
		}

		List<Long> vocabularyIds = getVocabularyIds();

		if (vocabularyIds.size() != 1) {
			return null;
		}

		AssetVocabulary vocabulary = AssetVocabularyServiceUtil.getVocabulary(
			vocabularyIds.get(0));

		if (!AssetCategoryPermission.contains(
				_themeDisplay.getPermissionChecker(), vocabulary.getGroupId(),
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
				ActionKeys.ADD_CATEGORY) ||
			!Objects.equals(
				vocabulary.getGroupId(), _themeDisplay.getScopeGroupId())) {

			return null;
		}

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_httpServletRequest,
				AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_asset_category.jsp"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"groupId", vocabulary.getGroupId()
		).setParameter(
			"itemSelectorEventName", _itemSelectedEventName
		).setParameter(
			"vocabularyId", vocabulary.getVocabularyId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private JSONArray _getCategoriesJSONArray(
			long vocabularyId, long categoryId)
		throws Exception {

		List<AssetCategory> assetCategories =
			AssetCategoryServiceUtil.getVocabularyCategories(
				categoryId, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		return JSONUtil.toJSONArray(
			assetCategories,
			assetCategory -> JSONUtil.put(
				"children",
				() -> {
					JSONArray childrenJSONArray = _getCategoriesJSONArray(
						vocabularyId, assetCategory.getCategoryId());

					if (childrenJSONArray.length() > 0) {
						return childrenJSONArray;
					}

					return null;
				}
			).put(
				"className", AssetCategory.class.getName()
			).put(
				"classNameId",
				PortalUtil.getClassNameId(AssetCategory.class.getName())
			).put(
				"externalReferenceCode",
				assetCategory.getExternalReferenceCode()
			).put(
				"icon", "categories"
			).put(
				"id", assetCategory.getCategoryId()
			).put(
				"name", assetCategory.getTitle(_themeDisplay.getLocale())
			).put(
				"nodePath",
				assetCategory.getPath(_themeDisplay.getLocale(), true)
			));
	}

	private JSONArray _getVocabulariesJSONArray() throws Exception {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		boolean allowedSelectVocabularies = ParamUtil.getBoolean(
			_httpServletRequest, "allowedSelectVocabularies");

		for (long vocabularyId : getVocabularyIds()) {
			jsonArray.put(
				JSONUtil.put(
					"children", _getCategoriesJSONArray(vocabularyId, 0)
				).put(
					"disabled", !allowedSelectVocabularies
				).put(
					"icon", "vocabulary"
				).put(
					"id", vocabularyId
				).put(
					"name", getVocabularyTitle(vocabularyId)
				).put(
					"vocabulary", true
				));
		}

		return jsonArray;
	}

	private boolean _isMultiSelection() {
		if (_infoItemItemSelectorCriterion.isMultiSelection() ||
			!ParamUtil.getBoolean(_httpServletRequest, "singleSelect", true)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SelectAssetCategoryInfoItemDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final InfoItemItemSelectorCriterion _infoItemItemSelectorCriterion;
	private final String _itemSelectedEventName;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private List<Long> _vocabularyIds;

}