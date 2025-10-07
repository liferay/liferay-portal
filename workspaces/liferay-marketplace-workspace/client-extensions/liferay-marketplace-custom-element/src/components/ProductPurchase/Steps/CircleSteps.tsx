/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';

import './StepWizard.scss';

type Step = {
	active: boolean;
	index?: boolean;
	key: string;
	subTitle?: string;
	title: string;
};

type CircleStepsProps = {
	className?: string;
	steps: Step[];
};

const CircleSteps: React.FC<CircleStepsProps> = ({className, steps}) => {
	const activeStepIndex = steps.findIndex(({active}) => active);

	const stepIcon = (step: any, index: number) => {
		if (step.active) {
			return 'radio-button';
		}

		if (index < activeStepIndex) {
			return 'check';
		}

		return 'simple-circle';
	};

	return (
		<div
			className={classNames(
				'mx-6 d-flex justify-content-around step-wizard',
				className
			)}
		>
			{steps.map((step, index) => (
				<div
					className={classNames('step', {
						done: index < activeStepIndex,
						selected: step.active,
					})}
					key={index}
				>
					<ClayIcon
						className={classNames('mr-2 step', {
							done: index < activeStepIndex,
							selected: step.active,
						})}
						symbol={stepIcon(step, index)}
					/>

					{step.title}
				</div>
			))}
		</div>
	);
};

export default CircleSteps;
