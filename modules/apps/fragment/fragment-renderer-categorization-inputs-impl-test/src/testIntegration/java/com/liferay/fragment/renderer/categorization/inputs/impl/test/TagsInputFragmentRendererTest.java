/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.categorization.inputs.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class TagsInputFragmentRendererTest
	extends BaseInputFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	protected ObjectEntry addObjectEntry() throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), group.getGroupId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId(),
				new String[] {"tag1"}));
	}

	@Override
	protected void assertRender(
		int expectedResult, HttpServletRequest httpServletRequest) {

		Map<String, Object> data =
			(Map<String, Object>)httpServletRequest.getAttribute(
				"liferay-asset:asset-tags-selector:data");

		List<Map<String, String>> selectedItems =
			(List<Map<String, String>>)data.get("selectedItems");

		Assert.assertEquals(
			selectedItems.toString(), expectedResult, selectedItems.size());

		if (expectedResult <= 0) {
			return;
		}

		Map<String, String> selectedItem = selectedItems.get(0);

		Assert.assertEquals("tag1", selectedItem.get("value"));
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _tagsInputFragmentRenderer;
	}

	@Override
	protected String getRenderKey() {
		return "com.liferay.fragment.renderer.categorization.inputs.internal." +
			"TagsInputFragmentRenderer";
	}

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.fragment.renderer.categorization.inputs.internal.TagsInputFragmentRenderer",
		type = FragmentRenderer.class
	)
	private FragmentRenderer _tagsInputFragmentRenderer;

}