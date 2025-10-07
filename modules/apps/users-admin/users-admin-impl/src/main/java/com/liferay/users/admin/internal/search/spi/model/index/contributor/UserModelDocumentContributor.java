/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search.spi.model.index.contributor;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
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
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
			long[] organizationIds = user.getOrganizationIds();

			long[] activeTransitiveGroupIds = _getActiveTransitiveGroupIds(
				user.getUserId());

			document.addKeyword(Field.COMPANY_ID, user.getCompanyId());
			document.addDate(Field.CREATE_DATE, user.getCreateDate());
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
			document.addKeyword(
				"ancestorOrganizationIds",
				_getAncestorOrganizationIds(user.getOrganizationIds()));
			document.addDate("birthDate", user.getBirthday());
			document.addKeyword("defaultUser", user.isDefaultUser());
			document.addKeyword("emailAddress", user.getEmailAddress());
			document.addKeyword(
				"emailAddressDomain",
				_getEmailAddressDomain(user.getEmailAddress()));
			document.addText("firstName", user.getFirstName());
			document.addText("fullName", user.getFullName());
			document.addKeyword("groupIds", user.getGroupIds());
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
			document.addKeyword("organizationIds", organizationIds);
			document.addKeyword(
				"organizationCount", String.valueOf(organizationIds.length));

			long[] roleIds = user.getRoleIds();

			document.addKeyword("roleIds", roleIds);
			document.addKeyword(
				"roleNames",
				ListUtil.toArray(
					_roleLocalService.getRoles(roleIds), Role.NAME_ACCESSOR));

			document.addText("screenName", user.getScreenName());
			document.addKeyword("teamIds", _getTeamIds(user));
			document.addKeyword("userGroupIds", user.getUserGroupIds());

			long[] userGroupRoleIds = _getUserGroupRoleIds(user.getUserId());

			document.addKeyword("userGroupRoleIds", userGroupRoleIds);
			document.addKeyword(
				"userGroupRoleNames",
				ListUtil.toArray(
					_roleLocalService.getRoles(userGroupRoleIds),
					Role.NAME_ACCESSOR));

			_populateAddresses(document, user.getAddresses(), 0, 0);

			for (Locale locale :
					_language.getAvailableLocales(user.getGroupId())) {

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

	private long[] _getActiveTransitiveGroupIds(long userId)
		throws PortalException {

		return ArrayUtil.toLongArray(
			TransformUtil.transform(
				groupLocalService.getUserGroups(userId, true),
				group -> {
					if (group.isActive() && group.isSite()) {
						return group.getGroupId();
					}

					return null;
				}));
	}

	private long[] _getAncestorOrganizationIds(long[] organizationIds)
		throws Exception {

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

	private String _getEmailAddressDomain(String emailAddress) {
		return emailAddress.substring(emailAddress.indexOf(StringPool.AT) + 1);
	}

	private Set<String> _getLocalizedCountryNames(Country country) {
		Set<String> countryNames = new HashSet<>();

		for (Locale locale : _language.getAvailableLocales()) {
			String countryName = country.getName(locale);

			countryName = StringUtil.toLowerCase(countryName);

			countryNames.add(countryName);
		}

		return countryNames;
	}

	private long[] _getTeamIds(User user) {
		long[] userGroupIds = user.getUserGroupIds();

		if (userGroupIds.length == 0) {
			return user.getTeamIds();
		}

		long[] teamIds = user.getTeamIds();

		for (long userGroupId : user.getUserGroupIds()) {
			teamIds = ArrayUtil.append(
				teamIds,
				_userGroupLocalService.getTeamPrimaryKeys(userGroupId));
		}

		return teamIds;
	}

	private long[] _getUserGroupRoleIds(long userId) {
		Set<Long> userGroupRoleIds = new HashSet<>();

		for (UserGroupRole userGroupRole :
				userGroupRoleLocalService.getUserGroupRoles(userId)) {

			userGroupRoleIds.add(userGroupRole.getRoleId());
		}

		return ArrayUtil.toLongArray(userGroupRoleIds);
	}

	private void _populateAddresses(
			Document document, List<Address> addresses, long regionId,
			long countryId)
		throws PortalException {

		List<String> cities = new ArrayList<>();

		List<String> countries = new ArrayList<>();

		if (countryId > 0) {
			try {
				countries.addAll(
					_getLocalizedCountryNames(
						countryService.getCountry(countryId)));
			}
			catch (NoSuchCountryException noSuchCountryException) {
				if (_log.isWarnEnabled()) {
					_log.warn(noSuchCountryException);
				}
			}
		}

		List<String> regions = new ArrayList<>();

		if (regionId > 0) {
			try {
				Region region = regionService.getRegion(regionId);

				regions.add(StringUtil.toLowerCase(region.getName()));
			}
			catch (NoSuchRegionException noSuchRegionException) {
				if (_log.isWarnEnabled()) {
					_log.warn(noSuchRegionException);
				}
			}
		}

		List<String> streets = new ArrayList<>();
		List<String> zips = new ArrayList<>();

		for (Address address : addresses) {
			cities.add(StringUtil.toLowerCase(address.getCity()));
			countries.addAll(_getLocalizedCountryNames(address.getCountry()));

			Region region = address.getRegion();

			regions.add(StringUtil.toLowerCase(region.getName()));

			streets.add(StringUtil.toLowerCase(address.getStreet1()));
			streets.add(StringUtil.toLowerCase(address.getStreet2()));
			streets.add(StringUtil.toLowerCase(address.getStreet3()));

			zips.add(StringUtil.toLowerCase(address.getZip()));
		}

		document.addText("city", cities.toArray(new String[0]));
		document.addText("country", countries.toArray(new String[0]));
		document.addText("region", regions.toArray(new String[0]));
		document.addText("street", streets.toArray(new String[0]));
		document.addText("zip", zips.toArray(new String[0]));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserModelDocumentContributor.class);

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}