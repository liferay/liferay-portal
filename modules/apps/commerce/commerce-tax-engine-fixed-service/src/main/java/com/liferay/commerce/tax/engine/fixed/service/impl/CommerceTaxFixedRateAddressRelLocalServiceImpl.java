/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.impl;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl;
import com.liferay.commerce.tax.engine.fixed.service.base.CommerceTaxFixedRateAddressRelLocalServiceBaseImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel",
	service = AopService.class
)
public class CommerceTaxFixedRateAddressRelLocalServiceImpl
	extends CommerceTaxFixedRateAddressRelLocalServiceBaseImpl {

	@Override
	public CommerceTaxFixedRateAddressRel addCommerceTaxFixedRateAddressRel(
			long userId, long groupId, long commerceTaxMethodId,
			long cpTaxCategoryId, long countryId, long regionId, String zip,
			double rate)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.getCPTaxCategory(cpTaxCategoryId);

		long commerceTaxFixedRateAddressRelId = counterLocalService.increment();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			commerceTaxFixedRateAddressRelPersistence.create(
				commerceTaxFixedRateAddressRelId);

		commerceTaxFixedRateAddressRel.setGroupId(groupId);
		commerceTaxFixedRateAddressRel.setCompanyId(user.getCompanyId());
		commerceTaxFixedRateAddressRel.setUserId(user.getUserId());
		commerceTaxFixedRateAddressRel.setUserName(user.getFullName());
		commerceTaxFixedRateAddressRel.setCommerceTaxMethodId(
			commerceTaxMethodId);
		commerceTaxFixedRateAddressRel.setCPTaxCategoryId(
			cpTaxCategory.getCPTaxCategoryId());
		commerceTaxFixedRateAddressRel.setCountryId(countryId);
		commerceTaxFixedRateAddressRel.setRegionId(regionId);
		commerceTaxFixedRateAddressRel.setZip(zip);
		commerceTaxFixedRateAddressRel.setRate(rate);

		return commerceTaxFixedRateAddressRelPersistence.update(
			commerceTaxFixedRateAddressRel);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceTaxFixedRateAddressRel addCommerceTaxFixedRateAddressRel(
			long commerceTaxMethodId, long cpTaxCategoryId, long countryId,
			long regionId, String zip, double rate,
			ServiceContext serviceContext)
		throws PortalException {

		return commerceTaxFixedRateAddressRelLocalService.
			addCommerceTaxFixedRateAddressRel(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				commerceTaxMethodId, cpTaxCategoryId, countryId, regionId, zip,
				rate);
	}

	@Override
	public void deleteCommerceTaxFixedRateAddressRelsByCPTaxCategoryId(
		long cpTaxCategoryId) {

		commerceTaxFixedRateAddressRelPersistence.removeByCPTaxCategoryId(
			cpTaxCategoryId);
	}

	@Override
	public void deleteCommerceTaxFixedRateAddressRelsByCommerceTaxMethodId(
		long commerceTaxMethodId) {

		commerceTaxFixedRateAddressRelPersistence.removeByCommerceTaxMethodId(
			commerceTaxMethodId);
	}

	@Override
	public CommerceTaxFixedRateAddressRel fetchCommerceTaxFixedRateAddressRel(
		long commerceTaxMethodId, long cpTaxCategoryId, long countryId,
		long regionId, String zip) {

		long[] countryIds = {countryId, 0};
		long[] regionIds = {regionId, 0};

		return _fetchCommerceTaxFixedRateAddressRel(
			curZip -> commerceTaxFixedRateAddressRelPersistence.findByC_C_C_R_Z(
				commerceTaxMethodId, cpTaxCategoryId, countryIds, regionIds,
				curZip, 0, 1, _orderByComparator),
			zip);
	}

	@Override
	public CommerceTaxFixedRateAddressRel fetchCommerceTaxFixedRateAddressRel(
		long commerceTaxMethodId, long countryId, long regionId, String zip) {

		long[] countryIds = {countryId, 0};
		long[] regionIds = {regionId, 0};

		return _fetchCommerceTaxFixedRateAddressRel(
			curZip -> commerceTaxFixedRateAddressRelPersistence.findByC_C_R_Z(
				commerceTaxMethodId, countryIds, regionIds, curZip, 0, 1,
				_orderByComparator),
			zip);
	}

	@Override
	public List<CommerceTaxFixedRateAddressRel>
		getCommerceTaxFixedRateAddressRels(
			long cpTaxCategoryId, int start, int end) {

		return commerceTaxFixedRateAddressRelPersistence.findByCPTaxCategoryId(
			cpTaxCategoryId, start, end);
	}

	@Override
	public List<CommerceTaxFixedRateAddressRel>
		getCommerceTaxFixedRateAddressRels(
			long cpTaxCategoryId, int start, int end,
			OrderByComparator<CommerceTaxFixedRateAddressRel>
				orderByComparator) {

		return commerceTaxFixedRateAddressRelPersistence.findByCPTaxCategoryId(
			cpTaxCategoryId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceTaxFixedRateAddressRelsCount(long cpTaxCategoryId) {
		return commerceTaxFixedRateAddressRelPersistence.countByCPTaxCategoryId(
			cpTaxCategoryId);
	}

	@Override
	public List<CommerceTaxFixedRateAddressRel>
		getCommerceTaxMethodFixedRateAddressRels(
			long commerceTaxMethodId, int start, int end) {

		return commerceTaxFixedRateAddressRelPersistence.
			findByCommerceTaxMethodId(commerceTaxMethodId, start, end);
	}

	@Override
	public List<CommerceTaxFixedRateAddressRel>
		getCommerceTaxMethodFixedRateAddressRels(
			long commerceTaxMethodId, int start, int end,
			OrderByComparator<CommerceTaxFixedRateAddressRel>
				orderByComparator) {

		return commerceTaxFixedRateAddressRelPersistence.
			findByCommerceTaxMethodId(
				commerceTaxMethodId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceTaxMethodFixedRateAddressRelsCount(
		long commerceTaxMethodId) {

		return commerceTaxFixedRateAddressRelPersistence.
			countByCommerceTaxMethodId(commerceTaxMethodId);
	}

	@Override
	public CommerceTaxFixedRateAddressRel updateCommerceTaxFixedRateAddressRel(
			long commerceTaxFixedRateAddressRelId, long countryId,
			long regionId, String zip, double rate)
		throws PortalException {

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			commerceTaxFixedRateAddressRelPersistence.findByPrimaryKey(
				commerceTaxFixedRateAddressRelId);

		commerceTaxFixedRateAddressRel.setCountryId(countryId);
		commerceTaxFixedRateAddressRel.setRegionId(regionId);
		commerceTaxFixedRateAddressRel.setZip(zip);
		commerceTaxFixedRateAddressRel.setRate(rate);

		return commerceTaxFixedRateAddressRelPersistence.update(
			commerceTaxFixedRateAddressRel);
	}

	private CommerceTaxFixedRateAddressRel _fetchCommerceTaxFixedRateAddressRel(
		Function<String, List<CommerceTaxFixedRateAddressRel>> function,
		String zip) {

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel = null;

		List<CommerceTaxFixedRateAddressRel> commerceTaxFixedRateAddressRels =
			function.apply(StringPool.BLANK);

		if (!commerceTaxFixedRateAddressRels.isEmpty()) {
			commerceTaxFixedRateAddressRel =
				commerceTaxFixedRateAddressRels.get(0);
		}

		if (Validator.isBlank(zip)) {
			return commerceTaxFixedRateAddressRel;
		}

		commerceTaxFixedRateAddressRels = function.apply(zip);

		if (commerceTaxFixedRateAddressRels.isEmpty()) {
			return commerceTaxFixedRateAddressRel;
		}

		CommerceTaxFixedRateAddressRel zipCommerceTaxFixedRateAddressRel =
			commerceTaxFixedRateAddressRels.get(0);

		if ((commerceTaxFixedRateAddressRel == null) ||
			(commerceTaxFixedRateAddressRel.getPrimaryKey() >
				zipCommerceTaxFixedRateAddressRel.getPrimaryKey())) {

			return zipCommerceTaxFixedRateAddressRel;
		}

		return commerceTaxFixedRateAddressRel;
	}

	private static final OrderByComparator<CommerceTaxFixedRateAddressRel>
		_orderByComparator = OrderByComparatorFactoryUtil.create(
			CommerceTaxFixedRateAddressRelModelImpl.TABLE_NAME,
			"commerceTaxFixedRateAddressRelId", true);

	@Reference
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}