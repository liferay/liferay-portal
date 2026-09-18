<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<div class="nav-menu overflow-hidden sites-directory-taglib">
	<c:choose>
		<c:when test="<%= sitesDirectoryDisplayContext.isHidden() %>">
			<div class="alert alert-info">
				<liferay-ui:message key="no-sites-were-found" />
			</div>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test='<%= Objects.equals(sitesDirectoryDisplayContext.getDisplayStyle(), "descriptive") || Objects.equals(sitesDirectoryDisplayContext.getDisplayStyle(), "icon") %>'>
					<c:choose>
						<c:when test="<%= Validator.isNull(portletDisplay.getId()) %>">
							<div class="alert alert-info">
								<liferay-ui:message arguments="<%= sitesDirectoryDisplayContext.getDisplayStyle() %>" key="the-display-style-x-cannot-be-used-in-this-context" />
							</div>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container
								searchContainer="<%= sitesDirectoryDisplayContext.getSearchContainer() %>"
							>
								<liferay-ui:search-container-row
									className="com.liferay.portal.kernel.model.Group"
									keyProperty="groupId"
									modelVar="childGroup"
								>
									<c:choose>
										<c:when test='<%= Objects.equals(sitesDirectoryDisplayContext.getDisplayStyle(), "icon") %>'>
											<liferay-ui:search-container-column-text>
												<clay:vertical-card
													verticalCard="<%= new GroupVerticalCard(childGroup, renderRequest) %>"
												/>
											</liferay-ui:search-container-column-text>
										</c:when>
										<c:otherwise>

											<%
											String logoURL = childGroup.getLogoURL(themeDisplay, false);
											%>

											<c:choose>
												<c:when test="<%= Validator.isNotNull(logoURL) %>">
													<liferay-ui:search-container-column-image
														src="<%= logoURL %>"
													/>
												</c:when>
												<c:otherwise>
													<liferay-ui:search-container-column-icon
														icon="sites"
													/>
												</c:otherwise>
											</c:choose>

											<liferay-ui:search-container-column-text
												colspan="<%= 2 %>"
											>
												<div class="h5">
													<aui:a href="<%= (childGroup.getGroupId() != scopeGroupId) ? childGroup.getDisplayURL(themeDisplay) : null %>">
														<%= HtmlUtil.escape(childGroup.getDescriptiveName(locale)) %>
													</aui:a>
												</div>

												<div class="h6 text-default">
													<%= HtmlUtil.escape(childGroup.getDescription(locale)) %>
												</div>

												<div class="h6 text-default">
													<liferay-asset:asset-tags-summary
														className="<%= Group.class.getName() %>"
														classPK="<%= childGroup.getGroupId() %>"
													/>
												</div>

												<div class="h6 text-default">
													<liferay-asset:asset-categories-summary
														className="<%= Group.class.getName() %>"
														classPK="<%= childGroup.getGroupId() %>"
													/>
												</div>
											</liferay-ui:search-container-column-text>
										</c:otherwise>
									</c:choose>
								</liferay-ui:search-container-row>

								<liferay-ui:search-iterator
									displayStyle="<%= sitesDirectoryDisplayContext.getDisplayStyle() %>"
									markupView="lexicon"
								/>
							</liferay-ui:search-container>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>

					<%
					long parentGroupId = GroupConstants.DEFAULT_LIVE_GROUP_ID;

					Group rootGroup = sitesDirectoryDisplayContext.getRootGroup();

					if (rootGroup != null) {
						parentGroupId = rootGroup.getGroupId();
					}

					List<Group> childGroups = GroupLocalServiceUtil.getGroups(themeDisplay.getCompanyId(), parentGroupId, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

					childGroups = ListUtil.filter(childGroups, group -> group.hasPrivateLayouts() || group.hasPublicLayouts());
					%>

					<c:choose>
						<c:when test="<%= childGroups.isEmpty() %>">
							<div class="alert alert-info">
								<liferay-ui:message key="no-sites-were-found" />
							</div>
						</c:when>
						<c:otherwise>
							<ul class="level-1 sites">

								<%
								for (Group childGroup : childGroups) {
									String className = StringPool.BLANK;

									if (Objects.equals(sitesDirectoryDisplayContext.getDisplayStyle(), "list-hierarchy")) {
										className += "open ";
									}

									if (scopeGroupId == childGroup.getGroupId()) {
										className += "font-weight-bold selected";
									}
								%>

									<li class="<%= className %>">
										<c:choose>
											<c:when test="<%= childGroup.getGroupId() != themeDisplay.getScopeGroupId() %>">
												<a class="<%= className %>" href="<%= HtmlUtil.escapeHREF(childGroup.getDisplayURL(themeDisplay, !childGroup.hasPublicLayouts())) %>">
													<%= HtmlUtil.escape(childGroup.getDescriptiveName(themeDisplay.getLocale())) %>
												</a>
											</c:when>
											<c:otherwise>
												<span class="<%= className %>">
													<%= HtmlUtil.escape(childGroup.getDescriptiveName(themeDisplay.getLocale())) %>
												</span>
											</c:otherwise>
										</c:choose>

										<c:if test='<%= Objects.equals(sitesDirectoryDisplayContext.getDisplayStyle(), "list-hierarchy") %>'>

											<%
											request.setAttribute("view.jsp-groupLevel", 2);
											request.setAttribute("view.jsp-parentGroupId", childGroup.getGroupId());
											%>

											<liferay-util:include page="/view_child_groups.jsp" servletContext="<%= application %>" />
										</c:if>
									</li>

								<%
								}
								%>

							</ul>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</div>