/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class OrganizationDisplayTerms extends DisplayTerms {

	public static final String CITY = "city";

	public static final String COUNTRY_ID = "countryId";

	public static final String NAME = "name";

	public static final String PARENT_ORGANIZATION_ID = "parentOrganizationId";

	public static final String REGION_ID = "regionId";

	public static final String STREET = "street";

	public static final String TYPE = "type";

	public static final String ZIP = "zip";

	public OrganizationDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		city = ParamUtil.getString(portletRequest, CITY);
		countryId = ParamUtil.getLong(portletRequest, COUNTRY_ID);
		name = ParamUtil.getString(portletRequest, NAME);
		parentOrganizationId = ParamUtil.getLong(
			portletRequest, PARENT_ORGANIZATION_ID);
		regionId = ParamUtil.getLong(portletRequest, REGION_ID);
		street = ParamUtil.getString(portletRequest, STREET);
		type = ParamUtil.getString(portletRequest, TYPE);
		zip = ParamUtil.getString(portletRequest, ZIP);
	}

	public String getCity() {
		return city;
	}

	public long getCountryId() {
		return countryId;
	}

	public String getName() {
		return name;
	}

	public long getParentOrganizationId() {
		return parentOrganizationId;
	}

	public long getRegionId() {
		return regionId;
	}

	public String getStreet() {
		return street;
	}

	public String getType() {
		return type;
	}

	public String getZip() {
		return zip;
	}

	public void setType(String type) {
		this.type = type;
	}

	protected String city;
	protected long countryId;
	protected String name;
	protected long parentOrganizationId;
	protected long regionId;
	protected String street;
	protected String type;
	protected String zip;

}