/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.helper;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.punchout.service.PunchOutReturnService;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.util.CommerceURLValidatorUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaclyn Ong
 */
@Component(
	property = "service.ranking:Integer=100",
	service = PunchOutReturnService.class
)
public class PunchOut2GoReturnServiceImpl implements PunchOutReturnService {

	@Override
	public String returnToPunchOutVendor(
			CommerceOrder commerceOrder, String url)
		throws Exception {

		try {
			if (!CommerceURLValidatorUtil.isValidURL(
					commerceOrder.getCompanyId(), url)) {

				_log.error("Punch out return URL " + url + " is not allowed");

				return null;
			}

			JSONObject cartJSONObject = _jsonFactory.createJSONObject();

			cartJSONObject.put(
				"contract_id", StringPool.BLANK
			).put(
				"discount", commerceOrder.getTotalDiscountAmount()
			).put(
				"discount_description", StringPool.BLANK
			).put(
				"quote_id", StringPool.BLANK
			).put(
				"shipping", commerceOrder.getShippingAmount()
			).put(
				"shipping_description", commerceOrder.getShippingOptionName()
			).put(
				"tax", commerceOrder.getTaxAmount()
			).put(
				"tax_description", StringPool.BLANK
			).put(
				"total", commerceOrder.getTotal()
			);

			CommerceCurrency commerceCurrency =
				commerceOrder.getCommerceCurrency();

			cartJSONObject.put("currencyCode", commerceCurrency.getCode());

			JSONArray cartItemJSONArray = _jsonFactory.createJSONArray();

			for (CommerceOrderItem commerceOrderItem :
					commerceOrder.getCommerceOrderItems()) {

				JSONObject cartItemJSONObject = _jsonFactory.createJSONObject();

				cartItemJSONObject.put(
					"skuid", commerceOrderItem.getCPInstanceId()
				).put(
					"supplierauxid",
					commerceOrder.getCommerceOrderId() + "/" +
						commerceOrderItem.getCommerceOrderItemId()
				).put(
					"supplierid", commerceOrderItem.getSku()
				);

				CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();

				cartItemJSONObject.put(
					"description", cpDefinition.getDescription());

				StringBundler assetCategorySB = new StringBundler();

				AssetEntry assetEntry = _assetEntryLocalService.getEntry(
					CPDefinition.class.getName(),
					cpDefinition.getCPDefinitionId());

				for (AssetCategory assetCategory : assetEntry.getCategories()) {
					assetCategorySB.append(assetCategory.getName());

					assetCategorySB.append(StringPool.COMMA);
				}

				cartItemJSONObject.put(
					"classification", assetCategorySB.toString()
				).put(
					"commercePriceListId",
					commerceOrderItem.getCommercePriceListId()
				).put(
					"deliveryGroup", commerceOrderItem.getDeliveryGroupName()
				).put(
					"deliveryGroupName",
					commerceOrderItem.getDeliveryGroupName()
				).put(
					"discountAmount", commerceOrderItem.getDiscountAmount()
				).put(
					"externalReferenceCode",
					commerceOrderItem.getExternalReferenceCode()
				).put(
					"finalPrice", commerceOrderItem.getFinalPrice()
				).put(
					"parentCommerceOrderItemId",
					commerceOrderItem.getParentCommerceOrderItemId()
				).put(
					"printedNote", commerceOrderItem.getPrintedNote()
				).put(
					"promoPrice", commerceOrderItem.getPromoPrice()
				).put(
					"quantity", commerceOrderItem.getQuantity()
				).put(
					"shippedQuantity", commerceOrderItem.getShippedQuantity()
				).put(
					"unitprice", commerceOrderItem.getUnitPrice()
				).put(
					"uom", "EA"
				);

				JSONObject shippingAddressJSONObject =
					_buildShippingAddressJSONObject(
						commerceOrderItem.getShippingAddressId());

				cartItemJSONObject.put(
					"shippingAddress", shippingAddressJSONObject);

				CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
					commerceOrderItem.getCPInstanceId());

				cartItemJSONObject.put("unspsc", cpInstance.getUnspsc());

				String formattedCreateDate = _getDateString(
					commerceOrderItem.getCreateDate());

				cartItemJSONObject.put("createDate", formattedCreateDate);

				String formattedModifiedDate = _getDateString(
					commerceOrderItem.getModifiedDate());

				cartItemJSONObject.put("modifiedDate", formattedModifiedDate);

				String formattedRequestedDeliveryDate = _getDateString(
					commerceOrderItem.getRequestedDeliveryDate());

				cartItemJSONObject.put(
					"requestedDeliveryDate", formattedRequestedDeliveryDate);

				cartItemJSONArray.put(cartItemJSONObject);
			}

			cartJSONObject.put("items", cartItemJSONArray);

			String cartJSON = cartJSONObject.toString();

			if (_log.isDebugEnabled()) {
				String stringBundler = StringBundler.concat(
					"PunchOut2Go cart transfer request to ", url,
					"; cart JSON: ", cartJSON);

				_log.debug(stringBundler);
			}

			URL urlObj = new URL(url);

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlObj.openConnection();

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty(
				"Content-type", "application/json");

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				httpURLConnection.getOutputStream());

