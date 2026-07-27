/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.internal.resource.v1_0;

import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.batch.planner.rest.dto.v1_0.SiteScope;
import com.liferay.batch.planner.rest.internal.vulcan.yaml.openapi.OpenAPIYAMLProvider;
import com.liferay.batch.planner.rest.resource.v1_0.SiteScopeResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.util.OpenAPIUtil;
import com.liferay.portal.vulcan.yaml.openapi.OpenAPIYAML;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Matija Petanjek
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/site-scope.properties",
	scope = ServiceScope.PROTOTYPE, service = SiteScopeResource.class
)
public class SiteScopeResourceImpl extends BaseSiteScopeResourceImpl {

	@Override
	public Page<SiteScope> getPlanInternalClassNameKeySiteScopesPage(
			String internalClassNameKey, Boolean export)
		throws Exception {

		return Page.of(
			_getSiteScopes(
				_getEntityScopes(
					GetterUtil.getBoolean(export), internalClassNameKey)));
	}

	private List<String> _getEntityScopes(
			boolean export, String internalClassNameKey)
		throws Exception {

		if (internalClassNameKey.contains(StringPool.POUND)) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					contextCompany.getCompanyId(),
					TaskItemUtil.getTaskItemDelegateName(internalClassNameKey));

			return Collections.singletonList(objectDefinition.getScope());
		}

		OpenAPIYAML openAPIYAML = _openAPIYAMLProvider.getOpenAPIYAML(
			contextCompany.getCompanyId(), internalClassNameKey);

		if (export) {
			return OpenAPIUtil.getReadEntityScopes(
				TaskItemUtil.getSimpleClassName(internalClassNameKey),
				openAPIYAML);
		}

		return OpenAPIUtil.getCreateEntityScopes(
			TaskItemUtil.getSimpleClassName(internalClassNameKey), openAPIYAML);
	}

	private List<SiteScope> _getSiteScopes(List<String> entityScopes)
		throws Exception {

		if (!entityScopes.contains("site")) {
			return Collections.emptyList();
		}

		if (_portal.isOmniadmin(contextUser.getUserId())) {
			return _toSiteScopes(
				_groupService.getGroups(
					contextCompany.getCompanyId(),
					GroupConstants.ANY_PARENT_GROUP_ID, true));
		}

		return _toSiteScopes(
			_groupService.getUserSitesGroups(
				contextUser.getUserId(), _CLASS_NAMES, QueryUtil.ALL_POS));
	}

	private List<SiteScope> _toSiteScopes(List<Group> groups) {
		return transform(
			groups,
			group -> new SiteScope() {
				{
					setLabel(group::getDescriptiveName);
					setValue(group::getGroupId);
				}
			});
	}

	private static final String[] _CLASS_NAMES = {
		Company.class.getName(), Group.class.getName(),
		Organization.class.getName()
	};

	@Reference
	private GroupService _groupService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private OpenAPIYAMLProvider _openAPIYAMLProvider;

	@Reference
	private Portal _portal;

}