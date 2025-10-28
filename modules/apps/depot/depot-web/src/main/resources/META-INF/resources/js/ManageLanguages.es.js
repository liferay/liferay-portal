/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayLabel from '@clayui/label';
import ClayModal from '@clayui/modal';
import ClayTable from '@clayui/table';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

const noop = () => {};

const ManageLanguages = ({
	availableLocales,
	customDefaultLocaleId,
	customLocales,
	observer,
	onModalClose = noop,
	onModalDone = noop,
}) => {
	const [selectedLocales, setSelectedLocales] = useState(customLocales);

	const [selectedLocalesIds, setSelectedLocalesIds] = useState(
		selectedLocales.map(({localeId}) => localeId)
	);

	const onChangeLocale = (checked, displayName, selectedLocaleId) => {
		if (checked) {
			setSelectedLocales(
				selectedLocales.concat({
					displayName,
					localeId: selectedLocaleId,
				})
			);
		}
		else {
			setSelectedLocales(
				selectedLocales.filter(
					({localeId}) => localeId !== selectedLocaleId
				)
			);
		}
	};

	useEffect(() => {
		setSelectedLocalesIds(selectedLocales.map(({localeId}) => localeId));
	}, [selectedLocales]);

	const Language = ({displayName, isDefault, localeId}) => {
		const checked = selectedLocalesIds.indexOf(localeId) !== -1;

		return (
			<ClayTable.Row>
				<ClayTable.Cell>
					<ClayCheckbox
						aria-label={sub(
							Liferay.Language.get('select-x'),
							displayName
						)}
						checked={checked}
						disabled={isDefault}
						label={displayName}
						onChange={() => {
							onChangeLocale(!checked, displayName, localeId);
						}}
					>
						{isDefault && (
							<ClayLabel className="ml-3" displayType="info">
								{Liferay.Language.get('default')}
							</ClayLabel>
						)}
					</ClayCheckbox>
				</ClayTable.Cell>
			</ClayTable.Row>
		);
	};

	return (
		<ClayModal observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('language-selection')}
			</ClayModal.Header>

			<ClayModal.Body scrollable>
				<ClayTable borderless headVerticalAlignment="middle">
					<ClayTable.Body>
						{availableLocales.map((locale) => {
							return (
								<Language
									{...locale}
									isDefault={
										customDefaultLocaleId ===
										locale.localeId
									}
									key={locale.localeId}
								/>
							);
						})}
					</ClayTable.Body>
				</ClayTable>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={onModalClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={() => onModalDone(selectedLocales)}
						>
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

ManageLanguages.propTypes = {
	availableLocales: PropTypes.arrayOf(
		PropTypes.shape({
			displayName: PropTypes.string,
			localeId: PropTypes.string,
		})
	).isRequired,
	customDefaultLocaleId: PropTypes.string.isRequired,
	customLocales: PropTypes.arrayOf(
		PropTypes.shape({
			displayName: PropTypes.string,
			localeId: PropTypes.string,
		})
	).isRequired,
	observer: PropTypes.object.isRequired,
	onModalClose: PropTypes.func,
	onModalDone: PropTypes.func,
};

export default function (props) {
	return <ManageLanguages {...props} />;
}
