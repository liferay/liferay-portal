/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.category.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.content.category.web.internal.configuration.CPCategoryContentPortletInstanceConfiguration;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CPCategoryContentDisplayContext {

	public CPCategoryContentDisplayContext(
			HttpServletRequest httpServletRequest,
			AssetCategoryService assetCategoryService,
			CommerceMediaResolver commerceMediaResolver,
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			GroupLocalService groupLocalService, Portal portal)
		throws ConfigurationException {

		_httpServletRequest = httpServletRequest;
		_assetCategoryService = assetCategoryService;
		_commerceMediaResolver = commerceMediaResolver;
		_cpAttachmentFileEntryService = cpAttachmentFileEntryService;
		_groupLocalService = groupLocalService;
		_portal = portal;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_cpCategoryContentPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				CPCategoryContentPortletInstanceConfiguration.class,
				_themeDisplay);
	}

	public AssetCategory getAssetCategory() throws PortalException {
		if (_cpCategoryContentPortletInstanceConfiguration.useAssetCategory()) {
			_assetCategory =
				_assetCategoryService.fetchCategoryByExternalReferenceCode(
					_cpCategoryContentPortletInstanceConfiguration.
						assetCategoryExternalReferenceCode(),
					_themeDisplay.getScopeGroupId());

			if (_assetCategory == null) {
				_assetCategory =
					_assetCategoryService.fetchCategoryByExternalReferenceCode(
						_cpCategoryContentPortletInstanceConfiguration.
							assetCategoryExternalReferenceCode(),
						_themeDisplay.getCompanyGroupId());
			}
		}
		else {
			_assetCategory = (AssetCategory)_httpServletRequest.getAttribute(
				WebKeys.ASSET_CATEGORY);
		}

		return _assetCategory;
	}

	public String getDefaultImageSrc() throws Exception {
		AssetCategory assetCategory = getAssetCategory();

		if (assetCategory == null) {
			return null;
		}

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryService.getCPAttachmentFileEntries(
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId(),
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
		return _cpCategoryContentPortletInstanceConfiguration.displayStyle();
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			_cpCategoryContentPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		Group group = _themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
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

		String displayStyleGroupExternalReferenceCode =
			_cpCategoryContentPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		Group group = _themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
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

	public boolean useAssetCategory() {
		return _cpCategoryContentPortletInstanceConfiguration.
			useAssetCategory();
	}

	private AssetCategory _assetCategory;
	private final AssetCategoryService _assetCategoryService;
	private final CommerceMediaResolver _commerceMediaResolver;
	private final CPAttachmentFileEntryService _cpAttachmentFileEntryService;
	private final CPCategoryContentPortletInstanceConfiguration
		_cpCategoryContentPortletInstanceConfiguration;
	private Long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Portal _portal;
	private final ThemeDisplay _themeDisplay;

}