/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.asset.kernel.NoSuchClassTypeException;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.validator.FragmentEntryValidator;
import com.liferay.headless.delivery.dto.v1_0.ClientExtension;
import com.liferay.headless.delivery.dto.v1_0.ContentSubtype;
import com.liferay.headless.delivery.dto.v1_0.ContentType;
import com.liferay.headless.delivery.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.delivery.dto.v1_0.MasterPage;
import com.liferay.headless.delivery.dto.v1_0.PageDefinition;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.headless.delivery.dto.v1_0.PageRule;
import com.liferay.headless.delivery.dto.v1_0.PageTemplate;
import com.liferay.headless.delivery.dto.v1_0.PageTemplateCollection;
import com.liferay.headless.delivery.dto.v1_0.Settings;
import com.liferay.headless.delivery.dto.v1_0.StyleBook;
import com.liferay.headless.delivery.dto.v1_0.UtilityPageTemplate;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.layout.importer.PortletPermissionsImporter;
import com.liferay.layout.internal.importer.exception.DropzoneLayoutStructureItemException;
import com.liferay.layout.internal.importer.helper.PortletConfigurationImporterHelper;
import com.liferay.layout.internal.importer.structure.util.CollectionItemLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.CollectionLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.ColumnLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.ContainerLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.DropZoneLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.FormLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.FormRelationshipLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.FormStepContainerLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.FormStepItemLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.FragmentDropZoneLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.FragmentLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.LayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.LayoutStructureRuleImporter;
import com.liferay.layout.internal.importer.structure.util.RowLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.structure.util.WidgetLayoutStructureItemImporter;
import com.liferay.layout.internal.importer.validator.DisplayPageTemplateValidator;
import com.liferay.layout.internal.importer.validator.MasterPageValidator;
import com.liferay.layout.internal.importer.validator.PageDefinitionValidator;
import com.liferay.layout.internal.importer.validator.PageTemplateCollectionValidator;
import com.liferay.layout.internal.importer.validator.PageTemplateValidator;
import com.liferay.layout.internal.importer.validator.UtilityPageTemplateValidator;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateExportImportConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.util.CheckUnlockedLayoutThreadLocal;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.layout.util.constants.LayoutStructureConstants;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.utility.page.constants.LayoutUtilityPageExportImportConstants;
import com.liferay.layout.utility.page.converter.LayoutUtilityPageEntryTypeConverter;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = LayoutsImporter.class)
public class LayoutsImporterImpl implements LayoutsImporter {

	@Override
	public void importFile(
			long userId, long groupId, File file,
			LayoutsImportStrategy layoutsImportStrategy,
			boolean preserveItemIds)
		throws Exception {

		importFile(
			userId, groupId,
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			file, layoutsImportStrategy, preserveItemIds);
	}

	@Override
	public List<LayoutsImporterResultEntry> importFile(
			long userId, long groupId, long layoutPageTemplateCollectionId,
			File file, LayoutsImportStrategy layoutsImportStrategy,
			boolean preserveItemIds)
		throws Exception {

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			new ArrayList<>();

		ZipReader zipReader = _zipReaderFactory.getZipReader(file);

		_processMasterLayoutLayoutPageTemplateEntries(
			groupId, layoutsImporterResultEntries, layoutsImportStrategy,
			preserveItemIds, userId, zipReader);

		_processBasicLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId,
			layoutsImporterResultEntries, layoutsImportStrategy,
			preserveItemIds, userId, zipReader);

		Map<String, LayoutPageTemplateCollection>
			layoutPageTemplateCollectionsMap =
				_processDisplayPageLayoutPageTemplateCollections(
					groupId, layoutPageTemplateCollectionId,
					layoutsImporterResultEntries, layoutsImportStrategy,
					zipReader);

