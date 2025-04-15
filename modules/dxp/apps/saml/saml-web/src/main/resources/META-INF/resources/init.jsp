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
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
page import="com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PropertiesParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.saml.constants.SamlProviderConfigurationKeys" %><%@
page import="com.liferay.saml.constants.SamlWebKeys" %><%@
page import="com.liferay.saml.opensaml.integration.field.expression.handler.UserFieldExpressionHandler" %><%@
page import="com.liferay.saml.opensaml.integration.field.expression.resolver.UserFieldExpressionResolver" %><%@
page import="com.liferay.saml.opensaml.integration.field.expression.resolver.registry.UserFieldExpressionResolverRegistry" %><%@
page import="com.liferay.saml.persistence.exception.DuplicateSamlIdpSpConnectionSamlSpEntityIdException" %><%@
page import="com.liferay.saml.persistence.exception.DuplicateSamlSpIdpConnectionSamlIdpEntityIdException" %><%@
page import="com.liferay.saml.persistence.exception.SamlIdpSpConnectionMetadataUrlException" %><%@
page import="com.liferay.saml.persistence.exception.SamlIdpSpConnectionMetadataXmlException" %><%@
page import="com.liferay.saml.persistence.exception.SamlIdpSpConnectionNameException" %><%@
page import="com.liferay.saml.persistence.exception.SamlIdpSpConnectionSamlSpEntityIdException" %><%@
page import="com.liferay.saml.persistence.exception.SamlSpIdpConnectionMetadataUrlException" %><%@
page import="com.liferay.saml.persistence.exception.SamlSpIdpConnectionMetadataXmlException" %><%@
page import="com.liferay.saml.persistence.exception.SamlSpIdpConnectionSamlIdpEntityIdException" %><%@
page import="com.liferay.saml.persistence.model.SamlIdpSpConnection" %><%@
page import="com.liferay.saml.persistence.model.SamlSpIdpConnection" %><%@
page import="com.liferay.saml.runtime.certificate.CertificateTool" %><%@
page import="com.liferay.saml.runtime.configuration.SamlProviderConfiguration" %><%@
page import="com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper" %><%@
page import="com.liferay.saml.runtime.exception.CertificateKeyPasswordException" %><%@
page import="com.liferay.saml.runtime.metadata.LocalEntityManager" %><%@
page import="com.liferay.saml.util.NameIdTypeValues" %><%@
page import="com.liferay.saml.util.PortletPropsKeys" %><%@
page import="com.liferay.saml.web.internal.display.context.AttributeMappingDisplayContext" %><%@
page import="com.liferay.saml.web.internal.display.context.GeneralTabDefaultViewDisplayContext" %><%@
page import="com.liferay.saml.web.internal.exception.UserAttributeMappingException" %><%@
page import="com.liferay.saml.web.internal.exception.UserIdentifierExpressionException" %><%@
page import="com.liferay.saml.web.internal.util.NameIdTypeValuesUtil" %><%@
page import="com.liferay.saml.web.internal.util.SamlTempFileEntryUtil" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.security.InvalidParameterException" %><%@
page import="java.security.KeyStore" %><%@
page import="java.security.cert.CertificateException" %><%@
page import="java.security.cert.X509Certificate" %>

<%@ page import="java.text.DecimalFormatSymbols" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Date" %><%@
page import="java.util.Enumeration" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String currentURL = PortalUtil.getCurrentURL(request);

LocalEntityManager localEntityManager = (LocalEntityManager)request.getAttribute(LocalEntityManager.class.getName());
NameIdTypeValues nameIdTypeValues = NameIdTypeValuesUtil.getNameIdTypeValues();
GeneralTabDefaultViewDisplayContext generalTabDefaultViewDisplayContext = (GeneralTabDefaultViewDisplayContext)renderRequest.getAttribute(GeneralTabDefaultViewDisplayContext.class.getName());
SamlProviderConfigurationHelper samlProviderConfigurationHelper = (SamlProviderConfigurationHelper)request.getAttribute(SamlProviderConfigurationHelper.class.getName());

SamlProviderConfiguration samlProviderConfiguration = null;

if (samlProviderConfigurationHelper != null) {
	samlProviderConfiguration = samlProviderConfigurationHelper.getSamlProviderConfiguration();
}
%>