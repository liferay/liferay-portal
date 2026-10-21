/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.internal.upgrade.v2_5_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.security.audit.constants.AuditEventConstants;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.ArrayList;
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
public class AuditEventContextNameUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		AuditEvent auditEvent1 = _addAuditEvent(null);
		AuditEvent auditEvent2 = _addAuditEvent(StringPool.BLANK);

		String contextName = RandomTestUtil.randomString();

		AuditEvent auditEvent3 = _addAuditEvent(contextName);

		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(2, 5, 0));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_finderCache.clearCache();

		auditEvent1 = _auditEventLocalService.fetchAuditEvent(
			auditEvent1.getAuditEventId());

		Assert.assertEquals(
			AuditEventConstants.CONTEXT_NAME_INSTANCE,
			auditEvent1.getContextName());

		auditEvent2 = _auditEventLocalService.fetchAuditEvent(
			auditEvent2.getAuditEventId());

		Assert.assertEquals(
			AuditEventConstants.CONTEXT_NAME_INSTANCE,
			auditEvent2.getContextName());

		auditEvent3 = _auditEventLocalService.fetchAuditEvent(
			auditEvent3.getAuditEventId());

		Assert.assertEquals(contextName, auditEvent3.getContextName());
	}

	private AuditEvent _addAuditEvent(String contextName) {
		AuditEvent auditEvent = _auditEventLocalService.addAuditEvent(
			AuditMessageBuilder.buildAuditMessage(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomString(), new ArrayList<>()));

		auditEvent.setContextName(contextName);

		auditEvent = _auditEventLocalService.updateAuditEvent(auditEvent);

		_auditEvents.add(auditEvent);

		return auditEvent;
	}

	@Inject
	private AuditEventLocalService _auditEventLocalService;

	@DeleteAfterTestRun
	private final List<AuditEvent> _auditEvents = new ArrayList<>();

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.storage.internal.upgrade.registry.AuditStorageServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}