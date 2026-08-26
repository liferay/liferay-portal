/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.site.provider;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.definition.setting.util.ObjectDefinitionSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.provider.SitemapURLProvider;
import com.liferay.site.provider.helper.SitemapURLProviderHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(service = SitemapURLProvider.class)
public class ObjectEntrySitemapURLProvider implements SitemapURLProvider {

	@Override
	public String getClassName() {
		return ObjectEntry.class.getName();
	}

	@Override
	public Date getModifiedDate(long companyId, long groupId)
		throws PortalException {

		Date modifiedDate = null;

		List<Long> companyObjectDefinitionIds = new ArrayList<>();
		List<Long> siteObjectDefinitionIds = new ArrayList<>();

		for (ObjectDefinition objectDefinition :
				_sitemapConfigurationManager.getCompanySitemapObjectDefinitions(
					companyId)) {

			if (Objects.equals(
					objectDefinition.getScope(),
					ObjectDefinitionConstants.SCOPE_COMPANY)) {

				companyObjectDefinitionIds.add(
					objectDefinition.getObjectDefinitionId());
			}
			else {
				siteObjectDefinitionIds.add(
					objectDefinition.getObjectDefinitionId());
			}
		}

		if (!siteObjectDefinitionIds.isEmpty()) {
			modifiedDate = _getLatestModifiedDate(
				_getGroupIds(groupId),
				siteObjectDefinitionIds.toArray(new Long[0]));
		}

		if (!companyObjectDefinitionIds.isEmpty()) {
			Date companyDate = _getLatestModifiedDate(
				new long[] {GroupConstants.DEFAULT_PARENT_GROUP_ID},
				companyObjectDefinitionIds.toArray(new Long[0]));

			if ((companyDate != null) &&
				((modifiedDate == null) || companyDate.after(modifiedDate))) {

				modifiedDate = companyDate;
			}
		}

		return modifiedDate;
	}

	@Override
	public boolean isInclude(long companyId, long groupId)
		throws PortalException {

		return ListUtil.isNotEmpty(
			_sitemapConfigurationManager.getCompanySitemapObjectDefinitions(
				companyId));
	}

	@Override
	public void visitLayout(
			Element element, String layoutUuid, LayoutSet layoutSet,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layoutUuid, layoutSet.getGroupId(), layoutSet.isPrivateLayout());

		if ((layout == null) || !layout.isTypeAssetDisplay() ||
			_sitemapURLProviderHelper.isExcludeLayoutFromSitemap(layout)) {

			return;
		}

		ObjectDefinition objectDefinition =
			_getObjectDefinitionFromLayoutPageTemplateEntry(
				themeDisplay.getCompanyId(), layout);

		if ((objectDefinition == null) || !objectDefinition.isActive() ||
			!_sitemapConfigurationManager.isObjectDefinitionCompanyIncluded(
				themeDisplay.getCompanyId(),
				String.valueOf(objectDefinition.getObjectDefinitionId())) ||
			!ObjectDefinitionSettingUtil.isSitemapable(
				objectDefinition,
				_getObjectDefinitionSettingsMap(themeDisplay.getCompanyId()))) {

			return;
		}

