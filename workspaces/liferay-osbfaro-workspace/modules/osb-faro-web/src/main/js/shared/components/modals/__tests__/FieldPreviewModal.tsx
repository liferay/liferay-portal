import FieldPreviewModal from '../FieldPreviewModal';
import React from 'react';
import {render} from '@testing-library/react';
import {useModal} from '@clayui/modal';
import {waitForModal} from 'test/render-modal';

jest.unmock('react-dom');

const DefaultComponent = (
	props: Partial<React.ComponentProps<typeof FieldPreviewModal>> = {}
) => {
	const {observer} = useModal({defaultOpen: true});

	return (
		<FieldPreviewModal
			dataSourceFn={() => Promise.resolve()}
			observer={observer}
			sourceName="foo"
			{...props}
		/>
	);
};

describe('FieldPreviewModal', () => {
	it('should render', async () => {
		render(<DefaultComponent />);

		await waitForModal();

		expect(document.body).toMatchSnapshot();
	});

	it('should render with fieldname', async () => {
		const fieldName = 'bar';

		const {getByText} = render(<DefaultComponent fieldName={fieldName} />);

		await waitForModal();

		expect(getByText(fieldName)).toBeTruthy();
	});
});
