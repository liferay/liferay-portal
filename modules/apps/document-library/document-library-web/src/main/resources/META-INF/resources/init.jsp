<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/comment" prefix="liferay-comment" %><%@
taglib uri="http://liferay.com/tld/data-engine" prefix="liferay-data-engine" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/document-library" prefix="liferay-document-library" %><%@
taglib uri="http://liferay.com/tld/dynamic-section" prefix="liferay-dynamic-section" %><%@
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/export-import-changeset" prefix="liferay-export-import-changeset" %><%@
taglib uri="http://liferay.com/tld/friendly-url" prefix="liferay-friendly-url" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/layout" prefix="liferay-layout" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/ratings" prefix="liferay-ratings" %><%@
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/sharing" prefix="liferay-sharing" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/trash" prefix="liferay-trash" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil" %><%@
page import="com.liferay.asset.kernel.model.AssetEntry" %><%@
page import="com.liferay.asset.kernel.model.AssetRenderer" %><%@
page import="com.liferay.asset.kernel.model.AssetRendererFactory" %><%@
page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %><%@
page import="com.liferay.asset.util.AssetHelper" %><%@
page import="com.liferay.document.library.constants.DLPortletKeys" %><%@
page import="com.liferay.document.library.display.context.DLDisplayContextProvider" %><%@
page import="com.liferay.document.library.display.context.DLEditFileEntryDisplayContext" %><%@
page import="com.liferay.document.library.display.context.DLFilePicker" %><%@
page import="com.liferay.document.library.display.context.DLViewFileEntryHistoryDisplayContext" %><%@
page import="com.liferay.document.library.display.context.DLViewFileVersionDisplayContext" %><%@
page import="com.liferay.document.library.display.context.IGViewFileVersionDisplayContext" %><%@
page import="com.liferay.document.library.kernel.antivirus.AntivirusScannerException" %><%@
page import="com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil" %><%@
page import="com.liferay.document.library.kernel.exception.DuplicateFileEntryException" %><%@
page import="com.liferay.document.library.kernel.exception.DuplicateFileEntryTypeException" %><%@
page import="com.liferay.document.library.kernel.exception.DuplicateFolderNameException" %><%@
page import="com.liferay.document.library.kernel.exception.DuplicateRepositoryNameException" %><%@
page import="com.liferay.document.library.kernel.exception.FileEntryDisplayDateException" %><%@
page import="com.liferay.document.library.kernel.exception.FileEntryLockException" %><%@
page import="com.liferay.document.library.kernel.exception.FileExtensionException" %><%@
page import="com.liferay.document.library.kernel.exception.FileMimeTypeException" %><%@
page import="com.liferay.document.library.kernel.exception.FileNameException" %><%@
page import="com.liferay.document.library.kernel.exception.FileShortcutPermissionException" %><%@
page import="com.liferay.document.library.kernel.exception.FileSizeException" %><%@
page import="com.liferay.document.library.kernel.exception.FolderNameException" %><%@
page import="com.liferay.document.library.kernel.exception.InvalidFileVersionException" %><%@
page import="com.liferay.document.library.kernel.exception.NoSuchFileEntryException" %><%@
page import="com.liferay.document.library.kernel.exception.NoSuchFileException" %><%@
page import="com.liferay.document.library.kernel.exception.NoSuchFileShortcutException" %><%@
page import="com.liferay.document.library.kernel.exception.NoSuchFolderException" %><%@
page import="com.liferay.document.library.kernel.exception.NoSuchMetadataSetException" %><%@
page import="com.liferay.document.library.kernel.exception.RepositoryNameException" %><%@
page import="com.liferay.document.library.kernel.exception.RequiredFileEntryTypeException" %><%@
page import="com.liferay.document.library.kernel.exception.RequiredFileException" %><%@
page import="com.liferay.document.library.kernel.exception.SourceFileNameException" %><%@
page import="com.liferay.document.library.kernel.model.DLFileEntry" %><%@
page import="com.liferay.document.library.kernel.model.DLFileEntryConstants" %><%@
page import="com.liferay.document.library.kernel.model.DLFileEntryMetadata" %><%@
page import="com.liferay.document.library.kernel.model.DLFileEntryType" %><%@
page import="com.liferay.document.library.kernel.model.DLFileEntryTypeConstants" %><%@
page import="com.liferay.document.library.kernel.model.DLFileShortcutConstants" %><%@
page import="com.liferay.document.library.kernel.model.DLFileVersion" %><%@
page import="com.liferay.document.library.kernel.model.DLFolder" %><%@
page import="com.liferay.document.library.kernel.model.DLFolderConstants" %><%@
page import="com.liferay.document.library.kernel.processor.AudioProcessorUtil" %><%@
page import="com.liferay.document.library.kernel.processor.ImageProcessorUtil" %><%@
page import="com.liferay.document.library.kernel.processor.PDFProcessorUtil" %><%@
page import="com.liferay.document.library.kernel.processor.RawMetadataProcessor" %><%@
page import="com.liferay.document.library.kernel.processor.VideoProcessorUtil" %><%@
page import="com.liferay.document.library.kernel.service.DLAppLocalServiceUtil" %><%@
page import="com.liferay.document.library.kernel.service.DLAppServiceUtil" %><%@
page import="com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalServiceUtil" %><%@
page import="com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil" %><%@
page import="com.liferay.document.library.kernel.service.DLFileEntryTypeServiceUtil" %><%@
page import="com.liferay.document.library.kernel.util.DLUtil" %><%@
page import="com.liferay.document.library.preview.exception.DLPreviewGenerationInProcessException" %><%@
page import="com.liferay.document.library.preview.exception.DLPreviewSizeException" %><%@
page import="com.liferay.document.library.util.DLURLHelperUtil" %><%@
page import="com.liferay.document.library.web.internal.constants.DLWebKeys" %><%@
page import="com.liferay.document.library.web.internal.dao.search.DLResultRowSplitter" %><%@
page import="com.liferay.document.library.web.internal.dao.search.IGResultRowSplitter" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLAdminDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLAdminManagementToolbarDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLAdminNavigationDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLConfigurationDisplaycontext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLEditDDMStructureDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLSelectRestrictedFileEntryTypesDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLViewFileEntryMetadataSetsDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLViewFileEntryMetadataSetsManagementToolbarDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLViewFileEntryTypesDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.DLViewMoreMenuItemsDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.IGDisplayContextProvider" %><%@
page import="com.liferay.document.library.web.internal.display.context.IGViewDisplayContext" %><%@
page import="com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper" %><%@
page import="com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper" %><%@
page import="com.liferay.document.library.web.internal.display.context.helper.IGRequestHelper" %><%@
page import="com.liferay.document.library.web.internal.exception.DLObjectSizeLimitExceededException" %><%@
page import="com.liferay.document.library.web.internal.exception.FileNameExtensionException" %><%@
page import="com.liferay.document.library.web.internal.helper.DLTrashHelper" %><%@
page import="com.liferay.document.library.web.internal.portlet.action.EditFileEntryMVCActionCommand" %><%@
page import="com.liferay.document.library.web.internal.search.EntriesChecker" %><%@
page import="com.liferay.document.library.web.internal.security.permission.resource.DDMStructurePermission" %><%@
page import="com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryPermission" %><%@
page import="com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryTypePermission" %><%@
page import="com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission" %><%@
page import="com.liferay.document.library.web.internal.servlet.taglib.clay.FileEntryTemplateVerticalCard" %><%@
page import="com.liferay.document.library.web.internal.servlet.taglib.clay.FolderHorizontalCard" %><%@
page import="com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings" %><%@
page import="com.liferay.document.library.web.internal.util.DLBreadcrumbUtil" %><%@
page import="com.liferay.document.library.web.internal.util.DLSubscriptionUtil" %><%@
page import="com.liferay.document.library.web.internal.util.DLWebComponentProvider" %><%@
page import="com.liferay.document.library.web.internal.util.IGUtil" %><%@
page import="com.liferay.dynamic.data.mapping.exception.NoSuchStructureException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StorageFieldRequiredException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StructureDefinitionException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StructureDuplicateElementException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StructureNameException" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMStructure" %><%@
page import="com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalServiceUtil" %><%@
page import="com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil" %><%@
page import="com.liferay.dynamic.data.mapping.storage.DDMFormValues" %><%@
page import="com.liferay.dynamic.data.mapping.storage.StorageType" %><%@
page import="com.liferay.dynamic.data.mapping.util.comparator.StructureStructureKeyComparator" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPDropdownItemList" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.InvalidRepositoryException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchRepositoryException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.lock.DuplicateLockException" %><%@
page import="com.liferay.portal.kernel.log.Log" %><%@
page import="com.liferay.portal.kernel.log.LogFactoryUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.Portlet" %><%@
page import="com.liferay.portal.kernel.model.Repository" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.repository.AuthenticationRepositoryException" %><%@
page import="com.liferay.portal.kernel.repository.RepositoryConfiguration" %><%@
page import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
page import="com.liferay.portal.kernel.repository.model.FileShortcut" %><%@
page import="com.liferay.portal.kernel.repository.model.FileVersion" %><%@
page import="com.liferay.portal.kernel.repository.model.Folder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.theme.ThemeDisplay" %><%@
page import="com.liferay.portal.kernel.upload.FileItem" %><%@
page import="com.liferay.portal.kernel.upload.LiferayFileItemException" %><%@
page import="com.liferay.portal.kernel.upload.UploadRequestSizeException" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.HttpComponentsUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PortletKeys" %><%@
page import="com.liferay.portal.kernel.util.PrefsPropsUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsKeys" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.TempFileEntryUtil" %><%@
page import="com.liferay.portal.kernel.util.TextFormatter" %><%@
page import="com.liferay.portal.kernel.util.UnicodeFormatter" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.webdav.WebDAVUtil" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowDefinition" %><%@
page import="com.liferay.portal.repository.registry.RepositoryClassDefinition" %><%@
page import="com.liferay.portal.repository.registry.RepositoryClassDefinitionCatalogUtil" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.portlet.documentlibrary.DLGroupServiceSettings" %><%@
page import="com.liferay.portlet.documentlibrary.constants.DLConstants" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %><%@
page import="com.liferay.taglib.search.ResultRow" %><%@
page import="com.liferay.taglib.servlet.PipingServletResponseFactory" %><%@
page import="com.liferay.trash.model.TrashEntry" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.text.DecimalFormatSymbols" %><%@
page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Arrays" %><%@
page import="java.util.Date" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
DLTrashHelper dlTrashHelper = (DLTrashHelper)request.getAttribute(DLWebKeys.DOCUMENT_LIBRARY_TRASH_HELPER);

DLDisplayContextProvider dlDisplayContextProvider = DLWebComponentProvider.getDLDisplayContextProvider();
IGDisplayContextProvider igDisplayContextProvider = DLWebComponentProvider.getIGDisplayContextProvider();

Format dateFormat = FastDateFormatFactoryUtil.getDate(locale, timeZone);
Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>

<%@ include file="/init-ext.jsp" %>