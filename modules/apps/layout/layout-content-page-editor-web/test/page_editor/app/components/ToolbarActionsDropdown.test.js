/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openConfirmModal} from 'frontend-js-components-web';
import React from 'react';

import ToolbarActionsDropdown from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/ToolbarActionsDropdown';
import {
	useDisabledRedo,
	useDisabledUndo,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/Undo';
import {
	useHistoryItems,
	useOnHistoryItemClick,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/UndoHistory';
import useUndoRedoActions from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/useUndoRedoActions';
import useDisabledDiscardDraft from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/useDisabledDiscardDraft';
import useOnToggleSidebars from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/useOnToggleSidebars';
import StoreMother from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

const INITIAL_STATE = {
	network: {status: 0},
	sidebar: {hidden: true},
	undoHistory: [{}],
};

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openConfirmModal: jest.fn(({onConfirm}) => onConfirm(false)),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/components/useDisabledDiscardDraft',
	() => jest.fn(() => false)
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/components/useOnToggleSidebars',
	() => {
		const onToggleSidebars = jest.fn();

		return () => onToggleSidebars;
	}
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/Undo',
	() => {
		return {
			useDisabledRedo: jest.fn(() => false),
			useDisabledUndo: jest.fn(() => false),
		};
	}
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/useUndoRedoActions',
	() => {
		const undoRedo = {onRedo: jest.fn(), onUndo: jest.fn()};

		return () => undoRedo;
	}
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/components/undo/UndoHistory',
	() => {
		const onHistoryItemClick = {
			loadingHistory: false,
			onHistoryItemClick: jest.fn(),
		};

		return {
			useHistoryItems: jest.fn(() => [{label: 'update-editable-values'}]),
			useOnHistoryItemClick: () => onHistoryItemClick,
		};
	}
);

const renderComponent = ({
	state = INITIAL_STATE,
	discardDraftFormRef = React.createRef(),
} = {}) => {
	return render(
		<StoreMother.Component getState={() => state}>
			<form ref={discardDraftFormRef} />

			<ToolbarActionsDropdown discardDraftFormRef={discardDraftFormRef} />
		</StoreMother.Component>
	);
};

describe('ToolbarActionsDropdown', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	beforeEach(() => {
		window.submitForm = jest.fn();
	});

	it('renders ToolbarActionsDropdown component', () => {
		renderComponent();

		['undo', 'redo', 'history', 'show-sidebars', 'discard-draft'].forEach(
			(dropdownItem) =>
				expect(screen.getByText(dropdownItem)).toBeInTheDocument()
		);
	});

	it('renders ToolbarActionsDropdown button and makes sure it has tooltip and aria-label', () => {
		renderComponent();

		expect(screen.getByTitle('actions')).toBeInTheDocument();
		expect(screen.getByLabelText('actions')).toBeInTheDocument();
	});

	describe('Undo option', () => {
		it('calls onUndo when Undo option is selected', async () => {
			renderComponent();

			const {onUndo} = useUndoRedoActions();

			await userEvent.click(screen.getByText('undo'));

			expect(onUndo).toBeCalled();
		});

		it('disables Undo option when useDisabledUndo returns true', () => {
			useDisabledUndo.mockImplementation(jest.fn(() => true));

			renderComponent();

			expect(screen.getByText('undo')).toBeDisabled();
		});
	});

	describe('Redo option', () => {
		it('calls onRedo when Redo option is selected', async () => {
			renderComponent();

			const {onRedo} = useUndoRedoActions();

			await userEvent.click(screen.getByText('redo'));

			expect(onRedo).toBeCalled();
		});

		it('disables Redo option when useDisabledRedo returns true', () => {
			useDisabledRedo.mockImplementation(jest.fn(() => true));

			renderComponent();

			expect(screen.getByText('redo')).toBeDisabled();
		});
	});

	describe('History option', () => {
		it('renders history items and Undo All when there are history items', () => {
			renderComponent();

			expect(screen.getByText('undo-all')).toBeInTheDocument();
			expect(
				screen.getByText('update-editable-values')
			).toBeInTheDocument();
		});

		it('calls onHistoryItemClick when Undo All is selected', async () => {
			renderComponent();

			await userEvent.click(screen.getByText('undo-all'));

			const {onHistoryItemClick} = useOnHistoryItemClick();

			expect(onHistoryItemClick).toBeCalled();
		});

		it('calls onHistoryItemClick when a history item is selected', async () => {
			const {onHistoryItemClick} = useOnHistoryItemClick();

			useHistoryItems.mockImplementation(
				jest.fn(() => [
					{
						label: 'update-editable-values',
						onClick: () => {
							onHistoryItemClick();
						},
					},
				])
			);

			renderComponent();

			await userEvent.click(screen.getByText('update-editable-values'));

			expect(onHistoryItemClick).toBeCalled();
		});

		it('disables History option when there are no history items', () => {
			useHistoryItems.mockImplementation(jest.fn(() => []));

			renderComponent();

			expect(screen.getByText('history')).toBeDisabled();
		});
	});

	describe('Show Sidebars option', () => {
		it('calls useOnToggleSidebars when Show Sidebars option is selected', async () => {
			renderComponent();

			const onToggleSidebars = useOnToggleSidebars();

			await userEvent.click(screen.getByText('show-sidebars'));

			expect(onToggleSidebars).toBeCalled();
		});

		it('changes the label to Hide Sidebars when the sidebars are opened', () => {
			renderComponent({
				state: {...INITIAL_STATE, sidebar: {hidden: false}},
			});

			expect(screen.getByText('hide-sidebars')).toBeInTheDocument();
		});
	});

	describe('Discard Draft option', () => {
		it('calls onDiscardDraft when Discard Draft option is selected', async () => {
			const ref = React.createRef();

			renderComponent({discardDraftFormRef: ref});

			await userEvent.click(screen.getByText('discard-draft'));

			expect(openConfirmModal).toHaveBeenCalledWith({
				message:
					'are-you-sure-you-want-to-discard-current-draft-and-apply-latest-published-changes',
				onConfirm: expect.any(Function),
			});
		});

		it('disables the Discard Draft Button when useDisabledDiscardDraft returns true', () => {
			useDisabledDiscardDraft.mockImplementation(jest.fn(() => true));

			renderComponent();

			expect(screen.getByText('discard-draft')).toBeDisabled();
		});
	});
});
