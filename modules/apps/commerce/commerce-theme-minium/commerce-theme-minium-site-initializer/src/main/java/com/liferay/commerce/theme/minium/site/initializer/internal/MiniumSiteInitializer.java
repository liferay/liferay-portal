/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.theme.minium.site.initializer.internal;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.settings.AccountEntryGroupSettings;
import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.initializer.util.AssetCategoriesImporter;
import com.liferay.commerce.initializer.util.BlogsImporter;
import com.liferay.commerce.initializer.util.CPDefinitionsImporter;
import com.liferay.commerce.initializer.util.CPOptionCategoriesImporter;
import com.liferay.commerce.initializer.util.CPOptionsImporter;
import com.liferay.commerce.initializer.util.CPSpecificationOptionsImporter;
import com.liferay.commerce.initializer.util.CommerceAccountsImporter;
import com.liferay.commerce.initializer.util.CommerceDiscountsImporter;
import com.liferay.commerce.initializer.util.CommerceInventoryWarehousesImporter;
import com.liferay.commerce.initializer.util.CommercePriceEntriesImporter;
import com.liferay.commerce.initializer.util.CommercePriceListsImporter;
import com.liferay.commerce.initializer.util.CommerceUsersImporter;
import com.liferay.commerce.initializer.util.DDMFormImporter;
import com.liferay.commerce.initializer.util.DLImporter;
import com.liferay.commerce.initializer.util.KBArticleImporter;
import com.liferay.commerce.initializer.util.OrganizationImporter;
import com.liferay.commerce.initializer.util.PortletSettingsImporter;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.media.CommerceCatalogDefaultImage;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.importer.CPFileImporter;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.commerce.theme.minium.SiteInitializerDependencyResolver;
import com.liferay.commerce.theme.minium.SiteInitializerDependencyResolverThreadLocal;
import com.liferay.commerce.util.AccountEntryAllowedTypesUtil;
import com.liferay.commerce.util.CommerceAccountRoleHelper;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.ThemeSetting;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.site.exception.InitializationException;
import com.liferay.site.initializer.SiteInitializer;

import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.InputStream;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "site.initializer.key=" + MiniumSiteInitializer.KEY,
	service = SiteInitializer.class
)
public class MiniumSiteInitializer implements SiteInitializer {

	public static final String KEY = "minium-initializer";

