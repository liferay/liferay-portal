/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.resource.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountGroupService;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AdminAccountGroup;
import com.liferay.headless.commerce.admin.account.internal.odata.entity.v1_0.AccountGroupEntityModel;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AdminAccountGroupResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.ExpandoUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/admin-account-group.properties",
	scope = ServiceScope.PROTOTYPE, service = AdminAccountGroupResource.class
)
@Deprecated
public class AdminAccountGroupResourceImpl
	extends BaseAdminAccountGroupResourceImpl {

	@Override
	public Response deleteAccountGroup(Long id) throws Exception {
		_accountGroupService.deleteAccountGroup(id);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response deleteAccountGroupByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		AccountGroup accountGroup =
			_accountGroupService.getAccountGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		_accountGroupService.deleteAccountGroup(
			accountGroup.getAccountGroupId());

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<AdminAccountGroup>
			getAccountByExternalReferenceCodeAccountGroupsPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.getAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return _getAdminAccountGroups(
			accountEntry.getAccountEntryId(), pagination);
	}

	@Override
	public AdminAccountGroup getAccountGroup(Long id) throws Exception {
		return _adminAccountGroupDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				GetterUtil.getLong(id),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Override
	public AdminAccountGroup getAccountGroupByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		AccountGroup accountGroup =
			_accountGroupService.getAccountGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return _adminAccountGroupDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				accountGroup.getAccountGroupId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Override
	public Page<AdminAccountGroup> getAccountGroupsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			AccountGroup.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toAccountGroup(
				_accountGroupService.getAccountGroup(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public Page<AdminAccountGroup> getAccountIdAccountGroupsPage(
			Long id, Pagination pagination)
		throws Exception {

		return _getAdminAccountGroups(id, pagination);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Response patchAccountGroup(
			Long id, AdminAccountGroup adminAccountGroup)
		throws Exception {

		_accountGroupService.updateAccountGroup(
			adminAccountGroup.getExternalReferenceCode(), id,
			adminAccountGroup.getDescription(), adminAccountGroup.getName(),
			_serviceContextHelper.getServiceContext());

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchAccountGroupByExternalReferenceCode(
			String externalReferenceCode, AdminAccountGroup adminAccountGroup)
		throws Exception {

		AccountGroup accountGroup =
			_accountGroupService.getAccountGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		_accountGroupService.updateAccountGroup(
			externalReferenceCode, accountGroup.getAccountGroupId(),
			adminAccountGroup.getDescription(), adminAccountGroup.getName(),
			_serviceContextHelper.getServiceContext());

		// Expando

		Map<String, ?> customFields = adminAccountGroup.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), AccountGroup.class,
				accountGroup.getPrimaryKey(), customFields);
		}

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public AdminAccountGroup postAccountGroup(
			AdminAccountGroup adminAccountGroup)
		throws Exception {

		AccountGroup accountGroup = null;

		if (Validator.isNotNull(adminAccountGroup.getExternalReferenceCode())) {
			accountGroup =
				_accountGroupService.fetchAccountGroupByExternalReferenceCode(
					adminAccountGroup.getExternalReferenceCode(),
					contextCompany.getCompanyId());
		}

		if (accountGroup == null) {
			accountGroup = _accountGroupService.addAccountGroup(
				adminAccountGroup.getExternalReferenceCode(),
				contextUser.getUserId(), adminAccountGroup.getDescription(),
				adminAccountGroup.getName(),
				_serviceContextHelper.getServiceContext());
		}
		else {
			accountGroup = _accountGroupService.updateAccountGroup(
				adminAccountGroup.getExternalReferenceCode(),
				accountGroup.getAccountGroupId(),
				adminAccountGroup.getDescription(), adminAccountGroup.getName(),
				_serviceContextHelper.getServiceContext());
		}

		// Expando

		Map<String, ?> customFields = adminAccountGroup.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), AccountGroup.class,
				accountGroup.getPrimaryKey(), customFields);
		}

		return _adminAccountGroupDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				accountGroup.getAccountGroupId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	private Page<AdminAccountGroup> _getAdminAccountGroups(
			long accountEntryId, Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_accountGroupService.getAccountGroupsByAccountEntryId(
					accountEntryId, pagination.getStartPosition(),
					pagination.getEndPosition()),
				accountGroup -> _toAccountGroup(accountGroup)),
			pagination,
			_accountGroupService.getAccountGroupsCountByAccountEntryId(
				accountEntryId));
	}

	private AdminAccountGroup _toAccountGroup(AccountGroup accountGroup)
		throws Exception {

		return _adminAccountGroupDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				accountGroup.getAccountGroupId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	private static final EntityModel _entityModel =
		new AccountGroupEntityModel();

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.account.internal.dto.v1_0.converter.AdminAccountGroupDTOConverter)"
	)
	private DTOConverter<AccountGroup, AdminAccountGroup>
		_adminAccountGroupDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}