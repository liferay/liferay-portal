/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer;

import com.liferay.content.marketing.platform.web.internal.util.ObjectEntryUtil;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = FragmentRenderer.class)
public class CommentsComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "comments";
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		return "CommentsPanel";
	}

	@Override
	protected String getLabelKey() {
		return "comments";
	}

	@Override
	protected String getModuleName() {
		return "site-cms-site-initializer";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = ObjectEntryUtil.getObjectEntry(
			httpServletRequest);

		if (objectEntry == null) {
			return null;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"addCommentURL",
			_getURL(
				"/add_content_item_comment", objectDefinition, objectEntry,
				themeDisplay)
		).put(
			"deleteCommentURL",
			_getURL(
				"/delete_content_item_comment", objectDefinition, objectEntry,
				themeDisplay)
		).put(
			"editCommentURL",
			_getURL(
				"/edit_content_item_comment", objectDefinition, objectEntry,
				themeDisplay)
		).put(
			"editorConfig",
			() -> {
				EditorConfiguration contentItemCommentEditorConfiguration =
					EditorConfigurationFactoryUtil.getEditorConfiguration(
						StringPool.BLANK, "contentItemCommentEditor",
						StringPool.BLANK, Collections.emptyMap(), themeDisplay,
						RequestBackedPortletURLFactoryUtil.create(
							httpServletRequest));

				Map<String, Object> data =
					contentItemCommentEditorConfiguration.getData();

				return data.get("editorConfig");
			}
		).put(
			"getCommentsURL",
			_getURL(
				"/get_asset_comments", objectDefinition, objectEntry,
				themeDisplay)
		).put(
			"getMentionUsersURL",
			() -> {
				if (FeatureFlagManagerUtil.isEnabled(
						themeDisplay.getCompanyId(), "LPD-104086")) {

					return _getURL(
						"/get_content_item_mention_users", objectDefinition,
						objectEntry, themeDisplay);
				}

				return null;
			}
		).build();
	}

	private String _getURL(
		String actionId, ObjectDefinition objectDefinition,
		ObjectEntry objectEntry, ThemeDisplay themeDisplay) {

		return StringBundler.concat(
			themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
			GroupConstants.CMS_FRIENDLY_URL, actionId, "?p_l_id=",
			themeDisplay.getPlid(), "&classNameId=",
			_portal.getClassNameId(objectDefinition.getClassName()),
			"&classPK=", objectEntry.getObjectEntryId());
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

}