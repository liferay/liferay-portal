/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.web.internal.constants;

/**
 * @author Marta Medio
 */
public interface MFATimeBasedOTPEventTypes {

	public static final String MFA_TIMEBASED_OTP_NOT_VERIFIED =
		"MFA_TIMEBASED_OTP_NOT_VERIFIED";

	public static final String MFA_TIMEBASED_OTP_SETUP_ADDED =
		"MFA_TIMEBASED_OTP_SETUP_ADDED";

	public static final String MFA_TIMEBASED_OTP_SETUP_REMOVED =
		"MFA_TIMEBASED_OTP_SETUP_REMOVED";

	public static final String MFA_TIMEBASED_OTP_VERIFICATION_FAILURE =
		"MFA_TIMEBASED_OTP_VERIFICATION_FAILURE";

	public static final String MFA_TIMEBASED_OTP_VERIFICATION_SUCCESS =
		"MFA_TIMEBASED_OTP_VERIFICATION_SUCCESS";

	public static final String MFA_TIMEBASED_OTP_VERIFIED =
		"MFA_TIMEBASED_OTP_VERIFIED";

}