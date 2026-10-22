/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.manager.DSRecipientViewDefinitionManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.web.internal.display.context.SignDSRequestDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE,
		"mvc.command.name=/digital_signature/sign_ds_request",
		"portlet.add.default.resource.check.whitelist.mvc.action=true"
	},
	service = MVCRenderCommand.class
)
public class SignDSRequestMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DSRequest dsRequest = _dsRequestManager.fetchDSRequest(
			themeDisplay.getCompanyId(),
			ParamUtil.getLong(renderRequest, "dsRequestId"));

		User user = themeDisplay.getUser();

		if ((dsRequest == null) ||
			!dsRequest.isSignatureRequired(user.getUserId())) {

			return "/sign_digital_signature/view.jsp";
		}

		try {
			if (!_dsRequestManager.hasPermission(
					themeDisplay.getPermissionChecker(), dsRequest,
					ActionKeys.VIEW)) {

				return "/sign_digital_signature/view.jsp";
			}
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}

		if (!themeDisplay.isStatePopUp()) {
			try {
				String urlViewInContext = _fetchURLViewInContext(
					dsRequest, renderRequest, renderResponse, themeDisplay);

				if (Validator.isNull(urlViewInContext)) {
					urlViewInContext = _portal.getLayoutFullURL(
						themeDisplay.getLayout(), themeDisplay);
				}

				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);

				httpServletResponse.sendRedirect(
					HttpComponentsUtil.setParameter(
						urlViewInContext, "dsRequestId",
						dsRequest.getDSRequestId()));
			}
			catch (IOException | PortalException exception) {
				throw new PortletException(exception);
			}

			return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
		}

		try {
			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new SignDSRequestDisplayContext(
					DigitalSignatureConfigurationUtil.
						getDigitalSignatureConfiguration(
							themeDisplay.getCompanyId(),
							dsRequest.getSiteGroupId()),
					dsRequest, _groupLocalService, _portal,
					_dsRecipientViewDefinitionManager.
						addDSRecipientViewDefinition(
							themeDisplay.getCompanyId(),
							dsRequest.getSiteGroupId(),
							dsRequest.getExternalReferenceCode(),
							dsRequest.fetchSentDSRequestRecipient(
								user.getUserId()),
							_portal.getPortalURL(
								_portal.getHttpServletRequest(renderRequest))),
					renderRequest, renderResponse));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to create the recipient view for digital signature " +
					"request " + dsRequest.getDSRequestId(),
				exception);

			return "/sign_digital_signature/view.jsp";
		}

		return "/sign_digital_signature/sign_ds_request.jsp";
	}

	private String _fetchURLViewInContext(
		DSRequest dsRequest, RenderRequest renderRequest,
		RenderResponse renderResponse, ThemeDisplay themeDisplay) {

		for (long fileEntryId : dsRequest.getFileEntryIds()) {
			DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
				fileEntryId);

			if ((dlFileEntry == null) || (dlFileEntry.getClassNameId() == 0)) {
				continue;
			}

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						dlFileEntry.getClassName());

			if (assetRendererFactory == null) {
				continue;
			}

			try {
				AssetRenderer<?> assetRenderer =
					assetRendererFactory.getAssetRenderer(
						dlFileEntry.getClassPK());

				if ((assetRenderer == null) ||
					!assetRenderer.hasViewPermission(
						themeDisplay.getPermissionChecker())) {

					continue;
				}

				String assetDisplayPageFriendlyURL =
					_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
						new InfoItemReference(
							dlFileEntry.getClassName(),
							new ClassPKInfoItemIdentifier(
								dlFileEntry.getClassPK())),
						themeDisplay);

				if (Validator.isNotNull(assetDisplayPageFriendlyURL)) {
					return assetDisplayPageFriendlyURL;
				}

				String urlViewInContext = assetRenderer.getURLViewInContext(
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse), null);

				if (Validator.isNotNull(urlViewInContext) &&
					!Objects.equals(
						HttpComponentsUtil.getParameter(
							urlViewInContext, "p_l_id", false),
						"0")) {

					return urlViewInContext;
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SignDSRequestMVCRenderCommand.class);

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DSRecipientViewDefinitionManager _dsRecipientViewDefinitionManager;

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}