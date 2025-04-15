/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.internal.resource.v1_0;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidator;
import com.liferay.headless.admin.content.internal.dto.v1_0.extension.ExtensionStructuredContent;
import com.liferay.headless.admin.content.internal.dto.v1_0.util.VersionUtil;
import com.liferay.headless.admin.content.internal.odata.entity.v1_0.StructuredContentEntityModel;
import com.liferay.headless.admin.content.resource.v1_0.StructuredContentResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.ContentField;
import com.liferay.headless.delivery.dto.v1_0.StructuredContent;
import com.liferay.headless.delivery.dto.v1_0.util.DDMFormValuesUtil;
import com.liferay.headless.delivery.dto.v1_0.util.StructuredContentUtil;
import com.liferay.headless.delivery.dynamic.data.mapping.DDMFormFieldUtil;
import com.liferay.headless.delivery.search.aggregation.AggregationUtil;
import com.liferay.headless.delivery.search.sort.SortUtil;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.EntityExtensionUtil;
import com.liferay.portal.vulcan.util.LocalDateTimeUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/structured-content.properties",
	scope = ServiceScope.PROTOTYPE, service = StructuredContentResource.class
)
public class StructuredContentResourceImpl
	extends BaseStructuredContentResourceImpl implements EntityModelResource {

	@Override
	public void deleteStructuredContentByVersion(
			Long structuredContentId, Double version)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				structuredContentId, WorkflowConstants.STATUS_ANY, false);

		_journalArticleService.deleteArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId(), version,
			journalArticle.getUrlTitle(), new ServiceContext());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<StructuredContent> getSiteStructuredContentsPage(
			Long siteId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		boolean permissionToManageVersions = _hasPermission(siteId);

		return SearchUtil.search(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteStructuredContentsPage",
					JournalConstants.RESOURCE_NAME, siteId)
			).build(),
			booleanQuery -> {
				if (!GetterUtil.getBoolean(flatten)) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					booleanFilter.add(
						new TermFilter(
							Field.FOLDER_ID,
							String.valueOf(
								JournalFolderConstants.
									DEFAULT_PARENT_FOLDER_ID)),
						BooleanClauseOccur.MUST);
				}
			},
			filter, JournalArticle.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ARTICLE_ID, Field.SCOPE_GROUP_ID,
				Field.ROOT_ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);

				if (permissionToManageVersions) {
					searchContext.setAttribute(
						Field.STATUS, WorkflowConstants.STATUS_ANY);
					searchContext.setAttribute("latest", Boolean.TRUE);
				}
				else {
					searchContext.setAttribute(
						Field.STATUS, WorkflowConstants.STATUS_APPROVED);
					searchContext.setAttribute("head", Boolean.TRUE);
				}

				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {siteId});

				SearchRequestBuilder searchRequestBuilder =
					_searchRequestBuilderFactory.builder(searchContext);

				AggregationUtil.processVulcanAggregation(
					_aggregations, _ddmIndexer, _queries, searchRequestBuilder,
					aggregation);

				SortUtil.processSorts(
					_ddmIndexer, searchRequestBuilder, searchContext.getSorts(),
					_queries, _sorts);
			},
			sorts,
			document -> {
				JournalArticle journalArticle;

				if (permissionToManageVersions) {
					journalArticle =
						_journalArticleLocalService.getLatestArticle(
							GetterUtil.getLong(
								document.get(Field.ROOT_ENTRY_CLASS_PK)),
							WorkflowConstants.STATUS_ANY, false);
				}
				else {
					journalArticle =
						_journalArticleLocalService.getLatestArticle(
							GetterUtil.getLong(
								document.get(Field.ROOT_ENTRY_CLASS_PK)),
							WorkflowConstants.STATUS_APPROVED, true);
				}

				return _toExtensionStructuredContent(journalArticle);
			});
	}

	@Override
	public StructuredContent getStructuredContentByVersion(
			Long structuredContentId, Double version)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				structuredContentId, WorkflowConstants.STATUS_ANY, false);

		return _toExtensionStructuredContent(
			_journalArticleService.getArticle(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				version));
	}

	@Override
	public Page<StructuredContent> getStructuredContentsVersionsPage(
			Long structuredContentId)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				structuredContentId, WorkflowConstants.STATUS_ANY, false);

		if (_hasPermission(journalArticle.getGroupId())) {
			return Page.of(
				transform(
					_journalArticleService.getArticlesByArticleId(
						journalArticle.getGroupId(),
						journalArticle.getArticleId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null),
					this::_toExtensionStructuredContent));
		}

		return Page.of(
			transform(
				_journalArticleService.getArticlesByArticleId(
					journalArticle.getGroupId(), journalArticle.getArticleId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null),
				this::_toExtensionStructuredContent));
	}

	@Override
	public StructuredContent postSiteStructuredContentDraft(
			Long siteId, StructuredContent structuredContent)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureService.getStructure(
			structuredContent.getContentStructureId());

		Map<Locale, String> titleMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			structuredContent.getTitle(), structuredContent.getTitle_i18n());

		Map<Locale, String> descriptionMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			structuredContent.getDescription(),
			structuredContent.getDescription_i18n());

		Set<Locale> notFoundLocales = new HashSet<>(descriptionMap.keySet());

		Map<Locale, String> friendlyUrlMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			structuredContent.getFriendlyUrlPath(),
			structuredContent.getFriendlyUrlPath_i18n(), titleMap);

		notFoundLocales.addAll(friendlyUrlMap.keySet());

		LocalizedMapUtil.validateI18n(
			true, LocaleUtil.getSiteDefault(), "Structured content", titleMap,
			notFoundLocales);

		_validateContentFields(
			structuredContent.getContentFields(), ddmStructure);

		LocalDateTime localDateTime = LocalDateTimeUtil.toLocalDateTime(
			structuredContent.getDatePublished(), null,
			ZoneId.of(contextUser.getTimeZoneId()));

		ServiceContext serviceContext = ServiceContextBuilder.create(
			siteId, contextHttpServletRequest,
			structuredContent.getViewableByAsString()
		).assetCategoryIds(
			structuredContent.getTaxonomyCategoryIds()
		).assetTagNames(
			structuredContent.getKeywords()
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				JournalArticle.class.getName(), contextCompany.getCompanyId(),
				structuredContent.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();

		Double priority = structuredContent.getPriority();

		if (priority != null) {
			serviceContext.setAssetPriority(priority);
		}

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return _toExtensionStructuredContent(
			_journalArticleService.addArticle(
				null, siteId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				0, 0, null, true, titleMap, descriptionMap, friendlyUrlMap,
				StructuredContentUtil.getJournalArticleContent(
					_ddm,
					DDMFormValuesUtil.toDDMFormValues(
						titleMap.keySet(), structuredContent.getContentFields(),
						ddmStructure.getDDMForm(), _dlAppService, siteId,
						_journalArticleService, _layoutLocalService,
						contextAcceptLanguage.getPreferredLocale(),
						_getRootDDMFormFields(ddmStructure)),
					_jsonDDMFormValuesSerializer, _ddmFormValuesValidator,
					ddmStructure, _journalConverter),
				ddmStructure.getStructureId(), _getDDMTemplateKey(ddmStructure),
				null, localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), 0, 0, 0, 0,
				0, true, 0, 0, 0, 0, 0, true, true, false, 0, 0, null, null,
				null, null, serviceContext));
	}

	private String _getDDMTemplateKey(DDMStructure ddmStructure) {
		List<DDMTemplate> ddmTemplates = ddmStructure.getTemplates();

		if (ddmTemplates.isEmpty()) {
			return StringPool.BLANK;
		}

		DDMTemplate ddmTemplate = ddmTemplates.get(0);

		return ddmTemplate.getTemplateKey();
	}

	private List<DDMFormField> _getRootDDMFormFields(
		DDMStructure ddmStructure) {

		return transform(
			ddmStructure.getRootFieldNames(),
			fieldName -> DDMFormFieldUtil.getDDMFormField(
				_ddmStructureService, ddmStructure, fieldName));
	}

	private boolean _hasPermission(Long siteId) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return permissionChecker.hasPermission(
			siteId, JournalPortletKeys.JOURNAL, 0,
			ActionKeys.ACCESS_IN_CONTROL_PANEL);
	}

	private StructuredContent _toExtensionStructuredContent(
			JournalArticle journalArticle)
		throws Exception {

		DTOConverter<JournalArticle, StructuredContent> dtoConverter =
			(DTOConverter<JournalArticle, StructuredContent>)
				_dtoConverterRegistry.getDTOConverter(
					JournalArticle.class.getName());

		if (dtoConverter == null) {
			return null;
		}

		StructuredContent structuredContent = dtoConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				Collections.emptyMap(), _dtoConverterRegistry,
				contextHttpServletRequest, journalArticle.getResourcePrimKey(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			journalArticle);

		return EntityExtensionUtil.extend(
			structuredContent, StructuredContent.class,
			ExtensionStructuredContent.class,
			ExtensionStructuredContent -> ExtensionStructuredContent.setVersion(
				VersionUtil.toVersion(contextAcceptLanguage, journalArticle)));
	}

	private void _validateContentFields(
		ContentField[] contentFields, DDMStructure ddmStructure) {

		if (ArrayUtil.isEmpty(contentFields)) {
			return;
		}

		for (ContentField contentField : contentFields) {
			DDMFormField ddmFormField = DDMFormFieldUtil.getDDMFormField(
				_ddmStructureService, ddmStructure, contentField.getName());

			if (ddmFormField == null) {
				throw new BadRequestException(
					StringBundler.concat(
						"Unable to get content field value for \"",
						contentField.getName(), "\" for content structure ",
						ddmStructure.getStructureId()));
			}

			_validateContentFields(
				contentField.getNestedContentFields(), ddmStructure);
		}
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private DDM _ddm;

	@Reference
	private DDMFormValuesValidator _ddmFormValuesValidator;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	private final EntityModel _entityModel = new StructuredContentEntityModel();

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalConverter _journalConverter;

	@Reference(target = "(ddm.form.values.serializer.type=json)")
	private DDMFormValuesSerializer _jsonDDMFormValuesSerializer;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Queries _queries;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Sorts _sorts;

}