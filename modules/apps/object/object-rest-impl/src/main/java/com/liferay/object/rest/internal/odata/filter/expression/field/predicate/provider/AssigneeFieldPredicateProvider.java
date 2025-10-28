/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.odata.filter.expression.field.predicate.provider;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.object.odata.filter.expression.field.predicate.provider.FieldPredicateProvider;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.filter.expression.BinaryExpression;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "field.predicate.provider.key=" + ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE,
	service = FieldPredicateProvider.class
)
public class AssigneeFieldPredicateProvider implements FieldPredicateProvider {

	@Override
	public Predicate getBinaryExpressionPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		Object left, long objectDefinitionId,
		BinaryExpression.Operation operation, Object right) {

		if (!Objects.equals(operation, BinaryExpression.Operation.EQ)) {
			throw new UnsupportedOperationException(
				operation + " is not supported in assignee fields");
		}

		try {
			ObjectField objectField = _objectFieldLocalService.getObjectField(
				objectDefinitionId, String.valueOf(left));

			Table<?> table = _objectFieldLocalService.getTable(
				objectDefinitionId, objectField.getName());

			Column<?, Long> classNameIdColumn =
				(Column<?, Long>)table.getColumn(
					"classNameId_" + objectField.getDBColumnName());
			Column<?, Long> classPKColumn = (Column<?, Long>)table.getColumn(
				"classPK_" + objectField.getDBColumnName());

			return Predicate.or(
				classNameIdColumn.eq(
					_portal.getClassNameId(Role.class.getName())
				).and(
					classPKColumn.in(_getRoleIds(GetterUtil.getLong(right)))
				).withParentheses(),
				classNameIdColumn.eq(
					_portal.getClassNameId(User.class.getName())
				).and(
					classPKColumn.eq(GetterUtil.getLong(right))
				).withParentheses()
			).withParentheses();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	@Override
	public Predicate getContainsPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, Object fieldValue) {

		throw new UnsupportedOperationException(
			"Unsupported method getContainsPredicate for assignee fields");
	}

	@Override
	public Predicate getInPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		Object left, List<Object> rights) {

		throw new UnsupportedOperationException(
			"Unsupported method getInPredicate for assignee fields");
	}

	@Override
	public Predicate getIsNotEmptyPredicate(
		String fieldName,
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier) {

		throw new UnsupportedOperationException(
			"Unsupported method getIsNotEmptyPredicate for assignee fields");
	}

	public Predicate getStartsWithPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, Object fieldValue) {

		throw new UnsupportedOperationException(
			"Unsupported method getStartsWithPredicate for assignee fields");
	}

	private Long[] _getRoleIds(long userId) throws PortalException {
		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return new Long[] {0L};
		}

		return TransformUtil.transformToArray(
			user.getAllRoles(), RoleModel::getRoleId, Long.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssigneeFieldPredicateProvider.class);

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}