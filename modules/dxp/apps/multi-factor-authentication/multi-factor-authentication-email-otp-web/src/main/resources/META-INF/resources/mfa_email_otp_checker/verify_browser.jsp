<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long mfaEmailOTPFailedAttemptsRetryTimeout = GetterUtil.getLong(request.getAttribute(MFAEmailOTPWebKeys.MFA_EMAIL_OTP_FAILED_ATTEMPTS_RETRY_TIMEOUT));

long mfaEmailOTPRemainingResendTime = 0;

long mfaEmailOTPResendEmailTimeout = mfaEmailOTPConfiguration.resendEmailTimeout();
long mfaEmailOTPSetAtTime = GetterUtil.getLong(request.getAttribute(MFAEmailOTPWebKeys.MFA_EMAIL_OTP_SET_AT_TIME));

if ((mfaEmailOTPResendEmailTimeout > 0) && (mfaEmailOTPSetAtTime > 0)) {
	long mfaEmailOTPElapsedTime = (System.currentTimeMillis() - mfaEmailOTPSetAtTime) / 1000;

	if (mfaEmailOTPElapsedTime < mfaEmailOTPResendEmailTimeout) {
		mfaEmailOTPRemainingResendTime = mfaEmailOTPResendEmailTimeout - Math.max(0, mfaEmailOTPElapsedTime);
	}
}
%>

<c:if test="<%= mfaEmailOTPFailedAttemptsRetryTimeout > 0 %>">
	<div class="alert alert-danger" id="<portlet:namespace />maximumAllowedAttemptsError">
		<liferay-ui:message arguments="<%= mfaEmailOTPFailedAttemptsRetryTimeout %>" key="maximum-allowed-attempts-error" translateArguments="<%= false %>" />
	</div>
</c:if>

<div id="<portlet:namespace />phaseOne">
	<div class="portlet-msg-info">
		<liferay-ui:message arguments="<%= GetterUtil.getString(request.getAttribute(MFAEmailOTPWebKeys.MFA_EMAIL_OTP_SEND_TO_ADDRESS_OBFUSCATED)) %>" key="press-the-button-below-to-obtain-your-one-time-password-it-will-be-sent-to-x" translateArguments="<%= false %>" />
	</div>

	<aui:button-row>
		<aui:button disabled="<%= mfaEmailOTPRemainingResendTime > 0 %>" id="sendEmailButton" value='<%= (mfaEmailOTPRemainingResendTime > 0) ? String.valueOf(mfaEmailOTPRemainingResendTime) : "send" %>' />
	</aui:button-row>
</div>

<div id="<portlet:namespace />messageContainer"></div>

<div id="<portlet:namespace />phaseTwo">
	<aui:input autocomplete="off" label="enter-the-otp-from-the-email" name="otp" showRequiredLabel="yes" />
</div>

<aui:button-row>
	<aui:button id="submitEmailButton" type="submit" value="submit" />
</aui:button-row>

<aui:script use="aui-base,aui-io-request">
	<liferay-portlet:resourceURL id="/mfa_email_otp_verify/send_mfa_email_otp" portletName="<%= MFAEmailOTPPortletKeys.MFA_EMAIL_OTP_VERIFY %>" var="sendEmailOTPURL" />

	var failedAttemptsRetryTimeout = <%= mfaEmailOTPFailedAttemptsRetryTimeout %>;

	var failedAttemptsRetryCountdown;

	var resendCountdown;

	var messageContainer = A.one('#<portlet:namespace />messageContainer');

	var sendEmailButton = A.one('#<portlet:namespace />sendEmailButton');

	var submitEmailButton = A.one('#<portlet:namespace />submitEmailButton');

	var originalSendButtonText = '<%= UnicodeLanguageUtil.get(request, "send") %>';

	var originalSubmitButtonText = submitEmailButton.text();

	var remainingResendTime = <%= mfaEmailOTPRemainingResendTime %>;

	function <portlet:namespace />createCountdown(f, countdown, interval) {
		return setInterval(() => {
			--countdown;
			f(countdown);
		}, interval);
	}

	function <portlet:namespace />setFailedAttemptsRetryCountdown(
		failedAttemptsRetryDuration
	) {
		if (failedAttemptsRetryDuration < 1) {
			var maximumAllowedAttemptsError = A.one(
				'#<portlet:namespace />maximumAllowedAttemptsError'
			);

			if (maximumAllowedAttemptsError) {
				maximumAllowedAttemptsError.remove();
			}

			submitEmailButton.text(originalSubmitButtonText);

			submitEmailButton.removeAttribute('disabled');

			clearInterval(failedAttemptsRetryCountdown);

			failedAttemptsRetryCountdown = null;
		}
		else {
			submitEmailButton.text(failedAttemptsRetryDuration);
		}
	}

	function <portlet:namespace />setResendCountdown(resendDuration) {
		if (resendDuration < 1) {
			sendEmailButton.text(originalSendButtonText);

			sendEmailButton.removeAttribute('disabled');

			sendEmailButton.removeClass('disabled');

			clearInterval(resendCountdown);

			resendCountdown = null;

			messageContainer.html('');
		}
		else {
			sendEmailButton.text(resendDuration);
		}
	}

	if (remainingResendTime > 0) {
		sendEmailButton.setAttribute('disabled', 'disabled');

		<portlet:namespace />setResendCountdown(remainingResendTime);

		resendCountdown = <portlet:namespace />createCountdown(
			<portlet:namespace />setResendCountdown,
			remainingResendTime,
			1000
		);
	}

	if (failedAttemptsRetryTimeout > 0) {
		submitEmailButton.setAttribute('disabled', 'disabled');

		failedAttemptsRetryCountdown = <portlet:namespace />createCountdown(
			<portlet:namespace />setFailedAttemptsRetryCountdown,
			failedAttemptsRetryTimeout,
			1000
		);
	}

	A.one('#<portlet:namespace />sendEmailButton').on('click', (event) => {
		sendEmailButton.setAttribute('disabled', 'disabled');

		var resendDuration = <%= mfaEmailOTPConfiguration.resendEmailTimeout() %>;

		<portlet:namespace />setResendCountdown(resendDuration);

		resendCountdown = <portlet:namespace />createCountdown(
			<portlet:namespace />setResendCountdown,
			resendDuration,
			1000
		);

		var data = {
			p_auth: Liferay.authToken,
		};

		var setupEmail = A.one('#<portlet:namespace />setupEmail');

		if (setupEmail) {
			data['email'] = setupEmail.val();
		}

		var sendEmailOTPURL = '<%= HtmlUtil.escapeJS(sendEmailOTPURL) %>';

		A.io.request(sendEmailOTPURL, {
			dataType: 'JSON',
			data: data,
			method: 'POST',
			on: {
				failure: function (event, id, obj) {
					var messageContainer = A.one(
						'#<portlet:namespace />messageContainer'
					);

					messageContainer.html(
						'<span class="alert alert-danger"><liferay-ui:message key="failed-to-send-email" /></span>'
					);

					sendEmailButton.text(originalSendButtonText);
					sendEmailButton.removeAttribute('disabled');
					sendEmailButton.removeClass('disabled');

					clearInterval(resendCountdown);

					resendCountdown = null;
				},
				success: function (event, id, obj) {
					messageContainer.html(
						'<span class="alert alert-success"><liferay-ui:message key="your-otp-has-been-sent-by-email" /> <liferay-ui:message key="please-wait-before-requesting-a-new-otp" /></span>'
					);

					var phaseTwo = A.one('#<portlet:namespace />phaseTwo');
					phaseTwo.disabled = false;
				},
			},
		});
	});
</aui:script>