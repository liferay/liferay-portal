/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Mendez Gonzalez
 */
@Component(service = StagedModelDataHandler.class)
public class EmailAddressStagedModelDataHandler
	extends BaseStagedModelDataHandler<EmailAddress> {

	public static final String[] CLASS_NAMES = {EmailAddress.class.getName()};

	@Override
	public void deleteStagedModel(EmailAddress emailAddress) {
		_emailAddressLocalService.deleteEmailAddress(emailAddress);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		EmailAddress emailAddress =
			_emailAddressLocalService.fetchEmailAddressByUuidAndCompanyId(
				uuid, group.getCompanyId());

		deleteStagedModel(emailAddress);
	}

	@Override
	public List<EmailAddress> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_emailAddressLocalService.fetchEmailAddressByUuidAndCompanyId(
				uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, EmailAddress emailAddress)
		throws Exception {

		Element emailAddressElement = portletDataContext.getExportDataElement(
			emailAddress);

		portletDataContext.addClassedModel(
			emailAddressElement,
			ExportImportPathUtil.getModelPath(emailAddress), emailAddress);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, EmailAddress emailAddress)
		throws Exception {

		ScopeUtil.validateCompanyScoped(portletDataContext);

		long userId = portletDataContext.getUserId(emailAddress.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			emailAddress);

		EmailAddress existingEmailAddress =
			_emailAddressLocalService.fetchEmailAddressByUuidAndCompanyId(
				emailAddress.getUuid(), portletDataContext.getCompanyId());

		EmailAddress importedEmailAddress = null;

		if (existingEmailAddress == null) {
			serviceContext.setUuid(emailAddress.getUuid());

			importedEmailAddress = _emailAddressLocalService.addEmailAddress(
				emailAddress.getExternalReferenceCode(), userId,
				emailAddress.getClassName(), emailAddress.getClassPK(),
				emailAddress.getAddress(), emailAddress.getListTypeId(),
				emailAddress.isPrimary(), serviceContext);
		}
		else {
			importedEmailAddress = _emailAddressLocalService.updateEmailAddress(
				existingEmailAddress.getExternalReferenceCode(),
				existingEmailAddress.getEmailAddressId(),
				emailAddress.getAddress(), emailAddress.getListTypeId(),
				emailAddress.isPrimary());
		}

		portletDataContext.importClassedModel(
			emailAddress, importedEmailAddress);
	}

	@Reference
	private EmailAddressLocalService _emailAddressLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}