/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.object.system;

import com.liferay.headless.admin.user.dto.v1_0.Organization;
import com.liferay.headless.admin.user.resource.v1_0.OrganizationResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.OrganizationTable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mateus Santana
 */
@Component(service = SystemObjectDefinitionManager.class)
public class OrganizationSystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	@Override
	public long addBaseModel(User user, Map<String, Object> values)
		throws Exception {

		OrganizationResource organizationResource = _buildOrganizationResource(
			false, user);

		Organization organization = organizationResource.postOrganization(
			_toOrganization(values));

		setExtendedProperties(
			Organization.class.getName(), organization, user, values);

		return GetterUtil.getLong(organization.getId());
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return _organizationLocalService.deleteOrganization(
			(com.liferay.portal.kernel.model.Organization)baseModel);
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _organizationLocalService.
			fetchOrganizationByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return _organizationLocalService.getOrganizationByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationLocalService.getOrganization(primaryKey);

		return organization.getExternalReferenceCode();
	}

	@Override
	public String getExternalReferenceCode() {
		return "L_ORGANIZATION";
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		return new JaxRsApplicationDescriptor(
			"Liferay.Headless.Admin.User", "headless-admin-user",
			"organizations", "v1.0");
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "organization"
		).put(
			"pluralLabel", "organizations"
		).build();
	}

	@Override
	public Class<?> getModelClass() {
		return com.liferay.portal.kernel.model.Organization.class;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return Arrays.asList(
			new LongIntegerObjectFieldBuilder(
			).labelMap(
				createLabelMap("parentOrganizationId")
			).name(
				"parentOrganizationId"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("comments")
			).name(
				"comment"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("name")
			).name(
				"name"
			).required(
				true
			).system(
				true
			).build());
	}

	@Override
	public BaseModel<?> getOrAddEmptyBaseModel(
			String externalReferenceCode, User user)
		throws PortalException {

		return _organizationService.getOrAddEmptyOrganization(
			externalReferenceCode, StringPool.BLANK);
	}

	@Override
	public Page<?> getPage(
			User user, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		OrganizationResource organizationResource = _buildOrganizationResource(
			true, user);

		return organizationResource.getOrganizationsPage(
			null, search, filter, pagination, sorts);
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return OrganizationTable.INSTANCE.organizationId;
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return OrganizationTable.INSTANCE;
	}

	@Override
	public String getTitleObjectFieldName() {
		return "name";
	}

	@Override
	public int getVersion() {
		return 3;
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	private OrganizationResource _buildOrganizationResource(
		boolean checkPermissions, User user) {

		OrganizationResource.Builder builder =
			_organizationResourceFactory.create();

		return builder.checkPermissions(
			checkPermissions
		).preferredLocale(
			user.getLocale()
		).user(
			user
		).build();
	}

	private Organization _toOrganization(Map<String, Object> values) {
		return new Organization() {
			{
				setComment(() -> GetterUtil.getString(values.get("comment")));
				setName(() -> GetterUtil.getString(values.get("name")));
			}
		};
	}

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private OrganizationResource.Factory _organizationResourceFactory;

	@Reference
	private OrganizationService _organizationService;

}