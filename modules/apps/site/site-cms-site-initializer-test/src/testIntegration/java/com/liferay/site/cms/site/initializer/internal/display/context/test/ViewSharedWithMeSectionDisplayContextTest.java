/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alicia García
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
@Sync
public class ViewSharedWithMeSectionDisplayContextTest
	extends BaseSectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testGetAdditionalProps() throws Exception {
		_assertEquals(
			HashMapBuilder.<String, Object>put(
				"autocompleteURL",
				() -> StringBundler.concat(
					"/o/search/v1.0/search?emptySearch=true",
					"&entryClassNames=com.liferay.portal.kernel.model.User,",
					"com.liferay.portal.kernel.model.UserGroup&nestedFields=",
					"embedded")
			).put(
				"collaboratorURLs",
				() -> {
					Map<String, String> collaboratorURLs = new HashMap<>();

					for (ObjectDefinition objectDefinition :
							objectDefinitionService.getCMSObjectDefinitions(
								group.getCompanyId(),
								getObjectFolderExternalReferenceCodes())) {

						collaboratorURLs.put(
							objectDefinition.getClassName(),
							StringBundler.concat(
								"/o", objectDefinition.getRESTContextPath(),
								"/{objectEntryId}/collaborators"));
					}

					collaboratorURLs.put(
						ObjectEntryFolder.class.getName(),
						"/o/headless-object/v1.0/object-entry-folders" +
							"/{objectEntryFolderId}/collaborators");

					return collaboratorURLs;
				}
			).put(
				"commentsProps",
				HashMapBuilder.<String, Object>put(
					"addCommentURL",
					StringBundler.concat(
						themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
						GroupConstants.CMS_FRIENDLY_URL,
						"/add_content_item_comment")
				).put(
					"deleteCommentURL",
					StringBundler.concat(
						themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
						GroupConstants.CMS_FRIENDLY_URL,
						"/delete_content_item_comment")
				).put(
					"editCommentURL",
					StringBundler.concat(
						themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
						GroupConstants.CMS_FRIENDLY_URL,
						"/edit_content_item_comment")
				).put(
					"editorConfig",
					() -> {
						EditorConfiguration
							contentItemCommentEditorConfiguration =
								EditorConfigurationFactoryUtil.
									getEditorConfiguration(
										StringPool.BLANK,
										"contentItemCommentEditor",
										StringPool.BLANK,
										Collections.emptyMap(), themeDisplay,
										RequestBackedPortletURLFactoryUtil.
											create(themeDisplay.getRequest()));

						Map<String, Object> data =
							contentItemCommentEditorConfiguration.getData();

						return data.get("editorConfig");
					}
				).put(
					"getCommentsURL",
					StringBundler.concat(
						themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
						GroupConstants.CMS_FRIENDLY_URL, "/get_asset_comments")
				).build()
			).put(
				"contentViewURL",
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?&p_l_mode=read&p_p_state=",
					LiferayWindowState.POP_UP, "&redirect=",
					themeDisplay.getURLCurrent(),
					"&objectEntryId={embedded.id}")
			).build(),
			getAdditionalProps());
	}

	@Override
	protected CreationMenu getCreationMenu(ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return null;
	}

	@Override
	protected Map<String, String> getExpectedCreationMenuItems()
		throws PortalException {

		return Collections.emptyMap();
	}

	@Override
	protected String getObjectFolderExternalReferenceCode() {
		if (RandomTestUtil.randomBoolean()) {
			return ObjectFolderConstants.
				EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES;
		}

		return ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES;
	}

	@Override
	protected String[] getObjectFolderExternalReferenceCodes() {
		return new String[] {
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
		};
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		Object viewRecycleBinSectionDisplayContext =
			httpServletRequest.getAttribute(
				"com.liferay.site.cms.site.initializer.internal.display." +
					"context.ViewSharedWithMeSectionDisplayContext");

		Assert.assertNotNull(viewRecycleBinSectionDisplayContext);

		return viewRecycleBinSectionDisplayContext;
	}

	private void _assertEquals(
			Map<String, ?> expectedMap, Map<String, ?> actualMap)
		throws Exception {

		Assert.assertEquals(
			actualMap.toString(), expectedMap.size(), actualMap.size());

		JSONObject expectedJSONObject = _jsonFactory.createJSONObject(
			expectedMap);
		JSONObject actualJSONObject = _jsonFactory.createJSONObject(actualMap);

		JSONAssert.assertEquals(
			expectedJSONObject.toString(), actualJSONObject.toString(),
			JSONCompareMode.STRICT);
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewSharedWithMeJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private JSONFactory _jsonFactory;

}