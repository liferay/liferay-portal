/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.exportimport.portlet.preferences.processor.base.BaseExportImportPortletPreferencesProcessor;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
		return ListUtil.fromArray(
			_journalContentMetadataExporterImporterCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(
			_journalContentMetadataExporterImporterCapability,
			_referencedStagedModelImporterCapability);
	}

	@Override
	public boolean isPublishDisplayedContent() {
		return ExportImportThreadLocal.isPortletStagingInProcess();
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
			portletDataContext.addPortletPermissions(
				JournalConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			throw _getPortletDataException(
				portalException, "Unable to export portlet permissions",
				PortletDataException.EXPORT_PORTLET_PERMISSIONS);
		}

		Group articleGroup = _getArticleGroup(
			portletDataContext, portletPreferences);

		try {
			updateExportPortletPreferencesExternalReferenceCodes(
				portletDataContext,
				_portletLocalService.getPortletById(
					portletDataContext.getCompanyId(),
					portletDataContext.getPortletId()),
				portletPreferences, "groupExternalReferenceCode",
				Group.class.getName());
		}
		catch (Exception exception) {
			throw _getPortletDataException(
				exception, "Unable to update portlet preferences during export",
				PortletDataException.EXPORT_PORTLET_DATA);
		}

		_exportArticle(portletDataContext, portletPreferences, articleGroup);

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			portletDataContext.importPortletPermissions(
				JournalConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			throw _getPortletDataException(
				portalException, "Unable to import portlet permissions",
				PortletDataException.IMPORT_PORTLET_PERMISSIONS);
		}

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
			throw _getPortletDataException(
				exception, "Unable to update portlet preferences during import",
				PortletDataException.IMPORT_PORTLET_DATA);
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

	private void _exportArticle(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, Group articleGroup)
		throws PortletDataException {

		String articleExternalReferenceCode = portletPreferences.getValue(
			"articleExternalReferenceCode", null);

		String portletId = portletDataContext.getPortletId();

		if (Validator.isNull(articleExternalReferenceCode)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No journal article external reference code found in " +
						"preferences of portlet " + portletId);
			}

			return;
		}

		if (articleGroup == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No group found in preferences of portlet " + portletId);
			}

			return;
		}

		if (ExportImportThreadLocal.isStagingInProcess() &&
			!articleGroup.isStagedPortlet(JournalPortletKeys.JOURNAL)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Journal article is not staged in the site " +
						articleGroup.getName());
			}

			return;
		}

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			return;
		}

		JournalArticle article =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					articleGroup.getGroupId(), articleExternalReferenceCode,
					new int[] {
						WorkflowConstants.STATUS_APPROVED,
						WorkflowConstants.STATUS_EXPIRED,
						WorkflowConstants.STATUS_SCHEDULED
					});

		if (article == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Portlet ", portletId,
						" refers to an invalid external reference code ",
						articleExternalReferenceCode));
			}

			return;
		}

		long scopeGroupId = portletDataContext.getScopeGroupId();

		try {
			portletDataContext.setScopeGroupId(articleGroup.getGroupId());

			Element articleElement = portletDataContext.getExportDataElement(
				article);

			if (!GetterUtil.getBoolean(
					articleElement.attributeValue("articleAdded"))) {

				articleElement.addAttribute("articleAdded", "true");

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portletId, article);
			}

			_exportDDMTemplate(portletDataContext, portletPreferences, article);
		}
		finally {
			portletDataContext.setScopeGroupId(scopeGroupId);
		}
	}

	private void _exportDDMTemplate(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, JournalArticle article)
		throws PortletDataException {

		String ddmTemplateExternalReferenceCode = portletPreferences.getValue(
			"ddmTemplateExternalReferenceCode", null);

		if (Validator.isNull(ddmTemplateExternalReferenceCode)) {
			return;
		}

		DDMTemplate ddmTemplate =
			_ddmTemplateLocalService.fetchDDMTemplateByExternalReferenceCode(
				ddmTemplateExternalReferenceCode, article.getGroupId(), true);

		if ((ddmTemplate == null) ||
			Objects.equals(
				article.getDDMTemplateKey(), ddmTemplate.getTemplateKey())) {

			return;
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, article, ddmTemplate,
			PortletDataContext.REFERENCE_TYPE_STRONG);
	}

	private Group _getArticleGroup(
		PortletDataContext portletDataContext,
		PortletPreferences portletPreferences) {

		String groupExternalReferenceCode = portletPreferences.getValue(
			"groupExternalReferenceCode", null);

		if (Validator.isNull(groupExternalReferenceCode)) {
			return _groupLocalService.fetchGroup(
				portletDataContext.getScopeGroupId());
		}

		return _groupLocalService.fetchGroupByExternalReferenceCode(
			groupExternalReferenceCode, portletDataContext.getCompanyId());
	}

	private PortletDataException _getPortletDataException(
		Exception exception, String message, int type) {

		PortletDataException portletDataException = new PortletDataException(
			message, exception);

		portletDataException.setPortletId(
			JournalContentPortletKeys.JOURNAL_CONTENT);
		portletDataException.setType(type);

		return portletDataException;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentExportImportPortletPreferencesProcessor.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private ExportImportPortletPreferencesProcessorHelper
		_exportImportPortletPreferencesProcessorHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference(
		target = "(component.name=com.liferay.journal.content.web.internal.exportimport.portlet.preferences.processor.JournalContentMetadataExporterImporterCapability)"
	)
	private Capability _journalContentMetadataExporterImporterCapability;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference(target = "(name=ReferencedStagedModelImporter)")
	private Capability _referencedStagedModelImporterCapability;

}