<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LocalizedItemSelectorRendering localizedItemSelectorRendering = LocalizedItemSelectorRendering.get(liferayPortletRequest);

List<NavigationItem> navigationItems = localizedItemSelectorRendering.getNavigationItems();
%>

<c:choose>
	<c:when test="<%= navigationItems.isEmpty() %>">

		<%
		if (_log.isWarnEnabled()) {
			String[] criteria = ParamUtil.getParameterValues(renderRequest, "criteria");

			_log.warn("No item selector views found for " + StringUtil.merge(criteria, StringPool.COMMA_AND_SPACE));
		}
		%>

		<div class="alert alert-info">
			<liferay-ui:message key="selection-is-not-available" />
		</div>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="<%= navigationItems.size() > 1 %>">
				<c:choose>
					<c:when test="<%= navigationItems.size() <= 5 %>">
						<clay:navigation-bar
							cssClass="border-bottom"
							inverted="<%= false %>"
							navigationItems="<%= navigationItems %>"
						/>

						<liferay-util:include page="/view_item_selector.jsp" servletContext="<%= application %>" />
					</c:when>
					<c:otherwise>
						<clay:container-fluid
							cssClass="container-view"
						>
							<clay:row>
								<clay:col
									md="3"
								>
									<clay:vertical-nav
										verticalNavItems="<%= localizedItemSelectorRendering.getVerticalNavItemList() %>"
									/>
								</clay:col>

								<clay:col
									md="9"
								>
									<clay:sheet
										size="full"
									>

										<%
										NavigationItem activeNavigationItem = localizedItemSelectorRendering.getActiveNavigationItem();
										%>

										<c:if test="<%= activeNavigationItem != null %>">
											<h2 class="sheet-title">
												<clay:content-row
													verticalAlign="center"
												>
													<clay:content-col>
														<%= GetterUtil.getString(activeNavigationItem.get("label")) %>
													</clay:content-col>
												</clay:content-row>
											</h2>
										</c:if>

										<clay:sheet-section>
											<liferay-util:include page="/view_item_selector.jsp" servletContext="<%= application %>" />
										</clay:sheet-section>
									</clay:sheet>
								</clay:col>
							</clay:row>
						</clay:container-fluid>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<liferay-util:include page="/view_item_selector.jsp" servletContext="<%= application %>" />
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_item_selector_web.view_jsp");
%>