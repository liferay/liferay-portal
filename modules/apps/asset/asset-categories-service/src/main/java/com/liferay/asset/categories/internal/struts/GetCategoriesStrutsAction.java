/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.struts;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 */
@Component(
	property = "path=/asset/get_categories", service = StrutsAction.class
)
public class GetCategoriesStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<AssetCategory> assetCategories = getCategories(httpServletRequest);

		for (AssetCategory assetCategory : assetCategories) {
			List<AssetCategory> childAssetCategories =
				_assetCategoryService.getChildCategories(
					assetCategory.getCategoryId());

			jsonArray.put(
				JSONUtil.put(
					"categoryId", assetCategory.getCategoryId()
				).put(
					"childrenCount", childAssetCategories.size()
				).put(
					"hasChildren", !childAssetCategories.isEmpty()
				).put(
					"name", assetCategory.getName()
				).put(
					"parentCategoryId", assetCategory.getParentCategoryId()
				).put(
					"titleCurrentValue", assetCategory.getTitleCurrentValue()
				));
		}

		ServletResponseUtil.write(httpServletResponse, jsonArray.toString());

		return null;
	}

	protected List<AssetCategory> getCategories(
			HttpServletRequest httpServletRequest)
		throws Exception {

		List<AssetCategory> assetCategories = Collections.emptyList();

		long scopeGroupId = ParamUtil.getLong(
			httpServletRequest, "scopeGroupId");
		long categoryId = ParamUtil.getLong(httpServletRequest, "categoryId");
		long vocabularyId = ParamUtil.getLong(
			httpServletRequest, "vocabularyId");
		int start = ParamUtil.getInteger(
			httpServletRequest, "start", QueryUtil.ALL_POS);
		int end = ParamUtil.getInteger(
			httpServletRequest, "end", QueryUtil.ALL_POS);

		if (categoryId > 0) {
			if (scopeGroupId > 0) {
				assetCategories = _assetCategoryService.getVocabularyCategories(
					scopeGroupId, categoryId, vocabularyId, start, end, null);
			}
			else {
				assetCategories = _assetCategoryService.getChildCategories(
					categoryId, start, end, null);
			}
		}
		else if (vocabularyId > 0) {
			long parentCategoryId = ParamUtil.getLong(
				httpServletRequest, "parentCategoryId",
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

			if (scopeGroupId > 0) {
				assetCategories = _assetCategoryService.getVocabularyCategories(
					scopeGroupId, parentCategoryId, vocabularyId, start, end,
					null);
			}
			else {
				assetCategories = _assetCategoryService.getVocabularyCategories(
					parentCategoryId, vocabularyId, start, end, null);
			}
		}

		return assetCategories;
	}

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private JSONFactory _jsonFactory;

}