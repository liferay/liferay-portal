/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.recent.bloggers.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.blogs.recent.bloggers.constants.RecentBloggersPortletKeys;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "jakarta.portlet.name=" + RecentBloggersPortletKeys.RECENT_BLOGGERS,
	service = ExportImportPortletPreferencesProcessor.class
)
public class RecentBloggersExportImportPortletPreferencesProcessor
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
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			return _updateExportPortletPreferences(
				portletDataContext, portletDataContext.getPortletId(),
				portletPreferences);
		}
		catch (Exception exception) {
			throw new PortletDataException(
				"Unable to update portlet preferences during export",
				exception);
		}
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			return _updateImportPortletPreferences(
				portletDataContext, portletPreferences);
		}
		catch (Exception exception) {
			throw new PortletDataException(
				"Unable to update portlet preferences during import",
				exception);
		}
	}

	private PortletPreferences _updateExportPortletPreferences(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		long organizationId = GetterUtil.getLong(
			portletPreferences.getValue("organizationId", null));

		if (organizationId > 0) {
			Portlet portlet = _portletLocalService.getPortletById(
				portletDataContext.getCompanyId(), portletId);

			Function<String, String> exportPortletPreferencesNewValueFunction =
				primaryKey -> {
					long primaryKeyLong = GetterUtil.getLong(primaryKey);

					Organization organization =
						_organizationLocalService.fetchOrganization(
							primaryKeyLong);

					if (organization == null) {
						return null;
					}

					portletDataContext.addReferenceElement(
						portlet, portletDataContext.getExportDataRootElement(),
						organization,
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);

					return organization.getUuid();
				};

			_exportImportPortletPreferencesProcessorHelper.
				updateExportPortletPreferencesClassPKs(
					portletDataContext, portlet, portletPreferences,
					"organizationId", Organization.class.getName(),
					exportPortletPreferencesNewValueFunction);
		}

		return portletPreferences;
	}

	private PortletPreferences _updateImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		Company company = _companyLocalService.getCompanyById(
			portletDataContext.getCompanyId());

		Group companyGroup = company.getGroup();

		Map<Long, Long> primaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Organization.class);

		Function<String, Long> importPortletPreferencesNewValueFunction =
			portletPreferencesOldValue -> {
				if (Validator.isNumber(portletPreferencesOldValue)) {
					long oldPrimaryKey = GetterUtil.getLong(
						portletPreferencesOldValue);

					return MapUtil.getLong(
						primaryKeys, oldPrimaryKey, oldPrimaryKey);
				}

				Organization organization =
					_organizationLocalService.
						fetchOrganizationByUuidAndCompanyId(
							portletPreferencesOldValue,
							portletDataContext.getCompanyId());

				if (organization != null) {
					return organization.getOrganizationId();
				}

				return null;
			};

		_exportImportPortletPreferencesProcessorHelper.
			updateImportPortletPreferencesClassPKs(
				portletDataContext, portletPreferences, "organizationId",
				companyGroup.getGroupId(),
				importPortletPreferencesNewValueFunction);

		return portletPreferences;
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ExportImportPortletPreferencesProcessorHelper
		_exportImportPortletPreferencesProcessorHelper;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

}