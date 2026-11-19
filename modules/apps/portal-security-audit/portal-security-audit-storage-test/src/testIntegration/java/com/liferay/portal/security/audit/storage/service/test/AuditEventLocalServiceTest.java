/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.security.audit.constants.AuditEventConstants;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.model.AuditEventAccountEntryRel;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.security.audit.storage.service.persistence.AuditEventAccountEntryRelUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class AuditEventLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddAuditEvent() throws Exception {
		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new ArrayList<>());

		long[] accountEntryIds = {
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong()
		};

		auditMessage.setUserAccountEntryIds(accountEntryIds);

		long companyId = RandomTestUtil.randomLong();

		auditMessage.setCompanyId(companyId);

		auditMessage.setCorrelationId(RandomTestUtil.randomString());
		auditMessage.setHttpMethod(RandomTestUtil.randomString());
		auditMessage.setImpersonated(RandomTestUtil.randomBoolean());
		auditMessage.setImpersonatedUserEmailAddress(
			RandomTestUtil.randomString());
		auditMessage.setImpersonatedUserId(RandomTestUtil.randomLong());
		auditMessage.setImpersonatedUserName(RandomTestUtil.randomString());
		auditMessage.setObjectName(RandomTestUtil.randomString());
		auditMessage.setRequestId(RandomTestUtil.randomString());
		auditMessage.setRequestIdGenerated(RandomTestUtil.randomBoolean());
		auditMessage.setResourceAction(RandomTestUtil.randomString());
		auditMessage.setResourceType(RandomTestUtil.randomString());
		auditMessage.setRoles(RandomTestUtil.randomString());
		auditMessage.setUserAgent(RandomTestUtil.randomString());
		auditMessage.setUserEmailAddress(RandomTestUtil.randomString());

		AuditEvent auditEvent = _auditEventLocalService.addAuditEvent(
			auditMessage);

		Assert.assertEquals(
			auditEvent.getAccountEntryId(), auditMessage.getAccountEntryId());
		Assert.assertEquals(
			auditEvent.getContextName(), auditMessage.getContextName());
		Assert.assertEquals(
			auditEvent.getCorrelationId(), auditMessage.getCorrelationId());
		Assert.assertEquals(
			auditEvent.getHttpMethod(), auditMessage.getHttpMethod());
		Assert.assertEquals(
			auditEvent.isImpersonated(), auditMessage.isImpersonated());
		Assert.assertEquals(
			auditEvent.getImpersonatedUserEmailAddress(),
			auditMessage.getImpersonatedUserEmailAddress());
		Assert.assertEquals(
			auditEvent.getImpersonatedUserId(),
			auditMessage.getImpersonatedUserId());
		Assert.assertEquals(
			auditEvent.getImpersonatedUserName(),
			auditMessage.getImpersonatedUserName());
		Assert.assertEquals(
			auditEvent.getObjectName(), auditMessage.getObjectName());
		Assert.assertEquals(
			auditEvent.getRequestId(), auditMessage.getRequestId());
		Assert.assertEquals(
			auditEvent.isRequestIdGenerated(),
			auditMessage.isRequestIdGenerated());
		Assert.assertEquals(
			auditEvent.getResourceAction(), auditMessage.getResourceAction());
		Assert.assertEquals(
			auditEvent.getResourceType(), auditMessage.getResourceType());
		Assert.assertEquals(auditEvent.getRoles(), auditMessage.getRoles());
		Assert.assertEquals(
			auditEvent.getUserAgent(), auditMessage.getUserAgent());
		Assert.assertEquals(
			auditEvent.getUserEmailAddress(),
			auditMessage.getUserEmailAddress());

		AuditEvent persistedAuditEvent =
			_auditEventLocalService.fetchAuditEvent(
				auditEvent.getAuditEventId());

		Assert.assertEquals(companyId, persistedAuditEvent.getCompanyId());

		_assertAuditEventAccountEntryRels(accountEntryIds, auditEvent);

		_testAddAuditEvent(StringPool.BLANK);
		_testAddAuditEvent(null);
	}

	@Test
	public void testAddAuditEvents() throws Exception {
		AuditMessage auditMessage1 = new AuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		long[] accountEntryIds = {
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong()
		};

		auditMessage1.setUserAccountEntryIds(accountEntryIds);

		AuditMessage auditMessage2 = new AuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_auditEventLocalService.addAuditEvents(
			Arrays.asList(auditMessage1, auditMessage2));

		_assertAuditEventAccountEntryRels(
			accountEntryIds, _getAuditEvent(auditMessage1));
		_assertAuditEventAccountEntryRels(
			new long[0], _getAuditEvent(auditMessage2));
	}

	@Test
	public void testDeleteAuditEvent() throws Exception {
		AuditMessage auditMessage = new AuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		auditMessage.setUserAccountEntryIds(
			new long[] {RandomTestUtil.randomLong()});

		AuditEvent auditEvent = _auditEventLocalService.addAuditEvent(
			auditMessage);

		_auditEventLocalService.deleteAuditEvent(auditEvent);

		_assertAuditEventAccountEntryRels(new long[0], auditEvent);
	}

	private void _assertAuditEventAccountEntryRels(
			long[] accountEntryIds, AuditEvent auditEvent)
		throws Exception {

		List<AuditEventAccountEntryRel> auditEventAccountEntryRels =
			TransactionTestUtil.invoke(
				AuditEventAccountEntryRelUtil.getPersistence(),
				auditEventAccountEntryRelPersistence ->
					auditEventAccountEntryRelPersistence.findByAuditEventId(
						auditEvent.getAuditEventId()));

		Assert.assertEquals(
			auditEventAccountEntryRels.toString(), accountEntryIds.length,
			auditEventAccountEntryRels.size());

		for (AuditEventAccountEntryRel auditEventAccountEntryRel :
				auditEventAccountEntryRels) {

			Assert.assertTrue(
				ArrayUtil.contains(
					accountEntryIds,
					auditEventAccountEntryRel.getAccountEntryId()));
			Assert.assertEquals(
				auditEvent.getCompanyId(),
				auditEventAccountEntryRel.getCompanyId());
			Assert.assertEquals(
				auditEvent.getCreateDate(),
				auditEventAccountEntryRel.getCreateDate());
		}
	}

	private AuditEvent _getAuditEvent(AuditMessage auditMessage) {
		List<AuditEvent> auditEvents = _auditEventLocalService.getAuditEvents(
			auditMessage.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(auditEvents.toString(), 1, auditEvents.size());

		return auditEvents.get(0);
	}

	private void _testAddAuditEvent(String contextName) throws Exception {
		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), contextName,
			RandomTestUtil.randomString(), new ArrayList<>());

		auditMessage.setCompanyId(RandomTestUtil.randomLong());

		AuditEvent auditEvent = _auditEventLocalService.addAuditEvent(
			auditMessage);

		Assert.assertEquals(
			AuditEventConstants.CONTEXT_NAME_INSTANCE,
			auditEvent.getContextName());

		_assertAuditEventAccountEntryRels(new long[0], auditEvent);
	}

	@Inject
	private AuditEventLocalService _auditEventLocalService;

}