/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.info.list.renderer.InfoListRenderer;
import com.liferay.info.list.renderer.InfoListRendererRegistry;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_available_list_renderers"
	},
	service = MVCResourceCommand.class
)
public class GetAvailableListRenderersMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<InfoListRenderer<?>> infoListRenderers =
			_infoListRendererRegistry.getInfoListRenderers(
				_infoSearchClassMapperRegistry.getClassName(
					ParamUtil.getString(resourceRequest, "className")));

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (InfoListRenderer<?> infoListRenderer : infoListRenderers) {
			jsonArray.put(
				JSONUtil.put(
					"label", infoListRenderer.getLabel(themeDisplay.getLocale())
				).put(
					"value", infoListRenderer.getKey()
				));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonArray);
	}

	@Reference
	private InfoListRendererRegistry _infoListRendererRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private JSONFactory _jsonFactory;

}