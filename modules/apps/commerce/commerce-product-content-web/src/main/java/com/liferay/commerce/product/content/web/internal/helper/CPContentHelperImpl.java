/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.helper;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.commerce.constants.CPDefinitionInventoryConstants;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemLocalService;
import com.liferay.commerce.inventory.util.comparator.CommerceInventoryReplenishmentItemAvailabilityDateComparator;
import com.liferay.commerce.media.CommerceCatalogDefaultImage;
import com.liferay.commerce.media.CommerceMediaProvider;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.constants.CPContentContributorConstants;
import com.liferay.commerce.product.constants.CPOptionCategoryConstants;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.content.render.CPContentRenderer;
import com.liferay.commerce.product.content.render.CPContentRendererRegistry;
import com.liferay.commerce.product.content.util.CPMedia;
import com.liferay.commerce.product.content.web.internal.util.CPMediaImpl;
import com.liferay.commerce.product.content.web.internal.util.CPMediaUtil;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.product.util.CPContentContributor;
import com.liferay.commerce.product.util.CPContentContributorRegistry;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;

import java.text.Format;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Ivica Cardic
 */
@Component(service = CPContentHelper.class)
public class CPContentHelperImpl implements CPContentHelper {

	@Override
	public JSONObject getAvailabilityContentContributorValueJSONObject(
			CPCatalogEntry cpCatalogEntry,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return getCPContentContributorValueJSONObject(
			CPContentContributorConstants.AVAILABILITY_NAME, cpCatalogEntry,
			httpServletRequest);
	}

	@Override
	public String getAvailabilityEstimateLabel(
			HttpServletRequest httpServletRequest)
		throws Exception {

		JSONObject availabilityEstimateJSONObject =
			getCPContentContributorValueJSONObject(
				CPContentContributorConstants.AVAILABILITY_ESTIMATE_NAME,
				httpServletRequest);

		if (availabilityEstimateJSONObject == null) {
			return StringPool.BLANK;
		}

		return availabilityEstimateJSONObject.getString(
			CPContentContributorConstants.AVAILABILITY_ESTIMATE_NAME);
	}

	@Override
	public String getAvailabilityLabel(HttpServletRequest httpServletRequest)
		throws Exception {

		JSONObject availabilityJSONObject =
			getCPContentContributorValueJSONObject(
				CPContentContributorConstants.AVAILABILITY_NAME,
				httpServletRequest);

		if (availabilityJSONObject == null) {
			return StringPool.BLANK;
		}

		return availabilityJSONObject.getString(
			CPContentContributorConstants.AVAILABILITY_NAME);
	}

