import * as API from 'shared/api';
import autobind from 'autobind-decorator';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import CollapsibleOverlay from 'shared/components/CollapsibleOverlay';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import Form from 'shared/components/form';
import FormSelectFieldInput from 'contacts/components/form/SelectFieldInput';
import Label from 'shared/components/form/Label';
import Loading from 'shared/components/Loading';
import React from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import {
	accountsListColumns,
	individualsListColumns
} from 'shared/util/table-columns';
import {
	ANIMATION_DURATION,
	AXIS,
	getTextWidth,
	RechartsTooltip
} from 'shared/util/recharts';
import {autoCancel, hasRequest} from 'shared/util/request-decorator';
import {
	Bar,
	CartesianGrid,
	Cell,
	ComposedChart,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis
} from 'recharts';
import {compose, withSelectedPoint, withStatefulPagination} from 'shared/hoc';
import {
	Conjunctions,
	RelationalOperators
} from 'segment/segment-editor/dynamic/utils/constants';
import {connect} from 'react-redux';
import {createNumberMask} from 'text-mask-addons';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {FieldContexts, FieldTypes} from 'shared/util/constants';
import {getBarColor} from 'shared/util/charts';
import {formatPercent, getFinitePercent} from 'shared/util/numbers';
import {hasChanges} from 'shared/util/react';
import {List, Map} from 'immutable';
import {noop, omit, pickBy, truncate} from 'lodash';
import {paginationConfig, paginationDefaults} from 'shared/util/pagination';
import {PropTypes} from 'prop-types';
import {setUriQueryValues} from 'shared/util/router';
import {sub} from 'shared/util/lang';

const SearchableEntityTableHOC = withStatefulPagination(
	SearchableEntityTable,
	{
		initialOrderIOMap: createOrderIOMap(NAME)
	},
	props => omit(props, 'onSearchValueChange')
);

export const CONTEXT_OPTIONS = [
	{
		label: Liferay.Language.get('individuals'),
		value: FieldContexts.Demographics
	},
	{
		label: Liferay.Language.get('accounts'),
		value: FieldContexts.Organization
	}
];

const BAR_WIDTH = 60;
const CHART_DATA_ID = 'count';
const CHART_PADDING = 60;
const DEFAULT_NUMBER_OF_BINS = 10;
const DEFAULT_SELECTED_POINT = 0;
const MAX_ROWS = 100;
const MAX_Y_AXIS_CHAR_COUNT = 50;
const PADDING_FOR_PERCENTAGE = getTextWidth(' - 100.0%');

export function getContextLabel(context) {
	const contextOption = CONTEXT_OPTIONS.find(({value}) => value === context);

	return contextOption ? contextOption.label : null;
}

export const numberOfBinsMask = createNumberMask({
	includeThousandsSeparator: false,
	prefix: ''
});

function formatTickVal(name, percent, showPercentage) {
	let suffix = '';

	if (showPercentage) {
		suffix =
			percent < 0.1
				? `- < ${formatPercent(0.1, 1)}`
				: `- ${formatPercent(percent, 1)}`;
	}

	return `${truncate(name, {
		length: MAX_Y_AXIS_CHAR_COUNT
	})} ${suffix}`;
}

/**
 * Get the chart height and bar config by calculating
 * the length of the data multiplied by bar width.
 * @param {number} dataLength - The length of the data.
 * @param {boolean} histogram - Whether the chart is a histogram type.
 * @returns {Object} Chart height and bar width config.
 */
export function getChartSizeConfig(dataLength, histogram) {
	const height = BAR_WIDTH * dataLength + CHART_PADDING;

	return histogram ? {height} : {bar: {width: {ratio: 0.8}}, height};
}

@hasRequest
export class Distribution extends React.Component {
	static defaultProps = {
		...paginationDefaults,
		contextOptions: CONTEXT_OPTIONS,
		delta: 10,
		id: '',
		numberOfBins: DEFAULT_NUMBER_OF_BINS,
		pageContainer: false,
		selectedPoint: DEFAULT_SELECTED_POINT,
		title: Liferay.Language.get('breakdown-of-known-members')
	};

