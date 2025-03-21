/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.BaseImageEditorUploadFileEntryHandler;
import com.liferay.upload.UniqueFileNameProvider;
import com.liferay.upload.UploadHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/upload_fragment_composition_preview"
	},
	service = MVCActionCommand.class
)
public class UploadFragmentCompositionPreviewMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		uploadHandler.upload(
			_fragmentEntryPreviewImageEditorUploadFileEntryHandler,
			itemSelectorUploadResponseHandler, actionRequest, actionResponse);
	}

	@Reference
	protected DLAppService dlAppService;

	@Reference
	protected ItemSelectorUploadResponseHandler
		itemSelectorUploadResponseHandler;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	protected PortletResourcePermission portletResourcePermission;

	@Reference
	protected UniqueFileNameProvider uniqueFileNameProvider;

	@Reference
	protected UploadHandler uploadHandler;

	private final FragmentEntryPreviewImageEditorUploadFileEntryHandler
		_fragmentEntryPreviewImageEditorUploadFileEntryHandler =
			new FragmentEntryPreviewImageEditorUploadFileEntryHandler();

	private class FragmentEntryPreviewImageEditorUploadFileEntryHandler
		extends BaseImageEditorUploadFileEntryHandler {

		@Override
		protected void checkPermissions(
				UploadPortletRequest uploadPortletRequest)
			throws PrincipalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			portletResourcePermission.check(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroup(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);
		}

		@Override
		protected DLAppService getDLAppService() {
			return dlAppService;
		}

		@Override
		protected String getFolderName() {
			return FragmentEntryPreviewImageEditorUploadFileEntryHandler.class.
				getName();
		}

		@Override
		protected UniqueFileNameProvider getUniqueFileNameProvider() {
			return uniqueFileNameProvider;
		}

	}

}