/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
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
import com.liferay.exportimport.portlet.preferences.processor.base.BaseExportImportPortletPreferencesProcessor;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
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
			_journalContentMetadataExporterImporterCapability, _capability);
	}

	@Override
	public boolean isPublishDisplayedContent() {
		return false;
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		String portletId = portletDataContext.getPortletId();

		try {
			portletDataContext.addPortletPermissions(
				JournalConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(
				JournalContentPortletKeys.JOURNAL_CONTENT);
			portletDataException.setType(
				PortletDataException.EXPORT_PORTLET_PERMISSIONS);

			throw portletDataException;
		}

		String articleExternalReferenceCode = portletPreferences.getValue(
			"articleExternalReferenceCode", null);

		if (articleExternalReferenceCode == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No article external reference code found in preferences " +
						"of portlet " + portletId);
			}

			return portletPreferences;
		}

		long companyId = _getCompanyId(portletDataContext);

		long articleGroupId = _getGroupId(companyId, portletPreferences);

		try {
			updateExportPortletPreferencesExternalReferenceCodes(
				portletDataContext,
				_portletLocalService.getPortletById(companyId, portletId),
				portletPreferences, "groupExternalReferenceCode",
				Group.class.getName());
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

		if (articleGroupId <= 0) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No group ID found in preferences of portlet " + portletId);
			}

			return portletPreferences;
		}

		Group group = _groupLocalService.fetchGroup(articleGroupId);

		if (group == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No group found with group ID " + articleGroupId);
			}

			return portletPreferences;
		}

		if (ExportImportThreadLocal.isStagingInProcess() &&
			!group.isStagedPortlet(JournalPortletKeys.JOURNAL)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Web content is not staged in the site " + group.getName());
			}

			return portletPreferences;
		}

		long previousScopeGroupId = portletDataContext.getScopeGroupId();

		if (articleGroupId != previousScopeGroupId) {
			portletDataContext.setScopeGroupId(articleGroupId);
		}

		JournalArticle article =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					articleGroupId, articleExternalReferenceCode,
					new int[] {
						WorkflowConstants.STATUS_APPROVED,
						WorkflowConstants.STATUS_EXPIRED,
						WorkflowConstants.STATUS_SCHEDULED
					});

		if ((article != null) &&
			Objects.equals(
				article.getStatus(), WorkflowConstants.STATUS_IN_TRASH)) {

			article = null;
		}

		if (article == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Portlet ", portletId,
						" refers to an invalid external reference code ",
						articleExternalReferenceCode));
			}

			portletDataContext.setScopeGroupId(previousScopeGroupId);

			return portletPreferences;
		}

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			portletDataContext.setScopeGroupId(previousScopeGroupId);

			return portletPreferences;
		}

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()});

		Element articleElement = portletDataContext.getExportDataElement(
			article);

		if (!GetterUtil.getBoolean(
				articleElement.attributeValue("articleAdded"))) {

			articleElement.addAttribute("articleAdded", "true");

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, portletId, article);
		}

		String defaultDDMTemplateKey = article.getDDMTemplateKey();

		String preferenceDDMTemplateKey = null;

		String preferenceDDMTemplateExternalReferenceCode =
			portletPreferences.getValue(
				"ddmTemplateExternalReferenceCode", null);

		if (Validator.isNotNull(preferenceDDMTemplateExternalReferenceCode)) {
			DDMTemplate ddmTemplate =
				_ddmTemplateLocalService.
					fetchDDMTemplateByExternalReferenceCode(
						preferenceDDMTemplateExternalReferenceCode,
						article.getGroupId(), true);

			if (ddmTemplate != null) {
				preferenceDDMTemplateKey = ddmTemplate.getTemplateKey();
			}
		}

		if (Validator.isNotNull(defaultDDMTemplateKey) &&
			Validator.isNotNull(preferenceDDMTemplateKey) &&
			!defaultDDMTemplateKey.equals(preferenceDDMTemplateKey)) {

			try {
				DDMTemplate ddmTemplate =
					_ddmTemplateLocalService.fetchTemplate(
						article.getGroupId(),
						_portal.getClassNameId(DDMStructure.class),
						preferenceDDMTemplateKey, true);

				if (ddmTemplate == null) {
					ddmTemplate = _ddmTemplateLocalService.getTemplate(
						article.getGroupId(),
						_portal.getClassNameId(DDMStructure.class),
						defaultDDMTemplateKey, true);

					portletPreferences.setValue(
						"ddmTemplateExternalReferenceCode",
						ddmTemplate.getExternalReferenceCode());
				}

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, article, ddmTemplate,
					PortletDataContext.REFERENCE_TYPE_STRONG);
			}
			catch (PortalException | ReadOnlyException exception) {
				PortletDataException portletDataException =
					new PortletDataException(exception);

				portletDataException.setPortletId(
					JournalContentPortletKeys.JOURNAL_CONTENT);
				portletDataException.setType(
					PortletDataException.EXPORT_REFERENCED_TEMPLATE);

				throw portletDataException;
			}
		}

		portletDataContext.setScopeGroupId(previousScopeGroupId);

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
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(
				JournalContentPortletKeys.JOURNAL_CONTENT);
			portletDataException.setType(
				PortletDataException.IMPORT_PORTLET_PERMISSIONS);

			throw portletDataException;
		}

		long previousScopeGroupId = portletDataContext.getScopeGroupId();

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long importGroupId = _getGroupId(
			_getCompanyId(portletDataContext), portletPreferences);

		if ((importGroupId == portletDataContext.getCompanyGroupId()) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			portletDataContext.setScopeType("company");
		}

		long groupId = MapUtil.getLong(groupIds, importGroupId, importGroupId);
		String articleExternalReferenceCode = portletPreferences.getValue(
			"articleExternalReferenceCode", null);

		Map<String, Long> articleGroupIds =
			(Map<String, Long>)portletDataContext.getNewPrimaryKeysMap(
				JournalArticle.class + ".groupId");

		if (articleGroupIds.containsKey(articleExternalReferenceCode)) {
			groupId = articleGroupIds.get(articleExternalReferenceCode);
		}

		portletDataContext.setScopeGroupId(groupId);

		try {
			if (Validator.isNotNull(articleExternalReferenceCode) &&
				(groupId != 0)) {

				Group importedArticleGroup = _groupLocalService.fetchGroup(
					groupId);

				if (importedArticleGroup == null) {
					if (_log.isDebugEnabled()) {
						_log.debug("No group found with group ID " + groupId);
					}

					return portletPreferences;
				}

				if (!ExportImportThreadLocal.isStagingInProcess() ||
					importedArticleGroup.isStagedPortlet(
						JournalPortletKeys.JOURNAL)) {

					Map<String, String> articleExternalReferenceCodes =
						(Map<String, String>)
							portletDataContext.getNewPrimaryKeysMap(
								JournalArticle.class +
									".articleExternalReferenceCode");

					articleExternalReferenceCode = MapUtil.getString(
						articleExternalReferenceCodes,
						articleExternalReferenceCode,
						articleExternalReferenceCode);

					portletPreferences.setValue(
						"articleExternalReferenceCode",
						articleExternalReferenceCode);

					portletPreferences.setValue(
						"groupExternalReferenceCode",
						importedArticleGroup.getExternalReferenceCode());
				}
			}

			String ddmTemplateExternalReferenceCode =
				portletPreferences.getValue(
					"ddmTemplateExternalReferenceCode", null);

			if (Validator.isNotNull(ddmTemplateExternalReferenceCode)) {
				Map<String, String> ddmTemplateExternalReferenceCodes =
					(Map<String, String>)
						portletDataContext.getNewPrimaryKeysMap(
							DDMTemplate.class +
								".ddmTemplateExternalReferenceCode");

				ddmTemplateExternalReferenceCode = MapUtil.getString(
					ddmTemplateExternalReferenceCodes,
					ddmTemplateExternalReferenceCode,
					ddmTemplateExternalReferenceCode);

				portletPreferences.setValue(
					"ddmTemplateExternalReferenceCode",
					ddmTemplateExternalReferenceCode);
			}
		}
		catch (ReadOnlyException readOnlyException) {
			PortletDataException portletDataException =
				new PortletDataException(readOnlyException);

			portletDataException.setPortletId(
				JournalContentPortletKeys.JOURNAL_CONTENT);
			portletDataException.setType(
				PortletDataException.UPDATE_PORTLET_PREFERENCES);

			throw portletDataException;
		}

		portletDataContext.setScopeGroupId(previousScopeGroupId);

		return portletPreferences;
	}

	@Override
	protected String getExportPortletPreferencesValue(
			PortletDataContext portletDataContext, Portlet portlet,
			String className, long primaryKeyLong)
		throws Exception {

		return "";
	}

	@Override
	protected Long getImportPortletPreferencesNewValue(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<Long, Long> primaryKeys,
			String portletPreferencesOldValue)
		throws Exception {

		return 0L;
	}

	private long _getCompanyId(PortletDataContext portletDataContext) {
		if (portletDataContext != null) {
			return portletDataContext.getCompanyId();
		}

		return CompanyThreadLocal.getCompanyId();
	}

	private long _getGroupId(
		long companyId, PortletPreferences portletPreferences) {

		String groupExternalReferenceCode = portletPreferences.getValue(
			"groupExternalReferenceCode", StringPool.BLANK);

		if (Validator.isNull(groupExternalReferenceCode)) {
			return 0;
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			groupExternalReferenceCode, companyId);

		if (group == null) {
			return 0;
		}

		return group.getGroupId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentExportImportPortletPreferencesProcessor.class);

	@Reference(target = "(name=ReferencedStagedModelImporter)")
	private Capability _capability;

	@Reference(unbind = "-")
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference(unbind = "-")
	private GroupLocalService _groupLocalService;

	@Reference(unbind = "-")
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference(
		target = "(component.name=com.liferay.journal.content.web.internal.exportimport.portlet.preferences.processor.JournalContentMetadataExporterImporterCapability)"
	)
	private Capability _journalContentMetadataExporterImporterCapability;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}