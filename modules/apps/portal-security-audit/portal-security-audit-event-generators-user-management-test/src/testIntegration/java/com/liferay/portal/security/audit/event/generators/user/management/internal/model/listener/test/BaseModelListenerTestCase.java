/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Christian Moura
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
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("eventTypes", "*");

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> auditMessages.add(auditMessage), properties);
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	protected AuditMessage fetchAuditMessage(
		String className, String eventType) {

		for (AuditMessage auditMessage : auditMessages) {
			if (Objects.equals(auditMessage.getClassName(), className) &&
				Objects.equals(auditMessage.getEventType(), eventType)) {

				return auditMessage;
			}
		}

		return null;
	}

	protected final List<AuditMessage> auditMessages = new ArrayList<>();

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

}