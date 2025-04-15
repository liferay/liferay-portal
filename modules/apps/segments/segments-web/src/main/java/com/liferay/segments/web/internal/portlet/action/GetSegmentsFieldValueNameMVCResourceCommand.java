/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizerRegistry;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS,
		"mvc.command.name=/segments/get_segments_field_value_name"
	},
	service = MVCResourceCommand.class
)
public class GetSegmentsFieldValueNameMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			getFieldValueNameJSONObject(
				ParamUtil.getString(resourceRequest, "entityName"),
				ParamUtil.getString(resourceRequest, "fieldName"),
				ParamUtil.getString(resourceRequest, "fieldValue"),
				_portal.getLocale(resourceRequest)));
	}

	protected JSONObject getFieldValueNameJSONObject(
		String entityName, String fieldName, String fieldValue, Locale locale) {

		return JSONUtil.put(
			"fieldValueName",
			_getFieldValueName(entityName, fieldName, fieldValue, locale));
	}

	private String _getFieldValueName(
		String entityName, String fieldName, String fieldValue, Locale locale) {

		SegmentsFieldCustomizer segmentsFieldCustomizer =
			_segmentsFieldCustomizerRegistry.getSegmentsFieldCustomizer(
				entityName, fieldName);

		if (segmentsFieldCustomizer == null) {
			return null;
		}

		return segmentsFieldCustomizer.getFieldValueName(fieldValue, locale);
	}

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsFieldCustomizerRegistry _segmentsFieldCustomizerRegistry;

}