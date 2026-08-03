/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleEvent;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleListener;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.process.local.LocalProcessLauncher;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.PortalCacheManagerProvider;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cluster.ClusterNodeResponse;
import com.liferay.portal.kernel.cluster.ClusterNodeResponses;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.ClusterableInvokerUtil;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log4j.Log4JUtil;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.TomcatClusterTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalServiceUtil;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.test.cluster.tomcat.TomcatCluster;
import com.liferay.portal.test.cluster.tomcat.TomcatNode;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.apache.logging.log4j.core.LoggerContext;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class ClusterGeneralTest implements Serializable {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@ClassRule
	public static final TomcatClusterTestRule tomcatClusterTestRule =
		new TomcatClusterTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		TomcatCluster.Builder builder1 =
			tomcatClusterTestRule.buildTomcatNode();

		_tomcatNode1 = builder1.build();

		_tomcatNode1.start(true);

		TomcatCluster.Builder builder2 =
			tomcatClusterTestRule.buildTomcatNode();

		_tomcatNode2 = builder2.build();

		_tomcatNode2.start(true);
	}

	@Test
	public void testAddAndDeleteBlogEntriesOnSeparateNodes() throws Exception {
		long groupId = TestPropsValues.getGroupId();
		long userId = TestPropsValues.getUserId();

		BlogsEntry blogsEntry1 = _tomcatNode1.syncExecute(
			() -> BlogsEntryLocalServiceUtil.addEntry(
				userId, "Blogs Entry1 Title", "Blogs Entry1 Content",
				ServiceContextTestUtil.getServiceContext(groupId, userId)));

		Assert.assertEquals(
			blogsEntry1,
			_tomcatNode2.syncExecute(
				() -> BlogsEntryLocalServiceUtil.fetchBlogsEntry(
					blogsEntry1.getEntryId())));

		BlogsEntry blogsEntry2 = _tomcatNode2.syncExecute(
			() -> BlogsEntryLocalServiceUtil.addEntry(
				userId, "Blogs Entry2 Title", "Blogs Entry2 Content",
				ServiceContextTestUtil.getServiceContext(groupId, userId)));

		Assert.assertEquals(
			blogsEntry2,
			_tomcatNode1.syncExecute(
				() -> BlogsEntryLocalServiceUtil.fetchBlogsEntry(
					blogsEntry2.getEntryId())));

		Hits hits = _getSearchHits(_tomcatNode1, "Entry2");

		Assert.assertEquals(hits.toString(), 1, hits.getLength());

		Document document = hits.doc(0);

		Assert.assertEquals(
			String.valueOf(blogsEntry2.getEntryId()),
			document.get(Field.ENTRY_CLASS_PK));

		_tomcatNode2.syncExecute(
			() -> {
				BlogsEntryLocalServiceUtil.deleteEntry(blogsEntry2);

				return null;
			});

		Assert.assertEquals(
			blogsEntry1,
			_tomcatNode1.syncExecute(
				() -> BlogsEntryLocalServiceUtil.fetchBlogsEntry(
					blogsEntry1.getEntryId())));

		Assert.assertNull(
			_tomcatNode1.syncExecute(
				() -> BlogsEntryLocalServiceUtil.fetchBlogsEntry(
					blogsEntry2.getEntryId())));

		hits = _getSearchHits(_tomcatNode1, "Entry2");

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	@Test
	public void testCanAddCategoryToDocumentOnSlaveNode() throws Exception {
		long groupId = TestPropsValues.getGroupId();
		long userId = TestPropsValues.getUserId();

		AssetCategory assetCategory = _tomcatNode1.syncExecute(
			() -> {
				AssetVocabulary assetVocabulary =
					AssetVocabularyLocalServiceUtil.addVocabulary(
						userId, groupId, "Vocabulary Name 1",
						ServiceContextTestUtil.getServiceContext(
							groupId, userId));

				return AssetCategoryLocalServiceUtil.addCategory(
					userId, groupId, "Category Name 1",
					assetVocabulary.getVocabularyId(),
					ServiceContextTestUtil.getServiceContext(groupId, userId));
			});

		FileEntry fileEntry = _tomcatNode1.syncExecute(
			() -> DLAppLocalServiceUtil.addFileEntry(
				null, userId, groupId,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Document_1.docx",
				"application/docx", TestDataConstants.TEST_BYTE_ARRAY, null,
				null, null,
				ServiceContextTestUtil.getServiceContext(groupId, userId)));

		_tomcatNode2.syncExecute(
			() -> {
				AssetEntryLocalServiceUtil.updateEntry(
					userId, groupId, DLFileEntry.class.getName(),
					fileEntry.getFileEntryId(),
					new long[] {assetCategory.getCategoryId()}, null);

				return null;
			});

		AssetEntry assetEntry = _tomcatNode1.syncExecute(
			() -> AssetEntryLocalServiceUtil.getEntry(
				DLFileEntry.class.getName(), fileEntry.getFileEntryId()));

		Assert.assertArrayEquals(
			new long[] {assetCategory.getCategoryId()},
			assetEntry.getCategoryIds());
	}

	@Test
	public void testCanCreateVirtualInstanceWithClustering() throws Exception {
		_testCanCreateVirtualInstanceWithClustering(_tomcatNode2, _tomcatNode1);
	}

	@Test
	public void testCanInvokeMethods() throws Exception {
		String tomcatNode1ClusterNodeId = _tomcatNode1.syncExecute(
			ClusterGeneralTest::_getLocalClusterNodeId);

		String tomcatNode2ClusterNodeId = _tomcatNode2.syncExecute(
			ClusterGeneralTest::_getLocalClusterNodeId);

		String masterNodeClusterNodeId = tomcatNode2ClusterNodeId;

		if (_tomcatNode1.syncExecute(ClusterMasterExecutorUtil::isMaster)) {
			masterNodeClusterNodeId = tomcatNode1ClusterNodeId;
		}

		Assert.assertEquals(
			tomcatNode2ClusterNodeId,
			_tomcatNode1.syncExecute(
				() -> _testInvokeMethod(ClusterExecutorUtil.class)));

		Assert.assertEquals(
			tomcatNode1ClusterNodeId,
			_tomcatNode2.syncExecute(
				() -> _testInvokeMethod(ClusterExecutorUtil.class)));

		Assert.assertEquals(
			masterNodeClusterNodeId,
			_tomcatNode1.syncExecute(
				() -> _testInvokeMethodOnMaster(ClusterExecutorUtil.class)));

		Assert.assertEquals(
			masterNodeClusterNodeId,
			_tomcatNode2.syncExecute(
				() -> _testInvokeMethodOnMaster(ClusterExecutorUtil.class)));

		Assert.assertEquals(
			tomcatNode2ClusterNodeId,
			_tomcatNode1.syncExecute(
				() -> _testInvokeMethod(ClusterSampleClass.class)));

		Assert.assertEquals(
			tomcatNode1ClusterNodeId,
			_tomcatNode2.syncExecute(
				() -> _testInvokeMethod(ClusterSampleClass.class)));

		Assert.assertEquals(
			masterNodeClusterNodeId,
			_tomcatNode1.syncExecute(
				() -> _testInvokeMethodOnMaster(ClusterSampleClass.class)));

		Assert.assertEquals(
			masterNodeClusterNodeId,
			_tomcatNode2.syncExecute(
				() -> _testInvokeMethodOnMaster(ClusterSampleClass.class)));
	}

	@Test
	public void testCanUpdateLogLevelsForAllNodesFromMaster() throws Exception {
		_testCanUpdateLogLevelsForAllNodes(_tomcatNode2, _tomcatNode1, true);
	}

	@Test
	public void testCanUpdateLogLevelsForAllNodesFromSlave() throws Exception {
		_testCanUpdateLogLevelsForAllNodes(_tomcatNode1, _tomcatNode2, false);
	}

	@Test
	public void testCanUpdatePortalPropertiesWithMultipleClusters()
		throws Exception {

		_testCanUpdatePortalPropertiesWithMultipleClusters(
			_tomcatNode1, "emailAddress");
		_testCanUpdatePortalPropertiesWithMultipleClusters(
			_tomcatNode2, "emailAddress");

		String originalNode2AuthType = _tomcatNode2.syncExecute(
			() -> ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "COMPANY_SECURITY_AUTH_TYPE", "screenName"));

		try {
			_testCanUpdatePortalPropertiesWithMultipleClusters(
				_tomcatNode1, "emailAddress");
			_testCanUpdatePortalPropertiesWithMultipleClusters(
				_tomcatNode2, "screenName");

			String originalNode1AuthType = _tomcatNode1.syncExecute(
				() -> ReflectionTestUtil.getAndSetFieldValue(
					PropsValues.class, "COMPANY_SECURITY_AUTH_TYPE",
					"screenName"));

			try {
				_testCanUpdatePortalPropertiesWithMultipleClusters(
					_tomcatNode1, "screenName");
				_testCanUpdatePortalPropertiesWithMultipleClusters(
					_tomcatNode2, "screenName");
			}
			finally {
				_tomcatNode1.syncExecute(
					() -> ReflectionTestUtil.getAndSetFieldValue(
						PropsValues.class, "COMPANY_SECURITY_AUTH_TYPE",
						originalNode1AuthType));
			}
		}
		finally {
			_tomcatNode2.syncExecute(
				() -> ReflectionTestUtil.getAndSetFieldValue(
					PropsValues.class, "COMPANY_SECURITY_AUTH_TYPE",
					originalNode2AuthType));
		}
	}

	@Test
	public void testEnableAndDisableFeatureFlag() throws Exception {
		_testEnableAndDisableFeatureFlag(_tomcatNode1, _tomcatNode2);
		_testEnableAndDisableFeatureFlag(_tomcatNode2, _tomcatNode1);
	}

	@Test
	public void testLanguageOverrideSyncsBetweenNodes() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		String key = RandomTestUtil.randomString();

		String languageId = "en_US";

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		String value = RandomTestUtil.randomString();

		long ploEntryId = _tomcatNode1.syncExecute(
			() -> {
				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							companyId)) {

					PLOEntry ploEntry =
						PLOEntryLocalServiceUtil.addOrUpdatePLOEntry(
							null, companyId, TestPropsValues.getUserId(), key,
							languageId, value);

					return ploEntry.getPloEntryId();
				}
			});

		Assert.assertEquals(
			value,
			_tomcatNode2.syncExecute(
				() -> {
					try (SafeCloseable safeCloseable =
							CompanyThreadLocal.setCompanyIdWithSafeCloseable(
								companyId)) {

						return LanguageUtil.get(locale, key);
					}
				}));

		Assert.assertEquals(
			key,
			_tomcatNode1.syncExecute(
				() -> {
					try (SafeCloseable safeCloseable =
							CompanyThreadLocal.setCompanyIdWithSafeCloseable(
								companyId)) {

						PLOEntryLocalServiceUtil.deletePLOEntry(ploEntryId);

						return LanguageUtil.get(locale, key);
					}
				}));
		Assert.assertEquals(
			key,
			_tomcatNode2.syncExecute(
				() -> {
					try (SafeCloseable safeCloseable =
							CompanyThreadLocal.setCompanyIdWithSafeCloseable(
								companyId)) {

						return LanguageUtil.get(locale, key);
					}
				}));
	}

	@Test
	public void testShutdownAndStartupNodes() throws Exception {

		// Assert node 1 and node 2 can see each other

		_assertNodesVisibleToEachOther(_tomcatNode1, _tomcatNode2);

		// Restart node 2, use node 1 as the verifier node

		_restartAndVerifyNode(_tomcatNode2, _tomcatNode1);
	}

	@Test
	public void testSlaveNodeCanBecomeMasterNode() throws Exception {

		// Assert node 1 is the master node

		Assert.assertTrue(
			_tomcatNode1.syncExecute(ClusterMasterExecutorUtil::isMaster));

		// Assert node 2 is a slave node

		Assert.assertFalse(
			_tomcatNode2.syncExecute(ClusterMasterExecutorUtil::isMaster));

		// Register a listener for node 2

		_tomcatNode2.syncExecute(
			() -> {
				TestClusterMasterTokenTransitionListener.register();

				return null;
			});

		// Stop node 1

		_tomcatNode1.stop();

		// After node 1 stops, confirm that node 2 is the new master node

		Assert.assertTrue(
			_tomcatNode2.syncExecute(
				() -> {
					TestClusterMasterTokenTransitionListener.await();

					return ClusterMasterExecutorUtil.isMaster();
				}));

		// Restart node 1

		_tomcatNode1.start(true);

		// Assert node 2 is still the master node

		Assert.assertTrue(
			_tomcatNode2.syncExecute(ClusterMasterExecutorUtil::isMaster));

		// Assert node 1 is still a slave node

		Assert.assertFalse(
			_tomcatNode1.syncExecute(ClusterMasterExecutorUtil::isMaster));
	}

	@Test
	public void testValidateFileEntryOnSeparateNodes() throws Exception {
		long groupId = TestPropsValues.getGroupId();
		long userId = TestPropsValues.getUserId();

		_testValidateFileEntryOnSeparateNodes(
			groupId, userId, RandomTestUtil.randomString(), _tomcatNode1,
			_tomcatNode2);
		_testValidateFileEntryOnSeparateNodes(
			groupId, userId, RandomTestUtil.randomString(), _tomcatNode2,
			_tomcatNode1);
	}

	@Test
	public void testValidatePublishOnTwoNodes() throws Exception {
		long userId = TestPropsValues.getUserId();

		TomcatNode masterTomcatNode = _tomcatNode2;
		TomcatNode slaveTomcatNode = _tomcatNode1;

		if (_tomcatNode1.syncExecute(ClusterMasterExecutorUtil::isMaster)) {
			masterTomcatNode = _tomcatNode1;
			slaveTomcatNode = _tomcatNode2;
		}

		Object[] values = masterTomcatNode.syncExecute(
			() -> {
				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						TestPropsValues.getUser()));

				try {
					Group liveGroup = GroupTestUtil.addGroup();

					long liveGroupId = liveGroup.getGroupId();

					StagingLocalServiceUtil.enableLocalStaging(
						userId, liveGroup, false, false, new ServiceContext());

					liveGroup = GroupLocalServiceUtil.getGroup(liveGroupId);

					Group stagingGroup = liveGroup.getStagingGroup();

					long stagingGroupId = stagingGroup.getGroupId();

					return new Object[] {
						liveGroupId, stagingGroupId,
						LayoutTestUtil.addTypePortletLayout(
							stagingGroupId, "Staging Test Page", false),
						BlogsEntryLocalServiceUtil.addEntry(
							userId, "Blogs Entry Title", "Blogs Entry Content",
							ServiceContextTestUtil.getServiceContext(
								stagingGroupId, userId))
					};
				}
				finally {
					PermissionThreadLocal.setPermissionChecker(null);
				}
			});

		long liveGroupId = (Long)values[0];
		long stagingGroupId = (Long)values[1];
		Layout stagingLayout = (Layout)values[2];
		BlogsEntry stagingBlogsEntry = (BlogsEntry)values[3];

		_publishLayouts(
			masterTomcatNode, masterTomcatNode, userId, stagingGroupId,
			liveGroupId, stagingLayout.getLayoutId());

		_assertEqualOnBothNodes(
			masterTomcatNode, slaveTomcatNode,
			() -> LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				stagingLayout.getUuid(), liveGroupId, false));
		_assertEqualOnBothNodes(
			masterTomcatNode, slaveTomcatNode,
			() -> BlogsEntryLocalServiceUtil.fetchBlogsEntryByUuidAndGroupId(
				stagingBlogsEntry.getUuid(), liveGroupId));

		BlogsEntry liveBlogsEntryFromMaster = masterTomcatNode.syncExecute(
			() -> BlogsEntryLocalServiceUtil.fetchBlogsEntryByUuidAndGroupId(
				stagingBlogsEntry.getUuid(), liveGroupId));

		JournalArticle stagingArticleFromSlave = slaveTomcatNode.syncExecute(
			() -> JournalTestUtil.addArticle(
				stagingGroupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				"WebContent Title", "WebContent Content",
				LocaleUtil.getSiteDefault(), false, true));

		_publishLayouts(
			slaveTomcatNode, masterTomcatNode, userId, stagingGroupId,
			liveGroupId, stagingLayout.getLayoutId());

		Assert.assertEquals(
			liveBlogsEntryFromMaster,
			masterTomcatNode.syncExecute(
				() ->
					BlogsEntryLocalServiceUtil.fetchBlogsEntryByUuidAndGroupId(
						stagingBlogsEntry.getUuid(), liveGroupId)));

		_assertEqualOnBothNodes(
			masterTomcatNode, slaveTomcatNode,
			() ->
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						stagingArticleFromSlave.getUuid(), liveGroupId));
	}

	private static String _getLocalClusterNodeId() {
		ClusterNode localClusterNode =
			ClusterExecutorUtil.getLocalClusterNode();

		return localClusterNode.getClusterNodeId();
	}

	private <T extends Serializable> void _assertEqualOnBothNodes(
			TomcatNode masterTomcatNode, TomcatNode slaveTomcatNode,
			TomcatNode.ClusterExecutable<T> clusterExecutable)
		throws Exception {

		T masterValue = masterTomcatNode.syncExecute(clusterExecutable);
		T slaveValue = slaveTomcatNode.syncExecute(clusterExecutable);

		Assert.assertNotNull(masterValue);
		Assert.assertNotNull(slaveValue);

		Assert.assertEquals(masterValue, slaveValue);
	}

	private void _assertNodesVisibleToEachOther(
			TomcatNode tomcatNode1, TomcatNode tomcatNode2)
		throws Exception {

		// Assert node 1 has a valid cluster node

		ClusterNode clusterNode1 = tomcatNode1.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		Assert.assertNotNull(clusterNode1);

		// Assert node 2 has a valid cluster node

		ClusterNode clusterNode2 = tomcatNode2.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		Assert.assertNotNull(clusterNode2);

		// Assert node 1 can see node 2

		Assert.assertTrue(
			tomcatNode1.syncExecute(
				() -> {
					List<ClusterNode> clusterNodes =
						ClusterExecutorUtil.getClusterNodes();

					return clusterNodes.contains(clusterNode2);
				}));

		// Assert node 2 can see node 1

		Assert.assertTrue(
			tomcatNode2.syncExecute(
				() -> {
					List<ClusterNode> clusterNodes =
						ClusterExecutorUtil.getClusterNodes();

					return clusterNodes.contains(clusterNode1);
				}));
	}

	private long _authenticate(TomcatNode tomcatNode, String login)
		throws Exception {

		return tomcatNode.syncExecute(
			() -> AuthenticatedSessionManagerUtil.getAuthenticatedUserId(
				new MockHttpServletRequest(), login,
				TestPropsValues.USER_PASSWORD, null));
	}

	private AutoCloseable _disableClusterableAdviceCallMasterTimeout(
			TomcatNode tomcatNode)
		throws Exception {

		long originaTimeout = tomcatNode.syncExecute(
			() -> ReflectionTestUtil.getAndSetFieldValue(
				ClusterableInvokerUtil.class,
				"_CLUSTERABLE_ADVICE_CALL_MASTER_TIMEOUT", 0L));

		return () -> tomcatNode.syncExecute(
			() -> ReflectionTestUtil.getAndSetFieldValue(
				ClusterableInvokerUtil.class,
				"_CLUSTERABLE_ADVICE_CALL_MASTER_TIMEOUT", originaTimeout));
	}

	private MVCActionCommand _getEditServerMVCActionCommand()
		throws InvalidSyntaxException {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Collection<ServiceReference<MVCActionCommand>> serviceReferences =
			bundleContext.getServiceReferences(
				MVCActionCommand.class,
				"(mvc.command.name=/server_admin/edit_server)");

		Iterator<ServiceReference<MVCActionCommand>> iterator =
			serviceReferences.iterator();

		ServiceReference<MVCActionCommand>
			editServerMVCActionCommandServiceReference = iterator.next();

		return bundleContext.getService(
			editServerMVCActionCommandServiceReference);
	}

	private Hits _getSearchHits(TomcatNode tomcatNode, String keywords)
		throws Exception {

		return tomcatNode.syncExecute(
			() -> {
				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						TestPropsValues.getUser()));

				try {
					Indexer<BlogsEntry> indexer =
						IndexerRegistryUtil.getIndexer(BlogsEntry.class);

					SearchContext searchContext =
						SearchContextTestUtil.getSearchContext();

					searchContext.setKeywords(keywords);

					return indexer.search(searchContext);
				}
				finally {
					PermissionThreadLocal.setPermissionChecker(null);
				}
			});
	}

	private void _publishLayouts(
			TomcatNode invokerTomcatNode, TomcatNode masterTomcatNode,
			long userId, long stagingGroupId, long liveGroupId, long layoutId)
		throws Exception {

		long exportImportConfigurationId = invokerTomcatNode.syncExecute(
			() -> {
				ExportImportConfiguration exportImportConfiguration =
					ExportImportConfigurationLocalServiceUtil.
						addDraftExportImportConfiguration(
							userId,
							ExportImportConfigurationConstants.
								TYPE_PUBLISH_LAYOUT_LOCAL,
							ExportImportConfigurationSettingsMapFactoryUtil.
								buildPublishLayoutLocalSettingsMap(
									UserLocalServiceUtil.getUser(userId),
									stagingGroupId, liveGroupId, false,
									new long[] {layoutId},
									ExportImportConfigurationParameterMapFactoryUtil.
										buildParameterMap()));

				return exportImportConfiguration.
					getExportImportConfigurationId();
			});

		Future<?> future = masterTomcatNode.execute(
			() -> {
				TestExportImportLifecycleListener.await(
					String.valueOf(exportImportConfigurationId));

				return null;
			});

		invokerTomcatNode.syncExecute(
			() -> {
				String originalName = PrincipalThreadLocal.getName();

				PrincipalThreadLocal.setName(userId);

				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						TestPropsValues.getUser()));

				try {
					StagingUtil.publishLayouts(
						userId, exportImportConfigurationId);

					return null;
				}
				finally {
					PermissionThreadLocal.setPermissionChecker(null);
					PrincipalThreadLocal.setName(originalName);
				}
			});

		future.get();
	}

	private void _restartAndVerifyNode(
			TomcatNode restartTomcatNode, TomcatNode verifierTomcatNode)
		throws Exception {

		// Capture both cluster nodes before stopping the restart node

		ClusterNode restartClusterNode = restartTomcatNode.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		Assert.assertNotNull(restartClusterNode);

		ClusterNode verifierClusterNode = verifierTomcatNode.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		Assert.assertNotNull(verifierClusterNode);

		// Stop the restart node

		restartTomcatNode.stop();

		// Assert the verifier node still retains the same cluster node ID

		Assert.assertEquals(
			verifierClusterNode.getClusterNodeId(),
			verifierTomcatNode.syncExecute(
				ClusterGeneralTest::_getLocalClusterNodeId));

		// Assert verifier node can no longer see the restart node

		Assert.assertFalse(
			verifierTomcatNode.syncExecute(
				() -> {
					List<ClusterNode> clusterNodes =
						ClusterExecutorUtil.getClusterNodes();

					return clusterNodes.contains(restartClusterNode);
				}));

		// Restart restart node

		restartTomcatNode.start(true);

		// Assert restart node has a new valid cluster node

		ClusterNode newRestartClusterNode = restartTomcatNode.syncExecute(
			ClusterExecutorUtil::getLocalClusterNode);

		Assert.assertNotNull(newRestartClusterNode);

		// Assert verifier node still retains the same cluster node ID

		Assert.assertEquals(
			verifierClusterNode.getClusterNodeId(),
			verifierTomcatNode.syncExecute(
				ClusterGeneralTest::_getLocalClusterNodeId));

		// Assert mutual visibility with the new restart node

		_assertNodesVisibleToEachOther(restartTomcatNode, verifierTomcatNode);
	}

	private void _setEnabledForFeatureFlags(
			long companyId, String key, boolean enabled)
		throws Exception {

		SystemBundleUtil.callService(
			"com.liferay.feature.flag.web.internal.feature.flag." +
				"FeatureFlagsBagProvider",
			featureFlagsBagProvider -> {
				ReflectionTestUtil.invoke(
					featureFlagsBagProvider, "setEnabled",
					new Class<?>[] {long.class, String.class, boolean.class},
					companyId, key, enabled);

				return null;
			});
	}

	private void _testCanCreateVirtualInstanceWithClustering(
			TomcatNode mutatorTomcatNode, TomcatNode observerTomcatNode)
		throws Exception {

		try (AutoCloseable autoCloseable1 =
				_disableClusterableAdviceCallMasterTimeout(mutatorTomcatNode);
			AutoCloseable autoCloseable2 =
				_disableClusterableAdviceCallMasterTimeout(
					observerTomcatNode)) {

			long companyId = mutatorTomcatNode.syncExecute(
				() -> {
					Company company = CompanyTestUtil.addCompany();

					return company.getCompanyId();
				});

			Assert.assertNotNull(
				observerTomcatNode.syncExecute(
					() -> CompanyLocalServiceUtil.fetchCompany(companyId)));

			observerTomcatNode.syncExecute(
				() -> {
					TestPortalCacheListener.register(companyId);

					return null;
				});

			Assert.assertNull(
				mutatorTomcatNode.syncExecute(
					() -> {
						CompanyLocalServiceUtil.deleteCompany(companyId);

						return CompanyLocalServiceUtil.fetchCompany(companyId);
					}));
			Assert.assertNull(
				observerTomcatNode.syncExecute(
					() -> {
						TestPortalCacheListener.await();

						return CompanyLocalServiceUtil.fetchCompany(companyId);
					}));
		}
	}

	private void _testCanUpdateLogLevelsForAllNodes(
			TomcatNode receiverTomcatNode, TomcatNode senderTomcatNode,
			boolean senderTomcatNodeIsMaster)
		throws Exception {

		// Assert sender node is the master node when
		// senderTomcatNodeIsMaster is true

		Assert.assertEquals(
			senderTomcatNodeIsMaster,
			senderTomcatNode.syncExecute(ClusterMasterExecutorUtil::isMaster));

		// Assert receiver node is the master node when
		// senderTomcatNodeIsMaster is false

		Assert.assertEquals(
			!senderTomcatNodeIsMaster,
			receiverTomcatNode.syncExecute(
				ClusterMasterExecutorUtil::isMaster));

		// Register listener for receiver node

		receiverTomcatNode.syncExecute(
			() -> {
				TestPropertyChangeListener.register();

				return null;
			});

		// Update log levels in sender node and assert change

		Assert.assertEquals(
			"DEBUG",
			senderTomcatNode.syncExecute(
				() -> {
					ReflectionTestUtil.invoke(
						_getEditServerMVCActionCommand(), "_updateLogLevels",
						new Class<?>[] {Map.class},
						Collections.singletonMap(
							ClusterGeneralTest.class.getName(), "DEBUG"));

					return Log4JUtil.getPriority(
						ClusterGeneralTest.class.getName());
				}));

		// Assert the change in receiver node

		Assert.assertEquals(
			"DEBUG",
			receiverTomcatNode.syncExecute(
				() -> {
					TestPropertyChangeListener.await();

					return Log4JUtil.getPriority(
						ClusterGeneralTest.class.getName());
				}));

		// Register listener for receiver node

		receiverTomcatNode.syncExecute(
			() -> {
				TestPropertyChangeListener.register();

				return null;
			});

		// Update log levels in sender node and assert change

		Assert.assertEquals(
			"ERROR",
			senderTomcatNode.syncExecute(
				() -> {
					ReflectionTestUtil.invoke(
						_getEditServerMVCActionCommand(), "_updateLogLevels",
						new Class<?>[] {Map.class},
						Collections.singletonMap(
							ClusterGeneralTest.class.getName(), "ERROR"));

					return Log4JUtil.getPriority(
						ClusterGeneralTest.class.getName());
				}));

		// Assert the change in receiver node

		Assert.assertEquals(
			"ERROR",
			receiverTomcatNode.syncExecute(
				() -> {
					TestPropertyChangeListener.await();

					return Log4JUtil.getPriority(
						ClusterGeneralTest.class.getName());
				}));
	}

	private void _testCanUpdatePortalPropertiesWithMultipleClusters(
			TomcatNode tomcatNode, String expectedAuthType)
		throws Exception {

		if (expectedAuthType.equals("emailAddress")) {
			Assert.assertEquals(
				TestPropsValues.getUserId(),
				_authenticate(tomcatNode, "test@liferay.com"));

			try {
				_authenticate(tomcatNode, "test");

				Assert.fail();
			}
			catch (AuthException authException) {
			}
		}
		else if (expectedAuthType.equals("screenName")) {
			Assert.assertEquals(
				TestPropsValues.getUserId(), _authenticate(tomcatNode, "test"));

			try {
				_authenticate(tomcatNode, "test@liferay.com");

				Assert.fail();
			}
			catch (AuthException authException) {
			}
		}
	}

	private void _testEnableAndDisableFeatureFlag(
			TomcatNode mutatorTomcatNode, TomcatNode observerTomcatNode)
		throws Exception {

		String key = "LPS-170670";

		observerTomcatNode.syncExecute(
			() -> {
				TestFeatureFlagListener.register(
					PortalUtil.getDefaultCompanyId(), key);

				return null;
			});

		Assert.assertTrue(
			mutatorTomcatNode.syncExecute(
				() -> {
					_setEnabledForFeatureFlags(
						PortalUtil.getDefaultCompanyId(), key, true);

					return FeatureFlagManagerUtil.isEnabled(
						PortalUtil.getDefaultCompanyId(), key);
				}));

		Assert.assertTrue(
			observerTomcatNode.syncExecute(
				() -> {
					TestFeatureFlagListener.await();

					return FeatureFlagManagerUtil.isEnabled(
						PortalUtil.getDefaultCompanyId(), key);
				}));

		observerTomcatNode.syncExecute(
			() -> {
				TestFeatureFlagListener.register(
					PortalUtil.getDefaultCompanyId(), key);

				return null;
			});

		Assert.assertFalse(
			mutatorTomcatNode.syncExecute(
				() -> {
					_setEnabledForFeatureFlags(
						PortalUtil.getDefaultCompanyId(), key, false);

					return FeatureFlagManagerUtil.isEnabled(
						PortalUtil.getDefaultCompanyId(), key);
				}));

		Assert.assertFalse(
			observerTomcatNode.syncExecute(
				() -> {
					TestFeatureFlagListener.await();

					return FeatureFlagManagerUtil.isEnabled(
						PortalUtil.getDefaultCompanyId(), key);
				}));
	}

	private String _testInvokeMethod(Class<?> clazz) throws Exception {
		ClusterNode localClusterNode =
			ClusterExecutorUtil.getLocalClusterNode();

		ClusterNode targetClusterNode = null;

		for (ClusterNode clusterNode : ClusterExecutorUtil.getClusterNodes()) {
			if (!clusterNode.equals(localClusterNode)) {
				targetClusterNode = clusterNode;

				break;
			}
		}

		if (targetClusterNode == null) {
			return null;
		}

		MethodKey methodKey = new MethodKey(clazz, "getLocalClusterNode");

		MethodHandler methodHandler = new MethodHandler(methodKey);

		ClusterRequest clusterRequest = ClusterRequest.createUnicastRequest(
			methodHandler, targetClusterNode.getClusterNodeId());

		FutureClusterResponses futureClusterResponses =
			ClusterExecutorUtil.execute(clusterRequest);

		ClusterNodeResponses clusterNodeResponses =
			futureClusterResponses.get();

		ClusterNodeResponse clusterNodeResponse =
			clusterNodeResponses.getClusterResponse(
				targetClusterNode.getClusterNodeId());

		ClusterNode clusterNode = (ClusterNode)clusterNodeResponse.getResult();

		return clusterNode.getClusterNodeId();
	}

	private String _testInvokeMethodOnMaster(Class<?> clazz) throws Exception {
		MethodKey methodKey = new MethodKey(clazz, "getLocalClusterNode");

		MethodHandler methodHandler = new MethodHandler(methodKey);

		Future<ClusterNode> future = ClusterMasterExecutorUtil.executeOnMaster(
			methodHandler);

		ClusterNode clusterNode = future.get();

		return clusterNode.getClusterNodeId();
	}

	private void _testValidateFileEntryOnSeparateNodes(
			long groupId, long userId, String fileName, TomcatNode tomcatNode1,
			TomcatNode tomcatNode2)
		throws Exception {

		FileEntry fileEntry = tomcatNode1.syncExecute(
			() -> DLAppLocalServiceUtil.addFileEntry(
				null, userId, groupId,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
				ContentTypes.TEXT_PLAIN, TestDataConstants.TEST_BYTE_ARRAY,
				null, null, null,
				ServiceContextTestUtil.getServiceContext(groupId, userId)));

		FileEntry syncedFileEntry = tomcatNode2.syncExecute(
			() -> DLAppLocalServiceUtil.getFileEntry(
				fileEntry.getFileEntryId()));

		Assert.assertEquals(fileEntry, syncedFileEntry);
	}

	private static transient TomcatNode _tomcatNode1;
	private static transient TomcatNode _tomcatNode2;

	private static class ClusterSampleClass {

		public static ClusterNode getLocalClusterNode() {
			return ClusterExecutorUtil.getLocalClusterNode();
		}

	}

	private static class TestClusterMasterTokenTransitionListener
		implements ClusterMasterTokenTransitionListener {

		public static void await() throws Exception {
			Map<String, Object> attributes =
				LocalProcessLauncher.ProcessContext.getAttributes();

			TestClusterMasterTokenTransitionListener
				testClusterMasterTokenTransitionListener =
					(TestClusterMasterTokenTransitionListener)attributes.remove(
						TestClusterMasterTokenTransitionListener.class.
							getName());

			CountDownLatch countDownLatch =
				testClusterMasterTokenTransitionListener._countDownLatch;

			countDownLatch.await();

			ServiceRegistration<?> serviceRegistration =
				testClusterMasterTokenTransitionListener._serviceRegistration;

			serviceRegistration.unregister();
		}

		public static void register() {
			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			TestClusterMasterTokenTransitionListener
				testClusterMasterTokenTransitionListener =
					new TestClusterMasterTokenTransitionListener();

			testClusterMasterTokenTransitionListener._serviceRegistration =
				bundleContext.registerService(
					ClusterMasterTokenTransitionListener.class,
					testClusterMasterTokenTransitionListener, null);

			Map<String, Object> attributes =
				LocalProcessLauncher.ProcessContext.getAttributes();

			attributes.put(
				TestClusterMasterTokenTransitionListener.class.getName(),
				testClusterMasterTokenTransitionListener);
		}

		@Override
		public void masterTokenAcquired() {
			_countDownLatch.countDown();
		}

		@Override
		public void masterTokenReleased() {
		}

		private final CountDownLatch _countDownLatch = new CountDownLatch(1);
		private ServiceRegistration<?> _serviceRegistration;

	}

	private static class TestExportImportLifecycleListener
		implements ExportImportLifecycleListener {

		public static void await(String processId) throws Exception {
			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			CountDownLatch countDownLatch = new CountDownLatch(1);

			TestExportImportLifecycleListener
				testExportImportLifecycleListener =
					new TestExportImportLifecycleListener(
						countDownLatch, processId);

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					ExportImportLifecycleListener.class,
					testExportImportLifecycleListener, null);

			countDownLatch.await();

			serviceRegistration.unregister();

			testExportImportLifecycleListener._rethrow();
		}

		@Override
		public boolean isParallel() {
			return false;
		}

		@Override
		public void onExportImportLifecycleEvent(
			ExportImportLifecycleEvent exportImportLifecycleEvent) {

			if (!_processId.equals(exportImportLifecycleEvent.getProcessId())) {
				return;
			}

			int code = exportImportLifecycleEvent.getCode();

			if (code ==
					ExportImportLifecycleConstants.
						EVENT_PUBLICATION_LAYOUT_LOCAL_SUCCEEDED) {

				_countDownLatch.countDown();
			}
			else if (code ==
						ExportImportLifecycleConstants.
							EVENT_PUBLICATION_LAYOUT_LOCAL_FAILED) {

				for (Serializable attribute :
						exportImportLifecycleEvent.getAttributes()) {

					if (attribute instanceof Throwable) {
						_throwable = (Throwable)attribute;

						break;
					}
				}

				_countDownLatch.countDown();
			}
		}

		private TestExportImportLifecycleListener(
			CountDownLatch countDownLatch, String processId) {

			_countDownLatch = countDownLatch;
			_processId = processId;
		}

		private void _rethrow() {
			if (_throwable != null) {
				ReflectionUtil.throwException(_throwable);
			}
		}

		private final CountDownLatch _countDownLatch;
		private final String _processId;
		private volatile Throwable _throwable;

	}

	private static class TestFeatureFlagListener
		implements FeatureFlagListener {

		public static void await() throws Exception {
			Map<String, Object> attributes =
				LocalProcessLauncher.ProcessContext.getAttributes();

			TestFeatureFlagListener testFeatureFlagListener =
				(TestFeatureFlagListener)attributes.remove(
					TestFeatureFlagListener.class.getName());

			CountDownLatch countDownLatch =
				testFeatureFlagListener._countDownLatch;

			countDownLatch.await();

			ServiceRegistration<?> serviceRegistration =
				testFeatureFlagListener._serviceRegistration;

			serviceRegistration.unregister();
		}

		public static void register(long companyId, String featureFlagKey) {
			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			TestFeatureFlagListener testFeatureFlagListener =
				new TestFeatureFlagListener(companyId);

			testFeatureFlagListener._serviceRegistration =
				bundleContext.registerService(
					FeatureFlagListener.class, testFeatureFlagListener,
					MapUtil.singletonDictionary(
						"feature.flag.key", featureFlagKey));

			Map<String, Object> attributes =
				LocalProcessLauncher.ProcessContext.getAttributes();

			attributes.put(
				TestFeatureFlagListener.class.getName(),
				testFeatureFlagListener);
		}

		@Override
		public void onValue(
			long companyId, String featureFlagKey, boolean enabled) {

			if (companyId == _companyId) {
				_countDownLatch.countDown();
			}
		}

		private TestFeatureFlagListener(long companyId) {
			_companyId = companyId;
		}

		private final long _companyId;
		private final CountDownLatch _countDownLatch = new CountDownLatch(2);
		private ServiceRegistration<?> _serviceRegistration;

	}

	private static class TestPortalCacheListener
		implements PortalCacheListener<Long, CacheModel<?>> {

		public static void await() throws Exception {
			Map<String, Object> attributes =
				LocalProcessLauncher.ProcessContext.getAttributes();

			AutoCloseable autoCloseable = (AutoCloseable)attributes.remove(
				TestPortalCacheListener.class.getName());

			autoCloseable.close();
		}

		public static void register(long companyId) {
			PortalCacheManager<? extends Serializable, ?> portalCacheManager =
				PortalCacheManagerProvider.getPortalCacheManager(
					PortalCacheManagerNames.MULTI_VM);

			PortalCache<Long, CacheModel<?>> portalCache =
				(PortalCache<Long, CacheModel<?>>)
					portalCacheManager.fetchPortalCache(
						EntityCache.class.getName() + "." +
							CompanyImpl.class.getName());

			TestPortalCacheListener testPortalCacheListener =
				new TestPortalCacheListener(companyId);

			portalCache.registerPortalCacheListener(testPortalCacheListener);

			CountDownLatch countDownLatch =
				testPortalCacheListener._countDownLatch;

			Map<String, Object> attributes =
				LocalProcessLauncher.ProcessContext.getAttributes();

			attributes.put(
				TestPortalCacheListener.class.getName(),
				(AutoCloseable)() -> {
					countDownLatch.await();

					portalCache.unregisterPortalCacheListener(
						testPortalCacheListener);
				});
		}

		@Override
		public void dispose() {
		}

		@Override
		public void notifyEntryEvicted(
			PortalCache<Long, CacheModel<?>> portalCache, Long key,
			CacheModel<?> value, int timeToLive) {
		}

		@Override
		public void notifyEntryExpired(
			PortalCache<Long, CacheModel<?>> portalCache, Long key,
			CacheModel<?> value, int timeToLive) {
		}

		@Override
		public void notifyEntryPut(
			PortalCache<Long, CacheModel<?>> portalCache, Long key,
			CacheModel<?> value, int timeToLive) {
		}

		@Override
		public void notifyEntryRemoved(
			PortalCache<Long, CacheModel<?>> portalCache, Long key,
			CacheModel<?> value, int timeToLive) {

			if (key == _companyId) {
				_countDownLatch.countDown();
			}
		}

		@Override
		public void notifyEntryUpdated(
			PortalCache<Long, CacheModel<?>> portalCache, Long key,
			CacheModel<?> value, int timeToLive) {
		}

		@Override
		public void notifyRemoveAll(
			PortalCache<Long, CacheModel<?>> portalCache) {
		}

		private TestPortalCacheListener(long companyId) {
			_companyId = companyId;
		}

		private final long _companyId;
		private final CountDownLatch _countDownLatch = new CountDownLatch(1);

	}

	private static class TestPropertyChangeListener
		implements PropertyChangeListener {

		public static void await() throws Exception {
			Map<String, Object> attributes =
				LocalProcessLauncher.ProcessContext.getAttributes();

			TestPropertyChangeListener testPropertyChangeListener =
				(TestPropertyChangeListener)attributes.remove(
					TestPropertyChangeListener.class.getName());

			CountDownLatch countDownLatch =
				testPropertyChangeListener._countDownLatch;

			countDownLatch.await();

			LoggerContext loggerContext = LoggerContext.getContext();

			loggerContext.removePropertyChangeListener(
				testPropertyChangeListener);
		}

		public static void register() {
			LoggerContext loggerContext = LoggerContext.getContext();

			TestPropertyChangeListener testPropertyChangeListener =
				new TestPropertyChangeListener();

			loggerContext.addPropertyChangeListener(testPropertyChangeListener);

			Map<String, Object> attributes =
				LocalProcessLauncher.ProcessContext.getAttributes();

			attributes.put(
				TestPropertyChangeListener.class.getName(),
				testPropertyChangeListener);
		}

		@Override
		public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
			_countDownLatch.countDown();
		}

		private final CountDownLatch _countDownLatch = new CountDownLatch(1);

	}

}