/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.search.BaseOpenSearchImpl;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * @author Charles May
 * @author Brian Wing Shun Chan
 */
public class PortalOpenSearchImpl extends BaseOpenSearchImpl {

	public PortalOpenSearchImpl(
		String openSearchURL, String openSearchDescriptionURL) {

		super(openSearchURL, openSearchDescriptionURL);
	}

	@Override
	public String search(
			HttpServletRequest httpServletRequest, long groupId, long userId,
			String keywords, int startPage, int itemsPerPage, String format)
		throws SearchException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			int start = (startPage * itemsPerPage) - itemsPerPage;
			int end = startPage * itemsPerPage;

			Hits results = CompanyLocalServiceUtil.search(
				themeDisplay.getCompanyId(), userId, keywords, start, end);

			String[] queryTerms = results.getQueryTerms();

			int total = results.getLength();

			Object[] values = addSearchResults(
				queryTerms, keywords, startPage, itemsPerPage, total, start,
				"Liferay Portal Search: " + keywords, StringPool.BLANK, format,
				themeDisplay);

			com.liferay.portal.kernel.xml.Document doc =
				(com.liferay.portal.kernel.xml.Document)values[0];
			Element root = (Element)values[1];

			Document[] docs = results.getDocs();

			for (int i = 0; i < docs.length; i++) {
				Document result = results.doc(i);

				String className = result.get(Field.ENTRY_CLASS_NAME);

				String portletId = PortletProviderUtil.getPortletId(
					className, PortletProvider.Action.VIEW);

				Portlet portlet = PortletLocalServiceUtil.getPortletById(
					themeDisplay.getCompanyId(), portletId);

				if (portlet == null) {
					continue;
				}

				String portletTitle = PortalUtil.getPortletTitle(
					portletId, themeDisplay.getUser());

				long resultGroupId = GetterUtil.getLong(
					result.get(Field.GROUP_ID));

				long resultScopeGroupId = GetterUtil.getLong(
					result.get(Field.SCOPE_GROUP_ID));

				if (resultScopeGroupId == 0) {
					resultScopeGroupId = themeDisplay.getScopeGroupId();
				}

				String entryClassName = GetterUtil.getString(
					result.get(Field.ENTRY_CLASS_NAME));

				long entryClassPK = GetterUtil.getLong(
					result.get(Field.ENTRY_CLASS_PK));

				String title = StringPool.BLANK;

				PortletURL portletURL = getPortletURL(
					httpServletRequest, portletId, resultScopeGroupId);

				String url = portletURL.toString();

				Date modifiedDate = result.getDate(Field.MODIFIED_DATE);

				String content = StringPool.BLANK;

				Indexer<?> indexer = IndexerRegistryUtil.getIndexer(
					entryClassName);

				if (indexer != null) {
					String snippet = result.get(Field.SNIPPET);

					Summary summary = indexer.getSummary(
						result, snippet, null, null);

					if (summary == null) {
						continue;
					}

					title = summary.getTitle();
					url = portletURL.toString();
					content = summary.getContent();
				}

				double score = results.score(i);

				addSearchResult(
					root, resultGroupId, resultScopeGroupId, entryClassName,
					entryClassPK,
					StringBundler.concat(portletTitle, " &raquo; ", title), url,
					modifiedDate, content, score, format);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Return\n" + doc.asXML());
			}

			return doc.asXML();
		}
		catch (Exception exception) {
			throw new SearchException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalOpenSearchImpl.class);

}