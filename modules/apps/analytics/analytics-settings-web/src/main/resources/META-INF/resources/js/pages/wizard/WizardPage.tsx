/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayMultiStepNav from '@clayui/multi-step-nav';
import React, {useState} from 'react';

import {EPageView, Events, useDispatch} from '../..';
import {IPages} from '../../utils/types';
import AttributesStep from './AttributesStep';
import ConnectStep from './ConnectStep';
import PeopleStep from './PeopleStep';
import PropertyStep from './PropertyStep';
import RecommendationsStep from './RecommendationsStep';

export interface IGenericStepProps {
	onCancel: () => void;
	onChangeStep: (step: ESteps) => void;
}

export enum ESteps {
	ConnectAC = 0,
	Property = 1,
	People = 2,
	Attributes = 3,
	Recommendations = 4,
}

interface IStepProps<T, K> extends IPages<T, K> {
	available: boolean;
}

const STEPS: IStepProps<IGenericStepProps, ESteps>[] = [
	{
		Component: ConnectStep,
		available: true,
		key: ESteps.ConnectAC,
		title: Liferay.Language.get('connect'),
	},
	{
		Component: PropertyStep,
		available: false,
		key: ESteps.Property,
		title: Liferay.Language.get('property'),
	},
	{
		Component: PeopleStep,
		available: false,
		key: ESteps.People,
		title: Liferay.Language.get('people'),
	},
	{
		Component: AttributesStep,
		available: false,
		key: ESteps.Attributes,
		title: Liferay.Language.get('attributes'),
	},
];

if (Liferay.FeatureFlags['LPD-20640']) {
	STEPS.push({
		Component: RecommendationsStep,
		available: false,
		key: ESteps.Recommendations,
		title: Liferay.Language.get('recommendations'),
	});
}

const WizardPage: React.FC<React.HTMLAttributes<HTMLElement>> = () => {
	const [step, setStep] = useState<ESteps>(ESteps.ConnectAC);
	const [steps, setSteps] = useState(STEPS);

	const dispatch = useDispatch();

	return (
		<div className="sheet-lg">
			<ClayMultiStepNav indicatorLabel="top">
				{steps.map(({available, key: nextStep, title}, index) => {
					const completed = step > nextStep && nextStep !== step;

					return (
						<ClayMultiStepNav.Item
							active={nextStep === step}
							complete={step > nextStep}
							data-testid={
								nextStep === step && 'multi-step-item-active'
							}
							expand={index + 1 !== STEPS.length}
							key={nextStep}
						>
							{index < STEPS.length - 1 && (
								<ClayMultiStepNav.Divider />
							)}

							<ClayMultiStepNav.Indicator
								complete={completed}
								label={1 + index}
								onClick={() => available && setStep(nextStep)}
								subTitle={title}
							/>
						</ClayMultiStepNav.Item>
					);
				})}
			</ClayMultiStepNav>

			{steps.map(({Component, key: currentStep}) => (
				<div key={currentStep}>
					{currentStep === step && (
						<Component
							onCancel={() => {
								dispatch({
									payload: EPageView.Default,
									type: Events.ChangePageView,
								});

								Liferay.Util.openToast({
									message: Liferay.Language.get(
										'dxp-has-successfully-connected-to-analytics-cloud,-complete-your-setup-in-the-instance-scope-menu'
									),
									type: 'info',
								});
							}}
							onChangeStep={(nextStep) => {
								const updatedSteps = steps.map((step) => {
									if (nextStep === step.key) {
										return {
											...step,
											available: true,
										};
									}

									return step;
								});

								setSteps(updatedSteps);
								setStep(nextStep);
							}}
						/>
					)}
				</div>
			))}
		</div>
	);
};

export default WizardPage;
