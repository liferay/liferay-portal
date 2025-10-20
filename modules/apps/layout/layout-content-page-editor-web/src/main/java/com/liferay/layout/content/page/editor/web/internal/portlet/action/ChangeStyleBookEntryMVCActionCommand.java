/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.StyleBookEntryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/change_style_book_entry"
	},
	service = MVCActionCommand.class
)
public class ChangeStyleBookEntryMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		LayoutPermissionUtil.checkLayoutRestrictedUpdatePermission(
			themeDisplay.getPermissionChecker(), layout);

		Layout updatedLayout = _layoutLocalService.updateStyleBookEntryERC(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			ParamUtil.getString(actionRequest, "styleBookEntryERC"));

		if (layout.isDraftLayout()) {
			UnicodeProperties layoutTypeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			layoutTypeSettingsUnicodeProperties.put(
				LayoutTypeSettingsConstants.KEY_DESIGN_CONFIGURATION_MODIFIED,
				Boolean.TRUE.toString());

			updatedLayout = _layoutLocalService.updateLayout(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(),
				layoutTypeSettingsUnicodeProperties.toString());
		}

		FrontendTokenDefinition frontendTokenDefinition = null;

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-30204")) {

			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					themeDisplay.getLayout());
		}
		else {
			Group group = themeDisplay.getScopeGroup();

			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					_layoutSetLocalService.fetchLayoutSet(
						themeDisplay.getSiteGroupId(),
						group.isLayoutSetPrototype()));
		}

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.
				fetchStyleBookEntryByExternalReferenceCode(
					updatedLayout.getStyleBookEntryERC(),
					updatedLayout.getGroupId());

		if (styleBookEntry == null) {
			styleBookEntry = DefaultStyleBookEntryUtil.getDefaultStyleBookEntry(
				updatedLayout);
		}

		return JSONUtil.put(
			"tokenValues",
			StyleBookEntryUtil.getFrontendTokensValues(
				frontendTokenDefinition, themeDisplay.getLocale(),
				styleBookEntry));
	}

	@Override
	protected boolean isLayoutLockRequired() {
		return false;
	}

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}