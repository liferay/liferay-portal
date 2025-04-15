/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

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
		"mvc.command.name=/layout_content_page_editor/get_fragment_entry_input_field_types"
	},
	service = MVCResourceCommand.class
)
public class GetFragmentEntryInputFieldTypesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String fragmentEntryKey = ParamUtil.getString(
			resourceRequest, "fragmentEntryKey");

		String typeOptions = _getFragmentTypeOptions(
			fragmentEntryKey,
			ParamUtil.getLong(
				resourceRequest, "groupId", themeDisplay.getScopeGroupId()));

		if (Validator.isNull(typeOptions)) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONArray());

			return;
		}

		JSONObject typeOptionsJSONObject = _jsonFactory.createJSONObject(
			typeOptions);

		JSONArray fieldTypesJSONArray = typeOptionsJSONObject.getJSONArray(
			"fieldTypes");

		if ((fieldTypesJSONArray == null) ||
			(fieldTypesJSONArray.length() == 0)) {

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONArray());

			return;
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, fieldTypesJSONArray);
	}

	private String _getFragmentTypeOptions(
		String fragmentEntryKey, long groupId) {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				groupId, fragmentEntryKey);

		if (fragmentEntry == null) {
			fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					fragmentEntryKey);
		}

		if (fragmentEntry != null) {
			return fragmentEntry.getTypeOptions();
		}

		FragmentRenderer fragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(fragmentEntryKey);

		if (fragmentRenderer != null) {
			return fragmentRenderer.getTypeOptions();
		}

		return StringPool.BLANK;
	}

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private JSONFactory _jsonFactory;

}