/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.util;

import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.util.DDMXML;
import com.liferay.exportimport.resources.importer.portlet.preferences.PortletPreferencesTranslator;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.search.index.IndexStatusManager;

import java.io.File;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Raymond Augé
 * @author Ryan Park
 */
public class ResourceImporter extends FileSystemImporter {

	public ResourceImporter(
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

		super(
			assetTagLocalService, ddmFormJSONDeserializer,
			ddmFormXSDDeserializer, ddmStructureLocalService,
			ddmTemplateLocalService, ddmxml, dlAppLocalService,
			dlFileEntryLocalService, dlFolderLocalService, indexStatusManager,
			indexerRegistry, journalArticleLocalService,
			journalFolderLocalService, layoutLocalService,
			layoutPrototypeLocalService, layoutSetLocalService,
			layoutSetPrototypeLocalService, mimeTypes, portal,
			portletPreferencesFactory, portletPreferencesLocalService,
			serviceTrackerMap, repositoryLocalService, saxReader,
			themeLocalService, dlURLHelper);
	}

	@Override
	public void importResources() throws Exception {
		doImportResources();
	}

	@Override
	protected void addApplicationDisplayTemplate(
			String parentDirName, String dirName, long classNameId)
		throws Exception {

		Set<String> resourcePaths = servletContext.getResourcePaths(
			_getResourcePath(parentDirName) + StringPool.SLASH + dirName);

		if (resourcePaths == null) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			URL url = servletContext.getResource(resourcePath);

			URLConnection urlConnection = url.openConnection();

			String script = StringUtil.read(urlConnection.getInputStream());

			if (Validator.isNull(script)) {
				continue;
			}

			File file = new File(resourcePath);

			addApplicationDisplayTemplate(script, file, classNameId);
		}
	}

	@Override
	protected void addDDLDisplayTemplates(
			String ddmStructureKey, String dirName, String fileName)
		throws Exception {

		Set<String> resourcePaths = servletContext.getResourcePaths(
			_getResourcePath(dirName) + StringPool.SLASH + fileName);

		if (resourcePaths == null) {
			return;
		}

		DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
			groupId, PortalUtil.getClassNameId(DDLRecordSet.class),
			ddmStructureKey);

		for (String resourcePath : resourcePaths) {
			URL url = servletContext.getResource(resourcePath);

			URLConnection urlConnection = url.openConnection();

			String script = StringUtil.read(urlConnection.getInputStream());

			if (Validator.isNull(script)) {
				return;
			}

			addDDMTemplate(
				groupId, ddmStructure.getStructureId(), resourcePath,
				getDDMTemplateLanguage(resourcePath), script,
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, StringPool.BLANK);
		}
	}

	@Override
	protected void addDDLFormTemplates(
			String ddmStructureKey, String dirName, String fileName)
		throws Exception {

		Set<String> resourcePaths = servletContext.getResourcePaths(
			_getResourcePath(dirName) + StringPool.SLASH + fileName);

		if (resourcePaths == null) {
			return;
		}

		DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
			groupId, PortalUtil.getClassNameId(DDLRecordSet.class),
			ddmStructureKey);

		for (String resourcePath : resourcePaths) {
			URL url = servletContext.getResource(resourcePath);

			URLConnection urlConnection = url.openConnection();

			String script = StringUtil.read(urlConnection.getInputStream());

			if (Validator.isNull(script)) {
				return;
			}

			addDDMTemplate(
				groupId, ddmStructure.getStructureId(), resourcePath, "xsd",
				script, DDMTemplateConstants.TEMPLATE_TYPE_FORM,
				DDMTemplateConstants.TEMPLATE_MODE_CREATE);
		}
	}

	@Override
	protected void addDDLStructures(String dirName) throws Exception {
		Set<String> resourcePaths = servletContext.getResourcePaths(
			_getResourcePath(dirName));

		if (resourcePaths == null) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			File file = new File(resourcePath);

			URL url = servletContext.getResource(resourcePath);

			URLConnection urlConnection = url.openConnection();

			addDDMStructures(
				FileUtil.stripExtension(file.getName()),
				urlConnection.getInputStream());
		}
	}

	@Override
	protected void addDDMStructures(String parentStructureId, String dirName)
		throws Exception {

		Set<String> resourcePaths = servletContext.getResourcePaths(
			_getResourcePath(dirName));

		if (resourcePaths == null) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith(StringPool.SLASH)) {
				continue;
			}

			String name = FileUtil.getShortFileName(resourcePath);

			URL url = servletContext.getResource(resourcePath);

			URLConnection urlConnection = url.openConnection();

			addDDMStructures(
				parentStructureId, name, urlConnection.getInputStream());
		}
	}

	@Override
	protected void addDDMTemplates(String ddmStructureKey, String dirName)
		throws Exception {

		Set<String> resourcePaths = servletContext.getResourcePaths(
			_getResourcePath(dirName));

		if (resourcePaths == null) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith(StringPool.SLASH)) {
				continue;
			}

			String name = FileUtil.getShortFileName(resourcePath);

			URL url = servletContext.getResource(resourcePath);

			URLConnection urlConnection = url.openConnection();

			addDDMTemplates(
				ddmStructureKey, name, urlConnection.getInputStream());
		}
	}

	@Override
	protected void addDLFileEntries(String dirName) throws Exception {
		Set<String> resourcePaths = servletContext.getResourcePaths(
			_getResourcePath(dirName));

		if (resourcePaths == null) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith(StringPool.SLASH)) {
				addDLFolder(
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, resourcePath);
			}
			else {
				addDLFileEntry(resourcePath);
			}
		}
	}

	protected void addDLFileEntry(String resourcePath) throws Exception {
		Long parentFolderId = _folderIds.get(
			FileUtil.getPath(resourcePath) + StringPool.SLASH);

		if (parentFolderId == null) {
			parentFolderId = 0L;
		}

		URL url = servletContext.getResource(resourcePath);

		URLConnection urlConnection = url.openConnection();

		addDLFileEntry(
			parentFolderId, FileUtil.getShortFileName(resourcePath),
			urlConnection.getInputStream(), urlConnection.getContentLength());
	}

	@Override
	protected long addDLFolder(long parentFolderId, String resourcePath)
		throws Exception {

		long folderId = super.addDLFolder(
			parentFolderId,
			FileUtil.getShortFileName(FileUtil.getPath(resourcePath)));

		_folderIds.put(resourcePath, folderId);

		Set<String> resourcePaths = servletContext.getResourcePaths(
			resourcePath);

		if ((resourcePaths == null) || resourcePaths.isEmpty()) {
			return folderId;
		}

		for (String curResourcePath : resourcePaths) {
			if (curResourcePath.endsWith(StringPool.SLASH)) {
				addDLFolder(folderId, curResourcePath);
			}
			else {
				addDLFileEntry(curResourcePath);
			}
		}

		return folderId;
	}

	@Override
	protected void addJournalArticles(
			long ddmStructureId, String ddmTemplateKey, String dirName,
			long folderId)
		throws Exception {

		Set<String> resourcePaths = servletContext.getResourcePaths(
			_getResourcePath(dirName));

		if (resourcePaths == null) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith(StringPool.SLASH)) {
				String folderName = FileUtil.getShortFileName(
					StringUtil.replaceLast(
						resourcePath, CharPool.FORWARD_SLASH,
						StringPool.BLANK));

				JournalFolder journalFolder =
					journalFolderLocalService.fetchFolder(groupId, folderName);

				if (journalFolder == null) {
					journalFolder = journalFolderLocalService.addFolder(
						null, userId, groupId, folderId, folderName,
						StringPool.BLANK, serviceContext);
				}

				addJournalArticles(
					ddmStructureId, ddmTemplateKey,
					dirName + CharPool.FORWARD_SLASH + folderName,
					journalFolder.getFolderId());
			}
			else {
				String name = FileUtil.getShortFileName(resourcePath);

				URL url = servletContext.getResource(resourcePath);

				URLConnection urlConnection = url.openConnection();

				addJournalArticles(
					ddmStructureId, ddmTemplateKey, name, folderId,
					urlConnection.getInputStream());
			}
		}
	}

	@Override
	protected void addLayoutPrototype(String dirName) throws Exception {
		Set<String> resourcePaths = servletContext.getResourcePaths(
			_getResourcePath(dirName));

		if (resourcePaths == null) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			String extension = FileUtil.getExtension(resourcePath);

			if (!extension.equals("json")) {
				return;
			}

			URL url = servletContext.getResource(resourcePath);

			URLConnection urlConnection = url.openConnection();

			addLayoutPrototype(urlConnection.getInputStream());
		}
	}

	@Override
	protected InputStream getInputStream(String fileName) throws Exception {
		URL url = servletContext.getResource(_getResourcePath(fileName));

		if (url == null) {
			return null;
		}

		URLConnection urlConnection = url.openConnection();

		return urlConnection.getInputStream();
	}

	private String _getResourcePath(String dirName) {
		if (resourcesDir.endsWith(StringPool.SLASH) &&
			dirName.startsWith(StringPool.SLASH)) {

			return resourcesDir.concat(dirName.substring(1));
		}

		return resourcesDir.concat(dirName);
	}

	private final Map<String, Long> _folderIds = new HashMap<>();

}