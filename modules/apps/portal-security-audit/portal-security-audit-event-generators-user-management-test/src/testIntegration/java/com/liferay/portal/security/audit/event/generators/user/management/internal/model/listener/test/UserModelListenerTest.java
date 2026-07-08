/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Ivica Cardic
 */
@RunWith(Arquillian.class)
public class UserModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_auditMessages = new ArrayList<>();

		Bundle bundle = FrameworkUtil.getBundle(UserModelListenerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("eventTypes", "*");

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> _auditMessages.add(auditMessage), properties);
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_user = UserTestUtil.addUser();

		_company = CompanyTestUtil.addCompany();

		Assert.assertFalse(_user.isAgreedToTermsOfUse());

		_auditMessages.clear();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_userLocalService.updateAgreedToTermsOfUse(_user.getUserId(), true);
		}

		AuditMessage agreedToTermsOfUseAuditMessage = null;

		for (AuditMessage auditMessage : _auditMessages) {
			if (Objects.equals(
					auditMessage.getClassName(), User.class.getName()) &&
				Objects.equals(
					auditMessage.getEventType(),
					EventTypes.AGREED_TO_TERMS_OF_USE)) {

				agreedToTermsOfUseAuditMessage = auditMessage;

				break;
			}
		}

		JSONObject additionalInfoJSONObject =
			agreedToTermsOfUseAuditMessage.getAdditionalInfo();

		Assert.assertTrue(
			additionalInfoJSONObject.has("termsOfUseJournalArticleGroupId"));
		Assert.assertTrue(
			additionalInfoJSONObject.has("termsOfUseJournalArticleId"));

		Assert.assertEquals(
			String.valueOf(_user.getUserId()),
			agreedToTermsOfUseAuditMessage.getClassPK());
		Assert.assertEquals(
			_user.getCompanyId(),
			agreedToTermsOfUseAuditMessage.getCompanyId());

		_auditMessages.clear();

		_user = _userLocalService.getUser(_user.getUserId());

		_user.setComments(RandomTestUtil.randomString());

		_user = _userLocalService.updateUser(_user);

		for (AuditMessage auditMessage : _auditMessages) {
			Assert.assertNotEquals(
				EventTypes.AGREED_TO_TERMS_OF_USE, auditMessage.getEventType());
		}
	}

	private List<AuditMessage> _auditMessages;

	@DeleteAfterTestRun
	private Company _company;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}