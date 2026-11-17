/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lianne Louie
 */
@Component(service = StagedModelDataHandler.class)
public class CountryStagedModelDataHandler
	extends BaseStagedModelDataHandler<Country> {

	public static final String[] CLASS_NAMES = {Country.class.getName()};

	@Override
	public void deleteStagedModel(Country country) throws PortalException {
		_countryLocalService.deleteCountry(country.getCountryId());
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		Country country = _countryLocalService.getCountryByUuidAndCompanyId(
			uuid, group.getCompanyId());

		if (country != null) {
			deleteStagedModel(country);
		}
	}

	@Override
	public List<Country> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_countryLocalService.fetchCountryByUuidAndCompanyId(
				uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, Country country)
		throws Exception {

		Element countryElement = portletDataContext.getExportDataElement(
			country);

		portletDataContext.addClassedModel(
			countryElement, ExportImportPathUtil.getModelPath(country),
			country);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Country country)
		throws Exception {

		ScopeUtil.validateInstanceScoped(portletDataContext);

		Country existingCountry = _countryLocalService.fetchCountryByA2(
			portletDataContext.getCompanyId(), country.getA2());

		Country importedCountry = null;

		if (existingCountry == null) {
			ServiceContext serviceContext =
				portletDataContext.createServiceContext(country);

			serviceContext.setUuid(country.getUuid());

			importedCountry = _countryLocalService.addCountry(
				country.getExternalReferenceCode(), country.getA2(),
				country.getA3(), country.isActive(), country.isBillingAllowed(),
				country.getIdd(), country.getName(), country.getNumber(),
				country.getPosition(), country.isShippingAllowed(),
				country.isSubjectToVAT(), country.isZipRequired(),
				serviceContext);
		}
		else {
			importedCountry = _countryLocalService.updateCountry(
				country.getExternalReferenceCode(),
				existingCountry.getCountryId(), country.getA2(),
				country.getA3(), country.isActive(), country.isBillingAllowed(),
				country.getIdd(), country.getName(), country.getNumber(),
				country.getPosition(), country.isShippingAllowed(),
				country.isSubjectToVAT());
		}

		portletDataContext.importClassedModel(country, importedCountry);
	}

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}