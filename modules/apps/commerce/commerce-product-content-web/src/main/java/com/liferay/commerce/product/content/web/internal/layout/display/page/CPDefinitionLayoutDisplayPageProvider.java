/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.layout.display.page;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.BaseLayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Alec Sloan
 */
@Component(service = LayoutDisplayPageProvider.class)
public class CPDefinitionLayoutDisplayPageProvider
	extends BaseLayoutDisplayPageProvider<CPDefinition> {

	@Override
	public String getClassName() {
		return CPDefinition.class.getName();
	}

	@Override
	public LayoutDisplayPageObjectProvider<CPDefinition>
		getLayoutDisplayPageObjectProvider(CPDefinition cpDefinition) {

		if (cpDefinition.getStatus() == WorkflowConstants.STATUS_IN_TRASH) {
			return null;
		}

		long groupId = cpDefinition.getGroupId();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			groupId = serviceContext.getScopeGroupId();
		}

		return new CPDefinitionLayoutDisplayPageObjectProvider(
			cpDefinition, groupId);
	}

	@Override
	public LayoutDisplayPageObjectProvider<CPDefinition>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

		try {
			Group group = _groupLocalService.getGroup(groupId);

			Group companyGroup = _groupLocalService.getCompanyGroup(
				group.getCompanyId());

			FriendlyURLEntry friendlyURLEntry =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
					companyGroup.getGroupId(),
					_portal.getClassNameId(CProduct.class),
					FriendlyURLEntryConstants.
						FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
					urlTitle);

			if (friendlyURLEntry == null) {
				return null;
			}

			CProduct cProduct = _cProductLocalService.getCProduct(
				friendlyURLEntry.getClassPK());

			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(
					cProduct.getPublishedCPDefinitionId());

			return new CPDefinitionLayoutDisplayPageObjectProvider(
				cpDefinition, groupId);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Override
	public String getURLSeparator() {
		return _cpFriendlyURL.getProductURLSeparator(
			CompanyThreadLocal.getCompanyId());
	}

	@Override
	protected LayoutDisplayPageObjectProvider<CPDefinition>
		doGetLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			return null;
		}

		CPDefinition cpDefinition = null;

		long companyId = CompanyThreadLocal.getCompanyId();
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			companyId = serviceContext.getCompanyId();
			groupId = serviceContext.getScopeGroupId();
		}

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemReference.getInfoItemIdentifier();

			cpDefinition = _cpDefinitionLocalService.fetchCPDefinition(
				classPKInfoItemIdentifier.getClassPK());
		}
		else {
			ERCInfoItemIdentifier ercInfoItemIdentifier =
				(ERCInfoItemIdentifier)infoItemIdentifier;

			cpDefinition =
				_cpDefinitionLocalService.
					fetchCPDefinitionByCProductExternalReferenceCode(
						ercInfoItemIdentifier.getExternalReferenceCode(),
						companyId, true);
		}

		if ((cpDefinition == null) ||
			(cpDefinition.getStatus() == WorkflowConstants.STATUS_IN_TRASH)) {

			return null;
		}

		return new CPDefinitionLayoutDisplayPageObjectProvider(
			cpDefinition, groupId);
	}

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPFriendlyURL _cpFriendlyURL;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}