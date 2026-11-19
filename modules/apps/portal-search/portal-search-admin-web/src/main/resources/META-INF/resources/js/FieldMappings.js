/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {useTimeout} from '@liferay/frontend-js-react-web';
import PropTypes from 'prop-types';
import React, {useRef, useState} from 'react';

class AceEditor extends React.Component {
	constructor(props) {
		super(props);

		this.container = React.createRef();
	}

	componentDidMount() {
		AUI().use('aui-ace-editor', (A) => {
			const editor = new A.AceEditor({
				boundingBox: this.container.current,
				highlightActiveLine: false,
				mode: 'json',
				readOnly: 'true',
				tabSize: 4,
				value: this.props.fieldMappings,
				width: '100%',
			}).render();

			editor.on('render', () => {
				this.props.onRender({
					editorElement: document.querySelector('.ace_editor'),
					editorTextInput: document.querySelector('.ace_text-input'),
				});
			});
		});
	}

	shouldComponentUpdate() {
		return false;
	}

	render() {
		return (
			<div className="lfr-source-editor-code" ref={this.container}></div>
		);
	}
}

AceEditor.propTypes = {
	fieldMappings: PropTypes.string,
	onRender: PropTypes.func,
};

const FieldMappings = ({
	fieldMappingIndexDisplayContexts,
	fieldMappings,
	selectedIndexName,
}) => {
	const [theme, setTheme] = useState('light');

	const editorElementRef = useRef();
	const editorTextInputRef = useRef();

	const delay = useTimeout();

	const handleCopyEditorText = (event) => {
		const editorTextInput = editorTextInputRef.current;

		editorTextInput.focus();
		editorTextInput.select();

		delay(() => {
			document.execCommand('copy');
		}, 0);

		event.currentTarget.dataset.title = Liferay.Language.get('copied');
	};

	const getEditorFontSize = () => {
		return parseInt(
			window
				.getComputedStyle(editorElementRef.current, null)
				.getPropertyValue('font-size'),
			10
		);
	};

	return (
		<ClayLayout.ContainerFluid
			className="portal-search-admin-field-mappings"
			formSize="lg"
		>
			<ClayLayout.Row>
				<ClayLayout.Col md="3">
					<nav className="menubar menubar-transparent menubar-vertical-expand-lg">
						<ul className="nav nav-nested">
							<li className="nav-header">
								{Liferay.Language.get('index')}
							</li>

							{fieldMappingIndexDisplayContexts.map((item) => {
								return (
									<li className="nav-item" key={item.name}>
										<a
											className={`${item.cssClass} nav-link`}
											href={`${item.url}`}
										>
											{item.name}
										</a>
									</li>
								);
							})}
						</ul>
					</nav>
				</ClayLayout.Col>

				<ClayLayout.Col md="9">
					<ClayLayout.Sheet>
						<ClayLayout.SheetHeader>
							<h2 className="sheet-title">{selectedIndexName}</h2>

							<div className="sheet-text">
								{Liferay.Language.get(
									'a-list-of-all-field-mappings-in-this-index'
								)}
							</div>
						</ClayLayout.SheetHeader>

						<ClayLayout.SheetSection>
							<ClayButton
								className="lfr-portal-tooltip"
								displayType="secondary"
								monospaced
								onClick={() => {
									setTheme(
										theme === 'light' ? 'dark' : 'light'
									);
								}}
								title={
									theme === 'light'
										? Liferay.Language.get('dark-theme')
										: Liferay.Language.get('light-theme')
								}
							>
								<ClayIcon symbol="moon" />
							</ClayButton>

							<ClayButton.Group className="btn-group-font-size">
								<ClayButton
									className="btn-plus lfr-portal-tooltip"
									displayType="secondary"
									monospaced
									onClick={() => {
										editorElementRef.current.style.fontSize =
											getEditorFontSize() + 2 + 'px';
									}}
									title={Liferay.Language.get('zoom-in')}
								>
									<ClayIcon symbol="plus" />
								</ClayButton>

								<ClayButton
									className="btn-hr lfr-portal-tooltip"
									displayType="secondary"
									monospaced
									onClick={() => {
										editorElementRef.current.style.fontSize =
											getEditorFontSize() - 2 + 'px';
									}}
									title={Liferay.Language.get('zoom-out')}
								>
									<ClayIcon symbol="hr" />
								</ClayButton>
							</ClayButton.Group>

							<ClayButton
								className="btn-copy lfr-portal-tooltip"
								displayType="secondary"
								monospaced
								onClick={handleCopyEditorText}
								title={Liferay.Language.get('copy')}
							>
								<ClayIcon symbol="copy" />
							</ClayButton>
						</ClayLayout.SheetSection>

						<ClayLayout.SheetSection
							className={`${
								theme === 'dark' && 'ace_dark'
							} lfr-source-editor lfr-source-editor-content`}
						>
							<AceEditor
								fieldMappings={fieldMappings}
								onRender={({
									editorElement,
									editorTextInput,
								}) => {
									editorElementRef.current = editorElement;
									editorTextInputRef.current =
										editorTextInput;
								}}
							/>
						</ClayLayout.SheetSection>
					</ClayLayout.Sheet>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</ClayLayout.ContainerFluid>
	);
};

FieldMappings.propTypes = {
	fieldMappingIndexDisplayContexts: PropTypes.array,
	fieldMappings: PropTypes.string,
	selectedIndexName: PropTypes.string,
};

export default FieldMappings;
