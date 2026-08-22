/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.search.spi.model.index.contributor;

import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.AddressTable;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryConstants;
import com.liferay.portal.kernel.model.CountryTable;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationTable;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.RegionTable;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Fabiano Nazar
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.Organization",
	service = ModelDocumentContributor.class
)
public class OrganizationModelDocumentContributor
	implements ModelDocumentContributor<Organization> {

	@Override
	public void contribute(Document document, Organization organization) {
		try {
			document.addKeyword(Field.COMPANY_ID, organization.getCompanyId());
			document.addText(Field.NAME, organization.getName());
			document.addKeyword(
				Field.ORGANIZATION_ID, organization.getOrganizationId());

			String treePath = organization.getTreePath();

			document.addKeyword(Field.TREE_PATH, treePath);

			document.addKeyword(Field.TYPE, organization.getType());
			document.addTextSortable(Field.TYPE, organization.getType());
			document.addTextSortable(
				"nameTreePath", _buildNameTreePath(treePath));
			document.addKeyword(
				"parentOrganizationId", organization.getParentOrganizationId());
			document.remove(Field.USER_NAME);

			_populateAddresses(document, organization);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	private String _buildNameTreePath(String treePath) {
		List<String> organizationNames = new ArrayList<>();

		for (String organizationIdString :
				StringUtil.split(treePath, CharPool.SLASH)) {

			if (organizationIdString.isEmpty()) {
				continue;
			}

			String name = _getOrganizationName(organizationIdString);

			if (name != null) {
				organizationNames.add(name);
			}
		}

		return StringUtil.merge(organizationNames, " > ");
	}

	private AddressData _getAddressData(Organization organization) {
		Map<Long, AddressData> addressDataMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				OrganizationModelDocumentContributor.class.getName() +
					"#_getAddressData",
				count -> {
					Map<Long, List<List<String>>> addressValuesMap =
						new HashMap<>();

					for (Object[] values :
							_addressLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									AddressTable.INSTANCE.classPK,
									AddressTable.INSTANCE.city,
									CountryTable.INSTANCE.name.as(
										"countryName"),
									RegionTable.INSTANCE.name.as("regionName"),
									AddressTable.INSTANCE.street1,
									AddressTable.INSTANCE.street2,
									AddressTable.INSTANCE.street3,
									AddressTable.INSTANCE.zip
								).from(
									AddressTable.INSTANCE
								).leftJoinOn(
									CountryTable.INSTANCE,
									AddressTable.INSTANCE.countryId.eq(
										CountryTable.INSTANCE.countryId)
								).leftJoinOn(
									RegionTable.INSTANCE,
									AddressTable.INSTANCE.regionId.eq(
										RegionTable.INSTANCE.regionId)
								).where(
									AddressTable.INSTANCE.companyId.eq(
										organization.getCompanyId()
									).and(
										AddressTable.INSTANCE.classNameId.eq(
											_classNameLocalService.
												getClassNameId(
													Organization.class.
														getName()))
									)
								),
								false)) {

						List<List<String>> addressValues =
							addressValuesMap.computeIfAbsent(
								(Long)values[0],
								key -> Arrays.asList(
									new ArrayList<>(), new ArrayList<>(),
									new ArrayList<>(), new ArrayList<>(),
									new ArrayList<>()));

						List<String> cities = addressValues.get(0);

						cities.add(StringUtil.toLowerCase((String)values[1]));

						List<String> countryNames = addressValues.get(1);

						countryNames.addAll(
							_getCountryNames((String)values[2]));

						List<String> regionNames = addressValues.get(2);

						regionNames.add(
							StringUtil.toLowerCase((String)values[3]));

						List<String> streets = addressValues.get(3);

						streets.add(StringUtil.toLowerCase((String)values[4]));
						streets.add(StringUtil.toLowerCase((String)values[5]));
						streets.add(StringUtil.toLowerCase((String)values[6]));

						List<String> zips = addressValues.get(4);

						zips.add(StringUtil.toLowerCase((String)values[7]));
					}

					Map<Long, AddressData> localAddressDataMap =
						new HashMap<>();

					for (Map.Entry<Long, List<List<String>>> entry :
							addressValuesMap.entrySet()) {

						List<List<String>> lists = entry.getValue();

						localAddressDataMap.put(
							entry.getKey(),
							new AddressData(
								lists.get(0), lists.get(1), lists.get(2),
								lists.get(3), lists.get(4)));
					}

					return localAddressDataMap;
				});

		if (addressDataMap == null) {
			List<String> cities = new ArrayList<>();
			List<String> countryNames = new ArrayList<>();
			List<String> regionNames = new ArrayList<>();
			List<String> streets = new ArrayList<>();
			List<String> zips = new ArrayList<>();

			for (Address address : organization.getAddresses()) {
				cities.add(StringUtil.toLowerCase(address.getCity()));

				Country country = address.getCountry();

				countryNames.addAll(_getCountryNames(country.getName()));

				Region region = address.getRegion();

				regionNames.add(StringUtil.toLowerCase(region.getName()));

				streets.add(StringUtil.toLowerCase(address.getStreet1()));
				streets.add(StringUtil.toLowerCase(address.getStreet2()));
				streets.add(StringUtil.toLowerCase(address.getStreet3()));

				zips.add(StringUtil.toLowerCase(address.getZip()));
			}

			return new AddressData(
				cities, countryNames, regionNames, streets, zips);
		}

		return addressDataMap.get(organization.getOrganizationId());
	}

	private String _getCountryName(Locale locale, String name) {
		String localizedName = LanguageUtil.get(
			locale, CountryConstants.NAME_PREFIX + name);

		if (!localizedName.startsWith(CountryConstants.NAME_PREFIX)) {
			return localizedName;
		}

		return name;
	}

	private Set<String> _getCountryNames(String name) {
		Set<String> countryNames = new HashSet<>();

		for (Locale locale : _language.getAvailableLocales()) {
			countryNames.add(
				StringUtil.toLowerCase(_getCountryName(locale, name)));
		}

		return countryNames;
	}

	private String _getOrganizationName(String organizationIdString) {
		Map<String, String> organizationNames =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				OrganizationModelDocumentContributor.class.getName() +
					"#_getOrganizationName",
				count -> {
					Map<String, String> localOrganizationNames =
						new HashMap<>();

					for (Object[] values :
							_organizationLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									OrganizationTable.INSTANCE.organizationId,
									OrganizationTable.INSTANCE.name
								).from(
									OrganizationTable.INSTANCE
								),
								false)) {

						localOrganizationNames.put(
							String.valueOf(values[0]), (String)values[1]);
					}

					return localOrganizationNames;
				});

		if (organizationNames == null) {
			Organization organization =
				_organizationLocalService.fetchOrganization(
					GetterUtil.getLong(organizationIdString));

			if (organization == null) {
				return null;
			}

			return organization.getName();
		}

		return organizationNames.get(organizationIdString);
	}

	private void _populateAddresses(
			Document document, Organization organization)
		throws PortalException {

		List<String> countryNames = null;

		if (organization.getCountryId() > 0) {
			try {
				Country country = _countryService.getCountry(
					organization.getCountryId());

				countryNames = new ArrayList<>(
					_getCountryNames(country.getName()));
			}
			catch (NoSuchCountryException noSuchCountryException) {
				if (_log.isWarnEnabled()) {
					_log.warn(noSuchCountryException);
				}
			}
		}

		List<String> regionNames = null;

		if (organization.getRegionId() > 0) {
			try {
				Region region = _regionService.getRegion(
					organization.getRegionId());

				regionNames = new ArrayList<>(
					Arrays.asList(StringUtil.toLowerCase(region.getName())));
			}
			catch (NoSuchRegionException noSuchRegionException) {
				if (_log.isWarnEnabled()) {
					_log.warn(noSuchRegionException);
				}
			}
		}

		AddressData addressData = _getAddressData(organization);

		if ((addressData == null) &&
			((countryNames != null) || (regionNames != null))) {

			addressData = new AddressData(
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());
		}

		if (addressData != null) {
			addressData.contribute(document, countryNames, regionNames);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationModelDocumentContributor.class);

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CountryService _countryService;

	@Reference
	private Language _language;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private RegionService _regionService;

	private static class AddressData {

		public void contribute(
			Document document, List<String> countryNames,
			List<String> regionNames) {

			document.addText("city", _cities);

			if (countryNames == null) {
				document.addText("country", _countryNames);
			}
			else {
				Collections.addAll(countryNames, _countryNames);

				document.addText(
					"country", countryNames.toArray(new String[0]));
			}

			if (regionNames == null) {
				document.addText("region", _regionNames);
				document.addKeyword(
					Field.getSortableFieldName("region"), _regionNames);
			}
			else {
				Collections.addAll(regionNames, _regionNames);

				String[] regionNamesArray = regionNames.toArray(new String[0]);

				document.addText("region", regionNamesArray);
				document.addKeyword(
					Field.getSortableFieldName("region"), regionNamesArray);
			}

			document.addText("street", _streets);
			document.addText("zip", _zips);
		}

		private AddressData(
			List<String> cities, List<String> countryNames,
			List<String> regionNames, List<String> streets, List<String> zips) {

			_cities = cities.toArray(new String[0]);
			_countryNames = countryNames.toArray(new String[0]);
			_regionNames = regionNames.toArray(new String[0]);
			_streets = streets.toArray(new String[0]);
			_zips = zips.toArray(new String[0]);
		}

		private final String[] _cities;
		private final String[] _countryNames;
		private final String[] _regionNames;
		private final String[] _streets;
		private final String[] _zips;

	}

}