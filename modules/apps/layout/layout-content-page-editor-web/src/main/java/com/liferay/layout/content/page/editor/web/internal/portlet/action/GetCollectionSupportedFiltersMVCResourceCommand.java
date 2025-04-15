/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.info.filter.InfoFilter;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverRegistry;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReferenceFactory;
import com.liferay.layout.list.retriever.ListObjectReferenceFactoryRegistry;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_collection_supported_filters"
	},
	service = MVCResourceCommand.class
)
public class GetCollectionSupportedFiltersMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String collections = ParamUtil.getString(
			resourceRequest, "collections");

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			_getSupportedFiltersJSONObject(
				_jsonFactory.createJSONArray(collections)));
	}

	private JSONObject _getSupportedFiltersJSONObject(
			JSONArray collectionsJSONArray)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (int i = 0; i < collectionsJSONArray.length(); i++) {
			JSONObject collectionJSONObject =
				collectionsJSONArray.getJSONObject(i);

			JSONObject layoutObjectReferenceJSONObject =
				collectionJSONObject.getJSONObject("layoutObjectReference");

			String type = layoutObjectReferenceJSONObject.getString("type");

			LayoutListRetriever<?, ListObjectReference> layoutListRetriever =
				(LayoutListRetriever<?, ListObjectReference>)
					_layoutListRetrieverRegistry.getLayoutListRetriever(type);

			if (layoutListRetriever == null) {
				continue;
			}

			ListObjectReferenceFactory<?> listObjectReferenceFactory =
				_listObjectReferenceFactoryRegistry.getListObjectReference(
					type);

			if (listObjectReferenceFactory == null) {
				continue;
			}

			jsonObject.put(
				collectionJSONObject.getString("collectionId"),
				JSONUtil.toJSONArray(
					layoutListRetriever.getSupportedInfoFilters(
						listObjectReferenceFactory.getListObjectReference(
							layoutObjectReferenceJSONObject)),
					InfoFilter::getFilterTypeName));
		}

		return jsonObject;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutListRetrieverRegistry _layoutListRetrieverRegistry;

	@Reference
	private ListObjectReferenceFactoryRegistry
		_listObjectReferenceFactoryRegistry;

}