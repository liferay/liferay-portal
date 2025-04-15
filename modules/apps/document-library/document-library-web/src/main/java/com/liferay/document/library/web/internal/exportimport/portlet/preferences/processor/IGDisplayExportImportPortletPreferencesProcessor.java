/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
	service = ExportImportPortletPreferencesProcessor.class
)
public class IGDisplayExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		List<Capability> exportCapabilities =
			_dlExportImportPortletPreferencesProcessor.getExportCapabilities();

		exportCapabilities.add(_igDisplayExportCapability);

		return exportCapabilities;
	}

	@Override
	public List<Capability> getImportCapabilities() {
		List<Capability> importCapabilities =
			_dlExportImportPortletPreferencesProcessor.getImportCapabilities();

		importCapabilities.add(_igDisplayImportCapability);

		return importCapabilities;
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return _dlExportImportPortletPreferencesProcessor.
			processExportPortletPreferences(
				portletDataContext, portletPreferences);
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return _dlExportImportPortletPreferencesProcessor.
			processImportPortletPreferences(
				portletDataContext, portletPreferences);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY + ")"
	)
	private ExportImportPortletPreferencesProcessor
		_dlExportImportPortletPreferencesProcessor;

	@Reference(target = "(name=IGDisplayExportCapability)")
	private Capability _igDisplayExportCapability;

	@Reference(target = "(name=IGDisplayImportCapability)")
	private Capability _igDisplayImportCapability;

}