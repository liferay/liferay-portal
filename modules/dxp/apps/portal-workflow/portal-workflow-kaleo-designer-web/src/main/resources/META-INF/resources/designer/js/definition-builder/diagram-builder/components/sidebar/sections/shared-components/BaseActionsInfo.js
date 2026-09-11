/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput, ClaySelect} from '@clayui/form';
import PropTypes from 'prop-types';
import React, {useContext, useEffect} from 'react';

import {DefinitionBuilderContext} from '../../../../../DefinitionBuilderContext';
import {filterScriptOption} from '../../../../util/filterScriptOption';
import {sortElements} from '../utils';
import HelpIcon from './HelpIcon';

const BaseActionsInfo = ({
	actionTypes,
	description,
	executionType,
	executionTypeInput,
	executionTypeOptions,
	name,
	placeholderName,
	placeholderScript,
	priority,
	script,
	scriptLanguage,
	selectedActionType,
	setDescription,
	setExecutionType,
	setExecutionTypeOptions,
	setName,
	setPriority,
	setScript,
	setScriptLanguage,
	setSelectedActionType,
	setStatus,
	status,
	statuses,
	updateActionInfo,
}) => {
	const {
		allowScriptContentToBeExecutedOrIncluded,
		hadGroovyOrJavaScriptBefore,
	} = useContext(DefinitionBuilderContext);

	useEffect(() => {
		if (executionTypeOptions) {
			sortElements(executionTypeOptions, 'value');

			return function cleanup() {
				setExecutionTypeOptions(
					executionTypeOptions.filter(({value}) => {
						return value !== 'onAssignment';
					})
				);
			};
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const filteredActionTypes = filterScriptOption(
		allowScriptContentToBeExecutedOrIncluded,
		hadGroovyOrJavaScriptBefore,
		actionTypes
	);

	return (
		<>
			<ClayForm.Group>
				<label htmlFor="name">
					{Liferay.Language.get('name')}

					<span className="ml-1 mr-1 text-warning">*</span>
				</label>

				<ClayInput
					id="name"
					onBlur={() =>
						updateActionInfo({
							description,
							executionType,
							name,
							priority,
							script,
							scriptLanguage,
							status,
						})
					}
					onChange={({target}) => {
						setName(target.value);
					}}
					placeholder={placeholderName}
					type="text"
					value={name}
				/>
			</ClayForm.Group>
			<ClayForm.Group>
				<label htmlFor="description">
					{Liferay.Language.get('description')}
				</label>

				<ClayInput
					id="description"
					onBlur={() =>
						updateActionInfo({
							description,
							executionType,
							name,
							priority,
							script,
							scriptLanguage,
							status,
						})
					}
					onChange={({target}) => {
						setDescription(target.value);
					}}
					type="text"
					value={description}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="type">
					{Liferay.Language.get('type')}

					<span className="ml-1 mr-1 text-warning">*</span>
				</label>

				<ClaySelect
					aria-label="Select"
					className={!selectedActionType ? 'select-placeholder' : ''}
					defaultValue={status ? 'update-status' : scriptLanguage}
					id="type"
					onChange={({target}) => {
						setScriptLanguage(target.value);
						setSelectedActionType(
							actionTypes.find(
								(item) => item.value === target.value
							)
						);
						setScript('');
						setStatus('');
					}}
					onClickCapture={() =>
						updateActionInfo({
							description,
							executionType,
							name,
							priority,
							script,
							scriptLanguage,
							status,
						})
					}
				>
					<ClaySelect.Option
						hidden
						key={0}
						label={Liferay.Language.get('select-a-script-type')}
						value="select-a-script-type"
					/>

					{actionTypes &&
						filteredActionTypes.map((item, index) => (
							<ClaySelect.Option
								className="select-options"
								key={index + 1}
								label={item.label}
								value={item.value}
							/>
						))}
				</ClaySelect>
			</ClayForm.Group>

			{selectedActionType?.type === 'script' && (
				<ClayForm.Group>
					<label htmlFor="script">
						{Liferay.Language.get('script')}

						<span className="ml-1 mr-1 text-warning">*</span>
					</label>

					<ClayInput
						component="textarea"
						id="script"
						onBlur={() =>
							updateActionInfo({
								description,
								executionType,
								name,
								priority,
								script,
								scriptLanguage,
								status: undefined,
							})
						}
						onChange={({target}) => {
							setScript(target.value);
						}}
						placeholder={placeholderScript}
						type="text"
						value={script}
					/>
				</ClayForm.Group>
			)}

			{selectedActionType?.type === 'status' && (
				<ClayForm.Group>
					<label htmlFor="update-status">Status</label>

					<ClaySelect
						aria-label="Select"
						className={!status ? 'select-placeholder' : ''}
						defaultValue={status}
						id="update-status"
						onChange={({target}) => {
							setStatus(target.value);
						}}
						onClickCapture={() =>
							updateActionInfo({
								description,
								executionType,
								name,
								priority,
								scriptLanguage,
								status,
							})
						}
					>
						<ClaySelect.Option
							hidden
							key={0}
							label={Liferay.Language.get('choose-an-option')}
							value="choose-an-option"
						/>

						{statuses &&
							statuses.map((item) => (
								<ClaySelect.Option
									key={item.value}
									label={item.label}
									value={item.value}
								/>
							))}
					</ClaySelect>
				</ClayForm.Group>
			)}

			{typeof executionTypeInput !== 'undefined' && (
				<ClayForm.Group>
					<label htmlFor="execution-type">
						{Liferay.Language.get('execution-type')}
					</label>

					<ClaySelect
						aria-label="Select"
						defaultValue={executionType}
						id="execution-type"
						onChange={({target}) => {
							setExecutionType(target.value);
						}}
						onClickCapture={() =>
							updateActionInfo({
								description,
								executionType,
								name,
								priority,
								script,
								scriptLanguage,
								status,
							})
						}
					>
						{executionTypeOptions &&
							executionTypeOptions.map((item) => (
								<ClaySelect.Option
									key={item.value}
									label={item.label}
									value={item.value}
								/>
							))}
					</ClaySelect>
				</ClayForm.Group>
			)}

			<ClayForm.Group>
				<label htmlFor="priority">
					{Liferay.Language.get('priority')}
				</label>

				<HelpIcon
					className="ml-1"
					message={Liferay.Language.get(
						'lower-numbers-represent-higher-priority'
					)}
				/>

				<ClayInput
					aria-label="Select"
					id="priority"
					onBlur={({target}) => {
						const {value: newValue} = target;
						setPriority(newValue);

						updateActionInfo({
							description,
							executionType,
							name,
							priority,
							script,
							scriptLanguage,
							status,
						});
					}}
					onChange={({target}) => {
						const {value: newValue} = target;
						setPriority(newValue);
					}}
					onWheel={(event) => event.target.blur()}
					type="number"
					value={priority}
				/>
			</ClayForm.Group>
		</>
	);
};

BaseActionsInfo.propTypes = {
	actionTypes: PropTypes.array,
	description: PropTypes.string,
	executionType: PropTypes.string,
	executionTypeInput: PropTypes.func,
	executionTypeOptions: PropTypes.array,
	name: PropTypes.string,
	placeholderName: PropTypes.string,
	placeholderScript: PropTypes.string,
	priority: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
	script: PropTypes.string,
	scriptLanguage: PropTypes.string,
	selectedItem: PropTypes.object,
	setDescription: PropTypes.func,
	setExecutionType: PropTypes.func,
	setExecutionTypeOptions: PropTypes.func,
	setName: PropTypes.func,
	setPriority: PropTypes.func,
	setScript: PropTypes.func,
	setScriptLanguage: PropTypes.func,
	updateActionInfo: PropTypes.func,
};

export default BaseActionsInfo;
