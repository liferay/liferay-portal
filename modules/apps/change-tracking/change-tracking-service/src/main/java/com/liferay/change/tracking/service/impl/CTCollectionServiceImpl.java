/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.exception.CTCollectionStatusException;
import com.liferay.change.tracking.model.CTAutoResolutionInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTCollectionTable;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.change.tracking.service.base.CTCollectionServiceBaseImpl;
import com.liferay.change.tracking.service.persistence.CTAutoResolutionInfoPersistence;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRoleTable;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
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
		"json.web.service.context.path=CTCollection"
	},
	service = AopService.class
)
public class CTCollectionServiceImpl extends CTCollectionServiceBaseImpl {

	@Override
	public CTCollection addCTCollection(
			String externalReferenceCode, long ctRemoteId, String name,
			String description)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null, CTActionKeys.ADD_PUBLICATION);

		User user = getUser();

		return ctCollectionLocalService.addCTCollection(
			externalReferenceCode, user.getCompanyId(), user.getUserId(),
			ctRemoteId, name, description);
	}

	@Override
	public void deleteCTAutoResolutionInfo(long ctAutoResolutionInfoId)
		throws PortalException {

		CTAutoResolutionInfo ctAutoResolutionInfo =
			_ctAutoResolutionInfoPersistence.fetchByPrimaryKey(
				ctAutoResolutionInfoId);

		if (ctAutoResolutionInfo == null) {
			return;
		}

		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), ctAutoResolutionInfo.getCtCollectionId(),
			ActionKeys.UPDATE);

		ctCollectionLocalService.deleteCTAutoResolutionInfo(
			ctAutoResolutionInfoId);
	}

	@Override
	public CTCollection deleteCTCollection(CTCollection ctCollection)
		throws PortalException {

		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), ctCollection, ActionKeys.DELETE);

		return ctCollectionLocalService.deleteCTCollection(ctCollection);
	}

	@Override
	public void discardCTEntry(long ctCollectionId, List<CTEntry> ctEntries)
		throws PortalException {

		CTCollection ctCollection = ctCollectionPersistence.findByPrimaryKey(
			ctCollectionId);

		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), ctCollection, ActionKeys.UPDATE);

		ctCollectionLocalService.discardCTEntry(
			ctCollectionId, ctEntries, false);
	}

	@Override
	public void discardCTEntry(
			long ctCollectionId, long modelClassNameId, long modelClassPK)
		throws PortalException {

		CTCollection ctCollection = ctCollectionPersistence.findByPrimaryKey(
			ctCollectionId);

		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), ctCollection, ActionKeys.UPDATE);

		ctCollectionLocalService.discardCTEntry(
			ctCollectionId, modelClassNameId, modelClassPK, false);
	}

	@Override
	public List<CTCollection> getCTCollections(
			int[] statuses, int start, int end,
			OrderByComparator<CTCollection> orderByComparator)
		throws PortalException {

		User user = getUser();

		if (statuses == null) {
			return ctCollectionPersistence.filterFindByCompanyId(
				user.getCompanyId(), start, end, orderByComparator);
		}

		return ctCollectionPersistence.filterFindByC_S(
			user.getCompanyId(), statuses, start, end, orderByComparator);
	}

	@Override
	public List<CTCollection> getCTCollections(
			int[] statuses, String keywords, int start, int end,
			OrderByComparator<CTCollection> orderByComparator)
		throws PortalException {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			CTCollectionTable.INSTANCE
		).from(
			CTCollectionTable.INSTANCE
		).where(
			_getPredicate(statuses, keywords)
		).orderBy(
			CTCollectionTable.INSTANCE, orderByComparator
		).limit(
			start, end
		);

		return ctCollectionPersistence.dslQuery(dslQuery);
	}

	@Override
	public int getCTCollectionsCount(int[] statuses, String keywords)
		throws PortalException {

		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			CTCollectionTable.INSTANCE
		).where(
			_getPredicate(statuses, keywords)
		);

		return ctCollectionPersistence.dslQueryCount(dslQuery);
	}

	@Override
	public void moveCTEntries(
			long fromCTCollectionId, long toCTCollectionId,
			List<CTEntry> ctEntries)
		throws PortalException {

		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), fromCTCollectionId, ActionKeys.UPDATE);
		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), toCTCollectionId, ActionKeys.UPDATE);

		ctCollectionLocalService.moveCTEntries(
			fromCTCollectionId, toCTCollectionId, ctEntries);
	}

	@Override
	public void moveCTEntry(
			long fromCTCollectionId, long toCTCollectionId,
			long modelClassNameId, long modelClassPK)
		throws PortalException {

		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), fromCTCollectionId, ActionKeys.UPDATE);
		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), toCTCollectionId, ActionKeys.UPDATE);

		ctCollectionLocalService.moveCTEntry(
			fromCTCollectionId, toCTCollectionId, modelClassNameId,
			modelClassPK);
	}

	@Override
	public void publishCTCollection(long userId, long ctCollectionId)
		throws PortalException {

		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), ctCollectionId, CTActionKeys.PUBLISH);

		CTCollection ctCollection = ctCollectionLocalService.getCTCollection(
			ctCollectionId);

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			throw new CTCollectionStatusException(
				"Change tracking collection is already published");
		}

		if (!ctCollection.isInProgress()) {
			throw new CTCollectionStatusException(
				"Change tracking collection is not a draft");
		}

		_ctProcessLocalService.addCTProcess(userId, ctCollectionId);
	}

	@Override
	public CTCollection undoCTCollection(
			long ctCollectionId, long userId, String name, String description)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		_ctCollectionModelResourcePermission.check(
			permissionChecker, ctCollectionId, ActionKeys.VIEW);

		_portletResourcePermission.check(
			permissionChecker, null, CTActionKeys.ADD_PUBLICATION);

		return ctCollectionLocalService.undoCTCollection(
			ctCollectionId, userId, name, description);
	}

	@Override
	public CTCollection updateCTCollection(
			long userId, long ctCollectionId, String name, String description)
		throws PortalException {

		_ctCollectionModelResourcePermission.check(
			getPermissionChecker(), ctCollectionId, ActionKeys.UPDATE);

		return ctCollectionLocalService.updateCTCollection(
			userId, ctCollectionId, name, description);
	}

	private Predicate _getPredicate(int[] statuses, String keywords)
		throws PortalException {

		User user = getUser();

		Predicate predicate = CTCollectionTable.INSTANCE.companyId.eq(
			user.getCompanyId()
		).and(
			() -> {
				if (ArrayUtil.isEmpty(statuses)) {
					return null;
				}

				return CTCollectionTable.INSTANCE.status.in(
					ArrayUtil.toArray(statuses));
			}
		);

		String[] keywordsArray = _customSQL.keywords(
			keywords, true, WildcardMode.SURROUND);

		predicate = predicate.and(
			Predicate.withParentheses(
				Predicate.or(
					_customSQL.getKeywordsPredicate(
						DSLFunctionFactoryUtil.lower(
							CTCollectionTable.INSTANCE.name),
						keywordsArray),
					_customSQL.getKeywordsPredicate(
						DSLFunctionFactoryUtil.lower(
							CTCollectionTable.INSTANCE.description),
						keywordsArray))));

		Predicate permissionWherePredicate =
			_inlineSQLHelper.getPermissionWherePredicate(
				CTCollection.class, CTCollectionTable.INSTANCE.ctCollectionId);

		if (permissionWherePredicate == null) {
			return predicate;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return predicate.and(
			permissionWherePredicate.or(
				CTCollectionTable.INSTANCE.ctCollectionId.in(
					DSLQueryFactoryUtil.selectDistinct(
						GroupTable.INSTANCE.classPK
					).from(
						GroupTable.INSTANCE
					).innerJoinON(
						UserGroupRoleTable.INSTANCE,
						UserGroupRoleTable.INSTANCE.groupId.eq(
							GroupTable.INSTANCE.groupId)
					).where(
						GroupTable.INSTANCE.companyId.eq(
							permissionChecker.getCompanyId()
						).and(
							GroupTable.INSTANCE.classNameId.eq(
								_classNameLocalService.getClassNameId(
									CTCollection.class.getName()))
						).and(
							UserGroupRoleTable.INSTANCE.userId.eq(
								permissionChecker.getUserId())
						)
					))
			).withParentheses());
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CTAutoResolutionInfoPersistence _ctAutoResolutionInfoPersistence;

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@Reference
	private CTProcessLocalService _ctProcessLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private InlineSQLHelper _inlineSQLHelper;

	@Reference(target = "(resource.name=" + CTConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}