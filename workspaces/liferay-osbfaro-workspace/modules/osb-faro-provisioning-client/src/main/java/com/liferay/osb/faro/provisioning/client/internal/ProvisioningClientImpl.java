/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.internal;

import com.liferay.osb.faro.provisioning.client.ProvisioningClient;
import com.liferay.osb.faro.provisioning.client.constants.KoroneikiConstants;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.exception.NoSuchCorpProjectException;
import com.liferay.osb.faro.provisioning.client.exception.NoSuchProductPurchaseException;
import com.liferay.osb.faro.provisioning.client.exception.NoSuchRoleException;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.provisioning.client.util.KoroneikiHttpUtil;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.Account;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.Contact;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ContactRole;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ExternalLink;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.Product;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ProductConsumption;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ProductPurchase;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 * @author Matthew Kong
 */
@Component(service = ProvisioningClient.class)
public class ProvisioningClientImpl implements ProvisioningClient {

	@Override
	public void addCorpProjectUsers(String corpProjectUuid, String[] userUuids)
		throws Exception {

		ContactRole contactRole = KoroneikiHttpUtil.fetchContactRole(
			KoroneikiConstants.CONTACT_ROLE_NAME_MEMBER,
			ContactRole.Type.ACCOUNT_CUSTOMER);

		if (contactRole == null) {
			throw new NoSuchRoleException();
		}

		Account account = _getCorpProjectAccount(corpProjectUuid);

		for (String userUuid : userUuids) {
			User user = _userLocalService.fetchUserByUuidAndCompanyId(
				userUuid, _portal.getDefaultCompanyId());

			if (user == null) {
				continue;
			}

			Contact contact = _getContact(user);

			KoroneikiHttpUtil.assignAccountContactRole(
				account.getKey(), contactRole.getKey(), contact.getUuid());
		}
	}

	@Override
	public void addProductConsumption(String corpProjectUuid, long groupId)
		throws Exception {

		ProductConsumption productConsumption = new ProductConsumption();

		Account account = _getCorpProjectAccount(corpProjectUuid);

		productConsumption.setAccountKey(account.getKey());

		Date date = new Date();

		productConsumption.setDateCreated(date);

		ProductPurchase productPurchase = _getProductPurchase(account);

		if (productPurchase == null) {
			throw new NoSuchProductPurchaseException();
		}

		if (!_isConsumableProductPurchase(productPurchase)) {
			return;
		}

		productConsumption.setEndDate(productPurchase.getEndDate());
		productConsumption.setExternalLinks(
			new ExternalLink[] {_createExternalLink(date, groupId)});
		productConsumption.setProductKey(productPurchase.getProductKey());
		productConsumption.setProductPurchaseKey(productPurchase.getKey());
		productConsumption.setStartDate(date);

		KoroneikiHttpUtil.postProductConsumption(
			account.getKey(), productConsumption);
	}

	@Override
	public void addUserCorpProjectRoles(
			String corpProjectUuid, String[] userUuids, String roleName)
		throws Exception {

		ContactRole contactRole = KoroneikiHttpUtil.fetchContactRole(
			roleName, ContactRole.Type.ACCOUNT_CUSTOMER);

		if (contactRole == null) {
			throw new NoSuchRoleException();
		}

		Account account = _getCorpProjectAccount(corpProjectUuid);

		for (String userUuid : userUuids) {
			User user = _userLocalService.fetchUserByUuidAndCompanyId(
				userUuid, _portal.getDefaultCompanyId());

			if (user == null) {
				continue;
			}

			Contact contact = _getContact(user);

			KoroneikiHttpUtil.assignAccountContactRole(
				account.getKey(), contactRole.getKey(), contact.getUuid());
		}
	}

	@Override
	public void deleteUserCorpProjectRoles(
			String corpProjectUuid, String[] userUuids, String roleName)
		throws Exception {

		ContactRole contactRole = KoroneikiHttpUtil.fetchContactRole(
			roleName, ContactRole.Type.ACCOUNT_CUSTOMER);

		if (contactRole == null) {
			throw new NoSuchRoleException();
		}

		Account account = _getCorpProjectAccount(corpProjectUuid);

		for (String userUuid : userUuids) {
			KoroneikiHttpUtil.unassignAccountContactRole(
				account.getKey(), contactRole.getKey(), userUuid);
		}
	}

