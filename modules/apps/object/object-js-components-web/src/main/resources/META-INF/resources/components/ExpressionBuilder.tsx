/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {FieldBase} from 'frontend-js-components-web';
import {createResourceURL, fetch} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {REQUIRED_MSG} from '../utils/constants';
import CodeEditor, {SidebarCategory} from './CodeEditor/index';

export function ExpressionBuilder({
	buttonDisabled,
	className,
	component,
	disabled,
	error,
	feedbackMessage,
	hideFeedback,
	id,
	label,
	name,
	onBlur,
	onChange,
	onInput,
	onOpenModal,
	required,
	type,
	value,
	...otherProps
}: IProps) {
	return (
		<FieldBase
			className={className}
			disabled={disabled}
			errorMessage={error}
			helpMessage={feedbackMessage}
			hideFeedback={hideFeedback}
			id={id}
			label={label}
			required={required}
		>
			<ClayInput.Group>
				<ClayInput.GroupItem prepend>
					<ClayInput
						{...otherProps}
						component={component}
						disabled={disabled}
						id={id}
						name={name}
						onBlur={onBlur}
						onChange={onChange}
						onInput={onInput}
						type={type}
						value={value}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem append shrink>
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('expand-input-area')}
						disabled={buttonDisabled}
						displayType="secondary"
						onClick={onOpenModal}
						symbol="code"
						title={Liferay.Language.get('expand-input-area')}
					/>
				</ClayInput.GroupItem>
			</ClayInput.Group>
		</FieldBase>
	);
}

export function ExpressionBuilderModal({sidebarElements}: IModalProps) {
	const editorRef = useRef<CodeMirror.Editor>(null);
	const [
		{
			error,
			eventSidebarElements,
			header,
			onSave,
			placeholder,
			required,
			source,
			validateExpressionURL,
		},
		setState,
	] = useState<{
		error?: string;
		eventSidebarElements?: SidebarCategory[];
		header?: string;
		onSave?: Callback;
		placeholder?: string;
		required?: boolean;
		source?: string;
		validateExpressionURL?: string;
	}>({});

	const {observer, onOpenChange} = useModal({
		onClose: () => setState({}),
	});

	useEffect(() => {
		const openModal = (params: {
			eventSidebarElements: SidebarCategory[];
			header: string;
			onSave: Callback;
			placeholder: string;
			required: boolean;
			source: string;
			validateExpressionURL: string;
		}) => {
			setState(params);
		};

		Liferay.on('openExpressionBuilderModal', openModal);

		return () =>
			Liferay.detach(
				'openExpressionBuilderModal',
				openModal as () => void
			);
	}, []);

	if (source === undefined) {
		return null;
	}

	const closeModal = () => {
		onOpenChange(false);
	};

	const handleSave = async () => {
		const source = editorRef.current?.getValue();

		let error: string | undefined;

		if (required && !source?.trim()) {
			error = REQUIRED_MSG;
		}
		else if (source?.trim() && validateExpressionURL) {
			const response = await fetch(
				createResourceURL(validateExpressionURL, {
					expression: source,
				}).href
			);

			const {valid}: {valid: boolean} = await response.json();

			if (!valid) {
				error = Liferay.Language.get('syntax-error');
			}
		}

		if (error) {
			setState((state) => ({
				...state,
				error,
			}));
		}
		else {
			onSave?.(source);
			closeModal();
		}
	};

	return (
		<ClayModal
			center
			className="lfr-objects__expression-builder-modal"
			observer={observer}
			size="lg"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{header ?? Liferay.Language.get('expression-builder')}
			</ClayModal.Header>

			<ClayModal.Body>
				<CodeEditor
					error={error}
					onChange={() => {}}
					placeholder={
						placeholder ??
						`<#-- ${Liferay.Language.get(
							'create-the-condition-of-the-action-using-the-expression-builder'
						)} -->`
					}
					ref={editorRef}
					sidebarElements={eventSidebarElements || sidebarElements}
					value={source}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={handleSave}>
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

type Callback = (source?: string) => void;

interface IModalProps {
	sidebarElements: SidebarCategory[];
}
interface IProps extends React.InputHTMLAttributes<HTMLInputElement> {
	buttonDisabled?: boolean;
	component?: 'input' | 'textarea' | React.ForwardRefExoticComponent<any>;
	disabled?: boolean;
	error?: string;
	feedbackMessage?: string;
	hideFeedback?: boolean;
	id?: string;
	label?: string;
	name?: string;
	onOpenModal: () => void;
	required?: boolean;
	type?: 'number' | 'text';
	value?: string | number | string[];
}
