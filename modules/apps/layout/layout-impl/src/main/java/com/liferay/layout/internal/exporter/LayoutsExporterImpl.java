/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.exporter;

import com.liferay.headless.delivery.dto.v1_0.ContentSubtype;
import com.liferay.headless.delivery.dto.v1_0.ContentType;
import com.liferay.headless.delivery.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.delivery.dto.v1_0.MasterPage;
import com.liferay.headless.delivery.dto.v1_0.PageDefinition;
import com.liferay.headless.delivery.dto.v1_0.PageTemplate;
import com.liferay.headless.delivery.dto.v1_0.PageTemplateCollection;
import com.liferay.headless.delivery.dto.v1_0.UtilityPageTemplate;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.exporter.LayoutsExporter;
import com.liferay.layout.internal.headless.delivery.dto.v1_0.util.MasterPageUtil;
import com.liferay.layout.internal.headless.delivery.dto.v1_0.util.PageTemplateCollectionUtil;
import com.liferay.layout.internal.headless.delivery.dto.v1_0.util.PageTemplateUtil;
import com.liferay.layout.internal.headless.delivery.dto.v1_0.util.UtilityPageTemplateUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateExportImportConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.io.File;
import java.io.IOException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = LayoutsExporter.class)
public class LayoutsExporterImpl implements LayoutsExporter {

	@Override
	public File exportLayoutPageTemplateCollections(
			long[] layoutPageTemplateCollectionIds)
		throws Exception {

		ZipWriter zipWriter = new ZipWriter();

		_exportLayoutPageTemplateCollections(
			TransformUtil.transformToList(
				layoutPageTemplateCollectionIds,
				layoutPageTemplateCollectionId ->
					_layoutPageTemplateCollectionService.
						fetchLayoutPageTemplateCollection(
							layoutPageTemplateCollectionId)),
			_getPageDefinitionDTOConverter(), StringPool.BLANK, zipWriter);

		return zipWriter.getFile();
	}

	@Override
	public File exportLayoutPageTemplateEntries(long groupId) throws Exception {
		DTOConverter<LayoutStructure, PageDefinition>
			pageDefinitionDTOConverter = _getPageDefinitionDTOConverter();
		ZipWriter zipWriter = new ZipWriter();

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				groupId, WorkflowConstants.STATUS_APPROVED);

		for (LayoutPageTemplateEntry layoutPageTemplateEntry :
				layoutPageTemplateEntries) {

			if (layoutPageTemplateEntry.getType() ==
					LayoutPageTemplateEntryTypeConstants.BASIC) {

				_populatePageTemplatesZipWriter(
					layoutPageTemplateEntry, pageDefinitionDTOConverter,
					zipWriter);
			}
			else if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

				_populateDisplayPagesZipWriter(
					layoutPageTemplateEntry, pageDefinitionDTOConverter,
					StringPool.BLANK, zipWriter);
			}
			else if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

