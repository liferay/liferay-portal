/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor.web.internal.editor.configuration;

import com.liferay.document.library.kernel.processor.AudioProcessorUtil;
import com.liferay.frontend.editor.ckeditor.web.internal.constants.CKEditorConstants;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {"editor.name=ckeditor", "editor.name=ckeditor_classic"},
	service = EditorConfigContributor.class
)
public class CKEditorConfigContributor extends BaseCKEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		super.populateConfigJSONObject(
			jsonObject, inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);

		jsonObject.put(
			"autoSaveTimeout", 3000
		).put(
			"closeNoticeTimeout", 8000
		).put(
			"entities", Boolean.FALSE
		);

		String extraPlugins =
			"addimages,autogrow,autolink,colordialog,filebrowser," +
				"itemselector,lfrpopup,media,stylescombo,tableselection," +
					"videoembed";

		boolean inlineEdit = GetterUtil.getBoolean(
			(String)inputEditorTaglibAttributes.get(
				CKEditorConstants.ATTRIBUTE_NAMESPACE + ":inlineEdit"));

		if (inlineEdit) {
			extraPlugins += ",ajaxsave,restore";
		}

		if (_isShowAICreator(inputEditorTaglibAttributes)) {
			extraPlugins += ",aicreator";
		}

		jsonObject.put(
			"applicationTitle",
			_language.get(themeDisplay.getLocale(), "rich-text-editor")
		).put(
			"extraPlugins", extraPlugins
		).put(
			"filebrowserWindowFeatures",
			"title=" + _language.get(themeDisplay.getLocale(), "browse")
		).put(
			"pasteFromWordRemoveFontStyles", Boolean.FALSE
		).put(
			"pasteFromWordRemoveStyles", Boolean.FALSE
		).put(
			"removePlugins", "elementspath"
		).put(
			"stylesSet", _getStyleFormatsJSONArray(themeDisplay.getLocale())
		).put(
			"title", false
		);

		JSONArray toolbarSimpleJSONArray = _getToolbarSimpleJSONArray(
			inputEditorTaglibAttributes);

		jsonObject.put(
			"toolbar_editInPlace", toolbarSimpleJSONArray
		).put(
			"toolbar_email", toolbarSimpleJSONArray
		).put(
			"toolbar_liferay", toolbarSimpleJSONArray
		).put(
			"toolbar_liferayArticle", toolbarSimpleJSONArray
		).put(
			"toolbar_phone", toolbarSimpleJSONArray
		).put(
			"toolbar_simple", toolbarSimpleJSONArray
		).put(
			"toolbar_tablet", toolbarSimpleJSONArray
		).put(
			"toolbar_text_advanced",
			_getToolbarTextAdvancedJSONArray(inputEditorTaglibAttributes)
		).put(
			"toolbar_text_simple",
			_getToolbarTextSimpleJSONArray(inputEditorTaglibAttributes)
		);
	}

	private JSONObject _getStyleFormatJSONObject(
		String styleFormatName, String element, String cssClass) {

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
			"name", styleFormatName
		);
	}

	private JSONArray _getStyleFormatsJSONArray(Locale locale) {
		return JSONUtil.putAll(
			_getStyleFormatJSONObject(
				_language.get(locale, "normal"), "p", null),
			_getStyleFormatJSONObject(
				_language.format(locale, "heading-x", "1"), "h1", null),
			_getStyleFormatJSONObject(
				_language.format(locale, "heading-x", "2"), "h2", null),
			_getStyleFormatJSONObject(
				_language.format(locale, "heading-x", "3"), "h3", null),
			_getStyleFormatJSONObject(
				_language.format(locale, "heading-x", "4"), "h4", null),
			_getStyleFormatJSONObject(
				_language.get(locale, "preformatted-text"), "pre", null),
			_getStyleFormatJSONObject(
				_language.get(locale, "cited-work"), "cite", null),
			_getStyleFormatJSONObject(
				_language.get(locale, "computer-code"), "code", null),
			_getStyleFormatJSONObject(
				_language.get(locale, "info-message"), "div",
				"overflow-auto portlet-msg-info"),
			_getStyleFormatJSONObject(
				_language.get(locale, "alert-message"), "div",
				"overflow-auto portlet-msg-alert"),
			_getStyleFormatJSONObject(
				_language.get(locale, "error-message"), "div",
				"overflow-auto portlet-msg-error"));
	}

	private JSONArray _getToolbarSimpleJSONArray(
		Map<String, Object> inputEditorTaglibAttributes) {

		return JSONUtil.putAll(
			toJSONArray("['Undo', 'Redo']"),
			toJSONArray("['Styles', 'Bold', 'Italic', 'Underline']"),
			toJSONArray("['NumberedList', 'BulletedList']"),
			toJSONArray("['Link', Unlink]"),
			toJSONArray("['Table', 'ImageSelector', 'VideoSelector']")
		).put(
			() -> {
				if (AudioProcessorUtil.isEnabled()) {
					return toJSONArray("['AudioSelector']");
				}

				return null;
			}
		).put(
			() -> {
				if (isShowSource(inputEditorTaglibAttributes)) {
					return toJSONArray("['Source', 'Expand']");
				}

				return null;
			}
		).put(
			() -> {
				if (_isShowAICreator(inputEditorTaglibAttributes)) {
					return toJSONArray("['AICreator']");
				}

				return null;
			}
		);
	}

	private JSONArray _getToolbarTextAdvancedJSONArray(
		Map<String, Object> inputEditorTaglibAttributes) {

		return JSONUtil.putAll(
			toJSONArray("['Undo', 'Redo']"), toJSONArray("['Styles']"),
			toJSONArray("['FontColor', 'BGColor']"),
			toJSONArray("['Bold', 'Italic', 'Underline', 'Strikethrough']"),
			toJSONArray("['RemoveFormat']"),
			toJSONArray("['NumberedList', 'BulletedList']"),
			toJSONArray("['IncreaseIndent', 'DecreaseIndent']"),
			toJSONArray("['IncreaseIndent', 'DecreaseIndent']"),
			toJSONArray("['Link', Unlink]")
		).put(
			() -> {
				if (isShowSource(inputEditorTaglibAttributes)) {
					return toJSONArray("['Source', 'Expand']");
				}

				return null;
			}
		).put(
			() -> {
				if (_isShowAICreator(inputEditorTaglibAttributes)) {
					return toJSONArray("['AICreator']");
				}

				return null;
			}
		);
	}

	private JSONArray _getToolbarTextSimpleJSONArray(
		Map<String, Object> inputEditorTaglibAttributes) {

		return JSONUtil.putAll(
			toJSONArray("['Undo', 'Redo']"),
			toJSONArray("['Styles', 'Bold', 'Italic', 'Underline']"),
			toJSONArray("['NumberedList', 'BulletedList']"),
			toJSONArray("['Link', Unlink]")
		).put(
			() -> {
				if (isShowSource(inputEditorTaglibAttributes)) {
					return toJSONArray("['Source', 'Expand']");
				}

				return null;
			}
		).put(
			() -> {
				if (_isShowAICreator(inputEditorTaglibAttributes)) {
					return toJSONArray("['AICreator']");
				}

				return null;
			}
		);
	}

	private boolean _isShowAICreator(
		Map<String, Object> inputEditorTaglibAttributes) {

		return GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				CKEditorConstants.ATTRIBUTE_NAMESPACE + ":showAICreator"));
	}

	@Reference
	private Language _language;

}