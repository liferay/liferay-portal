/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.portal.security.auto.login.internal.events;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.punchout.oauth2.provider.model.PunchOutAccessToken;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaclyn Ong
 */
@Component(property = "key=login.events.post", service = LifecycleAction.class)
public class PunchOutLoginPostAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			Object punchOutAccessTokenObject = httpServletRequest.getAttribute(
				"punchOutAccessToken");

			Object punchOutUserIdObject = httpServletRequest.getAttribute(
				"punchOutUserId");

			if ((punchOutAccessTokenObject == null) ||
				(punchOutUserIdObject == null)) {

				return;
			}

			PunchOutAccessToken punchOutAccessToken =
				(PunchOutAccessToken)httpServletRequest.getAttribute(
					"punchOutAccessToken");

			long punchOutUserId = (long)httpServletRequest.getAttribute(
				"punchOutUserId");

			_startNewPunchOutSession(
				_portal.getCompanyId(httpServletRequest),
				punchOutAccessToken.getGroupId(),
				punchOutAccessToken.getCommerceAccountId(),
				punchOutAccessToken.getCurrencyCode(), punchOutAccessToken,
				punchOutUserId, httpServletRequest, httpServletResponse);

			httpServletRequest.removeAttribute("punchOutAccessToken");

			httpServletRequest.removeAttribute("punchOutUserId");
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private ThemeDisplay _getThemeDisplay() {
		return new ThemeDisplay() {
			{
				setSignedIn(true);
			}
		};
	}

	private void _startNewPunchOutSession(
			long companyId, long groupId, long commerceAccountId,
			String currencyCode, PunchOutAccessToken punchOutAccessToken,
			long punchOutUserId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		String commerceCurrencyCode = null;

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				companyId, currencyCode);

		if (commerceCurrency != null) {
			commerceCurrencyCode = commerceCurrency.getCode();
		}

		long commerceChannelGroupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				groupId);

		String commerceOrderUuid = punchOutAccessToken.getCommerceOrderUuid();

		CommerceOrder commerceOrder;

		if (!Validator.isBlank(commerceOrderUuid)) {
			commerceOrder =
				_commerceOrderLocalService.fetchCommerceOrderByUuidAndGroupId(
					commerceOrderUuid, commerceChannelGroupId);
		}
		else {
			commerceOrder = _commerceOrderLocalService.addCommerceOrder(
				punchOutUserId, commerceChannelGroupId, commerceAccountId,
				commerceCurrencyCode, 0);

			commerceOrder = _commerceOrderLocalService.updateStatus(
				punchOutUserId, commerceOrder.getCommerceOrderId(),
				WorkflowConstants.STATUS_APPROVED, Collections.emptyMap());
		}

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceAccountId, commerceChannelGroupId, null,
			commerceOrder.getCommerceOrderId(), companyId);

		httpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT, commerceContext);

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		String cookieName = _commerceOrderHttpHelper.getCookieName(
			commerceOrder.getGroupId());

		Cookie cookie = new Cookie(cookieName, commerceOrder.getUuid());

		cookie.setMaxAge(-1);

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, cookie, httpServletRequest,
			httpServletResponse);

		HttpSession httpSession = httpServletRequest.getSession();

		HashMap<String, Object> punchOutSessionAttributes =
			punchOutAccessToken.getPunchOutSessionAttributes();

		for (Map.Entry<String, Object> mapElement :
				punchOutSessionAttributes.entrySet()) {

			String key = mapElement.getKey();

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Adding attribute to session (key=", key, ", value=",
						mapElement.getValue(), StringPool.CLOSE_PARENTHESIS));
			}

			httpSession.setAttribute(key, mapElement.getValue());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PunchOutLoginPostAction.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private Portal _portal;

}