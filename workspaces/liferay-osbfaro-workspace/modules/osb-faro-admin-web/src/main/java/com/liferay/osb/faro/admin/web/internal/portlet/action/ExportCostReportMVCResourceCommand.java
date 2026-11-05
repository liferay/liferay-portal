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
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.CSVUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Time;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

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
				"Workspace,Project ID,Owner,Subscription,Location,Events,",
				"Anonymous Individuals,Query Cost (USD),Storage Cost (USD),",
				"Total Cost (USD)\n"));

		Map<String, BigQueryPrice> bigQueryPrices = new HashMap<>();

		for (Object[] values : _getFaroProjectUsageTotals()) {
			FaroProject faroProject = _faroProjectLocalService.fetchFaroProject(
				(Long)values[0]);

			if (faroProject == null) {
				continue;
			}

			sb.append(CSVUtil.encode(faroProject.getName()));
			sb.append(StringPool.COMMA);
			sb.append(CSVUtil.encode(faroProject.getProjectId()));
			sb.append(StringPool.COMMA);
			sb.append(
				CSVUtil.encode(FaroProjectOwnerUtil.fetchOwner(faroProject)));
			sb.append(StringPool.COMMA);
			sb.append(CSVUtil.encode(faroProject.getShortSubscriptionName()));
			sb.append(StringPool.COMMA);

			BigQueryPrice bigQueryPrice = bigQueryPrices.computeIfAbsent(
				faroProject.getServerLocation(),
				serverLocation -> _contactsEngineClient.getBigQueryPrice(
					faroProject));

			sb.append(CSVUtil.encode(bigQueryPrice.getLocation()));

			sb.append(StringPool.COMMA);
			sb.append(values[3]);
			sb.append(StringPool.COMMA);
			sb.append(values[2]);
			sb.append(StringPool.COMMA);

			BigDecimal onDemandCost = _getCost(
				(Long)values[5], _BYTES_PER_TIB,
				bigQueryPrice.getOnDemandPricePerTiB());
			BigDecimal reservationCost = _getCost(
				(Long)values[6], _MILLISECONDS_PER_HOUR,
				bigQueryPrice.getReservationPricePerSlotHour());

			BigDecimal queryCost = onDemandCost.add(reservationCost);

			sb.append(_toString(queryCost));

			sb.append(StringPool.COMMA);

			BigDecimal activeLogicalStorageCost = _getCost(
				(Long)values[1], _BYTE_DAYS_PER_GIB_MONTH,
				bigQueryPrice.getActiveLogicalStoragePricePerGiBMonth());
			BigDecimal longTermLogicalStorageCost = _getCost(
				(Long)values[4], _BYTE_DAYS_PER_GIB_MONTH,
				bigQueryPrice.getLongTermLogicalStoragePricePerGiBMonth());

			BigDecimal storageCost = activeLogicalStorageCost.add(
				longTermLogicalStorageCost);

			sb.append(_toString(storageCost));

			sb.append(StringPool.COMMA);
			sb.append(_toString(queryCost.add(storageCost)));
			sb.append(StringPool.NEW_LINE);
		}

		String csv = sb.toString();

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, "cost-report.csv",
			csv.getBytes(StringPool.UTF8), ContentTypes.TEXT_CSV_UTF8);
	}

	private BigDecimal _getCost(
		long amount, BigDecimal divisor, BigDecimal price) {

		BigDecimal amountBigDecimal = BigDecimal.valueOf(amount);

		BigDecimal quantityBigDecimal = amountBigDecimal.divide(
			divisor, MathContext.DECIMAL64);

		return quantityBigDecimal.multiply(price);
	}

	private List<Object[]> _getFaroProjectUsageTotals() {
		DynamicQuery dynamicQuery =
			_faroProjectUsageLocalService.dynamicQuery();

		Property property = PropertyFactoryUtil.forName("usageTime");

		dynamicQuery.add(property.ge(System.currentTimeMillis() - Time.YEAR));

		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		projectionList.add(
			ProjectionFactoryUtil.groupProperty("faroProjectId"));
		projectionList.add(ProjectionFactoryUtil.sum("activeLogicalBytes"));
		projectionList.add(
			ProjectionFactoryUtil.sum("anonymousIndividualsCount"));
		projectionList.add(ProjectionFactoryUtil.sum("eventsCount"));
		projectionList.add(ProjectionFactoryUtil.sum("longTermLogicalBytes"));
		projectionList.add(ProjectionFactoryUtil.sum("onDemandBytesBilled"));
		projectionList.add(ProjectionFactoryUtil.sum("reservationSlotMs"));

		dynamicQuery.setProjection(projectionList);

		return _faroProjectUsageLocalService.dynamicQuery(dynamicQuery);
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