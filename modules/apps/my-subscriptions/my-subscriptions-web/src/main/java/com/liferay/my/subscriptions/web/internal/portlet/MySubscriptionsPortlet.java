/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.my.subscriptions.web.internal.portlet;

import com.liferay.my.subscriptions.web.internal.constants.MySubscriptionsPortletKeys;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 * @author Jonathan Lee
 * @author Peter Fellwock
 */
@Component(
	enabled = false,
	property = {
		"com.liferay.portlet.css-class-wrapper=my-subscriptions-portlet",
		"com.liferay.portlet.display-category=category.collaboration",
		"com.liferay.portlet.icon=/icons/my_subscriptions.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=0",
		"jakarta.portlet.display-name=My Subscriptions",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + MySubscriptionsPortletKeys.MY_SUBSCRIPTIONS,
		"jakarta.portlet.portlet-info.keywords=My Subscriptions",
		"jakarta.portlet.portlet-info.short-title=My Subscriptions",
		"jakarta.portlet.portlet-info.title=My Subscriptions",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class MySubscriptionsPortlet extends MVCPortlet {

	public void unsubscribe(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return;
		}

		long[] subscriptionIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "subscriptionIds"), 0L);

		for (long subscriptionId : subscriptionIds) {
			if (subscriptionId <= 0) {
				continue;
			}

			Subscription subscription =
				_subscriptionLocalService.getSubscription(subscriptionId);

			if (themeDisplay.getUserId() != subscription.getUserId()) {
				throw new PrincipalException();
			}

			_subscriptionLocalService.deleteSubscription(subscription);
		}
	}

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.my.subscriptions.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

}