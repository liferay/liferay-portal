/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React from 'react';

interface IProps {
	backLabel: string;
	children?: React.ReactNode;
	continueDisabled?: boolean;
	continueLabel?: string;
	continueLoading?: boolean;
	onBack: () => void;
	onCancel: () => void;
	onContinue: () => void;
}

export default function StepActions({
	backLabel,
	children,
	continueDisabled,
	continueLabel = Liferay.Language.get('continue'),
	continueLoading,
	onBack,
	onCancel,
	onContinue,
}: IProps) {
	return (
		<div className="content-site-generator__step-actions">
			<ClayButton displayType="link" onClick={onBack}>
				<ClayIcon
					className="mr-1"
					spritemap={Liferay.Icons.spritemap}
					symbol="angle-left"
				/>

				{backLabel}
			</ClayButton>

			<div className="content-site-generator__step-actions-end">
				{children}

				<ClayButton displayType="secondary" onClick={onCancel}>
					{Liferay.Language.get('cancel')}
				</ClayButton>

				<ClayButton
					disabled={continueDisabled || continueLoading}
					displayType="primary"
					onClick={onContinue}
				>
					{continueLabel}

					{continueLoading && (
						<span
							aria-hidden="true"
							className="loading-animation loading-animation-sm ml-2"
						/>
					)}
				</ClayButton>
			</div>
		</div>
	);
}
