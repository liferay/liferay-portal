/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.controller;

import com.liferay.asset.link.model.adapter.StagedAssetLink;
import com.liferay.expando.kernel.exception.NoSuchTableException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.util.ExpandoConverterUtil;
import com.liferay.exportimport.changeset.constants.ChangesetPortletKeys;
import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.controller.PortletImportController;
import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.exception.ExportImportDocumentException;
import com.liferay.exportimport.kernel.exception.LARFileException;
import com.liferay.exportimport.kernel.exception.LARTypeException;
import com.liferay.exportimport.kernel.exception.LayoutImportException;
import com.liferay.exportimport.kernel.exception.MissingReferenceException;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.MissingReference;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerStatusMessageSender;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.lar.DeletionSystemEventImporter;
import com.liferay.exportimport.lar.PermissionImporter;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorRegistryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.PortletIdException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.plugin.Version;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portlet.PortletPreferencesImpl;

import java.io.File;
import java.io.Serializable;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

import org.apache.commons.lang.time.StopWatch;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Joel Kozikowski
 * @author Charles May
 * @author Raymond Augé
 * @author Jorge Ferrer
 * @author Bruno Farache
 * @author Zsigmond Rab
 * @author Douglas Wong
 * @author Máté Thurzó
 */
@Component(
	property = "model.class.name=com.liferay.portal.kernel.model.Portlet",
	service = {ExportImportController.class, PortletImportController.class}
)
public class PortletImportControllerImpl implements PortletImportController {

	@Override
	public void deletePortletData(PortletDataContext portletDataContext)
		throws Exception {

		PortletPreferencesIds portletPreferencesIds =
			_portletPreferencesFactory.getPortletPreferencesIds(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), 0,
				portletDataContext.getPlid(),
				portletDataContext.getPortletId());

		jakarta.portlet.PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPreferences(
				portletPreferencesIds);

		if (portletPreferences == null) {
			portletPreferences = new PortletPreferencesImpl();
		}

		String xml = deletePortletData(portletDataContext, portletPreferences);

