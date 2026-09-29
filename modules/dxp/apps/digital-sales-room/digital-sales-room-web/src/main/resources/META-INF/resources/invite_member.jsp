<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL name="/digital_sales_room/invite_member" var="inviteMemberURL" />

<liferay-frontend:edit-form
	action="<%= inviteMemberURL %>"
	cssClass="dsr-invite-member-form"
>

	<%
	InviteMemberDisplayContext inviteMemberDisplayContext = (InviteMemberDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
	%>

	<liferay-frontend:edit-form-body>
		<aui:input name="redirect" type="hidden" value="<%= inviteMemberDisplayContext.getRedirect() %>" />
		<aui:input name="ticketKey" type="hidden" value="<%= inviteMemberDisplayContext.getTicketKey() %>" />

		<h2 class="sheet-title">
			<liferay-ui:message key="create-account" />
		</h2>

		<clay:sheet-section>
			<div class="form-group">
				<h3 class="sheet-subtitle">
					<liferay-ui:message key="user-display-data" />
				</h3>

				<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeDuplicate.class %>" focusField="screenName" message="the-screen-name-you-requested-is-already-taken" />
				<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNull.class %>" focusField="screenName" message="the-screen-name-cannot-be-blank" />
				<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNumeric.class %>" focusField="screenName" message="the-screen-name-cannot-contain-only-numeric-values" />
				<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeReserved.class %>" focusField="screenName" message="the-screen-name-you-requested-is-reserved" />
				<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeReservedForAnonymous.class %>" focusField="screenName" message="the-screen-name-you-requested-is-reserved-for-the-anonymous-user" />
				<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeUsedByGroup.class %>" focusField="screenName" message="the-screen-name-you-requested-is-already-taken-by-a-site" />

				<liferay-ui:error exception="<%= UserScreenNameException.MustNotExceedMaximumLength.class %>" focusField="screenName">

					<%
					int screenNameMaxLength = ModelHintsUtil.getMaxLength(User.class.getName(), "screenName");
					%>

					<liferay-ui:message arguments="<%= String.valueOf(screenNameMaxLength) %>" key="please-enter-a-screen-name-with-fewer-than-x-characters" />
				</liferay-ui:error>

				<liferay-ui:error exception="<%= UserScreenNameException.MustProduceValidFriendlyURL.class %>" focusField="screenName" message="the-screen-name-you-requested-must-produce-a-valid-friendly-url" />

				<liferay-ui:error exception="<%= UserScreenNameException.MustValidate.class %>" focusField="screenName">

					<%
					UserScreenNameException.MustValidate userScreenNameException = (UserScreenNameException.MustValidate)errorException;
					%>

					<liferay-ui:message key="<%= userScreenNameException.screenNameValidator.getDescription(locale) %>" />
				</liferay-ui:error>

				<aui:model-context model="<%= User.class %>" />

				<aui:input name="screenName">

					<%
					ScreenNameValidator screenNameValidator = ScreenNameValidatorFactory.getInstance();
					%>

					<c:if test="<%= Validator.isNotNull(screenNameValidator.getAUIValidatorJS()) %>">
						<aui:validator errorMessage="<%= screenNameValidator.getDescription(locale) %>" name="custom">
							<%= screenNameValidator.getAUIValidatorJS() %>
						</aui:validator>
					</c:if>
				</aui:input>

				<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeDuplicate.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-already-taken" />
				<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeNull.class %>" focusField="emailAddress" message="please-enter-an-email-address" />
				<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBePOP3User.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-reserved" />
				<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeReserved.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-reserved" />
				<liferay-ui:error exception="<%= UserEmailAddressException.MustNotUseCompanyMx.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-not-valid-because-its-domain-is-reserved" />
				<liferay-ui:error exception="<%= UserEmailAddressException.MustValidate.class %>" focusField="emailAddress" message="please-enter-a-valid-email-address" />

				<div class="form-group">
					<label>
						<liferay-ui:message key="email-address" />
					</label>

					<div class="form-control-plaintext">
						<%= HtmlUtil.escape(inviteMemberDisplayContext.getEmailAddress()) %>
					</div>
				</div>
			</div>

			<div class="form-group">
				<h3 class="sheet-subtitle">
					<liferay-ui:message key="personal-information" />
				</h3>

				<clay:row>
					<clay:col
						md="6"
					>
						<liferay-user:user-name-fields />
					</clay:col>

					<clay:col
						md="6"
					>
						<aui:input label="job-title" maxlength='<%= ModelHintsUtil.getMaxLength(Contact.class.getName(), "jobTitle") %>' name="jobTitle" type="text" />
					</clay:col>
				</clay:row>
			</div>

			<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.LOGIN_CREATE_ACCOUNT_ALLOW_CUSTOM_PASSWORD, PropsValues.LOGIN_CREATE_ACCOUNT_ALLOW_CUSTOM_PASSWORD) %>">
				<div class="form-group">
					<h3 class="sheet-subtitle">
						<liferay-ui:message key="password" />
					</h3>

					<clay:row>
						<clay:col
							md="6"
						>
							<aui:input label="password" name="password1" required="<%= true %>" size="30" type="password" value="" />

							<aui:input label="reenter-password" name="password2" required="<%= true %>" size="30" type="password" value="">
								<aui:validator name="equalTo">
									'#<portlet:namespace />password1'
								</aui:validator>
							</aui:input>
						</clay:col>
					</clay:row>
				</div>
			</c:if>
		</clay:sheet-section>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= inviteMemberDisplayContext.getBackURL() %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>