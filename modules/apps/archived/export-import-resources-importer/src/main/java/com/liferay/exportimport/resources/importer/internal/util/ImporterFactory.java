/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.util;

import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.util.DDMXML;
import com.liferay.exportimport.resources.importer.portlet.preferences.PortletPreferencesTranslator;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.plugin.PluginPackageUtil;
import com.liferay.portal.search.index.IndexStatusManager;

import jakarta.servlet.ServletContext;

import java.net.URL;
import java.net.URLConnection;

import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = ImporterFactory.class)
public class ImporterFactory {

	public static final String RESOURCES_DIR =
		"/WEB-INF/classes/resources-importer/";

	public static final String TEMPLATES_DIR =
		"/WEB-INF/classes/templates-importer/";

	public Importer createImporter(
			long companyId, ServletContext servletContext,
			PluginPackageProperties pluginPackageProperties)
		throws Exception {

		Importer importer = null;

		String resourcesDir = pluginPackageProperties.getResourcesDir();

		Set<String> resourcePaths = servletContext.getResourcePaths(
			RESOURCES_DIR);
		Set<String> templatePaths = servletContext.getResourcePaths(
			TEMPLATES_DIR);

		URL privateLARURL = null;
		URL publicLARURL = servletContext.getResource(
			RESOURCES_DIR.concat("archive.lar"));

		if (publicLARURL == null) {
			privateLARURL = servletContext.getResource(
				RESOURCES_DIR.concat("private.lar"));

			publicLARURL = servletContext.getResource(
				RESOURCES_DIR.concat("public.lar"));
		}

		if ((privateLARURL != null) || (publicLARURL != null)) {
			LARImporter larImporter = _getLARImporter();

			if (privateLARURL != null) {
				URLConnection privateLARURLConnection =
					privateLARURL.openConnection();

				larImporter.setPrivateLARInputStream(
					privateLARURLConnection.getInputStream());
			}

			if (publicLARURL != null) {
				URLConnection publicLARURLConnection =
					publicLARURL.openConnection();

				larImporter.setPublicLARInputStream(
					publicLARURLConnection.getInputStream());
			}

			importer = larImporter;
		}
		else if ((resourcePaths != null) && !resourcePaths.isEmpty()) {
			importer = _getResourceImporter();

			importer.setResourcesDir(RESOURCES_DIR);
		}
		else if ((templatePaths != null) && !templatePaths.isEmpty()) {
			importer = _getResourceImporter();

			Group group = _groupLocalService.getCompanyGroup(companyId);

			importer.setGroupId(group.getGroupId());

			importer.setResourcesDir(TEMPLATES_DIR);
		}
		else if (Validator.isNotNull(resourcesDir)) {
			importer = _getFileSystemImporter();

			importer.setResourcesDir(resourcesDir);
		}

		if (importer == null) {
			throw new ImporterException("No valid importer found");
		}

		_configureImporter(
			companyId, importer, servletContext, pluginPackageProperties);

		return importer;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, PortletPreferencesTranslator.class,
			"portlet.preferences.translator.portlet.id");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _configureImporter(
			long companyId, Importer importer, ServletContext servletContext,
			PluginPackageProperties pluginPackageProperties)
		throws Exception {

		importer.setAppendVersion(pluginPackageProperties.isAppendVersion());
		importer.setCompanyId(companyId);
		importer.setDeveloperModeEnabled(
			pluginPackageProperties.isDeveloperModeEnabled());
		importer.setIndexAfterImport(
			pluginPackageProperties.indexAfterImport());
		importer.setServletContext(servletContext);
		importer.setServletContextName(servletContext.getServletContextName());
		importer.setTargetClassName(
			pluginPackageProperties.getTargetClassName());

		String targetValue = pluginPackageProperties.getTargetValue();

		if (Validator.isNull(targetValue)) {
			targetValue = TextFormatter.format(
				servletContext.getServletContextName(), TextFormatter.J);
		}

		importer.setTargetValue(targetValue);

		importer.setUpdateModeEnabled(
			pluginPackageProperties.isUpdateModeEnabled());

		PluginPackage pluginPackage =
			PluginPackageUtil.getInstalledPluginPackage(
				servletContext.getServletContextName());

		importer.setVersion(pluginPackage.getVersion());

		importer.afterPropertiesSet();
	}

	private FileSystemImporter _getFileSystemImporter() {
		return new FileSystemImporter(
			_assetTagLocalService, _jsonDDMFormDeserializer,
			_xsdDDMFormDeserializer, _ddmStructureLocalService,
			_ddmTemplateLocalService, _ddmxml, _dlAppLocalService,
			_dlFileEntryLocalService, _dlFolderLocalService,
			_indexStatusManager, _indexerRegistry, _journalArticleLocalService,
			_journalFolderLocalService, _layoutLocalService,
			_layoutPrototypeLocalService, _layoutSetLocalService,
			_layoutSetPrototypeLocalService, _mimeTypes, _portal,
			_portletPreferencesFactory, _portletPreferencesLocalService,
			_serviceTrackerMap, _repositoryLocalService, _saxReader,
			_themeLocalService, _dlURLHelper);
	}

	private LARImporter _getLARImporter() {
		return new LARImporter();
	}

	private ResourceImporter _getResourceImporter() {
		return new ResourceImporter(
			_assetTagLocalService, _jsonDDMFormDeserializer,
			_xsdDDMFormDeserializer, _ddmStructureLocalService,
			_ddmTemplateLocalService, _ddmxml, _dlAppLocalService,
			_dlFileEntryLocalService, _dlFolderLocalService,
			_indexStatusManager, _indexerRegistry, _journalArticleLocalService,
			_journalFolderLocalService, _layoutLocalService,
			_layoutPrototypeLocalService, _layoutSetLocalService,
			_layoutSetPrototypeLocalService, _mimeTypes, _portal,
			_portletPreferencesFactory, _portletPreferencesLocalService,
			_serviceTrackerMap, _repositoryLocalService, _saxReader,
			_themeLocalService, _dlURLHelper);
	}

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DDMXML _ddmxml;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private IndexStatusManager _indexStatusManager;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference(target = "(ddm.form.deserializer.type=json)")
	private DDMFormDeserializer _jsonDDMFormDeserializer;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private MimeTypes _mimeTypes;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference
	private SAXReader _saxReader;

	private ServiceTrackerMap<String, PortletPreferencesTranslator>
		_serviceTrackerMap;

	@Reference
	private ThemeLocalService _themeLocalService;

	@Reference(target = "(ddm.form.deserializer.type=xsd)")
	private DDMFormDeserializer _xsdDDMFormDeserializer;

}