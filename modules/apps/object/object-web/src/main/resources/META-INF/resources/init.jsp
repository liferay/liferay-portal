<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/friendly-url" prefix="liferay-friendly-url" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.learn.LearnMessageUtil" %><%@
page import="com.liferay.list.type.model.ListTypeDefinition" %><%@
page import="com.liferay.object.constants.ObjectLayoutBoxConstants" %><%@
page import="com.liferay.object.constants.ObjectWebKeys" %><%@
page import="com.liferay.object.display.context.ObjectEntryDisplayContext" %><%@
page import="com.liferay.object.model.ObjectAction" %><%@
page import="com.liferay.object.model.ObjectDefinition" %><%@
page import="com.liferay.object.model.ObjectEntry" %><%@
page import="com.liferay.object.model.ObjectField" %><%@
page import="com.liferay.object.model.ObjectFolder" %><%@
page import="com.liferay.object.model.ObjectLayout" %><%@
page import="com.liferay.object.model.ObjectLayoutBox" %><%@
page import="com.liferay.object.model.ObjectRelationship" %><%@
page import="com.liferay.object.model.ObjectValidationRule" %><%@
page import="com.liferay.object.model.ObjectView" %><%@
page import="com.liferay.object.web.internal.list.type.constants.ListTypeFDSNames" %><%@
page import="com.liferay.object.web.internal.list.type.display.context.ViewListTypeDefinitionsDisplayContext" %><%@
page import="com.liferay.object.web.internal.list.type.display.context.ViewListTypeEntriesDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.constants.ObjectDefinitionsFDSNames" %><%@
page import="com.liferay.object.web.internal.object.definitions.constants.ObjectDefinitionsScreenNavigationEntryConstants" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsActionsDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsDetailsDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsFieldsDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsLayoutsDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsRelationshipsDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsStateManagerDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsValidationsDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsViewsDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.ViewObjectDefinitionsDisplayContext" %><%@
page import="com.liferay.object.web.internal.object.definitions.display.context.util.LocalizedJSONArrayUtil" %><%@
page import="com.liferay.object.web.internal.object.entries.constants.ObjectEntriesFDSNames" %><%@
page import="com.liferay.object.web.internal.object.entries.display.context.ViewObjectEntriesDisplayContext" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.LayoutConstants" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsConstants" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.url.URLBuilder" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.LocalizationUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsKeys" %><%@
page import="com.liferay.portal.kernel.util.PropsUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %>

<%@ page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />