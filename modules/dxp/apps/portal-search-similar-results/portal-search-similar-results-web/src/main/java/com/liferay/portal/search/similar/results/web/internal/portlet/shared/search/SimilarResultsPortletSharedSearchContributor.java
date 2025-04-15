/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.portlet.shared.search;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.query.MoreLikeThisQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.similar.results.web.internal.builder.SimilarResultsContributorsRegistry;
import com.liferay.portal.search.similar.results.web.internal.builder.SimilarResultsRoute;
import com.liferay.portal.search.similar.results.web.internal.constants.SimilarResultsPortletKeys;
import com.liferay.portal.search.similar.results.web.internal.contributor.SimilarResultsContributor;
import com.liferay.portal.search.similar.results.web.internal.portlet.SimilarResultsPortletPreferences;
import com.liferay.portal.search.similar.results.web.internal.portlet.SimilarResultsPortletPreferencesImpl;
import com.liferay.portal.search.similar.results.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaHelper;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(
	property = "jakarta.portlet.name=" + SimilarResultsPortletKeys.SIMILAR_RESULTS,
	service = PortletSharedSearchContributor.class
)
public class SimilarResultsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SimilarResultsRoute similarResultsRoute =
			_similarResultsContributorsRegistry.detectRoute(
				_getURLString(portletSharedSearchSettings));

		if (similarResultsRoute == null) {
			return;
		}

		SimilarResultsContributor similarResultsContributor =
			similarResultsRoute.getContributor();

		CriteriaBuilderImpl criteriaBuilderImpl = new CriteriaBuilderImpl();

		CriteriaHelper criteriaHelper = new CriteriaHelperImpl(
			getGroupId(portletSharedSearchSettings), similarResultsRoute);

		similarResultsContributor.resolveCriteria(
			criteriaBuilderImpl, criteriaHelper);

		Criteria criteria = criteriaBuilderImpl.build();

		if (criteria != null) {
			contribute(criteria, portletSharedSearchSettings);
		}
	}

	@Activate
	protected void activate() {
		_similarResultsContributorsRegistry =
			new SimilarResultsContributorsRegistry(
				_assetEntryLocalService, _blogsEntryLocalService,
				_dlFileEntryLocalService, _dlFolderLocalService,
				_mbCategoryLocalService, _mbMessageLocalService, _uidFactory,
				_wikiNodeLocalService, _wikiPageLocalService);
	}

	protected void contribute(
		Criteria criteria,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SimilarResultsPortletPreferences similarResultsPortletPreferences =
			new SimilarResultsPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				similarResultsPortletPreferences.getFederatedSearchKey());

		_filterByEntryClassName(
			criteria, portletSharedSearchSettings, searchRequestBuilder);

		_filterByGroupId(
			searchRequestBuilder, similarResultsPortletPreferences,
			portletSharedSearchSettings);

		searchRequestBuilder.query(
			_getMoreLikeThisQuery(
				criteria.getUID(), similarResultsPortletPreferences)
		).emptySearchEnabled(
			true
		).size(
			similarResultsPortletPreferences.getMaxItemDisplay()
		);

		_setUIDRenderRequestAttribute(criteria, portletSharedSearchSettings);
	}

	protected long getGroupId(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ThemeDisplay themeDisplay =
			portletSharedSearchSettings.getThemeDisplay();

		return themeDisplay.getScopeGroupId();
	}

	protected long[] getGroupIds(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ThemeDisplay themeDisplay =
			portletSharedSearchSettings.getThemeDisplay();

		try {
			List<Long> groupIds = new ArrayList<>();

			groupIds.add(themeDisplay.getScopeGroupId());

			List<Group> groups = _groupLocalService.getGroups(
				themeDisplay.getCompanyId(), Layout.class.getName(),
				themeDisplay.getScopeGroupId());

			for (Group group : groups) {
				groupIds.add(group.getGroupId());
			}

			return ArrayUtil.toLongArray(groupIds);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return new long[] {themeDisplay.getScopeGroupId()};
		}
	}

	private void _filterByEntryClassName(
		Criteria criteria,
		PortletSharedSearchSettings portletSharedSearchSettings,
		SearchRequestBuilder searchRequestBuilder) {

		String parameterValue = portletSharedSearchSettings.getParameter(
			"similar.results.all.classes");

		if (parameterValue != null) {
			return;
		}

		String className = criteria.getType();

		if (!Validator.isBlank(className)) {
			searchRequestBuilder.addComplexQueryPart(
				_getComplexQueryPart(_getEntryClassNameQuery(className)));
		}
	}

	private void _filterByGroupId(
		SearchRequestBuilder searchRequestBuilder,
		SimilarResultsPortletPreferences similarResultsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		if (Objects.equals(
				similarResultsPortletPreferences.getSearchScope(),
				"this-site")) {

			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setGroupIds(
					getGroupIds(portletSharedSearchSettings)));
		}
	}

	private ComplexQueryPart _getComplexQueryPart(Query query) {
		return _complexQueryPartBuilderFactory.builder(
		).query(
			query
		).build();
	}

	private Query _getEntryClassNameQuery(String entryClassName) {
		return _queries.term(Field.ENTRY_CLASS_NAME, entryClassName);
	}

	private MoreLikeThisQuery _getMoreLikeThisQuery(
		String uid,
		SimilarResultsPortletPreferences similarResultsPortletPreferences) {

		MoreLikeThisQuery moreLikeThisQuery = _queries.moreLikeThis(
			Collections.singleton(
				_queries.documentIdentifier(
					similarResultsPortletPreferences.getIndexName(),
					similarResultsPortletPreferences.getDocType(), uid)));

		_populate(moreLikeThisQuery, similarResultsPortletPreferences);

		return moreLikeThisQuery;
	}

	private String _getURLString(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		return _portal.getCurrentURL(
			portletSharedSearchSettings.getRenderRequest());
	}

	private void _populate(
		MoreLikeThisQuery moreLikeThisQuery,
		SimilarResultsPortletPreferences similarResultsPortletPreferences) {

		String fields = similarResultsPortletPreferences.getFields();

		if (!Validator.isBlank(fields)) {
			moreLikeThisQuery.addFields(
				SearchStringUtil.splitAndUnquote(
					SearchStringUtil.maybe(fields)));
		}

		String stopWords = similarResultsPortletPreferences.getStopWords();

		if (!Validator.isBlank(stopWords)) {
			moreLikeThisQuery.addStopWords(
				SearchStringUtil.splitAndUnquote(
					SearchStringUtil.maybe(StringUtil.toLowerCase(stopWords))));
		}

		moreLikeThisQuery.setAnalyzer(
			similarResultsPortletPreferences.getAnalyzer());
		moreLikeThisQuery.setMaxDocFrequency(
			similarResultsPortletPreferences.getMaxDocFrequency());
		moreLikeThisQuery.setMaxQueryTerms(
			similarResultsPortletPreferences.getMaxQueryTerms());
		moreLikeThisQuery.setMaxWordLength(
			similarResultsPortletPreferences.getMaxWordLength());
		moreLikeThisQuery.setMinDocFrequency(
			similarResultsPortletPreferences.getMinDocFrequency());
		moreLikeThisQuery.setMinShouldMatch(
			similarResultsPortletPreferences.getMinShouldMatch());
		moreLikeThisQuery.setMinTermFrequency(
			similarResultsPortletPreferences.getMinTermFrequency());
		moreLikeThisQuery.setMinWordLength(
			similarResultsPortletPreferences.getMinWordLength());
		moreLikeThisQuery.setTermBoost(
			similarResultsPortletPreferences.getTermBoost());
	}

	private void _setUIDRenderRequestAttribute(
		Criteria criteria,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		RenderRequest renderRequest =
			portletSharedSearchSettings.getRenderRequest();

		renderRequest.setAttribute(Field.UID, criteria.getUID());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SimilarResultsPortletSharedSearchContributor.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private Queries _queries;

	private SimilarResultsContributorsRegistry
		_similarResultsContributorsRegistry;

	@Reference
	private UIDFactory _uidFactory;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}