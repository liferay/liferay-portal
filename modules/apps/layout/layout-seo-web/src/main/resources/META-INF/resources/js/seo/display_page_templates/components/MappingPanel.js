/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import classNames from 'classnames';
import {PropTypes} from 'prop-types';
import React, {useRef, useState} from 'react';

import {SUPPORTED_FIELD_TYPES} from '../constants';
import useOnClickOutside from '../hooks/useOnClickOutside';

const noop = () => {};

const normalizeField = ({key, label}) => ({
	label,
	value: key,
});

function MappingPanel({
	isActive = false,
	name,
	fields,
	field: initialField,
	fieldTypes = [],
	source,
	onSelect = noop,
	clearSelectionOnClose = false,
}) {
	const [isPanelOpen, setIsPanelOpen] = useState(false);
	const [fieldValue, setFieldValue] = useState(
		initialField?.key || fields[0].key
	);
	const wrapperRef = useRef(null);

	const handleOnClose = () => {
		if (isPanelOpen) {
			setIsPanelOpen(false);

			if (clearSelectionOnClose) {
				setFieldValue(fields[0].key);
			}
		}
	};

	useOnClickOutside([wrapperRef.current], handleOnClose);

	const handleChangeField = (event) => {
		const {value} = event.target;
		const field = fields.find(({key}) => key === value);

		setFieldValue(field.key);
	};

	const handleOnSelect = () => {
		const field = fields.find(({key}) => key === fieldValue);

		onSelect({
			field,
			source,
		});
	};

	return (
		<div className="dpt-mapping-panel-wrapper" ref={wrapperRef}>
			<ClayButtonWithIcon
				className={classNames('dpt-mapping-btn', {
					active: isActive,
				})}
				displayType="secondary"
				monospaced
				onClick={() => {
					setIsPanelOpen((state) => !state);
				}}
				symbol="bolt"
				title={Liferay.Language.get('map')}
			/>

			{isPanelOpen && (
				<div
					className="dpt-mapping-panel popover popover-scrollable"
					onClick={(event) => event.stopPropagation()}
				>
					<div className="popover-body">
						<ClayForm.Group small>
							<label htmlFor={`${name}_mappingSelectorSource`}>
								{Liferay.Language.get('source')}
							</label>

							<ClayInput
								id={`${name}_mappingSelectorSource`}
								readOnly
								value={source.initialValue}
							/>
						</ClayForm.Group>

						<ClayForm.Group small>
							<label
								htmlFor={`${name}_mappingSelectorFieldSelect`}
							>
								{Liferay.Language.get('field')}
							</label>

							<ClaySelectWithOption
								id={`${name}_mappingSelectorFieldSelect`}
								onChange={handleChangeField}
								options={fields.map(normalizeField)}
								value={fieldValue}
							/>
						</ClayForm.Group>

						<ClayButton
							block
							disabled={initialField?.key === fieldValue}
							displayType="primary"
							onClick={handleOnSelect}
						>
							{fieldTypes.includes(SUPPORTED_FIELD_TYPES.TEXT)
								? Liferay.Language.get('add-field')
								: Liferay.Language.get('map-content')}
						</ClayButton>
					</div>
				</div>
			)}
		</div>
	);
}

MappingPanel.propTypes = {
	clearSelectionOnClose: PropTypes.bool,
	field: PropTypes.shape({
		key: PropTypes.string,
		label: PropTypes.string,
	}),
	fieldTypes: PropTypes.array,
	fields: PropTypes.arrayOf(
		PropTypes.shape({
			key: PropTypes.string,
			label: PropTypes.string,
		})
	).isRequired,
	isActive: PropTypes.bool,
	name: PropTypes.string.isRequired,
	onSelect: PropTypes.func,
	source: PropTypes.shape({
		initialValue: PropTypes.string,
	}).isRequired,
};

export default MappingPanel;
