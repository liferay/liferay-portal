/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class SearchCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, SearchCTTest.class.getName(), SearchCTTest.class.getName());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testCollectionCTModelPreFilter() throws Exception {
		_productionUserGroup = UserGroupTestUtil.addUserGroup(
			_group.getGroupId());

		UserGroup addedUserGroup = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			addedUserGroup = UserGroupTestUtil.addUserGroup(
				_group.getGroupId());
		}

		_assertAllHits(
			_USER_GROUP_CLASS,
			_getUIDs(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, _productionUserGroup),
			_getUIDs(_ctCollection1.getCtCollectionId(), addedUserGroup));

		_assertProductionHits(_USER_GROUP_CLASS, _productionUserGroup);

		_assertCollectionHits(
			_ctCollection1.getCtCollectionId(), _USER_GROUP_CLASS,
			new UserGroup[] {addedUserGroup},
			new UserGroup[] {_productionUserGroup});
	}

	@Test
	public void testCollectionCTModelPreFilterExclusion() throws Exception {
		_productionUserGroup = UserGroupTestUtil.addUserGroup(
			_group.getGroupId());

		UserGroup modifiedUserGroup1 = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			UserGroup productionUserGroup = _userGroupLocalService.getUserGroup(
				_productionUserGroup.getUserGroupId());

			productionUserGroup.setName("P1 UserGroup");

			modifiedUserGroup1 = _userGroupLocalService.updateUserGroup(
				productionUserGroup);
		}

		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, SearchCTTest.class.getSimpleName(),
			SearchCTTest.class.getSimpleName());

		UserGroup modifiedUserGroup2 = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			UserGroup productionUserGroup = _userGroupLocalService.getUserGroup(
				_productionUserGroup.getUserGroupId());

			productionUserGroup.setName("P2 UserGroup");

			modifiedUserGroup2 = _userGroupLocalService.updateUserGroup(
				productionUserGroup);
		}

		_assertProductionHits(_USER_GROUP_CLASS, _productionUserGroup);

		_assertCollectionHits(
			_ctCollection1.getCtCollectionId(), _USER_GROUP_CLASS,
			new UserGroup[] {modifiedUserGroup1}, new UserGroup[0]);

		_assertCollectionHits(
			_ctCollection2.getCtCollectionId(), _USER_GROUP_CLASS,
			new UserGroup[] {modifiedUserGroup2}, new UserGroup[0]);
	}

	@Test
	public void testCollectionVersusProduction() throws Exception {
		JournalArticle addedJournalArticle = null;

		JournalArticle deletedJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		JournalArticle modifiedJournalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		JournalArticle modifiedJournalArticle2 = null;

		Layout addedLayout = null;

		Layout deletedLayout = LayoutTestUtil.addTypePortletLayout(_group);
		Layout modifiedLayout = LayoutTestUtil.addTypePortletLayout(_group);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			addedJournalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			deletedJournalArticle = _journalArticleLocalService.deleteArticle(
				deletedJournalArticle);

			modifiedJournalArticle2 = JournalTestUtil.updateArticle(
				modifiedJournalArticle1, "testModifyJournalArticle");

			addedLayout = LayoutTestUtil.addTypePortletLayout(_group);

			deletedLayout = _layoutLocalService.deleteLayout(deletedLayout);

			modifiedLayout.setFriendlyURL("/testModifyLayout");

			modifiedLayout = _layoutLocalService.updateLayout(modifiedLayout);
		}

		_assertProductionHits(
			_JOURNAL_ARTICLE_CLASS, deletedJournalArticle,
			modifiedJournalArticle1);

		_assertCollectionHits(
			_ctCollection1.getCtCollectionId(), _JOURNAL_ARTICLE_CLASS,
			new JournalArticle[] {addedJournalArticle, modifiedJournalArticle2},
			new JournalArticle[0]);

		_assertProductionHits(_LAYOUT_CLASS, deletedLayout, modifiedLayout);

		_assertCollectionHits(
			_ctCollection1.getCtCollectionId(), _LAYOUT_CLASS,
			new Layout[] {addedLayout, modifiedLayout}, new Layout[0]);

		_assertAllHits(
			_ALL_INDEXER_CLASSES,
			_getUIDs(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, deletedJournalArticle,
				deletedLayout, modifiedJournalArticle1, modifiedLayout),
			_getUIDs(
				_ctCollection1.getCtCollectionId(), addedJournalArticle,
				addedLayout, modifiedJournalArticle2, modifiedLayout));
	}

	@Test
	public void testPublishAndUndoArticle() throws Exception {
		JournalArticle addedJournalArticle = null;

		JournalArticle deletedJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		JournalArticle modifiedJournalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		JournalArticle modifiedJournalArticle2 = null;

		JournalArticle unmodifiedJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			addedJournalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			deletedJournalArticle = _journalArticleLocalService.deleteArticle(
				deletedJournalArticle);

			modifiedJournalArticle2 = JournalTestUtil.updateArticle(
				modifiedJournalArticle1, "testModifyJournalArticle");
		}

		_assertProductionHits(
			_JOURNAL_ARTICLE_CLASS, deletedJournalArticle,
			modifiedJournalArticle1, unmodifiedJournalArticle);

		_ctProcessLocalService.addCTProcess(
			_ctCollection1.getUserId(), _ctCollection1.getCtCollectionId());

		_assertProductionHits(
			_JOURNAL_ARTICLE_CLASS, addedJournalArticle,
			modifiedJournalArticle2, unmodifiedJournalArticle);

		_undoCTCollection = _ctCollectionLocalService.undoCTCollection(
			_ctCollection1.getCtCollectionId(), _ctCollection1.getUserId(),
			"(undo) " + _ctCollection1.getName(), StringPool.BLANK);

		_ctProcessLocalService.addCTProcess(
			_undoCTCollection.getUserId(),
			_undoCTCollection.getCtCollectionId());

		_assertProductionHits(
			_JOURNAL_ARTICLE_CLASS, deletedJournalArticle,
			modifiedJournalArticle1, unmodifiedJournalArticle);
	}

	@Test
	public void testPublishAndUndoLayout() throws Exception {
		Layout addedLayout = null;

		Layout deletedLayout = LayoutTestUtil.addTypePortletLayout(_group);
		Layout modifiedLayout = LayoutTestUtil.addTypePortletLayout(_group);
		Layout unmodifiedLayout = LayoutTestUtil.addTypePortletLayout(_group);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			addedLayout = LayoutTestUtil.addTypePortletLayout(_group);
			deletedLayout = _layoutLocalService.deleteLayout(deletedLayout);

			modifiedLayout.setFriendlyURL("/testModifyLayout");

			modifiedLayout = _layoutLocalService.updateLayout(modifiedLayout);
		}

		_assertProductionHits(
			_LAYOUT_CLASS, deletedLayout, modifiedLayout, unmodifiedLayout);

		_ctProcessLocalService.addCTProcess(
			_ctCollection1.getUserId(), _ctCollection1.getCtCollectionId());

		_assertProductionHits(
			_LAYOUT_CLASS, addedLayout, modifiedLayout, unmodifiedLayout);

		_assertAllHits(
			_LAYOUT_CLASS,
			_getUIDs(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, addedLayout,
				modifiedLayout, unmodifiedLayout),
			_getUIDs(
				_ctCollection1.getCtCollectionId(), addedLayout,
				modifiedLayout));

		_undoCTCollection = _ctCollectionLocalService.undoCTCollection(
			_ctCollection1.getCtCollectionId(), _ctCollection1.getUserId(),
			"(undo) " + _ctCollection1.getName(), StringPool.BLANK);

		_ctProcessLocalService.addCTProcess(
			_undoCTCollection.getUserId(),
			_undoCTCollection.getCtCollectionId());

		_assertProductionHits(
			_LAYOUT_CLASS, deletedLayout, modifiedLayout, unmodifiedLayout);

		_assertAllHits(
			_LAYOUT_CLASS,
			_getUIDs(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, deletedLayout,
				modifiedLayout, unmodifiedLayout),
			_getUIDs(
				_ctCollection1.getCtCollectionId(), addedLayout,
				modifiedLayout),
			_getUIDs(
				_undoCTCollection.getCtCollectionId(), deletedLayout,
				modifiedLayout));
	}

	@Test
	public void testSearchCTCollection() throws Exception {
		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).emptySearchEnabled(
				true
			).entryClassNames(
				CTCollection.class.getName()
			).modelIndexerClasses(
				CTCollection.class
			).withSearchContext(
				searchContext -> {
				}
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			Field.UID,
			Arrays.asList(
				_uidFactory.getUID(
					CTCollection.class.getName(),
					_ctCollection1.getCtCollectionId(),
					CTConstants.CT_COLLECTION_ID_PRODUCTION)));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _assertAllHits(Class<?>[] classes, String[]... uids) {
		_assertHits(
			CTConstants.CT_COLLECTION_ID_PRODUCTION, classes,
			ArrayUtil.append(uids), true);
	}

	private void _assertCollectionHits(
		long ctCollectionId, Class<?>[] classes,
		CTModel<?>[] collectionCtModels, CTModel<?>[] productionCtModels) {

		_assertHits(
			ctCollectionId, classes,
			ArrayUtil.append(
				_getUIDs(ctCollectionId, collectionCtModels),
				_getUIDs(
					CTConstants.CT_COLLECTION_ID_PRODUCTION,
					productionCtModels)),
			false);
	}

	private void _assertHits(
		long ctCollectionId, Class<?>[] classes, String[] uids, boolean all) {

		String[] classNames = new String[classes.length];

		for (int i = 0; i < classes.length; i++) {
			classNames[i] = classes[i].getName();
		}

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).emptySearchEnabled(
				true
			).entryClassNames(
				classNames
			).modelIndexerClasses(
				classes
			).withSearchContext(
				searchContext -> {
					if (all) {
						searchContext.setAttribute(
							"com.liferay.change.tracking.filter.ctCollectionId",
							"ALL");
					}

					searchContext.setUserId(_group.getCreatorUserId());
				}
			);

		if (!ArrayUtil.contains(classes, UserGroup.class)) {
			searchRequestBuilder.groupIds(
				_group.getGroupId()
			).withSearchContext(
				searchContext -> {
					searchContext.setAttribute(
						Field.GROUP_ID, _group.getGroupId());
					searchContext.setAttribute(
						Field.TYPE,
						new String[] {LayoutConstants.TYPE_PORTLET});
				}
			);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			SearchResponse searchResponse = _searcher.search(
				searchRequestBuilder.build());

			DocumentsAssert.assertValuesIgnoreRelevance(
				searchResponse.getRequestString(),
				searchResponse.getDocuments(), Field.UID, Arrays.asList(uids));
		}
	}

	private void _assertProductionHits(
		Class<?>[] classes, CTModel<?>... ctModels) {

		_assertHits(
			CTConstants.CT_COLLECTION_ID_PRODUCTION, classes,
			_getUIDs(CTConstants.CT_COLLECTION_ID_PRODUCTION, ctModels), false);
	}

	private String[] _getUIDs(long ctCollectionId, CTModel<?>... ctModels) {
		String[] uids = new String[ctModels.length];

		for (int i = 0; i < ctModels.length; i++) {
			uids[i] = _uidFactory.getUID(
				ctModels[i].getModelClassName(), ctModels[i].getPrimaryKey(),
				ctCollectionId);
		}

		return uids;
	}

	private static final Class<?>[] _ALL_INDEXER_CLASSES = {
		JournalArticle.class, Layout.class
	};

	private static final Class<?>[] _JOURNAL_ARTICLE_CLASS = {
		JournalArticle.class
	};

	private static final Class<?>[] _LAYOUT_CLASS = {Layout.class};

	private static final Class<?>[] _USER_GROUP_CLASS = {UserGroup.class};

	@DeleteAfterTestRun
	private CTCollection _ctCollection1;

	@DeleteAfterTestRun
	private CTCollection _ctCollection2;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private UserGroup _productionUserGroup;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	@Inject
	private UIDFactory _uidFactory;

	@DeleteAfterTestRun
	private CTCollection _undoCTCollection;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

}