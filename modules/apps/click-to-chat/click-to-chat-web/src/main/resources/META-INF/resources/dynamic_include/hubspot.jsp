<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<%
String errorMessage = null;
String identificationToken = null;

String[] parts = clickToChatChatProviderAccountId.split(StringPool.SLASH);

if (themeDisplay.isSignedIn() && (parts.length > 1)) {
	try {
		Http.Options options = new Http.Options();

		options.addHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		options.setBody(
			JSONUtil.put(
				"email", user.getEmailAddress()
			).put(
				"firstName", user.getFirstName()
			).put(
				"lastName", user.getLastName()
			).toString(),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setLocation("https://api.hubspot.com/conversations/v3/visitor-identification/tokens/create?hapikey=" + parts[1]);
		options.setPost(true);

		String responseJSON = HttpUtil.URLtoString(options);

		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(responseJSON);

		errorMessage = HtmlUtil.escapeJS(responseJSONObject.getString("message"));

		identificationToken = responseJSONObject.getString("token");
	}
	catch (Exception exception) {
		if (_log.isWarnEnabled()) {
			_log.warn(exception);
		}
	}
}
%>

<aui:script async="<%= true %>" defer="<%= true %>" id="hs-script-loader" src='<%= "//js-na1.hs-scripts.com/" + parts[0] + ".js" %>' type="text/javascript"></aui:script>

<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() && (parts.length > 1) %>">
		<c:choose>
			<c:when test="<%= Validator.isNull(identificationToken) %>">
				<aui:script position="inline">
					Liferay.Util.openToast({
						message:
							'<%= (errorMessage != null) ? errorMessage : LanguageUtil.get(resourceBundle, "unable-to-connect-to-hubspot") %>',
						type: 'danger',
					});
				</aui:script>
			</c:when>
			<c:otherwise>
				<aui:script position="inline" type="text/javascript">
					window.hsConversationsSettings = {
						identificationEmail: '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>',
						identificationToken: '<%= HtmlUtil.escapeJS(identificationToken) %>',
					};

					window.HubSpotConversations.widget.load();
				</aui:script>
			</c:otherwise>
		</c:choose>
	</c:when>
</c:choose>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_click_to_chat_web.dynamic_include.hubspot_jsp");
%>