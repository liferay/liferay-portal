<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

List<LayoutPageTemplateEntry> masterLayoutPageTemplateEntries = new ArrayList<>();

LayoutPageTemplateEntry layoutPageTemplateEntry = LayoutPageTemplateEntryLocalServiceUtil.createLayoutPageTemplateEntry(0);

layoutPageTemplateEntry.setName(LanguageUtil.get(request, "blank"));
layoutPageTemplateEntry.setStatus(WorkflowConstants.STATUS_APPROVED);

masterLayoutPageTemplateEntries.add(layoutPageTemplateEntry);

masterLayoutPageTemplateEntries.addAll(LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(scopeGroupId, LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

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
			for (LayoutPageTemplateEntry masterLayoutPageTemplateEntry : masterLayoutPageTemplateEntries) {
			%>

				<li class="card-page-item card-page-item-asset">

					<%
					SelectLayoutPageTemplateEntryMasterLayoutVerticalCard selectLayoutPageTemplateEntryMasterLayoutVerticalCard = new SelectLayoutPageTemplateEntryMasterLayoutVerticalCard(masterLayoutPageTemplateEntry, renderRequest, renderResponse);
					%>

					<clay:vertical-card
						additionalProps='<%=
							HashMapBuilder.<String, Object>put(
								"addLayoutPageTemplateEntryURL", selectLayoutPageTemplateEntryMasterLayoutVerticalCard.getAddLayoutPageTemplateEntryURL()
							).put(
								"dialogTitle", LanguageUtil.get(request, "add-page-template")
							).put(
								"mainFieldLabel", LanguageUtil.get(request, "name")
							).put(
								"mainFieldName", "name"
							).put(
								"mainFieldPlaceholder", LanguageUtil.get(request, "name")
							).build()
						%>'
						propsTransformer="{SelectLayoutPageTemplateEntryMasterLayoutVerticalCardPropsTransformer} from layout-page-template-admin-web"
						verticalCard="<%= selectLayoutPageTemplateEntryMasterLayoutVerticalCard %>"
					/>
				</li>

			<%
			}
			%>

		</ul>
	</div>
</clay:container-fluid>