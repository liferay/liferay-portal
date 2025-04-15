/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.exportimport.data.handler;

import com.liferay.dynamic.data.mapping.constants.DDMConstants;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = "jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
	service = PortletDataHandler.class
)
public class DDMFormAdminPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "forms";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataLocalized(true);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(DDMDataProviderInstance.class),
			new StagedModelType(DDMFormInstanceRecord.class),
			new StagedModelType(DDMFormInstance.class));

		PortletDataHandlerControl[] formsPortletDataHandlerControlChildren = {
			new PortletDataHandlerBoolean(
				NAMESPACE, "form-entries", true, false, null,
				DDMFormInstanceRecord.class.getName())
		};

		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "ddm-data-provider", true, false, null,
				DDMDataProviderInstance.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "forms", true, false,
				formsPortletDataHandlerControlChildren,
				DDMFormInstance.class.getName()));

		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				DDMFormAdminPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_formInstanceStagedModelRepository.deleteStagedModels(
			portletDataContext);

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		portletDataContext.addPortletPermissions(DDMConstants.RESOURCE_NAME);

		Element rootElement = addExportDataRootElement(portletDataContext);

		if (portletDataContext.getBooleanParameter(
				NAMESPACE, "ddm-data-provider")) {

			ActionableDynamicQuery
				ddmDataProviderInstanceActionableDynamicQuery =
					_ddmDataProviderInstanceStagedModelRepository.
						getExportActionableDynamicQuery(portletDataContext);

			ddmDataProviderInstanceActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "forms")) {
			ActionableDynamicQuery formInstanceActionableDynamicQuery =
				_formInstanceStagedModelRepository.
					getExportActionableDynamicQuery(portletDataContext);

			formInstanceActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "form-entries")) {
			ActionableDynamicQuery recordActionableDynamicQuery =
				_formInstanceRecordStagedModelRepository.
					getExportActionableDynamicQuery(portletDataContext);

			recordActionableDynamicQuery.performActions();
		}

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortletPermissions(DDMConstants.RESOURCE_NAME);

		if (portletDataContext.getBooleanParameter(
				NAMESPACE, "ddm-data-provider")) {

			Element dataProviderInstancesElement =
				portletDataContext.getImportDataGroupElement(
					DDMDataProviderInstance.class);

			List<Element> dataProviderInstanceElements =
				dataProviderInstancesElement.elements();

			for (Element dataProviderInstanceElement :
					dataProviderInstanceElements) {

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, dataProviderInstanceElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "forms")) {
			Element formInstancesElement =
				portletDataContext.getImportDataGroupElement(
					DDMFormInstance.class);

			List<Element> formInstanceElements =
				formInstancesElement.elements();

			for (Element formInstanceElement : formInstanceElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, formInstanceElement);
			}

			Element structuresElement =
				portletDataContext.getImportDataGroupElement(
					DDMStructure.class);

			List<Element> structureElements = structuresElement.elements();

			for (Element structureElement : structureElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, structureElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "form-entries")) {
			Element formInstanceRecordsElement =
				portletDataContext.getImportDataGroupElement(
					DDMFormInstanceRecord.class);

			List<Element> formInstanceRecordElements =
				formInstanceRecordsElement.elements();

			for (Element formInstanceRecordElement :
					formInstanceRecordElements) {

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, formInstanceRecordElement);
			}
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
						DDMDataProviderInstance.class.getName()),
					new StagedModelType(DDMFormInstance.class.getName()),
					new StagedModelType(DDMFormInstanceRecord.class.getName())
				});

			return;
		}

		ActionableDynamicQuery ddmDataProviderInstanceActionableDynamicQuery =
			_ddmDataProviderInstanceStagedModelRepository.
				getExportActionableDynamicQuery(portletDataContext);

		ddmDataProviderInstanceActionableDynamicQuery.performCount();

		ActionableDynamicQuery formInstanceActionableDynamicQuery =
			_formInstanceStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		formInstanceActionableDynamicQuery.performCount();

		ActionableDynamicQuery recordActionableDynamicQuery =
			_formInstanceRecordStagedModelRepository.
				getExportActionableDynamicQuery(portletDataContext);

		recordActionableDynamicQuery.performCount();
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance)"
	)
	private StagedModelRepository<DDMDataProviderInstance>
		_ddmDataProviderInstanceStagedModelRepository;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord)"
	)
	private StagedModelRepository<DDMFormInstanceRecord>
		_formInstanceRecordStagedModelRepository;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstance)"
	)
	private StagedModelRepository<DDMFormInstance>
		_formInstanceStagedModelRepository;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Staging _staging;

}