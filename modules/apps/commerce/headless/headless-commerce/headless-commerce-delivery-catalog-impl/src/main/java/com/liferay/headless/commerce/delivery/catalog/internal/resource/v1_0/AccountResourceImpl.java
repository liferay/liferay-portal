/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Account;
import com.liferay.headless.commerce.delivery.catalog.internal.odata.entity.v1_0.AccountEntityModel;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.AccountResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Danny Situ
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account.properties",
	scope = ServiceScope.PROTOTYPE, service = AccountResource.class
)
public class AccountResourceImpl extends BaseAccountResourceImpl {

	@Override
	public Page<Account> getChannelAccountsPage(
			Long channelId, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				addAction(
					AccountActionKeys.ADD_ACCOUNT_ENTRY, "postChannelAccount",
					PortletKeys.PORTAL, 0L)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, 0L, "getChannelAccountsPage",
					_accountEntryModelResourcePermission)
			).build(),
			booleanQuery -> {
				int count =
					_commerceChannelAccountEntryRelLocalService.
						getCommerceChannelAccountEntryRelsCount(
							channelId, null,
							CommerceChannelAccountEntryRelConstants.
								TYPE_ELIGIBILITY);

				if (count > 0) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					TermsFilter termsFilter = new TermsFilter(
						"commerceChannelIds");

					termsFilter.addValues(
						ArrayUtil.toStringArray(new long[] {channelId}));

					booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
				}
			},
			filter, AccountEntry.class.getName(), search, pagination,
			queryConfig -> {
			},
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toAccount(
				_accountEntryLocalService.getAccountEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Account postChannelAccount(Long channelId, Account account)
		throws Exception {

		AccountEntry accountEntry = _accountEntryService.addAccountEntry(
			account.getExternalReferenceCode(), contextUser.getUserId(), 0,
			account.getName(), account.getDescription(), _getDomains(account),
			null, _getLogoBytes(account, null, false), account.getTaxId(),
			_getType(account), _getStatus(account),
			_createServiceContext(account));

		int count =
			_commerceChannelAccountEntryRelLocalService.
				getCommerceChannelAccountEntryRelsCount(
					channelId, null,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		if (count > 0) {
			_commerceChannelAccountEntryRelLocalService.
				addCommerceChannelAccountEntryRel(
					contextUser.getUserId(), accountEntry.getAccountEntryId(),
					null, 0, channelId, false, 0,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);
		}

		if (_isValidId(account.getDefaultBillingAddressId())) {
			_accountEntryLocalService.updateDefaultBillingAddressId(
				accountEntry.getAccountEntryId(),
				account.getDefaultBillingAddressId());
		}

		if (_isValidId(account.getDefaultShippingAddressId())) {
			_accountEntryLocalService.updateDefaultShippingAddressId(
				accountEntry.getAccountEntryId(),
				account.getDefaultShippingAddressId());
		}

		_accountEntryOrganizationRelLocalService.
			setAccountEntryOrganizationRels(
				accountEntry.getAccountEntryId(), _getOrganizationIds(account));

		return _toAccount(accountEntry);
	}

	private ServiceContext _createServiceContext(Account account)
		throws Exception {

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				AccountEntry.class.getName(), contextCompany.getCompanyId(),
				account.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale());

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		ServiceContext serviceContext = ServiceContextBuilder.create(
			contextCompany.getGroupId(), contextHttpServletRequest, null
		).expandoBridgeAttributes(
			expandoBridgeAttributes
		).build();

		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setUserId(contextUser.getUserId());

		return serviceContext;
	}

	private String[] _getDomains(Account account) {
		String[] domains = account.getDomains();

		if (domains == null) {
			return new String[0];
		}

		return domains;
	}

	private byte[] _getLogoBytes(
			Account account, AccountEntry accountEntry,
			boolean useAccountEntryDefault)
		throws Exception {

		Long logoId = account.getLogoId();

		if ((accountEntry != null) && (logoId == null) &&
			useAccountEntryDefault) {

			logoId = accountEntry.getLogoId();
		}

		if ((logoId == null) || (logoId == 0) ||
			((accountEntry != null) && (accountEntry.getLogoId() == logoId))) {

			return null;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(logoId);

		return _file.getBytes(fileEntry.getContentStream());
	}

	private long[] _getOrganizationIds(Account account) {
		Long[] organizationIds = account.getOrganizationIds();

		if (organizationIds == null) {
			return new long[0];
		}

		return ArrayUtil.toArray(organizationIds);
	}

	private int _getStatus(Account account) {
		Integer status = account.getStatus();

		if (status == null) {
			return WorkflowConstants.STATUS_APPROVED;
		}

		return status;
	}

	private String _getType(Account account) {
		String type = account.getTypeAsString();

		if (type == null) {
			return AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS;
		}

		return type;
	}

	private boolean _isValidId(Long value) {
		if ((value == null) || (value <= 0)) {
			return false;
		}

		return true;
	}

	private Account _toAccount(AccountEntry accountEntry) throws Exception {
		if (accountEntry == null) {
			return null;
		}

		return _accountDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				accountEntry.getAccountEntryId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	private static final EntityModel _entityModel = new AccountEntityModel();

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.AccountDTOConverter)"
	)
	private DTOConverter<AccountEntry, Account> _accountDTOConverter;

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private File _file;

}