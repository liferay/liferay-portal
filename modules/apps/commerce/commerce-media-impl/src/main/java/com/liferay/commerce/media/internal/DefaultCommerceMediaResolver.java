/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.media.internal;

import com.liferay.account.constants.AccountConstants;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.media.constants.CommerceMediaConstants;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;

import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceMediaResolver.class)
public class DefaultCommerceMediaResolver implements CommerceMediaResolver {

	@Override
	public String getDefaultURL(long groupId) {
		return StringBundler.concat(
			_portal.getPathModule(), StringPool.SLASH,
			CommerceMediaConstants.SERVLET_PATH, "/default/?groupId=", groupId);
	}

	@Override
	public String getDownloadURL(
			long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException {

		return getURL(commerceAccountId, cpAttachmentFileEntryId, true, false);
	}

	@Override
	public String getDownloadVirtualOrderItemURL(
		long commerceVirtualOrderItemId, long fileEntryId) {

		return StringBundler.concat(
			_portal.getPathModule(), StringPool.SLASH,
			CommerceMediaConstants.SERVLET_PATH,
			CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_ORDER_ITEM,
			commerceVirtualOrderItemId,
			CommerceMediaConstants.URL_SEPARATOR_FILE, fileEntryId);
	}

	@Override
	public String getDownloadVirtualProductSampleURL(
			String className, long classPK, long commerceAccountId,
			long fileEntryId)
		throws PortalException {

		if (className.equals(CPInstance.class.getName())) {
			return StringBundler.concat(
				_portal.getPathModule(), StringPool.SLASH,
				CommerceMediaConstants.SERVLET_PATH, "/accounts/",
				commerceAccountId,
				CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_SKU_SAMPLE,
				classPK, CommerceMediaConstants.URL_SEPARATOR_FILE,
				fileEntryId);
		}

		return StringBundler.concat(
			_portal.getPathModule(), StringPool.SLASH,
			CommerceMediaConstants.SERVLET_PATH, "/accounts/",
			commerceAccountId,
			CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_PRODUCT_SAMPLE,
			classPK, CommerceMediaConstants.URL_SEPARATOR_FILE, fileEntryId);
	}

	@Override
	public String getDownloadVirtualProductURL(
			String className, long classPK, long commerceAccountId,
			long fileEntryId)
		throws PortalException {

		if (className.equals(CPInstance.class.getName())) {
			return StringBundler.concat(
				_portal.getPathModule(), StringPool.SLASH,
				CommerceMediaConstants.SERVLET_PATH, "/accounts/",
				commerceAccountId,
				CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_SKU, classPK,
				CommerceMediaConstants.URL_SEPARATOR_FILE, fileEntryId);
		}

		return StringBundler.concat(
			_portal.getPathModule(), StringPool.SLASH,
			CommerceMediaConstants.SERVLET_PATH, "/accounts/",
			commerceAccountId,
			CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_PRODUCT, classPK,
			CommerceMediaConstants.URL_SEPARATOR_FILE, fileEntryId);
	}

	@Override
	public String getThumbnailURL(
			long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException {

		return getURL(commerceAccountId, cpAttachmentFileEntryId, false, true);
	}

	@Override
	public String getURL(long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException {

		return getURL(commerceAccountId, cpAttachmentFileEntryId, false, false);
	}

	@Override
	public String getURL(
			long commerceAccountId, long cpAttachmentFileEntryId,
			boolean download, boolean thumbnail)
		throws PortalException {

		return getURL(
			commerceAccountId, cpAttachmentFileEntryId, download, thumbnail,
			true);
	}

	@Override
	public String getURL(
			long commerceAccountId, long cpAttachmentFileEntryId,
			boolean download, boolean thumbnail, boolean secure)
		throws PortalException {

		StringBundler sb = new StringBundler(9);

		sb.append(_portal.getPathModule());
		sb.append(StringPool.SLASH);
		sb.append(CommerceMediaConstants.SERVLET_PATH);

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryLocalService.fetchCPAttachmentFileEntry(
				cpAttachmentFileEntryId);

		if (cpAttachmentFileEntry == null) {
			HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

			if (httpSession == null) {
				return sb.toString();
			}

			long companyId = GetterUtil.getLong(
				httpSession.getAttribute(WebKeys.COMPANY_ID));

			Company company = _companyLocalService.getCompany(companyId);

			return getDefaultURL(company.getGroupId());
		}

		if (secure) {
			DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
				cpAttachmentFileEntry.getFileEntryId());

			if ((dlFileEntry != null) &&
				!cpAttachmentFileEntry.isCDNEnabled() &&
				!_dlFileEntryModelResourcePermission.contains(
					PermissionThreadLocal.getPermissionChecker(), dlFileEntry,
					ActionKeys.VIEW)) {

				return getDefaultURL(cpAttachmentFileEntry.getGroupId());
			}

			String className = cpAttachmentFileEntry.getClassName();

			if (className.equals(AssetCategory.class.getName())) {
				AssetCategory assetCategory =
					_assetCategoryLocalService.fetchCategory(
						cpAttachmentFileEntry.getClassPK());

				if (!AssetCategoryPermission.contains(
						PermissionThreadLocal.getPermissionChecker(),
						assetCategory, ActionKeys.VIEW)) {

					return getDefaultURL(cpAttachmentFileEntry.getGroupId());
				}
			}
			else if (className.equals(CPDefinition.class.getName())) {
				if (commerceAccountId ==
						AccountConstants.ACCOUNT_ENTRY_ID_ADMIN) {

					CPDefinition cpDefinition =
						_cpDefinitionLocalService.getCPDefinition(
							cpAttachmentFileEntry.getClassPK());

					if (!_commerceCatalogModelResourcePermission.contains(
							PermissionThreadLocal.getPermissionChecker(),
							cpDefinition.getCommerceCatalog(),
							ActionKeys.VIEW)) {

						return getDefaultURL(
							cpAttachmentFileEntry.getGroupId());
					}
				}
				else {
					if (!_commerceProductViewPermission.contains(
							PermissionThreadLocal.getPermissionChecker(),
							commerceAccountId,
							cpAttachmentFileEntry.getClassPK())) {

						return getDefaultURL(
							cpAttachmentFileEntry.getGroupId());
					}
				}
			}
		}

		if (cpAttachmentFileEntry.isCDNEnabled()) {
			return cpAttachmentFileEntry.getCDNURL();
		}

		sb.append("/accounts/");
		sb.append(commerceAccountId);

		if (cpAttachmentFileEntry.getType() ==
				CPAttachmentFileEntryConstants.TYPE_IMAGE) {

			sb.append("/images/");
		}
		else if (cpAttachmentFileEntry.getType() ==
					CPAttachmentFileEntryConstants.TYPE_OTHER) {

			sb.append("/attachments/");
		}

		sb.append(cpAttachmentFileEntry.getCPAttachmentFileEntryId());
		sb.append("?download=");
		sb.append(download);

		return sb.toString();
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	private ModelResourcePermission<DLFileEntry>
		_dlFileEntryModelResourcePermission;

	@Reference
	private Portal _portal;

}