/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.navigation.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.asset.categories.navigation.web.internal.configuration.CPAssetCategoriesNavigationPortletInstanceConfiguration;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Marco Leo
 */
public class CPAssetCategoriesNavigationDisplayContext {

	public CPAssetCategoriesNavigationDisplayContext(
			AssetCategoryService assetCategoryService,
			AssetVocabularyService assetVocabularyService,
			CommerceMediaResolver commerceMediaResolver,
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			CPFriendlyURL cpFriendlyURL,
			FriendlyURLEntryLocalService friendlyURLEntryLocalService,
			GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest, Portal portal)
		throws ConfigurationException {

		_assetCategoryService = assetCategoryService;
		_assetVocabularyService = assetVocabularyService;
		_commerceMediaResolver = commerceMediaResolver;
		_cpAttachmentFileEntryService = cpAttachmentFileEntryService;
		_cpFriendlyURL = cpFriendlyURL;
		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_portal = portal;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_cpAssetCategoriesNavigationPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				CPAssetCategoriesNavigationPortletInstanceConfiguration.class,
				_themeDisplay);
	}

	public List<AssetCategory> getAssetCategories() throws PortalException {
		if (_assetCategories != null) {
			return _assetCategories;
		}

		AssetCategory assetCategory = _getParentCategory();

		if (assetCategory != null) {
			_assetCategories = _assetCategoryService.getVocabularyCategories(
				assetCategory.getCategoryId(), assetCategory.getVocabularyId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
		else {
			if (useRootCategory()) {
				return Collections.emptyList();
			}

			AssetVocabulary assetVocabulary = getAssetVocabulary();

			if (assetVocabulary == null) {
				return Collections.emptyList();
			}

			_assetCategories =
				_assetCategoryService.getVocabularyRootCategories(
					assetVocabulary.getGroupId(),
					assetVocabulary.getVocabularyId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);
		}

		return _assetCategories;
	}

	public List<AssetVocabulary> getAssetVocabularies() throws PortalException {
		if (_assetVocabularies != null) {
			return _assetVocabularies;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_assetVocabularies = _assetVocabularyService.getGroupVocabularies(
			themeDisplay.getCompanyGroupId(),
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);

		return _assetVocabularies;
	}

	public AssetVocabulary getAssetVocabulary() throws PortalException {
		if (_assetVocabulary != null) {
			return _assetVocabulary;
		}

		String assetVocabularyExternalReferenceCode =
			_cpAssetCategoriesNavigationPortletInstanceConfiguration.
				assetVocabularyExternalReferenceCode();

		if (Validator.isNull(assetVocabularyExternalReferenceCode)) {
			return _assetVocabulary;
		}

		try {
			_assetVocabulary =
				_assetVocabularyService.
					getAssetVocabularyByExternalReferenceCode(
						_themeDisplay.getCompanyGroupId(),
						assetVocabularyExternalReferenceCode);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return _assetVocabulary;
	}

	public List<AssetCategory> getChildAssetCategories(long categoryId)
		throws PortalException {

		return _assetCategoryService.getChildCategories(categoryId);
	}

	public CPAssetCategoriesNavigationPortletInstanceConfiguration
		getCPAssetCategoriesNavigationPortletInstanceConfiguration() {

		return _cpAssetCategoriesNavigationPortletInstanceConfiguration;
	}

	public String getDefaultImageSrc(long categoryId) throws Exception {
		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryService.getCPAttachmentFileEntries(
				_portal.getClassNameId(AssetCategory.class), categoryId,
				CPAttachmentFileEntryConstants.TYPE_IMAGE,
				WorkflowConstants.STATUS_APPROVED, 0, 1);

		if (cpAttachmentFileEntries.isEmpty()) {
			return null;
		}

		CPAttachmentFileEntry cpAttachmentFileEntry =
			cpAttachmentFileEntries.get(0);

		if (cpAttachmentFileEntry == null) {
			return null;
		}

		return _commerceMediaResolver.getURL(
			CommerceUtil.getCommerceAccountId(
				(CommerceContext)_httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT)),
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());
	}

	public String getDisplayStyle() {
		return _cpAssetCategoriesNavigationPortletInstanceConfiguration.
			displayStyle();
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			_cpAssetCategoriesNavigationPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		Group group = _themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				_themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupId = group.getGroupId();
		}
		else {
			_displayStyleGroupId = _themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(_displayStyleGroupKey)) {
			return _displayStyleGroupKey;
		}

		Group group = _themeDisplay.getScopeGroup();

		String displayStyleGroupExternalReferenceCode =
			_cpAssetCategoriesNavigationPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				_themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupKey = group.getGroupKey();
		}
		else {
			_displayStyleGroupKey = StringPool.BLANK;
		}

		return _displayStyleGroupKey;
	}

	public String getFriendlyURL(long categoryId, ThemeDisplay themeDisplay)
		throws Exception {

		AssetCategory assetCategory = _assetCategoryService.fetchCategory(
			categoryId);

		if (assetCategory == null) {
			return StringPool.BLANK;
		}

		long classNameId = _portal.getClassNameId(AssetCategory.class);

		FriendlyURLEntry friendlyURLEntry = null;

		try {
			friendlyURLEntry =
				_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
					classNameId, categoryId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}

		String groupFriendlyURL = _portal.getGroupFriendlyURL(
			themeDisplay.getLayoutSet(), themeDisplay, false, false);

		String languageId = LanguageUtil.getLanguageId(
			themeDisplay.getLocale());

		String assetCategoryURLSeparator =
			_cpFriendlyURL.getAssetCategoryURLSeparator(
				themeDisplay.getCompanyId());

		return groupFriendlyURL + assetCategoryURLSeparator +
			friendlyURLEntry.getUrlTitle(languageId);
	}

	public String getRootAssetCategoryId() throws PortalException {
		AssetCategory assetCategory = null;

		String rootAssetCategoryExternalReferenceCode =
			_cpAssetCategoriesNavigationPortletInstanceConfiguration.
				rootAssetCategoryExternalReferenceCode();

		if (Validator.isNotNull(rootAssetCategoryExternalReferenceCode)) {
			assetCategory =
				_assetCategoryService.fetchCategoryByExternalReferenceCode(
					rootAssetCategoryExternalReferenceCode,
					_themeDisplay.getCompanyGroupId());
		}

		if (assetCategory != null) {
			return String.valueOf(assetCategory.getCategoryId());
		}

		return StringPool.BLANK;
	}

	public String getVocabularyNavigation(ThemeDisplay themeDisplay)
		throws Exception {

		long categoryId = 0;

		AssetCategory assetCategory = _getParentCategory();

		if (assetCategory == null) {
			assetCategory = (AssetCategory)_httpServletRequest.getAttribute(
				WebKeys.ASSET_CATEGORY);
		}

		if (assetCategory != null) {
			categoryId = assetCategory.getCategoryId();
		}

		List<AssetCategory> categories = getAssetCategories();

		if (categories.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		sb.append("<div class=\"lfr-asset-category-list-container\">");
		sb.append("<ul class=\"lfr-asset-category-list\">");

		_buildCategoriesNavigation(categories, categoryId, themeDisplay, sb);

		sb.append("</ul></div>");

		return sb.toString();
	}

	public boolean useCategoryFromRequest() {
		return _cpAssetCategoriesNavigationPortletInstanceConfiguration.
			useCategoryFromRequest();
	}

	public boolean useRootCategory() {
		return _cpAssetCategoriesNavigationPortletInstanceConfiguration.
			useRootCategory();
	}

	private void _buildCategoriesNavigation(
			List<AssetCategory> categories, long categoryId,
			ThemeDisplay themeDisplay, StringBundler sb)
		throws Exception {

		for (AssetCategory assetCategory : categories) {
			List<AssetCategory> childAssetCategories = getChildAssetCategories(
				assetCategory.getCategoryId());

			String friendlyURL = getFriendlyURL(
				assetCategory.getCategoryId(), themeDisplay);

			sb.append("<li class=\"tree-node\"><span>");

			if (categoryId == assetCategory.getCategoryId()) {
				sb.append("<a class=\"tag-selected\" href=\"");
				sb.append(HtmlUtil.escape(friendlyURL));
			}
			else {
				sb.append("<a href=\"");
				sb.append(HtmlUtil.escape(friendlyURL));
			}

			sb.append("\">");

			String categoryTitle = assetCategory.getTitle(
				themeDisplay.getLocale());

			sb.append(HtmlUtil.escape(categoryTitle));

			sb.append("</a>");
			sb.append("</span>");

			if (!childAssetCategories.isEmpty()) {
				sb.append("<ul>");

				_buildCategoriesNavigation(
					childAssetCategories, categoryId, themeDisplay, sb);

				sb.append("</ul>");
			}

			sb.append("</li>");
		}
	}

	private AssetCategory _getParentCategory() throws PortalException {
		AssetCategory assetCategory = null;

		if (useRootCategory()) {
			if (useCategoryFromRequest()) {
				assetCategory = (AssetCategory)_httpServletRequest.getAttribute(
					WebKeys.ASSET_CATEGORY);
			}
			else {
				long categoryId = GetterUtil.getLong(getRootAssetCategoryId());

				if (categoryId > 0) {
					assetCategory = _assetCategoryService.getCategory(
						categoryId);
				}
			}
		}

		return assetCategory;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPAssetCategoriesNavigationDisplayContext.class);

	private List<AssetCategory> _assetCategories;
	private final AssetCategoryService _assetCategoryService;
	private List<AssetVocabulary> _assetVocabularies;
	private AssetVocabulary _assetVocabulary;
	private final AssetVocabularyService _assetVocabularyService;
	private final CommerceMediaResolver _commerceMediaResolver;
	private final CPAssetCategoriesNavigationPortletInstanceConfiguration
		_cpAssetCategoriesNavigationPortletInstanceConfiguration;
	private final CPAttachmentFileEntryService _cpAttachmentFileEntryService;
	private final CPFriendlyURL _cpFriendlyURL;
	private Long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private final FriendlyURLEntryLocalService _friendlyURLEntryLocalService;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Portal _portal;
	private final ThemeDisplay _themeDisplay;

}