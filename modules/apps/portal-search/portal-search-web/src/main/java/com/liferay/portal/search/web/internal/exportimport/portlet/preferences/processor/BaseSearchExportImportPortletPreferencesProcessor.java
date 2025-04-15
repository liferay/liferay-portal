/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.base.BaseExportImportPortletPreferencesProcessor;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.portlet.PortletPreferences;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Felipe Lorenz
 */
public abstract class BaseSearchExportImportPortletPreferencesProcessor
	extends BaseExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(exportCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(importCapability);
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

	@Override
	protected String getExportPortletPreferencesValue(
			PortletDataContext portletDataContext, Portlet portlet,
			String className, long primaryKeyLong)
		throws Exception {

		return null;
	}

	@Override
	protected Long getImportPortletPreferencesNewValue(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<Long, Long> primaryKeys,
			String portletPreferencesOldValue)
		throws Exception {

		return null;
	}

	@Reference(target = "(name=CommonPortletDisplayTemplateExportCapability)")
	protected Capability exportCapability;

	@Reference(target = "(name=CommonPortletDisplayTemplateImportCapability)")
	protected Capability importCapability;

}