/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.frontend.taglib.react.servlet.taglib.ComponentTag;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.layout.page.template.info.item.capability.EditPageInfoItemCapability;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = FragmentRenderer.class)
public class LocalizationSelectFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "INPUTS";
	}

	@Override
	public String getConfiguration(
		FragmentRendererContext fragmentRendererContext) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", getClass());

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					LocalizationSelectFragmentRenderer.class.
						getResourceAsStream(
							"dependencies/configuration.json")));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject, resourceBundle);
		}
		catch (IOException | JSONException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public String getIcon() {
		return "automatic-translate";
	}

	@Override
	public String getKey() {
		return "localization-select";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "localization-select");
	}

	@Override
	public int getType() {
		return FragmentConstants.TYPE_INPUT;
	}

	@Override
	public String getTypeOptions() {
		return JSONUtil.put(
			"fieldTypes", JSONUtil.putAll("localizationSelect")
		).toString();
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (InfoItemClassDetails infoItemClassDetails :
				_infoItemServiceRegistry.getInfoItemClassDetails(
					EditPageInfoItemCapability.KEY)) {

			InfoPermissionProvider infoPermissionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoPermissionProvider.class,
					infoItemClassDetails.getClassName());

			if ((infoPermissionProvider == null) ||
				infoPermissionProvider.hasViewPermission(
					themeDisplay.getPermissionChecker())) {

				return true;
			}
		}

		return false;
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write("<div><span aria-hidden=\"true\" class=\"");
			printWriter.write("loading-animation\"></span>");

			ComponentTag componentTag = new ComponentTag();

			componentTag.setModule(
				"{LocalizationSelect} from fragment-impl/api");
			componentTag.setPageContext(
				PageContextFactoryUtil.create(
					httpServletRequest, httpServletResponse));
			componentTag.setServletContext(_servletContext);

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			componentTag.setProps(
				HashMapBuilder.<String, Object>put(
					"defaultLanguageId",
					LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale())
				).put(
					"editMode", fragmentRendererContext.isEditMode()
				).put(
					"hideLanguageLabel",
					GetterUtil.getBoolean(
						_fragmentEntryConfigurationParser.getFieldValue(
							fragmentEntryLink.getConfiguration(),
							fragmentEntryLink.getEditableValues(),
							LocaleUtil.getMostRelevantLocale(),
							"hideLanguageLabel"))
				).put(
					"locales",
					JSONUtil.toJSONArray(
						_language.getCompanyAvailableLocales(
							themeDisplay.getCompanyId()),
						locale -> {
							String w3cLanguageId = LocaleUtil.toW3cLanguageId(
								locale);

							return JSONUtil.put(
								"id", LocaleUtil.toLanguageId(locale)
							).put(
								"label", w3cLanguageId
							).put(
								"name", locale.getDisplayName()
							).put(
								"symbol", StringUtil.toLowerCase(w3cLanguageId)
							);
						})
				).put(
					"size",
					GetterUtil.getString(
						_fragmentEntryConfigurationParser.getFieldValue(
							fragmentEntryLink.getConfiguration(),
							fragmentEntryLink.getEditableValues(),
							LocaleUtil.getMostRelevantLocale(), "size"),
						"normal")
				).build());

			componentTag.doStartTag();

			componentTag.doEndTag();

			printWriter.write("</div>");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LocalizationSelectFragmentRenderer.class);

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.fragment.impl)")
	private ServletContext _servletContext;

}