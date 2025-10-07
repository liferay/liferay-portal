/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.PasswordPolicyRel;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrgLaborLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyRelLocalService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Mendez Gonzalez
 */
@Component(service = StagedModelDataHandler.class)
public class OrganizationStagedModelDataHandler
	extends BaseStagedModelDataHandler<Organization> {

	public static final String[] CLASS_NAMES = {Organization.class.getName()};

	@Override
	public void deleteStagedModel(Organization organization)
		throws PortalException {

		_organizationLocalService.deleteOrganization(organization);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		Organization organization =
			_organizationLocalService.fetchOrganizationByUuidAndCompanyId(
				uuid, group.getCompanyId());

		if (organization != null) {
			deleteStagedModel(organization);
		}
	}

	@Override
	public List<Organization> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_organizationLocalService.fetchOrganizationByUuidAndCompanyId(
				uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(Organization organization) {
		return organization.getName();
	}

	@Override
	public boolean validateReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		Map<Long, Long> organizationIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Organization.class);

		long organizationId = GetterUtil.getLong(
			referenceElement.attributeValue("class-pk"));

		if (organizationIds.containsKey(organizationId)) {
			return true;
		}

		Organization organization =
			_organizationLocalService.fetchOrganizationByUuidAndCompanyId(
				GetterUtil.getString(referenceElement.attributeValue("uuid")),
				GetterUtil.getLong(portletDataContext.getCompanyId()));

		if (organization == null) {
			return false;
		}

		organizationIds.put(organizationId, organization.getOrganizationId());

		return true;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		Queue<Organization> organizations = new LinkedList<>();

		organizations.add(organization);

		while (!organizations.isEmpty()) {
			Organization exportedOrganization = organizations.remove();

			if (exportedOrganization.getParentOrganizationId() !=
					OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, exportedOrganization,
					exportedOrganization.getParentOrganization(),
					PortletDataContext.REFERENCE_TYPE_PARENT);
			}

			_exportAddresses(portletDataContext, exportedOrganization);
			_exportCountry(portletDataContext, exportedOrganization);
			_exportEmailAddresses(portletDataContext, exportedOrganization);
			_exportListType(portletDataContext, exportedOrganization);
			_exportOrgLabors(portletDataContext, exportedOrganization);
			_exportPasswordPolicyRel(portletDataContext, exportedOrganization);
			_exportPhones(portletDataContext, exportedOrganization);
			_exportWebsites(portletDataContext, exportedOrganization);

			Element organizationElement =
				portletDataContext.getExportDataElement(exportedOrganization);

			portletDataContext.addClassedModel(
				organizationElement,
				ExportImportPathUtil.getModelPath(exportedOrganization),
				exportedOrganization);

			organizations.addAll(exportedOrganization.getSuborganizations());
		}
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		organization = _importCountry(portletDataContext, organization);
		organization = _importListType(portletDataContext, organization);

		long userId = portletDataContext.getUserId(organization.getUserUuid());

		Map<Long, Long> organizationIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Organization.class);

		long parentOrganizationId = MapUtil.getLong(
			organizationIds, organization.getParentOrganizationId(),
			organization.getParentOrganizationId());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			organization);

		serviceContext.setUserId(userId);

		Organization existingOrganization =
			_organizationLocalService.fetchOrganizationByUuidAndCompanyId(
				organization.getUuid(), portletDataContext.getCompanyId());

		if (existingOrganization == null) {
			existingOrganization = _organizationLocalService.fetchOrganization(
				portletDataContext.getCompanyId(), organization.getName());
		}

		Organization importedOrganization = null;

		if (existingOrganization == null) {
			serviceContext.setUuid(organization.getUuid());

			importedOrganization = _organizationLocalService.addOrganization(
				null, userId, parentOrganizationId, organization.getName(),
				organization.getType(), organization.getRegionId(),
				organization.getCountryId(), organization.getStatusListTypeId(),
				organization.getComments(), false, serviceContext);
		}
		else {
			importedOrganization = _organizationLocalService.updateOrganization(
				existingOrganization.getExternalReferenceCode(),
				portletDataContext.getCompanyId(),
				existingOrganization.getOrganizationId(), parentOrganizationId,
				organization.getName(), organization.getType(),
				organization.getRegionId(), organization.getCountryId(),
				organization.getStatusListTypeId(), organization.getComments(),
				true, null, false, serviceContext);
		}

		_importAddresses(
			portletDataContext, organization, importedOrganization);
		_importEmailAddresses(
			portletDataContext, organization, importedOrganization);
		_importOrgLabors(
			portletDataContext, organization, importedOrganization);
		_importPasswordPolicyRel(
			portletDataContext, organization, importedOrganization);
		_importPhones(portletDataContext, organization, importedOrganization);
		_importWebsites(portletDataContext, organization, importedOrganization);

		portletDataContext.importClassedModel(
			organization, importedOrganization);
	}

	@Override
	protected void importReferenceStagedModels(
			PortletDataContext portletDataContext, Organization organization)
		throws PortletDataException {

		if (organization.getParentOrganizationId() !=
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, organization, Organization.class,
				organization.getParentOrganizationId());
		}

		StagedModelDataHandlerUtil.importReferenceStagedModel(
			portletDataContext, Country.class, organization.getCountryId());
	}

	private void _exportAddresses(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		List<Address> addresses = _addressLocalService.getAddresses(
			organization.getCompanyId(), organization.getModelClassName(),
			organization.getOrganizationId());

		for (Address address : addresses) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, organization, address,
				PortletDataContext.REFERENCE_TYPE_EMBEDDED);
		}
	}

	private void _exportCountry(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		if (organization.getCountryId() > 0) {
			Country country = _countryLocalService.getCountry(
				organization.getCountryId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, organization, country,
				PortletDataContext.REFERENCE_TYPE_EMBEDDED);
		}
	}

	private void _exportEmailAddresses(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		List<EmailAddress> emailAddresses =
			_emailAddressLocalService.getEmailAddresses(
				organization.getCompanyId(), organization.getModelClassName(),
				organization.getOrganizationId());

		for (EmailAddress emailAddress : emailAddresses) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, organization, emailAddress,
				PortletDataContext.REFERENCE_TYPE_EMBEDDED);
		}
	}

	private void _exportListType(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		if (organization.getStatusListTypeId() > 0) {
			ListType listType = _listTypeLocalService.getListType(
				organization.getStatusListTypeId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, organization, listType,
				PortletDataContext.REFERENCE_TYPE_EMBEDDED);
		}
	}

	private void _exportOrgLabors(
		PortletDataContext portletDataContext, Organization organization) {

		List<OrgLabor> orgLabors = _orgLaborLocalService.getOrgLabors(
			organization.getOrganizationId());

		String path = ExportImportPathUtil.getModelPath(
			organization, OrgLabor.class.getSimpleName());

		portletDataContext.addZipEntry(path, orgLabors);
	}

	private void _exportPasswordPolicyRel(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		PasswordPolicyRel passwordPolicyRel =
			_passwordPolicyRelLocalService.fetchPasswordPolicyRel(
				Organization.class.getName(), organization.getOrganizationId());

		if (passwordPolicyRel == null) {
			return;
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, organization,
			_passwordPolicyLocalService.getPasswordPolicy(
				passwordPolicyRel.getPasswordPolicyId()),
			PortletDataContext.REFERENCE_TYPE_STRONG);
	}

	private void _exportPhones(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		List<Phone> phones = _phoneLocalService.getPhones(
			organization.getCompanyId(), organization.getModelClassName(),
			organization.getOrganizationId());

		for (Phone phone : phones) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, organization, phone,
				PortletDataContext.REFERENCE_TYPE_EMBEDDED);
		}
	}

	private void _exportWebsites(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		List<Website> websites = _websiteLocalService.getWebsites(
			organization.getCompanyId(), organization.getModelClassName(),
			organization.getOrganizationId());

		for (Website website : websites) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, organization, website,
				PortletDataContext.REFERENCE_TYPE_EMBEDDED);
		}
	}

	private void _importAddresses(
			PortletDataContext portletDataContext, Organization organization,
			Organization importedOrganization)
		throws Exception {

		List<Element> addressElements =
			portletDataContext.getReferenceDataElements(
				organization, Address.class);

		List<Address> addresses = new ArrayList<>(addressElements.size());

		for (Element addressElement : addressElements) {
			String addressPath = addressElement.attributeValue("path");

			Address address = (Address)portletDataContext.getZipEntryAsObject(
				addressElement, addressPath);

			address.setClassPK(importedOrganization.getOrganizationId());

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, address);

			Map<Long, Long> addressIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Address.class);

			address.setAddressId(addressIds.get(address.getPrimaryKey()));

			addresses.add(address);
		}

		UsersAdminUtil.updateAddresses(
			Organization.class.getName(),
			importedOrganization.getOrganizationId(), addresses,
			ListTypeConstants.ORGANIZATION_ADDRESS);
	}

	private Organization _importCountry(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		List<Element> countryElements =
			portletDataContext.getReferenceDataElements(
				organization, Country.class);

		for (Element countryElement : countryElements) {
			String countryPath = countryElement.attributeValue("path");

			Country country = (Country)portletDataContext.getZipEntryAsObject(
				countryPath);

			if (country == null) {
				continue;
			}

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, country);

			country = _countryLocalService.getCountryByA2(
				portletDataContext.getCompanyId(), country.getA2());

			organization.setCountryId(country.getCountryId());
		}

		return organization;
	}

	private void _importEmailAddresses(
			PortletDataContext portletDataContext, Organization organization,
			Organization importedOrganization)
		throws Exception {

		List<Element> emailAddressElements =
			portletDataContext.getReferenceDataElements(
				organization, EmailAddress.class);

		List<EmailAddress> emailAddresses = new ArrayList<>(
			emailAddressElements.size());

		for (Element emailAddressElement : emailAddressElements) {
			String emailAddressPath = emailAddressElement.attributeValue(
				"path");

			EmailAddress emailAddress =
				(EmailAddress)portletDataContext.getZipEntryAsObject(
					emailAddressElement, emailAddressPath);

			emailAddress.setClassPK(importedOrganization.getOrganizationId());

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, emailAddress);

			Map<Long, Long> emailAddressIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					EmailAddress.class);

			long emailAddressId = emailAddressIds.get(
				emailAddress.getPrimaryKey());

			emailAddress.setEmailAddressId(emailAddressId);

			emailAddresses.add(emailAddress);
		}

		UsersAdminUtil.updateEmailAddresses(
			Organization.class.getName(),
			importedOrganization.getOrganizationId(), emailAddresses);
	}

	private Organization _importListType(
			PortletDataContext portletDataContext, Organization organization)
		throws Exception {

		List<Element> listTypeElements =
			portletDataContext.getReferenceDataElements(
				organization, ListType.class);

		for (Element listTypeElement : listTypeElements) {
			String listTypePath = listTypeElement.attributeValue("path");

			ListType listType =
				(ListType)portletDataContext.getZipEntryAsObject(listTypePath);

			if (listType == null) {
				continue;
			}

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, listType);

			listType = _listTypeLocalService.getListType(
				portletDataContext.getCompanyId(), listType.getName(),
				listType.getType());

			organization.setStatusListTypeId(listType.getListTypeId());
		}

		return organization;
	}

	private void _importOrgLabors(
			PortletDataContext portletDataContext, Organization organization,
			Organization importedOrganization)
		throws Exception {

		String path = ExportImportPathUtil.getModelPath(
			organization, OrgLabor.class.getSimpleName());

		List<OrgLabor> orgLabors =
			(List<OrgLabor>)portletDataContext.getZipEntryAsObject(path);

		for (OrgLabor orgLabor : orgLabors) {
			orgLabor.setOrgLaborId(0);
		}

		UsersAdminUtil.updateOrgLabors(
			importedOrganization.getOrganizationId(), orgLabors);
	}

	private void _importPasswordPolicyRel(
			PortletDataContext portletDataContext, Organization organization,
			Organization importedOrganization)
		throws Exception {

		List<Element> passwordPolicyElements =
			portletDataContext.getReferenceDataElements(
				organization, PasswordPolicy.class);

		if (passwordPolicyElements.isEmpty()) {
			return;
		}

		Element passwordPolicyElement = passwordPolicyElements.get(0);

		String passwordPolicyPath = passwordPolicyElement.attributeValue(
			"path");

		PasswordPolicy passwordPolicy =
			(PasswordPolicy)portletDataContext.getZipEntryAsObject(
				passwordPolicyPath);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, passwordPolicy);

		Map<Long, Long> passwordPolicyIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				PasswordPolicy.class);

		long passwordPolicyId = passwordPolicyIds.get(
			passwordPolicy.getPrimaryKey());

		_organizationLocalService.addPasswordPolicyOrganizations(
			passwordPolicyId,
			new long[] {importedOrganization.getOrganizationId()});
	}

	private void _importPhones(
			PortletDataContext portletDataContext, Organization organization,
			Organization importedOrganization)
		throws Exception {

		List<Element> phoneElements =
			portletDataContext.getReferenceDataElements(
				organization, Phone.class);

		List<Phone> phones = new ArrayList<>(phoneElements.size());

		for (Element phoneElement : phoneElements) {
			String phonePath = phoneElement.attributeValue("path");

			Phone phone = (Phone)portletDataContext.getZipEntryAsObject(
				phoneElement, phonePath);

			phone.setClassPK(importedOrganization.getOrganizationId());

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, phone);

			Map<Long, Long> phoneIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Phone.class);

			phone.setPhoneId(phoneIds.get(phone.getPrimaryKey()));

			phones.add(phone);
		}

		UsersAdminUtil.updatePhones(
			Organization.class.getName(),
			importedOrganization.getOrganizationId(), phones);
	}

	private void _importWebsites(
			PortletDataContext portletDataContext, Organization organization,
			Organization importedOrganization)
		throws Exception {

		List<Element> websiteElements =
			portletDataContext.getReferenceDataElements(
				organization, Website.class);

		List<Website> websites = new ArrayList<>(websiteElements.size());

		for (Element websiteElement : websiteElements) {
			String websitePath = websiteElement.attributeValue("path");

			Website website = (Website)portletDataContext.getZipEntryAsObject(
				websiteElement, websitePath);

			website.setClassPK(importedOrganization.getOrganizationId());

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, website);

			Map<Long, Long> websiteIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Website.class);

			website.setWebsiteId(websiteIds.get(website.getPrimaryKey()));

			websites.add(website);
		}

		UsersAdminUtil.updateWebsites(
			Organization.class.getName(),
			importedOrganization.getOrganizationId(), websites);
	}

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference
	private EmailAddressLocalService _emailAddressLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private OrgLaborLocalService _orgLaborLocalService;

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Reference
	private PasswordPolicyRelLocalService _passwordPolicyRelLocalService;

	@Reference
	private PhoneLocalService _phoneLocalService;

	@Reference
	private WebsiteLocalService _websiteLocalService;

}