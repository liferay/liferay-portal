/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.internal.page.ratings.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportProcessCallbackRegistry;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;
import com.liferay.ratings.page.ratings.constants.PageRatingsPortletKeys;

import jakarta.portlet.PortletPreferences;

import java.util.List;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gergely Mathe
 */
@Component(
	property = "jakarta.portlet.name=" + PageRatingsPortletKeys.PAGE_RATINGS,
	service = PortletDataHandler.class
)
public class PageRatingsPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "ratings";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataAlwaysStaged(true);
		setDataLevel(DataLevel.PORTLET_INSTANCE);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(RatingsEntry.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "ratings-entries", true, false, null,
				RatingsEntry.class.getName(),
				StagedModelType.REFERRER_CLASS_NAME_ALL));
		setPublishToLiveByDefault(true);
		setStagingControls(getExportControls());
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		if (!portletDataContext.getBooleanParameter(
				NAMESPACE, "ratings-entries") ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			return getExportDataRootElementString(rootElement);
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_getRatingsEntryActionableDynamicQuery(portletDataContext);

		actionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		PortletDataContext clonedPortletDataContext =
			_portletDataContextFactory.clonePortletDataContext(
				portletDataContext);

		_exportImportProcessCallbackRegistry.registerCallback(
			portletDataContext.getExportImportProcessId(),
			new ImportRatingsCallable(clonedPortletDataContext));

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			_getRatingsEntryCountActionableDynamicQuery(portletDataContext);

		actionableDynamicQuery.performCount();
	}

	private long _getGroupId(RatingsEntry ratingsEntry) throws PortalException {
		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(ratingsEntry.getClassName());

		if (persistedModelLocalService == null) {
			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		PersistedModel persistedModel = null;

		try {
			persistedModel = persistedModelLocalService.getPersistedModel(
				ratingsEntry.getClassPK());
		}
		catch (NoSuchModelException noSuchModelException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchModelException);
			}

			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		if (!(persistedModel instanceof GroupedModel)) {
			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		GroupedModel groupedModel = (GroupedModel)persistedModel;

		return groupedModel.getGroupId();
	}

	private ActionableDynamicQuery _getRatingsEntryActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		ActionableDynamicQuery actionableDynamicQuery =
			_ratingsEntryLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.setPerformActionMethod(
			(RatingsEntry ratingsEntry) -> {
				long groupId = _getGroupId(ratingsEntry);

				if (groupId != portletDataContext.getScopeGroupId()) {
					return;
				}

				StagedModelDataHandlerUtil.exportStagedModel(
					portletDataContext, ratingsEntry);
			});

		return actionableDynamicQuery;
	}

	private ActionableDynamicQuery _getRatingsEntryCountActionableDynamicQuery(
			final PortletDataContext portletDataContext)
		throws Exception {

		final ExportActionableDynamicQuery exportActionableDynamicQuery =
			_ratingsEntryLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		exportActionableDynamicQuery.setPerformActionMethod(
			(RatingsEntry ratingsEntry) -> {
				long groupId = _getGroupId(ratingsEntry);

				if (groupId != portletDataContext.getScopeGroupId()) {
					return;
				}

				ManifestSummary manifestSummary =
					portletDataContext.getManifestSummary();

				manifestSummary.incrementModelAdditionCount(
					exportActionableDynamicQuery.getStagedModelType());
			});
		exportActionableDynamicQuery.setPerformCountMethod(
			new ActionableDynamicQuery.PerformCountMethod() {

				@Override
				public long performCount() throws PortalException {
					exportActionableDynamicQuery.performActions();

					ManifestSummary manifestSummary =
						portletDataContext.getManifestSummary();

					StagedModelType stagedModelType =
						exportActionableDynamicQuery.getStagedModelType();

					long modelDeletionCount =
						_exportImportHelper.getModelDeletionCount(
							portletDataContext, stagedModelType);

					manifestSummary.addModelDeletionCount(
						stagedModelType, modelDeletionCount);

					manifestSummary.addModelAdditionCount(stagedModelType, 0);

					return manifestSummary.getModelAdditionCount(
						stagedModelType);
				}

			});

		return exportActionableDynamicQuery;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PageRatingsPortletDataHandler.class);

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportProcessCallbackRegistry
		_exportImportProcessCallbackRegistry;

	@Reference
	private PortletDataContextFactory _portletDataContextFactory;

	@Reference
	private RatingsEntryLocalService _ratingsEntryLocalService;

	private static class ImportRatingsCallable implements Callable<Void> {

		public ImportRatingsCallable(PortletDataContext portletDataContext) {
			_portletDataContext = portletDataContext;
		}

		@Override
		public Void call() throws PortalException {
			if (!_portletDataContext.getBooleanParameter(
					NAMESPACE, "ratings-entries")) {

				return null;
			}

			Element entriesElement =
				_portletDataContext.getImportDataGroupElement(
					RatingsEntry.class);

			List<Element> entryElements = entriesElement.elements();

			for (Element entryElement : entryElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					_portletDataContext, entryElement);
			}

			return null;
		}

		private final PortletDataContext _portletDataContext;

	}

}