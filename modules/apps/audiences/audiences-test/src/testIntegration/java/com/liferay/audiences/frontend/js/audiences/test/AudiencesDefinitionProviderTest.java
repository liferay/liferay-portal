/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.frontend.js.audiences.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.frontend.js.audiences.AudiencesDefinition;
import com.liferay.frontend.js.audiences.AudiencesDefinitionProvider;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
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
public class AudiencesDefinitionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-93951"))
	@Test
	public void testGetAudiencesDefinition() throws Exception {
		String name = RandomTestUtil.randomString();

		_audiencesEntries.add(
			_audiencesEntryLocalService.addAudiencesEntry(
				null, "{\"conjunction\": \"AND\", \"rules\": []}", name,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId())));

		AudiencesDefinition audiencesDefinition =
			_audiencesDefinitionProvider.getAudiencesDefinition(
				TestPropsValues.getCompanyId());

		String content = audiencesDefinition.getContent();

		Assert.assertTrue(content.contains(name));
		Assert.assertEquals(
			HashedFilesUtil.computeHash(content),
			audiencesDefinition.getHash());
	}

	@Inject
	private AudiencesDefinitionProvider _audiencesDefinitionProvider;

	@DeleteAfterTestRun
	private final List<AudiencesEntry> _audiencesEntries = new ArrayList<>();

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

}