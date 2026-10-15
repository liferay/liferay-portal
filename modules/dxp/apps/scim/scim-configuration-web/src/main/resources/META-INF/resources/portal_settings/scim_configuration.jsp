<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String oAuth2AccessToken = GetterUtil.getString(request.getAttribute(ScimWebKeys.SCIM_OAUTH2_ACCESS_TOKEN));
String oAuth2AccessTokenExpirationDateString = GetterUtil.getString(request.getAttribute(ScimWebKeys.SCIM_OAUTH2_ACCESS_TOKEN_EXPIRATION_DATE));
long oAuth2AccessTokenExpirationDays = GetterUtil.getLong(request.getAttribute(ScimWebKeys.SCIM_OAUTH2_ACCESS_TOKEN_EXPIRATION_DAYS));
String oAuth2ApplicationName = GetterUtil.getString(request.getAttribute(ScimWebKeys.SCIM_OAUTH2_APPLICATION_NAME));
%>

<aui:input name="<%= Constants.CMD %>" type="hidden" value="" />

<div class="col-md-12">
	<aui:input label="oauth2-application-name" name="oAuth2ApplicationName" readonly="<%= Validator.isNotNull(oAuth2ApplicationName) %>" required="<%= true %>" type="text" value="<%= oAuth2ApplicationName %>" />

	<aui:select helpMessage="scim-matcher-field-help" label="scim-matcher-field" name="matcherField" required="<%= true %>" value="<%= request.getAttribute(ScimWebKeys.SCIM_MATCHER_FIELD) %>">
		<aui:option label="" value="" />
		<aui:option label="email" localizeLabel="<%= false %>" value="email" />
		<aui:option label="userName" localizeLabel="<%= false %>" value="userName" />
	</aui:select>

	<c:if test="<%= Validator.isNotNull(oAuth2ApplicationName) %>">

		<%
		String oAuth2AccessTokenInputId = liferayPortletResponse.getNamespace() + "oAuth2AccessToken";
		%>

		<c:if test="<%= !PropsValues.FIPS_ENABLED || Validator.isNotNull(oAuth2AccessToken) %>">
			<div class="form-group">
				<label for="<%= oAuth2AccessTokenInputId %>">
					<liferay-ui:message key="access-token" />
				</label>

				<div class="input-group input-group-sm">
					<div class="input-group-item input-group-prepend">
						<input class="form-control" id="<%= oAuth2AccessTokenInputId %>" readonly value="<%= oAuth2AccessToken %>" />
					</div>

					<span class="input-group-append input-group-item input-group-item-shrink">
						<clay:button
							cssClass="lfr-portal-tooltip scim-infopanel-copy-clipboard"
							data-clipboard-target='<%= "#" + oAuth2AccessTokenInputId %>'
							displayType="secondary"
							icon="paste"
							id="copyAccessToken"
							name="copyAccessToken"
							title="copy-link"
						/>
					</span>
				</div>
			</div>
		</c:if>

		<c:if test="<%= PropsValues.FIPS_ENABLED && Validator.isNotNull(oAuth2AccessTokenExpirationDateString) %>">
			<div class="alert alert-info">
				<liferay-ui:message key="the-access-token-is-shown-only-once-generate-a-new-one-if-you-did-not-copy-it" />
			</div>
		</c:if>

		<c:if test="<%= Validator.isNotNull(oAuth2AccessTokenExpirationDateString) %>">
			<c:choose>
				<c:when test="<%= (oAuth2AccessTokenExpirationDays < 30) && (oAuth2AccessTokenExpirationDays > 0) %>">
					<div class="alert alert-warning">
						<liferay-ui:message arguments="<%= oAuth2AccessTokenExpirationDateString %>" key="the-access-token-for-the-scim-client-will-expire-at-x" translateArguments="<%= false %>" />
					</div>
				</c:when>
				<c:when test="<%= oAuth2AccessTokenExpirationDays < 0 %>">
					<div class="alert alert-danger">
						<liferay-ui:message arguments="<%= oAuth2AccessTokenExpirationDateString %>" key="the-access-token-for-the-scim-client-expired-at-x" translateArguments="<%= false %>" />
					</div>
				</c:when>
			</c:choose>
		</c:if>

		<div class="form-group">
			<label for="<portlet:namespace />generateAccessToken">
				<liferay-ui:message key="scim-generate-access-token" />

				<span aria-label="<%= LanguageUtil.get(request, "scim-generate-access-token-help") %>" class="lfr-portal-tooltip" tabindex="0" title="<%= LanguageUtil.get(request, "scim-generate-access-token-help") %>">
					<clay:icon
						symbol="question-circle-full"
					/>
				</span>
			</label>

			<div class="input-group input-group-sm">
				<aui:button id="generateAccessToken" label="discard-changes" name="generateAccessToken" small="<%= true %>" value="generate" />
			</div>
		</div>

		<div class="form-group">
			<label for="<portlet:namespace />resetSCIMClientData">
				<liferay-ui:message key="scim-reset-client-data" />

				<span aria-label="<%= LanguageUtil.get(request, "scim-reset-client-data-help") %>" class="lfr-portal-tooltip" tabindex="0" title="<%= LanguageUtil.get(request, "scim-reset-client-data-help") %>">
					<clay:icon
						symbol="question-circle-full"
					/>
				</span>
			</label>

			<div class="input-group input-group-sm">
				<aui:button id="resetSCIMClientData" label="discard-changes" name="resetSCIMClientData" small="<%= true %>" value="reset" />
			</div>
		</div>

		<c:if test="<%= Validator.isNotNull(oAuth2AccessTokenExpirationDateString) %>">
			<div class="form-group">
				<label for="<portlet:namespace />revokeAccessToken">
					<liferay-ui:message key="scim-revoke-all" />

					<span aria-label="<%= LanguageUtil.get(request, "scim-revoke-all-help") %>" class="lfr-portal-tooltip" tabindex="0" title="<%= LanguageUtil.get(request, "scim-revoke-all-help") %>">
						<clay:icon
							symbol="question-circle-full"
						/>
					</span>
				</label>

				<div class="input-group input-group-sm">
					<aui:button id="revokeAccessToken" label="discard-changes" name="revokeAccessToken" small="<%= true %>" value="revoke" />
				</div>
			</div>
		</c:if>
	</c:if>
