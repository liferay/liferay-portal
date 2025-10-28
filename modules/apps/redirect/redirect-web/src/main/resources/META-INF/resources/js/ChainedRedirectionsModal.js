/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayModal from '@clayui/modal';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

const noop = () => {};

const ChainedRedirectionsModal = ({
	callback = noop,
	observer,
	onModalClose = noop,
	redirectEntryChainCause,
	saveButtonLabel,
}) => {
	const [updateChainedRedirectEntries, setUpdateChainedRedirectEntries] =
		useState(true);

	const handleSubmit = (event) => {
		event.preventDefault();
		callback(updateChainedRedirectEntries);
	};

	return (
		<ClayModal
			className="portlet-redirect-modal"
			observer={observer}
			size="md"
			status="warning"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('redirect-chain')}
			</ClayModal.Header>

			<form onSubmit={handleSubmit}>
				<ClayModal.Body>
					<p className="portlet-redirect-modal-title">
						<strong>
							{redirectEntryChainCause === 'sourceURL'
								? Liferay.Language.get(
										'do-you-want-to-create-the-redirect-and-update-the-references'
									)
								: Liferay.Language.get(
										'do-you-want-to-create-this-redirect-and-update-it'
									)}
						</strong>
					</p>

					<div className="portlet-redirect-modal-description">
						<p>
							{redirectEntryChainCause === 'sourceURL'
								? Liferay.Language.get(
										'there-are-other-redirects-pointing-to-the-source-url-of-this-redirect'
									)
								: Liferay.Language.get(
										'this-redirect-points-to-the-source-url-of-another-redirect'
									)}
						</p>

						<p>
							{redirectEntryChainCause === 'sourceURL'
								? Liferay.Language.get(
										'update-the-references-of-the-other-redirects-to-avoid-this-chain'
									)
								: Liferay.Language.get(
										'update-this-redirect-to-avoid-this-chain'
									)}
						</p>
					</div>

					<fieldset className="fieldset">
						<ClayCheckbox
							checked={updateChainedRedirectEntries}
							label={
								redirectEntryChainCause === 'sourceURL'
									? Liferay.Language.get('update-references')
									: Liferay.Language.get('update-redirect')
							}
							onChange={() =>
								setUpdateChainedRedirectEntries((val) => !val)
							}
						/>
					</fieldset>
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

							<ClayButton displayType="primary" type="submit">
								{saveButtonLabel}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</form>
		</ClayModal>
	);
};

ChainedRedirectionsModal.propTypes = {
	callback: PropTypes.func,
	observer: PropTypes.object.isRequired,
	onModalClose: PropTypes.func,
	redirectEntryChainCause: PropTypes.string.isRequired,
	saveButtonLabel: PropTypes.string.isRequired,
};

export default ChainedRedirectionsModal;
