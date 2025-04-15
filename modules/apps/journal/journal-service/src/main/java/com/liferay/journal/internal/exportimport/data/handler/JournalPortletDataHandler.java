/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.exportimport.data.handler;

import com.liferay.changeset.model.ChangesetCollection;
import com.liferay.changeset.model.ChangesetEntry;
import com.liferay.changeset.service.ChangesetCollectionLocalService;
import com.liferay.changeset.service.ChangesetEntryLocalService;
import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
import com.liferay.data.engine.service.DEDataDefinitionFieldLinkLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerChoice;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryRegistryUtil;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFeedLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.util.JournalContent;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the Journal portlet export and import functionality, which is to
 * clone all articles, structures, and templates associated with the layout's
 * group. Upon import, new instances of the corresponding articles, structures,
 * and templates are created or updated according to the DATA_MIRROW strategy
 * The author of the newly created objects are determined by the
 * JournalCreationStrategy class defined in <i>portal.properties</i>. That
 * strategy also allows the text of the journal article to be modified prior to
 * import.
 *
 * <p>
 * This <code>PortletDataHandler</code> differs from
 * <code>JournalContentPortletDataHandlerImpl</code> in that it exports all
 * articles owned by the group whether or not they are actually displayed in a
 * portlet in the layout set.
 * </p>
 *
 * <p>
 * For a better understanding of this class, see
 * <code>com.liferay.journal.content.web.lar.JournalContentPortletDataHandler</code>
 * located in Liferay Portal's external <code>modules</code> directory.
 * </p>
 *
 * @author Raymond Augé
 * @author Joel Kozikowski
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 * @author Karthik Sudarshan
 * @author Wesley Gong
 * @author Hugo Huijser
 * @author Daniel Kocsis
 * @author László Csontos
 * @author Máté Thurzó
 * @see    com.liferay.journal.internal.exportimport.creation.strategy.JournalCreationStrategy
 * @see    PortletDataHandler
 */
@Component(
	configurationPid = "com.liferay.journal.configuration.JournalServiceConfiguration",
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"schema.version=" + JournalPortletDataHandler.SCHEMA_VERSION
	},
	service = PortletDataHandler.class
)
public class JournalPortletDataHandler extends BasePortletDataHandler {

	public static final String[] CLASS_NAMES = {
		JournalArticle.class.getName(), JournalFolder.class.getName()
	};

	public static final String NAMESPACE = "journal";

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
		return JournalConstants.RESOURCE_NAME;
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public String getServiceName() {
		return JournalConstants.SERVICE_NAME;
	}