				_populateMasterLayoutsZipWriter(
					layoutPageTemplateEntry, pageDefinitionDTOConverter,
					zipWriter);
			}
		}

		return zipWriter.getFile();
	}

	@Override
	public File exportLayoutPageTemplateEntries(
			long[] layoutPageTemplateEntryIds, int type)
		throws Exception {

		if (LayoutPageTemplateEntryTypeConstants.BASIC == type) {
			return _exportLayoutPageTemplateEntries(
				layoutPageTemplateEntryIds, type,
				this::_populatePageTemplatesZipWriter);
		}

		if (LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE == type) {
			return _exportLayoutPageTemplateEntries(
				layoutPageTemplateEntryIds, type,
				(layoutPageTemplateEntry, pageDefinitionDTOConverter,
				 zipWriter) -> _populateDisplayPagesZipWriter(
					layoutPageTemplateEntry, pageDefinitionDTOConverter,
					StringPool.BLANK, zipWriter));
		}

		if (LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT != type) {
			return null;
		}

		return _exportLayoutPageTemplateEntries(
			layoutPageTemplateEntryIds, type,
			this::_populateMasterLayoutsZipWriter);
	}

	@Override
	public File exportLayoutPageTemplateEntriesAndLayoutPageTemplateCollections(
			long[] layoutPageTemplateEntryIds,
			long[] layoutPageTemplateCollectionIds)
		throws Exception {

		DTOConverter<LayoutStructure, PageDefinition>
			pageDefinitionDTOConverter = _getPageDefinitionDTOConverter();
		ZipWriter zipWriter = new ZipWriter();

		for (long layoutPageTemplateEntryId : layoutPageTemplateEntryIds) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.getLayoutPageTemplateEntry(
					layoutPageTemplateEntryId);

			if (layoutPageTemplateEntry.isDraft() ||
				(layoutPageTemplateEntry.getType() !=
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE)) {

				continue;
			}

			_populateDisplayPagesZipWriter(
				layoutPageTemplateEntry, pageDefinitionDTOConverter,
				StringPool.BLANK, zipWriter);
		}

		_exportLayoutPageTemplateCollections(
			TransformUtil.transformToList(
				layoutPageTemplateCollectionIds,
				layoutPageTemplateCollectionId ->
					_layoutPageTemplateCollectionService.
						fetchLayoutPageTemplateCollection(
							layoutPageTemplateCollectionId)),
			_getPageDefinitionDTOConverter(), StringPool.BLANK, zipWriter);

		return zipWriter.getFile();
	}

	@Override
	public File exportLayoutUtilityPageEntries(long[] layoutUtilityPageEntryIds)
		throws Exception {

		DTOConverter<LayoutStructure, PageDefinition>
			pageDefinitionDTOConverter = _getPageDefinitionDTOConverter();
		ZipWriter zipWriter = new ZipWriter();

		for (long layoutUtilityPageEntryId : layoutUtilityPageEntryIds) {
			LayoutUtilityPageEntry layoutUtilityPageEntry =
				_layoutUtilityPageEntryService.fetchLayoutUtilityPageEntry(
					layoutUtilityPageEntryId);

			_populateLayoutUtilityPageEntriesZipWriter(
				layoutUtilityPageEntry, pageDefinitionDTOConverter, zipWriter);
		}

		return zipWriter.getFile();
	}

	private void _exportLayoutPageTemplateCollections(
			List<LayoutPageTemplateCollection> layoutPageTemplateCollections,
			DTOConverter<LayoutStructure, PageDefinition>
				pageDefinitionDTOConverter,
			String path, ZipWriter zipWriter)
		throws Exception {

		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				layoutPageTemplateCollections) {

			String layoutPageTemplateCollectionKey =
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionKey();

			String newPath =
				path + StringPool.SLASH + layoutPageTemplateCollectionKey;

			_populateDisplayPagesZipWriter(
				layoutPageTemplateCollection, newPath, zipWriter);

			List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
				_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
					layoutPageTemplateCollection.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					WorkflowConstants.STATUS_APPROVED);

			for (LayoutPageTemplateEntry layoutPageTemplateEntry :
					layoutPageTemplateEntries) {

				_populateDisplayPagesZipWriter(
					layoutPageTemplateEntry, pageDefinitionDTOConverter,
					newPath, zipWriter);
			}

			_exportLayoutPageTemplateCollections(
				_layoutPageTemplateCollectionService.
					getLayoutPageTemplateCollections(
						layoutPageTemplateCollection.getGroupId(),
						layoutPageTemplateCollection.
							getLayoutPageTemplateCollectionId()),
				pageDefinitionDTOConverter, newPath, zipWriter);
		}
	}

	private File _exportLayoutPageTemplateEntries(
			long[] layoutPageTemplateEntryIds, int type,
			UnsafeTriConsumer
				<LayoutPageTemplateEntry,
				 DTOConverter<LayoutStructure, PageDefinition>, ZipWriter,
				 Exception> unsafeConsumer)
		throws Exception {

		DTOConverter<LayoutStructure, PageDefinition>
			pageDefinitionDTOConverter = _getPageDefinitionDTOConverter();
		ZipWriter zipWriter = new ZipWriter();

		for (long layoutPageTemplateEntryId : layoutPageTemplateEntryIds) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.getLayoutPageTemplateEntry(
					layoutPageTemplateEntryId);

			if (layoutPageTemplateEntry.isDraft() ||
				(layoutPageTemplateEntry.getType() != type)) {

				continue;
			}

			unsafeConsumer.accept(
				layoutPageTemplateEntry, pageDefinitionDTOConverter, zipWriter);
		}

		return zipWriter.getFile();
	}

	private ContentSubtype _getContentSubtype(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return new ContentSubtype() {
			{
				setSubtypeId(
					() -> {
						if (layoutPageTemplateEntry.getClassTypeId() < 0) {
							return null;
						}

						return layoutPageTemplateEntry.getClassTypeId();
					});
				setSubtypeKey(
					() -> {
						if (Validator.isNull(
								layoutPageTemplateEntry.getClassTypeKey())) {

							return null;
						}

						return layoutPageTemplateEntry.getClassTypeKey();
					});
			}
		};
	}

	private DTOConverterContext _getDTOConverterContext(
		Layout layout, LayoutStructure layoutStructure) {

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, layoutStructure.getMainItemId(), null,
				null, null);

		dtoConverterContext.setAttribute("layout", layout);

		return dtoConverterContext;
	}

	private LayoutStructure _getLayoutStructure(Layout layout) {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		return LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());
	}

	private DTOConverter<LayoutStructure, PageDefinition>
		_getPageDefinitionDTOConverter() {

		return (DTOConverter<LayoutStructure, PageDefinition>)
			_dtoConverterRegistry.getDTOConverter(
				LayoutStructure.class.getName());
	}

	private FileEntry _getPreviewFileEntry(long previewFileEntryId) {
		if (previewFileEntryId <= 0) {
			return null;
		}

		try {
			return PortletFileRepositoryUtil.getPortletFileEntry(
				previewFileEntryId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get file entry preview", portalException);
			}
		}

		return null;
	}

	private void _populateDisplayPagesZipWriter(
			LayoutPageTemplateCollection layoutPageTemplateCollection,
			String path, ZipWriter zipWriter)
		throws IOException {

		zipWriter.addEntry(
			path + StringPool.SLASH +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_DISPLAY_PAGE_TEMPLATE_COLLECTION,
			JSONUtil.put(
				"description", layoutPageTemplateCollection.getDescription()
			).put(
				"name", layoutPageTemplateCollection.getName()
			).toString());
	}

	private void _populateDisplayPagesZipWriter(
			LayoutPageTemplateEntry layoutPageTemplateEntry,
			DTOConverter<LayoutStructure, PageDefinition>
				pageDefinitionDTOConverter,
			String path, ZipWriter zipWriter)
		throws Exception {

		String displayPagePath =
			path + "/display-page-templates/" +
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey();

		DisplayPageTemplate displayPageTemplate = _toDisplayPageTemplate(
			layoutPageTemplateEntry);

		zipWriter.addEntry(
			displayPagePath + StringPool.SLASH +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_DISPLAY_PAGE_TEMPLATE,
			displayPageTemplate.toString());

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		if (layout != null) {
			LayoutStructure layoutStructure = _getLayoutStructure(layout);

			PageDefinition pageDefinition = pageDefinitionDTOConverter.toDTO(
				_getDTOConverterContext(layout, layoutStructure),
				layoutStructure);

			zipWriter.addEntry(
				displayPagePath + "/page-definition.json",
				pageDefinition.toString());
		}

		FileEntry previewFileEntry = _getPreviewFileEntry(
			layoutPageTemplateEntry.getPreviewFileEntryId());

		if (previewFileEntry != null) {
			zipWriter.addEntry(
				displayPagePath + "/thumbnail." +
					previewFileEntry.getExtension(),
				previewFileEntry.getContentStream());
		}
	}

	private void _populateLayoutUtilityPageEntriesZipWriter(
			LayoutUtilityPageEntry layoutUtilityPageEntry,
			DTOConverter<LayoutStructure, PageDefinition>
				pageDefinitionDTOConverter,
			ZipWriter zipWriter)
		throws Exception {

		String layoutUtilityPageEntryPath =
			"layout-utility-page-template/" +
				layoutUtilityPageEntry.getExternalReferenceCode();

		UtilityPageTemplate utilityPageTemplate =
			UtilityPageTemplateUtil.toUtilityPageTemplate(
				layoutUtilityPageEntry);

		zipWriter.addEntry(
			layoutUtilityPageEntryPath + "/utility-page.json",
			utilityPageTemplate.toString());

		Layout layout = _layoutLocalService.fetchLayout(
			layoutUtilityPageEntry.getPlid());

		if (layout != null) {
			LayoutStructure layoutStructure = _getLayoutStructure(layout);

			PageDefinition pageDefinition = pageDefinitionDTOConverter.toDTO(
				_getDTOConverterContext(layout, layoutStructure),
				layoutStructure);

			zipWriter.addEntry(
				layoutUtilityPageEntryPath + "/page-definition.json",
				pageDefinition.toString());
		}

		FileEntry previewFileEntry = _getPreviewFileEntry(
			layoutUtilityPageEntry.getPreviewFileEntryId());

		if (previewFileEntry != null) {
			zipWriter.addEntry(
				layoutUtilityPageEntryPath + "/thumbnail." +
					previewFileEntry.getExtension(),
				previewFileEntry.getContentStream());
		}
	}

	private void _populateMasterLayoutsZipWriter(
			LayoutPageTemplateEntry layoutPageTemplateEntry,
			DTOConverter<LayoutStructure, PageDefinition>
				pageDefinitionDTOConverter,
			ZipWriter zipWriter)
		throws Exception {

		String masterLayoutPath =
			"master-pages/" +
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey();

		MasterPage masterPage = MasterPageUtil.toMasterPage(
			layoutPageTemplateEntry);

		zipWriter.addEntry(
			masterLayoutPath + StringPool.SLASH +
				LayoutPageTemplateExportImportConstants.FILE_NAME_MASTER_PAGE,
			masterPage.toString());

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		if (layout != null) {
			LayoutStructure layoutStructure = _getLayoutStructure(layout);

			PageDefinition pageDefinition = pageDefinitionDTOConverter.toDTO(
				_getDTOConverterContext(layout, layoutStructure),
				layoutStructure);

			zipWriter.addEntry(
				masterLayoutPath + "/page-definition.json",
				pageDefinition.toString());
		}

		FileEntry previewFileEntry = _getPreviewFileEntry(
			layoutPageTemplateEntry.getPreviewFileEntryId());

		if (previewFileEntry != null) {
			zipWriter.addEntry(
				masterLayoutPath + "/thumbnail." +
					previewFileEntry.getExtension(),
				previewFileEntry.getContentStream());
		}
	}

	private void _populatePageTemplatesZipWriter(
			LayoutPageTemplateEntry layoutPageTemplateEntry,
			DTOConverter<LayoutStructure, PageDefinition>
				pageDefinitionDTOConverter,
			ZipWriter zipWriter)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				getLayoutPageTemplateCollection(
					layoutPageTemplateEntry.
						getLayoutPageTemplateCollectionId());

		String layoutPageTemplateCollectionKey =
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionKey();

		String layoutPageTemplateCollectionPath =
			"page-templates/" + layoutPageTemplateCollectionKey;

		PageTemplateCollection pageTemplateCollection =
			PageTemplateCollectionUtil.toPageTemplateCollection(
				layoutPageTemplateCollection);

		zipWriter.addEntry(
			layoutPageTemplateCollectionPath + StringPool.SLASH +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_PAGE_TEMPLATE_COLLECTION,
			pageTemplateCollection.toString());

		String layoutPageTemplateEntryPath =
			layoutPageTemplateCollectionPath + StringPool.SLASH +
				layoutPageTemplateEntry.getLayoutPageTemplateEntryKey();

		PageTemplate pageTemplate = PageTemplateUtil.toPageTemplate(
			layoutPageTemplateEntry);

		zipWriter.addEntry(
			layoutPageTemplateEntryPath + StringPool.SLASH +
				LayoutPageTemplateExportImportConstants.FILE_NAME_PAGE_TEMPLATE,
			pageTemplate.toString());

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		if (layout != null) {
			LayoutStructure layoutStructure = _getLayoutStructure(layout);

			PageDefinition pageDefinition = pageDefinitionDTOConverter.toDTO(
				_getDTOConverterContext(layout, layoutStructure),
				layoutStructure);

			zipWriter.addEntry(
				layoutPageTemplateEntryPath + "/page-definition.json",
				pageDefinition.toString());
		}

		FileEntry previewFileEntry = _getPreviewFileEntry(
			layoutPageTemplateEntry.getPreviewFileEntryId());

		if (previewFileEntry != null) {
			zipWriter.addEntry(
				layoutPageTemplateEntryPath + "/thumbnail." +
					previewFileEntry.getExtension(),
				previewFileEntry.getContentStream());
		}
	}

	private DisplayPageTemplate _toDisplayPageTemplate(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return new DisplayPageTemplate() {
			{
				setContentSubtype(
					() -> {
						if ((layoutPageTemplateEntry.getClassTypeId() < 0) &&
							Validator.isNull(
								layoutPageTemplateEntry.getClassTypeKey())) {

							return null;
						}

						InfoItemFormVariationsProvider<?>
							infoItemFormVariationsProvider =
								_infoItemServiceRegistry.
									getFirstInfoItemService(
										InfoItemFormVariationsProvider.class,
										_infoSearchClassMapperRegistry.
											getClassName(
												layoutPageTemplateEntry.
													getClassName()));

						if (infoItemFormVariationsProvider == null) {
							return _getContentSubtype(layoutPageTemplateEntry);
						}

						InfoItemFormVariation infoItemFormVariation =
							infoItemFormVariationsProvider.
								getInfoItemFormVariation(
									layoutPageTemplateEntry.getGroupId(),
									layoutPageTemplateEntry.getClassTypeKey(),
									String.valueOf(
										layoutPageTemplateEntry.
											getClassTypeId()));

						if (infoItemFormVariation == null) {
							return _getContentSubtype(layoutPageTemplateEntry);
						}

						return new ContentSubtype() {
							{
								setSubtypeId(
									() -> GetterUtil.getLong(
										infoItemFormVariation.getKey()));

								setSubtypeKey(
									() ->
										infoItemFormVariation.
											getExternalReferenceCode());
							}
						};
					});
				setContentType(
					() -> new ContentType() {
						{
							setClassName(layoutPageTemplateEntry::getClassName);
						}
					});
				setName(layoutPageTemplateEntry::getName);
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutsExporterImpl.class);

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

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
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

}