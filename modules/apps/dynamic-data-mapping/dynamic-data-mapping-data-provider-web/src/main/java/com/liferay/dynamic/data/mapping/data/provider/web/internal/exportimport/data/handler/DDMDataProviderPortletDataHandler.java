/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.exportimport.data.handler;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(
	property = "jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_DATA_PROVIDER,
	service = PortletDataHandler.class
)
public class DDMDataProviderPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "ddm-data-provider";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		portletDataContext.addPortletPermissions(
			DDMDataProviderInstance.class.getName());

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		ExportActionableDynamicQuery exportActionableDynamicQuery =
			_ddmDataProviderInstanceStagedModelRepository.
				getExportActionableDynamicQuery(portletDataContext);

		exportActionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataLocalized(true);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(DDMDataProviderInstance.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "ddm-data-provider", true, false, null,
				DDMDataProviderInstance.class.getName()));
		setPublishToLiveByDefault(true);
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				DDMDataProviderPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_ddmDataProviderInstanceStagedModelRepository.deleteStagedModels(
			portletDataContext);

		return portletPreferences;
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortletPermissions(
			DDMDataProviderInstance.class.getName());

		Element ddmDataProviderInstancesElement =
			portletDataContext.getImportDataGroupElement(
				DDMDataProviderInstance.class);

		List<Element> ddmDataProviderInstanceElements =
			ddmDataProviderInstancesElement.elements();

		for (Element ddmDataProviderInstanceElement :
				ddmDataProviderInstanceElements) {

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, ddmDataProviderInstanceElement);
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
					new StagedModelType(DDMDataProviderInstance.class.getName())
				});

			return;
		}

		ActionableDynamicQuery entryExportActionableDynamicQuery =
			_ddmDataProviderInstanceStagedModelRepository.
				getExportActionableDynamicQuery(portletDataContext);

		entryExportActionableDynamicQuery.performCount();
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance)"
	)
	private StagedModelRepository<DDMDataProviderInstance>
		_ddmDataProviderInstanceStagedModelRepository;

	@Reference
	private Staging _staging;

}