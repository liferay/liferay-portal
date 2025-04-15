/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.asset.display.page.portlet;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.portlet.BaseAssetDisplayPageFriendlyURLResolver;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.permission.WorkflowPermissionUtil;

import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = FriendlyURLResolver.class)
public class DefaultAssetDisplayPageFriendlyURLResolver
	extends BaseAssetDisplayPageFriendlyURLResolver {

	@Override
	public String getActualURL(
			long companyId, long groupId, boolean privateLayout,
			String mainPath, String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)requestContext.get("request");

		JournalArticle journalArticle = _getJournalArticle(
			groupId, friendlyURL, params);

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_getLayoutDisplayPageObjectProvider(journalArticle);

		if (Validator.isNull(_getLayoutUuid(journalArticle)) &&
			(layoutDisplayPageObjectProvider != null) &&
			AssetDisplayPageUtil.hasAssetDisplayPage(
				groupId, layoutDisplayPageObjectProvider.getClassNameId(),
				layoutDisplayPageObjectProvider.getClassPK(),
				layoutDisplayPageObjectProvider.getClassTypeId())) {

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setCompany(_companyLocalService.getCompany(companyId));

			String portalURL = _portal.getPortalURL(httpServletRequest);

			themeDisplay.setPortalDomain(
				HttpComponentsUtil.getDomain(portalURL));
			themeDisplay.setPortalURL(portalURL);

			themeDisplay.setScopeGroupId(groupId);
			themeDisplay.setSiteGroupId(groupId);

			String assetFriendlyURL =
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					new InfoItemReference(
						layoutDisplayPageObjectProvider.getClassName(),
						new ClassPKInfoItemIdentifier(
							layoutDisplayPageObjectProvider.getClassPK())),
					_portal.getLocale(httpServletRequest), themeDisplay);

			if (Validator.isNotNull(assetFriendlyURL)) {
				return assetFriendlyURL;
			}

			return super.getActualURL(
				companyId, groupId, privateLayout, mainPath, friendlyURL,
				params, requestContext);
		}

		return _getBasicLayoutURL(
			groupId, privateLayout, mainPath, friendlyURL, requestContext,
			journalArticle.getUrlTitle(_portal.getLocale(httpServletRequest)),
			journalArticle);
	}

	@Override
	public LayoutFriendlyURLComposite getLayoutFriendlyURLComposite(
			long companyId, long groupId, boolean privateLayout,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		JournalArticle journalArticle = _getJournalArticle(
			groupId, friendlyURL, params);

		Locale locale = getLocale(requestContext);

		if (locale != null) {
			Map<Locale, String> friendlyURLMap =
				journalArticle.getFriendlyURLMap();

			String journalArticleFriendlyURL = friendlyURLMap.get(locale);

			if (Validator.isNotNull(journalArticleFriendlyURL)) {
				friendlyURL = getURLSeparator() + journalArticleFriendlyURL;
			}
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_getLayoutDisplayPageObjectProvider(journalArticle);

		String layoutUuid = _getLayoutUuid(journalArticle);

		if (Validator.isNull(layoutUuid) &&
			(layoutDisplayPageObjectProvider != null) &&
			AssetDisplayPageUtil.hasAssetDisplayPage(
				groupId, layoutDisplayPageObjectProvider.getClassNameId(),
				layoutDisplayPageObjectProvider.getClassPK(),
				layoutDisplayPageObjectProvider.getClassTypeId())) {

			return super.getLayoutFriendlyURLComposite(
				companyId, groupId, privateLayout, friendlyURL, params,
				requestContext);
		}

		Layout layout = _layoutLocalService.getLayoutByUuidAndGroupId(
			layoutUuid, groupId, privateLayout);

		return new LayoutFriendlyURLComposite(layout, friendlyURL, false);
	}

	@Override
	public String getURLSeparator() {
		return JournalArticleConstants.CANONICAL_URL_SEPARATOR;
	}

	private AssetEntry _getAssetEntry(JournalArticle journalArticle) {
		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(), journalArticle.getPrimaryKey());

		if (assetEntry != null) {
			return assetEntry;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				JournalArticle.class.getName());

		try {
			return assetRendererFactory.getAssetEntry(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey());
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private String _getBasicLayoutURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL, Map<String, Object> requestContext,
			String urlTitle, JournalArticle journalArticle)
		throws PortalException {

		Layout layout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_getLayoutUuid(journalArticle), groupId, privateLayout);

		String layoutActualURL = _portal.getLayoutActualURL(layout, mainPath);

		Map<String, String[]> actualParams = new HashMap<>();

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String defaultAssetPublisherPortletId =
			typeSettingsUnicodeProperties.get(
				LayoutTypePortletConstants.DEFAULT_ASSET_PUBLISHER_PORTLET_ID);

		String currentDefaultAssetPublisherPortletId =
			defaultAssetPublisherPortletId;

		if (Validator.isNull(defaultAssetPublisherPortletId)) {
			defaultAssetPublisherPortletId = PortletIdCodec.encode(
				AssetPublisherPortletKeys.ASSET_PUBLISHER);
		}

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)requestContext.get("request");

		if (Validator.isNull(currentDefaultAssetPublisherPortletId)) {
			String actualPortletAuthenticationToken = AuthTokenUtil.getToken(
				httpServletRequest, layout.getPlid(),
				defaultAssetPublisherPortletId);

			actualParams.put(
				"p_p_auth", new String[] {actualPortletAuthenticationToken});
		}

		actualParams.put(
			"p_p_id", new String[] {defaultAssetPublisherPortletId});
		actualParams.put("p_p_lifecycle", new String[] {"0"});

		if (Validator.isNull(currentDefaultAssetPublisherPortletId)) {
			actualParams.put(
				"p_p_state", new String[] {WindowState.MAXIMIZED.toString()});
		}

		actualParams.put(
			"p_j_a_id", new String[] {String.valueOf(journalArticle.getId())});
		actualParams.put("p_p_mode", new String[] {"view"});

		String namespace = _portal.getPortletNamespace(
			defaultAssetPublisherPortletId);

		actualParams.put(
			namespace + "mvcPath", new String[] {"/view_content.jsp"});

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				JournalArticle.class.getName());

		actualParams.put(
			namespace + "type", new String[] {assetRendererFactory.getType()});

		Locale locale = _portal.getLocale(httpServletRequest);

		AssetEntry assetEntry = _getAssetEntry(journalArticle);

		actualParams.put(
			namespace + "assetEntryId",
			new String[] {String.valueOf(assetEntry.getEntryId())});

		String ddmTemplateKey = _getDDMTemplateKey(groupId, friendlyURL);

		if (Validator.isNotNull(ddmTemplateKey)) {
			actualParams.put(
				namespace + "ddmTemplateKey", new String[] {ddmTemplateKey});
		}

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				groupId,
				_classNameLocalService.getClassNameId(JournalArticle.class),
				urlTitle);

		if (friendlyURLEntryLocalization != null) {
			String languageId = LocaleUtil.toLanguageId(locale);

			if (!Objects.equals(
					friendlyURLEntryLocalization.getLanguageId(), languageId) &&
				ArrayUtil.contains(
					journalArticle.getAvailableLanguageIds(), languageId)) {

				actualParams.put(
					namespace + "languageId", new String[] {languageId});

				locale = LocaleUtil.fromLanguageId(languageId);
			}
			else {
				actualParams.put(
					namespace + "languageId",
					new String[] {
						friendlyURLEntryLocalization.getLanguageId()
					});

				locale = LocaleUtil.fromLanguageId(
					friendlyURLEntryLocalization.getLanguageId());
			}
		}

		String queryString = HttpComponentsUtil.parameterMapToString(
			actualParams, false);

		if (layoutActualURL.contains(StringPool.QUESTION)) {
			layoutActualURL =
				layoutActualURL + StringPool.AMPERSAND + queryString;
		}
		else {
			layoutActualURL =
				layoutActualURL + StringPool.QUESTION + queryString;
		}

		_portal.addPageTitle(
			journalArticle.getTitle(locale), httpServletRequest);

		String pageDescription = HtmlUtil.unescape(
			HtmlUtil.stripHtml(journalArticle.getDescription(locale)));

		if (Validator.isNotNull(pageDescription)) {
			_portal.addPageDescription(pageDescription, httpServletRequest);
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_getLayoutDisplayPageObjectProvider(journalArticle);

		if (layoutDisplayPageObjectProvider == null) {
			return layoutActualURL;
		}

		String keywords = layoutDisplayPageObjectProvider.getKeywords(locale);

		if (Validator.isNotNull(keywords)) {
			_portal.addPageKeywords(keywords, httpServletRequest);
		}

		return layoutActualURL;
	}

	private String _getDDMTemplateKey(long groupId, String friendlyURL) {
		List<String> paths = StringUtil.split(friendlyURL, CharPool.SLASH);

		if (paths.size() <= 2) {
			return StringPool.BLANK;
		}

		String ddmTemplateKey = paths.get(paths.size() - 1);

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			groupId, _portal.getClassNameId(DDMStructure.class), ddmTemplateKey,
			true);

		if (ddmTemplate != null) {
			return ddmTemplateKey;
		}

		return StringPool.BLANK;
	}

	private String _getFullURLTitle(String friendlyURL) {
		String urlSeparator = getURLSeparator();

		return friendlyURL.substring(urlSeparator.length());
	}

	private long _getId(String friendlyURL) {
		List<String> paths = StringUtil.split(friendlyURL, CharPool.SLASH);

		if (paths.size() <= 2) {
			return 0;
		}

		return GetterUtil.getLong(paths.get(paths.size() - 1));
	}

	private JournalArticle _getJournalArticle(
			long groupId, String friendlyURL, Map<String, String[]> params)
		throws PortalException {

		JournalArticle journalArticle = null;

		String normalizedUrlTitle =
			_friendlyURLNormalizer.normalizeWithEncoding(
				_getFullURLTitle(friendlyURL));

		String[] versions = params.get("version");

		if (ArrayUtil.isNotEmpty(versions) && !_isGuestUser()) {
			double version = GetterUtil.getDouble(versions[0]);

			journalArticle = _journalArticleLocalService.fetchArticleByUrlTitle(
				groupId, normalizedUrlTitle, version);
		}

		if (journalArticle == null) {
			journalArticle =
				_journalArticleLocalService.fetchLatestArticleByUrlTitle(
					groupId, normalizedUrlTitle,
					WorkflowConstants.STATUS_APPROVED);
		}

		if (journalArticle == null) {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			journalArticle =
				_journalArticleLocalService.fetchLatestArticleByUrlTitle(
					groupId, normalizedUrlTitle,
					WorkflowConstants.STATUS_PENDING);

			if ((journalArticle != null) &&
				!GetterUtil.getBoolean(
					WorkflowPermissionUtil.hasPermission(
						permissionChecker, groupId,
						"com.liferay.journal.model.JournalArticle",
						journalArticle.getId(), ActionKeys.VIEW))) {

				throw new PrincipalException();
			}
		}

		if (journalArticle == null) {
			normalizedUrlTitle = _friendlyURLNormalizer.normalizeWithEncoding(
				_getURLTitle(friendlyURL));

			double version = _getVersion(friendlyURL);

			if (version > 0) {
				journalArticle =
					_journalArticleLocalService.fetchArticleByUrlTitle(
						groupId, normalizedUrlTitle, version);
			}
			else {
				journalArticle =
					_journalArticleLocalService.fetchLatestArticleByUrlTitle(
						groupId, normalizedUrlTitle,
						WorkflowConstants.STATUS_APPROVED);
			}
		}

		if (journalArticle == null) {
			normalizedUrlTitle = _friendlyURLNormalizer.normalizeWithEncoding(
				_getURLTitle(friendlyURL));

			long id = _getId(friendlyURL);

			if (id > 0) {
				journalArticle = _journalArticleLocalService.fetchArticle(id);
			}
		}

		if (journalArticle == null) {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			journalArticle =
				_journalArticleLocalService.getLatestArticleByUrlTitle(
					groupId, normalizedUrlTitle,
					WorkflowConstants.STATUS_PENDING);

			if (!GetterUtil.getBoolean(
					WorkflowPermissionUtil.hasPermission(
						permissionChecker, groupId,
						"com.liferay.journal.model.JournalArticle",
						journalArticle.getId(), ActionKeys.VIEW))) {

				throw new PrincipalException();
			}
		}

		Map<Locale, String> friendlyURLMap = journalArticle.getFriendlyURLMap();

		if (!friendlyURLMap.containsValue(normalizedUrlTitle)) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No latest version of a JournalArticle exists with the key",
					"{groupId=", groupId, ", urlTitle=",
					_getURLTitle(friendlyURL), ", status=",
					WorkflowConstants.STATUS_ANY, "}"));
		}

		return journalArticle;
	}

	private LayoutDisplayPageObjectProvider<?>
		_getLayoutDisplayPageObjectProvider(JournalArticle journalArticle) {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					JournalArticle.class.getName());

		InfoItemReference infoItemReference = new InfoItemReference(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		return layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
			infoItemReference);
	}

	private String _getLayoutUuid(JournalArticle journalArticle) {
		String layoutUuid = journalArticle.getLayoutUuid();

		if (Validator.isNotNull(layoutUuid)) {
			return layoutUuid;
		}

		AssetEntry assetEntry = _getAssetEntry(journalArticle);

		return assetEntry.getLayoutUuid();
	}

	private String _getURLTitle(String friendlyURL) {
		String fullURLTitle = _getFullURLTitle(friendlyURL);

		if (!fullURLTitle.contains(StringPool.SLASH)) {
			return fullURLTitle;
		}

		String urlSeparator = getURLSeparator();

		return friendlyURL.substring(
			urlSeparator.length(), friendlyURL.lastIndexOf(StringPool.SLASH));
	}

	private double _getVersion(String friendlyURL) {
		List<String> paths = StringUtil.split(friendlyURL, CharPool.SLASH);

		if (paths.size() <= 2) {
			return 0;
		}

		String lastPath = paths.get(paths.size() - 1);

		List<String> numbers = StringUtil.split(lastPath, CharPool.PERIOD);

		if ((numbers.size() == 2) && Validator.isDigit(numbers.get(0)) &&
			Validator.isDigit(numbers.get(1))) {

			return GetterUtil.getDouble(lastPath);
		}

		return 0;
	}

	private boolean _isGuestUser() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return true;
		}

		User user = _userLocalService.fetchUser(serviceContext.getUserId());

		if ((user == null) || user.isGuestUser()) {
			return true;
		}

		return false;
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}