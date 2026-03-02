/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayLocalizedInput from '@clayui/localized-input';
import classNames from 'classnames';
import {FieldArray, withFormik} from 'formik';
import {
	openConfirmModal,
	openModal,
	openToast,
} from 'frontend-js-components-web';
import {debounce, fetch, navigate} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import {useMovementSource} from '../../contexts/KeyboardMovementContext';
import {
	applyConjunctionChangeToContributor,
	applyCriteriaChangeToContributors,
	initialContributorsToContributors,
} from '../../utils/contributors';
import {initialContributorShape} from '../../utils/types.es';
import {sub} from '../../utils/utils';
import ContributorInputs from '../criteria_builder/ContributorInputs.es';
import ContributorsBuilder from '../criteria_builder/ContributorsBuilder';
import KeyboardMovementManager from '../keyboard_movement/KeyboardMovementManager';
import KeyboardMovementPreview from '../keyboard_movement/KeyboardMovementPreview';

function SegmentEdit({
	availableLocales,
	contributors: initialContributors = [],
	defaultLanguageId,
	formId,
	hasUpdatePermission,
	initialMembersCount,
	isSegmentationEnabled,
	learnResources,
	locale,
	portletNamespace,
	previewMembersURL,
	propertyGroups,
	redirect,
	requestMembersCountURL,
	scopeName,
	segmentsConfigurationURL,
	setFieldValue,
	showInEditMode = false,
	validateForm,
	values,
}) {
	const contributors = initialContributorsToContributors(
		initialContributors,
		propertyGroups
	);

	const [data, setData] = useState({
		contributors,
		disabledSave: queryIsEmpty(contributors),
		editing: showInEditMode,
		hasChanged: false,
		hasEmptyValues: false,
		isSegmentationDisabledAlertDismissed: false,
		membersCount: initialMembersCount,
		membersCountLoading: false,
		validTitle: !!values.name[defaultLanguageId],
	});

	const [selectedLocale, setSelectedLocale] = useState({
		label: standardizeLocaleKey(defaultLanguageId),
		symbol: standardizeLocaleKey(defaultLanguageId).toLowerCase(),
	});

	const [translations, setTranslations] = useState(
		formatLocales(values.name, standardizeLocaleKey)
	);

	const fetchMembersCount = () => {
		const formElement = document.getElementById(formId);
		const formData = new FormData(formElement);

		fetch(requestMembersCountURL, {
			body: formData,
			method: 'POST',
		})
			.then((response) => response.json())
			.then((membersCount) => {
				setData((prevState) => ({
					...prevState,
					membersCount,
					membersCountLoading: false,
				}));
			})
			.catch(() => {
				setData((prevState) => ({
					...prevState,
					membersCountLoading: false,
				}));

				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	};

	const debouncedFetchMembersCount = debounce(fetchMembersCount, 500);

	const handleCriteriaEdit = () => {
		if (!data.editing) {
			document.querySelector('.criteria-sidebar-root')?.focus();
		}

		setData((prevState) => {
			return {...prevState, editing: !data.editing};
		});
	};

	const handleLocalizedInputChange = (translations) => {
		setFieldValue('name', formatLocales(translations, normalizeLocaleKey));

		setData((prevState) => {
			const validTitle =
				!!translations[standardizeLocaleKey(defaultLanguageId)];

			return {
				...prevState,
				hasChanged: true,
				validTitle,
			};
		});

		setTranslations(translations);
	};

	const handleQueryChange = (criteriaChange, index) => {
		setData((prevState) => {
			const contributors = applyCriteriaChangeToContributors(
				prevState.contributors,
				{
					criteriaChange,
					propertyKey: index,
				}
			);

			return {
				...prevState,
				contributors,
				disabledSave: queryIsEmpty(contributors),
				hasChanged: true,
				hasEmptyValues: false,
				membersCountLoading: true,
			};
		});

		debouncedFetchMembersCount();
	};

	const handleConjunctionChange = (conjunctionName) => {
		setData((prevState) => {
			const contributors = applyConjunctionChangeToContributor(
				prevState.contributors,
				conjunctionName
			);

			return {
				...prevState,
				contributors,
				hasChanged: true,
				membersCountLoading: true,
			};
		});

		debouncedFetchMembersCount();
	};

	const handleAlertClose = () => {
		setData((prevState) => {
			return {
				...prevState,
				hasEmptyValues: false,
			};
		});
	};

	/**
	 * Decides wether to redirect the user or warn
	 *
	 * @memberof SegmentEdit
	 */
	const handleCancelButton = () => {
		if (data.hasChanged) {
			openConfirmModal({
				message: Liferay.Language.get(
					'criteria-cancel-confirmation-message'
				),
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						navigate(redirect);
					}
				},
			});
		}
		else {
			navigate(redirect);
		}
	};

	/**
	 * Opens a modal from to show the view from `this.props.previewMembersURL`
	 *
	 * @memberof SegmentEdit
	 */
	const handlePreviewMembers = () => {
		const {name} = values;
		const segmentLocalizedName = name[locale];

		openModal({
			id: 'segment-members-dialog',
			size: 'full-screen',
			title: sub(Liferay.Language.get('x-members'), [
				Liferay.Util.escape(segmentLocalizedName),
			]),
			url: previewMembersURL,
		});
	};

	/**
	 * Validates fields with validation and prevents the default form submission
	 * if there are any errors.
	 *
	 * Form submission is defined by the action attribute on the <form> element
	 * outside of this component. Since we are leveraging the <aui:form> taglib
	 * to handle submission and formik to handle value changes and validation,
	 * this method uses formik to validate and prevents the taglib form action
	 * from being called.
	 * @param {Class} event Event to prevent a form submission from occurring.
	 */
	const handleValidate = (event) => {
		const hasEmptyValues = queryHasEmptyValues(data.contributors);

		setData((prevState) => {
			return {
				...prevState,
				hasEmptyValues,
			};
		});

		if (hasEmptyValues) {
			event.preventDefault();

			return;
		}

		event.persist();

		validateForm().then((errors) => {
			const errorMessages = Object.values(errors);

			if (errorMessages.length) {
				event.preventDefault();

				errorMessages.forEach((message) => {
					openToast({
						message,
						type: 'danger',
					});
				});
			}
		});
	};

	const renderContributors = () => {
		const emptyContributors = queryIsEmpty(data.contributors);

		const segmentName = values.name[locale];

		return propertyGroups && data.contributors ? (
			<ContributorsBuilder
				contributors={data.contributors}
				editing={data.editing}
				emptyContributors={emptyContributors}
				isSegmentationDisabledAlertDismissed={
					data.isSegmentationDisabledAlertDismissed
				}
				isSegmentationEnabled={isSegmentationEnabled}
				learnResources={learnResources}
				membersCount={data.membersCount}
				membersCountLoading={data.membersCountLoading}
				onAlertClose={handleAlertClose}
				onConjunctionChange={handleConjunctionChange}
				onPreviewMembers={handlePreviewMembers}
				onQueryChange={handleQueryChange}
				propertyGroups={propertyGroups}
				renderEmptyValuesErrors={data.hasEmptyValues}
				requestMembersCountURL={requestMembersCountURL}
				scopeName={scopeName}
				segmentName={segmentName}
			/>
		) : null;
	};

	const renderLocalizedInputs = () => {
		const langs = Object.keys(values.name);

		return langs.map((key) => {
			let returnVal;
			const value = values.name[key];

			if (key === defaultLanguageId) {
				returnVal = (
					<React.Fragment key={key}>
						<input
							name={`${portletNamespace}name_${key}`}
							readOnly
							type="hidden"
							value={value}
						/>

						<input
							name={`${portletNamespace}key`}
							readOnly
							type="hidden"
							value={value}
						/>

						<input
							name={`${portletNamespace}name`}
							readOnly
							type="hidden"
							value={value}
						/>
					</React.Fragment>
				);
			}
			else {
				returnVal = (
					<React.Fragment key={key}>
						<input
							name={`${portletNamespace}name_${key}`}
							readOnly
							type="hidden"
							value={value}
						/>
					</React.Fragment>
				);
			}

			return returnVal;
		});
	};

	const disabledSaveButton = data.disabledSave || !data.validTitle;

	const placeholder = Liferay.Language.get('untitled-segment');

	const showDisabledSegmentationAlert =
		!isSegmentationEnabled && !data.isSegmentationDisabledAlertDismissed;

	const editButtonTitle = data.editing
		? Liferay.Language.get('enter-view-mode')
		: Liferay.Language.get('enter-edit-mode');

	const movementSource = useMovementSource();

	return (
		<>
			{movementSource ? (
				<>
					<KeyboardMovementManager
						contributors={data.contributors}
						onMove={handleQueryChange}
					/>
					<KeyboardMovementPreview />
				</>
			) : null}

			<div
				className={classNames('segment-edit-page-root', {
					'segment-edit-page-root--has-alert': data.hasEmptyValues,
					'segment-edit-page-root--with-warning':
						showDisabledSegmentationAlert,
				})}
			>
				<input
					name={`${portletNamespace}active`}
					type="hidden"
					value={values.active}
				/>

				<div className="form-header">
					<ClayLayout.ContainerFluid className="form-header-container">
						<div className="form-header-section-left">
							<FieldArray
								name="values.name"
								render={renderLocalizedInputs}
							/>

							<div className="clay-localized-input">
								<ClayLocalizedInput
									data-testid="localized-input-button"
									label=""
									locales={Object.keys(availableLocales)
										.sort((languageId) =>
											languageId === defaultLanguageId
												? -1
												: 1
										)
										.map((initLabel) => {
											const label =
												standardizeLocaleKey(initLabel);

											return {
												label,
												symbol: label.toLowerCase(),
											};
										})}
									onSelectedLocaleChange={setSelectedLocale}
									onTranslationsChange={
										handleLocalizedInputChange
									}
									placeholder={placeholder}
									readOnly={!data.editing}
									selectedLocale={selectedLocale}
									translations={translations}
								/>
							</div>
						</div>

						{hasUpdatePermission && (
							<div className="form-header-section-right">
								<div className="btn-group">
									<div className="btn-group-item">
										<ClayButton
											className="text-capitalize"
											displayType="secondary"
											onClick={handleCancelButton}
											size="sm"
										>
											{Liferay.Language.get('cancel')}
										</ClayButton>
									</div>

									<div className="btn-group-item">
										<ClayButton
											className="text-capitalize"
											disabled={disabledSaveButton}
											displayType="primary"
											onClick={(event) =>
												handleValidate(event)
											}
											size="sm"
											type="submit"
										>
											{Liferay.Language.get('save')}
										</ClayButton>
									</div>

									<div className="btn-group-item">
										<ClayButtonWithIcon
											aria-label={editButtonTitle}
											borderless
											displayType="secondary"
											onClick={handleCriteriaEdit}
											outline
											role="tab"
											size="sm"
											symbol="cog"
											title={editButtonTitle}
										/>
									</div>
								</div>
							</div>
						)}
					</ClayLayout.ContainerFluid>
				</div>

				<div className="form-body">
					{showDisabledSegmentationAlert && (
						<ClayAlert
							className="mx-0"
							displayType="warning"
							onClose={() =>
								setData((prevState) => ({
									...prevState,
									isSegmentationDisabledAlertDismissed: true,
								}))
							}
							variant="stripe"
						>
							<strong className="lead">
								{Liferay.Language.get(
									'segmentation-is-disabled'
								)}
							</strong>

							{segmentsConfigurationURL ? (
								<ClayLink href={segmentsConfigurationURL}>
									{Liferay.Language.get(
										'to-enable,-go-to-instance-settings'
									)}
								</ClayLink>
							) : (
								Liferay.Language.get(
									'contact-your-system-administrator-to-enable-it'
								)
							)}
						</ClayAlert>
					)}

					<FieldArray
						name="contributors"
						render={renderContributors}
					/>

					<ContributorInputs contributors={data.contributors} />
				</div>
			</div>
		</>
	);
}