	static propTypes = {
		...paginationConfig,
		contextOptions: PropTypes.arrayOf(
			PropTypes.shape({label: PropTypes.string, value: PropTypes.string})
		),
		distributionsKey: PropTypes.string.isRequired,
		error: PropTypes.bool,
		fetchDistribution: PropTypes.func.isRequired,
		fieldDistributionIList: PropTypes.instanceOf(List),
		fieldMappingFieldName: PropTypes.string,
		groupId: PropTypes.string.isRequired,
		hasSelectedPoint: PropTypes.bool,
		history: PropTypes.object.isRequired,
		id: PropTypes.string,
		knownIndividualCount: PropTypes.number,
		loading: PropTypes.bool,
		noResultsRenderer: PropTypes.func,
		numberOfBins: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
		onPointSelect: PropTypes.func.isRequired,
		pageContainer: PropTypes.bool,
		selectedContext: PropTypes.string,
		selectedPoint: PropTypes.number,
		title: PropTypes.string
	};

	state = {
		fieldMappingSelected: null,
		histogram: false,
		hoverIndex: -1,
		selectedContext: FieldContexts.Demographics,
		showIndividualsPreview: false
	};

	_formRef = React.createRef();
	_formSelectFieldInputRef = React.createRef();

	componentDidMount() {
		this.fetchFieldMappings();
	}

	componentDidUpdate(prevProps, prevState) {
		const {fieldMappingSelected} = this.state;

		const fieldMappingSelectedChanged =
			prevState.fieldMappingSelected &&
			hasChanges(prevState, this.state, 'fieldMappingSelected');

		const histogramNumberOfBinsChanged =
			hasChanges(prevProps, this.props, 'numberOfBins') &&
			fieldMappingSelected.rawType === FieldTypes.Number;

		if (fieldMappingSelectedChanged || histogramNumberOfBinsChanged) {
			this.handleFetchDistributionData();
		}
	}

	@autobind
	buildNumberFilter([min, max]) {
		const {
			props: {fieldDistributionIList, selectedPoint},
			state: {
				fieldMappingSelected: {context, name}
			}
		} = this;

		const getFilter = (operator, value) =>
			`${context}/${name}/value ${operator} ${value}`;

		const filter = [getFilter(RelationalOperators.GE, min)];

		if (fieldDistributionIList.size - 1 === selectedPoint) {
			filter.push(getFilter(RelationalOperators.LE, max));
		} else {
			filter.push(getFilter(RelationalOperators.LT, max));
		}

		return filter.join(` ${Conjunctions.And} `);
	}

	@autobind
	buildStringFilter(distributionValues) {
		const {
			fieldMappingSelected: {context, name}
		} = this.state;

		const filter = `${context}/${name}/value ${RelationalOperators.EQ} '${distributionValues[0]}'`;

		return filter;
	}

	@autobind
	fetchAccounts({
		delta,
		fieldMappingSelected: {name: propertyName},
		filter,
		orderIOMap,
		page,
		query
	}) {
		const {groupId, id} = this.props;

		return API.accounts.search(
			pickBy({
				delta,
				filter,
				groupId,
				includePropertyNames: [propertyName],
				individualSegmentId: id,
				orderIOMap,
				page,
				query
			})
		);
	}

	@autoCancel
	handleFetchDistributionData() {
		const {
			props: {
				channelId,
				fetchDistribution,
				fieldMappingFieldName,
				groupId,
				id,
				numberOfBins
			},
			state: {selectedContext}
		} = this;

		return fetchDistribution(
			pickBy({
				channelId,
				context: selectedContext,
				count: MAX_ROWS,
				fieldMappingFieldName,
				groupId,
				id,
				individualSegmentId: id,
				numberOfBins
			})
		);
	}

	@autoCancel
	fetchFieldMappings() {
		const {fieldMappingFieldName, groupId, history} = this.props;

		const fieldMappingFn = fieldMappingFieldName
			? () =>
					API.fieldMappings.fetch({
						fieldMappingFieldName,
						groupId
					})
			: () => API.fieldMappings.fetchDefault(groupId);

		return fieldMappingFn()
			.then(fieldMapping => {
				if (!fieldMappingFieldName) {
					history.replace(
						setUriQueryValues({
							fieldMappingFieldName: fieldMapping.id
						})
					);
				}

				this.setState(
					{
						fieldMappingSelected: fieldMapping,
						histogram: fieldMapping.rawType === FieldTypes.Number,
						selectedContext: fieldMapping.context
					},
					() => this.handleFetchDistributionData()
				);
			})
			.catch(noop);
	}

