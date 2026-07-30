/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Map;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
@Sync
public class ViewProjectInfoSummarySectionDisplayContextTest
	extends BaseInfoSummarySectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetProperties() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		String[] assetTagNames = {RandomTestUtil.randomString()};

		serviceContext.setAssetTagNames(assetTagNames);

		String title = RandomTestUtil.randomString();
		User user1 = UserTestUtil.addUser();
		User user2 = UserTestUtil.addUser();

		cmpProjectObjectEntry =
			_objectEntryLocalService.partialUpdateObjectEntry(
				cmpProjectObjectEntry.getUserId(),
				cmpProjectObjectEntry.getObjectEntryId(),
				cmpProjectObjectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"dueDate", "2026-01-31"
				).put(
					"r_userToCMPProjectManager_userId", user1.getUserId()
				).put(
					"r_userToCMPProjectSponsor_userId", user2.getUserId()
				).put(
					"state", "inProgress"
				).put(
					"title", title
				).build(),
				serviceContext);

		Map<String, Object> properties = getProperties(cmpProjectObjectEntry);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"cmpProjectObjectEntryId",
				cmpProjectObjectEntry.getObjectEntryId()
			).put(
				"dueDate", "2026-01-31"
			).put(
				"funnelStages", new String[0]
			).put(
				"hasUpdatePermission", true
			).put(
				"initialState", "inProgress"
			).put(
				"manager",
				JSONUtil.put(
					"image", user1.getPortraitURL(themeDisplay)
				).put(
					"name", user1.getFullName()
				)
			).put(
				"personas", new String[0]
			).put(
				"sponsor",
				JSONUtil.put(
					"image", user2.getPortraitURL(themeDisplay)
				).put(
					"name", user2.getFullName()
				)
			).put(
				"states",
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
					))
			).put(
				"tags", assetTagNames
			).put(
				"title", title
			).toString(),
			_jsonFactory.looseSerializeDeep(properties), true);
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	@Override
	protected Object getSectionDisplayContext(
		HttpServletRequest httpServletRequest) {

		return httpServletRequest.getAttribute(
			"com.liferay.site.cmp.site.initializer.internal.display.context." +
				"ViewProjectInfoSummarySectionDisplayContext");
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewProjectInfoSummaryJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}