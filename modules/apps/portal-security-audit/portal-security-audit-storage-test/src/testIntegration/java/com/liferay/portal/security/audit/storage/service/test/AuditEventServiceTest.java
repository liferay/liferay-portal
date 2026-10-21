/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.audit.constants.AuditEventConstants;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.security.audit.storage.comparator.AuditEventCreateDateComparator;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.security.audit.storage.service.AuditEventService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class AuditEventServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
	}

	@After
	public void tearDown() {
		for (AuditEvent auditEvent : _auditEvents) {
			_auditEventLocalService.deleteAuditEvent(auditEvent);
		}
	}

	@Test
	public void testGetAuditEvents() throws Exception {
		String contextName = RandomTestUtil.randomString();

		_auditEvents.add(
			_auditEventLocalService.addAuditEvent(
				AuditMessageBuilder.buildAuditMessage(
					0L, RandomTestUtil.randomString(),
					RandomTestUtil.randomLong(), contextName,
					RandomTestUtil.randomString(), new ArrayList<>())));
		_auditEvents.add(
			_auditEventLocalService.addAuditEvent(
				AuditMessageBuilder.buildAuditMessage(
					_accountEntry.getAccountEntryId(),
					RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
					contextName, RandomTestUtil.randomString(),
					new ArrayList<>())));
		_auditEvents.add(
			_auditEventLocalService.addAuditEvent(
				AuditMessageBuilder.buildAuditMessage(
					_accountEntry.getAccountEntryId(),
					RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
					contextName, RandomTestUtil.randomString(),
					new ArrayList<>())));

		_auditEvents.add(
			_auditEventLocalService.addAuditEvent(
				AuditMessageBuilder.buildAuditMessage(
					_accountEntry.getAccountEntryId(),
					RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
					null, RandomTestUtil.randomString(), new ArrayList<>())));

		User accountMemberUser = UserTestUtil.addUser();

		_associateUser(
			_roleLocalService.getRole(
				TestPropsValues.getCompanyId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER),
			accountMemberUser);

		UserTestUtil.setUser(accountMemberUser);

		try {
			_auditEventService.getAuditEvents(
				TestPropsValues.getCompanyId(), 0, 0, null, null, null,
				new long[] {_accountEntry.getAccountEntryId()}, null, null,
				null, null, contextName, null, null, 0, null, true, 1, 10,
				new AuditEventCreateDateComparator());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		User accountAdminUser = UserTestUtil.addUser();

		_associateUser(
			_roleLocalService.getRole(
				TestPropsValues.getCompanyId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR),
			accountAdminUser);

		UserTestUtil.setUser(accountAdminUser);

		List<AuditEvent> auditEvents = _auditEventService.getAuditEvents(
			TestPropsValues.getCompanyId(), 0, 0, null, null, null,
			new long[] {_accountEntry.getAccountEntryId()}, null, null, null,
			null, contextName, null, null, 0, null, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, new AuditEventCreateDateComparator());

		Assert.assertEquals(auditEvents.toString(), 2, auditEvents.size());

		try {
			_auditEventService.getAuditEvents(
				TestPropsValues.getCompanyId(), 0, 0, null, null, null, null,
				null, null, null, null, contextName, null, null, 0, null, true,
				1, 10, new AuditEventCreateDateComparator());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		UserTestUtil.setUser(TestPropsValues.getUser());

		auditEvents = _auditEventService.getAuditEvents(
			TestPropsValues.getCompanyId(), 0, 0, null, null, null,
			new long[] {_accountEntry.getAccountEntryId()}, null, null, null,
			null, contextName, null, null, 0, null, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, new AuditEventCreateDateComparator());

		Assert.assertEquals(auditEvents.toString(), 2, auditEvents.size());

		auditEvents = _auditEventService.getAuditEvents(
			TestPropsValues.getCompanyId(), 0, 0, null, null, null,
			new long[] {_accountEntry.getAccountEntryId(), 0}, null, null, null,
			null, contextName, null, null, 0, null, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, new AuditEventCreateDateComparator());

		Assert.assertEquals(auditEvents.toString(), 3, auditEvents.size());

		auditEvents = _auditEventService.getAuditEvents(
			TestPropsValues.getCompanyId(), 0, 0, null, null, null,
			new long[] {_accountEntry.getAccountEntryId()}, null, null, null,
			null, null, null, null, 0, null, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, new AuditEventCreateDateComparator());

		Assert.assertEquals(auditEvents.toString(), 3, auditEvents.size());

		auditEvents = _auditEventService.getAuditEvents(
			TestPropsValues.getCompanyId(), 0, 0, null, null, null,
			new long[] {_accountEntry.getAccountEntryId()}, null, null, null,
			null, AuditEventConstants.CONTEXT_NAME_INSTANCE, null, null, 0,
			null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new AuditEventCreateDateComparator());

		Assert.assertEquals(auditEvents.toString(), 1, auditEvents.size());

		auditEvents = _auditEventService.getAuditEvents(
			TestPropsValues.getCompanyId(), 0, 0, null, null, null,
			new long[] {_accountEntry.getAccountEntryId()}, null, null, null,
			null, AuditEventConstants.CONTEXT_NAME_INSTANCE, null, null, 0,
			null, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new AuditEventCreateDateComparator());

		Assert.assertEquals(auditEvents.toString(), 1, auditEvents.size());
	}

	private void _associateUser(Role role, User user) throws Exception {
		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), user.getUserId());

		AccountRole accountRole =
			_accountRoleLocalService.getAccountRoleByRoleId(role.getRoleId());

		_accountRoleLocalService.associateUser(
			_accountEntry.getAccountEntryId(), accountRole.getAccountRoleId(),
			user.getUserId());
	}

	private static final List<AuditEvent> _auditEvents = new ArrayList<>();

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private AuditEventLocalService _auditEventLocalService;

	@Inject
	private AuditEventService _auditEventService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}