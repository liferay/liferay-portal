/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.exception.CTPublishConflictException;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTProcessService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplayFactory;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationStatistics;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class CTCollectionServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER);

		_resourcePermissionLocalService.addResourcePermission(
			_role.getCompanyId(), CTConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_role.getCompanyId()), _role.getRoleId(),
			CTActionKeys.ADD_PUBLICATION);

		_roleLocalService.addUserRole(_user.getUserId(), _role);
		_roleLocalService.addUserRole(
			_user.getUserId(),
			_roleLocalService.getDefaultGroupRole(_group.getGroupId()));
	}

	@Test
	public void testAddCTCollectionWithCustomOwnerPermissions()
		throws Exception {

		UserTestUtil.setUser(_user);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), null);

		Assert.assertTrue(
			_ctCollectionModelResourcePermission.contains(
				permissionChecker, _ctCollection, CTActionKeys.PUBLISH));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"defaultOwnerActionIds",
							new String[] {
								ActionKeys.UPDATE, ActionKeys.VIEW,
								CTActionKeys.INVITE_USERS
							}
						).build())) {

			_ctCollection = _ctCollectionLocalService.addCTCollection(
				null, TestPropsValues.getCompanyId(), _user.getUserId(), 0,
				RandomTestUtil.randomString(), null);

			Assert.assertFalse(
				_ctCollectionModelResourcePermission.contains(
					permissionChecker, _ctCollection, CTActionKeys.PUBLISH));
		}
	}

	@Test
	public void testDiscardCTEntryWithRemovedParent() throws Exception {
		UserTestUtil.setUser(_user);

		JournalFolder folder = _journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), folder.getFolderId());

		_ctCollection = _ctCollectionService.addCTCollection(
			null, _user.getCompanyId(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_journalArticleLocalService.moveArticle(
				_group.getGroupId(), journalArticle.getArticleId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, null);

			_journalFolderLocalService.deleteFolder(folder);
		}

		long journalArticleClassNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class);

		long folderClassNameId = _classNameLocalService.getClassNameId(
			JournalFolder.class);

		Assert.assertFalse(
			_ctCollectionLocalService.isCTEntryEnclosed(
				_ctCollection.getCtCollectionId(), journalArticleClassNameId,
				journalArticle.getPrimaryKey()));

		_ctCollectionService.discardCTEntry(
			_ctCollection.getCtCollectionId(), journalArticleClassNameId,
			journalArticle.getPrimaryKey());

		Assert.assertNull(
			_ctEntryLocalService.fetchCTEntry(
				_ctCollection.getCtCollectionId(), journalArticleClassNameId,
				journalArticle.getPrimaryKey()));

		Assert.assertTrue(
			_ctCollectionLocalService.isCTEntryEnclosed(
				_ctCollection.getCtCollectionId(), folderClassNameId,
				journalArticle.getPrimaryKey()));

		_ctCollectionService.discardCTEntry(
			_ctCollection.getCtCollectionId(), folderClassNameId,
			folder.getPrimaryKey());

		Assert.assertEquals(
			0,
			_ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection.getCtCollectionId()));

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(*) from JournalArticle where id_ = ",
					journalArticle.getPrimaryKey(), " and ctCollectionId = ",
					_ctCollection.getCtCollectionId()));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(0, resultSet.getInt(1));
		}

		Destination destination = MessageBusUtil.getDestination(
			CTDestinationNames.CT_ENTRY_REINDEX);

		DestinationStatistics destinationStatistics =
			destination.getDestinationStatistics();

		int i = 0;

		while ((destinationStatistics.getActiveThreadCount() > 0) ||
			   (destinationStatistics.getPendingMessageCount() > 0)) {

			if (i++ > 60) {
				break;
			}

			Thread.sleep(500);

			destinationStatistics = destination.getDestinationStatistics();
		}

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				CTEntry.class
			).withSearchContext(
				searchContext -> searchContext.setAttribute(
					"ctCollectionId", _ctCollection.getCtCollectionId())
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		Assert.assertEquals(0, searchResponse.getTotalHits());
	}

	@Test
	public void testMoveCTEntryWithConstraintConflict() throws Exception {
		UserTestUtil.setUser(_user);

		CTCollection fromCollection = _ctCollectionService.addCTCollection(
			null, _user.getCompanyId(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		JournalFolder journalFolder = null;
		String folderName = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fromCollection.getCtCollectionId())) {

			journalFolder = _journalFolderFixture.addFolder(
				_group.getGroupId(), folderName);
		}

		CTCollection toCTCollection = _ctCollectionService.addCTCollection(
			null, _user.getCompanyId(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					toCTCollection.getCtCollectionId())) {

			_journalFolderFixture.addFolder(_group.getGroupId(), folderName);
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.change.tracking.service.impl." +
					"CTCollectionLocalServiceImpl",
				LoggerTestUtil.ERROR)) {

			_ctCollectionService.moveCTEntry(
				fromCollection.getCtCollectionId(),
				toCTCollection.getCtCollectionId(),
				_classNameLocalService.getClassNameId(JournalFolder.class),
				journalFolder.getPrimaryKey());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals("Conflict detected", logEntry.getMessage());
		}
		catch (PortalException portalException) {
			Assert.assertTrue(
				portalException instanceof CTPublishConflictException);
		}
	}

	@Test
	public void testMoveCTEntryWithModificationConflict() throws Exception {
		UserTestUtil.setUser(_user);

		CTCollection fromCollection = _ctCollectionService.addCTCollection(
			null, _user.getCompanyId(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticle journalArticle2 = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fromCollection.getCtCollectionId())) {

			journalArticle2 = JournalTestUtil.updateArticle(journalArticle1);
		}

		CTCollection toCTCollection = _ctCollectionService.addCTCollection(
			null, _user.getCompanyId(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					toCTCollection.getCtCollectionId())) {

			JournalTestUtil.updateArticle(journalArticle1);
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.change.tracking.service.impl." +
					"CTCollectionLocalServiceImpl",
				LoggerTestUtil.ERROR)) {

			_ctCollectionService.moveCTEntry(
				fromCollection.getCtCollectionId(),
				toCTCollection.getCtCollectionId(),
				_classNameLocalService.getClassNameId(JournalArticle.class),
				journalArticle2.getPrimaryKey());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals("Conflict detected", logEntry.getMessage());
		}
		catch (PortalException portalException) {
			Assert.assertTrue(
				portalException instanceof CTPublishConflictException);
		}
	}

	@Test
	public void testMoveCTEntryWithoutParent() throws Exception {
		UserTestUtil.setUser(_user);

		CTCollection fromCollection = _ctCollectionService.addCTCollection(
			null, _user.getCompanyId(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		JournalArticle journalArticle = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fromCollection.getCtCollectionId())) {

			journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}

		CTCollection toCTCollection = _ctCollectionService.addCTCollection(
			null, _user.getCompanyId(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.change.tracking.service.impl." +
					"CTCollectionLocalServiceImpl",
				LoggerTestUtil.ERROR)) {

			_ctCollectionService.moveCTEntry(
				fromCollection.getCtCollectionId(),
				toCTCollection.getCtCollectionId(),
				_classNameLocalService.getClassNameId(JournalArticle.class),
				journalArticle.getPrimaryKey());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals("Conflict detected", logEntry.getMessage());
		}
		catch (PortalException portalException) {
			Assert.assertTrue(
				portalException instanceof CTPublishConflictException);
		}
	}

	@Test
	public void testPublishCTCollection() throws Exception {
		UserTestUtil.setUser(_user);

		Assert.assertEquals(
			0,
			_ctCollectionService.getCTCollectionsCount(
				_user.getCompanyId(), null, ""));

		_ctCollection = _ctCollectionService.addCTCollection(
			null, _user.getCompanyId(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Assert.assertEquals(
			1,
			_ctCollectionService.getCTCollectionsCount(
				_user.getCompanyId(), null, ""));

		JournalFolder journalFolder = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			journalFolder = _journalFolderFixture.addFolder(
				_group.getGroupId(), RandomTestUtil.randomString());

			_ctCollectionService.publishCTCollection(
				_user.getUserId(), _ctCollection.getCtCollectionId());
		}

		Assert.assertEquals(
			journalFolder,
			_journalFolderLocalService.fetchJournalFolder(
				journalFolder.getFolderId()));

		List<CTProcess> ctProcesses = _ctProcessService.getCTProcesses(
			_user.getCompanyId(), _user.getUserId(), _ctCollection.getName(),
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(ctProcesses.toString(), 1, ctProcesses.size());

		CTProcess ctProcess = ctProcesses.get(0);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				ctProcess.getBackgroundTaskId());

		BackgroundTaskDisplay backgroundTaskDisplay =
			_backgroundTaskDisplayFactory.getBackgroundTaskDisplay(
				backgroundTask.getBackgroundTaskId());

		Assert.assertEquals(100, backgroundTaskDisplay.getPercentage());

		Assert.assertEquals(
			_ctCollection.getCtCollectionId(), ctProcess.getCtCollectionId());
	}

	@Inject
	private static BackgroundTaskDisplayFactory _backgroundTaskDisplayFactory;

	@Inject
	private static BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTCollectionService _ctCollectionService;

	@Inject
	private static CTEntryLocalService _ctEntryLocalService;

	@Inject
	private static CTProcessService _ctProcessService;

	@Inject
	private static JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private static JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private static ResourcePermissionLocalService
		_resourcePermissionLocalService;

	@Inject
	private static RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject(
		filter = "model.class.name=com.liferay.change.tracking.model.CTCollection"
	)
	private volatile ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@DeleteAfterTestRun
	private Group _group;

	private final JournalFolderFixture _journalFolderFixture =
		new JournalFolderFixture(_journalFolderLocalService);

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@DeleteAfterTestRun
	private User _user;

}