import * as API from 'shared/api';
import autobind from 'autobind-decorator';
import CriteriaBuilder from './criteria-builder';
import CriteriaSidebar from './criteria-sidebar';
import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import Form, {withField} from 'shared/components/form';
import NavigationWarning from 'shared/components/NavigationWarning';
import React from 'react';
import Toolbar from './Toolbar';
import {AlertTypes} from 'shared/components/Alert';
import {
	buildQueryString,
	translateQueryToCriteria,
	wrapInCriteriaGroup
} from './utils/odata';
import {CriterionGroup} from './utils/types';
import {DndProvider} from 'react-dnd';
import {Formik} from 'formik';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {
	invalidateCriterionWithMissingProperty,
	validateSegmentInputs
} from './utils/utils';
import {List} from 'immutable';
import {PropertyGroup, Segment} from 'shared/util/records';
import {
	ReferencedObjectsContext,
	withReferencedObjectsProvider
} from './context/referencedObjects';
import {SegmentStates, SegmentTypes} from 'shared/util/constants';

/**
 * Returns an error message if the criteria contains an invalid row.
 */
export function validateSegmentEditor(criteria) {
	let error;

	if (
		!criteria ||
		!criteria.items.length ||
		!validateSegmentInputs(criteria)
	) {
		error = Liferay.Language.get('empty-fields');
	}

	return error;
}

const CriteriaBuilderForm = withField(
	({channelId, field: {name, value}, groupId, ...fieldProps}) => {
		const handleChange = criteria => {
			const {
				form: {setFieldValue}
			} = fieldProps;

			setFieldValue(name, criteria);
		};

		return (
			<CriteriaBuilder
				{...fieldProps}
				channelId={channelId}
				criteria={value}
				groupId={groupId}
				onChange={handleChange}
			/>
		);
	}
);

type FormValues = {
	criteria: CriterionGroup;
	includeAnonymousUsers: boolean;
	name: string;
};

interface ISegmentEditorProps {
	channelId: string;
	groupId: string;
	id?: string;
	onDelete: boolean;
	onSubmit: (
		form: FormValues,
		ref: React.Ref<Formik>,
		requestFn: (params: FormValues) => Promise<any>
	) => void;
	propertyGroupsIList: List<PropertyGroup>;
	segment: Segment;
}

class SegmentEditor extends React.Component<ISegmentEditorProps> {
	static contextType = ReferencedObjectsContext;

	static defaultProps = {
		segment: new Segment()
	};

	_formRef = React.createRef<Formik>();

	@autobind
	createBatchSegment({criteria, includeAnonymousUsers, name}) {
		const {
			channelId,
			groupId,
			segment: {id}
		} = this.props;

		const request = id
			? API.individualSegment.update
			: API.individualSegment.create;

		const requestData = {
			criteriaString: buildQueryString([criteria]),
			description: '',
			includeAnonymousUsers,
			name: name.trim(),
			segmentType: SegmentTypes.Batch
		};

		return request({...requestData, channelId, groupId, id});
	}

	@autobind
	hasChanges(newIncludeAnonymousUsers, newName, newCriteriaString) {
		const {
			segment: {criteriaString, includeAnonymousUsers, name}
		} = this.props;

		return (
			newIncludeAnonymousUsers !== includeAnonymousUsers ||
			name !== newName ||
			criteriaString !== newCriteriaString
		);
	}

	@autobind
	handleSubmit(form) {
		const {onSubmit} = this.props;

		onSubmit(form, this._formRef, this.createBatchSegment);
	}

	render() {
		const {
			context: {referencedProperties},
			props: {
				channelId,
				groupId,
				id,
				onDelete,
				propertyGroupsIList,
				segment: {
					criteriaString,
					includeAnonymousUsers,
					name,
					state: segmentState
				}
			}
		} = this;

		return (
			<DndProvider backend={HTML5Backend}>
				<div className='segment-edit-page-root'>
					<Form
						initialValues={{
							criteria:
								id && criteriaString
									? invalidateCriterionWithMissingProperty(
											translateQueryToCriteria(
												criteriaString
											),
											referencedProperties
									  )
									: wrapInCriteriaGroup([]),
							includeAnonymousUsers,
							name
						}}
						onSubmit={this.handleSubmit}
						ref={this._formRef}
					>
						{({
							handleSubmit,
							isSubmitting,
							isValid,
							values: {criteria, includeAnonymousUsers, name}
						}) => {
							const newCriteriaString = buildQueryString([
								criteria
							]);
							const hasChanges = this.hasChanges(
								includeAnonymousUsers,
								name,
								newCriteriaString
							);

							return (
								<Form.Form
									className='contributor-builder-root editing'
									onSubmit={handleSubmit}
								>
									<NavigationWarning
										when={
											hasChanges &&
											!isSubmitting &&
											!onDelete
										}
									/>

									<Toolbar
										channelId={channelId}
										criteria={criteria}
										criteriaString={newCriteriaString}
										groupId={groupId}
										id={id}
										includeAnonymousUsers={
											includeAnonymousUsers
										}
										valid={isValid && hasChanges}
									/>

									<div className='form-body'>
										<div className='criteria-builder-section-sidebar'>
											<CriteriaSidebar
												propertyGroupsIList={
													propertyGroupsIList
												}
											/>
										</div>

										<div className='criteria-builder-section-main'>
											<div className='contributor-container'>
												<div className='container-fluid container-fluid-max-xl'>
													<div className='content-wrapper'>
														{segmentState ===
															SegmentStates.Disabled && (
															<EmbeddedAlertList
																alerts={[
																	{
																		iconSymbol:
																			'exclamation-full',
																		message: Liferay.Language.get(
																			'some-criteria-are-empty-please-update-to-continue-using-this-segment'
																		),
																		title: Liferay.Language.get(
																			'error'
																		),
																		type:
																			AlertTypes.Danger
																	}
																]}
															/>
														)}

														<div className='sheet'>
															<CriteriaBuilderForm
																channelId={
																	channelId
																}
																groupId={
																	groupId
																}
																id={id}
																name='criteria'
																validate={
																	validateSegmentEditor
																}
															/>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</Form.Form>
							);
						}}
					</Form>
				</div>
			</DndProvider>
		);
	}
}

export default withReferencedObjectsProvider(SegmentEditor);
