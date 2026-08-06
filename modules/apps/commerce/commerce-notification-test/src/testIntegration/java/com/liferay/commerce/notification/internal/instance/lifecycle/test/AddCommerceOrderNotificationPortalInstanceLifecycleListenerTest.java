/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class AddCommerceOrderNotificationPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_portalInstanceLifecycleListener.portalInstanceRegistered(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
	}

	@Test
	public void testCommerceOrderNotificationObjectActionAvailability()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "CommerceOrder");

		Assert.assertNotNull(objectDefinition);

		ObjectAction objectAction = _objectActionLocalService.fetchObjectAction(
			"L_COMMERCE_ORDER_NOTIFICATION",
			objectDefinition.getObjectDefinitionId());

		Assert.assertNotNull(objectAction);
		Assert.assertFalse(objectAction.isActive());
	}

	@Test
	public void testCommerceOrderNotificationTemplateAvailability()
		throws Exception {

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.
				fetchNotificationTemplateByExternalReferenceCode(
					"COMMERCE_ORDER_TEMPLATE", TestPropsValues.getCompanyId());

		Assert.assertNotNull(notificationTemplate);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.notification.internal.instance.lifecycle.AddCommerceOrderNotificationPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

}