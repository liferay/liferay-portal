/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.constants.UADConstants;
import com.liferay.user.associated.data.web.internal.display.UADApplicationSummaryDisplay;
import com.liferay.user.associated.data.web.internal.display.UADEntity;
import com.liferay.user.associated.data.web.internal.display.UADHierarchyDisplay;
import com.liferay.user.associated.data.web.internal.search.UADHierarchyChecker;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Samuel Trong Tran
 */
public class UADSearchContainerBuilderUtil {

	public static SearchContainer<UADEntity<?>>
			getApplicationSummaryUADEntitySearchContainer(
				LiferayPortletResponse liferayPortletResponse,
				RenderRequest renderRequest, PortletURL currentURL,
				List<UADApplicationSummaryDisplay>
					uadApplicationSummaryDisplays)
		throws PortletException {

		SearchContainer<UADEntity<?>> searchContainer =
			_constructSearchContainer(
				renderRequest, currentURL, "name",
				new String[] {"name", "count"});

		searchContainer.setResultsAndTotal(
			ListUtil.sort(
				TransformUtil.transform(
					uadApplicationSummaryDisplays,
					uadApplicationSummaryDisplay -> {
						if (Objects.equals(
								uadApplicationSummaryDisplay.
									getApplicationKey(),
								UADConstants.ALL_APPLICATIONS) ||
							(uadApplicationSummaryDisplay.getCount() == 0)) {

							return null;
						}

						return _constructApplicationSummaryUADEntity(
							liferayPortletResponse, renderRequest, currentURL,
							uadApplicationSummaryDisplay);
					}),
				_getComparator(
					searchContainer.getOrderByCol(),
					searchContainer.getOrderByType())));
		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(liferayPortletResponse));

