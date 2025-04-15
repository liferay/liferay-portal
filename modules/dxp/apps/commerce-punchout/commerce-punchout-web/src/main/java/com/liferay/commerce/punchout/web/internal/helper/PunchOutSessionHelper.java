/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.web.internal.helper;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.punchout.configuration.PunchOutConfiguration;
import com.liferay.commerce.punchout.constants.PunchOutConstants;
import com.liferay.commerce.punchout.service.PunchOutAccountRoleHelper;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaclyn Ong
 */
@Component(service = PunchOutSessionHelper.class)
public class PunchOutSessionHelper {

	public HttpSession getHttpSession(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		return originalHttpServletRequest.getSession();
	}

	public String getPunchOutReturnURL(HttpServletRequest httpServletRequest) {
		HttpSession httpSession = getHttpSession(httpServletRequest);

		Object punchOutReturnUrlObject = httpSession.getAttribute(
			PunchOutConstants.PUNCH_OUT_RETURN_URL_ATTRIBUTE_NAME);

		if (punchOutReturnUrlObject == null) {
			return null;
		}

		return (String)punchOutReturnUrlObject;
	}

	public boolean punchOutAllowed(HttpServletRequest httpServletRequest) {
		try {
			CommerceOrder commerceOrder = _getCommerceOrder(httpServletRequest);

			if (commerceOrder == null) {
				return false;
			}

			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			AccountEntry accountEntry = commerceContext.getAccountEntry();

			return _punchOutAccountRoleHelper.hasPunchOutRole(
				commerceOrder.getUserId(), accountEntry.getAccountEntryId());
		}
		catch (Exception exception) {
			_log.error(
				"Failed to determine whether user has " +
					PunchOutConstants.ROLE_NAME_ACCOUNT_PUNCH_OUT +
						" role under commerce account",
				exception);

			return false;
		}
	}

	public boolean punchOutEnabled(HttpServletRequest httpServletRequest) {
		try {
			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			long commerceChannelGroupId =
				commerceContext.getCommerceChannelGroupId();

			if (commerceChannelGroupId == 0L) {
				return false;
			}

			PunchOutConfiguration punchOutConfiguration =
				_getPunchOutConfiguration(commerceChannelGroupId);

			if (punchOutConfiguration != null) {
				return punchOutConfiguration.enabled();
			}
		}
		catch (Exception exception) {
			_log.error("Failed to load punch out configuration", exception);
		}

		return false;
	}

	public boolean punchOutSession(HttpServletRequest httpServletRequest) {
		return !Validator.isBlank(getPunchOutReturnURL(httpServletRequest));
	}

	private CommerceOrder _getCommerceOrder(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		String commerceOrderUuid = ParamUtil.getString(
			httpServletRequest, "commerceOrderUuid");

		if (Validator.isNotNull(commerceOrderUuid)) {
			long groupId =
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(
						_portal.getScopeGroupId(httpServletRequest));

			return _commerceOrderService.getCommerceOrderByUuidAndGroupId(
				commerceOrderUuid, groupId);
		}

		return _commerceOrderHttpHelper.getCurrentCommerceOrder(
			httpServletRequest);
	}

	private PunchOutConfiguration _getPunchOutConfiguration(
		long commerceChannelGroupId) {

		try {
			return _configurationProvider.getConfiguration(
				PunchOutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannelGroupId, PunchOutConstants.SERVICE_NAME));
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get punch out configuration",
				configurationException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PunchOutSessionHelper.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private PunchOutAccountRoleHelper _punchOutAccountRoleHelper;

}