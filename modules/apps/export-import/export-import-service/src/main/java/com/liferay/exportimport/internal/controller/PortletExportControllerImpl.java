/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.controller;

import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.model.adapter.StagedAssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.exportimport.changeset.constants.ChangesetPortletKeys;
import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.controller.PortletExportController;
import com.liferay.exportimport.internal.lar.PermissionExporter;
import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.exception.ExportImportIOException;
import com.liferay.exportimport.kernel.exception.LayoutImportException;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportProcessCallbackRegistry;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerStatusMessageSender;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.lar.DeletionSystemEventExporter;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorRegistryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

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
	service = {ExportImportController.class, PortletExportController.class}
)
public class PortletExportControllerImpl implements PortletExportController {

	@Override
	public File export(ExportImportConfiguration exportImportConfiguration)
		throws Exception {

		PortletDataContext portletDataContext = null;

		try {
			ExportImportThreadLocal.setPortletExportInProcess(true);

			portletDataContext = getPortletDataContext(
				exportImportConfiguration);

			_exportImportLifecycleManager.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_PORTLET_EXPORT_STARTED,
				getProcessFlag(),
				String.valueOf(
					exportImportConfiguration.getExportImportConfigurationId()),
				_portletDataContextFactory.clonePortletDataContext(
					portletDataContext));

			File file = doExport(portletDataContext);

			ExportImportThreadLocal.setPortletExportInProcess(false);

			_exportImportLifecycleManager.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_PORTLET_EXPORT_SUCCEEDED,
				getProcessFlag(),
				String.valueOf(
					exportImportConfiguration.getExportImportConfigurationId()),
				_portletDataContextFactory.clonePortletDataContext(
					portletDataContext));

