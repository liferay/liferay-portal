/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.web.internal.util.DSCommerceOrderAttachmentUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.digital.signature.exception.DSRequestException;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/get_ds_request_recipient_users"
	},
	service = MVCResourceCommand.class
)
public class GetDSRequestRecipientUsersMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(
					ParamUtil.getLong(resourceRequest, "commerceOrderId"));

			_commerceOrderModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), commerceOrder,
				ActionKeys.UPDATE);

			if (!DSCommerceOrderAttachmentUtil.isEnabled(
					commerceOrder.getCompanyId(), commerceOrder.getGroupId())) {

				throw new DSRequestException.MustHaveEmbeddedViewEnabled(
					commerceOrder.getGroupId());
			}

			Set<Long> accountEntryUserIds = new HashSet<>(
				TransformUtil.transform(
					DSCommerceOrderAttachmentUtil.getAccountEntryUsers(
						_accountEntryModelResourcePermission, commerceOrder,
						themeDisplay.getPermissionChecker()),
					User::getUserId));

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.toJSONArray(
					ListUtil.filter(
						_userLocalService.search(
							commerceOrder.getCompanyId(),
							ParamUtil.getString(resourceRequest, "keywords"),
							WorkflowConstants.STATUS_APPROVED, null, 0, 10,
							(OrderByComparator<User>)null),
						user -> _isAllowedDSRecipient(
							accountEntryUserIds, commerceOrder,
							themeDisplay.getPermissionChecker(), user)),
					DSCommerceOrderAttachmentUtil::toJSONObject));
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

	private boolean _isAllowedDSRecipient(
		Set<Long> accountEntryUserIds, CommerceOrder commerceOrder,
		PermissionChecker permissionChecker, User user) {

		if (!DSCommerceOrderAttachmentUtil.isValidDSRecipient(
				commerceOrder.getCompanyId(), user)) {

			return false;
		}

		if (accountEntryUserIds.contains(user.getUserId()) ||
			(user.getUserId() == commerceOrder.getUserId())) {

			return true;
		}

		return UserPermissionUtil.contains(
			permissionChecker, user.getUserId(), ActionKeys.VIEW);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetDSRequestRecipientUsersMVCResourceCommand.class);

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private UserLocalService _userLocalService;

}