		if (xml != null) {
			_portletPreferencesLocalService.updatePreferences(
				portletPreferencesIds.getOwnerId(),
				portletPreferencesIds.getOwnerType(),
				portletPreferencesIds.getPlid(),
				portletPreferencesIds.getPortletId(), xml);
		}
	}

	@Override
	public void importAssetLinks(PortletDataContext portletDataContext)
		throws Exception {

		String xml = portletDataContext.getZipEntryAsString(
			ExportImportPathUtil.getSourceRootPath(portletDataContext) +
				"/links.xml");

		if (xml == null) {
			return;
		}

		Element importDataRootElement =
			portletDataContext.getImportDataRootElement();

		try {
			Document document = SAXReaderUtil.read(xml);

			portletDataContext.setImportDataRootElement(
				document.getRootElement());

			Element linksElement = portletDataContext.getImportDataGroupElement(
				StagedAssetLink.class);

			List<Element> linkElements = linksElement.elements();

			for (Element linkElement : linkElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, linkElement);
			}
		}
		finally {
			portletDataContext.setImportDataRootElement(importDataRootElement);
		}
	}

	@Override
	public void importDataDeletions(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws Exception {

		ZipReader zipReader = null;

		try {

			// LAR validation

			ExportImportThreadLocal.setPortletDataDeletionImportInProcess(true);

			Map<String, Serializable> settingsMap =
				exportImportConfiguration.getSettingsMap();

			Map<String, String[]> parameterMap =
				(Map<String, String[]>)settingsMap.get("parameterMap");
			String portletId = MapUtil.getString(settingsMap, "portletId");
			long targetPlid = MapUtil.getLong(settingsMap, "targetPlid");
			long targetGroupId = MapUtil.getLong(settingsMap, "targetGroupId");

			Layout layout = _layoutLocalService.getLayout(targetPlid);

			zipReader = _zipReaderFactory.getZipReader(file);

			validateFile(
				layout.getCompanyId(), targetGroupId, portletId, zipReader);

			PortletDataContext portletDataContext = getPortletDataContext(
				exportImportConfiguration, file);

			boolean deletePortletData = MapUtil.getBoolean(
				parameterMap, PortletDataHandlerKeys.DELETE_PORTLET_DATA);

			// Portlet data deletion

			if (deletePortletData) {
				if (_log.isDebugEnabled()) {
					_log.debug("Deleting portlet data");
				}

				deletePortletData(portletDataContext);
			}

			// Deletion system events

			populateDeletionStagedModelTypes(portletDataContext);

			_deletionSystemEventImporter.importDeletionSystemEvents(
				portletDataContext);
		}
		finally {
			ExportImportThreadLocal.setPortletDataDeletionImportInProcess(
				false);

			if (zipReader != null) {
				zipReader.close();
			}
		}
	}

	@Override
	public void importFile(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws Exception {

		PortletDataContext portletDataContext = null;

		try {
			ExportImportThreadLocal.setPortletImportInProcess(true);

			portletDataContext = getPortletDataContext(
				exportImportConfiguration, file);

			_exportImportLifecycleManager.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_PORTLET_IMPORT_STARTED,
				getProcessFlag(),
				String.valueOf(
					exportImportConfiguration.getExportImportConfigurationId()),
				_portletDataContextFactory.clonePortletDataContext(
					portletDataContext));

			long userId = MapUtil.getLong(
				exportImportConfiguration.getSettingsMap(), "userId");

			_importPortletInfo(portletDataContext, userId);

			ExportImportThreadLocal.setPortletImportInProcess(false);

			_exportImportLifecycleManager.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_PORTLET_IMPORT_SUCCEEDED,
				getProcessFlag(),
				String.valueOf(
					exportImportConfiguration.getExportImportConfigurationId()),
				_portletDataContextFactory.clonePortletDataContext(
					portletDataContext),
				userId);
		}
		catch (Throwable throwable) {
			ExportImportThreadLocal.setPortletImportInProcess(false);

			_exportImportLifecycleManager.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_PORTLET_IMPORT_FAILED,
				getProcessFlag(),
				String.valueOf(
					exportImportConfiguration.getExportImportConfigurationId()),
				_portletDataContextFactory.clonePortletDataContext(
					portletDataContext),
				throwable);

			throw throwable;
		}
	}

	@Override
	public void importPortletData(
			PortletDataContext portletDataContext, Element portletDataElement)
		throws Exception {

		PortletPreferencesIds portletPreferencesIds =
			_portletPreferencesFactory.getPortletPreferencesIds(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), 0,
				portletDataContext.getPlid(),
				portletDataContext.getPortletId());

		jakarta.portlet.PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPreferences(
				portletPreferencesIds);

		if (portletPreferences == null) {
			portletPreferences = new PortletPreferencesImpl();
		}

		String xml = importPortletData(
			portletDataContext, portletPreferences, portletDataElement);

		if (Validator.isNotNull(xml)) {
			_portletPreferencesLocalService.updatePreferences(
				portletPreferencesIds.getOwnerId(),
				portletPreferencesIds.getOwnerType(),
				portletPreferencesIds.getPlid(),
				portletPreferencesIds.getPortletId(), xml);
		}
	}

	public String importPortletData(
			PortletDataContext portletDataContext,
			jakarta.portlet.PortletPreferences portletPreferences,
			Element portletDataElement)
		throws Exception {

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(
				portletDataContext.getCompanyId(),
				portletDataContext.getPortletId());

		if ((portletDataHandler == null) ||
			portletDataHandler.isDataPortletInstanceLevel()) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Do not import portlet data for portlet ",
						portletDataContext.getPortletId(),
						" because the portlet does not have a portlet data ",
						"handler"));
			}

			return null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Importing data for portlet " +
					portletDataContext.getPortletId());
		}

		String portletData = portletDataContext.getZipEntryAsString(
			portletDataElement.attributeValue("path"));

		if (Validator.isNull(portletData)) {
			return null;
		}

		if (ExportImportThreadLocal.isPortletStagingInProcess() &&
			ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext)) {

			String changesetPortletId = ChangesetPortletKeys.CHANGESET;

			Portlet changesetPortlet = _portletLocalService.getPortletById(
				changesetPortletId);

			PortletDataHandler changesetPortletPortletDataHandlerInstance =
				changesetPortlet.getPortletDataHandlerInstance();

			portletPreferences =
				changesetPortletPortletDataHandlerInstance.importData(
					portletDataContext, changesetPortletId, portletPreferences,
					portletData);
		}
		else {
			portletPreferences = portletDataHandler.importData(
				portletDataContext, portletDataContext.getPortletId(),
				portletPreferences, portletData);
		}

		if (portletPreferences == null) {
			return null;
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	@Override
	public void importPortletPreferences(
			PortletDataContext portletDataContext, long companyId, long groupId,
			Layout layout, Element parentElement, boolean preserveScopeLayoutId,
			boolean importPortletArchivedSetups, boolean importPortletData,
			boolean importPortletSetup, boolean importPortletUserPreferences)
		throws Exception {

		long plid = LayoutConstants.DEFAULT_PLID;
		String scopeType = StringPool.BLANK;
		String scopeLayoutUuid = StringPool.BLANK;

		if (layout != null) {
			plid = layout.getPlid();

			if (preserveScopeLayoutId) {
				jakarta.portlet.PortletPreferences jxPortletPreferences =
					PortletPreferencesFactoryUtil.getLayoutPortletSetup(
						layout, portletDataContext.getPortletId());

				scopeType = GetterUtil.getString(
					jxPortletPreferences.getValue("lfrScopeType", null));
				scopeLayoutUuid = GetterUtil.getString(
					jxPortletPreferences.getValue("lfrScopeLayoutUuid", null));

				portletDataContext.setScopeType(scopeType);
				portletDataContext.setScopeLayoutUuid(scopeLayoutUuid);
			}
		}

		List<Element> portletPreferencesElements = parentElement.elements(
			"portlet-preferences");

		for (Element portletPreferencesElement : portletPreferencesElements) {
			String path = portletPreferencesElement.attributeValue("path");

			if (!portletDataContext.isPathNotProcessed(path)) {
				continue;
			}

			String xml = null;

			Element element = null;

			try {
				xml = portletDataContext.getZipEntryAsString(path);

				Document preferencesDocument = SAXReaderUtil.read(xml);

				element = preferencesDocument.getRootElement();
			}
			catch (DocumentException documentException) {
				ExportImportDocumentException exportImportDocumentException =
					new ExportImportDocumentException(documentException);

				exportImportDocumentException.setPortletId(
					portletDataContext.getPortletId());
				exportImportDocumentException.setType(
					ExportImportDocumentException.PORTLET_PREFERENCES_IMPORT);

				throw exportImportDocumentException;
			}

			long ownerId = GetterUtil.getLong(
				element.attributeValue("owner-id"));

			int ownerType = GetterUtil.getInteger(
				element.attributeValue("owner-type"));

			if ((ownerType == PortletKeys.PREFS_OWNER_TYPE_COMPANY) ||
				!importPortletSetup) {

				continue;
			}

			if ((ownerType == PortletKeys.PREFS_OWNER_TYPE_ARCHIVED) &&
				!importPortletArchivedSetups) {

				continue;
			}

			if ((ownerType == PortletKeys.PREFS_OWNER_TYPE_USER) &&
				(ownerId != PortletKeys.PREFS_OWNER_ID_DEFAULT) &&
				!importPortletUserPreferences) {

				continue;
			}

			long curPlid = plid;
			String curPortletId = portletDataContext.getPortletId();

			if (ownerType == PortletKeys.PREFS_OWNER_TYPE_GROUP) {
				curPlid = PortletKeys.PREFS_PLID_SHARED;
				curPortletId = portletDataContext.getRootPortletId();
				ownerId = portletDataContext.getScopeGroupId();
			}

			long elementPlid = GetterUtil.getLong(
				element.attributeValue("plid"));

			Map<String, String[]> parameterMap =
				portletDataContext.getParameterMap();

			parameterMap.put(
				"portletPreferencePlid",
				new String[] {String.valueOf(elementPlid)});

			portletDataContext.setParameterMap(parameterMap);

			if ((ownerType == PortletKeys.PREFS_OWNER_TYPE_LAYOUT) &&
				(ownerId != PortletKeys.PREFS_OWNER_ID_DEFAULT) &&
				(elementPlid == PortletKeys.PREFS_PLID_SHARED)) {

				curPlid = PortletKeys.PREFS_PLID_SHARED;
				ownerId = portletDataContext.getScopeGroupId();
			}

			if (ownerType == PortletKeys.PREFS_OWNER_TYPE_ARCHIVED) {
				String userUuid = element.attributeValue("archive-user-uuid");

				long userId = portletDataContext.getUserId(userUuid);

				String name = element.attributeValue("archive-name");

				curPortletId = portletDataContext.getRootPortletId();

				PortletItem portletItem =
					_portletItemLocalService.updatePortletItem(
						userId, groupId, name, curPortletId,
						PortletPreferences.class.getName());

				curPlid = LayoutConstants.DEFAULT_PLID;
				ownerId = portletItem.getPortletItemId();
			}

			if (ownerType == PortletKeys.PREFS_OWNER_TYPE_USER) {
				String userUuid = element.attributeValue("user-uuid");

				ownerId = portletDataContext.getUserId(userUuid);
			}

			boolean defaultUser = GetterUtil.getBoolean(
				element.attributeValue("default-user"));

			if (defaultUser) {
				ownerId = _userLocalService.getGuestUserId(companyId);
			}

			jakarta.portlet.PortletPreferences jxPortletPreferences =
				PortletPreferencesFactoryUtil.fromXML(
					companyId, ownerId, ownerType, curPlid, curPortletId, xml);

			Element importDataRootElement =
				portletDataContext.getImportDataRootElement();

			try {
				Element preferenceDataElement =
					portletPreferencesElement.element("preference-data");

				if (preferenceDataElement != null) {
					portletDataContext.setImportDataRootElement(
						preferenceDataElement);
				}

				ExportImportPortletPreferencesProcessor
					exportImportPortletPreferencesProcessor =
						ExportImportPortletPreferencesProcessorRegistryUtil.
							getExportImportPortletPreferencesProcessor(
								PortletIdCodec.decodePortletName(curPortletId));

				if (exportImportPortletPreferencesProcessor != null) {
					List<Capability> importCapabilities =
						exportImportPortletPreferencesProcessor.
							getImportCapabilities();

					if (ListUtil.isNotEmpty(importCapabilities)) {
						for (Capability importCapability : importCapabilities) {
							importCapability.process(
								portletDataContext, jxPortletPreferences);
						}
					}

					exportImportPortletPreferencesProcessor.
						processImportPortletPreferences(
							portletDataContext, jxPortletPreferences);
				}
			}
			finally {
				portletDataContext.setImportDataRootElement(
					importDataRootElement);
			}

			_updatePortletPreferences(
				portletDataContext, ownerId, ownerType, curPlid, curPortletId,
				PortletPreferencesFactoryUtil.toXML(jxPortletPreferences),
				importPortletData);
		}

		if (preserveScopeLayoutId && (layout != null)) {
			jakarta.portlet.PortletPreferences jxPortletPreferences =
				PortletPreferencesFactoryUtil.getLayoutPortletSetup(
					layout, portletDataContext.getPortletId());

			try {
				jxPortletPreferences.setValue("lfrScopeType", scopeType);
				jxPortletPreferences.setValue(
					"lfrScopeLayoutUuid", scopeLayoutUuid);

				jxPortletPreferences.store();
			}
			finally {
				portletDataContext.setScopeType(scopeType);
				portletDataContext.setScopeLayoutUuid(scopeLayoutUuid);
			}
		}
	}

	@Override
	public void importServicePortletPreferences(
			PortletDataContext portletDataContext, Element serviceElement)
		throws PortalException {

		long ownerId = GetterUtil.getLong(
			serviceElement.attributeValue("owner-id"));
		int ownerType = GetterUtil.getInteger(
			serviceElement.attributeValue("owner-type"));
		String serviceName = serviceElement.attributeValue("service-name");

		if (ownerType == PortletKeys.PREFS_OWNER_TYPE_GROUP) {
			ownerId = portletDataContext.getGroupId();
		}
		else if (ownerType == PortletKeys.PREFS_OWNER_TYPE_COMPANY) {
			ownerId = portletDataContext.getCompanyId();
		}

		PortletPreferences portletPreferences = getPortletPreferences(
			portletDataContext.getCompanyId(), ownerId, ownerType,
			LayoutConstants.DEFAULT_PLID, serviceName);

		for (Attribute attribute : serviceElement.attributes()) {
			serviceElement.remove(attribute);
		}

		_portletPreferencesLocalService.updatePreferences(
			portletPreferences.getOwnerId(), portletPreferences.getOwnerType(),
			portletPreferences.getPlid(), portletPreferences.getPortletId(),
			serviceElement.asXML());
	}

	@Override
	public void readExpandoTables(PortletDataContext portletDataContext)
		throws Exception {

		String xml = portletDataContext.getZipEntryAsString(
			ExportImportPathUtil.getSourceRootPath(portletDataContext) +
				"/expando-tables.xml");

		if (xml == null) {
			return;
		}

		Document document = SAXReaderUtil.read(xml);

		Element rootElement = document.getRootElement();

		List<Element> expandoTableElements = rootElement.elements(
			"expando-table");

		for (Element expandoTableElement : expandoTableElements) {
			String className = expandoTableElement.attributeValue("class-name");

			ExpandoTable expandoTable = null;

			try {
				expandoTable = _expandoTableLocalService.getDefaultTable(
					portletDataContext.getCompanyId(), className);
			}
			catch (NoSuchTableException noSuchTableException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchTableException);
				}

				expandoTable = _expandoTableLocalService.addDefaultTable(
					portletDataContext.getCompanyId(), className);
			}

			List<Element> expandoColumnElements = expandoTableElement.elements(
				"expando-column");

			for (Element expandoColumnElement : expandoColumnElements) {
				long columnId = GetterUtil.getLong(
					expandoColumnElement.attributeValue("column-id"));
				String name = expandoColumnElement.attributeValue("name");
				int type = GetterUtil.getInteger(
					expandoColumnElement.attributeValue("type"));
				String defaultData = expandoColumnElement.elementText(
					"default-data");
				String typeSettings = expandoColumnElement.elementText(
					"type-settings");

				Serializable defaultDataObject =
					ExpandoConverterUtil.getAttributeFromString(
						type, defaultData);

				ExpandoColumn expandoColumn =
					_expandoColumnLocalService.fetchColumn(
						expandoTable.getTableId(), name);

				if (expandoColumn != null) {
					_expandoColumnLocalService.updateColumn(
						expandoColumn.getColumnId(), name, type,
						defaultDataObject);
				}
				else {
					expandoColumn = _expandoColumnLocalService.addColumn(
						expandoTable.getTableId(), name, type,
						defaultDataObject);
				}

				_expandoColumnLocalService.updateTypeSettings(
					expandoColumn.getColumnId(), typeSettings);

				portletDataContext.importPermissions(
					ExpandoColumn.class, columnId, expandoColumn.getColumnId());
			}
		}
	}

	@Override
	public void readLocks(PortletDataContext portletDataContext)
		throws Exception {

		String xml = portletDataContext.getZipEntryAsString(
			ExportImportPathUtil.getSourceRootPath(portletDataContext) +
				"/locks.xml");

		if (xml == null) {
			return;
		}

		Document document = SAXReaderUtil.read(xml);

		Element rootElement = document.getRootElement();

		List<Element> assetElements = rootElement.elements("asset");

		for (Element assetElement : assetElements) {
			String path = assetElement.attributeValue("path");

			Lock lock = (Lock)portletDataContext.getZipEntryAsObject(path);

			if (lock != null) {
				String className = assetElement.attributeValue("class-name");
				String key = assetElement.attributeValue("key");

				portletDataContext.addLocks(className, key, lock);
			}
		}
	}

	@Override
	public void resetPortletScope(
		PortletDataContext portletDataContext, long groupId) {

		portletDataContext.setScopeGroupId(groupId);
		portletDataContext.setScopeLayoutUuid(StringPool.BLANK);
		portletDataContext.setScopeType(StringPool.BLANK);
	}

	@Override
	public MissingReferences validateFile(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws Exception {

		ZipReader zipReader = null;

		try {
			ExportImportThreadLocal.setPortletValidationInProcess(true);

			Map<String, Serializable> settingsMap =
				exportImportConfiguration.getSettingsMap();

			String portletId = MapUtil.getString(settingsMap, "portletId");
			long targetGroupId = MapUtil.getLong(settingsMap, "targetGroupId");

			long targetPlid = MapUtil.getLong(settingsMap, "targetPlid");

			Layout layout = _layoutLocalService.getLayout(targetPlid);

			zipReader = _zipReaderFactory.getZipReader(file);

			validateFile(
				layout.getCompanyId(), targetGroupId, portletId, zipReader);

			MissingReferences missingReferences =
				_exportImportHelper.validateMissingReferences(
					getPortletDataContext(exportImportConfiguration, file));

			Map<String, MissingReference> dependencyMissingReferences =
				missingReferences.getDependencyMissingReferences();

			if (!dependencyMissingReferences.isEmpty()) {
				if (isValidateMissingReferences()) {
					throw new MissingReferenceException(missingReferences);
				}

				if (_log.isWarnEnabled()) {
					try {
						JSONArray errorMessagesJSONArray =
							_staging.getErrorMessagesJSONArray(
								LocaleUtil.getDefault(),
								dependencyMissingReferences);

						_log.warn(
							"Missing reference validation errors ignored: " +
								errorMessagesJSONArray);
					}
					catch (Exception exception) {
						_log.warn(exception);
					}
				}
			}

			return missingReferences;
		}
		finally {
			ExportImportThreadLocal.setPortletValidationInProcess(false);

			if (zipReader != null) {
				zipReader.close();
			}
		}
	}

	protected String deletePortletData(
			PortletDataContext portletDataContext,
			jakarta.portlet.PortletPreferences portletPreferences)
		throws Exception {

		Group group = _groupLocalService.getGroup(
			portletDataContext.getGroupId());

		if (!group.isStagedPortlet(portletDataContext.getPortletId())) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Do not delete portlet data for portlet " +
						portletDataContext.getPortletId() +
							" because the portlet is not staged");
			}

			return null;
		}

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(
				portletDataContext.getCompanyId(),
				portletDataContext.getPortletId());

		if (portletDataHandler == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Do not delete portlet data for portlet ",
						portletDataContext.getPortletId(),
						" because the portlet does not have a ",
						"PortletDataHandler"));
			}

			return null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Deleting data for portlet " +
					portletDataContext.getPortletId());
		}

		try {
			portletPreferences = portletDataHandler.deleteData(
				portletDataContext, portletDataContext.getPortletId(),
				portletPreferences);
		}
		finally {
			portletDataContext.setGroupId(portletDataContext.getScopeGroupId());
		}

		if (portletPreferences == null) {
			return null;
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	protected PortletDataContext getPortletDataContext(
			ExportImportConfiguration exportImportConfiguration, File file)
		throws PortalException {

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");
		String portletId = MapUtil.getString(settingsMap, "portletId");
		long targetPlid = MapUtil.getLong(settingsMap, "targetPlid");
		long targetGroupId = MapUtil.getLong(settingsMap, "targetGroupId");
		long userId = MapUtil.getLong(settingsMap, "userId");

		Layout layout = _layoutLocalService.getLayout(targetPlid);

		String userIdStrategyString = MapUtil.getString(
			parameterMap, PortletDataHandlerKeys.USER_ID_STRATEGY);

		PortletDataContext portletDataContext =
			_portletDataContextFactory.createImportPortletDataContext(
				layout.getCompanyId(), targetGroupId, parameterMap,
				_exportImportHelper.getUserIdStrategy(
					userId, userIdStrategyString),
				_zipReaderFactory.getZipReader(file));

		portletDataContext.setExportImportProcessId(
			String.valueOf(
				exportImportConfiguration.getExportImportConfigurationId()));
		portletDataContext.setPlid(targetPlid);
		portletDataContext.setPortletId(portletId);
		portletDataContext.setPrivateLayout(layout.isPrivateLayout());
		portletDataContext.setType("portlet");

		return portletDataContext;
	}

	protected PortletPreferences getPortletPreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String serviceName)
		throws PortalException {

		PortletPreferences portletPreferences = null;

		try {
			if ((ownerType == PortletKeys.PREFS_OWNER_TYPE_ARCHIVED) ||
				(ownerType == PortletKeys.PREFS_OWNER_TYPE_COMPANY) ||
				(ownerType == PortletKeys.PREFS_OWNER_TYPE_GROUP)) {

				portletPreferences =
					_portletPreferencesLocalService.getPortletPreferences(
						ownerId, ownerType, LayoutConstants.DEFAULT_PLID,
						serviceName);
			}
			else {
				portletPreferences =
					_portletPreferencesLocalService.getPortletPreferences(
						ownerId, ownerType, plid, serviceName);
			}
		}
		catch (NoSuchPortletPreferencesException
					noSuchPortletPreferencesException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchPortletPreferencesException);
			}

			portletPreferences =
				_portletPreferencesLocalService.addPortletPreferences(
					companyId, ownerId, ownerType, plid, serviceName, null,
					null);
		}

		return portletPreferences;
	}

	protected int getProcessFlag() {
		if (ExportImportThreadLocal.isPortletStagingInProcess()) {
			return ExportImportLifecycleConstants.
				PROCESS_FLAG_PORTLET_STAGING_IN_PROCESS;
		}

		return ExportImportLifecycleConstants.
			PROCESS_FLAG_PORTLET_IMPORT_IN_PROCESS;
	}

	protected boolean isValidateMissingReferences() {
		try {
			ExportImportServiceConfiguration exportImportServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ExportImportServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return exportImportServiceConfiguration.validateMissingReferences();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return true;
	}

	protected void populateDeletionStagedModelTypes(
			PortletDataContext portletDataContext)
		throws Exception {

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(
				portletDataContext.getCompanyId(),
				portletDataContext.getPortletId());

		if (portletDataHandler == null) {
			return;
		}

		portletDataContext.addDeletionSystemEventStagedModelTypes(
			portletDataHandler.getDeletionSystemEventStagedModelTypes());
		portletDataContext.addDeletionSystemEventStagedModelTypes(
			new StagedModelType(StagedAssetLink.class));
	}

	protected void validateFile(
			long companyId, long groupId, String portletId, ZipReader zipReader)
		throws Exception {

		// XML

		String xml = zipReader.getEntryAsString("/manifest.xml");

		if (xml == null) {
			throw new LARFileException(LARFileException.TYPE_MISSING_MANIFEST);
		}

		Element rootElement = null;

		try {
			Document document = SAXReaderUtil.read(xml);

			rootElement = document.getRootElement();
		}
		catch (Exception exception) {
			throw new LARFileException(
				LARFileException.TYPE_INVALID_MANIFEST, exception);
		}

		// Build compatibility

		Element headerElement = rootElement.element("header");

		int importBuildNumber = GetterUtil.getInteger(
			headerElement.attributeValue("build-number"));

		if (importBuildNumber < ReleaseInfo.RELEASE_7_0_0_BUILD_NUMBER) {
			throw new LayoutImportException(
				LayoutImportException.TYPE_WRONG_BUILD_NUMBER,
				new Object[] {importBuildNumber, ReleaseInfo.getBuildNumber()});
		}

		BiPredicate<Version, Version> majorVersionBiPredicate =
			(currentVersion, importVersion) -> Objects.equals(
				currentVersion.getMajor(), importVersion.getMajor());

		BiPredicate<Version, Version> minorVersionBiPredicate =
			(currentVersion, importVersion) -> {
				int currentMinorVersion = GetterUtil.getInteger(
					currentVersion.getMinor(), -1);
				int importedMinorVersion = GetterUtil.getInteger(
					importVersion.getMinor(), -1);

				if (((currentMinorVersion == -1) &&
					 (importedMinorVersion == -1)) ||
					(currentMinorVersion < importedMinorVersion)) {

					return false;
				}

				return true;
			};

		BiPredicate<Version, Version> manifestVersionBiPredicate =
			(currentVersion, importVersion) -> {
				BiPredicate<Version, Version> versionBiPredicate =
					majorVersionBiPredicate.and(minorVersionBiPredicate);

				return versionBiPredicate.test(currentVersion, importVersion);
			};

		String importSchemaVersion = GetterUtil.getString(
			headerElement.attributeValue("schema-version"), "1.0.0");

		if (!manifestVersionBiPredicate.test(
				Version.getInstance(
					ExportImportConstants.EXPORT_IMPORT_SCHEMA_VERSION),
				Version.getInstance(importSchemaVersion))) {

			throw new LayoutImportException(
				LayoutImportException.TYPE_WRONG_LAR_SCHEMA_VERSION,
				new Object[] {
					importSchemaVersion,
					ExportImportConstants.EXPORT_IMPORT_SCHEMA_VERSION
				});
		}

		// Type

		String larType = headerElement.attributeValue("type");

		if (!larType.equals("portlet")) {
			throw new LARTypeException(larType, new String[] {"portlet"});
		}

		// Portlet compatibility

		String rootPortletId = headerElement.attributeValue("root-portlet-id");

		String expectedRootPortletId = PortletIdCodec.decodePortletName(
			portletId);

		if (!expectedRootPortletId.equals(rootPortletId)) {
			throw new PortletIdException(expectedRootPortletId);
		}

		Element portletElement = rootElement.element("portlet");

		String schemaVersion = GetterUtil.getString(
			portletElement.attributeValue("schema-version"), "1.0.0");

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(companyId, portletId);

		if (!portletDataHandler.validateSchemaVersion(schemaVersion)) {
			throw new LayoutImportException(
				LayoutImportException.TYPE_WRONG_PORTLET_SCHEMA_VERSION,
				new Object[] {
					schemaVersion, portletId,
					portletDataHandler.getSchemaVersion()
				});
		}

		// Available locales

		if (portletDataHandler.isDataLocalized()) {
			String[] sourceAvailableLanguageIds = StringUtil.split(
				headerElement.attributeValue("available-locales"));

			for (String sourceAvailableLanguageId :
					sourceAvailableLanguageIds) {

				if (!_language.isAvailableLocale(
						_portal.getSiteGroupId(groupId),
						sourceAvailableLanguageId)) {

					LocaleException localeException = new LocaleException(
						LocaleException.TYPE_EXPORT_IMPORT);

					localeException.setSourceAvailableLanguageIds(
						Arrays.asList(sourceAvailableLanguageIds));
					localeException.setTargetAvailableLocales(
						_language.getAvailableLocales(
							_portal.getSiteGroupId(groupId)));

					throw localeException;
				}
			}
		}
	}

	private void _importPortletInfo(
			PortletDataContext portletDataContext, long userId)
		throws Exception {

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();

			serviceContext.setCompanyId(portletDataContext.getCompanyId());
			serviceContext.setSignedIn(false);
			serviceContext.setUserId(userId);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);
		}

		// LAR validation

		validateFile(
			portletDataContext.getCompanyId(), portletDataContext.getGroupId(),
			portletDataContext.getPortletId(),
			portletDataContext.getZipReader());

		// Source and target group id

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		groupIds.put(
			portletDataContext.getSourceGroupId(),
			portletDataContext.getGroupId());

		// Manifest

		ManifestSummary manifestSummary =
			_exportImportHelper.getManifestSummary(portletDataContext);

		if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
			_portletDataHandlerStatusMessageSender.sendStatusMessage(
				"portlet", portletDataContext.getPortletId(), manifestSummary);
		}

		portletDataContext.setManifestSummary(manifestSummary);

		// Read expando tables, locks and permissions to make them
		// available to the data handlers through the portlet data context

		Element rootElement = portletDataContext.getImportDataRootElement();

		Element portletElement = null;

		try {
			portletElement = rootElement.element("portlet");

			Document portletDocument = SAXReaderUtil.read(
				portletDataContext.getZipEntryAsString(
					portletElement.attributeValue("path")));

			portletElement = portletDocument.getRootElement();
		}
		catch (DocumentException documentException) {
			ExportImportDocumentException exportImportDocumentException =
				new ExportImportDocumentException(documentException);

			exportImportDocumentException.setPortletId(
				portletDataContext.getPortletId());
			exportImportDocumentException.setType(
				ExportImportDocumentException.PORTLET_DATA_IMPORT);

			throw exportImportDocumentException;
		}

		boolean importPermissions = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PERMISSIONS);

		_permissionImporter.clearCache();

		if (importPermissions) {
			_permissionImporter.checkRoles(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), userId, portletElement);

			_permissionImporter.readPortletDataPermissions(portletDataContext);
		}

		String layoutsImportMode = MapUtil.getString(
			parameterMap, PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE);

		if (!layoutsImportMode.equals(
				PortletDataHandlerKeys.
					LAYOUTS_IMPORT_MODE_CREATED_FROM_PROTOTYPE)) {

			readExpandoTables(portletDataContext);
		}

		readLocks(portletDataContext);

		Element portletDataElement = portletElement.element("portlet-data");

		Map<String, Boolean> importPortletControlsMap =
			_exportImportHelper.getImportPortletControlsMap(
				portletDataContext.getCompanyId(),
				portletDataContext.getPortletId(), parameterMap,
				portletDataElement, manifestSummary);

		Layout layout = _layoutLocalService.getLayout(
			portletDataContext.getPlid());

		try {

			// Portlet preferences

			importPortletPreferences(
				portletDataContext, layout.getCompanyId(),
				portletDataContext.getGroupId(), layout, portletElement, true,
				importPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS),
				importPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_DATA),
				importPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_SETUP),
				importPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_USER_PREFERENCES));

			// Portlet data

			if (importPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_DATA)) {

				if (_log.isDebugEnabled()) {
					_log.debug("Importing portlet data");
				}

				importPortletData(portletDataContext, portletDataElement);
			}
		}
		finally {
			resetPortletScope(
				portletDataContext, portletDataContext.getGroupId());
		}

		// Portlet permissions

		if (importPermissions) {
			if (_log.isDebugEnabled()) {
				_log.debug("Importing portlet permissions");
			}

			PortletDataHandler portletDataHandler =
				_portletDataHandlerProvider.provide(
					portletDataContext.getCompanyId(),
					portletDataContext.getPortletId());

			if ((portletDataHandler != null) &&
				Validator.isNotNull(portletDataHandler.getResourceName())) {

				portletDataContext.importPortletPermissions(
					portletDataHandler.getResourceName());
			}

			_permissionImporter.importPortletPermissions(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), userId, layout, portletElement,
				portletDataContext.getPortletId());

			if (userId > 0) {
				Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
					User.class);

				indexer.reindex(_userLocalService.fetchUser(userId));
			}
		}

		// Asset links

		if (_log.isDebugEnabled()) {
			_log.debug("Importing asset links");
		}

		importAssetLinks(portletDataContext);

		// Deletion system events

		_deletionSystemEventImporter.importDeletionSystemEvents(
			portletDataContext);

		if (_log.isInfoEnabled()) {
			_log.info("Importing portlet takes " + stopWatch.getTime() + " ms");
		}

		// Service portlet preferences

		boolean importPortletSetup = importPortletControlsMap.get(
			PortletDataHandlerKeys.PORTLET_SETUP);

		if (importPortletSetup) {
			try {
				List<Element> serviceElements = rootElement.elements("service");

				for (Element serviceElement : serviceElements) {
					Document serviceDocument = SAXReaderUtil.read(
						portletDataContext.getZipEntryAsString(
							serviceElement.attributeValue("path")));

					importServicePortletPreferences(
						portletDataContext, serviceDocument.getRootElement());
				}
			}
			catch (DocumentException documentException) {
				ExportImportDocumentException exportImportDocumentException =
					new ExportImportDocumentException(documentException);

				exportImportDocumentException.setPortletId(
					portletDataContext.getPortletId());
				exportImportDocumentException.setType(
					ExportImportDocumentException.PORTLET_SERVICE_IMPORT);

				throw exportImportDocumentException;
			}
			catch (PortalException portalException) {
				throw new PortletDataException(
					"Unable to import service preferences for portlet " +
						portletDataContext.getPortletId(),
					portalException);
			}
		}

		ZipReader zipReader = portletDataContext.getZipReader();

		zipReader.close();
	}

	private void _updatePortletPreferences(
			PortletDataContext portletDataContext, long ownerId, int ownerType,
			long plid, String portletId, String xml, boolean importData)
		throws Exception {

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(
				portletDataContext.getCompanyId(), portletId);

		// Current portlet preferences

		jakarta.portlet.PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				portletDataContext.getCompanyId(), ownerId, ownerType, plid,
				portletId);

		// New portlet preferences

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				portletDataContext.getCompanyId(), ownerId, ownerType, plid,
				portletId, xml);

		if (importData || !MergeLayoutPrototypesThreadLocal.isInProgress()) {
			String currentLastPublishDate = portletPreferences.getValue(
				"last-publish-date", null);
			String newLastPublishDate = jxPortletPreferences.getValue(
				"last-publish-date", null);

			if (Validator.isNotNull(currentLastPublishDate)) {
				jxPortletPreferences.setValue(
					"last-publish-date", currentLastPublishDate);
			}
			else if (Validator.isNotNull(newLastPublishDate)) {
				jxPortletPreferences.reset("last-publish-date");
			}

			_portletPreferencesLocalService.updatePreferences(
				ownerId, ownerType, plid, portletId,
				PortletPreferencesFactoryUtil.toXML(jxPortletPreferences));

			return;
		}

		// Portlet preferences will be updated only when importing data

		String[] dataPortletPreferences =
			portletDataHandler.getDataPortletPreferences();

		Enumeration<String> enumeration = jxPortletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			String scopeType = portletDataContext.getScopeType();

			if (!ArrayUtil.contains(dataPortletPreferences, name) ||
				(Validator.isNull(portletDataContext.getScopeLayoutUuid()) &&
				 scopeType.equals("company"))) {

				portletPreferences.setValues(
					name, jxPortletPreferences.getValues(name, null));
			}
		}

		_portletPreferencesLocalService.updatePreferences(
			ownerId, ownerType, plid, portletId, portletPreferences);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletImportControllerImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DeletionSystemEventImporter _deletionSystemEventImporter;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLifecycleManager _exportImportLifecycleManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PermissionImporter _permissionImporter;

	@Reference
	private Portal _portal;

	@Reference
	private PortletDataContextFactory _portletDataContextFactory;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Reference
	private PortletDataHandlerStatusMessageSender
		_portletDataHandlerStatusMessageSender;

	@Reference
	private PortletItemLocalService _portletItemLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private Staging _staging;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

}