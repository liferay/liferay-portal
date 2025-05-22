/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.manager;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.runtime.internal.BaseKaleoBean;
import com.liferay.portal.workflow.kaleo.runtime.manager.PortalKaleoManager;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = PortalKaleoManager.class)
public class DefaultPortalKaleoManager
	extends BaseKaleoBean implements PortalKaleoManager {

	@Override
	public void deleteKaleoData(long companyId) throws Exception {
		kaleoDefinitionLocalService.deleteCompanyKaleoDefinitions(companyId);

		kaleoLogLocalService.deleteCompanyKaleoLogs(companyId);
	}

	@Override
	public void deployDefaultDefinitionLink(String assetClassName)
		throws Exception {

		companyLocalService.forEachCompanyId(
			companyId -> {
				try {
					User guestUser = userLocalService.getGuestUser(companyId);

					Group companyGroup = groupLocalService.getCompanyGroup(
						companyId);

					String definitionName =
						WorkflowDefinitionConstants.NAME_SINGLE_APPROVER;

					if (_definitionAssets.containsKey(assetClassName)) {
						definitionName = _definitionAssets.get(assetClassName);
					}

					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setCompanyId(companyId);

					deployDefaultDefinitionLink(
						guestUser, companyId, companyGroup, assetClassName,
						definitionName);
				}
				catch (PortalException portalException) {
					throw new SystemException(portalException);
				}
			});
	}

	@Override
	public void deployDefaultDefinitionLinks() throws Exception {
		companyLocalService.forEachCompanyId(
			companyId -> {
				try {
					deployDefaultDefinitionLinks(companyId);
				}
				catch (Exception exception) {
					throw new SystemException(exception);
				}
			});
	}

	@Override
	public void deployDefaultDefinitionLinks(long companyId) throws Exception {
		User guestUser = userLocalService.getGuestUser(companyId);

		Group companyGroup = groupLocalService.getCompanyGroup(companyId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		for (Map.Entry<String, String> entry : _definitionAssets.entrySet()) {
			String assetClassName = entry.getKey();
			String definitionName = entry.getValue();

			deployDefaultDefinitionLink(
				guestUser, companyId, companyGroup, assetClassName,
				definitionName);
		}
	}

	@Override
	public void deployDefaultDefinitions() throws Exception {
		companyLocalService.forEachCompanyId(
			companyId -> {
				try {
					deployDefaultDefinitions(companyId);
				}
				catch (Exception exception) {
					throw new SystemException(exception);
				}
			});
	}

	@Override
	public void deployDefaultDefinitions(long companyId) throws Exception {
		for (Map.Entry<String, String> entry : _definitionFiles.entrySet()) {
			String definitionName = entry.getKey();

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(companyId);

			int kaleoDefinitionsCount =
				kaleoDefinitionLocalService.getKaleoDefinitionsCount(
					definitionName, serviceContext);

			if (kaleoDefinitionsCount > 0) {
				return;
			}

			Class<?> clazz = getClass();

			ClassLoader classLoader = clazz.getClassLoader();

			String fileName = entry.getValue();

			InputStream inputStream = classLoader.getResourceAsStream(fileName);

			if (inputStream == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to find definition file for ",
							definitionName, " with file name ", fileName));
				}

				return;
			}

			User guestUser = userLocalService.getGuestUser(companyId);

			_workflowDefinitionManager.deployWorkflowDefinition(
				WorkflowDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_SINGLE_APPROVER,
				serviceContext.getCompanyId(), guestUser.getUserId(),
				_getLocalizedTitle(companyId, definitionName), definitionName,
				FileUtil.getBytes(inputStream));
		}
	}

	@Override
	public void deployDefaultRoles() throws Exception {
		companyLocalService.forEachCompanyId(
			companyId -> {
				try {
					deployDefaultRoles(companyId);
				}
				catch (Exception exception) {
					throw new SystemException(exception);
				}
			});
	}

	@Override
	public void deployDefaultRoles(long companyId) throws Exception {
		User guestUser = userLocalService.getGuestUser(companyId);

		for (Map.Entry<String, String> entry : _defaultRoles.entrySet()) {
			String name = entry.getKey();

			Role role = roleLocalService.fetchRole(companyId, name);

			if (role != null) {
				continue;
			}

			roleLocalService.addRole(
				null, guestUser.getUserId(), null, 0, name, null,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), entry.getValue()
				).build(),
				RoleConstants.TYPE_REGULAR, null, null);
		}
	}

	@Override
	public void deployKaleoDefaults() throws Exception {
		deployDefaultRoles();
		deployDefaultDefinitions();
		deployDefaultDefinitionLinks();
	}

	@Override
	public void deployKaleoDefaults(long companyId) throws Exception {
		deployDefaultRoles(companyId);
		deployDefaultDefinitions(companyId);
		deployDefaultDefinitionLinks(companyId);
	}

	public void setDefaultRoles(Map<String, String> defaultRoles) {
		_defaultRoles.putAll(defaultRoles);
	}

	public void setDefinitionAssets(Map<String, String> definitionAssets) {
		_definitionAssets.putAll(definitionAssets);
	}

	public void setDefinitionFiles(Map<String, String> definitionFiles) {
		_definitionFiles.putAll(definitionFiles);
	}

	protected void deployDefaultDefinitionLink(
			User guestUser, long companyId, Group companyGroup,
			String assetClassName, String workflowDefinitionName)
		throws PortalException {

		WorkflowDefinitionLink workflowDefinitionLink =
			workflowDefinitionLinkLocalService.
				fetchDefaultWorkflowDefinitionLink(companyId, assetClassName);

		if (workflowDefinitionLink != null) {
			return;
		}

		List<WorkflowDefinition> workflowDefinitions =
			_workflowDefinitionManager.getActiveWorkflowDefinitions(
				companyId, workflowDefinitionName, 0, 20,
				workflowComparatorFactory.getDefinitionNameComparator(false));

		if (workflowDefinitions.isEmpty()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No workflow definitions found for " +
						workflowDefinitionName);
			}

			return;
		}

		WorkflowDefinition workflowDefinition = workflowDefinitions.get(0);

		workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, guestUser.getUserId(), companyId, companyGroup.getGroupId(),
			assetClassName, 0, 0, workflowDefinition.getName(),
			workflowDefinition.getVersion());
	}

	@Reference
	protected CompanyLocalService companyLocalService;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected RoleLocalService roleLocalService;

	@Reference
	protected UserLocalService userLocalService;

	@Reference
	protected WorkflowComparatorFactory workflowComparatorFactory;

	private String _getLocalizedTitle(long companyId, String definitionName)
		throws Exception {

		if (!Objects.equals(
				WorkflowDefinitionConstants.NAME_SINGLE_APPROVER,
				definitionName)) {

			return _localization.updateLocalization(
				StringPool.BLANK, "title", definitionName,
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()));
		}

		LocalizedValuesMap localizedValuesMap = new LocalizedValuesMap();

		Group companyGroup = groupLocalService.getCompanyGroup(companyId);

		for (Locale availableLocale :
				_language.getAvailableLocales(companyGroup.getGroupId())) {

			localizedValuesMap.put(
				availableLocale,
				_language.get(
					ResourceBundleUtil.getModuleAndPortalResourceBundle(
						availableLocale, DefaultPortalKaleoManager.class),
					"single-approver"));
		}

		return _localization.getXml(localizedValuesMap, "title");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultPortalKaleoManager.class);

	private final Map<String, String> _defaultRoles = new HashMap<>();
	private final Map<String, String> _definitionAssets = new HashMap<>();
	private final Map<String, String> _definitionFiles = HashMapBuilder.put(
		WorkflowDefinitionConstants.NAME_SINGLE_APPROVER,
		"META-INF/definitions/single-approver-workflow-definition.xml"
	).build();

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}