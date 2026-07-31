/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.saml.persistence.model.SamlSpMessage;
import com.liferay.saml.persistence.service.SamlSpMessageLocalService;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Regisson Aguiar
 */
@RunWith(Arquillian.class)
public class SamlSpMessageLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddSamlSpMessage() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		String samlIdpResponseKey = StringUtil.randomString(256);

		SamlSpMessage samlSpMessage =
			_samlSpMessageLocalService.addSamlSpMessage(
				StringUtil.randomString(),
				new Date(System.currentTimeMillis() + Time.HOUR),
				samlIdpResponseKey, serviceContext);

		Assert.assertEquals(
			samlIdpResponseKey, samlSpMessage.getSamlIdpResponseKey());
	}

	@Inject
	private SamlSpMessageLocalService _samlSpMessageLocalService;

}