	@Override
	public List<CPDefinitionSpecificationOptionValue>
			getCategorizedCPDefinitionSpecificationOptionValues(
				long cpDefinitionId, long cpOptionCategoryId)
		throws PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValues(
				cpDefinitionId, cpOptionCategoryId, true);
	}

	@Override
	public CPCatalogEntry getCPCatalogEntry(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		CPCatalogEntry cpCatalogEntry =
			(CPCatalogEntry)httpServletRequest.getAttribute(
				CPWebKeys.CP_CATALOG_ENTRY);

		if (cpCatalogEntry == null) {
			long productId = ParamUtil.getLong(httpServletRequest, "productId");

			try {
				CProduct cProduct = _cProductLocalService.fetchCProduct(
					productId);

				if (cProduct == null) {
					return null;
				}

				CommerceContext commerceContext =
					(CommerceContext)httpServletRequest.getAttribute(
						CommerceWebKeys.COMMERCE_CONTEXT);

				cpCatalogEntry = _cpDefinitionHelper.getCPCatalogEntry(
					CommerceUtil.getCommerceAccountId(
						(CommerceContext)httpServletRequest.getAttribute(
							CommerceWebKeys.COMMERCE_CONTEXT)),
					commerceContext.getCommerceChannelGroupId(),
					cProduct.getPublishedCPDefinitionId(),
					_portal.getLocale(httpServletRequest));
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		return cpCatalogEntry;
	}

	@Override
	public JSONObject getCPContentContributorValueJSONObject(
			String contributorKey, CPCatalogEntry cpCatalogEntry,
			HttpServletRequest httpServletRequest)
		throws Exception {

		CPContentContributor cpContentContributor =
			_cpContentContributorRegistry.getCPContentContributor(
				contributorKey);

		if (cpContentContributor == null) {
			return null;
		}

		return cpContentContributor.getValue(
			getDefaultCPInstance(cpCatalogEntry), httpServletRequest);
	}

	@Override
	public JSONObject getCPContentContributorValueJSONObject(
			String contributorKey, HttpServletRequest httpServletRequest)
		throws Exception {

		return getCPContentContributorValueJSONObject(
			contributorKey, getCPCatalogEntry(httpServletRequest),
			httpServletRequest);
	}

	@Override
	public String getCPContentRendererKey(
		String type, RenderRequest renderRequest) {

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		String value = portletPreferences.getValue(
			type + "--cpTypeRendererKey", null);

		if (Validator.isNotNull(value)) {
			return value;
		}

		List<CPContentRenderer> cpContentRenderers = getCPContentRenderers(
			type);

		if (cpContentRenderers.isEmpty()) {
			return StringPool.BLANK;
		}

		CPContentRenderer cpContentRenderer = cpContentRenderers.get(0);

		if (cpContentRenderer == null) {
			return StringPool.BLANK;
		}

		return cpContentRenderer.getKey();
	}

	@Override
	public List<CPContentRenderer> getCPContentRenderers(String cpType) {
		return _cpContentRendererRegistry.getCPContentRenderers(cpType);
	}

	@Override
	public String getCPDefinitionCDNURL(
			long cpDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception {

		CPDefinition cpDefinition = _cpDefinitionLocalService.fetchCPDefinition(
			cpDefinitionId);

		if (cpDefinition == null) {
			return StringPool.BLANK;
		}

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			cpDefinition.getCPAttachmentFileEntries(
				CPAttachmentFileEntryConstants.TYPE_IMAGE,
				WorkflowConstants.STATUS_APPROVED);

		if (cpAttachmentFileEntries.isEmpty()) {
			return cpDefinition.getDefaultImageThumbnailSrc(
				CommerceUtil.getCommerceAccountId(
					(CommerceContext)httpServletRequest.getAttribute(
						CommerceWebKeys.COMMERCE_CONTEXT)));
		}

		CPAttachmentFileEntry cpAttachmentFileEntry =
			cpAttachmentFileEntries.get(0);

		if (!cpAttachmentFileEntry.isCDNEnabled()) {
			return StringPool.BLANK;
		}

		return cpAttachmentFileEntry.getCDNURL();
	}

	@Override
	public FileVersion getCPDefinitionImageFileVersion(
			long cpDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception {

		if (!_commerceProductViewPermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				CommerceUtil.getCommerceAccountId(
					(CommerceContext)httpServletRequest.getAttribute(
						CommerceWebKeys.COMMERCE_CONTEXT)),
				cpDefinitionId)) {

			return null;
		}

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpDefinitionLocalService.getDefaultImageCPAttachmentFileEntry(
				cpDefinitionId);

		if (cpAttachmentFileEntry != null) {
			FileEntry fileEntry = cpAttachmentFileEntry.fetchFileEntry();

			if (fileEntry != null) {
				return fileEntry.getFileVersion();
			}
		}

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		FileEntry fileEntry = _commerceMediaProvider.getDefaultImageFileEntry(
			_portal.getCompanyId(httpServletRequest),
			cpDefinition.getGroupId());

		return fileEntry.getFileVersion();
	}

	@Override
	public List<CPDefinitionSpecificationOptionValue>
			getCPDefinitionSpecificationOptionValues(long cpDefinitionId)
		throws PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValues(
				cpDefinitionId,
				CPOptionCategoryConstants.DEFAULT_CP_OPTION_CATEGORY_ID, true);
	}

	@Override
	public List<CPMedia> getCPMedias(
			long cpDefinitionId, ThemeDisplay themeDisplay)
		throws PortalException {

		return CPMediaUtil.getAttachmentCPMedias(
			_portal.getClassNameId(CPDefinition.class.getName()),
			cpDefinitionId, _cpAttachmentFileEntryLocalService,
			_dlFileEntryLocalService, _dlFileEntryModelResourcePermission,
			themeDisplay);
	}

	@Override
	public List<CPOptionCategory> getCPOptionCategories(long companyId) {
		return _cpOptionCategoryLocalService.getCPOptionCategories(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<CPType> getCPTypes() {
		return _cpTypeRegistry.getCPTypes();
	}

	@Override
	public CPInstance getDefaultCPInstance(CPCatalogEntry cpCatalogEntry)
		throws Exception {

		if (cpCatalogEntry == null) {
			return null;
		}

		return _cpInstanceHelper.getDefaultCPInstance(
			cpCatalogEntry.getCPDefinitionId());
	}

	@Override
	public CPInstance getDefaultCPInstance(
			HttpServletRequest httpServletRequest)
		throws Exception {

		return getDefaultCPInstance(getCPCatalogEntry(httpServletRequest));
	}

	@Override
	public CPSku getDefaultCPSku(CPCatalogEntry cpCatalogEntry)
		throws Exception {

		return _cpInstanceHelper.getDefaultCPSku(cpCatalogEntry);
	}

	@Override
	public String getDefaultImageFileURL(
			long commerceAccountId, long cpDefinitionId)
		throws PortalException {

		return _cpDefinitionHelper.getDefaultImageFileURL(
			commerceAccountId, cpDefinitionId);
	}

	@Override
	public String getDownloadFileEntryURL(
			FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws PortalException {

		CPMedia cpMedia = new CPMediaImpl(fileEntry, themeDisplay);

		return cpMedia.getDownloadURL();
	}

	@Override
	public String getFriendlyURL(
			CPCatalogEntry cpCatalogEntry, ThemeDisplay themeDisplay)
		throws PortalException {

		return _cpDefinitionHelper.getFriendlyURL(
			cpCatalogEntry.getCPDefinitionId(), themeDisplay);
	}

	@Override
	public List<CPMedia> getImages(
			long cpDefinitionId, boolean gallery, ThemeDisplay themeDisplay)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		return CPMediaUtil.getImageCPMedias(
			_amImageHTMLTagFactory,
			_portal.getClassNameId(CPDefinition.class.getName()),
			cpDefinition.getCPDefinitionId(), _commerceCatalogDefaultImage,
			_commerceMediaResolver, _cpAttachmentFileEntryLocalService, gallery,
			cpDefinition.getGroupId(), themeDisplay);
	}

	@Override
	public String getImageURL(FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws Exception {

		CPMedia cpMedia = new CPMediaImpl(fileEntry, themeDisplay);

		return cpMedia.getURL();
	}

	@Override
	public String getIncomingQuantityLabel(
			long companyId, Locale locale, String sku, String unitOfMeasureKey,
			User user)
		throws PortalException {

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			_commerceInventoryReplenishmentItemLocalService.
				fetchCommerceInventoryReplenishmentItem(
					companyId, sku, unitOfMeasureKey,
					CommerceInventoryReplenishmentItemAvailabilityDateComparator.
						getInstance(true));

		if (commerceInventoryReplenishmentItem == null) {
			return StringPool.BLANK;
		}

		Format dateFormat = FastDateFormatFactoryUtil.getDate(
			user.getLocale(), user.getTimeZone());

		BigDecimal commerceInventoryReplenishmentItemQuantity =
			commerceInventoryReplenishmentItem.getQuantity();

		return _language.format(
			locale, "incoming-date-quantity-x-x-items",
			new Object[] {
				dateFormat.format(
					commerceInventoryReplenishmentItem.getAvailabilityDate()),
				commerceInventoryReplenishmentItemQuantity.intValue()
			});
	}

	@Override
	public BigDecimal getMinOrderQuantity(long cpDefinitionId) {
		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(cpDefinitionId);

		BigDecimal minOrderQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MIN_ORDER_QUANTITY;

		if (cpDefinitionInventory != null) {
			minOrderQuantity = cpDefinitionInventory.getMinOrderQuantity();
		}

		return minOrderQuantity;
	}

	@Override
	public String getReplacementCommerceProductFriendlyURL(
			CPSku cpSku, ThemeDisplay themeDisplay)
		throws PortalException {

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		long commerceOrderTypeId = 0;

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		if (commerceOrder != null) {
			commerceOrderTypeId = commerceOrder.getCommerceOrderTypeId();
		}

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		CPInstance firstAvailableReplacementCPInstance =
			_cpInstanceHelper.fetchFirstAvailableReplacementCPInstance(
				accountEntry.getAccountEntryId(),
				commerceContext.getCommerceChannelGroupId(),
				commerceOrderTypeId, cpSku.getCPInstanceId());

		if (firstAvailableReplacementCPInstance == null) {
			return StringPool.BLANK;
		}

		return _cpDefinitionHelper.getFriendlyURL(
			firstAvailableReplacementCPInstance.getCPDefinitionId(),
			themeDisplay);
	}

	@Override
	public String getStockQuantity(HttpServletRequest httpServletRequest)
		throws Exception {

		JSONObject stockQuantityJSONObject =
			getCPContentContributorValueJSONObject(
				CPContentContributorConstants.STOCK_QUANTITY_NAME,
				httpServletRequest);

		if (stockQuantityJSONObject == null) {
			return StringPool.BLANK;
		}

		return stockQuantityJSONObject.getString(
			CPContentContributorConstants.STOCK_QUANTITY_NAME);
	}

	@Override
	public String getStockQuantityLabel(HttpServletRequest httpServletRequest)
		throws Exception {

		JSONObject stockQuantityJSONObject =
			getCPContentContributorValueJSONObject(
				CPContentContributorConstants.STOCK_QUANTITY_NAME,
				httpServletRequest);

		if (stockQuantityJSONObject == null) {
			return StringPool.BLANK;
		}

		return _language.format(
			httpServletRequest, "stock-quantity-x",
			stockQuantityJSONObject.getString(
				CPContentContributorConstants.STOCK_QUANTITY_NAME));
	}

	@Override
	public String getSubscriptionInfoLabel(
			HttpServletRequest httpServletRequest)
		throws Exception {

		JSONObject subscriptionInfoJSONObject =
			getCPContentContributorValueJSONObject(
				CPContentContributorConstants.SUBSCRIPTION_INFO,
				httpServletRequest);

		if (subscriptionInfoJSONObject == null) {
			return StringPool.BLANK;
		}

		return subscriptionInfoJSONObject.getString(
			CPContentContributorConstants.SUBSCRIPTION_INFO);
	}

	@Override
	public ResourceURL getViewAttachmentURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		ResourceURL resourceURL = liferayPortletResponse.createResourceURL();

		CPCatalogEntry cpCatalogEntry = getCPCatalogEntry(
			_portal.getHttpServletRequest(liferayPortletRequest));

		if (cpCatalogEntry != null) {
			resourceURL.setParameter(
				"cpDefinitionId",
				String.valueOf(cpCatalogEntry.getCPDefinitionId()));
		}

		resourceURL.setResourceID("/cp_content_web/view_cp_attachments");

		return resourceURL;
	}

	@Override
	public boolean hasChildCPDefinitions(long cpDefinitionId) {
		return _cpDefinitionLocalService.hasChildCPDefinitions(cpDefinitionId);
	}

	@Override
	public boolean hasCPDefinitionOptionRels(long cpDefinitionId) {
		int cpDefinitionOptionRelsCount =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRelsCount(
				cpDefinitionId);

		if (cpDefinitionOptionRelsCount > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasCPDefinitionSpecificationOptionValues(long cpDefinitionId)
		throws PortalException {

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				_cpDefinitionSpecificationOptionValueLocalService.
					getCPDefinitionSpecificationOptionValues(
						cpDefinitionId, true, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null);

		return !cpDefinitionSpecificationOptionValues.isEmpty();
	}

	@Override
	public boolean hasMultipleCPSkus(CPCatalogEntry cpCatalogEntry) {
		List<CPInstance> cpDefinitionInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpCatalogEntry.getCPDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, 0, 2, null);

		if (cpDefinitionInstances.size() == 1) {
			CPInstance cpInstance = cpDefinitionInstances.get(0);

			int cpInstanceUnitOfMeasureCount =
				_cpInstanceUnitOfMeasureLocalService.
					getActiveCPInstanceUnitOfMeasuresCount(
						cpInstance.getCPInstanceId());

			if (cpInstanceUnitOfMeasureCount > 1) {
				return true;
			}
		}
		else if (cpDefinitionInstances.size() > 1) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasReplacement(
			CPSku cpSku, HttpServletRequest httpServletRequest)
		throws Exception {

		if ((cpSku == null) || !cpSku.isDiscontinued()) {
			return false;
		}

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		long commerceAccountId = AccountConstants.ACCOUNT_ENTRY_ID_GUEST;

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry != null) {
			commerceAccountId = accountEntry.getAccountEntryId();
		}

		long commerceOrderTypeId = 0;

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		if (commerceOrder != null) {
			commerceOrderTypeId = commerceOrder.getCommerceOrderTypeId();
		}

		CPInstance firstAvailableReplacementCPInstance =
			_cpInstanceHelper.fetchFirstAvailableReplacementCPInstance(
				commerceAccountId, commerceContext.getCommerceChannelGroupId(),
				commerceOrderTypeId, cpSku.getCPInstanceId());

		if (firstAvailableReplacementCPInstance != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isDirectReplacement(CPSku cpSku) throws Exception {
		if ((cpSku == null) || cpSku.isDiscontinued()) {
			return false;
		}

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cpSku.getCPInstanceId());

		if ((cpInstance == null) || cpInstance.isDiscontinued()) {
			return false;
		}

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		List<CPInstance> cpInstances = _cpInstanceLocalService.getCPInstances(
			cpInstance.getCPInstanceUuid(), cpDefinition.getCProductId(),
			WorkflowConstants.STATUS_APPROVED);

		return !cpInstances.isEmpty();
	}

	@Override
	public boolean isInWishList(
			CPSku cpSku, CPCatalogEntry cpCatalogEntry,
			ThemeDisplay themeDisplay)
		throws Exception {

		CommerceWishList commerceWishList =
			_commerceWishListService.getDefaultCommerceWishList(
				themeDisplay.getScopeGroupId());

		if (commerceWishList != null) {
			long commerceWishListId = commerceWishList.getCommerceWishListId();

			if (cpSku != null) {
				int itemByContainsCPInstanceCount =
					_commerceWishListItemService.
						getCommerceWishListItemByContainsCPInstanceCount(
							commerceWishListId, cpSku.getCPInstanceUuid());

				if (itemByContainsCPInstanceCount > 0) {
					return true;
				}

				return false;
			}

			int itemByContainsCProductCount =
				_commerceWishListItemService.
					getCommerceWishListItemByContainsCProductCount(
						commerceWishListId, cpCatalogEntry.getCProductId());

			if (itemByContainsCProductCount > 0) {
				return true;
			}

			return false;
		}

		return false;
	}

	@Override
	public void renderCPType(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		CPCatalogEntry cpCatalogEntry = getCPCatalogEntry(httpServletRequest);

		if (cpCatalogEntry == null) {
			return;
		}

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		CPContentRenderer cpContentRenderer =
			_cpContentRendererRegistry.getCPContentRenderer(
				getCPContentRendererKey(
					cpCatalogEntry.getProductTypeName(), renderRequest));

		if (cpContentRenderer == null) {
			cpContentRenderer = _cpContentRendererRegistry.getCPContentRenderer(
				"default");
		}

		cpContentRenderer.render(
			cpCatalogEntry, httpServletRequest, httpServletResponse);
	}

	@Override
	public void renderOptions(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		CPCatalogEntry cpCatalogEntry = getCPCatalogEntry(httpServletRequest);

		if (cpCatalogEntry == null) {
			return;
		}

		long cpInstanceId = 0;

		CPInstance defaultCPInstance = getDefaultCPInstance(cpCatalogEntry);

		if (defaultCPInstance != null) {
			cpInstanceId = defaultCPInstance.getCPInstanceId();
		}

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpCatalogEntry.getCPDefinitionId());

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			CommerceOptionType commerceOptionType =
				_commerceOptionTypeRegistry.getCommerceOptionType(
					cpDefinitionOptionRel.getCommerceOptionTypeKey());

			commerceOptionType.render(
				cpDefinitionOptionRel, cpInstanceId, false, null,
				httpServletRequest, httpServletResponse);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPContentHelperImpl.class);

	@Reference
	private AMImageHTMLTagFactory _amImageHTMLTagFactory;

	@Reference
	private CommerceCatalogDefaultImage _commerceCatalogDefaultImage;

	@Reference
	private CommerceInventoryReplenishmentItemLocalService
		_commerceInventoryReplenishmentItemLocalService;

	@Reference
	private CommerceMediaProvider _commerceMediaProvider;

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CommerceOptionTypeRegistry _commerceOptionTypeRegistry;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CommerceWishListItemService _commerceWishListItemService;

	@Reference
	private CommerceWishListService _commerceWishListService;

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private CPContentContributorRegistry _cpContentContributorRegistry;

	@Reference
	private CPContentRendererRegistry _cpContentRendererRegistry;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CPTypeRegistry _cpTypeRegistry;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	private ModelResourcePermission<DLFileEntry>
		_dlFileEntryModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}