		_visitObjectEntries(
			element, _getGroupIds(layoutSet.getGroupId()), layout,
			objectDefinition, themeDisplay);
	}

	@Override
	public void visitLayoutSet(
			Element element, LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		long[] groupIds = _getGroupIds(layoutSet.getGroupId());

		for (ObjectDefinition objectDefinition :
				_sitemapConfigurationManager.getCompanySitemapObjectDefinitions(
					themeDisplay.getCompanyId())) {

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchDefaultLayoutPageTemplateEntry(
						layoutSet.getGroupId(),
						_classNameLocalService.getClassNameId(
							objectDefinition.getClassName()),
						0);

			if ((layoutPageTemplateEntry == null) ||
				!layoutPageTemplateEntry.isDefaultTemplate()) {

				continue;
			}

			Layout layout = _layoutLocalService.fetchLayout(
				layoutPageTemplateEntry.getPlid());

			if ((layout == null) || !layout.isTypeAssetDisplay() ||
				_sitemapURLProviderHelper.isExcludeLayoutFromSitemap(layout)) {

				continue;
			}

			_visitObjectEntries(
				element, groupIds, layout, objectDefinition, themeDisplay);
		}
	}

	private List<ObjectEntry> _getApprovedObjectEntries(
			long[] groupIds, ObjectDefinition objectDefinition)
		throws PortalException {

		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			return _objectEntryService.getObjectEntries(
				GroupConstants.DEFAULT_PARENT_GROUP_ID,
				objectDefinition.getObjectDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);
		}

		List<ObjectEntry> objectEntries = new ArrayList<>();

		for (long groupId : groupIds) {
			objectEntries.addAll(
				_objectEntryService.getObjectEntries(
					groupId, objectDefinition.getObjectDefinitionId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS));
		}

		return objectEntries;
	}

	private Set<Locale> _getAvailableLocales(
		ObjectDefinition objectDefinition, Set<Locale> siteAvailableLocales) {

		Set<Locale> availableLocales = new HashSet<>();

		if (SetUtil.isEmpty(siteAvailableLocales)) {
			return availableLocales;
		}

		for (String availableLanguageId :
				objectDefinition.getAvailableLanguageIds()) {

			Locale locale = LocaleUtil.fromLanguageId(availableLanguageId);

			if (siteAvailableLocales.contains(locale)) {
				availableLocales.add(locale);
			}
		}

		return availableLocales;
	}

	private String _getFriendlyURL(
		boolean cms, long groupId, String languageId,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		String urlTitle = objectEntry.getURLTitle(
			LocaleUtil.fromLanguageId(languageId));

		if (Validator.isNull(urlTitle)) {
			if (!objectDefinition.isDefaultStorageType()) {
				return objectEntry.getExternalReferenceCode();
			}

			return String.valueOf(objectEntry.getObjectEntryId());
		}

		if (!cms || (groupId == objectEntry.getGroupId())) {
			return urlTitle;
		}

		Group group = _groupLocalService.fetchGroup(objectEntry.getGroupId());

		if (group == null) {
			return urlTitle;
		}

		return StringBundler.concat(
			StringUtil.removeFirst(group.getFriendlyURL(), StringPool.SLASH),
			StringPool.SLASH, urlTitle);
	}

	private long[] _getGroupIds(long groupId) throws PortalException {
		return ArrayUtil.append(
			new long[] {groupId},
			ListUtil.toLongArray(
				_depotEntryLocalService.getGroupConnectedDepotEntries(
					groupId, DepotConstants.TYPE_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
				DepotEntry::getGroupId));
	}

	private Date _getLatestModifiedDate(
		long[] groupIds, Long[] objectDefinitionIds) {

		if (ArrayUtil.isEmpty(groupIds)) {
			return null;
		}

		List<Date> modifiedDates = _objectEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectEntryTable.INSTANCE.modifiedDate
			).from(
				ObjectEntryTable.INSTANCE
			).where(
				ObjectEntryTable.INSTANCE.groupId.in(
					ArrayUtil.toArray(groupIds)
				).and(
					ObjectEntryTable.INSTANCE.objectDefinitionId.in(
						objectDefinitionIds)
				).and(
					ObjectEntryTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_APPROVED)
				).and(
					ObjectEntryTable.INSTANCE.modifiedDate.isNotNull()
				)
			).orderBy(
				ObjectEntryTable.INSTANCE.modifiedDate.descending()
			).limit(
				0, 1
			));

		if (modifiedDates.isEmpty()) {
			return null;
		}

		return modifiedDates.get(0);
	}

	private ObjectDefinition _getObjectDefinitionFromLayoutPageTemplateEntry(
		long companyId, Layout layout) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if ((layoutPageTemplateEntry == null) ||
			!layoutPageTemplateEntry.isDefaultTemplate()) {

			return null;
		}

		return _objectDefinitionLocalService.fetchObjectDefinitionByClassName(
			companyId,
			_portal.getClassName(layoutPageTemplateEntry.getClassNameId()));
	}

	private Map<Long, ObjectDefinitionSetting> _getObjectDefinitionSettingsMap(
		long companyId) {

		return _objectDefinitionSettingLocalService.
			getObjectDefinitionSettingsMap(
				companyId, ObjectDefinitionSettingConstants.NAME_SITEMAPABLE);
	}

	private void _visitObjectEntries(
			Element element, long[] groupIds, Layout layout,
			ObjectDefinition objectDefinition, ThemeDisplay themeDisplay)
		throws PortalException {

		List<ObjectEntry> objectEntries = _getApprovedObjectEntries(
			groupIds, objectDefinition);

		if (objectEntries.isEmpty()) {
			return;
		}

		Set<Locale> objectDefinitionAvailableLocales = _getAvailableLocales(
			objectDefinition,
			_language.getAvailableLocales(themeDisplay.getScopeGroupId()));

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String urlSeparator = StringUtil.quote(
			objectDefinition.getFriendlyURLSeparator(), CharPool.SLASH);

		try (SafeCloseable safeCloseable =
				GroupThreadLocal.setGroupIdWithSafeCloseable(
					layout.getGroupId())) {

			for (ObjectEntry objectEntry : objectEntries) {
				String friendlyURL = _getFriendlyURL(
					objectDefinition.isCMS(), layout.getGroupId(),
					themeDisplay.getLanguageId(), objectDefinition,
					objectEntry);

				String canonicalURL = _portal.getCanonicalURL(
					urlSeparator + friendlyURL, themeDisplay, layout);

				Map<Locale, String> alternateURLs = _portal.getAlternateURLs(
					canonicalURL, themeDisplay, layout,
					objectDefinitionAvailableLocales);

				for (String alternateURL : alternateURLs.values()) {
					_sitemapManager.addURLElement(
						element, alternateURL, typeSettingsUnicodeProperties,
						objectEntry.getModifiedDate(), canonicalURL,
						alternateURLs, layout.getGroupId());
				}
			}
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private SitemapURLProviderHelper _sitemapURLProviderHelper;

}