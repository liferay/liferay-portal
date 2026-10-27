import React from 'react';
import {screen} from '@testing-library/react';

/**
 * Replaces the searchable table modals with components that record the props
 * they receive, so a test can check what a screen asks the modal for without
 * rendering the modal itself. Import it before the component under test.
 */

let mockLastProps;

jest.mock('shared/components/modals/SearchableTableModal', () => ({
	__esModule: true,
	default: props => {
		mockLastProps = props;

		return <div data-testid='searchable-table-modal' />;
	}
}));

jest.mock('shared/components/modals/SearchableTableModalGraphql', () => ({
	__esModule: true,
	default: props => {
		mockLastProps = props;

		return <div data-testid='searchable-table-modal' />;
	}
}));

export const getModalProps = () => mockLastProps;

export const isModalOpen = () =>
	!!screen.queryByTestId('searchable-table-modal');

export const resetModalProps = () => {
	mockLastProps = undefined;
};
