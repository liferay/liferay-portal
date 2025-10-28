/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useResource} from '@clayui/data-provider';
import {ClayInput} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {
	PagesVisitor,
	useConfig,
	useFormState,
} from 'data-engine-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useMemo, useRef, useState} from 'react';

import {Editor} from './editor/Editor.es';

function getTransformedPages(pages) {
	return pages.map(({title}, index) => ({
		label: `${index + 1} ${title || Liferay.Language.get('page-title')}`,
		name: index.toString(),
		value: index.toString(),
	}));
}

function getFields(pages) {
	const fields = [];
	const visitor = new PagesVisitor(pages);

	visitor.mapFields(
		(field, fieldIndex, columnIndex, rowIndex, pageIndex) => {
			if (field.type !== 'fieldset') {
				fields.push({
					...field,
					pageIndex,
					value: field.fieldName,
				});
			}
		},
		true,
		true
	);

	return fields;
}

const RuleEditorModalContent = ({onClose, onSaveRule, rule}) => {
	const {
		ruleSettings: {functionsMetadata, functionsURL},
	} = useConfig();
	const {pages} = useFormState();
	const [invalidRule, setInvalidRule] = useState(true);
	const [ruleName, setRuleName] = useState(
		rule?.name[themeDisplay.getDefaultLanguageId()] ?? ''
	);

	/**
	 * This reference is used for updating rule value without causing a re-render
	 */
	const ruleRef = useRef(rule);

	const {resource: rolesResource} = useResource({
		fetch,
		link: `${window.location.origin}/o/headless-admin-user/v1.0/roles?restrictFields=rolePermissions`,
	});

	const roles = useMemo(
		() =>
			rolesResource?.items.map(({id, name}) => ({
				id: `${id}`,
				label: name,
				name,
				value: name,
			})),
		[rolesResource]
	);

	const transformedPages = useMemo(() => getTransformedPages(pages), [pages]);
	const fields = useMemo(() => getFields(pages), [pages]);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{rule
					? Liferay.Language.get('edit-rule')
					: Liferay.Language.get('create-new-rule')}
			</ClayModal.Header>
			<ClayModal.Header withTitle={false}>
				<ClayInput.Group className="pl-4 pr-4">
					<ClayInput.GroupItem>
						<ClayInput
							aria-label={Liferay.Language.get('rule-title')}
							className="form-control-inline"
							onChange={({target: {value}}) => setRuleName(value)}
							placeholder={Liferay.Language.get('untitled-rule')}
							type="text"
							value={ruleName}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayModal.Header>
			<ClayModal.Body>
				<Editor
					allowActions={[
						'show',
						'enable',
						'require',
						'calculate',
						'jump-to-page',
					]}
					className="pl-4 pr-4"
					dataProvider={[]}
					fields={fields}
					functionsURL={functionsURL}
					onChange={(rule) => {
						ruleRef.current = rule;
					}}
					onValidator={(value) => setInvalidRule(!value)}
					operatorsByType={functionsMetadata}
					pages={transformedPages}
					roles={roles}
					rule={rule}
				/>
			</ClayModal.Body>
			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={invalidRule || !ruleName}
							onClick={() => {
								onSaveRule({
									...ruleRef.current,
									name: ruleName,
								});

								onClose();
							}}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
};

export default function RuleEditorModal({
	onClose: onCloseFn,
	onSaveRule,
	rule,
	showModal,
}) {
	const {observer, onClose} = useModal({
		onClose: onCloseFn,
	});

	if (!showModal) {
		return null;
	}

	return (
		<ClayModal
			className="data-layout-builder-editor-modal"
			observer={observer}
			size="full-screen"
		>
			<RuleEditorModalContent
				onClose={onClose}
				onSaveRule={onSaveRule}
				rule={rule}
			/>
		</ClayModal>
	);
}
