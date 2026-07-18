/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManagerUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.DuplicateRegionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RegionCodeException;
import com.liferay.portal.kernel.exception.RegionNameException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationTable;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.RegionLocalizationTable;
import com.liferay.portal.kernel.model.RegionTable;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.CountryPersistence;
import com.liferay.portal.kernel.service.persistence.OrganizationPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.base.RegionLocalServiceBaseImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class RegionLocalServiceImpl extends RegionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Region addRegion(
			String externalReferenceCode, long countryId, boolean active,
			String name, double position, String regionCode,
			ServiceContext serviceContext)
		throws PortalException {

		Country country = _countryPersistence.findByPrimaryKey(countryId);

		_validate(-1, countryId, name, regionCode);

		if (Validator.isNull(externalReferenceCode)) {
			externalReferenceCode = country.getA2() + "_" + regionCode;
		}

		Region region = regionPersistence.create(
			counterLocalService.increment(Region.class.getName()));

		region.setExternalReferenceCode(externalReferenceCode);
		region.setCompanyId(serviceContext.getCompanyId());

		User user = _userPersistence.findByPrimaryKey(
			serviceContext.getUserId());

		region.setUserId(user.getUserId());
		region.setUserName(user.getFullName());

		region.setCountryId(countryId);
		region.setActive(active);
		region.setName(name);
		region.setPosition(position);
		region.setRegionCode(regionCode);
		region.setStatus(WorkflowConstants.STATUS_APPROVED);

		return regionPersistence.update(region);
	}

	@Override
	public void deleteCountryRegions(long countryId) {
		for (Region region :
				getRegions(
					countryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			deleteRegion(region);
		}
	}

	@Override
	public Region deleteRegion(long regionId) throws PortalException {
		Region region = regionPersistence.findByPrimaryKey(regionId);

		return deleteRegion(region);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Region deleteRegion(Region region) {

		// Region

		regionPersistence.remove(region);

		// Address

		_addressLocalService.deleteRegionAddresses(region.getRegionId());

		// Organizations

		for (Organization organization :
				_organizationPersistence.<List<Organization>>dslQuery(
					DSLQueryFactoryUtil.select(
						OrganizationTable.INSTANCE
					).from(
						OrganizationTable.INSTANCE
					).where(
						OrganizationTable.INSTANCE.regionId.eq(
							region.getRegionId())
					))) {

			organization.setRegionId(0);

			_organizationLocalService.updateOrganization(organization);
		}

		return region;
	}

	@Override
	public Region fetchRegion(long countryId, String regionCode) {
		return regionPersistence.fetchByC_R(countryId, regionCode);
	}

	@Override
	public Region getOrAddEmptyRegion(
			String externalReferenceCode, long companyId, long userId,
			long countryId, String regionCode, String name)
		throws PortalException {

		return EmptyModelManagerUtil.getOrAddEmptyModel(
			Region.class, companyId, externalReferenceCode,
			this::fetchRegionByExternalReferenceCode,
			this::getRegionByExternalReferenceCode,
			() -> {
				String regionCodeString =
					(fetchRegion(countryId, regionCode) != null) ?
						externalReferenceCode : regionCode;

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCompanyId(companyId);
				serviceContext.setUserId(userId);

				Region region = regionLocalService.addRegion(
					externalReferenceCode, countryId, false,
					GetterUtil.getString(name), 0D, regionCodeString,
					serviceContext);

				region.setStatus(WorkflowConstants.STATUS_EMPTY);

				return regionPersistence.update(region);
			},
			"region");
	}

	@Override
	public Region getRegion(long countryId, String regionCode)
		throws PortalException {

		return regionPersistence.findByC_R(countryId, regionCode);
	}

	@Override
	public List<Region> getRegions(long countryId, boolean active)
		throws PortalException {

		return regionPersistence.findByC_A(countryId, active);
	}

	@Override
	public List<Region> getRegions(
		long countryId, boolean active, int start, int end,
		OrderByComparator<Region> orderByComparator) {

		return regionPersistence.findByC_A(
			countryId, active, start, end, orderByComparator);
	}

	@Override
	public List<Region> getRegions(
		long countryId, int start, int end,
		OrderByComparator<Region> orderByComparator) {

		return regionPersistence.findByCountryId(
			countryId, start, end, orderByComparator);
	}

	@Override
	public List<Region> getRegions(long companyId, String a2, boolean active)
		throws PortalException {

		Country country = _countryPersistence.findByC_A2(companyId, a2);

		return regionPersistence.findByC_A(country.getCountryId(), active);
	}

	@Override
	public int getRegionsCount(long countryId) {
		return regionPersistence.countByCountryId(countryId);
	}

	@Override
	public int getRegionsCount(long countryId, boolean active) {
		return regionPersistence.countByC_A(countryId, active);
	}

	@Override
	public BaseModelSearchResult<Region> searchRegions(
			long companyId, Boolean active, String keywords,
			LinkedHashMap<String, Object> params, int start, int end,
			OrderByComparator<Region> orderByComparator)
		throws PortalException {

		return BaseModelSearchResult.unsafeCreateWithStartAndEnd(
			startAndEnd -> regionPersistence.dslQuery(
				_getGroupByStep(
					DSLQueryFactoryUtil.selectDistinct(RegionTable.INSTANCE),
					companyId, active, keywords, params
				).orderBy(
					RegionTable.INSTANCE, orderByComparator
				).limit(
					startAndEnd.getStart(), startAndEnd.getEnd()
				)),
			regionPersistence.dslQueryCount(
				_getGroupByStep(
					DSLQueryFactoryUtil.countDistinct(
						RegionTable.INSTANCE.regionId),
					companyId, active, keywords, params)),
			start, end);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Region updateActive(long regionId, boolean active)
		throws PortalException {

		Region region = regionPersistence.findByPrimaryKey(regionId);

		region.setActive(active);

		return regionPersistence.update(region);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Region updateRegion(
			String externalReferenceCode, long regionId, boolean active,
			String name, double position, String regionCode)
		throws PortalException {

		Region region = regionPersistence.findByPrimaryKey(regionId);

		_validate(regionId, region.getCountryId(), name, regionCode);

		if (Validator.isNotNull(externalReferenceCode)) {
			region.setExternalReferenceCode(externalReferenceCode);
		}

		region.setActive(active);
		region.setName(name);
		region.setPosition(position);
		region.setRegionCode(regionCode);

		if (region.getStatus() == WorkflowConstants.STATUS_EMPTY) {
			region.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		return regionPersistence.update(region);
	}

	private OrderByStep _getGroupByStep(
		FromStep fromStep, long companyId, Boolean active, String keywords,
		LinkedHashMap<String, Object> params) {

		JoinStep joinStep = fromStep.from(
			RegionTable.INSTANCE
		).leftJoinOn(
			RegionLocalizationTable.INSTANCE,
			RegionTable.INSTANCE.regionId.eq(
				RegionLocalizationTable.INSTANCE.regionId)
		);

		return joinStep.where(
			RegionTable.INSTANCE.companyId.eq(
				companyId
			).and(
				() -> {
					if (active == null) {
						return null;
					}

					return RegionTable.INSTANCE.active.eq(active);
				}
			).and(
				() -> {
					if (Validator.isNull(keywords)) {
						return null;
					}

					Predicate keywordsPredicate = null;

					for (String keyword :
							CustomSQLUtil.keywords(keywords, true)) {

						for (Column<?, String> column :
								new Column[] {
									RegionTable.INSTANCE.name,
									RegionTable.INSTANCE.regionCode,
									RegionLocalizationTable.INSTANCE.title
								}) {

							keywordsPredicate = Predicate.or(
								keywordsPredicate,
								DSLFunctionFactoryUtil.lower(
									column
								).like(
									keyword
								));
						}
					}

					return Predicate.withParentheses(keywordsPredicate);
				}
			).and(
				() -> {
					if (MapUtil.isEmpty(params)) {
						return null;
					}

					long countryId = (long)params.get("countryId");

					if (countryId > 0) {
						return RegionTable.INSTANCE.countryId.eq(countryId);
					}

					return null;
				}
			));
	}

	private void _validate(
			long regionId, long countryId, String name, String regionCode)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new RegionNameException("Name is null");
		}

		if (Validator.isNull(regionCode)) {
			throw new RegionCodeException("Region code is null");
		}

		if (CompanyThreadLocal.isInitializingPortalInstance() ||
			LazyReferencingThreadLocal.isEnabled()) {

			return;
		}

		Region region = fetchRegion(countryId, regionCode);

		if ((region != null) && (region.getRegionId() != regionId)) {
			throw new DuplicateRegionException(
				"Region code belongs to another region");
		}
	}

	@BeanReference(type = AddressLocalService.class)
	private AddressLocalService _addressLocalService;

	@BeanReference(type = CountryPersistence.class)
	private CountryPersistence _countryPersistence;

	@BeanReference(type = OrganizationLocalService.class)
	private OrganizationLocalService _organizationLocalService;

	@BeanReference(type = OrganizationPersistence.class)
	private OrganizationPersistence _organizationPersistence;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}