</div>

<liferay-frontend:component
	module="{InfoPanel} from scim-configuration-web"
/>

<aui:script>
	var copyAccessToken = document.getElementById(
		'<portlet:namespace />copyAccessToken'
	);

	if (copyAccessToken) {
		copyAccessToken.addEventListener('click', (event) => {
			this._clipboard = new ClipboardJS('.scim-infopanel-copy-clipboard');

			this._clipboard.on('success', this._handleClipboardSuccess.bind(this));
		});
	}

	var generateAccessToken = document.getElementById(
		'<portlet:namespace />generateAccessToken'
	);

	if (generateAccessToken) {
		generateAccessToken.addEventListener('click', (event) => {
			Liferay.Util.openConfirmModal({
				message:
					'<liferay-ui:message key="are-you-sure-you-want-to-generate-a-new-access-token" />',
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						var form = window.document['<portlet:namespace />fm'];
						form['<portlet:namespace /><%= Constants.CMD %>'].value =
							'generate';

						form.submit();
					}
				},
			});
		});
	}

	var resetSCIMClientData = document.getElementById(
		'<portlet:namespace />resetSCIMClientData'
	);

	if (resetSCIMClientData) {
		resetSCIMClientData.addEventListener('click', (event) => {
			Liferay.Util.openConfirmModal({
				message:
					'<liferay-ui:message key="are-you-sure-you-want-to-reset-all-scim-client-data" />',
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						var form = window.document['<portlet:namespace />fm'];
						form['<portlet:namespace /><%= Constants.CMD %>'].value =
							'reset';

						form.submit();
					}
				},
			});
		});
	}

	var revokeAccessToken = document.getElementById(
		'<portlet:namespace />revokeAccessToken'
	);

	if (revokeAccessToken) {
		revokeAccessToken.addEventListener('click', (event) => {
			Liferay.Util.openConfirmModal({
				message:
					'<liferay-ui:message key="are-you-sure-you-want-to-revoke-all-access-tokens" />',
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						var form = window.document['<portlet:namespace />fm'];
						form['<portlet:namespace /><%= Constants.CMD %>'].value =
							'revoke';

						form.submit();
					}
				},
			});
		});
	}
</aui:script>