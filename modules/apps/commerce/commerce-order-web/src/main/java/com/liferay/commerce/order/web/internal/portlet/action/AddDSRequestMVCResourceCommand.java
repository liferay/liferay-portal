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
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.digital.signature.constants.DigitalSignatureConstants;
import com.liferay.digital.signature.exception.DSRequestException;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/add_ds_request"
	},
	service = MVCResourceCommand.class
)
public class AddDSRequestMVCResourceCommand
	extends BaseTransactionalMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			super.doServeResource(resourceRequest, resourceResponse);
		}
		catch (PortletException portletException) {
			_voidDSEnvelope(resourceRequest);

			Throwable throwable = portletException.getCause();

			if (!(throwable instanceof PortalException) ||
				(throwable instanceof PrincipalException)) {

				throw portletException;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(throwable);
			}

			try {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)resourceRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse,
					JSONUtil.put(
						"errorMessage",
						DSCommerceOrderAttachmentUtil.getErrorMessage(
							_language, themeDisplay.getLocale(),
							(PortalException)throwable)));
			}
			catch (IOException ioException) {
				throw new PortletException(ioException);
			}
		}
	}

	@Override
	protected void doTransactionalCommand(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		try {
			CommerceOrderAttachment commerceOrderAttachment =
				_commerceOrderAttachmentLocalService.getCommerceOrderAttachment(
					ParamUtil.getLong(
						resourceRequest, "commerceOrderAttachmentId"));

			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(
					commerceOrderAttachment.getCommerceOrderId());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_commerceOrderModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), commerceOrder,
				ActionKeys.UPDATE);

			_commerceOrderAttachmentModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), commerceOrderAttachment,
				ActionKeys.UPDATE);

			if (!DSCommerceOrderAttachmentUtil.isEnabled(commerceOrder)) {
				throw new DSCommerceOrderAttachmentException.
					MustHaveEmbeddedViewEnabled(
						commerceOrder.getCommerceOrderId());
			}

			if (!_hasAllowedFileExtension(commerceOrderAttachment)) {
				throw new DSCommerceOrderAttachmentException.
					MustHaveSupportedFileExtension(
						commerceOrderAttachment.getCommerceOrderAttachmentId());
			}

			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
					commerceOrder.getGroupId());

			DSRequest dsRequest = _dsRequestManager.addDSRequest(
				commerceOrder.getCompanyId(),
				new DSEnvelope() {
					{
						setDSRecipients(
							_getDSRecipients(
								commerceOrder,
								themeDisplay.getPermissionChecker(),
								resourceRequest));
						setEmailBlurb(
							ParamUtil.getString(
								resourceRequest, "emailMessage"));
						setEmailSubject(
							ParamUtil.getString(
								resourceRequest, "emailSubject"));
						setExpireAfter(
							ParamUtil.getInteger(
								resourceRequest, "expireAfter"));
						setName(commerceOrderAttachment.getTitle());
					}
				},
				new long[] {commerceOrderAttachment.getFileEntryId()},
				commerceChannel.getSiteGroupId(), themeDisplay.getUserId());

			resourceRequest.setAttribute(DSRequest.class.getName(), dsRequest);

			_dsRequestManager.sendDSRequestNotifications(
				dsRequest.getDSRequestId());

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONObject());
		}
		catch (NoSuchModelException noSuchModelException) {
			throw new PrincipalException(noSuchModelException);
		}
	}

	private List<DSRecipient> _getDSRecipients(
			CommerceOrder commerceOrder, PermissionChecker permissionChecker,
			ResourceRequest resourceRequest)
		throws PortalException {

		List<DSRecipient> dsRecipients = new ArrayList<>();

		Set<Long> accountUserIds = new HashSet<>(
			TransformUtil.transform(
				DSCommerceOrderAttachmentUtil.getAccountUsers(commerceOrder),
				User::getUserId));

		int count = 0;
		Set<Long> recipientUserIds = new HashSet<>();
		boolean sequential = ParamUtil.getBoolean(
			resourceRequest, "sequential", true);

		for (long recipientUserId :
				ParamUtil.getLongValues(resourceRequest, "recipientUserIds")) {

			if (!recipientUserIds.add(recipientUserId)) {
				continue;
			}

			User user = _userLocalService.getUser(recipientUserId);

			if ((user.getCompanyId() != commerceOrder.getCompanyId()) ||
				(user.getType() != UserConstants.TYPE_REGULAR) ||
				!user.isActive()) {

				throw new DSRequestException.MustHaveValidRecipient(
					recipientUserId);
			}

			if (!accountUserIds.contains(recipientUserId) &&
				(recipientUserId != commerceOrder.getUserId())) {

				UserPermissionUtil.check(
					permissionChecker, recipientUserId, ActionKeys.VIEW);

				count++;
			}

			if (count > 1) {
				throw new DSCommerceOrderAttachmentException.
					MustNotHaveMultipleCountersigners();
			}

			DSRecipient dsRecipient = new DSRecipient();

			dsRecipient.setDSRecipientId(
				String.valueOf(dsRecipients.size() + 1));
			dsRecipient.setEmailAddress(user.getEmailAddress());
			dsRecipient.setName(user.getFullName());

			int routingOrder = 1;

			if (sequential) {
				routingOrder = dsRecipients.size() + 1;
			}

			dsRecipient.setRoutingOrder(routingOrder);

			dsRecipients.add(dsRecipient);
		}

		if (dsRecipients.isEmpty()) {
			throw new DSCommerceOrderAttachmentException.MustHaveRecipient();
		}

		return dsRecipients;
	}

	private boolean _hasAllowedFileExtension(
			CommerceOrderAttachment commerceOrderAttachment)
		throws PortalException {

		FileEntry fileEntry = _dlAppLocalService.fetchFileEntry(
			commerceOrderAttachment.getFileEntryId());

		if (fileEntry == null) {
			return false;
		}

		return ArrayUtil.contains(
			DigitalSignatureConstants.ALLOWED_FILE_EXTENSIONS,
			fileEntry.getExtension());
	}

	private void _voidDSEnvelope(ResourceRequest resourceRequest) {
		DSRequest dsRequest = (DSRequest)resourceRequest.getAttribute(
			DSRequest.class.getName());

		if (dsRequest == null) {
			return;
		}

		try {
			_dsEnvelopeManager.voidDSEnvelope(
				dsRequest.getCompanyId(), dsRequest.getExternalReferenceCode(),
				dsRequest.getSiteGroupId(),
				"Unable to record the digital signature request");
		}
		catch (Exception exception) {
			_log.error(
				"Unable to void digital signature envelope " +
					dsRequest.getExternalReferenceCode(),
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddDSRequestMVCResourceCommand.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrderAttachment)"
	)
	private ModelResourcePermission<CommerceOrderAttachment>
		_commerceOrderAttachmentModelResourcePermission;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DSEnvelopeManager _dsEnvelopeManager;

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private UserLocalService _userLocalService;

}