/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.test;

import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Ivica Cardic
 */
public class BaseBatchEngineTaskExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		user = TestPropsValues.getUser();

		Date date = new Date();

		Instant instant = date.toInstant();

		instant = instant.truncatedTo(ChronoUnit.MINUTES);

		baseDate = Date.from(instant);

		Bundle bundle = FrameworkUtil.getBundle(
			BatchEngineImportTaskExecutorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_batchEngineTaskItemDelegateServiceRegistration =
			bundleContext.registerService(
				BatchEngineTaskItemDelegate.class.getName(),
				new TestBlogPostingBatchEngineTaskItemDelegate(),
				new HashMapDictionary<String, String>());

		initialCount = getBlogEntriesCount();
	}

	@After
	public void tearDown() throws Exception {
		_batchEngineTaskItemDelegateServiceRegistration.unregister();
	}

	public static class BlogPostingEntityModel implements EntityModel {

		public BlogPostingEntityModel() {
			_entityFieldsMap = EntityModel.toEntityFieldsMap(
				new CollectionEntityField(
					new IntegerEntityField(
						"taxonomyCategoryIds", locale -> "assetCategoryIds")),
				new CollectionEntityField(
					new StringEntityField(
						"keywords", locale -> "assetTagNames.lowercase")),
				new DateTimeEntityField(
					"dateCreated",
					locale -> Field.getSortableFieldName(Field.CREATE_DATE),
					locale -> Field.CREATE_DATE),
				new DateTimeEntityField(
					"dateModified",
					locale -> Field.getSortableFieldName(Field.MODIFIED_DATE),
					locale -> Field.MODIFIED_DATE),
				new IntegerEntityField("creatorId", locale -> Field.USER_ID),
				new StringEntityField(
					"headline",
					locale -> Field.getSortableFieldName(Field.TITLE)));
		}

		@Override
		public Map<String, EntityField> getEntityFieldsMap() {
			return _entityFieldsMap;
		}

		@Override
		public String getName() {
			return "com_liferay_batch_engine_internal_test_" +
				"BlogPostingEntityModel";
		}

		private final Map<String, EntityField> _entityFieldsMap;

	}

	public class TestBlogPostingBatchEngineTaskItemDelegate
		extends BaseBatchEngineTaskItemDelegate<BlogPosting> {

		@Override
		public void create(
				Collection<BlogPosting> blogPostings,
				Map<String, Serializable> parameters)
			throws Exception {

			Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());

			super.create(blogPostings, parameters);
		}

		@Override
		public BlogPosting createItem(
				BlogPosting blogPosting,
				Map<String, Serializable> queryParameters)
			throws Exception {

			LocalDateTime localDateTime = _toLocalDateTime(
				blogPosting.getDatePublished());

			_blogsEntryService.addEntry(
				null, blogPosting.getHeadline(),
				blogPosting.getAlternativeHeadline(),
				blogPosting.getFriendlyUrlPath(), blogPosting.getDescription(),
				blogPosting.getArticleBody(), localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), true, true,
				new String[0], null, new ImageSelector(), null,
				_createServiceContext(blogPosting.getSiteId()));

			return null;
		}

		@Override
		public void delete(
				Collection<BlogPosting> blogPostings,
				Map<String, Serializable> parameters)
			throws Exception {

			Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());

			super.delete(blogPostings, parameters);
		}

		@Override
		public void deleteItem(
				BlogPosting blogPosting,
				Map<String, Serializable> queryParameters)
			throws Exception {

			_blogsEntryService.deleteEntry(blogPosting.getId());
		}

		@Override
		public EntityModel getEntityModel(
				Map<String, List<String>> multivaluedMap)
			throws Exception {

			return new BlogPostingEntityModel();
		}

		@Override
		public Page<BlogPosting> read(
				Filter filter, Pagination pagination, Sort[] sorts,
				Map<String, Serializable> parameters, String search)
			throws Exception {

			Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());

			long siteId = GetterUtil.getLong(parameters.get("siteId"));

			return _search(
				booleanQuery -> {
				},
				filter, search, pagination,
				queryConfig -> queryConfig.setSelectedFieldNames(
					Field.ENTRY_CLASS_PK),
				searchContext -> {
					searchContext.setAttribute(
						Field.STATUS, WorkflowConstants.STATUS_ANY);
					searchContext.setCompanyId(contextCompany.getCompanyId());
					searchContext.setGroupIds(new long[] {siteId});
				},
				sorts,
				document -> _toBlogPosting(
					_blogsEntryService.getEntry(
						GetterUtil.getLong(
							document.get(Field.ENTRY_CLASS_PK)))));
		}

		@Override
		public void update(
				Collection<BlogPosting> blogPostings,
				Map<String, Serializable> parameters)
			throws Exception {

			Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());

			super.update(blogPostings, parameters);
		}

		@Override
		public void updateItem(
				BlogPosting blogPosting, Map<String, Serializable> parameters)
			throws Exception {

			Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());

			LocalDateTime localDateTime = _toLocalDateTime(
				blogPosting.getDatePublished());

			BlogsEntry blogsEntry = _blogsEntryService.getEntry(
				blogPosting.getId());

			_blogsEntryService.updateEntry(
				blogPosting.getId(), blogPosting.getHeadline(),
				blogPosting.getAlternativeHeadline(),
				blogPosting.getFriendlyUrlPath(), blogPosting.getDescription(),
				blogPosting.getArticleBody(), localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), true, true,
				new String[0], null, new ImageSelector(), null,
				_createServiceContext(blogsEntry.getGroupId()));
		}

		private SearchContext _createSearchContext(
				BooleanClause<?> booleanClause, String keywords,
				Pagination pagination,
				UnsafeConsumer<QueryConfig, Exception>
					queryConfigUnsafeConsumer,
				Sort[] sorts)
			throws Exception {

			SearchContext searchContext = new SearchContext();

			QueryConfig queryConfig = searchContext.getQueryConfig();

			queryConfig.setHighlightEnabled(false);
			queryConfig.setScoreEnabled(false);

			queryConfigUnsafeConsumer.accept(queryConfig);

			searchContext.setBooleanClauses(
				new BooleanClause[] {booleanClause});

			if (pagination != null) {
				searchContext.setEnd(pagination.getEndPosition());
			}

			searchContext.setKeywords(keywords);
			searchContext.setSorts(sorts);

			if (pagination != null) {
				searchContext.setStart(pagination.getStartPosition());
			}

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			searchContext.setUserId(permissionChecker.getUserId());

			return searchContext;
		}

		private ServiceContext _createServiceContext(Long groupId) {
			ServiceContext serviceContext = new ServiceContext();

			if (groupId != null) {
				serviceContext.setScopeGroupId(groupId);
			}

			return serviceContext;
		}

		private BooleanClause<?> _getBooleanClause(
				UnsafeConsumer<BooleanQuery, Exception>
					booleanQueryUnsafeConsumer,
				Filter filter)
			throws Exception {

			BooleanQuery booleanQuery = new BooleanQueryImpl();

			booleanQuery.add(new MatchAllQuery(), BooleanClauseOccur.MUST);

			BooleanFilter booleanFilter = new BooleanFilter();

			if (filter != null) {
				booleanFilter.add(filter, BooleanClauseOccur.MUST);
			}

			booleanQuery.setPreBooleanFilter(booleanFilter);

			booleanQueryUnsafeConsumer.accept(booleanQuery);

			return BooleanClauseFactoryUtil.create(
				booleanQuery, BooleanClauseOccur.MUST.getName());
		}

		private Page<BlogPosting> _search(
				UnsafeConsumer<BooleanQuery, Exception>
					booleanQueryUnsafeConsumer,
				Filter filter, String keywords, Pagination pagination,
				UnsafeConsumer<QueryConfig, Exception>
					queryConfigUnsafeConsumer,
				UnsafeConsumer<SearchContext, Exception>
					searchContextUnsafeConsumer,
				Sort[] sorts,
				UnsafeFunction<Document, BlogPosting, Exception>
					transformUnsafeFunction)
			throws Exception {

			if (sorts == null) {
				sorts = new Sort[] {
					new Sort(Field.ENTRY_CLASS_PK, Sort.LONG_TYPE, false)
				};
			}

			List<BlogPosting> blogPostings = new ArrayList<>();

			Indexer<?> indexer = IndexerRegistryUtil.getIndexer(
				(Class<?>)BlogsEntry.class);

			SearchContext searchContext = _createSearchContext(
				_getBooleanClause(booleanQueryUnsafeConsumer, filter), keywords,
				pagination, queryConfigUnsafeConsumer, sorts);

			searchContextUnsafeConsumer.accept(searchContext);

			Hits hits = indexer.search(searchContext);

			for (Document document : hits.getDocs()) {
				BlogPosting item = transformUnsafeFunction.apply(document);

				if (item == null) {
					continue;
				}

				blogPostings.add(item);
			}

			return Page.of(
				blogPostings, pagination, indexer.searchCount(searchContext));
		}

		private BlogPosting _toBlogPosting(BlogsEntry blogsEntry) {
			BlogPosting blogPosting = new BlogPosting();

			blogPosting.setAlternativeHeadline(blogsEntry.getSubtitle());
			blogPosting.setArticleBody(blogsEntry.getContent());
			blogPosting.setDateCreated(blogsEntry.getCreateDate());
			blogPosting.setDateModified(blogsEntry.getModifiedDate());
			blogPosting.setDatePublished(blogsEntry.getDisplayDate());
			blogPosting.setDescription(blogsEntry.getDescription());
			blogPosting.setEncodingFormat("text/html");
			blogPosting.setFriendlyUrlPath(blogsEntry.getUrlTitle());
			blogPosting.setHeadline(blogsEntry.getTitle());
			blogPosting.setId(blogsEntry.getEntryId());
			blogPosting.setSiteId(blogsEntry.getGroupId());

			return blogPosting;
		}

		private LocalDateTime _toLocalDateTime(Date date) {
			Instant instant = date.toInstant();

			ZonedDateTime zonedDateTime = instant.atZone(
				ZoneId.systemDefault());

			return zonedDateTime.toLocalDateTime();
		}

	}

	protected List<BlogsEntry> addBlogsEntries() throws Exception {
		List<BlogsEntry> blogsEntries = new ArrayList<>();

		for (int i = 0; i < ROWS_COUNT; i++) {
			blogsEntries.add(
				blogsEntryLocalService.addEntry(
					user.getUserId(), "headline" + i, "alternativeHeadline" + i,
					null, "articleBody" + i, new Date(baseDate.getTime()),
					false, false, null, null, null, null,
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getCompanyId(),
						TestPropsValues.getGroupId(), user.getUserId())));
		}

		return blogsEntries;
	}

	protected void assertBlogsEntriesCount() throws Exception {
		Assert.assertEquals(initialCount + ROWS_COUNT, getBlogEntriesCount());
	}

	protected int getBlogEntriesCount() throws Exception {
		return blogsEntryLocalService.getGroupEntriesCount(
			TestPropsValues.getGroupId(),
			new QueryDefinition<>(WorkflowConstants.STATUS_ANY));
	}

	protected static final String[] FIELD_NAMES = {
		"alternativeHeadline", "articleBody", "datePublished", "headline",
		"siteId"
	};

	protected static final int ROWS_COUNT = 18;

	protected Date baseDate;

	@Inject
	protected BlogsEntryLocalService blogsEntryLocalService;

	protected final DateFormat dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ssX");
	protected int initialCount;
	protected User user;

	private ServiceRegistration<?>
		_batchEngineTaskItemDelegateServiceRegistration;

	@Inject
	private BlogsEntryService _blogsEntryService;

}