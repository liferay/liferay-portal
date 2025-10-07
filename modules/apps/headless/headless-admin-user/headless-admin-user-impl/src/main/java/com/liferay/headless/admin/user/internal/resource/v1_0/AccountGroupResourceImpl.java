/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.exception.DuplicateAccountGroupRelException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountGroupRelService;
import com.liferay.account.service.AccountGroupService;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.dto.v1_0.AccountGroup;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.odata.entity.v1_0.AccountGroupEntityModel;
import com.liferay.headless.admin.user.internal.util.v1_0.ResourcePermissionUtil;
import com.liferay.headless.admin.user.resource.v1_0.AccountGroupResource;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.util.DTOConverterUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account-group.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = AccountGroupResource.class
)
public class AccountGroupResourceImpl
	extends BaseAccountGroupResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<AccountGroup> {

	@Override
	public void deleteAccountGroup(Long accountGroupId) throws Exception {
		_accountGroupService.deleteAccountGroup(accountGroupId);
	}

	@Override
	public void deleteAccountGroupByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		deleteAccountGroup(
			DTOConverterUtil.getModelPrimaryKey(
				_accountGroupResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public void
			deleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		AccountGroupRel accountGroupRel =
			_accountGroupRelService.fetchAccountGroupRel(
				DTOConverterUtil.getModelPrimaryKey(
					_accountGroupResourceDTOConverter, externalReferenceCode),
				AccountEntry.class.getName(),
				DTOConverterUtil.getModelPrimaryKey(
					_accountResourceDTOConverter,
					accountExternalReferenceCode));

		if (accountGroupRel != null) {
			_accountGroupRelService.deleteAccountGroupRel(
				accountGroupRel.getAccountGroupRelId());
		}
	}

	@Override
	public Page<AccountGroup> getAccountAccountGroupsPage(
			Long accountId, Pagination pagination)
		throws Exception {

		return _getAccountGroups(accountId, pagination);
	}

	@Override
	public Page<AccountGroup>
			getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
				String accountExternalReferenceCode, Pagination pagination)
		throws Exception {

		return getAccountAccountGroupsPage(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, accountExternalReferenceCode),
			pagination);
	}

	@Override
	public AccountGroup getAccountGroup(Long accountGroupId) throws Exception {
		return _toAccountGroup(
			_accountGroupService.getAccountGroup(accountGroupId));
	}

	@Override
	public AccountGroup getAccountGroupByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return getAccountGroup(
			DTOConverterUtil.getModelPrimaryKey(
				_accountGroupResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<AccountGroup> getAccountGroupsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				addAction(
					AccountActionKeys.ADD_ACCOUNT_GROUP, "postAccountGroup",
					PortletKeys.PORTAL, 0L)
			).put(
				"create-by-external-reference-code",
				addAction(
					AccountActionKeys.ADD_ACCOUNT_GROUP,
					"putAccountGroupByExternalReferenceCode",
					PortletKeys.PORTAL, 0L)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, 0L, "getAccountGroupsPage",
					_accountGroupModelResourcePermission)
			).build(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			com.liferay.account.model.AccountGroup.class.getName(),
			StringPool.BLANK, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}
			},
			sorts,
			document -> _toAccountGroup(
				_accountGroupService.getAccountGroup(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return new AccountGroupEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(
					com.liferay.account.model.AccountGroup.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportDescriptor() {

			@Override
			public String getItemClassName() {
				return com.liferay.account.model.AccountGroup.class.getName();
			}

			@Override
			public List<String> getNestedFields() {
				return List.of("accountBriefs", "creator");
			}

			@Override
			public String getPortletId() {
				return AccountPortletKeys.ACCOUNT_GROUPS_ADMIN;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

		};
	}

	@Override
	public AccountGroup patchAccountGroup(
			Long accountGroupId, AccountGroup accountGroup)
		throws Exception {

		com.liferay.account.model.AccountGroup serviceBuilderAccountGroup =
			_accountGroupService.getAccountGroup(accountGroupId);

		return _updateAccountGroup(accountGroup, serviceBuilderAccountGroup);
	}

	@Override
	public AccountGroup patchAccountGroupByExternalReferenceCode(
			String externalReferenceCode, AccountGroup accountGroup)
		throws Exception {

		com.liferay.account.model.AccountGroup serviceBuilderAccountGroup =
			_accountGroupService.getAccountGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return _updateAccountGroup(accountGroup, serviceBuilderAccountGroup);
	}

	@Override
	public AccountGroup postAccountGroup(AccountGroup accountGroup)
		throws Exception {

		com.liferay.account.model.AccountGroup serviceBuilderAccountGroup =
			_accountGroupService.addAccountGroup(
				accountGroup.getExternalReferenceCode(),
				contextUser.getUserId(), accountGroup.getDescription(),
				accountGroup.getName(), _createServiceContext(accountGroup));

		return _toAccountGroup(
			_updateNestedResources(accountGroup, serviceBuilderAccountGroup));
	}

	@Override
	public void
			postAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		_accountGroupRelService.addAccountGroupRel(
			DTOConverterUtil.getModelPrimaryKey(
				_accountGroupResourceDTOConverter, externalReferenceCode),
			AccountEntry.class.getName(),
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, accountExternalReferenceCode));
	}

	@Override
	public AccountGroup putAccountGroup(
			Long accountGroupId, AccountGroup accountGroup)
		throws Exception {

		if (accountGroupId <= 0) {
			com.liferay.account.model.AccountGroup serviceBuilderAccountGroup =
				_accountGroupService.addAccountGroup(
					accountGroup.getExternalReferenceCode(),
					contextUser.getUserId(), accountGroup.getDescription(),
					accountGroup.getName(),
					_createServiceContext(accountGroup));

			return _toAccountGroup(
				_updateNestedResources(
					accountGroup, serviceBuilderAccountGroup));
		}

		return _toAccountGroup(
			_updateNestedResources(
				accountGroup,
				_accountGroupService.updateAccountGroup(
					accountGroup.getExternalReferenceCode(), accountGroupId,
					accountGroup.getDescription(), accountGroup.getName(),
					_createServiceContext(accountGroup))));
	}

	@Override
	public AccountGroup putAccountGroupByExternalReferenceCode(
			String externalReferenceCode, AccountGroup accountGroup)
		throws Exception {

		com.liferay.account.model.AccountGroup serviceBuilderAccountGroup =
			_accountGroupService.fetchAccountGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (serviceBuilderAccountGroup == null) {
			return putAccountGroup(0L, accountGroup);
		}

		return putAccountGroup(
			serviceBuilderAccountGroup.getAccountGroupId(), accountGroup);
	}

	private com.liferay.account.model.AccountGroup _addAccountGroupRel(
			AccountBrief accountBrief,
			com.liferay.account.model.AccountGroup serviceBuilderAccountGroup)
		throws Exception {

		String externalReferenceCode = accountBrief.getExternalReferenceCode();
		String type = accountBrief.getType();

		if (Validator.isNull(externalReferenceCode) || Validator.isNull(type)) {
			return serviceBuilderAccountGroup;
		}

		try {
			AccountEntry accountEntry =
				_accountEntryService.getOrAddEmptyAccountEntry(
					externalReferenceCode, accountBrief.getName(), type);

			_accountGroupRelService.addAccountGroupRel(
				serviceBuilderAccountGroup.getAccountGroupId(),
				AccountEntry.class.getName(), accountEntry.getAccountEntryId());
		}
		catch (DuplicateAccountGroupRelException
					duplicateAccountGroupRelException) {

			if (_log.isDebugEnabled()) {
				_log.debug(duplicateAccountGroupRelException);
			}
		}

		return serviceBuilderAccountGroup;
	}

	private ServiceContext _createServiceContext(AccountGroup accountGroup)
		throws Exception {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			contextCompany.getGroupId(), contextHttpServletRequest, null
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				com.liferay.account.model.AccountGroup.class.getName(),
				contextCompany.getCompanyId(), accountGroup.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();

		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setUserId(contextUser.getUserId());

		return serviceContext;
	}

	private Page<AccountGroup> _getAccountGroups(
			long accountId, Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_accountGroupService.getAccountGroupsByAccountEntryId(
					accountId, pagination.getStartPosition(),
					pagination.getEndPosition()),
				accountGroup -> _toAccountGroup(accountGroup)),
			pagination,
			_accountGroupService.getAccountGroupsCountByAccountEntryId(
				accountId));
	}

	private DTOConverterContext _getDTOConverterContext(long accountGroupId) {
		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			HashMapBuilder.<String, Map<String, String>>put(
				"create-account-group-by-external-reference-code-account-by-" +
					"external-reference-code",
				addAction(
					AccountActionKeys.ASSIGN_ACCOUNTS, accountGroupId,
					"postAccountGroupByExternalReferenceCodeAccount" +
						"ByExternalReferenceCode",
					_accountGroupModelResourcePermission)
			).put(
				"delete",
				addAction(
					ActionKeys.DELETE, accountGroupId, "deleteAccountGroup",
					_accountGroupModelResourcePermission)
			).put(
				"delete-account-group-by-external-reference-code-account-by-" +
					"external-reference-code",
				addAction(
					AccountActionKeys.ASSIGN_ACCOUNTS, accountGroupId,
					"deleteAccountGroupByExternalReferenceCode" +
						"AccountByExternalReferenceCode",
					_accountGroupModelResourcePermission)
			).put(
				"delete-by-external-reference-code",
				addAction(
					ActionKeys.DELETE, accountGroupId,
					"deleteAccountGroupByExternalReferenceCode",
					_accountGroupModelResourcePermission)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, accountGroupId, "getAccountGroup",
					_accountGroupModelResourcePermission)
			).put(
				"get-by-external-reference-code",
				addAction(
					ActionKeys.VIEW, accountGroupId,
					"getAccountGroupByExternalReferenceCode",
					_accountGroupModelResourcePermission)
			).put(
				"replace",
				addAction(
					ActionKeys.UPDATE, accountGroupId, "putAccountGroup",
					_accountGroupModelResourcePermission)
			).put(
				"replace-by-external-reference-code",
				addAction(
					ActionKeys.UPDATE, accountGroupId,
					"putAccountGroupByExternalReferenceCode",
					_accountGroupModelResourcePermission)
			).put(
				"update",
				addAction(
					ActionKeys.UPDATE, accountGroupId, "patchAccountGroup",
					_accountGroupModelResourcePermission)
			).put(
				"update-by-external-reference-code",
				addAction(
					ActionKeys.UPDATE, accountGroupId,
					"patchAccountGroupByExternalReferenceCode",
					_accountGroupModelResourcePermission)
			).build(),
			null, contextHttpServletRequest, accountGroupId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private AccountGroup _toAccountGroup(
			com.liferay.account.model.AccountGroup accountGroup)
		throws Exception {

		return _accountGroupResourceDTOConverter.toDTO(
			_getDTOConverterContext(accountGroup.getAccountGroupId()));
	}

	private AccountGroup _updateAccountGroup(
			AccountGroup accountGroup,
			com.liferay.account.model.AccountGroup serviceBuilderAccountGroup)
		throws Exception {

		serviceBuilderAccountGroup = _accountGroupService.updateAccountGroup(
			GetterUtil.getString(
				accountGroup.getExternalReferenceCode(),
				serviceBuilderAccountGroup.getExternalReferenceCode()),
			serviceBuilderAccountGroup.getAccountGroupId(),
			GetterUtil.getString(
				accountGroup.getDescription(),
				serviceBuilderAccountGroup.getDescription()),
			GetterUtil.getString(
				accountGroup.getName(), serviceBuilderAccountGroup.getName()),
			_createServiceContext(accountGroup));

		return _toAccountGroup(
			_updateNestedResources(accountGroup, serviceBuilderAccountGroup));
	}

	private com.liferay.account.model.AccountGroup _updateNestedResources(
			AccountGroup accountGroup,
			com.liferay.account.model.AccountGroup serviceBuilderAccountGroup)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35914")) {
			return serviceBuilderAccountGroup;
		}

		AccountBrief[] accountBriefs = accountGroup.getAccountBriefs();

		if (ArrayUtil.isNotEmpty(accountBriefs)) {
			for (AccountBrief accountBrief : accountBriefs) {
				serviceBuilderAccountGroup = _addAccountGroupRel(
					accountBrief, serviceBuilderAccountGroup);
			}
		}

		return ResourcePermissionUtil.setResourcePermissions(
			serviceBuilderAccountGroup,
			serviceBuilderAccountGroup.getCompanyId(),
			accountGroup.getPermissions(), _resourcePermissionLocalService,
			_roleService, _roleTypeContributorProvider);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountGroupResourceImpl.class);

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountGroup)"
	)
	private ModelResourcePermission<com.liferay.account.model.AccountGroup>
		_accountGroupModelResourcePermission;

	@Reference
	private AccountGroupRelService _accountGroupRelService;

	@Reference(
		target = DTOConverterConstants.ACCOUNT_GROUP_RESOURCE_DTO_CONVERTER
	)
	private DTOConverter<com.liferay.account.model.AccountGroup, AccountGroup>
		_accountGroupResourceDTOConverter;

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference(target = DTOConverterConstants.ACCOUNT_RESOURCE_DTO_CONVERTER)
	private DTOConverter<AccountEntry, Account> _accountResourceDTOConverter;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

}