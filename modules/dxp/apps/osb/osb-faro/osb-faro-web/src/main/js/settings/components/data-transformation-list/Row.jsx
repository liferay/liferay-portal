import * as API from 'shared/api';
import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import debounce from 'shared/util/debounce-decorator';
import FieldDropDown from './FieldDropDown';
import Form from 'shared/components/form';
import getCN from 'classnames';
import React from 'react';
import {autoCancel, hasRequest} from 'shared/util/request-decorator';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {hasChanges} from 'shared/util/react';
import {map, uniqBy} from 'lodash';
import {Map} from 'immutable';
import {PropTypes} from 'prop-types';
import {sub} from 'shared/util/lang';

const FIELD_PREVIEW_COUNT = 10;

@hasRequest
export class DataTransformationListRow extends React.Component {
	static defaultProps = {
		isDuplicateTargetField: false,
		mappingSuggestions: {}
	};

	static propTypes = {
		close: PropTypes.func.isRequired,
		dataSourceFn: PropTypes.func.isRequired,
		fieldIMap: PropTypes.instanceOf(Map).isRequired,
		fileVersionId: PropTypes.oneOfType([
			PropTypes.string,
			PropTypes.number
		]),
		groupId: PropTypes.string.isRequired,
		id: PropTypes.string,
		index: PropTypes.number,
		initialSearchedSuggestions: PropTypes.array,
		isDuplicateTargetField: PropTypes.bool,
		mappingSuggestions: PropTypes.object,
		onChange: PropTypes.func,
		onRemove: PropTypes.func,
		open: PropTypes.func.isRequired,
		readOnly: PropTypes.bool,
		sourceFieldPlaceholder: PropTypes.string,
		sourceFields: PropTypes.object,
		sourceName: PropTypes.string
	};

	constructor(props) {
		super(props);

		const {initialSearchedSuggestions} = this.props;

		let initialState = {
			loading: true,
			searchedSuggestions: [],
			showHelpIcon: true,
			sourceQuery: '',
			suggestionQuery: ''
		};

		if (initialSearchedSuggestions) {
			initialState = {
				...initialState,
				loading: false,
				searchedSuggestions: initialSearchedSuggestions
			};
		}

		this.state = initialState;
	}

	componentDidUpdate(prevProps, prevState) {
		if (hasChanges(prevState, this.state, 'suggestionQuery')) {
			this.setState({
				loading: true
			});

			this.handleFetchSuggestions();
		}

		if (hasChanges(prevProps, this.props, 'initialSearchedSuggestions')) {
			this.setState({
				loading: false,
				searchedSuggestions: this.props.initialSearchedSuggestions
			});
		}
	}

	componentWillUnmount() {
		this.handleFetchSuggestions.cancel();
	}

	@debounce(250)
	@autoCancel
	handleFetchSuggestions() {
		const {
			props: {dataSourceFn},
			state: {suggestionQuery}
		} = this;

		dataSourceFn(suggestionQuery)
			.then(searchedSuggestions => {
				this.setState({
					loading: false,
					searchedSuggestions
				});
			})
			.catch(err => {
				if (!err.IS_CANCELLATION_ERROR) {
					this.setState({
						loading: false
					});
				}
			});
	}

	@autobind
	handleFieldPreviewModal() {
		const {
			close,
			fieldIMap,
			fileVersionId,
			groupId,
			id,
			open,
			sourceName
		} = this.props;

		const fieldName = fieldIMap.getIn(['source', 'name']);

		open(modalTypes.FIELD_PREVIEW_MODAL, {
			dataSourceFn: () =>
				API.dataSource.fetchFieldValues(
					fileVersionId
						? {
								count: FIELD_PREVIEW_COUNT,
								fieldName,
								fileVersionId,
								groupId
						  }
						: {
								count: FIELD_PREVIEW_COUNT,
								fieldName,
								groupId,
								id
						  }
				),
			fieldName,
			onClose: close,
			sourceName
		});
	}

	@autobind
	handleMappingSelect(item) {
		const {
			props: {index, onChange},
			state: {showHelpIcon}
		} = this;

		if (showHelpIcon) {
			this.setState({
				showHelpIcon: false
			});
		}

		if (onChange) {
			onChange(index, item);
		}
	}

	@autobind
	handleRemove() {
		const {index, onRemove} = this.props;

		if (onRemove) {
			onRemove(index);
		}
	}

	@autobind
	handleSourceSelect(item) {
		const {index, onChange} = this.props;

		if (onChange) {
			onChange(index, item, true);
		}
	}

	@autobind
	handleSourceSearch(value) {
		this.setState({
			sourceQuery: value
		});
	}

	@autobind
	handleMappingSearch(value) {
		this.setState({
			suggestionQuery: value
		});
	}

	getMappingSuggestions() {
		const {
			props: {fieldIMap, mappingSuggestions},
			state: {searchedSuggestions, suggestionQuery}
		} = this;

		const sourceName = fieldIMap.getIn(['source', 'name']);

		let suggestions = mappingSuggestions[sourceName]
			? mappingSuggestions[sourceName].filter(
					suggestion =>
						suggestion &&
						suggestion.name
							.toLowerCase()
							.match(suggestionQuery.toLowerCase())
			  )
			: [];

		let arr = [];

		suggestions = suggestions.map(({name, values}) => ({
			name,
			value: values
		}));

		if (suggestions.length > 0) {
			arr = arr.concat([
				{name: Liferay.Language.get('suggestions'), subheader: true},
				...suggestions
			]);
		}

		if (
			searchedSuggestions.length > 0 &&
			uniqBy([...arr, ...searchedSuggestions]).length > 0
		) {
			arr = arr.concat([
				{
					name: Liferay.Language.get('analytics-cloud-fields'),
					subheader: true
				},
				...searchedSuggestions
			]);
		}

		return uniqBy(arr, 'name');
	}

