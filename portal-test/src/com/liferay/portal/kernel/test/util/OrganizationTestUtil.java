/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.CountryLocalServiceUtil;
import com.liferay.portal.kernel.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.kernel.service.ListTypeServiceUtil;
import com.liferay.portal.kernel.service.OrgLaborLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.PasswordPolicyRelLocalServiceUtil;
import com.liferay.portal.kernel.service.PhoneLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WebsiteLocalServiceUtil;
import com.liferay.portlet.passwordpoliciesadmin.util.test.PasswordPolicyTestUtil;

import java.util.List;

/**
 * @author Alberto Chaparro
 */
public class OrganizationTestUtil {

	public static Address addAddress(Organization organization)
		throws Exception {

		return AddressLocalServiceUtil.addAddress(
			null, organization.getUserId(), organization.getModelClassName(),
			organization.getOrganizationId(), RandomTestUtil.nextLong(),
			_getListTypeId(
				organization.getCompanyId(),
				ListTypeConstants.ORGANIZATION_ADDRESS),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, RandomTestUtil.randomString(),
			false, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			null, new ServiceContext());
	}

	public static Country addCountry(
			Organization organization, ServiceContext serviceContext)
		throws Exception {

		Country country = CountryLocalServiceUtil.fetchCountryByA2(
			organization.getCompanyId(), "ZZ");

		if (country == null) {
			country = CountryLocalServiceUtil.addCountry(
				null, "ZZ", "ZZZ", true, true, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomDouble(), true, false, false,
				serviceContext);
		}

		OrganizationLocalServiceUtil.updateOrganization(
			organization.getExternalReferenceCode(),
			organization.getCompanyId(), organization.getOrganizationId(),
			organization.getParentOrganizationId(), organization.getName(),
			organization.getType(), organization.getRegionId(),
			country.getCountryId(), organization.getStatusListTypeId(),
			organization.getComments(), false, null, false, null);

		return country;
	}

	public static EmailAddress addEmailAddress(Organization organization)
		throws Exception {

		return EmailAddressLocalServiceUtil.addEmailAddress(
			null, organization.getUserId(), organization.getModelClassName(),
			organization.getOrganizationId(), "test@liferay.com",
			_getListTypeId(
				organization.getCompanyId(),
				ListTypeConstants.ORGANIZATION_EMAIL_ADDRESS),
			false, new ServiceContext());
	}

	public static OrgLabor addOrgLabor(Organization organization)
		throws Exception {

		return OrgLaborLocalServiceUtil.addOrgLabor(
			organization.getOrganizationId(),
			_getListTypeId(
				organization.getCompanyId(),
				ListTypeConstants.ORGANIZATION_SERVICE),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());
	}

	public static Organization addOrganization() throws Exception {
		return addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);
	}

	public static Organization addOrganization(boolean site) throws Exception {
		return addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), site);
	}

	public static Organization addOrganization(
			long parentOrganizationId, String name, boolean site)
		throws Exception {

		return OrganizationLocalServiceUtil.addOrganization(
			TestPropsValues.getUserId(), parentOrganizationId, name, site);
	}

	public static Organization addOrganization(String type) throws Exception {
		ListType listType = ListTypeServiceUtil.getListType(
			TestPropsValues.getCompanyId(),
			ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
			ListTypeConstants.ORGANIZATION_STATUS);

		return OrganizationLocalServiceUtil.addOrganization(
			null, TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), type, 0, 0, listType.getListTypeId(),
			StringPool.BLANK, false, null);
	}

	public static PasswordPolicy addPasswordPolicyRel(
			Organization organization, ServiceContext serviceContext)
		throws Exception {

		PasswordPolicy passwordPolicy =
			PasswordPolicyTestUtil.addPasswordPolicy(serviceContext);

		PasswordPolicyRelLocalServiceUtil.addPasswordPolicyRel(
			passwordPolicy.getPasswordPolicyId(),
			organization.getModelClassName(), organization.getOrganizationId());

		return passwordPolicy;
	}

	public static Phone addPhone(Organization organization) throws Exception {
		return PhoneLocalServiceUtil.addPhone(
			null, organization.getUserId(), organization.getModelClassName(),
			organization.getOrganizationId(), "0000000000", "000",
			_getListTypeId(
				organization.getCompanyId(),
				ListTypeConstants.ORGANIZATION_PHONE),
			false, new ServiceContext());
	}

	public static Organization addSite(Organization organization)
		throws Exception {

		return OrganizationLocalServiceUtil.updateOrganization(
			organization.getExternalReferenceCode(),
			organization.getCompanyId(), organization.getOrganizationId(),
			organization.getParentOrganizationId(), organization.getName(),
			organization.getType(), organization.getRegionId(),
			organization.getCountryId(), organization.getStatusListTypeId(),
			organization.getComments(), false, null, true, null);
	}

	public static Website addWebsite(Organization organization)
		throws Exception {

		return WebsiteLocalServiceUtil.addWebsite(
			null, organization.getUserId(), organization.getModelClassName(),
			organization.getOrganizationId(), "http://www.test.com",
			_getListTypeId(
				organization.getCompanyId(),
				ListTypeConstants.ORGANIZATION_WEBSITE),
			false, new ServiceContext());
	}

	public static void updateAsset(
			Organization organization, long[] assetCategoryIds,
			String[] assetTagNames)
		throws Exception {

		OrganizationLocalServiceUtil.updateAsset(
			organization.getUserId(), organization, assetCategoryIds,
			assetTagNames);
	}

	private static long _getListTypeId(long companyId, String type)
		throws Exception {

		List<ListType> listTypes = ListTypeServiceUtil.getListTypes(
			companyId, type);

		ListType listType = listTypes.get(0);

		return listType.getListTypeId();
	}

}