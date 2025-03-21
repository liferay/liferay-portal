/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.collection.filter.FragmentCollectionFilter;
import com.liferay.fragment.collection.filter.FragmentCollectionFilterRegistry;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pablo Molina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_collection_filters"
	},
	service = MVCResourceCommand.class
)
public class GetCollectionFiltersMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONObject fragmentCollectionFiltersJSONObject =
			_jsonFactory.createJSONObject();

		for (FragmentCollectionFilter fragmentCollectionFilter :
				_fragmentCollectionFilterRegistry.
					getFragmentCollectionFilters()) {

			fragmentCollectionFiltersJSONObject.put(
				fragmentCollectionFilter.getFilterKey(),
				JSONUtil.put(
					"configuration",
					_getConfigurationJSONObject(
						fragmentCollectionFilter.getConfiguration())
				).put(
					"key", fragmentCollectionFilter.getFilterKey()
				).put(
					"label",
					fragmentCollectionFilter.getLabel(themeDisplay.getLocale())
				));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			fragmentCollectionFiltersJSONObject);
	}

	private JSONObject _getConfigurationJSONObject(String configuration) {
		try {
			return _jsonFactory.createJSONObject(configuration);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return _jsonFactory.createJSONObject();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetCollectionFiltersMVCResourceCommand.class);

	@Reference
	private FragmentCollectionFilterRegistry _fragmentCollectionFilterRegistry;

	@Reference
	private JSONFactory _jsonFactory;

}