SegmentEdit.propTypes = {
	availableLocales: PropTypes.object.isRequired,
	contributors: PropTypes.arrayOf(initialContributorShape),
	defaultLanguageId: PropTypes.string.isRequired,
	formId: PropTypes.string,
	hasUpdatePermission: PropTypes.bool,
	initialMembersCount: PropTypes.number,
	isSegmentationEnabled: PropTypes.bool,
	locale: PropTypes.string.isRequired,
	portletNamespace: PropTypes.string,
	previewMembersURL: PropTypes.string,
	propertyGroups: PropTypes.array,
	redirect: PropTypes.string.isRequired,
	requestMembersCountURL: PropTypes.string,
	scopeName: PropTypes.string,
	segmentsConfigurationURL: PropTypes.string,
	setFieldValue: PropTypes.func,
	showInEditMode: PropTypes.bool,
	validateForm: PropTypes.func,
	values: PropTypes.object,
};

/**
 * Checks if every query in each contributor has a value.
 * @return {boolean} True if none of the contributor's queries have a value.
 */
function queryIsEmpty(contributors) {
	return contributors.every((contributor) => !contributor.query);
}

/**
 * Checks if every item inside criteriaMap > items array has empry/falsy value in its value property.
 * @return {boolean} True if a non trythy values is found.
 */
