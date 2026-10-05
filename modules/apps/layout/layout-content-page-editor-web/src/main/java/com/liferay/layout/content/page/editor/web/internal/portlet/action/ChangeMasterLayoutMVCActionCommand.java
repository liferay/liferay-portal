/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.StyleBookEntryUtil;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;
import com.liferay.style.book.util.StyleBookUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/change_master_layout"
	},
	service = MVCActionCommand.class
)
public class ChangeMasterLayoutMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String masterLayoutPageTemplateEntryERC = ParamUtil.getString(
			actionRequest, "masterLayoutPageTemplateEntryERC");
		String masterLayoutPageTemplateEntryScopeERC = ParamUtil.getString(
			actionRequest, "masterLayoutPageTemplateEntryScopeERC");

		Layout layout = _layoutLocalService.fetchLayout(themeDisplay.getPlid());

		LayoutPermissionUtil.checkLayoutRestrictedUpdatePermission(
			themeDisplay.getPermissionChecker(), layout);

		Layout updatedLayout =
			_layoutLocalService.updateMasterLayoutPageTemplateEntry(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), masterLayoutPageTemplateEntryERC,
				masterLayoutPageTemplateEntryScopeERC);

		if (layout.isDraftLayout()) {
			UnicodeProperties layoutTypeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			layoutTypeSettingsUnicodeProperties.put(
				LayoutTypeSettingsConstants.KEY_DESIGN_CONFIGURATION_MODIFIED,
				Boolean.TRUE.toString());

			updatedLayout = _layoutLocalService.updateTypeSettings(
				updatedLayout, layoutTypeSettingsUnicodeProperties.toString());
		}

		actionRequest.setAttribute(WebKeys.LAYOUT, updatedLayout);

		if (Validator.isNull(masterLayoutPageTemplateEntryERC)) {
			return JSONUtil.put(
				"styleBookEntryERC", _getStyleBookEntryERC(updatedLayout)
			).put(
				"styleBookEntryScopeERC",
				GetterUtil.getString(updatedLayout.getStyleBookEntryScopeERC())
			).put(
				"styleBooks",
				_getStyleBooksJSONArray(updatedLayout, themeDisplay)
			);
		}

		Layout masterLayout = _layoutLocalService.getLayout(
			updatedLayout.getMasterLayoutPlid());

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				masterLayout.getGroupId(), masterLayout.getPlid(),
				SegmentsExperienceConstants.KEY_DEFAULT);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				masterLayout.getGroupId(), masterLayout.getPlid());

		JSONObject fragmentEntryLinksJSONObject =
			_jsonFactory.createJSONObject();

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			JSONObject fragmentEntryLinkJSONObject =
				_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
					fragmentEntryLink,
					_portal.getHttpServletRequest(actionRequest),
					_portal.getHttpServletResponse(actionResponse),
					layoutStructure);

			fragmentEntryLinkJSONObject.put("masterLayout", Boolean.TRUE);

			fragmentEntryLinksJSONObject.put(
				String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
				fragmentEntryLinkJSONObject);
		}

		return JSONUtil.put(
			"fragmentEntryLinks", fragmentEntryLinksJSONObject
		).put(
			"masterLayoutData", layoutStructure.toJSONObject()
		).put(
			"styleBookEntryERC", _getStyleBookEntryERC(updatedLayout)
		).put(
			"styleBookEntryScopeERC",
			GetterUtil.getString(updatedLayout.getStyleBookEntryScopeERC())
		).put(
			"styleBooks", _getStyleBooksJSONArray(updatedLayout, themeDisplay)
		);
	}

	@Override
	protected boolean isLayoutLockRequired() {
		return false;
	}

	private String _getStyleBookEntryERC(Layout layout) {
		StyleBookEntry styleBookEntry =
			DefaultStyleBookEntryUtil.getDefaultStyleBookEntry(layout);

		if (styleBookEntry != null) {
			return styleBookEntry.getExternalReferenceCode();
		}

		return StringPool.BLANK;
	}

	private JSONArray _getStyleBooksJSONArray(
			Layout layout, ThemeDisplay themeDisplay)
		throws Exception {

		JSONArray styleBooksJSONArray = _jsonFactory.createJSONArray();

		FrontendTokenDefinition frontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(layout);

		if (frontendTokenDefinition == null) {
			return styleBooksJSONArray;
		}

		StyleBookEntry defaultStyleBookEntry =
			DefaultStyleBookEntryUtil.getDefaultMasterStyleBookEntry(layout);

		styleBooksJSONArray.put(
			JSONUtil.put(
				"imagePreviewURL",
				() -> {
					if (defaultStyleBookEntry != null) {
						return defaultStyleBookEntry.getImagePreviewURL(
							themeDisplay);
					}

					return StringPool.BLANK;
				}
			).put(
				"name",
				DefaultStyleBookEntryUtil.getStyleBookEntryName(
					layout, themeDisplay.getLocale(),
					StyleBookUtil.getStyleFromThemeStyleBookEntry(
						layout, themeDisplay.getLocale()))
			).put(
				"styleBookEntryERC", StringPool.BLANK
			).put(
				"subtitle",
				() -> {
					if (defaultStyleBookEntry != null) {
						return defaultStyleBookEntry.getName();
					}

					return null;
				}
			).put(
				"tokenValues",
				StyleBookEntryUtil.getFrontendTokensValues(
					frontendTokenDefinition, themeDisplay.getLocale(),
					defaultStyleBookEntry)
			));

		return JSONUtil.concat(
			styleBooksJSONArray,
			JSONUtil.toJSONArray(
				StyleBookEntryUtil.getStyleBookEntryMaps(
					frontendTokenDefinition, true, layout, themeDisplay),
				map -> _jsonFactory.createJSONObject(map)));
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}