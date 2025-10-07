/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Alignments, Weights} from '../types/global';
import TooltipTemplate from './TooltipTemplate';

export type Column = {
	align?: Alignments;
	className?: string;
	color?: string;
	colspan?: number;
	label: string | (() => React.ReactNode);
	truncated?: boolean;
	weight?: Weights;
	width?: number;
};

export interface IChartTooltipProps extends React.HTMLAttributes<HTMLElement> {
	description?: string;
	header?: {
		className?: string;
		columns: Column[];
	}[];
	rows?: {
		className?: string;
		columns: Column[];
	}[];
}

interface IColumnsProps extends React.HTMLAttributes<HTMLElement> {
	columns: Column[];
}

const Columns: React.FC<IColumnsProps> = ({columns}) => (
	<>
		{columns.map(
			(
				{align, className, colspan, label, truncated, weight, width},
				index
			) => {
				const labelValue =
					typeof label === 'function' ? label() : label;

				return (
					<TooltipTemplate.Column
						align={align}
						className={className}
						colSpan={colspan}
						key={`column-${index}`}
						style={width ? {minWidth: `${width}px`} : {}}
						truncated={truncated}
						weight={weight}
					>
						{labelValue}
					</TooltipTemplate.Column>
				);
			}
		)}
	</>
);

const ChartTooltip: React.FC<IChartTooltipProps> = ({
	className,
	description,
	header,
	rows,
}) => (
	<TooltipTemplate className={className}>
		{!!header && (
			<TooltipTemplate.Header>
				{header.map(({className, columns}, index) => (
					<TooltipTemplate.Row key={`header-rows-${index}`}>
						<Columns className={className} columns={columns} />
					</TooltipTemplate.Row>
				))}
			</TooltipTemplate.Header>
		)}

		{!!rows && (
			<TooltipTemplate.Body>
				{description ? (
					<TooltipTemplate.Row key="body-rows-description">
						<TooltipTemplate.Column
							className="my-2 text-secondary"
							colSpan={header?.[0].columns.length}
						>
							{description}
						</TooltipTemplate.Column>
					</TooltipTemplate.Row>
				) : (
					<></>
				)}

				{rows.map(({className, columns}, index) => (
					<TooltipTemplate.Row
						className={className}
						key={`body-rows-${index}`}
					>
						<Columns columns={columns} />
					</TooltipTemplate.Row>
				))}
			</TooltipTemplate.Body>
		)}
	</TooltipTemplate>
);

export default ChartTooltip;