		return searchContainer;
	}

	public static SearchContainer<UADEntity<?>>
		getHierarchyUADEntitySearchContainer(
			LiferayPortletResponse liferayPortletResponse,
			RenderRequest renderRequest, String applicationKey,
			PortletURL currentURL, long[] groupIds, String parentContainerKey,
			Serializable parentContainerId, User selectedUser,
			UADHierarchyDisplay uadHierarchyDisplay) {

		SearchContainer<UADEntity<?>> searchContainer =
			_constructSearchContainer(
				renderRequest, currentURL, "name",
				uadHierarchyDisplay.getSortingFieldNames());

		try {
			DisplayTerms displayTerms = searchContainer.getDisplayTerms();

			List<Object> entities = new ArrayList<>();

			entities.addAll(
				uadHierarchyDisplay.search(
					parentContainerKey, parentContainerId,
					selectedUser.getUserId(), groupIds,
					displayTerms.getKeywords(), null, null, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS));

			if (Objects.equals(String.valueOf(parentContainerId), "0")) {
				entities.addAll(
					uadHierarchyDisplay.search(
						parentContainerKey, -1L, selectedUser.getUserId(),
						groupIds, displayTerms.getKeywords(), null, null,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS));
			}

			LiferayPortletRequest liferayPortletRequest =
				PortalUtil.getLiferayPortletRequest(renderRequest);

			searchContainer.setResultsAndTotal(
				ListUtil.sort(
					TransformUtil.transform(
						entities,
						entity -> _constructHierarchyUADEntity(
							liferayPortletRequest, liferayPortletResponse,
							applicationKey, entity, selectedUser.getUserId(),
							uadHierarchyDisplay)),
					_getComparator(
						searchContainer.getOrderByCol(),
						searchContainer.getOrderByType())));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			searchContainer.setResultsAndTotal(Collections::emptyList, 0);
		}

		searchContainer.setRowChecker(
			new UADHierarchyChecker(
				liferayPortletResponse, uadHierarchyDisplay.getUADDisplays()));

		return searchContainer;
	}

	public static SearchContainer<UADEntity<?>> getUADEntitySearchContainer(
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest, PortletURL currentURL, long[] groupIds,
		User selectedUser, UADDisplay<Object> uadDisplay) {

		SearchContainer<UADEntity<?>> searchContainer =
			_constructSearchContainer(
				renderRequest, currentURL, "modifiedDate",
				uadDisplay.getSortingFieldNames());

		try {
			DisplayTerms displayTerms = searchContainer.getDisplayTerms();

			List<?> entities = uadDisplay.search(
				selectedUser.getUserId(), groupIds, displayTerms.getKeywords(),
				searchContainer.getOrderByCol(),
				searchContainer.getOrderByType(), searchContainer.getStart(),
				searchContainer.getEnd());

			LiferayPortletRequest liferayPortletRequest =
				PortalUtil.getLiferayPortletRequest(renderRequest);

			List<UADEntity<?>> uadEntities = new ArrayList<>();

			for (Object entity : entities) {
				uadEntities.add(
					_constructUADEntity(
						liferayPortletRequest, liferayPortletResponse, entity,
						uadDisplay));
			}

			searchContainer.setResultsAndTotal(
				() -> uadEntities,
				(int)uadDisplay.searchCount(
					selectedUser.getUserId(), groupIds,
					displayTerms.getKeywords()));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			searchContainer.setResultsAndTotal(Collections::emptyList, 0);
		}

		searchContainer.setRowChecker(
			new UADHierarchyChecker(
				liferayPortletResponse, new UADDisplay[] {uadDisplay}));

		return searchContainer;
	}

	private static <T> UADEntity<T> _constructApplicationSummaryUADEntity(
			LiferayPortletResponse liferayPortletResponse,
			RenderRequest renderRequest, PortletURL currentURL,
			UADApplicationSummaryDisplay uadApplicationSummaryDisplay)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		UADEntity<T> uadEntity = new UADEntity(
			null, uadApplicationSummaryDisplay.getApplicationKey(), null, false,
			null, true,
			PortletURLBuilder.create(
				PortletURLUtil.clone(currentURL, liferayPortletResponse)
			).setParameter(
				"applicationKey",
				uadApplicationSummaryDisplay.getApplicationKey()
			).buildString());

		uadEntity.addColumnEntry(
			"name",
			UADLanguageUtil.getApplicationName(
				uadApplicationSummaryDisplay.getApplicationKey(),
				themeDisplay.getLocale()));
		uadEntity.addColumnEntry(
			"count", uadApplicationSummaryDisplay.getCount());

		return uadEntity;
	}

	private static <T> UADEntity<T> _constructHierarchyUADEntity(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String applicationKey, T entity, long selectedUserId,
			UADHierarchyDisplay uadHierarchyDisplay)
		throws Exception {

		UADEntity<T> uadEntity = new UADEntity(
			uadHierarchyDisplay.unwrap(entity),
			uadHierarchyDisplay.getPrimaryKey(entity),
			uadHierarchyDisplay.getEditURL(
				liferayPortletRequest, liferayPortletResponse, entity),
			uadHierarchyDisplay.isInTrash(entity),
			uadHierarchyDisplay.getTypeKey(entity),
			uadHierarchyDisplay.isUserOwned(entity, selectedUserId),
			uadHierarchyDisplay.getViewURL(
				liferayPortletRequest, liferayPortletResponse, applicationKey,
				entity, selectedUserId));

		Map<String, Object> columnFieldValues =
			uadHierarchyDisplay.getFieldValues(
				entity, LocaleThreadLocal.getThemeDisplayLocale());

		for (Map.Entry<String, Object> entry : columnFieldValues.entrySet()) {
			uadEntity.addColumnEntry(
				entry.getKey(), SafeDisplayValueUtil.get(entry.getValue()));
		}

		return uadEntity;
	}

	private static SearchContainer<UADEntity<?>> _constructSearchContainer(
		RenderRequest renderRequest, PortletURL currentURL,
		String defaultOrderByCol, String[] sortingFieldNames) {

		DisplayTerms displayTerms = new DisplayTerms(renderRequest);

		SearchContainer<UADEntity<?>> searchContainer = new SearchContainer<>(
			renderRequest, displayTerms, displayTerms,
			SearchContainer.DEFAULT_CUR_PARAM,
			ParamUtil.getInteger(
				renderRequest, SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_CUR),
			SearchContainer.DEFAULT_DELTA, currentURL, null,
			"no-entities-remain-of-this-type", null);

		searchContainer.setId("uadEntities_" + StringUtil.randomId());
		searchContainer.setOrderableHeaders(
			new LinkedHashMap<String, String>() {
				{
					for (String orderByColumn : sortingFieldNames) {
						put(
							TextFormatter.format(
								orderByColumn, TextFormatter.K),
							orderByColumn);
					}
				}
			});

		String orderByCol = SearchOrderByUtil.getOrderByCol(
			renderRequest, UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			StringPool.BLANK);

		if (!ArrayUtil.contains(sortingFieldNames, orderByCol)) {
			orderByCol = defaultOrderByCol;
		}

		searchContainer.setOrderByCol(orderByCol);

		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				renderRequest,
				UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA, "asc"));

		return searchContainer;
	}

	private static UADEntity<?> _constructUADEntity(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, Object entity,
			UADDisplay<Object> uadDisplay)
		throws Exception {

		UADEntity<?> uadEntity = new UADEntity(
			entity, uadDisplay.getPrimaryKey(entity),
			uadDisplay.getEditURL(
				entity, liferayPortletRequest, liferayPortletResponse),
			uadDisplay.isInTrash(entity), uadDisplay.getTypeKey(), true, null);

		Map<String, Object> columnFieldValues = uadDisplay.getFieldValues(
			entity, uadDisplay.getColumnFieldNames(),
			LocaleThreadLocal.getThemeDisplayLocale());

		for (String columnFieldName : uadDisplay.getColumnFieldNames()) {
			uadEntity.addColumnEntry(
				columnFieldName,
				SafeDisplayValueUtil.get(
					columnFieldValues.get(columnFieldName)));
		}

		return uadEntity;
	}

	private static Comparator<UADEntity<?>> _getComparator(
		String orderByColumn, String orderByType) {

		Comparator<UADEntity<?>> comparator = Comparator.comparing(
			uadEntity -> {
				Object entry = uadEntity.getColumnEntry(orderByColumn);

				if (entry == null) {
					return "";
				}

				return (String)entry;
			});

		if (orderByColumn.equals("count")) {
			comparator = Comparator.comparingLong(
				uadEntity -> {
					try {
						return Long.valueOf(
							(String)uadEntity.getColumnEntry(orderByColumn));
					}
					catch (NumberFormatException numberFormatException) {
						if (_log.isDebugEnabled()) {
							_log.debug(numberFormatException);
						}

						return 0L;
					}
				});
		}

		if (orderByType.equals("desc")) {
			comparator = comparator.reversed();
		}

		return comparator;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UADSearchContainerBuilderUtil.class);

}