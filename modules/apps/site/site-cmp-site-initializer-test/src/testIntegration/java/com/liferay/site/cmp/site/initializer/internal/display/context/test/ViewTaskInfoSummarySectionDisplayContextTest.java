/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Assignee;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Map;

import org.junit.Before;
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
@Sync
public class ViewTaskInfoSummarySectionDisplayContextTest
	extends BaseInfoSummarySectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);
	}

	@Test
	public void testGetProperties() throws Exception {
		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		String[] assetTagNames = {RandomTestUtil.randomString()};

		serviceContext.setAssetTagNames(assetTagNames);

		String title = RandomTestUtil.randomString();

		_cmpTaskObjectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
			_cmpTaskObjectEntry.getUserId(),
			_cmpTaskObjectEntry.getObjectEntryId(),
			_cmpTaskObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"assignTo",
				HashMapBuilder.put(
					"classNameId",
					_classNameLocalService.getClassNameId(Role.class.getName())
				).put(
					"classPK", role.getRoleId()
				).build()
			).put(
				"dueDate", "2026-01-31"
			).put(
				"state", "inProgress"
			).put(
				"title", title
			).build(),
			serviceContext);

		Map<String, Object> properties = getProperties(_cmpTaskObjectEntry);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"assignTo",
				JSONUtil.put(
					"externalReferenceCode", role.getExternalReferenceCode()
				).put(
					"name", role.getName()
				).put(
					"type", Assignee.Type.ROLE.toString()
				)
			).put(
				"cmpTaskObjectEntryId", _cmpTaskObjectEntry.getObjectEntryId()
			).put(
				"dueDate", "2026-01-31"
			).put(
				"hasUpdatePermission", true
			).put(
				"initialState", "inProgress"
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
				"ViewTaskInfoSummarySectionDisplayContext");
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private ObjectEntry _cmpTaskObjectEntry;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewTaskInfoSummaryJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}