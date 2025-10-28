/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v3_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.lang.reflect.Method;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();

		_db = DBManagerUtil.getDB();

		_dbInspector = new DBInspector(_connection);

		_layout = LayoutTestUtil.addTypeContentPublishedLayout(
			_groupLocalService.fetchGroup(TestPropsValues.getGroupId()),
			RandomTestUtil.randomString(), WorkflowConstants.STATUS_APPROVED);

		_draftLayout = _layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());

		_updateFragmentEntryLinks();
	}

	@After
	public void tearDown() throws Exception {
		DataAccess.cleanUp(_connection);
	}

	@Test
	public void testUpgrade() throws Exception {
		Group globalGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		ServiceContext globalServiceContext =
			ServiceContextTestUtil.getServiceContext(
				globalGroup.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection globalFragmentCollection =
			FragmentTestUtil.addFragmentCollection(
				globalServiceContext.getScopeGroupId());

		FragmentEntry globalFragmentEntry =
			FragmentEntryTestUtil.addFragmentEntry(
				globalFragmentCollection.getFragmentCollectionId());

		FragmentEntryLink globalDraftLayoutFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				null, globalFragmentEntry.getCss(),
				globalFragmentEntry.getConfiguration(),
				globalFragmentEntry.getExternalReferenceCode(),
				globalFragmentEntry.getScopeERC(),
				globalFragmentEntry.getHtml(), globalFragmentEntry.getJs(),
				_draftLayout, globalFragmentEntry.getFragmentEntryKey(),
				globalFragmentEntry.getType(), null, 0, _segmentsExperienceId);

		FragmentEntryLink draftLayoutFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				null, _draftLayout, _segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_layout.getGroupId(), _layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 2, fragmentEntryLinks.size());

		FragmentEntryLink globalPublishedLayoutFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				_layout.getGroupId(),
				globalDraftLayoutFragmentEntryLink.getExternalReferenceCode(),
				_layout.getPlid());

		FragmentEntryLink publishedLayoutFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				_layout.getGroupId(),
				draftLayoutFragmentEntryLink.getExternalReferenceCode(),
				_layout.getPlid());

		List<Long> fragmentEntryLinkIds = Arrays.asList(
			draftLayoutFragmentEntryLink.getFragmentEntryLinkId(),
			globalDraftLayoutFragmentEntryLink.getFragmentEntryLinkId(),
			publishedLayoutFragmentEntryLink.getFragmentEntryLinkId(),
			globalPublishedLayoutFragmentEntryLink.getFragmentEntryLinkId());

		Map<Long, Map<String, Object>> expectedValuesMap = _getExpectedValues(
			fragmentEntryLinkIds);

		_updateFragmentEntryLinks(expectedValuesMap, fragmentEntryLinkIds);

		runUpgrade();

		_assertFragmentEntryLinks(expectedValuesMap, fragmentEntryLinkIds);
		_assertFragmentEntryLinkTableColumns();
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			"{}", _draftLayout, _segmentsExperienceId);
	}

	@Override
	protected CTService<?> getCTService() {
		return _fragmentEntryLinkLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(3, 0, 0));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			Class<?> upgradeProcessClass = upgradeProcess.getClass();

			Method getPostUpgradeStepsMethod =
				upgradeProcessClass.getDeclaredMethod("getPostUpgradeSteps");

			getPostUpgradeStepsMethod.setAccessible(true);

			UpgradeStep[] postUpgradeSteps =
				(UpgradeStep[])getPostUpgradeStepsMethod.invoke(upgradeProcess);

			upgradeProcess.upgrade();

			for (UpgradeStep postUpgradeStep : postUpgradeSteps) {
				postUpgradeStep.upgrade();
			}
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		FragmentEntryLink fragmentEntryLink = (FragmentEntryLink)ctModel;

		return _fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())
			).toString(),
			true);
	}

	private void _assertFragmentEntryLinks(
			Map<Long, Map<String, Object>> expectedValuesMap,
			List<Long> fragmentEntryLinkIds)
		throws Exception {

		for (long fragmentEntryLinkId : fragmentEntryLinkIds) {
			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLinkId);

			Map<String, Object> expectedValues = expectedValuesMap.get(
				fragmentEntryLinkId);

			Assert.assertEquals(
				expectedValues.get("fragmentEntryId"),
				_getFragmentEntryId(fragmentEntryLink));
			Assert.assertEquals(
				expectedValues.get("fragmentEntryERC"),
				fragmentEntryLink.getFragmentEntryERC());
			Assert.assertEquals(
				expectedValues.get("fragmentEntryScopeERC"),
				fragmentEntryLink.getFragmentEntryScopeERC());
			Assert.assertEquals(
				expectedValues.get("originalFragmentEntryLinkERC"),
				fragmentEntryLink.getOriginalFragmentEntryLinkERC());
			Assert.assertEquals(
				expectedValues.get("originalFragmentEntryLinkId"),
				_getOriginalFragmentEntryLinkId(fragmentEntryLink));
		}
	}

	private void _assertFragmentEntryLinkTableColumns() throws Exception {
		Assert.assertFalse(
			_dbInspector.hasColumn("FragmentEntryLink", "fragmentEntryId"));
		Assert.assertTrue(
			_dbInspector.hasColumn("FragmentEntryLink", "fragmentEntryERC"));
		Assert.assertTrue(
			_dbInspector.hasColumn(
				"FragmentEntryLink", "fragmentEntryScopeERC"));
		Assert.assertTrue(
			_dbInspector.hasColumn(
				"FragmentEntryLink", "originalFragmentEntryLinkERC"));
		Assert.assertFalse(
			_dbInspector.hasColumn(
				"FragmentEntryLink", "originalFragmentEntryLinkId"));
	}

	private Map<Long, Map<String, Object>> _getExpectedValues(
			List<Long> fragmentEntryLinkIds)
		throws Exception {

		Map<Long, Map<String, Object>> expectedValuesMap = new HashMap<>();

		for (long fragmentEntryLinkId : fragmentEntryLinkIds) {
			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLinkId);

			expectedValuesMap.put(
				fragmentEntryLinkId,
				HashMapBuilder.<String, Object>put(
					"fragmentEntryERC", fragmentEntryLink.getFragmentEntryERC()
				).put(
					"fragmentEntryId", _getFragmentEntryId(fragmentEntryLink)
				).put(
					"fragmentEntryScopeERC",
					fragmentEntryLink.getFragmentEntryScopeERC()
				).put(
					"originalFragmentEntryLinkERC",
					fragmentEntryLink.getOriginalFragmentEntryLinkERC()
				).put(
					"originalFragmentEntryLinkId",
					_getOriginalFragmentEntryLinkId(fragmentEntryLink)
				).build());
		}

		return expectedValuesMap;
	}

	private long _getFragmentEntryId(FragmentEntryLink fragmentEntryLink) {
		if (Validator.isNull(fragmentEntryLink.getFragmentEntryERC())) {
			return 0;
		}

		long groupId = fragmentEntryLink.getGroupId();

		if (Validator.isNotNull(fragmentEntryLink.getFragmentEntryScopeERC())) {
			Group group =
				GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
					fragmentEntryLink.getFragmentEntryScopeERC(),
					fragmentEntryLink.getCompanyId());

			groupId = group.getGroupId();
		}

		FragmentEntry fragmentEntry =
			FragmentEntryLocalServiceUtil.
				fetchFragmentEntryByExternalReferenceCode(
					fragmentEntryLink.getFragmentEntryERC(), groupId);

		return fragmentEntry.getFragmentEntryId();
	}

	private long _getOriginalFragmentEntryLinkId(
		FragmentEntryLink fragmentEntryLink) {

		if (Validator.isNull(
				fragmentEntryLink.getOriginalFragmentEntryLinkERC())) {

			return 0;
		}

		FragmentEntryLink originalFragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.
				fetchFragmentEntryLinkByExternalReferenceCode(
					fragmentEntryLink.getOriginalFragmentEntryLinkERC(),
					fragmentEntryLink.getGroupId());

		return originalFragmentEntryLink.getFragmentEntryLinkId();
	}

	private void _updateFragmentEntryLinks() throws Exception {
		_db.alterTableAddColumn(
			_connection, "FragmentEntryLink", "originalFragmentEntryLinkId",
			"LONG");

		_db.alterTableAddColumn(
			_connection, "FragmentEntryLink", "fragmentEntryId", "LONG");

		Group guestGroup = GroupLocalServiceUtil.getFriendlyURLGroup(
			PortalUtil.getDefaultCompanyId(),
			GroupConstants.GUEST_FRIENDLY_URL);

		Layout guestLayout = LayoutLocalServiceUtil.getLayoutByFriendlyURL(
			guestGroup.getGroupId(), false,
			PropsValues.DEFAULT_GUEST_PUBLIC_LAYOUT_FRIENDLY_URL);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				guestGroup.getGroupId(), guestLayout.getPlid());

		List<Long> fragmentEntryLinkIds = new ArrayList<>();

		for (FragmentEntryLink link : fragmentEntryLinks) {
			fragmentEntryLinkIds.add(link.getFragmentEntryLinkId());
		}

		Map<Long, Map<String, Object>> expectedValuesMap = _getExpectedValues(
			fragmentEntryLinkIds);

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select FragmentEntryLink1.ctCollectionId, ",
					"FragmentEntry.fragmentEntryId, ",
					"FragmentEntryLink1.fragmentEntryLinkId, ",
					"FragmentEntryLink2.fragmentEntryLinkId from ",
					"FragmentEntryLink FragmentEntryLink1 left join ",
					"FragmentEntryLink FragmentEntryLink2 on ",
					"FragmentEntryLink1.ctCollectionId = ",
					"FragmentEntryLink2.ctCollectionId and ",
					"FragmentEntryLink1.originalFragmentEntryLinkERC = ",
					"FragmentEntryLink2.externalReferenceCode left join ",
					"FragmentEntry on FragmentEntry.ctCollectionId = ",
					"FragmentEntryLink1.ctCollectionId and ",
					"FragmentEntry.externalReferenceCode = ",
					"FragmentEntryLink1.fragmentEntryERC left join Group_ on ",
					"Group_.ctCollectionId = ",
					"FragmentEntryLink1.ctCollectionId and ( ",
					"Group_.externalReferenceCode = ",
					"FragmentEntryLink1.fragmentEntryScopeERC or ( ",
					"Group_.groupId = FragmentEntryLink1.groupId and ",
					"FragmentEntryLink1.fragmentEntryScopeERC is null))"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long ctCollectionId = resultSet.getLong(1);
				long fragmentEntryId = resultSet.getLong(2);
				long fragmentEntryLinkId1 = resultSet.getLong(3);
				long fragmentEntryLinkId2 = resultSet.getLong(4);

				_db.runSQL(
					StringBundler.concat(
						"update FragmentEntryLink set ",
						"originalFragmentEntryLinkId = ", fragmentEntryLinkId2,
						", fragmentEntryId = ", fragmentEntryId,
						" where fragmentEntryLinkId = ", fragmentEntryLinkId1,
						" and ctCollectionId = ", ctCollectionId));
			}
		}

		_assertFragmentEntryLinks(expectedValuesMap, fragmentEntryLinkIds);
	}

	private void _updateFragmentEntryLinks(
			Map<Long, Map<String, Object>> expectedValuesMap,
			List<Long> fragmentEntryLinkIds)
		throws Exception {

		for (long fragmentEntryLinkId : fragmentEntryLinkIds) {
			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLinkId);

			_db.runSQL(
				StringBundler.concat(
					"update FragmentEntryLink set originalFragmentEntryLinkId ",
					"= ", _getOriginalFragmentEntryLinkId(fragmentEntryLink),
					", fragmentEntryId = ",
					_getFragmentEntryId(fragmentEntryLink),
					" where fragmentEntryLinkId = ",
					fragmentEntryLink.getFragmentEntryLinkId()));
		}

		_assertFragmentEntryLinks(expectedValuesMap, fragmentEntryLinkIds);
	}

	@Inject(
		filter = "(&(component.name=com.liferay.fragment.internal.upgrade.registry.FragmentServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private Connection _connection;
	private DB _db;
	private DBInspector _dbInspector;
	private Layout _draftLayout;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}