/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollectionTable;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.model.CTProcessTable;
import com.liferay.change.tracking.service.base.CTProcessServiceBaseImpl;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.LimitStep;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.background.task.model.BackgroundTaskTable;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = {
		"json.web.service.context.name=ct",
		"json.web.service.context.path=CTProcess"
	},
	service = AopService.class
)
public class CTProcessServiceImpl extends CTProcessServiceBaseImpl {

	@Override
	public CTProcess deleteCTProcess(long ctProcessId) throws PortalException {
		_ctProcessModelResourcePermission.check(
			getPermissionChecker(), ctProcessId, ActionKeys.DELETE);

		CTProcess ctProcess = ctProcessPersistence.findByPrimaryKey(
			ctProcessId);

		return ctProcessLocalService.deleteCTProcess(ctProcess);
	}

	@Override
	public List<CTProcess> getCTProcesses(
			long companyId, long userId, String keywords, int status, int start,
			int end, OrderByComparator<CTProcess> orderByComparator)
		throws PortalException {

		PortletPermissionUtil.check(
			getPermissionChecker(), CTPortletKeys.PUBLICATIONS,
			ActionKeys.VIEW);

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			CTProcessTable.INSTANCE
		).from(
			CTProcessTable.INSTANCE
		).innerJoinON(
			CTCollectionTable.INSTANCE,
			CTCollectionTable.INSTANCE.ctCollectionId.eq(
				CTProcessTable.INSTANCE.ctCollectionId)
		).innerJoinON(
			BackgroundTaskTable.INSTANCE,
			BackgroundTaskTable.INSTANCE.backgroundTaskId.eq(
				CTProcessTable.INSTANCE.backgroundTaskId)
		).where(
			_getPredicate(companyId, keywords, status, userId)
		).orderBy(
			orderByStep -> {
				if (orderByComparator != null) {
					LimitStep limitStep = orderByStep.orderBy(
						CTProcessTable.INSTANCE, orderByComparator);

					if (limitStep == orderByStep) {
						return orderByStep.orderBy(
							CTCollectionTable.INSTANCE, orderByComparator);
					}

					return limitStep;
				}

				return orderByStep.orderBy(
					CTProcessTable.INSTANCE.createDate.descending());
			}
		).limit(
			start, end
		);

		return ctProcessPersistence.dslQuery(dslQuery);
	}

	@Override
	public int getCTProcessesCount(
		long companyId, long userId, String keywords, int status) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			CTProcessTable.INSTANCE
		).innerJoinON(
			CTCollectionTable.INSTANCE,
			CTCollectionTable.INSTANCE.ctCollectionId.eq(
				CTProcessTable.INSTANCE.ctCollectionId)
		).innerJoinON(
			BackgroundTaskTable.INSTANCE,
			BackgroundTaskTable.INSTANCE.backgroundTaskId.eq(
				CTProcessTable.INSTANCE.backgroundTaskId)
		).where(
			_getPredicate(companyId, keywords, status, userId)
		);

		return ctProcessPersistence.dslQueryCount(dslQuery);
	}

	private Predicate _getPredicate(
		long companyId, String keywords, int status, long userId) {

		Predicate predicate = CTProcessTable.INSTANCE.companyId.eq(
			companyId
		).and(
			() -> {
				if (status != WorkflowConstants.STATUS_ANY) {
					return BackgroundTaskTable.INSTANCE.status.eq(status);
				}

				return null;
			}
		).and(
			() -> CTProcessTable.INSTANCE.type.eq(
				CTConstants.CT_PROCESS_PUBLISH)
		).and(
			() -> {
				if (userId > 0) {
					return CTProcessTable.INSTANCE.userId.eq(userId);
				}

				return null;
			}
		);

		Predicate keywordsPredicate = null;

		for (String keyword :
				_customSQL.keywords(keywords, true, WildcardMode.SURROUND)) {

			if (keyword == null) {
				continue;
			}

			Predicate keywordPredicate = DSLFunctionFactoryUtil.lower(
				CTCollectionTable.INSTANCE.name
			).like(
				keyword
			).or(
				DSLFunctionFactoryUtil.lower(
					CTCollectionTable.INSTANCE.description
				).like(
					keyword
				)
			);

			if (keywordsPredicate == null) {
				keywordsPredicate = keywordPredicate;
			}
			else {
				keywordsPredicate = keywordsPredicate.or(keywordPredicate);
			}
		}

		if (keywordsPredicate == null) {
			return predicate;
		}

		return predicate.and(keywordsPredicate.withParentheses());
	}

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTProcess)"
	)
	private ModelResourcePermission<CTProcess>
		_ctProcessModelResourcePermission;

	@Reference
	private CustomSQL _customSQL;

}