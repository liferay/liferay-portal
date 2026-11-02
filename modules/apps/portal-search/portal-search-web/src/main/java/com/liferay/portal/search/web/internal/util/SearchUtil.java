/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.OpenSearch;
import com.liferay.portal.kernel.search.OpenSearchRegistryUtil;
import com.liferay.portal.kernel.search.OpenSearchUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XMLUtil;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.WindowState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class SearchUtil {

	public static Tuple getElements(
		String xml, String className, int inactiveGroupsCount) {

		List<Element> resultRows = new ArrayList<>();
		int totalRows = 0;

		try {
			xml = XMLUtil.stripInvalidChars(xml);

			Document document = SAXReaderUtil.read(xml);

			Element rootElement = document.getRootElement();

			List<Element> elements = rootElement.elements("entry");

			totalRows = GetterUtil.getInteger(
				rootElement.elementText(
					OpenSearchUtil.getQName(
						"totalResults", OpenSearchUtil.OS_NAMESPACE)));

			for (Element element : elements) {
				try {
					long entryScopeGroupId = GetterUtil.getLong(
						element.elementText(
							OpenSearchUtil.getQName(
								"scopeGroupId",
								OpenSearchUtil.LIFERAY_NAMESPACE)));

					if ((entryScopeGroupId != 0) && (inactiveGroupsCount > 0)) {
						Group entryGroup = GroupServiceUtil.getGroup(
							entryScopeGroupId);

						if (entryGroup.isLayout()) {
							entryGroup = GroupLocalServiceUtil.getGroup(
								entryGroup.getParentGroupId());
						}

						if (!GroupLocalServiceUtil.isLiveGroupActive(
								entryGroup)) {

							totalRows--;

							continue;
						}
					}

					resultRows.add(element);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to retrieve individual search result for " +
							className,
						exception);

					totalRows--;
				}
			}
		}
		catch (Exception exception) {
			_log.error("Unable to display content for " + className, exception);
		}

		return new Tuple(resultRows, totalRows);
	}

	public static List<OpenSearch> getOpenSearchInstances(
		String primarySearch) {

		List<OpenSearch> openSearchInstances = ListUtil.filter(
			OpenSearchRegistryUtil.getOpenSearchInstances(),
			OpenSearch::isEnabled);

		if (Validator.isNotNull(primarySearch)) {
			for (int i = 0; i < openSearchInstances.size(); i++) {
				OpenSearch openSearch = openSearchInstances.get(i);

				if (primarySearch.equals(openSearch.getClassName())) {
					if (i != 0) {
						openSearchInstances.remove(i);

						openSearchInstances.add(0, openSearch);
					}

					break;
				}
			}
		}

		return openSearchInstances;
	}

	public static String getSearchResultViewURL(
		RenderRequest renderRequest, RenderResponse renderResponse,
		String className, long classPK, boolean viewInContext) {

		try {
			PortletURL viewContentURL = PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCPath(
				"/view_content.jsp"
			).setPortletMode(
				PortletMode.VIEW
			).setWindowState(
				WindowState.MAXIMIZED
			).buildPortletURL();

			if (Validator.isNull(className) || (classPK <= 0)) {
				return viewContentURL.toString();
			}

			AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
				className, classPK);

			if (assetEntry == null) {
				return StringPool.BLANK;
			}

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(className);

			if (assetRendererFactory == null) {
				return viewContentURL.toString();
			}

			viewContentURL.setParameter(
				"assetEntryId", String.valueOf(assetEntry.getEntryId()));
			viewContentURL.setParameter("type", assetRendererFactory.getType());

			if (!viewInContext && assetEntry.isVisible()) {
				return viewContentURL.toString();
			}

			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(classPK);

			String viewURL = assetRenderer.getURLViewInContext(
				PortalUtil.getLiferayPortletRequest(renderRequest),
				PortalUtil.getLiferayPortletResponse(renderResponse),
				viewContentURL.toString());

			if (Validator.isNull(viewURL)) {
				viewURL = viewContentURL.toString();
			}

			return viewURL;
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to get search result view URL for class ",
					className, " with primary key ", classPK),
				exception);

			return StringPool.BLANK;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(SearchUtil.class);

}