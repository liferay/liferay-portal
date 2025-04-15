/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.portlet.data.handler.helper.PortletDataHandlerHelper;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.segments.constants.SegmentsConstants;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.model.SegmentsEntry;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS,
	service = PortletDataHandler.class
)
public class SegmentsPortletDataHandler extends BasePortletDataHandler {

	public static final String[] CLASS_NAMES = {SegmentsEntry.class.getName()};

	public static final String NAMESPACE = "segments";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public String getServiceName() {
		return SegmentsConstants.SERVICE_NAME;
	}

	@Override
	public boolean validateSchemaVersion(String schemaVersion) {
		return _portletDataHandlerHelper.validateSchemaVersion(
			schemaVersion, getSchemaVersion());
	}

	@Activate
	protected void activate() {
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(SegmentsEntry.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "segments", true, false, null,
				SegmentsEntry.class.getName()));
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				SegmentsPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_segmentsEntryStagedModelRepository.deleteStagedModels(
			portletDataContext);

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "segments")) {
			return getExportDataRootElementString(rootElement);
		}

		portletDataContext.addPortletPermissions(
			SegmentsConstants.RESOURCE_NAME);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		ActionableDynamicQuery segmentsEntryActionableDynamicQuery =
			_segmentsEntryStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		segmentsEntryActionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "segments")) {
			return null;
		}

		portletDataContext.importPortletPermissions(
			SegmentsConstants.RESOURCE_NAME);

		Element segmentsEntriesElement =
			portletDataContext.getImportDataGroupElement(SegmentsEntry.class);

		List<Element> segmentsEntryElements = segmentsEntriesElement.elements();

		for (Element segmentsEntryElement : segmentsEntryElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, segmentsEntryElement);
		}

		return null;
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
					new StagedModelType(SegmentsEntry.class.getName())
				});

			return;
		}

		ActionableDynamicQuery segmentsEntryExportActionableDynamicQuery =
			_segmentsEntryStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		segmentsEntryExportActionableDynamicQuery.performCount();
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private PortletDataHandlerHelper _portletDataHandlerHelper;

	@Reference(
		target = "(model.class.name=com.liferay.segments.model.SegmentsEntry)",
		unbind = "-"
	)
	private StagedModelRepository<SegmentsEntry>
		_segmentsEntryStagedModelRepository;

	@Reference
	private Staging _staging;

}