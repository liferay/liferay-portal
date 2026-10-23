/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.account.model.AccountEntry;
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

			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_commerceOrderAttachmentModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), commerceOrderAttachment,
				ActionKeys.UPDATE);

			if (!_hasAllowedFileExtension(commerceOrderAttachment)) {
				throw new DSCommerceOrderAttachmentException.
					MustHaveSupportedFileExtension(
						commerceOrderAttachment.getCommerceOrderAttachmentId());
			}

			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(
					commerceOrderAttachment.getCommerceOrderId());

			_commerceOrderModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), commerceOrder,
				ActionKeys.UPDATE);

			if (!DSCommerceOrderAttachmentUtil.isEnabled(
					commerceOrder.getCompanyId(), commerceOrder.getGroupId())) {

				throw new DSRequestException.MustHaveEmbeddedViewEnabled(
					commerceOrder.getGroupId());
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

		List<User> users = TransformUtil.unsafeTransformToList(
			ArrayUtil.unique(
				ParamUtil.getLongValues(resourceRequest, "recipientUserIds")),
			_userLocalService::getUser);

		_validateUsers(commerceOrder, permissionChecker, users);

		boolean sequential = ParamUtil.getBoolean(
			resourceRequest, "sequential", true);

		return TransformUtil.transform(
			users,
			user -> {
				DSRecipient dsRecipient = new DSRecipient();

				int index = users.indexOf(user) + 1;

				dsRecipient.setDSRecipientId(String.valueOf(index));
				dsRecipient.setEmailAddress(user.getEmailAddress());
				dsRecipient.setName(user.getFullName());

				int routingOrder = 1;

				if (sequential) {
					routingOrder = index;
				}

				dsRecipient.setRoutingOrder(routingOrder);

				return dsRecipient;
			});
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

	private void _validateUsers(
			CommerceOrder commerceOrder, PermissionChecker permissionChecker,
			List<User> users)
		throws PortalException {

		if (users.isEmpty()) {
			throw new DSCommerceOrderAttachmentException.MustHaveRecipient();
		}

		Set<Long> accountEntryUserIds = new HashSet<>(
			TransformUtil.transform(
				DSCommerceOrderAttachmentUtil.getAccountEntryUsers(
					_accountEntryModelResourcePermission, commerceOrder,
					permissionChecker),
				User::getUserId));

		int count = 0;

		for (User user : users) {
			if (!DSCommerceOrderAttachmentUtil.isValidDSRecipient(
					commerceOrder.getCompanyId(), user)) {

				throw new DSRequestException.MustHaveValidRecipient(
					user.getUserId());
			}

			if (accountEntryUserIds.contains(user.getUserId()) ||
				(user.getUserId() == commerceOrder.getUserId())) {

				continue;
			}

			UserPermissionUtil.check(
				permissionChecker, user.getUserId(), ActionKeys.VIEW);

			count++;

			if (count > 1) {
				throw new DSCommerceOrderAttachmentException.
					MustNotHaveMultipleCountersigners();
			}
		}
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

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

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