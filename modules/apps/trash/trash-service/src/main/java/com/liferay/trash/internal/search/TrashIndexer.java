/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.internal.search;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.model.TrashEntry;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 * @author Zsolt Berentey
 */
@Component(service = Indexer.class)
public class TrashIndexer extends BaseIndexer<TrashEntry> {

	public static final String CLASS_NAME = TrashEntry.class.getName();

	public TrashIndexer() {
		setDefaultSelectedFieldNames(
			Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.REMOVED_BY_USER_NAME, Field.REMOVED_DATE,
			Field.ROOT_ENTRY_CLASS_NAME, Field.ROOT_ENTRY_CLASS_PK, Field.UID);
		setFilterSearch(true);
		setPermissionAware(true);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public BooleanQuery getFullQuery(SearchContext searchContext)
		throws SearchException {

		try {
			BooleanFilter fullQueryBooleanFilter = new BooleanFilter();

			fullQueryBooleanFilter.addRequiredTerm(
				Field.COMPANY_ID, searchContext.getCompanyId());

			List<TrashHandler> trashHandlers =
				TrashHandlerRegistryUtil.getTrashHandlers();

			for (TrashHandler trashHandler : trashHandlers) {
				Filter filter = trashHandler.getExcludeFilter(searchContext);

				if (filter != null) {
					fullQueryBooleanFilter.add(
						filter, BooleanClauseOccur.MUST_NOT);
				}
			}

			long[] groupIds = searchContext.getGroupIds();

			if (ArrayUtil.isNotEmpty(groupIds)) {
				TermsFilter groupTermsFilter = new TermsFilter(Field.GROUP_ID);

				groupTermsFilter.addValues(ArrayUtil.toStringArray(groupIds));

				fullQueryBooleanFilter.add(
					groupTermsFilter, BooleanClauseOccur.MUST);
			}

			fullQueryBooleanFilter.addRequiredTerm(
				Field.STATUS, WorkflowConstants.STATUS_IN_TRASH);

			return createFullQuery(fullQueryBooleanFilter, searchContext);
		}
		catch (SearchException searchException) {
			throw searchException;
		}
		catch (Exception exception) {
			throw new SearchException(exception);
		}
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, String entryClassName,
			long entryClassPK, String actionId)
		throws Exception {

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			entryClassName);

		return trashHandler.hasTrashPermission(
			permissionChecker, 0, entryClassPK, actionId);
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		if (searchContext.getAttributes() == null) {
			return;
		}

		addSearchLocalizedTerm(searchQuery, searchContext, Field.CONTENT, true);
		addSearchLocalizedTerm(
			searchQuery, searchContext, Field.DESCRIPTION, true);
		addSearchTerm(
			searchQuery, searchContext, Field.REMOVED_BY_USER_NAME, true);
		addSearchLocalizedTerm(searchQuery, searchContext, Field.TITLE, true);
		addSearchTerm(searchQuery, searchContext, Field.TYPE, false);
		addSearchTerm(searchQuery, searchContext, Field.USER_NAME, true);

		Group group = null;

		long[] groupIds = searchContext.getGroupIds();

		if ((groupIds != null) && (groupIds.length > 0)) {
			group = _groupLocalService.fetchGroup(groupIds[0]);
		}

		if ((group == null) ||
			Objects.equals(
				group.getDefaultLanguageId(), searchContext.getLanguageId())) {

			return;
		}

		addSearchTerm(
			searchQuery, searchContext,
			_localization.getLocalizedName(
				Field.DESCRIPTION, group.getDefaultLanguageId()),
			true);
		addSearchTerm(
			searchQuery, searchContext,
			_localization.getLocalizedName(
				Field.CONTENT, group.getDefaultLanguageId()),
			true);
		addSearchTerm(
			searchQuery, searchContext,
			_localization.getLocalizedName(
				Field.TITLE, group.getDefaultLanguageId()),
			true);
	}

	@Override
	protected void doDelete(TrashEntry trashEntry) {
	}

	@Override
	protected Document doGetDocument(TrashEntry trashEntry) {
		return null;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return null;
	}

	@Override
	protected void doReindex(String className, long classPK) {
	}

	@Override
	protected void doReindex(String[] ids) {
	}

	@Override
	protected void doReindex(TrashEntry trashEntry) {
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Localization _localization;

}