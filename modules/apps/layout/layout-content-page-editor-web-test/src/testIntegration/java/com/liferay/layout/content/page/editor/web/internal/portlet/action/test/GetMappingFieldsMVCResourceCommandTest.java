/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Locale;
import java.util.Objects;

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
public class GetMappingFieldsMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void test() throws Exception {
		long classNameId = _portal.getClassNameId(BlogsEntry.class);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, 0, null, true,
				WorkflowConstants.STATUS_APPROVED);

		JSONArray jsonArray = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getMappingFieldsJSONArray",
			new Class<?>[] {long.class, String.class, long.class, Locale.class},
			classNameId, StringPool.BLANK, _group.getGroupId(), LocaleUtil.US);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (!Objects.equals(
					jsonObject.getString("label"),
					_DISPLAY_PAGE_FIELDSET_NAME)) {

				continue;
			}

			JSONArray fieldsJSONArray = jsonObject.getJSONArray("fields");

			JSONObject defaultJSONObject = fieldsJSONArray.getJSONObject(0);

			Assert.assertEquals(
				"displayPageURL", defaultJSONObject.getString("name"));

			JSONObject assetDisplayPageJSONObject =
				fieldsJSONArray.getJSONObject(1);

			Assert.assertEquals(
				layoutPageTemplateEntry.getName(),
				assetDisplayPageJSONObject.getString("name"));
		}
	}

	private static final String _DISPLAY_PAGE_FIELDSET_NAME = "Display Page";

	private Group _group;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/get_mapping_fields"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private Portal _portal;

}