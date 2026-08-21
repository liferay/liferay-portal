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
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
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

	@Test
	@TestInfo("LPD-95291")
	public void testAddAudiencesEntry() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		AudiencesEntry audiencesEntry = _addAudiencesEntry(
			externalReferenceCode,
			JSONUtil.put(
				"conjunction", "AND"
			).put(
				"rules",
				JSONUtil.putAll(
					JSONUtil.put(
						"attribute", "url"
					).put(
						"operator", "eq"
					).put(
						"value", RandomTestUtil.randomString()
					),
					JSONUtil.put(
						"attribute", "segments"
					).put(
						"operator", "includes"
					).put(
						"value", RandomTestUtil.randomString()
					),
					JSONUtil.put(
						"attribute",
						"custom:/o/frontend-js-audiences-web/__liferay__" +
							"/custom-attributes.js#signed_in"
					).put(
						"operator", "eq"
					).put(
						"value", true
					))
			).toString(),
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
				null,
				JSONUtil.put(
					"conjunction", "INVALID"
				).put(
					"rules", JSONUtil.putAll()
				).toString(),
				RandomTestUtil.randomString()));
		AssertUtils.assertFailure(
			DuplicateAudiencesEntryExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate audiences entry with external reference code ",
				externalReferenceCode, " and company ",
				TestPropsValues.getCompanyId()),
			() -> _addAudiencesEntry(
				externalReferenceCode, StringPool.BLANK,
				RandomTestUtil.randomString()));
	}

	@Test
	@TestInfo("LPD-95291")
	public void testDeleteAudiencesEntry() throws Exception {
		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.addAudiencesEntry(
				null, TestPropsValues.getUserId(), StringPool.BLANK,
				RandomTestUtil.randomString());

		long audiencesEntryId = audiencesEntry.getAudiencesEntryId();

		_audiencesEntryLocalService.deleteAudiencesEntry(audiencesEntryId);

		Assert.assertNull(
			_audiencesEntryLocalService.fetchAudiencesEntry(audiencesEntryId));
	}

	@Test
	@TestInfo({"LPD-94450", "LPD-95291"})
	public void testUpdateAudiencesEntry() throws Exception {
		AudiencesEntry audiencesEntry = _addAudiencesEntry(
			null, StringPool.BLANK, RandomTestUtil.randomString());

		_user = UserTestUtil.addUser();

		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		audiencesEntry = _audiencesEntryLocalService.updateAudiencesEntry(
			externalReferenceCode, _user.getUserId(),
			audiencesEntry.getAudiencesEntryId(), audiencesEntry.getJSON(),
			name);

		Assert.assertEquals(
			externalReferenceCode, audiencesEntry.getExternalReferenceCode());
		Assert.assertEquals(name, audiencesEntry.getName());
		Assert.assertEquals(_user.getUserId(), audiencesEntry.getUserId());
		Assert.assertEquals(_user.getFullName(), audiencesEntry.getUserName());

		AudiencesEntry updatedAudiencesEntry = audiencesEntry;

		AssertUtils.assertFailure(
			AudiencesEntryNameException.class, null,
			() -> _audiencesEntryLocalService.updateAudiencesEntry(
				updatedAudiencesEntry.getExternalReferenceCode(),
				TestPropsValues.getUserId(),
				updatedAudiencesEntry.getAudiencesEntryId(),
				updatedAudiencesEntry.getJSON(), StringPool.BLANK));
		AssertUtils.assertFailure(
			AudiencesEntryJSONException.class,
			"/conjunction: INVALID is not a valid enum value",
			() -> _audiencesEntryLocalService.updateAudiencesEntry(
				updatedAudiencesEntry.getExternalReferenceCode(),
				TestPropsValues.getUserId(),
				updatedAudiencesEntry.getAudiencesEntryId(),
				JSONUtil.put(
					"conjunction", "INVALID"
				).put(
					"rules", JSONUtil.putAll()
				).toString(),
				updatedAudiencesEntry.getName()));

		audiencesEntry = _addAudiencesEntry(
			null, StringPool.BLANK, RandomTestUtil.randomString());

		long audiencesEntryId = audiencesEntry.getAudiencesEntryId();

		AssertUtils.assertFailure(
			DuplicateAudiencesEntryExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate audiences entry with external reference code ",
				externalReferenceCode, " and company ",
				TestPropsValues.getCompanyId()),
			() -> _audiencesEntryLocalService.updateAudiencesEntry(
				externalReferenceCode, TestPropsValues.getUserId(),
				audiencesEntryId, StringPool.BLANK,
				RandomTestUtil.randomString()));
	}

	private AudiencesEntry _addAudiencesEntry(
			String externalReferenceCode, String json, String name)
		throws Exception {

		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.addAudiencesEntry(
				externalReferenceCode, TestPropsValues.getUserId(), json, name);

		_audiencesEntries.add(audiencesEntry);

		return audiencesEntry;
	}

	@DeleteAfterTestRun
	private final List<AudiencesEntry> _audiencesEntries = new ArrayList<>();

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@DeleteAfterTestRun
	private User _user;

}