/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.search;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchPermissionChecker;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.io.Serializable;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Leonardo Barros
 */
public class DDMFormInstanceRecordIndexer
	extends BaseIndexer<DDMFormInstanceRecord> {

	public static final String CLASS_NAME =
		DDMFormInstanceRecord.class.getName();

	public DDMFormInstanceRecordIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.UID);
		setDefaultSelectedLocalizedFieldNames(Field.DESCRIPTION, Field.TITLE);
		setPermissionAware(true);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public BooleanFilter getFacetBooleanFilter(
			String className, SearchContext searchContext)
		throws Exception {

		BooleanFilter facetBooleanFilter = new BooleanFilter();

		facetBooleanFilter.addTerm(
			Field.ENTRY_CLASS_NAME, DDMFormInstanceRecord.class.getName());

		if (searchContext.getUserId() > 0) {
			facetBooleanFilter =
				searchPermissionChecker.getPermissionBooleanFilter(
					searchContext.getCompanyId(), searchContext.getGroupIds(),
					searchContext.getUserId(), DDMFormInstance.class.getName(),
					facetBooleanFilter, searchContext);
		}

		return facetBooleanFilter;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		int status = GetterUtil.getInteger(
			searchContext.getAttribute(Field.STATUS),
			WorkflowConstants.STATUS_APPROVED);

		if (status != WorkflowConstants.STATUS_ANY) {
			contextBooleanFilter.addRequiredTerm(Field.STATUS, status);
		}

		long formInstanceId = GetterUtil.getLong(
			searchContext.getAttribute("formInstanceId"));

		if (formInstanceId > 0) {
			contextBooleanFilter.addRequiredTerm(
				"formInstanceId", formInstanceId);
		}

		addSearchClassTypeIds(contextBooleanFilter, searchContext);

		String ddmStructureFieldName = (String)searchContext.getAttribute(
			"ddmStructureFieldName");
		Serializable ddmStructureFieldValue = searchContext.getAttribute(
			"ddmStructureFieldValue");

		if (Validator.isNotNull(ddmStructureFieldName) &&
			Validator.isNotNull(ddmStructureFieldValue)) {

			QueryFilter queryFilter = ddmIndexer.createFieldValueQueryFilter(
				ddmStructureFieldName, ddmStructureFieldValue,
				searchContext.getLocale());

			contextBooleanFilter.add(queryFilter, BooleanClauseOccur.MUST);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchTerm(searchQuery, searchContext, Field.USER_NAME, false);

		_addContentSearchTerm(searchQuery, searchContext);
	}

	@Override
	protected void doDelete(DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		deleteDocument(
			ddmFormInstanceRecord.getCompanyId(),
			ddmFormInstanceRecord.getFormInstanceRecordId());
	}

	@Override
	protected Document doGetDocument(
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		Document document = getBaseModelDocument(
			CLASS_NAME, ddmFormInstanceRecord);

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			ddmFormInstanceRecord.getFormInstanceRecordVersion();

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecordVersion.getFormInstance();

		document.addKeyword(
			Field.CLASS_NAME_ID,
			classNameLocalService.getClassNameId(DDMFormInstance.class));
		document.addKeyword(
			Field.CLASS_PK, ddmFormInstance.getFormInstanceId());
		document.addKeyword(
			Field.CLASS_TYPE_ID,
			ddmFormInstanceRecordVersion.getFormInstanceId());
		document.addDate(
			Field.MODIFIED_DATE, ddmFormInstance.getModifiedDate());
		document.addKeyword(Field.RELATED_ENTRY, true);
		document.addKeyword(
			Field.STATUS, ddmFormInstanceRecordVersion.getStatus());
		document.addKeyword(
			Field.VERSION, ddmFormInstanceRecordVersion.getVersion());
		document.addKeyword(
			"formInstanceId", ddmFormInstance.getFormInstanceId());

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		DDMFormValues ddmFormValues =
			ddmFormInstanceRecordVersion.getDDMFormValues();

		_addContent(ddmFormInstanceRecordVersion, ddmFormValues, document);

		ddmIndexer.addAttributes(document, ddmStructure, ddmFormValues);

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		long formInstanceId = GetterUtil.getLong(
			document.get("formInstanceId"));

		String title = _getTitle(formInstanceId, locale);

		Summary summary = createSummary(
			document, Field.TITLE, Field.DESCRIPTION);

		summary.setMaxContentLength(200);
		summary.setTitle(title);

		return summary;
	}

	@Override
	protected void doReindex(DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		indexWriterHelper.updateDocument(
			ddmFormInstanceRecord.getCompanyId(),
			getDocument(ddmFormInstanceRecord));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		DDMFormInstanceRecord ddmFormInstanceRecord =
			ddmFormInstanceRecordLocalService.getFormInstanceRecord(classPK);

		doReindex(ddmFormInstanceRecord);
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexFormInstanceRecords(companyId);
	}

	protected ClassNameLocalService classNameLocalService;
	protected DDMFormInstanceLocalService ddmFormInstanceLocalService;
	protected DDMFormInstanceRecordLocalService
		ddmFormInstanceRecordLocalService;
	protected DDMFormInstanceRecordVersionLocalService
		ddmFormInstanceRecordVersionLocalService;
	protected DDMIndexer ddmIndexer;
	protected IndexWriterHelper indexWriterHelper;
	protected SearchPermissionChecker searchPermissionChecker;

	private void _addContent(
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion,
			DDMFormValues ddmFormValues, Document document)
		throws Exception {

		Set<Locale> locales = ddmFormValues.getAvailableLocales();

		for (Locale locale : locales) {
			document.addText(
				"ddmContent_" + LocaleUtil.toLanguageId(locale),
				_extractContent(ddmFormInstanceRecordVersion, locale));
		}
	}

	private void _addContentSearchTerm(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		Locale locale = searchContext.getLocale();

		addSearchTerm(
			searchQuery, searchContext,
			"ddmContent_ " + LocaleUtil.toLanguageId(locale), false);
	}

	private String _extractContent(
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion,
			Locale locale)
		throws Exception {

		DDMFormValues ddmFormValues =
			ddmFormInstanceRecordVersion.getDDMFormValues();

		if (ddmFormValues == null) {
			return StringPool.BLANK;
		}

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecordVersion.getFormInstance();

		return ddmIndexer.extractIndexableAttributes(
			ddmFormInstance.getStructure(), ddmFormValues, locale);
	}

	private ResourceBundle _getResourceBundle(Locale defaultLocale) {
		return PortalUtil.getResourceBundle(defaultLocale);
	}

	private String _getTitle(long formInstanceId, Locale locale) {
		try {
			DDMFormInstance ddmFormInstance =
				ddmFormInstanceLocalService.getFormInstance(formInstanceId);

			String ddmFormInstanceName = ddmFormInstance.getName(locale);

			return LanguageUtil.format(
				_getResourceBundle(locale), "form-record-for-form-x",
				ddmFormInstanceName, false);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return StringPool.BLANK;
	}

	private void _reindexFormInstanceRecords(long companyId) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			ddmFormInstanceRecordLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property formInstanceRecordIdProperty =
					PropertyFactoryUtil.forName("formInstanceRecordId");

				DynamicQuery ddmFormInstanceRecordVersionDynamicQuery =
					ddmFormInstanceRecordVersionLocalService.dynamicQuery();

				ddmFormInstanceRecordVersionDynamicQuery.setProjection(
					ProjectionFactoryUtil.property("formInstanceRecordId"));

				dynamicQuery.add(
					formInstanceRecordIdProperty.in(
						ddmFormInstanceRecordVersionDynamicQuery));

				Property formInstanceProperty = PropertyFactoryUtil.forName(
					"formInstanceId");

				DynamicQuery ddmFormInstanceDynamicQuery =
					ddmFormInstanceLocalService.dynamicQuery();

				ddmFormInstanceDynamicQuery.setProjection(
					ProjectionFactoryUtil.property("formInstanceId"));

				dynamicQuery.add(
					formInstanceProperty.in(ddmFormInstanceDynamicQuery));
			});
		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(DDMFormInstanceRecord ddmFormInstanceRecord) -> {
				try {
					Document document = getDocument(ddmFormInstanceRecord);

					if (document != null) {
						indexableActionableDynamicQuery.addDocuments(document);
					}
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index form instance record " +
								ddmFormInstanceRecord.getFormInstanceRecordId(),
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceRecordIndexer.class);

}