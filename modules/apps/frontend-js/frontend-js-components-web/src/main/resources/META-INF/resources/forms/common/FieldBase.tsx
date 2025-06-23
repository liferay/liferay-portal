/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayPopover from '@clayui/popover';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classNames from 'classnames';
import React, {useState} from 'react';

import FieldFeedback from './FieldFeedback';

import './FieldBase.scss';

interface FieldBaseProps {
	children: React.ReactNode;
	className?: string;
	disabled?: boolean;
	errorMessage?: string;
	helpMessage?: string;
	hideFeedback?: boolean;
	id?: string;
	label?: string;
	popover?: {
		alignPosition?: 'top' | 'bottom';
		content?: string;
		disableScroll?: boolean;
		header?: string;
	};
	required?: boolean;
	tooltip?: string;
	warningMessage?: string;
}

export function RequiredMask({disabled}: {disabled?: boolean}) {
	return (
		<>
			<span className="ml-1 reference-mark text-warning">
				<ClayIcon
					className={classNames({
						'field-base-disabled-icon': disabled,
					})}
					symbol="asterisk"
				/>
			</span>

			<span className="hide-accessible sr-only">
				{Liferay.Language.get('mandatory')}
			</span>
		</>
	);
}

export default function FieldBase({
	children,
	className,
	disabled,
	errorMessage,
	helpMessage,
	hideFeedback,
	id,
	label,
	popover,
	required,
	tooltip,
	warningMessage,
}: FieldBaseProps) {
	const [showPopover, setShowPopover] = useState(false);

	return (
		<ClayForm.Group
			className={classNames(className, {
				'has-error': errorMessage,
				'has-warning': warningMessage && !errorMessage,
			})}
		>
			{label && (
				<label className={classNames({disabled})} htmlFor={id}>
					{label}

					{required && <RequiredMask disabled={disabled} />}
				</label>
			)}

			{tooltip && (
				<>
					&nbsp;
					<ClayTooltipProvider>
						<span data-tooltip-align="top" title={tooltip}>
							<ClayIcon
								className="field-base-tooltip-icon"
								symbol="question-circle-full"
							/>
						</span>
					</ClayTooltipProvider>
				</>
			)}

			{popover && (
				<>
					&nbsp;
					<ClayPopover
						alignPosition={popover.alignPosition}
						disableScroll
						header={popover.header}
						show={showPopover}
						trigger={
							<ClayIcon
								className="field-base-tooltip-icon"
								onMouseOut={() => setShowPopover(false)}
								onMouseOver={() => setShowPopover(true)}
								symbol="question-circle-full"
							/>
						}
					>
						{popover.content}
					</ClayPopover>
				</>
			)}

			{children}

			{!hideFeedback && (
				<FieldFeedback
					errorMessage={errorMessage}
					helpMessage={helpMessage}
					id={`${id}fieldFeedback`}
					warningMessage={warningMessage}
				/>
			)}
		</ClayForm.Group>
	);
}
