import * as API from 'shared/api';
import BaseEditPage from 'shared/components/base-edit-page';
import Canvas from 'shared/components/canvas/Canvas';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from 'shared/components/Icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayPopover from '@clayui/popover';
import CriteriaBuilder from './criteria-builder';
import CriteriaDragPreview from './criteria-sidebar/CriteriaDragPreview';
import CriteriaSidebar from './criteria-sidebar';
import DndProvider from 'shared/components/DndProvider';
import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import Form, {
	validateExternalReferenceCode,
	validateRequired,
	withField,
} from 'shared/components/form';
import NavigationWarning from 'shared/components/NavigationWarning';
import React from 'react';
import SegmentMembership from './SegmentMembership';
import SegmentSequentialToggle from 'segment/components/SegmentSequentialToggle';
import Toolbar from './Toolbar';
import {AlertTypes} from 'shared/components/Alert';
import {ClayButtonWithIcon} from '@clayui/button';
import {
	buildQueryString,
	translateQueryToCriteria,
	wrapInCriteriaGroup,
} from './utils/odata';
import {Criteria, CriterionGroup} from './utils/types';
import {
	hasNestedOrExceeded,
	hasRootAndExceeded,
	invalidateCriterionWithMissingProperty,
	validateSegmentInputs,
} from './utils/utils';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {KeyboardMovementProvider} from './context/keyboardMovement';
import {List} from 'immutable';
import {NESTED_OR_LIMIT_ALERT, SEQUENTIAL_LIMIT_ALERT} from './utils/constants';
import {PropertyGroup, Segment} from 'shared/util/records';
import {
	ReferencedObjectsContext,
	withReferencedObjectsProvider,
} from './context/referencedObjects';
import {
	getSegmentCategoryLabel,
	getSegmentTypeLabel,
} from 'segment/utils/labels';
import {
	SegmentCategories,
	SegmentStates,
	SegmentTypes,
} from 'shared/util/constants';
import {useField} from 'formik';
import {v4 as uuidv4} from 'uuid';

/**
 * Returns an error message if the criteria contains an invalid row,
 * or if sequential mode is enabled and the criteria exceed the limit.
 */
export function validateSegmentEditor(
	criteria: CriterionGroup | null,
	sequential?: boolean
) {
	let error;

	if (
		!criteria ||
		!criteria.items.length ||
		!validateSegmentInputs(criteria)
	) {
		error = Liferay.Language.get('empty-fields');
	}
	else if (sequential) {
		if (hasNestedOrExceeded(criteria)) {
			error = NESTED_OR_LIMIT_ALERT.exceedsLimit.text;
		}
		else if (hasRootAndExceeded(criteria)) {
			error = SEQUENTIAL_LIMIT_ALERT.exceedsLimit.text;
		}
	}

	return error;
}

const CriteriaBuilderForm = withField(
	({
		channelId,
		field: {name, value},
		groupId,
		segmentCategory,
		segmentType,
		sequential,
		...fieldProps
	}: {
		channelId: string;
		field: {name: string; value: any};
		groupId: string;
		segmentCategory: SegmentCategories;
		segmentType: SegmentTypes;
		[key: string]: any;
	}) => {
		const handleChange = (criteria: Criteria) => {
			const {
				form: {setFieldValue},
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
				segmentCategory={segmentCategory}
				segmentType={segmentType}
				sequential={sequential}
			/>
		);
	}
);

const SegmentTitle: React.FC = () => {
	const [{onBlur, onChange, value}, {error, touched}] = useField<string>({
		name: 'name',
		validate: validateRequired,
	});

	return (
		<BaseEditPage.Title
			errorMessage={touched ? error : undefined}
			id="name"
			label={Liferay.Language.get('title')}
			name="name"
			onBlur={onBlur}
			onChange={onChange}
			placeholder={Liferay.Language.get('new-segment')}
			required
			value={value ?? ''}
		/>
	);
};

const SegmentDetails: React.FC<{
	segmentCategory: SegmentCategories;
	type: SegmentTypes;
}> = ({segmentCategory, type}) => {
	const [{onBlur, onChange, value}, {error, touched}] = useField<string>({
		name: 'externalReferenceCode',
		validate: validateExternalReferenceCode,
	});

	return (
		<div className="mb-4 mt-4">
			<ClayLayout.Row className="align-items-center mb-2">
				<ClayLayout.Col lg={2} md={3} sm={12}>
					<span className="align-items-center d-flex font-weight-semi-bold text-nowrap text-secondary">
						<ClayIcon className="mr-2" symbol="plus-squares" />

						{Liferay.Language.get('segment-type')}
					</span>
				</ClayLayout.Col>

				<ClayLayout.Col>
					<ClayLabel displayType="secondary" inverse>
						{getSegmentCategoryLabel(segmentCategory)}
					</ClayLabel>

					<ClayLabel displayType="info" inverse>
						{getSegmentTypeLabel(type)}
					</ClayLabel>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayForm.Group
				className={error && touched ? 'has-error mb-0' : 'mb-0'}
			>
				<ClayLayout.Row className="align-items-center">
					<ClayLayout.Col
						className="align-items-center d-flex"
						lg={2}
						md={3}
						sm={12}
					>
						<label
							className="align-items-center d-flex font-weight-semi-bold mb-0 text-nowrap text-secondary"
							htmlFor="externalReferenceCode"
						>
							<ClayIcon className="mr-2" symbol="link" />

							{Liferay.Language.get('segment-erc')}

							<ClayIcon
								className="reference-mark"
								symbol="asterisk"
							/>
						</label>

						<ClayPopover
							alignPosition="top"
							closeOnClickOutside
							header={Liferay.Language.get('segment-erc')}
							trigger={
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get('help')}
									borderless
									className="ml-2 rounded-lg"
									displayType="secondary"
									monospaced
									size="xs"
									symbol="question-circle-full"
								/>
							}
						>
							<span>
								{Liferay.Language.get(
									'unique-key-for-referencing-the-segment-definition'
								)}
							</span>

							<br />
							<br />

							<span>
								{Liferay.Language.get(
									'erc-must-contain-only-lowercase-letters-numbers-hyphens-and-underscores'
								)}
							</span>
						</ClayPopover>
					</ClayLayout.Col>

					<ClayLayout.Col lg={4} md={6}>
						<ClayInput
							id="externalReferenceCode"
							name="externalReferenceCode"
							onBlur={onBlur}
							onChange={onChange}
							required
							sizing="sm"
							type="text"
							value={value ?? ''}
						/>
					</ClayLayout.Col>
				</ClayLayout.Row>

				{error && touched && (
					<p
						className="font-weight-semi-bold mb-0 mt-1 text-danger"
						role="alert"
					>
						<ClayIcon className="mr-1" symbol="info-circle" />

						{error}
					</p>
				)}
			</ClayForm.Group>
		</div>
	);
};

