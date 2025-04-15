/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.ThemeDisplayModel;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Wesley Gong
 * @author Angelo Jefferson
 * @author Hugo Huijser
 */
public class JournalUtil {

	public static final String[] SELECTED_FIELD_NAMES = {
		Field.ARTICLE_ID, Field.COMPANY_ID, Field.GROUP_ID, Field.UID
	};

	public static String getJournalControlPanelLink(
		long folderId, long groupId,
		LiferayPortletResponse liferayPortletResponse) {

		if (liferayPortletResponse != null) {
			return PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setParameter(
				"folderId", folderId
			).setParameter(
				"groupId", groupId
			).buildString();
		}

		try {
			String articleURL = PortalUtil.getControlPanelFullURL(
				groupId,
				PortletProviderUtil.getPortletId(
					JournalArticle.class.getName(),
					PortletProvider.Action.EDIT),
				null);

			String namespace = PortalUtil.getPortletNamespace(
				JournalPortletKeys.JOURNAL);

			articleURL = HttpComponentsUtil.addParameter(
				articleURL, namespace + "groupId", groupId);

			return HttpComponentsUtil.addParameter(
				articleURL, namespace + "folderId", folderId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	public static String getJournalControlPanelLink(
			PortletRequest portletRequest, long folderId)
		throws PortalException {

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				portletRequest, JournalArticle.class.getName(),
				PortletProvider.Action.EDIT)
		).setParameter(
			"folderId", folderId
		).buildString();
	}

	public static Map<String, String> getTokens(
			JournalArticle article, DDMTemplate ddmTemplate,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws PortalException {

		DDMStructure ddmStructure = article.getDDMStructure();

		Map<String, String> tokens = HashMapBuilder.put(
			TemplateConstants.CLASS_NAME_ID,
			String.valueOf(
				ClassNameLocalServiceUtil.getClassNameId(DDMStructure.class))
		).put(
			"article_resource_pk", String.valueOf(article.getResourcePrimKey())
		).put(
			"ddm_structure_id", String.valueOf(ddmStructure.getStructureId())
		).put(
			"ddm_structure_key", ddmStructure.getStructureKey()
		).build();

		if (ddmTemplate != null) {
			tokens.put(
				"ddm_template_id", String.valueOf(ddmTemplate.getTemplateId()));
			tokens.put(
				"ddm_template_key",
				String.valueOf(ddmTemplate.getTemplateKey()));

			Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
				article.getCompanyId());

			if (companyGroup.getGroupId() == ddmTemplate.getGroupId()) {
				tokens.put(
					"company_group_id",
					String.valueOf(companyGroup.getGroupId()));
			}
		}

		if (themeDisplay != null) {
			_populateTokens(tokens, article.getGroupId(), themeDisplay);
		}
		else if (portletRequestModel != null) {
			ThemeDisplayModel themeDisplayModel =
				portletRequestModel.getThemeDisplayModel();

			if (themeDisplayModel != null) {
				try {
					_populateTokens(
						tokens, article.getGroupId(), themeDisplayModel);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(exception);
					}
				}
			}
		}
		else {
			tokens.put("company_id", String.valueOf(article.getCompanyId()));

			Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
				article.getCompanyId());

			tokens.put(
				"article_group_id", String.valueOf(article.getGroupId()));
			tokens.put(
				"company_group_id", String.valueOf(companyGroup.getGroupId()));
		}

