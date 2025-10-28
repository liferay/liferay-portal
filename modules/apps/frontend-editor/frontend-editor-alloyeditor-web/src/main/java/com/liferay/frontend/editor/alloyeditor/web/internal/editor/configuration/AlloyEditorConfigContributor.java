/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.alloyeditor.web.internal.editor.configuration;

import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
@Component(
	property = "editor.name=alloyeditor",
	service = EditorConfigContributor.class
)
public class AlloyEditorConfigContributor
	extends BaseAlloyEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		super.populateConfigJSONObject(
			jsonObject, inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);

		jsonObject.put("entities", Boolean.FALSE);

		String extraPlugins = jsonObject.getString("extraPlugins");

		if (Validator.isNotNull(extraPlugins)) {
			extraPlugins += ",itemselector,media,videoembed";
		}
		else {
			extraPlugins = "itemselector,media,videoembed";
		}

		jsonObject.put(
			"extraPlugins", extraPlugins
		).put(
			"toolbars", getToolbarsJSONObject(themeDisplay.getLocale())
		);
	}

	protected JSONObject getStyleFormatJSONObject(
		String styleFormatName, String element, String cssClass, int type) {

		return JSONUtil.put(
			"name", styleFormatName
		).put(
			"style", getStyleJSONObject(element, cssClass, type)
		);
	}

	protected JSONArray getStyleFormatsJSONArray(Locale locale) {
		return JSONUtil.putAll(
			getStyleFormatJSONObject(
				_language.get(locale, "normal"), "p", null,
				_CKEDITOR_STYLE_BLOCK),
			getStyleFormatJSONObject(
				_language.format(locale, "heading-x", "1"), "h1", null,
				_CKEDITOR_STYLE_BLOCK),
			getStyleFormatJSONObject(
				_language.format(locale, "heading-x", "2"), "h2", null,
				_CKEDITOR_STYLE_BLOCK),
			getStyleFormatJSONObject(
				_language.format(locale, "heading-x", "3"), "h3", null,
				_CKEDITOR_STYLE_BLOCK),
			getStyleFormatJSONObject(
				_language.format(locale, "heading-x", "4"), "h4", null,
				_CKEDITOR_STYLE_BLOCK),
			getStyleFormatJSONObject(
				_language.get(locale, "preformatted-text"), "pre", null,
				_CKEDITOR_STYLE_BLOCK),
			getStyleFormatJSONObject(
				_language.get(locale, "cited-work"), "cite", null,
				_CKEDITOR_STYLE_INLINE),
			getStyleFormatJSONObject(
				_language.get(locale, "computer-code"), "code", null,
				_CKEDITOR_STYLE_INLINE),
			getStyleFormatJSONObject(
				_language.get(locale, "info-message"), "div",
				"overflow-auto portlet-msg-info", _CKEDITOR_STYLE_BLOCK),
			getStyleFormatJSONObject(
				_language.get(locale, "alert-message"), "div",
				"overflow-auto portlet-msg-alert", _CKEDITOR_STYLE_BLOCK),
			getStyleFormatJSONObject(
				_language.get(locale, "error-message"), "div",
				"overflow-auto portlet-msg-error", _CKEDITOR_STYLE_BLOCK));
	}

	protected JSONObject getStyleFormatsJSONObject(Locale locale) {
		return JSONUtil.put(
			"cfg", JSONUtil.put("styles", getStyleFormatsJSONArray(locale))
		).put(
			"name", "styles"
		);
	}

	protected JSONObject getStyleJSONObject(
		String element, String cssClass, int type) {

		return JSONUtil.put(
			"attributes",
			() -> {
				if (Validator.isNotNull(cssClass)) {
					return JSONUtil.put("class", cssClass);
				}

				return null;
			}
		).put(
			"element", element
		).put(
			"type", type
		);
	}

	protected JSONObject getToolbarsAddJSONObject() {
		return JSONUtil.put(
			"buttons", toJSONArray("['image', 'video', 'table', 'hline']")
		).put(
			"tabIndex", 2
		);
	}

	protected JSONObject getToolbarsJSONObject(Locale locale) {
		return JSONUtil.put(
			"add", getToolbarsAddJSONObject()
		).put(
			"styles", getToolbarsStylesJSONObject(locale)
		);
	}

	protected JSONObject getToolbarsStylesJSONObject(Locale locale) {
		return JSONUtil.put(
			"selections", getToolbarsStylesSelectionsJSONArray(locale)
		).put(
			"tabIndex", 1
		);
	}

	protected JSONArray getToolbarsStylesSelectionsJSONArray(Locale locale) {
		return JSONUtil.putAll(
			_getToolbarsStylesSelectionsEmbedURLJSONObject(),
			getToolbarsStylesSelectionsLinkJSONObject(),
			_getToolbarsStylesSelectionsImageJSONObject(),
			getToolbarsStylesSelectionsTextJSONObject(locale));
	}

	protected JSONObject getToolbarsStylesSelectionsLinkJSONObject() {
		return JSONUtil.put(
			"buttons", toJSONArray("['linkEditBrowse']")
		).put(
			"name", "link"
		).put(
			"test", "AlloyEditor.SelectionTest.link"
		);
	}

	protected JSONObject getToolbarsStylesSelectionsTextJSONObject(
		Locale locale) {

		return JSONUtil.put(
			"buttons",
			JSONUtil.putAll(
				getStyleFormatsJSONObject(locale), "bold", "italic",
				"underline", "ol", "ul", "linkBrowse")
		).put(
			"name", "text"
		).put(
			"test", "AlloyEditor.SelectionTest.text"
		);
	}

	private JSONObject _getToolbarsStylesSelectionsEmbedURLJSONObject() {
		return JSONUtil.put(
			"buttons", toJSONArray("['imageLeft', 'imageCenter', 'imageRight']")
		).put(
			"name", "embedurl"
		).put(
			"test", "AlloyEditor.SelectionTest.embedUrl"
		);
	}

	private JSONObject _getToolbarsStylesSelectionsImageJSONObject() {
		return JSONUtil.put(
			"buttons",
			toJSONArray(
				"['imageLeft', 'imageCenter', 'imageRight', 'linkBrowse', " +
					"'imageAlt']")
		).put(
			"name", "image"
		).put(
			"setPosition", "AlloyEditor.SelectionSetPosition.image"
		).put(
			"test", "AlloyEditor.SelectionTest.image"
		);
	}

	private static final int _CKEDITOR_STYLE_BLOCK = 1;

	private static final int _CKEDITOR_STYLE_INLINE = 2;

	@Reference
	private Language _language;

}