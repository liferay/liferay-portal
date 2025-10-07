/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.internal.constants.SegmentsDestinationNames;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.service.SegmentsEntryRelLocalService;
import com.liferay.segments.service.SegmentsEntryRoleLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Dictionary;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Michael Bowerman
 */
@RunWith(Arquillian.class)
public class SegmentsEntryReindexMessageListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		Criteria criteria = new Criteria();

		_segmentsCriteriaContributor.contribute(
			criteria, String.format("(roleIds eq '%s')", _role.getRoleId()),
			Criteria.Conjunction.AND);

		_segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAssignRolesBySegmentManually() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"roleSegmentationEnabled", true
			).put(
				"segmentationEnabled", true
			).build();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_group.getCompanyId(),
						"com.liferay.segments.configuration." +
							"SegmentsCompanyConfiguration",
						properties);
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.segments.configuration.SegmentsConfiguration",
					properties)) {

			_segmentsConfigurationProvider.clearSegmentsCompanyConfigurations();

			_roleLocalService.addUserRole(
				_user1.getUserId(), _role.getRoleId());

			_segmentsEntryRoleLocalService.addSegmentsEntryRole(
				_segmentsEntry.getSegmentsEntryId(), role.getRoleId(),
				_serviceContext);

			_invokeMessageListener();

			List<SegmentsEntryRel> segmentsEntryRels =
				_segmentsEntryRelLocalService.getSegmentsEntryRels(
					_segmentsEntry.getSegmentsEntryId());

			Assert.assertEquals(
				segmentsEntryRels.toString(), 1, segmentsEntryRels.size());

			SegmentsEntryRel segmentsEntryRel = segmentsEntryRels.get(0);

			Assert.assertEquals(
				_user1.getUserId(), segmentsEntryRel.getClassPK());
			Assert.assertEquals(
				_segmentsEntry.getSegmentsEntryId(),
				segmentsEntryRel.getSegmentsEntryId());

			_serviceContext.setRequest(new MockHttpServletRequest());

			PermissionChecker permissionChecker =
				_permissionCheckerFactory.create(_user1);

			Assert.assertTrue(
				ArrayUtil.contains(
					permissionChecker.getRoleIds(
						_user1.getUserId(), _group.getGroupId()),
					role.getRoleId()));
		}
		finally {
			_roleLocalService.deleteRole(role);
		}
	}

	@Test
	public void testSegmentsEntryRelAdded() throws Exception {
		_roleLocalService.addUserRole(_user1.getUserId(), _role);

		_invokeMessageListener();

		List<SegmentsEntryRel> segmentsEntryRels =
			_segmentsEntryRelLocalService.getSegmentsEntryRels(
				_segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRels.toString(), 1, segmentsEntryRels.size());

		SegmentsEntryRel segmentsEntryRel1 = segmentsEntryRels.get(0);

		Assert.assertEquals(_user1.getUserId(), segmentsEntryRel1.getClassPK());

		_roleLocalService.addUserRole(_user2.getUserId(), _role);

		_invokeMessageListener();

		segmentsEntryRels = _segmentsEntryRelLocalService.getSegmentsEntryRels(
			_segmentsEntry.getSegmentsEntryId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS,
			OrderByComparatorFactoryUtil.create(
				"SegmentsEntryRel", "classPK", true));

		Assert.assertEquals(
			segmentsEntryRels.toString(), 2, segmentsEntryRels.size());

		SegmentsEntryRel segmentsEntryRel2 = segmentsEntryRels.get(0);
		SegmentsEntryRel segmentsEntryRel3 = segmentsEntryRels.get(1);

		Assert.assertEquals(_user1.getUserId(), segmentsEntryRel2.getClassPK());
		Assert.assertEquals(_user2.getUserId(), segmentsEntryRel3.getClassPK());
	}

	@Test
	public void testSegmentsEntryRelRemoved() throws Exception {
		_roleLocalService.addUserRole(_user1.getUserId(), _role);
		_roleLocalService.addUserRole(_user2.getUserId(), _role);

		_invokeMessageListener();

		List<SegmentsEntryRel> segmentsEntryRels =
			_segmentsEntryRelLocalService.getSegmentsEntryRels(
				_segmentsEntry.getSegmentsEntryId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				OrderByComparatorFactoryUtil.create(
					"SegmentsEntryRel", "classPK", true));

		Assert.assertEquals(
			segmentsEntryRels.toString(), 2, segmentsEntryRels.size());

		SegmentsEntryRel segmentsEntryRel1 = segmentsEntryRels.get(0);
		SegmentsEntryRel segmentsEntryRel2 = segmentsEntryRels.get(1);

		Assert.assertEquals(_user1.getUserId(), segmentsEntryRel1.getClassPK());
		Assert.assertEquals(_user2.getUserId(), segmentsEntryRel2.getClassPK());

		_roleLocalService.deleteUserRole(_user2.getUserId(), _role);

		_invokeMessageListener();

		segmentsEntryRels = _segmentsEntryRelLocalService.getSegmentsEntryRels(
			_segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRels.toString(), 1, segmentsEntryRels.size());

		SegmentsEntryRel segmentsEntryRel3 = segmentsEntryRels.get(0);

		Assert.assertEquals(_user1.getUserId(), segmentsEntryRel3.getClassPK());
	}

	private void _invokeMessageListener() throws Exception {
		Message message = new Message();

		message.put("companyId", _segmentsEntry.getCompanyId());
		message.put("segmentsEntryId", _segmentsEntry.getSegmentsEntryId());

		_messageListener.receive(message);
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "destination.name=" + SegmentsDestinationNames.SEGMENTS_ENTRY_REINDEX
	)
	private MessageListener _messageListener;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _segmentsCriteriaContributor;

	@DeleteAfterTestRun
	private SegmentsEntry _segmentsEntry;

	@Inject
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

	@Inject
	private SegmentsEntryRoleLocalService _segmentsEntryRoleLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

}