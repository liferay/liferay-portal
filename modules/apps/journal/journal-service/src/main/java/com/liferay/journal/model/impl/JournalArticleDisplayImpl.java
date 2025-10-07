/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.model.impl;

import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class JournalArticleDisplayImpl implements JournalArticleDisplay {

	public JournalArticleDisplayImpl(
		long companyId, String externalReferenceCode, long id,
		long resourcePrimKey, long groupId, long userId, String articleId,
		double version, String title, String urlTitle, String description,
		String[] availableLocales, String content, long ddmStructureId,
		String ddmTemplateKey, boolean smallImage, long smallImageId,
		String smallImageURL, String articleDisplayImageURL, int numberOfPages,
		int currentPage, boolean paginate, boolean cacheable) {

		_companyId = companyId;
		_externalReferenceCode = externalReferenceCode;
		_id = id;
		_resourcePrimKey = resourcePrimKey;
		_groupId = groupId;
		_userId = userId;
		_articleId = articleId;
		_version = version;
		_title = title;
		_urlTitle = urlTitle;
		_description = description;
		_availableLocales = availableLocales;
		_content = content;
		_ddmStructureId = ddmStructureId;
		_ddmTemplateKey = ddmTemplateKey;
		_smallImage = smallImage;
		_smallImageId = smallImageId;
		_smallImageURL = smallImageURL;
		_articleDisplayImageURL = articleDisplayImageURL;
		_numberOfPages = numberOfPages;
		_currentPage = currentPage;
		_paginate = paginate;
		_cacheable = cacheable;
	}

	@Override
	public String getArticleDisplayImageURL(ThemeDisplay themeDisplay) {
		return _articleDisplayImageURL;
	}

	@Override
	public String getArticleId() {
		return _articleId;
	}

	@Override
	public String[] getAvailableLocales() {
		return _availableLocales;
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public String getContent() {
		return _content;
	}

	@Override
	public int getCurrentPage() {
		return _currentPage;
	}

	@Override
	public long getDDMStructureId() {
		return _ddmStructureId;
	}

	@Override
	public String getDDMTemplateKey() {
		return _ddmTemplateKey;
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	@Override
	public long getGroupId() {
		return _groupId;
	}

	@Override
	public long getId() {
		return _id;
	}

	@Override
	public int getNumberOfPages() {
		return _numberOfPages;
	}

	@Override
	public long getResourcePrimKey() {
		return _resourcePrimKey;
	}

	@Override
	public long getSmallImageId() {
		return _smallImageId;
	}

	@Override
	public String getSmallImageURL() {
		return _smallImageURL;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public String getUrlTitle() {
		return _urlTitle;
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public double getVersion() {
		return _version;
	}

	@Override
	public boolean isCacheable() {
		return _cacheable;
	}

	@Override
	public boolean isPaginate() {
		return _paginate;
	}

	@Override
	public boolean isSmallImage() {
		return _smallImage;
	}

	@Override
	public void setCacheable(boolean cacheable) {
		_cacheable = cacheable;
	}

	@Override
	public void setContent(String content) {
		_content = content;
	}

	@Override
	public void setCurrentPage(int currentPage) {
		_currentPage = currentPage;
	}

	@Override
	public void setDDMStructureId(long ddmStructureId) {
		_ddmStructureId = ddmStructureId;
	}

	@Override
	public void setDDMTemplateKey(String ddmTemplateKey) {
		_ddmTemplateKey = ddmTemplateKey;
	}

	@Override
	public void setNumberOfPages(int numberOfPages) {
		_numberOfPages = numberOfPages;
	}

	@Override
	public void setPaginate(boolean paginate) {
		_paginate = paginate;
	}

	@Override
	public void setSmallImage(boolean smallImage) {
		_smallImage = smallImage;
	}

	@Override
	public void setSmallImageId(long smallImageId) {
		_smallImageId = smallImageId;
	}

	@Override
	public void setSmallImageURL(String smallImageURL) {
		_smallImageURL = smallImageURL;
	}

	private final String _articleDisplayImageURL;
	private final String _articleId;
	private final String[] _availableLocales;
	private boolean _cacheable;
	private final long _companyId;
	private String _content;
	private int _currentPage;
	private long _ddmStructureId;
	private String _ddmTemplateKey;
	private final String _description;
	private final String _externalReferenceCode;
	private final long _groupId;
	private final long _id;
	private int _numberOfPages;
	private boolean _paginate;
	private final long _resourcePrimKey;
	private boolean _smallImage;
	private long _smallImageId;
	private String _smallImageURL;
	private final String _title;
	private final String _urlTitle;
	private final long _userId;
	private final double _version;

}