/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.site.provider;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
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

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(service = SitemapURLProvider.class)
public class ObjectEntrySitemapURLProvider implements SitemapURLProvider {

	@Override
	public String getClassName() {
		return ObjectEntry.class.getName();
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

		if ((objectDefinition == null) ||
			!_sitemapConfigurationManager.isObjectDefinitionCompanyIncluded(
				themeDisplay.getCompanyId(),
				String.valueOf(objectDefinition.getObjectDefinitionId()))) {

			return;
		}

		_visitObjectEntries(
			element, layout, layoutSet, objectDefinition, themeDisplay);
	}

	@Override
	public void visitLayoutSet(
			Element element, LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		Long[] objectDefinitionIds =
			_sitemapConfigurationManager.getCompanySitemapObjectDefinitionIds(
				themeDisplay.getCompanyId());

		for (long objectDefinitionId : objectDefinitionIds) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectDefinitionId);

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
				element, layout, layoutSet, objectDefinition, themeDisplay);
		}
	}

	private List<ObjectEntry> _getApprovedObjectEntries(
		long groupId, ObjectDefinition objectDefinition) {

		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			return _objectEntryLocalService.getObjectEntries(
				GroupConstants.DEFAULT_PARENT_GROUP_ID,
				objectDefinition.getObjectDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);
		}

		return _objectEntryLocalService.getObjectEntries(
			groupId, objectDefinition.getObjectDefinitionId(),
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);
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
		String languageId, ObjectDefinition objectDefinition,
		ObjectEntry objectEntry) {

		String urlTitle = objectEntry.getURLTitle(
			LocaleUtil.fromLanguageId(languageId));

		if (Validator.isNotNull(urlTitle)) {
			return urlTitle;
		}

		if (!objectDefinition.isDefaultStorageType()) {
			return objectEntry.getExternalReferenceCode();
		}

		return String.valueOf(objectEntry.getObjectEntryId());
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

	private void _visitObjectEntries(
			Element element, Layout layout, LayoutSet layoutSet,
			ObjectDefinition objectDefinition, ThemeDisplay themeDisplay)
		throws PortalException {

		List<ObjectEntry> objectEntries = _getApprovedObjectEntries(
			layoutSet.getGroupId(), objectDefinition);

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

		for (ObjectEntry objectEntry : objectEntries) {
			String friendlyURL = _getFriendlyURL(
				themeDisplay.getLanguageId(), objectDefinition, objectEntry);

			String canonicalURL = _portal.getCanonicalURL(
				urlSeparator + friendlyURL, themeDisplay, layout);

			Map<Locale, String> alternateURLs = _portal.getAlternateURLs(
				canonicalURL, themeDisplay, layout,
				objectDefinitionAvailableLocales);

			for (String alternateURL : alternateURLs.values()) {
				_sitemapManager.addURLElement(
					element, alternateURL, typeSettingsUnicodeProperties,
					objectEntry.getModifiedDate(), canonicalURL, alternateURLs);
			}
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

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
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private SitemapURLProviderHelper _sitemapURLProviderHelper;

}