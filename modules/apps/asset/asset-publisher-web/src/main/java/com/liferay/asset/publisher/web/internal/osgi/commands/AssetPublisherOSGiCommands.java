/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.osgi.commands;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import org.apache.felix.service.command.Descriptor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = {
		"osgi.command.function=migratePortletPreferences",
		"osgi.command.scope=assetPublisher"
	},
	service = OSGiCommands.class
)
public class AssetPublisherOSGiCommands implements OSGiCommands {

	@Descriptor("Create a collection from manual and dynamic asset publishers")
	public void migratePortletPreferences() throws PortalException {
		_companyLocalService.forEachCompanyId(this::_migratePortletPreferences);
	}

	private AssetListEntry _addDynamicAssetListEntry(
			String instanceId, Layout layout,
			jakarta.portlet.PortletPreferences jxPortletPreferences,
			long scopeGroupId, ServiceContext serviceContext, long userId)
		throws Exception {

		String name = layout.getName(LocaleUtil.getDefault());

		return _assetListEntryLocalService.addDynamicAssetListEntry(
			null, userId, scopeGroupId,
			_getTitle(
				layout.isDraftLayout(), instanceId,
				name.substring(0, Math.min(name.length(), 60))),
			_getTypeSettings(layout, jxPortletPreferences), serviceContext);
	}

	private AssetListEntry _getAssetListEntry(
			long companyId, Layout layout, String portletId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws Exception {

		String selectionStyle = jxPortletPreferences.getValue(
			"selectionStyle", "dynamic");

		String instanceId = PortletIdCodec.decodeInstanceId(portletId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		long scopeGroupId = _getScopeGroupId(layout);

		serviceContext.setScopeGroupId(scopeGroupId);

		long userId = layout.getUserId();

		serviceContext.setUserId(userId);

		if (Objects.equals(
				selectionStyle,
				AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC)) {

			return _addDynamicAssetListEntry(
				instanceId, layout, jxPortletPreferences, scopeGroupId,
				serviceContext, userId);
		}

		if (!Objects.equals(
				selectionStyle,
				AssetPublisherSelectionStyleConstants.TYPE_MANUAL)) {

			return null;
		}

		String name = layout.getName(LocaleUtil.getDefault());

		return _assetListEntryLocalService.addManualAssetListEntry(
			null, userId, scopeGroupId,
			_getTitle(
				layout.isDraftLayout(), instanceId,
				name.substring(0, Math.min(name.length(), 60))),
			ListUtil.toLongArray(
				_assetPublisherHelper.getAssetEntries(
					null, jxPortletPreferences, null, companyId,
					_assetPublisherHelper.getGroupIds(
						jxPortletPreferences, scopeGroupId, layout),
					false, true, false,
					AssetRendererFactory.TYPE_LATEST_APPROVED),
				AssetEntry::getEntryId),
			serviceContext);
	}

	private long _getScopeGroupId(Layout layout) {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if ((layoutPageTemplateEntry != null) &&
			(layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			return layoutPageTemplateEntry.getGroupId();
		}

		return layout.getGroupId();
	}

	private String _getTitle(boolean draft, String instanceId, String name) {
		return StringBundler.concat(
			"AP ", instanceId, draft ? "_0" : "_1", StringPool.SPACE, name);
	}

	private String _getTypeSettings(
		Layout layout,
		jakarta.portlet.PortletPreferences jxPortletPreferences) {

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		Enumeration<String> enumeration = jxPortletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			String value = StringUtil.merge(
				jxPortletPreferences.getValues(name, null));

			if (Validator.isNull(value) || name.contains("email")) {
				continue;
			}

			if (!name.equals("scopeIds")) {
				unicodeProperties.put(name, value);

				continue;
			}

			List<Long> groupIds = TransformUtil.transformToList(
				value.split(StringPool.COMMA),
				part -> {
					if (part.equals("Group_default")) {
						return layout.getGroupId();
					}

					if (!part.startsWith("Group_")) {
						return null;
					}

					long groupId = GetterUtil.getLong(
						StringUtil.removeSubstring(part, "Group_"), -1);

					if (groupId != -1) {
						return groupId;
					}

					return null;
				});

			if (groupIds.isEmpty()) {
				continue;
			}

			name = "groupIds";
			value = ListUtil.toString(groupIds, StringPool.BLANK);

			unicodeProperties.put(name, value);
		}

		if (Validator.isNull(unicodeProperties.getProperty("anyAssetType"))) {
			unicodeProperties.put("anyAssetType", "true");
		}

		return unicodeProperties.toString();
	}

	private void _migratePortletPreferences(Long companyId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_portletPreferencesLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property property = PropertyFactoryUtil.forName("portletId");

				dynamicQuery.add(
					property.like(
						PortletIdCodec.encode(
							AssetPublisherPortletKeys.ASSET_PUBLISHER,
							StringPool.PERCENT)));

				dynamicQuery.add(
					RestrictionsFactoryUtil.eq("companyId", companyId));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(PortletPreferences portletPreferences) ->
				_migratePortletPreferences(portletPreferences));

		actionableDynamicQuery.performActions();
	}

	private void _migratePortletPreferences(
		PortletPreferences portletPreferences) {

		try {
			jakarta.portlet.PortletPreferences jxPortletPreferences =
				_portletPreferenceValueLocalService.getPreferences(
					portletPreferences);

			Layout layout = _layoutLocalService.fetchLayout(
				portletPreferences.getPlid());

			if (layout == null) {
				return;
			}

			AssetListEntry assetListEntry = _getAssetListEntry(
				portletPreferences.getCompanyId(), layout,
				portletPreferences.getPortletId(), jxPortletPreferences);

			if (assetListEntry != null) {
				jxPortletPreferences.setValue(
					"assetListEntryExternalReferenceCode",
					assetListEntry.getExternalReferenceCode());

				if (assetListEntry.getGroupId() != layout.getGroupId()) {
					Group group = _groupLocalService.getGroup(
						assetListEntry.getGroupId());

					jxPortletPreferences.setValue(
						"assetListEntryGroupExternalReferenceCode",
						group.getExternalReferenceCode());
				}

				jxPortletPreferences.setValue(
					"selectionStyle",
					AssetPublisherSelectionStyleConstants.TYPE_ASSET_LIST);

				jxPortletPreferences.store();
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetPublisherOSGiCommands.class);

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private AssetPublisherHelper _assetPublisherHelper;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}