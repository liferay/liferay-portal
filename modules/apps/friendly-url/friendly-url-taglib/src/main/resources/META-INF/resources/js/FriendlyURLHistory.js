/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {useModal} from '@clayui/modal';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useEffect, useRef, useState} from 'react';

import FriendlyURLHistoryModal from './FriendlyURLHistoryModal';

export default function FriendlyURLHistory({
	disabled: initialDisabled = false,
	elementId,
	localizable = false,
	...restProps
}) {
	const [showModal, setShowModal] = useState(false);
	const [selectedLanguageId, setSelectedLanguageId] = useState();
	const [disabled, setDisabled] = useState(initialDisabled);

	const inputRef = useRef(document.getElementById(elementId));

	const handleOnClose = () => {
		setShowModal(false);
	};

	const {observer, onClose} = useModal({
		onClose: handleOnClose,
	});

	useEffect(() => {
		const input = inputRef.current;

		if (input) {
			const mutationObserver = new MutationObserver((mutations) => {
				mutations.forEach((mutation) => {
					if (
						mutation.type === 'attributes' &&
						mutation.attributeName === 'disabled'
					) {
						setDisabled(mutation.target.disabled);
					}
				});
			});

			mutationObserver.observe(input, {
				attributeFilter: ['disabled'],
				attributes: true,
			});

			return () => {
				mutationObserver.disconnect(input);
			};
		}
	}, []);

	return (
		<>
			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('history[record]')}
				borderless
				className={classNames('btn-url-history', {
					['btn-url-history-localizable']: localizable,
				})}
				disabled={disabled}
				displayType="secondary"
				onClick={() => {
					if (localizable) {
						setSelectedLanguageId(
							Liferay.component(elementId).getSelectedLanguageId()
						);
					}
					setShowModal(true);
				}}
				outline
				small
				symbol="time"
				title={Liferay.Language.get('history[record]')}
			/>
			{showModal && (
				<FriendlyURLHistoryModal
					{...restProps}
					elementId={elementId}
					initialLanguageId={selectedLanguageId}
					localizable={localizable}
					observer={observer}
					onModalClose={onClose}
				/>
			)}
		</>
	);
}

FriendlyURLHistory.propTypes = {
	disabled: PropTypes.bool,
	elementId: PropTypes.string.isRequired,
	localizable: PropTypes.bool,
};
