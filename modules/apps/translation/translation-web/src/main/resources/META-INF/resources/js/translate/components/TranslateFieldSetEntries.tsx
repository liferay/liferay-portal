/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-0 6
 */

import {SourceEditing} from '@ckeditor/ckeditor5-source-editing';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import {
	CKEditor5ClassicEditor,
	ClassicEditor,
	TEditor,
} from 'frontend-editor-ckeditor-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

// @ts-ignore

import {FETCH_STATUS} from '../constants';

const noop = () => {};

enum ELangDir {
	LTR = 'ltr',
	RTL = 'rtl',
}

const getISO639LanguageCode = (localeId: string): string => {
	if (localeId?.match(/[a-z]{2}_[A-Z]{2}/)) {
		return localeId.split('_')[0];
	}

	return localeId;
};

const getSourceEditingArea = (editor: TEditor): HTMLElement | null =>
	editor.ui?.element?.querySelector<HTMLElement>('.ck-source-editing-area') ??
	null;

const TranslateAutoTranslateRow = ({
	autoTranslateEnabled,
	children,
	fieldStatus,
	handleAutoTranslateClick = noop,
	label,
	sourceContent,
}: {
	autoTranslateEnabled: boolean;
	children: any;
	fieldStatus: any;
	handleAutoTranslateClick: (
		event: React.MouseEvent<HTMLElement, MouseEvent>
	) => void;
	label: string;
	sourceContent: string;
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

const TranslateFieldFeedback = ({
	message = '',
	status = '',
}: {
	message: string;
	status: string;
}) =>
	status === FETCH_STATUS.ERROR || status === FETCH_STATUS.SUCCESS ? (
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
	) : (
		<></>
	);

const TranslateFieldEditor = ({
	editorConfiguration,
	fieldStatus,
	id,
	label,
	name,
	onChange = noop,
	sourceContent,
	sourceContentDir,
	targetContent,
	targetContentDir,
	targetLanguageId,
}: {
	editorConfiguration: any;
	fieldStatus: any;
	id: string;
	label: string;
	name: string;
	onChange: (data: string) => void;
	sourceContent: string;
	sourceContentDir: ELangDir;
	targetContent: string;
	targetContentDir: ELangDir;
	targetLanguageId: string;
}) => {
	const [content, setContent] = useState(targetContent);

	const editorRef = useRef<any>();
	const ckeditor5Ref = useRef<TEditor>();
	const internalUpdateRef = useRef(true);
	const lastSourceInputRef = useRef('');

	const handleOnChange = (data: string) => {
		setContent(data);

		onChange(data);
	};

	const handleOnReady = (editor: TEditor) => {
		ckeditor5Ref.current = editor;

		const sourceEditingPlugin: SourceEditing =
			editor.plugins.get('SourceEditing');

		if (!sourceEditingPlugin) {
			return;
		}

		sourceEditingPlugin.on('change:isSourceEditingMode', () => {
			if (!sourceEditingPlugin.isSourceEditingMode) {
				return;
			}

			const textarea =
				getSourceEditingArea(editor)?.querySelector('textarea');

			textarea?.addEventListener('input', () => {
				const data = editor.getData();

				lastSourceInputRef.current = data;

				handleOnChange(data);
			});
		});
	};

	useEffect(() => {
		if (editorRef.current?.editor && !internalUpdateRef.current) {
			editorRef.current.editor.setData(targetContent);
		}
		else {
			internalUpdateRef.current = false;
		}

		const editor = ckeditor5Ref.current;

		const sourceEditingPlugin: SourceEditing | undefined =
			editor?.plugins.get('SourceEditing');

		if (
			sourceEditingPlugin?.isSourceEditingMode &&
			targetContent !== lastSourceInputRef.current
		) {
			const sourceEditingArea = getSourceEditingArea(editor!);

			const textarea = sourceEditingArea?.querySelector('textarea');

			if (textarea && textarea.value !== targetContent) {
				textarea.value = targetContent;

				sourceEditingArea!.dataset.value = targetContent;

				lastSourceInputRef.current = targetContent;

				sourceEditingPlugin.updateEditorData();
			}
		}

		setContent(targetContent);
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

					{!Liferay.FeatureFlags['LPD-11235'] ? (
						<CKEditor5ClassicEditor
							config={{
								...editorConfiguration.editorConfig,
								language: {
									content:
										getISO639LanguageCode(targetLanguageId),
								},
							}}
							data={targetContent}
							onChange={(event, editor) => {
								handleOnChange(editor.getData());
							}}
							onReady={handleOnReady}
						/>
					) : (
						<ClassicEditor
							editorConfig={{
								...editorConfiguration.editorConfig,
								contentsLangDirection: targetContentDir,
							}}
							name={id}
							onChange={(data: string) => {
								handleOnChange(data);

								internalUpdateRef.current = true;
							}}
							onInstanceReady={({editor}: {editor: any}) => {

								// LPS-139363

								editor?.setData?.(content, {
									internal: true,
									noSnapshot: true,
								});
							}}
							ref={editorRef}
						/>
					)}

					<input
						name={name}
						onChange={() => {}}
						type="hidden"
						value={content}
					/>

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
}: {
	fieldStatus: any;
	id: string;
	label: string;
	multiline: boolean;
	name: string;
	onChange: (data: string) => void;
	sourceContent: string;
	sourceContentDir: string;
	targetContent: string;
	targetContentDir: string;
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
						onChange(event.target.value);
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
}: {
	autoTranslateEnabled: boolean;
	fetchAutoTranslateField: Function;
	infoFieldSetEntries: Array<any>;
	onChange: ({content, id}: {content: string; id: string}) => void;
	portletNamespace: string;
	targetFieldsContent: any;
}) => (
	<>
		{infoFieldSetEntries.map(({fields: fieldsSets, legend}) => (
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

				{fieldsSets.map((fieldSet: any) =>
					fieldSet.sourceContent.map(
						(sourceContent: string, index: number) => {
							const id: string = `${fieldSet.id}${index}`;

							const fieldProps = {
								...fieldSet,
								fieldStatus: {
									message: targetFieldsContent[id].message,
									status: targetFieldsContent[id].status,
								},
								id: `${portletNamespace}${id}`,
								name: `${portletNamespace}${fieldSet.id}`,
								onChange: (content: string) => {
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
						}
					)
				)}
			</React.Fragment>
		))}
	</>
);

export default TranslateFieldSetEntries;
