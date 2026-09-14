/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.audiences.service.AudiencesEntryGroupRelLocalService;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
public class AudiencesEntryGroupRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_audiencesEntry = _audiencesEntryLocalService.addAudiencesEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringPool.BLANK, RandomTestUtil.randomString());
	}

	@Test
	@TestInfo("LPD-105673")
	public void testDeleteAudiencesEntryDeletesAudiencesEntryGroupRels()
		throws Exception {

		_addAudiencesEntryGroupRel(GroupTestUtil.addGroup());
		_addAudiencesEntryGroupRel(GroupTestUtil.addGroup());

		Assert.assertEquals(2, _getAudiencesEntryGroupRelsCount());

		_audiencesEntryLocalService.deleteAudiencesEntry(_audiencesEntry);

		Assert.assertEquals(0, _getAudiencesEntryGroupRelsCount());
	}

	private AudiencesEntryGroupRel _addAudiencesEntryGroupRel(Group group)
		throws Exception {

		AudiencesEntryGroupRel audiencesEntryGroupRel =
			_audiencesEntryGroupRelLocalService.createAudiencesEntryGroupRel(
				_counterLocalService.increment());

		audiencesEntryGroupRel.setCompanyId(_audiencesEntry.getCompanyId());
		audiencesEntryGroupRel.setUserId(TestPropsValues.getUserId());
		audiencesEntryGroupRel.setAudienceEntryERC(
			_audiencesEntry.getExternalReferenceCode());
		audiencesEntryGroupRel.setGroupERC(group.getExternalReferenceCode());

		return _audiencesEntryGroupRelLocalService.addAudiencesEntryGroupRel(
			audiencesEntryGroupRel);
	}

	private List<AudiencesEntryGroupRel> _getAudiencesEntryGroupRels() {
		return _audiencesEntryGroupRelLocalService.
			getAudiencesEntryGroupRelsByAudienceEntryERC(
				_audiencesEntry.getCompanyId(),
				_audiencesEntry.getExternalReferenceCode());
	}

	private int _getAudiencesEntryGroupRelsCount() {
		List<AudiencesEntryGroupRel> audiencesEntryGroupRels =
			_getAudiencesEntryGroupRels();

		return audiencesEntryGroupRels.size();
	}

	private AudiencesEntry _audiencesEntry;

	@Inject
	private AudiencesEntryGroupRelLocalService
		_audiencesEntryGroupRelLocalService;

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

}
