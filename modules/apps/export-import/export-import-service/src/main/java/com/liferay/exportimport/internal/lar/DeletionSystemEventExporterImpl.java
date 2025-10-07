/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.exportimport.internal.data.handler.BatchEnginePortletDataHandler;
import com.liferay.exportimport.internal.data.handler.BatchEnginePortletDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportProcessCallbackRegistry;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.lar.DeletionSystemEventExporter;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zsolt Berentey
 */
@Component(service = DeletionSystemEventExporter.class)
public class DeletionSystemEventExporterImpl
	implements DeletionSystemEventExporter {

	public void exportDeletionSystemEvents(
			PortletDataContext portletDataContext)
		throws Exception {

		List<Long> exportedSystemEventIds = null;

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("deletion-system-events");

		Set<StagedModelType> deletionSystemEventStagedModelTypes =
			portletDataContext.getDeletionSystemEventStagedModelTypes();

		if (!deletionSystemEventStagedModelTypes.isEmpty() &&
			MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.DELETIONS)) {

			if (!MapUtil.getBoolean(
					portletDataContext.getParameterMap(),
					PortletDataHandlerKeys.DELETE_LAYOUTS)) {

				deletionSystemEventStagedModelTypes.remove(
					new StagedModelType(Layout.class));
			}

			exportedSystemEventIds = _exportDeletionSystemEvents(
				portletDataContext, rootElement,
				deletionSystemEventStagedModelTypes);
		}

		if (exportedSystemEventIds != null) {
			for (Long systemEventId : exportedSystemEventIds) {
				SystemEvent systemEvent =
					_systemEventLocalService.fetchSystemEvent(systemEventId);

				JSONObject jsonObject = _jsonFactory.createJSONObject(
					systemEvent.getExtraData());

				Object assetTitle = jsonObject.get("assetTitle");

				if (assetTitle == null) {
					continue;
				}

				ManifestSummary manifestSummary =
					portletDataContext.getManifestSummary();

				manifestSummary.addAssetTitle(
					systemEvent.getClassName(), String.valueOf(assetTitle));
			}
		}

		portletDataContext.addZipEntry(
			ExportImportPathUtil.getRootPath(portletDataContext) +
				"/deletion-system-events.xml",
			document.formattedString());

		Map<String, Map<?, ?>> newPrimaryKeysMaps =
			portletDataContext.getNewPrimaryKeysMaps();

		for (String key : newPrimaryKeysMaps.keySet()) {
			if (!key.endsWith(
					BatchEnginePortletDataHandler.
						BATCH_DELETE_CLASS_NAME_POSTFIX)) {

				continue;
			}

			BatchEnginePortletDataHandler batchEnginePortletDataHandler =
				BatchEnginePortletDataHandlerRegistryUtil.
					getBatchEnginePortletDataHandler(
						StringUtil.removeLast(
							key,
							BatchEnginePortletDataHandler.
								BATCH_DELETE_CLASS_NAME_POSTFIX));

			if (batchEnginePortletDataHandler != null) {
				batchEnginePortletDataHandler.exportDeletionSystemEvents(
					portletDataContext);
			}
		}

		if (ListUtil.isNotEmpty(exportedSystemEventIds) &&
			ExportImportThreadLocal.isStagingInProcess()) {

			_exportImportProcessCallbackRegistry.registerCallback(
				portletDataContext.getExportImportProcessId(),
				new DeleteSystemEventsCallable(exportedSystemEventIds));
		}
	}

	protected void addCreateDateProperty(
		PortletDataContext portletDataContext, DynamicQuery dynamicQuery) {

		if (!portletDataContext.hasDateRange()) {
			return;
		}

		Property createDateProperty = PropertyFactoryUtil.forName("createDate");

		dynamicQuery.add(
			createDateProperty.ge(portletDataContext.getStartDate()));

		dynamicQuery.add(
			createDateProperty.le(portletDataContext.getEndDate()));
	}

	protected void doAddCriteria(
		PortletDataContext portletDataContext,
		Set<StagedModelType> deletionSystemEventStagedModelTypes,
		DynamicQuery dynamicQuery) {

		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

		Property groupIdProperty = PropertyFactoryUtil.forName("groupId");

		disjunction.add(groupIdProperty.eq(0L));
		disjunction.add(
			groupIdProperty.eq(portletDataContext.getScopeGroupId()));

		dynamicQuery.add(disjunction);

		if (!deletionSystemEventStagedModelTypes.isEmpty()) {
			Property classNameIdProperty = PropertyFactoryUtil.forName(
				"classNameId");

			Property referrerClassNameIdProperty = PropertyFactoryUtil.forName(
				"referrerClassNameId");

			Disjunction referrerClassNameIdDisjunction =
				RestrictionsFactoryUtil.disjunction();

			for (StagedModelType stagedModelType :
					deletionSystemEventStagedModelTypes) {

				Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

				conjunction.add(
					classNameIdProperty.eq(stagedModelType.getClassNameId()));

				if (stagedModelType.getReferrerClassNameId() >= 0) {
					conjunction.add(
						referrerClassNameIdProperty.eq(
							stagedModelType.getReferrerClassNameId()));
				}

				String className = stagedModelType.getClassName();

				if (className.equals(Layout.class.getName())) {
					Property extraDataProperty = PropertyFactoryUtil.forName(
						"extraData");

					conjunction.add(
						extraDataProperty.like(
							"%\"privateLayout\":\"" +
								portletDataContext.isPrivateLayout() + "\"%"));
				}

				referrerClassNameIdDisjunction.add(conjunction);
			}

			dynamicQuery.add(referrerClassNameIdDisjunction);
		}

		Property typeProperty = PropertyFactoryUtil.forName("type");

		dynamicQuery.add(typeProperty.eq(SystemEventConstants.TYPE_DELETE));

		if (ExportImportDateUtil.isRangeDateRange(portletDataContext)) {
			addCreateDateProperty(portletDataContext, dynamicQuery);
		}
	}

	private void _exportDeletionSystemEvent(
		Element deletionSystemEventsElement,
		PortletDataContext portletDataContext, SystemEvent systemEvent) {

		String className = PortalUtil.getClassName(
			systemEvent.getClassNameId());

		if (BatchEnginePortletDataHandlerRegistryUtil.
				hasBatchEnginePortletDataHandler(className)) {

			Map<String, String> newPrimaryKeysMap =
				(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
					systemEvent.getClassName() +
						BatchEnginePortletDataHandler.
							BATCH_DELETE_CLASS_NAME_POSTFIX);

			newPrimaryKeysMap.put(
				systemEvent.getClassExternalReferenceCode(), StringPool.BLANK);
		}
		else {
			Element deletionSystemEventElement =
				deletionSystemEventsElement.addElement("deletion-system-event");

			deletionSystemEventElement.addAttribute(
				"class-external-reference-code",
				systemEvent.getClassExternalReferenceCode());
			deletionSystemEventElement.addAttribute("class-name", className);

			if (className.equals(FragmentEntry.class.getName())) {
				try {
					JSONObject extraDataJSONObject =
						_jsonFactory.createJSONObject(
							systemEvent.getExtraData());

					Long[] layoutIds = ArrayUtil.toArray(
						portletDataContext.getLayoutIds());

					if (layoutIds.length > 0) {
						String[] layoutUUIDs = new String[layoutIds.length];

						for (int i = 0; i < layoutIds.length; i++) {
							Layout layout = _layoutLocalService.getLayout(
								portletDataContext.getGroupId(),
								portletDataContext.isPrivateLayout(),
								layoutIds[i]);

							layoutUUIDs[i] = layout.getUuid();
						}

						extraDataJSONObject.put(
							"layoutUUIDs", layoutUUIDs
						).put(
							"privateLayout",
							portletDataContext.isPrivateLayout()
						);
					}

					deletionSystemEventElement.addAttribute(
						"extra-data", extraDataJSONObject.toString());
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					deletionSystemEventElement.addAttribute(
						"extra-data", systemEvent.getExtraData());
				}
			}
			else {
				deletionSystemEventElement.addAttribute(
					"extra-data", systemEvent.getExtraData());
			}

			deletionSystemEventElement.addAttribute(
				"group-id", String.valueOf(systemEvent.getGroupId()));

			if (systemEvent.getReferrerClassNameId() > 0) {
				deletionSystemEventElement.addAttribute(
					"referrer-class-name",
					PortalUtil.getClassName(
						systemEvent.getReferrerClassNameId()));
			}

			deletionSystemEventElement.addAttribute(
				"uuid", systemEvent.getClassUuid());
		}

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		manifestSummary.incrementModelDeletionCount(
			new StagedModelType(
				systemEvent.getClassNameId(),
				systemEvent.getReferrerClassNameId()));
	}

	private List<Long> _exportDeletionSystemEvents(
			PortletDataContext portletDataContext, Element rootElement,
			Set<StagedModelType> deletionSystemEventStagedModelTypes)
		throws Exception {

		List<Long> systemEventIds = new ArrayList<>();

		ActionableDynamicQuery actionableDynamicQuery =
			_systemEventLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> doAddCriteria(
				portletDataContext, deletionSystemEventStagedModelTypes,
				dynamicQuery));
		actionableDynamicQuery.setCompanyId(portletDataContext.getCompanyId());
		actionableDynamicQuery.setPerformActionMethod(
			(SystemEvent systemEvent) -> {
				_exportDeletionSystemEvent(
					rootElement, portletDataContext, systemEvent);

				systemEventIds.add(systemEvent.getSystemEventId());
			});

		actionableDynamicQuery.performActions();

		return systemEventIds;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeletionSystemEventExporterImpl.class);

	@Reference
	private ExportImportProcessCallbackRegistry
		_exportImportProcessCallbackRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private SystemEventLocalService _systemEventLocalService;

	private class DeleteSystemEventsCallable implements Callable<Void> {

		public DeleteSystemEventsCallable(List<Long> systemEventIds) {
			_systemEventIds = systemEventIds;
		}

		@Override
		public Void call() throws PortalException {
			for (Long systemEventId : _systemEventIds) {
				_deleteSystemEvent(systemEventId);
			}

			return null;
		}

		private void _deleteSystemEvent(long systemEventId)
			throws PortalException {

			try {
				_systemEventLocalService.deleteSystemEvent(systemEventId);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to delete system event. The system events " +
							"will be cleaned up by a scheduled process.",
						portalException);
				}
			}
		}

		private final List<Long> _systemEventIds;

	}

}