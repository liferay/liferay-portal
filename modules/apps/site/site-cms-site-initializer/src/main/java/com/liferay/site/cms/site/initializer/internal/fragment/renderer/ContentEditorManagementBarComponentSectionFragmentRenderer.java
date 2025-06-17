/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sandro Chinea
 */
@Component(service = FragmentRenderer.class)
public class ContentEditorManagementBarComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "content-editor";
	}

	@Override
	protected String getLabelKey() {
		return "content-editor-management-bar";
	}

	@Override
	protected String getModuleName() {
		return "ContentEditorManagementBar";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"backURL", ParamUtil.getString(httpServletRequest, "redirect")
		).put(
			"headerTitle",
			() -> {
				LayoutDisplayPageObjectProvider<?>
					layoutDisplayPageObjectProvider =
						(LayoutDisplayPageObjectProvider<?>)
							httpServletRequest.getAttribute(
								LayoutDisplayPageWebKeys.
									LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

				if (layoutDisplayPageObjectProvider == null) {
					return StringPool.BLANK;
				}

				Object displayObject =
					layoutDisplayPageObjectProvider.getDisplayObject();

				if (!(displayObject instanceof ObjectEntry)) {
					return StringPool.BLANK;
				}

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				ObjectEntry objectEntry = (ObjectEntry)displayObject;

				if (Objects.equals(
						WorkflowConstants.STATUS_APPROVED,
						objectEntry.getStatus())) {

					return language.format(
						themeDisplay.getLocale(), "edit-x",
						layoutDisplayPageObjectProvider.getTitle(
							themeDisplay.getLocale()));
				}

				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						objectEntry.getObjectDefinitionId());

				if (objectDefinition == null) {
					return StringPool.BLANK;
				}

				return language.format(
					themeDisplay.getLocale(), "new-x",
					objectDefinition.getLabel(themeDisplay.getLocale()));
			}
		).build();
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}