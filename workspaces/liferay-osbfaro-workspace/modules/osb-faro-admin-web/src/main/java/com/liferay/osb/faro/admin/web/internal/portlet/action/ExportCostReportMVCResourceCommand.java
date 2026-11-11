/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.admin.web.internal.portlet.action;

import com.liferay.osb.faro.admin.web.internal.constants.FaroAdminPortletKeys;
import com.liferay.osb.faro.admin.web.internal.util.FaroProjectOwnerUtil;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.BigQueryPrice;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroProjectUsageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionList;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.CSVUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Time;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FaroAdminPortletKeys.FARO_ADMIN,
		"mvc.command.name=/faro_admin/export_cost_report"
	},
	service = MVCResourceCommand.class
)
public class ExportCostReportMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append(
			StringBundler.concat(
				"Workspace,Project ID,Customer ID,Customer Name,Owner,",
				"Subscription,Location,Events,Billable Events,",
				"A/B Testing Events,Page Views,Anonymous Individuals,",
				"Known Individuals,Query Cost (USD),Query Cost Data Since,",
				"Storage Cost (USD),Storage Cost Data Since,Total Cost (USD)\n"));

		long startTime = System.currentTimeMillis() - Time.YEAR;

		Map<Long, Long> queryUsageTimes = _getFirstUsageTimes(
			"onDemandBytesBilled", "reservationSlotMs", startTime);
		Map<Long, Long> storageUsageTimes = _getFirstUsageTimes(
			"activeLogicalBytes", "longTermLogicalBytes", startTime);

		Map<String, BigQueryPrice> bigQueryPrices = new HashMap<>();

		for (Object[] values : _getFaroProjectUsageTotals(startTime)) {
			FaroProject faroProject = _faroProjectLocalService.fetchFaroProject(
				(Long)values[0]);

			if (faroProject == null) {
				continue;
			}

			Map<String, Long> totals = new HashMap<>();

			for (int i = 0; i < _PROPERTY_NAMES.length; i++) {
				totals.put(_PROPERTY_NAMES[i], GetterUtil.getLong(values[i + 1]));
			}

			sb.append(_encode(faroProject.getName()));
			sb.append(StringPool.COMMA);
			sb.append(_encode(faroProject.getProjectId()));
			sb.append(StringPool.COMMA);
			sb.append(_encode(faroProject.getCorpProjectUuid()));
			sb.append(StringPool.COMMA);
			sb.append(_encode(faroProject.getCorpProjectName()));
			sb.append(StringPool.COMMA);
			sb.append(
				_encode(FaroProjectOwnerUtil.fetchOwner(faroProject)));
			sb.append(StringPool.COMMA);
			sb.append(_encode(faroProject.getShortSubscriptionName()));
			sb.append(StringPool.COMMA);

			BigQueryPrice bigQueryPrice = bigQueryPrices.computeIfAbsent(
				faroProject.getServerLocation(),
				serverLocation -> _contactsEngineClient.getBigQueryPrice(
					faroProject));

			sb.append(_encode(bigQueryPrice.getLocation()));

			sb.append(StringPool.COMMA);
			sb.append(totals.get("eventsCount"));
			sb.append(StringPool.COMMA);
			sb.append(totals.get("billableEventsCount"));
			sb.append(StringPool.COMMA);
			sb.append(totals.get("experimentEventsCount"));
			sb.append(StringPool.COMMA);
			sb.append(totals.get("pageViewsCount"));
			sb.append(StringPool.COMMA);
			sb.append(totals.get("anonymousIndividualsCount"));
			sb.append(StringPool.COMMA);
			sb.append(totals.get("knownIndividualsCount"));
			sb.append(StringPool.COMMA);

			BigDecimal onDemandCost = _getCost(
				totals.get("onDemandBytesBilled"), _BYTES_PER_TIB,
				bigQueryPrice.getOnDemandPricePerTiB());
			BigDecimal reservationCost = _getCost(
				totals.get("reservationSlotMs"), _MILLISECONDS_PER_HOUR,
				bigQueryPrice.getReservationPricePerSlotHour());

			BigDecimal queryCost = onDemandCost.add(reservationCost);

			sb.append(_toString(queryCost));

			sb.append(StringPool.COMMA);
			sb.append(
				_formatUsageTime(
					queryUsageTimes.get(faroProject.getFaroProjectId())));
			sb.append(StringPool.COMMA);

			BigDecimal activeLogicalStorageCost = _getCost(
				totals.get("activeLogicalBytes"), _BYTE_DAYS_PER_GIB_MONTH,
				bigQueryPrice.getActiveLogicalStoragePricePerGiBMonth());
			BigDecimal longTermLogicalStorageCost = _getCost(
				totals.get("longTermLogicalBytes"), _BYTE_DAYS_PER_GIB_MONTH,
				bigQueryPrice.getLongTermLogicalStoragePricePerGiBMonth());

			BigDecimal storageCost = activeLogicalStorageCost.add(
				longTermLogicalStorageCost);

			sb.append(_toString(storageCost));

			sb.append(StringPool.COMMA);
			sb.append(
				_formatUsageTime(
					storageUsageTimes.get(faroProject.getFaroProjectId())));
			sb.append(StringPool.COMMA);
			sb.append(_toString(queryCost.add(storageCost)));
			sb.append(StringPool.NEW_LINE);
		}

		String csv = sb.toString();

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, "cost-report.csv",
			csv.getBytes(StringPool.UTF8), ContentTypes.TEXT_CSV_UTF8);
	}

	private String _encode(String value) {
		if (value == null) {
			return StringPool.BLANK;
		}

		return CSVUtil.encode(value);
	}

	private String _formatUsageTime(Long usageTime) {
		if (usageTime == null) {
			return StringPool.BLANK;
		}

		Instant instant = Instant.ofEpochMilli(usageTime);

		ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);

		LocalDate localDate = zonedDateTime.toLocalDate();

		return localDate.toString();
	}

	private BigDecimal _getCost(
		long amount, BigDecimal divisor, BigDecimal price) {

		BigDecimal amountBigDecimal = BigDecimal.valueOf(amount);

		BigDecimal quantityBigDecimal = amountBigDecimal.divide(
			divisor, MathContext.DECIMAL64);

		return quantityBigDecimal.multiply(price);
	}

	private List<Object[]> _getFaroProjectUsageTotals(long startTime) {
		DynamicQuery dynamicQuery =
			_faroProjectUsageLocalService.dynamicQuery();

		Property property = PropertyFactoryUtil.forName("usageTime");

		dynamicQuery.add(property.ge(startTime));

		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		projectionList.add(
			ProjectionFactoryUtil.groupProperty("faroProjectId"));

		for (String propertyName : _PROPERTY_NAMES) {
			projectionList.add(ProjectionFactoryUtil.sum(propertyName));
		}

		dynamicQuery.setProjection(projectionList);

		return _faroProjectUsageLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Returns the first usage time in the report window with a nonzero value
	 * for either property, per Faro project. Compute usage can only be
	 * backfilled for about 180 days and storage usage starts at the Asah
	 * deploy, so the cost of a workspace may cover less than 12 months.
	 */
	private Map<Long, Long> _getFirstUsageTimes(
		String propertyName1, String propertyName2, long startTime) {

		DynamicQuery dynamicQuery =
			_faroProjectUsageLocalService.dynamicQuery();

		Property property1 = PropertyFactoryUtil.forName(propertyName1);
		Property property2 = PropertyFactoryUtil.forName(propertyName2);

		dynamicQuery.add(
			RestrictionsFactoryUtil.or(property1.gt(0L), property2.gt(0L)));

		Property usageTimeProperty = PropertyFactoryUtil.forName("usageTime");

		dynamicQuery.add(usageTimeProperty.ge(startTime));

		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		projectionList.add(
			ProjectionFactoryUtil.groupProperty("faroProjectId"));
		projectionList.add(ProjectionFactoryUtil.min("usageTime"));

		dynamicQuery.setProjection(projectionList);

		Map<Long, Long> usageTimes = new HashMap<>();

		for (Object[] values :
				_faroProjectUsageLocalService.<Object[]>dynamicQuery(
					dynamicQuery)) {

			usageTimes.put((Long)values[0], (Long)values[1]);
		}

		return usageTimes;
	}

	private String _toString(BigDecimal bigDecimal) {
		BigDecimal scaledBigDecimal = bigDecimal.setScale(
			2, RoundingMode.HALF_UP);

		return scaledBigDecimal.toPlainString();
	}

	/**
	 * Storage is priced per GiB month and the daily usage rows add up to byte
	 * days, so a GiB month is a GiB stored for an average month of 365/12 days.
	 */
	private static final BigDecimal _BYTE_DAYS_PER_GIB_MONTH;

	private static final BigDecimal _BYTES_PER_TIB = BigDecimal.valueOf(
		1L << 40);

	private static final BigDecimal _MILLISECONDS_PER_HOUR = BigDecimal.valueOf(
		Time.HOUR);

	private static final String[] _PROPERTY_NAMES = {
		"activeLogicalBytes", "anonymousIndividualsCount",
		"billableEventsCount", "eventsCount", "experimentEventsCount",
		"knownIndividualsCount", "longTermLogicalBytes", "onDemandBytesBilled",
		"pageViewsCount", "reservationSlotMs"
	};

	static {
		BigDecimal byteDaysPerGiBYear = BigDecimal.valueOf((1L << 30) * 365);

		_BYTE_DAYS_PER_GIB_MONTH = byteDaysPerGiBYear.divide(
			BigDecimal.valueOf(12), MathContext.DECIMAL64);
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private FaroProjectUsageLocalService _faroProjectUsageLocalService;

}