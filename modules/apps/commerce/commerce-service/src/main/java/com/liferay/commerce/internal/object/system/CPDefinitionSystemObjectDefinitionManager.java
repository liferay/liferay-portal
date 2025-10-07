/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.system;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLocalizationTable;
import com.liferay.commerce.product.model.CPDefinitionTable;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
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
 * @author José Abelenda
 */
@Component(enabled = true, service = SystemObjectDefinitionManager.class)
public class CPDefinitionSystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	@Override
	public long addBaseModel(User user, Map<String, Object> values)
		throws Exception {

		ProductResource productResource = _buildProductResource(false, user);

		Product product = productResource.postProduct(_toProduct(values));

		setExtendedProperties(Product.class.getName(), product, user, values);

		return product.getProductId();
	}

	@Override
	public void checkModelResourcePermission(
			long objectDefinitionId, PermissionChecker permissionChecker,
			long primaryKey, String actionId)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.fetchCPDefinition(
			primaryKey);

		if (cpDefinition == null) {
			cpDefinition =
				_cpDefinitionLocalService.getCPDefinitionByCProductId(
					primaryKey);
		}

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
				cpDefinition.getGroupId());

		_commerceCatalogModelResourcePermission.check(
			permissionChecker, commerceCatalog, actionId);
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return _cpDefinitionLocalService.deleteCPDefinition(
			(CPDefinition)baseModel);
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _cProductLocalService.fetchCProductByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CProduct cProduct =
			_cProductLocalService.getCProductByExternalReferenceCode(
				externalReferenceCode, companyId);

		return _cpDefinitionLocalService.getCProductCPDefinition(
			cProduct.getCProductId(), cProduct.getLatestVersion());
	}

	@Override
	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException {

		CProduct cProduct = _cProductLocalService.fetchCProduct(primaryKey);

		if (cProduct == null) {
			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(primaryKey);

			cProduct = cpDefinition.getCProduct();
		}

		return cProduct.getExternalReferenceCode();
	}

	@Override
	public String getExternalReferenceCode() {
		return "L_COMMERCE_PRODUCT_DEFINITION";
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		return new JaxRsApplicationDescriptor(
			"Liferay.Headless.Commerce.Admin.Catalog",
			"headless-commerce-admin-catalog", "products", "v1.0");
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "cp-definition"
		).put(
			"pluralLabel", "cp-definitions"
		).build();
	}

	@Override
	public Table getLocalizationTable() {
		return CPDefinitionLocalizationTable.INSTANCE;
	}

	@Override
	public Class<?> getModelClass() {
		return CPDefinition.class;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return Arrays.asList(
			new BooleanObjectFieldBuilder(
			).labelMap(
				createLabelMap("active")
			).name(
				"active"
			).required(
				true
			).system(
				true
			).build(),
			new LongIntegerObjectFieldBuilder(
			).labelMap(
				createLabelMap("catalog-id")
			).name(
				"catalogId"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("description")
			).localized(
				true
			).name(
				"description"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("name")
			).localized(
				true
			).name(
				"name"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).dbColumnName(
				"CProductId"
			).labelMap(
				createLabelMap("product-id")
			).name(
				"productId"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("product-type")
			).name(
				"productType"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("short-description")
			).localized(
				true
			).name(
				"shortDescription"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("sku")
			).name(
				"skuFormatted"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("thumbnail")
			).name(
				"thumbnail"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("uuid")
			).name(
				"uuid"
			).system(
				true
			).build());
	}

	@Override
	public Page<?> getPage(
			User user, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		ProductResource productResource = _buildProductResource(true, user);

		return productResource.getProductsPage(
			search, filter, pagination, sorts);
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CPDefinitionTable.INSTANCE.CProductId;
	}

	@Override
	public String getRESTDTOIdPropertyName() {
		return "productId";
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return CPDefinitionTable.INSTANCE;
	}

	@Override
	public String getTitleObjectFieldName() {
		return "name";
	}

	@Override
	public Map<String, Object> getVariables(
		String contentType, ObjectDefinition objectDefinition,
		boolean oldValues, JSONObject payloadJSONObject) {

		Map<String, Object> variables = super.getVariables(
			contentType, objectDefinition, oldValues, payloadJSONObject);

		if (variables.containsKey("CProductId")) {
			variables.put("productId", variables.get("CProductId"));
		}

		return variables;
	}

	@Override
	public int getVersion() {
		return 3;
	}

	@Override
	public boolean isEnableLocalization() {
		return true;
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		ProductResource productResource = _buildProductResource(false, user);

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			primaryKey);

		productResource.patchProduct(
			cpDefinition.getCProductId(), _toProduct(values));

		setExtendedProperties(
			Product.class.getName(), JSONUtil.put("id", primaryKey), user,
			values);
	}

	private ProductResource _buildProductResource(
		boolean checkPermissions, User user) {

		ProductResource.Builder builder = _productResourceFactory.create();

		return builder.checkPermissions(
			checkPermissions
		).preferredLocale(
			user.getLocale()
		).user(
			user
		).build();
	}

	private Product _toProduct(Map<String, Object> values) {
		return new Product() {
			{
				setActive(() -> GetterUtil.getBoolean(values.get("active")));
				setCatalogId(() -> GetterUtil.getLong(values.get("catalogId")));
				setDescription(() -> getLanguageIdMap("description", values));
				setExternalReferenceCode(
					() -> GetterUtil.getString(
						values.get("externalReferenceCode")));
				setName(() -> getLanguageIdMap("name", values));
				setProductId(() -> GetterUtil.getLong(values.get("productId")));
				setProductType(
					() -> GetterUtil.getString(values.get("productType")));
				setShortDescription(
					() -> getLanguageIdMap("shortDescription", values));
				setSkuFormatted(
					() -> GetterUtil.getString(values.get("skuFormatted")));
				setThumbnail(
					() -> GetterUtil.getString(values.get("thumbnail")));
			}
		};
	}

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private ProductResource.Factory _productResourceFactory;

}