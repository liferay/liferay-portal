/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.upgrade.v1_1_0;

import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	public UpgradePortletPreferences(
		GroupLocalService groupLocalService,
		JournalArticleLocalService journalArticleLocalService,
		Language language, LayoutLocalService layoutLocalService,
		Portal portal) {

		_groupLocalService = groupLocalService;
		_journalArticleLocalService = journalArticleLocalService;
		_language = language;
		_layoutLocalService = layoutLocalService;
		_portal = portal;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			StringPool.PERCENT + JournalContentPortletKeys.JOURNAL_CONTENT +
				StringPool.PERCENT
		};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Portlet ", portletId, " with portlet preferences ",
					MapUtil.toString(portletPreferences.getMap())));
		}

		try {
			long groupId = GetterUtil.getLong(
				portletPreferences.getValue("groupId", "0"));

			if (groupId == 0) {
				return PortletPreferencesFactoryUtil.toXML(portletPreferences);
			}

			String articleId = GetterUtil.getString(
				portletPreferences.getValue("articleId", StringPool.BLANK));

			if (Validator.isNull(articleId)) {
				return PortletPreferencesFactoryUtil.toXML(portletPreferences);
			}

			JournalArticle journalArticle =
				_journalArticleLocalService.fetchArticle(groupId, articleId);

			if (journalArticle == null) {
				return PortletPreferencesFactoryUtil.toXML(portletPreferences);
			}

			Group group = _groupLocalService.getGroup(groupId);

			Layout layout = null;

			String scopeLayoutUuid = StringPool.BLANK;
			String scopeType = StringPool.BLANK;

			if (group.isCompany()) {
				scopeType = "company";
			}
			else if (group.isLayout()) {
				scopeType = "layout";

				layout = _layoutLocalService.fetchLayout(group.getClassPK());

				if (layout != null) {
					scopeLayoutUuid = layout.getUuid();
				}
			}

			String lfrScopeLayoutUuid = portletPreferences.getValue(
				"lfrScopeLayoutUuid", StringPool.BLANK);
			String lfrScopeType = portletPreferences.getValue(
				"lfrScopeType", StringPool.BLANK);

			if (Objects.equals(lfrScopeLayoutUuid, scopeLayoutUuid) &&
				Objects.equals(lfrScopeType, scopeType)) {

				return PortletPreferencesFactoryUtil.toXML(portletPreferences);
			}

			portletPreferences.setValue("lfrScopeLayoutUuid", scopeLayoutUuid);
			portletPreferences.setValue("lfrScopeType", scopeType);

			Locale locale = _portal.getSiteDefaultLocale(groupId);

			String portletSetupTitlePreferenceKey =
				"portletSetupTitle_" + LocaleUtil.toLanguageId(locale);

			String portletSetupTitle = portletPreferences.getValue(
				portletSetupTitlePreferenceKey, StringPool.BLANK);

			if (Validator.isNotNull(portletSetupTitle)) {
				portletSetupTitle = StringUtil.stripParentheticalSuffix(
					portletSetupTitle);

				if (layout != null) {
					portletSetupTitle = StringUtil.appendParentheticalSuffix(
						portletSetupTitle, layout.getName(locale));
				}
				else if (Objects.equals(scopeType, "company")) {
					portletSetupTitle = StringUtil.appendParentheticalSuffix(
						portletSetupTitle, _language.get(locale, "global"));
				}

				portletPreferences.setValue(
					portletSetupTitlePreferenceKey, portletSetupTitle);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw exception;
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePortletPreferences.class);

	private final GroupLocalService _groupLocalService;
	private final JournalArticleLocalService _journalArticleLocalService;
	private final Language _language;
	private final LayoutLocalService _layoutLocalService;
	private final Portal _portal;

}