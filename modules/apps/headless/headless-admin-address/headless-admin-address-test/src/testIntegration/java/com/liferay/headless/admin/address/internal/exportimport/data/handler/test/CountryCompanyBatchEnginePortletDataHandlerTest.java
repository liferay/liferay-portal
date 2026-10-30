/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.index.IndexStatusManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class CountryCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_regionIndexReadOnly = _indexStatusManager.isIndexReadOnly(
			Region.class.getName());

		_indexStatusManager.setIndexReadOnly(Region.class.getName(), true);
	}

	@After
	public void tearDown() {
		_indexStatusManager.setIndexReadOnly(
			Region.class.getName(), _regionIndexReadOnly);
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_countryLocalService.addCountry(
			externalReferenceCode, _getRandomA2(group.getCompanyId()),
			_getRandomA3(group.getCompanyId()), true, true,
			String.valueOf(RandomTestUtil.randomInt(100, 999)),
			RandomTestUtil.randomString(),
			String.valueOf(RandomTestUtil.nextInt()),
			RandomTestUtil.randomDouble(), true, true, true,
			ServiceContextTestUtil.getServiceContext(
				group.getCompanyId(), groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_countryLocalService.deleteCountry(
			_getCountry(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		Country country = _getCountry(externalReferenceCode, groupId);

		return country.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return com.liferay.headless.admin.address.dto.v1_0.Country.class.
			getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		Country country = _getCountry(externalReferenceCode, groupId);

		return country.getCountryId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		Country country =
			_countryLocalService.fetchCountryByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());

		if (country == null) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean supportsComments() {
		return false;
	}

	@Override
	protected boolean supportsEmptyEntries() {
		return false;
	}

	@Override
	protected boolean supportsPermissions() {
		return true;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		Country country = _getCountry(externalReferenceCode, groupId);

		country.setUserId(userId);

		_countryLocalService.updateCountry(country);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Country country = _getCountry(externalReferenceCode, groupId);

		_countryLocalService.updateCountry(
			country.getExternalReferenceCode(), country.getCountryId(),
			country.getA2(), country.getA3(), country.isActive(),
			country.isBillingAllowed(), country.getIdd(),
			RandomTestUtil.randomString(), country.getNumber(),
			country.getPosition(), country.isShippingAllowed(),
			country.isSubjectToVAT());
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		Country country = _getCountry(externalReferenceCode, groupId);

		country.setModifiedDate(modifiedDate);

		_countryLocalService.updateCountry(country);
	}

	private Country _getCountry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _countryLocalService.getCountryByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());
	}

	private String _getRandomA2(long companyId) {
		return StringUtil.toUpperCase(
			RandomTestUtil.randomString(
				2,
				randomString -> {
					Country country = _countryLocalService.fetchCountryByA2(
						companyId, StringUtil.toUpperCase(randomString));

					return country == null;
				}));
	}

	private String _getRandomA3(long companyId) {
		return StringUtil.toUpperCase(
			RandomTestUtil.randomString(
				3,
				randomString -> {
					Country country = _countryLocalService.fetchCountryByA3(
						companyId, StringUtil.toUpperCase(randomString));

					return country == null;
				}));
	}

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private IndexStatusManager _indexStatusManager;

	private boolean _regionIndexReadOnly;

}