type FormValues = {
	criteria: CriterionGroup;
	externalReferenceCode: string;
	includeAnonymousUsers: boolean;
	name: string;
	sequential: boolean;
};

interface ISegmentEditorProps {
	channelId: string;
	groupId: string;
	id?: string;
	onDelete: boolean;
	onDeleteSegment?: () => void;
	onSubmit: (
		form: FormValues,
		ref: React.RefObject<any>,
		requestFn: (params: FormValues) => Promise<any>
	) => void;
	propertyGroupsIList: List<PropertyGroup>;
	segment: Segment;
	segmentCategory: SegmentCategories;
	type: SegmentTypes;
}

class SegmentEditor extends React.Component<ISegmentEditorProps> {
	static contextType = ReferencedObjectsContext;

	static defaultProps = {
		segment: new Segment(),
	};

	state = {
		enabledSequentialSegment: false,
	};

	constructor(props: ISegmentEditorProps) {
		super(props);
		this.createSegment = this.createSegment.bind(this);
		this.hasChanges = this.hasChanges.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	declare context: React.ContextType<typeof ReferencedObjectsContext>;

	_defaultExternalReferenceCode = uuidv4();

	_formRef = React.createRef<any>();

	createSegment({
		criteria,
		externalReferenceCode,
		includeAnonymousUsers,
		name,
		sequential,
	}: FormValues) {
		const {
			channelId,
			groupId,
			segment: {id},
			segmentCategory,
			type,
		} = this.props;

		const request = id
			? API.individualSegment.update
			: API.individualSegment.create;

		const requestData = {
			channelId,
			criteriaString: buildQueryString([criteria]),
			description: '',
			externalReferenceCode,
			groupId,
			id,
			includeAnonymousUsers,
			name: name.trim(),
			segmentCategory,
			segmentType: type,
			sequential,
		};

		return request({...requestData});
	}

	hasChanges(
		newIncludeAnonymousUsers: boolean,
		newName: string,
		newCriteriaString: string,
		newSequential: boolean,
		newExternalReferenceCode: string
	) {
		const {
			segment: {
				criteriaString,
				externalReferenceCode,
				includeAnonymousUsers,
				name,
				sequential,
			},
		} = this.props;

		// A new segment has no external reference code, so the form seeds the
		// field with a generated one. Comparing against the raw (empty) segment
		// value would report a change the user never made, marking an untouched
		// creation form as dirty. Compare against the same value the form was
		// seeded with instead.

		return (
			newIncludeAnonymousUsers !== includeAnonymousUsers ||
			name !== newName ||
			criteriaString !== newCriteriaString ||
			sequential !== newSequential ||
			(externalReferenceCode || this._defaultExternalReferenceCode) !==
				newExternalReferenceCode
		);
	}

	handleSubmit(form: FormValues) {
		const {onSubmit} = this.props;

		onSubmit(form, this._formRef, this.createSegment);
	}

	render() {
		const {
			context: {referencedProperties},
			props: {
				channelId,
				groupId,
				id,
				onDelete,
				onDeleteSegment,
				propertyGroupsIList,
				segment: {
					criteriaString,
					externalReferenceCode,
					includeAnonymousUsers,
					name,
					sequential,
					state: segmentState,
				},
				segmentCategory,
				type,
			},
		} = this;

		return (
			<DndProvider backend={HTML5Backend}>
				<CriteriaDragPreview />

				<div className="segment-edit-page-root">
					<Form
						initialValues={{
							criteria:
								id && criteriaString
									? (invalidateCriterionWithMissingProperty(
											translateQueryToCriteria(
												criteriaString
											),
											referencedProperties as any
										) as CriterionGroup)
									: wrapInCriteriaGroup([]),
							externalReferenceCode:
								externalReferenceCode ||
								this._defaultExternalReferenceCode,
							includeAnonymousUsers,
							name,
							sequential,
						}}
						innerRef={this._formRef as any}
						onSubmit={this.handleSubmit}
						validate={(values: FormValues) => {
							const error = validateSegmentEditor(
								values.criteria,
								values.sequential
							);

							return error ? {criteria: error} : {};
						}}
						validateOnMount
					>
						{({
							handleSubmit,
							isSubmitting,
							isValid,
							setFieldValue,
							values: {
								criteria,
								externalReferenceCode,
								includeAnonymousUsers,
								name,
								sequential,
							},
						}) => {
							const newCriteriaString = buildQueryString([
								criteria,
							]);
							const hasChanges = this.hasChanges(
								includeAnonymousUsers,
								name,
								newCriteriaString,
								sequential,
								externalReferenceCode
							);

							return (
								<Form.Form
									className="contributor-builder-root d-flex editing flex-column"
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
										groupId={groupId}
										id={id ?? ''}
										onDeleteSegment={onDeleteSegment}
										valid={isValid && hasChanges}
									/>

									<KeyboardMovementProvider
										criteria={criteria}
										onChange={(newCriteria) =>
											setFieldValue(
												'criteria',
												newCriteria
											)
										}
										sequential={!!sequential}
									>
										<div className="form-body">
											<div className="criteria-builder-section-sidebar">
												<CriteriaSidebar
													channelId={channelId}
													criteriaString={
														criteriaString ??
														undefined
													}
													groupId={groupId}
													propertyGroupsIList={
														propertyGroupsIList
													}
													type={type}
												/>
											</div>

											<div className="criteria-builder-section-main">
												<div className="contributor-container">
													<div className="container-fluid container-fluid-max-xl">
														<div className="content-wrapper">
															<SegmentTitle />

															<SegmentDetails
																segmentCategory={
																	segmentCategory
																}
																type={type}
															/>

															{segmentState ===
																SegmentStates.Disabled && (
																<EmbeddedAlertList
																	alerts={[
																		{
																			iconSymbol:
																				'exclamation-full',
																			message:
																				Liferay.Language.get(
																					'some-criteria-are-empty-please-update-to-continue-using-this-segment'
																				),
																			title: Liferay.Language.get(
																				'error'
																			),
																			type: AlertTypes.Danger,
																		},
																	]}
																/>
															)}

															<Canvas className="segment-membership-root">
																<Canvas.Header
																	hasBody={
																		false
																	}
																	title={Liferay.Language.get(
																		'membership'
																	)}
																>
																	<Canvas.Actions>
																		<SegmentMembership
																			channelId={
																				channelId
																			}
																			criteria={
																				criteria
																			}
																			criteriaString={
																				newCriteriaString
																			}
																			groupId={
																				groupId
																			}
																			includeAnonymousUsers={
																				includeAnonymousUsers
																			}
																			segmentCategory={
																				segmentCategory
																			}
																			segmentType={
																				type
																			}
																		/>
																	</Canvas.Actions>
																</Canvas.Header>
															</Canvas>

															<Canvas>
																<Canvas.Header
																	title={Liferay.Language.get(
																		'segment-conditions'
																	)}
																>
																	{type ===
																		SegmentTypes.RealTime && (
																		<Canvas.Actions>
																			<SegmentSequentialToggle name="sequential" />
																		</Canvas.Actions>
																	)}
																</Canvas.Header>

																<Canvas.Body>
																	<CriteriaBuilderForm
																		channelId={
																			channelId
																		}
																		groupId={
																			groupId
																		}
																		id={id}
																		name="criteria"
																		segmentCategory={
																			segmentCategory
																		}
																		segmentType={
																			type
																		}
																		sequential={
																			sequential
																		}
																	/>
																</Canvas.Body>
															</Canvas>
														</div>
													</div>
												</div>
											</div>
										</div>
									</KeyboardMovementProvider>
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
