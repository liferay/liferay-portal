/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.email.otp.web.internal.portlet;

import com.liferay.multi.factor.authentication.email.otp.web.internal.constants.MFAEmailOTPPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.security.auth.InterruptedPortletRequestWhitelistUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.Portlet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.css-class-wrapper=portlet-mfa-email-otp-verify",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.preferences-company-wide=true",
		"jakarta.portlet.display-name=Multi Factor Authentication Email One-Time Password Verify",
		"jakarta.portlet.init-param.mvc-command-names-default-views=/mfa_email_otp_verify/verify",
		"jakarta.portlet.init-param.portlet-title-based-navigation=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/mfa_email_otp_verify/",
		"jakarta.portlet.name=" + MFAEmailOTPPortletKeys.MFA_EMAIL_OTP_VERIFY,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.version=4.0",
		"portlet.add.default.resource.check.whitelist=" + MFAEmailOTPPortletKeys.MFA_EMAIL_OTP_VERIFY
	},
	service = Portlet.class
)
public class MFAEmailOTPVerifyPortlet extends MVCPortlet {

	@Activate
	protected void activate(BundleContext bundleContext) {
		PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST = ArrayUtil.append(
			PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST,
			MFAEmailOTPPortletKeys.MFA_EMAIL_OTP_VERIFY);

		InterruptedPortletRequestWhitelistUtil.
			resetPortletInvocationWhitelist();
	}

	@Deactivate
	protected void deactivate() {
		PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST = ArrayUtil.remove(
			PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST,
			MFAEmailOTPPortletKeys.MFA_EMAIL_OTP_VERIFY);

		InterruptedPortletRequestWhitelistUtil.
			resetPortletInvocationWhitelist();
	}

}