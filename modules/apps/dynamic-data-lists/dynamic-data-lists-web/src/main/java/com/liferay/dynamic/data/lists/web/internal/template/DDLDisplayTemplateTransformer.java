/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.template;

import com.liferay.dynamic.data.lists.constants.DDLConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.web.internal.configuration.DDLWebConfigurationKeys;
import com.liferay.dynamic.data.lists.web.internal.configuration.DDLWebConfigurationUtil;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.templateparser.Transformer;

import jakarta.portlet.RenderRequest;

import java.util.Map;

/**
 * @author Marcellus Tavares
 */
public class DDLDisplayTemplateTransformer {

	public DDLDisplayTemplateTransformer(
		long ddmTemplateId, DDLRecordSet recordSet, ThemeDisplay themeDisplay,
		RenderRequest renderRequest) {

		_ddmTemplateId = ddmTemplateId;
		_recordSet = recordSet;
		_themeDisplay = themeDisplay;
		_renderRequest = renderRequest;
	}

	public String transform() throws Exception {
		Transformer transformer = TransformerHolder.getTransformer();

		Map<String, Object> contextObjects = HashMapBuilder.<String, Object>put(
			DDLConstants.RESERVED_DDM_STRUCTURE_ID,
			_recordSet.getDDMStructureId()
		).put(
			DDLConstants.RESERVED_DDM_TEMPLATE_ID, _ddmTemplateId
		).put(
			DDLConstants.RESERVED_RECORD_SET_DESCRIPTION,
			_recordSet.getDescription(_themeDisplay.getLocale())
		).put(
			DDLConstants.RESERVED_RECORD_SET_ID, _recordSet.getRecordSetId()
		).put(
			DDLConstants.RESERVED_RECORD_SET_NAME,
			_recordSet.getName(_themeDisplay.getLocale())
		).put(
			TemplateConstants.TEMPLATE_ID, _ddmTemplateId
		).put(
			"viewMode",
			() -> {
				if (_renderRequest != null) {
					return ParamUtil.getString(
						_renderRequest, "viewMode", Constants.VIEW);
				}

				return Constants.VIEW;
			}
		).build();

		DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.getTemplate(
			_ddmTemplateId);

		contextObjects.put(
			TemplateConstants.CLASS_NAME_ID, ddmTemplate.getClassNameId());

		TemplateHandler templateHandler =
			TemplateHandlerRegistryUtil.getTemplateHandler(
				DDLRecordSet.class.getName());

		contextObjects.putAll(templateHandler.getCustomContextObjects());

		return transformer.transform(
			_themeDisplay, contextObjects, ddmTemplate.getScript(),
			ddmTemplate.getLanguage(), new UnsyncStringWriter(),
			PortalUtil.getHttpServletRequest(_renderRequest),
			_themeDisplay.getResponse());
	}

	private final long _ddmTemplateId;
	private final DDLRecordSet _recordSet;
	private final RenderRequest _renderRequest;
	private final ThemeDisplay _themeDisplay;

	private static class TransformerHolder {

		public static Transformer getTransformer() {
			return _transformer;
		}

		private static final Transformer _transformer = new Transformer(
			DDLWebConfigurationKeys.DYNAMIC_DATA_LISTS_ERROR_TEMPLATE, true) {

			@Override
			protected String getErrorTemplateId(
				String errorTemplatePropertyKey, String langType) {

				return DDLWebConfigurationUtil.get(
					errorTemplatePropertyKey, new Filter(langType));
			}

		};

	}

}