	@Override
	public List<OSBAccountEntry> getOSBAccountEntries(
			String userUuid, String[] productEntryIds)
		throws Exception {

		StringBundler sb = new StringBundler(5);

		sb.append("contactUuids/any(s:s eq '");

		User user = _userLocalService.fetchUserByUuidAndCompanyId(
			userUuid, _portal.getDefaultCompanyId());

		if (user == null) {
			return Collections.emptyList();
		}

		List<OSBAccountEntry> osbAccountEntries = new ArrayList<>();

		Contact contact = _getContact(user);

		sb.append(contact.getUuid());

		sb.append("') and productKeys/any(s:s eq '");

		List<String> productKeys = new ArrayList<>();

		for (String productEntryId : productEntryIds) {
			String productName = _getProductName(productEntryId);

			if (Validator.isNull(productName)) {
				continue;
			}

			Product product = KoroneikiHttpUtil.fetchProduct(productName);

			if (product == null) {
				continue;
			}

			productKeys.add(product.getKey());
		}

		sb.append(StringUtil.merge(productKeys, "' or s eq '"));
		sb.append("')");

		int totalCount = KoroneikiHttpUtil.getAccountsCount(sb.toString());

		int page = 1;

		while (osbAccountEntries.size() < totalCount) {
			List<Account> accounts = KoroneikiHttpUtil.searchAccounts(
				sb.toString(), page, 500);

			for (Account account : accounts) {
				osbAccountEntries.add(new OSBAccountEntry(account));
			}

			page++;
		}

		return osbAccountEntries;
	}

	@Override
	public OSBAccountEntry getOSBAccountEntry(String corpProjectUuid)
		throws Exception {

		if (!corpProjectUuid.contains("Test")) {
			return new OSBAccountEntry(_getCorpProjectAccount(corpProjectUuid));
		}

		return new OSBAccountEntry() {
			{
				List<OSBOfferingEntry> osbOfferingEntries = new ArrayList<>();

				OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

				if (corpProjectUuid.endsWith("BusinessLXCTest")) {
					osbOfferingEntry.setProductEntryId(
						ProductConstants.PRODUCT_ENTRY_ID_LXC_BUSINESS);
				}
				else if (corpProjectUuid.endsWith("BusinessTest")) {
					osbOfferingEntry.setProductEntryId(
						ProductConstants.PRODUCT_ENTRY_ID_BUSINESS);
				}
				else if (corpProjectUuid.endsWith("EnterpriseLXCTest")) {
					osbOfferingEntry.setProductEntryId(
						ProductConstants.PRODUCT_ENTRY_ID_LXC_ENTERPRISE);
				}
				else if (corpProjectUuid.endsWith("EnterpriseTest")) {
					osbOfferingEntry.setProductEntryId(
						ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);
				}
				else if (corpProjectUuid.endsWith("ProLXCTest")) {
					osbOfferingEntry.setProductEntryId(
						ProductConstants.PRODUCT_ENTRY_ID_LXC_PRO);
				}

				osbOfferingEntry.setQuantity(1);
				osbOfferingEntry.setStatus(
					ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE);

				osbOfferingEntries.add(osbOfferingEntry);

				if (corpProjectUuid.contains("AddOn")) {
					OSBOfferingEntry contactsOSBOfferingEntry =
						new OSBOfferingEntry();
					OSBOfferingEntry trackedPagesOSBOfferingEntry =
						new OSBOfferingEntry();

					if (corpProjectUuid.endsWith("BusinessLXCTest") ||
						corpProjectUuid.endsWith("BusinessTest")) {

						contactsOSBOfferingEntry.setProductEntryId(
							ProductConstants.
								PRODUCT_ENTRY_ID_BUSINESS_CONTACTS);
						trackedPagesOSBOfferingEntry.setProductEntryId(
							ProductConstants.
								PRODUCT_ENTRY_ID_BUSINESS_TRACKED_PAGES);
					}
					else if (corpProjectUuid.endsWith("EnterpriseLXCTest") ||
							 corpProjectUuid.endsWith("EnterpriseTest")) {

						contactsOSBOfferingEntry.setProductEntryId(
							ProductConstants.
								PRODUCT_ENTRY_ID_ENTERPRISE_CONTACTS);
						trackedPagesOSBOfferingEntry.setProductEntryId(
							ProductConstants.
								PRODUCT_ENTRY_ID_ENTERPRISE_TRACKED_PAGES);
					}

					contactsOSBOfferingEntry.setQuantity(1);
					contactsOSBOfferingEntry.setStatus(
						ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE);

					osbOfferingEntries.add(contactsOSBOfferingEntry);

					trackedPagesOSBOfferingEntry.setQuantity(1);
					trackedPagesOSBOfferingEntry.setStatus(
						ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE);

					osbOfferingEntries.add(trackedPagesOSBOfferingEntry);
				}

				setOfferingEntries(osbOfferingEntries);
			}
		};
	}

	@Override
	public boolean isProductConsumed(String corpProjectUuid) throws Exception {
		Account account = _getCorpProjectAccount(corpProjectUuid);

		ProductPurchase productPurchase = _getProductPurchase(account);

		if (productPurchase == null) {
			throw new NoSuchProductPurchaseException();
		}

		if (!_isConsumableProductPurchase(productPurchase)) {
			return true;
		}

		List<ProductConsumption> productConsumptions =
			KoroneikiHttpUtil.getProductConsumptions(account.getKey(), 1, 100);

		for (ProductConsumption productConsumption : productConsumptions) {
			if (Objects.equals(
					productConsumption.getProductKey(),
					productPurchase.getProductKey())) {

				return true;
			}
		}

		return false;
	}

