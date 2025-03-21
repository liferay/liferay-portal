/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.taglib.servlet.taglib;

import com.liferay.data.engine.renderer.DataLayoutRenderer;
import com.liferay.data.engine.renderer.DataLayoutRendererContext;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.dto.v2_0.DataRecord;
import com.liferay.data.engine.rest.resource.v2_0.DataRecordResource;
import com.liferay.data.engine.taglib.internal.servlet.taglib.util.DataLayoutTaglibUtil;
import com.liferay.data.engine.taglib.servlet.taglib.base.BaseDataLayoutRendererTag;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.util.Collections;
import java.util.Map;

/**
 * @author Jeyvison Nascimento
 */
public class DataLayoutRendererTag extends BaseDataLayoutRendererTag {

	@Override
	public int doStartTag() throws JspException {
		int result = super.doStartTag();

		setNamespacedAttribute(getRequest(), "content", _getContent());

		return result;
	}

	private String _getContent() {
		String content = StringPool.BLANK;

		try {
			DataLayoutRendererContext dataLayoutRendererContext =
				new DataLayoutRendererContext();

			dataLayoutRendererContext.setContainerId(getContainerId());

			if (Validator.isNotNull(getContentType())) {
				dataLayoutRendererContext.setContentType(getContentType());
			}

			HttpServletRequest httpServletRequest = getRequest();

			if (Validator.isNotNull(getDataRecordId())) {
				dataLayoutRendererContext.setDataRecordValues(
					_getDataRecordValues(
						getDataRecordId(), httpServletRequest));
			}
			else {
				dataLayoutRendererContext.setDataRecordValues(
					getDataRecordValues());
			}

			if (Validator.isNotNull(getDefaultLanguageId())) {
				dataLayoutRendererContext.setDefaultLanguageId(
					getDefaultLanguageId());
			}

			dataLayoutRendererContext.setDisableFieldRepetition(
				getDisableFieldRepetition());
			dataLayoutRendererContext.setDisplayType(getDisplayType());
			dataLayoutRendererContext.setHttpServletRequest(httpServletRequest);
			dataLayoutRendererContext.setHttpServletResponse(
				PortalUtil.getHttpServletResponse(
					(PortletResponse)httpServletRequest.getAttribute(
						JavaConstants.JAVAX_PORTLET_RESPONSE)));

			if (Validator.isNotNull(getLanguageId())) {
				dataLayoutRendererContext.setLanguageId(getLanguageId());
			}
			else {
				dataLayoutRendererContext.setLanguageId(
					LanguageUtil.getLanguageId(
						PortalUtil.getLocale(httpServletRequest)));
			}

			dataLayoutRendererContext.setPersistDefaultValues(
				getPersistDefaultValues());
			dataLayoutRendererContext.setPersisted(getPersisted());
			dataLayoutRendererContext.setPortletNamespace(getNamespace());
			dataLayoutRendererContext.setReadOnly(getReadOnly());
			dataLayoutRendererContext.setSubmittable(getSubmittable());

			if (Validator.isNotNull(getDataLayoutId())) {
				content = _renderDataLayout(
					getDataLayoutId(), dataLayoutRendererContext);
			}
			else if (Validator.isNotNull(getDataDefinitionId())) {
				DataDefinition dataDefinition =
					DataLayoutTaglibUtil.getDataDefinition(
						getDataDefinitionId(), httpServletRequest);

				DataLayout dataLayout = dataDefinition.getDefaultDataLayout();

				if (dataLayout != null) {
					content = _renderDataLayout(
						dataLayout.getId(), dataLayoutRendererContext);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return content;
	}

	private Map<String, Object> _getDataRecordValues(
			Long dataRecordId, HttpServletRequest httpServletRequest)
		throws Exception {

		if (Validator.isNull(dataRecordId)) {
			return Collections.emptyMap();
		}

		DataRecordResource.Factory dataRecordResourceFactory =
			_dataRecordResourceFactorySnapshot.get();

		DataRecordResource.Builder dataRecordResourceBuilder =
			dataRecordResourceFactory.create();

		DataRecordResource dataRecordResource = dataRecordResourceBuilder.user(
			PortalUtil.getUser(httpServletRequest)
		).build();

		DataRecord dataRecord = dataRecordResource.getDataRecord(dataRecordId);

		return dataRecord.getDataRecordValues();
	}

	private String _renderDataLayout(
			Long dataLayoutId,
			DataLayoutRendererContext dataLayoutRendererContext)
		throws Exception {

		DataLayoutRenderer dataLayoutRenderer =
			_dataLayoutRendererSnapshot.get();

		return dataLayoutRenderer.render(
			dataLayoutId, dataLayoutRendererContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataLayoutRendererTag.class);

	private static final Snapshot<DataLayoutRenderer>
		_dataLayoutRendererSnapshot = new Snapshot<>(
			DataLayoutRendererTag.class, DataLayoutRenderer.class);
	private static final Snapshot<DataRecordResource.Factory>
		_dataRecordResourceFactorySnapshot = new Snapshot<>(
			DataLayoutRendererTag.class, DataRecordResource.Factory.class);

}