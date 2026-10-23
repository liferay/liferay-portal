/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.on.demand.user.ticket.generator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.constants.CTRoleConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.on.demand.user.ticket.generator.CTOnDemandUserTicketGenerator;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTOnDemandUserTicketGeneratorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGenerateWithNonshareableCTCollection() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Assert.assertFalse(ctCollection.isShareable());
		Assert.assertNull(
			_ctOnDemandUserTicketGenerator.generate(
				ctCollection.getCtCollectionId()));
	}

	@Test
	public void testGenerateWithShareableCTCollection() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		ctCollection.setShareable(true);

		ctCollection = _ctCollectionLocalService.updateCTCollection(
			ctCollection);

		Assert.assertTrue(ctCollection.isShareable());

		List<AuditMessage> auditMessages = new ArrayList<>();

		AuditRouter auditRouter =
			(AuditRouter)ReflectionTestUtil.getAndSetFieldValue(
				_ctOnDemandUserTicketGenerator, "_auditRouter",
				ProxyUtil.newProxyInstance(
					AuditRouter.class.getClassLoader(),
					new Class<?>[] {AuditRouter.class},
					(proxy, method, arguments) -> {
						auditMessages.add((AuditMessage)arguments[0]);

						return null;
					}));

		Ticket ticket = null;

		try {
			ticket = _ctOnDemandUserTicketGenerator.generate(
				ctCollection.getCtCollectionId());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_ctOnDemandUserTicketGenerator, "_auditRouter", auditRouter);
		}

		AuditMessage auditMessage = auditMessages.get(0);

		Assert.assertEquals("grant_on_demand_access", auditMessage.getAction());

		Assert.assertEquals(auditMessages.toString(), 1, auditMessages.size());

		Assert.assertEquals(
			CTCollection.class.getName(), ticket.getClassName());
		Assert.assertEquals(
			ctCollection.getCtCollectionId(), ticket.getClassPK());
		Assert.assertEquals(
			TicketConstants.TYPE_ON_DEMAND_USER_LOGIN, ticket.getType());

		User onDemandUser = _userLocalService.getUser(
			Long.valueOf(ticket.getExtraInfo()));

		Assert.assertEquals(
			UserConstants.TYPE_ON_DEMAND_USER, onDemandUser.getType());

		Group group = _groupLocalService.fetchGroup(
			ctCollection.getCompanyId(),
			_classNameLocalService.getClassNameId(CTCollection.class),
			ctCollection.getCtCollectionId());

		Assert.assertTrue(
			_userGroupRoleLocalService.hasUserGroupRole(
				onDemandUser.getUserId(), group.getGroupId(),
				CTRoleConstants.PUBLICATIONS_REVIEWER));

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			onDemandUser);

		Assert.assertFalse(
			_ctCollectionModelResourcePermission.contains(
				permissionChecker, ctCollection, ActionKeys.DELETE));
		Assert.assertFalse(
			_ctCollectionModelResourcePermission.contains(
				permissionChecker, ctCollection, ActionKeys.UPDATE));
		Assert.assertFalse(
			_ctCollectionModelResourcePermission.contains(
				permissionChecker, ctCollection, CTActionKeys.PUBLISH));
		Assert.assertTrue(
			_ctCollectionModelResourcePermission.contains(
				permissionChecker, ctCollection, ActionKeys.VIEW));
		Assert.assertTrue(
			PortletPermissionUtil.contains(
				permissionChecker, CTPortletKeys.PUBLICATIONS,
				ActionKeys.ACCESS_IN_CONTROL_PANEL));
		Assert.assertTrue(
			PortletPermissionUtil.contains(
				permissionChecker, CTPortletKeys.PUBLICATIONS,
				ActionKeys.VIEW));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.change.tracking.model.CTCollection"
	)
	private volatile ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@Inject
	private CTOnDemandUserTicketGenerator _ctOnDemandUserTicketGenerator;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private TicketLocalService _ticketLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}