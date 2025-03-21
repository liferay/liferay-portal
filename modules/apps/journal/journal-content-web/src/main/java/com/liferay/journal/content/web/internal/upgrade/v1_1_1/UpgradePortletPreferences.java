/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.upgrade.v1_1_1;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author Mikel Lorza
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	public UpgradePortletPreferences(
		long ddmStructureClassNameId,
		DDMTemplateLocalService ddmTemplateLocalService,
		GroupLocalService groupLocalService,
		JournalArticleLocalService journalArticleLocalService,
		LayoutLocalService layoutLocalService, Portal portal) {

		_ddmStructureClassNameId = ddmStructureClassNameId;
		_ddmTemplateLocalService = ddmTemplateLocalService;
		_groupLocalService = groupLocalService;
		_journalArticleLocalService = journalArticleLocalService;
		_layoutLocalService = layoutLocalService;
		_portal = portal;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			JournalContentPortletKeys.JOURNAL_CONTENT + "_INSTANCE_%"
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

		String articleId = portletPreferences.getValue("articleId", null);
		long groupId = GetterUtil.getLong(
			portletPreferences.getValue("groupId", null));

		if (Validator.isNull(articleId) || (groupId == 0)) {
			return PortletPreferencesFactoryUtil.toXML(portletPreferences);
		}

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return PortletPreferencesFactoryUtil.toXML(portletPreferences);
		}

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticle(groupId, articleId);

		if (journalArticle == null) {
			return PortletPreferencesFactoryUtil.toXML(portletPreferences);
		}

		portletPreferences.reset("articleId");
		portletPreferences.reset("groupId");
		portletPreferences.setValue(
			"articleExternalReferenceCode",
			journalArticle.getExternalReferenceCode());
		portletPreferences.setValue(
			"groupExternalReferenceCode", group.getExternalReferenceCode());

		String ddmTemplateKey = portletPreferences.getValue(
			"ddmTemplateKey", null);

		if (Validator.isNull(ddmTemplateKey)) {
			return PortletPreferencesFactoryUtil.toXML(portletPreferences);
		}

		long ddmTemplateGroupId = _portal.getSiteGroupId(
			journalArticle.getGroupId());

		if (group.isCompany()) {
			Layout layout = _layoutLocalService.fetchLayout(plid);

			if (layout != null) {
				ddmTemplateGroupId = layout.getGroupId();
			}
		}

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			ddmTemplateGroupId, _ddmStructureClassNameId, ddmTemplateKey, true);

		if (ddmTemplate == null) {
			return PortletPreferencesFactoryUtil.toXML(portletPreferences);
		}

		portletPreferences.reset("ddmTemplateKey");
		portletPreferences.setValue(
			"ddmTemplateExternalReferenceCode",
			ddmTemplate.getExternalReferenceCode());

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private final long _ddmStructureClassNameId;
	private final DDMTemplateLocalService _ddmTemplateLocalService;
	private final GroupLocalService _groupLocalService;
	private final JournalArticleLocalService _journalArticleLocalService;
	private final LayoutLocalService _layoutLocalService;
	private final Portal _portal;

}