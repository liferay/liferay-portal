/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, render} from '@testing-library/react';
import React from 'react';
import TaskStatusManager from "../../../../src/main/resources/META-INF/resources/js/main_view/task_status/TaskStatusManager";
import {MOCKED_TASK} from "./mocks";

describe('TaskStatusManager', () => {
    const {Liferay: originalLiferay} = window;

    beforeEach(() => {
        window['Liferay'] = {
            ...originalLiferay,

            // @ts-ignore

            Language: {
                get: (languageKey) => languageKey,
            }
        };
    });

    afterEach(() => {
        cleanup();

        window.Liferay = originalLiferay;

        jest.resetAllMocks();
    });

    describe('with processing tasks', () => {
        it('renders correctly for a single task', () => {
            const {getByText, queryByRole} = render(<TaskStatusManager {...MOCKED_TASK} totalCount={1} />);

            expect(getByText('1')).toBeInTheDocument();
            expect(getByText('processing-task')).toBeInTheDocument();
            expect(queryByRole('button', {name: 'close'})).not.toBeInTheDocument();
        });

        it('renders correctly for multiple tasks', () => {
            const {getByText, queryByRole} = render(<TaskStatusManager {...MOCKED_TASK} />);

            expect(getByText('5')).toBeInTheDocument();
            expect(getByText('processing-tasks')).toBeInTheDocument();
            expect(queryByRole('button', {name: 'close'})).not.toBeInTheDocument();
        });
    });

    describe('with no processing tasks', () => {
        it ('renders nothing at start', () => {
            const {queryByRole} = render(<TaskStatusManager />);

            expect(queryByRole('button')).not.toBeInTheDocument();
        });
    });
});