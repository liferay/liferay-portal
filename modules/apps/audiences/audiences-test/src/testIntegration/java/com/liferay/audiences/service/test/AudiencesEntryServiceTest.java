/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.exception.NoSuchAudiencesEntryException;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.audiences.service.AudiencesEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class AudiencesEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_audiencesEntry = _audiencesEntryLocalService.addAudiencesEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringPool.BLANK, RandomTestUtil.randomString());
		_user = UserTestUtil.addCompanyAdminUser(CompanyTestUtil.addCompany());

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	@TestInfo("LPD-101887")
	public void testDeleteAudiencesEntry() throws Exception {
		long audiencesEntryId = _audiencesEntry.getAudiencesEntryId();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				NoSuchAudiencesEntryException.class,
				() -> _audiencesEntryService.deleteAudiencesEntry(
					audiencesEntryId));
		}

		Assert.assertNotNull(
			_audiencesEntryLocalService.fetchAudiencesEntry(audiencesEntryId));

		_audiencesEntryService.deleteAudiencesEntry(audiencesEntryId);

		Assert.assertNull(
			_audiencesEntryLocalService.fetchAudiencesEntry(audiencesEntryId));
	}

	@Test
	@TestInfo("LPD-101887")
	public void testGetAudiencesEntries() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		List<AudiencesEntry> audiencesEntries =
			_audiencesEntryService.getAudiencesEntries(
				companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(audiencesEntries.contains(_audiencesEntry));

		String name = _audiencesEntry.getName();

		audiencesEntries = _audiencesEntryService.getAudiencesEntries(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(audiencesEntries.contains(_audiencesEntry));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _audiencesEntryService.getAudiencesEntries(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _audiencesEntryService.getAudiencesEntries(
					companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null));
		}
	}

	@Test
	@TestInfo("LPD-101887")
	public void testGetAudiencesEntriesCount() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		String name = _audiencesEntry.getName();

		Assert.assertEquals(
			1,
			_audiencesEntryService.getAudiencesEntriesCount(companyId, name));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _audiencesEntryService.getAudiencesEntriesCount(
					companyId));

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _audiencesEntryService.getAudiencesEntriesCount(
					companyId, name));
		}
	}

	@Test
	@TestInfo("LPD-101887")
	public void testGetAudiencesEntry() throws Exception {
		long audiencesEntryId = _audiencesEntry.getAudiencesEntryId();

		Assert.assertEquals(
			_audiencesEntry,
			_audiencesEntryService.getAudiencesEntry(audiencesEntryId));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				NoSuchAudiencesEntryException.class,
				() -> _audiencesEntryService.getAudiencesEntry(
					audiencesEntryId));
		}
	}

	@Test
	@TestInfo("LPD-101887")
	public void testUpdateAudiencesEntry() throws Exception {
		long audiencesEntryId = _audiencesEntry.getAudiencesEntryId();
		String externalReferenceCode =
			_audiencesEntry.getExternalReferenceCode();
		String json = _audiencesEntry.getJSON();
		String name = RandomTestUtil.randomString();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				NoSuchAudiencesEntryException.class,
				() -> _audiencesEntryService.updateAudiencesEntry(
					audiencesEntryId, externalReferenceCode, json, name));
		}

		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.getAudiencesEntry(audiencesEntryId);

		Assert.assertEquals(
			_audiencesEntry.getName(), audiencesEntry.getName());
		Assert.assertEquals(
			TestPropsValues.getUserId(), audiencesEntry.getUserId());

		audiencesEntry = _audiencesEntryService.updateAudiencesEntry(
			audiencesEntryId, externalReferenceCode, json, name);

		Assert.assertEquals(name, audiencesEntry.getName());
	}

	private AudiencesEntry _audiencesEntry;

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Inject
	private AudiencesEntryService _audiencesEntryService;

	private User _user;

}