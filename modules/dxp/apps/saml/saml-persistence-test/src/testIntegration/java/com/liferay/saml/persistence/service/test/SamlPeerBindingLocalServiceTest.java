/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.saml.persistence.model.SamlPeerBinding;
import com.liferay.saml.persistence.service.SamlPeerBindingLocalService;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class SamlPeerBindingLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddSamlPeerBinding() throws Exception {
		User user = UserTestUtil.addUser();

		String samlNameIdNameQualifier = StringUtil.randomString(1024);

		SamlPeerBinding samlPeerBinding =
			_samlPeerBindingLocalService.addSamlPeerBinding(
				user.getUserId(), StringPool.BLANK, StringPool.BLANK,
				samlNameIdNameQualifier, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK);

		Assert.assertEquals(
			samlNameIdNameQualifier,
			samlPeerBinding.getSamlNameIdNameQualifier());
	}

	@Inject
	private SamlPeerBindingLocalService _samlPeerBindingLocalService;

}