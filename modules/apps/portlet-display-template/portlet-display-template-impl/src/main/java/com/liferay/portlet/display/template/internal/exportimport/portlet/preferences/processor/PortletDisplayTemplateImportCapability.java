/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.internal.exportimport.portlet.preferences.processor;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.portlet.display.template.exportimport.portlet.preferences.processor.PortletDisplayTemplateRegister;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

/**
 * @author Máté Thurzó
 */
public class PortletDisplayTemplateImportCapability implements Capability {

	public PortletDisplayTemplateImportCapability(
		Portal portal, PortletLocalService portletLocalService,
		PortletDisplayTemplate portletDisplayTemplate,
		PortletDisplayTemplateRegister portletDisplayTemplateImportRegister) {

		_portal = portal;
		_portletLocalService = portletLocalService;
		_portletDisplayTemplate = portletDisplayTemplate;
		_portletDisplayTemplateImportRegister =
			portletDisplayTemplateImportRegister;
	}

	@Override
	public PortletPreferences process(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			return _importDisplayStyle(
				portletDataContext, portletDataContext.getPortletId(),
				portletPreferences);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return portletPreferences;
		}
	}

	protected long getClassNameId(
		PortletDataContext portletDataContext, String portletId) {

		Portlet portlet = _portletLocalService.getPortletById(
			portletDataContext.getCompanyId(), portletId);

		TemplateHandler templateHandler = portlet.getTemplateHandlerInstance();

		if (templateHandler == null) {
			return 0;
		}

		return _portal.getClassNameId(templateHandler.getClassName());
	}

	private PortletPreferences _importDisplayStyle(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		PortletPreferences processedPortletPreferences = portletPreferences;

		String displayStyle =
			_portletDisplayTemplateImportRegister.getDisplayStyle(
				portletDataContext, portletId, portletPreferences);

		if (Validator.isNull(displayStyle) ||
			!displayStyle.startsWith(
				PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX)) {

			return processedPortletPreferences;
		}

		StagedModelDataHandlerUtil.importReferenceStagedModels(
			portletDataContext, DDMTemplate.class);

		long displayStyleGroupId =
			_portletDisplayTemplateImportRegister.getDisplayStyleGroupId(
				portletDataContext, portletId, portletPreferences);

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = MapUtil.getLong(
			groupIds, displayStyleGroupId, displayStyleGroupId);

		DDMTemplate ddmTemplate =
			_portletDisplayTemplate.getPortletDisplayTemplateDDMTemplate(
				groupId, getClassNameId(portletDataContext, portletId),
				displayStyle, false);

		if (ddmTemplate != null) {
			portletPreferences.setValue(
				"displayStyleGroupId",
				String.valueOf(ddmTemplate.getGroupId()));
		}
		else {
			portletPreferences.setValue(
				"displayStyleGroupId", StringPool.BLANK);
		}

		return processedPortletPreferences;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletDisplayTemplateImportCapability.class);

	private final Portal _portal;
	private final PortletDisplayTemplate _portletDisplayTemplate;
	private final PortletDisplayTemplateRegister
		_portletDisplayTemplateImportRegister;
	private final PortletLocalService _portletLocalService;

}