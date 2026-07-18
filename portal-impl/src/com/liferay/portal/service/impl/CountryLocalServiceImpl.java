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
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.CountryA2Exception;
import com.liferay.portal.kernel.exception.CountryA3Exception;
import com.liferay.portal.kernel.exception.CountryNameException;
import com.liferay.portal.kernel.exception.CountryNumberException;
import com.liferay.portal.kernel.exception.CountryTitleException;
import com.liferay.portal.kernel.exception.DuplicateCountryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryLocalization;
import com.liferay.portal.kernel.model.CountryLocalizationTable;
import com.liferay.portal.kernel.model.CountryTable;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.base.CountryLocalServiceBaseImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @see CountryLocalServiceBaseImpl
 */
public class CountryLocalServiceImpl extends CountryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Country addCountry(
			String externalReferenceCode, String a2, String a3, boolean active,
			boolean billingAllowed, String idd, String name, String number,
			double position, boolean shippingAllowed, boolean subjectToVAT,
			boolean zipRequired, ServiceContext serviceContext)
		throws PortalException {

		// Country

		_validate(0, serviceContext.getCompanyId(), a2, a3, name, number);

		if (Validator.isNull(externalReferenceCode)) {
			externalReferenceCode = a2;
		}

		long countryId = counterLocalService.increment();

		Country country = countryPersistence.create(countryId);

		User user = _userPersistence.findByPrimaryKey(
			serviceContext.getUserId());

		country.setExternalReferenceCode(externalReferenceCode);
		country.setCompanyId(user.getCompanyId());
		country.setUserId(user.getUserId());
		country.setUserName(user.getFullName());

		country.setDefaultLanguageId(
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()));
		country.setA2(a2);
		country.setA3(a3);
		country.setActive(active);
		country.setBillingAllowed(billingAllowed);
		country.setGroupFilterEnabled(false);
		country.setIdd(idd);
		country.setName(name);
		country.setNumber(number);
		country.setPosition(position);
		country.setShippingAllowed(shippingAllowed);
		country.setSubjectToVAT(subjectToVAT);
		country.setZipRequired(zipRequired);
		country.setStatus(WorkflowConstants.STATUS_APPROVED);

		country = countryPersistence.update(country);

		// Resources

		_resourceLocalService.addResources(
			user.getCompanyId(), 0, user.getUserId(), Country.class.getName(),
			country.getCountryId(), false, false, false);

		return country;
	}

	@Override
	public void deleteCompanyCountries(long companyId) throws PortalException {
		List<Country> countries = countryPersistence.findByCompanyId(companyId);

		for (Country country : countries) {
			countryLocalService.deleteCountry(country);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Country deleteCountry(Country country) throws PortalException {

		// Country

		countryPersistence.remove(country);

		// Addresses

		_addressLocalService.deleteCountryAddresses(country.getCountryId());

		// Organizations

		_updateOrganizations(country.getCountryId());

		// Regions

		_regionLocalService.deleteCountryRegions(country.getCountryId());

		// Resources

		_resourceLocalService.deleteResource(
			country, ResourceConstants.SCOPE_INDIVIDUAL);

		return country;
	}

	@Override
	public Country deleteCountry(long countryId) throws PortalException {
		Country country = countryPersistence.findByPrimaryKey(countryId);

		return countryLocalService.deleteCountry(country);
	}

	@Override
	public Country fetchCountryByA2(long companyId, String a2) {
		return countryPersistence.fetchByC_A2(companyId, a2);
	}

	@Override
	public Country fetchCountryByA3(long companyId, String a3) {
		return countryPersistence.fetchByC_A3(companyId, a3);
	}

	@Override
	public Country fetchCountryByName(long companyId, String name) {
		return countryPersistence.fetchByC_Name(companyId, name);
	}

	@Override
	public Country fetchCountryByNumber(long companyId, String number) {
		return countryPersistence.fetchByC_Number(companyId, number);
	}

	@Override
	public List<Country> getCompanyCountries(long companyId) {
		return countryPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<Country> getCompanyCountries(long companyId, boolean active) {
		return countryPersistence.findByC_Active(companyId, active);
	}

	@Override
	public List<Country> getCompanyCountries(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return countryPersistence.findByC_Active(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public List<Country> getCompanyCountries(
		long companyId, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return countryPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCompanyCountriesCount(long companyId) {
		return countryPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getCompanyCountriesCount(long companyId, boolean active) {
		return countryPersistence.countByC_Active(companyId, active);
	}

	@Override
	public Country getCountryByA2(long companyId, String a2)
		throws PortalException {

		return countryPersistence.findByC_A2(companyId, a2);
	}

	@Override
	public Country getCountryByA3(long companyId, String a3)
		throws PortalException {

		return countryPersistence.findByC_A3(companyId, a3);
	}

	@Override
	public Country getCountryByName(long companyId, String name)
		throws PortalException {

		return countryPersistence.findByC_Name(companyId, name);
	}

	@Override
	public Country getCountryByNumber(long companyId, String number)
		throws PortalException {

		return countryPersistence.findByC_Number(companyId, number);
	}

	@Override
	public Country getOrAddEmptyCountry(
			String externalReferenceCode, String a2, String a3, long companyId,
			String name, long userId)
		throws PortalException {

		return EmptyModelManagerUtil.getOrAddEmptyModel(
			Country.class, companyId, externalReferenceCode,
			this::fetchCountryByExternalReferenceCode,
			this::getCountryByExternalReferenceCode,
			() -> {
				String countryName =
					(fetchCountryByName(companyId, name) != null) ?
						externalReferenceCode : name;

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCompanyId(companyId);
				serviceContext.setUserId(userId);

				Country country = countryLocalService.addCountry(
					externalReferenceCode, a2, a3, false, false, null,
					countryName, externalReferenceCode, 0D, false, false, false,
					serviceContext);

				country.setStatus(WorkflowConstants.STATUS_EMPTY);

				return countryPersistence.update(country);
			},
			"country");
	}

	@Override
	public BaseModelSearchResult<Country> searchCountries(
			long companyId, Boolean active, String keywords, int start, int end,
			OrderByComparator<Country> orderByComparator)
		throws PortalException {

		return BaseModelSearchResult.unsafeCreateWithStartAndEnd(
			startAndEnd -> countryPersistence.dslQuery(
				_getGroupByStep(
					DSLQueryFactoryUtil.selectDistinct(CountryTable.INSTANCE),
					companyId, active, keywords
				).orderBy(
					CountryTable.INSTANCE, orderByComparator
				).limit(
					startAndEnd.getStart(), startAndEnd.getEnd()
				)),
			countryPersistence.dslQueryCount(
				_getGroupByStep(
					DSLQueryFactoryUtil.countDistinct(
						CountryTable.INSTANCE.countryId),
					companyId, active, keywords)),
			start, end);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Country updateActive(long countryId, boolean active)
		throws PortalException {

		Country country = countryPersistence.findByPrimaryKey(countryId);

		country.setActive(active);

		return countryPersistence.update(country);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Country updateCountry(
			String externalReferenceCode, long countryId, String a2, String a3,
			boolean active, boolean billingAllowed, String idd, String name,
			String number, double position, boolean shippingAllowed,
			boolean subjectToVAT)
		throws PortalException {

		Country country = countryPersistence.findByPrimaryKey(countryId);

		_validate(countryId, country.getCompanyId(), a2, a3, name, number);

		if (Validator.isNotNull(externalReferenceCode)) {
			country.setExternalReferenceCode(externalReferenceCode);
		}

		country.setA2(a2);
		country.setA3(a3);
		country.setActive(active);
		country.setBillingAllowed(billingAllowed);
		country.setIdd(idd);
		country.setName(name);
		country.setNumber(number);
		country.setPosition(position);
		country.setShippingAllowed(shippingAllowed);

		if (country.getStatus() == WorkflowConstants.STATUS_EMPTY) {
			country.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		country.setSubjectToVAT(subjectToVAT);

		return countryPersistence.update(country);
	}

	@Override
	public List<CountryLocalization> updateCountryLocalizations(
			Country country, Map<String, String> titleMap)
		throws PortalException {

		_validateCountryLocalizationTitle(titleMap);

		return super.updateCountryLocalizations(country, titleMap);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Country updateGroupFilterEnabled(
			long countryId, boolean groupFilterEnabled)
		throws PortalException {

		Country country = countryPersistence.findByPrimaryKey(countryId);

		country.setGroupFilterEnabled(groupFilterEnabled);

		return countryPersistence.update(country);
	}

	private GroupByStep _getGroupByStep(
			FromStep fromStep, long companyId, Boolean active, String keywords)
		throws PortalException {

		JoinStep joinStep = fromStep.from(
			CountryTable.INSTANCE
		).leftJoinOn(
			CountryLocalizationTable.INSTANCE,
			CountryTable.INSTANCE.countryId.eq(
				CountryLocalizationTable.INSTANCE.countryId)
		);

		return joinStep.where(
			CountryTable.INSTANCE.companyId.eq(
				companyId
			).and(
				() -> {
					if (active == null) {
						return null;
					}

					return CountryTable.INSTANCE.active.eq(active);
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
									CountryTable.INSTANCE.a2,
									CountryTable.INSTANCE.a3,
									CountryTable.INSTANCE.name,
									CountryTable.INSTANCE.number,
									CountryLocalizationTable.INSTANCE.title
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
			));
	}

	private boolean _isDuplicateCountry(Country country, long countryId) {
		if ((country != null) && (country.getCountryId() != countryId)) {
			return true;
		}

		return false;
	}

	private void _updateOrganizations(long countryId) throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			_organizationLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property countryIdProperty = PropertyFactoryUtil.forName(
					"countryId");

				dynamicQuery.add(countryIdProperty.eq(countryId));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(Organization organization) -> {
				organization.setCountryId(0);
				organization.setRegionId(0);

				_organizationLocalService.updateOrganization(organization);
			});

		actionableDynamicQuery.performActions();
	}

	private void _validate(
			long countryId, long companyId, String a2, String a3, String name,
			String number)
		throws PortalException {

		if (Validator.isNull(a2)) {
			throw new CountryA2Exception("Missing A2");
		}

		if (a2.length() != 2) {
			throw new CountryA2Exception("A2 must be exactly two characters");
		}

		if (Validator.isNull(a3)) {
			throw new CountryA3Exception("Missing A3");
		}

		if (a3.length() != 3) {
			throw new CountryA3Exception("A3 must be exactly three characters");
		}

		if (Validator.isNull(name)) {
			throw new CountryNameException("Missing name");
		}

		if (Validator.isNull(number)) {
			throw new CountryNumberException("Missing number");
		}

		if (CompanyThreadLocal.isInitializingPortalInstance() ||
			LazyReferencingThreadLocal.isEnabled()) {

			return;
		}

		if (_isDuplicateCountry(fetchCountryByA2(companyId, a2), countryId)) {
			throw new DuplicateCountryException(
				"A2 belongs to another country");
		}

		if (_isDuplicateCountry(fetchCountryByA3(companyId, a3), countryId)) {
			throw new DuplicateCountryException(
				"A3 belongs to another country");
		}

		if (_isDuplicateCountry(
				fetchCountryByName(companyId, name), countryId)) {

			throw new DuplicateCountryException(
				"Name belongs to another country");
		}

		if (_isDuplicateCountry(
				fetchCountryByNumber(companyId, number), countryId)) {

			throw new DuplicateCountryException(
				"Number belongs to another country");
		}
	}

	private void _validateCountryLocalizationTitle(Map<String, String> titleMap)
		throws CountryTitleException.MustNotExceedMaximumLength {

		int titleMaxLength = ModelHintsUtil.getMaxLength(
			CountryLocalization.class.getName(), "title");

		for (Map.Entry<String, String> entry : titleMap.entrySet()) {
			String title = entry.getValue();

			if (Validator.isNull(title) || (title.length() <= titleMaxLength)) {
				continue;
			}

			throw new CountryTitleException.MustNotExceedMaximumLength(
				title, titleMaxLength);
		}
	}

	@BeanReference(type = AddressLocalService.class)
	private AddressLocalService _addressLocalService;

	@BeanReference(type = OrganizationLocalService.class)
	private OrganizationLocalService _organizationLocalService;

	@BeanReference(type = RegionLocalService.class)
	private RegionLocalService _regionLocalService;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}