	@autobind
	fetchIndividuals({
		delta,
		fieldMappingSelected: {name: propertyName},
		filter,
		orderIOMap,
		page,
		query
	}) {
		const {channelId, groupId, id} = this.props;

		return API.individuals.search(
			pickBy({
				channelId,
				delta,
				filter,
				groupId,
				includePropertyNames: [propertyName],
				individualSegmentId: id,
				orderIOMap,
				page,
				query
			})
		);
	}

	@autobind
	focusSelectFieldInput() {
		this._formSelectFieldInputRef.current.focus();
	}

	formatChartData(fieldDistributions) {
		const {histogram} = this.state;

		return fieldDistributions.map(({count, values}) => ({
			count,
			graphValue: histogram ? (values[0] + values[1]) / 2 : values[0],
			values
		}));
	}

	getNumberOfBins() {
		const {numberOfBins} = this.props;

		return Number(numberOfBins);
	}

	getFilter() {
		const {
			props: {fieldDistributionIList, selectedPoint},
			state: {
				fieldMappingSelected: {rawType}
			}
		} = this;

		const buildFn =
			rawType === FieldTypes.Number
				? this.buildNumberFilter
				: this.buildStringFilter;

		const distributionValues = fieldDistributionIList
			.getIn([selectedPoint, 'values'], new List())
			.toJS();

		return buildFn(distributionValues);
	}

	getYAxisTicks(fieldDistributions) {
		const {histogram} = this.state;

		return [
			...fieldDistributions.map(item => item.values[0]),
			histogram &&
				fieldDistributions.length &&
				fieldDistributions[fieldDistributions.length - 1].values[1]
		].filter(Boolean);
	}

	@autobind
	handleNumberOfBinsChange(event) {
		const {name, value} = event.target;

		const {history} = this.props;

		const {errors} = this._formRef.current;

		const numberOfBins = Number(value);

		const curNumberOfBins = this.getNumberOfBins();

		if (value && curNumberOfBins !== numberOfBins && !errors[name]) {
			history.replace(setUriQueryValues({numberOfBins}));
		}
	}

	@autobind
	handleBreakdownSelect(fieldMapping) {
		const {id, rawType} = fieldMapping;

		const {fieldMappingFieldName, history} = this.props;

		const histogram = rawType === FieldTypes.Number;

		this.handleOverlayClose();

		this.setState({
			fieldMappingSelected: fieldMapping,
			histogram
		});

		if (fieldMappingFieldName !== id) {
			history.replace(setUriQueryValues({fieldMappingFieldName: id}));
		}

		if (histogram) {
			history.replace(
				setUriQueryValues({numberOfBins: DEFAULT_NUMBER_OF_BINS})
			);
		}
	}

	@autobind
	handleChartSelect(index) {
		const {onPointSelect, selectedPoint} = this.props;

		const alreadySelected = selectedPoint === index;

		this.setState({
			showIndividualsPreview: alreadySelected ? false : true
		});

		onPointSelect({index: alreadySelected ? null : index});
	}

	@autobind
	handleContextSelect(event) {
		const {value} = event.target;

		this.setState({selectedContext: value}, () => {
			const {setTouched, validateField} = this._formRef.current;

			setTouched({breakdown: false});

			validateField('breakdown');
		});
	}

	@autobind
	handleOverlayClose() {
		const {onPointSelect} = this.props;

		this.setState({
			showIndividualsPreview: false
		});

		onPointSelect({index: null});
	}

	@autobind
	validateFieldMapping() {
		const {
			fieldMappingSelected: {context},
			selectedContext
		} = this.state;

		if (context !== selectedContext) {
			this.focusSelectFieldInput();

			return sub(Liferay.Language.get('invalid-breakdown-for-x'), [
				getContextLabel(selectedContext)
			]);
		}

		return '';
	}

