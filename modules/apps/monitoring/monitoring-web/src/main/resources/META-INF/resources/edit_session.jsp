<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String sessionId = ParamUtil.getString(request, "sessionId");

UserTracker userTracker = LiveUsers.getUserTracker(company.getCompanyId(), sessionId);

List<UserTrackerPath> paths = userTracker.getPaths();
int numHits = userTracker.getHits();

userTracker = userTracker.toEscapedModel();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(LanguageUtil.format(request, "session-id-x", sessionId, false));
%>

<portlet:actionURL name="/monitoring/edit_session" var="editSessionURL" />

<aui:form action="<%= editSessionURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="sessionId" type="hidden" value="<%= sessionId %>" />

	<c:choose>
		<c:when test="<%= userTracker == null %>">
			<liferay-ui:message key="session-id-could-not-be-found" />

			<aui:button href="<%= redirect %>" type="cancel" />
		</c:when>
		<c:otherwise>

			<%
			User user2 = null;

			try {
				user2 = UserLocalServiceUtil.getUserById(userTracker.getUserId());
			}
			catch (NoSuchUserException nsue) {
			}

			boolean userSessionAlive = false;
			%>

			<clay:sheet
				size="full"
			>
				<clay:panel-group>
					<clay:panel
						displayTitle='<%= LanguageUtil.get(request, "session") %>'
						expanded="<%= true %>"
					>
						<div class="panel-body">
							<dl>
								<dt class="h4">
									<liferay-ui:message key="session-id" />
								</dt>
								<dd>
									<%= HtmlUtil.escape(sessionId) %>
								</dd>
								<dt class="h4">
									<liferay-ui:message key="user-id" />
								</dt>
								<dd>
									<%= userTracker.getUserId() %>
								</dd>
								<dt class="h4">
									<liferay-ui:message key="name" />
								</dt>
								<dd>
									<%= (user2 != null) ? HtmlUtil.escape(user2.getFullName()) : LanguageUtil.get(request, "not-available") %>
								</dd>
								<dt class="h4">
									<liferay-ui:message key="email-address" />
								</dt>
								<dd>
									<%= (user2 != null) ? HtmlUtil.escape(user2.getEmailAddress()) : LanguageUtil.get(request, "not-available") %>
								</dd>
								<dt class="h4">
									<liferay-ui:message key="last-request" />
								</dt>
								<dd>
									<%= dateTimeFormat.format(userTracker.getModifiedDate()) %>
								</dd>
								<dt class="h4">
									<liferay-ui:message key="num-of-hits" />
								</dt>
								<dd>
									<%= numHits %>
								</dd>
								<dt class="h4">
									<liferay-ui:message key="browser-os-type" />
								</dt>
								<dd>
									<%= userTracker.getUserAgent() %>
								</dd>
								<dt class="h4">
									<liferay-ui:message key="remote-host-ip" />
								</dt>
								<dd>
									<%= userTracker.getRemoteAddr() %> / <%= userTracker.getRemoteHost() %>
								</dd>
							</dl>
						</div>
					</clay:panel>

					<clay:panel
						displayTitle='<%= LanguageUtil.get(request, "accessed-urls") %>'
					>
						<div class="panel-body">
							<dl>

								<%
								for (int i = 0; i < paths.size(); i++) {
									UserTrackerPath userTrackerPath = paths.get(i);
								%>

									<dt class="h4">
										<%= StringUtil.replace(userTrackerPath.getPath(), '&', "& ") %>
									</dt>
									<dd>
										<%= dateTimeFormat.format(userTrackerPath.getPathDate()) %>
									</dd>

								<%
								}
								%>

							</dl>
						</div>
					</clay:panel>

					<clay:panel
						displayTitle='<%= LanguageUtil.get(request, "session-attributes") %>'
					>
						<div class="panel-body">
							<dl>

								<%
								userSessionAlive = true;

								HttpSession userHttpSession = PortalSessionContext.get(sessionId);
								%>

								<c:if test="<%= userHttpSession != null %>">

									<%
									try {
										Set<String> sortedAttrNames = new TreeSet<String>();

										Enumeration<String> enumeration = userHttpSession.getAttributeNames();

										while (enumeration.hasMoreElements()) {
											String attrName = enumeration.nextElement();

											sortedAttrNames.add(attrName);
										}

										for (String attrName : sortedAttrNames) {
									%>

											<dt class="h4">
												<%= HtmlUtil.escape(attrName) %>
											</dt>

									<%
										}
									}
									catch (Exception e) {
										userSessionAlive = false;

										_log.error(e);
									}
									%>

								</c:if>
							</dl>
						</div>
					</clay:panel>
				</clay:panel-group>
			</clay:sheet>

			<aui:button-row>
				<c:if test="<%= userSessionAlive && !Objects.equals(session.getId(), sessionId) %>">
					<aui:button type="submit" value="kill-session" />
				</c:if>
			</aui:button-row>
		</c:otherwise>
	</c:choose>
</aui:form>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_monitoring_web.edit_session_jsp");
%>