<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DDMTemplate template = (DDMTemplate)request.getAttribute(DDMWebKeys.DYNAMIC_DATA_MAPPING_TEMPLATE);

String portletResource = ParamUtil.getString(request, "portletResource");
long templateId = BeanParamUtil.getLong(template, request, "templateId");
long classNameId = BeanParamUtil.getLong(template, request, "classNameId");
long classPK = BeanParamUtil.getLong(template, request, "classPK");
long resourceClassNameId = BeanParamUtil.getLong(template, request, "resourceClassNameId");

DDMTemplateVersion templateVersion = template.getTemplateVersion();

boolean showBackURL = ParamUtil.getBoolean(request, "showBackURL", true);
%>

<portlet:actionURL name="/dynamic_data_mapping/copy_template" var="copyTemplateURL">
	<portlet:param name="mvcPath" value="/copy_template.jsp" />
</portlet:actionURL>

<aui:form action="<%= copyTemplateURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= ddmDisplay.getEditTemplateBackURL(liferayPortletRequest, liferayPortletResponse, classNameId, classPK, resourceClassNameId, portletResource) %>" />

	<aui:input name="templateId" type="hidden" value="<%= String.valueOf(templateId) %>" />
	<aui:input name="status" type="hidden" value="<%= templateVersion.getStatus() %>" />

	<liferay-ui:error exception="<%= TemplateNameException.class %>" message="please-enter-a-valid-name" />

	<c:if test="<%= showBackURL && ddmDisplay.isShowBackURLInTitleBar() %>">

		<%
		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(ddmDisplay.getEditTemplateBackURL(liferayPortletRequest, liferayPortletResponse, classNameId, classPK, resourceClassNameId, portletResource));

		renderResponse.setTitle(LanguageUtil.get(request, "copy-template"));
		%>

	</c:if>

	<aui:model-context bean="<%= template %>" model="<%= DDMTemplate.class %>" />

	<div class="sheet">
		<div class="panel-group panel-group-flush">
			<aui:fieldset>
				<aui:input name="name" />

				<aui:input name="description" />
			</aui:fieldset>
		</div>
	</div>

	<aui:button-row>
		<aui:button type="submit" value="copy" />

		<aui:button href="<%= ddmDisplay.getEditTemplateBackURL(liferayPortletRequest, liferayPortletResponse, classNameId, classPK, resourceClassNameId, portletResource) %>" type="cancel" />
	</aui:button-row>
</aui:form>