			return file;
		}
		catch (Throwable throwable) {
			ExportImportThreadLocal.setPortletExportInProcess(false);

			_exportImportLifecycleManager.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_PORTLET_EXPORT_FAILED,
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
	public void exportAssetLinks(PortletDataContext portletDataContext)
		throws Exception {

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("links");

		Element exportDataRootElement =
			portletDataContext.getExportDataRootElement();

		try {
			portletDataContext.setExportDataRootElement(rootElement);

			List<AssetLink> assetLinks = new ArrayList<>();

			if (_isIncludeAllAssetLinks()) {
				assetLinks.addAll(
					_assetLinkLocalService.getLinks(
						portletDataContext.getGroupId(),
						portletDataContext.getStartDate(),
						portletDataContext.getEndDate(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS));
			}

			Set<Long> assetLinkIds = portletDataContext.getAssetLinkIds();

			for (Long assetLinkId : assetLinkIds) {
				AssetLink assetLink = _assetLinkLocalService.fetchAssetLink(
					assetLinkId);

				if ((assetLink != null) && !assetLinks.contains(assetLink)) {
					assetLinks.add(assetLink);
				}
			}

			for (AssetLink assetLink : assetLinks) {
				StagedAssetLink stagedAssetLink = ModelAdapterUtil.adapt(
					assetLink, AssetLink.class, StagedAssetLink.class);

				portletDataContext.addClassedModel(
					portletDataContext.getExportDataElement(stagedAssetLink),
					ExportImportPathUtil.getModelPath(stagedAssetLink),
					stagedAssetLink);
			}
		}
		finally {
			portletDataContext.setExportDataRootElement(exportDataRootElement);
		}

		portletDataContext.addZipEntry(
			ExportImportPathUtil.getRootPath(portletDataContext) + "/links.xml",
			document.formattedString());
	}

	@Override
	public void exportLocks(PortletDataContext portletDataContext)
		throws Exception {

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("locks");

		Map<String, Lock> locksMap = portletDataContext.getLocks();

		for (Map.Entry<String, Lock> entry : locksMap.entrySet()) {
			Lock lock = entry.getValue();

			String entryKey = entry.getKey();

			int pos = entryKey.indexOf(CharPool.POUND);

			String className = entryKey.substring(0, pos);
			String key = entryKey.substring(pos + 1);

			String path = _getLockPath(
				portletDataContext, className, key, lock);

			Element assetElement = rootElement.addElement("asset");

			assetElement.addAttribute("path", path);
			assetElement.addAttribute("class-name", className);
			assetElement.addAttribute("key", key);

			portletDataContext.addZipEntry(path, lock);
		}

		portletDataContext.addZipEntry(
			ExportImportPathUtil.getRootPath(portletDataContext) + "/locks.xml",
			document.formattedString());
	}

	@Override
	public void exportPortlet(
			PortletDataContext portletDataContext, long plid,
			Element parentElement, boolean exportPermissions,
			boolean exportPortletArchivedSetups, boolean exportPortletData,
			boolean exportPortletSetup, boolean exportPortletUserPreferences)
		throws Exception {

		long layoutId = LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout != null) {
			layoutId = layout.getLayoutId();
		}

		Portlet portlet = _portletLocalService.getPortletById(
			portletDataContext.getCompanyId(),
			portletDataContext.getPortletId());

		if ((portlet == null) || portlet.isUndeployedPortlet()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Do not export portlet " + portlet.getPortletId() +
						" because the portlet is not deployed");
			}

			return;
		}

		if (!portlet.isInstanceable() &&
			!portlet.isPreferencesUniquePerLayout() &&
			portletDataContext.hasNotUniquePerLayout(portlet.getPortletId())) {

			return;
		}

		PortletDataHandler portletDataHandler =
			portlet.getPortletDataHandlerInstance();

		if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
			PortletDataContext clonedPortletDataContext =
				_portletDataContextFactory.clonePortletDataContext(
					portletDataContext);

			ManifestSummary manifestSummary =
				clonedPortletDataContext.getManifestSummary();

			manifestSummary.resetCounters();

			portletDataHandler.prepareManifestSummary(clonedPortletDataContext);

			_portletDataHandlerStatusMessageSender.sendStatusMessage(
				"portlet", portlet.getPortletId(), manifestSummary);
		}

		Document document = SAXReaderUtil.createDocument();

		Element portletElement = document.addElement("portlet");

		portletElement.addAttribute("portlet-id", portlet.getPortletId());
		portletElement.addAttribute(
			"root-portlet-id", portlet.getRootPortletId());
		portletElement.addAttribute(
			"scope-group-id",
			String.valueOf(portletDataContext.getScopeGroupId()));
		portletElement.addAttribute(
			"scope-layout-type", portletDataContext.getScopeType());
		portletElement.addAttribute(
			"scope-layout-uuid", portletDataContext.getScopeLayoutUuid());
		portletElement.addAttribute(
			"private-layout",
			String.valueOf(portletDataContext.isPrivateLayout()));

		String path = StringBundler.concat(
			ExportImportPathUtil.getPortletPath(portletDataContext),
			StringPool.SLASH, plid, "/portlet.xml");

		portletElement.addAttribute("self-path", path);

		// Data

		if (exportPortletData) {
			jakarta.portlet.PortletPreferences jxPortletPreferences = null;

			if (ExportImportThreadLocal.isInitialLayoutStagingInProcess()) {
				if (layout != null) {
					Group liveGroup = layout.getGroup();

					if (liveGroup.isStaged()) {
						Group stagingGroup = liveGroup.getStagingGroup();

						layout.setGroupId(stagingGroup.getGroupId());

						jxPortletPreferences =
							PortletPreferencesFactoryUtil.getStrictPortletSetup(
								layout, portlet.getPortletId());
					}

					layout.setGroupId(liveGroup.getGroupId());
				}
				else {
					Group liveGroup = _groupLocalService.getGroup(
						portletDataContext.getGroupId());

					Group stagingGroup = liveGroup.getStagingGroup();

					jxPortletPreferences =
						PortletPreferencesFactoryUtil.getStrictPortletSetup(
							portletDataContext.getCompanyId(),
							stagingGroup.getGroupId(), portlet.getPortletId());
				}
			}
			else {
				if (layout != null) {
					jxPortletPreferences =
						PortletPreferencesFactoryUtil.getStrictPortletSetup(
							layout, portlet.getPortletId());
				}
				else {
					jxPortletPreferences =
						PortletPreferencesFactoryUtil.getStrictPortletSetup(
							portletDataContext.getCompanyId(),
							portletDataContext.getGroupId(),
							portlet.getPortletId());
				}
			}

			if (!portlet.isPreferencesUniquePerLayout()) {
				String dataKey = StringBundler.concat(
					portlet.getPortletId(), StringPool.AT,
					portletDataContext.getScopeType(), StringPool.AT,
					portletDataContext.getScopeLayoutUuid());

				if (!portletDataContext.hasNotUniquePerLayout(dataKey)) {
					portletDataContext.putNotUniquePerLayout(dataKey);

					exportPortletData(
						portletDataContext, portlet, layout,
						jxPortletPreferences, portletElement);
				}
			}
			else {
				exportPortletData(
					portletDataContext, portlet, layout, jxPortletPreferences,
					portletElement);
			}
		}

		// Portlet preferences

		if (exportPortletSetup) {

			// Company

			_exportPortletPreferences(
				portletDataContext, portletDataContext.getCompanyId(),
				PortletKeys.PREFS_OWNER_TYPE_COMPANY, false, layout, plid,
				portlet.getRootPortletId(), portletElement);

			// Group

			_exportPortletPreferences(
				portletDataContext, portletDataContext.getScopeGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_GROUP, false, layout,
				PortletKeys.PREFS_PLID_SHARED, portlet.getRootPortletId(),
				portletElement);

			// Group embedded portlets

			_exportPortletPreferences(
				portletDataContext, portletDataContext.getScopeGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, false, layout,
				PortletKeys.PREFS_PLID_SHARED, portlet.getPortletId(),
				portletElement);

			// Layout

			_exportPortletPreferences(
				portletDataContext, PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, false, layout, plid,
				portlet.getPortletId(), portletElement);
		}

		// Portlet user preferences

		if (exportPortletUserPreferences) {
			List<PortletPreferences> portletPreferencesList =
				_portletPreferencesLocalService.getPortletPreferences(
					PortletKeys.PREFS_OWNER_TYPE_USER, plid,
					portlet.getPortletId());

			for (PortletPreferences portletPreferences :
					portletPreferencesList) {

				boolean defaultUser = false;

				if (portletPreferences.getOwnerId() ==
						PortletKeys.PREFS_OWNER_ID_DEFAULT) {

					defaultUser = true;
				}

				_exportPortletPreferences(
					portletDataContext, portletPreferences.getOwnerId(),
					PortletKeys.PREFS_OWNER_TYPE_USER, defaultUser, layout,
					plid, portlet.getPortletId(), portletElement);
			}

			try {
				PortletPreferences groupPortletPreferences =
					_portletPreferencesLocalService.getPortletPreferences(
						portletDataContext.getScopeGroupId(),
						PortletKeys.PREFS_OWNER_TYPE_GROUP,
						PortletKeys.PREFS_PLID_SHARED,
						portlet.getRootPortletId());

				_exportPortletPreference(
					portletDataContext, portletDataContext.getScopeGroupId(),
					PortletKeys.PREFS_OWNER_TYPE_GROUP, false,
					groupPortletPreferences, portlet.getRootPortletId(),
					PortletKeys.PREFS_PLID_SHARED, portletElement);
			}
			catch (NoSuchPortletPreferencesException
						noSuchPortletPreferencesException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchPortletPreferencesException);
				}
			}
		}

		// Archived setups

		if (exportPortletArchivedSetups) {
			List<PortletItem> portletItems =
				_portletItemLocalService.getPortletItems(
					portletDataContext.getGroupId(), portlet.getRootPortletId(),
					PortletPreferences.class.getName());

			for (PortletItem portletItem : portletItems) {
				_exportPortletPreferences(
					portletDataContext, portletItem.getPortletItemId(),
					PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, false, null, plid,
					portletItem.getPortletId(), portletElement);
			}
		}

		// Permissions

		if (exportPermissions) {
			if (Validator.isNotNull(portletDataHandler.getResourceName())) {
				portletDataContext.addPortletPermissions(
					portletDataHandler.getResourceName());
			}

			_permissionExporter.exportPortletPermissions(
				portletDataContext, portlet.getPortletId(), layout,
				portletElement);
		}

		// Zip

		Element element = parentElement.addElement("portlet");

		element.addAttribute("display-name", portlet.getDisplayName());
		element.addAttribute("portlet-id", portlet.getPortletId());
		element.addAttribute("layout-id", String.valueOf(layoutId));
		element.addAttribute("path", path);

		StringBundler configurationOptionsSB = new StringBundler(6);

		if (exportPortletSetup) {
			configurationOptionsSB.append("setup");
			configurationOptionsSB.append(StringPool.COMMA);
		}

		if (exportPortletArchivedSetups) {
			configurationOptionsSB.append("archived-setups");
			configurationOptionsSB.append(StringPool.COMMA);
		}

		if (exportPortletUserPreferences) {
			configurationOptionsSB.append("user-preferences");
			configurationOptionsSB.append(StringPool.COMMA);
		}

		if (configurationOptionsSB.index() > 0) {
			configurationOptionsSB.setIndex(configurationOptionsSB.index() - 1);
		}

		element.addAttribute(
			"portlet-configuration", configurationOptionsSB.toString());

		element.addAttribute("portlet-data", String.valueOf(exportPortletData));
		element.addAttribute(
			"schema-version", portletDataHandler.getSchemaVersion());

		if (portletDataContext.isValidateExistingDataHandler()) {
			element.addAttribute("validate-existing-data-handler", "true");
		}

		try {
			portletDataContext.addZipEntry(path, document.formattedString());
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}
		}
	}

	public void exportPortletData(
			PortletDataContext portletDataContext, Portlet portlet,
			Layout layout,
			jakarta.portlet.PortletPreferences jxPortletPreferences,
			Element parentElement)
		throws Exception {

		if (portlet == null) {
			return;
		}

		PortletDataHandler portletDataHandler = _getPortletDataHandler(
			portletDataContext, portlet);

		if ((portletDataHandler == null) ||
			portletDataHandler.isDataPortletInstanceLevel()) {

			return;
		}

		Group group = _groupLocalService.getGroup(
			portletDataContext.getGroupId());

		long plid = LayoutConstants.DEFAULT_PLID;

		if (layout != null) {
			group = layout.getGroup();
			plid = layout.getPlid();
		}

		if (group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		String portletId = portlet.getPortletId();

		if (ExportImportThreadLocal.isStagingInProcess() &&
			!group.isStagedPortlet(portletId)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Not exporting data for " + portletId +
						" because it is configured not to be staged");
			}

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Exporting data for " + portletId);
		}

		String path = ExportImportPathUtil.getPortletDataPath(
			portletDataContext);

		if (portletDataContext.hasPrimaryKey(String.class, path)) {
			return;
		}

		Date originalStartDate = portletDataContext.getStartDate();

		Date portletLastPublishDate = ExportImportDateUtil.getLastPublishDate(
			portletDataContext, jxPortletPreferences);

		portletDataContext.setStartDate(portletLastPublishDate);

		long groupId = portletDataContext.getGroupId();

		portletDataContext.setGroupId(portletDataContext.getScopeGroupId());

		portletDataContext.clearScopedPrimaryKeys();

		String data = null;

		try {
			data = portletDataHandler.exportData(
				portletDataContext, portletId, jxPortletPreferences);
		}
		finally {
			portletDataContext.clearScopedPrimaryKeys();

			portletDataContext.setGroupId(groupId);
			portletDataContext.setStartDate(originalStartDate);
		}

		if (Validator.isNull(data)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Not exporting data for " + portletId +
						" because null data was returned");
			}

			return;
		}

		Element portletDataElement = parentElement.addElement("portlet-data");

		portletDataElement.addAttribute("path", path);

		portletDataContext.addZipEntry(path, data);

		boolean updateLastPublishDate = MapUtil.getBoolean(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE);

		if (ExportImportThreadLocal.isStagingInProcess() &&
			updateLastPublishDate) {

			DateRange adjustedDateRange = new DateRange(
				portletLastPublishDate, portletDataContext.getEndDate());

			_exportImportProcessCallbackRegistry.registerCallback(
				portletDataContext.getExportImportProcessId(),
				new UpdatePortletLastPublishDateCallable(
					adjustedDateRange, portletDataContext.getEndDate(),
					portletDataContext.getGroupId(), plid, portletId));
		}
	}

	@Override
	public void exportService(
			PortletDataContext portletDataContext, Element rootElement,
			boolean exportServiceSetup)
		throws Exception {

		if (!exportServiceSetup) {
			return;
		}

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(
				portletDataContext.getPortletId());

		if (portletDataHandler == null) {
			return;
		}

		String serviceName = portletDataHandler.getServiceName();

		if (Validator.isNotNull(serviceName)) {

			// Company service

			_exportServicePortletPreferences(
				portletDataContext, portletDataContext.getCompanyId(),
				PortletKeys.PREFS_OWNER_TYPE_COMPANY, serviceName, rootElement);

			// Group service

			_exportServicePortletPreferences(
				portletDataContext, portletDataContext.getScopeGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_GROUP, serviceName, rootElement);
		}
	}

	protected File doExport(PortletDataContext portletDataContext)
		throws Exception {

		boolean exportPermissions = MapUtil.getBoolean(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.PERMISSIONS);

		if (_log.isDebugEnabled()) {
			_log.debug("Export permissions " + exportPermissions);
		}

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		Layout layout = _layoutLocalService.getLayout(
			portletDataContext.getPlid());

		if (!layout.isTypeControlPanel() && !layout.isTypePanel() &&
			!layout.isTypePortlet()) {

			throw new LayoutImportException(
				StringBundler.concat(
					"Unable to export layout ", layout.getPlid(),
					" because it has an invalid type: ", layout.getType()));
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();

			serviceContext.setCompanyId(layout.getCompanyId());
			serviceContext.setSignedIn(false);
			serviceContext.setUserId(
				_userLocalService.getGuestUserId(layout.getCompanyId()));

			ServiceContextThreadLocal.pushServiceContext(serviceContext);
		}

		long layoutSetBranchId = MapUtil.getLong(
			portletDataContext.getParameterMap(), "layoutSetBranchId");

		serviceContext.setAttribute("layoutSetBranchId", layoutSetBranchId);

		long scopeGroupId = portletDataContext.getGroupId();

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				layout, portletDataContext.getPortletId());

		String scopeType = GetterUtil.getString(
			jxPortletPreferences.getValue("lfrScopeType", null));
		String scopeLayoutUuid = GetterUtil.getString(
			jxPortletPreferences.getValue("lfrScopeLayoutUuid", null));

		if (Validator.isNotNull(scopeType)) {
			Group scopeGroup = null;

			if (scopeType.equals("company")) {
				scopeGroup = _groupLocalService.getCompanyGroup(
					layout.getCompanyId());
			}
			else if (Validator.isNotNull(scopeLayoutUuid)) {
				scopeGroup = layout.getScopeGroup();
			}

			if (scopeGroup != null) {
				scopeGroupId = scopeGroup.getGroupId();
			}
		}

		portletDataContext.setScopeType(scopeType);
		portletDataContext.setScopeLayoutUuid(scopeLayoutUuid);

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		portletDataContext.setExportDataRootElement(rootElement);

		Element headerElement = rootElement.addElement("header");

		headerElement.addAttribute(
			"available-locales",
			StringUtil.merge(
				_language.getAvailableLocales(
					_portal.getSiteGroupId(
						portletDataContext.getScopeGroupId()))));
		headerElement.addAttribute(
			"build-number", String.valueOf(ReleaseInfo.getBuildNumber()));
		headerElement.addAttribute("export-date", Time.getRFC822());

		if (portletDataContext.hasDateRange()) {
			headerElement.addAttribute(
				"start-date",
				String.valueOf(portletDataContext.getStartDate()));
			headerElement.addAttribute(
				"end-date", String.valueOf(portletDataContext.getEndDate()));
		}

		headerElement.addAttribute("type", portletDataContext.getType());
		headerElement.addAttribute(
			"company-id", String.valueOf(portletDataContext.getCompanyId()));
		headerElement.addAttribute(
			"company-group-id",
			String.valueOf(portletDataContext.getCompanyGroupId()));
		headerElement.addAttribute("group-id", String.valueOf(scopeGroupId));
		headerElement.addAttribute(
			"user-personal-site-group-id",
			String.valueOf(portletDataContext.getUserPersonalSiteGroupId()));
		headerElement.addAttribute(
			"private-layout", String.valueOf(layout.isPrivateLayout()));
		headerElement.addAttribute(
			"root-portlet-id", portletDataContext.getRootPortletId());
		headerElement.addAttribute(
			"schema-version",
			ExportImportConstants.EXPORT_IMPORT_SCHEMA_VERSION);

		portletDataContext.setMissingReferencesElement(
			rootElement.addElement("missing-references"));

		Map<String, Boolean> exportPortletControlsMap =
			_exportImportHelper.getExportPortletControlsMap(
				layout.getCompanyId(), portletDataContext.getPortletId(),
				portletDataContext.getParameterMap());

		exportPortlet(
			portletDataContext, layout.getPlid(), rootElement,
			exportPermissions,
			exportPortletControlsMap.get(
				PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS),
			exportPortletControlsMap.get(PortletDataHandlerKeys.PORTLET_DATA),
			exportPortletControlsMap.get(PortletDataHandlerKeys.PORTLET_SETUP),
			exportPortletControlsMap.get(
				PortletDataHandlerKeys.PORTLET_USER_PREFERENCES));
		exportService(
			portletDataContext, rootElement,
			exportPortletControlsMap.get(PortletDataHandlerKeys.PORTLET_SETUP));

		exportAssetLinks(portletDataContext);
		exportLocks(portletDataContext);

		portletDataContext.addDeletionSystemEventStagedModelTypes(
			new StagedModelType(StagedAssetLink.class));

		_deletionSystemEventExporter.exportDeletionSystemEvents(
			portletDataContext);

		if (exportPermissions) {
			_permissionExporter.exportPortletDataPermissions(
				portletDataContext);
		}

		_exportImportHelper.writeManifestSummary(
			document, portletDataContext.getManifestSummary());

		if (_log.isInfoEnabled()) {
			_log.info("Exporting portlet took " + stopWatch.getTime() + " ms");
		}

		try {
			portletDataContext.addZipEntry(
				"/manifest.xml", document.formattedString());
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					PortletExportControllerImpl.class.getName(), ioException);

			exportImportIOException.setPortletId(
				portletDataContext.getPortletId());
			exportImportIOException.setType(
				ExportImportIOException.PORTLET_EXPORT);

			throw exportImportIOException;
		}

		ZipWriter zipWriter = portletDataContext.getZipWriter();

		return zipWriter.getFile();
	}

	protected PortletDataContext getPortletDataContext(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		long sourcePlid = MapUtil.getLong(settingsMap, "sourcePlid");
		long sourceGroupId = MapUtil.getLong(settingsMap, "sourceGroupId");
		String portletId = MapUtil.getString(settingsMap, "portletId");
		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		DateRange dateRange = ExportImportDateUtil.getDateRange(
			exportImportConfiguration);

		Layout layout = _layoutLocalService.getLayout(sourcePlid);
		ZipWriter zipWriter = _exportImportHelper.getPortletZipWriter(
			portletId);

		PortletDataContext portletDataContext =
			_portletDataContextFactory.createExportPortletDataContext(
				layout.getCompanyId(), sourceGroupId, parameterMap,
				dateRange.getStartDate(), dateRange.getEndDate(), zipWriter);

		portletDataContext.setExportImportProcessId(
			String.valueOf(
				exportImportConfiguration.getExportImportConfigurationId()));
		portletDataContext.setPlid(sourcePlid);
		portletDataContext.setPortletId(portletId);
		portletDataContext.setType("portlet");

		return portletDataContext;
	}

	protected PortletPreferences getPortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws PortalException {

		PortletPreferences portletPreferences = null;

		if ((ownerType == PortletKeys.PREFS_OWNER_TYPE_ARCHIVED) ||
			(ownerType == PortletKeys.PREFS_OWNER_TYPE_COMPANY) ||
			(ownerType == PortletKeys.PREFS_OWNER_TYPE_GROUP)) {

			portletPreferences =
				_portletPreferencesLocalService.getPortletPreferences(
					ownerId, ownerType, LayoutConstants.DEFAULT_PLID,
					portletId);
		}
		else {
			portletPreferences =
				_portletPreferencesLocalService.getPortletPreferences(
					ownerId, ownerType, plid, portletId);
		}

		return portletPreferences;
	}

	protected int getProcessFlag() {
		if (ExportImportThreadLocal.isPortletStagingInProcess()) {
			return ExportImportLifecycleConstants.
				PROCESS_FLAG_PORTLET_STAGING_IN_PROCESS;
		}

		return ExportImportLifecycleConstants.
			PROCESS_FLAG_PORTLET_EXPORT_IN_PROCESS;
	}

	private void _exportPortletPreference(
			PortletDataContext portletDataContext, long ownerId, int ownerType,
			boolean defaultUser, PortletPreferences portletPreferences,
			String portletId, long plid, Element parentElement)
		throws Exception {

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				portletPreferences);

		Portlet portlet = _portletLocalService.getPortletById(
			portletDataContext.getCompanyId(), portletId);

		Element portletPreferencesElement = parentElement.addElement(
			"portlet-preferences");

		if ((portlet != null) &&
			(portlet.getPortletDataHandlerInstance() != null)) {

			Element exportDataRootElement =
				portletDataContext.getExportDataRootElement();
			Set<String> oldScopedPrimaryKeys = new HashSet<>(
				portletDataContext.getScopedPrimaryKeys());

			Map<String, String[]> parameterMap =
				portletDataContext.getParameterMap();

			boolean portletDataAll = MapUtil.getBoolean(
				parameterMap, PortletDataHandlerKeys.PORTLET_DATA_ALL);

			try {
				portletDataContext.clearScopedPrimaryKeys();

				Set<String> scopedLayoutPrimaryKeys = new HashSet<>();

				for (String oldScopedPrimaryKey : oldScopedPrimaryKeys) {
					if (oldScopedPrimaryKey.contains(Layout.class.getName())) {
						scopedLayoutPrimaryKeys.add(oldScopedPrimaryKey);
					}
				}

				portletDataContext.addScopedPrimaryKeys(
					scopedLayoutPrimaryKeys);

				Element preferenceDataElement =
					portletPreferencesElement.addElement("preference-data");

				portletDataContext.setExportDataRootElement(
					preferenceDataElement);

				ExportImportPortletPreferencesProcessor
					exportImportPortletPreferencesProcessor =
						ExportImportPortletPreferencesProcessorRegistryUtil.
							getExportImportPortletPreferencesProcessor(
								portlet.getRootPortletId());

				if (exportImportPortletPreferencesProcessor != null) {
					List<Capability> exportCapabilities =
						exportImportPortletPreferencesProcessor.
							getExportCapabilities();

					if (ListUtil.isNotEmpty(exportCapabilities)) {
						for (Capability exportCapability : exportCapabilities) {
							exportCapability.process(
								portletDataContext, jxPortletPreferences);
						}
					}

					exportImportPortletPreferencesProcessor.
						processExportPortletPreferences(
							portletDataContext, jxPortletPreferences);
				}
			}
			finally {
				parameterMap.put(
					PortletDataHandlerKeys.PORTLET_DATA_ALL,
					new String[] {String.valueOf(portletDataAll)});

				portletDataContext.addScopedPrimaryKeys(oldScopedPrimaryKeys);
				portletDataContext.setExportDataRootElement(
					exportDataRootElement);
			}
		}

		Document document = SAXReaderUtil.read(
			PortletPreferencesFactoryUtil.toXML(jxPortletPreferences));

		Element rootElement = document.getRootElement();

		rootElement.addAttribute("owner-id", String.valueOf(ownerId));
		rootElement.addAttribute("owner-type", String.valueOf(ownerType));
		rootElement.addAttribute("default-user", String.valueOf(defaultUser));
		rootElement.addAttribute("plid", String.valueOf(plid));
		rootElement.addAttribute("portlet-id", portletId);

		if (ownerType == PortletKeys.PREFS_OWNER_TYPE_ARCHIVED) {
			PortletItem portletItem = _portletItemLocalService.getPortletItem(
				ownerId);

			rootElement.addAttribute(
				"archive-user-uuid", portletItem.getUserUuid());
			rootElement.addAttribute("archive-name", portletItem.getName());
		}
		else if (ownerType == PortletKeys.PREFS_OWNER_TYPE_USER) {
			User user = _userLocalService.fetchUserById(ownerId);

			if (user == null) {
				return;
			}

			rootElement.addAttribute("user-uuid", user.getUserUuid());
		}

		List<Node> nodes = document.selectNodes(
			"/portlet-preferences/preference[name/text() = " +
				"'last-publish-date']");

		for (Node node : nodes) {
			node.detach();
		}

		String path = ExportImportPathUtil.getPortletPreferencesPath(
			portletDataContext, portletId, ownerId, ownerType, plid);

		portletPreferencesElement.addAttribute("path", path);

		portletDataContext.addZipEntry(
			path, document.formattedString(StringPool.TAB, false, false));
	}

	private void _exportPortletPreferences(
			PortletDataContext portletDataContext, long ownerId, int ownerType,
			boolean defaultUser, Layout layout, long plid, String portletId,
			Element parentElement)
		throws Exception {

		PortletPreferences portletPreferences = null;

		try {
			portletPreferences = getPortletPreferences(
				ownerId, ownerType, plid, portletId);
		}
		catch (NoSuchPortletPreferencesException
					noSuchPortletPreferencesException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchPortletPreferencesException);
			}

			return;
		}

		if (_hasPortletId(layout, portletId, ownerType)) {
			_exportPortletPreference(
				portletDataContext, ownerId, ownerType, defaultUser,
				portletPreferences, portletId, plid, parentElement);
		}
	}

	private void _exportServicePortletPreference(
			PortletDataContext portletDataContext, long ownerId, int ownerType,
			PortletPreferences portletPreferences, String serviceName,
			Element parentElement)
		throws Exception {

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				portletPreferences);

		Document document = SAXReaderUtil.read(
			PortletPreferencesFactoryUtil.toXML(jxPortletPreferences));

		Element rootElement = document.getRootElement();

		rootElement.addAttribute("owner-id", String.valueOf(ownerId));
		rootElement.addAttribute("owner-type", String.valueOf(ownerType));
		rootElement.addAttribute("default-user", Boolean.FALSE.toString());
		rootElement.addAttribute("service-name", serviceName);

		if (ownerType == PortletKeys.PREFS_OWNER_TYPE_ARCHIVED) {
			PortletItem portletItem = _portletItemLocalService.getPortletItem(
				ownerId);

			rootElement.addAttribute(
				"archive-user-uuid", portletItem.getUserUuid());
			rootElement.addAttribute("archive-name", portletItem.getName());
		}
		else if (ownerType == PortletKeys.PREFS_OWNER_TYPE_USER) {
			User user = _userLocalService.fetchUserById(ownerId);

			if (user == null) {
				return;
			}

			rootElement.addAttribute("user-uuid", user.getUserUuid());
		}

		List<Node> nodes = document.selectNodes(
			"/portlet-preferences/preference[name/text() = " +
				"'last-publish-date']");

		for (Node node : nodes) {
			node.detach();
		}

		Element serviceElement = parentElement.addElement("service");

		String path = ExportImportPathUtil.getServicePortletPreferencesPath(
			portletDataContext, serviceName, ownerId, ownerType);

		serviceElement.addAttribute("path", path);

		serviceElement.addAttribute("service-name", serviceName);

		portletDataContext.addZipEntry(path, document.formattedString());
	}

	private void _exportServicePortletPreferences(
			PortletDataContext portletDataContext, long ownerId, int ownerType,
			String serviceName, Element parentElement)
		throws Exception {

		PortletPreferences portletPreferences = null;

		try {
			portletPreferences = getPortletPreferences(
				ownerId, ownerType, LayoutConstants.DEFAULT_PLID, serviceName);
		}
		catch (NoSuchPortletPreferencesException
					noSuchPortletPreferencesException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchPortletPreferencesException);
			}

			return;
		}

		_exportServicePortletPreference(
			portletDataContext, ownerId, ownerType, portletPreferences,
			serviceName, parentElement);
	}

	private String _getLockPath(
		PortletDataContext portletDataContext, String className, String key,
		Lock lock) {

		return StringBundler.concat(
			ExportImportPathUtil.getRootPath(portletDataContext), "/locks/",
			_portal.getClassNameId(className), CharPool.FORWARD_SLASH, key,
			CharPool.FORWARD_SLASH, lock.getLockId(), ".xml");
	}

	private PortletDataHandler _getPortletDataHandler(
		PortletDataContext portletDataContext, Portlet portlet) {

		portlet = _replacePortlet(portletDataContext, portlet);

		if (portlet != null) {
			return portlet.getPortletDataHandlerInstance();
		}

		return null;
	}

	private boolean _hasPortletId(
		Layout layout, String portletId, int ownerType) {

		if (layout == null) {
			return true;
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		if (layoutTypePortlet == null) {
			return true;
		}

		boolean rootPortletId = false;

		if ((ownerType == PortletKeys.PREFS_OWNER_TYPE_COMPANY) ||
			(ownerType == PortletKeys.PREFS_OWNER_TYPE_GROUP)) {

			rootPortletId = true;
		}

		if (!rootPortletId) {
			return layoutTypePortlet.hasPortletId(portletId);
		}

		List<Portlet> allPortlets = layoutTypePortlet.getAllPortlets(false);

		for (Portlet portlet : allPortlets) {
			if (portletId.equals(portlet.getRootPortletId())) {
				return true;
			}
		}

		return false;
	}

	private boolean _isIncludeAllAssetLinks() {
		try {
			ExportImportServiceConfiguration exportImportServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ExportImportServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return exportImportServiceConfiguration.includeAllAssetLinks();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private Portlet _replacePortlet(
		PortletDataContext portletDataContext, Portlet portlet) {

		if (ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext)) {

			String changesetPortletId = ChangesetPortletKeys.CHANGESET;

			if (ExportImportThreadLocal.isPortletStagingInProcess()) {
				return _portletLocalService.getPortletById(changesetPortletId);
			}

			if (ExportImportThreadLocal.isLayoutStagingInProcess() &&
				!changesetPortletId.equals(portlet.getPortletId())) {

				return null;
			}
		}

		return portlet;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletExportControllerImpl.class);

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DeletionSystemEventExporter _deletionSystemEventExporter;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLifecycleManager _exportImportLifecycleManager;

	@Reference
	private ExportImportProcessCallbackRegistry
		_exportImportProcessCallbackRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	private final PermissionExporter _permissionExporter =
		PermissionExporter.getInstance();

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
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Reference
	private UserLocalService _userLocalService;

	private class UpdatePortletLastPublishDateCallable
		implements Callable<Void> {

		public UpdatePortletLastPublishDateCallable(
			DateRange dateRange, Date endDate, long groupId, long plid,
			String portletId) {

			_dateRange = dateRange;
			_endDate = endDate;
			_groupId = groupId;
			_plid = plid;
			_portletId = portletId;
		}

		@Override
		public Void call() throws PortalException {
			Group group = _groupLocalService.getGroup(_groupId);

			Layout layout = _layoutLocalService.fetchLayout(_plid);

			if (group.hasStagingGroup()) {
				group = group.getStagingGroup();

				if (layout != null) {
					layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
						layout.getUuid(), group.getGroupId(),
						layout.isPrivateLayout());
				}
			}

			jakarta.portlet.PortletPreferences jxPortletPreferences = null;

			if (layout == null) {
				jxPortletPreferences =
					PortletPreferencesFactoryUtil.getStrictPortletSetup(
						group.getCompanyId(), group.getGroupId(), _portletId);
			}
			else {
				jxPortletPreferences =
					PortletPreferencesFactoryUtil.getStrictPortletSetup(
						layout, _portletId);
			}

			ExportImportDateUtil.updateLastPublishDate(
				_portletId, jxPortletPreferences, _dateRange, _endDate);

			return null;
		}

		private final DateRange _dateRange;
		private final Date _endDate;
		private final long _groupId;
		private final long _plid;
		private final String _portletId;

	}

}