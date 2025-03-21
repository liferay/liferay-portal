/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/publish_layout_page_template_entry"
	},
	service = MVCActionCommand.class
)
public class PublishLayoutPageTemplateEntryMVCActionCommand
	extends BaseContentPageEditorMVCActionCommand {

	@Override
	protected void doCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout draftLayout = _layoutLocalService.getLayout(
			themeDisplay.getPlid());

		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		LayoutPermissionUtil.check(
			themeDisplay.getPermissionChecker(), layout, ActionKeys.UPDATE);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_publishLayoutPageTemplateEntry(draftLayout, layout);

		String portletId = _portal.getPortletId(actionRequest);

		if (SessionMessages.contains(
				actionRequest,
				portletId.concat(
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE))) {

			SessionMessages.clear(actionRequest);
		}

		String key = "layoutPageTemplatePublished";

		if (layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

			key = "displayPagePublished";
		}
		else if (layoutPageTemplateEntry.getType() ==
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

			key = "masterPagePublished";
		}

		MultiSessionMessages.add(actionRequest, key);
	}

	private String _copySEOTypeSettingsUnicodeProperties(
		UnicodeProperties previousLayouTypeSettingsUnicodeProperties,
		UnicodeProperties layouTypeSettingsUnicodeProperties) {

		for (Map.Entry<String, String> entry :
				previousLayouTypeSettingsUnicodeProperties.entrySet()) {

			String key = entry.getKey();

			if (key.startsWith("mapped-") || key.startsWith("sitemap-")) {
				layouTypeSettingsUnicodeProperties.put(key, entry.getValue());
			}
		}

		return layouTypeSettingsUnicodeProperties.toString();
	}

	private LayoutPageTemplateEntry _publishLayoutPageTemplateEntry(
			Layout draftLayout, Layout layout)
		throws Exception {

		UnicodeProperties previousLayouTypeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		_layoutLocalService.copyLayoutContent(draftLayout, layout);

		LayoutStructureUtil.deleteMarkedForDeletionItems(
			draftLayout.getGroupId(), draftLayout.getPlid());

		draftLayout = _layoutLocalService.fetchLayout(draftLayout.getPlid());

		UnicodeProperties draftLayoutypeSettingsUnicodeProperties =
			draftLayout.getTypeSettingsProperties();

		draftLayoutypeSettingsUnicodeProperties.remove(
			LayoutTypeSettingsConstants.KEY_DESIGN_CONFIGURATION_MODIFIED);
		draftLayoutypeSettingsUnicodeProperties.put(
			LayoutTypeSettingsConstants.KEY_PUBLISHED, Boolean.TRUE.toString());

		draftLayout.setTypeSettingsProperties(
			draftLayoutypeSettingsUnicodeProperties);

		draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

		draftLayout = _layoutLocalService.updateLayout(draftLayout);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(draftLayout.getClassPK());

		_layoutPageTemplateEntryService.updateStatus(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			WorkflowConstants.STATUS_APPROVED);

		layout = _layoutLocalService.fetchLayout(layout.getPlid());

		layout.setStatus(WorkflowConstants.STATUS_APPROVED);

		layout = _layoutLocalService.updateLayout(layout);

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			_copySEOTypeSettingsUnicodeProperties(
				previousLayouTypeSettingsUnicodeProperties,
				layout.getTypeSettingsProperties()));

		return layoutPageTemplateEntry;
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

}