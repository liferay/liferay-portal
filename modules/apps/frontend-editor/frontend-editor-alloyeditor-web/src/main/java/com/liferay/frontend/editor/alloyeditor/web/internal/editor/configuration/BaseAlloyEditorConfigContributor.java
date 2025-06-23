/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.alloyeditor.web.internal.editor.configuration;

import com.liferay.frontend.editor.alloyeditor.web.internal.constants.AlloyEditorConstants;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.layout.item.selector.LayoutItemSelectorCriterion;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
public abstract class BaseAlloyEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		jsonObject.put(
			"allowedContent", Boolean.TRUE
		).put(
			"contentsLangDirection",
			HtmlUtil.escapeJS(
				getContentsLanguageDir(inputEditorTaglibAttributes))
		).put(
			"contentsLanguage",
			StringUtil.replace(
				getContentsLanguageId(inputEditorTaglibAttributes), "iw_",
				"he_")
		).put(
			"disableNativeSpellChecker", Boolean.FALSE
		).put(
			"extraPlugins",
			"addimages,ae_dragresize,ae_imagealignment,ae_placeholder," +
				"ae_selectionregion,ae_tableresize,ae_tabletools,ae_uicore"
		).put(
			"imageScaleResize", "scale"
		).put(
			"language",
			StringUtil.replace(getLanguageId(themeDisplay), "iw_", "he_")
		).put(
			"removePlugins",
			"autolink,contextmenu,elementspath,floatingspace,image2,link," +
				"liststyle,resize,table,tabletools,toolbar"
		).put(
			"skin", "moono-lisa"
		);

		String namespace = GetterUtil.getString(
			inputEditorTaglibAttributes.get(
				AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":namespace"));

		String name = GetterUtil.getString(
			inputEditorTaglibAttributes.get(
				AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":name"));

		name = namespace + name;

		jsonObject.put("srcNode", name);

		_populateFileBrowserURL(
			jsonObject, requestBackedPortletURLFactory,
			name + "selectDocument");
	}

	@Reference
	protected ItemSelector itemSelector;

	private void _populateFileBrowserURL(
		JSONObject jsonObject,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		String eventName) {

		ItemSelectorCriterion fileItemSelectorCriterion =
			new FileItemSelectorCriterion();

		fileItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		LayoutItemSelectorCriterion layoutItemSelectorCriterion =
			new LayoutItemSelectorCriterion();

		layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		jsonObject.put(
			"documentBrowseLinkUrl",
			String.valueOf(
				itemSelector.getItemSelectorURL(
					requestBackedPortletURLFactory, eventName,
					fileItemSelectorCriterion, layoutItemSelectorCriterion)));
	}

}