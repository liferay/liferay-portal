import * as API from 'shared/api';
import autobind from 'autobind-decorator';
import Checkbox from 'shared/components/Checkbox';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import DataTransformationList from './data-transformation-list';
import FormNavigation from './FormNavigation';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import NavigationWarning from 'shared/components/NavigationWarning';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {Fragment} from 'react';
import Sheet from 'shared/components/Sheet';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {autoCancel, hasRequest} from 'shared/util/request-decorator';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {fromJS, List, Map} from 'immutable';
import {hasChanges} from 'shared/util/react';
import {mapValues, noop, values} from 'lodash';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';

/**
 * Return number of unmapped fields, where unmapped is considered
 * unmatched or duplicate target fields.
 * @param {ImmutableList.<ImmutableMap>} fieldsIList - List of field Maps.
 * @param {ImmutableMap}  duplicateTargetFieldsIMap- Map
 * @returns {number} - Number of unmapped fields.
 */
function getUnmappedCount(fieldsIList, duplicateTargetFieldsIMap) {
	return (
		fieldsIList.filter(
			fieldIMap => !fieldIMap.getIn(['suggestion', 'name'])
		).size +
		fieldsIList.filter(fieldIMap =>
			duplicateTargetFieldsIMap.get(
				fieldIMap.getIn(['suggestion', 'name'])
			)
		).size
	);
}

/**
 * Return duplicate target field mappings.
 * @param {Array.<Object>} fieldsIList - Array of field objects.
 * @returns {Object} - Object of target fields that multiple
 *                     source fields are mapped to.
 */
function getDuplicateTargetFields(fieldsIList) {
	let duplicateFieldsIMap = new Map();

	const fieldsSeen = new Set();

	for (const fieldIMap of fieldsIList) {
		const suggestionIMap = fieldIMap.get('suggestion');

		const name = suggestionIMap.get('name');

		if (!name) {
			continue;
		}

		if (fieldsSeen.has(name)) {
			duplicateFieldsIMap = duplicateFieldsIMap.set(name, suggestionIMap);
		} else {
			fieldsSeen.add(name);
		}
	}

	return duplicateFieldsIMap;
}

/**
 * Function used to transform form fields into
 * fieldMapping fields.
 * @returns {Array}
 */
export function processFieldMappings(fieldsIList) {
	return fieldsIList.toJS().map(({source, suggestion}) => ({
		dataSourceFieldName: source.name,
		name: suggestion.name
	}));
}

export class DataTransformation extends React.Component {
	static defaultProps = {
		navigationWarning: false,
		showUnmatchedFields: true,
		sourceFieldPlaceholder: Liferay.Language.get(
			'select-field-from-source'
		),
		sourceTitle: Liferay.Language.get('source-data'),
		submitMessage: Liferay.Language.get('done'),
		title: Liferay.Language.get('map-contact-data')
	};

	static propTypes = {
		addAlert: PropTypes.func.isRequired,
		cancelHref: PropTypes.string,
		fileVersionId: PropTypes.oneOfType([
			PropTypes.string,
			PropTypes.number
		]),
		groupId: PropTypes.string.isRequired,
		id: PropTypes.string,
		initialValue: PropTypes.shape({
			fieldMappings: PropTypes.object,
			mappingSuggestions: PropTypes.object,
			sourceFields: PropTypes.object
		}),
		name: PropTypes.string,
		navigationWarning: PropTypes.bool,
		onPrevious: PropTypes.func,
		onSubmit: PropTypes.func.isRequired,
		showUnmatchedFields: PropTypes.bool,
		sourceFieldPlaceholder: PropTypes.string,
		sourceTitle: PropTypes.string,
		submitMessage: PropTypes.string,
		submitting: PropTypes.bool,
		title: PropTypes.oneOfType([PropTypes.string, PropTypes.array])
	};

	state = {
		duplicateTargetFieldsIMap: new Map(),
		error: false,
		fieldMappings: {},
		fieldsIList: new List(),
		hideMappedFields: false,
		initialSearchedSuggestions: [],
		loading: false,
		mappingSuggestions: {},
		sourceFields: {},
		valid: false
	};

	_staticFieldsIList = new List();

	componentDidMount() {
		const {initialValue} = this.props;

		this.handleFetchSuggestions();

		if (initialValue) {
			this.processExistingFields(initialValue);
		} else {
			this.fetchMappings();
		}
	}

