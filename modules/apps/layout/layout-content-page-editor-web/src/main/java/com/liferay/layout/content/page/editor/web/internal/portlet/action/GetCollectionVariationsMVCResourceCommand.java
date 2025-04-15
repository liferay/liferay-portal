/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalService;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Comparator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_collection_variations"
	},
	service = MVCResourceCommand.class
)
public class GetCollectionVariationsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			_getCollectionVariationsJSONArray(resourceRequest));
	}

	private JSONArray _getCollectionVariationsJSONArray(
			ResourceRequest resourceRequest)
		throws Exception {

		long assetListEntryId = ParamUtil.getLong(resourceRequest, "classPK");

		if (assetListEntryId <= 0) {
			return _jsonFactory.createJSONArray();
		}

		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRels(
					assetListEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		if (assetListEntrySegmentsEntryRels.size() < 2) {
			return _jsonFactory.createJSONArray();
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		assetListEntrySegmentsEntryRels = ListUtil.sort(
			assetListEntrySegmentsEntryRels,
			Comparator.comparingInt(
				AssetListEntrySegmentsEntryRel::getPriority));

		for (AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel :
				assetListEntrySegmentsEntryRels) {

			if (assetListEntrySegmentsEntryRel.getSegmentsEntryId() ==
					SegmentsEntryConstants.ID_DEFAULT) {

				jsonArray.put(
					SegmentsEntryConstants.getDefaultSegmentsEntryName(
						themeDisplay.getLocale()));

				continue;
			}

			SegmentsEntry segmentsEntry =
				_segmentsEntryLocalService.getSegmentsEntry(
					assetListEntrySegmentsEntryRel.getSegmentsEntryId());

			jsonArray.put(segmentsEntry.getName(themeDisplay.getLocale()));
		}

		return jsonArray;
	}

	@Reference
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

}