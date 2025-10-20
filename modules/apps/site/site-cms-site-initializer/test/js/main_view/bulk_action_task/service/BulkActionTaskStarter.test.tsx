/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ISearchAssetObjectEntry} from '../../../../../src/main/resources/META-INF/resources/js/common/types/AssetType';
import {
	IBulkActionTaskStarterDTO,
	IBulkActionTaskType,
} from '../../../../../src/main/resources/META-INF/resources/js/common/types/BulkActionTask';
import {
	displayCreateTaskErrorToast,
	displayCreateTaskSuccessToast,
} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/toastUtil';
import {BulkActionTaskStarter} from '../../../../../src/main/resources/META-INF/resources/js/main_view/bulk_actions_monitor/services/BulkActionTaskStarter';
import {
	composeCreateTaskDTO,
	composeCreateTaskURL,
	getTaskReportLink,
} from '../../../../../src/main/resources/META-INF/resources/js/main_view/bulk_actions_monitor/util';
import {getBulkActionTaskMessage} from '../../../../../src/main/resources/META-INF/resources/js/main_view/bulk_actions_monitor/util/notifications';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/utils/toastUtil',
	() => ({
		displayCreateTaskErrorToast: jest.fn(),
		displayCreateTaskSuccessToast: jest.fn(),
	})
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/bulk_actions_monitor/util',
	() => ({
		composeCreateTaskDTO: jest.fn(),
		composeCreateTaskURL: jest.fn(),
		getTaskReportLink: jest.fn(
			(classNameId, taskId) => `mock-report-link-${classNameId}-${taskId}`
		),
	})
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/bulk_actions_monitor/util/notifications',
	() => ({
		getBulkActionTaskMessage: jest.fn(),
	})
);

const MOCK_API_URL = '/api/test';
const MOCK_CLASS_NAME_ID = 12345;

const getStarterDTO = (
	overrides = {}
): IBulkActionTaskStarterDTO<keyof IBulkActionTaskType> & {
	apiURL: string;
	classNameId: number;
} => ({
	apiURL: MOCK_API_URL,
	classNameId: MOCK_CLASS_NAME_ID,
	keyValues: {foo: 'bar'},
	selectedData: {
		items: [{id: 1}, {id: 2}] as unknown as ISearchAssetObjectEntry[],
		selectAll: false,
	},
	type: 'DeleteBulkAction',
	...overrides,
});

describe('BulkActionTaskStarter', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	describe('constructor', () => {
		it('throws an error if apiURL is not provided', () => {
			expect(
				() => new BulkActionTaskStarter(getStarterDTO({apiURL: null}))
			).toThrow('Cannot POST bulk action task.');
		});

		it('initializes properties correctly', () => {
			const dto = getStarterDTO();
			(composeCreateTaskDTO as jest.Mock).mockReturnValue({
				mock: 'payload',
			});
			(composeCreateTaskURL as jest.Mock).mockReturnValue(
				'mock-post-url'
			);

			const starter = new BulkActionTaskStarter(dto);

			expect(starter.type).toBe(dto.type);
			expect(starter.payload).toEqual({mock: 'payload'});
			expect(starter.postURL).toBe('mock-post-url');
			expect(composeCreateTaskDTO).toHaveBeenCalledWith(
				dto.type,
				dto.keyValues,
				dto.selectedData
			);
			expect(composeCreateTaskURL).toHaveBeenCalledWith(
				dto.apiURL,
				dto.selectedData,
				false
			);
		});

		it('sets postURL correctly for DownloadBulkAction', () => {
			const dto = getStarterDTO({type: 'DownloadBulkAction'});

			new BulkActionTaskStarter(dto);

			expect(composeCreateTaskURL).toHaveBeenCalledWith(
				dto.apiURL,
				dto.selectedData,
				true
			);
		});
	});

	describe('onCreateSuccess', () => {
		const mockResponse = {data: {id: 987}};

		it('calls onCreateTaskSuccess and overrides default toast when specified', () => {
			const onCreateSuccess = jest.fn();
			const starter = new BulkActionTaskStarter(
				getStarterDTO({
					onCreateSuccess,
					overrideDefaultSuccessToast: true,
				})
			);

			starter.onCreateSuccess(mockResponse as any);

			expect(onCreateSuccess).toHaveBeenCalledWith(mockResponse);
			expect(displayCreateTaskSuccessToast).not.toHaveBeenCalled();
		});

		it('displays default success toast when override is false and there are no overrides', () => {
			const starter = new BulkActionTaskStarter(getStarterDTO());
			(getBulkActionTaskMessage as jest.Mock).mockReturnValue(
				'mock-message'
			);

			starter.onCreateSuccess(mockResponse as any);

			expect(displayCreateTaskSuccessToast).toHaveBeenCalled();
			expect(getTaskReportLink).toHaveBeenCalled();
		});

		it('displays default success toast when override is false', () => {
			const onCreateSuccess = jest.fn();
			const starter = new BulkActionTaskStarter(
				getStarterDTO({onCreateSuccess})
			);
			(getBulkActionTaskMessage as jest.Mock).mockReturnValue(
				'mock-message'
			);

			starter.onCreateSuccess(mockResponse as any);

			expect(displayCreateTaskSuccessToast).toHaveBeenCalled();
			expect(onCreateSuccess).toHaveBeenCalledWith(mockResponse);
		});

		it('displays a toast with the correct message when selectAll is false', () => {
			const starter = new BulkActionTaskStarter(getStarterDTO());
			(getBulkActionTaskMessage as jest.Mock).mockReturnValue(
				'mock-message'
			);

			starter.onCreateSuccess(mockResponse as any);

			expect(getBulkActionTaskMessage).toHaveBeenCalledWith(
				'DeleteBulkAction',
				'info',
				expect.any(Object)
			);
		});

		it('displays a toast with the correct message when selectAll is true', () => {
			const starter = new BulkActionTaskStarter(
				getStarterDTO({
					selectedData: {items: [], selectAll: true},
				})
			);
			(getBulkActionTaskMessage as jest.Mock).mockReturnValue(
				'mock-message'
			);

			starter.onCreateSuccess(mockResponse as any);

			expect(displayCreateTaskSuccessToast).toHaveBeenCalled();
			expect(getTaskReportLink).toHaveBeenCalled();
		});
	});

	describe('onCreateError', () => {
		const mockResponse = {error: new Error('Something went wrong')};

		it('calls onCreateTaskError and overrides default toast when specified', () => {
			const onCreateError = jest.fn();
			const starter = new BulkActionTaskStarter(
				getStarterDTO({
					onCreateError,
					overrideDefaultErrorToast: true,
				})
			);

			starter.onCreateError(mockResponse as any);

			expect(onCreateError).toHaveBeenCalledWith(mockResponse);
			expect(displayCreateTaskErrorToast).not.toHaveBeenCalled();
		});

		it('displays default error toast when override is false and there are no overrides', () => {
			const starter = new BulkActionTaskStarter(getStarterDTO());

			starter.onCreateError(mockResponse as any);

			expect(displayCreateTaskErrorToast).toHaveBeenCalledWith(
				mockResponse.error
			);
		});

		it('displays default error toast when override is false', () => {
			const onCreateError = jest.fn();
			const starter = new BulkActionTaskStarter(
				getStarterDTO({onCreateError})
			);

			starter.onCreateError(mockResponse as any);

			expect(displayCreateTaskErrorToast).toHaveBeenCalledWith(
				mockResponse.error
			);
			expect(onCreateError).toHaveBeenCalledWith(mockResponse);
		});
	});
});
