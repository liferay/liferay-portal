/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.blogs.constants.BlogsConstants;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.resource.SPIRatingResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.BlogPosting;
import com.liferay.headless.delivery.dto.v1_0.Image;
import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.headless.delivery.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.internal.dto.v1_0.util.DisplayPageRendererUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RatingUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.BlogPostingEntityModel;
import com.liferay.headless.delivery.resource.v1_0.BlogPostingResource;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalDateTimeUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import jakarta.ws.rs.core.MultivaluedMap;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/blog-posting.properties",
	scope = ServiceScope.PROTOTYPE, service = BlogPostingResource.class
)
@CTAware
public class BlogPostingResourceImpl extends BaseBlogPostingResourceImpl {

	@Override
	public void deleteBlogPosting(Long blogPostingId) throws Exception {
		_blogsEntryService.deleteEntry(blogPostingId);
	}

	@Override
	public void deleteBlogPostingMyRating(Long blogPostingId) throws Exception {
		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		spiRatingResource.deleteRating(blogPostingId);
	}

	@Override
	public void deleteSiteBlogPostingByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		BlogsEntry blogsEntry =
			_blogsEntryLocalService.getBlogsEntryByExternalReferenceCode(
				externalReferenceCode, siteId);

		_blogsEntryService.deleteEntry(blogsEntry.getEntryId());
	}

	@Override
	public BlogPosting getBlogPosting(Long blogPostingId) throws Exception {
		BlogsEntry blogsEntry = _blogsEntryService.getEntry(blogPostingId);

		return _toBlogPosting(blogsEntry);
	}

	@Override
	public Rating getBlogPostingMyRating(Long blogPostingId) throws Exception {
		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.getRating(blogPostingId);
	}

	@Override
	public String getBlogPostingRenderedContentByDisplayPageDisplayPageKey(
			Long blogPostingId, String displayPageKey)
		throws Exception {

		BlogsEntry blogsEntry = _blogsEntryService.getEntry(blogPostingId);

		return DisplayPageRendererUtil.toHTML(
			BlogsEntry.class.getName(), 0, displayPageKey,
			blogsEntry.getGroupId(), contextHttpServletRequest,
			contextHttpServletResponse, blogsEntry, _infoItemServiceRegistry,
			_layoutDisplayPageProviderRegistry, _layoutLocalService,
			_layoutPageTemplateEntryService);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new BlogPostingEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(BlogsEntry.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public BlogPosting getSiteBlogPostingByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		return _toBlogPosting(
			_blogsEntryService.getBlogsEntryByExternalReferenceCode(
				siteId, externalReferenceCode));
	}

	@Override
	public Page<BlogPosting> getSiteBlogPostingsPage(
			Long siteId, String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_ENTRY, "postSiteBlogPosting",
					BlogsConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_ENTRY, "postSiteBlogPostingBatch",
					BlogsConstants.RESOURCE_NAME, siteId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteBlogPostingBatch",
					BlogsConstants.RESOURCE_NAME, null)
			).put(
				"subscribe",
				addAction(
					ActionKeys.SUBSCRIBE, "putSiteBlogPostingSubscribe",
					BlogsConstants.RESOURCE_NAME, siteId)
			).put(
				"unsubscribe",
				addAction(
					ActionKeys.SUBSCRIBE, "putSiteBlogPostingUnsubscribe",
					BlogsConstants.RESOURCE_NAME, siteId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putBlogPostingBatch",
					BlogsConstants.RESOURCE_NAME, null)
			).build(),
			booleanQuery -> {
			},
			filter, BlogsEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(
					Field.STATUS, WorkflowConstants.STATUS_APPROVED);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {siteId});
			},
			sorts,
			document -> _toBlogPosting(
				_blogsEntryService.getEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public Rating postBlogPostingMyRating(Long blogPostingId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), blogPostingId);
	}

	@Override
	public BlogPosting postSiteBlogPosting(Long siteId, BlogPosting blogPosting)
		throws Exception {

		return _addBlogPosting(
			blogPosting.getExternalReferenceCode(), siteId, blogPosting);
	}

	@Override
	public BlogPosting putBlogPosting(
			Long blogPostingId, BlogPosting blogPosting)
		throws Exception {

		BlogsEntry blogsEntry = _blogsEntryService.getEntry(blogPostingId);

		return _updateBlogPosting(blogsEntry, blogPosting);
	}

	@Override
	public Rating putBlogPostingMyRating(Long blogPostingId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), blogPostingId);
	}

	@Override
	public BlogPosting putSiteBlogPostingByExternalReferenceCode(
			Long siteId, String externalReferenceCode, BlogPosting blogPosting)
		throws Exception {

		BlogsEntry blogsEntry =
			_blogsEntryLocalService.fetchBlogsEntryByExternalReferenceCode(
				externalReferenceCode, siteId);

		if (blogsEntry != null) {
			return _updateBlogPosting(blogsEntry, blogPosting);
		}

		return _addBlogPosting(externalReferenceCode, siteId, blogPosting);
	}

	@Override
	public void putSiteBlogPostingSubscribe(Long siteId) throws Exception {
		_blogsEntryService.subscribe(siteId);
	}

	@Override
	public void putSiteBlogPostingUnsubscribe(Long siteId) throws Exception {
		_blogsEntryService.unsubscribe(siteId);
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		BlogsEntry blogsEntry = _blogsEntryService.getEntry((Long)id);

		return blogsEntry.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return BlogsConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return BlogsEntry.class.getName();
	}

	@Override
	protected void preparePatch(
		BlogPosting blogPosting, BlogPosting existingBlogPosting) {

		Image image = blogPosting.getImage();

		if (image != null) {
			existingBlogPosting.setImage(
				() -> new Image() {
					{
						setCaption(image::getCaption);
						setImageId(image::getImageId);
					}
				});
		}

		TaxonomyCategoryBrief[] taxonomyCategoryBriefs =
			blogPosting.getTaxonomyCategoryBriefs();

		if (taxonomyCategoryBriefs != null) {
			blogPosting.setTaxonomyCategoryIds(
				() -> transform(
					taxonomyCategoryBriefs,
					TaxonomyCategoryBrief::getTaxonomyCategoryId, Long.class));
		}
	}

	private BlogPosting _addBlogPosting(
			String externalReferenceCode, long groupId, BlogPosting blogPosting)
		throws Exception {

		LocalDateTime localDateTime = LocalDateTimeUtil.toLocalDateTime(
			blogPosting.getDatePublished(), null,
			ZoneId.of(contextUser.getTimeZoneId()));
		Image image = blogPosting.getImage();

		return _toBlogPosting(
			_blogsEntryService.addEntry(
				externalReferenceCode, blogPosting.getHeadline(),
				blogPosting.getAlternativeHeadline(),
				blogPosting.getFriendlyUrlPath(), blogPosting.getDescription(),
				blogPosting.getArticleBody(), localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), true, true,
				new String[0], _getCaption(image), _getImageSelector(image),
				null, _createServiceContext(blogPosting, groupId)));
	}

	private ServiceContext _createServiceContext(
		BlogPosting blogPosting, long groupId) {

		return ServiceContextBuilder.create(
			groupId, contextHttpServletRequest,
			blogPosting.getViewableByAsString()
		).assetCategoryIds(
			blogPosting.getTaxonomyCategoryIds()
		).assetTagNames(
			blogPosting.getKeywords()
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				BlogsEntry.class.getName(), contextCompany.getCompanyId(),
				blogPosting.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();
	}

	private String _getCaption(Image image) {
		if (image == null) {
			return null;
		}

		return image.getCaption();
	}

	private ImageSelector _getImageSelector(Image image) {
		if ((image == null) || (image.getImageId() == 0)) {
			return new ImageSelector();
		}

		try {
			FileEntry fileEntry = _dlAppService.getFileEntry(
				image.getImageId());

			return new ImageSelector(
				FileUtil.getBytes(fileEntry.getContentStream()),
				fileEntry.getFileName(), fileEntry.getMimeType(),
				"{\"height\": 0, \"width\": 0, \"x\": 0, \"y\": 0}");
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to get file entry " + image.getImageId(), exception);
		}
	}

	private SPIRatingResource<Rating> _getSPIRatingResource() {
		return new SPIRatingResource<>(
			BlogsEntry.class.getName(), _ratingsEntryLocalService,
			ratingsEntry -> {
				BlogsEntry blogsEntry = _blogsEntryService.getEntry(
					ratingsEntry.getClassPK());

				return RatingUtil.toRating(
					HashMapBuilder.put(
						"create",
						addAction(
							ActionKeys.VIEW, blogsEntry,
							"postBlogPostingMyRating")
					).put(
						"delete",
						addAction(
							ActionKeys.VIEW, blogsEntry,
							"deleteBlogPostingMyRating")
					).put(
						"get",
						addAction(
							ActionKeys.VIEW, blogsEntry,
							"getBlogPostingMyRating")
					).put(
						"replace",
						addAction(
							ActionKeys.VIEW, blogsEntry,
							"putBlogPostingMyRating")
					).build(),
					_portal, ratingsEntry, _userLocalService);
			},
			contextUser);
	}

	private BlogPosting _toBlogPosting(BlogsEntry blogsEntry) throws Exception {
		return _blogPostingDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, blogsEntry, "deleteBlogPosting")
				).put(
					"get",
					addAction(ActionKeys.VIEW, blogsEntry, "getBlogPosting")
				).put(
					"get-rendered-content-by-display-page",
					addAction(
						ActionKeys.VIEW, blogsEntry,
						"getBlogPostingRenderedContentByDisplayPageDisplay" +
							"PageKey")
				).put(
					"replace",
					addAction(ActionKeys.UPDATE, blogsEntry, "putBlogPosting")
				).put(
					"update",
					addAction(ActionKeys.UPDATE, blogsEntry, "patchBlogPosting")
				).build(),
				_dtoConverterRegistry, blogsEntry.getEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private BlogPosting _updateBlogPosting(
			BlogsEntry blogsEntry, BlogPosting blogPosting)
		throws Exception {

		LocalDateTime localDateTime = LocalDateTimeUtil.toLocalDateTime(
			blogPosting.getDatePublished(), null,
			ZoneId.of(contextUser.getTimeZoneId()));
		Image image = blogPosting.getImage();

		return _toBlogPosting(
			_blogsEntryService.updateEntry(
				blogsEntry.getEntryId(), blogPosting.getHeadline(),
				blogPosting.getAlternativeHeadline(),
				blogPosting.getFriendlyUrlPath(), blogPosting.getDescription(),
				blogPosting.getArticleBody(), localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), true, true,
				new String[0], _getCaption(image), _getImageSelector(image),
				null,
				_createServiceContext(blogPosting, blogsEntry.getGroupId())));
	}

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.BlogPostingDTOConverter)"
	)
	private DTOConverter<BlogsEntry, BlogPosting> _blogPostingDTOConverter;

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private BlogsEntryService _blogsEntryService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

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
	private RatingsEntryLocalService _ratingsEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}