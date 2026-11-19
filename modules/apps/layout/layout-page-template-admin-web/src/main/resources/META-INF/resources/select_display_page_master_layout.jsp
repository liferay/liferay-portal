<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

DisplayPageDisplayContext displayPageDisplayContext = (DisplayPageDisplayContext)request.getAttribute(DisplayPageDisplayContext.class.getName());

if (displayPageDisplayContext == null) {
	InfoItemServiceRegistry infoItemServiceRegistry = (InfoItemServiceRegistry)request.getAttribute(InfoItemServiceRegistry.class.getName());

	displayPageDisplayContext = new DisplayPageDisplayContext(request, infoItemServiceRegistry, liferayPortletRequest, liferayPortletResponse);
}

SelectDisplayPageMasterLayoutDisplayContext selectDisplayPageMasterLayoutDisplayContext = new SelectDisplayPageMasterLayoutDisplayContext(request);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(LanguageUtil.get(request, "select-master-page"));
%>

<clay:container-fluid
	cssClass="container-view"
>
	<div class="lfr-search-container-wrapper">
		<ul class="card-page card-page-equal-height">

			<%
			for (LayoutPageTemplateEntry masterLayoutPageTemplateEntry : selectDisplayPageMasterLayoutDisplayContext.getMasterLayoutPageTemplateEntries()) {
			%>

				<li class="card-page-item card-page-item-asset">

					<%
					SelectDisplayPageMasterLayoutVerticalCard selectDisplayPageMasterLayoutVerticalCard = new SelectDisplayPageMasterLayoutVerticalCard(masterLayoutPageTemplateEntry, renderRequest, renderResponse);
					%>

					<clay:vertical-card
						additionalProps='<%=
							HashMapBuilder.<String, Object>put(
								"addDisplayPageURL", selectDisplayPageMasterLayoutVerticalCard.getAddDisplayPageURL()
							).put(
								"mappingTypes", displayPageDisplayContext.getMappingTypesJSONArray()
							).put(
								"title", LanguageUtil.get(request, "add-display-page-template")
							).build()
						%>'
						propsTransformer="{SelectDisplayPageMasterLayoutVerticalCardPropsTransformer} from layout-page-template-admin-web"
						verticalCard="<%= selectDisplayPageMasterLayoutVerticalCard %>"
					/>
				</li>

			<%
			}
			%>

		</ul>
	</div>
</clay:container-fluid>