function queryHasEmptyValues(contributors) {
	const checkForEmptyValuesInItems = (items) => {
		return items.some((item) => {
			const {items, value} = item;

			if (Object.prototype.hasOwnProperty.call(item, 'items')) {
				return checkForEmptyValuesInItems(items);
			}

			if (Object.prototype.hasOwnProperty.call(item, 'value')) {
				return !value.trim();
			}

			return false;
		});
	};

	/* get all items form each contributor object, generating a plain array */
	const items = contributors.reduce(
		(acc, contributor) => [
			...acc,
			...(contributor.criteriaMap?.items || []),
		],
		[]
	);

	return checkForEmptyValuesInItems(items);
}

function standardizeLocaleKey(key) {
	return key.replace(/_/g, '-');
}

function normalizeLocaleKey(key) {
	return key.replace(/-/g, '_');
}

function formatLocales(locales, fnFormat) {
	return Object.keys(locales).reduce((acc, cur) => {
		const label = fnFormat(cur);

		acc[label] = locales[cur];

		return acc;
	}, {});
}

export default withFormik({
	mapPropsToValues: (props) => ({
		active: props.initialSegmentActive || true,
		contributors: props.contributors || [],
		name: props.initialSegmentName || {},
	}),
	validate: (values) => {
		const errors = {};

		if (!values.name) {
			errors.name = Liferay.Language.get('segment-name-is-required');
		}

		return errors;
	},
})(SegmentEdit);
