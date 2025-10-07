/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.model;

import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.io.Serializable;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public interface JournalArticleDisplay extends Serializable {

	public String getArticleDisplayImageURL(ThemeDisplay themeDisplay);

	public String getArticleId();

	public String[] getAvailableLocales();

	public long getCompanyId();

	public String getContent();

	public int getCurrentPage();

	public long getDDMStructureId();

	public String getDDMTemplateKey();

	public String getDescription();

	public String getExternalReferenceCode();

	public long getGroupId();

	public long getId();

	public int getNumberOfPages();

	public long getResourcePrimKey();

	public long getSmallImageId();

	public String getSmallImageURL();

	public String getTitle();

	public String getUrlTitle();

	public long getUserId();

	public double getVersion();

	public boolean isCacheable();

	public boolean isPaginate();

	public boolean isSmallImage();

	public void setCacheable(boolean cacheable);

	public void setContent(String content);

	public void setCurrentPage(int currentPage);

	public void setDDMStructureId(long ddmStructureId);

	public void setDDMTemplateKey(String ddmTemplateKey);

	public void setNumberOfPages(int numberOfPages);

	public void setPaginate(boolean paginate);

	public void setSmallImage(boolean smallImage);

	public void setSmallImageId(long smallImageId);

	public void setSmallImageURL(String smallImageURL);

}