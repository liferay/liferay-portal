/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.categorization.inputs.internal;

import com.liferay.asset.taglib.servlet.taglib.AssetTagsSelectorTag;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.info.constants.InfoItemScopeConstants;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemCategorizationProvider;
import com.liferay.info.item.provider.InfoItemScopeProvider;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
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
public class TagsInputFragmentRenderer extends BaseInputFragmentRenderer {

	@Override
	public String getIcon() {
		return "tag";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "tags");
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

			AssetTagsSelectorTag assetTagsSelectorTag =
				new AssetTagsSelectorTag();

			assetTagsSelectorTag.setClassName(className);
			assetTagsSelectorTag.setClassPK(
				getClassPK(className, httpServletRequest));

			InfoItemScopeProvider<Object> infoItemScopeProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemScopeProvider.class, className);

			if (Objects.equals(
					infoItemScopeProvider.getScope(),
					InfoItemScopeConstants.SCOPE_COMPANY)) {

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				assetTagsSelectorTag.setGroupIds(
					new long[] {themeDisplay.getCompanyGroupId()});
			}

			assetTagsSelectorTag.doTag(httpServletRequest, httpServletResponse);

			printWriter.write("</div>");
		}
		catch (Exception exception) {
			_log.error(
				"Unable to render categorization input fragment", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TagsInputFragmentRenderer.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

}