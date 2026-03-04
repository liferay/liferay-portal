/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.constants.CommercePriceListActionKeys;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.base.CommercePriceListServiceBaseImpl;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Zoltán Takács
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePriceList"
	},
	service = AopService.class
)
public class CommercePriceListServiceImpl
	extends CommercePriceListServiceBaseImpl {

	@Override
	public CommercePriceList addCommercePriceList(
			String externalReferenceCode, long groupId,
			String commerceCurrencyCode, boolean netPrice, String type,
			long parentCommercePriceListId, boolean catalogBasePriceList,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		_checkPortletResourcePermission(
			groupId, CommercePriceListActionKeys.ADD_COMMERCE_PRICE_LIST);

		return commercePriceListLocalService.addCommercePriceList(
			externalReferenceCode, getUserId(), groupId, commerceCurrencyCode,
			netPrice, type, parentCommercePriceListId, catalogBasePriceList,
			name, priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public CommercePriceList addOrUpdateCommercePriceList(
			String externalReferenceCode, long groupId,
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type, ServiceContext serviceContext)
		throws PortalException {

		// Update

		if (commercePriceListId > 0) {
			try {
				return updateCommercePriceList(
					commercePriceListId, catalogBasePriceList,
					commerceCurrencyCode, displayDateDay, displayDateHour,
					displayDateMinute, displayDateMonth, displayDateYear,
					expirationDateDay, expirationDateHour, expirationDateMinute,
					expirationDateMonth, expirationDateYear, name, netPrice,
					neverExpire, parentCommercePriceListId, priority, type,
					serviceContext);
			}
			catch (NoSuchPriceListException noSuchPriceListException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find price list with ID: " +
							commercePriceListId,
						noSuchPriceListException);
				}
			}
		}

		if (Validator.isNotNull(externalReferenceCode)) {
			CommercePriceList commercePriceList =
				commercePriceListPersistence.fetchByERC_C(
					externalReferenceCode, serviceContext.getCompanyId());

			if (commercePriceList != null) {
				return updateCommercePriceList(
					commercePriceList.getCommercePriceListId(),
					catalogBasePriceList, commerceCurrencyCode, displayDateDay,
					displayDateHour, displayDateMinute, displayDateMonth,
					displayDateYear, expirationDateDay, expirationDateHour,
					expirationDateMinute, expirationDateMonth,
					expirationDateYear, name, netPrice, neverExpire,
					parentCommercePriceListId, priority, type, serviceContext);
			}
		}

		// Add

		return addCommercePriceList(
			externalReferenceCode, groupId, commerceCurrencyCode, netPrice,
			type, parentCommercePriceListId, catalogBasePriceList, name,
			priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public void deleteCommercePriceList(long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.DELETE);

		commercePriceListLocalService.deleteCommercePriceList(
			commercePriceListId);
	}

	@Override
	public CommercePriceList fetchCatalogBaseCommercePriceListByType(
			long groupId, String type)
		throws PortalException {

		CommercePriceList commercePriceList =
			commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(groupId, type);

		if (commercePriceList != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(), commercePriceList, ActionKeys.VIEW);
		}

		return commercePriceList;
	}

	@Override
	public CommercePriceList fetchCommercePriceList(long commercePriceListId)
		throws PortalException {

		CommercePriceList commercePriceList =
			commercePriceListLocalService.fetchCommercePriceList(
				commercePriceListId);

		if (commercePriceList != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);
		}

		return commercePriceList;
	}

	@Override
	public CommercePriceList fetchCommercePriceListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CommercePriceList commercePriceList =
			commercePriceListLocalService.
				fetchCommercePriceListByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commercePriceList != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(), commercePriceList, ActionKeys.VIEW);
		}

		return commercePriceList;
	}

	@Override
	public CommercePriceList getCommercePriceList(long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListLocalService.getCommercePriceList(
			commercePriceListId);
	}

	@Override
	public List<CommercePriceList> getCommercePriceLists(
			long companyId, int status, int start, int end,
			OrderByComparator<CommercePriceList> orderByComparator)
		throws PortalException {

		long[] groupIds = _getGroupIds(companyId);

		if (status == WorkflowConstants.STATUS_ANY) {
			return commercePriceListPersistence.filterFindByG_C_NotS(
				groupIds, companyId, WorkflowConstants.STATUS_IN_TRASH, start,
				end, orderByComparator);
		}

		return commercePriceListPersistence.filterFindByG_C_S(
			groupIds, companyId, status, start, end, orderByComparator);
	}

	@Override
	public List<CommercePriceList> getCommercePriceLists(
			long companyId, String type, int status, int start, int end,
			OrderByComparator<CommercePriceList> orderByComparator)
		throws PortalException {

		long[] groupIds = _getGroupIds(companyId);

		if (status == WorkflowConstants.STATUS_ANY) {
			return commercePriceListPersistence.filterFindByG_C_T_NotS(
				groupIds, companyId, type, WorkflowConstants.STATUS_IN_TRASH,
				start, end, orderByComparator);
		}

		return commercePriceListPersistence.filterFindByG_C_T_S(
			groupIds, companyId, type, status, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceListsCount(long companyId, int status)
		throws PortalException {

		long[] groupIds = _getGroupIds(companyId);

		if (status == WorkflowConstants.STATUS_ANY) {
			return commercePriceListPersistence.filterCountByG_C_NotS(
				groupIds, companyId, WorkflowConstants.STATUS_IN_TRASH);
		}

		return commercePriceListPersistence.filterCountByG_C_S(
			groupIds, companyId, status);
	}

	@Override
	public int getCommercePriceListsCount(
			long commercePricingClassId, String title)
		throws PrincipalException {

		return commercePriceListFinder.countByCommercePricingClassId(
			commercePricingClassId, title, true);
	}

	@Override
	public List<CommercePriceList> searchByCommercePricingClassId(
			long commercePricingClassId, String name, int start, int end)
		throws PrincipalException {

		return commercePriceListFinder.findByCommercePricingClassId(
			commercePricingClassId, name, start, end, true);
	}

	@Override
	public BaseModelSearchResult<CommercePriceList> searchCommercePriceLists(
			long companyId, String keywords, int status, int start, int end,
			Sort sort)
		throws PortalException {

		return commercePriceListLocalService.searchCommercePriceLists(
			companyId, _getGroupIds(companyId), keywords, status, start, end,
			sort);
	}

	@Override
	public int searchCommercePriceListsCount(
			long companyId, String keywords, int status)
		throws PortalException {

		return commercePriceListLocalService.searchCommercePriceListsCount(
			companyId, _getGroupIds(companyId), keywords, status);
	}

	@Override
	public void setCatalogBasePriceList(
			long groupId, long commercePriceListId, String type)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		commercePriceListLocalService.setCatalogBasePriceList(
			groupId, commercePriceListId, type);
	}

	@Override
	public CommercePriceList updateCommercePriceList(
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type, ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		return commercePriceListLocalService.updateCommercePriceList(
			commercePriceListId, catalogBasePriceList, commerceCurrencyCode,
			displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, name, netPrice, neverExpire,
			parentCommercePriceListId, priority, type, serviceContext);
	}

	@Override
	public CommercePriceList updateExternalReferenceCode(
			CommercePriceList commercePriceList, String externalReferenceCode,
			long companyId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceList, ActionKeys.UPDATE);

		return commercePriceListLocalService.updateExternalReferenceCode(
			commercePriceList, externalReferenceCode);
	}

	private void _checkPortletResourcePermission(long groupId, String actionId)
		throws PrincipalException {

		PortletResourcePermission portletResourcePermission =
			_commercePriceListModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), groupId, actionId);
	}

	private long[] _getGroupIds(long companyId) throws PortalException {
		return TransformUtil.transformToLongArray(
			_commerceCatalogService.search(
				companyId, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			CommerceCatalog::getGroupId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListServiceImpl.class);

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

}