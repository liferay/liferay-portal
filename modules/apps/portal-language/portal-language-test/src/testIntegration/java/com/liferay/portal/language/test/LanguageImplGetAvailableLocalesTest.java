/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class LanguageImplGetAvailableLocalesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@Test
	public void testCompanyThreadLocalIsDefaultWithNoArgs() throws Exception {
		long companyId = CompanyThreadLocal.getCompanyId();

		try {
			_resetCompanyLocales();

			Assert.assertEquals(_locales, _language.getAvailableLocales());
		}
		finally {
			CompanyThreadLocal.setCompanyId(companyId);
		}
	}

	@Test
	public void testGroupWithSpecificLocales() throws Exception {
		long groupId = _getGuestGroupId();

		GroupTestUtil.updateDisplaySettings(groupId, _locales, LocaleUtil.US);

		Assert.assertEquals(_locales, _language.getAvailableLocales(groupId));
	}

	@Test
	public void testGroupWithoutLocalesInheritsFromCompany() throws Exception {
		long companyId = CompanyThreadLocal.getCompanyId();

		try {
			_resetCompanyLocales();
		}
		finally {
			CompanyThreadLocal.setCompanyId(companyId);
		}

		Assert.assertEquals(
			_locales, _language.getAvailableLocales(_getGuestGroupId()));
	}

	private long _getGuestGroupId() throws Exception {
		Group group = _groupLocalService.getGroup(
			_company.getCompanyId(), GroupConstants.GUEST);

		return group.getGroupId();
	}

	private void _resetCompanyLocales() throws Exception {
		CompanyTestUtil.resetCompanyLocales(
			_company.getCompanyId(), _locales, LocaleUtil.US);
	}

	private static Company _company;
	private static final Set<Locale> _locales = new HashSet<>(
		Arrays.asList(
			LocaleUtil.BRAZIL, LocaleUtil.HUNGARY, LocaleUtil.JAPAN,
			LocaleUtil.US));

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

}