/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClayLayout from '@clayui/layout';
import {
	ILearnResourceContext,
	LearnMessage,
	LearnResourcesContext,
} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {PropsWithChildren} from 'react';

export interface NewSpaceFormSectionProps {
	description: string;
	learnResourceKey: string;
	learnResources: ILearnResourceContext;
	onSubmit?: (event: React.FormEvent<HTMLFormElement>) => void;
	step: 1 | 2;
	title: string;
	withForm?: boolean;
}

export function NewSpaceFormSection({
	children,
	description,
	learnResourceKey,
	learnResources,
	onSubmit,
	step,
	title,
	withForm = true,
}: PropsWithChildren<NewSpaceFormSectionProps>) {
	const pageContent = (
		<>
			<ClayLayout.ContainerFluid className="mb-5 p-0">
				<p className="mb-2 mt-3 mt-lg-6 text-secondary">
					{sub(Liferay.Language.get('step-x-of-x'), [step, 2])}
				</p>

				<h1 className="font-semibold mb-4 text-7">{title}</h1>

				<p className="text-5 text-secondary">{description}</p>

				<LearnResourcesContext.Provider value={learnResources}>
					<LearnMessage
						className="font-weight-semi-bold text-decoration-underline"
						resource="site-cms-site-initializer"
						resourceKey={learnResourceKey}
					/>
				</LearnResourcesContext.Provider>
			</ClayLayout.ContainerFluid>

			{children}
		</>
	);

	return withForm ? (
		<ClayForm onSubmit={onSubmit}>{pageContent}</ClayForm>
	) : (
		<div>{pageContent}</div>
	);
}
