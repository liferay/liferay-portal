/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDatePicker from '@clayui/date-picker';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classnames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {dateUtils} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useContext, useEffect, useState} from 'react';

import ChartContext from '../ChartContext';
import {getUser, getUserFullNameDefinition, updateUser} from '../data/users';
import FieldsWrapper from '../objects/FieldsWrapper';
import LogoSelector from '../utils/LogoSelector';
import {
	ACTION_KEYS,
	DEFAULT_IMAGE_PATHS_MAP,
	DEFAULT_USER_ACCOUNT_FULL_NAME_DEFINITION_FIELDS,
	INFO_PANEL_MODE_MAP,
	MODEL_TYPE_MAP,
	SYMBOLS_MAP,
} from '../utils/constants';
import {hasPermission} from '../utils/index';

function EditUserInfoPanel({
	data,
	namespace,
	pathImage,
	selectLogoURL,
	spritemap,
	type,
	updatePanelViewHandler,
}) {
	const [userData, setUserData] = useState({
		...data,
		errors: {},
		isValid: true,
	});

	const [isLoading, setIsLoading] = useState(false);
	const [userObjectDefinition, setUserObjectDefinition] = useState([]);
	const {chartInstanceRef} = useContext(ChartContext);

	const [userLanguageId, setUserLanguageId] = useState(data.languageId);
	const [fullNameDefinition, setFullNameDefinition] = useState([]);

	useEffect(() => {
		getUserFullNameDefinition(userLanguageId).then((data) => {
			setFullNameDefinition(
				data?.userAccountFullNameDefinitionFields ||
					DEFAULT_USER_ACCOUNT_FULL_NAME_DEFINITION_FIELDS
			);
		});
	}, [userLanguageId]);

	useEffect(() => {
		if (!userData.id || userData.fullLoaded) {
			return;
		}

		setIsLoading(true);

		getUser(userData.id)
			.then((newData) => {
				newData = Object.assign(userData, newData);
				newData.fullLoaded = true;
				newData.modelType = newData.type;
				newData.type = type;

				chartInstanceRef.current.updateNodeContent(newData);

				setUserData((prevState) => ({
					...prevState,
					...newData,
				}));

				setIsLoading(false);
			})
			.catch((error) => {
				openToast({
					message:
						error.message ||
						error.title ||
						Liferay.Language.get('an-error-occurred'),
					title: Liferay.Language.get('error'),
					type: 'danger',
				});

				setIsLoading(false);
			});

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [userData.id]);

	const isFieldVisible = (key) => {
		return !!fullNameDefinition.find((item) => {
			return item.key === key;
		});
	};

	const isFieldRequired = (key) => {
		const field = fullNameDefinition.find((item) => {
			return item.key === key;
		});

		return field && field.required;
	};

	const fieldValues = (key) => {
		const field = fullNameDefinition.find((item) => {
			return item.key === key;
		});

		return field?.values || [];
	};

	const onChangeHandler = ({target}) => {
		const errors = userData.errors;
		const targetName = target.name.replace(namespace, '');

		if (
			['alternateName', 'birthDate', 'emailAddress'].indexOf(
				targetName
			) >= 0 ||
			(targetName === 'additionalName' &&
				isFieldRequired('middle-name')) ||
			(targetName === 'givenName' && isFieldRequired('first-name')) ||
			(targetName === 'familyName' && isFieldRequired('last-name')) ||
			(targetName === 'honorificPrefix' && isFieldRequired('prefix')) ||
			(targetName === 'honorificSuffix' && isFieldRequired('suffix'))
		) {
			if (!target.value || target.value.length <= 0) {
				errors[targetName] = Liferay.Language.get(
					'this-field-is-required'
				);
			}
			else {
				delete errors[targetName];
			}
		}

		if (['birthDate'].indexOf(targetName) >= 0) {
			if (new Date(target.value) > new Date()) {
				errors[targetName] = Liferay.Language.get(
					'please-enter-a-valid-date'
				);
			}
			else {
				delete errors[targetName];
			}
		}

		if (['languageId'].indexOf(targetName) >= 0) {
			setUserLanguageId(target.value);
		}

		setUserData((prevState) => ({
			...prevState,
			errors,
			isValid: !Object.keys(errors).length,
			[targetName]: target.value,
		}));
	};

	const onObjectFieldsChangeHandler = useCallback(
		({data, hasError, name}) => {
			const errors = userData.errors;

			if (hasError) {
				errors[name] = true;
			}
			else {
				delete errors[name];
			}

			setUserData((prevState) => ({
				...prevState,
				...data,
				errors,
				isValid: !Object.keys(errors).length,
			}));
		},
		[userData]
	);

	const onObjectDefinitionLoadHandler = useCallback(({data}) => {
		setUserObjectDefinition(data);
	}, []);

	const onSaveHandler = useCallback(() => {
		if (
			!userData.isValid ||
			!hasPermission(userData, ACTION_KEYS.user.UPDATE)
		) {
			return;
		}

		setIsLoading(true);

		const data = {
			accountBriefs: userData.accountBriefs,
			additionalName: userData.additionalName,
			alternateName: userData.alternateName,
			birthDate: new Date(Date.parse(userData.birthDate)).toISOString(),
			emailAddress: userData.emailAddress,
			familyName: userData.familyName,
			givenName: userData.givenName,
			honorificPrefix: userData.honorificPrefix,
			honorificSuffix: userData.honorificSuffix,
			imageId: userData.imageId,
			jobTitle: userData.jobTitle,
			languageId: userData.languageId,
		};

		userObjectDefinition.forEach((objectDefinition) => {
			const objectDefinitionName = objectDefinition.name;

			if (
				objectDefinitionName in userData &&
				userData[objectDefinitionName] !== null
			) {
				data[objectDefinitionName] = userData[objectDefinitionName];
			}
		});

		updateUser(userData.id, data)
			.then((newData) => {
				newData = Object.assign(userData, newData);
				newData.modelType = newData.type;
				newData.type = type;

				chartInstanceRef.current.updateNodeContent(newData);

				updatePanelViewHandler({
					data: newData,
					mode: INFO_PANEL_MODE_MAP.view,
					type,
				});
				openToast({
					message: Liferay.Language.get(
						'your-request-completed-successfully'
					),
					type: 'success',
				});
			})
			.catch((error) => {
				openToast({
					message:
						error.message ||
						error.title ||
						Liferay.Language.get('an-error-occurred'),
					title: Liferay.Language.get('error'),
					type: 'danger',
				});
			});

		setIsLoading(false);
	}, [
		userData,
		chartInstanceRef,
		type,
		updatePanelViewHandler,
		userObjectDefinition,
	]);

	const onCancelHandler = useCallback(() => {
		updatePanelViewHandler({
			data,
			mode: INFO_PANEL_MODE_MAP.view,
			type,
		});
	}, [data, type, updatePanelViewHandler]);

	return (
		<>
			<div className="sidebar-header">
				<div className="autofit-row sidebar-section">
					<div className="autofit-col autofit-col-expand">
						<h1 className="component-title">
							{data.alternateName}
						</h1>

						<h2 className="component-subtitle">
							{Liferay.Language.get('user')}
						</h2>
					</div>
				</div>
			</div>
			<div className="flex-grow-1 sidebar-body">
				<div>
					<div className="sheet-subtitle">
						{Liferay.Language.get('user-display-data')}
					</div>

					<LogoSelector
						defaultIcon={`${spritemap}#${SYMBOLS_MAP[type]}`}
						disabled={isLoading}
						logoId={userData.imageId}
						logoURL={
							userData.image ||
							pathImage + DEFAULT_IMAGE_PATHS_MAP.user
						}
						name="imageId"
						namespace={namespace}
						onChange={onChangeHandler}
						selectLogoURL={selectLogoURL}
					/>

					<ClayForm.Group
						className={classnames({
							'has-error': !!userData.errors.alternateName,
						})}
					>
						<label htmlFor={`${namespace}alternateName`}>
							{Liferay.Language.get('screen-name')}

							<ClayIcon
								className="c-ml-1 reference-mark"
								symbol="asterisk"
							/>
						</label>

						<ClayInput
							disabled={isLoading}
							id={`${namespace}alternateName`}
							name={`${namespace}alternateName`}
							onChange={onChangeHandler}
							required={true}
							type="text"
							value={userData.alternateName}
						/>

						<ErrorMessage
							errors={userData.errors}
							name="alternateName"
						/>
					</ClayForm.Group>

					<ClayForm.Group
						className={classnames({
							'has-error': !!userData.errors.emailAddress,
						})}
					>
						<label htmlFor={`${namespace}emailAddress`}>
							{Liferay.Language.get('email-address')}

							<ClayIcon
								className="c-ml-1 reference-mark"
								symbol="asterisk"
							/>
						</label>

						<ClayInput
							disabled={isLoading}
							id={`${namespace}emailAddress`}
							name={`${namespace}emailAddress`}
							onChange={onChangeHandler}
							required={true}
							type="text"
							value={userData.emailAddress}
						/>

						<ErrorMessage
							errors={userData.errors}
							name="emailAddress"
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<label htmlFor={`${namespace}userId`}>
							{Liferay.Language.get('user-id')}
						</label>

						<ClayInput
							disabled={true}
							id={`${namespace}userId`}
							name={`${namespace}userId`}
							readOnly={true}
							type="text"
							value={userData.id}
						/>
					</ClayForm.Group>
				</div>

				<div>
					<div className="sheet-subtitle">
						{Liferay.Language.get('personal-information')}
					</div>

					<ClayForm.Group
						className={classnames({
							'has-error': !!userData.errors.languageId,
						})}
					>
						<label htmlFor={`${namespace}languageId`}>
							{Liferay.Language.get('language')}
						</label>

						<ClaySelectWithOption
							disabled={isLoading}
							id={`${namespace}languageId`}
							name={`${namespace}languageId`}
							onChange={onChangeHandler}
							options={Object.keys(
								Liferay.Language.available
							).map((key) => {
								return {
									label: Liferay.Language.available[key],
									value: key,
								};
							})}
							value={userData.languageId}
						/>

						<ErrorMessage
							errors={userData.errors}
							name="languageId"
						/>
					</ClayForm.Group>

					{isFieldVisible('prefix') && (
						<ClayForm.Group
							className={classnames({
								'has-error': !!userData.errors.honorificPrefix,
							})}
						>
							<label htmlFor={`${namespace}honorificPrefix`}>
								{Liferay.Language.get('prefix')}

								{isFieldRequired('prefix') && (
									<ClayIcon
										className="c-ml-1 reference-mark"
										symbol="asterisk"
									/>
								)}
							</label>

							<ClaySelectWithOption
								disabled={isLoading}
								id={`${namespace}honorificPrefix`}
								name={`${namespace}honorificPrefix`}
								onChange={onChangeHandler}
								options={[
									{},
									...fieldValues('prefix').map((item) => {
										return {
											label: item,
											value: item,
										};
									}),
								]}
								required={isFieldRequired('prefix')}
								value={userData.honorificPrefix}
							/>

							<ErrorMessage
								errors={userData.errors}
								name="honorificPrefix"
							/>
						</ClayForm.Group>
					)}

					{isFieldVisible('first-name') && (
						<ClayForm.Group
							className={classnames({
								'has-error': !!userData.errors.givenName,
							})}
						>
							<label htmlFor={`${namespace}givenName`}>
								{Liferay.Language.get('first-name')}

								{isFieldRequired('first-name') && (
									<ClayIcon
										className="c-ml-1 reference-mark"
										symbol="asterisk"
									/>
								)}
							</label>

							<ClayInput
								disabled={isLoading}
								id={`${namespace}givenName`}
								name={`${namespace}givenName`}
								onChange={onChangeHandler}
								required={isFieldRequired('first-name')}
								type="text"
								value={userData.givenName}
							/>

							<ErrorMessage
								errors={userData.errors}
								name="givenName"
							/>
						</ClayForm.Group>
					)}

					{isFieldVisible('middle-name') && (
						<ClayForm.Group
							className={classnames({
								'has-error': !!userData.errors.additionalName,
							})}
						>
							<label htmlFor={`${namespace}additionalName`}>
								{Liferay.Language.get('middle-name')}

								{isFieldRequired('middle-name') && (
									<ClayIcon
										className="c-ml-1 reference-mark"
										symbol="asterisk"
									/>
								)}
							</label>

							<ClayInput
								disabled={isLoading}
								id={`${namespace}additionalName`}
								name={`${namespace}additionalName`}
								onChange={onChangeHandler}
								required={isFieldRequired('middle-name')}
								type="text"
								value={userData.additionalName}
							/>

							<ErrorMessage
								errors={userData.errors}
								name="additionalName"
							/>
						</ClayForm.Group>
					)}

					{isFieldVisible('last-name') && (
						<ClayForm.Group
							className={classnames({
								'has-error': !!userData.errors.familyName,
							})}
						>
							<label htmlFor={`${namespace}familyName`}>
								{Liferay.Language.get('last-name')}

								{isFieldRequired('last-name') && (
									<ClayIcon
										className="c-ml-1 reference-mark"
										symbol="asterisk"
									/>
								)}
							</label>

							<ClayInput
								disabled={isLoading}
								id={`${namespace}familyName`}
								name={`${namespace}familyName`}
								onChange={onChangeHandler}
								required={isFieldRequired('last-name')}
								type="text"
								value={userData.familyName}
							/>

							<ErrorMessage
								errors={userData.errors}
								name="familyName"
							/>
						</ClayForm.Group>
					)}

					{isFieldVisible('suffix') && (
						<ClayForm.Group
							className={classnames({
								'has-error': !!userData.errors.honorificSuffix,
							})}
						>
							<label htmlFor={`${namespace}honorificSuffix`}>
								{Liferay.Language.get('suffix')}

								{isFieldRequired('suffix') && (
									<ClayIcon
										className="c-ml-1 reference-mark"
										symbol="asterisk"
									/>
								)}
							</label>

							<ClaySelectWithOption
								disabled={isLoading}
								id={`${namespace}honorificSuffix`}
								name={`${namespace}honorificSuffix`}
								onChange={onChangeHandler}
								options={[
									{},
									...fieldValues('suffix').map((item) => {
										return {
											label: item,
											value: item,
										};
									}),
								]}
								required={isFieldRequired('suffix')}
								value={userData.honorificSuffix}
							/>

							<ErrorMessage
								errors={userData.errors}
								name="honorificSuffix"
							/>
						</ClayForm.Group>
					)}

					<ClayForm.Group
						className={classnames({
							'has-error': !!userData.errors.jobTitle,
						})}
					>
						<label htmlFor={`${namespace}jobTitle`}>
							{Liferay.Language.get('job-title')}
						</label>

						<ClayInput
							disabled={isLoading}
							id={`${namespace}jobTitle`}
							name={`${namespace}jobTitle`}
							onChange={onChangeHandler}
							type="text"
							value={userData.jobTitle}
						/>

						<ErrorMessage
							errors={userData.errors}
							name="jobTitle"
						/>
					</ClayForm.Group>

					<ClayForm.Group
						className={classnames({
							'has-error': !!userData.errors.birthDate,
						})}
					>
						<label htmlFor={`${namespace}birthDate`}>
							{Liferay.Language.get('birthday')}
						</label>

						<ClayDatePicker
							ariaLabels={{
								buttonChooseDate: `${Liferay.Language.get(
									'select-date'
								)}`,
								buttonDot: `${Liferay.Language.get(
									'select-current-date'
								)}`,
								buttonNextMonth: `${Liferay.Language.get(
									'select-next-month'
								)}`,
								buttonPreviousMonth: `${Liferay.Language.get(
									'select-previous-month'
								)}`,
								dialog: `${Liferay.Language.get('select-date')}`,
								selectMonth: `${Liferay.Language.get('select-a-month')}`,
								selectYear: `${Liferay.Language.get('select-a-year')}`,
							}}
							dateFormat="P"
							disabled={isLoading}
							firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
							id={`${namespace}birthDate`}
							inputName={`${namespace}birthDate`}
							months={[
								`${Liferay.Language.get('january')}`,
								`${Liferay.Language.get('february')}`,
								`${Liferay.Language.get('march')}`,
								`${Liferay.Language.get('april')}`,
								`${Liferay.Language.get('may')}`,
								`${Liferay.Language.get('june')}`,
								`${Liferay.Language.get('july')}`,
								`${Liferay.Language.get('august')}`,
								`${Liferay.Language.get('september')}`,
								`${Liferay.Language.get('october')}`,
								`${Liferay.Language.get('november')}`,
								`${Liferay.Language.get('december')}`,
							]}
							onChange={(value) => {
								onChangeHandler({
									target: {
										name: `${namespace}birthDate`,
										value,
									},
								});
							}}
							spritemap={spritemap}
							value={dateUtils.format(
								new Date(userData.birthDate),
								'P'
							)}
							weekdaysShort={dateUtils.getWeekdaysShort()}
							years={{
								end: new Date().getFullYear(),
								start: new Date().getFullYear() - 100,
							}}
						/>

						<ErrorMessage
							errors={userData.errors}
							name="birthDate"
						/>
					</ClayForm.Group>
				</div>

				{Liferay.FeatureFlags['COMMERCE-13024'] &&
				userData.fullLoaded ? (
					<FieldsWrapper
						mode="edit"
						namespace={namespace}
						objectData={userData}
						objectExternalReferenceCode="L_USER"
						onObjectDataChange={onObjectFieldsChangeHandler}
						onObjectDefinitionLoad={onObjectDefinitionLoadHandler}
					></FieldsWrapper>
				) : (
					<></>
				)}
			</div>

			<div className="sidebar-footer">
				<ClayButton
					disabled={!userData.isValid || isLoading}
					displayType="primary"
					monospaced={false}
					onClick={onSaveHandler}
				>
					{Liferay.Language.get('save')}
				</ClayButton>

				<ClayButton
					className="ml-2"
					disabled={isLoading}
					displayType="secondary"
					monospaced={false}
					onClick={onCancelHandler}
				>
					{Liferay.Language.get('cancel')}
				</ClayButton>
			</div>
		</>
	);
}

const ErrorMessage = ({errors, name}) => {
	return (
		<>
			{!!errors[name] && (
				<div className="form-feedback-group">
					<div className="form-feedback-item">{errors[name]}</div>
				</div>
			)}
		</>
	);
};

EditUserInfoPanel.defaultProps = {
	type: MODEL_TYPE_MAP.account,
};

EditUserInfoPanel.propTypes = {
	data: PropTypes.object.isRequired,
	namespace: PropTypes.string,
	pathImage: PropTypes.string.isRequired,
	selectLogoURL: PropTypes.string,
	spritemap: PropTypes.string.isRequired,
	type: PropTypes.string,
	updatePanelViewHandler: PropTypes.func.isRequired,
};

export default EditUserInfoPanel;
