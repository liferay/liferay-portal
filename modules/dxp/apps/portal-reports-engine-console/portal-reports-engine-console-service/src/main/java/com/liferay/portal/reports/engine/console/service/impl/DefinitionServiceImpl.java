/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsoleConstants;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.service.base.DefinitionServiceBaseImpl;

import java.io.InputStream;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gavin Wan
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=reports",
		"json.web.service.context.path=Definition"
	},
	service = AopService.class
)
public class DefinitionServiceImpl extends DefinitionServiceBaseImpl {

	@Override
	public Definition addDefinition(
			long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, long sourceId,
			String reportParameters, String fileName, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			ActionKeys.ADD_DEFINITION);

		return definitionLocalService.addDefinition(
			getUserId(), groupId, nameMap, descriptionMap, sourceId,
			reportParameters, fileName, inputStream, serviceContext);
	}

	@Override
	public Definition deleteDefinition(long definitionId)
		throws PortalException {

		_definitionModelResourcePermission.check(
			getPermissionChecker(), definitionId, ActionKeys.DELETE);

		return definitionLocalService.deleteDefinition(definitionId);
	}

	@Override
	public Definition getDefinition(long definitionId) throws PortalException {
		_definitionModelResourcePermission.check(
			getPermissionChecker(), definitionId, ActionKeys.VIEW);

		return definitionPersistence.findByPrimaryKey(definitionId);
	}

	@Override
	public List<Definition> getDefinitions(
			long groupId, String definitionName, String description,
			String sourceId, String reportName, boolean andSearch, int start,
			int end, OrderByComparator<Definition> orderByComparator)
		throws PortalException {

		return definitionFinder.filterFindByG_S_N_D_RN(
			groupId, definitionName, description, GetterUtil.getLong(sourceId),
			reportName, andSearch, start, end, orderByComparator);
	}

	@Override
	public int getDefinitionsCount(
		long groupId, String definitionName, String description,
		String sourceId, String reportName, boolean andSearch) {

		return definitionFinder.filterCountByG_S_N_D_RN(
			groupId, definitionName, description, GetterUtil.getLong(sourceId),
			reportName, andSearch);
	}

	@Override
	public Definition updateDefinition(
			long definitionId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, long sourceId,
			String reportParameters, String fileName, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException {

		_definitionModelResourcePermission.check(
			getPermissionChecker(), definitionId, ActionKeys.UPDATE);

		return definitionLocalService.updateDefinition(
			definitionId, nameMap, descriptionMap, sourceId, reportParameters,
			fileName, inputStream, serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.reports.engine.console.model.Definition)"
	)
	private ModelResourcePermission<Definition>
		_definitionModelResourcePermission;

	@Reference(
		target = "(resource.name=" + ReportsEngineConsoleConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}