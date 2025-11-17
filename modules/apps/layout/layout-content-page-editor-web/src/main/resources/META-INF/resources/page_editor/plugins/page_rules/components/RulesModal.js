/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {
	ScreenReaderAnnouncerContextProvider,
	isNullOrUndefined,
} from '@liferay/layout-js-components-web';
import {CodeEditor} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import {openToast, useId} from 'frontend-js-components-web';
import React, {useEffect, useRef, useState} from 'react';
import {v4 as uuidv4} from 'uuid';

import {config} from '../../../app/config';
import {FRAGMENT_ENTRY_TYPES} from '../../../app/config/constants/fragmentEntryTypes';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../app/config/constants/freemarkerFragmentEntryProcessor';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../app/config/constants/layoutDataItemTypes';
import {useSelectItem} from '../../../app/contexts/ControlsContext';
import {useDispatch, useSelector} from '../../../app/contexts/StoreContext';
import selectFormConfiguration from '../../../app/selectors/selectFormConfiguration';
import selectFragmentEntryLink from '../../../app/selectors/selectFragmentEntryLink';
import FormService from '../../../app/services/FormService';
import RulesService from '../../../app/services/RulesService';
import addRule from '../../../app/thunks/addRule';
import updateRule from '../../../app/thunks/updateRule';
import {CACHE_KEYS, getCacheKey} from '../../../app/utils/cache';
import {isLayoutDataItemDeleted} from '../../../app/utils/isLayoutDataItemDeleted';
import useCache from '../../../app/utils/useCache';
import {
	RuleBuilderActionSection,
	RuleBuilderConditionSection,
} from './RuleBuilderSection';
export default function RulesModal({editingRule, onCloseModal}) {
	const {observer, onClose} = useModal({
		onClose: () => onCloseModal(editingRule?.id),
	});

	const layoutData = useSelector((state) => state.layoutData);
	const state = useSelector((state) => state);

	const rules = layoutData.pageRules;

	const dispatch = useDispatch();
	const nameId = useId();

	const [name, setName] = useState(
		editingRule?.name || getDefaultName(rules)
	);

	const [sidebarElements, setSidebarElements] = useState(
		config.codeEditorSidebarElements
	);

	const selectItem = useSelectItem();

	useEffect(() => {
		selectItem(null);
	}, [selectItem]);

	useEffect(() => {
		getFormFieldsSections(state).then((sections) => {
			setSidebarElements([
				...config.codeEditorSidebarElements,
				...sections,
			]);
		});
	}, [state]);

	const [script, setScript] = useState(editingRule?.script);

	const [nameError, setNameError] = useState(false);
	const [ruleError, setRuleError] = useState(false);

	const [actions, setActions] = useState(
		() => editingRule?.actions || [{id: uuidv4()}]
	);
	const [conditions, setConditions] = useState(
		() => editingRule?.conditions || [{id: uuidv4()}]
	);
	const [conditionType, setConditionType] = useState('all');

	const onSave = () => {
		if (!name) {
			setNameError(true);

			return;
		}

		if (
			actions.some((action) => !action.itemId) ||
			(conditions.some((condition) => !condition.options?.value) &&
				!script)
		) {
			setRuleError(true);

			return;
		}

		if (editingRule) {
			dispatch(
				updateRule({
					actions,
					conditionType,
					conditions,
					name,
					ruleId: editingRule.id,
					script,
				})
			).then(() =>
				openToast({
					message: Liferay.Language.get(
						'the-rule-was-updated-successfully'
					),
					type: 'success',
				})
			);
		}
		else {
			dispatch(
				addRule({
					actions,
					conditionType,
					conditions,
					name,
					script,
				})
			).then(() =>
				openToast({
					message: Liferay.Language.get(
						'the-rule-was-created-successfully'
					),
					type: 'success',
				})
			);
		}

		onClose();
	};

	const title = editingRule
		? Liferay.Language.get('edit-rule')
		: Liferay.Language.get('new-rule');

	return (
		<ClayModal
			containerProps={{className: 'cadmin'}}
			observer={observer}
			size="lg"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>
				<ErrorAlert setVisible={setRuleError} visible={ruleError} />

				<ClayForm.Group
					className={classNames({'has-error': nameError})}
				>
					<label htmlFor={nameId}>
						{Liferay.Language.get('rule-name')}

						<ClayIcon
							className="ml-1 reference-mark"
							focusable="false"
							role="presentation"
							symbol="asterisk"
						/>
					</label>

					<ClayInput
						id={nameId}
						onChange={(event) => {
							if (event.target.value) {
								setNameError(false);
							}

							setName(event.target.value);
						}}
						value={name}
					/>

					{nameError && (
						<ClayForm.FeedbackGroup>
							<ClayForm.FeedbackItem>
								<ClayForm.FeedbackIndicator symbol="exclamation-full" />

								{Liferay.Language.get('this-field-is-required')}
							</ClayForm.FeedbackItem>
						</ClayForm.FeedbackGroup>
					)}
				</ClayForm.Group>

				<p className="py-3">
					{Liferay.Language.get(
						'add-at-least-one-condition-and-one-action-to-complete-the-rule'
					)}
				</p>

				<ScreenReaderAnnouncerContextProvider>
					<div
						aria-label={Liferay.Language.get('conditions')}
						role="group"
					>
						{!isNullOrUndefined(script) ? (
							<CodeEditor
								error=""
								onChange={(template) => {
									setScript(template);
								}}
								placeholder="Esto es un test"
								sidebarElements={sidebarElements}
								value={script}
							/>
						) : (
							<RuleBuilderConditionSection
								conditionType={conditionType}
								conditions={conditions}
								setConditionType={setConditionType}
								setConditions={(conditions) => {
									setRuleError(false);

									setConditions(conditions);
								}}
								setScript={setScript}
							/>
						)}
					</div>

					<div
						aria-label={Liferay.Language.get('actions')}
						role="group"
					>
						<RuleBuilderActionSection
							actions={actions}
							setActions={(actions) => {
								setRuleError(false);

								setActions(actions);
							}}
						/>
					</div>
				</ScreenReaderAnnouncerContextProvider>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={onSave}>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

function getDefaultName(rules) {
	const nameIsUsed = (rules, name) =>
		rules.some((rule) => rule.name === name);

	let name = Liferay.Language.get('rule');
	let suffix = 0;

	while (nameIsUsed(rules, name)) {
		suffix++;

		name = `${Liferay.Language.get('rule')} ${suffix}`;
	}

	return name;
}

function ErrorAlert({setVisible, visible}) {
	const alertRef = useRef();

	useEffect(() => {
		if (visible) {
			alertRef.current?.scrollIntoView?.({
				behavior: 'smooth',
				block: 'center',
			});
		}
	}, [visible]);

	if (!visible) {
		return null;
	}

	return (
		<div ref={alertRef}>
			<ClayAlert
				displayType="danger"
				hideCloseIcon={false}
				onClose={() => setVisible(false)}
				title={Liferay.Language.get('error')}
			>
				{Liferay.Language.get(
					'the-rule-is-incomplete.-please-check-that-the-conditions-and-actions-are-completed-before-saving'
				)}
			</ClayAlert>
		</div>
	);
}

export async function getFormFieldsSections(state) {
	const formItems = Object.values(state.layoutData.items).filter(
		(item) =>
			item.type === LAYOUT_DATA_ITEM_TYPES.form &&
			item.config.classNameId !== '0' &&
			!isLayoutDataItemDeleted(state.layoutData, item.itemId)
	);

	const sections = [];

	for (const formItem of formItems) {
		const selectedInputFields = findSelectedFormInputFields(
			state,
			formItem.itemId
		);

		const {classNameId, classTypeId} = selectFormConfiguration(
			formItem,
			state.layoutData
		);

		const selectedType = config.formTypes.find(
			({value}) => value === classNameId
		);

		if (!classNameId) {
			return false;
		}

		const cacheKey = getCacheKey([
			CACHE_KEYS.formFields,
			classNameId,
			classTypeId,
		]);

		const {data: fields} = cacheKey;

		const promise = fields
			? Promise.resolve(fields)
			: FormService.getFormFields({
					classNameId,
					classTypeId,
				});

		const formFields = await promise;

		const items = formFields
			.flatMap((field) => ('fields' in field ? field.fields : []))
			.filter(
				(field) =>
					'key' in field &&
					selectedInputFields.some(
						(inputField) => inputField.fieldId === field.key
					)
			)
			.map((field) => {
				const inputField = selectedInputFields.find(
					(inputField) => inputField.fieldId === field.key
				);

				return {
					content: 'input__' + inputField.itemId.replace(/-/g, '_'),
					itemId: inputField.itemId,
					label: field.label,
				};
			});

		sections.push({
			items,
			label: selectedType.label,
		});
	}

	const cacheKey = getCacheKey([CACHE_KEYS.roles]);

	const {data: roles} = cacheKey;

	const rolesPromise = roles
		? Promise.resolve(roles)
		: RulesService.getRoles();

	const rolesData = await rolesPromise;

	sections.push({
		items: rolesData.map((role) => ({
			content: role.roleId,
			label: role.name,
		})),
		label: Liferay.Language.get('roles'),
	});

	return sections;
}

const FIELD_ID_CONFIGURATION_KEY = 'inputFieldId';

export function findSelectedFormInputFields(state, formId) {
	const selectedInputFields = [];

	const findSelectedFields = (itemId) => {
		const inputItem = state.layoutData.items[itemId];

		if (inputItem?.type === LAYOUT_DATA_ITEM_TYPES.fragment) {
			const {editableValues, fragmentEntryType} = selectFragmentEntryLink(
				state,
				inputItem
			);

			if (
				fragmentEntryType === FRAGMENT_ENTRY_TYPES.input &&
				editableValues[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]?.[
					FIELD_ID_CONFIGURATION_KEY
				]
			) {
				selectedInputFields.push({
					fieldId:
						editableValues[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR][
							FIELD_ID_CONFIGURATION_KEY
						],
					itemId: inputItem.itemId,
				});
			}
		}

		inputItem?.children.forEach(findSelectedFields);
	};

	findSelectedFields(formId);

	return selectedInputFields;
}
