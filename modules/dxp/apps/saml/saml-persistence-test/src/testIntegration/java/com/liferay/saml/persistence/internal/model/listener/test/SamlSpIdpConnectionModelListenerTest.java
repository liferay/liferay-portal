/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Lucas Miranda
 */
@RunWith(Arquillian.class)
public class SamlSpIdpConnectionModelListenerTest
	extends BaseModelListenerTestCase {

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnAfterCreate() throws Exception {
		_samlSpIdpConnection = _addSamlSpIdpConnection();

		AuditMessage auditMessage = fetchAuditMessage(
			SamlSpIdpConnection.class.getName(), EventTypes.ADD);

		_assertAuditMessage(auditMessage, _samlSpIdpConnection);

		Assert.assertEquals(
			"instance.samlspidpconnection.add",
			auditMessage.getResourceAction());
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		SamlSpIdpConnection samlSpIdpConnection = _addSamlSpIdpConnection();

		_samlSpIdpConnectionLocalService.deleteSamlSpIdpConnection(
			samlSpIdpConnection.getSamlSpIdpConnectionId());

		_assertAuditMessage(
			fetchAuditMessage(
				SamlSpIdpConnection.class.getName(), EventTypes.DELETE),
			samlSpIdpConnection);
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_samlSpIdpConnection = _addSamlSpIdpConnection();

		_samlSpIdpConnection =
			_samlSpIdpConnectionLocalService.updateSamlSpIdpConnection(
				_samlSpIdpConnection.getSamlSpIdpConnectionId(),
				_samlSpIdpConnection.getSamlIdpEntityId(),
				_samlSpIdpConnection.isAssertionSignatureRequired(),
				_samlSpIdpConnection.getClockSkew(),
				_samlSpIdpConnection.isEnabled(),
				_samlSpIdpConnection.isForceAuthn(),
				_samlSpIdpConnection.isLdapImportEnabled(),
				_samlSpIdpConnection.getMetadataUrl(), null,
				_samlSpIdpConnection.getName(),
				_samlSpIdpConnection.getNameIdFormat(),
				_samlSpIdpConnection.isSignAuthnRequest(),
				_samlSpIdpConnection.isUnknownUsersAreStrangers(),
				StringBundler.concat(
					"#", RandomTestUtil.randomString(), "\n",
					_samlSpIdpConnection.getUserAttributeMappings()),
				_samlSpIdpConnection.getUserIdentifierExpression(),
				ServiceContextTestUtil.getServiceContext());

		Assert.assertNull(
			fetchAuditMessage(
				SamlSpIdpConnection.class.getName(), EventTypes.UPDATE));

		_samlSpIdpConnection.setMetadataXml(RandomTestUtil.randomString());

		String name = _samlSpIdpConnection.getName();

		String newName = RandomTestUtil.randomString();

		_samlSpIdpConnection.setName(newName);

		_company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_samlSpIdpConnection =
				_samlSpIdpConnectionLocalService.updateSamlSpIdpConnection(
					_samlSpIdpConnection);
		}

		AuditMessage auditMessage = fetchAuditMessage(
			SamlSpIdpConnection.class.getName(), EventTypes.UPDATE);

		Assert.assertEquals(
			_samlSpIdpConnection.getCompanyId(), auditMessage.getCompanyId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"name", "metadataXml"
				).put(
					"newValue", ""
				).put(
					"oldValue", ""
				),
				JSONUtil.put(
					"name", "name"
				).put(
					"newValue", newName
				).put(
					"oldValue", name
				)
			).toString(),
			String.valueOf(additionalInfoJSONObject.getJSONArray("attributes")),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private SamlSpIdpConnection _addSamlSpIdpConnection() throws Exception {
		SamlSpIdpConnection samlSpIdpConnection =
			_samlSpIdpConnectionLocalService.createSamlSpIdpConnection(
				RandomTestUtil.nextLong());

		samlSpIdpConnection.setCompanyId(TestPropsValues.getCompanyId());
		samlSpIdpConnection.setSamlIdpEntityId(RandomTestUtil.randomString());
		samlSpIdpConnection.setMetadataXml(RandomTestUtil.randomString());
		samlSpIdpConnection.setName(RandomTestUtil.randomString());
		samlSpIdpConnection.setUserAttributeMappings(
			StringBundler.concat(
				RandomTestUtil.randomString(), "=",
				RandomTestUtil.randomString()));

		return _samlSpIdpConnectionLocalService.addSamlSpIdpConnection(
			samlSpIdpConnection);
	}

	private void _assertAuditMessage(
			AuditMessage auditMessage, SamlSpIdpConnection samlSpIdpConnection)
		throws Exception {

		Assert.assertEquals(
			samlSpIdpConnection.getCompanyId(), auditMessage.getCompanyId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"name", samlSpIdpConnection.getName()
			).put(
				"samlIdpEntityId", samlSpIdpConnection.getSamlIdpEntityId()
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private SamlSpIdpConnection _samlSpIdpConnection;

	@Inject
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

}