/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import {ClassicEditor} from 'frontend-editor-ckeditor-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {FETCH_STATUS} from '../constants';

const noop = () => {};

const TranslateAutoTranslateRow = ({
	autoTranslateEnabled,
	children,
	handleAutoTranslateClick = noop,
	label,
	fieldStatus,
	sourceContent,
}) => {
	if (!autoTranslateEnabled) {
		return children;
	}

	const isLoading = fieldStatus.status === FETCH_STATUS.LOADING;
	const text = sub(Liferay.Language.get('auto-translate-x-field'), label);

	return (
		<ClayLayout.Row>
			<ClayLayout.ContentCol className="col-autotranslate-content" expand>
				{children}
			</ClayLayout.ContentCol>

			<ClayLayout.ContentCol className="align-self-top col-autotranslate-button">
				<ClayButton
					disabled={isLoading || !sourceContent}
					displayType="secondary"
					monospaced
					onClick={handleAutoTranslateClick}
					title={text}
				>
					{isLoading ? (
						<ClayLoadingIndicator className="my-0" small />
					) : (
						<ClayIcon symbol="automatic-translate" />
					)}

					<span className="sr-only">{text}</span>
				</ClayButton>
			</ClayLayout.ContentCol>
		</ClayLayout.Row>
	);
};

const TranslateFieldFeedback = ({message = '', status = ''}) =>
	(status === FETCH_STATUS.ERROR || status === FETCH_STATUS.SUCCESS) && (
		<div
			className={classNames({
				'has-error': status === FETCH_STATUS.ERROR,
				'has-success': status === FETCH_STATUS.SUCCESS,
			})}
		>
			<div className="form-feedback-item">
				<span className="form-feedback-indicator mr-1">
					<ClayIcon
						symbol={
							status === FETCH_STATUS.SUCCESS
								? 'check-circle-full'
								: 'exclamation-full'
						}
					/>
				</span>

				{message}
			</div>
		</div>
	);

const TranslateFieldEditor = ({
	editorConfiguration,
	fieldStatus,
	id,
	label,
	name,
	sourceContent,
	sourceContentDir,
	targetContent,
	targetContentDir,
	onChange = noop,
}) => {
	const [content, setContent] = useState(targetContent);

	const editorRef = useRef();
	const internalUpdateRef = useRef(true);

	useEffect(() => {
		if (editorRef.current.editor && !internalUpdateRef.current) {
			editorRef.current.editor.setData(targetContent);
			setContent(targetContent);
		}
		else {
			internalUpdateRef.current = false;
		}
	}, [targetContent]);

	return (
		<ClayLayout.Row>
			<ClayLayout.Col md={6}>
				<ClayForm.Group>
					<label className="control-label">{label}</label>

					<div
						className="translation-editor-preview"
						dangerouslySetInnerHTML={{__html: sourceContent}}
						dir={sourceContentDir}
					/>
				</ClayForm.Group>
			</ClayLayout.Col>

			<ClayLayout.Col md={6}>
				<ClayForm.Group>
					<label className="control-label">{label}</label>

					<ClassicEditor
						editorConfig={{
							...editorConfiguration.editorConfig,
							contentsLangDirection: targetContentDir,
						}}
						name={id}
						onChange={(data) => {
							setContent(data);
							onChange(data);
							internalUpdateRef.current = true;
						}}
						onInstanceReady={({editor}) => {

							// LPS-139363

							editor?.setData?.(content, {
								interal: true,
								noSnapshot: true,
							});
						}}
						ref={editorRef}
					/>

					<input defaultValue={content} name={name} type="hidden" />

					<TranslateFieldFeedback
						message={fieldStatus.message}
						status={fieldStatus.status}
					/>
				</ClayForm.Group>
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
};

const TranslateFieldInput = ({
	fieldStatus,
	id,
	label,
	multiline,
	name,
	onChange = noop,
	sourceContent,
	sourceContentDir,
	targetContent,
	targetContentDir,
}) => (
	<ClayLayout.Row>
		<ClayLayout.Col md={6}>
			<ClayForm.Group>
				<label className="control-label">{label}</label>

				<ClayInput
					component={multiline ? 'textarea' : undefined}
					defaultValue={sourceContent}
					dir={sourceContentDir}
					readOnly
					type="text"
				/>
			</ClayForm.Group>
		</ClayLayout.Col>

		<ClayLayout.Col md={6}>
			<ClayForm.Group>
				<ClayLayout.Row>
					<ClayLayout.ContentCol expand>
						<label className="control-label" htmlFor={id}>
							{label}
						</label>
					</ClayLayout.ContentCol>
				</ClayLayout.Row>

				<ClayInput
					component={multiline ? 'textarea' : undefined}
					dir={targetContentDir}
					id={id}
					name={name}
					onChange={(event) => {
						const data = event.target.value;
						onChange(data);
					}}
					type="text"
					value={targetContent}
				/>

				<TranslateFieldFeedback
					message={fieldStatus.message}
					status={fieldStatus.status}
				/>
			</ClayForm.Group>
		</ClayLayout.Col>
	</ClayLayout.Row>
);

const TranslateFieldSetEntries = ({
	autoTranslateEnabled,
	fetchAutoTranslateField,
	infoFieldSetEntries,
	onChange,
	portletNamespace,
	targetFieldsContent,
}) =>
	infoFieldSetEntries.map(({fields: fieldsSets, legend}) => (
		<React.Fragment key={legend}>
			<ClayLayout.Row
				className={classNames({
					'row-autotranslate-title': autoTranslateEnabled,
				})}
			>
				<ClayLayout.Col md={6}>
					<div className="fieldset-title">{legend}</div>
				</ClayLayout.Col>

				<ClayLayout.Col md={6}>
					<div className="fieldset-title">{legend}</div>
				</ClayLayout.Col>
			</ClayLayout.Row>

			{fieldsSets.map((fieldSet) =>
				fieldSet.sourceContent.map((sourceContent, index) => {
					const id = `${fieldSet.id}${index}`;
					const fieldProps = {
						...fieldSet,
						fieldStatus: {
							message: targetFieldsContent[id].message,
							status: targetFieldsContent[id].status,
						},
						id: `${portletNamespace}${id}`,
						name: `${portletNamespace}${fieldSet.id}`,
						onChange: (content) => {
							onChange({content, id});
						},
						sourceContent,
						targetContent: targetFieldsContent[id].content,
					};

					return (
						<TranslateAutoTranslateRow
							autoTranslateEnabled={autoTranslateEnabled}
							fieldStatus={fieldProps.fieldStatus}
							handleAutoTranslateClick={() =>
								fetchAutoTranslateField(id)
							}
							key={id}
							label={fieldProps.label}
							sourceContent={fieldProps.sourceContent}
						>
							{fieldSet.html ? (
								<TranslateFieldEditor {...fieldProps} />
							) : (
								<TranslateFieldInput {...fieldProps} />
							)}
						</TranslateAutoTranslateRow>
					);
				})
			)}
		</React.Fragment>
	));

export default TranslateFieldSetEntries;
