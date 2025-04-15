/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.util;

import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.knowledge.base.service.KBFolderLocalServiceUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
public class KnowledgeBaseUtil {

	public static String getKBArticleAbsolutePath(
			PortletRequest portletRequest, long resourcePrimKey)
		throws PortalException {

		KBArticle kbArticle = KBArticleLocalServiceUtil.getLatestKBArticle(
			resourcePrimKey);

		String kbFolderAbsolutePath = getKBFolderAbsolutePath(
			portletRequest, kbArticle.getKbFolderId());

		if (!kbArticle.hasParentKBArticle()) {
			return kbFolderAbsolutePath;
		}

		List<KBArticle> kbArticles = kbArticle.getAncestorKBArticles();

		StringBundler sb = new StringBundler((kbArticles.size() * 3) + 2);

		sb.append(kbFolderAbsolutePath);
		sb.append(StringPool.SPACE);

		Collections.reverse(kbArticles);

		for (KBArticle currentKBArticle : kbArticles) {
			sb.append(StringPool.RAQUO_CHAR);
			sb.append(StringPool.SPACE);
			sb.append(currentKBArticle.getTitle());
		}

		return sb.toString();
	}

	public static String getKBArticleControlPanelLink(
			PortletRequest portletRequest, long resourcePrimKey)
		throws PortalException {

		long classNameId = PortalUtil.getClassNameId(
			KBArticleConstants.getClassName());

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				portletRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/knowledge_base/view"
		).setParameter(
			"parentResourceClassNameId", classNameId
		).setParameter(
			"parentResourcePrimKey", resourcePrimKey
		).setParameter(
			"resourceClassNameId", classNameId
		).setParameter(
			"resourcePrimKey", resourcePrimKey
		).setParameter(
			"selectedItemId", resourcePrimKey
		).buildString();
	}

	public static String getKBArticleDeleteURL(
		LiferayPortletResponse liferayPortletResponse, String cmd,
		boolean forceLock, String redirectURL, long resourcePrimKey) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/knowledge_base/delete_kb_article"
		).setCMD(
			cmd
		).setRedirect(
			redirectURL
		).setParameter(
			"forceLock", forceLock
		).setParameter(
			"resourcePrimKey", resourcePrimKey
		).buildString();
	}

	public static String getKBArticleEditURL(
		LiferayPortletRequest liferayPortletRequest, boolean forceLock,
		String redirectURL, long resourcePrimKey) {

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/knowledge_base/edit_kb_article"
		).setRedirect(
			redirectURL
		).setParameter(
			"forceLock", forceLock
		).setParameter(
			"resourcePrimKey", resourcePrimKey
		).buildString();
	}

	public static String getKBArticleExpireURL(
		LiferayPortletResponse liferayPortletResponse, boolean forceLock,
		String redirectURL, long resourcePrimKey) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/knowledge_base/expire_kb_article"
		).setRedirect(
			redirectURL
		).setParameter(
			"forceLock", forceLock
		).setParameter(
			"resourcePrimKey", resourcePrimKey
		).buildString();
	}

	public static String getKBArticleMoveURL(
		LiferayPortletResponse liferayPortletResponse, boolean dragAndDrop,
		boolean forceLock, long parentResourceClassNameId,
		long parentResourcePrimKey, int position, double priority,
		String redirectURL, long resourceClassNameId, long resourcePrimKey) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/knowledge_base/move_kb_object"
		).setRedirect(
			redirectURL
		).setParameter(
			"dragAndDrop", dragAndDrop
		).setParameter(
			"forceLock", forceLock
		).setParameter(
			"parentResourceClassNameId", parentResourceClassNameId
		).setParameter(
			"parentResourcePrimKey", parentResourcePrimKey
		).setParameter(
			"position", position
		).setParameter(
			"priority", priority
		).setParameter(
			"resourceClassNameId", resourceClassNameId
		).setParameter(
			"resourcePrimKey", resourcePrimKey
		).buildString();
	}

	public static String getKBArticleRevertURL(
		LiferayPortletResponse liferayPortletResponse, boolean forceLock,
		String redirectURL, long resourcePrimKey, int version) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/knowledge_base/update_kb_article"
		).setCMD(
			Constants.REVERT
		).setRedirect(
			redirectURL
		).setParameter(
			"forceLock", forceLock
		).setParameter(
			"resourcePrimKey", resourcePrimKey
		).setParameter(
			"version", version
		).setParameter(
			"workflowAction", WorkflowConstants.ACTION_PUBLISH
		).buildString();
	}

	public static String getKBArticleURL(
		long plid, long resourcePrimKey, int status, String portalURL,
		boolean maximized) {

		StringBundler sb = new StringBundler(10);

		sb.append(portalURL);
		sb.append(PortalUtil.getPathMain());
		sb.append("/knowledge_base/find_kb_article?plid=");
		sb.append(plid);
		sb.append("&resourcePrimKey=");
		sb.append(resourcePrimKey);

		if (status != WorkflowConstants.STATUS_APPROVED) {
			sb.append("&status=");
			sb.append(status);
		}

		if (maximized) {
			sb.append("&maximized=");
			sb.append(maximized);
		}

		return sb.toString();
	}

	public static String getKBFolderAbsolutePath(
			PortletRequest portletRequest, long kbFolderId)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (kbFolderId == KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return themeDisplay.translate("home");
		}

		KBFolder kbFolder = KBFolderLocalServiceUtil.getKBFolder(kbFolderId);

		List<KBFolder> kbFolders = kbFolder.getAncestorKBFolders();

		StringBundler sb = new StringBundler((kbFolders.size() * 4) + 5);

		sb.append(themeDisplay.translate("home"));
		sb.append(StringPool.SPACE);

		Collections.reverse(kbFolders);

		for (KBFolder currrentKBFolder : kbFolders) {
			sb.append(StringPool.RAQUO_CHAR);
			sb.append(StringPool.SPACE);
			sb.append(currrentKBFolder.getName());
			sb.append(StringPool.SPACE);
		}

		sb.append(StringPool.RAQUO_CHAR);
		sb.append(StringPool.SPACE);
		sb.append(kbFolder.getName());

		return sb.toString();
	}

	public static String getKBFolderControlPanelLink(
			PortletRequest portletRequest, long kbFolderId)
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				portletRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/knowledge_base/view"
		).buildPortletURL();

		if (kbFolderId != KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			portletURL.setParameter(
				"parentResourceClassNameId",
				String.valueOf(
					PortalUtil.getClassNameId(
						KBFolderConstants.getClassName())));
			portletURL.setParameter(
				"parentResourcePrimKey", String.valueOf(kbFolderId));
			portletURL.setParameter(
				"selectedItemId", String.valueOf(kbFolderId));
		}

		return portletURL.toString();
	}

	public static long getKBFolderId(
			long parentResourceClassNameId, long parentResourcePrimKey)
		throws PortalException {

		long kbFolderClassNameId = PortalUtil.getClassNameId(
			KBFolderConstants.getClassName());

		if (parentResourceClassNameId == kbFolderClassNameId) {
			return parentResourcePrimKey;
		}

		KBArticle kbArticle = KBArticleLocalServiceUtil.getLatestKBArticle(
			parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

		return kbArticle.getKbFolderId();
	}

	public static String getMimeType(byte[] bytes, String fileName) {
		try (InputStream inputStream = new UnsyncByteArrayInputStream(bytes)) {
			return MimeTypesUtil.getContentType(inputStream, fileName);
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}
		}

		return null;
	}

	public static Long[][] getParams(Long[] params) {
		if (ArrayUtil.isEmpty(params)) {
			return null;
		}

		if (params.length <= DBManagerUtil.getDBMaxParameters()) {
			return new Long[][] {new Long[0], params};
		}

		return new Long[][] {
			ArrayUtil.subset(
				params, DBManagerUtil.getDBMaxParameters(), params.length),
			ArrayUtil.subset(params, 0, DBManagerUtil.getDBMaxParameters())
		};
	}

	public static String getRedirect(ActionRequest actionRequest) {
		String redirect = (String)actionRequest.getAttribute(WebKeys.REDIRECT);

		if (Validator.isNull(redirect)) {
			redirect = ParamUtil.getString(actionRequest, "redirect");

			if (!Validator.isBlank(redirect)) {
				redirect = PortalUtil.escapeRedirect(redirect);
			}
		}

		return redirect;
	}

	public static String getUrlTitle(long id, String title) {
		if (title == null) {
			return String.valueOf(id);
		}

		title = StringUtil.toLowerCase(title.trim());

		if (Validator.isNull(title) || Validator.isNumber(title) ||
			title.equals("rss")) {

			title = String.valueOf(id);
		}
		else {
			title = FriendlyURLNormalizerUtil.normalizeWithPeriodsAndSlashes(
				title);
		}

		return ModelHintsUtil.trimString(
			KBArticle.class.getName(), "urlTitle", title);
	}

	public static boolean isValidUrlTitle(String urlTitle) {
		Matcher matcher = _validFriendlyUrlPattern.matcher(urlTitle);

		return matcher.matches();
	}

	public static void setPreferredKBFolderURLTitle(
			PortalPreferences portalPreferences, String contentRootPrefix,
			String value)
		throws JSONException {

		String preferredKBFolderURLTitle = portalPreferences.getValue(
			KBPortletKeys.KNOWLEDGE_BASE_DISPLAY, "preferredKBFolderURLTitle",
			"{}");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			preferredKBFolderURLTitle);

		jsonObject.put(contentRootPrefix, value);

		portalPreferences.setValue(
			KBPortletKeys.KNOWLEDGE_BASE_DISPLAY, "preferredKBFolderURLTitle",
			jsonObject.toString());
	}

	public static List<KBArticle> sort(
		long[] resourcePrimKeys, List<KBArticle> kbArticles) {

		Map<Long, KBArticle> map = new HashMap<>();

		for (KBArticle kbArticle : kbArticles) {
			map.put(kbArticle.getResourcePrimKey(), kbArticle);
		}

		kbArticles.clear();

		for (long resourcePrimKey : resourcePrimKeys) {
			if (map.containsKey(resourcePrimKey)) {
				kbArticles.add(map.get(resourcePrimKey));
			}
		}

		return kbArticles;
	}

	public static String[] splitKeywords(String keywords) {
		Set<String> keywordsSet = new LinkedHashSet<>();

		StringBundler sb = new StringBundler();

		for (char c : keywords.toCharArray()) {
			if (Character.isWhitespace(c)) {
				if (sb.length() > 0) {
					keywordsSet.add(sb.toString());

					sb = new StringBundler();
				}
			}
			else if (Character.isLetterOrDigit(c)) {
				sb.append(c);
			}
			else {
				return new String[] {keywords};
			}
		}

		if (sb.length() > 0) {
			keywordsSet.add(sb.toString());
		}

		return StringUtil.split(StringUtil.merge(keywordsSet));
	}

	public static String trimLeadingSlash(String s) {
		if (Validator.isNull(s)) {
			return s;
		}

		int x = 0;

		for (char c : s.toCharArray()) {
			if ((c != CharPool.BACK_SLASH) && (c != CharPool.FORWARD_SLASH)) {
				break;
			}

			x = x + 1;
		}

		return s.substring(x);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KnowledgeBaseUtil.class);

	private static final Pattern _validFriendlyUrlPattern = Pattern.compile(
		"/[a-z0-9_-]+");

}