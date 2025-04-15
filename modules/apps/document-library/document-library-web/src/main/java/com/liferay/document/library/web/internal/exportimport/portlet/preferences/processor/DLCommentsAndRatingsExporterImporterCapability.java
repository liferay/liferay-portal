/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.portlet.preferences.processor.Capability;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Cristina González
 */
@Component(service = Capability.class)
public class DLCommentsAndRatingsExporterImporterCapability
	implements Capability {

	@Override
	public PortletPreferences process(
		PortletDataContext portletDataContext,
		PortletPreferences portletPreferences) {

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.COMMENTS,
			new String[] {Boolean.TRUE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.RATINGS,
			new String[] {Boolean.TRUE.toString()});

		return portletPreferences;
	}

}