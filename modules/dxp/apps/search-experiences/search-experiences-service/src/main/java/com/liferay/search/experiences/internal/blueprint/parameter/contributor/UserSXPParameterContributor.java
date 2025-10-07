/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.parameter.contributor;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.kernel.service.permission.ExpandoColumnPermissionUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.search.experiences.blueprint.parameter.SXPParameter;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributor;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributorDefinition;
import com.liferay.search.experiences.internal.blueprint.parameter.BooleanArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.BooleanSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.DateSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.DoubleArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.DoubleSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.FloatArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.FloatSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.IntegerArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.IntegerSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.LongArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.LongSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.StringArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.StringSXPParameter;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.context.Context;

import java.beans.ExceptionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Petteri Karttunen
 */
public class UserSXPParameterContributor implements SXPParameterContributor {

	public UserSXPParameterContributor(
		AssetCategoryLocalService assetCategoryLocalService,
		AssetTagLocalService assetTagLocalService,
		ExpandoColumnLocalService expandoColumnLocalService,
		ExpandoValueLocalService expandoValueLocalService,
		GroupLocalService groupLocalService, Language language, Portal portal,
		SegmentsEntryRetriever segmentsEntryRetriever,
		UserGroupGroupRoleLocalService userGroupGroupRoleLocalService,
		UserGroupLocalService userGroupLocalService,
		UserGroupRoleLocalService userGroupRoleLocalService,
		UserLocalService userLocalService) {

		_assetCategoryLocalService = assetCategoryLocalService;
		_assetTagLocalService = assetTagLocalService;
		_expandoColumnLocalService = expandoColumnLocalService;
		_expandoValueLocalService = expandoValueLocalService;
		_groupLocalService = groupLocalService;
		_language = language;
		_portal = portal;
		_segmentsEntryRetriever = segmentsEntryRetriever;
		_userGroupGroupRoleLocalService = userGroupGroupRoleLocalService;
		_userGroupLocalService = userGroupLocalService;
		_userGroupRoleLocalService = userGroupRoleLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public void contribute(
		ExceptionListener exceptionListener, SearchContext searchContext,
		Set<SXPParameter> sxpParameters) {

		try {
			_contribute(searchContext, sxpParameters);
		}
		catch (PortalException portalException) {
			exceptionListener.exceptionThrown(portalException);

			_log.error(portalException);
		}
	}

	@Override
	public String getSXPParameterCategoryNameKey() {
		return "user";
	}

	@Override
	public List<SXPParameterContributorDefinition>
		getSXPParameterContributorDefinitions(long companyId, Locale locale) {

		return _getSXPParameterContributorDefinitions(
			companyId, locale,
			ListUtil.fromArray(
				new SXPParameterContributorDefinition(
					LongArraySXPParameter.class, "active-segment-entry-ids",
					"user.active_segment_entry_ids"),
				new SXPParameterContributorDefinition(
					IntegerSXPParameter.class, "age", "user.age"),
				new SXPParameterContributorDefinition(
					LongArraySXPParameter.class, "asset-category-ids",
					"user.asset_category_ids"),
				new SXPParameterContributorDefinition(
					StringArraySXPParameter.class, "asset-tag-names",
					"user.asset_tag_names"),
				new SXPParameterContributorDefinition(
					DateSXPParameter.class, "birthday", "user.birthday"),
				new SXPParameterContributorDefinition(
					DateSXPParameter.class, "create-date", "user.create_date"),
				new SXPParameterContributorDefinition(
					LongArraySXPParameter.class, "current-site-role-ids",
					"user.current_site_role_ids"),
				new SXPParameterContributorDefinition(
					StringSXPParameter.class, "email-domain",
					"user.email_domain"),
				new SXPParameterContributorDefinition(
					StringSXPParameter.class, "external-reference-code",
					"user.external_reference_code"),
				new SXPParameterContributorDefinition(
					StringSXPParameter.class, "first-name", "user.first_name"),
				new SXPParameterContributorDefinition(
					StringSXPParameter.class, "full-name", "user.full_name"),
				new SXPParameterContributorDefinition(
					StringArraySXPParameter.class,
					"group-external-reference-codes",
					"user.group_external_reference_codes"),
				new SXPParameterContributorDefinition(
					LongArraySXPParameter.class, "group-ids", "user.group_ids"),
				new SXPParameterContributorDefinition(
					LongSXPParameter.class, "user-id", "user.id"),
				new SXPParameterContributorDefinition(
					BooleanSXPParameter.class, "is-female", "user.is_female"),
				new SXPParameterContributorDefinition(
					BooleanSXPParameter.class, "is-gender-x",
					"user.is_gender_x"),
				new SXPParameterContributorDefinition(
					BooleanSXPParameter.class, "is-male", "user.is_male"),
				new SXPParameterContributorDefinition(
					BooleanSXPParameter.class, "is-omniadmin",
					"user.is_omniadmin"),
				new SXPParameterContributorDefinition(
					BooleanSXPParameter.class, "is-signed-in",
					"user.is_signed_in"),
				new SXPParameterContributorDefinition(
					StringSXPParameter.class, "job-title", "user.job_title"),
				new SXPParameterContributorDefinition(
					StringSXPParameter.class, "language-id",
					"user.language_id"),
				new SXPParameterContributorDefinition(
					StringSXPParameter.class, "last-name", "user.last_name"),
				new SXPParameterContributorDefinition(
					LongArraySXPParameter.class, "parent-asset-category-ids",
					"user.parent_asset_category_ids"),
				new SXPParameterContributorDefinition(
					LongArraySXPParameter.class, "regular-role-ids",
					"user.regular_role_ids"),
				new SXPParameterContributorDefinition(
					StringArraySXPParameter.class,
					"user-group-external-reference-codes",
					"user.user_group_external_reference_codes"),
				new SXPParameterContributorDefinition(
					LongArraySXPParameter.class, "user-group-ids",
					"user.user_group_ids")));
	}

	private void _addAssetCategories(
		Set<SXPParameter> sxpParameters, User user) {

		List<AssetCategory> assetCategories =
			_assetCategoryLocalService.getCategories(
				user.getModelClassName(), user.getPrimaryKey());

		if (assetCategories.isEmpty()) {
			return;
		}

		List<Long> assetCategoryIds = new ArrayList<>();
		List<Long> parentAssetCategoryIds = new ArrayList<>();

		for (AssetCategory assetCategory : assetCategories) {
			assetCategoryIds.add(assetCategory.getCategoryId());

			if (assetCategory.getParentCategoryId() > 0) {
				parentAssetCategoryIds.addAll(
					_getParentAssetCategoryIds(
						assetCategory.getParentCategory(),
						new ArrayList<Long>()));
			}
		}

		sxpParameters.add(
			new LongArraySXPParameter(
				"user.asset_category_ids", true,
				assetCategoryIds.toArray(new Long[0])));
		sxpParameters.add(
			new LongArraySXPParameter(
				"user.parent_asset_category_ids", true,
				parentAssetCategoryIds.toArray(new Long[0])));
	}

	private void _addExpandoSXPParameters(
			SearchContext searchContext, Set<SXPParameter> sxpParameters,
			User user)
		throws PortalException {

		List<ExpandoColumn> expandoColumns =
			_expandoColumnLocalService.getDefaultTableColumns(
				searchContext.getCompanyId(), User.class.getName());

		if (ListUtil.isEmpty(expandoColumns)) {
			return;
		}

		Map<Long, ExpandoValue> expandoValues = new HashMap<>();

		for (ExpandoValue expandoValue :
				_expandoValueLocalService.getRowValues(
					searchContext.getCompanyId(), User.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					user.getPrimaryKey(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS)) {

			expandoValues.put(expandoValue.getColumnId(), expandoValue);
		}

		for (ExpandoColumn expandoColumn : expandoColumns) {
			ExpandoValue expandoValue = expandoValues.get(
				expandoColumn.getColumnId());

			if (expandoValue == null) {
				expandoValue = _expandoValueLocalService.createExpandoValue(0);

				expandoValue.setData(expandoColumn.getDefaultData());
			}

			String expandoSXPParameterName = _getExpandoSXPParameterName(
				expandoColumn);

			int type = expandoColumn.getType();

			if (type == ExpandoColumnConstants.BOOLEAN) {
				sxpParameters.add(
					new BooleanSXPParameter(
						expandoSXPParameterName, true,
						expandoValue.getBoolean()));
			}
			else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
				sxpParameters.add(
					new BooleanArraySXPParameter(
						expandoSXPParameterName, true,
						ArrayUtil.toArray(expandoValue.getBooleanArray())));
			}
			else if (type == ExpandoColumnConstants.DATE) {
				sxpParameters.add(
					new DateSXPParameter(
						expandoSXPParameterName, true, expandoValue.getDate()));
			}
			else if (type == ExpandoColumnConstants.DOUBLE) {
				sxpParameters.add(
					new DoubleSXPParameter(
						expandoSXPParameterName, true,
						expandoValue.getDouble()));
			}
			else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
				sxpParameters.add(
					new DoubleArraySXPParameter(
						expandoSXPParameterName, true,
						ArrayUtil.toArray(expandoValue.getDoubleArray())));
			}
			else if (type == ExpandoColumnConstants.FLOAT) {
				sxpParameters.add(
					new FloatSXPParameter(
						expandoSXPParameterName, true,
						expandoValue.getFloat()));
			}
			else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
				sxpParameters.add(
					new FloatArraySXPParameter(
						expandoSXPParameterName, true,
						ArrayUtil.toArray(expandoValue.getFloatArray())));
			}
			else if (type == ExpandoColumnConstants.GEOLOCATION) {
				JSONObject jsonObject = expandoValue.getGeolocationJSONObject();

				sxpParameters.add(
					new DoubleSXPParameter(
						expandoSXPParameterName + ".latitude", true,
						jsonObject.getDouble("latitude")));
				sxpParameters.add(
					new DoubleSXPParameter(
						expandoSXPParameterName + ".longitude", true,
						jsonObject.getDouble("longitude")));
			}
			else if (type == ExpandoColumnConstants.INTEGER) {
				sxpParameters.add(
					new IntegerSXPParameter(
						expandoSXPParameterName, true,
						expandoValue.getInteger()));
			}
			else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
				sxpParameters.add(
					new IntegerArraySXPParameter(
						expandoSXPParameterName, true,
						ArrayUtil.toArray(expandoValue.getIntegerArray())));
			}
			else if (type == ExpandoColumnConstants.LONG) {
				sxpParameters.add(
					new LongSXPParameter(
						expandoSXPParameterName, true, expandoValue.getLong()));
			}
			else if (type == ExpandoColumnConstants.LONG_ARRAY) {
				sxpParameters.add(
					new LongArraySXPParameter(
						expandoSXPParameterName, true,
						ArrayUtil.toArray(expandoValue.getLongArray())));
			}
			else if (type == ExpandoColumnConstants.NUMBER) {
				sxpParameters.add(
					new StringSXPParameter(
						expandoSXPParameterName, true, expandoValue.getData()));
			}
			else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
				sxpParameters.add(
					new StringArraySXPParameter(
						expandoSXPParameterName, true,
						StringUtil.split(expandoValue.getData())));
			}
			else if (type == ExpandoColumnConstants.SHORT) {
				sxpParameters.add(
					new IntegerSXPParameter(
						expandoSXPParameterName, true,
						GetterUtil.getInteger(expandoValue.getShort())));
			}
			else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
				short[] shortArray = expandoValue.getShortArray();

				Integer[] integerArray = new Integer[shortArray.length];

				for (int i = 0; i < shortArray.length; i++) {
					integerArray[i] = (int)shortArray[i];
				}

				sxpParameters.add(
					new IntegerArraySXPParameter(
						expandoSXPParameterName, true, integerArray));
			}
			else if (type == ExpandoColumnConstants.STRING) {
				sxpParameters.add(
					new StringSXPParameter(
						expandoSXPParameterName, true,
						expandoValue.getString()));
			}
			else if (type == ExpandoColumnConstants.STRING_ARRAY) {
				sxpParameters.add(
					new StringArraySXPParameter(
						expandoSXPParameterName, true,
						expandoValue.getStringArray()));
			}
			else if (type == ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) {
				sxpParameters.add(
					new StringArraySXPParameter(
						StringBundler.concat(
							expandoSXPParameterName, StringPool.UNDERLINE,
							_language.getLanguageId(searchContext.getLocale())),
						true,
						expandoValue.getStringArray(
							searchContext.getLocale())));
			}
			else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
				sxpParameters.add(
					new StringSXPParameter(
						StringBundler.concat(
							expandoSXPParameterName, StringPool.UNDERLINE,
							_language.getLanguageId(searchContext.getLocale())),
						true,
						expandoValue.getString(searchContext.getLocale())));
			}
		}
	}

	private void _contribute(
			SearchContext searchContext, Set<SXPParameter> sxpParameters)
		throws PortalException {

		long userId = searchContext.getUserId();

		if (userId == 0) {
			return;
		}

		User user = _userLocalService.fetchUserById(userId);

		if (user == null) {
			return;
		}

		long[] segmentsEntryIds = new long[0];

		long scopeGroupId = GetterUtil.getLong(
			searchContext.getAttribute("search.experiences.scope.group.id"));

		if (scopeGroupId != 0) {
			segmentsEntryIds = _segmentsEntryRetriever.getSegmentsEntryIds(
				scopeGroupId, user.getUserId(),
				new Context() {
					{
						put(
							Context.LANGUAGE_ID,
							_language.getLanguageId(searchContext.getLocale()));
						put(Context.SIGNED_IN, !user.isGuestUser());
					}
				},
				new long[0]);

			segmentsEntryIds = ArrayUtil.filter(
				segmentsEntryIds, segmentsEntryId -> segmentsEntryId > 0);
		}

		sxpParameters.add(
			new LongArraySXPParameter(
				"user.active_segment_entry_ids", true,
				ArrayUtil.toLongArray(segmentsEntryIds)));

		sxpParameters.add(
			new IntegerSXPParameter(
				"user.age", true, _getAge(user.getBirthday())));
		sxpParameters.add(
			new StringArraySXPParameter(
				"user.asset_tag_names", true, _getAssetTagNames(user)));
		sxpParameters.add(
			new DateSXPParameter("user.birthday", true, user.getBirthday()));
		sxpParameters.add(
			new DateSXPParameter(
				"user.create_date", true, user.getCreateDate()));
		sxpParameters.add(
			new LongArraySXPParameter(
				"user.current_site_role_ids", true,
				_getCurrentSiteRoleIds(scopeGroupId, user)));
		sxpParameters.add(
			new StringSXPParameter(
				"user.email_domain", true, _getEmailAddressDomain(user)));
		sxpParameters.add(
			new StringSXPParameter(
				"user.external_reference_code", true,
				user.getExternalReferenceCode()));
		sxpParameters.add(
			new StringSXPParameter(
				"user.first_name", true, user.getFirstName()));
		sxpParameters.add(
			new StringSXPParameter("user.full_name", true, user.getFullName()));

		List<Group> groups = _groupLocalService.getGroups(user.getGroupIds());

		if (!groups.isEmpty()) {
			sxpParameters.add(
				new StringArraySXPParameter(
					"user.group_external_reference_codes", true,
					TransformUtil.transformToArray(
						groups, Group::getExternalReferenceCode,
						String.class)));
		}

		sxpParameters.add(
			new LongArraySXPParameter(
				"user.group_ids", true,
				ArrayUtil.toLongArray(user.getGroupIds())));
		sxpParameters.add(
			new LongSXPParameter("user.id", true, user.getUserId()));
		sxpParameters.add(
			new BooleanSXPParameter("user.is_female", true, user.isFemale()));
		sxpParameters.add(
			new BooleanSXPParameter(
				"user.is_gender_x", true, !user.isFemale() && !user.isMale()));
		sxpParameters.add(
			new BooleanSXPParameter("user.is_male", true, user.isMale()));
		sxpParameters.add(
			new BooleanSXPParameter(
				"user.is_omniadmin", true,
				_portal.isOmniadmin(user.getUserId())));
		sxpParameters.add(
			new BooleanSXPParameter(
				"user.is_signed_in", true, !user.isGuestUser()));
		sxpParameters.add(
			new StringSXPParameter("user.job_title", true, user.getJobTitle()));
		sxpParameters.add(
			new StringSXPParameter(
				"user.language_id", true, user.getLanguageId()));
		sxpParameters.add(
			new StringSXPParameter("user.last_name", true, user.getLastName()));
		sxpParameters.add(
			new LongArraySXPParameter(
				"user.regular_role_ids", true, _getRegularRoleIds(user)));

		List<UserGroup> userGroups = _userGroupLocalService.getUserUserGroups(
			userId);

		if (!userGroups.isEmpty()) {
			sxpParameters.add(
				new StringArraySXPParameter(
					"user.user_group_external_reference_codes", true,
					TransformUtil.transformToArray(
						userGroups, UserGroup::getExternalReferenceCode,
						String.class)));
			sxpParameters.add(
				new LongArraySXPParameter(
					"user.user_group_ids", true,
					TransformUtil.transformToArray(
						userGroups, UserGroup::getUserGroupId, Long.class)));
		}

		_addAssetCategories(sxpParameters, user);
		_addExpandoSXPParameters(searchContext, sxpParameters, user);
	}

	private int _getAge(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		int x = GetterUtil.getInteger(dateFormat.format(date));
		int y = GetterUtil.getInteger(dateFormat.format(new Date()));

		return (y - x) / 10000;
	}

	private String[] _getAssetTagNames(User user) {
		List<AssetTag> assetTags = _assetTagLocalService.getTags(
			user.getModelClassName(), user.getPrimaryKey());

		return ListUtil.toArray(assetTags, AssetTag.NAME_ACCESSOR);
	}

	private Long[] _getCurrentSiteRoleIds(Long scopeGroupId, User user) {
		List<Long> roleIds = new ArrayList<>();

		List<UserGroupRole> userGroupRoles =
			_userGroupRoleLocalService.getUserGroupRoles(user.getUserId());

		for (UserGroupRole userGroupRole : userGroupRoles) {
			roleIds.add(userGroupRole.getRoleId());
		}

		if (scopeGroupId != null) {
			List<UserGroupGroupRole> userGroupGroupRoles =
				_userGroupGroupRoleLocalService.getUserGroupGroupRolesByUser(
					user.getUserId(), scopeGroupId);

			for (UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles) {
				roleIds.add(userGroupGroupRole.getRoleId());
			}
		}

		return roleIds.toArray(new Long[0]);
	}

	private String _getEmailAddressDomain(User user) {
		String emailAddress = user.getEmailAddress();

		return emailAddress.substring(emailAddress.indexOf("@") + 1);
	}

	private String _getExpandoSXPParameterName(ExpandoColumn expandoColumn) {
		StringBundler sb = new StringBundler(2);

		sb.append("user.custom.field.");
		sb.append(
			StringUtil.toLowerCase(
				StringUtil.replace(
					expandoColumn.getName(), StringPool.BLANK, "_")));

		return sb.toString();
	}

	private String _getExpandoSXPParameterName(
		ExpandoColumn expandoColumn, Locale locale) {

		return StringBundler.concat(
			_getExpandoSXPParameterName(expandoColumn), StringPool.UNDERLINE,
			_language.getLanguageId(locale));
	}

	private List<Long> _getParentAssetCategoryIds(
		AssetCategory assetCategory, List<Long> parentAssetCategoryIds) {

		parentAssetCategoryIds.add(assetCategory.getCategoryId());

		if (assetCategory.getParentCategoryId() > 0) {
			_getParentAssetCategoryIds(
				assetCategory.getParentCategory(), parentAssetCategoryIds);
		}

		return parentAssetCategoryIds;
	}

	private Long[] _getRegularRoleIds(User user) throws PortalException {
		long[] roleIds = user.getRoleIds();

		List<UserGroup> userGroups = _userGroupLocalService.getUserUserGroups(
			user.getUserId());

		for (UserGroup userGroup : userGroups) {
			roleIds = ArrayUtil.append(
				roleIds,
				_groupLocalService.getRolePrimaryKeys(userGroup.getGroupId()));
		}

		return ArrayUtil.toLongArray(roleIds);
	}

	private List<SXPParameterContributorDefinition>
		_getSXPParameterContributorDefinitions(
			long companyId, Locale locale,
			List<SXPParameterContributorDefinition>
				sxpParameterContributorDefinitions) {

		List<ExpandoColumn> expandoColumns =
			_expandoColumnLocalService.getDefaultTableColumns(
				companyId, User.class.getName());

		if (ListUtil.isEmpty(expandoColumns)) {
			return sxpParameterContributorDefinitions;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		for (ExpandoColumn expandoColumn : expandoColumns) {
			if (PropsValues.
					PERMISSIONS_CUSTOM_ATTRIBUTE_READ_CHECK_BY_DEFAULT &&
				!ExpandoColumnPermissionUtil.contains(
					permissionChecker, companyId, User.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					expandoColumn.getName(), ActionKeys.VIEW)) {

				continue;
			}

			String expandoSXPParameterName = _getExpandoSXPParameterName(
				expandoColumn);

			int type = expandoColumn.getType();

			if (type == ExpandoColumnConstants.BOOLEAN) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						BooleanSXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.DATE) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						DateSXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.DOUBLE) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						DoubleSXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						DoubleArraySXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.FLOAT) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						FloatSXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						FloatArraySXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.GEOLOCATION) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						FloatSXPParameter.class,
						StringBundler.concat(
							expandoColumn.getDisplayName(locale), " (",
							_language.get(locale, "latitude"), ")"),
						expandoSXPParameterName + ".latitude"));
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						FloatSXPParameter.class,
						StringBundler.concat(
							expandoColumn.getDisplayName(locale), " (",
							_language.get(locale, "longitude"), ")"),
						expandoSXPParameterName + ".longitude"));
			}
			else if (type == ExpandoColumnConstants.INTEGER) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						IntegerSXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						IntegerArraySXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.LONG) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						LongSXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.LONG_ARRAY) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						LongArraySXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.NUMBER) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						StringSXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						StringArraySXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.SHORT) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						IntegerSXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						IntegerArraySXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.STRING) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						StringSXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.STRING_ARRAY) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						StringArraySXPParameter.class,
						expandoColumn.getDisplayName(locale),
						expandoSXPParameterName));
			}
			else if (type == ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						StringArraySXPParameter.class,
						StringBundler.concat(
							expandoColumn.getDisplayName(locale), " (",
							_language.get(locale, "localized"), ")"),
						_getExpandoSXPParameterName(expandoColumn, locale)));
			}
			else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
				sxpParameterContributorDefinitions.add(
					new SXPParameterContributorDefinition(
						StringSXPParameter.class,
						StringBundler.concat(
							expandoColumn.getDisplayName(locale), " (",
							_language.get(locale, "localized"), ")"),
						_getExpandoSXPParameterName(expandoColumn, locale)));
			}
		}

		return sxpParameterContributorDefinitions;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserSXPParameterContributor.class);

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final AssetTagLocalService _assetTagLocalService;
	private final ExpandoColumnLocalService _expandoColumnLocalService;
	private final ExpandoValueLocalService _expandoValueLocalService;
	private final GroupLocalService _groupLocalService;
	private final Language _language;
	private final Portal _portal;
	private final SegmentsEntryRetriever _segmentsEntryRetriever;
	private final UserGroupGroupRoleLocalService
		_userGroupGroupRoleLocalService;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserGroupRoleLocalService _userGroupRoleLocalService;
	private final UserLocalService _userLocalService;

}