	getTooltipTitle() {
		const {
			fieldIMap,
			isDuplicateTargetField,
			mappingSuggestions
		} = this.props;

		const sourceName = fieldIMap.getIn(['source', 'name']);
		const suggestionName = fieldIMap.getIn(['suggestion', 'name']);

		const unmatched = !sourceName || !suggestionName;

		if (isDuplicateTargetField) {
			return Liferay.Language.get(
				'only-one-field-per-source-can-be-mapped-to-the-same-analytics-cloud-field'
			);
		} else if (unmatched) {
			return Liferay.Language.get(
				'no-fields-matched.-please-select-a-field-from-the-dropdown-or-create-a-new-one'
			);
		} else if (!sourceName && !suggestionName) {
			return Liferay.Language.get(
				'choose-the-fields-that-you-want-to-match'
			);
		} else {
			return sub(
				Liferay.Language.get(
					'best-match-selected.-there-are-x-other-possible-matches'
				),
				[
					mappingSuggestions[sourceName] &&
						mappingSuggestions[sourceName].length - 1
				]
			);
		}
	}

	@autobind
	openCreateMappingModal() {
		const {close, groupId, open} = this.props;

		open(modalTypes.CREATE_MAPPING_MODAL, {
			groupId,
			onClose: close,
			onSubmit: item => {
				this.handleMappingSelect(item);

				close();
			}
		});
	}

	render() {
		const {
			props: {
				className,
				fieldIMap,
				index,
				isDuplicateTargetField,
				readOnly,
				sourceFieldPlaceholder,
				sourceFields,
				sourceName
			},
			state: {loading, showHelpIcon, sourceQuery, suggestionQuery}
		} = this;

		const sourceIMap = fieldIMap.get('source', new Map());
		const suggestionIMap = fieldIMap.get('suggestion', new Map());

		const suggestionValIMap = suggestionIMap.setIn(
			['sourceName'],
			sourceIMap.get('name')
		);

		const unmatched =
			!sourceIMap.get('name') || !suggestionIMap.get('name');

		const error = unmatched || isDuplicateTargetField;

		const classes = getCN('data-transformation-item-root', {
			error
		});

		return (
			<Form.Group
				autoFit
				className={classes + (className ? ` ${className}` : '')}
			>
				<Form.GroupItem>
					<FieldDropDown
						dataIMap={sourceIMap}
						onFieldPreviewModal={this.handleFieldPreviewModal}
						onSearchInput={this.handleSourceSearch}
						onSearchSelect={this.handleSourceSelect}
						placeholder={sourceFieldPlaceholder}
						readOnly={readOnly}
						searchInputValue={sourceQuery}
						searchItems={map(sourceFields, (value, key) => ({
							name: key,
							value
						})).filter(item =>
							item.name
								.toLowerCase()
								.match(sourceQuery.toLowerCase())
						)}
					/>
				</Form.GroupItem>

				<Form.GroupItem className='add-on' shrink>
					{error ? (
						<ClayIcon
							className='icon-root'
							symbol='faro_connection_error_ovals'
						/>
					) : (
						<ClayIcon
							className='icon-root'
							symbol='faro_connection_success_ovals'
						/>
					)}
				</Form.GroupItem>

				<Form.GroupItem>
					<FieldDropDown
						className='suggestion'
						dataIMap={suggestionValIMap}
						disabled={!sourceIMap.get('name')}
						footerButtonMessage={Liferay.Language.get('new-field')}
						footerOnClick={this.openCreateMappingModal}
						loading={loading}
						name={`${sourceName}.${index}`}
						onSearchInput={this.handleMappingSearch}
						onSearchSelect={this.handleMappingSelect}
						placeholder={Liferay.Language.get(
							'select-analytics-cloud-field'
						)}
						readOnly={readOnly}
						searchInputValue={suggestionQuery}
						searchItems={this.getMappingSuggestions()}
					/>
				</Form.GroupItem>

				{!readOnly && (
					<>
						<Form.GroupItem className='add-on' shrink>
							{(showHelpIcon || error) && (
								<span
									data-tooltip
									title={this.getTooltipTitle()}
								>
									<ClayIcon
										className='icon-root help'
										symbol={
											error
												? 'exclamation-full'
												: 'info-circle'
										}
									/>
								</span>
							)}
						</Form.GroupItem>

						<Form.GroupItem className='add-on' shrink>
							<ClayButton
								aria-label={Liferay.Language.get(
									'remove-field'
								)}
								className='button-root'
								data-tooltip
								displayType='unstyled'
								onClick={this.handleRemove}
								title={Liferay.Language.get('remove-field')}
							>
								<ClayIcon
									className='icon-root'
									symbol='times-circle'
								/>
							</ClayButton>
						</Form.GroupItem>
					</>
				)}
			</Form.Group>
		);
	}
}

export default compose(
	connect(
		null,
		{
			close,
			open
		},
		null
	)
)(DataTransformationListRow);
