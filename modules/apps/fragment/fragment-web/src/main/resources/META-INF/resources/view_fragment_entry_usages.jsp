<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
FragmentEntryLinkDisplayContext fragmentEntryLinkDisplayContext = new FragmentEntryLinkDisplayContext(request, renderRequest, renderResponse);

FragmentEntry fragmentEntry = fragmentEntryLinkDisplayContext.getFragmentEntry();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(fragmentEntryLinkDisplayContext.getRedirect());
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(LanguageUtil.format(request, "usages-and-propagation-x", fragmentEntry.getName()));
%>

<clay:container-fluid
	cssClass="container-form-lg"
	size="xxxl"
>
	<clay:row>
		<clay:col
			lg="3"
		>
			<strong class="text-uppercase">
				<liferay-ui:message key="usages" />
			</strong>

			<clay:vertical-nav
				verticalNavItems="<%= fragmentEntryLinkDisplayContext.getVerticalNavItemList() %>"
			/>
		</clay:col>

		<clay:col
			lg="9"
		>
			<clay:sheet
				size="full"
			>
				<h2 class="sheet-title">
					<clay:content-row
						verticalAlign="center"
					>
						<clay:content-col>
							<c:choose>
								<c:when test='<%= Objects.equals(fragmentEntryLinkDisplayContext.getNavigation(), "pages") %>'>
									<liferay-ui:message arguments="<%= fragmentEntryLinkDisplayContext.getPagesUsageCount() %>" key="pages-x" />
								</c:when>
								<c:when test='<%= Objects.equals(fragmentEntryLinkDisplayContext.getNavigation(), "master-pages") %>'>
									<liferay-ui:message arguments="<%= fragmentEntryLinkDisplayContext.getMasterPagesUsageCount() %>" key="master-pages-x" />
								</c:when>
								<c:when test='<%= Objects.equals(fragmentEntryLinkDisplayContext.getNavigation(), "page-templates") %>'>
									<liferay-ui:message arguments="<%= fragmentEntryLinkDisplayContext.getPageTemplatesUsageCount() %>" key="page-templates-x" />
								</c:when>
								<c:when test='<%= Objects.equals(fragmentEntryLinkDisplayContext.getNavigation(), "display-page-templates") %>'>
									<liferay-ui:message arguments="<%= fragmentEntryLinkDisplayContext.getDisplayPagesUsageCount() %>" key="display-page-templates-x" />
								</c:when>
								<c:otherwise>
									<liferay-ui:message arguments="<%= fragmentEntryLinkDisplayContext.getAllUsageCount() %>" key="all-x" />
								</c:otherwise>
							</c:choose>
						</clay:content-col>
					</clay:content-row>
				</h2>

				<clay:management-toolbar
					managementToolbarDisplayContext="<%= new FragmentEntryUsageManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, fragmentEntryLinkDisplayContext.getSearchContainer()) %>"
					propsTransformer="{FragmentEntryUsagesManagementToolbarPropsTransformer} from fragment-web"
				/>

				<portlet:actionURL name="/fragment/propagate_fragment_entry_changes" var="propagateFragmentEntryChangesURL">
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:actionURL>

				<aui:form action="<%= propagateFragmentEntryChangesURL %>" name="fm">
					<liferay-ui:search-container
						searchContainer="<%= fragmentEntryLinkDisplayContext.getSearchContainer() %>"
					>
						<liferay-ui:search-container-row
							className="com.liferay.fragment.model.FragmentEntryLink"
							keyPropertyName="fragmentEntryLinkId"
							modelVar="fragmentEntryLink"
						>
							<liferay-ui:search-container-column-text
								name="name"
								value="<%= HtmlUtil.escape(fragmentEntryLinkDisplayContext.getFragmentEntryLinkName(fragmentEntryLink)) %>"
							/>

							<liferay-ui:search-container-column-text
								name="type"
								translate="<%= true %>"
								value="<%= fragmentEntryLinkDisplayContext.getFragmentEntryLinkTypeLabel(fragmentEntryLink) %>"
							/>

							<liferay-ui:search-container-column-text
								name="using"
							>
								<span class="label <%= fragmentEntryLink.isLatestVersion() ? "label-success" : "label-info" %>">
									<liferay-ui:message key='<%= fragmentEntryLink.isLatestVersion() ? "latest-version" : "a-previous-version" %>' />
								</span>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-date
								name="last-propagation"
								value="<%= fragmentEntryLink.getLastPropagationDate() %>"
							/>
						</liferay-ui:search-container-row>

						<liferay-ui:search-iterator
							displayStyle="list"
							markupView="lexicon"
							searchResultCssClass="show-quick-actions-on-hover table table-autofit"
						/>
					</liferay-ui:search-container>
				</aui:form>
			</clay:sheet>
		</clay:col>
	</clay:row>
</clay:container-fluid>