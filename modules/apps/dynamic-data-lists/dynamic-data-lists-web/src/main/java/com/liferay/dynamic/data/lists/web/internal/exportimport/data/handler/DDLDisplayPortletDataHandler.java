/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.exportimport.data.handler;

import com.liferay.dynamic.data.lists.constants.DDLConstants;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
	service = PortletDataHandler.class
)
public class DDLDisplayPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "dynamic_data_lists";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public String getServiceName() {
		return DDLConstants.SERVICE_NAME;
	}

	@Override
	public boolean isDataPortletInstanceLevel() {
		return _ddlPortletDataHandler.isDataPortletInstanceLevel();
	}

	@Override
	public boolean isDisplayPortlet() {
		return false;
	}

	@Override
	public void prepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		_ddlPortletDataHandler.prepareManifestSummary(
			portletDataContext, portletPreferences);
	}

	@Activate
	protected void activate() {
		setDataLevel(DataLevel.PORTLET_INSTANCE);
		setDataPortletPreferences(
			"displayDDMTemplateId", "formDDMTemplateId", "recordSetId");
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

		if (portletPreferences == null) {
			return portletPreferences;
		}

		portletPreferences.setValue("displayDDMTemplateId", StringPool.BLANK);
		portletPreferences.setValue("editable", Boolean.TRUE.toString());
		portletPreferences.setValue("formDDMTemplateId", StringPool.BLANK);
		portletPreferences.setValue("formView", Boolean.FALSE.toString());
		portletPreferences.setValue("recordSetId", StringPool.BLANK);
		portletPreferences.setValue("spreadsheet", Boolean.FALSE.toString());

		return _ddlPortletDataHandler.deleteData(
			portletDataContext, portletId, portletPreferences);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS + ")"
	)
	private PortletDataHandler _ddlPortletDataHandler;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}