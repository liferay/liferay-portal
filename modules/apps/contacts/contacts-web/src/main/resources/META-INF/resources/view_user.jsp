<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long userId = ParamUtil.getLong(request, "userId");

User user2 = null;

if (userId > 0) {
	user2 = UserLocalServiceUtil.getUser(userId);
}
else {
	user2 = (User)request.getAttribute(ContactsWebKeys.CONTACTS_USER);
}

user2 = user2.toEscapedModel();

request.setAttribute("view_user.jsp-user", user2);
%>

<c:if test="<%= user2 != null %>">
	<div class="contacts-profile <%= (user.getUserId() == user2.getUserId()) ? "my-profile" : StringPool.BLANK %>" id="<portlet:namespace />contactsProfile">
		<c:if test="<%= (displayStyle == ContactsConstants.DISPLAY_STYLE_BASIC) || (displayStyle == ContactsConstants.DISPLAY_STYLE_FULL) %>">
			<clay:row>
				<clay:col
					cssClass="social-relations"
				>

					<%
					boolean connection = SocialRelationLocalServiceUtil.hasRelation(themeDisplay.getUserId(), user2.getUserId(), SocialRelationConstants.TYPE_BI_CONNECTION);
					boolean follower = SocialRelationLocalServiceUtil.hasRelation(user2.getUserId(), themeDisplay.getUserId(), SocialRelationConstants.TYPE_UNI_FOLLOWER);
					boolean following = SocialRelationLocalServiceUtil.hasRelation(themeDisplay.getUserId(), user2.getUserId(), SocialRelationConstants.TYPE_UNI_FOLLOWER);
					%>

					<c:if test="<%= connection || follower || following %>">
						<div class="lfr-asset-metadata">
							<c:if test="<%= connection %>">
								<span class="lfr-asset-icon lfr-asset-connection<%= (following || follower) ? StringPool.BLANK : " last" %>">
									<liferay-ui:icon
										icon="user"
										markupView="lexicon"
									/>

									<liferay-ui:message key="connection" />
								</span>
							</c:if>

							<c:if test="<%= following %>">
								<span class="lfr-asset-icon lfr-asset-following<%= follower ? StringPool.BLANK : " last" %>">
									<liferay-ui:icon
										icon="user"
										markupView="lexicon"
									/>

									<liferay-ui:message key="following" />
								</span>
							</c:if>

							<c:if test="<%= follower %>">
								<span class="last lfr-asset-follower lfr-asset-icon">
									<liferay-ui:icon
										icon="user"
										markupView="lexicon"
									/>

									<liferay-ui:message key="follower" />
								</span>
							</c:if>
						</div>
					</c:if>

					<clay:row>
						<clay:col
							cssClass="contacts-action"
						>
							<c:choose>
								<c:when test="<%= portletId.equals(ContactsPortletKeys.CONTACTS_CENTER) || portletId.equals(ContactsPortletKeys.MEMBERS) %>">

									<%
									boolean blocked = false;

									if (SocialRelationLocalServiceUtil.hasRelation(user2.getUserId(), themeDisplay.getUserId(), SocialRelationConstants.TYPE_UNI_ENEMY)) {
										blocked = true;
									}
									else if (SocialRelationLocalServiceUtil.hasRelation(themeDisplay.getUserId(), user2.getUserId(), SocialRelationConstants.TYPE_UNI_ENEMY)) {
										blocked = true;
									}

									boolean showConnectedRequestedIcon = !blocked && SocialRequestLocalServiceUtil.hasRequest(themeDisplay.getUserId(), User.class.getName(), themeDisplay.getUserId(), SocialRelationConstants.TYPE_BI_CONNECTION, user2.getUserId(), SocialRequestConstants.STATUS_PENDING);
									boolean showConnectedIcon = !blocked && SocialRelationLocalServiceUtil.hasRelation(themeDisplay.getUserId(), user2.getUserId(), SocialRelationConstants.TYPE_BI_CONNECTION);
									boolean showFollowingIcon = !blocked && SocialRelationLocalServiceUtil.hasRelation(themeDisplay.getUserId(), user2.getUserId(), SocialRelationConstants.TYPE_UNI_FOLLOWER);
									boolean showBlockIcon = SocialRelationLocalServiceUtil.hasRelation(themeDisplay.getUserId(), user2.getUserId(), SocialRelationConstants.TYPE_UNI_ENEMY);
									%>

									<liferay-ui:icon
										cssClass='<%= showConnectedRequestedIcon ? "action disabled" : "action disabled hide" %>'
										image="../aui/user"
										label="<%= true %>"
										message="connection-requested"
									/>

									<liferay-ui:icon
										cssClass='<%= showConnectedIcon ? "action connected" : "action connected hide" %>'
										image="../aui/user"
										label="<%= true %>"
										message="connected"
									/>

									<liferay-ui:icon
										cssClass='<%= showFollowingIcon ? "action following" : "action following hide" %>'
										image="../aui/user"
										label="<%= true %>"
										message="following"
									/>

									<liferay-ui:icon
										cssClass='<%= showBlockIcon ? "action block" : "action block hide" %>'
										image="../aui/ban-circle"
										label="<%= true %>"
										message="blocked"
									/>
								</c:when>
								<c:otherwise>
									<liferay-util:include page="/user/user_toolbar.jsp" servletContext="<%= application %>" />
								</c:otherwise>
							</c:choose>
						</clay:col>
					</clay:row>
				</clay:col>
			</clay:row>

			<div class="d-flex lfr-detail-info lfr-field-group" data-title="<%= LanguageUtil.get(request, "details") %>">

				<%
				PortletURL editDetailsURL = PortletURLFactoryUtil.create(request, PortletKeys.MY_ACCOUNT, embeddedPersonalApplicationLayout, PortletRequest.RENDER_PHASE);
				%>

				<clay:link
					borderless="<%= true %>"
					cssClass="edit-button lfr-portal-tooltip"
					displayType="secondary"
					href="<%= editDetailsURL.toString() %>"
					icon="pencil"
					monospaced="<%= true %>"
					small="<%= true %>"
					title='<%= LanguageUtil.format(request, "edit-x", LanguageUtil.get(request, "information"), false) %>'
					type="button"
				/>

				<c:if test="<%= showIcon %>">
					<div class="lfr-contact-thumb">
						<a href="<%= user2.getDisplayURL(themeDisplay) %>"><img alt="<%= HtmlUtil.escapeAttribute(user2.getFullName()) %>" src="<%= user2.getPortraitURL(themeDisplay) %>" /></a>
					</div>
				</c:if>

				<div class="<%= showIcon ? StringPool.BLANK : "no-icon" %> lfr-contact-info">
					<div class="lfr-contact-name">
						<a class="text-decoration-underline" href="<%= user2.getDisplayURL(themeDisplay) %>"><%= user2.getFullName() %></a>
					</div>

					<div class="lfr-contact-job-title">
						<%= user2.getJobTitle() %>
					</div>

					<c:if test="<%= showEmailAddress %>">
						<div class="lfr-contact-extra">
							<a class="text-decoration-underline" href="mailto:<%= user2.getEmailAddress() %>"><%= user2.getEmailAddress() %></a>
						</div>
					</c:if>
				</div>

				<div class="clear"><!-- --></div>
			</div>
		</c:if>

		<c:if test="<%= ((displayStyle == ContactsConstants.DISPLAY_STYLE_DETAIL) || (displayStyle == ContactsConstants.DISPLAY_STYLE_FULL) || ((themeDisplay.getUserId() == user2.getUserId()) && showCompleteYourProfile)) && UserPermissionUtil.contains(permissionChecker, user2.getUserId(), ActionKeys.VIEW) %>">
			<div class="user-information" id="<portlet:namespace />userInformation">
				<clay:row>
					<clay:col>
						<c:if test="<%= showUsersInformation %>">
							<clay:col
								cssClass="user-information-column-1"
								md="<%= showSites ? String.valueOf(9) : String.valueOf(12) %>"
							>
								<div class="user-information-title">
									<liferay-ui:message key="about" />
								</div>

								<div class="lfr-user-info-container">
									<liferay-util:include page="/user/view_user_information.jsp" servletContext="<%= application %>" />
								</div>

								<%
								Map<String, String> extensions = ContactsExtensionsUtil.getExtensions();

								Set<String> servletContextNames = extensions.keySet();

								request.setAttribute("view_user.jsp-showCompleteYourProfile", String.valueOf(showCompleteYourProfile));

								for (String servletContextName : servletContextNames) {
									String extensionPath = extensions.get(servletContextName);

									ServletContext extensionServletContext = ServletContextPool.get(servletContextName);

									String title = extensionPath.substring(extensionPath.lastIndexOf(StringPool.SLASH) + 1, extensionPath.lastIndexOf(StringPool.PERIOD));

									title = StringUtil.replace(title, CharPool.UNDERLINE, CharPool.DASH);
								%>

									<div class="user-information-title">
										<liferay-ui:message key="<%= title %>" />
									</div>

									<liferay-util:include page="<%= extensionPath %>" servletContext="<%= extensionServletContext %>" />

								<%
								}
								%>

							</clay:col>
						</c:if>

						<c:if test="<%= showSites || showTags %>">
							<clay:col
								cssClass="user-information-column-2"
								md="<%= showUsersInformation ? String.valueOf(3) : String.valueOf(12) %>"
							>
								<c:if test="<%= showSites %>">

									<%
									LinkedHashMap<String, Object> groupParams = LinkedHashMapBuilder.<String, Object>put(
										"site", Boolean.TRUE
									).build();

									if (scopeGroup.isUser()) {
										groupParams.put("usersGroups", Long.valueOf(scopeGroup.getClassPK()));
									}
									else {
										groupParams.put("usersGroups", Long.valueOf(themeDisplay.getUserId()));
									}

									groupParams.put("active", Boolean.TRUE);

									if (scopeGroup.isUser() && (themeDisplay.getUserId() != scopeGroup.getClassPK())) {
										List<Integer> types = new ArrayList<Integer>();

										types.add(GroupConstants.TYPE_SITE_OPEN);
										types.add(GroupConstants.TYPE_SITE_RESTRICTED);

										groupParams.put("types", types);
									}

									List<Group> results = GroupLocalServiceUtil.search(company.getCompanyId(), null, null, groupParams, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
									%>

									<div class="user-sites-title">
										<liferay-ui:message key="sites" />
									</div>

									<ul class="user-sites">
										<c:choose>
											<c:when test="<%= !results.isEmpty() %>">

												<%
												for (Group curGroup : results) {
												%>

													<li class="user-information-sites"><a class="text-decoration-underline" href="<%= curGroup.getDisplayURL(themeDisplay, !curGroup.hasPublicLayouts()) %>"><%= HtmlUtil.escape(curGroup.getDescriptiveName(locale)) %></a></li>

												<%
												}
												%>

											</c:when>
											<c:otherwise>
												<div class="empty">
													<liferay-ui:message arguments="<%= HtmlUtil.escape(PortalUtil.getUserName(user2.getUserId(), scopeGroup.getDescriptiveName(locale))) %>" key="x-does-not-belong-to-any-sites" translateArguments="<%= false %>" />
												</div>
											</c:otherwise>
										</c:choose>
									</ul>
								</c:if>

								<c:if test="<%= showTags %>">
									<div class="user-tags-title">
										<liferay-ui:message key="tags" />
									</div>

									<%
									List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(User.class.getName(), user2.getUserId());
									%>

									<c:choose>
										<c:when test="<%= !assetTags.isEmpty() %>">
											<div class="lfr-field-group user-tags-wrapper" data-title="<%= LanguageUtil.get(request, "tags") %>">

												<%
												PortletURL editCategorizationURL = PortletURLFactoryUtil.create(request, PortletKeys.MY_ACCOUNT, embeddedPersonalApplicationLayout, PortletRequest.RENDER_PHASE);
												%>

												<clay:link
													borderless="<%= true %>"
													cssClass="edit-button lfr-portal-tooltip"
													displayType="secondary"
													href="<%= editCategorizationURL.toString() %>"
													icon="pencil"
													monospaced="<%= true %>"
													small="<%= true %>"
													title='<%= LanguageUtil.format(request, "edit-x", LanguageUtil.get(request, "information"), false) %>'
													type="button"
												/>

												<ul class="user-tags">

													<%
													StringBuilder sb = new StringBuilder();

													String searchPortletId = PortletProviderUtil.getPortletId(PortalSearchApplicationType.Search.CLASS_NAME, PortletProvider.Action.VIEW);

													for (AssetTag assetTag : assetTags) {
														PortletURL searchURL = PortletURLBuilder.createRenderURL(
															liferayPortletResponse, searchPortletId
														).setMVCPath(
															"/search.jsp"
														).setKeywords(
															assetTag.getName()
														).setParameter(
															"groupId", "0"
														).setWindowState(
															WindowState.MAXIMIZED
														).buildPortletURL();

														sb.append("<li><a class=\"text-decoration-underline\" href=\"");
														sb.append(searchURL);
														sb.append("\">");
														sb.append(HtmlUtil.escape(assetTag.getName()));
														sb.append("</a></li>");
													}
													%>

													<%= sb.toString() %>
												</ul>
											</div>
										</c:when>
										<c:otherwise>
											<liferay-ui:message arguments="<%= HtmlUtil.escape(PortalUtil.getUserName(user2.getUserId(), scopeGroup.getDescriptiveName(locale))) %>" key="x-does-not-have-any-tags" translateArguments="<%= false %>" />
										</c:otherwise>
									</c:choose>
								</c:if>
							</clay:col>
						</c:if>
					</clay:col>
				</clay:row>
			</div>

			<c:if test="<%= showRecentActivity && UserPermissionUtil.contains(permissionChecker, user2.getUserId(), ActionKeys.VIEW) %>">
				<div class="user-information-title">
					<liferay-ui:message key="recent-activity" />
				</div>

				<liferay-social-activities:social-activities
					activities="<%= SocialActivityLocalServiceUtil.getUserActivities(user2.getUserId(), 0, 10) %>"
					feedEnabled="<%= false %>"
				/>
			</c:if>
		</c:if>
	</div>
</c:if>