		_processDisplayPageLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId,
			layoutPageTemplateCollectionsMap, layoutsImporterResultEntries,
			layoutsImportStrategy, preserveItemIds, userId, zipReader);

		_processLayoutUtilityPageEntries(
			groupId, layoutsImporterResultEntries, layoutsImportStrategy,
			preserveItemIds, userId, zipReader);

		return layoutsImporterResultEntries;
	}

	@Override
	public Layout importLayoutSettings(
			long userId, Layout layout, String settingsJSON)
		throws Exception {

		Settings settings = Settings.toDTO(settingsJSON);

		return _updateLayoutSettings(userId, layout, settings);
	}

	@Override
	public List<FragmentEntryLink> importPageElement(
			Layout layout, LayoutStructure layoutStructure, String parentItemId,
			String pageElementJSON, int position, boolean preserveItemIds)
		throws Exception {

		Consumer<LayoutStructure> consumer = processedLayoutStructure -> {
			try {
				_updateLayoutPageTemplateStructure(
					layout, processedLayoutStructure);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		};

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		return _importPageElement(
			consumer, layout, layoutStructure, parentItemId, pageElementJSON,
			position, preserveItemIds, segmentsExperienceId);
	}

	@Override
	public List<FragmentEntryLink> importPageElement(
			long userId, Layout layout, LayoutStructure layoutStructure,
			String parentItemId, String pageElementJSON, int position,
			boolean preserveItemIds, long segmentsExperienceId)
		throws Exception {

		Consumer<LayoutStructure> consumer = processedLayoutStructure -> {
			try {
				_layoutPageTemplateStructureLocalService.
					updateLayoutPageTemplateStructureData(
						userId, layout.getGroupId(), layout.getPlid(),
						segmentsExperienceId,
						processedLayoutStructure.toString());
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		};

		return _importPageElement(
			consumer, layout, layoutStructure, parentItemId, pageElementJSON,
			position, preserveItemIds, segmentsExperienceId);
	}

	@Override
	public boolean validateFile(
			long groupId, long layoutPageTemplateCollectionId, File file)
		throws Exception {

		ZipReader zipReader = _zipReaderFactory.getZipReader(file);

		List<String> entries = zipReader.getEntries();

		for (String entry : entries) {
			String content = zipReader.getEntryAsString(entry);

			if (Validator.isNull(content)) {
				continue;
			}

			if (_isDisplayPageTemplateCollectionFile(entry) &&
				_isRootFolder(entries, entry) &&
				!_isValidDisplayPageLayoutPageTemplateCollection(
					content, entry, groupId, layoutPageTemplateCollectionId)) {

				return false;
			}

			if (_isDisplayPageTemplateFile(entry) &&
				_isRootFolder(entries, entry) &&
				!_isValidDisplayPageLayoutPageTemplateEntry(
					content, entry, groupId, layoutPageTemplateCollectionId)) {

				return false;
			}

			if (_isMasterPageFile(entry) &&
				!_isValidMasterLayoutLayoutPageTemplateEntry(
					content, entry, groupId)) {

				return false;
			}

			if ((layoutPageTemplateCollectionId <= 0) &&
				_isPageTemplateCollectionFile(entry) &&
				!_isValidBasicLayoutPageTemplateCollection(
					content, entry, groupId)) {

				return false;
			}

			if (_isPageTemplateFile(entry) &&
				!_isValidBasicLayoutPageTemplateEntry(
					content, entry, groupId, layoutPageTemplateCollectionId)) {

				return false;
			}
		}

		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_addLayoutStructureItemImporter(
			new CollectionItemLayoutStructureItemImporter());
		_addLayoutStructureItemImporter(
			new CollectionLayoutStructureItemImporter(
				_assetListEntryLocalService));
		_addLayoutStructureItemImporter(
			new ColumnLayoutStructureItemImporter());
		_addLayoutStructureItemImporter(
			new ContainerLayoutStructureItemImporter());
		_addLayoutStructureItemImporter(
			new DropZoneLayoutStructureItemImporter(
				_fragmentCollectionContributorRegistry,
				_fragmentCollectionLocalService, _fragmentEntryLocalService,
				_fragmentRendererRegistry));
		_addLayoutStructureItemImporter(new FormLayoutStructureItemImporter());
		_addLayoutStructureItemImporter(
			new FormRelationshipLayoutStructureItemImporter());
		_addLayoutStructureItemImporter(
			new FormStepContainerLayoutStructureItemImporter());
		_addLayoutStructureItemImporter(
			new FormStepItemLayoutStructureItemImporter());
		_addLayoutStructureItemImporter(
			new FragmentDropZoneLayoutStructureItemImporter());
		_addLayoutStructureItemImporter(
			new FragmentLayoutStructureItemImporter(
				_companyLocalService, _fragmentCollectionContributorRegistry,
				_fragmentCollectionService, _fragmentEntryLinkLocalService,
				_fragmentEntryLocalService, _fragmentEntryProcessorRegistry,
				_fragmentEntryValidator, _fragmentRendererRegistry,
				_portletConfigurationImporterHelper, _portletFileRepository,
				_portletLocalService, _portletPermissionsImporter,
				_segmentsExperienceLocalService));
		_addLayoutStructureItemImporter(new RowLayoutStructureItemImporter());
		_addLayoutStructureItemImporter(
			new WidgetLayoutStructureItemImporter(
				_fragmentEntryLinkLocalService, _fragmentEntryProcessorRegistry,
				_portletConfigurationImporterHelper, _portletLocalService,
				_portletPermissionsImporter, _portletRegistry));
	}

	private void _addClientExtensionEntryRel(
		String cetExternalReferenceCode, Layout layout,
		ServiceContext serviceContext, String type,
		Map<String, String> clientExtensionConfig, long userId) {

		CET cet = _cetManager.getCET(
			layout.getCompanyId(), cetExternalReferenceCode);

		if ((cet == null) || !Objects.equals(type, cet.getType())) {
			return;
		}

		List<ClientExtensionEntryRel> clientExtensionEntryRels =
			_clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
				_portal.getClassNameId(Layout.class), layout.getPlid());

		for (ClientExtensionEntryRel clientExtensionEntryRel :
				clientExtensionEntryRels) {

			if (cetExternalReferenceCode.equals(
					clientExtensionEntryRel.getCETExternalReferenceCode())) {

				_clientExtensionEntryRelLocalService.
					deleteClientExtensionEntryRel(clientExtensionEntryRel);
			}
		}

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		if (clientExtensionConfig != null) {
			for (Map.Entry<String, String> entry :
					clientExtensionConfig.entrySet()) {

				unicodeProperties.put(entry.getKey(), entry.getValue());
			}
		}

		try {
			_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
				userId, layout.getGroupId(),
				_portal.getClassNameId(Layout.class.getName()),
				layout.getPlid(), cetExternalReferenceCode, type,
				unicodeProperties.toString(), serviceContext);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private LayoutPageTemplateEntry _addLayoutPageTemplateEntry(
			long groupId, long layoutPageTemplateCollectionId, long classNameId,
			long classTypeId, String name, int layoutPageTemplateEntryType)
		throws PortalException {

		if (classNameId == 0) {
			return _layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, groupId, layoutPageTemplateCollectionId, null, name,
				layoutPageTemplateEntryType, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextThreadLocal.getServiceContext());
		}

		return _layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
			null, groupId, layoutPageTemplateCollectionId, null, classNameId,
			classTypeId, name, 0, WorkflowConstants.STATUS_APPROVED,
			ServiceContextThreadLocal.getServiceContext());
	}

	private void _addLayoutStructureItemImporter(
		LayoutStructureItemImporter layoutStructureItemImporter) {

		_layoutStructureItemImporters.put(
			layoutStructureItemImporter.getPageElementType(),
			layoutStructureItemImporter);
	}

	private void _deleteExistingPortletPreferences(long plid) {
		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid);

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			_portletPreferencesLocalService.deletePortletPreferences(
				portletPreferences);
		}
	}

	private LayoutPageTemplateCollection _getBasicLayoutPageTemplateCollection(
			long groupId, long layoutPageTemplateCollectionId,
			LayoutsImportStrategy layoutsImportStrategy,
			PageTemplateCollectionEntry pageTemplateCollectionEntry)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection = null;

		if (layoutPageTemplateCollectionId > 0) {
			layoutPageTemplateCollection =
				_layoutPageTemplateCollectionService.
					fetchLayoutPageTemplateCollection(
						layoutPageTemplateCollectionId);

			if (layoutPageTemplateCollection == null) {
				throw new PortalException(
					"Invalid layout page template collection ID: " +
						layoutPageTemplateCollectionId);
			}

			return layoutPageTemplateCollection;
		}

		String layoutPageTemplateCollectionKey =
			pageTemplateCollectionEntry.getKey();

		PageTemplateCollection pageTemplateCollection =
			pageTemplateCollectionEntry.getPageTemplateCollection();

		layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					groupId, layoutPageTemplateCollectionKey,
					LayoutPageTemplateEntryTypeConstants.BASIC);

		if (layoutPageTemplateCollection == null) {
			layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollection(
						groupId, pageTemplateCollection.getName(),
						layoutPageTemplateCollectionId,
						LayoutPageTemplateEntryTypeConstants.BASIC);

			if (layoutPageTemplateCollection == null) {
				return _layoutPageTemplateCollectionService.
					addLayoutPageTemplateCollection(
						null, groupId, layoutPageTemplateCollectionId, null,
						pageTemplateCollection.getName(),
						pageTemplateCollection.getDescription(),
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						ServiceContextThreadLocal.getServiceContext());
			}
		}

		if (Objects.equals(
				LayoutsImportStrategy.KEEP_BOTH, layoutsImportStrategy)) {

			return _layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					pageTemplateCollection.getUuid(), groupId,
					layoutPageTemplateCollectionId, null,
					_layoutPageTemplateCollectionLocalService.
						getUniqueLayoutPageTemplateCollectionName(
							groupId, layoutPageTemplateCollectionId,
							pageTemplateCollection.getName(),
							LayoutPageTemplateEntryTypeConstants.BASIC),
					pageTemplateCollection.getDescription(),
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextThreadLocal.getServiceContext());
		}
		else if (Objects.equals(
					LayoutsImportStrategy.OVERWRITE, layoutsImportStrategy)) {

			return _layoutPageTemplateCollectionService.
				updateLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					pageTemplateCollection.getName(),
					pageTemplateCollection.getDescription());
		}

		if (layoutPageTemplateCollection == null) {
			throw new PortalException(
				"Invalid layout page template collection ID: " +
					layoutPageTemplateCollectionId);
		}

		return layoutPageTemplateCollection;
	}

	private PageTemplateCollectionEntry
		_getDefaultPageTemplateCollectionEntry() {

		PageTemplateCollection pageTemplateCollection =
			new PageTemplateCollection() {
				{
					setName(() -> _PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT);
				}
			};

		return new PageTemplateCollectionEntry(
			_PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT, pageTemplateCollection);
	}

	private LayoutPageTemplateCollection
			_getDisplayPageLayoutPageTemplateCollection(
				long groupId, LayoutsImportStrategy layoutsImportStrategy,
				long layoutPageTemplateCollectionId,
				PageTemplateCollection pageTemplateCollection)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					groupId, pageTemplateCollection.getName(),
					layoutPageTemplateCollectionId,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

		if (layoutPageTemplateCollection == null) {
			return _layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, groupId, layoutPageTemplateCollectionId, null,
					pageTemplateCollection.getName(),
					pageTemplateCollection.getDescription(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					ServiceContextThreadLocal.getServiceContext());
		}

		if (Objects.equals(
				LayoutsImportStrategy.KEEP_BOTH, layoutsImportStrategy)) {

			return _layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, groupId, layoutPageTemplateCollectionId, null,
					_layoutPageTemplateCollectionLocalService.
						getUniqueLayoutPageTemplateCollectionName(
							groupId, layoutPageTemplateCollectionId,
							pageTemplateCollection.getName(),
							LayoutPageTemplateCollectionTypeConstants.
								DISPLAY_PAGE),
					pageTemplateCollection.getDescription(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					ServiceContextThreadLocal.getServiceContext());
		}
		else if (Objects.equals(
					LayoutsImportStrategy.OVERWRITE, layoutsImportStrategy)) {

			return _layoutPageTemplateCollectionService.
				updateLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					pageTemplateCollection.getName(),
					pageTemplateCollection.getDescription());
		}

		return layoutPageTemplateCollection;
	}

	private String _getDisplayPageLayoutPageTemplateCollectionPath(
		String fileName) {

		return StringUtil.removeSubstring(
			fileName,
			LayoutPageTemplateExportImportConstants.
				FILE_NAME_DISPLAY_PAGE_TEMPLATE_COLLECTION);
	}

	private long _getFileEntryId(long contentDocumentId) {
		try {
			FileEntry fileEntry = _dlAppService.getFileEntry(contentDocumentId);

			return fileEntry.getFileEntryId();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return 0;
	}

	private String _getKey(String defaultKey, String fileName, String name) {
		String[] pathParts = StringUtil.split(fileName, CharPool.SLASH);

		String key = defaultKey;

		if (Validator.isNotNull(name)) {
			key = name;
		}

		if (pathParts.length > 1) {
			key = pathParts[pathParts.length - 2];
		}

		key = StringUtil.replace(key, CharPool.SPACE, CharPool.DASH);
		key = StringUtil.toLowerCase(key);

		return key;
	}

	private String _getPageDefinitionJSON(
		String fileName, ZipReader zipReader) {

		String path = fileName.substring(
			0, fileName.lastIndexOf(StringPool.FORWARD_SLASH) + 1);

		return zipReader.getEntryAsString(
			path +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_PAGE_DEFINITION);
	}

	private Map<String, PageTemplateCollectionEntry>
			_getPageTemplateCollectionEntryMap(
				List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
				ZipReader zipReader)
		throws Exception {

		Map<String, PageTemplateCollectionEntry> pageTemplateCollectionMap =
			new HashMap<>();

		List<String> entries = zipReader.getEntries();

		for (String entry : entries) {
			if (!_isPageTemplateCollectionFile(entry)) {
				continue;
			}

			String[] pathParts = StringUtil.split(entry, CharPool.SLASH);

			String pageTemplateCollectionKey = "imported";

			if (pathParts.length > 1) {
				pageTemplateCollectionKey = pathParts[pathParts.length - 2];
			}

			String content = zipReader.getEntryAsString(entry);

			PageTemplateCollectionValidator.validatePageTemplateCollection(
				content);

			PageTemplateCollection pageTemplateCollection =
				_objectMapper.readValue(content, PageTemplateCollection.class);

			pageTemplateCollectionMap.put(
				pageTemplateCollectionKey,
				new PageTemplateCollectionEntry(
					pageTemplateCollectionKey, pageTemplateCollection));
		}

		if (MapUtil.isEmpty(pageTemplateCollectionMap)) {
			pageTemplateCollectionMap.put(
				_PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT,
				_getDefaultPageTemplateCollectionEntry());
		}

		for (String entry : entries) {
			if (!_isPageTemplateFile(entry)) {
				continue;
			}

			String content = zipReader.getEntryAsString(entry);

			PageTemplate pageTemplate = null;

			try {
				PageTemplateValidator.validatePageTemplate(content);

				pageTemplate = _objectMapper.readValue(
					content, PageTemplate.class);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Invalid page template for: " + entry, exception);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						entry, LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {entry},
							"x-could-not-be-imported-because-its-page-" +
								"template-is-invalid")));

				continue;
			}

			PageTemplateCollectionEntry pageTemplateCollectionEntry =
				pageTemplateCollectionMap.get(
					_getPageTemplateCollectionKey(entries, entry));

			try {
				String pageDefinitionJSON = _getPageDefinitionJSON(
					entry, zipReader);

				PageDefinitionValidator.validatePageDefinition(
					pageDefinitionJSON);

				PageDefinition pageDefinition = _objectMapper.readValue(
					pageDefinitionJSON, PageDefinition.class);

				pageTemplateCollectionEntry.addPageTemplateEntry(
					_getKey(
						_PAGE_TEMPLATE_ENTRY_KEY_DEFAULT, entry,
						pageTemplate.getName()),
					new PageTemplateEntry(
						pageTemplate, pageDefinition,
						_getThumbnail(entry, zipReader), entry));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid page definition for: " +
							pageTemplate.getName(),
						exception);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						pageTemplate.getName(),
						LayoutsImporterResultEntry.TYPE_COLLECTION,
						LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {pageTemplate.getName()},
							"x-could-not-be-imported-because-its-page-" +
								"definition-is-invalid")));
			}
		}

		return pageTemplateCollectionMap;
	}

	private String _getPageTemplateCollectionKey(
		List<String> entries, String fileName) {

		if (fileName.lastIndexOf(CharPool.SLASH) == -1) {
			return "imported";
		}

		String path = fileName.substring(
			0, fileName.lastIndexOf(StringPool.FORWARD_SLASH));

		int index = entries.indexOf(
			path + CharPool.SLASH +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_PAGE_TEMPLATE_COLLECTION);

		if (index < 0) {
			return _getPageTemplateCollectionKey(entries, path);
		}

		int pos = path.lastIndexOf(CharPool.SLASH);

		String layoutPageTemplateCollectionKey = path.substring(pos + 1);

		if (Validator.isNotNull(layoutPageTemplateCollectionKey)) {
			return layoutPageTemplateCollectionKey;
		}

		return _PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT;
	}

	private long _getParentLayoutPageTemplateCollectionId(
		List<String> entries, String fileName,
		long layoutPageTemplateCollectionId,
		Map<String, LayoutPageTemplateCollection>
			layoutPageTemplateCollectionsMap) {

		if (_isRootFolder(entries, fileName)) {
			return layoutPageTemplateCollectionId;
		}

		String[] pathParts = StringUtil.split(fileName, CharPool.SLASH);

		if (pathParts.length <= 2) {
			return layoutPageTemplateCollectionId;
		}

		String path = StringUtil.merge(
			ArrayUtil.subset(pathParts, 0, pathParts.length - 2),
			StringPool.SLASH);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionsMap.get(path + StringPool.SLASH);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId();
		}

		return layoutPageTemplateCollectionId;
	}

	private long _getPreviewFileEntryId(
			String className, long classPK, long groupId, Thumbnail thumbnail,
			long userId)
		throws Exception {

		if (thumbnail == null) {
			return 0;
		}

		Repository repository = _portletFileRepository.fetchPortletRepository(
			groupId, LayoutAdminPortletKeys.GROUP_PAGES);

		if (repository == null) {
			repository = _portletFileRepository.addPortletRepository(
				groupId, LayoutAdminPortletKeys.GROUP_PAGES,
				ServiceContextThreadLocal.getServiceContext());
		}

		String imageFileName = classPK + "_preview" + thumbnail.getExtension();

		FileEntry fileEntry = _portletFileRepository.fetchPortletFileEntry(
			groupId, repository.getDlFolderId(), imageFileName);

		if (fileEntry != null) {
			_portletFileRepository.deletePortletFileEntry(
				fileEntry.getFileEntryId());
		}

		fileEntry = _portletFileRepository.addPortletFileEntry(
			groupId, userId, className, classPK,
			LayoutAdminPortletKeys.GROUP_PAGES, repository.getDlFolderId(),
			thumbnail.getBytes(), imageFileName,
			MimeTypesUtil.getContentType(imageFileName), false);

		return fileEntry.getFileEntryId();
	}

	private String _getThemeId(long companyId, String themeName) {
		List<Theme> themes = ListUtil.filter(
			_themeLocalService.getThemes(companyId),
			theme -> Objects.equals(theme.getName(), themeName));

		if (ListUtil.isEmpty(themes)) {
			return null;
		}

		Theme theme = themes.get(0);

		return theme.getThemeId();
	}

	private Thumbnail _getThumbnail(String fileName, ZipReader zipReader) {
		String path = fileName.substring(
			0, fileName.lastIndexOf(StringPool.FORWARD_SLASH) + 1);

		for (String thumbnailExtension : _THUMBNAIL_VALID_EXTENSIONS) {
			byte[] bytes = zipReader.getEntryAsByteArray(
				path + _THUMBNAIL_FILE_NAME + thumbnailExtension);

			if (ArrayUtil.isNotEmpty(bytes)) {
				return new Thumbnail(bytes, thumbnailExtension);
			}
		}

		return null;
	}

	private List<FragmentEntryLink> _importPageElement(
			Consumer<LayoutStructure> consumer, Layout layout,
			LayoutStructure layoutStructure, String parentItemId,
			String pageElementJSON, int position, boolean preserveItemIds,
			long segmentsExperienceId)
		throws Exception {

		PageElement pageElement = _objectMapper.readValue(
			pageElementJSON, PageElement.class);

		List<FragmentEntryLink> fragmentEntryLinks = new ArrayList<>();

		_processPageElement(
			fragmentEntryLinks, layout, layoutStructure,
			LayoutStructureConstants.LATEST_PAGE_DEFINITION_VERSION,
			pageElement, parentItemId, position, preserveItemIds,
			segmentsExperienceId, new HashSet<>());

		consumer.accept(layoutStructure);

		return fragmentEntryLinks;
	}

	private boolean _isDisplayPageTemplateCollectionFile(String fileName) {
		return fileName.endsWith(
			CharPool.SLASH +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_DISPLAY_PAGE_TEMPLATE_COLLECTION);
	}

	private boolean _isDisplayPageTemplateFile(String fileName) {
		return fileName.endsWith(
			CharPool.SLASH +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_DISPLAY_PAGE_TEMPLATE);
	}

	private boolean _isMasterPageFile(String fileName) {
		return fileName.endsWith(
			CharPool.SLASH +
				LayoutPageTemplateExportImportConstants.FILE_NAME_MASTER_PAGE);
	}

	private boolean _isPageTemplateCollectionFile(String fileName) {
		return fileName.endsWith(
			CharPool.SLASH +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_PAGE_TEMPLATE_COLLECTION);
	}

	private boolean _isPageTemplateFile(String fileName) {
		return fileName.endsWith(
			CharPool.SLASH +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_PAGE_TEMPLATE);
	}

	private boolean _isRootFolder(List<String> entries, String fileName) {
		String[] pathParts = StringUtil.split(fileName, CharPool.SLASH);

		if (pathParts.length > 2) {
			String path = StringUtil.merge(
				ArrayUtil.subset(pathParts, 0, pathParts.length - 2),
				StringPool.SLASH);

			String displayPageTemplateCollectionPath =
				path + CharPool.SLASH +
					LayoutPageTemplateExportImportConstants.
						FILE_NAME_DISPLAY_PAGE_TEMPLATE_COLLECTION;

			if (entries.contains(displayPageTemplateCollectionPath)) {
				return false;
			}
		}

		return true;
	}

	private boolean _isUtilityPageTemplateFile(String fileName) {
		return fileName.endsWith(
			CharPool.SLASH +
				LayoutUtilityPageExportImportConstants.FILE_NAME_UTILITY_PAGE);
	}

	private boolean _isValidBasicLayoutPageTemplateCollection(
		String content, String fileName, long groupId) {

		try {
			String[] pathParts = StringUtil.split(fileName, CharPool.SLASH);

			if (pathParts.length <= 1) {
				return true;
			}

			PageTemplateCollectionValidator.validatePageTemplateCollection(
				content);

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollection(
						groupId, pathParts[pathParts.length - 2],
						LayoutPageTemplateEntryTypeConstants.BASIC);

			if (layoutPageTemplateCollection != null) {
				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Invalid basic layout page template collection for: " +
						fileName,
					exception);
			}
		}

		return true;
	}

	private boolean _isValidBasicLayoutPageTemplateEntry(
		String content, String fileName, long groupId,
		long layoutPageTemplateCollectionId) {

		try {
			PageTemplateValidator.validatePageTemplate(content);

			PageTemplate pageTemplate = _objectMapper.readValue(
				content, PageTemplate.class);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						groupId, layoutPageTemplateCollectionId,
						pageTemplate.getName(),
						LayoutPageTemplateEntryTypeConstants.BASIC);

			if (layoutPageTemplateEntry != null) {
				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Invalid basic layout page template entry for: " + fileName,
					exception);
			}
		}

		return true;
	}

	private boolean _isValidDisplayPageLayoutPageTemplateCollection(
		String content, String fileName, long groupId,
		long layoutPageTemplateCollectionId) {

		try {
			PageTemplateCollectionValidator.validatePageTemplateCollection(
				content);

			PageTemplateCollection pageTemplateCollection =
				_objectMapper.readValue(content, PageTemplateCollection.class);

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollection(
						groupId, pageTemplateCollection.getName(),
						layoutPageTemplateCollectionId,
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

			if (layoutPageTemplateCollection != null) {
				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Invalid display page layout page template collection " +
						"for: " + fileName,
					exception);
			}
		}

		return true;
	}

	private boolean _isValidDisplayPageLayoutPageTemplateEntry(
		String content, String fileName, long groupId,
		long layoutPageTemplateCollectionId) {

		try {
			DisplayPageTemplateValidator.validateDisplayPageTemplate(content);

			DisplayPageTemplate displayPageTemplate = _objectMapper.readValue(
				content, DisplayPageTemplate.class);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						groupId, layoutPageTemplateCollectionId,
						displayPageTemplate.getName(),
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

			if (layoutPageTemplateEntry != null) {
				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Invalid display page layout page template entry for: " +
						fileName,
					exception);
			}
		}

		return true;
	}

	private boolean _isValidMasterLayoutLayoutPageTemplateEntry(
		String content, String fileName, long groupId) {

		try {
			MasterPageValidator.validateMasterPage(content);

			MasterPage masterPage = _objectMapper.readValue(
				content, MasterPage.class);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						groupId,
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						masterPage.getName(),
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

			if (layoutPageTemplateEntry != null) {
				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Invalid master layout layout page template entry for: " +
						fileName,
					exception);
			}
		}

		return true;
	}

	private void _processBasicLayoutPageTemplateEntries(
			long groupId, long layoutPageTemplateCollectionId,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy,
			boolean preserveItemIds, long userId, ZipReader zipReader)
		throws Exception {

		Map<String, PageTemplateCollectionEntry>
			pageTemplateCollectionEntryMap = _getPageTemplateCollectionEntryMap(
				layoutsImporterResultEntries, zipReader);

		for (Map.Entry<String, PageTemplateCollectionEntry> entry :
				pageTemplateCollectionEntryMap.entrySet()) {

			PageTemplateCollectionEntry pageTemplateCollectionEntry =
				entry.getValue();

			Map<String, PageTemplateEntry> pageTemplatesEntries =
				pageTemplateCollectionEntry.getPageTemplatesEntries();

			if (MapUtil.isEmpty(pageTemplatesEntries)) {
				continue;
			}

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_getBasicLayoutPageTemplateCollection(
					groupId, layoutPageTemplateCollectionId,
					layoutsImportStrategy, pageTemplateCollectionEntry);

			_processPageTemplateEntries(
				groupId, layoutPageTemplateCollection,
				layoutsImporterResultEntries, layoutsImportStrategy,
				pageTemplatesEntries, preserveItemIds, userId);
		}
	}

	private LayoutPageTemplateCollection
		_processDisplayPageLayoutPageTemplateCollection(
			String fileName, long groupId, long layoutPageTemplateCollectionId,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy, ZipReader zipReader) {

		PageTemplateCollection pageTemplateCollection = null;

		try {
			String content = zipReader.getEntryAsString(fileName);

			PageTemplateCollectionValidator.validatePageTemplateCollection(
				content);

			pageTemplateCollection = _objectMapper.readValue(
				content, PageTemplateCollection.class);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Invalid page template collection for: " + fileName,
					exception);
			}

			layoutsImporterResultEntries.add(
				new LayoutsImporterResultEntry(
					fileName, LayoutsImporterResultEntry.TYPE_COLLECTION,
					LayoutsImporterResultEntry.Status.INVALID,
					new LayoutsImporterResultEntry.ErrorMessage(
						new String[] {fileName},
						"x-could-not-be-imported-because-its-page-template-" +
							"is-invalid")));

			return null;
		}

		try {
			PageDefinitionValidator.validatePageDefinition(
				_getPageDefinitionJSON(fileName, zipReader));

			return _getDisplayPageLayoutPageTemplateCollection(
				groupId, layoutsImportStrategy, layoutPageTemplateCollectionId,
				pageTemplateCollection);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Invalid page definition for: " +
						pageTemplateCollection.getName(),
					exception);
			}

			layoutsImporterResultEntries.add(
				new LayoutsImporterResultEntry(
					pageTemplateCollection.getName(),
					LayoutsImporterResultEntry.TYPE_COLLECTION,
					LayoutsImporterResultEntry.Status.INVALID,
					new LayoutsImporterResultEntry.ErrorMessage(
						new String[] {fileName},
						"x-could-not-be-imported-because-its-page-definition-" +
							"is-invalid")));
		}

		return null;
	}

	private Map<String, LayoutPageTemplateCollection>
		_processDisplayPageLayoutPageTemplateCollections(
			long groupId, long layoutPageTemplateCollectionId,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy, ZipReader zipReader) {

		Map<String, LayoutPageTemplateCollection>
			layoutPageTemplateCollectionsMap = new HashMap<>();

		List<String> entries = zipReader.getEntries();

		for (String entry : entries) {
			if (!_isDisplayPageTemplateCollectionFile(entry) ||
				!_isRootFolder(entries, entry)) {

				continue;
			}

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_processDisplayPageLayoutPageTemplateCollection(
					entry, groupId, layoutPageTemplateCollectionId,
					layoutsImporterResultEntries, layoutsImportStrategy,
					zipReader);

			if (layoutPageTemplateCollection == null) {
				continue;
			}

			String displayPageLayoutPageTemplateCollectionPath =
				_getDisplayPageLayoutPageTemplateCollectionPath(entry);

			layoutPageTemplateCollectionsMap.put(
				displayPageLayoutPageTemplateCollectionPath,
				layoutPageTemplateCollection);

			_processDisplayPageLayoutPageTemplateCollections(
				groupId,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutPageTemplateCollectionsMap, layoutsImporterResultEntries,
				layoutsImportStrategy,
				displayPageLayoutPageTemplateCollectionPath, zipReader);
		}

		return layoutPageTemplateCollectionsMap;
	}

	private void _processDisplayPageLayoutPageTemplateCollections(
		long groupId, long layoutPageTemplateCollectionId,
		Map<String, LayoutPageTemplateCollection>
			layoutPageTemplateCollectionsMap,
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
		LayoutsImportStrategy layoutsImportStrategy,
		String parentDisplayPageLayoutPageTemplateCollectionPath,
		ZipReader zipReader) {

		List<String> entries = zipReader.getEntries();

		for (String entry : zipReader.getEntries()) {
			if (!_isDisplayPageTemplateCollectionFile(entry) ||
				_isRootFolder(entries, entry)) {

				continue;
			}

			String[] pathParts = StringUtil.split(entry, CharPool.SLASH);

			if (pathParts.length <= 2) {
				continue;
			}

			String path = StringUtil.merge(
				ArrayUtil.subset(pathParts, 0, pathParts.length - 2),
				StringPool.SLASH);

			if (!parentDisplayPageLayoutPageTemplateCollectionPath.equals(
					path + StringPool.SLASH)) {

				continue;
			}

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_processDisplayPageLayoutPageTemplateCollection(
					entry, groupId, layoutPageTemplateCollectionId,
					layoutsImporterResultEntries, layoutsImportStrategy,
					zipReader);

			if (layoutPageTemplateCollection == null) {
				continue;
			}

			String displayPageLayoutPageTemplateCollectionPath =
				_getDisplayPageLayoutPageTemplateCollectionPath(entry);

			layoutPageTemplateCollectionsMap.put(
				displayPageLayoutPageTemplateCollectionPath,
				layoutPageTemplateCollection);

			_processDisplayPageLayoutPageTemplateCollections(
				groupId,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutPageTemplateCollectionsMap, layoutsImporterResultEntries,
				layoutsImportStrategy,
				displayPageLayoutPageTemplateCollectionPath, zipReader);
		}
	}

	private void _processDisplayPageLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId,
		Map<String, LayoutPageTemplateCollection>
			layoutPageTemplateCollectionsMap,
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
		LayoutsImportStrategy layoutsImportStrategy, boolean preserveItemIds,
		long userId, ZipReader zipReader) {

		List<String> entries = zipReader.getEntries();

		for (String entry : entries) {
			if (!_isDisplayPageTemplateFile(entry)) {
				continue;
			}

			String content = zipReader.getEntryAsString(entry);

			DisplayPageTemplate displayPageTemplate = null;

			try {
				DisplayPageTemplateValidator.validateDisplayPageTemplate(
					content);

				displayPageTemplate = _objectMapper.readValue(
					content, DisplayPageTemplate.class);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid display page template for: " + entry,
						exception);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						entry, LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {entry},
							"x-could-not-be-imported-because-its-display-" +
								"page-template-is-invalid")));

				continue;
			}

			String pageDefinitionJSON = null;

			try {
				pageDefinitionJSON = _getPageDefinitionJSON(entry, zipReader);

				PageDefinitionValidator.validatePageDefinition(
					pageDefinitionJSON);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid page definition for: " +
							displayPageTemplate.getName(),
						exception);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						displayPageTemplate.getName(),
						LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {displayPageTemplate.getName()},
							"x-could-not-be-imported-because-its-page-" +
								"definition-is-invalid")));
			}

			try {
				TransactionInvokerUtil.invoke(
					_transactionConfig,
					new DisplayPagesImporterCallable(
						groupId, displayPageTemplate,
						_getParentLayoutPageTemplateCollectionId(
							entries, entry, layoutPageTemplateCollectionId,
							layoutPageTemplateCollectionsMap),
						layoutsImporterResultEntries, layoutsImportStrategy,
						_objectMapper.readValue(
							pageDefinitionJSON, PageDefinition.class),
						preserveItemIds, _getThumbnail(entry, zipReader),
						userId, entry));
			}
			catch (Throwable throwable) {
				if (_log.isWarnEnabled()) {
					_log.warn(throwable, throwable);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						entry, LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {entry},
							"x-could-not-be-imported-because-of-invalid-" +
								"values-in-its-page-definition")));
			}
		}
	}

	private LayoutPageTemplateEntry _processLayoutPageTemplateEntry(
			long classNameId, long classTypeId, long groupId,
			long layoutPageTemplateCollectionId,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy, String name,
			PageDefinition pageDefinition, boolean preserveItemIds,
			int layoutPageTemplateEntryType, long userId, Thumbnail thumbnail,
			String zipPath)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				groupId, layoutPageTemplateCollectionId, name,
				layoutPageTemplateEntryType);

		try (SafeCloseable safeCloseable =
				CheckUnlockedLayoutThreadLocal.
					setCheckUnlockedLayoutWithSafeCloseable(false)) {

			if ((layoutPageTemplateEntry != null) &&
				Objects.equals(
					LayoutsImportStrategy.DO_NOT_IMPORT,
					layoutsImportStrategy)) {

				return null;
			}

			boolean added = false;

			if (layoutPageTemplateEntry == null) {
				layoutPageTemplateEntry = _addLayoutPageTemplateEntry(
					groupId, layoutPageTemplateCollectionId, classNameId,
					classTypeId, name, layoutPageTemplateEntryType);
				added = true;
			}
			else if (Objects.equals(
						LayoutsImportStrategy.KEEP_BOTH,
						layoutsImportStrategy)) {

				layoutPageTemplateEntry = _addLayoutPageTemplateEntry(
					groupId, layoutPageTemplateCollectionId, classNameId,
					classTypeId,
					_layoutPageTemplateEntryLocalService.
						getUniqueLayoutPageTemplateEntryName(
							groupId, layoutPageTemplateCollectionId, name,
							layoutPageTemplateEntryType),
					layoutPageTemplateEntryType);
				added = true;
			}
			else if (Objects.equals(
						LayoutsImportStrategy.OVERWRITE,
						layoutsImportStrategy)) {

				_deleteExistingPortletPreferences(
					layoutPageTemplateEntry.getPlid());

				layoutPageTemplateEntry =
					_layoutPageTemplateEntryService.
						updateLayoutPageTemplateEntry(
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId(),
							name);

				added = true;
			}

			if (added) {
				Set<String> warningMessages = new HashSet<>();

				_processPageDefinition(
					layoutPageTemplateEntry.getPlid(), pageDefinition,
					preserveItemIds, userId, warningMessages);

				long previewFileEntryId = _getPreviewFileEntryId(
					LayoutPageTemplateEntry.class.getName(),
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					groupId, thumbnail, userId);

				layoutPageTemplateEntry =
					_layoutPageTemplateEntryService.
						updateLayoutPageTemplateEntry(
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId(),
							previewFileEntryId);

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						name, LayoutsImporterResultEntry.Status.IMPORTED,
						warningMessages.toArray(new String[0])));
			}
			else {
				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						name, LayoutsImporterResultEntry.Status.IGNORED,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {
								zipPath,
								_toTypeName(layoutPageTemplateEntryType)
							},
							_MESSAGE_KEY_IGNORED)));
			}
		}
		catch (DropzoneLayoutStructureItemException
					dropzoneLayoutStructureItemException) {

			if (_log.isWarnEnabled()) {
				_log.warn(dropzoneLayoutStructureItemException);
			}

			throw new PortalException();
		}
		catch (NoSuchClassTypeException noSuchClassTypeException) {
			if (_log.isWarnEnabled()) {
				_log.warn(noSuchClassTypeException);
			}

			layoutsImporterResultEntries.add(
				new LayoutsImporterResultEntry(
					name, LayoutsImporterResultEntry.Status.INVALID,
					new LayoutsImporterResultEntry.ErrorMessage(
						new String[] {zipPath}, _MESSAGE_KEY_TYPE_INVALID)));

			return null;
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			layoutsImporterResultEntries.add(
				new LayoutsImporterResultEntry(
					name, LayoutsImporterResultEntry.Status.INVALID,
					new LayoutsImporterResultEntry.ErrorMessage(
						new String[] {
							zipPath, _toTypeName(layoutPageTemplateEntryType)
						},
						_MESSAGE_KEY_NAME_INVALID)));

			return null;
		}

		return layoutPageTemplateEntry;
	}

	private void _processLayoutUtilityPageEntries(
		long groupId,
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
		LayoutsImportStrategy layoutsImportStrategy, boolean preserveItemIds,
		long userId, ZipReader zipReader) {

		for (String entry : zipReader.getEntries()) {
			if (!_isUtilityPageTemplateFile(entry)) {
				continue;
			}

			String content = zipReader.getEntryAsString(entry);

			UtilityPageTemplate utilityPageTemplate = null;

			try {
				UtilityPageTemplateValidator.validateUtilityPageTemplate(
					content);

				utilityPageTemplate = _objectMapper.readValue(
					content, UtilityPageTemplate.class);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid utility page template for: " + entry,
						exception);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						entry, LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {entry},
							"x-could-not-be-imported-because-its-utility-" +
								"page-is-invalid")));

				continue;
			}

			if (!FeatureFlagManagerUtil.isEnabled("LPD-6378") &&
				((utilityPageTemplate.getType() ==
					UtilityPageTemplate.Type.CREATE_ACCOUNT) ||
				 (utilityPageTemplate.getType() ==
					 UtilityPageTemplate.Type.FORGOT_PASSWORD) ||
				 (utilityPageTemplate.getType() ==
					 UtilityPageTemplate.Type.LOGIN))) {

				continue;
			}

			String pageDefinitionJSON = null;

			try {
				pageDefinitionJSON = _getPageDefinitionJSON(entry, zipReader);

				PageDefinitionValidator.validatePageDefinition(
					pageDefinitionJSON);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid page definition for: " +
							utilityPageTemplate.getName(),
						exception);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						utilityPageTemplate.getName(),
						LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {utilityPageTemplate.getName()},
							"x-could-not-be-imported-because-its-page-" +
								"definition-is-invalid")));

				continue;
			}

			try {
				TransactionInvokerUtil.invoke(
					_transactionConfig,
					new UtilityPageImporterCallable(
						groupId, layoutsImporterResultEntries,
						layoutsImportStrategy,
						_objectMapper.readValue(
							pageDefinitionJSON, PageDefinition.class),
						preserveItemIds, _getThumbnail(entry, zipReader),
						userId, utilityPageTemplate, entry));
			}
			catch (Throwable throwable) {
				if (_log.isWarnEnabled()) {
					_log.warn(throwable, throwable);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						entry, LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {entry},
							"x-could-not-be-imported-because-its-page-" +
								"definition-is-invalid")));
			}
		}
	}

	private void _processLayoutUtilityPageTemplateEntry(
			String externalReferenceCode, long groupId,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy,
			LayoutUtilityPageEntry layoutUtilityPageEntry, String name,
			PageDefinition pageDefinition, boolean preserveItemIds, String type,
			long userId, Thumbnail thumbnail, String zipPath)
		throws Exception {

		try {
			boolean added = false;

			if (layoutUtilityPageEntry == null) {
				layoutUtilityPageEntry =
					_layoutUtilityPageEntryService.addLayoutUtilityPageEntry(
						externalReferenceCode, groupId, 0, 0, false, name, type,
						0, ServiceContextThreadLocal.getServiceContext());

				added = true;
			}
			else if (Objects.equals(
						LayoutsImportStrategy.OVERWRITE,
						layoutsImportStrategy)) {

				_deleteExistingPortletPreferences(
					layoutUtilityPageEntry.getPlid());

				layoutUtilityPageEntry =
					_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
						layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
						name);

				added = true;
			}

			if (added) {
				Set<String> warningMessages = new HashSet<>();

				_processPageDefinition(
					layoutUtilityPageEntry.getPlid(), pageDefinition,
					preserveItemIds, userId, warningMessages);

				long previewFileEntryId = _getPreviewFileEntryId(
					LayoutUtilityPageEntry.class.getName(),
					layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
					groupId, thumbnail, userId);

				if (previewFileEntryId > 0) {
					_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
						layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
						previewFileEntryId);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						name, LayoutsImporterResultEntry.Status.IMPORTED,
						warningMessages.toArray(new String[0])));
			}
			else {
				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						name, LayoutsImporterResultEntry.Status.IGNORED,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {zipPath, "utility page"},
							_MESSAGE_KEY_IGNORED)));
			}
		}
		catch (DropzoneLayoutStructureItemException
					dropzoneLayoutStructureItemException) {

			if (_log.isWarnEnabled()) {
				_log.warn(dropzoneLayoutStructureItemException);
			}

			throw new PortalException();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			layoutsImporterResultEntries.add(
				new LayoutsImporterResultEntry(
					name, LayoutsImporterResultEntry.Status.INVALID,
					new LayoutsImporterResultEntry.ErrorMessage(
						new String[] {zipPath, "utility page"},
						_MESSAGE_KEY_NAME_INVALID)));
		}
	}

	private void _processMasterLayoutLayoutPageTemplateEntries(
		long groupId,
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
		LayoutsImportStrategy layoutsImportStrategy, boolean preserveItemIds,
		long userId, ZipReader zipReader) {

		for (String entry : zipReader.getEntries()) {
			if (!_isMasterPageFile(entry)) {
				continue;
			}

			String content = zipReader.getEntryAsString(entry);

			MasterPage masterPage = null;

			try {
				MasterPageValidator.validateMasterPage(content);

				masterPage = _objectMapper.readValue(content, MasterPage.class);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Invalid master page for: " + entry, exception);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						entry, LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {entry},
							"x-could-not-be-imported-because-its-master-page-" +
								"is-invalid")));

				continue;
			}

			String pageDefinitionJSON = null;

			try {
				pageDefinitionJSON = _getPageDefinitionJSON(entry, zipReader);

				PageDefinitionValidator.validatePageDefinition(
					pageDefinitionJSON);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid page definition for: " + masterPage.getName(),
						exception);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						masterPage.getName(),
						LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {masterPage.getName()},
							"x-could-not-be-imported-because-its-page-" +
								"definition-is-invalid")));

				continue;
			}

			try {
				TransactionInvokerUtil.invoke(
					_transactionConfig,
					new MasterLayoutTemplatesImporterCallable(
						groupId, masterPage.getName(),
						layoutsImporterResultEntries, layoutsImportStrategy,
						_objectMapper.readValue(
							pageDefinitionJSON, PageDefinition.class),
						preserveItemIds, _getThumbnail(entry, zipReader),
						userId, entry));
			}
			catch (Throwable throwable) {
				if (_log.isWarnEnabled()) {
					_log.warn(throwable, throwable);
				}

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						entry, LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {entry},
							"x-could-not-be-imported-because-of-invalid-" +
								"values-in-its-page-definition")));
			}
		}
	}

	private void _processPageDefinition(
			long plid, PageDefinition pageDefinition, boolean preserveItemIds,
			long userId, Set<String> warningMessages)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(plid);

		LayoutStructure layoutStructure = new LayoutStructure();

		if (pageDefinition != null) {
			PageElement pageElement = pageDefinition.getPageElement();

			LayoutStructureItem rootLayoutStructureItem =
				layoutStructure.addRootLayoutStructureItem(pageElement.getId());

			if ((pageElement.getType() == PageElement.Type.ROOT) &&
				(pageElement.getPageElements() != null)) {

				double pageDefinitionVersion = GetterUtil.getDouble(
					pageDefinition.getVersion(), 1);
				int position = 0;

				for (PageElement childPageElement :
						pageElement.getPageElements()) {

					if (_processPageElement(
							new ArrayList<>(), layout, layoutStructure,
							pageDefinitionVersion, childPageElement,
							rootLayoutStructureItem.getItemId(), position,
							preserveItemIds,
							_segmentsExperienceLocalService.
								fetchDefaultSegmentsExperienceId(
									layout.getPlid()),
							warningMessages)) {

						position++;
					}
				}
			}

			if (pageDefinition.getPageRules() != null) {
				for (PageRule pageRule : pageDefinition.getPageRules()) {
					LayoutStructureRuleImporter.addLayoutStructureRule(
						layoutStructure, pageRule);
				}
			}

			Settings settings = pageDefinition.getSettings();

			layout = _layoutLocalService.fetchLayout(layout.getPlid());

			layout = _updateLayoutSettings(userId, layout, settings);
		}
		else {
			layoutStructure.addRootLayoutStructureItem();
		}

		_updateLayoutPageTemplateStructure(layout, layoutStructure);

		_updateLayouts(plid, userId);
	}

	private boolean _processPageElement(
			List<FragmentEntryLink> fragmentEntryLinks, Layout layout,
			LayoutStructure layoutStructure, double pageDefinitionVersion,
			PageElement pageElement, String parentItemId, int position,
			boolean preserveItemIds, long segmentsExperienceId,
			Set<String> warningMessages)
		throws Exception {

		LayoutStructureItemImporter layoutStructureItemImporter =
			_layoutStructureItemImporters.get(pageElement.getType());

		LayoutStructureItem layoutStructureItem = null;

		if (layoutStructureItemImporter != null) {
			layoutStructureItem =
				layoutStructureItemImporter.addLayoutStructureItem(
					layoutStructure,
					new LayoutStructureItemImporterContext(
						layout, pageDefinitionVersion, parentItemId, position,
						preserveItemIds, segmentsExperienceId,
						_groupLocalService, _infoItemServiceRegistry,
						_infoSearchClassMapperRegistry, _layoutLocalService,
						_layoutPageTemplateEntryLocalService),
					pageElement, warningMessages);
		}
		else if (pageElement.getType() == PageElement.Type.ROOT) {
			layoutStructureItem = layoutStructure.getMainLayoutStructureItem();
		}
		else {
			return false;
		}

		if (layoutStructureItem == null) {
			return false;
		}

		if (layoutStructureItem instanceof FragmentStyledLayoutStructureItem) {
			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			fragmentEntryLinks.add(
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentStyledLayoutStructureItem.
						getFragmentEntryLinkId()));
		}

		if (pageElement.getPageElements() == null) {
			return true;
		}

		int childPosition = 0;

		for (PageElement childPageElement : pageElement.getPageElements()) {
			if (_processPageElement(
					fragmentEntryLinks, layout, layoutStructure,
					pageDefinitionVersion, childPageElement,
					layoutStructureItem.getItemId(), childPosition,
					preserveItemIds, segmentsExperienceId, warningMessages)) {

				childPosition++;
			}
		}

		return true;
	}

	private void _processPageTemplateEntries(
			long groupId,
			LayoutPageTemplateCollection layoutPageTemplateCollection,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy,
			Map<String, PageTemplateEntry> pageTemplateEntryMap,
			boolean preserveItemIds, long userId)
		throws Exception {

		for (Map.Entry<String, PageTemplateEntry> entry :
				pageTemplateEntryMap.entrySet()) {

			PageTemplateEntry pageTemplateEntry = entry.getValue();

			Callable<Void> callable = new BasicLayoutsImporterCallable(
				groupId,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutsImporterResultEntries, layoutsImportStrategy, userId,
				pageTemplateEntry, preserveItemIds);

			try {
				TransactionInvokerUtil.invoke(_transactionConfig, callable);
			}
			catch (Throwable throwable) {
				if (_log.isWarnEnabled()) {
					_log.warn(throwable, throwable);
				}

				PageTemplate pageTemplate = pageTemplateEntry.getPageTemplate();

				layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						pageTemplate.getName(),
						LayoutsImporterResultEntry.Status.INVALID,
						new LayoutsImporterResultEntry.ErrorMessage(
							new String[] {pageTemplate.getName()},
							"x-could-not-be-imported-because-of-invalid-" +
								"values-in-its-page-definition")));
			}
		}
	}

	private String _toTypeName(int layoutPageTemplateEntryType) {
		if (layoutPageTemplateEntryType ==
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

			return "display page template";
		}

		if (layoutPageTemplateEntryType ==
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

			return "master page";
		}

		if (layoutPageTemplateEntryType ==
				LayoutPageTemplateEntryTypeConstants.BASIC) {

			return "page template";
		}

		return null;
	}

	private void _updateLayoutPageTemplateStructure(
			Layout layout, LayoutStructure layoutStructure)
		throws Exception {

		JSONObject jsonObject = layoutStructure.toJSONObject();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		if (layoutPageTemplateStructure != null) {
			_layoutPageTemplateStructureLocalService.
				deleteLayoutPageTemplateStructure(layoutPageTemplateStructure);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		_layoutPageTemplateStructureLocalService.addLayoutPageTemplateStructure(
			serviceContext.getUserId(), layout.getGroupId(), layout.getPlid(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()),
			jsonObject.toString(), serviceContext);

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			for (FragmentEntryLink fragmentEntryLink :
					_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
						layout.getGroupId(), layout.getPlid())) {

				for (FragmentEntryLinkListener fragmentEntryLinkListener :
						_fragmentEntryLinkListenerRegistry.
							getFragmentEntryLinkListeners()) {

					fragmentEntryLinkListener.onAddFragmentEntryLink(
						fragmentEntryLink);
				}
			}
		}
	}

	private void _updateLayouts(long plid, long userId) throws Exception {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout = _layoutLocalService.copyLayoutContent(
			layout, draftLayout);

		_layoutLocalService.updateStatus(
			userId, draftLayout.getPlid(), WorkflowConstants.STATUS_APPROVED,
			ServiceContextThreadLocal.getServiceContext());

		_layoutLocalService.updateStatus(
			userId, plid, WorkflowConstants.STATUS_APPROVED,
			ServiceContextThreadLocal.getServiceContext());
	}

	private Layout _updateLayoutSettings(
		long userId, Layout layout, Settings settings) {

		if (settings == null) {
			layout.setThemeId(null);
			layout.setColorSchemeId(null);

			return _layoutLocalService.updateLayout(layout);
		}

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		Map<String, String> themeSettings =
			(Map<String, String>)settings.getThemeSettings();

		Set<Map.Entry<String, String>> entrySet = unicodeProperties.entrySet();

		entrySet.removeIf(
			entry -> {
				String key = entry.getKey();

				return key.startsWith("lfr-theme:");
			});

		if (themeSettings != null) {
			for (Map.Entry<String, String> entry : themeSettings.entrySet()) {
				unicodeProperties.put(entry.getKey(), entry.getValue());
			}

			layout.setTypeSettingsProperties(unicodeProperties);
		}

		if (Validator.isNotNull(settings.getThemeName())) {
			String themeId = _getThemeId(
				layout.getCompanyId(), settings.getThemeName());

			layout.setThemeId(themeId);
		}

		if (Validator.isNotNull(settings.getColorSchemeName())) {
			layout.setColorSchemeId(settings.getColorSchemeName());
		}

		StyleBook styleBook = settings.getStyleBook();

		if (styleBook != null) {
			StyleBookEntry styleBookEntry =
				_styleBookEntryLocalService.fetchStyleBookEntry(
					layout.getGroupId(), styleBook.getKey());

			if (styleBookEntry != null) {
				layout.setStyleBookEntryId(
					styleBookEntry.getStyleBookEntryId());
			}
		}

		if (Validator.isNotNull(settings.getCss())) {
			layout.setCss(settings.getCss());
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Map<String, Serializable> favIconMap =
			(Map<String, Serializable>)settings.getFavIcon();

		if (MapUtil.isNotEmpty(favIconMap)) {
			if (Objects.equals(favIconMap.get("contentType"), "Document")) {
				layout.setFaviconFileEntryId(
					_getFileEntryId(GetterUtil.getLong(favIconMap.get("id"))));
			}
			else if (favIconMap.containsKey("externalReferenceCode")) {
				_addClientExtensionEntryRel(
					String.valueOf(favIconMap.get("externalReferenceCode")),
					layout, serviceContext,
					ClientExtensionEntryConstants.TYPE_THEME_FAVICON, null,
					userId);
			}
		}

		MasterPage masterPage = settings.getMasterPage();

		if (masterPage != null) {
			LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						layout.getGroupId(), masterPage.getKey());

			if (masterLayoutPageTemplateEntry != null) {
				layout.setMasterLayoutPlid(
					masterLayoutPageTemplateEntry.getPlid());
			}
		}

		ArrayUtil.isNotEmptyForEach(
			settings.getGlobalCSSClientExtensions(),
			globalCSSClientExtension -> _addClientExtensionEntryRel(
				globalCSSClientExtension.getExternalReferenceCode(), layout,
				serviceContext, ClientExtensionEntryConstants.TYPE_GLOBAL_CSS,
				globalCSSClientExtension.getClientExtensionConfig(), userId));
		ArrayUtil.isNotEmptyForEach(
			settings.getGlobalJSClientExtensions(),
			globalJSClientExtension -> _addClientExtensionEntryRel(
				globalJSClientExtension.getExternalReferenceCode(), layout,
				serviceContext, ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
				globalJSClientExtension.getClientExtensionConfig(), userId));

		ClientExtension themeCSSClientExtension =
			settings.getThemeCSSClientExtension();

		if (themeCSSClientExtension != null) {
			_addClientExtensionEntryRel(
				themeCSSClientExtension.getExternalReferenceCode(), layout,
				serviceContext, ClientExtensionEntryConstants.TYPE_THEME_CSS,
				themeCSSClientExtension.getClientExtensionConfig(), userId);
		}

		ClientExtension themeSpritemapClientExtension =
			settings.getThemeSpritemapClientExtension();

		if (themeSpritemapClientExtension != null) {
			_addClientExtensionEntryRel(
				themeSpritemapClientExtension.getExternalReferenceCode(),
				layout, serviceContext,
				ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP,
				themeSpritemapClientExtension.getClientExtensionConfig(),
				userId);
		}

		return _layoutLocalService.updateLayout(layout);
	}

	private static final String _MESSAGE_KEY_IGNORED =
		"x-was-ignored-because-a-x-with-the-same-key-already-exists";

	private static final String _MESSAGE_KEY_NAME_INVALID =
		"x-could-not-be-imported-because-a-x-with-the-same-name-already-exists";

	private static final String _MESSAGE_KEY_TYPE_INVALID =
		"x-could-not-be-imported-because-its-content-type-or-subtype-is-" +
			"missing";

	private static final String _PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT =
		"imported";

	private static final String _PAGE_TEMPLATE_ENTRY_KEY_DEFAULT = "imported";

	private static final String _THUMBNAIL_FILE_NAME = "thumbnail";

	private static final String[] _THUMBNAIL_VALID_EXTENSIONS = {
		".bmp", ".gif", ".jpeg", ".jpg", ".png", ".svg", ".tiff"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutsImporterImpl.class);

	private static final ObjectMapper _objectMapper = new ObjectMapper();
	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private CETManager _cetManager;

	@Reference
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private FragmentEntryValidator _fragmentEntryValidator;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	private final EnumMap<PageElement.Type, LayoutStructureItemImporter>
		_layoutStructureItemImporters = new EnumMap<>(PageElement.Type.class);

	@Reference
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Reference
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletConfigurationImporterHelper
		_portletConfigurationImporterHelper;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPermissionsImporter _portletPermissionsImporter;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private ThemeLocalService _themeLocalService;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

	private static class Thumbnail {

		public byte[] getBytes() {
			return _bytes;
		}

		public String getExtension() {
			return _extension;
		}

		private Thumbnail(byte[] bytes, String extension) {
			_bytes = bytes;
			_extension = extension;
		}

		private final byte[] _bytes;
		private final String _extension;

	}

	private class BasicLayoutsImporterCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			PageTemplate pageTemplate = _pageTemplateEntry.getPageTemplate();

			_processLayoutPageTemplateEntry(
				0, 0, _groupId, _layoutPageTemplateCollectionId,
				_layoutsImporterResultEntries, _layoutsImportStrategy,
				pageTemplate.getName(), _pageTemplateEntry.getPageDefinition(),
				_preserveItemIds, LayoutPageTemplateEntryTypeConstants.BASIC,
				_userId, _pageTemplateEntry.getThumbnail(),
				_pageTemplateEntry.getZipPath());

			return null;
		}

		private BasicLayoutsImporterCallable(
			long groupId, long layoutPageTemplateCollectionId,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy, long userId,
			PageTemplateEntry pageTemplateEntry, boolean preserveItemIds) {

			_groupId = groupId;
			_layoutPageTemplateCollectionId = layoutPageTemplateCollectionId;
			_layoutsImporterResultEntries = layoutsImporterResultEntries;
			_layoutsImportStrategy = layoutsImportStrategy;
			_userId = userId;
			_pageTemplateEntry = pageTemplateEntry;
			_preserveItemIds = preserveItemIds;
		}

		private final long _groupId;
		private final long _layoutPageTemplateCollectionId;
		private final List<LayoutsImporterResultEntry>
			_layoutsImporterResultEntries;
		private final LayoutsImportStrategy _layoutsImportStrategy;
		private final PageTemplateEntry _pageTemplateEntry;
		private final boolean _preserveItemIds;
		private final long _userId;

	}

	private class DisplayPagesImporterCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			ContentType contentType = _displayPageTemplate.getContentType();

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_processLayoutPageTemplateEntry(
					_portal.getClassNameId(contentType.getClassName()),
					_getClassTypeId(
						contentType.getClassName(), _displayPageTemplate),
					_groupId, _layoutPageTemplateCollectionId,
					_layoutsImporterResultEntries, _layoutsImportStrategy,
					_displayPageTemplate.getName(), _pageDefinition,
					_preserveItemIds,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, _userId,
					_thumbnail, _zipPath);

			boolean defaultTemplate = GetterUtil.getBoolean(
				_displayPageTemplate.getDefaultTemplate());

			if ((layoutPageTemplateEntry != null) && defaultTemplate) {
				_layoutPageTemplateEntryLocalService.
					updateLayoutPageTemplateEntry(
						layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
						true);
			}

			return null;
		}

		private DisplayPagesImporterCallable(
			long groupId, DisplayPageTemplate displayPageTemplate,
			long layoutPageTemplateCollectionId,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy,
			PageDefinition pageDefinition, boolean preserveItemIds,
			Thumbnail thumbnail, long userId, String zipPath) {

			_groupId = groupId;
			_displayPageTemplate = displayPageTemplate;
			_layoutPageTemplateCollectionId = layoutPageTemplateCollectionId;
			_layoutsImporterResultEntries = layoutsImporterResultEntries;
			_layoutsImportStrategy = layoutsImportStrategy;
			_pageDefinition = pageDefinition;
			_preserveItemIds = preserveItemIds;
			_thumbnail = thumbnail;
			_userId = userId;
			_zipPath = zipPath;
		}

		private long _getClassTypeId(
			String className, DisplayPageTemplate displayPageTemplate) {

			InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFormVariationsProvider.class, className);

			if (infoItemFormVariationsProvider == null) {
				return 0;
			}

			ContentSubtype contentSubtype =
				displayPageTemplate.getContentSubtype();

			if (contentSubtype == null) {
				return -1;
			}

			String subtypeKey = contentSubtype.getSubtypeKey();

			if (Validator.isNull(subtypeKey)) {
				return GetterUtil.getLong(contentSubtype.getSubtypeId(), -1);
			}

			InfoItemFormVariation infoItemFormVariation =
				infoItemFormVariationsProvider.getInfoItemFormVariation(
					_groupId, subtypeKey);

			if (infoItemFormVariation == null) {
				infoItemFormVariation =
					infoItemFormVariationsProvider.
						getInfoItemFormVariationByExternalReferenceCode(
							subtypeKey, _groupId);
			}

			if (infoItemFormVariation != null) {
				return GetterUtil.getLong(infoItemFormVariation.getKey());
			}

			return -1;
		}

		private final DisplayPageTemplate _displayPageTemplate;
		private final long _groupId;
		private final long _layoutPageTemplateCollectionId;
		private final List<LayoutsImporterResultEntry>
			_layoutsImporterResultEntries;
		private final LayoutsImportStrategy _layoutsImportStrategy;
		private final PageDefinition _pageDefinition;
		private final boolean _preserveItemIds;
		private final Thumbnail _thumbnail;
		private final long _userId;
		private final String _zipPath;

	}

	private class MasterLayoutTemplatesImporterCallable
		implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			_processLayoutPageTemplateEntry(
				0, 0, _groupId,
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				_layoutsImporterResultEntries, _layoutsImportStrategy, _name,
				_pageDefinition, _preserveItemIds,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, _userId,
				_thumbnail, _zipPath);

			return null;
		}

		private MasterLayoutTemplatesImporterCallable(
			long groupId, String name,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy,
			PageDefinition pageDefinition, boolean preserveItemIds,
			Thumbnail thumbnail, long userId, String zipPath) {

			_groupId = groupId;
			_name = name;
			_layoutsImporterResultEntries = layoutsImporterResultEntries;
			_layoutsImportStrategy = layoutsImportStrategy;
			_pageDefinition = pageDefinition;
			_preserveItemIds = preserveItemIds;
			_thumbnail = thumbnail;
			_userId = userId;
			_zipPath = zipPath;
		}

		private final long _groupId;
		private final List<LayoutsImporterResultEntry>
			_layoutsImporterResultEntries;
		private final LayoutsImportStrategy _layoutsImportStrategy;
		private final String _name;
		private final PageDefinition _pageDefinition;
		private final boolean _preserveItemIds;
		private final Thumbnail _thumbnail;
		private final long _userId;
		private final String _zipPath;

	}

	private class PageTemplateCollectionEntry {

		public PageTemplateCollectionEntry(
			String key, PageTemplateCollection pageTemplateCollection) {

			_key = key;
			_pageTemplateCollection = pageTemplateCollection;
		}

		public void addPageTemplateEntry(
			String key, PageTemplateEntry pageTemplateEntry) {

			_pageTemplateEntries.put(key, pageTemplateEntry);
		}

		public String getKey() {
			return _key;
		}

		public PageTemplateCollection getPageTemplateCollection() {
			return _pageTemplateCollection;
		}

		public Map<String, PageTemplateEntry> getPageTemplatesEntries() {
			return _pageTemplateEntries;
		}

		private final String _key;
		private final PageTemplateCollection _pageTemplateCollection;
		private final Map<String, PageTemplateEntry> _pageTemplateEntries =
			new HashMap<>();

	}

	private class PageTemplateEntry {

		public PageTemplateEntry(
			PageTemplate pageTemplate, PageDefinition pageDefinition,
			Thumbnail thumbnail, String zipPath) {

			_pageTemplate = pageTemplate;
			_pageDefinition = pageDefinition;
			_thumbnail = thumbnail;
			_zipPath = zipPath;
		}

		public PageDefinition getPageDefinition() {
			return _pageDefinition;
		}

		public PageTemplate getPageTemplate() {
			return _pageTemplate;
		}

		public Thumbnail getThumbnail() {
			return _thumbnail;
		}

		public String getZipPath() {
			return _zipPath;
		}

		private final PageDefinition _pageDefinition;
		private final PageTemplate _pageTemplate;
		private final Thumbnail _thumbnail;
		private final String _zipPath;

	}

	private class UtilityPageImporterCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			LayoutUtilityPageEntry layoutUtilityPageEntry =
				_layoutUtilityPageEntryLocalService.
					fetchLayoutUtilityPageEntryByExternalReferenceCode(
						_utilityPageTemplate.getExternalReferenceCode(),
						_groupId);

			_processLayoutUtilityPageTemplateEntry(
				_utilityPageTemplate.getExternalReferenceCode(), _groupId,
				_layoutsImporterResultEntries, _layoutsImportStrategy,
				layoutUtilityPageEntry, _utilityPageTemplate.getName(),
				_pageDefinition, _preserveItemIds,
				LayoutUtilityPageEntryTypeConverter.convertToInternalValue(
					_utilityPageTemplate.getTypeAsString()),
				_userId, _thumbnail, _zipPath);

			return null;
		}

		private UtilityPageImporterCallable(
			long groupId,
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries,
			LayoutsImportStrategy layoutsImportStrategy,
			PageDefinition pageDefinition, boolean preserveItemIds,
			Thumbnail thumbnail, long userId,
			UtilityPageTemplate utilityPageTemplate, String zipPath) {

			_groupId = groupId;
			_layoutsImporterResultEntries = layoutsImporterResultEntries;
			_layoutsImportStrategy = layoutsImportStrategy;
			_pageDefinition = pageDefinition;
			_preserveItemIds = preserveItemIds;
			_thumbnail = thumbnail;
			_userId = userId;
			_utilityPageTemplate = utilityPageTemplate;
			_zipPath = zipPath;
		}

		private final long _groupId;
		private final List<LayoutsImporterResultEntry>
			_layoutsImporterResultEntries;
		private final LayoutsImportStrategy _layoutsImportStrategy;
		private final PageDefinition _pageDefinition;
		private final boolean _preserveItemIds;
		private final Thumbnail _thumbnail;
		private final long _userId;
		private final UtilityPageTemplate _utilityPageTemplate;
		private final String _zipPath;

	}

}