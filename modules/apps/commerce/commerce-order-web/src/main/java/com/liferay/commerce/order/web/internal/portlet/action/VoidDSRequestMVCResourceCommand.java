/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.order.web.internal.exception.DSCommerceOrderAttachmentException;
import com.liferay.commerce.order.web.internal.util.DSCommerceOrderAttachmentUtil;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.digital.signature.exception.DSRequestException;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/void_ds_request"
	},
	service = MVCResourceCommand.class
)
public class VoidDSRequestMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			CommerceOrderAttachment commerceOrderAttachment =
				_commerceOrderAttachmentLocalService.getCommerceOrderAttachment(
					ParamUtil.getLong(
						resourceRequest, "commerceOrderAttachmentId"));

			_commerceOrderAttachmentModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), commerceOrderAttachment,
				ActionKeys.UPDATE);

			_commerceOrderModelResourcePermission.check(
				themeDisplay.getPermissionChecker(),
				commerceOrderAttachment.getCommerceOrderId(),
				ActionKeys.UPDATE);

			if (!DSCommerceOrderAttachmentUtil.isEnabled(
					commerceOrderAttachment.getCompanyId(),
					commerceOrderAttachment.getGroupId())) {

				throw new DSRequestException.MustHaveEmbeddedViewEnabled(
					commerceOrderAttachment.getGroupId());
			}

			DSRequest dsRequest =
				_dsRequestManager.fetchLatestFileEntryDSRequest(
					commerceOrderAttachment.getCompanyId(),
					commerceOrderAttachment.getFileEntryId());

			if (dsRequest == null) {
				throw new DSCommerceOrderAttachmentException.MustHaveDSRequest(
					commerceOrderAttachment.getCommerceOrderAttachmentId());
			}

			if (dsRequest.isTerminal()) {
				throw new DSRequestException.MustBeActive(
					dsRequest.getDSRequestId());
			}

			_dsRequestManager.voidDSRequest(
				dsRequest.getDSRequestId(),
				_language.get(
					themeDisplay.getLocale(),
					"the-sender-voided-the-signature-request"));

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONObject());
		}
		catch (NoSuchModelException noSuchModelException) {
			throw new PrincipalException(noSuchModelException);
		}
		catch (PrincipalException principalException) {
			throw principalException;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"errorMessage",
					DSCommerceOrderAttachmentUtil.getErrorMessage(
						_language, themeDisplay.getLocale(), portalException)));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VoidDSRequestMVCResourceCommand.class);

	@Reference
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrderAttachment)"
	)
	private ModelResourcePermission<CommerceOrderAttachment>
		_commerceOrderAttachmentModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}