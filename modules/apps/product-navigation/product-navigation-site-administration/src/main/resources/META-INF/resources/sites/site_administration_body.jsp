<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PanelCategory panelCategory = (PanelCategory)request.getAttribute(ApplicationListWebKeys.PANEL_CATEGORY);

SiteAdministrationPanelCategoryDisplayContext siteAdministrationPanelCategoryDisplayContext = new SiteAdministrationPanelCategoryDisplayContext(liferayPortletRequest, null);
%>

<c:if test="<%= siteAdministrationPanelCategoryDisplayContext.getGroup() != null %>">
	<clay:row
		cssClass="navigation-link-container"
	>
		<clay:col
			md="12"
		>
			<c:if test="<%= siteAdministrationPanelCategoryDisplayContext.isShowStagingInfo() %>">

				<%
				Map<String, Object> data = HashMapBuilder.<String, Object>put(
					"qa-id", "staging"
				).build();
				%>

				<div class="float-right staging-links">
					<span class="<%= Validator.isNull(siteAdministrationPanelCategoryDisplayContext.getStagingGroupURL()) ? "active" : StringPool.BLANK %>">
						<aui:a data="<%= data %>" href="<%= siteAdministrationPanelCategoryDisplayContext.getStagingGroupURL() %>" label="staging" />
					</span>
					<span class="links-separator"> |</span>

					<%
					data.put("qa-id", "live");

					try {
						String liveGroupURL = siteAdministrationPanelCategoryDisplayContext.getLiveGroupURL();
					%>

						<span class="<%= Validator.isNull(liveGroupURL) ? "active" : StringPool.BLANK %>">
							<aui:a data="<%= data %>" href="<%= liveGroupURL %>" label="<%= siteAdministrationPanelCategoryDisplayContext.getLiveGroupLabel() %>" />
						</span>

					<%
					}
					catch (RemoteExportException | SystemException e) {
						if (e instanceof SystemException) {
							_log.error(e);
						}
					%>

						<aui:a data="<%= data %>" href="" id="remoteLiveLink" label="<%= siteAdministrationPanelCategoryDisplayContext.getLiveGroupLabel() %>" />

						<aui:script use="aui-tooltip">
							new A.Tooltip({
								bodyContent: Liferay.Language.get(
									'the-connection-to-the-remote-live-site-cannot-be-established-due-to-a-network-problem'
								),
								position: 'right',
								trigger: A.one('#<portlet:namespace />remoteLiveLink'),
								visible: false,
								zIndex: Liferay.zIndex.TOOLTIP,
							}).render();
						</aui:script>

					<%
					}
					%>

				</div>
			</c:if>

			<c:if test="<%= siteAdministrationPanelCategoryDisplayContext.isDisplaySiteLink() %>">
				<clay:link
					cssClass='<%= "list-group-heading navigation-link panel-header-link" + (siteAdministrationPanelCategoryDisplayContext.isFirstLayout() ? " first-layout" : "") %>'
					href="<%= siteAdministrationPanelCategoryDisplayContext.getGroupURL() %>"
					icon="home"
					label="home"
				/>
			</c:if>

			<c:if test="<%= siteAdministrationPanelCategoryDisplayContext.isShowLayoutsTree() %>">
				<clay:button
					cssClass="list-group-heading navigation-link panel-header-link"
					disabled="<%= siteAdministrationPanelCategoryDisplayContext.isLayoutsTreeDisabled() %>"
					displayType="unstyled"
					icon="pages-tree"
					id='<%= liferayPortletResponse.getNamespace() + "pagesTreeSidenavToggleId" %>'
					label="page-tree"
				/>
			</c:if>
		</clay:col>
	</clay:row>

	<c:if test="<%= siteAdministrationPanelCategoryDisplayContext.isShowSiteAdministration() %>">
		<liferay-application-list:panel-category-body
			panelCategory="<%= panelCategory %>"
		/>
	</c:if>
</c:if>

<c:if test="<%= (siteAdministrationPanelCategoryDisplayContext.getGroup() != null) && siteAdministrationPanelCategoryDisplayContext.isShowLayoutsTree() %>">
	<aui:script sandbox="<%= true %>">
		var pagesTreeToggle = document.getElementById(
			'<portlet:namespace />pagesTreeSidenavToggleId'
		);

		pagesTreeToggle.addEventListener('click', (event) => {
			Liferay.Portlet.destroy('#p_p_id<portlet:namespace />');

			Liferay.Util.Session.set(
				'com.liferay.product.navigation.product.menu.web_pagesTreeState',
				'open'
			).then(() => {
				Liferay.Util.fetch(
					'<%= siteAdministrationPanelCategoryDisplayContext.getPageTreeURL() %>'
				)
					.then((response) => {
						if (!response.ok) {
							throw new Error(
								'<liferay-ui:message key="an-unexpected-error-occurred" />'
							);
						}

						return response.text();
					})
					.then((response) => {
						var sidebar = document.querySelector(
							'.lfr-product-menu-sidebar .sidebar-body'
						);

						sidebar.innerHTML = '';

						response = Liferay.Util.setCSPNonce(response);

						var range = document.createRange();
						range.selectNode(sidebar);

						var fragment = range.createContextualFragment(response);

						var pagesTree = document.createElement('div');
						pagesTree.setAttribute('class', 'pages-tree');
						pagesTree.appendChild(fragment);

						sidebar.appendChild(pagesTree);
					});
			});
		});
	</aui:script>
</c:if>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_product_navigation_site_administration.sites.site_administration_body_jsp");
%>