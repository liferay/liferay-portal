/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.customer.constants.ExternalLinkConstants;
import com.liferay.customer.constants.HeatTagConstants;
import com.liferay.customer.permission.BusinessEventPermission;
import com.liferay.customer.service.KoroneikiService;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ExternalLink;
import com.liferay.osb.spring.boot.client.zendesk.model.ZendeskTicket;
import com.liferay.osb.spring.boot.client.zendesk.search.SearchHits;
import com.liferay.osb.spring.boot.client.zendesk.search.ZendeskTicketQuery;
import com.liferay.osb.spring.boot.client.zendesk.service.ZendeskService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jenny Chen
 */
@RestController
public class AccountsSyncBusinessEventsRestController
	extends BaseRestController {

	@RequestMapping(
		method = RequestMethod.POST,
		path = "/accounts/{externalReferenceCode}/sync-business-events"
	)
	public ResponseEntity<String> post(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("externalReferenceCode") String externalReferenceCode,
			@RequestBody String json)
		throws Exception {

		try {
			_businessEventPermission.check(
				jwt, externalReferenceCode, ActionKeys.UPDATE);

			JSONObject jsonObject = new JSONObject(json);

			JSONArray jsonArray = jsonObject.getJSONArray("businessEvents");

			_updateZendesk(
				_fetchZendeskOrganizationId(externalReferenceCode),
				_getBusinessEventsSummary(jsonArray),
				_getAssociatedTicketIds(jsonArray),
				_getHighestHeatTag(jsonArray));

			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to update Zendesk business events for " +
					externalReferenceCode,
				exception);

			return new ResponseEntity(
				exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void scheduledHeatTagUpdate() throws Exception {
		int page = 1;

		Set<String> syncedAccountExternalReferenceCodes = new HashSet<>();

		while (page > 0) {
			JSONObject jsonObject = _getBusinessEventsJSONObject(
				"eventStatus ne 'canceled' and eventStatus ne 'completed'",
				page, 500, StringPool.BLANK);

			JSONArray jsonArray = jsonObject.getJSONArray("items");

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject businessEventJSONObject = jsonArray.getJSONObject(i);

				String externalReferenceCode =
					businessEventJSONObject.getString(
						"accountEntryToBusinessEventsERC");

				if (!syncedAccountExternalReferenceCodes.contains(
						externalReferenceCode)) {

					_updateAccountHeatTags(externalReferenceCode);

					syncedAccountExternalReferenceCodes.add(
						externalReferenceCode);
				}
			}

			if (jsonObject.getInt("lastPage") == page) {
				page = 0;
			}
			else {
				page += 1;
			}
		}
	}

	private long _fetchZendeskOrganizationId(String externalReferenceCode)
		throws Exception {

		List<ExternalLink> externalLinks = _koroneikiService.fetchExternalLinks(
			externalReferenceCode, 1, 1000);

		for (ExternalLink externalLink : externalLinks) {
			String domain = externalLink.getDomain();
			String entityName = externalLink.getEntityName();

			if (domain.equals(ExternalLinkConstants.DOMAIN_ZENDESK) &&
				entityName.equals(
					ExternalLinkConstants.ENTITY_NAME_ZENDESK_ORGANIZATION)) {

				return GetterUtil.getLong(externalLink.getEntityId());
			}
		}

		return 0;
	}

	private Long[] _getAssociatedTicketIds(JSONArray jsonArray) {
		Set<Long> associatedTicketIds = new HashSet<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray associatedTicketIdsJSONArray = jsonObject.getJSONArray(
				"associatedTicketIds");

			for (int j = 0; j < associatedTicketIdsJSONArray.length(); j++) {
				associatedTicketIds.add(
					associatedTicketIdsJSONArray.getLong(j));
			}
		}

		return associatedTicketIds.toArray(new Long[0]);
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-customer-etc-spring-boot-oahs");
	}

	private JSONObject _getBusinessEventsJSONObject(
			String filterString, int page, int pageSize, String sortString)
		throws Exception {

		StringBundler sb = new StringBundler(8);

		sb.append("/o/c/businessevents?filter=");

		if (Validator.isNotNull(filterString)) {
			sb.append(filterString);
		}

		sb.append("&page=");
		sb.append(page);
		sb.append("&pageSize=");
		sb.append(pageSize);

		if (Validator.isNotNull(sortString)) {
			sb.append("&sort=");
			sb.append(sortString);
		}

		return new JSONObject(get(_getAuthorization(), sb.toString()));
	}

	private String _getBusinessEventsSummary(JSONArray jsonArray) {
		List<String> businessEvents = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			List<String> businessEventFieldValues = new ArrayList<>();

			Iterator<String> iterator = jsonObject.keys();

			while (iterator.hasNext()) {
				String key = iterator.next();

				if (key.equals("associatedTicketIds")) {
					continue;
				}

				if (key.equals("eventType")) {
					JSONObject typeJSONObject = jsonObject.optJSONObject(key);

					businessEventFieldValues.add(
						"type: " + typeJSONObject.getString("name"));
				}
				else if (Validator.isNotNull(jsonObject.optString(key))) {
					businessEventFieldValues.add(
						key + ": " + jsonObject.getString(key));
				}
			}

			if (!businessEventFieldValues.isEmpty()) {
				businessEvents.add(
					StringUtil.merge(businessEventFieldValues, ",\n"));
			}
		}

		return StringUtil.merge(businessEvents, "\n\n");
	}

	private String _getHeatTag(JSONObject jsonObject) {
		JSONObject eventTypeJSONObject = jsonObject.getJSONObject("eventType");

		String targetGoLiveDateTime = jsonObject.getString(
			"targetGoLiveDateTime");

		return HeatTagConstants.getHeatTag(
			eventTypeJSONObject.getString("key"),
			ChronoUnit.DAYS.between(
				LocalDate.now(),
				LocalDate.parse(targetGoLiveDateTime.substring(0, 10))));
	}

	private String _getHighestHeatTag(JSONArray jsonArray) {
		String highestHeatTag = StringPool.BLANK;

		for (int i = 0; i < jsonArray.length(); i++) {
			String heatTag = _getHeatTag(jsonArray.getJSONObject(i));

			if (HeatTagConstants.getScore(highestHeatTag) <=
					HeatTagConstants.getScore(heatTag)) {

				highestHeatTag = heatTag;
			}
		}

		return highestHeatTag;
	}

	private void _updateAccountHeatTags(String externalReferenceCode)
		throws Exception {

		int page = 1;

		while (page > 0) {
			JSONObject jsonObject = _getBusinessEventsJSONObject(
				StringBundler.concat(
					"eventStatus ne 'canceled' and eventStatus ne 'completed' ",
					"and r_accountEntryToBusinessEvents_accountEntryERC eq '",
					externalReferenceCode, "'"),
				page, 500, "targetGoLiveDateTime:asc");

			_updateZendeskTickets(
				_fetchZendeskOrganizationId(externalReferenceCode),
				_getHighestHeatTag(jsonObject.getJSONArray("items")));

			if (jsonObject.getInt("lastPage") == page) {
				page = 0;
			}
			else {
				page += 1;
			}
		}
	}

	private void _updateZendesk(
			long zendeskOrganizationId, String businessEvents,
			Long[] associatedTicketIds, String highestHeatTag)
		throws Exception {

		_zendeskService.updateZendeskOrganization(
			zendeskOrganizationId, businessEvents);

		ZendeskTicketQuery zendeskTicketQuery = new ZendeskTicketQuery();

		zendeskTicketQuery.addCriterion(
			"organization:" + zendeskOrganizationId);
		zendeskTicketQuery.addCriterion("status<closed");

		int page = 1;

		while (page > 0) {
			zendeskTicketQuery.setPage(page);

			SearchHits<ZendeskTicket> searchHits = _zendeskService.search(
				zendeskTicketQuery);

			for (ZendeskTicket zendeskTicket : searchHits.getResults()) {
				Map<Long, String> customFields =
					zendeskTicket.getCustomFields();

				String heatTag = customFields.get(_zendeskHeatTagTicketFieldId);

				if ((HeatTagConstants.getScore(heatTag) <=
						HeatTagConstants.getScore(highestHeatTag)) &&
					!heatTag.equals(highestHeatTag)) {

					customFields.put(
						_zendeskHeatTagTicketFieldId, highestHeatTag);
				}

				customFields.put(
					_zendeskBusinessEventTicketFieldId, businessEvents);

				Set<String> tags = zendeskTicket.getTags();

				tags.remove("impacting_business_event");

				if (ArrayUtil.contains(
						associatedTicketIds,
						zendeskTicket.getZendeskTicketId())) {

					tags.add("impacting_business_event");
				}

				_zendeskService.updateZendeskTicket(
					zendeskTicket.getZendeskTicketId(), zendeskOrganizationId,
					zendeskTicket.getRequesterId(), zendeskTicket.getStatus(),
					customFields, tags);
			}

			page = searchHits.getNextPage();
		}
	}

	private void _updateZendeskTickets(
			long zendeskOrganizationId, String highestHeatTag)
		throws Exception {

		ZendeskTicketQuery zendeskTicketQuery = new ZendeskTicketQuery();

		zendeskTicketQuery.addCriterion(
			"organization:" + zendeskOrganizationId);
		zendeskTicketQuery.addCriterion("status<solved");

		int page = 1;

		while (page > 0) {
			zendeskTicketQuery.setPage(page);

			SearchHits<ZendeskTicket> searchHits = _zendeskService.search(
				zendeskTicketQuery);

			for (ZendeskTicket zendeskTicket : searchHits.getResults()) {
				Map<Long, String> customFields =
					zendeskTicket.getCustomFields();

				String heatTag = customFields.get(_zendeskHeatTagTicketFieldId);

				if ((HeatTagConstants.getScore(heatTag) <=
						HeatTagConstants.getScore(highestHeatTag)) &&
					!heatTag.equals(highestHeatTag)) {

					customFields.put(
						_zendeskHeatTagTicketFieldId, highestHeatTag);

					_zendeskService.updateZendeskTicket(
						zendeskTicket.getZendeskTicketId(),
						zendeskOrganizationId, zendeskTicket.getRequesterId(),
						zendeskTicket.getStatus(), customFields,
						zendeskTicket.getTags());
				}
			}

			page = searchHits.getNextPage();
		}
	}

	private static final Log _log = LogFactory.getLog(
		AccountsSyncBusinessEventsRestController.class);

	@Autowired
	private BusinessEventPermission _businessEventPermission;

	@Autowired
	private KoroneikiService _koroneikiService;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${liferay.customer.zendesk.business.event.ticket.field.id}")
	private long _zendeskBusinessEventTicketFieldId;

	@Value("${liferay.customer.zendesk.heat.tag.ticket.field.id}")
	private long _zendeskHeatTagTicketFieldId;

	@Autowired
	private ZendeskService _zendeskService;

}