/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.provider;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.context.Context;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributorRegistry;
import com.liferay.segments.internal.checker.UserSegmentsEntryMembershipChecker;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.odata.matcher.ODataMatcher;
import com.liferay.segments.odata.retriever.ODataRetriever;
import com.liferay.segments.provider.SegmentsEntryProvider;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
public abstract class BaseSegmentsEntryProvider
	implements SegmentsEntryProvider {

	@Override
	public long[] getSegmentsEntryClassPKs(
			long segmentsEntryId, int start, int end)
		throws PortalException {

		SegmentsEntry segmentsEntry =
			segmentsEntryLocalService.fetchSegmentsEntry(segmentsEntryId);

		if (segmentsEntry == null) {
			return new long[0];
		}

		String filterString = getFilterString(
			segmentsEntry, Criteria.Type.MODEL);

		if (Validator.isNull(filterString)) {
			return TransformUtil.transformToLongArray(
				segmentsEntryRelLocalService.getSegmentsEntryRels(
					segmentsEntryId, start, end, null),
				SegmentsEntryRel::getClassPK);
		}

		return TransformUtil.transformToLongArray(
			userODataRetriever.getResults(
				segmentsEntry.getCompanyId(), filterString,
				LocaleUtil.getDefault(), start, end),
			baseModel -> (Long)baseModel.getPrimaryKeyObj());
	}

	@Override
	public int getSegmentsEntryClassPKsCount(long segmentsEntryId)
		throws PortalException {

		SegmentsEntry segmentsEntry =
			segmentsEntryLocalService.fetchSegmentsEntry(segmentsEntryId);

		if (segmentsEntry == null) {
			return 0;
		}

		String filterString = getFilterString(
			segmentsEntry, Criteria.Type.MODEL);

		if (Validator.isNull(filterString)) {
			return segmentsEntryRelLocalService.getSegmentsEntryRelsCount(
				segmentsEntryId);
		}

		return userODataRetriever.getResultsCount(
			segmentsEntry.getCompanyId(), filterString,
			LocaleUtil.getDefault());
	}

	@Override
	public long[] getSegmentsEntryIds(
			long groupId, String className, long classPK, Context context)
		throws PortalException {

		return getSegmentsEntryIds(
			groupId, className, classPK, context, new long[0], new long[0]);
	}

	@Override
	public long[] getSegmentsEntryIds(
		long groupId, String className, long classPK, Context context,
		long[] filterSegmentsEntryIds, long[] segmentsEntryIds) {

		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyConstants.SYSTEM, "LPD-78863")) {

			return new long[0];
		}

		List<SegmentsEntry> segmentsEntries = new ArrayList<>();

		if (ArrayUtil.isNotEmpty(filterSegmentsEntryIds)) {
			segmentsEntries = segmentsEntryLocalService.getSegmentsEntries(
				filterSegmentsEntryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}

		if (segmentsEntries.isEmpty()) {
			segmentsEntries = segmentsEntryLocalService.getSegmentsEntries(
				groupId, getSource(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);
		}

		if (segmentsEntries.isEmpty()) {
			return new long[0];
		}

		User user = userLocalService.fetchUser(classPK);

		if ((user == null) ||
			(user.getType() == UserConstants.TYPE_DEFAULT_SERVICE_ACCOUNT)) {

			return new long[0];
		}

		try {
			Map<String, Object> userAttributes = _getUserAttributes(user);

			return TransformUtil.transformToLongArray(
				segmentsEntries,
				segmentsEntry -> {
					if (isMember(
							className, classPK, context, segmentsEntry,
							segmentsEntryIds, userAttributes)) {

						return segmentsEntry.getSegmentsEntryId();
					}

					return null;
				});
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return new long[0];
	}

	protected Criteria.Conjunction getConjunction(
		SegmentsEntry segmentsEntry, Criteria.Type type) {

		Criteria existingCriteria = segmentsEntry.getCriteriaObj();

		if (existingCriteria == null) {
			return Criteria.Conjunction.AND;
		}

		return existingCriteria.getTypeConjunction(type);
	}

	protected String getFilterString(
		SegmentsEntry segmentsEntry, Criteria.Type type) {

		Criteria existingCriteria = segmentsEntry.getCriteriaObj();

		if (existingCriteria == null) {
			return null;
		}

		Criteria criteria = new Criteria();

		for (SegmentsCriteriaContributor segmentsCriteriaContributor :
				segmentsCriteriaContributorRegistry.
					getSegmentsCriteriaContributors()) {

			Criteria.Criterion criterion =
				segmentsCriteriaContributor.getCriterion(existingCriteria);

			if (criterion == null) {
				continue;
			}

			segmentsCriteriaContributor.contribute(
				criteria, criterion.getFilterString(),
				Criteria.Conjunction.parse(criterion.getConjunction()));
		}

		return criteria.getFilterString(type);
	}

	protected abstract String getSource();

	protected boolean isMember(
		String className, long classPK, Context context,
		SegmentsEntry segmentsEntry, long[] segmentsEntryIds,
		Map<String, Object> userAttributes) {

		String contextFilterString = getFilterString(
			segmentsEntry, Criteria.Type.CONTEXT);
		String modelFilterString = getFilterString(
			segmentsEntry, Criteria.Type.MODEL);

		if (ArrayUtil.contains(
				(long[])userAttributes.get("segmentsEntryIds"),
				segmentsEntry.getSegmentsEntryId()) &&
			Validator.isNull(contextFilterString) &&
			Validator.isNull(modelFilterString)) {

			return true;
		}

		Criteria criteria = segmentsEntry.getCriteriaObj();

		if ((criteria == null) || MapUtil.isEmpty(criteria.getCriteria())) {
			return false;
		}

		Criteria.Conjunction contextConjunction = getConjunction(
			segmentsEntry, Criteria.Type.CONTEXT);

		if ((context != null) && Validator.isNotNull(contextFilterString)) {
			boolean guestUser = !GetterUtil.getBoolean(
				context.get(Context.SIGNED_IN), true);

			if (contextConjunction.equals(Criteria.Conjunction.AND) &&
				guestUser && Validator.isNotNull(modelFilterString)) {

				return false;
			}

			boolean matchesContext = false;

			try {
				matchesContext = oDataMatcher.matches(
					contextFilterString, context);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}

			if (matchesContext &&
				contextConjunction.equals(Criteria.Conjunction.OR)) {

				return true;
			}

			if (!matchesContext &&
				contextConjunction.equals(Criteria.Conjunction.AND)) {

				return false;
			}

			if (guestUser) {
				return matchesContext;
			}
		}

		if (Validator.isNotNull(modelFilterString)) {
			boolean matchesModel = false;

			try {
				matchesModel = UserSegmentsEntryMembershipChecker.isMember(
					StringBundler.concat(
						"(", modelFilterString, ") and (classPK eq CLASS_PK)"),
					userAttributes);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			Criteria.Conjunction modelConjunction = getConjunction(
				segmentsEntry, Criteria.Type.MODEL);

			if (matchesModel &&
				modelConjunction.equals(Criteria.Conjunction.OR)) {

				return true;
			}

			if (!matchesModel &&
				modelConjunction.equals(Criteria.Conjunction.AND)) {

				return false;
			}
		}

		return true;
	}

	@Reference
	protected AssetCategoryLocalService assetCategoryLocalService;

	@Reference
	protected AssetTagLocalService assetTagLocalService;

	@Reference
	protected ClassNameLocalService classNameLocalService;

	@Reference
	protected ExpandoColumnLocalService expandoColumnLocalService;

	@Reference
	protected ExpandoTableLocalService expandoTableLocalService;

	@Reference
	protected ExpandoValueLocalService expandoValueLocalService;

	@Reference(
		target = "(target.class.name=com.liferay.segments.context.Context)"
	)
	protected ODataMatcher<Context> oDataMatcher;

	@Reference
	protected Portal portal;

	@Reference
	protected SegmentsCriteriaContributorRegistry
		segmentsCriteriaContributorRegistry;

	@Reference
	protected SegmentsEntryLocalService segmentsEntryLocalService;

	@Reference
	protected SegmentsEntryRelLocalService segmentsEntryRelLocalService;

	@Reference
	protected UserLocalService userLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.User)"
	)
	protected ODataRetriever<User> userODataRetriever;

	private long[] _getSegmentsEntryIds(User user) throws Exception {
		return TransformUtil.transformToLongArray(
			segmentsEntryRelLocalService.getSegmentsEntryRels(
				portal.getClassNameId(User.class), user.getUserId()),
			SegmentsEntryRel::getSegmentsEntryId);
	}

	private Map<String, Object> _getUserAttributes(User user) throws Exception {
		Map<String, String> expandoValues = new HashMap<>();

		ExpandoTable expandoTable = expandoTableLocalService.fetchTable(
			user.getCompanyId(),
			classNameLocalService.getClassNameId(User.class.getName()),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable != null) {
			List<ExpandoColumn> expandoColumns =
				expandoColumnLocalService.getColumns(expandoTable.getTableId());

			for (ExpandoColumn expandoColumn : expandoColumns) {
				ExpandoValue expandoValue = expandoValueLocalService.getValue(
					expandoTable.getTableId(), expandoColumn.getColumnId(),
					user.getUserId());

				String expandoColumnName = expandoColumn.getName();

				String key = StringBundler.concat(
					"customField/_", expandoColumn.getColumnId(),
					StringPool.UNDERLINE,
					StringUtil.replace(
						expandoColumnName.replaceAll(
							":|;|'|\"", StringPool.BLANK),
						CharPool.SPACE, CharPool.UNDERLINE));

				if (expandoValue != null) {
					expandoValues.put(key, expandoValue.getData());
				}
				else {
					expandoValues.put(key, StringPool.BLANK);
				}
			}
		}

		return HashMapBuilder.<String, Object>putAll(
			user.getModelAttributes()
		).putAll(
			expandoValues
		).put(
			Field.ASSET_CATEGORY_IDS,
			TransformUtil.transform(
				assetCategoryLocalService.getCategories(
					portal.getClassNameId(User.class), user.getUserId()),
				assetCategory -> assetCategory.getCategoryId()
			).toArray(
				new Long[0]
			)
		).put(
			Field.ASSET_TAG_IDS,
			TransformUtil.transform(
				assetTagLocalService.getTags(
					portal.getClassNameId(User.class), user.getUserId()),
				assetTag -> assetTag.getTagId()
			).toArray(
				new Long[0]
			)
		).put(
			"birthDate",
			() -> {
				try {
					return user.getBirthday();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					return new Date(0);
				}
			}
		).put(
			"classPK", user.getUserId()
		).put(
			"groupIds", user.getGroupIds()
		).put(
			"organizationIds", user.getOrganizationIds()
		).put(
			"roleIds", user.getRoleIds()
		).put(
			"segmentsEntryIds", _getSegmentsEntryIds(user)
		).put(
			"teamIds", user.getTeamIds()
		).put(
			"userGroupIds", user.getUserGroupIds()
		).put(
			"userGroupRoleIds",
			TransformUtil.transform(
				user.getUserGroupRoles(), role -> role.getRoleId()
			).toArray(
				new Long[0]
			)
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSegmentsEntryProvider.class);

}