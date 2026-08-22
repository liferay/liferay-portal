/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search.spi.model.index.contributor;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.AddressTable;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ContactTable;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryConstants;
import com.liferay.portal.kernel.model.CountryTable;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationTable;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.RegionTable;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleTable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.UserGroupRoleTable;
import com.liferay.portal.kernel.model.UserGroups_TeamsTable;
import com.liferay.portal.kernel.model.Users_GroupsTable;
import com.liferay.portal.kernel.model.Users_OrgsTable;
import com.liferay.portal.kernel.model.Users_RolesTable;
import com.liferay.portal.kernel.model.Users_TeamsTable;
import com.liferay.portal.kernel.model.Users_UserGroupsTable;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.User",
	service = ModelDocumentContributor.class
)
public class UserModelDocumentContributor
	implements ModelDocumentContributor<User> {

	@Override
	public void contribute(Document document, User user) {
		try {
			AddressData addressData = _getAddressData(user);

			if (addressData != null) {
				addressData.contribute(document);
			}

			OrganizationData organizationData = _getOrganizationData(user);

			if (organizationData != null) {
				organizationData.contribute(document);
			}

			RoleData roleData = _getRoleData(user);

			if (roleData != null) {
				roleData.contribute(document);
			}

			UserGroupRoleData userGroupRoleData = _getUserGroupRoleData(user);

			if (userGroupRoleData != null) {
				userGroupRoleData.contribute(document);
			}

			document.addKeyword(Field.COMPANY_ID, user.getCompanyId());
			document.addDate(Field.CREATE_DATE, user.getCreateDate());

			long[] activeTransitiveGroupIds = _getActiveTransitiveGroupIds(
				user);

			document.addKeyword(Field.GROUP_ID, activeTransitiveGroupIds);

			document.addDate(Field.MODIFIED_DATE, user.getModifiedDate());
			document.addKeyword(Field.SCOPE_GROUP_ID, activeTransitiveGroupIds);
			document.addKeyword(Field.STATUS, user.getStatus());
			document.addKeyword(Field.TYPE, user.getType());
			document.addKeyword(Field.USER_ID, user.getUserId());
			document.addKeyword(Field.USER_NAME, user.getFullName(), true);
			document.addText(
				Field.getSortableFieldName(Field.USER_NAME),
				StringUtil.toLowerCase(user.getFullName()));
			document.addDate("birthDate", _getBirthday(user));
			document.addKeyword("defaultUser", user.isDefaultUser());
			document.addKeyword("emailAddress", user.getEmailAddress());
			document.addKeyword(
				"emailAddressDomain",
				_getEmailAddressDomain(user.getEmailAddress()));
			document.addText("firstName", user.getFirstName());
			document.addText("fullName", user.getFullName());
			document.addKeyword("groupIds", _getGroupIds(user));
			document.addText("jobTitle", user.getJobTitle());

			if (FeatureFlagManagerUtil.isEnabled("LPD-36010")) {
				boolean hasLoginDate = false;

				if (user.getLastLoginDate() != null) {
					hasLoginDate = true;
				}

				document.addKeyword("hasLoginDate", hasLoginDate);
			}
			else {
				document.addDate("lastLoginDate", user.getLastLoginDate());
			}

			document.addText("lastName", user.getLastName());
			document.addText("middleName", user.getMiddleName());
			document.addText("screenName", user.getScreenName());
			document.addKeyword("teamIds", _getTeamIds(user));
			document.addKeyword("userGroupIds", _getUserGroupIds(user));

			for (Locale locale : _language.getAvailableLocales()) {
				String languageId = LocaleUtil.toLanguageId(locale);

				document.addText(
					_localization.getLocalizedName("firstName", languageId),
					user.getFirstName());
				document.addText(
					_localization.getLocalizedName("fullName", languageId),
					user.getFullName());
				document.addText(
					_localization.getLocalizedName("lastName", languageId),
					user.getLastName());
				document.addText(
					_localization.getLocalizedName("middleName", languageId),
					user.getMiddleName());
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to index user " + user.getUserId(), exception);
			}
		}
	}

	@Reference
	protected CountryService countryService;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected IndexWriterHelper indexWriterHelper;

	@Reference
	protected OrganizationLocalService organizationLocalService;

	@Reference
	protected RegionService regionService;

	@Reference
	protected UserGroupRoleLocalService userGroupRoleLocalService;

	private long[] _getActiveTransitiveGroupIds(User user)
		throws PortalException {

		Map<Long, long[]> activeTransitiveGroupIdsMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				UserModelDocumentContributor.class.getName() +
					"#_getActiveTransitiveGroupIds",
				count -> groupLocalService.getUserInheritedSiteGroupIds(
					user.getCompanyId()));

		if (activeTransitiveGroupIdsMap == null) {
			return ArrayUtil.toLongArray(
				TransformUtil.transform(
					groupLocalService.getUserGroups(user, true),
					group -> {
						if (group.isActive() && group.isSite()) {
							return group.getGroupId();
						}

						return null;
					}));
		}

		long[] groupIds = activeTransitiveGroupIdsMap.get(user.getUserId());

		if (groupIds == null) {
			return new long[0];
		}

		return groupIds;
	}

	private AddressData _getAddressData(User user) {
		Map<Long, AddressData> addressDataMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				UserModelDocumentContributor.class.getName() +
					"#_getAddressData",
				count -> {
					Map<Long, AddressDataBuilder> addressDataBuilders =
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
										user.getCompanyId()
									).and(
										AddressTable.INSTANCE.classNameId.eq(
											_classNameLocalService.
												getClassNameId(
													Contact.class.getName()))
									)
								),
								false)) {

						AddressDataBuilder addressDataBuilder =
							addressDataBuilders.computeIfAbsent(
								(Long)values[0],
								key -> new AddressDataBuilder());

						addressDataBuilder.addCity(
							StringUtil.toLowerCase((String)values[1]));
						addressDataBuilder.addCountries(
							_getLocalizedCountryNames((String)values[2]));
						addressDataBuilder.addRegion(
							StringUtil.toLowerCase((String)values[3]));
						addressDataBuilder.addStreet(
							StringUtil.toLowerCase((String)values[4]));
						addressDataBuilder.addStreet(
							StringUtil.toLowerCase((String)values[5]));
						addressDataBuilder.addStreet(
							StringUtil.toLowerCase((String)values[6]));
						addressDataBuilder.addZip(
							StringUtil.toLowerCase((String)values[7]));
					}

					Map<Long, AddressData> localAddressDataMap =
						new HashMap<>();

					for (Map.Entry<Long, AddressDataBuilder> entry :
							addressDataBuilders.entrySet()) {

						AddressDataBuilder addressDataBuilder =
							entry.getValue();

						localAddressDataMap.put(
							entry.getKey(), addressDataBuilder.build());
					}

					return localAddressDataMap;
				});

		if (addressDataMap == null) {
			return new AddressData(user);
		}

		return addressDataMap.get(user.getContactId());
	}

	private long[] _getAncestorOrganizationIds(long[] organizationIds)
		throws Exception {

		if (organizationIds == null) {
			return null;
		}

		Set<Long> ancestorOrganizationIds = new HashSet<>();

		for (long organizationId : organizationIds) {
			Organization organization =
				organizationLocalService.getOrganization(organizationId);

			for (long ancestorOrganizationId :
					organization.getAncestorOrganizationIds()) {

				ancestorOrganizationIds.add(ancestorOrganizationId);
			}
		}

		return ArrayUtil.toLongArray(ancestorOrganizationIds);
	}

	private Date _getBirthday(User user) throws PortalException {
		Map<Long, Date> birthdayMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> _contactLocalService.dslQueryCount(
					DSLQueryFactoryUtil.count(
					).from(
						ContactTable.INSTANCE
					),
					false),
				UserModelDocumentContributor.class.getName() + "#_getBirthday",
				count -> {
					Map<Long, Date> localBirthdayMap = new HashMap<>();

					for (Object[] values :
							_contactLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									ContactTable.INSTANCE.contactId,
									ContactTable.INSTANCE.birthday
								).from(
									ContactTable.INSTANCE
								).where(
									ContactTable.INSTANCE.classNameId.eq(
										_classNameLocalService.getClassNameId(
											User.class.getName()))
								),
								false)) {

						localBirthdayMap.put((Long)values[0], (Date)values[1]);
					}

					return localBirthdayMap;
				});

		if (birthdayMap == null) {
			return user.getBirthday();
		}

		return birthdayMap.get(user.getContactId());
	}

	private String _getEmailAddressDomain(String emailAddress) {
		return emailAddress.substring(emailAddress.indexOf(StringPool.AT) + 1);
	}

	private long[] _getGroupIds(User user) {
		Map<Long, long[]> groupIdsMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				UserModelDocumentContributor.class.getName() + "#_getGroupIds",
				count -> {
					Map<Long, List<Long>> groupIdListMap = new HashMap<>();

					for (Object[] values :
							groupLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									Users_GroupsTable.INSTANCE.userId,
									Users_GroupsTable.INSTANCE.groupId
								).from(
									Users_GroupsTable.INSTANCE
								),
								false)) {

						List<Long> groupIds = groupIdListMap.computeIfAbsent(
							(Long)values[0], key -> new ArrayList<>());

						groupIds.add((Long)values[1]);
					}

					Map<Long, long[]> localGroupIdsMap = new HashMap<>();

					for (Map.Entry<Long, List<Long>> entry :
							groupIdListMap.entrySet()) {

						localGroupIdsMap.put(
							entry.getKey(),
							ArrayUtil.toLongArray(entry.getValue()));
					}

					return localGroupIdsMap;
				});

		if (groupIdsMap == null) {
			return user.getGroupIds();
		}

		return groupIdsMap.get(user.getUserId());
	}

	private Set<String> _getLocalizedCountryNames(String name) {
		Set<String> countryNames = new HashSet<>();

		for (Locale locale : _language.getAvailableLocales()) {
			countryNames.add(StringUtil.toLowerCase(_getName(locale, name)));
		}

		return countryNames;
	}

	private String _getName(Locale locale, String name) {
		String localizedName = LanguageUtil.get(
			locale, CountryConstants.NAME_PREFIX + name);

		if (!localizedName.startsWith(CountryConstants.NAME_PREFIX)) {
			return localizedName;
		}

		return name;
	}

	private OrganizationData _getOrganizationData(User user)
		throws Exception, PortalException {

		Map<Long, OrganizationData> organizationDataMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> organizationLocalService.dslQueryCount(
					DSLQueryFactoryUtil.count(
					).from(
						Users_OrgsTable.INSTANCE
					)),
				UserModelDocumentContributor.class.getName() +
					"#_getOrganizationData",
				count -> {
					Map<Long, OrganizationData> localOrganizationDataMap =
						new HashMap<>();

					if (count == 0) {
						return localOrganizationDataMap;
					}

					Map<Long, OrganizationDataBuilder>
						organizationDataBuilders = new HashMap<>();

					for (Object[] values :
							organizationLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									Users_OrgsTable.INSTANCE.userId,
									Users_OrgsTable.INSTANCE.organizationId,
									OrganizationTable.INSTANCE.treePath
								).from(
									Users_OrgsTable.INSTANCE
								).innerJoinON(
									OrganizationTable.INSTANCE,
									Users_OrgsTable.INSTANCE.organizationId.eq(
										OrganizationTable.INSTANCE.
											organizationId)
								),
								false)) {

						OrganizationDataBuilder organizationDataBuilder =
							organizationDataBuilders.computeIfAbsent(
								(Long)values[0],
								key -> new OrganizationDataBuilder());

						Long organizationId = (Long)values[1];

						organizationDataBuilder.addOrganizationId(
							organizationId);
						organizationDataBuilder.addTreePath(
							organizationId, (String)values[2]);
					}

					for (Map.Entry<Long, OrganizationDataBuilder> entry :
							organizationDataBuilders.entrySet()) {

						OrganizationDataBuilder organizationDataBuilder =
							entry.getValue();

						localOrganizationDataMap.put(
							entry.getKey(), organizationDataBuilder.build());
					}

					return localOrganizationDataMap;
				});

		if (organizationDataMap == null) {
			return new OrganizationData(user);
		}

		return organizationDataMap.get(user.getUserId());
	}

	private RoleData _getRoleData(User user) throws PortalException {
		Map<Long, RoleData> roleDataMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				UserModelDocumentContributor.class.getName() + "#_getRoleData",
				count -> {
					Map<Long, RoleDataBuilder> roleDataBuilders =
						new HashMap<>();

					for (Object[] values :
							_roleLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									Users_RolesTable.INSTANCE.userId,
									Users_RolesTable.INSTANCE.roleId,
									RoleTable.INSTANCE.name
								).from(
									Users_RolesTable.INSTANCE
								).innerJoinON(
									RoleTable.INSTANCE,
									Users_RolesTable.INSTANCE.roleId.eq(
										RoleTable.INSTANCE.roleId)
								),
								false)) {

						RoleDataBuilder roleDataBuilder =
							roleDataBuilders.computeIfAbsent(
								(Long)values[0], key -> new RoleDataBuilder());

						roleDataBuilder.addName((String)values[2]);
						roleDataBuilder.addRoleId((Long)values[1]);
					}

					Map<Long, RoleData> localRoleDataMap = new HashMap<>();

					for (Map.Entry<Long, RoleDataBuilder> entry :
							roleDataBuilders.entrySet()) {

						RoleDataBuilder roleDataBuilder = entry.getValue();

						localRoleDataMap.put(
							entry.getKey(), roleDataBuilder.build());
					}

					return localRoleDataMap;
				});

		if (roleDataMap == null) {
			return new RoleData(user);
		}

		return roleDataMap.get(user.getUserId());
	}

	private long[] _getTeamIds(User user) {
		Map<Long, long[]> teamIdsMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				UserModelDocumentContributor.class.getName() + "#_getTeamIds",
				count -> {
					Map<Long, List<Long>> teamIdListMap = new HashMap<>();

					for (Object[] values :
							_userGroupLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									Users_TeamsTable.INSTANCE.userId,
									Users_TeamsTable.INSTANCE.teamId
								).from(
									Users_TeamsTable.INSTANCE
								),
								false)) {

						List<Long> teamIds = teamIdListMap.computeIfAbsent(
							(Long)values[0], key -> new ArrayList<>());

						teamIds.add((Long)values[1]);
					}

					for (Object[] values :
							_userGroupLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									Users_UserGroupsTable.INSTANCE.userId,
									UserGroups_TeamsTable.INSTANCE.teamId
								).from(
									Users_UserGroupsTable.INSTANCE
								).innerJoinON(
									UserGroups_TeamsTable.INSTANCE,
									Users_UserGroupsTable.INSTANCE.userGroupId.
										eq(
											UserGroups_TeamsTable.INSTANCE.
												userGroupId)
								),
								false)) {

						List<Long> teamIds = teamIdListMap.computeIfAbsent(
							(Long)values[0], key -> new ArrayList<>());

						teamIds.add((Long)values[1]);
					}

					Map<Long, long[]> localTeamIdsMap = new HashMap<>();

					for (Map.Entry<Long, List<Long>> entry :
							teamIdListMap.entrySet()) {

						localTeamIdsMap.put(
							entry.getKey(),
							ArrayUtil.toLongArray(entry.getValue()));
					}

					return localTeamIdsMap;
				});

		if (teamIdsMap == null) {
			long[] teamIds = user.getTeamIds();

			long[] userGroupIds = user.getUserGroupIds();

			if (userGroupIds.length == 0) {
				return teamIds;
			}

			for (long userGroupId : userGroupIds) {
				teamIds = ArrayUtil.append(
					teamIds,
					_userGroupLocalService.getTeamPrimaryKeys(userGroupId));
			}

			return teamIds;
		}

		return teamIdsMap.get(user.getUserId());
	}

	private long[] _getUserGroupIds(User user) {
		Map<Long, long[]> userGroupIdsMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				UserModelDocumentContributor.class.getName() +
					"#_getUserGroupIds",
				count -> {
					Map<Long, List<Long>> userGroupIdListMap = new HashMap<>();

					for (Object[] values :
							_userGroupLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									Users_UserGroupsTable.INSTANCE.userId,
									Users_UserGroupsTable.INSTANCE.userGroupId
								).from(
									Users_UserGroupsTable.INSTANCE
								),
								false)) {

						List<Long> userGroupIds =
							userGroupIdListMap.computeIfAbsent(
								(Long)values[0], key -> new ArrayList<>());

						userGroupIds.add((Long)values[1]);
					}

					Map<Long, long[]> localUserGroupIdsMap = new HashMap<>();

					for (Map.Entry<Long, List<Long>> entry :
							userGroupIdListMap.entrySet()) {

						localUserGroupIdsMap.put(
							entry.getKey(),
							ArrayUtil.toLongArray(entry.getValue()));
					}

					return localUserGroupIdsMap;
				});

		if (userGroupIdsMap == null) {
			return user.getUserGroupIds();
		}

		return userGroupIdsMap.get(user.getUserId());
	}

	private UserGroupRoleData _getUserGroupRoleData(User user)
		throws PortalException {

		Map<Long, UserGroupRoleData> userGroupRoleDataMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				UserModelDocumentContributor.class.getName() +
					"#_getUserGroupRoleData",
				count -> {
					Map<Long, UserGroupRoleDataBuilder>
						userGroupRoleDataBuilders = new HashMap<>();

					for (Object[] values :
							userGroupRoleLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									UserGroupRoleTable.INSTANCE.userId,
									UserGroupRoleTable.INSTANCE.roleId,
									RoleTable.INSTANCE.name
								).from(
									UserGroupRoleTable.INSTANCE
								).innerJoinON(
									RoleTable.INSTANCE,
									UserGroupRoleTable.INSTANCE.roleId.eq(
										RoleTable.INSTANCE.roleId)
								),
								false)) {

						UserGroupRoleDataBuilder userGroupRoleDataBuilder =
							userGroupRoleDataBuilders.computeIfAbsent(
								(Long)values[0],
								key -> new UserGroupRoleDataBuilder());

						userGroupRoleDataBuilder.addName((String)values[2]);
						userGroupRoleDataBuilder.addRoleId((Long)values[1]);
					}

					Map<Long, UserGroupRoleData> localUserGroupRoleDataMap =
						new HashMap<>();

					for (Map.Entry<Long, UserGroupRoleDataBuilder> entry :
							userGroupRoleDataBuilders.entrySet()) {

						UserGroupRoleDataBuilder userGroupRoleDataBuilder =
							entry.getValue();

						localUserGroupRoleDataMap.put(
							entry.getKey(), userGroupRoleDataBuilder.build());
					}

					return localUserGroupRoleDataMap;
				});

		if (userGroupRoleDataMap == null) {
			return new UserGroupRoleData(user);
		}

		return userGroupRoleDataMap.get(user.getUserId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserModelDocumentContributor.class);

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ContactLocalService _contactLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	private class AddressData {

		public void contribute(Document document) {
			document.addText("city", _cities);
			document.addText("country", _countries);
			document.addText("region", _regions);
			document.addText("street", _streets);
			document.addText("zip", _zips);
		}

		private AddressData(
			String[] cities, String[] countries, String[] regions,
			String[] streets, String[] zips) {

			_cities = cities;
			_countries = countries;
			_regions = regions;
			_streets = streets;
			_zips = zips;
		}

		private AddressData(User user) {
			List<String> cities = new ArrayList<>();
			List<String> countries = new ArrayList<>();
			List<String> regions = new ArrayList<>();
			List<String> streets = new ArrayList<>();
			List<String> zips = new ArrayList<>();

			for (Address address : user.getAddresses()) {
				cities.add(StringUtil.toLowerCase(address.getCity()));

				Country country = address.getCountry();

				countries.addAll(_getLocalizedCountryNames(country.getName()));

				Region region = address.getRegion();

				regions.add(StringUtil.toLowerCase(region.getName()));

				streets.add(StringUtil.toLowerCase(address.getStreet1()));
				streets.add(StringUtil.toLowerCase(address.getStreet2()));
				streets.add(StringUtil.toLowerCase(address.getStreet3()));

				zips.add(StringUtil.toLowerCase(address.getZip()));
			}

			_cities = cities.toArray(new String[0]);
			_countries = countries.toArray(new String[0]);
			_regions = regions.toArray(new String[0]);
			_streets = streets.toArray(new String[0]);
			_zips = zips.toArray(new String[0]);
		}

		private final String[] _cities;
		private final String[] _countries;
		private final String[] _regions;
		private final String[] _streets;
		private final String[] _zips;

	}

	private class AddressDataBuilder {

		public void addCity(String city) {
			_cities.add(city);
		}

		public void addCountries(Collection<String> countries) {
			_countries.addAll(countries);
		}

		public void addRegion(String region) {
			_regions.add(region);
		}

		public void addStreet(String street) {
			_streets.add(street);
		}

		public void addZip(String zip) {
			_zips.add(zip);
		}

		public AddressData build() {
			return new AddressData(
				_cities.toArray(new String[0]),
				_countries.toArray(new String[0]),
				_regions.toArray(new String[0]),
				_streets.toArray(new String[0]), _zips.toArray(new String[0]));
		}

		private final List<String> _cities = new ArrayList<>();
		private final List<String> _countries = new ArrayList<>();
		private final List<String> _regions = new ArrayList<>();
		private final List<String> _streets = new ArrayList<>();
		private final List<String> _zips = new ArrayList<>();

	}

	private class OrganizationData {

		public void contribute(Document document) {
			document.addKeyword(
				"ancestorOrganizationIds", _ancestorOrganizationIds);

			document.addKeyword("organizationCount", _organizationIds.length);
			document.addKeyword("organizationIds", _organizationIds);
		}

		private OrganizationData(
			long[] ancestorOrganizationIds, long[] organizationIds) {

			_ancestorOrganizationIds = ancestorOrganizationIds;
			_organizationIds = organizationIds;
		}

		private OrganizationData(User user) throws Exception {
			_organizationIds = user.getOrganizationIds();

			_ancestorOrganizationIds = _getAncestorOrganizationIds(
				_organizationIds);
		}

		private final long[] _ancestorOrganizationIds;
		private final long[] _organizationIds;

	}

	private class OrganizationDataBuilder {

		public void addOrganizationId(Long organizationId) {
			_organizationIds.add(organizationId);
		}

		public void addTreePath(Long organizationId, String treePath) {
			try {
				if (Validator.isNull(treePath)) {
					Organization organization =
						organizationLocalService.getOrganization(
							organizationId);

					treePath = organization.buildTreePath();
				}

				long[] primaryKeys = StringUtil.split(
					treePath, StringPool.SLASH, 0L);

				if (primaryKeys.length <= 2) {
					return;
				}

				for (int i = 1; i < (primaryKeys.length - 1); i++) {
					_ancestorOrganizationIds.add(primaryKeys[i]);
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		public OrganizationData build() {
			return new OrganizationData(
				ArrayUtil.toLongArray(_ancestorOrganizationIds),
				ArrayUtil.toLongArray(_organizationIds));
		}

		private final Set<Long> _ancestorOrganizationIds = new HashSet<>();
		private final List<Long> _organizationIds = new ArrayList<>();

	}

	private class RoleData {

		public void contribute(Document document) {
			document.addKeyword("roleIds", _roleIds);
			document.addKeyword("roleNames", _names);
		}

		private RoleData(long[] roleIds, String[] names) {
			_roleIds = roleIds;
			_names = names;
		}

		private RoleData(User user) throws PortalException {
			_roleIds = user.getRoleIds();

			_names = ListUtil.toArray(
				_roleLocalService.getRoles(_roleIds), Role.NAME_ACCESSOR);
		}

		private final String[] _names;
		private final long[] _roleIds;

	}

	private class RoleDataBuilder {

		public void addName(String name) {
			_names.add(name);
		}

		public void addRoleId(Long roleId) {
			_roleIds.add(roleId);
		}

		public RoleData build() {
			return new RoleData(
				ArrayUtil.toLongArray(_roleIds), _names.toArray(new String[0]));
		}

		private final List<String> _names = new ArrayList<>();
		private final List<Long> _roleIds = new ArrayList<>();

	}

	private class UserGroupRoleData {

		public void contribute(Document document) {
			document.addKeyword("userGroupRoleIds", _roleIds);
			document.addKeyword("userGroupRoleNames", _names);
		}

		private UserGroupRoleData(long[] roleIds, String[] names) {
			_roleIds = roleIds;
			_names = names;
		}

		private UserGroupRoleData(User user) throws PortalException {
			_roleIds = ListUtil.toLongArray(
				user.getUserGroupRoles(), UserGroupRole::getRoleId);

			_names = ListUtil.toArray(
				_roleLocalService.getRoles(_roleIds), Role.NAME_ACCESSOR);
		}

		private final String[] _names;
		private final long[] _roleIds;

	}

	private class UserGroupRoleDataBuilder {

		public void addName(String name) {
			_names.add(name);
		}

		public void addRoleId(Long roleId) {
			_roleIds.add(roleId);
		}

		public UserGroupRoleData build() {
			return new UserGroupRoleData(
				ArrayUtil.toLongArray(_roleIds), _names.toArray(new String[0]));
		}

		private final List<String> _names = new ArrayList<>();
		private final List<Long> _roleIds = new ArrayList<>();

	}

}