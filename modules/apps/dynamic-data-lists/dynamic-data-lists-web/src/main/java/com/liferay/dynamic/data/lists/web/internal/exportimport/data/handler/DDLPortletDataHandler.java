/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.exportimport.data.handler;

import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
import com.liferay.dynamic.data.lists.constants.DDLConstants;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
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
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS,
	service = PortletDataHandler.class
)
public class DDLPortletDataHandler extends BasePortletDataHandler {

	public static final String[] CLASS_NAMES = {
		DDLRecord.class.getName(), DDLRecordSet.class.getName()
	};

	public static final String NAMESPACE = "dynamic_data_lists";

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
		return DDLConstants.SERVICE_NAME;
	}

	@Activate
	protected void activate() {
		setDataLocalized(true);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(DDLRecord.class),
			new StagedModelType(DDLRecordSet.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "record-sets", true, false, null,
				DDLRecordSet.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "records", true, false, null,
				DDLRecord.class.getName()));
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				DDLPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_ddlRecordSetStagedModelRepository.deleteStagedModels(
			portletDataContext);

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		portletDataContext.addPortletPermissions(DDLConstants.RESOURCE_NAME);

		Element rootElement = addExportDataRootElement(portletDataContext);

		if (portletDataContext.getBooleanParameter(NAMESPACE, "record-sets")) {
			ActionableDynamicQuery recordSetActionableDynamicQuery =
				_ddlRecordSetStagedModelRepository.
					getExportActionableDynamicQuery(portletDataContext);

			recordSetActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "records")) {
			ActionableDynamicQuery recordActionableDynamicQuery =
				_ddlRecordStagedModelRepository.getExportActionableDynamicQuery(
					portletDataContext);

			recordActionableDynamicQuery.performActions();
		}

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortletPermissions(DDLConstants.RESOURCE_NAME);

		if (portletDataContext.getBooleanParameter(NAMESPACE, "record-sets")) {
			Element recordSetsElement =
				portletDataContext.getImportDataGroupElement(
					DDLRecordSet.class);

			List<Element> recordSetElements = recordSetsElement.elements();

			for (Element recordSetElement : recordSetElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, recordSetElement);
			}

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

			Element ddmTemplatesElement =
				portletDataContext.getImportDataGroupElement(DDMTemplate.class);

			List<Element> ddmTemplateElements = ddmTemplatesElement.elements();

			for (Element ddmTemplateElement : ddmTemplateElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, ddmTemplateElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "records")) {
			Element recordsElement =
				portletDataContext.getImportDataGroupElement(DDLRecord.class);

			List<Element> recordElements = recordsElement.elements();

			for (Element recordElement : recordElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, recordElement);
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
					new StagedModelType(DDLRecord.class.getName()),
					new StagedModelType(DDLRecordSet.class.getName())
				});

			return;
		}

		ActionableDynamicQuery recordSetActionableDynamicQuery =
			_ddlRecordSetStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		recordSetActionableDynamicQuery.performCount();

		ActionableDynamicQuery recordActionableDynamicQuery =
			_ddlRecordStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		recordActionableDynamicQuery.performCount();
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.lists.model.DDLRecordSet)"
	)
	private StagedModelRepository<DDLRecordSet>
		_ddlRecordSetStagedModelRepository;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.lists.model.DDLRecord)"
	)
	private StagedModelRepository<DDLRecord> _ddlRecordStagedModelRepository;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Staging _staging;

}