	componentDidUpdate(prevProps, prevState) {
		if (hasChanges(prevState, this.state, 'fieldsIList')) {
			const duplicateTargetFieldsIMap = getDuplicateTargetFields(
				this.state.fieldsIList
			);

			this.setState({
				duplicateTargetFieldsIMap,
				valid: !getUnmappedCount(
					this.state.fieldsIList,
					duplicateTargetFieldsIMap
				)
			});
		}
	}

	@autoCancel
	fetchMappings() {
		const {addAlert, groupId} = this.props;

		this.setState({
			loading: true
		});

		return API.dataSource
			.fetchMappings({...this.getIdObject(), groupId})
			.then(mappings => {
				const mappingSuggestions = {};
				const sourceFields = {};

				for (const {name, suggestions, values} of mappings) {
					mappingSuggestions[name] = suggestions;
					sourceFields[name] = values[0];
				}

				this._staticFieldsIList = this.processMappings(mappings);

				this.setState({
					fieldsIList: this._staticFieldsIList,
					loading: false,
					mappingSuggestions,
					sourceFields
				});
			})
			.catch(err => {
				if (!err.IS_CANCELLATION_ERROR) {
					addAlert({
						alertType: Alert.Types.Error,
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						)
					});

					this.setState({error: true, loading: false});
				}
			});
	}

	@autoCancel
	@autobind
	fetchSuggestions(query) {
		const {groupId} = this.props;

		return API.fieldMappings
			.fetchSuggestions({
				cur: 1,
				delta: 10,
				groupId,
				query
			})
			.then(response =>
				response.items.map(({name, values}) => ({
					name,
					value: values[0]
				}))
			);
	}

	getIdObject() {
		const {fileVersionId, id} = this.props;

		if (fileVersionId) {
			return {fileVersionId};
		} else {
			return {id};
		}
	}

	@autobind
	handleAddField() {
		const {fieldsIList} = this.state;

		this.setState({
			fieldsIList: fieldsIList.push(
				new Map({source: new Map(), suggestion: new Map()})
			)
		});
	}

	handleFetchSuggestions() {
		this.fetchSuggestions()
			.then(searchedSuggestions => {
				this.setState({
					initialSearchedSuggestions: searchedSuggestions
				});
			})
			.catch(noop);
	}

	@autobind
	handleFieldsChange(fieldsIList) {
		this.setState({fieldsIList});
	}

	@autobind
	handleHideMappedFieldsClick(event) {
		this.setState({
			hideMappedFields: event.target.checked
		});
	}

	@autobind
	handlePreviousStep() {
		const {
			props: {onPrevious},
			state: {mappingSuggestions, sourceFields}
		} = this;

		onPrevious({
			// TODO: what does this value need to be? probably needed for liferay and salesforce
			fieldMappings: this._formRef.current.getValues(),
			mappingSuggestions,
			sourceFields
		});
	}

	@autobind
	handleSubmit() {
		const {onSubmit} = this.props;
		const {fieldsIList} = this.state;

		onSubmit(fieldsIList);
	}

	processExistingFields({fieldMappings, mappingSuggestions, sourceFields}) {
		const fieldsIList = fromJS(
			values(
				mapValues(fieldMappings, (value, key) => ({
					...value,
					sourceName: key
				}))
			).map(({name, sourceName, value}) => ({
				source: {
					name: sourceName
				},
				suggestion: {
					name,
					value
				}
			}))
		);

		this._staticFieldsIList = fieldsIList;

		this.setState({
			fieldsIList,
			mappingSuggestions,
			sourceFields
		});
	}

	processMappings(mappings) {
		const {id, showUnmatchedFields} = this.props;

		if (id && mappings.find(({mapping}) => mapping)) {
			mappings = mappings.filter(({mapping}) => mapping);
		}

		if (!showUnmatchedFields) {
			mappings = mappings.filter(({suggestions}) => suggestions.length);
		}

		return new List(
			mappings.map(({mapping, name, suggestions, values}) => {
				const suggestion = mapping || suggestions[0];

				return new Map({
					source: new Map({
						name,
						value: values[0]
					}),
					suggestion: new Map({
						name: suggestion && suggestion.name,
						value: suggestion && suggestion.values[0]
					})
				});
			})
		);
	}

	renderContent() {
		const {
			props: {
				fileVersionId,
				groupId,
				id,
				name,
				navigationWarning,
				sourceFieldPlaceholder,
				sourceTitle,
				submitting
			},
			state: {
				duplicateTargetFieldsIMap,
				error,
				fieldsIList,
				hideMappedFields,
				initialSearchedSuggestions,
				loading,
				mappingSuggestions,
				sourceFields
			}
		} = this;

		const unmappedFields = getUnmappedCount(
			fieldsIList,
			duplicateTargetFieldsIMap
		);

		const fieldsIListCount = fieldsIList.size;

		const mappedFields = fieldsIListCount - unmappedFields;

		if (loading) {
			return <Loading />;
		} else if (error) {
			return (
				<NoResultsDisplay
					title={Liferay.Language.get('an-unexpected-error-occurred')}
				/>
			);
		} else {
			return (
				<Fragment key='DATA_TRANSFORMATION'>
					{navigationWarning && (
						<NavigationWarning
							when={
								(!id ||
									!this._staticFieldsIList.equals(
										fieldsIList
									)) &&
								!submitting
							}
						/>
					)}

					<Sheet.Body className='summary-section'>
						<p className='summary'>
							<ClayIcon
								className='icon-root'
								symbol='faro_connection_success_ovals'
							/>

							{sub(Liferay.Language.get('x-fields-mapped'), [
								mappedFields
							])}

							<ClayIcon
								className='icon-root'
								symbol='faro_connection_error_ovals'
							/>

							{sub(Liferay.Language.get('x-fields-not-mapped'), [
								unmappedFields
							])}
						</p>

						<Checkbox
							checked={hideMappedFields}
							label={Liferay.Language.get('unmapped-fields-only')}
							onChange={this.handleHideMappedFieldsClick}
						/>
					</Sheet.Body>

					<Sheet.Section>
						{!!fieldsIListCount && (
							<DataTransformationList
								dataSourceFn={this.fetchSuggestions}
								duplicateTargetFieldsIMap={
									duplicateTargetFieldsIMap
								}
								fieldsIList={fieldsIList}
								fileVersionId={fileVersionId}
								groupId={groupId}
								hideMappedFields={hideMappedFields}
								id={id}
								initialSearchedSuggestions={
									initialSearchedSuggestions
								}
								mappingSuggestions={mappingSuggestions}
								name={name}
								onChange={this.handleFieldsChange}
								sourceFieldPlaceholder={sourceFieldPlaceholder}
								sourceFields={sourceFields}
								sourceTitle={sourceTitle}
								suggestionsTitle={Liferay.Language.get(
									'analytics-cloud-field'
								)}
							/>
						)}

						<div className='add-custom-field'>
							<div>
								{Liferay.Language.get(
									'add-a-custom-field-mapping'
								)}
							</div>

							<ClayButton
								className='button-root'
								displayType='secondary'
								onClick={this.handleAddField}
							>
								{Liferay.Language.get('add-field')}
							</ClayButton>
						</div>
					</Sheet.Section>
				</Fragment>
			);
		}
	}

	render() {
		const {
			props: {
				cancelHref,
				className,
				groupId,
				onPrevious,
				submitMessage,
				submitting,
				title
			},
			state: {loading, valid}
		} = this;

		return (
			<div className={getCN('data-transformation-root', className)}>
				<Sheet.Header>
					<h3 className='title'>{title}</h3>

					{!loading && (
						<p className='text-secondary'>
							{[
								Liferay.Language.get(
									'all-fields-have-been-mapped-automatically-to-the-best-possible-match'
								),
								Liferay.Language.get(
									'please-confirm-the-data-mapping-below-and-make-any-changes-if-necessary.-you-may-only-map-one-source-data-field-to-a-single-analytics-cloud-field'
								)
							].join(' ')}
						</p>
					)}
				</Sheet.Header>

				{this.renderContent()}

				<Sheet.Footer divider={false}>
					<FormNavigation
						cancelHref={
							cancelHref ||
							toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {groupId})
						}
						enableNext={valid}
						onNextStep={this.handleSubmit}
						onPreviousStep={
							onPrevious ? this.handlePreviousStep : undefined
						}
						submitMessage={submitMessage}
						submitting={submitting}
					/>
				</Sheet.Footer>
			</div>
		);
	}
}

export default compose(
	connect(null, {addAlert}),
	hasRequest
)(DataTransformation);
