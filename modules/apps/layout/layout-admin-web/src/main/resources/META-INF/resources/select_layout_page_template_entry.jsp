<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String backURL = layoutsAdminDisplayContext.getRedirect();

if (Validator.isNull(backURL)) {
	PortletURL portletURL = layoutsAdminDisplayContext.getPortletURL();

	backURL = portletURL.toString();
}

SelectLayoutPageTemplateEntryDisplayContext selectLayoutPageTemplateEntryDisplayContext = (SelectLayoutPageTemplateEntryDisplayContext)request.getAttribute(SelectLayoutPageTemplateEntryDisplayContext.class.getName());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(LanguageUtil.get(request, "select-template"));
%>

<clay:container-fluid
	cssClass="container-view"
	id='<%= liferayPortletResponse.getNamespace() + "layoutPageTemplateEntries" %>'
	size="xxxl"
>
	<clay:row>
		<clay:col
			lg="3"
		>
			<div class="c-mb-3 h5 text-uppercase">
				<liferay-ui:message key="page-template-sets" />
			</div>

			<clay:vertical-nav
				verticalNavItems="<%= layoutsAdminDisplayContext.getVerticalNavItemList(selectLayoutPageTemplateEntryDisplayContext) %>"
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
						<clay:content-col
							expand="<%= true %>"
						>
							<span class="text-uppercase">
								<c:choose>
									<c:when test="<%= selectLayoutPageTemplateEntryDisplayContext.isContentPages() %>">

										<%
										LayoutPageTemplateCollection layoutPageTemplateCollection = LayoutPageTemplateCollectionLocalServiceUtil.fetchLayoutPageTemplateCollection(selectLayoutPageTemplateEntryDisplayContext.getLayoutPageTemplateCollectionId());
										%>

										<c:if test="<%= layoutPageTemplateCollection != null %>">
											<%= HtmlUtil.escape(layoutPageTemplateCollection.getName()) %>
										</c:if>
									</c:when>
									<c:when test="<%= selectLayoutPageTemplateEntryDisplayContext.isBasicTemplates() %>">
										<liferay-ui:message key="basic-templates" />
									</c:when>
									<c:when test="<%= selectLayoutPageTemplateEntryDisplayContext.isGlobalTemplates() %>">
										<liferay-ui:message key="global-templates" />
									</c:when>
								</c:choose>
							</span>
						</clay:content-col>
					</clay:content-row>
				</h2>

				<c:choose>
					<c:when test="<%= selectLayoutPageTemplateEntryDisplayContext.isContentPages() %>">
						<liferay-ui:search-container
							iteratorURL="<%= currentURLObj %>"
							total="<%= selectLayoutPageTemplateEntryDisplayContext.getLayoutPageTemplateEntriesCount() %>"
						>
							<liferay-ui:search-container-results
								results="<%= selectLayoutPageTemplateEntryDisplayContext.getLayoutPageTemplateEntries(searchContainer.getStart(), searchContainer.getEnd()) %>"
							/>

							<liferay-ui:search-container-row
								className="com.liferay.layout.page.template.model.LayoutPageTemplateEntry"
								keyProperty="layoutPageTemplateEntryId"
								modelVar="layoutPageTemplateEntry"
							>
								<liferay-ui:search-container-column-text>
									<react:component
										module="{LayoutPageTemplateEntryCard} from layout-admin-web"
										props="<%= selectLayoutPageTemplateEntryDisplayContext.getLayoutPageTemplateEntryCardProps(layoutPageTemplateEntry) %>"
									/>
								</liferay-ui:search-container-column-text>
							</liferay-ui:search-container-row>

							<liferay-ui:search-iterator
								displayStyle="icon"
								markupView="lexicon"
							/>
						</liferay-ui:search-container>
					</c:when>
					<c:when test="<%= selectLayoutPageTemplateEntryDisplayContext.isBasicTemplates() %>">
						<liferay-util:include page="/select_basic_templates.jsp" servletContext="<%= application %>" />
					</c:when>
					<c:otherwise>
						<liferay-util:include page="/select_global_templates.jsp" servletContext="<%= application %>" />
					</c:otherwise>
				</c:choose>
			</clay:sheet>
		</clay:col>
	</clay:row>
</clay:container-fluid>

<aui:script sandbox="<%= true %>">
	var layoutPageTemplateEntries = document.getElementById(
		'<portlet:namespace />layoutPageTemplateEntries'
	);

	var addLayoutActionOptionQueryClickHandler = Liferay.Util.delegate(
		layoutPageTemplateEntries,
		'click',
		'.add-layout-action-option',
		(event) => {
			Liferay.Util.openModal({
				disableAutoClose: true,
				height: '60vh',
				id: 'addLayoutDialog',
				size: 'md',
				title: '<liferay-ui:message key="<%= layoutsAdminDisplayContext.getAddModalTitle() %>" />',
				url: event.delegateTarget.dataset.addLayoutUrl,
			});
		}
	);

	var addLayoutActionOptionQueryKeyDownHandler = Liferay.Util.delegate(
		layoutPageTemplateEntries,
		'keydown',
		'.add-layout-action-option',
		(event) => {
			if (event.code === 'Space' || event.code === 'Enter') {
				event.preventDefault();
				event.delegateTarget.click();
			}
		}
	);

	function handleDestroyPortlet() {
		addLayoutActionOptionQueryClickHandler.dispose();
		addLayoutActionOptionQueryKeyDownHandler.dispose();

		Liferay.detach('destroyPortlet', handleDestroyPortlet);
	}

	Liferay.on('destroyPortlet', handleDestroyPortlet);
</aui:script>