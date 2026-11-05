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
taglib uri="http://liferay.com/tld/social-activities" prefix="liferay-social-activities" %><%@
taglib uri="http://liferay.com/tld/text-localizer" prefix="liferay-text-localizer" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.admin.kernel.util.PortalSearchApplicationType" %><%@
page import="com.liferay.asset.kernel.model.AssetTag" %><%@
page import="com.liferay.asset.kernel.service.AssetTagLocalServiceUtil" %><%@
page import="com.liferay.contacts.constants.ContactsConstants" %><%@
page import="com.liferay.contacts.constants.ContactsWebKeys" %><%@
page import="com.liferay.contacts.model.Entry" %><%@
page import="com.liferay.contacts.service.EntryLocalServiceUtil" %><%@
page import="com.liferay.contacts.service.EntryServiceUtil" %><%@
page import="com.liferay.contacts.util.ContactsExtensionsUtil" %><%@
page import="com.liferay.contacts.util.ContactsUtil" %><%@
page import="com.liferay.contacts.web.internal.constants.ContactsPortletKeys" %><%@
page import="com.liferay.petra.string.CharPool" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Address" %><%@
page import="com.liferay.portal.kernel.model.BaseModel" %><%@
page import="com.liferay.portal.kernel.model.Contact" %><%@
page import="com.liferay.portal.kernel.model.EmailAddress" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.GroupConstants" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.model.ListType" %><%@
page import="com.liferay.portal.kernel.model.Phone" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.model.Website" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.PortletProvider" %><%@
page import="com.liferay.portal.kernel.portlet.PortletProviderUtil" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.AddressServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.EmailAddressServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.PhoneServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.WebsiteServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.UserPermissionUtil" %><%@
page import="com.liferay.portal.kernel.servlet.ServletContextPool" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.LinkedHashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PortletKeys" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.comparator.UserLastNameComparator" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.product.navigation.personal.menu.util.PersonalApplicationURLUtil" %><%@
page import="com.liferay.social.kernel.model.SocialRelationConstants" %><%@
page import="com.liferay.social.kernel.model.SocialRequestConstants" %><%@
page import="com.liferay.social.kernel.service.SocialActivityLocalServiceUtil" %><%@
page import="com.liferay.social.kernel.service.SocialRelationLocalServiceUtil" %><%@
page import="com.liferay.social.kernel.service.SocialRequestLocalServiceUtil" %>

<%@ page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %><%@
page import="jakarta.portlet.WindowState" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.LinkedHashMap" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Set" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String currentURL = PortalUtil.getCurrentURL(request);

String portletResource = ParamUtil.getString(request, "portletResource");

int displayStyle = GetterUtil.getInteger(portletPreferences.getValue("displayStyle", null), ContactsConstants.DISPLAY_STYLE_FULL);
boolean showAdditionalEmailAddresses = GetterUtil.getBoolean(portletPreferences.getValue("showAdditionalEmailAddresses", null), true);
boolean showAddresses = GetterUtil.getBoolean(portletPreferences.getValue("showAddresses", null), true);
boolean showComments = GetterUtil.getBoolean(portletPreferences.getValue("showComments", null), true);
boolean showCompleteYourProfile = GetterUtil.getBoolean(portletPreferences.getValue("showCompleteYourProfile", null));
boolean showEmailAddress = GetterUtil.getBoolean(portletPreferences.getValue("showEmailAddress", null));
boolean showIcon = GetterUtil.getBoolean(portletPreferences.getValue("showIcon", null), true);
boolean showInstantMessenger = GetterUtil.getBoolean(portletPreferences.getValue("showInstantMessenger", null), true);
boolean showPhones = GetterUtil.getBoolean(portletPreferences.getValue("showPhones", null), true);
boolean showRecentActivity = GetterUtil.getBoolean(portletPreferences.getValue("showRecentActivity", null));

boolean showOnlySiteMembers = false;

String portletId = portletDisplay.getId();

if (portletId.equals(ContactsPortletKeys.MEMBERS)) {
	showOnlySiteMembers = true;
}

boolean showSites = GetterUtil.getBoolean(portletPreferences.getValue("showSites", null));
boolean showSMS = GetterUtil.getBoolean(portletPreferences.getValue("showSMS", null), true);
boolean showSocialNetwork = GetterUtil.getBoolean(portletPreferences.getValue("showSocialNetwork", null), true);
boolean showTags = GetterUtil.getBoolean(portletPreferences.getValue("showTags", null));
boolean showUsersInformation = GetterUtil.getBoolean(portletPreferences.getValue("showUsersInformation", null), true);
boolean showWebsites = GetterUtil.getBoolean(portletPreferences.getValue("showWebsites", null), true);

Group scopeGroup = themeDisplay.getScopeGroup();

Layout embeddedPersonalApplicationLayout = PersonalApplicationURLUtil.getOrAddEmbeddedPersonalApplicationLayout(themeDisplay.getGuestUser(), scopeGroup, layout.isPrivateLayout());
%>