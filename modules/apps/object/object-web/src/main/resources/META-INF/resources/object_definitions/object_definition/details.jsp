<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ObjectDefinition objectDefinition = (ObjectDefinition)request.getAttribute(ObjectWebKeys.OBJECT_DEFINITION);

ObjectDefinitionsDetailsDisplayContext objectDefinitionsDetailsDisplayContext = (ObjectDefinitionsDetailsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(
	ParamUtil.getString(
		request, "backURL",
		URLBuilder.create(
			String.valueOf(renderResponse.createRenderURL())
		).setParameter(
			"objectFolderName", objectDefinitionsDetailsDisplayContext.getObjectFolderName()
		).build()));

renderResponse.setTitle(LanguageUtil.format(request, "edit-x", objectDefinition.getLabel(locale, true), false));
%>

<div id="<portlet:namespace />EditObjectDefinition">
	<react:component
		module="{EditObjectDetails} from object-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"backURL", portletDisplay.getURLBack()
			).put(
				"companies", objectDefinitionsDetailsDisplayContext.getScopeJSONArray("company")
			).put(
				"dbTableName", objectDefinition.getDBTableName()
			).put(
				"hasPublishObjectPermission", objectDefinitionsDetailsDisplayContext.hasPublishObjectPermission()
			).put(
				"hasUpdateObjectDefinitionPermission", objectDefinitionsDetailsDisplayContext.hasUpdateObjectDefinitionPermission()
			).put(
				"isApproved", objectDefinition.isApproved()
			).put(
				"isRootDescendantNode", objectDefinition.isRootDescendantNode()
			).put(
				"isRootNode", objectDefinition.isRootNode()
			).put(
				"label", LocalizationUtil.getLocalizationMap(objectDefinition.getLabel())
			).put(
				"nonRelationshipObjectFieldsInfo", objectDefinitionsDetailsDisplayContext.getNonrelationshipObjectFieldsInfo()
			).put(
				"objectDefinitionExternalReferenceCode", objectDefinition.getExternalReferenceCode()
			).put(
				"objectDefinitionId", objectDefinition.getObjectDefinitionId()
			).put(
				"pluralLabel", LocalizationUtil.getLocalizationMap(objectDefinition.getPluralLabel())
			).put(
				"portletNamespace", liferayPortletResponse.getNamespace()
			).put(
				"shortName", objectDefinition.getShortName()
			).put(
				"sites", objectDefinitionsDetailsDisplayContext.getScopeJSONArray("site")
			).put(
				"storageTypes", objectDefinitionsDetailsDisplayContext.getStorageTypesJSONArray()
			).build()
		%>'
	/>
</div>