/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener.test;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseModelListenerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_auditRouter = (AuditRouter)ReflectionTestUtil.getAndSetFieldValue(
			getModelListener(), "auditRouter",
			ProxyUtil.newProxyInstance(
				AuditRouter.class.getClassLoader(),
				new Class<?>[] {AuditRouter.class},
				(proxy, method, arguments) -> {
					_auditMessages.add((AuditMessage)arguments[0]);

					return null;
				}));

		CMPTestUtil.getOrAddGroup(BaseModelListenerTestCase.class);

		cmpProjectObjectEntry = CMPTestUtil.addCMPProjectObjectEntry();
	}

	@After
	public void tearDown() throws Exception {
		ReflectionTestUtil.setFieldValue(
			getModelListener(), "auditRouter", _auditRouter);

		_objectEntryLocalService.deleteObjectEntry(cmpProjectObjectEntry);
	}

	protected void assertAuditMessage(String expectedEventType) {
		AuditMessage auditMessage = _auditMessages.poll();

		Assert.assertEquals(
			cmpProjectObjectEntry.getModelClassName(),
			auditMessage.getClassName());
		Assert.assertEquals(
			cmpProjectObjectEntry.getObjectEntryId(),
			GetterUtil.getLong(auditMessage.getClassPK()));
		Assert.assertEquals(expectedEventType, auditMessage.getEventType());

		_auditMessages.clear();
	}

	protected abstract ModelListener<?> getModelListener();

	protected ObjectEntry cmpProjectObjectEntry;

	private final Queue<AuditMessage> _auditMessages = new LinkedList<>();
	private AuditRouter _auditRouter;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}