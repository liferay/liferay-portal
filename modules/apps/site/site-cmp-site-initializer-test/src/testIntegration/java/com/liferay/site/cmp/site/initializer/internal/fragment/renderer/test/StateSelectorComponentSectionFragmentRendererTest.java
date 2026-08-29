/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class StateSelectorComponentSectionFragmentRendererTest
	extends BaseComponentSectionFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetProps() throws Exception {
		Assert.assertEquals(
			"notStarted", MapUtil.getString(getProps(), "initialSelectedKey"));
		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"key", "blocked"
				).put(
					"name", "Blocked"
				).put(
					"nextStates", new String[] {"done", "inProgress"}
				),
				JSONUtil.put(
					"key", "done"
				).put(
					"name", "Done"
				).put(
					"nextStates", new String[] {"inProgress"}
				),
				JSONUtil.put(
					"key", "inProgress"
				).put(
					"name", "In Progress"
				).put(
					"nextStates", new String[] {"blocked", "done"}
				),
				JSONUtil.put(
					"key", "notStarted"
				).put(
					"name", "Not Started"
				).put(
					"nextStates", new String[] {"blocked", "inProgress"}
				)
			).toString(),
			MapUtil.getString(getProps(), "states"), true);

		cmpProjectObjectEntry =
			_objectEntryLocalService.partialUpdateObjectEntry(
				cmpProjectObjectEntry.getUserId(),
				cmpProjectObjectEntry.getObjectEntryId(),
				cmpProjectObjectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"state", "inProgress"
				).put(
					"title", RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		mockHttpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, cmpProjectObjectEntry);

		Assert.assertEquals(
			"inProgress", MapUtil.getString(getProps(), "initialSelectedKey"));
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.StateSelectorComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}