	@Override
	public boolean isPublishToLiveByDefault() {
		try {
			JournalServiceConfiguration journalServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					JournalServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return journalServiceConfiguration.publishToLiveByDefaultEnabled();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return true;
	}

	@Override
	public boolean isSupportsDataStrategyMirrorWithOverwriting() {
		return false;
	}

	@Activate
	@Modified
	protected void activate() {
		setDataLocalized(true);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(DDMStructure.class, JournalArticle.class),
			new StagedModelType(DDMTemplate.class, DDMStructure.class),
			new StagedModelType(JournalArticle.class),
			new StagedModelType(JournalArticle.class, DDMStructure.class),
			new StagedModelType(JournalFeed.class),
			new StagedModelType(JournalFolder.class));

		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "web-content", true, false,
				new PortletDataHandlerControl[] {
					new PortletDataHandlerBoolean(
						NAMESPACE, "referenced-content", true, false,
						new PortletDataHandlerControl[] {
							new PortletDataHandlerChoice(
								NAMESPACE, "referenced-content-behavior", 0,
								new String[] {
									"include-always", "include-if-modified"
								})
						}),
					new PortletDataHandlerBoolean(
						NAMESPACE, "version-history",
						_isVersionHistoryByDefaultEnabled())
				},
				JournalArticle.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "structures", true, false, null,
				DDMStructure.class.getName(), JournalArticle.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "templates", true, false, null,
				DDMTemplate.class.getName(), DDMStructure.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "feeds", true, false, null,
				JournalFeed.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "folders", true, false, null,
				JournalFolder.class.getName()));
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				JournalPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_journalArticleLocalService.deleteArticles(
			portletDataContext.getScopeGroupId());

		_journalFolderLocalService.deleteFolders(
			portletDataContext.getGroupId());

		long ddmStructureClassNameId = _portal.getClassNameId(
			DDMStructure.class);
		long journalArticleClassNameId = _portal.getClassNameId(
			JournalArticle.class);

		List<DDMTemplate> ddmTemplates = _ddmTemplateLocalService.getTemplates(
			portletDataContext.getCompanyId(),
			new long[] {portletDataContext.getGroupId()},
			new long[] {ddmStructureClassNameId}, null,
			journalArticleClassNameId, -1, -1, null);

		for (DDMTemplate ddmTemplate : ddmTemplates) {
			_ddmTemplateLocalService.deleteTemplate(ddmTemplate);
		}

		List<DDMStructure> ddmStructures =
			_ddmStructureLocalService.getStructures(
				portletDataContext.getScopeGroupId(),
				journalArticleClassNameId);

		long ddmStructureLayoutClassNameId = _portal.getClassNameId(
			DDMStructureLayout.class);

		for (DDMStructure ddmStructure : ddmStructures) {
			_deDataDefinitionFieldLinkLocalService.
				deleteDEDataDefinitionFieldLinks(
					ddmStructureClassNameId, ddmStructure.getStructureId());

			List<DDMStructureVersion> ddmStructureVersions =
				_ddmStructureVersionLocalService.getStructureVersions(
					ddmStructure.getStructureId());

			for (DDMStructureVersion ddmStructureVersion :
					ddmStructureVersions) {

				List<DDMStructureLayout> ddmStructureLayouts =
					_ddmStructureLayoutLocalService.getStructureLayouts(
						ddmStructure.getGroupId(),
						ddmStructure.getClassNameId(),
						ddmStructureVersion.getStructureVersionId());

				for (DDMStructureLayout ddmStructureLayout :
						ddmStructureLayouts) {

					_deDataDefinitionFieldLinkLocalService.
						deleteDEDataDefinitionFieldLinks(
							ddmStructureLayoutClassNameId,
							ddmStructureLayout.getStructureLayoutId());
				}
			}

			_ddlRecordSetLocalService.deleteDDMStructureRecordSets(
				ddmStructure.getStructureId());
		}

		_ddmStructureLocalService.deleteStructures(
			portletDataContext.getScopeGroupId(), journalArticleClassNameId);

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		portletDataContext.addPortletPermissions(
			JournalConstants.RESOURCE_NAME);

		Element rootElement = addExportDataRootElement(portletDataContext);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		if (portletDataContext.getBooleanParameter(NAMESPACE, "feeds")) {
			ActionableDynamicQuery feedActionableDynamicQuery =
				_journalFeedLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			feedActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "folders")) {
			ActionableDynamicQuery folderActionableDynamicQuery =
				_journalFolderLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			folderActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "structures")) {
			ActionableDynamicQuery ddmStructureActionableDynamicQuery =
				_getDDMStructureActionableDynamicQuery(portletDataContext);

			ddmStructureActionableDynamicQuery.performActions();

			// Export DDM structure default values

			ActionableDynamicQuery
				ddmStructureDefaultValuesActionableDynamicQuery =
					_getDDMStructureDefaultValuesActionableDynamicQuery(
						portletDataContext);

			ddmStructureDefaultValuesActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "templates")) {
			ActionableDynamicQuery ddmTemplateActionableDynamicQuery =
				_getDDMTemplateActionableDynamicQuery(portletDataContext);

			ddmTemplateActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "web-content")) {
			ActionableDynamicQuery articleActionableDynamicQuery =
				_getArticleActionableDynamicQuery(portletDataContext);

			articleActionableDynamicQuery.performActions();
		}

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortletPermissions(
			JournalConstants.RESOURCE_NAME);

		if (portletDataContext.getBooleanParameter(NAMESPACE, "feeds")) {
			Element feedsElement = portletDataContext.getImportDataGroupElement(
				JournalFeed.class);

			List<Element> feedElements = feedsElement.elements();

			for (Element feedElement : feedElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, feedElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "folders")) {
			Element foldersElement =
				portletDataContext.getImportDataGroupElement(
					JournalFolder.class);

			List<Element> folderElements = foldersElement.elements();

			for (Element folderElement : folderElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, folderElement);
			}
		}

		Element articlesElement = portletDataContext.getImportDataGroupElement(
			JournalArticle.class);

		List<Element> articleElements = articlesElement.elements();

		if (portletDataContext.getBooleanParameter(NAMESPACE, "structures")) {
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

			// Importing DDM structure default values

			for (Element articleElement : articleElements) {
				String className = articleElement.attributeValue(
					"attached-class-name");

				if (Validator.isNotNull(className) &&
					className.equals(DDMStructure.class.getName())) {

					StagedModelDataHandlerUtil.importStagedModel(
						portletDataContext, articleElement);
				}
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "templates")) {
			Element ddmTemplatesElement =
				portletDataContext.getImportDataGroupElement(DDMTemplate.class);

			List<Element> ddmTemplateElements = ddmTemplatesElement.elements();

			for (Element ddmTemplateElement : ddmTemplateElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, ddmTemplateElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "web-content")) {
			for (Element articleElement : articleElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, articleElement);
			}

			Map<String, String> postProcessArticleUuids =
				(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
					JournalArticle.class + ".postProcessArticleUuid");

			Collection<String> articleModelPaths =
				postProcessArticleUuids.values();

			articleModelPaths.forEach(portletDataContext::removePrimaryKey);

			for (Element articleElement : articleElements) {
				String uuid = articleElement.attributeValue("uuid");

				if (postProcessArticleUuids.remove(uuid) != null) {
					StagedModelDataHandlerUtil.importStagedModel(
						portletDataContext, articleElement);
				}
			}

			_journalContent.clearCache();
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
					new StagedModelType(
						DDMStructure.class.getName(),
						JournalArticle.class.getName()),
					new StagedModelType(JournalFeed.class.getName()),
					new StagedModelType(JournalFolder.class.getName())
				});

			_populateDDMTemplateLastPublishDateCounts(portletDataContext);
			_populateJournalArticleLastPublishDateCounts(portletDataContext);

			return;
		}

		ActionableDynamicQuery articleActionableDynamicQuery =
			_getArticleActionableDynamicQuery(portletDataContext);

		articleActionableDynamicQuery.performCount();

		ActionableDynamicQuery ddmStructureActionableDynamicQuery =
			_getDDMStructureActionableDynamicQuery(portletDataContext);

		ddmStructureActionableDynamicQuery.performCount();

		ActionableDynamicQuery ddmTemplateActionableDynamicQuery =
			_getDDMTemplateActionableDynamicQuery(portletDataContext);

		ddmTemplateActionableDynamicQuery.performCount();

		ActionableDynamicQuery feedActionableDynamicQuery =
			_journalFeedLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		feedActionableDynamicQuery.performCount();

		ActionableDynamicQuery folderActionableDynamicQuery =
			_journalFolderLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		folderActionableDynamicQuery.performCount();
	}

	private ActionableDynamicQuery _getArticleActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		ExportActionableDynamicQuery exportActionableDynamicQuery =
			_journalArticleLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		ExportActionableDynamicQuery.AddCriteriaMethod addCriteriaMethod =
			exportActionableDynamicQuery.getAddCriteriaMethod();

		exportActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				addCriteriaMethod.addCriteria(dynamicQuery);

				if (portletDataContext.getBooleanParameter(
						NAMESPACE, "version-history")) {

					return;
				}

				Class<?> clazz = getClass();

				DynamicQuery versionArticleDynamicQuery =
					DynamicQueryFactoryUtil.forClass(
						JournalArticle.class, "versionArticle",
						clazz.getClassLoader());

				versionArticleDynamicQuery.setProjection(
					ProjectionFactoryUtil.alias(
						ProjectionFactoryUtil.max("versionArticle.version"),
						"versionArticle.version"));

				// We need to use the "this" default alias to make sure the
				// database engine handles this subquery as a correlated
				// subquery

				versionArticleDynamicQuery.add(
					RestrictionsFactoryUtil.eqProperty(
						"this.resourcePrimKey",
						"versionArticle.resourcePrimKey"));

				Property workflowStatusProperty = PropertyFactoryUtil.forName(
					"status");

				versionArticleDynamicQuery.add(
					workflowStatusProperty.in(
						_journalArticleStagedModelDataHandler.
							getExportableStatuses()));

				Property versionProperty = PropertyFactoryUtil.forName(
					"version");

				dynamicQuery.add(
					versionProperty.eq(versionArticleDynamicQuery));
			});

		exportActionableDynamicQuery.setStagedModelType(
			new StagedModelType(JournalArticle.class.getName()));

		return exportActionableDynamicQuery;
	}

	private ActionableDynamicQuery _getDDMStructureActionableDynamicQuery(
		PortletDataContext portletDataContext) {

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
						_portal.getClassNameId(JournalArticle.class)));
			});

		exportActionableDynamicQuery.setStagedModelType(
			new StagedModelType(
				DDMStructure.class.getName(), JournalArticle.class.getName()));

		return exportActionableDynamicQuery;
	}

	private ActionableDynamicQuery
		_getDDMStructureDefaultValuesActionableDynamicQuery(
			PortletDataContext portletDataContext) {

		ExportActionableDynamicQuery exportActionableDynamicQuery =
			_journalArticleLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		exportActionableDynamicQuery.setStagedModelType(
			new StagedModelType(
				JournalArticle.class.getName(), DDMStructure.class.getName()));

		return exportActionableDynamicQuery;
	}

	private ActionableDynamicQuery _getDDMTemplateActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		ExportActionableDynamicQuery exportActionableDynamicQuery =
			_ddmTemplateLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		ActionableDynamicQuery.AddCriteriaMethod addCriteriaMethod =
			exportActionableDynamicQuery.getAddCriteriaMethod();

		exportActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				addCriteriaMethod.addCriteria(dynamicQuery);

				Property classNameIdProperty = PropertyFactoryUtil.forName(
					"classNameId");

				long ddmStructureClassNameId = _portal.getClassNameId(
					DDMStructure.class);

				dynamicQuery.add(
					classNameIdProperty.eq(ddmStructureClassNameId));

				long journalArticleClassNameId = _portal.getClassNameId(
					JournalArticle.class);

				Property resourceClassNameIdProperty =
					PropertyFactoryUtil.forName("resourceClassNameId");

				dynamicQuery.add(
					resourceClassNameIdProperty.eq(journalArticleClassNameId));
			});

		exportActionableDynamicQuery.setStagedModelType(
			new StagedModelType(
				DDMTemplate.class.getName(), DDMStructure.class.getName()));

		return exportActionableDynamicQuery;
	}

	private boolean _isVersionHistoryByDefaultEnabled() {
		try {
			JournalServiceConfiguration journalServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					JournalServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return journalServiceConfiguration.versionHistoryByDefaultEnabled();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return true;
	}

	private void _populateDDMTemplateLastPublishDateCounts(
			PortletDataContext portletDataContext)
		throws Exception {

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		StagedModelType stagedModelType = new StagedModelType(
			DDMTemplate.class.getName(), DDMStructure.class.getName());

		long modelAdditionCount = manifestSummary.getModelAdditionCount(
			stagedModelType);

		if (modelAdditionCount > -1) {
			return;
		}

		ChangesetCollection changesetCollection =
			_changesetCollectionLocalService.fetchChangesetCollection(
				portletDataContext.getScopeGroupId(),
				StagingConstants.RANGE_FROM_LAST_PUBLISH_DATE_CHANGESET_NAME);

		if (changesetCollection != null) {
			StagedModelRepository<?> stagedModelRepository =
				StagedModelRepositoryRegistryUtil.getStagedModelRepository(
					stagedModelType.getClassName());

			if (stagedModelRepository != null) {
				long journalArticleClassNameId = _portal.getClassNameId(
					JournalArticle.class);
				modelAdditionCount = 0;

				for (ChangesetEntry changesetEntry :
						_changesetEntryLocalService.getChangesetEntries(
							changesetCollection.getChangesetCollectionId(),
							stagedModelType.getClassNameId())) {

					DDMTemplate ddmTemplate =
						(DDMTemplate)stagedModelRepository.getStagedModel(
							changesetEntry.getClassPK());

					if (Objects.equals(
							ddmTemplate.getClassNameId(),
							stagedModelType.getReferrerClassNameId()) &&
						Objects.equals(
							ddmTemplate.getResourceClassNameId(),
							journalArticleClassNameId)) {

						modelAdditionCount++;
					}
				}
			}

			manifestSummary.addModelAdditionCount(
				stagedModelType, modelAdditionCount);
		}

		long modelDeletionCount = _exportImportHelper.getModelDeletionCount(
			portletDataContext, stagedModelType);

		manifestSummary.addModelDeletionCount(
			stagedModelType, modelDeletionCount);
	}

	private void _populateJournalArticleLastPublishDateCounts(
			PortletDataContext portletDataContext)
		throws Exception {

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		StagedModelType articleStagedModelType = new StagedModelType(
			JournalArticle.class);

		long modelAdditionCount = manifestSummary.getModelAdditionCount(
			articleStagedModelType);

		if (modelAdditionCount > -1) {
			return;
		}

		ChangesetCollection changesetCollection =
			_changesetCollectionLocalService.fetchChangesetCollection(
				portletDataContext.getScopeGroupId(),
				StagingConstants.RANGE_FROM_LAST_PUBLISH_DATE_CHANGESET_NAME);

		if (changesetCollection != null) {
			modelAdditionCount =
				_changesetEntryLocalService.getChangesetEntriesCount(
					changesetCollection.getChangesetCollectionId(),
					_portal.getClassNameId(JournalArticleResource.class));

			manifestSummary.addModelAdditionCount(
				articleStagedModelType, modelAdditionCount);
		}

		long modelDeletionCount = _exportImportHelper.getModelDeletionCount(
			portletDataContext, articleStagedModelType);

		manifestSummary.addModelDeletionCount(
			articleStagedModelType, modelDeletionCount);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalPortletDataHandler.class);

	@Reference
	private ChangesetCollectionLocalService _changesetCollectionLocalService;

	@Reference
	private ChangesetEntryLocalService _changesetEntryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DDLRecordSetLocalService _ddlRecordSetLocalService;

	@Reference
	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DEDataDefinitionFieldLinkLocalService
		_deDataDefinitionFieldLinkLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference(
		target = "(component.name=com.liferay.journal.internal.exportimport.data.handler.JournalArticleStagedModelDataHandler)"
	)
	private StagedModelDataHandler<JournalArticle>
		_journalArticleStagedModelDataHandler;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private JournalFeedLocalService _journalFeedLocalService;

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

	@Reference
	private Staging _staging;

}