	render() {
		const {
			props: {
				channelId,
				delta,
				error,
				fieldDistributionIList,
				groupId,
				hasSelectedPoint,
				knownIndividualCount,
				loading,
				noResultsRenderer,
				orderIOMap,
				page,
				pageContainer,
				query,
				selectedPoint
			},
			state: {
				fieldMappingSelected,
				histogram,
				hoverIndex,
				selectedContext,
				showIndividualsPreview
			}
		} = this;

		const numberOfBins = this.getNumberOfBins();

		const fieldDistributions = fieldDistributionIList.toJS();

		const yAxisTicks = this.getYAxisTicks(fieldDistributions);

		const formattedChartData = this.formatChartData(fieldDistributions);

		const fieldDistributionsCount = fieldDistributions.length;

		const yAxisWidth = yAxisTicks.reduce((acc, item) => {
			const textWidth = getTextWidth(
				truncate(item.toString(), {length: MAX_Y_AXIS_CHAR_COUNT})
			);

			return textWidth > acc ? textWidth : acc;
		}, 60);

		const yAxisDomain = histogram
			? [yAxisTicks[0], yAxisTicks[yAxisTicks.length - 1]]
			: [0, 'auto'];

		return (
			<>
				<BasePage.Body pageContainer={pageContainer}>
					<Card>
						<Card.Header>
							<Card.Title>
								{Liferay.Language.get(
									'distribution-by-attribute'
								)}
							</Card.Title>

							<span className='description-secondary'>
								{Liferay.Language.get(
									'breakdown-known-members-by-the-top-100-results-or-the-number-of-bins-assigned-for-a-selected-attribute.-only-results-data-will-appear'
								)}
							</span>
						</Card.Header>

						<Card.Body>
							<Form
								enableReinitialize
								initialValues={{
									breakdown: fieldMappingSelected,
									numberOfBins
								}}
								innerRef={this._formRef}
							>
								<Form.Form className='chart-options'>
									<Label>
										{Liferay.Language.get('breakdown-by')}
									</Label>

									<Form.Group autoFit className='mt-2'>
										<Form.GroupItem shrink>
											<FormSelectFieldInput
												context={selectedContext}
												groupId={groupId}
												name='breakdown'
												onSelect={
													this.handleBreakdownSelect
												}
												ref={
													this
														._formSelectFieldInputRef
												}
												validate={
													this.validateFieldMapping
												}
											/>
										</Form.GroupItem>

										{histogram && (
											<>
												<Form.GroupItem label shrink>
													<Form.Label htmlFor='numberOfBins'>
														{Liferay.Language.get(
															'number-of-bins'
														)}
													</Form.Label>
												</Form.GroupItem>

												<Form.GroupItem
													className='chart-options-bins-input'
													shrink
												>
													{/* eslint-disable */}
													<Form.Input
														mask={numberOfBinsMask}
														name='numberOfBins'
														onChange={
															this
																.handleNumberOfBinsChange
														}
														showSuccess={false}
													/>
													{/* eslint-enable */}
												</Form.GroupItem>
											</>
										)}
									</Form.Group>
								</Form.Form>
							</Form>

							{error && !loading && (
								<ErrorDisplay
									onReload={this.handleFetchDistributionData}
									spacer
								/>
							)}

							{loading && <Loading />}

							{!error && !loading && (
								<div className='chart-container'>
									{!fieldDistributionsCount &&
										noResultsRenderer &&
										noResultsRenderer()}

									{!!fieldDistributionsCount && (
										<ResponsiveContainer
											height={
												BAR_WIDTH *
													fieldDistributionIList.size +
												CHART_PADDING
											}
										>
											<ComposedChart
												data={formattedChartData}
												layout='vertical'
											>
												<CartesianGrid
													horizontal={false}
													stroke={AXIS.gridStroke}
													strokeDasharray='3 3'
												/>

												<Tooltip
													content={({
														active,
														payload
													}) => {
														if (active && payload) {
															const {
																name,
																payload: {
																	count,
																	values
																}
															} = payload[0];

															return (
																<RechartsTooltip
																	rows={[
																		{
																			label: name,
																			value: count
																		}
																	]}
																	title={values.join(
																		' - '
																	)}
																/>
															);
														}

														return null;
													}}
												/>

												<YAxis
													axisLine={{
														stroke: AXIS.borderStroke
													}}
													dataKey='graphValue'
													domain={yAxisDomain}
													tickFormatter={val =>
														formatTickVal(
															val,
															getFinitePercent(
																fieldDistributionIList.getIn(
																	[
																		yAxisTicks.indexOf(
																			val
																		),
																		CHART_DATA_ID
																	]
																),
																knownIndividualCount
															),
															!histogram &&
																selectedContext ===
																	FieldContexts.Demographics
														)
													}
													ticks={yAxisTicks}
													type={
														histogram
															? 'number'
															: 'category'
													}
													width={
														yAxisWidth +
														PADDING_FOR_PERCENTAGE
													}
												/>

												<YAxis
													axisLine={{
														stroke: AXIS.borderStroke
													}}
													dataKey='graphValue'
													domain={yAxisDomain}
													orientation='right'
													tick={false}
													tickLine={false}
													yAxisId='right'
												/>

												<XAxis
													axisLine={{
														stroke: AXIS.borderStroke
													}}
													dataKey={CHART_DATA_ID}
													interval='preserveStart'
													orientation='top'
													scale='linear'
													tickLine={false}
													type='number'
												/>

												<XAxis
													axisLine={{
														stroke: AXIS.borderStroke
													}}
													dataKey={CHART_DATA_ID}
													tick={false}
													tickLine={false}
													xAxisId='bottom'
												/>

												<Bar
													animationDuration={
														ANIMATION_DURATION.bar
													}
													dataKey={CHART_DATA_ID}
													onClick={(e, index) =>
														this.handleChartSelect(
															index
														)
													}
													onMouseEnter={(e, index) =>
														this.setState({
															hoverIndex: index
														})
													}
													onMouseLeave={() =>
														this.setState({
															hoverIndex: -1
														})
													}
												>
													{formattedChartData.map(
														(item, index) => (
															<Cell
																fill={getBarColor(
																	index,
																	hoverIndex,
																	selectedPoint
																)}
																key={`cell-${index}`}
																style={{
																	cursor: 'pointer'
																}}
															/>
														)
													)}
												</Bar>
											</ComposedChart>
										</ResponsiveContainer>
									)}
								</div>
							)}
						</Card.Body>
					</Card>
				</BasePage.Body>

				{fieldMappingSelected && hasSelectedPoint && (
					<CollapsibleOverlay
						onClose={this.handleOverlayClose}
						title={sub(
							fieldMappingSelected.context ===
								FieldContexts.Demographics
								? Liferay.Language.get('individuals-matching-x')
								: Liferay.Language.get('accounts-matching-x'),
							[
								<span className='distribution-name' key='NAME'>
									{`"${fieldMappingSelected.name}"`}
								</span>
							],
							false
						)}
						visible={showIndividualsPreview}
					>
						<SearchableEntityTableHOC
							columns={[
								fieldMappingSelected.context ===
								FieldContexts.Demographics
									? individualsListColumns.getName({
											channelId,
											groupId
									  })
									: accountsListColumns.getName({
											channelId,
											groupId
									  }),
								{
									accessor: `properties.${fieldMappingSelected.name}`,
									label: fieldMappingSelected.name,
									sortable: false
								}
							]}
							dataSourceFn={
								fieldMappingSelected.context ===
								FieldContexts.Demographics
									? this.fetchIndividuals
									: this.fetchAccounts
							}
							dataSourceParams={{
								fieldMappingSelected,
								filter: this.getFilter(),
								selectedPoint
							}}
							delta={delta}
							orderIOmap={orderIOMap}
							page={page}
							query={query}
							rowIdentifier='id'
							showFilterAndOrder={false}
						/>
					</CollapsibleOverlay>
				)}
			</>
		);
	}
}

export default compose(
	withSelectedPoint,
	connect(
		(
			state,
			{
				distributionsKey,
				fieldMappingFieldName,
				knownIndividualCount,
				selectedContext
			}
		) => {
			const distributionIMap = state.getIn(
				['distributions', distributionsKey],
				new Map()
			);

			return {
				error: distributionIMap.get('error'),
				fieldDistributionIList:
					distributionIMap.getIn(['data', 'items']) || new List(),
				fieldMappingFieldName:
					fieldMappingFieldName ||
					distributionIMap.get('fieldMappingFieldName'),
				loading:
					distributionIMap.get('loading', true) ||
					knownIndividualCount === null,
				selectedContext:
					selectedContext || distributionIMap.get('context')
			};
		}
	)
)(Distribution);
