/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.editor.configuration;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.document.library.kernel.processor.AudioProcessorUtil;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletMode;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
	service = EditorConfigContributor.class
)
public class CommerceCKEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		if (!_isAICreatorChatGPTGroupEnabled(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId())) {

			return;
		}

		EditorConfigContributor ckEditorConfigContributor =
			_ckEditorConfigContributorSnapshot.get();

		ckEditorConfigContributor.populateConfigJSONObject(
			jsonObject, inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);
		jsonObject.put(
			"applicationTitle",
			_language.get(themeDisplay.getLocale(), "rich-text-editor")
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
		jsonObject.put(
			"aiCreatorOpenAIURL",
			() -> PortletURLBuilder.create(
				requestBackedPortletURLFactory.createControlPanelRenderURL(
					"com_liferay_ai_creator_openai_web_internal_portlet_" +
						"AICreatorOpenAIPortlet",
					themeDisplay.getScopeGroup(),
					themeDisplay.getRefererGroupId(), 0)
			).setMVCPath(
				"/view.jsp"
			).setPortletMode(
				PortletMode.VIEW
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"aiCreatorPortletNamespace",
			() -> _portal.getPortletNamespace(
				"com_liferay_ai_creator_openai_web_internal_portlet_" +
					"AICreatorOpenAIPortlet")
		).put(
			"extraPlugins",
			() -> {
				String extraPlugins =
					"addimages,autogrow,autolink,colordialog,filebrowser," +
					"itemselector,lfrpopup,media,stylescombo,videoembed";

				return extraPlugins.concat(",aicreator");
			}
		).put(
			"isAICreatorOpenAIAPIKey",
			() -> {
				try {
					if (Validator.isNotNull(
							_aiCreatorOpenAIConfigurationManager.
								getAICreatorOpenAIGroupAPIKey(
									themeDisplay.getCompanyId(),
									themeDisplay.getScopeGroupId()))) {

						return true;
					}
				}
				catch (ConfigurationException configurationException) {
					if (_log.isDebugEnabled()) {
						_log.debug(configurationException);
					}
				}

				return false;
			}
		);

		_putAICreator(
			jsonObject, "toolbar_simple", "toolbar_text_advanced",
			"toolbar_text_simple");
	}

	private boolean _isAICreatorChatGPTGroupEnabled(
		long companyId, long groupId) {

		try {
			if (_aiCreatorOpenAIConfigurationManager.
					isAICreatorChatGPTGroupEnabled(companyId, groupId)) {

				return true;
			}
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return false;
	}

	private void _putAICreator(JSONObject jsonObject, String... keys) {
		for (String key : keys) {
			JSONArray jsonArray = (JSONArray)jsonObject.get(key);

			jsonArray.put(JSONUtil.put("AICreator"));
		}
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

	private boolean isShowSource(
		Map<String, Object> inputEditorTaglibAttributes) {

		return GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				ATTRIBUTE_NAMESPACE + ":showSource"),
			true);
	}

	private boolean _isShowAICreator(
		Map<String, Object> inputEditorTaglibAttributes) {

		return GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				ATTRIBUTE_NAMESPACE + ":showAICreator"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCKEditorConfigContributor.class);

	@Reference
	private AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;

	private final Snapshot<EditorConfigContributor>
		_ckEditorConfigContributorSnapshot = new Snapshot<>(
			CommerceCKEditorConfigContributor.class,
			EditorConfigContributor.class, "(editor.name=ckeditor)");

	public static final String ATTRIBUTE_NAMESPACE = "liferay-ui:input-editor";

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}