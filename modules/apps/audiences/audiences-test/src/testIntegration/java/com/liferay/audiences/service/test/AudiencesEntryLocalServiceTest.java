/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.exception.AudiencesEntryJSONException;
import com.liferay.audiences.exception.AudiencesEntryNameException;
import com.liferay.audiences.exception.DuplicateAudiencesEntryExternalReferenceCodeException;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AudiencesEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			TestPropsValues.getGroupId());
	}

	@Test
	@TestInfo("LPD-95291")
	public void testAddAudiencesEntry() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		AudiencesEntry audiencesEntry = _addAudiencesEntry(
			externalReferenceCode,
			StringBundler.concat(
				"{\"conjunction\": \"AND\", \"rules\": [{\"attribute\": ",
				"\"url\", \"operator\": \"eq\", \"value\": \"",
				RandomTestUtil.randomString(), "\"}]}"),
			name);

		Assert.assertEquals(
			externalReferenceCode, audiencesEntry.getExternalReferenceCode());
		Assert.assertNotNull(audiencesEntry.getJSON());
		Assert.assertEquals(name, audiencesEntry.getName());

		AssertUtils.assertFailure(
			AudiencesEntryNameException.class, null,
			() -> _addAudiencesEntry(null, StringPool.BLANK, StringPool.BLANK));
		AssertUtils.assertFailure(
			AudiencesEntryJSONException.class,
			"/conjunction: INVALID is not a valid enum value",
			() -> _addAudiencesEntry(
				null, "{\"conjunction\": \"INVALID\", \"rules\": []}",
				RandomTestUtil.randomString()));
	}

	@Test(
		expected = DuplicateAudiencesEntryExternalReferenceCodeException.class
	)
	@TestInfo("LPD-95291")
	public void testAddAudiencesEntryWithExistingExternalReferenceCode()
		throws Exception {

		AudiencesEntry audiencesEntry = _addAudiencesEntry(
			null, StringPool.BLANK, RandomTestUtil.randomString());

		_audiencesEntryLocalService.addAudiencesEntry(
			audiencesEntry.getExternalReferenceCode(), StringPool.BLANK,
			RandomTestUtil.randomString(), _serviceContext);
	}

	@Test
	@TestInfo("LPD-95291")
	public void testDeleteAudiencesEntry() throws Exception {
		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.addAudiencesEntry(
				null, StringPool.BLANK, RandomTestUtil.randomString(),
				_serviceContext);

		long audiencesEntryId = audiencesEntry.getAudiencesEntryId();

		_audiencesEntryLocalService.deleteAudiencesEntry(audiencesEntryId);

		Assert.assertNull(
			_audiencesEntryLocalService.fetchAudiencesEntry(audiencesEntryId));
	}

	@Test
	@TestInfo("LPD-95291")
	public void testUpdateAudiencesEntry() throws Exception {
		AudiencesEntry audiencesEntry = _addAudiencesEntry(
			null, StringPool.BLANK, RandomTestUtil.randomString());

		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		audiencesEntry = _audiencesEntryLocalService.updateAudiencesEntry(
			audiencesEntry.getAudiencesEntryId(), externalReferenceCode,
			audiencesEntry.getJSON(), name);

		Assert.assertEquals(
			externalReferenceCode, audiencesEntry.getExternalReferenceCode());
		Assert.assertEquals(name, audiencesEntry.getName());

		AudiencesEntry updatedAudiencesEntry = audiencesEntry;

		AssertUtils.assertFailure(
			AudiencesEntryNameException.class, null,
			() -> _audiencesEntryLocalService.updateAudiencesEntry(
				updatedAudiencesEntry.getAudiencesEntryId(),
				updatedAudiencesEntry.getExternalReferenceCode(),
				updatedAudiencesEntry.getJSON(), StringPool.BLANK));
		AssertUtils.assertFailure(
			AudiencesEntryJSONException.class,
			"/conjunction: INVALID is not a valid enum value",
			() -> _audiencesEntryLocalService.updateAudiencesEntry(
				updatedAudiencesEntry.getAudiencesEntryId(),
				updatedAudiencesEntry.getExternalReferenceCode(),
				"{\"conjunction\": \"INVALID\", \"rules\": []}",
				updatedAudiencesEntry.getName()));
	}

	@Test(
		expected = DuplicateAudiencesEntryExternalReferenceCodeException.class
	)
	@TestInfo("LPD-95291")
	public void testUpdateAudiencesEntryWithExistingExternalReferenceCode()
		throws Exception {

		AudiencesEntry audiencesEntry1 = _addAudiencesEntry(
			null, StringPool.BLANK, RandomTestUtil.randomString());

		AudiencesEntry audiencesEntry2 = _addAudiencesEntry(
			null, StringPool.BLANK, RandomTestUtil.randomString());

		_audiencesEntryLocalService.updateAudiencesEntry(
			audiencesEntry2.getAudiencesEntryId(),
			audiencesEntry1.getExternalReferenceCode(),
			audiencesEntry2.getJSON(), audiencesEntry2.getName());
	}

	private AudiencesEntry _addAudiencesEntry(
			String externalReferenceCode, String json, String name)
		throws Exception {

		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.addAudiencesEntry(
				externalReferenceCode, json, name, _serviceContext);

		_audiencesEntries.add(audiencesEntry);

		return audiencesEntry;
	}

	@DeleteAfterTestRun
	private final List<AudiencesEntry> _audiencesEntries = new ArrayList<>();

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	private ServiceContext _serviceContext;

}