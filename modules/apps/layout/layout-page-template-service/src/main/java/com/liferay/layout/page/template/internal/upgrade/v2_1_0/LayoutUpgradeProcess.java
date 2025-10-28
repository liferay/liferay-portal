/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v2_1_0;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * @author Pavel Savinov
 */
public class LayoutUpgradeProcess extends UpgradeProcess {

	public LayoutUpgradeProcess(
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		LayoutLocalService layoutLocalService,
		LayoutPrototypeLocalService layoutPrototypeLocalService) {

		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_layoutLocalService = layoutLocalService;
		_layoutPrototypeLocalService = layoutPrototypeLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeSchema();
		_upgradeLayout();
	}

	private String _generateFriendlyURLUUID() {
		UUID uuid = new UUID(
			SecureRandomUtil.nextLong(), SecureRandomUtil.nextLong());

		return StringPool.SLASH + uuid;
	}

	private long _getPlid(
			long companyId, long userId, long groupId, String name, int type,
			long layoutPrototypeId, ServiceContext serviceContext)
		throws Exception {

		if ((type == LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE) &&
			(layoutPrototypeId > 0)) {

			LayoutPrototype layoutPrototype =
				_layoutPrototypeLocalService.getLayoutPrototype(
					layoutPrototypeId);

			Layout layout = layoutPrototype.getLayout();

			return layout.getPlid();
		}

		boolean privateLayout = false;
		String layoutType = LayoutConstants.TYPE_ASSET_DISPLAY;

		if (type == LayoutPageTemplateEntryTypeConstants.BASIC) {
			layoutType = LayoutConstants.TYPE_CONTENT;
			privateLayout = true;
		}

		Map<Locale, String> titleMap = Collections.singletonMap(
			LocaleUtil.getSiteDefault(), name);

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		Layout layout = _layoutLocalService.addLayout(
			null, PortalUtil.getValidUserId(companyId, userId), groupId,
			privateLayout, 0, titleMap, titleMap, null, null, null, layoutType,
			UnicodePropertiesBuilder.put(
				LayoutTypeSettingsConstants.KEY_PUBLISHED, "true"
			).buildString(),
			true, true,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), _generateFriendlyURLUUID()
			).build(),
			serviceContext);

		return layout.getPlid();
	}

	private void _upgradeLayout() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		try (LoggingTimer loggingTimer = new LoggingTimer();
			Statement s = connection.createStatement();
			ResultSet resultSet = s.executeQuery(
				StringBundler.concat(
					"select layoutPageTemplateEntryId, groupId, companyId, ",
					"userId, name, type_, layoutPrototypeId, companyId from ",
					"LayoutPageTemplateEntry where plid is null or plid = 0"));
			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update LayoutPageTemplateEntry set plid = ? where " +
						"layoutPageTemplateEntryId = ?")) {

			while (resultSet.next()) {
				long companyId = resultSet.getLong("companyId");
				long userId = resultSet.getLong("userId");
				long groupId = resultSet.getLong("groupId");
				String name = resultSet.getString("name");
				int type = resultSet.getInt("type_");
				long layoutPrototypeId = resultSet.getLong("layoutPrototypeId");

				long plid = _getPlid(
					companyId, userId, groupId, name, type, layoutPrototypeId,
					serviceContext);

				preparedStatement.setLong(1, plid);

				long layoutPageTemplateEntryId = resultSet.getLong(
					"layoutPageTemplateEntryId");

				preparedStatement.setLong(2, layoutPageTemplateEntryId);

				preparedStatement.addBatch();

				List<FragmentEntryLink> fragmentEntryLinks =
					_fragmentEntryLinkLocalService.getFragmentEntryLinks(
						groupId,
						PortalUtil.getClassNameId(
							LayoutPageTemplateEntry.class),
						layoutPageTemplateEntryId);

				Layout draftLayout = _layoutLocalService.fetchDraftLayout(plid);

				for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
					fragmentEntryLink.setClassNameId(
						PortalUtil.getClassNameId(Layout.class));
					fragmentEntryLink.setClassPK(plid);
					fragmentEntryLink.setPlid(plid);

					fragmentEntryLink =
						_fragmentEntryLinkLocalService.updateFragmentEntryLink(
							fragmentEntryLink);

					_fragmentEntryLinkLocalService.addFragmentEntryLink(
						null, draftLayout.getUserId(), draftLayout.getGroupId(),
						null, fragmentEntryLink.getFragmentEntryERC(),
						fragmentEntryLink.getFragmentEntryScopeERC(), 0,
						draftLayout.getPlid(), fragmentEntryLink.getCss(),
						fragmentEntryLink.getHtml(), fragmentEntryLink.getJs(),
						fragmentEntryLink.getConfiguration(),
						fragmentEntryLink.getEditableValues(), StringPool.BLANK,
						fragmentEntryLink.getPosition(), null,
						fragmentEntryLink.getType(), serviceContext);
				}
			}

			preparedStatement.executeBatch();
		}
	}

	private void _upgradeSchema() throws Exception {
		alterTableAddColumn("LayoutPageTemplateEntry", "plid", "LONG");
	}

	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPrototypeLocalService _layoutPrototypeLocalService;

}