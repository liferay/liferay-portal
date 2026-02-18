<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>
<%@ taglib uri="http://liferay.com/tld/learn" prefix="liferay-learn" %><%@
taglib uri="http://liferay.com/tld/portal-workflow" prefix="liferay-portal-workflow" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %>

<%@ page import="com.liferay.bulk.selection.BulkSelectionRunner" %><%@
page import="com.liferay.bulk.selection.util.BulkSelectionRunnerUtil" %><%@
page import="com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil" %><%@
page import="com.liferay.digital.signature.constants.DigitalSignaturePortletKeys" %><%@
page import="com.liferay.document.library.configuration.DLConfiguration" %><%@
page import="com.liferay.document.library.exception.DLFileEntryConfigurationException" %><%@
page import="com.liferay.document.library.exception.DLStorageQuotaExceededException" %><%@
page import="com.liferay.document.library.kernel.antivirus.AntivirusVirusFoundException" %><%@
page import="com.liferay.document.library.kernel.exception.DuplicateDLFolderExternalReferenceCodeException" %><%@
page import="com.liferay.document.library.kernel.exception.DuplicateFileEntryExternalReferenceCodeException" %><%@
page import="com.liferay.document.library.kernel.exception.FileEntryExpirationDateException" %><%@
page import="com.liferay.document.library.kernel.exception.FileEntryReviewDateException" %><%@
page import="com.liferay.document.library.kernel.model.DLFileShortcut" %><%@
page import="com.liferay.document.library.kernel.model.DLVersionNumberIncrease" %><%@
page import="com.liferay.document.library.kernel.processor.DLProcessorHelperUtil" %><%@
page import="com.liferay.document.library.kernel.util.DLValidatorUtil" %><%@
page import="com.liferay.document.library.util.DLFileEntryTypeUtil" %><%@
page import="com.liferay.document.library.web.internal.display.context.CopyDLObjectsDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLAccessFromDesktopDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLAdminDisplayContextProvider" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLEditFileEntryTypeDataEngineDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLEditFileShortcutDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLEditFolderDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLFileEntryAdditionalMetadataSetsDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLFileEntryConfigurationDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLFileEntryTypeDetailsDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLInfoPanelDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLSizeLimitConfigurationDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLViewDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLViewEntriesDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLViewEntryHistoryDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLViewFileEntryDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.FolderActionDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.search.DDMStructureRowChecker" %><%@
page import="com.liferay.document.library.web.internal.util.DDMStructureUtil" %><%@
page import="com.liferay.document.library.web.internal.util.DLAssetHelperUtil" %><%@
page import="com.liferay.document.library.web.internal.util.DLFileEntryUtil" %><%@
page import="com.liferay.document.library.web.internal.util.DataRecordValuesUtil" %><%@
page import="com.liferay.document.library.web.internal.util.FolderItemSelectorURLProvider" %><%@
page import="com.liferay.document.library.web.internal.util.RepositoryClassDefinitionUtil" %><%@
page import="com.liferay.dynamic.data.mapping.exception.RequiredStructureException" %><%@
page import="com.liferay.dynamic.data.mapping.util.DDMFormValuesToMapConverter" %><%@
page import="com.liferay.expando.kernel.exception.ValueDataException" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList" %><%@
page import="com.liferay.item.selector.ItemSelector" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.RowChecker" %><%@
page import="com.liferay.portal.kernel.lock.Lock" %><%@
page import="com.liferay.portal.kernel.repository.model.RepositoryEntry" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.SetUtil" %><%@
page import="com.liferay.portal.kernel.view.count.ViewCountManagerUtil" %><%@
page import="com.liferay.portal.util.RepositoryUtil" %><%@
page import="com.liferay.product.navigation.personal.menu.util.PersonalApplicationURLUtil" %><%@
page import="com.liferay.ratings.kernel.RatingsType" %><%@
page import="com.liferay.ratings.kernel.definition.PortletRatingsDefinitionUtil" %><%@
page import="com.liferay.taglib.util.PortalIncludeUtil" %>

<%@ page import="java.util.Collections" %>

<%
DLConfiguration dlConfiguration = ConfigurationProviderUtil.getSystemConfiguration(DLConfiguration.class);
DLRequestHelper dlRequestHelper = new DLRequestHelper(request);
%>

<%@ include file="/document_library/init-ext.jsp" %>