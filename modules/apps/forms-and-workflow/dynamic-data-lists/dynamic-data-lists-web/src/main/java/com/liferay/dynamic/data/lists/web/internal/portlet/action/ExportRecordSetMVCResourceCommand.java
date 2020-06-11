/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.lists.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.exporter.DDLExporter;
import com.liferay.dynamic.data.lists.exporter.DDLExporterFactory;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordSetService;
import com.liferay.dynamic.data.lists.web.configuration.DDLWebConfiguration;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.lists.web.configuration.DDLWebConfiguration",
	immediate = true,
	property = {
		"javax.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS,
		"javax.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
		"mvc.command.name=exportRecordSet"
	},
	service = MVCResourceCommand.class
)
public class ExportRecordSetMVCResourceCommand extends BaseMVCResourceCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_ddlWebConfiguration = ConfigurableUtil.createConfigurable(
			DDLWebConfiguration.class, properties);
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long recordSetId = ParamUtil.getLong(resourceRequest, "recordSetId");

		DDLRecordSet recordSet = _ddlRecordSetService.getRecordSet(recordSetId);

		String fileExtension = ParamUtil.getString(
			resourceRequest, "fileExtension");

		if (StringUtil.equals(fileExtension, "csv") &&
			StringUtil.equals(_ddlWebConfiguration.csvExport(), "disabled")) {

			return;
		}

		String fileName =
			recordSet.getName(themeDisplay.getLocale()) + CharPool.PERIOD +
				fileExtension;

		DDLExporter exporter = _ddlExporterFactory.getDDLExporter(
			fileExtension);

		exporter.setLocale(themeDisplay.getLocale());

		byte[] bytes = exporter.export(
			recordSetId, WorkflowConstants.STATUS_APPROVED);

		String contentType = MimeTypesUtil.getContentType(fileName);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, fileName, bytes, contentType);
	}

	@Reference(unbind = "-")
	protected void setDDLExporterFactory(
		DDLExporterFactory ddlExporterFactory) {

		_ddlExporterFactory = ddlExporterFactory;
	}

	@Reference(unbind = "-")
	protected void setDDLRecordSetService(
		DDLRecordSetService ddlRecordSetService) {

		_ddlRecordSetService = ddlRecordSetService;
	}

	private DDLExporterFactory _ddlExporterFactory;
	private DDLRecordSetService _ddlRecordSetService;
	private volatile DDLWebConfiguration _ddlWebConfiguration;

}