/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.dashboard.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.commerce.dashboard.web.internal.constants.CommerceDashboardPortletKeys;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michele Vigilante
 */
@Component(
	property = "jakarta.portlet.name=" + CommerceDashboardPortletKeys.COMMERCE_DASHBOARD_FORECASTS_CHART,
	service = ExportImportPortletPreferencesProcessor.class
)
public class
	CommerceDashboardForecastsChartExportImportPortletPreferencesProcessor
		implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(_exportCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(_importCapability);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return null;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return null;
	}

	@Reference(target = "(name=CommonPortletDisplayTemplateExportCapability)")
	private Capability _exportCapability;

	@Reference(target = "(name=CommonPortletDisplayTemplateImportCapability)")
	private Capability _importCapability;

}