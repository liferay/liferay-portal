/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.activator;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandler;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.oauth2.provider.scope.spi.scope.mapper.ScopeMapper;
import com.liferay.osb.faro.web.internal.application.ApiApplication;
import com.liferay.osb.faro.web.internal.controller.api.DemandbaseAccountFaroController;
import com.liferay.osb.faro.web.internal.controller.api.HubSpotWebhookFaroController;
import com.liferay.osb.faro.web.internal.controller.api.MarketoWebhookFaroController;
import com.liferay.osb.faro.web.internal.controller.api.RecommendationFaroController;
import com.liferay.osb.faro.web.internal.controller.api.ReportFaroController;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"osgi.jaxrs.name=Liferay.Analytics.Cloud.REST", "sap.scope.finder=true"
	},
	service = {
		PortalInstanceLifecycleListener.class, PrefixHandlerFactory.class,
		ScopeFinder.class, ScopeMapper.class
	}
)
public class OAuth2AuthorizationExpandoPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener
	implements PrefixHandlerFactory, ScopeFinder, ScopeMapper {

	@Override
	public PrefixHandler create(
		Function<String, Object> propertyAccessorFunction) {

		return PrefixHandler.PASS_THROUGH_PREFIX_HANDLER;
	}

	@Override
	public Collection<String> findScopes() {
		return _scopeAliasesList;
	}

	@Override
	public Set<String> map(String scope) {
		return Collections.singleton(scope);
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		_addSAPEntries(company.getCompanyId());

		long classNameId = _classNameLocalService.getClassNameId(
			OAuth2Authorization.class.getName());

		ExpandoTable expandoTable = _expandoTableLocalService.fetchTable(
			company.getCompanyId(), classNameId,
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable == null) {
			expandoTable = _expandoTableLocalService.addTable(
				company.getCompanyId(), classNameId,
				ExpandoTableConstants.DEFAULT_TABLE_NAME);
		}

		addExpandoColumn(expandoTable, "groupId", ExpandoColumnConstants.LONG);
		addExpandoColumn(expandoTable, "type", ExpandoColumnConstants.STRING);
	}

	@Override
	public void portalInstanceUnregistered(Company company)
		throws PortalException {

		_deleteSAPEntries(company.getCompanyId());

		ExpandoTable expandoTable = _expandoTableLocalService.fetchTable(
			company.getCompanyId(),
			_classNameLocalService.getClassNameId(
				OAuth2Authorization.class.getName()),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		_expandoColumnLocalService.deleteColumn(
			expandoTable.getTableId(), "groupId");
		_expandoColumnLocalService.deleteColumn(
			expandoTable.getTableId(), "type");

		List<ExpandoColumn> expandoColumns =
			_expandoColumnLocalService.getColumns(expandoTable.getTableId());

		if (expandoColumns.isEmpty()) {
			_expandoTableLocalService.deleteExpandoTable(expandoTable);
		}
	}

	@Activate
	protected void activate() {
		_scopeAliasesList = TransformUtil.transformToList(
			_SAP_ENTRY_OBJECT_ARRAYS,
			sapEntryObject -> StringUtil.replaceFirst(
				sapEntryObject[0], "OAUTH2_", StringPool.BLANK));
	}

	protected void addExpandoColumn(
			ExpandoTable expandoTable, String name, int type)
		throws Exception {

		ExpandoColumn expandoColumn = _expandoColumnLocalService.fetchColumn(
			expandoTable.getTableId(), name);

		if (expandoColumn != null) {
			return;
		}

		_expandoColumnLocalService.addColumn(
			expandoTable.getTableId(), name, type);
	}

	private void _addSAPEntries(long companyId) throws Exception {
		for (String[] sapEntryObjectArray : _SAP_ENTRY_OBJECT_ARRAYS) {
			String sapEntryName = sapEntryObjectArray[0];

			SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
				companyId, sapEntryName);

			if (sapEntry != null) {
				continue;
			}

			_sapEntryLocalService.addSAPEntry(
				_userLocalService.getDefaultUserId(companyId),
				sapEntryObjectArray[1], true, true, sapEntryName,
				Collections.singletonMap(LocaleUtil.getDefault(), sapEntryName),
				new ServiceContext());
		}
	}

	private void _deleteSAPEntries(long companyId) {
		for (String[] sapEntryObjectArray : _SAP_ENTRY_OBJECT_ARRAYS) {
			try {
				SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
					companyId, sapEntryObjectArray[0]);

				if (sapEntry != null) {
					_sapEntryLocalService.deleteSAPEntry(sapEntry);
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private static final String[][] _SAP_ENTRY_OBJECT_ARRAYS = {
		{
			"OAUTH2_" + ApiApplication.OAuth2ScopeAliases.ACCOUNTS_WRITE,
			DemandbaseAccountFaroController.class.getName() + "#postAccount"
		},
		{
			"OAUTH2_" + ApiApplication.OAuth2ScopeAliases.HUBSPOT_WRITE,
			HubSpotWebhookFaroController.class.getName() + "#postHubSpotWebhook"
		},
		{
			"OAUTH2_" + ApiApplication.OAuth2ScopeAliases.MARKETO_WRITE,
			MarketoWebhookFaroController.class.getName() + "#postMarketoWebhook"
		},
		{
			"OAUTH2_" +
				ApiApplication.OAuth2ScopeAliases.RECOMMENDATIONS_EVERYTHING,
			RecommendationFaroController.class.getName() + "*"
		},
		{
			"OAUTH2_" + ApiApplication.OAuth2ScopeAliases.REPORTS_EVERYTHING,
			ReportFaroController.class.getName() + "*"
		}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2AuthorizationExpandoPortalInstanceLifecycleListener.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	private List<String> _scopeAliasesList;

	@Reference
	private UserLocalService _userLocalService;

}