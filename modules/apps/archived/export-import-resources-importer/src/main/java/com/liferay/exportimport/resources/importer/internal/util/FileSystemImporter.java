/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.util;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.dynamic.data.mapping.util.DDMXML;
import com.liferay.exportimport.resources.importer.internal.constants.ResourcesImporterConstants;
import com.liferay.exportimport.resources.importer.portlet.preferences.PortletPreferencesTranslator;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.search.index.IndexStatusManager;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import jakarta.portlet.PortletPreferences;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan Park
 * @author Raymond Augé
 */
public class FileSystemImporter extends BaseImporter {

	public FileSystemImporter(
		AssetTagLocalService assetTagLocalService,
		DDMFormDeserializer ddmFormJSONDeserializer,
		DDMFormDeserializer ddmFormXSDDeserializer,
		DDMStructureLocalService ddmStructureLocalService,
		DDMTemplateLocalService ddmTemplateLocalService, DDMXML ddmxml,
		DLAppLocalService dlAppLocalService,
		DLFileEntryLocalService dlFileEntryLocalService,
		DLFolderLocalService dlFolderLocalService,
		IndexStatusManager indexStatusManager, IndexerRegistry indexerRegistry,
		JournalArticleLocalService journalArticleLocalService,
		JournalFolderLocalService journalFolderLocalService,
		LayoutLocalService layoutLocalService,
		LayoutPrototypeLocalService layoutPrototypeLocalService,
		LayoutSetLocalService layoutSetLocalService,
		LayoutSetPrototypeLocalService layoutSetPrototypeLocalService,
		MimeTypes mimeTypes, Portal portal,
		PortletPreferencesFactory portletPreferencesFactory,
		PortletPreferencesLocalService portletPreferencesLocalService,
		ServiceTrackerMap<String, PortletPreferencesTranslator>
			serviceTrackerMap,
		RepositoryLocalService repositoryLocalService, SAXReader saxReader,
		ThemeLocalService themeLocalService, DLURLHelper dlURLHelper) {

		_dlURLHelper = dlURLHelper;

		this.assetTagLocalService = assetTagLocalService;
		this.ddmFormJSONDeserializer = ddmFormJSONDeserializer;
		this.ddmFormXSDDeserializer = ddmFormXSDDeserializer;
		this.ddmStructureLocalService = ddmStructureLocalService;
		this.ddmTemplateLocalService = ddmTemplateLocalService;
		this.ddmxml = ddmxml;
		this.dlAppLocalService = dlAppLocalService;
		this.dlFileEntryLocalService = dlFileEntryLocalService;
		this.dlFolderLocalService = dlFolderLocalService;
		this.indexStatusManager = indexStatusManager;
		this.indexerRegistry = indexerRegistry;
		this.journalArticleLocalService = journalArticleLocalService;
		this.journalFolderLocalService = journalFolderLocalService;
		this.layoutLocalService = layoutLocalService;
		this.layoutPrototypeLocalService = layoutPrototypeLocalService;
		this.layoutSetLocalService = layoutSetLocalService;
		this.layoutSetPrototypeLocalService = layoutSetPrototypeLocalService;
		this.mimeTypes = mimeTypes;
		this.portal = portal;
		this.portletPreferencesFactory = portletPreferencesFactory;
		this.portletPreferencesLocalService = portletPreferencesLocalService;
		this.serviceTrackerMap = serviceTrackerMap;
		this.repositoryLocalService = repositoryLocalService;
		this.saxReader = saxReader;
		this.themeLocalService = themeLocalService;
	}

	@Override
	public void importResources() throws Exception {
		_resourcesDir = new File(resourcesDir);

		if (!_resourcesDir.isDirectory() || !_resourcesDir.canRead()) {
			throw new IllegalArgumentException(
				"Unaccessible resource directory " + resourcesDir);
		}

		doImportResources();
	}

