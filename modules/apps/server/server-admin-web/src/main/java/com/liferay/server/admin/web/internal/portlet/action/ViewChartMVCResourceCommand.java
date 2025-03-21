/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import java.util.ResourceBundle;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 * @author Philip Jones
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/server_admin/view_chart"
	},
	service = MVCResourceCommand.class
)
public class ViewChartMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String type = ParamUtil.getString(resourceRequest, "type", "max");
		long usedMemory = ParamUtil.getLong(resourceRequest, "usedMemory");

		ValueDataset valueDataset = null;

		StringBundler sb = new StringBundler(5);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", themeDisplay.getLocale(), getClass());

		sb.append(ResourceBundleUtil.getString(resourceBundle, "used-memory"));

		sb.append(StringPool.SPACE);
		sb.append(StringPool.FORWARD_SLASH);
		sb.append(StringPool.SPACE);

		if (type.equals("total")) {
			long totalMemory = ParamUtil.getLong(
				resourceRequest, "totalMemory");

			valueDataset = new DefaultValueDataset(
				(usedMemory * 100) / totalMemory);

			sb.append(
				ResourceBundleUtil.getString(resourceBundle, "total-memory"));
		}
		else {
			long maxMemory = ParamUtil.getLong(resourceRequest, "maxMemory");

			valueDataset = new DefaultValueDataset(
				(usedMemory * 100) / maxMemory);

			sb.append(
				ResourceBundleUtil.getString(resourceBundle, "maximum-memory"));
		}

		JFreeChart jFreeChart = _getJFreeChart(
			sb.toString(), _getMeterPlot(themeDisplay, valueDataset));

		resourceResponse.setContentType(ContentTypes.IMAGE_PNG);

		ChartUtilities.writeChartAsPNG(
			resourceResponse.getPortletOutputStream(), jFreeChart, 280, 180);
	}

	private JFreeChart _getJFreeChart(String title, MeterPlot meterPlot) {
		JFreeChart jFreeChart = new JFreeChart(
			title, new Font(null, Font.PLAIN, 13), meterPlot, true);

		jFreeChart.removeLegend();
		jFreeChart.setBackgroundPaint(Color.white);

		return jFreeChart;
	}

	private MeterPlot _getMeterPlot(
		ThemeDisplay themeDisplay, ValueDataset valueDataset) {

		MeterPlot meterPlot = new MeterPlot(valueDataset);

		meterPlot.addInterval(
			new MeterInterval(
				themeDisplay.translate("normal"), new Range(0.0D, 75D),
				Color.lightGray, new BasicStroke(2.0F),
				new Color(0, 255, 0, 64)));

		meterPlot.addInterval(
			new MeterInterval(
				themeDisplay.translate("warning"), new Range(75D, 90D),
				Color.lightGray, new BasicStroke(2.0F),
				new Color(255, 255, 0, 64)));

		meterPlot.addInterval(
			new MeterInterval(
				themeDisplay.translate("critical"), new Range(90D, 100D),
				Color.lightGray, new BasicStroke(2.0F),
				new Color(255, 0, 0, 128)));

		meterPlot.setDialBackgroundPaint(Color.white);
		meterPlot.setDialShape(DialShape.PIE);
		meterPlot.setDialOutlinePaint(Color.gray);
		meterPlot.setTickLabelFont(new Font(null, Font.PLAIN, 10));
		meterPlot.setTickLabelPaint(Color.darkGray);
		meterPlot.setTickLabelsVisible(true);
		meterPlot.setTickPaint(Color.lightGray);
		meterPlot.setTickSize(5D);
		meterPlot.setMeterAngle(180);
		meterPlot.setNeedlePaint(Color.darkGray);
		meterPlot.setRange(new Range(0.0D, 100D));
		meterPlot.setValueFont(new Font(null, Font.PLAIN, 10));
		meterPlot.setValuePaint(Color.black);
		meterPlot.setUnits("%");

		return meterPlot;
	}

}