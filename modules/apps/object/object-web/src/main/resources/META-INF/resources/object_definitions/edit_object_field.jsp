<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DecimalFormat decimalFormat = NumericDDMFormFieldUtil.getDecimalFormat(LocaleUtil.getDefault());

DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();

ObjectDefinition objectDefinition = (ObjectDefinition)request.getAttribute(ObjectWebKeys.OBJECT_DEFINITION);
ObjectDefinitionsFieldsDisplayContext objectDefinitionsFieldsDisplayContext = (ObjectDefinitionsFieldsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
ObjectField objectField = (ObjectField)request.getAttribute(ObjectWebKeys.OBJECT_FIELD);
%>

<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" var="baseResourceURL" />

<react:component
	module="{EditObjectField} from object-web"
	props='<%=
		HashMapBuilder.<String, Object>put(
			"baseResourceURL", String.valueOf(baseResourceURL)
		).put(
			"ckEditor5Config", objectDefinitionsFieldsDisplayContext.getEditorConfig()
		).put(
			"countries", objectDefinitionsFieldsDisplayContext.getCountries(objectField)
		).put(
			"creationLanguageId", objectDefinition.getDefaultLanguageId()
		).put(
			"decimalSeparator", String.valueOf(decimalFormatSymbols.getDecimalSeparator())
		).put(
			"filterOperators", LocalizedJSONArrayUtil.getFilterOperatorsJSONObject(locale)
		).put(
			"forbiddenChars", PropsUtil.getArray(PropsKeys.DL_CHAR_BLACKLIST)
		).put(
			"forbiddenLastChars", objectDefinitionsFieldsDisplayContext.getForbiddenLastCharacters()
		).put(
			"forbiddenNames", PropsUtil.getArray(PropsKeys.DL_NAME_BLACKLIST)
		).put(
			"hasDepotEntry", objectDefinitionsFieldsDisplayContext.hasDepotEntry()
		).put(
			"isDefaultStorageType", objectDefinition.isDefaultStorageType()
		).put(
			"isRootDescendantNode", objectDefinition.isRootDescendantNode()
		).put(
			"learnResources", LearnMessageUtil.getReactDataJSONObject("object-web")
		).put(
			"metadataObjectFieldNames", ObjectFieldUtil.getMetadataObjectFieldNamesJSONArray()
		).put(
			"objectDefinitionExternalReferenceCode", objectDefinition.getExternalReferenceCode()
		).put(
			"objectFieldId", objectField.getObjectFieldId()
		).put(
			"readOnly", !objectDefinitionsFieldsDisplayContext.hasUpdateObjectDefinitionPermission()
		).put(
			"workflowStatuses", LocalizedJSONArrayUtil.getWorkflowStatusJSONArray(locale)
		).build()
	%>'
/>