			outputStreamWriter.write(cartJSON);

			outputStreamWriter.flush();

			int responseCode = httpURLConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String response = StringUtil.toLowerCase(
					StringUtil.read(httpURLConnection.getInputStream()));

				if (_log.isDebugEnabled()) {
					_log.debug(
						"JSON response received from PunchOut2Go: " + response);
				}

				JSONArray jsonArray = _jsonFactory.createJSONArray(
					StringPool.OPEN_BRACKET + response +
						StringPool.CLOSE_BRACKET);

				JSONObject jsonObject = jsonArray.getJSONObject(0);

				return jsonObject.getString("redirecturl");
			}

			_log.error(
				"PunchOut2Go cart transfer response code: " + responseCode);

			return null;
		}
		catch (Exception exception) {
			_log.error("PunchOut2Go cart transfer failed", exception);

			throw exception;
		}
	}

	private JSONObject _buildCountryJSONObject(long countryId) {
		JSONObject countryJSONObject = _jsonFactory.createJSONObject();

		countryJSONObject.put("id", countryId);

		if (countryId < 1) {
			return countryJSONObject;
		}

		Country country = _countryLocalService.fetchCountry(countryId);

		if (country == null) {
			return countryJSONObject;
		}

		countryJSONObject.put(
			"name", country.getName()
		).put(
			"numericISOCode", country.getNumber()
		).put(
			"threeLettersISOCode", country.getA3()
		).put(
			"twoLettersISOCode", country.getA2()
		);

		return countryJSONObject;
	}

	private JSONObject _buildRegionJSONObject(long regionId) {
		JSONObject regionJSONObject = _jsonFactory.createJSONObject();

		regionJSONObject.put("id", regionId);

		if (regionId < 1) {
			return regionJSONObject;
		}

		Region region = _regionLocalService.fetchRegion(regionId);

		if (region == null) {
			return regionJSONObject;
		}

		regionJSONObject.put(
			"code", region.getRegionCode()
		).put(
			"name", region.getName()
		);

		return regionJSONObject;
	}

	private JSONObject _buildShippingAddressJSONObject(long shippingAddressId) {
		JSONObject shippingAddressJSONObject = _jsonFactory.createJSONObject();

		shippingAddressJSONObject.put("shippingAddressId", shippingAddressId);

		if (shippingAddressId < 1) {
			return shippingAddressJSONObject;
		}

		CommerceAddress shippingAddress =
			_commerceAddressLocalService.fetchCommerceAddress(
				shippingAddressId);

		if (shippingAddress == null) {
			return shippingAddressJSONObject;
		}

		shippingAddressJSONObject.put(
			"city", shippingAddress.getCity()
		).put(
			"externalReferenceCode", shippingAddress.getExternalReferenceCode()
		).put(
			"name", shippingAddress.getName()
		).put(
			"street1", shippingAddress.getStreet1()
		).put(
			"street2", shippingAddress.getStreet2()
		).put(
			"street3", shippingAddress.getStreet3()
		);

		JSONObject regionJSONObject = _buildRegionJSONObject(
			shippingAddress.getRegionId());

		shippingAddressJSONObject.put(
			"region", regionJSONObject
		).put(
			"zip", shippingAddress.getZip()
		);

		JSONObject countryJSONObject = _buildCountryJSONObject(
			shippingAddress.getCountryId());

		shippingAddressJSONObject.put(
			"companyId", shippingAddress.getCompanyId()
		).put(
			"country", countryJSONObject
		).put(
			"description", shippingAddress.getDescription()
		).put(
			"groupId", shippingAddress.getGroupId()
		).put(
			"isDefaultBilling", shippingAddress.isDefaultBilling()
		).put(
			"isDefaultShipping", shippingAddress.isDefaultShipping()
		).put(
			"isGeolocated", shippingAddress.isGeolocated()
		).put(
			"latitude", shippingAddress.getLatitude()
		).put(
			"longitude", shippingAddress.getLongitude()
		).put(
			"phoneNumber", shippingAddress.getPhoneNumber()
		).put(
			"type", shippingAddress.getType()
		);

		String formattedCreateDate = _getDateString(
			shippingAddress.getCreateDate());

		shippingAddressJSONObject.put("createDate", formattedCreateDate);

		String formattedModifiedDate = _getDateString(
			shippingAddress.getModifiedDate());

		shippingAddressJSONObject.put("modifiedDate", formattedModifiedDate);

		return shippingAddressJSONObject;
	}

	private String _getDateString(Date date) {
		if (date == null) {
			return StringPool.BLANK;
		}

		return _dateFormat.format(date);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PunchOut2GoReturnServiceImpl.class);

	private static final DateFormat _dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss'Z'");

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RegionLocalService _regionLocalService;

}