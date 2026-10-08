/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.site.provider;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.commerce.product.util.comparator.CPDefinitionModifiedDateComparator;
import com.liferay.commerce.util.CommerceChannelConfigurationUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalizationTable;
import com.liferay.friendly.url.model.FriendlyURLEntryMappingTable;
import com.liferay.friendly.url.model.FriendlyURLEntryTable;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.provider.SitemapURLProvider;
import com.liferay.site.provider.helper.SitemapURLProviderHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = SitemapURLProvider.class)
public class CPDefinitionSitemapURLProvider implements SitemapURLProvider {

	@Override
	public String getClassName() {
		return CPDefinition.class.getName();
	}

	@Override
	public Date getModifiedDate(long companyId, long groupId)
		throws PortalException {

		long commerceChannelGroupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				groupId);

		if (commerceChannelGroupId <= 0) {
			return null;
		}

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				companyId, AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
				_accountGroupLocalService.getAccountGroupIds(
					AccountConstants.ACCOUNT_ENTRY_ID_GUEST),
				new long[] {commerceChannelGroupId}, true,
				new int[] {WorkflowConstants.STATUS_APPROVED}, 0, 1,
				CPDefinitionModifiedDateComparator.getInstance(false));

		if (cpDefinitions.isEmpty()) {
			return null;
		}

		CPDefinition cpDefinition = cpDefinitions.get(0);

		return cpDefinition.getModifiedDate();
	}

	@Override
	public boolean isInclude(long companyId, long groupId)
		throws PortalException {

		if (!_sitemapConfigurationManager.includeCommerceProductsGroupEnabled(
				companyId, groupId)) {

			return false;
		}

		long commerceChannelGroupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				groupId);

		if (commerceChannelGroupId > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void visitLayout(
			Element element, String layoutUuid, LayoutSet layoutSet,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layoutUuid, layoutSet.getGroupId(), layoutSet.isPrivateLayout());

		if ((layout == null) ||
			!SitemapURLProviderUtil.hasPortletId(
				layout, CPPortletKeys.CP_CONTENT_WEB)) {

			return;
		}

		_visitCPDefinitions(
			element, layout, layoutSet.getGroupId(), themeDisplay);
	}

	@Override
	public void visitLayoutSet(
			Element element, LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = _getLayout(layoutSet);

		if (layout == null) {
			return;
		}

		_visitCPDefinitions(
			element, layout, layoutSet.getGroupId(), themeDisplay);
	}

	@Activate
	protected void activate() {
		_batchSize = 500;
	}

	private Map<Long, FriendlyURLEntry> _getFriendlyURLEntriesMap(
		List<CPDefinition> cpDefinitions) {

		Map<Long, FriendlyURLEntry> friendlyURLEntriesMap = new HashMap<>();

		Long[] cProductIds = TransformUtil.transformToArray(
			cpDefinitions, CPDefinition::getCProductId, Long.class);

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					FriendlyURLEntryTable.INSTANCE
				).from(
					FriendlyURLEntryTable.INSTANCE
				).innerJoinON(
					FriendlyURLEntryMappingTable.INSTANCE,
					FriendlyURLEntryMappingTable.INSTANCE.friendlyURLEntryId.eq(
						FriendlyURLEntryTable.INSTANCE.friendlyURLEntryId)
				).where(
					FriendlyURLEntryTable.INSTANCE.classNameId.eq(
						_portal.getClassNameId(CProduct.class)
					).and(
						FriendlyURLEntryTable.INSTANCE.classPK.in(cProductIds)
					)
				));

		for (FriendlyURLEntry friendlyURLEntry : friendlyURLEntries) {
			friendlyURLEntriesMap.put(
				friendlyURLEntry.getClassPK(), friendlyURLEntry);
		}

		return friendlyURLEntriesMap;
	}

	private Map<Long, List<String>> _getLanguageIdsMap(
		Collection<FriendlyURLEntry> friendlyURLEntries) {

		Map<Long, List<String>> languageIdsMap = new HashMap<>();

		if (friendlyURLEntries.isEmpty()) {
			return languageIdsMap;
		}

		Long[] friendlyURLEntryIds = TransformUtil.transformToArray(
			friendlyURLEntries, FriendlyURLEntry::getFriendlyURLEntryId,
			Long.class);

		List<Object[]> rows = _friendlyURLEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				FriendlyURLEntryLocalizationTable.INSTANCE.friendlyURLEntryId,
				FriendlyURLEntryLocalizationTable.INSTANCE.languageId
			).from(
				FriendlyURLEntryLocalizationTable.INSTANCE
			).where(
				FriendlyURLEntryLocalizationTable.INSTANCE.friendlyURLEntryId.
					in(friendlyURLEntryIds)
			));

		for (Object[] row : rows) {
			List<String> languageIds = languageIdsMap.computeIfAbsent(
				(Long)row[0], friendlyURLEntryId -> new ArrayList<>());

			languageIds.add((String)row[1]);
		}

		return languageIdsMap;
	}

	private Layout _getLayout(LayoutSet layoutSet) throws PortalException {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchDefaultLayoutPageTemplateEntry(
					layoutSet.getGroupId(),
					_portal.getClassNameId(CPDefinition.class), 0);

		if ((layoutPageTemplateEntry != null) &&
			layoutPageTemplateEntry.isDefaultTemplate()) {

			Layout layout = _layoutLocalService.fetchLayout(
				layoutPageTemplateEntry.getPlid());

			if ((layout != null) && layout.isTypeAssetDisplay()) {
				return layout;
			}
		}

		return _layoutLocalService.fetchLayout(
			_portal.getPlidFromPortletId(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				CPPortletKeys.CP_CONTENT_WEB));
	}

	private void _visitCPDefinition(
			Set<Locale> availableLocales, String currentSiteURL,
			Element element, FriendlyURLEntry friendlyURLEntry,
			List<String> languageIds, Layout layout, Date modifiedDate,
			ThemeDisplay themeDisplay,
			UnicodeProperties typeSettingsUnicodeProperties,
			String urlSeparator)
		throws PortalException {

		Map<Locale, String> alternateFriendlyURLs =
			SitemapURLProviderUtil.getAlternateFriendlyURLs(
				_portal.getAlternateURLs(
					StringBundler.concat(
						currentSiteURL, urlSeparator,
						friendlyURLEntry.getUrlTitle()),
					themeDisplay, layout, availableLocales),
				languageIds);

		String productFriendlyURL = alternateFriendlyURLs.get(
			_portal.getLocale(themeDisplay.getRequest()));

		for (String alternateFriendlyURL : alternateFriendlyURLs.values()) {
			_sitemapManager.addURLElement(
				element, alternateFriendlyURL, typeSettingsUnicodeProperties,
				modifiedDate, productFriendlyURL, alternateFriendlyURLs,
				layout.getGroupId());
		}
	}

	private void _visitCPDefinitions(
			Element element, Layout layout, long siteGroupId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		long commerceChannelGroupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				siteGroupId);

		if ((commerceChannelGroupId <= 0) ||
			!CommerceChannelConfigurationUtil.isGuestBrowsingEnabled(
				commerceChannelGroupId) ||
			(layout.isSystem() && !layout.isTypeAssetDisplay()) ||
			_sitemapURLProviderHelper.isExcludeLayoutFromSitemap(layout)) {

			return;
		}

		long[] accountGroupIds = _accountGroupLocalService.getAccountGroupIds(
			AccountConstants.ACCOUNT_ENTRY_ID_GUEST);
		OrderByComparator<CPDefinition> orderByComparator =
			OrderByComparatorFactoryUtil.create(
				"CPDefinition", "CPDefinitionId", true);
		int start = 0;

		Set<Locale> availableLocales = _language.getAvailableLocales(
			layout.getGroupId());
		String currentSiteURL = _portal.getGroupFriendlyURL(
			layout.getLayoutSet(), themeDisplay, false, false);
		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();
		String urlSeparator = _cpFriendlyURL.getProductURLSeparator(
			themeDisplay.getCompanyId());

		while (true) {
			List<CPDefinition> cpDefinitions =
				_cpDefinitionLocalService.getCPDefinitions(
					themeDisplay.getCompanyId(),
					AccountConstants.ACCOUNT_ENTRY_ID_GUEST, accountGroupIds,
					new long[] {commerceChannelGroupId}, true,
					new int[] {WorkflowConstants.STATUS_APPROVED}, start,
					start + _batchSize, orderByComparator);

			if (cpDefinitions.isEmpty()) {
				return;
			}

			Map<Long, FriendlyURLEntry> friendlyURLEntriesMap =
				_getFriendlyURLEntriesMap(cpDefinitions);

			Map<Long, List<String>> languageIdsMap = _getLanguageIdsMap(
				friendlyURLEntriesMap.values());

			for (CPDefinition cpDefinition : cpDefinitions) {
				FriendlyURLEntry friendlyURLEntry = friendlyURLEntriesMap.get(
					cpDefinition.getCProductId());

				if (friendlyURLEntry == null) {
					continue;
				}

				_visitCPDefinition(
					availableLocales, currentSiteURL, element, friendlyURLEntry,
					languageIdsMap.get(
						friendlyURLEntry.getFriendlyURLEntryId()),
					layout, cpDefinition.getModifiedDate(), themeDisplay,
					typeSettingsUnicodeProperties, urlSeparator);
			}

			if (cpDefinitions.size() < _batchSize) {
				return;
			}

			start += _batchSize;
		}
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	private int _batchSize;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPFriendlyURL _cpFriendlyURL;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private SitemapURLProviderHelper _sitemapURLProviderHelper;

}