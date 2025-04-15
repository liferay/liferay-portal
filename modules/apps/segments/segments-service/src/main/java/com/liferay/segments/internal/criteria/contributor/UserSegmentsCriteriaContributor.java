/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.criteria.contributor;

import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.Groups_OrgsTable;
import com.liferay.portal.kernel.model.Groups_RolesTable;
import com.liferay.portal.kernel.model.Groups_UserGroupsTable;
import com.liferay.portal.kernel.model.Users_GroupsTable;
import com.liferay.portal.kernel.model.Users_OrgsTable;
import com.liferay.portal.kernel.model.Users_UserGroupsTable;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.criteria.mapper.SegmentsCriteriaJSONObjectMapper;
import com.liferay.segments.field.Field;
import com.liferay.segments.internal.odata.entity.EntityModelFieldMapper;
import com.liferay.segments.internal.odata.entity.UserEntityModel;

import jakarta.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"segments.criteria.contributor.key=" + UserSegmentsCriteriaContributor.KEY,
		"segments.criteria.contributor.model.class.name=com.liferay.portal.kernel.model.User",
		"segments.criteria.contributor.priority:Integer=10"
	},
	service = SegmentsCriteriaContributor.class
)
public class UserSegmentsCriteriaContributor
	implements SegmentsCriteriaContributor {

	public static final String KEY = "user";

	@Override
	public void contribute(
		Criteria criteria, String filterString,
		Criteria.Conjunction conjunction) {

		criteria.addCriterion(getKey(), getType(), filterString, conjunction);

		String newFilterString = filterString;

		Matcher matcher = _roleIdsPattern.matcher(filterString);

		while (matcher.find()) {
			long roleId = _getRoleId(matcher.group());

			newFilterString = StringUtil.replace(
				newFilterString, matcher.group(),
				String.join(
					StringPool.BLANK, StringPool.OPEN_PARENTHESIS,
					matcher.group(), _getGroupIdsFilterString(roleId),
					_getOrganizationIdsFilterString(roleId),
					_getUserGroupIdsFilterString(roleId),
					StringPool.CLOSE_PARENTHESIS));
		}

		matcher = _dateModifiedPattern.matcher(filterString);

		while (matcher.find()) {
			String date = matcher.group("date");

			newFilterString = StringUtil.replace(
				newFilterString, matcher.group(),
				StringBundler.concat(
					"dateModified ge ", date, "T00:00:00.000Z and ",
					"dateModified le ", date, "T23:59:59.999Z"));
		}

		criteria.addFilter(getType(), newFilterString, conjunction);
	}

	@Override
	public JSONObject getCriteriaJSONObject(Criteria criteria)
		throws Exception {

		return _segmentsCriteriaJSONObjectMapper.toJSONObject(criteria, this);
	}

	@Override
	public EntityModel getEntityModel() {
		return _entityModel;
	}

	@Override
	public String getEntityName() {
		return UserEntityModel.NAME;
	}

	@Override
	public List<Field> getFields(PortletRequest portletRequest) {
		return _entityModelFieldMapper.getFields(_entityModel, portletRequest);
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Criteria.Type getType() {
		return Criteria.Type.MODEL;
	}

	private String _getGroupIdsFilterString(long roleId) {
		List<Long> groupIds = _roleLocalService.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				Users_GroupsTable.INSTANCE.groupId
			).from(
				Groups_RolesTable.INSTANCE
			).innerJoinON(
				Users_GroupsTable.INSTANCE,
				Users_GroupsTable.INSTANCE.groupId.eq(
					Groups_RolesTable.INSTANCE.groupId)
			).where(
				Groups_RolesTable.INSTANCE.roleId.eq(roleId)
			));

		if (groupIds.isEmpty()) {
			return StringPool.BLANK;
		}

		return String.join(
			StringPool.BLANK, StringPool.SPACE,
			Criteria.Conjunction.OR.getValue(), StringPool.SPACE,
			_toFilterString("groupIds", groupIds));
	}

	private String _getOrganizationIdsFilterString(long roleId) {
		List<Long> organizationIds = _roleLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				Users_OrgsTable.INSTANCE.organizationId
			).from(
				Groups_RolesTable.INSTANCE
			).innerJoinON(
				GroupTable.INSTANCE,
				GroupTable.INSTANCE.groupId.eq(
					Groups_RolesTable.INSTANCE.groupId)
			).innerJoinON(
				Users_OrgsTable.INSTANCE,
				Users_OrgsTable.INSTANCE.organizationId.eq(
					GroupTable.INSTANCE.classPK)
			).where(
				Groups_RolesTable.INSTANCE.roleId.eq(roleId)
			).union(
				DSLQueryFactoryUtil.select(
					Users_OrgsTable.INSTANCE.organizationId
				).from(
					Groups_RolesTable.INSTANCE
				).innerJoinON(
					Groups_OrgsTable.INSTANCE,
					Groups_OrgsTable.INSTANCE.groupId.eq(
						Groups_RolesTable.INSTANCE.groupId)
				).innerJoinON(
					Users_OrgsTable.INSTANCE,
					Users_OrgsTable.INSTANCE.organizationId.eq(
						Groups_OrgsTable.INSTANCE.organizationId)
				).where(
					Groups_RolesTable.INSTANCE.roleId.eq(roleId)
				)
			));

		if (organizationIds.isEmpty()) {
			return StringPool.BLANK;
		}

		return String.join(
			StringPool.BLANK, StringPool.SPACE,
			Criteria.Conjunction.OR.getValue(), StringPool.SPACE,
			_toFilterString("organizationIds", organizationIds));
	}

	private long _getRoleId(String criterionString) {
		int indexOf = criterionString.indexOf("'");
		int lastIndexOf = criterionString.lastIndexOf("'");

		if ((indexOf == -1) || (lastIndexOf == -1)) {
			return -1;
		}

		return GetterUtil.getLong(
			criterionString.substring(indexOf + 1, lastIndexOf));
	}

	private String _getUserGroupIdsFilterString(long roleId) {
		List<Long> userGroupIds = _roleLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				Users_UserGroupsTable.INSTANCE.userGroupId
			).from(
				Groups_RolesTable.INSTANCE
			).innerJoinON(
				GroupTable.INSTANCE,
				GroupTable.INSTANCE.groupId.eq(
					Groups_RolesTable.INSTANCE.groupId)
			).innerJoinON(
				Users_UserGroupsTable.INSTANCE,
				Users_UserGroupsTable.INSTANCE.userGroupId.eq(
					GroupTable.INSTANCE.classPK)
			).where(
				Groups_RolesTable.INSTANCE.roleId.eq(roleId)
			).union(
				DSLQueryFactoryUtil.select(
					Users_UserGroupsTable.INSTANCE.userGroupId
				).from(
					Groups_RolesTable.INSTANCE
				).innerJoinON(
					Groups_UserGroupsTable.INSTANCE,
					Groups_UserGroupsTable.INSTANCE.groupId.eq(
						Groups_RolesTable.INSTANCE.groupId)
				).innerJoinON(
					Users_UserGroupsTable.INSTANCE,
					Users_UserGroupsTable.INSTANCE.userGroupId.eq(
						Groups_UserGroupsTable.INSTANCE.userGroupId)
				).where(
					Groups_RolesTable.INSTANCE.roleId.eq(roleId)
				)
			));

		if (userGroupIds.isEmpty()) {
			return StringPool.BLANK;
		}

		return String.join(
			StringPool.BLANK, StringPool.SPACE,
			Criteria.Conjunction.OR.getValue(), StringPool.SPACE,
			_toFilterString("userGroupIds", userGroupIds));
	}

	private String _toFilterString(String fieldName, List<Long> ids) {
		List<String> conditions = new ArrayList<>();

		for (long id : ids) {
			conditions.add(StringBundler.concat(fieldName, " eq '", id, "'"));
		}

		return String.join(" or ", conditions);
	}

	private static final Pattern _dateModifiedPattern = Pattern.compile(
		"dateModified eq (?<date>\\d{4}-\\d{2}-\\d{2})T.*Z");
	private static final Pattern _roleIdsPattern = Pattern.compile(
		"roleIds eq '\\d+'");

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(entity.model.name=" + UserEntityModel.NAME + ")"
	)
	private volatile EntityModel _entityModel;

	@Reference
	private EntityModelFieldMapper _entityModelFieldMapper;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(target = "(segments.criteria.mapper.key=odata)")
	private SegmentsCriteriaJSONObjectMapper _segmentsCriteriaJSONObjectMapper;

}