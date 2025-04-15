<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.dynamic.data.mapping.exception.FormInstanceNameException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.FormInstanceSettingsRedirectURLException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.FormInstanceSettingsStorageTypeException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StorageException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StructureDefinitionException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StructureLayoutException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StructureNameException" %><%@
page import="com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants" %><%@
page import="com.liferay.dynamic.data.mapping.form.web.internal.FormInstanceFieldSettingsException" %><%@
page import="com.liferay.dynamic.data.mapping.form.web.internal.display.context.DDMFormAdminDisplayContext" %><%@
page import="com.liferay.dynamic.data.mapping.form.web.internal.display.context.DDMFormViewFormInstanceRecordsDisplayContext" %><%@
page import="com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper.FieldSetPermissionCheckerHelper" %><%@
page import="com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper.FormInstancePermissionCheckerHelper" %><%@
page import="com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormInstanceExpirationStatusUtil" %><%@
page import="com.liferay.dynamic.data.mapping.form.web.internal.search.DDMFormInstanceRowChecker" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMForm" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMFormField" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMFormInstance" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMStructure" %><%@
page import="com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue" %><%@
page import="com.liferay.dynamic.data.mapping.storage.DDMFormValues" %><%@
page import="com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidationException" %><%@
page import="com.liferay.dynamic.data.mapping.validator.DDMFormValidationException" %><%@
page import="com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException" %><%@
page import="com.liferay.object.model.ObjectDefinition" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker" %><%@
page import="com.liferay.portal.kernel.exception.ModelListenerException" %><%@
page import="com.liferay.portal.kernel.json.JSONArray" %><%@
page import="com.liferay.portal.kernel.json.JSONObject" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.upload.FileItem" %><%@
page import="com.liferay.portal.kernel.upload.LiferayFileItemException" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.taglib.search.DateSearchEntry" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.Set" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
DDMFormAdminDisplayContext ddmFormAdminDisplayContext = (DDMFormAdminDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

String dataProviderInstanceParameterSettingsURL = ddmFormAdminDisplayContext.getDataProviderInstanceParameterSettingsURL();
String dataProviderInstancesURL = ddmFormAdminDisplayContext.getDataProviderInstancesURL();
String displayStyle = ddmFormAdminDisplayContext.getDisplayStyle();
JSONObject functionsMetadataJSONObject = ddmFormAdminDisplayContext.getFunctionsMetadataJSONObject();
String functionsURL = ddmFormAdminDisplayContext.getFunctionsURL();
String rolesURL = ddmFormAdminDisplayContext.getRolesURL();
JSONArray ddmFormRulesJSONArray = ddmFormAdminDisplayContext.getDDMFormRulesJSONArray();
%>

<%@ include file="/admin/init-ext.jsp" %>

<aui:script>
	Liferay.namespace('Forms').portletNamespace = '<portlet:namespace />';
</aui:script>