<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SelectSiteInitializerDisplayContext selectSiteInitializerDisplayContext = new SelectSiteInitializerDisplayContext(request, renderRequest, renderResponse);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(selectSiteInitializerDisplayContext.getBackURL());
portletDisplay.setURLBackTitle(ParamUtil.getString(request, "backURLTitle"));

renderResponse.setTitle(LanguageUtil.get(request, "select-template"));
%>

<clay:container-fluid
	cssClass="container-view"
	fullWidth="<%= true %>"
>
	<clay:row>
		<clay:col
			lg="3"
		>
			<clay:vertical-nav
				verticalNavItems="<%= selectSiteInitializerDisplayContext.getVerticalNavItemList() %>"
			/>
		</clay:col>

		<clay:col
			lg="9"
		>
			<clay:sheet
				size="full"
			>
				<h2 class="sheet-title"><%= HtmlUtil.escape(selectSiteInitializerDisplayContext.getTitle()) %></h2>

				<div class="sheet-text">
					<liferay-ui:message key="select-the-template-to-create-your-site" />
				</div>

				<aui:form name="fm">
					<liferay-ui:search-container
						searchContainer="<%= selectSiteInitializerDisplayContext.getSearchContainer() %>"
					>
						<liferay-ui:search-container-row
							className="com.liferay.site.admin.web.internal.util.SiteInitializerItem"
							keyProperty="key"
							modelVar="siteInitializerItem"
						>
							<liferay-ui:search-container-column-text>
								<clay:vertical-card
									propsTransformer="{SelectSiteInitializerVerticalCardPropsTransformer} from site-admin-web"
									verticalCard="<%= new SelectSiteInitializerVerticalCard(siteInitializerItem, renderRequest, renderResponse) %>"
								/>
							</liferay-ui:search-container-column-text>
						</liferay-ui:search-container-row>

						<liferay-ui:search-iterator
							displayStyle="icon"
							markupView="lexicon"
						/>
					</liferay-ui:search-container>
				</aui:form>
			</clay:sheet>
		</clay:col>
	</clay:row>
</clay:container-fluid>