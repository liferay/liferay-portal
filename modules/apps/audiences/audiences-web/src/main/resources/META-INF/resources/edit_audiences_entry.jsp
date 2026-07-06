<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditAudiencesEntryDisplayContext editAudiencesEntryDisplayContext = (EditAudiencesEntryDisplayContext)request.getAttribute(EditAudiencesEntryDisplayContext.class.getName());

renderResponse.setTitle(editAudiencesEntryDisplayContext.getTitle());
%>

<liferay-util:html-top>
	<aui:style type="text/css">
		.control-menu,
		.side-navigation-container {
			display: none;
		}
	</aui:style>
</liferay-util:html-top>

<liferay-ui:error embed="<%= false %>" exception="<%= AudiencesEntryJSONException.class %>" message="you-have-entered-invalid-json" />

<liferay-ui:error embed="<%= false %>" exception="<%= AudiencesEntryNameException.class %>" message="please-enter-a-valid-name" />

<liferay-ui:error embed="<%= false %>" exception="<%= DuplicateAudiencesEntryExternalReferenceCodeException.class %>" message="please-enter-a-unique-external-reference-code" />

<portlet:actionURL name="/audiences/update_audiences_entry" var="updateAudiencesEntryActionURL" />

<aui:form action="<%= updateAudiencesEntryActionURL %>" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= editAudiencesEntryDisplayContext.getRedirect() %>" />
	<aui:input name="audiencesEntryId" type="hidden" value="<%= editAudiencesEntryDisplayContext.getAudiencesEntryId() %>" />

	<div id="<%= liferayPortletResponse.getNamespace() %>-audience-builder-root">
		<div class="inline-item my-5 p-5 w-100">
			<span aria-hidden="true" class="loading-animation"></span>
		</div>

		<react:component
			module="{AudienceBuilder} from audiences-web"
			props="<%= editAudiencesEntryDisplayContext.getData() %>"
		/>
	</div>
</aui:form>