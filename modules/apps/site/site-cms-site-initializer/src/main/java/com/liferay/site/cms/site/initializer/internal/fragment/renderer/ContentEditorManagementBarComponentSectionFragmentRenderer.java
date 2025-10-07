/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.InfoItemUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (Objects.equals(layoutMode, Constants.READ)) {
			return;
		}

		super.render(
			fragmentRendererContext, httpServletRequest, httpServletResponse);
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

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		return HashMapBuilder.<String, Object>put(
			"backURL", ParamUtil.getString(httpServletRequest, "redirect")
		).put(
			"hasWorkflow",
			() -> {
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

				long assetLibraryId = InfoItemUtil.getGroupId(
					httpServletRequest);

				ObjectEntry objectEntry = (ObjectEntry)displayObject;

				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						objectEntry.getObjectDefinitionId());

				return _workflowDefinitionLinkLocalService.
					hasWorkflowDefinitionLink(
						themeDisplay.getCompanyId(), assetLibraryId,
						objectDefinition.getClassName());
			}
		).put(
			"headerTitle",
			() -> {
				if (layoutDisplayPageObjectProvider == null) {
					return StringPool.BLANK;
				}

				Object displayObject =
					layoutDisplayPageObjectProvider.getDisplayObject();

				if (!(displayObject instanceof ObjectEntry)) {
					return StringPool.BLANK;
				}

				ObjectEntry objectEntry = (ObjectEntry)displayObject;

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				String title = _getTitle(
					layoutDisplayPageObjectProvider, objectEntry, themeDisplay);

				Layout layout = themeDisplay.getLayout();

				LayoutPageTemplateEntry layoutPageTemplateEntry =
					_layoutPageTemplateEntryLocalService.
						fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

				String layoutPageTemplateEntryKey =
					layoutPageTemplateEntry.getLayoutPageTemplateEntryKey();

				if (layoutPageTemplateEntryKey.startsWith(
						"LFR_CMS_TRANSLATION_")) {

					return language.format(
						themeDisplay.getLocale(), "translate-x", title);
				}

				if (objectEntry.getVersion() > 0) {
					return language.format(
						themeDisplay.getLocale(), "edit-x", title);
				}

				return language.format(
					themeDisplay.getLocale(), "new-x", title);
			}
		).build();
	}

	private String _getTitle(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider,
		ObjectEntry objectEntry, ThemeDisplay themeDisplay) {

		String title = layoutDisplayPageObjectProvider.getTitle(
			themeDisplay.getLocale());

		if (Validator.isNull(title)) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectEntry.getObjectDefinitionId());

			return objectDefinition.getLabel(themeDisplay.getLocale());
		}

		return title;
	}

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}