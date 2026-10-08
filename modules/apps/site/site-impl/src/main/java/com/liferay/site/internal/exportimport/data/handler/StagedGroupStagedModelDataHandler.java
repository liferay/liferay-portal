/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.exportimport.data.handler;

import com.liferay.exportimport.controller.PortletExportController;
import com.liferay.exportimport.controller.PortletImportController;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerStatusMessageSender;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.exportimport.lar.PermissionImporter;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.portlet.element.handler.PortletElementHandler;
import com.liferay.exportimport.portlet.element.handler.PortletElementHandlerFactory;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.layout.set.model.adapter.StagedLayoutSet;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.site.internal.exportimport.staged.model.repository.StagedGroupStagedModelRepositoryUtil;
import com.liferay.site.model.adapter.StagedGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(service = StagedModelDataHandler.class)
public class StagedGroupStagedModelDataHandler
	extends BaseStagedModelDataHandler<StagedGroup> {

	public static final String[] CLASS_NAMES = {StagedGroup.class.getName()};

	@Override
	public void deleteStagedModel(StagedGroup stagedGroup) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteStagedModel(
		String uuid, long groupId, String className, String extraData) {

		throw new UnsupportedOperationException();
	}

	@Override
	public List<StagedGroup> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return Collections.emptyList();
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(StagedGroup stagedGroup) {
		Group group = stagedGroup.getGroup();

		return group.getName();
	}

	@Override
	public boolean validateReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		if ((groupId == 0) || groupIds.containsKey(groupId)) {
			return true;
		}

		Group existingGroup =
			StagedGroupStagedModelRepositoryUtil.fetchExistingGroup(
				portletDataContext, referenceElement);

		if (existingGroup == null) {
			return false;
		}

		groupIds.put(groupId, existingGroup.getGroupId());

		return true;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, StagedGroup stagedGroup)
		throws Exception {

		// Collect site portlets and initialize the progress bar

		Group group = stagedGroup.getGroup();

		Set<String> portletIds = _checkExportablePortletIds(
			group, portletDataContext);

		if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
			ManifestSummary manifestSummary =
				portletDataContext.getManifestSummary();

			_portletDataHandlerStatusMessageSender.sendStatusMessage(
				"layout", ArrayUtil.toStringArray(portletIds), manifestSummary);

			manifestSummary.resetCounters();
		}

		long[] layoutIds = portletDataContext.getLayoutIds();

		if (group.isLayoutPrototype()) {
			layoutIds = _exportImportHelper.getAllLayoutIds(
				group.getGroupId(), portletDataContext.isPrivateLayout());
		}

		// Export site data portlets

		long previousScopeGroupId = portletDataContext.getScopeGroupId();

		try {
			_exportSitePortlets(
				portletDataContext, stagedGroup, portletIds, layoutIds);
		}
		finally {
			portletDataContext.setScopeGroupId(previousScopeGroupId);
		}

		// Layout set with layouts

		List<? extends StagedModel> childStagedModels =
			StagedGroupStagedModelRepositoryUtil.fetchChildrenStagedModels(
				portletDataContext, stagedGroup);

		for (StagedModel stagedModel : childStagedModels) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, stagedGroup, stagedModel,
				PortletDataContext.REFERENCE_TYPE_CHILD);
		}

		// Serialize the group

		Element groupElement = portletDataContext.getExportDataElement(
			stagedGroup);

		portletDataContext.addClassedModel(
			groupElement, ExportImportPathUtil.getModelPath(stagedGroup),
			stagedGroup);
	}

	@Override
	protected void doImportMissingReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		if ((groupId == 0) || groupIds.containsKey(groupId)) {
			return;
		}

		Group existingGroup =
			StagedGroupStagedModelRepositoryUtil.fetchExistingGroup(
				portletDataContext, referenceElement);

		if (existingGroup == null) {
			return;
		}

		groupIds.put(groupId, existingGroup.getGroupId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, StagedGroup stagedGroup)
		throws Exception {

		Element rootElement = portletDataContext.getImportDataRootElement();

		Element sitePortletsElement = rootElement.element("site-portlets");

		List<Element> sitePortletElements = sitePortletsElement.elements();

		// Initialize progress bar

		if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
			List<String> portletIds = new ArrayList<>();

			for (Element portletElement : sitePortletElements) {
				PortletElementHandler portletElementHandler =
					_portletElementHandlerFactory.create(portletElement);

				Portlet portlet = portletElementHandler.getPortlet(
					portletDataContext.getCompanyId());

				if ((portlet == null) &&
					!portletElementHandler.isMissingPortletSupported()) {

					continue;
				}

				portletIds.add(portletElementHandler.getSourcePortletId());
			}

			_portletDataHandlerStatusMessageSender.sendStatusMessage(
				"layout", ArrayUtil.toStringArray(portletIds),
				portletDataContext.getManifestSummary());
		}

		// Import site data portlets

		if (_log.isDebugEnabled() && !sitePortletElements.isEmpty()) {
			_log.debug("Importing portlets");
		}

		_importSitePortlets(
			portletDataContext, sitePortletElements, stagedGroup);

		// Import services

		Element siteServicesElement = rootElement.element("site-services");

		List<Element> siteServiceElements = siteServicesElement.elements(
			"service");

		if (_log.isDebugEnabled() && !siteServiceElements.isEmpty()) {
			_log.debug("Importing services");
		}

		_importSiteServices(portletDataContext, siteServiceElements);

		// Import layout set

		Element layoutSetElement = portletDataContext.getImportDataGroupElement(
			StagedLayoutSet.class);

		for (Element groupElement : layoutSetElement.elements()) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, groupElement);
		}
	}

	@Override
	protected StagedModelRepository<StagedGroup> getStagedModelRepository() {
		return _stagedGroupStagedModelRepository;
	}

	@Override
	protected void importReferenceStagedModels(
			PortletDataContext portletDataContext, StagedGroup stagedModel)
		throws PortletDataException {
	}

	private Set<String> _checkExportablePortletIds(
			Group group, PortletDataContext portletDataContext)
		throws Exception {

		Set<String> portletIds = new LinkedHashSet<>();

		Group liveGroup = group;

		if (liveGroup.isStagingGroup()) {
			liveGroup = liveGroup.getLiveGroup();
		}

		for (Portlet portlet :
				_exportImportHelper.getExportablePortlets(
					portletDataContext.getCompanyId(), false,
					group.getGroupId())) {

			String portletId = portlet.getRootPortletId();

			if (ExportImportThreadLocal.isStagingInProcess() &&
				!liveGroup.isStagedPortlet(portletId)) {

				continue;
			}

			// Calculate the amount of exported data

			if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
				Map<String, Boolean> exportPortletControlsMap =
					_exportImportHelper.getExportPortletControlsMap(
						portletDataContext.getCompanyId(), portletId,
						portletDataContext.getParameterMap(),
						portletDataContext.getType());

				if (exportPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_DATA)) {

					PortletDataHandler portletDataHandler =
						portlet.getPortletDataHandlerInstance();

					portletDataHandler.prepareManifestSummary(
						portletDataContext);
				}
			}

			// Add portlet ID to exportable portlets list

			portletIds.add(portletId);
		}

		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferences(
				portletDataContext.getScopeGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				PortletKeys.PREFS_PLID_SHARED);

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			portletIds.add(portletPreferences.getPortletId());
		}

		return portletIds;
	}

	private void _exportPortlet(
			PortletDataContext portletDataContext, String portletId, long plid,
			long scopeGroupId, String scopeType, String scopeLayoutUuid,
			String type, Element portletsElement, Element servicesElement,
			boolean permissions)
		throws Exception {

		portletDataContext.setPlid(plid);
		portletDataContext.setPortletId(portletId);
		portletDataContext.setScopeGroupId(scopeGroupId);
		portletDataContext.setScopeType(scopeType);
		portletDataContext.setScopeLayoutUuid(scopeLayoutUuid);
		portletDataContext.setValidateExistingDataHandler(false);

		Map<String, Boolean> exportPortletControlsMap =
			_exportImportHelper.getExportPortletControlsMap(
				portletDataContext.getCompanyId(), portletId,
				portletDataContext.getParameterMap(), type);

		try {
			_exportImportLifecycleManager.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_PORTLET_EXPORT_STARTED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				_portletDataContextFactory.clonePortletDataContext(
					portletDataContext));

			_portletExportController.exportPortlet(
				portletDataContext, plid, portletsElement, permissions,
				exportPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS),
				exportPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_DATA),
				exportPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_SETUP),
				exportPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_USER_PREFERENCES));
			_portletExportController.exportService(
				portletDataContext, servicesElement,
				exportPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_SETUP));

			_exportImportLifecycleManager.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_PORTLET_EXPORT_SUCCEEDED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				_portletDataContextFactory.clonePortletDataContext(
					portletDataContext));
		}
		catch (Throwable throwable) {
			_exportImportLifecycleManager.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.EVENT_PORTLET_EXPORT_FAILED,
				getProcessFlag(), portletDataContext.getExportImportProcessId(),
				_portletDataContextFactory.clonePortletDataContext(
					portletDataContext),
				throwable);

			throw throwable;
		}
	}

	private void _exportSitePortlets(
			PortletDataContext portletDataContext, StagedGroup stagedGroup,
			Set<String> portletIds, long[] layoutIds)
		throws Exception {

		// Prepare XML

		Element rootElement = portletDataContext.getExportDataRootElement();

		Element portletsElement = rootElement.element("site-portlets");
		Element servicesElement = rootElement.element("site-services");

		String type = portletDataContext.getType();

		// Export portlets

		boolean permissions = MapUtil.getBoolean(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.PERMISSIONS);

		Group group = stagedGroup.getGroup();

		List<Layout> layouts = _layoutLocalService.getLayouts(
			group.getGroupId(), portletDataContext.isPrivateLayout());

		for (String portletId : portletIds) {

			// Default scope

			_exportPortlet(
				portletDataContext, portletId, LayoutConstants.DEFAULT_PLID,
				portletDataContext.getGroupId(), StringPool.BLANK,
				StringPool.BLANK, type, portletsElement, servicesElement,
				permissions);

			Portlet portlet = _portletLocalService.getPortletById(
				portletDataContext.getCompanyId(), portletId);

			if (!portlet.isScopeable()) {
				continue;
			}

			// Scoped data

			for (Layout layout : layouts) {
				if (!ArrayUtil.contains(layoutIds, layout.getLayoutId()) ||
					!layout.isTypePortlet() || !layout.hasScopeGroup()) {

					continue;
				}

				LayoutTypePortlet layoutTypePortlet =
					(LayoutTypePortlet)layout.getLayoutType();

				if (!layoutTypePortlet.hasPortletId(portletId)) {
					continue;
				}

				Group scopeGroup = layout.getScopeGroup();

				_exportPortlet(
					portletDataContext, portletId, layout.getPlid(),
					scopeGroup.getGroupId(), StringPool.BLANK, layout.getUuid(),
					type, portletsElement, servicesElement, permissions);
			}
		}

		// LayoutStagedModelDataHandler.exportLayoutPortlets() was deleted in
		// LPD-80547. This loop restores the missing per-page display portlet
		// export that it used to perform for PORTLET_INSTANCE portlets (e.g.
		// Web Content Display) whose article references are stored in portlet
		// preferences and not tracked by the site-level portlet export above.

		long companyId = portletDataContext.getCompanyId();
		long scopeGroupId = portletDataContext.getScopeGroupId();

		for (Layout layout : layouts) {
			if (!ArrayUtil.contains(layoutIds, layout.getLayoutId())) {
				continue;
			}

			long plid = layout.getPlid();

			List<PortletPreferences> layoutPortletPreferencesList =
				_portletPreferencesLocalService.getPortletPreferences(
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid);

			for (PortletPreferences portletPreferences :
					layoutPortletPreferencesList) {

				String portletId = portletPreferences.getPortletId();

				Portlet portlet = _portletLocalService.getPortletById(
					companyId, portletId);

				if (portlet == null) {
					continue;
				}

				PortletDataHandler portletDataHandler =
					portlet.getPortletDataHandlerInstance();

				if ((portletDataHandler == null) ||
					!portletDataHandler.isDisplayPortlet()) {

					continue;
				}

				_exportPortlet(
					portletDataContext, portletId, plid, scopeGroupId,
					StringPool.BLANK, StringPool.BLANK, type, portletsElement,
					servicesElement, permissions);
			}
		}
	}

	private void _importSitePortlets(
			PortletDataContext portletDataContext,
			List<Element> sitePortletElements, StagedGroup stagedGroup)
		throws Exception {

		Map<Long, Layout> layouts =
			(Map<Long, Layout>)portletDataContext.getNewPrimaryKeysMap(
				Layout.class + ".layout");

		boolean permissions = MapUtil.getBoolean(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.PERMISSIONS);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		_permissionImporter.clearCache();

		List<PortletElementHandler> nonbatchPortletElementHandlers =
			new ArrayList<>();
		Map<Integer, List<PortletElementHandler>> rankedBatchPortletElements =
			new TreeMap<>();

		for (Element portletElement : sitePortletElements) {
			PortletElementHandler portletElementHandler =
				_portletElementHandlerFactory.create(portletElement);

			PortletDataHandler portletDataHandler =
				_portletDataHandlerProvider.provide(
					portletDataContext.getCompanyId(),
					portletElementHandler.getSourcePortletId());

			if (portletDataHandler == null) {
				String targetPortletId =
					portletElementHandler.getTargetPortletId(
						portletDataContext.getCompanyId());

				if (targetPortletId == null) {
					if (portletElementHandler.isMissingPortletSupported()) {
						rankedBatchPortletElements.computeIfAbsent(
							portletElementHandler.getRank(),
							__ -> new ArrayList<>()
						).add(
							portletElementHandler
						);
					}

					continue;
				}

				portletDataHandler = _portletDataHandlerProvider.provide(
					portletDataContext.getCompanyId(), targetPortletId);

				if (portletDataHandler == null) {
					continue;
				}
			}

			if (portletDataHandler.isBatch()) {
				rankedBatchPortletElements.computeIfAbsent(
					portletDataHandler.getRank(), __ -> new ArrayList<>()
				).add(
					portletElementHandler
				);
			}
			else {
				nonbatchPortletElementHandlers.add(portletElementHandler);
			}
		}

		List<PortletElementHandler> orderedPortletElementHandlers =
			new ArrayList<>();

		for (List<PortletElementHandler> portletElementHandler :
				rankedBatchPortletElements.values()) {

			orderedPortletElementHandlers.addAll(portletElementHandler);
		}

		orderedPortletElementHandlers.addAll(nonbatchPortletElementHandlers);

		for (PortletElementHandler portletElementHandler :
				orderedPortletElementHandlers) {

			Layout layout = layouts.get(portletElementHandler.getLayoutId());

			long plid = LayoutConstants.DEFAULT_PLID;

			if (layout != null) {
				plid = layout.getPlid();
			}

			portletDataContext.setPlid(plid);

			String targetPortletId = portletElementHandler.getTargetPortletId(
				portletDataContext.getCompanyId());

			if (targetPortletId == null) {
				StagedModelDataHandlerUtil.handleException(
					portletDataContext,
					new PortletDataException(
						_language.format(
							LocaleUtil.US,
							"the-data-handler-for-the-x-portlet-is-missing-" +
								"from-the-system",
							portletElementHandler.getDisplayName())),
					stagedGroup);

				continue;
			}

			portletDataContext.setPortletId(targetPortletId);

			String sourcePortletId = portletElementHandler.getSourcePortletId();

			if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
				_portletDataHandlerStatusMessageSender.sendStatusMessage(
					"portlet", sourcePortletId,
					portletDataContext.getManifestSummary());
			}

			String portletPath = portletElementHandler.getPath();

			Document portletDocument = SAXReaderUtil.read(
				portletDataContext.getZipEntryAsString(portletPath));

			Element portletElement = portletDocument.getRootElement();

			// The order of the import is important. You must always import the
			// portlet preferences first, then the portlet data, then the
			// portlet permissions. The import of the portlet data assumes that
			// portlet preferences already exist.

			_exportImportHelper.setPortletScope(
				portletDataContext, portletElement);

			long portletPreferencesGroupId = portletDataContext.getGroupId();

			Element portletDataElement = portletElement.element("portlet-data");

			Map<String, Boolean> importPortletControlsMap =
				_exportImportHelper.getImportPortletControlsMap(
					portletDataContext.getCompanyId(), sourcePortletId,
					targetPortletId, portletDataContext.getParameterMap(),
					portletDataElement,
					portletDataContext.getManifestSummary());

			if (layout != null) {
				portletPreferencesGroupId = layout.getGroupId();
			}

			try {
				_exportImportLifecycleManager.fireExportImportLifecycleEvent(
					ExportImportLifecycleConstants.EVENT_PORTLET_IMPORT_STARTED,
					getProcessFlag(),
					portletDataContext.getExportImportProcessId(),
					_portletDataContextFactory.clonePortletDataContext(
						portletDataContext));

				// Portlet preferences

				_portletImportController.importPortletPreferences(
					portletDataContext, portletDataContext.getCompanyId(),
					portletPreferencesGroupId, layout, portletElement, false,
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
						PortletDataHandlerKeys.PORTLET_DATA) ||
					_isHidden(
						portletDataContext.getCompanyId(), targetPortletId)) {

					_portletImportController.importPortletData(
						portletDataContext, portletDataElement);
				}

				_exportImportLifecycleManager.fireExportImportLifecycleEvent(
					ExportImportLifecycleConstants.
						EVENT_PORTLET_IMPORT_SUCCEEDED,
					getProcessFlag(),
					portletDataContext.getExportImportProcessId(),
					_portletDataContextFactory.clonePortletDataContext(
						portletDataContext));
			}
			catch (Throwable throwable) {
				_exportImportLifecycleManager.fireExportImportLifecycleEvent(
					ExportImportLifecycleConstants.EVENT_PORTLET_IMPORT_FAILED,
					getProcessFlag(),
					portletDataContext.getExportImportProcessId(),
					_portletDataContextFactory.clonePortletDataContext(
						portletDataContext),
					throwable);

				throw throwable;
			}
			finally {
				_portletImportController.resetPortletScope(
					portletDataContext, portletPreferencesGroupId);
			}

			// Portlet permissions

			if (permissions) {
				_permissionImporter.importPortletPermissions(
					portletDataContext.getCompanyId(),
					portletDataContext.getGroupId(), serviceContext.getUserId(),
					layout, portletElement, targetPortletId);
			}

			// Archived setups

			_portletImportController.importPortletPreferences(
				portletDataContext, portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), null, portletElement, false,
				importPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS),
				importPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_DATA),
				importPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_SETUP),
				importPortletControlsMap.get(
					PortletDataHandlerKeys.PORTLET_USER_PREFERENCES));
		}
	}

	private void _importSiteServices(
			PortletDataContext portletDataContext,
			List<Element> siteServiceElements)
		throws Exception {

		for (Element serviceElement : siteServiceElements) {
			String path = serviceElement.attributeValue("path");

			Document siteServiceDocument = SAXReaderUtil.read(
				portletDataContext.getZipEntryAsString(path));

			serviceElement = siteServiceDocument.getRootElement();

			_portletImportController.importServicePortletPreferences(
				portletDataContext, serviceElement);
		}
	}

	private boolean _isHidden(long companyId, String portletId) {
		Portlet portlet = _portletLocalService.getPortletById(
			companyId, portletId);

		PortletDataHandler portletDataHandler =
			portlet.getPortletDataHandlerInstance();

		if (portletDataHandler == null) {
			return false;
		}

		return portletDataHandler.isHidden();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagedGroupStagedModelDataHandler.class);

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLifecycleManager _exportImportLifecycleManager;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PermissionImporter _permissionImporter;

	@Reference
	private PortletDataContextFactory _portletDataContextFactory;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Reference
	private PortletDataHandlerStatusMessageSender
		_portletDataHandlerStatusMessageSender;

	@Reference
	private PortletElementHandlerFactory _portletElementHandlerFactory;

	@Reference
	private PortletExportController _portletExportController;

	@Reference
	private PortletImportController _portletImportController;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.site.model.adapter.StagedGroup)"
	)
	private StagedModelRepository<StagedGroup>
		_stagedGroupStagedModelRepository;

}