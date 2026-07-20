/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.WebUrlUtil;
import com.liferay.headless.admin.user.resource.v1_0.WebUrlResource;
import com.liferay.portal.kernel.exception.NoSuchWebsiteException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.ListTypeService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.WebsiteService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.util.DTOConverterUtil;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/web-url.properties",
	scope = ServiceScope.PROTOTYPE, service = WebUrlResource.class
)
public class WebUrlResourceImpl extends BaseWebUrlResourceImpl {

	@Override
	public void deleteWebUrl(Long websiteId) throws Exception {
		Website website = _websiteService.getWebsite(websiteId);

		_websiteService.deleteWebsite(websiteId);

		if (website.isPrimary()) {
			_updatePrimaryWebsite(website.getClassName(), website.getClassPK());
		}
	}

	@Override
	public void deleteWebUrlByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		Website website = _websiteService.fetchWebsiteByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (website == null) {
			throw new NoSuchWebsiteException(
				"Unable to find website with external reference code " +
					externalReferenceCode);
		}

		deleteWebUrl(website.getWebsiteId());
	}

	@Override
	public Page<WebUrl> getAccountByExternalReferenceCodeWebUrlsPage(
			String externalReferenceCode)
		throws Exception {

		return getAccountWebUrlsPage(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<WebUrl> getAccountWebUrlsPage(Long accountId)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryService.getAccountEntry(
			accountId);

		return Page.of(
			transform(
				_websiteService.getWebsites(
					accountEntry.getModelClassName(),
					accountEntry.getAccountEntryId()),
				WebUrlUtil::toWebUrl));
	}

	@Override
	public Page<WebUrl> getOrganizationByExternalReferenceCodeWebUrlsPage(
			String externalReferenceCode)
		throws Exception {

		return getOrganizationWebUrlsPage(
			String.valueOf(
				DTOConverterUtil.getModelPrimaryKey(
					_organizationResourceDTOConverter, externalReferenceCode)));
	}

	@Override
	public Page<WebUrl> getOrganizationWebUrlsPage(String organizationId)
		throws Exception {

		Organization organization = _organizationResourceDTOConverter.getObject(
			organizationId);

		return Page.of(
			transform(
				_websiteService.getWebsites(
					organization.getModelClassName(),
					organization.getOrganizationId()),
				WebUrlUtil::toWebUrl));
	}

	@Override
	public Page<WebUrl> getUserAccountByExternalReferenceCodeWebUrlsPage(
			String externalReferenceCode)
		throws Exception {

		return getUserAccountWebUrlsPage(
			DTOConverterUtil.getModelPrimaryKey(
				_userResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<WebUrl> getUserAccountWebUrlsPage(Long userAccountId)
		throws Exception {

		User user = _userService.getUserById(userAccountId);

		return Page.of(
			transform(
				_websiteService.getWebsites(
					Contact.class.getName(), user.getContactId()),
				WebUrlUtil::toWebUrl));
	}

	@Override
	public WebUrl getWebUrl(Long webUrlId) throws Exception {
		return WebUrlUtil.toWebUrl(_websiteService.getWebsite(webUrlId));
	}

	@Override
	public WebUrl getWebUrlByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		Website website = _websiteService.fetchWebsiteByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (website == null) {
			throw new NoSuchWebsiteException(
				"Unable to find website with external reference code " +
					externalReferenceCode);
		}

		return getWebUrl(website.getWebsiteId());
	}

	@Override
	public WebUrl patchWebUrl(Long webUrlId, WebUrl webUrl) throws Exception {
		Website website = _websiteService.getWebsite(webUrlId);

		boolean oldPrimary = website.isPrimary();

		boolean newPrimary = GetterUtil.getBoolean(
			webUrl.getPrimary(), oldPrimary);

		website = _websiteService.updateWebsite(
			GetterUtil.getString(
				webUrl.getExternalReferenceCode(),
				website.getExternalReferenceCode()),
			webUrlId, GetterUtil.getString(webUrl.getUrl(), website.getUrl()),
			GetterUtil.getLong(
				_getListTypeId(website.getClassName(), webUrl.getUrlType()),
				website.getListTypeId()),
			newPrimary);

		if (!newPrimary && oldPrimary) {
			List<Website> websites = _websiteService.getWebsites(
				website.getClassName(), website.getClassPK());

			for (Website currentWebsite : websites) {
				if ((websites.size() == 1) ||
					(currentWebsite.getWebsiteId() != website.getWebsiteId())) {

					_websiteService.updateWebsite(
						currentWebsite.getExternalReferenceCode(),
						currentWebsite.getWebsiteId(), currentWebsite.getUrl(),
						currentWebsite.getListTypeId(), true);

					break;
				}
			}
		}

		return WebUrlUtil.toWebUrl(website);
	}

	@Override
	public WebUrl patchWebUrlByExternalReferenceCode(
			String externalReferenceCode, WebUrl webUrl)
		throws Exception {

		Website website = _websiteService.fetchWebsiteByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (website == null) {
			throw new NoSuchWebsiteException(
				"Unable to find website with external reference code " +
					externalReferenceCode);
		}

		return patchWebUrl(website.getWebsiteId(), webUrl);
	}

	private Long _getListTypeId(String className, String name) {
		ListType listType = _listTypeService.fetchListType(
			contextCompany.getCompanyId(), name,
			className + ListTypeConstants.WEBSITE);

		if (listType == null) {
			return null;
		}

		return listType.getListTypeId();
	}

	private void _updatePrimaryWebsite(String className, long contactId)
		throws Exception {

		List<Website> websites = _websiteService.getWebsites(
			className, contactId);

		if (websites.isEmpty()) {
			return;
		}

		Website website = websites.get(0);

		_websiteService.updateWebsite(
			website.getExternalReferenceCode(), website.getWebsiteId(),
			website.getUrl(), website.getListTypeId(), true);
	}

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference(target = DTOConverterConstants.ACCOUNT_RESOURCE_DTO_CONVERTER)
	private DTOConverter<AccountEntry, Account> _accountResourceDTOConverter;

	@Reference
	private ListTypeService _listTypeService;

	@Reference(
		target = DTOConverterConstants.ORGANIZATION_RESOURCE_DTO_CONVERTER
	)
	private DTOConverter
		<Organization, com.liferay.headless.admin.user.dto.v1_0.Organization>
			_organizationResourceDTOConverter;

	@Reference(target = DTOConverterConstants.USER_RESOURCE_DTO_CONVERTER)
	private DTOConverter<User, UserAccount> _userResourceDTOConverter;

	@Reference
	private UserService _userService;

	@Reference
	private WebsiteService _websiteService;

}