/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.internal.resource.v1_0;

import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.scim.rest.dto.v1_0.PatchOp;
import com.liferay.scim.rest.dto.v1_0.User;
import com.liferay.scim.rest.internal.manager.UserManagerImpl;
import com.liferay.scim.rest.internal.manager.UserResourceManagerImpl;
import com.liferay.scim.rest.internal.util.ScimUtil;
import com.liferay.scim.rest.resource.v1_0.UserResource;

import jakarta.ws.rs.core.Response;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.endpoints.UserResourceManager;

/**
 * @author Olivér Kecskeméty
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user.properties",
	scope = ServiceScope.PROTOTYPE, service = UserResource.class
)
public class UserResourceImpl extends BaseUserResourceImpl {

	@Override
	public Response deleteV2User(String id) throws Exception {
		return ScimUtil.buildResponse(
			_userResourceManager.delete(id, _userManager));
	}

	@Override
	public Object getV2UserById(String id) throws Exception {
		return ScimUtil.buildResponse(
			_userResourceManager.get(id, _userManager, null, null));
	}

	@Override
	public Object getV2Users(Integer count, Integer startIndex, Filter filter)
		throws Exception {

		return ScimUtil.buildResponse(
			_userResourceManager.listWithGET(
				_userManager,
				ParamUtil.getString(contextHttpServletRequest, "filter", null),
				startIndex, count, null, null, null, null, null));
	}

	@Override
	public Response patchV2User(String id, PatchOp patchOp) throws Exception {
		return ScimUtil.buildResponse(
			_userResourceManager.updateWithPATCH(
				id, patchOp.toString(), _userManager, null, null));
	}

	@Override
	public Response postV2User(User user) throws Exception {
		return ScimUtil.buildResponse(
			_userResourceManager.create(
				user.toString(), _userManager, null, null));
	}

	@Override
	public Response putV2User(String id, User user) throws Exception {
		return ScimUtil.buildResponse(
			_userResourceManager.updateWithPUT(
				id, user.toString(), _userManager, null, null));
	}

	@Activate
	protected void activate() {
		_userManager = new UserManagerImpl(
			_classNameLocalService, _companyLocalService, _configurationAdmin,
			_expandoColumnLocalService, _expandoTableLocalService,
			_expandoValueLocalService, _searcher, _searchRequestBuilderFactory,
			_userGroupLocalService, _userGroupService, _userLocalService,
			_userService);
	}

	private static final UserResourceManager _userResourceManager =
		new UserResourceManagerImpl();

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserGroupService _userGroupService;

	@Reference
	private UserLocalService _userLocalService;

	private UserManager _userManager;

	@Reference
	private UserService _userService;

}