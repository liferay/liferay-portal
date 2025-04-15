/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.search;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.CerebroEngineClient;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.model.display.main.FaroSubscriptionDisplay;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Date;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = Indexer.class)
public class FaroProjectIndexer extends BaseIndexer<FaroProject> {

	public static final String CLASS_NAME = FaroProject.class.getName();

	public FaroProjectIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.GROUP_ID, Field.NAME, Field.UID, Field.USER_ID,
			"corpProjectName", "corpProjectUuid", "createDate",
			"dataSourceConnected", "individualsLimit", "individualsUsage",
			"lastAccessDate", "offline", "pageViewsLimit", "pageViewsUsage",
			"subscription", "subscriptionName");
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		if (Validator.isNotNull(searchContext.getKeywords())) {
			addSearchTerm(searchQuery, searchContext, Field.GROUP_ID, false);
			addSearchTerm(searchQuery, searchContext, Field.NAME, true);
			addSearchTerm(searchQuery, searchContext, "corpProjectName", true);
			addSearchTerm(searchQuery, searchContext, "corpProjectUuid", false);
			addSearchTerm(searchQuery, searchContext, "weDeployKey", false);
		}

		long faroProjectId = GetterUtil.getLong(
			searchContext.getAttribute("faroProjectId"));

		if (faroProjectId > 0) {
			fullQueryBooleanFilter.addRequiredTerm(
				Field.ENTRY_CLASS_PK, faroProjectId);
		}

		boolean inactive = GetterUtil.getBoolean(
			searchContext.getAttribute("inactive"));

		if (inactive) {
			BooleanFilter booleanFilter = new BooleanFilter();

			booleanFilter.addRangeTerm(
				"lastAccessDate_sortable", 0,
				System.currentTimeMillis() - (3 * Time.MONTH));

			fullQueryBooleanFilter.add(booleanFilter, BooleanClauseOccur.MUST);
		}

		long maxUsage = GetterUtil.getLong(
			searchContext.getAttribute("maxUsage"));
		long minUsage = GetterUtil.getLong(
			searchContext.getAttribute("minUsage"));

		if ((maxUsage > 0) && (minUsage > 0)) {
			BooleanFilter booleanFilter = new BooleanFilter();

			BooleanFilter individualsBooleanFilter = new BooleanFilter();

			individualsBooleanFilter.addRangeTerm(
				"individualsUsage_sortable", minUsage, maxUsage);

			booleanFilter.add(individualsBooleanFilter);

			BooleanFilter pageViewsBooleanFilter = new BooleanFilter();

			pageViewsBooleanFilter.addRangeTerm(
				"pageViewsUsage_sortable", minUsage, maxUsage);

			booleanFilter.add(pageViewsBooleanFilter);

			fullQueryBooleanFilter.add(booleanFilter, BooleanClauseOccur.MUST);
		}

		boolean offline = GetterUtil.getBoolean(
			searchContext.getAttribute("offline"));

		if (offline) {
			fullQueryBooleanFilter.addRequiredTerm("offline", true);
		}

		String subscriptionName = GetterUtil.getString(
			searchContext.getAttribute("subscriptionName"));

		if (Validator.isNotNull(subscriptionName)) {
			fullQueryBooleanFilter.addRequiredTerm(
				"subscriptionName", StringUtil.toLowerCase(subscriptionName));
		}
	}

	@Override
	protected void doDelete(FaroProject faroProject) throws Exception {
		deleteDocument(0, faroProject.getFaroProjectId());
	}

	@Override
	protected Document doGetDocument(FaroProject faroProject) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Begin reindex " + faroProject.getGroupId());
		}

		Document document = getBaseModelDocument(CLASS_NAME, faroProject);

		document.addKeyword(Field.GROUP_ID, faroProject.getGroupId());
		document.addKeyword(Field.NAME, faroProject.getName());
		document.addKeyword(Field.USER_ID, faroProject.getUserId());
		document.addKeyword(
			"corpProjectName", faroProject.getCorpProjectName());
		document.addKeyword(
			"corpProjectUuid", faroProject.getCorpProjectUuid());
		document.addDate("createDate", new Date(faroProject.getCreateTime()));
		document.addKeyword(
			"dataSourceConnected", faroProject.isDataSourceConnected());

		FaroSubscriptionDisplay faroSubscriptionDisplay = JSONUtil.readValue(
			faroProject.getSubscription(), FaroSubscriptionDisplay.class);

		document.addNumber(
			"individualsLimit", faroSubscriptionDisplay.getIndividualsLimit());
		document.addNumber(
			"individualsUsage",
			_getUsage(
				faroSubscriptionDisplay.
					getIndividualsCountSinceLastAnniversary(),
				faroSubscriptionDisplay.getIndividualsLimit()));
		document.addDate(
			"lastAnniversaryDate",
			faroSubscriptionDisplay.getLastAnniversaryDate());

		document.addDate(
			"lastAccessDate", new Date(faroProject.getLastAccessTime()));

		try {
			if (!StringUtil.equals(
					faroProject.getState(), FaroProjectConstants.STATE_READY)) {

				document.addKeyword("offline", StringPool.TRUE);
			}
			else {
				faroSubscriptionDisplay.setCounts(
					faroProject, _cerebroEngineClient, _contactsEngineClient);

				if (_log.isInfoEnabled()) {
					_log.info(
						"Finished setting counts " + faroProject.getGroupId());
				}
			}
		}
		catch (Exception exception) {
			document.addKeyword("offline", StringPool.TRUE);

			_log.error(exception);
		}

		document.addNumber(
			"pageViewsLimit", faroSubscriptionDisplay.getPageViewsLimit());
		document.addNumber(
			"pageViewsUsage",
			_getUsage(
				faroSubscriptionDisplay.getPageViewsCountSinceLastAnniversary(),
				faroSubscriptionDisplay.getPageViewsLimit()));
		document.addKeyword(
			"subscription",
			JSONUtil.writeValueAsString(faroSubscriptionDisplay));
		document.addKeyword(
			"subscriptionName",
			StringUtil.removeSubstring(
				faroSubscriptionDisplay.getName(), "Liferay Analytics Cloud "));

		if (Validator.isNull(faroProject.getWeDeployKey())) {
			if (_log.isInfoEnabled()) {
				_log.info("WeDeployKey is null " + faroProject.getGroupId());
			}

			return document;
		}

		document.addKeyword("weDeployKey", faroProject.getWeDeployKey());

		if (_log.isInfoEnabled()) {
			_log.info("Finished reindex " + faroProject.getGroupId());
		}

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return null;
	}

	@Override
	protected void doReindex(FaroProject faroProject) throws Exception {
		_indexWriterHelper.updateDocument(0L, getDocument(faroProject));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(_faroProjectLocalService.getFaroProject(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_faroProjectLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod<FaroProject>)
				faroProject -> {
					try {
						Document document = getDocument(faroProject);

						if (document != null) {
							indexableActionableDynamicQuery.addDocuments(
								document);
						}
					}
					catch (PortalException portalException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to index faro project " +
									faroProject.getFaroProjectId(),
								portalException);
						}
					}
				});

		indexableActionableDynamicQuery.performActions();
	}

	private double _getUsage(long count, long limit) {
		if ((count == 0) || (limit == 0)) {
			return 0.0;
		}

		double usage = 100.0 * count / limit;

		return Math.round(usage * 100) / 100.0;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroProjectIndexer.class);

	@Reference
	private CerebroEngineClient _cerebroEngineClient;

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

}