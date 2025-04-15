/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.address;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.liferay.commerce.country.CommerceCountryManager;
import com.liferay.commerce.frontend.internal.address.model.CountryModel;
import com.liferay.commerce.frontend.internal.address.model.RegionModel;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = AddressResource.class)
public class AddressResource {

	@GET
	@Path("/address/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommerceAddress(
		@PathParam("id") long commerceAddressId) {

		try {
			CommerceAddress commerceAddress =
				_commerceAddressService.fetchCommerceAddress(commerceAddressId);

			if (commerceAddress != null) {
				String json = _OBJECT_MAPPER.writeValueAsString(
					_jsonFactory.looseSerialize(commerceAddress));

				return Response.ok(
					json, MediaType.APPLICATION_JSON
				).build();
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Response.status(
			Response.Status.INTERNAL_SERVER_ERROR
		).build();
	}

	@GET
	@Path("/address/regions/{countryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegions(
		@PathParam("countryId") long countryId,
		@Context ThemeDisplay themeDisplay) {

		List<RegionModel> regionModels = new ArrayList<>();

		List<Region> regions = _regionService.getRegions(countryId, true);

		for (Region region : regions) {
			regionModels.add(
				new RegionModel(region.getRegionId(), region.getName()));
		}

		try {
			String json = _OBJECT_MAPPER.writeValueAsString(regionModels);

			return Response.ok(
				json, MediaType.APPLICATION_JSON
			).build();
		}
		catch (JsonProcessingException jsonProcessingException) {
			_log.error(jsonProcessingException);
		}

		return Response.status(
			Response.Status.INTERNAL_SERVER_ERROR
		).build();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), passing companyId is redundant
	 */
	@Deprecated
	@GET
	@Path("/address/countries")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getShippingCountries(
		@QueryParam("companyId") long companyId,
		@Context ThemeDisplay themeDisplay) {

		return _getCountries(
			_countryService.getCompanyCountries(companyId, true),
			themeDisplay.getLanguageId());
	}

	@GET
	@Path("/address/countries")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getShippingCountries(@Context ThemeDisplay themeDisplay) {
		List<Country> countries = _countryService.getCompanyCountries(
			themeDisplay.getCompanyId(), true);

		return _getCountries(countries, themeDisplay.getLanguageId());
	}

	@GET
	@Path("/address/countries-by-channel-id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getShippingCountriesByChannelId(
		@QueryParam("channelId") long channelId,
		@Context ThemeDisplay themeDisplay) {

		List<Country> countries =
			_commerceCountryManager.getShippingCountriesByChannelId(
				channelId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		return _getCountries(countries, themeDisplay.getLanguageId());
	}

	private Response _getCountries(List<Country> countries, String languageId) {
		List<CountryModel> countryModels = new ArrayList<>();

		for (Country country : countries) {
			countryModels.add(
				new CountryModel(
					country.getCountryId(), country.getTitle(languageId),
					country.isBillingAllowed(), country.isShippingAllowed()));
		}

		try {
			String json = _OBJECT_MAPPER.writeValueAsString(countryModels);

			return Response.ok(
				json, MediaType.APPLICATION_JSON
			).build();
		}
		catch (JsonProcessingException jsonProcessingException) {
			_log.error(jsonProcessingException);
		}

		return Response.status(
			Response.Status.INTERNAL_SERVER_ERROR
		).build();
	}

	private static final ObjectMapper _OBJECT_MAPPER = new ObjectMapper() {
		{
			configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
			enable(SerializationFeature.INDENT_OUTPUT);
		}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		AddressResource.class);

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceCountryManager _commerceCountryManager;

	@Reference
	private CountryService _countryService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RegionService _regionService;

}