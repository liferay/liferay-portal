/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.helper;

import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.media.CommerceMediaProvider;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.availability.CPAvailabilityChecker;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.discovery.CPConfigurationListDiscovery;
import com.liferay.commerce.product.helper.CPCollectionProviderHelper;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.internal.catalog.CPSkuImpl;
import com.liferay.commerce.product.internal.util.comparator.CPDefinitionOptionRelComparator;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.commerce.product.util.comparator.CPDefinitionOptionValueRelPriorityComparator;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 * @author Igor Beslic
 */
@Component(service = CPInstanceHelper.class)
public class CPInstanceHelperImpl implements CPInstanceHelper {

	@Override
	public CPInstance fetchCPInstance(
			long cpDefinitionId, JSONArray skuOptionJSONArray)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		if (cpDefinition.isIgnoreSKUCombinations()) {
			return getDefaultCPInstance(cpDefinitionId);
		}

		if (JSONUtil.isEmpty(skuOptionJSONArray)) {
			return null;
		}

		return _fetchCPInstance(cpDefinitionId, skuOptionJSONArray);
	}

	@Override
	public CPInstance fetchCPInstance(
			long cpDefinitionId, String serializedFormFieldValues)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		if (cpDefinition.isIgnoreSKUCombinations()) {
			return getDefaultCPInstance(cpDefinitionId);
		}

		if (CPJSONUtil.isEmpty(serializedFormFieldValues)) {
			return null;
		}

		return _fetchCPInstance(cpDefinitionId, serializedFormFieldValues);
	}

	@Override
	public BigDecimal fetchCPInstanceUnitPrice(CPInstance cpInstance) {
		try {
			CommerceContext commerceContext = CommerceContextThreadLocal.get();

			CommerceMoney unitPriceCommerceMoney =
				_commerceProductPriceCalculation.getUnitPrice(
					cpInstance.getCPInstanceId(), BigDecimal.ONE,
					commerceContext.getCommerceCurrency(), false,
					StringPool.BLANK, commerceContext);

			if (!unitPriceCommerceMoney.isEmpty()) {
				return unitPriceCommerceMoney.getPrice();
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	@Override
	public BigDecimal fetchCPInstanceUnitPromoPrice(CPInstance cpInstance) {
		try {
			CommerceContext commerceContext = CommerceContextThreadLocal.get();

			CommerceMoney unitPromoPriceCommerceMoney =
				_commerceProductPriceCalculation.getPromoPrice(
					cpInstance.getCPInstanceId(), BigDecimal.ONE,
					commerceContext.getCommerceCurrency(), true,
					StringPool.BLANK, commerceContext);

			if (!unitPromoPriceCommerceMoney.isEmpty()) {
				return unitPromoPriceCommerceMoney.getPrice();
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	@Override
	public CPInstance fetchFirstAvailableReplacementCPInstance(
			long accountEntryId, long commerceChannelGroupId,
			long commerceOrderTypeId, long cpInstanceId)
		throws PortalException {

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cpInstanceId);

		if ((cpInstance == null) || !cpInstance.isDiscontinued() ||
			_cpAvailabilityChecker.check(
				accountEntryId, commerceChannelGroupId, cpInstance,
				StringPool.BLANK,
				_cpDefinitionInventoryEngine.getMinOrderQuantity(
					_getCPConfigurationListId(
						accountEntryId, commerceChannelGroupId,
						commerceOrderTypeId, cpInstance),
					cpInstance))) {

			return null;
		}

		cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			cpInstance.getReplacementCProductId(),
			cpInstance.getReplacementCPInstanceUuid());

		return _fetchFirstAvailableReplacementCPInstance(
			accountEntryId, commerceChannelGroupId, 0, cpInstance);
	}

	@Override
	public CPInstance fetchReplacementCPInstance(
			long cProductId, String cpInstanceUuid)
		throws PortalException {

		return _cpInstanceLocalService.fetchCProductInstance(
			cProductId, cpInstanceUuid);
	}

	@Override
	public List<CPDefinitionOptionValueRel> filterCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId,
			List<Long> skuCombinationCPDefinitionOptionValueRelIds)
		throws PortalException {

		List<CPInstanceOptionValueRel> cpInstanceOptionValueRels =
			_cpInstanceOptionValueRelLocalService.
				getCPDefinitionOptionRelCPInstanceOptionValueRels(
					cpDefinitionOptionRelId);

		List<CPDefinitionOptionValueRel> filtered = new ArrayList<>();

		for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
				cpInstanceOptionValueRels) {

			if (!_hasCPInstanceCPDefinitionOptionValueRelIds(
					cpInstanceOptionValueRel.getCPInstanceId(),
					skuCombinationCPDefinitionOptionValueRelIds)) {

				continue;
			}

			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				_cpDefinitionOptionValueRelLocalService.
					getCPInstanceCPDefinitionOptionValueRel(
						cpDefinitionOptionRelId,
						cpInstanceOptionValueRel.getCPInstanceId());

			if (filtered.contains(cpDefinitionOptionValueRel)) {
				continue;
			}

			filtered.add(cpDefinitionOptionValueRel);
		}

		Collections.sort(filtered);

		return filtered;
	}

	@Override
	public List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long commerceAccountId, long commerceChannelGroupId,
			long cpDefinitionId, String serializedFormFieldValues, int type)
		throws Exception {

		return getCPAttachmentFileEntries(
			commerceAccountId, commerceChannelGroupId, cpDefinitionId,
			serializedFormFieldValues, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);
	}

	@Override
	public List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long commerceAccountId, long commerceChannelGroupId,
			long cpDefinitionId, String serializedFormFieldValues, int type,
			int start, int end)
		throws Exception {

		_commerceProductViewPermission.check(
			PermissionThreadLocal.getPermissionChecker(), commerceAccountId,
			commerceChannelGroupId, cpDefinitionId);

		return _cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
			cpDefinitionId, true, serializedFormFieldValues, type, start, end);
	}

	@Override
	public Map<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
		getCPDefinitionOptionValueRelsMap(
			long cpDefinitionId, boolean skuContributor, boolean publicStore) {

		List<CPDefinitionOptionRel> cpDefinitionOptionRels;

		if (skuContributor) {
			cpDefinitionOptionRels =
				_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
					cpDefinitionId, true);
		}
		else {
			cpDefinitionOptionRels =
				_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
					cpDefinitionId);
		}

		if (cpDefinitionOptionRels.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
			cpDefinitionOptionRelsMap = new TreeMap<>(
				new CPDefinitionOptionRelComparator());

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			if (cpDefinitionOptionRel.isSkuContributor() && publicStore) {
				cpDefinitionOptionRelsMap.put(
					cpDefinitionOptionRel,
					getCPInstanceCPDefinitionOptionValueRels(
						cpDefinitionId,
						cpDefinitionOptionRel.getCPDefinitionOptionRelId()));

				continue;
			}

			if (cpDefinitionOptionRel.isDefinedExternally()) {
				cpDefinitionOptionRelsMap.put(
					cpDefinitionOptionRel,
					_cpCollectionProviderHelper.getCPDefinitionOptionValueRels(
						cpDefinitionOptionRel, null, null));

				continue;
			}

			cpDefinitionOptionRelsMap.put(
				cpDefinitionOptionRel,
				cpDefinitionOptionRel.getCPDefinitionOptionValueRels());
		}

		return cpDefinitionOptionRelsMap;
	}

	@Override
	public Map<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
			getCPDefinitionOptionValueRelsMap(long cpDefinitionId, String json)
		throws PortalException {

		Map<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
			cpDefinitionOptionRelListMap = new HashMap<>();

		if (Validator.isNull(json)) {
			return cpDefinitionOptionRelListMap;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String key = jsonObject.getString("key");

			if (Validator.isNull(key)) {
				key = jsonObject.getString("skuOptionKey");
			}

			CPDefinitionOptionRel cpDefinitionOptionRel =
				_cpDefinitionOptionRelLocalService.
					fetchCPDefinitionOptionRelByKey(cpDefinitionId, key);

			if (cpDefinitionOptionRel == null) {
				continue;
			}

			JSONArray valueJSONArray = jsonObject.getJSONArray("value");

			if (valueJSONArray == null) {
				Object skuOptionValueKey = jsonObject.get("skuOptionValueKey");

				if (skuOptionValueKey == null) {
					continue;
				}

				if (JSONUtil.isJSONArray(skuOptionValueKey.toString())) {
					valueJSONArray = (JSONArray)skuOptionValueKey;
				}
				else {
					valueJSONArray = JSONUtil.put(skuOptionValueKey);
				}
			}

			for (int j = 0; j < valueJSONArray.length(); j++) {
				CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
					_cpDefinitionOptionValueRelLocalService.
						fetchCPDefinitionOptionValueRel(
							cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
							valueJSONArray.getString(j));

				if (cpDefinitionOptionValueRel == null) {
					continue;
				}

				List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
					cpDefinitionOptionRelListMap.get(cpDefinitionOptionRel);

				if (cpDefinitionOptionValueRels == null) {
					cpDefinitionOptionValueRels = new ArrayList<>();

					cpDefinitionOptionRelListMap.put(
						cpDefinitionOptionRel, cpDefinitionOptionValueRels);
				}

				cpDefinitionOptionValueRels.add(cpDefinitionOptionValueRel);
			}
		}

		return cpDefinitionOptionRelListMap;
	}

	@Override
	public String getCPInstanceAdaptiveMediaImageHTMLTag(
			long commerceAccountId, long companyId, long cpInstanceId)
		throws Exception {

		FileVersion fileVersion = getCPInstanceImageFileVersion(
			commerceAccountId, companyId, cpInstanceId);

		String originalImgTag = StringBundler.concat(
			"<img alt=\"thumbnail\" class=\"aspect-ratio-bg-cover ",
			"aspect-ratio-item aspect-ratio-item-center-middle ",
			"aspect-ratio-item-fluid card-type-asset-icon\" src=\"",
			getCPInstanceThumbnailSrc(commerceAccountId, cpInstanceId),
			"\" />");

		if (fileVersion == null) {
			return originalImgTag;
		}

		return _amImageHTMLTagFactory.create(
			originalImgTag, fileVersion.getFileEntry());
	}

	@Override
	public String getCPInstanceCDNURL(long commerceAccountId, long cpInstanceId)
		throws Exception {

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cpInstanceId);

		if (cpInstance == null) {
			return StringPool.BLANK;
		}

		JSONArray jsonArray = CPJSONUtil.toJSONArray(
			_cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
					cpInstanceId));

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
				cpInstance.getCPDefinitionId(), null, jsonArray.toString(),
				CPAttachmentFileEntryConstants.TYPE_IMAGE, 0, 1);

		if (cpAttachmentFileEntries.isEmpty()) {
			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			return cpDefinition.getDefaultImageThumbnailSrc(commerceAccountId);
		}

		CPAttachmentFileEntry cpAttachmentFileEntry =
			cpAttachmentFileEntries.get(0);

		if (!cpAttachmentFileEntry.isCDNEnabled()) {
			return StringPool.BLANK;
		}

		return cpAttachmentFileEntry.getCDNURL();
	}

	@Override
	public Map<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
			getCPInstanceCPDefinitionOptionRelsMap(long cpInstanceId)
		throws PortalException {

		List<CPInstanceOptionValueRel> cpInstanceCPInstanceOptionValueRels =
			_cpInstanceOptionValueRelLocalService.
				getCPInstanceCPInstanceOptionValueRels(cpInstanceId);

		if (cpInstanceCPInstanceOptionValueRels.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
			cpDefinitionOptionRelsMap = new HashMap<>();

		for (CPInstanceOptionValueRel cpInstanceCPInstanceOptionValueRel :
				cpInstanceCPInstanceOptionValueRels) {

			CPDefinitionOptionRel cpDefinitionOptionRel =
				_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRel(
					cpInstanceCPInstanceOptionValueRel.
						getCPDefinitionOptionRelId());

			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
				cpDefinitionOptionRelsMap.get(cpDefinitionOptionRel);

			if (cpDefinitionOptionValueRels == null) {
				cpDefinitionOptionValueRels = new ArrayList<>();

				cpDefinitionOptionRelsMap.put(
					cpDefinitionOptionRel, cpDefinitionOptionValueRels);
			}

			cpDefinitionOptionValueRels.add(
				_cpDefinitionOptionValueRelLocalService.
					getCPDefinitionOptionValueRel(
						cpInstanceCPInstanceOptionValueRel.
							getCPDefinitionOptionValueRelId()));
		}

		return cpDefinitionOptionRelsMap;
	}

	@Override
	public List<CPDefinitionOptionValueRel>
		getCPInstanceCPDefinitionOptionValueRels(
			long cpDefinitionId, long cpDefinitionOptionRelId) {

		List<CPInstanceOptionValueRel> cpDefinitionCPInstanceOptionValueRels =
			_cpInstanceOptionValueRelLocalService.
				getCPDefinitionCPInstanceOptionValueRels(cpDefinitionId);

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			new ArrayList<>();

		for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
				cpDefinitionCPInstanceOptionValueRels) {

			if (cpDefinitionOptionRelId !=
					cpInstanceOptionValueRel.getCPDefinitionOptionRelId()) {

				continue;
			}

			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				_cpDefinitionOptionValueRelLocalService.
					fetchCPDefinitionOptionValueRel(
						cpInstanceOptionValueRel.
							getCPDefinitionOptionValueRelId());

			if ((cpDefinitionOptionValueRel != null) &&
				!cpDefinitionOptionValueRels.contains(
					cpDefinitionOptionValueRel)) {

				cpDefinitionOptionValueRels.add(cpDefinitionOptionValueRel);
			}
		}

		Collections.sort(
			cpDefinitionOptionValueRels,
			CPDefinitionOptionValueRelPriorityComparator.getInstance(true));

		return cpDefinitionOptionValueRels;
	}

	@Override
	public List<CPInstanceOptionValueRel>
		getCPInstanceCPInstanceOptionValueRels(long cpInstanceId) {

		return _cpInstanceOptionValueRelLocalService.
			getCPInstanceCPInstanceOptionValueRels(cpInstanceId);
	}

	@Override
	public FileVersion getCPInstanceImageFileVersion(
			long commerceAccountId, long companyId, long cpInstanceId)
		throws Exception {

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cpInstanceId);

		if (cpInstance == null) {
			return null;
		}

		JSONArray jsonArray = CPJSONUtil.toJSONArray(
			_cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
					cpInstanceId));

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
				cpInstance.getCPDefinitionId(), null, jsonArray.toString(),
				CPAttachmentFileEntryConstants.TYPE_IMAGE, 0, 1);

		if (cpAttachmentFileEntries.isEmpty()) {
			CPAttachmentFileEntry cpAttachmentFileEntry =
				_cpDefinitionLocalService.getDefaultImageCPAttachmentFileEntry(
					cpInstance.getCPDefinitionId());

			if (cpAttachmentFileEntry != null) {
				FileEntry fileEntry = cpAttachmentFileEntry.fetchFileEntry();

				if (fileEntry != null) {
					return fileEntry.getFileVersion();
				}
			}

			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(
					cpInstance.getCPDefinitionId());

			FileEntry fileEntry =
				_commerceMediaProvider.getDefaultImageFileEntry(
					companyId, cpDefinition.getGroupId());

			return fileEntry.getFileVersion();
		}

		CPAttachmentFileEntry cpAttachmentFileEntry =
			cpAttachmentFileEntries.get(0);

		FileEntry fileEntry = cpAttachmentFileEntry.fetchFileEntry();

		if (fileEntry == null) {
			return null;
		}

		return fileEntry.getFileVersion();
	}

	@Override
	public String getCPInstanceThumbnailSrc(
			long commerceAccountId, long cpInstanceId)
		throws Exception {

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cpInstanceId);

		if (cpInstance == null) {
			return StringPool.BLANK;
		}

		JSONArray jsonArray = CPJSONUtil.toJSONArray(
			_cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
					cpInstanceId));

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
				cpInstance.getCPDefinitionId(), null, jsonArray.toString(),
				CPAttachmentFileEntryConstants.TYPE_IMAGE, 0, 1);

		if (cpAttachmentFileEntries.isEmpty()) {
			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			return cpDefinition.getDefaultImageThumbnailSrc(commerceAccountId);
		}

		CPAttachmentFileEntry cpAttachmentFileEntry =
			cpAttachmentFileEntries.get(0);

		return _commerceMediaResolver.getThumbnailURL(
			commerceAccountId,
			cpAttachmentFileEntry.getCPAttachmentFileEntryId(), false);
	}

	@Override
	public CPInstance getDefaultCPInstance(long cpDefinitionId)
		throws PortalException {

		return _cpInstanceLocalService.fetchDefaultCPInstance(cpDefinitionId);
	}

	@Override
	public CPSku getDefaultCPSku(CPCatalogEntry cpCatalogEntry)
		throws Exception {

		CPInstance cpInstance = getDefaultCPInstance(
			cpCatalogEntry.getCPDefinitionId());

		if (cpInstance == null) {
			return null;
		}

		return new CPSkuImpl(
			cpInstance, fetchCPInstanceUnitPrice(cpInstance),
			fetchCPInstanceUnitPromoPrice(cpInstance));
	}

	@Override
	public List<KeyValuePair> getKeyValuePairs(
			long cpDefinitionId, String json, Locale locale)
		throws PortalException {

		List<KeyValuePair> values = new ArrayList<>();

		if (Validator.isNull(json)) {
			return values;
		}

		JSONArray jsonArray = CPJSONUtil.toJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String key = jsonObject.getString("key");

			CPDefinitionOptionRel cpDefinitionOptionRel =
				_cpDefinitionOptionRelLocalService.
					fetchCPDefinitionOptionRelByKey(cpDefinitionId, key);

			if (cpDefinitionOptionRel == null) {
				continue;
			}

			JSONArray valueJSONArray = CPJSONUtil.getJSONArray(
				jsonObject, "value");

			for (int j = 0; j < valueJSONArray.length(); j++) {
				String value = valueJSONArray.getString(j);

				CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
					_cpDefinitionOptionValueRelLocalService.
						fetchCPDefinitionOptionValueRel(
							cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
							value);

				if (cpDefinitionOptionValueRel != null) {
					value = cpDefinitionOptionValueRel.getName(locale);
				}
				else {
					value = valueJSONArray.getString(j);
				}

				KeyValuePair keyValuePair = new KeyValuePair();

				keyValuePair.setKey(cpDefinitionOptionRel.getName(locale));
				keyValuePair.setValue(value);

				values.add(keyValuePair);
			}
		}

		return values;
	}

	@Override
	public CPSku toCPSku(CPInstance cpInstance) {
		return new CPSkuImpl(cpInstance);
	}

	private CPInstance _fetchCPInstance(
			long cpDefinitionId, JSONArray skuOptionJSONArray)
		throws PortalException {

		int cpDefinitionOptionRelsCount =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRelsCount(
				cpDefinitionId, true);

		if (cpDefinitionOptionRelsCount == 0) {
			return null;
		}

		Map<Long, List<Long>>
			cpDefinitionOptionRelCPDefinitionOptionValueRelIds =
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
						cpDefinitionId, true, skuOptionJSONArray);

		if (cpDefinitionOptionRelCPDefinitionOptionValueRelIds.isEmpty() ||
			(cpDefinitionOptionRelsCount !=
				cpDefinitionOptionRelCPDefinitionOptionValueRelIds.size())) {

			return null;
		}

		List<CPInstanceOptionValueRel> cpDefinitionCPInstanceOptionValueRels =
			_cpInstanceOptionValueRelLocalService.
				getCPDefinitionCPInstanceOptionValueRels(cpDefinitionId);

		Map<Long, Integer> cpInstanceCPInstanceOptionValueHits =
			new HashMap<>();

		for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
				cpDefinitionCPInstanceOptionValueRels) {

			if (!cpDefinitionOptionRelCPDefinitionOptionValueRelIds.containsKey(
					cpInstanceOptionValueRel.getCPDefinitionOptionRelId())) {

				continue;
			}

			List<Long> cpDefinitionOptionValueIds =
				cpDefinitionOptionRelCPDefinitionOptionValueRelIds.get(
					cpInstanceOptionValueRel.getCPDefinitionOptionRelId());

			if (!cpDefinitionOptionValueIds.contains(
					cpInstanceOptionValueRel.
						getCPDefinitionOptionValueRelId())) {

				continue;
			}

			if (cpInstanceCPInstanceOptionValueHits.containsKey(
					cpInstanceOptionValueRel.getCPInstanceId())) {

				int value = cpInstanceCPInstanceOptionValueHits.get(
					cpInstanceOptionValueRel.getCPInstanceId());

				cpInstanceCPInstanceOptionValueHits.put(
					cpInstanceOptionValueRel.getCPInstanceId(), value + 1);

				continue;
			}

			cpInstanceCPInstanceOptionValueHits.put(
				cpInstanceOptionValueRel.getCPInstanceId(), 1);
		}

		if (cpInstanceCPInstanceOptionValueHits.isEmpty()) {
			return null;
		}

		long cpInstanceId = _getTopId(cpInstanceCPInstanceOptionValueHits);

		if (cpDefinitionOptionRelsCount !=
				cpInstanceCPInstanceOptionValueHits.get(cpInstanceId)) {

			return null;
		}

		return _cpInstanceLocalService.getCPInstance(cpInstanceId);
	}

	private CPInstance _fetchCPInstance(long cpDefinitionId, String json)
		throws PortalException {

		int cpDefinitionOptionRelsCount =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRelsCount(
				cpDefinitionId, true);

		if (cpDefinitionOptionRelsCount == 0) {
			return null;
		}

		Map<Long, List<Long>>
			cpDefinitionOptionRelCPDefinitionOptionValueRelIds =
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
						cpDefinitionId, true, json);

		if (cpDefinitionOptionRelCPDefinitionOptionValueRelIds.isEmpty() ||
			(cpDefinitionOptionRelsCount !=
				cpDefinitionOptionRelCPDefinitionOptionValueRelIds.size())) {

			return null;
		}

		List<CPInstanceOptionValueRel> cpDefinitionCPInstanceOptionValueRels =
			_cpInstanceOptionValueRelLocalService.
				getCPDefinitionCPInstanceOptionValueRels(cpDefinitionId);

		Map<Long, Integer> cpInstanceCPInstanceOptionValueHits =
			new HashMap<>();

		for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
				cpDefinitionCPInstanceOptionValueRels) {

			if (!cpDefinitionOptionRelCPDefinitionOptionValueRelIds.containsKey(
					cpInstanceOptionValueRel.getCPDefinitionOptionRelId())) {

				continue;
			}

			List<Long> cpDefinitionOptionValueIds =
				cpDefinitionOptionRelCPDefinitionOptionValueRelIds.get(
					cpInstanceOptionValueRel.getCPDefinitionOptionRelId());

			if (!cpDefinitionOptionValueIds.contains(
					cpInstanceOptionValueRel.
						getCPDefinitionOptionValueRelId())) {

				continue;
			}

			if (cpInstanceCPInstanceOptionValueHits.containsKey(
					cpInstanceOptionValueRel.getCPInstanceId())) {

				int value = cpInstanceCPInstanceOptionValueHits.get(
					cpInstanceOptionValueRel.getCPInstanceId());

				cpInstanceCPInstanceOptionValueHits.put(
					cpInstanceOptionValueRel.getCPInstanceId(), value + 1);

				continue;
			}

			cpInstanceCPInstanceOptionValueHits.put(
				cpInstanceOptionValueRel.getCPInstanceId(), 1);
		}

		if (cpInstanceCPInstanceOptionValueHits.isEmpty()) {
			return null;
		}

		long cpInstanceId = _getTopId(cpInstanceCPInstanceOptionValueHits);

		if (cpDefinitionOptionRelsCount !=
				cpInstanceCPInstanceOptionValueHits.get(cpInstanceId)) {

			return null;
		}

		return _cpInstanceLocalService.getCPInstance(cpInstanceId);
	}

	private CPInstance _fetchFirstAvailableReplacementCPInstance(
			long accountEntryId, long commerceChannelGroupId,
			long commerceOrderTypeId, CPInstance cpInstance)
		throws PortalException {

		if ((cpInstance == null) || !cpInstance.isDiscontinued() ||
			_cpAvailabilityChecker.check(
				accountEntryId, commerceChannelGroupId, cpInstance,
				StringPool.BLANK,
				_cpDefinitionInventoryEngine.getMinOrderQuantity(
					_getCPConfigurationListId(
						accountEntryId, commerceChannelGroupId,
						commerceOrderTypeId, cpInstance),
					cpInstance))) {

			return cpInstance;
		}

		return _fetchFirstAvailableReplacementCPInstance(
			accountEntryId, commerceChannelGroupId, commerceOrderTypeId,
			_cpInstanceLocalService.fetchCProductInstance(
				cpInstance.getReplacementCProductId(),
				cpInstance.getReplacementCPInstanceUuid()));
	}

	private long _getCPConfigurationListId(
			long accountEntryId, long commerceChannelGroupId,
			long commerceOrderTypeId, CPInstance cpInstance)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceChannelGroupId);

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				cpInstance.getCompanyId(), cpInstance.getGroupId(),
				accountEntryId, commerceChannel.getCommerceChannelId(),
				commerceOrderTypeId);

		return cpConfigurationList.getCPConfigurationListId();
	}

	private long _getTopId(Map<Long, Integer> idIdHits) {
		long topId = 0;

		int topIdHits = 0;

		for (Map.Entry<Long, Integer> idIdHitsEntry : idIdHits.entrySet()) {
			if (topIdHits > idIdHitsEntry.getValue()) {
				continue;
			}

			topId = idIdHitsEntry.getKey();
			topIdHits = idIdHitsEntry.getValue();
		}

		return topId;
	}

	private boolean _hasCPInstanceCPDefinitionOptionValueRelIds(
		long cpInstanceId,
		List<Long> skuCombinationCPDefinitionOptionValueRelIds) {

		for (Long skuCombinationCPDefinitionOptionValueRelId :
				skuCombinationCPDefinitionOptionValueRelIds) {

			if (!_cpInstanceOptionValueRelLocalService.
					hasCPInstanceCPDefinitionOptionValueRel(
						skuCombinationCPDefinitionOptionValueRelId,
						cpInstanceId)) {

				return false;
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPInstanceHelperImpl.class);

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private AMImageHTMLTagFactory _amImageHTMLTagFactory;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceMediaProvider _commerceMediaProvider;

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private CPAvailabilityChecker _cpAvailabilityChecker;

	@Reference
	private CPCollectionProviderHelper _cpCollectionProviderHelper;

	@Reference
	private CPConfigurationListDiscovery _cpConfigurationListDiscovery;

	@Reference
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Reference
	private CPDefinitionInventoryEngine _cpDefinitionInventoryEngine;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}