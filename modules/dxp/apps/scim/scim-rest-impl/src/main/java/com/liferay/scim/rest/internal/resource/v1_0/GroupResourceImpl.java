/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.internal.resource.v1_0;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.scim.rest.dto.v1_0.Group;
import com.liferay.scim.rest.dto.v1_0.PatchOp;
import com.liferay.scim.rest.internal.manager.GroupResourceManagerImpl;
import com.liferay.scim.rest.internal.manager.UserManagerImpl;
import com.liferay.scim.rest.internal.util.ScimUtil;
import com.liferay.scim.rest.resource.v1_0.GroupResource;

import jakarta.ws.rs.core.Response;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.endpoints.GroupResourceManager;

/**
 * @author Olivér Kecskeméty
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/group.properties",
	scope = ServiceScope.PROTOTYPE, service = GroupResource.class
)
public class GroupResourceImpl extends BaseGroupResourceImpl {

	@Override
	public Response deleteV2Group(String id) throws Exception {
		return ScimUtil.buildResponse(
			_groupResourceManager.delete(id, _userManager));
	}

	@Override
	public Object getV2GroupById(String id, String excludedAttributes)
		throws Exception {

		return ScimUtil.buildResponse(
			_groupResourceManager.get(
				id, _userManager, null, excludedAttributes));
	}

	@Override
	public Object getV2Groups(
			Integer count, String excludedAttributes, Integer startIndex,
			Filter filter)
		throws Exception {

		return ScimUtil.buildResponse(
			_groupResourceManager.listWithGET(
				_userManager,
				ParamUtil.getString(contextHttpServletRequest, "filter", null),
				startIndex, count, null, null, null, null, excludedAttributes));
	}

	@Override
	public Response patchV2Group(String id, PatchOp patchOp) throws Exception {
		return ScimUtil.buildResponse(
			_groupResourceManager.updateWithPATCH(
				id, ScimUtil.transformGroupPatchOp(patchOp), _userManager));
	}

	@Override
	public Response postV2Group(Group group) throws Exception {
		return ScimUtil.buildResponse(
			_groupResourceManager.create(
				group.toString(), _userManager, null, null));
	}

	@Override
	public Response putV2Group(String id, Group group) throws Exception {
		return ScimUtil.buildResponse(
			_groupResourceManager.updateWithPUT(
				id, group.toString(), _userManager, null, null));
	}

	@Activate
	protected void activate() {
		_userManager = new UserManagerImpl(
			_addressLocalService, _classNameLocalService, _companyLocalService,
			_configurationAdmin, _contactLocalService, _counterLocalService,
			_countryLocalService, _emailAddressLocalService,
			_expandoColumnLocalService, _expandoTableLocalService,
			_expandoValueLocalService, _listTypeLocalService,
			_phoneLocalService, _regionLocalService, _userGroupLocalService,
			_userGroupService, _userLocalService, _userService,
			_websiteLocalService);
	}

	private static final GroupResourceManager _groupResourceManager =
		new GroupResourceManagerImpl();

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ContactLocalService _contactLocalService;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference
	private EmailAddressLocalService _emailAddressLocalService;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference
	private PhoneLocalService _phoneLocalService;

	@Reference
	private RegionLocalService _regionLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserGroupService _userGroupService;

	@Reference
	private UserLocalService _userLocalService;

	private UserManager _userManager;

	@Reference
	private UserService _userService;

	@Reference
	private WebsiteLocalService _websiteLocalService;

}