/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

/**
 * @author Carolina Barbosa
 */
public class AutocompleteAssigneeMVCResourceCommand
	extends BaseMVCResourceCommand {

	public AutocompleteAssigneeMVCResourceCommand(
		Queries queries, RoleLocalService roleLocalService, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		UserLocalService userLocalService) {

		_queries = queries;
		_roleLocalService = roleLocalService;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				themeDisplay.getCompanyId()
			).emptySearchEnabled(
				true
			).entryClassNames(
				Role.class.getName(), User.class.getName()
			).query(
				_getBooleanQuery(
					themeDisplay.getCompanyId(),
					ParamUtil.getString(resourceRequest, "search"))
			).size(
				20
			).build());

		for (Document document : searchResponse.getDocuments()) {
			String name = document.getString(Field.NAME);

			String type = StringUtil.extractLast(
				document.getString(Field.ENTRY_CLASS_NAME), StringPool.PERIOD);

			if (StringUtil.equals(type, "User")) {
				name = document.getString("fullName");
			}

			if (Validator.isNull(name)) {
				continue;
			}

			jsonArray.put(
				JSONUtil.put(
					"externalReferenceCode",
					document.getString("externalReferenceCode")
				).put(
					"image",
					() -> {
						if (!StringUtil.equals(type, "User")) {
							return null;
						}

						User user = _userLocalService.fetchUser(
							document.getLong(Field.ENTRY_CLASS_PK));

						if (user == null) {
							return null;
						}

						return user.getPortraitURL(themeDisplay);
					}
				).put(
					"name", name
				).put(
					"type", type
				));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put("items", jsonArray));
	}

	private BooleanQuery _getBooleanQuery(long companyId, String search)
		throws Exception {

		BooleanQuery booleanQuery = _queries.booleanQuery();

		BooleanQuery roleBooleanQuery = _queries.booleanQuery();

		Role guestRole = _roleLocalService.getRole(
			companyId, RoleConstants.GUEST);

		roleBooleanQuery.addMustNotQueryClauses(
			_queries.term(Field.ENTRY_CLASS_PK, guestRole.getRoleId()));

		if (Validator.isNull(search)) {
			return booleanQuery.addShouldQueryClauses(roleBooleanQuery);
		}

		roleBooleanQuery.addMustQueryClauses(
			_queries.matchPhrasePrefix(Field.NAME, search),
			_queries.term(Field.ENTRY_CLASS_NAME, Role.class.getName()));

		BooleanQuery userBooleanQuery = _queries.booleanQuery();

		userBooleanQuery.addMustQueryClauses(
			_queries.matchPhrasePrefix("fullName", search),
			_queries.term(Field.ENTRY_CLASS_NAME, User.class.getName()));

		return booleanQuery.addShouldQueryClauses(
			roleBooleanQuery, userBooleanQuery);
	}

	private final Queries _queries;
	private final RoleLocalService _roleLocalService;
	private final Searcher _searcher;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final UserLocalService _userLocalService;

}