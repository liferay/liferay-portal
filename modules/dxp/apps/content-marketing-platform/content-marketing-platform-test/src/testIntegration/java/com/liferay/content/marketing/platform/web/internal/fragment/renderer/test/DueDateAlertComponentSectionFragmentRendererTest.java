/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Larissa Ribeiro
 */
@RunWith(Arquillian.class)
public class DueDateAlertComponentSectionFragmentRendererTest
	extends BaseComponentSectionFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-107678")
	@Test
	public void testGetProps() throws Exception {
		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition, cmpTaskObjectEntry);

		Assert.assertEquals(Collections.emptyMap(), getProps());

		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition,
			addCMPTaskObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"r_cmpTaskToCMPTasks_c_cmpTaskId",
					cmpTaskObjectEntry.getObjectEntryId()
				).build()));

		Assert.assertEquals(Collections.emptyMap(), getProps());

		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition,
			addCMPTaskObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"r_cmpTaskToCMPTasks_c_cmpTaskId",
					() -> {
						ObjectEntry cmpTaskObjectEntry2 = addCMPTaskObjectEntry(
							HashMapBuilder.<String, Serializable>put(
								"dueDate",
								DateUtil.parseDate(
									"yyyy-MM-dd", "2026-12-11", LocaleUtil.US)
							).build());

						return cmpTaskObjectEntry2.getObjectEntryId();
					}
				).build()));

		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"parentDueDate", "2026-12-11"
			).build(),
			getProps());
	}

	@FeatureFlag(enable = false, value = "LPD-107678")
	@Test
	public void testGetPropsWhenFeatureFlagIsDisabled() throws Exception {
		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition,
			addCMPTaskObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"r_cmpTaskToCMPTasks_c_cmpTaskId",
					() -> {
						ObjectEntry cmpTaskObjectEntry2 = addCMPTaskObjectEntry(
							HashMapBuilder.<String, Serializable>put(
								"dueDate",
								DateUtil.parseDate(
									"yyyy-MM-dd", "2026-12-11", LocaleUtil.US)
							).build());

						return cmpTaskObjectEntry2.getObjectEntryId();
					}
				).build()));

		Assert.assertEquals(Collections.emptyMap(), getProps());
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	@Inject(
		filter = "component.name=com.liferay.content.marketing.platform.web.internal.fragment.renderer.DueDateAlertComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}