	protected void addApplicationDisplayTemplate(
			String script, File file, long classNameId)
		throws PortalException {

		String fileName = FileUtil.stripExtension(file.getName());

		String name = getName(fileName);

		DDMTemplate ddmTemplate = ddmTemplateLocalService.fetchTemplate(
			groupId, classNameId, _getKey(fileName));

		if (ddmTemplate != null) {
			if (!developerModeEnabled) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"DDM template with name ", name, " and version ",
							version, " already exists"));
				}

				return;
			}

			if (!updateModeEnabled) {
				ddmTemplateLocalService.deleteTemplate(ddmTemplate);
			}
		}

		try {
			if (!updateModeEnabled || (ddmTemplate == null)) {
				ddmTemplateLocalService.addTemplate(
					null, userId, groupId, classNameId, 0,
					portal.getClassNameId(PortletDisplayTemplate.class),
					_getKey(fileName), getMap(name), null,
					DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
					StringPool.BLANK, getDDMTemplateLanguage(file.getName()),
					script, true, false, StringPool.BLANK, null,
					serviceContext);
			}
			else {
				ddmTemplateLocalService.updateTemplate(
					userId, ddmTemplate.getTemplateId(),
					ddmTemplate.getClassPK(), getMap(name), null,
					DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
					StringPool.BLANK, getDDMTemplateLanguage(file.getName()),
					script, ddmTemplate.isCacheable(), serviceContext);
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to import widget template " + file.getName(),
					portalException);
			}

			throw portalException;
		}
	}

	protected void addApplicationDisplayTemplate(
			String parentDirName, String dirName, long classNameId)
		throws Exception {

		File dir = new File(
			_resourcesDir, parentDirName + StringPool.SLASH + dirName);

		if (!dir.isDirectory() || !dir.canRead()) {
			return;
		}

		File[] files = _listFiles(dir);

		for (File file : files) {
			String script = StringUtil.read(getInputStream(file));

			if (Validator.isNull(script)) {
				continue;
			}

			addApplicationDisplayTemplate(script, file, classNameId);
		}
	}

	protected void addDDLDisplayTemplates(
			String ddmStructureKey, String dirName, String fileName)
		throws Exception {

		File dir = new File(
			_resourcesDir, dirName + StringPool.SLASH + fileName);

		if (!dir.isDirectory() || !dir.canRead()) {
			return;
		}

		DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
			groupId, portal.getClassNameId(DDLRecordSet.class),
			ddmStructureKey);

		File[] files = _listFiles(dir);

		for (File file : files) {
			String script = StringUtil.read(getInputStream(file));

			if (Validator.isNull(script)) {
				return;
			}

			addDDMTemplate(
				groupId, ddmStructure.getStructureId(), file.getName(),
				getDDMTemplateLanguage(file.getName()), script,
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null);
		}
	}

	protected void addDDLFormTemplates(
			String ddmStructureKey, String dirName, String fileName)
		throws Exception {

		File dir = new File(
			_resourcesDir, dirName + StringPool.SLASH + fileName);

		if (!dir.isDirectory() || !dir.canRead()) {
			return;
		}

		DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
			groupId, portal.getClassNameId(DDLRecordSet.class),
			ddmStructureKey);

		File[] files = _listFiles(dir);

		for (File file : files) {
			String script = StringUtil.read(getInputStream(file));

			if (Validator.isNull(script)) {
				return;
			}

			addDDMTemplate(
				groupId, ddmStructure.getStructureId(), file.getName(), "xsd",
				script, DDMTemplateConstants.TEMPLATE_TYPE_FORM,
				DDMTemplateConstants.TEMPLATE_MODE_CREATE);
		}
	}

	protected void addDDLStructures(String dirName) throws Exception {
		File dir = new File(_resourcesDir, dirName);

		if (!dir.isDirectory() || !dir.canRead()) {
			return;
		}

		File[] files = _listFiles(dir);

		for (File file : files) {
			String fileName = FileUtil.stripExtension(file.getName());

			addDDMStructures(fileName, getInputStream(file));
		}
	}

	protected void addDDMStructures(String fileName, InputStream inputStream)
		throws Exception {

		fileName = FileUtil.stripExtension(fileName);

		String name = getName(fileName);

		DDMStructure ddmStructure = ddmStructureLocalService.fetchStructure(
			groupId, portal.getClassNameId(DDLRecordSet.class),
			_getKey(fileName));

		if (ddmStructure != null) {
			if (!developerModeEnabled) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"DDM structure with name ", name, " and version ",
							version, " already exists"));
				}

				return;
			}

			if (!updateModeEnabled) {
				ddmStructureLocalService.deleteDDMStructure(ddmStructure);
			}
		}

		try {
			String definition = StringUtil.read(inputStream);

			ddmxml.validateXML(definition);

			DDMForm ddmForm = _deserializeXSD(definition);

			DDMFormLayout ddmFormLayout = DDMUtil.getDefaultDDMFormLayout(
				ddmForm);

			if (!updateModeEnabled || (ddmStructure == null)) {
				ddmStructure = ddmStructureLocalService.addStructure(
					null, userId, groupId,
					DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
					portal.getClassNameId(DDLRecordSet.class),
					_getKey(fileName), getMap(name), null, ddmForm,
					ddmFormLayout, StorageType.DEFAULT.toString(),
					DDMStructureConstants.TYPE_DEFAULT, serviceContext);
			}
			else {
				ddmStructure = ddmStructureLocalService.updateStructure(
					userId, ddmStructure.getStructureId(),
					DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
					getMap(name), null, ddmForm, ddmFormLayout, serviceContext);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to import DDM structure " + fileName, exception);
			}

			throw exception;
		}

		addDDLDisplayTemplates(
			ddmStructure.getStructureKey(),
			_DDL_STRUCTURE_DISPLAY_TEMPLATE_DIR_NAME, fileName);

		addDDLFormTemplates(
			ddmStructure.getStructureKey(),
			_DDL_STRUCTURE_FORM_TEMPLATE_DIR_NAME, fileName);
	}

	protected void addDDMStructures(
			String parentDDMStructureKey, String dirName)
		throws Exception {

		File dir = new File(_resourcesDir, dirName);

		if (!dir.isDirectory() || !dir.canRead()) {
			return;
		}

		File[] files = _listFiles(dir);

		for (File file : files) {
			try (InputStream inputStream = new BufferedInputStream(
					new FileInputStream(file))) {

				addDDMStructures(
					parentDDMStructureKey, file.getName(), inputStream);
			}
		}
	}

	protected void addDDMStructures(
			String parentDDMStructureKey, String fileName,
			InputStream inputStream)
		throws Exception {

		String language = _getDDMStructureLanguage(fileName);

		fileName = FileUtil.stripExtension(fileName);

		String name = getName(fileName);

		DDMStructure ddmStructure = ddmStructureLocalService.fetchStructure(
			groupId, portal.getClassNameId(JournalArticle.class),
			_getKey(fileName));

		if (ddmStructure != null) {
			if (!developerModeEnabled) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"DDM structure with name ", name, " and version ",
							version, " already exists"));
				}

				return;
			}

			if (!updateModeEnabled) {
				ddmStructureLocalService.deleteDDMStructure(ddmStructure);
			}
		}

		String content = StringUtil.read(inputStream);

		DDMForm ddmForm = null;

		if (language.equals(TemplateConstants.LANG_TYPE_XML)) {
			ddmxml.validateXML(content);

			ddmForm = _deserializeXSD(content);
		}
		else {
			ddmForm = _deserializeJSONDDMForm(content);
		}

		DDMFormLayout ddmFormLayout = DDMUtil.getDefaultDDMFormLayout(ddmForm);

		_setServiceContext(fileName);

		try {
			if (!updateModeEnabled || (ddmStructure == null)) {
				ddmStructure = ddmStructureLocalService.addStructure(
					userId, groupId, parentDDMStructureKey,
					portal.getClassNameId(JournalArticle.class),
					_getKey(fileName), getMap(name), null, ddmForm,
					ddmFormLayout, StorageType.DEFAULT.toString(),
					DDMStructureConstants.TYPE_DEFAULT, serviceContext);
			}
			else {
				DDMStructure parentStructure =
					ddmStructureLocalService.fetchStructure(
						groupId, portal.getClassNameId(JournalArticle.class),
						parentDDMStructureKey);

				long parentDDMStructureId =
					DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID;

				if (parentStructure != null) {
					parentDDMStructureId = parentStructure.getStructureId();
				}

				ddmStructure = ddmStructureLocalService.updateStructure(
					userId, ddmStructure.getStructureId(), parentDDMStructureId,
					getMap(name), null, ddmForm, ddmFormLayout, serviceContext);
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to import DDM structure " + fileName,
					portalException);
			}

			throw portalException;
		}

		_ddmStructureIds.add(ddmStructure.getStructureId());

		addDDMTemplates(
			ddmStructure.getStructureKey(),
			_JOURNAL_DDM_TEMPLATES_DIR_NAME + fileName);

		if (Validator.isNull(parentDDMStructureKey)) {
			addDDMStructures(
				ddmStructure.getStructureKey(),
				_JOURNAL_DDM_STRUCTURES_DIR_NAME + fileName);
		}
	}

	protected void addDDMTemplate(
			long templateGroupId, long ddmStructureId, String fileName,
			String language, String script, String type, String mode)
		throws Exception {

		fileName = FileUtil.getShortFileName(fileName);

		fileName = FileUtil.stripExtension(fileName);

		String name = getName(fileName);

		DDMTemplate ddmTemplate = ddmTemplateLocalService.fetchTemplate(
			groupId, portal.getClassNameId(DDMStructure.class),
			_getKey(fileName));

		if (ddmTemplate != null) {
			if (!developerModeEnabled) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"DDM template with name ", name, " and version ",
							version, " already exists"));
				}

				return;
			}

			if (!updateModeEnabled) {
				ddmTemplateLocalService.deleteTemplate(ddmTemplate);
			}
		}

		try {
			if (!updateModeEnabled || (ddmTemplate == null)) {
				ddmTemplateLocalService.addTemplate(
					null, userId, templateGroupId,
					portal.getClassNameId(DDMStructure.class), ddmStructureId,
					portal.getClassNameId(JournalArticle.class),
					_getKey(fileName), getMap(name), null, type, mode, language,
					script, true, false, StringPool.BLANK, null,
					serviceContext);
			}
			else {
				ddmTemplateLocalService.updateTemplate(
					userId, ddmTemplate.getTemplateId(),
					portal.getClassNameId(DDMStructure.class), getMap(name),
					null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null,
					language, script, ddmTemplate.isCacheable(),
					serviceContext);
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to import DDM template " + fileName,
					portalException);
			}

			throw portalException;
		}
	}

	protected void addDDMTemplates(String ddmStructureKey, String dirName)
		throws Exception {

		File dir = new File(_resourcesDir, dirName);

		if (!dir.isDirectory() || !dir.canRead()) {
			return;
		}

		File[] files = _listFiles(dir);

		for (File file : files) {
			try (InputStream inputStream = new BufferedInputStream(
					new FileInputStream(file))) {

				addDDMTemplates(ddmStructureKey, file.getName(), inputStream);
			}
		}
	}

	protected void addDDMTemplates(
			String ddmStructureKey, String fileName, InputStream inputStream)
		throws Exception {

		String language = getDDMTemplateLanguage(fileName);

		fileName = FileUtil.stripExtension(fileName);

		String name = getName(fileName);

		_setServiceContext(fileName);

		DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
			groupId, portal.getClassNameId(JournalArticle.class),
			ddmStructureKey);

		DDMTemplate ddmTemplate = ddmTemplateLocalService.fetchTemplate(
			groupId, portal.getClassNameId(DDMStructure.class),
			_getKey(fileName));

		if (ddmTemplate != null) {
			if (!developerModeEnabled) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"DDM template with name ", name, " and version ",
							version, " already exists"));
				}

				return;
			}

			if (!updateModeEnabled) {
				ddmTemplateLocalService.deleteTemplate(ddmTemplate);
			}
		}

		String script = StringUtil.read(inputStream);

		try {
			if (!updateModeEnabled || (ddmTemplate == null)) {
				ddmTemplate = ddmTemplateLocalService.addTemplate(
					null, userId, groupId,
					portal.getClassNameId(DDMStructure.class),
					ddmStructure.getStructureId(),
					portal.getClassNameId(JournalArticle.class),
					_getKey(fileName), getMap(name), null,
					DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null, language,
					_replaceFileEntryURL(script), true, false, null, null,
					serviceContext);
			}
			else {
				ddmTemplate = ddmTemplateLocalService.updateTemplate(
					userId, ddmTemplate.getTemplateId(),
					portal.getClassNameId(DDMStructure.class), getMap(name),
					null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null,
					language, _replaceFileEntryURL(script),
					ddmTemplate.isCacheable(), serviceContext);
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to import DDM template " + fileName,
					portalException);
			}

			throw portalException;
		}

		addJournalArticles(
			ddmStructure.getStructureId(), ddmTemplate.getTemplateKey(),
			_JOURNAL_ARTICLES_DIR_NAME + fileName,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	protected void addDLFileEntries(String dirName) throws Exception {
		File dir = new File(_resourcesDir, dirName);

		if (!dir.isDirectory() || !dir.canRead()) {
			return;
		}

		File[] files = dir.listFiles();

		if (ArrayUtil.isEmpty(files)) {
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				addDLFolder(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, file);
			}
			else {
				addDLFileEntry(
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, file);
			}
		}
	}

	protected void addDLFileEntry(long parentFolderId, File file)
		throws Exception {

		try (InputStream inputStream = new BufferedInputStream(
				new FileInputStream(file))) {

			addDLFileEntry(
				parentFolderId, file.getName(), inputStream, file.length());
		}
	}

	protected void addDLFileEntry(
			long parentFolderId, String fileName, InputStream inputStream,
			long length)
		throws Exception {

		_setServiceContext(fileName);

		FileEntry fileEntry = null;

		try {
			try {
				fileEntry = dlAppLocalService.addFileEntry(
					null, userId, groupId, parentFolderId, fileName,
					mimeTypes.getContentType(fileName), fileName,
					StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
					inputStream, length, null, null, null, serviceContext);
			}
			catch (DuplicateFileEntryException duplicateFileEntryException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(duplicateFileEntryException);
				}

				fileEntry = dlAppLocalService.getFileEntry(
					groupId, parentFolderId, fileName);

				String previousVersion = fileEntry.getVersion();

				fileEntry = dlAppLocalService.updateFileEntry(
					userId, fileEntry.getFileEntryId(), fileName,
					mimeTypes.getContentType(fileName), fileName,
					StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
					DLVersionNumberIncrease.MAJOR, inputStream, length,
					fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
					fileEntry.getReviewDate(), serviceContext);

				dlFileEntryLocalService.deleteFileVersion(
					fileEntry.getUserId(), fileEntry.getFileEntryId(),
					previousVersion);
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to import DL file entry " + fileName,
					portalException);
			}

			throw portalException;
		}

		_addPrimaryKey(DLFileEntry.class.getName(), fileEntry.getPrimaryKey());

		_fileEntries.put(fileName, fileEntry);
	}

	protected long addDLFolder(long parentFolderId, File folder)
		throws Exception {

		long folderId = addDLFolder(parentFolderId, folder.getName());

		File[] files = folder.listFiles();

		if (ArrayUtil.isEmpty(files)) {
			return folderId;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				addDLFolder(folderId, file);
			}
			else {
				addDLFileEntry(folderId, file);
			}
		}

		return folderId;
	}

	protected long addDLFolder(long parentFolderId, String folderName)
		throws Exception {

		DLFolder dlFolder = dlFolderLocalService.fetchFolder(
			groupId, parentFolderId, folderName);

		if (dlFolder == null) {
			dlFolder = dlFolderLocalService.addFolder(
				null, userId, groupId, groupId, false, parentFolderId,
				folderName, null, false, serviceContext);
		}

		_addPrimaryKey(DLFolder.class.getName(), dlFolder.getPrimaryKey());

		return dlFolder.getFolderId();
	}

	protected void addJournalArticles(
			long ddmStructureId, String ddmTemplateKey, String dirName,
			long folderId)
		throws Exception {

		File dir = new File(_resourcesDir, dirName);

		if (!dir.isDirectory() || !dir.canRead()) {
			return;
		}

		File[] files = _listFiles(dir);

		for (File file : files) {
			try (InputStream inputStream = new BufferedInputStream(
					new FileInputStream(file))) {

				addJournalArticles(
					ddmStructureId, ddmTemplateKey, file.getName(), folderId,
					inputStream);
			}
		}
	}

	protected void addJournalArticles(
			long ddmStructureId, String ddmTemplateKey, String fileName,
			long folderId, InputStream inputStream)
		throws Exception {

		String title = FileUtil.stripExtension(fileName);

		JSONObject assetJSONObject = _assetJSONObjectMap.get(fileName);

		Map<Locale, String> descriptionMap = null;

		boolean indexable = true;

		if (assetJSONObject != null) {
			String abstractSummary = assetJSONObject.getString(
				"abstractSummary");

			descriptionMap = getMap(abstractSummary);

			indexable = GetterUtil.getBoolean(
				assetJSONObject.getString("indexable"), true);
		}

		String content = StringUtil.read(inputStream);

		content = _replaceFileEntryURL(content);
		content = _replaceLinksToLayouts(content);

		Locale articleDefaultLocale = LocaleUtil.fromLanguageId(
			LocalizationUtil.getDefaultLanguageId(content));

		boolean smallImage = false;
		String smallImageURL = StringPool.BLANK;

		if (assetJSONObject != null) {
			String smallImageFileName = assetJSONObject.getString("smallImage");

			if (Validator.isNotNull(smallImageFileName)) {
				smallImage = true;

				FileEntry fileEntry = _fileEntries.get(smallImageFileName);

				if (fileEntry != null) {
					smallImageURL = _dlURLHelper.getPreviewURL(
						fileEntry, fileEntry.getFileVersion(), null,
						StringPool.BLANK);
				}
			}
		}

		_setServiceContext(fileName);

		String journalArticleId = _getJournalId(fileName);

		JournalArticle journalArticle =
			journalArticleLocalService.fetchLatestArticle(
				groupId, journalArticleId, WorkflowConstants.STATUS_ANY);

		try {
			Map<Locale, String> titleMap = getMap(articleDefaultLocale, title);

			if (journalArticle == null) {
				journalArticle = journalArticleLocalService.addArticle(
					null, userId, groupId, folderId, 0, 0, journalArticleId,
					false, JournalArticleConstants.VERSION_DEFAULT, titleMap,
					descriptionMap, titleMap, content, ddmStructureId,
					ddmTemplateKey, StringPool.BLANK, 1, 1, 2010, 0, 0, 0, 0, 0,
					0, 0, true, 0, 0, 0, 0, 0, true, indexable, smallImage, 0,
					0, smallImageURL, null, new HashMap<String, byte[]>(),
					StringPool.BLANK, serviceContext);
			}
			else {
				journalArticle = journalArticleLocalService.updateArticle(
					userId, groupId, folderId, journalArticleId,
					journalArticle.getVersion(), titleMap, descriptionMap, null,
					content, ddmTemplateKey, StringPool.BLANK, 1, 1, 2010, 0, 0,
					0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, indexable,
					smallImage, 0, 0, smallImageURL, null,
					new HashMap<String, byte[]>(), StringPool.BLANK,
					serviceContext);
			}

			journalArticleLocalService.updateStatus(
				userId, groupId, journalArticle.getArticleId(),
				journalArticle.getVersion(), WorkflowConstants.STATUS_APPROVED,
				StringPool.BLANK, new HashMap<String, Serializable>(),
				serviceContext);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to import journal article " + fileName,
					portalException);
			}

			throw portalException;
		}

		_addPrimaryKey(
			JournalArticle.class.getName(), journalArticle.getPrimaryKey());
	}

	protected void addLayoutPrototype(InputStream inputStream)
		throws Exception {

		String content = StringUtil.read(inputStream);

		if (Validator.isNull(content)) {
			return;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(content);

		JSONObject layoutTemplateJSONObject = jsonObject.getJSONObject(
			"layoutTemplate");

		Map<Locale, String> nameMap = getMap(
			layoutTemplateJSONObject.getString("name"));

		String name = nameMap.get(LocaleUtil.getDefault());

		LayoutPrototype layoutPrototype = getLayoutPrototype(companyId, name);

		if (layoutPrototype != null) {
			if (!developerModeEnabled) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Layout prototype with name ", name,
							" already exists for company ", companyId));
				}

				return;
			}

			if (!updateModeEnabled) {
				layoutPrototypeLocalService.deleteLayoutPrototype(
					layoutPrototype);
			}
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		String uuid = layoutTemplateJSONObject.getString("uuid");

		if (Validator.isNotNull(uuid)) {
			serviceContext.setUuid(uuid);
		}

		Map<Locale, String> descriptionMap = getMap(
			layoutTemplateJSONObject, "description");

		try {
			if (!updateModeEnabled || (layoutPrototype == null)) {
				layoutPrototype =
					layoutPrototypeLocalService.addLayoutPrototype(
						userId, companyId, getMap(name), descriptionMap, true,
						serviceContext);
			}
			else {
				layoutPrototype =
					layoutPrototypeLocalService.updateLayoutPrototype(
						layoutPrototype.getLayoutPrototypeId(), getMap(name),
						descriptionMap, layoutPrototype.isActive(),
						serviceContext);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to import layout prototype " + name, exception);
			}

			throw exception;
		}

		JSONArray columnsJSONArray = layoutTemplateJSONObject.getJSONArray(
			"columns");

		Layout layout = layoutPrototype.getLayout();

		_addLayoutColumns(
			layout, LayoutTypePortletConstants.COLUMN_PREFIX, columnsJSONArray);

		layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

	protected void addLayoutPrototype(String dirName) throws Exception {
		File layoutTemplatesDir = new File(_resourcesDir, dirName);

		if (!layoutTemplatesDir.isDirectory() ||
			!layoutTemplatesDir.canRead()) {

			return;
		}

		File[] files = _listFiles(layoutTemplatesDir);

		for (File file : files) {
			addLayoutPrototype(getInputStream(file));
		}
	}

	protected void doImportResources() throws Exception {
		serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(groupId);

		boolean indexReadOnly = indexStatusManager.isIndexReadOnly();

		try {
			indexStatusManager.setIndexReadOnly(true);

			_setUpSitemap("sitemap.json");

			_setUpAssets("assets.json");
			_setUpSettings("settings.json");

			indexStatusManager.setIndexReadOnly(false);

			long startTime = System.currentTimeMillis();

			if (_log.isDebugEnabled()) {
				_log.debug("Commence indexing");
			}

			if (isIndexAfterImport()) {
				index();
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Indexing completed in " +
						(System.currentTimeMillis() - startTime) + "ms");
			}
		}
		finally {
			indexStatusManager.setIndexReadOnly(indexReadOnly);

			_ddmStructureIds.clear();
			_primaryKeys.clear();
		}
	}

	protected String getDDMTemplateLanguage(String fileName) {
		String extension = FileUtil.getExtension(fileName);

		if (extension.equals(TemplateConstants.LANG_TYPE_CSS) ||
			extension.equals(TemplateConstants.LANG_TYPE_FTL) ||
			extension.equals(TemplateConstants.LANG_TYPE_VM)) {

			return extension;
		}

		return TemplateConstants.LANG_TYPE_VM;
	}

	protected InputStream getInputStream(File file) throws Exception {
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			return null;
		}

		return new BufferedInputStream(new FileInputStream(file));
	}

	protected InputStream getInputStream(String fileName) throws Exception {
		File file = new File(_resourcesDir, fileName);

		return getInputStream(file);
	}

	protected Map<Locale, String> getMap(
		JSONObject layoutJSONObject, String name) {

		Map<Locale, String> map = new HashMap<>();

		JSONObject jsonObject = layoutJSONObject.getJSONObject(
			name.concat("Map"));

		if (jsonObject != null) {
			map = (Map<Locale, String>)LocalizationUtil.deserialize(jsonObject);

			if (!map.containsKey(LocaleUtil.getDefault())) {
				Collection<String> values = map.values();

				Iterator<String> iterator = values.iterator();

				map.put(LocaleUtil.getDefault(), iterator.next());
			}
		}
		else {
			String value = layoutJSONObject.getString(name);

			map.put(LocaleUtil.getDefault(), value);
		}

		return map;
	}

	protected String getName(String name) {
		if (!appendVersion) {
			return name;
		}

		return name + " - " + version;
	}

	protected void index() {
		for (Map.Entry<String, Set<Long>> primaryKeysEntry :
				_primaryKeys.entrySet()) {

			String className = primaryKeysEntry.getKey();

			Indexer<?> indexer = indexerRegistry.getIndexer(className);

			if (indexer == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("No indexer for " + className);
				}

				continue;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Indexing " + className);
			}

			for (long primaryKey : primaryKeysEntry.getValue()) {
				try {
					indexer.reindex(className, primaryKey);
				}
				catch (SearchException searchException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to index entry for class name ",
								className, " and primary key ", primaryKey),
							searchException);
					}
				}
			}
		}

		if (_ddmStructureIds.isEmpty()) {
			return;
		}

		Set<Long> primaryKeys = _primaryKeys.get(
			JournalArticle.class.getName());

		Indexer<?> indexer = indexerRegistry.getIndexer(
			JournalArticle.class.getName());

		for (long ddmStructureId : _ddmStructureIds) {
			List<JournalArticle> journalArticles =
				journalArticleLocalService.getArticlesByStructureId(
					getGroupId(), ddmStructureId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			for (JournalArticle journalArticle : journalArticles) {
				if ((primaryKeys != null) &&
					primaryKeys.contains(journalArticle.getPrimaryKey())) {

					continue;
				}

				try {
					indexer.reindex(
						JournalArticle.class.getName(),
						journalArticle.getPrimaryKey());
				}
				catch (SearchException searchException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Cannot index entry: className=",
								JournalArticle.class.getName(), ", primaryKey=",
								journalArticle.getPrimaryKey()),
							searchException);
					}
				}
			}
		}
	}

	protected final AssetTagLocalService assetTagLocalService;
	protected final DDMFormDeserializer ddmFormJSONDeserializer;
	protected final DDMFormDeserializer ddmFormXSDDeserializer;
	protected final DDMStructureLocalService ddmStructureLocalService;
	protected final DDMTemplateLocalService ddmTemplateLocalService;
	protected final DDMXML ddmxml;
	protected final DLAppLocalService dlAppLocalService;
	protected final DLFileEntryLocalService dlFileEntryLocalService;
	protected final DLFolderLocalService dlFolderLocalService;
	protected final IndexerRegistry indexerRegistry;
	protected final IndexStatusManager indexStatusManager;
	protected final JournalArticleLocalService journalArticleLocalService;
	protected final JournalFolderLocalService journalFolderLocalService;
	protected final LayoutLocalService layoutLocalService;
	protected final LayoutPrototypeLocalService layoutPrototypeLocalService;
	protected final LayoutSetLocalService layoutSetLocalService;
	protected final LayoutSetPrototypeLocalService
		layoutSetPrototypeLocalService;
	protected final MimeTypes mimeTypes;
	protected final Portal portal;
	protected final PortletPreferencesFactory portletPreferencesFactory;
	protected final PortletPreferencesLocalService
		portletPreferencesLocalService;
	protected final RepositoryLocalService repositoryLocalService;
	protected final SAXReader saxReader;
	protected ServiceContext serviceContext;
	protected final ServiceTrackerMap<String, PortletPreferencesTranslator>
		serviceTrackerMap;
	protected final ThemeLocalService themeLocalService;

	private void _addApplicationDisplayTemplates(String dirName)
		throws Exception {

		for (Object[] applicationDisplayTemplateType :
				_APPLICATION_DISPLAY_TEMPLATE_TYPES) {

			String className = (String)applicationDisplayTemplateType[1];

			addApplicationDisplayTemplate(
				dirName, (String)applicationDisplayTemplateType[0],
				portal.getClassNameId(className));
		}
	}

	private void _addLayout(
			boolean privateLayout, long parentLayoutId,
			JSONObject layoutJSONObject)
		throws Exception {

		if (targetClassName.equals(LayoutSetPrototype.class.getName())) {
			privateLayout = true;
		}

		Map<Locale, String> nameMap = getMap(layoutJSONObject, "name");
		Map<Locale, String> titleMap = getMap(layoutJSONObject, "title");

		String type = layoutJSONObject.getString("type");

		if (Validator.isNull(type)) {
			type = LayoutConstants.TYPE_PORTLET;
		}

		String typeSettings = layoutJSONObject.getString("typeSettings");

		boolean hidden = layoutJSONObject.getBoolean("hidden");

		String themeId = layoutJSONObject.getString("themeId");

		String layoutCss = layoutJSONObject.getString("layoutCss");

		String colorSchemeId = layoutJSONObject.getString("colorSchemeId");

		String friendlyURL = layoutJSONObject.getString("friendlyURL");

		if (Validator.isNotNull(friendlyURL) &&
			!friendlyURL.startsWith(StringPool.SLASH)) {

			friendlyURL = StringPool.SLASH + friendlyURL;
		}

		Map<Locale, String> friendlyURLMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), friendlyURL
		).build();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setUserId(userId);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			String layoutPrototypeName = layoutJSONObject.getString(
				"layoutPrototypeName");

			String layoutPrototypeUuid = null;

			if (Validator.isNotNull(layoutPrototypeName)) {
				LayoutPrototype layoutPrototype = getLayoutPrototype(
					companyId, layoutPrototypeName);

				layoutPrototypeUuid = layoutPrototype.getUuid();
			}
			else {
				layoutPrototypeUuid = layoutJSONObject.getString(
					"layoutPrototypeUuid");
			}

			if (Validator.isNotNull(layoutPrototypeUuid)) {
				boolean layoutPrototypeLinkEnabled = GetterUtil.getBoolean(
					layoutJSONObject.getString("layoutPrototypeLinkEnabled"));

				serviceContext.setAttribute(
					"layoutPrototypeLinkEnabled", layoutPrototypeLinkEnabled);

				serviceContext.setAttribute(
					"layoutPrototypeUuid", layoutPrototypeUuid);
			}

			Layout layout = layoutLocalService.fetchLayoutByFriendlyURL(
				groupId, privateLayout, friendlyURL);

			if (layout != null) {
				if (!developerModeEnabled) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"Layout with friendly URL " + friendlyURL +
								" already exists");
					}

					return;
				}

				if (!updateModeEnabled) {
					layoutLocalService.deleteLayout(layout);
				}
			}

			if (!updateModeEnabled || (layout == null)) {
				layout = layoutLocalService.addLayout(
					null, userId, groupId, privateLayout, parentLayoutId,
					nameMap, titleMap, null, null, null, type, typeSettings,
					hidden, friendlyURLMap, serviceContext);
			}
			else {
				_resetLayoutColumns(layout);

				layout = layoutLocalService.updateLayout(
					groupId, privateLayout, layout.getLayoutId(),
					parentLayoutId, nameMap, titleMap,
					layout.getDescriptionMap(), layout.getKeywordsMap(),
					layout.getRobotsMap(), type, hidden, friendlyURLMap,
					layout.getIconImage(), null, 0, 0, 0, serviceContext);
			}

			if (Validator.isNotNull(themeId) ||
				Validator.isNotNull(colorSchemeId)) {

				// If the theme ID or the color scheme ID are not null, then the
				// layout has a custom look and feel and should be updated in
				// the database

				layoutLocalService.updateLookAndFeel(
					groupId, privateLayout, layout.getLayoutId(), themeId,
					colorSchemeId, layoutCss);
			}

			String layoutTemplateId = layoutJSONObject.getString(
				"layoutTemplateId", _defaultLayoutTemplateId);

			if (Validator.isNotNull(layoutTemplateId)) {
				LayoutTypePortlet layoutTypePortlet =
					(LayoutTypePortlet)layout.getLayoutType();

				layoutTypePortlet.setLayoutTemplateId(
					userId, layoutTemplateId, false);
			}

			JSONArray columnsJSONArray = layoutJSONObject.getJSONArray(
				"columns");

			_addLayoutColumns(
				layout, LayoutTypePortletConstants.COLUMN_PREFIX,
				columnsJSONArray);

			layoutLocalService.updateLayout(
				groupId, layout.isPrivateLayout(), layout.getLayoutId(),
				layout.getTypeSettings());

			JSONArray layoutsJSONArray = layoutJSONObject.getJSONArray(
				"layouts");

			_addLayouts(privateLayout, layout.getLayoutId(), layoutsJSONArray);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to import layout " + layoutJSONObject, exception);
			}

			throw exception;
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _addLayoutColumn(
			Layout layout, String columnId, JSONArray columnJSONArray)
		throws Exception {

		if (columnJSONArray == null) {
			return;
		}

		for (int i = 0; i < columnJSONArray.length(); i++) {
			JSONObject portletJSONObject = columnJSONArray.getJSONObject(i);

			if (portletJSONObject == null) {
				String journalArticleId = _getJournalId(
					columnJSONArray.getString(i));

				portletJSONObject = _getDefaultPortletJSONObject(
					journalArticleId);
			}

			_addLayoutColumnPortlet(layout, columnId, portletJSONObject);
		}
	}

	private void _addLayoutColumnPortlet(
			Layout layout, String columnId, JSONObject portletJSONObject)
		throws Exception {

		String rootPortletId = portletJSONObject.getString("portletId");

		if (Validator.isNull(rootPortletId)) {
			throw new ImporterException("portletId is not specified");
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		PortletPreferencesTranslator portletPreferencesTranslator =
			serviceTrackerMap.getService(rootPortletId);

		if (portletPreferencesTranslator == null) {
			portletPreferencesTranslator = serviceTrackerMap.getService(
				ResourcesImporterConstants.PORTLET_ID_DEFAULT);
		}

		String portletId = layoutTypePortlet.addPortletId(
			userId, rootPortletId, columnId, -1, false);

		if (portletId == null) {
			return;
		}

		JSONObject portletPreferencesJSONObject =
			portletJSONObject.getJSONObject("portletPreferences");

		if ((portletPreferencesJSONObject == null) ||
			(portletPreferencesJSONObject.length() == 0)) {

			return;
		}

		if (portletPreferencesTranslator != null) {
			PortletPreferences portletPreferences =
				PortletPreferencesLocalServiceUtil.getPreferences(
					PortletPreferencesFactoryUtil.getPortletPreferencesIds(
						layout.getGroupId(), 0, layout, portletId, false));

			Iterator<String> iterator = portletPreferencesJSONObject.keys();

			while (iterator.hasNext()) {
				String key = iterator.next();

				portletPreferencesTranslator.translate(
					portletPreferencesJSONObject, key, portletPreferences);
			}

			portletPreferences.store();
		}

		if (rootPortletId.equals(PortletKeys.NESTED_PORTLETS)) {
			JSONArray columnsJSONArray =
				portletPreferencesJSONObject.getJSONArray("columns");

			_addLayoutColumns(
				layout,
				StringBundler.concat(
					StringPool.UNDERLINE, portletId,
					StringPool.DOUBLE_UNDERLINE,
					LayoutTypePortletConstants.COLUMN_PREFIX),
				columnsJSONArray);
		}
	}

	private void _addLayoutColumns(
			Layout layout, String columnPrefix, JSONArray columnsJSONArray)
		throws Exception {

		if (columnsJSONArray == null) {
			return;
		}

		for (int i = 0; i < columnsJSONArray.length(); i++) {
			JSONArray columnJSONArray = columnsJSONArray.getJSONArray(i);

			_addLayoutColumn(layout, columnPrefix + (i + 1), columnJSONArray);
		}
	}

	private void _addLayouts(
			boolean privateLayout, long parentLayoutId,
			JSONArray layoutsJSONArray)
		throws Exception {

		if (layoutsJSONArray == null) {
			return;
		}

		for (int i = 0; i < layoutsJSONArray.length(); i++) {
			JSONObject layoutJSONObject = layoutsJSONArray.getJSONObject(i);

			_addLayout(privateLayout, parentLayoutId, layoutJSONObject);
		}
	}

	private void _addPrimaryKey(String className, long primaryKey) {
		Set<Long> primaryKeys = _primaryKeys.get(className);

		if (primaryKeys == null) {
			primaryKeys = new HashSet<>();

			_primaryKeys.put(className, primaryKeys);
		}

		primaryKeys.add(primaryKey);
	}

	private DDMForm _deserializeJSONDDMForm(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				ddmFormJSONDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private DDMForm _deserializeXSD(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				ddmFormXSDDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private String _getDDMStructureLanguage(String fileName) {
		String extension = FileUtil.getExtension(fileName);

		if (extension.equals(TemplateConstants.LANG_TYPE_JSON) ||
			extension.equals(TemplateConstants.LANG_TYPE_XML)) {

			return extension;
		}

		return TemplateConstants.LANG_TYPE_XML;
	}

	private JSONObject _getDefaultPortletJSONObject(String journalArticleId) {
		return JSONUtil.put(
			"portletId", _JOURNAL_CONTENT_PORTLET_ID
		).put(
			"portletPreferences",
			JSONUtil.put(
				"articleId", journalArticleId
			).put(
				"groupId", groupId
			).put(
				"portletSetupPortletDecoratorId", "borderless"
			)
		);
	}

	private String _getJournalId(String fileName) {
		String id = FileUtil.stripExtension(fileName);

		id = StringUtil.toUpperCase(id);

		return StringUtil.replace(id, CharPool.SPACE, CharPool.DASH);
	}

	private String[] _getJSONArrayAsStringArray(
		JSONObject jsonObject, String key) {

		JSONArray jsonArray = jsonObject.getJSONArray(key);

		if (jsonArray != null) {
			return ArrayUtil.toStringArray(jsonArray);
		}

		return new String[0];
	}

	private JSONObject _getJSONObject(String fileName) throws Exception {
		String json = null;

		try (InputStream inputStream = getInputStream(fileName)) {
			if (inputStream == null) {
				return null;
			}

			json = StringUtil.read(inputStream);
		}

		json = StringUtil.replace(
			json, new String[] {"${companyId}", "${groupId}", "${userId}"},
			new String[] {
				String.valueOf(companyId), String.valueOf(groupId),
				String.valueOf(userId)
			});

		return JSONFactoryUtil.createJSONObject(json);
	}

	private String _getKey(String name) {
		name = StringUtil.replace(name, CharPool.SPACE, CharPool.DASH);

		name = StringUtil.toUpperCase(name);

		if (appendVersion) {
			name = name + StringPool.DASH + version;
		}

		return name;
	}

	private File[] _listFiles(File dir) {
		File[] files1 = dir.listFiles();

		if (files1 == null) {
			return new File[0];
		}

		List<File> files2 = new ArrayList<>();

		for (File file : files1) {
			if (file.isFile()) {
				files2.add(file);
			}
		}

		return files2.toArray(new File[0]);
	}

	private String _replaceFileEntryURL(String content) throws Exception {
		Matcher matcher = _fileEntryPattern.matcher(content);

		while (matcher.find()) {
			String fileName = matcher.group(1);

			FileEntry fileEntry = _fileEntries.get(fileName);

			String fileEntryURL = StringPool.BLANK;

			if (fileEntry != null) {
				fileEntryURL = _dlURLHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK);
			}

			content = matcher.replaceFirst(fileEntryURL);

			matcher.reset(content);
		}

		return content;
	}

	private String _replaceLinksToLayouts(String content) throws Exception {
		Matcher matcher = _groupIdPattern.matcher(content);

		while (matcher.find()) {
			content = matcher.replaceFirst(String.valueOf(groupId));

			matcher.reset(content);
		}

		return content;
	}

	private void _resetLayoutColumns(Layout layout) throws PortalException {
		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		Set<Map.Entry<String, String>> entries = unicodeProperties.entrySet();

		Iterator<Map.Entry<String, String>> iterator = entries.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();

			String key = entry.getKey();

			if (!key.startsWith("column-")) {
				continue;
			}

			String[] portletIds = StringUtil.split(entry.getValue());

			for (String portletId : portletIds) {
				try {
					portletPreferencesLocalService.deletePortletPreferences(
						PortletKeys.PREFS_OWNER_ID_DEFAULT,
						PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
						portletId);
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to delete portlet preferences for " +
								"portlet " + portletId,
							portalException);
					}
				}
			}

			iterator.remove();
		}

		layout.setTypeSettingsProperties(unicodeProperties);

		layoutLocalService.updateLayout(layout);
	}

	private void _setServiceContext(String name) {
		JSONObject assetJSONObject = _assetJSONObjectMap.get(name);

		String[] assetTagNames = null;

		if (assetJSONObject != null) {
			assetTagNames = _getJSONArrayAsStringArray(assetJSONObject, "tags");
		}

		serviceContext.setAssetTagNames(assetTagNames);
	}

	private void _setUpAssets(JSONArray assetsJSONArray) {
		if (assetsJSONArray == null) {
			return;
		}

		for (int i = 0; i < assetsJSONArray.length(); i++) {
			JSONObject assetJSONObject = assetsJSONArray.getJSONObject(i);

			String name = assetJSONObject.getString("name");

			_assetJSONObjectMap.put(name, assetJSONObject);
		}
	}

	private void _setUpAssets(String fileName) throws Exception {
		if (!updateModeEnabled && !isCompanyGroup()) {
			List<AssetTag> assetTags = assetTagLocalService.getGroupTags(
				groupId);

			for (AssetTag assetTag : assetTags) {
				assetTagLocalService.deleteAssetTag(assetTag);
			}

			repositoryLocalService.deleteRepositories(groupId);

			journalArticleLocalService.deleteArticles(groupId);

			ddmTemplateLocalService.deleteTemplates(groupId);

			ddmStructureLocalService.deleteStructures(groupId);
		}

		JSONObject jsonObject = _getJSONObject(fileName);

		if (jsonObject != null) {
			JSONArray assetsJSONArray = jsonObject.getJSONArray("assets");

			_setUpAssets(assetsJSONArray);
		}

		addDLFileEntries(_DL_DOCUMENTS_DIR_NAME);

		_addApplicationDisplayTemplates(_APPLICATION_DISPLAY_TEMPLATE_DIR_NAME);

		addDDLStructures(_DDL_STRUCTURE_DIR_NAME);

		addDDMStructures(StringPool.BLANK, _JOURNAL_DDM_STRUCTURES_DIR_NAME);

		addDDMTemplates(StringPool.BLANK, _JOURNAL_DDM_TEMPLATES_DIR_NAME);

		addLayoutPrototype(_LAYOUT_PROTOTYPE_DIR_NAME);
	}

	private void _setUpSettings(String fileName) throws Exception {
		if (targetClassName.equals(Group.class.getName())) {
			return;
		}

		JSONObject jsonObject = _getJSONObject(fileName);

		if (jsonObject == null) {
			return;
		}

		LayoutSetPrototype layoutSetPrototype =
			layoutSetPrototypeLocalService.getLayoutSetPrototype(
				getTargetClassPK());

		String layoutSetPrototypeSettings = jsonObject.getString(
			"layoutSetPrototypeSettings", StringPool.BLANK);

		layoutSetPrototype.setSettings(layoutSetPrototypeSettings);

		layoutSetPrototypeLocalService.updateLayoutSetPrototype(
			layoutSetPrototype);
	}

	private void _setUpSitemap(String fileName) throws Exception {
		if (!updateModeEnabled) {
			layoutLocalService.deleteLayouts(
				groupId, true, new ServiceContext());

			layoutLocalService.deleteLayouts(
				groupId, false, new ServiceContext());
		}

		JSONObject jsonObject = _getJSONObject(fileName);

		if (jsonObject == null) {
			return;
		}

		_defaultLayoutTemplateId = jsonObject.getString(
			"layoutTemplateId", StringPool.BLANK);

		_updateLayoutSetThemeId(jsonObject);

		JSONArray layoutsJSONArray = jsonObject.getJSONArray("layouts");

		if (layoutsJSONArray != null) {
			_addLayouts(
				false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
				layoutsJSONArray);
		}
		else {
			JSONArray publicPagesJSONArray = jsonObject.getJSONArray(
				"publicPages");

			if (publicPagesJSONArray != null) {
				_addLayouts(
					false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
					publicPagesJSONArray);
			}

			JSONArray privatePagesJSONArray = jsonObject.getJSONArray(
				"privatePages");

			if (privatePagesJSONArray != null) {
				_addLayouts(
					true, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
					privatePagesJSONArray);
			}
		}
	}

	private void _updateLayoutSetThemeId(JSONObject sitemapJSONObject)
		throws Exception {

		String themeId = sitemapJSONObject.getString("themeId");

		if (Validator.isNotNull(themeId)) {
			Theme theme = themeLocalService.fetchTheme(companyId, themeId);

			if (theme == null) {
				themeId = null;
			}
		}

		if (Validator.isNull(themeId)) {
			int pos = servletContextName.indexOf("-theme");

			if (pos != -1) {
				themeId =
					servletContextName.substring(0, pos) +
						PortletConstants.WAR_SEPARATOR + servletContextName;

				themeId = portal.getJsSafePortletId(themeId);

				Theme theme = themeLocalService.fetchTheme(companyId, themeId);

				if (theme == null) {
					themeId = null;
				}
			}
		}

		if (Validator.isNotNull(themeId)) {
			layoutSetLocalService.updateLookAndFeel(
				groupId, themeId, null, null);
		}
	}

	private static final String _APPLICATION_DISPLAY_TEMPLATE_DIR_NAME =
		"/templates/application_display";

	private static final Object[][] _APPLICATION_DISPLAY_TEMPLATE_TYPES = {
		{"asset_category", "com.liferay.asset.kernel.model.AssetCategory"},
		{"asset_entry", "com.liferay.asset.kernel.model.AssetEntry"},
		{"asset_tag", "com.liferay.asset.kernel.model.AssetTag"},
		{"blogs_entry", "com.liferay.blogs.model.BlogsEntry"},
		{
			"bread_crumb",
			"com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry"
		},
		{
			"document_library",
			"com.liferay.portal.kernel.repository.model.FileEntry"
		},
		{
			"language_entry",
			"com.liferay.portal.kernel.servlet.taglib.ui.LanguageEntry"
		},
		{"rss_feed", "com.liferay.rss.web.internal.util.RSSFeed"},
		{"site_map", "com.liferay.portal.kernel.model.LayoutSet"},
		{"site_navigation", "com.liferay.portal.kernel.theme.NavItem"},
		{"wiki_page", "com.liferay.wiki.model.WikiPage"}
	};

	private static final String _DDL_STRUCTURE_DIR_NAME =
		"/templates/dynamic_data_list/structure";

	private static final String _DDL_STRUCTURE_DISPLAY_TEMPLATE_DIR_NAME =
		"/templates/dynamic_data_list/display_template";

	private static final String _DDL_STRUCTURE_FORM_TEMPLATE_DIR_NAME =
		"/templates/dynamic_data_list/form_template";

	private static final String _DL_DOCUMENTS_DIR_NAME =
		"/document_library/documents/";

	private static final String _JOURNAL_ARTICLES_DIR_NAME =
		"/journal/articles/";

	private static final String _JOURNAL_CONTENT_PORTLET_ID =
		"com_liferay_journal_content_web_portlet_JournalContentPortlet";

	private static final String _JOURNAL_DDM_STRUCTURES_DIR_NAME =
		"/journal/structures/";

	private static final String _JOURNAL_DDM_TEMPLATES_DIR_NAME =
		"/journal/templates/";

	private static final String _LAYOUT_PROTOTYPE_DIR_NAME = "/templates/page";

	private static final Log _log = LogFactoryUtil.getLog(
		FileSystemImporter.class);

	private static final Pattern _fileEntryPattern = Pattern.compile(
		"\\[\\$FILE=([^\\$]+)\\$\\]");
	private static final Pattern _groupIdPattern = Pattern.compile(
		"\\[\\$GROUP_ID\\$\\]");

	private final Map<String, JSONObject> _assetJSONObjectMap = new HashMap<>();
	private final Set<Long> _ddmStructureIds = new HashSet<>();
	private String _defaultLayoutTemplateId;
	private final DLURLHelper _dlURLHelper;
	private final Map<String, FileEntry> _fileEntries = new HashMap<>();
	private final Map<String, Set<Long>> _primaryKeys = new HashMap<>();
	private File _resourcesDir;

}