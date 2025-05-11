<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewObjectDefinitionsDisplayContext viewObjectDefinitionsDisplayContext = (ViewObjectDefinitionsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" var="baseResourceURL" />

<div>
	<react:component
		module="{ViewObjectDefinitions} from object-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"baseResourceURL",
				URLBuilder.create(
					String.valueOf(baseResourceURL)
				).setParameter(
					"objectFolderName", "Default"
				).build()
			).put(
				"editObjectDefinitionURL", viewObjectDefinitionsDisplayContext.getEditObjectDefinitionURL()
			).put(
				"importObjectDefinitionURL", viewObjectDefinitionsDisplayContext.getImportObjectDefinitionURL()
			).put(
				"importObjectFolderURL", viewObjectDefinitionsDisplayContext.getImportObjectFolderURL()
			).put(
				"learnResourceContext", LearnMessageUtil.getReactDataJSONObject("object-web")
			).put(
				"modelBuilderURL", viewObjectDefinitionsDisplayContext.getModelBuilderURL()
			).put(
				"nameMaxLength", ModelHintsConstants.TEXT_MAX_LENGTH
			).put(
				"objectDefinitionsCreationMenu", viewObjectDefinitionsDisplayContext.getCreationMenu()
			).put(
				"objectDefinitionsFDSActionDropdownItems", viewObjectDefinitionsDisplayContext.getFDSActionDropdownItems()
			).put(
				"objectDefinitionsFDSName", ObjectDefinitionsFDSNames.OBJECT_DEFINITIONS
			).put(
				"objectDefinitionsStorageTypes", viewObjectDefinitionsDisplayContext.getStorageTypesJSONArray()
			).put(
				"objectFolderPermissionsURL", viewObjectDefinitionsDisplayContext.getPermissionsURL(ObjectFolder.class.getName())
			).put(
				"portletNamespace", liferayPortletResponse.getNamespace()
			).build()
		%>'
	/>
</div>