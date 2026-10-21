/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.web.internal;

import com.liferay.multi.factor.authentication.fido2.web.internal.audit.MFAFIDO2AuditMessageBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Rafael Praxedes
 */
public abstract class BaseMFAFIDO2WebTestCase {

	@Before
	public void setUp() throws Exception {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		PortalUtil portalUtil = new PortalUtil();

		Mockito.when(
			portal.getUserName(REAL_USER_ID, StringPool.BLANK)
		).thenReturn(
			REAL_USER_NAME
		);

		portalUtil.setPortal(portal);

		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		auditRequestContext.setRealUserId(REAL_USER_ID);

		ReflectionTestUtil.setFieldValue(
			mfaFIDO2AuditMessageBuilder, "_auditRouter", auditRouter);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			MFAFIDO2AuditMessageBuilder.class, mfaFIDO2AuditMessageBuilder,
			null);
	}

	@After
	public void tearDown() {
		AuditRequestContextThreadLocal.removeAuditRequestContext();

		PrincipalThreadLocal.setName(null);

		_serviceRegistration.unregister();
	}

	protected void assertAuditMessage(
		AuditMessage auditMessage, String doAsUserId, String eventType,
		long selectedUserId) {

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			doAsUserId, additionalInfoJSONObject.getString("doAsUserId", null));

		Assert.assertEquals(User.class.getName(), auditMessage.getClassName());
		Assert.assertEquals(
			String.valueOf(selectedUserId), auditMessage.getClassPK());
		Assert.assertEquals(eventType, auditMessage.getEventType());
		Assert.assertEquals(REAL_USER_ID, auditMessage.getUserId());
		Assert.assertEquals(REAL_USER_NAME, auditMessage.getUserName());
	}

	protected static final long REAL_USER_ID = RandomTestUtil.randomLong();

	protected static final String REAL_USER_NAME =
		RandomTestUtil.randomString();

	protected final AuditRouter auditRouter = Mockito.mock(AuditRouter.class);
	protected final MFAFIDO2AuditMessageBuilder mfaFIDO2AuditMessageBuilder =
		new MFAFIDO2AuditMessageBuilder();
	protected final Portal portal = Mockito.mock(Portal.class);

	private ServiceRegistration<MFAFIDO2AuditMessageBuilder>
		_serviceRegistration;

}