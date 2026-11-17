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
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.AddressLocalService;
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
public class AddressStagedModelDataHandler
	extends BaseStagedModelDataHandler<Address> {

	public static final String[] CLASS_NAMES = {Address.class.getName()};

	@Override
	public void deleteStagedModel(Address address) {
		_addressLocalService.deleteAddress(address);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		Address address = _addressLocalService.fetchAddressByUuidAndCompanyId(
			uuid, group.getCompanyId());

		if (address != null) {
			deleteStagedModel(address);
		}
	}

	@Override
	public void doExportStagedModel(
			PortletDataContext portletDataContext, Address address)
		throws Exception {

		Element addressElement = portletDataContext.getExportDataElement(
			address);

		portletDataContext.addClassedModel(
			addressElement, ExportImportPathUtil.getModelPath(address),
			address);
	}

	@Override
	public List<Address> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_addressLocalService.fetchAddressByUuidAndCompanyId(
				uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Address address)
		throws Exception {

		ScopeUtil.validateCompanyScoped(portletDataContext);

		long userId = portletDataContext.getUserId(address.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			address);

		Address existingAddress =
			_addressLocalService.fetchAddressByUuidAndCompanyId(
				address.getUuid(), portletDataContext.getCompanyId());

		Address importedAddress = null;

		if (existingAddress == null) {
			serviceContext.setUuid(address.getUuid());

			importedAddress = _addressLocalService.addAddress(
				null, userId, address.getClassName(), address.getClassPK(),
				address.getCountryId(), address.getListTypeId(),
				address.getRegionId(), address.getCity(), null,
				address.isMailing(), null, address.isPrimary(),
				address.getStreet1(), address.getStreet2(),
				address.getStreet3(), null, address.getZip(), null,
				serviceContext);
		}
		else {
			importedAddress = _addressLocalService.updateAddress(
				existingAddress.getExternalReferenceCode(),
				existingAddress.getAddressId(), address.getCountryId(),
				address.getListTypeId(), address.getRegionId(),
				address.getCity(), existingAddress.getDescription(),
				address.isMailing(), existingAddress.getName(),
				address.isPrimary(), address.getStreet1(), address.getStreet2(),
				address.getStreet3(), existingAddress.getSubtype(),
				address.getZip(), existingAddress.getPhoneNumber());
		}

		portletDataContext.importClassedModel(address, importedAddress);
	}

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}