	@Override
	public void unsetCorpProjectUsers(
			String corpProjectUuid, String[] userUuids)
		throws Exception {

		Account account = _getCorpProjectAccount(corpProjectUuid);

		for (String userUuid : userUuids) {
			int page = 1;

			while (true) {
				List<ContactRole> contactRoles =
					KoroneikiHttpUtil.getAccountContactRoles(
						account.getKey(), userUuid, page, 500);

				if (ListUtil.isEmpty(contactRoles)) {
					break;
				}

				for (ContactRole contactRole : contactRoles) {
					KoroneikiHttpUtil.unassignAccountContactRole(
						account.getKey(), contactRole.getKey(), userUuid);
				}

				page++;
			}
		}
	}

	private ExternalLink _createExternalLink(Date date, long groupId) {
		ExternalLink externalLink = new ExternalLink();

		externalLink.setDateCreated(date);
		externalLink.setDomain("analytics-cloud");
		externalLink.setEntityId(String.valueOf(groupId));
		externalLink.setEntityName("groupId");

		StringBuilder sb = new StringBuilder();

		sb.append(FaroPropsValues.FARO_URL);
		sb.append("/workspace/");
		sb.append(groupId);
		sb.append("/sites");

		externalLink.setUrl(sb.toString());

		return externalLink;
	}

	private Contact _getContact(User user) throws Exception {
		Contact contact = KoroneikiHttpUtil.fetchContact(
			user.getEmailAddress());

		if (contact == null) {
			contact = new Contact();

			contact.setEmailAddress(user.getEmailAddress());
			contact.setFirstName(user.getFirstName());
			contact.setLastName(user.getLastName());
			contact.setUuid(user.getUuid());

			contact = KoroneikiHttpUtil.postContact(contact);
		}

		return contact;
	}

	private Account _getCorpProjectAccount(String corpProjectUuid)
		throws Exception {

		Account account = null;

		if (StringUtil.startsWith(
				corpProjectUuid, KoroneikiConstants.ACCOUNT_KEY_PREFIX)) {

			account = KoroneikiHttpUtil.fetchAccount(corpProjectUuid);
		}
		else {
			List<Account> accounts = KoroneikiHttpUtil.getAccounts(
				KoroneikiConstants.DOMAIN_WEB, corpProjectUuid,
				KoroneikiConstants.ENTITY_NAME_CORP_PROJECT, 1, 1);

			if (ListUtil.isNotEmpty(accounts)) {
				account = accounts.get(0);
			}
		}

		if (account == null) {
			throw new NoSuchCorpProjectException();
		}

		return account;
	}

	private String _getProductName(String productEntryId) {
		String productName = ProductConstants.getProductName(productEntryId);

		if (productName != null) {
			if (!productName.startsWith("Liferay SaaS")) {
				productName = StringUtil.removeSubstring(
					productName, "Liferay ");
			}

			return productName;
		}

		return null;
	}

	private ProductPurchase _getProductPurchase(Account account) {
		ProductPurchase baseProductPurchase = null;

		List<String> baseProductEntryIds =
			ProductConstants.getBaseProductEntryIds();

		for (ProductPurchase productPurchase : account.getProductPurchases()) {
			if (!baseProductEntryIds.contains(
					productPurchase.getProductKey())) {

				continue;
			}

			if ((baseProductPurchase == null) ||
				((baseProductPurchase.getStatus() !=
					productPurchase.getStatus()) &&
				 Objects.equals(
					 productPurchase.getStatus(),
					 ProductPurchase.Status.APPROVED)) ||
				((baseProductPurchase.getStatus() ==
					productPurchase.getStatus()) &&
				 _isAfter(baseProductPurchase, productPurchase))) {

				baseProductPurchase = productPurchase;
			}
		}

		return baseProductPurchase;
	}

	private boolean _isAfter(
		ProductPurchase baseProductPurchase, ProductPurchase productPurchase) {

		Date baseProductPurchaseStartDate = baseProductPurchase.getStartDate();

		if ((baseProductPurchaseStartDate == null) ||
			(baseProductPurchaseStartDate.getTime() >
				System.currentTimeMillis())) {

			return false;
		}

		Date productPurchaseStartDate = productPurchase.getStartDate();

		if (productPurchaseStartDate.getTime() >
				baseProductPurchaseStartDate.getTime()) {

			return true;
		}

		return false;
	}

	private boolean _isConsumableProductPurchase(
		ProductPurchase productPurchase) {

		return !Objects.equals(
			productPurchase.getProductKey(),
			ProductConstants.PRODUCT_ENTRY_ID_DIGITAL_SALES_ROOM);
	}

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}