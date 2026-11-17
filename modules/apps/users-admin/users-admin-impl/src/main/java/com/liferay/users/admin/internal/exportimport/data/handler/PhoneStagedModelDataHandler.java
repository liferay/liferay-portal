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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PhoneLocalService;
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
public class PhoneStagedModelDataHandler
	extends BaseStagedModelDataHandler<Phone> {

	public static final String[] CLASS_NAMES = {Phone.class.getName()};

	@Override
	public void deleteStagedModel(Phone phone) {
		_phoneLocalService.deletePhone(phone);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		Phone phone = _phoneLocalService.fetchPhoneByUuidAndCompanyId(
			uuid, group.getCompanyId());

		if (phone != null) {
			deleteStagedModel(phone);
		}
	}

	@Override
	public List<Phone> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_phoneLocalService.fetchPhoneByUuidAndCompanyId(uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, Phone phone)
		throws Exception {

		Element phoneElement = portletDataContext.getExportDataElement(phone);

		portletDataContext.addClassedModel(
			phoneElement, ExportImportPathUtil.getModelPath(phone), phone);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Phone phone)
		throws Exception {

		ScopeUtil.validateInstanceScoped(portletDataContext);

		long userId = portletDataContext.getUserId(phone.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			phone);

		Phone existingPhone = _phoneLocalService.fetchPhoneByUuidAndCompanyId(
			phone.getUuid(), portletDataContext.getCompanyId());

		Phone importedPhone = null;

		if (existingPhone == null) {
			serviceContext.setUuid(phone.getUuid());

			importedPhone = _phoneLocalService.addPhone(
				phone.getExternalReferenceCode(), userId, phone.getClassName(),
				phone.getClassPK(), phone.getNumber(), phone.getExtension(),
				phone.getListTypeId(), phone.isPrimary(), serviceContext);
		}
		else {
			importedPhone = _phoneLocalService.updatePhone(
				phone.getExternalReferenceCode(), existingPhone.getPhoneId(),
				phone.getNumber(), phone.getExtension(), phone.getListTypeId(),
				phone.isPrimary());
		}

		portletDataContext.importClassedModel(phone, importedPhone);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PhoneLocalService _phoneLocalService;

}