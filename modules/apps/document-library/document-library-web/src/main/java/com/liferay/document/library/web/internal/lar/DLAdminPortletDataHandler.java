/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.lar;

import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
import com.liferay.document.library.constants.DLPortletDataHandlerConstants;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerChoice;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryRegistryUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Raymond Augé
 * @author Sampsa Sohlman
 * @author Máté Thurzó
 * @author Zsolt Berentey
 * @author Gergely Mathe
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
	service = PortletDataHandler.class
)
public class DLAdminPortletDataHandler extends BasePortletDataHandler {

	public static final String[] CLASS_NAMES = {
		DLFileEntryConstants.getClassName(), DLFileEntryType.class.getName(),
		DLFileShortcutConstants.getClassName(),
		DLFolderConstants.getClassName(), Repository.class.getName(),
		RepositoryEntry.class.getName()
	};

	public static final String NAMESPACE =
		DLPortletDataHandlerConstants.NAMESPACE;

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String getResourceName() {
		return DLConstants.RESOURCE_NAME;
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public String getServiceName() {
		return DLConstants.SERVICE_NAME;
	}

	@Override
	public boolean isSupportsDataStrategyMirrorWithOverwriting() {
		return true;
	}

	@Activate
	protected void activate() {
		setDataLocalized(true);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(DLFileEntryType.class),
			new StagedModelType(DLFileShortcut.class),
			new StagedModelType(DLFileEntryConstants.getClassName()),
			new StagedModelType(DLFolderConstants.getClassName()),
			new StagedModelType(
				Repository.class.getName(),
				StagedModelType.REFERRER_CLASS_NAME_ALL));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "repositories", true, false, null,
				Repository.class.getName(),
				StagedModelType.REFERRER_CLASS_NAME_ALL),
			new PortletDataHandlerBoolean(
				NAMESPACE, "folders", true, false, null,
				DLFolderConstants.getClassName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "documents", true, false,
				new PortletDataHandlerControl[] {
					new PortletDataHandlerBoolean(
						NAMESPACE, "previews-and-thumbnails"),
					new PortletDataHandlerBoolean(
						getNamespace(), "referenced-content", true, false,
						new PortletDataHandlerControl[] {
							new PortletDataHandlerChoice(
								getNamespace(), "referenced-content-behavior",
								0,
								new String[] {
									"include-always", "include-if-modified"
								})
						})
				},
				DLFileEntryConstants.getClassName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "document-types", true, false, null,
				DLFileEntryType.class.getName()),
			new PortletDataHandlerBoolean(
				getNamespace(), "metadata", true, false, null,
				DDMStructure.class.getName(),
				DLFileEntryMetadata.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "shortcuts", true, false, null,
				DLFileShortcutConstants.getClassName()));
		setPublishToLiveByDefault(PropsValues.DL_PUBLISH_TO_LIVE_BY_DEFAULT);
		setRank(90);
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				DLPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_dlAppLocalService.deleteAllRepositories(
			portletDataContext.getScopeGroupId());
		_dlFileEntryTypeLocalService.deleteFileEntryTypes(
			portletDataContext.getScopeGroupId());

		_ddmStructureLocalService.deleteStructures(
			portletDataContext.getScopeGroupId(),
			_portal.getClassNameId(DLFileEntryMetadata.class));

		if (portletPreferences == null) {
			return portletPreferences;
		}

		portletPreferences.setValue("enable-comment-ratings", StringPool.BLANK);
		portletPreferences.setValue("fileEntriesPerPage", StringPool.BLANK);
		portletPreferences.setValue("fileEntryColumns", StringPool.BLANK);
		portletPreferences.setValue("folderColumns", StringPool.BLANK);
		portletPreferences.setValue("foldersPerPage", StringPool.BLANK);
		portletPreferences.setValue(
			"rootFolderExternalReferenceCode", StringPool.BLANK);
		portletPreferences.setValue(
			"selectedGroupExternalReferenceCode", StringPool.BLANK);
		portletPreferences.setValue(
			"selectedRepositoryExternalReferenceCode", StringPool.BLANK);
		portletPreferences.setValue("showFoldersSearch", StringPool.BLANK);
		portletPreferences.setValue("showSubfolders", StringPool.BLANK);

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		portletDataContext.addPortletPermissions(DLConstants.RESOURCE_NAME);

		Element rootElement = addExportDataRootElement(portletDataContext);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		if (portletDataContext.getBooleanParameter(NAMESPACE, "folders")) {
			StagedModelRepository<?> stagedModelRepository =
				StagedModelRepositoryRegistryUtil.getStagedModelRepository(
					DLFolder.class.getName());

			ActionableDynamicQuery folderActionableDynamicQuery =
				stagedModelRepository.getExportActionableDynamicQuery(
					portletDataContext);

			folderActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "documents")) {
			StagedModelRepository<?> stagedModelRepository =
				StagedModelRepositoryRegistryUtil.getStagedModelRepository(
					DLFileEntry.class.getName());

			ActionableDynamicQuery fileEntryActionableDynamicQuery =
				stagedModelRepository.getExportActionableDynamicQuery(
					portletDataContext);

			fileEntryActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(
				NAMESPACE, "document-types")) {

			StagedModelRepository<?> stagedModelRepository =
				StagedModelRepositoryRegistryUtil.getStagedModelRepository(
					DLFileEntryType.class.getName());

			ActionableDynamicQuery fileEntryTypeActionableDynamicQuery =
				stagedModelRepository.getExportActionableDynamicQuery(
					portletDataContext);

			fileEntryTypeActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "metadata")) {
			ExportActionableDynamicQuery exportActionableDynamicQuery =
				_ddmStructureLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			ActionableDynamicQuery.AddCriteriaMethod addCriteriaMethod =
				exportActionableDynamicQuery.getAddCriteriaMethod();

			exportActionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					addCriteriaMethod.addCriteria(dynamicQuery);

					Property classNameIdProperty = PropertyFactoryUtil.forName(
						"classNameId");

					dynamicQuery.add(
						classNameIdProperty.eq(
							_portal.getClassNameId(DLFileEntryMetadata.class)));
				});

			exportActionableDynamicQuery.setStagedModelType(
				new StagedModelType(
					DDMStructure.class.getName(),
					DLFileEntryMetadata.class.getName()));

			exportActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "repositories")) {
			StagedModelRepository<?> stagedModelRepository =
				StagedModelRepositoryRegistryUtil.getStagedModelRepository(
					Repository.class.getName());

			ActionableDynamicQuery repositoryActionableDynamicQuery =
				stagedModelRepository.getExportActionableDynamicQuery(
					portletDataContext);

			repositoryActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "shortcuts")) {
			StagedModelRepository<?> stagedModelRepository =
				StagedModelRepositoryRegistryUtil.getStagedModelRepository(
					DLFileShortcut.class.getName());

			ActionableDynamicQuery fileShortcutActionableDynamicQuery =
				stagedModelRepository.getExportActionableDynamicQuery(
					portletDataContext);

			fileShortcutActionableDynamicQuery.performActions();
		}

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortletPermissions(DLConstants.RESOURCE_NAME);

		if (portletDataContext.getBooleanParameter(NAMESPACE, "folders")) {
			Element foldersElement =
				portletDataContext.getImportDataGroupElement(DLFolder.class);

			List<Element> folderElements = foldersElement.elements();

			for (Element folderElement : folderElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, folderElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "documents")) {
			Element fileEntriesElement =
				portletDataContext.getImportDataGroupElement(DLFileEntry.class);

			List<Element> fileEntryElements = fileEntriesElement.elements();

			for (Element fileEntryElement : fileEntryElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, fileEntryElement);
			}
		}

		if (portletDataContext.getBooleanParameter(
				NAMESPACE, "document-types")) {

			Element fileEntryTypesElement =
				portletDataContext.getImportDataGroupElement(
					DLFileEntryType.class);

			List<Element> fileEntryTypeElements =
				fileEntryTypesElement.elements();

			for (Element fileEntryTypeElement : fileEntryTypeElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, fileEntryTypeElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "metadata")) {
			Element ddmStructuresElement =
				portletDataContext.getImportDataGroupElement(
					DDMStructure.class);

			List<Element> ddmStructureElements =
				ddmStructuresElement.elements();

			for (Element ddmStructureElement : ddmStructureElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, ddmStructureElement);
			}

			for (Element ddmStructureElement : ddmStructureElements) {
				List<Element> deDataDefinitionFieldLinkElements =
					portletDataContext.getReferenceDataElements(
						ddmStructureElement, DEDataDefinitionFieldLink.class,
						null);

				for (Element deDataDefinitionFieldLinkElement :
						deDataDefinitionFieldLinkElements) {

					String path =
						deDataDefinitionFieldLinkElement.attributeValue("path");

					DEDataDefinitionFieldLink deDataDefinitionFieldLink =
						(DEDataDefinitionFieldLink)
							portletDataContext.getZipEntryAsObject(
								deDataDefinitionFieldLinkElement, path);

					StagedModelDataHandlerUtil.importStagedModel(
						portletDataContext, deDataDefinitionFieldLink);
				}
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "repositories")) {
			Element repositoriesElement =
				portletDataContext.getImportDataGroupElement(Repository.class);

			List<Element> repositoryElements = repositoriesElement.elements();

			for (Element repositoryElement : repositoryElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, repositoryElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "shortcuts")) {
			Element fileShortcutsElement =
				portletDataContext.getImportDataGroupElement(
					DLFileShortcut.class);

			List<Element> fileShortcutElements =
				fileShortcutsElement.elements();

			for (Element fileShortcutElement : fileShortcutElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, fileShortcutElement);
			}
		}

		Element friendlyURLEntriesElement =
			portletDataContext.getImportDataGroupElement(
				FriendlyURLEntry.class);

		List<Element> friendlyURLEntryElements =
			friendlyURLEntriesElement.elements();

		for (Element friendlyURLEntryElement : friendlyURLEntryElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, friendlyURLEntryElement);
		}

		return portletPreferences;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		if (ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext)) {

			_staging.populateLastPublishDateCounts(
				portletDataContext,
				new StagedModelType[] {
					new StagedModelType(DLFileEntry.class.getName()),
					new StagedModelType(DLFileEntryType.class.getName()),
					new StagedModelType(
						DDMStructure.class.getName(),
						DLFileEntryMetadata.class.getName()),
					new StagedModelType(DLFileShortcut.class.getName()),
					new StagedModelType(DLFolder.class.getName()),
					new StagedModelType(Repository.class.getName())
				});

			return;
		}

		StagedModelRepository<?> fileShortcutStagedModelRepository =
			StagedModelRepositoryRegistryUtil.getStagedModelRepository(
				DLFileShortcut.class.getName());

		ActionableDynamicQuery dlFileShortcutActionableDynamicQuery =
			fileShortcutStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		dlFileShortcutActionableDynamicQuery.performCount();

		StagedModelRepository<?> fileEntryStagedModelRepository =
			StagedModelRepositoryRegistryUtil.getStagedModelRepository(
				DLFileEntry.class.getName());

		ActionableDynamicQuery fileEntryActionableDynamicQuery =
			fileEntryStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		fileEntryActionableDynamicQuery.performCount();

		StagedModelRepository<?> dlFileEntryTypeStagedModelRepository =
			StagedModelRepositoryRegistryUtil.getStagedModelRepository(
				DLFileEntryType.class.getName());

		ActionableDynamicQuery fileEntryTypeActionableDynamicQuery =
			dlFileEntryTypeStagedModelRepository.
				getExportActionableDynamicQuery(portletDataContext);

		fileEntryTypeActionableDynamicQuery.performCount();

		StagedModelRepository<?> folderStagedModelRepository =
			StagedModelRepositoryRegistryUtil.getStagedModelRepository(
				DLFolder.class.getName());

		ActionableDynamicQuery folderActionableDynamicQuery =
			folderStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		folderActionableDynamicQuery.performCount();

		StagedModelRepository<?> repositoryStagedModelRepository =
			StagedModelRepositoryRegistryUtil.getStagedModelRepository(
				Repository.class.getName());

		ActionableDynamicQuery repositoryActionableDynamicQuery =
			repositoryStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		repositoryActionableDynamicQuery.performCount();
	}

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private Staging _staging;

}