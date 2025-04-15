/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.MappingContentUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_mapping_fields"
	},
	service = MVCResourceCommand.class
)
public class GetMappingFieldsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String classTypeId = ParamUtil.getString(
			resourceRequest, "classTypeId");
		long classNameId = ParamUtil.getLong(resourceRequest, "classNameId");

		try {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_getMappingFieldsJSONArray(
					classNameId, classTypeId, themeDisplay.getScopeGroupId(),
					themeDisplay.getLocale()));
		}
		catch (Exception exception) {
			_log.error("Unable to get mapping fields", exception);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					_language.get(
						themeDisplay.getRequest(),
						"an-unexpected-error-occurred")));
		}
	}

	private JSONArray _getMappingFieldsJSONArray(
			long classNameId, String classTypeId, long groupId, Locale locale)
		throws Exception {

		return MappingContentUtil.getMappingFieldsJSONArray(
			classTypeId, groupId, _infoItemServiceRegistry,
			_portal.fetchClassName(classNameId), locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetMappingFieldsMVCResourceCommand.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}