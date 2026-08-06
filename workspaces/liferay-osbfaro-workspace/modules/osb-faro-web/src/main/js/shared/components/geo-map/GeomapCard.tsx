import * as d3 from 'd3';
import GeomapChart from './GeomapChart';
import GeoMapLangKey from './geo-map-lang-key';
import getCN from 'classnames';
import React, {useEffect, useRef, useState} from 'react';
import {Colors} from 'shared/util/charts';
import {createRoot} from 'react-dom/client';
import {formatPercent, toThousands} from 'shared/util/numbers';

const OTHERS = 'others';
const TOTAL_COUNTRIES_LIST = 5;

interface ICountry {
	id: string;
	name: string;
	total: number;
	value: number;
}

interface IFeatureProperties {
	name: string;
	total: number;
	value: number;
	[key: string]: any;
}

interface IFeature {
	id?: string;
	properties: IFeatureProperties;
	[key: string]: any;
}

interface IListProps {
	countries: ICountry[];
	features: IFeature[];
	onSelectCountry: (feature: IFeature | boolean | null) => void;
}

const geoMapLangKey: Record<string, string> = GeoMapLangKey;

const List = ({countries, features, onSelectCountry}: IListProps) => {
	const [hoverList, setHoverList] = useState<number | null>(null);

	const getLocationName = (location: string) =>
		location.toLowerCase() === OTHERS
			? Liferay.Language.get('others')
			: geoMapLangKey[location];

	const getPathSelected = (
		locationFilter: string
	): IFeature | boolean | null => {
		for (let i = 0; i < features.length; i++) {
			if (features[i].properties.name.includes(locationFilter)) {
				return features[i];
			}
		}

		return locationFilter.includes('Other') ? null : true;
	};

	const handleMouseOverList = (locationList: string, index: number) => {
		setHoverList(index < TOTAL_COUNTRIES_LIST ? index : null);

		onSelectCountry(getPathSelected(locationList));
	};

	return (
		<table className="analytics-geomap-table">
			<tbody>
				{countries
					.filter(
						(value: ICountry, index: number) =>
							index < TOTAL_COUNTRIES_LIST || value.id === OTHERS
					)
					.map((value: ICountry, index: number) => {
						const classNames = (classes: string) =>
							getCN(classes, {
								['lighten-item']:
									hoverList !== null && hoverList !== index,
								['text-l-secondary']: value.id === OTHERS,
							});

						return (
							<tr
								key={index}
								onFocus={() =>
									handleMouseOverList(value.name, index)
								}
								onMouseLeave={() => {
									setHoverList(null);

									onSelectCountry(null);
								}}
								onMouseOver={() =>
									handleMouseOverList(value.name, index)
								}
							>
								<td
									className={classNames(
										'text-left font-weight-semibold'
									)}
								>
									{getLocationName(value.name) || value.name}
								</td>

								<td className={classNames('text-right')}>
									{toThousands(value.total)}

									<span className="percentage font-weight-semibold">
										{formatPercent(value.value)}
									</span>
								</td>
							</tr>
						);
					})}
			</tbody>
		</table>
	);
};

const mergeData = (countries: ICountry[]) => {
	const countriesJson = require('../../../../resources/META-INF/resources/countries.geo.json');
	const data = {...countriesJson.data};

	return {
		...countriesJson,
		features: data.features.map((feature: IFeature) => {
			const country = countries.find((country: ICountry) =>
				feature.properties.name.includes(country.id)
			);

			return {
				...feature,
				properties: {
					...feature.properties,
					total: country?.total ?? 0,
					value: country?.value ?? 0,
				},
			};
		}),
	};
};

interface ITooltipProps {
	metricLabel: string;
	payload: IFeature;
}

const Tooltip = ({metricLabel, payload}: ITooltipProps) => (
	<>
		<div className="arrow" />

		<div className="popover-header">
			{geoMapLangKey[payload.properties.name]}
		</div>

		<div className="d-flex justify-content-between popover-body">
			<div className="mr-4">
				{payload.properties.total} <span>{metricLabel}</span>
			</div>

			<div>{formatPercent(payload.properties.value)}</div>
		</div>
	</>
);

