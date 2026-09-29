/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	cleanup,
	render,
	renderHook,
	screen,
	waitFor,
} from '@testing-library/react';
import React, {useState} from 'react';
import ResizeObserver from 'resize-observer-polyfill';

import {
	IRoomDataContext,
	IRoomStepProps,
} from '../../../src/main/resources/META-INF/resources/js/common/utils/types';
import {RoomContext} from '../../../src/main/resources/META-INF/resources/js/components/RoomInitializer';
import RoomSelectTemplateStep from '../../../src/main/resources/META-INF/resources/js/components/RoomSelectTemplateStep';

global.ResizeObserver = ResizeObserver;

let {result: useStateHookResult} = renderHook(() =>
	useState<IRoomDataContext>({
		accountId: 0,
		errors: {},
		friendlyURL: '',
		roomName: '',
		templateKey: '',
	})
);

const component = ({
	loading = false,
	numberOfSteps = 1,
	setHandleStepSubmit,
	siteTemplates,
}: IRoomStepProps & {loading?: boolean}) => {
	return (
		<RoomContext.Provider
			value={{
				dataContext: useStateHookResult.current[0],
				loading,
				setDataContext: useStateHookResult.current[1],
			}}
		>
			<RoomSelectTemplateStep
				numberOfSteps={numberOfSteps}
				setHandleStepSubmit={setHandleStepSubmit}
				siteTemplates={siteTemplates}
			/>
		</RoomContext.Provider>
	);
};

const renderComponent = ({
	numberOfSteps = 1,
	setHandleStepSubmit,
	siteTemplates,
}: IRoomStepProps) => {
	return render(
		component({numberOfSteps, setHandleStepSubmit, siteTemplates})
	);
};

describe('RoomSelectTemplateStep', () => {
	beforeEach(() => {
		({result: useStateHookResult} = renderHook(() =>
			useState<IRoomDataContext>({
				accountId: 0,
				errors: {},
				friendlyURL: '',
				roomName: '',
				templateKey: '',
			})
		));
	});

	afterEach(() => {
		jest.clearAllMocks();

		cleanup();
	});

	it('renders the correct UI elements', async () => {
		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
			siteTemplates: [
				{
					description: 'description1',
					friendlyURL: 'friendlyURL1',
					name: 'name1',
					uuid: 'uuid1',
				},
				{
					description: 'description2',
					friendlyURL: 'friendlyURL2',
					name: 'name2',
					uuid: 'uuid2',
				},
			],
		});

		expect(screen.getByTestId('templatePreview')).toBeInTheDocument();
		expect(
			screen.queryByTestId('templatePreviewFrame')
		).not.toBeInTheDocument();
		expect(screen.getByTestId('savedTemplates')).toBeInTheDocument();
		expect(screen.getByTestId('stepLocator')).toBeInTheDocument();
		expect(screen.getByTestId('stepTitle')).toBeInTheDocument();
	});

	it('loads templates', async () => {
		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
			siteTemplates: [
				{
					description: 'description1',
					friendlyURL: 'friendlyURL1',
					name: 'name1',
					uuid: 'uuid1',
				},
				{
					description: 'description2',
					friendlyURL: 'friendlyURL2',
					name: 'name2',
					uuid: 'uuid2',
				},
			],
		});

		await waitFor(() => {
			expect(screen.getByTestId(`template_uuid1`)).toBeInTheDocument();
			expect(
				screen.getByTestId(`templateName_uuid1`)
			).toBeInTheDocument();
			expect(screen.getByTestId(`templateName_uuid1`)).toHaveTextContent(
				'name1'
			);
			expect(
				screen.getByTestId(`templateDescription_uuid1`)
			).toBeInTheDocument();
			expect(
				screen.getByTestId(`templateDescription_uuid1`)
			).toHaveTextContent('description1');
			expect(screen.getByTestId(`template_uuid2`)).toBeInTheDocument();
			expect(
				screen.getByTestId(`templateName_uuid2`)
			).toBeInTheDocument();
			expect(screen.getByTestId(`templateName_uuid2`)).toHaveTextContent(
				'name2'
			);
			expect(
				screen.getByTestId(`templateDescription_uuid2`)
			).toBeInTheDocument();
			expect(
				screen.getByTestId(`templateDescription_uuid2`)
			).toHaveTextContent('description2');
		});
	});

	it('validate UI interaction', async () => {
		renderComponent({
			numberOfSteps: 1,
			setHandleStepSubmit: () => {},
			siteTemplates: [
				{
					description: 'description1',
					friendlyURL: 'friendlyURL1',
					name: 'name1',
					uuid: 'uuid1',
				},
				{
					description: 'description2',
					friendlyURL: 'friendlyURL2',
					name: 'name2',
					uuid: 'uuid2',
				},
			],
		});

		await waitFor(() => {
			expect(screen.getByTestId(`template_uuid1`)).toBeInTheDocument();
		});

		screen.getByTestId(`templateName_uuid1`).click();

		await waitFor(() => {
			const state = useStateHookResult.current[0];

			expect(state.templateKey).toBe('uuid1');
			expect(
				screen.getByTestId('templatePreviewFrame')
			).toBeInTheDocument();
		});

		screen.getByTestId(`templateName_uuid2`).click();

		await waitFor(() => {
			const state = useStateHookResult.current[0];

			expect(state.templateKey).toBe('uuid2');
			expect(
				screen.getByTestId('templatePreviewFrame')
			).toBeInTheDocument();
		});
	});
});
