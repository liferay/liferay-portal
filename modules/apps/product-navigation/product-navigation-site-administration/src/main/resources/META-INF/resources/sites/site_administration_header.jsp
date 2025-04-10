<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SiteAdministrationPanelCategoryDisplayContext siteAdministrationPanelCategoryDisplayContext = new SiteAdministrationPanelCategoryDisplayContext(liferayPortletRequest, null);

Group group = siteAdministrationPanelCategoryDisplayContext.getGroup();
PanelCategory panelCategory = siteAdministrationPanelCategoryDisplayContext.getPanelCategory();

int childPanelCategoriesSize = GetterUtil.getInteger(request.getAttribute("product_menu.jsp-childPanelCategoriesSize"));
%>

<c:choose>
	<c:when test="<%= group != null %>">
		<c:choose>
			<c:when test="<%= childPanelCategoriesSize > 1 %>">
				<%@ include file="/sites/site_administration_header_icon_sites.jspf" %>

				<aui:style type="text/css">
					.site-administration--header {
						background-image: url(<%= siteAdministrationPanelCategoryDisplayContext.getLogoURL() %>) !important;
					}
				</aui:style>

				<a aria-controls="<portlet:namespace /><%= AUIUtil.normalizeId(panelCategory.getKey()) %>Collapse" aria-expanded="<%= siteAdministrationPanelCategoryDisplayContext.isCollapsedPanel() %>" class="panel-toggler <%= (group != null) ? "collapse-icon collapse-icon-middle " : StringPool.BLANK %> <%= siteAdministrationPanelCategoryDisplayContext.isCollapsedPanel() ? StringPool.BLANK : "collapsed" %> site-administration-toggler" data-parent="#<portlet:namespace />Accordion" data-qa-id="productMenuSiteAdministrationPanelCategory" data-toggle="liferay-collapse" href="#<portlet:namespace /><%= AUIUtil.normalizeId(panelCategory.getKey()) %>Collapse" id="<portlet:namespace /><%= AUIUtil.normalizeId(panelCategory.getKey()) %>Toggler" <%= (group != null) ? "role=\"button\"" : StringPool.BLANK %>>
					<clay:content-row
						verticalAlign="center"
					>
						<clay:content-col>
							<c:choose>
								<c:when test="<%= Validator.isNotNull(siteAdministrationPanelCategoryDisplayContext.getLogoURL()) %>">
									<div class="aspect-ratio-bg-cover site-administration--header sticker"></div>
								</c:when>
								<c:otherwise>
									<clay:sticker
										displayType="secondary"
										icon="<%= group.getIconCssClass() %>"
									/>
								</c:otherwise>
							</c:choose>
						</clay:content-col>

						<clay:content-col
							cssClass="mr-4"
							expand="<%= true %>"
						>
							<div class="lfr-portal-tooltip site-name text-truncate" title="<%= HtmlUtil.escapeAttribute(siteAdministrationPanelCategoryDisplayContext.getGroupName()) %>">
								<%= HtmlUtil.escape(siteAdministrationPanelCategoryDisplayContext.getGroupName()) %>

								<c:if test="<%= siteAdministrationPanelCategoryDisplayContext.isShowStagingInfo() && !group.isStagedRemotely() %>">
									<span class="site-sub-name"> - <liferay-ui:message key="<%= siteAdministrationPanelCategoryDisplayContext.getStagingLabel() %>" /></span>
								</c:if>
							</div>
						</clay:content-col>
					</clay:content-row>

					<c:if test="<%= siteAdministrationPanelCategoryDisplayContext.getNotificationsCount() > 0 %>">
						<clay:sticker
							cssClass="panel-notifications-count"
							displayType="warning"
							position="top-right"
							size="sm"
						>
							<%= siteAdministrationPanelCategoryDisplayContext.getNotificationsCount() %>
						</clay:sticker>
					</c:if>

					<clay:icon
						cssClass="collapse-icon-closed"
						symbol="angle-right"
					/>

					<clay:icon
						cssClass="collapse-icon-open"
						symbol="angle-down"
					/>
				</a>
			</c:when>
			<c:otherwise>
				<%@ include file="/sites/site_administration_header_no_collapsible.jspf" %>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="<%= siteAdministrationPanelCategoryDisplayContext.isShowSiteSelector() %>">
		<%@ include file="/sites/site_administration_header_icon_sites.jspf" %>

		<div class="collapsed panel-toggler">
			<span class="site-name">
				<liferay-ui:message key="choose-a-site" />
			</span>
		</div>
	</c:when>
</c:choose>