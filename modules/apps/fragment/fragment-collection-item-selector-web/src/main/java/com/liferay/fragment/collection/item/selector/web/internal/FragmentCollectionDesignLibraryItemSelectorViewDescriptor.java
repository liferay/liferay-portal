/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.item.selector.web.internal;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.ResultRowSplitterEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Gabriel Lima
 */
public class FragmentCollectionDesignLibraryItemSelectorViewDescriptor
	extends FragmentCollectionItemSelectorViewDescriptor {

	public FragmentCollectionDesignLibraryItemSelectorViewDescriptor(
		long[] groupIds, HttpServletRequest httpServletRequest,
		PortletURL portletURL) {

		super(groupIds, httpServletRequest, portletURL);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_locale = themeDisplay.getLocale();
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		FragmentCollection fragmentCollection) {

		String groupName = _getGroupName(fragmentCollection.getGroupId());

		if (groupName == null) {
			return super.getItemDescriptor(fragmentCollection);
		}

		return new FragmentCollectionItemDescriptor(
			fragmentCollection,
			StringBundler.concat(
				"[", groupName, "] ", fragmentCollection.getName()));
	}

	@Override
	public ResultRowSplitter getResultRowSplitter() {
		return resultRows -> {
			Map<Long, List<ResultRow>> resultRowsMap = new LinkedHashMap<>();

			for (ResultRow resultRow : resultRows) {
				FragmentCollection fragmentCollection =
					(FragmentCollection)resultRow.getObject();

				List<ResultRow> groupResultRows = resultRowsMap.computeIfAbsent(
					fragmentCollection.getGroupId(),
					groupId -> new ArrayList<>());

				groupResultRows.add(resultRow);
			}

			return TransformUtil.transform(
				resultRowsMap.keySet(),
				groupId -> new ResultRowSplitterEntry(
					HtmlUtil.escape(_getGroupName(groupId)),
					resultRowsMap.get(groupId)));
		};
	}

	@Override
	protected OrderByComparator<FragmentCollection> getOrderByComparator(
		boolean orderByAsc) {

		return OrderByComparatorFactoryUtil.create(
			"FragmentCollection", "groupId", true, "name", orderByAsc);
	}

	private String _getGroupName(long groupId) {
		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		try {
			return group.getDescriptiveName(_locale);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return group.getName(_locale);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentCollectionDesignLibraryItemSelectorViewDescriptor.class);

	private final Locale _locale;

}