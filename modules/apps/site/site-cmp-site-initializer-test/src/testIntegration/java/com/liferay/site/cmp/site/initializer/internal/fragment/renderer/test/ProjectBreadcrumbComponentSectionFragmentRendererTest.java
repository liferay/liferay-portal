/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

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
public class ProjectBreadcrumbComponentSectionFragmentRendererTest
	extends BaseComponentSectionFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetProps() throws Exception {
		Map<String, Object> props = getProps();

		JSONArray jsonArray = (JSONArray)props.get("actionItems");

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"href",
				StringBundler.concat(
					themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL, "/e/edit-project/",
					_portal.getClassNameId(
						cmpProjectObjectDefinition.getClassName()),
					StringPool.SLASH, cmpProjectObjectEntry.getObjectEntryId(),
					"?redirect=", themeDisplay.getURLCurrent())
			).put(
				"label", "Edit"
			).put(
				"symbolLeft", "pencil"
			).toString(),
			jsonObject.toString(), true);

		jsonObject = jsonArray.getJSONObject(1);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"href",
				StringBundler.concat(
					"/o", cmpProjectObjectDefinition.getRESTContextPath(),
					"/scopes/", cmpProjectObjectEntry.getGroupId(),
					"/by-external-reference-code/",
					cmpProjectObjectEntry.getExternalReferenceCode(),
					"/subscribe")
			).put(
				"label", "Watch Project"
			).put(
				"redirect",
				StringBundler.concat(
					themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL, "/e/project/",
					PortalUtil.getClassNameId(
						cmpProjectObjectDefinition.getClassName()),
					StringPool.SLASH, cmpProjectObjectEntry.getObjectEntryId())
			).put(
				"successMessage",
				_language.format(
					mockHttpServletRequest, "you-are-successfully-watching-x",
					StringBundler.concat(
						"<strong>", cmpProjectObjectEntryTitle, "</strong>"))
			).put(
				"symbolLeft", "bell-on"
			).put(
				"target", "asyncPost"
			).toString(),
			jsonObject.toString(), true);

		jsonObject = jsonArray.getJSONObject(2);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"className", "text-danger"
			).put(
				"confirmationMessage",
				_language.format(
					mockHttpServletRequest, "delete-project-confirmation-body",
					cmpProjectObjectEntryTitle)
			).put(
				"confirmationTitle",
				_language.format(
					mockHttpServletRequest, "delete-asset-confirmation-title",
					cmpProjectObjectEntryTitle)
			).put(
				"href",
				StringBundler.concat(
					"/o", cmpProjectObjectDefinition.getRESTContextPath(),
					StringPool.SLASH, cmpProjectObjectEntry.getObjectEntryId())
			).put(
				"label", "Delete"
			).put(
				"redirect",
				StringBundler.concat(
					themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL, "/projects")
			).put(
				"successMessage",
				_language.format(
					mockHttpServletRequest, "x-was-successfully-deleted",
					StringBundler.concat(
						"<strong>", cmpProjectObjectEntryTitle, "</strong>"))
			).put(
				"symbolLeft", "trash"
			).put(
				"target", "asyncDelete"
			).toString(),
			jsonObject.toString(), true);

		jsonArray = (JSONArray)props.get("breadcrumbItems");

		jsonObject = jsonArray.getJSONObject(0);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"active", false
			).put(
				"href",
				StringBundler.concat(
					themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL, "/projects")
			).put(
				"label", LanguageUtil.get(mockHttpServletRequest, "projects")
			).toString(),
			jsonObject.toString(), true);

		jsonObject = jsonArray.getJSONObject(1);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"active", true
			).put(
				"href", StringPool.BLANK
			).put(
				"label", cmpProjectObjectEntryTitle
			).toString(),
			jsonObject.toString(), true);

		Assert.assertTrue((Boolean)props.get("hideSpace"));
		Assert.assertEquals("lg", props.get("size"));
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ProjectBreadcrumbComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private Language _language;

	@Inject
	private Portal _portal;

}