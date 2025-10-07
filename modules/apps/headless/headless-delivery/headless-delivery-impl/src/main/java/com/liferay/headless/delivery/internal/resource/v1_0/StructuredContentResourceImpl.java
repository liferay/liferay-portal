/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidator;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.resource.SPIRatingResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.ContentField;
import com.liferay.headless.delivery.dto.v1_0.ContentFieldValue;
import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.headless.delivery.dto.v1_0.RelatedContent;
import com.liferay.headless.delivery.dto.v1_0.StructuredContent;
import com.liferay.headless.delivery.dto.v1_0.util.DDMFormValuesUtil;
import com.liferay.headless.delivery.dto.v1_0.util.DDMValueUtil;
import com.liferay.headless.delivery.dto.v1_0.util.StructuredContentUtil;
import com.liferay.headless.delivery.dynamic.data.mapping.DDMFormFieldUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.DisplayPageRendererUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RatingUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RenderedContentValueUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.EntityFieldsProvider;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.StructuredContentEntityModel;
import com.liferay.headless.delivery.resource.v1_0.StructuredContentResource;
import com.liferay.headless.delivery.search.aggregation.AggregationUtil;
import com.liferay.headless.delivery.search.filter.FilterUtil;
import com.liferay.headless.delivery.search.sort.SortUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.exception.NoSuchFolderException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.journal.util.JournalContent;
import com.liferay.journal.util.JournalConverter;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
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
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.util.ContentLanguageUtil;
import com.liferay.portal.vulcan.util.LocalDateTimeUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
	extends BaseStructuredContentResourceImpl {

	@Override
	public void deleteAssetLibraryStructuredContentByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticleByExternalReferenceCode(
				assetLibraryId, externalReferenceCode);

		_journalArticleService.deleteArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getArticleResourceUuid(), new ServiceContext());
	}

	@Override
	public void deleteSiteStructuredContentByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticleByExternalReferenceCode(
				siteId, externalReferenceCode);

		_journalArticleService.deleteArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getArticleResourceUuid(), new ServiceContext());
	}

	@Override
	public void deleteStructuredContent(Long structuredContentId)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		_journalArticleService.deleteArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getArticleResourceUuid(), new ServiceContext());
	}

	@Override
	public void deleteStructuredContentMyRating(Long structuredContentId)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		spiRatingResource.deleteRating(structuredContentId);
	}

	@Override
	public StructuredContent
			getAssetLibraryStructuredContentByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		return _getStructuredContent(
			_journalArticleService.getLatestArticleByExternalReferenceCode(
				assetLibraryId, externalReferenceCode));
	}

	@Override
	public Page<StructuredContent> getAssetLibraryStructuredContentsPage(
			Long assetLibraryId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getStructuredContentsPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_ARTICLE, "postAssetLibraryStructuredContent",
					JournalConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_ARTICLE,
					"postAssetLibraryStructuredContentBatch",
					JournalConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteStructuredContentBatch",
					JournalConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getAssetLibraryStructuredContentsPage",
					JournalConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putStructuredContentBatch",
					JournalConstants.RESOURCE_NAME, null)
			).build(),
			_createStructuredContentsPageBooleanQueryUnsafeConsumer(flatten),
			assetLibraryId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	public Page<StructuredContent> getContentStructureStructuredContentsPage(
			Long contentStructureId, String search, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureService.getStructure(
			contentStructureId);

		return _getStructuredContentsPage(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.VIEW, ddmStructure.getStructureId(),
					"getContentStructureStructuredContentsPage",
					ddmStructure.getUserId(), JournalConstants.RESOURCE_NAME,
					ddmStructure.getGroupId())
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						com.liferay.portal.kernel.search.Field.CLASS_TYPE_ID,
						contentStructureId.toString()),
					BooleanClauseOccur.MUST);
			},
			null, search, aggregation, filter, pagination, sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		List<EntityField> entityFields = null;

		long contentStructureId = GetterUtil.getLong(
			(String)multivaluedMap.getFirst("contentStructureId"));

		if (contentStructureId > 0) {
			DDMStructure ddmStructure = _ddmStructureService.getStructure(
				contentStructureId);

			entityFields = _entityFieldsProvider.provide(ddmStructure);
		}

		return new StructuredContentEntityModel(
			entityFields,
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(JournalArticle.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public StructuredContent getSiteStructuredContentByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		return _getStructuredContent(
			_journalArticleService.getLatestArticleByExternalReferenceCode(
				siteId, externalReferenceCode));
	}

	@Override
	public StructuredContent getSiteStructuredContentByKey(
			Long siteId, String key)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getArticle(
			siteId, key);

		return _getStructuredContent(journalArticle);
	}

	@Override
	public StructuredContent getSiteStructuredContentByUuid(
			Long siteId, String uuid)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.getJournalArticleByUuidAndGroupId(
				uuid, siteId);

		_journalArticleModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			journalArticle.getResourcePrimKey(), ActionKeys.VIEW);

		return _getStructuredContent(journalArticle);
	}

	@Override
	public Page<StructuredContent> getSiteStructuredContentsPage(
			Long siteId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getStructuredContentsPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_ARTICLE, "postSiteStructuredContent",
					JournalConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_ARTICLE, "postSiteStructuredContentBatch",
					JournalConstants.RESOURCE_NAME, siteId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteStructuredContentBatch",
					JournalConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteStructuredContentsPage",
					JournalConstants.RESOURCE_NAME, siteId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putStructuredContentBatch",
					JournalConstants.RESOURCE_NAME, null)
			).build(),
			_createStructuredContentsPageBooleanQueryUnsafeConsumer(flatten),
			siteId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	public StructuredContent getStructuredContent(Long structuredContentId)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		return _getStructuredContent(journalArticle);
	}

	@Override
	public Page<StructuredContent>
			getStructuredContentFolderStructuredContentsPage(
				Long structuredContentFolderId, Boolean flatten, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		JournalFolder journalFolder = _journalFolderService.getFolder(
			structuredContentFolderId);

		return _getStructuredContentsPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_ARTICLE, journalFolder.getFolderId(),
					"postStructuredContentFolderStructuredContent",
					journalFolder.getUserId(), JournalConstants.RESOURCE_NAME,
					journalFolder.getGroupId())
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_ARTICLE, journalFolder.getFolderId(),
					"postStructuredContentFolderStructuredContentBatch",
					journalFolder.getUserId(), JournalConstants.RESOURCE_NAME,
					journalFolder.getGroupId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, journalFolder.getFolderId(),
					"getStructuredContentFolderStructuredContentsPage",
					journalFolder.getUserId(), JournalConstants.RESOURCE_NAME,
					journalFolder.getGroupId())
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				String field = com.liferay.portal.kernel.search.Field.FOLDER_ID;

				if (GetterUtil.getBoolean(flatten)) {
					field = com.liferay.portal.kernel.search.Field.TREE_PATH;
				}

				booleanFilter.add(
					new TermFilter(field, structuredContentFolderId.toString()),
					BooleanClauseOccur.MUST);
			},
			journalFolder.getGroupId(), search, aggregation, filter, pagination,
			sorts);
	}

	@Override
	public Rating getStructuredContentMyRating(Long structuredContentId)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.getRating(structuredContentId);
	}

	@Override
	public String
			getStructuredContentRenderedContentByDisplayPageDisplayPageKey(
				Long structuredContentId, String displayPageKey)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		return DisplayPageRendererUtil.toHTML(
			JournalArticle.class.getName(), ddmStructure.getStructureId(),
			displayPageKey, journalArticle.getGroupId(),
			contextHttpServletRequest, contextHttpServletResponse,
			journalArticle, _infoItemServiceRegistry,
			_layoutDisplayPageProviderRegistry, _layoutLocalService,
			_layoutPageTemplateEntryService);
	}

	@Override
	public String getStructuredContentRenderedContentContentTemplate(
			Long structuredContentId, String templateId)
		throws Exception {

		return RenderedContentValueUtil.renderTemplate(
			_classNameLocalService, _ddmTemplateLocalService,
			_groupLocalService, contextHttpServletRequest,
			_journalArticleService, _journalContent,
			contextAcceptLanguage.getPreferredLocale(), structuredContentId,
			templateId, contextUriInfo);
	}

	@Override
	public StructuredContent patchStructuredContent(
			Long structuredContentId, StructuredContent structuredContent)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		if (!ArrayUtil.contains(
				journalArticle.getAvailableLanguageIds(),
				contextAcceptLanguage.getPreferredLanguageId())) {

			throw new BadRequestException(
				StringBundler.concat(
					"Unable to patch structured content with language ",
					LocaleUtil.toW3cLanguageId(
						contextAcceptLanguage.getPreferredLanguageId()),
					" because it is only available in the following languages ",
					Arrays.toString(
						LocaleUtil.toW3cLanguageIds(
							journalArticle.getAvailableLanguageIds()))));
		}

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		_validateContentFields(
			structuredContent.getContentFields(), ddmStructure);

		boolean neverExpire = _isNeverExpire(structuredContent, journalArticle);

		int[] expirationDateArray = _getExpirationDateArray(
			structuredContent, journalArticle, neverExpire);

		LocalDateTime localDateTime = LocalDateTimeUtil.toLocalDateTime(
			structuredContent.getDatePublished(),
			journalArticle.getDisplayDate(),
			ZoneId.of(contextUser.getTimeZoneId()));

		return _toStructuredContent(
			_journalArticleService.updateArticle(
				journalArticle.getGroupId(), journalArticle.getFolderId(),
				journalArticle.getArticleId(), journalArticle.getVersion(),
				LocalizedMapUtil.patchLocalizedMap(
					journalArticle.getTitleMap(),
					contextAcceptLanguage.getPreferredLocale(),
					structuredContent.getTitle(),
					structuredContent.getTitle_i18n()),
				LocalizedMapUtil.patchLocalizedMap(
					journalArticle.getDescriptionMap(),
					contextAcceptLanguage.getPreferredLocale(),
					structuredContent.getDescription(),
					structuredContent.getDescription_i18n()),
				LocalizedMapUtil.patchLocalizedMap(
					journalArticle.getFriendlyURLMap(),
					contextAcceptLanguage.getPreferredLocale(),
					structuredContent.getFriendlyUrlPath(),
					structuredContent.getFriendlyUrlPath_i18n()),
				_journalConverter.getContent(
					ddmStructure,
					_toPatchedFields(
						structuredContent.getContentFields(), journalArticle),
					journalArticle.getGroupId()),
				_getDDMTemplateKey(ddmStructure),
				journalArticle.getLayoutUuid(),
				localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(),
				expirationDateArray[0], expirationDateArray[1],
				expirationDateArray[2], expirationDateArray[3],
				expirationDateArray[4], neverExpire, 0, 0, 0, 0, 0, true, true,
				false, 0, 0, null, null, null, null,
				_createServiceContext(
					_getAssetCategoryIds(journalArticle, structuredContent),
					_getAssetLinkEntryIds(journalArticle, structuredContent),
					_getAssetPriority(journalArticle, structuredContent),
					_getAssetTagNames(journalArticle, structuredContent),
					journalArticle.getGroupId(), structuredContent)));
	}

	@Override
	public StructuredContent postAssetLibraryStructuredContent(
			Long assetLibraryId, StructuredContent structuredContent)
		throws Exception {

		return postSiteStructuredContent(assetLibraryId, structuredContent);
	}

	@Override
	public StructuredContent postSiteStructuredContent(
			Long siteId, StructuredContent structuredContent)
		throws Exception {

		Long parentStructuredContentFolderId =
			structuredContent.getStructuredContentFolderId();

		if (Validator.isNull(parentStructuredContentFolderId)) {
			parentStructuredContentFolderId =
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return _addStructuredContent(
			structuredContent.getExternalReferenceCode(), siteId,
			parentStructuredContentFolderId, structuredContent);
	}

	@Override
	public StructuredContent postStructuredContentFolderStructuredContent(
			Long structuredContentFolderId, StructuredContent structuredContent)
		throws Exception {

		JournalFolder journalFolder = _journalFolderService.getFolder(
			structuredContentFolderId);

		return _addStructuredContent(
			structuredContent.getExternalReferenceCode(),
			journalFolder.getGroupId(), structuredContentFolderId,
			structuredContent);
	}

	@Override
	public Rating postStructuredContentMyRating(
			Long structuredContentId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), structuredContentId);
	}

	@Override
	public StructuredContent
			putAssetLibraryStructuredContentByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode,
				StructuredContent structuredContent)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					assetLibraryId, externalReferenceCode);

		if (journalArticle != null) {
			return _updateStructuredContent(journalArticle, structuredContent);
		}

		return _addStructuredContent(
			externalReferenceCode, assetLibraryId,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, structuredContent);
	}

	@Override
	public StructuredContent putSiteStructuredContentByExternalReferenceCode(
			Long siteId, String externalReferenceCode,
			StructuredContent structuredContent)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					siteId, externalReferenceCode);

		if (journalArticle != null) {
			return _updateStructuredContent(journalArticle, structuredContent);
		}

		return _addStructuredContent(
			externalReferenceCode, siteId,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, structuredContent);
	}

	@Override
	public StructuredContent putStructuredContent(
			Long structuredContentId, StructuredContent structuredContent)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		return _updateStructuredContent(journalArticle, structuredContent);
	}

	@Override
	public Rating putStructuredContentMyRating(
			Long structuredContentId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), structuredContentId);
	}

	@Override
	public void putStructuredContentSubscribe(Long structuredContentId)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		_journalArticleService.subscribe(
			journalArticle.getGroupId(), journalArticle.getResourcePrimKey());
	}

	@Override
	public void putStructuredContentUnsubscribe(Long structuredContentId)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		_journalArticleService.unsubscribe(
			journalArticle.getGroupId(), journalArticle.getResourcePrimKey());
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle((Long)id);

		return journalArticle.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return JournalConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return JournalArticle.class.getName();
	}

	private StructuredContent _addStructuredContent(
			String externalReferenceCode, Long groupId,
			Long parentStructuredContentFolderId,
			StructuredContent structuredContent)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureService.getStructure(
			structuredContent.getContentStructureId());

		LocalDateTime localDateTime = LocalDateTimeUtil.toLocalDateTime(
			structuredContent.getDatePublished(), null,
			ZoneId.of(contextUser.getTimeZoneId()));

		boolean neverExpire = _isNeverExpire(structuredContent, null);

		int[] expirationDateArray = _getExpirationDateArray(
			structuredContent, null, neverExpire);

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

		long[] assetLinkEntryIds = _getAssetLinkEntryIds(
			structuredContent.getRelatedContents());

		Double priority = structuredContent.getPriority();

		if (priority == null) {
			priority = 0.0;
		}

		return _toStructuredContent(
			_journalArticleService.addArticle(
				externalReferenceCode, groupId, parentStructuredContentFolderId,
				0, 0, null, true, titleMap, descriptionMap, friendlyUrlMap,
				StructuredContentUtil.getJournalArticleContent(
					_ddm,
					DDMFormValuesUtil.toDDMFormValues(
						titleMap.keySet(), structuredContent.getContentFields(),
						ddmStructure.getDDMForm(), _dlAppService, groupId,
						_journalArticleService, _layoutLocalService,
						contextAcceptLanguage.getPreferredLocale(),
						_getRootDDMFormFields(ddmStructure)),
					_jsonDDMFormValuesSerializer, _ddmFormValuesValidator,
					ddmStructure, _journalConverter),
				ddmStructure.getStructureId(), _getDDMTemplateKey(ddmStructure),
				null, localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(),
				expirationDateArray[0], expirationDateArray[1],
				expirationDateArray[2], expirationDateArray[3],
				expirationDateArray[4], neverExpire, 0, 0, 0, 0, 0, true, true,
				false, 0, 0, null, null, null, null,
				_createServiceContext(
					structuredContent.getTaxonomyCategoryIds(),
					assetLinkEntryIds, priority,
					structuredContent.getKeywords(), groupId,
					structuredContent)));
	}

	private boolean _containsI18nMap(ContentField[] contentFields) {
		if (ArrayUtil.isEmpty(contentFields)) {
			return false;
		}

		for (ContentField contentField : contentFields) {
			if (MapUtil.isNotEmpty(contentField.getContentFieldValue_i18n()) ||
				_containsI18nMap(contentField.getNestedContentFields())) {

				return true;
			}
		}

		return false;
	}

	private ServiceContext _createServiceContext(
			Long[] assetCategoryIds, long[] assetLinkEntryIds,
			double assetPriority, String[] assetTagNames, long groupId,
			StructuredContent structuredContent)
		throws Exception {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest,
			structuredContent.getViewableByAsString()
		).assetCategoryIds(
			assetCategoryIds
		).assetTagNames(
			assetTagNames
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				JournalArticle.class.getName(), contextCompany.getCompanyId(),
				structuredContent.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).permissions(
			ModelPermissionsUtil.toModelPermissions(
				contextCompany.getCompanyId(),
				structuredContent.getPermissions(),
				getPermissionCheckerResourceId(structuredContent.getId()),
				getPermissionCheckerResourceName(structuredContent.getId()),
				resourceActionLocalService, resourcePermissionLocalService,
				roleLocalService)
		).build();

		serviceContext.setAssetLinkEntryIds(assetLinkEntryIds);
		serviceContext.setAssetPriority(assetPriority);

		return serviceContext;
	}

	private UnsafeConsumer<BooleanQuery, Exception>
		_createStructuredContentsPageBooleanQueryUnsafeConsumer(
			Boolean flatten) {

		return booleanQuery -> {
			if (!GetterUtil.getBoolean(flatten)) {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						com.liferay.portal.kernel.search.Field.FOLDER_ID,
						String.valueOf(
							JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID)),
					BooleanClauseOccur.MUST);
			}
		};
	}

	private Long[] _getAssetCategoryIds(
			JournalArticle journalArticle, StructuredContent structuredContent)
		throws Exception {

		if ((journalArticle == null) ||
			(structuredContent.getTaxonomyCategoryIds() != null)) {

			return structuredContent.getTaxonomyCategoryIds();
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		return ArrayUtil.toLongArray(assetEntry.getCategoryIds());
	}

	private long[] _getAssetLinkEntryIds(
			JournalArticle journalArticle, StructuredContent structuredContent)
		throws Exception {

		RelatedContent[] relatedContents =
			structuredContent.getRelatedContents();

		if (relatedContents != null) {
			return _getAssetLinkEntryIds(relatedContents);
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		List<AssetLink> assetLinks = _assetLinkLocalService.getLinks(
			assetEntry.getEntryId());

		return ListUtil.toLongArray(assetLinks, AssetLink.ENTRY_ID2_ACCESSOR);
	}

	private long[] _getAssetLinkEntryIds(RelatedContent[] relatedContents) {
		List<Long> assetLinkEntryIds = new ArrayList<>();

		if (relatedContents == null) {
			return ArrayUtil.toLongArray(assetLinkEntryIds);
		}

		for (RelatedContent relatedContent : relatedContents) {
			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				_assetTypeTypeToClassNames.get(relatedContent.getContentType()),
				relatedContent.getId());

			if (assetEntry != null) {
				assetLinkEntryIds.add(assetEntry.getEntryId());
			}
		}

		return ArrayUtil.toLongArray(assetLinkEntryIds);
	}

	private double _getAssetPriority(
			JournalArticle journalArticle, StructuredContent structuredContent)
		throws Exception {

		Double priority = structuredContent.getPriority();

		if (priority != null) {
			return priority;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		return assetEntry.getPriority();
	}

	private String[] _getAssetTagNames(
			JournalArticle journalArticle, StructuredContent structuredContent)
		throws Exception {

		if ((journalArticle == null) ||
			(structuredContent.getKeywords() != null)) {

			return structuredContent.getKeywords();
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		return ArrayUtil.toStringArray(assetEntry.getTagNames());
	}

	private String _getDDMTemplateKey(DDMStructure ddmStructure) {
		List<DDMTemplate> ddmTemplates = ddmStructure.getTemplates();

		if (ddmTemplates.isEmpty()) {
			return StringPool.BLANK;
		}

		DDMTemplate ddmTemplate = ddmTemplates.get(0);

		return ddmTemplate.getTemplateKey();
	}

	private int[] _getExpirationDateArray(
		StructuredContent structuredContent, JournalArticle journalArticle,
		boolean neverExpire) {

		int expirationDateMonth = 0;
		int expirationDateDay = 0;
		int expirationDateYear = 0;
		int expirationDateHour = 0;
		int expirationDateMinute = 0;

		if (!neverExpire) {
			Date date = new Date();

			Date dateExpired = structuredContent.getDateExpired();

			if ((dateExpired == null) && (journalArticle != null)) {
				dateExpired = journalArticle.getExpirationDate();
			}

			if (dateExpired == null) {
				dateExpired = new Date(date.getTime() + Time.YEAR);
			}

			if (dateExpired.after(date)) {
				Calendar expirationCal = CalendarFactoryUtil.getCalendar(
					contextUser.getTimeZone());

				expirationCal.setTime(dateExpired);

				expirationDateMonth = expirationCal.get(Calendar.MONTH);
				expirationDateDay = expirationCal.get(Calendar.DATE);
				expirationDateYear = expirationCal.get(Calendar.YEAR);
				expirationDateHour = expirationCal.get(Calendar.HOUR);
				expirationDateMinute = expirationCal.get(Calendar.MINUTE);

				if (expirationCal.get(Calendar.AM_PM) == Calendar.PM) {
					expirationDateHour += 12;
				}
			}
			else {
				throw new BadRequestException(
					"Expiration date must be either empty or a date in the " +
						"future");
			}
		}

		return new int[] {
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute
		};
	}

	private List<DDMFormField> _getRootDDMFormFields(
		DDMStructure ddmStructure) {

		return transform(
			ddmStructure.getRootFieldNames(),
			fieldName -> DDMFormFieldUtil.getDDMFormField(
				_ddmStructureService, ddmStructure, fieldName));
	}

	private SPIRatingResource<Rating> _getSPIRatingResource() {
		return new SPIRatingResource<>(
			JournalArticle.class.getName(), _ratingsEntryLocalService,
			ratingsEntry -> {
				JournalArticle journalArticle =
					_journalArticleService.getLatestArticle(
						ratingsEntry.getClassPK());

				return RatingUtil.toRating(
					HashMapBuilder.put(
						"create",
						addAction(
							ActionKeys.VIEW,
							journalArticle.getResourcePrimKey(),
							"postStructuredContentMyRating",
							journalArticle.getUserId(),
							JournalArticle.class.getName(),
							journalArticle.getGroupId())
					).put(
						"delete",
						addAction(
							ActionKeys.VIEW,
							journalArticle.getResourcePrimKey(),
							"deleteStructuredContentMyRating",
							journalArticle.getUserId(),
							JournalArticle.class.getName(),
							journalArticle.getGroupId())
					).put(
						"get",
						addAction(
							ActionKeys.VIEW,
							journalArticle.getResourcePrimKey(),
							"getStructuredContentMyRating",
							journalArticle.getUserId(),
							JournalArticle.class.getName(),
							journalArticle.getGroupId())
					).put(
						"replace",
						addAction(
							ActionKeys.VIEW,
							journalArticle.getResourcePrimKey(),
							"putStructuredContentMyRating",
							journalArticle.getUserId(),
							JournalArticle.class.getName(),
							journalArticle.getGroupId())
					).build(),
					_portal, ratingsEntry, _userLocalService);
			},
			contextUser);
	}

	private StructuredContent _getStructuredContent(
			JournalArticle journalArticle)
		throws Exception {

		ContentLanguageUtil.addContentLanguageHeader(
			journalArticle.getAvailableLanguageIds(),
			journalArticle.getDefaultLanguageId(), contextHttpServletResponse,
			contextAcceptLanguage.getPreferredLocale());

		return _toStructuredContent(journalArticle);
	}

	private Page<StructuredContent> _getStructuredContentsPage(
			Map<String, Map<String, String>> actions,
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			Long siteId, String keywords, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions, booleanQueryUnsafeConsumer,
			FilterUtil.processFilter(_ddmIndexer, filter),
			JournalArticle.class.getName(), keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				com.liferay.portal.kernel.search.Field.ARTICLE_ID,
				com.liferay.portal.kernel.search.Field.SCOPE_GROUP_ID),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(
					com.liferay.portal.kernel.search.Field.STATUS,
					WorkflowConstants.STATUS_APPROVED);
				searchContext.setAttribute("head", Boolean.TRUE);
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (siteId != null) {
					searchContext.setGroupIds(new long[] {siteId});
				}

				SearchRequestBuilder searchRequestBuilder =
					_searchRequestBuilderFactory.builder(searchContext);

				AggregationUtil.processVulcanAggregation(
					_aggregations, _ddmIndexer, _queries, searchRequestBuilder,
					aggregation);

				SortUtil.processSorts(
					_ddmIndexer, searchRequestBuilder, searchContext.getSorts(),
					_queries, _sorts);
			},
			sorts, this::_toStructuredContent);
	}

	private boolean _isNeverExpire(
		StructuredContent structuredContent, JournalArticle journalArticle) {

		Boolean neverExpire = structuredContent.getNeverExpire();

		if (neverExpire == null) {
			neverExpire = true;

			if ((journalArticle != null) &&
				(journalArticle.getExpirationDate() != null)) {

				neverExpire = false;
			}
		}

		return neverExpire;
	}

	private void _populateContentFieldValuesMap(
		ContentField[] contentFields,
		Map<String, List<ContentFieldValue>> contentFieldValuesMap) {

		if (ArrayUtil.isEmpty(contentFields)) {
			return;
		}

		for (ContentField contentField : contentFields) {
			ContentFieldValue contentFieldValue =
				contentField.getContentFieldValue();

			if ((contentFieldValue != null) &&
				(contentFieldValue.getData() != null)) {

				List<ContentFieldValue> contentFieldValues =
					contentFieldValuesMap.computeIfAbsent(
						contentField.getName(), key -> new ArrayList<>());

				contentFieldValues.add(contentFieldValue);
			}

			_populateContentFieldValuesMap(
				contentField.getNestedContentFields(), contentFieldValuesMap);
		}
	}

	private Fields _toFields(
			Set<Locale> availableLocales, ContentField[] contentFields,
			JournalArticle journalArticle)
		throws Exception {

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		if (_containsI18nMap(contentFields)) {
			ServiceContext serviceContext = new ServiceContext();

			DDMFormValues ddmFormValues = DDMFormValuesUtil.toDDMFormValues(
				availableLocales, contentFields, ddmStructure.getDDMForm(),
				_dlAppService, journalArticle.getGroupId(),
				_journalArticleService, _layoutLocalService,
				contextAcceptLanguage.getPreferredLocale(),
				_getRootDDMFormFields(ddmStructure));

			serviceContext.setAttribute(
				"ddmFormValues",
				DDMFormValuesUtil.getContent(
					_jsonDDMFormValuesSerializer, ddmStructure.getDDMForm(),
					ddmFormValues.getDDMFormFieldValues()));

			return _ddm.getFields(
				ddmStructure.getStructureId(), serviceContext);
		}

		Fields fields = _ddmFormValuesToFieldsConverter.convert(
			ddmStructure, journalArticle.getDDMFormValues());

		if (ArrayUtil.isEmpty(contentFields)) {
			return fields;
		}

		Map<String, List<ContentFieldValue>> contentFieldValuesMap =
			new HashMap<>();

		_populateContentFieldValuesMap(contentFields, contentFieldValuesMap);

		for (Map.Entry<String, List<ContentFieldValue>> entry :
				contentFieldValuesMap.entrySet()) {

			Field field = fields.get(entry.getKey());

			if (field == null) {
				continue;
			}

			List<Serializable> fieldValues = new ArrayList<>();

			for (ContentFieldValue contentFieldValue : entry.getValue()) {
				fieldValues.add(
					FieldConstants.getSerializable(
						contextAcceptLanguage.getPreferredLocale(),
						LocaleUtil.ROOT, field.getDataType(),
						contentFieldValue.getData()));
			}

			if (ListUtil.isNotEmpty(fieldValues)) {
				field.setValues(
					contextAcceptLanguage.getPreferredLocale(), fieldValues);
			}
		}

		_ddmFormValuesValidator.validate(
			_fieldsToDDMFormValuesConverter.convert(ddmStructure, fields));

		return fields;
	}

	private Fields _toPatchedFields(
			ContentField[] contentFields, JournalArticle journalArticle)
		throws Exception {

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		Fields fields = _ddmFormValuesToFieldsConverter.convert(
			ddmStructure, journalArticle.getDDMFormValues());

		if (ArrayUtil.isEmpty(contentFields)) {
			return fields;
		}

		for (Field field : fields) {
			if (field.isRepeatable()) {
				throw new BadRequestException(
					"Unable to patch a structured content with a repeatable " +
						"field. Instead, update the structured content.");
			}
		}

		DDMFormValues ddmFormValues = DDMFormValuesUtil.toDDMFormValues(
			SetUtil.fromArray(
				LocaleUtil.fromLanguageIds(
					journalArticle.getAvailableLanguageIds())),
			contentFields, ddmStructure.getDDMForm(), _dlAppService,
			journalArticle.getGroupId(), _journalArticleService,
			_layoutLocalService, contextAcceptLanguage.getPreferredLocale(),
			_getRootDDMFormFields(ddmStructure));

		Map<String, DDMFormFieldValue> ddmFormFieldValuesMap = new HashMap<>();

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormValues.getDDMFormFieldValues()) {

			ddmFormFieldValuesMap.put(
				ddmFormFieldValue.getFieldReference(), ddmFormFieldValue);
		}

		for (ContentField contentField : contentFields) {
			DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValuesMap.get(
				contentField.getName());

			if (ddmFormFieldValue == null) {
				continue;
			}

			Field field = fields.get(ddmFormFieldValue.getName());

			Value value = DDMValueUtil.toDDMValue(
				contentField,
				DDMFormFieldUtil.getDDMFormField(
					_ddmStructureService, ddmStructure, contentField.getName()),
				_dlAppService, journalArticle.getGroupId(),
				_journalArticleService, _layoutLocalService,
				contextAcceptLanguage.getPreferredLocale());

			field.setValue(
				contextAcceptLanguage.getPreferredLocale(),
				value.getString(contextAcceptLanguage.getPreferredLocale()));

			ContentField[] nestedContentFields =
				contentField.getNestedContentFields();

			if (nestedContentFields != null) {
				_toPatchedFields(nestedContentFields, journalArticle);
			}
		}

		ddmFormValues = _fieldsToDDMFormValuesConverter.convert(
			ddmStructure, fields);

		_ddmFormValuesValidator.validate(ddmFormValues);

		return fields;
	}

	private StructuredContent _toStructuredContent(Document document)
		throws Exception {

		try {
			return _toStructuredContent(
				_journalArticleService.getLatestArticle(
					GetterUtil.getLong(
						document.get(
							com.liferay.portal.kernel.search.Field.
								SCOPE_GROUP_ID)),
					document.get(
						com.liferay.portal.kernel.search.Field.ARTICLE_ID),
					WorkflowConstants.STATUS_APPROVED));
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFolderException);
			}

			return null;
		}
	}

	private StructuredContent _toStructuredContent(
			JournalArticle journalArticle)
		throws Exception {

		return _structuredContentDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, journalArticle.getResourcePrimKey(),
						"deleteStructuredContent", journalArticle.getUserId(),
						JournalArticle.class.getName(),
						journalArticle.getGroupId())
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, journalArticle.getResourcePrimKey(),
						"getStructuredContent", journalArticle.getUserId(),
						JournalArticle.class.getName(),
						journalArticle.getGroupId())
				).put(
					"get-rendered-content",
					addAction(
						ActionKeys.VIEW, journalArticle.getResourcePrimKey(),
						"getStructuredContentRenderedContentContentTemplate",
						journalArticle.getUserId(),
						JournalArticle.class.getName(),
						journalArticle.getGroupId())
				).put(
					"get-rendered-content-by-display-page",
					addAction(
						ActionKeys.VIEW, journalArticle.getResourcePrimKey(),
						"getStructuredContentRenderedContentByDisplayPage" +
							"DisplayPageKey",
						journalArticle.getUserId(),
						JournalArticle.class.getName(),
						journalArticle.getGroupId())
				).put(
					"replace",
					addAction(
						ActionKeys.UPDATE, journalArticle.getResourcePrimKey(),
						"putStructuredContent", journalArticle.getUserId(),
						JournalArticle.class.getName(),
						journalArticle.getGroupId())
				).put(
					"subscribe",
					addAction(
						ActionKeys.SUBSCRIBE,
						journalArticle.getResourcePrimKey(),
						"putStructuredContentSubscribe",
						journalArticle.getUserId(),
						JournalArticle.class.getName(),
						journalArticle.getGroupId())
				).put(
					"unsubscribe",
					addAction(
						ActionKeys.SUBSCRIBE,
						journalArticle.getResourcePrimKey(),
						"putStructuredContentUnsubscribe",
						journalArticle.getUserId(),
						JournalArticle.class.getName(),
						journalArticle.getGroupId())
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, journalArticle.getResourcePrimKey(),
						"patchStructuredContent", journalArticle.getUserId(),
						JournalArticle.class.getName(),
						journalArticle.getGroupId())
				).build(),
				_dtoConverterRegistry, contextHttpServletRequest,
				journalArticle.getResourcePrimKey(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			journalArticle);
	}

	private StructuredContent _updateStructuredContent(
			JournalArticle journalArticle, StructuredContent structuredContent)
		throws Exception {

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		Map<Locale, String> titleMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			structuredContent.getTitle(), structuredContent.getTitle_i18n(),
			journalArticle.getTitleMap());

		Map<Locale, String> descriptionMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			structuredContent.getDescription(),
			structuredContent.getDescription_i18n(),
			journalArticle.getDescriptionMap());

		Set<Locale> notFoundLocales = new HashSet<>(descriptionMap.keySet());

		Map<Locale, String> friendlyUrlMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			structuredContent.getFriendlyUrlPath(),
			structuredContent.getFriendlyUrlPath_i18n(),
			journalArticle.getFriendlyURLMap());

		friendlyUrlMap =
			friendlyUrlMap.isEmpty() ? journalArticle.getFriendlyURLMap() :
				friendlyUrlMap;

		notFoundLocales.addAll(friendlyUrlMap.keySet());

		LocalizedMapUtil.validateI18n(
			false, LocaleUtil.getSiteDefault(), "Structured content", titleMap,
			notFoundLocales);

		_validateContentFields(
			structuredContent.getContentFields(), ddmStructure);

		LocalDateTime localDateTime = LocalDateTimeUtil.toLocalDateTime(
			structuredContent.getDatePublished(),
			journalArticle.getDisplayDate(),
			ZoneId.of(contextUser.getTimeZoneId()));

		boolean neverExpire = _isNeverExpire(structuredContent, journalArticle);

		int[] expirationDateArray = _getExpirationDateArray(
			structuredContent, journalArticle, neverExpire);

		return _toStructuredContent(
			_journalArticleService.updateArticle(
				journalArticle.getGroupId(), journalArticle.getFolderId(),
				journalArticle.getArticleId(), journalArticle.getVersion(),
				titleMap, descriptionMap, friendlyUrlMap,
				_journalConverter.getContent(
					ddmStructure,
					_toFields(
						titleMap.keySet(), structuredContent.getContentFields(),
						journalArticle),
					journalArticle.getGroupId()),
				_getDDMTemplateKey(ddmStructure),
				journalArticle.getLayoutUuid(),
				localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(),
				expirationDateArray[0], expirationDateArray[1],
				expirationDateArray[2], expirationDateArray[3],
				expirationDateArray[4], neverExpire, 0, 0, 0, 0, 0, true, true,
				false, 0, 0, null, null, null, null,
				_createServiceContext(
					_getAssetCategoryIds(journalArticle, structuredContent),
					_getAssetLinkEntryIds(journalArticle, structuredContent),
					_getAssetPriority(journalArticle, structuredContent),
					_getAssetTagNames(journalArticle, structuredContent),
					journalArticle.getGroupId(), structuredContent)));
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

	private static final Log _log = LogFactoryUtil.getLog(
		StructuredContentResourceImpl.class);

	private static final Map<String, String> _assetTypeTypeToClassNames =
		new HashMapBuilder<>().<String, String>put(
			"BlogPosting", "com.liferay.blogs.model.BlogsEntry"
		).put(
			"Document", "com.liferay.document.library.kernel.model.DLFileEntry"
		).put(
			"KnowledgeBaseArticle", "com.liferay.knowledge.base.model.KBArticle"
		).put(
			"Organization", Organization.class.getName()
		).put(
			"StructuredContent", "com.liferay.journal.model.JournalArticle"
		).put(
			"UserAccount", User.class.getName()
		).put(
			"WebPage", Layout.class.getName()
		).put(
			"WebSite", Group.class.getName()
		).put(
			"WikiPage", "com.liferay.wiki.model.WikiPage"
		).build();

	@Reference
	private Aggregations _aggregations;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDM _ddm;

	@Reference
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Reference
	private DDMFormValuesValidator _ddmFormValuesValidator;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private EntityFieldsProvider _entityFieldsProvider;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private FieldsToDDMFormValuesConverter _fieldsToDDMFormValuesConverter;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private JournalFolderService _journalFolderService;

	@Reference(target = "(ddm.form.values.serializer.type=json)")
	private DDMFormValuesSerializer _jsonDDMFormValuesSerializer;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private Queries _queries;

	@Reference
	private RatingsEntryLocalService _ratingsEntryLocalService;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Sorts _sorts;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.StructuredContentDTOConverter)"
	)
	private DTOConverter<JournalArticle, StructuredContent>
		_structuredContentDTOConverter;

	@Reference
	private UserLocalService _userLocalService;

}