interface IGeomapCardProps {
	data: {
		countries: ICountry[];
	};
	metricLabel: string;
}

export const GeomapCard = ({data, metricLabel}: IGeomapCardProps) => {
	const mergedData = mergeData(data.countries);
	const [selectedCountry, setSelectedCountry] = useState<
		IFeature | boolean | null
	>(null);

	const chartRef = useRef<any>(null);

	const fillFn = (d: IFeature) => {
		if (d && d.properties) {
			const value = d.properties[chartRef.current._color.value];

			if (!value) {
				return Colors.mapEmpty;
			}

			if (selectedCountry) {
				if (
					typeof selectedCountry !== 'boolean' &&
					selectedCountry !== null &&
					d.id === selectedCountry.id
				) {
					return chartRef.current._color.selected;
				}

				return Colors.mapEmpty;
			}

			return chartRef.current.colorScale!(value);
		}

		return chartRef.current.colorScale!(0);
	};

	useEffect(() => {
		const tooltip = d3
			.select(chartRef.current._element)
			.append('div')
			.attr('class', 'clay-popover-top popover text-2')
			.style('position', 'absolute')
			.style('display', 'none');

		const tooltipRoot = createRoot(tooltip.node() as HTMLElement);

		const handleMouseOver = (
			_feature: IFeature,
			index: number,
			selection: any
		) => {
			const node = selection[index];

			d3.select(node).style('fill', chartRef.current._color.selected);

			tooltipRoot.render(
				<Tooltip metricLabel={metricLabel} payload={_feature} />
			);

			tooltip.style('display', null);
		};

		const handleMouseOut = (
			_feature: IFeature,
			index: number,
			selection: any
		) => {
			const node = selection[index];

			d3.select(node).style('fill', (value: any) =>
				chartRef.current._fillFn(value)
			);

			tooltip.style('display', 'none');
		};

		const handleMoveTooltip = () => {
			const tooltipNode = tooltip.node() as HTMLElement | null;

			if (!tooltipNode) {
				return;
			}

			const {height, width} = tooltipNode.getBoundingClientRect();

			const tooltipProps = {

				// @ts-ignore
				left: d3.event.layerX - (width / 2 + 5),

				// @ts-ignore
				top: d3.event.layerY - (height + 20),
			};

			tooltip
				.style('left', `${tooltipProps.left}px`)
				.style('top', `${tooltipProps.top}px`);
		};

		const bounds = chartRef.current.svg.node()!.getBoundingClientRect();

		chartRef.current.projection = d3
			.geoMercator()
			.scale(100)
			.translate([bounds.width / 2, bounds.height / 2])
			.fitHeight(bounds.height, chartRef.current._data.data);

		chartRef.current.path = d3
			.geoPath()
			.projection(chartRef.current.projection);

		chartRef.current
			.mapLayer!.selectAll('path')
			.attr('d', chartRef.current.path)
			.on('mousemove', handleMoveTooltip)
			.on('mouseout', handleMouseOut)
			.on('mouseover', handleMouseOver);

		chartRef.current._fillFn = fillFn;

		return () => {
			tooltipRoot.unmount();
		};
	}, []);

	useEffect(() => {
		chartRef.current.mapLayer!.selectAll('path').style('fill', fillFn);
	}, [selectedCountry]);

	return (
		<div className="analytics-geomap">
			<div style={{height: 232, width: 350}}>
				<GeomapChart
					color={{
						empty: Colors.mapEmpty,
						range: {
							max: Colors.mapMax,
							min: Colors.mapMin,
						},
						selected: Colors.mapSelected,
						value: 'total',
					}}
					data={{...mergedData, type: 'geo-map'}}
					ref={chartRef}
				/>
			</div>

			<List
				countries={data.countries}
				features={mergedData.features}
				onSelectCountry={setSelectedCountry}
			/>
		</div>
	);
};
