/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.vocabulary.item.selector.web.internal;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class AssetVocabularyItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public AssetVocabularyItemDescriptor(
		AssetVocabulary assetVocabulary,
		HttpServletRequest httpServletRequest) {

		_assetVocabulary = assetVocabulary;
		_httpServletRequest = httpServletRequest;
	}

	@Override
	public String getIcon() {
		return "vocabulary";
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public Date getModifiedDate() {
		return _assetVocabulary.getModifiedDate();
	}

	@Override
	public String getPayload() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"assetVocabularyId",
			String.valueOf(_assetVocabulary.getVocabularyId())
		).put(
			"groupId", String.valueOf(_assetVocabulary.getGroupId())
		).put(
			"title", _assetVocabulary.getTitle(themeDisplay.getLocale())
		).put(
			"uuid", _assetVocabulary.getUuid()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return LanguageUtil.format(
			locale, "x-categories", _assetVocabulary.getCategoriesCount());
	}

	@Override
	public String getTitle(Locale locale) {
		StringBundler sb = new StringBundler(5);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		sb.append(_assetVocabulary.getTitle(themeDisplay.getLocale()));

		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		if (_assetVocabulary.getGroupId() == themeDisplay.getCompanyGroupId()) {
			sb.append(LanguageUtil.get(_httpServletRequest, "global"));
		}
		else {
			try {
				Group group = GroupLocalServiceUtil.getGroup(
					_assetVocabulary.getGroupId());

				sb.append(group.getDescriptiveName(themeDisplay.getLocale()));
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	@Override
	public long getUserId() {
		return _assetVocabulary.getUserId();
	}

	@Override
	public String getUserName() {
		return _assetVocabulary.getUserName();
	}

	@Override
	public boolean isCompact() {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetVocabularyItemDescriptor.class);

	private final AssetVocabulary _assetVocabulary;
	private final HttpServletRequest _httpServletRequest;

}