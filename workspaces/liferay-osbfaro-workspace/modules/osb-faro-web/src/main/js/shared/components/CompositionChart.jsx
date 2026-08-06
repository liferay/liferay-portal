import * as d3Selection from 'd3-selection';
import * as d3Shape from 'd3-shape';
import getCN from 'classnames';
import React from 'react';
import TextTruncate from './TextTruncate';
import {CHART_COLORS} from 'shared/util/charts';
import {formatPercent, getFinitePercent, toThousands} from 'shared/util/numbers';
import {get, isNull} from 'lodash';
import {getSafeDisplayValue} from 'shared/util/util';
import {PropTypes} from 'prop-types';

/**
 * Combine d3 utilities into one namespace
 */
const d3 = Object.assign({}, d3Shape, d3Selection);

/**
 * Render pie data in array order instead of sorting by value
 */
const pie = d3.pie().sort(null);

const DEFAULT_CHART_SIZE = 120;

const UNFILLED_COLOR = '#F5F5F7';

const DATA_SHAPE = PropTypes.shape({
	color: PropTypes.string,
	label: PropTypes.string,
	value: PropTypes.number
}).isRequired;

/**
 * Get Display Value
 * @param {number} value
 * @param {number} total
 */
function getDisplayValue(value, total) {
	const floatPercent = parseFloat(value / total);

	if (floatPercent < 0.01 && floatPercent !== 0) {
		return `< ${formatPercent(1, 0)}`;
	}

	const finitePercent = getFinitePercent(value, total);

	return isNull(finitePercent) ? null : formatPercent(finitePercent, 0);
}

export class CompositionLegend extends React.Component {
	render() {
		const {items, total} = this.props;

		return (
			<ul className='legend-template composition-legend-root'>
				{items.map(({color, label, value}) => {
					const displayValue = getDisplayValue(value, total);

					return (
						<li
							className='bb-legend-item three-columns'
							key={label}
						>
							<div className='legend-template-column d-flex align-items-baseline'>
								<span
									className='circle'
									style={{backgroundColor: color}}
								/>

								<TextTruncate title={label} />
							</div>

							<div
								className='legend-template-column justify-content-end'
								data-testid='active-count'
							>
								{getSafeDisplayValue(toThousands(value))}
							</div>

							<div className='legend-template-column justify-content-end'>
								<b data-testid='active-porcentage'>
									{isNull(displayValue) ? '-' : displayValue}
								</b>
							</div>
						</li>
					);
				})}
			</ul>
		);
	}
}

export default class CompositionChart extends React.Component {
	static defaultProps = {
		arcGap: 3,
		arcWidth: 13,
		height: DEFAULT_CHART_SIZE,
		total: 0,
		width: DEFAULT_CHART_SIZE
	};

	static propTypes = {
		arcGap: PropTypes.number,
		arcWidth: PropTypes.number,
		height: PropTypes.number,
		innerData: DATA_SHAPE,
		outerData: DATA_SHAPE,
		total: PropTypes.number,
		width: PropTypes.number
	};

	constructor() {
		super();

		this._containerRef = React.createRef();
	}

	componentDidMount() {
		this.attachChart();
	}

	componentWillUnmount() {
		if (this._svg) {
			this._svg.remove();
		}
	}

	attachChart() {
		const {arcGap, arcWidth, height, innerData, outerData, total, width} =
			this.props;

		const innerValue = get(innerData, 'value', 0);

		const outerValue = get(outerData, 'value', 0);

		const totalValue = total || 1;

		const arcOuterRadius = width / 2;

		const arcOuter = d3
			.arc()
			.innerRadius(arcOuterRadius - arcWidth)
			.outerRadius(arcOuterRadius);

		const arcInner = d3
			.arc()
			.innerRadius(arcOuterRadius - (arcWidth * 2 + arcGap))
			.outerRadius(arcOuterRadius - (arcWidth + arcGap));

		this._svg = d3
			.select(this._containerRef.current)
			.append('svg')
			.attr('width', `${width}px`)
			.attr('height', `${height}px`);

		this._svg
			.append('g')
			.attr('transform', `translate(${width / 2} ${height / 2})`)
			.selectAll('path')
			.data(pie([outerValue, totalValue - outerValue]))
			.enter()
			.append('path')
			.attr('d', arcOuter)
			.attr('fill', (_, i) =>
				i === 0
					? get(outerData, 'color', CHART_COLORS[3])
					: UNFILLED_COLOR
			);

		this._svg
			.append('g')
			.attr('transform', `translate(${width / 2} ${height / 2})`)
			.selectAll('path')
			.data(pie([innerValue, totalValue - innerValue]))
			.enter()
			.append('path')
			.attr('d', arcInner)
			.attr('fill', (_, i) =>
				i === 0
					? get(innerData, 'color', CHART_COLORS[0])
					: UNFILLED_COLOR
			);
	}

	render() {
		const {className, innerData, outerData, total} = this.props;

		return (
			<div
				className={getCN('composition-chart-root', {
					[className]: className
				})}
			>
				<div className='chart-root' ref={this._containerRef} />

				<CompositionLegend
					items={[
						{
							...outerData,
							color: get(outerData, 'color', CHART_COLORS[3])
						},
						{
							...innerData,
							color: get(innerData, 'color', CHART_COLORS[0])
						}
					]}
					total={total}
				/>
			</div>
		);
	}
}
