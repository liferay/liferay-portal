/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.exportimport.portlet.preferences.processor.base.BaseExportImportPortletPreferencesProcessor;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	property = "jakarta.portlet.name=" + JournalContentPortletKeys.JOURNAL_CONTENT,
	service = ExportImportPortletPreferencesProcessor.class
)
public class JournalContentExportImportPortletPreferencesProcessor
	extends BaseExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return Collections.emptyList();
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return Collections.emptyList();
	}

	@Override
	public void processExportPortletPreferences(
			long companyId, PortletPreferences portletPreferences)
		throws PortletDataException {

		_exportImportPortletPreferencesProcessorHelper.
			updateGroupExportPortletPreferencesExternalReferenceCode(
				companyId, "groupExternalReferenceCode", portletPreferences);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			Portlet portlet = _portletLocalService.getPortletById(
				portletDataContext.getCompanyId(),
				portletDataContext.getPortletId());

			updateExportPortletPreferencesExternalReferenceCodes(
				portletDataContext, portlet, portletPreferences,
				"groupExternalReferenceCode", Group.class.getName());

			return portletPreferences;
		}
		catch (Exception exception) {
			PortletDataException portletDataException =
				new PortletDataException(
					"Unable to update portlet preferences during export",
					exception);

			portletDataException.setPortletId(
				JournalContentPortletKeys.JOURNAL_CONTENT);
			portletDataException.setType(
				PortletDataException.EXPORT_PORTLET_DATA);

			throw portletDataException;
		}
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			Company company = _companyLocalService.getCompanyById(
				portletDataContext.getCompanyId());

			Group companyGroup = company.getGroup();

			updateImportPortletPreferencesExternalReferenceCodes(
				portletDataContext, portletPreferences,
				"groupExternalReferenceCode", Group.class,
				companyGroup.getGroupId());

			return portletPreferences;
		}
		catch (Exception exception) {
			PortletDataException portletDataException =
				new PortletDataException(
					"Unable to update portlet preferences during import",
					exception);

			portletDataException.setPortletId(
				JournalContentPortletKeys.JOURNAL_CONTENT);
			portletDataException.setType(
				PortletDataException.IMPORT_PORTLET_DATA);

			throw portletDataException;
		}
	}

	@Override
	protected String getExportPortletPreferencesValue(
		PortletDataContext portletDataContext, Portlet portlet,
		String className, long primaryKeyLong) {

		return null;
	}

	@Override
	protected String getImportPortletPreferencesNewExternalReferenceCode(
		PortletDataContext portletDataContext, Class<?> clazz,
		long companyGroupId, Map<String, String[]> primaryKeys,
		String externalReferenceCode) {

		if (!clazz.equals(Group.class)) {
			return null;
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, portletDataContext.getCompanyId());

		if (group == null) {
			return null;
		}

		return externalReferenceCode;
	}

	@Override
	protected Long getImportPortletPreferencesNewValue(
		PortletDataContext portletDataContext, Class<?> clazz,
		long companyGroupId, Map<Long, Long> primaryKeys,
		String portletPreferencesOldValue) {

		return null;
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ExportImportPortletPreferencesProcessorHelper
		_exportImportPortletPreferencesProcessorHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

}