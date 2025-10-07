/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.site.provider;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.internal.util.JournalUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.provider.SitemapURLProvider;
import com.liferay.site.provider.helper.SitemapURLProviderHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = SitemapURLProvider.class)
public class JournalArticleSitemapURLProvider implements SitemapURLProvider {

	@Override
	public String getClassName() {
		return JournalArticle.class.getName();
	}

	@Override
	public boolean isInclude(long companyId, long groupId)
		throws PortalException {

		return _sitemapConfigurationManager.includeWebContentGroupEnabled(
			companyId, groupId);
	}

	@Override
	public void visitLayout(
			Element element, String layoutUuid, LayoutSet layoutSet,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layoutUuid, layoutSet.getGroupId(), layoutSet.isPrivateLayout());

		if ((layout == null) ||
			(layout.isSystem() && !layout.isTypeAssetDisplay())) {

			return;
		}

		if (layout.isTypeAssetDisplay()) {
			visitArticles(
				element, layout, layoutSet, themeDisplay,
				getDisplayPageTemplateArticles(layout), false);
		}
		else {
			visitArticles(
				element, null, layoutSet, themeDisplay,
				_getDisplayPageArticles(layoutSet.getGroupId(), layoutUuid),
				true);
		}
	}

	@Override
	public void visitLayoutSet(
			Element element, LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		int start = QueryUtil.ALL_POS;
		int end = QueryUtil.ALL_POS;

		int count = _journalArticleService.getLayoutArticlesCount(
			layoutSet.getGroupId());

		if (count > SitemapManager.MAXIMUM_ENTRIES) {
			start = count - SitemapManager.MAXIMUM_ENTRIES;
			end = count;
		}

		List<JournalArticle> journalArticles =
			_journalArticleService.getLayoutArticles(
				layoutSet.getGroupId(), start, end);

		visitArticles(
			element, null, layoutSet, themeDisplay, journalArticles, true);
	}

	protected List<JournalArticle> getDisplayPageTemplateArticles(Layout layout)
		throws PortalException {

		List<JournalArticle> journalArticles = new ArrayList<>();

		if (layout == null) {
			return journalArticles;
		}

		DynamicQuery assetDisplayPageEntryDynamicQuery =
			_assetDisplayPageEntryLocalService.dynamicQuery();

		long classNameId = _portal.getClassNameId(
			JournalArticle.class.getName());

		Property classNameIdProperty = PropertyFactoryUtil.forName(
			"classNameId");

		assetDisplayPageEntryDynamicQuery.add(
			classNameIdProperty.eq(classNameId));

		Property layoutPageTemplateEntryIdProperty =
			PropertyFactoryUtil.forName("layoutPageTemplateEntryId");

		assetDisplayPageEntryDynamicQuery.add(
			layoutPageTemplateEntryIdProperty.ne(Long.valueOf(0)));

		Property plidProperty = PropertyFactoryUtil.forName("plid");

		assetDisplayPageEntryDynamicQuery.add(
			plidProperty.eq(layout.getPlid()));

		assetDisplayPageEntryDynamicQuery.setProjection(
			PropertyFactoryUtil.forName("classPK"));

		List<Long> resourcePrimKeys =
			_assetDisplayPageEntryLocalService.dynamicQuery(
				assetDisplayPageEntryDynamicQuery);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if ((layoutPageTemplateEntry != null) &&
			layoutPageTemplateEntry.isDefaultTemplate()) {

			resourcePrimKeys = new ArrayList<>(resourcePrimKeys);

			for (long groupId :
					_siteConnectedGroupGroupProvider.
						getCurrentAndAncestorSiteAndDepotGroupIds(
							layout.getGroupId())) {

				if (groupId == layout.getGroupId()) {
					resourcePrimKeys.addAll(
						_journalArticleLocalService.
							getArticlesClassPKsWithDefaultDisplayPage(
								groupId,
								layoutPageTemplateEntry.getClassTypeId()));
				}
				else {
					resourcePrimKeys.addAll(
						TransformUtil.transform(
							_journalArticleResourceLocalService.
								getArticleResources(groupId),
							JournalArticleResource::getResourcePrimKey));
				}
			}
		}

		for (Long resourcePrimKey : resourcePrimKeys) {
			try {
				JournalArticle journalArticle =
					_journalArticleService.getLatestArticle(resourcePrimKey);

				if (journalArticle.isIndexable()) {
					journalArticles.add(journalArticle);
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return journalArticles;
	}

	protected Layout getDisplayPageTemplateLayout(
		long groupId, long journalArticleResourcePrimKey,
		DDMStructure ddmStructure) {

		long classNameId = _portal.getClassNameId(
			JournalArticle.class.getName());

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				groupId, classNameId, journalArticleResourcePrimKey);

		if (assetDisplayPageEntry == null) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchDefaultLayoutPageTemplateEntry(
						groupId, classNameId, ddmStructure.getStructureId());

			if (layoutPageTemplateEntry == null) {
				return null;
			}

			return _layoutLocalService.fetchLayout(
				layoutPageTemplateEntry.getPlid());
		}

		if (assetDisplayPageEntry.getType() ==
				AssetDisplayPageConstants.TYPE_NONE) {

			return null;
		}

		long assetDisplayPageEntryPlid = assetDisplayPageEntry.getPlid();

		return _layoutLocalService.fetchLayout(assetDisplayPageEntryPlid);
	}

	protected void visitArticles(
			Element element, Layout layout, LayoutSet layoutSet,
			ThemeDisplay themeDisplay, List<JournalArticle> journalArticles,
			boolean headCheck)
		throws PortalException {

		if (journalArticles.isEmpty()) {
			return;
		}

		String portalURL = _portal.getPortalURL(layoutSet, themeDisplay);
		Set<String> processedArticleIds = new HashSet<>();
		Set<Locale> siteAvailableLocales = _language.getAvailableLocales(
			themeDisplay.getScopeGroupId());

		for (JournalArticle journalArticle : journalArticles) {
			if (processedArticleIds.contains(journalArticle.getArticleId()) ||
				(journalArticle.getStatus() !=
					WorkflowConstants.STATUS_APPROVED) ||
				(headCheck && !JournalUtil.isHead(journalArticle))) {

				continue;
			}

			Layout articleLayout = layout;

			if ((articleLayout == null) &&
				Validator.isNotNull(journalArticle.getLayoutUuid())) {

				articleLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
					journalArticle.getLayoutUuid(), layoutSet.getGroupId(),
					layoutSet.isPrivateLayout());
			}
			else if (articleLayout == null) {
				articleLayout = getDisplayPageTemplateLayout(
					layoutSet.getGroupId(), journalArticle.getResourcePrimKey(),
					journalArticle.getDDMStructure());
			}

			if (_sitemapURLProviderHelper.isExcludeLayoutFromSitemap(
					articleLayout)) {

				continue;
			}

			String groupFriendlyURL = _portal.getGroupFriendlyURL(
				_layoutSetLocalService.getLayoutSet(
					journalArticle.getGroupId(), false),
				themeDisplay, false, false);

			StringBundler sb = new StringBundler(4);

			if (!groupFriendlyURL.startsWith(portalURL)) {
				sb.append(portalURL);
			}

			sb.append(groupFriendlyURL);

			if (Validator.isNotNull(journalArticle.getLayoutUuid())) {
				sb.append(JournalArticleConstants.CANONICAL_URL_SEPARATOR);
			}
			else {
				sb.append(_getFriendlyURLSeparator());
			}

			sb.append(journalArticle.getUrlTitle());

			String articleURL = _portal.getCanonicalURL(
				sb.toString(), themeDisplay, articleLayout);

			Map<Locale, String> alternateURLs = _portal.getAlternateURLs(
				articleURL, themeDisplay, articleLayout,
				_getAvailableLocales(journalArticle, siteAvailableLocales));

			for (String alternateURL : alternateURLs.values()) {
				_sitemapManager.addURLElement(
					element, alternateURL, null,
					journalArticle.getModifiedDate(), articleURL,
					alternateURLs);
			}

			processedArticleIds.add(journalArticle.getArticleId());
		}
	}

	private Set<Locale> _getAvailableLocales(
		JournalArticle journalArticle, Set<Locale> siteAvailableLocales) {

		Set<Locale> availableLocales = new HashSet<>();

		if (SetUtil.isEmpty(siteAvailableLocales)) {
			return availableLocales;
		}

		for (String availableLanguageId :
				journalArticle.getAvailableLanguageIds()) {

			Locale locale = LocaleUtil.fromLanguageId(availableLanguageId);

			if (siteAvailableLocales.contains(locale)) {
				availableLocales.add(locale);
			}
		}

		return availableLocales;
	}

	private List<JournalArticle> _getDisplayPageArticles(
		long groupId, String layoutUuid) {

		int start = QueryUtil.ALL_POS;
		int end = QueryUtil.ALL_POS;

		int count = _journalArticleService.getArticlesByLayoutUuidCount(
			groupId, layoutUuid);

		if (count > SitemapManager.MAXIMUM_ENTRIES) {
			start = count - SitemapManager.MAXIMUM_ENTRIES;
			end = count;
		}

		return _journalArticleService.getArticlesByLayoutUuid(
			groupId, layoutUuid, start, end);
	}

	private String _getFriendlyURLSeparator() {
		FriendlyURLResolver friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.
				getFriendlyURLResolverByDefaultURLSeparator(
					FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE);

		if (friendlyURLResolver != null) {
			return friendlyURLResolver.getURLSeparator();
		}

		return FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleSitemapURLProvider.class);

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private SitemapURLProviderHelper _sitemapURLProviderHelper;

}