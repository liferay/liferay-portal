/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v0_0_5;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.PortletPreferencesImpl;

import jakarta.portlet.PortletPreferences;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class UpgradeJournalArticles extends BasePortletIdUpgradeProcess {

	public UpgradeJournalArticles(
		AssetCategoryLocalService assetCategoryLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		GroupLocalService groupLocalService,
		LayoutLocalService layoutLocalService,
		PortletPreferenceValueLocalService portletPreferenceValueLocalService,
		PortletPreferencesLocalService portletPreferencesLocalService) {

		_assetCategoryLocalService = assetCategoryLocalService;
		_ddmStructureLocalService = ddmStructureLocalService;
		_groupLocalService = groupLocalService;
		_layoutLocalService = layoutLocalService;
		_portletPreferenceValueLocalService =
			portletPreferenceValueLocalService;
		_portletPreferencesLocalService = portletPreferencesLocalService;
	}

	@Override
	protected String[][] getRenamePortletIdsArray() {
		return new String[][] {
			{_PORTLET_ID_JOURNAL_CONTENT_LIST, _PORTLET_ID_ASSET_PUBLISHER}
		};
	}

	protected long getStructureId(
			long companyId, long groupId, String ddmStructureKey)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			groupId, PortalUtil.getClassNameId(JournalArticle.class.getName()),
			ddmStructureKey);

		if (ddmStructure == null) {
			Group companyGroup = _groupLocalService.getCompanyGroup(companyId);

			_ddmStructureLocalService.fetchStructure(
				companyGroup.getGroupId(),
				PortalUtil.getClassNameId(JournalArticle.class.getName()),
				ddmStructureKey);
		}

		if (ddmStructure != null) {
			return ddmStructure.getStructureId();
		}

		return 0;
	}

	@Override
	protected void updateInstanceablePortletPreferences(
			String oldRootPortletId, String newRootPortletId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select distinct PortletPreferences.portletPreferencesId ",
					"from PortletPreferences inner join ",
					"PortletPreferenceValue on ",
					"PortletPreferenceValue.portletPreferencesId = ",
					"PortletPreferences.portletPreferencesId where portletId ",
					"= '", oldRootPortletId, "' OR portletId like '",
					oldRootPortletId, "_INSTANCE_%' OR portletId like '",
					oldRootPortletId, "_USER_%_INSTANCE_%'"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long portletPreferencesId = resultSet.getLong(
					"portletPreferencesId");

				com.liferay.portal.kernel.model.PortletPreferences
					portletPreferences =
						_portletPreferencesLocalService.getPortletPreferences(
							portletPreferencesId);

				long plid = portletPreferences.getPlid();

				String portletId = portletPreferences.getPortletId();

				long userId = PortletIdCodec.decodeUserId(portletId);
				String instanceId = PortletIdCodec.decodeInstanceId(portletId);

				String newPortletId = PortletIdCodec.encode(
					_PORTLET_ID_ASSET_PUBLISHER, userId, instanceId);

				portletPreferences.setPortletId(newPortletId);

				portletPreferences =
					_portletPreferencesLocalService.updatePortletPreferences(
						portletPreferences);

				PortletPreferences oldPortletPreferences =
					_portletPreferenceValueLocalService.getPreferences(
						portletPreferences);

				_portletPreferencesLocalService.updatePreferences(
					portletPreferences.getOwnerId(),
					portletPreferences.getOwnerType(),
					portletPreferences.getPlid(),
					portletPreferences.getPortletId(),
					_getNewPortletPreferences(
						oldPortletPreferences, plid, oldRootPortletId,
						newRootPortletId));
			}
		}
	}

	@Override
	protected void updatePortlet(
			String oldRootPortletId, String newRootPortletId)
		throws Exception {

		try {
			updateResourcePermission(oldRootPortletId, newRootPortletId, true);

			updateInstanceablePortletPreferences(
				oldRootPortletId, newRootPortletId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private long _getCategoryId(long companyId, String type) throws Exception {
		List<AssetCategory> assetCategories = _assetCategoryLocalService.search(
			companyId, type, new String[0], QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		if (!assetCategories.isEmpty()) {
			AssetCategory assetCategory = assetCategories.get(0);

			return assetCategory.getCategoryId();
		}

		return 0;
	}

	private PortletPreferences _getNewPortletPreferences(
			PortletPreferences oldPortletPreferences, long plid,
			String oldRootPortletId, String newRootPortletId)
		throws Exception {

		PortletPreferences newPortletPreferences = new PortletPreferencesImpl();

		newPortletPreferences.setValue(
			"anyAssetType",
			String.valueOf(
				PortalUtil.getClassNameId(JournalArticle.class.getName())));

		String ddmStructureKey = oldPortletPreferences.getValue(
			"ddmStructureKey", StringPool.BLANK);
		Layout layout = _layoutLocalService.getLayout(plid);

		long structureId = getStructureId(
			layout.getCompanyId(), layout.getGroupId(), ddmStructureKey);

		if (structureId > 0) {
			newPortletPreferences.setValue(
				"anyClassTypeJournalArticleAssetRendererFactory",
				String.valueOf(structureId));
		}

		String assetLinkBehavior = "showFullContent";

		if (StringUtil.equals(
				oldPortletPreferences.getValue("pageUrl", StringPool.BLANK),
				"viewInContext")) {

			assetLinkBehavior = "viewInPortlet";
		}

		newPortletPreferences.setValue("assetLinkBehavior", assetLinkBehavior);

		if (structureId > 0) {
			newPortletPreferences.setValue(
				"classTypeIds", String.valueOf(structureId));
		}

		newPortletPreferences.setValue(
			"delta",
			String.valueOf(
				GetterUtil.getInteger(
					oldPortletPreferences.getValue(
						"pageDelta", StringPool.BLANK))));
		newPortletPreferences.setValue("displayStyle", "table");
		newPortletPreferences.setValue("metadataFields", "publish-date,author");
		newPortletPreferences.setValue(
			"orderByColumn1",
			oldPortletPreferences.getValue("orderByCol", StringPool.BLANK));
		newPortletPreferences.setValue(
			"orderByType1",
			oldPortletPreferences.getValue("orderByType", StringPool.BLANK));
		newPortletPreferences.setValue("paginationType", "none");

		String portletSetupCss = oldPortletPreferences.getValue(
			"portletSetupCss", StringPool.BLANK);

		portletSetupCss = StringUtil.replace(
			portletSetupCss,
			new String[] {
				"#p_p_id_" + oldRootPortletId, "#portlet_" + oldRootPortletId
			},
			new String[] {
				"#p_p_id_" + newRootPortletId, "#portlet_" + newRootPortletId
			});

		newPortletPreferences.setValue("portletSetupCss", portletSetupCss);

		long categoryId = _getCategoryId(
			layout.getCompanyId(),
			oldPortletPreferences.getValue("type", StringPool.BLANK));

		if (categoryId > 0) {
			newPortletPreferences.setValue(
				"queryAndOperator0", Boolean.TRUE.toString());
			newPortletPreferences.setValue(
				"queryContains0", Boolean.TRUE.toString());
			newPortletPreferences.setValue("queryName0", "assetCategories");
			newPortletPreferences.setValue(
				"queryValues0", String.valueOf(categoryId));
		}

		newPortletPreferences.setValue(
			"showAddContentButton", Boolean.FALSE.toString());

		long groupId = GetterUtil.getLong(
			oldPortletPreferences.getValue("groupId", StringPool.BLANK));

		String groupName = String.valueOf(groupId);

		if (groupId == layout.getGroupId()) {
			groupName = "default";
		}

		newPortletPreferences.setValue("scopeIds", "Group_" + groupName);

		return newPortletPreferences;
	}

	private static final String _PORTLET_ID_ASSET_PUBLISHER =
		"com_liferay_asset_publisher_web_AssetPublisherPortlet";

	private static final String _PORTLET_ID_JOURNAL_CONTENT_LIST = "62";

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeJournalArticles.class);

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final GroupLocalService _groupLocalService;
	private final LayoutLocalService _layoutLocalService;
	private final PortletPreferencesLocalService
		_portletPreferencesLocalService;
	private final PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

}