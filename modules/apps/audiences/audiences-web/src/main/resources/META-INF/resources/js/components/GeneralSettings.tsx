/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import classNames from 'classnames';
import React from 'react';

interface IProps {
	errorMessage?: string;
	expanded: boolean;
	externalReferenceCode: string;
	externalReferenceCodeInputRef: React.RefObject<HTMLInputElement>;
	namespace: string;
	onExpandedChange: (expanded: boolean) => void;
	onExternalReferenceCodeChange: (externalReferenceCode: string) => void;
}

export default function GeneralSettings({
	errorMessage,
	expanded,
	externalReferenceCode,
	externalReferenceCodeInputRef,
	namespace,
	onExpandedChange,
	onExternalReferenceCodeChange,
}: IProps) {
	return (
		<ClayPanel
			className="audience-builder-general-settings border mt-4 rounded"
			collapsable
			collapseHeaderClassNames="align-items-center d-flex justify-content-between px-4 py-3"
			displayTitle={
				<span className="font-weight-bold text-6">
					{Liferay.Language.get('general-settings')}
				</span>
			}
			expanded={expanded}
			onExpandedChange={onExpandedChange}
			showCollapseIcon
		>
			<ClayPanel.Body>
				<ClayForm.Group
					className={classNames('mb-0', {
						'has-error': !!errorMessage,
					})}
				>
					<label htmlFor={`${namespace}externalReferenceCodeInput`}>
						{Liferay.Language.get('erc')}

						<ClayIcon
							className="c-ml-1 reference-mark"
							focusable="false"
							role="presentation"
							symbol="asterisk"
						/>

						<ClayIcon
							className="lfr-portal-tooltip ml-1 text-secondary"
							data-title={Liferay.Language.get(
								'unique-key-for-referencing-the-audience-definition'
							)}
							focusable="false"
							role="dialog"
							symbol="question-circle"
							tabIndex={0}
						/>
					</label>

					<ClayInput
						aria-describedby={
							errorMessage &&
							`${namespace}externalReferenceCodeError`
						}
						aria-invalid={!!errorMessage}
						aria-required
						id={`${namespace}externalReferenceCodeInput`}
						onChange={(event) =>
							onExternalReferenceCodeChange(event.target.value)
						}
						ref={externalReferenceCodeInputRef}
						type="text"
						value={externalReferenceCode}
					/>

					{errorMessage && (
						<ClayForm.FeedbackGroup role="alert">
							<ClayForm.FeedbackItem
								id={`${namespace}externalReferenceCodeError`}
							>
								<ClayForm.FeedbackIndicator symbol="exclamation-full" />

								{errorMessage}
							</ClayForm.FeedbackItem>
						</ClayForm.FeedbackGroup>
					)}
				</ClayForm.Group>
			</ClayPanel.Body>
		</ClayPanel>
	);
}
