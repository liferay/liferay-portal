/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayMultiStepNav from '@clayui/multi-step-nav';
import {Form, Formik, FormikConfig, FormikHelpers, FormikValues} from 'formik';
import React, {ReactElement, useState} from 'react';

import Footer from './Footer';

interface WizardStepProps {
	actionButtonLabel?: string;
	actionButtonSymbol?: string;
	children: React.ReactNode;
	initialValues?: FormikValues;
	isStepValid?: (values: FormikValues) => boolean;
	onSubmit?: FormikConfig<FormikValues>['onSubmit'];
	title: string;
	validate?: FormikConfig<FormikValues>['validate'];
}

export function WizardStep({children}: WizardStepProps) {
	return <>{children}</>;
}

export function Wizard({
	backURL,
	children,
}: {
	backURL: string;
	children: React.ReactElement<WizardStepProps>[];
}) {
	const [stepNumber, setStepNumber] = useState(0);
	const [formState, setFormState] = useState({});

	const steps = React.Children.toArray(
		children
	) as ReactElement<WizardStepProps>[];

	const totalSteps = steps.length;

	const step = steps[stepNumber] as React.ReactElement<WizardStepProps>;

	const {
		actionButtonLabel,
		actionButtonSymbol,
		isStepValid,
		onSubmit,
		validate,
	} = step.props;

	const initialValues = steps.reduce(
		(accumulator, {props}) => ({
			...accumulator,
			...props.initialValues,
		}),
		{} as FormikValues
	);

	const next = () => {
		setStepNumber((stepNumber) => Math.min(stepNumber + 1, totalSteps - 1));
	};

	const previous = () => {
		setStepNumber((stepNumber) => Math.max(stepNumber - 1, 0));
	};

	const handleSubmit = async (
		values: FormikValues,
		formikHelpers: FormikHelpers<FormikValues>
	) => {
		await onSubmit?.(values, formikHelpers);

		setFormState((prevState) => ({
			...prevState,
			...values,
		}));

		formikHelpers.setTouched({});

		next();
	};

	return (
		<Formik
			initialValues={{...initialValues, ...formState}}
			onSubmit={handleSubmit}
			validate={validate}
			validateOnMount
		>
			{(formik) => (
				<Form noValidate>
					<ClayMultiStepNav
						center
						className="c-mx-lg-9"
						indicatorLabel="top"
					>
						{steps.map((step, index) => {
							const {title: multiStepTitle} = step.props;

							return (
								<ClayMultiStepNav.Item
									active={index === stepNumber}
									key={index}
									state={
										stepNumber > index
											? 'complete'
											: undefined
									}
								>
									{index < totalSteps - 1 && (
										<ClayMultiStepNav.Divider />
									)}

									<ClayMultiStepNav.Indicator
										label={1 + index}
										subTitle={multiStepTitle}
									/>
								</ClayMultiStepNav.Item>
							);
						})}
					</ClayMultiStepNav>

					{step}

					<Footer
						actionButtonLabel={actionButtonLabel}
						actionButtonSymbol={actionButtonSymbol}
						backURL={backURL}
						continueDisabled={
							formik.isSubmitting ||
							formik.status?.submitted ||
							(isStepValid
								? !isStepValid(formik.values)
								: !formik.isValid)
						}
						onPrevious={stepNumber > 0 ? previous : undefined}
					/>
				</Form>
			)}
		</Formik>
	);
}
