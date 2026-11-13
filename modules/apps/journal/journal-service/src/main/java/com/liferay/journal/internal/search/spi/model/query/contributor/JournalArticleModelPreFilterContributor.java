/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.search.spi.model.query.contributor;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.asset.AssetSubtypeIdentifier;
import com.liferay.portal.search.filter.DateRangeFilterBuilder;
import com.liferay.portal.search.filter.FilterBuilders;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.io.Serializable;

import java.text.Format;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "indexer.class.name=com.liferay.journal.model.JournalArticle",
	service = ModelPreFilterContributor.class
)
public class JournalArticleModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		_workflowStatusModelPreFilterContributor.contribute(
			booleanFilter, modelSearchSettings, searchContext);

		Long classNameId = (Long)searchContext.getAttribute(
			Field.CLASS_NAME_ID);

		if ((classNameId != null) && (classNameId != 0)) {
			booleanFilter.addRequiredTerm(
				Field.CLASS_NAME_ID, classNameId.toString());
		}

		long[] classTypeIds = searchContext.getClassTypeIds();

		if (ArrayUtil.isNotEmpty(classTypeIds)) {
			TermsFilter classTypeIdsTermsFilter = new TermsFilter(
				Field.CLASS_TYPE_ID);

			classTypeIdsTermsFilter.addValues(
				ArrayUtil.toStringArray(classTypeIds));

			booleanFilter.add(classTypeIdsTermsFilter, BooleanClauseOccur.MUST);
		}

		String ddmStructureFieldName = (String)searchContext.getAttribute(
			"ddmStructureFieldName");
		Serializable ddmStructureFieldValue = searchContext.getAttribute(
			"ddmStructureFieldValue");

		if (Validator.isNotNull(ddmStructureFieldName) &&
			Validator.isNotNull(ddmStructureFieldValue)) {

			Locale locale = LocaleUtil.getMostRelevantLocale();

			try {
				QueryFilter queryFilter =
					_ddmIndexer.createFieldValueQueryFilter(
						ddmStructureFieldName, ddmStructureFieldValue, locale);

				booleanFilter.add(queryFilter, BooleanClauseOccur.MUST);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		String ddmStructureKey = (String)searchContext.getAttribute(
			"ddmStructureKey");

		if (Validator.isNotNull(ddmStructureKey)) {
			booleanFilter.addRequiredTerm("ddmStructureKey", ddmStructureKey);
		}

		HashMap<String, List<AssetSubtypeIdentifier>>
			assetSubtypeIdentifiersMap =
				(HashMap<String, List<AssetSubtypeIdentifier>>)
					searchContext.getAttribute("assetSubtypeIdentifiersMap");

		if ((assetSubtypeIdentifiersMap != null) &&
			assetSubtypeIdentifiersMap.containsKey(
				JournalArticle.class.getName())) {

			BooleanFilter subtypeBooleanFilter = new BooleanFilter();

			List<AssetSubtypeIdentifier> assetSubtypeIdentifiers =
				assetSubtypeIdentifiersMap.get(JournalArticle.class.getName());

			for (AssetSubtypeIdentifier assetSubtypeIdentifier :
					assetSubtypeIdentifiers) {

				try {
					Group group =
						_groupLocalService.getGroupByExternalReferenceCode(
							assetSubtypeIdentifier.
								getGroupExternalReferenceCode(),
							searchContext.getCompanyId());

					DDMStructure ddmStructure =
						_ddmStructureLocalService.
							fetchStructureByExternalReferenceCode(
								assetSubtypeIdentifier.
									getSubtypeExternalReferenceCode(),
								group.getGroupId(),
								_classNameLocalService.getClassNameId(
									JournalArticle.class));

					subtypeBooleanFilter.addTerm(
						"ddmStructureKey", ddmStructure.getStructureKey());
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to add subtype filter", exception);
					}
				}
			}

			if (subtypeBooleanFilter.hasClauses()) {
				booleanFilter.add(
					subtypeBooleanFilter, BooleanClauseOccur.MUST);
			}
		}

		String ddmTemplateKey = (String)searchContext.getAttribute(
			"ddmTemplateKey");

		if (Validator.isNotNull(ddmTemplateKey)) {
			booleanFilter.addRequiredTerm("ddmTemplateKey", ddmTemplateKey);
		}

		boolean head = GetterUtil.getBoolean(
			searchContext.getAttribute("head"), Boolean.TRUE);
		boolean headOrShowNonindexable = GetterUtil.getBoolean(
			searchContext.getAttribute("headOrShowNonindexable"));
		boolean latest = GetterUtil.getBoolean(
			searchContext.getAttribute("latest"));
		boolean relatedClassName = GetterUtil.getBoolean(
			searchContext.getAttribute("relatedClassName"));
		boolean showNonindexable = GetterUtil.getBoolean(
			searchContext.getAttribute("showNonindexable"));

		if (latest && !relatedClassName && !showNonindexable) {
			booleanFilter.addRequiredTerm("latest", Boolean.TRUE);
		}
		else if (head && !headOrShowNonindexable && !relatedClassName &&
				 !showNonindexable) {

			booleanFilter.addRequiredTerm("head", Boolean.TRUE);
		}

		if (latest && !relatedClassName && showNonindexable) {
			booleanFilter.addRequiredTerm("latest", Boolean.TRUE);
		}
		else if (!relatedClassName && showNonindexable) {
			booleanFilter.addRequiredTerm("headListable", Boolean.TRUE);
		}
		else if (headOrShowNonindexable && !relatedClassName) {
			booleanFilter.add(
				new BooleanFilter() {
					{
						addTerm("head", Boolean.TRUE);
						addTerm("headListable", Boolean.TRUE);
					}
				},
				BooleanClauseOccur.MUST);
		}

		boolean filterExpired = GetterUtil.getBoolean(
			searchContext.getAttribute("filterExpired"));

		if (!filterExpired) {
			return;
		}

		DateRangeFilterBuilder dateRangeFilterBuilder =
			_filterBuilders.dateRangeFilterBuilder();

		dateRangeFilterBuilder.setFieldName(Field.EXPIRATION_DATE);

		String formatPattern = PropsUtil.get(
			PropsKeys.INDEX_DATE_FORMAT_PATTERN);

		dateRangeFilterBuilder.setFormat(formatPattern);

		Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			formatPattern);

		dateRangeFilterBuilder.setFrom(dateFormat.format(new Date()));

		dateRangeFilterBuilder.setIncludeLower(false);
		dateRangeFilterBuilder.setIncludeUpper(false);

		booleanFilter.add(dateRangeFilterBuilder.build());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleModelPreFilterContributor.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private FilterBuilders _filterBuilders;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(model.pre.filter.contributor.id=WorkflowStatus)")
	private ModelPreFilterContributor _workflowStatusModelPreFilterContributor;

}