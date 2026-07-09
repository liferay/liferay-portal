/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.manager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.xml.Element;

import java.io.InputStream;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Raymond Augé
 */
@ProviderType
public interface SitemapManager {

	public static final int MAXIMUM_ENTRIES = 50000;

	public default void addURLElement(
		Element element, String url,
		UnicodeProperties typeSettingsUnicodeProperties, Date modifiedDate,
		String canonicalURL, Map<Locale, String> alternateURLs) {

		addURLElement(
			element, url, typeSettingsUnicodeProperties, modifiedDate,
			canonicalURL, alternateURLs, 0);
	}

	public void addURLElement(
		Element element, String url,
		UnicodeProperties typeSettingsUnicodeProperties, Date modifiedDate,
		String canonicalURL, Map<Locale, String> alternateURLs, long groupId);

	public void deleteRegenerateSitemapScheduledJobs(long companyId)
		throws PortalException;

	public String encodeXML(String input);

	public Map<Locale, String> getAlternateURLs(
			String canonicalURL, ThemeDisplay themeDisplay, Layout layout)
		throws PortalException;

	public long getAssetTypeClassNameId(String assetTypeKey);

	public Map<Long, String> getAssetTypeKeys();

	public String getSitemap(
			long groupId, boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getSitemap(
			long assetTypeClassNameId, String layoutUuid, long groupId,
			boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getSitemap(
			long assetTypeClassNameId, String layoutUuid, long groupId,
			int page, boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getSitemap(
			String layoutUuid, long groupId, boolean privateLayout,
			ThemeDisplay themeDisplay)
		throws PortalException;

	public InputStream getSitemapInputStream(
			String assetTypeKey, String layoutUuid, long groupId, int page,
			boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException;

	public void regenerateSitemap(
			String assetTypeKey, long companyId, long groupId)
		throws PortalException;

	public void scheduleRegenerateSitemap(
		String assetTypeKey, long companyId, long groupId, Date startDate);

}