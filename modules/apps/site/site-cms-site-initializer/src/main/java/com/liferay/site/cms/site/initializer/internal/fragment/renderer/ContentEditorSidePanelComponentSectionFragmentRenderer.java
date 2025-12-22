/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.internal.util.CommentUtil;
import com.liferay.subscription.service.SubscriptionLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sandro Chinea
 */
@Component(service = FragmentRenderer.class)
public class ContentEditorSidePanelComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "content-editor";
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (Objects.equals(layoutMode, Constants.READ)) {
			return;
		}

		super.render(
			fragmentRendererContext, httpServletRequest, httpServletResponse);
	}

	@Override
	protected String getLabelKey() {
		return "content-editor-side-panel";
	}

	@Override
	protected String getModuleName() {
		return "ContentEditorSidePanel";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if (layoutDisplayPageObjectProvider == null) {
			return Collections.emptyMap();
		}

		Object displayObject =
			layoutDisplayPageObjectProvider.getDisplayObject();

		if (!(displayObject instanceof ObjectEntry)) {
			return Collections.emptyMap();
		}

		ObjectEntry objectEntry = (ObjectEntry)displayObject;

		long classNameId = _classNameLocalService.getClassNameId(
			objectEntry.getModelClassName());
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"addCommentURL",
			() -> {
				if (_discussionPermission.hasAddPermission(
						themeDisplay.getPermissionChecker(),
						themeDisplay.getCompanyId(),
						themeDisplay.getScopeGroupId(),
						objectEntry.getModelClassName(),
						objectEntry.getObjectEntryId())) {

					return StringBundler.concat(
						themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
						GroupConstants.CMS_FRIENDLY_URL,
						"/add_content_item_comment?classNameId=", classNameId,
						"&classPK=", objectEntry.getObjectEntryId());
				}

				return null;
			}
		).put(
			"assetLibraryId", objectEntry.getGroupId()
		).put(
			"assetType", classNameId
		).put(
			"cmsGroupId", themeDisplay.getScopeGroupId()
		).put(
			"comments",
			() -> {
				JSONArray jsonArray = _jsonFactory.createJSONArray();

				if (!_commentManager.hasDiscussion(
						objectEntry.getModelClassName(),
						objectEntry.getObjectEntryId())) {

					return jsonArray;
				}

				List<Comment> rootComments = _commentManager.getRootComments(
					objectEntry.getModelClassName(),
					objectEntry.getObjectEntryId(),
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

				for (Comment rootComment : rootComments) {
					JSONObject commentJSONObject =
						CommentUtil.getCommentJSONObject(
							rootComment, httpServletRequest);

					List<Comment> childComments =
						_commentManager.getChildComments(
							rootComment.getCommentId(),
							WorkflowConstants.STATUS_APPROVED,
							QueryUtil.ALL_POS, QueryUtil.ALL_POS);

					JSONArray childCommentsJSONArray =
						_jsonFactory.createJSONArray();

					for (Comment childComment : childComments) {
						childCommentsJSONArray.put(
							CommentUtil.getCommentJSONObject(
								childComment, httpServletRequest));
					}

					commentJSONObject.put("children", childCommentsJSONArray);

					jsonArray.put(commentJSONObject);
				}

				return jsonArray;
			}
		).put(
			"contentAPIURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), "/o",
				objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId())
		).put(
			"deleteCommentURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL, "/delete_content_item_comment")
		).put(
			"editCommentURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/edit_content_item_comment?classNameId=",
				classNameId, "&classPK=", objectEntry.getObjectEntryId())
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
			"expirationDate",
			() -> {
				Date expirationDate = objectEntry.getExpirationDate();

				if (expirationDate == null) {
					return null;
				}

				return DateUtil.getDate(
					expirationDate, "yyyy-MM-dd'T'HH:mm",
					themeDisplay.getLocale());
			}
		).put(
			"hasUpdatePermission",
			() -> {
				ModelResourcePermission<ObjectEntry> modelResourcePermission =
					_objectEntryService.getModelResourcePermission(
						objectEntry.getObjectDefinitionId());

				return modelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), objectEntry,
					ActionKeys.UPDATE);
			}
		).put(
			"id", String.valueOf(objectEntry.getObjectEntryId())
		).put(
			"isSubscribed",
			() -> _subscriptionLocalService.isSubscribed(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId())
		).put(
			"reviewDate",
			() -> {
				Date reviewDate = objectEntry.getReviewDate();

				if (reviewDate == null) {
					return null;
				}

				return DateUtil.getDate(
					reviewDate, "yyyy-MM-dd'T'HH:mm", themeDisplay.getLocale());
			}
		).put(
			"subscribeURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/subscribe_content_item?classNameId=", classNameId,
				"&classPK=", objectEntry.getObjectEntryId(),
				"&objectDefinitionId=", objectEntry.getObjectDefinitionId())
		).put(
			"type", objectDefinition.getLabel(themeDisplay.getLocale())
		).put(
			"version", () -> String.valueOf(objectEntry.getVersion())
		).build();
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

}