	@Override
	public String getDescription(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, "minium-description");
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, "minium");
	}

	@Override
	public String getThumbnailSrc() {
		return _servletContext.getContextPath() + "/images/thumbnail.png";
	}

	public void init() {
		_cpDefinitions = new HashMap<>();
	}

	@Override
	public void initialize(long groupId) throws InitializationException {
		try {
			SiteInitializerDependencyResolver
				siteInitializerDependencyResolver =
					SiteInitializerDependencyResolverThreadLocal.
						getSiteInitializerDependencyResolver();

			if (siteInitializerDependencyResolver != null) {
				_siteInitializerDependencyResolver =
					siteInitializerDependencyResolver;
			}
			else {
				_siteInitializerDependencyResolver =
					_defaultSiteInitializerDependencyResolver;
			}

			ServiceContext serviceContext = _getServiceContext(groupId);

			_cpFileImporter.updateLookAndFeel(
				_MINIUM_THEME_ID, true, serviceContext);
			_cpFileImporter.updateLookAndFeel(
				_MINIUM_THEME_ID, false, serviceContext);

			_updateLogo(serviceContext);

			CommerceCatalog commerceCatalog = _createCatalog(serviceContext);

			long catalogGroupId = commerceCatalog.getGroupId();

			CommerceChannel commerceChannel = _createChannel(
				commerceCatalog, serviceContext);

			_createRoles(
				serviceContext, commerceChannel.getCommerceChannelId());

			_configureB2BSite(commerceChannel.getGroup(), serviceContext);

			_createLayouts(serviceContext);

			_importAssetCategories(serviceContext);

			_importBlogsEntries(serviceContext);

			_importCommerceDiscounts(serviceContext);

			_importCPOptionCategories(catalogGroupId, serviceContext);

			_importCPSpecificationOptions(catalogGroupId, serviceContext);

			List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
				_importCommerceInventoryWarehouses(serviceContext);

			_importCPOptions(catalogGroupId, serviceContext);

			_importCommerceOrganizations(serviceContext);

			_importCommerceAccounts(serviceContext);

			List<CPDefinition> cpDefinitions = _importCPDefinitions(
				catalogGroupId, commerceChannel.getCommerceChannelId(),
				commerceInventoryWarehouses, serviceContext);

			_importRelatedProducts(cpDefinitions, serviceContext);

			_importCommercePriceLists(catalogGroupId, serviceContext);

			_importCommercePriceEntries(catalogGroupId, serviceContext);

			_importBaseCommercePriceListEntries(commerceCatalog, cpDefinitions);

			_importCommerceUsers(serviceContext);

			_importDDMForms(serviceContext);

			_importDLFileEntries(serviceContext);

			_importJournalArticles(serviceContext);

			_importKBArticles(serviceContext);

			_importThemePortletSettings(serviceContext);

			_importPortletSettings(serviceContext);

			_setCommerceShippingMethod(
				commerceChannel.getGroupId(), "fixed", serviceContext);

			int catalogCPDefinitionsCount =
				_cpDefinitionLocalService.getCPDefinitionsCount(
					catalogGroupId, WorkflowConstants.STATUS_ANY);

			if (catalogCPDefinitionsCount > 0) {
				_setDefaultCatalogImage(catalogGroupId, serviceContext);
			}
			else {
				_commerceCatalogLocalService.deleteCommerceCatalog(
					commerceCatalog);
			}

			_setThemeSettings(serviceContext);
		}
		catch (InitializationException initializationException) {
			throw initializationException;
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new InitializationException(exception);
		}
	}

	@Override
	public boolean isActive(long companyId) {
		if (!_searchCapabilities.isCommerceSupported()) {
			return false;
		}

		Theme theme = _themeLocalService.fetchTheme(
			companyId, _MINIUM_THEME_ID);

		if (theme == null) {
			if (_log.isInfoEnabled()) {
				_log.info(_MINIUM_THEME_ID + " is not registered");
			}

			return false;
		}

		return true;
	}

	@Activate
	protected void activate() {
		init();
	}

	@Deactivate
	protected void deactivate() {
		_cpDefinitions = null;
	}

	private void _configureB2BSite(Group group, ServiceContext serviceContext)
		throws Exception {

		group.setType(GroupConstants.TYPE_SITE_PRIVATE);
		group.setManualMembership(true);
		group.setMembershipRestriction(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION);

		group = _groupLocalService.updateGroup(group);

		_commerceCurrencyLocalService.importDefaultValues(true, serviceContext);
		_cpMeasurementUnitLocalService.importDefaultValues(serviceContext);

		_commerceAccountRoleHelper.checkCommerceAccountRoles(serviceContext);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				group.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue(
			"commerceSiteType",
			String.valueOf(CommerceChannelConstants.SITE_TYPE_B2B));

		modifiableSettings.store();

		_accountEntryGroupSettings.setAllowedTypes(
			serviceContext.getScopeGroupId(),
			_getAllowedTypes(group.getGroupId()));
	}

	private CommerceCatalog _createCatalog(ServiceContext serviceContext)
		throws Exception {

		Group group = serviceContext.getScopeGroup();

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
				serviceContext.getCompanyId());

		return _commerceCatalogLocalService.addCommerceCatalog(
			StringPool.BLANK, group.getName(serviceContext.getLanguageId()),
			commerceCurrency.getCode(), serviceContext.getLanguageId(),
			serviceContext);
	}

	private CommerceChannel _createChannel(
			CommerceCatalog commerceCatalog, ServiceContext serviceContext)
		throws Exception {

		Group group = serviceContext.getScopeGroup();

		return _commerceChannelLocalService.addCommerceChannel(
			StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			group.getGroupId(),
			group.getName(serviceContext.getLanguageId()) + " Portal",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			commerceCatalog.getCommerceCurrencyCode(), serviceContext);
	}

	private void _createLayouts(ServiceContext serviceContext)
		throws Exception {

		SiteInitializerDependencyResolver siteInitializerDependencyResolver =
			SiteInitializerDependencyResolverThreadLocal.
				getSiteInitializerDependencyResolver();

		if (siteInitializerDependencyResolver != null) {
			_siteInitializerDependencyResolver =
				siteInitializerDependencyResolver;
		}
		else {
			_siteInitializerDependencyResolver =
				_defaultSiteInitializerDependencyResolver;
		}

		_cpFileImporter.cleanLayouts(serviceContext);

		_cpFileImporter.createLayouts(
			_jsonFactory.createJSONArray(
				_siteInitializerDependencyResolver.getJSON("layouts.json")),
			_siteInitializerDependencyResolver.getImageClassLoader(),
			_siteInitializerDependencyResolver.getImageDependencyPath(),
			serviceContext);
	}

	private void _createRoles(
			ServiceContext serviceContext, long commerceChannelId)
		throws Exception {

		_cpFileImporter.createRoles(
			_getJSONArray("roles.json"), serviceContext);

		_updateUserRole(serviceContext);
		_updateOperationManagerRole(serviceContext, commerceChannelId);
	}

	private String[] _getAllowedTypes(long commerceChannelGroupId)
		throws Exception {

		CommerceAccountGroupServiceConfiguration
			commerceAccountGroupServiceConfiguration =
				_configurationProvider.getConfiguration(
					CommerceAccountGroupServiceConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceChannelGroupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		return AccountEntryAllowedTypesUtil.getAllowedTypes(
			commerceAccountGroupServiceConfiguration.commerceSiteType());
	}

	private CPDefinition _getCPDefinitionByName(String name) {
		return _cpDefinitions.get(name);
	}

	private long[] _getCProductIds(JSONArray jsonArray) {
		List<Long> cProductIdsList = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			CPDefinition cpDefinitionEntry = _getCPDefinitionByName(
				jsonArray.getString(i));

			cProductIdsList.add(cpDefinitionEntry.getCProductId());
		}

		return ArrayUtil.toLongArray(cProductIdsList);
	}

	private JSONArray _getJSONArray(String name) throws Exception {
		return _jsonFactory.createJSONArray(
			_siteInitializerDependencyResolver.getJSON(name));
	}

	private JSONObject _getJSONObject(String name) throws Exception {
		return _jsonFactory.createJSONObject(
			_siteInitializerDependencyResolver.getJSON(name));
	}

	private ServiceContext _getServiceContext(long groupId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Group group = _groupLocalService.getGroup(groupId);

		serviceContext.setCompanyId(group.getCompanyId());

		serviceContext.setLanguageId(
			_language.getLanguageId(LocaleUtil.getSiteDefault()));

		serviceContext.setScopeGroupId(groupId);

		User user = _userLocalService.getUser(PrincipalThreadLocal.getUserId());

		serviceContext.setTimeZone(user.getTimeZone());
		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}

	private void _importAssetCategories(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing asset categories...");
		}

		Group group = serviceContext.getScopeGroup();

		Company company = _companyLocalService.getCompany(
			serviceContext.getCompanyId());

		_assetCategoriesImporter.importAssetCategories(
			_getJSONArray("categories.json"),
			group.getName(serviceContext.getLocale()),
			_siteInitializerDependencyResolver.getImageClassLoader(),
			_siteInitializerDependencyResolver.getImageDependencyPath(),
			company.getGroupId(), serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("Asset categories successfully imported");
		}
	}

	private void _importBaseCommercePriceListEntries(
			CommerceCatalog commerceCatalog, List<CPDefinition> cpDefinitions)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing base commerce price list entries...");
		}

		_commercePriceEntriesImporter.importBaseCommercePriceListEntries(
			commerceCatalog, cpDefinitions,
			CommercePriceListConstants.TYPE_PRICE_LIST);
		_commercePriceEntriesImporter.importBaseCommercePriceListEntries(
			commerceCatalog, cpDefinitions,
			CommercePriceListConstants.TYPE_PROMOTION);

		if (_log.isInfoEnabled()) {
			_log.info("Base commerce price list entries successfully imported");
		}
	}

	private void _importBlogsEntries(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing Blogs Entries...");
		}

		_blogsImporter.importBlogsEntries(
			_getJSONArray("blogs.json"),
			_siteInitializerDependencyResolver.getImageClassLoader(),
			_siteInitializerDependencyResolver.getImageDependencyPath(),
			serviceContext.getScopeGroupId(), serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("Blogs Entries successfully imported");
		}
	}

	private void _importCommerceAccounts(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing Commerce Accounts...");
		}

		_commerceAccountsImporter.importCommerceAccounts(
			_getJSONArray("accounts.json"),
			_siteInitializerDependencyResolver.getImageClassLoader(),
			_siteInitializerDependencyResolver.getDependenciesPath(),
			serviceContext.getScopeGroupId(), serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("Commerce Accounts successfully imported");
		}
	}

	private void _importCommerceDiscounts(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing Commerce Discounts...");
		}

		_commerceDiscountsImporter.importCommerceDiscounts(
			_getJSONArray("discounts.json"), serviceContext.getScopeGroupId(),
			serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("Commerce Discounts successfully imported");
		}
	}

	private List<CommerceInventoryWarehouse> _importCommerceInventoryWarehouses(
			ServiceContext serviceContext)
		throws Exception {

		return _commerceInventoryWarehousesImporter.
			importCommerceInventoryWarehouses(
				_getJSONArray("warehouses.json"),
				serviceContext.getScopeGroupId(), serviceContext.getUserId());
	}

	private void _importCommerceOrganizations(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing organizations...");
		}

		_organizationImporter.importOrganizations(
			_getJSONArray("organizations.json"),
			serviceContext.getScopeGroupId(), serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("Organizations successfully imported");
		}
	}

	private void _importCommercePriceEntries(
			long catalogGroupId, ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing commerce price entries...");
		}

		JSONArray jsonArray = _getJSONArray("price-entries.json");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String sku = jsonObject.getString("sku");

			String externalReferenceCode =
				sku + _siteInitializerDependencyResolver.getKey();

			jsonObject.put("externalReferenceCode", externalReferenceCode);
		}

		_commercePriceEntriesImporter.importCommercePriceEntries(
			jsonArray, catalogGroupId, serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("Commerce price entries successfully imported");
		}
	}

	private void _importCommercePriceLists(
			long catalogGroupId, ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing commerce price lists...");
		}

		_commercePriceListsImporter.importCommercePriceLists(
			catalogGroupId, _getJSONArray("price-lists.json"),
			serviceContext.getScopeGroupId(), serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("Commerce price lists successfully imported");
		}
	}

	private void _importCommerceUsers(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing Commerce Users...");
		}

		_commerceUsersImporter.importCommerceUsers(
			_getJSONArray("users.json"),
			_siteInitializerDependencyResolver.getImageClassLoader(),
			_siteInitializerDependencyResolver.getImageDependencyPath(),
			serviceContext.getScopeGroupId(), serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("Commerce Users successfully imported");
		}
	}

	private List<CPDefinition> _importCPDefinitions(
			long catalogGroupId, long commerceChannelId,
			List<CommerceInventoryWarehouse> commerceInventoryWarehouses,
			ServiceContext serviceContext)
		throws Exception {

		Group group = serviceContext.getScopeGroup();

		JSONArray jsonArray = _getJSONArray("products.json");

		if (_siteInitializerDependencyResolver != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				String externalReferenceCode = jsonObject.getString(
					"externalReferenceCode");

				String newExternalReferenceCode =
					externalReferenceCode +
						_siteInitializerDependencyResolver.getKey();

				jsonObject.put(
					"externalReferenceCode", newExternalReferenceCode);

				JSONArray skusJSONArray = jsonObject.getJSONArray("skus");

				if (skusJSONArray != null) {
					for (int j = 0; j < skusJSONArray.length(); j++) {
						JSONObject skuJSONObject = skusJSONArray.getJSONObject(
							j);

						String skuExternalReferenceCode =
							skuJSONObject.getString("externalReferenceCode");

						String newSkuExternalReferenceCode =
							skuExternalReferenceCode +
								_siteInitializerDependencyResolver.getKey();

						skuJSONObject.put(
							"externalReferenceCode",
							newSkuExternalReferenceCode);
					}
				}
			}
		}

		long[] commerceInventoryWarehouseIds = ListUtil.toLongArray(
			commerceInventoryWarehouses,
			CommerceInventoryWarehouse.
				COMMERCE_INVENTORY_WAREHOUSE_ID_ACCESSOR);

		return _cpDefinitionsImporter.importCPDefinitions(
			jsonArray, group.getName(serviceContext.getLocale()),
			catalogGroupId, commerceChannelId, commerceInventoryWarehouseIds,
			_siteInitializerDependencyResolver.getImageClassLoader(),
			_siteInitializerDependencyResolver.getImageDependencyPath(),
			serviceContext.getScopeGroupId(), serviceContext.getUserId());
	}

	private void _importCPOptionCategories(
			long catalogGroupId, ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing commerce product option categories...");
		}

		_cpOptionCategoriesImporter.importCPOptionCategories(
			_getJSONArray("option-categories.json"), catalogGroupId,
			serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info(
				"Commerce product option categories successfully imported");
		}
	}

	private List<CPOption> _importCPOptions(
			long catalogGroupId, ServiceContext serviceContext)
		throws Exception {

		return _cpOptionsImporter.importCPOptions(
			_getJSONArray("options.json"), catalogGroupId,
			serviceContext.getUserId());
	}

	private void _importCPSpecificationOptions(
			long catalogGroupId, ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing commerce product specification options...");
		}

		_cpSpecificationOptionsImporter.importCPSpecificationOptions(
			_getJSONArray("specification-options.json"), catalogGroupId,
			serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info(
				"Commerce product specification options successfully imported");
		}
	}

	private void _importDDMForms(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing DDM Forms...");
		}

		_ddmFormImporter.importDDMForms(
			_getJSONArray("forms.json"), serviceContext.getScopeGroupId(),
			serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("DDM Forms successfully imported");
		}
	}

	private void _importDLFileEntries(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing DL File Entries...");
		}

		_dlImporter.importDocuments(
			_getJSONArray("dl-file-entries.json"),
			_siteInitializerDependencyResolver.getDocumentsClassLoader(),
			_siteInitializerDependencyResolver.getDocumentsDependencyPath(),
			serviceContext.getScopeGroupId(), serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("DL File Entries successfully imported");
		}
	}

	private void _importJournalArticles(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing Journal Articles...");
		}

		_cpFileImporter.createJournalArticles(
			_getJSONArray("journal-articles.json"),
			_siteInitializerDependencyResolver.getDocumentsClassLoader(),
			_siteInitializerDependencyResolver.getDependenciesPath() +
				"journal_articles/",
			serviceContext);

		if (_log.isInfoEnabled()) {
			_log.info("Journal Articles successfully imported");
		}
	}

	private void _importKBArticles(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing KB Articles...");
		}

		_kbArticleImporter.importKBArticles(
			_getJSONArray("kb-articles.json"), serviceContext.getScopeGroupId(),
			serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("KB Articles successfully imported");
		}
	}

	private void _importPortletSettings(ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing portlet settings...");
		}

		Company company = _companyLocalService.getCompany(
			serviceContext.getCompanyId());

		_portletSettingsImporter.importPortletSettings(
			_getJSONArray("portlet-settings.json"),
			_siteInitializerDependencyResolver.getDisplayTemplatesClassLoader(),
			_siteInitializerDependencyResolver.
				getDisplayTemplatesDependencyPath(),
			serviceContext.getScopeGroupId(), company.getGroupId(),
			serviceContext.getUserId());

		if (_log.isInfoEnabled()) {
			_log.info("Portlet settings successfully imported");
		}
	}

	private void _importRelatedProducts(
			JSONArray jsonArray, ServiceContext serviceContext)
		throws Exception {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject productJSONObject = jsonArray.getJSONObject(i);

			JSONArray relatedProductsJSONArray = productJSONObject.getJSONArray(
				"RelatedProducts");

			if (relatedProductsJSONArray == null) {
				continue;
			}

			String name = productJSONObject.getString("Name");

			CPDefinition cpDefinition = _getCPDefinitionByName(name);

			_cpDefinitionLinkLocalService.updateCPDefinitionLinkCProductIds(
				cpDefinition.getCPDefinitionId(),
				_getCProductIds(relatedProductsJSONArray), "related",
				serviceContext);
		}
	}

	private void _importRelatedProducts(
			List<CPDefinition> cpDefinitions, ServiceContext serviceContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Importing related products...");
		}

		for (CPDefinition cpDefinition : cpDefinitions) {
			_cpDefinitions.put(
				cpDefinition.getName(serviceContext.getLanguageId()),
				cpDefinition);
		}

		_importRelatedProducts(_getJSONArray("products.json"), serviceContext);

		if (_log.isInfoEnabled()) {
			_log.info("Related products successfully imported");
		}
	}

	private void _importThemePortletSettings(ServiceContext serviceContext)
		throws Exception {

		Company company = _companyLocalService.getCompany(
			serviceContext.getCompanyId());

		_portletSettingsImporter.importPortletSettings(
			_getJSONArray("theme-portlet-settings.json"),
			_siteInitializerDependencyResolver.getDisplayTemplatesClassLoader(),
			_siteInitializerDependencyResolver.
				getDisplayTemplatesDependencyPath(),
			serviceContext.getScopeGroupId(), company.getGroupId(),
			serviceContext.getUserId());
	}

	private void _setCommerceShippingMethod(
			long groupId, String shippingMethod, ServiceContext serviceContext)
		throws PortalException {

		Locale locale = serviceContext.getLocale();

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				shippingMethod);

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.addCommerceShippingMethod(
				serviceContext.getUserId(), groupId,
				HashMapBuilder.put(
					locale, commerceShippingEngine.getName(locale)
				).build(),
				HashMapBuilder.put(
					locale, commerceShippingEngine.getDescription(locale)
				).build(),
				true, shippingMethod, null, 0, StringPool.BLANK);

		_setCommerceShippingOption(
			commerceShippingMethod, "Standard Delivery", StringPool.BLANK,
			BigDecimal.valueOf(15), serviceContext);

		_setCommerceShippingOption(
			commerceShippingMethod, "Expedited Delivery", StringPool.BLANK,
			BigDecimal.valueOf(25), serviceContext);
	}

	private void _setCommerceShippingOption(
			CommerceShippingMethod commerceShippingMethod, String name,
			String description, BigDecimal price, ServiceContext serviceContext)
		throws PortalException {

		_commerceShippingFixedOptionLocalService.addCommerceShippingFixedOption(
			serviceContext.getUserId(), commerceShippingMethod.getGroupId(),
			commerceShippingMethod.getCommerceShippingMethodId(), price,
			HashMapBuilder.put(
				serviceContext.getLocale(), description
			).build(),
			null,
			HashMapBuilder.put(
				serviceContext.getLocale(), name
			).build(),
			0);
	}

	private void _setDefaultCatalogImage(
			long catalogGroupId, ServiceContext serviceContext)
		throws Exception {

		ClassLoader classLoader =
			_siteInitializerDependencyResolver.getImageClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			_siteInitializerDependencyResolver.getImageDependencyPath() +
				"Minium_ProductImage_Default.png");

		File file = null;

		try {
			file = _file.createTempFile(inputStream);

			String mimeType = MimeTypesUtil.getContentType(file);

			FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
				catalogGroupId, serviceContext.getUserId(),
				MiniumSiteInitializer.class.getName(), file.getName(), file,
				mimeType);

			_commerceCatalogDefaultImage.updateDefaultCatalogFileEntryId(
				catalogGroupId, fileEntry.getFileEntryId());
		}
		finally {
			if (file != null) {
				_file.delete(file);
			}
		}
	}

	private void _setThemeSettings(ServiceContext serviceContext)
		throws Exception {

		JSONObject themeSettingsJSONObject = _getJSONObject(
			"theme-settings.json");

		Iterator<String> iterator = themeSettingsJSONObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			String value = themeSettingsJSONObject.getString(key);

			_updateThemeSetting(key, value, serviceContext);
		}
	}

	private void _updateLogo(ServiceContext serviceContext) throws Exception {
		ClassLoader classLoader =
			_siteInitializerDependencyResolver.getImageClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			_siteInitializerDependencyResolver.getImageDependencyPath() +
				"minium_logo.png");

		File file = _file.createTempFile(inputStream);

		_cpFileImporter.updateLogo(file, true, true, serviceContext);
		_cpFileImporter.updateLogo(file, false, true, serviceContext);
	}

	private void _updateOperationManagerRole(
			ServiceContext serviceContext, long commerceChannelId)
		throws Exception {

		ModelPermissions modelPermissions = ModelPermissionsFactory.create(
			HashMapBuilder.put(
				"Operations Manager", new String[] {"VIEW"}
			).build(),
			null);

		_resourcePermissionLocalService.addModelResourcePermissions(
			serviceContext.getCompanyId(), serviceContext.getScopeGroupId(),
			serviceContext.getUserId(), CommerceChannel.class.getName(),
			String.valueOf(commerceChannelId), modelPermissions);
	}

	private void _updateThemeSetting(
		String key, String value, ServiceContext serviceContext) {

		Theme theme = _themeLocalService.fetchTheme(
			serviceContext.getCompanyId(), _MINIUM_THEME_ID);

		if (theme == null) {
			return;
		}

		Map<String, ThemeSetting> configurableSettings =
			theme.getConfigurableSettings();

		ThemeSetting themeSetting = configurableSettings.get(key);

		themeSetting.setValue(value);
	}

	private void _updateUserRole(ServiceContext serviceContext)
		throws Exception {

		Role role = _roleLocalService.fetchRole(
			serviceContext.getCompanyId(), "User");

		_resourcePermissionLocalService.addResourcePermission(
			serviceContext.getCompanyId(), "com.liferay.commerce.product",
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), "VIEW_PRICE");
	}

	private static final String _MINIUM_THEME_ID = "minium_WAR_miniumtheme";

	private static final Log _log = LogFactoryUtil.getLog(
		MiniumSiteInitializer.class);

	@Reference
	private AccountEntryGroupSettings _accountEntryGroupSettings;

	@Reference
	private AssetCategoriesImporter _assetCategoriesImporter;

	@Reference
	private BlogsImporter _blogsImporter;

	@Reference
	private CommerceAccountRoleHelper _commerceAccountRoleHelper;

	@Reference
	private CommerceAccountsImporter _commerceAccountsImporter;

	@Reference
	private CommerceCatalogDefaultImage _commerceCatalogDefaultImage;

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceDiscountsImporter _commerceDiscountsImporter;

	@Reference
	private CommerceInventoryWarehousesImporter
		_commerceInventoryWarehousesImporter;

	@Reference
	private CommercePriceEntriesImporter _commercePriceEntriesImporter;

	@Reference
	private CommercePriceListsImporter _commercePriceListsImporter;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private CommerceUsersImporter _commerceUsersImporter;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private Map<String, CPDefinition> _cpDefinitions;

	@Reference
	private CPDefinitionsImporter _cpDefinitionsImporter;

	@Reference
	private CPFileImporter _cpFileImporter;

	@Reference
	private CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;

	@Reference
	private CPOptionCategoriesImporter _cpOptionCategoriesImporter;

	@Reference
	private CPOptionsImporter _cpOptionsImporter;

	@Reference
	private CPSpecificationOptionsImporter _cpSpecificationOptionsImporter;

	@Reference
	private DDMFormImporter _ddmFormImporter;

	@Reference(
		target = "(site.initializer.key=" + MiniumSiteInitializer.KEY + ")"
	)
	private SiteInitializerDependencyResolver
		_defaultSiteInitializerDependencyResolver;

	@Reference
	private DLImporter _dlImporter;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private KBArticleImporter _kbArticleImporter;

	@Reference
	private Language _language;

	@Reference
	private OrganizationImporter _organizationImporter;

	@Reference
	private PortletSettingsImporter _portletSettingsImporter;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.theme.minium.site.initializer)"
	)
	private ServletContext _servletContext;

	private SiteInitializerDependencyResolver
		_siteInitializerDependencyResolver;

	@Reference
	private ThemeLocalService _themeLocalService;

	@Reference
	private UserLocalService _userLocalService;

}