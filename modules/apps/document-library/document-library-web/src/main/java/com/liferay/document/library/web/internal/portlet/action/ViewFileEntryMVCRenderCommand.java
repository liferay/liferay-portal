/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.LinkedAssetEntryIdsUtil;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLDisplayContextProvider;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.repository.authorization.capability.AuthorizationCapability;
import com.liferay.document.library.util.DLAssetHelper;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContextProvider;
import com.liferay.document.library.web.internal.display.context.DLViewFileEntryDisplayContext;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.NoSuchRepositoryEntryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/view_file_entry"
	},
	service = MVCRenderCommand.class
)
public class ViewFileEntryMVCRenderCommand
	extends BaseFileEntryMVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			long fileEntryId = ParamUtil.getLong(renderRequest, "fileEntryId");

			Repository fileEntryRepository =
				RepositoryProviderUtil.getFileEntryRepository(fileEntryId);

			if (fileEntryRepository.isCapabilityProvided(
					AuthorizationCapability.class)) {

				AuthorizationCapability authorizationCapability =
					fileEntryRepository.getCapability(
						AuthorizationCapability.class);

				authorizationCapability.authorize(
					renderRequest, renderResponse);

				if (authorizationCapability.hasCustomRedirectFlow(
						renderRequest, renderResponse)) {

					return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
				}
			}

			if (!StringUtil.equals(
					DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
					_portal.getPortletId(renderRequest))) {

				ThemeDisplay themeDisplay =
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				String assetDisplayPageFriendlyURL =
					_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
						new InfoItemReference(
							FileEntry.class.getName(),
							new ClassPKInfoItemIdentifier(fileEntryId)),
						themeDisplay);

				if (assetDisplayPageFriendlyURL != null) {
					HttpServletResponse httpServletResponse =
						_portal.getHttpServletResponse(renderResponse);

					httpServletResponse.sendRedirect(
						assetDisplayPageFriendlyURL);

					return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
				}
			}

			return super.render(renderRequest, renderResponse);
		}
		catch (NoSuchFileEntryException | NoSuchFileVersionException |
			   NoSuchRepositoryEntryException | PrincipalException exception) {

			SessionErrors.add(renderRequest, exception.getClass());

			return "/document_library/error.jsp";
		}
		catch (IOException | PortalException exception) {
			throw new PortletException(exception);
		}
	}

	@Override
	protected String getPath() {
		return "/document_library/view_file_entry.jsp";
	}

	@Override
	protected void setAttributes(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {

		super.setAttributes(renderRequest, renderResponse);

		DLAdminDisplayContext dlAdminDisplayContext =
			_dlAdminDisplayContextProvider.getDLAdminDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_portal.getHttpServletResponse(renderResponse));

		DLViewFileEntryDisplayContext dlViewFileEntryDisplayContext =
			new DLViewFileEntryDisplayContext(
				dlAdminDisplayContext, _dlDisplayContextProvider,
				_portal.getHttpServletRequest(renderRequest), _language,
				_portal, renderRequest, renderResponse);

		renderRequest.setAttribute(
			DLViewFileEntryDisplayContext.class.getName(),
			dlViewFileEntryDisplayContext);

		AssetEntry layoutAssetEntry = _assetEntryLocalService.fetchEntry(
			DLFileEntryConstants.getClassName(),
			_dlAssetHelper.getAssetClassPK(
				dlViewFileEntryDisplayContext.getFileEntry(),
				dlViewFileEntryDisplayContext.getFileVersion()));

		renderRequest.setAttribute(
			WebKeys.LAYOUT_ASSET_ENTRY, layoutAssetEntry);

		if (layoutAssetEntry != null) {
			LinkedAssetEntryIdsUtil.addLinkedAssetEntryId(
				renderRequest, layoutAssetEntry.getEntryId());
		}
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private DLAdminDisplayContextProvider _dlAdminDisplayContextProvider;

	@Reference
	private DLAssetHelper _dlAssetHelper;

	@Reference
	private DLDisplayContextProvider _dlDisplayContextProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}