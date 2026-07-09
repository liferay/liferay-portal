/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classNames from 'classnames';
import React from 'react';

interface FeedbackActionsRowProps {
	className?: string;
	feedbackGiven?: boolean;
	onReport: () => void;
	onThumbsUp?: () => void;
}

function stopMouseDown(event: React.MouseEvent<HTMLButtonElement>) {
	event.stopPropagation();
}

const FeedbackActionsRow: React.FC<FeedbackActionsRowProps> = ({
	className,
	feedbackGiven = false,
	onReport,
	onThumbsUp,
}) => {
	return (
		<ClayTooltipProvider>
			<div
				className={classNames(
					'ai-feedback-actions-row align-items-center d-inline-flex',
					className
				)}
			>
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('give-positive-feedback')}
					borderless
					data-tooltip-align="top"
					disabled={feedbackGiven}
					displayType="secondary"
					onClick={onThumbsUp}
					onMouseDown={stopMouseDown}
					size="sm"
					spritemap={Liferay.Icons.spritemap}
					symbol="thumbs-up"
					title={Liferay.Language.get('give-positive-feedback')}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get(
						'send-negative-feedback-or-report-legal-concern'
					)}
					borderless
					data-tooltip-align="top"
					disabled={feedbackGiven}
					displayType="secondary"
					onClick={onReport}
					onMouseDown={stopMouseDown}
					size="sm"
					spritemap={Liferay.Icons.spritemap}
					symbol="thumbs-down"
					title={Liferay.Language.get(
						'send-negative-feedback-or-report-legal-concern'
					)}
				/>
			</div>
		</ClayTooltipProvider>
	);
};

export default FeedbackActionsRow;
