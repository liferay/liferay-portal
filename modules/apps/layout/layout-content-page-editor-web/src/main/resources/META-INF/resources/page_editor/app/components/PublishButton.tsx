/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React, {useState} from 'react';

import {config} from '../config/index';
import {FormValidationModal, useCheckFormsValidity} from '../js-index';

type Props = {
	ariaLabel: string;
	canPublish?: boolean;
	disabled?: boolean;
	formRef: React.RefObject<HTMLFormElement>;
	icon?: string | null;
	label: string;
	onPublish: () => void;
};

export default function PublishButton({
	ariaLabel,
	canPublish,
	disabled,
	formRef,
	icon,
	label,
	onPublish,
}: Props) {
	const checkFormsValidity = useCheckFormsValidity();

	const [openFormValidationModal, setOpenFormValidationModal] =
		useState(false);

	const submitURL =
		config.singleSegmentsExperienceMode &&
		config.saveVariantSegmentsExperienceURL
			? config.saveVariantSegmentsExperienceURL
			: config.publishURL;

	return (
		<>
			<form action={submitURL} method="POST" ref={formRef}>
				<input
					name={`${config.portletNamespace}redirect`}
					type="hidden"
					value={config.redirectURL}
				/>

				<ClayButton
					aria-label={ariaLabel}
					disabled={disabled || config.pending || !canPublish}
					displayType="primary"
					onClick={async () => {
						const valid = await checkFormsValidity();

						if (valid) {
							onPublish();
						}
						else {
							setOpenFormValidationModal(true);
						}
					}}
					size="sm"
				>
					{icon ? <ClayIcon className="mr-2" symbol={icon} /> : null}

					{label}
				</ClayButton>
			</form>

			{openFormValidationModal && (
				<FormValidationModal
					onCloseModal={() => setOpenFormValidationModal(false)}
					onPublish={onPublish}
				/>
			)}
		</>
	);
}
