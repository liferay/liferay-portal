/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;

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
	public void testAddAuditEvent() {
		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new ArrayList<>());

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
	}

	@Inject
	private AuditEventLocalService _auditEventLocalService;

}