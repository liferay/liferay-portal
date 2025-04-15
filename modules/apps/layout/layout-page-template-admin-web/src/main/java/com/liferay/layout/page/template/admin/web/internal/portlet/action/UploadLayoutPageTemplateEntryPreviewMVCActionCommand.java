/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.BaseImageEditorUploadFileEntryHandler;
import com.liferay.upload.UniqueFileNameProvider;
import com.liferay.upload.UploadHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/upload_layout_page_template_entry_preview"
	},
	service = MVCActionCommand.class
)
public class UploadLayoutPageTemplateEntryPreviewMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_layoutPageTemplateEntryPreviewImageEditorUploadFileEntryHandler,
			_itemSelectorUploadResponseHandler, actionRequest, actionResponse);
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference(
		target = "(model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry)"
	)
	private ModelResourcePermission<LayoutPageTemplateEntry>
		_layoutPageTemplateEntryModelResourcePermission;

	private final
		LayoutPageTemplateEntryPreviewImageEditorUploadFileEntryHandler
			_layoutPageTemplateEntryPreviewImageEditorUploadFileEntryHandler =
				new LayoutPageTemplateEntryPreviewImageEditorUploadFileEntryHandler();

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UploadHandler _uploadHandler;

	private class
		LayoutPageTemplateEntryPreviewImageEditorUploadFileEntryHandler
			extends BaseImageEditorUploadFileEntryHandler {

		@Override
		protected void checkPermissions(
				UploadPortletRequest uploadPortletRequest)
			throws PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long layoutPageTemplateEntryId = ParamUtil.getLong(
				uploadPortletRequest, "layoutPageTemplateEntryId");

			_layoutPageTemplateEntryModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), layoutPageTemplateEntryId,
				ActionKeys.UPDATE);
		}

		@Override
		protected DLAppService getDLAppService() {
			return _dlAppService;
		}

		@Override
		protected String getFolderName() {
			return LayoutPageTemplateEntryPreviewImageEditorUploadFileEntryHandler.class.
				getName();
		}

		@Override
		protected UniqueFileNameProvider getUniqueFileNameProvider() {
			return _uniqueFileNameProvider;
		}

	}

}