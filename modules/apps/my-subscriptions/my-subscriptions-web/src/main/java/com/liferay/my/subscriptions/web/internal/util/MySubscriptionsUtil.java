/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.my.subscriptions.web.internal.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.journal.model.JournalFolder;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBThreadLocalServiceUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Peter Shin
 * @author Jonathan Lee
 */
public class MySubscriptionsUtil {

	public static AssetRenderer<?> getAssetRenderer(
		String className, long classPK) {

		try {
			return _getAssetRenderer(_getClassName(className), classPK);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	public static String getAssetTypeDescription(
		Locale locale, String className) {

		List<String> classNames = StringUtil.split(
			className, CharPool.UNDERLINE);

		if (classNames.size() < 2) {
			return ResourceActionsUtil.getModelResource(locale, className);
		}

		StringBundler sb = new StringBundler((classNames.size() * 2) + 2);

		sb.append(
			ResourceActionsUtil.getModelResource(locale, classNames.get(0)));

		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 1; i < classNames.size(); i++) {
			sb.append(
				ResourceActionsUtil.getModelResource(
					locale, classNames.get(i)));
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	public static String getAssetURLViewInContext(
			ThemeDisplay themeDisplay, String className, long classPK)
		throws PortalException {

		if (className.equals(_CLASS_NAME_BLOGS_ENTRY)) {
			return PortalUtil.getLayoutFullURL(classPK, PortletKeys.BLOGS);
		}

		if (className.equals(Folder.class.getName())) {
			return PortalUtil.getLayoutFullURL(
				classPK, PortletKeys.DOCUMENT_LIBRARY);
		}

		if (className.equals(_KNOWLEDGE_BASE_MODEL_CLASS_NAME)) {
			return PortalUtil.getLayoutFullURL(
				classPK, _KNOWLEDGE_BASE_DISPLAY_PORTLET_ID);
		}

		if (className.equals(Layout.class.getName())) {
			return PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayout(classPK), themeDisplay);
		}

		if (className.equals(MBCategory.class.getName())) {
			return PortalUtil.getLayoutFullURL(
				classPK,
				PortletProviderUtil.getPortletId(
					MBMessage.class.getName(), PortletProvider.Action.VIEW));
		}

		if (!className.equals(WikiNode.class.getName())) {
			return null;
		}

		long plid = PortalUtil.getPlidFromPortletId(
			themeDisplay.getScopeGroupId(), WikiPortletKeys.WIKI);

		if (plid == 0) {
			return null;
		}

		StringBundler sb = new StringBundler(5);

		sb.append(
			PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayout(plid), themeDisplay));
		sb.append(Portal.FRIENDLY_URL_SEPARATOR);
		sb.append("wiki/");
		sb.append(classPK);
		sb.append("/all_pages");

		return sb.toString();
	}

	public static String getTitleText(
			Locale locale, String className, long classPK, String title)
		throws PortalException {

		if (Validator.isNotNull(title)) {
			return title;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(classPK);

		if (className.equals(BlogsEntry.class.getName()) ||
			className.equals(_CLASS_NAME_BLOGS_ENTRY)) {

			title = "Blog at ";
		}
		else if (className.equals(DLFileEntryType.class.getName())) {
			if (group != null) {
				return LanguageUtil.get(locale, "basic-document");
			}

			DLFileEntryType dlFileEntryType =
				DLFileEntryTypeLocalServiceUtil.getDLFileEntryType(classPK);

			return dlFileEntryType.getName(locale);
		}
		else if (className.equals(DLFolderConstants.getClassName()) ||
				 className.equals(Folder.class.getName())) {

			if (group != null) {
				return LanguageUtil.get(locale, "home");
			}
		}
		else if (className.equals(JournalFolder.class.getName())) {
			if (group != null) {
				return LanguageUtil.get(locale, "home");
			}
		}
		else if (className.equals(_KNOWLEDGE_BASE_MODEL_CLASS_NAME)) {
			title = "Knowledge Base Article at ";
		}
		else if (className.equals(Layout.class.getName())) {
			Layout layout = LayoutLocalServiceUtil.getLayout(classPK);

			return layout.getName(locale);
		}
		else if (className.equals(MBCategory.class.getName())) {
			title = "Message Board at ";
		}
		else if (className.equals(PortletPreferences.class.getName())) {
			PortletPreferences portletPreferences =
				PortletPreferencesLocalServiceUtil.getPortletPreferences(
					classPK);

			jakarta.portlet.PortletPreferences jxPortletPreferences =
				PortletPreferencesFactoryUtil.getPortletSetup(
					LayoutLocalServiceUtil.getLayout(
						portletPreferences.getPlid()),
					portletPreferences.getPortletId(), null);

			String portletTitle = jxPortletPreferences.getValue(
				"portletSetupTitle_" + LocaleUtil.toLanguageId(locale),
				StringPool.BLANK);

			if (Validator.isNull(portletTitle)) {
				portletTitle = "Asset Publisher";
			}

			return portletTitle;
		}
		else if (className.equals(WikiNode.class.getName())) {
			WikiNode wikiNode = WikiNodeLocalServiceUtil.getWikiNode(classPK);

			return wikiNode.getName();
		}

		if (group != null) {
			title += group.getDescriptiveName(locale);
		}

		if (Validator.isNull(title)) {
			title = String.valueOf(classPK);
		}

		return title;
	}

	private static AssetRenderer<?> _getAssetRenderer(
			String className, long classPK)
		throws Exception {

		if (className.equals(Folder.class.getName())) {
			className = DLFolder.class.getName();
		}
		else if (className.equals(MBThread.class.getName())) {
			className = MBMessage.class.getName();

			MBThread mbThread = MBThreadLocalServiceUtil.getThread(classPK);

			classPK = mbThread.getRootMessageId();
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		return assetRendererFactory.getAssetRenderer(classPK);
	}

	private static String _getClassName(String className) {
		List<String> classNames = StringUtil.split(
			className, CharPool.UNDERLINE);

		if (classNames.size() == 2) {
			String firstClassName = classNames.get(0);

			if (firstClassName.equals(MBDiscussion.class.getName())) {
				return classNames.get(1);
			}
		}

		return className;
	}

	private static final String _CLASS_NAME_BLOGS_ENTRY =
		"com.liferay.blogs.kernel.model.BlogsEntry";

	private static final String _KNOWLEDGE_BASE_DISPLAY_PORTLET_ID =
		"com_liferay_knowledge_base_web_portlet_DisplayPortlet";

	private static final String _KNOWLEDGE_BASE_MODEL_CLASS_NAME =
		"com.liferay.knowledge.base.model.KBArticle";

	private static final Log _log = LogFactoryUtil.getLog(
		MySubscriptionsUtil.class);

}