/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.announcements.web.internal.util.AnnouncementsUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	property = "jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS,
	service = ExportImportPortletPreferencesProcessor.class
)
public class AnnouncementsExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return null;
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return null;
	}

	@Override
	public void processExportPortletPreferences(
			long companyId, PortletPreferences portletPreferences)
		throws PortletDataException {

		_updateSelectedScopeGroupExternalReferenceCodes(
			companyId, portletPreferences);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		_updateSelectedScopeGroupExternalReferenceCodes(
			portletDataContext.getCompanyId(), portletPreferences);

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
		PortletDataContext portletDataContext,
		PortletPreferences portletPreferences) {

		return portletPreferences;
	}

	private void _updateSelectedScopeGroupExternalReferenceCodes(
			long companyId, PortletPreferences portletPreferences)
		throws PortletDataException {

		String selectedScopeGroupExternalReferenceCodes =
			portletPreferences.getValue(
				"selectedScopeGroupExternalReferenceCodes", null);

		if (Validator.isBlank(selectedScopeGroupExternalReferenceCodes)) {
			return;
		}

		try {
			List<String> externalReferenceCodes =
				AnnouncementsUtil.toStringList(
					selectedScopeGroupExternalReferenceCodes);

			List<String> newExternalReferenceCodes = new ArrayList<>(
				externalReferenceCodes.size());

			for (String externalReferenceCode : externalReferenceCodes) {
				newExternalReferenceCodes.add(
					_exportImportPortletPreferencesProcessorHelper.
						getGroupExportPortletPreferencesExternalReferenceCode(
							companyId, externalReferenceCode));
			}

			portletPreferences.setValue(
				"selectedScopeGroupExternalReferenceCodes",
				AnnouncementsUtil.toJSON(newExternalReferenceCodes));
		}
		catch (Exception exception) {
			throw new PortletDataException(
				"Unable to update portlet preferences during export",
				exception);
		}
	}

	@Reference
	private ExportImportPortletPreferencesProcessorHelper
		_exportImportPortletPreferencesProcessorHelper;

}