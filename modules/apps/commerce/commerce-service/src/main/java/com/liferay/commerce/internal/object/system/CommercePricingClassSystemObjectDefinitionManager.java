/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.system;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassTable;
import com.liferay.commerce.pricing.service.CommercePricingClassLocalService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductGroup;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductGroupResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
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
 * @author Gleice Lisbino
 */
@Component(enabled = true, service = SystemObjectDefinitionManager.class)
public class CommercePricingClassSystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	@Override
	public long addBaseModel(
			boolean checkPermissions, User user, Map<String, Object> values)
		throws Exception {

		ProductGroupResource productGroupResource = _buildProductGroupResource(
			checkPermissions, user);

		ProductGroup productGroup = productGroupResource.postProductGroup(
			_toProductGroup(values));

		setExtendedProperties(
			ProductGroup.class.getName(), productGroup, user, values);

		return productGroup.getId();
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return _commercePricingClassLocalService.deleteCommercePricingClass(
			(CommercePricingClass)baseModel);
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _commercePricingClassLocalService.
			fetchCommercePricingClassByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return _commercePricingClassLocalService.
			getCommercePricingClassByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException {

		CommercePricingClass commercePricingClass =
			_commercePricingClassLocalService.getCommercePricingClass(
				primaryKey);

		return commercePricingClass.getExternalReferenceCode();
	}

	@Override
	public String getExternalReferenceCode() {
		return "L_COMMERCE_PRODUCT_GROUP";
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		return new JaxRsApplicationDescriptor(
			"Liferay.Headless.Commerce.Admin.Catalog",
			"headless-commerce-admin-catalog", "product-groups", "v1.0");
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "commerce-product-group"
		).put(
			"pluralLabel", "commerce-product-groups"
		).build();
	}

	@Override
	public Class<?> getModelClass() {
		return CommercePricingClass.class;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return Arrays.asList(
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("description")
			).name(
				"description"
			).system(
				true
			).build(),
			new IntegerObjectFieldBuilder(
			).labelMap(
				createLabelMap("number-of-products")
			).name(
				"productsCount"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("title")
			).name(
				"title"
			).required(
				true
			).system(
				true
			).build());
	}

	@Override
	public Page<?> getPage(
			User user, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		ProductGroupResource productGroupResource = _buildProductGroupResource(
			true, user);

		return productGroupResource.getProductGroupsPage(
			search, filter, pagination, sorts);
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommercePricingClassTable.INSTANCE.commercePricingClassId;
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return CommercePricingClassTable.INSTANCE;
	}

	@Override
	public String getTitleObjectFieldName() {
		return "title";
	}

	@Override
	public int getVersion() {
		return 3;
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		ProductGroupResource productGroupResource = _buildProductGroupResource(
			false, user);

		productGroupResource.patchProductGroup(
			primaryKey, _toProductGroup(values));

		setExtendedProperties(
			ProductGroup.class.getName(), JSONUtil.put("id", primaryKey), user,
			values);
	}

	private ProductGroupResource _buildProductGroupResource(
		boolean checkPermissions, User user) {

		ProductGroupResource.Builder builder =
			_productGroupResourceFactory.create();

		return builder.checkPermissions(
			checkPermissions
		).preferredLocale(
			user.getLocale()
		).user(
			user
		).build();
	}

	private ProductGroup _toProductGroup(Map<String, Object> values) {
		return new ProductGroup() {
			{
				setDescription(() -> getLanguageIdMap("description", values));
				setExternalReferenceCode(
					() -> GetterUtil.getString(
						values.get("externalReferenceCode")));
				setProductsCount(
					() -> GetterUtil.getInteger(values.get("productsCount")));
				setTitle(() -> getLanguageIdMap("title", values));
			}
		};
	}

	@Reference
	private CommercePricingClassLocalService _commercePricingClassLocalService;

	@Reference
	private ProductGroupResource.Factory _productGroupResourceFactory;

}