		return tokens;
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
			title = FriendlyURLNormalizerUtil.normalizeWithEncoding(title);
		}

		return ModelHintsUtil.trimString(
			JournalArticle.class.getName(), "urlTitle", title);
	}

	public static boolean isHead(JournalArticle article) {
		JournalArticle latestArticle =
			JournalArticleLocalServiceUtil.fetchLatestArticle(
				article.getResourcePrimKey(),
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_IN_TRASH
				});

		if ((latestArticle != null) && !latestArticle.isIndexable()) {
			return false;
		}
		else if ((latestArticle != null) &&
				 (article.getId() == latestArticle.getId())) {

			return true;
		}

		return false;
	}

	public static boolean isHeadListable(JournalArticle article) {
		JournalArticle latestArticle =
			JournalArticleLocalServiceUtil.fetchLatestArticle(
				article.getResourcePrimKey(),
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_IN_TRASH,
					WorkflowConstants.STATUS_SCHEDULED
				});

		if ((latestArticle != null) &&
			(article.getId() == latestArticle.getId())) {

			return true;
		}

		return false;
	}

	public static boolean isLatestArticle(JournalArticle article) {
		JournalArticle latestArticle =
			JournalArticleLocalServiceUtil.fetchLatestArticle(
				article.getResourcePrimKey(), WorkflowConstants.STATUS_ANY,
				false);

		if ((latestArticle != null) &&
			(article.getId() == latestArticle.getId())) {

			return true;
		}

		return false;
	}

	private static String _getCustomTokenValue(
		String tokenName,
		JournalServiceConfiguration journalServiceConfiguration) {

		for (String tokenValue :
				journalServiceConfiguration.customTokenValues()) {

			String[] tokenValueParts = tokenValue.split("\\=");

			if (tokenValueParts.length != 2) {
				continue;
			}

			if (tokenValueParts[0].equals(tokenName)) {
				return tokenValueParts[1];
			}
		}

		return null;
	}

	private static void _populateCustomTokens(
		Map<String, String> tokens, long companyId) {

		JournalServiceConfiguration journalServiceConfiguration = null;

		try {
			journalServiceConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					JournalServiceConfiguration.class, companyId);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (journalServiceConfiguration == null) {
			return;
		}

		if ((_customTokens == null) &&
			ArrayUtil.isNotEmpty(
				journalServiceConfiguration.customTokenNames()) &&
			ArrayUtil.isNotEmpty(
				journalServiceConfiguration.customTokenValues())) {

			synchronized (JournalUtil.class) {
				_customTokens = new HashMap<>();

				for (String tokenName :
						journalServiceConfiguration.customTokenNames()) {

					String tokenValue = _getCustomTokenValue(
						tokenName, journalServiceConfiguration);

					if (Validator.isNull(tokenValue)) {
						continue;
					}

					_customTokens.put(tokenName, tokenValue);
				}
			}
		}

		if (MapUtil.isNotEmpty(_customTokens)) {
			tokens.putAll(_customTokens);
		}
	}

	private static void _populateTokens(
			Map<String, String> tokens, long articleGroupId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();

		Group group = layout.getGroup();

		LayoutSet layoutSet = layout.getLayoutSet();

		String friendlyUrlCurrent = null;

		if (layout.isPublicLayout()) {
			friendlyUrlCurrent = themeDisplay.getPathFriendlyURLPublic();
		}
		else if (group.isUserGroup()) {
			friendlyUrlCurrent = themeDisplay.getPathFriendlyURLPrivateUser();
		}
		else {
			friendlyUrlCurrent = themeDisplay.getPathFriendlyURLPrivateGroup();
		}

		String layoutSetFriendlyUrl = themeDisplay.getI18nPath();

		TreeMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		if (virtualHostnames.isEmpty() ||
			!virtualHostnames.containsKey(themeDisplay.getServerName())) {

			layoutSetFriendlyUrl = friendlyUrlCurrent + group.getFriendlyURL();
		}

		tokens.put("article_group_id", String.valueOf(articleGroupId));
		tokens.put("cdn_host", themeDisplay.getCDNHost());
		tokens.put("company_id", String.valueOf(themeDisplay.getCompanyId()));
		tokens.put("friendly_url_current", friendlyUrlCurrent);
		tokens.put(
			"friendly_url_private_group",
			themeDisplay.getPathFriendlyURLPrivateGroup());
		tokens.put(
			"friendly_url_private_user",
			themeDisplay.getPathFriendlyURLPrivateUser());
		tokens.put(
			"friendly_url_public", themeDisplay.getPathFriendlyURLPublic());
		tokens.put("group_friendly_url", group.getFriendlyURL());
		tokens.put("image_path", themeDisplay.getPathImage());
		tokens.put("layout_set_friendly_url", layoutSetFriendlyUrl);
		tokens.put("main_path", themeDisplay.getPathMain());
		tokens.put("portal_ctx", themeDisplay.getPathContext());
		tokens.put(
			"portal_url",
			HttpComponentsUtil.removeProtocol(themeDisplay.getURLPortal()));
		tokens.put(
			"protocol",
			HttpComponentsUtil.getProtocol(themeDisplay.getURLPortal()));
		tokens.put("root_path", themeDisplay.getPathContext());
		tokens.put(
			"scope_group_id", String.valueOf(themeDisplay.getScopeGroupId()));
		tokens.put(
			"site_group_id", String.valueOf(themeDisplay.getSiteGroupId()));
		tokens.put("theme_image_path", themeDisplay.getPathThemeImages());

		_populateCustomTokens(tokens, themeDisplay.getCompanyId());
	}

	private static void _populateTokens(
			Map<String, String> tokens, long articleGroupId,
			ThemeDisplayModel themeDisplayModel)
		throws Exception {

		Layout layout = LayoutLocalServiceUtil.getLayout(
			themeDisplayModel.getPlid());

		Group group = layout.getGroup();

		LayoutSet layoutSet = layout.getLayoutSet();

		String friendlyUrlCurrent = null;

		if (layout.isPublicLayout()) {
			friendlyUrlCurrent = themeDisplayModel.getPathFriendlyURLPublic();
		}
		else if (group.isUserGroup()) {
			friendlyUrlCurrent =
				themeDisplayModel.getPathFriendlyURLPrivateUser();
		}
		else {
			friendlyUrlCurrent =
				themeDisplayModel.getPathFriendlyURLPrivateGroup();
		}

		String layoutSetFriendlyUrl = themeDisplayModel.getI18nPath();

		TreeMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		if (virtualHostnames.isEmpty() ||
			!virtualHostnames.containsKey(themeDisplayModel.getServerName())) {

			layoutSetFriendlyUrl = friendlyUrlCurrent + group.getFriendlyURL();
		}

		tokens.put("article_group_id", String.valueOf(articleGroupId));
		tokens.put("cdn_host", themeDisplayModel.getCdnHost());
		tokens.put(
			"company_id", String.valueOf(themeDisplayModel.getCompanyId()));
		tokens.put("friendly_url_current", friendlyUrlCurrent);
		tokens.put(
			"friendly_url_private_group",
			themeDisplayModel.getPathFriendlyURLPrivateGroup());
		tokens.put(
			"friendly_url_private_user",
			themeDisplayModel.getPathFriendlyURLPrivateUser());
		tokens.put(
			"friendly_url_public",
			themeDisplayModel.getPathFriendlyURLPublic());
		tokens.put("group_friendly_url", group.getFriendlyURL());
		tokens.put("image_path", themeDisplayModel.getPathImage());
		tokens.put("layout_set_friendly_url", layoutSetFriendlyUrl);
		tokens.put("main_path", themeDisplayModel.getPathMain());
		tokens.put("portal_ctx", themeDisplayModel.getPathContext());
		tokens.put(
			"portal_url",
			HttpComponentsUtil.removeProtocol(
				themeDisplayModel.getURLPortal()));
		tokens.put(
			"protocol",
			HttpComponentsUtil.getProtocol(themeDisplayModel.getURLPortal()));
		tokens.put("root_path", themeDisplayModel.getPathContext());
		tokens.put(
			"scope_group_id",
			String.valueOf(themeDisplayModel.getScopeGroupId()));
		tokens.put("theme_image_path", themeDisplayModel.getPathThemeImages());

		_populateCustomTokens(tokens, themeDisplayModel.getCompanyId());
	}

	private static final Log _log = LogFactoryUtil.getLog(JournalUtil.class);

	private static Map<String, String> _customTokens;

}