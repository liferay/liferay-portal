/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.servlet;

import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.service.CommerceOrderAttachmentService;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/commerce-order-attachment-signature",
		"osgi.http.whiteboard.servlet.name=com.liferay.commerce.internal.servlet.DSCommerceOrderAttachmentDetailsServlet",
		"osgi.http.whiteboard.servlet.pattern=/commerce-order-attachment-signature/details/*"
	},
	service = Servlet.class
)
public class DSCommerceOrderAttachmentDetailsServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String originalName = PrincipalThreadLocal.getName();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			User user = _portal.getUser(httpServletRequest);

			if ((user == null) || user.isGuestUser()) {
				_sendError(httpServletResponse);

				return;
			}

			PrincipalThreadLocal.setName(user.getUserId());
			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(user));

			CommerceOrderAttachment commerceOrderAttachment =
				_commerceOrderAttachmentService.getCommerceOrderAttachment(
					ParamUtil.getLong(
						httpServletRequest, "commerceOrderAttachmentId"));

			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

			ServletResponseUtil.write(
				httpServletResponse,
				String.valueOf(
					_toJSONObject(
						_dsRequestManager.getFileEntryDSRequests(
							user.getCompanyId(),
							commerceOrderAttachment.getFileEntryId()))));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			_sendError(httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			_sendError(httpServletResponse);
		}
		finally {
			PrincipalThreadLocal.setName(originalName);
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private String _getDSRequestRecipientStatus(
		DSRequest dsRequest, DSRequestRecipient dsRequestRecipient) {

		String status = dsRequestRecipient.getStatus();

		if (Objects.equals(
				DSRequestConstants.STATUS_EXPIRED, dsRequest.getStatus()) &&
			(Objects.equals(DSRequestConstants.STATUS_CREATED, status) ||
			 Objects.equals(DSRequestConstants.STATUS_SENT, status))) {

			return DSRequestConstants.STATUS_EXPIRED;
		}

		return status;
	}

	private void _sendError(HttpServletResponse httpServletResponse)
		throws IOException {

		if (!httpServletResponse.isCommitted()) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private JSONObject _toDSRequestRecipientJSONObject(
		DSRequest dsRequest, DSRequestRecipient dsRequestRecipient) {

		return JSONUtil.put(
			"emailAddress", dsRequestRecipient.getEmailAddress()
		).put(
			"name", dsRequestRecipient.getName()
		).put(
			"sentDate", _toTime(dsRequestRecipient.getSentDate())
		).put(
			"signingOrder", dsRequestRecipient.getSigningOrder()
		).put(
			"status",
			_getDSRequestRecipientStatus(dsRequest, dsRequestRecipient)
		).put(
			"statusDate", _toTime(dsRequestRecipient.getStatusDate())
		);
	}

	private JSONObject _toHistoryJSONObject(DSRequest dsRequest) {
		return JSONUtil.put(
			"createDate", _toTime(dsRequest.getCreateDate())
		).put(
			"externalReferenceCode", dsRequest.getExternalReferenceCode()
		).put(
			"status", dsRequest.getStatus()
		).put(
			"statusDate", _toTime(dsRequest.getStatusDate())
		);
	}

	private JSONObject _toJSONObject(List<DSRequest> dsRequests)
		throws Exception {

		if (dsRequests.isEmpty()) {
			return _jsonFactory.createJSONObject();
		}

		DSRequest dsRequest = dsRequests.get(0);

		return JSONUtil.put(
			"expirationDate", _toTime(dsRequest.getExpirationDate())
		).put(
			"externalReferenceCode", dsRequest.getExternalReferenceCode()
		).put(
			"history",
			JSONUtil.toJSONArray(
				dsRequests.subList(1, dsRequests.size()),
				this::_toHistoryJSONObject)
		).put(
			"recipients",
			JSONUtil.toJSONArray(
				dsRequest.getDSRequestRecipients(),
				dsRequestRecipient -> _toDSRequestRecipientJSONObject(
					dsRequest, dsRequestRecipient))
		).put(
			"status", dsRequest.getStatus()
		).put(
			"statusDate", _toTime(dsRequest.getStatusDate())
		).put(
			"userName", dsRequest.getUserName()
		);
	}

	private Long _toTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DSCommerceOrderAttachmentDetailsServlet.class);

	@Reference
	private CommerceOrderAttachmentService _commerceOrderAttachmentService;

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

}