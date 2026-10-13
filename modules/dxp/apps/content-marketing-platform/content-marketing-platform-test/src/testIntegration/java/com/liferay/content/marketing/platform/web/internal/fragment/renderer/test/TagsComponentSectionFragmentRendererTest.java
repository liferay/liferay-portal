/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class TagsComponentSectionFragmentRendererTest
	extends BaseComponentSectionFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetProps() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		String keyword1 = RandomTestUtil.randomString();
		String keyword2 = RandomTestUtil.randomString();

		serviceContext.setAssetTagNames(new String[] {keyword1, keyword2});

		cmpProjectObjectEntry =
			_objectEntryLocalService.partialUpdateObjectEntry(
				cmpProjectObjectEntry.getUserId(),
				cmpProjectObjectEntry.getObjectEntryId(),
				cmpProjectObjectEntry.getObjectEntryFolderId(),
				Collections.emptyMap(), serviceContext);

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());

		AssertUtils.assertEquals(
			HashMapBuilder.<String, Object>put(
				"cmsGroupId", themeDisplay.getScopeGroupId()
			).put(
				"hasUpdatePermission", true
			).put(
				"objectEntryKeywords", new String[] {keyword1, keyword2}
			).put(
				"projectGroupId", GroupConstants.DEFAULT_PARENT_GROUP_ID
			).build(),
			getProps());

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker(
				UserTestUtil.addUser(), false));

		AssertUtils.assertEquals(
			HashMapBuilder.<String, Object>put(
				"cmsGroupId", themeDisplay.getScopeGroupId()
			).put(
				"hasUpdatePermission", false
			).put(
				"objectEntryKeywords", new String[] {keyword1, keyword2}
			).put(
				"projectGroupId", GroupConstants.DEFAULT_PARENT_GROUP_ID
			).build(),
			getProps());
	}

	@FeatureFlag("LPD-99403")
	@Test
	public void testGetPropsWithCMPProjectObjectEntry() throws Exception {
		Map<String, Object> props = getProps();

		Assert.assertEquals(
			cmpProjectObjectEntry.getGroupId(), props.get("projectGroupId"));

		testGetPropsWithCMPTaskObjectEntry();
		testGetPropsWithCMSObjectEntry();
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	@Inject(
		filter = "component.name=com.liferay.content.marketing.platform.web.internal.fragment.renderer.TagsComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}