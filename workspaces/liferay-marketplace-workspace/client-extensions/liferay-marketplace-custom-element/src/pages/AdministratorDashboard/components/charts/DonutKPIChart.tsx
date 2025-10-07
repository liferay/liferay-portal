/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Label from '@clayui/label';
import React from 'react';
import {Cell, Pie, PieChart, ResponsiveContainer} from 'recharts';

import i18n from '../../../../i18n';
import {percentage as getPercentage} from '../../util';

import './DonutKPIChart.scss';

type DonutKPIChartProps = {
	annualTargetCurrent: number;
	annualTargetTotal: number | string;
	colors: string[];
	externalPage?: boolean;
	monthlyIncreasePct?: number;
	monthlyIncreaseValue?: number;
	monthlyIncreaseValueIsGrowing?: number;
	onClick?: null | (() => void);
	title: string;
};

const DonutKPIChart: React.FC<DonutKPIChartProps> = ({
	annualTargetCurrent,
	annualTargetTotal,
	colors,
	externalPage,
	monthlyIncreasePct = 0,
	monthlyIncreaseValue = 0,
	monthlyIncreaseValueIsGrowing = false,
	onClick,
	title,
}) => {
	const percentage = getPercentage(
		Number(annualTargetTotal),
		annualTargetCurrent
	);

	const data = [
		{name: 'filed', value: Math.min(percentage, 100)},
		{name: 'remainder', value: Math.max(0, 100 - percentage)},
	];

	return (
		<div className="border d-inline-flex flex-column justify-content-between marketplace-donut-chart p-5">
			<div className="align-items-start d-flex flex-row justify-content-between">
				<span className="font-weight-bold mr-3 text-wrap">{title}</span>

				{onClick && (
					<span className="align-items-center d-flex justify-content-center">
						<Button
							className="align-items-center d-flex details-button rounded-lg text-nowrap"
							displayType="secondary"
							onClick={onClick}
							size="sm"
						>
							{i18n.translate('view-details')}{' '}
							{externalPage && (
								<ClayIcon
									className="ml-1 mt-0"
									style={{fontSize: 8}}
									symbol="shortcut"
								/>
							)}
						</Button>
					</span>
				)}
			</div>

			<div className="align-items-center d-flex flex-row justify-content-between mt-3">
				<div className="donut-chart-container">
					<ResponsiveContainer>
						<PieChart tabIndex={-1}>
							<Pie
								cornerRadius={0}
								data={data}
								dataKey="value"
								endAngle={-270}
								innerRadius={40}
								outerRadius={80}
								paddingAngle={0}
								startAngle={90}
							>
								{data.map((_: any, index: number) => (
									<Cell fill={colors[index]} key={index}>
										{index}
									</Cell>
								))}
							</Pie>

							<text
								className="marketplace-donut-chart-center-legend"
								dominantBaseline="middle"
								textAnchor="middle"
								x="52%"
								y="52%"
							>
								<tspan
									fontSize={percentage >= 100 ? '40' : '44'}
								>
									{percentage}
								</tspan>
								<tspan fontSize="14">%</tspan>
							</text>
						</PieChart>
					</ResponsiveContainer>
				</div>

				<div className="chart-legend d-flex flex-column">
					<div className="d-flex flex-column mb-3">
						<span className="font-weight-bold text-small">
							{i18n.translate('annual-target')}
						</span>

						<div className="align-items-center d-flex flex-row">
							<div className="align-items-center d-flex font-weight-bold justify-content-center text-title">
								<span className="chart-legend-data mr-1">
									{annualTargetCurrent || 0}
								</span>
								/
								<span className="mx-1">
									{annualTargetTotal || 0}
								</span>
							</div>
							<span className="text-small">
								{i18n.translate('of-target')}
							</span>
						</div>
					</div>

					{!!monthlyIncreaseValue && (
						<div className="d-flex flex-column">
							<span className="font-weight-bold text-small">
								{i18n.translate('monthly-increase')}
							</span>

							<div className="align-items-center d-flex flex-row">
								<div className="font-weight-bold mr-3 text-title">
									{`${monthlyIncreaseValueIsGrowing ? '+' : '-'}${monthlyIncreaseValue}`}
								</div>

								<Label
									displayType={
										monthlyIncreaseValueIsGrowing
											? 'success'
											: 'danger'
									}
								>
									<span className="d-flex lign-items-center">
										<ClayIcon
											symbol={
												monthlyIncreaseValueIsGrowing
													? 'order-arrow-up'
													: 'order-arrow-donw'
											}
										/>
										<span className="ml-1">{`${monthlyIncreasePct} % `}</span>
									</span>
								</Label>
							</div>
						</div>
					)}
				</div>
			</div>
		</div>
	);
};

export default DonutKPIChart;
