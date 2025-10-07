/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.categorization.inputs.internal;

import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.taglib.servlet.taglib.AssetCategoriesSelectorTag;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.constants.InfoItemScopeConstants;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemCategorizationProvider;
import com.liferay.info.item.provider.InfoItemScopeProvider;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = FragmentRenderer.class)
public class CategoriesInputFragmentRenderer extends BaseInputFragmentRenderer {

	@Override
	public JSONObject getConfigurationJSONObject(
		FragmentRendererContext fragmentRendererContext) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					getClass(),
					"/com/liferay/fragment/renderer/categorization/inputs" +
						"/internal/dependencies/configuration.json"));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject,
				ResourceBundleUtil.getBundle("content.Language", getClass()));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	@Override
	public String getIcon() {
		return "categories";
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, "categories");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			FormStyledLayoutStructureItem formStyledLayoutStructureItem =
				getFormStyledLayoutStructureItem(
					fragmentRendererContext.getFragmentEntryLink(),
					httpServletRequest);

			if (formStyledLayoutStructureItem == null) {
				return;
			}

			String className = formStyledLayoutStructureItem.getClassName();

			if (Validator.isNull(className)) {
				return;
			}

			InfoItemCategorizationProvider<Object>
				infoItemCategorizationProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemCategorizationProvider.class, className);

			if (infoItemCategorizationProvider == null) {
				return;
			}

			PrintWriter printWriter = httpServletResponse.getWriter();

			if (!infoItemCategorizationProvider.supportsCategorization()) {
				writeDisabledCategorizationAlert(
					fragmentRendererContext, httpServletRequest,
					httpServletResponse, printWriter);

				return;
			}

			printWriter.write("<div");

			if (fragmentRendererContext.isEditMode()) {
				printWriter.write(" inert");
			}

			printWriter.write(StringPool.GREATER_THAN);

			AssetCategoriesSelectorTag assetCategoriesSelectorTag =
				new AssetCategoriesSelectorTag();

			assetCategoriesSelectorTag.setClassName(className);
			assetCategoriesSelectorTag.setClassPK(
				getClassPK(className, httpServletRequest));
			assetCategoriesSelectorTag.setClassTypePK(
				formStyledLayoutStructureItem.getClassTypeId());

			InfoItemScopeProvider<Object> infoItemScopeProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemScopeProvider.class, className);

			if (Objects.equals(
					infoItemScopeProvider.getScope(),
					InfoItemScopeConstants.SCOPE_COMPANY)) {

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				assetCategoriesSelectorTag.setGroupIds(
					new long[] {themeDisplay.getCompanyGroupId()});
			}

			assetCategoriesSelectorTag.setShowLabel(false);
			assetCategoriesSelectorTag.setVisibilityTypes(
				_getVisibilityTypes(
					fragmentRendererContext.getFragmentEntryLink()));

			assetCategoriesSelectorTag.doTag(
				httpServletRequest, httpServletResponse);

			printWriter.write("</div>");
		}
		catch (Exception exception) {
			_log.error(
				"Unable to render categorization input fragment", exception);
		}
	}

	private int[] _getVisibilityTypes(FragmentEntryLink fragmentEntryLink) {
		String vocabularyType = GetterUtil.getString(
			_fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getConfigurationJSONObject(),
				fragmentEntryLink.getEditableValuesJSONObject(),
				LocaleUtil.getMostRelevantLocale(), "vocabularyType"));

		if (Objects.equals(vocabularyType, "all")) {
			return AssetVocabularyConstants.VISIBILITY_TYPES;
		}

		if (Objects.equals(vocabularyType, "internal")) {
			return new int[] {
				AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL
			};
		}

		return new int[] {AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CategoriesInputFragmentRenderer.class);

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

}